package com.wkclz.core.helper;

import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;


public class RequestHelper {

    /**
     * 获取请求 url
     * @return
     */
    public static String getRequestUrl(){
        HttpServletRequest request = getRequest();
        if (request == null){
            return "unknown";
        }
        return request.getRequestURL().toString();
    }



    /**
     * 获取当前请求
     * @return
     */
    public static HttpServletRequest getRequest(){
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if (requestAttributes == null){
            return null;
        }
        ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) requestAttributes;
        HttpServletRequest request = servletRequestAttributes.getRequest();
        return request;
    }


}
