package com.maple.game.osee.controller;

import com.google.protobuf.Message;
import com.maple.engine.anotation.AppController;
import com.maple.engine.anotation.AppHandler;
import com.maple.engine.data.ServerUser;
import com.maple.game.osee.entity.fishing.NewBaseFishingRoom;
import com.maple.game.osee.manager.UserStatusManager;
import com.maple.game.osee.proto.OseeMessage;
import com.maple.game.osee.proto.OseePublicData;
import com.maple.gamebase.container.GameContainer;
import com.maple.gamebase.data.BaseGameRoom;
import com.maple.network.manager.NetManager;
import org.springframework.beans.factory.annotation.Autowired;

import java.lang.reflect.Method;

/**
 * 客户端数据同步管理
 */
@AppController
public class SynchronousController {

    /**
     * 检查方法
     */
    public void checker(Method taskMethod, Message req, ServerUser user, Long exp) throws Exception {
        taskMethod.invoke(this, req, user);
    }

    @Autowired
    private UserStatusManager userStatusManager;

    /**
     * 用户状态同步
     */
    @AppHandler(msgCode = OseeMessage.OseeMsgCode.C_S_TTMY_PLAYER_STATUS_REQUEST_VALUE)
    public void playerStatus(OseePublicData.PlayerStatusRequest req, ServerUser user) {

        BaseGameRoom room = GameContainer.getGameRoomByPlayerId(user.getId());

        if (room instanceof NewBaseFishingRoom) {

            NewBaseFishingRoom newBaseFishingRoom = (NewBaseFishingRoom)room;

            NetManager.sendMessage(OseeMessage.OseeMsgCode.S_C_TTMY_PLAYER_STATUS_RESPONSE_VALUE,
                userStatusManager.getUserStatusInfo(user, (int)req.getIndex(), newBaseFishingRoom), user);

        }

    }

}
