package com.wkclz.core.util;

import java.util.UUID;

/**
 * Description:
 * Created: wangkaicun @ 2018-03-28 上午11:10
 */
public class UniqueCodeUtil {

    /**
     * 可用于产生长度要求不高的唯一 id
     *
     * @return
     */
    public static String getJavaUuid() {
        UUID uuid = UUID.randomUUID();
        return uuid.toString().replaceAll("-", "").toLowerCase();
    }

    public static synchronized String getOrderNum() {
        StringBuffer sb = new StringBuffer();
        sb.append(System.currentTimeMillis());
        sb.append(Thread.currentThread().getId());
        sb.append(1 + (int) (Math.random() * 10000));
        return sb.toString();
    }

}
