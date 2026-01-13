package com.maple.game.osee.controller.gm;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.func.VoidFunc0;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.text.StrBuilder;
import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.DigestUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import com.maple.common.lobby.manager.LobbyManager;
import com.maple.common.lobby.manager.WanderSubtitleManager;
import com.maple.common.login.manager.LoginManager;
import com.maple.common.login.proto.LoginMessage.LoginMsgCode;
import com.maple.common.login.proto.LoginMessage.LogoutResponse;
import com.maple.database.config.redis.RedisHelper;
import com.maple.database.data.DbEntity;
import com.maple.database.data.entity.UserEntity;
import com.maple.database.data.entity.WanderSubtitleEntity;
import com.maple.database.data.mapper.UserAuthenticationMapper;
import com.maple.database.data.mapper.UserMapper;
import com.maple.database.data.mapper.WanderSubtitleMapper;
import com.maple.database.util.RedissonUtil;
import com.maple.engine.anotation.GmController;
import com.maple.engine.anotation.GmHandler;
import com.maple.engine.container.DataContainer;
import com.maple.engine.container.UserContainer;
import com.maple.engine.data.ServerUser;
import com.maple.engine.manager.UserManager;
import com.maple.engine.utils.JsonMapUtils;
import com.maple.engine.utils.JsonMapUtils.JsonInnerType;
import com.maple.game.osee.common.RedisUtil;
import com.maple.game.osee.controller.gm.base.GmBaseController;
import com.maple.game.osee.dao.data.entity.*;
import com.maple.game.osee.dao.data.entity.gm.GmAuthenticationInfo;
import com.maple.game.osee.dao.data.entity.gm.GmCdkInfo;
import com.maple.game.osee.dao.data.entity.gm.GmCdkTypeInfo;
import com.maple.game.osee.dao.data.mapper.*;
import com.maple.game.osee.dao.data.mapper.gm.GmCommonMapper;
import com.maple.game.osee.dao.log.entity.*;
import com.maple.game.osee.dao.log.mapper.*;
import com.maple.game.osee.entity.*;
import com.maple.game.osee.entity.fishing.challenge.FishJc;
import com.maple.game.osee.entity.fishing.challenge.FishingChallengePlayer;
import com.maple.game.osee.entity.fishing.challenge.FishingChallengeRoom;
import com.maple.game.osee.entity.fishing.csv.file.FishConfig;
import com.maple.game.osee.entity.gm.AgentPlayerAll;
import com.maple.game.osee.entity.gm.ChangeAll;
import com.maple.game.osee.entity.gm.CommonResponse;
import com.maple.game.osee.manager.MessageManager;
import com.maple.game.osee.manager.PlayerManager;
import com.maple.game.osee.manager.fishing.FishingChallengeManager;
import com.maple.game.osee.manager.huiwei.LoginSignManager;
import com.maple.game.osee.manager.lobby.CdkManager;
import com.maple.game.osee.manager.lobby.CommonLobbyManager;
import com.maple.game.osee.manager.lobby.ShoppingManager;
import com.maple.game.osee.proto.OseeMessage;
import com.maple.game.osee.proto.OseePublicData;
import com.maple.game.osee.proto.lobby.OseeLobbyMessage;
import com.maple.game.osee.service.GmCommonService;
import com.maple.game.osee.util.FishingChallengeFightFishUtil;
import com.maple.game.osee.util.FishingChallengeUtil;
import com.maple.game.osee.util.GameUtil;
import com.maple.gamebase.container.GameContainer;
import com.maple.gamebase.data.BaseGamePlayer;
import com.maple.gamebase.data.BaseGameRoom;
import com.maple.network.manager.NetManager;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.redisson.api.RAtomicDouble;
import org.redisson.api.RBucket;
import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;
import org.redisson.codec.JsonJacksonCodec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.maple.game.osee.util.FishingChallengeFightFishUtil.getKsFzByYkType;

/**
 * 后台基础控制器
 */
@GmController
@Slf4j
public class GmCommonController extends GmBaseController {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private OseePlayerMapper playerMapper;

    @Autowired
    private GmCommonMapper gmCommonMapper;

    @Autowired
    private UserAuthenticationMapper authenticationMapper;

    @Autowired
    private OseeNoticeMapper noticeMapper;

    @Autowired
    private WanderSubtitleMapper subtitleMapper;

    @Autowired
    private OseeLotteryShopMapper lotteryShopMapper;

    @Autowired
    private OseeRealLotteryLogMapper realLotteryMapper;

    @Autowired
    private OseeUnrealLotteryLogMapper unrealLotteryMapper;

    @Autowired
    private OseeRechargeLogMapper rechargeLogMapper;

    @Autowired
    private OseePlayerTenureLogMapper tenureLogMapper;

    @Autowired
    private OseeCutMoneyLogMapper cutMoneyLogMapper;

    @Autowired
    private OseeExpendLogMapper expendLogMapper;

    @Autowired
    private OseeGobangRecordLogMapper gobangRecordMapper;

    @Autowired
    private OseeFruitRecordLogMapper fruitRecordMapper;

    @Autowired
    private OseeFighttenRecordLogMapper fighttenRecordMapper;

    @Autowired
    private OseeFishingRecordLogMapper fishingRecordMapper;

    @Autowired
    private LobbyManager lobbyManager;

    @Autowired
    private CommonLobbyManager commonLobbyManager;

    @Autowired
    private CdkManager cdkManager;

    @Autowired
    private WanderSubtitleManager subtitleManager;

    @Autowired
    private ShoppingManager shoppingManager;

    @Autowired
    private PlayerManager playerManager;

    @Autowired
    private AgentMapper agentMapper;

    @Autowired
    private MessageManager messageManager;

    @Autowired
    private CrystalExchangeLogMapper crystalExchangeLogMapper;

    @Autowired
    private StockMapper stockMapper;

    @Autowired
    private AppRewardRankMapper rewardRankMapper;

    @Autowired
    private AppRewardLogMapper rewardLogMapper;

    @Autowired
    private OseeForgingLogMapper oseeForgingLogMapper;

    @Resource
    RedissonClient redissonClient;

    @Resource
    GmCommonService baseService;

    /**
     * 邮件撤销
     */
    @GmHandler(key = "/usdt/mail/revoke")
    public void mailRevoke(Map<String, Object> params, CommonResponse response) {

        String errorMsg = messageManager.revokeMessage(new BigDecimal("" + params.get("id")).longValue());

        if (StrUtil.isNotBlank(errorMsg)) {

            response.setSuccess(false);
            response.setErrMsg(errorMsg);
            return;

        }

        response.setErrCode("200");

    }

    @Autowired
    private UserManager userManager;

    @Autowired
    private MessageMapper messageMapper;

    /**
     * 邮件列表
     */
    @GmHandler(key = "/usdt/mail/list")
    public void getMailList(Map<String, Object> params, CommonResponse response) throws Exception {

        StringBuilder condBuilder = new StringBuilder(" WHERE 1=1 ");
        StringBuilder pageBuilder = new StringBuilder();

        // 解析数据
        int page = JsonMapUtils.parseObject(params, "page", JsonInnerType.TYPE_INT);
        int pageSize = JsonMapUtils.parseObject(params, "pageSize", JsonInnerType.TYPE_INT);

        pageBuilder.append(" LIMIT ").append((page - 1) * pageSize).append(", ").append(pageSize);

        for (Entry<String, Object> entry : params.entrySet()) {

            String key = entry.getKey();

            if (entry.getValue() == null) {
                continue;
            }
            switch (key) {
                case "startTime":
                    long startTime = JsonMapUtils.parseObject(params, "startTime", JsonInnerType.TYPE_LONG);
                    if (startTime > 0) {
                        Date startDate = new Date(startTime);
                        condBuilder.append(" AND msg.create_time >= '").append(DATE_FORMATER.format(startDate))
                                .append("'");
                    }
                    break;
                case "endTime":
                    long endTime = JsonMapUtils.parseObject(params, "endTime", JsonInnerType.TYPE_LONG);
                    if (endTime > 0) {
                        Date endDate = new Date(endTime);
                        condBuilder.append(" AND msg.create_time <= '").append(DATE_FORMATER.format(endDate))
                                .append("'");
                    }
                    break;
                case "username":
                    String username = JsonMapUtils.parseObject(params, "username", JsonInnerType.TYPE_STRING);
                    if (!StringUtils.isEmpty(username)) {
                        final long uid = userManager.getUserByUsername(username).getId();
                        if (uid > 0) {
                            condBuilder.append(" AND msg.to_id = ").append(uid);
                        }
                    }
                    break;
                case "title":
                    String title = JsonMapUtils.parseObject(params, "title", JsonInnerType.TYPE_STRING);
                    if (!StringUtils.isEmpty(title)) {
                        condBuilder.append(" AND msg.title like '%").append(title).append("%' ");
                    }
                    break;
                case "state":
                    Integer state = JsonMapUtils.parseObject(params, "state", JsonInnerType.TYPE_INT);
                    if (state != null) {
                        condBuilder.append(" AND msg.state = ").append(state);
                    }
                    break;
                case "receive":
                    Boolean receive = MapUtil.getBool(params, "receive");
                    if (receive != null) {
                        condBuilder.append(" AND msg.receive = ").append(receive);
                    }
                    break;
                case "gameId":
                    Long gameId = MapUtil.getLong(params, "gameId");
                    if (gameId != null) {
                        condBuilder.append(" AND ( msg.from_game_id = ").append(gameId).append(" OR msg.to_game_id = ")
                                .append(gameId).append(" )");

                    }
                    break;
                case "createName":
                    String createName = MapUtil.getStr(params, "createName");
                    if (StrUtil.isNotBlank(createName)) {
                        condBuilder.append(" AND msg.create_name like '%").append(createName).append("%' ");
                    }
                    break;
                default:
                    break;
            }
        }

        final List<MessageEntity> logs = messageMapper.getList(condBuilder.toString(), pageBuilder.toString());
        final Map<String, Object> listCount = messageMapper.getListCount(condBuilder.toString());

        long count = new BigDecimal("" + listCount.getOrDefault("count", 0)).longValue();

        List<Map<String, Object>> dataList = new LinkedList<>();

        for (MessageEntity log : logs) {

            Map<String, Object> dataMap = new HashMap<>();

            dataMap.put("id", log.getId());

            dataMap.put("fromGameId", log.getFromGameId());
            dataMap.put("toGameId", log.getToGameId());

            dataMap.put("createTime", log.getCreateTime());

            ServerUser serverUser = UserContainer.getUserById(log.getToId());
            if (serverUser != null) {
                dataMap.put("nickname", serverUser.getNickname());
            } else {
                dataMap.put("nickname", "");
            }

            dataMap.put("title", log.getTitle());
            dataMap.put("content", log.getContent());

            final String itemsJson = log.getItemsJson();

            if (!StringUtils.isEmpty(itemsJson)) {

                final List<ItemData> items = JSON.parseArray(itemsJson, ItemData.class);

                int bCount = 0, iCount = 0;
                StringBuilder props = new StringBuilder();
                StringBuilder battery = new StringBuilder();
                StringBuilder wing = new StringBuilder();
                for (int i = 0; i < items.size(); i++) {

                    final ItemData item = items.get(i);

                    if (item.getItemId() == ItemId.DIAMOND.getId()) { // 子弹
                        bCount += item.getCount();
                    }

                    if (item.getItemId() == ItemId.DRAGON_CRYSTAL.getId()) { // 金币
                        iCount += item.getCount();
                    }

                    if (ItemId.isProp(item.getItemId())) {

                        final ItemId itemIdById = ItemId.getItemIdById(item.getItemId());
                        if (itemIdById != null) {
                            props.append(" ").append(itemIdById.getInfo()).append("*").append(item.getCount());
                        }

                    }

                    if (ItemId.isBattery(item.getItemId())) {

                        final ItemId itemIdById = ItemId.getItemIdById(item.getItemId());
                        if (itemIdById != null) {
                            battery.append(" ").append(itemIdById.getInfo()).append("*").append(item.getCount());
                        }

                    }

                    if (ItemId.isWing(item.getItemId())) {

                        final ItemId itemIdById = ItemId.getItemIdById(item.getItemId());
                        if (itemIdById != null) {
                            wing.append(" ").append(itemIdById.getInfo()).append("*").append(item.getCount());
                        }

                    }

                }

                dataMap.put("bCount", bCount);
                dataMap.put("iCount", iCount);
                dataMap.put("props", props.toString());
                dataMap.put("battery", battery.toString());
                dataMap.put("wing", wing.toString());

            }

            dataMap.put("state", log.getState());
            dataMap.put("receiveTime", log.getReceiveTime());
            dataMap.put("receive", log.getReceive());

            dataList.add(dataMap);

        }

        Map<String, Object> result = new HashMap<>();
        result.put("list", dataList);
        result.put("totalNum", count);
        response.setData(result);

    }

    /**
     * 获取用户列表
     */
    @GmHandler(key = "/osee/player/list")
    public void doPlayerListTask(Map<String, Object> params, CommonResponse response) throws Exception {

        StringBuilder condBuilder = new StringBuilder();
        StringBuilder pageBuilder = new StringBuilder();
        StringBuilder orderBuilder = new StringBuilder();

        // 解析数据
        int page = JsonMapUtils.parseObject(params, "page", JsonInnerType.TYPE_INT);
        int pageSize = JsonMapUtils.parseObject(params, "pageSize", JsonInnerType.TYPE_INT);

        pageBuilder.append(" LIMIT ").append((page - 1) * pageSize).append(", ").append(pageSize);

        for (Entry<String, Object> entry : params.entrySet()) {
            String key = entry.getKey();
            switch (key) {
                case "startTime":
                    Long startTime = MapUtil.getLong(params, "startTime");
                    if (startTime != null && startTime > 0) {
                        Date date = new Date(startTime);
                        condBuilder.append(" AND user.create_time >= '").append(DATE_FORMATER.format(date)).append("'");
                    }
                    break;
                case "endTime":
                    Long endTime = MapUtil.getLong(params, "endTime");
                    if (endTime != null && endTime > 0) {
                        Date date = new Date(endTime);
                        condBuilder.append(" AND user.create_time <= '").append(DATE_FORMATER.format(date)).append("'");
                    }
                    break;
                case "playerId":
                    Long playerId = MapUtil.getLong(params, "playerId");
                    if (playerId != null) {
                        condBuilder.append(" AND user.id = ").append(playerId);
                    }
                    break;
                case "gameId":
                    Long gameId = MapUtil.getLong(params, "gameId");
                    if (gameId != null) {
                        condBuilder.append(" AND user.game_id = ").append(gameId);
                    }
                    break;
                case "username":
                    String username = JsonMapUtils.parseObject(params, "username", JsonInnerType.TYPE_STRING);
                    if (!StringUtils.isEmpty(username)) {
                        condBuilder.append(" AND user.username LIKE '%").append(username).append("%'");
                    }
                    break;
                case "nickname":
                    String nickname = JsonMapUtils.parseObject(params, "nickname", JsonInnerType.TYPE_STRING);
                    if (!StringUtils.isEmpty(nickname)) {
                        condBuilder.append(" AND user.nickname LIKE '%").append(nickname).append("%'");
                    }
                    break;
                case "userState":
                    Long userState = MapUtil.getLong(params, "userState");
                    if (userState != null && userState > 0) {
                        condBuilder.append(" AND user.user_state = ").append(userState - 1);
                    }
                    break;
                case "gameState":

                    Long gameState = MapUtil.getLong(params, "gameState");

                    if (gameState != null) {

                        if (gameState == -1) { // 查询：所有在线的，0 不在线

                            condBuilder.append(" AND user.online_state > 0");

                        } else { // 如果查询的是：在线

                            condBuilder.append(" AND user.online_state = ").append(gameState);

                        }

                        orderBuilder.append(" player.last_join_room_time DESC, ");

                    }

                    break;

                case "vipLevel":
                    Integer vipLevel = MapUtil.getInt(params, "vipLevel");
                    if (vipLevel != null && vipLevel > 0) {
                        condBuilder.append(" AND player.vip_level = ").append(vipLevel);
                    }
                    break;
                case "loginType":
                    Integer loginType = MapUtil.getInt(params, "loginType");
                    if (loginType != null && loginType > 0) {
                        if (loginType == 1) { // 微信登录
                            condBuilder.append(" AND TRIM(user.openid) != ''");
                        } else if (loginType == 2) { // 账号登录
                            condBuilder.append(" AND TRIM(user.openid) = ''");
                        }
                    }
                    break;
                case "loseControl":
                    Integer loseControl = MapUtil.getInt(params, "loseControl");
                    if (loseControl != null && loseControl > 0) {
                        condBuilder.append(" AND player.lose_control = ").append(loseControl - 1);
                    }
                    break;
                case "userIp":
                    String userIp = JsonMapUtils.parseObject(params, "userIp", JsonInnerType.TYPE_STRING);
                    if (!StringUtils.isEmpty(userIp)) {
                        condBuilder.append(" AND player.userIp LIKE '%").append(userIp).append("%'");
                    }
                    break;
                case "inviteCode":
                    String inviteCode = JsonMapUtils.parseObject(params, "inviteCode", JsonInnerType.TYPE_STRING);
                    if (!StringUtils.isEmpty(inviteCode)) {
                        condBuilder.append(" AND user.invite_code = ").append(inviteCode).append(" ");
                    }
                    break;
            }
        }

        List<Map> idList =
                playerMapper.getGmPlayerIdList(condBuilder.toString(), pageBuilder.toString(), orderBuilder.toString());
        int idCount = playerMapper.getGmPlayerCount(condBuilder.toString());

        List<Map<String, Object>> playerList = new LinkedList<>();
        for (Map map : idList) {

            ServerUser user = UserContainer.getUserById(Long.valueOf(map.get("id").toString()));
            OseePlayerEntity entity = PlayerManager.getPlayerEntity(user);

            Map<String, Object> playerInfoMap = new HashMap<>();

            playerInfoMap.put("userIp", entity.getUserIp());

            playerInfoMap.put("playerId", user.getId());
            playerInfoMap.put("gameId", user.getGameId());

            playerInfoMap.put("vipLevel", entity.getVipLevel());
            playerInfoMap.put("level", entity.getLevel()); // 玩家等级
            playerInfoMap.put("batteryLevel", entity.getBatteryLevel()); // 最高拥有炮台等级
            playerInfoMap.put("playerType", entity.getPlayerType() + 1);
            // playerInfoMap.put("nickname", user.getNickname());
            playerInfoMap.put("loginType", StringUtils.isEmpty(user.getOpenid()) ? 2 : 1);

            String dragonCrystalStr = NumberUtil.decimalFormat(",####", entity.getDragonCrystal());

            playerInfoMap.put("dragonCrystal", entity.getDragonCrystal());
            playerInfoMap.put("dragonCrystalStr", dragonCrystalStr);


            String goldTorpedo = NumberUtil.decimalFormat(",####", entity.getGoldTorpedo());

            playerInfoMap.put("goldTorpedo", goldTorpedo);
            playerInfoMap.put("goldTorpedoBang", entity.getGoldTorpedoBang());
            // 天天摸鱼手机号即是账号
            // playerInfoMap.put("username", StringUtils.isEmpty(user.getPhonenum()) ? "" : user.getUsername());
            playerInfoMap.put("username", user.getUsername());
            playerInfoMap.put("diamond", entity.getDiamond());
            playerInfoMap.put("lottery", entity.getLottery());
            playerInfoMap.put("money", entity.getMoney());

            String bankMoneyStr = NumberUtil.decimalFormat(",####", entity.getBankMoney());

            playerInfoMap.put("bankMoney", entity.getBankMoney());
            playerInfoMap.put("bankMoneyStr", bankMoneyStr);

            playerInfoMap.put("createTime", map.get("createTime"));
            playerInfoMap.put("gameState", user.getEntity().getOnlineState());

            playerInfoMap.put("loseControl", entity.getLoseControl() + 1);
            playerInfoMap.put("userState", user.getEntity().getUserState() + 1);

            playerInfoMap.put("useBattery", entity.getUseBattery());// 消耗子弹总量

            playerInfoMap.put("totalDragonCrystal", entity.getTotalDragonCrystal()); // 累计获得龙晶
            String userControlState = "无";


            boolean hasPersonalBatteryLevelFlag = redissonClient
                    .<String>getBucket(
                            FishingChallengeFightFishUtil.FISHING_PERSONAL_JCZD0_LIST_BATTERY_LEVEL_USER_PRE + user.getId())
                    .isExists();

            if (hasPersonalBatteryLevelFlag) {

                userControlState = "节点|" + userControlState;

            } else {

                userControlState = "无|" + userControlState;

            }

            BaseGameRoom room = GameContainer.getGameRoomByPlayerId(user.getId());

            RAtomicDouble jrProduceRatomicDouble = redissonClient
                    .getAtomicDouble(FishingChallengeFightFishUtil.FISHING_JR_PRODUCE_USER_PRE + user.getId());

            RAtomicDouble jrXhRatomicDouble =
                    redissonClient.getAtomicDouble(FishingChallengeFightFishUtil.FISHING_JR_XH_USER_PRE + user.getId());

            double jrProduce = jrProduceRatomicDouble.get();

            double jrXh = jrXhRatomicDouble.get();

            if (jrProduceRatomicDouble.remainTimeToLive() == -1) {

                // 设置：今日过期
                jrProduceRatomicDouble.expire(DateUtil.endOfDay(new Date()).toInstant());

            }

            if (jrXhRatomicDouble.remainTimeToLive() == -1) {

                // 设置：今日过期
                jrXhRatomicDouble.expire(DateUtil.endOfDay(new Date()).toInstant());

            }

            playerInfoMap.put("jryk", jrProduce - jrXh); // 今日盈亏

            NewBaseGamePlayer player = null;

            if (room instanceof NewBaseGameRoom) {

                player = room.getGamePlayerById(user.getId());

            }

            if (player != null) {

                long batteryLevel = player.getBatteryLevel();

                if (batteryLevel == 0) {

                    if (room == null) {

                        // 获取：场次的最低炮倍
                        batteryLevel =
                                FishingChallengeManager.getBatteryLevel(FishingChallengeManager.ROOM_ONE.getRoomIndex(), 0);

                    } else {

                        // 获取：场次的最低炮倍
                        batteryLevel =
                                FishingChallengeManager.getBatteryLevel(((NewBaseGameRoom) room).getRoomIndex(), 0);

                    }

                }

                NewBaseGameRoom newRoom = (NewBaseGameRoom) room;

                String roomIndexStr = String.valueOf(newRoom.getRoomIndex());

                // 处理：roomIndexStr
                roomIndexStr = handleRoomIndexStr(user.getId(), room, roomIndexStr);

                // 场次历史产出
                double cclsProduce = redissonClient
                        .getAtomicDouble(FishingChallengeFightFishUtil.FISHING_CCLS_PRODUCE_ROOM_INDEX_USER_PRE
                                + roomIndexStr + ":" + user.getId())
                        .get();

                // 场次历史消耗
                double cclsXh =
                        redissonClient.getAtomicDouble(FishingChallengeFightFishUtil.FISHING_CCLS_XH_ROOM_INDEX_USER_PRE
                                + roomIndexStr + ":" + user.getId()).get();

                playerInfoMap.put("ccyk", cclsProduce - cclsXh); // 场次历史盈亏

                RAtomicDouble jrRoomIndexProduceRatomicDouble =
                        redissonClient.getAtomicDouble(FishingChallengeFightFishUtil.FISHING_JR_PRODUCE_ROOM_INDEX_USER_PRE
                                + user.getId() + ":" + roomIndexStr);

                RAtomicDouble jrRoomIndexXhRatomicDouble =
                        redissonClient.getAtomicDouble(FishingChallengeFightFishUtil.FISHING_JR_XH_ROOM_INDEX_USER_PRE
                                + user.getId() + ":" + roomIndexStr);

                double jrRoomIndexProduce = jrRoomIndexProduceRatomicDouble.get();

                double jrRoomIndexXh = jrRoomIndexXhRatomicDouble.get();

                if (jrRoomIndexProduceRatomicDouble.remainTimeToLive() == -1) {

                    // 设置：今日过期
                    jrRoomIndexProduceRatomicDouble.expire(DateUtil.endOfDay(new Date()).toInstant());

                }

                if (jrRoomIndexXhRatomicDouble.remainTimeToLive() == -1) {

                    // 设置：今日过期
                    jrRoomIndexXhRatomicDouble.expire(DateUtil.endOfDay(new Date()).toInstant());

                }

                playerInfoMap.put("ccjryk", jrRoomIndexProduce - jrRoomIndexXh); // 今日场次盈亏

                playerInfoMap.put("ksFz", getKsFzByYkType(user.getId(), ((NewBaseGameRoom) room).getRoomIndex(), 1)); // 历史亏损峰值

                playerInfoMap.put("jrKsFz", getKsFzByYkType(user.getId(), ((NewBaseGameRoom) room).getRoomIndex(), 2)); // 今日亏损峰值

                // 进场产出
                double jcProduce =
                        redissonClient.getAtomicDouble(FishingChallengeFightFishUtil.FISHING_JC_PRODUCE_ROOM_INDEX_USER_PRE
                                + roomIndexStr + ":" + user.getId()).get();

                // 进场消耗
                double jcXh = redissonClient.getAtomicDouble(
                                FishingChallengeFightFishUtil.FISHING_JC_XH_ROOM_INDEX_USER_PRE + roomIndexStr + ":" + user.getId())
                        .get();

                playerInfoMap.put("jcyk", jcProduce - jcXh); // 进场盈亏

                // 玩家进场收益比
                double jcrtp1 = FishingChallengeFightFishUtil.handleAndGetJcrtp1(jcProduce, jcXh);

                // 期望子弹数
                RBucket<Double> jczd0Bucket =
                        redissonClient.getBucket(FishingChallengeFightFishUtil.FISHING_JCZD0_USER_PRE + user.getId());

                // 二次伤害鱼的，还未加的钱
                long doubleKillWinMoney = FishingChallengeFightFishUtil.getSecondaryDamageFishAllMoney(player.getId());

                // 当前子弹数
                long jczd1 = (entity.getDragonCrystal() + doubleKillWinMoney) / batteryLevel;

                String jczd0Str;
                Double jczd0 = jczd0Bucket.get();
                if (jczd0 == null) {
                    jczd0Str = "0";
                } else {
                    jczd0Str = BigDecimal.valueOf(jczd0).setScale(1, RoundingMode.HALF_UP).toString();
                }

                Long bdfz = redissonClient
                        .<Long>getBucket(FishingChallengeFightFishUtil.FISHING_BDFZ_USER_PRE + player.getId()).get();
                if (bdfz == null) {
                    bdfz = 0L;
                }

                long bd = redissonClient
                        .getAtomicLong(FishingChallengeFightFishUtil.FISHING_BD_USER_PRE + player.getId() + ":").get();

                StrBuilder jcrtpStrBuilder = StrBuilder.create();

                jcrtpStrBuilder.append(jczd1).append(" | ").append(jczd0Str).append(" | ")
                        .append(
                                BigDecimal.valueOf(jcrtp1).multiply(BigDecimal.valueOf(100)).setScale(2, RoundingMode.HALF_UP))
                        .append(" | ").append(bdfz - bd).append(" | ").append(player.getBfxyValue()).append(" | ")
                        .append(batteryLevel);

                playerInfoMap.put("jcrtpStr", jcrtpStrBuilder.toString());

                // 当前攻击次数/总攻击次数/奖励倍数/mz
                playerInfoMap.put("fishHitInfoStr", getFishHitInfoStr(player));


            } else {

                playerInfoMap.put("jcyk", "-"); // 进场盈亏

                playerInfoMap.put("ccyk", "-"); // 场次盈亏

                playerInfoMap.put("ccjryk", "-"); // 今日场次盈亏

                playerInfoMap.put("jcrtpStr", "-"); // 进场盈亏，现改为：实际子弹数 | 期望子弹数 | 实际进场收益比

                playerInfoMap.put("fishHitInfoStr", "-"); // 进场盈亏


                playerInfoMap.put("ksFz", "-"); // 历史亏损峰值

                playerInfoMap.put("jrKsFz", "-"); // 今日亏损峰值

            }


            // 赠送关系：最近送出的代理昵称|最近接收的代理昵称
            String giveGiftLastToUserNickName = "";
            String giveGiftLastFromUserNickName = "";

            // 最近赠送给哪个用户
            Long giveGiftLastToUserId = redissonClient.<Long, Long>getMap("GIVE_GIFT_LAST_TO").get(user.getId());

            // 最近被哪个用户赠送
            Long giveGiftLastFromUserId = redissonClient.<Long, Long>getMap("GIVE_GIFT_LAST_FROM").get(user.getId());

            if (giveGiftLastToUserId != null) {

                ServerUser giveGiftLastToUser = UserContainer.getUserById(giveGiftLastToUserId);

                if (giveGiftLastToUser != null) {

                    giveGiftLastToUserNickName = giveGiftLastToUser.getUsername();

                }

            }

            if (giveGiftLastFromUserId != null) {

                ServerUser giveGiftLastFromUser = UserContainer.getUserById(giveGiftLastFromUserId);

                if (giveGiftLastFromUser != null) {

                    giveGiftLastFromUserNickName = giveGiftLastFromUser.getUsername();

                }

            }

            playerInfoMap.put("giftLogStr", giveGiftLastToUserNickName + "|" + giveGiftLastFromUserNickName);

            playerList.add(playerInfoMap);

        }

        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("list", playerList);
        resultMap.put("totalNum", idCount);
        response.setData(resultMap);

        // dailyTask.grandPrixDay7Handler();

    }

