package com.wkclz.core.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

/**
 * Description:
 * Created: wangkaicun @ 2018-01-20 下午11:20
 */
public class BeanUtil {

    private final static Logger logger = LoggerFactory.getLogger(BeanUtil.class);
    private final static Map<String, List<PropertyDescriptor>> PROPERTY_DESCRIPTORS = new HashMap<>();

    /**
     * remove the blank string in the  Object
     *
     * @return
     */
    public static <T> T removeBlank(T obj) {
        if (obj == null) {
            return null;
        }
        try {
            List<PropertyDescriptor> propertyDescriptors = getPropertyDescriptors(obj.getClass());
            assert propertyDescriptors != null;
            for (PropertyDescriptor property : propertyDescriptors) {
                Method getter = property.getReadMethod();
                Object value = getter.invoke(obj);
                if (value != null && value.toString().trim().length() == 0) {
                    Method setter = property.getWriteMethod();
                    setter.invoke(obj, new Object[]{null});
                }
            }
        } catch (IllegalAccessException | InvocationTargetException e) {
            logger.error(e.getMessage(), e);
        }
        return obj;
    }


    // 获取对象中有值的方法
    public static <T> List<Method> getValuedList(T param){
        List<PropertyDescriptor> propertyDescriptors = getPropertyDescriptors(param.getClass());
        List<Method> list = null;
        assert propertyDescriptors != null;
        for (PropertyDescriptor property : propertyDescriptors) {
            Method getter = property.getReadMethod();
            Object value = null;
            try {
                value = getter.invoke(param);
            } catch (IllegalAccessException | InvocationTargetException e) {
                logger.error(e.getMessage(), e);
            }
            if (value != null) {
                if (list == null){
                    list = new ArrayList<>();
                }
                list.add(getter);
            }
        }
        return list;
    }


    public static List<PropertyDescriptor> getPropertyDescriptors(Class clazz){
        List<PropertyDescriptor> propertyDescriptors = PROPERTY_DESCRIPTORS.get(clazz.getName());
        if (propertyDescriptors != null){
            return propertyDescriptors;
        }
        try {
            BeanInfo beanInfo = Introspector.getBeanInfo(clazz);
            PropertyDescriptor[] propertyDescriptorsArr = beanInfo.getPropertyDescriptors();
            List<PropertyDescriptor> list = new ArrayList<>();
            for (PropertyDescriptor propertyDescriptor : propertyDescriptorsArr) {
                list.add(propertyDescriptor);
            }
            PROPERTY_DESCRIPTORS.put(clazz.getName(), list);
            return list;
        } catch (IntrospectionException e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }

    /**
     * Bean 复制【 copyProperties，效率极低，推荐使用】
     *
     * @param source 源Bean
     * @param target 目标Bean
     * @param <S>    Source
     * @param <T>    Target
     */
    public static <S, T> void cp(S source, T target) {
        if (source == null || target == null) {
            return;
        }
        BeanUtils.copyProperties(source, target, getNullPropertyNames(source));
    }

    /**
     * List Bean 复制【 copyProperties，效率极低，推荐使用】
     *
     * @param source 源ListBean
     * @param clazz  目标ListType
     * @param <S>    Source
     * @param <T>    Target
     * @return
     */
    public static <S, T> List<T> cp(List<S> source, Class<T> clazz) {
        if (source == null || source.size() == 0) {
            return null;
        }
        List<T> list = new ArrayList<>();
        try {
            for (S s : source) {
                T t = clazz.getDeclaredConstructor().newInstance();
                cp(s, t);
                list.add(t);
            }
        } catch (InstantiationException | NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            logger.error(e.getMessage(), e);
        }
        return list;
    }

    /**
     * 找出Bean 中，为 null 的属性
     *
     * @param source
     * @return
     */
    public static String[] getNullPropertyNames(Object source) {
        final BeanWrapper src = new BeanWrapperImpl(source);
        java.beans.PropertyDescriptor[] pds = src.getPropertyDescriptors();

        Set<String> emptyNames = new HashSet<String>();
        for (java.beans.PropertyDescriptor pd : pds) {
            Object srcValue = src.getPropertyValue(pd.getName());
            if (srcValue == null) {
                emptyNames.add(pd.getName());
            }
        }
        String[] result = new String[emptyNames.size()];
        return emptyNames.toArray(result);
    }

}
