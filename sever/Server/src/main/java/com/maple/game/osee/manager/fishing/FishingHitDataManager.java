package com.maple.game.osee.manager.fishing;

import com.alibaba.fastjson.JSON;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.maple.database.config.redis.RedisHelper;
import com.maple.engine.manager.GsonManager;
import com.maple.engine.utils.ThreadPoolUtils;
import com.maple.game.osee.entity.fishing.FishingGamePlayer;
import com.maple.game.osee.entity.fishing.FishingGameRoom;
import com.maple.game.osee.manager.fishing.util.FishingUtil;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.lang.reflect.Type;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * 捕鱼命中数据管理类
 */
@Component
public class FishingHitDataManager {

    private static Logger logger = LoggerFactory.getLogger(FishingHitDataManager.class);

    private static RedissonClient redissonClient;

    public FishingHitDataManager(
            RedissonClient redissonClient) {
        FishingHitDataManager.redissonClient = redissonClient;
    }

    /**
     * 新手等级限制
     */
    public static int GREENER_LIMIT = 15;

    /**
     * 每日赢取金币表
     */
    private static Map<Long, Map<Integer, Long>> DAILY_WIN_MAP = new ConcurrentHashMap<>();

    /**
     * 总赢取金币表
     */
    public static Map<Long, Map<Integer, Long>> TOTAL_WIN_MAP = new ConcurrentHashMap<>();

    /**
     * 小黑屋玩家表
     */
    public static Map<Long, Map<Integer, Long>> BLACK_ROOM_MAP = new ConcurrentHashMap<>();

    /**
     * 玩家系数表
     */
    public static Map<Long, Double> PLAYER_FISHING_PROB_MAP = new ConcurrentHashMap<>();

    /**
     * 初始库存
     */
    public static long[][] FISHING_INIT_POOL = {{0, 0, 0, 0, 0, 0, 0, 0, 0}, {0, 0, 0, 0, 0, 0, 0, 0, 0}};

    /**
     * 捕鱼库存
     */
    public static long[][] FISHING_POOL = {{0, 0, 0, 0, 0, 0, 0, 0, 0}, {0, 0, 0, 0, 0, 0, 0, 0, 0}};

    /**
     * 全服捕鱼参数
     */
    public static double[][] FISHING_PROB =
            {{0D, 0D, 0D, 0D, 0D, 0D, 0D, 0D, 0D}, {0D, 0D, 0D, 0D, 0D, 0D, 0D, 0D, 0D}};

    /**
     * 全服捕鱼参数单位变化量
     */
    public static double[][] FISHING_UNIT_PROB = {{0.01D, 0.01D, 0.01D, 0.01D, 0.01D, 0.01D, 0.01D, 0.01D},
            {0.01D, 0.01D, 0.01D, 0.01D, 0.01D, 0.01D, 0.01D, 0.1D}};

    /**
     * 单位变化量所需库存金币
     */
    public static long[][] FISHING_PER_UNIT_MONEY = {{100L, 100L, 100L, 100L, 100L, 100L, 100L, 100L, 100L},
            {100L, 100L, 100L, 100L, 100L, 100L, 100L, 100L, 100L}};

    /**
     * 捕鱼抽水参数
     */
    public static double[][] FISHING_CUT_PROB =
            {{0D, 0D, 0D, 0D, 0D, 0D, 0D, 0D, 0D}, {0D, 0D, 0D, 0D, 0D, 0D, 0D, 0D, 0D}};

    /**
     * 玩家当日输赢影响概率(输、赢)
     */
    public static double[][][] PLAYER_DAILY_PROB =
            {{{0D, 0D}, {0D, 0D}, {0D, 0D}, {0D, 0D}, {0D, 0D}, {0D, 0D}, {0D, 0D}, {0D, 0D}, {0D, 0D}},
                    {{0D, 0D}, {0D, 0D}, {0D, 0D}, {0D, 0D}, {0D, 0D}, {0D, 0D}, {0D, 0D}, {0D, 0D}, {0D, 0D}}};

    /**
     * 玩家当日输赢限制值(输、赢)
     */
    public static long[][][] PLAYER_DAILY_LIMIT =
            {{{0L, 0L}, {0L, 0L}, {0L, 0L}, {0L, 0L}, {0L, 0L}, {0L, 0L}, {0L, 0L}, {0L, 0L}, {0L, 0L}, {0L, 0L}},
                    {{0L, 0L}, {0L, 0L}, {0L, 0L}, {0L, 0L}, {0L, 0L}, {0L, 0L}, {0L, 0L}, {0L, 0L}, {0L, 0L}, {0L, 0L}}};

    /**
     * 玩家总计输赢影响概率(输、赢)
     */
    public static double[][][] PLAYER_TOTAL_PROB =
            {{{0D, 0D}, {0D, 0D}, {0D, 0D}, {0D, 0D}, {0D, 0D}, {0D, 0D}, {0D, 0D}, {0D, 0D}, {0D, 0D}},
                    {{0D, 0D}, {0D, 0D}, {0D, 0D}, {0D, 0D}, {0D, 0D}, {0D, 0D}, {0D, 0D}, {0D, 0D}, {0D, 0D}}};

    /**
     * 玩家总计输赢限制值(输、赢)
     */
    public static long[][][] PLAYER_TOTAL_LIMIT =
            {{{0L, 0L}, {0L, 0L}, {0L, 0L}, {0L, 0L}, {0L, 0L}, {0L, 0L}, {0L, 0L}, {0L, 0L}, {0L, 0L}},
                    {{0L, 0L}, {0L, 0L}, {0L, 0L}, {0L, 0L}, {0L, 0L}, {0L, 0L}, {0L, 0L}, {0L, 0L}, {0L, 0L}}};

    /**
     * 小黑屋影响概率
     */
    public static double[][] BLACK_ROOM_PROB =
            {{0D, 0D, 0D, 0D, 0D, 0D, 0D, 0D, 0D}, {0D, 0D, 0D, 0D, 0D, 0D, 0D, 0D, 0D}};

    /**
     * 小黑屋限制值
     */
    public static long[][] BLACK_ROOM_LIMIT =
            {{0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L}, {0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L}};

    public static Type getType() {
        return new TypeToken<Map<Integer, Long>>() {
        }.getType();
    }

    /**
     * 获取新人标识
     */
    public static int getGreener(FishingGamePlayer player) {
        return player.getLevel() >= GREENER_LIMIT ? 1 : 0;
    }

    /**
     * 获取玩家日常输赢表
     */
    public static Map<Integer, Long> getPlayerDailyMap(long playerId) {
        if (!DAILY_WIN_MAP.containsKey(playerId)) {
            DAILY_WIN_MAP.put(playerId, new HashMap<>());
        }
        return DAILY_WIN_MAP.get(playerId);
    }

    /**
     * 获取玩家总输赢表
     */
    public static Map<Integer, Long> getPlayerTotalMap(long playerId) {
        if (!TOTAL_WIN_MAP.containsKey(playerId)) {
            String key = String.format("Fishing:TotalWin:%d", playerId);
            String value = RedisHelper.get(key);
            if (!StringUtils.isEmpty(value)) {
                Map<Integer, Long> blackMap = new Gson().fromJson(value, getType());
                TOTAL_WIN_MAP.put(playerId, blackMap);
            } else {
                TOTAL_WIN_MAP.put(playerId, new HashMap<>());
            }
        }
        return TOTAL_WIN_MAP.get(playerId);
    }

    /**
     * 获取玩家小黑屋表
     */
    public static Map<Integer, Long> getPlayerBlackMap(long playerId) {
        if (!BLACK_ROOM_MAP.containsKey(playerId)) {
            String key = String.format("Fishing:BlackRoom:%d", playerId);
            String value = RedisHelper.get(key);
            if (!StringUtils.isEmpty(value)) {
                Map<Integer, Long> blackMap = new Gson().fromJson(value, getType());
                BLACK_ROOM_MAP.put(playerId, blackMap);
            } else {
                BLACK_ROOM_MAP.put(playerId, new HashMap<>());
            }
        }
        return BLACK_ROOM_MAP.get(playerId);
    }

    /**
     * 获取每日输赢金币
     */
    public static long getDailyWin(long playerId, int index) {
        return getPlayerDailyMap(playerId).getOrDefault(index, 0L);
    }

    /**
     * 增加每日输赢金币
     */
    public static void addDailyWin(long playerId, int index, long addMoney) {
        Map<Integer, Long> dailyMap = getPlayerDailyMap(playerId);
        synchronized (dailyMap) {
            dailyMap.put(index, dailyMap.getOrDefault(index, 0L) + addMoney);
        }
    }

    /**
     * 获取小黑屋金币
     */
    public static long getBlackRoom(long playerId, int index) {
        return getPlayerBlackMap(playerId).getOrDefault(index, 0L);
    }

    /**
     * 获取小黑屋金币
     */
    public static void setBlackRoom(long playerId, int index, long value) {
        Map<Integer, Long> blackMap = getPlayerBlackMap(playerId);
        blackMap.put(index, value);
    }

    /**
     * 增加小黑屋金币
     */
    public static void addBlackRoom(long playerId, int index, long addMoney) {
        Map<Integer, Long> blackMap = getPlayerBlackMap(playerId);
        // synchronized (blackMap) {
        // long black = blackMap.getOrDefault(index, 0L) + addMoney;
        // black = Math.max(0L, black);
        // blackMap.put(index, black);
        // }
        blackMap.put(index, blackMap.getOrDefault(index, 0L) + addMoney);
    }

    /**
     * 获取总输赢金币
     */
    public static long getTotalWin(long playerId, int index) {
        return getPlayerTotalMap(playerId).getOrDefault(index, 0L);
    }

    /**
     * 增加总输赢金币
     */
    public static void addTotalWin(long playerId, int index, long addMoney) {
        Map<Integer, Long> totalMap = getPlayerTotalMap(playerId);
        synchronized (totalMap) {
            totalMap.put(index, totalMap.getOrDefault(index, 0L) + addMoney);
        }
    }

    /**
     * 重置总输赢金币
     */
    public static void setTotalWin(long playerId, int index, long addMoney) {
        Map<Integer, Long> totalMap = getPlayerTotalMap(playerId);
        synchronized (totalMap) {
            totalMap.put(index, addMoney);
        }
    }

    /**
     * 增加输赢金币
     */
    public static void addWin(FishingGameRoom gameRoom, FishingGamePlayer player, long addMoney) {
        ThreadPoolUtils.TASK_SERVICE_POOL.schedule(() -> {
            long money = addMoney;
            int index = gameRoom.getRoomIndex() - 1;
            // logger.info("index: " + index);

            player.setChangeMoney(player.getChangeMoney() + money);
            if (money > 0) {
                player.setSpendMoney(player.getSpendMoney() + money);
            } else {
                player.setWinMoney(player.getWinMoney() + money);
            }

            int greener = getGreener(player);
            if (money > 0) { // 发射子弹消耗
                long cutMoney = 0L;
                if (index == 4) {
                    if (greener == 1) {
                        cutMoney = (long) (money * (FISHING_CUT_PROB[greener][7] / 100D));
                    } else {
                        cutMoney = (long) (money * (FISHING_CUT_PROB[greener][5] / 100D));
                    }
                } else {
                    cutMoney = (long) (money * (FISHING_CUT_PROB[greener][index] / 100D));
                }

                money -= cutMoney;
                // 设置总抽水金币
                player.setCutMoney(player.getCutMoney() + cutMoney);
            }
            if (greener == 1) {
                if (index == 0) {
                    addDailyWin(player.getId(), 4, money); // 每日输赢
                    addTotalWin(player.getId(), 4, money); // 总输赢
                } else if (index == 1) {
                    addDailyWin(player.getId(), 5, money); // 每日输赢
                    addTotalWin(player.getId(), 5, money); // 总输赢
                } else if (index == 2) {
                    addDailyWin(player.getId(), 6, money); // 每日输赢
                    addTotalWin(player.getId(), 6, money); // 总输赢
                } else if (index == 3) {
                    addDailyWin(player.getId(), 7, money); // 每日输赢
                    addTotalWin(player.getId(), 7, money); // 总输赢
                } else {
                    addDailyWin(player.getId(), 9, money); // 每日输赢
                    addTotalWin(player.getId(), 9, money); // 总输赢
                }
            } else {
                if (index == 4) {
                    addDailyWin(player.getId(), 8, money); // 每日输赢
                    addTotalWin(player.getId(), 8, money); // 总输赢
                } else {
                    addDailyWin(player.getId(), index, money); // 每日输赢
                    addTotalWin(player.getId(), index, money); // 总输赢
                }
            }
            synchronized (FISHING_POOL) {
                // 服务器库存
                if (greener == 1) {
                    if (index == 4) {
                        index = 7;
                    }
                }
                FISHING_POOL[greener][index] += money;
            }
        }, 0, TimeUnit.SECONDS);
    }

    /**
     * 获取玩家捕鱼系数
     */
    public static double getPlayerFishingProb(long playerId) {
        if (PLAYER_FISHING_PROB_MAP.containsKey(playerId)) {
            return PLAYER_FISHING_PROB_MAP.get(playerId);
        }

        String key = String.format("Fishing:PlayerFishingProb:%d", playerId);
        String value = RedisHelper.get(key);
        if (!StringUtils.isEmpty(value)) {
            double result = Double.parseDouble(value);
            PLAYER_FISHING_PROB_MAP.put(playerId, result);
            return result;
        }

        PLAYER_FISHING_PROB_MAP.put(playerId, 0D);
        return 0;
    }

    /**
     * 设置玩家捕鱼参数
     */
    public static void setPlayerFishingProb(long playerId, double prob) {
        PLAYER_FISHING_PROB_MAP.put(playerId, prob);
    }

    /**
     * 重置全服玩家捕鱼系数
     */
    // public static void resetAllPlayerTOTALWINMAP(Long playerId,int greener) {
    // TOTAL_WIN_MAP.clear();
    // Map<Integer, Long> blackMap = new HashMap<>();
    // if(greener == 1){
    // blackMap.put(0,FishingUtil.q0[1]);
    // blackMap.put(1,FishingUtil.q0[3]);
    // blackMap.put(2,FishingUtil.q0[5]);
    // blackMap.put(3,FishingUtil.q0[7]);
    // }else{
    // blackMap.put(0,FishingUtil.q0[0]);
    // blackMap.put(1,FishingUtil.q0[2]);
    // blackMap.put(2,FishingUtil.q0[4]);
    // blackMap.put(3,FishingUtil.q0[6]);
    // }
    // TOTAL_WIN_MAP.put(playerId, blackMap);
    // RedisHelper.set(String.format("Fishing:TotalWin:%d", playerId),new Gson().toJson(blackMap));
    // System.out.println(RedisHelper.get(String.format("Fishing:TotalWin:%d", playerId)));
    // }

    /**
     * 重置全服玩家总输赢参数
     */
    public static void resetAllPlayerFishingProb() {
        PLAYER_FISHING_PROB_MAP.clear();
        // TODO: 需要实现
        // RedisHelper.removePattern("Fishing:PlayerFishingProb:*");
    }

    /**
     * 获取库存金币
     */
    public static long getPool(int greener, int roomIndex) {
        return FISHING_POOL[greener][roomIndex - 1];
    }

    /**
     * 获取服务器捕鱼系数
     */
    public static double getServerProb(int greener, int index) {
        if (FISHING_PER_UNIT_MONEY[greener][index] <= 0) {
            return Double.MIN_VALUE;
        }
        return FISHING_PROB[greener][index] + (FISHING_UNIT_PROB[greener][index]
                * ((FISHING_POOL[greener][index]) / (double) FISHING_PER_UNIT_MONEY[greener][index]));
    }

