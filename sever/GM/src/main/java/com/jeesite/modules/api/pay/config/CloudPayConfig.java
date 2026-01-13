package com.jeesite.modules.api.pay.config;

import com.jeesite.modules.osee.config.ProjectConfig;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.io.Serializable;

/**
 * 9A云支付接口
 * <p>
 * author: xds
 */
@Data
@Configuration
public class CloudPayConfig implements Serializable {

    @Autowired
    ProjectConfig projectConfig;

    /**
     * 支付参数实体
     */
    private static final long serialVersionUID = 8062013469509603867L;

//    @Value("2.0")
//    private String version;
//    @Value("${cloudPay.merchantId}")
//    private String parter;
    // @Value("${cloudPay.secretKey}")
    //private String key;
//
//    @Value("${cloudPay.in_pay}")
//    private String inp_pay;
//    @Value("${cloudPay.returnUrl}")
//    private String inp_RecefiveUrl;
//    @Value("${cloudPay.notifyUrl}")
//    private String inp_NotifyUrl;

    @Value("${cloudPay.user_id}")
    public String user_id;
    @Value("${cloudPay.key}")
    public String key;
    @Value("${cloudPay.mchPrivateKey}")
    public String mchPrivateKey;
    @Value("${cloudPay.platPublicKey}")
    public String platPublicKey;
    /**
     * 请求下单url
     */
    @Value("${cloudPay.req_url}")
    public String req_url;
    /**
     * 通知回调url
     */
    @Value("${cloudPay.notify_url}")
    public String notify_url;

    public String getNotify_url() {
        return String.format(notify_url, projectConfig.getServer(), projectConfig.getCode());
    }
}
