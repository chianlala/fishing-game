package com.jeesite.modules.api.pay.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.jeesite.modules.api.pay.domain.ShopItem;
import com.jeesite.modules.api.pay.service.PayService;
import com.jeesite.modules.api.pay.vo.OrderRequestVO;
import com.jeesite.modules.osee.service.BaseService;
import com.jeesite.modules.osee.vo.CommonResponse;
import org.springframework.stereotype.Service;

/**
 * 支付服务层实现类
 *
 * @author zjl
 */
@Service
public class PayServiceImpl extends BaseService implements PayService {

    /**
     * 发送订单信息到游戏服务器保存
     */
    @Override
    public CommonResponse sendOrderInfoToGameServer(String orderId, OrderRequestVO orderRequest,
        ShopItem shopItem) {

        try {

            JSONObject object = new JSONObject();
            object.put("orderNum", orderId);
            object.put("playerId", orderRequest.getPlayerId());
            object.put("payMoney", shopItem.getPayMoney() * 100);
            object.put("shopName", shopItem.getShopName());
            object.put("shopType", shopItem.getShopType());
            object.put("shopCount", shopItem.getShopCount());
            object.put("rechargeType", orderRequest.getPayType());

            return restTemplate.postForObject(apiConfig.buildApiUrl("/osee/pay_order/add"),
                object.toJSONString(), CommonResponse.class);

        } catch (Exception e) {

            logger.error("发送支付订单信息到游戏服务器失败：{}", e.getMessage());
            return new CommonResponse("ERROR_SEND_ORDER_TO_GAME_SERVER",
                "发送订单信息到游戏服务器失败");

        }

    }

    /**
     * 发送订单信息到游戏服务器保存
     */
    @Override
    public CommonResponse sendHwOrderInfoToGameServer(String orderId, OrderRequestVO orderRequest,
        ShopItem shopItem, String result) {

        try {

            JSONObject object = new JSONObject();
            object.put("orderNum", orderId);
            object.put("playerId", orderRequest.getPlayerId());
            object.put("payMoney", shopItem.getPayMoney() * 100);
            object.put("shopName", shopItem.getShopName());
            object.put("shopType", shopItem.getShopType());
            object.put("shopCount", shopItem.getShopCount());
            object.put("rechargeType", orderRequest.getPayType());
            object.put("result", result);
            return restTemplate.postForObject(apiConfig.buildApiUrl("/osee/pay_order/hwadd"),
                object.toJSONString(), CommonResponse.class);

        } catch (Exception e) {

            logger.error("发送支付订单信息到游戏服务器失败：{}", e.getMessage());
            return new CommonResponse("ERROR_SEND_ORDER_TO_GAME_SERVER",
                "发送订单信息到游戏服务器失败");

        }

    }

    /**
     * 发送支付通知结果到游戏服务器 orderState 1:成功 2:失败 payMoney 订单支付的金额，单位为分
     */
    @Override
    public CommonResponse sendNotifyToGameServer(String orderId, Double payMoney,
        Integer orderState) {

        try {

            JSONObject object = new JSONObject();

            object.put("orderNum", orderId);
            object.put("payMoney", payMoney);
            object.put("orderState", orderState);

            return restTemplate.postForObject(apiConfig.buildApiUrl("/osee/pay_order/update"),
                object.toJSONString(), CommonResponse.class);

        } catch (Exception e) {

            logger.error("发送支付通知回调到游戏服务器失败：{}", e.getMessage());
            return new CommonResponse("ERROR_SEND_NOTIFY_TO_GAME_SERVER",
                "发送支付通知回调到游戏服务器失败");

        }

    }
}
