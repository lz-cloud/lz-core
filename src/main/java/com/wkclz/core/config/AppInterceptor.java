package com.wkclz.core.config;

import com.wkclz.core.config.handler.LogTraceHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Description:
 * Created: wangkaicun @ 2017-10-18 下午11:45
 */

@Configuration
public class AppInterceptor implements WebMvcConfigurer {

    @Autowired
    private LogTraceHandler logTraceHandler;


    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(logTraceHandler).addPathPatterns("/**");
    }

}
