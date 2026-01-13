package com.maple.game.osee.entity.fishing.challenge;

import java.util.Date;

/**
 * 鱼类数据
 */
public class FishJc {

    private long id;

    private long userId;

    private String nickName;

    private long vip;

    private long type;

    private long roomIndex;

    private long num;

    private long num1;

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

    public long getVip() {
        return vip;
    }

    public void setVip(long vip) {
        this.vip = vip;
    }

    public long getNum() {
        return num;
    }

    public void setNum(long num) {
        this.num = num;
    }

    public long getNum1() {
        return num1;
    }

    public void setNum1(long num1) {
        this.num1 = num1;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public long getType() {
        return type;
    }

    public void setType(long type) {
        this.type = type;
    }

    public long getRoomIndex() {
        return roomIndex;
    }

    public void setRoomIndex(long roomIndex) {
        this.roomIndex = roomIndex;
    }
}
