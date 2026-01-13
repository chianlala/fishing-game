package com.maple.game.osee.pojo.fish;

import cn.hutool.core.map.MapUtil;
import com.maple.engine.data.ServerUser;
import com.maple.game.osee.entity.fishing.NewBaseFishingRoom;
import com.maple.game.osee.entity.fishing.csv.file.FishConfig;
import com.maple.game.osee.entity.fishing.game.FishStruct;
import kotlin.jvm.functions.Function4;

import java.util.Map;

/**
 * 生成鱼
 */
public class FishFactory {

    private static final Map<Integer,
            Function4<FishStruct, FishConfig, ServerUser, NewBaseFishingRoom, AbsFish>> SPECIAL_FISH_MAP =
            MapUtil.newHashMap();

    static {
        // 奖券
        SPECIAL_FISH_MAP.put(90, (fish, config, user, room) -> new JiangQuan(fish, config));
    }

    /**
     * @param fish   鱼属性
     * @param config 鱼配置
     * @param user   攻击的用户
     * @return 被攻击的鱼
     */
    public static AbsFish create(FishStruct fish, FishConfig config, ServerUser user, NewBaseFishingRoom room) {
        Function4<FishStruct, FishConfig, ServerUser, NewBaseFishingRoom, AbsFish> function4 =
                SPECIAL_FISH_MAP.get(config.getModelId());
        if (function4 == null) {
            // 默认返回普通鱼
            return new CommonFish(fish, config, user);

        } else {

            // 返回：特殊鱼
            return function4.invoke(fish, config, user, room);

        }

    }
}
