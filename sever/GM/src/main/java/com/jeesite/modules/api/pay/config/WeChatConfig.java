package com.jeesite.modules.api.pay.config;

import com.jeesite.modules.osee.config.ProjectConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * 微信配置
 *
 * @author zjl
 */
@Configuration
public class WeChatConfig {

    @Value("${wechat.pay.appId}")
    private String appId;

    @Value("${wechat.pay.secretKey}")
    private String secretKey;

    @Value("${wechat.pay.mchId}")
    private String mchId;

    @Value("${wechat.pay.notifyUrl}")
    private String notifyUrl;

    @Value("${wechat.pay.signType}")
    private String signType;

    @Value("${wechat.pay.unifiedOrderUrl}")
    private String unifiedOrderUrl;

    @Autowired
    private ProjectConfig projectConfig;

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public String getMchId() {
        return mchId;
    }

    public void setMchId(String mchId) {
        this.mchId = mchId;
    }

    public String getNotifyUrl() {
        return String.format(notifyUrl, projectConfig.getServer(), projectConfig.getCode());
    }

    public void setNotifyUrl(String notifyUrl) {
        this.notifyUrl = notifyUrl;
    }

    public String getSignType() {
        return signType;
    }

    public void setSignType(String signType) {
        this.signType = signType;
    }

    public String getUnifiedOrderUrl() {
        return unifiedOrderUrl;
    }

    public void setUnifiedOrderUrl(String unifiedOrderUrl) {
        this.unifiedOrderUrl = unifiedOrderUrl;
    }
}
