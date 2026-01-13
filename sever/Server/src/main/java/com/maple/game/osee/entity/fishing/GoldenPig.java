package com.maple.game.osee.entity.fishing;

import java.util.Date;

/**
 * 鱼类数据
 */
public class GoldenPig {

    private long id;

    private long userId;

    private String nickName;

    private long preLottery;

    private long num;

    private long changeLottery;

    private long afterLottery;

    private Date createTime;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public long getPreLottery() {
        return preLottery;
    }

    public void setPreLottery(long preLottery) {
        this.preLottery = preLottery;
    }

    public long getNum() {
        return num;
    }

    public void setNum(long num) {
        this.num = num;
    }

    public long getChangeLottery() {
        return changeLottery;
    }

    public void setChangeLottery(long changeLottery) {
        this.changeLottery = changeLottery;
    }

    public long getAfterLottery() {
        return afterLottery;
    }

    public void setAfterLottery(long afterLottery) {
        this.afterLottery = afterLottery;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}
