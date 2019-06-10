package com.wkclz.core.config;

import com.wkclz.core.helper.AuthHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Description: 仅用于创建 session
 * Created: wangkaicun @ 2017-10-18 下午11:41
 */
@Component
public class SessionInterceptor extends HandlerInterceptorAdapter {

    @Autowired
    private AuthHelper authHelper;

    @Override
    public boolean preHandle(HttpServletRequest req, HttpServletResponse rep, Object handler) {
        authHelper.checkUserSession(req);
        return true;
    }

}
