package com.wkclz.core.util;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.net.MalformedURLException;
import java.net.URL;

public class UrlUtil {

    private final static Logger logger = LoggerFactory.getLogger(UrlUtil.class);

    /**
     * 获取请求域名
     *
     * @param req
     * @return
     */
    public static String getDomain(HttpServletRequest req) {
        String domain = req.getHeader("Origin");
        if (StringUtils.isBlank(domain)) {
            domain = req.getHeader("Referer");
        }
        if (StringUtils.isBlank(domain)) {
            domain = req.getParameter("Origin");
        }
        if (StringUtils.isBlank(domain)) {
            domain = req.getParameter("Referer");
        }
        // 非前后分离的情况，为当前域名
        if (StringUtils.isBlank(domain)) {
            domain = req.getRequestURL().toString();
        }
        domain = getDomainFronUrl(domain);
        return domain;
    }


    public static String getDomainFronUrl(String url) {
        if (url == null || url.trim().length() == 0) {
            return url;
        }
        if (!url.startsWith("http")) {
            url = "http://" + url;
        }
        try {
            URL url1 = new URL(url);
            String host = url1.getHost();
            return host;
        } catch (MalformedURLException e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }


    public static String getFrontPortalAndDomain(HttpServletRequest req) {
        String domain = getDomain(req);
        String portalAndDomain = req.getProtocol() + "://" + domain;
        return portalAndDomain;
    }


}