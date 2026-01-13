package com.maple.game.osee.dao.log.entity;

import com.maple.database.data.DbEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * 捕鱼记录实体类
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class OseeFishingRecordLogEntity extends DbEntity {

    private static final long serialVersionUID = 5711596587043216673L;

    /**
     * 玩家id
     */
    private long playerId;

    /**
     * 捕鱼场次
     */
    private int roomIndex;

    /**
     * 花费金币
     */
    private long spendMoney;

    /**
     * 赢取的金币
     */
    private long winMoney;

    /**
     * 掉落的青铜鱼雷数量
     */
    private long dropBronzeTorpedoNum;

    /**
     * 掉落的白银鱼雷数量
     */
    private long dropSilverTorpedoNum;

    /**
     * 掉落的黄金鱼雷数量
     */
    private long dropGoldTorpedoNum;

    /**
     * 掉落的绑定黄金鱼雷数量
     */
    private long dropGoldTorpedoBangNum;

    /**
     * 掉落的绑定稀有鱼雷数量
     */
    private long dropRareTorpedoBangNum;

    /**
     * 掉落的稀有鱼雷数量
     */
    private long dropRareTorpedoNum;

    /**
     * 进入房间时间
     */
    private Date joinTime;

    /**
     * 退出房间时间
     */
    private Date exitTime;

    /**
     * 停留时间
     */
    private long timeSpending;

    /**
     * 道具使用情况
     */
    private String useProps;

    /**
     * 游戏类型：1 fishing game 2 slots game 3 大逃杀
     */
    private Integer gameType;

}
