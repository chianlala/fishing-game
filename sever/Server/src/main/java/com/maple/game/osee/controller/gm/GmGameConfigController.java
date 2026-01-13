package com.maple.game.osee.controller.gm;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.text.StrBuilder;
import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import com.google.gson.Gson;
import com.maple.database.config.redis.RedisHelper;
import com.maple.database.data.mapper.UserMapper;
import com.maple.engine.anotation.GmController;
import com.maple.engine.anotation.GmHandler;
import com.maple.engine.container.UserContainer;
import com.maple.engine.data.ServerUser;
import com.maple.engine.manager.GsonManager;
import com.maple.game.osee.common.RedisUtil;
import com.maple.game.osee.controller.gm.base.GmBaseController;
import com.maple.game.osee.dao.data.entity.OseePlayerEntity;
import com.maple.game.osee.dao.data.mapper.OseePlayerMapper;
import com.maple.game.osee.dao.log.entity.AppGameLogEntity;
import com.maple.game.osee.dao.log.entity.AppRankLogEntity;
import com.maple.game.osee.dao.log.entity.AppRewardLogEntity;
import com.maple.game.osee.dao.log.mapper.*;
import com.maple.game.osee.entity.GameEnum;
import com.maple.game.osee.entity.NewBaseGamePlayer;
import com.maple.game.osee.entity.NewBaseGameRoom;
import com.maple.game.osee.entity.fishing.FishingGameRoom;
import com.maple.game.osee.entity.fishing.challenge.FishingChallengeRoom;
import com.maple.game.osee.entity.fishing.csv.file.FishCcxxConfig;
import com.maple.game.osee.entity.fishing.grandprix.FishingGrandPrixRoom;
import com.maple.game.osee.entity.gm.CommonResponse;
import com.maple.game.osee.manager.PlayerManager;
import com.maple.game.osee.manager.fishing.FishingChallengeManager;
import com.maple.game.osee.manager.fishing.FishingGrandPrixManager;
import com.maple.game.osee.manager.fishing.FishingHitDataManager;
import com.maple.game.osee.manager.fishing.FishingManager;
import com.maple.game.osee.manager.fishing.util.FishingUtil;
import com.maple.game.osee.model.bo.BdzConfigBO;
import com.maple.game.osee.model.dto.ProfitRatioDTO;
import com.maple.game.osee.model.entity.AccountDetailDO;
import com.maple.game.osee.model.entity.TblProfitRatioLogDO;
import com.maple.game.osee.model.enums.AccountDetailTypeEnum;
import com.maple.game.osee.timer.fishing.ProfitRatioLogTask;
import com.maple.game.osee.util.*;
import com.maple.gamebase.container.GameContainer;
import com.maple.gamebase.data.BaseGamePlayer;
import com.maple.gamebase.data.BaseGameRoom;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.redisson.api.*;
import org.redisson.codec.JsonJacksonCodec;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Stream;

import static com.maple.game.osee.manager.fishing.FishingHitDataManager.*;
import static com.maple.game.osee.util.FishingChallengeFightFishUtil.getKsFzByYkType;

/**
 * 后台游戏设置控制器
 */
@GmController
@Slf4j
public class GmGameConfigController extends GmBaseController {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private OseePlayerMapper playerMapper;

    @Autowired
    private OseeCutMoneyLogMapper cutMoneyLogMapper;

    @Autowired
    private OseeExpendLogMapper expendLogMapper;

    @Autowired
    private AppRankLogMapper rankLogMapper;

    @Autowired
    private AppGameLogMapper gameLogMapper;

    @Autowired
    private OseeFruitRecordLogMapper fruitRecordLogMapper;

    /**
     * 收益比范围设置
     */
    @GmHandler(key = "/osee/game/ai/profitRatioSave")
    public void profitRatioSave(Map<String, Object> params, CommonResponse response) {

        ProfitRatioDTO dto = BeanUtil.copyProperties(params, ProfitRatioDTO.class);

        // 设置：TP
        // Double[] tp1Arr = dto.getTp1Arr();
        // RedisHelper.set("tp_c", JSONUtil.toJsonStr(tp1Arr));
        // for (int i = 0; i < tp1Arr.length; i++) {
        // FishingUtil.tp_c[i] = tp1Arr[i];
        // }
        //
        // Double[] tp2Arr = dto.getTp2Arr();
        // RedisHelper.set("tp_z", JSONUtil.toJsonStr(tp2Arr));
        // for (int i = 0; i < tp2Arr.length; i++) {
        // FishingUtil.tp_z[i] = tp2Arr[i];
        // }

        RBatch batch = redissonClient.createBatch();

        ProfitRatioDTO profitRatioDTO = FishingTUtil.getProfitRatioDTO();

        Set<Integer> resetTxIndexSet = new HashSet<>();

        // 判断值，是否修改过
        for (int i = 0; i < profitRatioDTO.getTxMinArr().length; i++) {
            if (profitRatioDTO.getTxMinArr()[i] != dto.getTxMinArr()[i]) {
                resetTxIndexSet.add(i);
            }
        }

        // 判断值，是否修改过
        for (int i = 0; i < profitRatioDTO.getTxMaxArr().length; i++) {
            if (profitRatioDTO.getTxMaxArr()[i] != dto.getTxMaxArr()[i]) {
                resetTxIndexSet.add(i);
            }
        }

        for (Integer item : resetTxIndexSet) {
            FishingChallengeManager.getRedisTxValue(true, item, dto); // 重置 tx值
        }

        // 取一个新的范围值，并设置过期时间
        for (int i = 0; i < dto.getRoomIndex().length; i++) {

            int roomIndex = dto.getRoomIndex()[i];

            batch.getBucket("profitRatioNextValue:" + roomIndex).deleteAsync(); // 移除：下次收益比

            batch.getBucket("redisProfitRatioValue:" + roomIndex).deleteAsync(); // 移除：当前收益比

        }

        batch.execute(); // 执行批量操作

        FishingTUtil.setProfitRatioDTO(dto); // 设置：ProfitRatioDTO对象


//        RMap<String, Object> redissonClientMap = redissonClient.getMap(GM_GAME_AI_INFO); // 取缓存
        // 收益比范围
//        redissonClientMap.put("profitRatioDTO", FishingTUtil.getProfitRatioDTO());

    }

    /**
     * bdz设置
     */
    @GmHandler(key = "/osee/game/ai/bdzSave")
    public void bdzSave(Map<String, Object> paramMap, CommonResponse response) {

        FishingChallengeFightFishUtil.setBdzConfigBO(BeanUtil.copyProperties(paramMap, BdzConfigBO.class));

//        RMap<String, Object> redissonClientMap = redissonClient.getMap(GM_GAME_AI_INFO); // 取缓存
//        redissonClientMap.put("bdzConfigBO", FishingChallengeFightFishUtil.getCommonBdzConfigBO());  // 设置缓存

    }

    private static RedissonClient redissonClient;

    @Resource
    public void setRedissonClient(RedissonClient redissonClient) {
        GmGameConfigController.redissonClient = redissonClient;
    }

    /**
     * 收益比控制数据清零
     */
    @GmHandler(key = "/osee/game/ai/profitRatioClear")
    public void profitRatioClear(Map<String, Object> params, CommonResponse response) {

        String roomIndexStr = MapUtil.getStr(params, "roomIndex");

        Integer roomIndex = Convert.toInt(roomIndexStr);

        if (roomIndex == null) {

            response.setSuccess(false);
            response.setErrMsg("参数校验错误");
            return;

        }

        RBatch batch = redissonClient.createBatch();

        batch.getAtomicLong("ALL_USED_XH_TOTAL_STR:" + roomIndex).deleteAsync();
        batch.getAtomicLong("ALL_PRODUCE_GOLD_TOTAL_STR:" + roomIndex).deleteAsync();

        batch.getAtomicLong("ALL_USED_XH_TOTAL_DYNAMIC_STR:" + roomIndex).deleteAsync();
        batch.getAtomicLong("ALL_PRODUCE_GOLD_TOTAL_DYNAMIC_STR:" + roomIndex).deleteAsync();

        batch.execute(); // 执行批量操作

        FishingChallengeFightFishUtil.getDynamicMultipleFishPrizePoolAtomicLong(roomIndex).delete();

        // FishingChallengeManager.getQfkzbfmbAtomicDouble(type - 1).delete(); // 移除：QFKZBFMB

    }

    public static String GM_GAME_AI_INFO = "GM_GAME_AI_INFO"; // 获取机器人配置信息 存在Redis中

    /**
     * 获取服务器ai设置
     */
    @SuppressWarnings("unchecked")
    @GmHandler(key = "/osee/game/ai/info")
    public void doGameAiInfoTask(Map<String, Object> params, CommonResponse response) {

//        RMap<String, Object> redissonClientMap = redissonClient.getMap(GM_GAME_AI_INFO); // 取缓存
//        if (ObjectUtils.isNotEmpty(redissonClientMap)){
//            response.setData(redissonClientMap);
//            return;
//        }

        Map<String, Object> resultMap = (Map<String, Object>) response.getData();

        Map<String, Object> fishConfig2 = new HashMap<>();
        fishConfig2.put("bm", RedisUtil.val("Fishing:GrandPrixRobot:BM1", "1000000"));
        fishConfig2.put("startTime", RedisUtil.val("Fishing:GrandPrixRobot:startTime", "10:00"));
        fishConfig2.put("endTime", RedisUtil.val("Fishing:GrandPrixRobot:endTime", "20:00"));

        fishConfig2.put("cxjl", RedisHelper.redissonClient.getBucket("GAME:USER:CHALLANGE:cxjl").get());
        fishConfig2.put("cxfz", RedisHelper.redissonClient.getBucket("GAME:USER:CHALLANGE:cxfz").get());
        resultMap.put("fishingGrandPrixRobot", fishConfig2);

        Map<String, Object> totalConfig = new HashMap<>();
        totalConfig.put("isOpen", RedisUtil.val("ALL_IS_OPEN", 1d).longValue());
        totalConfig.put("createTime", RedisUtil.val("ALL_OPEN_CREATETIME", ""));

        totalConfig.put("totalMoney", RedisUtil.val("ALL_QXH_USER_CHALLANGE", 0D).longValue());
        totalConfig.put("totalMoney1", RedisUtil.val("ALL_QXH_USER_CHALLANGE:1", 0D).longValue());
        totalConfig.put("totalMoney2", RedisUtil.val("ALL_QXH_USER_CHALLANGE:2", 0D).longValue());
        totalConfig.put("totalMoney3", RedisUtil.val("ALL_QXH_USER_CHALLANGE:3", 0D).longValue());
        totalConfig.put("totalMoney4", RedisUtil.val("ALL_QXH_USER_CHALLANGE:4", 0D).longValue());
        totalConfig.put("totalMoney5", RedisUtil.val("ALL_QXH_USER_CHALLANGE:5", 0D).longValue());

        totalConfig.put("qxz", RedisUtil.val("ALL_QXZ_USER_CHALLANGE", 0D).longValue());
        totalConfig.put("qxz1", RedisUtil.val("ALL_QXZ_USER_CHALLANGE:1", 0D).longValue());
        totalConfig.put("qxz2", RedisUtil.val("ALL_QXZ_USER_CHALLANGE:2", 0D).longValue());
        totalConfig.put("qxz3", RedisUtil.val("ALL_QXZ_USER_CHALLANGE:3", 0D).longValue());
        totalConfig.put("qxz4", RedisUtil.val("ALL_QXZ_USER_CHALLANGE:4", 0D).longValue());
        totalConfig.put("qxz5", RedisUtil.val("ALL_QXZ_USER_CHALLANGE:5", 0D).longValue());

        totalConfig.put("m", RedisUtil.val("GAME:CONFIG:CHALLANGE:M", 0D));
        totalConfig.put("kzlx", RedisUtil.val("GAME:CONFIG:CHALLANGE:KZLX", 0D).intValue());

        totalConfig.put("zblx", RedisUtil.val("user:status:cfg:zblx", 1D));
        totalConfig.put("zbls", RedisUtil.val("user:status:cfg:zbls", 1D));
        totalConfig.put("tg0", RedisUtil.val("user:status:cfg:tg0", 0.87D));
        totalConfig.put("tg1", RedisUtil.val("user:status:cfg:tg1", 0.95D));
        totalConfig.put("tg5", RedisUtil.val("user:status:cfg:tg5", 1D));
        totalConfig.put("tg10", RedisUtil.val("user:status:cfg:tg10", 1.05D));
        totalConfig.put("tg11", RedisUtil.val("user:status:cfg:tg11", 1.2D));
        totalConfig.put("mgjs", RedisUtil.val("user:status:cfg:mgjs", 0.005D));
        totalConfig.put("phx1", RedisUtil.val("user:status:cfg:phx1", 8D).intValue());
        totalConfig.put("phx2", RedisUtil.val("user:status:cfg:phx2", 15D).intValue());
        totalConfig.put("gls1", RedisUtil.val("user:status:cfg:gls1", 1D));
        totalConfig.put("glx1", RedisUtil.val("user:status:cfg:glx1", 0.5D));
        totalConfig.put("cp1a", RedisUtil.val("user:status:cfg:cp1a", 6D).intValue());
        totalConfig.put("cp1b", RedisUtil.val("user:status:cfg:cp1b", 10D).intValue());
        totalConfig.put("gls2", RedisUtil.val("user:status:cfg:gls2", 1D));
        totalConfig.put("glx2", RedisUtil.val("user:status:cfg:glx2", 0D));
        totalConfig.put("cp2a", RedisUtil.val("user:status:cfg:cp2a", 8D).intValue());
        totalConfig.put("cp2b", RedisUtil.val("user:status:cfg:cp2b", 15D).intValue());

        // JBS JBS_TJ 等输赢上下限控制参数
        Stream.of("jbs", "lbs", "jhs", "lhs").forEach(k -> {
            totalConfig.put(k, 0);
            totalConfig.put(k + "Tj", 0);
        });
        final RMap<String, Number> sykzMap =
                RedisHelper.redissonClient.getMap("GAME:CONFIG:CHALLANGE:SYKZ", new JsonJacksonCodec());
        totalConfig.putAll(sykzMap.readAllMap());


        resultMap.put("fishingTotal", totalConfig);

        // 收益比范围
        resultMap.put("profitRatioDTO", FishingTUtil.getProfitRatioDTO());

        // bdz
        resultMap.put("bdzConfigBO", FishingChallengeFightFishUtil.getCommonBdzConfigBO());

//        redissonClientMap.readAllMap();
//        // 存缓存
//        redissonClientMap.putAll(resultMap);
    }

    /**
     * 修改捕鱼ai设置
     */
    @GmHandler(key = "/osee/game/ai/fishing/update")
    public void doGameAiFishingUpdateTask(Map<String, Object> params, CommonResponse response) {
        int minRefresh = (int) (double) params.get("minRefresh");
        int maxRefresh = (int) (double) params.get("maxRefresh");
        int minDisappear = (int) (double) params.get("minDisappear");
        int maxDisappear = (int) (double) params.get("maxDisappear");

        if (maxRefresh < minRefresh || maxDisappear < minDisappear) {

            response.setSuccess(false);
            response.setErrMsg("数据不正确");
            return;

        }

    }

    /**
     * 修改捕鱼ai设置
     */
    @GmHandler(key = "/osee/game/ai/fishingGrandPrix/update/bm")
    public void doGameAiFishingGrandPrixUpdateBmTask(Map<String, Object> params, CommonResponse response) {
        // Long bm = new BigDecimal(params.get("bm").toString()).longValue();
        if (params.containsKey("bm")) {
            RedisHelper.set("Fishing:GrandPrixRobot:BM1", params.get("bm").toString());
        }
    }

    @GmHandler(key = "/osee/game/ai/fishingGrandPrix/update/grandPrixTime")
    public void doGameAiFishingGrandPrixTimeTask(Map<String, Object> params, CommonResponse response) {

        if (params.containsKey("startTime")) {
            RedisHelper.set("Fishing:GrandPrixRobot:startTime", params.get("startTime").toString());
        }

        if (params.containsKey("endTime")) {
            RedisHelper.set("Fishing:GrandPrixRobot:endTime", params.get("endTime").toString());
        }

    }

    @GmHandler(key = "/osee/game/ai/fishingGrandPrix/update/cxfz")
    public void doGameAiFishingGrandPrixUpdateCxfz(Map<String, Object> params, CommonResponse response) {
        // Long bm = new BigDecimal(params.get("bm").toString()).longValue();
        if (params.containsKey("cxfz")) {
            RedisHelper.redissonClient.getBucket("GAME:USER:CHALLANGE:cxfz")
                    .set(new BigDecimal(params.get("cxfz").toString()).longValue());
        }
    }

    @GmHandler(key = "/osee/game/ai/fishingGrandPrix/update/cxjl")
    public void doGameAiFishingGrandPrixUpdateCxjl(Map<String, Object> params, CommonResponse response) {
        if (params.containsKey("cxjl")) {
            RedisHelper.redissonClient.getBucket("GAME:USER:CHALLANGE:cxjl")
                    .set(new BigDecimal(params.get("cxjl").toString()).intValue());
        }
    }

    /**
     * 修改捕鱼ai设置
     */
    @GmHandler(key = "/osee/game/ai/fishingGrandPrix/update")
    public void doGameAiFishingGrandPrixUpdateTask(Map<String, Object> params, CommonResponse response) {


        int minRefresh = (int) (double) params.get("minRefresh");
        int maxRefresh = (int) (double) params.get("maxRefresh");
        int minDisappear = (int) (double) params.get("minDisappear");
        int maxDisappear = (int) (double) params.get("maxDisappear");
        if (maxRefresh < minRefresh || maxDisappear < minDisappear) {
            response.setSuccess(false);
            response.setErrMsg("数据不正确");
            return;
        }

    }

    /**
     * 设置龙晶场QXH
     */
    @GmHandler(key = "/osee/game/ai/totalMoney/update2")
    public void doGameAiTwoTotalMoneyUpdateTask2(Map<String, String> paramMap, CommonResponse response) {

        if (paramMap != null) {
            for (Map.Entry<String, String> item : paramMap.entrySet()) {
                RedisHelper.set("ALL_QXH_USER_CHALLANGE:" + item.getKey(), item.getValue()); // QXH
                return;
            }
        }

    }

    /**
     * 修改龙晶场库存设置
     */
    @GmHandler(key = "/osee/game/ai/totalMoney/update")
    public void doGameAiTwoTotalMoneyUpdateTask(Map<String, Object> params, CommonResponse response) {
        RedisHelper.set("ALL_IS_OPEN", String.valueOf(params.get("isOpen")));
        RedisHelper.set("ALL_OPEN_CREATETIME", String.valueOf(params.get("createTime")));

        // RedisHelper.set("ALL_QXH_USER_CHALLANGE", String.valueOf(params.get("totalMoney")));//QXH
        // RedisHelper.set("ALL_QXH_USER_CHALLANGE:1", String.valueOf(params.get("totalMoney1")));
        // RedisHelper.set("ALL_QXH_USER_CHALLANGE:2", String.valueOf(params.get("totalMoney2")));
        // RedisHelper.set("ALL_QXH_USER_CHALLANGE:3", String.valueOf(params.get("totalMoney3")));
        // RedisHelper.set("ALL_QXH_USER_CHALLANGE:4", String.valueOf(params.get("totalMoney4")));
        // RedisHelper.set("ALL_QXH_USER_CHALLANGE:5", String.valueOf(params.get("totalMoney5")));

        RedisHelper.set("ALL_QXZ_USER_CHALLANGE", String.valueOf(params.get("qxz")));
        RedisHelper.set("ALL_QXZ_USER_CHALLANGE:1", String.valueOf(params.get("qxz1")));
        RedisHelper.set("ALL_QXZ_USER_CHALLANGE:2", String.valueOf(params.get("qxz2")));
        RedisHelper.set("ALL_QXZ_USER_CHALLANGE:3", String.valueOf(params.get("qxz3")));
        RedisHelper.set("ALL_QXZ_USER_CHALLANGE:4", String.valueOf(params.get("qxz4")));
        RedisHelper.set("ALL_QXZ_USER_CHALLANGE:5", String.valueOf(params.get("qxz5")));

        // RedisHelper.set("GAME:CONFIG:CHALLANGE:M", String.valueOf(params.get("m")));
        RedisHelper.redissonClient.getBucket("GAME:CONFIG:CHALLANGE:M").set(params.get("m"));
        // JBS JBS_TJ 等输赢上下限控制参数
        final RMap<String, Number> sykzMap =
                RedisHelper.redissonClient.getMap("GAME:CONFIG:CHALLANGE:SYKZ", new JsonJacksonCodec());
        Stream.of("jbs", "lbs", "jhs", "lhs").filter(k -> params.get(k) != null).forEach(k -> {
            sykzMap.put(k, new BigDecimal(String.valueOf(params.get(k))));
            sykzMap.put(k + "Tj", new BigDecimal(String.valueOf(params.get(k + "Tj"))));
        });

        // 清除T
        // RedisHelper.removePattern("USER_FISHTYPE_[1234]_T_CHALLANGE*");
        Stream.of(1, 2, 3, 4).forEach(type -> {
            RedisHelper.redissonClient.getMap("GAME:USER:T;CHALLANGE;" + type, new JsonJacksonCodec()).clear();
        });
    }

    /**
     * 修改龙晶场库存设置
     */
    @GmHandler(key = "/osee/game/ai/totalMoney/update/kzlx")
    public void doGameAiTwoTotalMoneyUpdateKzlxTask(Map<String, Object> params, CommonResponse response) {

        RedisHelper.set("GAME:CONFIG:CHALLANGE:KZLX", String.valueOf(params.get("kzlx")));

        RedisHelper.set("user:status:cfg:zblx", String.valueOf(params.get("zblx")));
        RedisHelper.set("user:status:cfg:zbls", String.valueOf(params.get("zbls")));
        RedisHelper.set("user:status:cfg:tg0", String.valueOf(params.get("tg0")));
        RedisHelper.set("user:status:cfg:tg1", String.valueOf(params.get("tg1")));
        RedisHelper.set("user:status:cfg:tg5", String.valueOf(params.get("tg5")));
        RedisHelper.set("user:status:cfg:tg10", String.valueOf(params.get("tg10")));
        RedisHelper.set("user:status:cfg:tg11", String.valueOf(params.get("tg11")));
        RedisHelper.set("user:status:cfg:mgjs", String.valueOf(params.get("mgjs")));
        RedisHelper.set("user:status:cfg:phx1", String.valueOf(params.get("phx1")));
        RedisHelper.set("user:status:cfg:phx2", String.valueOf(params.get("phx2")));
        RedisHelper.set("user:status:cfg:gls1", String.valueOf(params.get("gls1")));
        RedisHelper.set("user:status:cfg:glx1", String.valueOf(params.get("glx1")));
        RedisHelper.set("user:status:cfg:cp1a", String.valueOf(params.get("cp1a")));
        RedisHelper.set("user:status:cfg:cp1b", String.valueOf(params.get("cp1b")));
        RedisHelper.set("user:status:cfg:gls2", String.valueOf(params.get("gls2")));
        RedisHelper.set("user:status:cfg:glx2", String.valueOf(params.get("glx2")));
        RedisHelper.set("user:status:cfg:cp2a", String.valueOf(params.get("cp2a")));
        RedisHelper.set("user:status:cfg:cp2b", String.valueOf(params.get("cp2b")));

    }

