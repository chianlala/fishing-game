package com.maple.game.osee.manager;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.json.JSONUtil;
import com.google.protobuf.Message;
import com.maple.engine.container.DataContainer;
import com.maple.engine.container.UserContainer;
import com.maple.engine.data.ServerUser;
import com.maple.engine.utils.DateUtils;
import com.maple.game.osee.common.RedisUtil;
import com.maple.game.osee.dao.data.entity.*;
import com.maple.game.osee.dao.data.mapper.*;
import com.maple.game.osee.dao.log.entity.OseePlayerTenureLogEntity;
import com.maple.game.osee.dao.log.mapper.OseePlayerTenureLogMapper;
import com.maple.game.osee.entity.ItemChangeReason;
import com.maple.game.osee.entity.ItemData;
import com.maple.game.osee.entity.ItemId;
import com.maple.game.osee.entity.fishing.csv.file.PlayerLevelConfig;
import com.maple.game.osee.manager.fishing.FishingChallengeManager;
import com.maple.game.osee.manager.lobby.CommonLobbyManager;
import com.maple.game.osee.proto.HwLoginMessage;
import com.maple.game.osee.proto.OseeMessage.OseeMsgCode;
import com.maple.game.osee.proto.OseePublicData;
import com.maple.game.osee.proto.lobby.OseeLobbyMessage;
import com.maple.game.osee.proto.lobby.OseeLobbyMessage.PlayerMoneyResponse;
import com.maple.game.osee.proto.lobby.OseeLobbyMessage.VipLevelResponse;
import com.maple.network.manager.NetManager;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * 1688玩家管理类
 */
@Component
@Slf4j
public class PlayerManager implements DisposableBean {

    private static final Logger logger = LoggerFactory.getLogger(PlayerManager.class);

    /**
     * 最大金币数量
     */
    public static final long MAX_MONEY = 1000000000000000L;

    /**
     * 最大道具数量
     */
    public static final long MAX_PROP = Long.MAX_VALUE;

    /**
     * vip等级充值阈值
     */
    public static final long[] VIP_MONEY = {1, 2, 3, 4, 5, 6, 7, 8, 9};

    /**
     * 实体更新器
     */
    public static final List<IEntityUpdater> entityUpdaters = new LinkedList<>();

    /**
     * 需要更新的实体
     */
    public static Set<OseePlayerEntity> updateEntities = new CopyOnWriteArraySet<>();

    private static OseePlayerMapper playerMapper;

    private static OseePlayerTenureLogMapper tenureLogMapper;

    private static ShoppingMapper shoppingMapper;

    private static MessageMapper messageMapper;

    private static MessageManager messageManager;

    @Autowired
    public PlayerManager(OseePlayerMapper playerMapper, OseePlayerTenureLogMapper tenureLogMapper,
                         ShoppingMapper shoppingMapper, MessageMapper messageMapper, MessageManager messageManager, GUserPropsMapper gUserPropsMapper) {

        PlayerManager.playerMapper = playerMapper;
        PlayerManager.tenureLogMapper = tenureLogMapper;
        PlayerManager.shoppingMapper = shoppingMapper;
        PlayerManager.messageMapper = messageMapper;
        PlayerManager.messageManager = messageManager;

        PlayerManager.gUserPropsMapper = gUserPropsMapper;
        shoppingMapper.createTable();

    }

    @Autowired
    private AgentMapper agentMapper;

    @Autowired
    private static GUserPropsMapper gUserPropsMapper;

    @Autowired
    private UserPropsManager userPropsManager;