    /**
     * 获取：命中情况
     */
    @NotNull
    public static String getFishHitInfoStr(NewBaseGamePlayer player) {

        if (player == null) {

            return "-";

        }

        return player.getClientRoomNumber() + " | " + player.getCurrentHitFishName() + " | "
                + player.getCurrentHitNumber() + " | " + player.getNeedHitNumber() + player.getNeedHitNumberMode() + " | "
                + player.getFishRewardMult() + " | " + player.getMz();

    }

    /**
     * 处理：roomIndexStr
     */
    public static String handleRoomIndexStr(long userId, BaseGameRoom room, String roomIndexStr) {

        return roomIndexStr;

    }

    // 通过：用户，区分，指定鱼种击杀是否开启，默认：false
    public static final String USER_SPECIFY_FISH_KILL_FLAG = "USER_SPECIFY_FISH_KILL_FLAG:";

    // 通过：用户，区分，指定鱼种击杀，原始值，map：key：鱼configId，value：击杀次数
    public static final String USER_SPECIFY_FISH_KILL_ORIGIN = "USER_SPECIFY_FISH_KILL_ORIGIN:";

    // 通过：用户，区分，指定鱼种击杀，当前值，map：key：鱼configId，value：击杀次数
    public static final String USER_SPECIFY_FISH_KILL_CURRENT = "USER_SPECIFY_FISH_KILL_CURRENT:";

    /**
     * 获取：指定击杀鱼种
     */
    @GmHandler(key = "/osee/player/getSpecifyFishKill")
    public void getSpecifyFishKill(Map<String, Object> paramMap, CommonResponse response) {

        Long userId = MapUtil.getLong(paramMap, "userId");

        if (userId == null) {
            return;
        }

        ServerUser user = UserContainer.getUserById(userId);

        if (user == null) {

            response.setSuccess(false);
            response.setErrMsg("玩家不存在");
            return;

        }

        RMap<Long, Integer> userSpecifyFishKillCurrentRMap =
                redissonClient.getMap(USER_SPECIFY_FISH_KILL_CURRENT + userId);

        Map<Long, Integer> userSpecifyFishKillCurrentMap = userSpecifyFishKillCurrentRMap.readAllMap();

        response.setData(userSpecifyFishKillCurrentMap);

    }

    /**
     * 设置：指定击杀鱼种
     */
    // @SneakyThrows
    // @GmHandler(key = "/osee/player/setSpecifyFishKill")
    public void setSpecifyFishKill(Map<String, Object> paramMap, CommonResponse response) {

        // Long userId = MapUtil.getLong(paramMap, "userId");
        //
        // if (userId == null) {
        // return;
        // }
        //
        // ServerUser user = UserContainer.getUserById(userId);
        //
        // if (user == null) {
        //
        // response.setSuccess(false);
        // response.setErrMsg("玩家不存在");
        // return;
        //
        // }
        //
        // synchronized (user) {
        //
        // JSONArray specifyFishMapList = MapUtil.get(paramMap, "specifyFishMapList", JSONArray.class);
        //
        // RMap<Long, Integer> userSpecifyFishKillOriginRMap =
        // redissonClient.getMap(USER_SPECIFY_FISH_KILL_ORIGIN + userId);
        //
        // RMap<Long, Integer> userSpecifyFishKillCurrentRMap =
        // redissonClient.getMap(USER_SPECIFY_FISH_KILL_CURRENT + userId);
        //
        // Map<Long, Integer> userSpecifyFishKillOriginMap = MapUtil.newHashMap(specifyFishMapList.size());
        //
        // for (Object item : specifyFishMapList) {
        //
        // JSONObject jsonObject = (JSONObject)item;
        //
        // Long fishConfigId = jsonObject.getLong("fishConfigId");
        //
        // if (fishConfigId == null) {
        // continue;
        // }
        //
        // FishConfig fishConfig = DataContainer.getData(fishConfigId, FishConfig.class);
        //
        // if (fishConfig == null) {
        // continue;
        // }
        //
        // Integer killCount = jsonObject.getInt("killCount", 0);
        //
        // if (killCount <= 0) {
        // continue;
        // }
        //
        // userSpecifyFishKillOriginMap.put(fishConfigId, killCount);
        //
        // }
        //
        // VoidFunc0 terminateVoidFunc0 = () -> {
        //
        // userSpecifyFishKillOriginRMap.delete();
        //
        // userSpecifyFishKillCurrentRMap.delete();
        //
        // if (CollUtil.isEmpty(userSpecifyFishKillOriginMap)) {
        //
        // userSpecifyFishKillOriginMap.put(-1L, 0);
        //
        // }
        //
        // userSpecifyFishKillOriginRMap.putAll(userSpecifyFishKillOriginMap);
        //
        // userSpecifyFishKillCurrentRMap.putAll(userSpecifyFishKillOriginMap);
        //
        // };
        //
        // String oldTargetStrValue = RedisUtil.val("USER_PERSONAL_CONTROL_NUM_CHALLENGE" + userId, 0d).toString();
        //
        // int bfType = FishingChallengeFightFishHelper.getTTypeEnumCategory(userId, 1);
        //
        // int hsType = FishingChallengeFightFishHelper.getTTypeEnumCategory(userId, 2);
        //
        // TTypeEnum hsTypeEnum = TTypeEnum.getByTypeAndCategory(1, hsType);
        //
        // TTypeEnum bfTypeEnum = TTypeEnum.getByTypeAndCategory(2, bfType);
        //
        // // 保存：个控修改记录
        // boolean terminateVoidFunc0CallFlag = tblOseeFishingUserControllerLogService
        // .saveLog(user.getUsername(), oldTargetStrValue, oldTargetStrValue, user.getId(), hsTypeEnum, bfTypeEnum,
        // false, MapUtil.getStr(paramMap, "operatorName", ""), 0L, 1, terminateVoidFunc0);
        //
        // if (!terminateVoidFunc0CallFlag) {
        //
        // terminateVoidFunc0.call();
        //
        // }
        //
        // }

    }

    /**
     * 获取用户详细信息
     */
    @GmHandler(key = "/osee/player/info")
    public void doPlayerInfoTask(Map<String, Object> params, CommonResponse response) {

        long playerId = (long) (double) params.get("playerId");

        ServerUser user = UserContainer.getUserById(playerId);

        if (user != null) {

            Map<String, Object> resultMap = new HashMap<>();

            resultMap.put("phone", user.getEntity().getPhonenum());

            resultMap.put("loseControl", PlayerManager.getPlayerEntity(user).getLoseControl() + 1);
            resultMap.put("playerType", PlayerManager.getPlayerEntity(user).getPlayerType() + 1);
            resultMap.put("playerSendGift", PlayerManager.getPlayerEntity(user).getSendGift());

            // 是否限制：用户的盈亏爆发
            RBucket<Boolean> ykbfFlagRBucket =
                    redissonClient.getBucket(FishingChallengeFightFishUtil.USER_YKBF_FLAG + user.getId());

            Boolean ykbfFlag = ykbfFlagRBucket.get();

            if (BooleanUtil.isFalse(ykbfFlag)) {

                resultMap.put("ykbfFlag", 0);

            } else {

                resultMap.put("ykbfFlag", 1);

            }
            resultMap.put("gameId", user.getGameId());
            resultMap.put("payWay", 0);
            String burstTmax = RedisHelper.get("USER_PERSONAL_CONTROL_BURST_TMAX" + playerId);
            Double[] burstTmax1 = {0.0, 0.0, 0.0, -40.0};
            if (burstTmax != null && burstTmax.length() != 0) {
                burstTmax = burstTmax.substring(burstTmax.lastIndexOf("[") + 1).replaceAll("]", "");
                String[] ap1 = burstTmax.split(",");
                List<Double> listap = new ArrayList<>();
                for (String a : ap1) {
                    // System.out.println(a);
                    if (a.isEmpty() || "null".equals(a)) {
                        a = "0";
                    }
                    listap.add(Double.parseDouble(a));
                }
                burstTmax1 = listap.toArray(new Double[0]);
            }
            resultMap.put("fishingProb", RedisUtil.val("USER_PERSONAL_CONTROL_NUM" + playerId, 0D));
            resultMap.put("burstTmax", burstTmax1);

            String burstTmin = RedisHelper.get("USER_PERSONAL_CONTROL_BURST_TMIN" + playerId);
            Double[] burstTmin1 = {-20.0, -30.0, -35.0, -75.0};
            if (burstTmin != null && burstTmin.length() != 0) {
                burstTmin = burstTmin.substring(burstTmin.lastIndexOf("[") + 1).replaceAll("]", "");
                String[] ap1 = burstTmin.split(",");
                List<Double> listap = new ArrayList<>();
                for (String a : ap1) {
                    // System.out.println(a);
                    if (a.isEmpty() || a.equals("null")) {
                        a = "0";
                    }
                    listap.add(Double.parseDouble(a));
                }
                burstTmin1 = listap.toArray(new Double[0]);
            }
            resultMap.put("burstTmin", burstTmin1);
            String recoveryTmax = RedisHelper.get("USER_PERSONAL_CONTROL_RECOVERY_TMAX" + playerId);
            Double[] recoveryTmax1 = {20.0, 30.0, 35.0, 100.0};
            if (recoveryTmax != null && recoveryTmax.length() != 0) {
                recoveryTmax = recoveryTmax.substring(recoveryTmax.lastIndexOf("[") + 1).replaceAll("]", "");
                String[] ap1 = recoveryTmax.split(",");
                List<Double> listap = new ArrayList<>();
                for (String a : ap1) {
                    // System.out.println(a);
                    if (a.isEmpty() || a.equals("null")) {
                        a = "0";
                    }
                    listap.add(Double.parseDouble(a));
                }
                recoveryTmax1 = listap.toArray(new Double[0]);
            }
            resultMap.put("recoveryTmax", recoveryTmax1);

            String recoveryTmin = RedisHelper.get("USER_PERSONAL_CONTROL_RECOVERY_TMIN" + playerId);
            Double[] recoveryTmin1 = {0.0, 0.0, 0.0, 50.0};
            if (recoveryTmin != null && recoveryTmin.length() != 0) {
                recoveryTmin = recoveryTmin.substring(recoveryTmin.lastIndexOf("[") + 1).replaceAll("]", "");
                String[] ap1 = recoveryTmin.split(",");
                List<Double> listap = new ArrayList<>();
                for (String a : ap1) {
                    // System.out.println(a);
                    if (a.isEmpty() || a.equals("null")) {
                        a = "0";
                    }
                    listap.add(Double.parseDouble(a));
                }
                recoveryTmin1 = listap.toArray(new Double[0]);
            }
            resultMap.put("recoveryTmin", recoveryTmin1);

            String burstTmaxChallEnge = RedisHelper.get("USER_PERSONAL_CONTROL_BURST_TMAX_CHALLENGE" + playerId);
            Double[] burstTmax1ChallEnge = {0.0, 0.0, 0.0, -40.0};
            if (burstTmaxChallEnge != null && burstTmaxChallEnge.length() != 0) {
                burstTmaxChallEnge =
                        burstTmaxChallEnge.substring(burstTmaxChallEnge.lastIndexOf("[") + 1).replaceAll("]", "");
                String[] ap1 = burstTmaxChallEnge.split(",");
                List<Double> listap = new ArrayList<>();
                for (String a : ap1) {
                    // System.out.println(a);
                    if (a.isEmpty() || a.equals("null")) {
                        a = "0";
                    }
                    listap.add(Double.parseDouble(a));
                }
                burstTmax1ChallEnge = listap.toArray(new Double[0]);
            }

            double val = RedisUtil.val("USER_PERSONAL_CONTROL_NUM_CHALLENGE" + playerId, 0d);

            resultMap.put("fishingProbChallEnge",
                    BigDecimal.valueOf(val).divide(BigDecimal.valueOf(10000)).toPlainString());

            resultMap.put("burstTmaxChallEnge", burstTmax1ChallEnge);

            String burstTminChallEnge = RedisHelper.get("USER_PERSONAL_CONTROL_BURST_TMIN_CHALLENGE" + playerId);
            Double[] burstTmin1ChallEnge = {-20.0, -30.0, -35.0, -75.0};
            if (burstTminChallEnge != null && burstTminChallEnge.length() != 0) {
                burstTminChallEnge =
                        burstTminChallEnge.substring(burstTminChallEnge.lastIndexOf("[") + 1).replaceAll("]", "");
                String[] ap1 = burstTminChallEnge.split(",");
                List<Double> listap = new ArrayList<>();
                for (String a : ap1) {
                    // System.out.println(a);
                    if (a.isEmpty() || a.equals("null")) {
                        a = "0";
                    }
                    listap.add(Double.parseDouble(a));
                }
                burstTmin1ChallEnge = listap.toArray(new Double[0]);
            }
            resultMap.put("burstTminChallEnge", burstTmin1ChallEnge);
            String recoveryTmaxChallEnge = RedisHelper.get("USER_PERSONAL_CONTROL_RECOVERY_TMAX_CHALLENGE" + playerId);
            Double[] recoveryTmax1ChallEnge = {20.0, 30.0, 35.0, 100.0};
            if (recoveryTmaxChallEnge != null && recoveryTmaxChallEnge.length() != 0) {
                recoveryTmaxChallEnge =
                        recoveryTmaxChallEnge.substring(recoveryTmaxChallEnge.lastIndexOf("[") + 1).replaceAll("]", "");
                String[] ap1 = recoveryTmaxChallEnge.split(",");
                List<Double> listap = new ArrayList<>();
                for (String a : ap1) {
                    // System.out.println(a);
                    if (a.isEmpty() || a.equals("null")) {
                        a = "0";
                    }
                    listap.add(Double.parseDouble(a));
                }
                recoveryTmax1ChallEnge = listap.toArray(new Double[0]);
            }
            resultMap.put("recoveryTmaxChallEnge", recoveryTmax1ChallEnge);

            String recoveryTminChallEnge = RedisHelper.get("USER_PERSONAL_CONTROL_RECOVERY_TMIN_CHALLENGE" + playerId);
            Double[] recoveryTmin1ChallEnge = {0.0, 0.0, 0.0, 50.0};
            if (recoveryTminChallEnge != null && recoveryTminChallEnge.length() != 0) {
                recoveryTminChallEnge =
                        recoveryTminChallEnge.substring(recoveryTminChallEnge.lastIndexOf("[") + 1).replaceAll("]", "");
                String[] ap1 = recoveryTminChallEnge.split(",");
                List<Double> listap = new ArrayList<>();
                for (String a : ap1) {
                    // System.out.println(a);
                    if (a.isEmpty() || a.equals("null")) {
                        a = "0";
                    }
                    listap.add(Double.parseDouble(a));
                }
                recoveryTmin1ChallEnge = listap.toArray(new Double[0]);
            }
            resultMap.put("recoveryTminChallEnge", recoveryTmin1ChallEnge);

            resultMap.put("nickname", user.getNickname());
            resultMap.put("lottery", PlayerManager.getPlayerEntity(user).getLottery());

            response.setData(resultMap);

        } else {

            response.setSuccess(false);
            response.setErrMsg("玩家不存在");

        }

    }

    /**
     * 修改用户金币
     */
    @GmHandler(key = "/osee/player/tenure/update")
    public void doPlayerTenureUpdateTask(Map<String, Object> params, CommonResponse response) {

        long playerId = (long) (double) params.get("playerId");
        int tenureType = (int) (double) params.get("tenureType");
        long number = (long) (double) params.get("number");
        String creator = params.get("creator").toString();

        ItemChangeReason reason = null;

        ServerUser user = UserContainer.getUserById(playerId);

        OseePlayerEntity playerEntity = PlayerManager.getPlayerEntity(user);

        long preDiamond = playerEntity.getDiamond();
        long preGoldTorpedo = playerEntity.getGoldTorpedo();

        long changeMoney = 0;
        long changeDiamond = 0;
        long changeGoldTorpedo = 0;

        if (PlayerManager.checkItem(user, tenureType, -number)) {

            reason = number >= 0 ? ItemChangeReason.GM_RECHARGE : ItemChangeReason.GM_DEDUCT;
            PlayerManager.addItem(user, tenureType, number, reason, true);

            OseeRechargeLogEntity log = new OseeRechargeLogEntity();
            log.setUserId(user.getId());
            log.setNickname(user.getNickname());
            log.setShopType(tenureType);
            log.setCount((int) number);
            log.setCreator(creator);
            log.setRechargeType(3); // 后台充值
            log.setOrderState(1); // 充值成功

            if (number > 0) { // 充值
                log.setOrderNum("R" + System.currentTimeMillis() / 1000 + ThreadLocalRandom.current().nextInt(1000));
            } else { // 扣除
                log.setOrderNum("D" + System.currentTimeMillis() / 1000 + ThreadLocalRandom.current().nextInt(1000));
            }

            if (tenureType == ItemId.MONEY.getId() && number > 0) {
                if (RedisUtil.val("USER_T_STATUS" + user.getId(), 0L) == 0) {
                    // int x = CommonLobbyManager.getUserT(user,1);
                    // RedisHelper.set("USER_T_PEAK_VALUE"+user.getId(),String.valueOf(x));
                }
                long a = RedisUtil.val("USER_T_BANKRUPTCY_NUMBER" + user.getId(), 0L);
                RedisHelper.set("USER_T_BANKRUPTCY_NUMBER" + user.getId(), String.valueOf(a + number));
            }

            if (tenureType == ItemId.DRAGON_CRYSTAL.getId()) { // 如果是龙晶

                // 改变：gretj
                if (redissonClient
                        .<String>getBucket(
                                FishingChallengeFightFishUtil.FISHING_PERSONAL_JCZD0_LIST_BATTERY_LEVEL_USER_PRE + user.getId())
                        .isExists()) {

                    redissonClient.getAtomicDouble(FishingChallengeFightFishUtil.FISHING_GRETJ_USER_PRE + user.getId())
                            .addAndGet(-number);

                }

                // 节点变化时，清除 aq相关
                FishingChallengeFightFishUtil.cleanAqData(user.getId());

                changeMoney = number;

            } else if (tenureType == ItemId.DIAMOND.getId()) { // 如果是钻石

                changeDiamond = number;

            } else if (tenureType == ItemId.GOLD_TORPEDO.getId()) { // 如果是弹头

                changeGoldTorpedo = number;

            }

        } else if (tenureType == ItemId.MONEY.getId() && PlayerManager.getRealPlayerMoney(user) + number >= 0) {

            List<ItemData> itemData = new ArrayList<>();
            itemData.add(new ItemData(ItemId.MONEY.getId(), -PlayerManager.getItemNum(user, ItemId.MONEY)));
            itemData.add(new ItemData(ItemId.BANK_MONEY.getId(), number - itemData.get(0).getCount()));
            PlayerManager.addItems(user, itemData, ItemChangeReason.GM_DEDUCT, true);

            OseeRechargeLogEntity log = new OseeRechargeLogEntity();
            log.setUserId(user.getId());
            log.setNickname(user.getNickname());
            log.setShopType(tenureType);
            log.setCount((int) number);
            log.setCreator(creator);
            log.setRechargeType(3); // 后台充值
            log.setOrderState(1); // 充值成功
            log.setOrderNum("D" + System.currentTimeMillis() / 1000 + ThreadLocalRandom.current().nextInt(1000));
            rechargeLogMapper.save(log);

        } else {

            response.setSuccess(false);
            response.setErrMsg("用户余额不足");

        }

    }

