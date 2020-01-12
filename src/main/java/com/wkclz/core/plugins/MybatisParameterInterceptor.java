package com.wkclz.core.plugins;

import org.apache.ibatis.executor.parameter.ParameterHandler;
import org.apache.ibatis.plugin.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.PreparedStatement;
import java.util.Properties;

@Intercepts({@Signature(type = ParameterHandler.class, method = "setParameters", args = PreparedStatement.class)})
public class MybatisParameterInterceptor implements Interceptor {

    private static final Logger logger = LoggerFactory.getLogger(MybatisParameterInterceptor.class);

    @Override
    public Object intercept(Invocation invocation) throws Throwable {

        logger.info("mybatis.setParameters.interceptor");

        /*
        ParameterHandler parameterHandler = (ParameterHandler) invocation.getTarget();
        //PreparedStatement preparedStatement = (PreparedStatement) invocation.getArgs()[0];
        Object parameterObject = parameterHandler.getParameterObject();
        */

        Object proceedReslut = invocation.proceed();
        return proceedReslut;
    }

    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    @Override
    public void setProperties(Properties properties) {

    }
}
