package com.maple.game.osee.util;

import com.maple.engine.utils.MySettingUtil;
import lombok.SneakyThrows;

/**
 * 发送短信：工具类
 */
public class SmsUtil {

    private static int getSmsType() {
        return MySettingUtil.SETTING.getInt("sms.type", 1);
    }

    /**
     * 忘记密码
     */
    public static String forgetPassword(String phoneNumber, String checkCode) {

        int smsType = getSmsType();

        if (smsType == 2) {

            return SmsAliYunUtil.forgetPassword(phoneNumber, checkCode);

        } else {

            return SmsTencentUtil.forgetPassword(phoneNumber, checkCode);

        }

    }

    /**
     * 注册
     */
    public static String signIn(String phoneNumber, String checkCode) {

        int smsType = getSmsType();

        if (smsType == 2) {

            return SmsAliYunUtil.signIn(phoneNumber, checkCode);

        } else {

            return SmsTencentUtil.signIn(phoneNumber, checkCode);

        }

    }

    /**
     * 发送短信
     */
    @SneakyThrows
    public static String send(String phoneNumber, String checkCode) {

        int smsType = getSmsType();

        if (smsType == 2) {

            return SmsAliYunUtil.send(phoneNumber, checkCode);

        } else {

            return SmsTencentUtil.send(phoneNumber, checkCode);

        }

    }

}
