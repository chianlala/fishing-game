package com.maple.game.osee.util;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.maple.common.lobby.proto.LobbyMessage;
import com.maple.database.config.redis.RedisHelper;
import com.maple.engine.container.DataContainer;
import com.maple.engine.container.UserContainer;
import com.maple.engine.data.ServerUser;
import com.maple.engine.utils.ThreadPoolUtils;
import com.maple.game.osee.common.RedisUtil;
import com.maple.game.osee.dao.data.entity.OseePlayerEntity;
import com.maple.game.osee.entity.ItemChangeReason;
import com.maple.game.osee.entity.ItemId;
import com.maple.game.osee.entity.fishing.FishingGamePlayer;
import com.maple.game.osee.entity.fishing.NewBaseFishingRoom;
import com.maple.game.osee.entity.fishing.csv.file.FishCcxxConfig;
import com.maple.game.osee.entity.fishing.csv.file.FishConfig;
import com.maple.game.osee.entity.fishing.game.FishStruct;
import com.maple.game.osee.manager.PlayerManager;
import com.maple.game.osee.manager.UserPropsManager;
import com.maple.game.osee.manager.fishing.FishingChallengeManager;
import com.maple.game.osee.manager.lobby.ShoppingManager;
import com.maple.game.osee.proto.OseeMessage;
import com.maple.game.osee.proto.fishing.TtmyFishingChallengeMessage;
import com.maple.game.osee.timer.AutoWanderSubtitle;
import com.maple.network.manager.NetManager;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.Nullable;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.maple.game.osee.manager.fishing.FishingManager.*;

@Component
@Slf4j
public class FishingHelper {

    private static RedissonClient redissonClient;

    private static UserPropsManager userPropsManager;

    public FishingHelper(RedissonClient redissonClient, UserPropsManager userPropsManager) {

        FishingHelper.redissonClient = redissonClient;

        FishingHelper.userPropsManager = userPropsManager;

    }

    /**
     * 处理：收益比，基础的集合
     */
    public static void handlerProfitRatioBaseList(List<Long> produceGoldTotalList, List<Long> usedXhTotalList,
                                                  List<BigDecimal> profitRatioHopeList, List<BigDecimal> profitRatioRealList, int roomIndex) {

        long produceGold = redissonClient.getAtomicLong("ALL_PRODUCE_GOLD_TOTAL_STR:" + roomIndex).get();

        produceGoldTotalList.add(produceGold);

        long usedXh = redissonClient.getAtomicLong("ALL_USED_XH_TOTAL_STR:" + roomIndex).get();

        usedXhTotalList.add(usedXh);

        profitRatioHopeList.add(FishingTUtil.getProfitRatioHope(roomIndex));

        if (usedXh == 0) {

            profitRatioRealList.add(BigDecimal.ZERO);

        } else {

            profitRatioRealList.add(NumberUtil.div(Long.valueOf(produceGold), Long.valueOf(usedXh), 4));

        }

    }

    /**
     * 处理：收益比，基础的集合
     */
    public static void handlerProfitRatioBaseDynamicList(List<Long> produceGoldTotalDynamicList,
                                                         List<Long> usedXhTotalDynamicList, List<BigDecimal> profitRatioDynamicRealList, int roomIndex) {

        long produceGold = redissonClient.getAtomicLong("ALL_PRODUCE_GOLD_TOTAL_DYNAMIC_STR:" + roomIndex).get();

        produceGoldTotalDynamicList.add(produceGold);

        long usedXh = redissonClient.getAtomicLong("ALL_USED_XH_TOTAL_DYNAMIC_STR:" + roomIndex).get();

        usedXhTotalDynamicList.add(usedXh);

        if (usedXh == 0) {

            profitRatioDynamicRealList.add(BigDecimal.ZERO);

        } else {

            profitRatioDynamicRealList.add(NumberUtil.div(Long.valueOf(produceGold), Long.valueOf(usedXh), 4));

        }

    }

