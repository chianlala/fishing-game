package com.maple.game.osee.util;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.*;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.google.protobuf.GeneratedMessage;
import com.maple.database.config.redis.RedisHelper;
import com.maple.engine.container.DataContainer;
import com.maple.engine.data.ServerUser;
import com.maple.engine.utils.ThreadPoolUtils;
import com.maple.game.osee.dao.data.entity.OseePlayerEntity;
import com.maple.game.osee.entity.ItemChangeReason;
import com.maple.game.osee.entity.ItemId;
import com.maple.game.osee.entity.fishing.FishingGamePlayer;
import com.maple.game.osee.entity.fishing.NewBaseFishingRoom;
import com.maple.game.osee.entity.fishing.challenge.FishingChallengeRoom;
import com.maple.game.osee.entity.fishing.csv.file.*;
import com.maple.game.osee.entity.fishing.game.FishStruct;
import com.maple.game.osee.manager.PlayerManager;
import com.maple.game.osee.manager.fishing.FishingManager;
import com.maple.game.osee.pojo.fish.JiangQuan;
import com.maple.game.osee.proto.OseeMessage;
import com.maple.game.osee.proto.OseePublicData;
import com.maple.game.osee.proto.TtmyFishingGrandPrixMessage;
import com.maple.game.osee.proto.fishing.FishBossMessage;
import com.maple.game.osee.proto.fishing.OseeFishingMessage;
import com.maple.game.osee.proto.fishing.TtmyFishingChallengeMessage;
import com.maple.gamebase.container.GameContainer;
import com.maple.gamebase.data.BaseGamePlayer;
import com.maple.gamebase.data.BaseGameRoom;
import com.maple.network.manager.NetManager;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;
import org.redisson.client.codec.StringCodec;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.maple.game.osee.manager.fishing.FishingManager.BOSS_BUGLE_COOL_TIME;
import static com.maple.game.osee.manager.fishing.FishingManager.DYNAMIC_MULT_FISH_ROOM_CODE;

/**
 * 搜索：doFishingRoomTask（刷鱼规则）
 * <p>
 * // 获取：房间内的刷鱼规则
 * <p>
 * MyRefreshFishingHelper.getRoomRefreshRule(gameRoom);
 * <p>
 * // 检查并执行刷鱼
 * <p>
 * MyRefreshFishingHelper.checkAndRefresh(gameRoom);
 * <p>
 * 移除；int getRoomIndex 方法
 * <p>
 * 移除；void refreshGroupFish 方法
 * <p>
 * 移除；void refreshFish 方法
 * <p>
 * 移除；long getNextRefreshTime 方法
 * <p>
 * 移除；long refreshGroupFishGetRouteId 方法
 * <p>
 * 移除；void refreshGroupFishByRouteId 方法
 * <p>
 * 移除；void refreshGroupFishByRouteId 方法
 * <p>
 * 移除；void refreshFishWithDelay0 方法
 * <p>
 * 移除；void useBossBugle 方法，需要注意：使用限制
 * <p>
 * 移除；void sendRoomFishMult 方法
 * <p>
 * 移除；long getRoomRuleId 方法，并变成：MyRefreshFishingHelper.getRoomGoldFishRuleId
 * <p>
 * 搜索：神灯道具，并改成：MyRefreshFishingHelper.magicLampRefreshFish(gameRoom, routeId);
 * <p>
 * 下面一共：三个地方
 * <p>
 * 搜索：判断过期鱼，并从鱼表内移除，加上：MyRefreshFishingHelper.checkAndDurationRefreshFish(gameRoom, fish);
 * <p>
 * 搜索：【!boom && !hit】 或者 【!boom && !tool】，加上：MyRefreshFishingHelper.checkAndDurationRefreshFish(gameRoom, fish);
 * <p>
 * 搜索：Manager.useBossBugle，改成：MyRefreshFishingHelper.useBossBugle
 * 搜索：Manager.sendRoomFishMult，改成：MyRefreshFishingHelper.sendRoomFishMult
 * <p>
 * createFishInfoProto：MyRefreshFishingHelper.createFishInfoProtoForChallenge
 * createFishInfoProto：MyRefreshFishingHelper.createFishInfoProtoForGrandPrix
 * createFishInfoProto：MyRefreshFishingHelper.createFishInfoProtoForGeneral
 * <p>
 * sendRoomMessage：MyRefreshFishingUtil.sendRoomMessage
 * <p>
 * summonFish：MyRefreshFishingHelper.summonTestFish(request, user);
 */
@Slf4j
@Component
public class MyRefreshFishingHelper {

    private static RedissonClient redissonClient;

    /**
     * 禁鱼规则
     * 实现思路：
     * 在刷鱼时候，记录 房间的禁用类型规则，在此方法排除规则
     * 在该鱼死亡获取逃跑时候 取消房间的禁用  ，死亡鱼，在击中鱼里面  ，   逃跑记录时间挫。并且冰冻加时间
     * <p>
     * 应该不用 在刷普通鱼时候判断是否禁用
     *
     * @param room
     */
    public static void setRoomDisablingRules(NewBaseFishingRoom room) {
        // 判断禁用规则是否到期   -- 逃跑鱼
        for (Map.Entry<FishStruct, Long> integerLongEntry : room.getRoomMaxTypesFishForbiddenTime().entrySet()) {
            if (integerLongEntry.getValue() < System.currentTimeMillis()) {
                deleteRoomLimitFish(room, integerLongEntry.getKey());
            }
        }
        // 找到 所有 需要禁用规则
        List<FishRefreshRule> refreshRules = room.getNextRefreshTime().entrySet().stream().filter(item -> {
                    for (Map.Entry<FishStruct, List<Integer>> fishStructListEntry : room.getRoomFishForbiddenTypes().entrySet()) {
                        if (fishStructListEntry.getValue().contains(item.getKey().getType())) {
                            return true;
                        }
                    }
                    return false;
                }).map(Map.Entry::getKey)         // 取出被过滤的 getKey 的值;
                .collect(Collectors.toList());  // 收集结果到列表中;
        // 删除下次刷新鱼时间 并且记录
        for (FishRefreshRule refreshRule : refreshRules) {
            if (ObjectUtils.isNotEmpty(room.getNextRefreshTime().get(refreshRule))) {
                room.getRootRemoveFishForbiddenTypes().put(refreshRule, room.getNextRefreshTime().get(refreshRule));
                room.getNextRefreshTime().remove(refreshRule);
            }
        }
        // 统一清除鱼
        if (ObjectUtils.isNotEmpty(refreshRules)) {
            // 删除 当前房间刷鱼禁用类型
            for (Map.Entry<FishStruct, List<Integer>> fishStructListEntry : room.getRoomFishForbiddenTypes().entrySet()) {
                FishRefreshRule rule = DataContainer.getData(fishStructListEntry.getKey().getRuleId(), FishRefreshRule.class); // 找到此鱼的刷新规则
                if (refreshRules.contains(rule)) {
                    room.getRoomFishForbiddenTypes().remove(fishStructListEntry.getKey());
                }
            }
            // 删除 当前房间限制刷新规则 鱼的 时间挫 每条鱼都记录的
            for (Map.Entry<FishStruct, Long> fishStructLongEntry : room.getRoomMaxTypesFishForbiddenTime().entrySet()) {
                FishRefreshRule rule = DataContainer.getData(fishStructLongEntry.getKey().getRuleId(), FishRefreshRule.class); // 找到此鱼的刷新规则
                if (refreshRules.contains(rule)) {
                    room.getRoomFishForbiddenTypes().remove(fishStructLongEntry.getKey());
                }
            }
        }
    }

    /**
     * 删除 房间禁用 的鱼 、 删除 记录限制刷新规则 鱼的 时间挫 、 把删除的 全部 放入房间
     *
     * @param room
     * @param fishStruct
     */
    public static void deleteRoomLimitFish(NewBaseFishingRoom room, FishStruct fishStruct) {
        room.getRoomFishForbiddenTypes().remove(fishStruct);// 删除 房间禁用 的鱼
        room.getRoomMaxTypesFishForbiddenTime().remove(fishStruct);// 删除 记录限制刷新规则 鱼的 时间挫
        if (ObjectUtils.isNotEmpty(room.getRootRemoveFishForbiddenTypes())) {
            room.getNextRefreshTime().putAll(room.getRootRemoveFishForbiddenTypes()); // 把删除的 全部 放入房间
        }
        room.getRootRemoveFishForbiddenTypes().clear();
    }

    @Resource
    public void setRedissonClient(RedissonClient redissonClient) {
        MyRefreshFishingHelper.redissonClient = redissonClient;
    }

    // 判断：是否打印日志，传入：ruleId
    public static final Function<Long, Boolean> PRINT_LOG_FUNCTION = ruleId -> {

        // return ruleId == 839;

        // return true;

        // return ruleId == 893;
        return ruleId == 860;

    };

    /**
     * 获取：房间内的刷鱼规则
     */
    public static void getRoomRefreshRule(NewBaseFishingRoom room) {

        if (room.getNextRefreshTime() != null) {
            return;
        }

        Map<FishRefreshRule, Long> refreshTime = new HashMap<>();

        int roomIndex = room.getRoomIndex();

        // 获取该房间场次的所有刷鱼规则
        List<FishRefreshRule> allRefreshRuleList = CollUtil.newArrayList();
        // 获取：该房间场次所有的号角规则
        List<FishRefreshRule> bugleRefreshRuleList = CollUtil.newArrayList();

        DataContainer.getDatas(FishRefreshRule.class).forEach(it -> {

            if (BooleanUtil.isFalse(Arrays.asList(it.getRealScene()).contains(roomIndex))) {
                return;
            }

            if (CollUtil.isNotEmpty(it.getRefreshTimeList())) {

                CopyOnWriteArraySet<Integer> roomCodeSet = REFRESH_TIME_RULE_ID_AND_ROOM_CODE_MAP.computeIfAbsent(it.getId(), k -> new CopyOnWriteArraySet<>());

                roomCodeSet.add(room.getCode());

                room.getRefreshTimeRuleIdSet().add(it.getId()); // 添加：该准点刷新规则 id

                return;

            }

            if (it.getType() == 1 || it.getType() == 4 || it.getType() == 5 || it.getType() == 6) { // 1 普通 4普通一号 5普通二号 6鱼潮
                allRefreshRuleList.add(BeanUtil.copyProperties(it, FishRefreshRule.class));
            } else if (it.getType() == 3) {
                bugleRefreshRuleList.add(BeanUtil.copyProperties(it, FishRefreshRule.class));
            }

        });

        room.setBugleRefreshRuleList(bugleRefreshRuleList);

        // 规则放入房间内存放
        room.getRefreshRuleList().addAll(allRefreshRuleList);

        for (FishRefreshRule refreshRule : room.getRefreshRuleList()) {

            // 若为鱼潮，则重置房间鱼潮时间
            if (refreshRule.isFishTide()) {
                room.setMinFishTideDelay(refreshRule.getMinDelay());
                int max = refreshRule.getMaxDelay() == 0 ? refreshRule.getMinDelay() : refreshRule.getMaxDelay();
                room.setMaxFishTideDelay(max);

                if (room.getMinFishTideDelay() > 0) {
                    // 设置距离下次刷新鱼潮的时长
                    room.setNextFishTideTime(ThreadLocalRandom.current().nextLong(room.getMinFishTideDelay(), room.getMaxFishTideDelay() + 1));
                }
                continue;
            }

            // 将Boss鱼配置单独提出来
            if (refreshRule.isBoss()) {
                refreshRule.setNextRefreshTime(MyRefreshFishingUtil.getNextRefreshTime(refreshRule, 0)); // 新刷新规则：设置boss的下次刷新时间
                room.getBossRefreshRuleList().add(refreshRule);
                // boss鱼不自动刷，由系统控制刷新
                continue;
            }

            refreshTime.put(refreshRule, MyRefreshFishingUtil.getNextRefreshTime(refreshRule, 0));

        }

        room.setNextRefreshTime(refreshTime);

    }

