package com.maple.game.osee.util;

import java.util.concurrent.CompletableFuture;

import org.springframework.stereotype.Component;

import com.aliyun.auth.credentials.Credential;
import com.aliyun.auth.credentials.provider.StaticCredentialProvider;
import com.aliyun.sdk.service.dysmsapi20170525.AsyncClient;
import com.aliyun.sdk.service.dysmsapi20170525.models.SendSmsRequest;
import com.aliyun.sdk.service.dysmsapi20170525.models.SendSmsResponse;
import com.aliyun.sdk.service.dysmsapi20170525.models.SendSmsResponseBody;
import com.maple.engine.utils.MySettingUtil;

import cn.hutool.json.JSONUtil;
import darabonba.core.client.ClientOverrideConfiguration;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

/**
 * 短信工具类：阿里云
 */
@Component
@Slf4j
public class SmsAliYunUtil {

    /**
     * 忘记密码
     */
    public static String forgetPassword(String phoneNumbers, String code) {

        return send(phoneNumbers, code);

    }

    /**
     * 注册
     */
    public static String signIn(String phoneNumbers, String code) {

        return send(phoneNumbers, code);

    }

    /**
     * 发送短信
     */
    @SneakyThrows
    public static String send(String phoneNumbers, String code) {

        SendSmsRequest sendSmsRequest = SendSmsRequest.builder().phoneNumbers(phoneNumbers)
            .signName(MySettingUtil.SETTING.getStr("sms.aliyun.signName"))
            .templateCode(MySettingUtil.SETTING.getStr("sms.aliyun.templateCode"))
            .templateParam(JSONUtil.createObj().set("code", code).toString()).build();

        // Configure Credentials authentication information, including ak, secret, token
        StaticCredentialProvider provider = StaticCredentialProvider
            .create(Credential.builder().accessKeyId(MySettingUtil.getRsaValue("sms.aliyun.accessKeyId"))
                .accessKeySecret(MySettingUtil.getRsaValue("sms.aliyun.accessKeySecret")).build());

        // Configure the Client
        AsyncClient client = AsyncClient.builder().region("cn-hangzhou") // Region ID
            .credentialsProvider(provider)
            .overrideConfiguration(ClientOverrideConfiguration.create().setEndpointOverride("dysmsapi.aliyuncs.com"))
            .build();

        // Asynchronously get the return value of the API request
        CompletableFuture<SendSmsResponse> response = client.sendSms(sendSmsRequest);

        // Synchronously get the return value of the API request
        SendSmsResponse resp = response.get();

        SendSmsResponseBody body = resp.getBody();

        // Finally, close the client
        client.close();

        if ("OK".equals(body.getCode())) {

            return null;

        }

        log.info("阿里云短信发送失败，phoneNumbers：{}，code：{}，message：{}", sendSmsRequest.getPhoneNumbers(), body.getCode(),
            body.getMessage());

        return body.getCode();

    }

    public static void main(String[] args) {

    }

}
