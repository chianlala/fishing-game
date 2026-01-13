package com.maple.game.osee.dao.data.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * 用户道具表
 * @TableName g_user_props
 */
@TableName(value ="g_user_props")
@Data
public class GUserProps implements Serializable {
    /**
     * 用户id
     */
    @TableId
    private Long userId;

    /**
     * 物品id
     */
    private Integer propsId;

    /**
     * 物品数量/时间戳，备注：时间戳为 -100时，表示是永久
     */
    private Long quantity;

    /**
     * 到期时间
     */
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private Date expirationTime;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}