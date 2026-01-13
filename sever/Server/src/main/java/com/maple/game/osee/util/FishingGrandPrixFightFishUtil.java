package com.maple.game.osee.util;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.WeightRandom;
import com.maple.engine.container.DataContainer;
import com.maple.game.osee.common.RedisUtil;
import com.maple.game.osee.dao.data.entity.OseePlayerEntity;
import com.maple.game.osee.entity.fishing.FishingGamePlayer;
import com.maple.game.osee.entity.fishing.NewBaseFishingRoom;
import com.maple.game.osee.entity.fishing.csv.file.FishConfig;
import com.maple.game.osee.entity.fishing.game.FireStruct;
import com.maple.game.osee.entity.fishing.game.FishStruct;
import com.maple.game.osee.entity.fishing.grandprix.FishingGrandPrixPlayer;
import com.maple.game.osee.manager.PlayerManager;
import com.maple.game.osee.manager.fishing.FishingChallengeManager;
import com.maple.game.osee.manager.fishing.FishingGrandPrixManager;
import com.maple.game.osee.model.bo.ActiveConfigBO;
import com.maple.game.osee.model.enums.ControlTypeEnum;
import com.maple.game.osee.model.enums.ZtControlTypeEnum;
import com.maple.game.osee.pojo.fish.AbsFish;
import com.maple.game.osee.pojo.fish.FishFactory;
import com.maple.game.osee.proto.OseeMessage;
import com.maple.game.osee.proto.TtmyFishingGrandPrixMessage;
import com.maple.game.osee.service.impl.ActiveServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.*;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

import static com.maple.game.osee.manager.fishing.FishingManager.SKILL_CRIT_TIME;
import static com.maple.game.osee.manager.fishing.FishingManager.SKILL_ELETIC_TIME;

/**
 * 大奖赛捕鱼：击中鱼，工具类
 */
@Component
@Slf4j
public class FishingGrandPrixFightFishUtil {

    private static final String PRE_KEY = "GrandPrix:";

    private static RedissonClient redissonClient;

    public FishingGrandPrixFightFishUtil(RedissonClient redissonClient) {

        FishingGrandPrixFightFishUtil.redissonClient = redissonClient;

    }

    /**
     * 玩家击中鱼，一系列的处理
     */
    public static void playerFightFish(NewBaseFishingRoom room, FishingGrandPrixPlayer fishingGamePlayer, long fireId,
                                       List<Long> fishIdList) {

        fightFish(room, fishingGamePlayer, fireId, fishIdList);

    }

    /**
     * 处理：击中鱼
     */
    public static void fightFish(NewBaseFishingRoom room, FishingGrandPrixPlayer player, long fireId,
                                 List<Long> fishIdList) {

        FireStruct fireStruct = FishingChallengeFightFishUtil.fightFishHandlerFire(player, fireId); // 处理子弹

        if (fireStruct == null) {
            return;
        }

        OseePlayerEntity playerEntity = PlayerManager.getPlayerEntity(player.getUser());

        synchronized (playerEntity) {

            long currentTimeMillis = System.currentTimeMillis();

            for (long item : fishIdList) {

                try {

                    // long l1 = System.currentTimeMillis();

                    // 处理鱼
                    fightFishHandlerFish(room, item, player, currentTimeMillis, playerEntity);

                    // log.info("处理，击中鱼，耗时：{}", System.currentTimeMillis() - l1);

                } catch (Exception e) {

                    e.printStackTrace();

                }

            }

        }

    }

    /**
     * 处理鱼
     */
    private static void fightFishHandlerFish(NewBaseFishingRoom room, long fishId, FishingGrandPrixPlayer player,
                                             long currentTimeMillis, OseePlayerEntity playerEntity) {

        final long batteryLevel = player.getBatteryLevel(); // 炮倍

        // 需要花费的金币
        long needMoney = FishingChallengeManager.getNeedMoney(batteryLevel, currentTimeMillis, player, room);

        // 获取：鱼对象
        final FishStruct fishStruct = room.getFishMap().get(fishId);

        if (fishStruct == null) {
            return;
        }

        // 执行：处理鱼
        execFightFishHandlerFish(room, fishId, player, needMoney, fishStruct, currentTimeMillis, batteryLevel,
                playerEntity);

    }

