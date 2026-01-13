package com.maple.game.osee.manager.fishing;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.text.StrBuilder;
import com.maple.database.config.redis.RedisHelper;
import com.maple.database.data.entity.UserEntity;
import com.maple.engine.container.DataContainer;
import com.maple.engine.container.UserContainer;
import com.maple.engine.data.ServerUser;
import com.maple.engine.utils.ThreadPoolUtils;
import com.maple.game.osee.common.RedisUtil;
import com.maple.game.osee.dao.data.entity.OseePlayerEntity;
import com.maple.game.osee.dao.data.mapper.OseePlayerMapper;
import com.maple.game.osee.dao.log.entity.*;
import com.maple.game.osee.dao.log.mapper.AppRewardRankMapper;
import com.maple.game.osee.dao.log.mapper.OseeCutMoneyLogMapper;
import com.maple.game.osee.dao.log.mapper.OseeFishingRecordLogMapper;
import com.maple.game.osee.dao.log.mapper.OseePlayerTenureLogMapper;
import com.maple.game.osee.entity.GameEnum;
import com.maple.game.osee.entity.ItemChangeReason;
import com.maple.game.osee.entity.ItemId;
import com.maple.game.osee.entity.fishing.FishingGamePlayer;
import com.maple.game.osee.entity.fishing.FishingGameRoom;
import com.maple.game.osee.entity.fishing.csv.file.FishConfig;
import com.maple.game.osee.entity.fishing.csv.file.PlayerLevelConfig;
import com.maple.game.osee.entity.fishing.game.FishStruct;
import com.maple.game.osee.entity.fishing.task.GoalType;
import com.maple.game.osee.entity.fishing.task.TaskType;
import com.maple.game.osee.manager.PlayerManager;
import com.maple.game.osee.manager.UserPropsManager;
import com.maple.game.osee.manager.UserStatusManager;
import com.maple.game.osee.manager.fishing.util.FishingUtil;
import com.maple.game.osee.proto.OseeMessage;
import com.maple.game.osee.proto.OseeMessage.OseeMsgCode;
import com.maple.game.osee.proto.OseePublicData;
import com.maple.game.osee.proto.TtmyFishingRecordProto;
import com.maple.game.osee.proto.fishing.OseeFishingMessage.*;
import com.maple.game.osee.util.FishingHelper;
import com.maple.game.osee.util.GameUtil;
import com.maple.game.osee.util.MyRefreshFishingHelper;
import com.maple.game.osee.util.MyRefreshFishingUtil;
import com.maple.gamebase.container.GameContainer;
import com.maple.gamebase.data.BaseGamePlayer;
import com.maple.gamebase.data.BaseGameRoom;
import com.maple.gamebase.data.fishing.BaseFishingRoom;
import com.maple.gamebase.manager.fishing.BaseFishingManager;
import com.maple.network.manager.NetManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.maple.game.osee.manager.fishing.FishingHitDataManager.GREENER_LIMIT;

/**
 * 1688捕鱼管理类
 */
@Component
@Slf4j
public class FishingManager extends BaseFishingManager {

    @Autowired
    private OseePlayerTenureLogMapper tenureLogMapper;

    @Autowired
    private OseeCutMoneyLogMapper cutMoneyLogMapper;

    @Autowired
    private OseeFishingRecordLogMapper fishingRecordLogMapper;

    @Autowired
    private OseePlayerMapper playerMapper;

    @Autowired
    private AppRewardRankMapper appRewardRankMapper;

    /**
     * 默认鱼存活时间
     */
    public static final int DEFAULT_LIFE_TIME = 120;

    /**
     * 技能锁定持续时间
     */
    public static final long SKILL_LOCK_TIME = 20000;

    /**
     * 技能急速持续时间
     */
    public static final long SKILL_FAST_TIME = 20000;

    /**
     * 技能电磁炮持续时间
     */
    public static final long SKILL_ELETIC_TIME = 20000;

    /**
     * 技能黑洞炮持续时间
     */
    public static final long SKILL_BLACK_HOLE_TIME = 20000;

    /**
     * 技能鱼雷炮持续时间
     */
    public static final long SKILL_TORPEDO_TIME = 20000;

    /**
     * 技能钻头持续时间
     */
    public static final long SKILL_BIT_TIME = 20000;

    /**
     * 技能冰冻持续时间
     */
    // public static final long SKILL_FROZEN_TIME = 20000;
    public static final long SKILL_FROZEN_TIME = 15000;

    /**
     * 技能暴击持续时间
     */
    public static final long SKILL_CRIT_TIME = 20000;

    /**
     * 分身炮持续时间
     */
    public static final long SKILL_FEN_SHEN_TIME = 20000;

    /**
     * 技能翻倍持续时间
     */
    public static final long SKILL_DOUBLE_TIME = 20000;

    /**
     * 未操作踢出房间的时长
     */
    public static final long ROOM_KICK_TIME = 5 * 60 * 1000; // 5分钟
//    public static final long ROOM_KICK_TIME = 1 * 60 * 1000; // 1分钟

    /**
     * 房间使用boss号角的冷却时长
     */
    public static final long BOSS_BUGLE_COOL_TIME = 60 * 1000; // 60秒

    /**
     * 玩家今日积分 + playerId
     */
    public static final String PLAYER_CONFIG_POINT_DAY_KEY = "player:config:point:day:";

    /**
     * 玩家周积分 + playerId
     */
    public static final String PLAYER_CONFIG_POINT_WEEK_KEY = "player:config:point:week:";

    /**
     * 玩家月积分 + playerId
     */
    public static final String PLAYER_CONFIG_POINT_MONTH_KEY = "player:config:point:month:";

    /**
     * 月排行榜
     */
    public static final String PLAYER_CONFIG_RANK_MONTH_KEY = "player:config:rank:month";

    /**
     * 周排行榜
     */
    public static final String PLAYER_CONFIG_RANK_WEEK_KEY = "player:config:rank:week";

    /**
     * 日排行榜
     */
    public static final String PLAYER_CONFIG_RANK_DAY_KEY = "player:config:rank:day";

    /**
     * 月排行榜
     */
    public static final String PLAYER_CONFIG_RANK_LAST_MONTH_KEY = "player:config:last:rank:month:";

    /**
     * 周排行榜
     */
    public static final String PLAYER_CONFIG_RANK_LAST_WEEK_KEY = "player:config:last:rank:week:";

    /**
     * 日排行榜
     */
    public static final String PLAYER_CONFIG_RANK_LAST_DAY_KEY = "player:config:last:rank:day:";

    /**
     * 玩家今日掉落弹头数 + playerId
     */
    public static final String PLAYER_CONFIG_GOLD_DAY_KEY = "player:config:gold:day:";

    /**
     * 玩家周掉落弹头数 + playerId
     */
    public static final String PLAYER_CONFIG_GOLD_WEEK_KEY = "player:config:gold:week:";

    /**
     * 玩家月掉落弹头数 + playerId
     */
    public static final String PLAYER_CONFIG_GOLD_MONTH_KEY = "player:config:gold:month:";

    /**
     * 月排行榜
     */
    public static final String PLAYER_CONFIG_GOLD_RANK_MONTH_KEY = "player:config:gold:rank:month";

    /**
     * 周排行榜
     */
    public static final String PLAYER_CONFIG_GOLD_RANK_WEEK_KEY = "player:config:gold:rank:week";

    /**
     * 日排行榜
     */
    public static final String PLAYER_CONFIG_GOLD_RANK_DAY_KEY = "player:config:gold:rank:day";

    /**
     * 月排行榜
     */
    public static final String PLAYER_CONFIG_RANK_GOLD_LAST_MONTH_KEY = "player:config:last:gold:rank:month:";

    /**
     * 周排行榜
     */
    public static final String PLAYER_CONFIG_RANK_GOLD_LAST_WEEK_KEY = "player:config:last:gold:rank:week:";

    /**
     * 日排行榜
     */
    public static final String KILL_FISH_RANK_DAY_KEY = "kill:fish:rank:day";

    /**
     * 月排行榜
     */
    public static final String KILL_FISH_RANK_MONTH_KEY = "kill:fish:rank:month:";

    /**
     * 周排行榜
     */
    public static final String KILL_FISH_RANK_WEEK_KEY = "kill:fish:rank:week:";

    /**
     * 日排行榜
     */
    public static final String FIGHT_NUM_RANK_DAY_KEY = "fight:num:rank:day";

    /**
     * 月排行榜
     */
    public static final String FIGHT_NUM_RANK_MONTH_KEY = "fight:num:rank:month:";

    /**
     * 周排行榜
     */
    public static final String FIGHT_NUM_RANK_WEEK_KEY = "fight:num:rank:week:";

    /**
     * 日排行榜
     */
    public static final String PLAYER_CONFIG_RANK_GOLD_LAST_DAY_KEY = "player:config:last:gold:rank:day:";

    /**
     * 炮台等级限制
     */
    public static final int[][] batteryLevelLimit =
            {{Integer.MAX_VALUE, Integer.MIN_VALUE}, {Integer.MAX_VALUE, Integer.MIN_VALUE},
                    {Integer.MAX_VALUE, Integer.MIN_VALUE}, {Integer.MAX_VALUE, Integer.MIN_VALUE},
                    {Integer.MAX_VALUE, Integer.MIN_VALUE}, {Integer.MAX_VALUE, Integer.MIN_VALUE}};

    /**
     * 房间进入金币限制
     */
    public static final long[] enterLimit = {0, 500000, 2000000, 10000000, 50000000};

    /**
     * 各个鱼雷的金币价值：青铜、白银、黄金
     */
    public static long[] TORPEDO_VALUE = {500000, 5000000, 500000, 5000000};

    public static long[] TORPEDO_FALSE_VALUE = {5000000, 50000000, 2000000, 20000000};

    /**
     * 免费玩家掉落几率
     */
    public static double TORPEDO_DROP_FREE_RATE = -1;

    /**
     * 免费玩家兑换几率
     */
    public static double TORPEDO_DROP_EXCHANGE_RATE = -1;

    /**
     * 免费玩家掉落几率
     */
    public static double CHALLENGE_DROP_EXCHANGE_RATE = -1;

    /**
     * 付费玩家每充值金钱数
     */
    public static long TORPEDO_DROP_PER_PAY_MONEY = -1;

    /**
     * 付费玩家每充值提升几率
     */
    public static double TORPEDO_DROP_PER_PAY_RATE = -1;

    /**
     * 免费玩家掉落几率
     */
    public static double CHALLENGE_DROP_FREE_RATE = -1;

    /**
     * 付费玩家每充值金钱数
     */
    public static long CHALLENGE_DROP_PER_PAY_MONEY = -1;

    /**
     * 付费玩家每充值提升几率
     */
    public static double CHALLENGE_DROP_PER_PAY_RATE = -1;

    /**
     * 记录掉落和使用的鱼雷数量的Map
     */
    public static ConcurrentHashMap<String, Long> TORPEDO_RECORD = new ConcurrentHashMap<>();

