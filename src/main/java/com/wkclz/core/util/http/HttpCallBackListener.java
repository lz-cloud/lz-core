package com.wkclz.core.util.http;

public interface HttpCallBackListener {

    void onFinish(String response);

    void onError(Exception e);

}
