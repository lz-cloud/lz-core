package com.wkclz.core.config.handler;

import com.wkclz.core.base.Result;
import com.wkclz.core.exception.BizException;
import com.wkclz.core.helper.TraceHelper;
import org.jboss.logging.MDC;
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
            MDC.clear();
            traceHelper.checkTraceInfo(req, rep);
        } catch (Exception e){
            Result error;
            BizException bizException = getBizException(e);
            if (bizException != null){
                error = Result.error(bizException);
            } else {
                error = Result.error(e.getMessage());
            }
            Result.responseError(rep, error);
            return false;
        }
        return true;
    }


    /**
     * 获取真实的原因
     * @param exception
     * @return
     */
    private static BizException getBizException(Exception exception){
        if (exception == null){
            return null;
        }
        if (exception instanceof BizException){
            return (BizException) exception;
        }
        Throwable cause = exception.getCause();
        if (cause == null){
            return null;
        }
        if (cause instanceof BizException){
            return (BizException) cause;
        }
        cause = cause.getCause();
        if (cause == null){
            return null;
        }
        if (cause instanceof BizException){
            return (BizException) cause;
        }
        return null;
    }

}
