package com.maple.game.osee.manager.fishing;

import cn.hutool.cache.impl.TimedCache;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.text.StrBuilder;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.maple.database.config.redis.RedisHelper;
import com.maple.database.data.entity.UserEntity;
import com.maple.database.util.MyEntityUtil;
import com.maple.engine.container.DataContainer;
import com.maple.engine.container.UserContainer;
import com.maple.engine.data.ServerUser;
import com.maple.engine.utils.MySettingUtil;
import com.maple.engine.utils.ThreadPoolUtils;
import com.maple.game.osee.common.RedisUtil;
import com.maple.game.osee.dao.data.entity.KillBossEntity;
import com.maple.game.osee.dao.data.entity.OseePlayerEntity;
import com.maple.game.osee.dao.data.mapper.OseePlayerMapper;
import com.maple.game.osee.dao.log.entity.CrystalExchangeLogEntity;
import com.maple.game.osee.dao.log.mapper.AppRewardRankMapper;
import com.maple.game.osee.dao.log.mapper.CrystalExchangeLogMapper;
import com.maple.game.osee.entity.*;
import com.maple.game.osee.entity.fishing.FishingGamePlayer;
import com.maple.game.osee.entity.fishing.NewBaseFishingRoom;
import com.maple.game.osee.entity.fishing.challenge.FishJc;
import com.maple.game.osee.entity.fishing.challenge.FishingChallengePlayer;
import com.maple.game.osee.entity.fishing.challenge.FishingChallengeRoom;
import com.maple.game.osee.entity.fishing.csv.file.BatteryLevelLjConfig;
import com.maple.game.osee.entity.fishing.csv.file.FishCcxxConfig;
import com.maple.game.osee.entity.fishing.csv.file.FishConfig;
import com.maple.game.osee.entity.fishing.csv.file.FishJoinMoneyConfig;
import com.maple.game.osee.entity.fishing.game.FireStruct;
import com.maple.game.osee.entity.fishing.game.FishStruct;
import com.maple.game.osee.manager.PlayerManager;
import com.maple.game.osee.manager.UserPropsManager;
import com.maple.game.osee.manager.UserStatusManager;
import com.maple.game.osee.model.dto.FireInfoDTO;
import com.maple.game.osee.model.dto.ProfitRatioDTO;
import com.maple.game.osee.model.entity.AccountDetailDO;
import com.maple.game.osee.pojo.UserStatusT;
import com.maple.game.osee.proto.OseeMessage;
import com.maple.game.osee.proto.OseePublicData;
import com.maple.game.osee.proto.fishing.TtmyFishingChallengeMessage;
import com.maple.game.osee.util.*;
import com.maple.gamebase.container.GameContainer;
import com.maple.gamebase.data.BaseGamePlayer;
import com.maple.gamebase.data.BaseGameRoom;
import com.maple.network.manager.NetManager;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.Nullable;
import org.redisson.api.RBatch;
import org.redisson.api.RBucket;
import org.redisson.api.RMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.maple.database.config.redis.RedisHelper.redissonClient;
import static com.maple.game.osee.manager.fishing.FishingManager.*;
import static com.maple.game.osee.util.FishingChallengeFightFishUtil.*;
import static com.maple.game.osee.util.MyRefreshFishingUtil.CHALLENGEE_AND_INTEGRAL_FISHING_ROOM_INDEX_LIST;
import static com.maple.game.osee.util.MyRefreshFishingUtil.CHALLENGE_AND_INTEGRAL_FISHING_CCXX_CONFIG_LIST;

/**
 * 捕鱼挑战赛管理类
 *
 * @author Junlong
 */
@Component
@Slf4j
public class FishingChallengeManager {

    @Autowired
    private OseePlayerMapper playerMapper;

    @Autowired
    private CrystalExchangeLogMapper crystalExchangeLogMapper;

    @Autowired
    private AppRewardRankMapper appRewardRankMapper;

    private static UserStatusManager userStatusManager;

    @Resource
    public void setUserStatusManager(UserStatusManager userStatusManager) {
        FishingChallengeManager.userStatusManager = userStatusManager;
    }

    private static OseePlayerMapper oseePlayerMapper;

    @Resource
    public void setOseePlayerMapper(OseePlayerMapper oseePlayerMapper) {
        FishingChallengeManager.oseePlayerMapper = oseePlayerMapper;
    }

    private static UserPropsManager userPropsManager;

    @Resource
    public void setUserPropsManager(UserPropsManager userPropsManager) {
        FishingChallengeManager.userPropsManager = userPropsManager;
    }

    /**
     * 最多可建房间数
     */
    private static final int MAX_ROOM_NUM = 20;

    /**
     * 进入房间最小炮台等级
     */
    private static final int MIN_BATTERY_LEVEL = 1000;

    /**
     * 记录玩家开火信息的集合
     */
    public static List<FireInfoDTO> fireInfoList = CollUtil.newCopyOnWriteArrayList(null); // 固定倍数
    public static List<FireInfoDTO> fireInfoDynamicList = CollUtil.newCopyOnWriteArrayList(null); // 动态倍数
    public static List<AccountDetailDO> accountDetailDOList = CollUtil.newCopyOnWriteArrayList(null); // 击杀鱼记录

    private static final Object FIRE_INFO_LIST_LOCK = new Object(); // 锁名
    private static final Object FIRE_INFO_DYNAMIC_LIST_LOCK = new Object(); // 锁名
    private static final Object FISHING_KILL_LOG_LOCK = new Object(); // 锁名

    /**
     * 定时保存：子弹消耗
     */
    @Scheduled(fixedDelay = 1500)
    public void updateFireInfoToRedis() {

        // 处理：开火信息集合
        handlerFireInfoList(1);
        handlerFireInfoList(2);

    }

    /**
     * 定时保存：击杀鱼记录
     */
    @Scheduled(fixedDelay = 2000)
    public void insertFishingKillLogDOToDoris() {

        // 处理：击杀鱼记录集合
        handleFishingKillLogDOList();

    }

    /**
     * 处理：开火信息集合
     *
     * @param type 1 固定倍数 2 动态倍数
     */
    private void handlerFireInfoList(int type) {

        final Object fireInfoListLockObject;
        List<FireInfoDTO> sourceFireInfoList;
        if (type == 1) {
            fireInfoListLockObject = FIRE_INFO_LIST_LOCK;
            sourceFireInfoList = fireInfoList;
        } else {
            fireInfoListLockObject = FIRE_INFO_DYNAMIC_LIST_LOCK;
            sourceFireInfoList = fireInfoDynamicList;
        }

        List<FireInfoDTO> tempFireInfoList;
        synchronized (fireInfoListLockObject) {
            if (CollUtil.isEmpty(sourceFireInfoList)) {
                return;
            }
            tempFireInfoList = sourceFireInfoList;
            if (type == 1) {
                fireInfoList = CollUtil.newCopyOnWriteArrayList(null);
            } else {
                fireInfoDynamicList = CollUtil.newCopyOnWriteArrayList(null);
            }
        }

        // 花费的钱：按照场次分
        Map<Integer, Long> usedMoneyMap = tempFireInfoList.stream()
                .collect(Collectors.groupingBy(FireInfoDTO::getIndex, Collectors.summingLong(FireInfoDTO::getUsedMoney)));

        // 赢的钱：按照场次分
        Map<Integer, Long> winMoneyMap = tempFireInfoList.stream()
                .collect(Collectors.groupingBy(FireInfoDTO::getIndex, Collectors.summingLong(FireInfoDTO::getWinMoney)));

        // 消耗：key
        String allUsedRedisPreKey;
        // 金币产出：key
        String allProduceGoldRedisPreKey;

        if (type == 1) {
            allUsedRedisPreKey = "ALL_USED_XH_TOTAL_STR:";
            allProduceGoldRedisPreKey = "ALL_PRODUCE_GOLD_TOTAL_STR:";
        } else {
            allUsedRedisPreKey = "ALL_USED_XH_TOTAL_DYNAMIC_STR:";
            allProduceGoldRedisPreKey = "ALL_PRODUCE_GOLD_TOTAL_DYNAMIC_STR:";
        }

        RBatch batch = redissonClient.createBatch();

        // 按照：场次，遍历进行处理
        usedMoneyMap.forEach((key, usedMoneyValue) -> {

            if (usedMoneyValue <= 0) {
                return;
            }

            // 消耗：key
            String allUsedRedisKey = allUsedRedisPreKey + key;

            // 金币产出：key
            String allProduceGoldRedisKey = allProduceGoldRedisPreKey + key;

            // 本次场次：产出金币总和
            Long nowWinMoney = winMoneyMap.getOrDefault(key, 0L);

            // 累加：总消耗
            batch.getAtomicLong(allUsedRedisKey).addAndGetAsync(usedMoneyValue);

            if (nowWinMoney > 0) {
                // 累加：总金币产出
                batch.getAtomicLong(allProduceGoldRedisKey).addAndGetAsync(nowWinMoney);
            }

        });

        batch.execute(); // 执行：批量操作

    }


    private static final String ACCOUNT_DETAIL_PRE_INSERT_SQL_TEMP;
    private static final String ACCOUNT_DETAIL_SUF_INSERT_SQL_TEMP;

    private static final Field[] ACCOUNT_DETAIL_FIELD_ARR = ReflectUtil.getFields(AccountDetailDO.class);

    static {

        StrBuilder strBuilderPre = StrBuilder.create(" ( ");
        StrBuilder strBuilderSuf = StrBuilder.create(" ( ");

        for (int i = 0; i < ACCOUNT_DETAIL_FIELD_ARR.length; i++) {

            Field field = ACCOUNT_DETAIL_FIELD_ARR[i];

            String fieldName = field.getName();

            strBuilderPre.append(StrUtil.toUnderlineCase(fieldName));

            if (field.getType().equals(String.class)) {
                strBuilderSuf.append("'{}'");
            } else {
                strBuilderSuf.append("{}");
            }

            if (i != ACCOUNT_DETAIL_FIELD_ARR.length - 1) {
                strBuilderPre.append(",");
                strBuilderSuf.append(",");
            }

        }

        strBuilderPre.append(" ) values ");
        strBuilderSuf.append(" ) ");

        ACCOUNT_DETAIL_PRE_INSERT_SQL_TEMP = strBuilderPre.toString();

        ACCOUNT_DETAIL_SUF_INSERT_SQL_TEMP = strBuilderSuf.toString();

    }

    /**
     * 处理：击杀鱼记录集合
     */
    private void handleFishingKillLogDOList() {

        List<AccountDetailDO> tempAccountDetailDOList;
        synchronized (FISHING_KILL_LOG_LOCK) {
            if (CollUtil.isEmpty(accountDetailDOList)) {
                return;
            }
            tempAccountDetailDOList = accountDetailDOList;
            accountDetailDOList = CollUtil.newCopyOnWriteArrayList(null);
        }

        StrBuilder strBuilder = StrBuilder.create(ACCOUNT_DETAIL_PRE_INSERT_SQL_TEMP);

        for (int i = 0; i < tempAccountDetailDOList.size(); i++) {

            AccountDetailDO accountDetailDO = tempAccountDetailDOList.get(i);

            List<Object> fieldValueList = new ArrayList<>();

            for (Field field : ACCOUNT_DETAIL_FIELD_ARR) {

                // 获取：字段值
                Object fieldValue = ReflectUtil.getFieldValue(accountDetailDO, field);

                if (fieldValue == null && field.getType() == String.class) {

                    fieldValueList.add("");

                } else {

                    fieldValueList.add(fieldValue);

                }

            }

            strBuilder.append(StrUtil.format(ACCOUNT_DETAIL_SUF_INSERT_SQL_TEMP, fieldValueList.toArray()));

            if (i != tempAccountDetailDOList.size() - 1) {
                strBuilder.append(",");
            }

        }

    }

    // 第一个房间
    public static FishingChallengeRoom ROOM_ONE = null;

    public FishingChallengeManager() {

        // 每一秒循环执行房间任务
        ThreadPoolUtils.TIMER_SERVICE_POOL.scheduleAtFixedRate(() -> {

            try {

                List<BaseGameRoom> gameRooms = GameContainer.getGameRooms();

                Set<Integer> removeRoomCodeSet = new HashSet<>();

                for (BaseGameRoom gameRoom : gameRooms) {

                    if (gameRoom.getClass() != FishingChallengeRoom.class) {
                        continue;
                    }

                    FishingChallengeRoom room = (FishingChallengeRoom) gameRoom;

                    if (!CHALLENGEE_AND_INTEGRAL_FISHING_ROOM_INDEX_LIST.contains(room.getRoomIndex())) {
                        continue;
                    }

                    if (room.getPlayerSize() > 0) { // 有玩家才刷鱼

                        doFishingRoomTask(room);

                    } else {

                        room.reset(true);

                        if (room.isVerify() && System.currentTimeMillis() > room.getRobotVerifySafeTs()) {

                            // log.info("移除房间");

                            removeRoomCodeSet.add(room.getCode()); // 移除：密码房

                        }

                    }

                }

                for (Integer item : removeRoomCodeSet) {

                    GameContainer.roomCodeMap.remove(item);

                }

            } catch (Exception e) {

                log.error("捕鱼挑战赛:执行房间循环任务时出现异常:[{}][{}]", e.getMessage(), e);

            }

        }, 0, 1000, TimeUnit.MILLISECONDS);

        // 循环执行房间任务
        ThreadPoolUtils.TIMER_SERVICE_POOL.scheduleAtFixedRate(() -> {

            try {

                List<FishingChallengeRoom> gameRooms = GameContainer.getGameRooms(FishingChallengeRoom.class);

                for (FishingChallengeRoom gameRoom : gameRooms) {

                    if (gameRoom == null) {
                        continue;
                    }

                    if (!CHALLENGEE_AND_INTEGRAL_FISHING_ROOM_INDEX_LIST.contains(gameRoom.getRoomIndex())) {
                        continue;
                    }

                    if (gameRoom.getPlayerSize() > 0) { // 有玩家才执行

                        doAttackTask(gameRoom);

                    }

                }

            } catch (Exception e) {

                log.error("捕鱼挑战赛:房间循环任务时出现异常:[{}][{}]", e.getMessage(), e);

            }

        }, 0, 10, TimeUnit.MILLISECONDS);

        ThreadPoolUtils.TASK_SERVICE_POOL.schedule(() -> {

            for (int i = 0; i < CHALLENGE_AND_INTEGRAL_FISHING_CCXX_CONFIG_LIST.size(); i++) {

                FishCcxxConfig item = CHALLENGE_AND_INTEGRAL_FISHING_CCXX_CONFIG_LIST.get(i);

                for (int j = 0; j < item.getRoomNumber(); j++) {

                    FishingChallengeRoom room =
                            GameContainer.createGameRoom(FishingChallengeRoom.class, item.getPlayerNumber());

                    room.setRoomIndex(item.getSessionId());
                    room.setConfigGameId(item.getGameId());

                }

            }

        }, 5, TimeUnit.SECONDS);

    }

    /**
     * @param index 0 1 2 3 4
     */
    public static RBucket<Integer> getTxBucket(int index) {

        return redissonClient.getBucket("REDIS_TX_VALUE:" + index);

    }

    /**
     * 获取：tx值
     *
     * @param resetFlag 是否重置 tx值
     * @param index     0 1 2 3 4
     */
    public static int getRedisTxValue(boolean resetFlag, int index, ProfitRatioDTO profitRatioDTO) {

        RBucket<Integer> txBucket = getTxBucket(index);

        if (resetFlag) {

            int tx = RandomUtil.getRandom(profitRatioDTO.getTxMinArr()[index], profitRatioDTO.getTxMaxArr()[index]);

            txBucket.set(tx); // 设置到：redis里面

            return tx;

        }

        Integer tx = txBucket.get();

        if (tx == null) {
            tx = getRedisTxValue(true, index, profitRatioDTO);
        }

        return tx;

    }

