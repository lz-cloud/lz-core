package com.wkclz.core.util;

import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Properties;

public class PropUtil {

    public static Properties readProp(String propertiesPath){
        File file = new File(propertiesPath);
        if (!file.exists()){
            return null;
        }

        InputStream in = null;
        Properties props = new Properties();
        try {
            in = new BufferedInputStream(new FileInputStream(file));
            props.load(in);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (in!=null){
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return props;

    }

    public static void writeProp(String propertiesPath, Properties newProps){
        Properties oldProp = readProp(propertiesPath);
        newProps.forEach((propKey,propValue) ->{
            oldProp.setProperty(propKey.toString(),propValue.toString());
        });

        Properties sortProp = MapUtil.map2Prop(MapUtil.sortMapByKey(MapUtil.prop2Map(oldProp)));

        File file = new File(propertiesPath);
        if (!file.exists()){
            file.mkdirs();
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        Writer fw = null;
        try {
            fw = new FileWriter(propertiesPath);
            sortProp.store(fw, "此属性文件由程序自动管理，请不要手动编辑");
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fw!=null){
                try {
                    fw.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }




    /**
     * Map 2 Object
     * @param prop
     * @param clazz
     * @return
     * @throws Exception
     */
    public static Object prop2Object(Properties prop, Class<?> clazz){
        if (prop == null) { return null; }
        Object obj = null;
        try {
            obj = clazz.newInstance();
            Field[] declaredFields = obj.getClass().getDeclaredFields();
            for(Field field:declaredFields){
                int mod = field.getModifiers();
                if(Modifier.isStatic(mod) || Modifier.isFinal(mod)){
                    continue;
                }
                field.setAccessible(true);
                field.set(obj, prop.get(field.getName()));
            }
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return obj;
    }





}