    /**
     * 添加多个物品并指定变动原因
     */
    public static void addItems(ServerUser user, List<ItemData> itemDatas, ItemChangeReason reason, boolean save) {

        OseePlayerEntity playerEntity = getPlayerEntity(user);

        if (playerEntity == null || playerEntity.getUserId() == 0 || user.getEntity() == null) {
            return;
        }

        synchronized (playerEntity) {

            for (ItemData item : itemDatas) {

                addItem(user, item.getItemId(), item.getCount(), reason, save);

            }

        }

        // for (ItemData itemData : itemDatas) {
        //
        // synchronized (playerEntity) {
        //
        // OseePlayerTenureLogEntity log = new OseePlayerTenureLogEntity();
        // log.setPreDiamond(playerEntity.getDiamond());
        // log.setPreMoney(playerEntity.getMoney());
        // log.setPreLottery(playerEntity.getLottery());
        // log.setPreBankMoney(playerEntity.getBankMoney());
        // log.setPreBronzeTorpedo(playerEntity.getBronzeTorpedo());
        // log.setPreSilverTorpedo(playerEntity.getSilverTorpedo());
        // log.setPreGoldTorpedo(playerEntity.getGoldTorpedo());
        // log.setPreSkillLock(playerEntity.getSkillLock());
        // log.setPreSkillFast(playerEntity.getSkillFast());
        // log.setPreSkillFrozen(playerEntity.getSkillFrozen());
        // log.setPreSkillCrit(playerEntity.getSkillCrit());
        // log.setPreBossBugle(playerEntity.getBossBugle());
        // log.setPreRareTorpedo(playerEntity.getRareTorpedo());
        // log.setPreRareTorpedoBang(playerEntity.getRareTorpedoBang());
        // log.setPreDragonCrystal(playerEntity.getDragonCrystal());
        //
        // ItemId itemId = ItemId.getItemIdById(itemData.getItemId());
        // switch (itemId) {
        // case MONEY:
        // playerEntity.setMoney(getMoneyResult(playerEntity.getMoney(), itemData.getCount()));
        // log.setChangeMoney(log.getChangeMoney() + itemData.getCount());
        // sendPlayerPropOneResponse(user, itemId, playerEntity.getMoney());
        // break;
        // case BANK_MONEY:
        // playerEntity.setBankMoney(getMoneyResult(playerEntity.getBankMoney(), itemData.getCount()));
        // log.setChangeBankMoney(log.getChangeBankMoney() + itemData.getCount());
        // sendPlayerPropOneResponse(user, itemId, playerEntity.getBankMoney());
        // break;
        // case LOTTERY:
        // playerEntity.setLottery(getMoneyResult(playerEntity.getLottery(), itemData.getCount()));
        // log.setChangeLottery(log.getChangeLottery() + itemData.getCount());
        // sendPlayerPropOneResponse(user, itemId, playerEntity.getLottery());
        // break;
        // case DIAMOND:
        // playerEntity.setDiamond(getMoneyResult(playerEntity.getDiamond(), itemData.getCount()));
        // log.setChangeDiamond(log.getChangeDiamond() + itemData.getCount());
        // sendPlayerPropOneResponse(user, itemId, playerEntity.getDiamond());
        // break;
        // // 鱼雷道具
        // case BRONZE_TORPEDO:
        // playerEntity
        // .setBronzeTorpedo(getPropResult(playerEntity.getBronzeTorpedo(), itemData.getCount()));
        // log.setChangeBronzeTorpedo(log.getChangeBronzeTorpedo() + itemData.getCount());
        // sendPlayerPropOneResponse(user, itemId, playerEntity.getBronzeTorpedo());
        // break;
        // case SILVER_TORPEDO:
        // playerEntity
        // .setSilverTorpedo(getPropResult(playerEntity.getSilverTorpedo(), itemData.getCount()));
        // log.setChangeSilverTorpedo(log.getChangeSilverTorpedo() + itemData.getCount());
        // sendPlayerPropOneResponse(user, itemId, playerEntity.getSilverTorpedo());
        // break;
        // case GOLD_TORPEDO:
        // playerEntity.setGoldTorpedo(getPropResult(playerEntity.getGoldTorpedo(), itemData.getCount()));
        // log.setChangeGoldTorpedo(log.getChangeGoldTorpedo() + itemData.getCount());
        // sendPlayerPropOneResponse(user, itemId, playerEntity.getGoldTorpedo());
        // break;
        // case GOLD_TORPEDO_BANG:
        // playerEntity
        // .setGoldTorpedoBang(getPropResult(playerEntity.getGoldTorpedoBang(), itemData.getCount()));
        // log.setChangeGoldTorpedo(log.getChangeGoldTorpedo() + itemData.getCount());
        // sendPlayerPropOneResponse(user, itemId, playerEntity.getGoldTorpedoBang());
        // break;
        // case RARE_TORPEDO:
        // playerEntity.setRareTorpedo(getPropResult(playerEntity.getRareTorpedo(), itemData.getCount()));
        // log.setChangeRareTorpedo(log.getChangeRareTorpedo() + itemData.getCount());
        // sendPlayerPropOneResponse(user, itemId, playerEntity.getRareTorpedo());
        // break;
        // case RARE_TORPEDO_BANG:
        // playerEntity
        // .setRareTorpedoBang(getPropResult(playerEntity.getRareTorpedoBang(), itemData.getCount()));
        // log.setChangeRareTorpedoBang(log.getChangeRareTorpedoBang() + itemData.getCount());
        // sendPlayerPropOneResponse(user, itemId, playerEntity.getRareTorpedoBang());
        // break;
        // // 技能
        // case SKILL_LOCK:
        // playerEntity.setSkillLock(getPropResult(playerEntity.getSkillLock(), itemData.getCount()));
        // log.setChangeSkillLock(log.getChangeSkillLock() + itemData.getCount());
        // sendPlayerPropOneResponse(user, itemId, playerEntity.getSkillLock());
        // break;
        // case SKILL_FROZEN:
        // playerEntity.setSkillFrozen(getPropResult(playerEntity.getSkillFrozen(), itemData.getCount()));
        // log.setChangeSkillFrozen(log.getChangeSkillFrozen() + itemData.getCount());
        // sendPlayerPropOneResponse(user, itemId, playerEntity.getSkillFrozen());
        // break;
        // case SKILL_FAST:
        // playerEntity.setSkillFast(getPropResult(playerEntity.getSkillFast(), itemData.getCount()));
        // log.setChangeSkillFast(log.getChangeSkillFast() + itemData.getCount());
        // sendPlayerPropOneResponse(user, itemId, playerEntity.getSkillFast());
        // break;
        // case SKILL_CRIT:
        // playerEntity.setSkillCrit(getPropResult(playerEntity.getSkillCrit(), itemData.getCount()));
        // log.setChangeSkillCrit(log.getChangeSkillCrit() + itemData.getCount());
        // sendPlayerPropOneResponse(user, itemId, playerEntity.getSkillCrit());
        // break;
        // // 月卡
        // case MONTH_CARD:
        // playerEntity.setMonthCardExpireDate(
        // getDateResult(playerEntity.getMonthCardExpireDate(), itemData.getCount()));
        // sendPlayerPropOneResponse(user, itemId,
        // playerEntity.getMonthCardExpireDate().atStartOfDay(ZoneId.systemDefault()).toInstant()
        // .toEpochMilli());
        // break;
        // // BOSS号角
        // case BOSS_BUGLE:
        // playerEntity.setBossBugle(getPropResult(playerEntity.getBossBugle(), itemData.getCount()));
        // log.setChangeBossBugle(log.getChangeBossBugle() + itemData.getCount());
        // sendPlayerPropOneResponse(user, itemId, playerEntity.getBossBugle());
        // break;
        // case QSZS_BATTERY_VIEW: // 骑士之誓炮台外观
        // playerEntity.setQszsBatteryExpireDate(
        // getDateResult(playerEntity.getQszsBatteryExpireDate(), itemData.getCount()));
        // if (getItemNum(user, ItemId.QSZS_BATTERY_VIEW) > 40000) {
        // sendPlayerPropOneResponse(user, itemId, -100);
        // } else {
        // sendPlayerPropOneResponse(user, itemId, getItemNum(user, ItemId.QSZS_BATTERY_VIEW));
        // }
        // break;
        // case BLNH_BATTERY_VIEW: // 冰龙怒吼炮台外观
        // playerEntity.setBlnhBatteryExpireDate(
        // getDateResult(playerEntity.getBlnhBatteryExpireDate(), itemData.getCount()));
        // if (getItemNum(user, ItemId.BLNH_BATTERY_VIEW) > 40000) {
        // sendPlayerPropOneResponse(user, itemId, -100);
        // } else {
        // sendPlayerPropOneResponse(user, itemId, getItemNum(user, ItemId.BLNH_BATTERY_VIEW));
        // }
        // break;
        // case LHTZ_BATTERY_VIEW: // 莲花童子炮台外观
        // playerEntity.setLhtzBatteryExpireDate(
        // getDateResult(playerEntity.getLhtzBatteryExpireDate(), itemData.getCount()));
        // if (getItemNum(user, ItemId.LHTZ_BATTERY_VIEW) > 40000) {
        // sendPlayerPropOneResponse(user, itemId, -100);
        // } else {
        // sendPlayerPropOneResponse(user, itemId, getItemNum(user, ItemId.LHTZ_BATTERY_VIEW));
        // }
        // break;
        // case LBS_BATTERY_VIEW: // 蓝宝石炮台外观
        // playerEntity.setLbsBatteryExpireDate(
        // getDateResult(playerEntity.getLbsBatteryExpireDate(), itemData.getCount()));
        // if (getItemNum(user, ItemId.LBS_BATTERY_VIEW) > 40000) {
        // sendPlayerPropOneResponse(user, itemId, -100);
        // } else {
        // sendPlayerPropOneResponse(user, itemId, getItemNum(user, ItemId.LBS_BATTERY_VIEW));
        // }
        // break;
        // case TJZX_BATTERY_VIEW: // 钛晶之息炮台外观
        // playerEntity.setTjzxBatteryExpireDate(
        // getDateResult(playerEntity.getTjzxBatteryExpireDate(), itemData.getCount()));
        // if (getItemNum(user, ItemId.TJZX_BATTERY_VIEW) > 40000) {
        // sendPlayerPropOneResponse(user, itemId, -100);
        // } else {
        // sendPlayerPropOneResponse(user, itemId, getItemNum(user, ItemId.TJZX_BATTERY_VIEW));
        // }
        // break;
        // case HJHP_BATTERY_VIEW: // 黄金火炮炮台外观
        // playerEntity.setHjhpBatteryExpireDate(
        // getDateResult(playerEntity.getHjhpBatteryExpireDate(), itemData.getCount()));
        // if (getItemNum(user, ItemId.HJHP_BATTERY_VIEW) > 40000) {
        // sendPlayerPropOneResponse(user, itemId, -100);
        // } else {
        // sendPlayerPropOneResponse(user, itemId, getItemNum(user, ItemId.HJHP_BATTERY_VIEW));
        // }
        // break;
        // case GJZY_BATTERY_VIEW: // 冠军之眼炮台外观
        // playerEntity.setGjzyBatteryExpireDate(
        // getDateResult(playerEntity.getGjzyBatteryExpireDate(), itemData.getCount()));
        // if (getItemNum(user, ItemId.GJZY_BATTERY_VIEW) > 40000) {
        // sendPlayerPropOneResponse(user, itemId, -100);
        // } else {
        // sendPlayerPropOneResponse(user, itemId, getItemNum(user, ItemId.GJZY_BATTERY_VIEW));
        // }
        // break;
        // case HJZP_BATTERY_VIEW: // 合金重炮炮台外观
        // playerEntity.setHjzpBatteryExpireDate(
        // getDateResult(playerEntity.getHjzpBatteryExpireDate(), itemData.getCount()));
        // if (getItemNum(user, ItemId.HJZP_BATTERY_VIEW) > 40000) {
        // sendPlayerPropOneResponse(user, itemId, -100);
        // } else {
        // sendPlayerPropOneResponse(user, itemId, getItemNum(user, ItemId.HJZP_BATTERY_VIEW));
        // }
        // break;
        // case ZLHP_BATTERY_VIEW: // 臻蓝火炮炮台外观
        // playerEntity.setZlhpBatteryExpireDate(
        // getDateResult(playerEntity.getZlhpBatteryExpireDate(), itemData.getCount()));
        // if (getItemNum(user, ItemId.ZLHP_BATTERY_VIEW) > 40000) {
        // sendPlayerPropOneResponse(user, itemId, -100);
        // } else {
        // sendPlayerPropOneResponse(user, itemId, getItemNum(user, ItemId.ZLHP_BATTERY_VIEW));
        // }
        // break;
        // case SWHP_BATTERY_VIEW: // 死亡火炮炮台外观
        // playerEntity.setSwhpBatteryExpireDate(
        // getDateResult(playerEntity.getSwhpBatteryExpireDate(), itemData.getCount()));
        // if (getItemNum(user, ItemId.SWHP_BATTERY_VIEW) > 40000) {
        // sendPlayerPropOneResponse(user, itemId, -100);
        // } else {
        // sendPlayerPropOneResponse(user, itemId, getItemNum(user, ItemId.SWHP_BATTERY_VIEW));
        // }
        // break;
        // case DRAGON_CRYSTAL: // 龙晶
        // playerEntity
        // .setDragonCrystal(getMoneyResult(playerEntity.getDragonCrystal(), itemData.getCount()));
        // log.setChangeDragonCrystal(log.getChangeDragonCrystal() + itemData.getCount());
        // sendPlayerPropOneResponse(user, itemId, playerEntity.getDragonCrystal());
        // break;
        // case FEN_SHEN: // 分身炮道具
        // playerEntity.setFenShen(getPropResult(playerEntity.getFenShen(), itemData.getCount()));
        // sendPlayerPropOneResponse(user, itemId, playerEntity.getFenShen());
        // break;
        // case SKILL_ELETIC: // 电磁炮道具
        // playerEntity.setSkillEle(getPropResult(playerEntity.getSkillEle(), itemData.getCount()));
        // sendPlayerPropOneResponse(user, itemId, playerEntity.getSkillEle());
        // break;
        // case DRAGON_BLESS: //龙珠祝福
        // break;
        // case FU_DAI_6: // 绿色福袋
        // break;
        // case FU_DAI_30: // 蓝色福袋
        // break;
        // case FU_DAI_128: // 紫色福袋
        // break;
        // case FU_DAI_328: // 橙色福袋
        // break;
        // case FU_DAI_648: // 红色福袋
        // break;
        // // 鱼骨
        // case YU_GU:
        // playerEntity.setYuGu(getMoneyResult(playerEntity.getYuGu(), itemData.getCount()));
        // sendPlayerPropOneResponse(user, itemId, playerEntity.getYuGu());
        // break;
        // // 海妖石
        // case HAI_YAO_SHI:
        // playerEntity.setHaiYaoShi(getMoneyResult(playerEntity.getHaiYaoShi(), itemData.getCount()));
        // sendPlayerPropOneResponse(user, itemId, playerEntity.getHaiYaoShi());
        // break;
        // // 王魂石
        // case WANG_HUN_SHI:
        // playerEntity.setWangHunShi(getMoneyResult(playerEntity.getWangHunShi(), itemData.getCount()));
        // sendPlayerPropOneResponse(user, itemId, playerEntity.getWangHunShi());
        // break;
        // // 海魂石
        // case HAI_HUN_SHI:
        // playerEntity.setHaiHunShi(getMoneyResult(playerEntity.getHaiHunShi(), itemData.getCount()));
        // sendPlayerPropOneResponse(user, itemId, playerEntity.getHaiHunShi());
        // break;
        // // 珍珠石
        // case ZHEN_ZHU_SHI:
        // playerEntity.setZhenZhuShi(getMoneyResult(playerEntity.getZhenZhuShi(), itemData.getCount()));
        // sendPlayerPropOneResponse(user, itemId, playerEntity.getZhenZhuShi());
        // break;
        // // 海兽石
        // case HAI_SHOU_SHI:
        // playerEntity.setHaiShouShi(getMoneyResult(playerEntity.getHaiShouShi(), itemData.getCount()));
        // sendPlayerPropOneResponse(user, itemId, playerEntity.getHaiShouShi());
        // break;
        // // 海魔石
        // case HAI_MO_SHI:
        // playerEntity.setHaiMoShi(getMoneyResult(playerEntity.getHaiMoShi(), itemData.getCount()));
        // sendPlayerPropOneResponse(user, itemId, playerEntity.getHaiMoShi());
        // break;
        // // 召唤石
        // case ZHAO_HUAN_SHI:
        // playerEntity.setZhaoHuanShi(getMoneyResult(playerEntity.getZhaoHuanShi(), itemData.getCount()));
        // sendPlayerPropOneResponse(user, itemId, playerEntity.getZhaoHuanShi());
        // break;
        // // 电磁石
        // case DIAN_CI_SHI:
        // playerEntity.setDianChiShi(getMoneyResult(playerEntity.getDianChiShi(), itemData.getCount()));
        // sendPlayerPropOneResponse(user, itemId, playerEntity.getDianChiShi());
        // break;
        // // 黑洞石
        // case HEI_DONG_SHI:
        // playerEntity.setHeiDongShi(getMoneyResult(playerEntity.getHeiDongShi(), itemData.getCount()));
        // sendPlayerPropOneResponse(user, itemId, playerEntity.getHeiDongShi());
        // break;
        // // 领主石
        // case LING_ZHU_SHI:
        // playerEntity.setLingZhuShi(getMoneyResult(playerEntity.getLingZhuShi(), itemData.getCount()));
        // sendPlayerPropOneResponse(user, itemId, playerEntity.getLingZhuShi());
        // break;
        // // 龙骨
        // case LONG_GU:
        // playerEntity.setLongGu(getMoneyResult(playerEntity.getLongGu(), itemData.getCount()));
        // sendPlayerPropOneResponse(user, itemId, playerEntity.getLongGu());
        // break;
        // // 龙珠
        // case LONG_ZHU:
        // playerEntity.setLongZhu(getMoneyResult(playerEntity.getLongZhu(), itemData.getCount()));
        // sendPlayerPropOneResponse(user, itemId, playerEntity.getLongZhu());
        // break;
        // // 龙元
        // case LONG_YUAN:
        // playerEntity.setLongYuan(getMoneyResult(playerEntity.getLongYuan(), itemData.getCount()));
        // sendPlayerPropOneResponse(user, itemId, playerEntity.getLongYuan());
        // break;
        // // 龙脊
        // case LONG_JI:
        // playerEntity.setLongJi(getMoneyResult(playerEntity.getLongJi(), itemData.getCount()));
        // sendPlayerPropOneResponse(user, itemId, playerEntity.getLongJi());
        // break;
        // // 黑洞炮技能
        // case SKILL_BLACK_HOLE:
        // playerEntity
        // .setSkillBlackHole(getMoneyResult(playerEntity.getSkillBlackHole(), itemData.getCount()));
        // sendPlayerPropOneResponse(user, itemId, playerEntity.getSkillBlackHole());
        // break;
        // // 鱼雷炮技能
        // case SKILL_TORPEDO:
        // playerEntity
        // .setSkillTorpedo(getMoneyResult(playerEntity.getSkillTorpedo(), itemData.getCount()));
        // sendPlayerPropOneResponse(user, itemId, playerEntity.getSkillTorpedo());
        // break;
        // // 赠送卡
        // case SEND_CARD:
        // playerEntity.setSendCard(getMoneyResult(playerEntity.getSendCard(), itemData.getCount()));
        // sendPlayerPropOneResponse(user, itemId, playerEntity.getSendCard());
        // break;
        // // 黑铁弹
        // case BLACK_BULLET:
        // playerEntity.setBlackBullet(getMoneyResult(playerEntity.getBlackBullet(), itemData.getCount()));
        // sendPlayerPropOneResponse(user, itemId, playerEntity.getBlackBullet());
        // break;
        // // 青铜弹
        // case BRONZE_BULLET:
        // playerEntity
        // .setBronzeBullet(getMoneyResult(playerEntity.getBronzeBullet(), itemData.getCount()));
        // sendPlayerPropOneResponse(user, itemId, playerEntity.getBronzeBullet());
        // break;
        // // 白银弹
        // case SILVER_BULLET:
        // playerEntity
        // .setSilverBullet(getMoneyResult(playerEntity.getSilverBullet(), itemData.getCount()));
        // sendPlayerPropOneResponse(user, itemId, playerEntity.getSilverBullet());
        // break;
        // // 黄金弹
        // case GOLD_BULLET:
        // playerEntity.setGoldBullet(getMoneyResult(playerEntity.getGoldBullet(), itemData.getCount()));
        // sendPlayerPropOneResponse(user, itemId, playerEntity.getGoldBullet());
        // break;
        // // 钻头
        // case SKILL_BIT:
        // playerEntity.setSkillBit(getMoneyResult(playerEntity.getSkillBit(), itemData.getCount()));
        // sendPlayerPropOneResponse(user, itemId, playerEntity.getSkillBit());
        // break;
        // // 直升500000
        // case ZHI_SHENG_500000:
        // playerEntity.setBatteryLevel(500000);
        // sendPlayerPropOneResponse(user, itemId, playerEntity.getBatteryLevel());
        // break;
        // default:
        // return;
        // }
        //
        // if (save) {
        // playerMapper.update(playerEntity);
        // if (reason != null) {
        // log.setUserId(user.getId());
        // log.setNickname(user.getNickname());
        // log.setReason(reason.getId());
        // tenureLogMapper.save(log);
        // }
        // } else {
        // updateEntities.add(playerEntity);
        // }
        // }
        // }

        // sendPlayerMoneyResponse(user);
        // sendPlayerPropResponse(user);
    }

