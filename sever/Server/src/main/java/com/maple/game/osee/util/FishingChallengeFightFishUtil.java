package com.maple.game.osee.util;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.text.StrBuilder;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import com.google.protobuf.GeneratedMessage;
import com.maple.database.config.redis.RedisHelper;
import com.maple.engine.container.DataContainer;
import com.maple.engine.container.UserContainer;
import com.maple.engine.data.ServerUser;
import com.maple.engine.utils.ThreadPoolUtils;
import com.maple.game.osee.common.RedisUtil;
import com.maple.game.osee.dao.data.entity.KillBossEntity;
import com.maple.game.osee.dao.data.entity.OseePlayerEntity;
import com.maple.game.osee.dao.data.entity.UserProps;
import com.maple.game.osee.dao.data.mapper.OseePlayerMapper;
import com.maple.game.osee.entity.ItemChangeReason;
import com.maple.game.osee.entity.ItemId;
import com.maple.game.osee.entity.NewBaseGamePlayer;
import com.maple.game.osee.entity.NewBaseGameRoom;
import com.maple.game.osee.entity.fishing.FishingGamePlayer;
import com.maple.game.osee.entity.fishing.NewBaseFishingRoom;
import com.maple.game.osee.entity.fishing.challenge.FishingChallengePlayer;
import com.maple.game.osee.entity.fishing.csv.file.*;
import com.maple.game.osee.entity.fishing.game.FireStruct;
import com.maple.game.osee.entity.fishing.game.FishStruct;
import com.maple.game.osee.manager.PlayerManager;
import com.maple.game.osee.manager.UserPropsManager;
import com.maple.game.osee.manager.fishing.FishingChallengeManager;
import com.maple.game.osee.manager.fishing.FishingManager;
import com.maple.game.osee.model.bo.ActiveConfigBO;
import com.maple.game.osee.model.bo.BdzConfigBO;
import com.maple.game.osee.model.bo.FishMultRangeBO;
import com.maple.game.osee.model.dto.FireInfoDTO;
import com.maple.game.osee.model.dto.ProfitRatioDTO;
import com.maple.game.osee.model.entity.AccountDetailDO;
import com.maple.game.osee.pojo.fish.AbsFish;
import com.maple.game.osee.pojo.fish.FishFactory;
import com.maple.game.osee.proto.OseeMessage;
import com.maple.game.osee.proto.OseePublicData;
import com.maple.game.osee.proto.fishing.TtmyFishingChallengeMessage;
import com.maple.game.osee.service.impl.ActiveServiceImpl;
import com.maple.gamebase.container.GameContainer;
import com.maple.gamebase.data.BaseGamePlayer;
import com.maple.gamebase.data.BaseGameRoom;
import com.maple.network.manager.NetManager;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.redisson.api.*;
import org.redisson.codec.JsonJacksonCodec;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

import static com.maple.game.osee.controller.gm.GmCommonController.handleRoomIndexStr;
import static com.maple.game.osee.manager.fishing.FishingManager.*;
import static com.maple.game.osee.proto.OseeMessage.OseeMsgCode.S_C_TTMY_FISHING_CHALLENGE_REPLY_FIGHT_FISH_RESPONSE_VALUE;

/**
 * 挑战场捕鱼：击中鱼，工具类
 */
@Component
@Slf4j
public class FishingChallengeFightFishUtil {

    /**
     * 使用的：货币
     */
    public static final ItemId USE_MONEY = ItemId.DRAGON_CRYSTAL;

    private static OseePlayerMapper playerMapper;
    public static RedissonClient redissonClient;
    private static UserPropsManager userPropsManager;

    public FishingChallengeFightFishUtil(OseePlayerMapper playerMapper, RedissonClient redissonClient,
                                         UserPropsManager userPropsManager) {

        FishingChallengeFightFishUtil.playerMapper = playerMapper;
        FishingChallengeFightFishUtil.redissonClient = redissonClient;
        FishingChallengeFightFishUtil.userPropsManager = userPropsManager;

    }

    private static final ScheduledThreadPoolExecutor SCHEDULED_THREAD_POOL_EXECUTOR =
            ThreadUtil.createScheduledExecutor(1);

    // 消耗：击杀上一条鱼后，开始统计，击杀鱼后清零。（包含期间攻击所有目标的消耗金币，以场次为单位）
    public static final String FISHING_KILL_GAP_XH_ROOM_INDEX_USER_PRE = "FISHING_KILL_GAP_XH_ROOM_INDEX_USER_PRE:";

    /**
     * 玩家击中鱼，一系列的处理
     *
     * @param deductNeedMoneyFlag 是否扣除 needMoney
     */
    public static boolean playerFightFish(NewBaseFishingRoom room, FishingChallengePlayer fishingGamePlayer,
                                          @Nullable Long fireId, List<Long> fishIdList, boolean replyFightFlag, @Nullable Long needMoney,
                                          @Nullable Integer delta, boolean deductNeedMoneyFlag, @Nullable Integer hitType) {

        // 处理：击中鱼
        return fightFish(room, fishingGamePlayer, fireId, fishIdList, replyFightFlag, needMoney, delta,
                deductNeedMoneyFlag, hitType);

    }

