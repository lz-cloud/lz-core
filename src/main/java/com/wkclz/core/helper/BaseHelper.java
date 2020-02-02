package com.wkclz.core.helper;

import com.wkclz.core.base.BaseModel;
import com.wkclz.core.base.Result;
import com.wkclz.core.base.Sys;
import com.wkclz.core.pojo.enums.EnvType;
import com.wkclz.core.util.MapUtil;
import com.wkclz.core.util.ResultSetMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

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


    public static List<Long> getIdsFromBaseModel(BaseModel model) {
        List<Long> ids = model.getIds();
        if (model.getIds() == null) {
            ids = new ArrayList<>();
        }
        if (model.getId() != null) {
            ids.add(model.getId());
        }
        model.setIds(ids);
        return ids;
    }

    public static Result removeCheck(BaseModel model) {
        Result result = new Result();
        List<Long> ids = getIdsFromBaseModel(model);
        if (ids.isEmpty()) {
            result.setError("id or ids can not be null at the same time");
        }
        return result;
    }

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


    /**
     * 从 jdbc 查询
     *
     * @param conn
     * @param sql
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T> List<T> jdbcExecutor(Connection conn, String sql, Class<T> clazz) {
        Statement statement = null;
        try {
            statement = conn.createStatement();
            ResultSet results = statement.executeQuery(sql);
            List<Map> maps = ResultSetMapper.toMapList(results);
            List<T> list = MapUtil.map2ObjList(maps, clazz);
            return list;
        } catch (SQLException e) {
            logger.error(e.getMessage(), e);
        } finally {
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException e) {
                    logger.error(e.getMessage(), e);
                }
            }
        }
        return null;
    }

    public static List<Map> jdbcExecutor(Connection conn, String sql) {
        Statement statement = null;
        try {
            statement = conn.createStatement();
            ResultSet results = statement.executeQuery(sql);
            List<Map> maps = ResultSetMapper.toMapList(results);
            return maps;
        } catch (SQLException e) {
            logger.error(e.getMessage(), e);
        } finally {
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException e) {
                    logger.error(e.getMessage(), e);
                }
            }
        }
        return null;
    }


    /**
     * 注入风险检测。默认只作用在 orderBy 上。不能用于其他地方的注入检测
     * 注入风险：
     * 1、orderBy 中使用 ${}。可用此方法进行检测，不可使用其他字段传入
     * 2、MBG 的 noValue，singleValue，betweenValue，listValue 注入风险：Example 的 Criteria 产生，无注入风险
     * 3、MBG 的 like 注入风险：like 前的由 Example 控制，后的为 #{}, 无注入风险
     * 4、Custom 实现的 like 强制使用 AND column like concat("%",#{value},"%")
     *
     * @param str
     * @return
     */
    private static boolean sqlInj(String str) {
        str = str.toLowerCase();
        String injStr = "'|and|exec|insert|select|delete|update|count|*|%|chr|mid|master|truncate|char|declare|;| or |-|+";
        String[] injStra = injStr.split("\\|");
        for (int i = 0; i < injStra.length; i++) {
            String is = injStra[i];
            if (str.indexOf(is) >= 0) {
                return true;
            }
        }
        return false;
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
