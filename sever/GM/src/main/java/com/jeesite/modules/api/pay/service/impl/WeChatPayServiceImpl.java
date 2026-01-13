package com.jeesite.modules.api.pay.service.impl;

import com.jeesite.modules.api.pay.config.WeChatConfig;
import com.jeesite.modules.api.pay.domain.ShopItem;
import com.jeesite.modules.api.pay.service.PayService;
import com.jeesite.modules.api.pay.service.WeChatPayService;
import com.jeesite.modules.api.pay.util.PayConstants;
import com.jeesite.modules.api.pay.util.SignUtils;
import com.jeesite.modules.api.pay.vo.OrderRequestVO;
import com.jeesite.modules.osee.service.BaseService;
import com.jeesite.modules.osee.vo.CommonResponse;
import com.jeesite.modules.util.CommonUtils;
import com.jeesite.modules.util.HttpUtil;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * 微信支付服务层实现类
 *
 * @author zjl
 */
@Service
public class WeChatPayServiceImpl extends BaseService implements WeChatPayService {

    @Autowired
    private WeChatConfig weChatConfig;

    @Autowired
    private PayService payService;

    /**
     * 统一下单
     */
    @Override
    public CommonResponse unifiedorder(String orderId, OrderRequestVO orderRequest,
        ShopItem shopItem) {
        // 支付参数数据
        SortedMap<String, Object> params = new TreeMap<>();
        params.put("appid", weChatConfig.getAppId());
        params.put("mch_id", weChatConfig.getMchId());
        params.put("nonce_str", CommonUtils.createNonceStr(-1));
        params.put("sign_type", weChatConfig.getSignType());
        params.put("body", "商品购买-" + shopItem.getShopName());
        params.put("out_trade_no", orderId);
        // 订单总金额，单位为分
        params.put("total_fee", (int) (shopItem.getPayMoney() * 100));
        // 调用微信支付API的机器IP，支持IPV4和IPV6两种格式的IP地址。
        params.put("spbill_create_ip", CommonUtils.getLocalIp());
        // 支付结果通知回调地址
        params.put("notify_url", weChatConfig.getNotifyUrl());
        // 交易支付类型
        String tradeType = orderRequest.getTradeType();
        params.put("trade_type", tradeType);
        // 生成签名并放入请求参数列表
        params.put("sign", SignUtils.createSign(params, weChatConfig.getSecretKey()));

        String requestXml = CommonUtils.mapToXml(params);
        logger.info("微信支付下单请求数据 {}", requestXml);

        String responseXml = HttpUtil.doPostXml(weChatConfig.getUnifiedOrderUrl(), requestXml);
        logger.info("微信支付下单返回数据 {}", responseXml);

        if (StringUtils.isEmpty(responseXml)) {
            return new CommonResponse(PayConstants.PAY_ERROR, "下单请求响应为空！");
        }
        Map<String, Object> map = CommonUtils.xmlToMap(responseXml);
        // 判断通信是否成功
        if (map.get("return_code").equals(PayConstants.WECHAT_RETURN_CODE_SUCCESS)) {
            // 判断业务是否成功
            if (map.get("result_code").equals(PayConstants.WECHAT_RESULT_CODE_SUCCESS)) {
                // 验证返回签名
                String retSign = (String) map.get("sign");
                // 移除签名字段，不参与签名的计算
                map.remove("sign");
                SortedMap<String, Object> calSignMap = new TreeMap<>(map);
                // 计算出来的签名
                String calSign = SignUtils.createSign(calSignMap, weChatConfig.getSecretKey());
                if (!retSign.equals(calSign)) {
                    logger.error("微信支付下单返回数据签名出错 订单号:{}", orderId);
                    return new CommonResponse(PayConstants.PAY_ERROR, "返回数据签名错误");
                }
                SortedMap<String, Object> payMap = new TreeMap<>();
                if (tradeType.equals(PayConstants.TRADE_TYPE_WECHAT_APP)) { // App支付需要的参数数据
                    payMap.put("appid", weChatConfig.getAppId());
                    payMap.put("partnerid", weChatConfig.getMchId());
                    payMap.put("prepayid", String.valueOf(map.get("prepay_id")));
                    payMap.put("package", "Sign=WXPay");
                    payMap.put("noncestr", CommonUtils.createNonceStr(-1));
                    payMap.put("timestamp", String.valueOf(System.currentTimeMillis() / 1000));
                    payMap.put("sign", SignUtils.createSign(payMap, weChatConfig.getSecretKey()));
                }
                logger.info("微信支付下单成功[{}] 返回前端数据:{}", orderId, payMap.toString());
                return new CommonResponse(payMap);
            }
            logger.error("微信下单请求业务失败 订单号:{} {}:{}", orderId, map.get("err_code"),
                map.get("err_code_des"));
            return new CommonResponse((String) map.get("err_code"),
                (String) map.get("err_code_des"));
        }
        logger.error("微信下单请求通信失败 {}:{}", map.get("return_code"), map.get("return_msg"));
        return new CommonResponse((String) map.get("return_code"), (String) map.get("return_msg"));
    }

