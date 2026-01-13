package com.jeesite.modules.api.pay.service;

import com.jeesite.modules.api.pay.domain.ShopItem;
import com.jeesite.modules.osee.vo.CommonResponse;

import javax.servlet.http.HttpServletRequest;

public interface EasyPayService {

    CommonResponse unifiedorder(String orderId, ShopItem shopItem);

    CommonResponse notify(HttpServletRequest request);
}
