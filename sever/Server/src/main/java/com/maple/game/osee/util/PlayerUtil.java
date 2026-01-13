package com.maple.game.osee.util;

import static com.maple.game.osee.controller.gm.GmCommonController.handleRoomIndexStr;

import java.util.Date;

import javax.annotation.Resource;

import org.redisson.api.RAtomicDouble;
import org.redisson.api.RBatch;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import com.maple.game.osee.dao.data.entity.OseePlayerEntity;
import com.maple.game.osee.entity.fishing.NewBaseFishingRoom;

import cn.hutool.core.date.DateUtil;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class PlayerUtil {

    private static RedissonClient redissonClient;

    @Resource
    public void setRedissonClient(RedissonClient redissonClient) {
        PlayerUtil.redissonClient = redissonClient;
    }

    /**
     * 处理：使用子弹
     */
    public static void handleUseBattery(RedissonClient redissonClient, long useCount, RBatch batch, String roomIndexStr,
        long userId, boolean addGapFlag) {

        // 增加：玩家场次历史消耗的金币
        batch
            .getAtomicDouble(
                FishingChallengeFightFishUtil.FISHING_CCLS_XH_ROOM_INDEX_USER_PRE + roomIndexStr + ":" + userId)
            .addAndGetAsync(useCount);

        // 增加：玩家进场消耗的金币
        batch
            .getAtomicDouble(
                FishingChallengeFightFishUtil.FISHING_JC_XH_ROOM_INDEX_USER_PRE + roomIndexStr + ":" + userId)
            .addAndGetAsync(useCount);

        if (addGapFlag) {

            // 击杀上一条鱼后，到本次这条鱼的消耗
            batch
                .getAtomicDouble(
                    FishingChallengeFightFishUtil.FISHING_KILL_GAP_XH_ROOM_INDEX_USER_PRE + roomIndexStr + ":" + userId)
                .addAndGetAsync(useCount);

        }

        RAtomicDouble jrRatomicDouble =
            redissonClient.getAtomicDouble(FishingChallengeFightFishUtil.FISHING_JR_XH_USER_PRE + userId);

        // 增加：玩家今日消耗的金币
        jrRatomicDouble.addAndGet(useCount);

        if (jrRatomicDouble.remainTimeToLive() == -1) {

            // 设置：今日过期
            jrRatomicDouble.expire(DateUtil.endOfDay(new Date()).toInstant());

        }

        RAtomicDouble roomJrRatomicDouble = redissonClient.getAtomicDouble(
            FishingChallengeFightFishUtil.FISHING_JR_XH_ROOM_INDEX_USER_PRE + userId + ":" + roomIndexStr);

        // 增加：玩家今日场次消耗的金币
        roomJrRatomicDouble.addAndGet(useCount);

        if (roomJrRatomicDouble.remainTimeToLive() == -1) {

            // 设置：今日过期
            roomJrRatomicDouble.expire(DateUtil.endOfDay(new Date()).toInstant());

        }

    }

    /**
     * 处理：赢的钱
     */
    public static void handleWinMoney(long winMoney, OseePlayerEntity playerEntity, NewBaseFishingRoom gameRoom) {

        // 累计：获得的金币
        playerEntity.setTotalDragonCrystal(playerEntity.getTotalDragonCrystal() + winMoney);

        RBatch batch = redissonClient.createBatch();

        String roomIndexStr = String.valueOf(gameRoom.getRoomIndex());

        // 处理：roomIndexStr
        roomIndexStr = handleRoomIndexStr(playerEntity.getUserId(), gameRoom, roomIndexStr);

        PlayerUtil.handleProduce(winMoney, playerEntity, batch, roomIndexStr);

        batch.execute(); // 执行：批量操作

    }

    /**
     * 处理：产出
     */
    public static void handleProduce(long winMoney, OseePlayerEntity playerEntity, RBatch batch, String roomIndexStr) {

        // 增加：玩家场次历史获得的金币
        batch.getAtomicDouble(FishingChallengeFightFishUtil.FISHING_CCLS_PRODUCE_ROOM_INDEX_USER_PRE + roomIndexStr
            + ":" + playerEntity.getUserId()).addAndGetAsync(winMoney);

        // 增加：玩家进场获得的金币
        batch.getAtomicDouble(FishingChallengeFightFishUtil.FISHING_JC_PRODUCE_ROOM_INDEX_USER_PRE + roomIndexStr + ":"
            + playerEntity.getUserId()).addAndGetAsync(winMoney);

        RAtomicDouble jrRatomicDouble = redissonClient
            .getAtomicDouble(FishingChallengeFightFishUtil.FISHING_JR_PRODUCE_USER_PRE + playerEntity.getUserId());

        // 增加：玩家今日获得的金币
        jrRatomicDouble.addAndGet(winMoney);

        if (jrRatomicDouble.remainTimeToLive() == -1) {

            // 设置：今日过期
            jrRatomicDouble.expire(DateUtil.endOfDay(new Date()).toInstant());

        }

        RAtomicDouble roomJrRatomicDouble =
            redissonClient.getAtomicDouble(FishingChallengeFightFishUtil.FISHING_JR_PRODUCE_ROOM_INDEX_USER_PRE
                + playerEntity.getUserId() + ":" + roomIndexStr);

        // 增加：玩家今日场次获得的金币
        roomJrRatomicDouble.addAndGet(winMoney);

        if (roomJrRatomicDouble.remainTimeToLive() == -1) {

            // 设置：今日过期
            roomJrRatomicDouble.expire(DateUtil.endOfDay(new Date()).toInstant());

        }

    }

}
