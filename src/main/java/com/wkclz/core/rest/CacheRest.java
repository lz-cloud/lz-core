package com.wkclz.core.rest;

import com.wkclz.core.base.Result;
import com.wkclz.core.helper.AccessHelper;
import com.wkclz.core.helper.ApiDomainHelper;
import com.wkclz.core.helper.SystemConfigHelper;
import com.wkclz.core.helper.TenantDomainHelper;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
public class CacheRest {

    /**
     * 取缓存,用于问题排查及缓存同步
     * @return
     */


    @GetMapping(Routes.CACHE_SYS_CONFIG)
    public Result cacheSysConfig(){
        Map<String, String> local = SystemConfigHelper.getLocal();
        return Result.data(local);
    }
    @GetMapping(Routes.CACHE_API_DOMAIN)
    public Result cacheApiDomain(){
        List<String> local = ApiDomainHelper.getLocal();
        return Result.data(local);
    }
    @GetMapping(Routes.CACHE_TENANT_DOMAIN)
    public Result cacheTenantDomain(){
        Map<String, Object> local = TenantDomainHelper.getLocal();
        return Result.data(local);
    }
    @GetMapping(Routes.CACHE_ACCESS_URI)
    public Result cacheAccessUri(){
        List<String> local = AccessHelper.getLocal();
        return Result.data(local);
    }

}
