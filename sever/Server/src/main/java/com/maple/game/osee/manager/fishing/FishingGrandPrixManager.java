package com.maple.game.osee.manager.fishing;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.util.BooleanUtil;
import com.maple.common.login.util.UsdtLoginUtil;
import com.maple.database.config.redis.RedisHelper;
import com.maple.database.data.mapper.UserMapper;
import com.maple.engine.container.DataContainer;
import com.maple.engine.container.UserContainer;
import com.maple.engine.data.ServerUser;
import com.maple.engine.utils.ThreadPoolUtils;
import com.maple.game.osee.common.RedisUtil;
import com.maple.game.osee.dao.data.entity.OseePlayerEntity;
import com.maple.game.osee.dao.data.entity.UserStatus;
import com.maple.game.osee.dao.data.mapper.OseePlayerMapper;
import com.maple.game.osee.dao.log.entity.AppRewardLogEntity;
import com.maple.game.osee.dao.log.entity.AppRewardRankEntity;
import com.maple.game.osee.dao.log.entity.OseeFishingRecordLogEntity;
import com.maple.game.osee.dao.log.entity.OseePlayerTenureLogEntity;
import com.maple.game.osee.dao.log.mapper.AppRankLogMapper;
import com.maple.game.osee.dao.log.mapper.AppRewardRankMapper;
import com.maple.game.osee.dao.log.mapper.OseeFishingRecordLogMapper;
import com.maple.game.osee.dao.log.mapper.OseePlayerTenureLogMapper;
import com.maple.game.osee.entity.GameEnum;
import com.maple.game.osee.entity.ItemChangeReason;
import com.maple.game.osee.entity.ItemId;
import com.maple.game.osee.entity.fishing.NewBaseFishingRoom;
import com.maple.game.osee.entity.fishing.csv.file.FishConfig;
import com.maple.game.osee.entity.fishing.game.FireStruct;
import com.maple.game.osee.entity.fishing.game.FishStruct;
import com.maple.game.osee.entity.fishing.grandprix.FishingGrandPrixPlayer;
import com.maple.game.osee.entity.fishing.grandprix.FishingGrandPrixRoom;
import com.maple.game.osee.entity.fishing.task.GoalType;
import com.maple.game.osee.entity.fishing.task.TaskType;
import com.maple.game.osee.manager.PlayerManager;
import com.maple.game.osee.manager.UserPropsManager;
import com.maple.game.osee.manager.UserStatusManager;
import com.maple.game.osee.manager.fishing.util.FishingUtil;
import com.maple.game.osee.proto.HwLoginMessage;
import com.maple.game.osee.proto.OseeMessage;
import com.maple.game.osee.proto.OseePublicData;
import com.maple.game.osee.proto.fishing.TtmyFishingChallengeMessage;
import com.maple.game.osee.util.*;
import com.maple.gamebase.container.GameContainer;
import com.maple.gamebase.data.BaseGamePlayer;
import com.maple.network.manager.NetManager;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RList;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.maple.game.osee.manager.fishing.FishingManager.*;
import static com.maple.game.osee.proto.TtmyFishingGrandPrixMessage.*;

@Component
@Slf4j
public class FishingGrandPrixManager {

    @Autowired
    UserMapper userMapper;

    private static OseePlayerMapper oseePlayerMapper;

    @Resource
    public void setOseePlayerMapper(OseePlayerMapper oseePlayerMapper) {
        FishingGrandPrixManager.oseePlayerMapper = oseePlayerMapper;
    }

    @Autowired
    AppRankLogMapper appRankLogMapper;

    private static AppRewardRankMapper appRewardRankMapper;

    @Resource
    public void setAppRewardRankMapper(AppRewardRankMapper appRewardRankMapper) {
        FishingGrandPrixManager.appRewardRankMapper = appRewardRankMapper;
    }

    private static FishingUtil fishingUtil;

    @Resource
    public void setFishingUtil(FishingUtil fishingUtil) {
        FishingGrandPrixManager.fishingUtil = fishingUtil;
    }

    private static RedissonClient redissonClient;

    @Resource
    public void setRedissonClient(RedissonClient redissonClient) {
        FishingGrandPrixManager.redissonClient = redissonClient;
    }

    // private Logger logger = LoggerFactory.getLogger(this.getClass());


    /**
     * 第一次加入房间的花费
     */
    public static final String GRANDPRIX_CONFIG_BM1 = "Fishing:GrandPrixRobot:BM1";
    /**
     * 玩家大奖赛炮台外观 + playerId
     */
    public static final String PLAYER_GRANDPRIX_CONFIG_VB_KEY = "player:grandprix:config:vb:";
    /**
     * 玩家大奖赛翅膀外观 + playerId
     */
    public static final String PLAYER_GRANDPRIX_CONFIG_VW_KEY = "player:grandprix:config:vw:";

    /**
     * 玩家大奖赛剩余子弹数量 + playerId
     */
    public static final String PLAYER_GRANDPRIX_CONFIG_BULLET_KEY = "player:grandprix:config:bullet:";

    /**
     * 周排行榜
     */
    public static final String PLAYER_GRANDPRIX_CONFIG_RANK_WEEK_KEY = "player:grandprix:config:rank:week";

    /**
     * 上周排行榜
     */
    public static final String PLAYER_GRANDPRIX_LAST_CONFIG_RANK_WEEK_KEY = "player:last:grandprix:config:rank:week";

    /**
     * 日排行榜
     */
    public static final String PLAYER_GRANDPRIX_CONFIG_RANK_DAY_KEY = "player:grandprix:config:rank:day";

    /**
     * 昨日排行榜
     */
    public static final String PLAYER_GRANDPRIX_LAST_CONFIG_RANK_DAY_KEY = "player:last:grandprix:config:rank:day";

    /**
     * 玩家大奖赛今日积分 + playerId
     */
    public static final String PLAYER_GRANDPRIX_CONFIG_POINT_DAY_KEY = "player:grandprix:config:point:day:";

    /**
     * 玩家大奖赛昨日积分 + playerId
     */
    public static final String PLAYER_GRANDPRIX_LAST_CONFIG_POINT_DAY_KEY = "player:last:grandprix:config:point:day:";

    /**
     * 玩家大奖赛周积分 + playerId
     */
    public static final String PLAYER_GRANDPRIX_CONFIG_POINT_WEEK_KEY = "player:grandprix:config:point:week:";

    /**
     * 玩家大奖赛上周积分 + playerId
     */
    public static final String PLAYER_GRANDPRIX_LAST_CONFIG_POINT_WEEK_KEY = "player:last:grandprix:config:point:week:";

    /**
     * 玩家大奖赛今日游戏局数 + playerId
     */
    public static final String PLAYER_GRANDPRIX_CONFIG_GAMES_KEY = "player:grandprix:config:games:";

    /**
     * 玩家大奖赛入场金币数
     */
    public static final String PLAYER_GRANDPRIX_CONFIG_ENTRY_GOLD_KEY = "player:grandprix:config:entry:gold";

    /**
     * 玩家大奖赛入场钻石数
     */
    public static final String PLAYER_GRANDPRIX_CONFIG_ENTRY_DIAMOND_KEY = "player:grandprix:config:entry:diamond";

    /**
     * 目前所有的boss数量
     */
    private static long bossNum;

    /**
     * 大奖赛总库存
     */
    public static final String PLAYER_GRANDPRIX_CONFIG_STOCK_KEY = "player:grandprix:config:stock";

    /**
     * 玩家总输赢
     */
    public static final String PLAYER_GRANDPRIX_CONFIG_POINT_TOTAL_KEY = "player:grandprix:config:point:total:";

    /**
     * 玩家今日输赢
     */
    public static final String PLAYER_GRANDPRIX_CONFIG_POINT_DAY_TOTAL_KEY = "player:grandprix:config:point:day:total:";

    /**
     * 玩家本周输赢
     */
    public static final String PLAYER_GRANDPRIX_CONFIG_POINT_WEEK_TOTAL_KEY =
            "player:grandprix:config:point:week:total:";

    // -------------------- 以下为玩家参数控制变量 -----------------
    public static final String PLAYER_GRANDPRIX_CONFIG_BLACK_ROOM_KEY = "player:grandprix:config:blackroom";

    // 实时AP值
    public static final String PLAYER_GRANDPRIX_CONFIG_AP_KEY = "player:grandprix:config:ap";

    // 实时AP值
    public static final String PLAYER_GRANDPRIX_CONFIG_q0_KEY = "player:grandprix:config:q0";

    // AP变化0.01库存变化量
    public static final String PLAYER_GRANDPRIX_CONFIG_APT_KEY = "player:grandprix:config:apt";

    // 小黑屋参数
    public static final String PLAYER_GRANDPRIX_CONFIG_BP_KEY = "player:grandprix:config:bp";

    // 小黑屋金币
    public static final String PLAYER_GRANDPRIX_CONFIG_QZ_KEY = "player:grandprix:config:qz";

    // 赢上限参数
    public static final String PLAYER_GRANDPRIX_CONFIG_PQ_KEY = "player:grandprix:config:pq";

    // （历史）玩家累积赢取金币
    public static final String PLAYER_GRANDPRIX_CONFIG_QY_KEY = "player:grandprix:config:qy";

    // 输下限参数
    public static final String PLAYER_GRANDPRIX_CONFIG_PA_KEY = "player:grandprix:config:pa";

    // （历史）玩家累积输掉金币
    public static final String PLAYER_GRANDPRIX_CONFIG_QS_KEY = "player:grandprix:config:qs";

    // 幸运参数
    public static final String PLAYER_GRANDPRIX_CONFIG_PW_KEY = "player:grandprix:config:pw";

    // （当天）玩家累积赢取金币
    public static final String PLAYER_GRANDPRIX_CONFIG_QX_KEY = "player:grandprix:config:qx";

    // 挽救参数
    public static final String PLAYER_GRANDPRIX_CONFIG_PY_KEY = "player:grandprix:config:py";

    // （当天）玩家累计输掉金币
    public static final String PLAYER_GRANDPRIX_CONFIG_QW_KEY = "player:grandprix:config:qw";

    // 抽水参数
    public static final String PLAYER_GRANDPRIX_CONFIG_TP_KEY = "player:grandprix:config:tp";

    // 预留金币
    public static long initPool = 0;

    private static OseePlayerTenureLogMapper tenureLogMapper;

    private static OseeFishingRecordLogMapper fishingRecordLogMapper;

    @Resource
    public void setTenureLogMapper(OseePlayerTenureLogMapper tenureLogMapper) {
        FishingGrandPrixManager.tenureLogMapper = tenureLogMapper;
    }

    @Resource
    public void setFishingRecordLogMapper(OseeFishingRecordLogMapper fishingRecordLogMapper) {
        FishingGrandPrixManager.fishingRecordLogMapper = fishingRecordLogMapper;
    }

    public FishingGrandPrixManager() {

        // 每一秒循环执行房间任务
        long loopTime = 1000;

        ThreadPoolUtils.TIMER_SERVICE_POOL.scheduleAtFixedRate(() -> {

            if (isThisGameOver(10)) {// 游戏结束(延长10分钟) 结算清退玩家

                GameContainer.getGameRooms(FishingGrandPrixRoom.class).stream().filter(Objects::nonNull)
                        .filter(room -> room.getPlayerSize() > 0).forEach(room -> {

                            Arrays.stream(room.getGamePlayers())
                                    .filter(player -> player != null && player.getUser().isOnline()) // 在线玩家

                                    .forEach(player -> {

                                        // 游戏结算
                                        end(player.getId(), 0L, player.getUser());
                                        // 踢出游戏
                                        exitRoom((FishingGrandPrixPlayer) player, room);

                                    });

                        });

                return;

            }

            try {
                List<FishingGrandPrixRoom> gameRooms = GameContainer.getGameRooms(FishingGrandPrixRoom.class);
                for (FishingGrandPrixRoom gameRoom : gameRooms) {
                    if (gameRoom != null) {
                        if (gameRoom.getPlayerSize() > 0) { // 有玩家才刷鱼
                            // if (FishingRobotManager.USE_GRANDPRIX_ROBOT != 0) { // 使用机器人才生成默认机器人
                            // // 加几个机器人占座
                            // List<BaseGamePlayer> players = Arrays.asList(gameRoom.getGamePlayers());
                            // long robotCount =
                            // players.stream().filter(player -> player instanceof FishingGrGameRobot).count();
                            // if (robotCount >= FishingRobotManager.ROBOT_GRANDPRIX_COUNT) { // 机器人刷新数量已达上限
                            // doFishingRoomTask(gameRoom);
                            // continue;
                            // }
                            // int robotNum = ThreadLocalRandom.current()
                            // .nextInt(1, FishingRobotManager.ROBOT_GRANDPRIX_COUNT + 1);
                            // for (int i = 1; i < robotNum; i++) {
                            // FishingGrGameRobot robotPlayer = robotManager.createRobotPlayer2(gameRoom);
                            // if (robotPlayer != null) {
                            // robotJoinRoom(UserContainer.getUserById(robotPlayer.getId()), gameRoom);
                            // sendJoinRoomResponse(gameRoom, robotPlayer); // 发送加入房间消息
                            // sendPlayersInfoResponse(gameRoom, robotPlayer); // 发送玩家数据列表消息
                            // sendSynchroniseResponse(gameRoom, robotPlayer); // 发送同步鱼消息
                            // sendFrozenMessage(gameRoom, robotPlayer); // 发送房间当前冰冻消息
                            // sendRoomPlayerInfoResponse(gameRoom, robotPlayer);
                            // MyRefreshFishingUtil.sendRoomMessage(gameRoom,
                            // OseeMessage.OseeMsgCode.S_C_TTMY_FISHING_GRAND_PRIX_ROOM_PLAYER_INFO_RESPONSE_VALUE,
                            // createGrandPrixRoomPlayerInfoResponse(robotPlayer));
                            // // 机器人自动发炮的定时任务
                            // robotManager.robotFire2(gameRoom, robotPlayer.getId());
                            // }
                            // }
                            // }
                            doFishingRoomTask(gameRoom);
                        } else {
                            gameRoom.reset(true);
                        }
                    }
                }
            } catch (Exception e) {
                log.error("捕鱼大奖赛:执行房间循环任务时出现异常:[{}][{}]", e.getMessage(), e);
            }
        }, 0, loopTime, TimeUnit.MILLISECONDS);
    }

    /**
     * 玩家重连
     */
    public void reconnect(FishingGrandPrixRoom gameRoom, FishingGrandPrixPlayer gamePlayer) {
        log.info("玩家[{}]重连大奖赛捕鱼房间[{}]", gamePlayer.getUser().getNickname(), gameRoom.getCode());
        ServerUser user = gamePlayer.getUser();
        joinFishingRoom(user, gameRoom); // 发送加入房间消息
        sendPlayersInfoResponse(gameRoom, gamePlayer); // 发送玩家数据列表消息
        sendSynchroniseResponse(gameRoom, gamePlayer); // 发送同步鱼消息
        sendFrozenMessage(gameRoom, gamePlayer); // 发送房间当前冰冻消息
    }

    /**
     * 向客户端响应大奖赛是否开始 如果大奖赛已经开始，则返回true 和 玩家子弹剩余数量 否则 返回false
     *
     * @param playerId 玩家Id
     */
    public void start(Long playerId, ServerUser user) {

        FishingGrandPrixStartResponse.Builder builder = FishingGrandPrixStartResponse.newBuilder();

        int bullet = RedisUtil.val(PLAYER_GRANDPRIX_CONFIG_BULLET_KEY + playerId, 1000);
        int games = RedisUtil.val(PLAYER_GRANDPRIX_CONFIG_GAMES_KEY + playerId, 0);

        // 玩家今日挑战次数
        builder.setGames(games);

        // 开始时间
        final String startTimeStr = RedisUtil.val("Fishing:GrandPrixRobot:startTime", "10:00");

        DateTime startDateTime = new DateTime(startTimeStr);

        builder.setStartTime(startDateTime.getTime());

        // 结束时间
        final String endTimeStr = RedisUtil.val("Fishing:GrandPrixRobot:endTime", "20:00");

        DateTime endDateTime = new DateTime(endTimeStr);

        builder.setEndTime(endDateTime.getTime());

        long currentTimeMillis = System.currentTimeMillis();

        if (currentTimeMillis > builder.getStartTime() && currentTimeMillis < builder.getEndTime()) {

            builder.setProgress(true);
            builder.setBullet(bullet);

            // 报名费用
            double v = RedisUtil.val(GRANDPRIX_CONFIG_BM1 + user.getId(),
                    Convert.toLong(RedisUtil.val(GRANDPRIX_CONFIG_BM1, "1000000"))) * (games * 0.1 + 1);

            long cost = (long) v;

            builder.setCost(cost);

            NetManager.sendMessage(OseeMessage.OseeMsgCode.S_C_TTMY_FISHING_GRAND_PRIX_START_RESPONSE_VALUE, builder,
                    user);

        } else {

            builder.setProgress(false);
            NetManager.sendMessage(OseeMessage.OseeMsgCode.S_C_TTMY_FISHING_GRAND_PRIX_START_RESPONSE_VALUE, builder,
                    user);

        }

    }

    /**
     * 游戏是否结束
     */
    private boolean isThisGameOver(int extend) {

        final String startTimeStr = RedisUtil.val("Fishing:GrandPrixRobot:startTime", "10:00");

        DateTime startDateTime = new DateTime(startTimeStr);

        final String endTimeStr = RedisUtil.val("Fishing:GrandPrixRobot:endTime", "20:00");

        DateTime endDateTime = new DateTime(endTimeStr);

        endDateTime.offset(DateField.MINUTE, extend);

        long currentTimeMillis = System.currentTimeMillis();

        return BooleanUtil
                .isFalse(currentTimeMillis > startDateTime.getTime() && currentTimeMillis < endDateTime.getTime());

    }

    /**
     * 响应用户排名
     *
     * @param rankType
     * @param pageCurrent
     * @param pageSize
     * @param total
     * @param user
     */
    public void rank(int rankType, int pageCurrent, int pageSize, int total, ServerUser user) {

        String key;

        if (rankType == 1) {
            key = PLAYER_GRANDPRIX_CONFIG_RANK_DAY_KEY;
        } else {
            key = PLAYER_GRANDPRIX_CONFIG_RANK_WEEK_KEY;
        }

        int start = (pageCurrent - 1) * pageSize;
        int end = start + pageSize - 1;

        FishingGrandPrixRankResponse.Builder builder = FishingGrandPrixRankResponse.newBuilder();
        builder.setRankType(rankType);

        FishingGrandPrixRewardMessage.Builder builder1 = FishingGrandPrixRewardMessage.newBuilder();
        Set<String> rankIdSet = RedisUtil.values(key, start, end - 1);

        // 处理：榜单
        rankIdSet = handleRankIdSet(pageSize, key, start, end, rankIdSet);

        int index = 0;
        for (String rankId : rankIdSet) {

            long playerId = Long.parseLong(rankId);

            ServerUser user3 = UserContainer.getUserById(playerId);

            if (user3 == null) {

                continue;

            }

            index++;

            if (index > pageSize) { // 结束循环
                break;
            }

            FishingGrandPrixPlayerInfoMessage.Builder b = FishingGrandPrixPlayerInfoMessage.newBuilder();

            b.setPlayerId(playerId);
            int pointDay = getPointDay(playerId);
            b.setDayPoint(pointDay);

            // log.info("playerId：{}，pointDay：{}", playerId, pointDay);

            String[] split = getPointBonusDay(playerId, pointDay).split(",");
            if (split.length == 3) {
                b.setWingBonus(Double.parseDouble(split[0]));
                b.setBatteryBonus(Double.parseDouble(split[1]));
                b.setGameBonus(Double.parseDouble(split[2]));
            }

            b.setWeekPoint(getPointWeek(playerId));
            b.setRank(index);

            b.setName(user3.getEntity().getNickname().substring(0, 2) + "***");
            b.setHeadIndex(user3.getEntity().getHeadIndex());
            b.setHeadUrl(user3.getEntity().getHeadUrl());
            b.setSex(user3.getEntity().getSex());

            AppRewardRankEntity rewardRank = appRewardRankMapper.findReward(rankType, index);

            if (rewardRank != null) {

                // 处理：奖励
                handleRewardRank(builder1, rewardRank);

            } else {

                builder1.setItemId(0);
                builder1.setItemNum(0);

            }

            b.setReward(builder1);
            builder.addPlayerInfos(b);
            builder1.setItemId(0);
            builder1.setItemNum(0);

        }

        // log.info("rankType：{}，rankIdSet：{}，pageCurrent：{}，pageSize：{}，playerCount：{}", rankType, rankIdSet,
        // pageCurrent,
        // pageSize, builder.getPlayerInfosCount());

        NetManager.sendMessage(OseeMessage.OseeMsgCode.S_C_TTMY_FINSHING_GRAND_PRIX_RANK_RESPONSE_VALUE, builder, user);

    }

    /**
     * 处理：奖励
     */
    private void handleRewardRank(FishingGrandPrixRewardMessage.Builder builder1, AppRewardRankEntity rewardRank) {

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

    }

    /**
     * 玩家大奖赛结算
     *
     * @param userId
     * @param user
     */
    public static void end(long userId, Long batteryLevel, ServerUser user) {

        FishingGrandPrixPlayer player1 = GameContainer.getPlayerById(user.getId());

        // 结算的时候，处理排行榜
        endHandlerRank(user, player1);

        String key = PLAYER_GRANDPRIX_CONFIG_RANK_DAY_KEY;

        Set<String> rankIds = RedisUtil.values(key, 0, Integer.MAX_VALUE);

        int index = 0;
        for (String rankId : rankIds) {
            ++index;
            if (userId == Long.parseLong(rankId)) {
                break;
            }
        }

        FishingGrandPrixEndResponse.Builder builder = FishingGrandPrixEndResponse.newBuilder();
        builder.setDayPoint(getPointDay(userId));
        builder.setRank(index);

        int games = RedisUtil.val(PLAYER_GRANDPRIX_CONFIG_GAMES_KEY + userId, 0);
        Long dayPoint = (long) (RedisUtil.get(PLAYER_GRANDPRIX_CONFIG_POINT_DAY_KEY + userId, games - 1));

        // TODO:翅膀加成借用vip
        final String wv = RedisUtil.val(PLAYER_GRANDPRIX_CONFIG_VW_KEY + user.getId(), "80");
        double vip = 0.0;
        if (new BigDecimal(wv).intValue() == ItemId.WING_VIEW_1.getId()) {
            vip = 0.005;
        } else if (new BigDecimal(wv).intValue() == ItemId.WING_VIEW_2.getId()) {
            vip = 0.01;
        } else if (new BigDecimal(wv).intValue() == ItemId.WING_VIEW_3.getId()) {
            vip = 0.015;
        } else if (new BigDecimal(wv).intValue() == ItemId.WING_VIEW_4.getId()) {
            vip = 0.02;
        } else if (new BigDecimal(wv).intValue() == ItemId.WING_VIEW_5.getId()) {
            vip = 0.025;
        }

        int game = RedisUtil.val(PLAYER_GRANDPRIX_CONFIG_GAMES_KEY + userId, 0);
        final Double random = RandomUtil.getRandom(Arrays.asList(0.1, 0.11, 0.12, 0.13, 0.14, 0.15));
        double ga = (game - 1) * random / 100;

        // TODO:炮台外观加成(借用炮台倍数加成)
        final String bv = RedisUtil.val(PLAYER_GRANDPRIX_CONFIG_VB_KEY + user.getId(), "70");
        double ba = 0.0;
        if (new BigDecimal(bv).intValue() == ItemId.BATTERY_VIEW_5.getId()) {
            ba = 0.005;
        } else if (new BigDecimal(bv).intValue() == ItemId.BATTERY_VIEW_6.getId()) {
            ba = 0.01;
        } else if (new BigDecimal(bv).intValue() == ItemId.BATTERY_VIEW_7.getId()) {
            ba = 0.015;
        }

        dayPoint += new Double(dayPoint * (ba + vip + ga)).longValue();

        builder.setNowPoint(dayPoint); // 结算以后的分数
        builder.setVip(vip);// vip加成(翅膀加成)
        builder.setGa(ga);// 游戏次数加成
        builder.setBattery(ba);// 炮倍加成(炮台加成)

        // RedisHelper.set("FISHING_GRANDPRIX_ALL_BATTERYLEVEL" + userId, "0");
        AppRewardRankEntity rewardRank = appRewardRankMapper.findReward(1, index);
        if (rewardRank != null) {

            AppRewardLogEntity reward = rewardRank.getReward();

            if (reward.getGold() != 0) {
                builder.setItemId(1);
                builder.setItemNum(reward.getGold());
            } else {
                if (builder.getItemId() == 0) {
                    builder.setItemId(0);
                    builder.setItemNum(0);
                }
            }

            if (reward.getDiamond() != 0) {
                builder.setItemId(4);
                builder.setItemNum(reward.getDiamond());
            } else {
                if (builder.getItemId() == 0) {
                    builder.setItemId(0);
                    builder.setItemNum(0);
                }
            }

            if (reward.getLowerBall() != 0) {
                builder.setItemId(5);
                builder.setItemNum(reward.getLowerBall());
            } else {
                if (builder.getItemId() == 0) {
                    builder.setItemId(0);
                    builder.setItemNum(0);
                }
            }

            if (reward.getMiddleBall() != 0) {
                builder.setItemId(6);
                builder.setItemNum(reward.getMiddleBall());
            } else {
                if (builder.getItemId() == 0) {
                    builder.setItemId(0);
                    builder.setItemNum(0);
                }
            }

            if (reward.getHighBall() != 0) {
                builder.setItemId(7);
                builder.setItemNum(reward.getHighBall());
            } else {
                if (builder.getItemId() == 0) {
                    builder.setItemId(0);
                    builder.setItemNum(0);
                }
            }

            if (reward.getSkillLock() != 0) {
                builder.setItemId(8);
                builder.setItemNum(reward.getSkillLock());
            } else {
                if (builder.getItemId() == 0) {
                    builder.setItemId(0);
                    builder.setItemNum(0);
                }
            }

            if (reward.getSkillFrozen() != 0) {
                builder.setItemId(9);
                builder.setItemNum(reward.getSkillFrozen());
            } else {
                if (builder.getItemId() == 0) {
                    builder.setItemId(0);
                    builder.setItemNum(0);
                }
            }

            if (reward.getSkillFast() != 0) {
                builder.setItemId(10);
                builder.setItemNum(reward.getSkillFast());
            } else {
                if (builder.getItemId() == 0) {
                    builder.setItemId(0);
                    builder.setItemNum(0);
                }
            }

            if (reward.getSkillCrit() != 0) {
                builder.setItemId(11);
                builder.setItemNum(reward.getSkillCrit());
            } else {
                if (builder.getItemId() == 0) {
                    builder.setItemId(0);
                    builder.setItemNum(0);
                }
            }

            if (reward.getBossBugle() != 0) {
                builder.setItemId(13);
                builder.setItemNum(reward.getBossBugle());
            } else {
                if (builder.getItemId() == 0) {
                    builder.setItemId(0);
                    builder.setItemNum(0);
                }
            }

        } else {

            builder.setItemId(0);
            builder.setItemNum(0);

        }

        NetManager.sendMessage(OseeMessage.OseeMsgCode.S_C_GRANDPRIX_END_RESPONSE_VALUE, builder, user);

    }

    /**
     * 结算的时候，处理排行榜
     */
    private static void endHandlerRank(ServerUser user, FishingGrandPrixPlayer player1) {

        // 保存今日最高积分到每周积分
        List<String> dayPointList = RedisUtil.getList(PLAYER_GRANDPRIX_CONFIG_POINT_DAY_KEY + user.getId());

        Optional<String> max = dayPointList.stream().max(Comparator.comparingInt(Integer::parseInt));
        String maxDayPint = "0";
        if (max.isPresent()) {
            maxDayPint = dayPointList.stream().max(Comparator.comparingInt(Integer::parseInt)).orElse("0");
        }

        Calendar calendar = Calendar.getInstance();
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        List<String> weekPoint = RedisUtil.getList(PLAYER_GRANDPRIX_CONFIG_POINT_WEEK_KEY + user.getId()).stream()
                .sorted((Comparator.comparing(Integer::parseInt))).sorted(Comparator.reverseOrder()).limit(3)
                .collect(Collectors.toList());

        long totalWeekPoint = 0L;
        for (int i = 0; i < weekPoint.size(); i++) {
            totalWeekPoint += Long.parseLong(weekPoint.get(i));
        }

    }

    /**
     * 大奖赛 玩家加入房间
     *
     * @param user
     */
    public void joinRoom(ServerUser user) {

        long batteryLevel = PlayerManager.getPlayerBatteryLevel(user);
        if (batteryLevel < 2000) {
            NetManager.sendHintMessageToClient("炮台等级不足2000，请先升级", user);
            return;
        }
        // 是否开赛检查
        // Calendar calendar = Calendar.getInstance();
        // int hourOfDay = calendar.get(Calendar.HOUR_OF_DAY);

        if (UsdtLoginUtil.getProdFlag()) {
            if (isThisGameOver(0)) {// 游戏已结束
                NetManager.sendHintMessageToClient("今日比赛已结束", user);
                return;
            }
        }

        // 子弹限制
        int maxBullet = 1000;
        // 获取玩家子弹剩余数量与局数 （默认2000发，第一局）
        int bullet = RedisUtil.val(PLAYER_GRANDPRIX_CONFIG_BULLET_KEY + user.getId(), 0);

        if (bullet == 0) {// 没有子弹需要花钱购买(开始游戏)

            int games = RedisUtil.val(PLAYER_GRANDPRIX_CONFIG_GAMES_KEY + user.getId(), 0);

            double v = RedisUtil.val(GRANDPRIX_CONFIG_BM1 + user.getId(),
                    Convert.toLong(RedisUtil.val(GRANDPRIX_CONFIG_BM1, "1000000"))) * (games * 0.1 + 1);

            long cost = (long) v;

            // 金币检查
            if (!PlayerManager.checkItem(user, ItemId.DRAGON_CRYSTAL, cost)) {
                // NetManager.sendHintMessageToClient("金币不足" + cost + "，无法进入该房间", user);
                NetManager.sendHintMessageToClient("请保留足额报名费!", user);
                return;
            }

            OseePlayerEntity playerEntity = PlayerManager.getPlayerEntity(user);

            long preDragonCrystal = playerEntity.getDragonCrystal();
            long preDiamond = playerEntity.getDiamond();
            long preGoldTorpedo = playerEntity.getGoldTorpedo();
            long preLottery = playerEntity.getLottery();

            PlayerManager.addItem(user, ItemId.DRAGON_CRYSTAL, -cost, ItemChangeReason.FISHING_GRANDPRIX_JOIN_ROOM,
                    true);

            long costDay = redissonClient.getAtomicLong("GRANDPRIX_JOIN_ROOM_COST_DAY:" + user.getId()).addAndGet(cost);
            long costWeek =
                    redissonClient.getAtomicLong("GRANDPRIX_JOIN_ROOM_COST_WEEK:" + user.getId()).addAndGet(cost);

            log.info("日累计报名：{}，周累计报名：{}", costDay, costWeek);

            // 改变：gretj
            if (redissonClient
                    .<String>getBucket(
                            FishingChallengeFightFishUtil.FISHING_PERSONAL_JCZD0_LIST_BATTERY_LEVEL_USER_PRE + user.getId())
                    .isExists()) {

                redissonClient.getAtomicDouble(FishingChallengeFightFishUtil.FISHING_GRETJ_USER_PRE + user.getId())
                        .addAndGet(cost);

            }

            // 节点变化时，清除 aq相关
            FishingChallengeFightFishUtil.cleanAqData(user.getId());

            // 记录本局外观数据
            final Object userStatusMap = userStatusManager.getUserStatusMap(user.getId(), "batter:view:gp", null);

            if (userStatusMap != null) {

                RedisHelper.set(PLAYER_GRANDPRIX_CONFIG_VB_KEY + user.getId(), userStatusMap.toString());

            }

            final Object userStatusMap1 = userStatusManager.getUserStatusMap(user.getId(), "wing:view:gp", null);

            if (userStatusMap1 != null) {

                RedisHelper.set(PLAYER_GRANDPRIX_CONFIG_VW_KEY + user.getId(), userStatusMap1.toString());

            }

            bullet = maxBullet;
            // 重新初始化用户子弹和游戏局数数据 添加新一局的积分信息
            games++;

            // 添加：新的积分信息
            initGrandprixConfigPointDay(user.getId(), games, bullet);

        }

        // if (games >= 10) {
        // NetManager.sendHintMessageToClient("今日参加大奖赛次数已达上限", user);
        // return;
        // }

        // 初始化：周积分信息
        initGrandprixWeekPoint(user.getId());

        List<FishingGrandPrixRoom> gameRoomList = GameContainer.getGameRooms(FishingGrandPrixRoom.class);
        for (FishingGrandPrixRoom gameRoom : gameRoomList) {
            // 加入房间条件: 1:目标房间场次与玩家所选场次相同 2:房间人数未满
            if (gameRoom.getMaxSize() > gameRoom.getPlayerSize()) {
                synchronized (gameRoom) {
                    joinFishingRoom(user, gameRoom);
                }
                return;
            }
        }

        // 没有房间就新建一个房间
        FishingGrandPrixRoom gameRoom = createFishingRoom();
        synchronized (gameRoom) {
            joinFishingRoom(user, gameRoom);
        }

    }

    /**
     * 初始化：周积分信息
     */
    public static void initGrandprixWeekPoint(long userId) {

        List<String> weekPoint = RedisUtil.getList(PLAYER_GRANDPRIX_CONFIG_POINT_WEEK_KEY + userId);

        if (weekPoint == null || weekPoint.size() == 0) {
            RedisUtil.rightPush(PLAYER_GRANDPRIX_CONFIG_POINT_WEEK_KEY + userId, "0", "0", "0", "0", "0", "0", "0");
        }

    }

    /**
     * 添加：新的积分信息
     */
    public static void initGrandprixConfigPointDay(long userId, int games, int bullet) {

        RedisUtil.rightPush(PLAYER_GRANDPRIX_CONFIG_POINT_DAY_KEY + userId, "0");
        RedisUtil.rightPush(PLAYER_GRANDPRIX_CONFIG_POINT_DAY_KEY + "bonus:" + userId, "");

        RedisHelper.set(PLAYER_GRANDPRIX_CONFIG_GAMES_KEY + userId, String.valueOf(games));
        RedisHelper.set(PLAYER_GRANDPRIX_CONFIG_BULLET_KEY + userId, String.valueOf(bullet));

    }

    /**
     * 创建一个房间
     */
    public static FishingGrandPrixRoom createFishingRoom() {

        FishingGrandPrixRoom gameRoom = GameContainer.createGameRoom(FishingGrandPrixRoom.class, 4);
        gameRoom.setRoomIndex(MyRefreshFishingUtil.GRAND_PRIX_ROOM_INDEX);

        return gameRoom;

    }

    private void joinFishingRoom(ServerUser user, FishingGrandPrixRoom room) {

        // 检查：是否可以加入房间
        if (!GameUtil.joinRoomCheck(user)) {
            return;
        }

        // if (PlayerManager.getPlayerVipLevel(user) < 4) {
        // NetManager.sendErrorMessageToClient("VIP4才能加入该房间", user);
        // return;
        // }
        // int bullet = RedisUtil.val(PLAYER_GRANDPRIX_CONFIG_BULLET_KEY + user.getId(), 1000);
        // if (bullet == 1000) {
        // RedisUtil.rightPush(PLAYER_GRANDPRIX_CONFIG_POINT_DAY_KEY + user.getId(), "0");
        // }

        GameUtil.joinRoomPre(user.getId(), null);

        long enterMoney = PlayerManager.getPlayerEntity(user).getMoney();
        for (BaseGamePlayer baseGamePlayer : room.getGamePlayers()) {
            if (baseGamePlayer != null) {
                if (baseGamePlayer.getUser().getId() == user.getId()) {
                    FishingGrandPrixPlayer player = room.getGamePlayerById(user.getId());
                    exitRoom(player, room);
                }
            }
        }
        FishingGrandPrixPlayer player = GameContainer.createGamePlayer(room, user, FishingGrandPrixPlayer.class);
        player.setEnterMoney(enterMoney);
        player.setEnterRoomTime(System.currentTimeMillis());

        String vb = RedisUtil.val(PLAYER_GRANDPRIX_CONFIG_VB_KEY + user.getId(), "70");
        player.setViewIndex(new BigDecimal(vb).intValue());

        String vm = RedisUtil.val(PLAYER_GRANDPRIX_CONFIG_VW_KEY + user.getId(), "80");
        player.setWingIndex(new BigDecimal(vm).intValue());
        // String viewIndex = RedisHelper.get("USE_BATTERYVIEW:" + user.getId());
        // if (!viewIndex.isEmpty()) {
        // player.setViewIndex(Integer.valueOf(viewIndex));
        // }

        // 设置玩家在房间内的初始炮台等级
        // 玩家拥有的最高炮台等级
        // long batteryLevel = PlayerManager.getPlayerEntity(user).getBatteryLevel();

        player.setBatteryLevel(2000);
        // int usedBatteryLevel = RedisUtil.val("USER_BATTERY_LEVEL_USED" + player.getUser().getId(), 0);
        // if (usedBatteryLevel > 2000) {
        // long xh1 = new Double(RedisUtil.val("ALL_XH_1-50" + player.getUser().getId(), 0D)).longValue();
        // long xh2 = new Double(RedisUtil.val("ALL_XH_50-100" + player.getUser().getId(), 0D)).longValue();
        // long xh3 = new Double(RedisUtil.val("ALL_XH_100-200" + player.getUser().getId(), 0D)).longValue();
        // long xh4 = new Double(RedisUtil.val("ALL_XH_200-max" + player.getUser().getId(), 0D)).longValue();
        // long cx = new Double(RedisUtil.val("ALL_CX_USER" + player.getUser().getId(), 0D)).longValue();
        // RedisHelper.set("ALL_CX_USER" + player.getUser().getId(), String.valueOf(cx - xh1 - xh2 - xh3 - xh4));
        // RedisHelper.set("ALL_XH_1-50" + player.getUser().getId(), "0");
        // RedisHelper.set("ALL_XH_50-100" + player.getUser().getId(), "0");
        // RedisHelper.set("ALL_XH_100-200" + player.getUser().getId(), "0");
        // RedisHelper.set("ALL_XH_200-max" + player.getUser().getId(), "0");
        // }
        RedisHelper.set("USER_BATTERY_LEVEL_USED" + player.getUser().getId(), String.valueOf(2000));
        // int x = CommonLobbyManager.getUserT(user,13);
        // RedisHelper.set("USER_T_PEAK_VALUE"+user.getId(),String.valueOf(x));
        // joinchangePeak(user, 13);

        sendJoinRoomResponse(room, player); // 发送加入房间消息
        sendPlayersInfoResponse(room, player); // 发送玩家数据列表消息
        sendSynchroniseResponse(room, player); // 发送同步鱼消息
        sendFrozenMessage(room, player); // 发送房间当前冰冻消息
        sendRoomPlayerInfoResponse(room, player); // 将自己的数据广播到房间所有玩家

        // MyRefreshFishingUtil
        // .sendRoomMessage(room, OseeMessage.OseeMsgCode.S_C_TTMY_FISHING_GRAND_PRIX_ROOM_PLAYER_INFO_RESPONSE_VALUE,
        // createGrandPrixRoomPlayerInfoResponse(player, room));

    }