    /**
     * 房间循环任务，怪物攻击
     */
    private void doAttackTask(FishingChallengeRoom room) {

        long currentTimeMillis = System.currentTimeMillis();

        FishCcxxConfig fishCcxxConfig = MyRefreshFishingUtil.FISHING_CCXX_CONFIG_MAP.get(room.getRoomIndex());

        for (FishStruct fish : room.getFishMap().values()) {

            long maxLifeTime =
                    Math.round(fish.getLifeTime() > 0 ? fish.getLifeTime() : FishingManager.DEFAULT_LIFE_TIME * 1000);

            // if (MyRefreshFishingHelper.PRINT_LOG_FUNCTION.apply(fish.getRuleId())) {
            //
            // log.info("maxLifeTime：{}，createTime：{}，currentTimeMillis：{}，移除：{}，fishId：{}", maxLifeTime,
            // fish.getCreateTime(), currentTimeMillis, maxLifeTime + fish.getCreateTime() < currentTimeMillis,
            // fish.getId());
            //
            // }

            // 这里不用加冰冻时间，因为：fish.getLifeTime()，里面加了冰冻时间的
            if (maxLifeTime + fish.getCreateTime() < currentTimeMillis) {

            } else {

                // 处理：怪物攻击玩家
                handleMonsterAttackPlayer(fish, currentTimeMillis, room, fishCcxxConfig);

            }

        }

    }

    /**
     * 房间循环任务,刷鱼等
     */
    private void doFishingRoomTask(FishingChallengeRoom room) {

        long currentTimeMillis = System.currentTimeMillis();

        // 获取：房间内的刷鱼规则
        MyRefreshFishingHelper.getRoomRefreshRule(room);

        // 设置：房间内的禁用规则
        MyRefreshFishingHelper.setRoomDisablingRules(room);

        // 检查并执行刷鱼
        MyRefreshFishingHelper.checkAndRefresh(room);

        // 判断玩家操作时间
        for (int i = 0; i < room.getMaxSize(); i++) {

            FishingChallengePlayer player = room.getGamePlayerBySeat(i);

            if (player == null) {
                continue;
            }

//            if (ObjectUtils.isEmpty(player.lastFireTime.get(player.getId()))) { // 非空处理
//                player.lastFireTime.put(player.getId(), System.currentTimeMillis());
//            }

//            log.info("玩家姓名：{},最后一次开炮时间：{}", player.getUser().getNickname(), (currentTimeMillis - player.lastFireTime.get(player.getId())) / 1000);
            // 检查玩家是否长时间未操作
            if (currentTimeMillis - player.getLastFireTime() > FishingManager.ROOM_KICK_TIME) {

                NetManager.sendHintBoxMessageToClient("您长时间未操作，已被移出捕鱼房间", player.getUser(), 10);

                FishingChallengeUtil.exitRoom(player, room);

            }

        }

        // 判断过期鱼，并从鱼表内移除
        List<Long> removeFishId = new LinkedList<>();

        for (FishStruct fish : room.getFishMap().values()) {

            long maxLifeTime =
                    Math.round(fish.getLifeTime() > 0 ? fish.getLifeTime() : FishingManager.DEFAULT_LIFE_TIME * 1000);

//             if (MyRefreshFishingHelper.PRINT_LOG_FUNCTION.apply(fish.getRuleId())) {
//
//             log.info("maxLifeTime：{}，createTime：{}，currentTimeMillis：{}，移除：{}，fishId：{}", maxLifeTime,
//             fish.getCreateTime(), currentTimeMillis, maxLifeTime + fish.getCreateTime() < currentTimeMillis,
//             fish.getId());
//
//             }

            // 这里不用加冰冻时间，因为：fish.getLifeTime()，里面加了冰冻时间的
            if (maxLifeTime + fish.getCreateTime() < currentTimeMillis) {

//                 log.info("maxLifeTime：{}，CreateTime：{}，currentTimeMillis：{}，移除：{}，fish：{}", maxLifeTime,
//                 fish.getCreateTime(),
//                 currentTimeMillis, maxLifeTime + fish.getCreateTime() < currentTimeMillis,fish);

                removeFishId.add(fish.getId());

                long numm = RedisUtil.val("FISHING_CHALLENGE_GAME_GOLD_FISH_NUM2" + room.getCode(), 0L);

                double ruleId = MyRefreshFishingHelper.getRoomGoldFishRuleId(room); // 获取当前场次的黄金鱼刷新规则

                if (ruleId == fish.getRuleId()) {

                    numm -= 1;

                    if (numm < 0) {

                        numm = 0;

                    }

                    RedisHelper.set("FISHING_CHALLENGE_GAME_GOLD_FISH_NUM2" + room.getCode(), String.valueOf(numm));

                }

                MyRefreshFishingHelper.checkAndDurationRefreshFish(room, fish, false);

            } else {

            }

        }

        for (long fishId : removeFishId) {

//            log.info("移除：{}",fishId);
            room.getFishMap().remove(fishId);
//            room.removeFishMap(fishId);

        }

    }

    // /**
    // * 处理：世界 boss
    // * 主要针对：新的房间
    // */
    // private void handleWorldBoos(FishingChallengeRoom room) {

    // for (Map.Entry<Long, ConcurrentHashMap<Integer, NewBaseFishingRoom>> entry :
    // MyRefreshFishingHelper.WORLD_BOSS_ROOM_MAP
    // .entrySet()) {
    //
    // ConcurrentHashMap<Integer, NewBaseFishingRoom> item = entry.getValue();
    //
    // if (item.containsKey(room.getCode())) {
    // continue;
    // }
    //
    // NewBaseFishingRoom anyRoom = CollUtil.getFirst(item.values());
    //
    // if (anyRoom == null) {
    // continue;
    // }
    //
    // FishStruct fishStruct = anyRoom.getFishMap().get(entry.getKey());
    //
    // if (fishStruct == null) {
    // continue;
    // }
    //
    // CopyOnWriteArraySet<Integer> copyOnWriteArraySet =
    // MyRefreshFishingHelper.REFRESH_TIME_RULE_ID_ROOM_CODE_MAP.get(fishStruct.getRuleId());
    //
    // if (CollUtil.isEmpty(copyOnWriteArraySet)) {
    // continue;
    // }
    //
    // if (!copyOnWriteArraySet.contains(room.getCode())) { // 如果：没有该房间没有配置该刷新规则，则不刷鱼该鱼
    // continue;
    // }
    //
    // FishStruct copyFishStruct = fishStruct;
    //
    // if (MyRefreshFishingHelper.SPECIAL_WORLD_BOSS_MODEL_ID_SET.contains(fishStruct.getModelId())) {
    //
    // // 拷贝一份对象
    // copyFishStruct = BeanUtil.copyProperties(fishStruct, FishStruct.class);
    //
    // }
    //
    // MyRefreshFishingHelper.putFishToRoomFishMap(room, true, copyFishStruct, null);
    //
    // GeneratedMessage.Builder<?> messageBuilder = room.getRefreshFishMessageBuilder();
    //
    // // log.info("世界 boss：开始 ===========");
    //
    // // 同步鱼
    // room.addFishInfos(copyFishStruct, messageBuilder); // 添加到 messageBuilder里
    //
    // // log.info("世界 boss：结束 ===========");
    //
    // MyRefreshFishingUtil.sendRoomMessage(room, room.getRefreshFishResponseValue(), messageBuilder);
    //
    // }

    // }

    /**
     * 处理：怪物攻击玩家
     */
    private void handleMonsterAttackPlayer(FishStruct fish, long currentTimeMillis, FishingChallengeRoom room,
                                           FishCcxxConfig fishCcxxConfig) {

        if (fish.getFishType2() == 4) {

            // log.info("怪物可以攻击的玩家：{}", fish.getTempMonsterAttackPlayerIdSet());

        }

        if (fish.getMonsterAttackScopeList() == null || CollUtil.isEmpty(fish.getTempMonsterAttackPlayerIdSet())) {
            return;
        }

        if (fish.getMonsterAttackPlayerId() == null) {

            fish.setMonsterAttackPlayerId(CollUtil.getFirst(fish.getTempMonsterAttackPlayerIdSet()));

        } else {

            if (fish.isBossBulge()) {

                try {

                    // 随机一个，备注：这里由于并发问题，会出现 null指针，可以忽略该错误
                    fish.setMonsterAttackPlayerId(cn.hutool.core.util.RandomUtil
                            .randomEle(new ArrayList<>(fish.getTempMonsterAttackPlayerIdSet())));

                } catch (Exception ignored) {

                }

                // log.info("本次要攻击的玩家：{}", fish.getMonsterAttackPlayerId());

            }

        }

        // 本次：怪物要攻击的玩家 id
        Long monsterAttackPlayerId = fish.getMonsterAttackPlayerId();

        if (monsterAttackPlayerId == null) {
            return;
        }

        BaseGamePlayer player = room.getGamePlayerById(monsterAttackPlayerId);

        if (player == null) { // 如果玩家不存在，则需要换一个玩家进行攻击

            fish.setMonsterAttackPlayerId(null);

            fish.getTempMonsterAttackPlayerIdSet().remove(monsterAttackPlayerId);

            return;

        }

        // 如果：不存在攻击时长，则结束
        if (CollUtil.isEmpty(fish.getMonsterAttackTimeList())) {
            return;
        }

        int frozenType = fishCcxxConfig.getFrozenType(); // 0 全屏（默认） 1 随机

        long frozenFinishMs; // 已经冰冻了多久

        if (frozenType == 1) {

            frozenFinishMs = currentTimeMillis - fish.getLastFishFrozenTime(); // 鱼已经冰冻了多久

        } else {

            frozenFinishMs = currentTimeMillis - room.getLastRoomFrozenTime(); // 房间已经冰冻了多久

        }

        if (frozenFinishMs < SKILL_FROZEN_TIME && fish.isAddSurvivalTimeFlag()) { // 如果：鱼还在冰冻中

            long frozenRemainMs = SKILL_FROZEN_TIME - frozenFinishMs; // 鱼冰冻剩余时间

            if (fish.getMonsterLastAttackTs() < currentTimeMillis) { // 如果：没有增加过冰冻时间

                long attackFinishMs = currentTimeMillis - fish.getMonsterLastAttackTs(); // 已经攻击了多久

                if (attackFinishMs < fish.getMonsterAttackTime()) { // 如果：怪物还没有攻击完成

                    fish.setAttackFinishMs(attackFinishMs);

                    fish.setMonsterLastAttackTs(fish.getMonsterLastAttackTs() + frozenRemainMs); // 加上：剩余冰冻时间

                }

            }

            return;

        }

        if (currentTimeMillis - fish.getMonsterLastAttackTs() < fish.getMonsterAttackTime()) {
            return;
        }

        fish.setMonsterLastAttackTs(currentTimeMillis); // 设置；上一次攻击时间

        int index = RandomUtil.getRandom(0, fish.getMonsterAttackTimeList().size());

        Integer time = fish.getMonsterAttackTimeList().get(index);

        fish.setMonsterAttackTime(time); // 设置：本次攻击的时间

        if (fish.isAddSurvivalTimeFlag()) {

            // 增加：鱼的存活时间
            FishingHelper.addFishLifeTime(fish.getMonsterAttackTime(), fish);

        }

        int hitPlayerValue =
                RandomUtil.getRandom(fish.getMonsterAttackScopeList().get(0), fish.getMonsterAttackScopeList().get(1));

        fish.setHitPlayerValue(hitPlayerValue);
        fish.setHitPlayerId(monsterAttackPlayerId);

        // 进行攻击
        TtmyFishingChallengeMessage.XmjjMonsterAttackPlayerResponse.Builder builder =
                TtmyFishingChallengeMessage.XmjjMonsterAttackPlayerResponse.newBuilder();

        builder.setMonsterId(fish.getId());
        builder.setPlayerId(monsterAttackPlayerId);
        builder.setHitPlayerValue(hitPlayerValue);

        builder.setMonsterAttackTime(fish.getMonsterAttackTime());

        builder.setMonsterAttackTimeType(fish.getMonsterAttackTimeList().indexOf(fish.getMonsterAttackTime()) + 1);

        MyRefreshFishingUtil.sendRoomMessage(room,
                OseeMessage.OseeMsgCode.S_C_XMJJ_MONSTER_ATTACK_PLAYER_RESPONSE_VALUE, builder);

    }

    /**
     * 创建房间信息协议
     */
    public TtmyFishingChallengeMessage.FishingChallengeRoomInfoProto.Builder
    createRoomInfoProto(FishingChallengeRoom room) {

        TtmyFishingChallengeMessage.FishingChallengeRoomInfoProto.Builder builder =
                TtmyFishingChallengeMessage.FishingChallengeRoomInfoProto.newBuilder();

        builder.setRoomCode(room.getCode());
        builder.setBoss(room.getBoss());
        builder.setVip(room.isVip());
        builder.setVerify(room.isVerify());

        builder.setRoomIndex(room.getRoomIndex());

        for (BaseGamePlayer gamePlayer : room.getGamePlayers()) {

            if (gamePlayer != null) {

                UserEntity userEntity = gamePlayer.getUser().getEntity();

                if (userEntity.getHeadIndex() == 0) {

                    builder.addHeadImg(userEntity.getHeadUrl());

                } else {

                    builder.addHeadImg(String.valueOf(userEntity.getHeadIndex()));

                }

            }

        }

        return builder;

    }

    /**
     * 创建玩家信息协议
     */
    public static TtmyFishingChallengeMessage.FishingChallengePlayerInfoProto.Builder
    createPlayerInfoProto(FishingChallengePlayer player, NewBaseFishingRoom room) {

        TtmyFishingChallengeMessage.FishingChallengePlayerInfoProto.Builder builder =
                TtmyFishingChallengeMessage.FishingChallengePlayerInfoProto.newBuilder();

        builder.setPlayerId(player.getId());

        OseePlayerEntity oseePlayerEntity = PlayerManager.getPlayerEntity(player.getUser());

        UserEntity userEntity = player.getUser().getEntity();

        if (userEntity == null) {
            userEntity = new UserEntity();
        }

        builder.setDiamond(String.valueOf(oseePlayerEntity.getDiamond()));
        builder.setName(player.getUser().getNickname());
        builder.setHeadIndex(userEntity.getHeadIndex());
        builder.setHeadUrl(MyEntityUtil.getNotNullStr(userEntity.getHeadUrl()));
        builder.setSex(userEntity.getSex());
        builder.setMoney(player.getMoney());
        builder.setSeat(player.getSeat());
        builder.setOnline(player.getUser().isOnline());
        builder.setVipLevel(player.getVipLevel());
        builder.setViewIndex(player.getViewIndex());
        builder.setWingIndex(player.getWingIndex());
        builder.setBatteryLevel((int) player.getBatteryLevel());
        builder.setBatteryMult(player.getBatteryMult());
        builder.setLevel(player.getLevel());

        builder.setLottery(oseePlayerEntity.getLottery());


        if (player.getV3ProtoBuilder() != null) {

            builder.setV3(player.getV3ProtoBuilder());

        }

        return builder;

    }

    // ******************************************

    /**
     * 发送加入房间响应
     */
    public static void sendJoinRoomResponse(NewBaseFishingRoom room, FishingChallengePlayer player) {
        TtmyFishingChallengeMessage.FishingChallengeJoinRoomResponse.Builder builder =
                TtmyFishingChallengeMessage.FishingChallengeJoinRoomResponse.newBuilder();

        builder.setRoomCode(room.getCode());
        builder.setVip(room.isVip());
        builder.setRoomType(room.getRoomIndex());

        NetManager.sendMessage(OseeMessage.OseeMsgCode.S_C_TTMY_FISHING_CHALLENGE_JOIN_ROOM_RESPONSE_VALUE, builder,
                player.getUser());

    }

    /**
     * 给房间内所有玩家发送某玩家信息
     */
    public static void sendRoomPlayerInfoResponse(NewBaseFishingRoom room, FishingChallengePlayer player) {

        TtmyFishingChallengeMessage.FishingChallengeRoomPlayerInfoResponse.Builder builder =
                TtmyFishingChallengeMessage.FishingChallengeRoomPlayerInfoResponse.newBuilder();

        builder.setPlayerInfo(createPlayerInfoProto(player, room));

        MyRefreshFishingUtil.sendRoomMessage(room,
                OseeMessage.OseeMsgCode.S_C_TTMY_FISHING_CHALLENGE_ROOM_PLAYER_INFO_RESPONSE_VALUE, builder);

    }

