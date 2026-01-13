package com.maple.game.osee.listener;

import com.maple.engine.data.ServerUser;
import com.maple.game.osee.dao.data.entity.OseePlayerEntity;
import com.maple.game.osee.dao.data.mapper.OseePlayerMapper;
import com.maple.game.osee.entity.fishing.FishingGameRoom;
import com.maple.game.osee.manager.PlayerManager;
import com.maple.gamebase.container.GameContainer;
import com.maple.gamebase.data.BaseGameRoom;
import com.maple.network.event.exit.ExitEvent;
import com.maple.network.event.exit.IExitEventListener;
import com.maple.network.impl.netty.tcpsocket.NettyTcpSocketServerHandler;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 1688玩家退出监听器
 */
@Component
public class OseeExitListener implements IExitEventListener {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Resource
    OseePlayerMapper playerMapper;

    @Resource
    NettyTcpSocketServerHandler nettyTcpSocketServerHandler;

    @Override
    public void handleExitEvent(ExitEvent event) {

        ServerUser user = event.getUser();

        String exitReasonStr = event.getExitReasonStr();


        ChannelHandlerContext channel = nettyTcpSocketServerHandler.getClientChannel(user.getConnect());


        BaseGameRoom gameRoom = GameContainer.getGameRoomByPlayerId(user.getId());
        if (gameRoom instanceof FishingGameRoom) {
            // fishingManager.exitFishingRoom((FishingGameRoom) gameRoom, user);
        }

        // 退出游戏的时候，保存一下用户数据
        OseePlayerEntity playerEntity = PlayerManager.getPlayerEntity(user);
        synchronized (playerEntity) {
            playerMapper.update(playerEntity);
        }
    }

}
