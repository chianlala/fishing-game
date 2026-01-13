package com.maple.game.osee.model.dto;

import com.maple.database.model.dto.MyPageDTO;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class GameKillFishPageDTO extends MyPageDTO {

    /**
     * 用户 id
     */
    private Long userId;

    /**
     * 用户登录名
     */
    private String userName;

    /**
     * 记录类型
     */
    private Integer type;

    /**
     * 游戏状态
     */
    private Integer gameState;

    /**
     * 个控值：类型：1 为0 2 不为0
     */
    private Integer individualControlValueType;

    /**
     * 鱼的名字
     */
    private String killFishName;

    /**
     * 时间范围：起始时间
     */
    private String ctBeginTime;

    /**
     * 时间范围：结束时间
     */
    private String ctEndTime;

}
