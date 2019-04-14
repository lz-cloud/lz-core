package com.wkclz.core.util;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ResultSetMapper {

    public static List<Map> toMapList(ResultSet rs) {
        List list = new ArrayList();
        try {
            // 获取数据库表结构
            ResultSetMetaData meta = rs.getMetaData();
            while (rs.next()) {
                Map<String, Object> map = new HashMap<>();
                // 循环获取指定行的每一列的信息
                for (int i = 1; i <= meta.getColumnCount(); i++) {
                    // 当前列名
                    String colName = meta.getColumnLabel(i);
                    colName = StringUtil.underlineToCamel(colName);
                    Object value = rs.getObject(i);
                    if (value != null) {
                        map.put(colName, value);
                    }
                }
                list.add(map);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            // do nothing
        }
        return list;
    }


}
