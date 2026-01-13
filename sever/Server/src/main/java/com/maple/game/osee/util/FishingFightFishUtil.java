package com.maple.game.osee.util;

import com.maple.database.config.redis.RedisHelper;
import com.maple.game.osee.entity.NewBaseGamePlayer;
import com.maple.game.osee.entity.fishing.challenge.FishingChallengeRoom;
import com.maple.game.osee.pojo.fish.AbsFish;
import com.maple.game.osee.proto.OseeMessage;
import com.maple.game.osee.proto.fishing.FishBossMessage;
import com.maple.game.osee.proto.fishing.TtmyFishingChallengeMessage;
import com.maple.gamebase.data.BaseGamePlayer;
import com.maple.gamebase.data.fishing.BaseFishingRoom;
import com.maple.network.manager.NetManager;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;

@Component
@Slf4j
public class FishingFightFishUtil {

    /**
     * 杀死鱼之后，发送信息
     */
    @SneakyThrows
    public static void afterKillSendMessageForBoss(BaseFishingRoom room, long winMoney, byte[] messageByteArr,
                                                   AbsFish absFish) {

        RedisHelper.remove(absFish.getKey());

        FishBossMessage.FishBossMultipleResponse.Builder message =
                FishBossMessage.FishBossMultipleResponse.parseFrom(messageByteArr).toBuilder();

        afterKillSendMessageForBoss(room, winMoney, message, absFish);

    }

    /**
     * 杀死鱼之后，发送信息
     */
    public static void afterKillSendMessageForBoss(BaseFishingRoom room, long winMoney,
                                                   FishBossMessage.FishBossMultipleResponse.Builder message, AbsFish absFish) {

        message.setFishId(absFish.getFish().getId()); // 这里要用最新的 fishId，不然前端杀不死鱼
        message.setMoney(winMoney);
        message.setPlayerId(absFish.getUser().getId());
        message.setFishName(absFish.getConfig().getName());

        // 给房间内的所有用户发送信息
        for (BaseGamePlayer gamePlayer : room.getGamePlayers()) {
            if (gamePlayer != null) {
                NetManager.sendMessage(OseeMessage.OseeMsgCode.S_C_FISH_BOSS_MULTIPLE_RESPONSE_VALUE, message,
                        gamePlayer.getUser());
            }
        }

    }

    /**
     * 杀死鱼之后，发送信息
     */
    @SneakyThrows
    public static void afterKillSendMessageForDouble(BaseFishingRoom room, long winMoney, byte[] messageByteArr,
                                                     AbsFish absFish) {

        RedisHelper.remove(absFish.getKey());

        TtmyFishingChallengeMessage.FishingChallengeDoubleKillResponse.Builder message =
                TtmyFishingChallengeMessage.FishingChallengeDoubleKillResponse.parseFrom(messageByteArr).toBuilder();

        afterKillSendMessageForDouble(room, winMoney, message, absFish);

    }

    /**
     * 杀死鱼之后，发送信息
     */
    @SneakyThrows
    public static void afterKillSendMessageForDouble(BaseFishingRoom room, long winMoney, byte[] messageByteArr,
                                                     AbsFish absFish, Consumer<TtmyFishingChallengeMessage.FishingChallengeDoubleKillResponse.Builder> consumer) {

        TtmyFishingChallengeMessage.FishingChallengeDoubleKillResponse.Builder message =
                TtmyFishingChallengeMessage.FishingChallengeDoubleKillResponse.parseFrom(messageByteArr).toBuilder();

        afterKillSendMessageForDouble(room, winMoney, message, absFish, consumer);

    }

    /**
     * 杀死鱼之后，发送信息
     */
    public static void afterKillSendMessageForDouble(BaseFishingRoom room, long winMoney,
                                                     TtmyFishingChallengeMessage.FishingChallengeDoubleKillResponse.Builder builder, AbsFish absFish) {

        afterKillSendMessageForDouble(room, winMoney, builder, absFish, null);

    }

