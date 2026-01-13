package com.jeesite.modules.api.pay.config;

import com.jeesite.modules.osee.config.ProjectConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * 第三方支付的配置信息
 *
 * @author zjl
 */
@Configuration
public class ThirdPayConfig {

    /**
     * 商户号
     */
    @Value("${thirdpay.merId}")
    private String merId;

    /**
     * 密钥
     */
    @Value("${thirdpay.secretKey}")
    private String secretKey;

    /**
     * 支付回调通知url
     */
    @Value("${thirdpay.notifyUrl}")
    private String notifyUrl;

    /**
     * 支付完成后跳转地址
     */
    @Value("${thirdpay.callbackUrl}")
    private String callbackUrl;

    /**
     * 支付请求链接
     */
    @Value("${thirdpay.payUrl}")
    private String payUrl;

    /**
     * 支付订单查询链接
     */
    @Value("${thirdpay.queryUrl}")
    private String queryUrl;

    @Autowired
    private ProjectConfig projectConfig;

    public String getMerId() {
        return merId;
    }

    public void setMerId(String merId) {
        this.merId = merId;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public String getNotifyUrl() {
        return String.format(notifyUrl, projectConfig.getServer(), projectConfig.getCode());
    }

    public void setNotifyUrl(String notifyUrl) {
        this.notifyUrl = notifyUrl;
    }

    public String getCallbackUrl() {
        return String.format(callbackUrl, projectConfig.getServer(), projectConfig.getCode());
    }

    public void setCallbackUrl(String callbackUrl) {
        this.callbackUrl = callbackUrl;
    }

    public String getPayUrl() {
        return payUrl;
    }

    public void setPayUrl(String payUrl) {
        this.payUrl = payUrl;
    }

    public String getQueryUrl() {
        return queryUrl;
    }

    public void setQueryUrl(String queryUrl) {
        this.queryUrl = queryUrl;
    }
}
