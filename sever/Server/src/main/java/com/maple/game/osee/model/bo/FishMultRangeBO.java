package com.maple.game.osee.model.bo;

import java.util.List;

import lombok.Data;

@Data
public class FishMultRangeBO {

    /**
     * 范围
     */
    private List<List<Integer>> rangeAllList;

    /**
     * 爆发
     */
    private List<Double> bf;

    /**
     * 回收
     */
    private List<Double> hs;

    /**
     * 初始爆发
     */
    private List<Double> chuShiBf;

    /**
     * 初始回收
     */
    private List<Double> chuShiHs;

}
