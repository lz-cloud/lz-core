package com.wkclz.core.plugins;

import com.wkclz.core.base.BaseModel;
import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Map;
import java.util.Properties;


@Intercepts({
    @Signature(type = Executor.class,method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class}),
    @Signature(type = Executor.class,method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class, CacheKey.class, BoundSql.class})
})
public class MybatisQueryInterceptor implements Interceptor {

    private static final Logger logger = LoggerFactory.getLogger(MybatisQueryInterceptor.class);

    @Override
    public Object intercept(Invocation invocation) throws Throwable {

        Object[] args = invocation.getArgs();
        Object parameter = args[1];

        // 参数为对象
        if (parameter != null && parameter instanceof BaseModel) {
            checkModel(parameter, false);
        }

        // 参数为 List 【在 Map 里面】
        if (parameter != null && parameter instanceof Map){
            Map parameterMap = (Map)parameter;
            Collection values = parameterMap.values();
            for (Object parameterObj : values) {
                if (parameterObj != null && parameterObj instanceof Collection){
                    Collection parameters = (Collection)parameterObj;
                    for (Object p:parameters) {
                        boolean isBaseModel = checkModel(p, true);
                        if (!isBaseModel) {
                            break;
                        }
                    }
                }
            }
        }

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

    private static boolean checkModel(Object paramter, boolean isBatch){
        if (!(paramter instanceof BaseModel)) {
            return false;
        }
        BaseModel clearPatameter = (BaseModel) paramter;
        clearPatameter.init();
        return true;
    }

}