    /**
     * 获取：实际收益比，固定倍数
     */
    public static BigDecimal getProfitRatio(int roomIndex) {

        long produceGoldTotal = redissonClient.getAtomicLong("ALL_PRODUCE_GOLD_TOTAL_STR:" + roomIndex).get();

        long usedXhTotal = redissonClient.getAtomicLong("ALL_USED_XH_TOTAL_STR:" + roomIndex).get();

        if (usedXhTotal != 0) {
            return BigDecimal.valueOf(NumberUtil.div(produceGoldTotal, usedXhTotal, 4));
        } else {
            return BigDecimal.ZERO;
        }

    }

    /**
     * 破产处理
     */
    public static void bankruptcyHandler(FishingGamePlayer player, boolean addXh, String msg) {
        long money = player.getMoney();
        player.addMoney(-money); // 扣除：现在所有的钱
        if (money == 0) { // 如果：原本钱就是 0，则进行提示
            NetManager.sendErrorMessageToClient(msg, player.getUser());
        } else {
            if (addXh) { // 增加到 xh里面
                long xh = RedisUtil.val("ALL_XH_1-50_CHALLANGE" + player.getUser().getId(), 0d).longValue();
                RedisHelper.set("ALL_XH_1-50_CHALLANGE" + player.getUser().getId(), String.valueOf(xh + money));
            }
        }
    }

    /**
     * 游走字幕
     */
    public static void wanderSubtitle(NewBaseFishingRoom room, FishingGamePlayer player, Long winMoney, long mult,
                                      long fishConfigId, int index, long delay, long userId) {

        FishCcxxConfig fishCcxxConfig = MyRefreshFishingUtil.FISHING_CCXX_CONFIG_MAP.get(room.getRoomIndex());

        if (fishCcxxConfig == null) {
            return;
        }

        if (mult < fishCcxxConfig.getWanderSubtitleMinWinMultiple()) {
            return;
        }

        long batteryLevel = winMoney / mult; // 炮倍

        // log.info("炮倍：{}，赢的钱：{}，鱼的倍数：{}", batteryLevel, winMoney, mult);

        batteryLevel = (batteryLevel / 1000) * 1000;

        if (batteryLevel < fishCcxxConfig.getWanderSubtitleMinBatteryLevel()) {
            return;
        }

        ServerUser serverUser = UserContainer.getUserById(userId);

        if (serverUser == null) {

            return;

        }

        FishConfig fishConfig = DataContainer.getData(fishConfigId, FishConfig.class);

        if (fishConfig == null) {
            return;
        }

        long finalBatteryLevel = batteryLevel;

        ThreadPoolUtils.TASK_SERVICE_POOL.schedule(() -> {

            String dateStr = new DateTime().toString();

            String nickname = getNickname(serverUser.getNickname());

            String sceneName = MyRefreshFishingUtil.getSceneName(room.getRoomIndex());

            if (StrUtil.isBlank(sceneName)) {
                sceneName = "捕鱼房间";
            }

            String fishName = fishConfig.getName();

            String actionName = "捕获";

            long winBullet = winMoney / ShoppingManager.GOLD_TORPEDO_TO_DRAGON_CRYSTAL;

            String decimalFormat = NumberUtil.decimalFormat(",###", winMoney);

            String text = StrUtil.format(AutoWanderSubtitle.TEMPLATES[index], dateStr, nickname, sceneName,
                    finalBatteryLevel, actionName, fishName, decimalFormat, winBullet);

            LobbyMessage.WanderSubtitleResponse.Builder builder = LobbyMessage.WanderSubtitleResponse.newBuilder();

            builder.setLevel(1);
            builder.setContent(text);

            builder.setDate(dateStr);
            builder.setNickname(nickname);
            builder.setSceneName(sceneName);
            builder.setBatteryLevel(String.valueOf(finalBatteryLevel));
            builder.setFishName(fishName);
            builder.setWinMoney(winMoney);
            builder.setWinBullet(winBullet);

            // 给全部在线玩家推送游走字幕消息
            LobbyMessage.WanderSubtitleResponse msg = builder.build();

            List<ServerUser> serverUsers = UserContainer.getActiveServerUsers();

            String formatStr = StrUtil.format(AutoWanderSubtitle.TEMPLATES[index], dateStr, serverUser.getNickname(),
                    sceneName, finalBatteryLevel, actionName, fishName, decimalFormat, winBullet);

            for (ServerUser user : serverUsers) {

                if (user.isOnline()) {

                    if (user.getId() == player.getId()) {

                        builder.setContent(formatStr);

                        builder.setNickname(serverUser.getNickname());

                        NetManager.sendMessage(LobbyMessage.LobbyMsgCode.S_C_WANDER_SUBTITLE_RESPONSE_VALUE,
                                builder.build(), user);

                    } else {

                        NetManager.sendMessage(LobbyMessage.LobbyMsgCode.S_C_WANDER_SUBTITLE_RESPONSE_VALUE, msg, user);

                    }

                }

            }

        }, delay, TimeUnit.SECONDS);

    }

