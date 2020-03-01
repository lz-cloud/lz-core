package com.wkclz.core.aop;


import com.wkclz.core.base.BaseModel;
import com.wkclz.core.exception.BizException;
import com.wkclz.core.util.BeanUtil;
import com.wkclz.core.util.DateUtil;
import com.wkclz.core.util.JdbcUtil;
import com.wkclz.core.util.StringUtil;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * RestAop
 * wangkc @ 2019-07-28 23:56:25
 */
@Aspect
@Component
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
    private final String POINT_CUT = "@within(org.apache.ibatis.annotations.Mapper)";

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

        Object[] args = point.getArgs();
        if (args != null){
            for (Object arg : args) {
                check(arg);
            }
        }

        // 请求具体方法
        Object obj = null;
        try {
            obj = point.proceed();
        } catch (Throwable throwable) {
            logger.error(throwable.getMessage(), throwable);
        }
        return obj;
    }

    private static void check(Object arg){
        if (arg == null){
            return;
        }
        if (!(arg instanceof BaseModel)){
            return;
        }

        BaseModel model = (BaseModel) arg;

        BeanUtil.removeBlank(model);
        String orderBy = model.getOrderBy();
        // 注入风险检测
        if (orderBy != null && !orderBy.equals(BaseModel.DEFAULE_ORDER_BY) && JdbcUtil.sqlInj(orderBy)) {
            throw BizException.error("orderBy 有注入风险，请谨慎操作！");
        }

        if (StringUtils.isBlank(orderBy)){
            orderBy = BaseModel.DEFAULE_ORDER_BY;
        }
        // 大小写处理
        orderBy = StringUtil.check2LowerCase(orderBy, "DESC");
        orderBy = StringUtil.check2LowerCase(orderBy, "ASC");
        // 驼峰处理
        orderBy = StringUtil.camelToUnderline(orderBy);

        model.setOrderBy(orderBy);
        // keyword 查询处理
        if (StringUtils.isNotBlank(model.getKeyword())) {
            model.setKeyword("%" + model.getKeyword() + "%");
        }
        // 时间范围查询处理
        DateUtil.formatDateRange(model);
    }

}
