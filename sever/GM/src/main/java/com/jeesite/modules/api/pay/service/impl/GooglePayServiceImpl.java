package com.jeesite.modules.api.pay.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.jeesite.modules.api.pay.domain.ShopItem;
import com.jeesite.modules.api.pay.service.GooglePayService;
import com.jeesite.modules.api.pay.service.PayService;
import com.jeesite.modules.model.bo.SysPayGooglePurchasesBO;
import com.jeesite.modules.osee.vo.CommonResponse;
import com.jeesite.modules.util.MySettingUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

@Slf4j
@Service
public class GooglePayServiceImpl implements GooglePayService {

    @Resource
    PayService payService;

    /**
     * 统一下单
     */
    @Override
    public CommonResponse unifiedorder(String orderId, ShopItem shopItem) {

        return new CommonResponse(JSONUtil.createObj().set("orderId", orderId));

    }

    /**
     * 支付回调
     */
    @Override
    public CommonResponse notify(HttpServletRequest request) {

        String token = request.getParameter("token");

        if (StrUtil.isBlank(token)) {

            return new CommonResponse("500", "token不存在");

        }

        String productId = request.getParameter("productId");

        if (StrUtil.isBlank(productId)) {

            return new CommonResponse("500", "productId不存在");

        }

        String googlePayApiKey = MySettingUtil.SETTING.getStr("pay.google.apiKey");

        String packageName = MySettingUtil.SETTING.getStr("pay.google.packageName");

        // 查询：谷歌那边的订单状态，文档地址：https://developers.google.com/android-publisher/api-ref/rest/v3/purchases.products/get?hl=zh-cn
        // https://androidpublisher.googleapis.com/androidpublisher/v3/applications/{packageName}/purchases/products/{productId}/tokens/{token}
        String url = StrUtil.format(
            "https://androidpublisher.googleapis.com/androidpublisher/v3/applications/{}/purchases/products/{}/tokens/{}?key={}",
            packageName, productId, token, googlePayApiKey);

        String body = HttpRequest.get(url).timeout(30 * 60 * 1000).execute().body();

        SysPayGooglePurchasesBO sysPayGooglePurchasesBO = JSONUtil.toBean(body,
            SysPayGooglePurchasesBO.class);

        if (sysPayGooglePurchasesBO == null) {

            return new CommonResponse("500", "订单不存在");

        }

        if (sysPayGooglePurchasesBO.getConsumptionState() != 1) {

            return new CommonResponse("501", "商品未消耗");

        }

        JSONObject jsonObject = JSONUtil.parseObj(
            sysPayGooglePurchasesBO.getObfuscatedExternalAccountId());

        // 获取：商品 id
        Integer itemId = jsonObject.getInt("itemId");

        Optional<ShopItem> optionalShopItem =
            CommonPayServiceImpl.SHOP_ITEMS.stream().filter(item -> item.getId().equals(itemId))
                .findFirst();

        if (!optionalShopItem.isPresent()) { // 未找到对应id的商品
            return new CommonResponse("502", "商品ID有误");
        }

        // 要购买的商品信息
        ShopItem shopItem = optionalShopItem.get();

        // 获取：订单 id
        String orderId = jsonObject.getStr("orderId");

        return payService.sendNotifyToGameServer(orderId, shopItem.getPayMoney(), 1);

    }

}
