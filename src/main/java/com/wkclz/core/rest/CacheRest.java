package com.wkclz.core.rest;

import com.wkclz.core.base.Result;
import com.wkclz.core.helper.AccessHelper;
import com.wkclz.core.helper.ApiDomainHelper;
import com.wkclz.core.helper.SystemConfigHelper;
import com.wkclz.core.helper.TenantDomainHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
public class CacheRest {
    private static final Logger logger = LoggerFactory.getLogger(CacheRest.class);

    /**
     * 取缓存,用于问题排查及缓存同步
     * @return
     */

    @GetMapping(Routes.CACHE_SYS_CONFIG)
    public Result cacheSysConfig(){
        logger.info("缓存获取： {}", SystemConfigHelper.class);
        Map<String, String> local = SystemConfigHelper.getLocal();
        return Result.data(local);
    }
    @GetMapping(Routes.CACHE_API_DOMAIN)
    public Result cacheApiDomain(){
        logger.info("缓存获取： {}", ApiDomainHelper.class);
        List<String> local = ApiDomainHelper.getLocal();
        return Result.data(local);
    }
    @GetMapping(Routes.CACHE_TENANT_DOMAIN)
    public Result cacheTenantDomain(){
        logger.info("缓存获取： {}", TenantDomainHelper.class);
        Map<String, Object> local = TenantDomainHelper.getLocal();
        return Result.data(local);
    }
    @GetMapping(Routes.CACHE_ACCESS_URI)
    public Result cacheAccessUri(){
        logger.info("缓存获取： {}", AccessHelper.class);
        List<String> local = AccessHelper.getLocal();
        return Result.data(local);
    }

}
