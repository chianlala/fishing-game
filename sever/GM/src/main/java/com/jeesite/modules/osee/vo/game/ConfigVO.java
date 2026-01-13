package com.jeesite.modules.osee.vo.game;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 设置数据传输实体
 *
 * @author zjl
 */
@Data
public class ConfigVO implements Serializable {

    private static final long serialVersionUID = -3645010375751785901L;

    private Long[] torpedoDropFreeRate;    // 免费玩家掉落几率
    private Long[] torpedoDropRateGoldFreeRate;    // 免费玩家掉落几率
    private Long[] bangTorpedoDropFreeRate;    // 免费玩家掉落几率
    private Long[] bangTorpedoDropRateGoldFreeRate;    // 免费玩家掉落几率
    private Long[] bossBugleRate;    // 免费玩家掉落几率
    private Long[] torpedoDropPerPayMoney;    // 付费玩家每充值金钱数
    private Long[] torpedoDropPerPayRate;    // 付费玩家每充值提升几率
    private Long[] torpedoDropPerExChangeRate;    // 鱼类兑换比例
    private Long[] torpedoDropPerExChangeRateMin;    // 鱼类兑换比例
    private Long[] torpedoNotBangDropPerExChangeRateMin;    // 鱼类兑换比例
    private Long[] rareTorpedoDropPerExChangeRate;    // 鱼类兑换比例
    private Long[] rareTorpedoDropPerExChangeRateMin;    // 鱼类兑换比例
    private Long[] rareTorpedoNotBangDropPerExChangeRate;    // 鱼类兑换比例
    private Long[] rareTorpedoNotBangDropPerExChangeRateMin;    // 鱼类兑换比例
    private Long[] torpedoNotBangDropPerExChangeRate;    // 非绑鱼类掉落比例


    private Double[] jcPercentage;    //奖池系数设置

    private Double[] gProd;    //触底爆发触发概率
    private Double[] lcf;    //触底阀值
    private Double[] dz;    //爆发最低值
    private Double[] bfsMin;    //爆发随机数小值
    private Double[] bfsMax;    //爆发随机数大值

    private Double[] tp_c;    //tp1概率
    private Double[] tp_z;    //tp2概率
    private Double[] tp_g;    //tp3概率
    private Double[] tp_s;    //tp4概率

    private Double k;    //k设置
    private Double sxxs;    //sxxs设置
    private Double xs;    //xs设置
    private Double sxhdz;    //sxhdz设置
    private Double csc;    //csc设置
    private Double ckc;    //ckc设置
    private Double zsc;    //zsc设置
    private Double zkc;    //zkc设置
    private Double csc1;    //csc1设置
    private Double ckc1;    //ckc1设置
    private Double zsc1;    //zsc1设置
    private Double zkc1;    //zkc1设置

    private Double[] pdx;    //t设置
    private Double[] pdxp;    //t设置
    private Double[] xdx;    //t设置
    private Double[] xdxp;    //t设置


    private Double[] cxPercentage;    //t设置

    private Double[] peakMax1;    //t设置

    private Double[] peakMin1;    //t设置

    private Double[] peakMaxNum1;    //t设置

    private Double[] peakMinNum1;    //t设置

    private Double[] peakMax2;    //t设置

    private Double[] peakMin2;    //t设置

    private Double[] peakMaxNum2;    //t设置

    private Double[] peakMinNum2;    //t设置

    private Double[] peakMax3;    //t设置

    private Double[] peakMin3;    //t设置

    private Double[] peakMaxNum3;    //t设置

    private Double[] peakMinNum3;    //t设置

    private Double[] PXMin;    //t设置

    private Double[] FPMin;    //t设置

    private Double[] PXMax;    //t设置

    private Double[] FPMax;    //t设置

    private List<FishingConfig> fishing;      // 捕鱼初中高设置

    private List<FighttenConfig> fightten;    // 拼十初中高设置

    private Double activeRate; // 推广活动奖励比例

    private Double tenDrawPercent; // 拼十抽水百分点
    private Double erbaDrawPercent; // 二八杠抽水百分点
    private Double labaDrawPercent1; // 水果拉霸抽水百分点
    private Double labaDrawPercent2; // 水果拉霸抽水百分点
    private Double labaDrawPercent3; // 水果拉霸抽水百分点
    private Long enterMoneyLimit1; // 水果拉霸入场金币要求
    private Long enterMoneyLimit2; // 水果拉霸入场金币要求
    private Long enterMoneyLimit3; // 水果拉霸入场金币要求
    private Double[] lineRate; // 水果拉霸 线的数量的概率
    private Double[] multipleLevel; // 水果拉霸 倍率等级的概率
    private Long[] linePoolGold; // 水果拉霸 每条线的库存 1-9