    /**
     * 初始化数据
     */
    public static void init() {

        final List<Double> tpc = JSON.parseArray(RedisHelper.get("tp_c"), Double.class);
        if (tpc != null) {
            for (int i = 0; i < tpc.size(); i++) {
                FishingUtil.tp_c[i] = tpc.get(i);
            }
        }
        final List<Double> tpz = JSON.parseArray(RedisHelper.get("tp_z"), Double.class);
        if (tpz != null) {
            for (int i = 0; i < tpz.size(); i++) {
                FishingUtil.tp_z[i] = tpz.get(i);
            }
        }
        final List<Double> tpg = JSON.parseArray(RedisHelper.get("tp_g"), Double.class);
        if (tpg != null) {
            for (int i = 0; i < tpg.size(); i++) {
                FishingUtil.tp_g[i] = tpg.get(i);
            }
        }
        final List<Double> tps = JSON.parseArray(RedisHelper.get("tp_s"), Double.class);
        if (tps != null) {
            for (int i = 0; i < tps.size(); i++) {
                FishingUtil.tp_s[i] = tps.get(i);
            }
        }

        String gProd = RedisHelper.get("gProd");
        // System.out.println("gProd*****************:" + gProd);
        if (gProd != null && gProd.length() != 0) {
            gProd = gProd.substring(gProd.lastIndexOf("[") + 1).replaceAll("]", "");
            String[] p1 = gProd.split(",");
            List<Double> listrecMax = new ArrayList<>();
            for (String p : p1) {
                // System.out.println(p);
                if (p == null || "".equals(p)) {
                    return;
                }
                listrecMax.add(Double.parseDouble(p));
            }
            listrecMax.add(0D);
            listrecMax.add(0D);
            listrecMax.add(0D);
            FishingUtil.gProd = listrecMax.toArray(new Double[0]);
        }
        String lcf = RedisHelper.get("lcf");
        // System.out.println("lcf*****************:" + lcf);
        if (lcf != null && lcf.length() != 0) {
            lcf = lcf.substring(lcf.lastIndexOf("[") + 1).replaceAll("]", "");
            String[] p1 = lcf.split(",");
            List<Double> listrecMax = new ArrayList<>();
            for (String p : p1) {
                // System.out.println(p);
                if (p == null || "".equals(p)) {
                    return;
                }
                listrecMax.add(Double.parseDouble(p));
            }
            listrecMax.add(0D);
            listrecMax.add(0D);
            listrecMax.add(0D);
            FishingUtil.lcf = listrecMax.toArray(new Double[0]);
        }
        String dz = RedisHelper.get("dz");
        // System.out.println("dz*****************:" + dz);
        if (dz != null && dz.length() != 0) {
            dz = dz.substring(dz.lastIndexOf("[") + 1).replaceAll("]", "");
            String[] p1 = dz.split(",");
            List<Double> listrecMax = new ArrayList<>();
            for (String p : p1) {
                // System.out.println(p);
                if (p == null || "".equals(p)) {
                    return;
                }
                listrecMax.add(Double.parseDouble(p));
            }
            listrecMax.add(0D);
            listrecMax.add(0D);
            listrecMax.add(0D);
            FishingUtil.dz = listrecMax.toArray(new Double[0]);
        }

        String bfsMin = RedisHelper.get("bfsMin");
        // System.out.println("bfsMin*****************:" + bfsMin);
        if (bfsMin != null && bfsMin.length() != 0) {
            bfsMin = bfsMin.substring(bfsMin.lastIndexOf("[") + 1).replaceAll("]", "");
            String[] p1 = bfsMin.split(",");
            List<Double> listrecMax = new ArrayList<>();
            for (String p : p1) {
                // System.out.println(p);
                if (p == null || "".equals(p)) {
                    return;
                }
                listrecMax.add(Double.parseDouble(p));
            }
            listrecMax.add(0D);
            listrecMax.add(0D);
            listrecMax.add(0D);
            FishingUtil.bfsMin = listrecMax.toArray(new Double[0]);
        }
        String bfsMax = RedisHelper.get("bfsMax");
        // System.out.println("bfsMax*****************:" + bfsMax);
        if (bfsMax != null && bfsMax.length() != 0) {
            bfsMax = bfsMax.substring(bfsMax.lastIndexOf("[") + 1).replaceAll("]", "");
            String[] p1 = bfsMax.split(",");
            List<Double> listrecMax = new ArrayList<>();
            for (String p : p1) {
                // System.out.println(p);
                if (p == null || "".equals(p)) {
                    return;
                }
                listrecMax.add(Double.parseDouble(p));
            }
            listrecMax.add(0D);
            listrecMax.add(0D);
            listrecMax.add(0D);
            FishingUtil.bfsMax = listrecMax.toArray(new Double[0]);
        }

        String torpedoDropFreeRate = RedisHelper.get("torpedoDropFreeRate");
        // System.out.println("torpedoDropFreeRate*****************:" + torpedoDropFreeRate);
        if (torpedoDropFreeRate != null && torpedoDropFreeRate.length() != 0) {
            torpedoDropFreeRate =
                    torpedoDropFreeRate.substring(torpedoDropFreeRate.lastIndexOf("[") + 1).replaceAll("]", "");
            String[] q1 = torpedoDropFreeRate.split(",");
            List<Double> list = new ArrayList<>();
            for (String q : q1) {
                // System.out.println(q);
                if (q == null || "".equals(q)) {
                    return;
                }
                list.add(Double.parseDouble(q));
            }
            FishingUtil.torpedoDropFreeRate = list.toArray(new Double[0]);
        }

        String torpedoDropRateGoldFreeRate = RedisHelper.get("torpedoDropRateGoldFreeRate");
        // System.out.println("torpedoDropRateGoldFreeRate*****************:" + torpedoDropRateGoldFreeRate);
        if (torpedoDropRateGoldFreeRate != null && torpedoDropRateGoldFreeRate.length() != 0) {
            torpedoDropRateGoldFreeRate = torpedoDropRateGoldFreeRate
                    .substring(torpedoDropRateGoldFreeRate.lastIndexOf("[") + 1).replaceAll("]", "");
            String[] q1 = torpedoDropRateGoldFreeRate.split(",");
            List<Double> list = new ArrayList<>();
            for (String q : q1) {
                // System.out.println(q);
                if (q == null || "".equals(q)) {
                    return;
                }
                list.add(Double.parseDouble(q));
            }
            list.add(0D);
            list.add(0D);
            list.add(0D);
            list.add(0D);
            list.add(0D);
            list.add(0D);
            list.add(0D);
            list.add(0D);
            list.add(0D);
            list.add(0D);
            list.add(0D);
            list.add(0D);
            FishingUtil.torpedoDropRateGoldFreeRate = list.toArray(new Double[0]);
        }

        String bangTorpedoDropFreeRate = RedisHelper.get("bangTorpedoDropFreeRate");
        // System.out.println("bangTorpedoDropFreeRate*****************:" + bangTorpedoDropFreeRate);
        if (bangTorpedoDropFreeRate != null && bangTorpedoDropFreeRate.length() != 0) {
            bangTorpedoDropFreeRate =
                    bangTorpedoDropFreeRate.substring(bangTorpedoDropFreeRate.lastIndexOf("[") + 1).replaceAll("]", "");
            String[] q1 = bangTorpedoDropFreeRate.split(",");
            List<Double> list = new ArrayList<>();
            for (String q : q1) {
                // System.out.println(q);
                if (q == null || "".equals(q)) {
                    return;
                }
                list.add(Double.parseDouble(q));
            }
            list.add(0D);
            list.add(0D);
            list.add(0D);
            list.add(0D);
            list.add(0D);
            list.add(0D);
            list.add(0D);
            list.add(0D);
            list.add(0D);
            list.add(0D);
            list.add(0D);
            list.add(0D);
            FishingUtil.bangTorpedoDropFreeRate = list.toArray(new Double[0]);
        }

        String bangTorpedoDropRateGoldFreeRate = RedisHelper.get("bangTorpedoDropRateGoldFreeRate");
        // System.out.println("bangTorpedoDropRateGoldFreeRate*****************:" + bangTorpedoDropRateGoldFreeRate);
        if (bangTorpedoDropRateGoldFreeRate != null && bangTorpedoDropRateGoldFreeRate.length() != 0) {
            bangTorpedoDropRateGoldFreeRate = bangTorpedoDropRateGoldFreeRate
                    .substring(bangTorpedoDropRateGoldFreeRate.lastIndexOf("[") + 1).replaceAll("]", "");
            String[] q1 = bangTorpedoDropRateGoldFreeRate.split(",");
            List<Double> list = new ArrayList<>();
            for (String q : q1) {
                // System.out.println(q);
                if (q == null || "".equals(q)) {
                    return;
                }
                list.add(Double.parseDouble(q));
            }
            list.add(0D);
            list.add(0D);
            list.add(0D);
            list.add(0D);
            list.add(0D);
            list.add(0D);
            list.add(0D);
            list.add(0D);
            list.add(0D);
            list.add(0D);
            list.add(0D);
            list.add(0D);
            FishingUtil.bangTorpedoDropRateGoldFreeRate = list.toArray(new Double[0]);
        }

        String bossBugleRate = RedisHelper.get("bossBugleRate");
        // System.out.println("bossBugleRate*****************:" + bossBugleRate);
        if (bossBugleRate != null && bossBugleRate.length() != 0) {
            bossBugleRate = bossBugleRate.substring(bossBugleRate.lastIndexOf("[") + 1).replaceAll("]", "");
            String[] q1 = bossBugleRate.split(",");
            List<Double> list = new ArrayList<>();
            for (String q : q1) {
                // System.out.println(q);
                if (q == null || "".equals(q)) {
                    return;
                }
                list.add(Double.parseDouble(q));
            }
            list.add(0D);
            list.add(0D);
            list.add(0D);
            list.add(0D);
            list.add(0D);
            list.add(0D);
            list.add(0D);
            list.add(0D);
            list.add(0D);
            list.add(0D);
            list.add(0D);
            list.add(0D);
            FishingUtil.bossBugleRate = list.toArray(new Double[0]);
        }
        String torpedoDropPerExChangeRateMin = RedisHelper.get("torpedoDropPerExChangeRateMin");
        // System.out.println("torpedoDropPerExChangeRateMin*****************:" + torpedoDropPerExChangeRateMin);
        if (torpedoDropPerExChangeRateMin != null && torpedoDropPerExChangeRateMin.length() != 0) {
            torpedoDropPerExChangeRateMin = torpedoDropPerExChangeRateMin
                    .substring(torpedoDropPerExChangeRateMin.lastIndexOf("[") + 1).replaceAll("]", "");
            String[] q1 = torpedoDropPerExChangeRateMin.split(",");
            List<Double> list = new ArrayList<>();
            for (String q : q1) {
                // System.out.println(q);
                if (q == null || "".equals(q)) {
                    return;
                }
                list.add(Double.parseDouble(q));
            }
            FishingUtil.torpedoDropPerExChangeRateMin = list.toArray(new Double[0]);
        }

        String torpedoNotBangDropPerExChangeRateMin = RedisHelper.get("torpedoNotBangDropPerExChangeRateMin");
        // System.out
        // .println("torpedoNotBangDropPerExChangeRateMin*****************:" + torpedoNotBangDropPerExChangeRateMin);
        if (torpedoNotBangDropPerExChangeRateMin != null && torpedoNotBangDropPerExChangeRateMin.length() != 0) {
            torpedoNotBangDropPerExChangeRateMin = torpedoNotBangDropPerExChangeRateMin
                    .substring(torpedoNotBangDropPerExChangeRateMin.lastIndexOf("[") + 1).replaceAll("]", "");
            String[] q1 = torpedoNotBangDropPerExChangeRateMin.split(",");
            List<Double> list = new ArrayList<>();
            for (String q : q1) {
                // System.out.println(q);
                if (q == null || "".equals(q)) {
                    return;
                }
                list.add(Double.parseDouble(q));
            }
            FishingUtil.torpedoNotBangDropPerExChangeRateMin = list.toArray(new Double[0]);
        }

        String rareTorpedoDropPerExChangeRate = RedisHelper.get("rareTorpedoDropPerExChangeRate");
        // System.out.println("rareTorpedoDropPerExChangeRate*****************:" + rareTorpedoDropPerExChangeRate);
        if (rareTorpedoDropPerExChangeRate != null && rareTorpedoDropPerExChangeRate.length() != 0) {
            rareTorpedoDropPerExChangeRate = rareTorpedoDropPerExChangeRate
                    .substring(rareTorpedoDropPerExChangeRate.lastIndexOf("[") + 1).replaceAll("]", "");
            String[] q1 = rareTorpedoDropPerExChangeRate.split(",");
            List<Double> list = new ArrayList<>();
            for (String q : q1) {
                // System.out.println(q);
                if (q == null || "".equals(q)) {
                    return;
                }
                list.add(Double.parseDouble(q));
            }
            FishingUtil.rareTorpedoDropPerExChangeRate = list.toArray(new Double[0]);
        }

        String rareTorpedoDropPerExChangeRateMin = RedisHelper.get("rareTorpedoDropPerExChangeRateMin");
        // System.out.println("rareTorpedoDropPerExChangeRateMin*****************:" +
        // rareTorpedoDropPerExChangeRateMin);
        if (rareTorpedoDropPerExChangeRateMin != null && rareTorpedoDropPerExChangeRateMin.length() != 0) {
            rareTorpedoDropPerExChangeRateMin = rareTorpedoDropPerExChangeRateMin
                    .substring(rareTorpedoDropPerExChangeRateMin.lastIndexOf("[") + 1).replaceAll("]", "");
            String[] q1 = rareTorpedoDropPerExChangeRateMin.split(",");
            List<Double> list = new ArrayList<>();
            for (String q : q1) {
                // System.out.println(q);
                if (q == null || "".equals(q)) {
                    return;
                }
                list.add(Double.parseDouble(q));
            }
            FishingUtil.rareTorpedoDropPerExChangeRateMin = list.toArray(new Double[0]);
        }

        String rareTorpedoNotBangDropPerExChangeRate = RedisHelper.get("rareTorpedoNotBangDropPerExChangeRate");
        // System.out
        // .println("rareTorpedoNotBangDropPerExChangeRate*****************:" + rareTorpedoNotBangDropPerExChangeRate);
        if (rareTorpedoNotBangDropPerExChangeRate != null && rareTorpedoNotBangDropPerExChangeRate.length() != 0) {
            rareTorpedoNotBangDropPerExChangeRate = rareTorpedoNotBangDropPerExChangeRate
                    .substring(rareTorpedoNotBangDropPerExChangeRate.lastIndexOf("[") + 1).replaceAll("]", "");
            String[] q1 = rareTorpedoNotBangDropPerExChangeRate.split(",");
            List<Double> list = new ArrayList<>();
            for (String q : q1) {
                // System.out.println(q);
                if (q == null || "".equals(q)) {
                    return;
                }
                list.add(Double.parseDouble(q));
            }
            FishingUtil.rareTorpedoNotBangDropPerExChangeRate = list.toArray(new Double[0]);
        }

        String rareTorpedoNotBangDropPerExChangeRateMin = RedisHelper.get("rareTorpedoNotBangDropPerExChangeRateMin");
        // System.out.println(
        // "rareTorpedoNotBangDropPerExChangeRateMin*****************:" + rareTorpedoNotBangDropPerExChangeRateMin);
        if (rareTorpedoNotBangDropPerExChangeRateMin != null
                && rareTorpedoNotBangDropPerExChangeRateMin.length() != 0) {
            rareTorpedoNotBangDropPerExChangeRateMin = rareTorpedoNotBangDropPerExChangeRateMin
                    .substring(rareTorpedoNotBangDropPerExChangeRateMin.lastIndexOf("[") + 1).replaceAll("]", "");
            String[] q1 = rareTorpedoNotBangDropPerExChangeRateMin.split(",");
            List<Double> list = new ArrayList<>();
            for (String q : q1) {
                // System.out.println(q);
                if (q == null || "".equals(q)) {
                    return;
                }
                list.add(Double.parseDouble(q));
            }
            FishingUtil.rareTorpedoNotBangDropPerExChangeRateMin = list.toArray(new Double[0]);
        }

        String torpedoDropPerPayMoney = RedisHelper.get("torpedoDropPerPayMoney");
        // System.out.println("torpedoDropPerPayMoney*****************:" + torpedoDropPerPayMoney);
        if (torpedoDropPerPayMoney != null && torpedoDropPerPayMoney.length() != 0) {
            torpedoDropPerPayMoney =
                    torpedoDropPerPayMoney.substring(torpedoDropPerPayMoney.lastIndexOf("[") + 1).replaceAll("]", "");
            String[] q1 = torpedoDropPerPayMoney.split(",");
            List<Double> list = new ArrayList<>();
            for (String q : q1) {
                // System.out.println(q);
                if (q == null || "".equals(q)) {
                    return;
                }
                list.add(Double.parseDouble(q));
            }
            FishingUtil.torpedoDropPerPayMoney = list.toArray(new Double[0]);
        }

        String torpedoDropPerPayRate = RedisHelper.get("torpedoDropPerPayRate");
        // System.out.println("torpedoDropPerPayRate*****************:" + torpedoDropPerPayRate);
        if (torpedoDropPerPayRate != null && torpedoDropPerPayRate.length() != 0) {
            torpedoDropPerPayRate =
                    torpedoDropPerPayRate.substring(torpedoDropPerPayRate.lastIndexOf("[") + 1).replaceAll("]", "");
            String[] q1 = torpedoDropPerPayRate.split(",");
            List<Double> list = new ArrayList<>();
            for (String q : q1) {
                // System.out.println(q);
                if (q == null || "".equals(q)) {
                    return;
                }
                list.add(Double.parseDouble(q));
            }
            FishingUtil.torpedoDropPerPayRate = list.toArray(new Double[0]);
        }

        String torpedoDropPerExChangeRate = RedisHelper.get("torpedoDropPerExChangeRate");
        // System.out.println("torpedoDropPerExChangeRate*****************:" + torpedoDropPerExChangeRate);
        if (torpedoDropPerExChangeRate != null && torpedoDropPerExChangeRate.length() != 0) {
            torpedoDropPerExChangeRate = torpedoDropPerExChangeRate
                    .substring(torpedoDropPerExChangeRate.lastIndexOf("[") + 1).replaceAll("]", "");
            String[] q1 = torpedoDropPerExChangeRate.split(",");
            List<Double> list = new ArrayList<>();
            for (String q : q1) {
                // System.out.println(q);
                if (q == null || "".equals(q)) {
                    return;
                }
                list.add(Double.parseDouble(q));
            }
            FishingUtil.torpedoDropPerExChangeRate = list.toArray(new Double[0]);
        }

        String torpedoNotBangDropPerExChangeRate = RedisHelper.get("torpedoNotBangDropPerExChangeRate");
        // System.out.println("torpedoNotBangDropPerExChangeRate*****************:" +
        // torpedoNotBangDropPerExChangeRate);
        if (torpedoNotBangDropPerExChangeRate != null && torpedoNotBangDropPerExChangeRate.length() != 0) {
            torpedoNotBangDropPerExChangeRate = torpedoNotBangDropPerExChangeRate
                    .substring(torpedoNotBangDropPerExChangeRate.lastIndexOf("[") + 1).replaceAll("]", "");
            String[] q1 = torpedoNotBangDropPerExChangeRate.split(",");
            List<Double> list = new ArrayList<>();
            for (String q : q1) {
                // System.out.println(q);
                if (q == null || "".equals(q)) {
                    return;
                }
                list.add(Double.parseDouble(q));
            }
            FishingUtil.torpedoNotBangDropPerExChangeRate = list.toArray(new Double[0]);
        }

        // String isUsed = RedisHelper.get("isUsed");
        // System.out.println("isUsed*****************:"+isUsed);
        // if(isUsed!=null&&isUsed.length()!=0){
        // isUsed = isUsed.substring(isUsed.lastIndexOf("[")+1).replaceAll("]","");
        // String[] q1 = isUsed.split(",");
        // List<String> list = new ArrayList<>();
        // for (String q : q1) {
        // System.out.println(q);
        // if(q==null||"".equals(q)){
        // return;
        // }
        // list.add(q);
        // }
        // FishingUtil.isUsed =list.toArray(new String[0]);
        // }

        String jcPercentage = RedisHelper.get("jcPercentage");
        // System.out.println("jcPercentage*****************:" + jcPercentage);
        if (jcPercentage != null && jcPercentage.length() != 0) {
            jcPercentage = jcPercentage.substring(jcPercentage.lastIndexOf("[") + 1).replaceAll("]", "");
            String[] ap1 = jcPercentage.split(",");
            List<Double> listap = new ArrayList<>();
            for (String a : ap1) {
                // System.out.println(a);
                if (a == null || "".equals(a)) {
                    return;
                }
                listap.add(Double.parseDouble(a));
            }
            listap.add(0.0);
            listap.add(0.0);
            listap.add(0.0);
            listap.add(0.0);
            listap.add(0.0);
            listap.add(0.0);
            listap.add(0.0);
            FishingUtil.jcPercentage = listap.toArray(new Double[0]);
        }

        String cxPercentage = RedisHelper.get("cxPercentage");
        // System.out.println("cxPercentage*****************:" + cxPercentage);
        if (cxPercentage != null && cxPercentage.length() != 0) {
            cxPercentage = cxPercentage.substring(cxPercentage.lastIndexOf("[") + 1).replaceAll("]", "");
            String[] ap1 = cxPercentage.split(",");
            List<Double> listap = new ArrayList<>();
            for (String a : ap1) {
                // System.out.println(a);
                if (a == null || "".equals(a)) {
                    return;
                }

                listap.add(Double.parseDouble(a));
            }
            listap.add(0.0);
            listap.add(0.0);
            listap.add(0.0);
            listap.add(0.0);
            listap.add(0.0);
            listap.add(0.0);
            listap.add(0.0);
            FishingUtil.cxPercentage = listap.toArray(new Double[0]);
        }

        String peakMax1 = RedisHelper.get("peakMax1");
        // System.out.println("peakMax1*****************:" + peakMax1);
        if (peakMax1 != null && peakMax1.length() != 0) {
            peakMax1 = peakMax1.substring(peakMax1.lastIndexOf("[") + 1).replaceAll("]", "");
            String[] ap1 = peakMax1.split(",");
            List<Double> listap = new ArrayList<>();
            for (String a : ap1) {
                // System.out.println(a);
                if (a == null || "".equals(a)) {
                    return;
                }
                listap.add(Double.parseDouble(a));
            }
            listap.add(0.0);
            listap.add(0.0);
            listap.add(0.0);
            listap.add(0.0);
            listap.add(0.0);
            listap.add(0.0);
            listap.add(0.0);
            FishingUtil.peakMax1 = listap.toArray(new Double[0]);
        }

        String peakMin1 = RedisHelper.get("peakMin1");
        // System.out.println("peakMin1*****************:" + peakMin1);
        if (peakMin1 != null && peakMin1.length() != 0) {
            peakMin1 = peakMin1.substring(peakMin1.lastIndexOf("[") + 1).replaceAll("]", "");
            String[] ap1 = peakMin1.split(",");
            List<Double> listap = new ArrayList<>();
            for (String a : ap1) {
                // System.out.println(a);
                if (a == null || "".equals(a)) {
                    return;
                }
                listap.add(Double.parseDouble(a));
            }
            listap.add(0.0);
            listap.add(0.0);
            listap.add(0.0);
            listap.add(0.0);
            listap.add(0.0);
            listap.add(0.0);
            listap.add(0.0);
            FishingUtil.peakMin1 = listap.toArray(new Double[0]);
        }

        String peakMax2 = RedisHelper.get("peakMax2");
        // System.out.println("peakMax2*****************:" + peakMax2);
        if (peakMax2 != null && peakMax2.length() != 0) {
            peakMax2 = peakMax2.substring(peakMax2.lastIndexOf("[") + 1).replaceAll("]", "");
            String[] ap1 = peakMax2.split(",");
            List<Double> listap = new ArrayList<>();
            for (String a : ap1) {
                // System.out.println(a);
                if (a == null || "".equals(a)) {
                    return;
                }
                listap.add(Double.parseDouble(a));
            }
            listap.add(0.0);
            listap.add(0.0);
            listap.add(0.0);
            listap.add(0.0);
            listap.add(0.0);
            listap.add(0.0);
            listap.add(0.0);
            FishingUtil.peakMax2 = listap.toArray(new Double[0]);
        }

        String peakMin2 = RedisHelper.get("peakMin2");
        // System.out.println("peakMin2*****************:" + peakMin2);
        if (peakMin2 != null && peakMin2.length() != 0) {
            peakMin2 = peakMin2.substring(peakMin2.lastIndexOf("[") + 1).replaceAll("]", "");
            String[] ap1 = peakMin2.split(",");
            List<Double> listap = new ArrayList<>();
            for (String a : ap1) {
                // System.out.println(a);
                if (a == null || "".equals(a)) {
                    return;
                }
                listap.add(Double.parseDouble(a));
            }
            listap.add(0.0);
            listap.add(0.0);
            listap.add(0.0);
            listap.add(0.0);
            listap.add(0.0);
            listap.add(0.0);
            listap.add(0.0);
            FishingUtil.peakMin2 = listap.toArray(new Double[0]);
        }

        String peakMax3 = RedisHelper.get("peakMax3");
        // System.out.println("peakMax3*****************:" + peakMax3);
        if (peakMax3 != null && peakMax3.length() != 0) {
            peakMax3 = peakMax3.substring(peakMax3.lastIndexOf("[") + 1).replaceAll("]", "");
            String[] ap1 = peakMax3.split(",");
            List<Double> listap = new ArrayList<>();
            for (String a : ap1) {
                // System.out.println(a);
                if (a == null || "".equals(a)) {
                    return;
                }
                listap.add(Double.parseDouble(a));
            }
            listap.add(0.0);
            listap.add(0.0);
            listap.add(0.0);
            listap.add(0.0);
            listap.add(0.0);
            listap.add(0.0);
            listap.add(0.0);
            FishingUtil.peakMax3 = listap.toArray(new Double[0]);
        }

        String peakMin3 = RedisHelper.get("peakMin3");
        // System.out.println("peakMin3*****************:" + peakMin3);
        if (peakMin3 != null && peakMin3.length() != 0) {
            peakMin3 = peakMin3.substring(peakMin3.lastIndexOf("[") + 1).replaceAll("]", "");
            String[] ap1 = peakMin3.split(",");
            List<Double> listap = new ArrayList<>();
            for (String a : ap1) {
                // System.out.println(a);
                if (a == null || "".equals(a)) {
                    return;
                }
                listap.add(Double.parseDouble(a));
            }
            listap.add(0.0);
            listap.add(0.0);
            listap.add(0.0);
            listap.add(0.0);
            listap.add(0.0);
            listap.add(0.0);
            listap.add(0.0);
            FishingUtil.peakMin3 = listap.toArray(new Double[0]);
        }

        String peakMaxNum1 = RedisHelper.get("peakMaxNum1");
        // System.out.println("peakMaxNum1*****************:" + peakMaxNum1);
        if (peakMaxNum1 != null && peakMaxNum1.length() != 0) {
            peakMaxNum1 = peakMaxNum1.substring(peakMaxNum1.lastIndexOf("[") + 1).replaceAll("]", "");
            String[] ap1 = peakMaxNum1.split(",");
            List<Double> listap = new ArrayList<>();
            for (String a : ap1) {
                // System.out.println(a);
                if (a == null || "".equals(a)) {
                    return;
                }
                listap.add(Double.parseDouble(a));
            }
            FishingUtil.peakMaxNum1 = listap.toArray(new Double[0]);
        }

        String peakMinNum1 = RedisHelper.get("peakMinNum1");
        // System.out.println("peakMinNum1*****************:" + peakMinNum1);
        if (peakMinNum1 != null && peakMinNum1.length() != 0) {
            peakMinNum1 = peakMinNum1.substring(peakMinNum1.lastIndexOf("[") + 1).replaceAll("]", "");
            String[] ap1 = peakMinNum1.split(",");
            List<Double> listap = new ArrayList<>();
            for (String a : ap1) {
                // System.out.println(a);
                if (a == null || "".equals(a)) {
                    return;
                }
                listap.add(Double.parseDouble(a));
            }
            FishingUtil.peakMinNum1 = listap.toArray(new Double[0]);
        }

        String peakMaxNum2 = RedisHelper.get("peakMaxNum2");
        // System.out.println("peakMaxNum2*****************:" + peakMaxNum2);
        if (peakMaxNum2 != null && peakMaxNum2.length() != 0) {
            peakMaxNum2 = peakMaxNum2.substring(peakMaxNum2.lastIndexOf("[") + 1).replaceAll("]", "");
            String[] ap1 = peakMaxNum2.split(",");
            List<Double> listap = new ArrayList<>();
            for (String a : ap1) {
                if (a == null || "".equals(a)) {
                    return;
                }
                listap.add(Double.parseDouble(a));
            }
            FishingUtil.peakMaxNum2 = listap.toArray(new Double[0]);
        }

        String peakMinNum2 = RedisHelper.get("peakMinNum2");
        // System.out.println("peakMinNum2*****************:" + peakMinNum2);
        if (peakMinNum2 != null && peakMinNum2.length() != 0) {
            peakMinNum2 = peakMinNum2.substring(peakMinNum2.lastIndexOf("[") + 1).replaceAll("]", "");
            String[] ap1 = peakMinNum2.split(",");
            List<Double> listap = new ArrayList<>();
            for (String a : ap1) {
                if (a == null || "".equals(a)) {
                    return;
                }
                listap.add(Double.parseDouble(a));
            }
            FishingUtil.peakMinNum2 = listap.toArray(new Double[0]);
        }

        String peakMaxNum3 = RedisHelper.get("peakMaxNum3");
        // System.out.println("peakMaxNum3*****************:" + peakMaxNum3);
        if (peakMaxNum3 != null && peakMaxNum3.length() != 0) {
            peakMaxNum3 = peakMaxNum3.substring(peakMaxNum3.lastIndexOf("[") + 1).replaceAll("]", "");
            String[] ap1 = peakMaxNum3.split(",");
            List<Double> listap = new ArrayList<>();
            for (String a : ap1) {
                if (a == null || "".equals(a)) {
                    return;
                }
                listap.add(Double.parseDouble(a));
            }
            FishingUtil.peakMaxNum3 = listap.toArray(new Double[0]);
        }

        String peakMinNum3 = RedisHelper.get("peakMinNum3");
        // System.out.println("peakMinNum3*****************:" + peakMinNum3);
        if (peakMinNum3 != null && peakMinNum3.length() != 0) {
            peakMinNum3 = peakMinNum3.substring(peakMinNum3.lastIndexOf("[") + 1).replaceAll("]", "");
            String[] ap1 = peakMinNum3.split(",");
            List<Double> listap = new ArrayList<>();
            for (String a : ap1) {
                if (a == null || "".equals(a)) {
                    return;
                }
                listap.add(Double.parseDouble(a));
            }
            FishingUtil.peakMinNum3 = listap.toArray(new Double[0]);
        }

        String q0 = RedisHelper.get("q0");
        // System.out.println("Q0*****************:" + q0);
        if (q0 != null && q0.length() != 0) {
            q0 = q0.substring(q0.lastIndexOf("[") + 1).replaceAll("]", "");
            String[] q1 = q0.split(",");
            List<Long> list = new ArrayList<>();
            for (String q : q1) {
                if (q == null || "".equals(q)) {
                    return;
                }
                list.add(new Double(Double.parseDouble(q)).longValue());
            }
            FishingUtil.q0 = list.toArray(new Long[0]);
        }
        String ap = RedisHelper.get("ap");
        // System.out.println("ap*****************:" + ap);
        if (ap != null && ap.length() != 0) {
            ap = ap.substring(ap.lastIndexOf("[") + 1).replaceAll("]", "");
            String[] ap1 = ap.split(",");
            List<Double> listap = new ArrayList<>();
            for (String a : ap1) {
                if (a == null || "".equals(a)) {
                    return;
                }
                listap.add(Double.parseDouble(a));
            }
            FishingUtil.ap = listap.toArray(new Double[0]);
        }
        String apt = RedisHelper.get("apt");
        // System.out.println("apt*****************:" + apt);
        if (apt != null && apt.length() != 0) {
            apt = apt.substring(apt.lastIndexOf("[") + 1).replaceAll("]", "");
            String[] p1 = apt.split(",");
            List<Long> listapt = new ArrayList<>();
            for (String p : p1) {
                if (p == null || "".equals(p)) {
                    return;
                }
                listapt.add(new Double(Double.parseDouble(p)).longValue());
            }
            FishingUtil.apt = listapt.toArray(new Long[0]);
        }

        String recMax = RedisHelper.get("recMax");
        // System.out.println("recMax*****************:" + recMax);
        if (recMax != null && recMax.length() != 0) {
            recMax = recMax.substring(recMax.lastIndexOf("[") + 1).replaceAll("]", "");
            String[] p1 = recMax.split(",");
            List<Long> listrecMax = new ArrayList<>();
            for (String p : p1) {
                if (p == null || "".equals(p)) {
                    return;
                }
                listrecMax.add(new Double(Double.parseDouble(p)).longValue());
            }
            FishingUtil.recMax = listrecMax.toArray(new Long[0]);
        }

        String recMin = RedisHelper.get("recMin");
        // System.out.println("recMin*****************:" + recMin);
        if (recMin != null && recMin.length() != 0) {
            recMin = recMin.substring(recMin.lastIndexOf("[") + 1).replaceAll("]", "");
            String[] p1 = recMin.split(",");
            List<Long> listrecMin = new ArrayList<>();
            for (String p : p1) {
                if (p == null || "".equals(p)) {
                    return;
                }
                listrecMin.add(new Double(Double.parseDouble(p)).longValue());
            }
            FishingUtil.recMin = listrecMin.toArray(new Long[0]);
        }

        String ap1 = RedisHelper.get("ap1");
        if (ap1 != null && ap1.length() != 0) {
            ap1 = ap1.substring(ap1.lastIndexOf("[") + 1).replaceAll("]", "");
            String[] ap11 = ap1.split(",");
            List<Double> listap = new ArrayList<>();
            for (String a : ap11) {
                if (a == null || "".equals(a)) {
                    return;
                }
                listap.add(Double.parseDouble(a));
            }
            FishingUtil.ap1 = listap.toArray(new Double[0]);
        }
        String apt1 = RedisHelper.get("apt1");
        if (apt1 != null && apt1.length() != 0) {
            apt1 = apt1.substring(apt1.lastIndexOf("[") + 1).replaceAll("]", "");
            String[] p1 = apt1.split(",");
            List<Long> listapt = new ArrayList<>();
            for (String p : p1) {
                if (p == null || "".equals(p)) {
                    return;
                }
                listapt.add(new Double(Double.parseDouble(p)).longValue());
            }
            FishingUtil.apt1 = listapt.toArray(new Long[0]);
        }

        String recMax1 = RedisHelper.get("recMax1");
        if (recMax1 != null && recMax1.length() != 0) {
            recMax1 = recMax1.substring(recMax1.lastIndexOf("[") + 1).replaceAll("]", "");
            String[] p1 = recMax1.split(",");
            List<Long> listrecMax = new ArrayList<>();
            for (String p : p1) {
                if (p == null || "".equals(p)) {
                    return;
                }
                listrecMax.add(new Double(Double.parseDouble(p)).longValue());
            }
            FishingUtil.recMax1 = listrecMax.toArray(new Long[0]);
        }

        String recMin1 = RedisHelper.get("recMin1");
        if (recMin1 != null && recMin1.length() != 0) {
            recMin1 = recMin1.substring(recMin1.lastIndexOf("[") + 1).replaceAll("]", "");
            String[] p1 = recMin1.split(",");
            List<Long> listrecMin = new ArrayList<>();
            for (String p : p1) {
                if (p == null || "".equals(p)) {
                    return;
                }
                listrecMin.add(new Double(Double.parseDouble(p)).longValue());
            }
            FishingUtil.recMin1 = listrecMin.toArray(new Long[0]);
        }

        String ap2 = RedisHelper.get("ap2");
        if (ap2 != null && ap2.length() != 0) {
            ap2 = ap2.substring(ap2.lastIndexOf("[") + 1).replaceAll("]", "");
            String[] ap12 = ap2.split(",");
            List<Double> listap = new ArrayList<>();
            for (String a : ap12) {
                if (a == null || "".equals(a)) {
                    return;
                }
                listap.add(Double.parseDouble(a));
            }
            FishingUtil.ap2 = listap.toArray(new Double[0]);
        }
        String apt2 = RedisHelper.get("apt2");
        if (apt2 != null && apt2.length() != 0) {
            apt2 = apt2.substring(apt2.lastIndexOf("[") + 1).replaceAll("]", "");
            String[] p1 = apt2.split(",");
            List<Long> listapt = new ArrayList<>();
            for (String p : p1) {
                if (p == null || "".equals(p)) {
                    return;
                }
                listapt.add(new Double(Double.parseDouble(p)).longValue());
            }
            FishingUtil.apt2 = listapt.toArray(new Long[0]);
        }

        String recMax2 = RedisHelper.get("recMax2");
        if (recMax2 != null && recMax2.length() != 0) {
            recMax2 = recMax2.substring(recMax2.lastIndexOf("[") + 1).replaceAll("]", "");
            String[] p1 = recMax2.split(",");
            List<Long> listrecMax = new ArrayList<>();
            for (String p : p1) {
                if (p == null || "".equals(p)) {
                    return;
                }
                listrecMax.add(new Double(Double.parseDouble(p)).longValue());
            }
            FishingUtil.recMax2 = listrecMax.toArray(new Long[0]);
        }

        String recMin2 = RedisHelper.get("recMin2");
        if (recMin2 != null && recMin2.length() != 0) {
            recMin2 = recMin2.substring(recMin2.lastIndexOf("[") + 1).replaceAll("]", "");
            String[] p1 = recMin2.split(",");
            List<Long> listrecMin = new ArrayList<>();
            for (String p : p1) {
                if (p == null || "".equals(p)) {
                    return;
                }
                listrecMin.add(new Double(Double.parseDouble(p)).longValue());
            }
            FishingUtil.recMin2 = listrecMin.toArray(new Long[0]);
        }
        String ap3 = RedisHelper.get("ap3");
        if (ap3 != null && ap3.length() != 0) {
            ap3 = ap3.substring(ap3.lastIndexOf("[") + 1).replaceAll("]", "");
            String[] ap13 = ap3.split(",");
            List<Double> listap = new ArrayList<>();
            for (String a : ap13) {
                if (a == null || "".equals(a)) {
                    return;
                }
                listap.add(Double.parseDouble(a));
            }
            FishingUtil.ap3 = listap.toArray(new Double[0]);
        }
        String apt3 = RedisHelper.get("apt3");
        if (apt3 != null && apt3.length() != 0) {
            apt3 = apt3.substring(apt3.lastIndexOf("[") + 1).replaceAll("]", "");
            String[] p1 = apt3.split(",");
            List<Long> listapt = new ArrayList<>();
            for (String p : p1) {
                if (p == null || "".equals(p)) {
                    return;
                }
                listapt.add(new Double(Double.parseDouble(p)).longValue());
            }
            FishingUtil.apt3 = listapt.toArray(new Long[0]);
        }

        String recMax3 = RedisHelper.get("recMax3");
        if (recMax3 != null && recMax3.length() != 0) {
            recMax3 = recMax3.substring(recMax3.lastIndexOf("[") + 1).replaceAll("]", "");
            String[] p1 = recMax3.split(",");
            List<Long> listrecMax = new ArrayList<>();
            for (String p : p1) {
                if (p == null || "".equals(p)) {
                    return;
                }
                listrecMax.add(new Double(Double.parseDouble(p)).longValue());
            }
            FishingUtil.recMax3 = listrecMax.toArray(new Long[0]);
        }

        String recMin3 = RedisHelper.get("recMin3");
        if (recMin3 != null && recMin3.length() != 0) {
            recMin3 = recMin3.substring(recMin3.lastIndexOf("[") + 1).replaceAll("]", "");
            String[] p1 = recMin3.split(",");
            List<Long> listrecMin = new ArrayList<>();
            for (String p : p1) {
                if (p == null || "".equals(p)) {
                    return;
                }
                listrecMin.add(new Double(Double.parseDouble(p)).longValue());
            }
            FishingUtil.recMin3 = listrecMin.toArray(new Long[0]);
        }

        String pumpNum = RedisHelper.get("pumpNum");
        if (pumpNum != null && pumpNum.length() != 0) {
            pumpNum = pumpNum.substring(pumpNum.lastIndexOf("[") + 1).replaceAll("]", "");
            String[] pumpNum1 = pumpNum.split(",");
            List<Long> list = new ArrayList<>();
            for (String q : pumpNum1) {
                if (q == null || "".equals(q)) {
                    return;
                }
                list.add(new Double(Double.parseDouble(q)).longValue());
            }
            FishingUtil.pumpNum = list.toArray(new Long[0]);
        }
        String pump = RedisHelper.get("pump");
        if (pump != null && pump.length() != 0) {
            pump = pump.substring(pump.lastIndexOf("[") + 1).replaceAll("]", "");
            String[] pump1 = pump.split(",");
            List<Long> listap = new ArrayList<>();
            for (String a : pump1) {
                if (a == null || "".equals(a)) {
                    return;
                }
                listap.add(new Double(Double.parseDouble(a)).longValue());
            }
            FishingUtil.pump = listap.toArray(new Long[0]);
        }

        String burstOne = RedisHelper.get("burstOne");
        if (burstOne != null && burstOne.length() != 0) {
            burstOne = burstOne.substring(burstOne.lastIndexOf("[") + 1).replaceAll("]", "");
            String[] p1 = burstOne.split(",");
            List<Long> listrecMax = new ArrayList<>();
            for (String p : p1) {
                if (p == null || "".equals(p)) {
                    return;
                }
                listrecMax.add(new Double(Double.parseDouble(p)).longValue());
            }
            FishingUtil.burstOne = listrecMax.toArray(new Long[0]);
        }

        String recOne = RedisHelper.get("recOne");
        if (recOne != null && recOne.length() != 0) {
            recOne = recOne.substring(recOne.lastIndexOf("[") + 1).replaceAll("]", "");
            String[] p1 = recOne.split(",");
            List<Long> listrecMin = new ArrayList<>();
            for (String p : p1) {
                if (p == null || "".equals(p)) {
                    return;
                }
                listrecMin.add(new Double(Double.parseDouble(p)).longValue());
            }
            FishingUtil.recOne = listrecMin.toArray(new Long[0]);
        }

        String balanceOne = RedisHelper.get("balanceOne");
        // System.out.println("balanceOne*****************:" + balanceOne);
        if (balanceOne != null && balanceOne.length() != 0) {
            balanceOne = balanceOne.substring(balanceOne.lastIndexOf("[") + 1).replaceAll("]", "");
            String[] p1 = balanceOne.split(",");
            List<Long> listrecMax = new ArrayList<>();
            for (String p : p1) {
                // System.out.println(p);
                if (p == null || "".equals(p)) {
                    return;
                }
                listrecMax.add(new Double(Double.parseDouble(p)).longValue());
            }
            listrecMax.add(0L);
            listrecMax.add(0L);
            listrecMax.add(0L);
            listrecMax.add(0L);
            listrecMax.add(0L);
            listrecMax.add(0L);
            listrecMax.add(0L);
            listrecMax.add(0L);
            listrecMax.add(0L);
            listrecMax.add(0L);
            listrecMax.add(0L);
            listrecMax.add(0L);
            FishingUtil.balanceOne = listrecMax.toArray(new Long[0]);
        }

        String balanceBuOne = RedisHelper.get("balanceBuOne");
        // System.out.println("balanceBuOne*****************:" + balanceBuOne);
        if (balanceBuOne != null && balanceBuOne.length() != 0) {
            balanceBuOne = balanceBuOne.substring(balanceBuOne.lastIndexOf("[") + 1).replaceAll("]", "");
            String[] p1 = balanceBuOne.split(",");
            List<Long> listrecMin = new ArrayList<>();
            for (String p : p1) {
                // System.out.println(p);
                if (p == null || "".equals(p)) {
                    return;
                }
                listrecMin.add(new Double(Double.parseDouble(p)).longValue());
            }
            listrecMin.add(0L);
            listrecMin.add(0L);
            listrecMin.add(0L);
            listrecMin.add(0L);
            listrecMin.add(0L);
            listrecMin.add(0L);
            listrecMin.add(0L);
            listrecMin.add(0L);
            listrecMin.add(0L);
            listrecMin.add(0L);
            listrecMin.add(0L);
            listrecMin.add(0L);
            FishingUtil.balanceBuOne = listrecMin.toArray(new Long[0]);
        }

        String gcOne = RedisHelper.get("gcOne");
        // System.out.println("gcOne*****************:" + gcOne);
        if (gcOne != null && gcOne.length() != 0) {
            gcOne = gcOne.substring(gcOne.lastIndexOf("[") + 1).replaceAll("]", "");
            String[] p1 = gcOne.split(",");
            List<Long> listrecMax = new ArrayList<>();
            for (String p : p1) {
                // System.out.println(p);
                if (p == null || "".equals(p)) {
                    return;
                }
                listrecMax.add(new Double(Double.parseDouble(p)).longValue());
            }
            listrecMax.add(0L);
            listrecMax.add(0L);
            listrecMax.add(0L);
            listrecMax.add(0L);
            listrecMax.add(0L);
            listrecMax.add(0L);
            listrecMax.add(0L);
            listrecMax.add(0L);
            listrecMax.add(0L);
            listrecMax.add(0L);
            listrecMax.add(0L);
            listrecMax.add(0L);
            FishingUtil.gcOne = listrecMax.toArray(new Long[0]);
        }

        String gcBurstOne = RedisHelper.get("gcBurstOne");
        // System.out.println("gcBurstOne*****************:" + gcBurstOne);
        if (gcBurstOne != null && gcBurstOne.length() != 0) {
            gcBurstOne = gcBurstOne.substring(gcBurstOne.lastIndexOf("[") + 1).replaceAll("]", "");
            String[] p1 = gcBurstOne.split(",");
            List<Long> listrecMin = new ArrayList<>();
            for (String p : p1) {
                // System.out.println(p);
                if (p == null || "".equals(p)) {
                    return;
                }
                listrecMin.add(new Double(Double.parseDouble(p)).longValue());
            }
            listrecMin.add(0L);
            listrecMin.add(0L);
            listrecMin.add(0L);
            listrecMin.add(0L);
            listrecMin.add(0L);
            listrecMin.add(0L);
            listrecMin.add(0L);
            listrecMin.add(0L);
            listrecMin.add(0L);
            listrecMin.add(0L);
            listrecMin.add(0L);
            listrecMin.add(0L);
            FishingUtil.gcBurstOne = listrecMin.toArray(new Long[0]);
        }

        String burstTwo = RedisHelper.get("burstTwo");
        // System.out.println("burstTwo*****************:" + burstTwo);
        if (burstTwo != null && burstTwo.length() != 0) {
            burstTwo = burstTwo.substring(burstTwo.lastIndexOf("[") + 1).replaceAll("]", "");
            String[] p1 = burstTwo.split(",");
            List<Long> listrecMax = new ArrayList<>();
            for (String p : p1) {
                // System.out.println(p);
                if (p == null || "".equals(p)) {
                    return;
                }
                listrecMax.add(new Double(Double.parseDouble(p)).longValue());
            }
            listrecMax.add(0L);
            listrecMax.add(0L);
            listrecMax.add(0L);
            listrecMax.add(0L);
            listrecMax.add(0L);
            listrecMax.add(0L);
            listrecMax.add(0L);
            listrecMax.add(0L);
            listrecMax.add(0L);
            listrecMax.add(0L);
            listrecMax.add(0L);
            listrecMax.add(0L);
            FishingUtil.burstTwo = listrecMax.toArray(new Long[0]);
        }

        String recTwo = RedisHelper.get("recTwo");
        // System.out.println("recTwo*****************:" + recTwo);
        if (recTwo != null && recTwo.length() != 0) {
            recTwo = recTwo.substring(recTwo.lastIndexOf("[") + 1).replaceAll("]", "");
            String[] p1 = recTwo.split(",");
            List<Long> listrecMin = new ArrayList<>();
            for (String p : p1) {
                // System.out.println(p);
                if (p == null || "".equals(p)) {
                    return;
                }
                listrecMin.add(new Double(Double.parseDouble(p)).longValue());
            }
            listrecMin.add(0L);
            listrecMin.add(0L);
            listrecMin.add(0L);
            listrecMin.add(0L);
            listrecMin.add(0L);
            listrecMin.add(0L);
            listrecMin.add(0L);
            listrecMin.add(0L);
            listrecMin.add(0L);
            listrecMin.add(0L);
            listrecMin.add(0L);
            listrecMin.add(0L);
            FishingUtil.recTwo = listrecMin.toArray(new Long[0]);
        }

        String balanceTwo = RedisHelper.get("balanceTwo");
        // System.out.println("balanceTwo*****************:" + balanceTwo);
        if (balanceTwo != null && balanceTwo.length() != 0) {
            balanceTwo = balanceTwo.substring(balanceTwo.lastIndexOf("[") + 1).replaceAll("]", "");
            String[] p1 = balanceTwo.split(",");
            List<Long> listrecMax = new ArrayList<>();
            for (String p : p1) {
                // System.out.println(p);
                if (p == null || "".equals(p)) {
                    return;
                }
                listrecMax.add(new Double(Double.parseDouble(p)).longValue());
            }
            listrecMax.add(0L);
            listrecMax.add(0L);
            listrecMax.add(0L);
            listrecMax.add(0L);
            listrecMax.add(0L);
            listrecMax.add(0L);
            listrecMax.add(0L);
            listrecMax.add(0L);
            listrecMax.add(0L);
            listrecMax.add(0L);
            listrecMax.add(0L);
            listrecMax.add(0L);
            FishingUtil.balanceTwo = listrecMax.toArray(new Long[0]);
        }

        String balanceBuTwo = RedisHelper.get("balanceBuTwo");
        // System.out.println("balanceBuTwo*****************:" + balanceBuTwo);
        if (balanceBuTwo != null && balanceBuTwo.length() != 0) {
            balanceBuTwo = balanceBuTwo.substring(balanceBuTwo.lastIndexOf("[") + 1).replaceAll("]", "");
            String[] p1 = balanceBuTwo.split(",");
            List<Long> listrecMin = new ArrayList<>();
            for (String p : p1) {
                // System.out.println(p);
                if (p == null || "".equals(p)) {
                    return;
                }
                listrecMin.add(new Double(Double.parseDouble(p)).longValue());
            }
            listrecMin.add(0L);
            listrecMin.add(0L);
            listrecMin.add(0L);
            listrecMin.add(0L);
            listrecMin.add(0L);
            listrecMin.add(0L);
            listrecMin.add(0L);
            listrecMin.add(0L);
            listrecMin.add(0L);
            listrecMin.add(0L);
            listrecMin.add(0L);
            listrecMin.add(0L);
            FishingUtil.balanceBuTwo = listrecMin.toArray(new Long[0]);
        }

        String gcTwo = RedisHelper.get("gcTwo");
        // System.out.println("gcTwo*****************:" + gcTwo);
        if (gcTwo != null && gcTwo.length() != 0) {
            gcTwo = gcTwo.substring(gcTwo.lastIndexOf("[") + 1).replaceAll("]", "");
            String[] p1 = gcTwo.split(",");
            List<Long> listrecMax = new ArrayList<>();
            for (String p : p1) {
                // System.out.println(p);
                if (p == null || "".equals(p)) {
                    return;
                }
                listrecMax.add(new Double(Double.parseDouble(p)).longValue());
            }
            listrecMax.add(0L);
            listrecMax.add(0L);
            listrecMax.add(0L);
            listrecMax.add(0L);
            listrecMax.add(0L);
            listrecMax.add(0L);
            listrecMax.add(0L);
            listrecMax.add(0L);
            listrecMax.add(0L);
            listrecMax.add(0L);
            listrecMax.add(0L);
            listrecMax.add(0L);
            FishingUtil.gcTwo = listrecMax.toArray(new Long[0]);
        }

        String gcBurstTwo = RedisHelper.get("gcBurstTwo");
        // System.out.println("gcBurstTwo*****************:" + gcBurstTwo);
        if (gcBurstTwo != null && gcBurstTwo.length() != 0) {
            gcBurstTwo = gcBurstTwo.substring(gcBurstTwo.lastIndexOf("[") + 1).replaceAll("]", "");
            String[] p1 = gcBurstTwo.split(",");
            List<Long> listrecMin = new ArrayList<>();
            for (String p : p1) {
                // System.out.println(p);
                if (p == null || "".equals(p)) {
                    return;
                }
                listrecMin.add(new Double(Double.parseDouble(p)).longValue());
            }
            listrecMin.add(0L);
            listrecMin.add(0L);
            listrecMin.add(0L);
            listrecMin.add(0L);
            listrecMin.add(0L);
            listrecMin.add(0L);
            listrecMin.add(0L);
            listrecMin.add(0L);
            listrecMin.add(0L);
            listrecMin.add(0L);
            listrecMin.add(0L);
            listrecMin.add(0L);
            FishingUtil.gcBurstTwo = listrecMin.toArray(new Long[0]);
        }

        String burstThree = RedisHelper.get("burstThree");
        // System.out.println("burstThree*****************:" + burstThree);
        if (burstThree != null && burstThree.length() != 0) {
            burstThree = burstThree.substring(burstThree.lastIndexOf("[") + 1).replaceAll("]", "");
            String[] p1 = burstThree.split(",");
            List<Long> listrecMax = new ArrayList<>();
            for (String p : p1) {
                // System.out.println(p);
                if (p == null || "".equals(p)) {
                    return;
                }
                listrecMax.add(new Double(Double.parseDouble(p)).longValue());
            }
            listrecMax.add(0L);
            listrecMax.add(0L);
            listrecMax.add(0L);
            listrecMax.add(0L);
            listrecMax.add(0L);
            listrecMax.add(0L);
            listrecMax.add(0L);
            listrecMax.add(0L);
            listrecMax.add(0L);
            listrecMax.add(0L);
            listrecMax.add(0L);
            listrecMax.add(0L);
            FishingUtil.burstThree = listrecMax.toArray(new Long[0]);
        }

        String recThree = RedisHelper.get("recThree");
        // System.out.println("recThree*****************:" + recThree);
        if (recThree != null && recThree.length() != 0) {
            recThree = recThree.substring(recThree.lastIndexOf("[") + 1).replaceAll("]", "");
            String[] p1 = recThree.split(",");
            List<Long> listrecMin = new ArrayList<>();
            for (String p : p1) {
                // System.out.println(p);
                if (p == null || "".equals(p)) {
                    return;
                }
                listrecMin.add(new Double(Double.parseDouble(p)).longValue());
            }
            listrecMin.add(0L);
            listrecMin.add(0L);
            listrecMin.add(0L);
            listrecMin.add(0L);
            listrecMin.add(0L);
            listrecMin.add(0L);
            listrecMin.add(0L);
            listrecMin.add(0L);
            listrecMin.add(0L);
            listrecMin.add(0L);
            listrecMin.add(0L);
            listrecMin.add(0L);
            FishingUtil.recThree = listrecMin.toArray(new Long[0]);
        }

        String balanceThree = RedisHelper.get("balanceThree");
        // System.out.println("balanceThree*****************:" + balanceThree);
        if (balanceThree != null && balanceThree.length() != 0) {
            balanceThree = balanceThree.substring(balanceThree.lastIndexOf("[") + 1).replaceAll("]", "");
            String[] p1 = balanceThree.split(",");
            List<Long> listrecMax = new ArrayList<>();
            for (String p : p1) {
                // System.out.println(p);
                if (p == null || "".equals(p)) {
                    return;
                }
                listrecMax.add(new Double(Double.parseDouble(p)).longValue());
            }
            listrecMax.add(0L);
            listrecMax.add(0L);
            listrecMax.add(0L);
            listrecMax.add(0L);
            listrecMax.add(0L);
            listrecMax.add(0L);
            listrecMax.add(0L);
            listrecMax.add(0L);
            listrecMax.add(0L);
            listrecMax.add(0L);
            listrecMax.add(0L);
            listrecMax.add(0L);
            FishingUtil.balanceThree = listrecMax.toArray(new Long[0]);
        }

        String balanceBuThree = RedisHelper.get("balanceBuThree");
        // System.out.println("balanceBuThree*****************:" + balanceBuThree);
        if (balanceBuThree != null && balanceBuThree.length() != 0) {
            balanceBuThree = balanceBuThree.substring(balanceBuThree.lastIndexOf("[") + 1).replaceAll("]", "");
            String[] p1 = balanceBuThree.split(",");
            List<Long> listrecMin = new ArrayList<>();
            for (String p : p1) {
                // System.out.println(p);
                if (p == null || "".equals(p)) {
                    return;
                }
                listrecMin.add(new Double(Double.parseDouble(p)).longValue());
            }
            listrecMin.add(0L);
            listrecMin.add(0L);
            listrecMin.add(0L);
            listrecMin.add(0L);
            listrecMin.add(0L);
            listrecMin.add(0L);
            listrecMin.add(0L);
            listrecMin.add(0L);
            listrecMin.add(0L);
            listrecMin.add(0L);
            listrecMin.add(0L);
            listrecMin.add(0L);
            FishingUtil.balanceBuThree = listrecMin.toArray(new Long[0]);
        }

        String gcThree = RedisHelper.get("gcThree");
        // System.out.println("gcThree*****************:" + gcThree);
        if (gcThree != null && gcThree.length() != 0) {
            gcThree = gcThree.substring(gcThree.lastIndexOf("[") + 1).replaceAll("]", "");
            String[] p1 = gcThree.split(",");
            List<Long> listrecMax = new ArrayList<>();
            for (String p : p1) {
                // System.out.println(p);
                if (p == null || "".equals(p)) {
                    return;
                }
                listrecMax.add(new Double(Double.parseDouble(p)).longValue());
            }
            listrecMax.add(0L);
            listrecMax.add(0L);
            listrecMax.add(0L);
            listrecMax.add(0L);
            listrecMax.add(0L);
            listrecMax.add(0L);
            listrecMax.add(0L);
            listrecMax.add(0L);
            listrecMax.add(0L);
            listrecMax.add(0L);
            listrecMax.add(0L);
            listrecMax.add(0L);
            FishingUtil.gcThree = listrecMax.toArray(new Long[0]);
        }

        String gcBurstThree = RedisHelper.get("gcBurstThree");
        // System.out.println("gcBurstThree*****************:" + gcBurstThree);
        if (gcBurstThree != null && gcBurstThree.length() != 0) {
            gcBurstThree = gcBurstThree.substring(gcBurstThree.lastIndexOf("[") + 1).replaceAll("]", "");
            String[] p1 = gcBurstThree.split(",");
            List<Long> listrecMin = new ArrayList<>();
            for (String p : p1) {
                // System.out.println(p);
                if (p == null || "".equals(p)) {
                    return;
                }
                listrecMin.add(new Double(Double.parseDouble(p)).longValue());
            }
            listrecMin.add(0L);
            listrecMin.add(0L);
            listrecMin.add(0L);
            listrecMin.add(0L);
            listrecMin.add(0L);
            listrecMin.add(0L);
            listrecMin.add(0L);
            listrecMin.add(0L);
            listrecMin.add(0L);
            listrecMin.add(0L);
            listrecMin.add(0L);
            listrecMin.add(0L);
            FishingUtil.gcBurstThree = listrecMin.toArray(new Long[0]);
        }

        String burstFour = RedisHelper.get("burstFour");
        // System.out.println("burstFour*****************:" + burstFour);
        if (burstFour != null && burstFour.length() != 0) {
            burstFour = burstFour.substring(burstFour.lastIndexOf("[") + 1).replaceAll("]", "");
            String[] p1 = burstFour.split(",");
            List<Long> listrecMax = new ArrayList<>();
            for (String p : p1) {
                // System.out.println(p);
                if (p == null || "".equals(p)) {
                    return;
                }
                listrecMax.add(new Double(Double.parseDouble(p)).longValue());
            }
            listrecMax.add(0L);
            listrecMax.add(0L);
            listrecMax.add(0L);
            listrecMax.add(0L);
            listrecMax.add(0L);
            listrecMax.add(0L);
            listrecMax.add(0L);
            listrecMax.add(0L);
            listrecMax.add(0L);
            listrecMax.add(0L);
            listrecMax.add(0L);
            listrecMax.add(0L);
            FishingUtil.burstFour = listrecMax.toArray(new Long[0]);
        }

        String recFour = RedisHelper.get("recFour");
        // System.out.println("recFour*****************:" + recFour);
        if (recFour != null && recFour.length() != 0) {
            recFour = recFour.substring(recFour.lastIndexOf("[") + 1).replaceAll("]", "");
            String[] p1 = recFour.split(",");
            List<Long> listrecMin = new ArrayList<>();
            for (String p : p1) {
                // System.out.println(p);
                if (p == null || "".equals(p)) {
                    return;
                }
                listrecMin.add(new Double(Double.parseDouble(p)).longValue());
            }
            listrecMin.add(0L);
            listrecMin.add(0L);
            listrecMin.add(0L);
            listrecMin.add(0L);
            listrecMin.add(0L);
            listrecMin.add(0L);
            listrecMin.add(0L);
            listrecMin.add(0L);
            listrecMin.add(0L);
            listrecMin.add(0L);
            listrecMin.add(0L);
            listrecMin.add(0L);
            FishingUtil.recFour = listrecMin.toArray(new Long[0]);
        }

        String balanceFour = RedisHelper.get("balanceFour");
        // System.out.println("balanceFour*****************:" + balanceFour);
        if (balanceFour != null && balanceFour.length() != 0) {
            balanceFour = balanceFour.substring(balanceFour.lastIndexOf("[") + 1).replaceAll("]", "");
            String[] p1 = balanceFour.split(",");
            List<Long> listrecMax = new ArrayList<>();
            for (String p : p1) {
                // System.out.println(p);
                if (p == null || "".equals(p)) {
                    return;
                }
                listrecMax.add(new Double(Double.parseDouble(p)).longValue());
            }
            listrecMax.add(0L);
            listrecMax.add(0L);
            listrecMax.add(0L);
            listrecMax.add(0L);
            listrecMax.add(0L);
            listrecMax.add(0L);
            listrecMax.add(0L);
            listrecMax.add(0L);
            listrecMax.add(0L);
            listrecMax.add(0L);
            listrecMax.add(0L);
            listrecMax.add(0L);
            FishingUtil.balanceFour = listrecMax.toArray(new Long[0]);
        }

        String balanceBuFour = RedisHelper.get("balanceBuFour");
        // System.out.println("balanceBuFour*****************:" + balanceBuFour);
        if (balanceBuFour != null && balanceBuFour.length() != 0) {
            balanceBuFour = balanceBuFour.substring(balanceBuFour.lastIndexOf("[") + 1).replaceAll("]", "");
            String[] p1 = balanceBuFour.split(",");
            List<Long> listrecMin = new ArrayList<>();
            for (String p : p1) {
                // System.out.println(p);
                if (p == null || "".equals(p)) {
                    return;
                }
                listrecMin.add(new Double(Double.parseDouble(p)).longValue());
            }
            listrecMin.add(0L);
            listrecMin.add(0L);
            listrecMin.add(0L);
            listrecMin.add(0L);
            listrecMin.add(0L);
            listrecMin.add(0L);
            listrecMin.add(0L);
            listrecMin.add(0L);
            listrecMin.add(0L);
            listrecMin.add(0L);
            listrecMin.add(0L);
            listrecMin.add(0L);
            FishingUtil.balanceBuFour = listrecMin.toArray(new Long[0]);
        }

        String gcFour = RedisHelper.get("gcFour");
        // System.out.println("gcFour*****************:" + gcFour);
        if (gcFour != null && gcFour.length() != 0) {
            gcFour = gcFour.substring(gcFour.lastIndexOf("[") + 1).replaceAll("]", "");
            String[] p1 = gcFour.split(",");
            List<Long> listrecMax = new ArrayList<>();
            for (String p : p1) {
                // System.out.println(p);
                if (p == null || "".equals(p)) {
                    return;
                }
                listrecMax.add(new Double(Double.parseDouble(p)).longValue());
            }
            listrecMax.add(0L);
            listrecMax.add(0L);
            listrecMax.add(0L);
            listrecMax.add(0L);
            listrecMax.add(0L);
            listrecMax.add(0L);
            listrecMax.add(0L);
            listrecMax.add(0L);
            listrecMax.add(0L);
            listrecMax.add(0L);
            listrecMax.add(0L);
            listrecMax.add(0L);
            FishingUtil.gcFour = listrecMax.toArray(new Long[0]);
        }

        String gcBurstFour = RedisHelper.get("gcBurstFour");
        // System.out.println("gcBurstFour*****************:" + gcBurstFour);
        if (gcBurstFour != null && gcBurstFour.length() != 0) {
            gcBurstFour = gcBurstFour.substring(gcBurstFour.lastIndexOf("[") + 1).replaceAll("]", "");
            String[] p1 = gcBurstFour.split(",");
            List<Long> listrecMin = new ArrayList<>();
            for (String p : p1) {
                // System.out.println(p);
                if (p == null || "".equals(p)) {
                    return;
                }
                listrecMin.add(new Double(Double.parseDouble(p)).longValue());
            }
            listrecMin.add(0L);
            listrecMin.add(0L);
            listrecMin.add(0L);
            listrecMin.add(0L);
            listrecMin.add(0L);
            listrecMin.add(0L);
            listrecMin.add(0L);
            listrecMin.add(0L);
            listrecMin.add(0L);
            listrecMin.add(0L);
            listrecMin.add(0L);
            listrecMin.add(0L);
            FishingUtil.gcBurstFour = listrecMin.toArray(new Long[0]);
        }

        String PXMin = RedisHelper.get("PXMin");
        // System.out.println("PXMin*****************:" + PXMin);
        if (PXMin != null && PXMin.length() != 0) {
            PXMin = PXMin.substring(PXMin.lastIndexOf("[") + 1).replaceAll("]", "");
            String[] px = PXMin.split(",");
            List<Double> listap = new ArrayList<>();
            for (String a : px) {
                // System.out.println(a);
                if (a == null || "".equals(a)) {
                    return;
                }
                listap.add(Double.parseDouble(a));
            }
            listap.add(0.0);
            listap.add(0.0);
            listap.add(0.0);
            listap.add(0.0);
            listap.add(0.0);
            listap.add(0.0);
            listap.add(0.0);
            listap.add(0.0);
            listap.add(0.0);
            listap.add(0.0);
            listap.add(0.0);
            listap.add(0.0);
            FishingUtil.PXMin = listap.toArray(new Double[0]);
        }

        String FPMin = RedisHelper.get("FPMin");
        // System.out.println("FPMin*****************:" + FPMin);
        if (FPMin != null && FPMin.length() != 0) {
            FPMin = FPMin.substring(FPMin.lastIndexOf("[") + 1).replaceAll("]", "");
            String[] px = FPMin.split(",");
            List<Double> listap = new ArrayList<>();
            for (String a : px) {
                // System.out.println(a);
                if (a == null || "".equals(a)) {
                    return;
                }
                listap.add(Double.parseDouble(a));
            }
            listap.add(0.0);
            listap.add(0.0);
            listap.add(0.0);
            listap.add(0.0);
            listap.add(0.0);
            listap.add(0.0);
            listap.add(0.0);
            listap.add(0.0);
            listap.add(0.0);
            listap.add(0.0);
            listap.add(0.0);
            listap.add(0.0);
            FishingUtil.FPMin = listap.toArray(new Double[0]);
        }

        String PXMax = RedisHelper.get("PXMax");
        // System.out.println("PXMax*****************:" + PXMax);
        if (PXMax != null && PXMax.length() != 0) {
            PXMax = PXMax.substring(PXMax.lastIndexOf("[") + 1).replaceAll("]", "");
            String[] px = PXMax.split(",");
            List<Double> listap = new ArrayList<>();
            for (String a : px) {
                // System.out.println(a);
                if (a == null || "".equals(a)) {
                    return;
                }
                listap.add(Double.parseDouble(a));
            }
            listap.add(0.0);
            listap.add(0.0);
            listap.add(0.0);
            listap.add(0.0);
            listap.add(0.0);
            listap.add(0.0);
            listap.add(0.0);
            listap.add(0.0);
            listap.add(0.0);
            listap.add(0.0);
            listap.add(0.0);
            listap.add(0.0);
            FishingUtil.PXMax = listap.toArray(new Double[0]);
        }

        String FPMax = RedisHelper.get("FPMax");
        // System.out.println("FPMax*****************:" + FPMax);
        if (FPMax != null && FPMax.length() != 0) {
            FPMax = FPMax.substring(FPMax.lastIndexOf("[") + 1).replaceAll("]", "");
            String[] px = FPMax.split(",");
            List<Double> listap = new ArrayList<>();
            for (String a : px) {
                // System.out.println(a);
                if (a == null || "".equals(a)) {
                    return;
                }
                listap.add(Double.parseDouble(a));
            }
            listap.add(0.0);
            listap.add(0.0);
            listap.add(0.0);
            listap.add(0.0);
            listap.add(0.0);
            listap.add(0.0);
            listap.add(0.0);
            listap.add(0.0);
            listap.add(0.0);
            listap.add(0.0);
            listap.add(0.0);
            listap.add(0.0);
            FishingUtil.FPMax = listap.toArray(new Double[0]);
        }

        String Pdx = RedisHelper.get("pdx");
        // System.out.println("pdx*****************:" + Pdx);
        if (Pdx != null && Pdx.length() != 0) {
            Pdx = Pdx.substring(Pdx.lastIndexOf("[") + 1).replaceAll("]", "");
            String[] px = Pdx.split(",");
            List<Double> listap = new ArrayList<>();
            for (String a : px) {
                // System.out.println(a);
                if (a == null || "".equals(a)) {
                    return;
                }
                listap.add(Double.parseDouble(a));
            }
            listap.add(0.0);
            listap.add(0.0);
            listap.add(0.0);
            listap.add(0.0);
            listap.add(0.0);
            listap.add(0.0);
            listap.add(0.0);
            listap.add(0.0);
            listap.add(0.0);
            listap.add(0.0);
            listap.add(0.0);
            listap.add(0.0);
            FishingUtil.Pdx = listap.toArray(new Double[0]);
        }

        String pdxp = RedisHelper.get("pdxp");
        // System.out.println("pdxp*****************:" + pdxp);
        if (pdxp != null && pdxp.length() != 0) {
            pdxp = pdxp.substring(pdxp.lastIndexOf("[") + 1).replaceAll("]", "");
            String[] px = pdxp.split(",");
            List<Double> listap = new ArrayList<>();
            for (String a : px) {
                // System.out.println(a);
                if (a == null || "".equals(a)) {
                    return;
                }
                listap.add(Double.parseDouble(a));
            }
            listap.add(0.0);
            listap.add(0.0);
            listap.add(0.0);
            listap.add(0.0);
            listap.add(0.0);
            listap.add(0.0);
            listap.add(0.0);
            listap.add(0.0);
            listap.add(0.0);
            listap.add(0.0);
            listap.add(0.0);
            listap.add(0.0);
            FishingUtil.Pdxp = listap.toArray(new Double[0]);
        }

        String Xdx = RedisHelper.get("xdx");
        // System.out.println("Xdx*****************:" + Xdx);
        if (Xdx != null && Xdx.length() != 0) {
            Xdx = Xdx.substring(Xdx.lastIndexOf("[") + 1).replaceAll("]", "");
            String[] px = Xdx.split(",");
            List<Double> listap = new ArrayList<>();
            for (String a : px) {
                // System.out.println(a);
                if (a == null || "".equals(a)) {
                    return;
                }
                listap.add(Double.parseDouble(a));
            }
            listap.add(0.0);
            listap.add(0.0);
            listap.add(0.0);
            listap.add(0.0);
            listap.add(0.0);
            listap.add(0.0);
            listap.add(0.0);
            listap.add(0.0);
            listap.add(0.0);
            listap.add(0.0);
            listap.add(0.0);
            listap.add(0.0);
            FishingUtil.Xdx = listap.toArray(new Double[0]);
        }

        String xdxp = RedisHelper.get("xdxp");
        // System.out.println("xdxp*****************:" + xdxp);
        if (xdxp != null && xdxp.length() != 0) {
            xdxp = xdxp.substring(xdxp.lastIndexOf("[") + 1).replaceAll("]", "");
            String[] px = xdxp.split(",");
            List<Double> listap = new ArrayList<>();
            for (String a : px) {
                // System.out.println(a);
                if (a == null || "".equals(a)) {
                    return;
                }
                listap.add(Double.parseDouble(a));
            }
            listap.add(0.0);
            listap.add(0.0);
            listap.add(0.0);
            listap.add(0.0);
            listap.add(0.0);
            listap.add(0.0);
            listap.add(0.0);
            listap.add(0.0);
            listap.add(0.0);
            listap.add(0.0);
            listap.add(0.0);
            listap.add(0.0);
            FishingUtil.Xdxp = listap.toArray(new Double[0]);
        }

        String recOne1 = RedisHelper.get("recOne1");
        // System.out.println("recOne1*****************:" + recOne1);
        if (recOne1 != null && recOne1.length() != 0) {
            recOne1 = recOne1.substring(recOne1.lastIndexOf("[") + 1).replaceAll("]", "");
            String[] q1 = recOne1.split(",");
            List<Long> list = new ArrayList<>();
            for (String q : q1) {
                // System.out.println(q);
                if (q == null || "".equals(q)) {
                    return;
                }
                list.add(new Double(Double.parseDouble(q)).longValue());
            }
            list.add(0L);
            list.add(0L);
            list.add(0L);
            list.add(0L);
            list.add(0L);
            list.add(0L);
            list.add(0L);
            list.add(0L);
            list.add(0L);
            list.add(0L);
            list.add(0L);
            list.add(0L);
            FishingUtil.recOne1 = list.toArray(new Long[0]);
        }

        String recTwo1 = RedisHelper.get("recTwo1");
        // System.out.println("recTwo1*****************:" + recTwo1);
        if (recTwo1 != null && recTwo1.length() != 0) {
            recTwo1 = recTwo1.substring(recTwo1.lastIndexOf("[") + 1).replaceAll("]", "");
            String[] q1 = recTwo1.split(",");
            List<Long> list = new ArrayList<>();
            for (String q : q1) {
                // System.out.println(q);
                if (q == null || "".equals(q)) {
                    return;
                }
                list.add(new Double(Double.parseDouble(q)).longValue());
            }
            list.add(0L);
            list.add(0L);
            list.add(0L);
            list.add(0L);
            list.add(0L);
            list.add(0L);
            list.add(0L);
            list.add(0L);
            list.add(0L);
            list.add(0L);
            list.add(0L);
            list.add(0L);
            FishingUtil.recTwo1 = list.toArray(new Long[0]);
        }

        String burstOne1 = RedisHelper.get("burstOne1");
        // System.out.println("burstOne1*****************:" + burstOne1);
        if (burstOne1 != null && burstOne1.length() != 0) {
            burstOne1 = burstOne1.substring(burstOne1.lastIndexOf("[") + 1).replaceAll("]", "");
            String[] q1 = burstOne1.split(",");
            List<Long> list = new ArrayList<>();
            for (String q : q1) {
                // System.out.println(q);
                if (q == null || "".equals(q)) {
                    return;
                }
                list.add(new Double(Double.parseDouble(q)).longValue());
            }
            list.add(0L);
            list.add(0L);
            list.add(0L);
            list.add(0L);
            list.add(0L);
            list.add(0L);
            list.add(0L);
            list.add(0L);
            list.add(0L);
            list.add(0L);
            list.add(0L);
            list.add(0L);
            FishingUtil.burstOne1 = list.toArray(new Long[0]);
        }

        String burstTwo1 = RedisHelper.get("burstTwo1");
        // System.out.println("burstTwo1*****************:" + burstTwo1);
        if (burstTwo1 != null && burstTwo1.length() != 0) {
            burstTwo1 = burstTwo1.substring(burstTwo1.lastIndexOf("[") + 1).replaceAll("]", "");
            String[] q1 = burstTwo1.split(",");
            List<Long> list = new ArrayList<>();
            for (String q : q1) {
                // System.out.println(q);
                if (q == null || "".equals(q)) {
                    return;
                }
                list.add(new Double(Double.parseDouble(q)).longValue());
            }
            list.add(0L);
            list.add(0L);
            list.add(0L);
            list.add(0L);
            list.add(0L);
            list.add(0L);
            list.add(0L);
            list.add(0L);
            list.add(0L);
            list.add(0L);
            list.add(0L);
            list.add(0L);
            FishingUtil.burstTwo1 = list.toArray(new Long[0]);
        }

        String balanceOne1 = RedisHelper.get("balanceOne1");
        // System.out.println("balanceOne1*****************:" + balanceOne1);
        if (balanceOne1 != null && balanceOne1.length() != 0) {
            balanceOne1 = balanceOne1.substring(balanceOne1.lastIndexOf("[") + 1).replaceAll("]", "");
            String[] q1 = balanceOne1.split(",");
            List<Long> list = new ArrayList<>();
            for (String q : q1) {
                // System.out.println(q);
                if (q == null || "".equals(q)) {
                    return;
                }
                list.add(new Double(Double.parseDouble(q)).longValue());
            }
            list.add(0L);
            list.add(0L);
            list.add(0L);
            list.add(0L);
            list.add(0L);
            list.add(0L);
            list.add(0L);
            list.add(0L);
            list.add(0L);
            list.add(0L);
            list.add(0L);
            list.add(0L);
            FishingUtil.balanceOne1 = list.toArray(new Long[0]);
        }

        String balanceTwo1 = RedisHelper.get("balanceTwo1");
        // System.out.println("balanceTwo1*****************:" + balanceTwo1);
        if (balanceTwo1 != null && balanceTwo1.length() != 0) {
            balanceTwo1 = balanceTwo1.substring(balanceTwo1.lastIndexOf("[") + 1).replaceAll("]", "");
            String[] q1 = balanceTwo1.split(",");
            List<Long> list = new ArrayList<>();
            for (String q : q1) {
                // System.out.println(q);
                if (q == null || "".equals(q)) {
                    return;
                }
                list.add(new Double(Double.parseDouble(q)).longValue());
            }
            list.add(0L);
            list.add(0L);
            list.add(0L);
            list.add(0L);
            list.add(0L);
            list.add(0L);
            list.add(0L);
            list.add(0L);
            list.add(0L);
            list.add(0L);
            list.add(0L);
            list.add(0L);
            FishingUtil.balanceTwo1 = list.toArray(new Long[0]);
        }

        String recOne2 = RedisHelper.get("recOne2");
        // System.out.println("recOne2*****************:" + recOne2);
        if (recOne2 != null && recOne2.length() != 0) {
            recOne2 = recOne2.substring(recOne2.lastIndexOf("[") + 1).replaceAll("]", "");
            String[] q1 = recOne.split(",");
            List<Long> list = new ArrayList<>();
            for (String q : q1) {
                // System.out.println(q);
                if (q == null || "".equals(q)) {
                    return;
                }
                list.add(new Double(Double.parseDouble(q)).longValue());
            }
            list.add(0L);
            list.add(0L);
            list.add(0L);
            list.add(0L);
            list.add(0L);
            list.add(0L);
            list.add(0L);
            list.add(0L);
            list.add(0L);
            list.add(0L);
            list.add(0L);
            list.add(0L);
            FishingUtil.recOne2 = list.toArray(new Long[0]);
        }

        String recTwo2 = RedisHelper.get("recTwo2");
        // System.out.println("recTwo2*****************:" + recTwo2);
        if (recTwo2 != null && recTwo2.length() != 0) {
            recTwo2 = recTwo2.substring(recTwo2.lastIndexOf("[") + 1).replaceAll("]", "");
            String[] q1 = recTwo2.split(",");
            List<Long> list = new ArrayList<>();
            for (String q : q1) {
                // System.out.println(q);
                if (q == null || "".equals(q)) {
                    return;
                }
                list.add(new Double(Double.parseDouble(q)).longValue());
            }
            list.add(0L);
            list.add(0L);
            list.add(0L);
            list.add(0L);
            list.add(0L);
            list.add(0L);
            list.add(0L);
            list.add(0L);
            list.add(0L);
            list.add(0L);
            list.add(0L);
            list.add(0L);
            FishingUtil.recTwo2 = list.toArray(new Long[0]);
        }

        String burstOne2 = RedisHelper.get("burstOne2");
        // System.out.println("burstOne2*****************:" + burstOne2);
        if (burstOne2 != null && burstOne2.length() != 0) {
            burstOne2 = burstOne2.substring(burstOne2.lastIndexOf("[") + 1).replaceAll("]", "");
            String[] q1 = burstOne2.split(",");
            List<Long> list = new ArrayList<>();
            for (String q : q1) {
                // System.out.println(q);
                if (q == null || "".equals(q)) {
                    return;
                }
                list.add(new Double(Double.parseDouble(q)).longValue());
            }
            list.add(0L);
            list.add(0L);
            list.add(0L);
            list.add(0L);
            list.add(0L);
            list.add(0L);
            list.add(0L);
            list.add(0L);
            list.add(0L);
            list.add(0L);
            list.add(0L);
            list.add(0L);
            FishingUtil.burstOne2 = list.toArray(new Long[0]);
        }

        String burstTwo2 = RedisHelper.get("burstTwo2");
        // System.out.println("burstTwo2*****************:" + burstTwo2);
        if (burstTwo2 != null && burstTwo2.length() != 0) {
            burstTwo2 = burstTwo2.substring(burstTwo2.lastIndexOf("[") + 1).replaceAll("]", "");
            String[] q1 = burstTwo2.split(",");
            List<Long> list = new ArrayList<>();
            for (String q : q1) {
                // System.out.println(q);
                if (q == null || "".equals(q)) {
                    return;
                }
                list.add(new Double(Double.parseDouble(q)).longValue());
            }
            list.add(0L);
            list.add(0L);
            list.add(0L);
            list.add(0L);
            list.add(0L);
            list.add(0L);
            list.add(0L);
            list.add(0L);
            list.add(0L);
            list.add(0L);
            list.add(0L);
            list.add(0L);
            FishingUtil.burstTwo2 = list.toArray(new Long[0]);
        }

        String balanceOne2 = RedisHelper.get("balanceOne2");
        // System.out.println("balanceOne2*****************:" + balanceOne2);
        if (balanceOne2 != null && balanceOne2.length() != 0) {
            balanceOne2 = balanceOne2.substring(balanceOne2.lastIndexOf("[") + 1).replaceAll("]", "");
            String[] q1 = balanceOne2.split(",");
            List<Long> list = new ArrayList<>();
            for (String q : q1) {
                // System.out.println(q);
                if (q == null || "".equals(q)) {
                    return;
                }
                list.add(new Double(Double.parseDouble(q)).longValue());
            }
            list.add(0L);
            list.add(0L);
            list.add(0L);
            list.add(0L);
            list.add(0L);
            list.add(0L);
            list.add(0L);
            list.add(0L);
            list.add(0L);
            list.add(0L);
            list.add(0L);
            list.add(0L);
            FishingUtil.balanceOne2 = list.toArray(new Long[0]);
        }

        String balanceTwo2 = RedisHelper.get("balanceTwo2");
        // System.out.println("balanceTwo2*****************:" + balanceTwo2);
        if (balanceTwo2 != null && balanceTwo2.length() != 0) {
            balanceTwo2 = balanceTwo2.substring(balanceTwo2.lastIndexOf("[") + 1).replaceAll("]", "");
            String[] q1 = balanceTwo2.split(",");
            List<Long> list = new ArrayList<>();
            for (String q : q1) {
                // System.out.println(q);
                if (q == null || "".equals(q)) {
                    return;
                }
                list.add(new Double(Double.parseDouble(q)).longValue());
            }
            list.add(0L);
            list.add(0L);
            list.add(0L);
            list.add(0L);
            list.add(0L);
            list.add(0L);
            list.add(0L);
            list.add(0L);
            list.add(0L);
            list.add(0L);
            list.add(0L);
            list.add(0L);
            FishingUtil.balanceTwo2 = list.toArray(new Long[0]);
        }

        String recOne3 = RedisHelper.get("recOne3");
        // System.out.println("recOne3*****************:" + recOne3);
        if (recOne3 != null && recOne3.length() != 0) {
            recOne3 = recOne3.substring(recOne3.lastIndexOf("[") + 1).replaceAll("]", "");
            String[] q1 = recOne3.split(",");
            List<Long> list = new ArrayList<>();
            for (String q : q1) {
                // System.out.println(q);
                if (q == null || "".equals(q)) {
                    return;
                }
                list.add(new Double(Double.parseDouble(q)).longValue());
            }
            list.add(0L);
            list.add(0L);
            list.add(0L);
            list.add(0L);
            list.add(0L);
            list.add(0L);
            list.add(0L);
            list.add(0L);
            list.add(0L);
            list.add(0L);
            list.add(0L);
            list.add(0L);
            FishingUtil.recOne3 = list.toArray(new Long[0]);
        }

        String recTwo3 = RedisHelper.get("recTwo3");
        // System.out.println("recTwo3*****************:" + recTwo3);
        if (recTwo3 != null && recTwo3.length() != 0) {
            recTwo3 = recTwo3.substring(recTwo3.lastIndexOf("[") + 1).replaceAll("]", "");
            String[] q1 = recTwo3.split(",");
            List<Long> list = new ArrayList<>();
            for (String q : q1) {
                // System.out.println(q);
                if (q == null || "".equals(q)) {
                    return;
                }
                list.add(new Double(Double.parseDouble(q)).longValue());
            }
            list.add(0L);
            list.add(0L);
            list.add(0L);
            list.add(0L);
            list.add(0L);
            list.add(0L);
            list.add(0L);
            list.add(0L);
            list.add(0L);
            list.add(0L);
            list.add(0L);
            list.add(0L);
            FishingUtil.recTwo3 = list.toArray(new Long[0]);
        }

        String burstOne3 = RedisHelper.get("burstOne3");
        // System.out.println("burstOne3*****************:" + burstOne3);
        if (burstOne3 != null && burstOne3.length() != 0) {
            burstOne3 = burstOne3.substring(burstOne3.lastIndexOf("[") + 1).replaceAll("]", "");
            String[] q1 = burstOne3.split(",");
            List<Long> list = new ArrayList<>();
            for (String q : q1) {
                // System.out.println(q);
                if (q == null || "".equals(q)) {
                    return;
                }
                list.add(new Double(Double.parseDouble(q)).longValue());
            }
            list.add(0L);
            list.add(0L);
            list.add(0L);
            list.add(0L);
            list.add(0L);
            list.add(0L);
            list.add(0L);
            list.add(0L);
            list.add(0L);
            list.add(0L);
            list.add(0L);
            list.add(0L);
            FishingUtil.burstOne3 = list.toArray(new Long[0]);
        }

        String burstTwo3 = RedisHelper.get("burstTwo3");
        // System.out.println("burstTwo3*****************:" + burstTwo3);
        if (burstTwo3 != null && burstTwo3.length() != 0) {
            burstTwo3 = burstTwo3.substring(burstTwo3.lastIndexOf("[") + 1).replaceAll("]", "");
            String[] q1 = burstTwo3.split(",");
            List<Long> list = new ArrayList<>();
            for (String q : q1) {
                // System.out.println(q);
                if (q == null || "".equals(q)) {
                    return;
                }
                list.add(new Double(Double.parseDouble(q)).longValue());
            }
            list.add(0L);
            list.add(0L);
            list.add(0L);
            list.add(0L);
            list.add(0L);
            list.add(0L);
            list.add(0L);
            list.add(0L);
            list.add(0L);
            list.add(0L);
            list.add(0L);
            list.add(0L);
            FishingUtil.burstTwo3 = list.toArray(new Long[0]);
        }

        String balanceOne3 = RedisHelper.get("balanceOne3");
        // System.out.println("balanceOne3*****************:" + balanceOne3);
        if (balanceOne3 != null && balanceOne3.length() != 0) {
            balanceOne3 = balanceOne3.substring(balanceOne3.lastIndexOf("[") + 1).replaceAll("]", "");
            String[] q1 = balanceOne3.split(",");
            List<Long> list = new ArrayList<>();
            for (String q : q1) {
                // System.out.println(q);
                if (q == null || "".equals(q)) {
                    return;
                }
                list.add(new Double(Double.parseDouble(q)).longValue());
            }
            list.add(0L);
            list.add(0L);
            list.add(0L);
            list.add(0L);
            list.add(0L);
            list.add(0L);
            list.add(0L);
            list.add(0L);
            list.add(0L);
            list.add(0L);
            list.add(0L);
            list.add(0L);
            FishingUtil.balanceOne3 = list.toArray(new Long[0]);
        }

        String balanceTwo3 = RedisHelper.get("balanceTwo3");
        // System.out.println("balanceTwo3*****************:" + balanceTwo3);
        if (balanceTwo3 != null && balanceTwo3.length() != 0) {
            balanceTwo3 = balanceTwo3.substring(balanceTwo3.lastIndexOf("[") + 1).replaceAll("]", "");
            String[] q1 = balanceTwo3.split(",");
            List<Long> list = new ArrayList<>();
            for (String q : q1) {
                // System.out.println(q);
                if (q == null || "".equals(q)) {
                    return;
                }
                list.add(new Double(Double.parseDouble(q)).longValue());
            }
            list.add(0L);
            list.add(0L);
            list.add(0L);
            list.add(0L);
            list.add(0L);
            list.add(0L);
            list.add(0L);
            list.add(0L);
            list.add(0L);
            list.add(0L);
            list.add(0L);
            list.add(0L);
            FishingUtil.balanceTwo3 = list.toArray(new Long[0]);
        }

        String recOne4 = RedisHelper.get("recOne4");
        // System.out.println("recOne4*****************:" + recOne4);
        if (recOne4 != null && recOne4.length() != 0) {
            recOne4 = recOne4.substring(recOne4.lastIndexOf("[") + 1).replaceAll("]", "");
            String[] q1 = recOne4.split(",");
            List<Long> list = new ArrayList<>();
            for (String q : q1) {
                // System.out.println(q);
                if (q == null || "".equals(q)) {
                    return;
                }
                list.add(new Double(Double.parseDouble(q)).longValue());
            }
            list.add(0L);
            list.add(0L);
            list.add(0L);
            list.add(0L);
            list.add(0L);
            list.add(0L);
            list.add(0L);
            list.add(0L);
            list.add(0L);
            list.add(0L);
            list.add(0L);
            list.add(0L);
            FishingUtil.recOne4 = list.toArray(new Long[0]);
        }

        String recTwo4 = RedisHelper.get("recTwo4");
        // System.out.println("recTwo4*****************:" + recTwo4);
        if (recTwo4 != null && recTwo4.length() != 0) {
            recTwo4 = recTwo4.substring(recTwo4.lastIndexOf("[") + 1).replaceAll("]", "");
            String[] q1 = recTwo4.split(",");
            List<Long> list = new ArrayList<>();
            for (String q : q1) {
                // System.out.println(q);
                if (q == null || "".equals(q)) {
                    return;
                }
                list.add(new Double(Double.parseDouble(q)).longValue());
            }
            list.add(0L);
            list.add(0L);
            list.add(0L);
            list.add(0L);
            list.add(0L);
            list.add(0L);
            list.add(0L);
            list.add(0L);
            list.add(0L);
            list.add(0L);
            list.add(0L);
            list.add(0L);
            FishingUtil.recTwo4 = list.toArray(new Long[0]);
        }

        String burstOne4 = RedisHelper.get("burstOne4");
        // System.out.println("burstOne4*****************:" + burstOne4);
        if (burstOne4 != null && burstOne4.length() != 0) {
            burstOne4 = burstOne4.substring(burstOne4.lastIndexOf("[") + 1).replaceAll("]", "");
            String[] q1 = burstOne4.split(",");
            List<Long> list = new ArrayList<>();
            for (String q : q1) {
                // System.out.println(q);
                if (q == null || "".equals(q)) {
                    return;
                }
                list.add(new Double(Double.parseDouble(q)).longValue());
            }
            list.add(0L);
            list.add(0L);
            list.add(0L);
            list.add(0L);
            list.add(0L);
            list.add(0L);
            list.add(0L);
            list.add(0L);
            list.add(0L);
            list.add(0L);
            list.add(0L);
            list.add(0L);
            FishingUtil.burstOne4 = list.toArray(new Long[0]);
        }

        String burstTwo4 = RedisHelper.get("burstTwo4");
        // System.out.println("burstTwo4*****************:" + burstTwo4);
        if (burstTwo4 != null && burstTwo4.length() != 0) {
            burstTwo4 = burstTwo4.substring(burstTwo4.lastIndexOf("[") + 1).replaceAll("]", "");
            String[] q1 = burstTwo4.split(",");
            List<Long> list = new ArrayList<>();
            for (String q : q1) {
                // System.out.println(q);
                if (q == null || "".equals(q)) {
                    return;
                }
                list.add(new Double(Double.parseDouble(q)).longValue());
            }
            list.add(0L);
            list.add(0L);
            list.add(0L);
            list.add(0L);
            list.add(0L);
            list.add(0L);
            list.add(0L);
            list.add(0L);
            list.add(0L);
            list.add(0L);
            list.add(0L);
            list.add(0L);
            FishingUtil.burstTwo4 = list.toArray(new Long[0]);
        }

        String balanceOne4 = RedisHelper.get("balanceOne4");
        // System.out.println("balanceOne4*****************:" + balanceOne4);
        if (balanceOne4 != null && balanceOne4.length() != 0) {
            balanceOne4 = balanceOne4.substring(balanceOne4.lastIndexOf("[") + 1).replaceAll("]", "");
            String[] q1 = balanceOne4.split(",");
            List<Long> list = new ArrayList<>();
            for (String q : q1) {
                // System.out.println(q);
                if (q == null || "".equals(q)) {
                    return;
                }
                list.add(new Double(Double.parseDouble(q)).longValue());
            }
            list.add(0L);
            list.add(0L);
            list.add(0L);
            list.add(0L);
            list.add(0L);
            list.add(0L);
            list.add(0L);
            list.add(0L);
            list.add(0L);
            list.add(0L);
            list.add(0L);
            list.add(0L);
            FishingUtil.balanceOne4 = list.toArray(new Long[0]);
        }

        String balanceTwo4 = RedisHelper.get("balanceTwo4");
        // System.out.println("balanceTwo4*****************:" + balanceTwo4);
        if (balanceTwo4 != null && balanceTwo4.length() != 0) {
            balanceTwo4 = balanceTwo4.substring(balanceTwo4.lastIndexOf("[") + 1).replaceAll("]", "");
            String[] q1 = balanceTwo4.split(",");
            List<Long> list = new ArrayList<>();
            for (String q : q1) {
                // System.out.println(q);
                if (q == null || "".equals(q)) {
                    return;
                }
                list.add(new Double(Double.parseDouble(q)).longValue());
            }
            list.add(0L);
            list.add(0L);
            list.add(0L);
            list.add(0L);
            list.add(0L);
            list.add(0L);
            list.add(0L);
            list.add(0L);
            list.add(0L);
            list.add(0L);
            list.add(0L);
            list.add(0L);
            FishingUtil.balanceTwo4 = list.toArray(new Long[0]);
        }

        Map<String,
                String> settings = RedisHelper.get(Arrays.asList("Fishing:Setting:Pool:InitPool",
                "Fishing:Setting:Pool:Pool", "Fishing:Setting:Pool:Prob", "Fishing:Setting:Pool:UnitProb",
                "Fishing:Setting:Pool:PerUnitMoney", "Fishing:Setting:Daily:Prob", "Fishing:Setting:Daily:Limit",
                "Fishing:Setting:Total:Prob", "Fishing:Setting:Total:Limit", "Fishing:Setting:BlackRoom:Prob",
                "Fishing:Setting:BlackRoom:Limit", "Fishing:Setting:Greener:Limit"));
        for (Entry<String, String> setting : settings.entrySet()) {
            if (setting.getValue() == null) {
                continue;
            }
            switch (setting.getKey()) {
                case "Fishing:Setting:Pool:InitPool":
                    FISHING_INIT_POOL = GsonManager.gson.fromJson(setting.getValue(), long[][].class);
                    break;
                case "Fishing:Setting:Pool:Pool":
                    FISHING_POOL = GsonManager.gson.fromJson(setting.getValue(), long[][].class);
                    break;
                case "Fishing:Setting:Pool:Prob":
                    FISHING_PROB = GsonManager.gson.fromJson(setting.getValue(), double[][].class);
                    break;
                case "Fishing:Setting:Pool:UnitProb":
                    FISHING_UNIT_PROB = GsonManager.gson.fromJson(setting.getValue(), double[][].class);
                    break;
                case "Fishing:Setting:Pool:PerUnitMoney":
                    FISHING_PER_UNIT_MONEY = GsonManager.gson.fromJson(setting.getValue(), long[][].class);
                    break;
                case "Fishing:Setting:Daily:Prob":
                    PLAYER_DAILY_PROB = GsonManager.gson.fromJson(setting.getValue(), double[][][].class);
                    break;
                case "Fishing:Setting:Daily:Limit":
                    PLAYER_DAILY_LIMIT = GsonManager.gson.fromJson(setting.getValue(), long[][][].class);
                    break;
                case "Fishing:Setting:Total:Prob":
                    PLAYER_TOTAL_PROB = GsonManager.gson.fromJson(setting.getValue(), double[][][].class);
                    break;
                case "Fishing:Setting:Total:Limit":
                    PLAYER_TOTAL_LIMIT = GsonManager.gson.fromJson(setting.getValue(), long[][][].class);
                    break;
                case "Fishing:Setting:BlackRoom:Prob":
                    BLACK_ROOM_PROB = GsonManager.gson.fromJson(setting.getValue(), double[][].class);
                    break;
                case "Fishing:Setting:BlackRoom:Limit":
                    BLACK_ROOM_LIMIT = GsonManager.gson.fromJson(setting.getValue(), long[][].class);
                    break;
                case "Fishing:Setting:Greener:Limit":
                    GREENER_LIMIT = Integer.parseInt(setting.getValue());
                    break;
            }
        }

        String prod = RedisHelper.get("prod");
        if (prod != null && prod.length() != 0) {
            prod = prod.substring(prod.lastIndexOf("[") + 1).replaceAll("]", "");
            String[] p1 = prod.split(",");
            FishingHitDataManager.FISHING_CUT_PROB[1][4] = Double.parseDouble(p1[1]);
            FishingHitDataManager.FISHING_CUT_PROB[1][5] = Double.parseDouble(p1[2]);
            FishingHitDataManager.FISHING_CUT_PROB[1][6] = Double.parseDouble(p1[3]);
            // FishingHitDataManager.FISHING_CUT_PROB[1][7] = Double.parseDouble(p1[11]);
        }
        // if(prod!=null&&prod.length()!=0){
        // prod = prod.substring(prod.lastIndexOf("[")+1).replaceAll("]","");
        // String[] p1 = prod.split(",");
        // FishingHitDataManager.FISHING_CUT_PROB[1][4] = Double.parseDouble(p1[1]);
        // FishingHitDataManager.FISHING_CUT_PROB[1][5] = Double.parseDouble(p1[2]);
        // FishingHitDataManager.FISHING_CUT_PROB[1][6] = Double.parseDouble(p1[3]);
        //// FishingHitDataManager.FISHING_CUT_PROB[1][7] = Double.parseDouble(p1[11]);
        // }

    }

