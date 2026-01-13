package com.maple.game.osee.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum FishingRobotTargetTypeEnum {

    ONE_ONE(101), // 攻击目标消失时切换，目标类型 1，备注：code 会和 100取余，得到鱼的类型
    ONE_TWO(102), // 攻击目标消失时切换，目标类型 2，备注：code 会和 100取余，得到鱼的类型
    ONE_THREE(103), // 攻击目标消失时切换，目标类型 3，备注：code 会和 100取余，得到鱼的类型
    ONE_FOUR(104), // 攻击目标消失时切换，目标类型 4，备注：code 会和 100取余，得到鱼的类型

    ONE_FIVE(191), // 攻击目标消失时切换，无目标

    TWO_ONE(201), // 无目标，1

    ;

    private int code; // 编号

}
