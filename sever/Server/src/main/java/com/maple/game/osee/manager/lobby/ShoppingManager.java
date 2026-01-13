package com.maple.game.osee.manager.lobby;

import static com.maple.game.osee.manager.PlayerManager.getPlayerEntity;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

import javax.annotation.PostConstruct;

import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import com.maple.database.config.redis.RedisHelper;
import com.maple.database.data.entity.UserAuthenticationEntity;
import com.maple.database.data.mapper.UserAuthenticationMapper;
import com.maple.engine.container.UserContainer;
import com.maple.engine.data.ServerUser;
import com.maple.engine.utils.DateUtils;
import com.maple.engine.utils.MySettingUtil;
import com.maple.game.osee.common.RedisUtil;
import com.maple.game.osee.dao.data.entity.*;
import com.maple.game.osee.dao.data.mapper.*;
import com.maple.game.osee.dao.log.entity.*;
import com.maple.game.osee.dao.log.mapper.AgentCutLogMapper;
import com.maple.game.osee.dao.log.mapper.OseeRealLotteryLogMapper;
import com.maple.game.osee.dao.log.mapper.OseeRechargeLogMapper;
import com.maple.game.osee.dao.log.mapper.OseeUnrealLotteryLogMapper;
import com.maple.game.osee.entity.ItemChangeReason;
import com.maple.game.osee.entity.ItemData;
import com.maple.game.osee.entity.ItemId;
import com.maple.game.osee.manager.PlayerManager;
import com.maple.game.osee.manager.UserPropsManager;
import com.maple.game.osee.proto.HwLoginMessage;
import com.maple.game.osee.proto.OseeMessage.OseeMsgCode;
import com.maple.game.osee.proto.OseePublicData;
import com.maple.game.osee.proto.fishing.OseeFishingMessage;
import com.maple.game.osee.proto.lobby.OseeLobbyMessage;
import com.maple.game.osee.proto.lobby.OseeLobbyMessage.BuyShopItemResponse;
import com.maple.game.osee.proto.lobby.OseeLobbyMessage.GetLotteryShopListResponse;
import com.maple.game.osee.proto.lobby.OseeLobbyMessage.LotteryShopItemProto;
import com.maple.game.osee.util.CommonUtil;
import com.maple.game.osee.util.FishingChallengeFightFishUtil;
import com.maple.gamebase.container.GameContainer;
import com.maple.gamebase.data.BaseGamePlayer;
import com.maple.network.manager.NetManager;

import cn.hutool.core.lang.func.VoidFunc0;
import cn.hutool.core.text.StrBuilder;
import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.StrUtil;
import lombok.SneakyThrows;

/**
 * 商城管理类
 */
@Component
public class ShoppingManager {

    @Autowired
    private OseeLotteryShopMapper lotteryShopMapper;

    @Autowired
    private AddressMapper addressMapper;

    @Autowired
    private UserAuthenticationMapper authenticationMapper;

    @Autowired
    private StockMapper stockMapper;

    @Autowired
    private OseeRealLotteryLogMapper realLotteryLogMapper;

    @Autowired
    private OseeUnrealLotteryLogMapper unrealLotteryLogMapper;

    @Autowired
    private OseePlayerMapper playerMapper;

    @Autowired
    private OseeRechargeLogMapper oseeRechargeLogMapper;

    @Autowired
    private CommonLobbyManager commonLobbyManager;

    @Autowired
    private AgentMapper agentMapper;

    @Autowired
    private AgentCutLogMapper agentCutLogMapper;

    @Autowired
    private ShoppingMapper shoppingMapper;

    /**
     * 奖券商品列表
     */
    private List<OseeLotteryShopEntity> lotteryShops = new LinkedList<>();

    /**
     * 金币价格 数量-价格
     */
    private int[][] goldPrice = {{56 * 10000, 25}, {126 * 10000, 56}, {476 * 10000, 212}, {896 * 10000, 400},
        {1876 * 10000, 837}, {3465 * 10000, 1546}, {3976 * 10000, 1775}, {4886 * 10000, 2181}};

    private int diaPex = 45000;

    /**
     * 道具价格 道具数量-所需钻石数量 锁定卡X78 78钻 冰冻卡X78 78钻 暴击卡X100 150钻
     * <p>
     * 加速卡X100 150钻 BOSS号角X20 918钻 分身炮X100 150钻 电磁炮X65 288钻 赠送卡X50 1000钻 钻头X50 519钻 黑洞炮X50 698钻 鱼雷炮X50 718钻
     */
    private final int[][] propPrice = {{78, 25}, {78, 25}, {40, 25}, {40, 25}, {10, 25}, {40, 25}, {40, 25}, {50, 1000},
        {50, 519}, {50, 698}, {50, 718}};

    /**
     * VIP3-9的每日boss号角购买次数限制
     */
    private final int[] bossBugleBuyLimit = {1, 2, 4, 10, 16, 32, 64};

    /**
     * 炮台外观价格 外观体验天数-所需钻石数量
     */
    private final int[][] batteryViewPrice =
        {{3, 20}, {3, 25}, {3, 25}, {3, 20}, {3, 20}, {3, 25}, {3, 25}, {3, 15}, {3, 25}, {3, 25}};

    /**
     * 每日福袋
     */
    private final Map<ItemId, List<ItemData>> dailyBagMap = new HashMap<>();

    /**
     * 每日礼包
     */
    private final Map<ItemId, List<ItemData>> dailyGiftMap = new HashMap<>();

    /**
     * 特惠礼包
     */
    private final Map<ItemId, List<ItemData>> onceBagMap = new HashMap<>();

    @Autowired
    private GShopPropsMapper shopPropsMapper;
    /**
     * 商城在售商品
     */
    private Map<Long, ShopProps> shopPropsForSaleMap;

    private static RedissonClient redissonClient;

    public ShoppingManager(RedissonClient redissonClient) {

        ShoppingManager.redissonClient = redissonClient;

    }

    /**
     * 配置设置
     */
    @PostConstruct
    public void settings() {
        // 新增钻石特惠礼包
        onceBagMap.put(ItemId.TE_HUI_680, Arrays.asList(new ItemData(ItemId.DIAMOND.getId(), -80),
            new ItemData(ItemId.DRAGON_CRYSTAL.getId(), 88000)));
        onceBagMap.put(ItemId.TE_HUI_1980, Arrays.asList(new ItemData(ItemId.DIAMOND.getId(), -300),
            new ItemData(ItemId.DRAGON_CRYSTAL.getId(), 360000)));
        onceBagMap.put(ItemId.TE_HUI_3180, Arrays.asList(new ItemData(ItemId.DIAMOND.getId(), -980),
            new ItemData(ItemId.DRAGON_CRYSTAL.getId(), 1380000)));
        onceBagMap.put(ItemId.TE_HUI_4680, Arrays.asList(new ItemData(ItemId.DIAMOND.getId(), -1980),
            new ItemData(ItemId.DRAGON_CRYSTAL.getId(), 2800000)));
        onceBagMap.put(ItemId.TE_HUI_5980, Arrays.asList(new ItemData(ItemId.DIAMOND.getId(), -3280),
            new ItemData(ItemId.DRAGON_CRYSTAL.getId(), 4660000)));
        onceBagMap.put(ItemId.TE_HUI_10000, Arrays.asList(new ItemData(ItemId.DIAMOND.getId(), -6480),
            new ItemData(ItemId.DRAGON_CRYSTAL.getId(), 9400000)));

    }

    /**
     * 刷新奖券商城数据
     */
    public void refreshLottery() {

        lotteryShops = new ArrayList<>(lotteryShopMapper.getAll());

        for (int i = 0; i < lotteryShops.size(); i++) {

            if (lotteryShops.get(i).getIndex() != i + 1) {

                lotteryShops.get(i).setIndex(i + 1);
                lotteryShopMapper.update(lotteryShops.get(i));

            }

        }

    }

    /**
     * 调换奖品位置
     */
    public boolean changeLottery(long id, int type) {
        int index = -1;
        for (int i = 0; i < lotteryShops.size(); i++) {
            if (lotteryShops.get(i).getId() == id) {
                index = i;
                break;
            }
        }

        if (index == -1) {
            return false;
        }
        if (index + type < 0 || index + type >= lotteryShops.size()) {
            return false;
        }

        lotteryShops.get(index + type).setIndex(index);
        lotteryShops.get(index).setIndex(index + type);
        lotteryShopMapper.update(lotteryShops.get(index + type));
        lotteryShopMapper.update(lotteryShops.get(index));

        refreshLottery();

        return true;
    }

    @Autowired
    private PlayerManager playerManager;
    @Autowired
    private UserPropsManager userPropsManager;

    @Autowired
    private GUserPropsMapper gUserPropsMapper;

    // 弹头和龙晶之间的比例  -- 游走字幕弹头和龙晶之间的比例
    public static int GOLD_TORPEDO_TO_DRAGON_CRYSTAL = 200000;

    /**
     * 购买商品
     */
    public void buyShopItem(ServerUser user, long index) {

        if (user.getId() == 0) {
            return;
        }

        if (index <= 10000) {
            // 购买：奖券商城相关
            buyLotteryShopItem(user, index);
            return;
        }

        // 获取购买的商品
        final ShopProps shopProps = shopPropsMapper.getForSaleProp(index);
        if (shopProps == null) {
            NetManager.sendHintMessageToClient("商品选择有误，请重新选择", user);
            return;
        }

        OseePlayerEntity playerEntity = getPlayerEntity(user);

        synchronized (playerEntity) {

            // 购买的商品
            final ItemId getItemId = ItemId.getItemIdById(shopProps.getPropsId());

            BaseGamePlayer player = GameContainer.getPlayerById(user.getId());

            if (player != null && ItemId.GOLD_TORPEDO.equals(getItemId)) {
                NetManager.sendHintMessageToClient("在房间内无法购买弹头！", user);
                return;
            }

            if (index == 14999) { // 全部龙晶兑换弹头

                long goldTorpedoNumber = playerEntity.getDragonCrystal() / GOLD_TORPEDO_TO_DRAGON_CRYSTAL;

                if (goldTorpedoNumber == 0) {
                    goldTorpedoNumber = 1;
                }

                shopProps.setQuantity(goldTorpedoNumber);
                shopProps.setPrice(goldTorpedoNumber * GOLD_TORPEDO_TO_DRAGON_CRYSTAL);

            } else if (index == 15999) { // 全部弹头兑换龙晶

                long goldTorpedoNumber = playerEntity.getGoldTorpedo();

                if (goldTorpedoNumber == 0) {
                    goldTorpedoNumber = 1;
                }

                shopProps.setQuantity(goldTorpedoNumber * GOLD_TORPEDO_TO_DRAGON_CRYSTAL);
                shopProps.setPrice(goldTorpedoNumber);

            }

            final ItemId itemId = ItemId.getItemIdById(shopProps.getCurrency());

            if (!PlayerManager.checkItem(user, shopProps.getCurrency(), shopProps.getPrice())) { // 物品数量检查
                NetManager.sendHintMessageToClient(itemId.getInfo() + "不足，无法购买", user);
                return;
            }

            // 额外赠送处理
            final Double firstGive = shopProps.getFirstGive();
            final Double followUpGive = shopProps.getFollowUpGive();
            if ((firstGive != null && firstGive > 0) || (followUpGive != null && followUpGive > 0)) { // 有设置购买额外赠送
                int firstBuy = RedisUtil.val("GAME:USER:" + user.getId() + "FIRST_BUY:" + shopProps.getId(), 0);
                if (firstBuy == 0 && firstGive != null && firstGive > 0) { // 首次
                    Long quantity = shopProps.getQuantity();
                    quantity = quantity + (int)(quantity * firstGive);
                    shopProps.setQuantity(quantity);
                    RedisHelper.set("GAME:USER:" + user.getId() + "FIRST_BUY:" + shopProps.getId(), "1");
                } else if (followUpGive != null && followUpGive > 0) { // 后续
                    Long quantity = shopProps.getQuantity();
                    quantity = quantity + (int)(quantity * followUpGive);
                    shopProps.setQuantity(quantity);
                }
            }

            OseePlayerEntity entity = getPlayerEntity(user);
            long preDiamond = entity.getDiamond();
            long preGoldTorpedo = entity.getGoldTorpedo();

            String changingReasonStr = StrBuilder.create().append("名称：").append(getItemId.getInfo()).append("\n数量：")
                .append(shopProps.getQuantity()).toString();



            PlayerManager.addItem(user, shopProps.getCurrency(), -shopProps.getPrice(), ItemChangeReason.SHOPPING,
                true);

            preDiamond = entity.getDiamond();
            preGoldTorpedo = entity.getGoldTorpedo();

            PlayerManager.addItem(user, shopProps.getPropsId(), shopProps.getQuantity(), ItemChangeReason.SHOPPING,
                true);



            if (shopProps.getCurrency() == ItemId.DIAMOND.getId()) { // 消耗钻石后置处理
                this.postConsumerDiamond(user, shopProps.getPrice());
            }

            if (shopProps.getPropsId() == ItemId.SKILL_TIAN_SHEN_GUAN_YU.getId()) { // 购买天神关羽
                // 保存天神关羽到数据
                UserProps userProps = gUserPropsMapper.getUserProps(user.getId(), ItemId.SKILL_TIAN_SHEN_GUAN_YU.getId());
                Long quantity = shopProps.getQuantity()*1000; // 获取数据库中的数量  quantity 单位是秒 转为毫秒
                Date date = new Date();
                if (ObjectUtils.isEmpty(userProps)){
                    userProps = new UserProps();
                    userProps.setUserId(user.getId());
                    userProps.setPropsId(ItemId.SKILL_TIAN_SHEN_GUAN_YU.getId());
                    date.setTime(date.getTime()+quantity);
                    userProps.setExpirationTime(date);
                    gUserPropsMapper.insertUserProps(userProps);
                }else if (ObjectUtils.isEmpty(userProps.getExpirationTime())){
                    date.setTime(date.getTime()+quantity);
                    userProps.setExpirationTime(date);
                    gUserPropsMapper.updateUserProps(userProps);
                } else if (userProps.getExpirationTime().getTime()>System.currentTimeMillis()) {
                    date = userProps.getExpirationTime();
                    date.setTime(date.getTime()+quantity);
                    userProps.setExpirationTime(date);
                    gUserPropsMapper.updateUserProps(userProps);
                }else {
                    date.setTime(date.getTime()+quantity);
                    userProps.setExpirationTime(date);
                    gUserPropsMapper.updateUserProps(userProps);
                }
            }
            if (shopProps.getPropsId() == ItemId.VIP_CARD.getId()) { // 购买VIP卡后置处理
                // List<UserProps> itemDatas = Arrays.asList(
                // new UserProps(user.getId(), ItemId.BATTERY_VIEW_1.getId(), 100 * 365 * 24 * 60 * 60L),
                // new UserProps(user.getId(), ItemId.BATTERY_VIEW_2.getId(), 100 * 365 * 24 * 60 * 60L)
                // new UserProps(user.getId(), ItemId.WING_VIEW_1.getId(), 100 * 365 * 24 * 60 * 60L)
                // );
                // userPropsManager.addUserProps(user, itemDatas, ItemChangeReason.SHOPPING);
                playerEntity.setVipLevel(4);
                playerMapper.update(playerEntity);
            }

            if (shopProps.getPropsId() == ItemId.VIP_CARD8.getId()) { // 购买VIP卡后置处理
                // List<UserProps> itemDatas = Arrays.asList(
                // new UserProps(user.getId(), ItemId.BATTERY_VIEW_1.getId(), 100 * 365 * 24 * 60 * 60L),
                // new UserProps(user.getId(), ItemId.BATTERY_VIEW_2.getId(), 100 * 365 * 24 * 60 * 60L)
                // new UserProps(user.getId(), ItemId.WING_VIEW_1.getId(), 100 * 365 * 24 * 60 * 60L)
                // );
                // userPropsManager.addUserProps(user, itemDatas, ItemChangeReason.SHOPPING);
                playerEntity.setVipLevel(8);
                playerMapper.update(playerEntity);
            }

            // 购买或者消耗龙晶的后置处理
            if (shopProps.getPropsId() == ItemId.DRAGON_CRYSTAL.getId()) {

                // 改变：gretj
                if (redissonClient
                    .<String>getBucket(
                        FishingChallengeFightFishUtil.FISHING_PERSONAL_JCZD0_LIST_BATTERY_LEVEL_USER_PRE + user.getId())
                    .isExists()) {

                    redissonClient.getAtomicDouble(FishingChallengeFightFishUtil.FISHING_GRETJ_USER_PRE + user.getId())
                        .addAndGet(-shopProps.getQuantity());

                }

                // 节点变化时，清除 aq相关
                FishingChallengeFightFishUtil.cleanAqData(user.getId());

            } else if (shopProps.getCurrency() == ItemId.DRAGON_CRYSTAL.getId()) {

                // 清除：盈利次数相关参数
                FishingChallengeFightFishUtil.clearYlcs(entity, "商城消耗龙晶", false, -shopProps.getPrice(), null, null);

                // 改变：gretj
                if (redissonClient
                    .<String>getBucket(
                        FishingChallengeFightFishUtil.FISHING_PERSONAL_JCZD0_LIST_BATTERY_LEVEL_USER_PRE + user.getId())
                    .isExists()) {

                    redissonClient.getAtomicDouble(FishingChallengeFightFishUtil.FISHING_GRETJ_USER_PRE + user.getId())
                        .addAndGet(shopProps.getPrice());

                }

                // 节点变化时，清除 aq相关
                FishingChallengeFightFishUtil.cleanAqData(user.getId());

            }

            // 物品处理
            userPropsManager.addUserProps(user,
                new UserProps(user.getId(), shopProps.getPropsId(), shopProps.getQuantity()),
                ItemChangeReason.SHOPPING);

            BuyShopItemResponse.Builder builder = BuyShopItemResponse.newBuilder();
            builder.setSuccess(true);
            builder.setIndex(index);
            NetManager.sendMessage(OseeMsgCode.S_C_OSEE_BUY_SHOP_ITEM_RESPONSE_VALUE, builder, user);

        }

    }

