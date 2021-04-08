package com.wkclz.core.rest;

import com.wkclz.core.base.Result;
import com.wkclz.core.base.Sys;
import com.wkclz.core.helper.IpHelper;
import com.wkclz.core.pojo.dto.JvmInfo;
import com.wkclz.core.util.JvmUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Properties;
import java.util.concurrent.TimeUnit;

@RestController
public class Monitor {

    private final static String MONITOR_REDIS = "monitor_redis:";

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 测试 redis 读写，返回服务器时间
     * @return
     */
    @GetMapping(Routes.MONITOR_REDIS)
    public Result redis(){
        String serverIp = IpHelper.getServerIp();
        String key = MONITOR_REDIS + Sys.CURRENT_ENV + ":" + Sys.APPLICATION_GROUP + ":" + Sys.APPLICATION_NAME + ":" + serverIp;
        long timpstramp = System.currentTimeMillis();
        stringRedisTemplate.boundValueOps(key).set(timpstramp + "");
        stringRedisTemplate.expire(key, 1, TimeUnit.DAYS);
        String s = stringRedisTemplate.boundValueOps(key).get();
        Long aLong = Long.valueOf(s);
        return Result.data(aLong);
    }

    /**
     * 获取服务器IP
     * @return
     */
    @GetMapping(Routes.MONITOR_IP)
    public Result ip(){
        String serverIp = IpHelper.getServerIp();
        return Result.data(serverIp);
    }

    /**
     * 获取服务器属性
     * @return
     */
    @GetMapping(Routes.MONITOR_PROPERTIES)
    public Result properties(){
        Properties properties = System.getProperties();
        return Result.data(properties);
    }

    /**
     * 获取服务器属性
     * @return
     */
    @GetMapping(Routes.MONITOR_JVM)
    public Result jvm(){
        JvmInfo info = JvmUtil.getJvmStatus();
        return Result.data(info);
    }

}
