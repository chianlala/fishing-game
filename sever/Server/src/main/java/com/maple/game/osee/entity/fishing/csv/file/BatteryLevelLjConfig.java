package com.maple.game.osee.entity.fishing.csv.file;

import com.maple.engine.anotation.AppData;
import com.maple.engine.data.BaseCsvData;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 龙晶场，炮台等级配置表
 */
@EqualsAndHashCode(callSuper = true)
@AppData(fileUrl = "data/fishing/cfg_battery_level_lj.csv")
@Data
public class BatteryLevelLjConfig extends BaseCsvData {

    /**
     * 炮台等级
     */
    private long batteryLevel;

    /**
     * 使用场景
     */
    private int scene;

    /**
     * 解锁需要的费用
     */
    private long cost;

    /**
     * 解锁成功奖励金币数
     */
    private long gold;

    /**
     * 积分加成
     */
    private double pointsBonus;

}
