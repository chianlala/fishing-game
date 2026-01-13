package com.jeesite.modules.util;

import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;


/**
 * Http多线程请求
 * <p>
 * Created by linzhe on 2011-01-11.
 */
public class HttpClientUtil {


    /**
     * http-get请求
     *
     * @param url
     * @return
     */
    public static String get(String url) {

        String res = "";

        CloseableHttpClient httpclient = HttpClients.createDefault(); // 构建http请求

        try {
            HttpGet httpget = new HttpGet(url);
            RequestConfig requestConfig = RequestConfig.custom()
                .setConnectTimeout(60000)
                .setConnectionRequestTimeout(60000).setSocketTimeout(60000)
                .build();
            httpget.setConfig(requestConfig);

            CloseableHttpResponse response = httpclient.execute(httpget);
            try {
                HttpEntity entity = response.getEntity();

                System.out.println(response.getStatusLine());

                if (entity != null) {
                    res = EntityUtils.toString(entity, "utf-8");
                }
                EntityUtils.consume(entity);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                response.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                httpclient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return res;
    }

    /**
     * http-post请求
     *
     * @param url
     * @param jsonStr
     * @return
     */
    public static String post(String url, String jsonStr) {

        String res = "";

        CloseableHttpClient httpclient = HttpClients.createDefault(); // 构建http请求

        try {
            HttpPost httpPost = new HttpPost(url);
            httpPost.addHeader("Content-Type", "application/json;charset=UTF-8");

            StringEntity enty = new StringEntity(jsonStr, "utf-8");
            httpPost.setEntity(enty);
            RequestConfig requestConfig = RequestConfig.custom()
                .setConnectTimeout(60000)
                .setConnectionRequestTimeout(60000).setSocketTimeout(60000)
                .build();
            httpPost.setConfig(requestConfig);

            CloseableHttpResponse response = httpclient.execute(httpPost);
            try {
                HttpEntity entity = response.getEntity();
                if (entity != null) {
                    res = EntityUtils.toString(entity, "utf-8");
                }
                EntityUtils.consume(entity);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                response.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                httpclient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return res;
    }

}
