package com.jeesite.modules.api.pay.service.impl;

import cn.hutool.json.JSONObject;
import com.jeesite.modules.api.pay.domain.ShopItem;
import com.jeesite.modules.api.pay.service.*;
import com.jeesite.modules.api.pay.util.PayConstants;
import com.jeesite.modules.api.pay.vo.OrderRequestVO;
import com.jeesite.modules.osee.service.BaseService;
import com.jeesite.modules.osee.vo.CommonResponse;
import com.jeesite.modules.util.CommonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * 通用支付服务服务实现类
 *
 * @author zjl
 */
@Service
public class CommonPayServiceImpl extends BaseService implements CommonPayService {

    @Autowired
    private PayService payService;

    @Autowired
    private WeChatPayService weChatPayService;

    @Autowired
    private CloudPayService cloudPayService;

    @Autowired
    private HuaWeiService huaWeiService;

    @Autowired
    private HyPayService hyPayService;
    @Autowired
    private LidPayService lidPayService;
    @Autowired
    private HlbPayService hlbPayService;
    @Autowired
    private AdaPayService adaPayService;
    @Autowired
    private DaiShouService daiShouService;

    @Resource
    JiaoYiLeService jiaoYiLeService;

    @Resource
    EdfapayService edfapayService;

    @Resource
    GooglePayService googlePayService;

    @Resource
    ApplePayService applePayService;

    @Autowired
    private KkPayService kkPayService;

    @Autowired
    private EasyPayService easyPayService;

    @Autowired
    @Qualifier("hengLongPay") // 指定支付方
    private ThirdPayService thirdPayService;

    // 商城所有的商品
    public static final List<ShopItem> SHOP_ITEMS = new ArrayList<>();

    public CommonPayServiceImpl() {

        // 商品ID
        AtomicInteger itemId = new AtomicInteger(1);

        // 添加金币商品 金钱:金币 比例 1:2w
        double rate = 2 * 10000;
        Object[] goldMoneys = {8.00, 18.00, 68.00, 128.00, 268.00, 495.00, 568.00, 698.00};

        for (Object money : goldMoneys) {

            SHOP_ITEMS
                .add(ShopItem.createGoldItem(itemId.getAndIncrement(), (Double) money,
                    (int) ((Double) money * rate)));

        }

        // 添加钻石商品 1:1
        Object[][] diamondMoneys =
            {{2.00, 20}, {10.00, 100}, {50.00, 500}, {98.00, 980}, {198.00, 1980}, {328.00, 3280},
                {648.00, 6480},
                {1000.00, 10000},};

        for (Object[] money : diamondMoneys) {
            SHOP_ITEMS.add(ShopItem.createDiamondItem(itemId.getAndIncrement(), (Double) money[0],
                (Integer) money[1]));
        }

        // 鱼雷售卖
        SHOP_ITEMS.add(
            new ShopItem(itemId.getAndIncrement(), 75.00, "低阶鱼雷*5", 5, 5)); // 青铜鱼雷*5=75元
        SHOP_ITEMS.add(
            new ShopItem(itemId.getAndIncrement(), 150.00, "低阶鱼雷*10", 5, 10)); //  青铜鱼雷*10=150元

        // 添加月卡30天商品
        SHOP_ITEMS.add(new ShopItem(100, 30.00, "月卡*30天", 12, 30));

    }

    @Override
    public CommonResponse getShopItems(Integer type) {

        // type 1-金币 4-钻石
        return new CommonResponse(
            SHOP_ITEMS.stream().filter(shopItem -> shopItem.getShopType().equals(type))
                .collect(Collectors.toList()));

    }

