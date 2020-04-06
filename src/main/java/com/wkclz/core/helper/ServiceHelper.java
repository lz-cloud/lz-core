package com.wkclz.core.helper;


import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import com.wkclz.core.base.Result;
import com.wkclz.core.base.Sys;
import com.wkclz.core.exception.BizException;
import com.wkclz.core.util.XPathUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 服务调用工具
 */

@Component
public class ServiceHelper {

    @Autowired(required = false)
    private DiscoveryClient client;

    public Result excuteGet(String uri){
        if (StringUtils.isBlank(uri)){
            throw BizException.error("request uri can not be null");
        }
        String applicationName = Sys.APPLICATION_NAME;
        List<ServiceInstance> instances = client.getInstances(applicationName);
        if (CollectionUtils.isEmpty(instances)){
            throw BizException.error("no instance exist, serviceId is {}", applicationName);
        }
        int count = 0;
        for (ServiceInstance instance : instances) {
            String url = "http://"+ instance.getHost() + ":" + instance.getPort() + uri;
            HttpRequest request = HttpUtil.createGet(url);
            request.setReadTimeout(3000);
            HttpResponse execute = request.execute();
            String body = execute.body();
            String code = XPathUtil.path(body, "code");
            if ("1".equals(code)){
                count ++;
            }
        }

        if (count == instances.size()){
            return Result.data( count + "个实例调用成功");
        }
        return Result.error( instances.size()  + " 个实例在线，但只有 " + count + " 个实例执行成功！" );
    }

}
