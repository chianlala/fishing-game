package com.jeesite.modules.api.pay.service;

import com.jeesite.modules.api.pay.domain.ShopItem;
import com.jeesite.modules.api.pay.vo.OrderRequestVO;
import com.jeesite.modules.osee.vo.CommonResponse;

import javax.servlet.http.HttpServletRequest;

/**
 * 微信支付服务层接口
 *
 * @author zjl
 */
public interface WeChatPayService {

    CommonResponse unifiedorder(String orderId, OrderRequestVO orderRequest, ShopItem shopItem);

    CommonResponse notify(HttpServletRequest request);
}
