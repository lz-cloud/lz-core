package com.wkclz.core.config;

import com.wkclz.core.helper.InterceptorHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Description: 权限
 * Created: wangkaicun @ 2017-10-18 下午11:41
 */
@Component
public class AuthInterceptor extends HandlerInterceptorAdapter {

    @Autowired
    private InterceptorHelper interceptorHelper;
    @Override
    public boolean preHandle(HttpServletRequest req, HttpServletResponse rep, Object handler) {
        return interceptorHelper.preHandle(req, rep);
    }

    @Override
    public void afterCompletion(HttpServletRequest req, HttpServletResponse rep, Object handler, Exception ex) {
        interceptorHelper.afterCompletion(req);
    }

}
