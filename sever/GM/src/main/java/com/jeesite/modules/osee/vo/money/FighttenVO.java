package com.jeesite.modules.osee.vo.money;

import com.jeesite.modules.osee.vo.BaseVO;

import java.io.Serializable;
import java.util.Date;

public class FighttenVO extends BaseVO implements Serializable {

    private static final long serialVersionUID = 2463455523605061229L;
    private Long playerId;
    private Date startTime;
    private Date endTime;

    public Long getPlayerId() {
        return playerId;
    }

    public void setPlayerId(Long playerId) {
        this.playerId = playerId;
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
