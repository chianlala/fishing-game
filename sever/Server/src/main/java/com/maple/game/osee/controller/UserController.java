package com.maple.game.osee.controller;

import com.google.protobuf.Message;
import com.maple.engine.anotation.AppController;
import com.maple.engine.anotation.AppHandler;
import com.maple.engine.data.ServerUser;
import com.maple.game.osee.entity.NewBaseGameRoom;
import com.maple.game.osee.manager.UserStatusManager;
import com.maple.game.osee.proto.OseeMessage;
import com.maple.game.osee.proto.OseePublicData;
import com.maple.gamebase.container.GameContainer;
import com.maple.gamebase.data.BaseGamePlayer;
import com.maple.gamebase.data.BaseGameRoom;
import com.maple.network.manager.NetManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.lang.reflect.Method;

/**
 * 用户管理
 */
@AppController
public class UserController {

    /**
     * 检查方法
     */
    public void checker(Method taskMethod, Message req, ServerUser user, Long exp) throws Exception {
        taskMethod.invoke(this, req, user);
    }

    private Logger logger = LoggerFactory.getLogger(getClass());
    @Autowired
    private UserStatusManager userStatusManager;

    /**
     * 用户房间状态同步
     */
    @AppHandler(msgCode = OseeMessage.OseeMsgCode.C_S_TTMY_PLAYER_ROOM_STATUS_REQUEST_VALUE)
    public void playerStatus(OseePublicData.PlayerRoomStatusRequest req, ServerUser user) {

        // String uuid = IdUtil.fastSimpleUUID();

        // String tcpIp = MessageUtil.getTcpIp(user);

        // String messageTemp = "【{}】，uid【{}】【{}】【{}】【{}】【C_S_TTMY_PLAYER_ROOM_STATUS_REQUEST】";

        // MessageUtil.pushMessage(StrUtil.format(messageTemp, "收到", user.getId(), tcpIp, DateUtil.now(), uuid));

        if (user.getEntity() == null) { // 如果当前 socket未登录，则不回此消息

            OseePublicData.PlayerRoomStatusResponse.Builder builder =
                OseePublicData.PlayerRoomStatusResponse.newBuilder();

            builder.setIndex(-1);
            builder.setDatetime(System.currentTimeMillis());

            NetManager.sendMessage(OseeMessage.OseeMsgCode.S_C_TTMY_PLAYER_ROOM_STATUS_RESPONSE_VALUE, builder, user);

            return;

        }

        OseePublicData.PlayerRoomStatusResponse.Builder builder = OseePublicData.PlayerRoomStatusResponse.newBuilder();

        BaseGamePlayer gamePlayer = GameContainer.getPlayerById(user.getId());

        builder.setIndex(0);
        builder.setDatetime(System.currentTimeMillis());

        if (gamePlayer != null) {

            BaseGameRoom gameRoom = GameContainer.getGameRoomByCode(gamePlayer.getRoomCode());

            if (gameRoom instanceof NewBaseGameRoom) {
                // if (gameRoom instanceof NewBaseFishingRoom) { // 捕鱼挑战赛
                builder.setIndex(((NewBaseGameRoom)gameRoom).getRoomIndex());
                // }
            }

        }

        // logger.debug("用户[{}]状态:[{}]", user.getUsername(), builder.getIndex());

        NetManager.sendMessage(OseeMessage.OseeMsgCode.S_C_TTMY_PLAYER_ROOM_STATUS_RESPONSE_VALUE, builder, user);

        // MessageUtil.pushMessage(StrUtil.format(messageTemp, "响应", user.getId(), tcpIp, DateUtil.now(), uuid));

    }
}
