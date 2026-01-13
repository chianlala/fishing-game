package com.maple.game.osee.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FireInfoDTO {

    /**
     * 场次的 index：0 1 2 3 4
     */
    private Integer index;

    /**
     * 玩家花费的钱
     */
    private Long usedMoney;

    /**
     * 玩家赢的钱
     */
    private Long winMoney;

}
