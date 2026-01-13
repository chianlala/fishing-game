package com.jeesite.modules.api.pay.web;

import cn.hutool.json.JSONObject;
import com.google.zxing.WriterException;
import com.jeesite.common.web.BaseController;
import com.jeesite.modules.api.pay.service.CommonPayService;
import com.jeesite.modules.api.pay.util.PayConstants;
import com.jeesite.modules.api.pay.vo.OrderRequestVO;
import com.jeesite.modules.osee.config.ProjectConfig;
import com.jeesite.modules.osee.vo.CommonResponse;
import com.jeesite.modules.util.QRCodeUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.net.URLDecoder;
import java.util.Enumeration;

/**
 * 支付控制层
 *
 * @author zjl
 */
@Controller
@RequestMapping("${project.apiPath}/pay")
@Slf4j
public class PayController extends BaseController {

    @Autowired
    private CommonPayService commonPayService;

    @Autowired
    private ProjectConfig projectConfig;

    /**
     * 获取商城商品列表
     */
    @GetMapping("shop/items/{type}")
    @ResponseBody
    public CommonResponse shopItems(@PathVariable Integer type) {
        if (type == null) {
            return new CommonResponse("ERROR_NULL_PARAMETER", "商品类型有误");
        }
        return commonPayService.getShopItems(type);
    }

    /**
     * 统一下单
     */
    @PostMapping("unifiedorder")
    @ResponseBody
    public CommonResponse unifiedorder(@RequestBody OrderRequestVO orderRequest) throws Exception {
        return commonPayService.unifiedorder(orderRequest);
    }

    /**
     * 微信支付通知回调
     */
    @RequestMapping("callback/wechat")
    @ResponseBody
    public String callbackWeChat(HttpServletRequest request) throws IOException {
        CommonResponse commonResponse = commonPayService.callback(request,
                PayConstants.PAY_TYPE_WECHAT, null);
        if (commonResponse.getSuccess()) {
            return (String) commonResponse.getData();
        }
        return commonResponse.getErrMsg();
    }

    /**
     * 快快支付通知回调
     */
    @RequestMapping("callback/kkpay")
    @ResponseBody
    public String callbackKk(HttpServletRequest request) throws IOException {
        CommonResponse commonResponse = commonPayService.callback(request,
                PayConstants.PAY_TYPE_KKAPAY, null);
        if (commonResponse.getSuccess()) {
            return (String) commonResponse.getData();
        }
        return commonResponse.getErrMsg();
    }

    /**
     * 快快支付通知回调
     */
    @RequestMapping("callback/easyPay")
    @ResponseBody
    public String callbackEasy(HttpServletRequest request) throws IOException {
        CommonResponse commonResponse = commonPayService.callback(request,
                PayConstants.PAY_TYPE_EASYPAY_WX, null);
        if (commonResponse.getSuccess()) {
            return (String) commonResponse.getData();
        }
        return commonResponse.getErrMsg();
    }

    /**
     * Ada支付通知回调
     */
    @RequestMapping("callback/Adacloudpay")
    @ResponseBody
    public String callbackAda(HttpServletRequest request) throws IOException {
        CommonResponse commonResponse = commonPayService.callback(request,
                PayConstants.PAY_TYPE_ADAPAY, null);
        if (commonResponse.getSuccess()) {
            return (String) commonResponse.getData();
        }
        return commonResponse.getErrMsg();
    }

    /**
     * 交易乐回调
     */
    @RequestMapping("callback/jiaoYiLe")
    @ResponseBody
    public String callbackJiaoYiLe(HttpServletRequest request) throws IOException {
        CommonResponse commonResponse = commonPayService.callback(request,
                PayConstants.PAY_TYPE_JIAOYILE, null);
        if (commonResponse.getSuccess()) {
            return (String) commonResponse.getData();
        }
        return commonResponse.getErrMsg();
    }

    /**
     * edfapay回调
     */
    @RequestMapping("callback/edfapay")
    @ResponseBody
    public String callbackEdfapay(HttpServletRequest request) throws IOException {
        CommonResponse commonResponse = commonPayService.callback(request,
                PayConstants.PAY_TYPE_EDFAPAY, null);
        if (commonResponse.getSuccess()) {
            return (String) commonResponse.getData();
        }
        return commonResponse.getErrMsg();
    }

    /**
     * 谷歌支付回调
     */
    @RequestMapping("callback/googlePay")
    @ResponseBody
    public String callbackGooglePay(HttpServletRequest request) throws IOException {

        CommonResponse commonResponse = commonPayService.callback(request,
                PayConstants.PAY_TYPE_GOOGLE, null);

        if (commonResponse.getSuccess()) {
            return (String) commonResponse.getData();
        }

        return commonResponse.getErrMsg();

    }

    /**
     * 苹果支付回调
     */
    @RequestMapping("callback/apply")
    @ResponseBody
    public String callbackApplyPay(@RequestBody JSONObject jsonObject, HttpServletRequest request)
            throws IOException {

        CommonResponse commonResponse = commonPayService.callback(request,
                PayConstants.PAY_TYPE_APPLE, jsonObject);

        if (commonResponse.getSuccess()) {
            return (String) commonResponse.getData();
        }

        return commonResponse.getErrMsg();

    }

