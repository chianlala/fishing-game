package com.jeesite.modules.api.pay.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jeesite.modules.api.pay.config.ThirdPayConfig;
import com.jeesite.modules.api.pay.domain.ShopItem;
import com.jeesite.modules.api.pay.service.PayService;
import com.jeesite.modules.api.pay.service.ThirdPayService;
import com.jeesite.modules.api.pay.util.PayConstants;
import com.jeesite.modules.api.pay.util.SignUtils;
import com.jeesite.modules.osee.config.ProjectConfig;
import com.jeesite.modules.osee.vo.CommonResponse;
import com.jeesite.modules.util.HttpUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * 第三方支付服务实现层-恒隆支付
 *
 * @author zjl
 */
@Service("hengLongPay")
public class ThirdPayServiceImpl implements ThirdPayService {

    private Logger logger = LoggerFactory.getLogger(getClass());

    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Autowired
    private ThirdPayConfig thirdPayConfig;

    @Autowired
    private PayService payService;

    @Autowired
    private ProjectConfig projectConfig;

    @Override
    public CommonResponse unifiedorder(String orderId, String bankCode, ShopItem shopItem) {
        logger.debug("第三方支付订单下单，订单号[{}]", orderId);
        try {
            SortedMap<String, Object> params = new TreeMap<>();
            params.put("pay_memberid", thirdPayConfig.getMerId());
            params.put("pay_orderid", orderId);
            params.put("pay_applydate", simpleDateFormat.format(new Date()));
            // 支付金额：元
            params.put("pay_amount", String.valueOf(shopItem.getPayMoney()));
            // 商品名称
            params.put("pay_productname", shopItem.getShopName());
            // 支付渠道
            params.put("pay_bankcode", bankCode);
            params.put("pay_notifyurl", thirdPayConfig.getNotifyUrl());
            params.put("pay_callbackurl", thirdPayConfig.getCallbackUrl());

            // 对数据进行签名
            String secretKey = thirdPayConfig.getSecretKey();
            String sign = SignUtils.createSign(params, secretKey);
            params.put("pay_md5sign", sign);

            logger.debug("第三方支付下单请求数据[{}]", params.toString());

            // 将下单数据发送过去
            String result = HttpUtil.doPostFormData(thirdPayConfig.getPayUrl(), params);
            logger.debug("第三方支付下单返回数据：{}", result);
            if (result == null || result.equals("")) {
                logger.error("第三方支付下单返回数据为空！");
                return new CommonResponse(PayConstants.PAY_ERROR, "请求下单错误！");
            }
            JSONObject jsonObject = JSON.parseObject(result);
            String status = jsonObject.getString("status");
            if (!status.equals("200")) {
                logger.error("第三方支付下单返回错误信息：{} - {}", status,
                    jsonObject.getString("errormsg"));
                return new CommonResponse(PayConstants.PAY_ERROR, jsonObject.getString("errormsg"));
            }

            // 获取成功返回的数据
            JSONObject data = jsonObject.getJSONObject("data");
            // 获取支付调起的链接
            String code_url = data.getString("QRCodeUrl");
            // 封装返回数据
            JSONObject object = new JSONObject();
            if (bankCode.equals("902") // 微信扫码支付
                || bankCode.equals("903") // 支付宝扫码支付
            ) {
                // 返回的是支付二维码网页
                code_url = String.format(
                    "http://%s/%s/api/pay/payQrcode?payUrl=%s&payMoney=%f&payType=%d",
                    projectConfig.getServer(),
                    projectConfig.getCode(),
                    URLEncoder.encode(code_url, "utf-8"),
                    data.getFloat("trade_amount"),
                    bankCode.equals("902") ? PayConstants.PAY_TYPE_WECHAT
                        : PayConstants.PAY_TYPE_ALI);
            } else {
                code_url = String.format("http://%s/%s/api/pay/payJump?payUrl=%s",
                    projectConfig.getServer(), projectConfig.getCode(),
                    URLEncoder.encode(code_url, "utf-8"));
                logger.error(code_url);
            }
            object.put("code_url", code_url);
            return new CommonResponse(object.toJSONString());
        } catch (Exception e) {
            logger.error("第三方支付下单返回处理错误：{}", e.getMessage());
            return new CommonResponse(PayConstants.PAY_ERROR, e.getMessage());
        }
    }

    @Override
    public CommonResponse notify(HttpServletRequest request) {
        logger.debug("第三方支付回调通知");

        SortedMap<String, Object> params = new TreeMap<>();
        Enumeration parameterNames = request.getParameterNames();
        while (parameterNames.hasMoreElements()) {
            Object key = parameterNames.nextElement();
            params.put(key.toString(), request.getParameter((String) key));
        }
        logger.info("回调参数------------------------:" + params);
        String returnCode = (String) params.get("returncode");
        if (!returnCode.equals("00")) {
            logger.error("第三方支付回调：交易还没有成功！");
            return new CommonResponse(PayConstants.PAY_ERROR, "订单交易还未成功！");
        }

        params.remove("attach");
        String retSign = (String) params.remove("sign");
        String localSign = SignUtils.createSign(params, thirdPayConfig.getSecretKey());
        if (!localSign.equals(retSign)) {
            // 签名不一致出错
            logger.error("第三方支付回调：签名错误！");
            return new CommonResponse(PayConstants.PAY_ERROR, "签名错误");
        }
        // 订单内的支付金额：元
        Double payMoney = Double.valueOf(String.valueOf(params.get("amount")));
        // 订单号
        String orderno = (String) params.get("orderid");
        // 发送通知回调给游戏服务器
        CommonResponse notifyResponse = payService.sendNotifyToGameServer(orderno, payMoney * 100,
            1);
        if (!notifyResponse.getSuccess()) { // 通知游戏服务器失败
            logger.error("第三方支付回调通知游戏服务器错误 订单号:{} 错误信息:{}", orderno,
                notifyResponse.getErrMsg());
            return new CommonResponse(notifyResponse.getErrCode(), notifyResponse.getErrMsg());
        }

        // 回调处理成功
        logger.debug("第三方支付回调处理成功：{}", orderno);
        // 接口方定义的返回 ok 为商户处理回调成功
        return new CommonResponse("ok");
    }
}
