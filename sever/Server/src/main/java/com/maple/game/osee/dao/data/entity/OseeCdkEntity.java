package com.maple.game.osee.dao.data.entity;

import com.maple.database.data.DbEntity;

import lombok.Getter;
import lombok.Setter;

/**
 * 1688 CDK实体类
 */
@Getter
@Setter
public class OseeCdkEntity extends DbEntity {

    private static final long serialVersionUID = -739784491987993967L;

    /**
     * cdk
     */
    private String cdk;

    /**
     * 类型id
     */
    private long typeId;

    /**
     * cdk奖励
     */
    private String rewards;

    /**
     * 兑换人id
     */
    private long userId;

    /**
     * 兑换人游戏 id
     */
    private Long userGameId;

    /**
     * 兑换人昵称
     */
    private String nickname;


}
