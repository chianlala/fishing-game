package com.jeesite.modules.api.pay.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.jeesite.modules.api.pay.config.CloudPayConfig;
import com.jeesite.modules.api.pay.domain.ShopItem;
import com.jeesite.modules.api.pay.service.HyPayService;
import com.jeesite.modules.api.pay.service.PayService;
import com.jeesite.modules.api.pay.util.HttpUtils;
import com.jeesite.modules.api.pay.util.PayConstants;
import com.jeesite.modules.api.pay.util.Res;
import com.jeesite.modules.osee.config.ProjectConfig;
import com.jeesite.modules.osee.vo.CommonResponse;
import org.apache.http.message.BasicNameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import java.util.*;


@Service
public class HyPayServiceImpl implements HyPayService {

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
    public CommonResponse unifiedorder(String orderId, String isSao, ShopItem shopItem) {
        try {
            logger.debug("第三方支付订单下单开始");

            int money = (int) (shopItem.getPayMoney() * 100);//注意金额是以分为单位。

            String url = "https://pay.cnmobi.cn/pay/scancode/alipay";

            List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
            params.add(new BasicNameValuePair("name", shopItem.getShopName()));
            params.add(new BasicNameValuePair("total", String.valueOf(money)));
            params.add(new BasicNameValuePair("orderNo", orderId));

            params.add(new BasicNameValuePair("merchantNo", "8099209208852911"));
            params.add(new BasicNameValuePair("notifyUrl",
                "http://139.155.248.244/ttmy_admin/api/pay/callback/hycloudpay"));

            Res resultMap = HttpUtils.httpPostAddSign(url, params);
            resultMap = HttpUtils.parseRes(resultMap);
            Map a = new HashMap();
            if (resultMap.getCode() == 1) {
                a = (Map) resultMap.getResult();
            } else {
                logger.error("第三方支付下单返回错误信息：{} - {}", resultMap.getCode(),
                    resultMap.getMsg());
                return new CommonResponse(PayConstants.PAY_ERROR, resultMap.getMsg());
            }
            System.out.println(a.get("imgUrl"));

            // 将下单数据发送过去
            logger.debug("第三方支付下单返回数据：{}", resultMap);
            if (resultMap == null || resultMap.equals("")) {
                logger.error("第三方支付下单返回数据为空！");
                return new CommonResponse(PayConstants.PAY_ERROR, "请求下单错误！");
            }
            // 封装返回数据
            JSONObject object = new JSONObject();
            if (isSao.equals("0")) {
                object.put("code_url", a.get("codeUrl"));
            } else {
                object.put("code_url", a.get("imgUrl"));
            }
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
        logger.debug("-- 华移支付 -- 第三方回调通知");

        SortedMap<String, Object> params = new TreeMap<>();
        Enumeration parameterNames = request.getParameterNames();
        while (parameterNames.hasMoreElements()) {
            Object key = parameterNames.nextElement();
            params.put(key.toString(), request.getParameter((String) key));
            logger.info(key.toString() + request.getParameter((String) key));


        }
        if (params.get("code").toString().equals("1")) {
            String orderNo = (String) params.get("orderNo");
            int money = Integer.parseInt((String) params.get("total"));
            // 发送通知回调给游戏服务器
            CommonResponse notifyResponse = payService.sendNotifyToGameServer(orderNo,
                (double) money, 1);
            if (!notifyResponse.getSuccess()) {
                logger.error("第三方支付回调通知游戏服务器错误 订单号:{} 错误信息:{}", orderNo,
                    notifyResponse.getErrMsg());
                return new CommonResponse(notifyResponse.getErrCode(), notifyResponse.getErrMsg());
            }
            logger.debug("订单号：" + orderNo + " --> 交易失败");
            // 回调处理成功
            logger.debug("第三方支付回调处理成功：{}", orderNo);
            // 接口方定义的返回 ok 为商户处理回调成功
            return new CommonResponse("ok");
        } else {
            logger.error("支付失败：{}", params.get("code"));
            return new CommonResponse("ERROR_PAY_FAIL", "订单支付失败");
        }
    }
}