    /**
     * 添加物品并指定变动原因
     */
    public static void addItem(ServerUser user, int itemId, long count, ItemChangeReason reason, boolean save) {
        addItem(user, ItemId.getItemIdById(itemId), count, reason, save);
    }

    /**
     * 添加物品并指定变动原因
     */
    public static void addItem(ServerUser user, ItemId itemId, long count, ItemChangeReason reason, boolean save) {

        OseePlayerEntity playerEntity = getPlayerEntity(user);

        if (playerEntity == null || playerEntity.getUserId() == 0 || user.getEntity() == null) {
            return;
        }

        synchronized (playerEntity) {

            OseePlayerTenureLogEntity log = new OseePlayerTenureLogEntity();
            log.setPreDiamond(playerEntity.getDiamond());
            log.setPreMoney(playerEntity.getMoney());
            log.setPreLottery(playerEntity.getLottery());
            log.setPreBankMoney(playerEntity.getBankMoney());
            log.setPreBronzeTorpedo(playerEntity.getBronzeTorpedo());
            log.setPreSilverTorpedo(playerEntity.getSilverTorpedo());
            log.setPreGoldTorpedo(playerEntity.getGoldTorpedo());
            log.setPreSkillLock(playerEntity.getSkillLock());
            log.setPreSkillFast(playerEntity.getSkillFast());
            log.setPreSkillDouble(playerEntity.getSkillDouble());
            log.setPreSkillFrozen(playerEntity.getSkillFrozen());
            log.setPreSkillCrit(playerEntity.getSkillCrit());
            log.setPreBossBugle(playerEntity.getBossBugle());
            log.setPreDragonCrystal(playerEntity.getDragonCrystal());
            log.setPreSkillEleTic(playerEntity.getSkillEle());

            switch (itemId) {
                case MONEY:
                    playerEntity.setMoney(getMoneyResult(playerEntity.getMoney(), count));
                    log.setChangeMoney(log.getChangeMoney() + count);
                    sendPlayerPropOneResponse(user, itemId, playerEntity.getMoney());
                    break;
                case BANK_MONEY:
                    playerEntity.setBankMoney(getMoneyResult(playerEntity.getBankMoney(), count));
                    log.setChangeBankMoney(log.getChangeBankMoney() + count);
                    sendPlayerPropOneResponse(user, itemId, playerEntity.getBankMoney());
                    break;
                case LOTTERY:
                    playerEntity.setLottery(getMoneyResult(playerEntity.getLottery(), count));
                    log.setChangeLottery(log.getChangeLottery() + count);
                    sendPlayerPropOneResponse(user, itemId, playerEntity.getLottery());
                    break;
                case DIAMOND:
                    playerEntity.setDiamond(getMoneyResult(playerEntity.getDiamond(), count));
                    log.setChangeDiamond(log.getChangeDiamond() + count);
                    sendPlayerPropOneResponse(user, itemId, playerEntity.getDiamond());
                    break;
                // 鱼雷道具
                case BRONZE_TORPEDO:
                    playerEntity.setBronzeTorpedo(getPropResult(playerEntity.getBronzeTorpedo(), count));
                    log.setChangeBronzeTorpedo(log.getChangeBronzeTorpedo() + count);
                    sendPlayerPropOneResponse(user, itemId, playerEntity.getBronzeTorpedo());
                    break;
                case SILVER_TORPEDO:
                    playerEntity.setSilverTorpedo(getPropResult(playerEntity.getSilverTorpedo(), count));
                    log.setChangeSilverTorpedo(log.getChangeSilverTorpedo() + count);
                    sendPlayerPropOneResponse(user, itemId, playerEntity.getSilverTorpedo());
                    break;
                case GOLD_TORPEDO:
                    playerEntity.setGoldTorpedo(getPropResult(playerEntity.getGoldTorpedo(), count));
                    log.setChangeGoldTorpedo(log.getChangeGoldTorpedo() + count);
                    sendPlayerPropOneResponse(user, itemId, playerEntity.getGoldTorpedo());
                    break;
                case GOLD_TORPEDO_BANG:
                    playerEntity.setGoldTorpedoBang(getPropResult(playerEntity.getGoldTorpedoBang(), count));
                    log.setChangeGoldTorpedo(log.getChangeGoldTorpedo() + count);
                    sendPlayerPropOneResponse(user, itemId, playerEntity.getGoldTorpedoBang());
                    break;
                case RARE_TORPEDO:
                    playerEntity.setRareTorpedo(getPropResult(playerEntity.getRareTorpedo(), count));
                    log.setChangeRareTorpedo(log.getChangeRareTorpedo() + count);
                    sendPlayerPropOneResponse(user, itemId, playerEntity.getRareTorpedo());
                    break;
                case RARE_TORPEDO_BANG:
                    playerEntity.setRareTorpedoBang(getPropResult(playerEntity.getRareTorpedoBang(), count));
                    log.setChangeRareTorpedoBang(log.getChangeRareTorpedoBang() + count);
                    sendPlayerPropOneResponse(user, itemId, playerEntity.getRareTorpedoBang());
                    break;
                // 技能
                case SKILL_LOCK:
                    playerEntity.setSkillLock(getPropResult(playerEntity.getSkillLock(), count));
                    log.setChangeSkillLock(log.getChangeSkillLock() + count);
                    sendPlayerPropOneResponse(user, itemId, playerEntity.getSkillLock());
                    break;
                case MAGIC_LAMP: // 神灯道具
                    playerEntity.setMagicLamp(getPropResult(playerEntity.getMagicLamp(), count));
                    sendPlayerPropOneResponse(user, itemId, playerEntity.getMagicLamp());
                    break;
                case SKILL_FROZEN:
                    playerEntity.setSkillFrozen(getPropResult(playerEntity.getSkillFrozen(), count));
                    log.setChangeSkillFrozen(log.getChangeSkillFrozen() + count);
                    sendPlayerPropOneResponse(user, itemId, playerEntity.getSkillFrozen());
                    break;
                case SKILL_FAST:
                    playerEntity.setSkillFast(getPropResult(playerEntity.getSkillFast(), count));
                    log.setChangeSkillFast(log.getChangeSkillFast() + count);
                    sendPlayerPropOneResponse(user, itemId, playerEntity.getSkillFast());
                    break;
                case SKILL_DOUBLE:
                    playerEntity.setSkillDouble(getPropResult(playerEntity.getSkillDouble(), count));
                    log.setChangeSkillDouble(log.getChangeSkillDouble() + count);
                    sendPlayerPropOneResponse(user, itemId, playerEntity.getSkillDouble());
                    break;
                case SKILL_CRIT:
                    playerEntity.setSkillCrit(getPropResult(playerEntity.getSkillCrit(), count));
                    log.setChangeSkillCrit(log.getChangeSkillCrit() + count);
                    sendPlayerPropOneResponse(user, itemId, playerEntity.getSkillCrit());
                    break;
                // 月卡
                case MONTH_CARD:
                    playerEntity.setMonthCardExpireDate(getDateResult(playerEntity.getMonthCardExpireDate(), count));
                    sendPlayerPropOneResponse(user, itemId, playerEntity.getMonthCardExpireDate()
                            .atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli());
                    break;
                // BOSS号角
                case BOSS_BUGLE:
                    playerEntity.setBossBugle(getPropResult(playerEntity.getBossBugle(), count));
                    log.setChangeBossBugle(log.getChangeBossBugle() + count);
                    sendPlayerPropOneResponse(user, itemId, playerEntity.getBossBugle());
                    break;
                case QSZS_BATTERY_VIEW: // 骑士之誓炮台外观
                    playerEntity
                            .setQszsBatteryExpireDate(getDateResult(playerEntity.getQszsBatteryExpireDate(), count));
                    if (getItemNum(user, ItemId.QSZS_BATTERY_VIEW) > 40000) {
                        sendPlayerPropOneResponse(user, itemId, -100);
                    } else {
                        sendPlayerPropOneResponse(user, itemId, getItemNum(user, ItemId.QSZS_BATTERY_VIEW));
                    }
                    break;
                case BLNH_BATTERY_VIEW: // 冰龙怒吼炮台外观
                    playerEntity
                            .setBlnhBatteryExpireDate(getDateResult(playerEntity.getBlnhBatteryExpireDate(), count));
                    if (getItemNum(user, ItemId.BLNH_BATTERY_VIEW) > 40000) {
                        sendPlayerPropOneResponse(user, itemId, -100);
                    } else {
                        sendPlayerPropOneResponse(user, itemId, getItemNum(user, ItemId.BLNH_BATTERY_VIEW));
                    }
                    break;
                case LHTZ_BATTERY_VIEW: // 莲花童子炮台外观
                    playerEntity
                            .setLhtzBatteryExpireDate(getDateResult(playerEntity.getLhtzBatteryExpireDate(), count));
                    if (getItemNum(user, ItemId.LHTZ_BATTERY_VIEW) > 40000) {
                        sendPlayerPropOneResponse(user, itemId, -100);
                    } else {
                        sendPlayerPropOneResponse(user, itemId, getItemNum(user, ItemId.LHTZ_BATTERY_VIEW));
                    }
                    break;
                case LBS_BATTERY_VIEW: // 蓝宝石炮台外观
                    playerEntity.setLbsBatteryExpireDate(getDateResult(playerEntity.getLbsBatteryExpireDate(), count));
                    if (getItemNum(user, ItemId.LBS_BATTERY_VIEW) > 40000) {
                        sendPlayerPropOneResponse(user, itemId, -100);
                    } else {
                        sendPlayerPropOneResponse(user, itemId, getItemNum(user, ItemId.LBS_BATTERY_VIEW));
                    }
                    break;
                case TJZX_BATTERY_VIEW: // 钛晶之息炮台外观
                    playerEntity
                            .setTjzxBatteryExpireDate(getDateResult(playerEntity.getTjzxBatteryExpireDate(), count));
                    if (getItemNum(user, ItemId.TJZX_BATTERY_VIEW) > 40000) {
                        sendPlayerPropOneResponse(user, itemId, -100);
                    } else {
                        sendPlayerPropOneResponse(user, itemId, getItemNum(user, ItemId.TJZX_BATTERY_VIEW));
                    }
                    break;
                case HJHP_BATTERY_VIEW: // 黄金火炮炮台外观
                    playerEntity
                            .setHjhpBatteryExpireDate(getDateResult(playerEntity.getHjhpBatteryExpireDate(), count));
                    if (getItemNum(user, ItemId.HJHP_BATTERY_VIEW) > 40000) {
                        sendPlayerPropOneResponse(user, itemId, -100);
                    } else {
                        sendPlayerPropOneResponse(user, itemId, getItemNum(user, ItemId.HJHP_BATTERY_VIEW));
                    }
                    break;
                case GJZY_BATTERY_VIEW: // 冠军之眼炮台外观
                    playerEntity
                            .setGjzyBatteryExpireDate(getDateResult(playerEntity.getGjzyBatteryExpireDate(), count));
                    if (getItemNum(user, ItemId.GJZY_BATTERY_VIEW) > 40000) {
                        sendPlayerPropOneResponse(user, itemId, -100);
                    } else {
                        sendPlayerPropOneResponse(user, itemId, getItemNum(user, ItemId.GJZY_BATTERY_VIEW));
                    }
                    break;
                case HJZP_BATTERY_VIEW: // 合金重炮炮台外观
                    playerEntity
                            .setHjzpBatteryExpireDate(getDateResult(playerEntity.getHjzpBatteryExpireDate(), count));
                    if (getItemNum(user, ItemId.HJZP_BATTERY_VIEW) > 40000) {
                        sendPlayerPropOneResponse(user, itemId, -100);
                    } else {
                        sendPlayerPropOneResponse(user, itemId, getItemNum(user, ItemId.HJZP_BATTERY_VIEW));
                    }
                    break;
                case ZLHP_BATTERY_VIEW: // 臻蓝火炮炮台外观
                    playerEntity
                            .setZlhpBatteryExpireDate(getDateResult(playerEntity.getZlhpBatteryExpireDate(), count));
                    if (getItemNum(user, ItemId.ZLHP_BATTERY_VIEW) > 40000) {
                        sendPlayerPropOneResponse(user, itemId, -100);
                    } else {
                        sendPlayerPropOneResponse(user, itemId, getItemNum(user, ItemId.ZLHP_BATTERY_VIEW));
                    }
                    break;
                case SWHP_BATTERY_VIEW: // 死亡火炮炮台外观
                    playerEntity
                            .setSwhpBatteryExpireDate(getDateResult(playerEntity.getSwhpBatteryExpireDate(), count));
                    if (getItemNum(user, ItemId.SWHP_BATTERY_VIEW) > 40000) {
                        sendPlayerPropOneResponse(user, itemId, -100);
                    } else {
                        sendPlayerPropOneResponse(user, itemId, getItemNum(user, ItemId.SWHP_BATTERY_VIEW));
                    }
                    break;
                case DRAGON_CRYSTAL: // 龙晶
                    playerEntity.setDragonCrystal(getMoneyResult(playerEntity.getDragonCrystal(), count));
                    log.setChangeDragonCrystal(log.getChangeDragonCrystal() + count);
                    sendPlayerPropOneResponse(user, itemId, playerEntity.getDragonCrystal());
                    break;
                case FEN_SHEN: // 分身炮道具
                    playerEntity.setFenShen(getPropResult(playerEntity.getFenShen(), count));
                    sendPlayerPropOneResponse(user, itemId, playerEntity.getFenShen());
                    break;
                case SKILL_ELETIC: // 电磁炮道具
                    playerEntity.setSkillEle(getPropResult(playerEntity.getSkillEle(), count));
                    sendPlayerPropOneResponse(user, itemId, playerEntity.getSkillEle());
                    break;
                // 鱼骨
                case YU_GU:
                    playerEntity.setYuGu(getMoneyResult(playerEntity.getYuGu(), count));
                    sendPlayerPropOneResponse(user, itemId, playerEntity.getYuGu());
                    break;
                // 海妖石
                case HAI_YAO_SHI:
                    playerEntity.setHaiYaoShi(getMoneyResult(playerEntity.getHaiYaoShi(), count));
                    sendPlayerPropOneResponse(user, itemId, playerEntity.getHaiYaoShi());
                    break;
                // 王魂石
                case WANG_HUN_SHI:
                    playerEntity.setWangHunShi(getMoneyResult(playerEntity.getWangHunShi(), count));
                    sendPlayerPropOneResponse(user, itemId, playerEntity.getWangHunShi());
                    break;
                // 海魂石
                case HAI_HUN_SHI:
                    playerEntity.setHaiHunShi(getMoneyResult(playerEntity.getHaiHunShi(), count));
                    sendPlayerPropOneResponse(user, itemId, playerEntity.getHaiHunShi());
                    break;
                // 珍珠石
                case ZHEN_ZHU_SHI:
                    playerEntity.setZhenZhuShi(getMoneyResult(playerEntity.getZhenZhuShi(), count));
                    sendPlayerPropOneResponse(user, itemId, playerEntity.getZhenZhuShi());
                    break;
                // 海兽石
                case HAI_SHOU_SHI:
                    playerEntity.setHaiShouShi(getMoneyResult(playerEntity.getHaiShouShi(), count));
                    sendPlayerPropOneResponse(user, itemId, playerEntity.getHaiShouShi());
                    break;
                // 海魔石
                case HAI_MO_SHI:
                    playerEntity.setHaiMoShi(getMoneyResult(playerEntity.getHaiMoShi(), count));
                    sendPlayerPropOneResponse(user, itemId, playerEntity.getHaiMoShi());
                    break;
                // 召唤石
                case ZHAO_HUAN_SHI:
                    playerEntity.setZhaoHuanShi(getMoneyResult(playerEntity.getZhaoHuanShi(), count));
                    sendPlayerPropOneResponse(user, itemId, playerEntity.getZhaoHuanShi());
                    break;
                // 电磁石
                case DIAN_CI_SHI:
                    playerEntity.setDianChiShi(getMoneyResult(playerEntity.getDianChiShi(), count));
                    sendPlayerPropOneResponse(user, itemId, playerEntity.getDianChiShi());
                    break;
                // 黑洞石
                case HEI_DONG_SHI:
                    playerEntity.setHeiDongShi(getMoneyResult(playerEntity.getHeiDongShi(), count));
                    sendPlayerPropOneResponse(user, itemId, playerEntity.getHeiDongShi());
                    break;
                // 领主石
                case LING_ZHU_SHI:
                    playerEntity.setLingZhuShi(getMoneyResult(playerEntity.getLingZhuShi(), count));
                    sendPlayerPropOneResponse(user, itemId, playerEntity.getLingZhuShi());
                    break;
                // 龙骨
                case LONG_GU:
                    playerEntity.setLongGu(getMoneyResult(playerEntity.getLongGu(), count));
                    sendPlayerPropOneResponse(user, itemId, playerEntity.getLongGu());
                    break;
                // 龙珠
                case LONG_ZHU:
                    playerEntity.setLongZhu(getMoneyResult(playerEntity.getLongZhu(), count));
                    sendPlayerPropOneResponse(user, itemId, playerEntity.getLongZhu());
                    break;
                // 龙元
                case LONG_YUAN:
                    playerEntity.setLongYuan(getMoneyResult(playerEntity.getLongYuan(), count));
                    sendPlayerPropOneResponse(user, itemId, playerEntity.getLongYuan());
                    break;
                // 龙脊
                case LONG_JI:
                    playerEntity.setLongJi(getMoneyResult(playerEntity.getLongJi(), count));
                    sendPlayerPropOneResponse(user, itemId, playerEntity.getLongJi());
                    break;
                // 黑洞炮技能
                case SKILL_BLACK_HOLE:
                    playerEntity.setSkillBlackHole(getMoneyResult(playerEntity.getSkillBlackHole(), count));
                    sendPlayerPropOneResponse(user, itemId, playerEntity.getSkillBlackHole());
                    break;
                // 鱼雷炮技能
                case SKILL_TORPEDO:
                    playerEntity.setSkillTorpedo(getMoneyResult(playerEntity.getSkillTorpedo(), count));
                    sendPlayerPropOneResponse(user, itemId, playerEntity.getSkillTorpedo());
                    break;
                // 黑铁弹
                case BLACK_BULLET:
                    playerEntity.setBlackBullet(getMoneyResult(playerEntity.getBlackBullet(), count));
                    sendPlayerPropOneResponse(user, itemId, playerEntity.getBlackBullet());
                    break;
                // 青铜弹
                case BRONZE_BULLET:
                    playerEntity.setBronzeBullet(getMoneyResult(playerEntity.getBronzeBullet(), count));
                    sendPlayerPropOneResponse(user, itemId, playerEntity.getBronzeBullet());
                    break;
                // 白银弹
                case SILVER_BULLET:
                    playerEntity.setSilverBullet(getMoneyResult(playerEntity.getSilverBullet(), count));
                    sendPlayerPropOneResponse(user, itemId, playerEntity.getSilverBullet());
                    break;
                // 黄金弹
                case GOLD_BULLET:
                    playerEntity.setGoldBullet(getMoneyResult(playerEntity.getGoldBullet(), count));
                    sendPlayerPropOneResponse(user, itemId, playerEntity.getGoldBullet());
                    break;
                // 钻头
                case SKILL_BIT:
                    playerEntity.setSkillBit(getMoneyResult(playerEntity.getSkillBit(), count));
                    sendPlayerPropOneResponse(user, itemId, playerEntity.getSkillBit());
                    break;
                // 直升500000
                case ZHI_SHENG_500000:
                    playerEntity.setBatteryLevel(500000);
                    sendPlayerPropOneResponse(user, itemId, playerEntity.getBatteryLevel());
                    break;
                case SKILL_TIAN_SHEN_GUAN_YU: // 天神关羽
                    playerEntity
                            .setTianShenGuanYuBatteryExpireDate(getDateResult(playerEntity.getTianShenGuanYuBatteryExpireDate(), count));
                    long itemNum = getItemNum(user, ItemId.SKILL_TIAN_SHEN_GUAN_YU);
                    if (itemNum < 0) {
                        sendPlayerPropOneResponse(user, itemId, -100);
                    } else {
                        sendPlayerPropOneResponse(user, itemId, itemNum);
                    }
                    break;
                default:
                    return;
            }

            if (save) {
                playerMapper.update(playerEntity);
            } else {
                updateEntities.add(playerEntity);
            }

        }

        // sendPlayerMoneyResponse(user);
        // sendPlayerPropResponse(user);
    }

