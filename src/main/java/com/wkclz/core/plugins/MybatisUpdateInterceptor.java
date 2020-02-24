package com.wkclz.core.plugins;

import com.wkclz.core.base.BaseModel;
import com.wkclz.core.exception.BizException;
import com.wkclz.core.helper.AuthHelper;
import com.wkclz.core.pojo.dto.User;
import com.wkclz.core.pojo.enums.ResultStatus;
import org.apache.commons.collections.CollectionUtils;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.plugin.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collection;
import java.util.Map;
import java.util.Properties;

@Intercepts({@Signature(type = Executor.class, method = "update", args = {MappedStatement.class, Object.class})})
public class MybatisUpdateInterceptor implements Interceptor {

    private static final Logger logger = LoggerFactory.getLogger(MybatisUpdateInterceptor.class);
    @Autowired
    private AuthHelper authHelper;

    @Override
    public Object intercept(Invocation invocation) throws Throwable {

        User user = authHelper.getUser();
        Long userId = -1L;
        if (user != null) {
            userId = user.getUserId();
        }

        // 参数处理
        Object[] args = invocation.getArgs();
        MappedStatement mappedStatement = (MappedStatement) args[0];
        Object parameter = args[1];
        SqlCommandType sqlCommandType = mappedStatement.getSqlCommandType();
        String id = mappedStatement.getId();
        SqlCommandType commandType = MybatisConfiguration.getCommandType(id, sqlCommandType);

        // 需要检查乐观锁, 使用完后要去除，保证线程安全
        boolean chechVersion = MDC.get(MybatisConfiguration.CHECH_VERSION) != null;
        boolean chechId = MDC.get(MybatisConfiguration.CHECH_ID) != null;
        MDC.remove(MybatisConfiguration.CHECH_VERSION);
        MDC.remove(MybatisConfiguration.CHECH_ID);

        // 参数为对象
        if (parameter != null && parameter instanceof BaseModel) {
            checkModel(parameter, commandType, userId, chechVersion, chechId);
        }

        // 参数为 List 【在 Map 里面】
        if (parameter != null && parameter instanceof Map){
            Map parameterMap = (Map)parameter;
            Collection values = parameterMap.values();
            for (Object parameterObj : values) {
                if (parameterObj != null && parameterObj instanceof Collection){
                    Collection parameters = (Collection)parameterObj;
                    for (Object p:parameters) {
                        boolean isBaseModel = checkModel(p, commandType, userId, chechVersion, chechId);
                        if (!isBaseModel) {
                            break;
                        }
                    }
                }
            }
        }
        logger.debug("mybatis.update.interceptor: operate user: {}", userId);

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

    private static boolean checkModel(Object paramter, SqlCommandType commandType, Long userId, boolean chechVersion, boolean chechId){
        if (!(paramter instanceof BaseModel)) {
            return false;
        }
        BaseModel clearPatameter = (BaseModel) paramter;
        // insert, upadte, delete 修改人
        clearPatameter.setUpdateBy(userId);
        clearPatameter.setStatus(1);
        // insert 时需要附加创建人
        if (commandType == SqlCommandType.INSERT) {
            clearPatameter.setId(null);
            clearPatameter.setVersion(null);
            clearPatameter.setCreateBy(userId);
            if (clearPatameter.getSort() == null){
                clearPatameter.setSort(0);
            }
        }
        // update 时 id 不能为空
        if (commandType == SqlCommandType.UPDATE) {
            if (chechId && clearPatameter.getId() == null) {
                throw BizException.error(ResultStatus.UPDATE_NO_ID);
            }
            // 批量更新不处理 version
            if (chechVersion && clearPatameter.getVersion() == null) {
                throw BizException.error(ResultStatus.UPDATE_NO_VERSION);
            }
        }
        // delete 时 id, ids 不能同时为空, 删除不校验 version
        if (commandType == SqlCommandType.DELETE) {
            if (clearPatameter.getId() == null && CollectionUtils.isEmpty(clearPatameter.getIds())) {
                throw BizException.error(ResultStatus.UPDATE_NO_ID);
            }
        }
        return true;
    }
}
