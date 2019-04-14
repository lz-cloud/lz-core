package com.wkclz.core.config;

import com.ulisesbocchio.jasyptspringboot.EncryptablePropertyDetector;
import com.wkclz.core.base.Constant;

public class MyEncryptablePropertyDetector implements EncryptablePropertyDetector {


    // 如果属性的字符开头为"{cipher}"，返回true，表明该属性是加密过的
    @Override
    public boolean isEncrypted(String s) {
        if (null != s) {
            return s.startsWith(Constant.CONFIG_ENCRYPTED_PREFIX);
        }
        return false;
    }
    // 该方法告诉工具，如何将自定义前缀去除
    @Override
    public String unwrapEncryptedValue(String s) {
        return s.substring(Constant.CONFIG_ENCRYPTED_PREFIX.length());
    }
}

