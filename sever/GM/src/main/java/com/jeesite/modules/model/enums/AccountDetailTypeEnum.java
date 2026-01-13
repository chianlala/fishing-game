package com.jeesite.modules.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AccountDetailTypeEnum {

    JOIN_ROOM(1, "加入房间"), //
    EXIT_ROOM(2, "退出房间"), //
    LOGIN_GAME(3, "登录游戏"), //
    EXIT_GAME(4, "退出游戏"), //
    BUY_GOODS(5, "购买商品"), //
    GOODS_TO_ACCOUNT(6, "商品到账"), //
    USE_PROPS(7, "使用道具"), //
    MAIL_COLLECTION(8, "邮件领取"), //
    KILL_FISH(9, "击杀鱼种"), //
    BACKGROUND_RECHARGE(10, "后台充值"), //
    BACKGROUND_DEDUCTION(11, "后台扣除"), //
    SETTLEMENT_GOLD_COIN(12, "结算金币"), //
    NODE_CHANGE(13, "节点变化"), //
    INDIVIDUAL_CONTROL_OPENING(14, "调节开启"), //
    INDIVIDUAL_CONTROL_SHUTDOWN(15, "调节关闭"), //
    CDK(16, "cdk兑换"), //
    GIVE_GIFT_SEND(17, "礼物赠送"), //
    BANK_MONEY(19, "保险箱"), //
    SLOTS_WIN(20, "slots中奖"), //
    REDEEM_GOODS(21, "兑换商品"), //
    REDEEM_GOODS_TO_ACCOUNT(22, "兑换商品到账"), //
    MANUAL_WITHDRAW(23, "提现发起"), //
    MANUAL_WITHDRAW_REJECT(24, "提现拒绝"), //
    GRAND_PRIX_APPLICATION(25, "大奖赛报名"), //

    BATTLE_ROYALE(26, "大逃杀"), //
    BIRDS_AND_BEASTS(27, "飞禽走兽"), //
    A_HUNDRED_MEN_FIGHT_TEN(28, "百人拼十"), //

    ;

    private final int value;
    private final String name;

}
