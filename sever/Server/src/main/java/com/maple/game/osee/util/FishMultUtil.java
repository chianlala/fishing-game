package com.maple.game.osee.util;

import cn.hutool.core.collection.CollUtil;
import com.maple.game.osee.pojo.fish.AbsFish;
import com.maple.game.osee.pojo.fish.FishMultipleHelperDTO;
import com.maple.game.osee.proto.fishing.FishBossMessage;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.util.TreeMap;

/**
 * 鱼倍数，工具类
 */
@Slf4j
public class FishMultUtil {

    @SneakyThrows
    public static byte[] getByteArr(AbsFish absFish) {

        TreeMap<Long, byte[]> treeMap = FishMultGenerateUtil.getTreeMapByAbsFish(absFish);

        if (CollUtil.isNotEmpty(treeMap)) {

            Long randomMoney = FishingFightFishUtil.doGetRandomMoney(absFish);

            // 通过 absFish，获取该鱼 最接近的倍数
            randomMoney = FishMultGenerateUtil.getValueByTreeMap(treeMap, randomMoney);

            byte[] byteArr = treeMap.get(randomMoney);

            absFish.setMessage(FishBossMessage.FishBossMultipleResponse.parseFrom(byteArr).toBuilder());
            absFish.setRandomMoney(randomMoney); // 必须赋值
            absFish.getMessage().setMult((int)absFish.getRandomMoney());

            return byteArr;

        } else {

            return getByteArrBase(absFish);

        }

    }

    private static byte[] getByteArrBase(AbsFish absFish) {

        FishMultipleHelperDTO fishMultipleHelperDTO = absFish.onlyComputeMultiple();

        absFish.setMessage(fishMultipleHelperDTO.getMessage());
        absFish.setRandomMoney(fishMultipleHelperDTO.getRandomMoney()); // 必须赋值
        absFish.getMessage().setMult((int)absFish.getRandomMoney());

        return absFish.getMessage().build().toByteArray();

    }

}
