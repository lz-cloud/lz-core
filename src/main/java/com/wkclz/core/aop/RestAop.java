package com.wkclz.core.aop;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wkclz.core.base.Result;
import com.wkclz.core.base.Sys;
import com.wkclz.core.exception.BizException;
import com.wkclz.core.helper.TraceHelper;
import com.wkclz.core.pojo.entity.TraceInfo;
import com.wkclz.core.pojo.enums.EnvType;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * RestAop
 * wangkc @ 2019-07-28 23:56:25
 */
@Aspect
@Component
public class RestAop {

    /**
     * : @Around环绕通知
     * : @Before通知执行
     * : @Before通知执行结束
     * : @Around环绕通知执行结束
     * : @After后置通知执行了!
     * : @AfterReturning第一个后置返回通知的返回值：18
     */

    private static final Logger logger = LoggerFactory.getLogger(RestAop.class);
    private static ObjectMapper objectMapper = new ObjectMapper();
    private final String POINT_CUT = "(" +
        "@within(org.springframework.stereotype.Controller) " +
        "|| @within(org.springframework.web.bind.annotation.RestController)" +
        ") && !execution(* org.springframework..*.*(..))";

    @Autowired
    private TraceHelper traceHelper;

    @Pointcut(POINT_CUT)
    public void pointCut() {
    }

    /**
     * 环绕通知：
     * 注意:Spring AOP的环绕通知会影响到AfterThrowing通知的运行,不要同时使用
     * <p>
     * 环绕通知非常强大，可以决定目标方法是否执行，什么时候执行，执行时是否需要替换方法参数，执行完毕是否需要替换返回值。
     * 环绕通知第一个参数必须是org.aspectj.lang.ProceedingJoinPoint类型
     */
    @Around(value = POINT_CUT)
    public Object doAroundAdvice(ProceedingJoinPoint point) {
        return servletRequestHandle(point);
    }

    private Object servletRequestHandle(ProceedingJoinPoint point) {
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        HttpServletRequest req = requestAttributes.getRequest();
        HttpServletResponse rep = requestAttributes.getResponse();

        // 追踪信息
        TraceInfo traceInfo = traceHelper.checkTraceInfo(req, rep);

        String method = req.getMethod();
        String uri = req.getRequestURI();
        Date requestTime = new Date();
        Date responeTime;
        Long costTime;

        // 请求具体方法
        Object obj = null;
        try {
            obj = point.proceed();
        } catch (Throwable throwable) {
            BizException bizException = getBizException(throwable);
            if (bizException != null){
                obj = Result.error(bizException);
            } else {
                obj = Result.error(throwable.getMessage());
            }
            logger.error(throwable.getMessage(),throwable);
        }

        // 返回参数处理
        responeTime = new Date();
        costTime = responeTime.getTime() - requestTime.getTime();
        if (obj != null && obj instanceof Result) {
            if (Sys.CURRENT_ENV != EnvType.PROD) {
                Result result = (Result) obj;
                result.setRequestTime(requestTime);
                result.setResponeTime(responeTime);
                result.setCostTime(costTime);
            }
        }

        // 日志
        String debug = req.getParameter("debug");
        boolean isDebug = logger.isDebugEnabled() || ( debug != null && "1".equals(debug));

        String args = getArgs(point);
        if (isDebug){
            String value = null;
            try {
                value = objectMapper.writeValueAsString(obj);
            } catch (JsonProcessingException e) {
                logger.error(e.getMessage(), e);
            }
            if (logger.isDebugEnabled()){
                logger.debug("{}|{}ms|{}|{}|{}", method, costTime, uri, args, value);
            } else {
                logger.info("{}|{}ms|{}|{}|{}", method, costTime, uri, args, value);
            }
        } else {
            logger.info("{}|{}ms|{}|{}", method, costTime, uri, args);
        }

        return obj;
    }

    private String getArgs(ProceedingJoinPoint point) {
        String value = null;
        Object[] args = point.getArgs();
        if (args != null && args.length > 0) {
            List<Object> baseModelArgs = new ArrayList<>();
            for (Object arg : args) {
                if (arg instanceof HttpServletRequest) {
                    continue;
                }
                if (arg instanceof HttpServletResponse) {
                    continue;
                }
                if (arg instanceof MultipartFile) {
                    continue;
                }
                baseModelArgs.add(arg);
            }
            if (!baseModelArgs.isEmpty()) {
                try {
                    value = objectMapper.writeValueAsString(baseModelArgs);
                } catch (JsonProcessingException e) {
                    logger.error(e.getMessage(), e);
                }
            }
        }
        return value;
    }

    /**
     * Throwable 找 BizException，找二级原因
     * @param throwable
     * @return
     */
    private static BizException getBizException(Throwable throwable){
        if (throwable == null){
            return null;
        }
        if (throwable instanceof BizException){
            return (BizException) throwable;
        }
        Throwable cause = throwable.getCause();
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
