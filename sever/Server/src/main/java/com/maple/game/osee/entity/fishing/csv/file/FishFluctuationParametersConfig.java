package com.maple.game.osee.entity.fishing.csv.file;

import java.util.List;

import com.maple.engine.anotation.AppData;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.json.JSONUtil;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * slot 命中系数
 */
@AppData(fileUrl = "data/fishing/cfg_fish_FluctuationParameters.csv")
@EqualsAndHashCode(callSuper = true)
@Data
public class FishFluctuationParametersConfig extends BaseFluctuationParametersConfig {

    /**
     * aq1的 bfqsjd乘积范围
     */
    private String pctdListStr;

    public List<Double> getPctdList() {
        List<Double> resList = JSONUtil.toList(pctdListStr, Double.class);
        return CollUtil.isEmpty(resList) ? CollUtil.newArrayList(0.2d, 0.6d) : resList;
    }

    /**
     * 机器人积分加成范围
     */
    private String robotIntegralMultListStr;

    public List<Double> getRobotIntegralMultList() {
        List<Double> resList = JSONUtil.toList(robotIntegralMultListStr, Double.class);
        return CollUtil.isEmpty(resList) ? CollUtil.newArrayList(1d, 2d) : resList;
    }

    /**
     * 机器人命中：1玩家命中逻辑；2概率随机命中逻辑
     */
    private int robotHitType = 2;

}
