package com.jeesite.modules.osee.vo.money;

import com.jeesite.modules.osee.vo.BaseVO;

import java.util.Date;

/**
 * 龙晶兑换记录传输实体
 *
 * @author Junlong
 */
public class CrystalExchangeVO extends BaseVO {

    private static final long serialVersionUID = 5276460484065282396L;

    private Date startTime;         // 起始时间 (时间戳)
    private Date endTime;           // 结束时间 (时间戳)

    private Long playerId;          // 玩家id
    private Integer exchangeType;   // 兑换类型

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

    public Integer getExchangeType() {
        return exchangeType;
    }

    public void setExchangeType(Integer exchangeType) {
        this.exchangeType = exchangeType;
    }
}
