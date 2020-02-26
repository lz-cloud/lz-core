package com.wkclz.core.util;

import com.wkclz.core.base.annotation.Desc;
import com.wkclz.core.base.annotation.Routers;
import com.wkclz.core.pojo.entity.RestInfo;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;


public class RestUtil {

    private static final Logger logger = LoggerFactory.getLogger(RestUtil.class);

    /**
     * 读取 controller
     * @return
     */
    @Desc("使用此方法，不要在Rest 类的上方加 RequestMapping")
    public static List<RestInfo> getMapping() {
        List<RestInfo> rests = new ArrayList<>();

        // 获取二级域下的所有 Class
        String clazzName = RestUtil.class.getName();
        int index = clazzName.indexOf(".", clazzName.indexOf(".") + 1);
        String packagePath = clazzName.substring(0, index);
        logger.info("package {} mappings...", packagePath);

        // 筛选出有 Controller 标识的类
        Set<Class<?>> classes = ClassUtil.getClasses(packagePath);
        // Rest 服务类
        List<Class> restClassList = classes.stream().filter(clazz -> clazz.isAnnotationPresent(RestController.class) || clazz.isAnnotationPresent(Controller.class)).collect(Collectors.toList());
        for (Class clazz : restClassList) {
            // 大 Rest 上的 RequestMapping
            /* 规范不允许这么写，不然分析很容易出错
            String prefix = "";
            boolean hasPreFix = clazz.isAnnotationPresent(RequestMapping.class);
            if (hasPreFix) {
                Annotation annotation = clazz.getAnnotation(RequestMapping.class);
                RequestMapping request = (RequestMapping) annotation;
                String[] values = request.value();
                if (values.length > 0) {
                    prefix = values[0];
                }
            }
            */


            // 获取类上的方法
            Method[] methods = clazz.getDeclaredMethods();
            for (Method method : methods) {
                Annotation[] annotations = method.getAnnotations();
                String uri = null;
                String desc = null;
                RequestMethod requestMethod = null;
                for (Annotation annotation : annotations) {
                    if (GetMapping.class == annotation.annotationType() ) {
                        GetMapping get = (GetMapping) annotation;
                        requestMethod = RequestMethod.GET;
                        String[] values = get.value();
                        uri = values.length == 0 ? null : values[0];
                        continue;
                    }
                    if (RequestMapping.class == annotation.annotationType()) {
                        RequestMapping get = (RequestMapping) annotation;
                        requestMethod = RequestMethod.GET;
                        String[] values = get.value();
                        uri = values.length == 0 ? null : values[0];
                        continue;
                    }
                    if (PostMapping.class == annotation.annotationType()) {
                        PostMapping post = (PostMapping) annotation;
                        requestMethod = RequestMethod.POST;
                        String[] values = post.value();
                        uri = values.length == 0 ? null : values[0];
                        continue;
                    }
                    if (DeleteMapping.class == annotation.annotationType()) {
                        DeleteMapping request = (DeleteMapping) annotation;
                        requestMethod = RequestMethod.DELETE;
                        String[] values = request.value();
                        uri = values.length == 0 ? null : values[0];
                        continue;
                    }
                    if (PutMapping.class == annotation.annotationType()) {
                        PutMapping request = (PutMapping) annotation;
                        requestMethod = RequestMethod.PUT;
                        String[] values = request.value();
                        uri = values.length == 0 ? null : values[0];
                        continue;
                    }

                    // 中文含义
                    if (Desc.class == annotation.annotationType()) {
                        Desc descAnnto = (Desc) annotation;
                        desc = descAnnto.value();
                        continue;
                    }

                    // 不是rest 接口的注解
                    if (null != uri || requestMethod == null) {
                        continue;
                    }
                }
                // 确定是 rest 接口，提取信息
                if (requestMethod != null) {
                    RestInfo restInfo = new RestInfo();
                    restInfo.setRequestMethod(requestMethod.name());
                    restInfo.setUri(uri);
                    restInfo.setFunctionPath(clazz.getName() + "." + method.getName());
                    restInfo.setRestDesc(desc);

                    // 方法名
                    String restName = uri.substring(1);
                    restName = restName.replaceAll("/", "_");
                    restName = StringUtil.underlineToCamel(restName);
                    restInfo.setRestName(restName);
                    rests.add(restInfo);
                }
            }
        }

        // Routers 类
        List<Class> routersClassList = classes.stream().filter(clazz -> clazz.isAnnotationPresent(Routers.class)).collect(Collectors.toList());
        Map<String, String> uriDescs = new HashMap<>();
        for (Class routerClazz : routersClassList) {
            Field[] fields = routerClazz.getDeclaredFields();
            try {
                for (Field field : fields) {
                    String val = new String();
                    Object o = field.get(val);
                    if (o == null) {
                        continue;
                    }
                    String value = o.toString();
                    Desc desc = field.getAnnotation(Desc.class);
                    if (desc != null) {
                        String annoDesc = desc.value();
                        uriDescs.put(value, annoDesc);
                    }
                }
            } catch (IllegalAccessException e) {
                logger.error(e.getMessage(), e);
            }
        }
        // 映射新解释
        for (RestInfo rest : rests) {
            String desc = uriDescs.get(rest.getUri());
            if (StringUtils.isNotBlank(desc)){
                rest.setRestDesc(desc);
            }
        }

        return rests;
    }

}

