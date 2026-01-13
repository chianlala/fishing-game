package com.jeesite.modules.api.pay.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import com.alipay.api.AlipayConstants;
import com.alipay.api.internal.util.AlipaySignature;
import com.jeesite.modules.api.pay.domain.ShopItem;
import com.jeesite.modules.api.pay.service.JiaoYiLeService;
import com.jeesite.modules.api.pay.service.PayService;
import com.jeesite.modules.api.pay.util.PayConstants;
import com.jeesite.modules.model.dto.JiaoYiLePayRequest;
import com.jeesite.modules.model.vo.JiaoYiLePayResponse;
import com.jeesite.modules.osee.vo.CommonResponse;
import com.jeesite.modules.util.JiaoYiLePayUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Slf4j
@Service
public class JiaoYiLeServiceImpl implements JiaoYiLeService {

    private static final String SERVER_URL = "https://console.jiaoyile.cn/gateway/pay.order/create";
    private static final String ALIPAY_PUBLIC_KEY =
        "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAigLUoKx2TmHwgI4beRwgp07q7QncR+BQcAs/j4dtYH0W9Gq3pKwhmEerHUozEXjWtGuT119AzGlIY8UnXA+NRX7h04OEm6tpLAl6yVo3gLeSYrKIyjXPIa4S3mCzApXrjx4/KWNcvX0xK9ly9iV3R0nCoYb9ZR/HttS03zt+9JQ3MVTXm9kBUDnC6A13lnDXKO54tBoRIXKANTxoVi7uSwuim5qFofh6AowKEwyBDEvVnburdDo7ZL7FL0ntglA2NfUF2Viyby0HNMmcvbl6fbm+z5jXLcK++RNJQZoiTtvej6S19eH38ihll0cdUqtibO5oi9xzLyjEJq/bTexdQQIDAQAB";

    @Resource
    PayService payService;

    /**
     * 统一下单
     */
    @Override
    public CommonResponse unifiedorder(String orderId, ShopItem shopItem) {

        String money = String.format("%.2f", shopItem.getPayMoney()); // 注意金额是以元为单位。

        JiaoYiLePayRequest jiaoYiLePayRequest = new JiaoYiLePayRequest();
        jiaoYiLePayRequest.setOut_trade_no(orderId);
        jiaoYiLePayRequest.setSubject("商超百货");
        jiaoYiLePayRequest.setTotal_amount(money);

        try {

            String content = JiaoYiLePayUtils.toSort(jiaoYiLePayRequest);

            String sign = JiaoYiLePayUtils.toSign(content);
            content = content + "&sign=" + URLEncoder.encode(sign, "UTF-8");

            String result = HttpUtil.post(SERVER_URL, content, 10 * 1000);

            JiaoYiLePayResponse jiaoYiLePayResponse = JSONUtil.toBean(result,
                JiaoYiLePayResponse.class);

            if (StrUtil.isBlank(jiaoYiLePayResponse.getData().getPayurl())) {

                log.info("JiaoYiLe-result：{}", result);

            }

            return new CommonResponse(JSONUtil.createObj().set("code_url",
                    "alipays://platformapi/startapp?appId=20000067&url=" + jiaoYiLePayResponse.getData()
                        .getPayurl())
                .toString());

        } catch (Exception e) {

            e.printStackTrace();

            return new CommonResponse(PayConstants.PAY_ERROR, "获取支付页面失败，请联系管理员");

        }

    }

    /**
     * 支付回调
     */
    @Override
    public CommonResponse notify(HttpServletRequest request) {

        //        log.info("开始执行，支付回调 ↓");

        Map<String, String[]> requestParamsMap = request.getParameterMap();
        Map<String, String> paramsMap = new HashMap<>(requestParamsMap.size());
        for (String item : requestParamsMap.keySet()) {
            String[] values = requestParamsMap.get(item);
            String valueStr = "";
            for (int i = 0; i < values.length; i++) {
                valueStr =
                    (i == values.length - 1) ? valueStr + values[i] : valueStr + values[i] + ",";
            }
            paramsMap.put(item, valueStr);
        }

        //        log.info("requestParamsMap：{}", JSONUtil.toJsonStr(requestParamsMap));
        //        log.info("paramsMap：{}", JSONUtil.toJsonStr(paramsMap));

        // 移除：value为空字符串的 key
        Set<String> removeKeySet = new HashSet<>();

        for (Map.Entry<String, String> item : paramsMap.entrySet()) {
            if (StrUtil.isBlank(item.getValue())) {
                removeKeySet.add(item.getKey());
            }
        }

        for (String item : removeKeySet) {
            paramsMap.remove(item);
        }

        //        log.info("开始执行，支付回调，验证签名");

        // 调用SDK验证签名
        boolean signVerified = false;

        try {

            signVerified = AlipaySignature
                .rsaCheckV2(paramsMap, ALIPAY_PUBLIC_KEY, AlipayConstants.CHARSET_UTF8,
                    AlipayConstants.SIGN_TYPE_RSA2);

        } catch (Exception e) {

            e.printStackTrace();

        }

        if (signVerified) {

            // 商户订单号
            String outTradeNo = new String(
                request.getParameter("out_trade_no").getBytes(StandardCharsets.ISO_8859_1),
                StandardCharsets.UTF_8);
            // 付款金额
            String totalAmount = new String(
                request.getParameter("total_amount").getBytes(StandardCharsets.ISO_8859_1),
                StandardCharsets.UTF_8);
            // 交易状态
            //            String tradeStatus = new String(request.getParameter("trade_status").getBytes(StandardCharsets.ISO_8859_1),
            //                StandardCharsets.UTF_8);
            String orderStatus = new String(
                request.getParameter("order_status").getBytes(StandardCharsets.ISO_8859_1),
                StandardCharsets.UTF_8);

            //            log.info("开始执行，支付回调，验签成功，outTradeNo：{}，totalAmount：{}，orderStatus：{}", outTradeNo, totalAmount,
            //                orderStatus);

            if ("SUCCESS".equals(orderStatus)) {

                // 发送通知回调给游戏服务器
                //                log.info("开始执行，验签成功，发送通知回调给游戏服务器");
                //                CommonResponse commonResponse =
                payService.sendNotifyToGameServer(outTradeNo,
                    new BigDecimal(totalAmount).doubleValue(), 1);
                //                log.info("commonResponse：{}", JSONUtil.toJsonStr(commonResponse));

            }

            return new CommonResponse("success");

        } else {

            //            return new CommonResponse("failure");
            //            log.info("验签失败");
            return new CommonResponse("fail");

        }

    }

}
