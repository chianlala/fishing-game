package com.maple.game.osee.entity.fishing.csv.file;

import com.maple.engine.anotation.AppData;
import com.maple.engine.data.BaseCsvData;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 鱼配置
 */
@EqualsAndHashCode(callSuper = true)
@AppData(fileUrl = "data/fishing/cfg_fish_join_money.csv")
@Data
public class FishJoinMoneyConfig extends BaseCsvData {

    /**
     * 最低金币/龙晶限制
     */
    private long minMoney;

    /**
     * 机器人生成最高金币/龙晶限制
     */
    private long robotMax;

    /**
     * 机器人生成最低金币/龙晶限制
     */
    private long robotMin;

    /**
     * 机器人生成钻石下限
     */
    private long robotDiamondMin;

    /**
     * 机器人生成钻石上限
     */
    private long robotDiamondMax;

    /**
     * 机器人生成奖卷下限
     */
    private long robotLotteryMin;

    /**
     * 机器人生成奖卷上限
     */
    private long robotLotteryMax;

}
