package com.jeesite.modules.api.agent.vo;

import java.util.Date;

public class UserVO extends BaseVO {

    private static final long serialVersionUID = 5027406213637768953L;

    private Date month;

    private Long playerId;
    private String nickname;

    public Date getMonth() {
        return month;
    }

    public void setMonth(Date month) {
        this.month = month;
    }

    public Long getPlayerId() {
        return playerId;
    }

    public void setPlayerId(Long playerId) {
        this.playerId = playerId;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }
}
