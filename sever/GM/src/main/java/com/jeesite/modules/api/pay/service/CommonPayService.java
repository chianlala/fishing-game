package com.jeesite.modules.api.pay.service;

import cn.hutool.json.JSONObject;
import com.jeesite.modules.api.pay.vo.OrderRequestVO;
import com.jeesite.modules.osee.vo.CommonResponse;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * 通用支付服务服务接口
 *
 * @author zjl
 */
public interface CommonPayService {

    CommonResponse unifiedorder(OrderRequestVO orderRequest) throws Exception;

    CommonResponse callback(HttpServletRequest request, int callbackType, JSONObject jsonObject)
        throws IOException;

    CommonResponse getShopItems(Integer type);

}
