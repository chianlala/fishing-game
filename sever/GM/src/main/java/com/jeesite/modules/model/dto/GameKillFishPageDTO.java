package com.jeesite.modules.model.dto;

import cn.hutool.json.JSONObject;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class GameKillFishPageDTO extends MyPageDTO {

    /**
     * 用户 id
     */
    private String userId;

    /**
     * 用户昵称
     */
    private String userName;

    /**
     * 鱼种名字
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
     * 记录类型，字典
     */
    private List<JSONObject> typeMapList;

    /**
     * 游戏状态，字典
     */
    private List<JSONObject> gameStateMapList;

}
