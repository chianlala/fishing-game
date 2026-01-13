package com.maple.game.osee.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Data;

/**
 * 代理的道具表
 */
@TableName(value = "tbl_agent_props_info")
@Data
public class TblAgentPropsInfoDO {

    /**
     * 代理的 userId
     */
    @TableId(type = IdType.INPUT)
    private Long id;

    /**
     * 龙晶总数量
     */
    private Long dragonCrystalTotal;

    /**
     * 龙晶可用数量
     */
    private Long dragonCrystalAvailable;

}
