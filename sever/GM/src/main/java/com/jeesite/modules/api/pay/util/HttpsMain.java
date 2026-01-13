package com.jeesite.modules.api.pay.util;


import com.jeesite.modules.api.pay.config.MyConfig;

import javax.net.ssl.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

public class HttpsMain {


    //发送短信请求地址
    public static String sendSmsUrl = MyConfig.reqUrl + "/sms/sendSmsCode";

    //上传图片：
    public static String uploadUrl = MyConfig.reqUrl + "/merchant/uploadPhoto";

    //发起支付，预创订单，查询订单，订单退款，关闭订单，撤销订单
    public static String payUrl = MyConfig.reqUrl + "/pay";

    //商户入驻，支付授权目录配置，关闭商户，开启商户，注册查询，商户信息查询
    public static String merchantUrl = MyConfig.reqUrl + "/merchant/service";

    //代付下单，代付，余额查询
    public static String daifuUrl = MyConfig.reqUrl + "/daifu";

    public static String httpsReq(String reqUrl, String param) throws NoSuchAlgorithmException,
        NoSuchProviderException, IOException,
        KeyManagementException {
        URL url = new URL(reqUrl);
        HttpsURLConnection httpsConn = (HttpsURLConnection) url.openConnection();
        httpsConn.setHostnameVerifier(new HostnameVerifier() {
            public boolean verify(String paramString, SSLSession paramSSLSession) {
                return true;
            }
        });

        //创建SSLContext对象，并使用我们指定的信任管理器初始化
        TrustManager tm = new X509TrustManager() {
            public void checkClientTrusted(X509Certificate[] paramArrayOfX509Certificate,
                String paramString) throws CertificateException {
            }

            public void checkServerTrusted(X509Certificate[] paramArrayOfX509Certificate,
                String paramString) throws CertificateException {
            }

            public X509Certificate[] getAcceptedIssuers() {
                return null;
            }
        };

        SSLContext sslContext = SSLContext.getInstance("SSL", "SunJSSE");
        sslContext.init(null, new TrustManager[]{tm}, new java.security.SecureRandom());

        //从上述SSLContext对象中得到SSLSocketFactory对象
        SSLSocketFactory ssf = sslContext.getSocketFactory();

        //创建HttpsURLConnection对象，并设置其SSLSocketFactory对象
        httpsConn.setSSLSocketFactory(ssf);

        httpsConn.setDoOutput(true);
        httpsConn.setRequestMethod("POST");
        httpsConn.setRequestProperty("Content-Type", "application/xml;charset=UTF-8");
        httpsConn.setRequestProperty("Content-Length", String.valueOf(param.length()));

        OutputStreamWriter out = new OutputStreamWriter(httpsConn.getOutputStream(), "UTF-8");
        out.write(param);
        out.flush();
        out.close();

        BufferedReader reader = new BufferedReader(new InputStreamReader(
            httpsConn.getInputStream(), "UTF-8"));
        String tempLine = "";
        StringBuffer resultBuffer = new StringBuffer();
        while ((tempLine = reader.readLine()) != null) {
            resultBuffer.append(tempLine).append(System.getProperty("line.separator"));
        }
        return resultBuffer.toString();
    }

    public static String httpReq(String urlStr, String param) throws IOException {
        URL url = new URL(urlStr);
        HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();

        httpConn.setDoOutput(true);
        httpConn.setRequestMethod("POST");
        httpConn.setRequestProperty("Content-Type", "application/xml;charset=UTF-8");
        httpConn.setRequestProperty("Content-Length", String.valueOf(param.length()));

        OutputStreamWriter out = new OutputStreamWriter(httpConn.getOutputStream(), "UTF-8");
        out.write(param);
        out.flush();
        out.close();

        BufferedReader reader = new BufferedReader(new InputStreamReader(
            httpConn.getInputStream(), "UTF-8"));
        String tempLine = "";
        StringBuffer resultBuffer = new StringBuffer();
        while ((tempLine = reader.readLine()) != null) {
            resultBuffer.append(tempLine).append(System.getProperty("line.separator"));
        }
        return resultBuffer.toString();
    }

}