    private Double percent1;     // 水果拉霸 奖池比例
    private Long fruitLabaBlackRoomEP1;  // 水果拉霸 小黑屋金币 EP
    private Integer fruitLabaBlackRoomTP1;  // 水果拉霸 小黑屋限制倍率 TP
    private Integer fruitLabaBlackRoomBP1;  // 水果拉霸 小黑屋限制中奖线数 BP
    private Long fruitLabaStockK01;  // 水果拉霸 阀值 K0
    private Long fruitLabaStockK11;  // 水果拉霸 阀值 K1
    private Long fruitLabaStockK21;  // 水果拉霸 阀值 K2
    private Long fruitLabaStockK31;  // 水果拉霸 阀值 K3
    private Long fruitLabaStockK41;  // 水果拉霸 阀值 K4
    private Long fruitLabaPrizeDXJ1; // 水果拉霸 当天中奖上限 DXJ
    private Long fruitLabaPrizeLXJ1; // 水果拉霸 历史中奖上限 LXJ
    private Long fruitLabaPrizeFLXJ1; // 水果拉霸 当天中奖下限 FLXJ
    private Long fruitLabaPrizeFDXJ1; // 水果拉霸 历史中奖下限 FDXJ
    private Integer fruitLabaPrizeXS1; // 水果拉霸 中奖线控制 XS
    private Integer fruitLabaPrizeFXS1; // 水果拉霸 中奖线控制 XS
    private Integer fruitLabaPrizeDX1; // 水果拉霸 幸运参数 DX
    private Integer fruitLabaPrizeLX1; // 水果拉霸 幸运参数 LX
    private Integer fruitLabaPrizeLW1; // 水果拉霸 幸运参数 LW
    private Integer fruitLabaPrizeDW1; // 水果拉霸 幸运参数 DW

    private Double percent2;     // 水果拉霸 奖池比例
    private Long fruitLabaBlackRoomEP2;  // 水果拉霸 小黑屋金币 EP
    private Integer fruitLabaBlackRoomTP2;  // 水果拉霸 小黑屋限制倍率 TP
    private Integer fruitLabaBlackRoomBP2;  // 水果拉霸 小黑屋限制中奖线数 BP
    private Long fruitLabaStockK02;  // 水果拉霸 阀值 K0
    private Long fruitLabaStockK12;  // 水果拉霸 阀值 K1
    private Long fruitLabaStockK22;  // 水果拉霸 阀值 K2
    private Long fruitLabaStockK32;  // 水果拉霸 阀值 K3
    private Long fruitLabaStockK42;  // 水果拉霸 阀值 K4
    private Long fruitLabaPrizeDXJ2; // 水果拉霸 当天中奖上限 DXJ
    private Long fruitLabaPrizeLXJ2; // 水果拉霸 历史中奖上限 LXJ
    private Long fruitLabaPrizeFLXJ2; // 水果拉霸 当天中奖下限 FLXJ
    private Long fruitLabaPrizeFDXJ2; // 水果拉霸 历史中奖下限 FDXJ
    private Integer fruitLabaPrizeXS2; // 水果拉霸 中奖线控制 XS
    private Integer fruitLabaPrizeFXS2; // 水果拉霸 中奖线控制 XS
    private Integer fruitLabaPrizeDX2; // 水果拉霸 幸运参数 DX
    private Integer fruitLabaPrizeLX2; // 水果拉霸 幸运参数 LX
    private Integer fruitLabaPrizeLW2; // 水果拉霸 幸运参数 LW
    private Integer fruitLabaPrizeDW2; // 水果拉霸 幸运参数 DW

