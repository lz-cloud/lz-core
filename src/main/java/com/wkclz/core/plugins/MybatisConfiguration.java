package com.wkclz.core.plugins;

import com.wkclz.core.base.BaseMapper;
import org.apache.ibatis.mapping.SqlCommandType;
import org.slf4j.MDC;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class MybatisConfiguration {

    protected static final String CHECK_ID = "check_id";
    protected static final String CHECK_VERSION = "check_version";

    // Mapper 操作缓存，减少反射
    private static Map<String, SqlCommandType> SQL_COMMAND_TYPE_MAPT = null;

    // @Bean 参数处理不友好，更换方法
    public MybatisParameterInterceptor parameterInterceptor() {
        return new MybatisParameterInterceptor();
    }

    // @Bean 没法拦截参数
    public MybatisParameterizeInterceptor parameterizeInterceptor() {
        return new MybatisParameterizeInterceptor();
    }

    // @Bean 没法拦截参数
    public MybatisPrepareInterceptor prepareInterceptor() {
        return new MybatisPrepareInterceptor();
    }

    // @Bean
    public MybatisQueryInterceptor queryInterceptor() {
        return new MybatisQueryInterceptor();
    }

    @Bean
    public MybatisUpdateInterceptor updateInterceptor() {
        return new MybatisUpdateInterceptor();
    }



    /**
     * @param mappedStatementId
     * @return
     */
    public static SqlCommandType getCommandType(String mappedStatementId, SqlCommandType type) {

        // 初次访问，只会是缓存
        if (SQL_COMMAND_TYPE_MAPT == null) {
            SQL_COMMAND_TYPE_MAPT = new HashMap<>();
            Class<BaseMapper> baseMapperClass = BaseMapper.class;
            Method[] methods = baseMapperClass.getDeclaredMethods();
            for (Method method : methods) {
                String methodName = method.getName();
                if ("insert".equals(methodName) || "insertBatch".equals(methodName)) {
                    SQL_COMMAND_TYPE_MAPT.put(methodName, SqlCommandType.INSERT);
                    continue;
                }
                if ("updateAll".equals(methodName) || "updateSelective".equals(methodName)) {
                    SQL_COMMAND_TYPE_MAPT.put(methodName, SqlCommandType.UPDATE);
                    continue;
                }
                if ("updateBatch".equals(methodName)) {
                    SQL_COMMAND_TYPE_MAPT.put(methodName, SqlCommandType.UPDATE);
                    continue;
                }
                if ("delete".equals(methodName)) {
                    SQL_COMMAND_TYPE_MAPT.put(methodName, SqlCommandType.DELETE);
                    continue;
                }
                SQL_COMMAND_TYPE_MAPT.put(methodName, SqlCommandType.SELECT);
            }
        }

        int lastIndex = mappedStatementId.lastIndexOf(".");
        if (lastIndex < 0){
            return type;
        }
        mappedStatementId = mappedStatementId.substring(lastIndex +1);
        SqlCommandType sqlCommandType = SQL_COMMAND_TYPE_MAPT.get(mappedStatementId);
        // SQL_COMMAND_TYPE_MAPT 额缓存，才是自己生成的，也才应该走检查流程
        if (sqlCommandType != null && sqlCommandType != SqlCommandType.SELECT){
            if ("updateAll".equals(mappedStatementId) || "updateSelective".equals(mappedStatementId)) {
                // 需要检查乐观锁, 使用完后要去除，保证线程安全
                MDC.put(CHECK_VERSION, "1");
                MDC.put(CHECK_ID, "1");
            }
            if ("updateBatch".equals(mappedStatementId)) {
                MDC.put(CHECK_ID, "1");
            }
        }
        return sqlCommandType == null ? type : sqlCommandType;
    }


}