    /**
     * 杀死鱼之后，发送信息
     */
    public static void afterKillSendMessageForDouble(BaseFishingRoom room, long winMoney,
                                                     TtmyFishingChallengeMessage.FishingChallengeDoubleKillResponse.Builder builder, AbsFish absFish,
                                                     @Nullable Consumer<TtmyFishingChallengeMessage.FishingChallengeDoubleKillResponse.Builder> consumer) {

        builder.setName(absFish.getConfig().getName());
        builder.setModelId(absFish.getFish().getId()); // 备注：modelId，暂时没用，所以这里传什么值不影响
        builder.setUserId(absFish.getUser().getId());

        NewBaseGamePlayer player = room.getGamePlayerById(absFish.getUser().getId());

        builder.setBatteryLevel(player.getBatteryLevel());

        builder.setWinMoney(winMoney);

        if (consumer != null) {

            consumer.accept(builder);

        }

        if (absFish.getFishKillResponseValue() == null) {

            if (room instanceof FishingChallengeRoom) {

                String result = builder.getJsonStr().replace("\\", ""); // 排除\\
                builder.setJsonStr(result);
                MyRefreshFishingUtil.sendRoomMessage(room,
                        OseeMessage.OseeMsgCode.S_C_FISHING_CHALLENGE_DOUBLE_KILL_RESPONSE_VALUE, builder);

            }

        } else {

            NetManager.sendMessage(absFish.getFishKillResponseValue(), builder, absFish.getUser());

        }

    }

    /**
     * 从配置文件里面，获取 倍数
     */
    public static long doGetRandomMoney(AbsFish absFish) {

        Byte type = absFish.getType();

        if (type == null || type == 1) { // 正常范围（默认）

            return getBaseMoney(absFish);

        } else if (type == 3) { // 爆发范围

            if (absFish.getConfig().getBfMaxMoney() > absFish.getConfig().getBfMoney()) {
                return RandomUtil.getRandom(absFish.getConfig().getBfMoney(), absFish.getConfig().getBfMaxMoney() + 1);
            }

            return absFish.getConfig().getBfMoney();

        } else { // 回收范围

            if (absFish.getConfig().getHsMaxMoney() > absFish.getConfig().getHsMoney()) {
                return RandomUtil.getRandom(absFish.getConfig().getHsMoney(), absFish.getConfig().getHsMaxMoney() + 1);
            }

            return absFish.getConfig().getHsMoney();

        }

    }

    private static long getBaseMoney(AbsFish absFish) {

        // Long bfJdcz = absFish.getFish().getBfJdcz();
        //
        // if (bfJdcz != null && bfJdcz > 0) {
        //
        // long bfMaxMoney = absFish.getConfig().getBfMaxMoney();
        //
        // log.info("{}：最高值：{}，爆发阶段差值：{}，maxMoney：{}", absFish.getConfig().getName(), bfMaxMoney, bfJdcz,
        // absFish.getConfig().getMaxMoney());
        //
        // if (bfMaxMoney > bfJdcz && bfJdcz > absFish.getConfig().getBfMoney()) {
        //
        // bfMaxMoney = bfJdcz;
        //
        // }
        //
        // return RandomUtil.getRandom(absFish.getConfig().getBfMoney(), bfMaxMoney + 1);
        //
        // }

        double minRandomMoney = absFish.getMinRandomMoney();

        double maxRandomMoney = absFish.getMaxRandomMoney();

        long minMoney = absFish.getConfig().getBfMoney();

        long maxMoney = absFish.getConfig().getBfMaxMoney();

        if (minRandomMoney != 0 && maxRandomMoney != 0 && maxRandomMoney > minRandomMoney && maxMoney > minMoney) {

            long min = (long) (maxRandomMoney - minRandomMoney);

            long max = (long) maxRandomMoney;

            if (min >= maxMoney) {
                return maxMoney;
            }

            if (max <= minMoney) {
                return minMoney;
            }

            if (min < minMoney) {

                min = minMoney;

            }

            if (max > maxMoney) {

                max = maxMoney;

            }

            long random = RandomUtil.getRandom(min, max + 1);

            // log.info("鱼：{}，倍数：{}，实际最小：{}，实际最大：{}，节点最小：{}，节点最大：{}，配置最小：{}，配置最大：{}", absFish.getConfig().getName(),
            // random, min, max, minRandomMoney, maxRandomMoney, minMoney, maxMoney);

            return random;

        }

        minMoney = absFish.getConfig().getMoney();

        maxMoney = absFish.getConfig().getMaxMoney();

        if (maxMoney > minMoney) {

            return RandomUtil.getRandom(minMoney, maxMoney + 1);

        }

        return minMoney;

    }

}
