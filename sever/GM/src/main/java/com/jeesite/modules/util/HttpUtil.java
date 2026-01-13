package com.jeesite.modules.util;

import okhttp3.*;

import java.io.IOException;
import java.util.Map;

/**
 * HTTP请求工具
 *
 * @author zjl
 */
public class HttpUtil {

    private static final OkHttpClient client = new OkHttpClient();

    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    private static final MediaType XML = MediaType.get("text/xml; charset=utf-8");

    /**
     * 提交POST请求，请求体格式为JSON
     */
    public static String doPostJson(String url, String json) {
        return doPostData(url, json, JSON);
    }

    /**
     * 提交POST请求，请求体格式为JSON
     */
    public static String doPostXml(String url, String xml) {
        return doPostData(url, xml, XML);
    }

    private static String doPostData(String url, String data, MediaType mediaType) {
        try {
            RequestBody requestBody = RequestBody.create(mediaType, data);
            Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();
            Response response = client.newCall(request).execute();
            if (response.body() != null) {
                return response.body().string();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * POST方式提交表单数据
     */
    public static String doPostFormData(String url, Map<String, Object> params) {
        try {
            FormBody.Builder builder = new FormBody.Builder();
            for (Map.Entry entry : params.entrySet()) {
                builder.add(entry.getKey().toString(), entry.getValue().toString());
            }
            Request request = new Request.Builder()
                .url(url)
                .post(builder.build())
                .build();
            Response response = client.newCall(request).execute();
            if (response.body() != null) {
                return response.body().string();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }
}
