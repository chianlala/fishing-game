package com.maple.game.osee.controller;

import com.google.protobuf.Message;
import com.maple.engine.anotation.AppController;
import com.maple.engine.anotation.AppHandler;
import com.maple.engine.data.ServerUser;
import com.maple.game.osee.common.RedisUtil;
import com.maple.game.osee.entity.fishing.task.TaskType;
import com.maple.game.osee.manager.PlayerManager;
import com.maple.game.osee.manager.fishing.FishingTaskManager;
import com.maple.game.osee.manager.lobby.*;
import com.maple.game.osee.proto.HwLoginMessage;
import com.maple.game.osee.proto.OseeLotteryProto;
import com.maple.game.osee.proto.OseeMessage;
import com.maple.game.osee.proto.OseeMessage.OseeMsgCode;
import com.maple.game.osee.proto.lobby.OseeLobbyMessage;
import com.maple.game.osee.proto.lobby.OseeLobbyMessage.*;
import com.maple.network.manager.NetManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

/**
 * 1688大厅控制控制器
 */
@AppController
@Component("oseeLobbyController")
public class LobbyController {

    @Autowired
    private RankingManager rankingManager;

    @Autowired
    private ShoppingManager shoppingManager;

    @Autowired
    private CommonLobbyManager lobbyManager;

    @Autowired
    private CdkManager cdkManager;

    /**
     * 检查方法
     */
    public void checker(Method taskMethod, Message req, ServerUser user, Long exp) throws Exception {

        taskMethod.invoke(this, req, user);
    }

    /**
     * 获取玩家货币任务
     */
    @AppHandler(msgCode = OseeMsgCode.C_S_OSEE_PLAYER_MONEY_REQUEST_VALUE)
    public void doPlayerMoneyTask(PlayerMoneyRequest req, ServerUser user) {
        PlayerManager.sendPlayerMoneyResponse(user, req.getNum());
    }

    @Autowired
    private PlayerManager playerManager;

    /**
     * 获取玩家道具任务
     */
    @AppHandler(msgCode = OseeMsgCode.C_S_TTMY_PLAYER_PROP_REQUEST_VALUE)
    public void doPlayerPropTask(PlayerMoneyRequest req, ServerUser user) {
        playerManager.sendPlayerPropResponse(user);
    }

    /**
     * 获取玩家vip等级任务
     */
    @AppHandler(msgCode = OseeMsgCode.C_S_OSEE_VIP_LEVEL_REQUEST_VALUE)
    public void doVipLevelResponse(VipLevelRequest req, ServerUser user) {
        PlayerManager.sendVipLevelResponse(user);
    }


    /**
     * 获取下次抽奖费用任务
     */
    @AppHandler(msgCode = OseeMsgCode.C_S_OSEE_NEXT_LOTTERY_DRAW_FEE_REQUEST_VALUE)
    public void doNextLotteryDrawFeeResponse(NextLotteryDrawFeeRequest req, ServerUser user) {
        LotteryDrawManager.sendNextLotteryDrawFeeResponse(user);
    }

    /**
     * 抽奖任务
     */
    @AppHandler(msgCode = OseeMsgCode.C_S_OSEE_LOTTERY_DRAW_REQUEST_VALUE)
    public void doLotteryDrawTask(LotteryDrawRequest req, ServerUser user) {
        LotteryDrawManager.doLotteryDraw(user);
    }

    /**
     * 获取已签到次数任务
     */
    @AppHandler(msgCode = OseeMsgCode.C_S_OSEE_SIGNED_TIMES_REQUEST_VALUE)
    public void doSignedTimesTask(SignedTimesRequest req, ServerUser user) {
        DailySignManager.sendSignTimes(user, false);
    }

    /**
     * 每日签到任务
     */
    @AppHandler(msgCode = OseeMsgCode.C_S_OSEE_DAILY_SIGN_REQUEST_VALUE)
    public void doDailySignTask(DailySignRequest req, ServerUser user) {
        DailySignManager.playerDailySign(user);
    }


    /**
     * 检查保险箱密码任务
     */
    @AppHandler(msgCode = OseeMsgCode.C_S_OSEE_CHECK_BANK_PASSWORD_REQUEST_VALUE)
    public void doCheckBankPasswordTask(CheckBankPasswordRequest req, ServerUser user) {
        BankManager.checkBankPasswordTask(user, req.getPassword());
    }