    /**
     * 处理：击中鱼
     *
     * @return true 扣钱了 false 没有扣钱
     */
    public static boolean fightFish(NewBaseFishingRoom room, FishingChallengePlayer player, @Nullable Long fireId,
                                    List<Long> fishIdList, boolean replyFightFlag, @Nullable Long needMoney, @Nullable Integer delta,
                                    boolean deductNeedMoneyFlag, @Nullable Integer hitType) {
        // 判断fishId 是否为空
        if (fireId != null) {
            FireStruct fireStruct = fightFishHandlerFire(player, fireId); // 处理子弹
            if (fireStruct == null) {
                return false;
            }
        }
        // 获取玩家实体
        OseePlayerEntity playerEntity = PlayerManager.getPlayerEntity(player.getUser());
        boolean res = false;
        synchronized (playerEntity) {
            long currentTimeMillis = System.currentTimeMillis();
            for (long item : fishIdList) {
                try {
                    // 处理鱼
                    boolean resTemp = fightFishHandlerFish(room, item, player, currentTimeMillis, replyFightFlag,
                            playerEntity, needMoney, delta, deductNeedMoneyFlag, hitType);
                    if (res == false) {
                        res = resTemp;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return res;
    }

    /**
     * 处理鱼
     *
     * @return true 扣钱了 false 没有扣钱
     */
    private static boolean fightFishHandlerFish(NewBaseFishingRoom room, long fishId, FishingChallengePlayer player,
                                                long currentTimeMillis, boolean replyFightFlag, OseePlayerEntity playerEntity, @Nullable Long needMoney,
                                                @Nullable Integer delta, boolean deductNeedMoneyFlag, @Nullable Integer hitType) {

        // 获取：鱼对象
        FishStruct fishStruct = room.getFishMap().get(fishId);
        if (ObjectUtils.isEmpty(fishStruct) && room.getRoomIndex() == 1411 || room.getRoomIndex() == 1412) {
            for (Map.Entry<FishStruct, List<Integer>> fishStructListEntry : room.getRoomFishForbiddenTypes().entrySet()) {
                if (fishStructListEntry.getKey().getId() == fishId) {
                    room.getFishMap().put(fishStructListEntry.getKey().getId(), fishStructListEntry.getKey());
                    fishStruct = fishStructListEntry.getKey();
                }
            }
        }

        if (fishStruct == null) {
            return false;
        }

        if (fishStruct.isWorldBossFlag()) { // 如果是：世界 boss
            if (CollUtil.isNotEmpty(fishStruct.getFishStructList())) {
                if (fishStruct.getFishStructList().get(0).getId() == fishStruct.getId()
                        && fishStruct.getMonsterHpDeductType() == 2) {
                    // 如果是：世界 boss，并且怪物血量扣除方式 == 2，则第一条鱼无法直接攻击
                    return false;
                }
            }
        }
        // 执行：处理鱼
        return execFightFishHandlerFish(room, fishId, player, needMoney, fishStruct, currentTimeMillis, replyFightFlag,
                playerEntity, delta, deductNeedMoneyFlag, hitType);
    }

    // 通过场次区分，动态倍数鱼奖池
    public static final String DYNAMIC_MULTIPLE_FISH_PRIZE_POOL_ROOM_INDEX_PRE =
            "DYNAMIC_MULTIPLE_FISH_PRIZE_POOL_ROOM_INDEX_PRE:";

    // 通过场次，鱼 modelId区分，动态倍数鱼，dtjcxs
    public static final String DYNAMIC_MULTIPLE_FISH_DTJCXS_ROOM_INDEX_MODEL_ID_PRE =
            "DYNAMIC_MULTIPLE_FISH_DTJCXS_ROOM_INDEX_MODEL_ID_PRE:";

    // 动态倍数，鱼 modelId集合
    public static final List<Integer> DYNAMIC_MULT_FISH_MODEL_ID_LIST = CollUtil.newArrayList(35, 37);

    /**
     * 获取；动态倍数鱼的奖池
     */
    public static RAtomicLong getDynamicMultipleFishPrizePoolAtomicLong(int roomIndex) {

        return redissonClient.getAtomicLong(DYNAMIC_MULTIPLE_FISH_PRIZE_POOL_ROOM_INDEX_PRE + roomIndex);

    }


    /**
     * 一些通用参数的处理，备注：可以不加锁，进行调用 cx：是盈亏，打鱼往里面填，死鱼从里面扣
     *
     * @param addValue 大于 0 表示击中鱼 小于 0 表示打死鱼
     */
    private static void fightFishHandlerCommonParam(Double addValue, long userId, boolean updateXhFlag,
                                                    Integer fishType2) {

        if (fishType2 == null) {
            return;
        }

        if (addValue == 0) {
            return;
        }

        if (updateXhFlag) {

            RAtomicDouble xhAtomicDouble =
                    redissonClient.getAtomicDouble(XH_CHALLENGE_USER_FISH_TYPE_2_PRE + userId + ":" + fishType2);

            // 增加：xh
            double xh = xhAtomicDouble.addAndGet(addValue);

            if (xh < 0) {
                xhAtomicDouble.delete();
            }

            // // log.info("xh：{}，addValue：{}", xh, addValue);

        }

    }

    /**
     * 清零 xh
     */
    public static void deleteAllXh(long userId) {

        RBatch batch = redissonClient.createBatch();

        for (int i = 1; i < 5; i++) { // 1 2 3 4

            batch.getAtomicDouble(XH_CHALLENGE_USER_FISH_TYPE_2_PRE + userId + ":" + i).deleteAsync();

        }

        batch.execute();

    }


    /**
     * 获取：下一个节点的值
     */
    public static double getNextJczd0(long batteryLevel, long userId, CallBack<Integer> bdCallBack,
                                      boolean batteryLevelIsBlankFlag, CallBack<String> batteryLevelCallBack) {

        RList<Double> jczd0ListRList = redissonClient.getList(FISHING_JCZD0_LIST_USER_PRE + userId);

        Double nextJczd0Double = jczd0ListRList.get(bdCallBack.getValue() + 1);

        if (nextJczd0Double == null) {
            return 0d;
        }

        // 下一个期望节点
        double nextJczd0 = nextJczd0Double;

        if (BooleanUtil.isFalse(batteryLevelIsBlankFlag)) { // 如果有个控

            String personalJczd0ListBatteryLevel = batteryLevelCallBack.getValue();

            double jczd0ListBatteryLevel = new BigDecimal(personalJczd0ListBatteryLevel).doubleValue();

            nextJczd0 = nextJczd0 * jczd0ListBatteryLevel / batteryLevel;

            // log.info("jczd0ListBatteryLevel：{}，batteryLevel：{}", jczd0ListBatteryLevel, batteryLevel);

        }

        return nextJczd0;

    }

    /**
     * 获取：上一个节点的值
     */
    @Nullable
    public static Double getPreJczd0(long batteryLevel, long userId, CallBack<Integer> bdCallBack,
                                     boolean batteryLevelIsBlankFlag, CallBack<String> batteryLevelCallBack, NewBaseGamePlayer player) {

        Integer bd = bdCallBack.getValue();

        // 上一个期望节点
        Double preJczd0;

        // log.info("bd：{}", bd);

        if (bd == 0) { // 获取：初始节点

            preJczd0 = getChuShiBulletNumber(player, batteryLevel);

        } else {

            RList<Double> jczd0ListRList = redissonClient.getList(FISHING_JCZD0_LIST_USER_PRE + userId);

            // 上一个期望节点
            preJczd0 = jczd0ListRList.get(bd - 1);

            if (preJczd0 == null) {
                return null;
            }

            if (BooleanUtil.isFalse(batteryLevelIsBlankFlag)) { // 如果有个控

                String personalJczd0ListBatteryLevel = batteryLevelCallBack.getValue();

                double jczd0ListBatteryLevel = new BigDecimal(personalJczd0ListBatteryLevel).doubleValue();

                preJczd0 = preJczd0 * jczd0ListBatteryLevel / batteryLevel;

                // log.info("jczd0ListBatteryLevel：{}，batteryLevel：{}", jczd0ListBatteryLevel, batteryLevel);

            }

        }

        return preJczd0;

    }


    /**
     * 执行：检查奖池
     */
    public static boolean doCheckPrizePoolInventory(long winMoney, int roomIndex,
                                                    @Nullable CallBack<String> reasonStrCallBack) {

        // 判断：是否超过奖池库存
        List<Long> produceGoldTotalList = new ArrayList<>(); // 每个房间 产出的 金币L，固定倍数
        List<Long> usedXhTotalList = new ArrayList<>(); // 每个房间的 子弹消耗，固定倍数

        List<Long> produceGoldTotalDynamicList = new ArrayList<>(); // 每个房间 产出的 金币L，动态倍数
        List<Long> usedXhTotalDynamicList = new ArrayList<>(); // 每个房间的 子弹消耗，动态倍数

        List<BigDecimal> profitRatioHopeList = new ArrayList<>(); // 每个房间的 期望收益比

        List<BigDecimal> profitRatioRealList = new ArrayList<>(); // 每个房间的 实际收益比，固定倍数
        List<BigDecimal> profitRatioRealDynamicList = new ArrayList<>(); // 每个房间的 实际收益比，动态倍数

        List<BigDecimal> getGoldHopeList = new ArrayList<>(); // 每个房间的 期望收益金币，固定倍数
        List<BigDecimal> getGoldRealList = new ArrayList<>(); // 每个房间的 实际收益金币，固定倍数

        List<BigDecimal> getGoldHopeDynamicList = new ArrayList<>(); // 每个房间的 期望收益金币，动态倍数
        List<BigDecimal> getGoldRealDynamicList = new ArrayList<>(); // 每个房间的 实际收益金币，动态倍数

        int i = 0;

        // 处理：收益比，基础的集合，固定倍数
        FishingHelper.handlerProfitRatioBaseList(produceGoldTotalList, usedXhTotalList, profitRatioHopeList,
                profitRatioRealList, roomIndex);

        // 处理：收益比，基础的集合，动态倍数
        FishingHelper.handlerProfitRatioBaseDynamicList(produceGoldTotalDynamicList, usedXhTotalDynamicList,
                profitRatioRealDynamicList, roomIndex);

        getGoldHopeList.add(new BigDecimal(usedXhTotalList.get(i))
                .multiply(BigDecimal.ONE.subtract(profitRatioHopeList.get(i))).setScale(5, BigDecimal.ROUND_HALF_DOWN));

        getGoldRealList.add(new BigDecimal(usedXhTotalList.get(i)).subtract(new BigDecimal(produceGoldTotalList.get(i)))
                .setScale(5, BigDecimal.ROUND_HALF_DOWN));

        getGoldHopeDynamicList.add(new BigDecimal(usedXhTotalDynamicList.get(i))
                .multiply(BigDecimal.ONE.subtract(profitRatioHopeList.get(i))).setScale(5, BigDecimal.ROUND_HALF_DOWN));

        getGoldRealDynamicList.add(new BigDecimal(usedXhTotalDynamicList.get(i))
                .subtract(new BigDecimal(produceGoldTotalDynamicList.get(i))).setScale(5, BigDecimal.ROUND_HALF_DOWN));

        // 奖池库存
        BigDecimal prizePoolInventoryBigDecimal = getGoldRealList.get(i).add(getGoldRealDynamicList.get(i))
                .subtract(getGoldHopeList.get(i)).subtract(getGoldHopeDynamicList.get(i));

        // log.info("判断：奖池库存，winMoney：{}，prizePoolInventoryBigDecimal：{}，roomIndex：{}", winMoney,
        // prizePoolInventoryBigDecimal.longValue(), roomIndex);

        // log.info("prizePoolInventoryBigDecimal：{}，getGoldReal：{}，getGoldHope：{}，roomIndex：{}",
        // prizePoolInventoryBigDecimal.longValue(), getGoldRealList.get(i), getGoldHopeList.get(i), roomIndex);

        if (winMoney > prizePoolInventoryBigDecimal.longValue()) { // 如果：发放奖励的子弹数 > 奖池库存

            // log.info("超过：奖池库存，winMoney：{}，prizePoolInventoryBigDecimal：{}", winMoney,
            // prizePoolInventoryBigDecimal.longValue());

            if (reasonStrCallBack != null) {

                reasonStrCallBack.setValue(reasonStrCallBack.getValue() + "，winMoney：" + winMoney + "，奖池库存："
                        + prizePoolInventoryBigDecimal.setScale(2, RoundingMode.HALF_UP).toPlainString());

            }

            return false;

        }

        return true;

    }


    /**
     * 处理：玩家进场收益比
     */
    public static double handleAndGetJcrtp1(double jcProduce, double jcXh) {

        double jcrtp1;

        if (jcXh == 0) {

            jcrtp1 = 0.7;

        } else {

            jcrtp1 = NumberUtil.div(jcProduce, jcXh, 4);

            if (jcrtp1 == 0) {

                jcrtp1 = 0.7;

            }

        }

        return jcrtp1;

    }

    // 检查：是否是奖券鱼
    public static final Function<Integer, Boolean> CHECK_LOTTERY_FUNC = modelId -> modelId == 90;

    /**
     * 执行：处理鱼
     *
     * @return true 扣钱了 false 没有扣钱
     */
    public static boolean execFightFishHandlerFish(NewBaseFishingRoom room, @Nullable Long fishId,
                                                   FishingChallengePlayer player, @Nullable Long needMoney, FishStruct fishStruct, long currentTimeMillis,
                                                   boolean replyFightFlag, OseePlayerEntity playerEntity, @Nullable Integer delta, boolean deductNeedMoneyFlag,
                                                   @Nullable Integer hitType) {

        // 注意：这个对象不要使用，不然会出现并发问题
        FishConfig fishConfig = DataContainer.getData(fishStruct.getConfigId(), FishConfig.class);

        // 获取：对应的鱼
        final AbsFish absFish = FishFactory.create(fishStruct, fishConfig, player.getUser(), room);

        final long userId = player.getUser().getId(); // 用户 id
        long randomMoney; // 鱼的倍数
        long winMoney; // 本次赢的钱
        boolean killFlag; // 是否击杀鱼
        long batteryLevel; // 炮倍等级
        Map<Long, Long> refFishIdAndMultipleMap = new HashMap<>(); // 关联的 鱼 id 和 倍数，备注：不包含自己
        boolean quanPingZhaDanFlag = fishConfig.getModelId() == 89 || fishConfig.getModelId() == 1201; // 是否是：全屏炸弹

        // 是否：发送鱼死亡响应
        CallBack<Boolean> killAndSendFishFlagCallBack = new CallBack<>(true);
        synchronized (fishStruct) { // 锁鱼
            if (fishId != null && room.getFishMap().get(fishId) == null) { // 如果：鱼不存在了
                return false;
            }

            synchronized (playerEntity) { // 锁用户，目的：退出房间时，可以原子操作
                if (room.getGamePlayerById(userId) == null) { // 如果：用户已经退出房间了
                    return false;
                }

                batteryLevel = player.getBatteryLevel(); // 炮倍
                if (needMoney == null) {
                    // 需要花费的金币
                    needMoney = FishingChallengeManager.getNeedMoney(batteryLevel, currentTimeMillis, player, room);
                }

                if (MyRefreshFishingUtil.INTEGRAL_ROOM_INDEX == room.getRoomIndex()) { // 积分场：打鱼逻辑不一样
                    return fightFishIntegralRoom(player, fishConfig, fishStruct, needMoney, room, batteryLevel,
                            playerEntity);
                }

                if (deductNeedMoneyFlag) {
                    // 检查金币是否足够
                    if (BooleanUtil.isFalse(PlayerManager.checkItem(player.getUser(), USE_MONEY, needMoney))) {
                        bankruptcyHandler(room, player, true, "金币不足,请前往充值", fishConfig, playerEntity);
                        sendReplyFightFishResponse(player, replyFightFlag); // 发送：命中鱼请求
                        return false;
                    }
                }
                player.setCurrentHitFishName(fishConfig.getName());

                // 只要玩家命中鱼，那么鱼就可以攻击该玩家
                fishStruct.getTempMonsterAttackPlayerIdSet().add(userId);

                // 判断击杀前的，一些处理
                fightFishHandlerPreKill(player, needMoney, room, replyFightFlag, playerEntity,
                        deductNeedMoneyFlag);

                // 是否是：动态倍数鱼
                boolean dynamicMultipleFishFlag =
                        FishingChallengeFightFishUtil.DYNAMIC_MULT_FISH_MODEL_ID_LIST.contains(fishConfig.getModelId());
                if (dynamicMultipleFishFlag) {
                    // 增加；动态倍数鱼的奖池
                    FishingChallengeFightFishUtil.getDynamicMultipleFishPrizePoolAtomicLong(room.getRoomIndex())
                            .addAndGet(needMoney);
                }
                CallBack<Double> jczd0CallBack = new CallBack<>();
                CallBack<Double> realJczd0CallBack = new CallBack<>();
                CallBack<Integer> bdCallBack = new CallBack<>();
                CallBack<Boolean> batteryLevelIsBlankFlagCallBack = new CallBack<>(true);
                CallBack<String> batteryLevelCallBack = new CallBack<>();

                boolean chuShiFlag = false;
                if (bdCallBack.getValue() != null) {
                    chuShiFlag = bdCallBack.getValue() == 0;
                    if (chuShiFlag) {
                        absFish.setType((byte) 2);
                    }
                }

                if (quanPingZhaDanFlag) {
                    // 处理：全屏炸弹的倍数
                    randomMoney = handleQuanPingZhaDanFlagRandomMoney(room, player, fishStruct, refFishIdAndMultipleMap);
                    if (randomMoney == 1) {
                        try {
                            Thread.sleep(1000);

                            randomMoney = handleQuanPingZhaDanFlagRandomMoney(room, player, fishStruct, refFishIdAndMultipleMap);
                            if (randomMoney == 1) {
                                log.info("sleep randomMoney==1");
                            }
                        } catch (InterruptedException e) {
//                            throw new RuntimeException(e);
                        }
                    }
                    absFish.setComputeMultipleType(4);
                } else {
                    // 处理：最小倍数和最大倍数
                    handleMinAndMaxRandomMoney(player, realJczd0CallBack, jczd0CallBack,
                            batteryLevel, bdCallBack, batteryLevelIsBlankFlagCallBack, batteryLevelCallBack, userId,
                            absFish);
                    randomMoney = absFish.getMultiple(); // 设置：鱼的倍数
                    // 处理：出奇制胜的倍数
                    randomMoney =
                            handleChuQiZhiShengRandomMoney(room, fishStruct, randomMoney, refFishIdAndMultipleMap, absFish);
                }

                // 本次赢的钱
                winMoney = FishingChallengeManager.getWinMoney(randomMoney, batteryLevel);

                CallBack<Long> randomMoneyCallBack = new CallBack<>(randomMoney);
                CallBack<Long> winMoneyCallBack = new CallBack<>(winMoney);
                CallBack<byte[]> messageByteArrCallBack = new CallBack<>(); // 击杀鱼之后，要使用的 messageByteArr
                // 执行：处理鱼
                killFlag = RandomUtil.isHappen(BigDecimal.valueOf(1.0d / randomMoney).setScale(5, RoundingMode.HALF_UP).doubleValue() * 100);

                randomMoney = randomMoneyCallBack.getValue(); // 重新赋值：鱼的倍数
                winMoney = winMoneyCallBack.getValue(); // 重新赋值：本次赢的钱
                if (killFlag) {
                    // 击杀之后的逻辑处理，需要上锁的一些操作
                    fightFishHandlerKillAfterForLock(room, player, fishConfig, absFish, randomMoney, fishStruct,
                            winMoney, userId, batteryLevel, playerEntity, messageByteArrCallBack.getValue(),
                            currentTimeMillis, bdCallBack, refFishIdAndMultipleMap, quanPingZhaDanFlag,
                            realJczd0CallBack.getValue(), delta);
                }

                long checkMoney = FishingChallengeFightFishUtil.getJczd1(player.getMoney(), 1, player.getId());
                if (checkMoney == 0) {
                    // 破产通用处理
                    FishingChallengeFightFishUtil.bankruptcyCommonHandler(player, room, playerEntity, false);
                }

                // 击中鱼之后的处理，需要上锁
                execFightFishHandlerFishAfterForLock(fishStruct, currentTimeMillis, player, room, randomMoney, userId,
                        refFishIdAndMultipleMap, killFlag, quanPingZhaDanFlag, winMoney, hitType,
                        killAndSendFishFlagCallBack);
            }
        }

        // 击中鱼之后的处理，不需要上锁
        execFightFishHandlerFishAfterForNoLock(room, player, needMoney, fishStruct, currentTimeMillis, fishConfig,
                userId, batteryLevel, randomMoney, winMoney, killFlag, refFishIdAndMultipleMap, absFish, hitType,
                killAndSendFishFlagCallBack);
        return true;
    }

    private static void handleMinAndMaxRandomMoney(FishingChallengePlayer player,
                                                   CallBack<Double> realJczd0CallBack, CallBack<Double> jczd0CallBack, long batteryLevel,
                                                   CallBack<Integer> bdCallBack, CallBack<Boolean> batteryLevelIsBlankFlagCallBack,
                                                   CallBack<String> batteryLevelCallBack, long userId, AbsFish absFish) {
        Double realJczd0 = realJczd0CallBack.getValue(); // 旧的 jczd0的值

        Double jczd0 = jczd0CallBack.getValue(); // 新的或者旧的 jczd0的值

        if (realJczd0 == null || jczd0 == null) {
            return;
        }

        double bfqsjd = realJczd0; // 最小值

        double pcbfjd = jczd0; // 最大值

        // 获取：下一个节点的值
        double nextJczd0 = getNextJczd0(batteryLevel, player.getId(), bdCallBack,
                batteryLevelIsBlankFlagCallBack.getValue(), batteryLevelCallBack);

        if (nextJczd0 > jczd0) {

            pcbfjd = nextJczd0;

        }

        if (realJczd0.equals(jczd0)) {

            Double preJczd0 = getPreJczd0(batteryLevel, userId, bdCallBack, batteryLevelIsBlankFlagCallBack.getValue(),
                    batteryLevelCallBack, player);

            if (preJczd0 != null) {

                bfqsjd = preJczd0;

            }

        }

        absFish.setMinRandomMoney(bfqsjd);

        absFish.setMaxRandomMoney(pcbfjd);

        // log.info("鱼：{}，最小值：{}，最大值：{}", absFish.getConfig().getName(), bfqsjd, pcbfjd);

    }

    /**
     * 击中鱼之后的处理，需要上锁
     */
    private static void execFightFishHandlerFishAfterForLock(FishStruct fishStruct, long currentTimeMillis,
                                                             FishingChallengePlayer player, NewBaseFishingRoom room, long randomMoney, long userId,
                                                             Map<Long, Long> refFishIdAndMultipleMap, boolean killFlag, boolean quanPingZhaDanFlag, long winMoney,
                                                             @Nullable Integer hitType, CallBack<Boolean> killAndSendFishFlagCallBack) {

        if (fishStruct.isWorldBossFlag()) {

            if (fishStruct.getMonsterHpDeductType() == 2 && killFlag
                    && CollUtil.isNotEmpty(fishStruct.getFishStructList())) {

                FishStruct firstFishStruct = fishStruct.getFishStructList().get(0);

                // log.info("roomCode：{}，fishId：{}，modelId：{}，monsterMaxHp：{}，monsterHpCount：{}", room.getCode(),
                // firstFishStruct.getId(), firstFishStruct.getModelId(), firstFishStruct.getMonsterMaxHp(),
                // firstFishStruct.getMonsterHpCount());

                if (firstFishStruct.getMonsterHpCount() <= 0) {

                    // 移除：当前这条世界 boss鱼
                    doRemoveCurrentWorldBoss(firstFishStruct, null, firstFishStruct, room, refFishIdAndMultipleMap,
                            quanPingZhaDanFlag, player, userId, randomMoney, killAndSendFishFlagCallBack);
                    return;

                }

                // 每一管血，有多少格血
                int hpCellNumber = firstFishStruct.getMonsterMaxHp() / firstFishStruct.getMonsterHpCount();

                TtmyFishingChallengeMessage.FishingChallengeHitFishResponse.Builder builder = null;

                if (winMoney > 0) {

                    // 不是最大命中次数，强制死亡时，才处理：第一个怪物血量
                    builder = handleMonsterHp(firstFishStruct, currentTimeMillis, player, room, randomMoney, userId,
                            refFishIdAndMultipleMap, killFlag, quanPingZhaDanFlag, hpCellNumber, hpCellNumber,
                            false, winMoney, hitType, killAndSendFishFlagCallBack);

                }

                // // 延迟
                // int delay = fishStruct.getDelayTime() * 1000;

                // 延迟：3秒
                // TtmyFishingChallengeMessage.FishingChallengeHitFishResponse.Builder finalBuilder = builder;

                // ThreadPoolUtils.TIMER_SERVICE_POOL.schedule(() -> {

                int monsterHp = firstFishStruct.getMonsterHp();

                // monsterHp = 0;

                if (monsterHp <= 0) { // 移除：全部房间该组合的所有鱼

                    // 移除：当前这条世界 boss鱼
                    doRemoveCurrentWorldBoss(firstFishStruct, builder, firstFishStruct, room, refFishIdAndMultipleMap,
                            quanPingZhaDanFlag, player, userId, randomMoney, killAndSendFishFlagCallBack);

                } else {

                    // 同步：第一条鱼的血量
                    syncFirstFishStruct(firstFishStruct, monsterHp, builder, player);

                    // 房间移除，该鱼
                    doHandleRemove(builder, refFishIdAndMultipleMap, quanPingZhaDanFlag, player, userId, randomMoney,
                            fishStruct, room, true, killAndSendFishFlagCallBack);

                }

                // }, delay, TimeUnit.MILLISECONDS);

            }

        } else if (fishStruct.getMonsterAttackScopeList() != null) {

            // 处理：怪物血量
            handleMonsterHp(fishStruct, currentTimeMillis, player, room, randomMoney, userId,
                    refFishIdAndMultipleMap, killFlag, quanPingZhaDanFlag, null, null, true, winMoney, hitType,
                    killAndSendFishFlagCallBack);

        }

    }

    /**
     * 通过 ruleId，获取房间集合
     */
    @NotNull
    public static List<NewBaseFishingRoom> getRoomListByRuleId(long ruleId) {

        CopyOnWriteArraySet<Integer> roomCodeSet =
                MyRefreshFishingHelper.REFRESH_TIME_RULE_ID_AND_ROOM_CODE_MAP.get(ruleId);

        if (CollUtil.isEmpty(roomCodeSet)) {
            return new ArrayList<>();
        }

        List<NewBaseFishingRoom> list = new ArrayList<>();

        for (Integer item : roomCodeSet) {

            BaseGameRoom room = GameContainer.roomCodeMap.get(item);

            if (room == null) {
                continue;
            }

            list.add((NewBaseFishingRoom) room);

        }

        return list;

    }

    /**
     * 同步：第一条鱼的血量
     */
    private static void syncFirstFishStruct(FishStruct firstFishStruct, int monsterHp,
                                            TtmyFishingChallengeMessage.FishingChallengeHitFishResponse.Builder builder, FishingChallengePlayer player) {

        if (!firstFishStruct.isWorldBossFlag()) {
            return;
        }

        if (builder == null) {
            return;
        }

        List<NewBaseFishingRoom> roomList = getRoomListByRuleId(firstFishStruct.getRuleId());

        roomList.forEach(item -> {

            FishStruct syncFishStruct = item.getFishMap().get(firstFishStruct.getId());

            if (syncFishStruct != null) {

                syncFishStruct.setMonsterHp(monsterHp);

                // 发送：命中鱼的响应
                MyRefreshFishingUtil.sendRoomMessage(item,
                        OseeMessage.OseeMsgCode.S_C_TTMY_FISHING_CHALLENGE_HIT_FISH_RESPONSE_VALUE, builder);

            }

        });

    }

    /**
     * 移除：当前这条世界 boss鱼
     */
    private static void doRemoveCurrentWorldBoss(FishStruct fishStruct,
                                                 @Nullable TtmyFishingChallengeMessage.FishingChallengeHitFishResponse.Builder builder,
                                                 FishStruct firstFishStruct, NewBaseFishingRoom room, Map<Long, Long> refFishIdAndMultipleMap,
                                                 boolean quanPingZhaDanFlag, FishingChallengePlayer player, long userId, long randomMoney,
                                                 CallBack<Boolean> killAndSendFishFlagCallBack) {

        if (firstFishStruct.getId() == fishStruct.getId()) {

            List<FishStruct> fishStructsListTemp = new ArrayList<>(firstFishStruct.getFishStructList());

            fishStructsListTemp.forEach(item -> {

                // 在所有房间都移除：该鱼
                handleRemove(builder, refFishIdAndMultipleMap, quanPingZhaDanFlag, player, userId, randomMoney,
                        item, killAndSendFishFlagCallBack);

            });

        } else {

            // 在所有房间都移除：该鱼
            handleRemove(builder, refFishIdAndMultipleMap, quanPingZhaDanFlag, player, userId, randomMoney,
                    fishStruct, killAndSendFishFlagCallBack);

        }

    }

    /**
     * 在所有房间都移除：该鱼
     */
    private static void handleRemove(
            @Nullable TtmyFishingChallengeMessage.FishingChallengeHitFishResponse.Builder builder,
            Map<Long, Long> refFishIdAndMultipleMap, boolean quanPingZhaDanFlag, FishingChallengePlayer player, long userId,
            long randomMoney, FishStruct fishStruct, CallBack<Boolean> killAndSendFishFlagCallBack) {

        List<NewBaseFishingRoom> roomList = getRoomListByRuleId(fishStruct.getRuleId());

        roomList.forEach(subItem -> {

            // 房间移除，该鱼
            doHandleRemove(builder, refFishIdAndMultipleMap, quanPingZhaDanFlag, player, userId, randomMoney,
                    fishStruct, subItem, true, killAndSendFishFlagCallBack);

        });

    }

    /**
     * 只在当前房间移除，该鱼
     */
    private static void doHandleRemove(
            TtmyFishingChallengeMessage.FishingChallengeHitFishResponse.@Nullable Builder builder,
            Map<Long, Long> refFishIdAndMultipleMap, boolean quanPingZhaDanFlag, FishingChallengePlayer player, long userId,
            long randomMoney, FishStruct fishStruct, NewBaseFishingRoom room, boolean removeFlag,
            CallBack<Boolean> killAndSendFishFlagCallBack) {

        if (removeFlag) {

            // 移除：鱼
            removeFish(room, fishStruct, refFishIdAndMultipleMap, quanPingZhaDanFlag, true);

        }

        // 发送：鱼死亡响应
        killAndSendFish(room, player, fishStruct, userId, randomMoney, 0, refFishIdAndMultipleMap);

        // 目的：避免重复发送响应
        killAndSendFishFlagCallBack.setValue(false);

        if (builder != null) {

            // 发送：命中鱼的响应
            MyRefreshFishingUtil.sendRoomMessage(room,
                    OseeMessage.OseeMsgCode.S_C_TTMY_FISHING_CHALLENGE_HIT_FISH_RESPONSE_VALUE, builder);

        }

    }

    /**
     * 处理：怪物血量
     */
    private static TtmyFishingChallengeMessage.FishingChallengeHitFishResponse.Builder handleMonsterHp(
            FishStruct fishStruct, long currentTimeMillis, FishingChallengePlayer player, NewBaseFishingRoom room,
            long randomMoney, long userId, Map<Long, Long> refFishIdAndMultipleMap, boolean killFlag,
            boolean quanPingZhaDanFlag, @Nullable Integer deductHpValue, @Nullable Integer hitFishValue,
            boolean sendMessageFlag, long winMoney, @Nullable Integer hitType,
            CallBack<Boolean> killAndSendFishFlagCallBack) {

        if (deductHpValue == null) {

            // 获取：攻击次数
            deductHpValue = getHitDelta(player, currentTimeMillis, room);

        }

        int monsterHp = fishStruct.getMonsterHp() - deductHpValue;

        if (monsterHp < 0) {
            monsterHp = 0;
        }

        fishStruct.setMonsterHp(monsterHp);

        if (hitFishValue == null) {

            hitFishValue = RandomUtil.getRandom(fishStruct.getPlayerAttackScopeList().get(0),
                    fishStruct.getPlayerAttackScopeList().get(1));

        }

        TtmyFishingChallengeMessage.FishingChallengeHitFishResponse.Builder builder =
                TtmyFishingChallengeMessage.FishingChallengeHitFishResponse.newBuilder();

        builder.setFishId(fishStruct.getId());
        builder.setHp(fishStruct.getMonsterHp());
        builder.setMxHp(fishStruct.getMonsterMaxHp());
        builder.setHpCount(fishStruct.getMonsterHpCount());
        builder.setAttackPlayerId(userId);
        builder.setHitFishValue(hitFishValue);

        builder.setNickname(player.getUser().getNickname());

        builder.setWinMoney(winMoney);

        builder.setRequestUserId(player.getId());

        if (hitType != null) {
            builder.setType(hitType);
        }

        if (sendMessageFlag) {

            // 发送：命中鱼的响应
            MyRefreshFishingUtil.sendRoomMessage(room,
                    OseeMessage.OseeMsgCode.S_C_TTMY_FISHING_CHALLENGE_HIT_FISH_RESPONSE_VALUE, builder);

        }

        if (monsterHp <= 0 && !killFlag) {

            if (sendMessageFlag) {

                // 只在当前房间移除，该鱼
                doHandleRemove(null, refFishIdAndMultipleMap, quanPingZhaDanFlag, player, userId, randomMoney,
                        fishStruct, room, true, killAndSendFishFlagCallBack);

            }

        }

        return builder;

    }

    /**
     * 积分场：打鱼处理
     *
     * @return true 扣钱了 false 没有扣钱
     */
    private static boolean fightFishIntegralRoom(FishingChallengePlayer player, FishConfig fishConfig,
                                                 FishStruct fishStruct, long needMoney, NewBaseFishingRoom room, long batteryLevel,
                                                 OseePlayerEntity playerEntity) {

        // 如果不是：奖券鱼
        if (BooleanUtil.isFalse(CHECK_LOTTERY_FUNC.apply(fishStruct.getModelId()))) {
            return false;
        }

        long preMoney = player.getMoney();

        long preLottery = player.getLottery();

        player.addMoney(-needMoney); // 扣除金币

        // 增加：消耗
        long xhlj =
                redissonClient.getAtomicLong("fightFishIntegralRoom:XHLJ:" + player.getUser().getId()).addAndGet(needMoney);

        long lottery = fishStruct.getLottery();

        if (lottery == 0) {
            return true;
        }

        long zs = lottery * batteryLevel / 10; // 奖券的张数

        long checkValue = 200 * zs;

        if (xhlj < checkValue) {
            return true;
        }

        // 奖券鱼死亡处理
        // 减少：xhlj
        // 发放奖励：lottery
        // 发送死鱼响应

        redissonClient.getAtomicLong("fightFishIntegralRoom:XHLJ:" + player.getUser().getId()).addAndGet(-checkValue);

        List<OseePublicData.ItemDataProto> dropItemList = new LinkedList<>();

        dropItemList.add(OseePublicData.ItemDataProto.newBuilder().setItemId(ItemId.LOTTERY.getId()).setItemNum(zs)
                .setOriginalNumber(PlayerManager.getPlayerEntity(player.getUser()).getLottery()).build());

        PlayerManager.addItem(player.getUser(), ItemId.LOTTERY, zs, ItemChangeReason.FISHING_RESULT, false);

        TtmyFishingChallengeMessage.FishingChallengeFightFishResponse.Builder builder =
                TtmyFishingChallengeMessage.FishingChallengeFightFishResponse.newBuilder();
        builder.setFishId(fishStruct.getId());
        builder.setPlayerId(player.getId());
        builder.setRestMoney(PlayerManager.getPlayerEntity(UserContainer.getUserById(player.getId())).getDragonCrystal());
        builder.setDropMoney(0);
        builder.addAllDropItems(dropItemList);

        builder.setMultiple(zs); // 鱼倍数
        MyRefreshFishingUtil.sendRoomMessage(room,
                OseeMessage.OseeMsgCode.S_C_TTMY_FISHING_CHALLENGE_FIGHT_FISH_RESPONSE_VALUE, builder);

        StrBuilder strBuilder = StrBuilder.create();

        strBuilder.append("奖卷：").append(lottery).append("，奖卷张数：").append(zs).append("，炮倍：").append(batteryLevel)
                .append("，总消耗：").append(xhlj).append("，本次消耗：").append(checkValue);

        return true;

    }

    /**
     * 处理：全屏炸弹的倍数
     */
    private static long handleQuanPingZhaDanFlagRandomMoney(NewBaseFishingRoom room, FishingChallengePlayer player,
                                                            FishStruct fishStruct, Map<Long, Long> refFishIdAndMultipleMap) {

        long randomMoney = 0;
        for (FishStruct item : room.getFishMap().values()) {
            if (item.getModelId() == fishStruct.getModelId()) {
                continue;
            }

            if (CHECK_LOTTERY_FUNC.apply(item.getModelId())) { // 奖券鱼，不能被全屏炸弹炸死
                log.info("鱼：id={},ModelId()={}, 奖券鱼", item.getId(), item.getModelId());
                continue;
            }

            if (item.isBossBulge()) {
                log.info("鱼：id={}, modeid={}, isBossBulge", item.getId(), item.getModelId());
                continue;
            }

            /*FishStruct fishTemp = room.getFishMap().get(item.getId());
            if (fishTemp == null) {
                log.info("鱼：id={}, fishTemp == null", item.getId());
                continue;
            }*/

            FishConfig configTemp = DataContainer.getData(item.getConfigId(), FishConfig.class);
            long multiple = FishFactory.create(item, configTemp, player.getUser(), room).getMultiple();
            if (multiple <= 0) {
                log.info("鱼：{}， multiple={}, multiple小于0", configTemp.getName(), multiple);
                continue;
            }

            long randomMoneyTemp = randomMoney + multiple;
            if (randomMoneyTemp <= 0) {
                log.info("鱼：{}， multiple={}, randomMoneyTemp小于0", configTemp.getName(), multiple);
                continue;
            }

            // log.info("鱼：{}，倍数：{}", configTemp.getName(), multiple);
            refFishIdAndMultipleMap.put(item.getId(), multiple);
            randomMoney = randomMoneyTemp;
        }

        if (randomMoney <= 0) {
//            log.info("爆炸鱼 倍数为1----> randomMoney={}, fishNum={}", randomMoney, room.getFishMap().size());
            randomMoney = 1L; // 如果没有其他鱼，则为 1倍
        }
        return randomMoney;
    }

    /**
     * 处理：出奇制胜的倍数
     */
    private static long handleChuQiZhiShengRandomMoney(NewBaseFishingRoom room, FishStruct fishStruct, long randomMoney,
                                                       Map<Long, Long> refFishIdAndMultipleMap, AbsFish absFish) {

        if (fishStruct.isChuQiZhiShengFlag()) {

            absFish.setComputeMultipleType(4);

            long finalRandomMoney = randomMoney;

            room.getFishMap().values().forEach(it -> {

                if (it.getModelId() != fishStruct.getModelId() || it.getId() == fishStruct.getId()) {

                    return;

                }

                refFishIdAndMultipleMap.put(it.getId(), finalRandomMoney); // 备注：这里倍数都是一样的

            });

            // 出奇制胜：奖励倍数 = 屏幕中同类鱼的倍数之和
            randomMoney = (refFishIdAndMultipleMap.size() + 1) * randomMoney;

        }

        return randomMoney;

    }


    // 通过用户区分，二次伤害的击杀信息，map：key：fishName + fishId，value：击杀信息
    public static final String SECONDARY_DAMAGE_FISH_USER_PRE = "SECONDARY_DAMAGE_FISH_USER_PRE:";

    /**
     * 获取：bucket
     */
    public static RBucket<Map<String, AccountDetailDO>> getSecondaryDamageFishMapBucket(long userId) {

        return redissonClient.getBucket(SECONDARY_DAMAGE_FISH_USER_PRE + userId, new JsonJacksonCodec());

    }

    /**
     * 获取：二次伤害鱼的，金币总和
     */
    public static long getSecondaryDamageFishAllMoney(long userId) {

        Map<String, AccountDetailDO> secondaryDamageFishMap = getSecondaryDamageFishMapBucket(userId).get();

        long allMoney = 0;

        if (CollUtil.isEmpty(secondaryDamageFishMap)) {
            return allMoney;
        }

        for (AccountDetailDO item : secondaryDamageFishMap.values()) {

            allMoney = allMoney + item.getChangeMoney(); // 累加：获得的钱

        }

        return allMoney;

    }


    // /**
    // * 添加：二次伤害鱼的死亡信息
    // */
    // public static void putSecondaryDamageFishInfo(String fishName, long fishId, AccountDetailDO accountDetailDO,
    // long userId) {
    //
    // RBucket<Map<String, AccountDetailDO>> secondaryDamageFishMapBucket = getSecondaryDamageFishMapBucket(userId);
    //
    // Map<String, AccountDetailDO> secondaryDamageFishMap = secondaryDamageFishMapBucket.get();
    //
    // if (secondaryDamageFishMap == null) {
    // secondaryDamageFishMap = new HashMap<>();
    // }
    //
    // secondaryDamageFishMap.put(fishName + "," + fishId, accountDetailDO);
    //
    // secondaryDamageFishMapBucket.set(secondaryDamageFishMap);
    //
    // }

    // 通过玩家区分，当前玩家的命中状态：1 爆发 2 回收
    public static final String FISHING_HIT_STATE_USER_PRE = "FISHING_HIT_STATE_USER_PRE:";

    // 通过玩家区分，期望子弹数
    public static final String FISHING_JCZD0_USER_PRE = "FISHING_JCZD0_USER_PRE:";

    // 通过玩家区分，重置期望子弹数的原因
    public static final String FISHING_JCZD0_CLEAN_REASON_USER_PRE = "FISHING_JCZD0_CLEAN_REASON_USER_PRE:";

    // // qxh，龙晶场
    // public static final String QXH_CHALLENGE = "QXH_CHALLENGE";
    // // qcx，根据场次区分
    // public static final String QCX_CHALLENGE_ROOM_INDEX_PRE = "QCX_CHALLENGE_ROOM_INDEX_PRE:";
    // xh，根据：用户，fishType2，区分
    public static final String XH_CHALLENGE_USER_FISH_TYPE_2_PRE = "XH_CHALLENGE_USER_FISH_TYPE_2_PRE:";
    // // cx，根据：用户，区分
    // public static final String CX_CHALLENGE_USER_PRE = "CX_CHALLENGE_USER_PRE:";
    // // jcx，根据：用户，区分
    // public static final String JCX_CHALLENGE_USER_PRE = "JCX_CHALLENGE_USER_PRE:";

    // 用户，鱼的 modelId，区分，值：是鱼的倍数
    public static final String CHALLENGE_USER_FISH_MODEL_ID_MULT_PRE = "CHALLENGE_USER_FISH_MODEL_ID_MULT_PRE:";

    // 正常：需要命中的总次数，根据：用户 id，modelId，区分
    public static final String NORMAL_NEED_HIT_COUNT_CHALLENGE_USER_ID_AND_MODEL_ID_PRE =
            "NORMAL_NEED_HIT_COUNT_CHALLENGE_USER_ID_AND_MODEL_ID_PRE:";
    // 正常：当前命中的总次数，根据：用户 id，modelId，区分
    public static final String NORMAL_CURRENT_HIT_COUNT_CHALLENGE_USER_ID_AND_MODEL_ID_PRE =
            "NORMAL_CURRENT_HIT_COUNT_CHALLENGE_USER_ID_AND_MODEL_ID_PRE:";

    // 爆发：需要命中的总次数，根据：用户 id，modelId，区分
    public static final String BURST_NEED_HIT_COUNT_CHALLENGE_USER_ID_AND_MODEL_ID_PRE =
            "BURST_NEED_HIT_COUNT_CHALLENGE_USER_ID_AND_MODEL_ID_PRE:";
    // 爆发：当前命中的总次数，根据：用户 id，modelId，区分
    public static final String BURST_CURRENT_HIT_COUNT_CHALLENGE_USER_ID_AND_MODEL_ID_PRE =
            "BURST_CURRENT_HIT_COUNT_CHALLENGE_USER_ID_AND_MODEL_ID_PRE:";

    // 回收：需要命中的总次数，根据：用户 id，modelId，区分
    public static final String RECYCLE_NEED_HIT_COUNT_CHALLENGE_USER_ID_AND_MODEL_ID_PRE =
            "RECYCLE_NEED_HIT_COUNT_CHALLENGE_USER_ID_AND_MODEL_ID_PRE:";
    // 回收：当前命中的总次数，根据：用户 id，modelId，区分
    public static final String RECYCLE_CURRENT_HIT_COUNT_CHALLENGE_USER_ID_AND_MODEL_ID_PRE =
            "RECYCLE_CURRENT_HIT_COUNT_CHALLENGE_USER_ID_AND_MODEL_ID_PRE:";

    // 鱼倍数范围集合
    public static Map<Integer, FishMultRangeBO> FISH_MULT_RANGE_MAP; //


    // 捕鱼，根据用户，鱼模型 id区分，上一次是爆发还是回收：0 回收（默认） 1 爆发
    public static final String FISHING_HIT_STATE_USER_MODEL_ID_PRE = "FISHING_HIT_STATE_USER_MODEL_ID_PRE:";

    // 捕鱼，根据场次，用户，鱼模型 id区分，本次的：kz,y几
    public static final String FISHING_KZ_INFO_ROOM_INDEX_USER_MODEL_ID_PRE =
            "FISHING_KZ_INFO_ROOM_INDEX_USER_MODEL_ID_PRE:";

    // 捕鱼，根据场次，用户区分，GRCCLSRTP，玩家场次历史消耗的金币，在某一场次的累计历史收益比
    public static final String FISHING_CCLS_XH_ROOM_INDEX_USER_PRE = "FISHING_CCLS_XH_ROOM_INDEX_USER_PRE:";
    // 捕鱼，根据场次，用户区分，GRCCLSRTP，玩家场次历史获得的金币，在某一场次的累计历史收益比
    public static final String FISHING_CCLS_PRODUCE_ROOM_INDEX_USER_PRE = "FISHING_CCLS_PRODUCE_ROOM_INDEX_USER_PRE:";

    // 捕鱼，根据场次，用户区分，玩家进场消耗的金币，退房间时清零，进房间开始计算
    public static final String FISHING_JC_XH_ROOM_INDEX_USER_PRE = "FISHING_JC_XH_ROOM_INDEX_USER_PRE:";
    // 捕鱼，根据场次，用户区分，玩家进场获得的金币，退房间时清零，进房间开始计算
    public static final String FISHING_JC_PRODUCE_ROOM_INDEX_USER_PRE = "FISHING_JC_PRODUCE_ROOM_INDEX_USER_PRE:";

    // 捕鱼，根据用户区分，玩家今日消耗的金币
    public static final String FISHING_JR_XH_USER_PRE = "FISHING_JR_XH_USER_PRE:";
    // 捕鱼，根据用户区分，玩家今日获得的金币
    public static final String FISHING_JR_PRODUCE_USER_PRE = "FISHING_JR_PRODUCE_USER_PRE:";

    // 捕鱼，根据用户，场次区分，玩家今日消耗的金币
    public static final String FISHING_JR_XH_ROOM_INDEX_USER_PRE = "FISHING_JR_XH_ROOM_INDEX_USER_PRE:";
    // 捕鱼，根据用户，场次区分，玩家今日获得的金币
    public static final String FISHING_JR_PRODUCE_ROOM_INDEX_USER_PRE = "FISHING_JR_PRODUCE_ROOM_INDEX_USER_PRE:";

    // 捕鱼，根据用户区分，爆发阶段子弹数低于多少时，直接死鱼
    public static final String FISHING_AQ_USER_PRE = "FISHING_AQ_USER_PRE:";

    // 捕鱼，通过玩家区分，当前的 bd（节点）
    public static final String FISHING_BD_USER_PRE = "FISHING_BD_USER_PRE:";

    // 通过玩家区分，游戏时长阀值 bd（节点）
    public static final String FISHING_BDFZ_USER_PRE = "FISHING_BDFZ_USER_PRE:";

    // 通过玩家区分，游戏时长阀值 bd（节点）集合，用于：分组节点，备注；每个元素的值就是：bdfz
    public static final String FISHING_BDFZ_LIST_USER_PRE = "FISHING_BDFZ_LIST_USER_PRE:";

    // 通过玩家区分，上一次的节点信息
    public static final String FISHING_LAST_NODE_INFO_USER_PRE = "FISHING_LAST_NODE_INFO_USER_PRE:";

    // 通过玩家区分，所有的期望节点集合
    public static final String FISHING_JCZD0_LIST_USER_PRE = "FISHING_JCZD0_LIST_USER_PRE:";

    // // 通过玩家区分，旧的期望节点集合
    // public static final String FISHING_OLD_JCZD0_LIST_USER_PRE = "FISHING_OLD_JCZD0_LIST_USER_PRE:";

    // 通过玩家区分，手动修改之后，当时的炮倍，用于：个控
    public static final String FISHING_PERSONAL_JCZD0_LIST_BATTERY_LEVEL_USER_PRE =
            "FISHING_PERSONAL_JCZD0_LIST_BATTERY_LEVEL_USER_PRE:";

    // 通过玩家区分，手动修改之后，初始金币数，目前：只显示用
    public static final String FISHING_PERSONAL_CHU_SHI_MONEY_USER_PRE = "FISHING_PERSONAL_CHU_SHI_MONEY_USER_PRE:";

    // 通过玩家区分，手动修改之后，额外当前金币数
    public static final String FISHING_GRETJ_USER_PRE = "FISHING_GRETJ_USER_PRE:";

    // 通过玩家区分，初始金币数
    public static final String FISHING_CHU_SHI_MONEY_USER_PRE = "FISHING_CHU_SHI_MONEY_USER_PRE:";

    // 从 redis里面获取：BDZ_CONFIG_BO
    public static final String BDZ_CONFIG_BO_REDIS_KEY = "BDZ_CONFIG_BO_KEY";

    private static BdzConfigBO bdzConfigBO = null;

    /**
     * 获取：BdzConfigBO对象
     */
    @NotNull
    public static BdzConfigBO getCommonBdzConfigBO() {

        if (bdzConfigBO != null) {

            return bdzConfigBO;

        } else {

            RBucket<BdzConfigBO> bdzConfigBOBucket =
                    redissonClient.getBucket(BDZ_CONFIG_BO_REDIS_KEY, new JsonJacksonCodec());

            BdzConfigBO redisBdzConfigBO = bdzConfigBOBucket.get();

            if (redisBdzConfigBO == null) {

                bdzConfigBO = new BdzConfigBO();
                bdzConfigBOBucket.set(bdzConfigBO); // 设置到：缓存里面

            } else {

                bdzConfigBO = redisBdzConfigBO;

            }

            return bdzConfigBO;

        }

    }

    /**
     * 获取：BdzConfigBO对象
     */
    @NotNull
    public static FishFluctuationParametersConfig getFishBdzConfigBO(int roomIndex) {

        FishFluctuationParametersConfig res = DataContainer.getData(roomIndex, FishFluctuationParametersConfig.class);

        if (res == null) {

            return new FishFluctuationParametersConfig();

        }

        return res;

    }


    /**
     * 设置：BdzConfigBO对象
     */
    public static void setBdzConfigBO(BdzConfigBO tempBdzConfigBO) {

        redissonClient.<BdzConfigBO>getBucket(BDZ_CONFIG_BO_REDIS_KEY, new JsonJacksonCodec()).set(tempBdzConfigBO);

        bdzConfigBO = tempBdzConfigBO;

    }

    /**
     * 获取：当前子弹数
     */
    public static long getJczd1(long money, long batteryLevel, long userId) {

        double gretj =
                redissonClient.getAtomicDouble(FishingChallengeFightFishUtil.FISHING_GRETJ_USER_PRE + userId).get();

        long jczd1 = (long) ((money + gretj) / batteryLevel);

        if (jczd1 < 0) {

            jczd1 = 0;

        }

        // log.info("gretj：{}，jczd1：{}", gretj, jczd1);

        return jczd1;

    }


    /**
     * 获取：状态
     */
    public static int getHitState(RBucket<Integer> hitStateBucket) {

        // 当前命中状态：1 爆发 2 回收
        Integer hitState = hitStateBucket.get();

        if (hitState == null) {

            // log.info("hitState：等于2");

            hitState = 2;

        }

        return hitState;

    }

    /**
     * 获取：初始子弹数
     */
    private static double getChuShiBulletNumber(NewBaseGamePlayer player, long batteryLevel) {

        double chuShiMoney;

        RAtomicDouble chuShiRAtomicDouble =
                redissonClient.getAtomicDouble(FISHING_CHU_SHI_MONEY_USER_PRE + player.getId());

        if (chuShiRAtomicDouble.isExists()) {

            chuShiMoney = chuShiRAtomicDouble.get();

        } else {

            chuShiMoney = player.getMoney(); // 如果不存在，则为当前金币数

            chuShiRAtomicDouble.set(chuShiMoney); // 设置到：redis里面

        }

        // 初始子弹数
        return chuShiMoney / batteryLevel;

    }

    // 通过：用户区分：是否限制用户的盈亏爆发，默认：true
    public static final String USER_YKBF_FLAG = "USER_YKBF_FLAG:";

    // 通过：用户区分：是否周期内触发过盈亏爆发节点，默认：false
    public static final String USER_CYCLE_YKBF_FLAG = "USER_CYCLE_YKBF_FLAG:";

    /**
     * 处理：是否：触碰盈亏爆发
     */
    private static void handleYkbfFlagCallBack(int roomIndex, long userId, CallBack<Boolean> ykbfFlagCallBack,
                                               double cclsyktf, NewBaseGamePlayer player, long batteryLevel, int index) {

        player.setBfxyValue(null);


        if (ykbfFlagCallBack.getValue()) {
            return;

        }

        // 执行：检查奖池
        if (!doCheckPrizePoolInventory(0, roomIndex, null)) {

            ykbfFlagCallBack.setValue(true);

            return;

        }

        // 是否限制：用户的盈亏爆发
        RBucket<Boolean> ykbfFlagRBucket = redissonClient.getBucket(USER_YKBF_FLAG + userId);

        Boolean ykbfFlag = ykbfFlagRBucket.get();

        if (!BooleanUtil.isFalse(ykbfFlag)) {

            ykbfFlagCallBack.setValue(true);

            return;

        }

        // // 获取：场次盈亏下限
        // long ccksfzxx = FishingTUtil.getProfitRatioDTO().getCcksfzxxArr()[index];
        //
        // cclsyktf = cclsyktf * batteryLevel; // 需要乘以炮倍
        //
        // if (cclsyktf >= -ccksfzxx) {
        //
        // ykbfFlagCallBack.setValue(true);
        //
        // if (!robotFlag) {
        // log.info("场次盈亏下限：cclsyktf：{}，-ccksfzxx：{}，userId：{}", cclsyktf, -ccksfzxx, userId);
        // }
        //
        // return;
        //
        // }

        double ksFz = getKsFzByYkType(userId, roomIndex); // 亏损峰值

        cclsyktf = cclsyktf * batteryLevel; // 需要乘以炮倍

        if (cclsyktf >= ksFz) {

            ykbfFlagCallBack.setValue(true);

            return;

        }

        // 是否周期内触发过爆发节点
        RBucket<Boolean> cycleYkbfFlagRbucket = redissonClient.getBucket(USER_CYCLE_YKBF_FLAG + userId);

        if (cycleYkbfFlagRbucket.isExists()) {

            ykbfFlagCallBack.setValue(true);

        }

    }

    /**
     * 获取：是否不处理：节点上限取值
     *
     * @return true 不处理 false 要处理
     */
    private static boolean getNotHandleJczd0Flag(NewBaseGameRoom room, int index, long userId, double jdsxksbfxs) {

        // 获取：对应场次的 cjxz
        int cjxz = FishingTUtil.getProfitRatioDTO().getCjxzArr()[index];

        int roomIndex = room.getRoomIndex();

        double ccyk = getCcykByYkType(userId, roomIndex);

        double ksFz = getKsFzByYkType(userId, roomIndex); // 亏损峰值

        // 新手亏损峰值阀值
        long xsksfzfz = FishingTUtil.getProfitRatioDTO().getXsksfzfzArr()[index];

        if (-ksFz > xsksfzfz) {

            return ccyk >= ksFz * jdsxksbfxs;

        }

        // log.info("ccyk：{}，cjxz：{}", ccyk, cjxz * 10000L);

        return ccyk >= (cjxz * 10000L);

    }

    // 历史亏损峰值：根据：场次，用户区分，备注：这里是负数
    public static final String KSFZ_ROOM_INDEX_USER_PRE = "KSFZ_ROOM_INDEX_USER_PRE";

    // 今日亏损峰值：根据：场次，用户区分，备注：这里是负数
    public static final String JR_KSFZ_ROOM_INDEX_USER_PRE = "JR_KSFZ_ROOM_INDEX_USER_PRE";

    /**
     * 根据后台配置的 type，获取场次亏损峰值
     */
    public static double getKsFzByYkType(long userId, int roomIndex, @Nullable Integer maxLimitYkType) {

        // 场次亏损峰值
        double ksFz;

        if (maxLimitYkType == null) {

            maxLimitYkType = getCommonBdzConfigBO().getMaxLimitYkType();

        }

        if (maxLimitYkType == 2) {

            RAtomicDouble jrKsFzAtomicDouble =
                    redissonClient.getAtomicDouble(JR_KSFZ_ROOM_INDEX_USER_PRE + roomIndex + ":" + userId);

            // 今日：亏损峰值
            ksFz = jrKsFzAtomicDouble.get();

            if (jrKsFzAtomicDouble.remainTimeToLive() == -1) {

                // 设置：今日过期
                jrKsFzAtomicDouble.expire(DateUtil.endOfDay(new Date()).toInstant());

            }

        } else {

            // 历史：亏损峰值
            ksFz = redissonClient.getAtomicDouble(KSFZ_ROOM_INDEX_USER_PRE + roomIndex + ":" + userId).get();

        }

        // log.info("ksFz：{}", ksFz);

        return ksFz;

    }

    /**
     * 根据后台配置的 type，获取场次亏损峰值
     */
    public static double getKsFzByYkType(long userId, int roomIndex) {

        return getKsFzByYkType(userId, roomIndex, null);

    }

    /**
     * 根据后台配置的 type，获取场次盈亏
     */
    public static double getCcykByYkType(long userId, int roomIndex) {

        // 场次盈亏
        double ccyk;

        int maxLimitYkType = getCommonBdzConfigBO().getMaxLimitYkType();

        if (maxLimitYkType == 2) {

            ccyk = handleJrKsFz(userId, roomIndex);

            handleKsFz(userId, roomIndex);

        } else {

            ccyk = handleKsFz(userId, roomIndex);

            handleJrKsFz(userId, roomIndex);

        }

        return ccyk;

    }

    private static double handleKsFz(long userId, int roomIndex) {

        // 场次历史产出
        double cclsProduce = redissonClient
                .getAtomicDouble(
                        FishingChallengeFightFishUtil.FISHING_CCLS_PRODUCE_ROOM_INDEX_USER_PRE + roomIndex + ":" + userId)
                .get();

        // 场次历史消耗
        double cclsXh = redissonClient.getAtomicDouble(
                FishingChallengeFightFishUtil.FISHING_CCLS_XH_ROOM_INDEX_USER_PRE + roomIndex + ":" + userId).get();

        // 场次历史盈亏
        double ccyk = (cclsProduce - cclsXh);

        // log.info("ccyk：{}，cclsProduce：{}，cclsXh：{}", ccyk, cclsProduce, cclsXh);

        if (ccyk >= 0) {
            return ccyk;
        }

        RAtomicDouble ksFzAtomicDouble =
                redissonClient.getAtomicDouble(KSFZ_ROOM_INDEX_USER_PRE + roomIndex + ":" + userId);

        // 历史亏损峰值
        double ksFz = ksFzAtomicDouble.get();

        if (ccyk < ksFz) {

            ksFzAtomicDouble.set(ccyk);

        }

        // log.info("ccyk：{}，cclsProduce：{}，cclsXh：{}，ksFz：{}", ccyk, cclsProduce, cclsXh, ksFz);

        return ccyk;

    }

    private static double handleJrKsFz(long userId, int roomIndex) {

        // 场次今日产出
        double cclsProduce =
                redissonClient
                        .getAtomicDouble(
                                FishingChallengeFightFishUtil.FISHING_JR_PRODUCE_ROOM_INDEX_USER_PRE + userId + ":" + roomIndex)
                        .get();

        // 场次今日消耗
        double cclsXh = redissonClient
                .getAtomicDouble(FishingChallengeFightFishUtil.FISHING_JR_XH_ROOM_INDEX_USER_PRE + userId + ":" + roomIndex)
                .get();

        // 场次今日盈亏
        double ccyk = (cclsProduce - cclsXh);

        // log.info("ccJrYk：{}，cclsProduce：{}，cclsXh：{}", ccyk, cclsProduce, cclsXh);

        if (ccyk >= 0) {
            return ccyk;
        }

        RAtomicDouble jrKsFzAtomicDouble =
                redissonClient.getAtomicDouble(JR_KSFZ_ROOM_INDEX_USER_PRE + roomIndex + ":" + userId);

        // 今日亏损峰值
        double jrKsFz = jrKsFzAtomicDouble.get();

        if (ccyk < jrKsFz) {

            jrKsFzAtomicDouble.set(ccyk);

            if (jrKsFzAtomicDouble.remainTimeToLive() == -1) {

                // 设置：今日过期
                jrKsFzAtomicDouble.expire(DateUtil.endOfDay(new Date()).toInstant());

            }

        }

        // log.info("ccJrYk：{}，cclsProduce：{}，cclsXh：{}，jrKsFz：{}", ccyk, cclsProduce, cclsXh, jrKsFz);

        return ccyk;

    }


    /**
     * 清除：盈利次数相关参数：加入房间，切换炮倍，消耗龙晶，得到龙晶
     * <p>
     * 1：重置所有的节点，包含初始节点 2：重置当前未完成的节点及后续节点 备注，后续程序判定：如果存在 jczd0List，但不存在 jczd0时，表示 2，如果都不存在，则表示 1
     *
     * @param money           正数加，负数减
     * @param oldBatteryLevel 如果为 null，则正常处理，如果不为 null，则按照存在个控处理：不重新生成曲线图
     */
    public static int clearYlcs(OseePlayerEntity playerEntity, String reasonStr, boolean joinRoomFlag, long money,
                                NewBaseGamePlayer player, Long oldBatteryLevel) {

        long userId = playerEntity.getUserId();

        NewBaseGameRoom room = null;

        if (player == null) {

            BaseGameRoom baseRoom = GameContainer.getGameRoomByPlayerId(userId);

            if (baseRoom != null) {

                BaseGamePlayer basePlayer = baseRoom.getGamePlayerById(userId);

                if (basePlayer instanceof NewBaseGamePlayer) {
                    player = (NewBaseGamePlayer) basePlayer;
                }

                if (baseRoom instanceof NewBaseGameRoom) {
                    room = (NewBaseGameRoom) baseRoom;
                }

            }

        }

        int clearType = 0; // 0 未清除曲线 1 需要清除所有曲线 2 需要清除当前节点以及后续的曲线

        synchronized (playerEntity) {

            long bd = 0;

            // 获取：个控时的炮倍，为空表示：没有进行个控
            String personalJczd0ListBatteryLevelStr =
                    redissonClient
                            .<String>getBucket(
                                    FishingChallengeFightFishUtil.FISHING_PERSONAL_JCZD0_LIST_BATTERY_LEVEL_USER_PRE + userId)
                            .get();

            boolean batteryLevelIsBlankFlag = StrUtil.isBlank(personalJczd0ListBatteryLevelStr);

            if (batteryLevelIsBlankFlag) {

                if (oldBatteryLevel != null) { // 如果：不存在个控，并且存在旧炮倍时，则按照存在个控处理：不重新生成曲线图

                    batteryLevelIsBlankFlag = false;
                    personalJczd0ListBatteryLevelStr = oldBatteryLevel.toString();

                }

            }

            RBatch batch = redissonClient.createBatch();

            if (batteryLevelIsBlankFlag) { // 如果没有个控

                if (joinRoomFlag) {

                    // 设置为：新的值
                    batch.getAtomicDouble(FISHING_CHU_SHI_MONEY_USER_PRE + userId).setAsync(money);

                    if (room != null) {

                        batch.<Integer>getBucket(FISHING_HIT_STATE_USER_PRE + userId).deleteAsync();

                    }

                    if (player != null) {

                        player.setChangeDragonCrystal(0);

                    }

                } else {

                    // bd = redissonClient.getAtomicLong(FISHING_BD_USER_PRE + userId).get();

                    if (money != 0) {

                        batch.getAtomicDouble(FISHING_CHU_SHI_MONEY_USER_PRE + userId).addAndGetAsync(money);

                        if (player != null) {

                            player.setChangeDragonCrystal(player.getChangeDragonCrystal() + money);

                        }

                    }

                }

            }

            if (bd == 0 && batteryLevelIsBlankFlag) { // 如果现在是第一个节点

                batch.getBucket(FISHING_BDFZ_USER_PRE + userId).deleteAsync();

                batch.getBucket(FISHING_BDFZ_LIST_USER_PRE + userId).deleteAsync();

                batch.getAtomicLong(FISHING_BD_USER_PRE + userId).deleteAsync();

                batch.getList(FISHING_JCZD0_LIST_USER_PRE + userId).deleteAsync();

                // batch.getList(FISHING_OLD_JCZD0_LIST_USER_PRE + userId).deleteAsync();

                clearType = 1;

            }

            // 清除：jczd0
            if (batteryLevelIsBlankFlag) {

                batch.getBucket(FISHING_JCZD0_USER_PRE + userId).deleteAsync();

                clearType = 2;

            } else { // 如果是个控

                if (player != null) {

                    int oldBatteryLevelTemp = new BigDecimal(personalJczd0ListBatteryLevelStr).intValue();

                    long batteryLevel = player.getBatteryLevel();

                    if (batteryLevel == 0) {

                        if (room == null) {

                            // 获取：场次的最低炮倍
                            batteryLevel = FishingChallengeManager
                                    .getBatteryLevel(FishingChallengeManager.ROOM_ONE.getRoomIndex(), 0);

                        } else {

                            // 获取：场次的最低炮倍
                            batteryLevel = FishingChallengeManager.getBatteryLevel(room.getRoomIndex(), 0);

                        }

                    }

                    // 备注：因为可能会被其他炮倍改变，所以是原来的炮倍的话，也要进行修改
                    // if (batteryLevel != oldBatteryLevelTemp) { // 如果：炮倍进行了切换

                    List<Double> jczd0List =
                            redissonClient.<Double>getList(FISHING_JCZD0_LIST_USER_PRE + userId).readAll();

                    if (CollUtil.isNotEmpty(jczd0List)) {

                        int bdTemp = (int) redissonClient.getAtomicLong(FISHING_BD_USER_PRE + userId).get();

                        double jczd0Temp = jczd0List.get(bdTemp);

                        jczd0Temp = BigDecimal.valueOf(jczd0Temp).multiply(BigDecimal.valueOf(oldBatteryLevelTemp))
                                .divide(BigDecimal.valueOf(batteryLevel), 2, RoundingMode.DOWN).doubleValue();

                        // log.info(
                        // "jczd0List：{}，bdTemp：{}，新jczd0Temp：{}，旧jczd0Temp：{}，oldBatteryLevelTemp：{}，batteryLevel：{}",
                        // JSONUtil.toJsonStr(jczd0List), bdTemp, jczd0Temp, jczd0List.get(bdTemp),
                        // oldBatteryLevelTemp, batteryLevel);

                        // 设置：新的当前期望子弹数
                        batch.<Double>getBucket(FishingChallengeFightFishUtil.FISHING_JCZD0_USER_PRE + player.getId())
                                .setAsync(jczd0Temp);

                    }

                    // }

                }

            }

            batch.getAtomicDouble(FISHING_AQ_USER_PRE + userId).deleteAsync();

            // 设置：重置 jczd0的原因
            batch.getBucket(FISHING_JCZD0_CLEAN_REASON_USER_PRE + userId).setAsync(reasonStr);

            batch.execute();

        }

        return clearType;

    }

    /**
     * 击中鱼之后的处理，不需要上锁
     */
    private static void execFightFishHandlerFishAfterForNoLock(NewBaseFishingRoom room, FishingChallengePlayer player,
                                                               long needMoney, FishStruct fishStruct, long currentTimeMillis, FishConfig fishConfig, long userId,
                                                               long batteryLevel, long randomMoney, long winMoney, boolean killFlag,
                                                               Map<Long, Long> refFishIdAndMultipleMap, AbsFish absFish, @Nullable Integer hitType,
                                                               CallBack<Boolean> killAndSendFishFlagCallBack) {

        // 金币同步
        moneySync(room, player);

        // 设置：今日的上榜权重（活跃榜）
        ActiveServiceImpl.activeTodayPut(new ActiveConfigBO(userId, 1));

        // 处理：消耗和产出
        handleUsedAndProduceGold(room, fishConfig.getModelId(), winMoney, needMoney, killFlag);

        if (killFlag) {

            // 击杀之后的逻辑处理，不需要上锁的一些操作
            fightFishHandlerKillAfterForNoLock(room, player, fishStruct, fishConfig, userId, batteryLevel, randomMoney,
                    winMoney, currentTimeMillis, refFishIdAndMultipleMap, absFish, killAndSendFishFlagCallBack);

        }

        // 处理：雷鸣破
        handleLeiMingPo(room, player, fishStruct, userId, hitType);

        // 处理：天神关羽
        handTianShenGuanYu(room, player, fishStruct, userId, hitType);
    }

    /**
     * 处理：天神关羽
     */
    private static void handTianShenGuanYu(NewBaseFishingRoom room, FishingChallengePlayer player, FishStruct fishStruct,
                                           long userId, @Nullable Integer hitType) {

        ServerUser user = player.getUser();
        // 只能在配置了该技能的房间里面使用
        FishCcxxConfig fishCcxxConfig = MyRefreshFishingUtil.FISHING_CCXX_CONFIG_MAP.get(room.getRoomIndex());
        if (fishCcxxConfig == null) {
            return;
        }
        List<Integer> skillIdList = fishCcxxConfig.getSkillIdList(); // 获取：可以使用的技能集合
        if (!skillIdList.contains(ItemId.SKILL_TIAN_SHEN_GUAN_YU.getId())) {
            return;
        }
        Boolean tianShenGuanYuUseFlag = FishingChallengeManager.getUserTianShenGuanYuUseFlag().get(userId);
        if (BooleanUtil.isTrue(tianShenGuanYuUseFlag)) { // 天神关羽如果开启
            boolean expiredFlag;
            UserProps userProps = userPropsManager.getUserProps(user.getId(), ItemId.SKILL_TIAN_SHEN_GUAN_YU.getId());
            if (ObjectUtils.isEmpty(userProps) || ObjectUtils.isEmpty(userProps.getExpirationTime()) ||
                    userProps.getExpirationTime().getTime() < System.currentTimeMillis()) {
                expiredFlag = true;
            } else {
                expiredFlag = false;
            }
            if (expiredFlag) { // 如果：天神关羽已到期
                // 关闭：天神关羽
                FishingChallengeManager.useSkill(room, player, ItemId.SKILL_TIAN_SHEN_GUAN_YU.getId(), 0, null);
            } else {
                if (hitType != null && hitType == 1) { // 如果：当前是天神关羽，则不扣除数量
                    return;
                }
                synchronized (player) {
                    Date lastUseTianShenGuanYuTs = player.getLastUseTianShenGuanYuTs();
                    Date date = new Date();
                    if (ObjectUtils.isEmpty(lastUseTianShenGuanYuTs)) {
                        player.setLastUseTianShenGuanYuTs(date);
                    } else if (date.getTime() > lastUseTianShenGuanYuTs.getTime() + (1000 * 4)) {
                        player.setLastUseTianShenGuanYuTs(date);  // 5秒以后
                        TtmyFishingChallengeMessage.FishingTianShenGuanYuPreUseResponse.Builder builder =
                                TtmyFishingChallengeMessage.FishingTianShenGuanYuPreUseResponse.newBuilder();
                        builder.setUserId(player.getId());
                        builder.setFishId(fishStruct.getId());
                        MyRefreshFishingUtil.sendRoomMessage(room,
                                OseeMessage.OseeMsgCode.S_C_FISHING_TIAN_SHEN_GUAN_YU_PRE_USE_RESPONSE_VALUE, builder);
                    }
                }
            }
        }
    }

    /**
     * 处理：雷鸣破
     */
    private static void handleLeiMingPo(NewBaseFishingRoom room, FishingChallengePlayer player, FishStruct fishStruct,
                                        long userId, @Nullable Integer hitType) {

        ServerUser user = player.getUser();
        // 只能在配置了该技能的房间里面使用
        FishCcxxConfig fishCcxxConfig = MyRefreshFishingUtil.FISHING_CCXX_CONFIG_MAP.get(room.getRoomIndex());
        if (fishCcxxConfig == null) {
            return;
        }
        List<Integer> skillIdList = fishCcxxConfig.getSkillIdList(); // 获取：可以使用的技能集合
        if (!skillIdList.contains(ItemId.SKILL_LEI_MING_PO.getId())) {
            return;
        }
        Boolean leiMingPoUseFlag = FishingChallengeManager.getUserLeiMingPoUseFlag().get(userId);
        if (BooleanUtil.isTrue(leiMingPoUseFlag)) { // 雷鸣破如果开启
            boolean expiredFlag;
            boolean payFlag = getPayFlag(skillIdList, fishCcxxConfig, ItemId.SKILL_LEI_MING_PO.getId());
            if (payFlag) {
                Long userPropsNum = userPropsManager.getUserProopsNum(user, ItemId.SKILL_LEI_MING_PO.getId());
                expiredFlag =
                        userPropsNum == null || userPropsNum <= 0 || userPropsNum <= System.currentTimeMillis();
            } else {
                expiredFlag = false;
            }

            if (expiredFlag) { // 如果：雷鸣破已到期
                // 关闭：雷鸣破
                FishingChallengeManager.useSkill(room, player, ItemId.SKILL_LEI_MING_PO.getId(), 0, null);
            } else {
                if (hitType != null && hitType == 1) { // 如果：当前是雷鸣破，则不扣除数量
                    return;
                }
                synchronized (player) {
                    if (player.getNextUseLeiMingPoCount() <= 0) {
                        player.setNextUseLeiMingPoCount(RandomUtil.getRandom(7, 10 + 1));
                        TtmyFishingChallengeMessage.FishingLeiMingPoPreUseResponse.Builder builder =
                                TtmyFishingChallengeMessage.FishingLeiMingPoPreUseResponse.newBuilder();
                        builder.setUserId(player.getId());
                        builder.setFishId(fishStruct.getId());
                        MyRefreshFishingUtil.sendRoomMessage(room,
                                OseeMessage.OseeMsgCode.S_C_FISHING_LEI_MING_PO_PRE_USE_RESPONSE_VALUE, builder);
                    } else {
                        player.setNextUseLeiMingPoCount(player.getNextUseLeiMingPoCount() - 1);
                    }
                }
            }

        }

    }

    /**
     * 金币同步
     */
    private static void moneySync(NewBaseFishingRoom newBaseFishingRoom, FishingGamePlayer fishingGamePlayer) {

        OseePlayerEntity oseePlayerEntity =
                PlayerManager.getPlayerEntity(UserContainer.getUserById(fishingGamePlayer.getId()));

        FishingChallengeManager.doFishingChallengeFireResponse(newBaseFishingRoom, fishingGamePlayer, 0, 0, 0,
                oseePlayerEntity.getDragonCrystal(), oseePlayerEntity.getDiamond());

    }

    /**
     * 判断击杀前的，一些处理
     */
    public static void fightFishHandlerPreKill(FishingChallengePlayer player, long needMoney, NewBaseFishingRoom room,
                                               boolean replyFightFlag, OseePlayerEntity playerEntity, boolean deductNeedMoneyFlag) {

        if (!deductNeedMoneyFlag) {
            return;
        }

        // 扣除金币
        player.useBattery(-needMoney, room, redissonClient);
//        log.info("扣除金币：{},Nickname：{},needMoney:{}",player.getUser().getId(),player.getUser().getNickname(),needMoney);


        // 设置：金币变化，备注：由于这几个参数，要在退房间时使用，所以要在加锁里面进行，2023-04-06，备注：暂时未使用这两个字段
        player.setChangeMoney(player.getChangeMoney() + needMoney);
        player.setSpendMoney(player.getSpendMoney() + needMoney);

    }

    /**
     * 发送：命中鱼请求
     */
    private static void sendReplyFightFishResponse(FishingGamePlayer fishingGamePlayer, boolean replyFightFlag) {

        if (replyFightFlag) {

            TtmyFishingChallengeMessage.FishingChallengeReplyFightFishResponse.Builder builder =
                    TtmyFishingChallengeMessage.FishingChallengeReplyFightFishResponse.newBuilder();

            builder.setRestMoney(fishingGamePlayer.getMoney());

            NetManager.sendMessage(S_C_TTMY_FISHING_CHALLENGE_REPLY_FIGHT_FISH_RESPONSE_VALUE, builder,
                    fishingGamePlayer.getUser());

        }

    }

    /**
     * 破产处理
     */
    public static void bankruptcyHandler(NewBaseFishingRoom room, FishingChallengePlayer player, boolean addFlag,
                                         String msg, FishConfig fishConfig, OseePlayerEntity playerEntity) {

        long money = player.getMoney();
        player.useBattery(-money, room, redissonClient); // 扣除：现在所有的钱

        if (money == 0) { // 如果：原本钱就是 0，则进行提示

            NetManager.sendErrorMessageToClient(msg, player.getUser());

        } else {

            // 金币同步
            FishingChallengeManager.playerMoneySync(player, room);

            if (addFlag) {
                // 一些通用参数的处理
                fightFishHandlerCommonParam((double) money, player.getId(), false, fishConfig.getFishType2());
            }

            // 处理：消耗和产出
            handleUsedAndProduceGold(room, fishConfig.getModelId(), 0L, -money, false);

            long checkMoney = FishingChallengeFightFishUtil.getJczd1(0, 1, player.getId());

            if (checkMoney == 0) {

                // 破产通用处理
                FishingChallengeFightFishUtil.bankruptcyCommonHandler(player, room, playerEntity, false);

            }

        }

    }

    /**
     * 破产通用处理
     */
    public static void bankruptcyCommonHandler(NewBaseGamePlayer player, @Nullable NewBaseGameRoom room,
                                               OseePlayerEntity playerEntity, boolean manuallyFlag) {

        synchronized (playerEntity) {

            long userId = playerEntity.getUserId();

            long bd = redissonClient.getAtomicLong(FISHING_BD_USER_PRE + userId).get();

            Long bdfz = redissonClient.<Long>getBucket(FISHING_BDFZ_USER_PRE + userId).get();

            if (bdfz == null) {
                bdfz = 0L;
            }

            Double jczd0 = redissonClient.<Double>getBucket(FISHING_JCZD0_USER_PRE + userId).get();

            if (jczd0 == null) {
                jczd0 = 0d;
            }

            if (room != null) {

                ProfitRatioDTO profitRatioDTO = FishingTUtil.getProfitRatioDTO();

                int index = profitRatioDTO.getIndexByRoomIndex(room.getRoomIndex());

                // 初始的：回收权重
                double chuShiHsWeight = profitRatioDTO.getChuShiHsWeightArr()[index];

                // 过程的：回收权重
                double guoChengHsWeight = profitRatioDTO.getGuoChengHsWeightArr()[index];

                // 随机取值的权重
                double randomWeight = profitRatioDTO.getBfxyRandomWeightArr()[index];

            }

            RBatch batch = redissonClient.createBatch();

            // 移除：个控时用的炮倍
            batch
                    .<String>getBucket(
                            FishingChallengeFightFishUtil.FISHING_PERSONAL_JCZD0_LIST_BATTERY_LEVEL_USER_PRE + userId)
                    .deleteAsync();

            // 移除：个控时用的初始金币
            batch.getAtomicDouble(FishingChallengeFightFishUtil.FISHING_PERSONAL_CHU_SHI_MONEY_USER_PRE + userId)
                    .deleteAsync();

            // 移除：个控时用的额外当前金币数
            batch.getAtomicDouble(FishingChallengeFightFishUtil.FISHING_GRETJ_USER_PRE + userId).deleteAsync();

            batch.getBucket(FISHING_BDFZ_USER_PRE + userId).deleteAsync();

            batch.getBucket(FISHING_BDFZ_LIST_USER_PRE + userId).deleteAsync();

            batch.getAtomicDouble(FISHING_CHU_SHI_MONEY_USER_PRE + userId).deleteAsync();

            batch.getAtomicLong(FISHING_BD_USER_PRE + userId).deleteAsync();

            batch.getList(FISHING_JCZD0_LIST_USER_PRE + userId).deleteAsync();

            // batch.getList(FISHING_OLD_JCZD0_LIST_USER_PRE + userId).deleteAsync();

            batch.getBucket(FISHING_JCZD0_USER_PRE + userId).deleteAsync();

            String reasonStr;

            if (manuallyFlag) {

                reasonStr = "手动重置";

            } else {

                boolean exists = redissonClient
                        .<String>getBucket(
                                FishingChallengeFightFishUtil.FISHING_PERSONAL_JCZD0_LIST_BATTERY_LEVEL_USER_PRE + userId)
                        .isExists();

                reasonStr = exists ? "个控结束" : "破产";

            }

            batch.<String>getBucket(FISHING_JCZD0_CLEAN_REASON_USER_PRE + userId).setAsync(reasonStr);

            batch.<Integer>getBucket(FISHING_HIT_STATE_USER_PRE + userId).deleteAsync();

            batch.execute();

        }

    }

    /**
     * 击杀之后的逻辑处理，不需要上锁的一些操作
     */
    private static void fightFishHandlerKillAfterForNoLock(NewBaseFishingRoom room, FishingGamePlayer player,
                                                           FishStruct fishStruct, FishConfig fishConfig, long userId, long batteryLevel, long randomMoney, long winMoney,
                                                           long currentTimeMillis, Map<Long, Long> refFishIdAndMultipleMap, AbsFish absFish,
                                                           CallBack<Boolean> killAndSendFishFlagCallBack) {

        // 处理：boss鱼死亡之后的通用处理
        handlerBossKillAfter(room, player, fishConfig, randomMoney, winMoney, userId, batteryLevel, absFish);

        if (killAndSendFishFlagCallBack.getValue()) {

            // 发送：鱼死亡响应
            killAndSendFish(room, player, fishStruct, userId, randomMoney, winMoney, refFishIdAndMultipleMap);

        }

        // 处理：游走字幕
        handlerWanderSubtitle(player, fishConfig, randomMoney, winMoney, room);

        // 如果是：世界boss，则要继续刷新该鱼
        if (fishStruct.isWorldBossFlag() && CollUtil.isNotEmpty(fishStruct.getFishStructList())) {

            FishStruct firstFishStruct = fishStruct.getFishStructList().get(0);

            if (firstFishStruct.getMonsterHp() > 0) {

                ThreadPoolUtils.TASK_SERVICE_POOL.schedule(() -> {

                    // 再检查一次：第一条鱼是否死亡
                    FishStruct firstFishStructCheck = fishStruct.getFishStructList().get(0);

                    if (firstFishStructCheck.getMonsterHpCount() <= 0) {
                        return;
                    }

                    fishStruct.getFishStructList().add(fishStruct);

                    GeneratedMessage.Builder<?> messageBuilder = room.getRefreshFishMessageBuilder();

                    // 同步鱼
                    room.addFishInfos(fishStruct, messageBuilder); // 添加到 messageBuilder里

                    // log.info("刷鱼-死亡之后：{}", fishStruct.getModelId());

                    // 继续刷新：该鱼
                    room.getFishMap().put(fishStruct.getId(), fishStruct);
//                    room.putFishMap(fishStruct);

                    MyRefreshFishingUtil.sendRoomMessage(room, room.getRefreshFishResponseValue(), messageBuilder);

                }, fishStruct.getDelayTime() * 1000L, TimeUnit.MILLISECONDS);

            }

        }

    }

    /**
     * 发送：鱼死亡响应
     */
    private static void killAndSendFish(NewBaseFishingRoom room, FishingGamePlayer player, FishStruct fishStruct,
                                        long userId, long randomMoney, long winMoney, Map<Long, Long> refFishIdAndMultipleMap) {

        // 处理动态刷新相关
        boolean durationRefreshFlag = MyRefreshFishingHelper.checkAndDurationRefreshFish(room, fishStruct, true);

        // 发送：鱼死亡响应
        sendFishDeathResponse(room, randomMoney, fishStruct, winMoney, userId, player, durationRefreshFlag,
                refFishIdAndMultipleMap);
    }

    /**
     * 处理：消耗和产出
     */
    public static void handleUsedAndProduceGold(NewBaseGameRoom room, int modelId, long winMoney, long needMoney, boolean killFlag) {


        if (BooleanUtil.isFalse(killFlag)) {
            winMoney = 0L;
        }

        if (FishingChallengeFightFishUtil.DYNAMIC_MULT_FISH_MODEL_ID_LIST.contains(modelId)) {

            FishingChallengeManager.fireInfoDynamicList.add(new FireInfoDTO(room.getRoomIndex(), needMoney, winMoney));

        }

        FishingChallengeManager.fireInfoList.add(new FireInfoDTO(room.getRoomIndex(), needMoney, winMoney));

    }


    /**
     * 处理：游走字幕
     */
    public static void handlerWanderSubtitle(FishingGamePlayer fishingGamePlayer, FishConfig fishConfig,
                                             long randomMoney, long winMoney, NewBaseFishingRoom newBaseFishingRoom) {


        int wanderSubtitleS = fishConfig.getWanderSubtitleS();

        boolean sendFlag = false;

        if (wanderSubtitleS >= 0) {

            sendFlag = true;

        }

        if (sendFlag) {

            FishingHelper.wanderSubtitle(newBaseFishingRoom, fishingGamePlayer, winMoney, randomMoney,
                    fishConfig.getId(), 9, wanderSubtitleS, fishingGamePlayer.getId());

            // 通报
            FishingHelper.notification(newBaseFishingRoom, fishingGamePlayer, winMoney, randomMoney, fishConfig.getId(),
                    wanderSubtitleS, fishingGamePlayer.getId());

        }

    }

    /**
     * 击杀之后的逻辑处理，需要上锁的一些操作
     */
    private static void fightFishHandlerKillAfterForLock(NewBaseFishingRoom room, FishingChallengePlayer player,
                                                         FishConfig fishConfig, AbsFish absFish, long randomMoney, FishStruct fishStruct, long winMoney, long userId,
                                                         long batteryLevel, OseePlayerEntity playerEntity, byte[] messageByteArr,
                                                         long currentTimeMillis, CallBack<Integer> bdCallBack, Map<Long, Long> refFishIdAndMultipleMap,
                                                         boolean quanPingZhaDanFlag, Double realJczd0, @Nullable Integer delta) {

        // 移除：鱼
        removeFish(room, fishStruct, refFishIdAndMultipleMap, quanPingZhaDanFlag, true);
        long preMoney = player.getMoney();

        // 处理：金币变化
        handleWinMoney(player, winMoney, playerEntity, room);

        // 处理：排行榜
        handlerRank(fishConfig, randomMoney, userId);

        // 鱼：死亡之后的处理，比如：移除缓存的倍数，发送播放特效响应等
        if (messageByteArr == null) {

            absFish.afterTheKill(room, winMoney, absFish.getKey());

        } else {

            // log.info("messageByteArr 不为 null：{}", messageByteArr.length);

            absFish.afterKillByMessageByteArr(room, winMoney, messageByteArr);

        }

        String roomIndexStr = String.valueOf(room.getRoomIndex());

        // 处理：roomIndexStr
        roomIndexStr = handleRoomIndexStr(player.getId(), room, roomIndexStr);

        double gapXh = redissonClient
                .getAtomicDouble(
                        FishingChallengeFightFishUtil.FISHING_KILL_GAP_XH_ROOM_INDEX_USER_PRE + roomIndexStr + ":" + userId)
                .getAndDelete();

        BigDecimal winMoneyBigDecimal = BigDecimal.valueOf(winMoney);

        BigDecimal gapXhBigDecimal = BigDecimal.valueOf(gapXh);

        // log.info("鱼种：{}，倍数：{}", fishConfig.getName(), randomMoney);

        StrBuilder changingReasonStrBuilder =
                StrBuilder.create().append("鱼种：").append(fishConfig.getName()).append("，倍数：").append(randomMoney)
                        .append("，炮倍：").append(batteryLevel).append("，奖励：").append(winMoneyBigDecimal.toPlainString())
                        .append("，盈亏：").append(winMoneyBigDecimal.subtract(gapXhBigDecimal).toPlainString()).append("，消耗：")
                        .append(gapXhBigDecimal.toPlainString());

        if (quanPingZhaDanFlag) {

            changingReasonStrBuilder.append("，鱼个数：").append(refFishIdAndMultipleMap.size());

        }


        // 处理：changingReasonStrBuilder：道具相关
        handleChangingReasonStrBuilderForProps(player, changingReasonStrBuilder, currentTimeMillis, delta);

        String aqInfoStr = player.getAqInfoStr();

        if (StrUtil.isNotBlank(aqInfoStr)) {

            player.setAqInfoStr(null);

            changingReasonStrBuilder.append("，AQ：").append(aqInfoStr);

        }

        String specifyFishInfoStr = player.getSpecifyFishInfoStr();

        if (StrUtil.isNotBlank(specifyFishInfoStr)) {

            player.setSpecifyFishInfoStr(null);

            changingReasonStrBuilder.append("，").append(specifyFishInfoStr);

        }

    }

    /**
     * 移除：鱼
     */
    private static void removeFish(NewBaseFishingRoom room, FishStruct fishStruct,
                                   Map<Long, Long> refFishIdAndMultipleMap, boolean quanPingZhaDanFlag, boolean removeFishStructListFlag) {

        // 出奇制胜 或者 全屏炸弹
        if (fishStruct.isChuQiZhiShengFlag() || quanPingZhaDanFlag) {

            // 处理：额外死的鱼
            for (Long item : refFishIdAndMultipleMap.keySet()) {
                room.getFishMap().remove(item);
//                room.removeFishMap(item);
            }

        }

        // 移除：该鱼
        room.getFishMap().remove(fishStruct.getId());
//        room.removeFishMap(fishStruct.getId());

        if (removeFishStructListFlag && CollUtil.isNotEmpty(fishStruct.getFishStructList())) {

            Iterator<FishStruct> iterator = fishStruct.getFishStructList().iterator();

            while (iterator.hasNext()) {

                FishStruct next = iterator.next();

                if (next.getId() == fishStruct.getId()) {

                    iterator.remove(); // 移除自己，备注：由于都是引用的同一个集合，所以这里移除了，其他鱼也会跟着一起移除
                    break; // 结束循环

                }

            }

            // 防止：不一定引用同一个集合，所以再次遍历移除
            for (FishStruct item : fishStruct.getFishStructList()) {

                long fishId = item.getId();

                FishStruct otherFishStruct = room.getFishMap().get(fishId);

                if (otherFishStruct != null && CollUtil.isNotEmpty(otherFishStruct.getFishStructList())) {

                    Iterator<FishStruct> otherIterator = otherFishStruct.getFishStructList().iterator();

                    while (otherIterator.hasNext()) {

                        FishStruct next = otherIterator.next();

                        if (next.getId() == fishStruct.getId()) {

                            otherIterator.remove(); // 移除自己
                            break; // 结束循环

                        }

                    }

                }

            }

        }

    }

    /**
     * 处理：道具相关
     */
    private static void handleChangingReasonStrBuilderForProps(FishingChallengePlayer fishingGamePlayer,
                                                               StrBuilder changingReasonStrBuilder, long currentTimeMillis, @Nullable Integer aqbh) {

        changingReasonStrBuilder.append("，道具：");

        List<String> list = new ArrayList<>();

        if (fishingGamePlayer.getLastAutoFireTime() != 0) {
            list.add("自动开炮");
        }
        if (fishingGamePlayer.getLastLockTime() != 0) {
            list.add("锁定");
        }
        if (fishingGamePlayer.getLastElectromagneticTime() != 0) {
            list.add("电磁炮");
        }
        if (fishingGamePlayer.getLastCritTime() != 0) {
            list.add("暴击");
        }

        if (fishingGamePlayer.getLastFrozenTime() + FishingManager.SKILL_FROZEN_TIME > currentTimeMillis) {
            list.add("冰冻");
        }

        if (aqbh != null) {

            list.add("aqbh" + aqbh);

        }

        if (list.size() > 0) {
            changingReasonStrBuilder.append(CollUtil.join(list, "，"));
        }

    }

    /**
     * 发送：鱼死亡响应
     */
    public static void sendFishDeathResponse(NewBaseFishingRoom room, long randomMoney, FishStruct fishStruct,
                                             long winMoney, long userId, FishingGamePlayer fishingGamePlayer, boolean durationRefreshFlag,
                                             Map<Long, Long> refFishIdAndMultipleMap) {

        TtmyFishingChallengeMessage.FishingChallengeFightFishResponse.Builder builder =
                TtmyFishingChallengeMessage.FishingChallengeFightFishResponse.newBuilder();
        builder.setFishId(fishStruct.getId());
        builder.setPlayerId(userId);
        builder.setRestMoney(fishingGamePlayer.getMoney());
        builder.setDropMoney(winMoney);
        builder.setMultiple(randomMoney); // 鱼倍数
//        builder.setContinueFlag(durationRefreshFlag);
        builder.setChuQiZhiShengFlag(fishStruct.isChuQiZhiShengFlag());
        // 处理：额外死的鱼
        if (CollUtil.isNotEmpty(refFishIdAndMultipleMap)) {
            for (Map.Entry<Long, Long> item : refFishIdAndMultipleMap.entrySet()) {
                TtmyFishingChallengeMessage.FishingChallengeFightFishRefFishItem.Builder refFishItemBuilder =
                        TtmyFishingChallengeMessage.FishingChallengeFightFishRefFishItem.newBuilder();
                refFishItemBuilder.setFishId(item.getKey());
                refFishItemBuilder.setMultiple(item.getValue());
                builder.addRefFishList(refFishItemBuilder);
            }
        }
        // 删除房间限制鱼  -- 杀死鱼
        MyRefreshFishingHelper.deleteRoomLimitFish(room, fishStruct);
        MyRefreshFishingUtil.sendRoomMessage(room,
                OseeMessage.OseeMsgCode.S_C_TTMY_FISHING_CHALLENGE_FIGHT_FISH_RESPONSE_VALUE, builder);

    }

    /**
     * 处理：boss鱼死亡之后的通用处理
     */
    private static void handlerBossKillAfter(NewBaseFishingRoom newBaseFishingRoom, FishingGamePlayer fishingGamePlayer,
                                             FishConfig fishConfig, long randomMoney, long winMoney, long userId, long batteryLevel,
                                             AbsFish absFish) {
        if (FishingChallengeManager.DOUBLE_KILL_END_SAVE_KILL_BOSS_FISH_MODEL_ID_SET
                .contains(fishConfig.getModelId())) {
            return;
        }

        if (fishConfig.getFishType() == 100) {

            KillBossEntity killBossEntity = new KillBossEntity();

            killBossEntity.setUserId(userId);
            killBossEntity.setNickName(fishingGamePlayer.getUser().getNickname());
            killBossEntity.setBatterLevel(batteryLevel);
            killBossEntity.setCreateTime(new Date());
            killBossEntity.setBossName(fishConfig.getName());
            killBossEntity.setMult(randomMoney);
            killBossEntity.setRoomIndex(newBaseFishingRoom.getRoomIndex());
            killBossEntity.setBloodPoolFloatKillValue(null);
            killBossEntity.setAward(winMoney + "金币");

            ThreadUtil.execute(() -> {

                playerMapper.saveKillBoss(killBossEntity); // 记录击杀Boss鱼日志

            });

        }

    }

    /**
     * 处理：金币变化
     */
    public static void handleWinMoney(FishingGamePlayer fishingGamePlayer, long winMoney, OseePlayerEntity playerEntity,
                                      NewBaseFishingRoom gameRoom) {

        if (winMoney != 0) {

            // 备注：由于这几个参数，要在退房间时使用，所以要在加锁里面进行，2023-04-06，备注：暂时未使用这两个字段
            fishingGamePlayer.setChangeMoney(fishingGamePlayer.getChangeMoney() - winMoney);
            fishingGamePlayer.setWinMoney(fishingGamePlayer.getWinMoney() - winMoney);

            // 加钱
            fishingGamePlayer.addMoney(winMoney);

            PlayerUtil.handleWinMoney(winMoney, playerEntity, gameRoom);

        }

    }

    /**
     * 处理：排行榜
     */
    private static void handlerRank(FishConfig fishConfig, long randomMoney, long userId) {

        if (randomMoney >= 1000 && fishConfig.getFishType() != 200) {

            double lastMult = RedisUtil.val("KILL_FISH_MULT" + userId, 0d);

            if (randomMoney == lastMult) {

                lastMult = lastMult + 0.001; // 相同倍数的，则加 0.001
                RedisHelper.set("KILL_FISH_NAME" + userId, fishConfig.getName());

            } else if (lastMult < randomMoney) {

                lastMult = randomMoney;
                RedisHelper.set("KILL_FISH_MULT" + userId, String.valueOf(lastMult));
                RedisHelper.set("KILL_FISH_NAME" + userId, fishConfig.getName());

            } else {

                return; // 如果当前倍数比上一次的小，则不要该数据

            }

            RedisUtil.zAdd(KILL_FISH_RANK_DAY_KEY, String.valueOf(userId), lastMult * 1000);

        }

    }

    /**
     * 处理：黄金鱼相关
     */
    public static void handlerGoldNumber(NewBaseFishingRoom newBaseFishingRoom, FishStruct fishStruct) {

        long ruleId = MyRefreshFishingHelper.getRoomGoldFishRuleId(newBaseFishingRoom);

        if (ruleId == fishStruct.getRuleId()) {

            long goldNumber = RedisUtil.val("FISHING_CHALLENGE_GAME_GOLD_FISH_NUM2" + newBaseFishingRoom.getCode(), 0L);

            goldNumber -= 1;

            if (goldNumber < 0) {

                goldNumber = 0;

            }

            RedisHelper.set("FISHING_CHALLENGE_GAME_GOLD_FISH_NUM2" + newBaseFishingRoom.getCode(),
                    String.valueOf(goldNumber));

        }

    }

    // 通过，用户，roomIndex，鱼 modelId，额外死亡次数
    public static final String FISHING_SWSJCS_USER_ROOM_INDEX_MODEL_ID_PRE =
            "FISHING_SWSJCS_USER_ROOM_INDEX_MODEL_ID_PRE:";

    // 通过，用户，roomIndex，鱼 modelId，额外死亡次数的鱼的 id
    public static final String FISHING_SWSJCS_FISH_ID_USER_ROOM_INDEX_MODEL_ID_PRE =
            "FISHING_SWSJCS_FISH_ID_USER_ROOM_INDEX_MODEL_ID_PRE:";

    /**
     * 获取：是否是和上次相同的鱼 备注：逃跑和击杀后，鱼的 id会不一样，则这个时候会返回 false
     */
    public static boolean swsjcsGetSameFishIdFlag(FishConfig fishConfig, long userId, NewBaseFishingRoom room,
                                                  Long fishId) {

        RBucket<Long> redisFishIdBucket = redissonClient.getBucket(FISHING_SWSJCS_FISH_ID_USER_ROOM_INDEX_MODEL_ID_PRE
                + userId + ":" + room.getRoomIndex() + ":" + fishConfig.getModelId());

        Long redisFishId = redisFishIdBucket.get();

        if (redisFishId == null) {

            redisFishId = fishId;
            redisFishIdBucket.set(redisFishId); // 设置到：缓存里

        }

        // 是否是：同一条鱼
        boolean sameFishIdFlag = redisFishId.equals(fishId);

        if (BooleanUtil.isFalse(redisFishId.equals(fishId))) {

            // 如果：鱼 id不一样
            redisFishIdBucket.set(fishId); // 设置到：缓存里

        }

        return sameFishIdFlag;

    }

    /**
     * 获取：攻击次数
     */
    public static int getHitDelta(FishingChallengePlayer player, long currentTimeMillis, NewBaseGameRoom room) {

        int delta = 1;

        // 根据技能情况，乘以倍数
        return (int) multDelta(player, currentTimeMillis, delta, room);

    }

    /**
     * 根据技能情况，乘以倍数
     */
    public static long multDelta(FishingGamePlayer player, long currentTimeMillis, long delta, NewBaseGameRoom room) {

        boolean userLeiShenBianUseFlag = FishingChallengeManager.getUserLeiShenBianUseFlag(player.getId(), room);

        if (userLeiShenBianUseFlag) {

            int mult = RedisUtil.val("USER_CRIT_MULT" + player.getUser().getId(), 1);

            delta = mult * delta;

        }

        // 只能在配置了该技能的房间里面使用
        FishCcxxConfig fishCcxxConfig = MyRefreshFishingUtil.FISHING_CCXX_CONFIG_MAP.get(room.getRoomIndex());

        if (fishCcxxConfig == null) {

            return delta;

        }

        List<Integer> skillIdList = fishCcxxConfig.getSkillIdList(); // 获取：可以使用的技能集合

        if (getPayFlag(skillIdList, fishCcxxConfig, ItemId.SKILL_CRIT.getId())) {

            if (currentTimeMillis - player.getLastCritTime() < SKILL_CRIT_TIME) {

                int mult = RedisUtil.val("USER_CRIT_MULT" + player.getUser().getId(), 1);

                delta = mult * delta;

            }

        } else {

            if (player.getLastCritTime() != 0) {

                int mult = RedisUtil.val("USER_CRIT_MULT" + player.getUser().getId(), 1);

                delta = mult * delta;

            }

        }

        if (getPayFlag(skillIdList, fishCcxxConfig, ItemId.SKILL_ELETIC.getId())) {

            if (currentTimeMillis - player.getLastElectromagneticTime() < SKILL_ELETIC_TIME) {
                // 电磁炮技能 倍数
//                delta = delta * 2;
                delta = delta * 4;

            }

        } else {

            if (player.getLastElectromagneticTime() != 0) {

//                delta = delta * 2;
                delta = delta * 4;

            }

        }

        if (getPayFlag(skillIdList, fishCcxxConfig, ItemId.FEN_SHEN.getId())) {

            if (currentTimeMillis - player.getLastFenShenTime() < SKILL_FEN_SHEN_TIME) {

                delta = delta * 3;

            }

        } else {

            if (player.getLastFenShenTime() != 0) {

                delta = delta * 3;

            }

        }

        if (getPayFlag(skillIdList, fishCcxxConfig, ItemId.SKILL_DOUBLE.getId())) {

            if (currentTimeMillis - player.getLastDoubleTime() < SKILL_DOUBLE_TIME) {

                delta = delta * 2;

            }

        } else {

            if (player.getLastDoubleTime() != 0) {

                delta = delta * 2;

            }

        }

        return delta;

    }

    public static boolean getPayFlag(List<Integer> skillIdList, FishCcxxConfig fishCcxxConfig, int skillId) {

        int index = skillIdList.indexOf(skillId);

        if (index == -1) {
            return true;
        }

        Integer skillPayType = fishCcxxConfig.getSkillPayTypeList().get(index);

        if (skillPayType == null) {
            skillPayType = 1; // 默认：付费
        }

        // 是否是付费道具
        return skillPayType == 1;

    }

    // 通过用户，鱼，区分，aq状态：满足了第几个条件，value：1 aq1 2 aq2 3 aq
    public static final String BF_BULLET_COUNT_LOW_AQ_STATE = "BF_BULLET_COUNT_LOW_AQ_STATE:";

    // 通过用户，鱼，区分，aq1 或者 aq2的值
    public static final String BF_BULLET_COUNT_LOW_AQ_VALUE = "BF_BULLET_COUNT_LOW_AQ_VALUE:";


    /**
     * 清除 aq相关
     */
    public static void cleanAqData(long userId) {

        RBatch batch = redissonClient.createBatch();

        List<FishConfig> fishConfigList = DataContainer.getDatas(FishConfig.class);

        for (FishConfig item : fishConfigList) {

            batch.getBucket(BF_BULLET_COUNT_LOW_AQ_STATE + userId + ":" + item.getModelId()).deleteAsync();

            batch.getBucket(BF_BULLET_COUNT_LOW_AQ_VALUE + userId + ":" + item.getModelId()).deleteAsync();

        }

        batch.execute();

        // log.info("清除 aq相关：{}", userId);

    }


    /**
     * 获取：下标，从 FISH_MULT_RANGE_LIST里面，通过鱼的倍数
     */
    public static int doGetIndexFromFishMultRangeListByRandomMoney(Double randomMoney,
                                                                   List<List<Integer>> fishMultRangeList) {

        int index = 0;

        for (List<Integer> item : fishMultRangeList) {

            boolean inFlag = NumberUtil.isIn(BigDecimal.valueOf(randomMoney), BigDecimal.valueOf(item.get(0)),
                    BigDecimal.valueOf(item.get(1)));

            if (inFlag) {
                break;
            }

            index++;

        }

        return index;

    }

    /**
     * 处理子弹
     *
     * @return null 表示子弹不存在
     */
    @Nullable
    public static FireStruct fightFishHandlerFire(FishingGamePlayer player, long fireId) {

        synchronized (player.getFireMap()) {

            FireStruct fireStruct = player.getFireMap().get(fireId);

            if (fireStruct == null) {
                return null;
            }

            fireStruct.setCount(fireStruct.getCount() - 1);

            if (fireStruct.getCount() <= 0) { // 该子弹是否打完了
                player.getFireMap().remove(fireId);
            }

            return fireStruct;

        }

    }

}
