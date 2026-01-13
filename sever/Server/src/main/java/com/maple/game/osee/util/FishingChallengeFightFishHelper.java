package com.maple.game.osee.util;

import cn.hutool.core.collection.CollUtil;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class FishingChallengeFightFishHelper {

    /**
     * yzdfz 集合
     */
    private static final List<Integer> YZDFZ_LIST = CollUtil.newArrayList(2, 10);

    private static String PRE_YZDFZ = "YZDFZ:";

    private static RedissonClient redissonClient;

    public FishingChallengeFightFishHelper(RedissonClient redissonClient) {
        FishingChallengeFightFishHelper.redissonClient = redissonClient;
    }

    /**
     * 检查：kzylbs（控制余量倍数）
     */
    public static boolean checkKzylbs(double kzylbs) {

        return -5 < kzylbs && kzylbs < 5;

    }


}
