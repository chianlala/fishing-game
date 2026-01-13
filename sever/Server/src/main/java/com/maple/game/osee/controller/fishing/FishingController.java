package com.maple.game.osee.controller.fishing;

import com.google.protobuf.Message;
import com.maple.database.config.redis.RedisHelper;
import com.maple.engine.anotation.AppController;
import com.maple.engine.anotation.AppHandler;
import com.maple.engine.container.UserContainer;
import com.maple.engine.data.ServerUser;
import com.maple.game.osee.common.RedisUtil;
import com.maple.game.osee.dao.data.entity.UserStatus;
import com.maple.game.osee.entity.ItemChangeReason;
import com.maple.game.osee.entity.ItemId;
import com.maple.game.osee.entity.NewBaseGameRoom;
import com.maple.game.osee.entity.fishing.FishingGamePlayer;
import com.maple.game.osee.entity.fishing.FishingGameRoom;
import com.maple.game.osee.entity.fishing.game.FireStruct;
import com.maple.game.osee.entity.fishing.task.TaskType;
import com.maple.game.osee.manager.PlayerManager;
import com.maple.game.osee.manager.UserPropsManager;
import com.maple.game.osee.manager.UserStatusManager;
import com.maple.game.osee.manager.fishing.FishingManager;
import com.maple.game.osee.manager.fishing.FishingTaskManager;
import com.maple.game.osee.proto.OseeMessage;
import com.maple.game.osee.proto.OseeMessage.OseeMsgCode;
import com.maple.game.osee.proto.OseePublicData;
import com.maple.game.osee.proto.fishing.OseeFishingMessage;
import com.maple.game.osee.proto.fishing.OseeFishingMessage.*;
import com.maple.game.osee.util.MyRefreshFishingHelper;
import com.maple.game.osee.util.MyRefreshFishingUtil;
import com.maple.gamebase.container.GameContainer;
import com.maple.gamebase.data.BaseGamePlayer;
import com.maple.gamebase.data.BaseGameRoom;
import com.maple.gamebase.data.fishing.BaseFishingRoom;
import com.maple.network.manager.NetManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

/**
 * 捕鱼控制器
 */
@AppController
public class FishingController {

    private static Logger logger = LoggerFactory.getLogger(FishingController.class);
    @Autowired
    private FishingManager fishingManager;

