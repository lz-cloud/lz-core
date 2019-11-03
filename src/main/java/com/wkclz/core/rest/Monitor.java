package com.wkclz.core.rest;

import com.wkclz.core.base.Result;
import com.wkclz.core.base.Sys;
import com.wkclz.core.helper.IpHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Properties;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/monitor")
public class Monitor {

    private final static String MONITOR_REDIS = "monitor_redis:";

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 测试 redis 读写，返回服务器时间
     * @return
     */
    @GetMapping("/redis")
    public Result redis(){
        String serverIP = IpHelper.getServerIP();
        String key = MONITOR_REDIS + Sys.CURRENT_ENV + ":" + Sys.APPLICATION_GROUP + ":" + Sys.APPLICATION_NAME + ":" + serverIP;
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
    @GetMapping("/ip")
    public Result ip(){
        String serverIP = IpHelper.getServerIP();
        return Result.data(serverIP);
    }

    /**
     * 获取服务器属性
     * @return
     */
    @GetMapping("/properties")
    public Result properties(){
        Properties properties = System.getProperties();
        return Result.data(properties);
    }






}
