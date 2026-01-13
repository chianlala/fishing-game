package com.maple.game.osee.dao.log.entity;

import com.maple.database.data.DbEntity;

/**
 * @author Junlong
 */
public class OceanRewordEntity extends DbEntity {

    private static final long serialVersionUID = 263808844889012331L;

    /**
     * 玩家昵称
     */
    private String nickName;

    /**
     * 玩家ID
     */
    private Long userId;

    /**
     * 玩家ID
     */
    private Long oceanUserId;

    /**
     * 钻石
     */
    private Long diamond;

    /**
     * 商品
     */
    private String shopName;

    /**
     * 奖励
     */
    private String reword;

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getDiamond() {
        return diamond;
    }

    public void setDiamond(Long diamond) {
        this.diamond = diamond;
    }

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public String getReword() {
        return reword;
    }

    public void setReword(String reword) {
        this.reword = reword;
    }

    public Long getOceanUserId() {
        return oceanUserId;
    }

    public void setOceanUserId(Long oceanUserId) {
        this.oceanUserId = oceanUserId;
    }
}
