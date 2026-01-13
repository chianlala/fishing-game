package com.maple.game.osee.entity.fishing;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.atomic.AtomicLong;

import com.google.protobuf.GeneratedMessage;
import com.maple.game.osee.entity.NewBaseGameRoom;
import com.maple.game.osee.entity.fishing.csv.file.FishRefreshRule;
import com.maple.game.osee.entity.fishing.csv.file.RouteConfig;
import com.maple.game.osee.entity.fishing.game.FishStruct;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;

@EqualsAndHashCode(callSuper = true)
@Data
@Slf4j
public abstract class NewBaseFishingRoom extends NewBaseGameRoom {

    /**
     * 配置表里面的 gameId，-1 是没有房间时的 -2 是普通场
     */
    private int configGameId;

    public abstract int getRefreshFishResponseValue(); // 必须指定 刷鱼的响应协议值

    public abstract int getFishMultipleResponseValue(); // 必须指定 倍数鱼的响应协议值

    public abstract int getUseBossBugleResponseValue(); // 必须指定 使用boss号角的响应协议值

    public abstract GeneratedMessage.Builder<?> getRefreshFishMessageBuilder(); // 必须指定 刷新鱼，需要的 builder

    public abstract void addFishInfos(FishStruct fishStruct, GeneratedMessage.Builder<?> messageBuilder); // 必须指定 添加鱼的逻辑

    /**
     * 实体 鱼id生成类
     */
    private AtomicLong roomIdCreator = new AtomicLong(1L);

    @Override
    public int getGameId() {
        return getRoomIndex();
    }

    public long getNextId() {
        return roomIdCreator.getAndIncrement();
    }

    /**
     * 下次刷新鱼时间
     */
    private Map<FishRefreshRule, Long> nextRefreshTime;

    /**
     * 该房间所有的鱼群刷新规则   刷新类型：1 普通 4普通一号 5普通二号
     */
    private List<FishRefreshRule> refreshRuleList = new LinkedList<>();

    /**
     * 当前房间刷鱼禁用类型  每条鱼都记录的
     * FishRefreshRule.type 刷新类型 1 普通 2 神灯 3号角 4普通一号 5普通二号 6鱼潮
     * 思路：
     * 刷新鱼后记录 当前警用 类型
     * 鱼走后(获取死亡) 取消 禁用类型
     */
    private Map<FishStruct,List<Integer>> roomFishForbiddenTypes = new ConcurrentHashMap<>();

    /**
     *  记录限制刷新规则 鱼的 时间挫 每条鱼都记录的
     *  Integer  FishRefreshRule.type 刷新类型 1 普通 2 神灯 3号角 4普通一号 5普通二号 6鱼潮
     *  FishStruct 记录鱼
     *  Long 到期时间挫 时间挫
     */
    private Map<FishStruct,Long> roomMaxTypesFishForbiddenTime = new ConcurrentHashMap<>();

    /**
     * 记录房间 移除刷新规则 鱼的map
     * Integer： 移除类型
     * List<Map<FishRefreshRule, Long>>  下次刷新鱼时间 集合
     */
    private Map<FishRefreshRule, Long> rootRemoveFishForbiddenTypes = new ConcurrentHashMap<>();

    /**
     * 最小鱼潮间隔时间
     */
    private int minFishTideDelay;

    /**
     * 最大鱼潮间隔时间
     */
    private int maxFishTideDelay;

    /**
     * 下次鱼潮时间
     */
    private long nextFishTideTime;

    /**
     * 最大鱼潮持续时间
     */
    private double maxDuration;

    /**
     * 房间内boss刷新规则
     */
    private List<FishRefreshRule> bossRefreshRuleList = new LinkedList<>();

    // /**
    // * 房间内神灯刷新规则
    // */
    // private List<FishRefreshRule> lampRefreshRuleList = new LinkedList<>();

    /**
     * 准点刷新的规则 idSet
     */
    private Set<Long> refreshTimeRuleIdSet = new CopyOnWriteArraySet<>();