    // 通过场次，鱼 modelId区分，动态倍数鱼，dtjcxs
    public static final String DYNAMIC_MULTIPLE_FISH_DTJCXS_ROOM_INDEX_MODEL_ID_PRE =
            "DYNAMIC_MULTIPLE_FISH_DTJCXS_ROOM_INDEX_MODEL_ID_PRE:";

    // 动态倍数，鱼 modelId集合
    public static final List<Integer> DYNAMIC_MULT_FISH_MODEL_ID_LIST = CollUtil.newArrayList(35, 37);

    /**
     * 一些通用参数的处理，备注：可以不加锁，进行调用 cx：是盈亏，打鱼往里面填，死鱼从里面扣
     *
     * @param addValue 大于 0 表示击中鱼 小于 0 表示打死鱼
     */
    private static void fightFishHandlerCommonParam(double addValue, long userId, boolean updateXhFlag, int fishType2) {

        if (addValue == 0) {
            return;
        }

        // 增加：xh
        RAtomicDoubleAsync atomicDoubleXH =
                redissonClient.getAtomicDouble(PRE_KEY + XH_CHALLENGE_USER_FISH_TYPE_2_PRE + userId + ":" + fishType2);

        atomicDoubleXH.addAndGetAsync(addValue);

    }

    /**
     * 执行：处理鱼
     *
     * @param dynamicMultipleFishFlag 是否是动态倍数鱼
     */
    public static boolean doExecFightFishHandlerFish(NewBaseFishingRoom room, FishingGrandPrixPlayer player,
                                                     FishConfig fishConfig, Long randomMoney, long userId, int fishType2, long winMoney, long needMoney,
                                                     ZtControlTypeEnum ztControlTypeEnum, Long fishId, boolean dynamicMultipleFishFlag, long personalNum,
                                                     long batteryLevel) {

        // if (personalNum != 0) {
        //
        // // 一些通用参数的处理
        // fightFishHandlerCommonParam(needMoney, player.getId(), true, fishType2);
        //
        // // 个控击杀判断
        // return personalControlKillHandler(player, fishConfig, randomMoney, userId, fishType2, roomType, winMoney,
        // needMoney, personalNum);
        //
        // }

        if (dynamicMultipleFishFlag) {
            if (ZtControlTypeEnum.HS_NORMAL.getType() == ztControlTypeEnum.getType()) {
                return false; // 如果是：动态倍速鱼，并且是回收时，则不死鱼
            }
        }

        boolean killFlag; // 是否击杀

        // 捕鱼，上一次是爆发还是回收：0 回收（默认） 1 爆发
        RBucket<Integer> redisFishingHitStateBucket =
                redissonClient.getBucket(FISHING_HIT_STATE_ROOM_INDEX_USER_MODEL_ID_PRE + room.getRoomIndex() + ":"
                        + player.getId() + ":" + fishConfig.getModelId());
        Integer redisFishingHitState = redisFishingHitStateBucket.get();
        if (redisFishingHitState == null) {
            redisFishingHitState = 0;
        }

        List<Integer> xList = FishingChallengeFightFishUtil.getFishBdzConfigBO(room.getRoomIndex()).getXList();
        int x = RandomUtil.getRandom(xList.get(0), xList.get(1));

        double kz;

        int currentFishingHitState; // 当前状态：0 回收 1 爆发

        double x1Difficulty = FishingTUtil.getProfitRatioDTO().getX1Difficulty(); // 获取：x1场次难度
        kz = x1Difficulty * Math.sin(0.0314 * x);
        currentFishingHitState = 1;
        player.setNeedHitNumberMode("y6");

        boolean forceUpdateNeedHitCountFlag;
        if (fishType2 <= 2) {
            forceUpdateNeedHitCountFlag = false; // 1 和 2 就不进行判断了
        } else {
            forceUpdateNeedHitCountFlag = redisFishingHitState != currentFishingHitState;
        }

        if (forceUpdateNeedHitCountFlag) {
            redisFishingHitStateBucket.set(currentFishingHitState); // 更新：redis，捕鱼，上一次是爆发还是回收
        }

        // 判断是否击杀，以及一些数据处理
        killFlag = fightFishHandlerKill(player, fishConfig, randomMoney, userId, ControlTypeEnum.NORMAL_CONTROL, null,
                null, true, System.currentTimeMillis(), kz, forceUpdateNeedHitCountFlag, ztControlTypeEnum, room, fishId,
                dynamicMultipleFishFlag, batteryLevel);

        if (killFlag) { // 如果击杀了

            // 处理：击杀之后的 命中次数相关
            handlerKillAfterForHitCount(fishConfig, userId, ControlTypeEnum.NORMAL_CONTROL, ztControlTypeEnum);

        }

        return killFlag;

    }

