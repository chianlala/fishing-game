/**
 * Alipay.com Inc. Copyright (c) 2004-2016 All Rights Reserved.
 */
package com.jeesite.modules.api.pay.util;

import org.apache.http.*;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;

/**
 *
 * @author simon.xxm
 * @version $Id: HttpsUtil.java, v 0.1 2016年1月25日 上午11:27:12 simon.xxm Exp $
 */
@SuppressWarnings("deprecation")
public class HttpsUtil {

    private static HttpClient httpclient = new DefaultHttpClient();

    static {
        initNetwork();
    }

    public static void main(String[] args) {
        HttpsUtil h = new HttpsUtil();
        System.out.println(h.httpGet("https://www.baidu.com"));

    }

    public static void close() {
        // 关闭连接释放资源
        httpclient.getConnectionManager().shutdown();
    }

    public static String httpGet(String url) {
        String result = "";
        //httpclient = new DefaultHttpClient();
        HttpGet httpget = new HttpGet(url);
        HttpResponse response = null;
        try {
            //ifHttps(url);
            response = httpclient.execute(httpget);
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        HttpEntity entity = null;
        if (response != null) {
            entity = response.getEntity();
        }

        if (entity != null) {
            try {
                String content = EntityUtils.toString(entity);
                // result = new String(content.getBytes("ISO-8859-1"), "UTF-8");
                result = content;

                // 解决HttpClient获取中文乱码 ，用String对象进行转码
            } catch (ParseException e1) {
                e1.printStackTrace();
            } catch (UnsupportedEncodingException e1) {
                e1.printStackTrace();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
        return result;
    }

    public static String httpPost(String url, List<NameValuePair> paras) {
        String result = "";
        HttpPost httppost = new HttpPost(url);
        HttpResponse response = null;
        try {
            //ifHttps(url);
            if (paras != null && paras.size() > 0) {
                httppost.setEntity(new UrlEncodedFormEntity(paras));
            }
            response = httpclient.execute(httppost);
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        HttpEntity entity = null;
        if (response != null) {
            entity = response.getEntity();
        }

        if (entity != null) {
            try {
                String content = EntityUtils.toString(entity);
                result = content;
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }
        return result;
    }

    public static String httpPost(String url, String xmlContent) {
        String result = "";
        HttpPost httppost = new HttpPost(url);
        HttpResponse response = null;
        try {
            httppost.setEntity(new StringEntity(xmlContent, ContentType.APPLICATION_XML));
            response = httpclient.execute(httppost);
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        HttpEntity entity = null;
        if (response != null) {
            entity = response.getEntity();
        }

        if (entity != null) {
            try {
                String content = EntityUtils.toString(entity);
                result = content;
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }
        return result;
    }

    public static String httpPost(String url, List<NameValuePair> paras, List<Header> heads) {
        String result = "";
        HttpPost httppost = new HttpPost(url);
        HttpResponse response = null;
        try {
            //ifHttps(url);
            if (paras != null && paras.size() > 0) {
                httppost.setEntity(new UrlEncodedFormEntity(paras));
            }
            if (heads != null && heads.size() > 0) {
                for (Header header : heads) {
                    httppost.setHeader(header);
                }
            }
            System.out.println(httppost.getFirstHeader("Cookie"));
            response = httpclient.execute(httppost);
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        HttpEntity entity = null;
        if (response != null) {
            entity = response.getEntity();
        }

        if (entity != null) {
            try {
                String content = EntityUtils.toString(entity);
                result = content;
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }
        return result;
    }

    public static String httpPost(String url, HttpEntity reqEntity) {
        String result = "";
        HttpPost httppost = new HttpPost(url);
        HttpResponse response = null;
        try {
            httppost.setEntity(reqEntity);
            response = httpclient.execute(httppost);
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        HttpEntity entity = null;
        if (response != null) {
            entity = response.getEntity();
        }

        if (entity != null) {
            try {
                String content = EntityUtils.toString(entity);
                result = content;
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }
        return result;
    }


    /**
     * 初始化网络模块
     */
    private static void initNetwork() {
        try {
            // **
            SSLContext ctx = SSLContext.getInstance("TLS"); //$NON-NLS-1$
            X509TrustManager tm = new X509TrustManager() {

                public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                    return null;
                }

                public void checkClientTrusted(java.security.cert.X509Certificate[] chain,
                    String authType)
                    throws java.security.cert.CertificateException {

                }

                public void checkServerTrusted(java.security.cert.X509Certificate[] chain,
                    String authType)
                    throws java.security.cert.CertificateException {
                }

            };
            ctx.init(null, new TrustManager[]{tm}, null);
            SSLSocketFactory ssf = new SSLSocketFactory(ctx,
                SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
            Scheme sch = new Scheme("https", 443, ssf); //$NON-NLS-1$
            org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager tcm = new org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager();
            tcm.setMaxTotal(10);
            tcm.getSchemeRegistry().register(sch);
            httpclient = new DefaultHttpClient(tcm);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}