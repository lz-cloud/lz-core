package com.wkclz.core.helper;

import cn.hutool.core.date.DateUtil;
import com.wkclz.core.exception.BizException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * @author wangkc
 * @date 2020-04-05 13:22
 */
@Component
public class RedisRockHelper {

    private static final Logger logger = LoggerFactory.getLogger(RedisRockHelper.class);

    @Autowired(required = false)
    private RedisTemplate redisTemplate;


    /**
     * redis 锁，
     * @param key
     * @return true, 加锁成功，可以继续， false, 加锁失败，需要等待
     */
    public boolean lock(String key){
       return lock(key, 60);
    }


    /**
     * redis 锁，
     * @param key
     * @param second
     * @return true, 加锁成功，可以继续， false, 加锁失败，需要等待
     */
    public boolean lock(String key, Integer second){
        if(StringUtils.isBlank(key)){
            throw BizException.error("key can not be null");
        }
        if(second == null){
            throw BizException.error("second can not be null");
        }
        long currentTimeMillis = System.currentTimeMillis();
        boolean boo = redisTemplate.opsForValue().setIfAbsent(key, currentTimeMillis + "", second, TimeUnit.SECONDS);
        if (!boo){
            Object o = redisTemplate.opsForValue().get(key);
            if (o == null){
                logger.error("");
                throw BizException.error("found lock {}, but can not found value!", key);
            }
            Long aLong = Long.valueOf(o.toString());
            Date date = new Date(aLong);
            logger.warn("lock {} faild, it has rocked @ {}", key, DateUtil.format(date, "yyyy-M-dd HH:mm:ss"));
        };
        return boo;
    }


    /**
     * 解锁
     * @param key
     * @return
     */
    public boolean unlock(String key){
        if(StringUtils.isBlank(key)){
            throw BizException.error("key can not be null");
        }
        boolean boo = redisTemplate.delete(key);
        return boo;
    }



}
