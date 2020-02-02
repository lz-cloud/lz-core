package com.wkclz.core.rest;

import com.wkclz.core.base.annotation.Desc;
import com.wkclz.core.base.annotation.Routers;

/**
 * Description:
 * Created: wangkaicun @ 2017-10-19 上午12:46
 */
@Routers("core 包路由")
public interface Routes {

    /**
     * 一个接口组对应一个 Controller
     */

    @Desc("1. 下载API 信息")
    String APIS = "/apis";


    /**
     * 监控 monitor
     */
    @Desc("1. 监控-redis")
    String MONITOR_REDIS = "/monitor/redis";
    @Desc("2. 监控-服务器IP")
    String MONITOR_IP = "/monitor/ip";
    @Desc("3. 监控-服务器属性")
    String MONITOR_PROPERTIES = "/monitor/properties";



}
