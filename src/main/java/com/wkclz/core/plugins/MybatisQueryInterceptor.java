package com.wkclz.core.plugins;

import com.wkclz.core.base.BaseModel;
import com.wkclz.core.exception.BizException;
import com.wkclz.core.util.BeanUtil;
import com.wkclz.core.util.DateUtil;
import com.wkclz.core.util.JdbcUtil;
import com.wkclz.core.util.StringUtil;
import org.apache.commons.lang3.StringUtils;
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

// 已在 MybatisConfiguration 配置不拦截,拦截了也没法在预编译前
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
        BaseModel model = (BaseModel) paramter;
        model.init();

        BeanUtil.removeBlank(model);
        String orderBy = model.getOrderBy();
        // 注入风险检测
        if (orderBy != null && !orderBy.equals(BaseModel.DEFAULE_ORDER_BY) && JdbcUtil.sqlInj(orderBy)) {
            throw BizException.error("orderBy 有注入风险，请谨慎操作！");
        }

        // 大小写处理
        model.setOrderBy(StringUtil.check2LowerCase(orderBy, "DESC"));
        model.setOrderBy(StringUtil.check2LowerCase(orderBy, "ASC"));
        // 驼峰处理
        model.setOrderBy(StringUtil.camelToUnderline(orderBy));
        // keyword 查询处理
        if (StringUtils.isNotBlank(model.getKeyword())) {
            model.setKeyword("%" + model.getKeyword() + "%");
        }
        // 时间范围查询处理
        DateUtil.formatDateRange(model);

        return true;
    }

}