    /**
     * 给房间内所有玩家发送某玩家信息
     */
    public static void sendRoomPlayerInfoResponse(NewBaseFishingRoom room, FishingGrandPrixPlayer player) {

        FishingGrandPrixRoomPlayerInfoResponse.Builder builder = FishingGrandPrixRoomPlayerInfoResponse.newBuilder();
        builder.setPlayerInfo(createGrandPrixRoomPlayerInfoResponse(player, room));

        MyRefreshFishingUtil.sendRoomMessage(room,
                OseeMessage.OseeMsgCode.S_C_TTMY_FISHING_GRAND_PRIX_ROOM_PLAYER_INFO_RESPONSE_VALUE, builder);

    }

    public static FishingPlayerInfoMessage.Builder createGrandPrixRoomPlayerInfoResponse(FishingGrandPrixPlayer player,
                                                                                         NewBaseFishingRoom room) {

        FishingPlayerInfoMessage.Builder builder = FishingPlayerInfoMessage.newBuilder();

        builder.setPlayerId(player.getId());
        builder.setName(player.getUser().getNickname());
        // builder.setHeadIndex(player.getUser().getEntity().getHeadIndex());
        // builder.setHeadUrl(player.getUser().getEntity().getHeadUrl());
        // builder.setSex(player.getUser().getEntity().getSex());
        builder.setMoney(player.getMoney());
        builder.setSeat(player.getSeat());
        builder.setOnline(player.getUser().isOnline());
        builder.setVipLevel(player.getVipLevel());
        // builder.setViewIndex(player.getViewIndex());
        // builder.setWingIndex(player.getWingIndex());

        builder.setBatteryLevel((int) player.getBatteryLevel());
        builder.setBatteryMult(player.getBatteryMult());
        builder.setLevel(player.getLevel());
        builder.setDiamond(String.valueOf(player.getDiamond()));

        final Object userStatusMap = userStatusManager.getUserStatusMap(player.getId(), "batter:view:gp", null);

        if (userStatusMap != null) {

            builder.setViewIndex(Integer.valueOf("" + userStatusMap));

        }

        final Object userStatusMap1 = userStatusManager.getUserStatusMap(player.getId(), "wing:view:gp", null);

        if (userStatusMap1 != null) {

            builder.setWingIndex(Integer.valueOf("" + userStatusMap1));

        }

        return builder;

    }

    /**
     * 向玩家发送房间当前的冰冻消息
     */
    public void sendFrozenMessage(FishingGrandPrixRoom gameRoom, FishingGrandPrixPlayer player) {

        FishingHelper.sendFrozenMessage(gameRoom, player.getUser());

        // long nowTime = System.currentTimeMillis();
        // if (nowTime - gameRoom.getLastRoomFrozenTime() < FishingManager.SKILL_FROZEN_TIME) { // 房间处于冰冻状态
        // FishingGrandPrixUseSkillResponse.Builder builder = FishingGrandPrixUseSkillResponse.newBuilder();
        // builder.setSkillId(ItemId.SKILL_FROZEN.getId()); // 冰冻
        // builder.setDuration(
        // (int)((FishingManager.SKILL_FROZEN_TIME - (nowTime - gameRoom.getLastRoomFrozenTime())) / 1000));
        //
        // // 新冰冻
        // gameRoom.getFishMap().values().stream()
        // .filter(it -> nowTime - it.getLastFishFrozenTime() < FishingManager.SKILL_FROZEN_TIME).forEach(it -> {
        // builder.addFishIds(it.getId());
        // builder.addRemainDurations(
        // (int)(FishingManager.SKILL_FROZEN_TIME - (nowTime - it.getLastFishFrozenTime())));
        // });
        //
        // NetManager
        // .sendMessage(OseeMessage.OseeMsgCode.S_C_TTMY_FISHING_GRAND_PRIX_USE_SKILL_RESPONSE_VALUE, builder,
        // player.getUser());
        // }

    }

    /**
     * 发送玩家加入房间消息
     */
    private void sendJoinRoomResponse(FishingGrandPrixRoom gameRoom, FishingGrandPrixPlayer player) {

        FishingGrandPrixJoinRoomResponse.Builder builder = FishingGrandPrixJoinRoomResponse.newBuilder();


        builder.setRoomCode(gameRoom.getCode());

        Integer games = RedisUtil.val(PLAYER_GRANDPRIX_CONFIG_GAMES_KEY + player.getId(), 0);

        builder.setBullet(RedisUtil.val(PLAYER_GRANDPRIX_CONFIG_BULLET_KEY + player.getId(), 1000));

        builder.setDayPoint(RedisUtil.get(PLAYER_GRANDPRIX_CONFIG_POINT_DAY_KEY + player.getId(), games - 1));

        NetManager.sendMessage(OseeMessage.OseeMsgCode.S_C_TTMY_FISHING_GRAND_PRIX_JOIN_ROOM_RESPONSE_VALUE, builder,
                player.getUser());

    }

    /**
     * 发送玩家列表消息
     */
    public static void sendPlayersInfoResponse(NewBaseFishingRoom room, FishingGrandPrixPlayer player) {

        FishingGrandPrixRoomPlayerInfoListResponse.Builder builder =
                FishingGrandPrixRoomPlayerInfoListResponse.newBuilder();

        for (BaseGamePlayer gamePlayer : room.getGamePlayers()) {

            if (gamePlayer != null) {

                long enterMoney = PlayerManager.getPlayerEntity(gamePlayer.getUser()).getMoney();

                FishingGrandPrixPlayer fishingGrandPrixPlayer = (FishingGrandPrixPlayer) gamePlayer;

                fishingGrandPrixPlayer.setEnterMoney(enterMoney);

                builder.addPlayerInfos(createPlayerInfoMessage(fishingGrandPrixPlayer, room));

            }

        }

        if (room.getGamePlayers().length == 0) {
            return;
        }

        NetManager.sendMessage(OseeMessage.OseeMsgCode.S_C_TTMY_FISHING_GRAND_PRIX_ROOM_PLAYER_INFO_LIST_RESPONSE_VALUE,
                builder, player.getUser());

        for (BaseGamePlayer gamePlayer : room.getGamePlayers()) {

            if (gamePlayer != null) {

                long enterMoney = PlayerManager.getPlayerEntity(gamePlayer.getUser()).getMoney();

                FishingGrandPrixPlayer fishingGrandPrixPlayer = (FishingGrandPrixPlayer) gamePlayer;

                fishingGrandPrixPlayer.setEnterMoney(enterMoney);

                if (player.getUser().getId() != gamePlayer.getUser().getId()) { // 发送其他人的道具使用情况

                    FishingGrandPrixUseSkillResponse.Builder skillBuilder =
                            FishingGrandPrixUseSkillResponse.newBuilder();

                    skillBuilder.setPlayerId(fishingGrandPrixPlayer.getId());

                    if (fishingGrandPrixPlayer.getLastCritTime() != 0) { // 开启暴击
                        skillBuilder.setSkillId(ItemId.SKILL_CRIT.getId());
                        skillBuilder.setDuration(Integer.MAX_VALUE);
                        NetManager.sendMessage(
                                OseeMessage.OseeMsgCode.S_C_TTMY_FISHING_CHALLENGE_USE_SKILL_RESPONSE_VALUE, skillBuilder,
                                player.getUser());
                    }

                    if (fishingGrandPrixPlayer.getLastElectromagneticTime() != 0) {// 开启电磁炮
                        skillBuilder.setSkillId(ItemId.SKILL_ELETIC.getId());
                        skillBuilder.setDuration(Integer.MAX_VALUE);
                        NetManager.sendMessage(
                                OseeMessage.OseeMsgCode.S_C_TTMY_FISHING_CHALLENGE_USE_SKILL_RESPONSE_VALUE, skillBuilder,
                                player.getUser());
                    }

                    if (fishingGrandPrixPlayer.getLastLockTime() != 0) {// 开启锁定
                        skillBuilder.setSkillId(ItemId.SKILL_LOCK.getId());
                        skillBuilder.setDuration(Integer.MAX_VALUE);
                        NetManager.sendMessage(
                                OseeMessage.OseeMsgCode.S_C_TTMY_FISHING_CHALLENGE_USE_SKILL_RESPONSE_VALUE, skillBuilder,
                                player.getUser());
                    }

                }

            }

        }

    }

    public static FishingPlayerInfoMessage.Builder createPlayerInfoMessage(FishingGrandPrixPlayer player,
                                                                           NewBaseFishingRoom room) {

        FishingPlayerInfoMessage.Builder builder = FishingPlayerInfoMessage.newBuilder();

        builder.setPlayerId(player.getId());


        OseePlayerEntity oseePlayerEntity = PlayerManager.getPlayerEntity(UserContainer.getUserById(player.getId()));


        builder.setDiamond(String.valueOf(oseePlayerEntity.getDiamond()));

        builder.setName(player.getUser().getNickname());
        builder.setMoney(player.getMoney());
        builder.setSeat(player.getSeat());
        builder.setOnline(player.getUser().isOnline());
        builder.setVipLevel(player.getVipLevel());
        builder.setBatteryLevel((int) player.getBatteryLevel());
        builder.setBatteryMult(player.getBatteryMult());
        builder.setLevel(player.getLevel());

        final Object userStatusMap = userStatusManager.getUserStatusMap(player.getId(), "batter:view:gp", null);

        if (userStatusMap != null) {

            builder.setViewIndex(Integer.valueOf("" + userStatusMap));

        }

        final Object userStatusMap1 = userStatusManager.getUserStatusMap(player.getId(), "wing:view:gp", null);

        if (userStatusMap1 != null) {

            builder.setWingIndex(Integer.valueOf("" + userStatusMap1));

        }

        return builder;

    }

    /**
     * 发送同步鱼消息
     */
    public void sendSynchroniseResponse(FishingGrandPrixRoom gameRoom, FishingGrandPrixPlayer player) {

        FishingGrandPrixSynchroniseResponse.Builder builder = FishingGrandPrixSynchroniseResponse.newBuilder();

        for (FishStruct fish : gameRoom.getFishMap().values()) {

            builder.addFishInfos(MyRefreshFishingHelper.createFishInfoProtoForGrandPrix(fish));
        }

        NetManager.sendMessage(OseeMessage.OseeMsgCode.S_C_TTMY_FISHING_GRAND_PRIX_SYNCHRONISE_RESPONSE_VALUE, builder,
                player.getUser());

    }

    /**
     * 发送电磁炮使用
     */
    public void useEle(long fishId, FishingGrandPrixRoom gameRoom, ServerUser user) {
        OseePublicData.UseEleResponse.Builder builder = OseePublicData.UseEleResponse.newBuilder();
        for (BaseGamePlayer gamePlayer : gameRoom.getGamePlayers()) {
            if (gamePlayer != null) {
                ServerUser user1 = UserContainer.getUserById(gamePlayer.getId());
                builder.setFishId(fishId);
                builder.setUserId(user.getId());
                NetManager.sendMessage(OseeMessage.OseeMsgCode.S_C_GRANDPRIX_USE_ELE_RESPONSE_VALUE, builder, user1);
            }
        }
    }

    /**
     * 发送黑洞炮使用
     */
    public void useBlack(float x, float y, FishingGrandPrixRoom gameRoom, ServerUser user) {
        OseePublicData.UseBlackResponse.Builder builder = OseePublicData.UseBlackResponse.newBuilder();
        for (BaseGamePlayer gamePlayer : gameRoom.getGamePlayers()) {
            if (gamePlayer != null) {
                ServerUser user1 = UserContainer.getUserById(gamePlayer.getId());
                builder.setX(x);
                builder.setY(y);
                builder.setUserId(user.getId());
                NetManager.sendMessage(OseeMessage.OseeMsgCode.S_C_GRANDPRIX_USE_BLACK_RESPONSE_VALUE, builder, user1);
            }
        }
    }

    /**
     * 发送黑洞炮使用
     */
    public void useTro(float x, float y, FishingGrandPrixRoom gameRoom, ServerUser user) {
        OseePublicData.UseTroResponse.Builder builder = OseePublicData.UseTroResponse.newBuilder();
        for (BaseGamePlayer gamePlayer : gameRoom.getGamePlayers()) {
            if (gamePlayer != null) {
                ServerUser user1 = UserContainer.getUserById(gamePlayer.getId());
                builder.setX(x);
                builder.setY(y);
                builder.setUserId(user.getId());
                NetManager.sendMessage(OseeMessage.OseeMsgCode.S_C_GRANDPRIX_USE_TRO_RESPONSE_VALUE, builder, user1);
            }
        }
    }

    /**
     * 发送钻头使用
     */
    public void useBit(float angle, FishingGrandPrixRoom gameRoom, ServerUser user) {
        OseePublicData.UseBitResponse.Builder builder = OseePublicData.UseBitResponse.newBuilder();
        for (BaseGamePlayer gamePlayer : gameRoom.getGamePlayers()) {
            if (gamePlayer != null) {
                ServerUser user1 = UserContainer.getUserById(gamePlayer.getId());
                builder.setAngle(angle);
                builder.setUserId(user.getId());
                NetManager.sendMessage(OseeMessage.OseeMsgCode.S_C_GRANDPRIX_USE_BIT_RESPONSE_VALUE, builder, user1);
            }
        }
    }

    public void getPlayerInfo(String playerId, ServerUser user) {

        FishingGrandPrixPlayerInfoResponse.Builder builder = FishingGrandPrixPlayerInfoResponse.newBuilder();

        builder.setPlayerInfo(createGrandPrixPlayerInfoMessage(Long.parseLong(playerId)));

        NetManager.sendMessage(OseeMessage.OseeMsgCode.S_C_TTMY_FINSHING_GRAND_PRIX_PLAYER_INFO_RESPONSE_VALUE, builder,
                user);

        // 发送玩家外观信息
        playerAppearanceInformation(user, null);

    }

    /**
     * 获取周积分
     *
     * @param playerId
     * @return
     */
    private int getPointWeek(long playerId) {
        return getPoint(PLAYER_GRANDPRIX_CONFIG_RANK_WEEK_KEY, String.valueOf(playerId));
    }

    /**
     * 获取今日最高积分
     *
     * @param playerId
     * @return
     */
    private static int getPointDay(long playerId) {
        return getPoint(PLAYER_GRANDPRIX_CONFIG_RANK_DAY_KEY, String.valueOf(playerId));
    }

    /**
     * 获取今日最高积分加成信息(翅膀,炮台,游戏次数)
     *
     * @param playerId
     * @return
     */
    private String getPointBonusDay(long playerId, long point) {

        int indexOf = RedisHelper.redissonClient.getList(PLAYER_GRANDPRIX_CONFIG_POINT_DAY_KEY + playerId)
                .indexOf(String.valueOf(point));

        RList<String> list =
                RedisHelper.redissonClient.getList(PLAYER_GRANDPRIX_CONFIG_POINT_DAY_KEY + "bonus:" + playerId);

        String s = list.get(indexOf);

        return s == null ? "" : s;

    }

    private static int getPoint(String key, String playerId) {

        Double point = RedisUtil.zScore(key, playerId);
        return point == null ? 0 : point.intValue();

    }

    private FishingGrandPrixPlayerInfoMessage.Builder createGrandPrixPlayerInfoMessage(long playerId) {

        FishingGrandPrixPlayerInfoMessage.Builder builder = FishingGrandPrixPlayerInfoMessage.newBuilder();

        builder.setPlayerId(playerId);

        int pointDay = getPointDay(playerId);

        builder.setDayPoint(pointDay);

        String[] split = getPointBonusDay(playerId, pointDay).split(",");
        if (split.length == 3) {
            builder.setWingBonus(Double.parseDouble(split[0]));
            builder.setBatteryBonus(Double.parseDouble(split[1]));
            builder.setGameBonus(Double.parseDouble(split[2]));
        }

        builder.setWeekPoint(getPointWeek(playerId));
        Set<String> ranksId = RedisUtil.values(PLAYER_GRANDPRIX_CONFIG_RANK_DAY_KEY, 0, 50 - 1);
        int index = 0;

        boolean contains = ranksId.contains(String.valueOf(playerId));
        if (contains) {
            for (String s : ranksId) {
                index++;
                if (s.equals(String.valueOf(playerId))) {
                    builder.setRank(index);
                    break;
                }

            }
        } else {
            builder.setRank(0);
        }

        // 玩家奖励
        return builder;

    }

    /**
     * 玩家发射子弹处理
     */
    public void playerFire(FishingGrandPrixRoom gameRoom, FishingGrandPrixPlayer player, FireStruct fire) {

        synchronized (player) {

            player.getUser().getEntity().setOnlineState(gameRoom.getGameId()); // 防止：在线状态不准确

            int bullet = RedisUtil.val(PLAYER_GRANDPRIX_CONFIG_BULLET_KEY + player.getId(), 1000);

            if (System.currentTimeMillis() - player.getLastFenShenTime() < SKILL_FEN_SHEN_TIME) { // 还在分身阶段就要扣三发子弹的钱

                fire.setCount(3);

            }

            // 子弹发射完 不对其进行处理
            if (bullet - fire.getCount() < 0) {
                return;
            }

            bullet = bullet - fire.getCount();

            RedisHelper.set(PLAYER_GRANDPRIX_CONFIG_BULLET_KEY + player.getId(), String.valueOf(bullet));

//            player.lastFireTime.put(player.getId(),System.currentTimeMillis());
            player.setLastFireTime(System.currentTimeMillis());
//            log.info("执行6：{} : {} ",player.getId(), System.currentTimeMillis());
            fire.setLevel(player.getBatteryLevel());
            fire.setMult(player.getBatteryMult());
            player.getFireMap().put(fire.getId(), fire);

            OseePlayerEntity oseePlayerEntity =
                    PlayerManager.getPlayerEntity(UserContainer.getUserById(player.getId()));

            // 广播玩家发送子弹响应
            doFishingGrandPrixFireResponse(gameRoom, player, fire.getId(), fire.getFishId(), fire.getAngle(),
                    oseePlayerEntity.getMoney(), oseePlayerEntity.getDiamond(), bullet);

        }

    }

