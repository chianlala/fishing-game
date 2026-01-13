package com.jeesite.modules.model.entity;

import lombok.Data;

import java.util.Date;

/**
 * 代理邮件记录表
 */
@Data
public class TblAgentMailDO {

    /**
     * 后台为 负数的后台 userId，否则为代理的 userId
     */
    private Long createId;

    private Date createTime;

    private String createName;

    private Date updateTime;

    /**
     * 创建人的类型：1 后台 2 代理
     */
    private Integer createUserType;

    /**
     * 发送对象的 userId（渠道 id），多个用英文逗号隔开
     */
    private String toUserIds;

    /**
     * 发送对象的类型：1 代理 2 玩家
     */
    private Integer toUserType;

    /**
     * 发送的物品 id
     */
    private Integer propsId;

    /**
     * 发送的物品数量
     */
    private Long propsCount;

}
