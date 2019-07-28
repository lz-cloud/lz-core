package com.wkclz.core.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

/**
 * Properties 相关处理类
 *
 * @author wangkc
 * @mail admin@wkclz.com
 * @since 2017-01-15 13:55:02
 */
public class PropertiesUtil {
    private static final Logger logger = LoggerFactory.getLogger(PropertiesUtil.class);

    /**
     * Properties 转换为 Map的List
     *
     * @param properties
     * @author wangkc
     * @mail admin@wkclz.com
     * @since 2017-01-15 13:56:24
     */
    public static Map<String, Object> prop2Map(Properties properties) {
        Map<String, Object> map = new HashMap<>();
        Set<Object> keySet = properties.keySet();
        for (Object key : keySet) {
            map.put(key.toString(), properties.get(key));
        }
        return map;
    }


    public static Properties propFile2Prop(String fileStr) {

        InputStream in = null;
        Properties prop = new Properties();
        try {
            in = new BufferedInputStream(new FileInputStream(new File(fileStr)));
            prop.load(in);
        } catch (FileNotFoundException e) {
            logger.error("FileNotFoundException", e);
        } catch (IOException e) {
            logger.error("IOException", e);
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    logger.error("IOException", e);
                }
            }
        }
        return prop;
    }


    /**
     * @param @param  file
     * @param @return
     * @param @throws IOException    设定文件
     * @throws
     * @Title:
     * @Description:
     * @author wangkc admin@wkclz.com
     * @date 2017年5月31日 下午1:35:19 *
     */
    public static Map<String, Object> propFile2Map(String file) throws IOException {
        Properties prop = propFile2Prop(file);
        return prop2Map(prop);
    }

}
