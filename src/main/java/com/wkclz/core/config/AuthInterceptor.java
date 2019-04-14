package com.wkclz.core.config;

import com.wkclz.core.helper.AccessHelper;
import com.wkclz.core.helper.ApiDomainHelper;
import com.wkclz.core.helper.AuthHelper;
import com.wkclz.core.helper.OrgDomainHelper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Description:
 * Created: wangkaicun @ 2017-10-18 下午11:41
 */
@Component
public class AuthInterceptor extends HandlerInterceptorAdapter {

    @Autowired
    private AuthHelper authHelper;
    @Autowired
    private AccessHelper accessHelper;
    @Autowired
    private ApiDomainHelper apiDomainHelper;
    @Autowired
    private OrgDomainHelper orgDomainHelper;

    @Override
    public boolean preHandle(HttpServletRequest req, HttpServletResponse rep, Object handler) {

        // API 安全检测
        boolean apiDomainCheckResult = apiDomainHelper.checkApiDomains(req, rep);
        if (!apiDomainCheckResult){
            return false;
        }

        // Org 安全检测
        boolean orgDomainCheckResult = orgDomainHelper.checkOrgDomains(req, rep);
        if (!orgDomainCheckResult){
            return false;
        }

        // uri 拦截检测【如果检查通过，无需再检查token】
        boolean checkAccessUriResult = accessHelper.checkAccessUri(req);
        if (checkAccessUriResult){
            // 内存 session 检测【不再需要返回判断】【如果存在，不会重复设置】
            authHelper.checkUserSession(req);
            return true;
        }

        // token 检测
        boolean userTokenCheckResult = authHelper.checkUserToken(req, rep);
        if (!userTokenCheckResult){
            return false;
        }

        // uri 权限检测 TODO
        boolean userUriAuthCheckResult = true;
        if (!userUriAuthCheckResult){
            return false;
        }

        // 内存 session 检测【不再需要返回判断】【如果存在，不会重复设置】
        authHelper.checkUserSession(req);

        return true;
    }


    @Override
    public void afterCompletion(HttpServletRequest req, HttpServletResponse rep, Object handler, Exception ex) {
        String token = req.getParameter("token");
        if (token == null){
            return;
        }
        if (StringUtils.isBlank(token)){
            return;
        }
        if (!token.startsWith("temp_")){
            return;
        }
        authHelper.invalidateSession(token);
    }

}
