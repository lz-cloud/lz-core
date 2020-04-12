package com.wkclz.core.helper.redis.topic;


import com.wkclz.core.helper.cache.impl.LzCacheImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.StringRedisTemplate;

public class ConsumerRedisListener implements MessageListener {

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
        if (value != null){
            System.out.println("consumer message: " + value);
            LzCacheImpl.clearLocal(value.toString());
        }
    }

}
