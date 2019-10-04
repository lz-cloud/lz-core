package com.wkclz.core.helper;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@ConfigurationProperties(prefix = "spring.application")
public class AppHelper {

    private final static Logger logger = LoggerFactory.getLogger(AppHelper.class);

    // 应用组
    private String group = "CMS";

    // 应用名称
    private String name = "APP";

    public Map<String, String> getAppInfo(){

        logger.info("===================>  System startup init begin...");

        // 初始化信息，需要应用名做前缀
        String appGroup = group;
        if (StringUtils.isBlank(appGroup)){
            appGroup = name;
        }
        appGroup = appGroup.toUpperCase();
        appGroup = appGroup.replace("-", "_");

        Map<String, String> map = new HashMap<>();
        map.put("group", appGroup);
        map.put("name", name);

        logger.info("===================>  系统应用组 {}, 名称 {} ", appGroup, name);

        return map;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
