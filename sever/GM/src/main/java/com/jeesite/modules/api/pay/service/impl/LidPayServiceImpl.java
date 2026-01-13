package com.jeesite.modules.api.pay.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.jeesite.modules.api.pay.config.CloudPayConfig;
import com.jeesite.modules.api.pay.domain.ShopItem;
import com.jeesite.modules.api.pay.service.LidPayService;
import com.jeesite.modules.api.pay.service.PayService;
import com.jeesite.modules.api.pay.util.PayConstants;
import com.jeesite.modules.api.pay.util.PayH5Utils;
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
public class LidPayServiceImpl implements LidPayService {

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
            logger.debug("第三方支付订单下单开始");
            int money = (int) (shopItem.getPayMoney() * 100);//注意金额是以分为单位。
            Map<String, String> params = new HashMap<>();
            params.put("merchantNo", PayH5Utils.MERCHANT_NO);
            params.put("notifyUrl", PayH5Utils.NOTIFY_URL);
            params.put("returnUrl", PayH5Utils.RETURN_URL);
            params.put("payMethod", "6022");
            params.put("version", "v2.0");
            params.put("name", shopItem.getShopName());
            params.put("orderNo", orderId);
            params.put("total", String.valueOf(money));
            params.put("timestamp", String.valueOf(System.currentTimeMillis()));
            params.put("sign", PayH5Utils.getSign(params));

            String result = PayH5Utils.sendPostMessage(PayH5Utils.PAY_URL, params);
            if (result != null && !"".equals(result)) {
                @SuppressWarnings("unchecked")
                Map<String, Object> obj = JSONObject.parseObject(result, Map.class);
                int code = (int) obj.get("code");
                if (code == 1)// 成功
                {
                    @SuppressWarnings("unchecked")
                    Map<String, String> resultObj = (Map<String, String>) obj.get("result");
                    // 验证返回参数签名
                    if (PayH5Utils.checkParamSign(resultObj)) {
                        logger.info("签名验证通过,可以在此处理订单下一步操作:");
                        // 获取payUrl
                        String payUrl = resultObj.get("payUrl");
                        logger.info("payUrl=" + payUrl);
                        // 封装返回数据
                        JSONObject object = new JSONObject();
                        object.put("code_url", payUrl);
                        logger.debug(object.toJSONString());
                        return new CommonResponse(object.toJSONString());
                    } else {
                        logger.info("签名验证失败。。。");
                        return new CommonResponse(PayConstants.PAY_ERROR, "签名验证失败!");
                    }
                } else {
                    String msg = (String) obj.get("msg");
                    logger.info("失败，原因：" + msg);
                    return new CommonResponse(PayConstants.PAY_ERROR, msg);
                }
            } else {
                logger.info("服务器连接异常，请重试！");
                return new CommonResponse(PayConstants.PAY_ERROR, "服务器连接异常，请重试！");
            }
        } catch (Exception e) {
            logger.info("服务器连接异常，请重试！");
            return new CommonResponse(PayConstants.PAY_ERROR, "服务器连接异常，请重试！");
        }
    }

    @Override
    public CommonResponse notify(HttpServletRequest request) {
        logger.debug("-- Lid支付 -- 第三方回调通知");

        SortedMap<String, Object> params = new TreeMap<>();
        Enumeration parameterNames = request.getParameterNames();
        while (parameterNames.hasMoreElements()) {
            Object key = parameterNames.nextElement();
            params.put(key.toString(), request.getParameter((String) key));
            logger.info(key.toString() + request.getParameter((String) key));
        }
//        if((int)params.get("code")==1){
//            String orderNo = (String) params.get("orderNo");
//            int money = Integer.parseInt((String) params.get("total"));
//            // 发送通知回调给游戏服务器
//            CommonResponse notifyResponse = payService.sendNotifyToGameServer(orderNo, (double) money, 1);
//            if(!notifyResponse.getSuccess()) {
//                logger.error("第三方支付回调通知游戏服务器错误 订单号:{} 错误信息:{}", orderNo, notifyResponse.getErrMsg());
//                return new CommonResponse(notifyResponse.getErrCode(), notifyResponse.getErrMsg());
//            }
//            logger.debug( "订单号：" + orderNo + " --> 交易失败");
//            // 回调处理成功
//            logger.debug("第三方支付回调处理成功：{}", orderNo);
//            // 接口方定义的返回 ok 为商户处理回调成功
        return new CommonResponse("ok");
//        }else{
//            logger.error("支付失败：{}",params.get("code"));
//            return new CommonResponse("ERROR_PAY_FAIL", "订单支付失败");
//        }
    }
}
