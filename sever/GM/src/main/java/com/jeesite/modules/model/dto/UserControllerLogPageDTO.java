package com.jeesite.modules.model.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

@EqualsAndHashCode(callSuper = true)
@Data
public class UserControllerLogPageDTO extends MyPageDTO {

    /**
     * 用户账号
     */
    private String walletAddress;

    /**
     * 状态：1 生效中 2 完成 3 终止
     */
    private Byte status;

    /**
     * 类型：1 回收 2 爆发
     */
    private Byte type;

    private Long createId;

    private String createName;

    /**
     * 创建时间范围：开始时间
     */
    private Date ctBeginTime;

    /**
     * 创建时间范围：结束时间
     */
    private Date ctEndTime;

}
