package com.wkclz.core.config;


import feign.RequestInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;


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
            ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attrs != null) {
                HttpServletRequest request = attrs.getRequest();
                // 如果在Cookie内通过如下方式取
                Cookie[] cookies = request.getCookies();

                if (cookies != null && cookies.length > 0) {
                    for (Cookie cookie : cookies) {
                        requestTemplate.header(cookie.getName(), cookie.getValue());
                    }
                }

                // 如果放在header内通过如下方式取
                Enumeration<String> headerNames = request.getHeaderNames();
                if (headerNames != null) {
                    while (headerNames.hasMoreElements()) {
                        String name = headerNames.nextElement();
                        String value = request.getHeader(name);
                        requestTemplate.header(name, value);
                    }
                }
                /*  理论header 已经包含了这个值
                // traceId
                String mdcTraceId = MDC.get("traceId");
                if (mdcTraceId != null){
                    requestTemplate.header("traceId", mdcTraceId);
                }
                */
                // seq 要一直变化，所以要获取新值
                String mdcSeq = MDC.get("seq");
                if (mdcSeq != null){
                    requestTemplate.header("seq", mdcSeq);
                }

            }
        };
    }
}