    // *****************************************************************************************

    /**
     * 设置捕鱼配置
     */
    public static void setFishingConfig(int greener, int index, Map<String, Object> config) {
        // FishingHitDataManager.FISHING_PROB[greener][index] = (double) config.get("fishingProb");
        // FishingHitDataManager.FISHING_PER_UNIT_MONEY[greener][index] = (long) (double) config.get("unitMoney");
        // FishingHitDataManager.BLACK_ROOM_PROB[greener][index] = (double) config.get("blackRoomProb");
        // FishingHitDataManager.BLACK_ROOM_LIMIT[greener][index] = (long) (double) config.get("blackRoomLimit");
        // FishingHitDataManager.PLAYER_TOTAL_PROB[greener][index][1] = (double) config.get("totalWinProb");
        // FishingHitDataManager.PLAYER_TOTAL_LIMIT[greener][index][1] = (long) (double) config.get("totalWinLimit");
        // FishingHitDataManager.PLAYER_TOTAL_PROB[greener][index][0] = (double) config.get("totalLoseProb");
        // FishingHitDataManager.PLAYER_TOTAL_LIMIT[greener][index][0] = (long) (double) config.get("totalLoseLimit");
        // FishingHitDataManager.PLAYER_DAILY_PROB[greener][index][1] = (double) config.get("dailyWinProb");
        // FishingHitDataManager.PLAYER_DAILY_LIMIT[greener][index][1] = (long) (double) config.get("dailyWinLimit");
        // FishingHitDataManager.PLAYER_DAILY_PROB[greener][index][0] = (double) config.get("dailyLoseProb");
        // FishingHitDataManager.PLAYER_DAILY_LIMIT[greener][index][0] = (long) (double) config.get("dailyLoseLimit");
        FishingHitDataManager.FISHING_CUT_PROB[greener][index] = (double) config.get("cutProb");

        // long initPoolMoney = Math.round((double) config.get("initPoolMoney"));
        // if (FishingHitDataManager.FISHING_INIT_POOL[greener][index] != initPoolMoney) {
        // FishingHitDataManager.FISHING_POOL[greener][index] = initPoolMoney;
        // }
        // FishingHitDataManager.FISHING_INIT_POOL[greener][index] = initPoolMoney;
    }

