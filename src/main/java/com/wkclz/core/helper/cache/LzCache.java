package com.wkclz.core.helper.cache;

public interface LzCache {

    <T> T get(T param);

    void wipe(Class clazz);

}