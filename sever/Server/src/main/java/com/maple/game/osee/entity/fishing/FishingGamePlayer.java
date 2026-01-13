package com.maple.game.osee.entity.fishing;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.redisson.api.RedissonClient;

import com.maple.game.osee.entity.ItemChangeReason;
import com.maple.game.osee.entity.ItemId;
import com.maple.game.osee.entity.NewBaseGamePlayer;
import com.maple.game.osee.entity.fishing.game.FireStruct;
import com.maple.game.osee.manager.PlayerManager;
import com.maple.game.osee.util.RandomUtil;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 捕鱼游戏玩家
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class FishingGamePlayer extends NewBaseGamePlayer {

    /**
     * 下一次使用雷鸣破的次数，小于等于 0 表示，要使用雷鸣破
     */
    private long nextUseLeiMingPoCount = RandomUtil.getRandom(7, 10 + 1);

    /**
     * 上一次使用天神关羽的时间戳，目的：防止多次请求
     */
    private Date lastUseTianShenGuanYuTs;

    /**
     * 上一次使用万剑诀的时间戳，目的：防止多次请求
     */
    private long lastUseWanJianJueTs;

    /**
     * 上一次使用炎爆符的时间戳，目的：防止多次请求
     */
    private long lastUseYanBaoFuTs;

    /**
     * 炮台外观序号
     */
    private int viewIndex;
    /**
     * 翅膀外观序号
     */
    private int wingIndex;

    /**
     * 炮台倍数
     */
    private int batteryMult = 1;

    /**
     * 玩家子弹表
     */
    private Map<Long, FireStruct> fireMap = new ConcurrentHashMap<>();

    /**
     * 进入捕鱼房间时的时间 用于计算玩家在房间内的时长
     */
    private long enterRoomTime = 0;

    /**
     * 上次使用雷神变的时间
     */
    private long lastLeiShenBianTime;

    /**
     * 上次使用雷鸣破的时间
     */
    private long lastLeiMingPoTime;

    /**
     * 上次使用天神关羽的时间
     */
    private long lastTianShenGuanYuTime;

    /**
     * 上次使用自动开炮的时间
     */
    private long lastAutoFireTime;

    /**
     * 上次使用锁定的时间
     */
    private long lastLockTime;

    /**
     * 上次使用冰冻技能的时间
     */
    private long lastFrozenTime;

    /**
     * 上次使用急速的时间
     */
    private long lastFastTime;

    /**
     * 上次使用电磁炮的时间
     */
    private long lastElectromagneticTime;

    /**
     * 上次使用黑洞炮的时间
     */
    private long lastBlackHoleTime;

    /**
     * 上次使用鱼雷炮的时间
     */
    private long lastTorpedoTime;

    /**
     * 上次使用钻头的时间
     */
    private long lastBitTime;

    /**
     * 最后一次触发暴击的时间
     */
    private long lastCritTime;

    /**
     * 最后一次使用分身炮的时间
     */
    private long lastFenShenTime;

    /**
     * 最后一次使用翻倍炮的时间
     */
    private long lastDoubleTime;

    /**
     * 最后一次开炮时间，或者，最后一次使用技能时间
     * Long 用户ID  Long 时间挫
     */
    private long lastFireTime = System.currentTimeMillis();
//    public static Map<Long,Long> lastFireTime = new ConcurrentHashMap<>();

    /**
     * 进入房间时金币
     */
    private long enterMoney;

    /**
     * 变化金币
     */
    private long changeMoney;

    /**
     * 抽水金币
     */
    private long cutMoney;

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
    private long dropBronzeTorpedoNum = 0;

    /**
     * 掉落的白银鱼雷数量
     */
    private long dropSilverTorpedoNum = 0;

    /**
     * 掉落的黄金鱼雷数量
     */
    private long dropGoldTorpedoNum = 0;

    /**
     * 掉落的黄金鱼雷数量
     */
    private long dropGoldTorpedoBangNum = 0;

    /**
     * 掉落的稀有鱼雷数量
     */
    private long dropRareTorpedoNum = 0;

    /**
     * 掉落的稀有鱼雷数量
     */
    private long dropRareTorpedoBangNum = 0;

    private long dragonCrystal = 0;

    /**
     * 鱼雷金币
     */
    private long torpedoMoney = 0;

    /**
     * 获取玩家金币数量
     */
    @Override
    public long getMoney() {
        return PlayerManager.getPlayerMoney(getUser());
    }

    /**
     * 添加金币
     */
    public void addMoney(long count) {
        PlayerManager.addItem(getUser(), ItemId.MONEY, count, ItemChangeReason.FISHING_RESULT, false);
    }

    public void useBattery(long count, NewBaseFishingRoom gameRoom, RedissonClient redissonClient) {

    }

    /**
     * 获取玩家vip等级
     */
    public int getVipLevel() {
        return PlayerManager.getPlayerVipLevel(getUser());
    }

    /**
     * 获取玩家钻石
     */
    public long getDiamond() {
        return PlayerManager.getPlayerDiamond(getUser());
    }

    /**
     * 获取玩家奖券
     */
    public long getLottery() {
        return PlayerManager.getPlayerLottery(getUser());
    }

    /**
     * 获取玩家等级
     */
    public int getLevel() {
        return PlayerManager.getPlayerEntity(getUser()).getLevel();
    }

}