    /**
     * 发送房间内所有玩家的信息给某玩家
     */
    public static void sendRoomPlayerInfoListResponse(NewBaseFishingRoom room, FishingChallengePlayer player) {

        if (room != null) {

            TtmyFishingChallengeMessage.FishingChallengeRoomPlayerInfoListResponse.Builder builder =
                    TtmyFishingChallengeMessage.FishingChallengeRoomPlayerInfoListResponse.newBuilder();

            for (BaseGamePlayer gamePlayer : room.getGamePlayers()) {

                if (gamePlayer != null) {

                    FishingChallengePlayer fishingChallengePlayer = (FishingChallengePlayer) gamePlayer;

                    builder.addPlayerInfos(createPlayerInfoProto(fishingChallengePlayer, room));

                }

            }

            builder.setUserId(player.getId());

            MyRefreshFishingUtil.sendRoomMessage(room,
                    OseeMessage.OseeMsgCode.S_C_TTMY_FISHING_CHALLENGE_ROOM_PLAYER_INFO_LIST_RESPONSE_VALUE, builder);

        }
    }

    /**
     * 向玩家发送房间当前的冰冻消息
     */
    public static void sendFrozenMessage(NewBaseFishingRoom gameRoom, FishingChallengePlayer player) {

        FishingHelper.sendFrozenMessage(gameRoom, player.getUser());

    }

    /**
     * 发送电磁炮使用
     */
    public void useEle(long fishId, FishingChallengeRoom gameRoom, ServerUser user) {
        OseePublicData.UseEleResponse.Builder builder = OseePublicData.UseEleResponse.newBuilder();
        for (BaseGamePlayer gamePlayer : gameRoom.getGamePlayers()) {
            if (gamePlayer != null) {
                ServerUser user1 = UserContainer.getUserById(gamePlayer.getId());
                builder.setFishId(fishId);
                builder.setUserId(user.getId());
                NetManager.sendMessage(OseeMessage.OseeMsgCode.S_C_CHALLENGE_USE_ELE_RESPONSE_VALUE, builder, user1);
            }
        }
    }

    /**
     * 发送黑洞炮使用
     */
    public void useBlack(float x, float y, FishingChallengeRoom gameRoom, ServerUser user) {
        OseePublicData.UseBlackResponse.Builder builder = OseePublicData.UseBlackResponse.newBuilder();
        for (BaseGamePlayer gamePlayer : gameRoom.getGamePlayers()) {
            if (gamePlayer != null) {
                ServerUser user1 = UserContainer.getUserById(gamePlayer.getId());
                builder.setX(x);
                builder.setY(y);
                builder.setUserId(user.getId());
                NetManager.sendMessage(OseeMessage.OseeMsgCode.S_C_CHALLENGE_USE_BLACK_RESPONSE_VALUE, builder, user1);
            }
        }
    }

    /**
     * 发送黑洞炮使用
     */
    public void useTro(float x, float y, FishingChallengeRoom gameRoom, ServerUser user) {
        OseePublicData.UseTroResponse.Builder builder = OseePublicData.UseTroResponse.newBuilder();
        for (BaseGamePlayer gamePlayer : gameRoom.getGamePlayers()) {
            if (gamePlayer != null) {
                ServerUser user1 = UserContainer.getUserById(gamePlayer.getId());
                builder.setX(x);
                builder.setY(y);
                builder.setUserId(user.getId());
                NetManager.sendMessage(OseeMessage.OseeMsgCode.S_C_CHALLENGE_USE_TRO_RESPONSE_VALUE, builder, user1);
            }
        }
    }

    /**
     * 发送钻头使用
     *
     * @param fishType 鱼类型：1 激光蟹 2 钻头蟹
     */
    public static void useBit(float angle, NewBaseFishingRoom gameRoom, ServerUser user, long fishId, int fishType) {

        OseePublicData.UseBitResponse.Builder builder = OseePublicData.UseBitResponse.newBuilder();
        builder.setAngle(angle);
        builder.setUserId(user.getId());
        builder.setFishId(fishId);
        builder.setFishType(fishType);

        for (BaseGamePlayer gamePlayer : gameRoom.getGamePlayers()) {
            if (gamePlayer != null) {
                ServerUser user1 = UserContainer.getUserById(gamePlayer.getId());
                NetManager.sendMessage(OseeMessage.OseeMsgCode.S_C_CHALLENGE_USE_BIT_RESPONSE_VALUE, builder, user1);
            }
        }

    }

    /**
     * 发送房间内鱼同步的响应
     */
    public static void sendSynchroniseResponse(NewBaseFishingRoom gameRoom, FishingChallengePlayer player) {

        TtmyFishingChallengeMessage.FishingChallengeSynchroniseResponse.Builder builder =
                TtmyFishingChallengeMessage.FishingChallengeSynchroniseResponse.newBuilder();

        // 不需要再次同步的鱼 idSet
        Set<Long> notSyncFishIdSet = CollUtil.newHashSet();

        for (FishStruct fish : gameRoom.getFishMap().values()) {

            if (notSyncFishIdSet.contains(fish.getId())) {
                continue;
            }

            if (CollUtil.isNotEmpty(fish.getFishStructList())) {

                // 跟着一起刷的鱼，就不需要再次进入循环了，目的：只需要装一个对象就可以了
                fish.getFishStructList().forEach(it -> notSyncFishIdSet.add(it.getId()));

                // 用第一个
                fish = fish.getFishStructList().get(0);

            }

            builder.addFishInfos(MyRefreshFishingHelper.createFishInfoProtoForChallenge(fish, true));

        }
        // 机械迷城 需要重新写入数据
        if (gameRoom.getGameId() == 1411 || gameRoom.getGameId() == 1412) {
            // 清除 重新写入返回数据
            builder.clear();
            List<FishStruct> FishStructList = gameRoom.getRoomFishForbiddenTypes().entrySet().stream().map(Map.Entry::getKey).collect(Collectors.toList());
            for (FishStruct fish : FishStructList) {
                builder.addFishInfos(MyRefreshFishingHelper.createFishInfoProtoForChallenge(fish, true));
            }
        }
        NetManager.sendMessage(OseeMessage.OseeMsgCode.S_C_TTMY_FISHING_CHALLENGE_SYNCHRONISE_RESPONSE_VALUE, builder,
                player.getUser());
//        log.info("发送同步鱼消息：长度:{}",builder.getFishInfosList().size());
    }

    /**
     * 为房间创建加入一名玩家
     */
    public static void addRoomPlayer(NewBaseFishingRoom room, ServerUser user) {

        // 检查：是否可以加入房间
        if (!GameUtil.joinRoomCheck(user)) {
            return;
        }

        OseePlayerEntity playerEntity = PlayerManager.getPlayerEntity(user);

        long level = getBatteryLevel(room.getRoomIndex(), 0);

        FishingChallengePlayer player;

        synchronized (room) {

            if (room.getPlayerSize() >= room.getMaxSize()) {
                NetManager.sendErrorMessageToClient("房间人数已满", user);
                return;
            }

            long enterMoney = playerEntity.getDragonCrystal();

            player = GameContainer.createGamePlayer(room, user, FishingChallengePlayer.class);

            player.setEnterMoney(enterMoney);
            player.setEnterRoomTime(System.currentTimeMillis());

            JOIN_ITEM.forEach(id -> player.getJoinItemCount().put(id, PlayerManager.getItemNum(user, id)));

            String viewIndex = RedisHelper.get("USE_BATTERYVIEW:" + user.getId());
            if (!viewIndex.isEmpty()) {
                player.setViewIndex(Integer.valueOf(viewIndex));
            }

            // 玩家初始炮台倍数
            player.setBatteryLevel(level);

            player.setLastBatteryLevel(player.getBatteryLevel());

            // 处理：炮倍等级切换
            FishingChallengeUtil.handlerBatteryLevelChange(player, level, room, true, "加入房间", playerEntity, null);

            // int x = CommonLobbyManager.getUserT(user,roomType+9);
            // RedisHelper.set("USER_T_PEAK_VALUE"+user.getId(),String.valueOf(x));
            sendJoinRoomResponse(room, player); // 发送加入房间响应
            // sendRoomPlayerInfoListResponse(room, player); // 发送当前房间内玩家的信息
            // sendRoomPlayerInfoResponse(room, player); // 发送自己的信息给房间内所有玩家
            // sendSynchroniseResponse(room, player); // 发送同步鱼消息
            // sendFrozenMessage(room, player); // 发送房间当前冰冻消息

            playerEntity.setLastJoinRoomTime(new Date());

            // 添加到：待更新的集合里
            PlayerManager.updateEntities.add(playerEntity);

            // log.info("玩家加入房间：{}，roomCode：{}", player.getId(), room.getCode());

            boolean userLeiShenBianUseFlag = getUserLeiShenBianUseFlag(player.getId(), room);

            if (userLeiShenBianUseFlag) { // 开启雷神变

                RedisHelper.set("USER_CRIT_MULT" + user.getId(), String.valueOf(2));

            }
            // 进入房间重置 最后一次开炮时间
//            player.lastFireTime.put(player.getId(),System.currentTimeMillis());
            player.setLastFireTime(System.currentTimeMillis());
        }

        synchronized (playerEntity) {

            // 模拟开一枪，然后重新生成节点
            generateNewJczd0List(playerEntity, user, null, false, false);

        }

        List<FishingChallengeRoom> gameRoomList = GameContainer.getGameRooms(FishingChallengeRoom.class).stream()
                .filter(it -> it.getRoomIndex() == room.getRoomIndex()).collect(Collectors.toList());

        for (int i = 0; i < gameRoomList.size(); i++) {

            if (gameRoomList.get(i).getCode() == room.getCode()) {

                player.setClientRoomNumber(i + 1);
                break;

            }

        }

    }

    // /**
    // * 发送捕获到boss鱼的广播响应
    // */
    // public static void sendCatchBossFishResponse(
    // TtmyFishingChallengeMessage.FishingChallengeCatchBossFishResponse.Builder response, long delay) {
    //
    // ThreadPoolUtils.TASK_SERVICE_POOL.schedule(() -> {
    // // 只发送到全服在捕鱼房间内的玩家
    // List<FishingChallengeRoom> fishingGameRooms = GameContainer.getGameRooms(FishingChallengeRoom.class);
    // for (FishingChallengeRoom gameRoom : fishingGameRooms) {
    // if (gameRoom != null) {
    // MyRefreshFishingUtil.sendRoomMessage(gameRoom,
    // OseeMessage.OseeMsgCode.S_C_TTMY_FISHING_CHALLENGE_CATCH_BOSS_FISH_RESPONSE_VALUE, response);
    // }
    // }
    // }, delay, TimeUnit.SECONDS);
    //
    // }

    // ******************************************

    /**
     * 挑战赛房间列表
     */
    public void roomList(ServerUser user, TtmyFishingChallengeMessage.FishingChallengeRoomListRequest request) {

        TtmyFishingChallengeMessage.FishingChallengeRoomListResponse.Builder builder =
                TtmyFishingChallengeMessage.FishingChallengeRoomListResponse.newBuilder();

        boolean verifyFlag = request.getVerifyFlag();

        boolean allFlag = request.getAllFlag();

        int roomIndex = request.getRoomIndex();

        List<FishingChallengeRoom> gameRooms = GameContainer.getGameRooms(FishingChallengeRoom.class).stream()
                .filter(it -> (allFlag || it.isVerify() == verifyFlag) && it.getRoomIndex() == roomIndex)
                .collect(Collectors.toList());

        gameRooms.forEach(fishingChallengeRoom -> builder.addRoomList(createRoomInfoProto(fishingChallengeRoom)));

        NetManager.sendMessage(OseeMessage.OseeMsgCode.S_C_TTMY_FISHING_CHALLENGE_ROOM_LIST_RESPONSE_VALUE, builder,
                user);

    }

    // 房间 sessionId，房间区服 id，房间 codeMap
    public static final Map<Integer,
            Map<Integer, TimedCache<Integer, Integer>>> roomSessionIdAndRoomAreaIdAndRoomCodeMap = new HashMap<>();

    /**
     * 创建房间-获取房间号
     */
    public void createRoomGetRoomCode(ServerUser user,
                                      TtmyFishingChallengeMessage.TtmyFishingChallengeCreateRoomGetRoomCodeRequest request) {

        // 获取：房间号
        Integer roomCode = getRoomCode(user, request);

        if (roomCode == null) {

            NetManager.sendErrorMessageToClient("当前场次拥挤，请前往其他场次！", user);

            log.info("当前场次拥挤：roomCode 为 null，sessionId：{}，areaId：{}，areaTotal：{}", request.getRoomSessionId(),
                    request.getCreateRoomAreaId(), request.getRoomAreaTotal());

            return;

        }

        TtmyFishingChallengeMessage.TtmyFishingChallengeCreateRoomGetRoomCodeResponse.Builder builder =
                TtmyFishingChallengeMessage.TtmyFishingChallengeCreateRoomGetRoomCodeResponse.newBuilder();

        builder.setRoomCode(roomCode);

        NetManager.sendMessage(
                OseeMessage.OseeMsgCode.S_C_TTMY_FISHING_CHALLENGE_CREATE_ROOM_GET_ROOM_CODE_RESPONSE_VALUE, builder, user);

    }

    /**
     * 获取：房间号
     */
    @Nullable
    public static Integer getRoomCode(@Nullable ServerUser user,
                                      TtmyFishingChallengeMessage.TtmyFishingChallengeCreateRoomGetRoomCodeRequest request) {

        Integer roomCode = null;

        synchronized (GameContainer.ROOM_LOCK) {

            // 获取：场次配置，并检查数量
            FishCcxxConfig fishCcxxConfig = getFishCcxxConfigAndCheckCount(user, request.getRoomSessionId());

            if (fishCcxxConfig == null) {
                return null;
            }

            Map<Integer, TimedCache<Integer, Integer>> roomAreaIdAndRoomCodeMap =
                    roomSessionIdAndRoomAreaIdAndRoomCodeMap.computeIfAbsent(request.getRoomSessionId(),
                            k -> new HashMap<>());

            TimedCache<Integer, Integer> roomCodeMap = roomAreaIdAndRoomCodeMap
                    .computeIfAbsent(request.getCreateRoomAreaId(), k -> new TimedCache<>(60 * 1000));

            int roomAreaTotal = request.getRoomAreaTotal();

            int createRoomAreaId = request.getCreateRoomAreaId();

            boolean endFlag = false;

            for (int i = 0; i < 10; i++) {

                if (endFlag) {
                    break;
                }

                int newRoomCode = ThreadLocalRandom.current().nextInt(100000, 1000000);

                for (int j = 0; j < 1000; j++) {

                    if (newRoomCode % roomAreaTotal == createRoomAreaId) {

                        if (!GameContainer.roomCodeMap.containsKey(newRoomCode)) {

                            if (!roomCodeMap.containsKey(newRoomCode)) {

                                roomCode = newRoomCode;

                                endFlag = true;

                                roomCodeMap.put(newRoomCode, newRoomCode);

                                break;

                            }

                        }

                    }

                    newRoomCode = newRoomCode + 1;

                }

            }

        }

        return roomCode;

    }