    /**
     * 获取击杀boss记录列表
     */
    @GmHandler(key = "/osee/killBoss/list")
    public void doKillBossListTask(Map<String, Object> params, CommonResponse response) throws Exception {
        StringBuilder condBuilder = new StringBuilder();
        StringBuilder pageBuilder = new StringBuilder();

        // 解析数据
        int page = JsonMapUtils.parseObject(params, "page", JsonInnerType.TYPE_INT);
        int pageSize = JsonMapUtils.parseObject(params, "pageSize", JsonInnerType.TYPE_INT);

        pageBuilder.append(" LIMIT ").append((page - 1) * pageSize).append(", ").append(pageSize);

        for (Entry<String, Object> entry : params.entrySet()) {
            String key = entry.getKey();
            switch (key) {
                case "startTime":
                    long startTime = JsonMapUtils.parseObject(params, "startTime", JsonInnerType.TYPE_LONG);
                    if (startTime > 0) {
                        Date date = new Date(startTime);
                        condBuilder.append(" AND createTime >= '").append(DATE_FORMATER.format(date)).append("'");
                    }
                    break;
                case "endTime":
                    long endTime = JsonMapUtils.parseObject(params, "endTime", JsonInnerType.TYPE_LONG);
                    if (endTime > 0) {
                        Date date = new Date(endTime);
                        condBuilder.append(" AND createTime <= '").append(DATE_FORMATER.format(date)).append("'");
                    }
                    break;
                case "userId":
                    long id = JsonMapUtils.parseObject(params, "userId", JsonInnerType.TYPE_LONG);
                    if (id > 0) {
                        condBuilder.append(" AND userId = '").append(id).append("'");
                    }
                    break;
                case "roomIndex":
                    long room = JsonMapUtils.parseObject(params, "roomIndex", JsonInnerType.TYPE_LONG);
                    if (room > 0) {
                        condBuilder.append(" AND room_index = '").append(room).append("' ");
                    }
                    break;
                case "bossName":
                    String bossName = MapUtil.getStr(params, "bossName");
                    if (StrUtil.isNotBlank(bossName)) {
                        condBuilder.append(" AND bossName like '%").append(bossName).append("%' ");
                    }
                    break;
            }
        }
        List<KillBossEntity> list = playerMapper.getKillBossList(condBuilder.toString(), pageBuilder.toString());
        int idCount = playerMapper.getKillBossCount(condBuilder.toString());
        List<Map<String, Object>> dataList = new LinkedList<>();
        for (KillBossEntity killBossEntity : list) {
            Map<String, Object> dataMap = new HashMap<>();
            dataMap.put("id", killBossEntity.getId());
            dataMap.put("createTime", killBossEntity.getCreateTime());
            dataMap.put("bossName", killBossEntity.getBossName());
            dataMap.put("nickName", killBossEntity.getNickName());
            dataMap.put("batterLevel", killBossEntity.getBatterLevel());
            dataMap.put("userId", killBossEntity.getUserId());
            dataMap.put("mult", killBossEntity.getMult());
            dataMap.put("roomIndex", killBossEntity.getRoomIndex());
            dataMap.put("award", killBossEntity.getAward());
            dataMap.put("bloodPoolFloatKillValue", killBossEntity.getBloodPoolFloatKillValue());
            dataList.add(dataMap);
        }
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("list", dataList);
        resultMap.put("totalNum", idCount);
        response.setData(resultMap);
    }

    /**
     * 冻结/解冻
     */
    @SuppressWarnings("unchecked")
    @GmHandler(key = "/osee/player/frozen")
    public void doPlayerFrozenTask(Map<String, Object> params, CommonResponse response) {
        List<Double> ids = (List<Double>) params.get("list");
        int type = (int) (double) params.get("type");

        type = type == 1 ? 1 : 0;
        for (double id : ids) {
            ServerUser user = UserContainer.getUserById(Math.round(id));
            user.getEntity().setUserState(type);
            userMapper.update(user.getEntity());
            if (type == 1) {
                NetManager.closeClientConnect(user);
            }
        }
    }

    /**
     * 强制下线
     */
    @SuppressWarnings("unchecked")
    @GmHandler(key = "/osee/player/offline")
    public void doPlayerOfflineTask(Map<String, Object> params, CommonResponse response) {
        List<Double> ids = (List<Double>) params.get("list");

        LogoutResponse resp = LogoutResponse.newBuilder().setResult(2).build();

        if (ids == null || ids.size() == 0) { // 全员下线
            List<ServerUser> users = UserContainer.getActiveServerUsers();
            for (ServerUser user : users) {
                doOffline(resp, user);
            }
        } else {
            for (double id : ids) { // 选定玩家下线
                ServerUser user = UserContainer.getUserById((long) id);
                if (user != null) {
                    doOffline(resp, user);
                }
            }
        }
    }

    /**
     * 执行：下线操作
     */
    private void doOffline(LogoutResponse resp, ServerUser user) {

        NetManager.sendMessage(LoginMsgCode.S_C_LOGOUT_RESPONSE_VALUE, resp, user);
        NetManager.closeClientConnect(user);

        BaseGamePlayer gamePlayer = GameContainer.getPlayerById(user.getId());
        if (gamePlayer != null) {
            synchronized (gamePlayer) {
                BaseGameRoom gameRoom = GameContainer.getGameRoomByCode(gamePlayer.getRoomCode());
                if (gameRoom != null) {
                    if (gameRoom instanceof FishingChallengeRoom) { // 捕鱼挑战赛
                        FishingChallengeUtil.exitRoom((FishingChallengePlayer) gamePlayer,
                                (FishingChallengeRoom) gameRoom);
                    }
                }
            }
        }

    }

    @Autowired
    private LoginManager loginManager;

    @Resource
    LoginSignManager loginSignManager;

    /**
     * 注册用户
     */
    @GmHandler(key = "/osee/player/register")
    public void doPlayerRegisterTask(Map<String, Object> paramMap, CommonResponse response) {

        String username = MapUtil.getStr(paramMap, "username");
        String password = MapUtil.getStr(paramMap, "password");

        UserEntity entity =
                loginSignManager.commonRegister(null, username, password, null, null, String.valueOf(1),
                        true, null, false, false);

        if (entity == null) {

            response.setErrMsg("注册失败");
            return;

        }

    }

    /**
     * 修改用户数据
     */
    @SneakyThrows
    @GmHandler(key = "/osee/player/update")
    public void doPlayerUpdateTask(Map<String, Object> paramMap, CommonResponse response) {

        Long playerId = ((Number) paramMap.get("playerId")).longValue();
        Integer ag = ((Number) paramMap.get("ag")).intValue();
        Integer one = ((Number) paramMap.get("one")).intValue();

        ServerUser user = UserContainer.getUserById((long) (double) paramMap.get("playerId"));

        Long gameId = MapUtil.getLong(paramMap, "gameId");

        if (user != null) {

            long userId = user.getId();

            long checkGameId = gameId == null ? userId : gameId; // 需要检查的 gameId

            long userGameId = user.getGameId();

            if (userGameId != checkGameId) {

                synchronized (LoginSignManager.GAME_ID_POOL) {

                    long countByGameId = userMapper.findCountByGameId(checkGameId, userId);

                    if (countByGameId > 0) {
                        response.setSuccess(false);
                        response.setErrMsg("游戏id重复：" + (checkGameId));
                        return;
                    }

                    // 移除：gameId池里面的数据
                    LoginSignManager.GAME_ID_POOL.remove(Convert.toInt(checkGameId));

                }

            }

            String phone = MapUtil.getStr(paramMap, "phone");

            user.getEntity().setPhonenum(phone);

            int playerType = (int) (double) paramMap.get("playerType") - 1; // 1~2 - 1 ：0~1 0表示玩家 1表示线下代理 2表示线上

            if (playerType == 1) { // 如果设置为代理了
                int payWay = Integer.parseInt(paramMap.get("payWay").toString());

                double first = (double) paramMap.get("rate");
                String playerName = paramMap.get("playerRemark").toString();
                String openChessCards = paramMap.get("openChessCards").toString();
                Double occ = Double.parseDouble(openChessCards);

                RedisHelper.set("Agent:OpenChessCards:" + userId, String.valueOf(occ.intValue()));
            } else if (playerType == 2) {
                int payWay = Integer.parseInt(paramMap.get("payWay").toString());


                double first = (double) paramMap.get("rateOnline");


                double firstpay = (double) paramMap.get("firstrpay");
                double otherpay = (double) paramMap.get("otherpay");
                String playerName = paramMap.get("playerRemark").toString();

                // String openChessCards = params.get("openChessCards1").toString();
                // Double occ = Double.parseDouble(openChessCards);
                //
                // RedisHelper.set("Agent:OpenChessCards:" + user.getId(), String.valueOf(occ.intValue()));

                RedisHelper.set("Agent:OpenChessCards:" + userId, "0");
            }

            String newPassword = MapUtil.getStr(paramMap, "password");

            if (StrUtil.isNotBlank(newPassword)) {

                user.getEntity().setPassword(DigestUtil.md5Hex(newPassword));
            }

            OseePlayerEntity playerEntity = PlayerManager.getPlayerEntity(user);

            int loseControl = MapUtil.getInt(paramMap, "loseControl", 1);

            playerEntity.setLoseControl(loseControl - 1);// 1~2 - 1 ：0~1 0表示正常 1表示控制必输 2表示必赢

            playerEntity.setPlayerType(playerType);
            if (StrUtil.isNotBlank(Convert.toStr(paramMap.get("bankpassword")))) {
                playerEntity.setBankPassword((String) paramMap.get("bankpassword"));
            }

            Integer ykbfFlag = MapUtil.getInt(paramMap, "ykbfFlag", 0);

            // 是否限制：用户的盈亏爆发
            RBucket<Boolean> ykbfRBucket =
                    redissonClient.<Boolean>getBucket(FishingChallengeFightFishUtil.USER_YKBF_FLAG + userId);

            if (ykbfFlag == 0) {

                ykbfRBucket.set(false);

            } else {

                ykbfRBucket.set(true);

            }

            // FishingHitDataManager.setPlayerFishingProb(user.getId(), (double) params.get("fishingProb"));
            Double fishingProb_obj = (Double) paramMap.get("fishingProb");
            RedisHelper.set("USER_PERSONAL_CONTROL_NUM" + userId, String.valueOf(fishingProb_obj));

            ArrayList<Double> burstTmax_obj = (ArrayList<Double>) paramMap.get("burstTmax");
            RedisHelper.set("USER_PERSONAL_CONTROL_BURST_TMAX" + userId, String.valueOf(burstTmax_obj));

            ArrayList<Double> burstTmin_obj = (ArrayList<Double>) paramMap.get("burstTmin");
            RedisHelper.set("USER_PERSONAL_CONTROL_BURST_TMIN" + userId, String.valueOf(burstTmin_obj));

            ArrayList<Double> recoveryTmax_obj = (ArrayList<Double>) paramMap.get("recoveryTmax");
            RedisHelper.set("USER_PERSONAL_CONTROL_RECOVERY_TMAX" + userId, String.valueOf(recoveryTmax_obj));

            ArrayList<Double> recoveryTmin_obj = (ArrayList<Double>) paramMap.get("recoveryTmin");
            RedisHelper.set("USER_PERSONAL_CONTROL_RECOVERY_TMIN" + userId, String.valueOf(recoveryTmin_obj));


            int hsType = MapUtil.getInt(paramMap, "hsType", 0);
            int bfType = MapUtil.getInt(paramMap, "bfType", 0);


            RBucket<Boolean> userSpecifyFishKillFlagRBucket =
                    redissonClient.getBucket(USER_SPECIFY_FISH_KILL_FLAG + userId);

            if (bfType == 6) {

                userSpecifyFishKillFlagRBucket.set(true);

            } else {

                userSpecifyFishKillFlagRBucket.set(false);

            }

            user.getEntity().setGameId(gameId); // 设置：玩家 id

            userMapper.updateWithGameId(user.getEntity());

            VoidFunc0 terminateVoidFunc0 = null;

            JSONArray specifyFishMapList = MapUtil.get(paramMap, "specifyFishMapList", JSONArray.class);

            if (specifyFishMapList != null) {

                RMap<Long, Integer> userSpecifyFishKillOriginRMap =
                        redissonClient.getMap(USER_SPECIFY_FISH_KILL_ORIGIN + userId);

                RMap<Long, Integer> userSpecifyFishKillCurrentRMap =
                        redissonClient.getMap(USER_SPECIFY_FISH_KILL_CURRENT + userId);

                Map<Long, Integer> userSpecifyFishKillOriginMap = MapUtil.newHashMap(specifyFishMapList.size());

                for (Object item : specifyFishMapList) {

                    JSONObject jsonObject = (JSONObject) item;

                    Long fishConfigId = jsonObject.getLong("fishConfigId");

                    if (fishConfigId == null) {
                        continue;
                    }

                    FishConfig fishConfig = DataContainer.getData(fishConfigId, FishConfig.class);

                    if (fishConfig == null) {
                        continue;
                    }

                    Integer killCount = jsonObject.getInt("killCount", 0);

                    if (killCount <= 0) {
                        continue;
                    }

                    userSpecifyFishKillOriginMap.put(fishConfigId, killCount);

                }

                terminateVoidFunc0 = () -> {

                    userSpecifyFishKillOriginRMap.delete();

                    userSpecifyFishKillCurrentRMap.delete();

                    if (CollUtil.isEmpty(userSpecifyFishKillOriginMap)) {

                        userSpecifyFishKillOriginMap.put(-1L, 0);

                    }

                    userSpecifyFishKillOriginRMap.putAll(userSpecifyFishKillOriginMap);

                    userSpecifyFishKillCurrentRMap.putAll(userSpecifyFishKillOriginMap);

                };

            }


            // 清除T
            // RedisHelper.set("USER_FISHTYPE_1_T_CHALLANGE" + user.getId(), String.valueOf(0));
            // RedisHelper.set("USER_FISHTYPE_2_T_CHALLANGE" + user.getId(), String.valueOf(0));
            // RedisHelper.set("USER_FISHTYPE_3_T_CHALLANGE" + user.getId(), String.valueOf(0));
            // RedisHelper.set("USER_FISHTYPE_4_T_CHALLANGE" + user.getId(), String.valueOf(0));

            Stream.of(1, 2, 3, 4).forEach(type -> {
                RedisHelper.redissonClient.getMap("GAME:USER:T;CHALLANGE;" + type, new JsonJacksonCodec()).clear();
            });

        }
    }

    /**
     * 获取实名认证列表
     */
    @GmHandler(key = "/osee/authentication/list")
    public void doAuthenticationListTask(Map<String, Object> params, CommonResponse response) throws Exception {
        StringBuilder condBuilder = new StringBuilder("WHERE 1=1");
        StringBuilder pageBuilder = new StringBuilder();

        // 解析数据
        int page = JsonMapUtils.parseObject(params, "page", JsonInnerType.TYPE_INT);
        int pageSize = JsonMapUtils.parseObject(params, "pageSize", JsonInnerType.TYPE_INT);

        pageBuilder.append(" LIMIT ").append((page - 1) * pageSize).append(", ").append(pageSize);

        for (Entry<String, Object> entry : params.entrySet()) {
            String key = entry.getKey();
            switch (key) {
                case "startTime":
                    long startTime = JsonMapUtils.parseObject(params, "startTime", JsonInnerType.TYPE_LONG);
                    if (startTime > 0) {
                        Date startDate = new Date(startTime);
                        condBuilder.append(" AND record.create_time >= '").append(DATE_FORMATER.format(startDate))
                                .append("'");
                    }
                    break;
                case "endTime":
                    long endTime = JsonMapUtils.parseObject(params, "endTime", JsonInnerType.TYPE_LONG);
                    if (endTime > 0) {
                        Date endDate = new Date(endTime);
                        condBuilder.append(" AND record.create_time <= '").append(DATE_FORMATER.format(endDate))
                                .append("'");
                    }
                    break;
                case "playerId":
                    long id = JsonMapUtils.parseObject(params, "playerId", JsonInnerType.TYPE_LONG);
                    if (id > 0) {
                        condBuilder.append(" AND record.user_id = ").append(id);
                    }
                    break;
                case "realName":
                    String realName = JsonMapUtils.parseObject(params, "realName", JsonInnerType.TYPE_STRING);
                    if (!StringUtils.isEmpty(realName)) {
                        condBuilder.append(" AND record.name = '").append(realName).append("'");
                    }
                    break;
                case "nickName":
                    String nickName = JsonMapUtils.parseObject(params, "nickName", JsonInnerType.TYPE_STRING);
                    if (!StringUtils.isEmpty(nickName)) {
                        condBuilder.append(" AND user.nickname = '").append(nickName).append("'");
                    }
                    break;
                case "phoneNum":
                    String phoneNum = JsonMapUtils.parseObject(params, "phoneNum", JsonInnerType.TYPE_STRING);
                    if (!StringUtils.isEmpty(phoneNum)) {
                        condBuilder.append(" AND record.phoneNum = '").append(phoneNum).append("'");
                    }
                    break;
            }
        }

        List<GmAuthenticationInfo> authentications =
                gmCommonMapper.getAuthenticationList(condBuilder.toString(), pageBuilder.toString());
        int total = gmCommonMapper.getAuthenticationCount(condBuilder.toString());

        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("totalNum", total);
        resultMap.put("list", JsonMapUtils.objectsToMaps(authentications));
        response.setData(resultMap);
    }

    /**
     * 删除实名认证记录
     */
    @GmHandler(key = "/osee/authentication/delete")
    public void doAuthenticationDeleteTask(Map<String, Object> params, CommonResponse response) throws Exception {
        long recordId = JsonMapUtils.parseObject(params, "id", JsonInnerType.TYPE_LONG);
        authenticationMapper.delete(recordId);
    }

    /**
     * 获取商品奖品数据
     */
    @GmHandler(key = "/osee/shop/list")
    public void doShopListTask(Map<String, Object> params, CommonResponse response) throws Exception {
        StringBuilder condBuilder = new StringBuilder("WHERE 1=1");
        StringBuilder pageBuilder = new StringBuilder();

        // 解析数据
        int page = JsonMapUtils.parseObject(params, "page", JsonInnerType.TYPE_INT);
        int pageSize = JsonMapUtils.parseObject(params, "pageSize", JsonInnerType.TYPE_INT);

        pageBuilder.append(" LIMIT ").append((page - 1) * pageSize).append(", ").append(pageSize);

        for (Entry<String, Object> entry : params.entrySet()) {
            String key = entry.getKey();
            switch (key) {
                case "name":
                    String name = JsonMapUtils.parseObject(params, "name", JsonInnerType.TYPE_STRING);
                    if (!StringUtils.isEmpty(name)) {
                        condBuilder.append(" AND name = '").append(name).append("'");
                    }
                    break;
                case "type":
                    int type = JsonMapUtils.parseObject(params, "type", JsonInnerType.TYPE_INT);
                    if (type > 0) {
                        condBuilder.append(" AND type = ").append(type);
                    }
                    break;
            }
        }
        Map<String, Object> resultMap = new HashMap<>();
        response.setData(resultMap);
    }

    /**
     * 添加商品奖品数据
     */
    @GmHandler(key = "/osee/shop/add")
    public void doShopAddTask(Map<String, Object> params, CommonResponse response) throws Exception {
        // 限购次数
        int size = params.containsKey("size") ? (int) (double) params.get("size") : 0;

        int type = (int) (double) params.get("type");
        int sendType = (int) (double) params.get("sendType");
        if (type == 1 && sendType == 1) { // 实物不能选择实时兑换
            response.setSuccess(false);
            response.setErrMsg("实物奖品不能选择实时兑换");
            return;
        } else if (type != 1 && sendType != 1) {
            response.setSuccess(false);
            response.setErrMsg("虚拟奖品请选择实时兑换");
            return;
        }
        // 库存
        int stock = params.containsKey("stock") ? (int) (double) params.get("stock") : 0;
        if (sendType == 3) { // 自动发卡库存不可设置，手动添加
            stock = 0;
        } else if (size > stock) { // 当限购次数大于库存时，则使用库存作为本轮可购次数
            size = stock;
        }

        OseeLotteryShopEntity shop = new OseeLotteryShopEntity();
        shop.setType(type);
        shop.setCount((long) (double) params.get("count"));
        shop.setName(params.get("name").toString());
        shop.setImg(params.get("img").toString());
        shop.setCost((int) (double) params.get("cost"));
        shop.setRefreshType((int) (double) params.get("refreshType"));
        shop.setSize(size);
        shop.setSendType(sendType);
        shop.setStock(stock);
        lotteryShopMapper.save(shop);
        shoppingManager.refreshLottery();
    }

    /**
     * 删除商品奖品数据
     */
    @GmHandler(key = "/osee/shop/delete")
    public void doShopDeleteTask(Map<String, Object> params, CommonResponse response) throws Exception {
        long id = (long) (double) params.get("id");
        lotteryShopMapper.deleteById(id);
        shoppingManager.refreshLottery();
    }

    /**
     * 修改商城奖品数据
     */
    @GmHandler(key = "/osee/shop/update")
    public void doShopUpdateTask(Map<String, Object> params, CommonResponse response) throws Exception {
        OseeLotteryShopEntity shop = lotteryShopMapper.getById((long) (double) params.get("id"));
        if (shop != null) {
            // 限购次数
            int size = params.containsKey("size") ? (int) (double) params.get("size") : 0;

            int type = (int) (double) params.get("type");
            int sendType = (int) (double) params.get("sendType");
            if (type == 1 && sendType == 1) { // 实物不能选择实时兑换
                response.setSuccess(false);
                response.setErrMsg("实物奖品不能选择实时兑换");
                return;
            } else if (type != 1 && sendType != 1) {
                response.setSuccess(false);
                response.setErrMsg("虚拟奖品请选择实时兑换");
                return;
            }
            // 库存
            int stock = params.containsKey("stock") ? (int) (double) params.get("stock") : 0;
            if (sendType == 3) { // 自动发卡库存不可设置，手动添加
                stock = 0;
            } else if (size > stock) { // 当限购次数大于库存时，则使用库存作为本轮可购次数
                size = stock;
            }
            shop.setType((int) (double) params.get("type"));
            shop.setCount((long) (double) params.get("count"));
            shop.setName(params.get("name").toString());
            shop.setImg(params.get("img").toString());
            shop.setCost((int) (double) params.get("cost"));
            shop.setSize(size);
            shop.setRefreshType((int) (double) params.get("refreshType"));
            shop.setSendType(sendType);
            shop.setStock(stock);
            lotteryShopMapper.update(shop);
            shoppingManager.refreshLottery();
        } else {
            response.setSuccess(false);
            response.setErrMsg("奖品不存在");
        }
    }

    /**
     * 获取指定商城奖品数据
     */
    @GmHandler(key = "/osee/shop/search")
    public void doShopSearchTask(Map<String, Object> params, CommonResponse response) throws Exception {
        OseeLotteryShopEntity entity = lotteryShopMapper.getById((long) (double) params.get("id"));
        if (entity != null) {
            Map<String, Object> itemMap = new HashMap<>();
            itemMap.put("id", entity.getId());
            itemMap.put("type", entity.getType());
            itemMap.put("count", entity.getCount());
            itemMap.put("name", entity.getName());
            itemMap.put("cost", entity.getCost());
            itemMap.put("img", entity.getImg());
            itemMap.put("size", entity.getSize());
            itemMap.put("restSize", entity.getSize() - entity.getUsedSize());
            itemMap.put("refreshType", entity.getRefreshType());
            itemMap.put("sendType", entity.getSendType());
            long stock;
            if (entity.getType() == 1 && entity.getSendType() == 3) { // 自动发卡的实物读取库存
                stock = stockMapper.getUnusedCount(entity.getId());
            } else {
                stock = entity.getStock();
            }
            itemMap.put("stock", stock);
            response.setData(itemMap);
        } else {
            response.setSuccess(false);
            response.setErrMsg("商品不存在");
        }
    }

