package com.wkclz.core.aop;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wkclz.core.base.BaseModel;
import com.wkclz.core.helper.AuthHelper;
import com.wkclz.core.helper.OrgDomainHelper;
import com.wkclz.core.pojo.dto.User;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;

@Aspect
@Component
public class RestAop {

    /**
     *  : @Around环绕通知
     *  : @Before通知执行
     *  : @Before通知执行结束
     *  : @Around环绕通知执行结束
     *  : @After后置通知执行了!
     *  : @AfterReturning第一个后置返回通知的返回值：18
     */

    private static final Logger logger = LoggerFactory.getLogger(RestAop.class);
    private static ObjectMapper objectMapper = new ObjectMapper();
    private final String POINT_CUT = "execution(public * com.wkclz.*.controller.custom..*.*(..))";


    @Autowired
    private AuthHelper authHelper;
    @Autowired
    private OrgDomainHelper orgDomainHelper;


    @Pointcut(POINT_CUT)
    public void pointCut(){}

    @Before(value = "pointCut()")
    public void before(JoinPoint joinPoint) {

        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest req = attributes.getRequest();

        String requestURI = req.getRequestURI();
        Long userId =  null;
        Long orgId = null;

        //获取目标方法参数信息
        Object[] args = joinPoint.getArgs();
        if (args == null || args.length == 0){
            return;
        }

        for (Object arg:args) {
            if (arg instanceof BaseModel){
                BaseModel model = (BaseModel)arg;
                userId = setCurrentUserId(model, req, userId);
                orgId = setCurrentOrgId(model, req, orgId);
            }
            if (arg instanceof ArrayList){
                ArrayList list = (ArrayList)arg;
                for (Object l : list){
                    if (l instanceof BaseModel){
                        BaseModel model = (BaseModel)l;
                        userId = setCurrentUserId(model, req, userId);
                        orgId = setCurrentOrgId(model, req, orgId);
                    }
                }
            }
        }

        String value = null;
        try {
            value = objectMapper.writeValueAsString(args);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        logger.info("request uri: {}, args: {}", requestURI, value);
    }


    /**
     * 后置返回
     *      如果第一个参数为JoinPoint，则第二个参数为返回值的信息
     *      如果第一个参数不为JoinPoint，则第一个参数为returning中对应的参数
     * returning：限定了只有目标方法返回值与通知方法参数类型匹配时才能执行后置返回通知，否则不执行，
     *            参数为Object类型将匹配任何目标返回值
     */
    @AfterReturning(value = POINT_CUT,returning = "result")
    public void doAfterReturningAdvice1(JoinPoint joinPoint,Object result){
        // logger.info("第一个后置返回通知的返回值："+result);
    }

    @AfterReturning(value = POINT_CUT,returning = "result",argNames = "result")
    public void doAfterReturningAdvice2(String result){
        // logger.info("第二个后置返回通知的返回值："+result);
    }
    //第一个后置返回通知的返回值：姓名是大大
    //第二个后置返回通知的返回值：姓名是大大
    //第一个后置返回通知的返回值：{name=小小, id=1}

    /**
     * 后置异常通知
     *  定义一个名字，该名字用于匹配通知实现方法的一个参数名，当目标方法抛出异常返回后，将把目标方法抛出的异常传给通知方法；
     *  throwing:限定了只有目标方法抛出的异常与通知方法相应参数异常类型时才能执行后置异常通知，否则不执行，
     *            对于throwing对应的通知方法参数为Throwable类型将匹配任何异常。
     * @param joinPoint
     * @param exception
     */
    @AfterThrowing(value = POINT_CUT,throwing = "exception")
    public void doAfterThrowingAdvice(JoinPoint joinPoint,Throwable exception){
        logger.info(joinPoint.getSignature().getName());
        if(exception instanceof NullPointerException){
            logger.info("发生了空指针异常!!!!!");
        }
    }

    @After(value = POINT_CUT)
    public void doAfterAdvice(JoinPoint joinPoint){
        // logger.info("后置通知执行了!");
    }

    /**
     * 环绕通知：
     *   注意:Spring AOP的环绕通知会影响到AfterThrowing通知的运行,不要同时使用
     *
     *   环绕通知非常强大，可以决定目标方法是否执行，什么时候执行，执行时是否需要替换方法参数，执行完毕是否需要替换返回值。
     *   环绕通知第一个参数必须是org.aspectj.lang.ProceedingJoinPoint类型
     */
    /*
    @Around(value = POINT_CUT)
    public Object doAroundAdvice(ProceedingJoinPoint proceedingJoinPoint){
        logger.info("@Around环绕通知："+proceedingJoinPoint.getSignature().toString());
        Object obj = null;
        try {
            //可以加参数
            obj = proceedingJoinPoint.proceed();
            logger.info(obj.toString());
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        logger.info("@Around环绕通知执行结束");
        return obj;
    }
    */



    private Long setCurrentUserId(BaseModel model, HttpServletRequest req, Long userId){
        if (model.getCurrentUserId() == null){
            if (userId == null){
                User user = authHelper.getSession(req);
                if (user != null){
                    userId = user.getUserId();
                }
            }
            if (userId != null){
                model.setCurrentUserId(userId);
            }
        }
        return userId;
    }

    private Long setCurrentOrgId(BaseModel model, HttpServletRequest req, Long orgId){
        if (model.getCurrentOrgId() == null){
            if (orgId == null){
                Long domainOrgId = orgDomainHelper.getOrgId(req);
                if (domainOrgId != null){
                    orgId = domainOrgId;
                }
            }
            if (orgId != null){
                model.setCurrentOrgId(orgId);
            }
        }
        return orgId;
    }


}
