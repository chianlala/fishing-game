package com.maple.game.osee.util;

import cn.hutool.core.date.BetweenFormatter;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.RandomUtil;
import com.maple.game.osee.model.dto.ProfitRatioDTO;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.redisson.codec.JsonJacksonCodec;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * 捕鱼：T 工具类
 */
@Component
@Slf4j
public class FishingTUtil {

    private static RedissonClient redissonClient;

    public FishingTUtil(RedissonClient redissonClient) {
        FishingTUtil.redissonClient = redissonClient;
    }

    private static ProfitRatioDTO profitRatioDTO = null;

    @NotNull
    public static ProfitRatioDTO getProfitRatioDTO() {

        if (profitRatioDTO != null) {

            int size = MyRefreshFishingUtil.CHALLENGE_AND_INTEGRAL_FISHING_CCXX_CONFIG_LIST.size();

            if (size != 0 && size != profitRatioDTO.getProfitRatioMinArr().length) {

                profitRatioDTO = null;

                redissonClient.getBucket("ProfitRatioDTO", new JsonJacksonCodec()).delete();

                return new ProfitRatioDTO();

            }

            return profitRatioDTO;

        } else {

            RBucket<ProfitRatioDTO> bucket = redissonClient.getBucket("ProfitRatioDTO", new JsonJacksonCodec());

            ProfitRatioDTO redisValue = bucket.get();

            if (redisValue == null) {

                profitRatioDTO = new ProfitRatioDTO();
                bucket.set(profitRatioDTO); // 设置到：缓存里面

            } else {

                profitRatioDTO = redisValue;

            }

            return profitRatioDTO;

        }

    }

    /**
     * 设置：ProfitRatioDTO对象
     */
    public static ProfitRatioDTO setProfitRatioDTO(ProfitRatioDTO temp) {

        redissonClient.<ProfitRatioDTO>getBucket("ProfitRatioDTO", new JsonJacksonCodec()).set(temp);
        profitRatioDTO = temp;

        return temp;

    }

    public static RBucket<Integer> getProfitRatioValueBucket(int roomIndex) {

        return redissonClient.getBucket("redisProfitRatioValue:" + roomIndex);

    }

    public static RBucket<Integer> getProfitRatioNextValueBucket(int roomIndex) {

        return redissonClient.getBucket("profitRatioNextValue:" + roomIndex);

    }

    /**
     * 获取：期望收益比的值
     *
     * @return 0 到 1 之间的小数
     */
    public static BigDecimal getProfitRatioHope(int roomIndex) {

        RBucket<Integer> profitRatioValueBucket = getProfitRatioValueBucket(roomIndex);

        // 例如：[920,980] 之间的数
        Integer profitRatioValue = profitRatioValueBucket.get();

        // 如果缓存值不存在，则重新在范围里面获取，并设置到缓存里面
        if (profitRatioValue == null) {

            ProfitRatioDTO dto = getProfitRatioDTO();

            int index = dto.getIndexByRoomIndex(roomIndex);

            if (index == -1) { // 如果：该 roomIndex不存在

                dto = setProfitRatioDTO(new ProfitRatioDTO());

            }

            index = dto.getIndexByRoomIndex(roomIndex); // 再获取一次

            if (index == -1) {
                throw new RuntimeException("roomIndex不存在：" + roomIndex);
            }

            // RBucket<Integer> profitRatioNextValueBucket = getProfitRatioNextValueBucket(roomIndex);
            //
            // Integer profitRatioNextValue = profitRatioNextValueBucket.get(); // 获取：下一次收益比的值
            //
            // if (profitRatioNextValue == null) {

            profitRatioValue =
                RandomUtil.randomInt(dto.getProfitRatioMinArr()[index], dto.getProfitRatioMinArr()[index] + 1);

            // } else {
            //
            // profitRatioValue = profitRatioNextValue;
            //
            // }

            // int timeToLive = dto.getFrequencyArr()[index];
            //
            // if (timeToLive <= 0) {
            // timeToLive = 180;
            // }
            //
            // profitRatioValueBucket.set(profitRatioValue, timeToLive, TimeUnit.MINUTES);

            profitRatioValueBucket.set(profitRatioValue);

            // profitRatioNextValue =
            // RandomUtil.randomInt(dto.getProfitRatioMinArr()[index], dto.getProfitRatioMinArr()[index] + 1);
            //
            // profitRatioNextValueBucket.set(profitRatioNextValue); // 重置：下次收益比的值

        }

        // 计算：期望收益比
        return calcProfitRatio(profitRatioValue);

    }

    /**
     * 计算：收益比
     */
    private static BigDecimal calcProfitRatio(Integer profitRatioValue) {

        return NumberUtil.div(profitRatioValue, new Integer(1000), 4);

    }

    // /**
    // * 获取：下次收益比
    // */
    // public static BigDecimal getProfitRatioNextValue(int roomIndex) {
    //
    // RBucket<Integer> profitRatioNextValueBucket = getProfitRatioNextValueBucket(roomIndex);
    //
    // Integer profitRatioNextValue = profitRatioNextValueBucket.get(); // 获取：下一次收益比的值
    //
    // if (profitRatioNextValue == null) {
    //
    // ProfitRatioDTO dto = getProfitRatioDTO();
    //
    // profitRatioNextValue =
    // RandomUtil.randomInt(dto.getProfitRatioMinArr()[roomIndex], dto.getProfitRatioMinArr()[roomIndex] + 1);
    //
    // profitRatioNextValueBucket.set(profitRatioNextValue); // 重置：下次收益比的值
    //
    // }
    //
    // return calcProfitRatio(profitRatioNextValue); // 计算：收益比
    //
    // }

    /**
     * 获取：收益比变化倒计时 index：0 1 2 3 4
     */
    public static String getCountdown(int index) {

        long remainTimeToLive = getProfitRatioValueBucket(index).remainTimeToLive();

        if (remainTimeToLive < 0) { // 如果 key不存在
            getProfitRatioHope(index); // 设置 key
            return getCountdown(index); // 返回：倒计时
        }

        return cn.hutool.core.date.DateUtil.formatBetween(remainTimeToLive, BetweenFormatter.Level.SECOND); // 剩余时间（字符串）

    }

    /**
     * 获取：每个房间的 期望收益金币，固定倍数
     */
    public static BigDecimal getGoldHope(int roomIndex) {

        long xh = redissonClient.getAtomicLong("ALL_USED_XH_TOTAL_STR:" + roomIndex).get();

        return new BigDecimal(xh).multiply(BigDecimal.ONE.subtract(getProfitRatioHope(roomIndex))).setScale(5,
            BigDecimal.ROUND_HALF_DOWN);

    }

    /**
     * 获取：每个房间的 实际收益金币，固定倍数
     */
    public static BigDecimal getGoldReal(int roomIndex) {

        long xh = redissonClient.getAtomicLong("ALL_USED_XH_TOTAL_STR:" + roomIndex).get();

        long produceGoldTotal = redissonClient.getAtomicLong("ALL_PRODUCE_GOLD_TOTAL_STR:" + roomIndex).get();

        return new BigDecimal(xh).subtract(new BigDecimal(produceGoldTotal).setScale(5, BigDecimal.ROUND_HALF_DOWN));

    }

}
