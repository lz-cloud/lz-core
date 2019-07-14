package com.wkclz.core.helper;

import com.alibaba.fastjson.JSONObject;
import com.wkclz.core.base.Sys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Description:
 * Created: wangkaicun @ 2019-02-13 20:55:11
 */
@Component
public class SystemConfigHelper extends BaseHelper {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    private static final String NAME_SPACE = "_SYSTEM_CONFIG";

    /**
     * redis 的缓存主动更新，java 的缓存被动更新
     */
    private static Long JAVA_LAST_ACTIVE_TIME = null;
    private static Map<String, String> SYSTEM_CONFIG = null;

    /**
     * 初始化 SYSTEM_CONFIG
     * @param systemConfigs
     */
    public void setSystemConfig(Map<String, String> systemConfigs){
        if (systemConfigs == null || systemConfigs.size() == 0){
            throw new RuntimeException("systemConfigs can not be null or empty!");
        }
        stringRedisTemplate.opsForValue().set(Sys.APPLICATION_GROUP + NAME_SPACE, JSONObject.toJSONString(systemConfigs));
        SYSTEM_CONFIG = systemConfigs;
    }

    private synchronized Map<String, String> getSystemConfigs(){
        Integer liveTime = getJavaCacheLiveTime();
        // java 缓存
        if (JAVA_LAST_ACTIVE_TIME != null && SYSTEM_CONFIG != null ){
            Long ttl = Long.valueOf(System.currentTimeMillis() - JAVA_LAST_ACTIVE_TIME);
            if (ttl.compareTo(Long.valueOf(liveTime) * 1000) == -1){
                return SYSTEM_CONFIG;
            }
        }
        JAVA_LAST_ACTIVE_TIME = System.currentTimeMillis();

        // redis 拉取
        String systemConfigsStr = stringRedisTemplate.opsForValue().get(Sys.APPLICATION_GROUP + NAME_SPACE);
        Map systemConfigs = JSONObject.parseObject(systemConfigsStr, Map.class);
        SYSTEM_CONFIG = systemConfigs;
        return SYSTEM_CONFIG;
    }

    public String getSystemConfig(String key) {
        if (key == null || key.trim().length() == 0){
            throw new RuntimeException("key must not be null ot empty!");
        }
        Map<String, String> systemConfigs = getSystemConfigs();
        if (systemConfigs == null || systemConfigs.size() == 0) {
            throw new RuntimeException("systemConfigs must be init after system start up!");
        }
        String value = systemConfigs.get(key);
        return value;
    }

}