    /**
     * 通报
     */
    public static void notification(NewBaseFishingRoom room, FishingGamePlayer player, Long winMoney, long mult,
                                    long fishConfigId, long delay, long userId) {

        FishConfig fishConfig = DataContainer.getData(fishConfigId, FishConfig.class);

        if (fishConfig == null) {
            return;
        }

        ServerUser serverUser = UserContainer.getUserById(userId);

        if (serverUser == null) {
            return;
        }

        FishCcxxConfig fishCcxxConfig = MyRefreshFishingUtil.FISHING_CCXX_CONFIG_MAP.get(room.getRoomIndex());

        if (fishCcxxConfig == null) {
            return;
        }

        if (mult < fishCcxxConfig.getNotificationMinWinMultiple()) {
            return;
        }

        long batteryLevel = winMoney / mult; // 炮倍

        if (batteryLevel < fishCcxxConfig.getNotificationMinBatteryLevel()) {
            return;
        }

        if (fishConfig.getFishType2() == 4) {

            Long finalWinMoney = winMoney;

            ThreadPoolUtils.TASK_SERVICE_POOL.schedule(() -> {

                TtmyFishingChallengeMessage.FishingChallengeCatchBossFishResponse.Builder builder =
                        TtmyFishingChallengeMessage.FishingChallengeCatchBossFishResponse.newBuilder();

                builder.setFishConfigId(fishConfigId);
                builder.setMoney(finalWinMoney);
                builder.setPlayerName(FishingHelper.getNickname(serverUser.getNickname()));
                builder.setPlayerVipLevel(player.getVipLevel());
                builder.setBatteryLevel((int) batteryLevel);

                // 给全部在线玩家推送消息
                TtmyFishingChallengeMessage.FishingChallengeCatchBossFishResponse msg = builder.build();

                List<ServerUser> serverUsers = UserContainer.getActiveServerUsers();

                for (ServerUser user : serverUsers) {

                    if (user.isOnline()) {

                        if (user.getId() == player.getId()) {

                            builder.setPlayerName(serverUser.getNickname());

                            NetManager.sendMessage(
                                    OseeMessage.OseeMsgCode.S_C_TTMY_FISHING_CHALLENGE_CATCH_BOSS_FISH_RESPONSE_VALUE,
                                    builder.build(), user);

                        } else {

                            NetManager.sendMessage(
                                    OseeMessage.OseeMsgCode.S_C_TTMY_FISHING_CHALLENGE_CATCH_BOSS_FISH_RESPONSE_VALUE, msg,
                                    user);

                        }

                    }

                }

            }, delay, TimeUnit.SECONDS);

        }

    }

    /**
     * 获取昵称
     */
    public static String getNickname(String nickname) {

        return nickname.substring(0, 2) + "***";

    }

    public interface UseSkillBuilderHelper {

        void setId(long userId, int skillId);

        void setDuration(int duration);

        void addFishIds(long fishId);

        void addRemainDurations(int remainDuration);

        void setSkillFishId(long routeId);

        void setRestMoney(long restMoney);

        void send(NewBaseFishingRoom room);

        void setNum1(int num1);

    }