    /**
     * 获取服务器统计记录
     */
    @SuppressWarnings("unchecked")
    @GmHandler(key = "/osee/server/statistics")
    public void doServerStatisticsTask(Map<String, Object> params, CommonResponse response) {
        Map<String, Object> resultMap = (Map<String, Object>) response.getData();

        Map<String, Object> statisticsMap = playerMapper.getGmStatistics();
        Map<String, Object> skillMap = playerMapper.getAllSkill();
        // List<Long> idList = playerMapper.getAllUserId();
        final Long all_jc_user_challange_1_small = RedisUtil.val("ALL_JC_USER_CHALLANGE_1_SMALL", 0L);
        final Long all_jc_user_challange_2_small = RedisUtil.val("ALL_JC_USER_CHALLANGE_2_SMALL", 0L);
        final Long all_jc_user_challange_3_small = RedisUtil.val("ALL_JC_USER_CHALLANGE_3_SMALL", 0L);
        final Long all_jc_user_challange_4_small = RedisUtil.val("ALL_JC_USER_CHALLANGE_4_SMALL", 0L);
        final Long all_jc_user_challange_5_small = RedisUtil.val("ALL_JC_USER_CHALLANGE_5_SMALL", 0L);
        resultMap.put("fishChallengeLittleReword1", all_jc_user_challange_1_small);
        resultMap.put("fishChallengeLittleReword2", all_jc_user_challange_2_small);
        resultMap.put("fishChallengeLittleReword3", all_jc_user_challange_3_small);
        resultMap.put("fishChallengeLittleReword4", all_jc_user_challange_4_small);
        resultMap.put("fishChallengeLittleReword5", all_jc_user_challange_5_small);
        resultMap.put("fishChallengeLittleRewordAll", all_jc_user_challange_1_small + all_jc_user_challange_2_small
                + all_jc_user_challange_3_small + all_jc_user_challange_4_small + all_jc_user_challange_5_small);

        resultMap.put("playerMoney", statisticsMap.get("money")); // 全服携带金币统计
        resultMap.put("bankMoney", statisticsMap.get("bankMoney")); // 全服保险箱金币统计
        resultMap.put("playerLj", statisticsMap.get("dragonCrystal"));
        resultMap.put("goldMoney", RedisUtil.val("USER_USED_TORPEDO_MONEY_4", 0L)); // 全服保险箱金币统计
        resultMap.put("lockAll", skillMap.get("lockAll"));
        resultMap.put("magicAll", skillMap.get("magicAll"));
        resultMap.put("bossAll", skillMap.get("bossAll"));
        resultMap.put("diamondAll", skillMap.get("diamondAll"));
        resultMap.put("yuGuAll", skillMap.get("yuGuAll"));
        resultMap.put("haiYaoShiAll", skillMap.get("haiYaoShiAll"));
        resultMap.put("wangHunShiAll", skillMap.get("wangHunShiAll"));
        resultMap.put("haiHunShiAll", skillMap.get("haiHunShiAll"));
        resultMap.put("zhenZhuShiAll", skillMap.get("zhenZhuShiAll"));
        resultMap.put("haiShouShiAll", skillMap.get("haiShouShiAll"));
        resultMap.put("haiMoShiAll", skillMap.get("haiMoShiAll"));
        resultMap.put("zhaoHuanShiAll", skillMap.get("zhaoHuanShiAll"));
        resultMap.put("dianChiShiAll", skillMap.get("dianChiShiAll"));
        resultMap.put("heiDongShiAll", skillMap.get("heiDongShiAll"));
        resultMap.put("lingZhuShiAll", skillMap.get("lingZhuShiAll"));
        resultMap.put("longGuAll", skillMap.get("longGuAll"));
        resultMap.put("longZhuAll", skillMap.get("longZhuAll"));
        resultMap.put("longYuanAll", skillMap.get("longYuanAll"));
        resultMap.put("longJiAll", skillMap.get("longJiAll"));
        resultMap.put("skillBlackHoleAll", skillMap.get("skillBlackHoleAll"));
        resultMap.put("skillTorpedoAll", skillMap.get("skillTorpedoAll"));
        resultMap.put("skillBitAll", skillMap.get("skillBitAll"));
        resultMap.put("lotteryAll", skillMap.get("lotteryAll"));
        resultMap.put("blackBulletAll", skillMap.get("blackBulletAll"));
        resultMap.put("bronzeBulletAll", skillMap.get("bronzeBulletAll"));
        resultMap.put("silverBulletAll", skillMap.get("silverBulletAll"));
        resultMap.put("goldBulletAll", skillMap.get("goldBulletAll"));
        resultMap.put("skillEleAll", skillMap.get("skillEleAll"));
        resultMap.put("rareTorpedoAll", skillMap.get("rareTorpedoAll"));
        resultMap.put("rareTorpedoBangAll", skillMap.get("rareTorpedoBangAll"));
        resultMap.put("goldTorpedoBangAll", skillMap.get("goldTorpedoBangAll"));
        resultMap.put("goldTorpedoAll", skillMap.get("goldTorpedoAll"));
        resultMap.put("critAll", skillMap.get("critAll"));
        resultMap.put("frozenAll", skillMap.get("frozenAll"));
        resultMap.put("goldAll",
                Long.valueOf(String.valueOf(statisticsMap.get("money"))) / 500000
                        + Long.valueOf(String.valueOf(statisticsMap.get("dragonCrystal"))) / 500000
                        + Long.valueOf(String.valueOf(skillMap.get("goldAll"))));
        resultMap.put("goldTorpedo", Long.valueOf(String.valueOf(statisticsMap.get("goldTorpedo"))));
        long fishPoolAll = 0;// 累加计算全服捕鱼库存
        for (long[] pools : FishingHitDataManager.FISHING_POOL) {
            // 除去龙晶战场的库存
            for (int i = 0; i < pools.length - 3; i++) {
                long pool = pools[i];
                fishPoolAll += pool;
            }
        }

        // 捕鱼抽水总额
        long fishCutAll = cutMoneyLogMapper.getTotalCutMoney(GameEnum.FISHING.getId());
        // 大奖赛总库存
        if ("".equals(RedisHelper.get("player:grandprix:config:stock"))) {
            long fishGrandPrixPoolAll = 0L;
            resultMap.put("fishGrandPrixPoolAll", fishGrandPrixPoolAll);
        } else {
            long fishGrandPrixPoolAll = Long.valueOf(RedisHelper.get("player:grandprix:config:stock"));
            resultMap.put("fishGrandPrixPoolAll", fishGrandPrixPoolAll);
        }

        // 龙晶战场的库存总额
        double fishChallengePoolAll = RedisUtil.val("ALL_QXH_USER_CHALLANGE", 0D);

        // 水果拉霸抽水总额
        long fruitCutMoney = cutMoneyLogMapper.getTotalCutMoney(GameEnum.FRUIT_LABA.getId());
        resultMap.put("fruitCutMoney", fruitCutMoney);
        resultMap.put("nowTime", System.currentTimeMillis());// 当前时间
        resultMap.put("expendMoney", expendLogMapper.getTotalExpendMoney()); // 支出总数
        // resultMap.put("cutMoney", fishCutAll
        // + cutMoneyLogMapper.getTotalCutMoney(GameEnum.ERBA_GAME.getId())
        // + cutMoneyLogMapper.getTotalCutMoney(GameEnum.GOBANG.getId())); // 金币抽水总数(捕鱼+二八杠+五子棋)
        resultMap.put("totalPlayer", userMapper.getUserCount()); // 注册人数
        resultMap.put("onlinePlayer", userMapper.getOnlineUserCount()); // 在线人数

        List<Long> list = playerMapper.getAllPlayer();
        long all = 0L;
        for (Long l : list) {
            all += RedisUtil.val("USER_T_BANKRUPTCY_NUMBER" + l, 0L);
        }
        resultMap.put("personalAll", all); // 全服个人产出库存统计

        resultMap.put("diamionAll", RedisUtil.val("USER_T_BUY_NUMBER_ALL", 0L)); // 全服钻石产出库存统计

        resultMap.put("goldUseAll", RedisUtil.val("USER_T_BANKRUPTCY_NUMBER_ALL", 0L)); // 全服弹头产出库存统计

        // 捕鱼库存
        resultMap.put("fishPoolAll", fishPoolAll); // 捕鱼全服库存统计

        // 捕鱼抽水总额
        resultMap.put("fishCutAll", fishCutAll);
        List<Object> fishPools = new ArrayList<>();
        for (int i = 0; i < 8; i++) {
            int index = i % 4;
            int greener = i / 4;
            Map<String, Object> fishPool = new HashMap<>();
            fishPool.put("currentPool", FishingHitDataManager.FISHING_POOL[greener][index]);
            fishPools.add(fishPool);
        }

        Map<String, Object> fishPool = new HashMap<>();
        fishPool.put("currentPool", FishingHitDataManager.FISHING_POOL[0][4]);
        fishPools.add(fishPool);
        Map<String, Object> fishPool1 = new HashMap<>();
        fishPool1.put("currentPool", FishingHitDataManager.FISHING_POOL[1][7]);
        fishPools.add(fishPool1);
        resultMap.put("fishPool", fishPools);

        // 捕鱼鱼雷掉落/使用数据
        resultMap.putAll(FishingManager.TORPEDO_RECORD);

        // 龙晶战场库存总额
        resultMap.put("fishChallengePoolAll", fishChallengePoolAll);

        ProfitRatioDTO profitRatioDTO = FishingTUtil.getProfitRatioDTO();

        List<Long> produceGoldTotalList = new ArrayList<>(); // 每个房间 产出的 金币L，固定倍数
        List<Long> usedXhTotalList = new ArrayList<>(); // 每个房间的 子弹消耗，固定倍数

        List<Long> produceGoldTotalDynamicList = new ArrayList<>(); // 每个房间 产出的 金币L，动态倍数
        List<Long> usedXhTotalDynamicList = new ArrayList<>(); // 每个房间的 子弹消耗，动态倍数

        List<BigDecimal> profitRatioHopeList = new ArrayList<>(); // 每个房间的 期望收益比

        List<BigDecimal> profitRatioRealList = new ArrayList<>(); // 每个房间的 实际收益比，固定倍数

        List<BigDecimal> profitRatioRealDynamicList = new ArrayList<>(); // 每个房间的 实际收益比，动态倍数

        List<Integer> profitRatioFrequencyList = new ArrayList<>(); // 每个房间的 收益比变化频率

        List<String> profitRatioCountdownList = new ArrayList<>(); // 每个房间的 收益比变化倒计时
        List<BigDecimal> profitRatioNextValueList = new ArrayList<>(); // 每个房间的 下次收益比

        List<BigDecimal> getGoldHopeList = new ArrayList<>(); // 每个房间的 期望收益金币，固定倍数
        List<BigDecimal> getGoldRealList = new ArrayList<>(); // 每个房间的 实际收益金币，固定倍数

        List<BigDecimal> getGoldHopeDynamicList = new ArrayList<>(); // 每个房间的 期望收益金币，动态倍数
        List<BigDecimal> getGoldRealDynamicList = new ArrayList<>(); // 每个房间的 实际收益金币，动态倍数

        List<Long> dynamicPrizePoolList = new ArrayList<>(); // 每个场次的 动态奖池

        List<String> showSessionNameList = new ArrayList<>(); // 每个场次的 展示的场次名称
        List<Integer> openList = new ArrayList<>(); // 每个场次的 是否启用：0 否 1 是
        List<Integer> sessionIdList = new ArrayList<>(); // 每个场次的场次 id

        int ccxxConfigSize = MyRefreshFishingUtil.CHALLENGE_AND_INTEGRAL_FISHING_CCXX_CONFIG_LIST.size();

        for (int i = 0; i < ccxxConfigSize; i++) {

            FishCcxxConfig fishCcxxConfig =
                    MyRefreshFishingUtil.CHALLENGE_AND_INTEGRAL_FISHING_CCXX_CONFIG_LIST.get(i);

            Integer roomIndex = fishCcxxConfig.getSessionId();

            int index = profitRatioDTO.getIndexByRoomIndex(roomIndex);

            showSessionNameList.add(fishCcxxConfig.getShowSessionName());
            openList.add(fishCcxxConfig.getOpen());
            sessionIdList.add(roomIndex);

            // 处理：收益比，基础的集合，固定倍数
            FishingHelper.handlerProfitRatioBaseList(produceGoldTotalList, usedXhTotalList, profitRatioHopeList,
                    profitRatioRealList, roomIndex);

            // 处理：收益比，基础的集合，动态倍数
            FishingHelper.handlerProfitRatioBaseDynamicList(produceGoldTotalDynamicList, usedXhTotalDynamicList,
                    profitRatioRealDynamicList, roomIndex);

            profitRatioFrequencyList.add(profitRatioDTO.getFrequencyArr()[index]);

            // profitRatioCountdownList.add(FishingTUtil.getCountdown(i)); // 设置：收益比变化倒计时
            profitRatioCountdownList.add("-1"); // 设置：收益比变化倒计时
            // profitRatioNextValueList.add(FishingTUtil.getProfitRatioNextValue(i)); // 设置：下次收益比
            profitRatioNextValueList.add(BigDecimal.valueOf(-1)); // 设置：下次收益比

            getGoldHopeList.add(new BigDecimal(usedXhTotalList.get(i))
                    .multiply(BigDecimal.ONE.subtract(profitRatioHopeList.get(i))).setScale(5, BigDecimal.ROUND_HALF_DOWN));

            getGoldRealList.add(new BigDecimal(usedXhTotalList.get(i))
                    .subtract(new BigDecimal(produceGoldTotalList.get(i))).setScale(5, BigDecimal.ROUND_HALF_DOWN));

            getGoldHopeDynamicList.add(new BigDecimal(usedXhTotalDynamicList.get(i))
                    .multiply(BigDecimal.ONE.subtract(profitRatioHopeList.get(i))).setScale(5, BigDecimal.ROUND_HALF_DOWN));

            getGoldRealDynamicList.add(new BigDecimal(usedXhTotalDynamicList.get(i))
                    .subtract(new BigDecimal(produceGoldTotalDynamicList.get(i))).setScale(5, BigDecimal.ROUND_HALF_DOWN));

            // 追加
            dynamicPrizePoolList
                    .add(FishingChallengeFightFishUtil.getDynamicMultipleFishPrizePoolAtomicLong(roomIndex).get());

            resultMap.put("fishChallengeBigReword" + (i + 1), getGoldRealList.get(i).add(getGoldRealDynamicList.get(i))
                    .subtract(getGoldHopeList.get(i)).subtract(getGoldHopeDynamicList.get(i)));

        }

        resultMap.put("produceGoldTotalList", produceGoldTotalList);
        resultMap.put("usedXhTotalList", usedXhTotalList);

        resultMap.put("produceGoldTotalDynamicList", produceGoldTotalDynamicList);
        resultMap.put("usedXhTotalDynamicList", usedXhTotalDynamicList);

        resultMap.put("profitRatioHopeList", profitRatioHopeList);

        resultMap.put("profitRatioRealList", profitRatioRealList);

        resultMap.put("profitRatioRealDynamicList", profitRatioRealDynamicList);

        // resultMap.put("resetNumList", resetNumList);
        resultMap.put("profitRatioFrequencyList", profitRatioFrequencyList);

        resultMap.put("profitRatioCountdownList", profitRatioCountdownList);
        resultMap.put("profitRatioNextValueList", profitRatioNextValueList);

        resultMap.put("getGoldHopeList", getGoldHopeList);
        resultMap.put("getGoldRealList", getGoldRealList);

        resultMap.put("getGoldHopeDynamicList", getGoldHopeDynamicList);
        resultMap.put("getGoldRealDynamicList", getGoldRealDynamicList);

        // resultMap.put("tTypeEnumNameList", tTypeEnumNameList);
        // resultMap.put("dsyjbPbList", dsyjbPbList);
        // resultMap.put("txList", txList);
        // resultMap.put("qfkzbfmbList", qfkzbfmbList);

        resultMap.put("dynamicPrizePoolList", dynamicPrizePoolList);

        // 追加
        // resultMap.put("currentProduceGoldTotalList", currentProduceGoldTotalList);
        // resultMap.put("currentUsedXhTotalList", currentUsedXhTotalList);
        // resultMap.put("currentProfitRatioRealList", currentProfitRatioRealList);
        // resultMap.put("currentGetGoldHopeList", currentGetGoldHopeList);
        // resultMap.put("currentGetGoldRealList", currentGetGoldRealList);

        resultMap.put("showSessionNameList", showSessionNameList);
        resultMap.put("openList", openList);
        resultMap.put("sessionIdList", sessionIdList);

        // log.info("showSessionNameList：{}", showSessionNameList);
        //
        // log.info("sessionIdList：{}", sessionIdList);
        //
        // log.info("openList：{}", openList);

        resultMap.put("fishChallengeBigRewordAll", 0L);

        // 龙晶场在线人数
        List<Integer> fishingChallengeOnlineList = new ArrayList<>();

        // 龙晶场机器人总数
        List<Integer> fishingChallengeRobotNumberList = new ArrayList<>();

        for (int i = 0; i < ccxxConfigSize; i++) {

            fishingChallengeOnlineList.add(0);

            fishingChallengeRobotNumberList.add(0);

        }

        // 分别统计各处在线人数
        for (BaseGameRoom room : GameContainer.getGameRooms()) {

            if (room instanceof FishingGameRoom) { // 捕鱼房间
                String key = "fishingOnline_" + ((FishingGameRoom) room).getRoomIndex();

                for (BaseGamePlayer player : room.getGamePlayers()) {
                    if (player != null && player.getUser().isOnline()) {
                        resultMap.put(key, ((int) resultMap.getOrDefault(key, 0)) + 1);
                    }
                }
            } else if (room instanceof FishingChallengeRoom) { // 捕鱼挑战赛房间人数

                for (BaseGamePlayer item : room.getGamePlayers()) {

                    NewBaseGamePlayer player = (NewBaseGamePlayer) item;

                    if (item != null && item.getUser().isOnline()) {

                        int index = profitRatioDTO.getIndexByRoomIndex(((NewBaseGameRoom) room).getRoomIndex());

                        Integer number = fishingChallengeOnlineList.get(index);

                        number = number + 1;

                        fishingChallengeOnlineList.set(index, number);

                    }

                }

            } else if (room instanceof FishingGrandPrixRoom) { // 大奖赛房间人数

                String key = "fishingGrandPrixOnline";

                String robotKey = "fishingGrandPrixRobotNumber";

                for (BaseGamePlayer player : room.getGamePlayers()) {

                    if (player != null && player.getUser() != null && player.getUser().isOnline()) {

                        resultMap.put(key, ((int) resultMap.getOrDefault(key, 0)) + 1);

                    }

                }

            }

        }

        // log.info("fishingChallengeOnlineList：{}", fishingChallengeOnlineList);

        resultMap.put("fishingChallengeOnlineList", fishingChallengeOnlineList);

        resultMap.put("fishingChallengeRobotNumberList", fishingChallengeRobotNumberList);

        // // 水果拉霸在线人数
        // int labaOnline = FruitLaBaManager.fruitRoomUser.size();
        // resultMap.put("labaOnline", labaOnline);
        //
        // int labaOnline1 = 0;
        // int labaOnline2 = 0;
        // int labaOnline3 = 0;
        // for (Map.Entry<Long, FruitlabaPlayer> entry : FruitLaBaManager.fruitplayers.entrySet()) {
        // if (entry.getValue().getRoomType() == 1) {
        // labaOnline1++;
        // } else if (entry.getValue().getRoomType() == 2) {
        // labaOnline2++;
        // } else if (entry.getValue().getRoomType() == 3) {
        // labaOnline3++;
        // }
        // }
        // resultMap.put("labaOnline1", labaOnline1);
        // resultMap.put("labaOnline2", labaOnline2);
        // resultMap.put("labaOnline3", labaOnline3);
        // resultMap.put("rewardTotal1", FruitLaBaManager.rewardTotal1);
        // resultMap.put("rewardTotal2", FruitLaBaManager.rewardTotal2);
        // resultMap.put("rewardTotal3", FruitLaBaManager.rewardTotal3);

        // 当前金币：
        // resultMap.put("erbaDailyMoney", TwoEightManager.getTERobotMoney(TwoEightConfig.RedisTwoEightDailyMoney));

        // 当日总金币：
        // long allMoney = TwoEightManager.getTERobotMoney(TwoEightConfig.RedisTwoEightDailyMoney);
        // TODO 加上其他游戏金币

    }


    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class LineHistoryListItem {

        private String money;

        /**
         * 精确到毫米的时间
         */
        private String createTimeMs;

    }

    /**
     * 获取玩家预测节点历史记录
     */
    @GmHandler(key = "/osee/server/monitor/playerHistoryData")
    public void getServerMonitorPlayerHistoryData(Map<String, Object> paramMap, CommonResponse response) {

        Map<String, Object> resultMap = (Map<String, Object>) response.getData();

        long userId = MapUtil.getLong(paramMap, "userId");

        ServerUser serverUser = UserContainer.getUserById(userId);

        if (serverUser == null) {

            return;

        }

        StrBuilder strBuilder = StrBuilder.create();

        String ctBeginTime = MapUtil.getStr(paramMap, "ctBeginTime");
        String ctEndTime = MapUtil.getStr(paramMap, "ctEndTime");

        if (StrUtil.isNotBlank(ctBeginTime)) {

            strBuilder.append(" AND create_ms >= ").append(DateUtil.parse(ctBeginTime).getTime()).append(" ");

        } else {

            // 默认：显示最近一天的数据
            strBuilder.append(" AND create_ms >= ").append(DateUtil.offsetHour(new Date(), -2).getTime()).append(" ");

        }

        if (StrUtil.isNotBlank(ctEndTime)) {

            strBuilder.append(" AND create_ms <= ").append(DateUtil.parse(ctEndTime).getTime()).append(" ");

        }

        int nodeChangeValue = AccountDetailTypeEnum.NODE_CHANGE.getValue();


        List<LineHistoryListItem> lineHistoryList = new ArrayList<>();

        TreeMap<Long, AccountDetailDO> treeMap = new TreeMap<>();

        BigDecimal tenThousand = BigDecimal.valueOf(10000);

        // 备注：不循环最后一个元素
        resultMap.put("lineHistoryList", lineHistoryList);

    }

    /**
     * 获取玩家预测节点
     */
    @GmHandler(key = "/osee/server/monitor/playerData")
    public void getServerMonitorPlayerData(Map<String, Object> paramMap, CommonResponse response) {

        Map<String, Object> resultMap = (Map<String, Object>) response.getData();

        long userId = MapUtil.getLong(paramMap, "userId");

        ServerUser serverUser = UserContainer.getUserById(userId);

        if (serverUser == null) {

            return;

        }

        List<Double> jczd0List = redissonClient
                .<Double>getList(FishingChallengeFightFishUtil.FISHING_JCZD0_LIST_USER_PRE + userId).readAll();

        if (CollUtil.isEmpty(jczd0List)) {

            return;

        }

        // 期望子弹数
        RBucket<Double> jczd0Bucket =
                redissonClient.getBucket(FishingChallengeFightFishUtil.FISHING_JCZD0_USER_PRE + userId);

        if (!jczd0Bucket.isExists()) {

            return;

        }

        int bdfz = jczd0List.size() - 1;

        List<LineListItem> lineList = new ArrayList<>();

        String personalJczd0ListBatteryLevel = redissonClient.<String>getBucket(
                FishingChallengeFightFishUtil.FISHING_PERSONAL_JCZD0_LIST_BATTERY_LEVEL_USER_PRE + userId).get();

        BigDecimal batteryLevelBigDecimal;

        long batteryLevel;

        NewBaseGamePlayer player = null;

        BaseGamePlayer basePlayer = GameContainer.getPlayerById(userId);

        if (basePlayer instanceof NewBaseGamePlayer) {

            player = (NewBaseGamePlayer) basePlayer;

        }

        // 初始金币数
        double chuShiMoney;

        long currentMoney = PlayerManager.getPlayerEntity(serverUser).getDragonCrystal(); // 当前的钱

        if (StrUtil.isBlank(personalJczd0ListBatteryLevel)) { // 如果没有个控

            if (player == null) {
                return;
            }

            batteryLevel = player.getBatteryLevel();

            batteryLevelBigDecimal = BigDecimal.valueOf(batteryLevel);

            chuShiMoney = redissonClient
                    .getAtomicDouble(FishingChallengeFightFishUtil.FISHING_CHU_SHI_MONEY_USER_PRE + userId).get();

        } else { // 如果有个控

            batteryLevelBigDecimal = new BigDecimal(personalJczd0ListBatteryLevel);

            if (player == null) {

                batteryLevel = batteryLevelBigDecimal.intValue();

            } else {

                batteryLevel = player.getBatteryLevel();

            }

            // 获取：暂存的初始金币
            chuShiMoney = redissonClient
                    .getAtomicDouble(FishingChallengeFightFishUtil.FISHING_PERSONAL_CHU_SHI_MONEY_USER_PRE + userId).get();

            // 需要额外处理一下钱
            currentMoney = (long) (currentMoney
                    + redissonClient.getAtomicDouble(FishingChallengeFightFishUtil.FISHING_GRETJ_USER_PRE + userId).get());

        }

        String chuShiMoneyStr = BigDecimal.valueOf(chuShiMoney).toPlainString();

        lineList.add(new LineListItem("起始金币", chuShiMoneyStr));

        // 完成的节点数
        long bd = redissonClient.getAtomicLong(FishingChallengeFightFishUtil.FISHING_BD_USER_PRE + userId).get();

        // log.info("batteryLevelBigDecimal：{}", batteryLevelBigDecimal.toPlainString());

        for (int i = 0; i < jczd0List.size(); i++) {

            lineList.add(new LineListItem(getJczd0Name(i, bdfz),
                    BigDecimal.valueOf(jczd0List.get(i)).multiply(batteryLevelBigDecimal).toPlainString()));

        }

        int currentMoneyIndex = (int) bd + 1;

        lineList.add(currentMoneyIndex, new LineListItem("当前金币", BigDecimal.valueOf(currentMoney).toPlainString()));

        resultMap.put("batteryLevel", batteryLevel);
        resultMap.put("lineList", lineList);
        resultMap.put("currentMoneyIndex", currentMoneyIndex);

        RBucket<List<Long>> bdfzListBucket =
                redissonClient.getBucket(FishingChallengeFightFishUtil.FISHING_BDFZ_LIST_USER_PRE + userId);

        List<Long> bdfzList = bdfzListBucket.get();

        List<Integer> extraChuShiJczd0IndexList = new ArrayList<>();

        List<Integer> extraShouWeiJczd0IndexList = new ArrayList<>();

        if (player != null) {

            extraChuShiJczd0IndexList = player.getExtraChuShiJczd0IndexList();

            extraShouWeiJczd0IndexList = player.getExtraShouWeiJczd0IndexList();

        }

        if (CollUtil.isNotEmpty(bdfzList)) {

            boolean checkCurrentMoneyIndexFlag = false; // 是否：增加过：currentMoneyIndex的位置

            for (int i = 0; i < bdfzList.size(); i++) {

                long bdfzAndEnd = bdfzList.get(i) + 1;

                if (i == 0) {

                    bdfzAndEnd = bdfzAndEnd + extraChuShiJczd0IndexList.size();

                    bdfzList.set(0, bdfzAndEnd);

                } else {

                    if (i == bdfzList.size() - 1) {

                        bdfzAndEnd = bdfzAndEnd + extraShouWeiJczd0IndexList.size();

                    }

                    bdfzList.set(i, bdfzList.get(i - 1) + bdfzAndEnd);

                }

                if (checkCurrentMoneyIndexFlag == false && bdfzList.get(i) > currentMoneyIndex) {

                    bdfzList.set(i, bdfzList.get(i) + 1);

                    checkCurrentMoneyIndexFlag = true;

                }

            }

        }

        resultMap.put("bdfzList", bdfzList);

        resultMap.put("extraJczd0IndexList",
                CollUtil.addAll(new ArrayList<>(extraChuShiJczd0IndexList), extraShouWeiJczd0IndexList));

    }

