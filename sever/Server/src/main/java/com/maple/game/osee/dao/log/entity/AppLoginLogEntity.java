package com.maple.game.osee.dao.log.entity;

import java.util.Date;

import com.maple.database.data.DbEntity;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class AppLoginLogEntity extends DbEntity {

    private static final long serialVersionUID = -4631967146495030846L;
    private long id; // 登录日志表

    private long userId; // 玩家id

    private Date createTime;

    private Date exitTime;

    @Override
    public long getId() {
        return id;
    }

    @Override
    public void setId(long id) {
        this.id = id;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    @Override
    public Date getCreateTime() {
        return createTime;
    }

    @Override
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getExitTime() {
        return exitTime;
    }

    public void setExitTime(Date exitTime) {
        this.exitTime = exitTime;
    }
}
