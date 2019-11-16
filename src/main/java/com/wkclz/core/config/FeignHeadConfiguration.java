package com.wkclz.core.config;


import com.wkclz.core.base.ThreadLocals;
import feign.RequestInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;
import java.util.HashMap;
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
                Map<String, String> trace = new HashMap<>();
                Enumeration<String> headerNames = request.getHeaderNames();
                if (headerNames != null) {
                    while (headerNames.hasMoreElements()) {
                        String name = headerNames.nextElement();
                        String value = request.getHeader(name);
                        requestTemplate.header(name, value);
                        trace.put(name, value);
                    }
                }

                if (trace.get("traceId") == null){
                    Object traceId = ThreadLocals.get("traceId");
                    if (traceId != null){
                        requestTemplate.header("traceId", traceId.toString());
                    }
                }
                if (trace.get("seq") == null){
                    Object seq = ThreadLocals.get("seq");
                    if (seq != null){
                        requestTemplate.header("seq", seq.toString());
                    }
                }
                if (trace.get("user") == null){
                    Object user = ThreadLocals.get("user");
                    if (user != null){
                        requestTemplate.header("user", user.toString());
                    }
                }
                if (trace.get("orgId") == null){
                    Object orgId = ThreadLocals.get("orgId");
                    if (orgId != null){
                        requestTemplate.header("orgId", orgId.toString());
                    }
                }
            }
        };
    }
}
