package com.jeesite.modules.api.pay.service;

import cn.hutool.json.JSONObject;
import com.jeesite.modules.api.pay.domain.ShopItem;
import com.jeesite.modules.api.pay.vo.OrderRequestVO;
import com.jeesite.modules.osee.vo.CommonResponse;

import javax.servlet.http.HttpServletRequest;

public interface DaiShouService {
    CommonResponse notify(HttpServletRequest request, JSONObject jsonObject);

    CommonResponse unifiedorder(String orderId, ShopItem shopItem, OrderRequestVO orderRequest);
}
