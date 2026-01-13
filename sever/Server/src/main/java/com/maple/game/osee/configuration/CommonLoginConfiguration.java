package com.maple.game.osee.configuration;

import com.maple.common.login.configuration.ICommonLoginConfiguration;
import com.maple.database.data.entity.UserEntity;
import com.maple.game.osee.entity.NewBaseGameRoom;
import com.maple.game.osee.manager.fishing.FishingGrandPrixManager;
import com.maple.game.osee.util.GameUtil;
import com.maple.gamebase.container.GameContainer;
import com.maple.gamebase.data.BaseGamePlayer;
import com.maple.gamebase.data.BaseGameRoom;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class CommonLoginConfiguration implements ICommonLoginConfiguration {

    @Autowired
    private FishingGrandPrixManager fishingGrandPrixManager;

    /**
     * 处理：退出房间相关
     */
    @Override
    public void handlerExitRoom(UserEntity userEntity) {

        GameUtil.exitRoom(userEntity.getId());

    }

    /**
     * 获取：房间 index相关，大厅返回 0
     */
    @Override
    public int getRoomIndex(UserEntity userEntity) {

        BaseGamePlayer baseGamePlayer = GameContainer.getPlayerById(userEntity.getId());

        if (baseGamePlayer == null) {
            return 0;
        }

        BaseGameRoom baseGameRoom = GameContainer.getGameRoomByCode(baseGamePlayer.getRoomCode());

        if (baseGameRoom == null) {
            return 0;
        }

        if (baseGameRoom instanceof NewBaseGameRoom) {
            // 在捕鱼房间内
            return ((NewBaseGameRoom) baseGameRoom).getRoomIndex();
        }

        return 0;

    }

}
