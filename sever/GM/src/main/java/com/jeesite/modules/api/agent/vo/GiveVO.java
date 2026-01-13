package com.jeesite.modules.api.agent.vo;


import java.io.Serializable;
import java.util.Date;

/**
 * 礼物赠送记录数据传输实体
 *
 * @author Junlong
 */
public class GiveVO extends BaseVO implements Serializable {

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

    //    public Long getFromId() {
//        return fromId;
//    }
//
//    public void setFromId(Long fromId) {
//        this.fromId = fromId;
//    }
//
//    public String getFromName() {
//        return fromName;
//    }
//
//    public void setFromName(String fromName) {
//        this.fromName = fromName;
//    }
//
//    public Long getToId() {
//        return toId;
//    }
//
//    public void setToId(Long toId) {
//        this.toId = toId;
//    }
//
//    public String getToName() {
//        return toName;
//    }
//
//    public void setToName(String toName) {
//        this.toName = toName;
//    }

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
