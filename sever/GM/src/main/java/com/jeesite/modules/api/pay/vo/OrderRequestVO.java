package com.jeesite.modules.api.pay.vo;

import java.io.Serializable;

/**
 * 下单请求数据传输实体类
 *
 * @author zjl
 */
public class OrderRequestVO implements Serializable {

    private static final long serialVersionUID = 6386074838957709367L;

    private Integer payType;    // 支付类型 1:微信官方 2:支付宝官方 3:第三方微信扫码 4:第三方支付宝扫码 5:第三方支付宝APP 其他:待定 跟PayConstants内相关常量对应
    private String tradeType;   // 交易类型 APP JSAPI NATIVE MWEB

    private Long playerId;      // 购买商品的玩家ID
    private Integer itemId;     // 需要购买的商品物品ID
    private String playerIp;     //用户ip

    private String payphone; // 手机号吗

    public String getPayphone() {
        return payphone;
    }

    public void setPayphone(String payphone) {
        this.payphone = payphone;
    }

    public Integer getPayType() {
        return payType;
    }

    public void setPayType(Integer payType) {
        this.payType = payType;
    }

    public String getTradeType() {
        return tradeType;
    }

    public void setTradeType(String tradeType) {
        this.tradeType = tradeType;
    }

    public Long getPlayerId() {
        return playerId;
    }

    public void setPlayerId(Long playerId) {
        this.playerId = playerId;
    }

    public Integer getItemId() {
        return itemId;
    }

    public void setItemId(Integer itemId) {
        this.itemId = itemId;
    }

    public String getPlayerIp() {
        return playerIp;
    }

    public void setPlayerIp(String playerIp) {
        this.playerIp = playerIp;
    }
}
