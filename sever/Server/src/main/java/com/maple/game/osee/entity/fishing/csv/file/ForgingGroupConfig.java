package com.maple.game.osee.entity.fishing.csv.file;

import com.maple.engine.anotation.AppData;
import com.maple.engine.data.BaseCsvData;

/**
 * 锻造/合成/强化配置
 */
@AppData(fileUrl = "data/fishing/cfg_forging_group.csv")
public class ForgingGroupConfig extends BaseCsvData {

    /**
     * 消耗材料
     */
    private String useScience;

    /**
     * 最小概率
     */
    private int minProbability;

    /**
     * 最大概率
     */
    private int maxProbability;

    /**
     * 成功奖励
     */
    private String successReward;

    /**
     * 失败奖励
     */
    private String failReward;

    public String getUseScience() {
        return useScience;
    }

    public void setUseScience(String useScience) {
        this.useScience = useScience;
    }

    public int getMinProbability() {
        return minProbability;
    }

    public void setMinProbability(int minProbability) {
        this.minProbability = minProbability;
    }

    public int getMaxProbability() {
        return maxProbability;
    }

    public void setMaxProbability(int maxProbability) {
        this.maxProbability = maxProbability;
    }

    public String getSuccessReward() {
        return successReward;
    }

    public void setSuccessReward(String successReward) {
        this.successReward = successReward;
    }

    public String getFailReward() {
        return failReward;
    }

    public void setFailReward(String failReward) {
        this.failReward = failReward;
    }
}