    /**
     * 设置捕鱼配置
     */
    public static Map<String, Object> getFishingConfig(int greener, int index) {
        Map<String, Object> fishConfig = new HashMap<>();
        fishConfig.put("fishingProb", FishingHitDataManager.FISHING_PROB[greener][index]);
        fishConfig.put("unitMoney", FishingHitDataManager.FISHING_PER_UNIT_MONEY[greener][index]);
        fishConfig.put("blackRoomProb", FishingHitDataManager.BLACK_ROOM_PROB[greener][index]);
        fishConfig.put("blackRoomLimit", FishingHitDataManager.BLACK_ROOM_LIMIT[greener][index]);
        fishConfig.put("totalWinProb", FishingHitDataManager.PLAYER_TOTAL_PROB[greener][index][1]);
        fishConfig.put("totalWinLimit", FishingHitDataManager.PLAYER_TOTAL_LIMIT[greener][index][1]);
        fishConfig.put("totalLoseProb", FishingHitDataManager.PLAYER_TOTAL_PROB[greener][index][0]);
        fishConfig.put("totalLoseLimit", FishingHitDataManager.PLAYER_TOTAL_LIMIT[greener][index][0]);
        fishConfig.put("dailyWinProb", FishingHitDataManager.PLAYER_DAILY_PROB[greener][index][1]);
        fishConfig.put("dailyWinLimit", FishingHitDataManager.PLAYER_DAILY_LIMIT[greener][index][1]);
        fishConfig.put("dailyLoseProb", FishingHitDataManager.PLAYER_DAILY_PROB[greener][index][0]);
        fishConfig.put("dailyLoseLimit", FishingHitDataManager.PLAYER_DAILY_LIMIT[greener][index][0]);
        fishConfig.put("cutProb", FishingHitDataManager.FISHING_CUT_PROB[greener][index]);
        fishConfig.put("initPoolMoney", FishingHitDataManager.FISHING_INIT_POOL[greener][index]);
        return fishConfig;
    }

