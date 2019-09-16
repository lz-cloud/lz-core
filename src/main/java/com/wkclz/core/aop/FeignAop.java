package com.wkclz.core.aop;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.wkclz.core.base.Result;
import com.wkclz.core.exception.BizException;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * FeignAop
 * wangkc @ 2019-08-25 20:33:55
 */
//@Aspect
//@Component
public class FeignAop {

    /**
     * : @Around环绕通知
     * : @Before通知执行
     * : @Before通知执行结束
     * : @Around环绕通知执行结束
     * : @After后置通知执行了!
     * : @AfterReturning第一个后置返回通知的返回值：18
     */

    private static final Logger logger = LoggerFactory.getLogger(FeignAop.class);
    private static ObjectMapper objectMapper = new ObjectMapper();
    private final String POINT_CUT = "@within(org.springframework.cloud.openfeign.FeignClient) )";


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

}
