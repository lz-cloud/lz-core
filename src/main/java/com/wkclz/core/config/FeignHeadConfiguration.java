package com.wkclz.core.config;


import com.wkclz.core.helper.LogTraceHelper;
import feign.RequestInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;


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
            Map<String, String> map = MDC.getCopyOfContextMap();

            // 日志跟踪信息
            if (map.get(LogTraceHelper.ORIGIN_IP) != null){
                requestTemplate.header(LogTraceHelper.ORIGIN_IP, map.get(LogTraceHelper.ORIGIN_IP));
            }
            if (map.get(LogTraceHelper.UPSTREAM_IP) != null){
                requestTemplate.header(LogTraceHelper.UPSTREAM_IP, map.get(LogTraceHelper.UPSTREAM_IP));
            }
            if (map.get(LogTraceHelper.TRACE_ID) != null){
                requestTemplate.header(LogTraceHelper.TRACE_ID, map.get(LogTraceHelper.TRACE_ID));
            }
            if (map.get(LogTraceHelper.SPAN_ID) != null){
                requestTemplate.header(LogTraceHelper.SPAN_ID, map.get(LogTraceHelper.SPAN_ID));
            }

            if (map.get(LogTraceHelper.SPAN_ID) != null){
                requestTemplate.header(LogTraceHelper.SPAN_ID, map.get(LogTraceHelper.SPAN_ID));
            }

            // 用户跟踪信息
            if (map.get(LogTraceHelper.TENANT_ID) != null){
                requestTemplate.header(LogTraceHelper.TENANT_ID, map.get(LogTraceHelper.TENANT_ID));
            }
            if (map.get(LogTraceHelper.AUTH_ID) != null){
                requestTemplate.header(LogTraceHelper.AUTH_ID, map.get(LogTraceHelper.AUTH_ID));
            }
            if (map.get(LogTraceHelper.USER_ID) != null){
                requestTemplate.header(LogTraceHelper.USER_ID, map.get(LogTraceHelper.USER_ID));
            }
        };
    }
}