    /**
     * 购买：奖券商城相关
     */
    private void buyLotteryShopItem(ServerUser user, long index) {

        OseeLotteryShopEntity lotteryShopEntity = null;

        for (OseeLotteryShopEntity item : lotteryShops) {

            if (item.getId() == index) {

                lotteryShopEntity = item;
                break;

            }

        }

        if (lotteryShopEntity == null) {

            NetManager.sendHintMessageToClient("商品选择有误，请重新选择", user);
            return;

        }

        OseePlayerEntity playerEntity = getPlayerEntity(user);

        synchronized (playerEntity) {

            if (!PlayerManager.checkItem(user, ItemId.LOTTERY, lotteryShopEntity.getCost())) {

                NetManager.sendHintMessageToClient("剩余奖券不足，无法兑换", user);
                return;

            }

            synchronized (lotteryShopEntity) {

                // 处理：要兑换的奖券商品
                handleLotteryShopEntity(user, index, lotteryShopEntity, playerEntity);

            }

        }

    }

    /**
     * 处理：要兑换的奖券商品
     */
    @SneakyThrows
    private void handleLotteryShopEntity(ServerUser user, long index, OseeLotteryShopEntity lotteryShopEntity,
        OseePlayerEntity playerEntity) {

        long stock; // 商品库存

        if (lotteryShopEntity.getType() == 1 && lotteryShopEntity.getSendType() == 3) {

            // 获取自动发卡的实物库存内未兑换的数量
            stock = stockMapper.getUnusedCount(lotteryShopEntity.getId());

        } else {

            stock = lotteryShopEntity.getStock();

        }

        if (stock <= 0) {

            NetManager.sendHintMessageToClient("商品库存不足，无法兑换", user);
            return;

        }

        long preLottery = playerEntity.getLottery();
        long preMoney = playerEntity.getDragonCrystal();
        long preDiamond = playerEntity.getDiamond();
        long preGoldTorpedo = playerEntity.getGoldTorpedo();

        VoidFunc0 voidFunc0 = null;

        List<ItemData> itemDataList = new LinkedList<>();

        itemDataList.add(new ItemData(ItemId.LOTTERY.getId(), -lotteryShopEntity.getCost()));

        if (lotteryShopEntity.getType() == 1) { // 实物

            AddressEntity addressEntity = addressMapper.getByPlayerId(user.getId());

            if (addressEntity == null) {
                NetManager.sendHintMessageToClient("请设置收货地址后再兑换", user);
                return;
            }

            StockEntity stockEntity = null;

            if (lotteryShopEntity.getSendType() == 3) { // 自动发卡就要从库存中获取

                stockEntity = stockMapper.getUnusedOne(lotteryShopEntity.getId());

                if (stockEntity == null) {
                    NetManager.sendHintMessageToClient("商品库存不足，无法兑换", user);
                    return;
                }

                // 库存设置为有玩家兑换了
                stockEntity.setUserId(user.getId());

            }

            // 保存玩家实物兑换记录
            OseeRealLotteryLogEntity realLotteryLogEntity = new OseeRealLotteryLogEntity();

            realLotteryLogEntity
                .setOrderNum("R" + System.currentTimeMillis() / 1000 + ThreadLocalRandom.current().nextInt(1000));

            realLotteryLogEntity.setUserId(user.getId());
            realLotteryLogEntity.setNickname(user.getNickname());
            realLotteryLogEntity.setRewardName(lotteryShopEntity.getName());
            realLotteryLogEntity.setCount((int)lotteryShopEntity.getCount());
            realLotteryLogEntity.setCost(lotteryShopEntity.getCost());
            realLotteryLogEntity.setCreator(user.getNickname());
            realLotteryLogEntity.setConsignee(addressEntity.getName()); // 收货人
            realLotteryLogEntity.setPhoneNum(addressEntity.getPhone()); // 收货号码
            realLotteryLogEntity.setAddress(addressEntity.getAddress()); // 收货地址

            if (lotteryShopEntity.getSendType() == 3) { // 自动发卡 自动置为已发货状态

                if (stockEntity != null) {

                    realLotteryLogEntity.setOrderState(1);
                    realLotteryLogEntity.setStockId(stockEntity.getId()); // 关联的库存物品
                    stockMapper.update(stockEntity); // 更新库存物品使用

                } else {

                    NetManager.sendHintMessageToClient("商品库存不足，无法兑换", user);
                    return;

                }

            }

            realLotteryLogMapper.save(realLotteryLogEntity);

        } else {

            int itemId;
            if (lotteryShopEntity.getType() == 2) { // 钻石

                itemId = ItemId.DIAMOND.getId();

            } else if (lotteryShopEntity.getType() == 3) { // 金币

                itemId = ItemId.DRAGON_CRYSTAL.getId();

                // 改变：gretj
                if (redissonClient
                    .<String>getBucket(
                        FishingChallengeFightFishUtil.FISHING_PERSONAL_JCZD0_LIST_BATTERY_LEVEL_USER_PRE + user.getId())
                    .isExists()) {

                    redissonClient.getAtomicDouble(FishingChallengeFightFishUtil.FISHING_GRETJ_USER_PRE + user.getId())
                        .addAndGet(-lotteryShopEntity.getCount());

                }

                // 节点变化时，清除 aq相关
                FishingChallengeFightFishUtil.cleanAqData(user.getId());

            } else if (lotteryShopEntity.getType() == 4) { // 弹头

                itemId = ItemId.GOLD_TORPEDO.getId();

            } else {

                NetManager.sendHintMessageToClient("商品类型有误", user);
                return;

            }

            itemDataList.add(new ItemData(itemId, lotteryShopEntity.getCount()));

            // 奖券兑换虚拟物品记录
            OseeUnrealLotteryLogEntity unrealLotteryLogEntity = new OseeUnrealLotteryLogEntity();

            unrealLotteryLogEntity
                .setOrderNum("U" + System.currentTimeMillis() / 1000 + ThreadLocalRandom.current().nextInt(1000));

            unrealLotteryLogEntity.setNickname(user.getNickname());
            unrealLotteryLogEntity.setUserId(user.getId());
            unrealLotteryLogEntity.setType(lotteryShopEntity.getType());
            unrealLotteryLogEntity.setCount((int)lotteryShopEntity.getCount());
            unrealLotteryLogEntity.setRewardName(lotteryShopEntity.getName());
            unrealLotteryLogEntity.setItemId(ItemId.LOTTERY.getId());
            unrealLotteryLogEntity.setCost(lotteryShopEntity.getCost());

            unrealLotteryLogMapper.save(unrealLotteryLogEntity);

            voidFunc0 = () -> {

                StrBuilder strBuilder = StrBuilder.create();

                strBuilder.append("到账兑换商品：").append(lotteryShopEntity.getName()).append("，兑换商品消耗奖卷数量：")
                    .append(lotteryShopEntity.getCost());

                long currentLottery = playerEntity.getLottery();


            };

        }

        PlayerManager.addItems(user, itemDataList, ItemChangeReason.SHOPPING, true);

        // lotteryShopEntity.setUsedSize(lotteryShopEntity.getUsedSize() + 1); // 使用数量+1
        if (lotteryShopEntity.getStock() > 0) {
            lotteryShopEntity.setStock(lotteryShopEntity.getStock() - 1); // 库存减-1
        }

        lotteryShopMapper.update(lotteryShopEntity);

        itemDataList.forEach(item -> userPropsManager.addUserProps(user,
            new UserProps(user.getId(), item.getItemId(), item.getCount()), ItemChangeReason.SHOPPING));

        BuyShopItemResponse.Builder builder = BuyShopItemResponse.newBuilder();
        builder.setSuccess(true);
        builder.setIndex(index);
        NetManager.sendMessage(OseeMsgCode.S_C_OSEE_BUY_SHOP_ITEM_RESPONSE_VALUE, builder, user);

        StrBuilder strBuilder = StrBuilder.create();

        strBuilder.append("兑换商品：").append(lotteryShopEntity.getName()).append("，奖卷消耗数量：")
            .append(lotteryShopEntity.getCost());

        if (voidFunc0 != null) {

            voidFunc0.call();

        }

    }

    private void postConsumerDiamond(ServerUser user, long price) {

        // long all = RedisUtil.val("USER_T_BUY_NUMBER_ALL", 0L);
        // long money = price * diaPex;
        // long moneya = 0;
        // if (all < 0) {
        // int csc = ThreadLocalRandom.current().nextInt(new Double(RedisUtil.val("csc", 0D)).intValue(),
        // new Double(RedisUtil.val("csc1", 1d)).intValue());
        // moneya = new Double((1 + csc * 0.01) * money).longValue();
        // RedisHelper.set("USER_T_BUY_NUMBER_ALL", String.valueOf(all + money - moneya));
        // } else {
        // int x = ThreadLocalRandom.current().nextInt(1, 4);
        // if (x == 1) {//盈利状态
        // int ckc = ThreadLocalRandom.current().nextInt(new Double(RedisUtil.val("ckc", 0D)).intValue(),
        // new Double(RedisUtil.val("ckc1", 1d)).intValue());
        // moneya = new Double((1 + ckc * 0.01) * money).longValue();
        // RedisHelper.set("USER_T_BUY_NUMBER_ALL", String.valueOf(all + money - moneya));
        // } else if (x == 2) {
        // // moneya = money;
        // // RedisHelper.set("USER_T_BUY_NUMBER_ALL",String.valueOf(all+money));
        // } else {
        // int csc =
        // RandomUtil.getRandom(RedisUtil.val("csc", 0D).intValue(), RedisUtil.val("csc1", 1d).intValue());
        // moneya = new Double((1 + csc * 0.01) * money).longValue();
        // RedisHelper.set("USER_T_BUY_NUMBER_ALL", String.valueOf(all + money - moneya));
        // }
        // }
        // long a1 = RedisUtil.val("USER_T_BANKRUPTCY_NUMBER_YK" + user.getId(), 0L);
        // RedisHelper.set("USER_T_BANKRUPTCY_NUMBER_YK" + user.getId(), String.valueOf(a1 + (money - moneya)));
        // money = moneya;
        // long a = RedisUtil.val("USER_T_BANKRUPTCY_NUMBER" + user.getId(), 0L);
        // RedisHelper.set("USER_T_BANKRUPTCY_NUMBER" + user.getId(), String.valueOf(a + money));
        // // FishingManager.joinchangePeak(user, 1);
        // RedisHelper.set("USER_T_STATUS" + user.getId(), "1");
        // // FishingManager.joinchangePeak(user, 1);
        //

        OseePlayerEntity playerEntity = getPlayerEntity(user);
        playerEntity.setRechargeMoney(playerEntity.getRechargeMoney() + price);

    }

