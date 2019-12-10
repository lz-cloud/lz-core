package com.wkclz.core.config.handler;

import com.wkclz.core.base.Result;
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
public class TraceHandler implements HandlerInterceptor {

    @Autowired
    private TraceHelper traceHelper;

    @Override
    public boolean preHandle(HttpServletRequest req, HttpServletResponse rep, Object handler) {
        try {
            traceHelper.checkTraceInfo(req, rep);
        } catch (Exception e){
            Result error = Result.error(e.getMessage());
            Result.responseError(rep, error);
            return false;
        }
        return true;
    }

}
