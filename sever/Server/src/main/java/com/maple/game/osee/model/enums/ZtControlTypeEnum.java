package com.maple.game.osee.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 正态分布控制，爆发回收类型的枚举类
 */
@AllArgsConstructor
@Getter
public enum ZtControlTypeEnum {

    HS_NORMAL(1), // 回收-正常
    HS_LOW_MULT(1), // 回收-低倍鱼，比如：1 2类型的鱼
    HS_GRAND_PRIX(1), // 回收-大奖赛，2023-03-14：新增
    HS_DEMO(1), // 回收-体验场，2023-03-14：新增
    HS_ROBOT(1), // 回收-机器人，2023-04-07：新增

    BF_NORMAL(2), // 爆发-正常
    BF_X1(2), // 爆发-x1场次，2023-03-14：废弃

    ;

    private final int type; // 1 回收 2 爆发

}