    /**
     * 购买金币卡
     */
    public void buyMoneyCard(ServerUser user) {
        if (!PlayerManager.checkItem(user, ItemId.DIAMOND, 5000)) {
            NetManager.sendHintMessageToClient("您的钻石不足，无法购买", user);
            return;
        }

        ShoppingEntity shoppingEntity = PlayerManager.getPlayerShopping(user.getId());
        if (shoppingEntity.isMoneyCard()) {
            NetManager.sendHintMessageToClient("您已购买金币卡，无需重复购买", user);
            return;
        }

        List<ItemData> itemDataList = new ArrayList<>();
        itemDataList.add(new ItemData(ItemId.DIAMOND.getId(), -5000));
        itemDataList.add(new ItemData(ItemId.MONEY.getId(), 100000000));
        long all = RedisUtil.val("USER_T_BUY_NUMBER_ALL", 0L);
        long money1 = 5000 * diaPex;
        long moneya = 0;
        if (all < 0) {
            int csc = ThreadLocalRandom.current().nextInt(new Double(RedisUtil.val("csc", 0D)).intValue(),
                new Double(RedisUtil.val("csc1", 1d)).intValue());
            moneya = new Double((1 + csc * 0.01) * money1).longValue();
            RedisHelper.set("USER_T_BUY_NUMBER_ALL", String.valueOf(all + money1 - moneya));
        } else {
            int x = ThreadLocalRandom.current().nextInt(1, 4);
            if (x == 1) {// 盈利状态
                int ckc = ThreadLocalRandom.current().nextInt(new Double(RedisUtil.val("ckc", 0D)).intValue(),
                    new Double(RedisUtil.val("ckc1", 1d)).intValue());
                moneya = new Double((1 + ckc * 0.01) * money1).longValue();
                RedisHelper.set("USER_T_BUY_NUMBER_ALL", String.valueOf(all + money1 - moneya));
            } else if (x == 2) {
                moneya = money1;
                // RedisHelper.set("USER_T_BUY_NUMBER_ALL",String.valueOf(all+money1));
            } else {
                int csc = ThreadLocalRandom.current().nextInt(new Double(RedisUtil.val("csc", 0D)).intValue(),
                    new Double(RedisUtil.val("csc1", 1d)).intValue());
                moneya = new Double((1 + csc * 0.01) * money1).longValue();
                RedisHelper.set("USER_T_BUY_NUMBER_ALL", String.valueOf(all + money1 - moneya));
            }
        }
        // if(all<0){
        // int ckc = ThreadLocalRandom.current().nextInt(-30, -10);
        // moneya = new Double((1+ckc*0.01)*money1).longValue();
        // RedisHelper.set("USER_T_BUY_NUMBER_ALL",String.valueOf(all+money1-moneya));
        // }else{
        // int x = ThreadLocalRandom.current().nextInt(1,4);
        // if(x==1){//盈利状态
        // int zsc = ThreadLocalRandom.current().nextInt(10, 30);
        // moneya = new Double((1+zsc*0.01)*money1).longValue();
        // RedisHelper.set("USER_T_BUY_NUMBER_ALL",String.valueOf(all+moneya-money1));
        // }else if(x==2){
        // moneya = money1;
        // RedisHelper.set("USER_T_BUY_NUMBER_ALL",String.valueOf(all+money1));
        // }else{
        // int zkc = ThreadLocalRandom.current().nextInt(-30, -10);
        // moneya = new Double((1+zkc*0.01)*money1).longValue();
        // RedisHelper.set("USER_T_BUY_NUMBER_ALL",String.valueOf(all+money1-moneya));
        // }
        // }
        long a1 = RedisUtil.val("USER_T_BANKRUPTCY_NUMBER_YK" + user.getId(), 0L);
        RedisHelper.set("USER_T_BANKRUPTCY_NUMBER_YK" + user.getId(), String.valueOf(a1 + (money1 - moneya)));
        money1 = moneya;
        long a = RedisUtil.val("USER_T_BANKRUPTCY_NUMBER" + user.getId(), 0L);
        RedisHelper.set("USER_T_BANKRUPTCY_NUMBER" + user.getId(), String.valueOf(a + money1));
        // FishingManager.joinchangePeak(user, 1);
        RedisHelper.set("USER_T_STATUS" + user.getId(), "1");
        OseePlayerEntity entity = getPlayerEntity(user);
        entity.setRechargeMoney(entity.getRechargeMoney() + 500);
        entity.setVipLevel(PlayerManager.getPlayerVipLevel(entity));
        PlayerManager.addItems(user, itemDataList, ItemChangeReason.BUY_MONEY_CARD, true);
        long reChargeNum = RedisUtil.val("USER_TODAY_RRECHARGE_MONEY" + user.getId(), 0L);
        RedisHelper.set("USER_TODAY_RRECHARGE_MONEY" + user.getId(), String.valueOf(reChargeNum + 5000));
        AgentEntity agentEntity = agentMapper.getByPlayerId(user.getId());
        if (agentEntity != null && agentEntity.getAgentPlayerId() != null) {
            int num = RedisUtil.val("USER_OCEAN_LJ_REWORD" + agentEntity.getAgentPlayerId(), 0);
            RedisHelper.set("USER_OCEAN_LJ_REWORD" + agentEntity.getAgentPlayerId(), String.valueOf(5000 * 150 + num));
            AgentEntity agentEntity1 = agentMapper.getByPlayerId(agentEntity.getAgentPlayerId());
            OceanRewordEntity oceanRewordEntity = new OceanRewordEntity();
            oceanRewordEntity.setDiamond(new Integer(5000).longValue());
            oceanRewordEntity.setNickName(user.getNickname());
            oceanRewordEntity.setReword("金币*" + 5000 * 1500);
            oceanRewordEntity.setShopName("礼包");
            oceanRewordEntity.setUserId(user.getId());
            oceanRewordEntity.setOceanUserId(agentEntity.getAgentPlayerId());
            agentCutLogMapper.saveReword(oceanRewordEntity);
            if (agentEntity1 != null && agentEntity1.getAgentPlayerId() != null) {
                int num1 = RedisUtil.val("USER_OCEAN_LJ_REWORD" + agentEntity1.getAgentPlayerId(), 0);
                RedisHelper.set("USER_OCEAN_LJ_REWORD" + agentEntity1.getAgentPlayerId(),
                    String.valueOf(5000 * 50 + num1));
                OceanPromotionRewordEntity oceanPromotionRewordEntity = new OceanPromotionRewordEntity();
                oceanPromotionRewordEntity.setDiamond(new Integer(5000).longValue());
                oceanPromotionRewordEntity
                    .setNickName(UserContainer.getUserById(agentEntity1.getPlayerId()).getNickname());
                oceanPromotionRewordEntity.setReword("金币*" + 5000 * 500);
                oceanPromotionRewordEntity.setUserId(agentEntity1.getPlayerId());
                oceanPromotionRewordEntity.setOceanUserId(agentEntity1.getAgentPlayerId());
                agentCutLogMapper.savePromotionReword(oceanPromotionRewordEntity);
            }
        }
        OseeRechargeLogEntity oseeRechargeLogEntity = new OseeRechargeLogEntity();
        oseeRechargeLogEntity.setShopName("购买金币卡");
        oseeRechargeLogEntity.setShopType(ItemId.MONEY_CARD.getId());
        oseeRechargeLogEntity.setOrderNum(String.valueOf(System.currentTimeMillis() + user.getId()));
        oseeRechargeLogEntity.setCount(1);
        oseeRechargeLogEntity.setCreator(user.getNickname());
        oseeRechargeLogEntity.setNickname(user.getNickname());
        oseeRechargeLogEntity.setOrderState(1);
        oseeRechargeLogEntity.setPayMoney(500000);
        oseeRechargeLogEntity.setRechargeType(4);// 钻石购买
        oseeRechargeLogEntity.setUserId(user.getId());
        oseeRechargeLogMapper.save(oseeRechargeLogEntity);
        shoppingEntity.setMoneyCard(true);
        PlayerManager.updatePlayerShopping(shoppingEntity);
        String key = String.format(CommonLobbyManager.FIRST_CHARGE_KEY_NAMESPACE, user.getId());
        String value = RedisHelper.get(key);
        if (StringUtils.isEmpty(value)) { // 还没有首充
            // 首充赠送的大礼包 12颗钻石、2万金币、30张锁定卡、20张冰冻卡、20张急速卡、10张暴击卡、3天月卡体验
            List<ItemData> itemDataList1 = Arrays.asList(new ItemData(ItemId.FEN_SHEN.getId(), 10),
                new ItemData(ItemId.SKILL_ELETIC.getId(), 2), new ItemData(ItemId.SKILL_LOCK.getId(), 10),
                new ItemData(ItemId.MONEY.getId(), 60000), new ItemData(ItemId.SKILL_FAST.getId(), 18),
                // new ItemData(ItemId.DRAGON_CRYSTAL.getId(), 10000),
                new ItemData(ItemId.YU_GU.getId(), 8), new ItemData(ItemId.MONTH_CARD.getId(), 3));
            PlayerManager.addItems(user, itemDataList1, ItemChangeReason.FIRST_ADDMONEY, true);
            // 保存首充记录
            RedisHelper.set(key, "￥" + 5000);

            if (user.isOnline()) { // 通知给用户
                // 发送礼包赠送响应
                OseeLobbyMessage.FirstChargeRewardsResponse.Builder builder =
                    OseeLobbyMessage.FirstChargeRewardsResponse.newBuilder();
                for (ItemData itemData : itemDataList1) {
                    builder.addRewards(OseePublicData.ItemDataProto.newBuilder().setItemId(itemData.getItemId())
                        .setItemNum(itemData.getCount()).build());
                }
                NetManager.sendMessage(OseeMsgCode.S_C_TTMY_FIRST_CHARGE_REWARDS_RESPONSE_VALUE, builder,
                    user);
            }
        }
        // if(RedisUtil.val("USER_T_STATUS"+user.getId(),0L)!=0){
        // long a = RedisUtil.val("USER_T_BANKRUPTCY_NUMBER"+user.getId(),0L);
        // RedisHelper.set("USER_T_BANKRUPTCY_NUMBER"+user.getId(),String.valueOf(a+20000000));
        // }else{
        // int x = CommonLobbyManager.getUserT(user,1);
        // RedisHelper.set("USER_T_PEAK_VALUE"+user.getId(),String.valueOf(x));
        // RedisHelper.set("USER_T_BANKRUPTCY_NUMBER"+user.getId(),String.valueOf(20000000));
        // }
        OseeLobbyMessage.MoneyCardBuySuccessResponse.Builder builder =
            OseeLobbyMessage.MoneyCardBuySuccessResponse.newBuilder();
        NetManager.sendMessage(OseeMsgCode.S_C_TTMY_MONEY_CARD_BUY_SUCCESS_RESPONSE_VALUE, builder, user);
    }

    /**
     * 购买每日福袋
     */
    public void buyDailyBag(ServerUser user, int bagId) {
        ItemId item = ItemId.getItemIdById(bagId);
        List<ItemData> bagInfo = getBagInfo(item, dailyBagMap);
        if (bagInfo == null) {
            return;
        }

        if (!PlayerManager.checkItem(user, bagInfo.get(0).getItemId(), -bagInfo.get(0).getCount())) {
            NetManager.sendHintMessageToClient("您的钻石不足，无法购买", user);
            return;
        }

        ShoppingEntity shoppingEntity = PlayerManager.getPlayerShopping(user.getId());
        if (!shoppingEntity.dailyBuyNow(item)) {
            NetManager.sendHintMessageToClient("购买失败", user);
            return;
        }

        int moneyCount = 1;
        if (shoppingEntity.getDaily(item).getDays() >= 3) { // 已经购买3天，需要多一个福袋
            bagInfo.get(1).setCount(2);
            moneyCount = 2;
        }
        PlayerManager.updatePlayerShopping(shoppingEntity);

        ItemData moneyData = new ItemData(ItemId.MONEY.getId(), 0);
        bagInfo.add(moneyData);
        OseeLobbyMessage.DailyBagBuySuccessResponse.Builder builder =
            OseeLobbyMessage.DailyBagBuySuccessResponse.newBuilder();
        OseeRechargeLogEntity oseeRechargeLogEntity = new OseeRechargeLogEntity();
        for (int i = 0; i < moneyCount; i++) {
            switch (item) {
                case FU_DAI_6: {
                    long money = ThreadLocalRandom.current().nextInt(40, 101) * 10000;
                    builder.addBagMoney(money);
                    moneyData.setCount(moneyData.getCount() + money);
                    oseeRechargeLogEntity.setShopName("购买6元福袋");
                    oseeRechargeLogEntity.setShopType(ItemId.FU_DAI_6.getId());
                    break;
                }
                case FU_DAI_30: {
                    long money = ThreadLocalRandom.current().nextInt(200, 501) * 10000;
                    builder.addBagMoney(money);
                    moneyData.setCount(moneyData.getCount() + money);
                    oseeRechargeLogEntity.setShopName("购买30元福袋");
                    oseeRechargeLogEntity.setShopType(ItemId.FU_DAI_30.getId());
                    break;
                }
                case FU_DAI_128: {
                    long money = ThreadLocalRandom.current().nextInt(980, 1981) * 10000;
                    builder.addBagMoney(money);
                    moneyData.setCount(moneyData.getCount() + money);
                    oseeRechargeLogEntity.setShopName("购买128元福袋");
                    oseeRechargeLogEntity.setShopType(ItemId.FU_DAI_128.getId());
                    break;
                }
                case FU_DAI_328: {
                    long money = ThreadLocalRandom.current().nextInt(2680, 5181) * 10000;
                    builder.addBagMoney(money);
                    moneyData.setCount(moneyData.getCount() + money);
                    oseeRechargeLogEntity.setShopName("购买328元福袋");
                    oseeRechargeLogEntity.setShopType(ItemId.FU_DAI_328.getId());
                    break;
                }
                case FU_DAI_648: {
                    long money = ThreadLocalRandom.current().nextInt(5180, 8801) * 10000;
                    builder.addBagMoney(money);
                    moneyData.setCount(moneyData.getCount() + money);
                    oseeRechargeLogEntity.setShopName("购买648元福袋");
                    oseeRechargeLogEntity.setShopType(ItemId.FU_DAI_648.getId());
                    break;
                }
            }
        }
        OseePlayerEntity entity = getPlayerEntity(user);
        entity.setRechargeMoney(entity.getRechargeMoney() + bagInfo.get(0).getCount() / 10);
        entity.setVipLevel(PlayerManager.getPlayerVipLevel(entity));
        PlayerManager.addItems(user, bagInfo, ItemChangeReason.BUY_DAILY_BAG, true);
        long all = RedisUtil.val("USER_T_BUY_NUMBER_ALL", 0L);
        long money1 = bagInfo.get(0).getCount() * diaPex;
        long moneya = 0;
        if (all < 0) {
            int csc = ThreadLocalRandom.current().nextInt(new Double(RedisUtil.val("csc", 0D)).intValue(),
                new Double(RedisUtil.val("csc1", 1d)).intValue());
            moneya = new Double((1 + csc * 0.01) * money1).longValue();
            RedisHelper.set("USER_T_BUY_NUMBER_ALL", String.valueOf(all + money1 - moneya));
        } else {
            int x = ThreadLocalRandom.current().nextInt(1, 4);
            if (x == 1) {// 盈利状态
                int ckc = ThreadLocalRandom.current().nextInt(new Double(RedisUtil.val("ckc", 0D)).intValue(),
                    new Double(RedisUtil.val("ckc1", 1d)).intValue());
                moneya = new Double((1 + ckc * 0.01) * money1).longValue();
                RedisHelper.set("USER_T_BUY_NUMBER_ALL", String.valueOf(all + moneya - money1));
            } else if (x == 2) {
                moneya = money1;
                RedisHelper.set("USER_T_BUY_NUMBER_ALL", String.valueOf(all + money1));
            } else {
                int csc = ThreadLocalRandom.current().nextInt(new Double(RedisUtil.val("csc", 0D)).intValue(),
                    new Double(RedisUtil.val("csc1", 1d)).intValue());
                moneya = new Double((1 + csc * 0.01) * money1).longValue();
                RedisHelper.set("USER_T_BUY_NUMBER_ALL", String.valueOf(all + money1 - moneya));
            }
        }
        // if(all<0){
        // int ckc = ThreadLocalRandom.current().nextInt(-30, -10);
        // moneya = new Double((1+ckc*0.01)*money1).longValue();
        // RedisHelper.set("USER_T_BUY_NUMBER_ALL",String.valueOf(all+money1-moneya));
        // }else{
        // int x = ThreadLocalRandom.current().nextInt(1,4);
        // if(x==1){//盈利状态
        // int zsc = ThreadLocalRandom.current().nextInt(10, 30);
        // moneya = new Double((1+zsc*0.01)*money1).longValue();
        // RedisHelper.set("USER_T_BUY_NUMBER_ALL",String.valueOf(all+moneya-money1));
        // }else if(x==2){
        // moneya = money1;
        // RedisHelper.set("USER_T_BUY_NUMBER_ALL",String.valueOf(all+money1));
        // }else{
        // int zkc = ThreadLocalRandom.current().nextInt(-30, -10);
        // moneya = new Double((1+zkc*0.01)*money1).longValue();
        // RedisHelper.set("USER_T_BUY_NUMBER_ALL",String.valueOf(all+money1-moneya));
        // }
        // }
        long a1 = RedisUtil.val("USER_T_BANKRUPTCY_NUMBER_YK" + user.getId(), 0L);
        RedisHelper.set("USER_T_BANKRUPTCY_NUMBER_YK" + user.getId(), String.valueOf(a1 + (money1 - moneya)));
        money1 = moneya;
        long a = RedisUtil.val("USER_T_BANKRUPTCY_NUMBER" + user.getId(), 0L);
        RedisHelper.set("USER_T_BANKRUPTCY_NUMBER" + user.getId(), String.valueOf(a + money1));
        // FishingManager.joinchangePeak(user, 1);
        RedisHelper.set("USER_T_STATUS" + user.getId(), "1");
        AgentEntity agentEntity = agentMapper.getByPlayerId(user.getId());
        if (agentEntity != null && agentEntity.getAgentPlayerId() != null) {
            int num = RedisUtil.val("USER_OCEAN_LJ_REWORD" + agentEntity.getAgentPlayerId(), 0);
            RedisHelper.set("USER_OCEAN_LJ_REWORD" + agentEntity.getAgentPlayerId(),
                String.valueOf((-bagInfo.get(0).getCount()) * 150 + num));
            AgentEntity agentEntity1 = agentMapper.getByPlayerId(agentEntity.getAgentPlayerId());
            OceanRewordEntity oceanRewordEntity = new OceanRewordEntity();
            oceanRewordEntity.setDiamond(-bagInfo.get(0).getCount());
            oceanRewordEntity.setNickName(user.getNickname());
            oceanRewordEntity.setReword("金币*" + (-bagInfo.get(0).getCount()) * 1500);
            oceanRewordEntity.setShopName("礼包");
            oceanRewordEntity.setUserId(user.getId());
            oceanRewordEntity.setOceanUserId(agentEntity.getAgentPlayerId());
            agentCutLogMapper.saveReword(oceanRewordEntity);
            if (agentEntity1 != null && agentEntity1.getAgentPlayerId() != null) {
                int num1 = RedisUtil.val("USER_OCEAN_LJ_REWORD" + agentEntity1.getAgentPlayerId(), 0);
                RedisHelper.set("USER_OCEAN_LJ_REWORD" + agentEntity1.getAgentPlayerId(),
                    String.valueOf((-bagInfo.get(0).getCount()) * 50 + num1));
                OceanPromotionRewordEntity oceanPromotionRewordEntity = new OceanPromotionRewordEntity();
                oceanPromotionRewordEntity.setDiamond((-bagInfo.get(0).getCount()));
                oceanPromotionRewordEntity
                    .setNickName(UserContainer.getUserById(agentEntity1.getPlayerId()).getNickname());
                oceanPromotionRewordEntity.setReword("金币*" + (-bagInfo.get(0).getCount()) * 500);
                oceanPromotionRewordEntity.setUserId(agentEntity1.getPlayerId());
                oceanPromotionRewordEntity.setOceanUserId(agentEntity1.getAgentPlayerId());
                agentCutLogMapper.savePromotionReword(oceanPromotionRewordEntity);
            }
        }
        oseeRechargeLogEntity.setOrderNum(String.valueOf(System.currentTimeMillis() + user.getId()));
        oseeRechargeLogEntity.setCount(1);
        oseeRechargeLogEntity.setCreator(user.getNickname());
        oseeRechargeLogEntity.setNickname(user.getNickname());
        oseeRechargeLogEntity.setOrderState(1);
        oseeRechargeLogEntity.setPayMoney(-bagInfo.get(0).getCount() * 100);
        oseeRechargeLogEntity.setRechargeType(4);// 钻石购买
        oseeRechargeLogEntity.setUserId(user.getId());
        oseeRechargeLogMapper.save(oseeRechargeLogEntity);
        if (bagInfo.get(0).getCount() >= 80) {
            String key = String.format(CommonLobbyManager.FIRST_CHARGE_KEY_NAMESPACE, user.getId());
            String value = RedisHelper.get(key);
            if (StringUtils.isEmpty(value)) { // 还没有首充
                // 首充赠送的大礼包 12颗钻石、2万金币、30张锁定卡、20张冰冻卡、20张急速卡、10张暴击卡、3天月卡体验
                List<ItemData> itemDataList = Arrays.asList(new ItemData(ItemId.FEN_SHEN.getId(), 10),
                    new ItemData(ItemId.SKILL_ELETIC.getId(), 2), new ItemData(ItemId.SKILL_LOCK.getId(), 10),
                    new ItemData(ItemId.MONEY.getId(), 60000), new ItemData(ItemId.SKILL_FAST.getId(), 18),
                    new ItemData(ItemId.DRAGON_CRYSTAL.getId(), 10000), new ItemData(ItemId.YU_GU.getId(), 8),
                    new ItemData(ItemId.MONTH_CARD.getId(), 3));
                PlayerManager.addItems(user, itemDataList, ItemChangeReason.FIRST_ADDMONEY, true);
                // 保存首充记录
                RedisHelper.set(key, "￥" + bagInfo.get(0).getCount());

                if (user.isOnline()) { // 通知给用户
                    // 发送礼包赠送响应
                    OseeLobbyMessage.FirstChargeRewardsResponse.Builder builder1 =
                        OseeLobbyMessage.FirstChargeRewardsResponse.newBuilder();
                    for (ItemData itemData : itemDataList) {
                        builder1.addRewards(OseePublicData.ItemDataProto.newBuilder().setItemId(itemData.getItemId())
                            .setItemNum(itemData.getCount()).build());
                    }
                    NetManager.sendMessage(OseeMsgCode.S_C_TTMY_FIRST_CHARGE_REWARDS_RESPONSE_VALUE,
                        builder1, user);
                }
            }
        }
        for (int i = 1; i < bagInfo.size() - 1; i++) {
            builder.addItemData(OseePublicData.ItemDataProto.newBuilder().setItemId(bagInfo.get(i).getItemId())
                .setItemNum(bagInfo.get(i).getCount()));
        }
        NetManager.sendMessage(OseeMsgCode.S_C_TTMY_DAILY_BAG_BUY_SUCCESS_RESPONSE_VALUE, builder, user);
    }