    /**
     * 房间内号角刷新规则
     */
    private List<FishRefreshRule> bugleRefreshRuleList = new LinkedList<>();

    /**
     * 房间鱼表
     */
    private Map<Long, FishStruct> fishMap = new ConcurrentHashMap<>();

//    /**
//     * 扩展 put ,调用打印日志
//     * @param fishStruct
//     */
//    public void putFishMap(FishStruct fishStruct){
//        fishMap.put(fishStruct.getId(), fishStruct);
//    }
//
//    /**
//     * 扩展remover ,
//     * @param id
//     */
//    public void removeFishMap(Long id){
//        fishMap.remove(id);
//    }

    /**
     * 房间内boss数量
     */
    private int boss = 0;

    /**
     * 房间内号角鱼数量
     */
    private int bugleFishNumber = 0;

    /**
     * 可以使用 boss号角的时间戳
     */
    private long canUseBossBugleTime;

    /**
     * 下一次会刷新的 boss鱼 fishId，会提前 20秒左右，赋值，并且boss刷新之后，该值会设置为 0，用于：同步 boss的一些效果
     */
    private long nextRefreshBossFishId;

    /**
     * 下一次刷新boss鱼的路线信息，备注：boss刷新之后，该值会设置为 null
     */
    private RouteConfig nextRefreshBossRouteConfig;

    /**
     * 下一次刷新boss鱼的时间，备注：boss刷新之后，该值会设置为 0
     */
    private long nextRefreshBossTime;

    /**
     * 是否正在刷新鱼潮
     */
    private boolean fishTide;

    /**
     * 房间无鱼持续时间
     */
    private int noFishTick;

    /**
     * 上一次 boss移除时间：毫秒，根据这个值是否大于 0来判断，上次是否有 boss鱼
     */
    private long lastBossRemoveTime = 0;

    /**
     * 上一次移除 boss鱼 fishId
     */
    private long lastBossRemoveFishId = 0;

    /**
     * 房间最后冰冻时间
     */
    private long lastRoomFrozenTime;

    /**
     * 最后刷新机器人时间
     */
    private long lastRefreshRobotTime = System.currentTimeMillis();

    /**
     * 是否是vip玩家创建的房间，备注：该字段暂时未使用
     */
    private boolean vip = false;

    /**
     * 是否：需要验证，备注：有密码时，该值为 true
     */
    private boolean verify;

    /**
     * 房间密码,VIP才能设置
     */
    private String roomPassword = "";

    /**
     * 机器人创建的密码房
     */
    private boolean robotVerify = false;

    /**
     * 机器人创建的密码房，安全未来时间戳：目的：让该房间可以不被马上清除
     */
    private long robotVerifySafeTs;

    public boolean reset(boolean cleanFishMapFlag) {

        synchronized (this) {

            long fishMapSize = getFishMap().size();

            setRoomTick(0);
            getRoomIdCreator().set(1L);

            if (cleanFishMapFlag) {
                getFishMap().clear();
            }

            // if (fishMapSize != 0) {
            //
            // log.info("房间重置：{}，重置：{}", getCode(), cleanFishMapFlag);
            //
            // }

            setNextRefreshTime(null);
            setMinFishTideDelay(0);
            setMaxFishTideDelay(0);
            setFishTide(false);
            setNoFishTick(0);
            setNextFishTideTime(0);
            setLastRoomFrozenTime(0);
            setLastRefreshRobotTime(0);
            getRefreshRuleList().clear();
            getBossRefreshRuleList().clear();
            getBugleRefreshRuleList().clear();
            setBoss(0);
            setBugleFishNumber(0);
            setLastBossRemoveTime(0);
            setLastBossRemoveFishId(0);
            setNextRefreshBossFishId(0);
            setNextRefreshBossRouteConfig(null);
            setNextRefreshBossTime(0);

            return fishMapSize != 0; // 等于 0则表示：空房间，不等于 0则表示，旧房间

        }

    }

}
