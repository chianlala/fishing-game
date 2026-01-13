package com.jeesite.modules.api.pay.service;

import cn.hutool.json.JSONObject;
import com.jeesite.modules.api.pay.domain.ShopItem;
import com.jeesite.modules.osee.vo.CommonResponse;

public interface ApplePayService {

    CommonResponse unifiedorder(String orderId, ShopItem shopItem);

    CommonResponse notify(JSONObject jsonObject);

}
