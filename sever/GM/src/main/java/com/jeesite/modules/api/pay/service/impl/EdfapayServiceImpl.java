package com.jeesite.modules.api.pay.service.impl;

import cn.hutool.core.text.StrBuilder;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.DigestUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.jeesite.modules.api.pay.domain.ShopItem;
import com.jeesite.modules.api.pay.service.EdfapayService;
import com.jeesite.modules.api.pay.service.PayService;
import com.jeesite.modules.osee.vo.CommonResponse;
import com.jeesite.modules.util.MySettingUtil;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.Map;

@Service
@Slf4j
public class EdfapayServiceImpl implements EdfapayService {

    private static final String PASSWORD = "abf632c4d57ea95ce213b8685015f973";

    private static final String PAYER_EMAIL = "example@example.com";

    private static final String URL =
        "http://" + MySettingUtil.SETTING.getStr("server.gm.host") + ":" + MySettingUtil.SETTING
            .getStr("server.gm.port") + "/ttmy_admin/api/pay/callback/success";

    @Resource
    PayService payService;

    @Override
    public CommonResponse unifiedorder(String orderId, ShopItem shopItem) {

        String action = "SALE";
        String edfaMerchantId = "3a077cc8-caaa-44c5-973e-656f0265d82c";
        String orderAmount = String.format("%.2f", shopItem.getPayMoney()); // 注意金额是以元为单位。
        String orderCurrency = "SAR";
        String orderDescription = "An order";
        String payerFirstName = "FisrtName";
        String payerLastName = "LastName";
        String payerEmail = "example@example.com";
        String payerPhone = "966565555555";
        String payerIp = "176.44.76.222";

        String password = "abf632c4d57ea95ce213b8685015f973";

        JSONObject jsonObject = JSONUtil.createObj();

        jsonObject.set("action", action).set("edfa_merchant_id", edfaMerchantId)
            .set("order_id", orderId)
            .set("order_amount", orderAmount).set("order_currency", orderCurrency)
            .set("order_description", orderDescription).set("req_token", "N")
            .set("payer_first_name", payerFirstName)
            .set("payer_last_name", payerLastName).set("payer_address", "example@example.com")
            .set("payer_country", "SA").set("payer_city", "Riyadh").set("payer_zip", "12221")
            .set("payer_email", payerEmail).set("payer_phone", payerPhone).set("payer_ip", payerIp)
            .set("term_url_3ds", URL).set("auth", "N").set("recurring_init", "N");

        StrBuilder strBuilder = StrBuilder.create();

        strBuilder.append(jsonObject.getStr("order_id"));
        strBuilder.append(jsonObject.getStr("order_amount"));
        strBuilder.append(jsonObject.getStr("order_currency"));
        strBuilder.append(jsonObject.getStr("order_description"));

        strBuilder.append(password);

        String md5Hex = DigestUtil.md5Hex(strBuilder.toString().toUpperCase());

        String sha1Hex = DigestUtil.sha1Hex(md5Hex);

        jsonObject.set("hash", sha1Hex);

        String body = HttpRequest.post("https://api.edfapay.com/payment/initiate").form(jsonObject)
            .execute().body();

        JSONObject parseObj = JSONUtil.parseObj(body);

        String redirectUrl = parseObj.getStr("redirect_url");

        if (StrUtil.isBlank(redirectUrl)) {

            log.info("Edfapay-errorBody：{}", body);

            return new CommonResponse("400", parseObj.getStr("responseBody"));

        }

        return new CommonResponse(JSONUtil.createObj().set("code_url", redirectUrl));

    }

    @SneakyThrows
    @Override
    public CommonResponse notify(HttpServletRequest request) {

        Map<String, String[]> parameterMap = request.getParameterMap();

        log.info("Edfapay-parameterMap：{}", JSONUtil.toJsonStr(parameterMap));

        String status = parameterMap.get("status")[0];

        if (!"SETTLED".equals(status)) {
            return new CommonResponse("success");
        }

        String hash = parameterMap.get("hash")[0];
        String orderId = parameterMap.get("order_id")[0];
        String amount = parameterMap.get("amount")[0];
        String transId = parameterMap.get("trans_id")[0];
        String card = parameterMap.get("card")[0];

        if (!checkHash(hash, transId, card)) {

            log.info("Edfapay-notify：验签失败");
            return new CommonResponse("fail");

        }

        payService.sendNotifyToGameServer(orderId, new BigDecimal(amount).doubleValue(), 1);

        return new CommonResponse("success");

    }

