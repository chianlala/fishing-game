package com.jeesite.modules.api.pay.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.huifu.adapay.Adapay;
import com.huifu.adapay.core.AdapayCore;
import com.huifu.adapay.core.util.AdapaySign;
import com.huifu.adapay.model.MerConfig;
import com.huifu.adapay.model.Payment;
import com.jeesite.modules.api.pay.config.CloudPayConfig;
import com.jeesite.modules.api.pay.domain.ShopItem;
import com.jeesite.modules.api.pay.service.AdaPayService;
import com.jeesite.modules.api.pay.service.PayService;
import com.jeesite.modules.api.pay.util.PayConstants;
import com.jeesite.modules.osee.config.ProjectConfig;
import com.jeesite.modules.osee.vo.CommonResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import java.util.*;


@Service
public class AdaPayServiceImpl implements AdaPayService {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    CloudPayConfig cloudPayConfig;

    @Autowired
    ProjectConfig projectConfig;

    @Autowired
    PayService payService;

    @Autowired
    RestTemplate restTemplate;

    @Override
    public CommonResponse unifiedorder(String orderId, ShopItem shopItem) {
        try {

            logger.debug("Ada支付订单下单开始");
            String money = String.format("%.2f", shopItem.getPayMoney());//注意金额是以元为单位。

            /**
             * debug 模式，开启后有详细的日志
             */
            Adapay.debug = true;

            /**
             * prodMode 模式，默认为生产模式，false可以使用mock模式
             */
            Adapay.prodMode = true;

            /**
             * 初始化商户配置，服务器启动前，必须通过该方式初始化商户配置完成
             * apiKey为prod模式的API KEY
             * mockApiKey为mock模式的API KEY
             * rsaPrivateKey为商户发起请求时，用于请求参数加签所需要的RSA私钥
             */
            String apiKey = "api_live_ebb4c96c-945a-4214-aa9e-96a2284d5406";
            String mockApiKey = "api_test_9eb44b14-9183-4412-a191-46af6b9fa52d";
            String rsaPrivateKey = "MIICdQIBADANBgkqhkiG9w0BAQEFAASCAl8wggJbAgEAAoGBAKUdpcTY5nzwPRKn4HqzEb6Dt57PKwnEA3hP+kMRrsGqTo9mew6ZdHyfDa/oTNPQKmD8xYbmW4M2dgsAEeWH8B/2sg5nqIQ64GdkCO6r31NBrR5DhX6ms/l4z+JL4mDWSAOjOpT0amDioohNaJynEHl2A4RRogBuC5udY1L1GrlRAgMBAAECgYBehiB2Fm2srKNp4K1D0Ouhkyo1QggzaQFLiQ2OA7t1pxeeRF7CNttW1H8zattAjvUT3OD/nzRYY38kQP/91NqDpnsQw/0SEjAidEfwZj1KmLt00cx1TSvyisyLutVINoPSzbANN73UTHePFKR/+TScp/iXG2iB/lWvyxy66gyZeQJBANDjUnaqiX+9GrJNVxcbZ/Fm3ZroxYSCza9HvJUshw4K5Rrjgj0wyvTrfeBtQW78GnjEwULFHGOJkHH0kdEPBycCQQDKWwc5NkWhLih1j7oJu+cftIWJ/mY7XKNT9RYCsFEOc4GgDWpNXBWel4iMQ12+5+Ir+hV/j4SCAFt9OlZEfMbHAkB+Q0oPTJn8SpQefr1LzFcSBfmhr13k8SPe9V+6U8X26QL/M9H/psSnMslNpPzOVzixE002TOsSB472Mr0JPyo1AkAWoFcObqMagq7Ddm625+vP/79uzqNfv9wDZ0QuyMhHdWLpIpbgT4ubUBZUmLPCxOrlP/FncgS/BIs2VW+P+OPBAkBZ1iNceCaeUCEhS85xprSK7YvoWRiYqEsaQOaH0vko8iURO3VlMqiI9b2IlqQ36sh3fM/VokeXS10W5/iGCXCd";
            MerConfig merConfig = new MerConfig();
            merConfig.setApiKey(apiKey);
            merConfig.setApiMockKey(mockApiKey);
            merConfig.setRSAPrivateKey(rsaPrivateKey);
            Adapay.initWithMerConfig(merConfig);

            /**
             * 初始化完成，可以开始交易
             */
            String appId = "app_03ba7272-034c-43ec-a423-c25b4b4ab9f3";
//            System.out.println("=======execute payment begin=======");
            //创建支付对象的参数，全部参数请参考 https://docs.adapay.tech/api/04-trade.html#id3
            Map<String, Object> paymentParams = new HashMap<>(10);
            paymentParams.put("app_id", appId);
            paymentParams.put("order_no", orderId);
            paymentParams.put("pay_channel", "alipay_wap");
//            paymentParams.put("pay_amt", "0.01");
            paymentParams.put("pay_amt", money);

            paymentParams.put("goods_title", shopItem.getShopName());
            paymentParams.put("goods_desc", "购买商品");
            paymentParams.put("notifyUrl",
                "http://139.155.248.244/ttmy_admin/api/pay/callback/Adacloudpay");

            //调用sdk方法，创建支付，得到支付对象
            Map<String, Object> payment = new HashMap<>();
            try {
                System.out.println("支付交易，请求参数：" + JSON.toJSONString(paymentParams));
                payment = Payment.create(paymentParams);

            } catch (Exception e) {
                e.printStackTrace();
            }
            if (!"succeeded".equals(payment.get("status").toString())) {
                logger.error("第三方支付下单返回错误信息：{} - {}", payment.get("order_no"),
                    payment.get("status"));
                return new CommonResponse(PayConstants.PAY_ERROR,
                    payment.get("error_msg").toString());
            }

            // 将下单数据发送过去
            logger.debug("第三方支付下单返回数据：{}", payment);
            if (payment == null || payment.equals("")) {
                logger.error("第三方支付下单返回数据为空！");
                return new CommonResponse(PayConstants.PAY_ERROR, "请求下单错误！");
            }
            // 封装返回数据
            JSONObject object = new JSONObject();
            Map<String, Object> payment1 = JSON.parseObject(
                JSON.toJSONString(payment.get("expend")));
            object.put("code_url", payment1.get("pay_info"));
            logger.debug(object.toJSONString());
            return new CommonResponse(object.toJSONString());
        } catch (Exception e) {
            logger.error("第三方支付下单出错：");
            e.printStackTrace();
            return new CommonResponse(PayConstants.PAY_ERROR, e.getMessage());
        }

    }

