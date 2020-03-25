package com.wkclz.core.aop;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.netflix.client.ClientException;
import com.wkclz.core.base.Result;
import com.wkclz.core.exception.BizException;
import com.wkclz.core.pojo.enums.ResultStatus;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * FeignAop
 * wangkc @ 2019-08-25 20:33:55
 */
@Aspect
@Component
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
    private final String POINT_CUT = "@within(org.springframework.cloud.openfeign.FeignClient)";


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
            logger.error(throwable.getMessage(), throwable);
            // feign 可判别异常
            if (throwable instanceof RuntimeException && throwable.getCause() instanceof ClientException){
                throw BizException.result(ResultStatus.NO_AVAILABLE_SERVER.getCode(), throwable.getMessage());
            }

            // 其他异常
            throw BizException.error(ResultStatus.UNKNOWN_RIBBON_ERROR);
        }

        // 请求成功，解析请求结果
        if (obj == null){
            return obj;
        }
        if (obj instanceof Result){
            Result result = (Result) obj;
            Integer code = result.getCode();
            if (code != 1 ) {
                BizException error = BizException.error(result.getMsg());
                error.setCode(code);
                throw error;
            }
        }
        return obj;
    }

}