    /**
     * 检查物品数量
     */
    public static boolean checkItem(ServerUser user, int itemId, long count) {
        return checkItem(user, ItemId.getItemIdById(itemId), count);
    }

    /**
     * 检查物品数量
     */
    public static boolean checkItem(ServerUser user, ItemId itemId, long count) {
        OseePlayerEntity playerEntity = getPlayerEntity(user);
        if (playerEntity == null) {
            return false;
        }
        synchronized (playerEntity) {
            switch (itemId) {
                case MONEY:
                    return playerEntity.getMoney() >= count;
                case BANK_MONEY:
                    return playerEntity.getBankMoney() >= count;
                case LOTTERY:
                    return playerEntity.getLottery() >= count;
                case DIAMOND:
                    return playerEntity.getDiamond() >= count;
                // 鱼雷道具
                case BRONZE_TORPEDO:
                    return playerEntity.getBronzeTorpedo() >= count;
                case SILVER_TORPEDO:
                    return playerEntity.getSilverTorpedo() >= count;
                case GOLD_TORPEDO:
                    return playerEntity.getGoldTorpedo() >= count;
                case GOLD_TORPEDO_BANG:
                    return playerEntity.getGoldTorpedoBang() >= count;
                case RARE_TORPEDO:
                    return playerEntity.getRareTorpedo() >= count;
                case RARE_TORPEDO_BANG:
                    return playerEntity.getRareTorpedoBang() >= count;
                // 技能
                case SKILL_LOCK:
                    return playerEntity.getSkillLock() >= count;
                case MAGIC_LAMP:
                    return playerEntity.getMagicLamp() >= count;
                case SKILL_FROZEN:
                    return playerEntity.getSkillFrozen() >= count;
                case SKILL_FAST:
                    return playerEntity.getSkillFast() >= count;
                case SKILL_DOUBLE:
                    return playerEntity.getSkillDouble() >= count;
                case SKILL_CRIT:
                    return playerEntity.getSkillCrit() >= count;
                case MONTH_CARD: // 月卡
                    long days = playerEntity.getMonthCardExpireDate().toEpochDay() - LocalDate.now().toEpochDay();
                    if (count < 0) {
                        return true;
                    } else {
                        return days >= count;
                    }
                    // BOSS号角
                case BOSS_BUGLE:
                    return playerEntity.getBossBugle() >= count;
                case DRAGON_CRYSTAL: // 龙晶
                    return playerEntity.getDragonCrystal() >= count;
                case FEN_SHEN: // 分身炮道具
                    return playerEntity.getFenShen() >= count;
                case SKILL_ELETIC: // 电磁炮道具
                    return playerEntity.getSkillEle() >= count;
                // 鱼骨
                case YU_GU:
                    return playerEntity.getYuGu() >= count;
                // 海妖石
                case HAI_YAO_SHI:
                    return playerEntity.getHaiYaoShi() >= count;
                // 王魂石
                case WANG_HUN_SHI:
                    return playerEntity.getWangHunShi() >= count;
                // 海魂石
                case HAI_HUN_SHI:
                    return playerEntity.getHaiHunShi() >= count;
                // 珍珠石
                case ZHEN_ZHU_SHI:
                    return playerEntity.getZhenZhuShi() >= count;
                // 海兽石
                case HAI_SHOU_SHI:
                    return playerEntity.getHaiShouShi() >= count;
                // 海魔石
                case HAI_MO_SHI:
                    return playerEntity.getHaiMoShi() >= count;
                // 召唤石
                case ZHAO_HUAN_SHI:
                    return playerEntity.getZhaoHuanShi() >= count;
                // 电磁石
                case DIAN_CI_SHI:
                    return playerEntity.getDianChiShi() >= count;
                // 黑洞石
                case HEI_DONG_SHI:
                    return playerEntity.getHeiDongShi() >= count;
                // 领主石
                case LING_ZHU_SHI:
                    return playerEntity.getLingZhuShi() >= count;
                // 龙骨
                case LONG_GU:
                    return playerEntity.getLongGu() >= count;
                // 龙珠
                case LONG_ZHU:
                    return playerEntity.getLongZhu() >= count;
                // 龙元
                case LONG_YUAN:
                    return playerEntity.getLongYuan() >= count;
                // 龙脊
                case LONG_JI:
                    return playerEntity.getLongJi() >= count;
                // 黑洞炮技能
                case SKILL_BLACK_HOLE:
                    return playerEntity.getSkillBlackHole() >= count;
                // 鱼雷炮技能
                case SKILL_TORPEDO:
                    return playerEntity.getSkillTorpedo() >= count;
                // 黑铁弹
                case BLACK_BULLET:
                    return playerEntity.getBlackBullet() >= count;
                // 青铜弹
                case BRONZE_BULLET:
                    return playerEntity.getBronzeBullet() >= count;
                // 白银弹
                case SILVER_BULLET:
                    return playerEntity.getSilverBullet() >= count;
                // 黄金弹
                case GOLD_BULLET:
                    return playerEntity.getGoldBullet() >= count;
                // 钻头
                case SKILL_BIT:
                    return playerEntity.getSkillBit() >= count;
                case SKILL_TIAN_SHEN_GUAN_YU:
//                    UserProps userProps = gUserPropsMapper.getUserProps(user.getId(), itemId.getId());
//                    if (ObjectUtils.isEmpty(userProps)||ObjectUtils.isEmpty(userProps.getExpirationTime()) ||
//                            userProps.getExpirationTime().getTime() < new Date().getTime()) {
////                        NetManager.sendHintBoxMessageToClient("9005：道具已经到期", user,10);
//                        return false;
//                    }
                    return true;
                default:
                    return false;
            }
        }
    }

