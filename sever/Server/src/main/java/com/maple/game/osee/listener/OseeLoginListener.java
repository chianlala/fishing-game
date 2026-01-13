package com.maple.game.osee.listener;

import com.maple.common.login.event.login.ILoginEventListener;
import com.maple.common.login.event.login.LoginEvent;
import com.maple.database.config.redis.RedisHelper;
import com.maple.database.data.entity.UserEntity;
import com.maple.database.data.mapper.UserMapper;
import com.maple.engine.data.ServerUser;
import com.maple.game.osee.common.RedisUtil;
import com.maple.game.osee.dao.data.entity.OseePlayerEntity;
import com.maple.game.osee.dao.data.mapper.OseePlayerMapper;
import com.maple.game.osee.dao.log.entity.AppRewardLogEntity;
import com.maple.game.osee.dao.log.entity.AppRewardRankEntity;
import com.maple.game.osee.dao.log.mapper.AppRewardRankMapper;
import com.maple.game.osee.manager.PlayerManager;
import com.maple.game.osee.manager.fishing.FishingChallengeManager;
import com.maple.game.osee.manager.fishing.FishingGrandPrixManager;
import com.maple.game.osee.manager.fishing.FishingManager;
import com.maple.game.osee.manager.lobby.CommonLobbyManager;
import com.maple.game.osee.proto.OseeMessage;
import com.maple.game.osee.proto.TtmyFishingGrandPrixMessage;
import com.maple.gamebase.container.GameContainer;
import com.maple.gamebase.data.BaseGamePlayer;
import com.maple.network.impl.netty.tcpsocket.NettyTcpSocketServerHandler;
import com.maple.network.manager.NetManager;
import com.maple.network.util.MessageUtil;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Date;

/**
 * 1688玩家登录监听器
 */
@Component
@Slf4j
public class OseeLoginListener implements ILoginEventListener {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private OseePlayerMapper playerMapper;

    @Autowired
    private CommonLobbyManager commonLobbyManager;

    @Autowired
    private FishingManager fishingManager;

    @Autowired
    private FishingChallengeManager fishingChallengeManager;

    @Autowired
    private FishingGrandPrixManager fishingGrandPrixManager;

    @Autowired
    UserMapper userMapper;

    @Autowired
    AppRewardRankMapper appRewardRankMapper;

    @Override
    public void handleLoginEvent(LoginEvent event) {

        ServerUser user = event.getUser();

        String tcpIp = MessageUtil.getTcpIp(user);

        ChannelHandlerContext channel = nettyTcpSocketServerHandler.getClientChannel(user.getConnect());

        handleFirstWeekLogin(user); // 处理每周第一次登录

        boolean b = false;

        OseePlayerEntity entity = PlayerManager.getPlayerEntity(user, true);

        if (entity.getLevel() == 1 && entity.getMoney() == 0 && entity.getBatteryLevel() == 1) {
            b = true;
        }

        PlayerManager.sendPlayerLevelResponse(user);
        if (b) {
            PlayerManager.sendPlayerMoneyResponse1(user);
        } else {
            PlayerManager.sendPlayerMoneyResponse(user, -1);
        }

        PlayerManager.sendVipLevelResponse(user);
        PlayerManager.sendPlayerBatteryLevelResponse(user);

        // 发送月卡每日奖励
        commonLobbyManager.sendDailyMonthCardRewards(user);
        // 发送VIP每日奖励
        // commonLobbyManager.sendDailyVipRewards(user);
        // 检查vip的金币补足情况
        commonLobbyManager.checkVipMoneyEnough(user);

        entity.setLastSignInTime(new Date());

        // 添加到：待更新的集合里
        PlayerManager.updateEntities.add(entity);

        // 检查房间是否需要重连
        BaseGamePlayer gamePlayer = GameContainer.getPlayerById(user.getId());

        if (gamePlayer != null) {

            // log.info("重连，玩家 id：{}，旧的通道：{}，是否存在：{}，新的通道：{}，是否存在：{}", user.getId(), gamePlayer.getUser().getConnect(),
            // channelExist(gamePlayer.getUser().getConnect()), user.getConnect(), channelExist(user.getConnect()));

            gamePlayer.setUser(user);

            // BaseGameRoom gameRoom = GameContainer.getGameRoomByCode(gamePlayer.getRoomCode());
            // if (gameRoom != null) {
            // if (gameRoom instanceof FightTenChallengeRoom) { // 房间是拼十挑战赛房间
            // tenChallengeManager.reconnect((FightTenChallengeRoom)gameRoom, (FightTenChallengePlayer)gamePlayer);
            // } else if (gameRoom instanceof FightTenRoom) { // 房间是拼十房间就要发送重连信息
            // fightTenManager.reconnect((FightTenRoom)gameRoom, (FightTenPlayer)gamePlayer);
            // } else if (gameRoom instanceof GobangGameRoom) { // 五子棋房间重连
            // gobangManager.reconnect((GobangGameRoom)gameRoom, user);
            // } else if (gameRoom instanceof TwoEightRoom) { // 房间是二八杠房间就要发送重连
            // twoEightManager.reconnect((TwoEightRoom)gameRoom, (TwoEightPlayer)gamePlayer);
            // } else if (gameRoom instanceof FishingChallengeRoom) { // 捕鱼挑战赛
            // fishingChallengeManager
            // .reconnect((FishingChallengeRoom)gameRoom, (FishingChallengePlayer)gamePlayer);
            // } else if (gameRoom instanceof FishingGameRoom) { // 普通捕鱼
            // fishingManager.reconnect((FishingGameRoom)gameRoom, (FishingGamePlayer)gamePlayer);
            // } else if (gameRoom instanceof FishingGrandPrixRoom) { //大奖赛
            // fishingGrandPrixManager
            // .reconnect((FishingGrandPrixRoom)gameRoom, (FishingGrandPrixPlayer)gamePlayer);
            // }
            // }

        }
    }

