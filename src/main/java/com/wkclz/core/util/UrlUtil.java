package com.wkclz.core.util;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.net.MalformedURLException;
import java.net.URL;

public class UrlUtil {

    private final static Logger logger = LoggerFactory.getLogger(UrlUtil.class);

    private static String getFrontUrl(HttpServletRequest req) {
        String domain = req.getHeader("Origin");
        if (StringUtils.isBlank(domain) || "null".equals(domain)) {
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
        return domain;
    }

    /**
     * 获取请求域名
     *
     * @param req
     * @return
     */
    public static String getFrontDomain(HttpServletRequest req) {
        String frontUrl = getFrontUrl(req);
        String domain = getDomainFronUrl(frontUrl);
        return domain;
    }

    public static Integer getFrontPort(HttpServletRequest req) {
        String frontUrl = getFrontUrl(req);
        Integer port = getPortFronUrl(frontUrl);
        return port;
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

    public static Integer getPortFronUrl(String url) {
        if (StringUtils.isBlank(url)) {
            return null;
        }
        try {
            URL url1 = new URL(url);
            int port = url1.getPort();
            if (port < 1){
                return null;
            }
            return port;
        } catch (MalformedURLException e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }

    public static String getFrontPortalDomainPort(HttpServletRequest req) {
        String domain = getFrontDomain(req);
        Integer port = getFrontPort(req);
        String protocol = req.getProtocol();
        String portalDomainPort = protocol + "://" + domain;
        if (port != null && !("http".equalsIgnoreCase(protocol) && port == 80) && !("https".equalsIgnoreCase(protocol) && port == 443)){
            portalDomainPort += ":" + port;
        }
        return portalDomainPort;
    }

}