    public List<FishConfig> playerFightFish(FishingGrandPrixRoom gameRoom, FishingGrandPrixPlayer player, long fireId,
                                            List<Long> fishIds, boolean b, boolean isTymFish) {
        try {
            List<FishConfig> configs = fightFish(gameRoom, player, fireId, fishIds, b, isTymFish);
            if (configs != null) {
                for (FishConfig config : configs) {
                    // 玩家加经验
                    FishingManager.addExperience(player.getUser(), config.getExp());
                }
            }
            return configs;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<FishConfig> grandPrixFightFish(FishingGrandPrixRoom gameRoom, FishingGrandPrixPlayer player,
                                               List<Long> fishIds, boolean b, boolean isTymFish, long specialFishId) {
        try {
            List<FishConfig> configs = grandPrixFightFish1(gameRoom, player, fishIds, b, isTymFish, specialFishId);
            if (configs != null) {
                for (FishConfig config : configs) {
                    // 玩家加经验
                    FishingManager.addExperience(player.getUser(), config.getExp());
                }
            }
            return configs;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 玩家击中鱼的各种逻辑判断
     */
    public static List<FishConfig> fightFish(FishingGrandPrixRoom gameRoom, FishingGrandPrixPlayer player, long fireId,
                                             List<Long> fishIds, boolean boom, boolean isTymFish) {
        List<FishConfig> configs = new LinkedList<>();
        synchronized (gameRoom) {
            // 非爆炸状态，且房间内不存在指定子弹
            FireStruct fire = null;
            if (!boom) {
                if (!player.getFireMap().containsKey(fireId)) {
                    return null;
                }

                fire = player.getFireMap().get(fireId);
                fire.setCount(fire.getCount() - 1);
                if (fire.getCount() <= 0) { // 该发子弹是否打完了
                    player.getFireMap().remove(fireId);
                }
            }

            for (Long fishId : fishIds) {
                // 鱼id不存在
                if (!gameRoom.getFishMap().containsKey(fishId)) {
                    continue;
                }

                FishStruct fish = gameRoom.getFishMap().get(fishId);
                FishConfig config = DataContainer.getData(fish.getConfigId(), FishConfig.class);

                // boss鱼无法因爆炸死亡
                if (boom && config.getFishType() == 100) {
                    continue;
                }

                long winMoney;

                long randomMoney = config.getMaxMoney() > config.getMoney()
                        ? ThreadLocalRandom.current().nextLong(config.getMoney(), config.getMaxMoney() + 1)
                        : config.getMoney();
                if (config.getFishType() == 100 || config.getFishType() == 50) {
                    if (config.getModelId() == 48) {
                        if (RedisUtil.val("USER_BOSS_MULT_48" + player.getUser().getId(), 0L) != 0) {
                            randomMoney = RedisUtil.val("USER_BOSS_MULT_48" + player.getUser().getId(), 0L);
                        } else {
                            RedisHelper.set("USER_BOSS_MULT_48" + player.getUser().getId(),
                                    String.valueOf(randomMoney));
                        }
                    } else if (config.getModelId() == 49) {
                        if (RedisUtil.val("USER_BOSS_MULT_49" + player.getUser().getId(), 0L) != 0) {
                            randomMoney = RedisUtil.val("USER_BOSS_MULT_49" + player.getUser().getId(), 0L);
                        } else {
                            RedisHelper.set("USER_BOSS_MULT_49" + player.getUser().getId(),
                                    String.valueOf(randomMoney));
                        }
                    } else if (config.getModelId() == 50) {
                        if (RedisUtil.val("USER_BOSS_MULT_50" + player.getUser().getId(), 0L) != 0) {
                            randomMoney = RedisUtil.val("USER_BOSS_MULT_50" + player.getUser().getId(), 0L);
                        } else {
                            RedisHelper.set("USER_BOSS_MULT_50" + player.getUser().getId(),
                                    String.valueOf(randomMoney));
                        }
                    } else if (config.getModelId() == 51) {
                        if (RedisUtil.val("USER_BOSS_MULT_51" + player.getUser().getId(), 0L) != 0) {
                            randomMoney = RedisUtil.val("USER_BOSS_MULT_51" + player.getUser().getId(), 0L);
                        } else {
                            RedisHelper.set("USER_BOSS_MULT_51" + player.getUser().getId(),
                                    String.valueOf(randomMoney));
                        }
                    } else if (config.getModelId() == 52) {
                        if (RedisUtil.val("USER_BOSS_MULT_52" + player.getUser().getId(), 0L) != 0) {
                            randomMoney = RedisUtil.val("USER_BOSS_MULT_52" + player.getUser().getId(), 0L);
                        } else {
                            RedisHelper.set("USER_BOSS_MULT_52" + player.getUser().getId(),
                                    String.valueOf(randomMoney));
                        }
                    } else if (config.getModelId() == 53) {
                        if (RedisUtil.val("USER_BOSS_MULT_53" + player.getUser().getId(), 0L) != 0) {
                            randomMoney = RedisUtil.val("USER_BOSS_MULT_53" + player.getUser().getId(), 0L);
                        } else {
                            RedisHelper.set("USER_BOSS_MULT_53" + player.getUser().getId(),
                                    String.valueOf(randomMoney));
                        }
                    } else if (config.getModelId() == 54) {
                        if (RedisUtil.val("USER_BOSS_MULT_54" + player.getUser().getId(), 0L) != 0) {
                            randomMoney = RedisUtil.val("USER_BOSS_MULT_54" + player.getUser().getId(), 0L);
                        } else {
                            RedisHelper.set("USER_BOSS_MULT_54" + player.getUser().getId(),
                                    String.valueOf(randomMoney));
                        }
                    }
                }
                if (config.getModelId() == 28) {
                    if (RedisUtil.val("USER_BOSS_MULT_28" + player.getUser().getId(), 0L) != 0) {
                        randomMoney = RedisUtil.val("USER_BOSS_MULT_28" + player.getUser().getId(), 0L);
                    } else {
                        RedisHelper.set("USER_BOSS_MULT_28" + player.getUser().getId(), String.valueOf(randomMoney));
                    }
                } else if (config.getModelId() == 29) {
                    if (RedisUtil.val("USER_BOSS_MULT_29" + player.getUser().getId(), 0L) != 0) {
                        randomMoney = RedisUtil.val("USER_BOSS_MULT_29" + player.getUser().getId(), 0L);
                    } else {
                        RedisHelper.set("USER_BOSS_MULT_29" + player.getUser().getId(), String.valueOf(randomMoney));
                    }
                } else if (config.getModelId() == 30) {
                    if (RedisUtil.val("USER_BOSS_MULT_30" + player.getUser().getId(), 0L) != 0) {
                        randomMoney = RedisUtil.val("USER_BOSS_MULT_30" + player.getUser().getId(), 0L);
                    } else {
                        RedisHelper.set("USER_BOSS_MULT_30" + player.getUser().getId(), String.valueOf(randomMoney));
                    }
                } else if (config.getModelId() == 31) {
                    if (RedisUtil.val("USER_BOSS_MULT_31" + player.getUser().getId(), 0L) != 0) {
                        randomMoney = RedisUtil.val("USER_BOSS_MULT_31" + player.getUser().getId(), 0L);
                    } else {
                        RedisHelper.set("USER_BOSS_MULT_31" + player.getUser().getId(), String.valueOf(randomMoney));
                    }
                } else if (config.getModelId() == 32) {
                    if (RedisUtil.val("USER_BOSS_MULT_32" + player.getUser().getId(), 0L) != 0) {
                        randomMoney = RedisUtil.val("USER_BOSS_MULT_32" + player.getUser().getId(), 0L);
                    } else {
                        RedisHelper.set("USER_BOSS_MULT_32" + player.getUser().getId(), String.valueOf(randomMoney));
                    }
                } else if (config.getModelId() == 33) {
                    if (RedisUtil.val("USER_BOSS_MULT_33" + player.getUser().getId(), 0L) != 0) {
                        randomMoney = RedisUtil.val("USER_BOSS_MULT_33" + player.getUser().getId(), 0L);
                    } else {
                        RedisHelper.set("USER_BOSS_MULT_33" + player.getUser().getId(), String.valueOf(randomMoney));
                    }
                } else if (config.getModelId() == 34) {
                    if (RedisUtil.val("USER_BOSS_MULT_34" + player.getUser().getId(), 0L) != 0) {
                        randomMoney = RedisUtil.val("USER_BOSS_MULT_34" + player.getUser().getId(), 0L);
                    } else {
                        RedisHelper.set("USER_BOSS_MULT_34" + player.getUser().getId(), String.valueOf(randomMoney));
                    }
                } else if (config.getModelId() == 35) {
                    if (RedisUtil.val("USER_BOSS_MULT_35" + player.getUser().getId(), 0L) != 0) {
                        randomMoney = RedisUtil.val("USER_BOSS_MULT_35" + player.getUser().getId(), 0L);
                    } else {
                        RedisHelper.set("USER_BOSS_MULT_35" + player.getUser().getId(), String.valueOf(randomMoney));
                    }
                } else if (config.getModelId() == 36) {
                    if (RedisUtil.val("USER_BOSS_MULT_36" + player.getUser().getId(), 0L) != 0) {
                        randomMoney = RedisUtil.val("USER_BOSS_MULT_36" + player.getUser().getId(), 0L);
                    } else {
                        RedisHelper.set("USER_BOSS_MULT_36" + player.getUser().getId(), String.valueOf(randomMoney));
                    }
                } else if (config.getModelId() == 37) {
                    if (RedisUtil.val("USER_BOSS_MULT_37" + player.getUser().getId(), 0L) != 0) {
                        randomMoney = RedisUtil.val("USER_BOSS_MULT_37" + player.getUser().getId(), 0L);
                    } else {
                        RedisHelper.set("USER_BOSS_MULT_37" + player.getUser().getId(), String.valueOf(randomMoney));
                    }
                } else if (config.getModelId() == 38) {
                    if (RedisUtil.val("USER_BOSS_MULT_38" + player.getUser().getId(), 0L) != 0) {
                        randomMoney = RedisUtil.val("USER_BOSS_MULT_38" + player.getUser().getId(), 0L);
                    } else {
                        RedisHelper.set("USER_BOSS_MULT_38" + player.getUser().getId(), String.valueOf(randomMoney));
                    }
                } else if (config.getModelId() == 39) {
                    if (RedisUtil.val("USER_BOSS_MULT_39" + player.getUser().getId(), 0L) != 0) {
                        randomMoney = RedisUtil.val("USER_BOSS_MULT_39" + player.getUser().getId(), 0L);
                    } else {
                        RedisHelper.set("USER_BOSS_MULT_39" + player.getUser().getId(), String.valueOf(randomMoney));
                    }
                } else if (config.getModelId() == 40) {
                    if (RedisUtil.val("USER_BOSS_MULT_40" + player.getUser().getId(), 0L) != 0) {
                        randomMoney = RedisUtil.val("USER_BOSS_MULT_40" + player.getUser().getId(), 0L);
                    } else {
                        RedisHelper.set("USER_BOSS_MULT_40" + player.getUser().getId(), String.valueOf(randomMoney));
                    }
                } else if (config.getModelId() == 41) {
                    if (RedisUtil.val("USER_BOSS_MULT_41" + player.getUser().getId(), 0L) != 0) {
                        randomMoney = RedisUtil.val("USER_BOSS_MULT_41" + player.getUser().getId(), 0L);
                    } else {
                        RedisHelper.set("USER_BOSS_MULT_41" + player.getUser().getId(), String.valueOf(randomMoney));
                    }
                } else if (config.getModelId() == 42) {
                    if (RedisUtil.val("USER_BOSS_MULT_42" + player.getUser().getId(), 0L) != 0) {
                        randomMoney = RedisUtil.val("USER_BOSS_MULT_42" + player.getUser().getId(), 0L);
                    } else {
                        RedisHelper.set("USER_BOSS_MULT_42" + player.getUser().getId(), String.valueOf(randomMoney));
                    }
                } else if (config.getModelId() == 43) {
                    if (RedisUtil.val("USER_BOSS_MULT_43" + player.getUser().getId(), 0L) != 0) {
                        randomMoney = RedisUtil.val("USER_BOSS_MULT_43" + player.getUser().getId(), 0L);
                    } else {
                        RedisHelper.set("USER_BOSS_MULT_43" + player.getUser().getId(), String.valueOf(randomMoney));
                    }
                } else if (config.getModelId() == 44) {
                    if (RedisUtil.val("USER_BOSS_MULT_44" + player.getUser().getId(), 0L) != 0) {
                        randomMoney = RedisUtil.val("USER_BOSS_MULT_44" + player.getUser().getId(), 0L);
                    } else {
                        RedisHelper.set("USER_BOSS_MULT_44" + player.getUser().getId(), String.valueOf(randomMoney));
                    }
                } else if (config.getModelId() == 45) {
                    if (RedisUtil.val("USER_BOSS_MULT_45" + player.getUser().getId(), 0L) != 0) {
                        randomMoney = RedisUtil.val("USER_BOSS_MULT_45" + player.getUser().getId(), 0L);
                    } else {
                        RedisHelper.set("USER_BOSS_MULT_45" + player.getUser().getId(), String.valueOf(randomMoney));
                    }
                } else if (config.getModelId() == 46) {
                    if (RedisUtil.val("USER_BOSS_MULT_46" + player.getUser().getId(), 0L) != 0) {
                        randomMoney = RedisUtil.val("USER_BOSS_MULT_46" + player.getUser().getId(), 0L);
                    } else {
                        RedisHelper.set("USER_BOSS_MULT_46" + player.getUser().getId(), String.valueOf(randomMoney));
                    }
                } else if (config.getModelId() == 47) {
                    if (RedisUtil.val("USER_BOSS_MULT_47" + player.getUser().getId(), 0L) != 0) {
                        randomMoney = RedisUtil.val("USER_BOSS_MULT_47" + player.getUser().getId(), 0L);
                    } else {
                        RedisHelper.set("USER_BOSS_MULT_47" + player.getUser().getId(), String.valueOf(randomMoney));
                    }
                } else if (config.getModelId() == 55) {
                    if (RedisUtil.val("USER_BOSS_MULT_55" + player.getUser().getId(), 0L) != 0) {
                        randomMoney = RedisUtil.val("USER_BOSS_MULT_55" + player.getUser().getId(), 0L);
                    } else {
                        RedisHelper.set("USER_BOSS_MULT_55" + player.getUser().getId(), String.valueOf(randomMoney));
                    }
                } else if (config.getModelId() == 56) {
                    if (RedisUtil.val("USER_BOSS_MULT_56" + player.getUser().getId(), 0L) != 0) {
                        randomMoney = RedisUtil.val("USER_BOSS_MULT_56" + player.getUser().getId(), 0L);
                    } else {
                        RedisHelper.set("USER_BOSS_MULT_56" + player.getUser().getId(), String.valueOf(randomMoney));
                    }
                } else if (config.getModelId() == 57) {
                    if (RedisUtil.val("USER_BOSS_MULT_57" + player.getUser().getId(), 0L) != 0) {
                        randomMoney = RedisUtil.val("USER_BOSS_MULT_57" + player.getUser().getId(), 0L);
                    } else {
                        RedisHelper.set("USER_BOSS_MULT_57" + player.getUser().getId(), String.valueOf(randomMoney));
                    }
                } else if (config.getModelId() == 58) {
                    if (RedisUtil.val("USER_BOSS_MULT_58" + player.getUser().getId(), 0L) != 0) {
                        randomMoney = RedisUtil.val("USER_BOSS_MULT_58" + player.getUser().getId(), 0L);
                    } else {
                        RedisHelper.set("USER_BOSS_MULT_58" + player.getUser().getId(), String.valueOf(randomMoney));
                    }
                } else if (config.getModelId() == 59) {
                    if (RedisUtil.val("USER_BOSS_MULT_59" + player.getUser().getId(), 0L) != 0) {
                        randomMoney = RedisUtil.val("USER_BOSS_MULT_59" + player.getUser().getId(), 0L);
                    } else {
                        RedisHelper.set("USER_BOSS_MULT_59" + player.getUser().getId(), String.valueOf(randomMoney));
                    }
                } else if (config.getModelId() == 60) {
                    if (RedisUtil.val("USER_BOSS_MULT_60" + player.getUser().getId(), 0L) != 0) {
                        randomMoney = RedisUtil.val("USER_BOSS_MULT_60" + player.getUser().getId(), 0L);
                    } else {
                        RedisHelper.set("USER_BOSS_MULT_60" + player.getUser().getId(), String.valueOf(randomMoney));
                    }
                } else if (config.getModelId() == 61) {
                    if (RedisUtil.val("USER_BOSS_MULT_61" + player.getUser().getId(), 0L) != 0) {
                        randomMoney = RedisUtil.val("USER_BOSS_MULT_61" + player.getUser().getId(), 0L);
                    } else {
                        RedisHelper.set("USER_BOSS_MULT_61" + player.getUser().getId(), String.valueOf(randomMoney));
                    }
                } else if (config.getModelId() == 62) {
                    if (RedisUtil.val("USER_BOSS_MULT_62" + player.getUser().getId(), 0L) != 0) {
                        randomMoney = RedisUtil.val("USER_BOSS_MULT_62" + player.getUser().getId(), 0L);
                    } else {
                        RedisHelper.set("USER_BOSS_MULT_62" + player.getUser().getId(), String.valueOf(randomMoney));
                    }
                } else if (config.getModelId() == 63) {
                    if (RedisUtil.val("USER_BOSS_MULT_63" + player.getUser().getId(), 0L) != 0) {
                        randomMoney = RedisUtil.val("USER_BOSS_MULT_63" + player.getUser().getId(), 0L);
                    } else {
                        RedisHelper.set("USER_BOSS_MULT_63" + player.getUser().getId(), String.valueOf(randomMoney));
                    }
                } else if (config.getModelId() == 64) {
                    if (RedisUtil.val("USER_BOSS_MULT_64" + player.getUser().getId(), 0L) != 0) {
                        randomMoney = RedisUtil.val("USER_BOSS_MULT_64" + player.getUser().getId(), 0L);
                    } else {
                        RedisHelper.set("USER_BOSS_MULT_64" + player.getUser().getId(), String.valueOf(randomMoney));
                    }
                } else if (config.getModelId() == 65) {
                    if (RedisUtil.val("USER_BOSS_MULT_65" + player.getUser().getId(), 0L) != 0) {
                        randomMoney = RedisUtil.val("USER_BOSS_MULT_65" + player.getUser().getId(), 0L);
                    } else {
                        RedisHelper.set("USER_BOSS_MULT_65" + player.getUser().getId(), String.valueOf(randomMoney));
                    }
                } else if (config.getModelId() == 66) {
                    if (RedisUtil.val("USER_BOSS_MULT_66" + player.getUser().getId(), 0L) != 0) {
                        randomMoney = RedisUtil.val("USER_BOSS_MULT_66" + player.getUser().getId(), 0L);
                    } else {
                        RedisHelper.set("USER_BOSS_MULT_66" + player.getUser().getId(), String.valueOf(randomMoney));
                    }
                }
                if (fire != null) {
                    winMoney = randomMoney * fire.getLevel();
                    // 暴击状态下成功命中鱼类，则获得1.5倍金币奖励
                    // if (System.currentTimeMillis() - player.getLastCritTime() < SKILL_CRIT_TIME) {
                    // winMoney *= 1.5;
                    // }
                } else {
                    if (isTymFish) {
                        long batteryLevel = 0;
                        batteryLevel = player.getBatteryLevel();
                        int batteryMult = 1;
                        // batteryMult = player.getBatteryLevel();
                        winMoney = randomMoney * batteryLevel * batteryMult;
                    } else {
                        winMoney = randomMoney * player.getBatteryLevel() * player.getBatteryMult();
                    }
                }
                long needMoney = player.getBatteryLevel() * player.getBatteryMult();
                long nowTime = System.currentTimeMillis();
                if (nowTime - player.getLastCritTime() < SKILL_CRIT_TIME) {
                    int mult = RedisUtil.val("USER_CRIT_MULT" + player.getUser().getId(), 1);
                    needMoney = mult * needMoney;
                }
                if (nowTime - player.getLastElectromagneticTime() < SKILL_ELETIC_TIME) {
                    needMoney = 2 * needMoney;
                }

                boolean hit = isHit(player, gameRoom.getRoomIndex(), fish, config, needMoney, winMoney, fireId);

                // 测试号
                if (player.getId() < 0) {
                    hit = true;
                }

                if (!boom && !hit) {
                    fish.setFireTimes(fish.getFireTimes() + 1);
                    continue;
                }

                MyRefreshFishingHelper.checkAndDurationRefreshFish(gameRoom, fish, true);

                List<OseePublicData.ItemDataProto> dropItems = new LinkedList<>();
                fish = gameRoom.getFishMap().remove(fishId);
//                gameRoom.removeFishMap(fishId);
                config.setWinMoney(winMoney);
                configs.add(config);
                player.setChangeMoney(player.getChangeMoney() - winMoney);
                player.setWinMoney(player.getWinMoney() - winMoney);
                if (config.getFishType() == 100 || config.getFishType() == 50) {
                    if (config.getModelId() == 48) {
                        RedisHelper.set("USER_BOSS_MULT_48" + player.getUser().getId(), String.valueOf(0));
                    } else if (config.getModelId() == 49) {
                        RedisHelper.set("USER_BOSS_MULT_49" + player.getUser().getId(), String.valueOf(0));
                    } else if (config.getModelId() == 50) {
                        RedisHelper.set("USER_BOSS_MULT_50" + player.getUser().getId(), String.valueOf(0));
                    } else if (config.getModelId() == 51) {
                        RedisHelper.set("USER_BOSS_MULT_51" + player.getUser().getId(), String.valueOf(0));
                    } else if (config.getModelId() == 52) {
                        RedisHelper.set("USER_BOSS_MULT_52" + player.getUser().getId(), String.valueOf(0));
                    } else if (config.getModelId() == 53) {
                        RedisHelper.set("USER_BOSS_MULT_53" + player.getUser().getId(), String.valueOf(0));
                    } else if (config.getModelId() == 54) {
                        RedisHelper.set("USER_BOSS_MULT_54" + player.getUser().getId(), String.valueOf(0));
                    }
                }
                if (config.getModelId() == 28) {
                    RedisHelper.set("USER_BOSS_MULT_28" + player.getUser().getId(), String.valueOf(0));
                } else if (config.getModelId() == 29) {
                    RedisHelper.set("USER_BOSS_MULT_29" + player.getUser().getId(), String.valueOf(0));
                } else if (config.getModelId() == 30) {
                    RedisHelper.set("USER_BOSS_MULT_30" + player.getUser().getId(), String.valueOf(0));
                } else if (config.getModelId() == 31) {
                    RedisHelper.set("USER_BOSS_MULT_31" + player.getUser().getId(), String.valueOf(0));
                } else if (config.getModelId() == 32) {
                    RedisHelper.set("USER_BOSS_MULT_32" + player.getUser().getId(), String.valueOf(0));
                } else if (config.getModelId() == 33) {
                    RedisHelper.set("USER_BOSS_MULT_33" + player.getUser().getId(), String.valueOf(0));
                } else if (config.getModelId() == 34) {
                    RedisHelper.set("USER_BOSS_MULT_34" + player.getUser().getId(), String.valueOf(0));
                } else if (config.getModelId() == 35) {
                    RedisHelper.set("USER_BOSS_MULT_35" + player.getUser().getId(), String.valueOf(0));
                } else if (config.getModelId() == 36) {
                    RedisHelper.set("USER_BOSS_MULT_36" + player.getUser().getId(), String.valueOf(0));
                } else if (config.getModelId() == 37) {
                    RedisHelper.set("USER_BOSS_MULT_37" + player.getUser().getId(), String.valueOf(0));
                } else if (config.getModelId() == 38) {
                    RedisHelper.set("USER_BOSS_MULT_38" + player.getUser().getId(), String.valueOf(0));
                } else if (config.getModelId() == 39) {
                    RedisHelper.set("USER_BOSS_MULT_39" + player.getUser().getId(), String.valueOf(0));
                } else if (config.getModelId() == 40) {
                    RedisHelper.set("USER_BOSS_MULT_40" + player.getUser().getId(), String.valueOf(0));
                } else if (config.getModelId() == 41) {
                    RedisHelper.set("USER_BOSS_MULT_41" + player.getUser().getId(), String.valueOf(0));
                } else if (config.getModelId() == 42) {
                    RedisHelper.set("USER_BOSS_MULT_42" + player.getUser().getId(), String.valueOf(0));
                } else if (config.getModelId() == 43) {
                    RedisHelper.set("USER_BOSS_MULT_43" + player.getUser().getId(), String.valueOf(0));
                } else if (config.getModelId() == 44) {
                    RedisHelper.set("USER_BOSS_MULT_44" + player.getUser().getId(), String.valueOf(0));
                } else if (config.getModelId() == 45) {
                    RedisHelper.set("USER_BOSS_MULT_45" + player.getUser().getId(), String.valueOf(0));
                } else if (config.getModelId() == 46) {
                    RedisHelper.set("USER_BOSS_MULT_46" + player.getUser().getId(), String.valueOf(0));
                } else if (config.getModelId() == 47) {
                    RedisHelper.set("USER_BOSS_MULT_47" + player.getUser().getId(), String.valueOf(0));
                } else if (config.getModelId() == 55) {
                    RedisHelper.set("USER_BOSS_MULT_55" + player.getUser().getId(), String.valueOf(0));
                } else if (config.getModelId() == 56) {
                    RedisHelper.set("USER_BOSS_MULT_56" + player.getUser().getId(), String.valueOf(0));
                } else if (config.getModelId() == 57) {
                    RedisHelper.set("USER_BOSS_MULT_57" + player.getUser().getId(), String.valueOf(0));
                } else if (config.getModelId() == 58) {
                    RedisHelper.set("USER_BOSS_MULT_58" + player.getUser().getId(), String.valueOf(0));
                } else if (config.getModelId() == 59) {
                    RedisHelper.set("USER_BOSS_MULT_59" + player.getUser().getId(), String.valueOf(0));
                } else if (config.getModelId() == 60) {
                    RedisHelper.set("USER_BOSS_MULT_60" + player.getUser().getId(), String.valueOf(0));
                } else if (config.getModelId() == 61) {
                    RedisHelper.set("USER_BOSS_MULT_61" + player.getUser().getId(), String.valueOf(0));
                } else if (config.getModelId() == 62) {
                    RedisHelper.set("USER_BOSS_MULT_62" + player.getUser().getId(), String.valueOf(0));
                } else if (config.getModelId() == 63) {
                    RedisHelper.set("USER_BOSS_MULT_63" + player.getUser().getId(), String.valueOf(0));
                } else if (config.getModelId() == 64) {
                    RedisHelper.set("USER_BOSS_MULT_64" + player.getUser().getId(), String.valueOf(0));
                } else if (config.getModelId() == 65) {
                    RedisHelper.set("USER_BOSS_MULT_65" + player.getUser().getId(), String.valueOf(0));
                } else if (config.getModelId() == 66) {
                    RedisHelper.set("USER_BOSS_MULT_66" + player.getUser().getId(), String.valueOf(0));
                }
                // Boss鱼死亡
                if (config.getFishType() == 100) {
                    if (player.getBatteryLevel() >= 1000) { // 炮台等级要1000倍以上才通报
                        // 进行全服通报
                        FishingGrandPrixCatchBossFishResponse.Builder builder =
                                FishingGrandPrixCatchBossFishResponse.newBuilder();
                        builder.setFishName(config.getName());
                        builder.setMoney(winMoney);
                        builder.setPlayerName(player.getUser().getNickname().substring(0, 2) + "***");
                        builder.setPlayerVipLevel(player.getVipLevel());
                        builder.setBatteryLevel((int) player.getBatteryLevel());
                        sendCatchBossFishResponse(builder);
                    }
                }

                if (config.getSkill() > 0) {
                    if (config.getSkill() > 5 && config.getSkill() < 9) { // 特殊技能鱼
                        FishingGrandPrixUseSkillResponse.Builder builder =
                                FishingGrandPrixUseSkillResponse.newBuilder();
                        builder.setPlayerId(player.getId());

                        if (config.getSkill() == 6) { // 局部爆炸鱼
                            builder.setSkillId(101);
                            builder.setSkillFishId(fishId);
                            RedisHelper.set("WINMONEY_SKILL_FISH" + 101 + player.getUser().getId(),
                                    String.valueOf(winMoney));
                        } else if (config.getSkill() == 7) { // 闪电鱼
                            builder.setSkillId(102);
                            builder.setSkillFishId(fishId);
                            RedisHelper.set("WINMONEY_SKILL_FISH" + 102 + player.getUser().getId(),
                                    String.valueOf(winMoney));
                        } else if (config.getSkill() == 8) { // 黑洞鱼
                            builder.setSkillId(103);
                            builder.setSkillFishId(fishId);
                            RedisHelper.set("WINMONEY_SKILL_FISH" + 103 + player.getUser().getId(),
                                    String.valueOf(winMoney));
                        }
                        winMoney = 0;
                        MyRefreshFishingUtil.sendRoomMessage(gameRoom,
                                OseeMessage.OseeMsgCode.S_C_TTMY_FISHING_GRAND_PRIX_USE_SKILL_RESPONSE_VALUE, builder);
                    } else {
                        if (fireId != -1) {
                            // 玩家实时金币变化
                            // TODO:2022年4月12日不再掉落金币
                            // player.addMoney(winMoney);
                            // log.info("winMoney"+winMoney);
                        }
                    }
                } else {
                    if (fireId != -1) {
                        // 玩家实时金币变化
                        // TODO:2022年4月12日不再掉落金币
                        // player.addMoney(winMoney);
                        // log.info("winMoney"+winMoney);
                    }
                }

                if (config.getFallingMaterials() != null) { // 会掉落物品的鱼
                    String[] fallingMaterials = config.getFallingMaterials().split(",");
                    // 随机掉落的技能数量
                    String[] skillDropMin = config.getMinSkillDropNum().split(",");
                    String[] skillDropMax = config.getMaxSkillDropNum().split(",");
                    // 1锁定//2冰冻//3急速//4暴击//5全屏爆炸//6局部爆炸//7闪电//8黑洞/9奖券//10钻石//11鱼骨/12海妖石//13王魂石
                    // 14海魂石//15珍珠石//16海兽石//17海魔石//18召唤石//19电磁石//20黑洞石//21领主石//22龙骨//23龙珠
                    // 24龙元//25龙脊//26黑洞炮//27电磁炮//28鱼雷炮//29号角//30分身//31核弹
                    // 32稀有核弹33黑铁弹/34青铜弹/35白银弹//36黄金弹
                    // 37赠送卡//38龙晶//39金币
                    int skillDropNum = 0;
                    int skillId = 0;
                    int probabilty = ThreadLocalRandom.current().nextInt(0, 101);
                    long battery = PlayerManager.getPlayerBatteryLevel(player.getUser());
                    int skillDropProbability = config.getSkillDropProbability();
                    if (battery >= 2000 && battery <= 50000) {
                        skillDropProbability = config.getSkillDropProbabilityOne();
                    } else if (battery >= 60000 && battery < 1000000) {
                        skillDropProbability = config.getSkillDropProbabilityTwo();
                    } else if (battery >= 1000000) {
                        skillDropProbability = config.getSkillDropProbabilityMax();
                    }
                    if (probabilty < skillDropProbability) {
                        for (int i = 0; i < fallingMaterials.length; i++) {
                            if (Integer.valueOf(fallingMaterials[i]) == 1) { // 锁定
                                skillDropNum = ThreadLocalRandom.current().nextInt(Integer.valueOf(skillDropMin[i]),
                                        Integer.valueOf(skillDropMax[i]) + 1);
                                if (skillDropNum > 0) {
                                    skillId = ItemId.SKILL_LOCK.getId();
                                }
                            } else if (Integer.valueOf(fallingMaterials[i]) == 2) { // 冰冻
                                skillDropNum = ThreadLocalRandom.current().nextInt(Integer.valueOf(skillDropMin[i]),
                                        Integer.valueOf(skillDropMax[i]) + 1);
                                if (skillDropNum > 0) {
                                    skillId = ItemId.SKILL_FROZEN.getId();
                                }
                            } else if (Integer.valueOf(fallingMaterials[i]) == 3) { // 急速
                                skillDropNum = ThreadLocalRandom.current().nextInt(Integer.valueOf(skillDropMin[i]),
                                        Integer.valueOf(skillDropMax[i]) + 1);
                                if (skillDropNum > 0) {
                                    skillId = ItemId.SKILL_FAST.getId();
                                }
                            } else if (Integer.valueOf(fallingMaterials[i]) == 4) { // 暴击
                                skillDropNum = ThreadLocalRandom.current().nextInt(Integer.valueOf(skillDropMin[i]),
                                        Integer.valueOf(skillDropMax[i]) + 1);
                                if (skillDropNum > 0) {
                                    skillId = ItemId.SKILL_CRIT.getId();
                                }
                            } else if (Integer.valueOf(fallingMaterials[i]) == 9) { // 奖券
                                skillDropNum = ThreadLocalRandom.current().nextInt(Integer.valueOf(skillDropMin[i]),
                                        Integer.valueOf(skillDropMax[i]) + 1);
                                if (skillDropNum > 0) {
                                    skillId = ItemId.LOTTERY.getId();
                                }
                            } else if (Integer.valueOf(fallingMaterials[i]) == 10) { // 钻石
                                skillDropNum = ThreadLocalRandom.current().nextInt(Integer.valueOf(skillDropMin[i]),
                                        Integer.valueOf(skillDropMax[i]) + 1);
                                if (skillDropNum > 0) {
                                    skillId = ItemId.DIAMOND.getId();
                                    // 做任务
                                    FishingTaskManager.doTask(player.getUser(), TaskType.DAILY, GoalType.GET_DIAMOND, 0,
                                            1);
                                }
                            } else if (Integer.valueOf(fallingMaterials[i]) == 11) { // 鱼骨
                                skillDropNum = ThreadLocalRandom.current().nextInt(Integer.valueOf(skillDropMin[i]),
                                        Integer.valueOf(skillDropMax[i]) + 1);
                                if (skillDropNum > 0) {
                                    skillId = ItemId.YU_GU.getId();
                                }
                            } else if (Integer.valueOf(fallingMaterials[i]) == 12) { // 海妖石
                                skillDropNum = ThreadLocalRandom.current().nextInt(Integer.valueOf(skillDropMin[i]),
                                        Integer.valueOf(skillDropMax[i]) + 1);
                                if (skillDropNum > 0) {
                                    skillId = ItemId.HAI_YAO_SHI.getId();
                                }
                            } else if (Integer.valueOf(fallingMaterials[i]) == 13) { // 王魂石
                                skillDropNum = ThreadLocalRandom.current().nextInt(Integer.valueOf(skillDropMin[i]),
                                        Integer.valueOf(skillDropMax[i]) + 1);
                                if (skillDropNum > 0) {
                                    skillId = ItemId.WANG_HUN_SHI.getId();
                                }
                            } else if (Integer.valueOf(fallingMaterials[i]) == 14) { // 海魂石
                                skillDropNum = ThreadLocalRandom.current().nextInt(Integer.valueOf(skillDropMin[i]),
                                        Integer.valueOf(skillDropMax[i]) + 1);
                                if (skillDropNum > 0) {
                                    skillId = ItemId.HAI_HUN_SHI.getId();
                                }
                            } else if (Integer.valueOf(fallingMaterials[i]) == 15) { // 珍珠石
                                skillDropNum = ThreadLocalRandom.current().nextInt(Integer.valueOf(skillDropMin[i]),
                                        Integer.valueOf(skillDropMax[i]) + 1);
                                if (skillDropNum > 0) {
                                    skillId = ItemId.ZHEN_ZHU_SHI.getId();
                                }
                            } else if (Integer.valueOf(fallingMaterials[i]) == 16) { // 海兽石
                                skillDropNum = ThreadLocalRandom.current().nextInt(Integer.valueOf(skillDropMin[i]),
                                        Integer.valueOf(skillDropMax[i]) + 1);
                                if (skillDropNum > 0) {
                                    skillId = ItemId.HAI_SHOU_SHI.getId();
                                }
                            } else if (Integer.valueOf(fallingMaterials[i]) == 17) { // 海魔石
                                skillDropNum = ThreadLocalRandom.current().nextInt(Integer.valueOf(skillDropMin[i]),
                                        Integer.valueOf(skillDropMax[i]) + 1);
                                if (skillDropNum > 0) {
                                    skillId = ItemId.HAI_MO_SHI.getId();
                                }
                            } else if (Integer.valueOf(fallingMaterials[i]) == 18) { // 召唤石
                                skillDropNum = ThreadLocalRandom.current().nextInt(Integer.valueOf(skillDropMin[i]),
                                        Integer.valueOf(skillDropMax[i]) + 1);
                                if (skillDropNum > 0) {
                                    skillId = ItemId.ZHAO_HUAN_SHI.getId();
                                }
                            } else if (Integer.valueOf(fallingMaterials[i]) == 19) { // 电磁石
                                skillDropNum = ThreadLocalRandom.current().nextInt(Integer.valueOf(skillDropMin[i]),
                                        Integer.valueOf(skillDropMax[i]) + 1);
                                if (skillDropNum > 0) {
                                    skillId = ItemId.DIAN_CI_SHI.getId();
                                }
                            } else if (Integer.valueOf(fallingMaterials[i]) == 20) { // 黑洞石
                                skillDropNum = ThreadLocalRandom.current().nextInt(Integer.valueOf(skillDropMin[i]),
                                        Integer.valueOf(skillDropMax[i]) + 1);
                                if (skillDropNum > 0) {
                                    skillId = ItemId.DIAN_CI_SHI.getId();
                                }
                            } else if (Integer.valueOf(fallingMaterials[i]) == 21) { // 领主石
                                skillDropNum = ThreadLocalRandom.current().nextInt(Integer.valueOf(skillDropMin[i]),
                                        Integer.valueOf(skillDropMax[i]) + 1);
                                if (skillDropNum > 0) {
                                    skillId = ItemId.LING_ZHU_SHI.getId();
                                }
                            } else if (Integer.valueOf(fallingMaterials[i]) == 22) { // 龙骨
                                skillDropNum = ThreadLocalRandom.current().nextInt(Integer.valueOf(skillDropMin[i]),
                                        Integer.valueOf(skillDropMax[i]) + 1);
                                if (skillDropNum > 0) {
                                    skillId = ItemId.LONG_GU.getId();
                                }
                            } else if (Integer.valueOf(fallingMaterials[i]) == 23) { // 龙珠
                                skillDropNum = ThreadLocalRandom.current().nextInt(Integer.valueOf(skillDropMin[i]),
                                        Integer.valueOf(skillDropMax[i]) + 1);
                                if (skillDropNum > 0) {
                                    skillId = ItemId.LONG_ZHU.getId();
                                }
                            } else if (Integer.valueOf(fallingMaterials[i]) == 24) { // 龙元
                                skillDropNum = ThreadLocalRandom.current().nextInt(Integer.valueOf(skillDropMin[i]),
                                        Integer.valueOf(skillDropMax[i]) + 1);
                                if (skillDropNum > 0) {
                                    skillId = ItemId.LONG_YUAN.getId();
                                }
                            } else if (Integer.valueOf(fallingMaterials[i]) == 25) { // 龙脊
                                skillDropNum = ThreadLocalRandom.current().nextInt(Integer.valueOf(skillDropMin[i]),
                                        Integer.valueOf(skillDropMax[i]) + 1);
                                if (skillDropNum > 0) {
                                    skillId = ItemId.LONG_JI.getId();
                                }
                            } else if (Integer.valueOf(fallingMaterials[i]) == 26) { // 黑洞炮
                                skillDropNum = ThreadLocalRandom.current().nextInt(Integer.valueOf(skillDropMin[i]),
                                        Integer.valueOf(skillDropMax[i]) + 1);
                                if (skillDropNum > 0) {
                                    skillId = ItemId.SKILL_BLACK_HOLE.getId();
                                }
                            } else if (Integer.valueOf(fallingMaterials[i]) == 27) { // 电磁炮
                                skillDropNum = ThreadLocalRandom.current().nextInt(Integer.valueOf(skillDropMin[i]),
                                        Integer.valueOf(skillDropMax[i]) + 1);
                                if (skillDropNum > 0) {
                                    skillId = ItemId.SKILL_ELETIC.getId();
                                }
                            } else if (Integer.valueOf(fallingMaterials[i]) == 28) { // 鱼雷炮
                                skillDropNum = ThreadLocalRandom.current().nextInt(Integer.valueOf(skillDropMin[i]),
                                        Integer.valueOf(skillDropMax[i]) + 1);
                                if (skillDropNum > 0) {
                                    skillId = ItemId.SKILL_TORPEDO.getId();
                                }
                            } else if (Integer.valueOf(fallingMaterials[i]) == 29) { // 号角
                                skillDropNum = ThreadLocalRandom.current().nextInt(Integer.valueOf(skillDropMin[i]),
                                        Integer.valueOf(skillDropMax[i]) + 1);
                                if (skillDropNum > 0) {
                                    skillId = ItemId.BOSS_BUGLE.getId();
                                }
                            } else if (Integer.valueOf(fallingMaterials[i]) == 30) { // 分身
                                skillDropNum = ThreadLocalRandom.current().nextInt(Integer.valueOf(skillDropMin[i]),
                                        Integer.valueOf(skillDropMax[i]) + 1);
                                if (skillDropNum > 0) {
                                    skillId = ItemId.FEN_SHEN.getId();
                                }
                            } else if (Integer.valueOf(fallingMaterials[i]) == 31) { // 核弹
                                skillDropNum = ThreadLocalRandom.current().nextInt(Integer.valueOf(skillDropMin[i]),
                                        Integer.valueOf(skillDropMax[i]) + 1);
                                if (skillDropNum > 0) {
                                    skillId = ItemId.GOLD_TORPEDO.getId();
                                }
                            } else if (Integer.valueOf(fallingMaterials[i]) == 32) { // 稀有核弹
                                skillDropNum = ThreadLocalRandom.current().nextInt(Integer.valueOf(skillDropMin[i]),
                                        Integer.valueOf(skillDropMax[i]) + 1);
                                if (skillDropNum > 0) {
                                    skillId = ItemId.RARE_TORPEDO.getId();
                                }
                            } else if (Integer.valueOf(fallingMaterials[i]) == 33) { // 黑铁弹
                                skillDropNum = ThreadLocalRandom.current().nextInt(Integer.valueOf(skillDropMin[i]),
                                        Integer.valueOf(skillDropMax[i]) + 1);
                                if (skillDropNum > 0) {
                                    skillId = ItemId.BLACK_BULLET.getId();
                                }
                            } else if (Integer.valueOf(fallingMaterials[i]) == 34) { // 青铜弹
                                skillDropNum = ThreadLocalRandom.current().nextInt(Integer.valueOf(skillDropMin[i]),
                                        Integer.valueOf(skillDropMax[i]) + 1);
                                if (skillDropNum > 0) {
                                    skillId = ItemId.BRONZE_BULLET.getId();
                                }
                            } else if (Integer.valueOf(fallingMaterials[i]) == 35) { // 白银弹
                                skillDropNum = ThreadLocalRandom.current().nextInt(Integer.valueOf(skillDropMin[i]),
                                        Integer.valueOf(skillDropMax[i]) + 1);
                                if (skillDropNum > 0) {
                                    skillId = ItemId.SILVER_BULLET.getId();
                                }
                            } else if (Integer.valueOf(fallingMaterials[i]) == 36) { // 黄金弹
                                skillDropNum = ThreadLocalRandom.current().nextInt(Integer.valueOf(skillDropMin[i]),
                                        Integer.valueOf(skillDropMax[i]) + 1);
                                if (skillDropNum > 0) {
                                    skillId = ItemId.GOLD_BULLET.getId();
                                }
                            }  else if (Integer.valueOf(fallingMaterials[i]) == 38) { // 龙晶
                                skillDropNum = ThreadLocalRandom.current().nextInt(Integer.valueOf(skillDropMin[i]),
                                        Integer.valueOf(skillDropMax[i]) + 1);
                                if (skillDropNum > 0) {
                                    skillId = ItemId.DRAGON_CRYSTAL.getId();
                                }
                            } else if (Integer.valueOf(fallingMaterials[i]) == 39) { // 金币
                                skillDropNum = ThreadLocalRandom.current().nextInt(Integer.valueOf(skillDropMin[i]),
                                        Integer.valueOf(skillDropMax[i]) + 1);
                                if (skillDropNum > 0) {
                                    skillId = ItemId.MONEY.getId();
                                }
                            }
                            if (skillId > 0) {
                                dropItems.add(OseePublicData.ItemDataProto.newBuilder().setItemId(skillId)
                                        .setItemNum(skillDropNum).build());
                            }
                        }
                    }
                }
                // 给玩家加掉落的鱼雷或者技能
                for (OseePublicData.ItemDataProto item : dropItems) {
                    // 变动原因为捕鱼产出消耗 ItemChangeReason.FISHING_RESULT
                    PlayerManager.addItem(player.getUser(), item.getItemId(), item.getItemNum(),
                            ItemChangeReason.FISHING_RESULT, true);
                }

                int games = RedisUtil.val(PLAYER_GRANDPRIX_CONFIG_GAMES_KEY + player.getId(), 0);
                Long dayPoint =
                        (long) (RedisUtil.get(PLAYER_GRANDPRIX_CONFIG_POINT_DAY_KEY + player.getId(), games - 1));
                dayPoint += randomMoney;

                // 2022年7月1日 加成计算放到结算的时候
                // if (player.getViewIndex() == ItemId.BATTERY_VIEW_3.getId()) { // 炮台1
                // dayPoint += new BigDecimal(randomMoney * 0.05).longValue();
                // } else if (player.getViewIndex() == ItemId.BATTERY_VIEW_4.getId()) { // 炮台2
                // dayPoint += new BigDecimal(randomMoney * 0.1).longValue();
                // }

                // 爆炸炸死的鱼不向玩家发送消息
                if (!boom) {
                    FishingGrandPrixFightFishResponse.Builder builder = FishingGrandPrixFightFishResponse.newBuilder();
                    builder.setFishId(fishId);
                    builder.setPlayerId(player.getId());
                    builder.setRestMoney(player.getMoney());
                    builder.setDropMoney(winMoney);
                    builder.setDayPoint(dayPoint);
                    builder.addAllDropItems(dropItems);
                    builder.setMultiple(randomMoney); // 鱼倍数
                    MyRefreshFishingUtil.sendRoomMessage(gameRoom,
                            OseeMessage.OseeMsgCode.S_C_TTMY_FISHING_GRAND_PRIX_FIGHT_FISH_RESPONSE_VALUE, builder);
                    RedisUtil.set(PLAYER_GRANDPRIX_CONFIG_POINT_DAY_KEY + player.getUser().getId(),
                            String.valueOf(dayPoint), games - 1);
                }
            }
        }
        return configs;
    }

    /**
     * 捕捉到特殊鱼
     */
    public void catchSpecialFish(FishingGrandPrixRoom gameRoom, long playerId, List<Long> fishIdsList,
                                 long specialFishId) {
        FishingGrandPrixPlayer player = gameRoom.getGamePlayerById(playerId);
        if (player == null) {
            return;
        }
        long beforeMoney = player.getMoney();
        List<FishConfig> configs = new ArrayList<>();
        if (specialFishId < 0) {
            configs = grandPrixFightFish(gameRoom, player, fishIdsList, true, true, specialFishId);
        } else {
            configs = playerFightFish(gameRoom, player, -1, fishIdsList, true, true);
        }
        int winMoney = 0;
        for (int i = 0; i < configs.size(); i++) {
            winMoney += configs.get(i).getWinMoney();
        }
        if (specialFishId < 0) {
            if (specialFishId == -64) {
                int skillMoney = ThreadLocalRandom.current().nextInt(250, 330);
                if (winMoney * 0.5 > skillMoney * 10000) {
                    RedisHelper.set("ALL_SKILL_XH" + player.getUser().getId(),
                            String.valueOf(3000000 - (skillMoney * 10000)));
                    winMoney = skillMoney * 10000;
                } else {
                    RedisHelper.set("ALL_SKILL_XH" + player.getUser().getId(),
                            String.valueOf(3000000 - (new Double(winMoney * 0.5).longValue())));
                    winMoney = new Double(winMoney * 0.5).intValue();
                }
            } else if (specialFishId == -51) {
                int skillMoney = ThreadLocalRandom.current().nextInt(150, 231);
                if (winMoney * 0.5 > skillMoney * 10000) {
                    RedisHelper.set("ALL_SKILL_XH" + player.getUser().getId(),
                            String.valueOf(2000000 - (skillMoney * 10000)));
                    winMoney = skillMoney * 10000;
                } else {
                    RedisHelper.set("ALL_SKILL_XH" + player.getUser().getId(),
                            String.valueOf(2000000 - (new Double(winMoney * 0.5).longValue())));
                    winMoney = new Double(winMoney * 0.5).intValue();
                }
            } else if (specialFishId == -52) {
                int skillMoney = ThreadLocalRandom.current().nextInt(100, 181);
                if (winMoney * 0.5 > skillMoney * 10000) {
                    RedisHelper.set("ALL_SKIFishingChallengeManagerLL_XH" + player.getUser().getId(),
                            String.valueOf(1500000 - (skillMoney * 10000)));
                    winMoney = skillMoney * 10000;
                } else {
                    RedisHelper.set("ALL_SKILL_XH" + player.getUser().getId(),
                            String.valueOf(1500000 - (new Double(winMoney * 0.5).longValue())));
                    winMoney = new Double(winMoney * 0.5).intValue();
                }
            }
            player.addMoney(winMoney);
        } else {
            long xh1 = new Double(RedisUtil.val("ALL_XH_100-200" + player.getUser().getId(), 0D)).longValue();
            if (specialFishId == 101) {
                long skillMoney = RedisUtil.val("WINMONEY_SKILL_FISH" + 101 + player.getUser().getId(), 0L);
                if (winMoney > skillMoney) {
                    winMoney = (int) skillMoney;
                } else {
                    winMoney = new Double(winMoney).intValue();
                }
                RedisHelper.set("ALL_XH_100-200" + player.getUser().getId(), String.valueOf(skillMoney - winMoney));
            } else if (specialFishId == 102) {
                long skillMoney = RedisUtil.val("WINMONEY_SKILL_FISH" + 102 + player.getUser().getId(), 0L);
                if (winMoney > skillMoney) {
                    winMoney = (int) skillMoney;
                } else {
                    winMoney = new Double(winMoney).intValue();
                }
                RedisHelper.set("ALL_XH_100-200" + player.getUser().getId(), String.valueOf(skillMoney - winMoney));
            } else if (specialFishId == 103) {
                long skillMoney = RedisUtil.val("WINMONEY_SKILL_FISH" + 103 + player.getUser().getId(), 0L);
                if (winMoney > skillMoney) {
                    winMoney = (int) skillMoney;
                } else {
                    winMoney = new Double(winMoney).intValue();
                }
                RedisHelper.set("ALL_XH_100-200" + player.getUser().getId(), String.valueOf(skillMoney - winMoney));
            }
            // if(winMoney>xh1){
            // winMoney=(int) xh1;
            // }
            player.addMoney(winMoney);

        }
        if (configs != null) {
            FishingGrandPrixCatchSpecialFishResponse.Builder builder =
                    FishingGrandPrixCatchSpecialFishResponse.newBuilder();
            builder.setSpecialFishId(specialFishId);
            builder.addAllFishIds(fishIdsList);
            builder.setPlayerId(player.getId());
            builder.setDropMoney(player.getMoney() - beforeMoney);
            builder.setRestMoney(player.getMoney());
            MyRefreshFishingUtil.sendRoomMessage(gameRoom,
                    OseeMessage.OseeMsgCode.S_C_TTMY_FISHING_GRAND_PRIX_CATCH_SPECIAL_FISH_RESPONSE_VALUE, builder);
        }
    }

    /**
     * 玩家击中鱼的各种逻辑判断
     */
    private List<FishConfig> grandPrixFightFish1(FishingGrandPrixRoom gameRoom, FishingGrandPrixPlayer player,
                                                 List<Long> fishIds, boolean boom, boolean isTymFish, long specialFishId) {
        List<FishConfig> configs = new LinkedList<>();
        synchronized (gameRoom) {
            // 非爆炸状态，且房间内不存在指定子弹
            // FireStruct fire = null;
            // if (!boom) {
            // if (!player.getFireMap().containsKey(fireId)) {
            // return null;
            // }
            //
            // fire = player.getFireMap().get(fireId);
            // fire.setCount(fire.getCount() - 1);
            // if (fire.getCount() <= 0) { // 该发子弹是否打完了
            // player.getFireMap().remove(fireId);
            // }
            // }

            for (Long fishId : fishIds) {
                // 鱼id不存在
                if (!gameRoom.getFishMap().containsKey(fishId)) {
                    continue;
                }

                FishStruct fish = gameRoom.getFishMap().get(fishId);
                FishConfig config = DataContainer.getData(fish.getConfigId(), FishConfig.class);

                // boss鱼无法因爆炸死亡
                if (boom && config.getFishType() == 100) {
                    continue;
                }

                long winMoney;

                long randomMoney = config.getMaxMoney() > config.getMoney()
                        ? ThreadLocalRandom.current().nextLong(config.getMoney(), config.getMaxMoney() + 1)
                        : config.getMoney();
                // if (fire != null) {
                // winMoney = randomMoney * fire.getLevel();
                // // 暴击状态下成功命中鱼类，则获得1.5倍金币奖励
                // if (System.currentTimeMillis() - player.getLastCritTime() < SKILL_CRIT_TIME) {
                // winMoney *= 1.5;
                // }
                // } else {
                if (isTymFish) {
                    long batteryLevel = 0;
                    batteryLevel = player.getBatteryLevel();
                    int batteryMult = 1;
                    // batteryMult = player.getBatteryLevel();
                    winMoney = randomMoney * batteryLevel * batteryMult;
                } else {
                    winMoney = randomMoney * player.getBatteryLevel() * player.getBatteryMult();
                }
                long batteryLevel = player.getBatteryLevel();
                int batteryMult = player.getBatteryMult();
                long needMoney = batteryLevel * batteryMult;
                // player.addMoney(-needMoney);
                boolean hit = isHit(player, gameRoom.getRoomIndex(), fish, config, needMoney, winMoney, -1);
                if (!boom && !hit) {
                    fish.setFireTimes(fish.getFireTimes() + 1);
                    continue;
                }

                MyRefreshFishingHelper.checkAndDurationRefreshFish(gameRoom, fish, true);

                fish = gameRoom.getFishMap().remove(fishId);
//                gameRoom.removeFishMap(fishId);
                config.setWinMoney(winMoney);
                configs.add(config);
                player.setChangeMoney(player.getChangeMoney() - winMoney);
                player.setWinMoney(player.getWinMoney() - winMoney);
                // }

                // Boss鱼死亡
                if (config.getFishType() == 100) {
                    if (player.getBatteryLevel() >= 1000) { // 炮台等级要1000倍以上才通报
                        // 进行全服通报
                        FishingGrandPrixCatchBossFishResponse.Builder builder =
                                FishingGrandPrixCatchBossFishResponse.newBuilder();
                        builder.setFishName(config.getName());
                        builder.setMoney(winMoney);
                        builder.setPlayerName(player.getUser().getNickname());
                        builder.setPlayerVipLevel(player.getVipLevel());
                        builder.setBatteryLevel((int) player.getBatteryLevel());
                        sendCatchBossFishResponse(builder);
                    }
                }

                // TODO 记录玩家金币和小黑屋相关数据
                // FishingHitDataManager.addChallengeWin(gameRoom, player, winMoney);
                // int greener = 1;
                // int index = gameRoom.getRoomIndex() - 1;
                // if (winMoney > FishingHitDataManager.BLACK_ROOM_LIMIT[greener][index]) {
                // FishingHitDataManager.addChallengeBlackRoom(player.getId(), index, winMoney);
                // }
                long a = Long.valueOf(RedisHelper.get(FishingGrandPrixManager.PLAYER_GRANDPRIX_CONFIG_QZ_KEY));
                long b = 0;
                if (!RedisHelper.get(FishingGrandPrixManager.PLAYER_GRANDPRIX_CONFIG_BP_KEY).equals("0.0")) {
                    b = Long.valueOf(RedisHelper.get(FishingGrandPrixManager.PLAYER_GRANDPRIX_CONFIG_BP_KEY));
                }
                // if(b!=0){
                // if (winMoney > Long.valueOf(RedisHelper.get(FishingGrandPrixManager.PLAYER_GRANDPRIX_CONFIG_QZ_KEY)))
                // {
                // if(!"".equals(RedisHelper.get("FISHING_GRANDPRIX_BLACKROOM:"+player.getUser().getId()))||RedisHelper.get("FISHING_GRANDPRIX_BLACKROOM:"+player.getUser().getId())!=null){
                // RedisHelper.set("FISHING_GRANDPRIX_BLACKROOM:"+player.getUser().getId(), String.valueOf(((winMoney -
                // a)/b) * 0.01 +
                // Long.valueOf(RedisHelper.get("FISHING_GRANDPRIX_BLACKROOM:"+player.getUser().getId()))));
                // }
                // }
                // }

                // 玩家收获金币
                if (winMoney > 0 && !boom && specialFishId == -64) {
                    player.addMoney(winMoney);
                }

                // 打死鱼之后掉落的物品
                List<HwLoginMessage.ItemDataProto1> dropItems = new LinkedList<>();
                if (config.getSkill() > 0) {
                    if (config.getSkill() > 5 && config.getSkill() < 9) { // 特殊技能鱼
                        FishingGrandPrixUseSkillResponse.Builder builder =
                                FishingGrandPrixUseSkillResponse.newBuilder();
                        builder.setPlayerId(player.getId());
                        if (config.getSkill() == 6) { // 局部爆炸鱼
                            builder.setSkillId(101);
                            builder.setSkillFishId(fishId);
                        } else if (config.getSkill() == 7) { // 闪电鱼
                            builder.setSkillId(102);
                            builder.setSkillFishId(fishId);
                        } else if (config.getSkill() == 8) { // 黑洞鱼
                            builder.setSkillId(103);
                            builder.setSkillFishId(fishId);
                        }
                        MyRefreshFishingUtil.sendRoomMessage(gameRoom,
                                OseeMessage.OseeMsgCode.S_C_TTMY_FISHING_GRAND_PRIX_USE_SKILL_RESPONSE_VALUE, builder);
                    }
                }
                if (config.getFallingMaterials() != null) { // 会掉落物品的鱼
                    String[] fallingMaterials = config.getFallingMaterials().split(",");
                    // 随机掉落的技能数量
                    String[] skillDropMin = config.getMinSkillDropNum().split(",");
                    String[] skillDropMax = config.getMaxSkillDropNum().split(",");
                    // 1锁定//2冰冻//3急速//4暴击//5全屏爆炸//6局部爆炸//7闪电//8黑洞/9奖券//10钻石//11鱼骨/12海妖石//13王魂石
                    // 14海魂石//15珍珠石//16海兽石//17海魔石//18召唤石//19电磁石//20黑洞石//21领主石//22龙骨//23龙珠
                    // 24龙元//25龙脊//26黑洞炮//27电磁炮//28鱼雷炮//29号角//30分身//31核弹
                    // 32稀有核弹33黑铁弹/34青铜弹/35白银弹//36黄金弹
                    // 37赠送卡//38龙晶//39金币
                    int skillDropNum = 0;
                    int skillId = 0;
                    int probabilty = ThreadLocalRandom.current().nextInt(0, 101);
                    int skillDropProbability = config.getSkillDropProbability();
                    if (player.getBatteryLevel() >= 2000 && player.getBatteryLevel() < 500000) {
                        skillDropProbability = config.getSkillDropProbabilityOne();
                    } else if (player.getBatteryLevel() >= 600000 && player.getBatteryLevel() < 1000000) {
                        skillDropProbability = config.getSkillDropProbabilityTwo();
                    } else if (player.getBatteryLevel() == 1000000) {
                        skillDropProbability = config.getSkillDropProbabilityMax();
                    }
                    if (probabilty < skillDropProbability) {
                        for (int i = 0; i < fallingMaterials.length; i++) {
                            if (Integer.valueOf(fallingMaterials[i]) == 1) { // 锁定
                                skillDropNum = ThreadLocalRandom.current().nextInt(Integer.valueOf(skillDropMin[i]),
                                        Integer.valueOf(skillDropMax[i]) + 1);
                                if (skillDropNum > 0) {
                                    skillId = ItemId.SKILL_LOCK.getId();
                                }
                            } else if (Integer.valueOf(fallingMaterials[i]) == 2) { // 冰冻
                                skillDropNum = ThreadLocalRandom.current().nextInt(Integer.valueOf(skillDropMin[i]),
                                        Integer.valueOf(skillDropMax[i]) + 1);
                                if (skillDropNum > 0) {
                                    skillId = ItemId.SKILL_FROZEN.getId();
                                }
                            } else if (Integer.valueOf(fallingMaterials[i]) == 3) { // 急速
                                skillDropNum = ThreadLocalRandom.current().nextInt(Integer.valueOf(skillDropMin[i]),
                                        Integer.valueOf(skillDropMax[i]) + 1);
                                if (skillDropNum > 0) {
                                    skillId = ItemId.SKILL_FAST.getId();
                                }
                            } else if (Integer.valueOf(fallingMaterials[i]) == 4) { // 暴击
                                skillDropNum = ThreadLocalRandom.current().nextInt(Integer.valueOf(skillDropMin[i]),
                                        Integer.valueOf(skillDropMax[i]) + 1);
                                if (skillDropNum > 0) {
                                    skillId = ItemId.SKILL_CRIT.getId();
                                }
                            } else if (Integer.valueOf(fallingMaterials[i]) == 9) { // 奖券
                                skillDropNum = ThreadLocalRandom.current().nextInt(Integer.valueOf(skillDropMin[i]),
                                        Integer.valueOf(skillDropMax[i]) + 1);
                                if (skillDropNum > 0) {
                                    skillId = ItemId.LOTTERY.getId();
                                }
                            } else if (Integer.valueOf(fallingMaterials[i]) == 10) { // 钻石
                                skillDropNum = ThreadLocalRandom.current().nextInt(Integer.valueOf(skillDropMin[i]),
                                        Integer.valueOf(skillDropMax[i]) + 1);
                                if (skillDropNum > 0) {
                                    skillId = ItemId.DIAMOND.getId();
                                    // 做任务
                                    FishingTaskManager.doTask(player.getUser(), TaskType.DAILY, GoalType.GET_DIAMOND, 0,
                                            1);
                                }
                            } else if (Integer.valueOf(fallingMaterials[i]) == 11) { // 鱼骨
                                skillDropNum = ThreadLocalRandom.current().nextInt(Integer.valueOf(skillDropMin[i]),
                                        Integer.valueOf(skillDropMax[i]) + 1);
                                if (skillDropNum > 0) {
                                    skillId = ItemId.YU_GU.getId();
                                }
                            } else if (Integer.valueOf(fallingMaterials[i]) == 12) { // 海妖石
                                skillDropNum = ThreadLocalRandom.current().nextInt(Integer.valueOf(skillDropMin[i]),
                                        Integer.valueOf(skillDropMax[i]) + 1);
                                if (skillDropNum > 0) {
                                    skillId = ItemId.HAI_YAO_SHI.getId();
                                }
                            } else if (Integer.valueOf(fallingMaterials[i]) == 13) { // 王魂石
                                skillDropNum = ThreadLocalRandom.current().nextInt(Integer.valueOf(skillDropMin[i]),
                                        Integer.valueOf(skillDropMax[i]) + 1);
                                if (skillDropNum > 0) {
                                    skillId = ItemId.WANG_HUN_SHI.getId();
                                }
                            } else if (Integer.valueOf(fallingMaterials[i]) == 14) { // 海魂石
                                skillDropNum = ThreadLocalRandom.current().nextInt(Integer.valueOf(skillDropMin[i]),
                                        Integer.valueOf(skillDropMax[i]) + 1);
                                if (skillDropNum > 0) {
                                    skillId = ItemId.HAI_HUN_SHI.getId();
                                }
                            } else if (Integer.valueOf(fallingMaterials[i]) == 15) { // 珍珠石
                                skillDropNum = ThreadLocalRandom.current().nextInt(Integer.valueOf(skillDropMin[i]),
                                        Integer.valueOf(skillDropMax[i]) + 1);
                                if (skillDropNum > 0) {
                                    skillId = ItemId.ZHEN_ZHU_SHI.getId();
                                }
                            } else if (Integer.valueOf(fallingMaterials[i]) == 16) { // 海兽石
                                skillDropNum = ThreadLocalRandom.current().nextInt(Integer.valueOf(skillDropMin[i]),
                                        Integer.valueOf(skillDropMax[i]) + 1);
                                if (skillDropNum > 0) {
                                    skillId = ItemId.HAI_SHOU_SHI.getId();
                                }
                            } else if (Integer.valueOf(fallingMaterials[i]) == 17) { // 海魔石
                                skillDropNum = ThreadLocalRandom.current().nextInt(Integer.valueOf(skillDropMin[i]),
                                        Integer.valueOf(skillDropMax[i]) + 1);
                                if (skillDropNum > 0) {
                                    skillId = ItemId.HAI_MO_SHI.getId();
                                }
                            } else if (Integer.valueOf(fallingMaterials[i]) == 18) { // 召唤石
                                skillDropNum = ThreadLocalRandom.current().nextInt(Integer.valueOf(skillDropMin[i]),
                                        Integer.valueOf(skillDropMax[i]) + 1);
                                if (skillDropNum > 0) {
                                    skillId = ItemId.ZHAO_HUAN_SHI.getId();
                                }
                            } else if (Integer.valueOf(fallingMaterials[i]) == 19) { // 电磁石
                                skillDropNum = ThreadLocalRandom.current().nextInt(Integer.valueOf(skillDropMin[i]),
                                        Integer.valueOf(skillDropMax[i]) + 1);
                                if (skillDropNum > 0) {
                                    skillId = ItemId.DIAN_CI_SHI.getId();
                                }
                            } else if (Integer.valueOf(fallingMaterials[i]) == 20) { // 黑洞石
                                skillDropNum = ThreadLocalRandom.current().nextInt(Integer.valueOf(skillDropMin[i]),
                                        Integer.valueOf(skillDropMax[i]) + 1);
                                if (skillDropNum > 0) {
                                    skillId = ItemId.DIAN_CI_SHI.getId();
                                }
                            } else if (Integer.valueOf(fallingMaterials[i]) == 21) { // 领主石
                                skillDropNum = ThreadLocalRandom.current().nextInt(Integer.valueOf(skillDropMin[i]),
                                        Integer.valueOf(skillDropMax[i]) + 1);
                                if (skillDropNum > 0) {
                                    skillId = ItemId.LING_ZHU_SHI.getId();
                                }
                            } else if (Integer.valueOf(fallingMaterials[i]) == 22) { // 龙骨
                                skillDropNum = ThreadLocalRandom.current().nextInt(Integer.valueOf(skillDropMin[i]),
                                        Integer.valueOf(skillDropMax[i]) + 1);
                                if (skillDropNum > 0) {
                                    skillId = ItemId.LONG_GU.getId();
                                }
                            } else if (Integer.valueOf(fallingMaterials[i]) == 23) { // 龙珠
                                skillDropNum = ThreadLocalRandom.current().nextInt(Integer.valueOf(skillDropMin[i]),
                                        Integer.valueOf(skillDropMax[i]) + 1);
                                if (skillDropNum > 0) {
                                    skillId = ItemId.LONG_ZHU.getId();
                                }
                            } else if (Integer.valueOf(fallingMaterials[i]) == 24) { // 龙元
                                skillDropNum = ThreadLocalRandom.current().nextInt(Integer.valueOf(skillDropMin[i]),
                                        Integer.valueOf(skillDropMax[i]) + 1);
                                if (skillDropNum > 0) {
                                    skillId = ItemId.LONG_YUAN.getId();
                                }
                            } else if (Integer.valueOf(fallingMaterials[i]) == 25) { // 龙脊
                                skillDropNum = ThreadLocalRandom.current().nextInt(Integer.valueOf(skillDropMin[i]),
                                        Integer.valueOf(skillDropMax[i]) + 1);
                                if (skillDropNum > 0) {
                                    skillId = ItemId.LONG_JI.getId();
                                }
                            } else if (Integer.valueOf(fallingMaterials[i]) == 26) { // 黑洞炮
                                skillDropNum = ThreadLocalRandom.current().nextInt(Integer.valueOf(skillDropMin[i]),
                                        Integer.valueOf(skillDropMax[i]) + 1);
                                if (skillDropNum > 0) {
                                    skillId = ItemId.SKILL_BLACK_HOLE.getId();
                                }
                            } else if (Integer.valueOf(fallingMaterials[i]) == 27) { // 电磁炮
                                skillDropNum = ThreadLocalRandom.current().nextInt(Integer.valueOf(skillDropMin[i]),
                                        Integer.valueOf(skillDropMax[i]) + 1);
                                if (skillDropNum > 0) {
                                    skillId = ItemId.SKILL_ELETIC.getId();
                                }
                            } else if (Integer.valueOf(fallingMaterials[i]) == 28) { // 鱼雷炮
                                skillDropNum = ThreadLocalRandom.current().nextInt(Integer.valueOf(skillDropMin[i]),
                                        Integer.valueOf(skillDropMax[i]) + 1);
                                if (skillDropNum > 0) {
                                    skillId = ItemId.SKILL_TORPEDO.getId();
                                }
                            } else if (Integer.valueOf(fallingMaterials[i]) == 29) { // 号角
                                skillDropNum = ThreadLocalRandom.current().nextInt(Integer.valueOf(skillDropMin[i]),
                                        Integer.valueOf(skillDropMax[i]) + 1);
                                if (skillDropNum > 0) {
                                    skillId = ItemId.BOSS_BUGLE.getId();
                                }
                            } else if (Integer.valueOf(fallingMaterials[i]) == 30) { // 暴击
                                skillDropNum = ThreadLocalRandom.current().nextInt(Integer.valueOf(skillDropMin[i]),
                                        Integer.valueOf(skillDropMax[i]) + 1);
                                if (skillDropNum > 0) {
                                    skillId = ItemId.SKILL_CRIT.getId();
                                }
                            } else if (Integer.valueOf(fallingMaterials[i]) == 31) { // 核弹
                                skillDropNum = ThreadLocalRandom.current().nextInt(Integer.valueOf(skillDropMin[i]),
                                        Integer.valueOf(skillDropMax[i]) + 1);
                                if (skillDropNum > 0) {
                                    skillId = ItemId.GOLD_TORPEDO.getId();
                                }
                            } else if (Integer.valueOf(fallingMaterials[i]) == 32) { // 稀有核弹
                                skillDropNum = ThreadLocalRandom.current().nextInt(Integer.valueOf(skillDropMin[i]),
                                        Integer.valueOf(skillDropMax[i]) + 1);
                                if (skillDropNum > 0) {
                                    skillId = ItemId.RARE_TORPEDO.getId();
                                }
                            } else if (Integer.valueOf(fallingMaterials[i]) == 33) { // 黑铁弹
                                skillDropNum = ThreadLocalRandom.current().nextInt(Integer.valueOf(skillDropMin[i]),
                                        Integer.valueOf(skillDropMax[i]) + 1);
                                if (skillDropNum > 0) {
                                    skillId = ItemId.BLACK_BULLET.getId();
                                }
                            } else if (Integer.valueOf(fallingMaterials[i]) == 34) { // 青铜弹
                                skillDropNum = ThreadLocalRandom.current().nextInt(Integer.valueOf(skillDropMin[i]),
                                        Integer.valueOf(skillDropMax[i]) + 1);
                                if (skillDropNum > 0) {
                                    skillId = ItemId.BRONZE_BULLET.getId();
                                }
                            } else if (Integer.valueOf(fallingMaterials[i]) == 35) { // 白银弹
                                skillDropNum = ThreadLocalRandom.current().nextInt(Integer.valueOf(skillDropMin[i]),
                                        Integer.valueOf(skillDropMax[i]) + 1);
                                if (skillDropNum > 0) {
                                    skillId = ItemId.SILVER_BULLET.getId();
                                }
                            } else if (Integer.valueOf(fallingMaterials[i]) == 36) { // 黄金弹
                                skillDropNum = ThreadLocalRandom.current().nextInt(Integer.valueOf(skillDropMin[i]),
                                        Integer.valueOf(skillDropMax[i]) + 1);
                                if (skillDropNum > 0) {
                                    skillId = ItemId.GOLD_BULLET.getId();
                                }
                            }else if (Integer.valueOf(fallingMaterials[i]) == 38) { // 龙晶
                                skillDropNum = ThreadLocalRandom.current().nextInt(Integer.valueOf(skillDropMin[i]),
                                        Integer.valueOf(skillDropMax[i]) + 1);
                                if (skillDropNum > 0) {
                                    skillId = ItemId.DRAGON_CRYSTAL.getId();
                                }
                            } else if (Integer.valueOf(fallingMaterials[i]) == 39) { // 金币
                                skillDropNum = ThreadLocalRandom.current().nextInt(Integer.valueOf(skillDropMin[i]),
                                        Integer.valueOf(skillDropMax[i]) + 1);
                                if (skillDropNum > 0) {
                                    skillId = ItemId.MONEY.getId();
                                }
                            }
                            if (skillId > 0) {
                                dropItems.add(HwLoginMessage.ItemDataProto1.newBuilder().setItemId(skillId)
                                        .setItemNum(skillDropNum).build());
                            }
                        }
                    }
                }
                // 给玩家加掉落的鱼雷或者技能
                for (HwLoginMessage.ItemDataProto1 item : dropItems) {
                    // 变动原因为捕鱼产出消耗 ItemChangeReason.FISHING_RESULT
                    PlayerManager.addItem(player.getUser(), item.getItemId(), item.getItemNum(),
                            ItemChangeReason.FISHING_RESULT, true);
                }

                int games = RedisUtil.val(PLAYER_GRANDPRIX_CONFIG_GAMES_KEY + player.getId(), 0);
                Long dayPoint =
                        (long) (RedisUtil.get(PLAYER_GRANDPRIX_CONFIG_POINT_DAY_KEY + player.getId(), games - 1));
                // if(isTymFish){
                // if("".equals(RedisHelper.get("batteryLevel"+player.getUser().getId()))){
                // batteryLevel = player.getBatteryLevel();
                // }else{
                // batteryLevel = Integer.parseInt(RedisHelper.get("batteryLevel"+player.getUser().getId()));
                // }
                dayPoint += randomMoney;
                // dayPoint += new Double(dayPoint *((batteryLevel-1000)/5000 + vip + ga)).longValue();
                // }else{
                // dayPoint += player.getBatteryLevel();
                // dayPoint += new Double(dayPoint *((player.getBatteryLevel()-1000)/5000 + vip + ga)).longValue();
                // }

                // 爆炸炸死的鱼不向玩家发送消息
                if (!boom) {
                    HwLoginMessage.GrandPrixBitFightFishResponse.Builder builder =
                            HwLoginMessage.GrandPrixBitFightFishResponse.newBuilder();
                    builder.setFishId(fishId);
                    builder.setPlayerId(player.getId());
                    builder.setRestMoney(player.getMoney());
                    builder.setDropMoney(winMoney);
                    builder.setDayPoint(dayPoint);
                    builder.addAllDropItems(dropItems);
                    builder.setTargetMult(randomMoney); // 鱼倍数
                    MyRefreshFishingUtil.sendRoomMessage(gameRoom,
                            OseeMessage.OseeMsgCode.S_C_GRANDPRIX_BIT_FIGHT_FISH_RESPONSE_VALUE, builder);
                    long stock = RedisUtil.val(PLAYER_GRANDPRIX_CONFIG_STOCK_KEY, 0L);
                    stock -= winMoney;
                    RedisHelper.set(PLAYER_GRANDPRIX_CONFIG_STOCK_KEY, String.valueOf(stock));

                    // 用户消耗金币存入用户自身库存
                    Long pointTotal = RedisUtil.val(PLAYER_GRANDPRIX_CONFIG_POINT_TOTAL_KEY + player.getId(), 0L);
                    pointTotal -= winMoney;
                    RedisHelper.set(PLAYER_GRANDPRIX_CONFIG_POINT_TOTAL_KEY + player.getId(),
                            String.valueOf(pointTotal));

                    Long pointDayTotal =
                            RedisUtil.val(PLAYER_GRANDPRIX_CONFIG_POINT_DAY_TOTAL_KEY + player.getId(), 0L);
                    pointDayTotal -= winMoney;
                    RedisHelper.set(PLAYER_GRANDPRIX_CONFIG_POINT_DAY_TOTAL_KEY + player.getId(),
                            String.valueOf(pointDayTotal));

                    RedisUtil.set(PLAYER_GRANDPRIX_CONFIG_POINT_DAY_KEY + player.getUser().getId(),
                            String.valueOf(dayPoint), games - 1);

                    // 游走字幕播报
                    // if (fish.getFishType() == 100) { // boss鱼才播报
                    // String text = String.format(
                    // AutoWanderSubtitle.TEMPLATES[ThreadLocalRandom.current().nextInt(8, 10)],
                    // player.getUser().getNickname().substring(0,2)+"***", randomMoney, config.getName(), winMoney /
                    // 10000
                    // );
                    // // 给全部在线玩家推送游走字幕消息
                    // PlayerManager.sendMessageToOnline(LobbyMessage.LobbyMsgCode.S_C_WANDER_SUBTITLE_RESPONSE_VALUE,
                    // LobbyMessage.WanderSubtitleResponse.newBuilder().setLevel(1).setContent(text).build());
                    // }
                }
            }
        }
        return configs;
    }

    private static void sendCatchBossFishResponse(FishingGrandPrixCatchBossFishResponse.Builder builder) {
        ThreadPoolUtils.TASK_SERVICE_POOL.schedule(() -> {
            // 只发送到全服在捕鱼房间内的玩家
            List<FishingGrandPrixRoom> fishingGameRooms = GameContainer.getGameRooms(FishingGrandPrixRoom.class);
            for (FishingGrandPrixRoom gameRoom : fishingGameRooms) {
                if (gameRoom != null) {
                    MyRefreshFishingUtil.sendRoomMessage(gameRoom,
                            OseeMessage.OseeMsgCode.S_C_TTMY_FISHING_GRAND_PRIX_CATCH_BOSS_FISH_RESPONSE_VALUE, builder);
                }
            }
        }, 0, TimeUnit.SECONDS);
    }

    /**
     * this code like shit, i don't want to change it, good luck
     *
     * @param roomIndex
     * @param fish
     * @param config
     * @return
     */
    // private static boolean isHit(FishingGrandPrixPlayer player, int roomIndex, FishStruct fish, FishConfig
    // config,long needMoney,long winMoney,long fireId) {
    // long xh1 = 0L;
    // boolean tool = false;
    // int t = 0;
    // Long personalNum = new
    // Double(RedisUtil.val("USER_PERSONAL_CONTROL_NUM"+player.getUser().getId(),0D)).longValue();
    //
    // String burstTmax = RedisHelper.get("USER_PERSONAL_CONTROL_BURST_TMAX"+player.getUser().getId());
    // Integer[] burstTmax_obj = {0,0,0,0};
    // if(burstTmax!=null&&burstTmax.length()!=0){
    // burstTmax = burstTmax.substring(burstTmax.lastIndexOf("[")+1).replaceAll("]","");
    // String[] q0 = burstTmax.split(",");
    // List<Integer> list = new ArrayList<>();
    // for (String q : q0) {
    // if(q==null||"".equals(q)){
    // continue;
    // }
    // list.add(new Double(Double.parseDouble(q)).intValue());
    // }
    // burstTmax_obj = list.toArray(new Integer[0]);
    // }
    //
    // String burstTmin = RedisHelper.get("USER_PERSONAL_CONTROL_BURST_TMIN"+player.getUser().getId());
    // Integer[] burstTmin_obj = {0,0,0,0};
    // if(burstTmin!=null&&burstTmin.length()!=0){
    // burstTmin = burstTmin.substring(burstTmin.lastIndexOf("[")+1).replaceAll("]","");
    // String[] q0 = burstTmin.split(",");
    // List<Integer> list = new ArrayList<>();
    // for (String q : q0) {
    // if(q==null||"".equals(q)){
    // continue;
    // }
    // list.add(new Double(Double.parseDouble(q)).intValue());
    // }
    // burstTmin_obj = list.toArray(new Integer[0]);
    // }
    //
    // String recoveryTmax = RedisHelper.get("USER_PERSONAL_CONTROL_RECOVERY_TMAX"+player.getUser().getId());
    // Integer[] recoveryTmax_obj = {0,0,0,0};
    // if(recoveryTmax!=null&&recoveryTmax.length()!=0){
    // recoveryTmax = recoveryTmax.substring(recoveryTmax.lastIndexOf("[")+1).replaceAll("]","");
    // String[] q0 = recoveryTmax.split(",");
    // List<Integer> list = new ArrayList<>();
    // for (String q : q0) {
    // if(q==null||"".equals(q)){
    // continue;
    // }
    // list.add(new Double(Double.parseDouble(q)).intValue());
    // }
    // recoveryTmax_obj = list.toArray(new Integer[0]);
    // }
    //
    // String recoveryTmin = RedisHelper.get("USER_PERSONAL_CONTROL_RECOVERY_TMIN"+player.getUser().getId());
    // Integer[] recoveryTmin_obj = {0,0,0,0};
    // if(recoveryTmin!=null&&recoveryTmin.length()!=0){
    // recoveryTmin = recoveryTmin.substring(recoveryTmin.lastIndexOf("[")+1).replaceAll("]","");
    // String[] q0 = recoveryTmin.split(",");
    // List<Integer> list = new ArrayList<>();
    // for (String q : q0) {
    // if(q==null||"".equals(q)){
    // continue;
    // }
    // list.add(new Double(Double.parseDouble(q)).intValue());
    // }
    // recoveryTmin_obj = list.toArray(new Integer[0]);
    // }
    // long cx = new Double(RedisUtil.val("ALL_CX_USER"+player.getUser().getId(),0D)).longValue();
    // long bankNumber = RedisUtil.val("USER_T_BANKRUPTCY_NUMBER"+player.getUser().getId(),0L)+500000;
    // double banlace = 0L;
    // if(cx>0){
    // banlace = bankNumber;
    // }else{
    // banlace = bankNumber - (cx * FishingUtil.cxPercentage[(roomIndex*2)-1]*0.01);
    // }
    // int tMax = RedisUtil.val("USER_T_MAX"+player.getUser().getId(),0);
    // int tMin = RedisUtil.val("USER_T_MIN"+player.getUser().getId(),0);
    // if(FishingUtil.pumpNum[13]>0){
    // if(FishingUtil.pumpNum[13]-(new Double(FishingUtil.pump[13]*0.01*needMoney).longValue())<0){
    // player.setCutMoney(player.getCutMoney()+FishingUtil.pumpNum[13]);
    // needMoney -= FishingUtil.pumpNum[13];
    // FishingUtil.pumpNum[13] = 0L;
    // }else{
    // player.setCutMoney(player.getCutMoney()+new Double(FishingUtil.pump[13]*0.01*needMoney).longValue());
    // FishingUtil.pumpNum[13] -= new Double(FishingUtil.pump[13]*0.01*needMoney).longValue();
    // needMoney -= new Double(FishingUtil.pump[13]*0.01*needMoney).longValue();
    // }
    // List<Long> peakMaxNum1_obj = new ArrayList<Long>();
    // peakMaxNum1_obj = Arrays.asList(FishingUtil.pumpNum);
    // RedisHelper.set("pumpNum", String.valueOf(peakMaxNum1_obj));
    // }
    // if(config.getFishType2()==1){
    // xh1 = new Double(RedisUtil.val("ALL_XH_1-50"+player.getUser().getId(),0D)).longValue();
    // RedisHelper.set("ALL_XH_1-50"+player.getUser().getId(),String.valueOf(xh1+needMoney));
    // if(personalNum!=0){//进入个控
    // if(personalNum>0){//进入个控爆发
    // t = ThreadLocalRandom.current().nextInt(burstTmin_obj[0],burstTmax_obj[0]+1);
    // }else{
    // t = ThreadLocalRandom.current().nextInt(recoveryTmin_obj[0],recoveryTmax_obj[0]+1);
    // }
    // }else if(FishingUtil.q0[13]!=0){//进入场控
    // if(FishingUtil.q0[13]>0){
    // t = ThreadLocalRandom.current().nextInt(new Double(FishingUtil.apt[13]).intValue(),new
    // Double(FishingUtil.ap[13]).intValue()+1);
    // }else{
    // t = ThreadLocalRandom.current().nextInt(new Double(FishingUtil.recMin[13]).intValue(),new
    // Double(FishingUtil.recMax[13]).intValue()+1);
    // }
    // }else{
    // t = ThreadLocalRandom.current().nextInt(tMin,tMax+1);
    // }
    // }else if(config.getFishType2()==2){
    // xh1 = new Double(RedisUtil.val("ALL_XH_50-100"+player.getUser().getId(),0D)).longValue();
    // RedisHelper.set("ALL_XH_50-100"+player.getUser().getId(),String.valueOf(xh1+needMoney));
    // if(personalNum!=0){//进入个控
    // if(personalNum>0){//进入个控爆发
    // t = ThreadLocalRandom.current().nextInt(burstTmin_obj[1],burstTmax_obj[1]+1);
    // }else{
    // t = ThreadLocalRandom.current().nextInt(recoveryTmin_obj[1],recoveryTmax_obj[1]+1);
    // }
    // }else if(FishingUtil.q0[13]!=0){//进入场控
    // if(FishingUtil.q0[13]>0){
    // t = ThreadLocalRandom.current().nextInt(new Double(FishingUtil.apt1[13]).intValue(),new
    // Double(FishingUtil.ap1[13]).intValue()+1);
    // }else{
    // t = ThreadLocalRandom.current().nextInt(new Double(FishingUtil.recMin1[13]).intValue(),new
    // Double(FishingUtil.recMax1[13]).intValue()+1);
    // }
    // }else{
    // t = ThreadLocalRandom.current().nextInt(tMin,tMax+1);
    // }
    // }else if(config.getFishType2()==3){
    // xh1 = new Double(RedisUtil.val("ALL_XH_100-200"+player.getUser().getId(),0D)).longValue();
    // RedisHelper.set("ALL_XH_100-200"+player.getUser().getId(),String.valueOf(xh1+needMoney));
    // if(personalNum!=0){//进入个控
    // if(personalNum>0){//进入个控爆发
    // t = ThreadLocalRandom.current().nextInt(burstTmin_obj[2],burstTmax_obj[2]+1);
    // }else{
    // t = ThreadLocalRandom.current().nextInt(recoveryTmin_obj[2],recoveryTmax_obj[2]+1);
    // }
    // }else if(FishingUtil.q0[13]!=0){//进入场控
    // if(FishingUtil.q0[13]>0){
    // t = ThreadLocalRandom.current().nextInt(new Double(FishingUtil.apt2[13]).intValue(),new
    // Double(FishingUtil.ap2[13]).intValue()+1);
    // }else{
    // t = ThreadLocalRandom.current().nextInt(new Double(FishingUtil.recMin2[13]).intValue(),new
    // Double(FishingUtil.recMax2[13]).intValue()+1);
    // }
    // }else{
    // t = ThreadLocalRandom.current().nextInt(tMin,tMax+1);
    // }
    // }else{
    // xh1 = new Double(RedisUtil.val("ALL_XH_200-max"+player.getUser().getId(),0D)).longValue();
    // RedisHelper.set("ALL_XH_200-max"+player.getUser().getId(),String.valueOf(xh1+needMoney));
    // t = ThreadLocalRandom.current().nextInt(tMin,tMax+1);
    // if(personalNum!=0){//进入个控
    // if(personalNum>0){//进入个控爆发
    // t = ThreadLocalRandom.current().nextInt(burstTmin_obj[3],burstTmax_obj[3]+1);
    // }else{
    // t = ThreadLocalRandom.current().nextInt(recoveryTmin_obj[3],recoveryTmax_obj[3]+1);
    // }
    // }else if(FishingUtil.q0[13]!=0){//进入场控
    // if(FishingUtil.q0[13]>0){
    // t = ThreadLocalRandom.current().nextInt(new Double(FishingUtil.apt3[13]).intValue(),new
    // Double(FishingUtil.ap3[13]).intValue()+1);
    // }else{
    // t = ThreadLocalRandom.current().nextInt(new Double(FishingUtil.recMin3[13]).intValue(),new
    // Double(FishingUtil.recMax3[13]).intValue()+1);
    // }
    // }else{
    // t = ThreadLocalRandom.current().nextInt(tMin,tMax+1);
    // }
    // }
    // long xl = winMoney;
    // long sj = xl*t/100;
    // long gr = xl-sj;
    // long xh =0L;
    // if(config.getFishType2()==1){
    // xh = new Double(RedisUtil.val("ALL_XH_1-50"+player.getUser().getId(),0D)).longValue();
    // }else if(config.getFishType2()==2){
    // xh = new Double(RedisUtil.val("ALL_XH_50-100"+player.getUser().getId(),0D)).longValue();
    // }else if(config.getFishType2()==3){
    // xh = new Double(RedisUtil.val("ALL_XH_100-200"+player.getUser().getId(),0D)).longValue();
    // }else{
    // xh = new Double(RedisUtil.val("ALL_XH_200-max"+player.getUser().getId(),0D)).longValue();
    // }
    // ArrayList<Long> list = new ArrayList<Long>();
    // list.add(new Double(RedisUtil.val("ALL_XH_200-max"+player.getUser().getId(),0D)).longValue());
    // list.add(new Double(RedisUtil.val("ALL_XH_1-50"+player.getUser().getId(),0D)).longValue());
    // list.add(new Double(RedisUtil.val("ALL_XH_50-100"+player.getUser().getId(),0D)).longValue());
    // list.add(new Double(RedisUtil.val("ALL_XH_100-200"+player.getUser().getId(),0D)).longValue());
    // Collections.sort(list);
    // tool = xh>=gr;
    // if(tool){
    // if(fireId!=-1){
    // if(config.getFishType2()!=3){
    // if(personalNum!=0) {//进入个控
    // if(personalNum>0&&personalNum-sj<0){
    // personalNum = 0L;
    // }else if(personalNum<0&&personalNum-sj>0){
    // personalNum = 0L;
    // }else{
    // personalNum = personalNum-sj;
    // }
    // RedisHelper.set("USER_PERSONAL_CONTROL_NUM"+player.getUser().getId(), String.valueOf(personalNum));
    // }else if(FishingUtil.q0[13]!=0){//进入场控
    // if(FishingUtil.q0[13]>0&&FishingUtil.q0[13]-sj<0){
    // FishingUtil.q0[13] = 0L;
    // }else if(FishingUtil.q0[13]<0&&FishingUtil.q0[13]-sj>0){
    // FishingUtil.q0[13] = 0L;
    // }else{
    // FishingUtil.q0[13] = FishingUtil.q0[13]-sj;
    // }
    // List<Long> peakMaxNum1_obj = new ArrayList<Long>();
    // peakMaxNum1_obj = Arrays.asList(FishingUtil.q0);
    // RedisHelper.set("pumpNum", String.valueOf(peakMaxNum1_obj));
    // }else{
    // RedisHelper.set("ALL_CX_USER" + player.getUser().getId(), String.valueOf(cx + sj));
    // }
    // }
    // if(config.getFishType2()==1){
    // RedisHelper.set("ALL_XH_1-50"+player.getUser().getId(),String.valueOf(xh-gr));
    // }else if(config.getFishType2()==2){
    // RedisHelper.set("ALL_XH_50-100"+player.getUser().getId(),String.valueOf(xh-gr));
    // }else if(config.getFishType2()==3){
    //// RedisHelper.set("ALL_XH_100-200"+player.getUser().getId(),String.valueOf(xh-gr));
    // }else{
    // RedisHelper.set("ALL_XH_200-max"+player.getUser().getId(),String.valueOf(xh-gr));
    // }
    // long x = RedisUtil.val("USER_T_STATUS"+player.getUser().getId(),0L);
    // if(player instanceof FishingGrGameRobot){
    // RedisHelper.set("USER_T_MAX"+player.getUser().getId(),String.valueOf(FishingUtil.burstOne[4]));
    // RedisHelper.set("USER_T_MIN"+player.getUser().getId(),String.valueOf(FishingUtil.recOne[4]));
    // }else{
    // if(x==1){
    //// if((FishingUtil.peakMax1[(roomIndex*2)-1]*banlace - (player.getMoney()+winMoney))<list.get(3)){
    //// int c = ThreadLocalRandom.current().nextInt(1,4);
    //// switch (c){
    //// case 1:{
    //// RedisHelper.set("USER_T_MAX"+player.getUser().getId(),String.valueOf(FishingUtil.burstOne[6]));
    //// RedisHelper.set("USER_T_MIN"+player.getUser().getId(),String.valueOf(FishingUtil.recOne[6]));
    //// break;
    //// }
    //// case 2:{
    //// RedisHelper.set("USER_T_MAX"+player.getUser().getId(),String.valueOf(FishingUtil.burstOne[7]));
    //// RedisHelper.set("USER_T_MIN"+player.getUser().getId(),String.valueOf(FishingUtil.recOne[7]));
    //// break;
    //// }
    //// case 3:{
    //// RedisHelper.set("USER_T_MAX"+player.getUser().getId(),String.valueOf(FishingUtil.burstOne[8]));
    //// RedisHelper.set("USER_T_MIN"+player.getUser().getId(),String.valueOf(FishingUtil.recOne[8]));
    //// break;
    //// }
    //// default:{
    //// break;
    //// }
    //// }
    //// int peakNum = RedisUtil.val("USER_T_PEAK_VALUE"+player.getUser().getId(),0);
    //// if(RedisUtil.val("USER_USE_PEAK"+player.getUser().getId(),0)+1>=peakNum){
    //// RedisHelper.set("USER_T_PEAK_VALUE"+player.getUser().getId(),String.valueOf(CommonLobbyManager.getUserT(player.getUser(),13)));
    //// }else{
    //// RedisHelper.set("USER_USE_PEAK"+player.getUser().getId(),String.valueOf(RedisUtil.val("USER_USE_PEAK"+player.getUser().getId(),0)+1));
    //// }
    //// }
    //// else{
    // if(player.getMoney()+winMoney>FishingUtil.peakMax1[(roomIndex*2)-1]*banlace){
    // int c = ThreadLocalRandom.current().nextInt(1,4);
    // switch (c){
    // case 1:{
    // RedisHelper.set("USER_T_MAX"+player.getUser().getId(),String.valueOf(FishingUtil.burstOne[6]));
    // RedisHelper.set("USER_T_MIN"+player.getUser().getId(),String.valueOf(FishingUtil.recOne[6]));
    // break;
    // }
    // case 2:{
    // RedisHelper.set("USER_T_MAX"+player.getUser().getId(),String.valueOf(FishingUtil.burstOne[7]));
    // RedisHelper.set("USER_T_MIN"+player.getUser().getId(),String.valueOf(FishingUtil.recOne[7]));
    // break;
    // }
    // case 3:{
    // RedisHelper.set("USER_T_MAX"+player.getUser().getId(),String.valueOf(FishingUtil.burstOne[8]));
    // RedisHelper.set("USER_T_MIN"+player.getUser().getId(),String.valueOf(FishingUtil.recOne[8]));
    // break;
    // }
    // default:{
    // break;
    // }
    // }
    // int peakNum = RedisUtil.val("USER_T_PEAK_VALUE"+player.getUser().getId(),0);
    // if(RedisUtil.val("USER_USE_PEAK"+player.getUser().getId(),0)+1>=peakNum){
    // RedisHelper.set("USER_T_PEAK_VALUE"+player.getUser().getId(),String.valueOf(CommonLobbyManager.getUserT(player.getUser(),13)));
    // }else{
    // RedisHelper.set("USER_USE_PEAK"+player.getUser().getId(),String.valueOf(RedisUtil.val("USER_USE_PEAK"+player.getUser().getId(),0)+1));
    // }
    // }
    // else if(player.getMoney()+winMoney<FishingUtil.peakMin1[(roomIndex*2)-1]*banlace){
    // int c = ThreadLocalRandom.current().nextInt(1,4);
    // switch (c){
    // case 1:{
    // RedisHelper.set("USER_T_MAX"+player.getUser().getId(),String.valueOf(FishingUtil.burstOne[0]));
    // RedisHelper.set("USER_T_MIN"+player.getUser().getId(),String.valueOf(FishingUtil.recOne[0]));
    // break;
    // }
    // case 2:{
    // RedisHelper.set("USER_T_MAX"+player.getUser().getId(),String.valueOf(FishingUtil.burstOne[1]));
    // RedisHelper.set("USER_T_MIN"+player.getUser().getId(),String.valueOf(FishingUtil.recOne[1]));
    // break;
    // }
    // case 3:{
    // RedisHelper.set("USER_T_MAX"+player.getUser().getId(),String.valueOf(FishingUtil.burstOne[2]));
    // RedisHelper.set("USER_T_MIN"+player.getUser().getId(),String.valueOf(FishingUtil.recOne[2]));
    // break;
    // }
    // default:{
    // break;
    // }
    // }
    //
    // int peakNum = RedisUtil.val("USER_T_PEAK_VALUE"+player.getUser().getId(),0);
    // if(RedisUtil.val("USER_USE_PEAK"+player.getUser().getId(),0)+1>=peakNum){
    // RedisHelper.set("USER_T_PEAK_VALUE"+player.getUser().getId(),String.valueOf(CommonLobbyManager.getUserT(player.getUser(),13)));
    // }else{
    // RedisHelper.set("USER_USE_PEAK"+player.getUser().getId(),String.valueOf(RedisUtil.val("USER_USE_PEAK"+player.getUser().getId(),0)+1));
    // }
    // }
    //// }
    // }
    // else if(x==2){
    //// if((FishingUtil.peakMax2[(roomIndex*2)-1]*banlace - (player.getMoney()+winMoney))<list.get(3)){
    //// int c = ThreadLocalRandom.current().nextInt(1,4);
    //// switch (c){
    //// case 1:{
    //// RedisHelper.set("USER_T_MAX"+player.getUser().getId(),String.valueOf(FishingUtil.burstOne[6]));
    //// RedisHelper.set("USER_T_MIN"+player.getUser().getId(),String.valueOf(FishingUtil.recOne[6]));
    //// break;
    //// }
    //// case 2:{
    //// RedisHelper.set("USER_T_MAX"+player.getUser().getId(),String.valueOf(FishingUtil.burstOne[7]));
    //// RedisHelper.set("USER_T_MIN"+player.getUser().getId(),String.valueOf(FishingUtil.recOne[7]));
    //// break;
    //// }
    //// case 3:{
    //// RedisHelper.set("USER_T_MAX"+player.getUser().getId(),String.valueOf(FishingUtil.burstOne[8]));
    //// RedisHelper.set("USER_T_MIN"+player.getUser().getId(),String.valueOf(FishingUtil.recOne[8]));
    //// break;
    //// }
    //// default:{
    //// break;
    //// }
    //// }
    //// int peakNum = RedisUtil.val("USER_T_PEAK_VALUE"+player.getUser().getId(),0);
    //// if(RedisUtil.val("USER_USE_PEAK"+player.getUser().getId(),0)+1>=peakNum){
    //// RedisHelper.set("USER_T_PEAK_VALUE"+player.getUser().getId(),String.valueOf(CommonLobbyManager.getUserT(player.getUser(),13)));
    //// }else{
    //// RedisHelper.set("USER_USE_PEAK"+player.getUser().getId(),String.valueOf(RedisUtil.val("USER_USE_PEAK"+player.getUser().getId(),0)+1));
    //// }
    //// }
    //// else{
    // if(player.getMoney()+winMoney>FishingUtil.peakMax2[(roomIndex*2)-1]*banlace){
    // int c = ThreadLocalRandom.current().nextInt(1,4);
    // switch (c){
    // case 1:{
    // RedisHelper.set("USER_T_MAX"+player.getUser().getId(),String.valueOf(FishingUtil.burstOne[6]));
    // RedisHelper.set("USER_T_MIN"+player.getUser().getId(),String.valueOf(FishingUtil.recOne[6]));
    // break;
    // }
    // case 2:{
    // RedisHelper.set("USER_T_MAX"+player.getUser().getId(),String.valueOf(FishingUtil.burstOne[7]));
    // RedisHelper.set("USER_T_MIN"+player.getUser().getId(),String.valueOf(FishingUtil.recOne[7]));
    // break;
    // }
    // case 3:{
    // RedisHelper.set("USER_T_MAX"+player.getUser().getId(),String.valueOf(FishingUtil.burstOne[8]));
    // RedisHelper.set("USER_T_MIN"+player.getUser().getId(),String.valueOf(FishingUtil.recOne[8]));
    // break;
    // }
    // default:{
    // break;
    // }
    // }
    // int peakNum = RedisUtil.val("USER_T_PEAK_VALUE"+player.getUser().getId(),0);
    // if(RedisUtil.val("USER_USE_PEAK"+player.getUser().getId(),0)+1>=peakNum){
    // RedisHelper.set("USER_T_PEAK_VALUE"+player.getUser().getId(),String.valueOf(CommonLobbyManager.getUserT(player.getUser(),13)));
    // }else{
    // RedisHelper.set("USER_USE_PEAK"+player.getUser().getId(),String.valueOf(RedisUtil.val("USER_USE_PEAK"+player.getUser().getId(),0)+1));
    // }
    // }
    // else if(player.getMoney()+winMoney<FishingUtil.peakMin2[(roomIndex*2)-1]*banlace){
    // int c = ThreadLocalRandom.current().nextInt(1,4);
    // switch (c){
    // case 1:{
    // RedisHelper.set("USER_T_MAX"+player.getUser().getId(),String.valueOf(FishingUtil.burstOne[0]));
    // RedisHelper.set("USER_T_MIN"+player.getUser().getId(),String.valueOf(FishingUtil.recOne[0]));
    // break;
    // }
    // case 2:{
    // RedisHelper.set("USER_T_MAX"+player.getUser().getId(),String.valueOf(FishingUtil.burstOne[1]));
    // RedisHelper.set("USER_T_MIN"+player.getUser().getId(),String.valueOf(FishingUtil.recOne[1]));
    // break;
    // }
    // case 3:{
    // RedisHelper.set("USER_T_MAX"+player.getUser().getId(),String.valueOf(FishingUtil.burstOne[2]));
    // RedisHelper.set("USER_T_MIN"+player.getUser().getId(),String.valueOf(FishingUtil.recOne[2]));
    // break;
    // }
    // default:{
    // break;
    // }
    // }
    // int peakNum = RedisUtil.val("USER_T_PEAK_VALUE"+player.getUser().getId(),0);
    // if(RedisUtil.val("USER_USE_PEAK"+player.getUser().getId(),0)+1>=peakNum){
    // RedisHelper.set("USER_T_PEAK_VALUE"+player.getUser().getId(),String.valueOf(CommonLobbyManager.getUserT(player.getUser(),13)));
    // }else{
    // RedisHelper.set("USER_USE_PEAK"+player.getUser().getId(),String.valueOf(RedisUtil.val("USER_USE_PEAK"+player.getUser().getId(),0)+1));
    // }
    // }
    //// }
    // }
    // else if(x==3){
    //// if((FishingUtil.peakMax3[(roomIndex*2)-1]*banlace) - (player.getMoney()+winMoney)<list.get(3)){
    //// int c = ThreadLocalRandom.current().nextInt(1,4);
    //// switch (c){
    //// case 1:{
    //// RedisHelper.set("USER_T_MAX"+player.getUser().getId(),String.valueOf(FishingUtil.burstOne[6]));
    //// RedisHelper.set("USER_T_MIN"+player.getUser().getId(),String.valueOf(FishingUtil.recOne[6]));
    //// break;
    //// }
    //// case 2:{
    //// RedisHelper.set("USER_T_MAX"+player.getUser().getId(),String.valueOf(FishingUtil.burstOne[7]));
    //// RedisHelper.set("USER_T_MIN"+player.getUser().getId(),String.valueOf(FishingUtil.recOne[7]));
    //// break;
    //// }
    //// case 3:{
    //// RedisHelper.set("USER_T_MAX"+player.getUser().getId(),String.valueOf(FishingUtil.burstOne[8]));
    //// RedisHelper.set("USER_T_MIN"+player.getUser().getId(),String.valueOf(FishingUtil.recOne[8]));
    //// break;
    //// }
    //// default:{
    //// break;
    //// }
    //// }
    //// int peakNum = RedisUtil.val("USER_T_PEAK_VALUE"+player.getUser().getId(),0);
    //// if(RedisUtil.val("USER_USE_PEAK"+player.getUser().getId(),0)+1>=peakNum){
    //// RedisHelper.set("USER_T_PEAK_VALUE"+player.getUser().getId(),String.valueOf(CommonLobbyManager.getUserT(player.getUser(),13)));
    //// }else{
    //// RedisHelper.set("USER_USE_PEAK"+player.getUser().getId(),String.valueOf(RedisUtil.val("USER_USE_PEAK"+player.getUser().getId(),0)+1));
    //// }
    //// }
    //// else{
    // if(player.getMoney()+winMoney>FishingUtil.peakMax3[(roomIndex*2)-1]*banlace){
    // int c = ThreadLocalRandom.current().nextInt(1,4);
    // switch (c){
    // case 1:{
    // RedisHelper.set("USER_T_MAX"+player.getUser().getId(),String.valueOf(FishingUtil.burstOne[6]));
    // RedisHelper.set("USER_T_MIN"+player.getUser().getId(),String.valueOf(FishingUtil.recOne[6]));
    // break;
    // }
    // case 2:{
    // RedisHelper.set("USER_T_MAX"+player.getUser().getId(),String.valueOf(FishingUtil.burstOne[7]));
    // RedisHelper.set("USER_T_MIN"+player.getUser().getId(),String.valueOf(FishingUtil.recOne[7]));
    // break;
    // }
    // case 3:{
    // RedisHelper.set("USER_T_MAX"+player.getUser().getId(),String.valueOf(FishingUtil.burstOne[8]));
    // RedisHelper.set("USER_T_MIN"+player.getUser().getId(),String.valueOf(FishingUtil.recOne[8]));
    // break;
    // }
    // default:{
    // break;
    // }
    // }
    // int peakNum = RedisUtil.val("USER_T_PEAK_VALUE"+player.getUser().getId(),0);
    // if(RedisUtil.val("USER_USE_PEAK"+player.getUser().getId(),0)+1>=peakNum){
    // RedisHelper.set("USER_T_PEAK_VALUE"+player.getUser().getId(),String.valueOf(CommonLobbyManager.getUserT(player.getUser(),13)));
    // }else{
    // RedisHelper.set("USER_USE_PEAK"+player.getUser().getId(),String.valueOf(RedisUtil.val("USER_USE_PEAK"+player.getUser().getId(),0)+1));
    // }
    // }
    // else if(player.getMoney()+winMoney<FishingUtil.peakMin3[(roomIndex*2)-1]*banlace){
    // int c = ThreadLocalRandom.current().nextInt(1,4);
    // switch (c){
    // case 1:{
    // RedisHelper.set("USER_T_MAX"+player.getUser().getId(),String.valueOf(FishingUtil.burstOne[0]));
    // RedisHelper.set("USER_T_MIN"+player.getUser().getId(),String.valueOf(FishingUtil.recOne[0]));
    // break;
    // }
    // case 2:{
    // RedisHelper.set("USER_T_MAX"+player.getUser().getId(),String.valueOf(FishingUtil.burstOne[1]));
    // RedisHelper.set("USER_T_MIN"+player.getUser().getId(),String.valueOf(FishingUtil.recOne[1]));
    // break;
    // }
    // case 3:{
    // RedisHelper.set("USER_T_MAX"+player.getUser().getId(),String.valueOf(FishingUtil.burstOne[2]));
    // RedisHelper.set("USER_T_MIN"+player.getUser().getId(),String.valueOf(FishingUtil.recOne[2]));
    // break;
    // }
    // default:{
    // break;
    // }
    // }
    // int peakNum = RedisUtil.val("USER_T_PEAK_VALUE"+player.getUser().getId(),0);
    // if(RedisUtil.val("USER_USE_PEAK"+player.getUser().getId(),0)+1>=peakNum){
    // RedisHelper.set("USER_T_PEAK_VALUE"+player.getUser().getId(),String.valueOf(CommonLobbyManager.getUserT(player.getUser(),13)));
    // }else{
    // RedisHelper.set("USER_USE_PEAK"+player.getUser().getId(),String.valueOf(RedisUtil.val("USER_USE_PEAK"+player.getUser().getId(),0)+1));
    // }
    // }
    //// }
    // }
    // }
    // }
    // }
    // else {
    // if(fireId!=-1){
    // long x = RedisUtil.val("USER_T_STATUS"+player.getUser().getId(),0L);
    // if(player instanceof FishingGrGameRobot){
    // RedisHelper.set("USER_T_MAX"+player.getUser().getId(),String.valueOf(FishingUtil.burstOne[4]));
    // RedisHelper.set("USER_T_MIN"+player.getUser().getId(),String.valueOf(FishingUtil.recOne[4]));
    // }else{
    // if(x==1){
    //// if((FishingUtil.peakMax1[(roomIndex*2)-1]*banlace - (player.getMoney()+winMoney))<list.get(3)){
    //// int c = ThreadLocalRandom.current().nextInt(1,4);
    //// switch (c){
    //// case 1:{
    //// RedisHelper.set("USER_T_MAX"+player.getUser().getId(),String.valueOf(FishingUtil.burstOne[6]));
    //// RedisHelper.set("USER_T_MIN"+player.getUser().getId(),String.valueOf(FishingUtil.recOne[6]));
    //// break;
    //// }
    //// case 2:{
    //// RedisHelper.set("USER_T_MAX"+player.getUser().getId(),String.valueOf(FishingUtil.burstOne[7]));
    //// RedisHelper.set("USER_T_MIN"+player.getUser().getId(),String.valueOf(FishingUtil.recOne[7]));
    //// break;
    //// }
    //// case 3:{
    //// RedisHelper.set("USER_T_MAX"+player.getUser().getId(),String.valueOf(FishingUtil.burstOne[8]));
    //// RedisHelper.set("USER_T_MIN"+player.getUser().getId(),String.valueOf(FishingUtil.recOne[8]));
    //// break;
    //// }
    //// default:{
    //// break;
    //// }
    //// }
    //// int peakNum = RedisUtil.val("USER_T_PEAK_VALUE"+player.getUser().getId(),0);
    //// if(RedisUtil.val("USER_USE_PEAK"+player.getUser().getId(),0)+1>=peakNum){
    //// RedisHelper.set("USER_T_PEAK_VALUE"+player.getUser().getId(),String.valueOf(CommonLobbyManager.getUserT(player.getUser(),13)));
    //// }else{
    //// RedisHelper.set("USER_USE_PEAK"+player.getUser().getId(),String.valueOf(RedisUtil.val("USER_USE_PEAK"+player.getUser().getId(),0)+1));
    //// }
    //// }
    //// else{
    // if(player.getMoney()+winMoney>FishingUtil.peakMax1[(roomIndex*2)-1]*banlace){
    // int c = ThreadLocalRandom.current().nextInt(1,4);
    // switch (c){
    // case 1:{
    // RedisHelper.set("USER_T_MAX"+player.getUser().getId(),String.valueOf(FishingUtil.burstOne[6]));
    // RedisHelper.set("USER_T_MIN"+player.getUser().getId(),String.valueOf(FishingUtil.recOne[6]));
    // break;
    // }
    // case 2:{
    // RedisHelper.set("USER_T_MAX"+player.getUser().getId(),String.valueOf(FishingUtil.burstOne[7]));
    // RedisHelper.set("USER_T_MIN"+player.getUser().getId(),String.valueOf(FishingUtil.recOne[7]));
    // break;
    // }
    // case 3:{
    // RedisHelper.set("USER_T_MAX"+player.getUser().getId(),String.valueOf(FishingUtil.burstOne[8]));
    // RedisHelper.set("USER_T_MIN"+player.getUser().getId(),String.valueOf(FishingUtil.recOne[8]));
    // break;
    // }
    // default:{
    // break;
    // }
    // }
    // int peakNum = RedisUtil.val("USER_T_PEAK_VALUE"+player.getUser().getId(),0);
    // if(RedisUtil.val("USER_USE_PEAK"+player.getUser().getId(),0)+1>=peakNum){
    // RedisHelper.set("USER_T_PEAK_VALUE"+player.getUser().getId(),String.valueOf(CommonLobbyManager.getUserT(player.getUser(),13)));
    // }else{
    // RedisHelper.set("USER_USE_PEAK"+player.getUser().getId(),String.valueOf(RedisUtil.val("USER_USE_PEAK"+player.getUser().getId(),0)+1));
    // }
    // }
    // else if(player.getMoney()+winMoney<FishingUtil.peakMin1[(roomIndex*2)-1]*banlace){
    // int c = ThreadLocalRandom.current().nextInt(1,4);
    // switch (c){
    // case 1:{
    // RedisHelper.set("USER_T_MAX"+player.getUser().getId(),String.valueOf(FishingUtil.burstOne[0]));
    // RedisHelper.set("USER_T_MIN"+player.getUser().getId(),String.valueOf(FishingUtil.recOne[0]));
    // break;
    // }
    // case 2:{
    // RedisHelper.set("USER_T_MAX"+player.getUser().getId(),String.valueOf(FishingUtil.burstOne[1]));
    // RedisHelper.set("USER_T_MIN"+player.getUser().getId(),String.valueOf(FishingUtil.recOne[1]));
    // break;
    // }
    // case 3:{
    // RedisHelper.set("USER_T_MAX"+player.getUser().getId(),String.valueOf(FishingUtil.burstOne[2]));
    // RedisHelper.set("USER_T_MIN"+player.getUser().getId(),String.valueOf(FishingUtil.recOne[2]));
    // break;
    // }
    // default:{
    // break;
    // }
    // }
    //
    // int peakNum = RedisUtil.val("USER_T_PEAK_VALUE"+player.getUser().getId(),0);
    // if(RedisUtil.val("USER_USE_PEAK"+player.getUser().getId(),0)+1>=peakNum){
    // RedisHelper.set("USER_T_PEAK_VALUE"+player.getUser().getId(),String.valueOf(CommonLobbyManager.getUserT(player.getUser(),13)));
    // }else{
    // RedisHelper.set("USER_USE_PEAK"+player.getUser().getId(),String.valueOf(RedisUtil.val("USER_USE_PEAK"+player.getUser().getId(),0)+1));
    // }
    // }
    //// }
    // }
    // else if(x==2){
    //// if((FishingUtil.peakMax2[(roomIndex*2)-1]*banlace - (player.getMoney()+winMoney))<list.get(3)){
    //// int c = ThreadLocalRandom.current().nextInt(1,4);
    //// switch (c){
    //// case 1:{
    //// RedisHelper.set("USER_T_MAX"+player.getUser().getId(),String.valueOf(FishingUtil.burstOne[6]));
    //// RedisHelper.set("USER_T_MIN"+player.getUser().getId(),String.valueOf(FishingUtil.recOne[6]));
    //// break;
    //// }
    //// case 2:{
    //// RedisHelper.set("USER_T_MAX"+player.getUser().getId(),String.valueOf(FishingUtil.burstOne[7]));
    //// RedisHelper.set("USER_T_MIN"+player.getUser().getId(),String.valueOf(FishingUtil.recOne[7]));
    //// break;
    //// }
    //// case 3:{
    //// RedisHelper.set("USER_T_MAX"+player.getUser().getId(),String.valueOf(FishingUtil.burstOne[8]));
    //// RedisHelper.set("USER_T_MIN"+player.getUser().getId(),String.valueOf(FishingUtil.recOne[8]));
    //// break;
    //// }
    //// default:{
    //// break;
    //// }
    //// }
    //// int peakNum = RedisUtil.val("USER_T_PEAK_VALUE"+player.getUser().getId(),0);
    //// if(RedisUtil.val("USER_USE_PEAK"+player.getUser().getId(),0)+1>=peakNum){
    //// RedisHelper.set("USER_T_PEAK_VALUE"+player.getUser().getId(),String.valueOf(CommonLobbyManager.getUserT(player.getUser(),13)));
    //// }else{
    //// RedisHelper.set("USER_USE_PEAK"+player.getUser().getId(),String.valueOf(RedisUtil.val("USER_USE_PEAK"+player.getUser().getId(),0)+1));
    //// }
    //// }
    //// else{
    // if(player.getMoney()+winMoney>FishingUtil.peakMax2[(roomIndex*2)-1]*banlace){
    // int c = ThreadLocalRandom.current().nextInt(1,4);
    // switch (c){
    // case 1:{
    // RedisHelper.set("USER_T_MAX"+player.getUser().getId(),String.valueOf(FishingUtil.burstOne[6]));
    // RedisHelper.set("USER_T_MIN"+player.getUser().getId(),String.valueOf(FishingUtil.recOne[6]));
    // break;
    // }
    // case 2:{
    // RedisHelper.set("USER_T_MAX"+player.getUser().getId(),String.valueOf(FishingUtil.burstOne[7]));
    // RedisHelper.set("USER_T_MIN"+player.getUser().getId(),String.valueOf(FishingUtil.recOne[7]));
    // break;
    // }
    // case 3:{
    // RedisHelper.set("USER_T_MAX"+player.getUser().getId(),String.valueOf(FishingUtil.burstOne[8]));
    // RedisHelper.set("USER_T_MIN"+player.getUser().getId(),String.valueOf(FishingUtil.recOne[8]));
    // break;
    // }
    // default:{
    // break;
    // }
    // }
    // int peakNum = RedisUtil.val("USER_T_PEAK_VALUE"+player.getUser().getId(),0);
    // if(RedisUtil.val("USER_USE_PEAK"+player.getUser().getId(),0)+1>=peakNum){
    // RedisHelper.set("USER_T_PEAK_VALUE"+player.getUser().getId(),String.valueOf(CommonLobbyManager.getUserT(player.getUser(),13)));
    // }else{
    // RedisHelper.set("USER_USE_PEAK"+player.getUser().getId(),String.valueOf(RedisUtil.val("USER_USE_PEAK"+player.getUser().getId(),0)+1));
    // }
    // }
    // else if(player.getMoney()+winMoney<FishingUtil.peakMin2[(roomIndex*2)-1]*banlace){
    // int c = ThreadLocalRandom.current().nextInt(1,4);
    // switch (c){
    // case 1:{
    // RedisHelper.set("USER_T_MAX"+player.getUser().getId(),String.valueOf(FishingUtil.burstOne[0]));
    // RedisHelper.set("USER_T_MIN"+player.getUser().getId(),String.valueOf(FishingUtil.recOne[0]));
    // break;
    // }
    // case 2:{
    // RedisHelper.set("USER_T_MAX"+player.getUser().getId(),String.valueOf(FishingUtil.burstOne[1]));
    // RedisHelper.set("USER_T_MIN"+player.getUser().getId(),String.valueOf(FishingUtil.recOne[1]));
    // break;
    // }
    // case 3:{
    // RedisHelper.set("USER_T_MAX"+player.getUser().getId(),String.valueOf(FishingUtil.burstOne[2]));
    // RedisHelper.set("USER_T_MIN"+player.getUser().getId(),String.valueOf(FishingUtil.recOne[2]));
    // break;
    // }
    // default:{
    // break;
    // }
    // }
    // int peakNum = RedisUtil.val("USER_T_PEAK_VALUE"+player.getUser().getId(),0);
    // if(RedisUtil.val("USER_USE_PEAK"+player.getUser().getId(),0)+1>=peakNum){
    // RedisHelper.set("USER_T_PEAK_VALUE"+player.getUser().getId(),String.valueOf(CommonLobbyManager.getUserT(player.getUser(),13)));
    // }else{
    // RedisHelper.set("USER_USE_PEAK"+player.getUser().getId(),String.valueOf(RedisUtil.val("USER_USE_PEAK"+player.getUser().getId(),0)+1));
    // }
    // }
    //// }
    // }
    // else if(x==3){
    //// if((FishingUtil.peakMax3[(roomIndex*2)-1]*banlace) - (player.getMoney()+winMoney)<list.get(3)){
    //// int c = ThreadLocalRandom.current().nextInt(1,4);
    //// switch (c){
    //// case 1:{
    //// RedisHelper.set("USER_T_MAX"+player.getUser().getId(),String.valueOf(FishingUtil.burstOne[6]));
    //// RedisHelper.set("USER_T_MIN"+player.getUser().getId(),String.valueOf(FishingUtil.recOne[6]));
    //// break;
    //// }
    //// case 2:{
    //// RedisHelper.set("USER_T_MAX"+player.getUser().getId(),String.valueOf(FishingUtil.burstOne[7]));
    //// RedisHelper.set("USER_T_MIN"+player.getUser().getId(),String.valueOf(FishingUtil.recOne[7]));
    //// break;
    //// }
    //// case 3:{
    //// RedisHelper.set("USER_T_MAX"+player.getUser().getId(),String.valueOf(FishingUtil.burstOne[8]));
    //// RedisHelper.set("USER_T_MIN"+player.getUser().getId(),String.valueOf(FishingUtil.recOne[8]));
    //// break;
    //// }
    //// default:{
    //// break;
    //// }
    //// }
    //// int peakNum = RedisUtil.val("USER_T_PEAK_VALUE"+player.getUser().getId(),0);
    //// if(RedisUtil.val("USER_USE_PEAK"+player.getUser().getId(),0)+1>=peakNum){
    //// RedisHelper.set("USER_T_PEAK_VALUE"+player.getUser().getId(),String.valueOf(CommonLobbyManager.getUserT(player.getUser(),13)));
    //// }else{
    //// RedisHelper.set("USER_USE_PEAK"+player.getUser().getId(),String.valueOf(RedisUtil.val("USER_USE_PEAK"+player.getUser().getId(),0)+1));
    //// }
    //// }
    //// else{
    // if(player.getMoney()+winMoney>FishingUtil.peakMax3[(roomIndex*2)-1]*banlace){
    // int c = ThreadLocalRandom.current().nextInt(1,4);
    // switch (c){
    // case 1:{
    // RedisHelper.set("USER_T_MAX"+player.getUser().getId(),String.valueOf(FishingUtil.burstOne[6]));
    // RedisHelper.set("USER_T_MIN"+player.getUser().getId(),String.valueOf(FishingUtil.recOne[6]));
    // break;
    // }
    // case 2:{
    // RedisHelper.set("USER_T_MAX"+player.getUser().getId(),String.valueOf(FishingUtil.burstOne[7]));
    // RedisHelper.set("USER_T_MIN"+player.getUser().getId(),String.valueOf(FishingUtil.recOne[7]));
    // break;
    // }
    // case 3:{
    // RedisHelper.set("USER_T_MAX"+player.getUser().getId(),String.valueOf(FishingUtil.burstOne[8]));
    // RedisHelper.set("USER_T_MIN"+player.getUser().getId(),String.valueOf(FishingUtil.recOne[8]));
    // break;
    // }
    // default:{
    // break;
    // }
    // }
    // int peakNum = RedisUtil.val("USER_T_PEAK_VALUE"+player.getUser().getId(),0);
    // if(RedisUtil.val("USER_USE_PEAK"+player.getUser().getId(),0)+1>=peakNum){
    // RedisHelper.set("USER_T_PEAK_VALUE"+player.getUser().getId(),String.valueOf(CommonLobbyManager.getUserT(player.getUser(),13)));
    // }else{
    // RedisHelper.set("USER_USE_PEAK"+player.getUser().getId(),String.valueOf(RedisUtil.val("USER_USE_PEAK"+player.getUser().getId(),0)+1));
    // }
    // }
    // else if(player.getMoney()+winMoney<FishingUtil.peakMin3[(roomIndex*2)-1]*banlace){
    // int c = ThreadLocalRandom.current().nextInt(1,4);
    // switch (c){
    // case 1:{
    // RedisHelper.set("USER_T_MAX"+player.getUser().getId(),String.valueOf(FishingUtil.burstOne[0]));
    // RedisHelper.set("USER_T_MIN"+player.getUser().getId(),String.valueOf(FishingUtil.recOne[0]));
    // break;
    // }
    // case 2:{
    // RedisHelper.set("USER_T_MAX"+player.getUser().getId(),String.valueOf(FishingUtil.burstOne[1]));
    // RedisHelper.set("USER_T_MIN"+player.getUser().getId(),String.valueOf(FishingUtil.recOne[1]));
    // break;
    // }
    // case 3:{
    // RedisHelper.set("USER_T_MAX"+player.getUser().getId(),String.valueOf(FishingUtil.burstOne[2]));
    // RedisHelper.set("USER_T_MIN"+player.getUser().getId(),String.valueOf(FishingUtil.recOne[2]));
    // break;
    // }
    // default:{
    // break;
    // }
    // }
    // int peakNum = RedisUtil.val("USER_T_PEAK_VALUE"+player.getUser().getId(),0);
    // if(RedisUtil.val("USER_USE_PEAK"+player.getUser().getId(),0)+1>=peakNum){
    // RedisHelper.set("USER_T_PEAK_VALUE"+player.getUser().getId(),String.valueOf(CommonLobbyManager.getUserT(player.getUser(),13)));
    // }else{
    // RedisHelper.set("USER_USE_PEAK"+player.getUser().getId(),String.valueOf(RedisUtil.val("USER_USE_PEAK"+player.getUser().getId(),0)+1));
    // }
    // }
    //// }
    // }
    // }
    // }
    // }
    // return tool;
    // }

    /**
     * 判断鱼是否被击中
     */
    private static boolean isHit(FishingGrandPrixPlayer player, int roomIndex, FishStruct fish, FishConfig config,
                                 long needMoney, long winMoney, long fireId) {
        roomIndex = 3;
        long xh1 = 0L;
        boolean tool = false;
        int t = 0;
        Long personalNum =
                new Double(RedisUtil.val("USER_PERSONAL_CONTROL_NUM" + player.getUser().getId(), 0D)).longValue();

        String burstTmax = RedisHelper.get("USER_PERSONAL_CONTROL_BURST_TMAX" + player.getUser().getId());
        Integer[] burstTmax_obj = {0, 0, 0, -40};
        if (burstTmax != null && burstTmax.length() != 0) {
            burstTmax = burstTmax.substring(burstTmax.lastIndexOf("[") + 1).replaceAll("]", "");
            String[] q0 = burstTmax.split(",");
            List<Integer> list = new ArrayList<>();
            for (String q : q0) {
                if (q == null || "".equals(q)) {
                    continue;
                }
                list.add(new Double(Double.parseDouble(q)).intValue());
            }
            burstTmax_obj = list.toArray(new Integer[0]);
        }

        String burstTmin = RedisHelper.get("USER_PERSONAL_CONTROL_BURST_TMIN" + player.getUser().getId());
        Integer[] burstTmin_obj = {-20, -30, -35, -75};
        if (burstTmin != null && burstTmin.length() != 0) {
            burstTmin = burstTmin.substring(burstTmin.lastIndexOf("[") + 1).replaceAll("]", "");
            String[] q0 = burstTmin.split(",");
            List<Integer> list = new ArrayList<>();
            for (String q : q0) {
                if (q == null || "".equals(q)) {
                    continue;
                }
                list.add(new Double(Double.parseDouble(q)).intValue());
            }
            burstTmin_obj = list.toArray(new Integer[0]);
        }

        String recoveryTmax = RedisHelper.get("USER_PERSONAL_CONTROL_RECOVERY_TMAX" + player.getUser().getId());
        Integer[] recoveryTmax_obj = {20, 30, 35, 100};
        if (recoveryTmax != null && recoveryTmax.length() != 0) {
            recoveryTmax = recoveryTmax.substring(recoveryTmax.lastIndexOf("[") + 1).replaceAll("]", "");
            String[] q0 = recoveryTmax.split(",");
            List<Integer> list = new ArrayList<>();
            for (String q : q0) {
                if (q == null || "".equals(q)) {
                    continue;
                }
                list.add(new Double(Double.parseDouble(q)).intValue());
            }
            recoveryTmax_obj = list.toArray(new Integer[0]);
        }

        String recoveryTmin = RedisHelper.get("USER_PERSONAL_CONTROL_RECOVERY_TMIN" + player.getUser().getId());
        Integer[] recoveryTmin_obj = {0, 0, 0, 50};
        if (recoveryTmin != null && recoveryTmin.length() != 0) {
            recoveryTmin = recoveryTmin.substring(recoveryTmin.lastIndexOf("[") + 1).replaceAll("]", "");
            String[] q0 = recoveryTmin.split(",");
            List<Integer> list = new ArrayList<>();
            for (String q : q0) {
                if (q == null || "".equals(q)) {
                    continue;
                }
                list.add(new Double(Double.parseDouble(q)).intValue());
            }
            recoveryTmin_obj = list.toArray(new Integer[0]);
        }
        if (RedisUtil.val("USER_T_STATUS" + player.getUser().getId(), 0L) != 0) {// 判断是否进入付费
            long cx = new Double(RedisUtil.val("ALL_CX_USER" + player.getUser().getId(), 0D)).longValue();
            long bankNumber = RedisUtil.val("USER_T_BANKRUPTCY_NUMBER" + player.getUser().getId(), 0L);
            // double banlace = 0L;
            // if(cx>0){
            // banlace = bankNumber;
            // }else{
            // banlace = bankNumber - (cx * FishingUtil.cxPercentage[(roomIndex*2)-1]*0.01);
            // }
            int tMax = RedisUtil.val("USER_T_MAX" + player.getUser().getId(), 0);
            int tMin = RedisUtil.val("USER_T_MIN" + player.getUser().getId(), 0);
            int tMax1 = RedisUtil.val("USER_T_MAX1" + player.getUser().getId(), 0);
            int tMin1 = RedisUtil.val("USER_T_MIN1" + player.getUser().getId(), 0);
            int tMax2 = RedisUtil.val("USER_T_MAX2" + player.getUser().getId(), 0);
            int tMin2 = RedisUtil.val("USER_T_MIN2" + player.getUser().getId(), 0);
            int tMax3 = RedisUtil.val("USER_T_MAX3" + player.getUser().getId(), 0);
            int tMin3 = RedisUtil.val("USER_T_MIN3" + player.getUser().getId(), 0);
            if (FishingUtil.pumpNum[roomIndex * 2 - 1] > 0) {
                if (FishingUtil.pumpNum[roomIndex * 2 - 1]
                        - new Double(FishingUtil.pump[roomIndex * 2 - 1] * 0.01 * needMoney).longValue() < 0) {
                    player.setCutMoney(player.getCutMoney() + FishingUtil.pumpNum[roomIndex * 2 - 1]);
                    needMoney -= FishingUtil.pumpNum[roomIndex * 2 - 1];
                    FishingUtil.pumpNum[roomIndex * 2 - 1] = 0L;
                } else {
                    player.setCutMoney(player.getCutMoney()
                            + new Double(FishingUtil.pump[roomIndex * 2 - 1] * 0.01 * needMoney).longValue());
                    FishingUtil.pumpNum[roomIndex * 2 - 1] -=
                            new Double(FishingUtil.pump[roomIndex * 2 - 1] * 0.01 * needMoney).longValue();
                    needMoney -= new Double(FishingUtil.pump[roomIndex * 2 - 1] * 0.01 * needMoney).longValue();
                }
                List<Long> peakMaxNum1_obj = new ArrayList<Long>();
                peakMaxNum1_obj = Arrays.asList(FishingUtil.pumpNum);
                RedisHelper.set("pumpNum", String.valueOf(peakMaxNum1_obj));
            }
            if (config.getFishType2() == 1) {
                if (new Double(RedisUtil.val("USER_FISHTYPE_1_T" + player.getUser().getId(), 0D)).intValue() != 0) {
                    t = new Double(RedisUtil.val("USER_FISHTYPE_1_T" + player.getUser().getId(), 0D)).intValue();
                } else {
                    if (personalNum != 0) {// 进入个控
                        if (personalNum > 0) {// 进入个控爆发
                            t = ThreadLocalRandom.current().nextInt(burstTmin_obj[0], burstTmax_obj[0] + 1);
                        } else {
                            t = ThreadLocalRandom.current().nextInt(recoveryTmin_obj[0], recoveryTmax_obj[0] + 1);
                        }
                    } else if (FishingUtil.q0[roomIndex * 2 - 1] != 0) {// 进入场控
                        if (FishingUtil.q0[roomIndex * 2 - 1] > 0) {
                            t = ThreadLocalRandom.current().nextInt(
                                    new Double(FishingUtil.apt[roomIndex * 2 - 1]).intValue(),
                                    new Double(FishingUtil.ap[roomIndex * 2 - 1]).intValue() + 1);
                        } else {
                            t = ThreadLocalRandom.current().nextInt(
                                    new Double(FishingUtil.recMin[roomIndex * 2 - 1]).intValue(),
                                    new Double(FishingUtil.recMax[roomIndex * 2 - 1]).intValue() + 1);
                        }
                    } else {
                        // t = ThreadLocalRandom.current().nextInt(tMin,tMax+1);
                        int lose = RedisUtil.val("USER_LOSE_ALL" + player.getUser().getId(), 0);
                        if (lose == 1) {
                            t = ThreadLocalRandom.current().nextInt(
                                    new Long(FishingUtil.gcOne[roomIndex * 2 - 1]).intValue(),
                                    new Long(FishingUtil.gcBurstOne[roomIndex * 2 - 1]).intValue());
                        } else {
                            t = ThreadLocalRandom.current().nextInt(tMin, tMax + 1);
                        }
                    }

                    RedisHelper.set("USER_FISHTYPE_1_T" + player.getUser().getId(), String.valueOf(t));
                }
                xh1 = new Double(RedisUtil.val("ALL_XH_1-50" + player.getUser().getId(), 0D)).longValue();
                RedisHelper.set("ALL_XH_1-50" + player.getUser().getId(), String.valueOf(xh1 + needMoney));
            } else if (config.getFishType2() == 2) {
                if (new Double(RedisUtil.val("USER_FISHTYPE_2_T" + player.getUser().getId(), 0D)).intValue() != 0) {
                    t = new Double(RedisUtil.val("USER_FISHTYPE_2_T" + player.getUser().getId(), 0D)).intValue();
                } else {
                    if (personalNum != 0) {// 进入个控
                        if (personalNum > 0) {// 进入个控爆发
                            t = ThreadLocalRandom.current().nextInt(burstTmin_obj[1], burstTmax_obj[1] + 1);
                        } else {
                            t = ThreadLocalRandom.current().nextInt(recoveryTmin_obj[1], recoveryTmax_obj[1] + 1);
                        }
                    } else if (FishingUtil.q0[roomIndex * 2 - 1] != 0) {// 进入场控
                        if (FishingUtil.q0[roomIndex * 2 - 1] > 0) {
                            t = ThreadLocalRandom.current().nextInt(
                                    new Double(FishingUtil.apt1[roomIndex * 2 - 1]).intValue(),
                                    new Double(FishingUtil.ap1[roomIndex * 2 - 1]).intValue() + 1);
                        } else {
                            t = ThreadLocalRandom.current().nextInt(
                                    new Double(FishingUtil.recMin1[roomIndex * 2 - 1]).intValue(),
                                    new Double(FishingUtil.recMax1[roomIndex * 2 - 1]).intValue() + 1);
                        }
                    } else {
                        // t = ThreadLocalRandom.current().nextInt(tMin,tMax+1);
                        int lose = RedisUtil.val("USER_LOSE_ALL" + player.getUser().getId(), 0);
                        if (lose == 1) {
                            t = ThreadLocalRandom.current().nextInt(
                                    new Long(FishingUtil.gcTwo[roomIndex * 2 - 1]).intValue(),
                                    new Long(FishingUtil.gcBurstTwo[roomIndex * 2 - 1]).intValue());
                        } else {
                            t = ThreadLocalRandom.current().nextInt(tMin1, tMax1 + 1);
                        }
                    }
                    RedisHelper.set("USER_FISHTYPE_2_T" + player.getUser().getId(), String.valueOf(t));
                }
                xh1 = new Double(RedisUtil.val("ALL_XH_50-100" + player.getUser().getId(), 0D)).longValue();
                RedisHelper.set("ALL_XH_50-100" + player.getUser().getId(), String.valueOf(xh1 + needMoney));
            } else if (config.getFishType2() == 3) {
                if (new Double(RedisUtil.val("USER_FISHTYPE_3_T" + player.getUser().getId(), 0D)).intValue() != 0) {
                    t = new Double(RedisUtil.val("USER_FISHTYPE_3_T" + player.getUser().getId(), 0D)).intValue();
                } else {
                    if (personalNum != 0) {// 进入个控
                        if (personalNum > 0) {// 进入个控爆发
                            t = ThreadLocalRandom.current().nextInt(burstTmin_obj[2], burstTmax_obj[2] + 1);
                        } else {
                            t = ThreadLocalRandom.current().nextInt(recoveryTmin_obj[2], recoveryTmax_obj[2] + 1);
                        }
                    } else if (FishingUtil.q0[roomIndex * 2 - 1] != 0) {// 进入场控
                        if (FishingUtil.q0[roomIndex * 2 - 1] > 0) {
                            t = ThreadLocalRandom.current().nextInt(
                                    new Double(FishingUtil.apt2[roomIndex * 2 - 1]).intValue(),
                                    new Double(FishingUtil.ap2[roomIndex * 2 - 1]).intValue() + 1);
                        } else {
                            t = ThreadLocalRandom.current().nextInt(
                                    new Double(FishingUtil.recMin2[roomIndex * 2 - 1]).intValue(),
                                    new Double(FishingUtil.recMax2[roomIndex * 2 - 1]).intValue() + 1);
                        }
                    } else {
                        int lose = RedisUtil.val("USER_LOSE_ALL" + player.getUser().getId(), 0);
                        if (lose == 1) {
                            t = ThreadLocalRandom.current().nextInt(
                                    new Long(FishingUtil.gcThree[roomIndex * 2 - 1]).intValue(),
                                    new Long(FishingUtil.gcBurstThree[roomIndex * 2 - 1]).intValue());
                        } else {
                            t = ThreadLocalRandom.current().nextInt(tMin2, tMax2 + 1);
                        }
                    }
                    RedisHelper.set("USER_FISHTYPE_3_T" + player.getUser().getId(), String.valueOf(t));
                }
                xh1 = new Double(RedisUtil.val("ALL_XH_100-200" + player.getUser().getId(), 0D)).longValue();
                RedisHelper.set("ALL_XH_100-200" + player.getUser().getId(), String.valueOf(xh1 + needMoney));
            } else {
                if (new Double(RedisUtil.val("USER_FISHTYPE_4_T" + player.getUser().getId(), 0D)).intValue() != 0) {
                    t = new Double(RedisUtil.val("USER_FISHTYPE_4_T" + player.getUser().getId(), 0D)).intValue();
                } else {
                    if (personalNum != 0) {// 进入个控
                        if (personalNum > 0) {// 进入个控爆发
                            t = ThreadLocalRandom.current().nextInt(burstTmin_obj[3], burstTmax_obj[3] + 1);
                        } else {
                            t = ThreadLocalRandom.current().nextInt(recoveryTmin_obj[3], recoveryTmax_obj[3] + 1);
                        }
                    } else if (FishingUtil.q0[roomIndex * 2 - 1] != 0) {// 进入场控
                        if (FishingUtil.q0[roomIndex * 2 - 1] > 0) {
                            t = ThreadLocalRandom.current().nextInt(
                                    new Double(FishingUtil.apt3[roomIndex * 2 - 1]).intValue(),
                                    new Double(FishingUtil.ap3[roomIndex * 2 - 1]).intValue() + 1);
                        } else {
                            t = ThreadLocalRandom.current().nextInt(
                                    new Double(FishingUtil.recMin3[roomIndex * 2 - 1]).intValue(),
                                    new Double(FishingUtil.recMax3[roomIndex * 2 - 1]).intValue() + 1);
                        }
                    } else {
                        int lose = RedisUtil.val("USER_LOSE_ALL" + player.getUser().getId(), 0);
                        if (lose == 1) {
                            t = ThreadLocalRandom.current().nextInt(
                                    new Long(FishingUtil.gcFour[roomIndex * 2 - 1]).intValue(),
                                    new Long(FishingUtil.gcBurstFour[roomIndex * 2 - 1]).intValue());
                        } else {
                            t = ThreadLocalRandom.current().nextInt(tMin3, tMax3 + 1);
                        }
                    }
                    RedisHelper.set("USER_FISHTYPE_4_T" + player.getUser().getId(), String.valueOf(t));
                }
                xh1 = new Double(RedisUtil.val("ALL_XH_200-max" + player.getUser().getId(), 0D)).longValue();
                RedisHelper.set("ALL_XH_200-max" + player.getUser().getId(), String.valueOf(xh1 + needMoney));
                // t = ThreadLocalRandom.current().nextInt(tMin,tMax+1);
            }
            player.setChangeMoney(player.getChangeMoney() + needMoney);
            player.setSpendMoney(player.getSpendMoney() + needMoney);
            long xl = winMoney;
            long sj = new Double(xl * t * 0.01).longValue();
            long gr = xl + sj;
            long xh = 0L;
            // log.info("t:"+t);
            if (config.getFishType2() == 1) {
                xh = new Double(RedisUtil.val("ALL_XH_1-50" + player.getUser().getId(), 0D)).longValue();
            } else if (config.getFishType2() == 2) {
                xh = new Double(RedisUtil.val("ALL_XH_50-100" + player.getUser().getId(), 0D)).longValue();
            } else if (config.getFishType2() == 3) {
                xh = new Double(RedisUtil.val("ALL_XH_100-200" + player.getUser().getId(), 0D)).longValue();
            } else {
                xh = new Double(RedisUtil.val("ALL_XH_200-max" + player.getUser().getId(), 0D)).longValue();
            }
            ArrayList<Long> list = new ArrayList<Long>();
            list.add(new Double(RedisUtil.val("ALL_XH_200-max" + player.getUser().getId(), 0D)).longValue());
            list.add(new Double(RedisUtil.val("ALL_XH_1-50" + player.getUser().getId(), 0D)).longValue());
            list.add(new Double(RedisUtil.val("ALL_XH_50-100" + player.getUser().getId(), 0D)).longValue());
            list.add(new Double(RedisUtil.val("ALL_XH_100-200" + player.getUser().getId(), 0D)).longValue());
            Collections.sort(list);
            tool = xh >= gr;
            if (tool) {
                if (fireId != -1) {
                    if (config.getFishType2() != 3) {
                        if (personalNum != 0) {// 进入个控
                            if (personalNum > 0 && personalNum + sj < 0) {
                                personalNum = 0L;
                            } else if (personalNum < 0 && personalNum + sj > 0) {
                                personalNum = 0L;
                            } else {
                                personalNum = personalNum + sj;
                            }
                            RedisHelper.set("USER_PERSONAL_CONTROL_NUM" + player.getUser().getId(),
                                    String.valueOf(personalNum));
                        } else if (FishingUtil.q0[roomIndex * 2 - 1] != 0) {// 进入场控
                            if (FishingUtil.q0[roomIndex * 2 - 1] > 0 && FishingUtil.q0[roomIndex * 2 - 1] + sj < 0) {
                                FishingUtil.q0[roomIndex * 2 - 1] = 0L;
                            } else if (FishingUtil.q0[roomIndex * 2 - 1] < 0
                                    && FishingUtil.q0[roomIndex * 2 - 1] + sj > 0) {
                                FishingUtil.q0[roomIndex * 2 - 1] = 0L;
                            } else {
                                FishingUtil.q0[roomIndex * 2 - 1] = FishingUtil.q0[roomIndex * 2 - 1] + sj;
                            }
                            List<Long> peakMaxNum1_obj = new ArrayList<Long>();
                            peakMaxNum1_obj = Arrays.asList(FishingUtil.q0);
                            RedisHelper.set("q0", String.valueOf(peakMaxNum1_obj));
                        } else {
                            RedisHelper.set("ALL_CX_USER" + player.getUser().getId(), String.valueOf(cx + sj));
                        }
                    }
                    if (config.getFishType2() == 1) {
                        RedisHelper.set("ALL_XH_1-50" + player.getUser().getId(), String.valueOf(xh - gr));
                    } else if (config.getFishType2() == 2) {
                        RedisHelper.set("ALL_XH_50-100" + player.getUser().getId(), String.valueOf(xh - gr));
                    } else if (config.getFishType2() == 3) {
                        RedisHelper.set("ALL_XH_100-200" + player.getUser().getId(), String.valueOf(xh - gr));
                    } else {
                        RedisHelper.set("ALL_XH_200-max" + player.getUser().getId(), String.valueOf(xh - gr));
                    }
                    if (RedisUtil.val("USER_PEAK_MONEY" + player.getUser().getId(), 1D) < RedisUtil
                            .val("USER_PEAK_MONEY_HIS" + player.getUser().getId(), 1D)) {// 当前峰值小于上一阶段峰值
                        if (config.getFishType2() == 1) {
                            RedisHelper.set("USER_T_MAX" + player.getUser().getId(),
                                    String.valueOf(FishingUtil.gcBurstOne[roomIndex * 2 - 1]));
                            RedisHelper.set("USER_T_MIN" + player.getUser().getId(),
                                    String.valueOf(FishingUtil.gcOne[roomIndex * 2 - 1]));
                        } else if (config.getFishType2() == 2) {
                            RedisHelper.set("USER_T_MAX1" + player.getUser().getId(),
                                    String.valueOf(FishingUtil.gcBurstTwo[roomIndex * 2 - 1]));
                            RedisHelper.set("USER_T_MIN1" + player.getUser().getId(),
                                    String.valueOf(FishingUtil.gcTwo[roomIndex * 2 - 1]));
                        } else if (config.getFishType2() == 3) {
                            RedisHelper.set("USER_T_MAX2" + player.getUser().getId(),
                                    String.valueOf(FishingUtil.gcBurstThree[roomIndex * 2 - 1]));
                            RedisHelper.set("USER_T_MIN2" + player.getUser().getId(),
                                    String.valueOf(FishingUtil.gcThree[roomIndex * 2 - 1]));
                        } else {
                            RedisHelper.set("USER_T_MAX3" + player.getUser().getId(),
                                    String.valueOf(FishingUtil.gcBurstFour[roomIndex * 2 - 1]));
                            RedisHelper.set("USER_T_MIN3" + player.getUser().getId(),
                                    String.valueOf(FishingUtil.gcFour[roomIndex * 2 - 1]));
                        }
                        if (player.getMoney() + winMoney < RedisUtil
                                .val("USER_PEAK_MONEY" + player.getUser().getId(), 0D)) {
                            changePeak(player.getUser(), roomIndex * 2 - 1);
                        }
                    } else {
                        if (config.getFishType2() == 1) {
                            RedisHelper.set("USER_T_MAX" + player.getUser().getId(),
                                    String.valueOf(FishingUtil.burstOne[roomIndex * 2 - 1]));
                            RedisHelper.set("USER_T_MIN" + player.getUser().getId(),
                                    String.valueOf(FishingUtil.recOne[roomIndex * 2 - 1]));
                        } else if (config.getFishType2() == 2) {
                            RedisHelper.set("USER_T_MAX1" + player.getUser().getId(),
                                    String.valueOf(FishingUtil.burstTwo[roomIndex * 2 - 1]));
                            RedisHelper.set("USER_T_MIN1" + player.getUser().getId(),
                                    String.valueOf(FishingUtil.recTwo[roomIndex * 2 - 1]));
                        } else if (config.getFishType2() == 3) {
                            RedisHelper.set("USER_T_MAX2" + player.getUser().getId(),
                                    String.valueOf(FishingUtil.burstThree[roomIndex * 2 - 1]));
                            RedisHelper.set("USER_T_MIN2" + player.getUser().getId(),
                                    String.valueOf(FishingUtil.recThree[roomIndex * 2 - 1]));
                        } else {
                            RedisHelper.set("USER_T_MAX3" + player.getUser().getId(),
                                    String.valueOf(FishingUtil.burstFour[roomIndex * 2 - 1]));
                            RedisHelper.set("USER_T_MIN3" + player.getUser().getId(),
                                    String.valueOf(FishingUtil.recFour[roomIndex * 2 - 1]));
                        }
                        if (player.getMoney() + winMoney >= RedisUtil
                                .val("USER_PEAK_MONEY" + player.getUser().getId(), 0D)) {
                            changePeak(player.getUser(), roomIndex * 2 - 1);
                        }
                    }
                }
            } else {
                if (fireId != -1) {
                    long x = RedisUtil.val("USER_T_STATUS" + player.getUser().getId(), 0L);
                    if (RedisUtil.val("USER_PEAK_MONEY" + player.getUser().getId(), 1D) < RedisUtil
                            .val("USER_PEAK_MONEY_HIS" + player.getUser().getId(), 1D)) {// 当前峰值小于上一阶段峰值
                        if (config.getFishType2() == 1) {
                            RedisHelper.set("USER_T_MIN" + player.getUser().getId(),
                                    String.valueOf(FishingUtil.gcOne[roomIndex * 2 - 1]));
                            RedisHelper.set("USER_T_MAX" + player.getUser().getId(),
                                    String.valueOf(FishingUtil.gcBurstOne[roomIndex * 2 - 1]));
                        } else if (config.getFishType2() == 2) {
                            RedisHelper.set("USER_T_MIN1" + player.getUser().getId(),
                                    String.valueOf(FishingUtil.gcTwo[roomIndex * 2 - 1]));
                            RedisHelper.set("USER_T_MAX1" + player.getUser().getId(),
                                    String.valueOf(FishingUtil.gcBurstTwo[roomIndex * 2 - 1]));
                        } else if (config.getFishType2() == 3) {
                            RedisHelper.set("USER_T_MIN2" + player.getUser().getId(),
                                    String.valueOf(FishingUtil.gcThree[roomIndex * 2 - 1]));
                            RedisHelper.set("USER_T_MAX2" + player.getUser().getId(),
                                    String.valueOf(FishingUtil.gcBurstThree[roomIndex * 2 - 1]));
                        } else {
                            RedisHelper.set("USER_T_MIN3" + player.getUser().getId(),
                                    String.valueOf(FishingUtil.gcFour[roomIndex * 2 - 1]));
                            RedisHelper.set("USER_T_MAX3" + player.getUser().getId(),
                                    String.valueOf(FishingUtil.gcBurstFour[roomIndex * 2 - 1]));
                        }
                        if (player.getMoney() + winMoney < RedisUtil
                                .val("USER_PEAK_MONEY" + player.getUser().getId(), 0D)) {
                            changePeak(player.getUser(), roomIndex * 2 - 1);
                        }
                    } else {
                        if (config.getFishType2() == 1) {
                            RedisHelper.set("USER_T_MAX" + player.getUser().getId(),
                                    String.valueOf(FishingUtil.burstOne[roomIndex * 2 - 1]));
                            RedisHelper.set("USER_T_MIN" + player.getUser().getId(),
                                    String.valueOf(FishingUtil.recOne[roomIndex * 2 - 1]));
                        } else if (config.getFishType2() == 2) {
                            RedisHelper.set("USER_T_MAX1" + player.getUser().getId(),
                                    String.valueOf(FishingUtil.burstTwo[roomIndex * 2 - 1]));
                            RedisHelper.set("USER_T_MIN1" + player.getUser().getId(),
                                    String.valueOf(FishingUtil.recTwo[roomIndex * 2 - 1]));
                        } else if (config.getFishType2() == 3) {
                            RedisHelper.set("USER_T_MAX2" + player.getUser().getId(),
                                    String.valueOf(FishingUtil.burstThree[roomIndex * 2 - 1]));
                            RedisHelper.set("USER_T_MIN2" + player.getUser().getId(),
                                    String.valueOf(FishingUtil.recThree[roomIndex * 2 - 1]));
                        } else {
                            RedisHelper.set("USER_T_MAX3" + player.getUser().getId(),
                                    String.valueOf(FishingUtil.burstFour[roomIndex * 2 - 1]));
                            RedisHelper.set("USER_T_MIN3" + player.getUser().getId(),
                                    String.valueOf(FishingUtil.recFour[roomIndex * 2 - 1]));
                        }
                        if (player.getMoney() + winMoney >= RedisUtil
                                .val("USER_PEAK_MONEY" + player.getUser().getId(), 0D)) {
                            changePeak(player.getUser(), roomIndex * 2 - 1);
                        }
                    }
                }
            }
        } else {// 未付费玩家
            long cx = new Double(RedisUtil.val("ALL_CX_USER" + player.getUser().getId(), 0D)).longValue();
            long bankNumber = RedisUtil.val("USER_T_BANKRUPTCY_NUMBER" + player.getUser().getId(), 0L);
            double banlace = 0L;
            if (cx > 0) {
                banlace = bankNumber;
            } else {
                banlace = bankNumber - (cx * FishingUtil.cxPercentage[((roomIndex - 1) * 2)] * 0.01);
            }
            int tMax = RedisUtil.val("USER_T_MAX_NEW" + player.getUser().getId(), 0);
            int tMin = RedisUtil.val("USER_T_MIN_NEW" + player.getUser().getId(), 0);
            int tMax1 = RedisUtil.val("USER_T_MAX_NEW1" + player.getUser().getId(), 0);
            int tMin1 = RedisUtil.val("USER_T_MIN_NEW1" + player.getUser().getId(), 0);
            int tMax2 = RedisUtil.val("USER_T_MAX_NEW2" + player.getUser().getId(), 0);
            int tMin2 = RedisUtil.val("USER_T_MIN_NEW2" + player.getUser().getId(), 0);
            int tMax3 = RedisUtil.val("USER_T_MAX_NEW3" + player.getUser().getId(), 0);
            int tMin3 = RedisUtil.val("USER_T_MIN_NEW3" + player.getUser().getId(), 0);
            if (FishingUtil.pumpNum[(roomIndex - 1) * 2] > 0) {
                if (FishingUtil.pumpNum[(roomIndex - 1) * 2]
                        - new Double(FishingUtil.pump[(roomIndex - 1) * 2] * 0.01 * needMoney).longValue() < 0) {
                    player.setCutMoney(player.getCutMoney() + FishingUtil.pumpNum[(roomIndex - 1) * 2]);
                    FishingUtil.pumpNum[(roomIndex - 1) * 2] = 0L;
                    needMoney -= FishingUtil.pumpNum[(roomIndex - 1) * 2];
                } else {
                    player.setCutMoney(player.getCutMoney()
                            + new Double(FishingUtil.pump[(roomIndex - 1) * 2] * 0.01 * needMoney).longValue());
                    FishingUtil.pumpNum[(roomIndex - 1) * 2] = FishingUtil.pumpNum[(roomIndex - 1) * 2]
                            - new Double(FishingUtil.pump[(roomIndex - 1) * 2] * 0.01 * needMoney).longValue();
                    needMoney -= new Double(FishingUtil.pump[(roomIndex - 1) * 2] * 0.01 * needMoney).longValue();
                }
                List<Long> peakMaxNum1_obj = new ArrayList<Long>();
                peakMaxNum1_obj = Arrays.asList(FishingUtil.pumpNum);
                RedisHelper.set("pumpNum", String.valueOf(peakMaxNum1_obj));
            }
            if (config.getFishType2() == 1) {
                if (new Double(RedisUtil.val("USER_FISHTYPE_1_T" + player.getUser().getId(), 0D)).intValue() != 0) {
                    t = new Double(RedisUtil.val("USER_FISHTYPE_1_T" + player.getUser().getId(), 0D)).intValue();
                } else {
                    if (personalNum != 0) {// 进入个控
                        if (personalNum > 0) {// 进入个控爆发
                            t = ThreadLocalRandom.current().nextInt(burstTmin_obj[0], burstTmax_obj[0] + 1);
                        } else {
                            t = ThreadLocalRandom.current().nextInt(recoveryTmin_obj[0], recoveryTmax_obj[0] + 1);
                        }
                    } else if (FishingUtil.q0[(roomIndex - 1) * 2] != 0) {// 进入场控
                        if (FishingUtil.q0[(roomIndex - 1) * 2] > 0) {
                            t = ThreadLocalRandom.current().nextInt(
                                    new Double(FishingUtil.apt[(roomIndex - 1) * 2]).intValue(),
                                    new Double(FishingUtil.ap[(roomIndex - 1) * 2]).intValue() + 1);
                        } else {
                            t = ThreadLocalRandom.current().nextInt(
                                    new Double(FishingUtil.recMin[(roomIndex - 1) * 2]).intValue(),
                                    new Double(FishingUtil.recMax[(roomIndex - 1) * 2]).intValue() + 1);
                        }
                    } else {
                        int lose = RedisUtil.val("USER_LOSE_ALL" + player.getUser().getId(), 0);
                        if (lose == 1) {
                            t = ThreadLocalRandom.current().nextInt(
                                    new Long(FishingUtil.gcOne[(roomIndex - 1) * 2]).intValue(),
                                    new Long(FishingUtil.gcBurstOne[(roomIndex - 1) * 2]).intValue());
                        } else {
                            t = ThreadLocalRandom.current().nextInt(tMin, tMax + 1);
                        }
                    }
                    RedisHelper.set("USER_FISHTYPE_1_T" + player.getUser().getId(), String.valueOf(t));
                }
                xh1 = new Double(RedisUtil.val("ALL_XH_1-50" + player.getUser().getId(), 0D)).longValue();
                RedisHelper.set("ALL_XH_1-50" + player.getUser().getId(), String.valueOf(xh1 + needMoney));
            } else if (config.getFishType2() == 2) {
                if (new Double(RedisUtil.val("USER_FISHTYPE_2_T" + player.getUser().getId(), 0D)).intValue() != 0) {
                    t = new Double(RedisUtil.val("USER_FISHTYPE_2_T" + player.getUser().getId(), 0D)).intValue();
                } else {
                    if (personalNum != 0) {// 进入个控
                        if (personalNum > 0) {// 进入个控爆发
                            t = ThreadLocalRandom.current().nextInt(burstTmin_obj[1], burstTmax_obj[1] + 1);
                        } else {
                            t = ThreadLocalRandom.current().nextInt(recoveryTmin_obj[1], recoveryTmax_obj[1] + 1);
                        }
                    } else if (FishingUtil.q0[(roomIndex - 1) * 2] != 0) {// 进入场控
                        if (FishingUtil.q0[(roomIndex - 1) * 2] > 0) {
                            t = ThreadLocalRandom.current().nextInt(
                                    new Double(FishingUtil.apt1[(roomIndex - 1) * 2]).intValue(),
                                    new Double(FishingUtil.ap1[(roomIndex - 1) * 2]).intValue() + 1);
                        } else {
                            t = ThreadLocalRandom.current().nextInt(
                                    new Double(FishingUtil.recMin1[(roomIndex - 1) * 2]).intValue(),
                                    new Double(FishingUtil.recMax1[(roomIndex - 1) * 2]).intValue() + 1);
                        }
                    } else {
                        int lose = RedisUtil.val("USER_LOSE_ALL" + player.getUser().getId(), 0);
                        if (lose == 1) {
                            t = ThreadLocalRandom.current().nextInt(
                                    new Long(FishingUtil.gcTwo[(roomIndex - 1) * 2]).intValue(),
                                    new Long(FishingUtil.gcBurstTwo[(roomIndex - 1) * 2]).intValue());
                        } else {
                            t = ThreadLocalRandom.current().nextInt(tMin1, tMax1 + 1);
                        }
                    }
                    RedisHelper.set("USER_FISHTYPE_2_T" + player.getUser().getId(), String.valueOf(t));
                }
                xh1 = new Double(RedisUtil.val("ALL_XH_50-100" + player.getUser().getId(), 0D)).longValue();
                RedisHelper.set("ALL_XH_50-100" + player.getUser().getId(), String.valueOf(xh1 + needMoney));
            } else if (config.getFishType2() == 3) {
                if (new Double(RedisUtil.val("USER_FISHTYPE_3_T" + player.getUser().getId(), 0D)).intValue() != 0) {
                    t = new Double(RedisUtil.val("USER_FISHTYPE_3_T" + player.getUser().getId(), 0D)).intValue();
                } else {
                    if (personalNum != 0) {// 进入个控
                        if (personalNum > 0) {// 进入个控爆发
                            t = ThreadLocalRandom.current().nextInt(burstTmin_obj[2], burstTmax_obj[2] + 1);
                        } else {
                            t = ThreadLocalRandom.current().nextInt(recoveryTmin_obj[2], recoveryTmax_obj[2] + 1);
                        }
                    } else if (FishingUtil.q0[(roomIndex - 1) * 2] != 0) {// 进入场控
                        if (FishingUtil.q0[(roomIndex - 1) * 2] > 0) {
                            t = ThreadLocalRandom.current().nextInt(
                                    new Double(FishingUtil.apt2[(roomIndex - 1) * 2]).intValue(),
                                    new Double(FishingUtil.ap2[(roomIndex - 1) * 2]).intValue() + 1);
                        } else {
                            t = ThreadLocalRandom.current().nextInt(
                                    new Double(FishingUtil.recMin2[(roomIndex - 1) * 2]).intValue(),
                                    new Double(FishingUtil.recMax2[(roomIndex - 1) * 2]).intValue() + 1);
                        }
                    } else {
                        int lose = RedisUtil.val("USER_LOSE_ALL" + player.getUser().getId(), 0);
                        if (lose == 1) {
                            t = ThreadLocalRandom.current().nextInt(
                                    new Long(FishingUtil.gcThree[(roomIndex - 1) * 2]).intValue(),
                                    new Long(FishingUtil.gcBurstThree[(roomIndex - 1) * 2]).intValue());
                        } else {
                            t = ThreadLocalRandom.current().nextInt(tMin2, tMax2 + 1);
                        }
                    }
                    RedisHelper.set("USER_FISHTYPE_3_T" + player.getUser().getId(), String.valueOf(t));
                }
                xh1 = new Double(RedisUtil.val("ALL_XH_100-200" + player.getUser().getId(), 0D)).longValue();
                RedisHelper.set("ALL_XH_100-200" + player.getUser().getId(), String.valueOf(xh1 + needMoney));
            } else {
                if (new Double(RedisUtil.val("USER_FISHTYPE_3_T" + player.getUser().getId(), 0D)).intValue() != 0) {
                    t = new Double(RedisUtil.val("USER_FISHTYPE_3_T" + player.getUser().getId(), 0D)).intValue();
                } else {
                    if (personalNum != 0) {// 进入个控
                        if (personalNum > 0) {// 进入个控爆发
                            t = ThreadLocalRandom.current().nextInt(burstTmin_obj[3], burstTmax_obj[3] + 1);
                        } else {
                            t = ThreadLocalRandom.current().nextInt(recoveryTmin_obj[3], recoveryTmax_obj[3] + 1);
                        }
                    } else if (FishingUtil.q0[(roomIndex - 1) * 2] != 0) {// 进入场控
                        if (FishingUtil.q0[(roomIndex - 1) * 2] > 0) {
                            t = ThreadLocalRandom.current().nextInt(
                                    new Double(FishingUtil.apt3[(roomIndex - 1) * 2]).intValue(),
                                    new Double(FishingUtil.ap3[(roomIndex - 1) * 2]).intValue() + 1);
                        } else {
                            t = ThreadLocalRandom.current().nextInt(
                                    new Double(FishingUtil.recMin3[(roomIndex - 1) * 2]).intValue(),
                                    new Double(FishingUtil.recMax3[(roomIndex - 1) * 2]).intValue() + 1);
                        }
                    } else {
                        int lose = RedisUtil.val("USER_LOSE_ALL" + player.getUser().getId(), 0);
                        if (lose == 1) {
                            t = ThreadLocalRandom.current().nextInt(
                                    new Long(FishingUtil.gcFour[(roomIndex - 1) * 2]).intValue(),
                                    new Long(FishingUtil.gcBurstFour[(roomIndex - 1) * 2]).intValue());
                        } else {
                            t = ThreadLocalRandom.current().nextInt(tMin3, tMax3 + 1);
                        }
                    }
                    RedisHelper.set("USER_FISHTYPE_4_T" + player.getUser().getId(), String.valueOf(t));
                }
                xh1 = new Double(RedisUtil.val("ALL_XH_200-max" + player.getUser().getId(), 0D)).longValue();
                RedisHelper.set("ALL_XH_200-max" + player.getUser().getId(), String.valueOf(xh1 + needMoney));
            }
            long xl = winMoney;
            long sj = new Double(xl * t * 0.01).longValue();
            long gr = xl + sj;
            long xh = 0L;
            if (config.getFishType2() == 1) {
                xh = new Double(RedisUtil.val("ALL_XH_1-50" + player.getUser().getId(), 0D)).longValue();
            } else if (config.getFishType2() == 2) {
                xh = new Double(RedisUtil.val("ALL_XH_50-100" + player.getUser().getId(), 0D)).longValue();
            } else if (config.getFishType2() == 3) {
                xh = new Double(RedisUtil.val("ALL_XH_100-200" + player.getUser().getId(), 0D)).longValue();
            } else {
                xh = new Double(RedisUtil.val("ALL_XH_200-max" + player.getUser().getId(), 0D)).longValue();
            }
            ArrayList<Long> list = new ArrayList<Long>();
            list.add(new Double(RedisUtil.val("ALL_XH_200-max" + player.getUser().getId(), 0D)).longValue());
            list.add(new Double(RedisUtil.val("ALL_XH_1-50" + player.getUser().getId(), 0D)).longValue());
            list.add(new Double(RedisUtil.val("ALL_XH_50-100" + player.getUser().getId(), 0D)).longValue());
            list.add(new Double(RedisUtil.val("ALL_XH_100-200" + player.getUser().getId(), 0D)).longValue());
            Collections.sort(list);
            tool = xh >= gr;
            if (tool) {
                if (fireId != -1) {
                    if (config.getFishType2() != 3) {
                        if (personalNum != 0) {// 进入个控
                            if (personalNum > 0 && personalNum + sj < 0) {
                                personalNum = 0L;
                            } else if (personalNum < 0 && personalNum + sj > 0) {
                                personalNum = 0L;
                            } else {
                                personalNum = personalNum + sj;
                            }
                            RedisHelper.set("USER_PERSONAL_CONTROL_NUM" + player.getUser().getId(),
                                    String.valueOf(personalNum));
                        } else if (FishingUtil.q0[(roomIndex - 1) * 2] != 0) {// 进入场控
                            if (FishingUtil.q0[(roomIndex - 1) * 2] > 0
                                    && FishingUtil.q0[(roomIndex - 1) * 2] + sj < 0) {
                                FishingUtil.q0[(roomIndex - 1) * 2] = 0L;
                            } else if (FishingUtil.q0[(roomIndex - 1) * 2] < 0
                                    && FishingUtil.q0[(roomIndex - 1) * 2] + sj > 0) {
                                FishingUtil.q0[(roomIndex - 1) * 2] = 0L;
                            } else {
                                FishingUtil.q0[(roomIndex - 1) * 2] = FishingUtil.q0[(roomIndex - 1) * 2] + sj;
                            }
                            List<Long> peakMaxNum1_obj = new ArrayList<Long>();
                            peakMaxNum1_obj = Arrays.asList(FishingUtil.q0);
                            RedisHelper.set("pumpNum", String.valueOf(peakMaxNum1_obj));
                        } else {
                            RedisHelper.set("ALL_CX_USER" + player.getUser().getId(), String.valueOf(cx + sj));
                        }
                    }
                    if (config.getFishType2() == 1) {
                        RedisHelper.set("ALL_XH_1-50" + player.getUser().getId(), String.valueOf(xh - gr));
                    } else if (config.getFishType2() == 2) {
                        RedisHelper.set("ALL_XH_50-100" + player.getUser().getId(), String.valueOf(xh - gr));
                    } else if (config.getFishType2() == 3) {
                        RedisHelper.set("ALL_XH_100-200" + player.getUser().getId(), String.valueOf(xh - gr));
                    } else {
                        RedisHelper.set("ALL_XH_200-max" + player.getUser().getId(), String.valueOf(xh - gr));
                    }
                    // long x = RedisUtil.val("USER_T_STATUS_NEW"+player.getUser().getId(),0L);
                    if (RedisUtil.val("USER_PEAK_MONEY" + player.getUser().getId(), 1D) < RedisUtil
                            .val("USER_PEAK_MONEY_HIS" + player.getUser().getId(), 1D)) {// 当前峰值小于上一阶段峰值
                        if (config.getFishType2() == 1) {
                            RedisHelper.set("USER_T_MIN_NEW" + player.getUser().getId(),
                                    String.valueOf(FishingUtil.gcOne[(roomIndex - 1) * 2]));
                            RedisHelper.set("USER_T_MAX_NEW" + player.getUser().getId(),
                                    String.valueOf(FishingUtil.gcBurstOne[(roomIndex - 1) * 2]));
                        } else if (config.getFishType2() == 2) {
                            RedisHelper.set("USER_T_MIN_NEW1" + player.getUser().getId(),
                                    String.valueOf(FishingUtil.gcTwo[(roomIndex - 1) * 2]));
                            RedisHelper.set("USER_T_MAX_NEW1" + player.getUser().getId(),
                                    String.valueOf(FishingUtil.gcBurstTwo[(roomIndex - 1) * 2]));
                        } else if (config.getFishType2() == 3) {
                            RedisHelper.set("USER_T_MIN_NEW2" + player.getUser().getId(),
                                    String.valueOf(FishingUtil.gcThree[(roomIndex - 1) * 2]));
                            RedisHelper.set("USER_T_MAX_NEW2" + player.getUser().getId(),
                                    String.valueOf(FishingUtil.gcBurstThree[(roomIndex - 1) * 2]));
                        } else {
                            RedisHelper.set("USER_T_MIN_NEW3" + player.getUser().getId(),
                                    String.valueOf(FishingUtil.gcFour[(roomIndex - 1) * 2]));
                            RedisHelper.set("USER_T_MAX_NEW3" + player.getUser().getId(),
                                    String.valueOf(FishingUtil.gcBurstFour[(roomIndex - 1) * 2]));
                        }
                        if (player.getMoney() + winMoney < RedisUtil
                                .val("USER_PEAK_MONEY" + player.getUser().getId(), 0D)) {
                            changePeak(player.getUser(), (roomIndex - 1) * 2);
                        }
                    } else {
                        if (config.getFishType2() == 1) {
                            RedisHelper.set("USER_T_MAX_NEW" + player.getUser().getId(),
                                    String.valueOf(FishingUtil.burstOne[(roomIndex - 1) * 2]));
                            RedisHelper.set("USER_T_MIN_NEW" + player.getUser().getId(),
                                    String.valueOf(FishingUtil.recOne[(roomIndex - 1) * 2]));
                        } else if (config.getFishType2() == 2) {
                            RedisHelper.set("USER_T_MAX_NEW1" + player.getUser().getId(),
                                    String.valueOf(FishingUtil.burstTwo[(roomIndex - 1) * 2]));
                            RedisHelper.set("USER_T_MIN_NEW1" + player.getUser().getId(),
                                    String.valueOf(FishingUtil.recTwo[(roomIndex - 1) * 2]));
                        } else if (config.getFishType2() == 3) {
                            RedisHelper.set("USER_T_MAX_NEW2" + player.getUser().getId(),
                                    String.valueOf(FishingUtil.burstThree[(roomIndex - 1) * 2]));
                            RedisHelper.set("USER_T_MIN_NEW2" + player.getUser().getId(),
                                    String.valueOf(FishingUtil.recThree[(roomIndex - 1) * 2]));
                        } else {
                            RedisHelper.set("USER_T_MAX_NEW3" + player.getUser().getId(),
                                    String.valueOf(FishingUtil.burstFour[(roomIndex - 1) * 2]));
                            RedisHelper.set("USER_T_MIN_NEW3" + player.getUser().getId(),
                                    String.valueOf(FishingUtil.recFour[(roomIndex - 1) * 2]));
                        }
                        if (player.getMoney() + winMoney >= RedisUtil
                                .val("USER_PEAK_MONEY" + player.getUser().getId(), 0D)) {
                            changePeak(player.getUser(), (roomIndex - 1) * 2);
                        }
                    }
                }
            } else {
                if (fireId != -1) {
                    // long x = RedisUtil.val("USER_T_STATUS_NEW"+player.getUser().getId(),0L);
                    if (RedisUtil.val("USER_PEAK_MONEY" + player.getUser().getId(), 1D) < RedisUtil
                            .val("USER_PEAK_MONEY_HIS" + player.getUser().getId(), 1D)) {// 当前峰值小于上一阶段峰值
                        if (config.getFishType2() == 1) {
                            RedisHelper.set("USER_T_MIN_NEW" + player.getUser().getId(),
                                    String.valueOf(FishingUtil.gcOne[(roomIndex - 1) * 2]));
                            RedisHelper.set("USER_T_MAX_NEW" + player.getUser().getId(),
                                    String.valueOf(FishingUtil.gcBurstOne[(roomIndex - 1) * 2]));
                        } else if (config.getFishType2() == 2) {
                            RedisHelper.set("USER_T_MIN_NEW1" + player.getUser().getId(),
                                    String.valueOf(FishingUtil.gcTwo[(roomIndex - 1) * 2]));
                            RedisHelper.set("USER_T_MAX_NEW1" + player.getUser().getId(),
                                    String.valueOf(FishingUtil.gcBurstTwo[(roomIndex - 1) * 2]));
                        } else if (config.getFishType2() == 3) {
                            RedisHelper.set("USER_T_MIN_NEW2" + player.getUser().getId(),
                                    String.valueOf(FishingUtil.gcThree[(roomIndex - 1) * 2]));
                            RedisHelper.set("USER_T_MAX_NEW2" + player.getUser().getId(),
                                    String.valueOf(FishingUtil.gcBurstThree[(roomIndex - 1) * 2]));
                        } else {
                            RedisHelper.set("USER_T_MIN_NEW3" + player.getUser().getId(),
                                    String.valueOf(FishingUtil.gcFour[(roomIndex - 1) * 2]));
                            RedisHelper.set("USER_T_MAX_NEW3" + player.getUser().getId(),
                                    String.valueOf(FishingUtil.gcBurstFour[(roomIndex - 1) * 2]));
                        }
                        if (player.getMoney() + winMoney < RedisUtil
                                .val("USER_PEAK_MONEY" + player.getUser().getId(), 0D)) {
                            changePeak(player.getUser(), (roomIndex - 1) * 2);
                        }
                    } else {
                        if (config.getFishType2() == 1) {
                            RedisHelper.set("USER_T_MAX_NEW" + player.getUser().getId(),
                                    String.valueOf(FishingUtil.burstOne[(roomIndex - 1) * 2]));
                            RedisHelper.set("USER_T_MIN_NEW" + player.getUser().getId(),
                                    String.valueOf(FishingUtil.recOne[(roomIndex - 1) * 2]));
                        } else if (config.getFishType2() == 2) {
                            RedisHelper.set("USER_T_MAX_NEW1" + player.getUser().getId(),
                                    String.valueOf(FishingUtil.burstTwo[(roomIndex - 1) * 2]));
                            RedisHelper.set("USER_T_MIN_NEW1" + player.getUser().getId(),
                                    String.valueOf(FishingUtil.recTwo[(roomIndex - 1) * 2]));
                        } else if (config.getFishType2() == 3) {
                            RedisHelper.set("USER_T_MAX_NEW2" + player.getUser().getId(),
                                    String.valueOf(FishingUtil.burstThree[(roomIndex - 1) * 2]));
                            RedisHelper.set("USER_T_MIN_NEW2" + player.getUser().getId(),
                                    String.valueOf(FishingUtil.recThree[(roomIndex - 1) * 2]));
                        } else {
                            RedisHelper.set("USER_T_MAX_NEW3" + player.getUser().getId(),
                                    String.valueOf(FishingUtil.burstFour[(roomIndex - 1) * 2]));
                            RedisHelper.set("USER_T_MIN_NEW3" + player.getUser().getId(),
                                    String.valueOf(FishingUtil.recFour[(roomIndex - 1) * 2]));
                        }
                        if (player.getMoney() + winMoney >= RedisUtil
                                .val("USER_PEAK_MONEY" + player.getUser().getId(), 0D)) {
                            changePeak(player.getUser(), (roomIndex - 1) * 2);
                        }
                    }
                }
            }
        }
        return tool;
    }

    // /**
    // * 判断鱼是否被击中
    // */
    // private static boolean isHit(FishingGrandPrixPlayer player, int roomIndex,FishStruct fish, FishConfig config,long
    // needMoney,
    // long winMoney,long fireId) {
    // long xh1 = 0L;
    // boolean tool = false;
    // int t = 0;
    // Long personalNum = new
    // Double(RedisUtil.val("USER_PERSONAL_CONTROL_NUM"+player.getUser().getId(),0D)).longValue();
    //
    // String burstTmax = RedisHelper.get("USER_PERSONAL_CONTROL_BURST_TMAX"+player.getUser().getId());
    // Integer[] burstTmax_obj = {0,0,0,-40};
    // if(burstTmax!=null&&burstTmax.length()!=0){
    // burstTmax = burstTmax.substring(burstTmax.lastIndexOf("[")+1).replaceAll("]","");
    // String[] q0 = burstTmax.split(",");
    // List<Integer> list = new ArrayList<>();
    // for (String q : q0) {
    // if(q==null||"".equals(q)){
    // continue;
    // }
    // list.add(new Double(Double.parseDouble(q)).intValue());
    // }
    // burstTmax_obj = list.toArray(new Integer[0]);
    // }
    //
    // String burstTmin = RedisHelper.get("USER_PERSONAL_CONTROL_BURST_TMIN"+player.getUser().getId());
    // Integer[] burstTmin_obj = {-20,-30,-35,-75};
    // if(burstTmin!=null&&burstTmin.length()!=0){
    // burstTmin = burstTmin.substring(burstTmin.lastIndexOf("[")+1).replaceAll("]","");
    // String[] q0 = burstTmin.split(",");
    // List<Integer> list = new ArrayList<>();
    // for (String q : q0) {
    // if(q==null||"".equals(q)){
    // continue;
    // }
    // list.add(new Double(Double.parseDouble(q)).intValue());
    // }
    // burstTmin_obj = list.toArray(new Integer[0]);
    // }
    //
    // String recoveryTmax = RedisHelper.get("USER_PERSONAL_CONTROL_RECOVERY_TMAX"+player.getUser().getId());
    // Integer[] recoveryTmax_obj = {20,30,35,100};
    // if(recoveryTmax!=null&&recoveryTmax.length()!=0){
    // recoveryTmax = recoveryTmax.substring(recoveryTmax.lastIndexOf("[")+1).replaceAll("]","");
    // String[] q0 = recoveryTmax.split(",");
    // List<Integer> list = new ArrayList<>();
    // for (String q : q0) {
    // if(q==null||"".equals(q)){
    // continue;
    // }
    // list.add(new Double(Double.parseDouble(q)).intValue());
    // }
    // recoveryTmax_obj = list.toArray(new Integer[0]);
    // }
    //
    // String recoveryTmin = RedisHelper.get("USER_PERSONAL_CONTROL_RECOVERY_TMIN"+player.getUser().getId());
    // Integer[] recoveryTmin_obj = {0,0,0,50};
    // if(recoveryTmin!=null&&recoveryTmin.length()!=0){
    // recoveryTmin = recoveryTmin.substring(recoveryTmin.lastIndexOf("[")+1).replaceAll("]","");
    // String[] q0 = recoveryTmin.split(",");
    // List<Integer> list = new ArrayList<>();
    // for (String q : q0) {
    // if(q==null||"".equals(q)){
    // continue;
    // }
    // list.add(new Double(Double.parseDouble(q)).intValue());
    // }
    // recoveryTmin_obj = list.toArray(new Integer[0]);
    // }
    // if(RedisUtil.val("USER_T_STATUS"+player.getUser().getId(),0L)!=0){//判断是否进入付费
    // long cx = new Double(RedisUtil.val("ALL_CX_USER"+player.getUser().getId(),0D)).longValue();
    // long bankNumber = RedisUtil.val("USER_T_BANKRUPTCY_NUMBER"+player.getUser().getId(),0L);
    // int tMax = RedisUtil.val("USER_T_MAX"+player.getUser().getId(),0);
    // int tMin = RedisUtil.val("USER_T_MIN"+player.getUser().getId(),0);
    // int tMax1 = RedisUtil.val("USER_T_MAX1"+player.getUser().getId(),0);
    // int tMin1 = RedisUtil.val("USER_T_MIN1"+player.getUser().getId(),0);
    // int tMax2 = RedisUtil.val("USER_T_MAX2"+player.getUser().getId(),0);
    // int tMin2 = RedisUtil.val("USER_T_MIN2"+player.getUser().getId(),0);
    // int tMax3 = RedisUtil.val("USER_T_MAX3"+player.getUser().getId(),0);
    // int tMin3 = RedisUtil.val("USER_T_MIN3"+player.getUser().getId(),0);
    // if(FishingUtil.pumpNum[roomIndex*2-1]>0){
    // if(FishingUtil.pumpNum[roomIndex*2-1] - new
    // Double(FishingUtil.pump[roomIndex*2-1]*0.01*needMoney).longValue()<0){
    // player.setCutMoney(player.getCutMoney()+FishingUtil.pumpNum[roomIndex*2-1]);
    // needMoney -= FishingUtil.pumpNum[roomIndex*2-1];
    // FishingUtil.pumpNum[roomIndex*2-1] = 0L;
    // }else{
    // player.setCutMoney(player.getCutMoney()+new Double(FishingUtil.pump[roomIndex*2-1]*0.01*needMoney).longValue());
    // FishingUtil.pumpNum[roomIndex*2-1] -= new Double(FishingUtil.pump[roomIndex*2-1]*0.01*needMoney).longValue();
    // needMoney -= new Double(FishingUtil.pump[roomIndex*2-1]*0.01*needMoney).longValue();
    // }
    // List<Long> peakMaxNum1_obj = new ArrayList<Long>();
    // peakMaxNum1_obj = Arrays.asList(FishingUtil.pumpNum);
    // RedisHelper.set("pumpNum", String.valueOf(peakMaxNum1_obj));
    // }
    // if(config.getFishType2()==1){
    // if (new Double(RedisUtil.val("USER_FISHTYPE_1_T" + player.getUser().getId(), 0D)).intValue() != 0) {
    // t = new Double(RedisUtil.val("USER_FISHTYPE_1_T" + player.getUser().getId(), 0D)).intValue();
    // } else {
    // if(personalNum!=0){//进入个控
    // if(personalNum>0){//进入个控爆发
    // t = ThreadLocalRandom.current().nextInt(burstTmin_obj[0],burstTmax_obj[0]+1);
    // }else{
    // t = ThreadLocalRandom.current().nextInt(recoveryTmin_obj[0],recoveryTmax_obj[0]+1);
    // }
    // }else if(FishingUtil.q0[roomIndex*2-1]!=0){//进入场控
    // if(FishingUtil.q0[roomIndex*2-1]>0){
    // t = ThreadLocalRandom.current().nextInt(new Double(FishingUtil.apt[roomIndex*2-1]).intValue(),new
    // Double(FishingUtil.ap[roomIndex*2-1]).intValue()+1);
    // }else{
    // t = ThreadLocalRandom.current().nextInt(new Double(FishingUtil.recMin[roomIndex*2-1]).intValue(),new
    // Double(FishingUtil.recMax[roomIndex*2-1]).intValue()+1);
    // }
    // }else{
    //// t = ThreadLocalRandom.current().nextInt(tMin,tMax+1);
    // int lose = RedisUtil.val("USER_LOSE_ALL"+player.getUser().getId(),0);
    // if(lose==1){
    // t = ThreadLocalRandom.current().nextInt(new Long(FishingUtil.recOne[2]).intValue(),new
    // Long(FishingUtil.burstOne[2]).intValue());
    // }else{
    // t = ThreadLocalRandom.current().nextInt(tMin,tMax+1);
    // }
    // }
    //
    // RedisHelper.set("USER_FISHTYPE_1_T" + player.getUser().getId(),String.valueOf(t));
    // }
    // xh1 = new Double(RedisUtil.val("ALL_XH_1-50"+player.getUser().getId(),0D)).longValue();
    // RedisHelper.set("ALL_XH_1-50"+player.getUser().getId(),String.valueOf(xh1+needMoney));
    // }else if(config.getFishType2()==2){
    // if (new Double(RedisUtil.val("USER_FISHTYPE_2_T" + player.getUser().getId(), 0D)).intValue() != 0) {
    // t = new Double(RedisUtil.val("USER_FISHTYPE_2_T" + player.getUser().getId(), 0D)).intValue();
    // } else {
    // if(personalNum!=0){//进入个控
    // if(personalNum>0){//进入个控爆发
    // t = ThreadLocalRandom.current().nextInt(burstTmin_obj[1],burstTmax_obj[1]+1);
    // }else{
    // t = ThreadLocalRandom.current().nextInt(recoveryTmin_obj[1],recoveryTmax_obj[1]+1);
    // }
    // }else if(FishingUtil.q0[roomIndex*2-1]!=0){//进入场控
    // if(FishingUtil.q0[roomIndex*2-1]>0){
    // t = ThreadLocalRandom.current().nextInt(new Double(FishingUtil.apt1[roomIndex*2-1]).intValue(),new
    // Double(FishingUtil.ap1[roomIndex*2-1]).intValue()+1);
    // }else{
    // t = ThreadLocalRandom.current().nextInt(new Double(FishingUtil.recMin1[roomIndex*2-1]).intValue(),new
    // Double(FishingUtil.recMax1[roomIndex*2-1]).intValue()+1);
    // }
    // }else{
    // int lose = RedisUtil.val("USER_LOSE_ALL"+player.getUser().getId(),0);
    // if(lose==1){
    // t = ThreadLocalRandom.current().nextInt(new Long(FishingUtil.recOne[5]).intValue(),new
    // Long(FishingUtil.burstOne[2]).intValue());
    // }else{
    // t = ThreadLocalRandom.current().nextInt(tMin1,tMax1+1);
    // }
    // }
    // RedisHelper.set("USER_FISHTYPE_2_T" + player.getUser().getId(),String.valueOf(t));
    // }
    // xh1 = new Double(RedisUtil.val("ALL_XH_50-100"+player.getUser().getId(),0D)).longValue();
    // RedisHelper.set("ALL_XH_50-100"+player.getUser().getId(),String.valueOf(xh1+needMoney));
    // }else if(config.getFishType2()==3){
    // if (new Double(RedisUtil.val("USER_FISHTYPE_3_T" + player.getUser().getId(), 0D)).intValue() != 0) {
    // t = new Double(RedisUtil.val("USER_FISHTYPE_3_T" + player.getUser().getId(), 0D)).intValue();
    // } else {
    // if(personalNum!=0){//进入个控
    // if(personalNum>0){//进入个控爆发
    // t = ThreadLocalRandom.current().nextInt(burstTmin_obj[2],burstTmax_obj[2]+1);
    // }else{
    // t = ThreadLocalRandom.current().nextInt(recoveryTmin_obj[2],recoveryTmax_obj[2]+1);
    // }
    // }else if(FishingUtil.q0[roomIndex*2-1]!=0){//进入场控
    // if(FishingUtil.q0[roomIndex*2-1]>0){
    // t = ThreadLocalRandom.current().nextInt(new Double(FishingUtil.apt2[roomIndex*2-1]).intValue(),new
    // Double(FishingUtil.ap2[roomIndex*2-1]).intValue()+1);
    // }else{
    // t = ThreadLocalRandom.current().nextInt(new Double(FishingUtil.recMin2[roomIndex*2-1]).intValue(),new
    // Double(FishingUtil.recMax2[roomIndex*2-1]).intValue()+1);
    // }
    // }else{
    // int lose = RedisUtil.val("USER_LOSE_ALL"+player.getUser().getId(),0);
    // if(lose==1){
    // t = ThreadLocalRandom.current().nextInt(new Long(FishingUtil.recOne[8]).intValue(),new
    // Long(FishingUtil.burstOne[8]).intValue());
    // }else{
    // t = ThreadLocalRandom.current().nextInt(tMin2,tMax2+1);
    // }
    // }
    // RedisHelper.set("USER_FISHTYPE_3_T" + player.getUser().getId(),String.valueOf(t));
    // }
    // xh1 = new Double(RedisUtil.val("ALL_XH_100-200"+player.getUser().getId(),0D)).longValue();
    // RedisHelper.set("ALL_XH_100-200"+player.getUser().getId(),String.valueOf(xh1+needMoney));
    // }else{
    // if (new Double(RedisUtil.val("USER_FISHTYPE_4_T" + player.getUser().getId(), 0D)).intValue() != 0) {
    // t = new Double(RedisUtil.val("USER_FISHTYPE_4_T" + player.getUser().getId(), 0D)).intValue();
    // } else {
    // if(personalNum!=0){//进入个控
    // if(personalNum>0){//进入个控爆发
    // t = ThreadLocalRandom.current().nextInt(burstTmin_obj[3],burstTmax_obj[3]+1);
    // }else{
    // t = ThreadLocalRandom.current().nextInt(recoveryTmin_obj[3],recoveryTmax_obj[3]+1);
    // }
    // }else if(FishingUtil.q0[roomIndex*2-1]!=0){//进入场控
    // if(FishingUtil.q0[roomIndex*2-1]>0){
    // t = ThreadLocalRandom.current().nextInt(new Double(FishingUtil.apt3[roomIndex*2-1]).intValue(),new
    // Double(FishingUtil.ap3[roomIndex*2-1]).intValue()+1);
    // }else{
    // t = ThreadLocalRandom.current().nextInt(new Double(FishingUtil.recMin3[roomIndex*2-1]).intValue(),new
    // Double(FishingUtil.recMax3[roomIndex*2-1]).intValue()+1);
    // }
    // }else{
    // int lose = RedisUtil.val("USER_LOSE_ALL"+player.getUser().getId(),0);
    // if(lose==1){
    // t = ThreadLocalRandom.current().nextInt(new Long(FishingUtil.recOne[11]).intValue(),new
    // Long(FishingUtil.burstOne[11]).intValue());
    // }else{
    // t = ThreadLocalRandom.current().nextInt(tMin3,tMax3+1);
    // }
    // }
    // RedisHelper.set("USER_FISHTYPE_4_T" + player.getUser().getId(),String.valueOf(t));
    // }
    // xh1 = new Double(RedisUtil.val("ALL_XH_200-max"+player.getUser().getId(),0D)).longValue();
    // RedisHelper.set("ALL_XH_200-max"+player.getUser().getId(),String.valueOf(xh1+needMoney));
    // }
    //
    // player.setChangeMoney(player.getChangeMoney()+needMoney);
    // player.setSpendMoney(player.getSpendMoney() + needMoney);
    // long xl = winMoney;
    // long sj = new Double(xl*t*0.01).longValue();
    // long gr = xl+sj;
    // long xh =0L;
    // if(config.getFishType2()==1){
    // xh = new Double(RedisUtil.val("ALL_XH_1-50"+player.getUser().getId(),0D)).longValue();
    // }else if(config.getFishType2()==2){
    // xh = new Double(RedisUtil.val("ALL_XH_50-100"+player.getUser().getId(),0D)).longValue();
    // }else if(config.getFishType2()==3){
    // xh = new Double(RedisUtil.val("ALL_XH_100-200"+player.getUser().getId(),0D)).longValue();
    // }else{
    // xh = new Double(RedisUtil.val("ALL_XH_200-max"+player.getUser().getId(),0D)).longValue();
    // }
    // ArrayList<Long> list = new ArrayList<Long>();
    // list.add(new Double(RedisUtil.val("ALL_XH_200-max"+player.getUser().getId(),0D)).longValue());
    // list.add(new Double(RedisUtil.val("ALL_XH_1-50"+player.getUser().getId(),0D)).longValue());
    // list.add(new Double(RedisUtil.val("ALL_XH_50-100"+player.getUser().getId(),0D)).longValue());
    // list.add(new Double(RedisUtil.val("ALL_XH_100-200"+player.getUser().getId(),0D)).longValue());
    // Collections.sort(list);
    // tool = xh>=gr;
    // if(tool){
    // if(fireId!=-1){
    // if(config.getFishType2()!=3){
    // if(personalNum!=0) {//进入个控
    // if(personalNum>0&&personalNum+sj<0){
    // personalNum = 0L;
    // }else if(personalNum<0&&personalNum+sj>0){
    // personalNum = 0L;
    // }else{
    // personalNum = personalNum+sj;
    // }
    // RedisHelper.set("USER_PERSONAL_CONTROL_NUM"+player.getUser().getId(), String.valueOf(personalNum));
    // }else if(FishingUtil.q0[roomIndex*2-1]!=0){//进入场控
    // if(FishingUtil.q0[roomIndex*2-1]>0&&FishingUtil.q0[roomIndex*2-1]+sj<0){
    // FishingUtil.q0[roomIndex*2-1] = 0L;
    // }else if(FishingUtil.q0[roomIndex*2-1]<0&&FishingUtil.q0[roomIndex*2-1]+sj>0){
    // FishingUtil.q0[roomIndex*2-1] = 0L;
    // }else{
    // FishingUtil.q0[roomIndex*2-1] = FishingUtil.q0[roomIndex*2-1]+sj;
    // }
    // List<Long> peakMaxNum1_obj = new ArrayList<Long>();
    // peakMaxNum1_obj = Arrays.asList(FishingUtil.q0);
    // RedisHelper.set("q0", String.valueOf(peakMaxNum1_obj));
    // }else{
    // RedisHelper.set("ALL_CX_USER" + player.getUser().getId(), String.valueOf(cx + sj));
    // }
    // }
    // if(config.getFishType2()==1){
    // RedisHelper.set("ALL_XH_1-50"+player.getUser().getId(),String.valueOf(xh-gr));
    // }else if(config.getFishType2()==2){
    // RedisHelper.set("ALL_XH_50-100"+player.getUser().getId(),String.valueOf(xh-gr));
    // }else if(config.getFishType2()==3){
    // RedisHelper.set("ALL_XH_100-200"+player.getUser().getId(),String.valueOf(xh-gr));
    // }else{
    // RedisHelper.set("ALL_XH_200-max"+player.getUser().getId(),String.valueOf(xh-gr));
    // }
    // if(player instanceof FishingGrGameRobot){
    // RedisHelper.set("USER_T_MAX"+player.getUser().getId(),String.valueOf(FishingUtil.burstOne[4]));
    // RedisHelper.set("USER_T_MIN"+player.getUser().getId(),String.valueOf(FishingUtil.recOne[4]));
    // RedisHelper.set("USER_T_MAX1"+player.getUser().getId(),String.valueOf(FishingUtil.burstOne[4]));
    // RedisHelper.set("USER_T_MIN1"+player.getUser().getId(),String.valueOf(FishingUtil.recOne[4]));
    // RedisHelper.set("USER_T_MAX2"+player.getUser().getId(),String.valueOf(FishingUtil.burstOne[4]));
    // RedisHelper.set("USER_T_MIN2"+player.getUser().getId(),String.valueOf(FishingUtil.recOne[4]));
    // RedisHelper.set("USER_T_MAX3"+player.getUser().getId(),String.valueOf(FishingUtil.burstOne[4]));
    // RedisHelper.set("USER_T_MIN3"+player.getUser().getId(),String.valueOf(FishingUtil.recOne[4]));
    // }else{
    // if(RedisUtil.val("USER_PEAK_MONEY"+player.getUser().getId(),1D)<RedisUtil.val("USER_PEAK_MONEY_HIS"+player.getUser().getId(),1D)){//当前峰值小于上一阶段峰值
    // if(config.getFishType2()==1){
    // RedisHelper.set("USER_T_MAX"+player.getUser().getId(),String.valueOf(FishingUtil.burstOne[2]));
    // RedisHelper.set("USER_T_MIN"+player.getUser().getId(),String.valueOf(FishingUtil.recOne[2]));
    // }else if(config.getFishType2()==2){
    // RedisHelper.set("USER_T_MAX1"+player.getUser().getId(),String.valueOf(FishingUtil.burstOne[5]));
    // RedisHelper.set("USER_T_MIN1"+player.getUser().getId(),String.valueOf(FishingUtil.recOne[5]));
    // }else if(config.getFishType2()==3){
    // RedisHelper.set("USER_T_MAX2"+player.getUser().getId(),String.valueOf(FishingUtil.burstOne[8]));
    // RedisHelper.set("USER_T_MIN2"+player.getUser().getId(),String.valueOf(FishingUtil.recOne[8]));
    // }else{
    // RedisHelper.set("USER_T_MAX3"+player.getUser().getId(),String.valueOf(FishingUtil.burstOne[11]));
    // RedisHelper.set("USER_T_MIN3"+player.getUser().getId(),String.valueOf(FishingUtil.recOne[11]));
    // }
    // if(player.getMoney()+winMoney<RedisUtil.val("USER_PEAK_MONEY"+player.getUser().getId(),0D)){
    // changePeak(player.getUser(),roomIndex*2-1);
    // }
    // }
    // else {
    // if(config.getFishType2()==1){
    // RedisHelper.set("USER_T_MAX"+player.getUser().getId(),String.valueOf(FishingUtil.burstOne[0]));
    // RedisHelper.set("USER_T_MIN"+player.getUser().getId(),String.valueOf(FishingUtil.recOne[0]));
    // }else if(config.getFishType2()==2){
    // RedisHelper.set("USER_T_MAX1"+player.getUser().getId(),String.valueOf(FishingUtil.burstOne[3]));
    // RedisHelper.set("USER_T_MIN1"+player.getUser().getId(),String.valueOf(FishingUtil.recOne[3]));
    // }else if(config.getFishType2()==3){
    // RedisHelper.set("USER_T_MAX2"+player.getUser().getId(),String.valueOf(FishingUtil.burstOne[6]));
    // RedisHelper.set("USER_T_MIN2"+player.getUser().getId(),String.valueOf(FishingUtil.recOne[6]));
    // }else{
    // RedisHelper.set("USER_T_MAX3"+player.getUser().getId(),String.valueOf(FishingUtil.burstOne[9]));
    // RedisHelper.set("USER_T_MIN3"+player.getUser().getId(),String.valueOf(FishingUtil.recOne[9]));
    // }
    // if(player.getMoney()+winMoney>=RedisUtil.val("USER_PEAK_MONEY"+player.getUser().getId(),0D)){
    // changePeak(player.getUser(),roomIndex*2-1);
    // }
    // }
    // }
    // }
    // }
    // else {
    // if(fireId!=-1){
    // long x = RedisUtil.val("USER_T_STATUS"+player.getUser().getId(),0L);
    // if(player instanceof FishingGrGameRobot){
    // RedisHelper.set("USER_T_MAX"+player.getUser().getId(),String.valueOf(FishingUtil.burstOne[4]));
    // RedisHelper.set("USER_T_MIN"+player.getUser().getId(),String.valueOf(FishingUtil.recOne[4]));
    // RedisHelper.set("USER_T_MAX1"+player.getUser().getId(),String.valueOf(FishingUtil.burstOne[4]));
    // RedisHelper.set("USER_T_MIN1"+player.getUser().getId(),String.valueOf(FishingUtil.recOne[4]));
    // RedisHelper.set("USER_T_MAX2"+player.getUser().getId(),String.valueOf(FishingUtil.burstOne[4]));
    // RedisHelper.set("USER_T_MIN2"+player.getUser().getId(),String.valueOf(FishingUtil.recOne[4]));
    // RedisHelper.set("USER_T_MAX3"+player.getUser().getId(),String.valueOf(FishingUtil.burstOne[4]));
    // RedisHelper.set("USER_T_MIN3"+player.getUser().getId(),String.valueOf(FishingUtil.recOne[4]));
    // }else{
    // if(RedisUtil.val("USER_PEAK_MONEY"+player.getUser().getId(),1D)<RedisUtil.val("USER_PEAK_MONEY_HIS"+player.getUser().getId(),1D)){//当前峰值小于上一阶段峰值
    // if(config.getFishType2()==1){
    // RedisHelper.set("USER_T_MAX"+player.getUser().getId(),String.valueOf(FishingUtil.burstOne[2]));
    // RedisHelper.set("USER_T_MIN"+player.getUser().getId(),String.valueOf(FishingUtil.recOne[2]));
    // }else if(config.getFishType2()==2){
    // RedisHelper.set("USER_T_MAX1"+player.getUser().getId(),String.valueOf(FishingUtil.burstOne[5]));
    // RedisHelper.set("USER_T_MIN1"+player.getUser().getId(),String.valueOf(FishingUtil.recOne[5]));
    // }else if(config.getFishType2()==3){
    // RedisHelper.set("USER_T_MAX2"+player.getUser().getId(),String.valueOf(FishingUtil.burstOne[8]));
    // RedisHelper.set("USER_T_MIN2"+player.getUser().getId(),String.valueOf(FishingUtil.recOne[8]));
    // }else{
    // RedisHelper.set("USER_T_MAX3"+player.getUser().getId(),String.valueOf(FishingUtil.burstOne[11]));
    // RedisHelper.set("USER_T_MIN3"+player.getUser().getId(),String.valueOf(FishingUtil.recOne[11]));
    // }
    // if(player.getMoney()<RedisUtil.val("USER_PEAK_MONEY"+player.getUser().getId(),0D)){
    // changePeak(player.getUser(),roomIndex*2-1);
    // }
    // }
    // else {
    // int c = ThreadLocalRandom.current().nextInt(1,4);
    // if(config.getFishType2()==1){
    // RedisHelper.set("USER_T_MAX"+player.getUser().getId(),String.valueOf(FishingUtil.burstOne[0]));
    // RedisHelper.set("USER_T_MIN"+player.getUser().getId(),String.valueOf(FishingUtil.recOne[0]));
    // }else if(config.getFishType2()==2){
    // RedisHelper.set("USER_T_MAX1"+player.getUser().getId(),String.valueOf(FishingUtil.burstOne[3]));
    // RedisHelper.set("USER_T_MIN1"+player.getUser().getId(),String.valueOf(FishingUtil.recOne[3]));
    // }else if(config.getFishType2()==3){
    // RedisHelper.set("USER_T_MAX2"+player.getUser().getId(),String.valueOf(FishingUtil.burstOne[6]));
    // RedisHelper.set("USER_T_MIN2"+player.getUser().getId(),String.valueOf(FishingUtil.recOne[6]));
    // }else{
    // RedisHelper.set("USER_T_MAX3"+player.getUser().getId(),String.valueOf(FishingUtil.burstOne[9]));
    // RedisHelper.set("USER_T_MIN3"+player.getUser().getId(),String.valueOf(FishingUtil.recOne[9]));
    // }
    // if(player.getMoney()>=RedisUtil.val("USER_PEAK_MONEY"+player.getUser().getId(),0D)){
    // changePeak(player.getUser(),roomIndex*2-1);
    // }
    // }
    // }
    // }
    // }
    // }
    // else{//未付费玩家
    // long cx = new Double(RedisUtil.val("ALL_CX_USER"+player.getUser().getId(),0D)).longValue();
    // long bankNumber = RedisUtil.val("USER_T_BANKRUPTCY_NUMBER"+player.getUser().getId(),0L);
    // double banlace = 0L;
    // if(cx>0){
    // banlace = bankNumber;
    // }else{
    // banlace = bankNumber - (cx * FishingUtil.cxPercentage[((roomIndex-1) * 2)]*0.01 );
    // }
    // int tMax = RedisUtil.val("USER_T_MAX_NEW"+player.getUser().getId(),0);
    // int tMin = RedisUtil.val("USER_T_MIN_NEW"+player.getUser().getId(),0);
    // int tMax1 = RedisUtil.val("USER_T_MAX_NEW1"+player.getUser().getId(),0);
    // int tMin1 = RedisUtil.val("USER_T_MIN_NEW1"+player.getUser().getId(),0);
    // int tMax2 = RedisUtil.val("USER_T_MAX_NEW2"+player.getUser().getId(),0);
    // int tMin2 = RedisUtil.val("USER_T_MIN_NEW2"+player.getUser().getId(),0);
    // int tMax3 = RedisUtil.val("USER_T_MAX_NEW3"+player.getUser().getId(),0);
    // int tMin3 = RedisUtil.val("USER_T_MIN_NEW3"+player.getUser().getId(),0);
    // if(FishingUtil.pumpNum[(roomIndex-1)*2]>0){
    // if(FishingUtil.pumpNum[(roomIndex-1)*2] - new
    // Double(FishingUtil.pump[(roomIndex-1)*2]*0.01*needMoney).longValue()<0){
    // player.setCutMoney(player.getCutMoney()+FishingUtil.pumpNum[(roomIndex-1)*2]);
    // FishingUtil.pumpNum[(roomIndex-1)*2] = 0L;
    // needMoney -= FishingUtil.pumpNum[(roomIndex-1)*2];
    // }else{
    // player.setCutMoney(player.getCutMoney()+new
    // Double(FishingUtil.pump[(roomIndex-1)*2]*0.01*needMoney).longValue());
    // FishingUtil.pumpNum[(roomIndex-1)*2] = FishingUtil.pumpNum[(roomIndex-1)*2] - new
    // Double(FishingUtil.pump[(roomIndex-1)*2]*0.01*needMoney).longValue();
    // needMoney -= new Double(FishingUtil.pump[(roomIndex-1)*2]*0.01*needMoney).longValue();
    // }
    // List<Long> peakMaxNum1_obj = new ArrayList<Long>();
    // peakMaxNum1_obj = Arrays.asList(FishingUtil.pumpNum);
    // RedisHelper.set("pumpNum", String.valueOf(peakMaxNum1_obj));
    // }
    // if(config.getFishType2()==1){
    // if (new Double(RedisUtil.val("USER_FISHTYPE_1_T" + player.getUser().getId(), 0D)).intValue() != 0) {
    // t = new Double(RedisUtil.val("USER_FISHTYPE_1_T" + player.getUser().getId(), 0D)).intValue();
    // } else {
    // if(personalNum!=0){//进入个控
    // if(personalNum>0){//进入个控爆发
    // t = ThreadLocalRandom.current().nextInt(burstTmin_obj[0],burstTmax_obj[0]+1);
    // }else{
    // t = ThreadLocalRandom.current().nextInt(recoveryTmin_obj[0],recoveryTmax_obj[0]+1);
    // }
    // }else if(FishingUtil.q0[(roomIndex-1)*2]!=0){//进入场控
    // if(FishingUtil.q0[(roomIndex-1)*2]>0){
    // t = ThreadLocalRandom.current().nextInt(new Double(FishingUtil.apt[(roomIndex-1)*2]).intValue(),new
    // Double(FishingUtil.ap[(roomIndex-1)*2]).intValue()+1);
    // }else{
    // t = ThreadLocalRandom.current().nextInt(new Double(FishingUtil.recMin[(roomIndex-1)*2]).intValue(),new
    // Double(FishingUtil.recMax[(roomIndex-1)*2]).intValue()+1);
    // }
    // }else{
    // int lose = RedisUtil.val("USER_LOSE_ALL"+player.getUser().getId(),0);
    // if(lose==1){
    // t = ThreadLocalRandom.current().nextInt(new Long(FishingUtil.recOne[2]).intValue(),new
    // Long(FishingUtil.burstOne[2]).intValue());
    // }else{
    // t = ThreadLocalRandom.current().nextInt(tMin,tMax+1);
    // }
    // }
    // RedisHelper.set("USER_FISHTYPE_1_T" + player.getUser().getId(),String.valueOf(t));
    // }
    // xh1 = new Double(RedisUtil.val("ALL_XH_1-50"+player.getUser().getId(),0D)).longValue();
    // RedisHelper.set("ALL_XH_1-50"+player.getUser().getId(),String.valueOf(xh1+needMoney));
    // }else if(config.getFishType2()==2){
    // if (new Double(RedisUtil.val("USER_FISHTYPE_2_T" + player.getUser().getId(), 0D)).intValue() != 0) {
    // t = new Double(RedisUtil.val("USER_FISHTYPE_2_T" + player.getUser().getId(), 0D)).intValue();
    // } else {
    // if(personalNum!=0){//进入个控
    // if(personalNum>0){//进入个控爆发
    // t = ThreadLocalRandom.current().nextInt(burstTmin_obj[1],burstTmax_obj[1]+1);
    // }else{
    // t = ThreadLocalRandom.current().nextInt(recoveryTmin_obj[1],recoveryTmax_obj[1]+1);
    // }
    // }else if(FishingUtil.q0[(roomIndex-1)*2]!=0){//进入场控
    // if(FishingUtil.q0[(roomIndex-1)*2]>0){
    // t = ThreadLocalRandom.current().nextInt(new Double(FishingUtil.apt1[(roomIndex-1)*2]).intValue(),new
    // Double(FishingUtil.ap1[(roomIndex-1)*2]).intValue()+1);
    // }else{
    // t = ThreadLocalRandom.current().nextInt(new Double(FishingUtil.recMin1[(roomIndex-1)*2]).intValue(),new
    // Double(FishingUtil.recMax1[(roomIndex-1)*2]).intValue()+1);
    // }
    // }else{
    // int lose = RedisUtil.val("USER_LOSE_ALL"+player.getUser().getId(),0);
    // if(lose==1){
    // t = ThreadLocalRandom.current().nextInt(new Long(FishingUtil.recOne[5]).intValue(),new
    // Long(FishingUtil.burstOne[5]).intValue());
    // }else{
    // t = ThreadLocalRandom.current().nextInt(tMin1,tMax1+1);
    // }
    // }
    // RedisHelper.set("USER_FISHTYPE_2_T" + player.getUser().getId(),String.valueOf(t));
    // }
    // xh1 = new Double(RedisUtil.val("ALL_XH_50-100"+player.getUser().getId(),0D)).longValue();
    // RedisHelper.set("ALL_XH_50-100"+player.getUser().getId(),String.valueOf(xh1+needMoney));
    // }else if(config.getFishType2()==3){
    // if (new Double(RedisUtil.val("USER_FISHTYPE_3_T" + player.getUser().getId(), 0D)).intValue() != 0) {
    // t = new Double(RedisUtil.val("USER_FISHTYPE_3_T" + player.getUser().getId(), 0D)).intValue();
    // } else {
    // if(personalNum!=0){//进入个控
    // if(personalNum>0){//进入个控爆发
    // t = ThreadLocalRandom.current().nextInt(burstTmin_obj[2],burstTmax_obj[2]+1);
    // }else{
    // t = ThreadLocalRandom.current().nextInt(recoveryTmin_obj[2],recoveryTmax_obj[2]+1);
    // }
    // }else if(FishingUtil.q0[(roomIndex-1)*2]!=0){//进入场控
    // if(FishingUtil.q0[(roomIndex-1)*2]>0){
    // t = ThreadLocalRandom.current().nextInt(new Double(FishingUtil.apt2[(roomIndex-1)*2]).intValue(),new
    // Double(FishingUtil.ap2[(roomIndex-1)*2]).intValue()+1);
    // }else{
    // t = ThreadLocalRandom.current().nextInt(new Double(FishingUtil.recMin2[(roomIndex-1)*2]).intValue(),new
    // Double(FishingUtil.recMax2[(roomIndex-1)*2]).intValue()+1);
    // }
    // }else{
    // int lose = RedisUtil.val("USER_LOSE_ALL"+player.getUser().getId(),0);
    // if(lose==1){
    // t = ThreadLocalRandom.current().nextInt(new Long(FishingUtil.recOne[8]).intValue(),new
    // Long(FishingUtil.burstOne[8]).intValue());
    // }else{
    // t = ThreadLocalRandom.current().nextInt(tMin2,tMax2+1);
    // }
    // }
    // RedisHelper.set("USER_FISHTYPE_3_T" + player.getUser().getId(),String.valueOf(t));
    // }
    // xh1 = new Double(RedisUtil.val("ALL_XH_100-200"+player.getUser().getId(),0D)).longValue();
    // RedisHelper.set("ALL_XH_100-200"+player.getUser().getId(),String.valueOf(xh1+needMoney));
    // }else{
    // if (new Double(RedisUtil.val("USER_FISHTYPE_3_T" + player.getUser().getId(), 0D)).intValue() != 0) {
    // t = new Double(RedisUtil.val("USER_FISHTYPE_3_T" + player.getUser().getId(), 0D)).intValue();
    // } else {
    // if(personalNum!=0){//进入个控
    // if(personalNum>0){//进入个控爆发
    // t = ThreadLocalRandom.current().nextInt(burstTmin_obj[3],burstTmax_obj[3]+1);
    // }else{
    // t = ThreadLocalRandom.current().nextInt(recoveryTmin_obj[3],recoveryTmax_obj[3]+1);
    // }
    // }else if(FishingUtil.q0[(roomIndex-1)*2]!=0){//进入场控
    // if(FishingUtil.q0[(roomIndex-1)*2]>0){
    // t = ThreadLocalRandom.current().nextInt(new Double(FishingUtil.apt3[(roomIndex-1)*2]).intValue(),new
    // Double(FishingUtil.ap3[(roomIndex-1)*2]).intValue()+1);
    // }else{
    // t = ThreadLocalRandom.current().nextInt(new Double(FishingUtil.recMin3[(roomIndex-1)*2]).intValue(),new
    // Double(FishingUtil.recMax3[(roomIndex-1)*2]).intValue()+1);
    // }
    // }else{
    // int lose = RedisUtil.val("USER_LOSE_ALL"+player.getUser().getId(),0);
    // if(lose==1){
    // t = ThreadLocalRandom.current().nextInt(new Long(FishingUtil.recOne[11]).intValue(),new
    // Long(FishingUtil.burstOne[11]).intValue());
    // }else{
    // t = ThreadLocalRandom.current().nextInt(tMin3,tMax3+1);
    // }
    // }
    // RedisHelper.set("USER_FISHTYPE_4_T" + player.getUser().getId(),String.valueOf(t));
    // }
    // xh1 = new Double(RedisUtil.val("ALL_XH_200-max"+player.getUser().getId(),0D)).longValue();
    // RedisHelper.set("ALL_XH_200-max"+player.getUser().getId(),String.valueOf(xh1+needMoney));
    // }
    // long xl = winMoney;
    // long sj = new Double(xl*t*0.01).longValue();
    // long gr = xl+sj;
    // long xh =0L;
    // if(config.getFishType2()==1){
    // xh = new Double(RedisUtil.val("ALL_XH_1-50"+player.getUser().getId(),0D)).longValue();
    // }else if(config.getFishType2()==2){
    // xh = new Double(RedisUtil.val("ALL_XH_50-100"+player.getUser().getId(),0D)).longValue();
    // }else if(config.getFishType2()==3){
    // xh = new Double(RedisUtil.val("ALL_XH_100-200"+player.getUser().getId(),0D)).longValue();
    // }else{
    // xh = new Double(RedisUtil.val("ALL_XH_200-max"+player.getUser().getId(),0D)).longValue();
    // }
    // ArrayList<Long> list = new ArrayList<Long>();
    // list.add(new Double(RedisUtil.val("ALL_XH_200-max"+player.getUser().getId(),0D)).longValue());
    // list.add(new Double(RedisUtil.val("ALL_XH_1-50"+player.getUser().getId(),0D)).longValue());
    // list.add(new Double(RedisUtil.val("ALL_XH_50-100"+player.getUser().getId(),0D)).longValue());
    // list.add(new Double(RedisUtil.val("ALL_XH_100-200"+player.getUser().getId(),0D)).longValue());
    // Collections.sort(list);
    // tool = xh>=gr;
    // if(tool){
    // if(fireId!=-1){
    // if(config.getFishType2()!=3){
    // if(personalNum!=0) {//进入个控
    // if(personalNum>0&&personalNum+sj<0){
    // personalNum = 0L;
    // }else if(personalNum<0&&personalNum+sj>0){
    // personalNum = 0L;
    // }else{
    // personalNum = personalNum+sj;
    // }
    // RedisHelper.set("USER_PERSONAL_CONTROL_NUM"+player.getUser().getId(), String.valueOf(personalNum));
    // }else if(FishingUtil.q0[(roomIndex-1)*2]!=0){//进入场控
    // if(FishingUtil.q0[(roomIndex-1)*2]>0&&FishingUtil.q0[(roomIndex-1)*2]+sj<0){
    // FishingUtil.q0[(roomIndex-1)*2] = 0L;
    // }else if(FishingUtil.q0[(roomIndex-1)*2]<0&&FishingUtil.q0[(roomIndex-1)*2]+sj>0){
    // FishingUtil.q0[(roomIndex-1)*2] = 0L;
    // }else{
    // FishingUtil.q0[(roomIndex-1)*2] = FishingUtil.q0[(roomIndex-1)*2]+sj;
    // }
    // List<Long> peakMaxNum1_obj = new ArrayList<Long>();
    // peakMaxNum1_obj = Arrays.asList(FishingUtil.q0);
    // RedisHelper.set("pumpNum", String.valueOf(peakMaxNum1_obj));
    // }else{
    // RedisHelper.set("ALL_CX_USER" + player.getUser().getId(), String.valueOf(cx + sj));
    // }
    // }
    //// long x = RedisUtil.val("USER_T_STATUS_NEW"+player.getUser().getId(),0L);
    // if(player instanceof FishingGrGameRobot){
    // if(config.getFishType2()==1){
    // RedisHelper.set("ALL_XH_1-50"+player.getUser().getId(),String.valueOf(0));
    // }else if(config.getFishType2()==2){
    // RedisHelper.set("ALL_XH_50-100"+player.getUser().getId(),String.valueOf(0));
    // }else if(config.getFishType2()==3){
    // RedisHelper.set("ALL_XH_100-200"+player.getUser().getId(),String.valueOf(0));
    // }else{
    // RedisHelper.set("ALL_XH_200-max"+player.getUser().getId(),String.valueOf(0));
    // }
    // RedisHelper.set("USER_T_MAX_NEW"+player.getUser().getId(),String.valueOf(FishingUtil.burstOne[4]));
    // RedisHelper.set("USER_T_MIN_NEW"+player.getUser().getId(),String.valueOf(FishingUtil.recOne[4]));
    // RedisHelper.set("USER_T_MAX_NEW1"+player.getUser().getId(),String.valueOf(FishingUtil.burstOne[4]));
    // RedisHelper.set("USER_T_MIN_NEW1"+player.getUser().getId(),String.valueOf(FishingUtil.recOne[4]));
    // RedisHelper.set("USER_T_MAX_NEW2"+player.getUser().getId(),String.valueOf(FishingUtil.burstOne[4]));
    // RedisHelper.set("USER_T_MIN_NEW2"+player.getUser().getId(),String.valueOf(FishingUtil.recOne[4]));
    // RedisHelper.set("USER_T_MAX_NEW3"+player.getUser().getId(),String.valueOf(FishingUtil.burstOne[4]));
    // RedisHelper.set("USER_T_MIN_NEW3"+player.getUser().getId(),String.valueOf(FishingUtil.recOne[4]));
    // }else{
    // if(config.getFishType2()==1){
    // RedisHelper.set("ALL_XH_1-50"+player.getUser().getId(),String.valueOf(xh-gr));
    // }else if(config.getFishType2()==2){
    // RedisHelper.set("ALL_XH_50-100"+player.getUser().getId(),String.valueOf(xh-gr));
    // }else if(config.getFishType2()==3){
    // RedisHelper.set("ALL_XH_100-200"+player.getUser().getId(),String.valueOf(xh-gr));
    // }else{
    // RedisHelper.set("ALL_XH_200-max"+player.getUser().getId(),String.valueOf(xh-gr));
    // }
    // if(RedisUtil.val("USER_PEAK_MONEY"+player.getUser().getId(),1D)<RedisUtil.val("USER_PEAK_MONEY_HIS"+player.getUser().getId(),1D)){//当前峰值小于上一阶段峰值
    // if(config.getFishType2()==1){
    // RedisHelper.set("USER_T_MAX_NEW"+player.getUser().getId(),String.valueOf(FishingUtil.burstOne[2]));
    // RedisHelper.set("USER_T_MIN_NEW"+player.getUser().getId(),String.valueOf(FishingUtil.recOne[2]));
    // }else if(config.getFishType2()==2){
    // RedisHelper.set("USER_T_MAX_NEW1"+player.getUser().getId(),String.valueOf(FishingUtil.burstOne[5]));
    // RedisHelper.set("USER_T_MIN_NEW1"+player.getUser().getId(),String.valueOf(FishingUtil.recOne[5]));
    // }else if(config.getFishType2()==3){
    // RedisHelper.set("USER_T_MAX_NEW2"+player.getUser().getId(),String.valueOf(FishingUtil.burstOne[8]));
    // RedisHelper.set("USER_T_MIN_NEW2"+player.getUser().getId(),String.valueOf(FishingUtil.recOne[8]));
    // }else{
    // RedisHelper.set("USER_T_MAX_NEW3"+player.getUser().getId(),String.valueOf(FishingUtil.burstOne[11]));
    // RedisHelper.set("USER_T_MIN_NEW3"+player.getUser().getId(),String.valueOf(FishingUtil.recOne[11]));
    // }
    // if(player.getMoney()+winMoney<RedisUtil.val("USER_PEAK_MONEY"+player.getUser().getId(),0D)){
    // changePeak(player.getUser(),(roomIndex-1)*2);
    // }
    // }
    // else {
    // if(config.getFishType2()==1){
    // RedisHelper.set("USER_T_MAX_NEW"+player.getUser().getId(),String.valueOf(FishingUtil.burstOne[0]));
    // RedisHelper.set("USER_T_MIN_NEW"+player.getUser().getId(),String.valueOf(FishingUtil.recOne[0]));
    // }else if(config.getFishType2()==2){
    // RedisHelper.set("USER_T_MAX_NEW1"+player.getUser().getId(),String.valueOf(FishingUtil.burstOne[3]));
    // RedisHelper.set("USER_T_MIN_NEW1"+player.getUser().getId(),String.valueOf(FishingUtil.recOne[3]));
    // }else if(config.getFishType2()==3){
    // RedisHelper.set("USER_T_MAX_NEW2"+player.getUser().getId(),String.valueOf(FishingUtil.burstOne[6]));
    // RedisHelper.set("USER_T_MIN_NEW2"+player.getUser().getId(),String.valueOf(FishingUtil.recOne[6]));
    // }else{
    // RedisHelper.set("USER_T_MAX_NEW3"+player.getUser().getId(),String.valueOf(FishingUtil.burstOne[9]));
    // RedisHelper.set("USER_T_MIN_NEW3"+player.getUser().getId(),String.valueOf(FishingUtil.recOne[9]));
    // }
    // if(player.getMoney()+winMoney>=RedisUtil.val("USER_PEAK_MONEY"+player.getUser().getId(),0D)){
    // changePeak(player.getUser(),(roomIndex-1)*2);
    // }
    // }
    // }
    // }
    // }
    // else {
    // if(fireId!=-1){
    //// long x = RedisUtil.val("USER_T_STATUS_NEW"+player.getUser().getId(),0L);
    // if(player instanceof FishingGrGameRobot){
    // RedisHelper.set("USER_T_MAX_NEW"+player.getUser().getId(),String.valueOf(FishingUtil.burstOne[4]));
    // RedisHelper.set("USER_T_MIN_NEW"+player.getUser().getId(),String.valueOf(FishingUtil.recOne[4]));
    // RedisHelper.set("USER_T_MAX_NEW1"+player.getUser().getId(),String.valueOf(FishingUtil.burstOne[4]));
    // RedisHelper.set("USER_T_MIN_NEW1"+player.getUser().getId(),String.valueOf(FishingUtil.recOne[4]));
    // RedisHelper.set("USER_T_MAX_NEW2"+player.getUser().getId(),String.valueOf(FishingUtil.burstOne[4]));
    // RedisHelper.set("USER_T_MIN_NEW2"+player.getUser().getId(),String.valueOf(FishingUtil.recOne[4]));
    // RedisHelper.set("USER_T_MAX_NEW3"+player.getUser().getId(),String.valueOf(FishingUtil.burstOne[4]));
    // RedisHelper.set("USER_T_MIN_NEW3"+player.getUser().getId(),String.valueOf(FishingUtil.recOne[4]));
    // }else{
    // if(RedisUtil.val("USER_PEAK_MONEY"+player.getUser().getId(),1D)<RedisUtil.val("USER_PEAK_MONEY_HIS"+player.getUser().getId(),1D)){//随机峰值小于携带金币数
    // if(config.getFishType2()==1){
    // RedisHelper.set("USER_T_MAX_NEW"+player.getUser().getId(),String.valueOf(FishingUtil.burstOne[2]));
    // RedisHelper.set("USER_T_MIN_NEW"+player.getUser().getId(),String.valueOf(FishingUtil.recOne[2]));
    // }else if(config.getFishType2()==2){
    // RedisHelper.set("USER_T_MAX_NEW1"+player.getUser().getId(),String.valueOf(FishingUtil.burstOne[5]));
    // RedisHelper.set("USER_T_MIN_NEW1"+player.getUser().getId(),String.valueOf(FishingUtil.recOne[5]));
    // }else if(config.getFishType2()==3){
    // RedisHelper.set("USER_T_MAX_NEW2"+player.getUser().getId(),String.valueOf(FishingUtil.burstOne[8]));
    // RedisHelper.set("USER_T_MIN_NEW2"+player.getUser().getId(),String.valueOf(FishingUtil.recOne[8]));
    // }else{
    // RedisHelper.set("USER_T_MAX_NEW3"+player.getUser().getId(),String.valueOf(FishingUtil.burstOne[11]));
    // RedisHelper.set("USER_T_MIN_NEW3"+player.getUser().getId(),String.valueOf(FishingUtil.recOne[11]));
    // }
    // if(player.getMoney()<RedisUtil.val("USER_PEAK_MONEY"+player.getUser().getId(),0D)){
    // changePeak(player.getUser(),(roomIndex-1)*2);
    // }
    // }
    // else {
    // if(config.getFishType2()==1){
    // RedisHelper.set("USER_T_MAX_NEW"+player.getUser().getId(),String.valueOf(FishingUtil.burstOne[0]));
    // RedisHelper.set("USER_T_MIN_NEW"+player.getUser().getId(),String.valueOf(FishingUtil.recOne[0]));
    // }else if(config.getFishType2()==2){
    // RedisHelper.set("USER_T_MAX_NEW1"+player.getUser().getId(),String.valueOf(FishingUtil.burstOne[3]));
    // RedisHelper.set("USER_T_MIN_NEW1"+player.getUser().getId(),String.valueOf(FishingUtil.recOne[3]));
    // }else if(config.getFishType2()==3){
    // RedisHelper.set("USER_T_MAX_NEW2"+player.getUser().getId(),String.valueOf(FishingUtil.burstOne[6]));
    // RedisHelper.set("USER_T_MIN_NEW2"+player.getUser().getId(),String.valueOf(FishingUtil.recOne[6]));
    // }else{
    // RedisHelper.set("USER_T_MAX_NEW3"+player.getUser().getId(),String.valueOf(FishingUtil.burstOne[9]));
    // RedisHelper.set("USER_T_MIN_NEW3"+player.getUser().getId(),String.valueOf(FishingUtil.recOne[9]));
    // }
    // if(player.getMoney()>=RedisUtil.val("USER_PEAK_MONEY"+player.getUser().getId(),0D)){
    // changePeak(player.getUser(),(roomIndex-1)*2);
    // }
    // }
    // }
    // }
    // }
    // }
    // return tool;
    // }

    public static void exitRoom(FishingGrandPrixPlayer player, NewBaseFishingRoom room) {

        if (player == null || player.getId() == 0) {
            return;
        }

        // 退出房间后的操作，比如日志记录等
        ServerUser user = player.getUser();

        OseePlayerEntity entity = PlayerManager.getPlayerEntity(user);

        if (entity != null) {

            synchronized (entity) { // 锁：用户

                if (player.getChangeMoney() != 0) {

                    // 保存到账户变动记录
                    OseePlayerTenureLogEntity log = new OseePlayerTenureLogEntity();
                    log.setUserId(user.getId());
                    log.setNickname(user.getNickname());
                    log.setReason(ItemChangeReason.FISHING_RESULT.getId());
                    log.setPreBankMoney(entity.getBankMoney());
                    log.setPreMoney(player.getEnterMoney());
                    log.setPreLottery(entity.getLottery());
                    log.setChangeMoney(player.getChangeMoney() - player.getTorpedoMoney());
                    tenureLogMapper.save(log);

                    // 保存到捕鱼记录
                    OseeFishingRecordLogEntity recordLogEntity = new OseeFishingRecordLogEntity();
                    recordLogEntity.setPlayerId(user.getId());
                    recordLogEntity.setRoomIndex(room.getRoomIndex());
                    recordLogEntity.setSpendMoney(player.getSpendMoney());
                    recordLogEntity.setWinMoney(player.getWinMoney() - player.getTorpedoMoney());
                    recordLogEntity.setDropBronzeTorpedoNum(player.getDropBronzeTorpedoNum());
                    recordLogEntity.setDropSilverTorpedoNum(player.getDropSilverTorpedoNum());
                    recordLogEntity.setDropGoldTorpedoNum(player.getDropGoldTorpedoNum());
                    recordLogEntity.setDropGoldTorpedoBangNum(player.getDropGoldTorpedoBangNum());
                    recordLogEntity.setDropRareTorpedoBangNum(player.getDropRareTorpedoNum());
                    recordLogEntity.setDropRareTorpedoBangNum(player.getDropRareTorpedoBangNum());
                    fishingRecordLogMapper.save(recordLogEntity);

                }

                // 保存本局抽水记录
                fishingUtil.saveCutProb(player, GameEnum.FISHING_GRANDPRIX.getId());

                if (new Double(FishingHitDataManager.getPlayerFishingProb(user.getId())).longValue() != 0) {

                    FishingHitDataManager.setPlayerFishingProb(user.getId(), 0.00);

                }

                // 把玩家从房间移除 VIP房间如果没人就删除
                FishingGrandPrixQuitResponse.Builder builder = FishingGrandPrixQuitResponse.newBuilder();
                builder.setPlayerId(player.getId());
                MyRefreshFishingUtil.sendRoomMessage(room,
                        OseeMessage.OseeMsgCode.S_C_TTMY_FINSHING_GRAND_PRIX_QUIT_RESPONSE_VALUE, builder);

                GameContainer.removeGamePlayer(room, player.getId(), room.isVip());

            }

        }

    }

    /**
     * 使用技能
     */
    public static void useSkill(NewBaseFishingRoom gameRoom, FishingGrandPrixPlayer player, int skillId, long routeId) {

        FishingGrandPrixUseSkillResponse.Builder builder = FishingGrandPrixUseSkillResponse.newBuilder();

        FishingHelper.useSkill(gameRoom, player, skillId, routeId, null, new FishingHelper.UseSkillBuilderHelper() {

            @Override
            public void setId(long userId, int skillId) {

                builder.setSkillId(skillId);
                builder.setPlayerId(player.getId());

            }

            @Override
            public void setDuration(int duration) {

                builder.setDuration(duration);

            }

            @Override
            public void addFishIds(long fishId) {

                builder.addFishIds(fishId);

            }

            @Override
            public void addRemainDurations(int remainDuration) {

                builder.addRemainDurations(remainDuration);

            }

            @Override
            public void setSkillFishId(long routeId) {

                builder.setSkillFishId(routeId);

            }

            @Override
            public void setRestMoney(long restMoney) {

                builder.setRestMoney(restMoney);

            }

            @Override
            public void send(NewBaseFishingRoom room) {

                MyRefreshFishingUtil.sendRoomMessage(room,
                        OseeMessage.OseeMsgCode.S_C_TTMY_FISHING_GRAND_PRIX_USE_SKILL_RESPONSE_VALUE, builder);

            }

            @Override
            public void setNum1(int num1) {

            }

        });

        // boolean closeFlag = skillId < 0; // 如果小于 0，则表示关闭
        //
        // if (closeFlag) {
        // skillId = -skillId; // 转化为正数
        // }
        //
        // player.setLastFireTime(System.currentTimeMillis());
        //
        // ServerUser user = player.getUser();
        // // 技能id有误
        // if (!skillIds.contains(skillId)) {
        // NetManager.sendHintBoxMessageToClient("禁止使用该道具", user, 10);
        // return;
        // }
        //
        // FishingGrandPrixUseSkillResponse.Builder builder = FishingGrandPrixUseSkillResponse.newBuilder();
        // builder.setSkillId(skillId);
        // builder.setPlayerId(player.getId());
        //
        // long nowTime = System.currentTimeMillis();
        // if (skillId == ItemId.SKILL_AUTO_FIRE.getId()) { // 自动开炮
        //
        // if (closeFlag) {
        // builder.setDuration(0);
        // player.setLastAutoFireTime(0);
        // } else {
        // builder.setDuration(Integer.MAX_VALUE);
        // player.setLastAutoFireTime(nowTime + Integer.MAX_VALUE);
        // }
        //
        // } else if (skillId == ItemId.SKILL_LOCK.getId()) { // 锁定
        // // if (nowTime - player.getLastLockTime() < SKILL_LOCK_TIME) {
        // // NetManager.sendHintBoxMessageToClient("技能冷却中", user, 10);
        // // return;
        // // }
        // // builder.setDuration((int) (SKILL_LOCK_TIME / 1000));
        // // player.setLastLockTime(nowTime);
        // if (closeFlag) {
        // builder.setDuration(0);
        // player.setLastLockTime(0);
        // } else {
        // builder.setDuration(Integer.MAX_VALUE);
        // player.setLastLockTime(nowTime + Integer.MAX_VALUE);
        // }
        //
        // } else if (skillId == ItemId.SKILL_FROZEN.getId()) { // 冰冻
        // // 技能数量不足
        // if (!PlayerManager.checkItem(user, skillId, 1)) {
        // NetManager.sendHintBoxMessageToClient("道具数量不足", user, 10);
        // return;
        // }
        // // int vipLevel = PlayerManager.getPlayerVipLevel(user);
        // // if (vipLevel < 4) {
        // // NetManager.sendHintMessageToClient("VIP4才可以使用该技能", user);
        // // return;
        // // }
        // if (nowTime - player.getLastFrozenTime() < 2000) { // 新冰冻：技能冷却时间 2秒
        // NetManager.sendHintBoxMessageToClient("技能冷却中", user, 10);
        // return;
        // }
        //
        // builder.setDuration((int)(SKILL_FROZEN_TIME / 1000));
        // player.setLastFrozenTime(nowTime);
        // gameRoom.setLastRoomFrozenTime(nowTime);
        //
        // List<Long> ruleIdList = gameRoom.getFishMap().values().stream().map(FishStruct::getRuleId).distinct()
        // .collect(Collectors.toList());
        //
        // List<Long> frozenRuleIdList; // 新冰冻：本次需要被冻住的规则
        // long origin = 6;
        // long bound = 12;
        // if (ruleIdList.size() > origin) {
        // long nextLong = ThreadLocalRandom.current().nextLong(origin + 1, bound + 1);
        // Collections.shuffle(ruleIdList); // 打乱顺序
        // frozenRuleIdList = ruleIdList.stream().limit(nextLong).collect(Collectors.toList());
        // } else {
        // frozenRuleIdList = ruleIdList;
        // }
        //
        // List<Long> frozenFishIdList = new LinkedList<>(); // 新冰冻
        //
        // for (FishStruct fish : gameRoom.getFishMap().values()) {
        // if (frozenRuleIdList.contains(fish.getRuleId())) {
        // long frozenAddTime = getFrozenAddTime(fish.getLastFishFrozenTime(), nowTime);
        // fish.setLifeTime(fish.getLifeTime() + frozenAddTime); // 延长鱼的存在时间
        // fish.setNowLifeTime(fish.getClientLifeTime()); // 记录冰冻时鱼的存活时间
        // fish.setFTime(fish.getFTime() + (frozenAddTime) * 1000);
        // fish.setLastFishFrozenTime(nowTime); // 新冰冻
        // frozenFishIdList.add(fish.getId());
        // }
        // }
        //
        // // 新冰冻
        // for (Long item : frozenFishIdList) {
        // builder.addFishIds(item);
        // builder.addRemainDurations((int)(FishingManager.SKILL_FROZEN_TIME));
        // }
        //
        // long frozenAddTime = getFrozenAddTime(gameRoom.getLastRoomFrozenTime(), nowTime);
        //
        // // 延迟鱼潮刷新时间 秒
        // gameRoom.setNextFishTideTime(gameRoom.getNextFishTideTime() + frozenAddTime);
        //
        // // 扣除使用的技能数量
        // PlayerManager.addItem(user, skillId, -1, ItemChangeReason.USE_ITEM, true);
        // } else if (skillId == ItemId.SKILL_ELETIC.getId()) { // 电磁炮
        // // int vipLevel = PlayerManager.getPlayerVipLevel(user);
        // // if (nowTime - player.getLastElectromagneticTime() < FishingManager.SKILL_ELETIC_TIME) {
        // // NetManager.sendHintBoxMessageToClient("技能冷却中", user,10);
        // // return;
        // // }
        // // builder.setDuration((int) (FishingManager.SKILL_ELETIC_TIME / 1000));
        // // player.setLastElectromagneticTime(nowTime);
        // if (closeFlag) {
        // builder.setDuration(0);
        // player.setLastElectromagneticTime(0);
        // } else {
        // builder.setDuration(Integer.MAX_VALUE);
        // player.setLastElectromagneticTime(nowTime + Integer.MAX_VALUE);
        // }
        //
        // } else if (skillId == ItemId.SKILL_BLACK_HOLE.getId()) { // 黑洞炮
        // int vipLevel = PlayerManager.getPlayerVipLevel(user);
        // if (vipLevel < 4) {
        // NetManager.sendHintMessageToClient("VIP4才可以使用该技能", user);
        // return;
        // }
        // if (nowTime - player.getLastBlackHoleTime() < SKILL_BLACK_HOLE_TIME) {
        // NetManager.sendHintBoxMessageToClient("技能冷却中", user, 10);
        // return;
        // }
        // builder.setDuration((int)(SKILL_BLACK_HOLE_TIME / 1000));
        // player.setLastBlackHoleTime(nowTime);
        // } else if (skillId == ItemId.SKILL_TORPEDO.getId()) { // 鱼雷炮
        // int vipLevel = PlayerManager.getPlayerVipLevel(user);
        // if (vipLevel < 4) {
        // NetManager.sendHintMessageToClient("VIP4才可以使用该技能", user);
        // return;
        // }
        // if (nowTime - player.getLastTorpedoTime() < SKILL_TORPEDO_TIME) {
        // NetManager.sendHintBoxMessageToClient("技能冷却中", user, 10);
        // return;
        // }
        // builder.setDuration((int)(SKILL_TORPEDO_TIME / 1000));
        // player.setLastTorpedoTime(nowTime);
        // } else if (skillId == ItemId.SKILL_BIT.getId()) { // 钻头
        // int vipLevel = PlayerManager.getPlayerVipLevel(user);
        // if (vipLevel < 3) {
        // NetManager.sendHintMessageToClient("VIP可以使用钻头技能", user);
        // return;
        // }
        // if (nowTime - player.getLastBitTime() < SKILL_BIT_TIME) {
        // NetManager.sendHintBoxMessageToClient("技能冷却中", user, 10);
        // return;
        // }
        // builder.setDuration((int)(SKILL_BIT_TIME / 1000));
        // player.setLastBitTime(nowTime);
        // } else if (skillId == ItemId.SKILL_FAST.getId()) { // 急速
        // // int vipLevel = PlayerManager.getPlayerVipLevel(user);
        // // if (vipLevel < 4) {
        // // NetManager.sendHintMessageToClient("VIP4才可以使用该技能", user);
        // // return;
        // // }
        // // builder.setDuration((int) (SKILL_FAST_TIME / 1000));
        // // player.setLastFastTime(nowTime);
        // if (nowTime - player.getLastFastTime() < FishingManager.SKILL_FAST_TIME) {
        // NetManager.sendHintBoxMessageToClient("技能冷却中", user, 10);
        // return;
        // }
        // int vipLevel = PlayerManager.getPlayerVipLevel(user);
        // if (vipLevel < 4) {
        // NetManager.sendHintMessageToClient("VIP4才可以使用该技能", user);
        // return;
        // }
        // builder.setDuration((int)(FishingManager.SKILL_FAST_TIME / 1000));
        // player.setLastFastTime(nowTime);
        // } else if (skillId == ItemId.SKILL_CRIT.getId()) { // 暴击
        // // int vipLevel = PlayerManager.getPlayerVipLevel(user);
        // // if (vipLevel < 8) {
        // // NetManager.sendHintMessageToClient("VIP8才可以使用该技能", user);
        // // return;
        // // }
        // // if (nowTime - player.getLastCritTime() < SKILL_CRIT_TIME) {
        // // NetManager.sendHintBoxMessageToClient("技能冷却中", user, 10);
        // // return;
        // // }
        // // builder.setDuration((int) (SKILL_CRIT_TIME / 1000));
        // // player.setLastCritTime(nowTime);
        // // RedisHelper.set("USER_CRIT_MULT" + user.getId(), String.valueOf(2));
        //
        // if (closeFlag) {
        // builder.setDuration(0);
        // player.setLastCritTime(0);
        // RedisHelper.set("USER_CRIT_MULT" + user.getId(), String.valueOf(1));
        // } else {
        // builder.setDuration(Integer.MAX_VALUE);
        // player.setLastCritTime(nowTime + Integer.MAX_VALUE);
        // RedisHelper.set("USER_CRIT_MULT" + user.getId(), String.valueOf(2));
        // }
        //
        // } else if (skillId == ItemId.FEN_SHEN.getId()) { // 分身炮道具
        // int vipLevel = PlayerManager.getPlayerVipLevel(user);
        // if (vipLevel < 8) {
        // NetManager.sendHintMessageToClient("VIP8才可以使用该技能", user);
        // return;
        // }
        // // if (nowTime - player.getLastFenShenTime() < SKILL_FEN_SHEN_TIME) {
        // // NetManager.sendHintBoxMessageToClient("技能冷却中", user,10);
        // // return;
        // // }
        // builder.setDuration((int)(SKILL_FEN_SHEN_TIME / 1000));
        // player.setLastFenShenTime(nowTime);
        //
        // }
        //
        // builder.setRestMoney(player.getMoney());
        // MyRefreshFishingUtil
        // .sendRoomMessage(gameRoom, OseeMessage.OseeMsgCode.S_C_TTMY_FISHING_GRAND_PRIX_USE_SKILL_RESPONSE_VALUE,
        // builder);

    }

    /**
     * 新冰冻：获取需要增加的：冰冻时间
     */
    private long getFrozenAddTime(long lastFrozenTime, long nowTime) {
        return Math.min((nowTime - lastFrozenTime) / 1000, FishingManager.SKILL_FROZEN_TIME / 1000);
    }

    /**
     * 发送同步锁定
     */
    public void sendSyncLockResponse(FishingGrandPrixSyncLockResponse.Builder response, FishingGrandPrixRoom gameRoom) {
        if (gameRoom != null) {
            MyRefreshFishingUtil.sendRoomMessage(gameRoom,
                    OseeMessage.OseeMsgCode.S_C_FISHING_GRANDPRIX_SYNC_LOCK_RESPONSE_VALUE, response);
        }
    }

    /**
     * 房间循环任务,刷鱼等
     */
    private void doFishingRoomTask(FishingGrandPrixRoom gameRoom) {
        long nowTime = System.currentTimeMillis();

        // 获取：房间内的刷鱼规则
        MyRefreshFishingHelper.getRoomRefreshRule(gameRoom);
        // 检查并执行刷鱼
        MyRefreshFishingHelper.checkAndRefresh(gameRoom);

        // 判断玩家操作时间
        for (int i = 0; i < gameRoom.getMaxSize(); i++) {
            FishingGrandPrixPlayer player = gameRoom.getGamePlayerBySeat(i);
            if (player == null) {
                continue;
            }
            // 检查玩家是否长时间未操作
//            if (ObjectUtils.isEmpty(player.lastFireTime.get(player.getId()))) { // 非空处理
//                player.lastFireTime.put(player.getId(), System.currentTimeMillis());
//            }
            if (nowTime - player.getLastFireTime() > ROOM_KICK_TIME) {
                NetManager.sendHintBoxMessageToClient("您长时间未操作，已被移出捕鱼房间", player.getUser(), 10);
                exitRoom(player, gameRoom);
            }
        }

        // 判断过期鱼，并从鱼表内移除
        List<Long> removeKey = new LinkedList<>();
        for (FishStruct fish : gameRoom.getFishMap().values()) {

            long maxLifeTime =
                    Math.round(fish.getLifeTime() > 0 ? fish.getLifeTime() : FishingManager.DEFAULT_LIFE_TIME);

            // 这里不用加冰冻时间，因为：fish.getLifeTime()，里面加了冰冻时间的
            if (maxLifeTime + fish.getCreateTime() < nowTime) {
                removeKey.add(fish.getId());

                MyRefreshFishingHelper.checkAndDurationRefreshFish(gameRoom, fish, false);
            }
        }
        for (long key : removeKey) {
            gameRoom.getFishMap().remove(key);
//            gameRoom.removeFishMap(key);
        }
    }

    /**
     * 更换炮台等级
     */
    public void changeBatteryLevel(FishingGrandPrixRoom gameRoom, FishingGrandPrixPlayer player, int targetLevel) {
        // 判断炮台等级数值是否有误
        if (targetLevel < 100 || targetLevel > 100000) {
            NetManager.sendErrorMessageToClient("更换的炮台等级有误", player.getUser());
            return;
        }

        // 改变玩家当前炮台等级
        player.setBatteryLevel(targetLevel);
        int usedBatteryLevel = RedisUtil.val("USER_BATTERY_LEVEL_USED" + player.getUser().getId(), 0);
        if (usedBatteryLevel > targetLevel) {
            long xh1 = new Double(RedisUtil.val("ALL_XH_1-50" + player.getUser().getId(), 0D)).longValue();
            long xh2 = new Double(RedisUtil.val("ALL_XH_50-100" + player.getUser().getId(), 0D)).longValue();
            long xh3 = new Double(RedisUtil.val("ALL_XH_100-200" + player.getUser().getId(), 0D)).longValue();
            long xh4 = new Double(RedisUtil.val("ALL_XH_200-max" + player.getUser().getId(), 0D)).longValue();
            long cx = new Double(RedisUtil.val("ALL_CX_USER" + player.getUser().getId(), 0D)).longValue();
            RedisHelper.set("ALL_CX_USER" + player.getUser().getId(), String.valueOf(cx - xh1 - xh2 - xh3 - xh4));
            RedisHelper.set("ALL_XH_1-50" + player.getUser().getId(), "0");
            RedisHelper.set("ALL_XH_50-100" + player.getUser().getId(), "0");
            RedisHelper.set("ALL_XH_100-200" + player.getUser().getId(), "0");
            RedisHelper.set("ALL_XH_200-max" + player.getUser().getId(), "0");
        }
        RedisHelper.set("USER_BATTERY_LEVEL_USED" + player.getUser().getId(), String.valueOf(targetLevel));
        FishingGrandPrixChangeBatteryLevelResponse.Builder builder =
                FishingGrandPrixChangeBatteryLevelResponse.newBuilder();
        builder.setPlayerId(player.getId());
        builder.setLevel((int) player.getBatteryLevel());
        MyRefreshFishingUtil.sendRoomMessage(gameRoom,
                OseeMessage.OseeMsgCode.S_C_TTMY_FISHING_GRAND_PRIX_CHANGE_BATTERY_LEVEL_RESPONSE_VALUE, builder);
    }

    @Autowired
    UserPropsManager userPropsManager;

    private static UserStatusManager userStatusManager;

    @Resource
    public void setUserStatusManager(UserStatusManager userStatusManager) {
        FishingGrandPrixManager.userStatusManager = userStatusManager;
    }

    public void changeBatteryView(ServerUser user, int viewIndex) {

        int bullet = RedisUtil.val(PLAYER_GRANDPRIX_CONFIG_BULLET_KEY + user.getId(), 0);

        if (bullet != 0) {

            NetManager.sendHintMessageToClient("游戏进行中不能设置", user);
            return;

        }

        final Long userProopsNum = userPropsManager.getUserProopsNum(user, viewIndex);

        if (userProopsNum <= System.currentTimeMillis() && !UserPropsManager.TIME_PROPS_NO_TIME.contains(viewIndex)) {

            NetManager.sendHintMessageToClient("该外观已到期", user);
            return;

        }

        if (UserPropsManager.TIME_PROPS_BV.contains(viewIndex)) {

            userStatusManager.setUserStatus(new UserStatus<>(user.getId(), "batter:view:gp", viewIndex), null);

            final OseePublicData.PlayerStatusResponse.Builder userStatusInfo =
                    userStatusManager.getUserStatusInfo(user, Arrays.asList("batter:view:gp"), null);

            NetManager.sendMessage(OseeMessage.OseeMsgCode.S_C_TTMY_PLAYER_STATUS_RESPONSE_VALUE, userStatusInfo, user);

        } else if (UserPropsManager.TIME_PROPS_WV.contains(viewIndex)) {

            userStatusManager.setUserStatus(new UserStatus<>(user.getId(), "wing:view:gp", viewIndex), null);

            final OseePublicData.PlayerStatusResponse.Builder userStatusInfo =
                    userStatusManager.getUserStatusInfo(user, Arrays.asList("wing:view:gp"), null);

            NetManager.sendMessage(OseeMessage.OseeMsgCode.S_C_TTMY_PLAYER_STATUS_RESPONSE_VALUE, userStatusInfo, user);

        } else {

            NetManager.sendHintMessageToClient("外观不存在", user);

        }

    }

    public void playerAppearanceInformation(ServerUser user, FishingGrandPrixRoom room) {

        final OseePublicData.PlayerStatusResponse.Builder userStatusInfo =
                userStatusManager.getUserStatusInfo(user, Arrays.asList("batter:view:gp", "wing:view:gp"), null);

        if (room == null) {

            NetManager.sendMessage(OseeMessage.OseeMsgCode.S_C_TTMY_PLAYER_STATUS_RESPONSE_VALUE, userStatusInfo, user);

        } else {

            MyRefreshFishingUtil.sendRoomMessage(room, OseeMessage.OseeMsgCode.S_C_TTMY_PLAYER_STATUS_RESPONSE_VALUE,
                    userStatusInfo);

        }

    }

    /**
     * 发送重新激活消息相关消息
     */
    public void sendReactiveMessage(FishingGrandPrixRoom gameRoom, FishingGrandPrixPlayer player) {

        // sendJoinRoomResponse(gameRoom, player); // 发送加入房间消息
        sendPlayersInfoResponse(gameRoom, player); // 发送玩家数据列表消息
        sendSynchroniseResponse(gameRoom, player); // 发送同步鱼消息
        sendFrozenMessage(gameRoom, player); // 发送房间当前冰冻消息

    }

    public void RankReword(long rankType, ServerUser user) {

        RankRewordResponse.Builder builder = RankRewordResponse.newBuilder();

        List<AppRewardRankEntity> rewardRank = appRewardRankMapper.findRankReward(rankType);

        builder.setRankType(rankType);

        long a = 0;

        for (AppRewardRankEntity appRewardRankEntity : rewardRank) {

            RankRewordMessage.Builder builder1 = RankRewordMessage.newBuilder();

            if (appRewardRankEntity.getRank() - a != 1) {
                String b = a + 1 + "-" + appRewardRankEntity.getRank();
                builder1.setRank(b);
            } else {
                builder1.setRank(appRewardRankEntity.getRank() + "");
            }

            a = appRewardRankEntity.getRank();
            AppRewardLogEntity reward = appRewardRankEntity.getReward();
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

            builder.addRankRewordMessage(builder1);
            builder1.setItemId(0);
            builder1.setItemNum(0);

        }

        NetManager.sendMessage(OseeMessage.OseeMsgCode.S_C__RANK_REWORD_RESPONSE_VALUE, builder, user);

    }

    public static void main(String[] args) {
        System.out.println((1000 - 1000) / 5000);
    }

    /**
     * 道具使用状态同步
     */
    public void propsUseStateSync(FishingGrandPrixPlayer player, FishingGrandPrixRoom gameRoom) {

        for (BaseGamePlayer gamePlayer : gameRoom.getGamePlayers()) {
            if (gamePlayer != null) {
                FishingGrandPrixPlayer fishingGrandPrixPlayer = (FishingGrandPrixPlayer) gamePlayer;
                TtmyFishingChallengeMessage.FishingChallengeUseSkillResponse.Builder skillBuilder =
                        TtmyFishingChallengeMessage.FishingChallengeUseSkillResponse.newBuilder();
                skillBuilder.setPlayerId(fishingGrandPrixPlayer.getId());
                if (fishingGrandPrixPlayer.getLastCritTime() != 0) { // 开启暴击
                    skillBuilder.setSkillId(ItemId.SKILL_CRIT.getId());
                    skillBuilder.setDuration(Integer.MAX_VALUE);
                    NetManager.sendMessage(OseeMessage.OseeMsgCode.S_C_TTMY_FISHING_CHALLENGE_USE_SKILL_RESPONSE_VALUE,
                            skillBuilder, player.getUser());
                }
                if (fishingGrandPrixPlayer.getLastElectromagneticTime() != 0) {// 开启电磁炮
                    skillBuilder.setSkillId(ItemId.SKILL_ELETIC.getId());
                    skillBuilder.setDuration(Integer.MAX_VALUE);
                    NetManager.sendMessage(OseeMessage.OseeMsgCode.S_C_TTMY_FISHING_CHALLENGE_USE_SKILL_RESPONSE_VALUE,
                            skillBuilder, player.getUser());
                }
                if (fishingGrandPrixPlayer.getLastLockTime() != 0) {// 开启锁定
                    skillBuilder.setSkillId(ItemId.SKILL_LOCK.getId());
                    skillBuilder.setDuration(Integer.MAX_VALUE);
                    NetManager.sendMessage(OseeMessage.OseeMsgCode.S_C_TTMY_FISHING_CHALLENGE_USE_SKILL_RESPONSE_VALUE,
                            skillBuilder, player.getUser());
                }
                if (fishingGrandPrixPlayer.getLastAutoFireTime() != 0) { // 开启自动开炮
                    skillBuilder.setSkillId(ItemId.SKILL_AUTO_FIRE.getId());
                    skillBuilder.setDuration(Integer.MAX_VALUE);
                    NetManager.sendMessage(OseeMessage.OseeMsgCode.S_C_TTMY_FISHING_CHALLENGE_USE_SKILL_RESPONSE_VALUE,
                            skillBuilder, player.getUser());
                }
            }
        }

    }

    /**
     * 玩家金币同步
     */
    public void playerMoneySync(FishingGrandPrixPlayer player, FishingGrandPrixRoom room) {

        OseePlayerEntity oseePlayerEntity = PlayerManager.getPlayerEntity(UserContainer.getUserById(player.getId()));

        doFishingGrandPrixFireResponse(room, player, 0, 0, 0, oseePlayerEntity.getDragonCrystal(),
                oseePlayerEntity.getDiamond(), RedisUtil.val(PLAYER_GRANDPRIX_CONFIG_BULLET_KEY + player.getId(), 1000));

    }

    /**
     * 广播玩家发送子弹响应，备注：也可以用于返回玩家金币的响应，fireId 传 0就行，前端那边会自行处理
     */
    public static void doFishingGrandPrixFireResponse(FishingGrandPrixRoom gameRoom, FishingGrandPrixPlayer player,
                                                      long fireId, long fishId, float angle, long restMoney, long restDiamond, int bullet) {

        FishingGrandPrixFireResponse.Builder builder = FishingGrandPrixFireResponse.newBuilder();

        builder.setFireId(fireId);
        builder.setFishId(fishId);
        builder.setAngle(angle);

        builder.setPlayerId(player.getId());

        builder.setRestMoney(restMoney);

        builder.setRestDiamond(restDiamond);

        builder.setBullet(bullet);

        MyRefreshFishingUtil.sendRoomMessage(gameRoom,
                OseeMessage.OseeMsgCode.S_C_TTMY_FISHING_GRAND_PRIX_FIRE_RESPONSE_VALUE, builder);

    }

}