    /**
     * 玩家使用技能
     */
    public static void useSkill(NewBaseFishingRoom room, FishingGamePlayer player, int skillId, long routeId,
                                @Nullable List<Long> fishIdList, UseSkillBuilderHelper useSkillBuilderHelper) {

        ServerUser user = player.getUser();

        FishCcxxConfig fishCcxxConfig = MyRefreshFishingUtil.FISHING_CCXX_CONFIG_MAP.get(room.getRoomIndex());

        if (fishCcxxConfig == null) {
            NetManager.sendHintBoxMessageToClient("配置不存在", user, 10);
            return;
        }

        List<Integer> skillIdList = fishCcxxConfig.getSkillIdList(); // 获取：可以使用的技能集合

        boolean closeFlag = skillId < 0; // 如果小于 0，则表示关闭

        if (closeFlag) {
            skillId = -skillId; // 转化为正数
        }

        long nowTime = System.currentTimeMillis();

        int index = skillIdList.indexOf(skillId);

        // 技能id有误
        if (index == -1) {
            NetManager.sendHintBoxMessageToClient("禁止使用该道具", user, 10);
            return;
        }

        ItemId itemId = ItemId.getItemIdById(skillId);

        if (itemId == null) {
            NetManager.sendHintBoxMessageToClient("道具不存在", user, 10);
            return;
        }

        boolean payFlag = getPayFlag(fishCcxxConfig, index);

        // 是否需要检查道具数量
        boolean checkItemNumberFlag = payFlag;

        // 技能数量不足
        if (checkItemNumberFlag) {

            if (BooleanUtil.isFalse(PlayerManager.checkItem(user, skillId, 1))) {
                NetManager.sendHintBoxMessageToClient("道具数量不足", user, 10);
                return;
            }

        }

        String changingReasonStr;

        if (payFlag) {

            changingReasonStr = null;

        } else {

            changingReasonStr = itemId.getInfo() + "：" + (closeFlag ? "关闭" : "开启");

        }

        OseePlayerEntity playerEntity = PlayerManager.getPlayerEntity(user);


        useSkillBuilderHelper.setId(player.getId(), skillId);

        long maxLong = Long.MAX_VALUE;

        if (skillId == ItemId.SKILL_AUTO_FIRE.getId()) { // 自动开炮

            if (closeFlag) {
                useSkillBuilderHelper.setDuration(0);
                player.setLastAutoFireTime(0);
            } else {
                useSkillBuilderHelper.setDuration(Integer.MAX_VALUE);
                player.setLastAutoFireTime(maxLong);
            }

        } else if (skillId == ItemId.SKILL_LEI_MING_PO.getId()) { // 雷鸣破

            if (!closeFlag && payFlag) {

                Long userPropsNum = userPropsManager.getUserProopsNum(user, itemId.getId());

                if (userPropsNum == null || userPropsNum <= 0 || userPropsNum <= System.currentTimeMillis()) {

                    NetManager.sendHintBoxMessageToClient("无法使用，雷鸣破已到期", user, 10);

                    closeFlag = true;

                }

            }

            if (closeFlag) {

                useSkillBuilderHelper.setDuration(0);
                player.setLastLeiMingPoTime(0);

                FishingChallengeManager.getUserLeiMingPoUseFlag().put(player.getId(), false);

            } else {

                useSkillBuilderHelper.setDuration(Integer.MAX_VALUE);
                player.setLastLeiMingPoTime(maxLong);

                FishingChallengeManager.getUserLeiMingPoUseFlag().put(player.getId(), true);

            }
        } else if (skillId == ItemId.SKILL_LEI_SHEN_BIAN.getId()) { // 雷神变

            if (!closeFlag && payFlag) {

                Long userPropsNum = userPropsManager.getUserProopsNum(user, itemId.getId());

                if (userPropsNum == null || userPropsNum <= 0 || userPropsNum <= System.currentTimeMillis()) {

                    NetManager.sendHintBoxMessageToClient("无法使用，雷神变已到期", user, 10);

                    closeFlag = true;

                }

            }

            if (closeFlag) {

                useSkillBuilderHelper.setDuration(0);
                player.setLastLeiShenBianTime(0);
                useSkillBuilderHelper.setNum1(1);

                FishingChallengeManager.getUserLeiShenBianUseFlag().put(player.getId(), false);

                RedisHelper.set("USER_CRIT_MULT" + user.getId(), String.valueOf(1));

            } else {

                useSkillBuilderHelper.setDuration(Integer.MAX_VALUE);
                player.setLastLeiShenBianTime(maxLong);

                FishingChallengeManager.getUserLeiShenBianUseFlag().put(player.getId(), true);

                int mult = 2;

                useSkillBuilderHelper.setNum1(mult);

                RedisHelper.set("USER_CRIT_MULT" + user.getId(), String.valueOf(mult));

            }

        } else if (skillId == ItemId.SKILL_LOCK.getId()) { // 锁定

            int duration = getDuration(closeFlag, payFlag, SKILL_LOCK_TIME);

            useSkillBuilderHelper.setDuration(duration);

            if (closeFlag) {

                player.setLastLockTime(0);

            } else {

                player.setLastLockTime(nowTime);

            }

        } else if (skillId == ItemId.SKILL_ELETIC.getId()) { // 电磁炮

            int duration = getDuration(closeFlag, payFlag, SKILL_ELETIC_TIME);

            useSkillBuilderHelper.setDuration(duration);

            if (closeFlag) {

                player.setLastElectromagneticTime(0);

            } else {

                player.setLastElectromagneticTime(nowTime);

            }

        } else if (skillId == ItemId.SKILL_BLACK_HOLE.getId()) { // 黑洞炮

            // int vipLevel = PlayerManager.getPlayerVipLevel(user);
            // if (vipLevel < 5) {
            // NetManager.sendHintMessageToClient("VIP才可以使用黑洞炮技能", user);
            // return;
            // }
            // if (nowTime - player.getLastBlackHoleTime() < SKILL_BLACK_HOLE_TIME) {
            // NetManager.sendHintBoxMessageToClient("技能冷却中", user, 10);
            // return;
            // }

            useSkillBuilderHelper.setDuration((int) (SKILL_BLACK_HOLE_TIME / 1000));
            player.setLastBlackHoleTime(nowTime);

        } else if (skillId == ItemId.SKILL_TORPEDO.getId()) { // 鱼雷炮

            // int vipLevel = PlayerManager.getPlayerVipLevel(user);
            // if (vipLevel < 4) {
            // NetManager.sendHintMessageToClient("VIP才可以使用鱼雷炮技能", user);
            // return;
            // }
            // if (nowTime - player.getLastTorpedoTime() < SKILL_TORPEDO_TIME) {
            // NetManager.sendHintBoxMessageToClient("技能冷却中", user, 10);
            // return;
            // }

            useSkillBuilderHelper.setDuration((int) (SKILL_TORPEDO_TIME / 1000));
            player.setLastTorpedoTime(nowTime);

        } else if (skillId == ItemId.SKILL_BIT.getId()) { // 钻头

            // int vipLevel = PlayerManager.getPlayerVipLevel(user);
            // if (vipLevel < 3) {
            // NetManager.sendHintMessageToClient("VIP才可以使用钻头技能", user);
            // return;
            // }
            // if (nowTime - player.getLastBitTime() < SKILL_BIT_TIME) {
            // NetManager.sendHintBoxMessageToClient("技能冷却中", user, 10);
            // return;
            // }

            useSkillBuilderHelper.setDuration((int) (SKILL_BIT_TIME / 1000));
            player.setLastBitTime(nowTime);

        } else if (skillId == ItemId.SKILL_FROZEN.getId()) { // 冰冻

            if (closeFlag) {
                player.setLastFrozenTime(0);
                return;
            }

            // int vipLevel = PlayerManager.getPlayerVipLevel(user);
            // if (vipLevel < 4) {
            // NetManager.sendHintMessageToClient("VIP4才可以使用该技能", user);
            // return;
            // }

            // if (nowTime - player.getLastFrozenTime() < 2000) { // 新冰冻：技能冷却时间 2秒
            // NetManager.sendHintBoxMessageToClient("技能冷却中", user, 10);
            // return;
            // }

            int frozenType = fishCcxxConfig.getFrozenType(); // 0 全屏（默认） 1 随机

            // log.info("frozenType：{}", frozenType);

            useSkillBuilderHelper.setDuration((int) (SKILL_FROZEN_TIME / 1000));

            long frozenAddTime = getFrozenAddTime(room.getLastRoomFrozenTime(), nowTime);

            if (CollUtil.isNotEmpty(fishIdList)) { // 如果：客户端传递了 fishIdList

                Set<Long> fishIdSet = new HashSet<>(fishIdList);

                for (FishStruct fish : room.getFishMap().values()) {

                    if (fishIdSet.contains(fish.getId()) && fish.isAddSurvivalTimeFlag()) {

                        long frozenAddTimeTemp = getFrozenAddTime(fish.getLastFishFrozenTime(), nowTime);

                        // 增加：鱼的存活时间
                        addFishLifeTime(frozenAddTimeTemp, fish);

                        fish.setLastFishFrozenTime(nowTime); // 新冰冻

                        useSkillBuilderHelper.addFishIds(fish.getId());
                        useSkillBuilderHelper.addRemainDurations((int) (SKILL_FROZEN_TIME));

                    }

                }

            } else if (frozenType == 1) {

                List<Long> ruleIdList = room.getFishMap().values().stream().map(FishStruct::getRuleId).distinct()
                        .collect(Collectors.toList());

                List<Long> frozenRuleIdList; // 新冰冻：本次需要被冻住的规则
                long origin = 6;
                long bound = 12;

                if (ruleIdList.size() > origin) {

                    long nextLong = ThreadLocalRandom.current().nextLong(origin + 1, bound + 1);
                    Collections.shuffle(ruleIdList); // 打乱顺序
                    frozenRuleIdList = ruleIdList.stream().limit(nextLong).collect(Collectors.toList());

                } else {

                    frozenRuleIdList = ruleIdList;

                }

                for (FishStruct fish : room.getFishMap().values()) {

                    if (frozenRuleIdList.contains(fish.getRuleId()) && fish.isAddSurvivalTimeFlag()) {

                        long frozenAddTimeTemp = getFrozenAddTime(fish.getLastFishFrozenTime(), nowTime);

                        // 增加：鱼的存活时间
                        addFishLifeTime(frozenAddTimeTemp, fish);

                        fish.setLastFishFrozenTime(nowTime); // 新冰冻

                        useSkillBuilderHelper.addFishIds(fish.getId());
                        useSkillBuilderHelper.addRemainDurations((int) (SKILL_FROZEN_TIME));

                    }

                }

            } else {

                synchronized (room.getFishMap()) {

                    for (FishStruct fish : room.getFishMap().values()) {

                        if (fish.isAddSurvivalTimeFlag()) {

                            // 增加：鱼的存活时间
                            addFishLifeTime(frozenAddTime, fish);

                            // log.info("LifeTime：{}，fishId：{}", fish.getLifeTime(), fish.getId());
                            // 增加 机械迷城  记录限制刷新规则 鱼的 时间挫
                            Long FishForbiddenTime = room.getRoomMaxTypesFishForbiddenTime().get(fish);
                            if (ObjectUtils.isNotEmpty(FishForbiddenTime)) {
                                room.getRoomMaxTypesFishForbiddenTime().put(fish, (FishForbiddenTime + frozenAddTime));
                            }

                        }

                    }

                    room.setLastRoomFrozenTime(nowTime);

                }

            }

            // 延迟鱼潮刷新时间：秒
            room.setNextFishTideTime(room.getNextFishTideTime() + frozenAddTime);

            player.setLastFrozenTime(nowTime);
            room.setLastRoomFrozenTime(nowTime);

        } else if (skillId == ItemId.SKILL_FAST.getId()) { // 急速

            // if (nowTime - player.getLastFastTime() < SKILL_FAST_TIME) {
            // NetManager.sendHintBoxMessageToClient("技能冷却中", user, 10);
            // return;
            // }
            // int vipLevel = PlayerManager.getPlayerVipLevel(user);
            // if (vipLevel < 4) {
            // NetManager.sendHintMessageToClient("VIP4才可以使用该技能", user);
            // return;
            // }

            int duration = getDuration(closeFlag, payFlag, SKILL_FAST_TIME);

            useSkillBuilderHelper.setDuration(duration);

            if (closeFlag) {

                player.setLastFastTime(0);

            } else {

                player.setLastFastTime(nowTime);

            }

        } else if (skillId == ItemId.SKILL_CRIT.getId()) { // 暴击

            int duration = getDuration(closeFlag, payFlag, SKILL_CRIT_TIME);

            useSkillBuilderHelper.setDuration(duration);

            if (closeFlag) {

                player.setLastCritTime(0);
                useSkillBuilderHelper.setNum1(1);

                RedisHelper.set("USER_CRIT_MULT" + user.getId(), String.valueOf(1));

            } else {

                player.setLastCritTime(nowTime);
                useSkillBuilderHelper.setNum1(2);

                RedisHelper.set("USER_CRIT_MULT" + user.getId(), String.valueOf(2));

            }

        } else if (skillId == ItemId.FEN_SHEN.getId()) { // 分身炮道具

            // int vipLevel = PlayerManager.getPlayerVipLevel(user);
            // if (vipLevel < 8) {
            // NetManager.sendHintMessageToClient("VIP8才可以使用该技能", user);
            // return;
            // }

            // if (nowTime - player.getLastFenShenTime() < SKILL_FEN_SHEN_TIME) {
            // NetManager.sendHintBoxMessageToClient("技能冷却中", user,10);
            // return;
            // }

            int duration = getDuration(closeFlag, payFlag, SKILL_FEN_SHEN_TIME);

            useSkillBuilderHelper.setDuration(duration);

            if (closeFlag) {

                player.setLastFenShenTime(0);

            } else {

                player.setLastFenShenTime(nowTime);

            }

        } else if (skillId == ItemId.SKILL_DOUBLE.getId()) { // 翻倍炮道具

            int duration = getDuration(closeFlag, payFlag, SKILL_DOUBLE_TIME);

            useSkillBuilderHelper.setDuration(duration);

            if (closeFlag) {

                player.setLastDoubleTime(0);

            } else {

                player.setLastDoubleTime(nowTime);

            }

            // if (player instanceof FishingChallengePlayer) {
            //
            // if (closeFlag) {
            //
            // // 切换炮倍
            // FishingChallengeManager
            // .changeBatteryLevel(room, (FishingChallengePlayer)player, player.getBatteryLevel() / 2, false);
            //
            // } else {
            //
            // // 切换炮倍
            // FishingChallengeManager
            // .changeBatteryLevel(room, (FishingChallengePlayer)player, player.getBatteryLevel(), false);
            //
            // }
            //
            // }

        } else if (skillId == ItemId.MAGIC_LAMP.getId()) { // 神灯道具

            // int vipLevel = PlayerManager.getPlayerVipLevel(user);
            // if (vipLevel < 8) {
            // NetManager.sendHintMessageToClient("VIP8才可以使用该技能", user);
            // return;
            // }

            // if (nowTime - player.getLastFrozenTime() < SKILL_FROZEN_TIME) {
            // NetManager.sendHintMessageToClient("冰冻期间无法使用神灯！", user);
            // return;
            // }

            if (routeId == -1) {
                NetManager.sendHintMessageToClient("禁止使用该道具！", user);
                return;
            }

            long num = RedisUtil.val("FISHING_CHALLENGE_GAME_GOLD_FISH_NUM2" + room.getCode(), 0L);
            if (num >= 10) {
                NetManager.sendHintMessageToClient("房间黄金鱼已达上限！", user);
                return;
            }

            MyRefreshFishingHelper.magicLampRefreshFish(room, routeId);

            num += 1;
            RedisHelper.set("FISHING_CHALLENGE_GAME_GOLD_FISH_NUM2" + room.getCode(), String.valueOf(num));
            useSkillBuilderHelper.setSkillFishId(routeId);

        } else {

            return;

        }

        if (checkItemNumberFlag) {

            // 扣除使用的技能数量
            PlayerManager.addItem(user, skillId, -1, ItemChangeReason.USE_ITEM, true);

        }

        useSkillBuilderHelper.setRestMoney(player.getMoney());

        useSkillBuilderHelper.send(room);

    }

