package com.jeesite.modules.api.pay.service;

import com.jeesite.modules.api.pay.domain.ShopItem;
import com.jeesite.modules.api.pay.vo.OrderRequestVO;
import com.jeesite.modules.osee.vo.CommonResponse;

/**
 * 支付服务层接口
 *
 * @author zjl
 */
public interface PayService {

    CommonResponse sendOrderInfoToGameServer(String orderId, OrderRequestVO orderRequest,
        ShopItem shopItem);

    CommonResponse sendHwOrderInfoToGameServer(String orderId, OrderRequestVO orderRequest,
        ShopItem shopItem, String result);

    CommonResponse sendNotifyToGameServer(String orderId, Double payMoney, Integer orderState);

}