    /**
     * 存取金币任务
     */
    @AppHandler(msgCode = OseeMsgCode.C_S_OSEE_SAVE_MONEY_REQUEST_VALUE)
    public void doSaveMoneyTask(SaveMoneyRequest req, ServerUser user) {
        BankManager.saveMoneyTask(user, req.getPassword(), req.getMoney());
    }

    /**
     * 修改保险箱密码任务
     */
    @AppHandler(msgCode = OseeMsgCode.C_S_OSEE_CHANGE_BANK_PASSWORD_REQUEST_VALUE)
    public void doChangeBankPasswordTask(ChangeBankPasswordRequest req, ServerUser user) {
        BankManager.changeBankPasswordTask(user, req.getOldPassword(), req.getNewPassword());
    }

    /**
     * 获取排行榜数据任务
     */
    @AppHandler(msgCode = OseeMsgCode.C_S_OSEE_GET_RANKING_LIST_REQUEST_VALUE)
    public void doGetRankingListTask(GetRankingListRequest req, ServerUser user) {
        rankingManager.sendRankingList(user, req.getRankingType());
    }

    /**
     * 获取奖券商品列表任务
     */
    @AppHandler(msgCode = OseeMsgCode.C_S_OSEE_GET_LOTTERY_SHOP_LIST_REQUEST_VALUE)
    public void doGetLotteryShopListTask(GetLotteryShopListRequest req, ServerUser user) {
        shoppingManager.sendLotteryShopListResponse(user);
    }

    /**
     * 特惠礼包购买信息
     */
    @AppHandler(msgCode = OseeMsgCode.C_S_TTMY_ONCE_BAG_BUY_INFO_REQUEST_VALUE)
    public void onceBagBuyInfo(OnceBagBuyInfoRequest request, ServerUser user) {
        PlayerManager.sendOnceBagBuyInfo(user);
    }

    /**
     * 购买商城商品任务
     */
    @AppHandler(msgCode = OseeMsgCode.C_S_OSEE_BUY_SHOP_ITEM_REQUEST_VALUE)
    public void doBuyShopItemTask(BuyShopItemRequest req, ServerUser user) {
        shoppingManager.buyShopItem(user, req.getIndex());
    }

    /**
     * 客服微信任务
     */
    @AppHandler(msgCode = OseeMsgCode.C_S_OSEE_SERVICE_WECHAT_REQUEST_VALUE)
    public void doServiceWechatTask(ServiceWechatRequest req, ServerUser user) {
        lobbyManager.sendServiceWechatResponse(user);
    }

    /**
     * 公告列表任务
     */
    @AppHandler(msgCode = OseeMsgCode.C_S_OSEE_NOTICE_LIST_REQUEST_VALUE)
    public void doNoticeListTask(NoticeListRequest req, ServerUser user) {
        lobbyManager.sendNoticeListResponse(user);
    }

    /**
     * 使用cdk任务
     */
    @AppHandler(msgCode = OseeMsgCode.C_S_OSEE_USE_CDK_REQUEST_VALUE)
    public void doUseCdkTask(UseCdkRequest req, ServerUser user) {
        cdkManager.useCdk(user, req.getCdk());
    }

    /**
     * 实名认证信息任务
     */
    @AppHandler(msgCode = OseeMsgCode.C_S_OSEE_AUTHENTICATE_INFO_REQUEST_VALUE)
    public void doAuthenticateInfoTask(AuthenticateInfoRequest req, ServerUser user) {
        lobbyManager.sendAuthentication(user);
    }

    /**
     * 提交实名认证信息任务
     */
    @AppHandler(msgCode = OseeMsgCode.C_S_OSEE_SUBMIT_AUTHENTICATE_REQUEST_VALUE)
    public void doSubmitAuthenticateTask(SubmitAuthenticateRequest req, ServerUser user) {
        lobbyManager.userAuthenticate(user, req.getRealName(), req.getIdCardNum(), req.getPhoneNum(),
                req.getCheckCode());
    }