    /**
     * 处理：击杀之后的 命中次数相关
     */
    public static void handlerKillAfterForHitCount(FishConfig fishConfig, long userId, ControlTypeEnum controlTypeEnum,
                                                   ZtControlTypeEnum ztControlTypeEnum) {

        String currentHitCountRedisKey =
                PRE_KEY + controlTypeEnum.getCurrentHitCountPre() + userId + ":" + fishConfig.getModelId();

        String needHitCountRedisKey =
                PRE_KEY + controlTypeEnum.getNeedHitCountPre() + userId + ":" + fishConfig.getModelId();

        RAtomicDouble atomicDoubleCurrentHitCount = redissonClient.getAtomicDouble(currentHitCountRedisKey);

        // 获取：当前命中次数
        double currentHitCount = atomicDoubleCurrentHitCount.get();

        RBucket<Double> bucketNeedHitCount = redissonClient.getBucket(needHitCountRedisKey);

        // 获取：需要命中次数
        double needHitCount = bucketNeedHitCount.get();

        RBatch batch = redissonClient.createBatch();

        batch.getAtomicDouble(currentHitCountRedisKey).deleteAsync();

        batch.getBucket(needHitCountRedisKey).deleteAsync();

        batch.execute(); // 执行批量操作

        // 移除：倍数范围，比自己低的，鱼的当前攻击次数
        if (ZtControlTypeEnum.BF_NORMAL.getType() == ztControlTypeEnum.getType()) {
            handlerFishMultRange(fishConfig, userId, controlTypeEnum, currentHitCount, needHitCount);
        }

    }

