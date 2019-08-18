package com.wkclz.core.util.http;

import okhttp3.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


public class HttpUtil {

    public static void main(String[] args) {

        // HttpURLConnection
        HttpUtil.sendHttpRequest("xxxx", new HttpCallBackListener() {
            @Override
            public void onFinish(String response) {
                // 这里执行返回逻辑
            }

            @Override
            public void onError(Exception e) {
                // 错误处理
            }
        });

        // OKHttp
        HttpUtil.sendOkHttpRequest("xxx", new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                // 失败处理
            }

            @Override
            public void onResponse(Call call, Response response) {
                // 成功处理
            }
        });
    }


    /**
     * 使用HttpURLConnection
     *
     * @param address
     * @param listener
     */
    public static void sendHttpRequest(final String address, final HttpCallBackListener listener) {
        new Thread(() -> {
            HttpURLConnection connection = null;
            try {
                URL url = new URL(address);
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setReadTimeout(8000);
                connection.setConnectTimeout(8000);
                connection.setDoInput(true);
                connection.setDoOutput(true);
                InputStream in = connection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                if (listener != null) {
                    listener.onFinish(response.toString());
                }
            } catch (IOException e) {
                if (listener != null) {
                    listener.onError(e);
                }
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
            }
        }).start();
    }

    /**
     * 使用okhttp请求
     *
     * @param address
     * @param callback
     */
    public static void sendOkHttpRequest(String address, Callback callback) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(address).build();
        client.newCall(request).enqueue(callback);
    }

}
