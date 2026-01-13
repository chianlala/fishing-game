package com.jeesite.modules.api.pay.service;

import com.jeesite.modules.api.pay.domain.ShopItem;
import com.jeesite.modules.osee.vo.CommonResponse;

import javax.servlet.http.HttpServletRequest;

public interface HlbPayService {

    CommonResponse unifiedorder(String orderId, ShopItem shopItem) throws Exception;

    CommonResponse notify(HttpServletRequest request);
}