    /**
     * 移除：倍数范围，比自己低的，鱼的当前攻击次数
     */
    private static void handlerFishMultRange(FishConfig fishConfig, long userId, ControlTypeEnum controlTypeEnum,
                                             double currentHitCount, double needHitCount) {

        String redisKey = PRE_KEY + CHALLENGE_USER_FISH_MODEL_ID_MULT_PRE + userId;

        Double randomMoney = RedisUtil.zScore(redisKey, String.valueOf(fishConfig.getModelId()));

        if (randomMoney == null) {
            return;
        }

        RedisUtil.zRemove(redisKey, String.valueOf(fishConfig.getModelId())); // 移除：该鱼的倍数

        // 获取：下标，从 FISH_MULT_RANGE_LIST里面，通过鱼的倍数
        int index = FishingChallengeFightFishUtil.doGetIndexFromFishMultRangeListByRandomMoney(randomMoney,
                FISH_MULT_RANGE_LIST);

        // 差值
        double differenceNumber = needHitCount - currentHitCount;

        if (index == 0 || differenceNumber <= 0) {
            return;
        }

        for (int i = index - 1; i >= 0; i--) {

            List<Integer> fishMultRangeList = FISH_MULT_RANGE_LIST.get(i);

            // 根据倍数范围，拿到鱼的 modelIdList
            List<String> modelIdList =
                    RedisUtil.zValueRangeByScore(redisKey, fishMultRangeList.get(0), fishMultRangeList.get(1));

            // 遍历：modelIdList
            for (String item : modelIdList) {

                RAtomicDouble atomicDoubleCurrentHitCountByModelId = redissonClient
                        .getAtomicDouble(PRE_KEY + controlTypeEnum.getCurrentHitCountPre() + userId + ":" + item);

                // 获取：该模型鱼的，当前命中次数
                double currentHitCountByModelId = atomicDoubleCurrentHitCountByModelId.get();

                if (currentHitCountByModelId > differenceNumber) {
                    atomicDoubleCurrentHitCountByModelId.set(currentHitCountByModelId - differenceNumber);
                    differenceNumber = 0;
                    // log.info("移除当前命中次数，modelId：{}，剩余次数：{}，移除的次数：{}", item, currentHitCountByModelId -
                    // differenceNumber,
                    // currentHitCountByModelId);
                } else {
                    atomicDoubleCurrentHitCountByModelId.delete();
                    differenceNumber = differenceNumber - currentHitCountByModelId;
                    // log.info("移除当前命中次数，modelId：{}，剩余差值：{}，移除的次数：{}", item, differenceNumber,
                    // currentHitCountByModelId);
                }

                if (differenceNumber <= 0) {
                    return;
                }

            }

        }

    }

    /**
     * 执行：处理鱼
     */
    private static void execFightFishHandlerFish(NewBaseFishingRoom room, long fishId, FishingGrandPrixPlayer player,
                                                 long needMoney, FishStruct fishStruct, long currentTimeMillis, long batteryLevel,
                                                 OseePlayerEntity playerEntity) {

        // 注意：这个对象不要使用，不然会出现并发问题
        final FishConfig fishConfig = DataContainer.getData(fishStruct.getConfigId(), FishConfig.class);

        // 获取：对应的鱼
        final AbsFish absFish = FishFactory.create(fishStruct, fishConfig, player.getUser(), room);

        absFish.setKey(PRE_KEY + absFish.getKey()); // 重新：设置一个 key

        final long userId = player.getUser().getId(); // 用户 id


        long randomMoney; // 鱼的倍数
        long winMoney = 0; // 本次赢的钱
        boolean killFlag; // 是否击杀鱼
        long personalNum = 0; // 个控值
        long dayPoint = 0; // 今日积分

        synchronized (fishStruct) { // 锁鱼

            if (room.getFishMap().get(fishId) == null) { // 如果：鱼不存在了
                return;
            }

            synchronized (playerEntity) { // 锁用户，目的：退出房间时，可以原子操作

                if (room.getGamePlayerById(userId) == null) { // 如果：用户已经退出房间了
                    return;
                }

                fightFishHandlerPreKill(player, needMoney, room); // 判断击杀前的，一些处理

                // 是否是：动态倍数鱼
                boolean dynamicMultipleFishFlag = false;

                if (dynamicMultipleFishFlag) {

                    // 增加；动态倍数鱼的奖池
                    FishingChallengeFightFishUtil.getDynamicMultipleFishPrizePoolAtomicLong(room.getRoomIndex())
                            .addAndGet(needMoney);

                }

                // personalNum = RedisUtil.val("USER_PERSONAL_CONTROL_NUM_CHALLENGE" + userId, 0d).longValue();

                // 获取：爆发还是回收
                ZtControlTypeEnum ztControlTypeEnum = ZtControlTypeEnum.HS_GRAND_PRIX;

                randomMoney = absFish.getMultiple(); // 设置：鱼的倍数

                // 执行：处理鱼
                killFlag = doExecFightFishHandlerFish(room, player, fishConfig, randomMoney, userId,
                        fishConfig.getFishType2(), winMoney, needMoney, ztControlTypeEnum, fishId, dynamicMultipleFishFlag,
                        personalNum, batteryLevel);

                if (killFlag) {

                    // 本次赢的钱
                    // winMoney = FishingChallengeManager.getWinMoney(fireStruct, randomMoney, player);

                    // 击杀之后的逻辑处理，需要上锁的一些操作
                    dayPoint = fightFishHandlerKillAfterForLock(room, player, fishConfig, absFish, randomMoney,
                            fishStruct, winMoney, userId, batteryLevel, personalNum, playerEntity);

                }

            }
        }

        // 击中鱼之后的处理，不需要上锁
        execFightFishHandlerFishAfterForNoLock(room, player, needMoney, fishStruct, currentTimeMillis, fishConfig,
                userId, batteryLevel, randomMoney, winMoney, killFlag, dayPoint);

    }