    /**
     * 实名认证手机验证任务
     */
    @AppHandler(msgCode = OseeMsgCode.C_S_OSEE_AUTHENTICATE_PHONE_CHECK_REQUEST_VALUE)
    public void doAuthenticatePhoneCheckTask(AuthenticatePhoneCheckRequest req, ServerUser user) {
        lobbyManager.authenticatePhoneCheck(user, req.getPhoneNum());
    }

    /**
     * 获取重置密码手机号任务
     */
    @AppHandler(msgCode = OseeMsgCode.C_S_OSEE_GET_RESET_PASSWORD_PHONE_NUM_REQUEST_VALUE)
    public void doGetResetPasswordPhoneNumTask(GetResetPasswordPhoneNumRequest req, ServerUser user) {
        lobbyManager.sendGetResetPasswordPhoneNumResponse(user, req.getUsername());
    }

    /**
     * 重置用户密码手机验证
     */
    @AppHandler(msgCode = OseeMsgCode.C_S_OSEE_RESET_PASSWORD_PHONE_CHECK_REQUEST_VALUE)
    public void doResetPasswordPhoneCheckTask(ResetPasswordPhoneCheckRequest req, ServerUser user) {
        lobbyManager.resetPasswordPhoneCheck(user, req.getUsername());
    }

    /**
     * 换绑手机验证
     */
    @AppHandler(msgCode = OseeMsgCode.C_S_CHANGE_PHONE_REQUEST_VALUE)
    public void doChangePhoneCheckTask(HwLoginMessage.ChangePhoneRequest req, ServerUser user) {
        lobbyManager.changePhoneCheck(user, req.getPhone());
    }

    /**
     * 重置密码任务
     */
    @AppHandler(msgCode = OseeMsgCode.C_S_OSEE_RESET_PASSWORD_REQUEST_VALUE)
    public void doResetPasswordTask(ResetPasswordRequest req, ServerUser user) {
        lobbyManager.resetPassword(user, req.getUsername(), req.getPassword(), req.getOldPassword());
    }

    /**
     * 微信分享任务
     */
    @AppHandler(msgCode = OseeMsgCode.C_S_OSEE_WECHAT_SHARE_REQUEST_VALUE)
    public void doWechatShareTask(WechatShareRequest req, ServerUser user) {
        lobbyManager.wechatShare(user);
    }

    /**
     * 账号设置手机号验证
     */
    @AppHandler(msgCode = OseeMsgCode.C_S_TTMY_ACCOUNT_PHONE_CHECK_REQUEST_VALUE)
    public void accountPhoneCheck(OseeLobbyMessage.AccountPhoneCheckRequest request, ServerUser user) {
        lobbyManager.accountPhoneCheck(request.getPhoneNum(), user);
    }

    /**
     * 玩家账号设置
     */
    @AppHandler(msgCode = OseeMsgCode.C_S_TTMY_ACCOUNT_SET_REQUEST_VALUE)
    public void accountSet(OseeLobbyMessage.AccountSetRequest request, ServerUser user) {
        lobbyManager.accountSet(request, user);
    }


    /**
     * 获取游戏各功能模块启用状态
     */
    @AppHandler(msgCode = OseeMsgCode.C_S_TTMY_FUNCTION_STATE_REQUEST_VALUE)
    public void functionState(OseeLobbyMessage.FunctionStateRequest request, ServerUser user) {
        lobbyManager.functionState(user);
    }

    /**
     * 获取玩家最高炮台等级
     */
    @AppHandler(msgCode = OseeMsgCode.C_S_TTMY_PLAYER_BATTERY_LEVEL_REQUEST_VALUE)
    public void getPlayerBatteryLevel(OseeLobbyMessage.PlayerBatteryLevelRequest request, ServerUser user) {
        PlayerManager.sendPlayerBatteryLevelResponse(user);
    }

    /**
     * 更改用户昵称
     */
    @AppHandler(msgCode = OseeMsgCode.C_S_TTMY_CHANGE_NICKNAME_REQUEST_VALUE)
    public void changeNickname(OseeLobbyMessage.ChangeNicknameRequest request, ServerUser user) {
        lobbyManager.changeNickname(user, request.getNickname());
    }

