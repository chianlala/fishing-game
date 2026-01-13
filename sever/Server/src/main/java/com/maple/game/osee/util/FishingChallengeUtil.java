package com.maple.game.osee.util;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.map.MapUtil;
import com.maple.database.config.redis.RedisHelper;
import com.maple.engine.container.DataContainer;
import com.maple.engine.data.ServerUser;
import com.maple.game.osee.common.RedisUtil;
import com.maple.game.osee.dao.data.entity.OseePlayerEntity;
import com.maple.game.osee.dao.data.mapper.OseePlayerMapper;
import com.maple.game.osee.dao.log.entity.OseeFishingRecordLogEntity;
import com.maple.game.osee.dao.log.entity.OseePlayerTenureLogEntity;
import com.maple.game.osee.dao.log.mapper.OseeFishingRecordLogMapper;
import com.maple.game.osee.dao.log.mapper.OseePlayerTenureLogMapper;
import com.maple.game.osee.entity.ItemChangeReason;
import com.maple.game.osee.entity.NewBaseGamePlayer;
import com.maple.game.osee.entity.NewBaseGameRoom;
import com.maple.game.osee.entity.fishing.NewBaseFishingRoom;
import com.maple.game.osee.entity.fishing.challenge.FishingChallengePlayer;
import com.maple.game.osee.entity.fishing.csv.file.FishConfig;
import com.maple.game.osee.manager.PlayerManager;
import com.maple.game.osee.manager.fishing.FishingChallengeManager;
import com.maple.game.osee.model.enums.ControlTypeEnum;
import com.maple.game.osee.proto.OseeMessage;
import com.maple.game.osee.proto.fishing.TtmyFishingChallengeMessage;
import com.maple.gamebase.container.GameContainer;
import com.maple.network.manager.NetManager;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBatch;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

import static com.maple.database.config.redis.RedisHelper.redissonClient;
import static com.maple.game.osee.controller.gm.GmCommonController.handleRoomIndexStr;

/**
 * 挑战场捕鱼：工具类
 */
@Component
@Slf4j
public class FishingChallengeUtil {

    // 所有鱼的 modelIdSet
    public static final Set<Integer> ALL_MODEL_ID_SET =
            DataContainer.getDatas(FishConfig.class).stream().map(FishConfig::getModelId).collect(Collectors.toSet());

    private static OseePlayerTenureLogMapper oseePlayerTenureLogMapper;
    private static OseeFishingRecordLogMapper oseeFishingRecordLogMapper;
    private static OseePlayerMapper oseePlayerMapper;

    public FishingChallengeUtil(OseePlayerTenureLogMapper oseePlayerTenureLogMapper,
                                OseeFishingRecordLogMapper oseeFishingRecordLogMapper, OseePlayerMapper oseePlayerMapper) {

        FishingChallengeUtil.oseePlayerTenureLogMapper = oseePlayerTenureLogMapper;
        FishingChallengeUtil.oseeFishingRecordLogMapper = oseeFishingRecordLogMapper;
        FishingChallengeUtil.oseePlayerMapper = oseePlayerMapper;

    }

    /**
     * 退出房间
     */
    public static void exitRoom(FishingChallengePlayer player, NewBaseFishingRoom room) {

        if (player == null) {
            return;
        }

        ServerUser user = player.getUser();
        OseePlayerEntity entity = PlayerManager.getPlayerEntity(user);

        long currentTimeMillis = System.currentTimeMillis();

        long userId = user.getId();

        if (entity != null) {

            synchronized (entity) { // 锁：用户

                long preMoney = player.getMoney();

                String roomIndexStr = String.valueOf(room.getRoomIndex());

                // 处理：roomIndexStr
                roomIndexStr = handleRoomIndexStr(player.getId(), room, roomIndexStr);

                // 更新：用户资料
                oseePlayerMapper.update(PlayerManager.getPlayerEntity(user));

                // 移出房间
                removeFromRoom(player, room, user);

            }

        }

        // 发送响应
        TtmyFishingChallengeMessage.FishingChallengeExitRoomResponse.Builder builder =
                TtmyFishingChallengeMessage.FishingChallengeExitRoomResponse.newBuilder();
        builder.setPlayerId(player.getId());

        MyRefreshFishingUtil.sendRoomMessage(room,
                OseeMessage.OseeMsgCode.S_C_TTMY_FISHING_CHALLENGE_EXIT_ROOM_RESPONSE_VALUE, builder);

        NetManager.sendMessage(OseeMessage.OseeMsgCode.S_C_TTMY_FISHING_CHALLENGE_EXIT_ROOM_RESPONSE_VALUE, builder,
                user);

    }

