package com.wkclz.core.helper;

import javax.servlet.http.HttpServletRequest;
import java.net.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

/**
 * Description:
 * Created: wangkaicun @ 2017-10-21 上午12:41
 */
public class IpHelper {

    public static String getIpAddr(HttpServletRequest req) {
        String ipAddress = req.getHeader("x-forwarded-for");
        if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = req.getHeader("Proxy-Client-IP");
        }
        if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = req.getHeader("WL-Proxy-Client-IP");
        }
        if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = req.getRemoteAddr();
            if ("127.0.0.1".equals(ipAddress) || "0:0:0:0:0:0:0:1".equals(ipAddress)) {
                // 根据网卡取本机配置的IP
                InetAddress inet = null;
                try {
                    inet = InetAddress.getLocalHost();
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                }
                ipAddress = inet == null ? null:inet.getHostAddress();
            }
        }
        // 对于通过多个代理的情况，第一个IP为客户端真实IP,多个IP按照','分割
        if (ipAddress != null && ipAddress.length() > 15) {
            // "***.***.***.***".length() = 15
            if (ipAddress.indexOf(",") > 0) {
                ipAddress = ipAddress.substring(0, ipAddress.indexOf(","));
            }
        }
        return ipAddress;
    }

    public static String getServerIP() {
        List<String> ipList = new ArrayList<>();
        //得到所有接口
        Enumeration<NetworkInterface> interfaces = null;
        try {
            interfaces = NetworkInterface.getNetworkInterfaces();
        } catch (SocketException e) {
            e.printStackTrace();
        }
        if (interfaces == null){
            return null;
        }
        while (interfaces.hasMoreElements()) {
            //得到单个接口
            NetworkInterface nextInterface = interfaces.nextElement();
            Enumeration<InetAddress> inetAddresses = nextInterface.getInetAddresses();
            while (inetAddresses.hasMoreElements()) {
                //得到单个IP
                InetAddress inetAddress = inetAddresses.nextElement();
                //确定要是 ipv4的地址
                if (inetAddress != null && inetAddress instanceof Inet4Address) {
                    String ip = inetAddress.getHostAddress();
                    ipList.add(ip);
                }
            }
        }
        StringBuffer ip = new StringBuffer();
        for (String i : ipList) {
            ip.append(i).append(",");
        }
        return ip.toString();
    }

}
