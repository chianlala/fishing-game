package com.jeesite.modules.osee.vo.agent;

import com.jeesite.modules.osee.vo.BaseVO;

import java.io.Serializable;
import java.util.Date;

/**
 * 兑换明细传输实体类
 *
 * @author Junlong
 */
public class CommissionExchangeVO extends BaseVO implements Serializable {

    private static final long serialVersionUID = -5890540479822158468L;

    private Long agentId;    // 代理玩家ID

    private Date startTime;
    private Date endTime;
//    private Long playerId;   // 贡献人id
//    private String nickname; // 贡献人昵称

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

//    public Long getPlayerId() {
//        return playerId;
//    }
//
//    public void setPlayerId(Long playerId) {
//        this.playerId = playerId;
//    }
//
//    public String getNickname() {
//        return nickname;
//    }
//
//    public void setNickname(String nickname) {
//        this.nickname = nickname;
//    }
}
