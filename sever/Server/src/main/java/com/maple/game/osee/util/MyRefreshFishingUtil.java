package com.maple.game.osee.util;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.json.JSONUtil;
import com.google.protobuf.GeneratedMessage;
import com.maple.engine.container.DataContainer;
import com.maple.game.osee.entity.fishing.csv.file.*;
import com.maple.gamebase.data.BaseGamePlayer;
import com.maple.gamebase.data.fishing.BaseFishingRoom;
import com.maple.network.manager.NetManager;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 捕鱼工具类
 */
@Slf4j
public class MyRefreshFishingUtil {

    // 捕鱼游戏，房间下标 set，并且已经启用了的房间，注意：房间数量一旦确定了，就不要修改
    public static Set<Integer> ENABLE_FISHING_ROOM_INDEX_SET;

    // 捕鱼游戏，房间 map
    public static Map<Integer, FishCcxxConfig> FISHING_CCXX_CONFIG_MAP;

    // 所有游戏，房间 map
    public static Map<Integer, FishCcxxConfig> CCXX_CONFIG_MAP;

    // 龙晶捕鱼游戏，房间下标 list
    public static List<Integer> CHALLENGE_FISHING_ROOM_INDEX_LIST;

    // 龙晶捕鱼游戏，房间下标 set
    public static Set<Integer> CHALLENGE_FISHING_ROOM_INDEX_SET;

    // 积分场
    public static Integer INTEGRAL_ROOM_INDEX;

    // 积分场，龙晶，捕鱼游戏，房间下标 list，备注：不包含未启用的房间
    public static List<Integer> CHALLENGEE_AND_INTEGRAL_FISHING_ROOM_INDEX_LIST;

    // 积分场，龙晶，捕鱼游戏，刷鱼房间配置 list
    public static List<FishCcxxConfig> CHALLENGE_AND_INTEGRAL_FISHING_CCXX_CONFIG_LIST;

    // 体验场
    public static Integer DEMO_ROOM_INDEX;

    // 大奖赛
    public static Integer GRAND_PRIX_ROOM_INDEX;

    // 机器人房间下标
    public static List<Integer> ROBOT_ROOM_INDEX_LIST;

    // 游戏配置
    public static FishGameConfig FISH_GAME_CONFIG;


    static {

        init();

    }

    public synchronized static void init() {

        // 捕鱼游戏，房间下标 set，并且已经启用了的房间，注意：房间数量一旦确定了，就不要修改
        ENABLE_FISHING_ROOM_INDEX_SET = DataContainer.getDatas(FishCcxxConfig.class).stream()
                .filter(it -> it.getSessionId() > 1 && it.getOpen() == 1)
                .map(FishCcxxConfig::getSessionId).collect(Collectors.toSet());

        // 所有游戏，房间配置 map
        CCXX_CONFIG_MAP = DataContainer.getDatas(FishCcxxConfig.class).stream()
                .collect(Collectors.toMap(FishCcxxConfig::getSessionId, it -> it));

        // 捕鱼游戏，房间配置 map
        FISHING_CCXX_CONFIG_MAP = DataContainer.getDatas(FishCcxxConfig.class).stream()
                .filter(it -> ENABLE_FISHING_ROOM_INDEX_SET.contains(it.getSessionId()))
                .collect(Collectors.toMap(FishCcxxConfig::getSessionId, it -> it));

        // 龙晶捕鱼游戏，房间下标 list
        CHALLENGE_FISHING_ROOM_INDEX_LIST =
                ENABLE_FISHING_ROOM_INDEX_SET.stream().collect(Collectors.toList());

        // 龙晶捕鱼游戏，房间下标 set
        CHALLENGE_FISHING_ROOM_INDEX_SET = new HashSet<>(CHALLENGE_FISHING_ROOM_INDEX_LIST);

        // 积分场
        INTEGRAL_ROOM_INDEX =
                ENABLE_FISHING_ROOM_INDEX_SET.stream().filter(it -> it > 200 && it < 300).findFirst().orElse(-100);

        // 积分场，龙晶，捕鱼游戏，房间下标 list
        CHALLENGEE_AND_INTEGRAL_FISHING_ROOM_INDEX_LIST =
                CollUtil.addAllIfNotContains(CollUtil.newArrayList(INTEGRAL_ROOM_INDEX), CHALLENGE_FISHING_ROOM_INDEX_LIST);

        // 积分场，龙晶，捕鱼游戏，房间配置 list
        CHALLENGE_AND_INTEGRAL_FISHING_CCXX_CONFIG_LIST = DataContainer.getDatas(FishCcxxConfig.class).stream()
                .filter(it -> (CHALLENGEE_AND_INTEGRAL_FISHING_ROOM_INDEX_LIST.contains(it.getSessionId())))
                .collect(Collectors.toList());

        // 体验场
        DEMO_ROOM_INDEX =
                ENABLE_FISHING_ROOM_INDEX_SET.stream().filter(it -> it > 100 && it < 200).findFirst().orElse(-200);

        // 大奖赛
        GRAND_PRIX_ROOM_INDEX = ENABLE_FISHING_ROOM_INDEX_SET.stream().filter(it -> it < 100).findFirst().orElse(-300);

        if (GRAND_PRIX_ROOM_INDEX < 0) {

            ROBOT_ROOM_INDEX_LIST = new ArrayList<>(MyRefreshFishingUtil.CHALLENGE_FISHING_ROOM_INDEX_SET);

        } else {

            // 机器人房间下标
            ROBOT_ROOM_INDEX_LIST =
                    CollUtil.addAllIfNotContains(CollUtil.newArrayList(MyRefreshFishingUtil.GRAND_PRIX_ROOM_INDEX),
                            new ArrayList<>(MyRefreshFishingUtil.CHALLENGE_FISHING_ROOM_INDEX_SET));

        }

        // 游戏配置
        FISH_GAME_CONFIG = DataContainer.getDatas(FishGameConfig.class).stream().findFirst().orElse(null);

    }

