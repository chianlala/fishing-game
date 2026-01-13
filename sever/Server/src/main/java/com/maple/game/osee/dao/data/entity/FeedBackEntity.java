package com.maple.game.osee.dao.data.entity;

import com.maple.database.data.DbEntity;

/**
 * 游戏内消息/邮件数据实体类
 *
 * @author Junlong
 */
public class FeedBackEntity extends DbEntity {

    private static final long serialVersionUID = 4104432655046923294L;

    /**
     * 内容
     */
    private Long userId;
    /**
     * 内容
     */
    private String context;

    /**
     * 内容
     */
    private String userName;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = context;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
