package com.wkclz.core.config;


import com.wkclz.core.helper.TraceHelper;
import com.wkclz.core.pojo.entity.TraceInfo;
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
    private final static Logger logger = LoggerFactory.getLogger(FeignHeadConfiguration.class);

    @Bean
    public RequestInterceptor requestInterceptor() {
        return requestTemplate -> {
            Map<String, Object> map = MDC.getMap();
            Set<Map.Entry<String, Object>> entries = map.entrySet();
            for (Map.Entry<String, Object> entry : entries) {
                if ("Content-Type".equalsIgnoreCase(entry.getKey())){
                    continue;
                }
                if ("Content-Length".equalsIgnoreCase(entry.getKey())){
                    continue;
                }
                // user 不再放到 header 中,数据传输量较大,json 还会报错
                if ("user".equalsIgnoreCase(entry.getKey())){
                    continue;
                }
                if (TraceHelper.SERVICE_ID.equalsIgnoreCase(entry.getKey())){
                    continue;
                }
                if (TraceHelper.INSTANCE_ID.equalsIgnoreCase(entry.getKey())){
                    continue;
                }
                if (TraceHelper.UPSTREAM_SERVICE_ID.equalsIgnoreCase(entry.getKey())){
                    continue;
                }
                if (TraceHelper.UPSTREAM_INSTANCE_ID.equalsIgnoreCase(entry.getKey())){
                    continue;
                }
                requestTemplate.header(entry.getKey(), entry.getValue().toString());
            }

            // zuul, gateway, feign 请求开始时，附加 upstream 信息【当前位置】
            // HandlerInterceptor 请接收请求，获取 upstream 信息
            requestTemplate.header(TraceHelper.UPSTREAM_SERVICE_ID, TraceInfo.getServiceId());
            requestTemplate.header(TraceHelper.UPSTREAM_INSTANCE_ID, TraceInfo.getInstanceId());
        };
    }
}
