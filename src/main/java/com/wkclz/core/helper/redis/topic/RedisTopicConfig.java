package com.wkclz.core.helper.redis.topic;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;


// 消息生产：stringRedisTemplate.convertAndSend("string-topic", msg);


@Configuration
public class RedisTopicConfig {

    @Autowired
    private RedisConnectionFactory redisConnectionFactory;

    @Bean
    public ConsumerRedisListener consumerRedis() {
        return new ConsumerRedisListener();
    }

    @Bean
    public ChannelTopic topic() {
        return new ChannelTopic("string-topic");
    }

    @Bean
    public RedisMessageListenerContainer redisMessageListenerContainer() {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(redisConnectionFactory);
        container.addMessageListener(consumerRedis(), topic());
        return container;
    }

}
