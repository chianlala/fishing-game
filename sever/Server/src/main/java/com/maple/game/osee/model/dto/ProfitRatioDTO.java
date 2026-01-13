package com.maple.game.osee.model.dto;

import java.io.Serializable;
import java.util.List;

import com.maple.game.osee.entity.fishing.csv.file.FishCcxxConfig;
import com.maple.game.osee.util.MyRefreshFishingUtil;

import cn.hutool.json.JSONUtil;
import lombok.Data;

@Data
public class ProfitRatioDTO implements Serializable {

    private static final long serialVersionUID = -7387894013961159324L;

    /**
     * 通过：roomIndex，获取：下标
     */
    public int getIndexByRoomIndex(int roomIndex) {

        int index = -1;

        for (int i = 0; i < getRoomIndex().length; i++) {

            if (roomIndex == getRoomIndex()[i]) {

                index = i;
                break;

            }

        }

        return index;

    }

    public ProfitRatioDTO() {

        int size = MyRefreshFishingUtil.ENABLE_FISHING_ROOM_INDEX_SET.size();

        this.roomIndex = new int[size];
        this.showSessionName = new String[size];
        this.open = new int[size];
        this.profitRatioMinArr = new int[size];
        this.profitRatioMaxArr = new int[size];
        this.resetNumArr = new int[size];
        this.frequencyArr = new int[size];
        this.tp1Arr = new Double[size];
        this.tp2Arr = new Double[size];
        this.tjmgArr = new int[size];
        this.cjmgArr = new int[size];
        this.bjmgArr = new int[size];
        this.ybmgArr = new int[size];
        this.txMinArr = new int[size];
        this.txMaxArr = new int[size];
        this.chuShiHsWeightArr = new int[size];
        this.guoChengHsWeightArr = new int[size];
        this.bfxyRandomWeightArr = new int[size];
        this.cjxzArr = new int[size];
        this.jdxzArr = new Double[size];

        this.ccksfzxxArr = new long[size];
        this.ccksfzsxArr = new long[size];

        this.ccksfzMinuteArr = new int[size];

        this.ccksfzxxWeightArr = new double[size];
        this.ccksfzsxWeightArr = new double[size];

        this.ccksfzxxBfxyArr = new String[size];
        this.ccksfzsxBfxyArr = new String[size];

        this.xsksfzfzArr = new long[size];
        this.jdsxksbfxsArr = new String[size];

        for (int i = 0; i < size; i++) {

            FishCcxxConfig fishCcxxConfig =
                MyRefreshFishingUtil.CHALLENGE_AND_INTEGRAL_FISHING_CCXX_CONFIG_LIST.get(i);

            this.roomIndex[i] = fishCcxxConfig.getSessionId();
            this.showSessionName[i] = fishCcxxConfig.getShowSessionName();
            this.open[i] = fishCcxxConfig.getOpen();

            commonSet(i);

        }

    }

    private void commonSet(int i) {

        this.profitRatioMinArr[i] = 920;
        this.profitRatioMaxArr[i] = 980;
        this.resetNumArr[i] = 1000;
        this.frequencyArr[i] = 70;
        this.tp1Arr[i] = 25d;
        this.tp2Arr[i] = 75d;
        this.tjmgArr[i] = 3500;
        this.cjmgArr[i] = 2000;
        this.bjmgArr[i] = 1000;
        this.ybmgArr[i] = 500;
        this.txMinArr[i] = 500;
        this.txMaxArr[i] = 3000;
        this.chuShiHsWeightArr[i] = 68;
        this.guoChengHsWeightArr[i] = 68;
        this.bfxyRandomWeightArr[i] = 30;
        this.cjxzArr[i] = 100;
        this.jdxzArr[i] = 0.8d;

        this.ccksfzxxArr[i] = 1000000;
        this.ccksfzsxArr[i] = 10000000;

        this.ccksfzMinuteArr[i] = 120;

        this.ccksfzxxWeightArr[i] = 0.8d;
        this.ccksfzsxWeightArr[i] = 0.8d;

        this.ccksfzxxBfxyArr[i] = "[0.5, 0.6]";
        this.ccksfzsxBfxyArr[i] = "[0.4, 0.7]";

        this.xsksfzfzArr[i] = 100;
        this.jdsxksbfxsArr[i] = "[0.55, 0.8]";

    }

    // roomIndex
    private int[] roomIndex;

    // 展示的场次名称
    private String[] showSessionName;

    // 是否启用：0 否 1是
    private int[] open;

    // 收益比范围-最小值
    private int[] profitRatioMinArr;

    // 收益比范围-最大值
    private int[] profitRatioMaxArr;

    // 子弹消耗重置阈值
    private int[] resetNumArr;

    // 变化频率
    private int[] frequencyArr;

    // t回收概率 1
    private Double[] tp1Arr;

    // t回收概率 2
    private Double[] tp2Arr;

    // 特级敏感
    private int[] tjmgArr;

    // 超级敏感
    private int[] cjmgArr;

    // 比较敏感
    private int[] bjmgArr;

    // 一般敏感
    private int[] ybmgArr;

    // tx最小值数组
    private int[] txMinArr;

    // tx最大值数组
    private int[] txMaxArr;

    // 2023-02-09 追加 ↓

    // 初始：回收权重
    private int[] chuShiHsWeightArr;

    // 过程：回收权重
    private int[] guoChengHsWeightArr;

    // bfxy，随机取值的权重
    private int[] bfxyRandomWeightArr;

    // cjxz，进场爆发上限
    private int[] cjxzArr;

    // jdxz，回馈系数
    private Double[] jdxzArr;

    // 2023-02-09 追加 ↓
    /**
     * x1场次：难度，2023-03-14：改为：大奖赛难度
     */
    private double x1Difficulty = -0.5d;

    /**
     * 体验场难度
     */
    private double demoDifficulty = -0.5d;

    /**
     * 机器人难度
     */
    private double robotDifficulty = 0.5d;

    /**
     * 盈亏下限值
     */
    private long[] ccksfzxxArr;

    /**
     * 盈亏上限值
     */
    private long[] ccksfzsxArr;

    /**
     * 触发周期：单位：分钟
     */
    private int[] ccksfzMinuteArr;

    /**
     * 盈亏爆发下限权重
     */
    private double[] ccksfzxxWeightArr;

    /**
     * 盈亏爆发上限权重
     */
    private double[] ccksfzsxWeightArr;

    /**
     * 盈亏爆发下限对应的 bfxy
     */
    private String[] ccksfzxxBfxyArr;

    /**
     * 盈亏爆发上限对应的 bfxy
     */
    private String[] ccksfzsxBfxyArr;

    public List<Double> getCcksfzxxBfxyList(int i) {
        return JSONUtil.toList(ccksfzxxBfxyArr[i], Double.class);
    }

    public List<Double> getCcksfzsxBfxyList(int i) {
        return JSONUtil.toList(ccksfzsxBfxyArr[i], Double.class);
    }

    /**
     * 新手亏损峰值阀值
     */
    private long[] xsksfzfzArr;

    /**
     * 节点上限亏损爆发系数
     */
    private String[] jdsxksbfxsArr;

    public List<Double> getJdsxksbfxsArrList(int i) {
        return JSONUtil.toList(jdsxksbfxsArr[i], Double.class);
    }

}