    /**
     * 获取捕鱼场景名称
     */
    @Nullable
    public static String getSceneName(int roomIndex) {

        return DataContainer.getDatas(FishCcxxConfig.class).stream().filter(it -> it.getSessionId() == roomIndex)
                .map(FishCcxxConfig::getShowSessionName).findFirst().orElse(null);

    }

    /**
     * 通过规则和 routeId，找到对应的组和 RouteConfig
     */
    public static void findByRouteIdAndRule(long routeId, FishRefreshRule rule, RouteConfig routeConfig,
                                            FishGroupConfig fishGroupConfig) {

        // 获取：所有组
        List<FishGroupConfig> fishGroupConfigList = MyRefreshFishingUtil.getFishGroupConfigListByFishRefreshRule(rule);

        // 找到 routeId对应的组
        for (FishGroupConfig item : fishGroupConfigList) {

            for (RouteConfig subItem : item.getRouteList()) {

                if (subItem.getRouteId() == routeId) {

                    BeanUtil.copyProperties(item, fishGroupConfig);
                    BeanUtil.copyProperties(subItem, routeConfig);
                    return;

                }

            }

        }

    }

    /**
     * 发送信息给房间所有玩家
     */
    public static void sendRoomMessage(BaseFishingRoom room, int msgCode, GeneratedMessage.Builder<?> messageBuilder) {

        if (room != null) {

            for (BaseGamePlayer gamePlayer : room.getGamePlayers()) {

                if (gamePlayer != null) {

                    NetManager.sendMessage(msgCode, messageBuilder, gamePlayer.getUser());

                }

            }

        }

    }

    /**
     * 获取下次刷新时间
     */
    public static long getNextRefreshTime(FishRefreshRule refreshRule, long nowRefreshTime) {

        long minTime = refreshRule.getMinDelay();

        long maxTime = refreshRule.getMaxDelay() == 0 ? minTime : refreshRule.getMaxDelay();

        return nowRefreshTime + com.maple.game.osee.util.RandomUtil.getRandom(minTime, maxTime + 1);

    }