    // *****************************************************************************************

    // /**
    // * 5秒一次储存任务
    // */
    // @Scheduled(initialDelay = 8000, fixedDelay = 10000)
    // public void hitDataSaver() {
    // RedisHelper.set("Fishing:Setting:Greener:Limit", Integer.toString(GREENER_LIMIT));
    // for (Entry<Long, Map<Integer, Long>> totalWinEntry : TOTAL_WIN_MAP.entrySet()) {
    // String key = String.format("Fishing:TotalWin:%d", totalWinEntry.getKey());
    // RedisHelper.set(key, new Gson().toJson(totalWinEntry.getValue()));
    // }
    // for (Entry<Long, Map<Integer, Long>> blackRoomEntry : BLACK_ROOM_MAP.entrySet()) {
    // String key = String.format("Fishing:BlackRoom:%d", blackRoomEntry.getKey());
    // RedisHelper.set(key, new Gson().toJson(blackRoomEntry.getValue()));
    // }
    // for (Entry<Long, Double> playerFishingProb : PLAYER_FISHING_PROB_MAP.entrySet()) {
    // String key = String.format("Fishing:PlayerFishingProb:%d", playerFishingProb.getKey());
    // RedisHelper.set(key, Double.toString(playerFishingProb.getValue()));
    // }
    // // 捕鱼挑战赛参数
    // for (Entry<Long, Map<Integer, Long>> totalWinEntry : CHALLENGE_TOTAL_WIN_MAP.entrySet()) {
    // String key = String.format("Fishing:TotalWinChallenge:%d", totalWinEntry.getKey());
    // RedisHelper.set(key, new Gson().toJson(totalWinEntry.getValue()));
    // }
    // for (Entry<Long, Map<Integer, Long>> blackRoomEntry : CHALLENGE_BLACK_ROOM_MAP.entrySet()) {
    // String key = String.format("Fishing:BlackRoomChallenge:%d", blackRoomEntry.getKey());
    // RedisHelper.set(key, new Gson().toJson(blackRoomEntry.getValue()));
    // }
    // // for (Entry<Long, Double> playerFishingProb : CHALLENGE_PLAYER_FISHING_PROB_MAP.entrySet()) {
    // // String key = String.format("Fishing:PlayerFishingProbChallenge:%d", playerFishingProb.getKey());
    // // RedisHelper.set(key, Double.toString(playerFishingProb.getValue()));
    // // }
    //
    // RedisHelper.set("Fishing:Setting:Pool:InitPool", GsonManager.gson.toJson(FISHING_INIT_POOL));
    // RedisHelper.set("Fishing:Setting:Pool:Pool", GsonManager.gson.toJson(FISHING_POOL));
    // RedisHelper.set("Fishing:Setting:Pool:Prob", GsonManager.gson.toJson(FISHING_PROB));
    // RedisHelper.set("Fishing:Setting:Pool:UnitProb", GsonManager.gson.toJson(FISHING_UNIT_PROB));
    // RedisHelper.set("Fishing:Setting:Pool:PerUnitMoney", GsonManager.gson.toJson(FISHING_PER_UNIT_MONEY));
    // RedisHelper.set("Fishing:Setting:Daily:Prob", GsonManager.gson.toJson(PLAYER_DAILY_PROB));
    // RedisHelper.set("Fishing:Setting:Daily:Limit", GsonManager.gson.toJson(PLAYER_DAILY_LIMIT));
    // RedisHelper.set("Fishing:Setting:Total:Prob", GsonManager.gson.toJson(PLAYER_TOTAL_PROB));
    // RedisHelper.set("Fishing:Setting:Total:Limit", GsonManager.gson.toJson(PLAYER_TOTAL_LIMIT));
    // RedisHelper.set("Fishing:Setting:BlackRoom:Prob", GsonManager.gson.toJson(BLACK_ROOM_PROB));
    // RedisHelper.set("Fishing:Setting:BlackRoom:Limit", GsonManager.gson.toJson(BLACK_ROOM_LIMIT));
    // }

