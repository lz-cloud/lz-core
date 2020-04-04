package com.wkclz.core.util;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.wkclz.core.exception.BizException;
import org.apache.commons.lang3.StringUtils;

public class XPathUtil {

    public static String path(String objectStr, String xPpath){
        if (StringUtils.isBlank(objectStr)){
            return null;
        }
        if (StringUtils.isBlank(xPpath)){
            return null;
        }
        JSONObject jsonObject = JSONObject.parseObject(objectStr);
        Object result = jsonObject;

        String[] paths = xPpath.split("/");
        for (int i = 0; i < paths.length; i++) {

            // 已经没有 JSONObject 了之后，不再分析
            if (!(result instanceof JSONObject)){
                continue;
            }

            // 继续分析
            String path = paths[i];
            if (path.contains("[") && path.contains("]")){
                // 数组, 分两步解析
                String p = path.substring(0, path.indexOf("["));
                String idx = path.substring(path.indexOf("[")+1, path.length() -1);
                Integer index = Integer.valueOf(idx);

                // 第一步提取出数组
                JSONObject tmp = (JSONObject) result;
                result = tmp.get(p);

                // 第二步，识别数组位置
                if (result instanceof JSONArray){
                    JSONArray arr = (JSONArray)result;
                    if (arr.size() <= index){
                        int maxIndex = arr.size() -1;
                        throw BizException.error("out of index in {}, max index is {}, like: {}[{}]", path, maxIndex, p, maxIndex);
                    }
                    result = arr.get(index);
                }

            } else {
                // 对象
                JSONObject tmp = (JSONObject) result;
                result = tmp.get(path);
            }

        }

        return (result == null) ? null:result.toString();
    }

}