    // xh，根据：用户，fishType2，区分
    public static final String XH_CHALLENGE_USER_FISH_TYPE_2_PRE = "XH_CHALLENGE_USER_FISH_TYPE_2_PRE:";

    // 用户，鱼的 modelId，区分，值：是鱼的倍数
    public static final String CHALLENGE_USER_FISH_MODEL_ID_MULT_PRE = "CHALLENGE_USER_FISH_MODEL_ID_MULT_PRE:";

    // 鱼倍数范围集合
    public static final List<List<Integer>> FISH_MULT_RANGE_LIST = CollUtil.newArrayList( //
            CollUtil.newArrayList(2, 49), //
            CollUtil.newArrayList(50, 99), //
            CollUtil.newArrayList(100, 299), //
            CollUtil.newArrayList(300, 599), //
            CollUtil.newArrayList(600, 999), //
            CollUtil.newArrayList(1000, 2000) //
    );

    // 捕鱼，根据场次，用户，鱼模型 id区分，上一次是爆发还是回收：0 回收（默认） 1 爆发
    public static final String FISHING_HIT_STATE_ROOM_INDEX_USER_MODEL_ID_PRE =
            "FISHING_HIT_STATE_ROOM_INDEX_USER_MODEL_ID_PRE:";

    // 捕鱼，根据场次，用户，鱼模型 id区分，本次的：kz,y几
    public static final String FISHING_KZ_INFO_ROOM_INDEX_USER_MODEL_ID_PRE =
            "FISHING_KZ_INFO_ROOM_INDEX_USER_MODEL_ID_PRE:";

    private static final WeightRandom<List<Double>> BFXY_WEIGHT_RANDOM = new WeightRandom<>();

    static {

        BFXY_WEIGHT_RANDOM.add(new WeightRandom.WeightObj<>(CollUtil.newArrayList(0.3d, 0.5d), 20));
        BFXY_WEIGHT_RANDOM.add(new WeightRandom.WeightObj<>(CollUtil.newArrayList(0.5d, 0.8d), 30));
        BFXY_WEIGHT_RANDOM.add(new WeightRandom.WeightObj<>(CollUtil.newArrayList(0.8d, 1.2d), 15));
        BFXY_WEIGHT_RANDOM.add(new WeightRandom.WeightObj<>(CollUtil.newArrayList(1.4d, 2d), 8));
        BFXY_WEIGHT_RANDOM.add(new WeightRandom.WeightObj<>(CollUtil.newArrayList(2d, 3d), 6));
        BFXY_WEIGHT_RANDOM.add(new WeightRandom.WeightObj<>(CollUtil.newArrayList(4d, 5d), 3));

    }

    /**
     * 击中鱼之后的处理，不需要上锁
     */
    private static void execFightFishHandlerFishAfterForNoLock(NewBaseFishingRoom newBaseFishingRoom,
                                                               FishingGamePlayer fishingGamePlayer, long needMoney, FishStruct fishStruct, long currentTimeMillis,
                                                               FishConfig fishConfig, long userId, long batteryLevel, long randomMoney, long winMoney,
                                                               boolean killFlag, long dayPoint) {

        // 金币同步
        // moneySync(newBaseFishingRoom, fishingGamePlayer, robotFlag);

        // 设置：今日的上榜权重（活跃榜）
        ActiveServiceImpl.activeTodayPut(new ActiveConfigBO(userId, 1));

        if (killFlag) {

            // 击杀之后的逻辑处理，不需要上锁的一些操作
            fightFishHandlerKillAfterForNoLock(newBaseFishingRoom, fishingGamePlayer, fishStruct, fishConfig, userId,
                    batteryLevel, randomMoney, winMoney, currentTimeMillis, dayPoint);

        }

    }

