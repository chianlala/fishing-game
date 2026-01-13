package com.maple.game.osee.model.entity;

import java.math.BigDecimal;
import java.util.Date;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Data;

/**
 * 捕鱼个控日志
 */
@Data
public class TblOseeFishingUserControllerLogDO {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Date createTime;

    private Long createId;

    private String createName;

    private Date updateTime;

    private Long updateId;

    private String updateName;

    private Long userId;

    /**
     * 钱包地址
     */
    private String walletAddress;

    /**
     * 类型：1 回收 2 爆发
     */
    private Byte type;

    /**
     * 类型值：1 2 3 4 5，根据 type组合使用
     */
    private Integer typeValue;

    /**
     * 类型值字符串，前端使用
     */
    @TableField(exist = false)
    private String typeValueStr;

    /**
     * 状态：1 生效中 2 完成 3 终止
     */
    private Byte status;

    /**
     * 目标值
     */
    private BigDecimal targetValue;

    /**
     * 当前值
     */
    private BigDecimal currentValue;

    /**
     * 实际值
     */
    private BigDecimal diffValue;

    /**
     * 实际调节值
     */
    private BigDecimal realFinishValue;

    /**
     * 目标次数
     */
    private String targetSpecifyFishStr;

    /**
     * 未完成次数
     */
    private String diffSpecifyFishStr;

    /**
     * 实际完成次数
     */
    private String realFinishSpecifyFishStr;

    /**
     * 类型一
     */
    private String one;

    /**
     * 类型二
     */
    private String two;

    /**
     * 类型三
     */
    private String three;

    /**
     * 类型四
     */
    private String four;

}
