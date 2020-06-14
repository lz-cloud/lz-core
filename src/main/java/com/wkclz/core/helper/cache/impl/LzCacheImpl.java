package com.wkclz.core.helper.cache.impl;

import com.alibaba.fastjson.JSONObject;
import com.wkclz.core.base.Sys;
import com.wkclz.core.constant.Queue;
import com.wkclz.core.constant.ServiceIdConstant;
import com.wkclz.core.exception.BizException;
import com.wkclz.core.helper.*;
import com.wkclz.core.helper.cache.LzCache;
import com.wkclz.core.helper.redis.topic.RedisTopicConfig;
import com.wkclz.core.rest.Routes;
import com.wkclz.core.util.BeanUtil;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class LzCacheImpl implements LzCache {

    private static final Logger logger = LoggerFactory.getLogger(LzCacheImpl.class);
    private static final Map<String, List> CACHE_CONFIG = new HashMap<>();

    @Autowired(required = false)
    private DiscoveryClient client;
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



    @Override
    public void cache2Local(){
        logger.info("正在尝试从远程加载缓存至本地...");
        List<ServiceInstance> instances = client.getInstances(ServiceIdConstant.LZ_SYS);
        if (CollectionUtils.isEmpty(instances)){
            logger.warn("have no {} online, please check, systen will be init again autoly if {} is start!", ServiceIdConstant.LZ_SYS, ServiceIdConstant.LZ_SYS);
            return;
        }

        ServiceInstance serviceInstance = null;
        for (ServiceInstance instance : instances) {
            // 是否在同一网段
            String host = instance.getHost();
            String serverIp = IpHelper.getServerIp();
            host = host.substring(0, host.lastIndexOf("."));
            host = host.substring(0, host.lastIndexOf("."));
            if (serverIp.contains(host)){
                serviceInstance = instance;
                break;
            }
        }
        if (serviceInstance == null){
            logger.warn("{} 服务可能不是同一网段，将使用消息通知更新", instances.get(0).getInstanceId());
            String key = Queue.LOGGER_QUEUE_PREFIX + Sys.APPLICATION_GROUP;
            stringRedisTemplate.opsForList().leftPush(key, System.currentTimeMillis()+"");
            return;
        }

        logger.info("正在从远程服务 {} 拉取缓存...", serviceInstance.getInstanceId());

        // 接口方式更新
        URI uriObj = serviceInstance.getUri();
        String uri = uriObj.toASCIIString();

        Map cacheSysConfig = request(uri + Routes.CACHE_SYS_CONFIG, Map.class);
        List cacheAccessUri = request(uri + Routes.CACHE_ACCESS_URI, List.class);
        List cacheApiDomain = request(uri + Routes.CACHE_API_DOMAIN, List.class);
        Map cacheTenantDomain = request(uri + Routes.CACHE_TENANT_DOMAIN, Map.class);

        SystemConfigHelper.setLocal(cacheSysConfig);
        AccessHelper.setLocal(cacheAccessUri);
        ApiDomainHelper.setLocal(cacheApiDomain);
        TenantDomainHelper.setLocal(cacheTenantDomain);
    }

    private static <T> T request(String url, Class<T> clazz){
        RestTemplate restTemplate = RestTemplateHelper.getRestTemplate();
        JSONObject jsonObject = null;
        try {
            jsonObject = restTemplate.getForObject(url, JSONObject.class);
        } catch (Exception e) {
            logger.warn("can not request: {}, with error: {}", url, e.getMessage());
            throw e;
        }
        Object code = jsonObject.get("code");
        Object msg = jsonObject.get("msg");
        Object data = jsonObject.get("data");
        if (code == null || !"1".equals(code.toString())){
            throw BizException.error("请求异常: {}", msg);
        }

        if (data == null){
            throw BizException.error("请求正常但无数据返回: {}", jsonObject);
        }

        T object = JSONObject.parseObject(JSONObject.toJSONString(data), clazz);
        return object;
    }

}