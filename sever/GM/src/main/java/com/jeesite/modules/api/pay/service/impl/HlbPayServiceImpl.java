package com.jeesite.modules.api.pay.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.jeesite.modules.api.pay.config.CloudPayConfig;
import com.jeesite.modules.api.pay.config.MyConfig;
import com.jeesite.modules.api.pay.domain.ShopItem;
import com.jeesite.modules.api.pay.service.HlbPayService;
import com.jeesite.modules.api.pay.service.PayService;
import com.jeesite.modules.api.pay.util.HttpsMain;
import com.jeesite.modules.api.pay.util.PayConstants;
import com.jeesite.modules.api.pay.util.XmlSignUtil;
import com.jeesite.modules.api.pay.util.XmlUtil;
import com.jeesite.modules.osee.config.ProjectConfig;
import com.jeesite.modules.osee.vo.CommonResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


@Service
public class HlbPayServiceImpl implements HlbPayService {

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
    public CommonResponse unifiedorder(String orderId, ShopItem shopItem) throws Exception {

        logger.debug("第三方支付订单下单开始");
        String function = "ant.mybank.bkmerchanttrade.dynamicOrder";
        XmlUtil xmlUtil = new XmlUtil();
        Map<String, String> form = new HashMap<>();
        form.put("OutTradeNo", orderId);
        form.put("Goodsid", null);
        form.put("Body", shopItem.getShopName());
        form.put("TotalAmount",
            String.valueOf(new Double(shopItem.getPayMoney()).longValue() * 100));
//        form.put("TotalAmount","1");
        form.put("Currency", "CNY");
        form.put("HlMerchantId", MyConfig.hlMerchantId);
        form.put("IsvOrgId", MyConfig.isvOrgId);
        //微信
//        form.put("ChannelType", "WX");
//        支付宝
        form.put("ChannelType", "ALI");
        form.put("DeviceCreateIp", "127.0.0.1");
        form.put("SettleType", "T1");
        form.put("NotifyUrl", "http://4139.155.248.244/ttmy_admin/api/pay/callback/hlbcloudpay");
        form.put("ProviderType", "09");
//        form.put("ChannelId","270951597");   //网商云资金旧微信渠道号：270951597  ， 新：270908432
//        form.put("SubMerchId","288136096");
//        form.put("SpecifySubMerchId","Y");

        //可选参数
//        form.put("OperatorId", "test");
//        form.put("StoreId", "test");
//        form.put("DeviceId", "test");
//        form.put("ExpireExpress", "1440");
//        form.put("Attach", "test");

        form.put("Function", function);
        form.put("ReqTime", new Timestamp(System.currentTimeMillis()).toString());
        //reqMsgId每次报文必须都不一样
        form.put("ReqMsgId", UUID.randomUUID().toString());

        //封装报文
        String param = xmlUtil.format(form, function);
        if (MyConfig.isSign) {//生产环境需进行rsa签名
            param = XmlSignUtil.sign(param, MyConfig.myRsaPrivateKey);
        }
        System.out.println(param);
        //发送请求
        String response = HttpsMain.httpsReq(HttpsMain.payUrl, param);
        System.out.println(response);
        if (MyConfig.isSign) {//生产环境需进行rsa验签
            if (!XmlSignUtil.verify(response, MyConfig.huiLianRsaPublicKey)) {
                throw new Exception("验签失败");
            }
        }
        //解析报文
        Map<String, Object> resMap = xmlUtil.parse(response, function);
        Map<String, Object> resMap1 = (Map) resMap.get("RespInfo");
        logger.info("resMap:{}", resMap);
        if (String.valueOf(resMap1.get("ResultStatus")).equals("S") && String.valueOf(
            resMap1.get("ResultMsg")).equals("成功")) {
            String payUrl = String.valueOf(resMap.get("QrCodeUrl"));
            logger.info("payUrl=" + payUrl);
            //封装返回数据
            JSONObject object = new JSONObject();
            object.put("code_url", payUrl);
            logger.debug(object.toJSONString());
            return new CommonResponse(object.toJSONString());
        } else {
            logger.info("拉起支付失败。。。");
            return new CommonResponse(PayConstants.PAY_ERROR,
                String.valueOf(resMap1.get("ResultMsg")));
        }
    }

    @Override
    public CommonResponse notify(HttpServletRequest request) {
        logger.debug("-- Hlb支付 -- 第三方回调通知");
        XmlUtil xmlUtil = new XmlUtil();
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(request.getInputStream()));
            String tempLine = "";
            StringBuffer resultBuffer = new StringBuffer();
            while ((tempLine = reader.readLine()) != null) {
                resultBuffer.append(tempLine).append(System.getProperty("line.separator"));
            }
            String response = resultBuffer.toString();
            response = response.replaceAll("\r\n", "");
            Map<String, Object> resMap = xmlUtil.parseReceive(response, "");
            if (resMap != null) {
                String orderNo = (String) resMap.get("OutTradeNo");
                int money = Integer.parseInt((String) resMap.get("TotalAmount"));
                // 发送通知回调给游戏服务器
                CommonResponse notifyResponse = payService.sendNotifyToGameServer(orderNo,
                    (double) money, 1);
                if (!notifyResponse.getSuccess()) {
                    logger.error("第三方支付回调通知游戏服务器错误 订单号:{} 错误信息:{}", orderNo,
                        notifyResponse.getErrMsg());
                    return new CommonResponse(notifyResponse.getErrCode(),
                        notifyResponse.getErrMsg());
                }
                // 回调处理成功
                logger.debug("第三方支付回调处理成功：{}", orderNo);
                // 接口方定义的返回 ok 为商户处理回调成功
                return new CommonResponse("ok");
            } else {
                return new CommonResponse("ERROR_PAY_FAIL", "订单支付失败");
            }
        } catch (Exception e) {
            return new CommonResponse("ERROR_PAY_FAIL", "订单支付失败");
        }
    }
}
