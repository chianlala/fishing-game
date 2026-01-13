package com.maple.game.osee.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 大逃杀状态
 */
@AllArgsConstructor
@Getter
public enum BattleRoyaleRoomStatusEnum {

    WAIT_PLAYER_JOIN(101), // 等待玩家加入：最低 20个玩家开始

    WAIT_KILLER_APPEAR(201), // 等待杀手出现：60s

    KILLER_APPEAR(301), // 杀手出现

    ;

    private final int code;

}
