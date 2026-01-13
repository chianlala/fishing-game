package com.maple.game.osee.model.entity;

import lombok.Data;

@Data
public class TblGivePropsDO {

    private Long createId;

    /**
     * 发送的物品 id
     */
    private Integer propsId;

    /**
     * 发送的物品数量
     */
    private Long propsCount;

    /**
     * 接收方的游戏 id
     */
    private Long toGameId;

}
