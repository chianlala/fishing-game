package com.maple.game.osee.entity.fishing.csv.file;

import java.util.List;

import com.maple.engine.data.BaseCsvData;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.json.JSONUtil;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * slot 命中系数
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class BaseFluctuationParametersConfig extends BaseCsvData {

    /**
     * bdfz集合字符串
     */
    private String bdfzListStr;

    public List<Integer> getBdfzList() {
        List<Integer> resList = JSONUtil.toList(bdfzListStr, Integer.class);
        return CollUtil.isEmpty(resList) ? CollUtil.newArrayList(5, 11) : resList;
    }

    /**
     * x集合字符串
     */
    private String xvalueListStr;

    public List<Integer> getXList() {
        List<Integer> resList = JSONUtil.toList(xvalueListStr, Integer.class);
        return CollUtil.isEmpty(resList) ? CollUtil.newArrayList(30, 70) : resList;
    }

    /**
     * 初始：x集合字符串
     */
    private String chuShiXvalueListStr;

    public List<Integer> getChuShiXList() {
        List<Integer> resList = JSONUtil.toList(chuShiXvalueListStr, Integer.class);
        return CollUtil.isEmpty(resList) ? CollUtil.newArrayList(30, 70) : resList;
    }

    // 节点上限值
    private long zdsx = 15000;

    // 2023-06-06 追加 ↓
    /**
     * 个控节点上限系数
     */
    private double gkjdsx = 0.5;

    /**
     * 分组节点的组合上限
     */
    private int groupMax = 5;

    /**
     * 谷底差值范围
     */
    private String gdListStr;

    public List<Integer> getGdList() {
        List<Integer> resList = JSONUtil.toList(gdListStr, Integer.class);
        return CollUtil.isEmpty(resList) ? CollUtil.newArrayList(300, 400) : resList;
    }

    /**
     * gdCzxs
     */
    private String gdCzxsListStr;

    public List<Double> getGdCzxsList() {
        List<Double> resList = JSONUtil.toList(gdCzxsListStr, Double.class);
        return CollUtil.isEmpty(resList) ? CollUtil.newArrayList(0.3, 1d) : resList;
    }

    /**
     * czCzxs：谷底差值亏损爆发比例
     */
    private String czCzxsListStr;

    public List<Double> getCzCzxsList() {
        List<Double> resList = JSONUtil.toList(czCzxsListStr, Double.class);
        return CollUtil.isEmpty(resList) ? CollUtil.newArrayList(0.3, 0.5) : resList;
    }

    /**
     * aq开关：0 关 1 开
     */
    private int aqFlag = 1;

    /**
     * lxbf1
     */
    private double lxbf1 = 3; // 0,200

    private double lxbf2 = 2.9; // 200,600

    private double lxbf3 = 2.8; // 600,1200

    private double lxbf4 = 2.7; // 1200,2000

    private double lxbf5 = 2.6; // 2000,3300

    private double lxbf6 = 2.5; // 3300,4600

    private double lxbf7 = 1.8; // 4600,7000

    private double lxbf8 = 1.6; // 7000,10000

    private double lxbf9 = 1.2; // 10000,20000

}