    private Double percent3;     // 水果拉霸 奖池比例
    private Long fruitLabaBlackRoomEP3;  // 水果拉霸 小黑屋金币 EP
    private Integer fruitLabaBlackRoomTP3;  // 水果拉霸 小黑屋限制倍率 TP
    private Integer fruitLabaBlackRoomBP3;  // 水果拉霸 小黑屋限制中奖线数 BP
    private Long fruitLabaStockK03;  // 水果拉霸 阀值 K0
    private Long fruitLabaStockK13;  // 水果拉霸 阀值 K1
    private Long fruitLabaStockK23;  // 水果拉霸 阀值 K2
    private Long fruitLabaStockK33;  // 水果拉霸 阀值 K3
    private Long fruitLabaStockK43;  // 水果拉霸 阀值 K4
    private Long fruitLabaPrizeDXJ3; // 水果拉霸 当天中奖上限 DXJ
    private Long fruitLabaPrizeLXJ3; // 水果拉霸 历史中奖上限 LXJ
    private Long fruitLabaPrizeFLXJ3; // 水果拉霸 当天中奖下限 FLXJ
    private Long fruitLabaPrizeFDXJ3; // 水果拉霸 历史中奖下限 FDXJ
    private Integer fruitLabaPrizeXS3; // 水果拉霸 中奖线控制 XS
    private Integer fruitLabaPrizeFXS3; // 水果拉霸 中奖线控制 XS
    private Integer fruitLabaPrizeDX3; // 水果拉霸 幸运参数 DX
    private Integer fruitLabaPrizeLX3; // 水果拉霸 幸运参数 LX
    private Integer fruitLabaPrizeLW3; // 水果拉霸 幸运参数 LW
    private Integer fruitLabaPrizeDW3; // 水果拉霸 幸运参数 DW

    private Long fishingGrandPrixInitPool;
    private Double fishingGrandPrixAP;
    private Long fishingGrandPrixAPT;
    private Double fishingGrandPrixBP;
    private Long fishingGrandPrixQZ;
    private Double fishingGrandPrixPQ;
    private Long fishingGrandPrixQY;
    private Double fishingGrandPrixPA;
    private Long fishingGrandPrixQS;
    private Double fishingGrandPrixPW;
    private Long fishingGrandPrixQX;
    private Double fishingGrandPrixPY;
    private Long fishingGrandPrixQW;
    private Double fishingGrandPrixTP;

    private String[] isUsed;
    private String wxS;
    private String wxH5;
    private String zfbH5;
    private String zfbS;

    public Long[] burstOne;
    public Long[] recOne;

    public Long[] balanceOne;
    public Long[] balanceBuOne;

    public Long[] gcOne;
    public Long[] gcBurstOne;

    public Long[] recTwo;
    public Long[] burstTwo;

    public Long[] balanceTwo;
    public Long[] balanceBuTwo;

    public Long[] gcTwo;
    public Long[] gcBurstTwo;

    public Long[] recThree;
    public Long[] burstThree;

    public Long[] balanceThree;
    public Long[] balanceBuThree;

    public Long[] gcThree;
    public Long[] gcBurstThree;

    public Long[] recFour;
    public Long[] burstFour;

    public Long[] balanceFour;
    public Long[] balanceBuFour;

    public Long[] gcFour;
    public Long[] gcBurstFour;

    public Long[] burstOne1;
    public Long[] recOne1;

    public Long[] burstTwo1;
    public Long[] recTwo1;

    public Long[] balanceOne1;
    public Long[] balanceTwo1;

    public Long[] burstOne2;
    public Long[] recOne2;

    public Long[] burstTwo2;
    public Long[] recTwo2;

    public Long[] balanceOne2;
    public Long[] balanceTwo2;

    public Long[] burstOne3;
    public Long[] recOne3;

    public Long[] burstTwo3;
    public Long[] recTwo3;

    public Long[] balanceOne3;
    public Long[] balanceTwo3;

    public Long[] burstOne4;
    public Long[] recOne4;

    public Long[] burstTwo4;
    public Long[] recTwo4;

    public Long[] balanceOne4;
    public Long[] balanceTwo4;

    public Long[] q0;

    public Double[] ap;

    public long[] apt;

    public Long[] recMax;

    public Long[] recMin;


    public Double[] ap1;

    public long[] apt1;

    public Long[] recMax1;

    public Long[] recMin1;


    public Double[] ap2;

    public long[] apt2;

    public Long[] recMax2;

    public Long[] recMin2;


    public Double[] ap3;

    public long[] apt3;

    public Long[] recMax3;

    public Long[] recMin3;

    public Long[] pumpNum;

    public Long[] pump;


    public Boolean checkLineRate() {
        if (lineRate == null) {
            return true;
        }
        double sum = 0;
        for (int i = 0; i <= 9; i++) {
            sum += lineRate[i];
        }
        return sum > 99.99 && sum < 100.001;
    }