    /**
     * 创建房间
     */
    @Nullable
    public static FishingChallengeRoom createRoom(@Nullable ServerUser user,
                                                  TtmyFishingChallengeMessage.FishingChallengeCreateRoomRequest request, boolean addToRoomFlag) {

        if (StrUtil.isBlank(request.getRoomPassword())) {

            NetManager.sendErrorMessageToClient("密码不能为空", user);
            return null;

        }

        if (request.getRoomPassword().length() > 20) {

            NetManager.sendErrorMessageToClient("密码长度必须在1-20位之间", user);
            return null;

        }

        synchronized (GameContainer.ROOM_LOCK) {

            int newRoomCode = request.getRoomCode();

            Map<Integer, TimedCache<Integer, Integer>> roomAreaIdAndRoomCodeMap =
                    roomSessionIdAndRoomAreaIdAndRoomCodeMap.computeIfAbsent(request.getRoomSessionId(),
                            k -> new HashMap<>());

            TimedCache<Integer, Integer> roomCodeMap = roomAreaIdAndRoomCodeMap
                    .computeIfAbsent(request.getCreateRoomAreaId(), k -> new TimedCache<>(60 * 1000));

            if (!roomCodeMap.containsKey(newRoomCode)) {

                NetManager.sendErrorMessageToClient("当前场次拥挤，请前往其他场次！", user);

                log.info("当前场次拥挤：roomCode 不存在，sessionId：{}，areaId：{}，areaTotal：{}，newRoomCode：{}",
                        request.getRoomSessionId(), request.getCreateRoomAreaId(), request.getRoomAreaTotal(), newRoomCode);

                return null;

            }

            roomCodeMap.remove(newRoomCode);

            if (GameContainer.roomCodeMap.containsKey(newRoomCode)) {

                NetManager.sendErrorMessageToClient("当前场次拥挤，请前往其他场次！", user);

                log.info("当前场次拥挤：房间已被创建，sessionId：{}，areaId：{}，areaTotal：{}，newRoomCode：{}", request.getRoomSessionId(),
                        request.getCreateRoomAreaId(), request.getRoomAreaTotal(), newRoomCode);

                return null;

            }

            // 获取：场次配置，并检查数量
            FishCcxxConfig fishCcxxConfig = getFishCcxxConfigAndCheckCount(user, request.getRoomSessionId());

            if (fishCcxxConfig == null) {
                return null;
            }

            FishingChallengeRoom room = GameContainer.createGameRoom(FishingChallengeRoom.class, 4);

            int oldRoomCode = room.getCode();

            room.setCode(newRoomCode); // 设置：新的 roomCode

            GameContainer.roomCodeMap.remove(oldRoomCode);

            GameContainer.roomCodeMap.put(room.getCode(), room);

            room.setRoomIndex(fishCcxxConfig.getSessionId());
            room.setConfigGameId(fishCcxxConfig.getGameId());

            // 新建房间的时候清除房间的缓存数据
            RedisHelper.set("FISHING_CHALLENGE_GAME_GOLD_FISH_NUM2" + room.getCode(), "0");

            room.setVerify(true);
            room.setRoomPassword(request.getRoomPassword());

            if (addToRoomFlag && user != null) {

                // 加入房间
                addRoomPlayer(room, user);

            }

            return room;

        }

    }

    /**
     * 获取：场次配置，并检查数量
     */
    @Nullable
    private static FishCcxxConfig getFishCcxxConfigAndCheckCount(@Nullable ServerUser user, int sessionId) {

        FishCcxxConfig fishCcxxConfig = CHALLENGE_AND_INTEGRAL_FISHING_CCXX_CONFIG_LIST.stream()
                .filter(it -> it.getSessionId() == sessionId).findFirst().orElse(null);

        if (fishCcxxConfig == null) {

            log.info("房间配置未找到：{}", sessionId);
            return null;

        }

        int roomNumber = fishCcxxConfig.getRoomNumber();

        long currentCount = GameContainer.getGameRooms().stream().filter(it -> {

            if (!(it instanceof NewBaseFishingRoom)) {
                return false;
            }

            NewBaseFishingRoom room = (NewBaseFishingRoom) it;

            return room.isVerify() && room.getRoomIndex() == sessionId;

        }).count();

        if (currentCount >= roomNumber + 40) {

            NetManager.sendErrorMessageToClient("当前场次拥挤，请前往其他场次！", user);

            log.info("当前场次拥挤：房间已被创建，sessionId：{}，currentCount：{}，roomNumber：{}", sessionId, currentCount, roomNumber);

            return null;

        }

        return fishCcxxConfig;

    }

    /**
     * 加入房间
     */
    public void listJoinRoom(ServerUser user, List<Integer> sessionIdList) {

        List<Integer> roomIndexList = new ArrayList<>();

        for (Integer item : sessionIdList) {

            boolean flag = checkRoomIndexJoinMoney(user, item, false);

            if (flag) {

                roomIndexList.add(item);

            }

        }

        if (CollUtil.isEmpty(roomIndexList)) {

            FishJoinMoneyConfig fishJoinMoneyConfig =
                    DataContainer.getData(sessionIdList.get(0), FishJoinMoneyConfig.class);

            NetManager.sendHintBoxMessageToClient(
                    StrUtil.format("加入当前场次金币不能低于{}万", fishJoinMoneyConfig.getMinMoney() / 10000), user, 10);

            return;

        }

        Integer roomIndex = cn.hutool.core.util.RandomUtil.randomEle(roomIndexList);

        // 执行：加入房间
        joinRoom(user, 0, null, roomIndex);

    }

    private static boolean checkRoomIndexJoinMoney(ServerUser user, Integer roomIndex, boolean sendMessageFlag) {
        return true;

    }


    /**
     * 玩家加入指定房间
     */
    public void joinRoom(ServerUser user, int roomCode, String roomPassword, int roomIndex) {

        Integer oldRoomCode = GameUtil.joinRoomPre(user.getId(), roomCode);

        if (oldRoomCode != null && oldRoomCode.equals(roomCode)) { // 如果：已经在同一个房间里面了
            return;
        }

        // if (Arrays.asList(2, 12).contains(rootType) && PlayerManager.getPlayerVipLevel(user) < 4) {
        // NetManager.sendErrorMessageToClient("VIP4才能加入该房间", user);
        // return;
        // }
        // if (Arrays.asList(3, 13).contains(rootType) && PlayerManager.getPlayerVipLevel(user) < 8) {
        // NetManager.sendErrorMessageToClient("VIP8才能加入该房间", user);
        // return;
        // }

        // 判断玩家的金币：是否可以加入房间
        boolean flag = checkRoomIndexJoinMoney(user, roomIndex, true);

        if (!flag) {
            return;
        }

        if (roomCode == 0) {

            NewBaseFishingRoom gameRoom = getNewBaseFishingRoom(user, roomIndex, false);

            if (gameRoom != null) {
                synchronized (gameRoom) {
                    addRoomPlayer(gameRoom, user);
                }
            }

        } else {

            BaseGameRoom gameRoom = GameContainer.getGameRoomByCode(roomCode);

            if (!(gameRoom instanceof FishingChallengeRoom)) {
                NetManager.sendErrorMessageToClient("房间不存在", user);
                return;
            }

            FishingChallengeRoom room = (FishingChallengeRoom) gameRoom;

            if (room.getPlayerSize() >= room.getMaxSize()) {
                NetManager.sendErrorMessageToClient("房间人数已满", user);
                return;
            }

            // if (room.isVip() && PlayerManager.getPlayerVipLevel(user) < 4) {
            // NetManager.sendErrorMessageToClient("VIP4及以上玩家才能加入VIP房间", user);
            // return;
            // }

            if (((FishingChallengeRoom) gameRoom).isVerify()
                    && StrUtil.isBlank(((FishingChallengeRoom) gameRoom).getRoomPassword())) {

                boolean setPassword = this.setPassword(user, room.getCode(), user.getId(), roomPassword);

                if (!setPassword) {

                    return;

                }

            }

            if (((FishingChallengeRoom) gameRoom).isVerify() && !StrUtil.equals(roomPassword, room.getRoomPassword())) {
                NetManager.sendErrorMessageToClient("房间密码错误", user);
                return;
            }

            // 加入房间
            addRoomPlayer(room, user);

        }

    }

    /**
     * 获取：房间，寻找房间
     */
    @Nullable
    public static NewBaseFishingRoom getNewBaseFishingRoom(ServerUser user, int roomIndex, boolean robotPasswordFlag) {
        List<BaseGameRoom> gameRooms = GameContainer.getGameRooms();

        Map<Integer, List<NewBaseFishingRoom>> collectMap = gameRooms.stream().filter(it -> {

                    if (!(it instanceof NewBaseFishingRoom)) {
                        return false;
                    }

                    NewBaseFishingRoom room = (NewBaseFishingRoom) it;

                    if (room.getRoomIndex() == roomIndex && it.getMaxSize() > it.getPlayerSize()) {

                        if (robotPasswordFlag) {

                            return room.isRobotVerify(); // 要机器人密码房

                        } else {

                            return !room.isVerify();

                        }

                    }

                    return false;

                }) //
                .map(it -> (NewBaseFishingRoom) it).collect(Collectors.groupingBy(NewBaseFishingRoom::getPlayerSize,
                        Collectors.mapping(it -> it, Collectors.toList())));

        if (CollUtil.isEmpty(collectMap)) {

            NetManager.sendHintBoxMessageToClient("当前场次拥挤，请前往其他场次！", user, 10);

            return null;

        }

        if (robotPasswordFlag && collectMap.size() != 3) {

            if (RandomUtil.isHappen(30d)) {
                return null;
            }

        }

        // if (collectMap.size() >= 2) {
        //
        // collectMap.remove(0);
        //
        // }

        Integer currentPlayerSizeKey =
                cn.hutool.core.util.RandomUtil.randomEle(ArrayUtil.toArray(collectMap.keySet(), Integer.class));

        return cn.hutool.core.util.RandomUtil.randomEle(collectMap.get(currentPlayerSizeKey));

    }

    /**
     * 设置密码
     */
    public boolean setPassword(ServerUser user, int roomCode, long userId, String password) {

        TtmyFishingChallengeMessage.FishingChallengeSetPassWordResponse.Builder builder =
                TtmyFishingChallengeMessage.FishingChallengeSetPassWordResponse.newBuilder();

        FishingChallengeRoom gameRoom = GameContainer.getGameRoomByCode(roomCode);

        if (!password.matches("^\\d{6}$")) {// 是否为纯数字

            NetManager.sendErrorMessageToClient("请输入6位数字密码！", user);
            return false;

        }

        gameRoom.setRoomPassword(password);
        gameRoom.setVerify(true);

        builder.setPassword(password);
        builder.setRoomCode(roomCode);
        builder.setUserId(userId);

        NetManager.sendMessage(OseeMessage.OseeMsgCode.S_C_TTMY_FISHING_CHALLENGE_SET_PASSWORD_RESPONSE_VALUE, builder,
                user);

        return true;

    }


    // 需要记录使用数量的道具
    public static List<ItemId> JOIN_ITEM = Arrays.asList(ItemId.SKILL_FROZEN, ItemId.MAGIC_LAMP);

    /**
     * 更改炮台外观
     */
    public void changeBatteryView(FishingChallengeRoom gameRoom, FishingChallengePlayer player, int viewIndex) {

        if (viewIndex >= ItemId.QSZS_BATTERY_VIEW.getId() && viewIndex <= ItemId.SWHP_BATTERY_VIEW.getId()
                || viewIndex >= ItemId.ZLHP_BATTERY_VIEW.getId() && viewIndex <= ItemId.LBS_BATTERY_VIEW.getId()) { // 切换到自己购买的炮台外观

            if (PlayerManager.getItemNum(player.getUser(), ItemId.getItemIdById(viewIndex)) <= 0) {
                NetManager.sendHintMessageToClient("该炮台外观已到期", player.getUser());
                return;
            }

            RedisHelper.set("USE_BATTERYVIEW:" + player.getUser().getId(), String.valueOf(viewIndex));
            player.setViewIndex(viewIndex); // 设置外观

            TtmyFishingChallengeMessage.FishingChallengeChangeBatteryViewResponse.Builder builder =
                    TtmyFishingChallengeMessage.FishingChallengeChangeBatteryViewResponse.newBuilder();
            builder.setPlayerId(player.getId());
            builder.setViewIndex(player.getViewIndex());

            MyRefreshFishingUtil.sendRoomMessage(gameRoom,
                    OseeMessage.OseeMsgCode.S_C_TTMY_FISHING_CHALLENGE_CHANGE_BATTERY_VIEW_RESPONSE_VALUE, builder);

        } else if (PlayerManager.getPlayerVipLevel(player.getUser()) >= viewIndex) {

            RedisHelper.set("USE_BATTERYVIEW:" + player.getUser().getId(), String.valueOf(viewIndex));
            player.setViewIndex(viewIndex); // 设置外观
            TtmyFishingChallengeMessage.FishingChallengeChangeBatteryViewResponse.Builder builder =
                    TtmyFishingChallengeMessage.FishingChallengeChangeBatteryViewResponse.newBuilder();
            builder.setPlayerId(player.getId());
            builder.setViewIndex(player.getViewIndex());

            MyRefreshFishingUtil.sendRoomMessage(gameRoom,
                    OseeMessage.OseeMsgCode.S_C_TTMY_FISHING_CHALLENGE_CHANGE_BATTERY_VIEW_RESPONSE_VALUE, builder);

        } else {

            NetManager.sendHintMessageToClient("您的VIP等级不足，无法更改该炮台外观", player.getUser());

        }

    }

    /**
     * 更换炮台等级
     * <p>
     * 备注：如果是翻倍技能使用期间，切换的炮倍，则不重新生成曲线
     */
    public static void changeBatteryLevel(NewBaseFishingRoom room, FishingChallengePlayer player, long targetLevel,
                                          boolean sendFlag) {

        boolean doubleFlag = false; // 是否是：翻倍技能使用期间

        // if (System.currentTimeMillis() - player.getLastDoubleTime() < SKILL_DOUBLE_TIME) {
        //
        // doubleFlag = true;
        //
        // }

        if (targetLevel == player.getBatteryLevel() && !doubleFlag) {

            return;

        }

        OseePlayerEntity playerEntity = PlayerManager.getPlayerEntity(player.getUser());

        // 炮倍不能超过限制
        targetLevel = getBatteryLevel(room.getRoomIndex(), targetLevel);

        String reasonStr;

        if (doubleFlag) {

            targetLevel = targetLevel * 2; // 炮倍翻倍

            reasonStr = "切换炮倍-翻倍：" + targetLevel;

        } else {

            reasonStr = "切换炮倍：" + targetLevel;

        }

        synchronized (playerEntity) {

            Long oldBatteryLevel;

            if (doubleFlag) {
                oldBatteryLevel = player.getBatteryLevel();
            } else {
                oldBatteryLevel = null;
            }

            // 改变玩家当前炮台等级
            player.setBatteryLevel(targetLevel);

            // log.info("batteryLevel-1：{}，targetLevel：{}", player.getBatteryLevel(), targetLevel);

            // 处理：炮倍等级切换
            int clearType = FishingChallengeUtil.handlerBatteryLevelChange(player, targetLevel, room, false, reasonStr,
                    playerEntity, oldBatteryLevel);

            // 重新生成曲线：0 未清除曲线 1 需要清除所有曲线 2 需要清除当前节点以及后续的曲线
            if (clearType == 1 || clearType == 2) {

                // 模拟开一枪，然后重新生成节点
                generateNewJczd0List(playerEntity, player.getUser(), null, false, false);

            } else {

                // 节点变化时，清除 aq相关
                cleanAqData(player.getId());

            }

        }

        if (sendFlag) {

            TtmyFishingChallengeMessage.FishingChallengeChangeBatteryLevelResponse.Builder builder =
                    TtmyFishingChallengeMessage.FishingChallengeChangeBatteryLevelResponse.newBuilder();

            builder.setPlayerId(player.getId());
            builder.setLevel((int) player.getBatteryLevel());

            MyRefreshFishingUtil.sendRoomMessage(room,
                    OseeMessage.OseeMsgCode.S_C_TTMY_FISHING_CHALLENGE_CHANGE_BATTERY_LEVEL_RESPONSE_VALUE, builder);

        }

    }

