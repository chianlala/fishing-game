package com.jeesite.modules.osee.vo.money;

import com.jeesite.modules.osee.vo.BaseVO;

import java.io.Serializable;
import java.util.Date;

/**
 * 抽水明细数据传输实体类
 *
 * @author zjl
 */
public class EarningVO extends BaseVO implements Serializable {

    private static final long serialVersionUID = 178819924397280962L;

    private Long playerId;          // 玩家id
    private String nickname;        // 玩家昵称
    private Integer game;           // 游戏 3:五子棋 4:水果拉霸 5:二八杠

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

    public Integer getGame() {
        return game;
    }

    public void setGame(Integer game) {
        this.game = game;
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
