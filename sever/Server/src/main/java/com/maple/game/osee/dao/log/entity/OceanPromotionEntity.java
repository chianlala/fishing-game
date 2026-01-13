package com.maple.game.osee.dao.log.entity;

import com.maple.database.data.DbEntity;

/**
 * @author Junlong
 */
public class OceanPromotionEntity extends DbEntity {

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
     * 使者ID
     */
    private Long oceanUserId;

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

    public Long getOceanUserId() {
        return oceanUserId;
    }

    public void setOceanUserId(Long oceanUserId) {
        this.oceanUserId = oceanUserId;
    }
}
