package com.maple.game.osee.model.entity;

import java.util.Date;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Data;

/**
 * 主表：手动提现
 */
@TableName(value = "tbl_manual_withdraw")
@Data
public class TblManualWithdrawDO {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 提现用户的 id
     */
    private Long userId;

    /**
     * 提现用户的 gameId
     */
    private Long userGameId;

    /**
     * 提现用户的 登录账号
     */
    private String userSignInName;

    /**
     * 提现发起时间
     */
    private Date createTime;

    /**
     * 提现更新时间
     */
    private Date updateTime;

    /**
     * 提现更新人 code（后台）
     */
    private String updateCode;

    /**
     * 提现更新人名称
     */
    private String updateName;

    /**
     * 提现龙晶前的数量
     */
    private Long numberPre;

    /**
     * 提现龙晶的数量
     */
    private Long number;

    /**
     * 提现龙晶后的数量
     */
    private Long numberSuf;

    /**
     * 提现状态：1 审核中 2 通过 3 拒绝
     */
    private Integer status;

}
