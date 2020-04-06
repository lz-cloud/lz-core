package com.wkclz.core.util;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.wkclz.core.exception.BizException;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class XPathUtil {

    /**
     * xPpath 找 json
     * @param objectStr
     * @param xPpath
     * @return
     */
    public static String path(String objectStr, String xPpath){
        if (StringUtils.isBlank(objectStr)){
            return null;
        }
        JSONObject jsonObject = JSONObject.parseObject(objectStr);
        return path(jsonObject, xPpath);
    }
    public static String path(JSONObject jsonObject, String xPpath){
        if (jsonObject == null){
            return null;
        }
        if (StringUtils.isBlank(xPpath)){
            return null;
        }
        Object result = pathValue(jsonObject, xPpath);
        return (result == null) ? null:result.toString();
    }




    /**
     * xPpath 找 json, 递归
     * @param object
     * @param xPpath
     * @return
     */
    private static Object pathValue(Object object, String xPpath){
        if (object == null || xPpath == null){
            return object;
        }
        if (xPpath.startsWith("/")){
            xPpath = xPpath.substring(1);
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

    /**
     * 从 JSONObject 内查找 xPath
     * @param objectStr
     * @return
     */
    public static Map<String, String> xPathDiscovery(String objectStr){
        return xPathDiscovery(objectStr, null, null);
    }
    public static Map<String, String> xPathDiscovery(JSONObject jsonObject){
        return xPathDiscovery(jsonObject, null, null);
    }
    public static Map<String, String> xPathDiscovery(String objectStr, String key, String value){
        if (StringUtils.isBlank(objectStr)){
            return null;
        }
        JSONObject jsonObject = JSONObject.parseObject(objectStr);
        return xPathDiscovery(jsonObject, key, value);
    }
    public static Map<String, String> xPathDiscovery(JSONObject jsonObject, String key, String value){
        if (jsonObject == null){
            return null;
        }
        // 整理成 map
        Map<String, String> json2Map = json2Map(jsonObject, null, null);

        if (StringUtils.isNotBlank(key)){
            json2Map = json2Map.entrySet().stream().filter(entry -> entry.getKey().contains(key)).collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue()));
        }
        if (StringUtils.isNotBlank(value)){
            json2Map = json2Map.entrySet().stream().filter(entry -> entry.getValue().contains(value)).collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue()));
        }
        // 值匹配
        return json2Map;
    }

    /**
     * 递归还原 xpath
     * @param object
     * @param basePath
     * @param values
     * @return
     */
    private static Map<String, String> json2Map(Object object, String basePath, Map<String, String> values){
        if (object == null){
            values.put(basePath, "");
            return values;
        }
        if (values == null){
            values = new HashMap<>();
        }
        if (basePath == null){
            basePath = "";
        }

        // 找到 leaf 了, 没有下一级
        if (!(object instanceof JSONObject)){
            values.put(basePath, object.toString());
            return values;
        }

        JSONObject jsonObject = (JSONObject) object;

        Set<String> keySet = jsonObject.keySet();
        for (String key : keySet) {
            String path = basePath + "/" + key;

            // 空，找到尽头
            Object o = jsonObject.get(key);

            // 有下一级，还是数组
            if (o != null && (o instanceof JSONArray)){
                JSONArray jsonArray = (JSONArray) o;
                for (int i = 0; i < jsonArray.size(); i++) {
                    json2Map(jsonArray.get(i), path + "[" + i + "]" , values);
                }
                continue;
            }

            // 有下一级，是对象, 或没下一级
            json2Map(o, path, values);
        }
        return values;
    }

    /*
    public static void main(String[] args) {
        String url = "http://127.0.0.1:8061/actuator/configprops";
        HttpRequest get = HttpUtil.createGet(url);
        HttpResponse execute = get.execute();
        String body = execute.body();
        Map<String, String> xx = xPathDiscovery(body, "mysql");
       System.out.println(xx);
    }
    */



}
