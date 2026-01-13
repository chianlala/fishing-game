package com.jeesite.modules.api.pay.service;

import com.jeesite.modules.api.pay.domain.ShopItem;
import com.jeesite.modules.osee.vo.CommonResponse;

import javax.servlet.http.HttpServletRequest;

/**
 * 第三方支付的接口层
 *
 * @author zjl
 */
public interface ThirdPayService {

    CommonResponse unifiedorder(String orderId, String bankCode, ShopItem shopItem);

    CommonResponse notify(HttpServletRequest request);
}