    /**
     * 获取：玩家游戏相关数据
     */
    @GmHandler(key = "/osee/server/monitor/playerGameData")
    public void getServerMonitorPlayerGameData(Map<String, Object> paramMap, CommonResponse response) {

        Map<String, Object> resultMap = (Map<String, Object>) response.getData();

        long userId = MapUtil.getLong(paramMap, "userId");

        ServerUser user = UserContainer.getUserById(userId);

        if (user == null) {
            return;
        }

        OseePlayerEntity entity = PlayerManager.getPlayerEntity(user);

        String giftTotalNumCondBuilder = " WHERE 1=1 " + GmCommonController.GIFT_TOTAL_NUM_COND_BUILDER_STR;

        resultMap.put("totalDragonCrystal", entity.getDragonCrystal() / 10000);

        resultMap.put("totalGoldTorpedo", entity.getGoldTorpedo());

        resultMap.put("gameId", user.getGameId());

        resultMap.put("userId", user.getId());

        resultMap.put("lsyk", entity.getTotalDragonCrystal() - entity.getUseBattery());

        RAtomicDouble jrProduceRatomicDouble =
                redissonClient.getAtomicDouble(FishingChallengeFightFishUtil.FISHING_JR_PRODUCE_USER_PRE + userId);

        RAtomicDouble jrXhRatomicDouble =
                redissonClient.getAtomicDouble(FishingChallengeFightFishUtil.FISHING_JR_XH_USER_PRE + userId);

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

        resultMap.put("jryk", jrProduce - jrXh); // 今日盈亏

        resultMap.put("userName", user.getUsername());

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

        resultMap.put("giftLogStr", giveGiftLastToUserNickName + "|" + giveGiftLastFromUserNickName);

        BaseGameRoom room = GameContainer.getGameRoomByPlayerId(user.getId());

        NewBaseGamePlayer player = null;

        if (room instanceof NewBaseGameRoom) {

            player = room.getGamePlayerById(user.getId());

        }

        if (player == null) {

            resultMap.put("jcrtpStr", "-");

            resultMap.put("fishHitInfoStr", "-");

            resultMap.put("sessionName", "-");

            resultMap.put("ksfz", "-");

        } else {

            NewBaseGameRoom newRoom = (NewBaseGameRoom) room;

            String roomIndexStr = String.valueOf(newRoom.getRoomIndex());

            // 处理：roomIndexStr
            roomIndexStr = GmCommonController.handleRoomIndexStr(user.getId(), room, roomIndexStr);

            StrBuilder jcrtpStrBuilder = StrBuilder.create();

            double ksFz = getKsFzByYkType(player.getId(), newRoom.getRoomIndex()); // 亏损峰值

            resultMap.put("ksfz", ksFz);

            // 进场产出
            double jcProduce =
                    redissonClient.getAtomicDouble(FishingChallengeFightFishUtil.FISHING_JC_PRODUCE_ROOM_INDEX_USER_PRE
                            + roomIndexStr + ":" + user.getId()).get();

            // 进场消耗
            double jcXh = redissonClient
                    .getAtomicDouble(
                            FishingChallengeFightFishUtil.FISHING_JC_XH_ROOM_INDEX_USER_PRE + roomIndexStr + ":" + user.getId())
                    .get();

            // 玩家进场收益比
            double jcrtp1 = FishingChallengeFightFishUtil.handleAndGetJcrtp1(jcProduce, jcXh);

            boolean hasPersonalBatteryLevelFlag = redissonClient
                    .<String>getBucket(
                            FishingChallengeFightFishUtil.FISHING_PERSONAL_JCZD0_LIST_BATTERY_LEVEL_USER_PRE + user.getId())
                    .isExists();

            // 二次伤害鱼的，还未加的钱
            long doubleKillWinMoney = FishingChallengeFightFishUtil.getSecondaryDamageFishAllMoney(player.getId());

            // 期望子弹数
            RBucket<Double> jczd0Bucket =
                    redissonClient.getBucket(FishingChallengeFightFishUtil.FISHING_JCZD0_USER_PRE + user.getId());

            // 当前子弹数
            long jczd1 = (entity.getDragonCrystal() + doubleKillWinMoney) / player.getBatteryLevel();

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

            jcrtpStrBuilder.append(jczd1).append(" | ").append(jczd0Str).append(" | ")
                    .append(BigDecimal.valueOf(jcrtp1).multiply(BigDecimal.valueOf(100)).setScale(2, RoundingMode.HALF_UP))
                    .append(" | ").append(bdfz - bd).append(" | ").append(player.getBfxyValue()).append(" | ")
                    .append(player.getBatteryLevel());

            resultMap.put("jcyk", jcProduce - jcXh); // 进场盈亏

            RAtomicDouble jrRoomIndexProduceRatomicDouble =
                    redissonClient.getAtomicDouble(FishingChallengeFightFishUtil.FISHING_JR_PRODUCE_ROOM_INDEX_USER_PRE
                            + user.getId() + ":" + roomIndexStr);

            RAtomicDouble jrRoomIndexXhRatomicDouble = redissonClient.getAtomicDouble(
                    FishingChallengeFightFishUtil.FISHING_JR_XH_ROOM_INDEX_USER_PRE + user.getId() + ":" + roomIndexStr);

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

            resultMap.put("ccjryk", jrRoomIndexProduce - jrRoomIndexXh); // 今日场次盈亏

            // 场次历史产出
            double cclsProduce =
                    redissonClient.getAtomicDouble(FishingChallengeFightFishUtil.FISHING_CCLS_PRODUCE_ROOM_INDEX_USER_PRE
                            + roomIndexStr + ":" + user.getId()).get();

            // 场次历史消耗
            double cclsXh = redissonClient.getAtomicDouble(
                            FishingChallengeFightFishUtil.FISHING_CCLS_XH_ROOM_INDEX_USER_PRE + roomIndexStr + ":" + user.getId())
                    .get();

            resultMap.put("ccyk", cclsProduce - cclsXh); // 场次历史盈亏

            resultMap.put("jcrtpStr", jcrtpStrBuilder.toString());

            resultMap.put("fishHitInfoStr", GmCommonController.getFishHitInfoStr(player));

            resultMap.put("sessionName", "-");

            for (FishCcxxConfig item : MyRefreshFishingUtil.CHALLENGE_AND_INTEGRAL_FISHING_CCXX_CONFIG_LIST) {

                if (item.getSessionId() == ((NewBaseGameRoom) room).getRoomIndex()) {

                    resultMap.put("sessionName", item.getShowSessionName());
                    break;

                }

            }

        }

    }

    /**
     * 执行：关闭个控（重置）
     */
    public static void doPlayerClosePersonal(long userId, @Nullable VoidFunc2<OseePlayerEntity, ServerUser> voidFunc2) {

        ServerUser serverUser = UserContainer.getUserById(userId);

        if (serverUser == null) {
            return;
        }

        NewBaseGamePlayer player = null;
        NewBaseGameRoom room = null;

        BaseGamePlayer basePlayer = GameContainer.getPlayerById(userId);

        if (basePlayer instanceof NewBaseGamePlayer) {

            player = (NewBaseGamePlayer) basePlayer;

            room = GameContainer.getGameRoomByCode(basePlayer.getRoomCode());

        }

        OseePlayerEntity playerEntity = PlayerManager.getPlayerEntity(serverUser);

        // 破产通用处理
        FishingChallengeFightFishUtil.bankruptcyCommonHandler(player, room, playerEntity, true);

        if (voidFunc2 != null) {

            voidFunc2.call(playerEntity, serverUser);

        }

    }

    /**
     * 保存预测节点
     */
    @GmHandler(key = "/osee/player/jczd0List/update")
    public void playerJczd0ListUpdate(Map<String, Object> paramMap, CommonResponse response) {

        // 执行：保存预测节点
        doPlayerJczd0ListUpdate(paramMap, response);

    }

    /**
     * 执行：保存预测节点
     */
    public static void doPlayerJczd0ListUpdate(Map<String, Object> paramMap, CommonResponse response) {

        String batteryLevelStr = MapUtil.getStr(paramMap, "batteryLevelStr");

        if (StrUtil.isBlank(batteryLevelStr)) {

            response.setErrMsg("操作失败：参数不合法-bl");
            return;

        }

        long userId = MapUtil.getLong(paramMap, "userId");

        List<Double> jczd0List = redissonClient
                .<Double>getList(FishingChallengeFightFishUtil.FISHING_JCZD0_LIST_USER_PRE + userId).readAll();

        // log.info("batteryLevelStr：{}，jczd0List：{}", batteryLevelStr, jczd0List);

        if (CollUtil.isEmpty(jczd0List)) {

            response.setErrMsg("操作失败：用户还没有生成预测节点");
            return;

        }

        BaseGamePlayer basePlayer = GameContainer.getPlayerById(userId);

        NewBaseGamePlayer player = null;
        OseePlayerEntity playerEntity;
        NewBaseGameRoom room = null;
        ServerUser user;

        if (basePlayer instanceof NewBaseGamePlayer) { // 如果在龙晶场里

            player = (NewBaseGamePlayer) basePlayer;

            playerEntity = PlayerManager.getPlayerEntity(player.getUser());

            room = GameContainer.getGameRoomByCode(player.getRoomCode());

            user = player.getUser();

        } else { // 如果不在龙晶场里

            user = UserContainer.getUserById(userId);

            if (user == null) { // 如果：用户不存在

                response.setErrMsg("操作失败：用户不存在");
                return;

            } else { // 如果在线

                playerEntity = PlayerManager.getPlayerEntity(user);

            }

        }

        synchronized (playerEntity) {

            // 完成的节点数
            long bd = redissonClient.getAtomicLong(FishingChallengeFightFishUtil.FISHING_BD_USER_PRE + userId).get();

            int jczd0ListSize = jczd0List.size();

            if (bd == jczd0ListSize - 1) {

                response.setErrMsg("操作失败：用户当前已经是结束节点，无法修改");
                return;

            }

            JSONObject jsonObject = JSONUtil.parseObj(paramMap);

            List<String> changeJczd0List = jsonObject.getBeanList("changeJczd0List", String.class);

            // log.info("changeJczd0List：{}", changeJczd0List);

            if (changeJczd0List.size() < jczd0ListSize) {

                response.setErrMsg("操作失败：节点数小于之前节点数");
                return;

            }

            BigDecimal batteryLevelStrBigDecimal = new BigDecimal(batteryLevelStr);

            long batteryLevel;

            if (player == null) {
                batteryLevel = batteryLevelStrBigDecimal.intValue();
            } else {
                batteryLevel = player.getBatteryLevel();
            }

            String personalJczd0ListBatteryLevel =
                    redissonClient
                            .<String>getBucket(
                                    FishingChallengeFightFishUtil.FISHING_PERSONAL_JCZD0_LIST_BATTERY_LEVEL_USER_PRE + userId)
                            .get();

            BigDecimal personalBigDecimal = null;

            if (StrUtil.isNotBlank(personalJczd0ListBatteryLevel)) { // 如果之前有个控

                personalBigDecimal = new BigDecimal(personalJczd0ListBatteryLevel);

            }

            for (int i = 0; i < changeJczd0List.size(); i++) {

                if (i < bd) {

                    if (personalBigDecimal != null) {

                        // 转换为：当前炮倍
                        double newValue = BigDecimal.valueOf(jczd0List.get(i)).multiply(personalBigDecimal)
                                .divide(batteryLevelStrBigDecimal, 2, RoundingMode.DOWN).doubleValue();

                        jczd0List.set(i, newValue);

                    }

                    continue; // 已完成的节点无法修改

                }

                // 转换为：当前炮倍
                double newValue = new BigDecimal(changeJczd0List.get(i))
                        .divide(batteryLevelStrBigDecimal, 2, RoundingMode.DOWN).doubleValue();

                if (i < jczd0ListSize) { // 替换

                    jczd0List.set(i, newValue);

                } else { // 追加

                    jczd0List.add(newValue);

                }

            }

            if (jczd0List.get(jczd0List.size() - 1) != 0) {

                jczd0List.add(0d);

            }

            // 初始金币数
            double chuShiMoney = redissonClient
                    .getAtomicDouble(FishingChallengeFightFishUtil.FISHING_CHU_SHI_MONEY_USER_PRE + userId).get();

            double chuShiBulletNumber = chuShiMoney / batteryLevelStrBigDecimal.doubleValue();

            RBatch batch = redissonClient.createBatch();

            batch.getList(FishingChallengeFightFishUtil.FISHING_JCZD0_LIST_USER_PRE + userId).deleteAsync();
            batch.getList(FishingChallengeFightFishUtil.FISHING_JCZD0_LIST_USER_PRE + userId).addAllAsync(jczd0List);

            int bdInt = (int) bd;

            double jczd0 = jczd0List.get(bdInt);

            int hitState; // 1 爆发 2 回收

            if (bd == 0) {

                hitState = jczd0 > chuShiBulletNumber ? 1 : 2; // 1 爆发 2 回收

            } else {

                // 和上一个节点做判断
                hitState = jczd0 > jczd0List.get(bdInt - 1) ? 1 : 2; // 1 爆发 2 回收

            }

            // log.info("hitState-0：{}，bdInt：{}", hitState, bdInt);

            batch.<Double>getBucket(FishingChallengeFightFishUtil.FISHING_JCZD0_USER_PRE + userId)
                    .setAsync(jczd0 * batteryLevelStrBigDecimal.doubleValue() / batteryLevel);

            // log.info("保存节点，hitState：{}", hitState);

            batch.<Integer>getBucket(FishingChallengeFightFishUtil.FISHING_HIT_STATE_USER_PRE + userId)
                    .setAsync(hitState);

            long bdfz = (jczd0List.size() - 1);

            // 初始的：回收权重
            double chuShiHsWeight;

            // 过程的：回收权重
            double guoChengHsWeight;

            // 随机取值的权重
            double randomWeight;

            if (room == null) {

                chuShiHsWeight = 0;
                guoChengHsWeight = 0;
                randomWeight = 0;

            } else {

                ProfitRatioDTO profitRatioDTO = FishingTUtil.getProfitRatioDTO();

                int index = profitRatioDTO.getIndexByRoomIndex(room.getRoomIndex());

                chuShiHsWeight = profitRatioDTO.getChuShiHsWeightArr()[index];
                guoChengHsWeight = profitRatioDTO.getGuoChengHsWeightArr()[index];
                randomWeight = profitRatioDTO.getBfxyRandomWeightArr()[index];

            }

            long jczd1 = FishingChallengeFightFishUtil.getJczd1(playerEntity.getDragonCrystal(), batteryLevel, userId);


            batch
                    .<String>getBucket(
                            FishingChallengeFightFishUtil.FISHING_PERSONAL_JCZD0_LIST_BATTERY_LEVEL_USER_PRE + userId)
                    .setAsync(batteryLevelStr);

            RAtomicDouble personalChuShiMoneyAtomicDouble = redissonClient
                    .getAtomicDouble(FishingChallengeFightFishUtil.FISHING_PERSONAL_CHU_SHI_MONEY_USER_PRE + userId);

            if (BooleanUtil.isFalse(personalChuShiMoneyAtomicDouble.isExists())) { // 如果之前不存在：初始金币数

                // 暂存这一刻的初始金币
                batch.getAtomicDouble(FishingChallengeFightFishUtil.FISHING_PERSONAL_CHU_SHI_MONEY_USER_PRE + userId)
                        .setAsync(chuShiMoney);

            }

            batch.getAtomicDouble(FishingChallengeFightFishUtil.FISHING_GRETJ_USER_PRE + userId).deleteAsync();

            batch.<Long>getBucket(FishingChallengeFightFishUtil.FISHING_BDFZ_USER_PRE + userId).setAsync(bdfz);

            batch.execute();

        }

    }