    /**
     * 获取物品数量
     */
    public static long getItemNum(ServerUser user, ItemId itemId) {
        OseePlayerEntity playerEntity = getPlayerEntity(user);
        if (playerEntity == null) {
            return 0;
        }
        synchronized (playerEntity) {
            switch (itemId) {
                case MONEY:
                    return playerEntity.getMoney();
                case BANK_MONEY:
                    return playerEntity.getBankMoney();
                case LOTTERY:
                    return playerEntity.getLottery();
                case DIAMOND:
                    return playerEntity.getDiamond();
                // 鱼雷道具
                case BRONZE_TORPEDO:
                    return playerEntity.getBronzeTorpedo();
                case SILVER_TORPEDO:
                    return playerEntity.getSilverTorpedo();
                case GOLD_TORPEDO:
                    return playerEntity.getGoldTorpedo();
                case GOLD_TORPEDO_BANG:
                    return playerEntity.getGoldTorpedoBang();
                // 技能
                case SKILL_LOCK:
                    return playerEntity.getSkillLock();
                case SKILL_FROZEN:
                    return playerEntity.getSkillFrozen();
                case SKILL_FAST:
                    return playerEntity.getSkillFast();
                case SKILL_CRIT:
                    return playerEntity.getSkillCrit();
                // 月卡
                case MONTH_CARD: {
                    long days = playerEntity.getMonthCardExpireDate().toEpochDay() - LocalDate.now().toEpochDay();
                    return days < 0 ? 0 : days;
                }
                // BOSS号角
                case BOSS_BUGLE:
                    return playerEntity.getBossBugle();
                case QSZS_BATTERY_VIEW: { // 骑士之誓炮台外观
                    long days = playerEntity.getQszsBatteryExpireDate().toEpochDay() - LocalDate.now().toEpochDay();
                    return days < 0 ? 0 : days;
                }
                case BLNH_BATTERY_VIEW: { // 冰龙怒吼炮台外观
                    long days = playerEntity.getBlnhBatteryExpireDate().toEpochDay() - LocalDate.now().toEpochDay();
                    return days < 0 ? 0 : days;
                }
                case LHTZ_BATTERY_VIEW: { // 莲花童子炮台外观
                    long days = playerEntity.getLhtzBatteryExpireDate().toEpochDay() - LocalDate.now().toEpochDay();
                    return days < 0 ? 0 : days;
                }
                case SWHP_BATTERY_VIEW: { // 死亡火炮炮台外观
                    long days = playerEntity.getSwhpBatteryExpireDate().toEpochDay() - LocalDate.now().toEpochDay();
                    return days < 0 ? 0 : days;
                }
                case LBS_BATTERY_VIEW: { // 蓝宝石炮台外观
                    long days = playerEntity.getLbsBatteryExpireDate().toEpochDay() - LocalDate.now().toEpochDay();
                    return days < 0 ? 0 : days;
                }
                case TJZX_BATTERY_VIEW: { // 钛晶之息炮台外观
                    long days = playerEntity.getTjzxBatteryExpireDate().toEpochDay() - LocalDate.now().toEpochDay();
                    return days < 0 ? 0 : days;
                }
                case HJHP_BATTERY_VIEW: { // 黄金火炮炮台外观
                    long days = playerEntity.getHjhpBatteryExpireDate().toEpochDay() - LocalDate.now().toEpochDay();
                    return days < 0 ? 0 : days;
                }
                case GJZY_BATTERY_VIEW: { // 冠军之眼炮台外观
                    long days = playerEntity.getGjzyBatteryExpireDate().toEpochDay() - LocalDate.now().toEpochDay();
                    return days < 0 ? 0 : days;
                }
                case HJZP_BATTERY_VIEW: { // 合金重炮炮台外观
                    long days = playerEntity.getHjzpBatteryExpireDate().toEpochDay() - LocalDate.now().toEpochDay();
                    return days < 0 ? 0 : days;
                }
                case ZLHP_BATTERY_VIEW: { // 臻蓝火炮炮台外观
                    long days = playerEntity.getZlhpBatteryExpireDate().toEpochDay() - LocalDate.now().toEpochDay();
                    return days < 0 ? 0 : days;
                }
                case DRAGON_CRYSTAL: // 龙晶
                    return playerEntity.getDragonCrystal();
                case FEN_SHEN: // 分身炮道具
                    return playerEntity.getFenShen();
                case SKILL_ELETIC: // 电磁炮炮道具
                    return playerEntity.getSkillEle();
                case RARE_TORPEDO: // 稀有核弹道具
                    return playerEntity.getRareTorpedo();
                case RARE_TORPEDO_BANG: // 绑定稀有核弹道具
                    return playerEntity.getRareTorpedoBang();
                // 鱼骨
                case YU_GU:
                    return playerEntity.getYuGu();
                // 海妖石
                case HAI_YAO_SHI:
                    return playerEntity.getHaiYaoShi();
                // 王魂石
                case WANG_HUN_SHI:
                    return playerEntity.getWangHunShi();
                // 海魂石
                case HAI_HUN_SHI:
                    return playerEntity.getHaiHunShi();
                // 珍珠石
                case ZHEN_ZHU_SHI:
                    return playerEntity.getZhenZhuShi();
                // 海兽石
                case HAI_SHOU_SHI:
                    return playerEntity.getHaiShouShi();
                // 海魔石
                case HAI_MO_SHI:
                    return playerEntity.getHaiMoShi();
                // 召唤石
                case ZHAO_HUAN_SHI:
                    return playerEntity.getZhaoHuanShi();
                // 电磁石
                case DIAN_CI_SHI:
                    return playerEntity.getDianChiShi();
                // 黑洞石
                case HEI_DONG_SHI:
                    return playerEntity.getHeiDongShi();
                // 领主石
                case LING_ZHU_SHI:
                    return playerEntity.getLingZhuShi();
                // 龙骨
                case LONG_GU:
                    return playerEntity.getLongGu();
                // 龙珠
                case LONG_ZHU:
                    return playerEntity.getLongZhu();
                // 龙元
                case LONG_YUAN:
                    return playerEntity.getLongYuan();
                // 龙脊
                case LONG_JI:
                    return playerEntity.getLongJi();
                // 黑洞炮技能
                case SKILL_BLACK_HOLE:
                    return playerEntity.getSkillBlackHole();
                // 鱼雷炮技能
                case SKILL_TORPEDO:
                    return playerEntity.getSkillTorpedo();
                // 黑铁弹
                case BLACK_BULLET:
                    return playerEntity.getBlackBullet();
                // 青铜弹
                case BRONZE_BULLET:
                    return playerEntity.getBronzeBullet();
                // 白银弹
                case SILVER_BULLET:
                    return playerEntity.getSilverBullet();
                // 黄金弹
                case GOLD_BULLET:
                    return playerEntity.getGoldBullet();
                // 钻头
                case SKILL_BIT:
                    return playerEntity.getSkillBit();
                case MAGIC_LAMP: // 获取神灯
                    return playerEntity.getMagicLamp();
                case SKILL_TIAN_SHEN_GUAN_YU: // 获取天神关羽数量
//                    long days = playerEntity.getTianShenGuanYuBatteryExpireDate().toEpochDay() - LocalDate.now().toEpochDay();
                    UserProps userProps = gUserPropsMapper.getUserProps(user.getId(), itemId.getId());
                    if (ObjectUtils.isEmpty(userProps) || ObjectUtils.isEmpty(userProps.getExpirationTime())) {
                        return 10;
                    }
                    return (userProps.getExpirationTime().getTime() - System.currentTimeMillis());
                default:
                    return 0;
            }
        }
    }

