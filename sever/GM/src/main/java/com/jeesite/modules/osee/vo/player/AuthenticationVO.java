package com.jeesite.modules.osee.vo.player;

import com.jeesite.modules.osee.vo.BaseVO;

import java.io.Serializable;
import java.util.Date;

/**
 * 实名认证传输实体
 *
 * @author zjl
 */
public class AuthenticationVO extends BaseVO implements Serializable {

    private static final long serialVersionUID = -7694783422521321664L;

    private Long playerId;      // 玩家id
    private String nickName;    // 玩家昵称
    private String realName;    // 真实姓名
    private String phoneNum;    // 手机号
    private Date startTime;     // 开始时间
    private Date endTime;       // 结束时间

    public Long getPlayerId() {
        return playerId;
    }

    public void setPlayerId(Long playerId) {
        this.playerId = playerId;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public String getPhoneNum() {
        return phoneNum;
    }

    public void setPhoneNum(String phoneNum) {
        this.phoneNum = phoneNum;
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
