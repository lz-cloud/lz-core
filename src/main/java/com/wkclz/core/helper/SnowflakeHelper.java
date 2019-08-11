package com.wkclz.core.helper;

import com.wkclz.core.base.Sys;
import com.wkclz.core.util.SnowflakeIdWorker;

import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

public class SnowflakeHelper {

    public static SnowflakeIdWorker SFIW = null;

    // 生成唯一序列
    public static synchronized long getSnowflakeId(){
        if (SFIW == null){
            long workId = SnowflakeHelper.getWorkId();
            long datacenterId = SnowflakeHelper.getDatacenterId();
            SFIW = new SnowflakeIdWorker(workId, datacenterId);
        }
        return SFIW.nextId();
    }

    /**
     * 获取机器编码
     */
    public static long getWorkId() {
        long machinePiece;
        StringBuilder sb = new StringBuilder();
        Enumeration<NetworkInterface> e = null;
        try {
            e = NetworkInterface.getNetworkInterfaces();
        } catch (SocketException e1) {
            e1.printStackTrace();
        }
        while (e.hasMoreElements()) {
            NetworkInterface ni = e.nextElement();
            sb.append(ni.toString());
        }
        machinePiece = sb.toString().hashCode();
        return machinePiece;
    }

    public static long getDatacenterId(){
        int hashCode = Sys.CURRENT_ENV.hashCode();
        return hashCode;
    }

    public static void main(String[] args) {
        for (int i = 0; i < 1000; i++) {
            long snowflakeId = SnowflakeHelper.getSnowflakeId();
            System.out.println(snowflakeId);
        }
    }


}
