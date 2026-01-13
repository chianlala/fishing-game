package com.maple.game.osee.dao.log.entity;

import com.maple.database.data.DbEntity;

import lombok.Data;

@Data
public class BaiRenRecordLogEntity extends DbEntity {
    /**
     * 玩家id
     */
    private long playerId;

    /**
     * 用户昵称
     */
    private String nickname;

    /**
     * 下注金额
     */
    private String input;


    /**
     * 牌型
     */
    private String cardType;

    /**
     * 账户金币变动数额
     */
    private long money;

    /**
     * 游戏前剩余金币
     */
    private long playBeforeMoney;

    /**
     * 游戏后剩余金币
     */
    private long playAfterMoney;

}
