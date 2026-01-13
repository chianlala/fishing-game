package com.jeesite.modules.osee.vo.money;

import com.jeesite.modules.osee.vo.BaseVO;

import java.io.Serializable;
import java.util.Date;

/**
 * 扣除明细数据传输实体类
 *
 * @author zjl
 */
public class DeductVO extends BaseVO implements Serializable {

    private static final long serialVersionUID = 2500075319987591752L;

    private String orderNum;        // 订单号
    private Long playerId;          // 玩家id
    private String nickname;        // 玩家昵称
    private String creator;         // 创建人
    private Integer deductType;     // 扣除类型 4:钻石 3:奖券 1:金币

    private Date startTime;         // 起始时间 (时间戳)
    private Date endTime;           // 结束时间 (时间戳)

    public String getOrderNum() {
        return orderNum;
    }

    public void setOrderNum(String orderNum) {
        this.orderNum = orderNum;
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

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public Integer getDeductType() {
        return deductType;
    }

    public void setDeductType(Integer deductType) {
        this.deductType = deductType;
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