    /**
     * 获取货币计算结果
     */
    public static long getMoneyResult(long money, long addMoney) {
        long result = money + addMoney;
        return result > 0 ? result < MAX_MONEY ? result : MAX_MONEY - 1 : 0;
    }

    /**
     * 获取道具计算结果
     */
    public static long getPropResult(long propNum, long addProp) {
        long result = propNum + addProp;
        return result > 0 ? (result <= MAX_PROP ? result : MAX_PROP) : 0;
    }

    /**
     * 获取日期计算结果
     */
    public static LocalDate getDateResult(LocalDate date, long days) {
        LocalDate now = LocalDate.now();
        if (date == null || date.isBefore(now)) { // 如果已经过期了就从今天开始计算
            return now.plusDays(days);
        }
        // 未过期就继续叠加时间
        return date.plusDays(days);
    }

    /**
     * 发送每日福袋购买信息
     */
    public static void sendDailyBagBuyInfo(ServerUser user) {
        ShoppingEntity shoppingEntity = getPlayerShopping(user.getId());

        OseeLobbyMessage.DailyBagBuyInfoResponse.Builder builder =
                OseeLobbyMessage.DailyBagBuyInfoResponse.newBuilder();
        shoppingEntity.getDailyBuyInfoMap().forEach((itemId, buyInfo) -> {
            if (DateUtils.isSameDay(buyInfo.getLastTime(), new Date())) {
                builder.addBuyItems(itemId.getId());
            }

            if (itemId == ItemId.FU_DAI_6) {
                builder.setFuDai6(buyInfo.getDays());
            } else if (itemId == ItemId.FU_DAI_30) {
                builder.setFuDai30(buyInfo.getDays());
            } else if (itemId == ItemId.FU_DAI_128) {
                builder.setFuDai128(buyInfo.getDays());
            } else if (itemId == ItemId.FU_DAI_328) {
                builder.setFuDai328(buyInfo.getDays());
            } else if (itemId == ItemId.FU_DAI_648) {
                builder.setFuDai648(buyInfo.getDays());
            }
        });
        NetManager.sendMessage(OseeMsgCode.S_C_TTMY_DAILY_BAG_BUY_INFO_RESPONSE_VALUE, builder, user);
    }

    /**
     * 发送每日礼包购买信息
     */
    public static void sendDailyBuyGiftInfo(ServerUser user) {
        ShoppingEntity shoppingEntity = getPlayerShopping(user.getId());

        HwLoginMessage.DailyBuyGiftInfoResponse.Builder builder = HwLoginMessage.DailyBuyGiftInfoResponse.newBuilder();
        shoppingEntity.getDailyBuyInfoMap().forEach((itemId, buyInfo) -> {
            if (DateUtils.isSameDay(buyInfo.getLastTime(), new Date())) {
                builder.addBuyItems(itemId.getId());
            }

            if (itemId == ItemId.FU_DAI_80) {
                builder.setFuDai80(buyInfo.getDays());
            } else if (itemId == ItemId.FU_DAI_280) {
                builder.setFuDai280(buyInfo.getDays());
            } else if (itemId == ItemId.FU_DAI_880) {
                builder.setFuDai880(buyInfo.getDays());
            } else if (itemId == ItemId.FU_DAI_1980) {
                builder.setFuDai1980(buyInfo.getDays());
            } else if (itemId == ItemId.FU_DAI_4680) {
                builder.setFuDai4680(buyInfo.getDays());
            }
        });
        NetManager.sendMessage(OseeMsgCode.S_C_TTMY_DAILY_BUY_GIFT_INFO_RESPONSE_VALUE, builder, user);
    }

    /**
     * 获取玩家数据
     */
    @Nullable
    public static OseePlayerEntity getPlayerEntity(ServerUser user) {
        return getPlayerEntity(user, true);
    }

