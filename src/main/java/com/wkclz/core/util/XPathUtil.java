package com.wkclz.core.util;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.wkclz.core.exception.BizException;
import org.apache.commons.lang3.StringUtils;

import java.util.Set;

public class XPathUtil {

    public static String path(String objectStr, String xPpath){
        if (StringUtils.isBlank(objectStr)){
            return null;
        }
        if (StringUtils.isBlank(xPpath)){
            return null;
        }
        JSONObject jsonObject = JSONObject.parseObject(objectStr);
        Object result = pathValue(jsonObject, xPpath);
        return (result == null) ? null:result.toString();
    }


    private static Object pathValue(Object object, String xPpath){
        if (object == null || xPpath == null){
            return object;
        }

        boolean isNode = xPpath.contains("/");
        String path = isNode? (xPpath.substring(0, xPpath.indexOf("/"))):xPpath;
        String nexPath = isNode? xPpath.substring(xPpath.indexOf("/") +1):null;

        if (StringUtils.isBlank(path)){
            throw BizException.error("path {} contain empty element, please check!", xPpath);
        }

        // 【数组】
        if (path.contains("[") && path.contains("]")){
            // 数组, 分两步解析
            String p = path.substring(0, path.indexOf("["));
            String idx = path.substring(path.indexOf("[")+1, path.length() -1);
            Integer index = Integer.valueOf(idx);

            // 第一步提取出数组
            JSONObject tmp = (JSONObject) object;
            Object arrayObject = tmp.get(p);

            // 第二步，识别数组位置
            if (arrayObject instanceof JSONArray){
                JSONArray arr = (JSONArray)arrayObject;
                if (arr.size() <= index){
                    int maxIndex = arr.size() -1;
                    throw BizException.error("out of index in {}, max index is {}, like: {}[{}]", path, maxIndex, p, maxIndex);
                }
                Object o = arr.get(index);
                return pathValue(o, nexPath);
            } else {
                throw BizException.error("path {} is not array, is an object!", path);
            }
        }

        JSONObject jsonObject = (JSONObject) object;

        // 【对象】* 匹配
        if ("*".equals(path)){
            Set<String> keys = jsonObject.keySet();
            for (String key : keys) {
                Object o = jsonObject.get(key);
                Object value = pathValue(o, nexPath);
                if (value != null){
                    return value;
                }
            }
            return null;
        }


        // 【对象】唯一匹配
        Object o = jsonObject.get(path);
        return pathValue(o, nexPath);
    }



}
