package com.wkclz.core.util;

import com.wkclz.core.pojo.entity.RestInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


public class RestUtil {

    private static final Logger logger = LoggerFactory.getLogger(ControllerUtil.class);

    /**
     * 读取 controller
     * @return
     */
    public static List<RestInfo> getMapping() {
        List<RestInfo> rests = new ArrayList<>();

        // 获取二级域下的所有 Class
        String clazzName = RestUtil.class.getName();
        int index = clazzName.indexOf(".", clazzName.indexOf(".") + 1);
        String packagePath = clazzName.substring(0, index);
        logger.info("package {} mappings...", packagePath);

        // 筛选出有 Controller 标识的类
        Set<Class<?>> classes = ClassUtil.getClasses(packagePath);
        List<Class> clazzList = classes.stream().filter(clazz -> clazz.isAnnotationPresent(RestController.class) || clazz.isAnnotationPresent(Controller.class)).collect(Collectors.toList());
        for (Class clazz : clazzList) {
            // 大 Rest 上的 RequestMapping
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

            // 获取类上的方法
            Method[] methods = clazz.getDeclaredMethods();
            for (Method method : methods) {
                Annotation[] annotations = method.getAnnotations();
                String uri = null;
                RequestMethod requestMethod = null;
                for (Annotation annotation : annotations) {
                    if (GetMapping.class == annotation.annotationType() || RequestMapping.class == annotation.annotationType()) {
                        GetMapping get = (GetMapping) annotation;
                        requestMethod = RequestMethod.GET;
                        String[] values = get.value();
                        uri = values.length == 0 ? null : values[0];
                    }
                    if (PostMapping.class == annotation.annotationType()) {
                        PostMapping post = (PostMapping) annotation;
                        requestMethod = RequestMethod.POST;
                        String[] values = post.value();
                        uri = values.length == 0 ? null : values[0];
                    }
                    if (DeleteMapping.class == annotation.annotationType()) {
                        DeleteMapping request = (DeleteMapping) annotation;
                        requestMethod = RequestMethod.DELETE;
                        String[] values = request.value();
                        uri = values.length == 0 ? null : values[0];
                    }
                    if (PutMapping.class == annotation.annotationType()) {
                        PutMapping request = (PutMapping) annotation;
                        requestMethod = RequestMethod.PUT;
                        String[] values = request.value();
                        uri = values.length == 0 ? null : values[0];
                    }
                    // 不是rest 接口的注解
                    if (null != uri || requestMethod == null) {
                        continue;
                    }
                }
                // 确定是 rest 接口，提取信息
                if (requestMethod != null) {
                    RestInfo restInfo = new RestInfo();
                    restInfo.setPrefix(prefix);
                    restInfo.setRequestMethod(requestMethod);
                    restInfo.setUri(uri);
                    restInfo.setClazz(clazz.getName() + "." + method.getName());

                    uri = prefix + uri;
                    // 方法名
                    String restName = uri.substring(1);
                    restName = restName.replaceAll("/", "_");
                    restName = StringUtil.underlineToCamel(restName);
                    restInfo.setRestName(restName);
                    rests.add(restInfo);
                }
            }
        }
        return rests;
    }

}