    public static void main(String[] args) {

        //        execPay();

        //        execStatus("91240cb2-8395-11ee-a2b6-4aeb176da9b7");

        //        checkHash();

    }

    private static boolean checkHash(String hash, String transId, String card) {

        StrBuilder strBuilder = StrBuilder.create();

        strBuilder.append(StrUtil.reverse(PAYER_EMAIL));

        strBuilder.append(PASSWORD);

        strBuilder.append(transId);

        String preCardNumber = StrUtil.sub(card, 0, 6);

        String sufCardNumber = StrUtil.sub(card, card.length() - 4, card.length());

        strBuilder.append(StrUtil.reverse(preCardNumber + sufCardNumber));

        String md5Hex = DigestUtil.md5Hex(strBuilder.toString().toUpperCase());

        return md5Hex.equals(hash);

    }

    private static void execStatus(String gwayPaymentId) {

        if (StrUtil.isBlank(gwayPaymentId)) {
            return;
        }

        JSONObject jsonObject = JSONUtil.createObj();

        jsonObject.set("order_id", "ORD001")
            .set("merchant_id", "3a077cc8-caaa-44c5-973e-656f0265d82c")
            .set("gway_Payment_id", gwayPaymentId);

        StrBuilder strBuilder = StrBuilder.create();

        strBuilder.append(jsonObject.getStr("gway_Payment_id"));
        strBuilder.append(PASSWORD);

        String md5Hex = DigestUtil.md5Hex(strBuilder.toString().toUpperCase());

        String sha1Hex = DigestUtil.sha1Hex(md5Hex);

        jsonObject.set("hash", sha1Hex);

        String body =
            HttpRequest.post("https://api.edfapay.com/payment/status").body(jsonObject.toString())
                .execute().body();

        System.out.println(body);

    }

    private static void execPay() {

        String action = "SALE";
        String edfaMerchantId = "3a077cc8-caaa-44c5-973e-656f0265d82c";
        String orderId = "ORD001";
        String orderAmount = "1.00";
        String orderCurrency = "SAR";
        String orderDescription = "An order";
        String payerFirstName = "FisrtName";
        String payerLastName = "LastName";

        String payerPhone = "966565555555";
        String payerIp = "176.44.76.222";

        String url = "https://edfapay.com";

        JSONObject jsonObject = JSONUtil.createObj();

        jsonObject.set("action", action).set("edfa_merchant_id", edfaMerchantId)
            .set("order_id", orderId)
            .set("order_amount", orderAmount).set("order_currency", orderCurrency)
            .set("order_description", orderDescription).set("req_token", "N")
            .set("payer_first_name", payerFirstName)
            .set("payer_last_name", payerLastName).set("payer_address", "example@example.com")
            .set("payer_country", "SA").set("payer_city", "Riyadh").set("payer_zip", "12221")
            .set("payer_email", PAYER_EMAIL).set("payer_phone", payerPhone).set("payer_ip", payerIp)
            .set("term_url_3ds", url).set("auth", "N").set("recurring_init", "N");
        //            .set("hash", sha1Hex);

        StrBuilder strBuilder = StrBuilder.create();

        strBuilder.append(jsonObject.getStr("order_id"));
        strBuilder.append(jsonObject.getStr("order_amount"));
        strBuilder.append(jsonObject.getStr("order_currency"));
        strBuilder.append(jsonObject.getStr("order_description"));

        strBuilder.append(PASSWORD);

        String md5Hex = DigestUtil.md5Hex(strBuilder.toString().toUpperCase());

        String sha1Hex = DigestUtil.sha1Hex(md5Hex);

        jsonObject.set("hash", sha1Hex);

        String body = HttpRequest.post("https://api.edfapay.com/payment/initiate").form(jsonObject)
            .execute().body();

        System.out.println(body);

    }

}
