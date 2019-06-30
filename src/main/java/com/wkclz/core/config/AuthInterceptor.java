package com.wkclz.core.config;

import com.wkclz.core.helper.InterceptorHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Description: 此处拦截不再加入到 WebMvcConfigurer， 真正的拦截交给网关
 * Created: wangkaicun @ 2017-10-18 下午11:41
 */
// @Component
public class AuthInterceptor implements HandlerInterceptor {

    @Autowired
    private InterceptorHelper interceptorHelper;
    @Override
    public boolean preHandle(HttpServletRequest req, HttpServletResponse rep, Object handler) {
        return interceptorHelper.preHandle(req, rep);
    }

    @Override
    public void afterCompletion(HttpServletRequest req, HttpServletResponse rep, Object handler, Exception ex) {
        interceptorHelper.afterCompletion(req, rep);
    }

}
