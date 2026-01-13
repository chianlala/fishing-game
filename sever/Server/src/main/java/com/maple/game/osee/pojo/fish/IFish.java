package com.maple.game.osee.pojo.fish;

import com.maple.gamebase.data.fishing.BaseFishingRoom;

/**
 * 鱼
 */
public interface IFish {

    /**
     * 只计算鱼的倍数
     *
     * @return 鱼的倍数
     */
    FishMultipleHelperDTO onlyComputeMultiple();

    /**
     * 计算鱼的倍数
     *
     * @return 鱼的倍数
     */
    long computeMultiple(String key);

    /**
     * 获取鱼的倍数
     *
     * @return 鱼的倍数
     */
    long getMultiple();

    /**
     * 获取鱼的 血池控制倍数
     *
     * @return 鱼的 血池控制倍数
     */
    long getBloodPoolMultiple();

    /**
     * 被击杀后(数据重置)
     */
    void afterTheKill(BaseFishingRoom gameRoom, long winMoney, String key);

}
