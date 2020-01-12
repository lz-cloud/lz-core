package com.wkclz.core.plugins;

import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.Properties;

@Intercepts({@Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class})})
public class MybatisQueryInterceptor implements Interceptor {

    private static final Logger logger = LoggerFactory.getLogger(MybatisQueryInterceptor.class);

    @Override
    public Object intercept(Invocation invocation) throws Throwable {

        logger.info("mybatis.query.interceptor");

        Object[] args = invocation.getArgs();
        for (Object arg : args) {
            if (arg == null) {
                continue;
            }
            String className = arg.getClass().getName();
            logger.info("args 类型：{}", className);
        }


        Object target = invocation.getTarget();
        Class<?> aClass = target.getClass();
        logger.info("target 类型：{}", aClass);


        Method method = invocation.getMethod();
        String name = method.getName();
        logger.info("method 名称：{}", name);


        long startTimeMillis = System.currentTimeMillis();
        Object proceedReslut = invocation.proceed();
        long endTimeMillis = System.currentTimeMillis();
        logger.info("<< ==== sql execute runnung time：{} millisecond ==== >>", (endTimeMillis - startTimeMillis));
        return proceedReslut;
    }

    @Override
    public Object plugin(Object target) {
        // 返回代理类
        return Plugin.wrap(target, this);
    }

    @Override
    public void setProperties(Properties properties) {

    }
}