    /**
     * 通过刷新规则，拿到一个 随机鱼组
     *
     * @param notRefreshGroupIdList 不刷新的鱼组 idList
     */
    @Nullable
    public static FishGroupConfig getRandomFishGroupConfigByFishRefreshRule(FishRefreshRule rule,
                                                                            List<Long> notRefreshGroupIdList) {

        if (rule.getRefreshCollectType() == 2) {

            // 获取：nextRefreshCollectIdList，如果为空，则重新赋值，如果不为空，则随机一个，并移除
            List<Long> nextRefreshCollectIdList = rule.getNextRefreshCollectIdList();

            if (CollUtil.isEmpty(nextRefreshCollectIdList)) {

                nextRefreshCollectIdList = rule.getCollectIdList();
                rule.setNextRefreshCollectIdList(nextRefreshCollectIdList);

            }

            Long collectId = RandomUtil.randomEle(nextRefreshCollectIdList);
            nextRefreshCollectIdList.remove(collectId);

            return MyRefreshFishingUtil.getRandomFishGroupConfigByCollectId(collectId, notRefreshGroupIdList);

        } else {

            if (CollUtil.isEmpty(rule.getCollectIdList())) {
                return null;
            }

            // 完全随机
            Long random = RandomUtil.randomEle(rule.getCollectIdList());

            return MyRefreshFishingUtil.getRandomFishGroupConfigByCollectId(random, notRefreshGroupIdList);

        }

    }

    /**
     * 通过 collectId，拿到一个 随机鱼组
     */
    @Nullable
    public static FishGroupConfig getRandomFishGroupConfigByCollectId(Long collectId,
                                                                      List<Long> notRefreshGroupIdList) {

        FishCollectConfig fishCollectConfig = DataContainer.getData(collectId, FishCollectConfig.class);

        if (fishCollectConfig == null) {
            return null;
        }

        List<Long> groupIdList;

        if (CollUtil.isEmpty(notRefreshGroupIdList)) {

            groupIdList = fishCollectConfig.getGroupIdList();

        } else {

            groupIdList = fishCollectConfig.getGroupIdList().stream().filter(it -> !notRefreshGroupIdList.contains(it))
                    .collect(Collectors.toList());

        }

        if (CollUtil.isEmpty(groupIdList)) {
            return null;
        }

        Long groupId = RandomUtil.randomEle(groupIdList);

        return DataContainer.getData(groupId, FishGroupConfig.class);

    }

    /**
     * 通过刷新规则，拿到所有
     */
    public static List<FishGroupConfig> getFishGroupConfigListByFishRefreshRule(FishRefreshRule fishRefreshRule) {

        List<Long> collectIdList = fishRefreshRule.getCollectIdList();

        // 获取：所有鱼集合
        List<FishCollectConfig> allCollectIdList = collectIdList.stream()
                .map(it -> DataContainer.getData(it, FishCollectConfig.class)).collect(Collectors.toList());

        // 获取：所有的 FishGroupConfig
        return allCollectIdList.stream().map(it -> it.getGroupIdList().stream().map(subIt -> {

            return DataContainer.getData(subIt, FishGroupConfig.class);

        }).collect(Collectors.toList())).flatMap(Collection::stream).collect(Collectors.toList());

    }

    /**
     * 通过鱼组配置：获取随机 fishId
     */
    public static long getRandomFishIdByGroupConfig(FishGroupConfig fishGroupConfig) {
        return RandomUtil.randomEle(fishGroupConfig.getFishIdList());
    }

    /**
     * 通过鱼组配置：获取随机 RouteConfig，尽量和旧的 oldRouteIdSet不重复
     */
    @Nullable
    public static RouteConfig getRandomRouteByGroupConfig(FishGroupConfig fishGroupConfig, Set<Long> oldRouteIdSet) {

        if (fishGroupConfig == null) {
            return null;
        }

        if (CollUtil.isEmpty(oldRouteIdSet)) {
            return fishGroupConfig.getRandomRoute();
        }

        List<RouteConfig> routeList = fishGroupConfig.getRouteList();

        if (routeList.size() == 1) {
            return routeList.get(0);
        }

        List<RouteConfig> routeConfigList =
                routeList.stream().filter(it -> !oldRouteIdSet.contains(it.getRouteId())).collect(Collectors.toList());

        if (CollUtil.isEmpty(routeConfigList)) { // 如果为空：则随机从 oldRouteIdSet里面取一个

            return routeList.stream().filter(it -> oldRouteIdSet.contains(it.getRouteId())).findAny().orElse(null);

        }

        return RandomUtil.randomEle(routeConfigList);

    }

}
