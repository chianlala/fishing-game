package com.jeesite.modules.api.pay.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jeesite.modules.api.pay.config.ThirdPayConfig;
import com.jeesite.modules.api.pay.domain.ShopItem;
import com.jeesite.modules.api.pay.service.PayService;
import com.jeesite.modules.api.pay.service.ThirdPayService;
import com.jeesite.modules.api.pay.util.PayConstants;
import com.jeesite.modules.api.pay.util.SignUtils;
import com.jeesite.modules.osee.vo.CommonResponse;
import com.jeesite.modules.util.CommonUtils;
import com.jeesite.modules.util.HttpUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * 第三方支付服务实现层-新生支付
 *
 * @author zjl
 */
@Service("jxPay")
public class ThirdPayServiceJxImpl implements ThirdPayService {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private ThirdPayConfig thirdPayConfig;

    @Autowired
    private PayService payService;

    @Override
    public CommonResponse unifiedorder(String orderId, String bankCode, ShopItem shopItem) {
        try {
            logger.debug("第三方支付订单下单开始");
            SortedMap<String, Object> params = new TreeMap<>();
            // 商户ID
            params.put("mchid", Integer.parseInt(thirdPayConfig.getMerId()));
            // 时间戳(10位秒)
            params.put("timestamp", String.valueOf(System.currentTimeMillis() / 1000));
            // 随机码
            params.put("nonce", CommonUtils.createNonceStr(-1));

            // 业务参数对象
            SortedMap<String, Object> dataMap = new TreeMap<>();
            // 支付类型
            dataMap.put("paytype", Integer.valueOf(bankCode));
            // 商户订单号
            dataMap.put("out_trade_no", orderId);
            // 商品名称
            dataMap.put("goodsname", shopItem.getShopName());
            // 支付金额，如0.01，以字符格式保留两位小数：元
            dataMap.put("total_fee", String.format("%.2f", shopItem.getPayMoney()));
            dataMap.put("notify_url", thirdPayConfig.getNotifyUrl());
            dataMap.put("return_url", thirdPayConfig.getCallbackUrl());
            // 终端用户发起请求IP
            dataMap.put("requestip", CommonUtils.getLocalIp());

            // 对数据进行签名 签名信息，MD5 后 32 位小写
            SortedMap<String, Object> signMap = new TreeMap<>();
            signMap.putAll(params);
            signMap.putAll(dataMap);
            String sign = SignUtils.createSign(signMap, thirdPayConfig.getSecretKey(), false);
            params.put("sign", sign);
            params.put("data", dataMap);
            // 参数转换为JSON格式
            String paramsJson = JSON.toJSONString(params);
            logger.debug("第三方支付下单请求数据[{}]", paramsJson);

            // 将下单数据发送过去
            String result = HttpUtil.doPostJson(thirdPayConfig.getPayUrl(), paramsJson);
            logger.debug("第三方支付下单返回数据：{}", result);
            if (result == null || result.equals("")) {
                logger.error("第三方支付下单返回数据为空");
                return new CommonResponse(PayConstants.PAY_ERROR, "请求下单错误");
            }
            JSONObject jsonObject = JSON.parseObject(result);
            int status = jsonObject.getIntValue("error");
            // 0代表成功,其他代表失败
            if (status != 0) {
                logger.error("第三方支付下单返回错误信息：{} - {}", status,
                    jsonObject.getString("msg"));
                return new CommonResponse(PayConstants.PAY_ERROR, jsonObject.getString("msg"));
            }

            // 获取成功返回的数据
            JSONObject data = jsonObject.getJSONObject("data");
            // 获取支付调起的链接
            String pay_url = data.getString("payurl");
            // 封装返回数据
            JSONObject object = new JSONObject();
            object.put("pay_url", pay_url);
            logger.debug("第三方支付下单成功，订单号[{}]", orderId);
            return new CommonResponse(object.toJSONString());
        } catch (Exception e) {
            logger.error("第三方支付下单出错：");
            e.printStackTrace();
            return new CommonResponse(PayConstants.PAY_ERROR, e.getMessage());
        }
    }

    @Override
    public CommonResponse notify(HttpServletRequest request) {
        try {
            logger.debug("第三方支付回调通知处理开始");
            BufferedReader streamReader = new BufferedReader(
                new InputStreamReader(request.getInputStream(), StandardCharsets.UTF_8));
            StringBuilder responseStrBuilder = new StringBuilder();
            String inputStr;
            while ((inputStr = streamReader.readLine()) != null) {
                responseStrBuilder.append(inputStr);
            }
            JSONObject responseJson = JSON.parseObject(responseStrBuilder.toString());
            logger.debug("第三方支付回调通知数据[{}]", responseJson.toJSONString());
            SortedMap<String, Object> params = new TreeMap<>();
            params.put("trade_no", responseJson.getString("trade_no"));
            params.put("total_fee", responseJson.getString("total_fee"));
            params.put("out_trade_no", responseJson.getString("out_trade_no"));
            params.put("tradingfee", responseJson.getString("tradingfee"));
            params.put("paysucessdate", responseJson.getString("paysucessdate"));
            params.put("sign", responseJson.getString("sign"));
            // 开始校对签名
            String retSign = (String) params.remove("sign");
            String localSign = SignUtils.createSign(params, thirdPayConfig.getSecretKey(), false);
            if (!localSign.equals(retSign)) {
                // 签名不一致出错
                logger.error("第三方支付回调：签名错误");
                return new CommonResponse(PayConstants.PAY_ERROR, "签名错误");
            }
            // 订单内的支付金额：元
            Double payMoney = Double.valueOf(String.valueOf(params.get("total_fee")));
            // 订单号
            String tradeNo = (String) params.get("trade_no");
            // 发送通知回调给游戏服务器
            CommonResponse notifyResponse = payService.sendNotifyToGameServer(tradeNo,
                payMoney * 100, 1);
            if (!notifyResponse.getSuccess()) { // 通知游戏服务器失败
                logger.error("第三方支付回调通知游戏服务器错误 订单号:{} 错误信息:{}", tradeNo,
                    notifyResponse.getErrMsg());
                return new CommonResponse(notifyResponse.getErrCode(), notifyResponse.getErrMsg());
            }

            // 回调处理成功
            logger.debug("第三方支付回调处理成功：{}", tradeNo);
            // 接口方定义的返回 success 为商户处理回调成功
            return new CommonResponse("success");
        } catch (Exception e) {
            logger.error("第三方支付回调通知处理出错：");
            e.printStackTrace();
            return new CommonResponse(PayConstants.PAY_ERROR, "订单交易还未成功");
        }
    }
}
