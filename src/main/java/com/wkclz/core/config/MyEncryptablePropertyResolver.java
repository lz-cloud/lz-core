package com.wkclz.core.config;

import com.ulisesbocchio.jasyptspringboot.EncryptablePropertyResolver;
import com.wkclz.core.base.Constant;
import com.wkclz.core.util.SecretUtil;

public class MyEncryptablePropertyResolver implements EncryptablePropertyResolver {
    //自定义解密方法
    @Override
    public String resolvePropertyValue(String s) {
        if (null != s && s.startsWith(Constant.CONFIG_ENCRYPTED_PREFIX)) {
            return SecretUtil.getDecryptPassword(s.substring(Constant.CONFIG_ENCRYPTED_PREFIX.length()));
        }
        return s;
    }
}