    /**
     * 初始化设置鱼潮
     */
    private static void initializationFishFactory(List<FishRefreshRule> fishRefreshRuleList, NewBaseFishingRoom room) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        List<Long> list = new ArrayList<>();
        List<Boolean> boolList = new ArrayList<>();
        if (ObjectUtils.isEmpty(tideOfFishTimeMap) || ObjectUtils.isEmpty(tideOfFishTimeMap.get(room.getCode())) ||
                fishRefreshRuleList.size() != tideOfFishTimeMap.get(room.getCode()).size()) {
            for (FishRefreshRule fishRefreshRule : fishRefreshRuleList) {
                list.add(System.currentTimeMillis() + (MyRefreshFishingUtil.getNextRefreshTime(fishRefreshRule, 0) * 1000)); // 初始化设置鱼潮规则时间
                boolList.add(false);
                log.info("初始化鱼潮时间:{}", dateFormat.format(new Date(System.currentTimeMillis() + (MyRefreshFishingUtil.getNextRefreshTime(fishRefreshRule, 0) * 1000))));
            }
            tideOfFishTimeMap.put(room.getCode(), list);
            isTideOfFishMap.put(room.getCode(), boolList);
        }
    }

    /**
     * 鱼潮规则时间 Integer:房间ID  List<Long>>下次刷新时间
     */
    public static Map<Integer, List<Long>> tideOfFishTimeMap = new ConcurrentHashMap<>();
    /**
     * 是否正在鱼潮刷鱼
     */
    public static Map<Integer, List<Boolean>> isTideOfFishMap = new ConcurrentHashMap<>();
    /**
     * 最大房间刷鱼潮时间  Long 存时间挫
     */
    public static Map<Integer, Long> maxDurationMap = new ConcurrentHashMap<>();

    /**
     * 检查并执行刷鱼
     */
    public static void checkAndRefresh(NewBaseFishingRoom room) {
        // 时间格式
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        // 拿到当前房间 所有刷新鱼潮的规则
        List<FishRefreshRule> fishRefreshRuleList = room.getRefreshRuleList().stream().filter(index -> index.isFishTide()).collect(Collectors.toList());
        initializationFishFactory(fishRefreshRuleList, room);
        List<Long> tideOfFishTimeList = tideOfFishTimeMap.get(room.getCode());
        List<Boolean> booleanList = isTideOfFishMap.get(room.getCode());
        if (ObjectUtil.isNotEmpty(fishRefreshRuleList) && fishRefreshRuleList.size() > 0 && ObjectUtils.isNotEmpty(tideOfFishTimeList)) { // 鱼潮
//            if (FishingRobotChallengeUtil.getHasRealPlayer(room)) { //是否有真实玩家
            for (int i = 0; i < fishRefreshRuleList.size(); i++) {
                // 判断房间是否正在刷鱼潮
                if (ObjectUtils.isNotEmpty(booleanList) && booleanList.stream().filter(b -> b).collect(Collectors.toList()).size() > 0) {
                    // 是否已经刷完
                    Long maxDuration = maxDurationMap.get(room.getCode());
                    if (maxDuration < System.currentTimeMillis() + 1000) { // 多1秒缓冲
                        // 重置时间 ,和是否 正在 刷鱼
                        tideOfFishTimeList.set(i, System.currentTimeMillis() + (MyRefreshFishingUtil.getNextRefreshTime(fishRefreshRuleList.get(i), 0) * 1000));
                        booleanList.set(i, false);
                        tideOfFishTimeMap.put(room.getCode(), tideOfFishTimeList);
                        isTideOfFishMap.put(room.getCode(), booleanList);
                        log.info("下次刷鱼潮时间:{}", dateFormat.format(new Date(tideOfFishTimeList.get(i))));
//                            log.info("鱼潮结束：{}", dateFormat.format(new Date()));
                    }
                    break;
                }
                // 判断规则是否已经到刷鱼时间
                if (tideOfFishTimeList.get(i) > System.currentTimeMillis()) {
                    continue;
                }
                // 到了刷鱼时间
                // 1.向客户端发送鱼潮来了
                TtmyFishingChallengeMessage.FishingChallengeFishTideResponse.Builder builder = TtmyFishingChallengeMessage.FishingChallengeFishTideResponse.newBuilder();
                MyRefreshFishingUtil.sendRoomMessage(room, OseeMessage.OseeMsgCode.S_C_TTMY_FISHING_CHALLENGE_FISH_TIDE_RESPONSE_VALUE, builder);
                // log.info("鱼潮来了:{}",room.getCode());
                // 1.2 除了BOOS 其他鱼放走 放鱼
//                Map<Long, FishStruct> collect = room.getFishMap().entrySet().stream().filter((entry -> entry.getValue().isBossFlag())).
//                        collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
//                Map<Long, FishStruct> fishMap = room.getFishMap();
//                fishMap.clear(); // 清除所有的鱼
//                if (ObjectUtils.isNotEmpty(collect)) {
//                    fishMap.putAll(collect);
//                }
//                List<FishStruct> fishStructList = collect.entrySet().stream().map(Map.Entry::getValue).collect(Collectors.toList());
//                Map<FishStruct, List<Integer>> roomFishForbiddenTypes = new ConcurrentHashMap<>();
//                Map<FishStruct, Long> roomMaxTypesFishForbiddenTime = new ConcurrentHashMap<>();
//                for (FishStruct struct : fishStructList) {
//                    if (ObjectUtils.isNotEmpty(roomFishForbiddenTypes.get(struct)) && ObjectUtils.isNotEmpty(roomMaxTypesFishForbiddenTime.get(struct))) {
//                        roomFishForbiddenTypes.put(struct, room.getRoomFishForbiddenTypes().get(struct));
//                        roomMaxTypesFishForbiddenTime.put(struct, room.getRoomMaxTypesFishForbiddenTime().get(struct));
//                    }
//                }
//                room.setRoomFishForbiddenTypes(roomFishForbiddenTypes);
//                room.setRoomMaxTypesFishForbiddenTime(roomMaxTypesFishForbiddenTime);
//                    log.info("除了BOOS 其他鱼放走 当前鱼集合：{}", room.getFishMap());
                // 2.开始刷鱼
                // 2.1拿到鱼潮的鱼组
                FishGroupConfig fishGroupConfig = MyRefreshFishingUtil.getRandomFishGroupConfigByFishRefreshRule(fishRefreshRuleList.get(i), null);
                long ruleId = fishRefreshRuleList.get(i).getId(); // 获取中间表id
                brushFishTide(room, fishGroupConfig, ruleId);  //刷鱼潮
                // 2.3 设置房间正在刷鱼
                booleanList.set(i, true);
                // 2.4 设置房间刷鱼时间
                Long maxDuration = System.currentTimeMillis() + getMaxDuration(fishGroupConfig.getFishSchoolDelayList(), fishGroupConfig.getRouteList()) * 1000;
                maxDurationMap.put(room.getCode(), maxDuration);
//                    log.info("鱼潮结束时间：{}", dateFormat.format(new Date(maxDuration)));
                return;
            }
//            }
        } //鱼潮
        long currentTimeMillis = System.currentTimeMillis();
        boolean hasBugleFish = room.getBugleFishNumber() > 0;
//        if (ObjectUtils.isEmpty(booleanList) || booleanList.stream().filter(b -> b).collect(Collectors.toList()).size() == 0) { // 鱼潮不能刷其他的鱼
        // 刷新 普通鱼
        for (Map.Entry<FishRefreshRule, Long> refreshEntry : room.getNextRefreshTime().entrySet()) {

            FishRefreshRule key = refreshEntry.getKey();
//            log.info("刷新 普通鱼:{}",key);
            if (key.getUseBossBugleInvalid() == 0 && hasBugleFish) {
                continue;
            }

            if (!key.isDynamicRefresh() && key.getFishCount() > 0) {

                if (key.getDurationTemp() > 0) {
                    key.setDurationTemp(key.getDurationTemp() - 1); // 持续时间减少
                }

                // 如果还存在该规则的鱼，则刷新：下次刷新时间
                refreshEntry.setValue(MyRefreshFishingUtil.getNextRefreshTime(key, room.getRoomTick()));

                // if (PRINT_LOG_FUNCTION.apply(key.getId())) {
                // log.info("新刷新规则：刷新普通鱼-1，ruleId：{}，持续时间减少为：{}，剩余刷新时间：{}，roomCode：{}", key.getId(),
                // key.getDurationTemp(), (refreshEntry.getValue() - room.getRoomTick()), room.getCode());
                // }

                continue;

            }

            // if (PRINT_LOG_FUNCTION.apply(key.getId())) {
            // log.info("新刷新规则：刷新普通鱼-2，ruleId：{}，剩余刷新时间：{}，roomCode：{}", key.getId(),
            // (refreshEntry.getValue() - room.getRoomTick()), room.getCode());
            // }

            if (key.isDynamicRefresh()) { // 无此鱼且为动态刷新配置

                if (ObjectUtils.isNotEmpty(room.getFishMap()) && room.getFishMap().values().stream().anyMatch(fish -> fish.getRuleId() == key.getId())) {
                    continue;
                }

                refreshEntry.setValue(MyRefreshFishingUtil.getNextRefreshTime(key, room.getRoomTick()));

                long nextDynamicRefreshDelayTs = key.getNextDynamicRefreshDelayTs();

                if (currentTimeMillis > nextDynamicRefreshDelayTs) {

                    refreshGroupFish(room, key.getId(), false);

                }

                // if (PRINT_LOG_FUNCTION.apply(key.getId())) {
                // log.info("刷新时间到-2：{}，roomCode：{}", key.getId(), room.getCode());
                // }

            } else if (refreshEntry.getValue() <= room.getRoomTick()) { // 刷新时间到

                if (!key.isDynamicRefresh()) {

                    key.setDurationTemp(key.getDuration()); // 持续时间重置
                    key.setFishCount(1);

                    // if (PRINT_LOG_FUNCTION.apply(key.getId())) {
                    // log.info("新刷新规则：普通鱼刷新时间到，ruleId：{}，持续时间重置为：{}，roomCode：{}", key.getId(), key.getDuration(),
                    // room.getCode());
                    // }

                }

                refreshEntry.setValue(MyRefreshFishingUtil.getNextRefreshTime(key, room.getRoomTick()));

                // if (PRINT_LOG_FUNCTION.apply(key.getId())) {
                // log.info("刷新时间到-1：{}，roomCode：{}", key.getId(), room.getCode());
                // }

                refreshGroupFish(room, key.getId(), false);

            }

        }
//        }

        // 刷 boss
        for (FishRefreshRule bossRefreshRule : room.getBossRefreshRuleList()) {

            if (bossRefreshRule.getUseBossBugleInvalid() == 0 && hasBugleFish) {
                continue;
            }

            if (room.getBoss() > 0) {

                if (bossRefreshRule.getDurationTemp() > 0) {
                    bossRefreshRule.setDurationTemp(bossRefreshRule.getDurationTemp() - 1); // 持续时间减少
                }

                // 如果存在 boss，则重新计算下次刷新时间
                bossRefreshRule.setNextRefreshTime(MyRefreshFishingUtil.getNextRefreshTime(bossRefreshRule, room.getRoomTick()));

                // log.info("新刷新规则：boss，持续时间减少为：{}，剩余刷新时间：{}， 当前房间boss数量：{}，roomCode：{}",
                // bossRefreshRule.getDurationTemp(), (bossRefreshRule.getNextRefreshTime() - room.getRoomTick()),
                // room.getBoss(), room.getCode());

                continue;

            }

            // log.info("新刷新规则：刷新boss，剩余刷新时间：{}，roomCode：{}", (bossRefreshRule.getNextRefreshTime() -
            // room.getRoomTick()),
            // room.getCode());

            if (bossRefreshRule.getNextRefreshTime() <= room.getRoomTick()) { // 刷新时间到

                room.setBoss(1);
                bossRefreshRule.setDurationTemp(bossRefreshRule.getDuration()); // 持续时间重置

                // log.info("新刷新规则：boss，刷新时间到，当前持续时间为：{}，当前房间boss数量：{}，roomCode：{}", bossRefreshRule.getDuration(),
                // room.getBoss(), room.getCode());

                bossRefreshRule.setNextRefreshTime(MyRefreshFishingUtil.getNextRefreshTime(bossRefreshRule, room.getRoomTick()));

                refreshFish(room, Collections.singletonList(room.getNextRefreshBossFishId()), room.getNextRefreshBossRouteConfig(), bossRefreshRule.getId(),
                        false, false, false, null, null);

                room.setNextRefreshBossFishId(0);
                room.setNextRefreshBossRouteConfig(null);
                room.setNextRefreshBossTime(0);

            }

            // 如果 boss还有 x秒刷新，则给前端发送：背景同步
            if (bossRefreshRule.getNextRefreshTime() - room.getRoomTick() <= MyTimeConstant.BACKGROUND_SYNC_TIME && room.getNextRefreshBossFishId() == 0) {

                FishGroupConfig fishGroupConfig = MyRefreshFishingUtil.getRandomFishGroupConfigByFishRefreshRule(bossRefreshRule, null);

                room.setNextRefreshBossFishId(MyRefreshFishingUtil.getRandomFishIdByGroupConfig(fishGroupConfig));
                room.setNextRefreshBossRouteConfig(MyRefreshFishingUtil.getRandomRouteByGroupConfig(fishGroupConfig, null));
                room.setNextRefreshBossTime(bossRefreshRule.getNextRefreshTime());

                backgroundSync(null, room, room.getNextRefreshBossFishId(), (room.getRoomTick() - bossRefreshRule.getNextRefreshTime()) * 1000);

            }

        }

        for (Map.Entry<Long, CopyOnWriteArraySet<Integer>> item : REFRESH_TIME_RULE_ID_AND_ROOM_CODE_MAP.entrySet()) {

            if (!item.getValue().contains(room.getCode())) {
                continue;
            }

            Long ruleId = item.getKey();

            FishRefreshRule fishRefreshRule = DataContainer.getData(ruleId, FishRefreshRule.class);

            if (fishRefreshRule == null) {
                continue;
            }

            List<String> refreshTimeList = fishRefreshRule.getRefreshTimeList();

            if (CollUtil.isEmpty(refreshTimeList)) {
                continue;
            }

            for (int i = 0; i < refreshTimeList.size(); i++) {

                String refreshTimeStr = refreshTimeList.get(i);

                DateTime timeToday = DateUtil.parseTimeToday(refreshTimeStr);

                long timeTodayTime = timeToday.getTime();

                boolean flag = currentTimeMillis > timeTodayTime && currentTimeMillis < timeTodayTime + 20 * 60 * 1000;

                if (!flag) {

                    continue;

                }

                FishGroupConfig fishGroupConfig = MyRefreshFishingUtil.getRandomFishGroupConfigByFishRefreshRule(fishRefreshRule, null);

                if (fishGroupConfig == null) {
                    break;
                }

                List<Long> fishIdList = fishGroupConfig.getFishIdList();

                if (CollUtil.isEmpty(fishIdList)) {
                    break;
                }

                Long fishId = fishIdList.get(0);

                FishConfig firstFishConfig = DataContainer.getData(fishId, FishConfig.class);

                if (firstFishConfig == null) {
                    break;
                }

                RMap<Long, Long> fixedLastRefreshTimeRuleIdMap = getFixedLastRefreshTimeRuleIdMap();

                fishRefreshRule.setFixedLastRefreshTime(timeTodayTime);

                fixedLastRefreshTimeRuleIdMap.put(ruleId, timeTodayTime);

                // 如果：该房间存在该鱼
                boolean anyMatch = ObjectUtils.isNotEmpty(room.getFishMap()) && room.getFishMap().values().stream().anyMatch(it -> it.getModelId() == firstFishConfig.getModelId());

                // boolean hasRealPlayer = FishingRobotChallengeUtil.getHasRealPlayer(room); // 房间内，是否有真实玩家

                // if (hasRealPlayer) {

                // log.info("是否刷开天魔猿：{}，roomCode：{}", !anyMatch, room.getCode());

                // }

                if (anyMatch) {
                    break;
                }

                // 执行：刷鱼
                refreshGroupFish(room, ruleId, false);

                break;

            }

        }

    }


    /**
     * 刷鱼潮
     *
     * @param room
     * @param fishGroupConfig
     * @param ruleId
     */
    private static void brushFishTide(NewBaseFishingRoom room, FishGroupConfig fishGroupConfig, Long ruleId) {
        List<List<Long>> fishSchoolIdLists = fishGroupConfig.getFishSchoolIdList(); // 鱼种集合
        List<List<Double>> fishSchoolDelayLists = fishGroupConfig.getFishSchoolDelayList(); // 刷新延迟
        List<RouteConfig> routeList = fishGroupConfig.getRouteList(); // 路经集合
//        long nowTime = System.currentTimeMillis();
//        double delay = 0;
//        for (int i = 0; i < fishSchoolDelayLists.size(); i++) {
//            List<Long> fishIdList = fishSchoolIdLists.get(i);
//            List<Double> delaylist = fishSchoolDelayLists.get(i);
//            RouteConfig routeConfig = routeList.get(i);
//            for (int j = 0; j < fishIdList.size(); j++) {
//                delay += delaylist.get(j);
//                ThreadPoolUtils.TIMER_SERVICE_POOL.schedule(() -> {
//                    refreshFish(room, fishIdList, routeConfig, ruleId, false, false, true, fishGroupConfig);
//                }, (int) (delay * 1000), TimeUnit.MILLISECONDS);
//            }
//            delay = 0;
//        }

        for (int i = 0; i < fishSchoolDelayLists.size(); i++) {
            List<Long> fishIdList = fishSchoolIdLists.get(i);
            List<Double> delayList = fishSchoolDelayLists.get(i);
            RouteConfig routeConfig = routeList.get(i);

            refreshFish(room, fishIdList, routeConfig, ruleId, false, false, false, fishGroupConfig, delayList);
        }
    }

    /**
     * 获取最大鱼潮持续时间
     *
     * @param fishSchoolDelayLists
     * @param routeList
     * @return
     */
    private static Long getMaxDuration(List<List<Double>> fishSchoolDelayLists, List<RouteConfig> routeList) {
//        List<Double> maxSumDelayList = getDoubles(fishSchoolDelayLists);
        long maxSumDoubles = getMaxSumDoubles(fishSchoolDelayLists);
        double maxDuration = Double.MIN_VALUE;
        for (RouteConfig route : routeList) {
            if (route.getTime() > maxDuration) {
                maxDuration = route.getTime();
            }
        }
//        for (Double v : maxSumDelayList) {
//            maxDuration += v;
//        }
        maxDuration += maxSumDoubles;
        return (long) maxDuration;
    }

    /**
     * 处理 List<List<Double>>  最大一列返回
     *
     * @param fishSchoolDelayLists
     * @return
     */
    @NotNull
    private static List<Double> getDoubles(List<List<Double>> fishSchoolDelayLists) {
        List<Double> maxSumDelayList = new ArrayList<>();
        double maxSum = Double.MIN_VALUE;
        for (List<Double> delayList : fishSchoolDelayLists) {
            double sum = 0.0;
            for (Double delay : delayList) {
                sum += delay;
            }
            if (sum > maxSum) {
                maxSum = sum;
                maxSumDelayList = delayList;
            }
        }
        return maxSumDelayList;
    }

    /**
     * 返回 最大延迟
     *
     * @param fishSchoolDelayLists 刷新延迟
     * @return
     */
    @NotNull
    private static long getMaxSumDoubles(List<List<Double>> fishSchoolDelayLists) {
        double maxSum = Double.MIN_VALUE;
        for (List<Double> delayList : fishSchoolDelayLists) {
            double sum = 0.0;
            for (Double delay : delayList) {
                sum += delay;
            }
            if (sum > maxSum) {
                maxSum = sum;
            }
        }
        return (long) maxSum;
    }

    /**
     * 返回 最大轨迹时长 单位秒
     *
     * @param groupConfig 传入组表
     * @return maxTrajectoryTime
     */
    private static long gatMaxTrajectoryDurationTime(FishGroupConfig groupConfig) {
        List<RouteConfig> routeList = groupConfig.getRouteList();
        double maxTrajectoryTime = Double.MIN_VALUE;
        for (RouteConfig routeConfig : routeList) {
            double time = routeConfig.getTime(); // 秒
            if (maxTrajectoryTime < time) {
                maxTrajectoryTime = time;
            }
        }
        return (long) maxTrajectoryTime;
    }

    private static long gatMaxTrajectoryDurationTime(RouteConfig routeConfig) {
        double maxTrajectoryTime = routeConfig.getTime();
        return (long) maxTrajectoryTime;
    }

    // key：ruleId，value：上一次的刷新时间戳
    public static final String FIXED_LAST_REFRESH_TIME_RULE_ID_MAP = "FIXED_LAST_REFRESH_TIME_RULE_ID_MAP";

    public static RMap<Long, Long> getFixedLastRefreshTimeRuleIdMap() {

        return redissonClient.getMap(FIXED_LAST_REFRESH_TIME_RULE_ID_MAP);

    }

    // key：modelId，value：上一次的悬红榜清除时间戳
    public static final String FIXED_LAST_XUAN_HONG_BANG_TIME_MODEL_ID_MAP = "FIXED_LAST_XUAN_HONG_BANG_TIME_MODEL_ID_MAP";

    public static RMap<Integer, Long> getFixedLastXuanHongBangTimeModelIdMap() {

        return redissonClient.getMap(FIXED_LAST_XUAN_HONG_BANG_TIME_MODEL_ID_MAP);

    }

    /**
     * 刷新随机的一群鱼
     */
    public static void refreshGroupFish(NewBaseFishingRoom gameRoom, long ruleId, boolean bossBulge) {

        FishRefreshRule fishRefreshRule = DataContainer.getData(ruleId, FishRefreshRule.class);

        if (fishRefreshRule == null) {
            return;
        }

        FishGroupConfig fishGroupConfig = MyRefreshFishingUtil.getRandomFishGroupConfigByFishRefreshRule(fishRefreshRule, null);

        // log.info("fishGroupConfig.getId()：{}", fishGroupConfig.getId());

        refreshGroupFish(gameRoom, ruleId, fishGroupConfig, bossBulge);

    }

    /**
     * 刷新指定的一群鱼
     */
    private static void refreshGroupFish(NewBaseFishingRoom gameRoom, long ruleId, FishGroupConfig groupConfig, boolean bossBulge) {

        if (groupConfig == null) {
            return;
        }

        double delay = 0;
        List<Long> fishIds = new LinkedList<>();
        RouteConfig routeConfig = groupConfig.getRandomRoute();

        if (groupConfig.getGroupType() == 3 || groupConfig.getGroupType() == 4) { // 一起刷鱼

            refreshFish(gameRoom, groupConfig.getFishIdList(), groupConfig.getRouteList().get(0), ruleId,
                    bossBulge, false, true, groupConfig, null);

            return;

        }

        // 鱼组和路线一一对应的话
        if (groupConfig.getRouteList().size() == groupConfig.getFishIdList().size()) {

            for (int i = 0; i < groupConfig.getFishIdList().size(); i++) {

                refreshFishWithDelay(gameRoom, Collections.singletonList(groupConfig.getFishIdList().get(i)), groupConfig.getRouteList().get(i), ruleId, delay, bossBulge, false, false, null);

            }

            return;

        }

        if (StringUtils.isEmpty(groupConfig.getDelayList())) { // 未设置延迟，默认同时刷出

            fishIds.addAll(groupConfig.getFishIdList());

        } else { // 设置延迟则分组刷新鱼

            fishIds.add(groupConfig.getFishIdList().get(0));

            for (int i = 1; i < groupConfig.getFishIdList().size(); i++) {

                Double realDelay = groupConfig.getDelayList().size() > i - 1 ? groupConfig.getDelayList().get(i - 1) : groupConfig.getDelayList().get(0);

                if (realDelay > 0) {

                    refreshFishWithDelay(gameRoom, fishIds, routeConfig, ruleId, delay, bossBulge, false, false, null);

                    delay += realDelay;

                    fishIds.clear();

                }

                fishIds.add(groupConfig.getFishIdList().get(i));

            }

        }

        refreshFishWithDelay(gameRoom, fishIds, routeConfig, ruleId, delay, bossBulge, false, false, null);

    }

    /**
     * 延迟：执行刷鱼
     */
    public static void refreshFishWithDelay(NewBaseFishingRoom gameRoom, List<Long> fishIdList, RouteConfig routeConfig, long ruleId,
                                            double delay, boolean bossBulge, boolean durationRefreshFlag,
                                            boolean groupRefreshFlag, @Nullable FishGroupConfig groupConfig) {

        ThreadPoolUtils.TASK_SERVICE_POOL.schedule(() -> refreshFish(gameRoom, fishIdList, routeConfig, ruleId, bossBulge,
                durationRefreshFlag, groupRefreshFlag, groupConfig, null), (int) (delay * 1000), TimeUnit.MILLISECONDS);

    }

    // 定时刷鱼时间集合，即：会在那个时间点刷出该鱼，例如：[07:00,15:00]，备注：24小时值，key：ruleId，value：roomCodeSet
    public static final Map<Long, CopyOnWriteArraySet<Integer>> REFRESH_TIME_RULE_ID_AND_ROOM_CODE_MAP = new ConcurrentHashMap<>();

    // 可以出现世界 boss的房间 roomIndex
    public static List<Integer> WORLD_BOSS_ROOM_INDEX_LIST = MyRefreshFishingUtil.CHALLENGE_FISHING_ROOM_INDEX_LIST;

    /**
     * 执行刷鱼
     *
     * @param groupRefreshFlag 是否是一起刷的鱼
     * @param delayList        是用于鱼潮的延迟
     */
    private static void refreshFish(NewBaseFishingRoom room, List<Long> fishIdList, RouteConfig routeConfig, long ruleId, boolean bossBulge,
                                    boolean durationRefreshFlag, boolean groupRefreshFlag, @Nullable FishGroupConfig groupConfig, List<Double> delayList) {

        long nowTime = System.currentTimeMillis();

        FishRefreshRule rule = DataContainer.getData(ruleId, FishRefreshRule.class);
        GeneratedMessage.Builder<?> messageBuilder = room.getRefreshFishMessageBuilder();

        FishCcxxConfig fishCcxxConfig = MyRefreshFishingUtil.FISHING_CCXX_CONFIG_MAP.get(room.getRoomIndex());

        int frozenType; // 0 全屏（默认） 1 随机

        if (fishCcxxConfig == null) {

            frozenType = 0;

        } else {

            frozenType = fishCcxxConfig.getFrozenType();

        }

        // 是否是：世界 boss
        boolean worldBossFlag = rule.isWorldBossFlag();

        // 本次：刷新的房间集合，备注：只有是：世界 boss时才有值
        List<NewBaseFishingRoom> refreshRoomList = new ArrayList<>();

        // 本次刷的鱼的集合
        List<FishStruct> fishStructList = new ArrayList<>();
        // 鱼潮延迟
        Double delay;
        long delayTime = new Date().getTime();
        // 最大轨迹时长 时间挫
        long maxTrajectoryTime = 0;
        for (int i = 0; i < fishIdList.size(); i++) {
            Long fishId = fishIdList.get(i);
            FishConfig fishConfig = DataContainer.getData(fishId, FishConfig.class);
            if (fishConfig == null) {
                log.info("不存在的 fishId：{}，ruleId：{}", fishId, ruleId);
                return;
            }

            FishStruct fishStruct = getFishStruct(room, routeConfig, ruleId, groupRefreshFlag, worldBossFlag, fishConfig, nowTime);
            fishStruct.setBossFlag(rule.isBoss());
            fishStruct.setFishType(fishConfig.getFishType());
            fishStruct.setFishType2(fishConfig.getFishType2());
            fishStruct.setWorldBossFlag(worldBossFlag); // 设置：是否是世界 boss
            fishStruct.setMonsterHpDeductType(fishConfig.getMonsterHpDeductType()); // 设置：怪物血量扣除方式
            fishStruct.setBossBulge(bossBulge); // 新刷新规则：设置是否是号角召唤的boss
            fishStruct.setDynamicRefresh(rule.isDynamicRefresh()); // 新刷新规则：设置是否动态刷新鱼
            fishStruct.setDurationRefreshFlag(durationRefreshFlag); // 新刷新规则：设置是否是持续刷鱼时间刷出
            fishStruct.setDelayTime(fishConfig.getDelayTime()); // 新刷新规则：持续刷新时间内的间隔时间
            fishStruct.setDurationDeathRefreshFlag(fishConfig.getDurationDeathRefreshFlag()); // 持续时间范围内死亡以后，是否继续刷鱼
            fishStruct.setBossBugleDelayTime(fishConfig.getBossBugleDelayTime()); // 使用boss号角的间隔时间
            fishStruct.setAddSurvivalTimeFlag(fishConfig.isAddSurvivalTimeFlag()); // 是否可以延长存活时间
            fishStruct.setFishRefreshType(rule.getType()); // 刷新类型：1 普通 2 神灯 3号角 4普通一号 5普通二号 6鱼潮

            if (ObjectUtils.isNotEmpty(groupConfig) && groupConfig.getGameId() == 6 && groupConfig.getGroupType() == 5) {  // 是鱼潮判断 ，当前为 机械迷城和 GroupType = 5 为 鱼群 鱼潮
                delay = delayList.get(i);  // 设置创建时间为延迟delay
                delayTime += (delay * 1000);
                fishStruct.setCreateTime(delayTime);
                fishStruct.setNowLifeTime(System.currentTimeMillis() - fishStruct.getCreateTime());
                fishStruct.setLifeTime(fishStruct.getClientLifeTime());
//                log.info("delay:{},delayTime:{} , fishStruct.setCreateTime：{}，fishStruct.LifeTime :{}",delay,delayTime, fishStruct.getCreateTime(), fishStruct.getLifeTime());
            }
            // 最大刷新时间
            RouteConfig routeConfig1 = new RouteConfig();
            FishGroupConfig fishGroupConfig1 = new FishGroupConfig();
            MyRefreshFishingUtil.findByRouteIdAndRule(fishStruct.getRouteId(), rule, routeConfig1, fishGroupConfig1);
            long maxTime;
            try {
                maxTime = fishStruct.getCreateTime() + gatMaxTrajectoryDurationTime(fishGroupConfig1) * 1000;
            } catch (Exception e) {
                try {
                    maxTime = fishStruct.getCreateTime() + gatMaxTrajectoryDurationTime(routeConfig) * 1000;
                } catch (Exception exception) {
                    maxTime = fishStruct.getCreateTime();
                }
            }
            if (maxTrajectoryTime < maxTime) {
                maxTrajectoryTime = maxTime - 1000;
            }
            // 记录鱼潮房间限制鱼
            room.getRoomFishForbiddenTypes().put(fishStruct, rule.getLimitRefreshRules());
            // 记录鱼潮房间限制鱼时间
            room.getRoomMaxTypesFishForbiddenTime().put(fishStruct, maxTrajectoryTime);

            if (bossBulge) {
                room.getFishMap().put(fishStruct.getId(), fishStruct);
//                room.putFishMap(fishStruct);
            }
            synchronized (room.getFishMap()) {
                // 如果是：全屏冰冻，并且是冰冻期间刷的鱼，则也冻住
                if (frozenType == 0 && fishStruct.isAddSurvivalTimeFlag()) {
                    long fTime = FishingManager.SKILL_FROZEN_TIME - (System.currentTimeMillis() - room.getLastRoomFrozenTime());
                    if (fTime > 0) {
                        fishStruct.setLifeTime(fishStruct.getLifeTime() + fTime); // 延长鱼的存在时间
                        fishStruct.setFTime(fTime);
                    }
                }
                if (!groupRefreshFlag) {
                    if (!putFishToRoomFishMap(room, worldBossFlag, fishStruct, refreshRoomList)) {
                        return;
                    }
                }
            }
            // 出奇制胜
            setChuQiZhiShengFlag(room, fishConfig, fishStruct);
            if (groupConfig != null && groupConfig.getShapeType() > 0) {
                fishStruct.setShapeId(groupConfig.getShapeType()); // 设置：鱼群的形状 id
            }
            // 奖券鱼
            setLottery(room, fishConfig, fishStruct);
            fishStruct.setGroupSeat(fishStructList.size());
            fishStructList.add(fishStruct); // 添加元素

            if (groupRefreshFlag) { // 如果是一起刷的鱼
                if (i == fishIdList.size() - 1) { // 如果现在是最后一条时
                    for (int j = 0; j < fishStructList.size(); j++) {

                        FishStruct item = fishStructList.get(j);

                        // if (SPECIAL_WORLD_BOSS_MODEL_ID_SET.contains(fishConfig.getModelId())) {
                        //
                        // log.info(
                        // "ruleId：{}，fishConfig.getId()：{}，routeConfig.getRouteId()：{}，roomCode：{}，fishId：{}",
                        // ruleId, item.getConfigId(), routeConfig.getRouteId(), room.getCode(), item.getId());
                        //
                        // }

                        // 如果是：一起刷的鱼，并且是世界 boss时，处理：第一条鱼
                        handleGroupRefreshFlagAndWorldBossFishStruct(item, rule);

                        item.setFishStructList(fishStructList); // 添加一个引用的集合，放到一起刷的鱼集合里面

                        // 这里：会标记已经刷过鱼的房间到：WORLD_BOSS_ROOM_MAP里
                        putFishToRoomFishMap(room, worldBossFlag, item, j == 0 ? refreshRoomList : null);

                    }

                }

                room.addFishInfos(fishStructList.get(0), messageBuilder); // 添加到 messageBuilder里

            } else {

                // 同步鱼
                room.addFishInfos(fishStruct, messageBuilder); // 添加到 messageBuilder里

            }
//             同步房间的鱼
//            synchronized (room.getFishMap()){
//                room.getFishMap().put(fishStruct.getId(), fishStruct);
//            }

            // Boss鱼刷新出来就延后鱼潮的刷新时间
            // 这样做就是防止鱼潮出来之后把boss赶跑了
            if (rule.isBoss()) {
                long nextFishTideTime = room.getNextFishTideTime();
                long roomTick = room.getRoomTick();
                float lifeTime = fishStruct.getLifeTime();
                if (roomTick + lifeTime >= nextFishTideTime) {
                    // Boss消失时间在鱼潮之后就要将鱼潮延时，避免赶走boss
                    nextFishTideTime += (long) (roomTick + lifeTime - nextFishTideTime + 30);
                    room.setNextFishTideTime(nextFishTideTime);
                }
            }

            if (FishingChallengeFightFishUtil.DYNAMIC_MULT_FISH_MODEL_ID_LIST.contains(fishConfig.getModelId())) { // 刷新了一条特殊鱼

                final RMap<Integer, Object> rMap = RedisHelper.redissonClient.getMap(String.format(DYNAMIC_MULT_FISH_ROOM_CODE, room.getCode()), StringCodec.INSTANCE);
                rMap.putIfAbsent(fishConfig.getModelId(), 300);

                // 同步
                sendRoomFishMult(room, fishConfig, fishStruct, Convert.toInt(rMap.get(fishConfig.getModelId())));

            }
        }
//        // 禁用鱼逻辑
//        for (Integer limitRefreshRule : rule.getLimitRefreshRules()) { //获取 禁用集合
//            Long roomMaxTypesFishForbiddenTime = room.getRoomMaxTypesFishForbiddenTime().get(limitRefreshRule);
//            if (ObjectUtils.isNotEmpty(roomMaxTypesFishForbiddenTime) && maxTrajectoryTime < roomMaxTypesFishForbiddenTime) {
//                room.getRoomMaxTypesFishForbiddenTime().put(limitRefreshRule, roomMaxTypesFishForbiddenTime);
//                continue;
//            }
//            room.getRoomMaxTypesFishForbiddenTime().put(limitRefreshRule, maxTrajectoryTime);
//        }

        if (worldBossFlag) {

            // if (ruleId == 841) {

            // log.info("刷鱼，房间长度：{}", refreshRoomList.size());

            // }

            for (NewBaseFishingRoom realRoom : refreshRoomList) {

                MyRefreshFishingUtil.sendRoomMessage(realRoom, realRoom.getRefreshFishResponseValue(), messageBuilder);

            }

        } else {

            // if (ruleId == 841) {

            // log.info("刷鱼");

            // }

            MyRefreshFishingUtil.sendRoomMessage(room, room.getRefreshFishResponseValue(), messageBuilder);

        }

        room.setNoFishTick(0);

    }

    /**
     * 奖券鱼：直接生成倍数
     */
    private static void setLottery(NewBaseFishingRoom room, FishConfig fishConfig, FishStruct fishStruct) {

        if (FishingChallengeFightFishUtil.CHECK_LOTTERY_FUNC.apply(fishConfig.getModelId())) {
            JiangQuan jiangQuan = new JiangQuan(fishStruct, fishConfig);
            long randomMoney = jiangQuan.computeMultiple(jiangQuan.getKey()); // 获取：鱼的倍数
            fishStruct.setLottery(randomMoney);

        }

    }

    /**
     * 获取：FishStruct对象
     */
    @NotNull
    public static FishStruct getFishStruct(NewBaseFishingRoom room, @Nullable RouteConfig routeConfig, @Nullable Long ruleId,
                                           boolean groupRefreshFlag, boolean worldBossFlag, FishConfig fishConfig, long nowTime) {

        long fishStructId;

        if (worldBossFlag) { // 如果是：世界 boss
            fishStructId = -fishConfig.getId();
        } else {
            fishStructId = room.getNextId();
        }

        int maxSafe = fishConfig.getMaxSafe() == 0 ? fishConfig.getMinSafe() + 1 : fishConfig.getMaxSafe();
        int safeTimes = ThreadLocalRandom.current().nextInt(fishConfig.getMinSafe(), maxSafe);

        // 生成一条鱼
        FishStruct fishStruct = new FishStruct();

        fishStruct.setId(fishStructId);
        fishStruct.setModelId(fishConfig.getModelId());

        if (ruleId != null) {
            fishStruct.setRuleId(ruleId);
        }

        fishStruct.setConfigId(fishConfig.getId());

        if (routeConfig != null) {

            fishStruct.setLifeTime(routeConfig.getTime() * 1000);
            fishStruct.setLifeOrigTime(routeConfig.getTime());

            fishStruct.setRouteId(routeConfig.getRouteId());

        }

        fishStruct.setSafeTimes(safeTimes);

        if (room.getRoomTick() <= 1 && !groupRefreshFlag && routeConfig != null) { // 如果是才进房间，根据轨迹时间，随机取一个范围

            long randomLong = RandomUtil.randomLong(Convert.toLong((routeConfig.getTime() * 100)), Convert.toLong((routeConfig.getTime() * 810)));

            fishStruct.setCreateTime(nowTime - randomLong);

        } else {
            fishStruct.setCreateTime(nowTime);
        }

        if (StrUtil.isNotBlank(fishConfig.getMonsterHpStr())) {

            // if (PRINT_LOG_FUNCTION.apply(ruleId)) {
            // System.out.println();
            // }

            fishStruct.setMonsterHp(fishConfig.getMonsterMaxHp());
            fishStruct.setMonsterMaxHp(fishConfig.getMonsterMaxHp());
            fishStruct.setMonsterHpCount(fishConfig.getMonsterHpCount());

            fishStruct.setPlayerAttackScopeList(fishConfig.getPlayerAttackScopeList());
            fishStruct.setMonsterAttackScopeList(fishConfig.getMonsterAttackScopeList());
            fishStruct.setMonsterAttackTimeList(fishConfig.getMonsterAttackTimeList());

        }


        return fishStruct;

    }

    /**
     * 如果是：一起刷的鱼，并且是世界 boss时，处理：第一条鱼
     */
    private static void handleGroupRefreshFlagAndWorldBossFishStruct(FishStruct fishStruct, FishRefreshRule rule) {

        if (fishStruct.isWorldBossFlag()) {

            if (rule.getFixedLastRefreshTime() != 0) {

                fishStruct.setCreateTime(rule.getFixedLastRefreshTime()); // 防止：存活时间过长

                // long maxLifeTime = Math.round(
                // fishStruct.getLifeTime() > 0 ? fishStruct.getLifeTime() : FishingManager.DEFAULT_LIFE_TIME * 1000);

                // log.info("modelId：{}，剩余存活时间：{}", fishStruct.getModelId(),
                // DateUtil.formatBetween(
                // maxLifeTime - (System.currentTimeMillis() - fishStruct.getCreateTime())));

            }

        }

    }

    /**
     * 往房间的 fishMap里面添加鱼
     *
     * @return true 添加成功 false 添加失败
     */
    public static boolean putFishToRoomFishMap(NewBaseFishingRoom room, boolean worldBossFlag, FishStruct fishStruct, @Nullable List<NewBaseFishingRoom> refreshRoomList) {

        if (worldBossFlag) {

            putFishToCurrentRoomFishMap(fishStruct, refreshRoomList, room);

        } else {

            room.getFishMap().put(fishStruct.getId(), fishStruct);
//            room.putFishMap(fishStruct);  // 打桩

        }

        return true;

    }

    /**
     * 只给当前房间，添加：这条鱼
     */
    public static void putFishToCurrentRoomFishMap(FishStruct fishStruct, @Nullable List<NewBaseFishingRoom> refreshRoomList, NewBaseFishingRoom room) {

        room.getFishMap().put(fishStruct.getId(), fishStruct);
//        room.putFishMap(fishStruct); // 打桩

        // log.info("刷鱼-1：{}", fishStruct.getModelId());

        if (refreshRoomList != null) {

            refreshRoomList.add(room);

        }

    }

    /**
     * 设置：出奇制胜
     */
    public static void setChuQiZhiShengFlag(NewBaseFishingRoom gameRoom, FishConfig fishConfig, FishStruct fishStruct) {

        if (fishConfig.getModelId() >= 9 && fishConfig.getModelId() <= 15) {

            // 百分之 25%的概率，是出奇制胜
            // 满足条件才可以出现：1. 不能有两只出奇制胜的鱼 2. 并且同一个 modelId，不能出现两只出奇制胜的鱼
            int randomInt = RandomUtil.randomInt(4);

            if (randomInt == 0) {

                synchronized (gameRoom) {

                    List<Integer> modelIdList = gameRoom.getFishMap().values().stream().filter(FishStruct::isChuQiZhiShengFlag).map(FishStruct::getModelId).collect(Collectors.toList());

                    if (modelIdList.size() <= 1 && !modelIdList.contains(fishConfig.getModelId())) {

                        fishStruct.setChuQiZhiShengFlag(true);

                    }

                }

            }

        }

    }

    /**
     * 设置并同步 特殊鱼倍数
     */
    public static void sendRoomFishMult(NewBaseFishingRoom gameRoom) {

        gameRoom.getFishMap().forEach((k, v) -> {

            FishConfig config = DataContainer.getData(v.getConfigId(), FishConfig.class);

            if (FishingChallengeFightFishUtil.DYNAMIC_MULT_FISH_MODEL_ID_LIST.contains(config.getModelId())) { // 刷新了一条特殊鱼

                final RMap<Integer, Object> rMap = RedisHelper.redissonClient.getMap(String.format(DYNAMIC_MULT_FISH_ROOM_CODE, gameRoom.getCode()), StringCodec.INSTANCE);

                // 同步
                sendRoomFishMult(gameRoom, config, v, Convert.toInt(rMap.getOrDefault(config.getModelId(), 300)));

            }

        });

    }

    /**
     * 同步 特殊鱼倍数
     */
    public static void sendRoomFishMult(NewBaseFishingRoom gameRoom, FishConfig config, FishStruct fish, long mult) {

        TtmyFishingChallengeMessage.FishingChallengeRoomFishMultipleResponse.Builder builder = TtmyFishingChallengeMessage.FishingChallengeRoomFishMultipleResponse.newBuilder();

        builder.setDatetime(System.currentTimeMillis());
        builder.setFishName(config.getName());
        builder.setMult(mult);
        builder.setFishId(fish.getId());

        MyRefreshFishingUtil.sendRoomMessage(gameRoom, gameRoom.getFishMultipleResponseValue(), builder);

    }

    /**
     * 创建鱼数据结构：挑战场
     */
    public static TtmyFishingChallengeMessage.FishingChallengeFishInfoProto createFishInfoProtoForChallenge(FishStruct fishStruct, boolean deepFlag) {

        TtmyFishingChallengeMessage.FishingChallengeFishInfoProto.Builder builder = TtmyFishingChallengeMessage.FishingChallengeFishInfoProto.newBuilder();

        builder.setId(fishStruct.getId());
        builder.setFishId(fishStruct.getConfigId());
        builder.setRouteId(fishStruct.getRouteId());

        // if (fishStruct.getConfigId() == 11073) {
        //
        // log.info("客户端存活时间：{}", fishStruct.getClientLifeTime());
        //
        // }

        builder.setClientLifeTime(fishStruct.getClientLifeTime());

        builder.setDurationRefreshFlag(fishStruct.isDurationRefreshFlag());
        builder.setChuQiZhiShengFlag(fishStruct.isChuQiZhiShengFlag());

        builder.setCreateTime((long) fishStruct.getLifeOrigTime()); // 这里创建时间赋值为：轨迹时长

        builder.setLottery(fishStruct.getLottery()); // 设置：奖券数

        builder.setGroupSeat(fishStruct.getGroupSeat()); // 设置：一起刷鱼时的位置

        builder.setFishGropID(fishStruct.getShapeId()); // 设置：形状 id

        builder.setHp(fishStruct.getMonsterHp());
        builder.setMaxHp(fishStruct.getMonsterMaxHp());
        builder.setHpCount(fishStruct.getMonsterHpCount());
        // 添加 刷新类型 type
        FishRefreshRule rule = DataContainer.getData(fishStruct.getRuleId(), FishRefreshRule.class);
        if (ObjectUtils.isNotEmpty(rule) && ObjectUtils.isNotEmpty(rule.getType())) {
            builder.setFishRefreshType(rule.getType());
        }
        if (fishStruct.getMonsterAttackTime() > 0) {

            builder.setMonsterAttackTime(fishStruct.getMonsterAttackTime());

            builder.setMonsterAttackTimeType(fishStruct.getMonsterAttackTimeList().indexOf(fishStruct.getMonsterAttackTime()) + 1);

            long finishMs = System.currentTimeMillis() - fishStruct.getMonsterLastAttackTs(); // 已经攻击了多久

            if (finishMs < 0 || finishMs < fishStruct.getAttackFinishMs()) {

                builder.setMonsterAttackRemainTime((int) (fishStruct.getMonsterAttackTime() - finishMs)); // 设置：还剩多久攻击时间

                builder.setHitPlayerValue(fishStruct.getHitPlayerValue());
                builder.setHitPlayerId(fishStruct.getHitPlayerId());

            } else {

                if (finishMs < fishStruct.getMonsterAttackTime()) { // 如果：还在攻击中

                    builder.setMonsterAttackRemainTime((int) (fishStruct.getMonsterAttackTime() - finishMs)); // 设置：还剩多久攻击时间

                    builder.setHitPlayerValue(fishStruct.getHitPlayerValue());
                    builder.setHitPlayerId(fishStruct.getHitPlayerId());

                }

            }

        }

        if (deepFlag) {

            // if (fishStruct.getId() < 0) {
            //
            // log.info("fishId：{}，modelId：{}，clientLifeTime：{}，ruleId：{}", fishStruct.getId(),
            // fishStruct.getModelId(), fishStruct.getClientLifeTime(), fishStruct.getRuleId());
            //
            // }

            if (CollUtil.isNotEmpty(fishStruct.getFishStructList())) {

                for (FishStruct item : fishStruct.getFishStructList()) {

                    builder.addFishGroup(createFishInfoProtoForChallenge(item, false)); // 设置：跟着刷的鱼，备注，包含自己

                }

            }

        }

        return builder.build();

    }

    /**
     * 创建鱼数据结构：大奖赛
     */
    public static TtmyFishingGrandPrixMessage.FishingGrandPrixFishInfoMessage createFishInfoProtoForGrandPrix(FishStruct struct) {

        TtmyFishingGrandPrixMessage.FishingGrandPrixFishInfoMessage.Builder builder = TtmyFishingGrandPrixMessage.FishingGrandPrixFishInfoMessage.newBuilder();

        builder.setId(struct.getId());
        builder.setFishId(struct.getConfigId());
        builder.setRouteId(struct.getRouteId());
        builder.setClientLifeTime(struct.getClientLifeTime());
        builder.setDurationRefreshFlag(struct.isDurationRefreshFlag());

        builder.setCreateTime((long) struct.getLifeOrigTime()); // 这里创建时间赋值为：轨迹时长

        return builder.build();

    }

    /**
     * 创建鱼数据结构：普通场
     */
    public static OseeFishingMessage.FishingFishInfoProto createFishInfoProtoForGeneral(FishStruct struct) {

        OseeFishingMessage.FishingFishInfoProto.Builder builder = OseeFishingMessage.FishingFishInfoProto.newBuilder();

        builder.setId(struct.getId());
        builder.setFishId(struct.getConfigId());
        builder.setRouteId(struct.getRouteId());
        builder.setClientLifeTime(struct.getClientLifeTime());
        builder.setDurationRefreshFlag(struct.isDurationRefreshFlag());

        builder.setCreateTime((long) struct.getLifeOrigTime()); // 这里创建时间赋值为：轨迹时长

        return builder.build();

    }

    /**
     * 背景同步
     */
    public static void backgroundSync(BaseGamePlayer player, NewBaseFishingRoom gameRoom, Long fishId, Long time) {

        OseePublicData.BackgroundSyncResponse.Builder builder = OseePublicData.BackgroundSyncResponse.newBuilder();

        if (fishId == null) {

            // 获取：房间内即将刷新的boss情况
            if (gameRoom.getNextRefreshBossFishId() != 0) {

                builder.setFishId(gameRoom.getNextRefreshBossFishId());
                builder.setTime((gameRoom.getRoomTick() - gameRoom.getNextRefreshBossTime()) * 1000);
                // log.info("gameRoom.getNextRefreshBossFishId() != 0");

            } else {

                // 获取：房间内boss的存在情况
                gameRoom.getFishMap().values().stream().filter(it -> {

                    FishRefreshRule fishRefreshRule = DataContainer.getData(it.getRuleId(), FishRefreshRule.class);
                    return fishRefreshRule != null && fishRefreshRule.isBoss();

                }).findFirst().ifPresent(it -> {

                    builder.setFishId(it.getConfigId());
                    builder.setTime(0);

                });

                if (builder.getFishId() == 0) {

                    if (gameRoom.getLastBossRemoveTime() == 0) {
                        return; // 如果房间内不存在被移除 boss
                    }

                    // 如果存在被移除 boss
                    builder.setFishId(gameRoom.getLastBossRemoveFishId());
                    builder.setTime(System.currentTimeMillis() - gameRoom.getLastBossRemoveTime());

                }

            }

        } else {

            builder.setFishId(fishId);
            builder.setTime(time);

        }

        // log.info("背景同步，执行：builder.getFishId()：{}，builder.getTime()：{}", builder.getFishId(), builder.getTime());

        if (player != null) {

            NetManager.sendMessage(OseeMessage.OseeMsgCode.S_C_TTMY_BACKGROUND_SYNC_RESPONSE_VALUE, builder, player.getUser());

        } else {

            for (BaseGamePlayer gamePlayer : gameRoom.getGamePlayers()) {

                if (gamePlayer != null) {

                    NetManager.sendMessage(OseeMessage.OseeMsgCode.S_C_TTMY_BACKGROUND_SYNC_RESPONSE_VALUE, builder, gamePlayer.getUser());

                }

            }

        }

    }

    /**
     * 新刷新规则：不是动态刷新的鱼，持续时间内，持续刷新鱼，并重新设置路径
     *
     * @return true 会动态刷一条鱼 false 不会动态刷
     */
    public static boolean checkAndDurationRefreshFish(NewBaseFishingRoom room, FishStruct fishStruct, boolean deathFlag) {

        if (fishStruct.isBossBulge()) {

            // log.info("新刷新规则：号角召唤的boss死亡或者逃跑刷新，现在房间 boss数量：{}", room.getBoss());

            // log.info("号角召唤的boss死亡或者逃跑刷新：{}", room.getCode());

            int delayTime = fishStruct.getBossBugleDelayTime();

            if (delayTime <= 0) {

                delayTime = (int) BOSS_BUGLE_COOL_TIME;

            } else {

                delayTime = delayTime * 1000;

            }

            if (deathFlag) {

                room.setCanUseBossBugleTime(System.currentTimeMillis() + delayTime);

                // log.info("delayTime：{}，roomCode：{}，下次可以使用时间：{}", delayTime, room.getCode(),
                // DateUtil.date(room.getCanUseBossBugleTime()));

            }

            room.setBugleFishNumber(0);

//             log.info("boss号角鱼{}：{}", deathFlag ? "死亡" : "逃跑", room.getCode());

            // 发送：持续刷的鱼，逃跑之后，是否还会再刷
            sendEscapeDurationRefreshResponse(room, false, !deathFlag, fishStruct.getConfigId());

            return false;

        }

        if (fishStruct.isDynamicRefresh()) {

            if (fishStruct.getDelayTime() > 0) {

                if (room.getNextRefreshTime() == null) {
                    return false;
                }

                CallBack<FishRefreshRule> fishRefreshRuleCallBack = new CallBack<>();

                room.getNextRefreshTime().entrySet().stream().filter(it -> it.getKey().getId() == fishStruct.getRuleId()).findFirst().ifPresent(it -> fishRefreshRuleCallBack.setValue(it.getKey()));

                FishRefreshRule fishRefreshRule = fishRefreshRuleCallBack.getValue();

                if (fishRefreshRule != null) {

                    fishRefreshRule.setNextDynamicRefreshDelayTs(System.currentTimeMillis() + (fishStruct.getDelayTime() * 1000L));

                }

            }

            return false;

        }

        String stateName = deathFlag ? "死亡" : "逃跑";

        FishRefreshRule rule = DataContainer.getData(fishStruct.getRuleId(), FishRefreshRule.class);

        if (rule == null) {
            return false;
        }

        // 房间里面的刷新规则
        AtomicReference<FishRefreshRule> fishRefreshRule = new AtomicReference<>(null);

        if (rule.isBoss()) {

            room.getBossRefreshRuleList().stream().filter(it -> it.getId() == fishStruct.getRuleId()).findFirst().ifPresent(fishRefreshRule::set);

        } else {

            room.getNextRefreshTime().entrySet().stream().filter(it -> it.getKey().getId() == fishStruct.getRuleId()).findFirst().ifPresent(it -> fishRefreshRule.set(it.getKey()));

        }

        if (fishRefreshRule.get() == null || fishRefreshRule.get().getDurationTemp() <= 0) {

            notDurationRefresh(room, fishStruct, stateName, rule, fishRefreshRule);

            // 发送：持续刷的鱼，逃跑之后，是否还会再刷
            sendEscapeDurationRefreshResponse(room, false, !deathFlag, fishStruct.getConfigId());

            return false;

        }

        // 如果是：死亡，并且 flag == 0，那么不刷鱼，并且：下一次的刷新时间，增加当前的剩余持续时间
        if (deathFlag && fishStruct.getDurationDeathRefreshFlag() == 0) {

            notDurationRefresh(room, fishStruct, stateName, rule, fishRefreshRule);

            fishRefreshRule.get().setNextRefreshTime(MyRefreshFishingUtil.getNextRefreshTime(rule, room.getRoomTick() + fishRefreshRule.get().getDurationTemp()));
            // log.info("持续时间内，死亡，并且标记不刷鱼，剩余持续时间：{}", fishRefreshRule.get().getDurationTemp());

            // 发送：持续刷的鱼，逃跑之后，是否还会再刷
            sendEscapeDurationRefreshResponse(room, false, false, fishStruct.getConfigId());

            return false;

        }

        if (rule.isBoss()) {

            room.setBoss(1);
            fishRefreshRule.get().setNextRefreshTime(MyRefreshFishingUtil.getNextRefreshTime(rule, room.getRoomTick()));

            // log.info("新刷新规则：boss死亡或者逃跑刷新，剩余持续时间：{}", fishRefreshRule.get().getDurationTemp());

        } else {

            fishRefreshRule.get().setFishCount(1);
            fishRefreshRule.get().setNextRefreshTime(MyRefreshFishingUtil.getNextRefreshTime(rule, room.getRoomTick()));

            // log.info("新刷新规则：普通鱼死亡或者逃跑刷新，剩余持续时间：{}", fishRefreshRule.get().getDurationTemp());

        }

        // 选择一个新的 路径，尽量和前面的不重复，并刷新鱼
        RouteConfig routeConfig = new RouteConfig();
        FishGroupConfig fishGroupConfig = new FishGroupConfig();
        MyRefreshFishingUtil.findByRouteIdAndRule(fishStruct.getRouteId(), rule, routeConfig, fishGroupConfig);

        if (CollUtil.isEmpty(fishGroupConfig.getRouteList())) {

            // Assert.notEmpty(fishGroupConfig.getRouteList(), "未找到轨迹信息，ruleId：{}，routeId：{}", rule.getId(),
            // fishStruct.getRouteId());

            List<FishGroupConfig> fishGroupConfigList = MyRefreshFishingUtil.getFishGroupConfigListByFishRefreshRule(rule);

            fishGroupConfig = RandomUtil.randomEle(fishGroupConfigList);

        }

        routeConfig = fishGroupConfig.getRandomRoute(); // 随机一个轨迹

        if (rule.isBoss()) {

            // 背景同步：这里因为下面要延迟刷鱼，所以要再一次背景同步，并且为了，新玩家进入房间时，也可以看到新boss的背景出现
            room.setLastBossRemoveFishId(fishStruct.getConfigId());
            room.setLastBossRemoveTime(System.currentTimeMillis() - (fishStruct.getDelayTime() * 1000L));
            backgroundSync(null, room, fishStruct.getConfigId(), -(fishStruct.getDelayTime() * 1000L));

        }

        if (fishStruct.getDelayTime() > 0) { // 延迟刷鱼

            refreshFishWithDelay(room, Collections.singletonList(fishStruct.getConfigId()), routeConfig, fishStruct.getRuleId(), fishStruct.getDelayTime(), false, true, false, null);

            // if (PRINT_LOG_FUNCTION.apply(rule.getId())) {
            // log.info("延迟刷鱼：{}，fishConfigId：{}，delayTime：{}", rule.getId(), fishStruct.getConfigId(),
            // fishStruct.getDelayTime());
            // }

        } else { // 立即刷鱼

            refreshFishWithDelay(room, Collections.singletonList(fishStruct.getConfigId()), routeConfig, fishStruct.getRuleId(), 0, false, true, false, null);

            // if (PRINT_LOG_FUNCTION.apply(rule.getId())) {
            // log.info("立即刷鱼：{}，fishConfigId：{}", rule.getId(), fishStruct.getConfigId());
            // }

        }

        // 发送：持续刷的鱼，逃跑之后，是否还会再刷
        sendEscapeDurationRefreshResponse(room, true, !deathFlag, fishStruct.getConfigId());

        return true;

    }

    /**
     * 发送：持续刷的鱼，逃跑之后，是否还会再刷
     */
    private static void sendEscapeDurationRefreshResponse(NewBaseFishingRoom gameRoom, boolean refreshFlag, boolean escapeFlag, long fishConfigId) {

        if (BooleanUtil.isFalse(escapeFlag)) {
            return;
        }

        TtmyFishingChallengeMessage.FishingChallengeRoomEscapeDurationRefreshResponse.Builder builder = TtmyFishingChallengeMessage.FishingChallengeRoomEscapeDurationRefreshResponse.newBuilder();

        builder.setRefreshFlag(refreshFlag);
        builder.setFishConfigId(fishConfigId);

        MyRefreshFishingUtil.sendRoomMessage(gameRoom, OseeMessage.OseeMsgCode.S_C_FISHING_CHALLENGE_ROOM_ESCAPE_DURATION_REFRESH_RESPONSE_VALUE, builder);

    }

    /**
     * 不持续刷新鱼时的处理
     */
    private static void notDurationRefresh(NewBaseFishingRoom gameRoom, FishStruct finalFish, String stateName, FishRefreshRule rule, AtomicReference<FishRefreshRule> fishRefreshRule) {

        if (rule.isBoss()) {
            gameRoom.setBoss(0);
            // 背景同步
            gameRoom.setLastBossRemoveFishId(finalFish.getConfigId());
            gameRoom.setLastBossRemoveTime(System.currentTimeMillis());
            backgroundSync(null, gameRoom, finalFish.getConfigId(), 1L);
            // log.info("新刷新规则：boss{}刷新，并且持续刷新时间到，现在房间 boss数量：{}", stateName, gameRoom.getBoss());
        } else if (fishRefreshRule.get() != null) {
            fishRefreshRule.get().setFishCount(0);
            // log.info("新刷新规则：普通鱼{}刷新，并且持续刷新时间到", stateName);
        }

    }

    /**
     * 获取：黄金鱼的刷新规则
     */
    @NotNull
    public static FishRefreshRule getRoomGoldFishRule(NewBaseFishingRoom gameRoom) {

        return DataContainer.getDatas(FishRefreshRule.class).stream().filter(rule -> rule.getType() == 2 && ArrayUtil.contains(rule.getRealScene(), gameRoom.getRoomIndex())).findAny().orElse(new FishRefreshRule());

    }

    /**
     * 获取：黄金鱼的刷新规则 id
     */
    public static long getRoomGoldFishRuleId(NewBaseFishingRoom gameRoom) {

        return getRoomGoldFishRule(gameRoom).getId();

    }

    /**
     * 使用神灯刷鱼
     */
    public static void magicLampRefreshFish(NewBaseFishingRoom gameRoom, long routeId) {

        FishRefreshRule fishRefreshRule = MyRefreshFishingHelper.getRoomGoldFishRule(gameRoom); // 获取当前场次的黄金鱼刷新规则

        Set<Long> goldFishRouteIdSet = gameRoom.getFishMap().values().stream().filter(it -> it.getRuleId() == fishRefreshRule.getId()).map(FishStruct::getRouteId).collect(Collectors.toSet());

        FishGroupConfig fishGroupConfig = new FishGroupConfig();
        RouteConfig routeConfig = new RouteConfig();

        // 如果当前房间，存在同一路线的黄金鱼，则获取新的 路径
        if (CollUtil.isNotEmpty(goldFishRouteIdSet) && goldFishRouteIdSet.contains(routeId)) {

            fishGroupConfig = MyRefreshFishingUtil.getRandomFishGroupConfigByFishRefreshRule(fishRefreshRule, null); // 随机一个组

            routeConfig = MyRefreshFishingUtil.getRandomRouteByGroupConfig(fishGroupConfig, goldFishRouteIdSet); // 随机一条路线

        } else { // 刷新：随机一条该规则下的鱼，但是路径是前端指定的路径

            MyRefreshFishingUtil.findByRouteIdAndRule(routeId, fishRefreshRule, routeConfig, fishGroupConfig);

        }

        if (routeId == 0) {
            return;
        }

        Assert.notNull(fishGroupConfig, "未找到神灯轨迹-1，routeId：{}", routeId);

        Assert.notEmpty(fishGroupConfig.getRouteList(), "未找到神灯轨迹-2，routeId：{}", routeId);

        long fishId = MyRefreshFishingUtil.getRandomFishIdByGroupConfig(fishGroupConfig); // 随机一条鱼

        // 刷鱼
        MyRefreshFishingHelper.refreshFishWithDelay(gameRoom, CollUtil.newArrayList(fishId), routeConfig, fishRefreshRule.getId(), 0, false, false, false, null);

    }

    /**
     * boss号角鱼的 configId
     */
    public static final List<Integer> BOSS_BUGLE_FISH_CONFIG_ID = CollUtil.newArrayList(1039, 1040, 1041, 1042, 1044, 1043);

    /**
     * 玩家使用BOSS号角
     */
    public static void useBossBugle(NewBaseFishingRoom room, FishingGamePlayer player, int type) {

        long currentTimeMillis = System.currentTimeMillis();

//        player.lastFireTime.put(player.getId(), currentTimeMillis);
        player.setLastFireTime(System.currentTimeMillis());
        ServerUser user = player.getUser();

        // if (PlayerManager.getPlayerVipLevel(user) < 4) {
        // NetManager.sendHintMessageToClient("VIP才可以使用BOSS号角", user);
        // return;
        // }

        // if (currentTimeMillis - room.getLastRoomFrozenTime() < FishingManager.SKILL_FROZEN_TIME) {
        //
        // NetManager.sendHintMessageToClient("房间冰冻中不能召唤boss", user);
        // return;
        //
        // }

        synchronized (room) {

            OseePlayerEntity playerEntity = PlayerManager.getPlayerEntity(user);

            synchronized (playerEntity) {

                long fishId;

                int index = type - 1;

                int useCount = 1;

                if (index >= 0 && index < BOSS_BUGLE_FISH_CONFIG_ID.size()) {

                    fishId = BOSS_BUGLE_FISH_CONFIG_ID.get(index);

                } else {

                    fishId = type;

                    FishConfig fishConfig = DataContainer.getData(fishId, FishConfig.class);

                    useCount = fishConfig.getHornCallsFish();

                    // log.info("使用 boss号角，fishId：{}，fishName：{}，useCount：{}", fishId, fishConfig.getName(), useCount);

                    if (useCount < 0) {
                        NetManager.sendHintMessageToClient("不能召唤该boss", user);
                        return;
                    }

                }

                if (useCount > 0 && !PlayerManager.checkItem(user, ItemId.BOSS_BUGLE, useCount)) {

                    NetManager.sendHintMessageToClient("BOSS号角数量不足", user);
                    return;

                }

                if (room.getBugleFishNumber() > 0) {

                    NetManager.sendHintMessageToClient("当前房间存在boss，逃跑或死亡后再使用", user);

                    // log.info("当前房间存在boss号角鱼：{}", room.getCode());

                    return;

                }

                long canUseBossBugleTime = room.getCanUseBossBugleTime();

                // log.info("roomCode：{}，下次可以使用时间：{}，当前时间：{}，是否不可以使用：{}", room.getCode(),
                // DateUtil.date(room.getCanUseBossBugleTime()), DateUtil.date(currentTimeMillis),
                // currentTimeMillis < canUseBossBugleTime);

                if (currentTimeMillis < canUseBossBugleTime) {

                    NetManager.sendHintMessageToClient("boss击杀结算中，需冷却" + ((canUseBossBugleTime - currentTimeMillis) / 1000) + "秒再使用", user);
                    return;

                }

                // 获取当前场次的号角刷新规则
                List<FishRefreshRule> bugleRefreshRuleList = room.getBugleRefreshRuleList();

                if (CollUtil.isEmpty(bugleRefreshRuleList)) {

                    log.info("房间没有号角刷新规则：{}", room.getRoomIndex());

                    return;

                }

                // key：bugleRefreshRuleList中的下标，value：对应 rule里满足条件的 fishGroupConfigList
                Map<Integer, List<FishGroupConfig>> map = MapUtil.newHashMap();

                for (int i = 0; i < bugleRefreshRuleList.size(); i++) {

                    FishRefreshRule rule = bugleRefreshRuleList.get(i);

                    for (Long collectId : rule.getCollectIdList()) {

                        FishCollectConfig fishCollectConfig = DataContainer.getData(collectId, FishCollectConfig.class);

                        if (fishCollectConfig == null) {
                            continue;
                        }

                        for (Long groupId : fishCollectConfig.getGroupIdList()) {

                            FishGroupConfig fishGroupConfig = DataContainer.getData(groupId, FishGroupConfig.class);

                            if (fishGroupConfig == null) {
                                continue;
                            }

                            if (fishGroupConfig.getFishIdList().contains(fishId)) {

                                List<FishGroupConfig> fishGroupConfigList = map.computeIfAbsent(i, k -> new ArrayList<>());

                                fishGroupConfigList.add(fishGroupConfig);

                            }

                        }

                    }

                }

                if (CollUtil.isEmpty(map)) {

                    log.info("没有找到号角刷新规则的 FishGroupConfig，roomIndex：{}，fishId：{}", room.getRoomIndex(), fishId);

                    return;

                }

                // 随机一个 rule
                Integer randomIndex = RandomUtil.randomEle(map.keySet().toArray(new Integer[0]));

                FishRefreshRule fishRefreshRule = bugleRefreshRuleList.get(randomIndex);

                // 随机一个：fishGroupConfig
                FishGroupConfig fishGroupConfig = RandomUtil.randomEle(map.get(randomIndex));

                if (useCount > 0) {

                    // 扣除使用的号角数量
                    PlayerManager.addItem(user, ItemId.BOSS_BUGLE, -useCount, ItemChangeReason.USE_ITEM, true);

                }

                // 按照规则刷新  刷鱼
                refreshFish(room, CollUtil.newArrayList(fishId), fishGroupConfig.getRandomRoute(), fishRefreshRule.getId(), true,
                        false, false, null, null);

                // 房间置为有号角的状态
                room.setBugleFishNumber(1);

                // log.info("设置有boss号角鱼：{}", room.getCode());

                // 发送响应
                TtmyFishingChallengeMessage.FishingChallengeUseBossBugleResponse.Builder builder = TtmyFishingChallengeMessage.FishingChallengeUseBossBugleResponse.newBuilder();

                builder.setPlayerId(player.getId());
                builder.setType(type);

                MyRefreshFishingUtil.sendRoomMessage(room, room.getUseBossBugleResponseValue(), builder);


                // if (robotFlag) {
                //
                // log.info("机器人：召唤boss成功：{}，roomCode：{}", player.getUser().getNickname(), room.getCode());
                //
                // } else {
                //
                // log.info("召唤boss成功：{}，roomCode：{}", player.getUser().getNickname(), room.getCode());
                //
                // }

                // long count = room.getFishMap().values().stream().filter(FishStruct::isBossBulge).count();

                // if (count > 1) {
                //
                // if (robotFlag) {
                //
                // log.info("出现两个 boss号角，机器人：{}，roomCode：{}，count：{}", player.getUser().getNickname(),
                // room.getCode(), count);
                //
                // } else {
                //
                // log.info("出现两个 boss号角：{}，roomCode：{}，count：{}", player.getUser().getNickname(), room.getCode(),
                // count);
                //
                // }
                //
                // }

            }

        }

    }

    /**
     * 召唤测试鱼
     */
    public static void summonTestFish(FishBossMessage.FishInfo request, ServerUser user) {

        BaseGameRoom room = GameContainer.getGameRoomByPlayerId(user.getId());

        if (!(room instanceof FishingChallengeRoom)) {
            return;
        }

        FishingChallengeRoom gameRoom = (FishingChallengeRoom) room;
        TtmyFishingChallengeMessage.FishingChallengeRefreshFishesResponse.Builder builder = TtmyFishingChallengeMessage.FishingChallengeRefreshFishesResponse.newBuilder();

        FishStruct fishStruct = new FishStruct();

        fishStruct.setId(gameRoom.getNextId());
        fishStruct.setRuleId(request.getRuleId());
        fishStruct.setConfigId(request.getConfigId());
        fishStruct.setRouteId(request.getRouteId());
        fishStruct.setLifeTime(request.getLifeTime() * 1000);
        fishStruct.setLifeOrigTime(request.getLifeTime());
        fishStruct.setSafeTimes(request.getSafeTimes());
        fishStruct.setCreateTime(System.currentTimeMillis());
        fishStruct.setFishType(request.getFishType());

        fishStruct.setFirst(gameRoom.getRoomTick() <= 1);

        FishConfig fishConfig = DataContainer.getData(request.getConfigId(), FishConfig.class);

        if (fishConfig != null) {

            if (StrUtil.isNotBlank(fishConfig.getMonsterHpStr())) {

                // if (PRINT_LOG_FUNCTION.apply(ruleId)) {
                // System.out.println();
                // }

                fishStruct.setMonsterHp(fishConfig.getMonsterMaxHp());
                fishStruct.setMonsterMaxHp(fishConfig.getMonsterMaxHp());
                fishStruct.setMonsterHpCount(fishConfig.getMonsterHpCount());

                fishStruct.setPlayerAttackScopeList(fishConfig.getPlayerAttackScopeList());
                fishStruct.setMonsterAttackScopeList(fishConfig.getMonsterAttackScopeList());
                fishStruct.setMonsterAttackTimeList(fishConfig.getMonsterAttackTimeList());

            }

        }

        gameRoom.getFishMap().put(fishStruct.getId(), fishStruct);
//        gameRoom.putFishMap(fishStruct); //
        builder.addFishInfos(MyRefreshFishingHelper.createFishInfoProtoForChallenge(fishStruct, true));
        MyRefreshFishingUtil.sendRoomMessage(gameRoom, OseeMessage.OseeMsgCode.S_C_TTMY_FISHING_CHALLENGE_REFRESH_FISHES_RESPONSE_VALUE, builder);

    }

}