    /**
     * 判断击杀前的，一些处理
     */
    private static void fightFishHandlerPreKill(FishingGamePlayer fishingGamePlayer, long needMoney,
                                                NewBaseFishingRoom gameRoom) {

    }

    /**
     * 击杀之后的逻辑处理，不需要上锁的一些操作
     */
    private static void fightFishHandlerKillAfterForNoLock(NewBaseFishingRoom newBaseFishingRoom,
                                                           FishingGamePlayer fishingGamePlayer, FishStruct fishStruct, FishConfig fishConfig, long userId,
                                                           long batteryLevel, long randomMoney, long winMoney, long currentTimeMillis, long dayPoint) {

        // 处理动态刷新相关
        boolean durationRefreshFlag =
                MyRefreshFishingHelper.checkAndDurationRefreshFish(newBaseFishingRoom, fishStruct, true);

        // 处理：黄金鱼相关
        FishingChallengeFightFishUtil.handlerGoldNumber(newBaseFishingRoom, fishStruct);

        // 处理：boss鱼死亡之后的通用处理
        handlerBossKillAfter(newBaseFishingRoom, fishingGamePlayer, fishConfig, randomMoney, winMoney, userId,
                batteryLevel);

        // 发送：鱼死亡响应
        sendFishDeathResponse(newBaseFishingRoom, randomMoney, fishStruct, winMoney, userId, fishingGamePlayer,
                durationRefreshFlag, dayPoint);

        // 处理：游走字幕
        FishingChallengeFightFishUtil.handlerWanderSubtitle(fishingGamePlayer, fishConfig, randomMoney, winMoney,
                newBaseFishingRoom);


    }


    /**
     * 击杀之后的逻辑处理，需要上锁的一些操作
     */
    private static long fightFishHandlerKillAfterForLock(NewBaseFishingRoom gameRoom,
                                                         FishingGrandPrixPlayer fishingGamePlayer, FishConfig fishConfig, AbsFish absFish, long randomMoney,
                                                         FishStruct fishStruct, long winMoney, long userId, long batteryLevel, long personalNum,
                                                         OseePlayerEntity playerEntity) {

        // 移除：该鱼
        gameRoom.getFishMap().remove(fishStruct.getId());
//        gameRoom.removeFishMap(fishStruct.getId());

        // 鱼：死亡之后的处理，比如：移除缓存的倍数，发送播放特效响应等
        absFish.afterTheKill(gameRoom, winMoney, absFish.getKey());

        // 处理：今日积分
        return handleDayPoint(randomMoney, userId);

    }

    /**
     * 处理：今日积分
     */
    private static long handleDayPoint(long randomMoney, long userId) {

        int games = RedisUtil.val(FishingGrandPrixManager.PLAYER_GRANDPRIX_CONFIG_GAMES_KEY + userId, 0);

        long dayPoint =
                RedisUtil.get(FishingGrandPrixManager.PLAYER_GRANDPRIX_CONFIG_POINT_DAY_KEY + userId, games - 1);

        // log.info("userId：{}，games：{}，dayPoint：{}，randomMoney：{}", userId, games, dayPoint, randomMoney);

        dayPoint += randomMoney;

        RedisUtil.set(FishingGrandPrixManager.PLAYER_GRANDPRIX_CONFIG_POINT_DAY_KEY + userId, String.valueOf(dayPoint),
                games - 1);

        return dayPoint;

    }

