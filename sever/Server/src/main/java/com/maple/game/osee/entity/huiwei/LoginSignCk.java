package com.maple.game.osee.entity.huiwei;

/**
 * 华为登录接口实体
 */
public class LoginSignCk {

    private String ts;

    private String playerId;

    private String playerLevel;

    private String playerSSign;

    private String cpSign;

    private String playerName;

    public String getTs() {
        return ts;
    }

    public void setTs(String ts) {
        this.ts = ts;
    }

    public String getPlayerId() {
        return playerId;
    }

    public void setPlayerId(String playerId) {
        this.playerId = playerId;
    }

    public String getPlayerLevel() {
        return playerLevel;
    }

    public void setPlayerLevel(String playerLevel) {
        this.playerLevel = playerLevel;
    }

    public String getPlayerSSign() {
        return playerSSign;
    }

    public void setPlayerSSign(String playerSSign) {
        this.playerSSign = playerSSign;
    }

    public String getCpSign() {
        return cpSign;
    }

    public void setCpSign(String cpSign) {
        this.cpSign = cpSign;
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }
}
