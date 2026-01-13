package com.jeesite.modules.osee.vo.money;

import com.jeesite.modules.osee.vo.BaseVO;

import java.io.Serializable;
import java.util.Date;

/**
 * 礼物赠送记录数据传输实体
 *
 * @author Junlong
 */
public class TribeWareHouseVO extends BaseVO implements Serializable {

    private static final long serialVersionUID = -2932694888774626744L;


    private Long playerId;          // 礼物赠送，被赠玩家的ID

    private Date startTime;         // 起始时间 (时间戳)
    private Date endTime;           // 结束时间 (时间戳)

    private Long agentId;

    public Long getPlayerId() {
        return playerId;
    }

    public void setPlayerId(Long playerId) {
        this.playerId = playerId;
    }

    public Long getAgentId() {
        return agentId;
    }

    public void setAgentId(Long agentId) {
        this.agentId = agentId;
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