    /**
     * 默认检查器
     */
    public void checker(Method taskMethod, Message req, ServerUser user, Long exp) {
        BaseGameRoom gameRoom = GameContainer.getGameRoomByPlayerId(user.getId());
        try {
            if (exp == 0) { // 根据任务方法设定的exp值，判断是否需要玩家在房间中操作
                // if (gameRoom != null) {
                // return;
                // }
                taskMethod.invoke(this, req, user);
            } else if (exp == 1) {
                if (!(gameRoom instanceof BaseFishingRoom)) {
                    FishingExitRoomResponse.Builder builder = FishingExitRoomResponse.newBuilder();
                    builder.setPlayerId(user.getId());
                    NetManager.sendMessage(OseeMsgCode.S_C_OSEE_FISHING_EXIT_ROOM_RESPONSE_VALUE, builder.build(),
                        user);
                    return;
                }
                FishingGamePlayer player = gameRoom.getGamePlayerById(user.getId());
                taskMethod.invoke(this, req, player, gameRoom);
            } else {
                taskMethod.invoke(this, req, user);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 捕鱼加入房间任务
     */
    // @AppHandler(msgCode = OseeMsgCode.C_S_OSEE_FISHING_JOIN_ROOM_REQUEST_VALUE)
    public void doFishingJoinRoomTask(FishingJoinRoomRequest req, ServerUser user) {
        fishingManager.playerJoinRoom(user, req.getRoomIndex());
    }

    /**
     * 小玛丽游戏结束任务
     */
    // @AppHandler(msgCode = OseeMsgCode.C_S_XML_END_REQUEST_VALUE, exp = 1)
    public void xmlEnd(XmlEndRequest req, FishingGamePlayer player, FishingGameRoom gameRoom) {
        XmlEndResponse.Builder builder = XmlEndResponse.newBuilder();
        builder.setUserId(req.getUserId());
        builder.setType(req.getType());
        builder.setReword(req.getReword());
        long a = new Double(RedisUtil.val("USER_XML_MONEY" + req.getUserId(), 0D)).longValue();
        PlayerManager.addItem(UserContainer.getUserById(req.getUserId()), ItemId.MONEY.getId(), a,
            ItemChangeReason.GAME_S, true);
        RedisHelper.set("USER_XML_MONEY" + req.getUserId(), "0");
        for (BaseGamePlayer gamePlayer : gameRoom.getGamePlayers()) {
            if (gamePlayer != null && gamePlayer.getUser().isOnline()) {
                NetManager.sendMessage(OseeMsgCode.S_C_XML_END_RESPONSE_VALUE, builder.build(), gamePlayer.getUser());
            }
        }

    }

    /**
     * 捕鱼玩家信息任务
     */
    // @AppHandler(msgCode = OseeMsgCode.C_S_OSEE_FISHING_PLAYER_INFO_REQUEST_VALUE, exp = 1)
    public void doFishingPlayerInfoTask(FishingPlayerInfoRequest req, FishingGamePlayer player,
        FishingGameRoom gameRoom) {
        NetManager.sendMessage(OseeMsgCode.S_C_OSEE_FISHING_PLAYER_INFO_RESPONSE_VALUE,
            fishingManager.createPlayerInfoResponse(gameRoom, player), player.getUser());
    }

    /**
     * 捕鱼玩家列表信息任务
     */
    // @AppHandler(msgCode = OseeMsgCode.C_S_OSEE_FISHING_PLAYERS_INFO_REQUEST_VALUE, exp = 1)
    public void doFishingPlayersInfoTask(FishingPlayersInfoRequest req, FishingGamePlayer player,
        FishingGameRoom gameRoom) {
        fishingManager.sendPlayersInfoResponse(gameRoom, player.getUser());
    }

    /**
     * 捕鱼退出房间任务
     */
    // @AppHandler(msgCode = OseeMsgCode.C_S_OSEE_FISHING_EXIT_ROOM_REQUEST_VALUE, exp = 1)
    public void doFishingExitRoomTask(FishingExitRoomRequest req, FishingGamePlayer user, FishingGameRoom gameRoom) {
        logger.info("doFishingExitRoomTask：移除" + user.getId());
        logger.info("doFishingExitRoomTask：移除" + gameRoom);
        fishingManager.exitFishingRoom(gameRoom, user.getUser());
    }

    // /**
    // * 捕鱼获取宝藏任务
    // */
    // @AppHandler(msgCode = OseeMsgCode.C_S_OSEE_FISHING_GET_TREASURE_REQUEST_VALUE, exp = -1)
    // public void doFishingGetTreasureTask(FishingGetTreasureRequest req, ServerUser user) {
    // treasureManager.drawTreasure(user, req.getIndex(), req.getDrawIndex());
    // }

    /**
     * 捕鱼获取房间任务列表任务
     */
    // @AppHandler(msgCode = OseeMsgCode.C_S_OSEE_FISHING_ROOM_TASK_LIST_REQUEST_VALUE, exp = -1)
    public void doFishingTaskListTask(FishingRoomTaskListRequest req, ServerUser user) {
        FishingTaskManager.sendRoomTaskListResponse(user);
    }

    /**
     * 捕鱼获取任务奖励任务
     */
    // @AppHandler(msgCode = OseeMsgCode.C_S_OSEE_FISHING_GET_ROOM_TASK_REWARD_REQUEST_VALUE, exp = -1)
    public void doFishingGetTaskRewardTask(FishingGetRoomTaskRewardRequest req, ServerUser user) {
        FishingTaskManager.getTaskReward(user, TaskType.ROOM, req.getTaskId());
    }

    @Autowired
    private UserPropsManager userPropsManager;
    @Autowired
    private UserStatusManager userStatusManager;

    private final List<Integer> bv =
        Arrays.asList(ItemId.BATTERY_VIEW_0.getId(), ItemId.BATTERY_VIEW_1.getId(), ItemId.BATTERY_VIEW_2.getId(),
            ItemId.BATTERY_VIEW_3.getId(), ItemId.BATTERY_VIEW_4.getId(), ItemId.BATTERY_VIEW_5.getId(),
            ItemId.BATTERY_VIEW_6.getId(), ItemId.BATTERY_VIEW_7.getId(), ItemId.BATTERY_VIEW_8.getId());

    private final List<Integer> wv = Arrays.asList(ItemId.WING_VIEW_0.getId(), ItemId.WING_VIEW_1.getId(),
        ItemId.WING_VIEW_2.getId(), ItemId.WING_VIEW_3.getId(), ItemId.WING_VIEW_4.getId(), ItemId.WING_VIEW_5.getId());

    // 没有时间条件限制的
    private final List<Integer> noTime = Arrays.asList(ItemId.BATTERY_VIEW_0.getId(), ItemId.WING_VIEW_0.getId(),
        ItemId.BATTERY_VIEW_1.getId(), ItemId.WING_VIEW_2.getId());

    /**
     * 捕鱼改变炮台外观任务
     */
    // @AppHandler(msgCode = OseeMsgCode.C_S_OSEE_FISHING_CHANGE_BATTERY_VIEW_REQUEST_VALUE, exp = 1)
    // @AppHandler(msgCode = OseeMsgCode.C_S_OSEE_FISHING_CHANGE_BATTERY_VIEW_REQUEST_VALUE)
    public void doFishingChangeBatteryViewTask(FishingChangeBatteryViewRequest req, ServerUser user) {

        Integer viewIndex = req.getTargetViewIndex();

        final Long userProopsNum = userPropsManager.getUserProopsNum(user, viewIndex);

        if (userProopsNum <= System.currentTimeMillis() && !noTime.contains(viewIndex)) {
            NetManager.sendHintMessageToClient("该外观已到期", user);
            return;
        }

        // 设置状态
        final FishingGameRoom room = GameContainer.getGameRoomByPlayerId(user.getId());

        if (bv.contains(viewIndex)) {

            userStatusManager.setUserStatus(new UserStatus<>(user.getId(), "batter:view", viewIndex), room);

            final OseePublicData.PlayerStatusResponse.Builder userStatusInfo =
                userStatusManager.getUserStatusInfo(user, Arrays.asList("batter:view"), room);

            if (room == null) {

                NetManager.sendMessage(OseeMessage.OseeMsgCode.S_C_TTMY_PLAYER_STATUS_RESPONSE_VALUE, userStatusInfo,
                    user);

            } else {

                MyRefreshFishingUtil.sendRoomMessage(room,
                    OseeMessage.OseeMsgCode.S_C_TTMY_PLAYER_STATUS_RESPONSE_VALUE, userStatusInfo);

            }

            return;

        } else if (wv.contains(viewIndex)) {

            userStatusManager.setUserStatus(new UserStatus<>(user.getId(), "wing:view", viewIndex), room);

            final OseePublicData.PlayerStatusResponse.Builder userStatusInfo =
                userStatusManager.getUserStatusInfo(user, Arrays.asList("wing:view"), room);

            if (room == null) {

                NetManager.sendMessage(OseeMessage.OseeMsgCode.S_C_TTMY_PLAYER_STATUS_RESPONSE_VALUE, userStatusInfo,
                    user);

            } else {

                MyRefreshFishingUtil.sendRoomMessage(room,
                    OseeMessage.OseeMsgCode.S_C_TTMY_PLAYER_STATUS_RESPONSE_VALUE, userStatusInfo);

            }

            return;

        } else {

            NetManager.sendHintMessageToClient("外观不存在", user);
            return;

        }

    }
    // /**
    // * 捕鱼改变炮台外观任务
    // */
    // @AppHandler(msgCode = OseeMsgCode.C_S_OSEE_FISHING_CHANGE_BATTERY_VIEW_REQUEST_VALUE, exp = 1)
    // public void doFishingChangeBatteryViewTask(FishingChangeBatteryViewRequest req, FishingGamePlayer player,
    // FishingGameRoom gameRoom) {
    // int viewIndex = req.getTargetViewIndex();
    // if (viewIndex >= ItemId.QSZS_BATTERY_VIEW.getId() && viewIndex <= ItemId.SWHP_BATTERY_VIEW.getId() || viewIndex
    // >= ItemId.ZLHP_BATTERY_VIEW.getId() && viewIndex <= ItemId.LBS_BATTERY_VIEW.getId()) { // 切换到自己购买的炮台外观
    // if (PlayerManager.getItemNum(player.getUser(), ItemId.getItemIdById(viewIndex)) <= 0) {
    // NetManager.sendHintMessageToClient("该炮台外观已到期", player.getUser());
    // return;
    // }
    // RedisHelper.set("USE_BATTERYVIEW:"+player.getUser().getId(),String.valueOf(viewIndex));
    // player.setViewIndex(viewIndex);
    // FishingChangeBatteryViewResponse.Builder builder = FishingChangeBatteryViewResponse.newBuilder();
    // builder.setPlayerId(player.getId());
    // builder.setViewIndex(player.getViewIndex());
    // MyRefreshFishingUtil.sendRoomMessage(gameRoom, OseeMsgCode.S_C_OSEE_FISHING_CHANGE_BATTERY_VIEW_RESPONSE_VALUE,
    // builder.build());
    // } else if (PlayerManager.getPlayerVipLevel(player.getUser()) >= viewIndex) {
    // RedisHelper.set("USE_BATTERYVIEW:"+player.getUser().getId(),String.valueOf(viewIndex));
    // player.setViewIndex(viewIndex);
    // FishingChangeBatteryViewResponse.Builder builder = FishingChangeBatteryViewResponse.newBuilder();
    // builder.setPlayerId(player.getId());
    // builder.setViewIndex(player.getViewIndex());
    // int msgCode = OseeMsgCode.S_C_OSEE_FISHING_CHANGE_BATTERY_VIEW_RESPONSE_VALUE;
    // MyRefreshFishingUtil.sendRoomMessage(gameRoom, msgCode, builder.build());
    // } else {
    // NetManager.sendHintMessageToClient("您的vip等级不足，无法更改该炮台外观", player.getUser());
    // }
    // }



    /**
     * 获取玩家是否在捕鱼房间内
     */
    @AppHandler(msgCode = OseeMsgCode.C_S_TTMY_IS_IN_FISHING_ROOM_REQUEST_VALUE, exp = -1)
    public void doIsInFishingRoomTask(IsInFishingRoomRequest request, ServerUser user) {

        IsInFishingRoomResponse.Builder builder = IsInFishingRoomResponse.newBuilder();

        BaseGamePlayer gamePlayer = GameContainer.getPlayerById(user.getId());

        if (gamePlayer == null) {

            builder.setIn(false);

        } else {

            BaseGameRoom gameRoom = GameContainer.getGameRoomByCode(gamePlayer.getRoomCode());

            if (gameRoom instanceof NewBaseGameRoom) {

                // 在捕鱼房间或者在龙晶战场捕鱼房间或者大奖赛房间内
                builder.setRoomIndex(((NewBaseGameRoom)gameRoom).getRoomIndex());
                builder.setIn(true);

            } else {

                builder.setIn(false);

            }

        }

        NetManager.sendMessage(OseeMsgCode.S_C_TTMY_IS_IN_FISHING_ROOM_RESPONSE_VALUE, builder, user);

    }

    /**
     * 获取用户使用号角剩余时间
     */
    // @AppHandler(msgCode = OseeMsgCode.C_S_GET_BOSS_BUGLE_REQUEST_VALUE, exp = -1)
    public void getBossBugle(OseeFishingMessage.GetBossBugleRequest request, ServerUser user) {
        GetBossBugleResponse.Builder builder = GetBossBugleResponse.newBuilder();
        String t = RedisHelper.get("GET_BOSS_BUGLE" + request.getUserId());
        long time = 0L;
        if (!t.isEmpty()) {
            time = Long.parseLong(t);
        } else {
            builder.setWaitTime(0L);
            NetManager.sendMessage(OseeMsgCode.S_C_GET_BOSS_BUGLE_RESPONSE_VALUE, builder, user);
        }
        long currentTimeMillis = System.currentTimeMillis() - time;
    }

    /**
     * 发送电磁炮使用
     */
    // @AppHandler(msgCode = OseeMsgCode.C_S_USE_ELE_REQUEST_VALUE, exp = 1)
    public void useEle(OseePublicData.UseEleRequest req, FishingGamePlayer player, FishingGameRoom gameRoom) {
        fishingManager.useEle(req.getFishId(), gameRoom, player.getUser());
    }

    /**
     * 发送黑洞炮使用
     */
    // @AppHandler(msgCode = OseeMsgCode.C_S_USE_BLACK_REQUEST_VALUE, exp = 1)
    public void useBlack(OseePublicData.UseBlackRequest req, FishingGamePlayer player, FishingGameRoom gameRoom) {
        fishingManager.useBlack(req.getX(), req.getY(), gameRoom, player.getUser());
    }

    /**
     * 发送鱼雷炮使用
     */
    // @AppHandler(msgCode = OseeMsgCode.C_S_USE_TRO_REQUEST_VALUE, exp = 1)
    public void useTro(OseePublicData.UseTroRequest req, FishingGamePlayer player, FishingGameRoom gameRoom) {
        fishingManager.useTro(req.getX(), req.getY(), gameRoom, player.getUser());
    }

    /**
     * 发送钻头使用
     */
    // @AppHandler(msgCode = OseeMsgCode.C_S_USE_BIT_REQUEST_VALUE, exp = 1)
    public void useBit(OseePublicData.UseBitRequest req, FishingGamePlayer player, FishingGameRoom gameRoom) {
        fishingManager.useBit(req.getAngle(), gameRoom, player.getUser());
    }

    /**
     * 获取玩家（日 或 周 或 月）积分
     */
    @AppHandler(msgCode = OseeMsgCode.C_S_OSEE_GET_PLAYER_POINT_REQUEST_VALUE, exp = -1)
    public void point(PlayerPointRequest request, ServerUser serverUser) {
        fishingManager.point(request.getRankType(), serverUser);
    }

    /**
     * 获取第五场次列表
     *
     * @param request
     * @param serverUser
     */
    // @AppHandler(msgCode = OseeMsgCode.C_S_TTMY_FISHING_ROOM_LIST_REQUEST_VALUE, exp = -1)
    public void fishingRoomList(FishingRoomListRequest request, ServerUser serverUser) {
        fishingManager.fishingRoomList(serverUser);
    }

    /**
     * 加入第五场次
     *
     * @param request
     * @param serverUser
     */
    // @AppHandler(msgCode = OseeMsgCode.C_S_TTMY_FISHING_JOIN_ROOM_BY_ROOM_CODE_REQUEST_VALUE, exp = -1)
    public void fishingJoinRoomByRoomCode(FishingJoinRoomByRoomCodeRequest request, ServerUser serverUser) {
        fishingManager.fishingJoinRoomByRoomCode(serverUser, request.getRoomCode());
    }

    /**
     * 切换座位
     *
     * @param request
     */
    // @AppHandler(msgCode = OseeMsgCode.C_S_TTMY_FISHING_CHANGE_SEAT_REQUEST_VALUE, exp = 1)
    public void fishingChangeSeat(FishingChangeSeatRequest request, FishingGamePlayer player,
        FishingGameRoom gameRoom) {
        fishingManager.changeSeat(gameRoom, player, request.getSeat());
    }


    /**
     * 同步锁定
     */
    // @AppHandler(msgCode = OseeMsgCode.C_S_FISHING_SYNC_LOCK_REQUEST_VALUE, exp = 1)
    public void syncLock(FishingSyncLockRequest request, FishingGamePlayer player, FishingGameRoom gameRoom) {
        FishingSyncLockResponse.Builder builder = FishingSyncLockResponse.newBuilder();
        builder.setFishId(request.getFishId());
        builder.setFishId1(request.getFishId1());
        builder.setFishId2(request.getFishId2());
        builder.setUserId(request.getUserId());
        fishingManager.sendSyncLockResponse(builder, gameRoom);
    }


    /**
     * 二次伤害鱼捕获鱼请求
     */
    // @AppHandler(msgCode = OseeMsgCode.C_S_FISHING_DOUBLE_KILL_FISH_REQUEST_VALUE, exp = 1)
    public void doubleKillFish(FishingDoubleKillFishRequest req, FishingGamePlayer player, FishingGameRoom gameRoom) {
        fishingManager.doubleKillFishs(gameRoom, req.getUserId(), req.getFishdsList());
    }

    /**
     * 二次伤害鱼捕获鱼结束
     */
    // @AppHandler(msgCode = OseeMsgCode.C_S_FISHING_DOUBLE_KILL_END_REQUEST_VALUE, exp = 1)
    public void doubleKillEnd(FishingDoubleKillEndResponse req, FishingGamePlayer player, FishingGameRoom gameRoom) {
        fishingManager.doubleKillEnd(gameRoom, player, req.getWinMoney(), req.getMult(), req.getFishName());
    }

}
