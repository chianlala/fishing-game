package com.maple.game.osee.entity;

/**
 * 账户变动原因
 */
public enum ItemChangeReason {

    USE_CDK(1, "CDK兑换"),

    THIRD_PARTY_RECHARGE(2, "第三方充值"),

    GM_RECHARGE(3, "后台充值"),

    GM_DEDUCT(4, "后台扣除"),

    SHOPPING(5, "商城兑换"),

    LOTTERY_PAY(6, "轮盘支付"),

    LOTTERY_WIN(7, "轮盘中奖"),

    SIGN_IN(8, "签到"),

    TASK_FINISH(9, "任务奖励"),

    AUTHENTICATION(10, "实名认证"),

    FIGHT_TEN_WIN(11, "真人拼十获胜"),

    FIGHT_TEN_LOSE(12, "真人拼十失败"),

    ERBA_GANG_WIN(13, "二八杠胜利"),

    ERBA_GANG_LOSE(14, "二八杠失败"),

    GOBANG_RESULT(15, "五子棋结算"),

    // GOBANG_LOSE(16, "五子棋失败"),

    GOBANG_FEE(17, "五子棋房费"),

    FISHING_RESULT(18, "捕鱼产出消耗"),

    FRUIT_LABA_FEE(19, "水果拉霸筹码消耗"),

    FRUIT_LABA_WIN(20, "水果拉霸中奖"),

    BANK_IN(21, "保险箱存入"),

    BANK_OUT(22, "保险箱取出"),

    WECHAT_SHARE(23, "微信分享"),

    ACCOUNT_SET(24, "账号设置"),

    AGENT_COMMISSION_EXCHANGE(25, "佣金兑换"),

    GIVE_GIFT(26, "礼物赠送"),

    USE_ITEM(27, "物品使用"),

    UNLOCK_BATTERY(28, "炮台解锁"),

    CHANGE_NICKNAME(29, "更改昵称"),

    LEVEL_UP(30, "升级奖励"),

    FIGHT_TEN_CHALLENGE_WIN(31, "拼十挑战赛获胜"),

    FIGHT_TEN_CHALLENGE_LOSE(32, "拼十挑战赛失败"),

    ACTIVE_AGENT(33, "全民推广奖励"),

    FISHING_GRANDPRIX_JOIN_ROOM(34, "捕鱼大奖赛入场"),

    BUY_DAILY_BAG(34, "购买福袋"),

    BUY_ONCE_BAG(35, "购买特惠礼包"),

    BUY_MONEY_CARD(36, "购买金币卡"),

    MONEY_CARD(37, "金币卡奖励"),

    BUY_BATTERY_LEVEL(38, "购买炮台直升"),

    FIRST_ADDMONEY(39, "首充"),

    MONTH_CARD(40, "月卡充值"),

    FISHING_GRANDPRIX_CAERD(41, "捕鱼大奖赛结算奖励"),

    VIP_DAY_CARD(42, "vip每日奖励"),

    BUY_DAILY_GIFT(43, "购买每日礼包"),

    TRIBE_ES(44, "创建部落"),

    TRIBE_WAREHOUSE(45, "部落仓库"),

    GAME_S(46, "小游戏"),

    RECHARGE_REWORD(47, "充值返利"),

    OPEN_OCEAN(48, "开启海洋使者"),

    GET_OCEAN_REWORD(49, "领取海洋使者推广奖励"),

    GET_TRIBE_GIFT(50, "领取部落礼包"),

    ANIMALS_BET(52, "飞禽走兽下注"),

    ANIMALS_STATEMENT(53, "飞禽走兽结算"),

    BAI_REN_BET(52, "百人拼十下注"),

    BAI_REN_STATEMENT(53, "百人拼十结算"),

    JUNGLE_KINGDOM_STATE(151, "丛林野兽旋转"),

    JUNGLE_KINGDOM_WIN(152, "丛林野兽中奖"),

    WILD_TURKEY_STATE(153, "野性火鸡旋转"),

    WILD_TURKEY_WIN(154, "野性火鸡中奖"),

    BEAST_LEGEND_STATE(155, "野兽传说旋转"),

    BEAST_LEGEND_WIN(156, "野兽传说中奖"),

    MAGIC_WIZARD_STATE(157, "魔法巫师旋转"),

    MAGIC_WIZARD_WIN(158, "魔法巫师中奖"),

    KRAKEN_STATE(159, "克拉肯旋转"),

    KRAKEN_WIN(160, "克拉肯中奖"),

    ;

    /**
     * 原因id
     */
    private int id;

    /**
     * 原因说明
     */
    private String info;

    ItemChangeReason(int id, String info) {
        this.id = id;
        this.info = info;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }
}
