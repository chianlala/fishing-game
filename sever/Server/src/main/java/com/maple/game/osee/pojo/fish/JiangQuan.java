package com.maple.game.osee.pojo.fish;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.RandomUtil;
import com.maple.game.osee.entity.fishing.csv.file.FishConfig;
import com.maple.game.osee.entity.fishing.game.FishStruct;
import com.maple.gamebase.data.fishing.BaseFishingRoom;

import java.util.List;

/**
 * 奖券
 */
public class JiangQuan extends AbsFish {

    public JiangQuan(FishStruct fish, FishConfig config) {
        super(fish, config);
    }

    @Override
    public int getComputeMultipleType() {
        return 3;
    }

    @Override
    public FishMultipleHelperDTO onlyComputeMultiple() {

        FishMultipleHelperDTO fishMultipleHelperDTO = new FishMultipleHelperDTO();

        List<Integer> multipleList = CollUtil.newArrayList(1, 2, 5, 10, 20, 50, 100);

        fishMultipleHelperDTO.setRandomMoney(RandomUtil.randomEle(multipleList));

        return fishMultipleHelperDTO;

    }

    @Override
    public long computeMultiple(String key) {
        return onlyComputeMultiple().getRandomMoney();
    }

    @Override
    public void afterKillByMessageByteArr(BaseFishingRoom gameRoom, long winMoney, byte[] messageByteArr) {

    }

}
