package com.jeesite.modules.api.pay.service.impl;

import com.jeesite.modules.api.pay.domain.ShopItem;
import com.jeesite.modules.api.pay.service.HuaWeiService;
import com.jeesite.modules.api.pay.service.PayService;
import com.jeesite.modules.api.pay.util.PayConstants;
import com.jeesite.modules.api.pay.vo.OrderRequestVO;
import com.jeesite.modules.osee.vo.CommonResponse;
import org.apache.http.util.TextUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.*;

@Service
public class HuaWeiServiceImpl implements HuaWeiService {

    @Autowired
    private PayService payService;

    /**
     * 支付公钥
     */
    public static final String HW_PAY_PSECRET = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAutYx8NfJhLGEVmV4uxANGtkdqWF04kCXN0glCW3ikWWdOsZW2swQS0cphk+h0QUWScOkWvYtnZ8Lgv2+LuJJTcI9plQQtdNaQq86mPnUMH/anBy0/f+ZLqJGMArpbdQ4FUwR+7eKBtugJoMKMtyzbxjxj5MuXmtNcFr522ZUAa2/y4I4tU+XAi9zsawK5fvUou/yJKeg2cg9N1Kq/wcRG7sqjX5utPkqXAgJ2yaR7LHPmBj5QbvnVPr4vYlDRIiUgwu5AIrlmaTNiPNwm/NC3ogrzzU3xe3R/eoYMZ84MehL+u9uTt02QLBAi+gvw0joq1IQD/x39gjmdBeSt4TXAwIDAQAB";


    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public CommonResponse notify(HttpServletRequest request) throws IOException {

        logger.debug("----------------------华为-----------------------------:" + request);
        SortedMap<String, Object> params = new TreeMap<>();
        Enumeration parameterNames = request.getParameterNames();
        logger.info("----------------------华为-----------------------------:" + parameterNames);
        while (parameterNames.hasMoreElements()) {
            Object key = parameterNames.nextElement();
            params.put(key.toString(), request.getParameter((String) key));
            logger.info("----------------------华为-----------------------------:" + key.toString()
                + request.getParameter((String) key));
        }
//        String content = getNoSign(params,true);
//        boolean isSign = doCheck(content,String.valueOf(params.get("sign")),HW_PAY_PSECRET,String.valueOf(params.get("signType")));
//        if(!isSign){
//            // 签名不一致出错
//            logger.error("华为支付回调：签名错误！");
//            return new CommonResponse(PayConstants.PAY_ERROR, "签名错误");
//        }
        // 订单内的支付金额：元
        Double payMoney = Double.valueOf(String.valueOf(params.get("amount")));
        // 订单号
        String orderno = (String) params.get("orderId");
        String exReserved = (String) params.get("extReserved");
//        String[] s = exReserved.split(",");
//        String playerId = s[0];
//        int itemId = Integer.parseInt(s[1]);
        OrderRequestVO orderRequestVO = new OrderRequestVO();
        orderRequestVO.setPayType(PayConstants.PAY_TYPE_HUAWEI);
        orderRequestVO.setPlayerId(Long.parseLong(exReserved));
        orderRequestVO.setTradeType("APP");
        orderRequestVO.setItemId(1);
        int showCount = Integer.parseInt(String.valueOf(params.get("productName"))
            .substring(String.valueOf(params.get("productName")).indexOf("_") + 1));
        String showName = String.valueOf(params.get("productName"))
            .substring(0, String.valueOf(params.get("productName")).indexOf("_") + 1);
        Integer showType = 1;
        if ("金币".equals(showName)) {
            showType = 1;
        } else if ("钻石".equals(showName)) {
            showType = 4;
        } else if ("奖券".equals(showName)) {
            showType = 3;
        }
        ShopItem shopItem = ShopItem.createHuaweiItem(1, payMoney,
            String.valueOf(params.get("productName")), showType, showCount);
        CommonResponse notifyResponse = payService.sendHwOrderInfoToGameServer(orderno,
            orderRequestVO, shopItem, String.valueOf(params.get("result")));
        if (!notifyResponse.getSuccess()) { // 通知游戏服务器失败
            logger.error("华为支付回调通知游戏服务器错误 订单号:{} 错误信息:{}", orderno,
                notifyResponse.getErrMsg());
            return new CommonResponse(notifyResponse.getErrCode(), notifyResponse.getErrMsg());
        }
        // 接口方定义的返回 ok 为商户处理回调成功
        return new CommonResponse("ok");
    }

    /**
     * 根据参数map获取待签名字符串
     *
     * @param params            待签名参数map
     * @param includeEmptyParam 是否包含值为空的参数： 与HMS-SDK支付能力交互的签名或验签，需要为false（不包含空参数）
     *                          由华为支付服务器回调给开发者的服务器的支付结果验签，需要为true（包含空参数）
     * @return 待签名字符串
     */
    private static String getNoSign(Map<String, Object> params, boolean includeEmptyParam) {
        StringBuilder content = new StringBuilder();
        // 按照key做排序
        List<String> keys = new ArrayList<String>(params.keySet());
        Collections.sort(keys);
        String value = null;
        Object object = null;
        boolean isFirstParm = true;
        for (int i = 0; i < keys.size(); i++) {
            String key = (String) keys.get(i);
            object = params.get(key);

            if (object == null) {
                value = "";
            } else if (object instanceof String) {
                value = (String) object;
            } else {
                value = String.valueOf(object);
            }
            //拼接成key=value&key=value&....格式的字符串
            if (includeEmptyParam || !TextUtils.isEmpty(value)) {
                content.append((isFirstParm ? "" : "&") + key + "=" + value);
                isFirstParm = false;
            } else {
                continue;
            }
        }
        //待签名的字符串
        return content.toString();
    }

//    /**
//     * 校验签名信息
//     * @param  content 待校验字符串
//     * @param sign  签名字符串
//     * @param publicKey 公钥
//
//     * @param signType 加密类型
//     * @param 是否校验通过
//     */
//
//    public boolean doCheck(String content, String sign, String publicKey, String signtype) {
//
//        try {
//
//            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
//            byte[] encodedKey = Base64.decode(publicKey,Base64.DEFAULT);
//
//            PublicKey pubKey = keyFactory.generatePublic(new X509EncodedKeySpec(encodedKey));
//
//            java.security.Signature signature = null;
//
//            if ("RSA256".equals(signtype)) {
//                signature = java.security.Signature.getInstance(SIGN_SHA256RSA_ALGORITHMS);
//            } else {
//                signature = java.security.Signature.getInstance(SIGN_ALGORITHMS);
//            }
//
//            signature.initVerify(pubKey);
//
//            signature.update(content.getBytes("utf-8"));
//            boolean bverify = signature.verify(Base64.decode(sign,Base64.DEFAULT));
//            return bverify;
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return false;
//    }

    public static void main(String[] args) {
        String a = "141475";
        String[] s = a.split(",");
        String playerId = s[0];
        int itemId = Integer.parseInt(s[1]);
        System.out.println(playerId + "------------------------" + itemId);
    }
}