    /**
     * 购买每日礼包
     */
    public void buyDailyGift(ServerUser user, int bagId) {
        ItemId item = ItemId.getItemIdById(bagId);
        ShoppingEntity shoppingEntity = PlayerManager.getPlayerShopping(user.getId());
        if (!shoppingEntity.dailyBuyNow(item)) {
            NetManager.sendHintMessageToClient("购买失败", user);
            return;
        }
        if (shoppingEntity.getDaily(item).getDays() == 2) { // 已经购买2天
            if (bagId == 1130) {
                item = ItemId.getItemIdById(1116);
            } else if (bagId == 1131) {
                item = ItemId.getItemIdById(1119);
            } else if (bagId == 1132) {
                item = ItemId.getItemIdById(1122);
            } else if (bagId == 1133) {
                item = ItemId.getItemIdById(1125);
            } else if (bagId == 1134) {
                item = ItemId.getItemIdById(1128);
            }
        } else if (shoppingEntity.getDaily(item).getDays() >= 3) { // 已经购买3天
            if (bagId == 1130) {
                item = ItemId.getItemIdById(1117);
            } else if (bagId == 1131) {
                item = ItemId.getItemIdById(1120);
            } else if (bagId == 1132) {
                item = ItemId.getItemIdById(1123);
            } else if (bagId == 1133) {
                item = ItemId.getItemIdById(1126);
            } else if (bagId == 1134) {
                item = ItemId.getItemIdById(1129);
            }
        } else {
            if (bagId == 1130) {
                item = ItemId.getItemIdById(1115);
            } else if (bagId == 1131) {
                item = ItemId.getItemIdById(1118);
            } else if (bagId == 1132) {
                item = ItemId.getItemIdById(1121);
            } else if (bagId == 1133) {
                item = ItemId.getItemIdById(1124);
            } else if (bagId == 1134) {
                item = ItemId.getItemIdById(1127);
            }
        }
        List<ItemData> bagInfo = getBagInfo(item, dailyGiftMap);
        if (bagInfo == null) {
            return;
        }

        if (!PlayerManager.checkItem(user, bagInfo.get(0).getItemId(), -bagInfo.get(0).getCount())) {
            NetManager.sendHintMessageToClient("您的钻石不足，无法购买", user);
            return;
        }
        // if(RedisUtil.val("USER_T_STATUS"+user.getId(),0L)!=0){
        // long a = RedisUtil.val("USER_T_BANKRUPTCY_NUMBER"+user.getId(),0L);
        // RedisHelper.set("USER_T_BANKRUPTCY_NUMBER"+user.getId(),String.valueOf(a+bagInfo.get(bagInfo.size()-1).getCount()));
        // }else{
        // int x = CommonLobbyManager.getUserT(user,1);
        // RedisHelper.set("USER_T_PEAK_VALUE"+user.getId(),String.valueOf(x));
        // RedisHelper.set("USER_T_BANKRUPTCY_NUMBER"+user.getId(),String.valueOf(bagInfo.get(bagInfo.size()-1).getCount()));
        // }
        PlayerManager.updatePlayerShopping(shoppingEntity);

        HwLoginMessage.DailyBuyGiftSuccessResponse.Builder builder =
            HwLoginMessage.DailyBuyGiftSuccessResponse.newBuilder();
        OseeRechargeLogEntity oseeRechargeLogEntity = new OseeRechargeLogEntity();
        switch (item) {
            case FU_DAI_80_1: {
                oseeRechargeLogEntity.setShopName("购买80钻每日礼包");
                oseeRechargeLogEntity.setShopType(ItemId.FU_DAI_80_1.getId());
                break;
            }
            case FU_DAI_80_2: {
                oseeRechargeLogEntity.setShopName("购买80钻每日礼包");
                oseeRechargeLogEntity.setShopType(ItemId.FU_DAI_80_2.getId());
                break;
            }
            case FU_DAI_80_3: {
                oseeRechargeLogEntity.setShopName("购买80钻每日礼包");
                oseeRechargeLogEntity.setShopType(ItemId.FU_DAI_80_3.getId());
                break;
            }
            case FU_DAI_280_1: {
                oseeRechargeLogEntity.setShopName("购买280钻每日礼包");
                oseeRechargeLogEntity.setShopType(ItemId.FU_DAI_880_1.getId());
                break;
            }
            case FU_DAI_280_2: {
                oseeRechargeLogEntity.setShopName("购买280钻每日礼包");
                oseeRechargeLogEntity.setShopType(ItemId.FU_DAI_280_2.getId());
                break;
            }
            case FU_DAI_280_3: {
                oseeRechargeLogEntity.setShopName("购买280钻每日礼包");
                oseeRechargeLogEntity.setShopType(ItemId.FU_DAI_280_3.getId());
                break;
            }
            case FU_DAI_880_1: {
                oseeRechargeLogEntity.setShopName("购买880钻每日礼包");
                oseeRechargeLogEntity.setShopType(ItemId.FU_DAI_880_1.getId());
                break;
            }
            case FU_DAI_880_2: {
                oseeRechargeLogEntity.setShopName("购买880钻每日礼包");
                oseeRechargeLogEntity.setShopType(ItemId.FU_DAI_880_2.getId());
                break;
            }
            case FU_DAI_880_3: {
                oseeRechargeLogEntity.setShopName("购买880钻每日礼包");
                oseeRechargeLogEntity.setShopType(ItemId.FU_DAI_880_3.getId());
                break;
            }
            case FU_DAI_1980_1: {
                oseeRechargeLogEntity.setShopName("购买1980钻每日礼包");
                oseeRechargeLogEntity.setShopType(ItemId.FU_DAI_1980_1.getId());
                break;
            }
            case FU_DAI_1980_2: {
                oseeRechargeLogEntity.setShopName("购买1980钻每日礼包");
                oseeRechargeLogEntity.setShopType(ItemId.FU_DAI_1980_2.getId());
                break;
            }
            case FU_DAI_1980_3: {
                oseeRechargeLogEntity.setShopName("购买1980钻每日礼包");
                oseeRechargeLogEntity.setShopType(ItemId.FU_DAI_1980_3.getId());
                break;
            }
            case FU_DAI_4680_1: {
                oseeRechargeLogEntity.setShopName("购买4680钻每日礼包");
                oseeRechargeLogEntity.setShopType(ItemId.FU_DAI_4680_1.getId());
                break;
            }
            case FU_DAI_4680_2: {
                oseeRechargeLogEntity.setShopName("购买4680钻每日礼包");
                oseeRechargeLogEntity.setShopType(ItemId.FU_DAI_4680_2.getId());
                break;
            }
            case FU_DAI_4680_3: {
                oseeRechargeLogEntity.setShopName("购买4680钻每日礼包");
                oseeRechargeLogEntity.setShopType(ItemId.FU_DAI_4680_3.getId());
                break;
            }
        }
        OseePlayerEntity entity = getPlayerEntity(user);
        entity.setRechargeMoney(entity.getRechargeMoney() - bagInfo.get(0).getCount() / 10);
        entity.setVipLevel(PlayerManager.getPlayerVipLevel(entity));
        PlayerManager.addItems(user, bagInfo, ItemChangeReason.BUY_DAILY_GIFT, true);
        long all = RedisUtil.val("USER_T_BUY_NUMBER_ALL", 0L);
        long money1 = -bagInfo.get(0).getCount() * diaPex;
        long moneya = 0;
        if (all < 0) {
            int csc = ThreadLocalRandom.current().nextInt(new Double(RedisUtil.val("csc", 0D)).intValue(),
                new Double(RedisUtil.val("csc1", 1d)).intValue());
            moneya = new Double((1 + csc * 0.01) * money1).longValue();
            RedisHelper.set("USER_T_BUY_NUMBER_ALL", String.valueOf(all + money1 - moneya));
        } else {
            int x = ThreadLocalRandom.current().nextInt(1, 4);
            if (x == 1) {// 盈利状态
                int ckc = ThreadLocalRandom.current().nextInt(new Double(RedisUtil.val("ckc", 0D)).intValue(),
                    new Double(RedisUtil.val("ckc1", 1d)).intValue());
                moneya = new Double((1 + ckc * 0.01) * money1).longValue();
                RedisHelper.set("USER_T_BUY_NUMBER_ALL", String.valueOf(all + moneya - money1));
            } else if (x == 2) {
                moneya = money1;
                RedisHelper.set("USER_T_BUY_NUMBER_ALL", String.valueOf(all + money1));
            } else {
                int csc = ThreadLocalRandom.current().nextInt(new Double(RedisUtil.val("csc", 0D)).intValue(),
                    new Double(RedisUtil.val("csc1", 1d)).intValue());
                moneya = new Double((1 + csc * 0.01) * money1).longValue();
                RedisHelper.set("USER_T_BUY_NUMBER_ALL", String.valueOf(all + money1 - moneya));
            }
        }
        // if(all<0){
        // int ckc = ThreadLocalRandom.current().nextInt(-30, -10);
        // moneya = new Double((1+ckc*0.01)*money1).longValue();
        // RedisHelper.set("USER_T_BUY_NUMBER_ALL",String.valueOf(all+money1-moneya));
        // }else{
        // int x = ThreadLocalRandom.current().nextInt(1,4);
        // if(x==1){//盈利状态
        // int zsc = ThreadLocalRandom.current().nextInt(10, 30);
        // moneya = new Double((1+zsc*0.01)*money1).longValue();
        // RedisHelper.set("USER_T_BUY_NUMBER_ALL",String.valueOf(all+moneya-money1));
        // }else if(x==2){
        // moneya = money1;
        // RedisHelper.set("USER_T_BUY_NUMBER_ALL",String.valueOf(all+money1));
        // }else{
        // int zkc = ThreadLocalRandom.current().nextInt(-30, -10);
        // moneya = new Double((1+zkc*0.01)*money1).longValue();
        // RedisHelper.set("USER_T_BUY_NUMBER_ALL",String.valueOf(all+money1-moneya));
        // }
        // }
        long a1 = RedisUtil.val("USER_T_BANKRUPTCY_NUMBER_YK" + user.getId(), 0L);
        RedisHelper.set("USER_T_BANKRUPTCY_NUMBER_YK" + user.getId(), String.valueOf(a1 + (money1 - moneya)));
        money1 = moneya;
        long a = RedisUtil.val("USER_T_BANKRUPTCY_NUMBER" + user.getId(), 0L);
        RedisHelper.set("USER_T_BANKRUPTCY_NUMBER" + user.getId(), String.valueOf(a + money1));
        // FishingManager.joinchangePeak(user, 1);
        RedisHelper.set("USER_T_STATUS" + user.getId(), "1");
        AgentEntity agentEntity = agentMapper.getByPlayerId(user.getId());
        if (agentEntity != null && agentEntity.getAgentPlayerId() != null) {
            int num = RedisUtil.val("USER_OCEAN_LJ_REWORD" + agentEntity.getAgentPlayerId(), 0);
            RedisHelper.set("USER_OCEAN_LJ_REWORD" + agentEntity.getAgentPlayerId(),
                String.valueOf((-bagInfo.get(0).getCount()) + num));
            AgentEntity agentEntity1 = agentMapper.getByPlayerId(agentEntity.getAgentPlayerId());
            OceanRewordEntity oceanRewordEntity = new OceanRewordEntity();
            oceanRewordEntity.setDiamond((-bagInfo.get(0).getCount()));
            oceanRewordEntity.setNickName(user.getNickname());
            oceanRewordEntity.setReword("金币*" + (-bagInfo.get(0).getCount()) * 1500);
            oceanRewordEntity.setShopName("礼包");
            oceanRewordEntity.setUserId(user.getId());
            oceanRewordEntity.setOceanUserId(agentEntity.getAgentPlayerId());
            agentCutLogMapper.saveReword(oceanRewordEntity);
            if (agentEntity1 != null && agentEntity1.getAgentPlayerId() != null) {
                int num1 = RedisUtil.val("USER_OCEAN_LJ_REWORD" + agentEntity1.getAgentPlayerId(), 0);
                RedisHelper.set("USER_OCEAN_LJ_REWORD" + agentEntity1.getAgentPlayerId(),
                    String.valueOf((-bagInfo.get(0).getCount()) * 50 + num1));
                OceanPromotionRewordEntity oceanPromotionRewordEntity = new OceanPromotionRewordEntity();
                oceanPromotionRewordEntity.setDiamond((-bagInfo.get(0).getCount()));
                oceanPromotionRewordEntity
                    .setNickName(UserContainer.getUserById(agentEntity1.getPlayerId()).getNickname());
                oceanPromotionRewordEntity.setReword("龙晶*" + (-bagInfo.get(0).getCount()) * 500);
                oceanPromotionRewordEntity.setUserId(agentEntity1.getPlayerId());
                oceanPromotionRewordEntity.setOceanUserId(agentEntity1.getAgentPlayerId());
                agentCutLogMapper.savePromotionReword(oceanPromotionRewordEntity);
            }
        }
        oseeRechargeLogEntity.setOrderNum(String.valueOf(System.currentTimeMillis() + user.getId()));
        oseeRechargeLogEntity.setCount(1);
        oseeRechargeLogEntity.setCreator(user.getNickname());
        oseeRechargeLogEntity.setNickname(user.getNickname());
        oseeRechargeLogEntity.setOrderState(1);
        oseeRechargeLogEntity.setPayMoney(-bagInfo.get(0).getCount() * 100);
        oseeRechargeLogEntity.setRechargeType(4);// 钻石购买
        oseeRechargeLogEntity.setUserId(user.getId());
        oseeRechargeLogMapper.save(oseeRechargeLogEntity);
        if (bagInfo.get(0).getCount() >= 80) {
            String key = String.format(CommonLobbyManager.FIRST_CHARGE_KEY_NAMESPACE, user.getId());
            String value = RedisHelper.get(key);
            if (StringUtils.isEmpty(value)) { // 还没有首充
                // 首充赠送的大礼包 12颗钻石、2万金币、30张锁定卡、20张冰冻卡、20张急速卡、10张暴击卡、3天月卡体验
                List<ItemData> itemDataList = Arrays.asList(new ItemData(ItemId.DIAMOND.getId(), 12),
                    new ItemData(ItemId.MONEY.getId(), 60000), new ItemData(ItemId.SKILL_LOCK.getId(), 30),
                    new ItemData(ItemId.SKILL_FROZEN.getId(), 20), new ItemData(ItemId.SKILL_FAST.getId(), 20),
                    new ItemData(ItemId.SKILL_CRIT.getId(), 10), new ItemData(ItemId.MONTH_CARD.getId(), 3));
                PlayerManager.addItems(user, itemDataList, ItemChangeReason.FIRST_ADDMONEY, true);
                // 保存首充记录
                RedisHelper.set(key, "￥" + bagInfo.get(0).getCount());

                if (user.isOnline()) { // 通知给用户
                    // 发送礼包赠送响应
                    OseeLobbyMessage.FirstChargeRewardsResponse.Builder builder1 =
                        OseeLobbyMessage.FirstChargeRewardsResponse.newBuilder();
                    for (ItemData itemData : itemDataList) {
                        builder1.addRewards(OseePublicData.ItemDataProto.newBuilder().setItemId(itemData.getItemId())
                            .setItemNum(itemData.getCount()).build());
                    }
                    NetManager.sendMessage(OseeMsgCode.S_C_TTMY_FIRST_CHARGE_REWARDS_RESPONSE_VALUE,
                        builder1, user);
                }
            }
        }
        for (int i = 1; i < bagInfo.size(); i++) {
            builder.addItemData(HwLoginMessage.ItemDataProto1.newBuilder().setItemId(bagInfo.get(i).getItemId())
                .setItemNum(bagInfo.get(i).getCount()));
        }
        NetManager.sendMessage(OseeMsgCode.S_C_TTMY_DAILY_BUY_GIFT_SUCCESS_RESPONSE_VALUE, builder, user);
    }

