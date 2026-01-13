package com.jeesite.modules.osee.vo.game;

import com.jeesite.modules.model.bo.BdzConfigBO;
import com.jeesite.modules.model.dto.ProfitRatioDTO;
import lombok.Data;

import java.io.Serializable;

/**
 * 机器人数据传输实体类
 *
 * @author zjl
 */
@Data
public class RobotVO implements Serializable {

    private static final long serialVersionUID = 8240031882361138946L;

    private FishingRobot fishing;


    private FishingGrandPrixRobot fishingGrandPrixRobot;

    private FighttenRobot fightten;

    private FighttenRobot1 fightten1;

    private FighttenRobot2 fightten2;

    private ErbaRobot erba;

    private Long tenRobotTotalWinMoney;

    private Long tenRobotTotalLoseMoney;

    private Long tenRobotTotalWinMoney1;

    private Long tenRobotTotalLoseMoney1;

    private Long tenRobotTotalWinMoney2;

    private Long tenRobotTotalLoseMoney2;

    private FishingTotal fishingTotal;

    private BloodPoolControl bloodPoolControl;

    private ProfitRatioDTO profitRatioDTO;

    private BdzConfigBO bdzConfigBO;

    // ***************************************************************

    /**
     * 血池控制
     */
    @Data
    public static class BloodPoolControl {

        private int[] fishControlArr = new int[]{1, 1, 1, 1}; // 鱼类型控制选择：1 血量 2 概率
        private long[] dt1 = new long[]{100000000, 100000000, 100000000, 100000000, 100000000};
        private long[] dt2 = new long[]{200000000, 200000000, 200000000, 200000000, 200000000};
        private long[] dt3 = new long[]{300000000, 300000000, 300000000, 300000000, 300000000};
        private long[] dt4 = new long[]{400000000, 400000000, 400000000, 400000000, 400000000};
        private int bfType = 4; // 选择的：爆发类型
        private int hsType = 4; // 选择的：回收类型
        private Double[] bfMinArr; // 爆发：下限数组
        private Double[] bfMaxArr; // 爆发：上限数组
        private Double[] hsMinArr; // 回收：下限数组
        private Double[] hsMaxArr; // 回收：上限数组
    }

    /**
     * 捕鱼机器人配置
     */
    public static class FishingRobot {

        private Integer useRobot;       // 是否启用机器人
        private Integer robotCount;     // 机器人数量
        private Integer minRefresh;     // 最小刷新阈值
        private Integer maxRefresh;     // 最大刷新阈值
        private Integer minDisappear;   // 最小消失阈值
        private Integer maxDisappear;   // 最大消失阈值

        public Integer getUseRobot() {
            return useRobot;
        }

        public void setUseRobot(Integer useRobot) {
            this.useRobot = useRobot;
        }

        public Integer getRobotCount() {
            return robotCount;
        }

        public void setRobotCount(Integer robotCount) {
            this.robotCount = robotCount;
        }

        public Integer getMinRefresh() {
            return minRefresh;
        }

        public void setMinRefresh(Integer minRefresh) {
            this.minRefresh = minRefresh;
        }

        public Integer getMaxRefresh() {
            return maxRefresh;
        }

        public void setMaxRefresh(Integer maxRefresh) {
            this.maxRefresh = maxRefresh;
        }

        public Integer getMinDisappear() {
            return minDisappear;
        }

        public void setMinDisappear(Integer minDisappear) {
            this.minDisappear = minDisappear;
        }

        public Integer getMaxDisappear() {
            return maxDisappear;
        }

        public void setMaxDisappear(Integer maxDisappear) {
            this.maxDisappear = maxDisappear;
        }
    }

    /**
     * 捕鱼机器人配置
     */
    @Data
    public static class FishingTotal {

        private Integer isOpen;     // 是否开启服务器
        private Integer kzlx;     // 库存控制类型
        private String createTime;
        private Long totalMoney;       // 龙晶场库存QXH
        private Long qxz;       // 龙晶场QXZ

