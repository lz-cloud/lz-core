package com.wkclz.core.aop;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.wkclz.core.base.BaseModel;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

/**
 * RestAop
 * wangkc @ 2019-07-28 23:56:25
 */
// @Aspect
// @Component
public class DaoAop {

    /**
     * : @Around环绕通知
     * : @Before通知执行
     * : @Before通知执行结束
     * : @Around环绕通知执行结束
     * : @After后置通知执行了!
     * : @AfterReturning第一个后置返回通知的返回值：18
     */

    private static final Logger logger = LoggerFactory.getLogger(DaoAop.class);
    private static ObjectMapper objectMapper = new ObjectMapper();
    private final String POINT_CUT = "execution(public * *..dao..*Mapper.*(..)) || execution(public * *..mapper..*Mapper.*(..))";


    private final List<String> WRITE_ACTIONS = Arrays.asList(
        "insert",
        "insertSelective",
        "updateByExampleSelective",
        "updateByExampleWithBLOBs",
        "updateByExample",
        "updateByPrimaryKeySelective",
        "updateByPrimaryKey",
        "updateByPrimaryKeyWithBLOBs",
        "insertBatch"
    );


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


        MethodSignature signature = (MethodSignature) point.getSignature();
        logger.debug("@Around环绕通知：" + signature.toString());

        Method method = signature.getMethod();
        Class<?> returnType = method.getReturnType();


        Object[] args = point.getArgs();

        /*
        boolean isPage = false;
        // 分页查询方法，自动处理分页
        if (PageData.class == returnType){
            BaseModel model = getFirstModelParam(args);
            model.setIsPage(1);
            isPage = true;
            BaseRepoHandler.pagePreHandle(model);
        }
        */


        // 插入，更新方法，自动处理最后更新人，最后更新时间
        String name = method.getName();
        if (WRITE_ACTIONS.contains(name)) {
            // TODO 完成具体的操作
        }


        // 请求具体方法
        Object obj = null;
        try {

            obj = point.proceed();
            /*
            if (isPage){
                List<Object> list = (List)point.proceed();
                obj = list;
            } else {
                obj = point.proceed();
            }
            */

        } catch (Throwable throwable) {
            logger.error("Throwable", throwable);
        }

        logger.debug("@Around环绕通知执行结束");
        return obj;
    }


    /**
     * 获取第一个 BaseModel 的参数
     *
     * @param args
     * @param <T>
     * @return
     */
    private static <T extends BaseModel> T getFirstModelParam(Object[] args) {
        T t = null;
        if (args == null) {
            BaseModel baseModel = new BaseModel();
            t = (T) baseModel;
            return t;
        }
        for (Object object : args) {
            if (object instanceof BaseModel) {
                t = (T) object;
                break;
            }
        }
        if (t == null) {
            BaseModel baseModel = new BaseModel();
            t = (T) baseModel;
        }
        return t;
    }

}
