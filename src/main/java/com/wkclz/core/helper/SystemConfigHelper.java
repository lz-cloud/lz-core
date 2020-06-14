package com.wkclz.core.helper;

import com.alibaba.fastjson.JSONObject;
import com.wkclz.core.base.Sys;
import com.wkclz.core.exception.BizException;
import com.wkclz.core.helper.redis.bean.RedisMsgBody;
import com.wkclz.core.helper.redis.topic.RedisTopicConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.Map;

/**
 * Description:
 * Created: wangkaicun @ 2019-02-13 20:55:11
 */
public class SystemConfigHelper extends BaseHelper {


    /**
     * redis 的缓存主动更新，java 的缓存被动更新
     */
    private static Map<String, String> SYSTEM_CONFIG = null;

    /**
     * 初始化 SYSTEM_CONFIG
     */
    public static boolean reflash() {
        return reflash(SYSTEM_CONFIG);
    }
    public static boolean reflash(Map<String, String> systemConfigs) {
        if (CollectionUtils.isEmpty(systemConfigs)) {
            throw BizException.error("systemConfigs can not be null or empty!");
        }
        RedisMsgBody body = new RedisMsgBody();
        body.setTag(SystemConfigHelper.class.getName());
        body.setMsg(systemConfigs);

        String msg = JSONObject.toJSONString(body);
        StringRedisTemplate stringRedisTemplate = Sys.getBean(StringRedisTemplate.class);
        stringRedisTemplate.convertAndSend(RedisTopicConfig.CACHE_CONFIG_TOPIC, msg);
        return true;
    }

    public static boolean setLocal(Object msg) {
        if (msg == null) {
            throw BizException.error("systemConfigs can not be null or empty!");
        }
        Map<String, String> systemConfigs = JSONObject.parseObject(msg.toString(), Map.class);
        return setLocal(systemConfigs);
    }
    public static boolean setLocal(Map<String, String> systemConfigs) {
        if (CollectionUtils.isEmpty(systemConfigs)) {
            throw BizException.error("systemConfigs can not be null or empty!");
        }
        SYSTEM_CONFIG = systemConfigs;
        return true;
    }

    public static Map<String, String> getLocal() {
        return SYSTEM_CONFIG;
    }

    public static String getSystemConfig(String key) {
        if (key == null || key.trim().length() == 0) {
            throw BizException.error("key must not be null ot empty!");
        }
        Map<String, String> systemConfigs = getLocal();
        if (systemConfigs == null || systemConfigs.size() == 0) {
            throw BizException.error("systemConfigs must be init after system start up!");
        }
        String value = systemConfigs.get(key);
        return value;
    }

}