package com.wkclz.core.helper.cache.impl;

import com.wkclz.core.exception.BizException;
import com.wkclz.core.helper.cache.LzCache;
import com.wkclz.core.helper.redis.topic.RedisTopicConfig;
import com.wkclz.core.util.BeanUtil;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class LzCacheImpl implements LzCache {

    private static final Logger logger = LoggerFactory.getLogger(LzCacheImpl.class);
    private static final Map<String, List> CACHE_CONFIG = new HashMap<>();

    @Autowired
    private StringRedisTemplate stringRedisTemplate;


    @Override
    public <T> T get(T param){
        if (param == null){
            return null;
        }
        String className = param.getClass().getName();
        List<T> cache = cache(className, null);
        if (CollectionUtils.isEmpty(cache)){
            throw BizException.error(className + " has no cache!");
        }

        List<Method> valuedList = BeanUtil.getValuedList(param);
        if (valuedList == null) {
            return null;
        }
        try {
            for (T config : cache) {
                int fit = 0;
                for (Method method : valuedList) {
                    Object configValue = method.invoke(config);
                    String paramValue = method.invoke(param).toString();
                    if (configValue != null && paramValue.equals(configValue.toString())){
                        fit ++;
                        continue;
                    }
                }
                if (fit == valuedList.size()){
                    return config;
                }
            }
        } catch (IllegalAccessException e) {
            logger.error(e.getMessage(), e);
        } catch (InvocationTargetException e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }


    @Override
    public void wipe(Class clazz){
        String name = clazz.getName();
        stringRedisTemplate.convertAndSend(RedisTopicConfig.CACHE_CONFIG_TOPIC,name);
    }

    // 加载缓存
    private synchronized <T> List<T> cache(String clazz, List<T> data){
        List<T> list = CACHE_CONFIG.get(clazz);
        if (list != null){
            return list;
        }
        CACHE_CONFIG.put(clazz, data);
        return CACHE_CONFIG.get(clazz);
    }


    // 清除相地
    public static void clearLocal(String clazz){
        CACHE_CONFIG.remove(clazz);
    }


}