    /**
     * 获取商品库存列表
     */
    @GmHandler(key = "/osee/shop/stock/list")
    public void doShopStockListTask(Map<String, Object> params, CommonResponse response) throws Exception {
        long shopId = JsonMapUtils.parseObject(params, "shopId", JsonInnerType.TYPE_LONG);
        OseeLotteryShopEntity shopEntity = lotteryShopMapper.getById(shopId);
        if (shopEntity == null) {
            response.setSuccess(false);
            response.setErrMsg("商品不存在");
            return;
        }

        // 查询条件构造
        StringBuilder query = new StringBuilder("where `shop_id` = ").append(shopId);
        for (Entry<String, Object> entry : params.entrySet()) {
            String key = entry.getKey();
            switch (key) {
                case "number":
                    String number = JsonMapUtils.parseObject(params, "number", JsonInnerType.TYPE_STRING);
                    if (!StringUtils.isEmpty(number)) {
                        query.append(" and `number` = '").append(number).append("'");
                    }
                    break;
                case "userId":
                    long userId = JsonMapUtils.parseObject(params, "userId", JsonInnerType.TYPE_LONG);
                    if (userId > 0) {
                        query.append(" and `user_id` = ").append(userId);
                    }
                    break;
                case "state":
                    int state = JsonMapUtils.parseObject(params, "state", JsonInnerType.TYPE_INT);
                    // 1-未兑换 2-已兑换
                    if (state == 1) {
                        query.append(" and `user_id` is null");
                    } else if (state == 2) {
                        query.append(" and `user_id` is not null");
                    }
                    break;
            }
        }
        // 获取数据的总条数
        long count = stockMapper.getCount(query.toString());
        query.append(" order by create_time desc");
        // 解析分页数据
        int pageNo = JsonMapUtils.parseObject(params, "page", JsonInnerType.TYPE_INT);
        int pageSize = JsonMapUtils.parseObject(params, "pageSize", JsonInnerType.TYPE_INT);
        query.append(" limit ").append((pageNo - 1) * pageSize).append(",").append(pageSize);
        // 获取数据
        List<StockEntity> stockEntityList = stockMapper.getList(query.toString());
        LinkedList<Map<String, Object>> list = new LinkedList<>();
        for (StockEntity entity : stockEntityList) {
            Map<String, Object> item = new HashMap<>();
            item.put("id", entity.getId());
            item.put("number", entity.getNumber());
            item.put("password", "******"); // entity.getPassword()); // 密码不传输
            item.put("userId", entity.getUserId());
            item.put("state", entity.getUserId() == null ? 1 : 2);
            list.add(item);
        }

        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("totalNum", count);
        resultMap.put("list", list);

        // 获取库存总数
        long stock = stockMapper.getCount("where `shop_id` = " + shopId);
        // 获取已经兑换的数量
        long usedNum = stockMapper.getCount("where `user_id` is not null and `shop_id` =" + shopId);
        resultMap.put("shopName", shopEntity.getName());
        resultMap.put("stock", stock);
        resultMap.put("usedNum", usedNum);
        response.setData(resultMap);
    }

    /**
     * 添加奖券商城商品库存任务
     */
    @GmHandler(key = "/osee/shop/stock/add")
    public void doShopStockAddTask(Map<String, Object> params, CommonResponse response) {
        StockEntity stockEntity = JSON.parseObject(JSON.toJSONString(params), StockEntity.class);
        if (stockEntity == null) {
            response.setSuccess(false);
            response.setErrMsg("数据为空");
            return;
        }
        stockMapper.save(stockEntity);
    }

    /**
     * 提交实物兑换订单
     */
    @GmHandler(key = "/osee/shop/submit")
    public void doShopSubmitTask(Map<String, Object> params, CommonResponse response) throws Exception {
        OseeLotteryShopEntity shop = lotteryShopMapper.getById((long) (double) params.get("shopId"));
        ServerUser user = UserContainer.getUserById((long) (double) params.get("playerId"));
        response.setSuccess(false);
        if (shop == null) {
            response.setErrMsg("商品不存在");
        } else if (user == null) {
            response.setErrMsg("用户不存在");
        } else if (shop.getType() != 1) {
            response.setErrMsg("目标商品不为实物");
        } else {
            int count = (int) (double) params.get("count");
            long price = count * shop.getCost();
            if (shop.getSize() != 0 && shop.getUsedSize() + count > shop.getSize()) {
                response.setErrMsg("商品剩余数量不足");
                return;
            }

            if (!PlayerManager.checkItem(user, ItemId.LOTTERY, price)) {
                response.setErrMsg("用户奖券不足");
                return;
            }
            PlayerManager.addItem(user, ItemId.LOTTERY, -price, ItemChangeReason.SHOPPING, true);

            shop.setUsedSize(shop.getUsedSize() + count);
            lotteryShopMapper.update(shop);

            OseeRealLotteryLogEntity entity = new OseeRealLotteryLogEntity();
            entity.setOrderNum("R" + System.currentTimeMillis() / 1000 + ThreadLocalRandom.current().nextInt(1000));
            entity.setUserId(user.getId());
            entity.setNickname(user.getNickname());
            entity.setRewardName(shop.getName());
            entity.setCount(count);
            entity.setCost(price);
            entity.setCreator(params.get("creator").toString());
            entity.setConsignee(params.get("consignee").toString());
            entity.setPhoneNum(params.get("phoneNum").toString());
            entity.setAddress(params.get("address").toString());
            realLotteryMapper.save(entity);
            response.setSuccess(true);
        }
    }

    /**
     * 获取虚拟道具兑换记录
     */
    @GmHandler(key = "/osee/shop/unreal_log/list")
    public void doShopUnrealLogListTask(Map<String, Object> params, CommonResponse response) throws Exception {
        StringBuilder condBuilder = new StringBuilder("WHERE 1=1");
        StringBuilder pageBuilder = new StringBuilder();

        // 解析数据
        int page = JsonMapUtils.parseObject(params, "page", JsonInnerType.TYPE_INT);
        int pageSize = JsonMapUtils.parseObject(params, "pageSize", JsonInnerType.TYPE_INT);

        pageBuilder.append(" LIMIT ").append((page - 1) * pageSize).append(", ").append(pageSize);

        for (Entry<String, Object> entry : params.entrySet()) {
            String key = entry.getKey();
            switch (key) {
                case "startTime":
                    long startTime = JsonMapUtils.parseObject(params, "startTime", JsonInnerType.TYPE_LONG);
                    if (startTime > 0) {
                        Date startDate = new Date(startTime);
                        condBuilder.append(" AND record.create_time >= '").append(DATE_FORMATER.format(startDate))
                                .append("'");
                    }
                    break;
                case "endTime":
                    long endTime = JsonMapUtils.parseObject(params, "endTime", JsonInnerType.TYPE_LONG);
                    if (endTime > 0) {
                        Date endDate = new Date(endTime);
                        condBuilder.append(" AND record.create_time <= '").append(DATE_FORMATER.format(endDate))
                                .append("'");
                    }
                    break;
                case "orderNum":
                    String orderNum = JsonMapUtils.parseObject(params, "orderNum", JsonInnerType.TYPE_STRING);
                    if (!StringUtils.isEmpty(orderNum)) {
                        condBuilder.append(" AND record.order_num = '").append(orderNum).append("'");
                    }
                    break;
                case "playerId":
                    long playerId = JsonMapUtils.parseObject(params, "playerId", JsonInnerType.TYPE_LONG);
                    if (!StringUtils.isEmpty(playerId)) {
                        condBuilder.append(" AND record.user_id = ").append(playerId);
                    }
                    break;
                case "nickname":
                    String nickname = JsonMapUtils.parseObject(params, "nickname", JsonInnerType.TYPE_STRING);
                    if (!StringUtils.isEmpty(nickname)) {
                        condBuilder.append(" AND record.nickname = '").append(nickname).append("'");
                    }
                    break;
            }
        }
        List<OseeUnrealLotteryLogEntity> logList =
                unrealLotteryMapper.getLogList(condBuilder.toString(), pageBuilder.toString());
        int logCount = unrealLotteryMapper.getLogCount(condBuilder.toString());

        List<Map<String, Object>> dataList = new LinkedList<>();
        for (OseeUnrealLotteryLogEntity log : logList) {
            Map<String, Object> dataMap = new HashMap<>();
            dataMap.put("createTime", log.getCreateTime());
            dataMap.put("orderNum", log.getOrderNum());
            dataMap.put("playerId", log.getUserId());
            dataMap.put("nickname", log.getNickname());
            dataMap.put("name", log.getRewardName());
            dataMap.put("type", log.getType());
            dataMap.put("count", log.getCount());
            dataMap.put("costType", log.getItemId());
            dataMap.put("cost", log.getCost());
            dataList.add(dataMap);
        }
        Map<String, Object> result = new HashMap<>();
        result.put("totalNum", logCount);
        result.put("list", dataList);
        response.setData(result);
    }

    /**
     * 获取实物道具兑换记录
     */
    @GmHandler(key = "/osee/shop/real_log/list")
    public void doShopRealLogListTask(Map<String, Object> params, CommonResponse response) throws Exception {
        StringBuilder condBuilder = new StringBuilder("WHERE 1=1");
        StringBuilder pageBuilder = new StringBuilder();

        // 解析数据
        int page = JsonMapUtils.parseObject(params, "page", JsonInnerType.TYPE_INT);
        int pageSize = JsonMapUtils.parseObject(params, "pageSize", JsonInnerType.TYPE_INT);

        pageBuilder.append(" LIMIT ").append((page - 1) * pageSize).append(", ").append(pageSize);

        for (Entry<String, Object> entry : params.entrySet()) {
            String key = entry.getKey();
            switch (key) {
                case "startTime":
                    long startTime = JsonMapUtils.parseObject(params, "startTime", JsonInnerType.TYPE_LONG);
                    if (startTime > 0) {
                        Date startDate = new Date(startTime);
                        condBuilder.append(" AND record.create_time >= '").append(DATE_FORMATER.format(startDate))
                                .append("'");
                    }
                    break;
                case "endTime":
                    long endTime = JsonMapUtils.parseObject(params, "endTime", JsonInnerType.TYPE_LONG);
                    if (endTime > 0) {
                        Date endDate = new Date(endTime);
                        condBuilder.append(" AND record.create_time <= '").append(DATE_FORMATER.format(endDate))
                                .append("'");
                    }
                    break;
                case "orderNum":
                    String orderNum = JsonMapUtils.parseObject(params, "orderNum", JsonInnerType.TYPE_STRING);
                    if (!StringUtils.isEmpty(orderNum)) {
                        condBuilder.append(" AND record.order_num = '").append(orderNum).append("'");
                    }
                    break;
                case "orderState":
                    int orderState = JsonMapUtils.parseObject(params, "orderState", JsonInnerType.TYPE_INT);
                    if (orderState > 0) {
                        condBuilder.append(" AND record.order_state = ").append(orderState - 1);
                    }
                    break;
                case "playerId":
                    long playerId = JsonMapUtils.parseObject(params, "playerId", JsonInnerType.TYPE_LONG);
                    if (!StringUtils.isEmpty(playerId)) {
                        condBuilder.append(" AND record.user_id = ").append(playerId);
                    }
                    break;
                case "nickname":
                    String nickname = JsonMapUtils.parseObject(params, "nickname", JsonInnerType.TYPE_STRING);
                    if (!StringUtils.isEmpty(nickname)) {
                        condBuilder.append(" AND record.nickname = '").append(nickname).append("'");
                    }
                    break;
            }
        }
        List<OseeRealLotteryLogEntity> logList =
                realLotteryMapper.getLogList(condBuilder.toString(), pageBuilder.toString());
        int logCount = realLotteryMapper.getLogCount(condBuilder.toString());
        List<Map<String, Object>> groupList = realLotteryMapper.getGroupCount("order_state");

        List<Map<String, Object>> dataList = new LinkedList<>();
        for (OseeRealLotteryLogEntity log : logList) {
            Map<String, Object> dataMap = new HashMap<>();
            dataMap.put("orderId", log.getId());
            dataMap.put("createTime", log.getCreateTime());
            // dataMap.put("orderNum", log.getOrderNum());
            dataMap.put("playerId", log.getUserId());
            dataMap.put("nickname", log.getNickname());
            dataMap.put("name", log.getRewardName());
            dataMap.put("count", log.getCount());
            dataMap.put("cost", log.getCost());
            // dataMap.put("creator", log.getCreator());
            dataMap.put("consignee", log.getConsignee());
            dataMap.put("phoneNum", log.getPhoneNum());
            dataMap.put("address", log.getAddress());
            dataMap.put("orderState", log.getOrderState() + 1);
            dataList.add(dataMap);
        }
        Map<String, Object> result = new HashMap<>();

        for (int i = 0, k = 0; i < 3; i++) {
            String key = "state_" + i;
            if (groupList.size() > 0 && k < groupList.size() && (int) groupList.get(k).get("key") == i) {
                result.put(key, groupList.get(k).get("count"));
                k++;
            } else {
                result.put(key, 0);
            }
        }

        result.put("totalNum", logCount);
        result.put("list", dataList);
        response.setData(result);
    }

    /**
     * 修改订单状态
     */
    @GmHandler(key = "/osee/shop/real_log/state/update")
    public void doShopRealLogStateUpdateTask(Map<String, Object> params, CommonResponse response) {

        long id = (long) (double) params.get("id");
        int state = (int) (double) params.get("state"); // 2 已发货 3 拒绝

        OseeRealLotteryLogEntity lotteryShopEntity = realLotteryMapper.getById(id);

        if (state != 2 && state != 3) {

            response.setSuccess(false);
            response.setErrMsg("无效的订单状态:" + state);
            return;

        } else if (lotteryShopEntity == null) {

            response.setSuccess(false);
            response.setErrMsg("订单不存在");
            return;

        }

        ServerUser user = UserContainer.getUserById(lotteryShopEntity.getUserId());

        if (user == null) {

            response.setSuccess(false);
            response.setErrMsg("用户不存在");
            return;

        }

        OseePlayerEntity playerEntity = PlayerManager.getPlayerEntity(user);

        synchronized (playerEntity) {

            if (lotteryShopEntity.getOrderState() != 0) {

                response.setSuccess(false);
                response.setErrMsg("订单当前状态为" + (lotteryShopEntity.getOrderState() == 1 ? "已发货" : "已拒绝") + "，无法修改订单状态");

            } else {

                lotteryShopEntity.setOrderState(state - 1);

                long preDragonCrystal = playerEntity.getDragonCrystal();
                long preDiamond = playerEntity.getDiamond();
                long preGoldTorpedo = playerEntity.getGoldTorpedo();
                long preLottery = playerEntity.getLottery();

                if (state == 3) {

                    // 拒绝发货退回奖券
                    PlayerManager.addItem(user, ItemId.LOTTERY, lotteryShopEntity.getCost(), ItemChangeReason.SHOPPING,
                            true);

                    StrBuilder strBuilder = StrBuilder.create();

                    strBuilder.append("拒绝发货，兑换商品：").append(lotteryShopEntity.getRewardName()).append("，兑换商品消耗奖卷数量：")
                            .append(lotteryShopEntity.getCost());

                } else {

                    StrBuilder strBuilder = StrBuilder.create();

                    strBuilder.append("已发货：兑换商品：").append(lotteryShopEntity.getRewardName()).append("，兑换商品消耗奖卷数量：")
                            .append(lotteryShopEntity.getCost());

                }

                realLotteryMapper.update(lotteryShopEntity);

            }

        }

    }

