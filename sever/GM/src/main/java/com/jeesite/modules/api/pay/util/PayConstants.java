package com.jeesite.modules.api.pay.util;

/**
 * 支付的一些常量声明
 *
 * @author zjl
 */
public class PayConstants {

    public static final int PAY_TYPE_WECHAT = 1;    // 微信支付
    public static final int PAY_TYPE_ALI = 2;       // 支付宝支付
    public static final int PAY_TYPE_THIRD = 6; // 第三方支付
    public static final int PAY_TYPE_THIRD_WECHAT_QRCODE = 3; // 第三方微信扫码
    public static final int PAY_TYPE_THIRD_ALI_QRCODE = 4; // 第三方支付宝扫码
    public static final int PAY_TYPE_THIRD_ALI = 5; // 第三方支付宝
    public static final int PAY_TYPE_THIRD_WECHAT = 7; // 第三方微信
    public static final int PAY_TYPE_HUAWEI = 8;    // 华为支付
    public static final int PAY_TYPE_HUAYI = 9;    // 华移支付宝支付
    public static final int PAY_TYPE_lIDPAY = 10;    // lid支付
    public static final int PAY_TYPE_HLBPAY = 11;    // 合利宝支付
    public static final int PAY_TYPE_HUAYI_SAO = 12;    // 华移支付宝扫码
    public static final int PAY_TYPE_ADAPAY = 13;    // Ada支付宝H5
    public static final int PAY_TYPE_KKAPAY_H5 = 14;    // 快快支付宝H5
    public static final int PAY_TYPE_KKAPAY = 15;    // 快快支付宝扫码
    public static final int PAY_TYPE_KKAPAY_WX = 16;    // 快快微信扫码
    public static final int PAY_TYPE_EASYPAY_WX = 17;    // easy微信扫码
    public static final int PAY_TYPE_JIAOYILE = 18;    // 交易乐
    public static final int PAY_TYPE_EDFAPAY = 20;    // Edfapay

    public static final int PAY_TYPE_GOOGLE = 19;  // 谷歌支付

    public static final int PAY_TYPE_APPLE = 21;  // 苹果支付

    public static final int PAY_TYPE_USD_BANK = 22;  // 美国网银

    public static final String TRADE_TYPE_WECHAT_APP = "APP"; // 微信APP支付
    public static final String TRADE_TYPE_WECHAT_NATIVE = "NATIVE"; // 微信原生支付
    public static final String TRADE_TYPE_WECHAT_JSAPI = "JSAPI"; // 微信JSAPI支付（或小程序支付）
    public static final String TRADE_TYPE_WECHAT_MWEB = "MWEB"; // 微信H5支付
    public static final String TRADE_TYPE_WECHAT_MICROPAY = "MICROPAY"; // 微信付款码支付，付款码支付有单独的支付接口，所以接口不需要上传，该字段在对账单中会出现

    public static final String WECHAT_RETURN_CODE_SUCCESS = "SUCCESS"; // 返回状态码 成功
    public static final String WECHAT_RETURN_CODE_FAIL = "FAIL"; // 返回状态码 失败
    public static final String WECHAT_RESULT_CODE_SUCCESS = "SUCCESS"; // 业务结果 成功
    public static final String WECHAT_RESULT_CODE_FAIL = "FAIL"; // 业务结果 失败

    public static final String PAY_ERROR = "ERROR_PAY"; // 支付出错

}
