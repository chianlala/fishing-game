package com.maple.game.osee.controller.fishing;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.google.protobuf.Message;
import com.maple.database.config.redis.RedisHelper;
import com.maple.engine.anotation.AppController;
import com.maple.engine.anotation.AppHandler;
import com.maple.engine.container.DataContainer;
import com.maple.engine.container.UserContainer;
import com.maple.engine.data.ServerUser;
import com.maple.game.osee.common.RedisUtil;
import com.maple.game.osee.dao.data.entity.OseePlayerEntity;
import com.maple.game.osee.dao.data.entity.UserStatus;
import com.maple.game.osee.entity.ItemChangeReason;
import com.maple.game.osee.entity.ItemId;
import com.maple.game.osee.entity.fishing.FishingGamePlayer;
import com.maple.game.osee.entity.fishing.challenge.FishingChallengePlayer;
import com.maple.game.osee.entity.fishing.challenge.FishingChallengeRoom;
import com.maple.game.osee.entity.fishing.csv.file.FishRefreshRule;
import com.maple.game.osee.entity.fishing.game.FireStruct;
import com.maple.game.osee.manager.PlayerManager;
import com.maple.game.osee.manager.UserPropsManager;
import com.maple.game.osee.manager.UserStatusManager;
import com.maple.game.osee.manager.fishing.FishingChallengeManager;
import com.maple.game.osee.manager.fishing.FishingManager;
import com.maple.game.osee.proto.HwLoginMessage;
import com.maple.game.osee.proto.OseeMessage;
import com.maple.game.osee.proto.OseePublicData;
import com.maple.game.osee.proto.TtmyFishingRecordProto;
import com.maple.game.osee.proto.fishing.FishBossMessage;
import com.maple.game.osee.proto.fishing.OseeFishingMessage;
import com.maple.game.osee.proto.fishing.TtmyFishingChallengeMessage;
import com.maple.game.osee.util.*;
import com.maple.gamebase.container.GameContainer;
import com.maple.gamebase.data.BaseGamePlayer;
import com.maple.gamebase.data.BaseGameRoom;
import com.maple.network.manager.NetManager;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Resource;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static com.maple.game.osee.proto.OseeMessage.OseeMsgCode.S_C_TTMY_FISHING_CHALLENGE_REPLY_FIGHT_FISH_RESPONSE_VALUE;

/**
 * 捕鱼挑战赛控制层
 */
@AppController
@Slf4j
public class FishingChallengeController {

    @Autowired
    private FishingChallengeManager challengeManager;

    @Autowired
    private FishingManager fishingManager;

    private static RedissonClient redissonClient;

    @Resource
    public void setRedissonClient(RedissonClient redissonClient) {
        FishingChallengeController.redissonClient = redissonClient;
    }

    /**
     * 默认检查器
     */
    public void checker(Method taskMethod, Message req, ServerUser user, Long exp) {

        try {

            BaseGameRoom gameRoom = GameContainer.getGameRoomByPlayerId(user.getId());

            // 根据任务方法设定的exp值，判断是否需要玩家在房间中操作
            if (exp == 0) { // 不在房间内的操作，同时玩家不在任何房间内

                if (gameRoom != null) {
                    return;
                }

                taskMethod.invoke(this, req, user);

            } else if (exp == 1) { // 房间内的操作
                if (gameRoom instanceof FishingChallengeRoom) { // 龙晶场

                    FishingChallengePlayer player = gameRoom.getGamePlayerById(user.getId());

                    player.setUser(user);

                    taskMethod.invoke(this, req, player, gameRoom);

                }
            } else {

                taskMethod.invoke(this, req, user);

            }

        } catch (Exception e) {

            e.printStackTrace();

        }

    }

    /**
     * 召唤一条指定的鱼
     */
    @AppHandler(msgCode = OseeMessage.OseeMsgCode.C_S_TEST_SUMMON_FISH_REQUEST_VALUE, exp = -1)
    public void summonFish(FishBossMessage.FishInfo request, ServerUser user) {
        MyRefreshFishingHelper.summonTestFish(request, user);
    }

    /**
     * 挑战赛房间列表
     */
    @AppHandler(msgCode = OseeMessage.OseeMsgCode.C_S_TTMY_FISHING_CHALLENGE_ROOM_LIST_REQUEST_VALUE, exp = -1)
    public void roomList(TtmyFishingChallengeMessage.FishingChallengeRoomListRequest request, ServerUser user) {
        challengeManager.roomList(user, request);
    }

    /**
     * 创建房间-获取房间号
     */
    @AppHandler(msgCode = OseeMessage.OseeMsgCode.C_S_TTMY_FISHING_CHALLENGE_CREATE_ROOM_GET_ROOM_CODE_REQUEST_VALUE,
            exp = 0)
    public void createRoomGetRoomCode(
            TtmyFishingChallengeMessage.TtmyFishingChallengeCreateRoomGetRoomCodeRequest request, ServerUser user) {
        challengeManager.createRoomGetRoomCode(user, request);
    }