    /**
     * 处理每周第一次登录
     */
    private void handleFirstWeekLogin(ServerUser user) {

        String a = RedisHelper.get("FIRST_WEEK_LOGIN:" + user.getId());

        if (!"0".equals(a)) {
            return;
        }

        String rankIds = RedisUtil.get("LAST_WEEK_GRANDPRIX");
        String[] r = rankIds.split(",");
        int index = 0;
        TtmyFishingGrandPrixMessage.FirstWeekLoginResponse.Builder builder =
                TtmyFishingGrandPrixMessage.FirstWeekLoginResponse.newBuilder();
        TtmyFishingGrandPrixMessage.FirstWeekLoginMessage.Builder builder1 =
                TtmyFishingGrandPrixMessage.FirstWeekLoginMessage.newBuilder();
        for (String rankId : r) {

            if (!rankId.isEmpty()) {

                long playerId = Long.parseLong(rankId);

                builder1.setWeekPoint(Double.valueOf(RedisUtil.val("GRANDPRIX_LAST_WEEK" + playerId, "0")).intValue());
                builder1.setPlayerId(playerId);
                builder1.setRank(++index);
                UserEntity userEntity = userMapper.findById(playerId);
                builder1.setName(userEntity.getNickname());
                builder1.setHeadIndex(userEntity.getHeadIndex());
                builder1.setHeadUrl(userEntity.getHeadUrl());
                AppRewardRankEntity rewardRank = appRewardRankMapper.findReward(2, index);

                if (rewardRank != null) {

                    AppRewardLogEntity reward = rewardRank.getReward();

                    if (reward.getGold() != 0) {
                        builder1.setItemId(1);
                        builder1.setItemNum(reward.getGold());
                    } else {
                        if (builder1.getItemId() == 0) {
                            builder1.setItemId(0);
                            builder1.setItemNum(0);
                        }
                    }

                    if (reward.getDiamond() != 0) {
                        builder1.setItemId(4);
                        builder1.setItemNum(reward.getDiamond());
                    } else {
                        if (builder1.getItemId() == 0) {
                            builder1.setItemId(0);
                            builder1.setItemNum(0);
                        }
                    }

                    if (reward.getLowerBall() != 0) {
                        builder1.setItemId(5);
                        builder1.setItemNum(reward.getLowerBall());
                    } else {
                        if (builder1.getItemId() == 0) {
                            builder1.setItemId(0);
                            builder1.setItemNum(0);
                        }
                    }

                    if (reward.getMiddleBall() != 0) {
                        builder1.setItemId(6);
                        builder1.setItemNum(reward.getMiddleBall());
                    } else {
                        if (builder1.getItemId() == 0) {
                            builder1.setItemId(0);
                            builder1.setItemNum(0);
                        }
                    }

                    if (reward.getHighBall() != 0) {
                        builder1.setItemId(7);
                        builder1.setItemNum(reward.getHighBall());
                    } else {
                        if (builder1.getItemId() == 0) {
                            builder1.setItemId(0);
                            builder1.setItemNum(0);
                        }
                    }

                    if (reward.getSkillLock() != 0) {
                        builder1.setItemId(8);
                        builder1.setItemNum(reward.getSkillLock());
                    } else {
                        if (builder1.getItemId() == 0) {
                            builder1.setItemId(0);
                            builder1.setItemNum(0);
                        }
                    }

                    if (reward.getSkillFrozen() != 0) {
                        builder1.setItemId(9);
                        builder1.setItemNum(reward.getSkillFrozen());
                    } else {
                        if (builder1.getItemId() == 0) {
                            builder1.setItemId(0);
                            builder1.setItemNum(0);
                        }
                    }

                    if (reward.getSkillFast() != 0) {
                        builder1.setItemId(10);
                        builder1.setItemNum(reward.getSkillFast());
                    } else {
                        if (builder1.getItemId() == 0) {
                            builder1.setItemId(0);
                            builder1.setItemNum(0);
                        }
                    }

                    if (reward.getSkillCrit() != 0) {
                        builder1.setItemId(11);
                        builder1.setItemNum(reward.getSkillCrit());
                    } else {
                        if (builder1.getItemId() == 0) {
                            builder1.setItemId(0);
                            builder1.setItemNum(0);
                        }
                    }

                    if (reward.getBossBugle() != 0) {
                        builder1.setItemId(13);
                        builder1.setItemNum(reward.getBossBugle());
                    } else {
                        if (builder1.getItemId() == 0) {
                            builder1.setItemId(0);
                            builder1.setItemNum(0);
                        }
                    }

                } else {

                    builder1.setItemId(0);
                    builder1.setItemNum(0);

                }

                builder.addFirstWeekLogin(builder1);

            }

            NetManager.sendMessage(OseeMessage.OseeMsgCode.S_C__FIRST_WEEK_LOGIN_RESPONSE_VALUE, builder, user);

        }

        RedisHelper.set("FIRST_WEEK_LOGIN:" + user.getId(), "1");

    }

    @Resource
    RedissonClient redissonClient;

    public static NettyTcpSocketServerHandler nettyTcpSocketServerHandler;

    @Resource
    public void setNettyTcpSocketServerHandler(NettyTcpSocketServerHandler nettyTcpSocketServerHandler) {
        OseeLoginListener.nettyTcpSocketServerHandler = nettyTcpSocketServerHandler;
    }

    /**
     * channel是否存在，true 存在
     */
    public static boolean channelExist(String channelId) {
        return nettyTcpSocketServerHandler.getClientChannel(channelId) != null;
    }

}
