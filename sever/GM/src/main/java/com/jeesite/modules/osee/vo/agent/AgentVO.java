package com.jeesite.modules.osee.vo.agent;

import com.jeesite.modules.osee.vo.BaseVO;

import java.io.Serializable;
import java.util.Date;

/**
 * 代理实体传输类
 *
 * @author Junlong
 */
public class AgentVO extends BaseVO implements Serializable {

    private static final long serialVersionUID = -3975290998025894279L;

    private Date startTime;
    private Date endTime;
    private Long playerId;
    private String nickname;
    private Integer state; // 代理状态：0-不指定 1-正常 2-冻结
    private Integer level; // 代理等级：0-不指定 1-一级代理 2-二级代理

    private Date month; // 月度年-月

    public Date getMonth() {
        return month;
    }

    public void setMonth(Date month) {
        this.month = month;
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

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }
}
