package com.wkclz.core.base;

import com.wkclz.core.helper.AppHelper;
import com.wkclz.core.pojo.enums.EnvType;
import com.wkclz.core.util.DateUtil;
import org.apache.commons.lang3.EnumUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;

import java.util.Map;

/**
 * Description:
 * Created: wangkaicun @ 2017-10-18 下午10:17
 */
public class Sys {

    private static final Logger logger = LoggerFactory.getLogger(Sys.class);

    /**
     * 系统分隔符
     */
    public static final String LINE_SEPARATOR = System.getProperty("line.separator");


    /**
     * default DEV
     * 当前启动的系统环境【初始为 DEV】
     */
    public static EnvType CURRENT_ENV = EnvType.DEV;

    /**
     * springboot 上下文
     */
    private static ApplicationContext applicationContext;

    /**
     * Application GROUP
     * 系统启动后会修改
     */
    public static String APPLICATION_GROUP = "CMS";

    /**
     * Application Name
     * 系统启动后会修改
     */
    public static String APPLICATION_NAME = "APP";

    /**
     * default now, it will be changed by main class
     * 系统启动时间
     */
    public static Long STARTUP_DATE = System.currentTimeMillis();

    /**
     * system start up success confirm
     * 系统启动确认
     */
    public static boolean SYSTEM_START_UP_CONFIRM = false;

    /**
     * system enum check confirm
     * 系统枚举检查
     */
    public static boolean SYSTEM_ENUM_CHECT_CONFIRM = false;


    public static void setEnv(ApplicationContext applicationContext) {
        setApplicationContext(applicationContext);

        Environment env = applicationContext.getEnvironment();
        String[] activeProfiles = env.getActiveProfiles();

        for (String profile : activeProfiles) {
            profile = profile.toUpperCase();
            EnvType envType = EnvType.DEV;

            if (EnumUtils.isValidEnum(EnvType.class, profile)) {
                envType = EnvType.valueOf(profile);
            } else {
                if (profile.contains(EnvType.PROD.toString())) {
                    envType = EnvType.PROD;
                }
                if (profile.contains(EnvType.UAT.toString())) {
                    envType = EnvType.UAT;
                }
                if (profile.contains(EnvType.SIT.toString())) {
                    envType = EnvType.PROD;
                }
                if (profile.contains(EnvType.DEV.toString())) {
                    envType = EnvType.DEV;
                }
            }
            CURRENT_ENV = envType;
            logger.info("===================>  System Env is changed to {}", CURRENT_ENV);
        }

        // set startupDate for the whole system
        Long startupDate = applicationContext.getStartupDate();

        AppHelper bean = applicationContext.getBean(AppHelper.class);
        Map<String, String> appInfo = bean.getAppInfo();

        Sys.APPLICATION_GROUP = appInfo.get("group");
        Sys.APPLICATION_NAME = appInfo.get("name");

        Sys.STARTUP_DATE = startupDate;
        logger.info("===================>  System is start up as {} @ {}", CURRENT_ENV, DateUtil.getYyyyMmDdHhMmSs(startupDate));
    }


    private static void setApplicationContext(ApplicationContext context) {
        applicationContext = context;
    }

    public static <T> T getBean(Class<T> clazz) {
        if (applicationContext == null){
            return null;
        }
        return applicationContext.getBean(clazz);
    }


}
