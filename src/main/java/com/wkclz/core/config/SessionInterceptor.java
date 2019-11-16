package com.wkclz.core.config;

import com.wkclz.core.helper.AuthHelper;
import com.wkclz.core.helper.OrgDomainHelper;
import com.wkclz.core.helper.TraceHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Description: 仅用于创建 session
 * Created: wangkaicun @ 2017-10-18 下午11:41
 */
@Component
public class SessionInterceptor implements HandlerInterceptor {

    @Autowired
    private AuthHelper authHelper;
    @Autowired
    private OrgDomainHelper orgDomainHelper;

    @Override
    public boolean preHandle(HttpServletRequest req, HttpServletResponse rep, Object handler) {
        // 用户
        authHelper.checkUserSession(req);
        // 跟踪链
        TraceHelper.checkTraceInfo(req);
        // 域名/组织/租户
        boolean checkOrg = orgDomainHelper.checkOrgDomains(req, rep);
        if (!checkOrg){
            return false;
        }
        return true;
    }

}
