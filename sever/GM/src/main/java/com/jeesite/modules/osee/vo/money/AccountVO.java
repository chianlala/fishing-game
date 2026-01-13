package com.jeesite.modules.osee.vo.money;

import com.jeesite.modules.osee.vo.BaseVO;

import java.io.Serializable;
import java.util.Date;

/**
 * 账户变动明细数据传输实体类
 *
 * @author zjl
 */
public class AccountVO extends BaseVO implements Serializable {

    private static final long serialVersionUID = 354612215267403721L;

    private Long playerId;          // 玩家id
    private String nickname;        // 玩家昵称

    private Integer type;           // 变动原因
    // 1:CDK兑换 2:第三方充值 3:后台充值
    // 4:后台扣除 5:商城兑换 6:轮盘支付
    // 7:轮盘中奖 8:签到 9:任务奖励
    // 10:实名认证 11:真人拼十获胜 12:真人拼十失败
    // 13:二八杠胜利 14:二八杠失败 15:五子棋胜利
    // 16:五子棋失败 17:五子棋房费 18:捕鱼产出消耗
    // 19:水果拉霸筹码消耗 20:水果拉霸中奖 21:保险箱存入 22:保险箱取出

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

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
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