    /**
     * 发送：鱼死亡响应
     */
    private static void sendFishDeathResponse(NewBaseFishingRoom newBaseFishingRoom, long randomMoney,
                                              FishStruct fishStruct, long winMoney, long userId, FishingGamePlayer fishingGamePlayer,
                                              boolean durationRefreshFlag, long dayPoint) {

        TtmyFishingGrandPrixMessage.FishingGrandPrixFightFishResponse.Builder builder =
                TtmyFishingGrandPrixMessage.FishingGrandPrixFightFishResponse.newBuilder();
        builder.setFishId(fishStruct.getId());
        builder.setPlayerId(userId);
        builder.setRestMoney(fishingGamePlayer.getMoney());
        builder.setDropMoney(winMoney);
        builder.setDayPoint(dayPoint);
        builder.setMultiple(randomMoney); // 鱼倍数

        MyRefreshFishingUtil.sendRoomMessage(newBaseFishingRoom,
                OseeMessage.OseeMsgCode.S_C_TTMY_FISHING_GRAND_PRIX_FIGHT_FISH_RESPONSE_VALUE, builder);

    }

    /**
     * 处理：boss鱼死亡之后的通用处理
     */
    private static void handlerBossKillAfter(NewBaseFishingRoom newBaseFishingRoom, FishingGamePlayer fishingGamePlayer,
                                             FishConfig fishConfig, long randomMoney, long winMoney, long userId, long batteryLevel) {

    }

