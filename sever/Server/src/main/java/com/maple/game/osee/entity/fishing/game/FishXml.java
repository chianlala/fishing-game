package com.maple.game.osee.entity.fishing.game;

/**
 * 鱼类数据
 */
public class FishXml {

    /**
     * 外转盘数字
     */
    private long outTable;

    /**
     * 内转盘数字
     */
    private long inTable;

    /**
     * 内转盘数字1
     */
    private long inTable1;

    /**
     * 内转盘数字2
     */
    private long inTable2;

    /**
     * 内转盘数字3
     */
    private long inTable3;

    /**
     * 奖励
     */
    private double reword;

    /**
     * 是否有三个一样
     */
    private boolean isSan;

    /**
     * 是否有四个一样
     */
    private boolean isSi;

    public long getOutTable() {
        return outTable;
    }

    public void setOutTable(long outTable) {
        this.outTable = outTable;
    }

    public long getInTable() {
        return inTable;
    }

    public void setInTable(long inTable) {
        this.inTable = inTable;
    }

    public long getInTable1() {
        return inTable1;
    }

    public void setInTable1(long inTable1) {
        this.inTable1 = inTable1;
    }

    public long getInTable2() {
        return inTable2;
    }

    public void setInTable2(long inTable2) {
        this.inTable2 = inTable2;
    }

    public long getInTable3() {
        return inTable3;
    }

    public void setInTable3(long inTable3) {
        this.inTable3 = inTable3;
    }

    public double getReword() {
        return reword;
    }

    public void setReword(double reword) {
        this.reword = reword;
    }

    public boolean isSan() {
        return isSan;
    }

    public void setSan(boolean san) {
        isSan = san;
    }

    public boolean isSi() {
        return isSi;
    }

    public void setSi(boolean si) {
        isSi = si;
    }
}