    /**
     * 模拟开一枪，然后重新生成节点
     */
    public static NewBaseGamePlayer generateNewJczd0List(OseePlayerEntity playerEntity, ServerUser user,
                                                         @Nullable String reasonStr, boolean currentHsFlag, boolean userControllerFlag) {

        long userId = playerEntity.getUserId();

        BaseGamePlayer basePlayer = GameContainer.getPlayerById(userId);

        NewBaseGamePlayer player;

        NewBaseGameRoom room;

        if (basePlayer instanceof NewBaseGamePlayer) { // 如果：在房间里

            player = ((NewBaseGamePlayer) basePlayer);

            room = GameContainer.getGameRoomByCode(player.getRoomCode());

        } else { // 如果不在房间里，则构建一个 player对象，并 取第一个场次作为房间

            room = FishingChallengeManager.ROOM_ONE;

            player = new FishingChallengePlayer();

            player.setUser(user);

            // 获取：场次的最低炮倍
            long level = FishingChallengeManager.getBatteryLevel(room.getRoomIndex(), 0);

            player.setBatteryLevel(level); // 备注：这里可以随便取一个炮倍，因为这里锁定的是金币的值

            player.setLastBatteryLevel(level);

        }

        // 是否修改过 jczd0List
        String personalJczd0ListBatteryLevel = redissonClient.<String>getBucket(
                FishingChallengeFightFishUtil.FISHING_PERSONAL_JCZD0_LIST_BATTERY_LEVEL_USER_PRE + userId).get();

        if (StrUtil.isNotBlank(personalJczd0ListBatteryLevel)) { // 如果：修改过节点的值

            return player;

        }

        long batteryLevel = player.getBatteryLevel();

        // 期望子弹数
        RBucket<Double> jczd0Bucket =
                redissonClient.getBucket(FishingChallengeFightFishUtil.FISHING_JCZD0_USER_PRE + player.getId());

        // 当前子弹数
        long jczd1 =
                FishingChallengeFightFishUtil.getJczd1(playerEntity.getDragonCrystal(), batteryLevel, player.getId());

        // 当前命中状态：1 爆发 2 回收
        RBucket<Integer> hitStateBucket = redissonClient.getBucket(FISHING_HIT_STATE_USER_PRE + player.getId());

        // 获取：当前命中状态：1 爆发 2 回收
        Integer hitState = getHitState(hitStateBucket);

        CallBack<Integer> hitStateCallBack = new CallBack<>(hitState);


        ProfitRatioDTO profitRatioDTO = FishingTUtil.getProfitRatioDTO();

        int index = profitRatioDTO.getIndexByRoomIndex(room.getRoomIndex());

        // 初始的：回收权重
        double chuShiHsWeight = profitRatioDTO.getChuShiHsWeightArr()[index];

        // 过程的：回收权重
        double guoChengHsWeight = profitRatioDTO.getGuoChengHsWeightArr()[index];

        // 随机取值的权重
        double randomWeight = profitRatioDTO.getBfxyRandomWeightArr()[index];
/*
        FishingChallengeFightFishUtil.setNewJczd0(player, jczd0Bucket, jczd1, hitStateBucket, room, hitStateCallBack,
                cclsyktf, playerEntity, bdfz, bd, chuShiHsWeight, guoChengHsWeight, randomWeight, reasonStr, true,
                personalJczd0ListBatteryLevel, batteryLevel, currentHsFlag, null, index, false, userControllerFlag, null);*/

        return player;

    }

    private static final Map<Integer, List<Integer>> CHALLENGE_BATTERY_LEVEL_MAP_LIST = MapUtil.newHashMap();

    /**
     * 炮倍不能超过限制
     */
    public static long getBatteryLevel(int roomIndex, long batteryLevel) {
        if (CollUtil.isEmpty(CHALLENGE_BATTERY_LEVEL_MAP_LIST.get(roomIndex))) {
            List<BatteryLevelLjConfig> batteryLevelLjConfigList = DataContainer.getDatas(BatteryLevelLjConfig.class);
            for (BatteryLevelLjConfig item : batteryLevelLjConfigList) {
                CHALLENGE_BATTERY_LEVEL_MAP_LIST.computeIfAbsent(item.getScene(), k -> new ArrayList<>()).add((int) item.getBatteryLevel());
            }
        }
        List<Integer> batteryLevelScopeList = CHALLENGE_BATTERY_LEVEL_MAP_LIST.get(roomIndex);
        if (CollUtil.isEmpty(batteryLevelScopeList)) {
            return 1;
        }
        if (batteryLevel < batteryLevelScopeList.get(0)) {
            return batteryLevelScopeList.get(0);
        } else if (batteryLevel > batteryLevelScopeList.get(batteryLevelScopeList.size() - 1)) {
            return batteryLevelScopeList.get(batteryLevelScopeList.size() - 1);
        }
        return batteryLevel;

    }

    /**
     * 玩家发射子弹
     */
    public void playerFire(FishingChallengeRoom gameRoom, FishingChallengePlayer player, FireStruct fire) {

        player.getUser().getEntity().setOnlineState(gameRoom.getGameId()); // 防止：在线状态不准确

        player.setLastFireTime(System.currentTimeMillis());
//        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        log.info("执行2：{} : {} ",player.getId(), simpleDateFormat.format(new Date()));
        fire.setLevel(player.getBatteryLevel());
        fire.setMult(player.getBatteryMult());
        player.getFireMap().put(fire.getId(), fire);

        OseePlayerEntity oseePlayerEntity = PlayerManager.getPlayerEntity(UserContainer.getUserById(player.getId()));

        // 广播玩家发送子弹响应
        doFishingChallengeFireResponse(gameRoom, player, fire.getId(), fire.getFishId(), fire.getAngle(),
                oseePlayerEntity.getDragonCrystal(), oseePlayerEntity.getDiamond());

    }

    public void sendRoomFishMult(FishingChallengeRoom gameRoom, FishingChallengePlayer player) {

        gameRoom.getFishMap().forEach((k, v) -> {

            // FishConfig config = DataContainer.getData(k, FishConfig.class);

            // if (Arrays.asList(35, 37).contains(config.getModelId())) { // 刷新了一条特殊鱼

            // final RMap<Integer, Number> rMap = RedisHelper.redissonClient
            // .getMap(String.format(USER_ID_MODEID_MULT, gameRoom.getCode()), new JsonJacksonCodec());

            // // 同步
            // sendRoomFishMult(gameRoom, config, v, rMap.getOrDefault(v.getId(), 300L).longValue());

            // }

        });

    }

    /**
     * 二次伤害鱼数据同步
     */
    public static void secondaryFishSendInfo(FishingChallengeRoom gameRoom,
                                             TtmyFishingChallengeMessage.FishingChallengeDoubleKillResponse.Builder builder) {

        MyRefreshFishingUtil.sendRoomMessage(gameRoom,
                OseeMessage.OseeMsgCode.S_C_FISHING_CHALLENGE_DOUBLE_KILL_RESPONSE_VALUE, builder);

    }

    /**
     * 获取：需要扣除的钱
     */
    public static long getNeedMoney(long batteryLevel, long currentTimeMillis, FishingGamePlayer player,
                                    NewBaseGameRoom room) {

        long needMoney = batteryLevel;

        needMoney = handlerNeedMoney(currentTimeMillis, player, needMoney, room);

        return needMoney;

    }

    /**
     * 处理：需要的钱
     */
    private static long handlerNeedMoney(long currentTimeMillis, FishingGamePlayer player, long needMoney,
                                         NewBaseGameRoom room) {

        // 根据技能情况，乘以倍数
        return multDelta(player, currentTimeMillis, needMoney, room);

    }


    /**
     * 获取：本次赢的钱
     */
    public static long getWinMoney(Long randomMoney, long batteryLevel) {

        return randomMoney * batteryLevel;

    }

    /**
     * 个控
     *
     * @param userId      用户id
     * @param personalNum 个控值
     * @param fishType    鱼类型
     * @return 用户T的状态
     */
    public static UserStatusT singleT(Long userId, Long personalNum, Integer fishType) {

        String burstTmax = RedisHelper.get("USER_PERSONAL_CONTROL_BURST_TMAX_CHALLENGE" + userId);
        Integer[] burstTmax_obj = {0, 0, 0, -40};
        if (burstTmax != null || burstTmax.length() != 0) {
            burstTmax = burstTmax.substring(burstTmax.lastIndexOf("[") + 1).replaceAll("]", "");
            String[] q0 = burstTmax.split(",");
            List<Integer> list = new ArrayList<>();
            for (String q : q0) {
                if (q == null || "".equals(q)) {
                    continue;
                }
                list.add(new Double(Double.parseDouble(q)).intValue());
            }
            burstTmax_obj = list.toArray(new Integer[0]);
        }

        String burstTmin = RedisHelper.get("USER_PERSONAL_CONTROL_BURST_TMIN_CHALLENGE" + userId);
        Integer[] burstTmin_obj = {-20, -30, -35, -75};
        if (burstTmin != null || burstTmin.length() != 0) {
            burstTmin = burstTmin.substring(burstTmin.lastIndexOf("[") + 1).replaceAll("]", "");
            String[] q0 = burstTmin.split(",");
            List<Integer> list = new ArrayList<>();
            for (String q : q0) {
                if (q == null || "".equals(q)) {
                    continue;
                }
                list.add(new Double(Double.parseDouble(q)).intValue());
            }
            burstTmin_obj = list.toArray(new Integer[0]);
        }

        String recoveryTmax = RedisHelper.get("USER_PERSONAL_CONTROL_RECOVERY_TMAX_CHALLENGE" + userId);
        Integer[] recoveryTmax_obj = {20, 30, 35, 100};
        if (recoveryTmax != null || recoveryTmax.length() != 0) {
            recoveryTmax = recoveryTmax.substring(recoveryTmax.lastIndexOf("[") + 1).replaceAll("]", "");
            String[] q0 = recoveryTmax.split(",");
            List<Integer> list = new ArrayList<>();
            for (String q : q0) {
                if (q == null || "".equals(q)) {
                    continue;
                }
                list.add(new Double(Double.parseDouble(q)).intValue());
            }
            recoveryTmax_obj = list.toArray(new Integer[0]);
        }

        String recoveryTmin = RedisHelper.get("USER_PERSONAL_CONTROL_RECOVERY_TMIN_CHALLENGE" + userId);
        Integer[] recoveryTmin_obj = {0, 0, 0, 50};
        if (recoveryTmin != null || recoveryTmin.length() != 0) {
            recoveryTmin = recoveryTmin.substring(recoveryTmin.lastIndexOf("[") + 1).replaceAll("]", "");
            String[] q0 = recoveryTmin.split(",");
            List<Integer> list = new ArrayList<>();
            for (String q : q0) {
                if (q == null || "".equals(q)) {
                    continue;
                }
                list.add(new Double(Double.parseDouble(q)).intValue());
            }
            recoveryTmin_obj = list.toArray(new Integer[0]);
        }

        if (personalNum > 0) {

            return new UserStatusT(UserStatusT.StatusT.SINGLE,
                    RandomUtil.getRandom(burstTmin_obj[fishType - 1], burstTmax_obj[fishType - 1] + 1), (byte) 1);

        } else if (personalNum < 0) {

            return new UserStatusT(UserStatusT.StatusT.SINGLE,
                    RandomUtil.getRandom(recoveryTmin_obj[fishType - 1], recoveryTmax_obj[fishType - 1] + 1), (byte) -1);

        }

        return null;

    }

    /**
     * 发送重新激活消息相关消息
     */
    public static void sendReactiveMessage(FishingChallengeRoom gameRoom, FishingChallengePlayer gamePlayer) {

        // sendJoinRoomResponse(gameRoom, gamePlayer); // 发送加入房间消息
        sendRoomPlayerInfoListResponse(gameRoom, gamePlayer); // 发送玩家数据列表消息
        sendSynchroniseResponse(gameRoom, gamePlayer); // 发送同步鱼消息
        sendFrozenMessage(gameRoom, gamePlayer); // 发送房间当前冰冻消息

    }

    /**
     * 玩家使用技能
     */
    public static void useSkill(NewBaseFishingRoom room, FishingChallengePlayer player, int skillId, long routeId,
                                @Nullable List<Long> fishIdList) {

        TtmyFishingChallengeMessage.FishingChallengeUseSkillResponse.Builder builder =
                TtmyFishingChallengeMessage.FishingChallengeUseSkillResponse.newBuilder();

        builder.setRequestPlayerId(player.getId());

        FishingHelper.useSkill(room, player, skillId, routeId, fishIdList, new FishingHelper.UseSkillBuilderHelper() {

            @Override
            public void setId(long userId, int skillId) {

                builder.setSkillId(skillId);
                builder.setPlayerId(player.getId());

            }

            @Override
            public void setDuration(int duration) {

                builder.setDuration(duration);

            }

            @Override
            public void addFishIds(long fishId) {

                builder.addFishIds(fishId);

            }

            @Override
            public void addRemainDurations(int remainDuration) {

                builder.addRemainDurations(remainDuration);

            }

            @Override
            public void setSkillFishId(long routeId) {

                builder.setSkillFishId(routeId);

            }

            @Override
            public void setRestMoney(long restMoney) {

                builder.setRestMoney(restMoney);

            }

            @Override
            public void send(NewBaseFishingRoom room) {

                MyRefreshFishingUtil.sendRoomMessage(room,
                        OseeMessage.OseeMsgCode.S_C_TTMY_FISHING_CHALLENGE_USE_SKILL_RESPONSE_VALUE, builder);

            }

            @Override
            public void setNum1(int num1) {

                builder.setNum1(num1);

            }

        });

    }

    /**
     * 捕捉到特殊鱼
     */
    public void catchSpecialFish(FishingChallengeRoom gameRoom, long playerId, List<Long> fishIdsList,
                                 long specialFishId) {

    }

    /**
     * 玩家重连
     */
    public void reconnect(FishingChallengeRoom gameRoom, FishingChallengePlayer gamePlayer) {

    }

    /**
     * VIP换座
     */
    public void changeSeat(FishingChallengeRoom gameRoom, FishingChallengePlayer player, int seat) {
        if (seat < 0 || seat >= gameRoom.getMaxSize()) {
            NetManager.sendErrorMessageToClient("座位序号有误", player.getUser());
            return;
        }
        // if (!gameRoom.isVip()) {
        // NetManager.sendErrorMessageToClient("VIP房间才能换座", player.getUser());
        // return;
        // }
        synchronized (gameRoom) {
            if (gameRoom.getGamePlayerBySeat(seat) != null) {
                NetManager.sendErrorMessageToClient("该座位已经有玩家了哦", player.getUser());
                return;
            }
            // 当前座位号
            int nowSeat = player.getSeat();
            gameRoom.getGamePlayers()[nowSeat] = null; // 清空之前座位的玩家信息
            gameRoom.getGamePlayers()[seat] = player; // 将自己的信息移到新座位
            gameRoom.getGamePlayers()[seat].setSeat(seat); // 玩家设置新座位号
            sendRoomPlayerInfoResponse(gameRoom, player);
        }
    }

    public void openJc(int type, Long userId, FishingChallengeRoom room, FishingChallengePlayer player) {

        TtmyFishingChallengeMessage.OpenJcResponse.Builder builder =
                TtmyFishingChallengeMessage.OpenJcResponse.newBuilder();
        long reword = 0L;
        FishJc fishJc = new FishJc();
        fishJc.setCreateTime(new Date());
        fishJc.setUserId(player.getUser().getId());
        fishJc.setVip(PlayerManager.getPlayerVipLevel(player.getUser()));
        fishJc.setNickName(player.getUser().getUsername());
        fishJc.setType(type);

        String sufStr = "";

        if (type == 1) {

            sufStr = "_SMALL";

        } else if (type == 2) {

            sufStr = "_BIG";

        }

        int roomIndex = room.getRoomIndex();

        String openKey = "USER_JC_OPEN_NUM_CHALLANGE_" + roomIndex;

        if (StrUtil.isNotBlank(sufStr)) {

            long num = RedisUtil.val(openKey + player.getUser().getId(), 0L);

            if (num < 100) {

                NetManager.sendErrorMessageToClient("海神令数量不足，无法开启奖池！", player.getUser());
                return;

            }

            fishJc.setRoomIndex(roomIndex);

            String key = "ALL_JC_USER_CHALLANGE_" + roomIndex + sufStr;

            long jcAll = RedisUtil.val(key, 0L);

            int a = ThreadLocalRandom.current().nextInt(1, 101);

            if (a < 23) {

                reword = new Double(jcAll * 0.01).longValue();
                RedisHelper.set(key, String.valueOf(jcAll - reword));
                fishJc.setNum(1);
                fishJc.setNum1(reword);

            } else if (a < 41) {

                reword = new Double(jcAll * 0.03).longValue();
                RedisHelper.set(key, String.valueOf(jcAll - reword));
                fishJc.setNum(3);
                fishJc.setNum1(reword);

            } else if (a < 57) {

                reword = new Double(jcAll * 0.05).longValue();
                RedisHelper.set(key, String.valueOf(jcAll - reword));
                fishJc.setNum(5);
                fishJc.setNum1(reword);

            } else if (a < 69) {

                reword = new Double(jcAll * 0.08).longValue();
                RedisHelper.set(key, String.valueOf(jcAll - reword));
                fishJc.setNum(8);
                fishJc.setNum1(reword);

            } else if (a < 79) {

                reword = new Double(jcAll * 0.1).longValue();
                RedisHelper.set(key, String.valueOf(jcAll - reword));
                fishJc.setNum(10);
                fishJc.setNum1(reword);

            } else if (a < 87) {

                reword = new Double(jcAll * 0.15).longValue();
                RedisHelper.set(key, String.valueOf(jcAll - reword));
                fishJc.setNum(15);
                fishJc.setNum1(reword);

            } else if (a < 93) {

                reword = new Double(jcAll * 0.2).longValue();
                RedisHelper.set(key, String.valueOf(jcAll - reword));
                fishJc.setNum(20);
                fishJc.setNum1(reword);

            } else if (a < 97) {

                reword = new Double(jcAll * 0.25).longValue();
                RedisHelper.set(key, String.valueOf(jcAll - reword));
                fishJc.setNum(25);
                fishJc.setNum1(reword);

            } else if (a < 99) {

                reword = new Double(jcAll * 0.3).longValue();
                RedisHelper.set(key, String.valueOf(jcAll - reword));
                fishJc.setNum(30);
                fishJc.setNum1(reword);

            } else {

                reword = new Double(jcAll * 0.35).longValue();
                RedisHelper.set(key, String.valueOf(jcAll - reword));
                fishJc.setNum(35);
                fishJc.setNum1(reword);

            }

            playerMapper.saveJc(fishJc);
            RedisHelper.set("ALL_JC_USER_CHALLANGE_" + roomIndex + player.getUser().getId(), String.valueOf(num - 100));

        }

        builder.setType(type);
        builder.setUserId(userId);
        builder.setMoney(reword);

        PlayerManager.addItem(UserContainer.getUserById(userId), ItemId.DRAGON_CRYSTAL, reword,
                ItemChangeReason.USE_ITEM, true);

        builder.setNum(RedisUtil.val(openKey + player.getUser().getId(), 0L));

        NetManager.sendMessage(OseeMessage.OseeMsgCode.S_C_TTMY_OPEN_JC_RESPONSE_VALUE, builder, player.getUser());

    }