    @NotNull
    private String getJczd0Name(int currentIndex, int endIndex) {

        String name;

        if (currentIndex == endIndex) {

            name = "结束金币";

        } else if (currentIndex == 0) {

            name = "初始金币";

        } else {

            name = "过程金币" + currentIndex;

        }

        return name;

    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class LineListItem {

        private String name;

        private String money;

    }

    /**
     * 获取服务器监控数据
     */
    @SuppressWarnings("unchecked")
    @GmHandler(key = "/osee/server/monitor")
    public void doServerMonitorTask(Map<String, Object> paramMap, CommonResponse response) {

        Map<String, Object> resultMap = (Map<String, Object>) response.getData();

        Date beginTime = MapUtil.getDate(paramMap, "beginTime");
        Date endTime = MapUtil.getDate(paramMap, "endTime");

        List<TblProfitRatioLogDO> tblProfitRatioLogDOList = ProfitRatioLogTask.getSevenDayData(beginTime, endTime);

        resultMap.put("tblProfitRatioLogDOList", tblProfitRatioLogDOList);

        // Map<String, Object> resultMap = (Map<String, Object>)response.getData();
        // resultMap.put("dailyExpend", expendLogMapper.getTodayExpendMoney());
        // resultMap.put("dailyCutMoney", cutMoneyLogMapper.getTodayCutMoney(GameEnum.FISHING.getId()));
        // long fishChallengeCut1 = cutMoneyLogMapper.getTodayCutMoney(7);
        // long fishChallengeCut2 = cutMoneyLogMapper.getTodayCutMoney(8);
        // long fishChallengeCut3 = cutMoneyLogMapper.getTodayCutMoney(9);
        // resultMap.put("dailyCutCrystal", fishChallengeCut1 + fishChallengeCut2 + fishChallengeCut3);
        // resultMap.put("nowTime", System.currentTimeMillis());
        //
        // List<Object> monitors = new ArrayList<>();
        // for (int i = 0; i < 8; i++) {
        // int index = i % 4;
        // int greener = i / 4;
        //
        // Map<String, Object> monitor = new HashMap<>();
        // monitor.put("initPool", FishingHitDataManager.FISHING_INIT_POOL[greener][index]);
        // monitor.put("currentPool", FishingHitDataManager.FISHING_POOL[greener][index]);
        // monitor.put("fishingProb", String.format("%.2f", FishingHitDataManager.getServerProb(greener, index)));
        // monitors.add(monitor);
        // }
        // for (int index = 4; index < 7; index++) {
        // // 捕鱼挑战赛
        // Map<String, Object> monitor = new HashMap<>();
        // monitor.put("initPool", FishingHitDataManager.FISHING_INIT_POOL[1][index]);
        // monitor.put("currentPool", FishingHitDataManager.FISHING_POOL[1][index]);
        // monitor.put("fishingProb", String.format("%.2f", FishingHitDataManager.getServerProb(1, index)));
        // monitors.add(monitor);
        // }
        // // 大奖赛
        // Map<String, Object> monitor = new HashMap<>();
        // monitor.put("initPool", FishingGrandPrixManager.initPool);
        // monitor.put("currentPool", RedisUtil.val(FishingGrandPrixManager.PLAYER_GRANDPRIX_CONFIG_STOCK_KEY, 0L));
        // Double val = RedisUtil.val(FishingGrandPrixManager.PLAYER_GRANDPRIX_CONFIG_AP_KEY, 0.0D);
        // Long val1 = RedisUtil.val(FishingGrandPrixManager.PLAYER_GRANDPRIX_CONFIG_STOCK_KEY, 0L);
        // Long val2 = RedisUtil.val(FishingGrandPrixManager.PLAYER_GRANDPRIX_CONFIG_APT_KEY, 100L);
        //
        // monitor.put("fishingProb", String.format("%.2f", val + (val1 / val2) * 0.01));
        // monitors.add(monitor);
        //
        // Map<String, Object> monitor1 = new HashMap<>();
        // monitor1.put("initPool", FishingHitDataManager.FISHING_INIT_POOL[0][4]);
        // monitor1.put("currentPool", FishingHitDataManager.FISHING_POOL[0][4]);
        // monitor1.put("fishingProb", String.format("%.2f", FishingHitDataManager.getServerProb(0, 4)));
        // monitors.add(monitor1);
        // Map<String, Object> monitor2 = new HashMap<>();
        // monitor2.put("initPool", FishingHitDataManager.FISHING_INIT_POOL[1][7]);
        // monitor2.put("currentPool", FishingHitDataManager.FISHING_POOL[1][7]);
        // monitor2.put("fishingProb", String.format("%.2f", FishingHitDataManager.getServerProb(1, 7)));
        // monitors.add(monitor2);
        //
        // resultMap.put("monitors", monitors);
    }

    public static String GM_GAME_CONFIG_INFO = "GM_GAME_CONFIG_INFO"; // 获取游戏设置信息 存在Redis中

    /**
     * 获取服务器游戏设置
     */
    @GmHandler(key = "/osee/game/config/info")
    public void doGameConfigInfoTask(Map<String, Object> params, CommonResponse response) {

//        RMap<String, Object> redissonClientMap  = redissonClient.getMap(GM_GAME_CONFIG_INFO); // 取缓存
//        if (ObjectUtils.isNotEmpty(redissonClientMap)){
//            response.setData(redissonClientMap);
//            return;
//        }

        Map<String, Object> resultMap = new HashMap<>();

        resultMap.put("k", RedisUtil.val("k", 0D));
        resultMap.put("sxxs", RedisUtil.val("sxxs", 0D));
        resultMap.put("xs", RedisUtil.val("xs", 0D));
        resultMap.put("sxhdz", RedisUtil.val("sxhdz", 0D));
        resultMap.put("csc", RedisUtil.val("csc", 0D));
        resultMap.put("ckc", RedisUtil.val("ckc", 0D));
        resultMap.put("zsc", RedisUtil.val("zsc", 0D));
        resultMap.put("zkc", RedisUtil.val("zkc", 0D));
        resultMap.put("csc1", RedisUtil.val("csc1", 0D));
        resultMap.put("ckc1", RedisUtil.val("ckc1", 0D));
        resultMap.put("zsc1", RedisUtil.val("zsc1", 0D));
        resultMap.put("zkc1", RedisUtil.val("zkc1", 0D));

        String torpedoDropFreeRate = RedisHelper.get("torpedoDropFreeRate");
        // // System.out.println("torpedoDropFreeRate*****************:" + torpedoDropFreeRate);
        if (torpedoDropFreeRate != null && torpedoDropFreeRate.length() != 0) {
            torpedoDropFreeRate =
                    torpedoDropFreeRate.substring(torpedoDropFreeRate.lastIndexOf("[") + 1).replaceAll("]", "");
            String[] q1 = torpedoDropFreeRate.split(",");
            List<Double> list = new ArrayList<>();
            for (String q : q1) {
                // // System.out.println(q);
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
            FishingUtil.torpedoDropFreeRate = list.toArray(new Double[0]);
        }
        resultMap.put("torpedoDropFreeRate", FishingUtil.torpedoDropFreeRate);

        String torpedoDropRateGoldFreeRate = RedisHelper.get("torpedoDropRateGoldFreeRate");
        // // System.out.println("torpedoDropRateGoldFreeRate*****************:" + torpedoDropRateGoldFreeRate);
        if (torpedoDropRateGoldFreeRate != null && torpedoDropRateGoldFreeRate.length() != 0) {
            torpedoDropRateGoldFreeRate = torpedoDropRateGoldFreeRate
                    .substring(torpedoDropRateGoldFreeRate.lastIndexOf("[") + 1).replaceAll("]", "");
            String[] q1 = torpedoDropRateGoldFreeRate.split(",");
            List<Double> list = new ArrayList<>();
            for (String q : q1) {
                // // System.out.println(q);
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
        resultMap.put("torpedoDropRateGoldFreeRate", FishingUtil.torpedoDropRateGoldFreeRate);

        String bangTorpedoDropFreeRate = RedisHelper.get("bangTorpedoDropFreeRate");
        // // System.out.println("bangTorpedoDropFreeRate*****************:" + bangTorpedoDropFreeRate);
        if (bangTorpedoDropFreeRate != null && bangTorpedoDropFreeRate.length() != 0) {
            bangTorpedoDropFreeRate =
                    bangTorpedoDropFreeRate.substring(bangTorpedoDropFreeRate.lastIndexOf("[") + 1).replaceAll("]", "");
            String[] q1 = bangTorpedoDropFreeRate.split(",");
            List<Double> list = new ArrayList<>();
            for (String q : q1) {
                // // System.out.println(q);
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
        resultMap.put("bangTorpedoDropFreeRate", FishingUtil.bangTorpedoDropFreeRate);

        String bangTorpedoDropRateGoldFreeRate = RedisHelper.get("bangTorpedoDropRateGoldFreeRate");
        // // System.out.println("bangTorpedoDropRateGoldFreeRate*****************:" + bangTorpedoDropRateGoldFreeRate);
        if (bangTorpedoDropRateGoldFreeRate != null && bangTorpedoDropRateGoldFreeRate.length() != 0) {
            bangTorpedoDropRateGoldFreeRate = bangTorpedoDropRateGoldFreeRate
                    .substring(bangTorpedoDropRateGoldFreeRate.lastIndexOf("[") + 1).replaceAll("]", "");
            String[] q1 = bangTorpedoDropRateGoldFreeRate.split(",");
            List<Double> list = new ArrayList<>();
            for (String q : q1) {
                // // System.out.println(q);
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
        resultMap.put("bangTorpedoDropRateGoldFreeRate", FishingUtil.bangTorpedoDropRateGoldFreeRate);

        String bossBugleRate = RedisHelper.get("bossBugleRate");
        // // System.out.println("bossBugleRate*****************:" + bossBugleRate);
        if (bossBugleRate != null && bossBugleRate.length() != 0) {
            bossBugleRate = bossBugleRate.substring(bossBugleRate.lastIndexOf("[") + 1).replaceAll("]", "");
            String[] q1 = bossBugleRate.split(",");
            List<Double> list = new ArrayList<>();
            for (String q : q1) {
                // // System.out.println(q);
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
        resultMap.put("bossBugleRate", FishingUtil.bossBugleRate);

        String torpedoDropPerPayMoney = RedisHelper.get("torpedoDropPerPayMoney");
        // // System.out.println("torpedoDropPerPayMoney*****************:" + torpedoDropPerPayMoney);
        if (torpedoDropPerPayMoney != null && torpedoDropPerPayMoney.length() != 0) {
            torpedoDropPerPayMoney =
                    torpedoDropPerPayMoney.substring(torpedoDropPerPayMoney.lastIndexOf("[") + 1).replaceAll("]", "");
            String[] q1 = torpedoDropPerPayMoney.split(",");
            List<Double> list = new ArrayList<>();
            for (String q : q1) {
                // // System.out.println(q);
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
            FishingUtil.torpedoDropPerPayMoney = list.toArray(new Double[0]);
        }
        resultMap.put("torpedoDropPerPayMoney", FishingUtil.torpedoDropPerPayMoney);

        String torpedoDropPerPayRate = RedisHelper.get("torpedoDropPerPayRate");
        // // System.out.println("torpedoDropPerPayRate*****************:" + torpedoDropPerPayRate);
        if (torpedoDropPerPayRate != null && torpedoDropPerPayRate.length() != 0) {
            torpedoDropPerPayRate =
                    torpedoDropPerPayRate.substring(torpedoDropPerPayRate.lastIndexOf("[") + 1).replaceAll("]", "");
            String[] q1 = torpedoDropPerPayRate.split(",");
            List<Double> list = new ArrayList<>();
            for (String q : q1) {
                // // System.out.println(q);
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
            FishingUtil.torpedoDropPerPayRate = list.toArray(new Double[0]);
        }
        resultMap.put("torpedoDropPerPayRate", FishingUtil.torpedoDropPerPayRate);

        String torpedoDropPerExChangeRate = RedisHelper.get("torpedoDropPerExChangeRate");
        // // System.out.println("torpedoDropPerExChangeRate*****************:" + torpedoDropPerExChangeRate);
        if (torpedoDropPerExChangeRate != null && torpedoDropPerExChangeRate.length() != 0) {
            torpedoDropPerExChangeRate = torpedoDropPerExChangeRate
                    .substring(torpedoDropPerExChangeRate.lastIndexOf("[") + 1).replaceAll("]", "");
            String[] q1 = torpedoDropPerExChangeRate.split(",");
            List<Double> list = new ArrayList<>();
            for (String q : q1) {
                // // System.out.println(q);
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
            FishingUtil.torpedoDropPerExChangeRate = list.toArray(new Double[0]);
        }
        resultMap.put("torpedoDropPerExChangeRate", FishingUtil.torpedoDropPerExChangeRate);

        String torpedoDropPerExChangeRateMin = RedisHelper.get("torpedoDropPerExChangeRateMin");
        // // System.out.println("torpedoDropPerExChangeRateMin*****************:" + torpedoDropPerExChangeRateMin);
        if (torpedoDropPerExChangeRateMin != null && torpedoDropPerExChangeRateMin.length() != 0) {
            torpedoDropPerExChangeRateMin = torpedoDropPerExChangeRateMin
                    .substring(torpedoDropPerExChangeRateMin.lastIndexOf("[") + 1).replaceAll("]", "");
            String[] q1 = torpedoDropPerExChangeRateMin.split(",");
            List<Double> list = new ArrayList<>();
            for (String q : q1) {
                // // System.out.println(q);
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
            FishingUtil.torpedoDropPerExChangeRateMin = list.toArray(new Double[0]);
        }
        resultMap.put("torpedoDropPerExChangeRateMin", FishingUtil.torpedoDropPerExChangeRateMin);

        String torpedoNotBangDropPerExChangeRateMin = RedisHelper.get("torpedoNotBangDropPerExChangeRateMin");
        // // System.out
        // .println("torpedoNotBangDropPerExChangeRateMin*****************:" + torpedoNotBangDropPerExChangeRateMin);
        if (torpedoNotBangDropPerExChangeRateMin != null && torpedoNotBangDropPerExChangeRateMin.length() != 0) {
            torpedoNotBangDropPerExChangeRateMin = torpedoNotBangDropPerExChangeRateMin
                    .substring(torpedoNotBangDropPerExChangeRateMin.lastIndexOf("[") + 1).replaceAll("]", "");
            String[] q1 = torpedoNotBangDropPerExChangeRateMin.split(",");
            List<Double> list = new ArrayList<>();
            for (String q : q1) {
                // // System.out.println(q);
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
            FishingUtil.torpedoNotBangDropPerExChangeRateMin = list.toArray(new Double[0]);
        }
        resultMap.put("torpedoNotBangDropPerExChangeRateMin", FishingUtil.torpedoNotBangDropPerExChangeRateMin);

        String rareTorpedoDropPerExChangeRate = RedisHelper.get("rareTorpedoDropPerExChangeRate");
        // // System.out.println("rareTorpedoDropPerExChangeRate*****************:" + rareTorpedoDropPerExChangeRate);
        if (rareTorpedoDropPerExChangeRate != null && rareTorpedoDropPerExChangeRate.length() != 0) {
            rareTorpedoDropPerExChangeRate = rareTorpedoDropPerExChangeRate
                    .substring(rareTorpedoDropPerExChangeRate.lastIndexOf("[") + 1).replaceAll("]", "");
            String[] q1 = rareTorpedoDropPerExChangeRate.split(",");
            List<Double> list = new ArrayList<>();
            for (String q : q1) {
                // // System.out.println(q);
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
            FishingUtil.rareTorpedoDropPerExChangeRate = list.toArray(new Double[0]);
        }
        resultMap.put("rareTorpedoDropPerExChangeRate", FishingUtil.rareTorpedoDropPerExChangeRate);

        String rareTorpedoDropPerExChangeRateMin = RedisHelper.get("rareTorpedoDropPerExChangeRateMin");
        // // System.out.println("rareTorpedoDropPerExChangeRateMin*****************:" +
        // rareTorpedoDropPerExChangeRateMin);
        if (rareTorpedoDropPerExChangeRateMin != null && rareTorpedoDropPerExChangeRateMin.length() != 0) {
            rareTorpedoDropPerExChangeRateMin = rareTorpedoDropPerExChangeRateMin
                    .substring(rareTorpedoDropPerExChangeRateMin.lastIndexOf("[") + 1).replaceAll("]", "");
            String[] q1 = rareTorpedoDropPerExChangeRateMin.split(",");
            List<Double> list = new ArrayList<>();
            for (String q : q1) {
                // // System.out.println(q);
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
            FishingUtil.rareTorpedoDropPerExChangeRateMin = list.toArray(new Double[0]);
        }
        resultMap.put("rareTorpedoDropPerExChangeRateMin", FishingUtil.rareTorpedoDropPerExChangeRateMin);

        String rareTorpedoNotBangDropPerExChangeRate = RedisHelper.get("rareTorpedoNotBangDropPerExChangeRate");
        // // System.out
        // .println("rareTorpedoNotBangDropPerExChangeRate*****************:" + rareTorpedoNotBangDropPerExChangeRate);
        if (rareTorpedoNotBangDropPerExChangeRate != null && rareTorpedoNotBangDropPerExChangeRate.length() != 0) {
            rareTorpedoNotBangDropPerExChangeRate = rareTorpedoNotBangDropPerExChangeRate
                    .substring(rareTorpedoNotBangDropPerExChangeRate.lastIndexOf("[") + 1).replaceAll("]", "");
            String[] q1 = rareTorpedoNotBangDropPerExChangeRate.split(",");
            List<Double> list = new ArrayList<>();
            for (String q : q1) {
                // // System.out.println(q);
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
            FishingUtil.rareTorpedoNotBangDropPerExChangeRate = list.toArray(new Double[0]);
        }
        resultMap.put("rareTorpedoNotBangDropPerExChangeRate", FishingUtil.rareTorpedoNotBangDropPerExChangeRate);

        String rareTorpedoNotBangDropPerExChangeRateMin = RedisHelper.get("rareTorpedoNotBangDropPerExChangeRateMin");
        // // System.out.println(
        // "rareTorpedoNotBangDropPerExChangeRateMin*****************:" + rareTorpedoNotBangDropPerExChangeRateMin);
        if (rareTorpedoNotBangDropPerExChangeRateMin != null
                && rareTorpedoNotBangDropPerExChangeRateMin.length() != 0) {
            rareTorpedoNotBangDropPerExChangeRateMin = rareTorpedoNotBangDropPerExChangeRateMin
                    .substring(rareTorpedoNotBangDropPerExChangeRateMin.lastIndexOf("[") + 1).replaceAll("]", "");
            String[] q1 = rareTorpedoNotBangDropPerExChangeRateMin.split(",");
            List<Double> list = new ArrayList<>();
            for (String q : q1) {
                // // System.out.println(q);
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
            FishingUtil.rareTorpedoNotBangDropPerExChangeRateMin = list.toArray(new Double[0]);
        }
        resultMap.put("rareTorpedoNotBangDropPerExChangeRateMin", FishingUtil.rareTorpedoNotBangDropPerExChangeRateMin);

        String torpedoNotBangDropPerExChangeRate = RedisHelper.get("torpedoNotBangDropPerExChangeRate");
        // // System.out.println("torpedoNotBangDropPerExChangeRate*****************:" +
        // torpedoNotBangDropPerExChangeRate);
        if (torpedoNotBangDropPerExChangeRate != null && torpedoNotBangDropPerExChangeRate.length() != 0) {
            torpedoNotBangDropPerExChangeRate = torpedoNotBangDropPerExChangeRate
                    .substring(torpedoNotBangDropPerExChangeRate.lastIndexOf("[") + 1).replaceAll("]", "");
            String[] q1 = torpedoNotBangDropPerExChangeRate.split(",");
            List<Double> list = new ArrayList<>();
            for (String q : q1) {
                // // System.out.println(q);
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
            FishingUtil.torpedoNotBangDropPerExChangeRate = list.toArray(new Double[0]);
        }
        resultMap.put("torpedoNotBangDropPerExChangeRate", FishingUtil.torpedoNotBangDropPerExChangeRate);

        // String isUsed = RedisHelper.get("isUsed");
        // // System.out.println("isUsed*****************:"+isUsed);
        // if(isUsed!=null&&isUsed.length()!=0){
        // isUsed = isUsed.substring(isUsed.lastIndexOf("[")+1).replaceAll("]","");
        // String[] q1 = isUsed.split(",");
        // List<String> list = new ArrayList<>();
        // for (String q : q1) {
        // // System.out.println(q);
        // if(q==null||"".equals(q)){
        // return;
        // }
        // list.add(q);
        // }
        //// list.add(0L);
        //// list.add(0L);
        //// list.add(0L);
        //// list.add(0L);
        //// list.add(0L);
        //// list.add(0L);
        // FishingUtil.isUsed =list.toArray(new String[0]);
        // }
        // resultMap.put("isUsed", FishingUtil.isUsed);

        String cxPercentage = RedisHelper.get("cxPercentage");
        // // System.out.println("cxPercentage*****************:" + cxPercentage);
        if (cxPercentage != null && cxPercentage.length() != 0) {
            cxPercentage = cxPercentage.substring(cxPercentage.lastIndexOf("[") + 1).replaceAll("]", "");
            String[] ap1 = cxPercentage.split(",");
            List<Double> listap = new ArrayList<>();
            for (String a : ap1) {
                // // System.out.println(a);
                if (a == null || "".equals(a)) {
                    return;
                }
                listap.add(Double.parseDouble(a));
            }
            listap.add(0D);
            listap.add(0D);
            listap.add(0D);
            listap.add(0D);
            listap.add(0D);
            listap.add(0D);
            FishingUtil.cxPercentage = listap.toArray(new Double[0]);
        }
        resultMap.put("cxPercentage", FishingUtil.cxPercentage);

        String jcPercentage = RedisHelper.get("jcPercentage");
        // // System.out.println("jcPercentage*****************:" + jcPercentage);
        if (jcPercentage != null && jcPercentage.length() != 0) {
            jcPercentage = jcPercentage.substring(jcPercentage.lastIndexOf("[") + 1).replaceAll("]", "");
            String[] ap1 = jcPercentage.split(",");
            List<Double> listap = new ArrayList<>();
            for (String a : ap1) {
                // // System.out.println(a);
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
        resultMap.put("jcPercentage", FishingUtil.jcPercentage);

        String peakMax1 = RedisHelper.get("peakMax1");
        // // System.out.println("peakMax1*****************:" + peakMax1);
        if (peakMax1 != null && peakMax1.length() != 0) {
            peakMax1 = peakMax1.substring(peakMax1.lastIndexOf("[") + 1).replaceAll("]", "");
            String[] ap1 = peakMax1.split(",");
            List<Double> listap = new ArrayList<>();
            for (String a : ap1) {
                // // System.out.println(a);
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
            FishingUtil.peakMax1 = listap.toArray(new Double[0]);
        }
        resultMap.put("peakMax1", FishingUtil.peakMax1);

        String peakMin1 = RedisHelper.get("peakMin1");
        // // System.out.println("peakMin1*****************:" + peakMin1);
        if (peakMin1 != null && peakMin1.length() != 0) {
            peakMin1 = peakMin1.substring(peakMin1.lastIndexOf("[") + 1).replaceAll("]", "");
            String[] ap1 = peakMin1.split(",");
            List<Double> listap = new ArrayList<>();
            for (String a : ap1) {
                // // System.out.println(a);
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
            FishingUtil.peakMin1 = listap.toArray(new Double[0]);
        }
        resultMap.put("peakMin1", FishingUtil.peakMin1);

        String peakMax2 = RedisHelper.get("peakMax2");
        // // System.out.println("peakMax2*****************:" + peakMax2);
        if (peakMax2 != null && peakMax2.length() != 0) {
            peakMax2 = peakMax2.substring(peakMax2.lastIndexOf("[") + 1).replaceAll("]", "");
            String[] ap1 = peakMax2.split(",");
            List<Double> listap = new ArrayList<>();
            for (String a : ap1) {
                // // System.out.println(a);
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
            FishingUtil.peakMax2 = listap.toArray(new Double[0]);
        }
        resultMap.put("peakMax2", FishingUtil.peakMax2);

        String peakMin2 = RedisHelper.get("peakMin2");
        // // System.out.println("peakMin2*****************:" + peakMin2);
        if (peakMin2 != null && peakMin2.length() != 0) {
            peakMin2 = peakMin2.substring(peakMin2.lastIndexOf("[") + 1).replaceAll("]", "");
            String[] ap1 = peakMin2.split(",");
            List<Double> listap = new ArrayList<>();
            for (String a : ap1) {
                // // System.out.println(a);
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
            FishingUtil.peakMin2 = listap.toArray(new Double[0]);
        }
        resultMap.put("peakMin2", FishingUtil.peakMin2);

        String peakMax3 = RedisHelper.get("peakMax3");
        // // System.out.println("peakMax3*****************:" + peakMax3);
        if (peakMax3 != null && peakMax3.length() != 0) {
            peakMax3 = peakMax3.substring(peakMax3.lastIndexOf("[") + 1).replaceAll("]", "");
            String[] ap1 = peakMax3.split(",");
            List<Double> listap = new ArrayList<>();
            for (String a : ap1) {
                // // System.out.println(a);
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
            FishingUtil.peakMax3 = listap.toArray(new Double[0]);
        }
        resultMap.put("peakMax3", FishingUtil.peakMax3);

        String peakMin3 = RedisHelper.get("peakMin3");
        // // System.out.println("peakMin3*****************:" + peakMin3);
        if (peakMin3 != null && peakMin3.length() != 0) {
            peakMin3 = peakMin3.substring(peakMin3.lastIndexOf("[") + 1).replaceAll("]", "");
            String[] ap1 = peakMin3.split(",");
            List<Double> listap = new ArrayList<>();
            for (String a : ap1) {
                // // System.out.println(a);
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
            FishingUtil.peakMin3 = listap.toArray(new Double[0]);
        }
        resultMap.put("peakMin3", FishingUtil.peakMin3);

        String peakMaxNum1 = RedisHelper.get("peakMaxNum1");
        // // System.out.println("peakMaxNum1*****************:" + peakMaxNum1);
        if (peakMaxNum1 != null && peakMaxNum1.length() != 0) {
            peakMaxNum1 = peakMaxNum1.substring(peakMaxNum1.lastIndexOf("[") + 1).replaceAll("]", "");
            String[] ap1 = peakMaxNum1.split(",");
            List<Double> listap = new ArrayList<>();
            for (String a : ap1) {
                // // System.out.println(a);
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
            FishingUtil.peakMaxNum1 = listap.toArray(new Double[0]);
        }
        resultMap.put("peakMaxNum1", FishingUtil.peakMaxNum1);

        String peakMinNum1 = RedisHelper.get("peakMinNum1");
        // // System.out.println("peakMinNum1*****************:" + peakMinNum1);
        if (peakMinNum1 != null && peakMinNum1.length() != 0) {
            peakMinNum1 = peakMinNum1.substring(peakMinNum1.lastIndexOf("[") + 1).replaceAll("]", "");
            String[] ap1 = peakMinNum1.split(",");
            List<Double> listap = new ArrayList<>();
            for (String a : ap1) {
                // // System.out.println(a);
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
            FishingUtil.peakMinNum1 = listap.toArray(new Double[0]);
        }
        resultMap.put("peakMinNum1", FishingUtil.peakMinNum1);

        String peakMaxNum2 = RedisHelper.get("peakMaxNum2");
        // // System.out.println("peakMaxNum2*****************:" + peakMaxNum2);
        if (peakMaxNum2 != null && peakMaxNum2.length() != 0) {
            peakMaxNum2 = peakMaxNum2.substring(peakMaxNum2.lastIndexOf("[") + 1).replaceAll("]", "");
            String[] ap1 = peakMaxNum2.split(",");
            List<Double> listap = new ArrayList<>();
            for (String a : ap1) {
                // // System.out.println(a);
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
            FishingUtil.peakMaxNum2 = listap.toArray(new Double[0]);
        }
        resultMap.put("peakMaxNum2", FishingUtil.peakMaxNum2);

        String peakMinNum2 = RedisHelper.get("peakMinNum2");
        // // System.out.println("peakMinNum2*****************:" + peakMinNum2);
        if (peakMinNum2 != null && peakMinNum2.length() != 0) {
            peakMinNum2 = peakMinNum2.substring(peakMinNum2.lastIndexOf("[") + 1).replaceAll("]", "");
            String[] ap1 = peakMinNum2.split(",");
            List<Double> listap = new ArrayList<>();
            for (String a : ap1) {
                // // System.out.println(a);
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
            FishingUtil.peakMinNum2 = listap.toArray(new Double[0]);
        }
        resultMap.put("peakMinNum2", FishingUtil.peakMinNum2);

        String peakMaxNum3 = RedisHelper.get("peakMaxNum3");
        // // System.out.println("peakMaxNum3*****************:" + peakMaxNum3);
        if (peakMaxNum3 != null && peakMaxNum3.length() != 0) {
            peakMaxNum3 = peakMaxNum3.substring(peakMaxNum3.lastIndexOf("[") + 1).replaceAll("]", "");
            String[] ap1 = peakMaxNum3.split(",");
            List<Double> listap = new ArrayList<>();
            for (String a : ap1) {
                // // System.out.println(a);
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
            FishingUtil.peakMaxNum3 = listap.toArray(new Double[0]);
        }
        resultMap.put("peakMaxNum3", FishingUtil.peakMaxNum3);

        String peakMinNum3 = RedisHelper.get("peakMinNum3");
        // // System.out.println("peakMinNum3*****************:" + peakMinNum3);
        if (peakMinNum3 != null && peakMinNum3.length() != 0) {
            peakMinNum3 = peakMinNum3.substring(peakMinNum3.lastIndexOf("[") + 1).replaceAll("]", "");
            String[] ap1 = peakMinNum3.split(",");
            List<Double> listap = new ArrayList<>();
            for (String a : ap1) {
                // // System.out.println(a);
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
            FishingUtil.peakMinNum3 = listap.toArray(new Double[0]);
        }
        resultMap.put("peakMinNum3", FishingUtil.peakMinNum3);

        // 支付信息
        resultMap.put("wxS", RedisUtil.get("OESS_PAY_WAY_WXS"));
        resultMap.put("wxH5", RedisUtil.get("OESS_PAY_WAY_WXH5"));
        resultMap.put("zfbS", RedisUtil.get("OESS_PAY_WAY_ZFBS"));
        resultMap.put("zfbH5", RedisUtil.get("OESS_PAY_WAY_ZFBH5"));

        List<Object> fishConfigList = new ArrayList<>(6);

        String q0 = RedisHelper.get("q0");
        // // System.out.println("Q0*****************:" + q0);
        if (q0 != null && q0.length() != 0) {
            q0 = q0.substring(q0.lastIndexOf("[") + 1).replaceAll("]", "");
            String[] q1 = q0.split(",");
            List<Long> list = new ArrayList<>();
            for (String q : q1) {
                // // System.out.println(q);
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
            FishingUtil.q0 = list.toArray(new Long[0]);
        }
        String ap = RedisHelper.get("ap");
        // // System.out.println("ap*****************:" + ap);
        if (ap != null && ap.length() != 0) {
            ap = ap.substring(ap.lastIndexOf("[") + 1).replaceAll("]", "");
            String[] ap1 = ap.split(",");
            List<Double> listap = new ArrayList<>();
            for (String a : ap1) {
                // // System.out.println(a);
                if (a == null || "".equals(a)) {
                    return;
                }
                listap.add(Double.parseDouble(a));
            }
            // listap.add(0.0);
            // listap.add(0.0);
            // listap.add(0.0);
            // listap.add(0.0);
            // listap.add(0.0);
            // listap.add(0.0);
            listap.add(0D);
            listap.add(0D);
            listap.add(0D);
            listap.add(0D);
            listap.add(0D);
            listap.add(0D);
            listap.add(0D);
            listap.add(0D);
            listap.add(0D);
            listap.add(0D);
            listap.add(0D);
            listap.add(0D);
            FishingUtil.ap = listap.toArray(new Double[0]);
        }
        String apt = RedisHelper.get("apt");
        // // System.out.println("apt*****************:" + apt);
        if (apt != null && apt.length() != 0) {
            apt = apt.substring(apt.lastIndexOf("[") + 1).replaceAll("]", "");
            String[] p1 = apt.split(",");
            List<Long> listapt = new ArrayList<>();
            for (String p : p1) {
                // // System.out.println(p);
                if (p == null || "".equals(p)) {
                    return;
                }
                listapt.add(new Double(Double.parseDouble(p)).longValue());
            }
            listapt.add(0L);
            listapt.add(0L);
            listapt.add(0L);
            listapt.add(0L);
            listapt.add(0L);
            listapt.add(0L);
            listapt.add(0L);
            listapt.add(0L);
            listapt.add(0L);
            listapt.add(0L);
            listapt.add(0L);
            listapt.add(0L);
            FishingUtil.apt = listapt.toArray(new Long[0]);
        }

        String recMax = RedisHelper.get("recMax");
        // // System.out.println("recMax*****************:" + recMax);
        if (recMax != null && recMax.length() != 0) {
            recMax = recMax.substring(recMax.lastIndexOf("[") + 1).replaceAll("]", "");
            String[] p1 = recMax.split(",");
            List<Long> listrecMax = new ArrayList<>();
            for (String p : p1) {
                // // System.out.println(p);
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
            FishingUtil.recMax = listrecMax.toArray(new Long[0]);
        }

        String recMin = RedisHelper.get("recMin");
        // // System.out.println("recMin*****************:" + recMin);
        if (recMin != null && recMin.length() != 0) {
            recMin = recMin.substring(recMin.lastIndexOf("[") + 1).replaceAll("]", "");
            String[] p1 = recMin.split(",");
            List<Long> listrecMin = new ArrayList<>();
            for (String p : p1) {
                // // System.out.println(p);
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
            FishingUtil.recMin = listrecMin.toArray(new Long[0]);
        }
        resultMap.put("q0", FishingUtil.q0);
        resultMap.put("ap", FishingUtil.ap);
        resultMap.put("apt", FishingUtil.apt);
        resultMap.put("recMax", FishingUtil.recMax);
        resultMap.put("recMin", FishingUtil.recMin);

        String ap1 = RedisHelper.get("ap1");
        // // System.out.println("ap1*****************:" + ap1);
        if (ap1 != null && ap1.length() != 0) {
            ap1 = ap1.substring(ap1.lastIndexOf("[") + 1).replaceAll("]", "");
            String[] ap11 = ap1.split(",");
            List<Double> listap = new ArrayList<>();
            for (String a : ap11) {
                // // System.out.println(a);
                if (a == null || "".equals(a)) {
                    return;
                }
                listap.add(Double.parseDouble(a));
            }
            listap.add(0D);
            listap.add(0D);
            listap.add(0D);
            listap.add(0D);
            listap.add(0D);
            listap.add(0D);
            listap.add(0D);
            listap.add(0D);
            listap.add(0D);
            listap.add(0D);
            listap.add(0D);
            listap.add(0D);
            FishingUtil.ap1 = listap.toArray(new Double[0]);
        }
        String apt1 = RedisHelper.get("apt1");
        // // System.out.println("apt1*****************:" + apt1);
        if (apt1 != null && apt1.length() != 0) {
            apt1 = apt1.substring(apt1.lastIndexOf("[") + 1).replaceAll("]", "");
            String[] p1 = apt1.split(",");
            List<Long> listapt = new ArrayList<>();
            for (String p : p1) {
                // // System.out.println(p);
                if (p == null || "".equals(p)) {
                    return;
                }
                listapt.add(new Double(Double.parseDouble(p)).longValue());
            }
            listapt.add(0L);
            listapt.add(0L);
            listapt.add(0L);
            listapt.add(0L);
            listapt.add(0L);
            listapt.add(0L);
            listapt.add(0L);
            listapt.add(0L);
            listapt.add(0L);
            listapt.add(0L);
            listapt.add(0L);
            listapt.add(0L);
            FishingUtil.apt1 = listapt.toArray(new Long[0]);
        }

        String recMax1 = RedisHelper.get("recMax1");
        // // System.out.println("recMax1*****************:" + recMax1);
        if (recMax1 != null && recMax1.length() != 0) {
            recMax1 = recMax1.substring(recMax1.lastIndexOf("[") + 1).replaceAll("]", "");
            String[] p1 = recMax1.split(",");
            List<Long> listrecMax = new ArrayList<>();
            for (String p : p1) {
                // // System.out.println(p);
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
            FishingUtil.recMax1 = listrecMax.toArray(new Long[0]);
        }

        String recMin1 = RedisHelper.get("recMin1");
        // // System.out.println("recMin1*****************:" + recMin1);
        if (recMin1 != null && recMin1.length() != 0) {
            recMin1 = recMin1.substring(recMin1.lastIndexOf("[") + 1).replaceAll("]", "");
            String[] p1 = recMin1.split(",");
            List<Long> listrecMin = new ArrayList<>();
            for (String p : p1) {
                // // System.out.println(p);
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
            FishingUtil.recMin1 = listrecMin.toArray(new Long[0]);
        }
        resultMap.put("ap1", FishingUtil.ap1);
        resultMap.put("apt1", FishingUtil.apt1);
        resultMap.put("recMax1", FishingUtil.recMax1);
        resultMap.put("recMin1", FishingUtil.recMin1);

        String ap2 = RedisHelper.get("ap2");
        // // System.out.println("ap2*****************:" + ap2);
        if (ap2 != null && ap2.length() != 0) {
            ap2 = ap2.substring(ap2.lastIndexOf("[") + 1).replaceAll("]", "");
            String[] ap11 = ap2.split(",");
            List<Double> listap = new ArrayList<>();
            for (String a : ap11) {
                // // System.out.println(a);
                if (a == null || "".equals(a)) {
                    return;
                }
                listap.add(Double.parseDouble(a));
            }
            listap.add(0D);
            listap.add(0D);
            listap.add(0D);
            listap.add(0D);
            listap.add(0D);
            listap.add(0D);
            listap.add(0D);
            listap.add(0D);
            listap.add(0D);
            listap.add(0D);
            listap.add(0D);
            listap.add(0D);
            FishingUtil.ap2 = listap.toArray(new Double[0]);
        }
        String apt2 = RedisHelper.get("apt2");
        // // System.out.println("apt2*****************:" + apt2);
        if (apt2 != null && apt2.length() != 0) {
            apt2 = apt2.substring(apt2.lastIndexOf("[") + 1).replaceAll("]", "");
            String[] p1 = apt2.split(",");
            List<Long> listapt = new ArrayList<>();
            for (String p : p1) {
                // // System.out.println(p);
                if (p == null || "".equals(p)) {
                    return;
                }
                listapt.add(new Double(Double.parseDouble(p)).longValue());
            }
            listapt.add(0L);
            listapt.add(0L);
            listapt.add(0L);
            listapt.add(0L);
            listapt.add(0L);
            listapt.add(0L);
            listapt.add(0L);
            listapt.add(0L);
            listapt.add(0L);
            listapt.add(0L);
            listapt.add(0L);
            listapt.add(0L);
            FishingUtil.apt2 = listapt.toArray(new Long[0]);
        }

        String recMax2 = RedisHelper.get("recMax2");
        // // System.out.println("recMax2*****************:" + recMax2);
        if (recMax2 != null && recMax2.length() != 0) {
            recMax2 = recMax2.substring(recMax2.lastIndexOf("[") + 1).replaceAll("]", "");
            String[] p1 = recMax2.split(",");
            List<Long> listrecMax = new ArrayList<>();
            for (String p : p1) {
                // // System.out.println(p);
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
            FishingUtil.recMax2 = listrecMax.toArray(new Long[0]);
        }

        String recMin2 = RedisHelper.get("recMin2");
        // // System.out.println("recMin2*****************:" + recMin2);
        if (recMin2 != null && recMin2.length() != 0) {
            recMin2 = recMin2.substring(recMin2.lastIndexOf("[") + 1).replaceAll("]", "");
            String[] p1 = recMin2.split(",");
            List<Long> listrecMin = new ArrayList<>();
            for (String p : p1) {
                // // System.out.println(p);
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
            FishingUtil.recMin2 = listrecMin.toArray(new Long[0]);
        }
        resultMap.put("ap2", FishingUtil.ap2);
        resultMap.put("apt2", FishingUtil.apt2);
        resultMap.put("recMax2", FishingUtil.recMax2);
        resultMap.put("recMin2", FishingUtil.recMin2);

        String ap3 = RedisHelper.get("ap3");
        // // System.out.println("ap3*****************:" + ap3);
        if (ap3 != null && ap3.length() != 0) {
            ap3 = ap3.substring(ap3.lastIndexOf("[") + 1).replaceAll("]", "");
            String[] ap11 = ap3.split(",");
            List<Double> listap = new ArrayList<>();
            for (String a : ap11) {
                // // System.out.println(a);
                if (a == null || "".equals(a)) {
                    return;
                }
                listap.add(Double.parseDouble(a));
            }
            listap.add(0D);
            listap.add(0D);
            listap.add(0D);
            listap.add(0D);
            listap.add(0D);
            listap.add(0D);
            listap.add(0D);
            listap.add(0D);
            listap.add(0D);
            listap.add(0D);
            listap.add(0D);
            listap.add(0D);
            FishingUtil.ap3 = listap.toArray(new Double[0]);
        }
        String apt3 = RedisHelper.get("apt3");
        // // System.out.println("apt3*****************:" + apt3);
        if (apt3 != null && apt3.length() != 0) {
            apt3 = apt3.substring(apt3.lastIndexOf("[") + 1).replaceAll("]", "");
            String[] p1 = apt3.split(",");
            List<Long> listapt = new ArrayList<>();
            for (String p : p1) {
                // // System.out.println(p);
                if (p == null || "".equals(p)) {
                    return;
                }
                listapt.add(new Double(Double.parseDouble(p)).longValue());
            }
            listapt.add(0L);
            listapt.add(0L);
            listapt.add(0L);
            listapt.add(0L);
            listapt.add(0L);
            listapt.add(0L);
            listapt.add(0L);
            listapt.add(0L);
            listapt.add(0L);
            listapt.add(0L);
            listapt.add(0L);
            listapt.add(0L);
            FishingUtil.apt3 = listapt.toArray(new Long[0]);
        }

        String recMax3 = RedisHelper.get("recMax3");
        // // System.out.println("recMax3*****************:" + recMax3);
        if (recMax3 != null && recMax3.length() != 0) {
            recMax3 = recMax3.substring(recMax3.lastIndexOf("[") + 1).replaceAll("]", "");
            String[] p1 = recMax3.split(",");
            List<Long> listrecMax = new ArrayList<>();
            for (String p : p1) {
                // // System.out.println(p);
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
            FishingUtil.recMax3 = listrecMax.toArray(new Long[0]);
        }

        String recMin3 = RedisHelper.get("recMin3");
        // // System.out.println("recMin3*****************:" + recMin3);
        if (recMin3 != null && recMin3.length() != 0) {
            recMin3 = recMin3.substring(recMin3.lastIndexOf("[") + 1).replaceAll("]", "");
            String[] p1 = recMin3.split(",");
            List<Long> listrecMin = new ArrayList<>();
            for (String p : p1) {
                // // System.out.println(p);
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
            FishingUtil.recMin3 = listrecMin.toArray(new Long[0]);
        }
        resultMap.put("ap3", FishingUtil.ap3);
        resultMap.put("apt3", FishingUtil.apt3);
        resultMap.put("recMax3", FishingUtil.recMax3);
        resultMap.put("recMin3", FishingUtil.recMin3);

        String pumpNum = RedisHelper.get("pumpNum");
        // // System.out.println("pumpNum*****************:" + pumpNum);
        if (pumpNum != null && pumpNum.length() != 0) {
            pumpNum = pumpNum.substring(pumpNum.lastIndexOf("[") + 1).replaceAll("]", "");
            String[] p1 = pumpNum.split(",");
            List<Long> listrecMax = new ArrayList<>();
            for (String p : p1) {
                // // System.out.println(p);
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
            FishingUtil.pumpNum = listrecMax.toArray(new Long[0]);
        }

        String pump = RedisHelper.get("pump");
        // // System.out.println("pump*****************:" + pump);
        if (pump != null && pump.length() != 0) {
            pump = pump.substring(pump.lastIndexOf("[") + 1).replaceAll("]", "");
            String[] p1 = pump.split(",");
            List<Long> listrecMin = new ArrayList<>();
            for (String p : p1) {
                // // System.out.println(p);
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
            FishingUtil.pump = listrecMin.toArray(new Long[0]);
        }
        resultMap.put("pump", FishingUtil.pump);
        resultMap.put("pumpNum", FishingUtil.pumpNum);

        String gProd = RedisHelper.get("gProd");
        // // System.out.println("gProd*****************:" + gProd);
        if (gProd != null && gProd.length() != 0) {
            gProd = gProd.substring(gProd.lastIndexOf("[") + 1).replaceAll("]", "");
            String[] p1 = gProd.split(",");
            List<Double> listrecMax = new ArrayList<>();
            for (String p : p1) {
                // // System.out.println(p);
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
        // // System.out.println("lcf*****************:" + lcf);
        if (lcf != null && lcf.length() != 0) {
            lcf = lcf.substring(lcf.lastIndexOf("[") + 1).replaceAll("]", "");
            String[] p1 = lcf.split(",");
            List<Double> listrecMax = new ArrayList<>();
            for (String p : p1) {
                // // System.out.println(p);
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
        // // System.out.println("dz*****************:" + dz);
        if (dz != null && dz.length() != 0) {
            dz = dz.substring(dz.lastIndexOf("[") + 1).replaceAll("]", "");
            String[] p1 = dz.split(",");
            List<Double> listrecMax = new ArrayList<>();
            for (String p : p1) {
                // // System.out.println(p);
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
        // // System.out.println("bfsMin*****************:" + bfsMin);
        if (bfsMin != null && bfsMin.length() != 0) {
            bfsMin = bfsMin.substring(bfsMin.lastIndexOf("[") + 1).replaceAll("]", "");
            String[] p1 = bfsMin.split(",");
            List<Double> listrecMax = new ArrayList<>();
            for (String p : p1) {
                // // System.out.println(p);
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
        // // System.out.println("bfsMax*****************:" + bfsMax);
        if (bfsMax != null && bfsMax.length() != 0) {
            bfsMax = bfsMax.substring(bfsMax.lastIndexOf("[") + 1).replaceAll("]", "");
            String[] p1 = bfsMax.split(",");
            List<Double> listrecMax = new ArrayList<>();
            for (String p : p1) {
                // // System.out.println(p);
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
        resultMap.put("gProd", FishingUtil.gProd);
        resultMap.put("lcf", FishingUtil.lcf);
        resultMap.put("dz", FishingUtil.dz);
        resultMap.put("bfsMin", FishingUtil.bfsMin);
        resultMap.put("bfsMax", FishingUtil.bfsMax);

        // final JSONArray tp_c = JSON.parseArray(RedisUtil.val("tp_c", "[0.0]"));
        // if (tp_c != null) {
        // for (int i = 0; i < tp_c.size(); i++) {
        // FishingUtil.tp_c[i] = tp_c.getDoubleValue(i);
        // }
        // }
        // final JSONArray tp_z = JSON.parseArray(RedisUtil.val("tp_z", "[0.0]"));
        // if (tp_z != null) {
        // for (int i = 0; i < tp_z.size(); i++) {
        // FishingUtil.tp_z[i] = tp_z.getDoubleValue(i);
        // }
        // }
        // final JSONArray tp_g = JSON.parseArray(RedisUtil.val("tp_g", "[0.0]"));
        // if (tp_g != null) {
        // for (int i = 0; i < tp_g.size(); i++) {
        // FishingUtil.tp_g[i] = tp_g.getDoubleValue(i);
        // }
        // }
        // final JSONArray tp_s = JSON.parseArray(RedisUtil.val("tp_s", "[0.0]"));
        // if (tp_s != null) {
        // for (int i = 0; i < tp_s.size(); i++) {
        // FishingUtil.tp_s[i] = tp_s.getDoubleValue(i);
        // }
        // }
        // resultMap.put("tp_c", FishingUtil.tp_c);
        // resultMap.put("tp_z", FishingUtil.tp_z);
        // resultMap.put("tp_g", FishingUtil.tp_g);
        // resultMap.put("tp_s", FishingUtil.tp_s);

        String burstOne = RedisHelper.get("burstOne");
        // // System.out.println("burstOne*****************:" + burstOne);
        if (burstOne != null && burstOne.length() != 0) {
            burstOne = burstOne.substring(burstOne.lastIndexOf("[") + 1).replaceAll("]", "");
            String[] p1 = burstOne.split(",");
            List<Long> listrecMax = new ArrayList<>();
            for (String p : p1) {
                // // System.out.println(p);
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
            FishingUtil.burstOne = listrecMax.toArray(new Long[0]);
        }

        String recOne = RedisHelper.get("recOne");
        // // System.out.println("recOne*****************:" + recOne);
        if (recOne != null && recOne.length() != 0) {
            recOne = recOne.substring(recOne.lastIndexOf("[") + 1).replaceAll("]", "");
            String[] p1 = recOne.split(",");
            List<Long> listrecMin = new ArrayList<>();
            for (String p : p1) {
                // // System.out.println(p);
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
            FishingUtil.recOne = listrecMin.toArray(new Long[0]);
        }
        resultMap.put("recOne", FishingUtil.recOne);
        resultMap.put("burstOne", FishingUtil.burstOne);

        String balanceOne = RedisHelper.get("balanceOne");
        // // // System.out.println("balanceOne*****************:" + balanceOne);
        if (balanceOne != null && balanceOne.length() != 0) {
            balanceOne = balanceOne.substring(balanceOne.lastIndexOf("[") + 1).replaceAll("]", "");
            String[] p1 = balanceOne.split(",");
            List<Long> listrecMax = new ArrayList<>();
            for (String p : p1) {
                // // // System.out.println(p);
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
        // // // System.out.println("balanceBuOne*****************:" + balanceBuOne);
        if (balanceBuOne != null && balanceBuOne.length() != 0) {
            balanceBuOne = balanceBuOne.substring(balanceBuOne.lastIndexOf("[") + 1).replaceAll("]", "");
            String[] p1 = balanceBuOne.split(",");
            List<Long> listrecMin = new ArrayList<>();
            for (String p : p1) {
                // // // System.out.println(p);
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
        resultMap.put("balanceOne", FishingUtil.balanceOne);
        resultMap.put("balanceBuOne", FishingUtil.balanceBuOne);

        String gcOne = RedisHelper.get("gcOne");
        // // // System.out.println("gcOne*****************:" + gcOne);
        if (gcOne != null && gcOne.length() != 0) {
            gcOne = gcOne.substring(gcOne.lastIndexOf("[") + 1).replaceAll("]", "");
            String[] p1 = gcOne.split(",");
            List<Long> listrecMax = new ArrayList<>();
            for (String p : p1) {
                // // // System.out.println(p);
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
        // // // System.out.println("gcBurstOne*****************:" + gcBurstOne);
        if (gcBurstOne != null && gcBurstOne.length() != 0) {
            gcBurstOne = gcBurstOne.substring(gcBurstOne.lastIndexOf("[") + 1).replaceAll("]", "");
            String[] p1 = gcBurstOne.split(",");
            List<Long> listrecMin = new ArrayList<>();
            for (String p : p1) {
                // // // System.out.println(p);
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
        resultMap.put("gcOne", FishingUtil.gcOne);
        resultMap.put("gcBurstOne", FishingUtil.gcBurstOne);

        String burstTwo = RedisHelper.get("burstTwo");
        // // // System.out.println("burstTwo*****************:" + burstTwo);
        if (burstTwo != null && burstTwo.length() != 0) {
            burstTwo = burstTwo.substring(burstTwo.lastIndexOf("[") + 1).replaceAll("]", "");
            String[] p1 = burstTwo.split(",");
            List<Long> listrecMax = new ArrayList<>();
            for (String p : p1) {
                // // // System.out.println(p);
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
        // // // System.out.println("recTwo*****************:" + recTwo);
        if (recTwo != null && recTwo.length() != 0) {
            recTwo = recTwo.substring(recTwo.lastIndexOf("[") + 1).replaceAll("]", "");
            String[] p1 = recTwo.split(",");
            List<Long> listrecMin = new ArrayList<>();
            for (String p : p1) {
                // // System.out.println(p);
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
        resultMap.put("recTwo", FishingUtil.recTwo);
        resultMap.put("burstTwo", FishingUtil.burstTwo);

        String balanceTwo = RedisHelper.get("balanceTwo");
        // // System.out.println("balanceTwo*****************:" + balanceTwo);
        if (balanceTwo != null && balanceTwo.length() != 0) {
            balanceTwo = balanceTwo.substring(balanceTwo.lastIndexOf("[") + 1).replaceAll("]", "");
            String[] p1 = balanceTwo.split(",");
            List<Long> listrecMax = new ArrayList<>();
            for (String p : p1) {
                // // System.out.println(p);
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
        // // System.out.println("balanceBuTwo*****************:" + balanceBuTwo);
        if (balanceBuTwo != null && balanceBuTwo.length() != 0) {
            balanceBuTwo = balanceBuTwo.substring(balanceBuTwo.lastIndexOf("[") + 1).replaceAll("]", "");
            String[] p1 = balanceBuTwo.split(",");
            List<Long> listrecMin = new ArrayList<>();
            for (String p : p1) {
                // // System.out.println(p);
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
        resultMap.put("balanceTwo", FishingUtil.balanceTwo);
        resultMap.put("balanceBuTwo", FishingUtil.balanceBuTwo);

        String gcTwo = RedisHelper.get("gcTwo");
        // // System.out.println("gcTwo*****************:" + gcTwo);
        if (gcTwo != null && gcTwo.length() != 0) {
            gcTwo = gcTwo.substring(gcTwo.lastIndexOf("[") + 1).replaceAll("]", "");
            String[] p1 = gcTwo.split(",");
            List<Long> listrecMax = new ArrayList<>();
            for (String p : p1) {
                // // System.out.println(p);
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
        // // System.out.println("gcBurstTwo*****************:" + gcBurstTwo);
        if (gcBurstTwo != null && gcBurstTwo.length() != 0) {
            gcBurstTwo = gcBurstTwo.substring(gcBurstTwo.lastIndexOf("[") + 1).replaceAll("]", "");
            String[] p1 = gcBurstTwo.split(",");
            List<Long> listrecMin = new ArrayList<>();
            for (String p : p1) {
                // // System.out.println(p);
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
        resultMap.put("gcTwo", FishingUtil.gcTwo);
        resultMap.put("gcBurstTwo", FishingUtil.gcBurstTwo);

        String burstThree = RedisHelper.get("burstThree");
        // // System.out.println("burstThree*****************:" + burstThree);
        if (burstThree != null && burstThree.length() != 0) {
            burstThree = burstThree.substring(burstThree.lastIndexOf("[") + 1).replaceAll("]", "");
            String[] p1 = burstThree.split(",");
            List<Long> listrecMax = new ArrayList<>();
            for (String p : p1) {
                // // System.out.println(p);
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
        // // System.out.println("recThree*****************:" + recThree);
        if (recThree != null && recThree.length() != 0) {
            recThree = recThree.substring(recThree.lastIndexOf("[") + 1).replaceAll("]", "");
            String[] p1 = recThree.split(",");
            List<Long> listrecMin = new ArrayList<>();
            for (String p : p1) {
                // // System.out.println(p);
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
        resultMap.put("recThree", FishingUtil.recThree);
        resultMap.put("burstThree", FishingUtil.burstThree);

        String balanceThree = RedisHelper.get("balanceThree");
        // // System.out.println("balanceThree*****************:" + balanceThree);
        if (balanceThree != null && balanceThree.length() != 0) {
            balanceThree = balanceThree.substring(balanceThree.lastIndexOf("[") + 1).replaceAll("]", "");
            String[] p1 = balanceThree.split(",");
            List<Long> listrecMax = new ArrayList<>();
            for (String p : p1) {
                // // System.out.println(p);
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
        // // System.out.println("balanceBuThree*****************:" + balanceBuThree);
        if (balanceBuThree != null && balanceBuThree.length() != 0) {
            balanceBuThree = balanceBuThree.substring(balanceBuThree.lastIndexOf("[") + 1).replaceAll("]", "");
            String[] p1 = balanceBuThree.split(",");
            List<Long> listrecMin = new ArrayList<>();
            for (String p : p1) {
                // // System.out.println(p);
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
        resultMap.put("balanceThree", FishingUtil.balanceThree);
        resultMap.put("balanceBuThree", FishingUtil.balanceBuThree);

        String gcThree = RedisHelper.get("gcThree");
        // // System.out.println("gcThree*****************:" + gcThree);
        if (gcThree != null && gcThree.length() != 0) {
            gcThree = gcThree.substring(gcThree.lastIndexOf("[") + 1).replaceAll("]", "");
            String[] p1 = gcThree.split(",");
            List<Long> listrecMax = new ArrayList<>();
            for (String p : p1) {
                // // System.out.println(p);
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
        // // System.out.println("gcBurstThree*****************:" + gcBurstThree);
        if (gcBurstThree != null && gcBurstThree.length() != 0) {
            gcBurstThree = gcBurstThree.substring(gcBurstThree.lastIndexOf("[") + 1).replaceAll("]", "");
            String[] p1 = gcBurstThree.split(",");
            List<Long> listrecMin = new ArrayList<>();
            for (String p : p1) {
                // // System.out.println(p);
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
        resultMap.put("gcThree", FishingUtil.gcThree);
        resultMap.put("gcBurstThree", FishingUtil.gcBurstThree);

        String burstFour = RedisHelper.get("burstFour");
        // // System.out.println("burstFour*****************:" + burstFour);
        if (burstFour != null && burstFour.length() != 0) {
            burstFour = burstFour.substring(burstFour.lastIndexOf("[") + 1).replaceAll("]", "");
            String[] p1 = burstFour.split(",");
            List<Long> listrecMax = new ArrayList<>();
            for (String p : p1) {
                // // System.out.println(p);
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
        // // System.out.println("recFour*****************:" + recFour);
        if (recFour != null && recFour.length() != 0) {
            recFour = recFour.substring(recFour.lastIndexOf("[") + 1).replaceAll("]", "");
            String[] p1 = recFour.split(",");
            List<Long> listrecMin = new ArrayList<>();
            for (String p : p1) {
                // // System.out.println(p);
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
        resultMap.put("recFour", FishingUtil.recFour);
        resultMap.put("burstFour", FishingUtil.burstFour);

        String balanceFour = RedisHelper.get("balanceFour");
        // // System.out.println("balanceFour*****************:" + balanceFour);
        if (balanceFour != null && balanceFour.length() != 0) {
            balanceFour = balanceFour.substring(balanceFour.lastIndexOf("[") + 1).replaceAll("]", "");
            String[] p1 = balanceFour.split(",");
            List<Long> listrecMax = new ArrayList<>();
            for (String p : p1) {
                // // System.out.println(p);
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
        // // System.out.println("balanceBuFour*****************:" + balanceBuFour);
        if (balanceBuFour != null && balanceBuFour.length() != 0) {
            balanceBuFour = balanceBuFour.substring(balanceBuFour.lastIndexOf("[") + 1).replaceAll("]", "");
            String[] p1 = balanceBuFour.split(",");
            List<Long> listrecMin = new ArrayList<>();
            for (String p : p1) {
                // // System.out.println(p);
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
        resultMap.put("balanceFour", FishingUtil.balanceFour);
        resultMap.put("balanceBuFour", FishingUtil.balanceBuFour);

        String gcFour = RedisHelper.get("gcFour");
        // // System.out.println("gcFour*****************:" + gcFour);
        if (gcFour != null && gcFour.length() != 0) {
            gcFour = gcFour.substring(gcFour.lastIndexOf("[") + 1).replaceAll("]", "");
            String[] p1 = gcFour.split(",");
            List<Long> listrecMax = new ArrayList<>();
            for (String p : p1) {
                // // System.out.println(p);
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
        // // System.out.println("gcBurstFour*****************:" + gcBurstFour);
        if (gcBurstFour != null && gcBurstFour.length() != 0) {
            gcBurstFour = gcBurstFour.substring(gcBurstFour.lastIndexOf("[") + 1).replaceAll("]", "");
            String[] p1 = gcBurstFour.split(",");
            List<Long> listrecMin = new ArrayList<>();
            for (String p : p1) {
                // // System.out.println(p);
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
        resultMap.put("gcFour", FishingUtil.gcFour);
        resultMap.put("gcBurstFour", FishingUtil.gcBurstFour);

        resultMap.put("tenDrawPercent", RedisHelper.get("Osee:FightTen:Config:WinDrawPercent"));

        String PXMin = RedisHelper.get("PXMin");
        // // System.out.println("PXMin*****************:" + PXMin);
        if (PXMin != null && PXMin.length() != 0) {
            PXMin = PXMin.substring(PXMin.lastIndexOf("[") + 1).replaceAll("]", "");
            String[] px = PXMin.split(",");
            List<Double> listap = new ArrayList<>();
            for (String a : px) {
                // // System.out.println(a);
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
        // // System.out.println("FPMin*****************:" + FPMin);
        if (FPMin != null && FPMin.length() != 0) {
            FPMin = FPMin.substring(FPMin.lastIndexOf("[") + 1).replaceAll("]", "");
            String[] px = FPMin.split(",");
            List<Double> listap = new ArrayList<>();
            for (String a : px) {
                // // System.out.println(a);
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
        resultMap.put("PXMin", FishingUtil.PXMin);
        resultMap.put("FPMIn", FishingUtil.FPMin);

        String PXMax = RedisHelper.get("PXMax");
        // // System.out.println("PXMax*****************:" + PXMax);
        if (PXMax != null && PXMax.length() != 0) {
            PXMax = PXMax.substring(PXMax.lastIndexOf("[") + 1).replaceAll("]", "");
            String[] px = PXMax.split(",");
            List<Double> listap = new ArrayList<>();
            for (String a : px) {
                // // System.out.println(a);
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
        // // System.out.println("FPMax*****************:" + FPMax);
        if (FPMax != null && FPMax.length() != 0) {
            FPMax = FPMax.substring(FPMax.lastIndexOf("[") + 1).replaceAll("]", "");
            String[] px = FPMax.split(",");
            List<Double> listap = new ArrayList<>();
            for (String a : px) {
                // // System.out.println(a);
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
        resultMap.put("PXMax", FishingUtil.PXMax);
        resultMap.put("FPMax", FishingUtil.FPMax);

        String Pdx = RedisHelper.get("pdx");
        // // System.out.println("pdx*****************:" + Pdx);
        if (Pdx != null && Pdx.length() != 0) {
            Pdx = Pdx.substring(Pdx.lastIndexOf("[") + 1).replaceAll("]", "");
            String[] px = Pdx.split(",");
            List<Double> listap = new ArrayList<>();
            for (String a : px) {
                // // System.out.println(a);
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
        resultMap.put("pdx", FishingUtil.Pdx);

        String pdxp = RedisHelper.get("pdxp");
        // // System.out.println("pdxp*****************:" + pdxp);
        if (pdxp != null && pdxp.length() != 0) {
            pdxp = pdxp.substring(pdxp.lastIndexOf("[") + 1).replaceAll("]", "");
            String[] px = pdxp.split(",");
            List<Double> listap = new ArrayList<>();
            for (String a : px) {
                // // System.out.println(a);
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
        resultMap.put("pdxp", FishingUtil.Pdxp);

        String Xdx = RedisHelper.get("xdx");
        // // System.out.println("Xdx*****************:" + Xdx);
        if (Xdx != null && Xdx.length() != 0) {
            Xdx = Xdx.substring(Xdx.lastIndexOf("[") + 1).replaceAll("]", "");
            String[] px = Xdx.split(",");
            List<Double> listap = new ArrayList<>();
            for (String a : px) {
                // // System.out.println(a);
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
        resultMap.put("xdx", FishingUtil.Xdx);

        String xdxp = RedisHelper.get("xdxp");
        // // System.out.println("xdxp*****************:" + xdxp);
        if (xdxp != null && xdxp.length() != 0) {
            xdxp = xdxp.substring(xdxp.lastIndexOf("[") + 1).replaceAll("]", "");
            String[] px = xdxp.split(",");
            List<Double> listap = new ArrayList<>();
            for (String a : px) {
                // // System.out.println(a);
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
        resultMap.put("xdxp", FishingUtil.Xdxp);

        String recOne1 = RedisHelper.get("recOne1");
        // // System.out.println("recOne1*****************:" + recOne1);
        if (recOne1 != null && recOne1.length() != 0) {
            recOne1 = recOne1.substring(recOne1.lastIndexOf("[") + 1).replaceAll("]", "");
            String[] q1 = recOne1.split(",");
            List<Long> list = new ArrayList<>();
            for (String q : q1) {
                // // System.out.println(q);
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
        // // System.out.println("recTwo1*****************:" + recTwo1);
        if (recTwo1 != null && recTwo1.length() != 0) {
            recTwo1 = recTwo1.substring(recTwo1.lastIndexOf("[") + 1).replaceAll("]", "");
            String[] q1 = recTwo1.split(",");
            List<Long> list = new ArrayList<>();
            for (String q : q1) {
                // // System.out.println(q);
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
        // // System.out.println("burstOne1*****************:" + burstOne1);
        if (burstOne1 != null && burstOne1.length() != 0) {
            burstOne1 = burstOne1.substring(burstOne1.lastIndexOf("[") + 1).replaceAll("]", "");
            String[] q1 = burstOne1.split(",");
            List<Long> list = new ArrayList<>();
            for (String q : q1) {
                // // System.out.println(q);
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
        // // System.out.println("burstTwo1*****************:" + burstTwo1);
        if (burstTwo1 != null && burstTwo1.length() != 0) {
            burstTwo1 = burstTwo1.substring(burstTwo1.lastIndexOf("[") + 1).replaceAll("]", "");
            String[] q1 = burstTwo1.split(",");
            List<Long> list = new ArrayList<>();
            for (String q : q1) {
                // // System.out.println(q);
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
        // // System.out.println("balanceOne1*****************:" + balanceOne1);
        if (balanceOne1 != null && balanceOne1.length() != 0) {
            balanceOne1 = balanceOne1.substring(balanceOne1.lastIndexOf("[") + 1).replaceAll("]", "");
            String[] q1 = balanceOne1.split(",");
            List<Long> list = new ArrayList<>();
            for (String q : q1) {
                // // System.out.println(q);
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
        // // System.out.println("balanceTwo1*****************:" + balanceTwo1);
        if (balanceTwo1 != null && balanceTwo1.length() != 0) {
            balanceTwo1 = balanceTwo1.substring(balanceTwo1.lastIndexOf("[") + 1).replaceAll("]", "");
            String[] q1 = balanceTwo1.split(",");
            List<Long> list = new ArrayList<>();
            for (String q : q1) {
                // // System.out.println(q);
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
        resultMap.put("recOne1", FishingUtil.recOne1);
        resultMap.put("recTwo1", FishingUtil.recTwo1);
        resultMap.put("burstOne1", FishingUtil.burstOne1);
        resultMap.put("burstTwo1", FishingUtil.burstTwo1);
        resultMap.put("balanceOne1", FishingUtil.balanceOne1);
        resultMap.put("balanceTwo1", FishingUtil.balanceTwo1);

        String recOne2 = RedisHelper.get("recOne2");
        // // System.out.println("recOne2*****************:" + recOne2);
        if (recOne2 != null && recOne2.length() != 0) {
            recOne2 = recOne2.substring(recOne2.lastIndexOf("[") + 1).replaceAll("]", "");
            String[] q1 = recOne.split(",");
            List<Long> list = new ArrayList<>();
            for (String q : q1) {
                // // System.out.println(q);
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
        // // System.out.println("recTwo2*****************:" + recTwo2);
        if (recTwo2 != null && recTwo2.length() != 0) {
            recTwo2 = recTwo2.substring(recTwo2.lastIndexOf("[") + 1).replaceAll("]", "");
            String[] q1 = recTwo2.split(",");
            List<Long> list = new ArrayList<>();
            for (String q : q1) {
                // // System.out.println(q);
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
        // // System.out.println("burstOne2*****************:" + burstOne2);
        if (burstOne2 != null && burstOne2.length() != 0) {
            burstOne2 = burstOne2.substring(burstOne2.lastIndexOf("[") + 1).replaceAll("]", "");
            String[] q1 = burstOne2.split(",");
            List<Long> list = new ArrayList<>();
            for (String q : q1) {
                // // System.out.println(q);
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
        // // System.out.println("burstTwo2*****************:" + burstTwo2);
        if (burstTwo2 != null && burstTwo2.length() != 0) {
            burstTwo2 = burstTwo2.substring(burstTwo2.lastIndexOf("[") + 1).replaceAll("]", "");
            String[] q1 = burstTwo2.split(",");
            List<Long> list = new ArrayList<>();
            for (String q : q1) {
                // // System.out.println(q);
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
        // // System.out.println("balanceOne2*****************:" + balanceOne2);
        if (balanceOne2 != null && balanceOne2.length() != 0) {
            balanceOne2 = balanceOne2.substring(balanceOne2.lastIndexOf("[") + 1).replaceAll("]", "");
            String[] q1 = balanceOne2.split(",");
            List<Long> list = new ArrayList<>();
            for (String q : q1) {
                // // System.out.println(q);
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
        // // System.out.println("balanceTwo2*****************:" + balanceTwo2);
        if (balanceTwo2 != null && balanceTwo2.length() != 0) {
            balanceTwo2 = balanceTwo2.substring(balanceTwo2.lastIndexOf("[") + 1).replaceAll("]", "");
            String[] q1 = balanceTwo2.split(",");
            List<Long> list = new ArrayList<>();
            for (String q : q1) {
                // // System.out.println(q);
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
        resultMap.put("recOne2", FishingUtil.recOne2);
        resultMap.put("recTwo2", FishingUtil.recTwo2);
        resultMap.put("burstOne2", FishingUtil.burstOne2);
        resultMap.put("burstTwo2", FishingUtil.burstTwo2);
        resultMap.put("balanceOne2", FishingUtil.balanceOne2);
        resultMap.put("balanceTwo2", FishingUtil.balanceTwo2);

        String recOne3 = RedisHelper.get("recOne3");
        // // System.out.println("recOne3*****************:" + recOne3);
        if (recOne3 != null && recOne3.length() != 0) {
            recOne3 = recOne3.substring(recOne3.lastIndexOf("[") + 1).replaceAll("]", "");
            String[] q1 = recOne3.split(",");
            List<Long> list = new ArrayList<>();
            for (String q : q1) {
                // // System.out.println(q);
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
        // // System.out.println("recTwo3*****************:" + recTwo3);
        if (recTwo3 != null && recTwo3.length() != 0) {
            recTwo3 = recTwo3.substring(recTwo3.lastIndexOf("[") + 1).replaceAll("]", "");
            String[] q1 = recTwo3.split(",");
            List<Long> list = new ArrayList<>();
            for (String q : q1) {
                // // System.out.println(q);
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
        // // System.out.println("burstOne3*****************:" + burstOne3);
        if (burstOne3 != null && burstOne3.length() != 0) {
            burstOne3 = burstOne3.substring(burstOne3.lastIndexOf("[") + 1).replaceAll("]", "");
            String[] q1 = burstOne3.split(",");
            List<Long> list = new ArrayList<>();
            for (String q : q1) {
                // // System.out.println(q);
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
        // // System.out.println("burstTwo3*****************:" + burstTwo3);
        if (burstTwo3 != null && burstTwo3.length() != 0) {
            burstTwo3 = burstTwo3.substring(burstTwo3.lastIndexOf("[") + 1).replaceAll("]", "");
            String[] q1 = burstTwo3.split(",");
            List<Long> list = new ArrayList<>();
            for (String q : q1) {
                // // System.out.println(q);
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
        // // System.out.println("balanceOne3*****************:" + balanceOne3);
        if (balanceOne3 != null && balanceOne3.length() != 0) {
            balanceOne3 = balanceOne3.substring(balanceOne3.lastIndexOf("[") + 1).replaceAll("]", "");
            String[] q1 = balanceOne3.split(",");
            List<Long> list = new ArrayList<>();
            for (String q : q1) {
                // // System.out.println(q);
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
        // // System.out.println("balanceTwo3*****************:" + balanceTwo3);
        if (balanceTwo3 != null && balanceTwo3.length() != 0) {
            balanceTwo3 = balanceTwo3.substring(balanceTwo3.lastIndexOf("[") + 1).replaceAll("]", "");
            String[] q1 = balanceTwo3.split(",");
            List<Long> list = new ArrayList<>();
            for (String q : q1) {
                // // System.out.println(q);
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
        resultMap.put("recOne3", FishingUtil.recOne3);
        resultMap.put("recTwo3", FishingUtil.recTwo3);
        resultMap.put("burstOne3", FishingUtil.burstOne3);
        resultMap.put("burstTwo3", FishingUtil.burstTwo3);
        resultMap.put("balanceOne3", FishingUtil.balanceOne3);
        resultMap.put("balanceTwo3", FishingUtil.balanceTwo3);

        String recOne4 = RedisHelper.get("recOne4");
        // // System.out.println("recOne4*****************:" + recOne4);
        if (recOne4 != null && recOne4.length() != 0) {
            recOne4 = recOne4.substring(recOne4.lastIndexOf("[") + 1).replaceAll("]", "");
            String[] q1 = recOne4.split(",");
            List<Long> list = new ArrayList<>();
            for (String q : q1) {
                // // System.out.println(q);
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
        // // System.out.println("recTwo4*****************:" + recTwo4);
        if (recTwo4 != null && recTwo4.length() != 0) {
            recTwo4 = recTwo4.substring(recTwo4.lastIndexOf("[") + 1).replaceAll("]", "");
            String[] q1 = recTwo4.split(",");
            List<Long> list = new ArrayList<>();
            for (String q : q1) {
                // // System.out.println(q);
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
        // // System.out.println("burstOne4*****************:" + burstOne4);
        if (burstOne4 != null && burstOne4.length() != 0) {
            burstOne4 = burstOne4.substring(burstOne4.lastIndexOf("[") + 1).replaceAll("]", "");
            String[] q1 = burstOne4.split(",");
            List<Long> list = new ArrayList<>();
            for (String q : q1) {
                // // System.out.println(q);
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
        // // System.out.println("burstTwo4*****************:" + burstTwo4);
        if (burstTwo4 != null && burstTwo4.length() != 0) {
            burstTwo4 = burstTwo4.substring(burstTwo4.lastIndexOf("[") + 1).replaceAll("]", "");
            String[] q1 = burstTwo4.split(",");
            List<Long> list = new ArrayList<>();
            for (String q : q1) {
                // // System.out.println(q);
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
        // // System.out.println("balanceOne4*****************:" + balanceOne4);
        if (balanceOne4 != null && balanceOne4.length() != 0) {
            balanceOne4 = balanceOne4.substring(balanceOne4.lastIndexOf("[") + 1).replaceAll("]", "");
            String[] q1 = balanceOne4.split(",");
            List<Long> list = new ArrayList<>();
            for (String q : q1) {
                // // System.out.println(q);
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
        // // System.out.println("balanceTwo4*****************:" + balanceTwo4);
        if (balanceTwo4 != null && balanceTwo4.length() != 0) {
            balanceTwo4 = balanceTwo4.substring(balanceTwo4.lastIndexOf("[") + 1).replaceAll("]", "");
            String[] q1 = balanceTwo4.split(",");
            List<Long> list = new ArrayList<>();
            for (String q : q1) {
                // // System.out.println(q);
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
        resultMap.put("recOne4", FishingUtil.recOne4);
        resultMap.put("recTwo4", FishingUtil.recTwo4);
        resultMap.put("burstOne4", FishingUtil.burstOne4);
        resultMap.put("burstTwo4", FishingUtil.burstTwo4);
        resultMap.put("balanceOne4", FishingUtil.balanceOne4);
        resultMap.put("balanceTwo4", FishingUtil.balanceTwo4);


        resultMap.put("fishingGrandPrixInitPool", FishingGrandPrixManager.initPool);
        resultMap.put("fishingGrandPrixAP", RedisUtil.val(FishingGrandPrixManager.PLAYER_GRANDPRIX_CONFIG_AP_KEY, 0D));
        resultMap.put("fishingGrandPrixAPT",
                RedisUtil.val(FishingGrandPrixManager.PLAYER_GRANDPRIX_CONFIG_APT_KEY, 100L));
        resultMap.put("fishingGrandPrixBP", RedisUtil.val(FishingGrandPrixManager.PLAYER_GRANDPRIX_CONFIG_BP_KEY, 0D));
        resultMap.put("fishingGrandPrixQZ", RedisUtil.val(FishingGrandPrixManager.PLAYER_GRANDPRIX_CONFIG_QZ_KEY, 0L));
        resultMap.put("fishingGrandPrixPQ", RedisUtil.val(FishingGrandPrixManager.PLAYER_GRANDPRIX_CONFIG_PQ_KEY, 0D));
        resultMap.put("fishingGrandPrixQY", RedisUtil.val(FishingGrandPrixManager.PLAYER_GRANDPRIX_CONFIG_QY_KEY, 0L));
        resultMap.put("fishingGrandPrixPA", RedisUtil.val(FishingGrandPrixManager.PLAYER_GRANDPRIX_CONFIG_PA_KEY, 0D));
        resultMap.put("fishingGrandPrixQS", RedisUtil.val(FishingGrandPrixManager.PLAYER_GRANDPRIX_CONFIG_QS_KEY, 0L));
        resultMap.put("fishingGrandPrixPW", RedisUtil.val(FishingGrandPrixManager.PLAYER_GRANDPRIX_CONFIG_PW_KEY, 0D));
        resultMap.put("fishingGrandPrixQX", RedisUtil.val(FishingGrandPrixManager.PLAYER_GRANDPRIX_CONFIG_QX_KEY, 0L));
        resultMap.put("fishingGrandPrixPY", RedisUtil.val(FishingGrandPrixManager.PLAYER_GRANDPRIX_CONFIG_PY_KEY, 0D));
        resultMap.put("fishingGrandPrixQW", RedisUtil.val(FishingGrandPrixManager.PLAYER_GRANDPRIX_CONFIG_QW_KEY, 0L));
        resultMap.put("fishingGrandPrixTP", RedisUtil.val(FishingGrandPrixManager.PLAYER_GRANDPRIX_CONFIG_TP_KEY, 0D));

        String prod = RedisHelper.get("prod");
        if (prod != null && prod.length() != 0) {
            prod = prod.substring(prod.lastIndexOf("[") + 1).replaceAll("]", "");
            String[] p1 = prod.split(",");
            FishingHitDataManager.FISHING_CUT_PROB[1][4] = Double.parseDouble(p1[0]);
            FishingHitDataManager.FISHING_CUT_PROB[1][5] = Double.parseDouble(p1[1]);
            FishingHitDataManager.FISHING_CUT_PROB[1][6] = Double.parseDouble(p1[2]);
        }

        // 捕鱼挑战赛配置
        fishConfigList.add(FishingHitDataManager.getFishingConfig(1, 4));// fishing[0]
        fishConfigList.add(FishingHitDataManager.getFishingConfig(1, 4));// fishing[1]
        fishConfigList.add(FishingHitDataManager.getFishingConfig(1, 5));// fishing[2]
        fishConfigList.add(FishingHitDataManager.getFishingConfig(1, 6));// fishing[3]
        fishConfigList.add(FishingHitDataManager.getFishingConfig(1, 7));// fishing[4]
        fishConfigList.add(FishingHitDataManager.getFishingConfig(1, 8));// fishing[5]
        resultMap.put("fishing", fishConfigList);
        response.setData(resultMap);

//         存缓存
//        redissonClientMap.putAll(resultMap);

    }

    /**
     * 修改捕鱼游戏设置
     */
    @SuppressWarnings("unchecked")
    @GmHandler(key = "/osee/game/config/fishing/update")
    public void doGameConfigFishingUpdateTask(Map<String, Object> params, CommonResponse response) {

        Double k = (Double) params.get("k");
        RedisHelper.set("k", String.valueOf(k));

        Double sxxs = (Double) params.get("sxxs");
        RedisHelper.set("sxxs", String.valueOf(sxxs));

        Double xs = (Double) params.get("xs");
        RedisHelper.set("xs", String.valueOf(xs));

        Double sxhdz = (Double) params.get("sxhdz");
        RedisHelper.set("sxhdz", String.valueOf(sxhdz));

        Double csc = (Double) params.get("csc");
        RedisHelper.set("csc", String.valueOf(csc));

        Double ckc = (Double) params.get("ckc");
        RedisHelper.set("ckc", String.valueOf(ckc));

        Double zsc = (Double) params.get("zsc");
        RedisHelper.set("zsc", String.valueOf(zsc));

        Double zkc = (Double) params.get("zkc");
        RedisHelper.set("zkc", String.valueOf(zkc));

        Double csc1 = (Double) params.get("csc1");
        RedisHelper.set("csc1", String.valueOf(csc1));

        Double ckc1 = (Double) params.get("ckc1");
        RedisHelper.set("ckc1", String.valueOf(ckc1));

        Double zsc1 = (Double) params.get("zsc1");
        RedisHelper.set("zsc1", String.valueOf(zsc1));

        Double zkc1 = (Double) params.get("zkc1");
        RedisHelper.set("zkc1", String.valueOf(zkc1));

        ArrayList<Double> gProd_obj = (ArrayList<Double>) params.get("gProd");
        FishingUtil.gProd = gProd_obj.toArray(new Double[0]);
        RedisHelper.set("gProd", String.valueOf(gProd_obj));

        ArrayList<Double> lcf_obj = (ArrayList<Double>) params.get("lcf");
        FishingUtil.lcf = lcf_obj.toArray(new Double[0]);
        RedisHelper.set("lcf", String.valueOf(lcf_obj));

        ArrayList<Double> dz_obj = (ArrayList<Double>) params.get("dz");
        FishingUtil.dz = dz_obj.toArray(new Double[0]);
        RedisHelper.set("dz", String.valueOf(dz_obj));

        ArrayList<Double> bfsMin_obj = (ArrayList<Double>) params.get("bfsMin");
        FishingUtil.bfsMin = bfsMin_obj.toArray(new Double[0]);
        RedisHelper.set("bfsMin", String.valueOf(bfsMin_obj));

        ArrayList<Double> bfsMax_obj = (ArrayList<Double>) params.get("bfsMax");
        FishingUtil.bfsMax = bfsMax_obj.toArray(new Double[0]);
        RedisHelper.set("bfsMax", String.valueOf(bfsMax_obj));

        ArrayList<Double> pdxPercentage_obj = (ArrayList<Double>) params.get("pdx");
        FishingUtil.Pdx = pdxPercentage_obj.toArray(new Double[0]);
        RedisHelper.set("pdx", String.valueOf(pdxPercentage_obj));

        ArrayList<Double> jcPercentage_obj = (ArrayList<Double>) params.get("jcPercentage");
        FishingUtil.jcPercentage = jcPercentage_obj.toArray(new Double[0]);
        RedisHelper.set("jcPercentage", String.valueOf(jcPercentage_obj));

        ArrayList<Double> pdxpPercentage_obj = (ArrayList<Double>) params.get("pdxp");
        FishingUtil.Pdxp = pdxpPercentage_obj.toArray(new Double[0]);
        RedisHelper.set("pdxp", String.valueOf(pdxpPercentage_obj));

        ArrayList<Double> xdxPercentage_obj = (ArrayList<Double>) params.get("xdx");
        FishingUtil.Xdx = xdxPercentage_obj.toArray(new Double[0]);
        RedisHelper.set("xdx", String.valueOf(xdxPercentage_obj));

        ArrayList<Double> xdxpPercentage_obj = (ArrayList<Double>) params.get("xdxp");
        FishingUtil.Xdxp = xdxpPercentage_obj.toArray(new Double[0]);
        RedisHelper.set("xdxp", String.valueOf(xdxpPercentage_obj));

        ArrayList<Double> cxPercentage_obj = (ArrayList<Double>) params.get("cxPercentage");
        FishingUtil.cxPercentage = cxPercentage_obj.toArray(new Double[0]);
        RedisHelper.set("cxPercentage", String.valueOf(cxPercentage_obj));

        ArrayList<Double> peakMax1_obj = (ArrayList<Double>) params.get("peakMax1");
        FishingUtil.peakMax1 = peakMax1_obj.toArray(new Double[0]);
        RedisHelper.set("peakMax1", String.valueOf(peakMax1_obj));

        ArrayList<Double> peakMin1_obj = (ArrayList<Double>) params.get("peakMin1");
        FishingUtil.peakMin1 = peakMin1_obj.toArray(new Double[0]);
        RedisHelper.set("peakMin1", String.valueOf(peakMin1_obj));

        ArrayList<Double> peakMaxNum1_obj = (ArrayList<Double>) params.get("peakMaxNum1");
        FishingUtil.peakMaxNum1 = peakMaxNum1_obj.toArray(new Double[0]);
        RedisHelper.set("peakMaxNum1", String.valueOf(peakMaxNum1_obj));

        ArrayList<Double> peakMinNum1_obj = (ArrayList<Double>) params.get("peakMinNum1");
        FishingUtil.peakMinNum1 = peakMinNum1_obj.toArray(new Double[0]);
        RedisHelper.set("peakMinNum1", String.valueOf(peakMinNum1_obj));

        ArrayList<Double> peakMax2_obj = (ArrayList<Double>) params.get("peakMax2");
        FishingUtil.peakMax2 = peakMax2_obj.toArray(new Double[0]);
        RedisHelper.set("peakMax2", String.valueOf(peakMax2_obj));

        ArrayList<Double> peakMin2_obj = (ArrayList<Double>) params.get("peakMin2");
        FishingUtil.peakMin2 = peakMin2_obj.toArray(new Double[0]);
        RedisHelper.set("peakMin2", String.valueOf(peakMin2_obj));

        ArrayList<Double> FPMin_obj = (ArrayList<Double>) params.get("FPMin");
        FishingUtil.FPMin = FPMin_obj.toArray(new Double[0]);
        RedisHelper.set("FPMin", String.valueOf(FPMin_obj));

        ArrayList<Double> PXMin_obj = (ArrayList<Double>) params.get("PXMin");
        FishingUtil.PXMin = PXMin_obj.toArray(new Double[0]);
        RedisHelper.set("PXMin", String.valueOf(PXMin_obj));

        ArrayList<Double> FPMax_obj = (ArrayList<Double>) params.get("FPMax");
        FishingUtil.FPMax = FPMax_obj.toArray(new Double[0]);
        RedisHelper.set("FPMax", String.valueOf(FPMax_obj));

        ArrayList<Double> PXMax_obj = (ArrayList<Double>) params.get("PXMax");
        FishingUtil.PXMax = PXMax_obj.toArray(new Double[0]);
        RedisHelper.set("PXMax", String.valueOf(PXMax_obj));

        ArrayList<Double> peakMaxNum2_obj = (ArrayList<Double>) params.get("peakMaxNum2");
        FishingUtil.peakMaxNum2 = peakMaxNum2_obj.toArray(new Double[0]);
        RedisHelper.set("peakMaxNum2", String.valueOf(peakMaxNum2_obj));

        ArrayList<Double> peakMinNum2_obj = (ArrayList<Double>) params.get("peakMinNum2");
        FishingUtil.peakMinNum2 = peakMinNum2_obj.toArray(new Double[0]);
        RedisHelper.set("peakMinNum2", String.valueOf(peakMinNum2_obj));

        ArrayList<Double> peakMax3_obj = (ArrayList<Double>) params.get("peakMax3");
        FishingUtil.peakMax3 = peakMax3_obj.toArray(new Double[0]);
        RedisHelper.set("peakMax3", String.valueOf(peakMax3_obj));

        ArrayList<Double> peakMin3_obj = (ArrayList<Double>) params.get("peakMin3");
        FishingUtil.peakMin3 = peakMin3_obj.toArray(new Double[0]);
        RedisHelper.set("peakMin3", String.valueOf(peakMin3_obj));

        ArrayList<Double> peakMaxNum3_obj = (ArrayList<Double>) params.get("peakMaxNum3");
        FishingUtil.peakMaxNum3 = peakMaxNum3_obj.toArray(new Double[0]);
        RedisHelper.set("peakMaxNum3", String.valueOf(peakMaxNum3_obj));

        ArrayList<Double> peakMinNum3_obj = (ArrayList<Double>) params.get("peakMinNum3");
        FishingUtil.peakMinNum3 = peakMinNum3_obj.toArray(new Double[0]);
        RedisHelper.set("peakMinNum3", String.valueOf(peakMinNum3_obj));

        ArrayList<Double> torpedoDropFreeRate_obj = (ArrayList<Double>) params.get("torpedoDropFreeRate");
        FishingUtil.torpedoDropFreeRate = torpedoDropFreeRate_obj.toArray(new Double[0]);
        RedisHelper.set("torpedoDropFreeRate", String.valueOf(torpedoDropFreeRate_obj));

        ArrayList<Double> torpedoDropRateGoldFreeRate_obj =
                (ArrayList<Double>) params.get("torpedoDropRateGoldFreeRate");
        FishingUtil.torpedoDropRateGoldFreeRate = torpedoDropRateGoldFreeRate_obj.toArray(new Double[0]);
        RedisHelper.set("torpedoDropRateGoldFreeRate", String.valueOf(torpedoDropRateGoldFreeRate_obj));

        ArrayList<Double> bangTorpedoDropFreeRate_obj = (ArrayList<Double>) params.get("bangTorpedoDropFreeRate");
        FishingUtil.bangTorpedoDropFreeRate = bangTorpedoDropFreeRate_obj.toArray(new Double[0]);
        RedisHelper.set("bangTorpedoDropFreeRate", String.valueOf(bangTorpedoDropFreeRate_obj));

        ArrayList<Double> bangTorpedoDropRateGoldFreeRate_obj =
                (ArrayList<Double>) params.get("bangTorpedoDropRateGoldFreeRate");
        FishingUtil.bangTorpedoDropRateGoldFreeRate = bangTorpedoDropRateGoldFreeRate_obj.toArray(new Double[0]);
        RedisHelper.set("bangTorpedoDropRateGoldFreeRate", String.valueOf(bangTorpedoDropRateGoldFreeRate_obj));

        ArrayList<Double> bossBugleRate_obj = (ArrayList<Double>) params.get("bossBugleRate");
        FishingUtil.bossBugleRate = bossBugleRate_obj.toArray(new Double[0]);
        RedisHelper.set("bossBugleRate", String.valueOf(bossBugleRate_obj));

        ArrayList<Double> torpedoDropPerPayMoney_obj = (ArrayList<Double>) params.get("torpedoDropPerPayMoney");
        FishingUtil.torpedoDropPerPayMoney = torpedoDropPerPayMoney_obj.toArray(new Double[0]);
        RedisHelper.set("torpedoDropPerPayMoney", String.valueOf(torpedoDropPerPayMoney_obj));

        ArrayList<Double> torpedoDropPerPayRate_obj = (ArrayList<Double>) params.get("torpedoDropPerPayRate");
        FishingUtil.torpedoDropPerPayRate = torpedoDropPerPayRate_obj.toArray(new Double[0]);
        RedisHelper.set("torpedoDropPerPayRate", String.valueOf(torpedoDropPerPayRate_obj));

        ArrayList<Double> torpedoDropPerExChangeRate_obj = (ArrayList<Double>) params.get("torpedoDropPerExChangeRate");
        FishingUtil.torpedoDropPerExChangeRate = torpedoDropPerExChangeRate_obj.toArray(new Double[0]);
        RedisHelper.set("torpedoDropPerExChangeRate", String.valueOf(torpedoDropPerExChangeRate_obj));

        ArrayList<Double> torpedoDropPerExChangeRateMin_obj =
                (ArrayList<Double>) params.get("torpedoDropPerExChangeRateMin");
        FishingUtil.torpedoDropPerExChangeRateMin = torpedoDropPerExChangeRateMin_obj.toArray(new Double[0]);
        RedisHelper.set("torpedoDropPerExChangeRateMin", String.valueOf(torpedoDropPerExChangeRateMin_obj));

        ArrayList<Double> torpedoNotBangDropPerExChangeRateMin_obj =
                (ArrayList<Double>) params.get("torpedoNotBangDropPerExChangeRateMin");
        FishingUtil.torpedoNotBangDropPerExChangeRateMin =
                torpedoNotBangDropPerExChangeRateMin_obj.toArray(new Double[0]);
        RedisHelper.set("torpedoNotBangDropPerExChangeRateMin",
                String.valueOf(torpedoNotBangDropPerExChangeRateMin_obj));

        ArrayList<Double> rareTorpedoDropPerExChangeRate_obj =
                (ArrayList<Double>) params.get("rareTorpedoDropPerExChangeRate");
        FishingUtil.rareTorpedoDropPerExChangeRate = rareTorpedoDropPerExChangeRate_obj.toArray(new Double[0]);
        RedisHelper.set("rareTorpedoDropPerExChangeRate", String.valueOf(rareTorpedoDropPerExChangeRate_obj));

        ArrayList<Double> rareTorpedoDropPerExChangeRateMin_obj =
                (ArrayList<Double>) params.get("rareTorpedoDropPerExChangeRateMin");
        FishingUtil.rareTorpedoDropPerExChangeRateMin = rareTorpedoDropPerExChangeRateMin_obj.toArray(new Double[0]);
        RedisHelper.set("rareTorpedoDropPerExChangeRateMin", String.valueOf(rareTorpedoDropPerExChangeRateMin_obj));

        ArrayList<Double> rareTorpedoNotBangDropPerExChangeRate_obj =
                (ArrayList<Double>) params.get("rareTorpedoNotBangDropPerExChangeRate");
        FishingUtil.rareTorpedoNotBangDropPerExChangeRate =
                rareTorpedoNotBangDropPerExChangeRate_obj.toArray(new Double[0]);
        RedisHelper.set("rareTorpedoNotBangDropPerExChangeRate",
                String.valueOf(rareTorpedoNotBangDropPerExChangeRate_obj));

        ArrayList<Double> rareTorpedoNotBangDropPerExChangeRateMin_obj =
                (ArrayList<Double>) params.get("rareTorpedoNotBangDropPerExChangeRateMin");
        FishingUtil.rareTorpedoNotBangDropPerExChangeRateMin =
                rareTorpedoNotBangDropPerExChangeRateMin_obj.toArray(new Double[0]);
        RedisHelper.set("rareTorpedoNotBangDropPerExChangeRateMin",
                String.valueOf(rareTorpedoNotBangDropPerExChangeRateMin_obj));

        ArrayList<Double> torpedoNotBangDropPerExChangeRate_obj =
                (ArrayList<Double>) params.get("torpedoNotBangDropPerExChangeRate");
        FishingUtil.torpedoNotBangDropPerExChangeRate = torpedoNotBangDropPerExChangeRate_obj.toArray(new Double[0]);
        RedisHelper.set("torpedoNotBangDropPerExChangeRate", String.valueOf(torpedoNotBangDropPerExChangeRate_obj));

        ArrayList<Double> obj1 = (ArrayList<Double>) params.get("recOne1");
        List<Long> list1 = new ArrayList<>();
        for (Double aDouble : obj1) {
            // // System.out.println(aDouble);
            list1.add(aDouble.longValue());
        }
        FishingUtil.recOne1 = list1.toArray(new Long[0]);
        RedisHelper.set("recOne1", String.valueOf(obj1));

        ArrayList<Double> burstOne_obj1 = (ArrayList<Double>) params.get("burstOne1");
        List<Long> listBurstOne1 = new ArrayList<>();
        for (Double aDouble : burstOne_obj1) {
            // // System.out.println(aDouble);
            listBurstOne1.add(aDouble.longValue());
        }
        FishingUtil.burstOne1 = listBurstOne1.toArray(new Long[0]);
        RedisHelper.set("burstOne1", String.valueOf(burstOne_obj1));

        ArrayList<Double> recTwo_obj1 = (ArrayList<Double>) params.get("recTwo1");
        List<Long> listRecTwo1 = new ArrayList<>();
        for (Double aDouble : recTwo_obj1) {
            // // System.out.println(aDouble);
            listRecTwo1.add(aDouble.longValue());
        }
        FishingUtil.recTwo1 = listRecTwo1.toArray(new Long[0]);
        RedisHelper.set("recTwo1", String.valueOf(recTwo_obj1));

        ArrayList<Double> burstTwo_obj1 = (ArrayList<Double>) params.get("burstTwo1");
        List<Long> listBurstTwo1 = new ArrayList<>();
        for (Double aDouble : burstTwo_obj1) {
            // // System.out.println(aDouble);
            listBurstTwo1.add(aDouble.longValue());
        }
        FishingUtil.burstTwo1 = listBurstTwo1.toArray(new Long[0]);
        RedisHelper.set("burstTwo1", String.valueOf(burstTwo_obj1));

        ArrayList<Double> balanceOne_obj1 = (ArrayList<Double>) params.get("balanceOne1");
        List<Long> balanceOne1 = new ArrayList<>();
        for (Double aDouble : balanceOne_obj1) {
            // // System.out.println(aDouble);
            balanceOne1.add(aDouble.longValue());
        }
        FishingUtil.balanceOne1 = balanceOne1.toArray(new Long[0]);
        RedisHelper.set("balanceOne1", String.valueOf(balanceOne_obj1));

        ArrayList<Double> balanceTwo_obj1 = (ArrayList<Double>) params.get("balanceTwo1");
        List<Long> balanceTwo1 = new ArrayList<>();
        for (Double aDouble : balanceTwo_obj1) {
            // // System.out.println(aDouble);
            balanceTwo1.add(aDouble.longValue());
        }
        FishingUtil.balanceTwo1 = balanceTwo1.toArray(new Long[0]);
        RedisHelper.set("balanceTwo1", String.valueOf(balanceTwo_obj1));

        ArrayList<Double> obj2 = (ArrayList<Double>) params.get("recOne2");
        List<Long> list2 = new ArrayList<>();
        for (Double aDouble : obj2) {
            // // System.out.println(aDouble);
            list2.add(aDouble.longValue());
        }
        FishingUtil.recOne2 = list2.toArray(new Long[0]);
        RedisHelper.set("recOne2", String.valueOf(obj2));

        ArrayList<Double> burstOne_obj2 = (ArrayList<Double>) params.get("burstOne2");
        List<Long> listBurstOne2 = new ArrayList<>();
        for (Double aDouble : burstOne_obj2) {
            // // System.out.println(aDouble);
            listBurstOne2.add(aDouble.longValue());
        }
        FishingUtil.burstOne2 = listBurstOne2.toArray(new Long[0]);
        RedisHelper.set("burstOne2", String.valueOf(burstOne_obj2));

        ArrayList<Double> recTwo_obj2 = (ArrayList<Double>) params.get("recTwo2");
        List<Long> listRecTwo2 = new ArrayList<>();
        for (Double aDouble : recTwo_obj2) {
            // // System.out.println(aDouble);
            listRecTwo2.add(aDouble.longValue());
        }
        FishingUtil.recTwo2 = listRecTwo2.toArray(new Long[0]);
        RedisHelper.set("recTwo2", String.valueOf(recTwo_obj2));

        ArrayList<Double> burstTwo_obj2 = (ArrayList<Double>) params.get("burstTwo2");
        List<Long> listBurstTwo2 = new ArrayList<>();
        for (Double aDouble : burstTwo_obj2) {
            // // System.out.println(aDouble);
            listBurstTwo2.add(aDouble.longValue());
        }
        FishingUtil.burstTwo2 = listBurstTwo2.toArray(new Long[0]);
        RedisHelper.set("burstTwo2", String.valueOf(burstTwo_obj2));

        ArrayList<Double> balanceOne_obj2 = (ArrayList<Double>) params.get("balanceOne2");
        List<Long> balanceOne2 = new ArrayList<>();
        for (Double aDouble : balanceOne_obj2) {
            // // System.out.println(aDouble);
            balanceOne2.add(aDouble.longValue());
        }
        FishingUtil.balanceOne2 = balanceOne1.toArray(new Long[0]);
        RedisHelper.set("balanceOne2", String.valueOf(balanceOne_obj2));

        ArrayList<Double> balanceTwo_obj2 = (ArrayList<Double>) params.get("balanceTwo2");
        List<Long> balanceTwo2 = new ArrayList<>();
        for (Double aDouble : balanceTwo_obj2) {
            // // System.out.println(aDouble);
            balanceTwo2.add(aDouble.longValue());
        }
        FishingUtil.balanceTwo2 = balanceTwo2.toArray(new Long[0]);
        RedisHelper.set("balanceTwo2", String.valueOf(balanceTwo_obj2));

        ArrayList<Double> obj3 = (ArrayList<Double>) params.get("recOne3");
        List<Long> list3 = new ArrayList<>();
        for (Double aDouble : obj3) {
            // // System.out.println(aDouble);
            list3.add(aDouble.longValue());
        }
        FishingUtil.recOne3 = list3.toArray(new Long[0]);
        RedisHelper.set("recOne3", String.valueOf(obj3));

        ArrayList<Double> burstOne_obj3 = (ArrayList<Double>) params.get("burstOne3");
        List<Long> listBurstOne3 = new ArrayList<>();
        for (Double aDouble : burstOne_obj3) {
            // // System.out.println(aDouble);
            listBurstOne3.add(aDouble.longValue());
        }
        FishingUtil.burstOne3 = listBurstOne3.toArray(new Long[0]);
        RedisHelper.set("burstOne3", String.valueOf(burstOne_obj3));

        ArrayList<Double> recTwo_obj3 = (ArrayList<Double>) params.get("recTwo3");
        List<Long> listRecTwo3 = new ArrayList<>();
        for (Double aDouble : recTwo_obj3) {
            // // System.out.println(aDouble);
            listRecTwo3.add(aDouble.longValue());
        }
        FishingUtil.recTwo3 = listRecTwo3.toArray(new Long[0]);
        RedisHelper.set("recTwo3", String.valueOf(recTwo_obj3));

        ArrayList<Double> burstTwo_obj3 = (ArrayList<Double>) params.get("burstTwo3");
        List<Long> listBurstTwo3 = new ArrayList<>();
        for (Double aDouble : burstTwo_obj3) {
            // // System.out.println(aDouble);
            listBurstTwo3.add(aDouble.longValue());
        }
        FishingUtil.burstTwo3 = listBurstTwo3.toArray(new Long[0]);
        RedisHelper.set("burstTwo3", String.valueOf(burstTwo_obj3));

        ArrayList<Double> balanceOne_obj3 = (ArrayList<Double>) params.get("balanceOne3");
        List<Long> balanceOne3 = new ArrayList<>();
        for (Double aDouble : balanceOne_obj3) {
            // // System.out.println(aDouble);
            balanceOne3.add(aDouble.longValue());
        }
        FishingUtil.balanceOne3 = balanceOne3.toArray(new Long[0]);
        RedisHelper.set("balanceOne3", String.valueOf(balanceOne_obj3));

        ArrayList<Double> balanceTwo_obj3 = (ArrayList<Double>) params.get("balanceTwo3");
        List<Long> balanceTwo3 = new ArrayList<>();
        for (Double aDouble : balanceTwo_obj3) {
            // // System.out.println(aDouble);
            balanceTwo3.add(aDouble.longValue());
        }
        FishingUtil.balanceTwo3 = balanceTwo3.toArray(new Long[0]);
        RedisHelper.set("balanceTwo3", String.valueOf(balanceTwo_obj3));

        ArrayList<Double> obj4 = (ArrayList<Double>) params.get("recOne4");
        List<Long> list4 = new ArrayList<>();
        for (Double aDouble : obj4) {
            // // System.out.println(aDouble);
            list4.add(aDouble.longValue());
        }
        FishingUtil.recOne4 = list4.toArray(new Long[0]);
        RedisHelper.set("recOne4", String.valueOf(obj4));

        ArrayList<Double> burstOne_obj4 = (ArrayList<Double>) params.get("burstOne4");
        List<Long> listBurstOne4 = new ArrayList<>();
        for (Double aDouble : burstOne_obj4) {
            // // System.out.println(aDouble);
            listBurstOne4.add(aDouble.longValue());
        }
        FishingUtil.burstOne4 = listBurstOne4.toArray(new Long[0]);
        RedisHelper.set("burstOne4", String.valueOf(burstOne_obj4));

        ArrayList<Double> recTwo_obj4 = (ArrayList<Double>) params.get("recTwo4");
        List<Long> listRecTwo4 = new ArrayList<>();
        for (Double aDouble : recTwo_obj4) {
            // // System.out.println(aDouble);
            listRecTwo4.add(aDouble.longValue());
        }
        FishingUtil.recTwo4 = listRecTwo4.toArray(new Long[0]);
        RedisHelper.set("recTwo4", String.valueOf(recTwo_obj4));

        ArrayList<Double> burstTwo_obj4 = (ArrayList<Double>) params.get("burstTwo4");
        List<Long> listBurstTwo4 = new ArrayList<>();
        for (Double aDouble : burstTwo_obj4) {
            // // System.out.println(aDouble);
            listBurstTwo4.add(aDouble.longValue());
        }
        FishingUtil.burstTwo4 = listBurstTwo4.toArray(new Long[0]);
        RedisHelper.set("burstTwo4", String.valueOf(burstTwo_obj4));

        ArrayList<Double> balanceOne_obj4 = (ArrayList<Double>) params.get("balanceOne4");
        List<Long> balanceOne4 = new ArrayList<>();
        for (Double aDouble : balanceOne_obj4) {
            // // System.out.println(aDouble);
            balanceOne4.add(aDouble.longValue());
        }
        FishingUtil.balanceOne4 = balanceOne4.toArray(new Long[0]);
        RedisHelper.set("balanceOne4", String.valueOf(balanceOne_obj4));

        ArrayList<Double> balanceTwo_obj4 = (ArrayList<Double>) params.get("balanceTwo4");
        List<Long> balanceTwo4 = new ArrayList<>();
        for (Double aDouble : balanceTwo_obj4) {
            // // System.out.println(aDouble);
            balanceTwo4.add(aDouble.longValue());
        }
        FishingUtil.balanceTwo4 = balanceTwo4.toArray(new Long[0]);
        RedisHelper.set("balanceTwo4", String.valueOf(balanceTwo_obj4));

        List<Map<String, Object>> configList = (List<Map<String, Object>>) params.get("fishing");
        List<Double> listProb = new ArrayList<>();
        // for (int i = 0; i < 8; i++) {
        // Map<String, Object> config = configList.get(i);
        //
        // int index = i % 4;
        // int greener = i / 4;
        //
        // FishingHitDataManager.setFishingConfig(greener, index, config);
        // listProb.add((double)config.get("cutProb"));
        // }
        listProb.add((double) configList.get(1).get("cutProb"));
        listProb.add((double) configList.get(2).get("cutProb"));
        listProb.add((double) configList.get(3).get("cutProb"));
        RedisHelper.set("prod", String.valueOf(listProb));
        // 捕鱼挑战赛配置
        FishingHitDataManager.setFishingConfig(1, 4, configList.get(1));
        FishingHitDataManager.setFishingConfig(1, 5, configList.get(2));
        FishingHitDataManager.setFishingConfig(1, 6, configList.get(3));

        RedisHelper.set("Fishing:Setting:Greener:Limit", Integer.toString(GREENER_LIMIT));
        for (Map.Entry<Long, Map<Integer, Long>> totalWinEntry : TOTAL_WIN_MAP.entrySet()) {
            String key = String.format("Fishing:TotalWin:%d", totalWinEntry.getKey());
            RedisHelper.set(key, new Gson().toJson(totalWinEntry.getValue()));
        }
        for (Map.Entry<Long, Map<Integer, Long>> blackRoomEntry : BLACK_ROOM_MAP.entrySet()) {
            String key = String.format("Fishing:BlackRoom:%d", blackRoomEntry.getKey());
            RedisHelper.set(key, new Gson().toJson(blackRoomEntry.getValue()));
        }
        for (Map.Entry<Long, Double> playerFishingProb : PLAYER_FISHING_PROB_MAP.entrySet()) {
            String key = String.format("Fishing:PlayerFishingProb:%d", playerFishingProb.getKey());
            RedisHelper.set(key, Double.toString(playerFishingProb.getValue()));
        }
        // 捕鱼挑战赛参数
        for (Map.Entry<Long, Map<Integer, Long>> totalWinEntry : CHALLENGE_TOTAL_WIN_MAP.entrySet()) {
            String key = String.format("Fishing:TotalWinChallenge:%d", totalWinEntry.getKey());
            RedisHelper.set(key, new Gson().toJson(totalWinEntry.getValue()));
        }
        for (Map.Entry<Long, Map<Integer, Long>> blackRoomEntry : CHALLENGE_BLACK_ROOM_MAP.entrySet()) {
            String key = String.format("Fishing:BlackRoomChallenge:%d", blackRoomEntry.getKey());
            RedisHelper.set(key, new Gson().toJson(blackRoomEntry.getValue()));
        }
        // for (Entry<Long, Double> playerFishingProb : CHALLENGE_PLAYER_FISHING_PROB_MAP.entrySet()) {
        // String key = String.format("Fishing:PlayerFishingProbChallenge:%d", playerFishingProb.getKey());
        // RedisHelper.set(key, Double.toString(playerFishingProb.getValue()));
        // }

        RedisHelper.set("Fishing:Setting:Pool:InitPool", GsonManager.gson.toJson(FISHING_INIT_POOL));
        RedisHelper.set("Fishing:Setting:Pool:Pool", GsonManager.gson.toJson(FISHING_POOL));
        RedisHelper.set("Fishing:Setting:Pool:Prob", GsonManager.gson.toJson(FISHING_PROB));
        RedisHelper.set("Fishing:Setting:Pool:UnitProb", GsonManager.gson.toJson(FISHING_UNIT_PROB));
        RedisHelper.set("Fishing:Setting:Pool:PerUnitMoney", GsonManager.gson.toJson(FISHING_PER_UNIT_MONEY));
        RedisHelper.set("Fishing:Setting:Daily:Prob", GsonManager.gson.toJson(PLAYER_DAILY_PROB));
        RedisHelper.set("Fishing:Setting:Daily:Limit", GsonManager.gson.toJson(PLAYER_DAILY_LIMIT));
        RedisHelper.set("Fishing:Setting:Total:Prob", GsonManager.gson.toJson(PLAYER_TOTAL_PROB));
        RedisHelper.set("Fishing:Setting:Total:Limit", GsonManager.gson.toJson(PLAYER_TOTAL_LIMIT));
        RedisHelper.set("Fishing:Setting:BlackRoom:Prob", GsonManager.gson.toJson(BLACK_ROOM_PROB));
        RedisHelper.set("Fishing:Setting:BlackRoom:Limit", GsonManager.gson.toJson(BLACK_ROOM_LIMIT));

        ArrayList<Double> obj = (ArrayList<Double>) params.get("q0");
        List<Long> list = new ArrayList<>();
        for (Double aDouble : obj) {
            // // System.out.println(aDouble);
            list.add(aDouble.longValue());
        }
        Long[] q0 = FishingUtil.q0;
        FishingUtil.q0 = list.toArray(new Long[0]);
        RedisHelper.set("q0", String.valueOf(obj));

        ArrayList<Double> ap_obj = (ArrayList<Double>) params.get("ap");
        FishingUtil.ap = ap_obj.toArray(new Double[0]);
        RedisHelper.set("ap", String.valueOf(ap_obj));

        ArrayList<Double> apt_obj = (ArrayList<Double>) params.get("apt");
        List<Long> apt = new ArrayList<>();
        for (Double aDouble : apt_obj) {
            apt.add(aDouble.longValue());
        }
        FishingUtil.apt = apt.toArray(new Long[0]);
        RedisHelper.set("apt", String.valueOf(apt_obj));

        ArrayList<Double> recMax_obj = (ArrayList<Double>) params.get("recMax");
        List<Long> recMax = new ArrayList<>();
        for (Double aDouble : recMax_obj) {
            recMax.add(aDouble.longValue());
        }
        FishingUtil.recMax = recMax.toArray(new Long[0]);
        RedisHelper.set("recMax", String.valueOf(recMax_obj));

        ArrayList<Double> recMin_obj = (ArrayList<Double>) params.get("recMin");
        List<Long> recMin = new ArrayList<>();
        for (Double aDouble : recMin_obj) {
            recMin.add(aDouble.longValue());
        }
        FishingUtil.recMin = recMin.toArray(new Long[0]);
        RedisHelper.set("recMin", String.valueOf(recMin_obj));

        ArrayList<Double> ap1_obj = (ArrayList<Double>) params.get("ap1");
        FishingUtil.ap1 = ap1_obj.toArray(new Double[0]);
        RedisHelper.set("ap1", String.valueOf(ap1_obj));

        ArrayList<Double> apt1_obj = (ArrayList<Double>) params.get("apt1");
        List<Long> apt1 = new ArrayList<>();
        for (Double aDouble : apt1_obj) {
            apt1.add(aDouble.longValue());
        }
        FishingUtil.apt1 = apt1.toArray(new Long[0]);
        RedisHelper.set("apt1", String.valueOf(apt1_obj));

        ArrayList<Double> recMax1_obj = (ArrayList<Double>) params.get("recMax1");
        List<Long> recMax1 = new ArrayList<>();
        for (Double aDouble : recMax1_obj) {
            recMax1.add(aDouble.longValue());
        }
        FishingUtil.recMax1 = recMax1.toArray(new Long[0]);
        RedisHelper.set("recMax1", String.valueOf(recMax1_obj));

        ArrayList<Double> recMin1_obj = (ArrayList<Double>) params.get("recMin1");
        List<Long> recMin1 = new ArrayList<>();
        for (Double aDouble : recMin1_obj) {
            recMin1.add(aDouble.longValue());
        }
        FishingUtil.recMin1 = recMin1.toArray(new Long[0]);
        RedisHelper.set("recMin1", String.valueOf(recMin1_obj));

        ArrayList<Double> ap2_obj = (ArrayList<Double>) params.get("ap2");
        FishingUtil.ap2 = ap2_obj.toArray(new Double[0]);
        RedisHelper.set("ap2", String.valueOf(ap2_obj));

        ArrayList<Double> apt2_obj = (ArrayList<Double>) params.get("apt2");
        List<Long> apt2 = new ArrayList<>();
        for (Double aDouble : apt2_obj) {
            apt2.add(aDouble.longValue());
        }
        FishingUtil.apt2 = apt2.toArray(new Long[0]);
        RedisHelper.set("apt2", String.valueOf(apt2_obj));

        ArrayList<Double> recMax2_obj = (ArrayList<Double>) params.get("recMax2");
        List<Long> recMax2 = new ArrayList<>();
        for (Double aDouble : recMax2_obj) {
            recMax2.add(aDouble.longValue());
        }
        FishingUtil.recMax2 = recMax2.toArray(new Long[0]);
        RedisHelper.set("recMax2", String.valueOf(recMax2_obj));

        ArrayList<Double> recMin2_obj = (ArrayList<Double>) params.get("recMin2");
        List<Long> recMin2 = new ArrayList<>();
        for (Double aDouble : recMin2_obj) {
            recMin2.add(aDouble.longValue());
        }
        FishingUtil.recMin2 = recMin2.toArray(new Long[0]);
        RedisHelper.set("recMin2", String.valueOf(recMin2_obj));

        ArrayList<Double> ap3_obj = (ArrayList<Double>) params.get("ap3");
        FishingUtil.ap3 = ap3_obj.toArray(new Double[0]);
        RedisHelper.set("ap3", String.valueOf(ap3_obj));

        ArrayList<Double> apt3_obj = (ArrayList<Double>) params.get("apt3");
        List<Long> apt3 = new ArrayList<>();
        for (Double aDouble : apt3_obj) {
            apt3.add(aDouble.longValue());
        }
        FishingUtil.apt3 = apt3.toArray(new Long[0]);
        RedisHelper.set("apt3", String.valueOf(apt3_obj));

        ArrayList<Double> recMax3_obj = (ArrayList<Double>) params.get("recMax3");
        List<Long> recMax3 = new ArrayList<>();
        for (Double aDouble : recMax3_obj) {
            recMax3.add(aDouble.longValue());
        }
        FishingUtil.recMax3 = recMax3.toArray(new Long[0]);
        RedisHelper.set("recMax3", String.valueOf(recMax3_obj));

        ArrayList<Double> recMin3_obj = (ArrayList<Double>) params.get("recMin3");
        List<Long> recMin3 = new ArrayList<>();
        for (Double aDouble : recMin3_obj) {
            recMin3.add(aDouble.longValue());
        }
        FishingUtil.recMin3 = recMin3.toArray(new Long[0]);
        RedisHelper.set("recMin3", String.valueOf(recMin3_obj));

        ArrayList<Double> pumb_obj = (ArrayList<Double>) params.get("pump");
        List<Long> pump = new ArrayList<>();
        for (Double aDouble : pumb_obj) {
            pump.add(aDouble.longValue());
        }
        FishingUtil.pump = pump.toArray(new Long[0]);
        RedisHelper.set("pump", String.valueOf(pumb_obj));

        ArrayList<Double> pumpNum_obj = (ArrayList<Double>) params.get("pumpNum");
        List<Long> pumpNum = new ArrayList<>();
        for (Double aDouble : pumpNum_obj) {
            pumpNum.add(aDouble.longValue());
        }
        FishingUtil.pumpNum = pumpNum.toArray(new Long[0]);
        RedisHelper.set("pumpNum", String.valueOf(pumpNum_obj));

        ArrayList<Double> tp_c = (ArrayList<Double>) params.get("tp_c");
        RedisHelper.set("tp_c", JSON.toJSONString(tp_c));
        for (int i = 0; i < tp_c.size(); i++) {
            FishingUtil.tp_c[i] = tp_c.get(i);
        }

        ArrayList<Double> tp_z = (ArrayList<Double>) params.get("tp_z");
        RedisHelper.set("tp_z", JSON.toJSONString(tp_z));
        for (int i = 0; i < tp_z.size(); i++) {
            FishingUtil.tp_z[i] = tp_z.get(i);
        }

        ArrayList<Double> tp_g = (ArrayList<Double>) params.get("tp_g");
        RedisHelper.set("tp_g", JSON.toJSONString(tp_g));
        for (int i = 0; i < tp_g.size(); i++) {
            FishingUtil.tp_g[i] = tp_g.get(i);
        }
        ArrayList<Double> tp_s = (ArrayList<Double>) params.get("tp_s");
        RedisHelper.set("tp_s", JSON.toJSONString(tp_s));
        for (int i = 0; i < tp_s.size(); i++) {
            FishingUtil.tp_s[i] = tp_s.get(i);
        }

        ArrayList<Double> burstOne_obj = (ArrayList<Double>) params.get("burstOne");
        List<Long> burstOne = new ArrayList<>();
        for (Double aDouble : burstOne_obj) {
            burstOne.add(aDouble.longValue());
        }
        FishingUtil.burstOne = burstOne.toArray(new Long[0]);
        RedisHelper.set("burstOne", String.valueOf(burstOne_obj));

        ArrayList<Double> recOne_obj = (ArrayList<Double>) params.get("recOne");
        List<Long> recOne = new ArrayList<>();
        for (Double aDouble : recOne_obj) {
            recOne.add(aDouble.longValue());
        }
        FishingUtil.recOne = recOne.toArray(new Long[0]);
        RedisHelper.set("recOne", String.valueOf(recOne_obj));

        ArrayList<Double> blanceOne_obj = (ArrayList<Double>) params.get("balanceOne");
        List<Long> balanceOne = new ArrayList<>();
        for (Double aDouble : blanceOne_obj) {
            balanceOne.add(aDouble.longValue());
        }
        FishingUtil.balanceOne = balanceOne.toArray(new Long[0]);
        RedisHelper.set("balanceOne", String.valueOf(blanceOne_obj));

        ArrayList<Double> balanceBuOne_obj = (ArrayList<Double>) params.get("balanceBuOne");
        List<Long> balanceBuOne = new ArrayList<>();
        for (Double aDouble : balanceBuOne_obj) {
            balanceBuOne.add(aDouble.longValue());
        }
        FishingUtil.balanceBuOne = balanceBuOne.toArray(new Long[0]);
        RedisHelper.set("balanceBuOne", String.valueOf(balanceBuOne_obj));

        ArrayList<Double> gcOne_obj = (ArrayList<Double>) params.get("gcOne");
        List<Long> gcOne = new ArrayList<>();
        for (Double aDouble : gcOne_obj) {
            gcOne.add(aDouble.longValue());
        }
        FishingUtil.gcOne = gcOne.toArray(new Long[0]);
        RedisHelper.set("gcOne", String.valueOf(gcOne_obj));

        ArrayList<Double> gcBuOne_obj = (ArrayList<Double>) params.get("gcBurstOne");
        List<Long> gcBurstOne = new ArrayList<>();
        for (Double aDouble : gcBuOne_obj) {
            gcBurstOne.add(aDouble.longValue());
        }
        FishingUtil.gcBurstOne = gcBurstOne.toArray(new Long[0]);
        RedisHelper.set("gcBurstOne", String.valueOf(gcBuOne_obj));

        ArrayList<Double> burstTwo_obj = (ArrayList<Double>) params.get("burstTwo");
        List<Long> burstTwo = new ArrayList<>();
        for (Double aDouble : burstTwo_obj) {
            burstTwo.add(aDouble.longValue());
        }
        FishingUtil.burstTwo = burstTwo.toArray(new Long[0]);
        RedisHelper.set("burstTwo", String.valueOf(burstTwo_obj));

        ArrayList<Double> recTwo_obj = (ArrayList<Double>) params.get("recTwo");
        List<Long> recTwo = new ArrayList<>();
        for (Double aDouble : recTwo_obj) {
            recTwo.add(aDouble.longValue());
        }
        FishingUtil.recTwo = recTwo.toArray(new Long[0]);
        RedisHelper.set("recTwo", String.valueOf(recTwo_obj));

        ArrayList<Double> blanceTwo_obj = (ArrayList<Double>) params.get("balanceTwo");
        List<Long> balanceTwo = new ArrayList<>();
        for (Double aDouble : blanceTwo_obj) {
            balanceTwo.add(aDouble.longValue());
        }
        FishingUtil.balanceTwo = balanceTwo.toArray(new Long[0]);
        RedisHelper.set("balanceTwo", String.valueOf(blanceTwo_obj));

        ArrayList<Double> balanceBuTwo_obj = (ArrayList<Double>) params.get("balanceBuTwo");
        List<Long> balanceBuTwo = new ArrayList<>();
        for (Double aDouble : balanceBuTwo_obj) {
            balanceBuTwo.add(aDouble.longValue());
        }
        FishingUtil.balanceBuTwo = balanceBuTwo.toArray(new Long[0]);
        RedisHelper.set("balanceBuTwo", String.valueOf(balanceBuTwo_obj));

        ArrayList<Double> gcTwo_obj = (ArrayList<Double>) params.get("gcTwo");
        List<Long> gcTwo = new ArrayList<>();
        for (Double aDouble : gcTwo_obj) {
            gcTwo.add(aDouble.longValue());
        }
        FishingUtil.gcTwo = gcTwo.toArray(new Long[0]);
        RedisHelper.set("gcTwo", String.valueOf(gcTwo_obj));

        ArrayList<Double> gcBuTwo_obj = (ArrayList<Double>) params.get("gcBurstTwo");
        List<Long> gcBurstTwo = new ArrayList<>();
        for (Double aDouble : gcBuTwo_obj) {
            gcBurstTwo.add(aDouble.longValue());
        }
        FishingUtil.gcBurstTwo = gcBurstTwo.toArray(new Long[0]);
        RedisHelper.set("gcBurstTwo", String.valueOf(gcBuTwo_obj));

        ArrayList<Double> burstThree_obj = (ArrayList<Double>) params.get("burstThree");
        List<Long> burstThree = new ArrayList<>();
        for (Double aDouble : burstThree_obj) {
            burstThree.add(aDouble.longValue());
        }
        FishingUtil.burstThree = burstThree.toArray(new Long[0]);
        RedisHelper.set("burstThree", String.valueOf(burstThree_obj));

        ArrayList<Double> recThree_obj = (ArrayList<Double>) params.get("recThree");
        List<Long> recThree = new ArrayList<>();
        for (Double aDouble : recThree_obj) {
            recThree.add(aDouble.longValue());
        }
        FishingUtil.recThree = recThree.toArray(new Long[0]);
        RedisHelper.set("recThree", String.valueOf(recThree_obj));

        ArrayList<Double> blanceThree_obj = (ArrayList<Double>) params.get("balanceThree");
        List<Long> balanceThree = new ArrayList<>();
        for (Double aDouble : blanceThree_obj) {
            balanceThree.add(aDouble.longValue());
        }
        FishingUtil.balanceThree = balanceThree.toArray(new Long[0]);
        RedisHelper.set("balanceThree", String.valueOf(blanceThree_obj));

        ArrayList<Double> balanceBuThree_obj = (ArrayList<Double>) params.get("balanceBuThree");
        List<Long> balanceBuThree = new ArrayList<>();
        for (Double aDouble : balanceBuThree_obj) {
            balanceBuThree.add(aDouble.longValue());
        }
        FishingUtil.balanceBuThree = balanceBuThree.toArray(new Long[0]);
        RedisHelper.set("balanceBuThree", String.valueOf(balanceBuThree_obj));

        ArrayList<Double> gcThree_obj = (ArrayList<Double>) params.get("gcThree");
        List<Long> gcThree = new ArrayList<>();
        for (Double aDouble : gcThree_obj) {
            gcThree.add(aDouble.longValue());
        }
        FishingUtil.gcThree = gcThree.toArray(new Long[0]);
        RedisHelper.set("gcThree", String.valueOf(gcThree_obj));

        ArrayList<Double> gcBuThree_obj = (ArrayList<Double>) params.get("gcBurstThree");
        List<Long> gcBurstThree = new ArrayList<>();
        for (Double aDouble : gcBuThree_obj) {
            gcBurstThree.add(aDouble.longValue());
        }
        FishingUtil.gcBurstThree = gcBurstThree.toArray(new Long[0]);
        RedisHelper.set("gcBurstThree", String.valueOf(gcBuThree_obj));

        ArrayList<Double> burstFour_obj = (ArrayList<Double>) params.get("burstFour");
        List<Long> burstFour = new ArrayList<>();
        for (Double aDouble : burstFour_obj) {
            burstFour.add(aDouble.longValue());
        }
        FishingUtil.burstFour = burstFour.toArray(new Long[0]);
        RedisHelper.set("burstFour", String.valueOf(burstFour_obj));

        ArrayList<Double> recFour_obj = (ArrayList<Double>) params.get("recFour");
        List<Long> recFour = new ArrayList<>();
        for (Double aDouble : recFour_obj) {
            recFour.add(aDouble.longValue());
        }
        FishingUtil.recFour = recFour.toArray(new Long[0]);
        RedisHelper.set("recFour", String.valueOf(recFour_obj));

        ArrayList<Double> blanceFour_obj = (ArrayList<Double>) params.get("balanceFour");
        List<Long> balanceFour = new ArrayList<>();
        for (Double aDouble : blanceFour_obj) {
            balanceFour.add(aDouble.longValue());
        }
        FishingUtil.balanceFour = balanceFour.toArray(new Long[0]);
        RedisHelper.set("balanceFour", String.valueOf(blanceFour_obj));

        ArrayList<Double> balanceBuFour_obj = (ArrayList<Double>) params.get("balanceBuFour");
        List<Long> balanceBuFour = new ArrayList<>();
        for (Double aDouble : balanceBuFour_obj) {
            balanceBuFour.add(aDouble.longValue());
        }
        FishingUtil.balanceBuFour = balanceBuFour.toArray(new Long[0]);
        RedisHelper.set("balanceBuFour", String.valueOf(balanceBuFour_obj));

        ArrayList<Double> gcFour_obj = (ArrayList<Double>) params.get("gcFour");
        List<Long> gcFour = new ArrayList<>();
        for (Double aDouble : gcFour_obj) {
            gcFour.add(aDouble.longValue());
        }
        FishingUtil.gcFour = gcFour.toArray(new Long[0]);
        RedisHelper.set("gcFour", String.valueOf(gcFour_obj));

        ArrayList<Double> gcBuFour_obj = (ArrayList<Double>) params.get("gcBurstFour");
        List<Long> gcBurstFour = new ArrayList<>();
        for (Double aDouble : gcBuFour_obj) {
            gcBurstFour.add(aDouble.longValue());
        }
        FishingUtil.gcBurstFour = gcBurstFour.toArray(new Long[0]);
        RedisHelper.set("gcBurstFour", String.valueOf(gcBuFour_obj));

        // 清除T
        // RedisHelper.removePattern("USER_FISHTYPE_[1234]_T_CHALLANGE*");
        Stream.of(1, 2, 3, 4).forEach(type -> {
            RedisHelper.redissonClient.getMap("GAME:USER:T;CHALLANGE;" + type, new JsonJacksonCodec()).clear();
        });

    }


    @GmHandler(key = "/osee/server/games")
    public void doServerGamesData(Map<String, Object> params, CommonResponse response) {

        Double page = Double.parseDouble(params.get("page").toString());
        Double limit = Double.parseDouble(params.get("limit").toString());
        Double startTime = null;
        Double endTime = null;
        Double mode = null;
        Double type = null;
        String startDate = null;
        String endDate = null;

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");

        if (params.get("startDate") != null && params.get("endDate") != null) {
            startTime = Double.parseDouble(params.get("startDate").toString());
            endTime = Double.parseDouble(params.get("endDate").toString());
            startDate = sdf.format(startTime);
            endDate = sdf.format(endTime);
        }
        if (params.get("mode") != null) {
            mode = Double.parseDouble(params.get("mode").toString());
        }
        if (params.get("type") != null) {
            type = Double.parseDouble(params.get("type").toString());
        }

        List<AppGameLogEntity> entityList =
                gameLogMapper.find(startDate, endDate, mode == null ? null : mode.intValue(),
                        limit.intValue() * (page.intValue() - 1), limit.intValue(), type == null ? null : type.intValue());

        int count = gameLogMapper.count(startDate, endDate, mode == null ? null : mode.intValue(),
                type == null ? null : type.intValue());

        for (AppGameLogEntity item : entityList) {

            String date = DateUtil.formatDate(item.getCreateTime());

            List<AppRankLogEntity> appRankLogEntityList =
                    rankLogMapper.find(0, Integer.MAX_VALUE, date, item.getType(), item.getMode(), " AND tb1.receive = 1");

            AppRewardLogEntity receivedReward = item.getReceivedReward();

            if (receivedReward == null) {

                receivedReward = new AppRewardLogEntity();
                item.setReceivedReward(receivedReward);

            }

            for (AppRankLogEntity subItem : appRankLogEntityList) {

                AppRewardLogEntity subReward = subItem.getReward();

                receivedReward.setGold(receivedReward.getGold() + subReward.getGold());
                receivedReward.setDiamond(receivedReward.getDiamond() + subReward.getDiamond());
                receivedReward.setLowerBall(receivedReward.getLowerBall() + subReward.getLowerBall());
                receivedReward.setMiddleBall(receivedReward.getMiddleBall() + subReward.getMiddleBall());
                receivedReward.setHighBall(receivedReward.getHighBall() + subReward.getHighBall());
                receivedReward.setSkillLock(receivedReward.getSkillLock() + subReward.getSkillLock());
                receivedReward.setSkillFast(receivedReward.getSkillFast() + subReward.getSkillFast());
                receivedReward.setSkillCrit(receivedReward.getSkillCrit() + subReward.getSkillCrit());
                receivedReward.setSkillFrozen(receivedReward.getSkillFrozen() + subReward.getSkillFrozen());
                receivedReward.setBossBugle(receivedReward.getBossBugle() + subReward.getBossBugle());

            }

        }

        response.setData(entityList);
        Map<String, Object> obj = new HashMap<>();
        obj.put("count", count);
        obj.put("data", entityList);
        response.setData(obj);
        response.setSuccess(true);

    }

    @GmHandler(key = "/osee/server/ranks")
    public void doServerRanksData(Map<String, Object> paramMap, CommonResponse response) {

        double page = Double.parseDouble(paramMap.get("page").toString());
        double limit = Double.parseDouble(paramMap.get("limit").toString());
        Double time = Double.parseDouble(paramMap.get("date").toString());

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        String date = sdf.format(time);
        double type = Double.parseDouble(paramMap.get("type").toString());

        Integer mode = MapUtil.getInt(paramMap, "mode");

        List<AppRankLogEntity> entities =
                rankLogMapper.find((int) limit * ((int) page - 1), (int) limit, date, (int) type, mode, "");

        int count = rankLogMapper.count(date, (int) type, mode);

        Map<String, Object> obj = new HashMap<>();
        obj.put("count", count);
        obj.put("data", entities);
        response.setData(obj);
        response.setSuccess(true);

    }

    public static void main(String[] args) {
        String q0 = "[1.0,2.0,3.0]";
        // // System.out.println(q0.substring(q0.lastIndexOf("[") + 1).replaceAll("]", ""));
        // // System.out.println(q0);
    }
}
