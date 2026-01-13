package com.maple.game.osee.model.bo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ActiveConfigBO {

    // 用户 id
    private Long userId;

    // 权重
    private Integer weight;

}