    public void getJcAllMoney(int type, Long userId, FishingChallengeRoom room, FishingChallengePlayer player) {

        TtmyFishingChallengeMessage.GetJcAllMoneyResponse.Builder builder =
                TtmyFishingChallengeMessage.GetJcAllMoneyResponse.newBuilder();

        builder.setType(type);
        builder.setUserId(userId);

        int roomIndex = room.getRoomIndex();

        String openKey = "USER_JC_OPEN_NUM_CHALLANGE_" + roomIndex;

        builder.setNum(RedisUtil.val(openKey + player.getUser().getId(), 0L));

        String sufStr = "";

        if (type == 1) {

            sufStr = "_SMALL";

        } else if (type == 2) {

            sufStr = "_BIG";

        }

        if (StrUtil.isNotBlank(sufStr)) {

            String key = "ALL_JC_USER_CHALLANGE_" + roomIndex + sufStr;

            builder.setMoney(RedisUtil.val(key, 0L));

        }

        NetManager.sendMessage(OseeMessage.OseeMsgCode.S_C_TTMY_GET_JC_ALL_MONEY_RESPONSE_VALUE, builder,
                player.getUser());

    }

    public void getJcRecord(Long userId, FishingChallengeRoom gameRoom, FishingChallengePlayer player) {
        TtmyFishingChallengeMessage.GetJcAllRecordResponse.Builder builder =
                TtmyFishingChallengeMessage.GetJcAllRecordResponse.newBuilder();
        // if (room1.containsKey(player.getUser())) {
        // List<FishJc> list = playerMapper.getAllJc(gameRoom.getRoomIndex());
        // for (FishJc fishJc : list) {
        // TtmyFishingChallengeMessage.JcRecord.Builder builder1 = TtmyFishingChallengeMessage.JcRecord.newBuilder();
        // SimpleDateFormat sdf = new SimpleDateFormat("MM/dd HH:mm");
        // builder1.setCreateTime(sdf.format(fishJc.getCreateTime()));
        // builder1.setNickName(fishJc.getNickName().substring(0, 2) + "***");
        // builder1.setNum(fishJc.getNum());
        // builder1.setNum1(fishJc.getNum1());
        // builder1.setUserId(fishJc.getUserId());
        // builder1.setVip(fishJc.getVip());
        // builder1.setType(fishJc.getType());
        // builder1.setHeadIndex(UserContainer.getUserById(userId).getEntity().getHeadIndex());
        // builder.addJcRecord(builder1);
        // }
        // }
        // else if (room2.containsKey(player.getUser())) {
        // List<FishJc> list = playerMapper.getAllJc(gameRoom.getRoomIndex());
        // for (FishJc fishJc : list) {
        // TtmyFishingChallengeMessage.JcRecord.Builder builder1 = TtmyFishingChallengeMessage.JcRecord.newBuilder();
        // SimpleDateFormat sdf = new SimpleDateFormat("MM/dd HH:mm");
        // builder1.setCreateTime(sdf.format(fishJc.getCreateTime()));
        // builder1.setNickName(fishJc.getNickName().substring(0, 2) + "***");
        // builder1.setNum(fishJc.getNum());
        // builder1.setNum1(fishJc.getNum1());
        // builder1.setUserId(fishJc.getUserId());
        // builder1.setVip(fishJc.getVip());
        // builder1.setType(fishJc.getType());
        // builder1.setHeadIndex(UserContainer.getUserById(userId).getEntity().getHeadIndex());
        // builder.addJcRecord(builder1);
        // }
        // }
        // else if (room3.containsKey(player.getUser())) {
        // List<FishJc> list = playerMapper.getAllJc(gameRoom.getRoomIndex());
        // for (FishJc fishJc : list) {
        // TtmyFishingChallengeMessage.JcRecord.Builder builder1 = TtmyFishingChallengeMessage.JcRecord.newBuilder();
        // SimpleDateFormat sdf = new SimpleDateFormat("MM/dd HH:mm");
        // builder1.setCreateTime(sdf.format(fishJc.getCreateTime()));
        // builder1.setNickName(fishJc.getNickName().substring(0, 2) + "***");
        // builder1.setNum(fishJc.getNum());
        // builder1.setNum1(fishJc.getNum1());
        // builder1.setUserId(fishJc.getUserId());
        // builder1.setVip(fishJc.getVip());
        // builder1.setType(fishJc.getType());
        // builder1.setHeadIndex(UserContainer.getUserById(userId).getEntity().getHeadIndex());
        // builder.addJcRecord(builder1);
        // }
        // }
        List<FishJc> list = playerMapper.getAllJc(gameRoom.getRoomIndex());
        for (FishJc fishJc : list) {
            TtmyFishingChallengeMessage.JcRecord.Builder builder1 = TtmyFishingChallengeMessage.JcRecord.newBuilder();
            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd HH:mm");
            builder1.setCreateTime(sdf.format(fishJc.getCreateTime()));
            builder1.setNickName(fishJc.getNickName().substring(0, 2) + "***");
            builder1.setNum(fishJc.getNum());
            builder1.setNum1(fishJc.getNum1());
            builder1.setUserId(fishJc.getUserId());
            builder1.setVip(fishJc.getVip());
            builder1.setType(fishJc.getType());
            builder1.setHeadIndex(UserContainer.getUserById(userId).getEntity().getHeadIndex());
            builder.addJcRecord(builder1);
        }
        NetManager.sendMessage(OseeMessage.OseeMsgCode.S_C_TTMY_GET_JC_RECORD_RESPONSE_VALUE, builder,
                player.getUser());
    }

    /**
     * 二次伤害鱼捕获鱼
     */
    public void doubleKillFishs(FishingChallengeRoom gameRoom, long playerId, List<Long> fishIdsList) {

        NewBaseGamePlayer player = gameRoom.getGamePlayerById(playerId);

        if (player == null) {
            return;
        }

        TtmyFishingChallengeMessage.FishingChallengeDoubleKillFishResponse.Builder builder =
                TtmyFishingChallengeMessage.FishingChallengeDoubleKillFishResponse.newBuilder();

        builder.setUserId(playerId);

        for (Long fishId : fishIdsList) {

            // 鱼id不存在
            if (!gameRoom.getFishMap().containsKey(fishId)) {
                continue;
            }

            FishStruct fish = gameRoom.getFishMap().get(fishId);
            FishConfig config = DataContainer.getData(fish.getConfigId(), FishConfig.class);
            if (Arrays.asList(36, 37, 54, 55, 56).contains(config.getModelId())) { // 二次伤害击杀了特殊鱼
                secondaryDamageKillsSpecialFish(gameRoom, playerId, fishId, config);
            }

            gameRoom.getFishMap().remove(fishId);
//            gameRoom.removeFishMap(fishId);

            builder.addFishds(fishId);
            TtmyFishingChallengeMessage.FishingChallengeFightFishResponse.Builder builder1 =
                    TtmyFishingChallengeMessage.FishingChallengeFightFishResponse.newBuilder();
            // MyRefreshFishingUtil.sendRoomMessage(gameRoom,
            // OseeMessage.OseeMsgCode.S_C_TTMY_FISHING_CHALLENGE_FIGHT_FISH_RESPONSE_VALUE, builder);
            builder1.setFishId(fishId);
            builder1.setPlayerId(playerId);
            builder1.setRestMoney(player.getMoney());
            builder1.setDropMoney(0);

            // builder1.setType(0);
            MyRefreshFishingUtil.sendRoomMessage(gameRoom,
                    OseeMessage.OseeMsgCode.S_C_TTMY_FISHING_CHALLENGE_FIGHT_FISH_RESPONSE_VALUE, builder1);

        }

        MyRefreshFishingUtil.sendRoomMessage(gameRoom,
                OseeMessage.OseeMsgCode.S_C_FISHING_CHALLENGE_DOUBLE_KILL_FISH_RESPONSE_VALUE, builder);

    }

    /**
     * 二次伤害鱼击杀特殊鱼
     *
     * @param gameRoom
     * @param playerId
     * @param fishId
     * @param config
     */
    private void secondaryDamageKillsSpecialFish(FishingChallengeRoom gameRoom, long playerId, Long fishId,
                                                 FishConfig config) {
        final ServerUser user = UserContainer.getUserById(playerId);
        final BaseGamePlayer player = GameContainer.getPlayerById(playerId);

        Long randomMoney = config.getMaxMoney() > config.getMoney()
                ? ThreadLocalRandom.current().nextLong(config.getMoney(), config.getMaxMoney() + 1) : config.getMoney();

        final long batteryLevel = PlayerManager.getPlayerEntity(user).getBatteryLevel();
        final int batteryMult = 1;
        long winMoney = randomMoney * batteryLevel;

        if (config.getModelId() == 37) { // 宝藏轮盘
            final List<Integer> bomb = Arrays.asList(2100, 2500, 3500, 4300, 4600);
            final List<Integer> nei = Arrays.asList(0, 400, 800);
            final List<Integer> zhong = Arrays.asList(0, 1200, 2000, 2100, 2500);
            final List<Integer> wai = Arrays.asList(0, 3200, 3500, 4000, 4300, 4600, 6000);

            Integer random = RandomUtil.getRandom(nei);
            if (random == 0) {
                random = RandomUtil.getRandom(zhong);
            }
            if (random == 0) {
                random = RandomUtil.getRandom(wai);
            }
            if (bomb.contains(random)) { // 随机到炸弹
                randomMoney = random.longValue();
                winMoney = 0L;
            } else if (random == 0) {
                random = RandomUtil.getRandom(6000, 10001);
                randomMoney = random.longValue();
                winMoney = randomMoney * batteryLevel * batteryMult;
            }

        } else if (config.getModelId() == 36) { // 炸弹
            randomMoney = gameRoom.getFishMap().values().stream()
                    .map(f -> DataContainer.getData(f.getConfigId(), FishConfig.class))
                    .filter(c -> !Arrays.asList(36, 37, 54, 55, 56).contains(c.getModelId()))
                    .map(c -> c.getMaxMoney() > c.getMoney()
                            ? ThreadLocalRandom.current().nextLong(c.getMoney(), c.getMaxMoney() + 1) : c.getMoney())
                    .reduce(Long::sum).orElse(0L);

            winMoney = randomMoney * batteryLevel * batteryMult;
        }

        if (config.getModelId() == 37) { // 宝藏轮盘
            TtmyFishingChallengeMessage.FishingChallengeDoubleKillResponse.Builder builder =
                    TtmyFishingChallengeMessage.FishingChallengeDoubleKillResponse.newBuilder();
            builder.setName(config.getName());
            builder.setModelId(fishId);
            builder.setUserId(player.getUser().getId());
            builder.setMult(randomMoney);
            builder.setWinMoney(winMoney);
            builder.setNum1((int) batteryLevel);
            secondaryFishSendInfo(gameRoom, builder);
        } else if (config.getModelId() == 36) { // 炸弹
            TtmyFishingChallengeMessage.FishingChallengeDoubleKillResponse.Builder builder =
                    TtmyFishingChallengeMessage.FishingChallengeDoubleKillResponse.newBuilder();
            builder.setName(config.getName());
            builder.setModelId(fishId);
            builder.setUserId(player.getUser().getId());
            builder.setWinMoney(winMoney);
            builder.setMult(randomMoney);
            secondaryFishSendInfo(gameRoom, builder);
        } else if (config.getModelId() == 54) { // 人鱼女皇
            TtmyFishingChallengeMessage.FishingChallengeDoubleKillResponse.Builder builder =
                    TtmyFishingChallengeMessage.FishingChallengeDoubleKillResponse.newBuilder();
            builder.setName(config.getName());
            builder.setModelId(fishId);
            builder.setUserId(player.getUser().getId());
            builder.setWinMoney(winMoney);
            builder.setMult(randomMoney);

            secondaryFishSendInfo(gameRoom, builder);

        } else if (config.getModelId() == 55) { // 玄武
            TtmyFishingChallengeMessage.FishingChallengeDoubleKillResponse.Builder builder =
                    TtmyFishingChallengeMessage.FishingChallengeDoubleKillResponse.newBuilder();
            builder.setName(config.getName());
            builder.setModelId(fishId);
            builder.setUserId(player.getUser().getId());
            builder.setWinMoney(winMoney);
            builder.setMult(randomMoney);
            secondaryFishSendInfo(gameRoom, builder);
        } else if (config.getModelId() == 56) { // 金龙
            TtmyFishingChallengeMessage.FishingChallengeDoubleKillResponse.Builder builder =
                    TtmyFishingChallengeMessage.FishingChallengeDoubleKillResponse.newBuilder();
            builder.setName(config.getName());
            builder.setModelId(fishId);
            builder.setUserId(player.getUser().getId());
            builder.setWinMoney(winMoney);
            builder.setMult(randomMoney);
            secondaryFishSendInfo(gameRoom, builder);
        }
    }

    /**
     * 二次伤害鱼结束
     */
    public void doubleKillEnd(FishingChallengeRoom gameRoom, FishingChallengePlayer player, Long winMoney, long mult,
                              long fishConfigId, long userId) {

        // 游走字幕
        FishingHelper.wanderSubtitle(gameRoom, player, winMoney, mult, fishConfigId, 9, 0, userId);

        // 通报
        FishingHelper.notification(gameRoom, player, winMoney, mult, fishConfigId, 0, userId);

        RedisHelper.set("DOUBLE_KILL_WINMONEY_CHALLENGE" + player.getUser().getId(), "0");

        // 保存：击杀 boss日志
        saveKillBoss(gameRoom, player, winMoney, mult, fishConfigId, userId);

    }

    public static final Set<Integer> DOUBLE_KILL_END_SAVE_KILL_BOSS_FISH_MODEL_ID_SET =
            CollUtil.newHashSet(101068, 101074);