    public FishingManager() {
        super();
        // List<BatteryLevelConfig> configs = DataContainer.getDatas(BatteryLevelConfig.class);
        // for (BatteryLevelConfig config : configs) {
        // int scene = config.getScene() - 1;
        //
        // batteryLevelLimit[scene][0] = Math.min(batteryLevelLimit[scene][0], config.getBatteryLevel());
        // batteryLevelLimit[scene][1] = Math.max(batteryLevelLimit[scene][1], config.getBatteryLevel());
        // }
        //
        // ThreadPoolUtils.TASK_SERVICE_POOL.schedule(() -> {
        // // 创建40个空房间
        // int createRoomNum = 20;
        // for (int i = 0; i < createRoomNum; i++) {
        // FishingGameRoom gameRoom = GameContainer.createGameRoom(FishingGameRoom.class, 4);
        // //TODO:2022年5月6日 新建房间删除以前的房间缓存
        // RedisHelper.set("FISHING_GAME_GOLD_FISH_NUM2" + gameRoom.getCode(), "0");
        // gameRoom.setVipLevel(3);
        // gameRoom.setRoomIndex(5);
        // }
        // int createRoomNum1 = 10;
        // for (int x = 0; x < createRoomNum1; x++) {
        // FishingGameRoom gameRoom = GameContainer.createGameRoom(FishingGameRoom.class, 4);
        // //TODO:2022年5月6日 新建房间删除以前的房间缓存
        // RedisHelper.set("FISHING_GAME_GOLD_FISH_NUM2" + gameRoom.getCode(), "0");
        // gameRoom.setVipLevel(7);
        // gameRoom.setRoomIndex(5);
        // }
        //
        // }, 5, TimeUnit.SECONDS);
    }

    /**
     * 玩家房间
     */
    public void playerJoinRoom(ServerUser user, int roomIndex) {
        // if (PlayerManager.getPlayerBatteryLevel(user) < batteryLevelLimit[roomIndex - 1][0]) {
        // // 炮台等级不足，发送解锁炮台提示
        // unlockBatteryLevelHint(user, batteryLevelLimit[roomIndex - 1][0]);
        // NetManager.sendHintBoxMessageToClient("炮台等级不足，请解锁更高等级炮台", user, 10);
        // return;
        // }

        // int roomIndex1 = 0;
        // int roomIndex2 = 0;
        // for (int i = 1; i < 7; i++) {
        // int minBatteryLevel = batteryLevelLimit[i - 1][0];
        // if (PlayerManager.getPlayerBatteryLevel(user) < minBatteryLevel) {
        // break;
        // } else {
        // roomIndex1 = i;
        // }
        // // 场次最低炮台等级限制
        // }
        // for (int j = 1; j < 6; j++) { // 检测金币是否满足进入房间的限制
        // if (!PlayerManager.checkItem(user, ItemId.MONEY, DataContainer.getData(j,
        // FishJoinMoneyConfig.class).getMinMoney())) {
        // break;
        // } else {
        // roomIndex2 = j;
        // }
        // }
        // long money = PlayerManager.getPlayerEntity(user).getMoney();
        // if (roomIndex1 >= roomIndex2) {
        // if (roomIndex == 1) {
        // if (money > 50 * 10000) {
        // NetManager.sendHintBoxMessageToClient("携带金币过多，请前往更高场次", user, 10);
        // return;
        // }
        // } else if (roomIndex == 2) {
        // if (money >= 200 * 10000) {
        // NetManager.sendHintBoxMessageToClient("携带金币过多，请前往更高场次", user, 10);
        // return;
        // }
        // } else if (roomIndex == 3) {
        // if (money >= 2000 * 10000) {
        // NetManager.sendHintBoxMessageToClient("携带金币过多，请前往更高场次", user, 10);
        // return;
        // }
        // } else if (roomIndex == 4) {
        // if (money >= 30000 * 10000) {
        // NetManager.sendHintBoxMessageToClient("携带金币过多，请前往更高场次", user, 10);
        // return;
        // }
        // }
        // } else {
        // if (roomIndex < roomIndex1) {
        // if (roomIndex == 1) {
        // if (money > 50 * 10000) {
        // NetManager.sendHintBoxMessageToClient("携带金币过多，请前往更高场次", user, 10);
        // return;
        // }
        // } else if (roomIndex == 2) {
        // if (money >= 200 * 10000) {
        // NetManager.sendHintBoxMessageToClient("携带金币过多，请前往更高场次", user, 10);
        // return;
        // }
        // } else if (roomIndex == 3) {
        // if (money >= 2000 * 10000) {
        // NetManager.sendHintBoxMessageToClient("携带金币过多，请前往更高场次", user, 10);
        // return;
        // }
        // } else if (roomIndex == 4) {
        // if (money >= 200000 * 10000) {
        // NetManager.sendHintBoxMessageToClient("携带金币过多，请前往更高场次", user, 10);
        // return;
        // }
        // }
        // }
        // }
        // if (!PlayerManager.checkItem(user, ItemId.MONEY, DataContainer.getData(roomIndex,
        // FishJoinMoneyConfig.class).getMinMoney())) {
        // NetManager.sendHintMessageToClient("携带金币不足，无法进入该房间", user);
        // return;
        // }

        if (roomIndex == 3 && PlayerManager.getPlayerVipLevel(user) < 4) {
            NetManager.sendErrorMessageToClient("VIP4才能加入该房间", user);
            return;
        }
        if (roomIndex == 4 && PlayerManager.getPlayerVipLevel(user) < 8) {
            NetManager.sendErrorMessageToClient("VIP8才能加入该房间", user);
            return;
        }
        // TODO:2022年4月12日 换桌功能修改
        if (roomIndex > 4) {
            NetManager.sendErrorMessageToClient("房间类型有误", user);
            return;
        }
        List<FishingGameRoom> gameRooms = GameContainer.getGameRooms(FishingGameRoom.class);
        final FishingGameRoom fishingGameRoom = gameRooms.stream()
                .filter(
                        gameRoom -> gameRoom.getRoomIndex() == roomIndex && gameRoom.getMaxSize() > gameRoom.getPlayerSize())
                .findAny().orElse(createFishingRoom(roomIndex));// 没有房间就新建一个房间
        if (fishingGameRoom != null) {
            synchronized (fishingGameRoom) {
                joinFishingRoom(user, fishingGameRoom);
            }
            return;
        }
        NetManager.sendHintBoxMessageToClient("当前场次拥挤，请前往其他场次！", user, 10);
        // for (FishingGameRoom gameRoom : gameRooms) {
        // if (roomIndex == 5 && gameRoom.getVipLevel() > PlayerManager.getPlayerVipLevel(user)) {
        // continue;
        // }
        // // 房间条件: 1:目标房间场次与玩家所选场次相同 2:房间人数未满
        // if (gameRoom.getRoomIndex() == roomIndex && gameRoom.getMaxSize() > gameRoom.getPlayerSize()) {
        // synchronized (fishingGameRoom) {
        // joinFishingRoom(user, fishingGameRoom);
        // }
        // return;
        // }
        // }
        // if (roomIndex < 5) {
        // // 没有房间就新建一个房间
        // FishingGameRoom gameRoom = createFishingRoom(roomIndex);
        // synchronized (gameRoom) {
        // joinFishingRoom(user, gameRoom);
        // }
        // } else {
        // NetManager.sendHintBoxMessageToClient("当前场次拥挤，请前往其他场次！", user, 10);
        // return;
        // }
    }

    /**
     * 创建捕鱼房间
     */
    private FishingGameRoom createFishingRoom(int roomIndex) {
        FishingGameRoom gameRoom = GameContainer.createGameRoom(FishingGameRoom.class, 4);
        // TODO:2022年5月6日 新建房间删除以前的房间缓存
        RedisHelper.set("FISHING_GAME_GOLD_FISH_NUM2" + gameRoom.getCode(), "0");
        gameRoom.setRoomIndex(roomIndex);
        gameRoom.setConfigGameId(-2);

        // if (FishingRobotManager.USE_ROBOT != 0) { // 使用机器人才生成默认机器人
        // ThreadPoolUtils.TASK_SERVICE_POOL.schedule(() -> {
        // // 加几个机器人占座
        // int robotNum = ThreadLocalRandom.current().nextInt(1, FishingRobotManager.ROBOT_COUNT + 1);
        // for (int i = 0; i < robotNum; i++) {
        // FishingGameRobot robotPlayer = robotManager.createRobotPlayer(gameRoom);
        // if (robotPlayer != null) {
        // // 将自己的信息发送给房间内所有玩家
        // MyRefreshFishingUtil
        // .sendRoomMessage(gameRoom, OseeMsgCode.S_C_OSEE_FISHING_PLAYER_INFO_RESPONSE_VALUE,
        // createPlayerInfoResponse(gameRoom, robotPlayer));
        // // 机器人自动发炮的定时任务
        // ThreadPoolUtils.TASK_SERVICE_POOL
        // .schedule(() -> robotManager.robotFire(gameRoom, robotPlayer.getId()), 0, TimeUnit.SECONDS);
        // }
        // }
        // }, 0, TimeUnit.SECONDS);
        // }

        // 获取：房间内的刷鱼规则
        MyRefreshFishingHelper.getRoomRefreshRule(gameRoom);
        // 检查并执行刷鱼
        MyRefreshFishingHelper.checkAndRefresh(gameRoom);

        return gameRoom;
    }

