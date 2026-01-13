package com.maple.game.osee.dao.log.entity;

import com.maple.database.data.DbEntity;

/**
 * @author Junlong
 */
public class OceanGetRewordEntity extends DbEntity {

    private static final long serialVersionUID = 263808844889012331L;

    /**
     * 玩家ID
     */
    private Long userId;

    /**
     * 数量
     */
    private Long num;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getNum() {
        return num;
    }

    public void setNum(Long num) {
        this.num = num;
    }
}
