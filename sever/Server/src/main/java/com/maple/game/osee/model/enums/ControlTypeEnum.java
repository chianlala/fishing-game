package com.maple.game.osee.model.enums;

import com.maple.game.osee.util.FishingChallengeFightFishUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 控制类型，枚举类
 */
@AllArgsConstructor
@Getter
public enum ControlTypeEnum {

    NORMAL_CONTROL("正常控制", 1, 0,
            FishingChallengeFightFishUtil.NORMAL_CURRENT_HIT_COUNT_CHALLENGE_USER_ID_AND_MODEL_ID_PRE,
            FishingChallengeFightFishUtil.NORMAL_NEED_HIT_COUNT_CHALLENGE_USER_ID_AND_MODEL_ID_PRE), //

    SINGLE_NORMAL_CONTROL("个控正常", 2, 0,
            ControlTypeEnum.SINGLE_NORMAL_FISHING_NORMAL_CURRENT_HIT_COUNT_CHALLENGE_USER_ID_AND_MODEL_ID_PRE,
            ControlTypeEnum.SINGLE_NORMAL_FISHING_NORMAL_NEED_HIT_COUNT_CHALLENGE_USER_ID_AND_MODEL_ID_PRE), //

    // PERSONAL_CONTROL_BURST("个控爆发", 2, 1,
    // FishingChallengeFightFishUtil.BURST_CURRENT_HIT_COUNT_CHALLENGE_USER_ID_AND_MODEL_ID_PRE,
    // FishingChallengeFightFishUtil.BURST_NEED_HIT_COUNT_CHALLENGE_USER_ID_AND_MODEL_ID_PRE, null, null), //
    //
    // PERSONAL_CONTROL_RECYCLE("个控回收", 2, -1,
    // FishingChallengeFightFishUtil.RECYCLE_CURRENT_HIT_COUNT_CHALLENGE_USER_ID_AND_MODEL_ID_PRE,
    // FishingChallengeFightFishUtil.RECYCLE_NEED_HIT_COUNT_CHALLENGE_USER_ID_AND_MODEL_ID_PRE, null, null), //

    ;

    // 捕鱼：个控正常：当前命中的总次数，根据：用户 id，modelId，区分
    private static final String SINGLE_NORMAL_FISHING_NORMAL_CURRENT_HIT_COUNT_CHALLENGE_USER_ID_AND_MODEL_ID_PRE =
            "SINGLE_NORMAL_FISHING_NORMAL_CURRENT_HIT_COUNT_CHALLENGE_USER_ID_AND_MODEL_ID_PRE:";

    // 捕鱼：个控正常：需要命中的总次数，根据：用户 id，modelId，区分
    private static final String SINGLE_NORMAL_FISHING_NORMAL_NEED_HIT_COUNT_CHALLENGE_USER_ID_AND_MODEL_ID_PRE =
            "SINGLE_NORMAL_FISHING_NORMAL_NEED_HIT_COUNT_CHALLENGE_USER_ID_AND_MODEL_ID_PRE:";

    // slots：个控正常：当前命中的总次数，根据：用户 id，区分
    private static final String SINGLE_NORMAL_SLOTS_NORMAL_CURRENT_HIT_COUNT_SLOT_USER_ID_PRE =
            "SINGLE_NORMAL_SLOTS_NORMAL_CURRENT_HIT_COUNT_SLOT_USER_ID_PRE:";

    // slots：个控正常：需要命中的总次数，根据：用户 id，区分
    private static final String SINGLE_NORMAL_SLOTS_NORMAL_NEED_HIT_COUNT_SLOT_USER_ID_PRE =
            "SINGLE_NORMAL_SLOTS_NORMAL_NEED_HIT_COUNT_SLOT_USER_ID_PRE:";

    private final String name; // 控制类型名称
    private final int code; // 控制类型 code
    private final int type; // -1 回收 0 正常 1 爆发

    String currentHitCountPre; // 当前命中的总次数-捕鱼
    String needHitCountPre; // 需要命中的总次数-捕鱼

}