    /**
     * 捕鱼房间
     */
    private void joinFishingRoom(ServerUser user, FishingGameRoom gameRoom) {

        GameUtil.joinRoomPre(user.getId(), null);

        log.info("玩家[{}]普通捕鱼房间[{}]", user.getNickname(), gameRoom.getCode());
        if (gameRoom.getRoomIndex() == 5 && PlayerManager.getPlayerVipLevel(user) < gameRoom.getVipLevel()) {
            NetManager.sendHintMessageToClient("vip等级不足，无法进入该房间", user);
            return;
        }
        long enterMoney = PlayerManager.getPlayerEntity(user).getMoney();
        FishingGamePlayer gamePlayer = GameContainer.createGamePlayer(gameRoom, user, FishingGamePlayer.class);
        boolean a = true;
        boolean b = true;
        // if(gamePlayer.getSeat()==2 ||gamePlayer.getSeat()==3){
        //// playerJoinRoom(user,gameRoom.getRoomIndex());
        //// return;
        //// }
        log.info("gamePlayer" + gamePlayer.getSeat() + "gamePlayerName" + gamePlayer.getUser().getNickname());
        if (gamePlayer.getSeat() != 0 || gamePlayer.getSeat() != 1) {
            // log.info("gamePlayer"+gamePlayer.getSeat()+"gamePlayerName"+gamePlayer.getUser().getNickname());
            for (int i = 0; i < gameRoom.getGamePlayers().length; i++) {
                if (gameRoom.getGamePlayers()[i] != null) {
                    log.info("gameRoom.getGamePlayers()[i].getSeat()" + gameRoom.getGamePlayers()[i].getSeat() + "Name:"
                            + gameRoom.getGamePlayers()[i].getUser().getNickname());
                    if (gameRoom.getGamePlayers()[i].getSeat() == 0 && gameRoom.getGamePlayers()[i].getUser() != user) {
                        a = false;
                    }
                    if (gameRoom.getGamePlayers()[i].getSeat() == 1 && gameRoom.getGamePlayers()[i].getUser() != user) {
                        b = false;
                    }
                }
            }
            if (a) {
                gamePlayer.setSeat(0);
            } else {
                if (b) {
                    gamePlayer.setSeat(1);
                }
            }

        }
        gamePlayer.setEnterMoney(enterMoney);
        gamePlayer.setEnterRoomTime(System.currentTimeMillis());
        String viewIndex = RedisHelper.get("USE_BATTERYVIEW:" + user.getId());
        if (!viewIndex.isEmpty()) {
            gamePlayer.setViewIndex(Integer.valueOf(viewIndex));
        }
        // 设置玩家在房间内的初始炮台等级
        // 玩家拥有的最高炮台等级
        long batteryLevel = PlayerManager.getPlayerEntity(user).getBatteryLevel();
        int bMax = batteryLevelLimit[gameRoom.getRoomIndex() - 1][1];
        if (batteryLevel > bMax) { // 高于房间内最高使用炮台等级就用房间最高的；低于房间内最低使用炮台等级不能进房间了
            batteryLevel = bMax;
        }
        gamePlayer.setBatteryLevel(batteryLevel);
        int usedBatteryLevel = RedisUtil.val("USER_BATTERY_LEVEL_USED" + gamePlayer.getUser().getId(), 0);
        if (usedBatteryLevel > batteryLevel) {
            long xh1 = new Double(RedisUtil.val("ALL_XH_1-50" + gamePlayer.getUser().getId(), 0D)).longValue();
            long xh2 = new Double(RedisUtil.val("ALL_XH_50-100" + gamePlayer.getUser().getId(), 0D)).longValue();
            long xh3 = new Double(RedisUtil.val("ALL_XH_100-200" + gamePlayer.getUser().getId(), 0D)).longValue();
            long xh4 = new Double(RedisUtil.val("ALL_XH_200-max" + gamePlayer.getUser().getId(), 0D)).longValue();
            long cx = new Double(RedisUtil.val("ALL_CX_USER" + gamePlayer.getUser().getId(), 0D)).longValue();
            RedisHelper.set("ALL_CX_USER" + gamePlayer.getUser().getId(), String.valueOf(cx - xh1 - xh2 - xh3 - xh4));
            RedisHelper.set("ALL_XH_1-50" + gamePlayer.getUser().getId(), "0");
            RedisHelper.set("ALL_XH_50-100" + gamePlayer.getUser().getId(), "0");
            RedisHelper.set("ALL_XH_100-200" + gamePlayer.getUser().getId(), "0");
            RedisHelper.set("ALL_XH_200-max" + gamePlayer.getUser().getId(), "0");
        }
        RedisHelper.set("USER_BATTERY_LEVEL_USED" + gamePlayer.getUser().getId(), String.valueOf(batteryLevel));
        // int x = 0;
        // if (RedisUtil.val("USER_T_STATUS" + user.getId(), 0L) != 0) {
        // joinchangePeak(user, gameRoom.getRoomIndex() * 2 - 1);
        // } else {
        // joinchangePeak(user, (gameRoom.getRoomIndex() - 1) * 2);
        // }
        // RedisHelper.set("USER_T_PEAK_VALUE"+user.getId(),String.valueOf(x));
        // log.info("发送房间消息");
        sendJoinRoomResposne(gameRoom, user); // 发送房间消息
        // log.info("发送玩家数据列表消息");
        sendPlayersInfoResponse(gameRoom, user); // 发送玩家数据列表消息
        // log.info("发送同步鱼消息");
        sendSynchroniseResponse(gameRoom, user); // 发送同步鱼消息
        // log.info("发送房间当前冰冻消息");
        sendFrozenMessage(gameRoom, user); // 发送房间当前冰冻消息
        // log.info("将自己的数据广播到房间所有玩家");
        // 将自己的数据广播到房间所有玩家
        MyRefreshFishingUtil.sendRoomMessage(gameRoom, OseeMsgCode.S_C_OSEE_FISHING_PLAYER_INFO_RESPONSE_VALUE,
                createPlayerInfoResponse(gameRoom, gamePlayer));
        // for (int i = 0; i < gameRoom.getMaxSize(); i++) {
        // FishingGamePlayer player = gameRoom.getGamePlayerBySeat(i);
        // if (player == null || !player.getUser().isOnline() || player.getId() == user.getId()) {
        // continue;
        // }
        // NetManager.sendMessage(OseeMsgCode.S_C_OSEE_FISHING_PLAYER_INFO_RESPONSE_VALUE, resp, player.getUser());
        // }
    }

    /**
     * 玩家重连
     */
    public void fishingRoomList(ServerUser user) {
        FishingRoomListResponse.Builder builder = FishingRoomListResponse.newBuilder();
        List<FishingGameRoom> fishingGameRooms = GameContainer.getGameRooms(FishingGameRoom.class);
        List<FishingGameRoom> gameRooms = GameContainer.getGameRooms(FishingGameRoom.class).stream()
                .filter(room -> room.getRoomIndex() == 5).collect(Collectors.toList());
        gameRooms.forEach(fishingRoom -> builder.addRoomList(createRoomInfoProto(fishingRoom)));
        NetManager.sendMessage(OseeMsgCode.S_C_TTMY_FISHING_ROOM_LIST_RESPONSE_VALUE, builder, user);
    }

    /**
     * 创建房间信息协议
     */
    public FishingRoomInfoProto.Builder createRoomInfoProto(FishingGameRoom room) {
        FishingRoomInfoProto.Builder builder = FishingRoomInfoProto.newBuilder();
        builder.setRoomCode(room.getCode());
        builder.setBoss(room.getBoss());
        builder.setVip(room.getVipLevel());
        for (BaseGamePlayer gamePlayer : room.getGamePlayers()) {
            if (gamePlayer != null) {
                UserEntity userEntity = gamePlayer.getUser().getEntity();
                if (userEntity.getHeadIndex() == 0) {
                    builder.addHeadImg(userEntity.getHeadUrl());
                } else {
                    builder.addHeadImg(String.valueOf(userEntity.getHeadIndex()));
                }
            }
        }
        return builder;
    }

    /**
     * 换座
     */
    public void changeSeat(FishingGameRoom gameRoom, FishingGamePlayer player, int seat) {
        ServerUser user = UserContainer.getUserById(player.getUser().getId());
        if (seat < 0 || seat >= gameRoom.getMaxSize()) {
            NetManager.sendErrorMessageToClient("座位序号有误", player.getUser());
            return;
        }
        synchronized (gameRoom) {
            BaseGamePlayer[] baseGamePlayer = gameRoom.getGamePlayers();
            for (BaseGamePlayer baseGamePlayer1 : baseGamePlayer) {
                if (baseGamePlayer1 != null && baseGamePlayer1.getSeat() == seat) {
                    NetManager.sendErrorMessageToClient("该座位已经有玩家了哦", player.getUser());
                    return;
                }
            }
            // 当前座位号
            int nowSeat = player.getSeat();
            gameRoom.getGamePlayers()[nowSeat] = null; // 清空之前座位的玩家信息
            gameRoom.getGamePlayers()[seat] = player; // 将自己的信息移到新座位
            gameRoom.getGamePlayers()[seat].setSeat(seat); // 玩家设置新座位号
            FishingPlayersInfoResponse.Builder builder = FishingPlayersInfoResponse.newBuilder();
            for (BaseGamePlayer gamePlayer : gameRoom.getGamePlayers()) {
                if (gamePlayer != null) {
                    // log.info("玩家信息："+gamePlayer.getSeat()+"------"+gamePlayer.getUser().getEntity().getNickname());
                    builder.addPlayerInfos(createPlayerInfoProto((FishingGamePlayer) gamePlayer, gameRoom));
                }
            }
            MyRefreshFishingUtil.sendRoomMessage(gameRoom, OseeMsgCode.S_C_OSEE_FISHING_PLAYERS_INFO_RESPONSE_VALUE,
                    builder);
            // sendPlayersInfoResponse(gameRoom, user);
        }
    }

