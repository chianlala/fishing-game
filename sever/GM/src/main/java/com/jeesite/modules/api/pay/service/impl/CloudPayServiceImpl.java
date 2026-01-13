package com.jeesite.modules.api.pay.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jeesite.modules.api.pay.config.CloudPayConfig;
import com.jeesite.modules.api.pay.domain.ShopItem;
import com.jeesite.modules.api.pay.entity.Order;
import com.jeesite.modules.api.pay.service.CloudPayService;
import com.jeesite.modules.api.pay.service.PayService;
import com.jeesite.modules.api.pay.util.Helper;
import com.jeesite.modules.api.pay.util.PayConstants;
import com.jeesite.modules.api.pay.util.SignUtil;
import com.jeesite.modules.osee.config.ProjectConfig;
import com.jeesite.modules.osee.vo.CommonResponse;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;
import java.util.SortedMap;
import java.util.TreeMap;


@Service
public class CloudPayServiceImpl implements CloudPayService {

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
    public CommonResponse unifiedorder(String orderId, String bankCode, ShopItem shopItem,
        String playerIp) {
        try {
            logger.debug("第三方支付订单下单开始");
            String type;
            if (bankCode.equals("905")) {
                type = "pay_alipay_code";
            } else if (bankCode.equals("906")) {
                type = "pay_alipay_h5code";
            } else {
                type = "pay_wxpay_code";
            }

            int money = (int) (shopItem.getPayMoney() * 100);//注意金额是以分为单位。

            Order order = new Order(cloudPayConfig.getUser_id(),
                orderId,
                String.valueOf(money),
                type,
                "http://www.baidu.com",
                cloudPayConfig.getNotify_url(),
                shopItem.getShopName(),
                "RSA2",
                String.valueOf(true),
                String.valueOf(false),
                shopItem.getShopName(),
                playerIp
            );

            //空参数过滤并转为URL请求格式
            String signParams = Helper.filterNullParams(order, null);
            logger.debug(signParams);
            //RSA签名
            String sign = SignUtil.getSign("RSA_1_256", signParams);
            //所有value进行urlEncode
            String params =
                Helper.valueToUrlEncode(signParams) + "&ly_sign=" + URLEncoder.encode(sign);

            URL url = new URL(cloudPayConfig.req_url + "?" + params);

            logger.debug(url.toString());
            HttpURLConnection httpConnection = (HttpURLConnection) url.openConnection();

            InputStream inputStream = httpConnection.getInputStream();

//            byte[] bytes = new byte[1024];
//            while(inputStream.read(bytes) != -1) {
//                result = new String(bytes, "UTF-8");
//            }

            String result = IOUtils.toString(inputStream, StandardCharsets.UTF_8);

            // 将下单数据发送过去
            logger.debug("第三方支付下单返回数据：{}", result);
            if (result == null || result.equals("")) {
                logger.error("第三方支付下单返回数据为空！");
                return new CommonResponse(PayConstants.PAY_ERROR, "请求下单错误！");
            }
            JSONObject data = JSON.parseObject(result);
//            JSONObject data = jsonObject.getJSONObject("Data");

            boolean return_code = data.getBoolean("result_status");
            if (!return_code) {
                logger.error("第三方支付下单返回错误信息: {}", data.getString("result_msg"));
                return new CommonResponse(PayConstants.PAY_ERROR, data.getString("result_msg"));
            }

            // 封装返回数据
            JSONObject object = new JSONObject();
            String code_url = data.getString("qr_code");
            if (bankCode.equals("906")) {
                code_url = String.format(
                    "http://%s/%s/api/pay/payQrcode?payUrl=%s&payMoney=%f&payType=%d",
                    projectConfig.getServer(),
                    projectConfig.getCode(),
                    URLEncoder.encode(code_url, "utf-8"),
                    money / 100.0,
                    PayConstants.PAY_TYPE_ALI);

                logger.debug(code_url);
                object.put("code_url", code_url);
                return new CommonResponse(object.toJSONString());
            } else if (bankCode.equals("902")) {
                code_url = String.format(
                    "http://%s/%s/api/pay/payQrcode?payUrl=%s&payMoney=%f&payType=%d",
                    projectConfig.getServer(),
                    projectConfig.getCode(),
                    URLEncoder.encode(code_url, "utf-8"),
                    money / 100.0,
                    PayConstants.PAY_TYPE_WECHAT);

                logger.debug(code_url);
                object.put("code_url", code_url);
                return new CommonResponse(object.toJSONString());
            }

            object.put("code_url", code_url);

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

        logger.debug("-- 9a云支付 -- 第三方回调通知");

        SortedMap<String, Object> params = new TreeMap<>();
        Enumeration parameterNames = request.getParameterNames();
        while (parameterNames.hasMoreElements()) {
            Object key = parameterNames.nextElement();
            params.put(key.toString(), request.getParameter((String) key));
            logger.info(key.toString() + request.getParameter((String) key));
        }

        // 支付回调状态
        String status = (String) params.get("result_status");
        // 系统订单号
        String orderNo = (String) params.get("ly_user_order_no");
        // 9A云平台订单号
        String orderId = (String) params.get("ly_sys_order_no");
        int money = Integer.parseInt((String) params.get("ly_money"));
        if (status.equals("fail")) {
            logger.debug(
                "系统订单号：" + orderNo + "9A云支付平台订单号：" + orderId + " --> 交易失败");
        }

        // 发送通知回调给游戏服务器
        CommonResponse notifyResponse = payService.sendNotifyToGameServer(orderNo, (double) money,
            1);

        if (!notifyResponse.getSuccess()) {
            logger.error("第三方支付回调通知游戏服务器错误 订单号:{} 错误信息:{}", orderNo,
                notifyResponse.getErrMsg());
            return new CommonResponse(notifyResponse.getErrCode(), notifyResponse.getErrMsg());
        }

        // 回调处理成功
        logger.debug("第三方支付回调处理成功：{}", orderNo);
        // 接口方定义的返回 ok 为商户处理回调成功
        return new CommonResponse("ok");
    }
}