        private Long totalMoney1;       // 龙晶场库存QXH
        private Long qxz1;       // 龙晶场QXZ
        private Long totalMoney2;       // 龙晶场库存QXH
        private Long qxz2;       // 龙晶场QXZ
        private Long totalMoney3;       // 龙晶场库存QXH
        private Long qxz3;       // 龙晶场QXZ
        private Long totalMoney4;       // 龙晶场库存QXH
        private Long qxz4;       // 龙晶场QXZ
        private Long totalMoney5;       // 龙晶场库存QXH
        private Long qxz5;       // 龙晶场QXZ

        private Double m;
        private Long jbs;
        private Double jbsTj;
        private Long lbs;
        private Double lbsTj;
        private Long jhs;
        private Double jhsTj;
        private Long lhs;
        private Double lhsTj;

        private Double zblx = 0D;
        private Double zbls = 0D;
        private Double tg0 = 0D;
        private Double tg1 = 0D;
        private Double tg5 = 0D;
        private Double tg10 = 0D;
        private Double tg11 = 0D;
        private Double mgjs = 0D;
        private Integer phx1 = 0;
        private Integer phx2 = 0;
        private Double gls1 = 0D;
        private Double glx1 = 0D;
        private Integer cp1a = 0;
        private Integer cp1b = 0;
        private Double gls2 = 0D;
        private Double glx2 = 0D;
        private Integer cp2a = 0;
        private Integer cp2b = 0;

        private BloodPoolControl bloodPoolControl; // 备注：不要使用，这是为了值传递而使用的
    }

    /**
     * 捕鱼机器人配置
     */
    @Data
    public static class FishingGrandPrixRobot {

        private Integer useRobot;       // 是否启用机器人
        private Integer robotCount;     // 机器人数量
        private Integer minRefresh;     // 最小刷新阈值
        private Integer maxRefresh;     // 最大刷新阈值
        private Integer minDisappear;   // 最小消失阈值
        private Integer maxDisappear;   // 最大消失阈值
        private Long bm;   // bm 入场费用
        private Long cxfz;   // cx奖励阀值
        private Long cxjl;   // cx奖励系数
        // 开放时间
        private String startTime;
        private String endTime;
    }

    /**
     * 拼十机器人设置
     */
    public static class FighttenRobot {

        private Integer useRobot;               // 0:关闭 1:启用
        private Integer robotNum;               // 单个房间机器人数量
        private Integer refreshTimeRangeBegin;  // 机器人刷新时间间隔起始
        private Integer refreshTimeRangeEnd;    // 机器人刷新时间间隔结尾
        private Integer winPercent;             // 机器人获胜金币占比(%)
        private int ctype;

        public Integer getUseRobot() {
            return useRobot;
        }

        public void setUseRobot(Integer useRobot) {
            this.useRobot = useRobot;
        }

        public Integer getRobotNum() {
            return robotNum;
        }

        public void setRobotNum(Integer robotNum) {
            this.robotNum = robotNum;
        }

        public Integer getRefreshTimeRangeBegin() {
            return refreshTimeRangeBegin;
        }

        public void setRefreshTimeRangeBegin(Integer refreshTimeRangeBegin) {
            this.refreshTimeRangeBegin = refreshTimeRangeBegin;
        }

        public Integer getRefreshTimeRangeEnd() {
            return refreshTimeRangeEnd;
        }

        public void setRefreshTimeRangeEnd(Integer refreshTimeRangeEnd) {
            this.refreshTimeRangeEnd = refreshTimeRangeEnd;
        }

        public Integer getWinPercent() {
            return winPercent;
        }

        public void setWinPercent(Integer winPercent) {
            this.winPercent = winPercent;
        }

        public int getCtype() {
            return ctype;
        }