    /**
     * 保存捕鱼记录
     */
    private static void saveFishingRecordLog(FishingChallengePlayer fishingChallengePlayer,
                                             NewBaseFishingRoom newBaseFishingRoom, ServerUser serverUser, long currentTimeMillis) {

        OseeFishingRecordLogEntity recordLogEntity = new OseeFishingRecordLogEntity();
        recordLogEntity.setPlayerId(serverUser.getId());
        recordLogEntity.setRoomIndex(newBaseFishingRoom.getRoomIndex());
        recordLogEntity.setSpendMoney(fishingChallengePlayer.getSpendMoney());
        recordLogEntity.setWinMoney(fishingChallengePlayer.getWinMoney());
        recordLogEntity.setDropBronzeTorpedoNum(fishingChallengePlayer.getDropBronzeTorpedoNum());
        recordLogEntity.setDropSilverTorpedoNum(fishingChallengePlayer.getDropSilverTorpedoNum());
        recordLogEntity.setDropGoldTorpedoNum(fishingChallengePlayer.getDropGoldTorpedoNum());
        recordLogEntity.setDropGoldTorpedoBangNum(fishingChallengePlayer.getDropGoldTorpedoBangNum());
        recordLogEntity.setDropRareTorpedoBangNum(fishingChallengePlayer.getDropRareTorpedoNum());
        recordLogEntity.setDropRareTorpedoBangNum(fishingChallengePlayer.getDropRareTorpedoBangNum());

        // 追加
        long joinTime = fishingChallengePlayer.getEnterRoomTime();
        recordLogEntity.setJoinTime(new Date(joinTime));
        recordLogEntity.setExitTime(new Date(currentTimeMillis));
        recordLogEntity.setTimeSpending(currentTimeMillis - joinTime);
        final String useProps = FishingChallengeManager.JOIN_ITEM.stream()
                .map(id -> id.getInfo() + (fishingChallengePlayer.getJoinItemCount().getOrDefault(id, 0L)
                        - PlayerManager.getItemNum(serverUser, id)))
                .reduce((o1, o2) -> o1 + "," + o2).orElse("");
        recordLogEntity.setUseProps(useProps);

        recordLogEntity.setGameType(1);

        oseeFishingRecordLogMapper.save(recordLogEntity);

    }

    /**
     * 保存账户变动记录
     */
    private static void saveTenureLog(FishingChallengePlayer fishingChallengePlayer, ServerUser serverUser,
                                      OseePlayerEntity oseePlayerEntity) {

        if (fishingChallengePlayer.getChangeMoney() != 0) {

            synchronized (oseePlayerEntity) {
                OseePlayerTenureLogEntity log = new OseePlayerTenureLogEntity();
                log.setUserId(serverUser.getId());
                log.setNickname(serverUser.getNickname());
                log.setReason(ItemChangeReason.FISHING_RESULT.getId());
                log.setPreBankMoney(oseePlayerEntity.getBankMoney());
                log.setPreDragonCrystal(fishingChallengePlayer.getEnterMoney());
                log.setPreLottery(oseePlayerEntity.getLottery());
                log.setChangeDragonCrystal(
                        fishingChallengePlayer.getChangeMoney() - fishingChallengePlayer.getTorpedoMoney());

                oseePlayerTenureLogMapper.save(log);
            }

        }

    }

    /**
     * 移出房间
     */
    private static void removeFromRoom(FishingChallengePlayer fishingChallengePlayer,
                                       NewBaseFishingRoom newBaseFishingRoom, ServerUser serverUser) {

        // 移出房间
        serverUser.setFishingChallengeRoomType(0);

        GameContainer.removeGamePlayer(newBaseFishingRoom, fishingChallengePlayer.getId(), false);

    }

    public static final String USER_BATTERY_LEVEL_USED_PRE = "USER_BATTERY_LEVEL_USED_PRE:";

    /**
     * 处理：切换炮倍
     */
    public static int handlerBatteryLevelChange(NewBaseGamePlayer player, long targetLevel, NewBaseGameRoom room,
                                                boolean joinRoomFlag, String reasonStr, OseePlayerEntity playerEntity, Long oldBatteryLevel) {

        long userId = player.getId();

        String redisKey = USER_BATTERY_LEVEL_USED_PRE + userId;

        long usedBatteryLevel = RedisUtil.val(redisKey, 0L);

        RedisHelper.set(redisKey, String.valueOf(targetLevel));

        // 处理：正态分布的炮倍切换相关
        handlerNormalDistributionBatteryLevelChange(player, targetLevel, usedBatteryLevel);

        long money;

        if (joinRoomFlag) {

            money = player.getMoney();

        } else {

            money = 0L;

        }

        // log.info("batteryLevel-2：{}", player.getBatteryLevel());

        // 切换炮倍：清除：盈利次数相关参数
        int clearType = FishingChallengeFightFishUtil.clearYlcs(playerEntity, reasonStr, joinRoomFlag, money, player,
                oldBatteryLevel);

        if (joinRoomFlag || usedBatteryLevel > targetLevel) { // 如果是：进入房间，或者高切低

            // 清除：所有的 xh
            FishingChallengeFightFishUtil.deleteAllXh(userId);

        }

        return clearType;

    }

    // 切换炮倍缓冲消耗池，根据：用户进行区分
    public static final String PRE_GRHPHCC_USER = "PRE_GRHPHCC_USER:";

