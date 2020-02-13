package com.wkclz.core.config.handler;

import com.wkclz.core.base.Result;
import com.wkclz.core.helper.AccessHelper;
import com.wkclz.core.helper.ApiDomainHelper;
import com.wkclz.core.helper.AuthHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Description: 此处拦截不再加入到 WebMvcConfigurer， 真正的拦截交给网关
 * Created: wangkaicun @ 2017-10-18 下午11:41
 */
@Component
public class AuthHandler {

    @Autowired
    private AuthHelper authHelper;
    @Autowired
    private AccessHelper accessHelper;
    @Autowired
    private ApiDomainHelper apiDomainHelper;

    public Result preHandle(HttpServletRequest req, HttpServletResponse rep) {

        // API 安全检测
        Result apiDomainCheckResult = apiDomainHelper.checkApiDomains(req, rep);
        if (apiDomainCheckResult != null) {
            return apiDomainCheckResult;
        }

        /**
         * 用户登录检测
         */

        // uri 拦截检测【如果检查通过，无需再检查token】
        boolean checkAccessUriResult = accessHelper.checkAccessUri(req);
        if (checkAccessUriResult) {
            req.setAttribute("access","true");
            return null;
        }

        // token 检测
        Result userTokenCheckResult = authHelper.checkUserToken(req, rep);
        if (userTokenCheckResult != null) {
            return userTokenCheckResult;
        }

        // uri 权限检测 TODO
        /*
        boolean userUriAuthCheckResult = true;
        if (!userUriAuthCheckResult){
            return false;
        }
        */
        return null;
    }

    public static boolean isAccess(HttpServletRequest req){
        String access = req.getHeader("access");
        if (access != null && "true".equals(access)){
            return true;
        }
        Object accessObj = req.getAttribute("access");
        if (accessObj != null && "true".equals(accessObj)){
            return true;
        }
        return false;
    }

}
