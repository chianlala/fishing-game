package com.jeesite.modules.api.agent.vo;

import java.util.Date;

public class MoneyVO extends BaseVO {

    private static final long serialVersionUID = -534800106362424460L;

    private Long playerId;
    private Long spreadId;

    private Date startTime;
    private Date endTime;

    public Long getPlayerId() {
        return playerId;
    }

    public void setPlayerId(Long playerId) {
        this.playerId = playerId;
    }

    public Long getSpreadId() {
        return spreadId;
    }

    public void setSpreadId(Long spreadId) {
        this.spreadId = spreadId;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }
}
