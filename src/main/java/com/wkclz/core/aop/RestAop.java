package com.wkclz.core.aop;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wkclz.core.base.BaseModel;
import com.wkclz.core.base.Result;
import com.wkclz.core.base.Sys;
import com.wkclz.core.exception.BizException;
import com.wkclz.core.helper.AuthHelper;
import com.wkclz.core.helper.InterceptorHelper;
import com.wkclz.core.helper.OrgDomainHelper;
import com.wkclz.core.helper.TraceHelper;
import com.wkclz.core.pojo.dto.User;
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

import javax.servlet.http.HttpServletRequest;
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
    private final String POINT_CUT = "(@within(org.springframework.stereotype.Controller) || @within(org.springframework.web.bind.annotation.RestController))";

    @Autowired
    private AuthHelper authHelper;
    @Autowired
    private OrgDomainHelper orgDomainHelper;
    @Autowired
    private InterceptorHelper interceptorHelper;

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
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            return servletRequestHandle(point, attributes);
        } else {
            return normalHandle(point);
        }
    }


    private Object servletRequestHandle(ProceedingJoinPoint point, ServletRequestAttributes attributes) {

        HttpServletRequest req = attributes.getRequest();

        // 给入参赋值
        String args = setArgs(point, req);
        /*
        if (req.getAttribute("isInner") == null){
            // 权限验证
            String authed = req.getHeader("authed");
            if (authed == null || !"true".equals(authed)){
                HttpServletResponse rep = attributes.getResponse();
                boolean handle = interceptorHelper.preHandle(req, rep);
                if (!handle){
                    logger.warn("======> no user found, please login again!");
                    return Result.remind("未找到登录信息，请重新登录!");
                }
            }
        }
        */

        // 追踪信息
        TraceInfo traceInfo = TraceHelper.checkTraceInfo(req);

        String method = req.getMethod();
        String uri = req.getRequestURI();
        Date requestTime = new Date();
        Date responeTime;
        Long costTime;

        // 请求具体方法
        Object obj;
        try {
            obj = point.proceed();
        } catch (Throwable throwable) {
            obj = Result.error(throwable.getMessage());
            logger.error("Throwable: "+ throwable.getLocalizedMessage());
            throwable.printStackTrace();
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
        /*
        String value = null;
        try {
            value = objectMapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            logger.error("JsonProcessingException", e);
        }
        */
        logger.info("{}|{}|{}|{}ms|{}|{}",traceInfo.getTraceId(), traceInfo.getSeq(), method, costTime, uri, args);

        return obj;
    }

    public Object normalHandle(ProceedingJoinPoint point) {
        Object obj = null;

        try {
            obj = point.proceed();
        } catch (Throwable throwable) {
            // 自定义异常，转换为 Result
            if (throwable instanceof BizException){
                BizException bizException = (BizException)throwable;
                Result result = new Result();
                result.setError(bizException.getMessage());
                if (bizException.getCode() == 0){
                    result.setCode(0);
                }
                obj = result;
            } else {
                // 非自定义异常不处理
                logger.error("Throwable", throwable);
            }
        }
        return obj;
    }


    private String setArgs(ProceedingJoinPoint point, HttpServletRequest req) {
        Long userId = null;
        Long orgId = null;
        String value = null;

        // param 赋值
        Object[] args = point.getArgs();
        if (args != null && args.length > 0) {
            List<Object> baseModelArgs = new ArrayList<>();
            for (Object arg : args) {
                if (arg instanceof BaseModel) {
                    BaseModel model = (BaseModel) arg;
                    userId = this.setCurrentUserId(model, req, userId);
                    orgId = this.setCurrentOrgId(model, req, orgId);
                    baseModelArgs.add(arg);
                }
                if (arg instanceof ArrayList) {
                    ArrayList list = (ArrayList) arg;
                    boolean isBaseModel = false;
                    for (Object l : list) {
                        if (l instanceof BaseModel) {
                            isBaseModel = true;
                            BaseModel model = (BaseModel) l;
                            userId = this.setCurrentUserId(model, req, userId);
                            orgId = this.setCurrentOrgId(model, req, orgId);
                        }
                    }
                    if (isBaseModel) {
                        baseModelArgs.add(arg);
                    }
                }
            }
            if (!baseModelArgs.isEmpty()) {
                try {
                    value = objectMapper.writeValueAsString(baseModelArgs);
                } catch (JsonProcessingException e) {
                    logger.error("JsonProcessingException", e);
                }
            }
        }
        return value;
    }


    private Long setCurrentUserId(BaseModel model, HttpServletRequest req, Long userId) {
        if (model.getCurrentUserId() == null) {
            if (userId == null) {
                User user = authHelper.getSession(req);
                if (user != null) {
                    userId = user.getUserId();
                }
            }
            if (userId != null) {
                model.setCurrentUserId(userId);
            }
        }
        return userId;
    }

    private Long setCurrentOrgId(BaseModel model, HttpServletRequest req, Long orgId) {
        if (model.getCurrentOrgId() == null) {
            if (orgId == null) {
                Long domainOrgId = orgDomainHelper.getOrgId(req);
                if (domainOrgId != null) {
                    orgId = domainOrgId;
                }
            }
            if (orgId != null) {
                model.setCurrentOrgId(orgId);
            }
        }
        return orgId;
    }


}
