package com.jeesite.modules.osee.vo.money;

import com.jeesite.modules.osee.vo.BaseVO;

import java.io.Serializable;
import java.util.Date;

/**
 * 充值明细数据传输实体类
 *
 * @author zjl
 */
public class ForgingVO extends BaseVO implements Serializable {

    private static final long serialVersionUID = -4490180356182504690L;

    private Integer type;   // 充值方式 1:微信 2:支付宝 3:后台
    private Integer isSuccess;     // 订单状态 1:成功 2:失败

    private Long playerId;          // 玩家id
    private String nickname;        // 玩家昵称

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

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Integer getIsSuccess() {
        return isSuccess;
    }

    public void setIsSuccess(Integer isSuccess) {
        this.isSuccess = isSuccess;
    }
}
