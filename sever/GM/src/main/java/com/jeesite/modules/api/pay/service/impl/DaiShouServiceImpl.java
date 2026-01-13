package com.jeesite.modules.api.pay.service.impl;

import cn.hutool.core.text.StrBuilder;
import cn.hutool.crypto.digest.MD5;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayConstants;
import com.alipay.api.internal.util.AlipaySignature;
import com.jeesite.modules.api.pay.domain.ShopItem;
import com.jeesite.modules.api.pay.service.DaiShouService;
import com.jeesite.modules.api.pay.service.PayService;
import com.jeesite.modules.api.pay.util.PayConstants;
import com.jeesite.modules.api.pay.vo.OrderRequestVO;
import com.jeesite.modules.osee.vo.CommonResponse;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.net.URLEncoder;
import java.util.*;

@Service
public class DaiShouServiceImpl implements DaiShouService {
    @Resource
    PayService payService;
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public CommonResponse notify(HttpServletRequest request, JSONObject jsonObject) {
        logger.debug("执行美国美元的回调方法......");
        List<String> keyList = new ArrayList<>(jsonObject.keySet());
        Collections.sort(keyList);
        // key1=value1&key2=value2
        StrBuilder strBuilder = StrBuilder.create();

        for (int i = 0; i < keyList.size(); i++) {
            String key = keyList.get(i);
            if (key.equals("sign")) {
                continue;
            }
            if (i != 0) {
                strBuilder.append("&");
            }
            strBuilder.append(key).append("=").append(jsonObject.get(key));
        }
        strBuilder.append("97a8df4fadfb613f0b4f0611c7dfc826");
        String signPre = strBuilder.toString();
        logger.info("signPre-->【{}】",signPre);
        String sign = MD5.create().digestHex(signPre).toLowerCase();
        logger.info("sign-->【{}】",sign);

        String signCheck = jsonObject.getStr("sign");
        logger.info("signCheck-->【{}】",signCheck);
//        sign = signCheck;
        if(!sign.equals(signCheck)){
            // 验签失败
            CommonResponse response = new CommonResponse("fail");
            response.setSuccess(false);
            response.setErrMsg("fail");
            return response;
        }
        //发货
        payService.sendNotifyToGameServer(jsonObject.getStr("order_no"), jsonObject.getDouble("order_realityamount") ,1);
        CommonResponse response = new CommonResponse(true);
        return response;

    }

    @Override
    public CommonResponse unifiedorder(String orderId, ShopItem shopItem, OrderRequestVO orderRequest) {
        logger.debug("美国网银支付订单开始!");
        String phone = orderRequest.getPayphone();
        if (StringUtils.isBlank(phone)) {
            logger.debug("美国网银支付失败，手机号码不可为空!");
            return new CommonResponse(PayConstants.WECHAT_RESULT_CODE_FAIL, "支付失败，手机号码不可为空！");
        }
        try {
            Map<String, String> payMap = new HashMap<>();
            String money = String.format("%.2f", shopItem.getPayMoney()); // 注意金额是以元为单位。
            payMap.put("mer_no", "1082775");
            payMap.put("order_no", orderId);
            payMap.put("order_amount", "100.00"); // 交易金额
            payMap.put("payname", "xiaoming");
            payMap.put("payemail", "xiaoming@email.com");
            payMap.put("payphone", phone);
            payMap.put("currency", "EGP");
            payMap.put("paytypecode", "26001");
            payMap.put("method", "trade.create");
            payMap.put("returnurl", "http://119.45.233.212:8080/ttmy_admin/api/pay/callback/pnsafepay");
            List<String> keyList = new ArrayList<>(payMap.keySet());
            Collections.sort(keyList);
            // key1=value1&key2=value2
            StrBuilder strBuilder = StrBuilder.create();
            for (int i = 0; i < keyList.size(); i++) {
                if (i != 0) {
                    strBuilder.append("&");
                }
                String key = keyList.get(i);
                strBuilder.append(key).append("=").append(payMap.get(key));
            }
            strBuilder.append("97a8df4fadfb613f0b4f0611c7dfc826");
            String signPre = strBuilder.toString();
            logger.info("signPre------>>>>【{}】",signPre);
            String sign = MD5.create().digestHex(signPre).toLowerCase();
            logger.info("簽名----》【{}】",sign);
            payMap.put("sign",sign);
            String resultStr = HttpUtil.post("http://api.pnsafepay.com/gateway.aspx", JSONUtil.toJsonStr(payMap), 10 * 1000);
            logger.info("------>[{}]",resultStr);
            return new CommonResponse(resultStr);
        } catch (Exception e) {
            e.printStackTrace();
            return new CommonResponse("status", PayConstants.WECHAT_RESULT_CODE_FAIL);
        }
    }


    public String sortAndSecret(List<String> keyList){


        return null;
    }

    public String sortByAsc(Map<String, String> payMap) {
        //将逗号换成&
        List<String> keys = new ArrayList<>(payMap.keySet());
        Collections.sort(keys);//不按首字母排序, 需要按首字母排序请打开
        StringBuilder preStrStringBuilder = new StringBuilder();
        for (int i = 0; i < keys.size(); i++) {
            String key = keys.get(i);
            Object o = payMap.get(key);
            //  String encode = URLEncoder.encode(map.get(key).toString(), "UTF-8");
            if (i == keys.size() - 1) {// 拼接时，不包括最后一个&字符
                preStrStringBuilder.append(key).append("=").append(o);
            } else {
                preStrStringBuilder.append(key).append("=").append(o).append("&");
            }
        }
        return preStrStringBuilder.toString();
    }

    public String toSign(String content) throws AlipayApiException {

        return AlipaySignature.rsaSign(content,
                "c3fccca80c99675dc6a8f92b079b3905",
                "utf-8", AlipayConstants.SIGN_TYPE_RSA2);

    }
}