    /**
     * 获取：是否是付费道具
     */
    public static boolean getPayFlag(FishCcxxConfig fishCcxxConfig, int index) {

        Integer skillPayType = fishCcxxConfig.getSkillPayTypeList().get(index);

        if (skillPayType == null) {
            skillPayType = 1; // 默认：付费
        }

        // 是否是付费道具
        return skillPayType == 1;

    }

    /**
     * 获取：技能持续时间
     */
    public static int getDuration(boolean closeFlag, boolean payFlag, long time) {

        int duration;

        if (closeFlag) {

            duration = 0;

        } else {

            if (payFlag) {

                duration = (int) (time / 1000);

            } else {

                duration = Integer.MAX_VALUE;

            }

        }

        return duration;

    }

    /**
     * 增加：鱼的存活时间
     */
    public static void addFishLifeTime(long addLifeTime, FishStruct fish) {

        fish.setLifeTime(fish.getLifeTime() + addLifeTime); // 延长鱼的存在时间
        fish.setNowLifeTime(fish.getClientLifeTime()); // 记录鱼的存活时间
        fish.setFTime(fish.getFTime() + (addLifeTime));

    }

    /**
     * 新冰冻：获取需要增加的：冰冻时间
     */
    public static long getFrozenAddTime(long lastFrozenTime, long nowTime) {

        return getFrozenAddTime(lastFrozenTime, nowTime, SKILL_FROZEN_TIME);

    }

