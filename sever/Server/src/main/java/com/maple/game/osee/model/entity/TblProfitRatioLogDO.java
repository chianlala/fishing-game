package com.maple.game.osee.model.entity;

import java.math.BigDecimal;
import java.util.Date;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Data;

/**
 * 主表：收益比历史记录
 */
@TableName(value = "tbl_profit_ratio_log")
@Data
public class TblProfitRatioLogDO {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Date createTime;

    @TableField(exist = false)
    private String createTimeStr;

    /**
     * 期望收益比
     */
    private BigDecimal hopeRatio;

    /**
     * 实际收益比
     */
    private BigDecimal realRatio;

    /**
     * 场次：0 1 2 3 4
     */
    private Integer roomIndex;

}
