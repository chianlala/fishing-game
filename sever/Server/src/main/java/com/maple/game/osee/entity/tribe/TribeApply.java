package com.maple.game.osee.entity.tribe;

import java.util.Date;

public class TribeApply {

    private Long id;
    private Long userId;
    private Long tribeId;
    private Long Isadopt;
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

    public Long getIsadopt() {
        return Isadopt;
    }

    public void setIsadopt(Long isadopt) {
        Isadopt = isadopt;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}