        public void setCtype(int ctype) {
            this.ctype = ctype;
        }
    }

    /**
     * 拼十机器人设置
     */
    public static class FighttenRobot1 {

        private Integer useRobot1;               // 0:关闭 1:启用
        private Integer robotNum1;               // 单个房间机器人数量
        private Integer refreshTimeRangeBegin1;  // 机器人刷新时间间隔起始
        private Integer refreshTimeRangeEnd1;    // 机器人刷新时间间隔结尾
        private Integer winPercent1;             // 机器人获胜金币占比(%)
        private int ctype;

        public Integer getUseRobot1() {
            return useRobot1;
        }

        public void setUseRobot1(Integer useRobot1) {
            this.useRobot1 = useRobot1;
        }

        public Integer getRobotNum1() {
            return robotNum1;
        }

        public void setRobotNum1(Integer robotNum1) {
            this.robotNum1 = robotNum1;
        }

        public Integer getRefreshTimeRangeBegin1() {
            return refreshTimeRangeBegin1;
        }

        public void setRefreshTimeRangeBegin1(Integer refreshTimeRangeBegin1) {
            this.refreshTimeRangeBegin1 = refreshTimeRangeBegin1;
        }

        public Integer getRefreshTimeRangeEnd1() {
            return refreshTimeRangeEnd1;
        }

        public void setRefreshTimeRangeEnd1(Integer refreshTimeRangeEnd1) {
            this.refreshTimeRangeEnd1 = refreshTimeRangeEnd1;
        }

        public Integer getWinPercent1() {
            return winPercent1;
        }

        public void setWinPercent1(Integer winPercent1) {
            this.winPercent1 = winPercent1;
        }

        public int getCtype() {
            return ctype;
        }

        public void setCtype(int ctype) {
            this.ctype = ctype;
        }
    }

    /**
     * 拼十机器人设置
     */
    public static class FighttenRobot2 {

        private Integer useRobot2;               // 0:关闭 1:启用
        private Integer robotNum2;               // 单个房间机器人数量
        private Integer refreshTimeRangeBegin2;  // 机器人刷新时间间隔起始
        private Integer refreshTimeRangeEnd2;    // 机器人刷新时间间隔结尾
        private Integer winPercent2;             // 机器人获胜金币占比(%)
        private int ctype;

        public Integer getUseRobot2() {
            return useRobot2;
        }

        public void setUseRobot2(Integer useRobot2) {
            this.useRobot2 = useRobot2;
        }

        public Integer getRobotNum2() {
            return robotNum2;
        }

        public void setRobotNum2(Integer robotNum2) {
            this.robotNum2 = robotNum2;
        }

        public Integer getRefreshTimeRangeBegin2() {
            return refreshTimeRangeBegin2;
        }

        public void setRefreshTimeRangeBegin2(Integer refreshTimeRangeBegin2) {
            this.refreshTimeRangeBegin2 = refreshTimeRangeBegin2;
        }

        public Integer getRefreshTimeRangeEnd2() {
            return refreshTimeRangeEnd2;
        }

        public void setRefreshTimeRangeEnd2(Integer refreshTimeRangeEnd2) {
            this.refreshTimeRangeEnd2 = refreshTimeRangeEnd2;
        }

        public Integer getWinPercent2() {
            return winPercent2;
        }

        public void setWinPercent2(Integer winPercent2) {
            this.winPercent2 = winPercent2;
        }

        public int getCtype() {
            return ctype;
        }

        public void setCtype(int ctype) {
            this.ctype = ctype;
        }
    }

    /**
     * 二八杠机器人设置
     */
    public static class ErbaRobot {

        private Integer useRobot;   // 0:关闭 1:启用

        private Long TwoEightRobotCurrentWinMoney; // 当前盈利金额

        private Long TwoEightRobotHistoryWinMoney; // 历史总盈利金额

        private Long RobotMinMoney;

