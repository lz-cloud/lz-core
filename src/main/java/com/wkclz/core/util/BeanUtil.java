package com.wkclz.core.util;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Description:
 * Created: wangkaicun @ 2018-01-20 下午11:20
 */
public class BeanUtil {

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
            BeanInfo beanInfo = Introspector.getBeanInfo(obj.getClass());
            PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
            for (PropertyDescriptor property : propertyDescriptors) {
                Method getter = property.getReadMethod();
                Object value = getter.invoke(obj);
                if (value != null && value.toString().trim().length() == 0) {
                    Method setter = property.getWriteMethod();
                    setter.invoke(obj, new Object[]{null});
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return obj;
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
                T t = clazz.newInstance();
                cp(s, t);
                list.add(t);
            }
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
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
