package com.wkclz.core.config;


import com.alibaba.fastjson.JSONObject;
import com.wkclz.core.base.Result;
import com.wkclz.core.base.Sys;
import com.wkclz.core.config.handler.AuthHandler;
import com.wkclz.core.constant.Queue;
import com.wkclz.core.helper.AccessHelper;
import com.wkclz.core.helper.LogTraceHelper;
import com.wkclz.core.pojo.entity.AccessLog;
import com.wkclz.core.pojo.entity.TraceInfo;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.StreamUtils;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;

@WebFilter(filterName = "gwFilter"  ,urlPatterns = "/*")
public class GwFilter implements Filter {

    @Value("${eureka.client.serviceUrl.defaultZone:null}")
    private String defaultZone;

    private static Logger logger = LoggerFactory.getLogger(GwFilter.class);

    @Autowired
    private AuthHandler authHandler;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private LogTraceHelper logTraceHelper;

    // 过滤器前缀
    private final static String[] URI_PREFIX = new String[]{
        "/gov/",
        "/gen/",
        "/sys/",
        "/cas/",
        "/pay/",
        "/order/",
        "/pms/",
        "/demo/",
    };

    @Override
    public void init(FilterConfig filterConfig) {
    }

    @Override
    public void destroy() {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        // 不走微服务才需要此过程
        if (!"null".equals(defaultZone)){
            chain.doFilter(request,response);
            return;
        }

        // 非微服务 【替代网关上的逻辑】

        HttpServletRequest httpRequest = (HttpServletRequest)request;
        HttpServletResponse httpResponse = (HttpServletResponse)response;


        // 记录日志
        AccessLog accessLog = AccessHelper.getAccessLog(httpRequest);
        if (accessLog!=null){
            String key = Queue.LOGGER_QUEUE_PREFIX + Sys.APPLICATION_GROUP;
            String jsonString = JSONObject.toJSONString(accessLog);
            //消息入队列
            logger.info("access: {}, UA: {}", accessLog.getRequestUri(), accessLog.getUserAgent());
            redisTemplate.opsForList().leftPush(key, jsonString);
        }


        // 权限
        MutableHttpServletRequest mutableRequest = new MutableHttpServletRequest(httpRequest);
        // 跟踪链
        TraceInfo traceInfo = logTraceHelper.checkTraceInfo(mutableRequest, httpResponse);
        String traceId = traceInfo.getTraceId();
        String spanId = traceInfo.getSpanId() + "";

        mutableRequest.putHeader(LogTraceHelper.TRACE_ID, traceId);
        // ctx.addZuulResponseHeader(LogTraceHelper.TRACE_ID, traceId);
        // mutableRequest.putHeader(LogTraceHelper.SPAN_ID, spanId);

        Result result = authHandler.preHandle(mutableRequest, httpResponse);
        logger.info("request {} status: {}", mutableRequest.getRequestURI(), result == null);

        // 全局 access 标识
        Object access = request.getAttribute("access");
        if (access != null && "true".equals(access)){
            mutableRequest.putHeader("access", "true");
        }

        if (result != null){
            try {
                InputStream in = request.getInputStream();
                String body = StreamUtils.copyToString(in, StandardCharsets.UTF_8);
                if (StringUtils.isNotBlank(body)){
                    logger.warn("Request interception, request body: {}", body);
                }
            } catch (IOException e) {
                logger.error(e.getMessage(), e);
            }
            Result.responseError(httpResponse, result);
            return;
        }


        String uri = mutableRequest.getRequestURI();
        for (String uriPrefix : URI_PREFIX) {
            if(uri.startsWith(uriPrefix)){
                uri = "/" + uri.substring(uriPrefix.length());
                mutableRequest.getRequestDispatcher(uri).forward(request,response);
                return;
            }
        }
        chain.doFilter(request,response);
        return;
    }


    final class MutableHttpServletRequest extends HttpServletRequestWrapper {
        private final Map<String, String> customHeaders;
        public MutableHttpServletRequest(HttpServletRequest request){
            super(request);
            this.customHeaders = new HashMap<>();
        }
        public void putHeader(String name, String value){
            this.customHeaders.put(name, value);
        }
        @Override
        public String getHeader(String name) {
            String headerValue = customHeaders.get(name);
            if (headerValue != null){
                return headerValue;
            }
            return ((HttpServletRequest) getRequest()).getHeader(name);
        }
        @Override
        public Enumeration<String> getHeaderNames() {
            if (customHeaders.isEmpty()) {
                return super.getHeaderNames();
            }
            Set<String> set = new HashSet<String>(customHeaders.keySet());
            // 添加自定义header
            Enumeration<String> e = ((HttpServletRequest) getRequest()).getHeaderNames();
            while (e.hasMoreElements()) {
                String n = e.nextElement();
                set.add(n);
            }
            return Collections.enumeration(set);
        }
    }
}

