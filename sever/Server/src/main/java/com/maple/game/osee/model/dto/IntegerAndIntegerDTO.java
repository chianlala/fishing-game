package com.maple.game.osee.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class IntegerAndIntegerDTO {

    /**
     * 类型
     */
    private Integer type;

    /**
     * 值
     */
    private Integer value;

}