    @Override
    public CommonResponse unifiedorder(OrderRequestVO orderRequest) throws Exception {

        // 获取商品ID
        Integer itemId = orderRequest.getItemId();

        Optional<ShopItem> optionalShopItem =
            SHOP_ITEMS.stream().filter(item -> item.getId().equals(itemId)).findFirst();

        if (!optionalShopItem.isPresent()) { // 未找到对应id的商品
            return new CommonResponse("ERROR_SHOP_ID", "商品ID有误！");
        }

        // 要购买的商品信息
        ShopItem shopItem = optionalShopItem.get();

        // 获取支付类型
        Integer payType = orderRequest.getPayType();
        // 生成订单号
        String orderId = CommonUtils.createOrderId(6);

        CommonResponse commonResponse;
        if (payType == PayConstants.PAY_TYPE_WECHAT) { // 微信支付

            commonResponse = weChatPayService.unifiedorder(orderId, orderRequest, shopItem);

        } else if (payType == PayConstants.PAY_TYPE_ALI) { // 支付宝支付

            commonResponse = new CommonResponse("NOI_SUPPORT", "暂不支持支付宝原生支付");

        } else if (payType == PayConstants.PAY_TYPE_THIRD_WECHAT_QRCODE) { // 第三方微信扫码

            //            commonResponse = thirdPayService.unifiedorder(orderId, "301", shopItem); // jxPay
            commonResponse =
                cloudPayService.unifiedorder(orderId, "902", shopItem,
                    orderRequest.getPlayerIp()); // hlPay
            // 第三方微信归为微信支付
            orderRequest.setPayType(PayConstants.PAY_TYPE_WECHAT);

        } else if (payType == PayConstants.PAY_TYPE_THIRD_ALI_QRCODE) { // 第三方支付宝扫码

            // commonResponse = thirdPayService.unifiedorder(orderId, "903", shopItem);
            // 第三方支付宝归为支付宝支付
            //  orderRequest.setPayType(PayConstants.PAY_TYPE_ALI);

            // 9A云支付接口
            commonResponse = cloudPayService.unifiedorder(orderId, "906", shopItem,
                orderRequest.getPlayerIp());

            logger.debug("130: --> " + commonResponse);
            // 第三方支付宝扫码归为支付宝支付
            orderRequest.setPayType(PayConstants.PAY_TYPE_ALI);

        } else if (payType == PayConstants.PAY_TYPE_HUAYI) { // 华移支付宝

            commonResponse = hyPayService.unifiedorder(orderId, "0", shopItem);
            orderRequest.setPayType(PayConstants.PAY_TYPE_ALI);

        } else if (payType == PayConstants.PAY_TYPE_EASYPAY_WX) { // easy微信H5

            commonResponse = easyPayService.unifiedorder(orderId, shopItem);
            orderRequest.setPayType(PayConstants.PAY_TYPE_WECHAT);

        } else if (payType == PayConstants.PAY_TYPE_ADAPAY) { // Ada支付宝

            commonResponse = adaPayService.unifiedorder(orderId, shopItem);
            orderRequest.setPayType(PayConstants.PAY_TYPE_ALI);

        } else if (payType == PayConstants.PAY_TYPE_JIAOYILE) { // 交易乐

            commonResponse = jiaoYiLeService.unifiedorder(orderId, shopItem);
            orderRequest.setPayType(PayConstants.PAY_TYPE_ALI);

        } else if (payType == PayConstants.PAY_TYPE_EDFAPAY) { // Edfapay

            commonResponse = edfapayService.unifiedorder(orderId, shopItem);
            orderRequest.setPayType(PayConstants.PAY_TYPE_EDFAPAY);

        } else if (payType == PayConstants.PAY_TYPE_GOOGLE) { // 谷歌支付

            commonResponse = googlePayService.unifiedorder(orderId, shopItem);
            orderRequest.setPayType(PayConstants.PAY_TYPE_GOOGLE);

        } else if (payType == PayConstants.PAY_TYPE_APPLE) { // 苹果支付

            commonResponse = applePayService.unifiedorder(orderId, shopItem);
            orderRequest.setPayType(PayConstants.PAY_TYPE_APPLE);

        } else if (payType == PayConstants.PAY_TYPE_HUAYI_SAO) { // 华移支付宝扫码

            commonResponse = hyPayService.unifiedorder(orderId, "1", shopItem);
            orderRequest.setPayType(PayConstants.PAY_TYPE_ALI);

        } else if (payType == PayConstants.PAY_TYPE_lIDPAY) { // lid支付宝

            commonResponse = lidPayService.unifiedorder(orderId, shopItem);
            orderRequest.setPayType(PayConstants.PAY_TYPE_ALI);

        } else if (payType == PayConstants.PAY_TYPE_HLBPAY) { // hlb支付宝

            commonResponse = hlbPayService.unifiedorder(orderId, shopItem);
            orderRequest.setPayType(PayConstants.PAY_TYPE_ALI);

        } else if (payType == PayConstants.PAY_TYPE_KKAPAY) { // 快快支付宝

            commonResponse = kkPayService.unifiedorder(orderId, 1, shopItem);
            orderRequest.setPayType(PayConstants.PAY_TYPE_ALI);

        } else if (payType == PayConstants.PAY_TYPE_KKAPAY_H5) { // 快快支付宝H5

            commonResponse = kkPayService.unifiedorder(orderId, 2, shopItem);
            orderRequest.setPayType(PayConstants.PAY_TYPE_ALI);

        } else if (payType == PayConstants.PAY_TYPE_KKAPAY_WX) { // 快快微信

            commonResponse = kkPayService.unifiedorder(orderId, 3, shopItem);
            orderRequest.setPayType(PayConstants.PAY_TYPE_WECHAT);


        } else if (payType == PayConstants.PAY_TYPE_USD_BANK) {  //美国网银

            commonResponse = daiShouService.unifiedorder(orderId, shopItem, orderRequest);
            orderRequest.setPayType(PayConstants.PAY_TYPE_USD_BANK);

        } else if (payType == PayConstants.PAY_TYPE_THIRD_ALI) {    // 第三方支付宝H5

            commonResponse = cloudPayService.unifiedorder(orderId, "905", shopItem,
                orderRequest.getPlayerIp());
            logger.debug("130: --> " + commonResponse);
            // 第三方支付宝H5归为支付宝支付
            orderRequest.setPayType(PayConstants.PAY_TYPE_ALI);

        } else {

            commonResponse = new CommonResponse("ERROR", "未知的支付类型");

        }

        if (commonResponse.getSuccess()) { // 下单成功 发给游戏服务器订单信息

            CommonResponse response = payService.sendOrderInfoToGameServer(orderId, orderRequest,
                shopItem);

            if (!response.getSuccess()) { // 发送不成功就不返回给前端下单信息
                return response;
            }

        }

        return commonResponse;

    }

