package com.jeesite.modules.osee.vo.agent;

import com.jeesite.modules.osee.vo.BaseVO;

import java.io.Serializable;
import java.util.Date;

/**
 * 代理提现申请
 */
public class WithdrawVO extends BaseVO implements Serializable {

    private static final long serialVersionUID = -8001370413472317595L;

    private Long channelId; // 渠道ID
    private String nickname; // 渠道昵称
    private Integer state; // 提现状态 1-未处理 2-已处理 3-已拒绝

    // 提现时间
    private Date startTime;
    private Date endTime;

    public Long getChannelId() {
        return channelId;
    }

    public void setChannelId(Long channelId) {
        this.channelId = channelId;
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
