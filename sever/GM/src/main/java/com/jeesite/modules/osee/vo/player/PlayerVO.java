package com.jeesite.modules.osee.vo.player;

import com.jeesite.modules.osee.vo.BaseVO;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 玩家数据传输实体类
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class PlayerVO extends BaseVO implements Serializable {

    private static final long serialVersionUID = -5591142220317370957L;

    private String operatorName; // 操作人名称

    private Integer ag;// 玩家AG参数
    private Integer one;// 玩家一级倍率控制
    private Integer[] ags;
    private Integer[] ones;

    private Date startTime;
    private Date endTime;
    private Long playerId;      // 玩家id
    private String username;
    private String nickname;
    private Integer userState;  // 用户状态 0:不指定 1:正常 2:冻结
    private Integer gameState;  // 游戏状态 0:不指定 1:离线 2:在线 3:游戏大厅 4:捕鱼 5:拼十 6:二八杠 7:水果拉霸 8:五子棋
    private Integer vipLevel;   // vip等级 0:不指定 1-12:vip等级
    private Integer loginType;  // 登录方式 0:不指定 1:微信 2:账号
    private Integer loseControl;// 输赢控制 0:不指定 1:正常 2:必输
    private Integer playerType = 1; // 玩家类型 0:不指定 1:玩家 2:代理
    private Long playerSendGift; //赠送类型 0：正常 1：限制
    private Integer ykbfFlag; // 盈亏爆发 0：正常 1：限制
    private String bankpassword; // 保险箱密码
    private String password;    // 玩家密码（更新用）
    private Double fishingProb; // 捕鱼参数（更新用）
    private Double[] burstTmax; // 个控爆发t值上限（更新用）
    private Double[] burstTmin; // 个控爆发t值下限（更新用）
    private Double[] recoveryTmax; // 个控回收t值上限（更新用）
    private Double[] recoveryTmin; // 个控回收t值下限（更新用）

    private BigDecimal fishingProbChallEnge; // 龙晶场捕鱼参数（更新用）
    private Double[] burstTmaxChallEnge; // 龙晶场个控爆发t值上限（更新用）
    private Double[] burstTminChallEnge; // 龙晶场个控爆发t值下限（更新用）
    private Double[] recoveryTmaxChallEnge; // 龙晶场个控回收t值上限（更新用）
    private Double[] recoveryTminChallEnge; // 龙晶场个控回收t值下限（更新用）

    private int bfType = 4; // 爆发类型
    private int hsType = 4; // 回收类型

    private Integer firstCommissionRate; // 一级代理佣金比例 0-100 整数
    private Integer secondCommissionRate; // 二级代理佣金比例 0-100 整数
    private Integer openChessCards; // 代理是否开启棋牌 0-不开，1-开启
    private Integer openChessCards1; // 代理是否开启棋牌 0-不开，1-开启

    private Double rate; // 渠道商分成比例
    private Double firstrpay; // 官方支付分成比例
    private Double otherpay; // 分发支付分成比例
    private String playerRemark; // 渠道商备注
    private String playerRemark1; // 渠道商备注
    private String payWay;//支付渠道
    private String payWayOnline;//线上支付渠道
    private Double rateOnline; // 线上渠道商分成比例

    private String userIp; // 用户ip

    private Long gameId; // 玩家id

    private String phone; // 手机号

    private List<Long> fishIdArr;

    private List<Integer> fishCountArr;

    private List<cn.hutool.json.JSONObject> specifyFishMapList;

    public Integer getAg() {
        return ag;
    }

    public void setAg(Integer ag) {
        this.ag = ag;
    }

    public Integer getOne() {
        return one;
    }

    public void setOne(Integer one) {
        this.one = one;
    }

    public Integer[] getAgs() {
        return ags;
    }

    public void setAgs(Integer[] ags) {
        this.ags = ags;
    }

    public Integer[] getOnes() {
        return ones;
    }

    public void setOnes(Integer[] ones) {
        this.ones = ones;
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

    public Long getPlayerId() {
        return playerId;
    }

    public void setPlayerId(Long playerId) {
        this.playerId = playerId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public Integer getUserState() {
        return userState;
    }

    public void setUserState(Integer userState) {
        this.userState = userState;
    }

    public Integer getGameState() {
        return gameState;
    }

    public void setGameState(Integer gameState) {
        this.gameState = gameState;
    }

    public Integer getVipLevel() {
        return vipLevel;
    }

    public void setVipLevel(Integer vipLevel) {
        this.vipLevel = vipLevel;
    }

    public Integer getLoginType() {
        return loginType;
    }

    public void setLoginType(Integer loginType) {
        this.loginType = loginType;
    }

    public Integer getLoseControl() {
        return loseControl;
    }

    public void setLoseControl(Integer loseControl) {
        this.loseControl = loseControl;
    }

    public Integer getPlayerType() {
        return playerType;
    }

    public void setPlayerType(Integer playerType) {
        this.playerType = playerType;
    }

    public String getBankpassword() {
        return bankpassword;
    }

    public void setBankpassword(String bankpassword) {
        this.bankpassword = bankpassword;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Integer getFirstCommissionRate() {
        return firstCommissionRate;
    }

    public void setFirstCommissionRate(Integer firstCommissionRate) {
        this.firstCommissionRate = firstCommissionRate;
    }

    public Integer getSecondCommissionRate() {
        return secondCommissionRate;
    }

    public void setSecondCommissionRate(Integer secondCommissionRate) {
        this.secondCommissionRate = secondCommissionRate;
    }

    public Integer getOpenChessCards() {
        return openChessCards;
    }

    public void setOpenChessCards(Integer openChessCards) {
        this.openChessCards = openChessCards;
    }

    public Integer getOpenChessCards1() {
        return openChessCards1;
    }

    public void setOpenChessCards1(Integer openChessCards1) {
        this.openChessCards1 = openChessCards1;
    }

    public Double getRate() {
        return rate;
    }

    public void setRate(Double rate) {
        this.rate = rate;
    }

    public Double getFirstrpay() {
        return firstrpay;
    }

    public void setFirstrpay(Double firstrpay) {
        this.firstrpay = firstrpay;
    }

    public Double getOtherpay() {
        return otherpay;
    }

    public void setOtherpay(Double otherpay) {
        this.otherpay = otherpay;
    }

    public String getPlayerRemark() {
        return playerRemark;
    }

    public void setPlayerRemark(String playerRemark) {
        this.playerRemark = playerRemark;
    }

    public String getPayWay() {
        return payWay;
    }

    public void setPayWay(String payWay) {
        this.payWay = payWay;
    }

    public String getPayWayOnline() {
        return payWayOnline;
    }

    public void setPayWayOnline(String payWayOnline) {
        this.payWayOnline = payWayOnline;
    }

    public Double getRateOnline() {
        return rateOnline;
    }

    public void setRateOnline(Double rateOnline) {
        this.rateOnline = rateOnline;
    }

    public Long getPlayerSendGift() {
        return playerSendGift;
    }

    public void setPlayerSendGift(Long playerSendGift) {
        this.playerSendGift = playerSendGift;
    }

}
