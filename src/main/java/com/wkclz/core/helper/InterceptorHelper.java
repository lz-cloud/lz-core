package com.wkclz.core.helper;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Description: 权限
 * Created: wangkaicun @ 2017-10-18 下午11:41
 */
@Component
public class InterceptorHelper {

    @Autowired
    private AuthHelper authHelper;
    @Autowired
    private AccessHelper accessHelper;
    @Autowired
    private ApiDomainHelper apiDomainHelper;
    @Autowired
    private OrgDomainHelper orgDomainHelper;

    public boolean preHandle(HttpServletRequest req, HttpServletResponse rep) {

        // API 安全检测
        boolean apiDomainCheckResult = apiDomainHelper.checkApiDomains(req, rep);
        if (!apiDomainCheckResult) {
            return false;
        }

        // Org 安全检测
        boolean orgDomainCheckResult = orgDomainHelper.checkOrgDomains(req, rep);
        if (!orgDomainCheckResult) {
            return false;
        }


        /**
         * 用户登录检测
         */


        // uri 拦截检测【如果检查通过，无需再检查token】
        boolean checkAccessUriResult = accessHelper.checkAccessUri(req);
        if (checkAccessUriResult) {
            // 内存 session 检测【不再需要返回判断】【如果存在，不会重复设置】
            authHelper.checkUserSession(req);
            return true;
        }

        // token 检测
        boolean userTokenCheckResult = authHelper.checkUserToken(req, rep);
        if (!userTokenCheckResult) {
            return false;
        }

        // uri 权限检测 TODO
        /*
        boolean userUriAuthCheckResult = true;
        if (!userUriAuthCheckResult){
            return false;
        }
        */

        // 内存 session 检测【不再需要返回判断】【如果存在，不会重复设置】
        authHelper.checkUserSession(req);

        return true;
    }

    public void afterCompletion(HttpServletRequest req, HttpServletResponse rep) {
        String token = req.getParameter("token");
        if (StringUtils.isBlank(token)) {
            return;
        }
        if (!token.startsWith("temp_")) {
            return;
        }
        authHelper.invalidateSession(req, rep);
    }

}
