package com.maple.game.osee.dao.data.entity.gm;

import lombok.Getter;
import lombok.Setter;

/**
 * 后台CDK数据
 */
@Getter
@Setter
public class GmCdkInfo {

    /**
     * cdk
     */
    private String cdkey;

    /**
     * 类型id
     */
    private String typeName;

    /**
     * 奖励
     */
    private String rewards;

    /**
     * 兑换人id
     */
    private long userId;

    /**
     * 兑换人id
     */
    private Long userGameId;

    /**
     * 兑换人昵称
     */
    private String nickname;

    /**
     * 所属代理的用户 id
     */
    private Long agentId;

    /**
     * 所属代理的游戏 id
     */
    private Long agentGameId;

}
