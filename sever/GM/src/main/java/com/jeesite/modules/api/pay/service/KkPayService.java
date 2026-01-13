package com.jeesite.modules.api.pay.service;

import com.jeesite.modules.api.pay.domain.ShopItem;
import com.jeesite.modules.osee.vo.CommonResponse;

import javax.servlet.http.HttpServletRequest;

public interface KkPayService {

    CommonResponse unifiedorder(String orderId, int isSao, ShopItem shopItem);

    CommonResponse notify(HttpServletRequest request);
}
