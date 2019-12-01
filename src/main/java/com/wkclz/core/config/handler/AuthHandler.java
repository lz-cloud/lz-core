package com.wkclz.core.config.handler;

import com.wkclz.core.helper.AccessHelper;
import com.wkclz.core.helper.ApiDomainHelper;
import com.wkclz.core.helper.AuthHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Description: 此处拦截不再加入到 WebMvcConfigurer， 真正的拦截交给网关
 * Created: wangkaicun @ 2017-10-18 下午11:41
 */
// @Component
public class AuthHandler implements HandlerInterceptor {

    @Autowired
    private AuthHelper authHelper;
    @Autowired
    private AccessHelper accessHelper;
    @Autowired
    private ApiDomainHelper apiDomainHelper;

    public boolean preHandle(HttpServletRequest req, HttpServletResponse rep) {

        // API 安全检测
        boolean apiDomainCheckResult = apiDomainHelper.checkApiDomains(req, rep);
        if (!apiDomainCheckResult) {
            return false;
        }

        /**
         * 用户登录检测
         */

        // uri 拦截检测【如果检查通过，无需再检查token】
        boolean checkAccessUriResult = accessHelper.checkAccessUri(req);
        if (checkAccessUriResult) {
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
        return true;
    }

}
