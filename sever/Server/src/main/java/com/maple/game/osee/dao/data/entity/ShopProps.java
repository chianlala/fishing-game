package com.maple.game.osee.dao.data.entity;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class ShopProps {

    private Long id;

    /**
     * 获得的道具
     */
    private Integer propsId;

    private String name;

    /**
     * 获得的道具数量
     */
    private Long quantity;

    /**
     * 花费的道具
     */
    private Integer currency;

    /**
     * 花费的道具数量
     */
    private Long price;

    private Integer status;

    private Double firstGive;

    private Double followUpGive;

}