    /**
     * 每天0点一次清空任务
     */
    @Scheduled(cron = "0 0 0 * * ?")
    public void zeroTimeWorker() {
        logger.info("执行零点捕鱼数据清空任务");
        DAILY_WIN_MAP = new ConcurrentHashMap<>();
        TOTAL_WIN_MAP = new ConcurrentHashMap<>();
        BLACK_ROOM_MAP = new ConcurrentHashMap<>();
        PLAYER_FISHING_PROB_MAP = new ConcurrentHashMap<>();
        // 捕鱼挑战赛
        CHALLENGE_DAILY_WIN_MAP = new ConcurrentHashMap<>();
        CHALLENGE_TOTAL_WIN_MAP = new ConcurrentHashMap<>();
        CHALLENGE_BLACK_ROOM_MAP = new ConcurrentHashMap<>();
        // CHALLENGE_PLAYER_FISHING_PROB_MAP = new ConcurrentHashMap<>();
    }

    // ******************************** 挑战赛相关参数操作 ********************************

    /**
     * 捕鱼挑战赛每日赢取金币表
     */
    private static Map<Long, Map<Integer, Long>> CHALLENGE_DAILY_WIN_MAP = new ConcurrentHashMap<>();

    /**
     * 捕鱼挑战赛总赢取金币表
     */
    public static Map<Long, Map<Integer, Long>> CHALLENGE_TOTAL_WIN_MAP = new ConcurrentHashMap<>();