    @Override
    public CommonResponse callback(HttpServletRequest request, int callbackType,
        JSONObject jsonObject)
        throws IOException {

        CommonResponse commonResponse;

        if (callbackType == PayConstants.PAY_TYPE_WECHAT) {

            commonResponse = weChatPayService.notify(request);

        } else if (callbackType == PayConstants.PAY_TYPE_ALI) {

            commonResponse = cloudPayService.notify(request);
            logger.debug("aaa");
            //commonResponse = new CommonResponse(PayConstants.PAY_ERROR, "不支持的支付回调");

        } else if (callbackType == PayConstants.PAY_TYPE_THIRD) { // 第三方支付统一回调

            commonResponse = thirdPayService.notify(request);

        } else if (callbackType == PayConstants.PAY_TYPE_HUAWEI) { //华为支付回调

            commonResponse = huaWeiService.notify(request);

        } else if (callbackType == PayConstants.PAY_TYPE_HUAYI) { //华移支付回调

            commonResponse = hyPayService.notify(request);

        } else if (callbackType == PayConstants.PAY_TYPE_lIDPAY) { //华移支付回调

            commonResponse = lidPayService.notify(request);

        } else if (callbackType == PayConstants.PAY_TYPE_KKAPAY) { //快快支付回调

            commonResponse = kkPayService.notify(request);

        } else if (callbackType == PayConstants.PAY_TYPE_EASYPAY_WX) { //easy支付回调

            commonResponse = easyPayService.notify(request);

        } else if (callbackType == PayConstants.PAY_TYPE_ADAPAY) { //Ada支付回调

            commonResponse = adaPayService.notify(request);

        } else if (callbackType == PayConstants.PAY_TYPE_JIAOYILE) { // 交易乐回调

            commonResponse = jiaoYiLeService.notify(request);

        } else if (callbackType == PayConstants.PAY_TYPE_EDFAPAY) { // Edfapay回调

            commonResponse = edfapayService.notify(request);

        } else if (callbackType == PayConstants.PAY_TYPE_GOOGLE) { // 谷歌支付回调

            commonResponse = googlePayService.notify(request);

        } else if (callbackType == PayConstants.PAY_TYPE_APPLE) { // 苹果支付回调

            commonResponse = applePayService.notify(jsonObject);

        } else if (callbackType == PayConstants.PAY_TYPE_HLBPAY) { //Hlb支付回调

            commonResponse = hlbPayService.notify(request);

        } else if (callbackType == PayConstants.PAY_TYPE_USD_BANK) { // 美国网银回调
            commonResponse = daiShouService.notify(request,jsonObject);
        } else {

            commonResponse = new CommonResponse(PayConstants.PAY_ERROR, "未知的回调通知类型");

        }

        return commonResponse;

    }

}
