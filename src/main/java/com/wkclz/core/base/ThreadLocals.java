package com.wkclz.core.base;

import com.alibaba.fastjson.JSONObject;
import com.wkclz.core.exception.BizException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class ThreadLocals {

    private static final Logger logger = LoggerFactory.getLogger(ThreadLocals.class);
    private final static ThreadLocal<Map<String, Object>> THREAD_CONTEXT = new ThreadLocal<>();

    public static Object get(String key) {
        if (StringUtils.isBlank(key)){
            return null;
        }
        return getContext().get(key);
    }

    public static void set(String key, Object value) {
        if (StringUtils.isBlank(key)){
            throw BizException.error("key can not be empty when setting ThreadLocals!");
        }
        Map<String, Object> context = getContext();
        context.put(key, value);
        if (logger.isDebugEnabled()){
            boolean b = context.containsKey(key);
            if (b){
                logger.debug("ThreadLocal Overwritten {} as {}", key, JSONObject.toJSONString(value));
            } else {
                logger.debug("ThreadLocal Added {} as {}", key, JSONObject.toJSONString(value));
            }
        }
    }

    public static Object remove(String key) {
        return getContext().remove(key);
    }

    private static Map<String, Object> getContext(){
        return THREAD_CONTEXT.get();
    }

}