    /**
     * 获取玩家数据
     */
    @Nullable
    public static OseePlayerEntity getPlayerEntity(ServerUser user, Boolean init) {

        synchronized (user) {

            long userId = user.getId();

            if (userId == 0) {
                return null;
            }

            OseePlayerEntity playerEntity = user.getExpertData(OseePlayerEntity.EntityId);
            if (playerEntity == null && init) { // 如果玩家信息为空就立即初始化一下玩家游戏信息
                // if (user.getEntity() != null) {
                // UserEntity userEntity = playerMapper.findByid(user.getId());

                playerEntity = playerMapper.findByUserId(userId);

                // StackTraceElement[] stackArr = Thread.currentThread().getStackTrace();

                // if (user.getId() == 0) {
                //
                // log.info("userId0：================ ↓");
                //
                // for (int i = 0; i < stackArr.length; i++) {
                //
                // log.info("类路径：{}，方法名：{}，调用行号：{}", stackArr[i].getClassName(), stackArr[i].getMethodName(),
                // stackArr[i].getLineNumber());
                //
                // }
                //
                // log.info("userId0：================ ↑");
                //
                // }

                if (playerEntity == null) {

                    playerEntity = new OseePlayerEntity();

                    // if (user.getId() == 0) {
                    //
                    // log.info("userId1：================ ↓");
                    //
                    // for (int i = 0; i < stackArr.length; i++) {
                    //
                    // log.info("类路径：{}，方法名：{}，调用行号：{}", stackArr[i].getClassName(), stackArr[i].getMethodName(),
                    // stackArr[i].getLineNumber());
                    //
                    // }
                    //
                    // log.info("userId1：================ ↑");
                    //
                    // }

                    playerEntity.setUserId(userId);

                    playerEntity.setMoney(0);
                    playerEntity.setYuGu(0);
                    playerEntity.setSkillLock(0);
                    playerEntity.setSkillFrozen(0);
                    playerEntity.setSkillFast(0);
                    playerEntity.setFenShen(0);
                    playerEntity.setSkillEle(0);
                    playerEntity.setSkillCrit(0);
                    playerEntity.setRechargeMoney(PlayerManager.VIP_MONEY[9 - 1]); // 设置充值的金额为对应等级之上
                    playerEntity.setVipLevel(3);
                    // long[] vipMoney = PlayerManager.VIP_MONEY;
                    // playerEntity.setRechargeMoney(vipMoney[5 - 1]); // 设置充值的金额为对应等级之上
                    // PlayerManager.sendVipLevelResponse(UserContainer.getUserById(playerEntity.getUserId()));
                    // 初始炮台等级为最低等级
                    playerEntity.setBatteryLevel(1000000);
                    playerMapper.save(playerEntity);

                    // 获取：全服邮件，并发放
                    List<MessageEntity> serverMessageList = messageMapper.getServerMessageList();

                    if (CollUtil.isNotEmpty(serverMessageList)) {

                        for (MessageEntity item : serverMessageList) {

                            MessageEntity messageEntity = new MessageEntity();
                            messageEntity.setFromId(item.getId() * item.getFromId());
                            messageEntity.setToId(userId);
                            messageEntity.setTitle(item.getTitle());
                            messageEntity.setContent(item.getContent());
                            messageEntity.setItems(item.getItems());
                            messageMapper.save(messageEntity);

                        }

                    }

                }

                user.putExpertData(OseePlayerEntity.EntityId, playerEntity);

            }

            return user.getExpertData(OseePlayerEntity.EntityId);

        }

    }


    /**
     * 获取玩家金币数量
     */
    public static long getPlayerMoney(ServerUser user) {
//        return getPlayerEntity(user).getMoney();
        return getPlayerEntity(user).getDragonCrystal();
    }

    /**
     * 获取玩家金币数量
     */
    public static long getPlayerDiamond(ServerUser user) {
        return getPlayerEntity(user).getDiamond();
    }

    /**
     * 获取玩家奖券数量
     */
    public static long getPlayerLottery(ServerUser user) {
        return getPlayerEntity(user).getLottery();
    }

    /**
     * 获取玩家真实金币数量
     */
    public static long getRealPlayerMoney(ServerUser user) {
        return getItemNum(user, ItemId.MONEY) + getItemNum(user, ItemId.BANK_MONEY);
    }

    /**
     * 发送特惠礼包购买信息
     */
    public static void sendOnceBagBuyInfo(ServerUser user) {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String time = sdf.format(new Date());
        List<ShoppingEntity> list = shoppingMapper.getByContion(user.getId(), time);
        // ShoppingEntity shoppingEntity = getPlayerShopping(user.getId());
        OseeLobbyMessage.OnceBagBuyInfoResponse.Builder builder = OseeLobbyMessage.OnceBagBuyInfoResponse.newBuilder();
        list.forEach(shoppingEntity -> builder.addBuyItems(Integer.valueOf(shoppingEntity.getDailyBagInfo())));
        NetManager.sendMessage(OseeMsgCode.S_C_TTMY_ONCE_BAG_BUY_INFO_RESPONSE_VALUE, builder, user);

    }

    /**
     * 根据玩家id获取购物信息
     */
    public static ShoppingEntity getPlayerShopping(long playerId) {
        ShoppingEntity entity = shoppingMapper.getByPlayerId(playerId);

        if (entity == null) {
            entity = new ShoppingEntity();
            entity.setPlayerId(playerId);
            shoppingMapper.save(entity);
        }
        return entity;
    }

    // /**
    // * 根据玩家id获取购物信息
    // */
    // public static ShoppingEntity getPlayerShopping(long playerId,String time) {
    // ShoppingEntity entity = shoppingMapper.getByContion(playerId,time);
    //
    // if (entity == null) {
    // entity = new ShoppingEntity();
    // entity.setPlayerId(playerId);
    // shoppingMapper.save1(entity);
    // }
    // return entity;
    // }

    /**
     * 获取玩家vip等级
     */
    public static int getPlayerVipLevel(ServerUser user) {
        return getPlayerVipLevel(getPlayerEntity(user));
    }

    /**
     * 获取玩家vip等级
     */
    public static int getPlayerVipLevel(OseePlayerEntity playerEntity) {
        // for (int i = 0; i < VIP_MONEY.length; i++) {
        // if (playerEntity.getRechargeMoney() < VIP_MONEY[i]) {
        // return i;
        // }
        // }
        // return VIP_MONEY.length;

        if (playerEntity == null) {
            return 0;
        }

        return playerEntity.getVipLevel();

    }

    /**
     * 获取玩家当前等级
     */
    public static int getPlayerLevel(ServerUser user) {
        return getPlayerEntity(user).getLevel();
    }

    /**
     * 获取玩家炮台等级
     */
    public static long getPlayerBatteryLevel(ServerUser user) {
        return getPlayerEntity(user).getBatteryLevel();
    }

    /**
     * 发送玩家物品数据
     */
    public static void sendPlayerMoneyResponse(ServerUser user) {

        sendPlayerMoneyResponse(user, 0);

    }

    /**
     * 发送玩家物品数据
     */
    public static void sendPlayerMoneyResponse(ServerUser user, int num) {

        CommonLobbyManager.checkReliefMoney(user); // 发放：救济金

        PlayerMoneyResponse.Builder builder = PlayerMoneyResponse.newBuilder();
        OseePlayerEntity entity = getPlayerEntity(user);
        builder.setMoney(entity.getMoney());
        builder.setLottery(entity.getLottery());
        builder.setDiamond(entity.getDiamond());
        builder.setBankMoney(entity.getBankMoney());
        builder.setDragonCrystal(entity.getDragonCrystal());
        builder.setIsFirstJoin(0);
        NetManager.sendMessage(OseeMsgCode.S_C_OSEE_PLAYER_MONEY_RESPONSE_VALUE, builder.build(), user);

    }

    /**
     * 发送玩家物品数据
     */
    public static void sendPlayerMoneyResponse1(ServerUser user) {

        CommonLobbyManager.checkReliefMoney(user); // 发放：救济金

        PlayerMoneyResponse.Builder builder = PlayerMoneyResponse.newBuilder();
        OseePlayerEntity entity = getPlayerEntity(user);
        builder.setMoney(entity.getMoney());
        builder.setLottery(entity.getLottery());
        builder.setDiamond(entity.getDiamond());
        builder.setBankMoney(entity.getBankMoney());
        builder.setDragonCrystal(entity.getDragonCrystal());
        builder.setIsFirstJoin(1);
        NetManager.sendMessage(OseeMsgCode.S_C_OSEE_PLAYER_MONEY_RESPONSE_VALUE, builder.build(), user);

    }

    /**
     * 发送玩家道具信息响应
     */
    public void sendPlayerPropResponse(ServerUser user) {

        OseePublicData.PlayerPropResponse.Builder builder = OseePublicData.PlayerPropResponse.newBuilder();

        OseePlayerEntity playerEntity = getPlayerEntity(user);

        if (playerEntity == null) {
            return;
        }

        builder.setBronzeTorpedo(playerEntity.getBronzeTorpedo());
        builder.setSilverTorpedo(playerEntity.getSilverTorpedo());
        builder.setGoldTorpedo(playerEntity.getGoldTorpedo());
        builder.setGoldTorpedoBang(playerEntity.getGoldTorpedoBang());
        builder.setSkillLock(playerEntity.getSkillLock());
        builder.setSkillFrozen(playerEntity.getSkillFrozen());
        builder.setSkillFast(playerEntity.getSkillFast());
        builder.setSkillDouble(playerEntity.getSkillDouble());
        builder.setSkillCrit(playerEntity.getSkillCrit());

        // 月卡结束日期的时间戳
        long epochMilli =
                playerEntity.getMonthCardExpireDate().atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli();
        builder.setMonthCardOverDate(epochMilli);

        // BOSS号角
        builder.setBossBugle(playerEntity.getBossBugle());

        // 分身炮道具
        builder.setFenShen(getItemNum(user, ItemId.FEN_SHEN));
        builder.setDianchipao(getItemNum(user, ItemId.SKILL_ELETIC));
        builder.setRareTorpedo(getItemNum(user, ItemId.RARE_TORPEDO));
        builder.setRareTorpedoBang(getItemNum(user, ItemId.RARE_TORPEDO_BANG));
        builder.setYuGu(getItemNum(user, ItemId.YU_GU));
        builder.setHaiYaoShi(getItemNum(user, ItemId.HAI_YAO_SHI));
        builder.setWangHunShi(getItemNum(user, ItemId.WANG_HUN_SHI));
        builder.setHaiHunShi(getItemNum(user, ItemId.HAI_HUN_SHI));
        builder.setZhenZhuShi(getItemNum(user, ItemId.ZHEN_ZHU_SHI));
        builder.setHaiShouShi(getItemNum(user, ItemId.HAI_SHOU_SHI));
        builder.setHaiMoShi(getItemNum(user, ItemId.HAI_MO_SHI));
        builder.setZhaoHunShi(getItemNum(user, ItemId.ZHAO_HUAN_SHI));
        builder.setDianCiShi(getItemNum(user, ItemId.DIAN_CI_SHI));
        builder.setHeiDongShi(getItemNum(user, ItemId.HEI_DONG_SHI));
        builder.setLingZhuShi(getItemNum(user, ItemId.LING_ZHU_SHI));
        builder.setLongGu(getItemNum(user, ItemId.LONG_GU));
        builder.setLongZhu(getItemNum(user, ItemId.LONG_ZHU));
        builder.setLongYuan(getItemNum(user, ItemId.LONG_YUAN));
        builder.setLongJi(getItemNum(user, ItemId.LONG_JI));
        builder.setHeiDongPao(getItemNum(user, ItemId.SKILL_BLACK_HOLE));
        builder.setYuLeiPao(getItemNum(user, ItemId.SKILL_TORPEDO));
        builder.setBlackBullet(getItemNum(user, ItemId.BLACK_BULLET));
        builder.setBronzeBullet(getItemNum(user, ItemId.BRONZE_BULLET));
        builder.setSilverBullet(getItemNum(user, ItemId.SILVER_BULLET));
        builder.setGoldBullet(getItemNum(user, ItemId.GOLD_BULLET));
        builder.setSkillBit(getItemNum(user, ItemId.SKILL_BIT));
        builder.setTsgy(getItemNum(user, ItemId.SKILL_TIAN_SHEN_GUAN_YU));
        builder.setUseBatteryView(String.valueOf(RedisUtil.val("USE_BATTERYVIEW:" + user.getId(), 0L)));

        final Map<Integer, Long> userProps = userPropsManager.getUserProps(user);

        Map<String, Long> res = new HashMap<>();

        userProps.forEach((k, v) -> {

            if (UserPropsManager.TIME_PROPS.contains(k)) {
                res.put("" + k, v);
            }
            if (k == ItemId.SKILL_TIAN_SHEN_GUAN_YU.getId()) { // 天神关羽排除外
                res.put("" + k, Math.abs(v)); // 取绝对值
            }

        });

        res.put("" + ItemId.MAGIC_LAMP.getId(), getItemNum(user, ItemId.MAGIC_LAMP));
        builder.setData(JSONUtil.toJsonStr(res));

        NetManager.sendMessage(OseeMsgCode.S_C_TTMY_PLAYER_PROP_RESPONSE_VALUE, builder, user);

    }