    /**
     * 支付成功的页面
     */
    @RequestMapping("callback/success")
    public String callbackSuccess() {

        return "modules/osee/shop/callbackSuccess";

    }

    /**
     * 华为通知回调
     */
    @RequestMapping("callback/huawei")
    @ResponseBody
    public String callbackHuaWei(HttpServletRequest request) throws IOException {
        CommonResponse commonResponse = commonPayService.callback(request,
                PayConstants.PAY_TYPE_HUAWEI, null);
        if (commonResponse.getSuccess()) {
            return (String) commonResponse.getData();
        }
        return commonResponse.getErrMsg();
    }

    /**
     * 第三方支付通知回调
     */
    @RequestMapping("callback/thirdpay")
    @ResponseBody
    public String callbackThird(HttpServletRequest request) throws IOException {
        CommonResponse commonResponse = commonPayService.callback(request,
                PayConstants.PAY_TYPE_THIRD, null);
        if (commonResponse.getSuccess()) {
            return (String) commonResponse.getData();
        }
        return commonResponse.getErrMsg();
    }

    /**
     * 第三方支付同步通知回调
     */
    @RequestMapping("callback/thirdpay/sync")
    public String callbackThirdSync(HttpServletRequest request) {
        Enumeration<String> parameterNames = request.getParameterNames();
        while (parameterNames.hasMoreElements()) {
            String parameterName = parameterNames.nextElement();
            String parameterValue = request.getParameter(parameterName);
            request.setAttribute(parameterName, parameterValue);
        }
        request.setAttribute("appName", projectConfig.getName());
        return "modules/api/pay/payCallbackSync";
    }

    // **************************************************

    /**
     * 扫码支付界面
     */
    @RequestMapping("payQrcode")
    public String payQrcode(String payUrl, Double payMoney, Integer payType,
                            HttpServletRequest request)
            throws IOException, WriterException {
        if (StringUtils.isEmpty(payUrl) || payMoney == null || payType == null) {
            payUrl = "";
            payMoney = 0D;
            payType = 0;
        }
        request.setAttribute("appName", projectConfig.getName());
        request.setAttribute("payQrcode", QRCodeUtil.createQRCodeBase64(payUrl, 300, 300, true));
        request.setAttribute("payMoney", payMoney);
        request.setAttribute("payType",
                payType == PayConstants.PAY_TYPE_WECHAT ? "微信"
                        : (payType == PayConstants.PAY_TYPE_ALI ? "支付宝" : "未知"));
        return "modules/api/pay/payQrcode";
    }

    /**
     * 支付重定向跳转界面
     */
    @RequestMapping("payJump")
    public String payJump(String payUrl, HttpServletRequest request) throws Exception {
        if (StringUtils.isEmpty(payUrl)) {
            payUrl = "";
        }
        request.setAttribute("payUrl", URLDecoder.decode(payUrl, "utf-8"));
        return "modules/api/pay/payJump";
    }

    /**
     * 9a云支付通知回调
     */

    @RequestMapping("callback/9acloudpay")
    @ResponseBody
    public String callback9aCloud(HttpServletRequest request) throws IOException {
        CommonResponse commonResponse = commonPayService.callback(request,
                PayConstants.PAY_TYPE_ALI, null);
        if (commonResponse.getSuccess()) {
            return "SUCCESS";
        }
        return commonResponse.getErrMsg();
    }

    /**
     * 华移支付通知回调
     */

    @RequestMapping("callback/hycloudpay")
    @ResponseBody
    public String callbackhyCloud(HttpServletRequest request) throws IOException {
        CommonResponse commonResponse = commonPayService.callback(request,
                PayConstants.PAY_TYPE_HUAYI, null);
        if (commonResponse.getSuccess()) {
            return "SUCCESS";
        }
        return commonResponse.getErrMsg();
    }

    /**
     * 华移支付通知回调
     */

    @RequestMapping("callback/lidcloudpay")
    @ResponseBody
    public String callbackLidCloud(HttpServletRequest request) throws IOException {
        CommonResponse commonResponse = commonPayService.callback(request,
                PayConstants.PAY_TYPE_lIDPAY, null);
        if (commonResponse.getSuccess()) {
            return "SUCCESS";
        }
        return commonResponse.getErrMsg();
    }

    /**
     * 合利宝支付通知回调
     */

    @RequestMapping("callback/hlbcloudpay")
    @ResponseBody
    public String callbackHlbCloud(HttpServletRequest request) throws IOException {
        CommonResponse commonResponse = commonPayService.callback(request,
                PayConstants.PAY_TYPE_HLBPAY, null);
        if (commonResponse.getSuccess()) {
            return "SUCCESS";
        }
        return commonResponse.getErrMsg();
    }

    /**
     * 美国网银
     */
    @RequestMapping("callback/pnsafepay")
    @ResponseBody
    public String callbackPnsafepay(@RequestBody JSONObject jsonObject, HttpServletRequest request) throws IOException {
        CommonResponse commonResponse = commonPayService.callback(request,
                PayConstants.PAY_TYPE_USD_BANK, jsonObject);
        if (commonResponse.getSuccess()) {
            return "ok";
        }
        return commonResponse.getErrMsg();
    }
}
