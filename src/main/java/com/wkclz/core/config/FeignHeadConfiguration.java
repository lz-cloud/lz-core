package com.wkclz.core.config;


import feign.RequestInterceptor;
import org.jboss.logging.MDC;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;
import java.util.Set;


/**
 * 自定义的请求头处理类，处理服务发送时的请求头；
 * 将服务接收到的请求头中的uniqueId和token字段取出来，并设置到新的请求头里面去转发给下游服务
 * 比如A服务收到一个请求，请求头里面包含uniqueId和token字段，A处理时会使用Feign客户端调用B服务
 * 那么uniqueId和token这两个字段就会添加到请求头中一并发给B服务；
 *
 * @author wangkc4
 * @create 2019/09/15 14:59:25
 * @since 1.0.0
 */
@Configuration
public class FeignHeadConfiguration {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Bean
    public RequestInterceptor requestInterceptor() {
        return requestTemplate -> {
            Map<String, Object> map = MDC.getMap();
            Set<Map.Entry<String, Object>> entries = map.entrySet();
            for (Map.Entry<String, Object> entry : entries) {
                requestTemplate.header(entry.getKey(), entry.getValue().toString());
            }
        };
    }
}
