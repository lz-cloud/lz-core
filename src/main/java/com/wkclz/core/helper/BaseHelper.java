package com.wkclz.core.helper;

import com.wkclz.core.base.Sys;
import com.wkclz.core.pojo.enums.EnvType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Description:
 * Created: wangkaicun @ 2018-03-07 下午10:17
 */
public class BaseHelper {


    private static final Logger logger = LoggerFactory.getLogger(BaseHelper.class);


    private static final Integer SESSION_LIVE_TIME_DEV = 24 * 60 * 60;
    private static final Integer SESSION_LIVE_TIME_SIT = 24 * 60 * 60;
    private static final Integer SESSION_LIVE_TIME_UAT = 1800;
    private static final Integer SESSION_LIVE_TIME_PROD = 1800;

    private static final Integer JAVA_CACHE_LIVE_TIME_DEV = 30;
    private static final Integer JAVA_CACHE_LIVE_TIME_SIT = 30;
    private static final Integer JAVA_CACHE_LIVE_TIME_UAT = 1800;
    private static final Integer JAVA_CACHE_LIVE_TIME_PROD = 1800;

    public static String getToken(HttpServletRequest req) {
        String token = null;
        Cookie[] cookies = req.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                String name = cookie.getName();
                if ("token".equals(name)) {
                    token = cookie.getValue();
                    break;
                }
            }
        }
        if (token == null) {
            token = req.getHeader("token");
        }
        if (token == null) {
            token = req.getParameter("token");
        }
        return token;
    }


    public static Map<String, String> getParamsFromRequest(HttpServletRequest req) {

        //获取支付宝POST过来反馈信息
        Map<String, String> params = new HashMap<>();
        Map<String, String[]> requestParams = req.getParameterMap();

        for (Iterator<String> iter = requestParams.keySet().iterator(); iter.hasNext(); ) {
            String name = iter.next();
            String[] values = requestParams.get(name);
            String valueStr = "";
            for (int i = 0; i < values.length; i++) {
                valueStr = (i == values.length - 1) ? valueStr + values[i] : valueStr + values[i] + ",";
            }
            //乱码解决，这段代码在出现乱码时使用
            // valueStr = new String(valueStr.getBytes("ISO-8859-1"), "utf-8");
            params.put(name, valueStr);
        }
        return params;
    }


    public static Integer getJavaCacheLiveTime() {
        Integer liveTime = 1800;
        if (EnvType.PROD == Sys.CURRENT_ENV) {
            liveTime = BaseHelper.JAVA_CACHE_LIVE_TIME_PROD;
        }
        if (EnvType.UAT == Sys.CURRENT_ENV) {
            liveTime = BaseHelper.JAVA_CACHE_LIVE_TIME_UAT;
        }
        if (EnvType.SIT == Sys.CURRENT_ENV) {
            liveTime = BaseHelper.JAVA_CACHE_LIVE_TIME_SIT;
        }
        if (EnvType.DEV == Sys.CURRENT_ENV) {
            liveTime = BaseHelper.JAVA_CACHE_LIVE_TIME_DEV;
        }
        return liveTime;
    }


    public static Integer getSessionLiveTime() {
        Integer liveTime = 1800;
        if (EnvType.PROD == Sys.CURRENT_ENV) {
            liveTime = BaseHelper.SESSION_LIVE_TIME_PROD;
        }
        if (EnvType.UAT == Sys.CURRENT_ENV) {
            liveTime = BaseHelper.SESSION_LIVE_TIME_UAT;
        }
        if (EnvType.SIT == Sys.CURRENT_ENV) {
            liveTime = BaseHelper.SESSION_LIVE_TIME_SIT;
        }
        if (EnvType.DEV == Sys.CURRENT_ENV) {
            liveTime = BaseHelper.SESSION_LIVE_TIME_DEV;
        }
        return liveTime;
    }


}