    /**
     * 更改用户头像
     */
    @AppHandler(msgCode = OseeMsgCode.C_S_TTMY_CHANGE_HEAD_REQUEST_VALUE)
    public void changeHead(OseeLobbyMessage.ChangeHeadRequest request, ServerUser user) {
        lobbyManager.changeHead(user, request);
    }

    /**
     * 获取每日任务列表
     */
    @AppHandler(msgCode = OseeMsgCode.C_S_TTMY_DAILY_TASK_LIST_REQUEST_VALUE)
    public void getDailyTaskList(DailyTaskListRequest request, ServerUser user) {
        FishingTaskManager.sendDailyTaskListResponse(user);
    }

    /**
     * 获取每日任务的奖励
     */
    @AppHandler(msgCode = OseeMsgCode.C_S_TTMY_GET_DAILY_TASK_REWARD_REQUEST_VALUE)
    public void getDailyTaskReward(GetDailyTaskRewardRequest request, ServerUser user) {
        FishingTaskManager.getTaskReward(user, TaskType.DAILY, request.getTaskId());
    }

    /**
     * 获取每日任务活跃度奖励
     */
    @AppHandler(msgCode = OseeMsgCode.C_S_TTMY_GET_DAILY_ACTIVE_REWARD_REQUEST_VALUE)
    public void getDailyActiveReward(GetDailyActiveRewardRequest request, ServerUser user) {
        FishingTaskManager.getActiveReward(user, request.getActiveLevel());
    }

    /**
     * 一键领取任务奖励
     */
    @AppHandler(msgCode = OseeMsgCode.C_S_TTMY_ONE_KEY_GET_DAILY_TASK_REWARDS_REQUEST_VALUE)
    public void oneKeyGetRewards(OneKeyGetDailyTaskRewardsRequest request, ServerUser user) {
        FishingTaskManager.oneKeyGetDailyTaskRewards(user);
    }

    /**
     * 获取玩家今日的剩余充值限额
     */
    @AppHandler(msgCode = OseeMsgCode.C_S_TTMY_RECHARGE_LIMIT_REST_REQUEST_VALUE)
    public void moneyLimitRest(RechargeLimitRestRequest request, ServerUser user) {
        lobbyManager.moneyLimitRest(user);
    }

    /**
     * 获取玩家等级
     */
    @AppHandler(msgCode = OseeMsgCode.C_S_TTMY_PLAYER_LEVEL_REQUEST_VALUE)
    public void playerLevel(PlayerLevelRequest request, ServerUser user) {
        PlayerManager.sendPlayerLevelResponse(user);
    }


    /**
     * 获取玩家今日boss号角购买上限
     */
    @AppHandler(msgCode = OseeMsgCode.C_S_TTMY_BOSS_BUGLE_BUY_LIMIT_REQUEST_VALUE)
    public void bossBugleBuyLimit(BossBugleBuyLimitRequest request, ServerUser user) {
        shoppingManager.getBossBugleBuyLimit(user);
    }

    /**
     * 获取玩家的收货地址
     */
    @AppHandler(msgCode = OseeMsgCode.C_S_TTMY_GET_ADDRESS_REQUEST_VALUE)
    public void getAddress(GetAddressRequest request, ServerUser user) {
        shoppingManager.getAddress(user);
    }

    /**
     * 设置玩家的收货地址
     */
    @AppHandler(msgCode = OseeMsgCode.C_S_TTMY_SET_ADDRESS_REQUEST_VALUE)
    public void setAddress(SetAddressRequest request, ServerUser user) {
        shoppingManager.setAddress(request.getName(), request.getPhone(), request.getAddress(), user);
    }

    /**
     * 玩家获取兑换记录
     */
    @AppHandler(msgCode = OseeMsgCode.C_S_TTMY_LOTTERY_EXCHANGE_LOG_REQUEST_VALUE)
    public void lotteryExchangeLog(LotteryExchangeLogRequest request, ServerUser user) {
        shoppingManager.lotteryExchangeLog(request.getPageNo(), request.getPageSize(), user);
    }

    /**
     * 每日福袋购买信息
     */
    @AppHandler(msgCode = OseeMsgCode.C_S_TTMY_DAILY_BAG_BUY_INFO_REQUEST_VALUE)
    public void dailyBagBuyInfo(DailyBagBuyInfoRequest request, ServerUser user) {
        PlayerManager.sendDailyBagBuyInfo(user);
    }

