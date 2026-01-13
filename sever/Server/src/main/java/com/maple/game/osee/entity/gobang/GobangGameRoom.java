package com.maple.game.osee.entity.gobang;

import com.maple.gamebase.data.BaseGamePlayer;
import com.maple.gamebase.data.gobang.BaseGobangRoom;

/**
 * 1688五子棋房间
 */
public class GobangGameRoom extends BaseGobangRoom {

    /**
     * 学费
     */
    private long tuition;

    /**
     * 是否已开局
     */
    private boolean start;

    public long getTuition() {
        return tuition;
    }

    public void setTuition(long tuition) {
        this.tuition = tuition;
    }

    public boolean isStart() {
        return start;
    }

    public void setStart(boolean start) {
        this.start = start;
    }

    public long getNowPlayerId() {
        GobangGamePlayer gamePlayer = getGamePlayerBySeat(getNowPlayerSeat());
        if (gamePlayer != null) {
            return gamePlayer.getId();
        }
        return 0;
    }

    @Override
    public void reset() {
        super.reset();
        start = false;
        for (BaseGamePlayer player : getGamePlayers()) {
            if (player != null) {
                ((GobangGamePlayer)player).setReady(false);
            }
        }
    }

    /**
     * 房间序号 房间表(cfg_fish_ccxx)里面的：sessionId
     * @return
     */
    @Override
    public int getGameId() {
        return 101401;
    }

}
