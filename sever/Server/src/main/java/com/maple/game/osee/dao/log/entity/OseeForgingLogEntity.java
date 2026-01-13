package com.maple.game.osee.dao.log.entity;

import com.maple.database.data.DbEntity;

/**
 * 充值记录实体
 */
public class OseeForgingLogEntity extends DbEntity {

    private static final long serialVersionUID = -1892095262961184920L;

    /**
     * 玩家id
     */
    private long userId;

    /**
     * 玩家昵称
     */
    private String nickname;

    /**
     * 消耗材料
     */
    private String payForging;

    /**
     * 奖励
     */
    private String reward;

    /**
     * 类型 1.锻造 2.合成 3.强化
     */
    private int type;

    /**
     * 是否成功
     */
    private int isSuccess;

    /**
     * 目标
     */
    private String target;

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getPayForging() {
        return payForging;
    }

    public void setPayForging(String payForging) {
        this.payForging = payForging;
    }

    public String getReward() {
        return reward;
    }

    public void setReward(String reward) {
        this.reward = reward;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getIsSuccess() {
        return isSuccess;
    }

    public void setIsSuccess(int isSuccess) {
        this.isSuccess = isSuccess;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }
}
