package com.maple.game.osee.entity.tribe;

import java.util.Date;

public class TribeUser {

    private Long id;
    private Long userId;
    private Long tribeId;
    private Long position;
    private Long userLevel;
    private Long money;
    private String userName;
    private Date createTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getTribeId() {
        return tribeId;
    }

    public void setTribeId(Long tribeId) {
        this.tribeId = tribeId;
    }

    public Long getPosition() {
        return position;
    }

    public void setPosition(Long position) {
        this.position = position;
    }

    public Long getUserLevel() {
        return userLevel;
    }

    public void setUserLevel(Long userLevel) {
        this.userLevel = userLevel;
    }

    public Long getMoney() {
        return money;
    }

    public void setMoney(Long money) {
        this.money = money;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}