    /**
     * 保存：击杀 boss日志
     */
    private void saveKillBoss(FishingChallengeRoom gameRoom, FishingChallengePlayer player, Long winMoney, long mult,
                              long fishConfigId, long userId) {

        FishConfig fishConfig = DataContainer.getData(fishConfigId, FishConfig.class);

        if (fishConfig == null) {
            return;
        }

        if (fishConfig.getFishType() != 100) {
            return;
        }

        if (!DOUBLE_KILL_END_SAVE_KILL_BOSS_FISH_MODEL_ID_SET.contains(fishConfig.getModelId())) {
            return;
        }

        long batteryLevel = winMoney / mult; // 炮倍

        KillBossEntity killBossEntity = new KillBossEntity();

        killBossEntity.setUserId(userId);
        killBossEntity.setNickName(player.getUser().getNickname());
        killBossEntity.setBatterLevel(batteryLevel);
        killBossEntity.setCreateTime(new Date());
        killBossEntity.setBossName(fishConfig.getName());
        killBossEntity.setMult(mult);
        killBossEntity.setRoomIndex(gameRoom.getRoomIndex());
        killBossEntity.setBloodPoolFloatKillValue(null);
        killBossEntity.setAward(winMoney + "金币");

        playerMapper.saveKillBoss(killBossEntity); // 记录击杀Boss鱼日志

    }

    private long getPoint(String key, String playerId) {

        Double point = RedisUtil.zScore(key, playerId);
        return point == null ? 0 : point.longValue();

    }

    /**
     * 发送同步锁定
     */
    public void sendSyncLockResponse(TtmyFishingChallengeMessage.FishingChallengeSyncLockResponse.Builder response,
                                     FishingChallengeRoom gameRoom) {
        if (gameRoom != null) {
            MyRefreshFishingUtil.sendRoomMessage(gameRoom,
                    OseeMessage.OseeMsgCode.S_C_FISHING_CHALLENGE_SYNC_LOCK_RESPONSE_VALUE, response);
        }
    }

    /**
     * 玩家在房间使用鱼雷
     */
    public void useTorpedo(ServerUser user,
                           List<TtmyFishingChallengeMessage.FishingChallengeUseTorpedo> fishingChallengeUseTorpedoList) {

        TtmyFishingChallengeMessage.FishingChallengeUseTorpedoResponse.Builder builder1 =
                TtmyFishingChallengeMessage.FishingChallengeUseTorpedoResponse.newBuilder();

        TtmyFishingChallengeMessage.FishingChallengeUseTorpedoResp.Builder builder =
                TtmyFishingChallengeMessage.FishingChallengeUseTorpedoResp.newBuilder();

        for (TtmyFishingChallengeMessage.FishingChallengeUseTorpedo fishingChallengeUseTorpedo : fishingChallengeUseTorpedoList) {

            if (fishingChallengeUseTorpedo.getTorpedoNum() <= 0) {
                return;
            }
            if (!PlayerManager.checkItem(user, fishingChallengeUseTorpedo.getTorpedoId(),
                    fishingChallengeUseTorpedo.getTorpedoNum())) {
                return;
            }

            CrystalExchangeLogEntity exchangeLogEntity = new CrystalExchangeLogEntity();
            exchangeLogEntity.setPlayerId(user.getId());

            exchangeLogEntity.setDragonCrystalBefore(PlayerManager.getItemNum(user, ItemId.DRAGON_CRYSTAL));
            exchangeLogEntity.setBronzeTorpedoBefore(PlayerManager.getItemNum(user, ItemId.BRONZE_TORPEDO));
            exchangeLogEntity.setSilverTorpedoBefore(PlayerManager.getItemNum(user, ItemId.SILVER_TORPEDO));
            exchangeLogEntity.setGoldTorpedoBefore(PlayerManager.getItemNum(user, ItemId.GOLD_TORPEDO));

            // 游戏中使用鱼雷可直接获得金币
            long money = 0;
            // long all = RedisUtil.val("USER_T_BANKRUPTCY_NUMBER_ALL",0L);
            if (fishingChallengeUseTorpedo.getTorpedoId() == ItemId.GOLD_TORPEDO.getId()) {

                exchangeLogEntity.setExchangeType(1);
                money = 50L * 10000 * fishingChallengeUseTorpedo.getTorpedoNum();
                // long moneya = 0;
                // if(all<0){
                // int zkc = ThreadLocalRandom.current().nextInt(new Double(RedisUtil.val("zkc",0)).intValue(), new
                // Double(RedisUtil.val("zkc1",0)).intValue());
                // moneya = new Double((1+zkc*0.01)*money).longValue();
                // RedisHelper.set("USER_T_BANKRUPTCY_NUMBER_ALL",String.valueOf(all+money-moneya));
                // }else{
                // int x = ThreadLocalRandom.current().nextInt(1,4);
                // if(x==1){//盈利状态
                // int zsc = ThreadLocalRandom.current().nextInt(new Double(RedisUtil.val("zsc",0)).intValue(), new
                // Double(RedisUtil.val("zsc1",0)).intValue());
                // moneya = new Double((1+zsc*0.01)*money).longValue();
                // RedisHelper.set("USER_T_BANKRUPTCY_NUMBER_ALL",String.valueOf(all+moneya-money));
                // }else if(x==2){
                // moneya = money;
                // RedisHelper.set("USER_T_BANKRUPTCY_NUMBER_ALL",String.valueOf(all+money));
                // }else{
                // int zkc = ThreadLocalRandom.current().nextInt(new Double(RedisUtil.val("zkc",0)).intValue(), new
                // Double(RedisUtil.val("zkc1",0)).intValue());
                // moneya = new Double((1+zkc*0.01)*money).longValue();
                // RedisHelper.set("USER_T_BANKRUPTCY_NUMBER_ALL",String.valueOf(all+money-moneya));
                // }
                // }
                // money = moneya;
                // long bankNumber = RedisUtil.val("USER_T_BANKRUPTCY_NUMBER_CHALLENGE"+user.getId(),0L);
                // RedisHelper.set("USER_T_BANKRUPTCY_NUMBER_CHALLENGE"+user.getId(),String.valueOf(bankNumber+moneya));
                RedisHelper.set("USER_T_STATUS" + user.getId(), "1");
                // 历史使用
                FishingManager.TORPEDO_RECORD.put("goldUseNumHistory",
                        FishingManager.TORPEDO_RECORD.getOrDefault("goldUseNumHistory", 0L)
                                + fishingChallengeUseTorpedo.getTorpedoNum());
                // 今日使用
                FishingManager.TORPEDO_RECORD.put("goldUseNumToday",
                        FishingManager.TORPEDO_RECORD.getOrDefault("goldUseNumToday", 0L)
                                + fishingChallengeUseTorpedo.getTorpedoNum());
                // 保存鱼雷使用数量记录
                RedisHelper.set("Fishing:TorpedoDropNum", JSON.toJSONString(FishingManager.TORPEDO_RECORD));
                List<ItemData> itemDataList = new LinkedList<>();
                // 扣除玩家鱼雷
                itemDataList.add(new ItemData(fishingChallengeUseTorpedo.getTorpedoId(),
                        -fishingChallengeUseTorpedo.getTorpedoNum()));
                // 增加玩家金币
                itemDataList.add(new ItemData(ItemId.DRAGON_CRYSTAL.getId(), money));
                PlayerManager.addItems(user, itemDataList, ItemChangeReason.USE_ITEM, true);

                for (ItemData itemData : itemDataList) {
                    int itemId = itemData.getItemId();
                    long count = itemData.getCount();
                    if (itemId == ItemId.DRAGON_CRYSTAL.getId()) {
                        exchangeLogEntity.setDragonCrystalChange(count);
                    } else if (itemId == ItemId.BRONZE_TORPEDO.getId()) {
                        exchangeLogEntity.setBronzeTorpedoChange(count);
                    } else if (itemId == ItemId.SILVER_TORPEDO.getId()) {
                        exchangeLogEntity.setSilverTorpedoChange(count);
                    } else if (itemId == ItemId.GOLD_TORPEDO.getId()) {
                        exchangeLogEntity.setGoldTorpedoChange(count);
                    }
                }

                crystalExchangeLogMapper.save(exchangeLogEntity);

            }

            // if (fishingChallengeUseTorpedo.getTorpedoId() == ItemId.RARE_TORPEDO.getId() ||
            // fishingChallengeUseTorpedo.getTorpedoId() == ItemId.RARE_TORPEDO_BANG.getId()) {
            // money = 10 * 25 * 10000 * fishingChallengeUseTorpedo.getTorpedoNum();
            // // 历史使用
            // FishingManager.TORPEDO_RECORD.put("goldUseNumHistory",
            // FishingManager.TORPEDO_RECORD.getOrDefault("goldUseNumHistory", 0L) +
            // fishingChallengeUseTorpedo.getTorpedoNum());
            // // 今日使用
            // FishingManager.TORPEDO_RECORD.put("goldUseNumToday",
            // FishingManager.TORPEDO_RECORD.getOrDefault("goldUseNumToday", 0L) +
            // fishingChallengeUseTorpedo.getTorpedoNum());
            // RedisHelper.set("Fishing:TorpedoDropNum", JSON.toJSONString(TORPEDO_RECORD));
            // }

            if (fishingChallengeUseTorpedo.getTorpedoId() == ItemId.DRAGON_CRYSTAL.getId()) {

                exchangeLogEntity.setExchangeType(2);
                money = fishingChallengeUseTorpedo.getTorpedoNum() / (50 * 10000);
                // // 保存鱼雷使用数量记录
                // RedisHelper.set("Fishing:TorpedoDropNum", JSON.toJSONString(FishingManager.TORPEDO_RECORD));
                List<ItemData> itemDataList = new LinkedList<>();
                // 扣除玩家鱼雷
                itemDataList.add(new ItemData(fishingChallengeUseTorpedo.getTorpedoId(),
                        -fishingChallengeUseTorpedo.getTorpedoNum()));
                // 增加玩家金币
                itemDataList.add(new ItemData(ItemId.GOLD_TORPEDO.getId(), money));
                PlayerManager.addItems(user, itemDataList, ItemChangeReason.USE_ITEM, true);

                for (ItemData itemData : itemDataList) {
                    int itemId = itemData.getItemId();
                    long count = itemData.getCount();
                    if (itemId == ItemId.DRAGON_CRYSTAL.getId()) {
                        exchangeLogEntity.setDragonCrystalChange(count);
                    } else if (itemId == ItemId.BRONZE_TORPEDO.getId()) {
                        exchangeLogEntity.setBronzeTorpedoChange(count);
                    } else if (itemId == ItemId.SILVER_TORPEDO.getId()) {
                        exchangeLogEntity.setSilverTorpedoChange(count);
                    } else if (itemId == ItemId.GOLD_TORPEDO.getId()) {
                        exchangeLogEntity.setGoldTorpedoChange(count);
                    }
                }

                crystalExchangeLogMapper.save(exchangeLogEntity);

                // long bankNumber = RedisUtil.val("USER_T_BANKRUPTCY_NUMBER_CHALLENGE"+user.getId(),0L);
                // RedisHelper.set("USER_T_BANKRUPTCY_NUMBER_CHALLENGE"+user.getId(),String.valueOf(bankNumber-fishingChallengeUseTorpedo.getTorpedoNum()));
            }

            builder.setPlayerId(user.getId());
            builder.setTorpedoId(fishingChallengeUseTorpedo.getTorpedoId());
            builder.setTorpedoNum(fishingChallengeUseTorpedo.getTorpedoNum());
            builder.setAngle(fishingChallengeUseTorpedo.getAngle());
            builder.setMoney(money);
            builder1.addFishingChallengeUseTorpedoResp(builder);

        }

        NetManager.sendMessage(OseeMessage.OseeMsgCode.S_C_TTMY_FISHING_Challenge_USE_TORPEDO_RESPONSE_VALUE, builder1,
                user);

    }

    // 通过：用户区分，雷鸣破，是否开启，默认：不开启
    public static final String USER_LEI_MING_PO_USE_FLAG = "USER_LEI_MING_PO_USE_FLAG";

    public static RMap<Long, Boolean> getUserLeiMingPoUseFlag() {

        return redissonClient.getMap(USER_LEI_MING_PO_USE_FLAG);

    }

    // 通过：用户区分，天神关羽，是否开启，默认：不开启
    public static final String USER_TIAN_SHEN_GUAN_YU_FLAG = "USER_TIAN_SHEN_GUAN_YU_FLAG";

    /**
     * 获取天神关羽是否开启，默认：不开启
     */
    public static RMap<Long, Boolean> getUserTianShenGuanYuUseFlag() {
        return redissonClient.getMap(USER_TIAN_SHEN_GUAN_YU_FLAG);
    }


    // 通过：用户区分，雷神变，是否开启，默认：不开启
    public static final String USER_LEI_SHEN_BIAN_USE_FLAG = "USER_LEI_SHEN_BIAN_USE_FLAG";

    public static RMap<Long, Boolean> getUserLeiShenBianUseFlag() {

        return redissonClient.getMap(USER_LEI_SHEN_BIAN_USE_FLAG);

    }

    public static boolean getUserLeiShenBianUseFlag(long userId, NewBaseGameRoom room) {

        // 只能在配置了该技能的房间里面使用
        FishCcxxConfig fishCcxxConfig = MyRefreshFishingUtil.FISHING_CCXX_CONFIG_MAP.get(room.getRoomIndex());

        if (fishCcxxConfig == null) {

            return false;

        }

        List<Integer> skillIdList = fishCcxxConfig.getSkillIdList(); // 获取：可以使用的技能集合

        if (!skillIdList.contains(ItemId.SKILL_LEI_MING_PO.getId())) {

            return false;

        }

        boolean payFlag = getPayFlag(fishCcxxConfig, ItemId.SKILL_LEI_MING_PO);


        return BooleanUtil.isTrue(getUserLeiShenBianUseFlag().get(userId));

    }

