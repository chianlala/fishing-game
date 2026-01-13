package com.maple.game.osee.entity.fishing.game;

/**
 * 鱼类数据
 */
public class FishKj {

    /**
     * 5牌数字1
     */
    private long num;

    /**
     * 5牌数字2
     */
    private long num1;

    /**
     * 5牌数字3
     */
    private long num2;

    /**
     * 5牌数字4
     */
    private long num3;

    /**
     * 5牌数字5
     */
    private long num4;

    /**
     * 翻牌数字
     */
    private long fNum;

    /**
     * 符石倍数
     */
    private long mult;

    /**
     * 翻牌倍数
     */
    private long mult1;

    /**
     * 牌型
     */
    private long cardType;

    /**
     * 奖励
     */
    private double reword;

    /**
     * 炮倍
     */
    private long batteryMult;

    /**
     * 玩家id
     */
    private long userId;

    public long getNum() {
        return num;
    }

    public void setNum(long num) {
        this.num = num;
    }

    public long getNum1() {
        return num1;
    }

    public void setNum1(long num1) {
        this.num1 = num1;
    }

    public long getNum2() {
        return num2;
    }

    public void setNum2(long num2) {
        this.num2 = num2;
    }

    public long getNum3() {
        return num3;
    }

    public void setNum3(long num3) {
        this.num3 = num3;
    }

    public long getNum4() {
        return num4;
    }

    public void setNum4(long num4) {
        this.num4 = num4;
    }

    public long getfNum() {
        return fNum;
    }

    public void setfNum(long fNum) {
        this.fNum = fNum;
    }

    public long getMult() {
        return mult;
    }

    public void setMult(long mult) {
        this.mult = mult;
    }

    public long getMult1() {
        return mult1;
    }

    public void setMult1(long mult1) {
        this.mult1 = mult1;
    }

    public long getCardType() {
        return cardType;
    }

    public void setCardType(long cardType) {
        this.cardType = cardType;
    }

    public double getReword() {
        return reword;
    }

    public void setReword(double reword) {
        this.reword = reword;
    }

    public long getBatteryMult() {
        return batteryMult;
    }

    public void setBatteryMult(long batteryMult) {
        this.batteryMult = batteryMult;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }
}