    /**
     * 判断是否击杀，以及一些数据处理
     *
     * @param needHitCountRedisFlag       是否从 redis中获取：needHitCount
     * @param forceUpdateNeedHitCountFlag 是否强制更新 needHitCount
     * @return true 击杀
     */
    public static boolean fightFishHandlerKill(FishingGrandPrixPlayer player, FishConfig fishConfig, long randomMoney,
                                               long userId, ControlTypeEnum controlTypeEnum, Supplier<Double> supplier, Function<Double, Double> function,
                                               boolean needHitCountRedisFlag, long currentTimeMillis, double kz, boolean forceUpdateNeedHitCountFlag,
                                               ZtControlTypeEnum ztControlTypeEnum, NewBaseFishingRoom room, Long fishId, boolean dynamicMultipleFishFlag,
                                               long batteryLevel) {

        RAtomicDouble atomicDoubleCurrentHitCount = redissonClient.getAtomicDouble(
                PRE_KEY + controlTypeEnum.getCurrentHitCountPre() + userId + ":" + fishConfig.getModelId());

        int delta = 1;
        if (currentTimeMillis - player.getLastElectromagneticTime() < SKILL_ELETIC_TIME) {
            delta = delta + 1;
        }
        if (currentTimeMillis - player.getLastCritTime() < SKILL_CRIT_TIME) {
            delta = delta + 1;
        }

        // 增加当前命中次数
        double currentHitCount = 0d;

        if (dynamicMultipleFishFlag) { // 如果是：动态倍数的鱼

            if (ZtControlTypeEnum.BF_NORMAL.getType() == ztControlTypeEnum.getType()) { // 如果是爆发

                // 获取：是否是和上次相同的鱼
                boolean sameFishIdFlag =
                        FishingChallengeFightFishUtil.swsjcsGetSameFishIdFlag(fishConfig, userId, room, fishId);

                RBucket<Integer> dtjcxsBucket =
                        redissonClient.getBucket(DYNAMIC_MULTIPLE_FISH_DTJCXS_ROOM_INDEX_MODEL_ID_PRE + room.getRoomIndex()
                                + ":" + fishConfig.getModelId());

                Integer dtjcxs = null;

                if (sameFishIdFlag) { // 如果：和上一次的鱼 id相同
                    dtjcxs = dtjcxsBucket.get();
                }

                if (dtjcxs == null) {
                    dtjcxs = RandomUtil.getRandom(300, 600);
                    dtjcxsBucket.set(dtjcxs); // 设置到：缓存里
                }

                // log.info("dtjcxs：{}，modelId：{}，fishId：{}", dtjcxs, fishConfig.getModelId(), fishId);

                // 动态倍数鱼：奖池的值
                long dynamicMultipleFishPrizePoolValue =
                        FishingChallengeFightFishUtil.getDynamicMultipleFishPrizePoolAtomicLong(room.getRoomIndex()).get();

                if (dynamicMultipleFishPrizePoolValue > batteryLevel * (randomMoney + dtjcxs)) {
                    currentHitCount = atomicDoubleCurrentHitCount.addAndGet(delta);
                }

                forceUpdateNeedHitCountFlag = false; // 不强制更新：需要命中的总次数
                supplier = () -> {
                    return RandomUtil.getRandom(10d, 2500d); // 新的：取命中的总次数的方式
                };

            }

        } else {
            currentHitCount = atomicDoubleCurrentHitCount.addAndGet(delta);
        }

        // 获取：需要命中次数
        Double needHitCount = null;

        RBucket<Double> bucketNeedHitCount = null;

        if (needHitCountRedisFlag) {

            bucketNeedHitCount = redissonClient
                    .getBucket(PRE_KEY + controlTypeEnum.getNeedHitCountPre() + userId + ":" + fishConfig.getModelId());

            if (forceUpdateNeedHitCountFlag) {
                needHitCount = null;
            } else {
                // 获取：需要命中次数
                needHitCount = bucketNeedHitCount.get();
            }

        }

        if (needHitCount == null) {

            if (supplier == null) {

                // 设置：kz等信息，目的：方便写入日志
                redissonClient
                        .<String>getBucket(FISHING_KZ_INFO_ROOM_INDEX_USER_MODEL_ID_PRE + room.getRoomIndex() + ":"
                                + player.getId() + ":" + fishConfig.getModelId())
                        .set(
                                BigDecimal.valueOf(kz).setScale(2, RoundingMode.HALF_UP) + "," + player.getNeedHitNumberMode());

                double mz = 1.0 / (randomMoney * (1 + kz));

                player.setMz((long) (randomMoney * (1 + kz)));

                needHitCount = MyMathUtil.floatKillNormalDistribution(mz); // 获取：正态分布值

                if (function != null) {
                    needHitCount = function.apply(needHitCount);
                }

            } else {
                needHitCount = supplier.get();
            }

            needHitCount = BigDecimal.valueOf(needHitCount).setScale(1, RoundingMode.HALF_UP).doubleValue();

            if (bucketNeedHitCount != null) {
                bucketNeedHitCount.set(needHitCount); // 设置到：redis里面
            }

            // 添加：一个鱼的倍数
            RedisUtil.zAdd(PRE_KEY + CHALLENGE_USER_FISH_MODEL_ID_MULT_PRE + userId,
                    String.valueOf(fishConfig.getModelId()), randomMoney);

        } else {
            if (dynamicMultipleFishFlag) { // 如果是：动态倍数的鱼
                // 更新：鱼的倍数
                RedisUtil.zAdd(PRE_KEY + CHALLENGE_USER_FISH_MODEL_ID_MULT_PRE + userId,
                        String.valueOf(fishConfig.getModelId()), randomMoney);
            }
        }

        // log.info("needHitCount：{}，currentHitCount：{}，是否击杀：{}，randomMoney：{}，modelId：{}，fishId：{}，累加的次数：{}，原始次数：{}",
        // needHitCount, currentHitCount, currentHitCount >= needHitCount, randomMoney, fishConfig.getModelId(),
        // fishId, currentHitCount - currentHitCountOriginal, currentHitCountOriginal);

        player.setCurrentHitNumber((long) currentHitCount);
        player.setNeedHitNumber(needHitCount);
        player.setFishRewardMult(randomMoney);
//        log.info("kill-次数判断4：{}，currentHitCount：{}，needHitCount：{}",
//                currentHitCount >= needHitCount, currentHitCount, needHitCount);
        return currentHitCount >= needHitCount;

    }

}