    /**
     * 离开捕鱼房间
     */
    public void exitFishingRoom(FishingGameRoom gameRoom, ServerUser user) {

        FishingGamePlayer player = gameRoom.getGamePlayerById(user.getId());

        if (player == null || player.getId() == 0) {
            return;
        }

        RedisHelper.set("USER_BOSS_MULT_48" + player.getUser().getId(), String.valueOf(0));
        RedisHelper.set("USER_BOSS_MULT_49" + player.getUser().getId(), String.valueOf(0));
        RedisHelper.set("USER_BOSS_MULT_50" + player.getUser().getId(), String.valueOf(0));
        RedisHelper.set("USER_BOSS_MULT_51" + player.getUser().getId(), String.valueOf(0));
        RedisHelper.set("USER_BOSS_MULT_52" + player.getUser().getId(), String.valueOf(0));
        RedisHelper.set("USER_BOSS_MULT_53" + player.getUser().getId(), String.valueOf(0));
        RedisHelper.set("USER_BOSS_MULT_54" + player.getUser().getId(), String.valueOf(0));
        RedisHelper.set("USER_BOSS_MULT_28" + player.getUser().getId(), String.valueOf(0));
        RedisHelper.set("USER_BOSS_MULT_29" + player.getUser().getId(), String.valueOf(0));
        RedisHelper.set("USER_BOSS_MULT_30" + player.getUser().getId(), String.valueOf(0));
        RedisHelper.set("USER_BOSS_MULT_31" + player.getUser().getId(), String.valueOf(0));
        RedisHelper.set("USER_BOSS_MULT_32" + player.getUser().getId(), String.valueOf(0));
        RedisHelper.set("USER_BOSS_MULT_33" + player.getUser().getId(), String.valueOf(0));
        RedisHelper.set("USER_BOSS_MULT_34" + player.getUser().getId(), String.valueOf(0));
        RedisHelper.set("USER_BOSS_MULT_35" + player.getUser().getId(), String.valueOf(0));
        RedisHelper.set("USER_BOSS_MULT_36" + player.getUser().getId(), String.valueOf(0));
        RedisHelper.set("USER_BOSS_MULT_37" + player.getUser().getId(), String.valueOf(0));
        RedisHelper.set("USER_BOSS_MULT_38" + player.getUser().getId(), String.valueOf(0));
        RedisHelper.set("USER_BOSS_MULT_39" + player.getUser().getId(), String.valueOf(0));
        RedisHelper.set("USER_BOSS_MULT_40" + player.getUser().getId(), String.valueOf(0));
        RedisHelper.set("USER_BOSS_MULT_41" + player.getUser().getId(), String.valueOf(0));
        RedisHelper.set("USER_BOSS_MULT_42" + player.getUser().getId(), String.valueOf(0));
        RedisHelper.set("USER_BOSS_MULT_43" + player.getUser().getId(), String.valueOf(0));
        RedisHelper.set("USER_BOSS_MULT_44" + player.getUser().getId(), String.valueOf(0));
        RedisHelper.set("USER_BOSS_MULT_45" + player.getUser().getId(), String.valueOf(0));
        RedisHelper.set("USER_BOSS_MULT_46" + player.getUser().getId(), String.valueOf(0));
        RedisHelper.set("USER_BOSS_MULT_47" + player.getUser().getId(), String.valueOf(0));
        RedisHelper.set("USER_BOSS_MULT_55" + player.getUser().getId(), String.valueOf(0));
        RedisHelper.set("USER_BOSS_MULT_56" + player.getUser().getId(), String.valueOf(0));
        RedisHelper.set("USER_BOSS_MULT_57" + player.getUser().getId(), String.valueOf(0));
        RedisHelper.set("USER_BOSS_MULT_58" + player.getUser().getId(), String.valueOf(0));
        RedisHelper.set("USER_BOSS_MULT_59" + player.getUser().getId(), String.valueOf(0));
        RedisHelper.set("USER_BOSS_MULT_60" + player.getUser().getId(), String.valueOf(0));
        RedisHelper.set("USER_BOSS_MULT_61" + player.getUser().getId(), String.valueOf(0));
        RedisHelper.set("USER_BOSS_MULT_62" + player.getUser().getId(), String.valueOf(0));
        RedisHelper.set("USER_BOSS_MULT_63" + player.getUser().getId(), String.valueOf(0));
        RedisHelper.set("USER_BOSS_MULT_64" + player.getUser().getId(), String.valueOf(0));
        RedisHelper.set("USER_BOSS_MULT_65" + player.getUser().getId(), String.valueOf(0));
        RedisHelper.set("USER_BOSS_MULT_66" + player.getUser().getId(), String.valueOf(0));
        RedisHelper.set("USER_FISHTYPE_1_T" + player.getUser().getId(), String.valueOf(0));
        RedisHelper.set("USER_FISHTYPE_2_T" + player.getUser().getId(), String.valueOf(0));
        RedisHelper.set("USER_FISHTYPE_3_T" + player.getUser().getId(), String.valueOf(0));
        RedisHelper.set("USER_FISHTYPE_4_T" + player.getUser().getId(), String.valueOf(0));
        long nowTime = System.currentTimeMillis();
        player.setLastBlackHoleTime(0);
        player.setLastTorpedoTime(0);
        player.setLastBitTime(0);
        player.setLastElectromagneticTime(0);
        player.setLastFastTime(0);
        player.setLastFrozenTime(0);
        player.setLastLockTime(0);
        player.setLastCritTime(0);
        player.setLastFenShenTime(0);
        long a = RedisUtil.val("USER_XML_MONEY" + player.getUser().getId(), 0D).longValue();
        if (a != 0) {
            PlayerManager.addItem(UserContainer.getUserById(player.getUser().getId()), ItemId.MONEY.getId(), a,
                    ItemChangeReason.GAME_S, true);
            RedisHelper.set("USER_XML_MONEY" + player.getUser().getId(), "0");
        }

        long a1 = RedisUtil.val("DOUBLE_KILL_WINMONEY" + player.getUser().getId(), 0D).longValue();
        if (a1 != 0) {
            PlayerManager.addItem(UserContainer.getUserById(player.getUser().getId()), ItemId.MONEY.getId(), a1,
                    ItemChangeReason.GAME_S, true);
            RedisHelper.set("DOUBLE_KILL_WINMONEY" + player.getUser().getId(), "0");
        }

        long stayTime = (System.currentTimeMillis() - player.getEnterRoomTime()) / 1000;
        // 做累计在线任务 分钟
        FishingTaskManager.doTask(user, TaskType.DAILY, GoalType.ONLINE, 0, (int) (stayTime / 60));
        FishingTaskManager.doTask1(user);
        if (new Double(FishingHitDataManager.getPlayerFishingProb(user.getId())).longValue() != 0) {
            FishingHitDataManager.setPlayerFishingProb(user.getId(), 0.00);
            int greener = player.getLevel() >= GREENER_LIMIT ? 1 : 0;
            int index = gameRoom.getRoomIndex() - 1;
            if (greener == 1) {
                if (index == 0) {
                    FishingHitDataManager.setTotalWin(user.getId(), 4,
                            Long.valueOf(RedisHelper.get("FISHING_TOTALWIN" + user.getId())));
                } else if (index == 1) {
                    FishingHitDataManager.setTotalWin(user.getId(), 5,
                            Long.valueOf(RedisHelper.get("FISHING_TOTALWIN" + user.getId())));
                } else if (index == 2) {
                    FishingHitDataManager.setTotalWin(user.getId(), 6,
                            Long.valueOf(RedisHelper.get("FISHING_TOTALWIN" + user.getId())));
                } else if (index == 3) {
                    FishingHitDataManager.setTotalWin(user.getId(), 7,
                            Long.valueOf(RedisHelper.get("FISHING_TOTALWIN" + user.getId())));
                } else {
                    FishingHitDataManager.setTotalWin(user.getId(), 9,
                            Long.valueOf(RedisHelper.get("FISHING_TOTALWIN" + user.getId())));
                }
            } else {
                if (index == 4) {
                    FishingHitDataManager.setTotalWin(user.getId(), 8,
                            Long.valueOf(RedisHelper.get("FISHING_TOTALWIN" + user.getId())));
                }
                FishingHitDataManager.setTotalWin(user.getId(), index,
                        Long.valueOf(RedisHelper.get("FISHING_TOTALWIN" + user.getId())));
            }
        }
        OseePlayerEntity entity = PlayerManager.getPlayerEntity(user);
        if (entity != null) {
            // 保存玩家金币变化记录
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
                if (gameRoom.getRoomIndex() == 5) {
                    recordLogEntity.setRoomIndex(7);
                } else {
                    recordLogEntity.setRoomIndex(gameRoom.getRoomIndex());
                }
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
            // 保存抽水记录
            if (player.getCutMoney() != 0) {
                OseeCutMoneyLogEntity cutLog = new OseeCutMoneyLogEntity();
                cutLog.setUserId(user.getId());
                cutLog.setGame(GameEnum.FISHING.getId());
                cutLog.setCutMoney(player.getCutMoney());
                cutMoneyLogMapper.save(cutLog);
            }
            playerMapper.update(PlayerManager.getPlayerEntity(user));
        }

        FishingExitRoomResponse.Builder builder = FishingExitRoomResponse.newBuilder();
        builder.setPlayerId(user.getId());
        MyRefreshFishingUtil.sendRoomMessage(gameRoom, OseeMsgCode.S_C_OSEE_FISHING_EXIT_ROOM_RESPONSE_VALUE, builder);
        GameContainer.removeGamePlayer(gameRoom, user.getId(), false);
    }


    private int getPoint(String key, String playerId) {
        Double point = RedisUtil.zScore(key, playerId);
        return point == null ? 0 : point.intValue();
    }

    /**
     * 获取月积分
     *
     * @param playerId
     * @return
     */
    private int getPointMonth(long playerId) {
        return getPoint(PLAYER_CONFIG_RANK_MONTH_KEY, String.valueOf(playerId));
    }

    /**
     * 获取周积分
     *
     * @param playerId
     * @return
     */
    private int getPointWeek(long playerId) {
        return getPoint(PLAYER_CONFIG_RANK_WEEK_KEY, String.valueOf(playerId));
    }

    /**
     * 获取今日最高积分
     *
     * @param playerId
     * @return
     */
    private int getPointDay(long playerId) {
        return getPoint(PLAYER_CONFIG_RANK_DAY_KEY, String.valueOf(playerId));
    }

    /**
     * 获取幸运王者榜月积分
     *
     * @param playerId
     * @return
     */
    private int getGoldPointMonth(long playerId) {
        return getPoint(PLAYER_CONFIG_GOLD_RANK_MONTH_KEY, String.valueOf(playerId));
    }

    /**
     * 获取幸运王者榜周积分
     *
     * @param playerId
     * @return
     */
    private int getGoldPointWeek(long playerId) {
        return getPoint(PLAYER_CONFIG_GOLD_RANK_WEEK_KEY, String.valueOf(playerId));
    }

    /**
     * 获取幸运王者榜今日最高积分
     *
     * @param playerId
     * @return
     */
    private int getGoldPointDay(long playerId) {
        return getPoint(PLAYER_CONFIG_GOLD_RANK_DAY_KEY, String.valueOf(playerId));
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

        String key = null;
        if (rankType == 1) {
            key = PLAYER_CONFIG_RANK_DAY_KEY;
        } else if (rankType == 2) {
            key = PLAYER_CONFIG_RANK_WEEK_KEY;
        } else if (rankType == 3) {
            key = PLAYER_CONFIG_RANK_MONTH_KEY;
        } else if (rankType == 4) {
            key = PLAYER_CONFIG_RANK_LAST_DAY_KEY;
        } else if (rankType == 5) {
            key = PLAYER_CONFIG_RANK_LAST_WEEK_KEY;
        } else if (rankType == 6) {
            key = PLAYER_CONFIG_RANK_LAST_MONTH_KEY;
        }

        int start = (pageCurrent - 1) * pageSize;
        int end = start + pageSize - 1;

        PlayerRankResponse.Builder builder = PlayerRankResponse.newBuilder();
        builder.setRankType(rankType);

        RewardMessage.Builder builder1 = RewardMessage.newBuilder();
        Set<String> rankIdSet = RedisUtil.values(key, start, end);

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

            PlayerInfoMessage.Builder b = PlayerInfoMessage.newBuilder();
            b.setPlayerId(playerId);
            b.setDayPoint(getPoint(key, String.valueOf(playerId)));
            b.setWeekPoint(getPoint(key, String.valueOf(playerId)));
            b.setMonthPoint(getPoint(key, String.valueOf(playerId)));
            b.setRank(index);

            OseePlayerEntity oseePlayerEntity = PlayerManager.getPlayerEntity(user3);

            b.setName(user3.getNickname());
            b.setVipLevel(oseePlayerEntity.getVipLevel());

            if (rankType >= 4) {
                rankType -= 3;
            }

            AppRewardRankEntity rewardRank = appRewardRankMapper.findReward1(rankType, index);

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

        NetManager.sendMessage(OseeMsgCode.S_C_OSEE_GET_PLAYER_RANK_RESPONSE_VALUE, builder, user);

    }

    /**
     * 处理：榜单
     */
    public static Set<String> handleRankIdSet(int pageSize, String key, int start, int end, Set<String> rankIdSet) {

        boolean needAdd = false;

        int index = 0;
        for (String item : rankIdSet) {

            long playerId = Long.parseLong(item);

            ServerUser user3 = UserContainer.getUserById(playerId);

            if (user3 == null) {

                needAdd = true;
                continue;

            }

            index++;

        }

        if (needAdd) {

            rankIdSet = RedisUtil.values(key, start, end + pageSize); // 再多加一页的数据，防止数据量不够

        }

        return rankIdSet;

    }