    /**
     * 道具使用状态同步，技能同步
     */
    public void propsUseStateSync(FishingChallengePlayer player, FishingChallengeRoom room,
                                  TtmyFishingChallengeMessage.FishingChallengePropsUseStateSyncRequest request) {

        long currentTimeMillis = System.currentTimeMillis();

        FishCcxxConfig fishCcxxConfig = MyRefreshFishingUtil.FISHING_CCXX_CONFIG_MAP.get(room.getRoomIndex());

        if (fishCcxxConfig == null) {

            NetManager.sendHintBoxMessageToClient("配置不存在", player.getUser(), 10);
            return;

        }

        TtmyFishingChallengeMessage.FishingChallengeUseSkillResponse.Builder skillBuilder =
                TtmyFishingChallengeMessage.FishingChallengeUseSkillResponse.newBuilder();

        skillBuilder.setRequestPlayerId(player.getId());

        for (BaseGamePlayer gamePlayer : room.getGamePlayers()) {

            if (gamePlayer == null) {
                continue;
            }

            long userId = gamePlayer.getId();

            if (request.getTargetPlayerId() != 0 && userId != request.getTargetPlayerId()) {
                continue;
            }

            skillBuilder.setTargetPlayerId(request.getTargetPlayerId());

            skillBuilder.setPlayerId(userId);

            FishingChallengePlayer challengePlayer = (FishingChallengePlayer) gamePlayer;

            if ((challengePlayer).getLastCritTime() != 0) { // 开启暴击

                if (getPayFlag(fishCcxxConfig, ItemId.SKILL_CRIT)) {

                    long remainCritTime = SKILL_CRIT_TIME - (currentTimeMillis - player.getLastCritTime());

                    if (remainCritTime > 0) {

                        skillBuilder.setSkillId(ItemId.SKILL_CRIT.getId());
                        skillBuilder.setDuration((int) (remainCritTime / 1000));
                        NetManager.sendMessage(
                                OseeMessage.OseeMsgCode.S_C_TTMY_FISHING_CHALLENGE_USE_SKILL_RESPONSE_VALUE, skillBuilder,
                                player.getUser());

                    }

                } else {

                    skillBuilder.setSkillId(ItemId.SKILL_CRIT.getId());
                    skillBuilder.setDuration(Integer.MAX_VALUE);
                    NetManager.sendMessage(OseeMessage.OseeMsgCode.S_C_TTMY_FISHING_CHALLENGE_USE_SKILL_RESPONSE_VALUE,
                            skillBuilder, player.getUser());

                }

            }

            if ((challengePlayer).getLastElectromagneticTime() != 0) { // 开启电磁炮

                if (getPayFlag(fishCcxxConfig, ItemId.SKILL_ELETIC)) {

                    long remainCritTime = SKILL_ELETIC_TIME - (currentTimeMillis - player.getLastElectromagneticTime());

                    if (remainCritTime > 0) {

                        skillBuilder.setSkillId(ItemId.SKILL_ELETIC.getId());
                        skillBuilder.setDuration((int) (remainCritTime / 1000));
                        NetManager.sendMessage(
                                OseeMessage.OseeMsgCode.S_C_TTMY_FISHING_CHALLENGE_USE_SKILL_RESPONSE_VALUE, skillBuilder,
                                player.getUser());

                    }

                } else {

                    skillBuilder.setSkillId(ItemId.SKILL_ELETIC.getId());
                    skillBuilder.setDuration(Integer.MAX_VALUE);
                    NetManager.sendMessage(OseeMessage.OseeMsgCode.S_C_TTMY_FISHING_CHALLENGE_USE_SKILL_RESPONSE_VALUE,
                            skillBuilder, player.getUser());

                }

            }

            if ((challengePlayer).getLastLockTime() != 0) { // 开启锁定

                if (getPayFlag(fishCcxxConfig, ItemId.SKILL_LOCK)) {

                    long remainCritTime = SKILL_LOCK_TIME - (currentTimeMillis - player.getLastLockTime());

                    if (remainCritTime > 0) {

                        skillBuilder.setSkillId(ItemId.SKILL_LOCK.getId());
                        skillBuilder.setDuration((int) (remainCritTime / 1000));
                        NetManager.sendMessage(
                                OseeMessage.OseeMsgCode.S_C_TTMY_FISHING_CHALLENGE_USE_SKILL_RESPONSE_VALUE, skillBuilder,
                                player.getUser());

                    }

                } else {

                    skillBuilder.setSkillId(ItemId.SKILL_LOCK.getId());
                    skillBuilder.setDuration(Integer.MAX_VALUE);
                    NetManager.sendMessage(OseeMessage.OseeMsgCode.S_C_TTMY_FISHING_CHALLENGE_USE_SKILL_RESPONSE_VALUE,
                            skillBuilder, player.getUser());

                }

            }

            if ((challengePlayer).getLastAutoFireTime() != 0) { // 开启自动开炮

                skillBuilder.setSkillId(ItemId.SKILL_AUTO_FIRE.getId());
                skillBuilder.setDuration(Integer.MAX_VALUE);
                NetManager.sendMessage(OseeMessage.OseeMsgCode.S_C_TTMY_FISHING_CHALLENGE_USE_SKILL_RESPONSE_VALUE,
                        skillBuilder, player.getUser());

            }

            Boolean leiMingPoUseFlag = getUserLeiMingPoUseFlag().get(userId);

            if (BooleanUtil.isTrue(leiMingPoUseFlag)) { // 开启雷鸣破
                skillBuilder.setSkillId(ItemId.SKILL_LEI_MING_PO.getId());
                skillBuilder.setDuration(Integer.MAX_VALUE);
                NetManager.sendMessage(OseeMessage.OseeMsgCode.S_C_TTMY_FISHING_CHALLENGE_USE_SKILL_RESPONSE_VALUE,
                        skillBuilder, player.getUser());
            }
            Boolean tianShenGuanYuUseFlag = getUserTianShenGuanYuUseFlag().get(userId);
            if (BooleanUtil.isTrue(tianShenGuanYuUseFlag)) { // 开启天神关羽
                skillBuilder.setSkillId(ItemId.SKILL_TIAN_SHEN_GUAN_YU.getId());
                skillBuilder.setDuration(Integer.MAX_VALUE);
                NetManager.sendMessage(OseeMessage.OseeMsgCode.S_C_TTMY_FISHING_CHALLENGE_USE_SKILL_RESPONSE_VALUE,
                        skillBuilder, player.getUser());
            }

            boolean userLeiShenBianUseFlag = getUserLeiShenBianUseFlag(player.getId(), room);

            if (userLeiShenBianUseFlag) { // 开启雷神变

                skillBuilder.setSkillId(ItemId.SKILL_LEI_SHEN_BIAN.getId());
                skillBuilder.setDuration(Integer.MAX_VALUE);

                int mult = RedisUtil.val("USER_CRIT_MULT" + player.getUser().getId(), 1);

                skillBuilder.setNum1(mult);

                NetManager.sendMessage(OseeMessage.OseeMsgCode.S_C_TTMY_FISHING_CHALLENGE_USE_SKILL_RESPONSE_VALUE,
                        skillBuilder, player.getUser());

            }

            long remainFenShenTime = SKILL_FEN_SHEN_TIME - (currentTimeMillis - challengePlayer.getLastFenShenTime());

            if (remainFenShenTime > 0) {

                skillBuilder.setSkillId(ItemId.FEN_SHEN.getId());
                skillBuilder.setDuration((int) (remainFenShenTime / 1000));
                NetManager.sendMessage(OseeMessage.OseeMsgCode.S_C_TTMY_FISHING_CHALLENGE_USE_SKILL_RESPONSE_VALUE,
                        skillBuilder, player.getUser());

            }

            long remainFastTime = SKILL_FAST_TIME - (currentTimeMillis - challengePlayer.getLastFastTime());

            if (remainFastTime > 0) {

                skillBuilder.setSkillId(ItemId.SKILL_FAST.getId());
                skillBuilder.setDuration((int) (remainFastTime / 1000));
                NetManager.sendMessage(OseeMessage.OseeMsgCode.S_C_TTMY_FISHING_CHALLENGE_USE_SKILL_RESPONSE_VALUE,
                        skillBuilder, player.getUser());

            }

            long remainDoubleTime = SKILL_DOUBLE_TIME - (currentTimeMillis - challengePlayer.getLastDoubleTime());

            if (remainDoubleTime > 0) {

                skillBuilder.setSkillId(ItemId.SKILL_DOUBLE.getId());
                skillBuilder.setDuration((int) (remainDoubleTime / 1000));
                NetManager.sendMessage(OseeMessage.OseeMsgCode.S_C_TTMY_FISHING_CHALLENGE_USE_SKILL_RESPONSE_VALUE,
                        skillBuilder, player.getUser());

            }

        }

    }

    /**
     * 获取：是否是付费道具
     */
    public static boolean getPayFlag(FishCcxxConfig fishCcxxConfig, ItemId itemId) {

        int index = fishCcxxConfig.getSkillIdList().indexOf(itemId.getId());

        Integer skillPayType;

        if (itemId.getId() == -1) {

            skillPayType = null;

        } else {

            skillPayType = fishCcxxConfig.getSkillPayTypeList().get(index);

        }

        if (skillPayType == null) {

            skillPayType = 1; // 默认：付费

        }

        // 是否是付费道具
        return skillPayType == 1;

    }

    /**
     * 玩家金币同步
     */
    public static void playerMoneySync(FishingGamePlayer player, NewBaseFishingRoom room) {

        OseePlayerEntity oseePlayerEntity = PlayerManager.getPlayerEntity(UserContainer.getUserById(player.getId()));

        doFishingChallengeFireResponse(room, player, 0, 0, 0, oseePlayerEntity.getDragonCrystal(),
                oseePlayerEntity.getDiamond());

    }

    /**
     * 广播玩家发送子弹响应，备注：也可以用于返回玩家金币的响应，fireId 传 0就行，前端那边会自行处理
     */
    public static void doFishingChallengeFireResponse(NewBaseFishingRoom gameRoom, FishingGamePlayer player,
                                                      long fireId, long fishId, float angle, long restMoney, long restDiamond) {

        TtmyFishingChallengeMessage.FishingChallengeFireResponse.Builder builder =
                TtmyFishingChallengeMessage.FishingChallengeFireResponse.newBuilder();

        builder.setFireId(fireId);
        builder.setFishId(fishId);
        builder.setAngle(angle);

        builder.setPlayerId(player.getId());

        builder.setRestMoney(restMoney);

        builder.setRestDiamond(restDiamond);

        MyRefreshFishingUtil.sendRoomMessage(gameRoom,
                OseeMessage.OseeMsgCode.S_C_TTMY_FISHING_CHALLENGE_FIRE_RESPONSE_VALUE, builder);

    }

    /**
     * 仙魔九界位置移动
     */
    public static void xmjjMoveRequest(TtmyFishingChallengeMessage.XmjjMoveRequest request,
                                       FishingChallengePlayer player, NewBaseFishingRoom room) {

//        player.lastFireTime.put(player.getId(), System.currentTimeMillis()); // 设置：活跃时间
        player.setLastFireTime(System.currentTimeMillis());
        TtmyFishingChallengeMessage.XmjjMoveResponse.Builder builder =
                TtmyFishingChallengeMessage.XmjjMoveResponse.newBuilder();

        builder.setPlayerID(request.getPlayerID());

        TtmyFishingChallengeMessage.V3Proto.Builder v3ProtoBuilder = TtmyFishingChallengeMessage.V3Proto.newBuilder();

        v3ProtoBuilder.setX(request.getV3().getX());
        v3ProtoBuilder.setY(request.getV3().getY());
        v3ProtoBuilder.setZ(request.getV3().getZ());

        player.setV3ProtoBuilder(v3ProtoBuilder); // 保存：玩家位置信息

        builder.setV3(v3ProtoBuilder);

        MyRefreshFishingUtil.sendRoomMessage(room, OseeMessage.OseeMsgCode.S_C_XMJJ_MOVE_RESPONSE_VALUE, builder);

    }

    /**
     * 仙魔九界，玩家离开怪物攻击范围时的请求
     */
    public static void xmjjPlayerLeaveMonsterAttackRangeRequest(
            TtmyFishingChallengeMessage.XmjjPlayerLeaveMonsterAttackRangeRequest request, FishingChallengePlayer player,
            FishingChallengeRoom room) {

        FishStruct fishStruct = room.getFishMap().get(request.getMonsterId());

        if (fishStruct == null) {
            return;
        }

        fishStruct.getTempMonsterAttackPlayerIdSet().remove(request.getPlayerId());

        // log.info("怪物可以攻击的玩家-remove：{}", fishStruct.getTempMonsterAttackPlayerIdSet());

        Long monsterAttackPlayerId = fishStruct.getMonsterAttackPlayerId();

        if (monsterAttackPlayerId != null && monsterAttackPlayerId.equals(request.getPlayerId())) {
            fishStruct.setMonsterAttackPlayerId(null); // 如果怪物正在攻击这个玩家，则设置怪物的攻击对象为 null
        }

        TtmyFishingChallengeMessage.XmjjPlayerLeaveMonsterAttackRangeResponse.Builder builder =
                TtmyFishingChallengeMessage.XmjjPlayerLeaveMonsterAttackRangeResponse.newBuilder();

        builder.setCode(200);

        NetManager.sendMessage(OseeMessage.OseeMsgCode.S_C_XMJJ_PLAYER_LEAVE_MONSTER_ATTACK_RANGE_RESPONSE_VALUE,
                builder, player.getUser());

    }

    /**
     * ip区域限制请求
     */
    public static void ipRegionLimitRequest(TtmyFishingChallengeMessage.IpRegionLimitRequest request, ServerUser user) {

        boolean allowFlag = true;

        String ip = request.getIp();

        if (StrUtil.isNotBlank(ip)) {

            boolean limitChineseIpFlag = MySettingUtil.SETTING.getBool("sys.limitChineseIpFlag", false);

            if (limitChineseIpFlag) {

                String region = Ip2RegionUtil.getRegion(ip);

                if (region.startsWith("中国|")) {
                    allowFlag = false;
                }

            }

        }

        TtmyFishingChallengeMessage.IpRegionLimitResponse.Builder builder =
                TtmyFishingChallengeMessage.IpRegionLimitResponse.newBuilder();

        builder.setAllowFlag(allowFlag);

        NetManager.sendMessage(OseeMessage.OseeMsgCode.S_C_IP_REGION_LIMIT_RESPONSE_VALUE, builder, user);

    }

    /**
     * 击杀boss列表请求
     */
    public static void killBossListRequest(TtmyFishingChallengeMessage.KillBossListRequest request, ServerUser user) {

        StringBuilder condBuilder = new StringBuilder();
        StringBuilder pageBuilder = new StringBuilder();

        int pageNum = request.getPageNum();
        int pageSize = request.getPageSize();

        pageBuilder.append(" LIMIT ").append((pageNum - 1) * pageSize).append(", ").append(pageSize);

        condBuilder.append(" AND userId = '").append(user.getId()).append("'");

        if (CollUtil.isNotEmpty(request.getRoomIndexList())) {

            condBuilder.append(" AND room_index IN (").append(CollUtil.join(request.getRoomIndexList(), ","))
                    .append(")");

        }

        List<KillBossEntity> list = oseePlayerMapper.getKillBossList(condBuilder.toString(), pageBuilder.toString());

        int count = oseePlayerMapper.getKillBossCount(condBuilder.toString());

        TtmyFishingChallengeMessage.KillBossListResponse.Builder builder =
                TtmyFishingChallengeMessage.KillBossListResponse.newBuilder();

        for (KillBossEntity item : list) {

            TtmyFishingChallengeMessage.KillBossListResponseItem.Builder itemBuilder =
                    TtmyFishingChallengeMessage.KillBossListResponseItem.newBuilder();

            itemBuilder.setFishName(item.getBossName());
            itemBuilder.setDateTimeStr(DateUtil.formatDateTime(item.getCreateTime()));
            itemBuilder.setBatteryLevel(item.getBatterLevel());
            itemBuilder.setMult(item.getMult());

            builder.addItem(itemBuilder);

        }

        builder.setTotal(count);

        NetManager.sendMessage(OseeMessage.OseeMsgCode.S_C_KILL_BOSS_LIST_RESPONSE_VALUE, builder, user);

    }

    /**
     * 鱼使用冰冻技能
     */
    public void fishUseFreeze(FishingChallengeRoom room, FishingChallengePlayer player,
                              TtmyFishingChallengeMessage.FishingFishUseFreezeRequest request) {

        long nowTime = System.currentTimeMillis();

        Set<Long> fishIdSet = new HashSet<>(request.getFishIdList());

        int freezeMs = request.getFreezeMs();

        for (FishStruct fish : room.getFishMap().values()) {

            if (fishIdSet.contains(fish.getId()) && fish.isAddSurvivalTimeFlag()) {

                long frozenAddTimeTemp =
                        FishingHelper.getFrozenAddTime(fish.getLastFishFrozenTime(), nowTime, freezeMs);

                // 增加：鱼的存活时间
                FishingHelper.addFishLifeTime(frozenAddTimeTemp, fish);

                fish.setLastFishFrozenTime(nowTime); // 新冰冻

            }

        }

        TtmyFishingChallengeMessage.FishingFishUseFreezeResponse.Builder builder =
                TtmyFishingChallengeMessage.FishingFishUseFreezeResponse.newBuilder();

        builder.addAllFishId(request.getFishIdList());
        builder.setFreezeMs(freezeMs);

        MyRefreshFishingUtil.sendRoomMessage(room, OseeMessage.OseeMsgCode.S_C_FISHING_FISH_USE_FREEZE_RESPONSE_VALUE,
                builder);

    }

    /**
     * 开天魔猿是否死亡
     */
    public static RBucket<Boolean> kaiTianMoYuanDeathFlagRbucket() {

        return redissonClient.getBucket("KAI_TIAN_MO_YUAN_DEATH_FLAG");

    }

    /**
     * 捕鱼房间消息同步请求
     *
     * @param request
     * @param player
     * @param gameRoom
     */
    public void fishingRoomMessage(TtmyFishingChallengeMessage.fishingRoomMessageRequest request, FishingChallengePlayer player, FishingChallengeRoom gameRoom) {
        if (gameRoom != null) {
            TtmyFishingChallengeMessage.fishingRoomMessageResponse.Builder builder =
                    TtmyFishingChallengeMessage.fishingRoomMessageResponse.newBuilder();
            builder.setPlayerId(request.getPlayerId());
            builder.setMsg(request.getMsg());
            builder.setFishId(request.getFishId());

            MyRefreshFishingUtil.sendRoomMessage(gameRoom, OseeMessage.OseeMsgCode.S_C_TTMY_USE_FISHING_ROOM_MESSAGE_RESPONSE_VALUE,
                    builder);

        }
    }
}
