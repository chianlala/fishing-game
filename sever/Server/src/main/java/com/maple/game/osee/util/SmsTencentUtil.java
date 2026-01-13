package com.maple.game.osee.util;

import com.maple.engine.utils.MySettingUtil;
import com.maple.game.osee.entity.tencentcloudapi.common.Credential;
import com.maple.game.osee.entity.tencentcloudapi.common.profile.ClientProfile;
import com.maple.game.osee.entity.tencentcloudapi.common.profile.HttpProfile;
import com.maple.game.osee.entity.tencentcloudapi.sms.v20190711.SmsClient;
import com.maple.game.osee.entity.tencentcloudapi.sms.v20190711.models.SendSmsResponse;
import com.maple.game.osee.entity.tencentcloudapi.sms.v20190711.models.SendStatus;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 短信工具类：腾讯云
 */
@Component
@Slf4j
public class SmsTencentUtil {

    /**
     * 忘记密码
     */
    public static String forgetPassword(String phoneNumber, String code) {

        return send(phoneNumber, code);

    }

    /**
     * 注册
     */
    public static String signIn(String phoneNumber, String code) {

        return send(phoneNumber, code);

    }

    /**
     * 发送短信
     */
    @SneakyThrows
    public static String send(String phoneNumber, String checkCode) {

        /* 必要步骤：
         * 实例化一个认证对象，入参需要传入腾讯云账户密钥对secretId，secretKey。
         * 这里采用的是从环境变量读取的方式，需要在环境变量中先设置这两个值。
         * 你也可以直接在代码中写死密钥对，但是小心不要将代码复制、上传或者分享给他人，
         * 以免泄露密钥对危及你的财产安全。
         * CAM密匙查询: https://console.cloud.tencent.com/cam/capi*/
        com.maple.game.osee.entity.tencentcloudapi.common.Credential cred = new Credential(
            MySettingUtil.getRsaValue("sms.tencent.secretId"), MySettingUtil.getRsaValue("sms.tencent.secretKey"));

        // 实例化一个http选项，可选，没有特殊需求可以跳过
        HttpProfile httpProfile = new HttpProfile();
        // 设置代理
        // httpProfile.setProxyHost("host");
        // httpProfile.setProxyPort(8080);
        /* SDK默认使用POST方法。
         * 如果你一定要使用GET方法，可以在这里设置。GET方法无法处理一些较大的请求 */
        httpProfile.setReqMethod("POST");
        /* SDK有默认的超时时间，非必要请不要进行调整
         * 如有需要请在代码中查阅以获取最新的默认值 */
        httpProfile.setConnTimeout(60);
        /* SDK会自动指定域名。通常是不需要特地指定域名的，但是如果你访问的是金融区的服务
         * 则必须手动指定域名，例如sms的上海金融区域名： sms.ap-shanghai-fsi.tencentcloudapi.com */
        httpProfile.setEndpoint("sms.tencentcloudapi.com");

        /* 非必要步骤:
         * 实例化一个客户端配置对象，可以指定超时时间等配置 */
        ClientProfile clientProfile = new ClientProfile();
        /* SDK默认用TC3-HMAC-SHA256进行签名
         * 非必要请不要修改这个字段 */
        clientProfile.setSignMethod("HmacSHA256");
        clientProfile.setHttpProfile(httpProfile);

        SmsClient client = new SmsClient(cred, "ap-guangzhou", clientProfile);

        com.maple.game.osee.entity.tencentcloudapi.sms.v20190711.models.SendSmsRequest req =
            new com.maple.game.osee.entity.tencentcloudapi.sms.v20190711.models.SendSmsRequest();

        /* 短信应用ID: 短信SdkAppid在 [短信控制台] 添加应用后生成的实际SdkAppid，示例如1400006666 */
        req.setSmsSdkAppid(MySettingUtil.SETTING.getStr("sms.tencent.sdkAppId"));

        /* 短信签名内容: 使用 UTF-8 编码，必须填写已审核通过的签名，签名信息可登录 [短信控制台] 查看 */
        req.setSign(MySettingUtil.SETTING.getStr("sms.tencent.sign"));

        /* 模板 ID: 必须填写已审核通过的模板 ID。模板ID可登录 [短信控制台] 查看 */
        req.setTemplateID(MySettingUtil.SETTING.getStr("sms.tencent.templateId"));

        /* 下发手机号码，采用 e.164 标准，+[国家或地区码][手机号]
         * 示例如：+8613711112222， 其中前面有一个+号 ，86为国家码，13711112222为手机号，最多不要超过200个手机号*/
        String[] phoneNumbers = {"+86" + phoneNumber};
        req.setPhoneNumberSet(phoneNumbers);

        /* 模板参数: 若无模板参数，则设置为空*/
        String[] templateParams = {String.valueOf(checkCode), "5"};
        req.setTemplateParamSet(templateParams);

        /* 通过 client 对象调用 SendSms 方法发起请求。注意请求方法名与请求对象是对应的
         * 返回的 res 是一个 SendSmsResponse 类的实例，与请求对象对应 */
        SendSmsResponse res = client.SendSms(req);

        SendStatus sendStatus = res.getSendStatusSet()[0];

        String code = sendStatus.getCode();

        if ("Ok".equals(code)) {

            return null;

        }

        log.info("腾讯云短信发送失败，phoneNumber：{}，code：{}，message：{}", phoneNumber, sendStatus.getCode(),
            sendStatus.getMessage());

        return sendStatus.getCode();

    }

}