    /**
     * 处理：奖励
     */
    private void handleRewardRank(RewardMessage.Builder builder1, AppRewardRankEntity rewardRank) {

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
     * 响应用户积分
     */
    public void point(int rankType, ServerUser user) {

        PlayerPointResponse.Builder builder = PlayerPointResponse.newBuilder();

        if (rankType == 1) {
            builder.setPoint(getPointDay(user.getId()));
        } else if (rankType == 2) {
            builder.setPoint(getPointWeek(user.getId()));
        } else if (rankType == 3) {
            builder.setPoint(getPointMonth(user.getId()));
        } else {
            builder.setPoint(0);
        }

        NetManager.sendMessage(OseeMsgCode.S_C_OSEE_GET_PLAYER_POINT_RESPONSE_VALUE, builder, user);
    }

    private static final List<PlayerLevelConfig> PLAYER_LEVEL_CONFIG_LIST =
            DataContainer.getDatas(PlayerLevelConfig.class);

    /**
     * 增加经验
     */
    public static void addExperience(ServerUser user, long experience) {

        OseePlayerEntity entity = PlayerManager.getPlayerEntity(user);

        synchronized (entity) {

            long exp = entity.getExperience() + experience;
            entity.setExperience(exp);

            while (entity.getExperience() > 0) {

                int level = entity.getLevel();

                PlayerLevelConfig cfg = null;

                for (PlayerLevelConfig config : PLAYER_LEVEL_CONFIG_LIST) {
                    if (config.getLevel() == level) {
                        cfg = config;
                        break;
                    }
                }

                if (cfg != null && cfg.getExp() <= entity.getExperience()) {

                    entity.setExperience(entity.getExperience() - cfg.getExp());
                    entity.setLevel(entity.getLevel() + 1);

                    // FishingLevelUpResponse.Builder builder = FishingLevelUpResponse.newBuilder();
                    // builder.setLevel(entity.getLevel());
                    //
                    // List<ItemData> rewards = cfg.getRealRewards();
                    // for (ItemData reward : rewards) {
                    // builder.addRewards(
                    // ItemDataProto.newBuilder().setItemId(reward.getItemId()).setItemNum(reward.getCount())
                    // .build());
                    // }
                    //
                    // // 给予奖励物品
                    // PlayerManager.addItems(user, rewards, ItemChangeReason.LEVEL_UP, true);
                    // NetManager.sendMessage(OseeMsgCode.S_C_OSEE_FISHING_LEVEL_UP_RESPONSE_VALUE, builder, user);

                } else {
                    break;
                }

            }
        }

    }

    /**
     * 房间特殊鱼倍数缓存((%s)房间ID Map->(鱼模型Id,倍数))
     */
    public static final String DYNAMIC_MULT_FISH_ROOM_CODE = "DYNAMIC_MULT_FISH_ROOM_CODE:%s";

    @Autowired
    private UserPropsManager userPropsManager;

    /**
     * 二次伤害鱼数据同步
     *
     * @param gameRoom
     * @param player
     * @param winMoney
     * @param builder
     */
    private void secondaryFishSendInfo(FishingGameRoom gameRoom, FishingGamePlayer player, long winMoney,
                                       FishingDoubleKillResponse.Builder builder) {
        // + ":" + builder.getModelId()
        final String key = "DOUBLE_KILL_WINMONEY" + player.getUser().getId();
        long killWinMoney = RedisUtil.val(key, 0D).longValue();
        RedisHelper.set(key, String.valueOf(winMoney + killWinMoney));
        MyRefreshFishingUtil.sendRoomMessage(gameRoom, OseeMsgCode.S_C_FISHING_DOUBLE_KILL_RESPONSE_VALUE, builder);
    }


    /**
     * 判断鱼是否被击中
     */
    private static boolean isHit(FishingGamePlayer player, int roomIndex, FishConfig config, long needMoney,
                                 long winMoney, long fireId) {
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

    public static void changePeak(ServerUser user, int roomIndex) {
        log.info("切换状态");
        double peak = RedisUtil.val("USER_PEAK" + user.getId(),
                new Long(PlayerManager.getPlayerEntity(user).getMoney()).doubleValue());// 当前平衡值
        double peakHis = RedisUtil.val("USER_PEAK_HIS" + user.getId(),
                new Long(PlayerManager.getPlayerEntity(user).getMoney()).doubleValue());// 上一段平衡值
        double peakMoney = RedisUtil.val("USER_PEAK_MONEY" + user.getId(),
                new Long(PlayerManager.getPlayerEntity(user).getMoney()).doubleValue());// 当前峰值
        if (RedisUtil.val("USER_PEAK_UP_OR_DOWN" + user.getId(), 1L) == 1) {// 递减
            if (peakMoney <= peak) {
                double sxxs = RedisUtil.val("sxxs", 0D);
                double xs = RedisUtil.val("xs", 0D);
                double sxhdz = RedisUtil.val("sxhdz", 0D);
                // 重新随平衡值
                peakHis = peak;
                int px = ThreadLocalRandom.current().nextInt(new Double(FishingUtil.PXMin[roomIndex]).intValue(),
                        new Double(FishingUtil.PXMax[roomIndex]).intValue() + 1);
                BigDecimal pxb = new BigDecimal(px * 0.01).setScale(3, BigDecimal.ROUND_HALF_UP);
                int fp = ThreadLocalRandom.current().nextInt(new Double(FishingUtil.FPMin[roomIndex]).intValue(),
                        new Double(FishingUtil.FPMax[roomIndex]).intValue() + 1);
                BigDecimal fpb = new BigDecimal(fp * 0.01).setScale(3, BigDecimal.ROUND_HALF_UP);
                peak = peak * (1 + pxb.doubleValue() + fpb.doubleValue());
                double peakMax = RedisUtil.val("USER_T_BANKRUPTCY_NUMBER" + user.getId(), 0D) * sxxs;// 当前平衡值上限
                if (peakMax >= sxhdz) {
                    peakMax = sxhdz;
                } else if (peakMax < xs) {
                    peakMax = xs;
                }
                if (peak > peakMax) {
                    for (int i = 0; i >= 0; i++) {
                        int px1 =
                                ThreadLocalRandom.current().nextInt(new Double(FishingUtil.PXMin[roomIndex]).intValue(),
                                        new Double(FishingUtil.PXMax[roomIndex]).intValue() + 1);
                        BigDecimal pxb1 = new BigDecimal(px1 * 0.01).setScale(3, BigDecimal.ROUND_HALF_UP);
                        int fp1 =
                                ThreadLocalRandom.current().nextInt(new Double(FishingUtil.FPMin[roomIndex]).intValue(),
                                        new Double(FishingUtil.FPMax[roomIndex]).intValue() + 1);
                        BigDecimal fpb1 = new BigDecimal(fp1 * 0.01).setScale(3, BigDecimal.ROUND_HALF_UP);
                        peak = peak * (1 + pxb1.doubleValue() + fpb1.doubleValue());
                        if (peak < peakMax) {
                            break;
                        }
                    }
                }
                // 重新随峰值
                if (peak > peakHis) {// 当前平衡值大于上一段平衡值
                    RedisHelper.set("USER_PEAK_MONEY_HIS" + user.getId(), String.valueOf(peakMoney));
                    peakMoney = ThreadLocalRandom.current().nextInt(new Double(peakHis).intValue(),
                            new Double(peak + 1).intValue());
                    RedisHelper.set("USER_PEAK_UP_OR_DOWN" + user.getId(), String.valueOf(2));
                } else {
                    RedisHelper.set("USER_PEAK_MONEY_HIS" + user.getId(), String.valueOf(peakMoney));
                    peakMoney = ThreadLocalRandom.current().nextInt(new Double(peak).intValue(),
                            new Double(peakHis + 1).intValue());
                    RedisHelper.set("USER_PEAK_UP_OR_DOWN" + user.getId(), String.valueOf(1));
                }
                RedisHelper.set("USER_PEAK" + user.getId(), String.valueOf(peak));
                RedisHelper.set("USER_PEAK_HIS" + user.getId(), String.valueOf(new Double(peakHis).longValue()));
                RedisHelper.set("USER_PEAK_MONEY" + user.getId(), String.valueOf(peakMoney));
            } else {
                double k = RedisUtil.val("k", 0D);
                if (peak > peakHis) {// 当前平衡值大于上一段平衡值
                    BigDecimal kb = new BigDecimal(k * 0.01).setScale(3, BigDecimal.ROUND_HALF_UP);
                    peakHis = peakHis * (1 + kb.doubleValue());
                    if (peakHis > peak) {
                        // peakHis = peak;
                        RedisHelper.set("USER_PEAK_MONEY_HIS" + user.getId(), String.valueOf(peakMoney));
                        peakMoney = ThreadLocalRandom.current().nextLong(new Double(peak).longValue(),
                                new Double(peakHis + 1).longValue());

                    } else {
                        RedisHelper.set("USER_PEAK_MONEY_HIS" + user.getId(), String.valueOf(peakMoney));
                        peakMoney = ThreadLocalRandom.current().nextLong(new Double(peakHis).longValue(),
                                new Double(peak + 1).longValue());
                    }
                    RedisHelper.set("USER_PEAK_UP_OR_DOWN" + user.getId(), String.valueOf(1));
                } else {
                    BigDecimal kb = new BigDecimal(k * 0.01).setScale(3, BigDecimal.ROUND_HALF_UP);
                    peakHis = peakHis * (1 - kb.doubleValue());
                    if (peakHis < peak) {
                        // peakHis = peak;
                        RedisHelper.set("USER_PEAK_MONEY_HIS" + user.getId(), String.valueOf(peakMoney));
                        peakMoney = ThreadLocalRandom.current().nextLong(new Double(peakHis).longValue(),
                                new Double(peak + 1).longValue());

                    } else {
                        RedisHelper.set("USER_PEAK_MONEY_HIS" + user.getId(), String.valueOf(peakMoney));
                        peakMoney = ThreadLocalRandom.current().nextLong(new Double(peak).longValue(),
                                new Double(peakHis + 1).longValue());
                    }
                    RedisHelper.set("USER_PEAK_UP_OR_DOWN" + user.getId(), String.valueOf(2));
                }
                RedisHelper.set("USER_PEAK" + user.getId(), String.valueOf(peak));
                RedisHelper.set("USER_PEAK_HIS" + user.getId(), String.valueOf(new Double(peakHis).longValue()));
                RedisHelper.set("USER_PEAK_MONEY" + user.getId(), String.valueOf(peakMoney));
            }
        } else {
            if (peakMoney >= peak) {

                double sxxs = RedisUtil.val("sxxs", 0D);
                double xs = RedisUtil.val("xs", 0D);
                double sxhdz = RedisUtil.val("sxhdz", 0D);
                // 重新随平衡值
                peakHis = peak;
                int px = ThreadLocalRandom.current().nextInt(new Double(FishingUtil.PXMin[roomIndex]).intValue(),
                        new Double(FishingUtil.PXMax[roomIndex]).intValue() + 1);
                BigDecimal pxb = new BigDecimal(px * 0.01).setScale(3, BigDecimal.ROUND_HALF_UP);
                int fp = ThreadLocalRandom.current().nextInt(new Double(FishingUtil.FPMin[roomIndex]).intValue(),
                        new Double(FishingUtil.FPMax[roomIndex]).intValue() + 1);
                BigDecimal fpb = new BigDecimal(fp * 0.01).setScale(3, BigDecimal.ROUND_HALF_UP);
                peak = peak * (1 + pxb.doubleValue() + fpb.doubleValue());
                double peakMax = RedisUtil.val("USER_T_BANKRUPTCY_NUMBER" + user.getId(), 0D) * sxxs;// 当前平衡值上限
                if (peakMax >= sxhdz) {
                    peakMax = sxhdz;
                } else if (peakMax < xs) {
                    peakMax = xs;
                }
                if (peak > peakMax) {
                    for (int i = 0; i >= 0; i++) {
                        int px1 =
                                ThreadLocalRandom.current().nextInt(new Double(FishingUtil.PXMin[roomIndex]).intValue(),
                                        new Double(FishingUtil.PXMax[roomIndex]).intValue() + 1);
                        BigDecimal pxb1 = new BigDecimal(px1 * 0.01).setScale(3, BigDecimal.ROUND_HALF_UP);
                        int fp1 =
                                ThreadLocalRandom.current().nextInt(new Double(FishingUtil.FPMin[roomIndex]).intValue(),
                                        new Double(FishingUtil.FPMax[roomIndex]).intValue() + 1);
                        BigDecimal fpb1 = new BigDecimal(fp1 * 0.01).setScale(3, BigDecimal.ROUND_HALF_UP);
                        peak = peak * (1 + pxb1.doubleValue() + fpb1.doubleValue());
                        if (peak < peakMax) {
                            break;
                        }
                    }
                }
                // 重新随峰值
                if (peak > peakHis) {// 当前平衡值大于上一段平衡值
                    RedisHelper.set("USER_PEAK_MONEY_HIS" + user.getId(), String.valueOf(peakMoney));
                    peakMoney = ThreadLocalRandom.current().nextLong(new Double(peakHis).longValue(),
                            new Double(peak + 1).longValue());
                    RedisHelper.set("USER_PEAK_UP_OR_DOWN" + user.getId(), String.valueOf(2));
                } else {
                    RedisHelper.set("USER_PEAK_MONEY_HIS" + user.getId(), String.valueOf(peakMoney));
                    peakMoney = ThreadLocalRandom.current().nextLong(new Double(peak).longValue(),
                            new Double(peakHis + 1).longValue());
                    RedisHelper.set("USER_PEAK_UP_OR_DOWN" + user.getId(), String.valueOf(1));
                }
                RedisHelper.set("USER_PEAK" + user.getId(), String.valueOf(peak));
                RedisHelper.set("USER_PEAK_HIS" + user.getId(), String.valueOf(new Double(peakHis).longValue()));
                RedisHelper.set("USER_PEAK_MONEY" + user.getId(), String.valueOf(peakMoney));
            } else {
                double k = RedisUtil.val("k", 0D);
                if (peak > peakHis) {// 当前平衡值大于上一段平衡值
                    BigDecimal kb = new BigDecimal(k * 0.01).setScale(3, BigDecimal.ROUND_HALF_UP);
                    peakHis = peakHis * (1 + kb.doubleValue());
                    if (peakHis > peak) {
                        // peakHis = peak;
                        RedisHelper.set("USER_PEAK_MONEY_HIS" + user.getId(), String.valueOf(peakMoney));
                        peakMoney = ThreadLocalRandom.current().nextLong(new Double(peak).longValue(),
                                new Double(peakHis + 1).longValue());

                    } else {
                        RedisHelper.set("USER_PEAK_MONEY_HIS" + user.getId(), String.valueOf(peakMoney));
                        peakMoney = ThreadLocalRandom.current().nextLong(new Double(peakHis).longValue(),
                                new Double(peak + 1).longValue());
                    }
                    RedisHelper.set("USER_PEAK_UP_OR_DOWN" + user.getId(), String.valueOf(1));
                } else {
                    BigDecimal kb = new BigDecimal(k * 0.01).setScale(3, BigDecimal.ROUND_HALF_UP);
                    peakHis = peakHis * (1 - kb.doubleValue());
                    if (peakHis < peak) {
                        // peakHis = peak;
                        RedisHelper.set("USER_PEAK_MONEY_HIS" + user.getId(), String.valueOf(peakMoney));
                        peakMoney = ThreadLocalRandom.current().nextLong(new Double(peakHis).longValue(),
                                new Double(peak + 1).longValue());

                    } else {
                        RedisHelper.set("USER_PEAK_MONEY_HIS" + user.getId(), String.valueOf(peakMoney));
                        peakMoney = ThreadLocalRandom.current().nextLong(new Double(peak).longValue(),
                                new Double(peakHis + 1).longValue());
                    }
                    RedisHelper.set("USER_PEAK_UP_OR_DOWN" + user.getId(), String.valueOf(2));
                }
                RedisHelper.set("USER_PEAK" + user.getId(), String.valueOf(peak));
                RedisHelper.set("USER_PEAK_HIS" + user.getId(), String.valueOf(new Double(peakHis).longValue()));
                RedisHelper.set("USER_PEAK_MONEY" + user.getId(), String.valueOf(peakMoney));
            }
        }
    }

    // public static void joinchangePeak(ServerUser user, int roomIndex) {
    // log.info("房间切换状态");
    // double peak = RedisUtil.val("USER_PEAK" + user.getId(), 0D);
    // double peakHis = RedisUtil.val("USER_PEAK_HIS" + user.getId(), 0D);
    // double peakMoney = RedisUtil.val("USER_PEAK_MONEY" + user.getId(), 0D);//当前峰值
    // double sxxs = RedisUtil.val("sxxs", 0D);
    // double xs = RedisUtil.val("xs", 0D);
    // double sxhdz = RedisUtil.val("sxhdz", 0D);
    // double peakMax = RedisUtil.val("USER_T_BANKRUPTCY_NUMBER" + user.getId(), 0D) * sxxs;//当前平衡值上限
    // if (peakMax >= sxhdz) {
    // peakMax = sxhdz;
    // } else if (peakMax < xs) {
    // peakMax = xs;
    // }
    // BigDecimal peakb = new BigDecimal(peakMax * 0.8).setScale(3, BigDecimal.ROUND_HALF_UP);
    // peak = peakb.longValue();
    // //重新随平衡值
    // peakHis = peak;
    // int px = ThreadLocalRandom.current().nextInt(new Double(FishingUtil.PXMin[roomIndex]).intValue(),
    // new Double(FishingUtil.PXMax[roomIndex]).intValue() + 1);
    // BigDecimal pxb = new BigDecimal(px * 0.01).setScale(3, BigDecimal.ROUND_HALF_UP);
    // int fp = ThreadLocalRandom.current().nextInt(new Double(FishingUtil.FPMin[roomIndex]).intValue(),
    // new Double(FishingUtil.FPMax[roomIndex]).intValue() + 1);
    // BigDecimal fpb = new BigDecimal(fp * 0.01).setScale(3, BigDecimal.ROUND_HALF_UP);
    // peak = peak * (1 + pxb.doubleValue() + fpb.doubleValue());
    // if (peak > peakMax) {
    // for (int i = 0; i >= 0; i++) {
    // int px1 = ThreadLocalRandom.current().nextInt(new Double(FishingUtil.PXMin[roomIndex]).intValue(),
    // new Double(FishingUtil.PXMax[roomIndex]).intValue() + 1);
    // BigDecimal pxb1 = new BigDecimal(px1 * 0.01).setScale(3, BigDecimal.ROUND_HALF_UP);
    // int fp1 = ThreadLocalRandom.current().nextInt(new Double(FishingUtil.FPMin[roomIndex]).intValue(),
    // new Double(FishingUtil.FPMax[roomIndex]).intValue() + 1);
    // BigDecimal fpb1 = new BigDecimal(fp1 * 0.01).setScale(3, BigDecimal.ROUND_HALF_UP);
    // peak = peak * (1 + pxb1.doubleValue() + fpb1.doubleValue());
    // if (peak < peakMax) {
    // break;
    // }
    // }
    // }
    // log.info("new Double(peak).intValue()" + new Double(peak).intValue());
    // log.info("new Double(peakHis).intValue()" + new Double(peakHis).intValue());
    // long peakInt = new Double(peak).longValue();
    // long peakHisInt = new Double(peakHis).longValue();
    // //重新随峰值
    // if (peakInt > peakHisInt) {//当前平衡值大于上一段平衡值
    // RedisHelper.set("USER_PEAK_MONEY_HIS" + user.getId(), String.valueOf(peakMoney));
    // peakMoney = ThreadLocalRandom.current().nextLong(peakHisInt, peakInt + 1);
    // RedisHelper.set("USER_PEAK_UP_OR_DOWN" + user.getId(), String.valueOf(2));
    // } else if (peakInt < peakHisInt) {
    // RedisHelper.set("USER_PEAK_MONEY_HIS" + user.getId(), String.valueOf(peakMoney));
    // peakMoney = ThreadLocalRandom.current().nextLong(peakInt, peakHisInt + 1);
    // RedisHelper.set("USER_PEAK_UP_OR_DOWN" + user.getId(), String.valueOf(1));
    // } else {
    // peakMoney = peakInt;
    // }
    // RedisHelper.set("USER_PEAK" + user.getId(), String.valueOf(peak));
    // RedisHelper.set("USER_PEAK_HIS" + user.getId(), String.valueOf(new Double(peakHis).longValue()));
    // RedisHelper.set("USER_PEAK_MONEY" + user.getId(), String.valueOf(peakMoney));
    // }

    @Autowired
    private UserStatusManager userStatusManager;

    /**
     * 创建玩家信息结构
     */
    private FishingPlayerInfoProto createPlayerInfoProto(FishingGamePlayer gamePlayer, FishingGameRoom gameRoom) {
        FishingPlayerInfoProto.Builder builder = FishingPlayerInfoProto.newBuilder();
        builder.setPlayerId(gamePlayer.getId());
        builder.setDiamond(String.valueOf(gamePlayer.getDragonCrystal()));
        builder.setName(gamePlayer.getUser().getNickname());
        builder.setHeadIndex(gamePlayer.getUser().getEntity().getHeadIndex());
        builder.setHeadUrl(gamePlayer.getUser().getEntity().getHeadUrl());
        builder.setSex(gamePlayer.getUser().getEntity().getSex());
        builder.setMoney(gamePlayer.getMoney());
        builder.setSeat(gamePlayer.getSeat());
        builder.setOnline(gamePlayer.getUser().isOnline());
        builder.setVipLevel(gamePlayer.getVipLevel());
        builder.setBatteryLevel((int) gamePlayer.getBatteryLevel());
        builder.setBatteryMult(gamePlayer.getBatteryMult());
        builder.setLevel(gamePlayer.getLevel());

        // builder.setViewIndex(gamePlayer.getViewIndex());
        final Object userStatusMap = userStatusManager.getUserStatusMap(gamePlayer.getId(), "batter:view", gameRoom);
        if (userStatusMap != null) {
            builder.setViewIndex(Integer.valueOf("" + userStatusMap));
        }
        final Object userStatusMap1 = userStatusManager.getUserStatusMap(gamePlayer.getId(), "wing:view", gameRoom);
        if (userStatusMap1 != null) {
            builder.setWingIndex(Integer.valueOf("" + userStatusMap1));
        }
        return builder.build();
    }

    /**
     * 创建玩家信息
     */
    public FishingPlayerInfoResponse.Builder createPlayerInfoResponse(FishingGameRoom gameRoom,
                                                                      FishingGamePlayer player) {
        FishingPlayerInfoResponse.Builder builder = FishingPlayerInfoResponse.newBuilder();
        builder.setPlayerInfo(createPlayerInfoProto(player, gameRoom));
        return builder;
    }

    /**
     * 发送玩家房间消息
     */
    private void sendJoinRoomResposne(FishingGameRoom gameRoom, ServerUser user) {
        FishingJoinRoomResponse.Builder builder = FishingJoinRoomResponse.newBuilder();
        builder.setRoomIndex(gameRoom.getRoomIndex());
        NetManager.sendMessage(OseeMsgCode.S_C_OSEE_FISHING_JOIN_ROOM_RESPONSE_VALUE, builder, user);

    }

    /**
     * 发送玩家列表消息
     */
    public void sendPlayersInfoResponse(FishingGameRoom gameRoom, ServerUser user) {
        FishingPlayersInfoResponse.Builder builder = FishingPlayersInfoResponse.newBuilder();
        for (BaseGamePlayer gamePlayer : gameRoom.getGamePlayers()) {
            if (gamePlayer != null) {
                // log.info("玩家信息："+gamePlayer.getSeat()+"------"+gamePlayer.getUser().getEntity().getNickname());
                builder.addPlayerInfos(createPlayerInfoProto((FishingGamePlayer) gamePlayer, gameRoom));
            }
        }
        MyRefreshFishingUtil.sendRoomMessage(gameRoom, OseeMsgCode.S_C_OSEE_FISHING_PLAYERS_INFO_RESPONSE_VALUE,
                builder);
        // NetManager.sendMessage(OseeMsgCode.S_C_OSEE_FISHING_PLAYERS_INFO_RESPONSE_VALUE, builder, user);
    }

    /**
     * 玩家加入指定房间
     */
    public void fishingJoinRoomByRoomCode(ServerUser user, int roomCode) {
        log.info("加入指定房间：" + user + " roomCode:" + roomCode + "");
        BaseGameRoom gameRoom = GameContainer.getGameRoomByCode(roomCode);
        if (!(gameRoom instanceof FishingGameRoom)) {
            NetManager.sendErrorMessageToClient("房间不存在", user);
            return;
        }
        FishingGameRoom room = (FishingGameRoom) gameRoom;
        if (!PlayerManager.checkItem(user, ItemId.MONEY, enterLimit[room.getRoomIndex() - 1])) {
            NetManager.sendHintMessageToClient("携带金币不足，无法进入该房间", user);
            return;
        }
        if (PlayerManager.getPlayerVipLevel(user) < room.getVipLevel()) {
            NetManager.sendHintMessageToClient("vip等级不足" + room.getVipLevel() + "，无法进入该房间", user);
            return;
        }
        if (room.getPlayerSize() >= room.getMaxSize()) {
            NetManager.sendErrorMessageToClient("房间人数已满", user);
            return;
        }
        // 加入房间
        joinFishingRoom(user, room);
    }

    /**
     * 发送同步鱼消息
     */
    public void sendSynchroniseResponse(FishingGameRoom gameRoom, ServerUser user) {
        FishingSynchroniseResponse.Builder builder = FishingSynchroniseResponse.newBuilder();
        for (FishStruct fish : gameRoom.getFishMap().values()) {
            builder.addFishInfos(MyRefreshFishingHelper.createFishInfoProtoForGeneral(fish));
        }
        NetManager.sendMessage(OseeMsgCode.S_C_OSEE_FISHING_SYNCHRONISE_RESPONSE_VALUE, builder, user);
    }

    /**
     * 向玩家发送房间当前的冰冻消息
     */
    public void sendFrozenMessage(FishingGameRoom gameRoom, ServerUser user) {

        // long nowTime = System.currentTimeMillis();
        // if (nowTime - gameRoom.getLastRoomFrozenTime() < SKILL_FROZEN_TIME) { // 房间处于冰冻状态
        // FishingUseSkillResponse.Builder builder = FishingUseSkillResponse.newBuilder();
        // builder.setSkillId(ItemId.SKILL_FROZEN.getId()); // 冰冻
        // builder.setDuration((int)((SKILL_FROZEN_TIME - (nowTime - gameRoom.getLastRoomFrozenTime())) / 1000));
        // NetManager.sendMessage(OseeMsgCode.S_C_OSEE_FISHING_USE_SKILL_RESPONSE_VALUE, builder, user);
        // }

        FishingHelper.sendFrozenMessage(gameRoom, user);

    }

    /**
     * 发送电磁
     */
    public void useEle(long fishId, FishingGameRoom gameRoom, ServerUser user) {
        OseePublicData.UseEleResponse.Builder builder = OseePublicData.UseEleResponse.newBuilder();
        for (BaseGamePlayer gamePlayer : gameRoom.getGamePlayers()) {
            if (gamePlayer != null) {
                ServerUser user1 = UserContainer.getUserById(gamePlayer.getId());
                builder.setFishId(fishId);
                builder.setUserId(user.getId());
                NetManager.sendMessage(OseeMsgCode.S_C_USE_ELE_RESPONSE_VALUE, builder, user1);
            }
        }
    }

    /**
     * 发送黑洞炮使用
     */
    public void useBlack(float x, float y, FishingGameRoom gameRoom, ServerUser user) {
        OseePublicData.UseBlackResponse.Builder builder = OseePublicData.UseBlackResponse.newBuilder();
        for (BaseGamePlayer gamePlayer : gameRoom.getGamePlayers()) {
            if (gamePlayer != null) {
                ServerUser user1 = UserContainer.getUserById(gamePlayer.getId());
                builder.setX(x);
                builder.setY(y);
                builder.setUserId(user.getId());
                NetManager.sendMessage(OseeMsgCode.S_C_USE_BLACK_RESPONSE_VALUE, builder, user1);
            }
        }
    }

    /**
     * 发送黑洞炮使用
     */
    public void useTro(float x, float y, FishingGameRoom gameRoom, ServerUser user) {
        OseePublicData.UseTroResponse.Builder builder = OseePublicData.UseTroResponse.newBuilder();
        for (BaseGamePlayer gamePlayer : gameRoom.getGamePlayers()) {
            if (gamePlayer != null) {
                ServerUser user1 = UserContainer.getUserById(gamePlayer.getId());
                builder.setX(x);
                builder.setY(y);
                builder.setUserId(user.getId());
                NetManager.sendMessage(OseeMsgCode.S_C_USE_TRO_RESPONSE_VALUE, builder, user1);
            }
        }
    }

    /**
     * 发送钻头使用
     */
    public void useBit(float angle, FishingGameRoom gameRoom, ServerUser user) {
        OseePublicData.UseBitResponse.Builder builder = OseePublicData.UseBitResponse.newBuilder();
        for (BaseGamePlayer gamePlayer : gameRoom.getGamePlayers()) {
            if (gamePlayer != null) {
                ServerUser user1 = UserContainer.getUserById(gamePlayer.getId());
                builder.setAngle(angle);
                builder.setUserId(user.getId());
                NetManager.sendMessage(OseeMsgCode.S_C_USE_BIT_RESPONSE_VALUE, builder, user1);
            }
        }
    }

    /**
     * 二次伤害鱼捕获鱼
     */
    public void doubleKillFishs(FishingGameRoom gameRoom, long playerId, List<Long> fishIdsList) {
        FishingDoubleKillFishResponse.Builder builder = FishingDoubleKillFishResponse.newBuilder();
        builder.setUserId(playerId);
        for (Long fishId : fishIdsList) {
            // 鱼id不存在
            if (!gameRoom.getFishMap().containsKey(fishId)) {
                continue;
            }
            gameRoom.getFishMap().remove(fishId);
//            gameRoom.removeFishMap(fishId);
            builder.addFishds(fishId);
            FishingFightFishResponse.Builder builder1 = FishingFightFishResponse.newBuilder();
            builder1.setFishId(fishId);
            builder1.setPlayerId(playerId);
            builder1.setRestMoney(PlayerManager.getPlayerMoney(UserContainer.getUserById(playerId)));
            builder1.setDropMoney(0);
            builder1.setType(0);
            MyRefreshFishingUtil.sendRoomMessage(gameRoom, OseeMsgCode.S_C_OSEE_FISHING_FIGHT_FISH_RESPONSE_VALUE,
                    builder1);

        }
        MyRefreshFishingUtil.sendRoomMessage(gameRoom, OseeMsgCode.S_C_FISHING_DOUBLE_KILL_FISH_RESPONSE_VALUE,
                builder);
    }

    /**
     * 二次伤害鱼结束
     */
    public void doubleKillEnd(FishingGameRoom gameRoom, FishingGamePlayer player, Long winMoney, long mult,
                              String fishName) {
        FishingDoubleKillEndResponse.Builder builder = FishingDoubleKillEndResponse.newBuilder();
        long doubleKillWinMoney =
                new Double(RedisUtil.val("DOUBLE_KILL_WINMONEY" + player.getUser().getId(), 0D)).longValue();
        if (doubleKillWinMoney <= 0) {
            return;
        }
        PlayerManager.addItem(player.getUser(), ItemId.MONEY, winMoney, ItemChangeReason.FISHING_RESULT, true);
        if (mult > 1000 && winMoney > 20000000) {
            // 进行全服通报
            CatchBossFishResponse.Builder builder1 = CatchBossFishResponse.newBuilder();
            builder1.setFishName(fishName);
            builder1.setMoney(winMoney);
            builder1.setPlayerName(player.getUser().getNickname().substring(0, 2) + "***");
            builder1.setPlayerVipLevel(player.getVipLevel());
            builder1.setBatteryLevel(new Long(mult).intValue());
            sendCatchBossFishResponse(builder1);
        }
        RedisHelper.set("DOUBLE_KILL_WINMONEY" + player.getUser().getId(), "0");
        builder.setUserId(player.getUser().getId());
        builder.setWinMoney(winMoney);
        NetManager.sendMessage(OseeMsgCode.S_C_FISHING_DOUBLE_KILL_END_RESPONSE_VALUE, builder, player.getUser());
        // MyRefreshFishingUtil.sendRoomMessage(gameRoom,OseeMsgCode.S_C_FISHING_DOUBLE_KILL_FISH_RESPONSE_VALUE,builder.build());
    }

    /**
     * 发送玩家捕获boss鱼响应到全服
     */
    public static void sendCatchBossFishResponse(CatchBossFishResponse.Builder response) {
        ThreadPoolUtils.TASK_SERVICE_POOL.schedule(() -> {
            // 只发送到全服在捕鱼房间内的玩家
            List<FishingGameRoom> fishingGameRooms = GameContainer.getGameRooms(FishingGameRoom.class);
            for (FishingGameRoom gameRoom : fishingGameRooms) {
                if (gameRoom != null) {
                    MyRefreshFishingUtil.sendRoomMessage(gameRoom, OseeMsgCode.S_C_TTMY_CATCH_BOSS_FISH_RESPONSE_VALUE,
                            response);
                }
            }
        }, 0, TimeUnit.SECONDS);
    }

    /**
     * 从房间创建开始就定时执行刷鱼逻辑
     */
    @Override
    protected void doFishingRoomTask0(BaseFishingRoom fishingRoom) {

        if (!(fishingRoom instanceof FishingGameRoom)) {
            return;
        }

        long nowTime = System.currentTimeMillis();
        FishingGameRoom gameRoom = (FishingGameRoom) fishingRoom;

        // 获取：房间内的刷鱼规则
        MyRefreshFishingHelper.getRoomRefreshRule(gameRoom);
        // 检查并执行刷鱼
        MyRefreshFishingHelper.checkAndRefresh(gameRoom);


        // 判断过期鱼，并从鱼表内移除
        List<Long> removeKey = new LinkedList<>();
        for (FishStruct fish : gameRoom.getFishMap().values()) {

            long maxLifeTime =
                    Math.round(fish.getLifeTime() > 0 ? fish.getLifeTime() : FishingManager.DEFAULT_LIFE_TIME);

            // 这里不用加冰冻时间，因为：fish.getLifeTime()，里面加了冰冻时间的
            if (maxLifeTime + fish.getCreateTime() < nowTime) {
                removeKey.add(fish.getId());
                long numm = RedisUtil.val("FISHING_GAME_GOLD_FISH_NUM2" + gameRoom.getCode(), 0L);
                double ruleId = MyRefreshFishingHelper.getRoomGoldFishRuleId(gameRoom); // 获取当前场次的黄金鱼刷新规则
                if (ruleId == fish.getRuleId()) {
                    numm -= 1;
                    if (numm < 0) {
                        numm = 0;
                    }
                    RedisHelper.set("FISHING_GAME_GOLD_FISH_NUM2" + gameRoom.getCode(), String.valueOf(numm));
                }

                MyRefreshFishingHelper.checkAndDurationRefreshFish(gameRoom, fish, false);
            }
        }

        for (long key : removeKey) {
            gameRoom.getFishMap().remove(key);
//            gameRoom.removeFishMap(key);
        }

    }

    /**
     * 房间内玩家使用技能
     */
    public void useSkill(FishingGameRoom gameRoom, FishingGamePlayer player, int skillId, long routeId) {

        // FishingHelper.useSkill(gameRoom, player, skillId, routeId);

        // player.setLastFireTime(System.currentTimeMillis());
        //
        // ServerUser user = player.getUser();
        // // 技能id有误
        // if (!skillIds.contains(skillId)) {
        // NetManager.sendHintBoxMessageToClient("禁止使用该道具", user, 10);
        // return;
        // }
        //
        // // 技能数量不足
        // if (!PlayerManager.checkItem(user, skillId, 1)) {
        // NetManager.sendHintBoxMessageToClient("道具数量不足", user, 10);
        // return;
        // }
        //
        // FishingUseSkillResponse.Builder builder = FishingUseSkillResponse.newBuilder();
        // builder.setSkillId(skillId);
        // builder.setPlayerId(player.getId());
        //
        // long nowTime = System.currentTimeMillis();
        // if (skillId == ItemId.SKILL_LOCK.getId()) { // 锁定
        // if (nowTime - player.getLastLockTime() < SKILL_LOCK_TIME) {
        // NetManager.sendHintBoxMessageToClient("技能冷却中", user, 10);
        // return;
        // }
        // builder.setDuration((int)(SKILL_LOCK_TIME / 1000));
        // player.setLastLockTime(nowTime);
        // } else if (skillId == ItemId.SKILL_FROZEN.getId()) { // 冰冻
        // int vipLevel = PlayerManager.getPlayerVipLevel(user);
        // if (vipLevel < 4) {
        // NetManager.sendHintMessageToClient("VIP4才可以使用该技能", user);
        // return;
        // }
        // if (nowTime - player.getLastFrozenTime() < SKILL_FROZEN_TIME) {
        // NetManager.sendHintBoxMessageToClient("技能冷却中", user, 10);
        // return;
        // }
        // if (nowTime - gameRoom.getLastRoomFrozenTime() < SKILL_FROZEN_TIME) {
        // NetManager.sendHintBoxMessageToClient("房间处于冰冻中", user, 10);
        // return;
        // }
        // builder.setDuration((int)(SKILL_FROZEN_TIME / 1000));
        // player.setLastFrozenTime(nowTime);
        // gameRoom.setLastRoomFrozenTime(nowTime);
        //
        // for (Entry<FishRefreshRule, Long> refresh : gameRoom.getNextRefreshTime().entrySet()) {
        // refresh.setValue(refresh.getValue() + SKILL_FROZEN_TIME / 1000); // 延迟冰冻持续时间段刷鱼
        // }
        //
        // long addTime = nowTime / 1000 - gameRoom.getLastRoomFrozenTime() / 1000;
        // long skillTime = SKILL_FROZEN_TIME / 1000;
        // addTime = Math.min(addTime, skillTime);
        //
        // for (FishStruct fish : gameRoom.getFishMap().values()) {
        // fish.setLifeTime(fish.getLifeTime() + addTime); // 延长鱼的存在时间
        // fish.setNowLifeTime(fish.getClientLifeTime()); // 记录冰冻时鱼的存活时间
        // fish.setFTime(fish.getFTime() + (addTime) * 1000);
        // }
        // // 延迟鱼潮刷新时间 秒
        // gameRoom.setNextFishTideTime(gameRoom.getNextFishTideTime() + addTime);
        //
        // player.setLastFrozenTime(nowTime);
        // gameRoom.setLastRoomFrozenTime(nowTime);
        // } else if (skillId == ItemId.SKILL_FAST.getId()) { // 急速
        // if (nowTime - player.getLastFastTime() < SKILL_FAST_TIME) {
        // NetManager.sendHintBoxMessageToClient("技能冷却中", user, 10);
        // return;
        // }
        // int vipLevel = PlayerManager.getPlayerVipLevel(user);
        // if (vipLevel < 4) {
        // NetManager.sendHintMessageToClient("VIP4才可以使用该技能", user);
        // return;
        // }
        // builder.setDuration((int)(SKILL_FAST_TIME / 1000));
        // player.setLastFastTime(nowTime);
        // } else if (skillId == ItemId.SKILL_ELETIC.getId()) { // 电磁炮
        // int vipLevel = PlayerManager.getPlayerVipLevel(user);
        // if (nowTime - player.getLastElectromagneticTime() < SKILL_ELETIC_TIME) {
        // NetManager.sendHintBoxMessageToClient("技能冷却中", user, 10);
        // return;
        // }
        // builder.setDuration((int)(SKILL_ELETIC_TIME / 1000));
        // player.setLastElectromagneticTime(nowTime);
        // } else if (skillId == ItemId.SKILL_BLACK_HOLE.getId()) { // 黑洞炮
        // int vipLevel = PlayerManager.getPlayerVipLevel(user);
        // if (vipLevel < 4) {
        // NetManager.sendHintMessageToClient("VIP才可以使用黑洞炮技能", user);
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
        // NetManager.sendHintMessageToClient("VIP才可以使用鱼雷炮技能", user);
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
        // NetManager.sendHintMessageToClient("VIP才可以使用钻头技能", user);
        // return;
        // }
        // if (nowTime - player.getLastBitTime() < SKILL_BIT_TIME) {
        // NetManager.sendHintBoxMessageToClient("技能冷却中", user, 10);
        // return;
        // }
        // builder.setDuration((int)(SKILL_BIT_TIME / 1000));
        // player.setLastBitTime(nowTime);
        // } else if (skillId == ItemId.SKILL_CRIT.getId()) { // 暴击
        // int vipLevel = PlayerManager.getPlayerVipLevel(user);
        // if (vipLevel < 8) {
        // NetManager.sendHintMessageToClient("VIP8才可以使用该技能", user);
        // return;
        // }
        // if (nowTime - player.getLastCritTime() < SKILL_CRIT_TIME) {
        // NetManager.sendHintBoxMessageToClient("技能冷却中", user, 10);
        // return;
        // }
        // builder.setDuration((int)(SKILL_CRIT_TIME / 1000));
        // player.setLastCritTime(nowTime);
        // RedisHelper.set("USER_CRIT_MULT" + user.getId(), String.valueOf(2));
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
        // } else if (skillId == ItemId.MAGIC_LAMP.getId()) {// 神灯道具
        // int vipLevel = PlayerManager.getPlayerVipLevel(user);
        // if (vipLevel < 8) {
        // NetManager.sendHintMessageToClient("VIP8才可以使用该技能", user);
        // return;
        // }
        // // if (nowTime - player.getLastFrozenTime() < SKILL_FROZEN_TIME) {
        // // NetManager.sendHintMessageToClient("冰冻期间无法使用神灯！", user);
        // // return;
        // // }
        // long num = RedisUtil.val("FISHING_GAME_GOLD_FISH_NUM2" + gameRoom.getCode(), 0L);
        // if (num >= 5) {
        // NetManager.sendHintMessageToClient("房间黄金鱼已达上限！", user);
        // return;
        // }
        //
        // MyRefreshFishingHelper.magicLampRefreshFish(gameRoom, routeId);
        //
        // num += 1;
        // RedisHelper.set("FISHING_GAME_GOLD_FISH_NUM2" + gameRoom.getCode(), String.valueOf(num));
        //
        // builder.setSkillFishId(routeId);
        // }
        //
        // builder.setRestMoney(player.getMoney());
        // MyRefreshFishingUtil.sendRoomMessage(gameRoom, OseeMsgCode.S_C_OSEE_FISHING_USE_SKILL_RESPONSE_VALUE,
        // builder);

        // // 扣除使用的技能数量
        // PlayerManager.addItem(user, skillId, -1, ItemChangeReason.USE_ITEM, true);
        // // 做任务 使用任意道具
        // FishingTaskManager.doTask(user, TaskType.DAILY, GoalType.USE_ITEM, 0, 1);
        // // 房间任务
        // FishingTaskManager.doTask(user, TaskType.ROOM, GoalType.USE_ITEM, skillId, 1);

    }

    /**
     * 发送同步锁定
     */
    public void sendSyncLockResponse(FishingSyncLockResponse.Builder response, FishingGameRoom gameRoom) {
        if (gameRoom != null) {
            MyRefreshFishingUtil.sendRoomMessage(gameRoom, OseeMsgCode.S_C_FISHING_SYNC_LOCK_RESPONSE_VALUE, response);
        }
    }


    /**
     * 查询捕鱼记录请求
     */
    public void fishingRecord(TtmyFishingRecordProto.FishingRecordResponse request, ServerUser user) {

        StrBuilder where = StrBuilder.create();

        where.append("where log.player_id = ").append(user.getId());

        StrBuilder page = StrBuilder.create();

        page.append("limit 30");

        List<OseeFishingRecordLogEntity> logList = fishingRecordLogMapper.getLogList(where.toString(), page.toString());

        TtmyFishingRecordProto.FishingRecordResponse.Builder builder =
                TtmyFishingRecordProto.FishingRecordResponse.newBuilder();

        // 组装数据
        for (OseeFishingRecordLogEntity item : logList) {
            TtmyFishingRecordProto.FishingRecord.Builder recordBuilder =
                    TtmyFishingRecordProto.FishingRecord.newBuilder();

            recordBuilder.setSceneName(MyRefreshFishingUtil.getSceneName(item.getRoomIndex()));
            recordBuilder.setJoinTime(DateUtil.formatDateTime(item.getJoinTime()));
            recordBuilder.setExitTime(DateUtil.formatDateTime(item.getExitTime()));
            recordBuilder.setMoneyChange(item.getWinMoney() - item.getSpendMoney());

            builder.addFishingRecordList(recordBuilder);

        }

        NetManager.sendMessage(OseeMessage.OseeMsgCode.S_C_OSEE_FISHING_RECORD_RESPONSE_VALUE, builder, user);

    }

}
