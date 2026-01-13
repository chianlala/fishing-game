package com.jeesite.modules.api.pay.service;

import com.jeesite.modules.api.pay.domain.ShopItem;
import com.jeesite.modules.osee.vo.CommonResponse;

import javax.servlet.http.HttpServletRequest;

public interface HyPayService {

    CommonResponse unifiedorder(String orderId, String isSao, ShopItem shopItem);

    CommonResponse notify(HttpServletRequest request);
}
