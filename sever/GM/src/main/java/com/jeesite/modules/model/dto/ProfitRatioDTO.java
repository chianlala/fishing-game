package com.jeesite.modules.model.dto;

import cn.hutool.json.JSONUtil;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class ProfitRatioDTO implements Serializable {

    private static final long serialVersionUID = -7387894013961159324L;

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