    public Boolean checkMultipleLevel() {
        if (multipleLevel == null) {
            return true;
        }
        double sum = 0;
        for (int i = 1; i <= 6; i++) {
            sum += multipleLevel[i];
        }
        return sum > 99.99 && sum < 100.001;
    }

    // **************************************************************************

    /**
     * 捕鱼配置
     */
    public static class FishingConfig {

        private Double fishingProb;     // 捕鱼系数 (ap)
        private Long unitMoney;         // 单位变化捕鱼系数 (apt)
        private Double blackRoomProb;   // 小黑屋参数 (bp)
        private Long blackRoomLimit;    // 小黑屋金币 (qz)
        private Double totalWinProb;    // 总计赢上限系数 (pq)
        private Long totalWinLimit;     // 总计赢上限金币 (qy)
        private Double totalLoseProb;   // 总计输下限系数 (pa)
        private Long totalLoseLimit;    // 总计输下限金币 (qs)
        private Double dailyWinProb;    // 幸运系数 (pw)
        private Long dailyWinLimit;     // 当天赢上限金币 (qx)
        private Double dailyLoseProb;   // 挽救系数 (py)
        private Long dailyLoseLimit;    // 当天输下限金币 (qw)
        private Double cutProb;         // 抽水参数 (tp)
        private Long initPoolMoney;     // 初始库存金币 (预留金币)

        public Double getFishingProb() {
            return fishingProb;
        }

        public void setFishingProb(Double fishingProb) {
            this.fishingProb = fishingProb;
        }

        public Long getUnitMoney() {
            return unitMoney;
        }

        public void setUnitMoney(Long unitMoney) {
            this.unitMoney = unitMoney;
        }

        public Double getBlackRoomProb() {
            return blackRoomProb;
        }

        public void setBlackRoomProb(Double blackRoomProb) {
            this.blackRoomProb = blackRoomProb;
        }

        public Long getBlackRoomLimit() {
            return blackRoomLimit;
        }

        public void setBlackRoomLimit(Long blackRoomLimit) {
            this.blackRoomLimit = blackRoomLimit;
        }

        public Double getTotalWinProb() {
            return totalWinProb;
        }

        public void setTotalWinProb(Double totalWinProb) {
            this.totalWinProb = totalWinProb;
        }

        public Long getTotalWinLimit() {
            return totalWinLimit;
        }

        public void setTotalWinLimit(Long totalWinLimit) {
            this.totalWinLimit = totalWinLimit;
        }

        public Double getTotalLoseProb() {
            return totalLoseProb;
        }

        public void setTotalLoseProb(Double totalLoseProb) {
            this.totalLoseProb = totalLoseProb;
        }

        public Long getTotalLoseLimit() {
            return totalLoseLimit;
        }

        public void setTotalLoseLimit(Long totalLoseLimit) {
            this.totalLoseLimit = totalLoseLimit;
        }

        public Double getDailyWinProb() {
            return dailyWinProb;
        }

        public void setDailyWinProb(Double dailyWinProb) {
            this.dailyWinProb = dailyWinProb;
        }

        public Long getDailyWinLimit() {
            return dailyWinLimit;
        }

        public void setDailyWinLimit(Long dailyWinLimit) {
            this.dailyWinLimit = dailyWinLimit;
        }

        public Double getDailyLoseProb() {
            return dailyLoseProb;
        }

        public void setDailyLoseProb(Double dailyLoseProb) {
            this.dailyLoseProb = dailyLoseProb;
        }

        public Long getDailyLoseLimit() {
            return dailyLoseLimit;
        }

        public void setDailyLoseLimit(Long dailyLoseLimit) {
            this.dailyLoseLimit = dailyLoseLimit;
        }

        public Double getCutProb() {
            return cutProb;
        }

        public void setCutProb(Double cutProb) {
            this.cutProb = cutProb;
        }

        public Long getInitPoolMoney() {
            return initPoolMoney;
        }

        public void setInitPoolMoney(Long initPoolMoney) {
            this.initPoolMoney = initPoolMoney;
        }
    }

    /**
     * 拼十配置
     */
    public static class FighttenConfig {

        private Integer type;       // 场次级别序号：0-初,1-中,2-高
        private Long maxBetMoney; // 房间最大下注数

        public Integer getType() {
            return type;
        }

        public void setType(Integer type) {
            this.type = type;
        }

        public Long getMaxBetMoney() {
            return maxBetMoney;
        }

        public void setMaxBetMoney(Long maxBetMoney) {
            this.maxBetMoney = maxBetMoney;
        }
    }
}