    @Override
    public CommonResponse notify(HttpServletRequest request) {
        logger.debug("-- Ada支付 -- 第三方回调通知");

        SortedMap<String, Object> params = new TreeMap<>();
        Enumeration parameterNames = request.getParameterNames();
        while (parameterNames.hasMoreElements()) {
            Object key = parameterNames.nextElement();
            params.put(key.toString(), request.getParameter((String) key));
            logger.info(key.toString() + request.getParameter((String) key));
        }
        String data = params.get("data").toString();
        String sign = params.get("sign").toString();
        boolean checkSign = false;
        //验签请参publicKey
        String publicKey = AdapayCore.PUBLIC_KEY;
        //验签
        try {
            checkSign = AdapaySign.verifySign(data, sign, publicKey);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (checkSign && "payment.succeeded".equals(params.get("type").toString())) {
            //验签成功逻辑
            Map<String, Object> map = JSON.parseObject(data, HashMap.class);
            String orderNo = (String) map.get("order_no");
            Double money = Double.parseDouble((String) map.get("pay_amt"));
            // 发送通知回调给游戏服务器
            CommonResponse notifyResponse = payService.sendNotifyToGameServer(orderNo,
                (double) money, 1);
            if (!notifyResponse.getSuccess()) {
                logger.error("Ada支付回调通知游戏服务器错误 订单号:{} 错误信息:{}", orderNo,
                    notifyResponse.getErrMsg());
                return new CommonResponse(notifyResponse.getErrCode(), notifyResponse.getErrMsg());
            }
            logger.debug("订单号：" + orderNo + " --> 交易失败");
            // 回调处理成功
            logger.debug("Ada支付回调处理成功：{}", orderNo);
            // 接口方定义的返回 ok 为商户处理回调成功
            return new CommonResponse("ok");
        } else {
            //验签失败逻辑
            logger.error("支付失败：{}", params.get("code"));
            return new CommonResponse("ERROR_PAY_FAIL", "订单支付失败");
        }
    }

    public static void main(String[] args) throws Exception {
        String money = String.format("%.2f", 0.01);//注意金额是以元为单位。

        /**
         * debug 模式，开启后有详细的日志
         */
        Adapay.debug = true;

        /**
         * prodMode 模式，默认为生产模式，false可以使用mock模式
         */
        Adapay.prodMode = true;

        /**
         * 初始化商户配置，服务器启动前，必须通过该方式初始化商户配置完成
         * apiKey为prod模式的API KEY
         * mockApiKey为mock模式的API KEY
         * rsaPrivateKey为商户发起请求时，用于请求参数加签所需要的RSA私钥
         */
        String apiKey = "api_live_ebb4c96c-945a-4214-aa9e-96a2284d5406";
        String mockApiKey = "api_test_9eb44b14-9183-4412-a191-46af6b9fa52d";
        String rsaPrivateKey = "MIICdQIBADANBgkqhkiG9w0BAQEFAASCAl8wggJbAgEAAoGBAKUdpcTY5nzwPRKn4HqzEb6Dt57PKwnEA3hP+kMRrsGqTo9mew6ZdHyfDa/oTNPQKmD8xYbmW4M2dgsAEeWH8B/2sg5nqIQ64GdkCO6r31NBrR5DhX6ms/l4z+JL4mDWSAOjOpT0amDioohNaJynEHl2A4RRogBuC5udY1L1GrlRAgMBAAECgYBehiB2Fm2srKNp4K1D0Ouhkyo1QggzaQFLiQ2OA7t1pxeeRF7CNttW1H8zattAjvUT3OD/nzRYY38kQP/91NqDpnsQw/0SEjAidEfwZj1KmLt00cx1TSvyisyLutVINoPSzbANN73UTHePFKR/+TScp/iXG2iB/lWvyxy66gyZeQJBANDjUnaqiX+9GrJNVxcbZ/Fm3ZroxYSCza9HvJUshw4K5Rrjgj0wyvTrfeBtQW78GnjEwULFHGOJkHH0kdEPBycCQQDKWwc5NkWhLih1j7oJu+cftIWJ/mY7XKNT9RYCsFEOc4GgDWpNXBWel4iMQ12+5+Ir+hV/j4SCAFt9OlZEfMbHAkB+Q0oPTJn8SpQefr1LzFcSBfmhr13k8SPe9V+6U8X26QL/M9H/psSnMslNpPzOVzixE002TOsSB472Mr0JPyo1AkAWoFcObqMagq7Ddm625+vP/79uzqNfv9wDZ0QuyMhHdWLpIpbgT4ubUBZUmLPCxOrlP/FncgS/BIs2VW+P+OPBAkBZ1iNceCaeUCEhS85xprSK7YvoWRiYqEsaQOaH0vko8iURO3VlMqiI9b2IlqQ36sh3fM/VokeXS10W5/iGCXCd";
        MerConfig merConfig = new MerConfig();
        merConfig.setApiKey(apiKey);
        merConfig.setApiMockKey(mockApiKey);
        merConfig.setRSAPrivateKey(rsaPrivateKey);
        Adapay.initWithMerConfig(merConfig);

        /**
         * 初始化完成，可以开始交易
         */
        String appId = "app_03ba7272-034c-43ec-a423-c25b4b4ab9f3";
//            System.out.println("=======execute payment begin=======");
        //创建支付对象的参数，全部参数请参考 https://docs.adapay.tech/api/04-trade.html#id3
        Map<String, Object> paymentParams = new HashMap<>(10);
        paymentParams.put("app_id", appId);
        paymentParams.put("order_no", "jsdk_payment_" + System.currentTimeMillis());
        paymentParams.put("pay_channel", "wx_pub");
        paymentParams.put("open_id", "oiKnEv-ShP6FTLETFTwfkJQuKVnI");
        paymentParams.put("is_raw", "1");
        paymentParams.put("pay_amt", "0.01");
//        paymentParams.put("pay_amt", money);

        paymentParams.put("goods_title", "测试");
        paymentParams.put("goods_desc", "购买商品");
        paymentParams.put("notifyUrl",
            "http://139.155.248.244/ttmy_admin/api/pay/callback/Adacloudpay");

        //调用sdk方法，创建支付，得到支付对象
        Map<String, Object> payment = new HashMap<>();
        try {
            System.out.println("支付交易，请求参数：" + JSON.toJSONString(paymentParams));
            payment = Payment.create(paymentParams);

        } catch (Exception e) {
            e.printStackTrace();
        }

        // 将下单数据发送过去
        System.out.println(payment);
        // 封装返回数据
        JSONObject object = new JSONObject();
        Map<String, Object> payment1 = JSON.parseObject(JSON.toJSONString(payment.get("expend")));
        object.put("code_url", payment1.get("pay_info"));
    }
}
