package com.jeesite.modules.model.bo;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.json.JSONUtil;
import lombok.Data;

import java.util.List;

@Data
public class BdzConfigBO {

    private double y5 = 4;

    /**
     * 上限盈亏类型：1 场次历史盈亏 2 场次今日盈亏
     */
    private int maxLimitYkType = 1;

//    /**
//     * bdfz集合字符串
//     */
//    private String bdfzListStr = JSONUtil.toJsonStr(CollUtil.newArrayList(5, 11));
//
//    public List<Integer> getBdfzList() {
//        return JSONUtil.toList(bdfzListStr, Integer.class);
//    }
//
//    /**
//     * 过程：x集合字符串
//     */
//    private String xvalueListStr = JSONUtil.toJsonStr(CollUtil.newArrayList(30, 70));
//
//    public List<Integer> getXList() {
//        return JSONUtil.toList(xvalueListStr, Integer.class);
//    }
//
//    /**
//     * 初始：x集合字符串
//     */
//    private String chuShiXvalueListStr = JSONUtil.toJsonStr(CollUtil.newArrayList(30, 70));
//
//    public List<Integer> getChuShiXList() {
//        return JSONUtil.toList(chuShiXvalueListStr, Integer.class);
//    }
//
//    private double y11 = -0.65;
//    private double y12 = -0.65;
//    private double y13 = -0.65;
//    private double y14 = -0.7;
//    private double y15 = -0.8;
//
//    private double y2 = 1;
//    private double y3 = 2;
//    private double y4 = 4;
//    private double y5 = 4;
//    private double y8 = 4;
//    private double y9 = 4;
//
//    // 2023-04-10：追加 ↓
//    private double cy11 = -0.65;
//    private double cy12 = -0.65;
//    private double cy13 = -0.65;
//    private double cy14 = -0.7;
//    private double cy15 = -0.8;
//
//    private double cy2 = 1;
//    private double cy3 = 2;
//    private double cy4 = 4;
//    private double cy5 = 4;
//    private double cy8 = 4;
//    private double cy9 = 4;
//
//    // 2023-04-10 追加 ↓
//    // 节点上限值
//    private long zdsx = 15000;
//
//    // 2023-06-06 追加 ↓
//    // slot爆发爆发范围集合字符串
//    private String bdXbListStr = JSONUtil.toJsonStr(CollUtil.newArrayList(1.5, 10));
//
//    public List<Double> getBdXbList() {
//        return JSONUtil.toList(bdXbListStr, Double.class);
//    }
//
//    // slot爆发回收范围集合字符串
//    private String bdXhListStr = JSONUtil.toJsonStr(CollUtil.newArrayList(0.3, 0.6));
//
//    public List<Double> getBdXhList() {
//        return JSONUtil.toList(bdXhListStr, Double.class);
//    }
//
//    /**
//     * 爆发：命中系数
//     */
//    private double bfMzXs = 0.2;
//
//    /**
//     * 回收权重
//     */
//    private int bfHsQz = 6;
//
//    /**
//     * 爆发权重
//     */
//    private int bfBfQz = 4;
//
//    /**
//     * 回收：命中系数
//     */
//    private double hsMzXs = 0.2;
//
//    /**
//     * 回收权重
//     */
//    private int hsHsQz = 2;
//
//    /**
//     * 爆发权重
//     */
//    private int hsBfQz = 1;
//
//    /**
//     * 个控节点上限系数
//     */
//    private double gkjdsx = 0.5;
//
//    /**
//     * 分组节点的组合上限
//     */
//    private int groupMax = 5;
//
//    /**
//     * aq1的 bfqsjd乘积范围
//     */
//    private String pctdListStr = JSONUtil.toJsonStr(CollUtil.newArrayList(0.2d, 0.6d));
//
//    public List<Double> getPctdList() {
//        return JSONUtil.toList(pctdListStr, Double.class);
//    }
//
//    /**
//     * 机器人积分加成范围
//     */
//    private String robotIntegralMultListStr = JSONUtil.toJsonStr(CollUtil.newArrayList(1d, 2d));
//
//    public List<Double> getRobotIntegralMultList() {
//        return JSONUtil.toList(robotIntegralMultListStr, Double.class);
//    }
//
//    /**
//     * 上限盈亏类型：1 场次历史盈亏 2 场次今日盈亏
//     */
//    private int maxLimitYkType = 1;
//
//    /**
//     * 谷底差值范围
//     */
//    private String gdListStr = JSONUtil.toJsonStr(CollUtil.newArrayList(300, 400));
//
//    public List<Integer> getGdList() {
//        return JSONUtil.toList(gdListStr, Integer.class);
//    }
//
//    /**
//     * gdCzxs
//     */
//    private double gdCzxs = 0.5;
//
//    /**
//     * gdCzxs
//     */
//    private String gdCzxsListStr = JSONUtil.toJsonStr(CollUtil.newArrayList(0.3, 1));
//
//    public List<Double> getGdCzxsList() {
//        return JSONUtil.toList(gdCzxsListStr, Double.class);
//    }
//
//    /**
//     * czCzxs：谷底差值亏损爆发比例
//     */
//    private String czCzxsListStr = JSONUtil.toJsonStr(CollUtil.newArrayList(0.3, 0.5));
//
//    public List<Double> getCzCzxsList() {
//        return JSONUtil.toList(czCzxsListStr, Double.class);
//    }
//
//    /**
//     * lxbf1
//     */
//    private double lxbf1 = 3; // 0,200
//
//    private double lxbf2 = 2.9; // 200,600
//
//    private double lxbf3 = 2.8; // 600,1200
//
//    private double lxbf4 = 2.7; // 1200,2000
//
//    private double lxbf5 = 2.6; // 2000,3300
//
//    private double lxbf6 = 2.5; // 3300,4600
//
//    private double lxbf7 = 1.8; // 4600,7000
//
//    private double lxbf8 = 1.6; // 7000,10000
//
//    private double lxbf9 = 1.2; // 10000,20000
//
//    /**
//     * aq开关：0 关 1 开
//     */
//    private int aqFlag = 1;

}
