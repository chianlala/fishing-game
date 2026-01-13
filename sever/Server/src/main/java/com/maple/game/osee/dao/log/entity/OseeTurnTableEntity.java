package com.maple.game.osee.dao.log.entity;

import com.maple.database.data.DbEntity;

/**
 * 充值记录实体
 */
public class OseeTurnTableEntity extends DbEntity {

    private static final long serialVersionUID = -1892095262961184920L;

    /**
     * 玩家id
     */
    private long userId;

    /**
     * 玩家昵称
     */
    private String userName;

    /**
     * 消耗材料
     */
    private long itemId;

    /**
     * 奖励
     */
    private long itemNum;

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public long getItemId() {
        return itemId;
    }

    public void setItemId(long itemId) {
        this.itemId = itemId;
    }

    public long getItemNum() {
        return itemNum;
    }

    public void setItemNum(long itemNum) {
        this.itemNum = itemNum;
    }
}
