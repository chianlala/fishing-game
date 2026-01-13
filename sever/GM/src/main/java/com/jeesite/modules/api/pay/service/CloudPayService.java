package com.jeesite.modules.api.pay.service;

import com.jeesite.modules.api.pay.domain.ShopItem;
import com.jeesite.modules.osee.vo.CommonResponse;

import javax.servlet.http.HttpServletRequest;

public interface CloudPayService {

    CommonResponse unifiedorder(String orderId, String bankCode, ShopItem shopItem,
        String playerIp);

    CommonResponse notify(HttpServletRequest request);
}