    /**
     * 捕鱼挑战赛小黑屋玩家表
     */
    public static Map<Long, Map<Integer, Long>> CHALLENGE_BLACK_ROOM_MAP = new ConcurrentHashMap<>();

    // /**
    // * 捕鱼挑战赛玩家系数表
    // */
    // private static Map<Long, Double> CHALLENGE_PLAYER_FISHING_PROB_MAP = new ConcurrentHashMap<>();

    /**
     * 获取捕鱼挑战赛玩家日常输赢表
     */
    public static Map<Integer, Long> getChallengePlayerDailyMap(long playerId) {
        if (!CHALLENGE_DAILY_WIN_MAP.containsKey(playerId)) {
            CHALLENGE_DAILY_WIN_MAP.put(playerId, new HashMap<>());
        }
        return CHALLENGE_DAILY_WIN_MAP.get(playerId);
    }

    /**
     * 获取捕鱼挑战赛玩家总输赢表
     */
    public static Map<Integer, Long> getChallengePlayerTotalMap(long playerId) {
        if (!CHALLENGE_TOTAL_WIN_MAP.containsKey(playerId)) {
            String key = String.format("Fishing:TotalWinChallenge:%d", playerId);
            String value = RedisHelper.get(key);
            if (!StringUtils.isEmpty(value)) {
                Map<Integer, Long> blackMap = new Gson().fromJson(value, getType());
                CHALLENGE_TOTAL_WIN_MAP.put(playerId, blackMap);
            } else {
                CHALLENGE_TOTAL_WIN_MAP.put(playerId, new HashMap<>());
            }
        }
        return CHALLENGE_TOTAL_WIN_MAP.get(playerId);
    }

