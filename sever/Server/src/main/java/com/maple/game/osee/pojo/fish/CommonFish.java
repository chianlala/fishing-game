package com.maple.game.osee.pojo.fish;

import com.maple.engine.data.ServerUser;
import com.maple.game.osee.entity.fishing.csv.file.FishConfig;
import com.maple.game.osee.entity.fishing.game.FishStruct;
import com.maple.game.osee.util.FishingFightFishUtil;
import com.maple.gamebase.data.fishing.BaseFishingRoom;

/**
 * 一般的鱼 从配置表中随机倍数
 */
public class CommonFish extends AbsFish {

    public CommonFish(FishStruct fish, FishConfig config, ServerUser user) {
        super(fish, config, user);
    }

    @Override
    public int getComputeMultipleType() {

        if (computeMultipleType == 0) {

            return this.config.getMaxMoney() > this.config.getMoney() ? 2 : 3;

        }

        return computeMultipleType;

    }

    @Override
    public FishMultipleHelperDTO onlyComputeMultiple() {

        FishMultipleHelperDTO fishMultipleHelperDTO = new FishMultipleHelperDTO();

        fishMultipleHelperDTO.setRandomMoney(FishingFightFishUtil.doGetRandomMoney(this));

        return fishMultipleHelperDTO;

    }

    /**
     * 从配置表中获取倍数
     *
     * @param key 缓存的键 用来辅助其他缓存值
     */
    @Override
    public long computeMultiple(String key) {

        return FishingFightFishUtil.doGetRandomMoney(this);

    }

    @Override
    public void afterKillByMessageByteArr(BaseFishingRoom gameRoom, long winMoney, byte[] messageByteArr) {

    }

}
