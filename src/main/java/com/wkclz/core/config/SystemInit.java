package com.wkclz.core.config;

import cn.hutool.core.thread.ThreadUtil;
import com.wkclz.core.constant.ServiceIdConstant;
import com.wkclz.core.helper.cache.LzCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * 系统启动后执行一次
 */

@Component
public class SystemInit implements ApplicationRunner {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());


    @Autowired
    private LzCache lzCache;
    @Autowired
    private SystemConfig systemConfig;


    @Override
    public void run(ApplicationArguments args) {
        if (!systemConfig.isCloud() || !ServiceIdConstant.LZ_SYS.equalsIgnoreCase(systemConfig.getApplicationName())){
            logger.info("not cloud or monomer application mode, do not sync cache by {}", ServiceIdConstant.LZ_SYS);
            return;
        }

        ThreadUtil.execAsync(() -> {
            ThreadUtil.sleep(1, TimeUnit.SECONDS);
            // Sys.initEnv();
            lzCache.cache2Local();;
        },false);
        logger.info("run {} over", this.getClass());
    }

}
