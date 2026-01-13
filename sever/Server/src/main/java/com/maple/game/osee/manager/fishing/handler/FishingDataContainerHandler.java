package com.maple.game.osee.manager.fishing.handler;

import com.maple.engine.handler.DataContainerHandler;
import com.maple.game.osee.entity.fishing.NewBaseFishingRoom;
import com.maple.game.osee.util.FishingChallengeFightFishUtil;
import com.maple.game.osee.util.MyRefreshFishingHelper;
import com.maple.game.osee.util.MyRefreshFishingUtil;
import com.maple.gamebase.container.GameContainer;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class FishingDataContainerHandler implements DataContainerHandler {

    @Override
    public void handler() {

        GameContainer.getGameRooms(NewBaseFishingRoom.class).stream().filter(Objects::nonNull).forEach(gameRoom -> {
            gameRoom.setNextRefreshTime(null);
        });

        // 初始化
        MyRefreshFishingUtil.init();

        // 初始化
        MyRefreshFishingHelper.WORLD_BOSS_ROOM_INDEX_LIST = MyRefreshFishingUtil.CHALLENGE_FISHING_ROOM_INDEX_LIST;

        // 初始化
        MyRefreshFishingHelper.REFRESH_TIME_RULE_ID_AND_ROOM_CODE_MAP.clear();
    }

}