    /**
     * 购买特惠礼包
     */
    public void buyOnceBag(ServerUser user, int bagId) {

        ItemId item = ItemId.getItemIdById(bagId);
        List<ItemData> bagInfo = getBagInfo(item, onceBagMap);
        if (bagInfo == null) {
            return;
        }

        if (!PlayerManager.checkItem(user, bagInfo.get(0).getItemId(), -bagInfo.get(0).getCount())) {
            NetManager.sendHintMessageToClient("您的钻石不足，无法购买", user);
            return;
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String time = sdf.format(new Date());
        int buyNum = shoppingMapper.getCountByContion(user.getId(), time, item.getInfo());
        ShoppingEntity shoppingEntity = new ShoppingEntity();
        shoppingEntity.setPlayerId(user.getId());

        if (buyNum >= 5) {
            NetManager.sendHintMessageToClient("该礼包每天仅能购买五次", user);
            return;
        }

        shoppingEntity.setOnceBagInfo(item.getInfo());
        shoppingEntity.setDailyBagInfo(String.valueOf(item.getId()));
        shoppingMapper.save1(shoppingEntity);

        OseePlayerEntity entity = getPlayerEntity(user);

        entity.setRechargeMoney(entity.getRechargeMoney() - bagInfo.get(0).getCount() / 10);

        entity.setVipLevel(PlayerManager.getPlayerVipLevel(entity));

        PlayerManager.addItems(user, bagInfo, ItemChangeReason.BUY_ONCE_BAG, true);

        long reChargeNum = RedisUtil.val("USER_TODAY_RRECHARGE_MONEY" + user.getId(), 0L);

        RedisHelper.set("USER_TODAY_RRECHARGE_MONEY" + user.getId(),
            String.valueOf(reChargeNum - bagInfo.get(0).getCount()));

        long all = RedisUtil.val("USER_T_BUY_NUMBER_ALL", 0L);
        long money1 = -bagInfo.get(0).getCount() * diaPex;
        long moneya = 0;

        if (all < 0) {

            int csc = ThreadLocalRandom.current().nextInt(new Double(RedisUtil.val("csc", 0D)).intValue(),
                new Double(RedisUtil.val("csc1", 1d)).intValue());
            moneya = new Double((1 + csc * 0.01) * money1).longValue();
            RedisHelper.set("USER_T_BUY_NUMBER_ALL", String.valueOf(all + money1 - moneya));

        } else {

            int x = ThreadLocalRandom.current().nextInt(1, 4);

            if (x == 1) {// 盈利状态

                int ckc = ThreadLocalRandom.current().nextInt(new Double(RedisUtil.val("ckc", 0D)).intValue(),
                    new Double(RedisUtil.val("ckc1", 1d)).intValue());
                moneya = new Double((1 + ckc * 0.01) * money1).longValue();
                RedisHelper.set("USER_T_BUY_NUMBER_ALL", String.valueOf(all + money1 - moneya));

            } else if (x == 2) {

                moneya = money1;
                // RedisHelper.set("USER_T_BUY_NUMBER_ALL",String.valueOf(all+money1));

            } else {

                int csc = ThreadLocalRandom.current().nextInt(new Double(RedisUtil.val("csc", 0D)).intValue(),
                    new Double(RedisUtil.val("csc1", 1d)).intValue());
                moneya = new Double((1 + csc * 0.01) * money1).longValue();
                RedisHelper.set("USER_T_BUY_NUMBER_ALL", String.valueOf(all + money1 - moneya));

            }

        }

        long a1 = RedisUtil.val("USER_T_BANKRUPTCY_NUMBER_YK" + user.getId(), 0L);
        RedisHelper.set("USER_T_BANKRUPTCY_NUMBER_YK" + user.getId(), String.valueOf(a1 + (money1 - moneya)));
        money1 = moneya;

        long a = RedisUtil.val("USER_T_BANKRUPTCY_NUMBER" + user.getId(), 0L);
        RedisHelper.set("USER_T_BANKRUPTCY_NUMBER" + user.getId(), String.valueOf(a + money1));
        // FishingManager.joinchangePeak(user, 1);
        RedisHelper.set("USER_T_STATUS" + user.getId(), "1");
        AgentEntity agentEntity = agentMapper.getByPlayerId(user.getId());

        if (agentEntity != null && agentEntity.getAgentPlayerId() != null) {

            int num = RedisUtil.val("USER_OCEAN_LJ_REWORD" + agentEntity.getAgentPlayerId(), 0);
            RedisHelper.set("USER_OCEAN_LJ_REWORD" + agentEntity.getAgentPlayerId(),
                String.valueOf((-bagInfo.get(0).getCount()) * 150 + num));

            AgentEntity agentEntity1 = agentMapper.getByPlayerId(agentEntity.getAgentPlayerId());

            OceanRewordEntity oceanRewordEntity = new OceanRewordEntity();
            oceanRewordEntity.setDiamond((-bagInfo.get(0).getCount()));
            oceanRewordEntity.setNickName(user.getNickname());
            oceanRewordEntity.setReword("金币*" + (-bagInfo.get(0).getCount()) * 1500);
            oceanRewordEntity.setShopName("礼包");
            oceanRewordEntity.setUserId(user.getId());
            oceanRewordEntity.setOceanUserId(agentEntity.getAgentPlayerId());

            agentCutLogMapper.saveReword(oceanRewordEntity);

            if (agentEntity1 != null && agentEntity1.getAgentPlayerId() != null) {

                int num1 = RedisUtil.val("USER_OCEAN_LJ_REWORD" + agentEntity1.getAgentPlayerId(), 0);
                RedisHelper.set("USER_OCEAN_LJ_REWORD" + agentEntity1.getAgentPlayerId(),
                    String.valueOf((-bagInfo.get(0).getCount()) * 50 + num1));

                OceanPromotionRewordEntity oceanPromotionRewordEntity = new OceanPromotionRewordEntity();

                oceanPromotionRewordEntity.setDiamond((-bagInfo.get(0).getCount()));
                oceanPromotionRewordEntity
                    .setNickName(UserContainer.getUserById(agentEntity1.getPlayerId()).getNickname());
                oceanPromotionRewordEntity.setReword("金币*" + (-bagInfo.get(0).getCount()) * 500);
                oceanPromotionRewordEntity.setUserId(agentEntity1.getPlayerId());
                oceanPromotionRewordEntity.setOceanUserId(agentEntity1.getAgentPlayerId());

                agentCutLogMapper.savePromotionReword(oceanPromotionRewordEntity);

            }

        }

        // if(RedisUtil.val("USER_T_STATUS"+user.getId(),0L)!=0){
        // long a = RedisUtil.val("USER_T_BANKRUPTCY_NUMBER"+user.getId(),0L);
        // RedisHelper.set("USER_T_BANKRUPTCY_NUMBER"+user.getId(),String.valueOf(a+bagInfo.get(bagInfo.size()-1).getCount()));
        // }else {
        // int x = CommonLobbyManager.getUserT(user,1);
        // RedisHelper.set("USER_T_PEAK_VALUE"+user.getId(),String.valueOf(x));
        // RedisHelper.set("USER_T_BANKRUPTCY_NUMBER"+user.getId(),String.valueOf(bagInfo.get(bagInfo.size()-1).getCount()));
        // }

        OseeRechargeLogEntity oseeRechargeLogEntity = new OseeRechargeLogEntity();

        oseeRechargeLogEntity.setShopName("购买特惠礼包");
        oseeRechargeLogEntity.setShopType(bagId);
        oseeRechargeLogEntity.setOrderNum(String.valueOf(System.currentTimeMillis() + user.getId()));
        oseeRechargeLogEntity.setCount(1);
        oseeRechargeLogEntity.setCreator(user.getNickname());
        oseeRechargeLogEntity.setNickname(user.getNickname());
        oseeRechargeLogEntity.setOrderState(1);
        oseeRechargeLogEntity.setPayMoney(-bagInfo.get(0).getCount() * 100);
        oseeRechargeLogEntity.setRechargeType(4);// 钻石购买
        oseeRechargeLogEntity.setUserId(user.getId());

        oseeRechargeLogMapper.save(oseeRechargeLogEntity);

        if (bagInfo.get(0).getCount() >= 80) {

            String key = String.format(CommonLobbyManager.FIRST_CHARGE_KEY_NAMESPACE, user.getId());
            String value = RedisHelper.get(key);

            if (StringUtils.isEmpty(value)) { // 还没有首充

                // 首充赠送的大礼包 12颗钻石、2万金币、30张锁定卡、20张冰冻卡、20张急速卡、10张暴击卡、3天月卡体验
                List<ItemData> itemDataList = Arrays.asList(new ItemData(ItemId.FEN_SHEN.getId(), 10),
                    new ItemData(ItemId.SKILL_ELETIC.getId(), 2), new ItemData(ItemId.SKILL_LOCK.getId(), 10),
                    new ItemData(ItemId.MONEY.getId(), 60000), new ItemData(ItemId.SKILL_FAST.getId(), 18),
                    // new ItemData(ItemId.DRAGON_CRYSTAL.getId(), 10000),
                    new ItemData(ItemId.YU_GU.getId(), 8), new ItemData(ItemId.MONTH_CARD.getId(), 3));

                PlayerManager.addItems(user, itemDataList, ItemChangeReason.FIRST_ADDMONEY, true);

                // 保存首充记录
                RedisHelper.set(key, "￥" + bagInfo.get(0).getCount());

                if (user.isOnline()) { // 通知给用户

                    // 发送礼包赠送响应
                    OseeLobbyMessage.FirstChargeRewardsResponse.Builder builder1 =
                        OseeLobbyMessage.FirstChargeRewardsResponse.newBuilder();

                    for (ItemData itemData : itemDataList) {

                        builder1.addRewards(OseePublicData.ItemDataProto.newBuilder().setItemId(itemData.getItemId())
                            .setItemNum(itemData.getCount()).build());

                    }

                    NetManager.sendMessage(OseeMsgCode.S_C_TTMY_FIRST_CHARGE_REWARDS_RESPONSE_VALUE,
                        builder1, user);

                }

            }

        }

        OseeLobbyMessage.OnceBagBuySuccessResponse.Builder builder =
            OseeLobbyMessage.OnceBagBuySuccessResponse.newBuilder();

        for (int i = 1; i < bagInfo.size(); i++) {

            builder.addItemData(OseePublicData.ItemDataProto.newBuilder().setItemId(bagInfo.get(i).getItemId())
                .setItemNum(bagInfo.get(i).getCount()));

        }

        NetManager.sendMessage(OseeMsgCode.S_C_TTMY_ONCE_BAG_BUY_SUCCESS_RESPONSE_VALUE, builder, user);

    }

    /**
     * 领取金币卡
     */
    public void receiveMoneyCard(ServerUser user) {
        ShoppingEntity shoppingEntity = PlayerManager.getPlayerShopping(user.getId());
        if (!shoppingEntity.isMoneyCard()) {
            return;
        }
        if (DateUtils.isSameDay(shoppingEntity.getLastReceive(), new Date())) {
            NetManager.sendHintMessageToClient("每日仅能领取一次金币卡奖励", user);
            return;
        }

        shoppingEntity.setLastReceive(new Date());
        PlayerManager.updatePlayerShopping(shoppingEntity);

        PlayerManager.addItem(user, ItemId.MONEY.getId(), 10000000, ItemChangeReason.MONEY_CARD, true);
        OseeLobbyMessage.ReceiveMoneyResponse.Builder builder = OseeLobbyMessage.ReceiveMoneyResponse.newBuilder();
        builder.addItemData(
            OseePublicData.ItemDataProto.newBuilder().setItemId(ItemId.MONEY.getId()).setItemNum(10000000));
        NetManager.sendMessage(OseeMsgCode.S_C_TTMY_RECEIVE_MONEY_RESPONSE_VALUE, builder, user);
    }

    /**
     * 购买炮台等级
     */
    public void buyBatteryLevel(ServerUser user, int type) {
        OseePlayerEntity playerEntity = getPlayerEntity(user);
        if (playerEntity == null) {
            return;
        }

        long batteryLevel = playerEntity.getBatteryLevel();
        if (batteryLevel >= 500000) {
            NetManager.sendHintMessageToClient(String.format("您当前最大炮台等级为%d，无需购买炮台直升", batteryLevel), user);
            return;
        }

        switch (type) {
            case 0: {// 直升10000
                if (!PlayerManager.checkItem(user, ItemId.DIAMOND, 36)) {
                    NetManager.sendHintMessageToClient("您的钻石不足，无法购买", user);
                    return;
                }

                playerEntity.setBatteryLevel(10000); // 炮台倍率更新为2000倍
                playerMapper.update(playerEntity);
                List<ItemData> itemDataList = new ArrayList<>();
                itemDataList.add(new ItemData(ItemId.DIAMOND.getId(), -36));
                itemDataList.add(new ItemData(ItemId.MONEY.getId(), 680000));
                // itemDataList.add(new ItemData(ItemId.DRAGON_CRYSTAL.getId(), 50000));
                itemDataList.add(new ItemData(ItemId.SKILL_LOCK.getId(), 10));
                itemDataList.add(new ItemData(ItemId.SKILL_FAST.getId(), 10));
                itemDataList.add(new ItemData(ItemId.FEN_SHEN.getId(), 10));
                OseePlayerEntity entity = getPlayerEntity(user);
                entity.setRechargeMoney(entity.getRechargeMoney() + 3);
                entity.setVipLevel(PlayerManager.getPlayerVipLevel(entity));
                PlayerManager.addItems(user, itemDataList, ItemChangeReason.BUY_BATTERY_LEVEL, true);
                long reChargeNum = RedisUtil.val("USER_TODAY_RRECHARGE_MONEY" + user.getId(), 0L);
                RedisHelper.set("USER_TODAY_RRECHARGE_MONEY" + user.getId(), String.valueOf(reChargeNum + 36));
                long all = RedisUtil.val("USER_T_BUY_NUMBER_ALL", 0L);
                long money1 = new Double(3.6 * diaPex).longValue();
                long moneya = 0;
                if (all < 0) {
                    int csc = ThreadLocalRandom.current().nextInt(new Double(RedisUtil.val("csc", 0D)).intValue(),
                        new Double(RedisUtil.val("csc1", 1d)).intValue());
                    moneya = new Double((1 + csc * 0.01) * money1).longValue();
                    RedisHelper.set("USER_T_BUY_NUMBER_ALL", String.valueOf(all + money1 - moneya));
                } else {
                    int x = ThreadLocalRandom.current().nextInt(1, 4);
                    if (x == 1) {// 盈利状态
                        int ckc = ThreadLocalRandom.current().nextInt(new Double(RedisUtil.val("ckc", 0D)).intValue(),
                            new Double(RedisUtil.val("ckc1", 1d)).intValue());
                        moneya = new Double((1 + ckc * 0.01) * money1).longValue();
                        RedisHelper.set("USER_T_BUY_NUMBER_ALL", String.valueOf(all + money1 - moneya));
                    } else if (x == 2) {
                        moneya = money1;
                        // RedisHelper.set("USER_T_BUY_NUMBER_ALL",String.valueOf(all+money1));
                    } else {
                        int csc = ThreadLocalRandom.current().nextInt(new Double(RedisUtil.val("csc", 0D)).intValue(),
                            new Double(RedisUtil.val("csc1", 1d)).intValue());
                        moneya = new Double((1 + csc * 0.01) * money1).longValue();
                        RedisHelper.set("USER_T_BUY_NUMBER_ALL", String.valueOf(all + money1 - moneya));
                    }
                }
                // if(all<0){
                // int ckc = ThreadLocalRandom.current().nextInt(-30, -10);
                // moneya = new Double((1+ckc*0.01)*money1).longValue();
                // RedisHelper.set("USER_T_BUY_NUMBER_ALL",String.valueOf(all+money1-moneya));
                // }else{
                // int x = ThreadLocalRandom.current().nextInt(1,4);
                // if(x==1){//盈利状态
                // int zsc = ThreadLocalRandom.current().nextInt(10, 30);
                // moneya = new Double((1+zsc*0.01)*money1).longValue();
                // RedisHelper.set("USER_T_BUY_NUMBER_ALL",String.valueOf(all+moneya-money1));
                // }else if(x==2){
                // moneya = money1;
                // RedisHelper.set("USER_T_BUY_NUMBER_ALL",String.valueOf(all+money1));
                // }else{
                // int zkc = ThreadLocalRandom.current().nextInt(-30, -10);
                // moneya = new Double((1+zkc*0.01)*money1).longValue();
                // RedisHelper.set("USER_T_BUY_NUMBER_ALL",String.valueOf(all+money1-moneya));
                // }
                // }
                long a1 = RedisUtil.val("USER_T_BANKRUPTCY_NUMBER_YK" + user.getId(), 0L);
                RedisHelper.set("USER_T_BANKRUPTCY_NUMBER_YK" + user.getId(), String.valueOf(a1 + (money1 - moneya)));
                money1 = moneya;
                long a = RedisUtil.val("USER_T_BANKRUPTCY_NUMBER" + user.getId(), 0L);
                RedisHelper.set("USER_T_BANKRUPTCY_NUMBER" + user.getId(), String.valueOf(a + money1));
                // FishingManager.joinchangePeak(user, 1);
                RedisHelper.set("USER_T_STATUS" + user.getId(), "1");
                AgentEntity agentEntity = agentMapper.getByPlayerId(user.getId());
                if (agentEntity != null && agentEntity.getAgentPlayerId() != null) {
                    int num = RedisUtil.val("USER_OCEAN_LJ_REWORD" + agentEntity.getAgentPlayerId(), 0);
                    RedisHelper.set("USER_OCEAN_LJ_REWORD" + agentEntity.getAgentPlayerId(),
                        String.valueOf(36 * 150 + num));
                    AgentEntity agentEntity1 = agentMapper.getByPlayerId(agentEntity.getAgentPlayerId());
                    OceanRewordEntity oceanRewordEntity = new OceanRewordEntity();
                    oceanRewordEntity.setDiamond(36L);
                    oceanRewordEntity.setNickName(user.getNickname());
                    oceanRewordEntity.setReword("金币*" + 36 * 1500);
                    oceanRewordEntity.setShopName("礼包");
                    oceanRewordEntity.setUserId(user.getId());
                    oceanRewordEntity.setOceanUserId(agentEntity.getAgentPlayerId());
                    agentCutLogMapper.saveReword(oceanRewordEntity);
                    if (agentEntity1 != null && agentEntity1.getAgentPlayerId() != null) {
                        int num1 = RedisUtil.val("USER_OCEAN_LJ_REWORD" + agentEntity1.getAgentPlayerId(), 0);
                        RedisHelper.set("USER_OCEAN_LJ_REWORD" + agentEntity1.getAgentPlayerId(),
                            String.valueOf(36 * 50 + num1));
                        OceanPromotionRewordEntity oceanPromotionRewordEntity = new OceanPromotionRewordEntity();
                        oceanPromotionRewordEntity.setDiamond(36L);
                        oceanPromotionRewordEntity
                            .setNickName(UserContainer.getUserById(agentEntity1.getPlayerId()).getNickname());
                        oceanPromotionRewordEntity.setReword("金币*" + 36 * 500);
                        oceanPromotionRewordEntity.setUserId(agentEntity1.getPlayerId());
                        oceanPromotionRewordEntity.setOceanUserId(agentEntity1.getAgentPlayerId());
                        agentCutLogMapper.savePromotionReword(oceanPromotionRewordEntity);
                    }
                }
                String key = String.format(CommonLobbyManager.FIRST_CHARGE_KEY_NAMESPACE, user.getId());
                String value = RedisHelper.get(key);
                if (StringUtils.isEmpty(value)) { // 还没有首充
                    // 首充赠送的大礼包 12颗钻石、2万金币、30张锁定卡、20张冰冻卡、20张急速卡、10张暴击卡、3天月卡体验
                    List<ItemData> itemDataList1 = Arrays.asList(new ItemData(ItemId.FEN_SHEN.getId(), 10),
                        new ItemData(ItemId.SKILL_ELETIC.getId(), 2), new ItemData(ItemId.SKILL_LOCK.getId(), 10),
                        new ItemData(ItemId.MONEY.getId(), 60000), new ItemData(ItemId.SKILL_FAST.getId(), 18),
                        // new ItemData(ItemId.DRAGON_CRYSTAL.getId(), 10000),
                        new ItemData(ItemId.YU_GU.getId(), 8), new ItemData(ItemId.MONTH_CARD.getId(), 3));
                    PlayerManager.addItems(user, itemDataList1, ItemChangeReason.FIRST_ADDMONEY, true);
                    // 保存首充记录
                    RedisHelper.set(key, "￥" + 36);

                    if (user.isOnline()) { // 通知给用户
                        // 发送礼包赠送响应
                        OseeLobbyMessage.FirstChargeRewardsResponse.Builder builder1 =
                            OseeLobbyMessage.FirstChargeRewardsResponse.newBuilder();
                        for (ItemData itemData : itemDataList) {
                            builder1.addRewards(OseePublicData.ItemDataProto.newBuilder()
                                .setItemId(itemData.getItemId()).setItemNum(itemData.getCount()).build());
                        }
                        NetManager.sendMessage(OseeMsgCode.S_C_TTMY_FIRST_CHARGE_REWARDS_RESPONSE_VALUE,
                            builder1, user);
                    }
                }
                OseeRechargeLogEntity oseeRechargeLogEntity = new OseeRechargeLogEntity();
                oseeRechargeLogEntity.setShopName("购买直升10000倍礼包");
                oseeRechargeLogEntity.setShopType(ItemId.BUY_BATTERY_LEVEL.getId());
                oseeRechargeLogEntity.setOrderNum(String.valueOf(System.currentTimeMillis() + user.getId()));
                oseeRechargeLogEntity.setCount(1);
                oseeRechargeLogEntity.setCreator(user.getNickname());
                oseeRechargeLogEntity.setNickname(user.getNickname());
                oseeRechargeLogEntity.setOrderState(1);
                oseeRechargeLogEntity.setPayMoney(36 * 100);
                oseeRechargeLogEntity.setRechargeType(4);// 钻石购买
                oseeRechargeLogEntity.setUserId(user.getId());
                oseeRechargeLogMapper.save(oseeRechargeLogEntity);
                OseeLobbyMessage.BuyBatteryLevelResponse.Builder builder =
                    OseeLobbyMessage.BuyBatteryLevelResponse.newBuilder();
                for (int i = 1; i < itemDataList.size(); i++) {
                    builder.addItemData(OseePublicData.ItemDataProto.newBuilder()
                        .setItemId(itemDataList.get(i).getItemId()).setItemNum(itemDataList.get(i).getCount()));
                }
                // if(RedisUtil.val("USER_T_STATUS"+user.getId(),0L)!=0){
                // long a = RedisUtil.val("USER_T_BANKRUPTCY_NUMBER"+user.getId(),0L);
                // RedisHelper.set("USER_T_BANKRUPTCY_NUMBER"+user.getId(),String.valueOf(a+1280000));
                // }else{
                // int x = CommonLobbyManager.getUserT(user,1);
                // RedisHelper.set("USER_T_PEAK_VALUE"+user.getId(),String.valueOf(x));
                // RedisHelper.set("USER_T_BANKRUPTCY_NUMBER"+user.getId(),String.valueOf(1280000));
                // }
                // 返回响应
                OseeFishingMessage.UnlockBatteryLevelResponse.Builder builder1 =
                    OseeFishingMessage.UnlockBatteryLevelResponse.newBuilder();
                builder1.setLevel(10000);
                builder1.setRewardGold(
                    OseePublicData.ItemDataProto.newBuilder().setItemId(ItemId.MONEY.getId()).setItemNum(0).build());
                NetManager.sendMessage(OseeMsgCode.S_C_TTMY_UNLOCK_BATTERY_LEVEL_RESPONSE_VALUE, builder1, user);
                NetManager.sendMessage(OseeMsgCode.S_C_TTMY_BUY_BATTERY_LEVEL_RESPONSE_VALUE, builder, user);
                break;
            }
            case 1: {// 直升100000
                if (!PlayerManager.checkItem(user, ItemId.DIAMOND, 98)) {
                    NetManager.sendHintMessageToClient("您的钻石不足，无法购买", user);
                    return;
                }

                playerEntity.setBatteryLevel(100000); // 炮台倍率更新为10000倍
                playerMapper.update(playerEntity);
                List<ItemData> itemDataList = new ArrayList<>();
                itemDataList.add(new ItemData(ItemId.DIAMOND.getId(), -98));
                itemDataList.add(new ItemData(ItemId.MONEY.getId(), 1280000));
                // itemDataList.add(new ItemData(ItemId.DRAGON_CRYSTAL.getId(), 100000));
                itemDataList.add(new ItemData(ItemId.SKILL_FAST.getId(), 12));
                itemDataList.add(new ItemData(ItemId.FEN_SHEN.getId(), 12));
                itemDataList.add(new ItemData(ItemId.SKILL_ELETIC.getId(), 3));
                // itemDataList.add(new ItemData(ItemId.SKILL_TORPEDO.getId(), 1));
                OseePlayerEntity entity = getPlayerEntity(user);
                entity.setRechargeMoney(entity.getRechargeMoney() + 9);
                entity.setVipLevel(PlayerManager.getPlayerVipLevel(entity));
                PlayerManager.addItems(user, itemDataList, ItemChangeReason.BUY_BATTERY_LEVEL, true);
                long reChargeNum = RedisUtil.val("USER_TODAY_RRECHARGE_MONEY" + user.getId(), 0L);
                RedisHelper.set("USER_TODAY_RRECHARGE_MONEY" + user.getId(), String.valueOf(reChargeNum + 98));
                long all = RedisUtil.val("USER_T_BUY_NUMBER_ALL", 0L);
                long money1 = new Double(9.8 * diaPex).longValue();
                long moneya = 0;
                if (all < 0) {
                    int csc = ThreadLocalRandom.current().nextInt(new Double(RedisUtil.val("csc", 0D)).intValue(),
                        new Double(RedisUtil.val("csc1", 1d)).intValue());
                    moneya = new Double((1 + csc * 0.01) * money1).longValue();
                    RedisHelper.set("USER_T_BUY_NUMBER_ALL", String.valueOf(all + money1 - moneya));
                } else {
                    int x = ThreadLocalRandom.current().nextInt(1, 4);
                    if (x == 1) {// 盈利状态
                        int ckc = ThreadLocalRandom.current().nextInt(new Double(RedisUtil.val("ckc", 0D)).intValue(),
                            new Double(RedisUtil.val("ckc1", 1d)).intValue());
                        moneya = new Double((1 + ckc * 0.01) * money1).longValue();
                        RedisHelper.set("USER_T_BUY_NUMBER_ALL", String.valueOf(all + money1 - moneya));
                    } else if (x == 2) {
                        moneya = money1;
                        // RedisHelper.set("USER_T_BUY_NUMBER_ALL",String.valueOf(all+money1));
                    } else {
                        int csc = ThreadLocalRandom.current().nextInt(new Double(RedisUtil.val("csc", 0D)).intValue(),
                            new Double(RedisUtil.val("csc1", 1d)).intValue());
                        moneya = new Double((1 + csc * 0.01) * money1).longValue();
                        RedisHelper.set("USER_T_BUY_NUMBER_ALL", String.valueOf(all + money1 - moneya));
                    }
                }
                // if(all<0){
                // int ckc = ThreadLocalRandom.current().nextInt(-30, -10);
                // moneya = new Double((1+ckc*0.01)*money1).longValue();
                // RedisHelper.set("USER_T_BUY_NUMBER_ALL",String.valueOf(all+money1-moneya));
                // }else{
                // int x = ThreadLocalRandom.current().nextInt(1,4);
                // if(x==1){//盈利状态
                // int zsc = ThreadLocalRandom.current().nextInt(10, 30);
                // moneya = new Double((1+zsc*0.01)*money1).longValue();
                // RedisHelper.set("USER_T_BUY_NUMBER_ALL",String.valueOf(all+moneya-money1));
                // }else if(x==2){
                // moneya = money1;
                // RedisHelper.set("USER_T_BUY_NUMBER_ALL",String.valueOf(all+money1));
                // }else{
                // int zkc = ThreadLocalRandom.current().nextInt(-30, -10);
                // moneya = new Double((1+zkc*0.01)*money1).longValue();
                // RedisHelper.set("USER_T_BUY_NUMBER_ALL",String.valueOf(all+money1-moneya));
                // }
                // }
                long a1 = RedisUtil.val("USER_T_BANKRUPTCY_NUMBER_YK" + user.getId(), 0L);
                RedisHelper.set("USER_T_BANKRUPTCY_NUMBER_YK" + user.getId(), String.valueOf(a1 + (money1 - moneya)));
                money1 = moneya;
                long a = RedisUtil.val("USER_T_BANKRUPTCY_NUMBER" + user.getId(), 0L);
                RedisHelper.set("USER_T_BANKRUPTCY_NUMBER" + user.getId(), String.valueOf(a + money1));
                // FishingManager.joinchangePeak(user, 1);
                RedisHelper.set("USER_T_STATUS" + user.getId(), "1");
                AgentEntity agentEntity = agentMapper.getByPlayerId(user.getId());
                if (agentEntity != null && agentEntity.getAgentPlayerId() != null) {
                    int num = RedisUtil.val("USER_OCEAN_LJ_REWORD" + agentEntity.getAgentPlayerId(), 0);
                    RedisHelper.set("USER_OCEAN_LJ_REWORD" + agentEntity.getAgentPlayerId(),
                        String.valueOf(98 * 150 + num));
                    AgentEntity agentEntity1 = agentMapper.getByPlayerId(agentEntity.getAgentPlayerId());
                    OceanRewordEntity oceanRewordEntity = new OceanRewordEntity();
                    oceanRewordEntity.setDiamond(98L);
                    oceanRewordEntity.setNickName(user.getNickname());
                    oceanRewordEntity.setReword("金币*" + 98 * 1500);
                    oceanRewordEntity.setShopName("礼包");
                    oceanRewordEntity.setUserId(user.getId());
                    oceanRewordEntity.setOceanUserId(agentEntity.getAgentPlayerId());
                    agentCutLogMapper.saveReword(oceanRewordEntity);
                    if (agentEntity1 != null && agentEntity1.getAgentPlayerId() != null) {
                        int num1 = RedisUtil.val("USER_OCEAN_LJ_REWORD" + agentEntity1.getAgentPlayerId(), 0);
                        RedisHelper.set("USER_OCEAN_LJ_REWORD" + agentEntity1.getAgentPlayerId(),
                            String.valueOf(98 * 50 + num1));
                        OceanPromotionRewordEntity oceanPromotionRewordEntity = new OceanPromotionRewordEntity();
                        oceanPromotionRewordEntity.setDiamond(98L);
                        oceanPromotionRewordEntity
                            .setNickName(UserContainer.getUserById(agentEntity1.getPlayerId()).getNickname());
                        oceanPromotionRewordEntity.setReword("金币*" + 98 * 500);
                        oceanPromotionRewordEntity.setUserId(agentEntity1.getPlayerId());
                        oceanPromotionRewordEntity.setOceanUserId(agentEntity1.getAgentPlayerId());
                        agentCutLogMapper.savePromotionReword(oceanPromotionRewordEntity);
                    }
                }
                String key = String.format(CommonLobbyManager.FIRST_CHARGE_KEY_NAMESPACE, user.getId());
                String value = RedisHelper.get(key);
                if (StringUtils.isEmpty(value)) { // 还没有首充
                    // 首充赠送的大礼包 12颗钻石、2万金币、30张锁定卡、20张冰冻卡、20张急速卡、10张暴击卡、3天月卡体验
                    List<ItemData> itemDataList1 = Arrays.asList(new ItemData(ItemId.FEN_SHEN.getId(), 10),
                        new ItemData(ItemId.SKILL_ELETIC.getId(), 2), new ItemData(ItemId.SKILL_LOCK.getId(), 10),
                        new ItemData(ItemId.MONEY.getId(), 60000), new ItemData(ItemId.SKILL_FAST.getId(), 18),
                        // new ItemData(ItemId.DRAGON_CRYSTAL.getId(), 10000),
                        new ItemData(ItemId.YU_GU.getId(), 8), new ItemData(ItemId.MONTH_CARD.getId(), 3));
                    PlayerManager.addItems(user, itemDataList1, ItemChangeReason.FIRST_ADDMONEY, true);
                    // 保存首充记录
                    RedisHelper.set(key, "￥" + 98);

                    if (user.isOnline()) { // 通知给用户
                        // 发送礼包赠送响应
                        OseeLobbyMessage.FirstChargeRewardsResponse.Builder builder1 =
                            OseeLobbyMessage.FirstChargeRewardsResponse.newBuilder();
                        for (ItemData itemData : itemDataList) {
                            builder1.addRewards(OseePublicData.ItemDataProto.newBuilder()
                                .setItemId(itemData.getItemId()).setItemNum(itemData.getCount()).build());
                        }
                        NetManager.sendMessage(OseeMsgCode.S_C_TTMY_FIRST_CHARGE_REWARDS_RESPONSE_VALUE,
                            builder1, user);
                    }
                }
                OseeRechargeLogEntity oseeRechargeLogEntity = new OseeRechargeLogEntity();
                oseeRechargeLogEntity.setShopName("购买直升100000倍礼包");
                oseeRechargeLogEntity.setShopType(ItemId.BUY_BATTERY_LEVEL.getId());
                oseeRechargeLogEntity.setOrderNum(String.valueOf(System.currentTimeMillis() + user.getId()));
                oseeRechargeLogEntity.setCount(1);
                oseeRechargeLogEntity.setCreator(user.getNickname());
                oseeRechargeLogEntity.setNickname(user.getNickname());
                oseeRechargeLogEntity.setOrderState(1);
                oseeRechargeLogEntity.setPayMoney(98 * 100);
                oseeRechargeLogEntity.setRechargeType(4);// 钻石购买
                oseeRechargeLogEntity.setUserId(user.getId());
                oseeRechargeLogMapper.save(oseeRechargeLogEntity);
                OseeLobbyMessage.BuyBatteryLevelResponse.Builder builder =
                    OseeLobbyMessage.BuyBatteryLevelResponse.newBuilder();
                for (int i = 1; i < itemDataList.size(); i++) {
                    builder.addItemData(OseePublicData.ItemDataProto.newBuilder()
                        .setItemId(itemDataList.get(i).getItemId()).setItemNum(itemDataList.get(i).getCount()));
                }
                // if(RedisUtil.val("USER_T_STATUS"+user.getId(),0L)!=0){
                // long a = RedisUtil.val("USER_T_BANKRUPTCY_NUMBER"+user.getId(),0L);
                // RedisHelper.set("USER_T_BANKRUPTCY_NUMBER"+user.getId(),String.valueOf(a+2980000));
                // }else{
                // int x = CommonLobbyManager.getUserT(user,1);
                // RedisHelper.set("USER_T_PEAK_VALUE"+user.getId(),String.valueOf(x));
                // RedisHelper.set("USER_T_BANKRUPTCY_NUMBER"+user.getId(),String.valueOf(2980000));
                // }
                // 返回响应
                OseeFishingMessage.UnlockBatteryLevelResponse.Builder builder1 =
                    OseeFishingMessage.UnlockBatteryLevelResponse.newBuilder();
                builder1.setLevel(100000);
                builder1.setRewardGold(
                    OseePublicData.ItemDataProto.newBuilder().setItemId(ItemId.MONEY.getId()).setItemNum(0).build());
                NetManager.sendMessage(OseeMsgCode.S_C_TTMY_UNLOCK_BATTERY_LEVEL_RESPONSE_VALUE, builder1, user);
                NetManager.sendMessage(OseeMsgCode.S_C_TTMY_BUY_BATTERY_LEVEL_RESPONSE_VALUE, builder, user);
                break;
            }
            case 2: {
                if (!PlayerManager.checkItem(user, ItemId.DIAMOND, 168)) {
                    NetManager.sendHintMessageToClient("您的钻石不足，无法购买", user);
                    return;
                }

                playerEntity.setBatteryLevel(200000); // 炮台倍率更新为10000倍
                playerMapper.update(playerEntity);
                List<ItemData> itemDataList = new ArrayList<>();
                itemDataList.add(new ItemData(ItemId.DIAMOND.getId(), -168));
                itemDataList.add(new ItemData(ItemId.MONEY.getId(), 3600000));
                // itemDataList.add(new ItemData(ItemId.DRAGON_CRYSTAL.getId(), 180000));
                itemDataList.add(new ItemData(ItemId.SKILL_FAST.getId(), 15));
                itemDataList.add(new ItemData(ItemId.FEN_SHEN.getId(), 15));
                itemDataList.add(new ItemData(ItemId.SKILL_ELETIC.getId(), 5));
                // itemDataList.add(new ItemData(ItemId.SKILL_TORPEDO.getId(), 1));
                OseePlayerEntity entity = getPlayerEntity(user);
                entity.setRechargeMoney(entity.getRechargeMoney() + 16);
                entity.setVipLevel(PlayerManager.getPlayerVipLevel(entity));
                PlayerManager.addItems(user, itemDataList, ItemChangeReason.BUY_BATTERY_LEVEL, true);
                long reChargeNum = RedisUtil.val("USER_TODAY_RRECHARGE_MONEY" + user.getId(), 0L);
                RedisHelper.set("USER_TODAY_RRECHARGE_MONEY" + user.getId(), String.valueOf(reChargeNum + 168));
                long all = RedisUtil.val("USER_T_BUY_NUMBER_ALL", 0L);
                long money1 = 168 * diaPex;
                long moneya = 0;
                if (all < 0) {
                    int csc = ThreadLocalRandom.current().nextInt(new Double(RedisUtil.val("csc", 0D)).intValue(),
                        new Double(RedisUtil.val("csc1", 1d)).intValue());
                    moneya = new Double((1 + csc * 0.01) * money1).longValue();
                    RedisHelper.set("USER_T_BUY_NUMBER_ALL", String.valueOf(all + money1 - moneya));
                } else {
                    int x = ThreadLocalRandom.current().nextInt(1, 4);
                    if (x == 1) {// 盈利状态
                        int ckc = ThreadLocalRandom.current().nextInt(new Double(RedisUtil.val("ckc", 0D)).intValue(),
                            new Double(RedisUtil.val("ckc1", 1d)).intValue());
                        moneya = new Double((1 + ckc * 0.01) * money1).longValue();
                        RedisHelper.set("USER_T_BUY_NUMBER_ALL", String.valueOf(all + money1 - moneya));
                    } else if (x == 2) {
                        moneya = money1;
                        // RedisHelper.set("USER_T_BUY_NUMBER_ALL",String.valueOf(all));
                    } else {
                        int csc = ThreadLocalRandom.current().nextInt(new Double(RedisUtil.val("csc", 0D)).intValue(),
                            new Double(RedisUtil.val("csc1", 1d)).intValue());
                        moneya = new Double((1 + csc * 0.01) * money1).longValue();
                        RedisHelper.set("USER_T_BUY_NUMBER_ALL", String.valueOf(all + money1 - moneya));
                    }
                }
                // if(all<0){
                // int ckc = ThreadLocalRandom.current().nextInt(-30, -10);
                // moneya = new Double((1+ckc*0.01)*money1).longValue();
                // RedisHelper.set("USER_T_BUY_NUMBER_ALL",String.valueOf(all+money1-moneya));
                // }else{
                // int x = ThreadLocalRandom.current().nextInt(1,4);
                // if(x==1){//盈利状态
                // int zsc = ThreadLocalRandom.current().nextInt(10, 30);
                // moneya = new Double((1+zsc*0.01)*money1).longValue();
                // RedisHelper.set("USER_T_BUY_NUMBER_ALL",String.valueOf(all+moneya-money1));
                // }else if(x==2){
                // moneya = money1;
                // RedisHelper.set("USER_T_BUY_NUMBER_ALL",String.valueOf(all+money1));
                // }else{
                // int zkc = ThreadLocalRandom.current().nextInt(-30, -10);
                // moneya = new Double((1+zkc*0.01)*money1).longValue();
                // RedisHelper.set("USER_T_BUY_NUMBER_ALL",String.valueOf(all+money1-moneya));
                // }
                // }
                long a1 = RedisUtil.val("USER_T_BANKRUPTCY_NUMBER_YK" + user.getId(), 0L);
                RedisHelper.set("USER_T_BANKRUPTCY_NUMBER_YK" + user.getId(), String.valueOf(a1 + (money1 - moneya)));
                money1 = moneya;
                long a = RedisUtil.val("USER_T_BANKRUPTCY_NUMBER" + user.getId(), 0L);
                RedisHelper.set("USER_T_BANKRUPTCY_NUMBER" + user.getId(), String.valueOf(a + money1));
                // FishingManager.joinchangePeak(user, 1);
                RedisHelper.set("USER_T_STATUS" + user.getId(), "1");
                AgentEntity agentEntity = agentMapper.getByPlayerId(user.getId());
                if (agentEntity != null && agentEntity.getAgentPlayerId() != null) {
                    int num = RedisUtil.val("USER_OCEAN_LJ_REWORD" + agentEntity.getAgentPlayerId(), 0);
                    RedisHelper.set("USER_OCEAN_LJ_REWORD" + agentEntity.getAgentPlayerId(),
                        String.valueOf(168 * 150 + num));
                    AgentEntity agentEntity1 = agentMapper.getByPlayerId(agentEntity.getAgentPlayerId());
                    OceanRewordEntity oceanRewordEntity = new OceanRewordEntity();
                    oceanRewordEntity.setDiamond(168L);
                    oceanRewordEntity.setNickName(user.getNickname());
                    oceanRewordEntity.setReword("金币*" + 168 * 1500);
                    oceanRewordEntity.setShopName("礼包");
                    oceanRewordEntity.setUserId(user.getId());
                    oceanRewordEntity.setOceanUserId(agentEntity.getAgentPlayerId());
                    agentCutLogMapper.saveReword(oceanRewordEntity);
                    if (agentEntity1 != null && agentEntity1.getAgentPlayerId() != null) {
                        int num1 = RedisUtil.val("USER_OCEAN_LJ_REWORD" + agentEntity1.getAgentPlayerId(), 0);
                        RedisHelper.set("USER_OCEAN_LJ_REWORD" + agentEntity1.getAgentPlayerId(),
                            String.valueOf(168 * 50 + num1));
                        OceanPromotionRewordEntity oceanPromotionRewordEntity = new OceanPromotionRewordEntity();
                        oceanPromotionRewordEntity.setDiamond(168L);
                        oceanPromotionRewordEntity
                            .setNickName(UserContainer.getUserById(agentEntity1.getPlayerId()).getNickname());
                        oceanPromotionRewordEntity.setReword("金币*" + 168 * 500);
                        oceanPromotionRewordEntity.setUserId(agentEntity1.getPlayerId());
                        oceanPromotionRewordEntity.setOceanUserId(agentEntity1.getAgentPlayerId());
                        agentCutLogMapper.savePromotionReword(oceanPromotionRewordEntity);
                    }
                }
                String key = String.format(CommonLobbyManager.FIRST_CHARGE_KEY_NAMESPACE, user.getId());
                String value = RedisHelper.get(key);
                if (StringUtils.isEmpty(value)) { // 还没有首充
                    // 首充赠送的大礼包 12颗钻石、2万金币、30张锁定卡、20张冰冻卡、20张急速卡、10张暴击卡、3天月卡体验
                    List<ItemData> itemDataList1 = Arrays.asList(new ItemData(ItemId.FEN_SHEN.getId(), 10),
                        new ItemData(ItemId.SKILL_ELETIC.getId(), 2), new ItemData(ItemId.SKILL_LOCK.getId(), 10),
                        new ItemData(ItemId.MONEY.getId(), 60000), new ItemData(ItemId.SKILL_FAST.getId(), 18),
                        // new ItemData(ItemId.DRAGON_CRYSTAL.getId(), 10000),
                        new ItemData(ItemId.YU_GU.getId(), 8), new ItemData(ItemId.MONTH_CARD.getId(), 3));
                    PlayerManager.addItems(user, itemDataList1, ItemChangeReason.FIRST_ADDMONEY, true);
                    // 保存首充记录
                    RedisHelper.set(key, "￥" + 168);

                    if (user.isOnline()) { // 通知给用户
                        // 发送礼包赠送响应
                        OseeLobbyMessage.FirstChargeRewardsResponse.Builder builder1 =
                            OseeLobbyMessage.FirstChargeRewardsResponse.newBuilder();
                        for (ItemData itemData : itemDataList) {
                            builder1.addRewards(OseePublicData.ItemDataProto.newBuilder()
                                .setItemId(itemData.getItemId()).setItemNum(itemData.getCount()).build());
                        }
                        NetManager.sendMessage(OseeMsgCode.S_C_TTMY_FIRST_CHARGE_REWARDS_RESPONSE_VALUE,
                            builder1, user);
                    }
                }
                OseeRechargeLogEntity oseeRechargeLogEntity = new OseeRechargeLogEntity();
                oseeRechargeLogEntity.setShopName("购买直升200000倍礼包");
                oseeRechargeLogEntity.setShopType(ItemId.BUY_BATTERY_LEVEL.getId());
                oseeRechargeLogEntity.setOrderNum(String.valueOf(System.currentTimeMillis() + user.getId()));
                oseeRechargeLogEntity.setCount(1);
                oseeRechargeLogEntity.setCreator(user.getNickname());
                oseeRechargeLogEntity.setNickname(user.getNickname());
                oseeRechargeLogEntity.setOrderState(1);
                oseeRechargeLogEntity.setPayMoney(168 * 100);
                oseeRechargeLogEntity.setRechargeType(4);// 钻石购买
                oseeRechargeLogEntity.setUserId(user.getId());
                oseeRechargeLogMapper.save(oseeRechargeLogEntity);
                OseeLobbyMessage.BuyBatteryLevelResponse.Builder builder =
                    OseeLobbyMessage.BuyBatteryLevelResponse.newBuilder();
                for (int i = 1; i < itemDataList.size(); i++) {
                    builder.addItemData(OseePublicData.ItemDataProto.newBuilder()
                        .setItemId(itemDataList.get(i).getItemId()).setItemNum(itemDataList.get(i).getCount()));
                }
                // if(RedisUtil.val("USER_T_STATUS"+user.getId(),0L)!=0){
                // long a = RedisUtil.val("USER_T_BANKRUPTCY_NUMBER"+user.getId(),0L);
                // RedisHelper.set("USER_T_BANKRUPTCY_NUMBER"+user.getId(),String.valueOf(a+5180000));
                // }else{
                // int x = CommonLobbyManager.getUserT(user,1);
                // RedisHelper.set("USER_T_PEAK_VALUE"+user.getId(),String.valueOf(x));
                // RedisHelper.set("USER_T_BANKRUPTCY_NUMBER"+user.getId(),String.valueOf(5180000));
                // }
                // 返回响应
                OseeFishingMessage.UnlockBatteryLevelResponse.Builder builder1 =
                    OseeFishingMessage.UnlockBatteryLevelResponse.newBuilder();
                builder1.setLevel(200000);
                builder1.setRewardGold(
                    OseePublicData.ItemDataProto.newBuilder().setItemId(ItemId.MONEY.getId()).setItemNum(0).build());
                NetManager.sendMessage(OseeMsgCode.S_C_TTMY_UNLOCK_BATTERY_LEVEL_RESPONSE_VALUE, builder1, user);
                NetManager.sendMessage(OseeMsgCode.S_C_TTMY_BUY_BATTERY_LEVEL_RESPONSE_VALUE, builder, user);
                break;
            }
            case 3: {
                if (!PlayerManager.checkItem(user, ItemId.DIAMOND, 528)) {
                    NetManager.sendHintMessageToClient("您的钻石不足，无法购买", user);
                    return;
                }

                playerEntity.setBatteryLevel(500000); // 炮台倍率更新为500000倍
                playerMapper.update(playerEntity);
                List<ItemData> itemDataList = new ArrayList<>();
                itemDataList.add(new ItemData(ItemId.DIAMOND.getId(), -528));
                itemDataList.add(new ItemData(ItemId.MONEY.getId(), 9800000));
                // itemDataList.add(new ItemData(ItemId.DRAGON_CRYSTAL.getId(), 280000));
                itemDataList.add(new ItemData(ItemId.SKILL_FAST.getId(), 18));
                itemDataList.add(new ItemData(ItemId.FEN_SHEN.getId(), 18));
                itemDataList.add(new ItemData(ItemId.SKILL_ELETIC.getId(), 10));
                // itemDataList.add(new ItemData(ItemId.SKILL_TORPEDO.getId(), 1));
                // itemDataList.add(new ItemData(ItemId.SKILL_BLACK_HOLE.getId(), 1));
                // itemDataList.add(new ItemData(ItemId.SKILL_BIT.getId(), 1));
                OseePlayerEntity entity = getPlayerEntity(user);
                entity.setRechargeMoney(entity.getRechargeMoney() + 52);
                entity.setVipLevel(PlayerManager.getPlayerVipLevel(entity));
                PlayerManager.addItems(user, itemDataList, ItemChangeReason.BUY_BATTERY_LEVEL, true);
                long reChargeNum = RedisUtil.val("USER_TODAY_RRECHARGE_MONEY" + user.getId(), 0L);
                RedisHelper.set("USER_TODAY_RRECHARGE_MONEY" + user.getId(), String.valueOf(reChargeNum + 528));
                long all = RedisUtil.val("USER_T_BUY_NUMBER_ALL", 0L);
                long money1 = 528 * diaPex;
                long moneya = 0;
                if (all < 0) {
                    int csc = ThreadLocalRandom.current().nextInt(new Double(RedisUtil.val("csc", 0D)).intValue(),
                        new Double(RedisUtil.val("csc1", 1d)).intValue());
                    moneya = new Double((1 + csc * 0.01) * money1).longValue();
                    RedisHelper.set("USER_T_BUY_NUMBER_ALL", String.valueOf(all + money1 - moneya));
                } else {
                    int x = ThreadLocalRandom.current().nextInt(1, 4);
                    if (x == 1) {// 盈利状态
                        int ckc = ThreadLocalRandom.current().nextInt(new Double(RedisUtil.val("ckc", 0D)).intValue(),
                            new Double(RedisUtil.val("ckc1", 1d)).intValue());
                        moneya = new Double((1 + ckc * 0.01) * money1).longValue();
                        RedisHelper.set("USER_T_BUY_NUMBER_ALL", String.valueOf(all + money1 - moneya));
                    } else if (x == 2) {
                        moneya = money1;
                        // RedisHelper.set("USER_T_BUY_NUMBER_ALL",String.valueOf(all+money1));
                    } else {
                        int csc = ThreadLocalRandom.current().nextInt(new Double(RedisUtil.val("csc", 0D)).intValue(),
                            new Double(RedisUtil.val("csc1", 1d)).intValue());
                        moneya = new Double((1 + csc * 0.01) * money1).longValue();
                        RedisHelper.set("USER_T_BUY_NUMBER_ALL", String.valueOf(all + money1 - moneya));
                    }
                }
                // if(all<0){
                // int ckc = ThreadLocalRandom.current().nextInt(-30, -10);
                // moneya = new Double((1+ckc*0.01)*money1).longValue();
                // RedisHelper.set("USER_T_BUY_NUMBER_ALL",String.valueOf(all+money1-moneya));
                // }else{
                // int x = ThreadLocalRandom.current().nextInt(1,4);
                // if(x==1){//盈利状态
                // int zsc = ThreadLocalRandom.current().nextInt(10, 30);
                // moneya = new Double((1+zsc*0.01)*money1).longValue();
                // RedisHelper.set("USER_T_BUY_NUMBER_ALL",String.valueOf(all+moneya-money1));
                // }else if(x==2){
                // moneya = money1;
                // RedisHelper.set("USER_T_BUY_NUMBER_ALL",String.valueOf(all+money1));
                // }else{
                // int zkc = ThreadLocalRandom.current().nextInt(-30, -10);
                // moneya = new Double((1+zkc*0.01)*money1).longValue();
                // RedisHelper.set("USER_T_BUY_NUMBER_ALL",String.valueOf(all+money1-moneya));
                // }
                // }
                long a1 = RedisUtil.val("USER_T_BANKRUPTCY_NUMBER_YK" + user.getId(), 0L);
                RedisHelper.set("USER_T_BANKRUPTCY_NUMBER_YK" + user.getId(), String.valueOf(a1 + (money1 - moneya)));
                money1 = moneya;
                long a = RedisUtil.val("USER_T_BANKRUPTCY_NUMBER" + user.getId(), 0L);
                RedisHelper.set("USER_T_BANKRUPTCY_NUMBER" + user.getId(), String.valueOf(a + money1));
                // FishingManager.joinchangePeak(user, 1);
                RedisHelper.set("USER_T_STATUS" + user.getId(), "1");
                AgentEntity agentEntity = agentMapper.getByPlayerId(user.getId());
                if (agentEntity != null && agentEntity.getAgentPlayerId() != null) {
                    int num = RedisUtil.val("USER_OCEAN_LJ_REWORD" + agentEntity.getAgentPlayerId(), 0);
                    RedisHelper.set("USER_OCEAN_LJ_REWORD" + agentEntity.getAgentPlayerId(),
                        String.valueOf(528 * 150 + num));
                    AgentEntity agentEntity1 = agentMapper.getByPlayerId(agentEntity.getAgentPlayerId());
                    OceanRewordEntity oceanRewordEntity = new OceanRewordEntity();
                    oceanRewordEntity.setDiamond(528L);
                    oceanRewordEntity.setNickName(user.getNickname());
                    oceanRewordEntity.setReword("金币*" + 528 * 1500);
                    oceanRewordEntity.setShopName("礼包");
                    oceanRewordEntity.setUserId(user.getId());
                    oceanRewordEntity.setOceanUserId(agentEntity.getAgentPlayerId());
                    agentCutLogMapper.saveReword(oceanRewordEntity);
                    if (agentEntity1 != null && agentEntity1.getAgentPlayerId() != null) {
                        int num1 = RedisUtil.val("USER_OCEAN_LJ_REWORD" + agentEntity1.getAgentPlayerId(), 0);
                        RedisHelper.set("USER_OCEAN_LJ_REWORD" + agentEntity1.getAgentPlayerId(),
                            String.valueOf(528 * 50 + num1));
                        OceanPromotionRewordEntity oceanPromotionRewordEntity = new OceanPromotionRewordEntity();
                        oceanPromotionRewordEntity.setDiamond(528L);
                        oceanPromotionRewordEntity
                            .setNickName(UserContainer.getUserById(agentEntity1.getPlayerId()).getNickname());
                        oceanPromotionRewordEntity.setReword("金币*" + 528 * 500);
                        oceanPromotionRewordEntity.setUserId(agentEntity1.getPlayerId());
                        oceanPromotionRewordEntity.setOceanUserId(agentEntity1.getAgentPlayerId());
                        agentCutLogMapper.savePromotionReword(oceanPromotionRewordEntity);
                    }
                }
                String key = String.format(CommonLobbyManager.FIRST_CHARGE_KEY_NAMESPACE, user.getId());
                String value = RedisHelper.get(key);
                if (StringUtils.isEmpty(value)) { // 还没有首充
                    // 首充赠送的大礼包 12颗钻石、2万金币、30张锁定卡、20张冰冻卡、20张急速卡、10张暴击卡、3天月卡体验
                    List<ItemData> itemDataList1 = Arrays.asList(new ItemData(ItemId.FEN_SHEN.getId(), 10),
                        new ItemData(ItemId.SKILL_ELETIC.getId(), 2), new ItemData(ItemId.SKILL_LOCK.getId(), 10),
                        new ItemData(ItemId.MONEY.getId(), 60000), new ItemData(ItemId.SKILL_FAST.getId(), 18),
                        // new ItemData(ItemId.DRAGON_CRYSTAL.getId(), 10000),
                        new ItemData(ItemId.YU_GU.getId(), 8), new ItemData(ItemId.MONTH_CARD.getId(), 3));
                    PlayerManager.addItems(user, itemDataList1, ItemChangeReason.FIRST_ADDMONEY, true);
                    // 保存首充记录
                    RedisHelper.set(key, "￥" + 528);

                    if (user.isOnline()) { // 通知给用户
                        // 发送礼包赠送响应
                        OseeLobbyMessage.FirstChargeRewardsResponse.Builder builder1 =
                            OseeLobbyMessage.FirstChargeRewardsResponse.newBuilder();
                        for (ItemData itemData : itemDataList) {
                            builder1.addRewards(OseePublicData.ItemDataProto.newBuilder()
                                .setItemId(itemData.getItemId()).setItemNum(itemData.getCount()).build());
                        }
                        NetManager.sendMessage(OseeMsgCode.S_C_TTMY_FIRST_CHARGE_REWARDS_RESPONSE_VALUE,
                            builder1, user);
                    }
                }
                OseeRechargeLogEntity oseeRechargeLogEntity = new OseeRechargeLogEntity();
                oseeRechargeLogEntity.setShopName("购买直升500000倍礼包");
                oseeRechargeLogEntity.setShopType(ItemId.BUY_BATTERY_LEVEL.getId());
                oseeRechargeLogEntity.setOrderNum(String.valueOf(System.currentTimeMillis() + user.getId()));
                oseeRechargeLogEntity.setCount(1);
                oseeRechargeLogEntity.setCreator(user.getNickname());
                oseeRechargeLogEntity.setNickname(user.getNickname());
                oseeRechargeLogEntity.setOrderState(1);
                oseeRechargeLogEntity.setPayMoney(528 * 100);
                oseeRechargeLogEntity.setRechargeType(4);// 钻石购买
                oseeRechargeLogEntity.setUserId(user.getId());
                oseeRechargeLogMapper.save(oseeRechargeLogEntity);
                OseeLobbyMessage.BuyBatteryLevelResponse.Builder builder =
                    OseeLobbyMessage.BuyBatteryLevelResponse.newBuilder();
                for (int i = 1; i < itemDataList.size(); i++) {
                    builder.addItemData(OseePublicData.ItemDataProto.newBuilder()
                        .setItemId(itemDataList.get(i).getItemId()).setItemNum(itemDataList.get(i).getCount()));
                }
                // if(RedisUtil.val("USER_T_STATUS"+user.getId(),0L)!=0){
                // long a = RedisUtil.val("USER_T_BANKRUPTCY_NUMBER"+user.getId(),0L);
                // RedisHelper.set("USER_T_BANKRUPTCY_NUMBER"+user.getId(),String.valueOf(a+7180000));
                // }else{
                // int x = CommonLobbyManager.getUserT(user,1);
                // RedisHelper.set("USER_T_PEAK_VALUE"+user.getId(),String.valueOf(x));
                // RedisHelper.set("USER_T_BANKRUPTCY_NUMBER"+user.getId(),String.valueOf(7180000));
                // }
                // 返回响应
                OseeFishingMessage.UnlockBatteryLevelResponse.Builder builder1 =
                    OseeFishingMessage.UnlockBatteryLevelResponse.newBuilder();
                builder1.setLevel(500000);
                builder1.setRewardGold(
                    OseePublicData.ItemDataProto.newBuilder().setItemId(ItemId.MONEY.getId()).setItemNum(0).build());
                NetManager.sendMessage(OseeMsgCode.S_C_TTMY_UNLOCK_BATTERY_LEVEL_RESPONSE_VALUE, builder1, user);
                NetManager.sendMessage(OseeMsgCode.S_C_TTMY_BUY_BATTERY_LEVEL_RESPONSE_VALUE, builder, user);
                break;
            }
            default: {
                NetManager.sendHintMessageToClient("购买炮台类型出错，请联系管理员！", user);
                break;
            }

        }
    }

    /**
     * 发送奖券商品列表
     */
    public void sendLotteryShopListResponse(ServerUser user) {
        GetLotteryShopListResponse.Builder builder = GetLotteryShopListResponse.newBuilder();
        for (OseeLotteryShopEntity entity : lotteryShops) {
            LotteryShopItemProto.Builder itemBuilder = LotteryShopItemProto.newBuilder();
            itemBuilder.setId(entity.getId());
            // itemBuilder.setImg(entity.getImg());
            itemBuilder.setLottery(entity.getCost());
            itemBuilder.setName(entity.getName());
            long stock;
            if (entity.getType() == 1 && entity.getSendType() == 3) { // 自动发卡的实物读取库存
                stock = stockMapper.getUnusedCount(entity.getId());
            } else {
                stock = entity.getStock();
            }
            itemBuilder.setRest((int)stock);
            builder.addShopItems(itemBuilder);
        }
        NetManager.sendMessage(OseeMsgCode.S_C_OSEE_GET_LOTTERY_SHOP_LIST_RESPONSE_VALUE, builder, user);
    }

    /**
     * 获取玩家今日Boss号角购买限制
     */
    public void getBossBugleBuyLimit(ServerUser user) {
        int vipLevel = PlayerManager.getPlayerVipLevel(user);
        OseeLobbyMessage.BossBugleBuyLimitResponse.Builder builder =
            OseeLobbyMessage.BossBugleBuyLimitResponse.newBuilder();
        if (vipLevel < 3) {
            builder.setBuyLimit(0);
            builder.setUsedLimit(0);
        } else {
            // 当前VIP等级的每日购买数量上限
            int buyLimit = bossBugleBuyLimit[vipLevel - 3];
            String value = RedisHelper.get(String.format("Server:BossBugleBuyLimit:%d", user.getId()));
            int usedLimit = 0;
            if (!StringUtils.isEmpty(value)) {
                String[] split = value.split(",");
                if (split.length == 2) {
                    usedLimit = Integer.parseInt(split[0]);
                    LocalDate date = LocalDate.parse(split[1]);
                    if (!date.isEqual(LocalDate.now())) { // 非今日限制次数就要重置
                        usedLimit = 0;
                    }
                }
            }
            builder.setBuyLimit(buyLimit);
            builder.setUsedLimit(usedLimit);
        }
        NetManager.sendMessage(OseeMsgCode.S_C_TTMY_BOSS_BUGLE_BUY_LIMIT_RESPONSE_VALUE, builder, user);
    }

    /**
     * 获取玩家的收货地址
     */
    public void getAddress(ServerUser user) {
        AddressEntity addressEntity = addressMapper.getByPlayerId(user.getId());
        OseeLobbyMessage.GetAddressResponse.Builder builder = OseeLobbyMessage.GetAddressResponse.newBuilder();
        if (addressEntity != null) {
            builder.setName(addressEntity.getName());
            builder.setPhone(addressEntity.getPhone());
            builder.setAddress(addressEntity.getAddress());
        }
        NetManager.sendMessage(OseeMsgCode.S_C_TTMY_GET_ADDRESS_RESPONSE_VALUE, builder, user);
    }

    /**
     * 设置玩家的收货地址
     */
    public void setAddress(String name, String phone, String address, ServerUser user) {

        if (StrUtil.isBlank(name) || StrUtil.isBlank(phone) || StrUtil.isBlank(address)) {
            NetManager.sendErrorMessageToClient("输入请不要为空", user);
            return;
        }

        Boolean checkSetAddressFlag = MySettingUtil.SETTING.getBool("checkSetAddressFlag", true);

        if (BooleanUtil.isTrue(checkSetAddressFlag)) {

            UserAuthenticationEntity authenticationEntity = authenticationMapper.getByUserId(user.getId());

            if (authenticationEntity == null) {
                NetManager.sendErrorMessageToClient("请实名认证之后再设置收货地址", user);
                return;
            }

            if (!authenticationEntity.getName().equals(name)) {
                NetManager.sendErrorMessageToClient("收货人姓名与实名认证姓名不一致", user);
                return;
            }

            String phonenum = user.getPhonenum();

            if (StrUtil.isBlank(phonenum)) {
                NetManager.sendErrorMessageToClient("请设置账号后重试", user);
                return;
            }

            if (!phonenum.equals(phone)) {
                NetManager.sendErrorMessageToClient("收货人手机号与绑定手机号不一致", user);
                return;
            }

        }

        boolean exist = true;

        AddressEntity addressEntity = addressMapper.getByPlayerId(user.getId());

        if (addressEntity == null) {

            exist = false;
            addressEntity = new AddressEntity();
            addressEntity.setPlayerId(user.getId());

        }

        addressEntity.setName(name);
        addressEntity.setPhone(phone);
        addressEntity.setAddress(address);

        if (exist) {
            addressMapper.update(addressEntity);
        } else {
            addressMapper.save(addressEntity);
        }

        NetManager.sendHintMessageToClient("收货地址信息设置成功", user);

    }

    /**
     * 获取玩家兑换记录
     */
    public void lotteryExchangeLog(int pageNo, int pageSize, ServerUser user) {
        OseeLobbyMessage.LotteryExchangeLogResponse.Builder builder =
            OseeLobbyMessage.LotteryExchangeLogResponse.newBuilder();
        StringBuilder query = new StringBuilder();
        query.append("where record.user_id = ").append(user.getId());
        int logCount = realLotteryLogMapper.getLogCount(query.toString()); // 数据总条数
        List<OseeRealLotteryLogEntity> logList =
            realLotteryLogMapper.getLogList(query.toString(), " limit " + (pageNo - 1) * pageSize + "," + pageSize // 数据总条数
            );
        builder.setTotalCount(logCount);
        builder.setPageNo(pageNo);
        for (OseeRealLotteryLogEntity logEntity : logList) {
            OseeLobbyMessage.LotteryExchangeLogProto.Builder proto =
                OseeLobbyMessage.LotteryExchangeLogProto.newBuilder();
            proto.setDate(CommonUtil.dateFormat(logEntity.getCreateTime(), "yyyy/MM/dd HH:mm:ss"));
            proto.setShopName(logEntity.getRewardName());
            proto.setState(logEntity.getOrderState());
            if (logEntity.getStockId() > 0) { // 获取商品对应的库存物品信息
                StockEntity stockEntity = stockMapper.getById(logEntity.getStockId());
                if (stockEntity != null) {
                    OseeLobbyMessage.StockInfoProto.Builder info = OseeLobbyMessage.StockInfoProto.newBuilder();
                    info.setNumber(stockEntity.getNumber());
                    info.setPassword(stockEntity.getPassword());
                    proto.setInfo(info);
                }
            }
            builder.addLog(proto);
        }
        NetManager.sendMessage(OseeMsgCode.S_C_TTMY_LOTTERY_EXCHANGE_LOG_RESPONSE_VALUE, builder, user);
    }

    /**
     * 获取礼包内容
     */
    private List<ItemData> getBagInfo(ItemId item, Map<ItemId, List<ItemData>> map) {
        if (item == null) {
            return null;
        }

        List<ItemData> bagInfo = map.get(item);
        if (bagInfo == null || bagInfo.size() == 0) {
            return null;
        }
        return new ArrayList<>(bagInfo);
    }
}