    /**
     * 处理：正态分布的炮倍切换相关
     */
    private static void handlerNormalDistributionBatteryLevelChange(NewBaseGamePlayer fishingGamePlayer,
                                                                    long targetLevel, long usedBatteryLevel) {

        if (usedBatteryLevel == targetLevel) {
            return;
        }

        long userId = fishingGamePlayer.getId();

        boolean highToLowFlag = usedBatteryLevel > targetLevel; // 是否是：高切低

        // 当前命中的总次数，redisKeyPre
        List<String> preCurrentHitCountRedisKeyList = new ArrayList<>();

        // for (ControlTypeEnum item : ControlTypeEnum.values()) {

        preCurrentHitCountRedisKeyList.add(ControlTypeEnum.NORMAL_CONTROL.getCurrentHitCountPre() + userId + ":");

        // }

        // 获取：所有的 modelIdSet
        Map<String, Double> redisMap = MapUtil.newHashMap(ALL_MODEL_ID_SET.size() + 1);


        for (Integer item : ALL_MODEL_ID_SET) {

            for (String subItem : preCurrentHitCountRedisKeyList) {

                String itemRedisKey = subItem + item;

                double redisValue = redissonClient.getAtomicDouble(itemRedisKey).get(); // 获取：当前命中次数

                redisMap.put(itemRedisKey, redisValue);

            }

        }

        if (CollUtil.isNotEmpty(redisMap)) {

            String preGrhphccKey = PRE_GRHPHCC_USER + userId + ":";

            if (highToLowFlag) {

                // 如果是：高切低
                highToLow(targetLevel, usedBatteryLevel, redisMap, preGrhphccKey);

            } else {

                // 如果是：低切高
                lowToHigh(targetLevel, usedBatteryLevel, redisMap, preGrhphccKey);

            }

        }

    }

    /**
     * 处理：低切高
     */
    private static void lowToHigh(long highLevel, long lowLevel, Map<String, Double> redisMap, String preGrhphccKey) {

        RBatch batch = redissonClient.createBatch();

        for (Map.Entry<String, Double> item : redisMap.entrySet()) {

            String grhphccKey = preGrhphccKey + item.getKey();

            double currentHitValue = item.getValue(); // 当前：攻击的次数

            double lowUsedMoney = currentHitValue * lowLevel; // 低倍消耗金币数

            double hitValueOne = lowUsedMoney / highLevel; // 攻击次数一

            double checkGrhphcc = (currentHitValue - hitValueOne) * highLevel;

            double grhphcc = redissonClient.getAtomicDouble(grhphccKey).get(); // 缓冲池的值

            double newCurrentHitValue; // 新的：当前攻击次数的值

            if (checkGrhphcc > grhphcc) { // 如果：大于缓冲池的值

                newCurrentHitValue = grhphcc / highLevel; // 攻击次数二：缓冲消耗池/高倍炮

                batch.getAtomicDouble(grhphccKey).setAsync(0); // 重置：缓存池的值

            } else { // 如果：小于缓冲池的值

                newCurrentHitValue = currentHitValue - hitValueOne; // 攻击次数二：低倍攻击次数-攻击次数一

            }

            newCurrentHitValue = hitValueOne + newCurrentHitValue; // 攻击次数一 + 攻击次数二

            batch.getAtomicDouble(item.getKey()).setAsync(newCurrentHitValue); // 设置：新的值

            // if (item.getKey().endsWith(":-218465:23")) {
            // log.info(
            // "usedBatteryLevel：{}，targetLevel：{}，currentHitValue：{}，newCurrentHitValue：{}，grhphcc：{}，checkGrhphcc：{}，lowUsedMoney：{}，hitValueOne：{}
            // ",
            // lowLevel, highLevel, currentHitValue, newCurrentHitValue, grhphcc, checkGrhphcc, lowUsedMoney,
            // hitValueOne);
            // }

        }

        batch.execute(); // 执行：批量操作

    }

    /**
     * 处理：高切低
     */
    private static void highToLow(long lowLevel, long highLevel, Map<String, Double> redisMap, String preGrhphccKey) {

        RBatch batch = redissonClient.createBatch();

        for (Map.Entry<String, Double> item : redisMap.entrySet()) {

            String grhphccKey = preGrhphccKey + item.getKey();

            double currentHitValue = item.getValue(); // 当前：攻击的次数

            double highUsedMoney = highLevel * currentHitValue; // 高倍消耗金币数

            double lowUsedMoney = lowLevel * currentHitValue; // 低倍消耗金币数

            double grhphcc = highUsedMoney - lowUsedMoney; // 需要存入：缓存池的值

            // if (item.getKey().endsWith(":-218465:23")) {
            // log.info(
            // "usedBatteryLevel：{}，targetLevel：{}，highUsedMoney：{}，lowUsedMoney：{}，grhphcc：{}，newGrhphcc：{}，currentHitValue：{}，
            // ",
            // highLevel, lowLevel, highUsedMoney, lowUsedMoney, grhphcc,
            // (grhphcc + redissonClient.getAtomicDouble(grhphccKey).get()), currentHitValue);
            // }

            batch.getAtomicDouble(grhphccKey).addAndGetAsync(grhphcc);

        }

        batch.execute(); // 执行：批量操作

    }

}
