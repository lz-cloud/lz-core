package com.wkclz.core.config;

import cn.hutool.core.thread.ThreadUtil;
import com.wkclz.core.base.Sys;
import com.wkclz.core.constant.ServiceIdConstant;
import com.wkclz.core.helper.cache.LzCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * 系统启动后执行一次
 */

@Component
@Configuration
public class SystemInit implements ApplicationRunner {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());


    @Autowired
    private LzCache lzCache;
    @Value("${spring.application.name}")
    private String applicationName;


    @Override
    public void run(ApplicationArguments args) {
        if (ServiceIdConstant.LZ_SYS.equalsIgnoreCase(applicationName)){
            logger.info("{} 不需要使用此缓存", ServiceIdConstant.LZ_SYS);
            return;
        }

        ThreadUtil.execAsync(() -> {
            ThreadUtil.sleep(1, TimeUnit.SECONDS);
            lzCache.cache2Local();;
        },false);

    }

}
