package com.maple.game.osee.entity.tribe;

import java.util.Date;

public class TribeWareHouse {

    private Long id;
    private Long userId;
    private Long tribeId;
    private String password;
    private Long itemId;
    private Long itemNum;
    private Long isOut;
    private Long outUserId;
    private Date outTime;
    private Date createTime;
    private String nickName;
    private String outNickName;
    private String itemName;

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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Long getItemId() {
        return itemId;
    }

    public void setItemId(Long itemId) {
        this.itemId = itemId;
    }

    public Long getItemNum() {
        return itemNum;
    }

    public void setItemNum(Long itemNum) {
        this.itemNum = itemNum;
    }

    public Long getIsOut() {
        return isOut;
    }

    public void setIsOut(Long isOut) {
        this.isOut = isOut;
    }

    public Long getOutUserId() {
        return outUserId;
    }

    public void setOutUserId(Long outUserId) {
        this.outUserId = outUserId;
    }

    public Date getOutTime() {
        return outTime;
    }

    public void setOutTime(Date outTime) {
        this.outTime = outTime;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getOutNickName() {
        return outNickName;
    }

    public void setOutNickName(String outNickName) {
        this.outNickName = outNickName;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }
}