    /**
     * 获取强化记录
     */
    @GmHandler(key = "/osee/forging/list")
    public void doForgingListTask(Map<String, Object> params, CommonResponse response) throws Exception {
        StringBuilder condBuilder = new StringBuilder("WHERE 1=1");
        StringBuilder pageBuilder = new StringBuilder();

        // 解析数据
        int page = JsonMapUtils.parseObject(params, "page", JsonInnerType.TYPE_INT);
        int pageSize = JsonMapUtils.parseObject(params, "pageSize", JsonInnerType.TYPE_INT);

        pageBuilder.append(" LIMIT ").append((page - 1) * pageSize).append(", ").append(pageSize);

        for (Entry<String, Object> entry : params.entrySet()) {
            String key = entry.getKey();
            switch (key) {
                case "startTime":
                    long startTime = JsonMapUtils.parseObject(params, "startTime", JsonInnerType.TYPE_LONG);
                    if (startTime > 0) {
                        Date startDate = new Date(startTime);
                        condBuilder.append(" AND log.create_time >= '").append(DATE_FORMATER.format(startDate))
                                .append("'");
                    }
                    break;
                case "endTime":
                    long endTime = JsonMapUtils.parseObject(params, "endTime", JsonInnerType.TYPE_LONG);
                    if (endTime > 0) {
                        Date endDate = new Date(endTime);
                        condBuilder.append(" AND log.create_time <= '").append(DATE_FORMATER.format(endDate))
                                .append("'");
                    }
                    break;
                case "type":
                    int type = JsonMapUtils.parseObject(params, "type", JsonInnerType.TYPE_INT);
                    condBuilder.append(" AND log.type = ").append(type);
                    break;
                case "playerId":
                    long playerId = JsonMapUtils.parseObject(params, "playerId", JsonInnerType.TYPE_LONG);
                    if (!StringUtils.isEmpty(playerId)) {
                        condBuilder.append(" AND log.user_id = ").append(playerId);
                    }
                    break;
                case "nickname":
                    String nickname = JsonMapUtils.parseObject(params, "nickname", JsonInnerType.TYPE_STRING);
                    if (!StringUtils.isEmpty(nickname)) {
                        condBuilder.append(" AND log.nickname = '").append(nickname).append("'");
                    }
                    break;
            }
        }

        List<OseeForgingLogEntity> logList =
                oseeForgingLogMapper.getLogList(condBuilder.toString(), pageBuilder.toString());
        long logCount = oseeForgingLogMapper.getLogCount(condBuilder.toString());

        List<Map<String, Object>> dataList = new LinkedList<>();
        for (OseeForgingLogEntity log : logList) {
            Map<String, Object> dataMap = new HashMap<>();
            dataMap.put("createTime", log.getCreateTime());
            dataMap.put("payForging", log.getPayForging());
            dataMap.put("playerId", log.getUserId());
            dataMap.put("nickname", log.getNickname());
            dataMap.put("reward", log.getReward());
            dataMap.put("target", log.getTarget());
            dataMap.put("type", log.getType());
            dataMap.put("isSuccess", log.getIsSuccess());
            dataList.add(dataMap);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("totalNum", logCount);
        result.put("list", dataList);
        response.setData(result);
    }

    /**
     * 获取玩家账户变动原因
     */
    @GmHandler(key = "/osee/player/tenure/change_reason")
    @SuppressWarnings("unchecked")
    public void doPlayerTenureChangeReasonTask(Map<String, Object> params, CommonResponse response) {
        Map<String, Object> resultMap = (Map<String, Object>) response.getData();
        List<Object> reasonList = new ArrayList<>();
        for (ItemChangeReason reason : ItemChangeReason.values()) {
            Map<String, Object> reasonMap = new HashMap<>();
            reasonMap.put("id", reason.getId());
            reasonMap.put("info", reason.getInfo());
            reasonList.add(reasonMap);
        }
        resultMap.put("list", reasonList);
    }

    /**
     * 获取玩家账户变动记录
     */
    @GmHandler(key = "/osee/player/tenure/log")
    public void doPlayerTenureLogTask(Map<String, Object> params, CommonResponse response) throws Exception {
        StringBuilder condBuilder = new StringBuilder("WHERE 1=1");
        StringBuilder pageBuilder = new StringBuilder();

        // 解析数据
        int page = JsonMapUtils.parseObject(params, "page", JsonInnerType.TYPE_INT);
        int pageSize = JsonMapUtils.parseObject(params, "pageSize", JsonInnerType.TYPE_INT);

        pageBuilder.append(" LIMIT ").append((page - 1) * pageSize).append(", ").append(pageSize);

        for (Entry<String, Object> entry : params.entrySet()) {
            String key = entry.getKey();
            switch (key) {
                case "startTime":
                    long startTime = JsonMapUtils.parseObject(params, "startTime", JsonInnerType.TYPE_LONG);
                    if (startTime > 0) {
                        Date startDate = new Date(startTime);
                        condBuilder.append(" AND log.create_time >= '").append(DATE_FORMATER.format(startDate))
                                .append("'");
                    }
                    break;
                case "endTime":
                    long endTime = JsonMapUtils.parseObject(params, "endTime", JsonInnerType.TYPE_LONG);
                    if (endTime > 0) {
                        Date endDate = new Date(endTime);
                        condBuilder.append(" AND log.create_time <= '").append(DATE_FORMATER.format(endDate))
                                .append("'");
                    }
                    break;
                case "playerId":
                    long playerId = JsonMapUtils.parseObject(params, "playerId", JsonInnerType.TYPE_LONG);
                    if (!StringUtils.isEmpty(playerId)) {
                        condBuilder.append(" AND log.user_id = ").append(playerId);
                    }
                    break;
                case "nickname":
                    String nickname = JsonMapUtils.parseObject(params, "nickname", JsonInnerType.TYPE_STRING);
                    if (!StringUtils.isEmpty(nickname)) {
                        condBuilder.append(" AND user.nickname = '").append(nickname).append("'");
                    }
                    break;
                case "type":
                    int type = JsonMapUtils.parseObject(params, "type", JsonInnerType.TYPE_INT);
                    if (type > 0) {
                        condBuilder.append(" AND log.reason = ").append(type);
                    }
                    break;
            }
        }

        List<OseePlayerTenureLogEntity> logs =
                tenureLogMapper.getLogList(condBuilder.toString(), pageBuilder.toString());
        int count = tenureLogMapper.getLogCount(condBuilder.toString());

        List<Map<String, Object>> dataList = new LinkedList<>();
        for (OseePlayerTenureLogEntity log : logs) {
            Map<String, Object> dataMap = new HashMap<>();
            dataMap.put("createTime", log.getCreateTime());
            dataMap.put("playerId", log.getUserId());
            dataMap.put("nickname", log.getNickname());
            dataMap.put("reason", log.getReason());
            dataMap.put("preDiamond", log.getPreDiamond());
            dataMap.put("changeDiamond", log.getChangeDiamond());
            dataMap.put("afterDiamond", log.getPreDiamond() + log.getChangeDiamond());
            dataMap.put("preMoney", log.getPreMoney());
            dataMap.put("changeMoney", log.getChangeMoney());
            dataMap.put("afterMoney", log.getPreMoney() + log.getChangeMoney());
            dataMap.put("preLottery", log.getPreLottery());
            dataMap.put("changeLottery", log.getChangeLottery());
            dataMap.put("afterLottery", log.getPreLottery() + log.getChangeLottery());
            dataMap.put("preBankMoney", log.getPreBankMoney());
            dataMap.put("changeBankMoney", log.getChangeBankMoney());
            dataMap.put("afterBankMoney", log.getPreBankMoney() + log.getChangeBankMoney());
            dataMap.put("changeDragonCrystal", log.getChangeDragonCrystal());
            dataMap.put("afterDragonCrystal", log.getPreDragonCrystal() + log.getChangeDragonCrystal());

            // 变动前鱼雷数量明细
            String preTorpedoStr = "青*" + log.getPreBronzeTorpedo() + "\n" + "银*" + log.getPreSilverTorpedo() + "\n"
                    + "金*" + log.getPreGoldTorpedo();
            dataMap.put("preTorpedo", preTorpedoStr);
            // 变动的鱼雷数量
            String changeTorpedoStr = "青 " + log.getChangeBronzeTorpedo() + "\n" + "银 " + log.getChangeSilverTorpedo()
                    + "\n" + "金 " + log.getChangeGoldTorpedo();
            dataMap.put("changeTorpedo", changeTorpedoStr);
            // 变动后的鱼雷数量明细
            String afterTorpedoStr = "青*" + (log.getPreBronzeTorpedo() + log.getChangeBronzeTorpedo()) + "\n" + "银*"
                    + (log.getPreSilverTorpedo() + log.getChangeSilverTorpedo()) + "\n" + "金*"
                    + (log.getPreGoldTorpedo() + log.getChangeGoldTorpedo());
            dataMap.put("afterTorpedo", afterTorpedoStr);

            // 变动前技能数量明细
            String preSkillStr = "锁定*" + log.getPreSkillLock() + "\n" + "冰冻*" + log.getPreSkillFrozen() + "\n" + "急速*"
                    + log.getPreSkillFast() + "\n" + "暴击*" + log.getPreSkillCrit();
            dataMap.put("preSkill", preSkillStr);
            // 变动的技能数量
            String changeSkillStr = "锁定 " + log.getChangeSkillLock() + "\n" + "冰冻 " + log.getChangeSkillFrozen() + "\n"
                    + "急速 " + log.getChangeSkillFast() + "\n" + "暴击 " + log.getChangeSkillCrit();
            dataMap.put("changeSkill", changeSkillStr);
            // 变动后的技能数量明细
            String afterSkillStr = "锁定*" + (log.getPreSkillLock() + log.getChangeSkillLock()) + "\n" + "冰冻*"
                    + (log.getPreSkillFrozen() + log.getChangeSkillFrozen()) + "\n" + "急速*"
                    + (log.getPreSkillFast() + log.getChangeSkillFast()) + "\n" + "暴击*"
                    + (log.getPreSkillCrit() + log.getChangeSkillCrit());
            dataMap.put("afterSkill", afterSkillStr);

            dataList.add(dataMap);
        }
        Map<String, Object> result = new HashMap<>();

        result.put("totalNum", count);
        result.put("list", dataList);
        response.setData(result);
    }

    /**
     * 获取抽水明细
     */
    @GmHandler(key = "/osee/cut_money/list")
    public void doCutMoneyListTask(Map<String, Object> params, CommonResponse response) throws Exception {
        StringBuilder condBuilder = new StringBuilder("WHERE 1=1");
        StringBuilder pageBuilder = new StringBuilder();

        // 解析数据
        int page = JsonMapUtils.parseObject(params, "page", JsonInnerType.TYPE_INT);
        int pageSize = JsonMapUtils.parseObject(params, "pageSize", JsonInnerType.TYPE_INT);

        pageBuilder.append(" LIMIT ").append((page - 1) * pageSize).append(", ").append(pageSize);

        for (Entry<String, Object> entry : params.entrySet()) {
            String key = entry.getKey();
            switch (key) {
                case "startTime":
                    long startTime = JsonMapUtils.parseObject(params, "startTime", JsonInnerType.TYPE_LONG);
                    if (startTime > 0) {
                        Date startDate = new Date(startTime);
                        condBuilder.append(" AND log.create_time >= '").append(DATE_FORMATER.format(startDate))
                                .append("'");
                    }
                    break;
                case "endTime":
                    long endTime = JsonMapUtils.parseObject(params, "endTime", JsonInnerType.TYPE_LONG);
                    if (endTime > 0) {
                        Date endDate = new Date(endTime);
                        condBuilder.append(" AND log.create_time <= '").append(DATE_FORMATER.format(endDate))
                                .append("'");
                    }
                    break;
                case "game":
                    int game = JsonMapUtils.parseObject(params, "game", JsonInnerType.TYPE_INT);
                    if (game > 0) {
                        condBuilder.append(" AND log.game = ").append(game);
                    }
                    break;
                case "playerId":
                    long playerId = JsonMapUtils.parseObject(params, "playerId", JsonInnerType.TYPE_LONG);
                    if (playerId > 0) {
                        condBuilder.append(" AND log.user_id = ").append(playerId);
                    }
                    break;
                // case "nickname":
                // String nickname = JsonMapUtils.parseObject(params, "nickname", JsonInnerType.TYPE_STRING);
                // if (!StringUtils.isEmpty(nickname)) {
                // condBuilder.append(" AND user.nickname = '").append(nickname).append("'");
                // }
                // break;
            }
        }

        List<OseeCutMoneyLogEntity> logs = cutMoneyLogMapper.getLogList(condBuilder.toString(), pageBuilder.toString());
        Map<String, Object> count = cutMoneyLogMapper.getLogCount(condBuilder.toString());

        List<Map<String, Object>> dataList = new LinkedList<>();
        for (OseeCutMoneyLogEntity log : logs) {
            Map<String, Object> dataMap = new HashMap<>();
            dataMap.put("createTime", log.getCreateTime());
            dataMap.put("playerId", log.getUserId());
            dataMap.put("nickname", UserContainer.getUserById(log.getUserId()).getNickname());
            dataMap.put("game", log.getGame());
            dataMap.put("cutMoney", log.getCutMoney());
            dataList.add(dataMap);
        }
        Map<String, Object> result = new HashMap<>();

        result.put("totalNum", count.get("totalNum"));
        result.put("totalCut", count.get("totalCut"));
        result.put("list", dataList);
        response.setData(result);
    }

    /**
     * 获取支出明细
     */
    @GmHandler(key = "/osee/pay_money/list")
    public void doPayMoneyListTask(Map<String, Object> params, CommonResponse response) throws Exception {
        StringBuilder condBuilder = new StringBuilder("WHERE 1=1");
        StringBuilder pageBuilder = new StringBuilder();

        // 解析数据
        int page = JsonMapUtils.parseObject(params, "page", JsonInnerType.TYPE_INT);
        int pageSize = JsonMapUtils.parseObject(params, "pageSize", JsonInnerType.TYPE_INT);

        pageBuilder.append(" LIMIT ").append((page - 1) * pageSize).append(", ").append(pageSize);

        for (Entry<String, Object> entry : params.entrySet()) {
            String key = entry.getKey();
            switch (key) {
                case "startTime":
                    long startTime = JsonMapUtils.parseObject(params, "startTime", JsonInnerType.TYPE_LONG);
                    if (startTime > 0) {
                        Date startDate = new Date(startTime);
                        condBuilder.append(" AND log.create_time >= '").append(DATE_FORMATER.format(startDate))
                                .append("'");
                    }
                    break;
                case "endTime":
                    long endTime = JsonMapUtils.parseObject(params, "endTime", JsonInnerType.TYPE_LONG);
                    if (endTime > 0) {
                        Date endDate = new Date(endTime);
                        condBuilder.append(" AND log.create_time <= '").append(DATE_FORMATER.format(endDate))
                                .append("'");
                    }
                    break;
                case "payType":
                    int payType = JsonMapUtils.parseObject(params, "payType", JsonInnerType.TYPE_INT);
                    if (payType > 0) {
                        condBuilder.append(" AND log.pay_type = ").append(payType);
                    }
                    break;
                case "playerId":
                    long playerId = JsonMapUtils.parseObject(params, "playerId", JsonInnerType.TYPE_LONG);
                    if (playerId > 0) {
                        condBuilder.append(" AND log.user_id = ").append(playerId);
                    }
                    break;
                case "nickname":
                    String nickname = JsonMapUtils.parseObject(params, "nickname", JsonInnerType.TYPE_STRING);
                    if (!StringUtils.isEmpty(nickname)) {
                        condBuilder.append(" AND user.nickname = '").append(nickname).append("'");
                    }
                    break;
            }
        }

        List<OseeExpendLogEntity> logs = expendLogMapper.getLogList(condBuilder.toString(), pageBuilder.toString());
        Map<String, Object> count = expendLogMapper.getLogCount(condBuilder.toString());

        List<Map<String, Object>> dataList = new LinkedList<>();
        for (OseeExpendLogEntity log : logs) {
            Map<String, Object> dataMap = new HashMap<>();
            dataMap.put("createTime", log.getCreateTime());
            dataMap.put("playerId", log.getUserId());
            dataMap.put("nickname", log.getNickname());
            dataMap.put("payType", log.getPayType());
            dataMap.put("diamond", log.getDiamond());
            dataMap.put("money", log.getMoney());
            dataMap.put("lottery", log.getLottery());
            dataList.add(dataMap);
        }
        Map<String, Object> result = new HashMap<>();

        result.put("totalNum", count.get("totalNum"));
        result.put("totalDiamond", count.get("diamond"));
        result.put("totalMoney", count.get("money"));
        result.put("totalLottery", count.get("lottery"));
        result.put("list", dataList);
        response.setData(result);
    }


    /**
     * 获取游走字幕列表
     */
    @GmHandler(key = "/osee/wander_subtitle/list")
    public void doWanderSubtitleListTask(Map<String, Object> params, CommonResponse response) throws Exception {
        int page = JsonMapUtils.parseObject(params, "page", JsonInnerType.TYPE_INT);
        int pageSize = JsonMapUtils.parseObject(params, "pageSize", JsonInnerType.TYPE_INT);

        int skip = (page - 1) * pageSize;

        List<WanderSubtitleEntity> subtitles = subtitleMapper.getPage(skip, pageSize);
        int totalCount = subtitleMapper.getTotolCount();

        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("totalNum", totalCount);

        long nowTime = System.currentTimeMillis();
        List<Map<String, Object>> mapResultsList = new LinkedList<>();
        for (int i = 0; i < subtitles.size(); i++) {
            WanderSubtitleEntity subtitle = subtitles.get(i);
            Map<String, Object> mapResults = new HashMap<>();
            mapResults.put("subtitleId", subtitle.getId());
            mapResults.put("content", subtitle.getContent());
            mapResults.put("intervalTime", subtitle.getIntervalTime());
            mapResults.put("effectiveTime", subtitle.getStartTime().getTime());
            mapResults.put("failureTime", subtitle.getEndTime().getTime());

            int state = 0;
            if (subtitle.getStartTime().getTime() > nowTime || subtitle.getEndTime().getTime() < nowTime) {
                state = 1;
            }
            mapResults.put("state", state);
            mapResultsList.add(mapResults);
        }
        resultMap.put("list", mapResultsList);
        response.setData(resultMap);
    }

    /**
     * 获取反馈列表
     */
    @GmHandler(key = "/osee/feedBack/list")
    public void doFeedBackListTask(Map<String, Object> params, CommonResponse response) throws Exception {
        StringBuilder condBuilder = new StringBuilder("WHERE 1=1");
        StringBuilder pageBuilder = new StringBuilder();

        // 解析数据
        int page = JsonMapUtils.parseObject(params, "page", JsonInnerType.TYPE_INT);
        int pageSize = JsonMapUtils.parseObject(params, "pageSize", JsonInnerType.TYPE_INT);

        pageBuilder.append(" LIMIT ").append((page - 1) * pageSize).append(", ").append(pageSize);

        for (Entry<String, Object> entry : params.entrySet()) {
            String key = entry.getKey();
            switch (key) {
                case "startTime":
                    long startTime = JsonMapUtils.parseObject(params, "startTime", JsonInnerType.TYPE_LONG);
                    if (startTime > 0) {
                        Date startDate = new Date(startTime);
                        condBuilder.append(" AND log.createTime >= '").append(DATE_FORMATER.format(startDate))
                                .append("'");
                    }
                    break;
                case "endTime":
                    long endTime = JsonMapUtils.parseObject(params, "endTime", JsonInnerType.TYPE_LONG);
                    if (endTime > 0) {
                        Date endDate = new Date(endTime);
                        condBuilder.append(" AND log.createTime <= '").append(DATE_FORMATER.format(endDate))
                                .append("'");
                    }
                    break;
            }
        }

        List<FeedBackEntity> logList =
                rechargeLogMapper.getFeedBackList(condBuilder.toString(), pageBuilder.toString());

        long logCount = rechargeLogMapper.getFeedBackCount(condBuilder.toString());

        List<Map<String, Object>> dataList = new LinkedList<>();

        Map<Long, String> messageMap = new HashMap<>();

        if (CollUtil.isNotEmpty(logList)) {

            Set<Long> feedbackIdSet = logList.stream().map(DbEntity::getId).collect(Collectors.toSet());

            String refIdSetStr = CollUtil.join(feedbackIdSet, ",");

            List<MessageEntity> messageList = messageMapper.getListByRefFeedbackIdSetStr(refIdSetStr);

            messageMap = messageList.stream()
                    .collect(Collectors.toMap(MessageEntity::getRefId, MessageEntity::getContent, (v1, v2) -> v2));

            // log.info("messageMap：{}，refIdSetStr：{}", JSONUtil.toJsonStr(messageMap), refIdSetStr);

        }

        for (FeedBackEntity item : logList) {

            Map<String, Object> dataMap = new HashMap<>();

            dataMap.put("createTime", item.getCreateTime());
            dataMap.put("userName", item.getUserName());

            dataMap.put("userId", item.getUserId());

            ServerUser user = UserContainer.getUserById(item.getUserId());

            if (user == null) {

                dataMap.put("gameId", item.getUserId());

            } else {

                dataMap.put("gameId", user.getGameId());

            }

            dataMap.put("context", item.getContext());
            dataMap.put("id", item.getId());

            String replyContent = messageMap.get(item.getId());

            dataMap.put("replyContent", replyContent);

            dataList.add(dataMap);

        }

        Map<String, Object> result = new HashMap<>();

        result.put("totalNum", logCount);
        result.put("list", dataList);

        response.setData(result);

    }

    /**
     * 删除反馈列表
     */
    @GmHandler(key = "/osee/feedBack/delete")
    public void deleteFeedBack(Map<String, Object> paramMap, CommonResponse response) throws Exception {

        Long feedBackId = MapUtil.getLong(paramMap, "feedBackId");

        if (feedBackId == null) {
            return;
        }

        rechargeLogMapper.delete(feedBackId);

    }

    /**
     * 查询游走字幕
     */
    @GmHandler(key = "/osee/wander_subtitle/query")
    public void doWanderSubtitleQueryTask(Map<String, Object> params, CommonResponse response) throws Exception {
        long id = JsonMapUtils.parseObject(params, "subtitleId", JsonInnerType.TYPE_LONG);
        WanderSubtitleEntity subtitle = subtitleMapper.getById(id);
        if (subtitle == null) {
            response.setSuccess(false);
            response.setErrMsg("游走字幕不存在");
            return;
        }
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("id", subtitle.getId());
        resultMap.put("content", subtitle.getContent());
        resultMap.put("intervalTime", subtitle.getIntervalTime());
        // resultMap.put("effectiveTime", subtitle.getStartTime().getTime());
        // resultMap.put("failureTime", subtitle.getEndTime().getTime());
        response.setData(resultMap);
    }

    /**
     * 更新游走字幕数据
     */
    @GmHandler(key = "/osee/wander_subtitle/update")
    public void doWanderSubtitleUpdateTask(Map<String, Object> params, CommonResponse response) throws Exception {
        long id = (long) (double) params.get("id");
        WanderSubtitleEntity subtitle = subtitleManager.getWanderSubtitleById(id);
        if (subtitle == null) {
            response.setSuccess(false);
            response.setErrMsg("游走字幕不存在");
            return;
        }
        subtitle.setContent(JsonMapUtils.parseObject(params, "content", JsonInnerType.TYPE_STRING));
        subtitle.setIntervalTime(JsonMapUtils.parseObject(params, "intervalTime", JsonInnerType.TYPE_INT));
        subtitle.setStartTime(JsonMapUtils.parseObject(params, "effectiveTime", JsonInnerType.TYPE_DATE));
        subtitle.setEndTime(JsonMapUtils.parseObject(params, "failureTime", JsonInnerType.TYPE_DATE));
        subtitleMapper.update(subtitle);
        subtitleManager.addWanderSubtitle(subtitle);
    }

    /**
     * 删除游走字幕
     */
    @GmHandler(key = "/osee/wander_subtitle/delete")
    public void doWanderSubtitleDeleteTask(Map<String, Object> params, CommonResponse response) throws Exception {
        long id = JsonMapUtils.parseObject(params, "subtitleId", JsonInnerType.TYPE_LONG);
        subtitleMapper.delete(id);
        subtitleManager.removeWanderSubtitle(id);
    }

    /**
     * 添加游走字幕
     */
    @GmHandler(key = "/osee/wander_subtitle/add")
    public void doWanderSubtitleAddTask(Map<String, Object> params, CommonResponse response) throws Exception {
        WanderSubtitleEntity subtitle = new WanderSubtitleEntity();
        subtitle.setContent(JsonMapUtils.parseObject(params, "content", JsonInnerType.TYPE_STRING));
        subtitle.setIntervalTime(JsonMapUtils.parseObject(params, "intervalTime", JsonInnerType.TYPE_INT));
        subtitle.setStartTime(JsonMapUtils.parseObject(params, "effectiveTime", JsonInnerType.TYPE_DATE));
        subtitle.setEndTime(JsonMapUtils.parseObject(params, "failureTime", JsonInnerType.TYPE_DATE));
        subtitleMapper.save(subtitle);

        if (subtitle.getId() > 0) {
            subtitleManager.addWanderSubtitle(subtitle);
        } else {
            response.setSuccess(false);
        }
    }

    /**
     * 获取公告列表
     */
    @GmHandler(key = "/osee/notice/list")
    public void doNoticeListTask(Map<String, Object> params, CommonResponse response) throws Exception {

        List<OseeNoticeEntity> notices = noticeMapper.getAll();

        Map<String, Object> resultMap = new HashMap<>();

        List<Map<String, Object>> noticeItemMap = JsonMapUtils.objectsToMaps(notices);

        long nowTime = System.currentTimeMillis();

        for (int i = 0; i < notices.size(); i++) {
            int state = 0;
            OseeNoticeEntity notice = notices.get(i);
            if (notice.getStartTime().getTime() > nowTime || notice.getEndTime().getTime() < nowTime) {
                state = 1;
            }
            noticeItemMap.get(i).put("state", state);
        }

        resultMap.put("list", noticeItemMap);
        response.setData(resultMap);

    }

    /**
     * 根据ID查找公告
     */
    @GmHandler(key = "/osee/notice/query")
    public void doNoticeQueryTask(Map<String, Object> params, CommonResponse response) throws Exception {

        long id = JsonMapUtils.parseObject(params, "noticeId", JsonInnerType.TYPE_LONG);

        OseeNoticeEntity notice = noticeMapper.getById(id);
        if (notice == null) {
            response.setSuccess(false);
            response.setErrMsg("公告不存在");
            return;
        }

        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("id", notice.getId());
        resultMap.put("title", notice.getTitle());
        resultMap.put("content", notice.getContent());
        // resultMap.put("startTime", notice.getStartTime().getTime());
        // resultMap.put("endTime", notice.getEndTime().getTime());
        response.setData(resultMap);

    }

    /**
     * 更新公告
     */
    @GmHandler(key = "/osee/notice/update")
    public void doNoticeUpdateTask(Map<String, Object> params, CommonResponse response) throws Exception {

        OseeNoticeEntity notice = noticeMapper.getById((long) (double) params.get("id"));

        if (notice == null) {
            response.setSuccess(false);
            response.setErrMsg("公告不存在");
            return;
        }

        notice.setTitle((String) params.get("title"));
        notice.setContent((String) params.get("content"));
        notice.setStartTime(JsonMapUtils.parseObject(params, "startTime", JsonInnerType.TYPE_DATE));
        notice.setEndTime(JsonMapUtils.parseObject(params, "endTime", JsonInnerType.TYPE_DATE));
        noticeMapper.update(notice);
        commonLobbyManager.refreshNotice();

    }

    /**
     * 添加公告
     */
    @GmHandler(key = "/osee/notice/add")
    public void doNoticeAddTask(Map<String, Object> params, CommonResponse response) throws Exception {

        OseeNoticeEntity notice = new OseeNoticeEntity();
        notice.setIndex(Integer.MAX_VALUE);
        notice.setTitle(JsonMapUtils.parseObject(params, "title", JsonInnerType.TYPE_STRING));
        notice.setContent(JsonMapUtils.parseObject(params, "content", JsonInnerType.TYPE_STRING));
        notice.setStartTime(JsonMapUtils.parseObject(params, "startTime", JsonInnerType.TYPE_DATE));
        notice.setEndTime(JsonMapUtils.parseObject(params, "endTime", JsonInnerType.TYPE_DATE));

        noticeMapper.save(notice);
        if (notice.getId() <= 0) {
            response.setSuccess(false);
            return;
        }

        commonLobbyManager.refreshNotice();

    }

    /**
     * 删除公告
     */
    @GmHandler(key = "/osee/notice/delete")
    public void doNoticeDeleteTask(Map<String, Object> params, CommonResponse response) throws Exception {

        long id = JsonMapUtils.parseObject(params, "id", JsonInnerType.TYPE_LONG);
        noticeMapper.deleteById(id);
        commonLobbyManager.refreshNotice();

    }

    /**
     * 交换公告顺序
     */
    @GmHandler(key = "/osee/notice/change")
    public void doNoticeChangeTask(Map<String, Object> params, CommonResponse response) throws Exception {

        long id = JsonMapUtils.parseObject(params, "id", JsonInnerType.TYPE_LONG);
        int type = JsonMapUtils.parseObject(params, "type", JsonInnerType.TYPE_INT);

        if (!commonLobbyManager.changeNotice(id, type)) {
            response.setSuccess(false);
            response.setErrMsg("该项无法继续移动");
        }

    }

    /**
     * 获取游戏版本信息
     */
    @GmHandler(key = "/osee/game_version")
    public void doGameVersionTask(Map<String, Object> params, CommonResponse response) {
        Map<String, Object> resultMap = new HashMap<>();
        String version = lobbyManager.getServerVersion();
        resultMap.put("version", version);
        response.setData(resultMap);
    }

    /**
     * 修改游戏版本信息
     */
    @GmHandler(key = "/osee/game_version/update")
    public void doGameVersionUpdateTask(Map<String, Object> params, CommonResponse response) throws Exception {
        String version = JsonMapUtils.parseObject(params, "version", JsonInnerType.TYPE_STRING);
        lobbyManager.setServerVersion(version);
    }

    /**
     * 获取cdk列表任务
     */
    @GmHandler(key = "/osee/cdk/list")
    public void doCdkListTask(Map<String, Object> params, CommonResponse response) throws Exception {

        StringBuilder condBuilder = new StringBuilder("WHERE 1=1");
        StringBuilder pageBuilder = new StringBuilder();

        boolean agentGmFlag = BooleanUtil.isTrue(MapUtil.getBool(params, "agentGmFlag"));

        Long agentId = null;
        if (agentGmFlag) { // 如果是：代理后台进行查询

            agentId = MapUtil.getLong(params, "agentId");

            if (agentId == null) {
                response.setErrMsg("操作失败：参数非法");
                return;
            }

        }

        for (Entry<String, Object> entry : params.entrySet()) {
            String key = entry.getKey();
            switch (key) {
                case "cdkey":
                    String cdkey = JsonMapUtils.parseObject(params, "cdkey", JsonInnerType.TYPE_STRING);
                    if (!StringUtils.isEmpty(cdkey)) {
                        condBuilder.append(" AND cdkey = '").append(cdkey).append("'");
                    }
                    break;

                case "typeId":
                    long typeId = JsonMapUtils.parseObject(params, "typeId", JsonInnerType.TYPE_LONG);
                    if (typeId > 0) {
                        condBuilder.append(" AND type_id = ").append(typeId);
                    }
                    break;

                case "agentId":

                    if (agentId != null && agentId >= 0) {
                        condBuilder.append(" AND agent_id = ").append(agentId);
                    }
                    break;

                case "agentGameId":

                    Long agentGameId = MapUtil.getLong(params, "agentGameId");
                    if (agentGameId != null) {
                        condBuilder.append(" AND agent_game_id = ").append(agentGameId);
                    }
                    break;

                case "userGameId":

                    Long userGameId = MapUtil.getLong(params, "userGameId");
                    if (userGameId != null) {
                        condBuilder.append(" AND user_game_id = ").append(userGameId);
                    }
                    break;

                case "used":
                    int used = JsonMapUtils.parseObject(params, "used", JsonInnerType.TYPE_INT);
                    if (used == 1) {
                        condBuilder.append(" AND user_id > 0");
                    } else if (used == 2) {
                        condBuilder.append(" AND user_id = 0");
                    }
                    break;

                case "page":
                case "pageSize":
                    if (pageBuilder.length() <= 0) {
                        // 解析分页数据
                        int page = JsonMapUtils.parseObject(params, "page", JsonInnerType.TYPE_INT);
                        int pageSize = JsonMapUtils.parseObject(params, "pageSize", JsonInnerType.TYPE_INT);
                        pageBuilder.append(" LIMIT ").append((page - 1) * pageSize).append(",").append(pageSize);
                    }
                    break;
            }
        }

        List<GmCdkInfo> cdkInfoList = gmCommonMapper.getCdkInfoList(condBuilder.toString(), pageBuilder.toString());
        int total = gmCommonMapper.getCdkInfoCount(condBuilder.toString());

        for (GmCdkInfo item : cdkInfoList) {

            String rewards = item.getRewards();

            List<Map> rewardMapList = JSONUtil.toList(rewards, Map.class);

            StringBuilder stringBuilder = new StringBuilder();

            for (Map subItem : rewardMapList) {

                String name = ItemId.getItemIdById((int) Double.parseDouble(subItem.get("itemId").toString())).getInfo();

                stringBuilder.append(name).append("*").append((int) Double.parseDouble(subItem.get("count").toString()))
                        .append(" ");

            }

            item.setRewards(stringBuilder.toString()); // 重新：设置奖励信息

        }

        Map<String, Object> resultMap = new HashMap<>();

        if (agentGmFlag) { // 如果是：代理后台进行查询

            Set<GmCdkTypeInfo> typeInfoSet = cdkManager.getCdkTypeListByAgentId(agentId);

            resultMap.put("cdkTypeList", typeInfoSet);

        }

        resultMap.put("totalNum", total);
        resultMap.put("list", cdkInfoList);
        response.setData(resultMap);

    }

    /**
     * 添加cdk任务
     */
    @GmHandler(key = "/osee/cdk/add")
    public void doCdkAddTask(Map<String, Object> params, CommonResponse response) throws Exception {

        String rewards = JsonMapUtils.parseObject(params, "rewards", JsonInnerType.TYPE_STRING);
        long typeId = JsonMapUtils.parseObject(params, "typeId", JsonInnerType.TYPE_LONG);
        int count = JsonMapUtils.parseObject(params, "count", JsonInnerType.TYPE_INT);

        Long agentGameId = MapUtil.getLong(params, "agentGameId");

        String errorMsg = cdkManager.createCdk(typeId, count, rewards, agentGameId);

        if (StrUtil.isNotBlank(errorMsg)) {

            response.setSuccess(false);
            response.setErrCode("ERROR_UNKNOWN");
            response.setErrMsg(errorMsg);

        }

    }

    /**
     * 删除cdk任务
     */
    @GmHandler(key = "/osee/cdk/delete")
    public void doCdkDeleteTask(Map<String, Object> params, CommonResponse response) throws Exception {
        long typeId = JsonMapUtils.parseObject(params, "typeId", JsonInnerType.TYPE_LONG);
        cdkManager.deleteCdk(typeId);
    }

    /**
     * 获取cdk类型列表任务
     */
    @GmHandler(key = "/osee/cdk_type/list")
    public void doCdkTypeListTask(Map<String, Object> params, CommonResponse response) throws Exception {
        List<OseeCdkTypeEntity> entities = cdkManager.getCdkTypes();
        List<GmCdkTypeInfo> typeInfos = new LinkedList<>();
        for (OseeCdkTypeEntity entity : entities) {
            GmCdkTypeInfo typeInfo = new GmCdkTypeInfo();
            typeInfo.setId(entity.getId());
            typeInfo.setName(entity.getName());
            typeInfos.add(typeInfo);
        }

        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("list", typeInfos);
        response.setData(resultMap);
    }

    /**
     * 添加cdk类型任务
     */
    @GmHandler(key = "/osee/cdk_type/add")
    public void doCdkTypeAddTask(Map<String, Object> params, CommonResponse response) throws Exception {
        String name = JsonMapUtils.parseObject(params, "name", JsonInnerType.TYPE_STRING);
        String errMsg = cdkManager.createCdkType(name);
        if (!StringUtils.isEmpty(errMsg)) {
            response.setSuccess(false);
            response.setErrMsg(errMsg);
        }
    }

    /**
     * 添加支付订单
     */
    @GmHandler(key = "/osee/pay_order/add")
    public void doPayOrderAddTask(Map<String, Object> params, CommonResponse response) {

        long todayRecharge = rechargeLogMapper.getTodayRecharge((long) (double) params.get("playerId")) / 100;

        if (todayRecharge >= 3000) {

            response.setSuccess(false);
            response.setErrMsg("您今日消费已达上限3000元，请次日充值！");
            return;

        }

        String orderNum = params.get("orderNum").toString();

        RedissonUtil.doLock("PAY_ORDER_ADD:" + orderNum, () -> {

            OseeRechargeLogEntity log = rechargeLogMapper.get(orderNum);

            if (log == null) {

                log = new OseeRechargeLogEntity();

                log.setOrderNum(orderNum);
                log.setUserId((long) (double) params.get("playerId"));
                log.setPayMoney((long) (double) params.get("payMoney"));
                log.setShopName(params.get("shopName").toString());
                log.setShopType((int) (double) params.get("shopType"));
                log.setCount((int) (double) params.get("shopCount"));
                log.setCreator("第三方");
                log.setRechargeType((int) (double) params.get("rechargeType"));

                ServerUser user = UserContainer.getUserById(log.getUserId());
                log.setNickname(user.getNickname());

                rechargeLogMapper.save(log);

            } else {

                response.setSuccess(false);
                response.setErrMsg("订单号已存在");

            }

        });

    }

    /**
     * 添加支付订单
     */
    @GmHandler(key = "/osee/pay_order/hwadd")
    public void doHuaweiPayOrderAddTask(Map<String, Object> params, CommonResponse response) {
        String orderNum = params.get("orderNum").toString();
        OseeRechargeLogEntity log = rechargeLogMapper.get(orderNum);
        if (log == null) {
            log = new OseeRechargeLogEntity();
            log.setOrderNum(orderNum);
            log.setUserId((long) (double) params.get("playerId"));
            log.setPayMoney((long) (double) params.get("payMoney"));
            log.setShopName(params.get("shopName").toString());
            log.setShopType((int) (double) params.get("shopType"));
            log.setCount((int) (double) params.get("shopCount"));
            log.setCreator("华为");
            log.setRechargeType(8);
            ServerUser user = UserContainer.getUserById(log.getUserId());
            log.setNickname(user.getNickname());

            // 支付成功
            if ("0".equals(params.get("result"))) {
                log.setOrderState(1);
                rechargeLogMapper.save(log);
                OseePlayerEntity entity = PlayerManager.getPlayerEntity(user);

                // VIP5及以上充值金币会额外赠送10%
                if (log.getShopType() == ItemId.MONEY.getId() && entity.getVipLevel() >= 5) {
                    log.setCount((int) (log.getCount() + log.getCount() * 0.1));
                }

                // 记录玩家充值的金钱数
                entity.setRechargeMoney(entity.getRechargeMoney() + log.getPayMoney() / 100);
                // 根据充值的金钱计算玩家的vip等级
                entity.setVipLevel(PlayerManager.getPlayerVipLevel(entity));

                // 给玩家加对应购买的物品
                PlayerManager.addItem(user, log.getShopType(), log.getCount(), ItemChangeReason.THIRD_PARTY_RECHARGE,
                        true);

                // 如果是购买的月卡
                if (log.getShopType() == ItemId.MONTH_CARD.getId()) {
                    // 赠送购买礼包 30颗钻石、10万金币、自动开炮30天
                    List<ItemData> itemDataList = Arrays.asList(new ItemData(ItemId.DIAMOND.getId(), 30),
                            new ItemData(ItemId.MONEY.getId(), 200000));
                    PlayerManager.addItems(user, itemDataList, ItemChangeReason.MONTH_CARD, true);

                    if (user.isOnline()) { // 通知给用户
                        // 发送礼包赠送响应
                        OseeLobbyMessage.BuyMonthCardRewardsResponse.Builder builder =
                                OseeLobbyMessage.BuyMonthCardRewardsResponse.newBuilder();
                        for (ItemData itemData : itemDataList) {
                            builder.addRewards(OseePublicData.ItemDataProto.newBuilder().setItemId(itemData.getItemId())
                                    .setItemNum(itemData.getCount()).build());
                        }
                        // 放入自动开炮 自定义自动开炮物品的ID为100
                        builder.addRewards(
                                OseePublicData.ItemDataProto.newBuilder().setItemId(100).setItemNum(30).build());
                        NetManager.sendMessage(OseeMessage.OseeMsgCode.S_C_TTMY_BUY_MONTH_CARD_REWARDS_RESPONSE_VALUE,
                                builder, user);
                    }
                    // 发送月卡每日奖励
                    commonLobbyManager.sendDailyMonthCardRewards(user);
                }

                if ((long) (double) params.get("payMoney") / 100 >= 8) {
                    // 判断6元首充是否有效
                    String key = String.format(CommonLobbyManager.FIRST_CHARGE_KEY_NAMESPACE, user.getId());
                    String value = RedisHelper.get(key);
                    if (StringUtils.isEmpty(value)) { // 还没有首充
                        // 首充赠送的大礼包 12颗钻石、2万金币、30张锁定卡、20张冰冻卡、20张急速卡、10张暴击卡、3天月卡体验
                        List<ItemData> itemDataList = Arrays.asList(new ItemData(ItemId.DIAMOND.getId(), 12),
                                new ItemData(ItemId.MONEY.getId(), 600000), new ItemData(ItemId.SKILL_LOCK.getId(), 30),
                                new ItemData(ItemId.SKILL_FROZEN.getId(), 20), new ItemData(ItemId.SKILL_FAST.getId(), 20),
                                new ItemData(ItemId.SKILL_CRIT.getId(), 10), new ItemData(ItemId.MONTH_CARD.getId(), 3));
                        PlayerManager.addItems(user, itemDataList, ItemChangeReason.FIRST_ADDMONEY, true);
                        // 保存首充记录
                        RedisHelper.set(key, "￥" + (long) (double) params.get("payMoney") / 100);

                        if (user.isOnline()) { // 通知给用户
                            // 发送礼包赠送响应
                            OseeLobbyMessage.FirstChargeRewardsResponse.Builder builder =
                                    OseeLobbyMessage.FirstChargeRewardsResponse.newBuilder();
                            for (ItemData itemData : itemDataList) {
                                builder.addRewards(OseePublicData.ItemDataProto.newBuilder()
                                        .setItemId(itemData.getItemId()).setItemNum(itemData.getCount()).build());
                            }
                            NetManager.sendMessage(OseeMessage.OseeMsgCode.S_C_TTMY_FIRST_CHARGE_REWARDS_RESPONSE_VALUE,
                                    builder, user);
                        }
                    }
                }
                rechargeLogMapper.updateOrderState(1, log.getId());

                try {
                    // 计算玩家是否有代理，然后要计算代理佣金，被禁用的代理无法获取返利
                    long userId = user.getId();
                    AgentEntity agentEntity = agentMapper.getByPlayerId(userId);
                } catch (Exception e) {
                    logger.error("玩家重置处理代理佣金出错");
                    e.printStackTrace();
                }
            } else {
                log.setOrderState(2);
                rechargeLogMapper.save(log);
            }
        } else {
            response.setSuccess(false);
            response.setErrMsg("订单号已存在");
        }
    }

    /**
     * 修改支付订单状态
     */
    @GmHandler(key = "/osee/pay_order/update")
    public void doPayOrderUpdateTask(Map<String, Object> params, CommonResponse response) {

        String orderNum = params.get("orderNum").toString();
        int orderState = (int) (double) params.get("orderState");

        RedissonUtil.doLock("doPayOrderUpdateTask:" + orderNum, () -> {

            OseeRechargeLogEntity log = rechargeLogMapper.get(orderNum);

            if (log == null) {

                response.setSuccess(false);
                response.setErrMsg("订单号不存在");

            } else if (log.getOrderState() != 0) {

                if (log.getOrderState() != orderState) {

                    response.setSuccess(false);
                    response.setErrMsg("订单当前状态不可编辑:" + log.getOrderState());

                }

            } else {

                // 实际支付的金额：元
                BigDecimal payMoney = new BigDecimal("" + params.get("payMoney"));

                // if (new BigDecimal(log.getPayMoney()).compareTo(payMoney.multiply(new BigDecimal("100"))) != 0) {
                //
                // response.setSuccess(false);
                // response.setErrMsg("订单金额不匹配:" + payMoney);
                // return;
                //
                // }

                if (orderState == 1) { // 支付成功
                    ServerUser user = UserContainer.getUserById(log.getUserId());

                    OseePlayerEntity entity = PlayerManager.getPlayerEntity(user);

                    // VIP5及以上充值金币会额外赠送10%
                    if (log.getShopType() == ItemId.MONEY.getId() && entity.getVipLevel() >= 5) {

                        log.setCount((int) (log.getCount() + log.getCount() * 0.1));

                    }

                    if (log.getShopType() == ItemId.MONEY.getId()) {

                        if (RedisUtil.val("USER_T_STATUS" + user.getId(), 0L) != 0) {

                            long a = RedisUtil.val("USER_T_BANKRUPTCY_NUMBER" + user.getId(), 0L);
                            RedisHelper.set("USER_T_BANKRUPTCY_NUMBER" + user.getId(),
                                    String.valueOf(a + log.getCount()));

                        } else {

                            // int x = CommonLobbyManager.getUserT(user,1);
                            // RedisHelper.set("USER_T_PEAK_VALUE"+user.getId(),String.valueOf(x));
                            RedisHelper.set("USER_T_BANKRUPTCY_NUMBER" + user.getId(), String.valueOf(log.getCount()));

                        }

                    }

                    // 记录玩家充值的金钱数
                    // entity.setRechargeMoney(entity.getRechargeMoney() + log.getPayMoney() / 100);
                    // 根据充值的金钱计算玩家的vip等级
                    // entity.setVipLevel(PlayerManager.getPlayerVipLevel(entity));

                    int firstBuyMoney8 = RedisUtil.val("FIRST_BUY_MONEY_8" + user.getId(), 0);
                    int firstBuyMoney18 = RedisUtil.val("FIRST_BUY_MONEY_18" + user.getId(), 0);
                    int firstBuyMoney68 = RedisUtil.val("FIRST_BUY_MONEY_68" + user.getId(), 0);
                    int firstBuyMoney128 = RedisUtil.val("FIRST_BUY_MONEY_128" + user.getId(), 0);
                    int firstBuyMoney268 = RedisUtil.val("FIRST_BUY_MONEY_268" + user.getId(), 0);
                    int firstBuyMoney495 = RedisUtil.val("FIRST_BUY_MONEY_495" + user.getId(), 0);
                    int firstBuyMoney568 = RedisUtil.val("FIRST_BUY_MONEY_568" + user.getId(), 0);
                    int firstBuyMoney698 = RedisUtil.val("FIRST_BUY_MONEY_698" + user.getId(), 0);

                    if (log.getShopType() == ItemId.MONEY.getId()) {
                        if (log.getCount() == 160000) {

                            if (firstBuyMoney8 == 0) {
                                log.setCount(log.getCount() * 2);
                                RedisHelper.set("FIRST_BUY_MONEY_8" + user.getId(), "1");
                            }

                        } else if (log.getCount() == 360000) {

                            if (firstBuyMoney18 == 0) {
                                log.setCount(log.getCount() * 2);
                                RedisHelper.set("FIRST_BUY_MONEY_18" + user.getId(), "1");
                            }

                        } else if (log.getCount() == 1360000) {

                            if (firstBuyMoney68 == 0) {
                                log.setCount(log.getCount() * 2);
                                RedisHelper.set("FIRST_BUY_MONEY_68" + user.getId(), "1");
                            }

                        } else if (log.getCount() == 2560000) {

                            if (firstBuyMoney128 == 0) {
                                log.setCount(log.getCount() * 2);
                                RedisHelper.set("FIRST_BUY_MONEY_128" + user.getId(), "1");
                            }

                        } else if (log.getCount() == 5360000) {

                            if (firstBuyMoney268 == 0) {
                                log.setCount(log.getCount() * 2);
                                RedisHelper.set("FIRST_BUY_MONEY_268" + user.getId(), "1");
                            } else {
                                log.setCount(log.getCount() + 268000);
                            }

                        } else if (log.getCount() == 9900000) {

                            if (firstBuyMoney495 == 0) {
                                log.setCount(log.getCount() * 2);
                                RedisHelper.set("FIRST_BUY_MONEY_495" + user.getId(), "1");
                            } else {
                                log.setCount(log.getCount() + 792000);
                            }

                        } else if (log.getCount() == 11360000) {

                            if (firstBuyMoney568 == 0) {
                                log.setCount(log.getCount() * 2);
                                RedisHelper.set("FIRST_BUY_MONEY_568" + user.getId(), "1");
                            } else {
                                log.setCount(log.getCount() + 1136000);
                            }

                        } else if (log.getCount() == 13960000) {

                            if (firstBuyMoney698 == 0) {
                                log.setCount(log.getCount() * 2);
                                RedisHelper.set("FIRST_BUY_MONEY_698" + user.getId(), "1");
                            } else {
                                log.setCount(log.getCount() + 2094000);
                            }

                        }

                    }

                    int firstBuyDiamond8 = RedisUtil.val("FIRST_BUY_DIAMOND_8" + user.getId(), 0);
                    int firstBuyDiamond18 = RedisUtil.val("FIRST_BUY_DIAMOND_18" + user.getId(), 0);
                    int firstBuyDiamond68 = RedisUtil.val("FIRST_BUY_DIAMOND_68" + user.getId(), 0);
                    int firstBuyDiamond168 = RedisUtil.val("FIRST_BUY_DIAMOND_168" + user.getId(), 0);
                    int firstBuyDiamond258 = RedisUtil.val("FIRST_BUY_DIAMOND_258" + user.getId(), 0);
                    int firstBuyDiamond498 = RedisUtil.val("FIRST_BUY_DIAMOND_498" + user.getId(), 0);
                    int firstBuyDiamond568 = RedisUtil.val("FIRST_BUY_DIAMOND_568" + user.getId(), 0);
                    int firstBuyDiamond698 = RedisUtil.val("FIRST_BUY_DIAMOND_698" + user.getId(), 0);

                    if (log.getShopType() == ItemId.DIAMOND.getId()) {

                        if (log.getCount() == 80) {

                            if (firstBuyDiamond8 == 0) {
                                log.setCount(log.getCount() + log.getCount() / 5);
                                RedisHelper.set("FIRST_BUY_DIAMOND_8" + user.getId(), "1");
                            }

                        } else if (log.getCount() == 180) {

                            if (firstBuyDiamond18 == 0) {
                                log.setCount(log.getCount() + log.getCount() / 5);
                                RedisHelper.set("FIRST_BUY_DIAMOND_18" + user.getId(), "1");
                            }

                        } else if (log.getCount() == 680) {

                            if (firstBuyDiamond68 == 0) {
                                log.setCount(log.getCount() + log.getCount() / 5);
                                RedisHelper.set("FIRST_BUY_DIAMOND_68" + user.getId(), "1");
                            }

                        } else if (log.getCount() == 1680) {

                            if (firstBuyDiamond168 == 0) {
                                log.setCount(log.getCount() + log.getCount() / 5);
                                RedisHelper.set("FIRST_BUY_DIAMOND_168" + user.getId(), "1");
                            }

                        } else if (log.getCount() == 2580) {

                            if (firstBuyDiamond258 == 0) {
                                log.setCount(log.getCount() + log.getCount() / 5);
                                RedisHelper.set("FIRST_BUY_DIAMOND_258" + user.getId(), "1");
                            }

                        } else if (log.getCount() == 4980) {

                            if (firstBuyDiamond498 == 0) {
                                log.setCount(log.getCount() + log.getCount() / 5);
                                RedisHelper.set("FIRST_BUY_DIAMOND_498" + user.getId(), "1");
                            } else {
                                log.setCount(log.getCount() + 249);
                            }

                        } else if (log.getCount() == 5680) {

                            if (firstBuyDiamond568 == 0) {
                                log.setCount(log.getCount() + log.getCount() / 5);
                                RedisHelper.set("FIRST_BUY_DIAMOND_568" + user.getId(), "1");
                            } else {
                                log.setCount(log.getCount() + 568);
                            }

                        } else if (log.getCount() == 6980) {

                            if (firstBuyDiamond698 == 0) {
                                log.setCount(log.getCount() + log.getCount() / 5);
                                RedisHelper.set("FIRST_BUY_DIAMOND_698" + user.getId(), "1");
                            } else {
                                log.setCount(log.getCount() + 698);
                            }

                        }

                    }

                    // 给玩家加对应购买的物品
                    PlayerManager.addItem(user, log.getShopType(), log.getCount(),
                            ItemChangeReason.THIRD_PARTY_RECHARGE, true);

                    // 如果是购买的月卡
                    if (log.getShopType() == ItemId.MONTH_CARD.getId()) {

                        // 赠送购买礼包 30颗钻石、10万金币、自动开炮30天
                        List<ItemData> itemDataList = Arrays.asList(new ItemData(ItemId.DIAMOND.getId(), 28),
                                new ItemData(ItemId.MONEY.getId(), 580000), new ItemData(ItemId.YU_GU.getId(), 16));

                        if (RedisUtil.val("USER_T_STATUS" + user.getId(), 0L) != 0) {

                            long a = RedisUtil.val("USER_T_BANKRUPTCY_NUMBER" + user.getId(), 0L);
                            RedisHelper.set("USER_T_BANKRUPTCY_NUMBER" + user.getId(), String.valueOf(a + 580000));

                        } else {

                            // int x = CommonLobbyManager.getUserT(user,1);
                            // RedisHelper.set("USER_T_PEAK_VALUE"+user.getId(),String.valueOf(x));
                            RedisHelper.set("USER_T_BANKRUPTCY_NUMBER" + user.getId(), String.valueOf(580000));

                        }

                        PlayerManager.addItems(user, itemDataList, ItemChangeReason.MONTH_CARD, true);

                        if (user.isOnline()) { // 通知给用户

                            // 发送礼包赠送响应
                            OseeLobbyMessage.BuyMonthCardRewardsResponse.Builder builder =
                                    OseeLobbyMessage.BuyMonthCardRewardsResponse.newBuilder();
                            for (ItemData itemData : itemDataList) {
                                builder.addRewards(OseePublicData.ItemDataProto.newBuilder()
                                        .setItemId(itemData.getItemId()).setItemNum(itemData.getCount()).build());
                            }

                            // 放入自动开炮 自定义自动开炮物品的ID为100
                            builder.addRewards(
                                    OseePublicData.ItemDataProto.newBuilder().setItemId(100).setItemNum(30).build());
                            NetManager.sendMessage(
                                    OseeMessage.OseeMsgCode.S_C_TTMY_BUY_MONTH_CARD_REWARDS_RESPONSE_VALUE, builder, user);

                        }

                        // 发送月卡每日奖励
                        commonLobbyManager.sendDailyMonthCardRewards(user);

                    }

                    if (payMoney.longValue() / 100 >= 8) {

                        // 判断8元首充是否有效
                        String key = String.format(CommonLobbyManager.FIRST_CHARGE_KEY_NAMESPACE, user.getId());
                        String value = RedisHelper.get(key);

                        if (StringUtils.isEmpty(value)) { // 还没有首充

                            // 首充赠送的大礼包 急速//分身//电磁炮//黑洞炮//金币//龙晶//鱼骨//月卡体验
                            // 18个//8个 //4个 //2个 //6W //1W //8个//3天
                            List<ItemData> itemDataList = Arrays.asList(new ItemData(ItemId.FEN_SHEN.getId(), 10),
                                    new ItemData(ItemId.SKILL_ELETIC.getId(), 2),
                                    new ItemData(ItemId.SKILL_LOCK.getId(), 10), new ItemData(ItemId.MONEY.getId(), 60000),
                                    new ItemData(ItemId.SKILL_FAST.getId(), 18),
                                    new ItemData(ItemId.DRAGON_CRYSTAL.getId(), 10000),
                                    new ItemData(ItemId.YU_GU.getId(), 8), new ItemData(ItemId.MONTH_CARD.getId(), 3));

                            PlayerManager.addItems(user, itemDataList, ItemChangeReason.FIRST_ADDMONEY, true);

                            // 保存首充记录
                            RedisHelper.set(key, "￥" + payMoney.longValue() / 100);

                            if (RedisUtil.val("USER_T_STATUS" + user.getId(), 0L) != 0) {

                                long a = RedisUtil.val("USER_T_BANKRUPTCY_NUMBER" + user.getId(), 0L);
                                RedisHelper.set("USER_T_BANKRUPTCY_NUMBER" + user.getId(), String.valueOf(a + 60000));

                            } else {

                                // int x = CommonLobbyManager.getUserT(user,1);
                                // RedisHelper.set("USER_T_PEAK_VALUE"+user.getId(),String.valueOf(x));
                                RedisHelper.set("USER_T_BANKRUPTCY_NUMBER" + user.getId(), String.valueOf(60000));

                            }

                            if (user.isOnline()) { // 通知给用户

                                // 发送礼包赠送响应
                                OseeLobbyMessage.FirstChargeRewardsResponse.Builder builder =
                                        OseeLobbyMessage.FirstChargeRewardsResponse.newBuilder();
                                for (ItemData itemData : itemDataList) {
                                    builder.addRewards(OseePublicData.ItemDataProto.newBuilder()
                                            .setItemId(itemData.getItemId()).setItemNum(itemData.getCount()).build());
                                }
                                NetManager.sendMessage(
                                        OseeMessage.OseeMsgCode.S_C_TTMY_FIRST_CHARGE_REWARDS_RESPONSE_VALUE, builder,
                                        user);

                            }

                        }

                    }

                    rechargeLogMapper.updateOrderState(orderState, log.getId());
                }

            }

        });

    }


    /**
     * 客服信息
     */
    @GmHandler(key = "/osee/support")
    public void doSupportTask(Map<String, Object> params, CommonResponse response) {
        @SuppressWarnings("unchecked")
        Map<String, Object> resultMap = (Map<String, Object>) response.getData();
        resultMap.put("wechat", commonLobbyManager.getSupportWechat());
        resultMap.put("qrcode", commonLobbyManager.getSupportQRCode());
    }

    /**
     * 设置客服信息
     */
    @GmHandler(key = "/osee/support/update")
    public void doSupportUpdateTask(Map<String, Object> params, CommonResponse response) {
        commonLobbyManager.setSupportWechat(params.get("wechat").toString());
        commonLobbyManager.setSupportQRCode(params.get("qrcode").toString());
    }


    public static final String GIFT_TOTAL_NUM_COND_BUILDER_STR =
            " AND (b.state + (b.receive * 100)) != 2 AND (b.state + (b.receive * 100)) != 4 ";


    /**
     * 获取玩家背包物品信息
     */
    @GmHandler(key = "/ttmy/player/package/info")
    public void doPlayerPackageInfoTask(Map<String, Object> params, CommonResponse response) {

        long playerId = (long) (double) params.get("playerId");

        ServerUser user = UserContainer.getUserById(playerId);

        if (user == null) {
            response.setSuccess(false);
            response.setErrMsg("玩家不存在！");
            return;
        }

        List<String> data = new LinkedList<>();
        for (ItemId itemId : ItemId.values()) {
            if ((itemId.getId() >= ItemId.GOLD_TORPEDO.getId() && itemId.getId() <= ItemId.SKILL_CRIT.getId())
                    || itemId.getId() == ItemId.BOSS_BUGLE.getId() || (itemId.getId() == ItemId.FEN_SHEN.getId())) {

                data.add(itemId.getInfo() + "*" + PlayerManager.getItemNum(user, itemId));

            }
        }

        response.setData(data);

    }

    /**
     * 获取玩家背包物品信息
     */
    @GmHandler(key = "/ttmy/player/cx/info")
    public void doPlayerCxInfoTask(Map<String, Object> params, CommonResponse response) {
        long playerId = (long) (double) params.get("playerId");
        ServerUser user = UserContainer.getUserById(playerId);
        if (user == null) {
            response.setSuccess(false);
            response.setErrMsg("玩家不存在！");
            return;
        }
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("cx",
                RedisUtil.val("ALL_CX_USER" + user.getId(), 0D) + RedisUtil.val("ALL_CX_USER_NEW" + user.getId(), 0D));
        resultMap.put("xh1", RedisUtil.val("ALL_XH_1-50" + user.getId(), 0D));
        resultMap.put("xh2", RedisUtil.val("ALL_XH_50-100" + user.getId(), 0D));
        resultMap.put("xh3", RedisUtil.val("ALL_XH_100-200" + user.getId(), 0D));
        resultMap.put("xh4", RedisUtil.val("ALL_XH_200-max" + user.getId(), 0D));
        resultMap.put("tMax", RedisUtil.val("USER_T_MAX" + user.getId(), 0));
        resultMap.put("tMin", RedisUtil.val("USER_T_MIN" + user.getId(), 0));
        resultMap.put("tMaxNew", RedisUtil.val("USER_T_MAX_NEW" + user.getId(), 0));
        resultMap.put("tMinNew", RedisUtil.val("USER_T_MIN_NEW" + user.getId(), 0));
        resultMap.put("cxChallenge", RedisUtil.val("ALL_CX_USER_CHALLANGE" + user.getId(), 0D));
        resultMap.put("xh1Challenge", RedisUtil.val("ALL_XH_CHALLANGE_1_" + user.getId(), 0D));
        resultMap.put("xh2Challenge", RedisUtil.val("ALL_XH_CHALLANGE_2_" + user.getId(), 0D));
        resultMap.put("xh3Challenge", RedisUtil.val("ALL_XH_CHALLANGE_3_" + user.getId(), 0D));
        resultMap.put("xh4Challenge", RedisUtil.val("ALL_XH_CHALLANGE_4_" + user.getId(), 0D));
        response.setData(resultMap);
    }

    /**
     * 龙晶兑换记录明细
     */
    @GmHandler(key = "/ttmy/money/log/crystal/exchange")
    public void doDragonCrystalExchangeLogTask(Map<String, Object> params, CommonResponse response) throws Exception {
        // 条件语句构建
        StringBuilder condBuilder = new StringBuilder(" where 1=1");

        for (Entry<String, Object> entry : params.entrySet()) {
            String key = entry.getKey();
            switch (key) {
                case "startTime":
                    long startTime = JsonMapUtils.parseObject(params, "startTime", JsonInnerType.TYPE_LONG);
                    if (startTime > 0) {
                        Date startDate = new Date(startTime);
                        condBuilder.append(" AND create_time >= '").append(DATE_FORMATER.format(startDate)).append("'");
                    }
                    break;
                case "endTime":
                    long endTime = JsonMapUtils.parseObject(params, "endTime", JsonInnerType.TYPE_LONG);
                    if (endTime > 0) {
                        Date endDate = new Date(endTime);
                        condBuilder.append(" AND create_time <= '").append(DATE_FORMATER.format(endDate)).append("'");
                    }
                    break;
                case "playerId":
                    long playerId = JsonMapUtils.parseObject(params, "playerId", JsonInnerType.TYPE_LONG);
                    if (playerId > 0) {
                        condBuilder.append(" AND player_id = ").append(playerId);
                    }
                    break;
                case "exchangeType":
                    int exchangeType = JsonMapUtils.parseObject(params, "exchangeType", JsonInnerType.TYPE_INT);
                    if (exchangeType >= 0) {
                        condBuilder.append(" AND exchange_type = ").append(exchangeType);
                    }
                    break;
            }
        }
        condBuilder.append(" order by create_time desc");

        // 数据总条数
        long count = crystalExchangeLogMapper.getCount(condBuilder.toString());
        // 解析分页数据
        int pageNo = JsonMapUtils.parseObject(params, "page", JsonInnerType.TYPE_INT);
        int pageSize = JsonMapUtils.parseObject(params, "pageSize", JsonInnerType.TYPE_INT);
        condBuilder.append(" limit ").append((pageNo - 1) * pageSize).append(",").append(pageSize);
        // 开始获取数据
        List<CrystalExchangeLogEntity> list = crystalExchangeLogMapper.getList(condBuilder.toString());

        List<Map> dataList = new LinkedList<>();
        for (CrystalExchangeLogEntity entity : list) {
            Map<String, Object> data = new HashMap<>();
            data.put("playerId", entity.getPlayerId());
            data.put("playerName", UserContainer.getUserById(entity.getPlayerId()).getNickname());
            data.put("exchangeType", entity.getExchangeType());
            String torpedo = "青*%d 银*%d 金*%d";
            data.put("torpedoBefore", String.format(torpedo, entity.getBronzeTorpedoBefore(),
                    entity.getSilverTorpedoBefore(), entity.getGoldTorpedoBefore()));
            data.put("torpedoChange", String.format(torpedo, entity.getBronzeTorpedoChange(),
                    entity.getSilverTorpedoChange(), entity.getGoldTorpedoChange()));
            data.put("torpedoAfter",
                    String.format(torpedo, entity.getBronzeTorpedoBefore() + entity.getBronzeTorpedoChange(),
                            entity.getSilverTorpedoBefore() + entity.getSilverTorpedoChange(),
                            entity.getGoldTorpedoBefore() + entity.getGoldTorpedoChange()));
            data.put("crystalBefore", entity.getDragonCrystalBefore());
            data.put("crystalChange", entity.getDragonCrystalChange());
            data.put("crystalAfter", entity.getDragonCrystalBefore() + entity.getDragonCrystalChange());
            data.put("createTime", entity.getCreateTime());
            dataList.add(data);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("totalNum", count);
        result.put("list", dataList);
        response.setData(result);
    }

    /**
     * 更改玩家vip等级
     */
    @GmHandler(key = "/ttmy/player/vip/change")
    public void doChangePlayerVipLevelTask(Map<String, Object> params, CommonResponse response) {
        long playerId = (long) (double) params.get("playerId");
        int vipLevel = (int) (double) params.get("vipLevel");
        ServerUser user = UserContainer.getUserById(playerId);
        if (user == null) {
            response.setSuccess(false);
            response.setErrMsg("玩家ID有误");
            return;
        }
        long[] vipMoney = PlayerManager.VIP_MONEY;
        if (vipLevel < 0 || vipLevel > vipMoney.length) {
            response.setSuccess(false);
            response.setErrMsg("输入的VIP等级有误");
            return;
        }
        OseePlayerEntity playerEntity = PlayerManager.getPlayerEntity(user);
        playerEntity.setVipLevel(vipLevel); // 设置等级
        playerEntity.setRechargeMoney(vipLevel == 0 ? 0 : vipMoney[vipLevel - 1] + 1); // 设置充值的金额为对应等级之上
        playerMapper.update(playerEntity);
        PlayerManager.sendVipLevelResponse(user);
    }


    /**
     * 发送系统邮件
     */
    @GmHandler(key = "/ttmy/send_mail")
    public void sendMail(Map<String, Object> params, CommonResponse response) {

        // 有值就是个人发送，否在全服发送
        long toId = params.containsKey("receiverId") ? (long) (double) params.get("receiverId") : 0;

        MessageEntity messageEntity = new MessageEntity();

        // 系统邮件发送ID为-1
        messageEntity.setFromId(-1L);
        messageEntity.setFromGameId(-1L);

        if (toId == 0) {

            messageEntity.setToId(toId);
            messageEntity.setToGameId(toId);

        } else {

            // 通过：gameId获取 user
            ServerUser serverUser = GameUtil.getServerUserByGameId(toId);

            if (serverUser == null) {

                response.setSuccess(false);
                response.setErrMsg("玩家ID不存在");
                return;

            }

            messageEntity.setToId(serverUser.getId());
            messageEntity.setToGameId(serverUser.getGameId());

        }

        messageEntity.setTitle(params.get("title").toString());
        messageEntity.setContent(params.get("content").toString());

        messageEntity.setType(MapUtil.getInt(params, "type", 0));

        messageEntity.setRefId(MapUtil.getLong(params, "refId"));

        if (params.containsKey("itemId") && params.containsKey("itemCount")) {
            Integer[] itemIds = JSON.parseObject(params.get("itemId").toString(), Integer[].class);
            Integer[] itemCounts = JSON.parseObject(params.get("itemCount").toString(), Integer[].class);
            ItemData[] itemData = new ItemData[itemIds.length];
            for (int i = 0; i < itemData.length; i++) {
                itemData[i] = new ItemData(itemIds[i], itemCounts[i]);
            }
            messageEntity.setItems(itemData);
        }

        MessageManager.sendMessage(messageEntity); // 发送邮件

    }

    @GmHandler(key = "/osee/shop/rewardSetting")
    public void rewardRank(Map<String, Object> params, CommonResponse response) {
        int type = JSON.parseObject(params.get("type").toString(), Integer.class);
        List<AppRewardRankEntity> appRewardRankEntities = rewardRankMapper.findByType(type);
        response.setData(appRewardRankEntities);
        response.setSuccess(true);
    }

    @GmHandler(key = "/osee/shop/updateRewardSetting")
    public void updateRewardSetting(Map<String, Object> params, CommonResponse response) throws Exception {
        int diamond = JSON.parseObject(params.get("diamond").toString(), Integer.class);
        int gold = JSON.parseObject(params.get("gold").toString(), Integer.class);
        int highBall = JSON.parseObject(params.get("highBall").toString(), Integer.class);
        int id = JSON.parseObject(params.get("id").toString(), Integer.class);
        int lowerBall = JSON.parseObject(params.get("lowerBall").toString(), Integer.class);
        int middleBall = JSON.parseObject(params.get("middleBall").toString(), Integer.class);
        int bossBugle = JSON.parseObject(params.get("bossBugle").toString(), Integer.class);
        int skillCrit = JSON.parseObject(params.get("skillCrit").toString(), Integer.class);
        int skillFast = JSON.parseObject(params.get("skillFast").toString(), Integer.class);
        int skillFrozen = JSON.parseObject(params.get("skillFrozen").toString(), Integer.class);
        int skillLock = JSON.parseObject(params.get("skillLock").toString(), Integer.class);
        AppRewardLogEntity entity = new AppRewardLogEntity();
        entity.setId(id);
        entity.setDiamond(diamond);
        entity.setGold(gold);
        entity.setHighBall(highBall);
        entity.setLowerBall(lowerBall);
        entity.setMiddleBall(middleBall);
        entity.setBossBugle(bossBugle);
        entity.setSkillCrit(skillCrit);
        entity.setSkillFast(skillFast);
        entity.setSkillFrozen(skillFrozen);
        entity.setSkillLock(skillLock);
        int a = rewardLogMapper.update(entity);

    }

    @GmHandler(key = "/osee/shop/saveRewardSetting")
    public void saveRewardSetting(Map<String, Object> params, CommonResponse response) {
        Integer rank = JSON.parseObject(params.get("rank").toString(), Integer.class);
        Integer type = JSON.parseObject(params.get("type").toString(), Integer.class);
        Integer status = JSON.parseObject(params.get("status").toString(), Integer.class);
        AppRewardLogEntity rewardLogEntity = new AppRewardLogEntity();
        rewardLogMapper.save(rewardLogEntity);
        AppRewardRankEntity entity = new AppRewardRankEntity();
        entity.setRank(rank);
        entity.setType(type);
        entity.setStatus(status);
        entity.setReward(rewardLogEntity);
        entity.setUpdateTime(new Date());
        rewardRankMapper.save(entity);
        AppRewardRankEntity result = rewardRankMapper.findById(entity.getId());
        response.setData(result);
        response.setSuccess(true);
    }

    @GmHandler(key = "/osee/shop/deleteRewardSetting")
    public void deleteRewardSetting(Map<String, Object> params, CommonResponse response) {
        Integer id = JSON.parseObject(params.get("id").toString(), Integer.class);
        AppRewardRankEntity entity = rewardRankMapper.findById(id);
        int rewardId = entity.getReward().getId();
        rewardLogMapper.delete(rewardId);
        rewardRankMapper.delete(id);
        response.setSuccess(true);
    }

    @GmHandler(key = "/osee/shop/updateRewardRank")
    public void updateRewardRank(Map<String, Object> params, CommonResponse response) {
        Integer id = JSON.parseObject(params.get("id").toString(), Integer.class);
        Integer rank = JSON.parseObject(params.get("rank").toString(), Integer.class);
        AppRewardRankEntity entity = new AppRewardRankEntity();
        entity.setId(id);
        entity.setRank(rank);
        rewardRankMapper.update(entity);
    }

    @GmHandler(key = "/osee/shop/rewardSetting1")
    public void rewardRank1(Map<String, Object> params, CommonResponse response) {
        int type = JSON.parseObject(params.get("type").toString(), Integer.class);
        List<AppRewardRankEntity> appRewardRankEntities = rewardRankMapper.findByType1(type);
        response.setData(appRewardRankEntities);
        response.setSuccess(true);
    }

    @GmHandler(key = "/osee/shop/updateRewardSetting1")
    public void updateRewardSetting1(Map<String, Object> params, CommonResponse response) throws Exception {
        int diamond = JSON.parseObject(params.get("diamond").toString(), Integer.class);
        int gold = JSON.parseObject(params.get("gold").toString(), Integer.class);
        int highBall = JSON.parseObject(params.get("highBall").toString(), Integer.class);
        int id = JSON.parseObject(params.get("id").toString(), Integer.class);
        int lowerBall = JSON.parseObject(params.get("lowerBall").toString(), Integer.class);
        int middleBall = JSON.parseObject(params.get("middleBall").toString(), Integer.class);
        int bossBugle = JSON.parseObject(params.get("bossBugle").toString(), Integer.class);
        int skillCrit = JSON.parseObject(params.get("skillCrit").toString(), Integer.class);
        int skillFast = JSON.parseObject(params.get("skillFast").toString(), Integer.class);
        int skillFrozen = JSON.parseObject(params.get("skillFrozen").toString(), Integer.class);
        int skillLock = JSON.parseObject(params.get("skillLock").toString(), Integer.class);
        AppRewardLogEntity entity = new AppRewardLogEntity();
        entity.setId(id);
        entity.setDiamond(diamond);
        entity.setGold(gold);
        entity.setHighBall(highBall);
        entity.setLowerBall(lowerBall);
        entity.setMiddleBall(middleBall);
        entity.setBossBugle(bossBugle);
        entity.setSkillCrit(skillCrit);
        entity.setSkillFast(skillFast);
        entity.setSkillFrozen(skillFrozen);
        entity.setSkillLock(skillLock);
        int a = rewardLogMapper.update1(entity);

    }

    @GmHandler(key = "/osee/shop/saveRewardSetting1")
    public void saveRewardSetting1(Map<String, Object> params, CommonResponse response) {
        Integer rank = JSON.parseObject(params.get("rank").toString(), Integer.class);
        Integer type = JSON.parseObject(params.get("type").toString(), Integer.class);
        Integer status = JSON.parseObject(params.get("status").toString(), Integer.class);
        AppRewardLogEntity rewardLogEntity = new AppRewardLogEntity();
        rewardLogMapper.save1(rewardLogEntity);
        AppRewardRankEntity entity = new AppRewardRankEntity();
        entity.setRank(rank);
        entity.setType(type);
        entity.setStatus(status);
        entity.setReward(rewardLogEntity);
        entity.setUpdateTime(new Date());
        rewardRankMapper.save1(entity);
        AppRewardRankEntity result = rewardRankMapper.findById1(entity.getId());
        response.setData(result);
        response.setSuccess(true);
    }

    @GmHandler(key = "/osee/shop/deleteRewardSetting1")
    public void deleteRewardSetting1(Map<String, Object> params, CommonResponse response) {
        Integer id = JSON.parseObject(params.get("id").toString(), Integer.class);
        AppRewardRankEntity entity = rewardRankMapper.findById1(id);
        int rewardId = entity.getReward().getId();
        rewardLogMapper.delete1(rewardId);
        rewardRankMapper.delete1(id);
        response.setSuccess(true);
    }

    @GmHandler(key = "/osee/shop/updateRewardRank1")
    public void updateRewardRank1(Map<String, Object> params, CommonResponse response) {
        Integer id = JSON.parseObject(params.get("id").toString(), Integer.class);
        Integer rank = JSON.parseObject(params.get("rank").toString(), Integer.class);
        AppRewardRankEntity entity = new AppRewardRankEntity();
        entity.setId(id);
        entity.setRank(rank);
        rewardRankMapper.update1(entity);
    }

    @GmHandler(key = "/osee/shop/rewardSetting2")
    public void rewardRank2(Map<String, Object> params, CommonResponse response) {
        int type = JSON.parseObject(params.get("type").toString(), Integer.class);
        List<AppRewardRankEntity> appRewardRankEntities = rewardRankMapper.findByType2(type);
        response.setData(appRewardRankEntities);
        response.setSuccess(true);
    }

    @GmHandler(key = "/osee/shop/updateRewardSetting2")
    public void updateRewardSetting2(Map<String, Object> params, CommonResponse response) throws Exception {
        int diamond = JSON.parseObject(params.get("diamond").toString(), Integer.class);
        int gold = JSON.parseObject(params.get("gold").toString(), Integer.class);
        int highBall = JSON.parseObject(params.get("highBall").toString(), Integer.class);
        int id = JSON.parseObject(params.get("id").toString(), Integer.class);
        int lowerBall = JSON.parseObject(params.get("lowerBall").toString(), Integer.class);
        int middleBall = JSON.parseObject(params.get("middleBall").toString(), Integer.class);
        int bossBugle = JSON.parseObject(params.get("bossBugle").toString(), Integer.class);
        int skillCrit = JSON.parseObject(params.get("skillCrit").toString(), Integer.class);
        int skillFast = JSON.parseObject(params.get("skillFast").toString(), Integer.class);
        int skillFrozen = JSON.parseObject(params.get("skillFrozen").toString(), Integer.class);
        int skillLock = JSON.parseObject(params.get("skillLock").toString(), Integer.class);
        AppRewardLogEntity entity = new AppRewardLogEntity();
        entity.setId(id);
        entity.setDiamond(diamond);
        entity.setGold(gold);
        entity.setHighBall(highBall);
        entity.setLowerBall(lowerBall);
        entity.setMiddleBall(middleBall);
        entity.setBossBugle(bossBugle);
        entity.setSkillCrit(skillCrit);
        entity.setSkillFast(skillFast);
        entity.setSkillFrozen(skillFrozen);
        entity.setSkillLock(skillLock);
        int a = rewardLogMapper.update2(entity);

    }

    @GmHandler(key = "/osee/shop/saveRewardSetting2")
    public void saveRewardSetting2(Map<String, Object> params, CommonResponse response) {
        Integer rank = JSON.parseObject(params.get("rank").toString(), Integer.class);
        Integer type = JSON.parseObject(params.get("type").toString(), Integer.class);
        Integer status = JSON.parseObject(params.get("status").toString(), Integer.class);
        AppRewardLogEntity rewardLogEntity = new AppRewardLogEntity();
        rewardLogMapper.save2(rewardLogEntity);
        AppRewardRankEntity entity = new AppRewardRankEntity();
        entity.setRank(rank);
        entity.setType(type);
        entity.setStatus(status);
        entity.setReward(rewardLogEntity);
        entity.setUpdateTime(new Date());
        rewardRankMapper.save2(entity);
        AppRewardRankEntity result = rewardRankMapper.findById2(entity.getId());
        response.setData(result);
        response.setSuccess(true);
    }

    @GmHandler(key = "/osee/shop/deleteRewardSetting2")
    public void deleteRewardSetting2(Map<String, Object> params, CommonResponse response) {
        Integer id = JSON.parseObject(params.get("id").toString(), Integer.class);
        AppRewardRankEntity entity = rewardRankMapper.findById2(id);
        int rewardId = entity.getReward().getId();
        rewardLogMapper.delete2(rewardId);
        rewardRankMapper.delete2(id);
        response.setSuccess(true);
    }

    @GmHandler(key = "/osee/shop/updateRewardRank2")
    public void updateRewardRank2(Map<String, Object> params, CommonResponse response) {
        Integer id = JSON.parseObject(params.get("id").toString(), Integer.class);
        Integer rank = JSON.parseObject(params.get("rank").toString(), Integer.class);
        AppRewardRankEntity entity = new AppRewardRankEntity();
        entity.setId(id);
        entity.setRank(rank);
        rewardRankMapper.update2(entity);
    }

    @GmHandler(key = "/osee/shop/rewardSetting3")
    public void rewardRank3(Map<String, Object> params, CommonResponse response) {
        int type = JSON.parseObject(params.get("type").toString(), Integer.class);
        List<AppRewardRankEntity> appRewardRankEntities = rewardRankMapper.findByType3(type);
        response.setData(appRewardRankEntities);
        response.setSuccess(true);
    }

    @GmHandler(key = "/osee/shop/updateRewardSetting3")
    public void updateRewardSetting3(Map<String, Object> params, CommonResponse response) throws Exception {
        int diamond = JSON.parseObject(params.get("diamond").toString(), Integer.class);
        int gold = JSON.parseObject(params.get("gold").toString(), Integer.class);
        int highBall = JSON.parseObject(params.get("highBall").toString(), Integer.class);
        int id = JSON.parseObject(params.get("id").toString(), Integer.class);
        int lowerBall = JSON.parseObject(params.get("lowerBall").toString(), Integer.class);
        int middleBall = JSON.parseObject(params.get("middleBall").toString(), Integer.class);
        int bossBugle = JSON.parseObject(params.get("bossBugle").toString(), Integer.class);
        int skillCrit = JSON.parseObject(params.get("skillCrit").toString(), Integer.class);
        int skillFast = JSON.parseObject(params.get("skillFast").toString(), Integer.class);
        int skillFrozen = JSON.parseObject(params.get("skillFrozen").toString(), Integer.class);
        int skillLock = JSON.parseObject(params.get("skillLock").toString(), Integer.class);
        AppRewardLogEntity entity = new AppRewardLogEntity();
        entity.setId(id);
        entity.setDiamond(diamond);
        entity.setGold(gold);
        entity.setHighBall(highBall);
        entity.setLowerBall(lowerBall);
        entity.setMiddleBall(middleBall);
        entity.setBossBugle(bossBugle);
        entity.setSkillCrit(skillCrit);
        entity.setSkillFast(skillFast);
        entity.setSkillFrozen(skillFrozen);
        entity.setSkillLock(skillLock);
        int a = rewardLogMapper.update3(entity);

    }

    @GmHandler(key = "/osee/shop/saveRewardSetting3")
    public void saveRewardSetting3(Map<String, Object> params, CommonResponse response) {
        Integer rank = JSON.parseObject(params.get("rank").toString(), Integer.class);
        Integer type = JSON.parseObject(params.get("type").toString(), Integer.class);
        Integer status = JSON.parseObject(params.get("status").toString(), Integer.class);
        AppRewardLogEntity rewardLogEntity = new AppRewardLogEntity();
        rewardLogMapper.save3(rewardLogEntity);
        AppRewardRankEntity entity = new AppRewardRankEntity();
        entity.setRank(rank);
        entity.setType(type);
        entity.setStatus(status);
        entity.setReward(rewardLogEntity);
        entity.setUpdateTime(new Date());
        rewardRankMapper.save3(entity);
        AppRewardRankEntity result = rewardRankMapper.findById3(entity.getId());
        response.setData(result);
        response.setSuccess(true);
    }

    @GmHandler(key = "/osee/shop/deleteRewardSetting3")
    public void deleteRewardSetting3(Map<String, Object> params, CommonResponse response) {
        Integer id = JSON.parseObject(params.get("id").toString(), Integer.class);
        AppRewardRankEntity entity = rewardRankMapper.findById3(id);
        int rewardId = entity.getReward().getId();
        rewardLogMapper.delete3(rewardId);
        rewardRankMapper.delete3(id);
        response.setSuccess(true);
    }

    @GmHandler(key = "/osee/shop/updateRewardRank3")
    public void updateRewardRank3(Map<String, Object> params, CommonResponse response) {
        Integer id = JSON.parseObject(params.get("id").toString(), Integer.class);
        Integer rank = JSON.parseObject(params.get("rank").toString(), Integer.class);
        AppRewardRankEntity entity = new AppRewardRankEntity();
        entity.setId(id);
        entity.setRank(rank);
        rewardRankMapper.update3(entity);
    }

    @GmHandler(key = "/osee/shop/rewardSetting4")
    public void rewardRank4(Map<String, Object> params, CommonResponse response) {
        int type = JSON.parseObject(params.get("type").toString(), Integer.class);
        List<AppRewardRankEntity> appRewardRankEntities = rewardRankMapper.findByType4(type);
        response.setData(appRewardRankEntities);
        response.setSuccess(true);
    }

    @GmHandler(key = "/osee/shop/updateRewardSetting4")
    public void updateRewardSetting4(Map<String, Object> params, CommonResponse response) throws Exception {
        int diamond = JSON.parseObject(params.get("diamond").toString(), Integer.class);
        int gold = JSON.parseObject(params.get("gold").toString(), Integer.class);
        int highBall = JSON.parseObject(params.get("highBall").toString(), Integer.class);
        int id = JSON.parseObject(params.get("id").toString(), Integer.class);
        int lowerBall = JSON.parseObject(params.get("lowerBall").toString(), Integer.class);
        int middleBall = JSON.parseObject(params.get("middleBall").toString(), Integer.class);
        int bossBugle = JSON.parseObject(params.get("bossBugle").toString(), Integer.class);
        int skillCrit = JSON.parseObject(params.get("skillCrit").toString(), Integer.class);
        int skillFast = JSON.parseObject(params.get("skillFast").toString(), Integer.class);
        int skillFrozen = JSON.parseObject(params.get("skillFrozen").toString(), Integer.class);
        int skillLock = JSON.parseObject(params.get("skillLock").toString(), Integer.class);
        AppRewardLogEntity entity = new AppRewardLogEntity();
        entity.setId(id);
        entity.setDiamond(diamond);
        entity.setGold(gold);
        entity.setHighBall(highBall);
        entity.setLowerBall(lowerBall);
        entity.setMiddleBall(middleBall);
        entity.setBossBugle(bossBugle);
        entity.setSkillCrit(skillCrit);
        entity.setSkillFast(skillFast);
        entity.setSkillFrozen(skillFrozen);
        entity.setSkillLock(skillLock);
        int a = rewardLogMapper.update4(entity);

    }

    @GmHandler(key = "/osee/shop/saveRewardSetting4")
    public void saveRewardSetting4(Map<String, Object> params, CommonResponse response) {
        Integer rank = JSON.parseObject(params.get("rank").toString(), Integer.class);
        Integer type = JSON.parseObject(params.get("type").toString(), Integer.class);
        Integer status = JSON.parseObject(params.get("status").toString(), Integer.class);
        AppRewardLogEntity rewardLogEntity = new AppRewardLogEntity();
        rewardLogMapper.save4(rewardLogEntity);
        AppRewardRankEntity entity = new AppRewardRankEntity();
        entity.setRank(rank);
        entity.setType(type);
        entity.setStatus(status);
        entity.setReward(rewardLogEntity);
        entity.setUpdateTime(new Date());
        rewardRankMapper.save4(entity);
        AppRewardRankEntity result = rewardRankMapper.findById4(entity.getId());
        response.setData(result);
        response.setSuccess(true);
    }

    @GmHandler(key = "/osee/shop/deleteRewardSetting4")
    public void deleteRewardSetting4(Map<String, Object> params, CommonResponse response) {
        Integer id = JSON.parseObject(params.get("id").toString(), Integer.class);
        AppRewardRankEntity entity = rewardRankMapper.findById4(id);
        int rewardId = entity.getReward().getId();
        rewardLogMapper.delete4(rewardId);
        rewardRankMapper.delete4(id);
        response.setSuccess(true);
    }

    @GmHandler(key = "/osee/shop/updateRewardRank4")
    public void updateRewardRank4(Map<String, Object> params, CommonResponse response) {
        Integer id = JSON.parseObject(params.get("id").toString(), Integer.class);
        Integer rank = JSON.parseObject(params.get("rank").toString(), Integer.class);
        AppRewardRankEntity entity = new AppRewardRankEntity();
        entity.setId(id);
        entity.setRank(rank);
        rewardRankMapper.update4(entity);
    }

    /**
     * 获取新增用户和付费留存率数据
     */
    @GmHandler(key = "/osee/reportForm/list")
    public void getReportForm(Map<String, Object> params, CommonResponse response) throws Exception {
        StringBuilder condBuilder = new StringBuilder("WHERE 1=1");
        StringBuilder pageBuilder = new StringBuilder();

        for (Entry<String, Object> entry : params.entrySet()) {
            String key = entry.getKey();
            switch (key) {
                case "type":
                    String type = JsonMapUtils.parseObject(params, "type", JsonInnerType.TYPE_STRING);
                    if (!StringUtils.isEmpty(type)) {
                        condBuilder.append(" AND type = '").append(type).append("'");
                    } else {
                        condBuilder.append(" AND type = 'register'");
                    }
                    break;
                case "startTime":
                    long startTime = JsonMapUtils.parseObject(params, "startTime", JsonInnerType.TYPE_LONG);
                    if (startTime > 0) {
                        condBuilder.append(" AND day_time >= '").append(DATE_FORMATER.format(startTime)).append("'");
                    }
                    break;
                case "endTime":
                    long endTime = JsonMapUtils.parseObject(params, "endTime", JsonInnerType.TYPE_LONG);
                    if (endTime > 0) {
                        condBuilder.append(" AND day_time <= '").append(DATE_FORMATER.format(endTime)).append("'");
                    }
                    break;
                case "id":
                    long id = JsonMapUtils.parseObject(params, "id", JsonInnerType.TYPE_LONG);
                    if (id > 0) {
                        condBuilder.append(" AND agent_id = ").append(id);
                    }
                    break;
                case "page":
                case "pageSize":
                    if (pageBuilder.length() <= 0) {
                        // 解析分页数据
                        int page = JsonMapUtils.parseObject(params, "page", JsonInnerType.TYPE_INT);
                        int pageSize = JsonMapUtils.parseObject(params, "pageSize", JsonInnerType.TYPE_INT);
                        pageBuilder.append(" LIMIT ").append(page).append(", ").append(pageSize);
                    }
                    break;
            }
        }
        condBuilder.append(" order by day_time desc ");
        List<ReporFormEntity> reportFormList =
                gmCommonMapper.getReportFormList(condBuilder.toString(), pageBuilder.toString());
        int total = gmCommonMapper.getReportFormCount(condBuilder.toString());

        List<Map<String, Object>> resultList = JsonMapUtils.objectsToMaps(reportFormList);
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("totalNum", total);
        resultMap.put("list", resultList);
        response.setData(resultMap);
    }

    /**
     * 获取新增用户和付费留存率数据
     */
    @GmHandler(key = "/osee/rateForm/list")
    public void getRatetForm(Map<String, Object> params, CommonResponse response) throws Exception {
        StringBuilder condBuilder = new StringBuilder("WHERE 1=1");
        StringBuilder pageBuilder = new StringBuilder();

        for (Entry<String, Object> entry : params.entrySet()) {
            String key = entry.getKey();
            switch (key) {
                case "startTime":
                    long startTime = JsonMapUtils.parseObject(params, "startTime", JsonInnerType.TYPE_LONG);
                    if (startTime > 0) {
                        condBuilder.append(" AND day_time >= '").append(DATE_FORMATER.format(startTime)).append("'");
                    }
                    break;
                case "endTime":
                    long endTime = JsonMapUtils.parseObject(params, "endTime", JsonInnerType.TYPE_LONG);
                    if (endTime > 0) {
                        condBuilder.append(" AND day_time <= '").append(DATE_FORMATER.format(endTime)).append("'");
                    }
                    break;
                case "id":
                    long id = JsonMapUtils.parseObject(params, "id", JsonInnerType.TYPE_LONG);
                    if (id > 0) {
                        condBuilder.append(" AND agent_id = ").append(id);
                    }
                    break;
                case "page":
                case "pageSize":
                    if (pageBuilder.length() <= 0) {
                        // 解析分页数据
                        int page = JsonMapUtils.parseObject(params, "page", JsonInnerType.TYPE_INT);
                        int pageSize = JsonMapUtils.parseObject(params, "pageSize", JsonInnerType.TYPE_INT);
                        pageBuilder.append(" LIMIT ").append(page).append(", ").append(pageSize);
                    }
                    break;
            }
        }
        condBuilder.append(" order by day_time desc ");
        List<RateFormEntity> ratetFormList =
                gmCommonMapper.getRatetFormList(condBuilder.toString(), pageBuilder.toString());
        int total = gmCommonMapper.getRateFormCount(condBuilder.toString());

        List<Map<String, Object>> resultList = JsonMapUtils.objectsToMaps(ratetFormList);
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("totalNum", total);
        resultMap.put("list", resultList);
        response.setData(resultMap);
    }

    /**
     * 获取奖池中奖记录
     */
    @GmHandler(key = "/osee/openRewordFormAll/list")
    public void getOpenRewordFormAll(Map<String, Object> params, CommonResponse response) throws Exception {
        StringBuilder condBuilder = new StringBuilder("WHERE 1=1");
        StringBuilder pageBuilder = new StringBuilder();
        // 解析数据
        int page = JsonMapUtils.parseObject(params, "page", JsonInnerType.TYPE_INT);
        int pageSize = JsonMapUtils.parseObject(params, "pageSize", JsonInnerType.TYPE_INT);

        pageBuilder.append(" LIMIT ").append((page - 1) * pageSize).append(", ").append(pageSize);
        for (Entry<String, Object> entry : params.entrySet()) {
            String key = entry.getKey();
            switch (key) {
                case "type":
                    String type = JsonMapUtils.parseObject(params, "type", JsonInnerType.TYPE_LONG);
                    if (!StringUtils.isEmpty(type)) {
                        condBuilder.append(" AND type = '").append(type).append("'");
                    }
                    break;
                case "startTime":
                    long startTime = JsonMapUtils.parseObject(params, "startTime", JsonInnerType.TYPE_LONG);
                    if (startTime > 0) {
                        condBuilder.append(" AND createTime >= '").append(DATE_FORMATER.format(startTime)).append("'");
                    }
                    break;
                case "endTime":
                    long endTime = JsonMapUtils.parseObject(params, "endTime", JsonInnerType.TYPE_LONG);
                    if (endTime > 0) {
                        condBuilder.append(" AND createTime <= '").append(DATE_FORMATER.format(endTime)).append("'");
                    }
                    break;
                case "userId":
                    long id = JsonMapUtils.parseObject(params, "userId", JsonInnerType.TYPE_LONG);
                    condBuilder.append(" AND userId = ").append(id);
                    break;
                case "nickName":
                    String nickName = JsonMapUtils.parseObject(params, "nickName", JsonInnerType.TYPE_STRING);
                    if (!StringUtils.isEmpty(nickName)) {
                        condBuilder.append(" AND nickName = ").append(nickName);
                    }
                    break;
            }
        }
        condBuilder.append(" order by createTime desc ");
        List<FishJc> reportFormList = gmCommonMapper.getOpenRewordList(condBuilder.toString(), pageBuilder.toString());
        int total = gmCommonMapper.getOpenRewordFormCount(condBuilder.toString());

        List<Map<String, Object>> resultList = JsonMapUtils.objectsToMaps(reportFormList);
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("totalNum", total);
        resultMap.put("list", resultList);
        response.setData(resultMap);
    }

    /**
     * 获取新增用户和付费留存率数据
     */
    @GmHandler(key = "/osee/reportFormAll/list")
    public void getReportFormAll(Map<String, Object> params, CommonResponse response) throws Exception {
        StringBuilder condBuilder = new StringBuilder("WHERE 1=1");
        StringBuilder pageBuilder = new StringBuilder();
        // 解析数据
        int page = JsonMapUtils.parseObject(params, "page", JsonInnerType.TYPE_INT);
        int pageSize = JsonMapUtils.parseObject(params, "pageSize", JsonInnerType.TYPE_INT);

        pageBuilder.append(" LIMIT ").append((page - 1) * pageSize).append(", ").append(pageSize);
        for (Entry<String, Object> entry : params.entrySet()) {
            String key = entry.getKey();
            switch (key) {
                case "type":
                    String type = JsonMapUtils.parseObject(params, "type", JsonInnerType.TYPE_STRING);
                    if (!StringUtils.isEmpty(type)) {
                        condBuilder.append(" AND type = '").append(type).append("'");
                    }
                    break;
                case "startTime":
                    long startTime = JsonMapUtils.parseObject(params, "startTime", JsonInnerType.TYPE_LONG);
                    if (startTime > 0) {
                        condBuilder.append(" AND day_time >= '").append(DATE_FORMATER.format(startTime)).append("'");
                    }
                    break;
                case "endTime":
                    long endTime = JsonMapUtils.parseObject(params, "endTime", JsonInnerType.TYPE_LONG);
                    if (endTime > 0) {
                        condBuilder.append(" AND day_time <= '").append(DATE_FORMATER.format(endTime)).append("'");
                    }
                    break;
                case "agentId":
                    long id = JsonMapUtils.parseObject(params, "agentId", JsonInnerType.TYPE_LONG);
                    condBuilder.append(" AND agent_id = ").append(id);
                    break;
            }
        }
        condBuilder.append(" order by day_time desc ");
        List<ReporFormEntity> reportFormList =
                gmCommonMapper.getReportFormList(condBuilder.toString(), pageBuilder.toString());
        // for(ReporFormEntity reportFormList1:reportFormList){
        // SimpleDateFormat sdf = new SimpleDateFormat("MM/dd HH:mm");
        // reportFormList1.setCreateTime(sdf.format(reportFormList1.getCreateTime()));
        // }
        int total = gmCommonMapper.getReportFormCount(condBuilder.toString());

        List<Map<String, Object>> resultList = JsonMapUtils.objectsToMaps(reportFormList);
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("totalNum", total);
        resultMap.put("list", resultList);
        response.setData(resultMap);
    }

    /**
     * 获取新增用户和付费留存率数据
     */
    @GmHandler(key = "/osee/rateFormAll/list")
    public void getRatetFormAll(Map<String, Object> params, CommonResponse response) throws Exception {
        StringBuilder condBuilder = new StringBuilder("WHERE 1=1");
        StringBuilder pageBuilder = new StringBuilder();
        // 解析数据
        int page = JsonMapUtils.parseObject(params, "page", JsonInnerType.TYPE_INT);
        int pageSize = JsonMapUtils.parseObject(params, "pageSize", JsonInnerType.TYPE_INT);

        pageBuilder.append(" LIMIT ").append((page - 1) * pageSize).append(", ").append(pageSize);
        for (Entry<String, Object> entry : params.entrySet()) {
            String key = entry.getKey();
            switch (key) {
                case "startTime":
                    long startTime = JsonMapUtils.parseObject(params, "startTime", JsonInnerType.TYPE_LONG);
                    if (startTime > 0) {
                        condBuilder.append(" AND day_time >= '").append(DATE_FORMATER.format(startTime)).append("'");
                    }
                    break;
                case "endTime":
                    long endTime = JsonMapUtils.parseObject(params, "endTime", JsonInnerType.TYPE_LONG);
                    if (endTime > 0) {
                        condBuilder.append(" AND day_time <= '").append(DATE_FORMATER.format(endTime)).append("'");
                    }
                    break;
                case "agentId":
                    long id = JsonMapUtils.parseObject(params, "agentId", JsonInnerType.TYPE_LONG);
                    condBuilder.append(" AND agent_id = ").append(id);
                    break;
            }
        }
        condBuilder.append(" order by day_time desc ");
        List<RateFormEntity> ratetFormList =
                gmCommonMapper.getRatetFormList(condBuilder.toString(), pageBuilder.toString());
        int total = gmCommonMapper.getRateFormCount(condBuilder.toString());

        List<Map<String, Object>> resultList = JsonMapUtils.objectsToMaps(ratetFormList);
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("totalNum", total);
        resultMap.put("list", resultList);
        response.setData(resultMap);
    }

    public static void main(String[] args) {
        Double firstCommissionRate = 50.0000;
        // 佣金
        Double commission = 8 * firstCommissionRate / 100;

        System.out.println(commission);
    }

    /**
     * 获取变化记录
     */
    @GmHandler(key = "/osee/changeAll/list")
    public void dochangeAllListTask(Map<String, Object> params, CommonResponse response) throws Exception {
        StringBuilder condBuilder = new StringBuilder("WHERE 1=1");
        StringBuilder pageBuilder = new StringBuilder();

        // 解析数据
        int page = JsonMapUtils.parseObject(params, "page", JsonInnerType.TYPE_INT);
        int pageSize = JsonMapUtils.parseObject(params, "pageSize", JsonInnerType.TYPE_INT);

        pageBuilder.append(" LIMIT ").append((page - 1) * pageSize).append(", ").append(pageSize);

        for (Entry<String, Object> entry : params.entrySet()) {
            String key = entry.getKey();
            switch (key) {
                case "startTime":
                    long startTime = JsonMapUtils.parseObject(params, "startTime", JsonInnerType.TYPE_LONG);
                    if (startTime > 0) {
                        Date startDate = new Date(startTime);
                        condBuilder.append(" AND log.create_time >= '").append(DATE_FORMATER.format(startDate))
                                .append("'");
                    }
                    break;
                case "endTime":
                    long endTime = JsonMapUtils.parseObject(params, "endTime", JsonInnerType.TYPE_LONG);
                    if (endTime > 0) {
                        Date endDate = new Date(endTime);
                        condBuilder.append(" AND log.create_time <= '").append(DATE_FORMATER.format(endDate))
                                .append("'");
                    }
                    break;
            }
        }

        List<ChangeAll> logList = rechargeLogMapper.getChangeAllList(condBuilder.toString(), pageBuilder.toString());
        long logCount = rechargeLogMapper.getChangeAllCount(condBuilder.toString());
        List<Map<String, Object>> dataList = new LinkedList<>();
        for (ChangeAll log : logList) {
            Map<String, Object> dataMap = new HashMap<>();
            dataMap.put("createTime", log.getCreateTime());
            dataMap.put("loginNum", log.getLoginNum());
            dataMap.put("goldAll", log.getGoldAll());
            dataMap.put("bossAll", log.getBossAll());
            dataMap.put("critAll", log.getCritAll());
            dataMap.put("lockAll", log.getLockAll());
            dataMap.put("magicAll", log.getMagicAll());
            dataMap.put("diamondAll", log.getDiamondAll());
            dataMap.put("frozenAll", log.getFrozenAll());
            dataMap.put("moneyChange", log.getMoneyChange());
            dataMap.put("dragonChange", log.getDragonChange());
            dataMap.put("yuGuAll", log.getYuGuAll());
            dataMap.put("haiHunShiAll", log.getHaiHunShiAll());
            dataMap.put("haiShouShiAll", log.getHaiShouShiAll());
            dataMap.put("dianCiShiAll", log.getDianCiShiAll());
            dataMap.put("haiMoShiAll", log.getHaiMoShiAll());
            dataMap.put("haiYaoShiAll", log.getHaiYaoShiAll());
            dataMap.put("zhenZhuShiAll", log.getZhenZhuShiAll());
            dataMap.put("zhaoHunShiAll", log.getZhaoHuanShiAll());
            dataMap.put("wangHunShiAll", log.getWangHunShiAll());
            dataMap.put("skillTorpedoAll", log.getSkillTorpedoAll());
            dataMap.put("skillBlackHoleAll", log.getSkillBlackHoleAll());
            dataMap.put("skillBitAll", log.getSkillBitAll());
            dataMap.put("longZhuAll", log.getLongZhuAll());
            dataMap.put("longYuanAll", log.getLongYuanAll());
            dataMap.put("longJiAll", log.getLongJiAll());
            dataMap.put("longGuAll", log.getLongGuAll());
            dataMap.put("lingZhuShiAll", log.getLingZhuShiAll());
            dataMap.put("heiDongShiAll", log.getHeiDongShiAll());
            dataList.add(dataMap);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("totalNum", logCount);
        result.put("list", dataList);
        response.setData(result);
    }

    /**
     * 获取变化记录
     */
    @GmHandler(key = "/osee/agentPlayerAll/list")
    public void doAgentPlayerAllListTask(Map<String, Object> params, CommonResponse response) throws Exception {
        StringBuilder condBuilder = new StringBuilder("WHERE 1=1");
        StringBuilder pageBuilder = new StringBuilder();

        // 解析数据
        int page = JsonMapUtils.parseObject(params, "page", JsonInnerType.TYPE_INT);
        int pageSize = JsonMapUtils.parseObject(params, "pageSize", JsonInnerType.TYPE_INT);

        pageBuilder.append(" LIMIT ").append((page - 1) * pageSize).append(", ").append(pageSize);

        for (Entry<String, Object> entry : params.entrySet()) {
            String key = entry.getKey();
            switch (key) {
                case "startTime":
                    long startTime = JsonMapUtils.parseObject(params, "startTime", JsonInnerType.TYPE_LONG);
                    if (startTime > 0) {
                        Date startDate = new Date(startTime);
                        condBuilder.append(" AND log.create_time >= '").append(DATE_FORMATER.format(startDate))
                                .append("'");
                    }
                    break;
                case "endTime":
                    long endTime = JsonMapUtils.parseObject(params, "endTime", JsonInnerType.TYPE_LONG);
                    if (endTime > 0) {
                        Date endDate = new Date(endTime);
                        condBuilder.append(" AND log.create_time <= '").append(DATE_FORMATER.format(endDate))
                                .append("'");
                    }
                    break;
            }
        }

        List<AgentPlayerAll> logList =
                rechargeLogMapper.getAgentPlayerAllList(condBuilder.toString(), pageBuilder.toString());
        long logCount = rechargeLogMapper.getAgentPlayerAllCount(condBuilder.toString());
        List<Map<String, Object>> dataList = new LinkedList<>();
        for (AgentPlayerAll log : logList) {
            Map<String, Object> dataMap = new HashMap<>();
            dataMap.put("createTime", log.getCreateTime());
            dataMap.put("loginNun", log.getLoginNum());
            dataMap.put("goldAll", log.getGoldAll());
            dataMap.put("bossAll", log.getBossAll());
            dataMap.put("critAll", log.getCritAll());
            dataMap.put("lockAll", log.getLockAll());
            dataMap.put("magicAll", log.getMagicAll());
            dataMap.put("diamondAll", log.getDiamondAll());
            dataMap.put("frozenAll", log.getFrozenAll());
            dataMap.put("goldChange", log.getGoldChange());
            dataMap.put("agentId", log.getAgentId());
            dataMap.put("agentName", log.getAgentName());
            dataMap.put("yuGuAll", log.getYuGuAll());
            dataMap.put("haiHunShiAll", log.getHaiHunShiAll());
            dataMap.put("haiShouShiAll", log.getHaiShouShiAll());
            dataMap.put("dianCiShiAll", log.getDianCiShiAll());
            dataMap.put("haiMoShiAll", log.getHaiMoShiAll());
            dataMap.put("haiYaoShiAll", log.getHaiYaoShiAll());
            dataMap.put("zhenZhuShiAll", log.getZhenZhuShiAll());
            dataMap.put("zhaoHunShiAll", log.getZhaoHuanShiAll());
            dataMap.put("wangHunShiAll", log.getWangHunShiAll());
            dataMap.put("skillTorpedoAll", log.getSkillTorpedoAll());
            dataMap.put("skillBlackHoleAll", log.getSkillBlackHoleAll());
            dataMap.put("skillBitAll", log.getSkillBitAll());
            dataMap.put("longZhuAll", log.getLongZhuAll());
            dataMap.put("longYuanAll", log.getLongYuanAll());
            dataMap.put("longJiAll", log.getLongJiAll());
            dataMap.put("longGuAll", log.getLongGuAll());
            dataMap.put("lingZhuShiAll", log.getLingZhuShiAll());
            dataMap.put("heiDongShiAll", log.getHeiDongShiAll());
            dataList.add(dataMap);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("totalNum", logCount);
        result.put("list", dataList);
        response.setData(result);
    }

    @Autowired
    private AnimalsRecordMapper animalsRecordMapper;

    @Autowired
    private BairenRecordMapper bairenRecordMapper;


    /**
     * 分页排序查询：击杀鱼记录
     */
    @GmHandler(key = "/osee/killFishList/page")
    public void killFishListPage(Map<String, Object> paramMap, CommonResponse response) {
        baseService.killFishListPage(paramMap, response);
    }

}