    @Override
    public CommonResponse notify(HttpServletRequest request) {
        try {
            Map<String, Object> notifyMap = CommonUtils.streamXmlToMap(request.getInputStream());
            logger.info("微信支付回调通知数据 {}", notifyMap.toString());

            // 判断通信是否成功
            if (notifyMap.get("return_code").equals(PayConstants.WECHAT_RETURN_CODE_SUCCESS)) {
                // 判断业务是否成功
                if (notifyMap.get("result_code").equals(PayConstants.WECHAT_RESULT_CODE_SUCCESS)) {
                    String retSign = (String) notifyMap.get("sign");
                    notifyMap.remove("sign");
                    SortedMap<String, Object> calSignMap = new TreeMap<>(notifyMap);
                    String calSign = SignUtils.createSign(calSignMap, weChatConfig.getSecretKey());
                    if (!retSign.equals(calSign)) {
                        logger.error("微信支付回调通知签名校验失败");
                        return new CommonResponse("ERROR_SIGN",
                            wechatNotifyReturnXML(PayConstants.WECHAT_RETURN_CODE_FAIL,
                                "签名校验失败"));
                    }
                    // 订单号
                    String outTradeNo = (String) notifyMap.get("out_trade_no");
                    // 支付金额：分
                    Double totalFee = Double.parseDouble(
                        String.valueOf(notifyMap.get("total_fee")));
                    // 发送通知回调给游戏服务器
                    CommonResponse notifyResponse = payService.sendNotifyToGameServer(outTradeNo,
                        totalFee, 1);
                    if (!notifyResponse.getSuccess()) { // 通知游戏服务器失败
                        logger.error("微信支付回调通知游戏服务器错误 订单号:{} 错误信息:{}",
                            outTradeNo, notifyResponse.getErrMsg());
                        return new CommonResponse(notifyResponse.getErrCode(),
                            wechatNotifyReturnXML(PayConstants.WECHAT_RETURN_CODE_FAIL,
                                notifyResponse.getErrMsg()));
                    }
                    return new CommonResponse(
                        wechatNotifyReturnXML(PayConstants.WECHAT_RETURN_CODE_SUCCESS, "OK"));
                }
                logger.error("微信支付回调通知业务错误 {}:{}", notifyMap.get("err_code"),
                    notifyMap.get("err_code_des"));
                return new CommonResponse((String) notifyMap.get("err_code"),
                    wechatNotifyReturnXML(PayConstants.WECHAT_RETURN_CODE_FAIL,
                        (String) notifyMap.get("err_code_des")));
            }
            logger.error("微信支付回调通知通信错误 {}:{}", notifyMap.get("return_code"),
                notifyMap.get("return_msg"));
            return new CommonResponse((String) notifyMap.get("return_code"),
                wechatNotifyReturnXML(PayConstants.WECHAT_RETURN_CODE_FAIL,
                    (String) notifyMap.get("return_msg")));
        } catch (IOException e) {
            logger.error("微信支付回调通知操作异常 {}", e.getMessage());
            return new CommonResponse("ERROR_NOTIFY",
                wechatNotifyReturnXML(PayConstants.WECHAT_RETURN_CODE_FAIL, "FAIL"));
        }
    }

    /**
     * 构造微信支付通知回调返回数据
     */
    private String wechatNotifyReturnXML(String returnCode, String returnMsg) {
        Document document = DocumentHelper.createDocument();
        Element rootElmt = document.addElement("xml");
        Element retCode = rootElmt.addElement("return_code");
        retCode.addCDATA(returnCode);
        Element retMsg = rootElmt.addElement("return_msg");
        retMsg.addCDATA(returnMsg);
        return document.asXML();
    }
}