    /**
     * 发送玩家道具信息响应
     */
    public static void sendPlayerPropOneResponse(ServerUser user, ItemId itemId, long itemNum) {
        OseePublicData.PlayerPropOneResponse.Builder builder = OseePublicData.PlayerPropOneResponse.newBuilder();
        builder.setItemId(itemId.getId());
        builder.setItemNum(itemNum);
        NetManager.sendMessage(OseeMsgCode.S_C_TTMY_PLAYER_PROP_ONE_RESPONSE_VALUE, builder, user);
    }

    /**
     * 发送玩家vip等级数据
     */
    public static void sendVipLevelResponse(ServerUser user) {
        VipLevelResponse.Builder builder = VipLevelResponse.newBuilder();
        // builder.setVipLevel(getPlayerVipLevel(user));
        builder.setVipLevel(getPlayerEntity(user).getVipLevel());
        OseePlayerEntity entity = getPlayerEntity(user);
        builder.setTotalMoney(entity.getRechargeMoney());
        if (builder.getVipLevel() < VIP_MONEY.length) {
            builder.setNextLevel(VIP_MONEY[builder.getVipLevel()] - entity.getRechargeMoney());
        }
        NetManager.sendMessage(OseeMsgCode.S_C_OSEE_VIP_LEVEL_RESPONSE_VALUE, builder.build(), user);
    }

    /**
     * 发送玩家当前拥有的最高炮台等级响应
     */
    public static void sendPlayerBatteryLevelResponse(ServerUser user) {
        OseeLobbyMessage.PlayerBatteryLevelResponse.Builder builder =
                OseeLobbyMessage.PlayerBatteryLevelResponse.newBuilder();
        builder.setLevel((int) getPlayerBatteryLevel(user));
        NetManager.sendMessage(OseeMsgCode.S_C_TTMY_PLAYER_BATTERY_LEVEL_RESPONSE_VALUE, builder, user);
    }

    /**
     * 向所有已登录在线客户端发送消息
     */
    public static void sendMessageToOnline(int msgCode, Message msg) {

        List<ServerUser> serverUsers = UserContainer.getActiveServerUsers();

        for (ServerUser user : serverUsers) {

            if (user.isOnline()) {
                NetManager.sendMessage(msgCode, msg, user);
            }

        }

    }

    /**
     * 发送金币卡购买信息
     */
    public static void sendMoneyCardBuyInfo(ServerUser user) {

        ShoppingEntity shoppingEntity = getPlayerShopping(user.getId());

        OseeLobbyMessage.MoneyCardBuyInfoResponse.Builder builder =
                OseeLobbyMessage.MoneyCardBuyInfoResponse.newBuilder();

        builder.setBought(shoppingEntity.isMoneyCard());
        builder.setReceived(DateUtils.isSameDay(shoppingEntity.getLastReceive(), new Date()));

        NetManager.sendMessage(OseeMsgCode.S_C_TTMY_MONEY_CARD_BUY_INFO_RESPONSE_VALUE, builder, user);

    }

    /**
     * 更新玩家购物信息
     */
    public static void updatePlayerShopping(ShoppingEntity shoppingEntity) {
        shoppingMapper.update(shoppingEntity);
    }

    /**
     * 更新玩家特惠礼包信息
     */
    public static void updatePlayerShopping1(ShoppingEntity shoppingEntity) {
        shoppingMapper.update1(shoppingEntity);
    }

    /**
     * 发送玩家等级响应
     */
    public static void sendPlayerLevelResponse(ServerUser user) {

        if (user.getId() == 0) {
            return;
        }

        OseePlayerEntity entity = PlayerManager.getPlayerEntity(user);
        OseeLobbyMessage.PlayerLevelResponse.Builder builder = OseeLobbyMessage.PlayerLevelResponse.newBuilder();
        builder.setLevel(entity.getLevel());

        if (user.getEntity().getPhonenum() != null) {
            builder.setPhone(user.getEntity().getPhonenum());
        }

        PlayerLevelConfig levelConfig = DataContainer.getData(entity.getLevel(), PlayerLevelConfig.class);

        if (levelConfig != null) {
            builder.setNextExperience(levelConfig.getExp());
            builder.setNowExperience(entity.getExperience());
        }

        NetManager.sendMessage(OseeMsgCode.S_C_TTMY_PLAYER_LEVEL_RESPONSE_VALUE, builder, user);

    }

    private static final Object UPDATE_ENTITIES_LOCK = new Object(); // 锁名

    @Resource
    FishingChallengeManager fishingChallengeManager;

    /**
     * 更新服务器所有玩家信息到数据库
     */
    @Scheduled(fixedDelay = 8000L)
    private void updatePlayers() {

        Set<OseePlayerEntity> updateEntitiesTemp;

        synchronized (UPDATE_ENTITIES_LOCK) {

            updateEntitiesTemp = PlayerManager.updateEntities;
            PlayerManager.updateEntities = new CopyOnWriteArraySet<>();

        }

        // log.info("updateEntitiesTempSize：{}", updateEntitiesTemp.size());

        for (OseePlayerEntity entity : updateEntitiesTemp) {

            try {

                playerMapper.update(entity);

                for (IEntityUpdater updater : entityUpdaters) {
                    updater.entityUpdate(entity);
                }

            } catch (Exception e) {

                logger.error("更新玩家[{}]数据出错[{}]", entity.getId(), e.getMessage());
                e.printStackTrace();

            }

        }

    }

    /**
     * 重写类销毁时调用的方法
     */
    @Override
    public void destroy() throws Exception {

        logger.info("服务器关闭，保存用户数据");
        updatePlayers();
        userPropsManager.batchSaveUserProps();

        fishingChallengeManager.updateFireInfoToRedis(); // 保存：子弹消耗
        fishingChallengeManager.insertFishingKillLogDOToDoris(); // 保存：击杀鱼记录

    }

    /**
     * 获取玩家拼十挑战剩余次数
     */
    public static long getPlayerTenChallengeTimes(ServerUser user) {
        OseePlayerEntity playerEntity = getPlayerEntity(user);
        return playerEntity.getTenChallengeTimes();
    }

    /**
     * 增加玩家拼十挑战次数
     */
    public static boolean addPlayerTenChallengeTimes(ServerUser user, long times) {
        OseePlayerEntity playerEntity = getPlayerEntity(user);
        synchronized (playerEntity) {
            long tenChallengeTimes = playerEntity.getTenChallengeTimes();
            tenChallengeTimes += times;
            if (tenChallengeTimes < 0) {
                return false;
            }
            // 更新挑战次数
            playerEntity.setTenChallengeTimes(tenChallengeTimes);
            playerMapper.update(playerEntity);
            return true;
        }
    }

    /**
     * 获取支付方式
     */
    public void getPayWay(ServerUser user) {
        long userId = user.getId();
        AgentEntity agentEntity = agentMapper.getByPlayerId(userId);
        if (agentEntity == null) {
            sendGetPayWaySuccessResponse(user, -1, false);
            return;
        }
        if (agentEntity.getAgentLevel() == 2) {
            Long firAgentPlayerId = agentEntity.getAgentPlayerId();
            // 一级代理
            AgentEntity firAgent = agentMapper.getByPlayerId(firAgentPlayerId);
            if (firAgent == null) {
                sendGetPayWaySuccessResponse(user, -1, false);
                return;
            }
            sendGetPayWaySuccessResponse(user, firAgent.getPayWay(), true);
            return;
        } else if ((agentEntity.getAgentLevel() == 3)) {
            Long secAgentPlayerId = agentEntity.getAgentPlayerId();
            // 线下二级代理 线上一级代理
            AgentEntity secAgent = agentMapper.getByPlayerId(secAgentPlayerId);
            if (secAgent == null) {
                sendGetPayWaySuccessResponse(user, -1, false);
                return;
            }
            ServerUser user1 = UserContainer.getUserById(secAgent.getPlayerId());
            if (PlayerManager.getPlayerEntity(user1).getPlayerType() + 1 == 3) {
                sendGetPayWaySuccessResponse(user, secAgent.getPayWay(), true);
                return;
            } else {
                Long firAgentPlayerId = secAgent.getAgentPlayerId();
                // 线下一级代理
                AgentEntity firAgent = agentMapper.getByPlayerId(firAgentPlayerId);
                if (firAgent == null) {
                    sendGetPayWaySuccessResponse(user, -1, false);
                    return;
                }
                sendGetPayWaySuccessResponse(user, firAgent.getPayWay(), true);
                return;
            }

        } else {
            sendGetPayWaySuccessResponse(user, agentEntity.getPayWay(), true);
        }
    }

    /**
     * 发送获取支付方式
     */
    private void sendGetPayWaySuccessResponse(ServerUser user, int payWay, boolean status) {
        OseePublicData.GetPayWayResponse.Builder builder = OseePublicData.GetPayWayResponse.newBuilder();
        builder.setPayWay(payWay);
        builder.setState(status);
        // 发送获取支付方式
        NetManager.sendMessage(OseeMsgCode.S_C_HW_GET_PAY_WAY_RESPONSE_VALUE, builder, user);
    }
}