    /**
     * 新冰冻：获取需要增加的：冰冻时间
     */
    public static long getFrozenAddTime(long lastFrozenTime, long nowTime, long skillFrozenTime) {

        return Math.min((nowTime - lastFrozenTime), skillFrozenTime);

    }

    /**
     * 向玩家发送房间当前的冰冻消息
     */
    public static void sendFrozenMessage(NewBaseFishingRoom gameRoom, ServerUser user) {

        FishCcxxConfig fishCcxxConfig = MyRefreshFishingUtil.FISHING_CCXX_CONFIG_MAP.get(gameRoom.getRoomIndex());

        if (fishCcxxConfig == null) {
            NetManager.sendHintBoxMessageToClient("冰冻配置不存在", user, 10);
            return;
        }

        int frozenType = fishCcxxConfig.getFrozenType(); // 0 全屏（默认） 1 随机

        long nowTime = System.currentTimeMillis();

        long fTs = System.currentTimeMillis() - gameRoom.getLastRoomFrozenTime();

        if (frozenType == 1) {

            if (fTs < SKILL_FROZEN_TIME) { // 房间处于冰冻状态

                TtmyFishingChallengeMessage.FishingChallengeUseSkillResponse.Builder builder =
                        TtmyFishingChallengeMessage.FishingChallengeUseSkillResponse.newBuilder();

                builder.setSkillId(ItemId.SKILL_FROZEN.getId()); // 冰冻
                builder.setDuration((int) ((SKILL_FROZEN_TIME - fTs) / 1000));

                // 新冰冻
                gameRoom.getFishMap().values().stream()
                        .filter(it -> nowTime - it.getLastFishFrozenTime() < SKILL_FROZEN_TIME).forEach(it -> {

                            builder.addFishIds(it.getId());
                            builder.addRemainDurations((int) (SKILL_FROZEN_TIME - (nowTime - it.getLastFishFrozenTime())));

                        });

                NetManager.sendMessage(OseeMessage.OseeMsgCode.S_C_TTMY_FISHING_CHALLENGE_USE_SKILL_RESPONSE_VALUE,
                        builder, user);

            }

        } else {

            if (fTs < SKILL_FROZEN_TIME) { // 房间处于冰冻状态

                TtmyFishingChallengeMessage.FishingChallengeUseSkillResponse.Builder builder =
                        TtmyFishingChallengeMessage.FishingChallengeUseSkillResponse.newBuilder();

                builder.setSkillId(ItemId.SKILL_FROZEN.getId()); // 冰冻
                builder.setDuration((int) ((SKILL_FROZEN_TIME - fTs) / 1000));

                NetManager.sendMessage(OseeMessage.OseeMsgCode.S_C_TTMY_FISHING_CHALLENGE_USE_SKILL_RESPONSE_VALUE,
                        builder, user);

            }

        }

    }

}
