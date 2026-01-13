package com.maple.game.osee.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum FishingRobotFireFrequencyEnum {

    TWO_HUNDRED(200), //
    // FOUR_HUNDRED(400), //
    // SIX_HUNDRED(600), //
    // EIGHT_HUNDRED(800), //
    // ONE_THOUSAND(1000), //
    // TWO_THOUSAND(2000), //
    // THREE_THOUSAND(3000), //

    ;

    private int ms; // 开火频率，单位：毫秒

}
