package com.jeesite.modules.osee.vo.money;

import com.jeesite.modules.osee.vo.BaseVO;

import java.io.Serializable;
import java.util.Date;

/**
 * 支出明细数据传输数据实体类
 *
 * @author zjl
 */
public class ExpendVO extends BaseVO implements Serializable {

    private static final long serialVersionUID = -8310366854152922891L;

    private Long playerId;          // 玩家id
    private String nickname;        // 玩家昵称
    private Integer payType;        // 支出类型 1:实名认证 2:任务奖励 3:每日签到 4:CDK兑换

    private Date startTime;         // 起始时间 (时间戳)
    private Date endTime;           // 结束时间 (时间戳)

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

    public Integer getPayType() {
        return payType;
    }

    public void setPayType(Integer payType) {
        this.payType = payType;
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
