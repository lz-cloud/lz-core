package com.wkclz.core.helper.redis.topic;


import com.alibaba.fastjson.JSONObject;
import com.wkclz.core.exception.BizException;
import com.wkclz.core.helper.*;
import com.wkclz.core.helper.redis.bean.RedisMsgBody;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.StringRedisTemplate;

public class ConsumerRedisListener implements MessageListener {

    private static final Logger logger = LoggerFactory.getLogger(ConsumerRedisListener.class);

    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Override
    public void onMessage(Message message, byte[] pattern) {
        doBusiness(message);
    }

    /**
     * 打印 message body 内容
     *
     * @param message
     */
    public void doBusiness(Message message) {
        Object value = stringRedisTemplate.getValueSerializer().deserialize(message.getBody());
        if (value == null){
            throw BizException.error("topic body is null: {}", message);
        }
        logger.info("consumer message: {}", value);
        
        String redisMsgBodyStr = value.toString();
        RedisMsgBody redisMsgBody = JSONObject.parseObject(redisMsgBodyStr, RedisMsgBody.class);

        String tag = redisMsgBody.getTag();
        Object msg = redisMsgBody.getMsg();


        boolean result = false;
        if (AccessHelper.class.getName().equals(tag)){
            result = AccessHelper.setLocal(msg);
        }
        if (ApiDomainHelper.class.getName().equals(tag)){
           result = ApiDomainHelper.setLocal(msg);
        }
        if (SystemConfigHelper.class.getName().equals(tag)){
            result = SystemConfigHelper.setLocal(msg);
        }
        if (TenantDomainHelper.class.getName().equals(tag)){
            result = TenantDomainHelper.setLocal(msg);
        }
        if (DictHelper.class.getName().equals(tag)){
            result = DictHelper.setLocal(msg);
        }

        if (!result){
            logger.error("no progress consume this msg {}", tag);
        }
        logger.info("consumer {} message finish", tag);
        // LzCacheImpl.clearLocal(value.toString());
    }

}
