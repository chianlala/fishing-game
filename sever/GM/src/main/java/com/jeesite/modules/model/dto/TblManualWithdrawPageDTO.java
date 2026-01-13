package com.jeesite.modules.model.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

@EqualsAndHashCode(callSuper = true)
@Data
public class TblManualWithdrawPageDTO extends MyPageDTO {

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
     * 状态：提现状态：1 审核中 2 通过 3 拒绝
     */
    private Integer status;

    /**
     * 创建时间范围：开始时间
     */
    private Date ctBeginTime;

    /**
     * 创建时间范围：结束时间
     */
    private Date ctEndTime;

}
