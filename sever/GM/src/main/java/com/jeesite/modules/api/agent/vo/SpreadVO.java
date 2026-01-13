package com.jeesite.modules.api.agent.vo;

import java.util.Date;

public class SpreadVO extends BaseVO {

    private static final long serialVersionUID = 3921553166795579010L;

    private Long playerId;
    private Long promoterId;
    private Integer gameState;
    private Integer vipLevel;

    private Date startTime;
    private Date endTime;

    public Long getPlayerId() {
        return playerId;
    }

    public void setPlayerId(Long playerId) {
        this.playerId = playerId;
    }

    public Long getPromoterId() {
        return promoterId;
    }

    public void setPromoterId(Long promoterId) {
        this.promoterId = promoterId;
    }

    public Integer getGameState() {
        return gameState;
    }

    public void setGameState(Integer gameState) {
        this.gameState = gameState;
    }

    public Integer getVipLevel() {
        return vipLevel;
    }

    public void setVipLevel(Integer vipLevel) {
        this.vipLevel = vipLevel;
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