    /**
     * 获取捕鱼挑战赛玩家小黑屋表
     */
    public static Map<Integer, Long> getChallengePlayerBlackMap(long playerId) {
        if (!CHALLENGE_BLACK_ROOM_MAP.containsKey(playerId)) {
            String key = String.format("Fishing:BlackRoomChallenge:%d", playerId);
            String value = RedisHelper.get(key);
            if (!StringUtils.isEmpty(value)) {
                Map<Integer, Long> blackMap = new Gson().fromJson(value, getType());
                CHALLENGE_BLACK_ROOM_MAP.put(playerId, blackMap);
            } else {
                CHALLENGE_BLACK_ROOM_MAP.put(playerId, new HashMap<>());
            }
        }
        return CHALLENGE_BLACK_ROOM_MAP.get(playerId);
    }

    /**
     * 获取捕鱼挑战赛每日输赢金币
     */
    public static long getChallengeDailyWin(long playerId, int index) {
        return getChallengePlayerDailyMap(playerId).getOrDefault(index, 0L);
    }

    /**
     * 增加捕鱼挑战赛每日输赢金币
     */
    public static void addChallengeDailyWin(long playerId, int index, long addMoney) {
        Map<Integer, Long> dailyMap = getChallengePlayerDailyMap(playerId);
        synchronized (dailyMap) {
            dailyMap.put(index, dailyMap.getOrDefault(index, 0L) + addMoney);
        }
    }

    /**
     * 获取捕鱼挑战赛小黑屋金币
     */
    public static long getChallengeBlackRoom(long playerId, int index) {
        return getChallengePlayerBlackMap(playerId).getOrDefault(index, 0L);
    }

    /**
     * 增加捕鱼挑战赛小黑屋金币
     */
    public static void setChallengeBlackRoom(long playerId, int index, long addMoney) {
        Map<Integer, Long> blackMap = getChallengePlayerBlackMap(playerId);
        blackMap.put(index, addMoney);
    }

    /**
     * 获取捕鱼挑战赛总输赢金币
     */
    public static long getChallengeTotalWin(long playerId, int index) {
        return getChallengePlayerTotalMap(playerId).getOrDefault(index, 0L);
    }

    /**
     * 增加捕鱼挑战赛总输赢金币
     */
    public static void addChallengeTotalWin(long playerId, int index, long addMoney) {
        Map<Integer, Long> totalMap = getChallengePlayerTotalMap(playerId);
        synchronized (totalMap) {
            totalMap.put(index, totalMap.getOrDefault(index, 0L) + addMoney);
        }
    }

    /**
     * 增加捕鱼挑战赛总输赢金币
     */
    public static void setChallengeTotalWin(long playerId, int index, long addMoney) {
        Map<Integer, Long> totalMap = getChallengePlayerTotalMap(playerId);
        synchronized (totalMap) {
            totalMap.put(index, addMoney);
        }
    }

}
