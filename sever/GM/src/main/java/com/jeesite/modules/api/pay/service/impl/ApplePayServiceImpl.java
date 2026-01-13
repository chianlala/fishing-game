package com.jeesite.modules.api.pay.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.hutool.jwt.JWT;
import com.jeesite.modules.api.pay.domain.ShopItem;
import com.jeesite.modules.api.pay.service.ApplePayService;
import com.jeesite.modules.api.pay.service.PayService;
import com.jeesite.modules.osee.vo.CommonResponse;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.ByteArrayInputStream;
import java.security.PublicKey;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Optional;

@Service
@Slf4j
public class ApplePayServiceImpl implements ApplePayService {

    @Resource
    PayService payService;

    /**
     * 下单接口
     */
    @Override
    public CommonResponse unifiedorder(String orderId, ShopItem shopItem) {

        return new CommonResponse(JSONUtil.createObj().set("orderId", orderId));

    }

    /**
     * 通知回调接口
     */
    @SneakyThrows
    @Override
    public CommonResponse notify(JSONObject jsonObject) {

        String signedPayloadStr = jsonObject.getStr("signedPayload");

        if (StrUtil.isBlank(signedPayloadStr)) {

            throw new RuntimeException("signedPayload不能为空");

        }

        JSONObject signedPayloadJson = getPayloads(signedPayloadStr);

        String signedTransactionInfoStr = signedPayloadJson.getJSONObject("data")
            .getStr("signedTransactionInfo");

        JSONObject signedTransactionInfoJson = getPayloads(signedTransactionInfoStr);

        String appAccountToken = signedTransactionInfoJson.getStr("appAccountToken");

        JSONObject appAccountTokenJson = JSONUtil.parseObj(appAccountToken);

        // 获取：商品 id
        Integer itemId = appAccountTokenJson.getInt("itemId");

        Optional<ShopItem> optionalShopItem =
            CommonPayServiceImpl.SHOP_ITEMS.stream().filter(item -> item.getId().equals(itemId))
                .findFirst();

        if (!optionalShopItem.isPresent()) { // 未找到对应id的商品

            log.info("502，商品ID有误：{}", itemId);
            return null;

        }

        // 要购买的商品信息
        ShopItem shopItem = optionalShopItem.get();

        // 获取：订单 id
        String orderId = appAccountTokenJson.getStr("orderId");

        if (StrUtil.isBlank(orderId)) {

            log.info("orderId为空");
            return null;

        }

        if (!"PURCHASE".equalsIgnoreCase(signedTransactionInfoJson.getStr("transactionReason"))) {

            log.info("不是交易成功的通知类型");
            return null;

        }

        return payService.sendNotifyToGameServer(orderId, shopItem.getPayMoney(), 1);

    }

    @SneakyThrows
    private JSONObject getPayloads(String signedPayload) {

        JWT jwt = JWT.of(signedPayload);

        String x5cStr = (String) jwt.getHeader("x5c");

        String x5c0 = JSONUtil.toList(x5cStr, String.class).get(0);

        byte[] x5c0Bytes = java.util.Base64.getDecoder().decode(x5c0);
        CertificateFactory fact = CertificateFactory.getInstance("X.509");
        X509Certificate cer = (X509Certificate) fact.generateCertificate(
            new ByteArrayInputStream(x5c0Bytes));

        PublicKey publicKey = cer.getPublicKey();

        jwt.setKey(publicKey.getEncoded());

        // TODO：需要验证 x5c整个证书链
        boolean verify = jwt.verify();

        JSONObject payloads = jwt.getPayloads();

        log.info("苹果支付，验签：{}，payloads：{}", verify, payloads);

        return payloads;

    }

}