    /**
     * 创建房间
     */
    @AppHandler(msgCode = OseeMessage.OseeMsgCode.C_S_TTMY_FISHING_CHALLENGE_CREATE_ROOM_REQUEST_VALUE, exp = 0)
    public void createRoom(TtmyFishingChallengeMessage.FishingChallengeCreateRoomRequest request, ServerUser user) {
        FishingChallengeManager.createRoom(user, request, true);
    }

    /**
     * 加入房间
     */
    // @AppHandler(msgCode = OseeMessage.OseeMsgCode.C_S_TTMY_FISHING_CHALLENGE_JOIN_ROOM_REQUEST_VALUE, exp = 0)
    @AppHandler(msgCode = OseeMessage.OseeMsgCode.C_S_TTMY_FISHING_CHALLENGE_LIST_JOIN_ROOM_REQUEST_VALUE, exp = -1)
    public void listJoinRoom(TtmyFishingChallengeMessage.FishingChallengeListJoinRoomRequest request, ServerUser user) {

        challengeManager.listJoinRoom(user, request.getSessionIdList());

    }

    /**
     * 加入房间
     */
    // @AppHandler(msgCode = OseeMessage.OseeMsgCode.C_S_TTMY_FISHING_CHALLENGE_JOIN_ROOM_REQUEST_VALUE, exp = 0)
    @AppHandler(msgCode = OseeMessage.OseeMsgCode.C_S_TTMY_FISHING_CHALLENGE_JOIN_ROOM_REQUEST_VALUE, exp = -1)
    public void joinRoom(TtmyFishingChallengeMessage.FishingChallengeJoinRoomRequest request, ServerUser user) {

        challengeManager.joinRoom(user, request.getRoomCode(), request.getRoomPassword(), request.getRoomType());

    }

    /**
     * 退出房间
     */
    @AppHandler(msgCode = OseeMessage.OseeMsgCode.C_S_TTMY_FISHING_CHALLENGE_EXIT_ROOM_REQUEST_VALUE, exp = 1)
    public void exitRoom(TtmyFishingChallengeMessage.FishingChallengeExitRoomRequest request,
                         FishingChallengePlayer player, FishingChallengeRoom room) {

        // challengeManager.exitRoom(player, room);

        FishingChallengeUtil.exitRoom(player, room);

    }


    // /**
    // * 捕鱼改变炮台外观任务
    // */
    // @AppHandler(msgCode = OseeMessage.OseeMsgCode.C_S_TTMY_FISHING_CHALLENGE_CHANGE_BATTERY_VIEW_REQUEST_VALUE, exp =
    // 1)
    // public void changeBatteryView(TtmyFishingChallengeMessage.FishingChallengeChangeBatteryViewRequest request,
    // FishingChallengePlayer player, FishingChallengeRoom gameRoom) {
    // challengeManager.changeBatteryView(gameRoom, player, request.getTargetViewIndex());
    // }
    @Autowired
    private UserPropsManager userPropsManager;
    @Autowired
    private UserStatusManager userStatusManager;

    private final List<Integer> bv = Arrays.asList(ItemId.BATTERY_VIEW_0.getId(), ItemId.BATTERY_VIEW_1.getId(),
            ItemId.BATTERY_VIEW_2.getId(), ItemId.BATTERY_VIEW_3.getId(), ItemId.BATTERY_VIEW_4.getId(),
            ItemId.BATTERY_VIEW_5.getId(), ItemId.BATTERY_VIEW_6.getId(), ItemId.BATTERY_VIEW_7.getId(),
            ItemId.BATTERY_VIEW_8.getId(), ItemId.BATTERY_VIEW_9.getId(), ItemId.BATTERY_VIEW_10.getId(),
            ItemId.BATTERY_VIEW_11.getId(), ItemId.BATTERY_VIEW_12.getId(), ItemId.BATTERY_VIEW_13.getId(),
            ItemId.BATTERY_VIEW_14.getId(), ItemId.BATTERY_VIEW_15.getId(), ItemId.BATTERY_VIEW_16.getId());

    private final List<Integer> wv = Arrays.asList(ItemId.WING_VIEW_0.getId(), ItemId.WING_VIEW_1.getId(),
            ItemId.WING_VIEW_2.getId(), ItemId.WING_VIEW_3.getId(), ItemId.WING_VIEW_4.getId(), ItemId.WING_VIEW_5.getId());

