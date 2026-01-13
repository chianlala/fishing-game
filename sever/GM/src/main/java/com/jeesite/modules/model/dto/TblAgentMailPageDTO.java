package com.jeesite.modules.model.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

@EqualsAndHashCode(callSuper = true)
@Data
public class TblAgentMailPageDTO extends MyPageDTO {

    /**
     * 后台为 -1，否则为代理的 userId
     */
    private Long createId;

    /**
     * 发送对象的 userId（渠道 id）
     */
    private Long toUserId;

    /**
     * 起始时间：创建时间
     */
    private Date ctBeginTime;

    /**
     * 结束时间：创建时间
     */
    private Date ctEndTime;

    /**
     * 起始时间：更新时间
     */
    private Date utBeginTime;

    /**
     * 起始时间：更新时间
     */
    private Date utEndTime;

    /**
     * 状态：1 待领取 2 已领取 3 已撤销
     */
    private Integer state;

    /**
     * 发送的物品数量，最小值
     */
    private Long propsCountMin;

    /**
     * 发送的物品数量，最大值
     */
    private Long propsCountMax;

}