        private Double toWinFirstCardProbably;
        private Double toWinSecondCardProbably;
        private Double toWinThirdCardProbably;
        private Double toWinLastCardProbably;

        private Double toLoseFirstCardProbably;
        private Double toLoseSecondCardProbably;
        private Double toLoseThirdCardProbably;
        private Double toLoseLastCardProbably;

        public boolean checkWinRate() {
            double sum =
                    toWinFirstCardProbably + toWinSecondCardProbably + toWinThirdCardProbably
                            + toWinLastCardProbably;
            return sum >= 99.999 && sum <= 100.001;
        }

        public boolean checkLoseRate() {
            double sum =
                    toLoseFirstCardProbably + toLoseSecondCardProbably + toLoseThirdCardProbably
                            + toLoseLastCardProbably;
            return sum >= 99.999 && sum <= 100.001;
        }

        public Integer getUseRobot() {
            return useRobot;
        }

        public void setUseRobot(Integer useRobot) {
            this.useRobot = useRobot;
        }

        public Long getTwoEightRobotCurrentWinMoney() {
            return TwoEightRobotCurrentWinMoney;
        }

        public void setTwoEightRobotCurrentWinMoney(Long twoEightRobotCurrentWinMoney) {
            TwoEightRobotCurrentWinMoney = twoEightRobotCurrentWinMoney;
        }

        public Long getTwoEightRobotHistoryWinMoney() {
            return TwoEightRobotHistoryWinMoney;
        }

        public void setTwoEightRobotHistoryWinMoney(Long twoEightRobotHistoryWinMoney) {
            TwoEightRobotHistoryWinMoney = twoEightRobotHistoryWinMoney;
        }

        public Long getRobotMinMoney() {
            return RobotMinMoney;
        }

        public void setRobotMinMoney(Long robotMinMoney) {
            RobotMinMoney = robotMinMoney;
        }

        public Double getToWinFirstCardProbably() {
            return toWinFirstCardProbably;
        }

        public void setToWinFirstCardProbably(Double toWinFirstCardProbably) {
            this.toWinFirstCardProbably = toWinFirstCardProbably;
        }

        public Double getToWinSecondCardProbably() {
            return toWinSecondCardProbably;
        }

        public void setToWinSecondCardProbably(Double toWinSecondCardProbably) {
            this.toWinSecondCardProbably = toWinSecondCardProbably;
        }

        public Double getToWinThirdCardProbably() {
            return toWinThirdCardProbably;
        }

        public void setToWinThirdCardProbably(Double toWinThirdCardProbably) {
            this.toWinThirdCardProbably = toWinThirdCardProbably;
        }

        public Double getToWinLastCardProbably() {
            return toWinLastCardProbably;
        }

        public void setToWinLastCardProbably(Double toWinLastCardProbably) {
            this.toWinLastCardProbably = toWinLastCardProbably;
        }

        public Double getToLoseFirstCardProbably() {
            return toLoseFirstCardProbably;
        }

        public void setToLoseFirstCardProbably(Double toLoseFirstCardProbably) {
            this.toLoseFirstCardProbably = toLoseFirstCardProbably;
        }

        public Double getToLoseSecondCardProbably() {
            return toLoseSecondCardProbably;
        }

        public void setToLoseSecondCardProbably(Double toLoseSecondCardProbably) {
            this.toLoseSecondCardProbably = toLoseSecondCardProbably;
        }

        public Double getToLoseThirdCardProbably() {
            return toLoseThirdCardProbably;
        }

        public void setToLoseThirdCardProbably(Double toLoseThirdCardProbably) {
            this.toLoseThirdCardProbably = toLoseThirdCardProbably;
        }

        public Double getToLoseLastCardProbably() {
            return toLoseLastCardProbably;
        }

        public void setToLoseLastCardProbably(Double toLoseLastCardProbably) {
            this.toLoseLastCardProbably = toLoseLastCardProbably;
        }
    }
}