    // 没有时间条件限制的物品
    private final List<Integer> noTime =
            Arrays.asList(ItemId.BATTERY_VIEW_0.getId(), ItemId.WING_VIEW_0.getId(), ItemId.BATTERY_VIEW_1.getId(),
                    ItemId.BATTERY_VIEW_2.getId(), ItemId.BATTERY_VIEW_3.getId(), ItemId.BATTERY_VIEW_4.getId(),
                    ItemId.BATTERY_VIEW_9.getId(), ItemId.BATTERY_VIEW_10.getId(), ItemId.BATTERY_VIEW_11.getId(),
                    ItemId.BATTERY_VIEW_14.getId(), ItemId.BATTERY_VIEW_15.getId(), ItemId.BATTERY_VIEW_16.getId());

    @AppHandler(msgCode = OseeMessage.OseeMsgCode.C_S_OSEE_FISHING_CHANGE_BATTERY_VIEW_REQUEST_VALUE, exp = -1)
    public void doFishingChangeBatteryViewTask(OseeFishingMessage.FishingChangeBatteryViewRequest req,
                                               ServerUser user) {

        Integer viewIndex = req.getTargetViewIndex();

        final Long userProopsNum = userPropsManager.getUserProopsNum(user, viewIndex);

        if (userProopsNum <= System.currentTimeMillis() && !noTime.contains(viewIndex)) {
            NetManager.sendHintMessageToClient("该外观已到期", user);
            return;
        }

        // 设置状态
        final FishingChallengeRoom room = GameContainer.getGameRoomByPlayerId(user.getId());

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

    /**
     * 捕鱼改变炮台等级任务
     */
    @AppHandler(msgCode = OseeMessage.OseeMsgCode.C_S_TTMY_FISHING_CHALLENGE_CHANGE_BATTERY_LEVEL_REQUEST_VALUE,
            exp = 1)
    public void changeBatteryLevel(TtmyFishingChallengeMessage.FishingChallengeChangeBatteryLevelRequest request,
                                   FishingChallengePlayer player, FishingChallengeRoom gameRoom) {

        FishingChallengeManager.changeBatteryLevel(gameRoom, player, request.getTargetLevel(), true);

    }

    /**
     * 玩家扣金币
     */
    @AppHandler(msgCode = OseeMessage.OseeMsgCode.C_S_TTMY_FISHING_CHALLENGE_DEMONY_REQUEST_VALUE, exp = 1)
    public void deMoney(TtmyFishingChallengeMessage.FishingChallengeDeMoneyRequest request,
                        FishingChallengePlayer player, FishingChallengeRoom room) {

        if (player.getUser().getId() == 0 || request.getMoney() >= 0) {
            return;
        }

        OseePlayerEntity playerEntity = PlayerManager.getPlayerEntity(player.getUser());

        synchronized (playerEntity) {

            if (PlayerManager.checkItem(player.getUser(), FishingChallengeFightFishUtil.USE_MONEY,
                    -request.getMoney())) {

                // 扣除：钱
                player.useBattery(request.getMoney(), room, redissonClient);

                long checkMoney = FishingChallengeFightFishUtil.getJczd1(player.getMoney(), 1, player.getId());

                if (checkMoney == 0) { // 如果：破产了

                    // 破产通用处理
                    FishingChallengeFightFishUtil.bankruptcyCommonHandler(player, room, playerEntity, false);

                }

                // 处理：消耗和产出
                FishingChallengeFightFishUtil.handleUsedAndProduceGold(room, request.getMoldleID(), 0L,
                        -request.getMoney(), false);

            }

            TtmyFishingChallengeMessage.FishingChallengeReplyFightFishResponse.Builder builder =
                    TtmyFishingChallengeMessage.FishingChallengeReplyFightFishResponse.newBuilder();

            builder.setRestMoney(player.getMoney());

            NetManager.sendMessage(S_C_TTMY_FISHING_CHALLENGE_REPLY_FIGHT_FISH_RESPONSE_VALUE, builder,
                    player.getUser());

        }

    }

    /**
     * 玩家发射子弹
     */
    @AppHandler(msgCode = OseeMessage.OseeMsgCode.C_S_TTMY_FISHING_CHALLENGE_FIRE_REQUEST_VALUE, exp = 1)
    public void playerFire(TtmyFishingChallengeMessage.FishingChallengeFireRequest request,
                           FishingChallengePlayer player, FishingChallengeRoom gameRoom) {

        if (player.getUser().getId() == 0) {
            return;
        }

        FireStruct fire = new FireStruct();
        fire.setId(request.getFireId() > 0 ? request.getFireId() : gameRoom.getNextId());
        fire.setFishId(request.getFishId());
        fire.setAngle(request.getAngle());
        challengeManager.playerFire(gameRoom, player, fire);

    }

    /**
     * 玩家打中鱼请求
     */
    @AppHandler(msgCode = OseeMessage.OseeMsgCode.C_S_TTMY_FISHING_CHALLENGE_FIGHT_FISH_REQUEST_VALUE, exp = 1)
    public void playerFightFish(TtmyFishingChallengeMessage.FishingChallengeFightFishRequest request,
                                FishingChallengePlayer player, FishingChallengeRoom gameRoom) {

        FishingChallengeFightFishUtil.playerFightFish(gameRoom, player, request.getFireId(),
                CollUtil.newArrayList(request.getFishId()), request.getReplyFightFlag(), null, null, true, null);

    }

    /**
     * 钻头打中鱼请求
     */
    @AppHandler(msgCode = OseeMessage.OseeMsgCode.C_S_CHALLENGE_BIT_FIGHT_FISH_REQUEST_VALUE, exp = 1)
    public void bitFightFish(HwLoginMessage.ChallengeBitFightFishRequest request, FishingChallengePlayer player,
                             FishingChallengeRoom gameRoom) {

    }

    /**
     * 同步房间内的鱼
     */
    @AppHandler(msgCode = OseeMessage.OseeMsgCode.C_S_TTMY_FISHING_CHALLENGE_SYNCHRONISE_REQUEST_VALUE, exp = 1)
    public void fishSynchronise(TtmyFishingChallengeMessage.FishingChallengeSynchroniseRequest request,
                                FishingChallengePlayer player, FishingChallengeRoom gameRoom) {

        FishingChallengeManager.sendSynchroniseResponse(gameRoom, player);

    }

    /**
     * 捕鱼重新激活
     */
    @AppHandler(msgCode = OseeMessage.OseeMsgCode.C_S_TTMY_FISHING_CHALLENGE_REACTIVE_REQUEST_VALUE, exp = 1)
    public void fishReactive(TtmyFishingChallengeMessage.FishingChallengeReactiveRequest request,
                             FishingChallengePlayer player, FishingChallengeRoom gameRoom) {

        FishingChallengeManager.sendReactiveMessage(gameRoom, player);

    }

    /**
     * 玩家使用技能
     */
    @AppHandler(msgCode = OseeMessage.OseeMsgCode.C_S_TTMY_FISHING_CHALLENGE_USE_SKILL_REQUEST_VALUE, exp = 1)
    public void useSkill(TtmyFishingChallengeMessage.FishingChallengeUseSkillRequest request,
                         FishingChallengePlayer player, FishingChallengeRoom gameRoom) {

        FishingChallengeManager.useSkill(gameRoom, player, request.getSkillId(), request.getRouteId(),
                request.getFishIdsList());

    }

    /**
     * 鱼使用冰冻技能
     */
    @AppHandler(msgCode = OseeMessage.OseeMsgCode.C_S_FISHING_FISH_USE_FREEZE_REQUEST_VALUE, exp = 1)
    public void fishUseFreeze(TtmyFishingChallengeMessage.FishingFishUseFreezeRequest request,
                              FishingChallengePlayer player, FishingChallengeRoom room) {

        challengeManager.fishUseFreeze(room, player, request);

    }

    /**
     * 捕捉到特殊鱼
     */
    @AppHandler(msgCode = OseeMessage.OseeMsgCode.C_S_TTMY_FISHING_CHALLENGE_CATCH_SPECIAL_FISH_REQUEST_VALUE, exp = 1)
    public void catchSpecialFish(TtmyFishingChallengeMessage.FishingChallengeCatchSpecialFishRequest request,
                                 FishingChallengePlayer player, FishingChallengeRoom room) {
        challengeManager.catchSpecialFish(room, request.getPlayerId(), request.getFishIdsList(),
                request.getSpecialFishId());
    }

    /**
     * 特殊鱼倍数获取
     */
    @AppHandler(msgCode = OseeMessage.OseeMsgCode.C_S_FISHING_CHALLENGE_ROOM_FISH_MULTIPLE_REQUEST_VALUE, exp = 1)
    public void doCatchSpecialFishTask(TtmyFishingChallengeMessage.FishingChallengeRoomFishMultipleRequest request,
                                       FishingChallengePlayer player, FishingChallengeRoom room) {

        challengeManager.sendRoomFishMult(room, player);

    }

    /**
     * 使用boss号角
     */
    @AppHandler(msgCode = OseeMessage.OseeMsgCode.C_S_TTMY_FISHING_CHALLENGE_USE_BOSS_BUGLE_REQUEST_VALUE, exp = 1)
    public void useBossBugle(TtmyFishingChallengeMessage.FishingChallengeUseBossBugleRequest request,
                             FishingChallengePlayer player, FishingChallengeRoom gameRoom) {

        MyRefreshFishingHelper.useBossBugle(gameRoom, player, request.getType());

    }

    /**
     * 捕鱼房间消息同步请求
     */
    @AppHandler(msgCode = OseeMessage.OseeMsgCode.C_S_TTMY_USE_FISHING_ROOM_MESSAGE_REQUEST_VALUE, exp = 1)
    public void fishingRoomMessage(TtmyFishingChallengeMessage.fishingRoomMessageRequest request,
                                   FishingChallengePlayer player, FishingChallengeRoom gameRoom) {
        challengeManager.fishingRoomMessage(request, player, gameRoom);
    }

    /**
     * VIP换座
     */
    @AppHandler(msgCode = OseeMessage.OseeMsgCode.C_S_TTMY_FISHING_CHALLENGE_CHANGE_SEAT_REQUEST_VALUE, exp = 1)
    public void changeSeat(TtmyFishingChallengeMessage.FishingChallengeChangeSeatRequest request,
                           FishingChallengePlayer player, FishingChallengeRoom gameRoom) {
        challengeManager.changeSeat(gameRoom, player, request.getSeat());
    }

    /**
     * 房间召唤鱼潮
     *
     * @param request
     * @param player
     * @param gameRoom
     */
    @AppHandler(msgCode = OseeMessage.OseeMsgCode.C_S_TTMY_ROOM_CALL_TIDE_OF_FISH_REQUEST_VALUE, exp = 1)
    public void roomCallTideOfFish(TtmyFishingChallengeMessage.roomCallTideOfFish request,
                                   FishingChallengePlayer player, FishingChallengeRoom gameRoom) {
        // 随机一个鱼潮为 当前时间挫
        List<Long> longList = MyRefreshFishingHelper.tideOfFishTimeMap.get(gameRoom.getCode());
        longList.set(RandomUtil.getRandom(0, longList.size() - 1), System.currentTimeMillis());
        MyRefreshFishingHelper.tideOfFishTimeMap.put(gameRoom.getCode(), longList);
    }

    /**
     * 使用鱼雷任务
     */
    @AppHandler(msgCode = OseeMessage.OseeMsgCode.C_S_TTMY_FISHING_Challenge_USE_TORPEDO_REQUEST_VALUE, exp = -1)
    public void doUseTorpedoTask(TtmyFishingChallengeMessage.FishingChallengeUseTorpedoRequest request,
                                 ServerUser user) {
        challengeManager.useTorpedo(user, request.getFishingChallengeUseTorpedoList());
    }

    /**
     * 发送电磁炮使用
     */
    @AppHandler(msgCode = OseeMessage.OseeMsgCode.C_S_CHALLENGE_USE_ELE_REQUEST_VALUE, exp = 1)
    public void useEle(OseePublicData.UseEleRequest req, FishingGamePlayer player, FishingChallengeRoom gameRoom) {
        challengeManager.useEle(req.getFishId(), gameRoom, player.getUser());
    }

    /**
     * 发送黑洞炮使用
     */
    @AppHandler(msgCode = OseeMessage.OseeMsgCode.C_S_CHALLENGE_USE_BLACK_REQUEST_VALUE, exp = 1)
    public void useBlack(OseePublicData.UseBlackRequest req, FishingGamePlayer player, FishingChallengeRoom gameRoom) {
        challengeManager.useBlack(req.getX(), req.getY(), gameRoom, player.getUser());
    }

    /**
     * 发送鱼雷炮使用
     */
    @AppHandler(msgCode = OseeMessage.OseeMsgCode.C_S_CHALLENGE_USE_TRO_REQUEST_VALUE, exp = 1)
    public void useTro(OseePublicData.UseTroRequest req, FishingGamePlayer player, FishingChallengeRoom gameRoom) {
        challengeManager.useTro(req.getX(), req.getY(), gameRoom, player.getUser());
    }

    /**
     * 发送钻头使用
     */
    @AppHandler(msgCode = OseeMessage.OseeMsgCode.C_S_CHALLENGE_USE_BIT_REQUEST_VALUE, exp = 1)
    public void useBit(OseePublicData.UseBitRequest req, FishingGamePlayer player, FishingChallengeRoom gameRoom) {
        FishingChallengeManager.useBit(req.getAngle(), gameRoom, player.getUser(), req.getFishId(), req.getFishType());
    }


    /**
     * 同步锁定
     */
    @AppHandler(msgCode = OseeMessage.OseeMsgCode.C_S_FISHING_CHALLENGE_SYNC_LOCK_REQUEST_VALUE, exp = 1)
    public void syncLock(TtmyFishingChallengeMessage.FishingChallengeSyncLockRequest request,
                         FishingChallengePlayer player, FishingChallengeRoom gameRoom) {
        TtmyFishingChallengeMessage.FishingChallengeSyncLockResponse.Builder builder =
                TtmyFishingChallengeMessage.FishingChallengeSyncLockResponse.newBuilder();
        builder.setFishId(request.getFishId());
        builder.setFishId1(request.getFishId1());
        builder.setFishId2(request.getFishId2());
        builder.setUserId(request.getUserId());
        challengeManager.sendSyncLockResponse(builder, gameRoom);
    }

    /**
     * 开启奖池
     */
    @AppHandler(msgCode = OseeMessage.OseeMsgCode.C_S_TTMY_OPEN_JC_REQUEST_VALUE, exp = 1)
    public void openJc(TtmyFishingChallengeMessage.OpenJcRequest request, FishingChallengePlayer player,
                       FishingChallengeRoom gameRoom) {
        challengeManager.openJc(request.getType(), request.getUserId(), gameRoom, player);
    }

    /**
     * 获取奖池金额
     */
    @AppHandler(msgCode = OseeMessage.OseeMsgCode.C_S_TTMY_GET_JC_ALL_MONEY_REQUEST_VALUE, exp = 1)
    public void getJcAllMoney(TtmyFishingChallengeMessage.GetJcAllMoneyRequest request, FishingChallengePlayer player,
                              FishingChallengeRoom gameRoom) {
        challengeManager.getJcAllMoney(request.getType(), request.getUserId(), gameRoom, player);
    }

    /**
     * 获取奖池金额
     */
    @AppHandler(msgCode = OseeMessage.OseeMsgCode.C_S_TTMY_GET_JC_RECORD_REQUEST_VALUE, exp = 1)
    public void getJcRecord(TtmyFishingChallengeMessage.GetJcAllRecordRequest request, FishingChallengePlayer player,
                            FishingChallengeRoom gameRoom) {
        challengeManager.getJcRecord(request.getUserId(), gameRoom, player);
    }

    /**
     * 小玛丽游戏结束任务
     */
    @AppHandler(msgCode = OseeMessage.OseeMsgCode.C_S_CHALLENGE_XML_END_REQUEST_VALUE, exp = 1)
    public void cxmlEnd(TtmyFishingChallengeMessage.ChallengeXmlEndRequest req, FishingChallengePlayer player,
                        FishingChallengeRoom gameRoom) {
        TtmyFishingChallengeMessage.ChallengeXmlEndResponse.Builder builder =
                TtmyFishingChallengeMessage.ChallengeXmlEndResponse.newBuilder();
        builder.setUserId(req.getUserId());
        builder.setType(req.getType());
        builder.setReword(req.getReword());
        long a = new Double(RedisUtil.val("USER_XML_MONEY_CHALLENGE" + req.getUserId(), 0D)).longValue();
        PlayerManager.addItem(UserContainer.getUserById(req.getUserId()), ItemId.DRAGON_CRYSTAL.getId(), a,
                ItemChangeReason.GAME_S, true);
        RedisHelper.set("USER_XML_MONEY_CHALLENGE" + req.getUserId(), "0");
        for (BaseGamePlayer gamePlayer : gameRoom.getGamePlayers()) {
            if (gamePlayer != null && gamePlayer.getUser().isOnline()) {
                NetManager.sendMessage(OseeMessage.OseeMsgCode.S_C_CHALLENGE_XML_END_RESPONSE_VALUE, builder.build(),
                        gamePlayer.getUser());
            }
        }

    }

    /**
     * 二次伤害鱼捕获鱼请求
     */
    @AppHandler(msgCode = OseeMessage.OseeMsgCode.C_S_FISHING_CHALLENGE_DOUBLE_KILL_FISH_REQUEST_VALUE, exp = 1)
    public void doubleKillFish(TtmyFishingChallengeMessage.FishingChallengeDoubleKillFishRequest req,
                               FishingChallengePlayer player, FishingChallengeRoom gameRoom) {
        challengeManager.doubleKillFishs(gameRoom, req.getUserId(), req.getFishdsList());
    }

    /**
     * 二次伤害鱼捕获鱼结束
     */
    @AppHandler(msgCode = OseeMessage.OseeMsgCode.C_S_FISHING_CHALLENGE_DOUBLE_KILL_END_REQUEST_VALUE, exp = 1)
    public void doubleKillEnd(TtmyFishingChallengeMessage.FishingChallengeDoubleKillEndRequest req,
                              FishingChallengePlayer player, FishingChallengeRoom gameRoom) {

        challengeManager.doubleKillEnd(gameRoom, player, req.getWinMoney(), req.getMult(), req.getFishConfigId(),
                req.getUserId());

    }


    /**
     * 道具使用状态同步
     */
    @AppHandler(msgCode = OseeMessage.OseeMsgCode.C_S_FISHING_CHALLENGE_PROPS_USE_STATE_SYNC_REQUEST_VALUE, exp = 1)
    public void propsUseStateSync(TtmyFishingChallengeMessage.FishingChallengePropsUseStateSyncRequest request,
                                  FishingChallengePlayer player, FishingChallengeRoom room) {

        challengeManager.propsUseStateSync(player, room, request);

    }

    /**
     * 背景同步
     */
    @AppHandler(msgCode = OseeMessage.OseeMsgCode.C_S_TTMY_BACKGROUND_SYNC_REQUEST_VALUE, exp = 1)
    public void backgroundSync(OseePublicData.BackgroundSyncRequest request, FishingChallengePlayer player,
                               FishingChallengeRoom gameRoom) {

        MyRefreshFishingHelper.backgroundSync(player, gameRoom, null, null);

    }

    /**
     * 获取玩家（日 或 周 或 月）排行榜
     */
    @AppHandler(msgCode = OseeMessage.OseeMsgCode.C_S_OSEE_GET_PLAYER_RANK_REQUEST_VALUE, exp = -1)
    public void rank(OseeFishingMessage.PlayerRankRequest request, ServerUser serverUser) {
        fishingManager.rank(request.getRankType(), request.getPageCurrent(), request.getPageSize(), request.getTotal(),
                serverUser);
    }

    /**
     * 查询捕鱼记录请求
     */
    @AppHandler(msgCode = OseeMessage.OseeMsgCode.C_S_OSEE_FISHING_RECORD_REQUEST_VALUE, exp = -1)
    public void fishingRecord(TtmyFishingRecordProto.FishingRecordResponse request, ServerUser user) {
        fishingManager.fishingRecord(request, user);
    }

    /**
     * 玩家金币同步
     */
    @AppHandler(msgCode = OseeMessage.OseeMsgCode.C_S_FISHING_CHALLENGE_PLAYER_MONEY_SYNC_REQUEST_VALUE, exp = 1)
    public void playerMoneySync(TtmyFishingChallengeMessage.FishingChallengePlayerMoneySyncRequest request,
                                FishingChallengePlayer player, FishingChallengeRoom room) {
        FishingChallengeManager.playerMoneySync(player, room);
    }

    /**
     * 仙魔九界位置移动
     */
    @AppHandler(msgCode = OseeMessage.OseeMsgCode.C_S_XMJJ_MOVE_REQUEST_VALUE, exp = 1)
    public void xmjjMoveRequest(TtmyFishingChallengeMessage.XmjjMoveRequest request, FishingChallengePlayer player,
                                FishingChallengeRoom room) {
        FishingChallengeManager.xmjjMoveRequest(request, player, room);
    }

    /**
     * 仙魔九界，玩家离开怪物攻击范围时的请求
     */
    @AppHandler(msgCode = OseeMessage.OseeMsgCode.C_S_XMJJ_PLAYER_LEAVE_MONSTER_ATTACK_RANGE_REQUEST_VALUE, exp = 1)
    public void xmjjPlayerLeaveMonsterAttackRangeRequest(
            TtmyFishingChallengeMessage.XmjjPlayerLeaveMonsterAttackRangeRequest request, FishingChallengePlayer player,
            FishingChallengeRoom room) {
        FishingChallengeManager.xmjjPlayerLeaveMonsterAttackRangeRequest(request, player, room);
    }


    /**
     * ip区域限制请求
     */
    @AppHandler(msgCode = OseeMessage.OseeMsgCode.C_S_IP_REGION_LIMIT_REQUEST_VALUE, exp = -1)
    public void ipRegionLimitRequest(TtmyFishingChallengeMessage.IpRegionLimitRequest request, ServerUser user) {
        FishingChallengeManager.ipRegionLimitRequest(request, user);
    }

    /**
     * 击杀boss列表请求
     */
    @AppHandler(msgCode = OseeMessage.OseeMsgCode.C_S_KILL_BOSS_LIST_REQUEST_VALUE, exp = -1)
    public void killBossListRequest(TtmyFishingChallengeMessage.KillBossListRequest request, ServerUser user) {
        FishingChallengeManager.killBossListRequest(request, user);
    }


    // 通过：用户 id进行区分，并且会在 5秒过期，预使用：炎爆符
    public static final String PRE_USE_YAN_BAO_FU_USER = "PRE_USE_YAN_BAO_FU_USER:";

    // 通过：用户 id进行区分，并且会在 5秒过期，预使用：万剑诀
    public static final String PRE_USE_WAN_JIAN_JUE_USER = "PRE_USE_WAN_JIAN_JUE_USER:";


    // 通过：用户 id进行区分，并且会在 5秒过期，预使用：微型导弹
    public static final String PRE_USE_WEI_XING_DAO_DAN_USER = "PRE_USE_WEI_XING_DAO_DAN_USER:";


    // 通过：用户 id进行区分，并且会在 5秒过期，预使用：微型导弹
    public static final String PRE_USE_LIA_NZI_HUO_PAO_USER = "PRE_USE_LIA_NZI_HUO_PAO_USER:";


    /**
     * 重置boss号角使用时间请求
     */
    @AppHandler(msgCode = OseeMessage.OseeMsgCode.C_S_RESET_CAN_USE_BOSS_BUGLE_TIME_REQUEST_VALUE, exp = 1)
    public void resetCanUseBossBugleTimeRequest(TtmyFishingChallengeMessage.ResetCanUseBossBugleTimeRequest request,
                                                FishingChallengePlayer player, FishingChallengeRoom gameRoom) {

        gameRoom.setCanUseBossBugleTime(0L);

        TtmyFishingChallengeMessage.ResetCanUseBossBugleTimeResponse.Builder builder =
                TtmyFishingChallengeMessage.ResetCanUseBossBugleTimeResponse.newBuilder();

        builder.setFishId(request.getFishId());
        builder.setWinMoney(request.getWinMoney());
        builder.setUserId(player.getId());

        builder.setRequestUserId(request.getUserId());

        MyRefreshFishingUtil.sendRoomMessage(gameRoom,
                OseeMessage.OseeMsgCode.S_C_RESET_CAN_USE_BOSS_BUGLE_TIME_RESPONSE_VALUE, builder);

    }


    /**
     * 场次下一次世界boss刷新时间请求
     */
    @AppHandler(msgCode = OseeMessage.OseeMsgCode.C_S_NEXT_SESSION_WORLD_BOSS_REFRESH_TIME_REQUEST_VALUE, exp = 1)
    public void nextSessionWorldBossRefreshTimeRequest(
            TtmyFishingChallengeMessage.NextSessionWorldBossRefreshTimeRequest request, FishingChallengePlayer player,
            FishingChallengeRoom gameRoom) {

        Set<Long> refreshTimeRuleIdSet = gameRoom.getRefreshTimeRuleIdSet();

        String hm = "";

        hm = getHm(refreshTimeRuleIdSet, hm);

        TtmyFishingChallengeMessage.NextSessionWorldBossRefreshTimeResponse.Builder builder =
                TtmyFishingChallengeMessage.NextSessionWorldBossRefreshTimeResponse.newBuilder();

        builder.setHm(hm);

        if (StrUtil.isNotBlank(hm)) {

            // 设置：还剩余多少秒
            DateTime timeToday = DateUtil.parseTimeToday(hm);

            long currentTimeMillis = System.currentTimeMillis();

            long timeTodayTime = timeToday.getTime();

            if (timeTodayTime < currentTimeMillis) {

                DateTime dateTime = DateUtil.offsetDay(timeToday, 1); // 增加一天

                timeTodayTime = dateTime.getTime();

            }

            builder.setRemainS((int) ((timeTodayTime - currentTimeMillis) / 1000));

        }

        MyRefreshFishingUtil.sendRoomMessage(gameRoom,
                OseeMessage.OseeMsgCode.S_C_NEXT_SESSION_WORLD_BOSS_REFRESH_TIME_RESPONSE_VALUE, builder);

    }

    private String getHm(Set<Long> refreshTimeRuleIdSet, String hm) {

        if (CollUtil.isEmpty(refreshTimeRuleIdSet)) {

            return hm;

        }

        Long firstRuleId = CollUtil.getFirst(refreshTimeRuleIdSet);

        if (firstRuleId != null) {

            FishRefreshRule rule = DataContainer.getData(firstRuleId, FishRefreshRule.class);

            if (rule != null) {

                long fixedLastRefreshTime = rule.getFixedLastRefreshTime();

                List<String> refreshTimeList = rule.getRefreshTimeList();

                for (int i = 0; i < refreshTimeList.size(); i++) {

                    String refreshTimeStr = refreshTimeList.get(i);

                    DateTime timeToday = DateUtil.parseTimeToday(refreshTimeStr);

                    if (fixedLastRefreshTime == timeToday.getTime()) {

                        if (i == refreshTimeList.size() - 1) { // 如果是：最后一个

                            hm = refreshTimeList.get(0); // 则获取第一个时间

                        } else {

                            hm = refreshTimeList.get(i + 1);

                        }

                        break;

                    }

                }

                if (StrUtil.isBlank(hm)) {

                    long currentTimeMillis = System.currentTimeMillis(); // 获取：最接近的一个时间

                    for (String item : refreshTimeList) {

                        DateTime timeToday = DateUtil.parseTimeToday(item);

                        if (currentTimeMillis < timeToday.getTime()) {

                            hm = item;
                            break;

                        }

                    }

                    if (StrUtil.isBlank(hm)) {

                        hm = refreshTimeList.get(0); // 则获取第一个时间

                    }

                }

            }

        }

        return hm;

    }

}