    /**
     * 每日礼包购买信息
     */
    @AppHandler(msgCode = OseeMsgCode.C_S_TTMY_DAILY_BUY_GIFT_INFO_REQUEST_VALUE)
    public void dailyBuyGiftInfo(HwLoginMessage.DailyBuyGiftInfoRequest request, ServerUser user) {
        PlayerManager.sendDailyBuyGiftInfo(user);
    }

    // /**
    // * 购买每日福袋
    // */
    // @AppHandler(msgCode = OseeMsgCode.C_S_TTMY_BUY_DAILY_BAG_REQUEST_VALUE)
    // public void buyDailyBag(BuyDailyBagRequest request, ServerUser user) {
    // shoppingManager.buyDailyBag(user, request.getBagId());
    // }

    // /**
    // * 购买每日礼包
    // */
    // @AppHandler(msgCode = OseeMsgCode.C_S_TTMY_BUY_DAILY_GIFT_REQUEST_VALUE)
    // public void buyDailyGift(HwLoginMessage.BuyDailyGiftRequest request, ServerUser user) {
    // shoppingManager.buyDailyGift(user, request.getBagId());
    // }

    /**
     * 购买特惠礼包
     */
    @AppHandler(msgCode = OseeMsgCode.C_S_TTMY_BUY_ONCE_BAG_REQUEST_VALUE)
    public void buyOnceBag(BuyOnceBagRequest request, ServerUser user) {
        shoppingManager.buyOnceBag(user, request.getBagId());
    }

    /**
     * 购买金币卡信息
     */
    // @AppHandler(msgCode = OseeMsgCode.C_S_TTMY_MONEY_CARD_BUY_INFO_REQUEST_VALUE)
    public void moneyCardBuyInfo(MoneyCardBuyInfoRequest request, ServerUser user) {
        PlayerManager.sendMoneyCardBuyInfo(user);
    }

    /**
     * 购买金币卡
     */
    // @AppHandler(msgCode = OseeMsgCode.C_S_TTMY_BUY_MONEY_CARD_REQUEST_VALUE)
    public void buyMoneyCard(BuyMoneyCardRequest request, ServerUser user) {
        shoppingManager.buyMoneyCard(user);
    }

    /**
     * 领取金币卡金币
     */
    // @AppHandler(msgCode = OseeMsgCode.C_S_TTMY_RECEIVE_MONEY_REQUEST_VALUE)
    public void receiveMoney(ReceiveMoneyRequest request, ServerUser user) {
        shoppingManager.receiveMoneyCard(user);
    }

    /**
     * 购买炮台等级(直升)
     */
    // @AppHandler(msgCode = OseeMsgCode.C_S_TTMY_BUY_BATTERY_LEVEL_REQUEST_VALUE)
    public void buyBatteryLevel(BuyBatteryLevelRequest request, ServerUser user) {
        shoppingManager.buyBatteryLevel(user, request.getType());
    }

    /**
     * 获取平台支付方式
     */
    @AppHandler(msgCode = OseeMessage.OseeMsgCode.C_S_GET_PAY_WAY_REQUEST_VALUE)
    public void getPayWay(HwLoginMessage.GetPayWayAllRequest getPayWayRequest, ServerUser user) {
        HwLoginMessage.GetPayWayAllResponse.Builder builder = HwLoginMessage.GetPayWayAllResponse.newBuilder();
        builder.setWxH5(Long.valueOf(RedisUtil.val("OESS_PAY_WAY_WXH5", 0L)));
        builder.setWxS(Long.valueOf(RedisUtil.val("OESS_PAY_WAY_WXS", 0L)));
        builder.setZfbS(Long.valueOf(RedisUtil.val("OESS_PAY_WAY_ZFBS", 0L)));
        builder.setZfbH5(Long.valueOf(RedisUtil.val("OESS_PAY_WAY_ZFBH5", 0L)));
        NetManager.sendMessage(OseeMessage.OseeMsgCode.S_C_GET_PAY_WAY_RESPONSE_VALUE, builder, user);
    }
}
