package com.maple.game.osee.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 血池控制：T 类型
 */
@AllArgsConstructor
@Getter
public enum BloodPoolTTypeEnum {

    HS_ONE("铁鱼", 1, 1, new Double[] {10d, 10d, 80d, 9000d}, new Double[] {50d, 50d, 160d, 10000d}), //
    HS_TWO("超级难", 1, 2, new Double[] {10d, 10d, 80d, 500d}, new Double[] {50d, 50d, 160d, 1000d}), //
    HS_THREE("比较难", 1, 3, new Double[] {10d, 10d, 50d, 100d}, new Double[] {50d, 50d, 80d, 500d}), //
    HS_FOUR("一般难", 1, 4, new Double[] {5d, 5d, 10d, 50d}, new Double[] {30d, 30d, 60d, 100d}), // 默认
    HS_FIVE("正常难度", 1, 5, new Double[] {5d, 5d, 5d, 5d}, new Double[] {30d, 30d, 50d, 50d}), //
    HS_SIX("高消低产", 1, 6, new Double[] {-6d, -6d, -6d, -6d}, new Double[] {-6d, -6d, -6d, -6d}), // 这里为：负数，目的：不和上面的重复，并且实际使用时不会取这个值

    BF_ONE("秒杀", 2, 1, new Double[] {-100d, -100d, -100d, -100d}, new Double[] {-90d, -90d, -100d, -100d}), //
    BF_TWO("超级容易", 2, 2, new Double[] {-100d, -100d, -100d, -100d}, new Double[] {-80d, -80d, -80d, -90d}), //
    BF_THREE("比较容易", 2, 3, new Double[] {-100d, -100d, -100d, -100d}, new Double[] {-70d, -70d, -70d, -70d}), //
    BF_FOUR("一般容易", 2, 4, new Double[] {-100d, -100d, -100d, -100d}, new Double[] {-40d, -40d, -60d, -60d}), // 默认
    BF_FIVE("正常难度", 2, 5, new Double[] {-80d, -80d, -80d, -100d}, new Double[] {-30d, -30d, -40d, -40d}), //
    BF_SIX("低消高产", 2, 6, new Double[] {6d, 6d, 6d, 6d}, new Double[] {6d, 6d, 6d, 6d}), // 这里为：正数，目的：不和上面的重复，并且实际使用时不会取这个值
    ;

    private String name; // 名称
    private int type; // 类型：1 回收 2 爆发
    private int value; // 难度
    private Double[] minArr; // 下限数组
    private Double[] maxArr; // 上限数组

}
