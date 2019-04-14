package com.wkclz.core.pojo.entity;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidPooledConnection;
import com.wkclz.core.util.SecretUtil;
import lombok.Data;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

@Data
public class DataSource {


    private static Map<String, DruidPooledConnection> dataConns = null;


    private String url;

    private String driverClassName = "com.mysql.jdbc.Driver";

    private String username;

    private String password;


    /**
     * 取得已经构造生成的数据库连接
     * @return 返回数据库连接对象
     * @throws Exception
     */
    public static synchronized DruidPooledConnection getConnect(DataSource dataSource) {

        if (dataConns == null){
            dataConns = new HashMap<>();
        }
        String url = dataSource.getUrl();
        String hex = SecretUtil.md5(url);
        DruidPooledConnection conn = dataConns.get(hex);
        try {
            if (conn == null || conn.isClosed()){
                DruidDataSource druidDataSource = getDruidDataSource(dataSource);
                conn = druidDataSource.getConnection();
                dataConns.put(hex, conn);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return conn;
    }

    private static DruidDataSource getDruidDataSource(DataSource dataSource) {

        DruidDataSource db =new DruidDataSource();

        //设置连接参数
        db.setUrl(dataSource.getUrl());
        db.setDriverClassName(dataSource.getDriverClassName());
        db.setUsername(dataSource.getUsername());
        db.setPassword(dataSource.getPassword());
        //配置初始化大小、最小、最大
        db.setInitialSize(1);
        db.setMinIdle(1);
        db.setMaxActive(20);
        //连接泄漏监测
        db.setRemoveAbandoned(true);
        db.setRemoveAbandonedTimeout(30);
        //配置获取连接等待超时的时间
        db.setMaxWait(20000);
        //配置间隔多久才进行一次检测，检测需要关闭的空闲连接，单位是毫秒
        db.setTimeBetweenEvictionRunsMillis(20000);
        //防止过期
        db.setValidationQuery("SELECT 'x'");
        db.setTestWhileIdle(true);
        db.setTestOnBorrow(true);

        return db;
    }


}