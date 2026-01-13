package com.maple.game.osee.dao.data.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.*;

import com.maple.database.data.entity.UserEntity;
import com.maple.database.data.mapper.UserMapper;
import com.maple.game.osee.dao.data.entity.KillBossEntity;
import com.maple.game.osee.dao.data.entity.OseePlayerEntity;
import com.maple.game.osee.entity.fishing.GoldenPig;
import com.maple.game.osee.entity.fishing.challenge.FishJc;
import com.maple.game.osee.entity.gm.AgentPlayerAll;

/**
 * 玩家实体数据交互接口
 */
@Mapper
public interface OseePlayerMapper {

    String TABLE_NAME = "tbl_osee_player";

    String TABLE_NAME1 = "tbl_app_user";

    String TABLE_NAME2 = "t_osee_jc_record";

    String TABLE_NAME3 = "t_osee_golden_pig_record";

    String TBL_KILL_BOSS = "tbl_kill_boss";

    /**
     * 新增数据
     */
    @Insert("INSERT INTO " + TABLE_NAME + " (`user_id`, `money`, `bank_money`, `bank_password`, `lottery`, "
        + "`diamond`, `vip_level`, `level`, `experience`, `recharge_money`, `lose_control`, `player_type`, "
        + "`bronze_torpedo`, `silver_torpedo`, `gold_torpedo`, "
        + "`skill_lock`, `skill_frozen`, `skill_fast`, `skill_crit`, "
        + "`battery_level`, `monthcard_expire_date`, `ten_challenge_times`, `boss_bugle`, "
        + "`qszs_battery_expire_date`, `blnh_battery_expire_date`, `lhtz_battery_expire_date`, `swhp_battery_expire_date`, "
        + "`lbs_battery_expire_date`, `tjzx_battery_expire_date`, `hjhp_battery_expire_date`, `gjzy_battery_expire_date`, "
        + "`hjzp_battery_expire_date`, `zlhp_battery_expire_date`, "
        + "`dragon_crystal`, `fen_shen`,`gold_torpedo_bang`,`rare_torpedo`,`rare_torpedo_bang`, `skillEle`,"
        + "`yuGu`, `haiYaoShi`,`wangHunShi`,`haiHunShi`,`zhenZhuShi`, `haiShouShi`,"
        + "`haiMoShi`, `zhaoHuanShi`,`dianChiShi`,`heiDongShi`,`lingZhuShi`, `longGu`,"
        + "`longZhu`, `longYuan`,`longJi`,`skillBlackHole`,`skillTorpedo`, `sendCard`,"
        + "`blackBullet`, `bronzeBullet`,`silverBullet`,`goldBullet`,`skillBit`" + ") VALUES ("
        + "#{entity.userId}, #{entity.money}, #{entity.bankMoney}, #{entity.bankPassword}, "
        + "#{entity.lottery}, #{entity.diamond}, #{entity.vipLevel}, #{entity.level}, "
        + "#{entity.experience}, #{entity.rechargeMoney}, #{entity.loseControl}, #{entity.playerType}, "
        + "#{entity.bronzeTorpedo}, #{entity.silverTorpedo}, #{entity.goldTorpedo}, "
        + "#{entity.skillLock}, #{entity.skillFrozen}, #{entity.skillFast}, #{entity.skillCrit},"
        + "#{entity.batteryLevel}, #{entity.monthCardExpireDate}, #{entity.tenChallengeTimes}, #{entity.bossBugle},"
        + "#{entity.qszsBatteryExpireDate}, #{entity.blnhBatteryExpireDate}, #{entity.lhtzBatteryExpireDate}, #{entity.swhpBatteryExpireDate}, "
        + "#{entity.lbsBatteryExpireDate}, #{entity.tjzxBatteryExpireDate}, #{entity.hjhpBatteryExpireDate}, #{entity.gjzyBatteryExpireDate}, "
        + "#{entity.hjzpBatteryExpireDate}, #{entity.zlhpBatteryExpireDate}, "
        + "#{entity.dragonCrystal}, #{entity.fenShen},#{entity.goldTorpedoBang},#{entity.rareTorpedo},#{entity.rareTorpedoBang},#{entity.skillEle},"
        + "#{entity.yuGu}, #{entity.haiYaoShi},#{entity.wangHunShi},#{entity.haiHunShi},#{entity.zhenZhuShi},#{entity.haiShouShi},"
        + "#{entity.haiMoShi}, #{entity.zhaoHuanShi},#{entity.dianChiShi},#{entity.heiDongShi},#{entity.lingZhuShi},#{entity.longGu},"
        + "#{entity.longZhu}, #{entity.longYuan},#{entity.longJi},#{entity.skillBlackHole},#{entity.skillTorpedo},#{entity.sendCard},"
        + "#{entity.blackBullet}, #{entity.bronzeBullet},#{entity.silverBullet},#{entity.goldBullet},#{entity.skillBit}"
        + ")")
    @Options(useGeneratedKeys = true, keyProperty = "entity.id")
    void save(@Param("entity") OseePlayerEntity playerEntity);

    /**
     * 新增数据
     */
    @Insert("INSERT INTO " + TABLE_NAME3 + " (`userId`, `nickName`, `preLottery`, `num`, `changeLottery`, "
        + "`afterLottery`, `createTime`" + ") VALUES ("
        + "#{entity.userId}, #{entity.nickName}, #{entity.preLottery}, #{entity.num}, "
        + "#{entity.changeLottery}, #{entity.afterLottery}, #{entity.createTime}" + ")")
    @Options(useGeneratedKeys = true, keyProperty = "entity.id")
    void saveLottery(@Param("entity") GoldenPig goldenPig);

    /**
     * 新增数据
     */
    @Insert("INSERT INTO " + TABLE_NAME2 + " (`userId`, `nickName`, `roomIndex`, `type`, `vip`, "
        + "`num`, `num1`, `createTime`" + ") VALUES ("
        + "#{entity.userId}, #{entity.nickName}, #{entity.roomIndex}, #{entity.type}, "
        + "#{entity.vip}, #{entity.num}, #{entity.num1}, #{entity.createTime}" + ")")
    @Options(useGeneratedKeys = true, keyProperty = "entity.id")
    void saveJc(@Param("entity") FishJc fishJc);

    /**
     * 查找奖池数据
     */
    @Select("SELECT * FROM " + TABLE_NAME2 + " where roomIndex = ${roomIndex} order by createTime desc limit 0,10")
    List<FishJc> getAllJc(@Param("roomIndex") long roomIndex);

    /**
     * 更新数据
     */
    @Update("UPDATE " + TABLE_NAME
        + " SET `money`=#{entity.money}, `total_dragon_crystal` =#{entity.totalDragonCrystal}, `use_battery`=#{entity.useBattery},"
        + " `lottery`=#{entity.lottery}, `diamond`=#{entity.diamond}, "
        + "`vip_level`=#{entity.vipLevel}, `level`=#{entity.level}, `experience`=#{entity.experience}, "
        + "`recharge_money`=#{entity.rechargeMoney},  "
        + "`player_type`=#{entity.playerType}, "
        + "`gold_torpedo`=#{entity.goldTorpedo},"
        + "`skill_lock`=#{entity.skillLock}, `skill_frozen`=#{entity.skillFrozen}, "
        + "`skill_fast`=#{entity.skillFast}, `skill_double`=#{entity.skillDouble}, `skill_crit`=#{entity.skillCrit}, "
        + "`battery_level`=#{entity.batteryLevel}, "
        + "`monthcard_expire_date`=#{entity.monthCardExpireDate},"
        + "`boss_bugle` = #{entity.bossBugle},"
        + "`qszs_battery_expire_date` = #{entity.qszsBatteryExpireDate}, `blnh_battery_expire_date` = #{entity.blnhBatteryExpireDate}, "
        + "`lhtz_battery_expire_date` = #{entity.lhtzBatteryExpireDate}, `swhp_battery_expire_date` = #{entity.swhpBatteryExpireDate}, "
        + "`dragon_crystal` = #{entity.dragonCrystal}, `fen_shen` = #{entity.fenShen},`magicLamp` = #{entity.magicLamp},`thousandSword` = #{entity.thousandSword},`vipCard` = #{entity.vipCard}"
        + " ,`userIp` = #{entity.userIp} " + " ,`last_join_room_time` = #{entity.lastJoinRoomTime} "
        + " ,`last_sign_in_time` = #{entity.lastSignInTime} " + " WHERE id=#{entity.id}")
    void update(@Param("entity") OseePlayerEntity playerEntity);

    // /**
    //// * 更新数据
    //// */
    //// @Update("UPDATE " + TABLE_NAME + " SET `money`=#{entity.money}, `bank_money`=#{entity.bankMoney}, "
    //// + "`bank_password`=#{entity.bankPassword}, `lottery`=#{entity.lottery}, `diamond`=#{entity.diamond}, "
    //// + "`vip_level`=#{entity.vipLevel}, `level`=#{entity.level}, `experience`=#{entity.experience}, "
    //// + "`recharge_money`=#{entity.rechargeMoney}, `lose_control`=#{entity.loseControl}, "
    //// + "`player_type`=#{entity.playerType}, "
    //// + "`bronze_torpedo`=#{entity.bronzeTorpedo}, `silver_torpedo`=#{entity.silverTorpedo},
    // `gold_torpedo`=#{entity.goldTorpedo},"
    //// + "`skill_lock`=#{entity.skillLock}, `skill_frozen`=#{entity.skillFrozen}, "
    //// + "`skill_fast`=#{entity.skillFast}, `skill_crit`=#{entity.skillCrit}, "
    //// + "`battery_level`=#{entity.batteryLevel}, "
    //// + "`monthcard_expire_date`=#{entity.monthCardExpireDate}, `ten_challenge_times`=#{entity.tenChallengeTimes}, "
    //// + "`boss_bugle` = #{entity.bossBugle},"
    //// + "`qszs_battery_expire_date` = #{entity.qszsBatteryExpireDate}, `blnh_battery_expire_date` =
    // #{entity.blnhBatteryExpireDate}, "
    //// + "`lhtz_battery_expire_date` = #{entity.lhtzBatteryExpireDate}, `swhp_battery_expire_date` =
    // #{entity.swhpBatteryExpireDate}, "
    //// + "`dragon_crystal` = #{entity.dragonCrystal}, `fen_shen` = #{entity.fenShen},`send_gift` = #{entity.sendGift}"
    //// + " WHERE id=#{entity.id}")
    //// void update(@Param("entity") OseePlayerEntity playerEntity);

    /**
     * 根据用户id查找玩家数据
     */
    @Select("SELECT *  FROM " + TABLE_NAME + " WHERE user_id=#{userId}")
    OseePlayerEntity findByUserId(@Param("userId") long userId);

    /**
     * 查找所有玩家数据
     */
    @Select("SELECT id FROM " + TABLE_NAME + "")
    List<Long> getAllPlayer();

    /**
     * 查找玩家数量通过ip
     */
    @Select("SELECT count(1) FROM " + TABLE_NAME + " where userIp = ${userIp}")
    int getCountByIp(@Param("userIp") String userIp);

    /**
     * 查找所有玩家 id
     */
    @Select("SELECT id FROM " + TABLE_NAME1 + "")
    List<Long> getAllUserId();

    /**
     * 查找所有玩家数据
     */
    @Select("SELECT `id`,`user_id`, `money`, `bank_money`, `bank_password`, `lottery`,"
        + "`diamond`, `vip_level`, `level`, `experience`, `recharge_money`, `lose_control`, `player_type`, "
        + "`bronze_torpedo`, `silver_torpedo`, `gold_torpedo`,`gold_torpedo_bang`,"
        + "`skill_lock`, `skill_frozen`, `skill_fast`, `skill_double`, `skill_crit`,"
        + "`battery_level`, `monthcard_expire_date`, `ten_challenge_times`, `boss_bugle`,"
        + "`qszs_battery_expire_date`, `blnh_battery_expire_date`, `lhtz_battery_expire_date`, `swhp_battery_expire_date`,"
        + "`dragon_crystal`, `fen_shen` ,`rare_torpedo`,`rare_torpedo_bang`, `skillEle` FROM " + TABLE_NAME + " ")
    List<OseePlayerEntity> getAllPlay();

    /**
     * 查询金币排行榜
     */
    @Select("SELECT * FROM " + TABLE_NAME + " ORDER BY (money + bank_money) DESC, vip_level DESC LIMIT 0, ${size}")
    List<OseePlayerEntity> selectMoneyRanking(@Param("size") int size);

    /**
     * 查询vip排行榜
     */
    @Select("SELECT * FROM " + TABLE_NAME + " WHERE vip_level > 0 ORDER BY vip_level DESC, (money + bank_money) DESC "
        + "LIMIT 0, ${size}")
    List<OseePlayerEntity> selectVipRanking(@Param("size") int size);

    /**
     * 查询黄金鱼雷排行榜
     */
    @Select("SELECT * FROM " + TABLE_NAME + " ORDER BY gold_torpedo DESC, vip_level DESC LIMIT 0, ${size}")
    List<OseePlayerEntity> selectGoldTorpedoRanking(@Param("size") int size);

    /**
     * 查询用户id列表
     */
    @Select("select user.id,user.create_time as createTime from (" + UserMapper.TABLE_NAME + " user," + TABLE_NAME
        + " player) "
        + "where user.id = player.user_id ${condition} and user.id > 2000 ORDER BY ${order} user.id DESC ${page}")
    List<Map> getGmPlayerIdList(@Param("condition") String condition, @Param("page") String page,
        @Param("order") String order);

    @Select("SELECT IFNULL(SUM(gold_torpedo),0) as goldAll,IFNULL(SUM(skill_lock),0) as lockAll,IFNULL(SUM(magicLamp),0) as magicAll,"
        + "IFNULL(SUM(boss_bugle),0) as bossAll,IFNULL(SUM(diamond),0) as diamondAll,IFNULL(SUM(skill_crit),0) as critAll,"
        + "IFNULL(SUM(skill_frozen),0) as frozenAll,IFNULL(SUM(yuGu),0) as yuGuAll,"
        + "IFNULL(SUM(haiYaoShi),0) as haiYaoShiAll,IFNULL(SUM(wangHunShi),0) as wangHunShiAll,IFNULL(SUM(haiHunShi),0) as haiHunShiAll,"
        + "IFNULL(SUM(zhenZhuShi),0) as zhenZhuShiAll,IFNULL(SUM(haiShouShi),0) as haiShouShiAll,IFNULL(SUM(haiMoShi),0) as haiMoShiAll,"
        + "IFNULL(SUM(zhaoHuanShi),0) as zhaoHuanShiAll,IFNULL(SUM(dianChiShi),0) as dianChiShiAll,IFNULL(SUM(heiDongShi),0) as heiDongShiAll,"
        + "IFNULL(SUM(lingZhuShi),0) as lingZhuShiAll,IFNULL(SUM(longGu),0) as longGuAll,IFNULL(SUM(longZhu),0) as longZhuAll,"
        + "IFNULL(SUM(longYuan),0) as longYuanAll,IFNULL(SUM(longJi),0) as longJiAll,IFNULL(SUM(skillBlackHole),0) as skillBlackHoleAll,"
        + "IFNULL(SUM(skillTorpedo),0) as skillTorpedoAll,IFNULL(SUM(skillBit),0) as skillBitAll,"
        + "IFNULL(SUM(blackBullet),0) as blackBulletAll,IFNULL(SUM(bronzeBullet),0) as bronzeBulletAll"
        + ",IFNULL(SUM(silverBullet),0) as silverBulletAll" + ",IFNULL(SUM(goldBullet),0) as goldBulletAll"
        + ",IFNULL(SUM(skillEle),0) as skillEleAll" + ",IFNULL(SUM(rare_torpedo),0) as rareTorpedoAll"
        + ",IFNULL(SUM(rare_torpedo_bang),0) as rareTorpedoBangAll"
        + ",IFNULL(SUM(gold_torpedo_bang),0) as goldTorpedoBangAll" + ",IFNULL(SUM(gold_torpedo),0) as goldTorpedoAll"
        + ",IFNULL(SUM(lottery),0) as lotteryAll from tbl_osee_player")
    Map<String, Object> getAllSkill();

    @Select("SELECT IFNULL(SUM(gold_torpedo),0) as goldAll,IFNULL(SUM(skill_lock),0) as lockAll,IFNULL(SUM(magicLamp),0) as magicAll,"
        + "IFNULL(SUM(boss_bugle),0) as bossAll,IFNULL(SUM(diamond),0) as diamondAll,IFNULL(SUM(skill_crit),0) as critAll,"
        + "IFNULL(SUM(skill_frozen),0) as frozenAll,IFNULL(SUM(yuGu),0) as yuGuAll,"
        + "IFNULL(SUM(haiYaoShi),0) as haiYaoShiAll,IFNULL(SUM(wangHunShi),0) as wangHunShiAll,IFNULL(SUM(haiHunShi),0) as haiHunShiAll,"
        + "IFNULL(SUM(zhenZhuShi),0) as zhenZhuShiAll,IFNULL(SUM(haiShouShi),0) as haiShouShiAll,IFNULL(SUM(haiMoShi),0) as haiMoShiAll,"
        + "IFNULL(SUM(zhaoHuanShi),0) as zhaoHuanShiAll,IFNULL(SUM(dianChiShi),0) as dianChiShiAll,IFNULL(SUM(heiDongShi),0) as heiDongShiAll,"
        + "IFNULL(SUM(lingZhuShi),0) as lingZhuShiAll,IFNULL(SUM(longGu),0) as longGuAll,IFNULL(SUM(longZhu),0) as longZhuAll,"
        + "IFNULL(SUM(longYuan),0) as longYuanAll,IFNULL(SUM(longJi),0) as longJiAll,IFNULL(SUM(skillBlackHole),0) as skillBlackHoleAll,"
        + "IFNULL(SUM(skillTorpedo),0) as skillTorpedoAll,IFNULL(SUM(skillBit),0) as skillBitAll,"
        + "IFNULL(SUM(blackBullet),0) as blackBulletAll,IFNULL(SUM(bronzeBullet),0) as bronzeBulletAll"
        + ",IFNULL(SUM(silverBullet),0) as silverBulletAll" + ",IFNULL(SUM(goldBullet),0) as goldBulletAll"
        + ",IFNULL(SUM(skillEle),0) as skillEleAll" + ",IFNULL(SUM(rare_torpedo),0) as rareTorpedoAll"
        + ",IFNULL(SUM(rare_torpedo_bang),0) as rareTorpedoBangAll"
        + ",IFNULL(SUM(gold_torpedo_bang),0) as goldTorpedoBangAll" + ",IFNULL(SUM(gold_torpedo),0) as goldTorpedoAll"
        + ",IFNULL(SUM(dragon_crystal),0) as draAll" + ",IFNULL(SUM(money),0) as moneyAll"
        + ",IFNULL(SUM(money),0) as moneyAll" + ",IFNULL(SUM(lottery),0) as lotteryAll from tbl_osee_player"
        + " where user_id = #{userId}")
    Map<String, Object> getAllSkillByUserId(@Param("userId") long useId);

    @Select("SELECT IFNULL(SUM(gold_torpedo),0) as goldAll,IFNULL(SUM(money),0) as moneyAll,IFNULL(SUM(dragon_crystal),0) as draAll,"
        + "IFNULL(SUM(skill_lock),0) as lockAll,IFNULL(SUM(magicLamp),0) as magicAll,IFNULL(SUM(boss_bugle),0) as bossAll,"
        + "IFNULL(SUM(diamond),0) as diamondAll,IFNULL(SUM(skill_crit),0) as critAll,IFNULL(SUM(skill_frozen),0) as frozenAll"
        + ",IFNULL(SUM(yuGu),0) as yuGuAll,"
        + "IFNULL(SUM(haiYaoShi),0) as haiYaoShiAll,IFNULL(SUM(wangHunShi),0) as wangHunShiAll,IFNULL(SUM(haiHunShi),0) as haiHunShiAll,"
        + "IFNULL(SUM(zhenZhuShi),0) as zhenZhuShiAll,IFNULL(SUM(haiShouShi),0) as haiShouShiAll,IFNULL(SUM(haiMoShi),0) as haiMoShiAll,"
        + "IFNULL(SUM(zhaoHuanShi),0) as zhaoHuanShiAll,IFNULL(SUM(dianChiShi),0) as dianChiShiAll,IFNULL(SUM(heiDongShi),0) as heiDongShiAll,"
        + "IFNULL(SUM(lingZhuShi),0) as lingZhuShiAll,IFNULL(SUM(longGu),0) as longGuAll,IFNULL(SUM(longZhu),0) as longZhuAll,"
        + "IFNULL(SUM(longYuan),0) as longYuanAll,IFNULL(SUM(longJi),0) as longJiAll,IFNULL(SUM(skillBlackHole),0) as skillBlackHoleAll,"
        + "IFNULL(SUM(skillTorpedo),0) as skillTorpedoAll,IFNULL(SUM(skillBit),0) as skillBitAll,IFNULL(SUM(lottery),0) as lotteryAll from tbl_osee_player t1"
        + " left join tbl_ttmy_agent t2 on t1.user_id = t2.player_id" + " where t2.agent_player_id = #{agentId}")
    Map<String, Object> getAllSkillByAgentId(@Param("agentId") long agentId);

    /**
     * 查询用户数量
     */
    @Select("select count(user.id) from (" + UserMapper.TABLE_NAME + " user," + TABLE_NAME + " player) "
        + "where user.id = player.user_id ${condition} and user.id > 2000")
    int getGmPlayerCount(@Param("condition") String condition);

    /**
     * 新增数据
     */
    @Insert("INSERT INTO " + TBL_KILL_BOSS + " (`userId`, `nickName`, `batterLevel`, `mult`, `bossName`, "
        + "`createTime`,`room_index`,`award`, `blood_pool_float_kill_value` " + ") VALUES ("
        + "#{entity.userId}, #{entity.nickName}, #{entity.batterLevel}, #{entity.mult}, "
        + "#{entity.bossName}, #{entity.createTime},#{entity.roomIndex}, #{entity.award}, #{entity.bloodPoolFloatKillValue} "
        + ")")
    @Options(useGeneratedKeys = true, keyProperty = "entity.id")
    void saveKillBoss(@Param("entity") KillBossEntity killBossEntity);

    /**
     * 查询列表
     */
    @Select("select * from " + TBL_KILL_BOSS + " " + " where 1=1 ${condition} ORDER BY createTime DESC ${page}")
    List<KillBossEntity> getKillBossList(@Param("condition") String condition, @Param("page") String page);

    /**
     * 查询数量
     */
    @Select("select count(*) from " + TBL_KILL_BOSS + " " + " where 1=1 ${condition}")
    int getKillBossCount(@Param("condition") String condition);

    /**
     * 获取后台统计记录
     */
    @Select("SELECT COALESCE(SUM(money), 0) money,COALESCE(SUM(gold_torpedo), 0) goldTorpedo, COALESCE(SUM(bank_money), 0) bankMoney,COALESCE(SUM(dragon_crystal), 0) dragonCrystal FROM "
        + TABLE_NAME)
    Map<String, Object> getGmStatistics();

    /**
     * 根据用户Unionid查询用户数据
     */
    @Select("SELECT * FROM " + TABLE_NAME1 + " WHERE nickname = #{nickName} and head_url = #{headerUrl}")
    UserEntity findByNickNameAndHeaderUrl(@Param("nickName") String nickName, @Param("headerUrl") String headerUrl);

    /**
     * 根据用户openid查询用户数据
     */
    @Select("SELECT * FROM " + TABLE_NAME1 + " WHERE openid = #{openid}")
    UserEntity findByOpenId(@Param("openid") String openid);

    /**
     * 根据用户id查询用户数据
     */
    @Select("SELECT * FROM " + TABLE_NAME1 + " WHERE id = #{openid}")
    UserEntity findByid(@Param("openid") Long openid);

    /**
     * 查询今日注册用户
     */
    @Select("select * from tbl_app_user where WHERE TO_DAYS( NOW( ) ) - TO_DAYS( create_time) <= 1")
    List<UserEntity> selectTodayRegisterUser();

    /**
     * 查询指定时间注册用户
     */
    @Select("select * from " + TABLE_NAME1 + " where create_time LIKE \"%\" #{dayTime}\"%\"")
    List<UserEntity> selectDayRegisterUser(@Param("dayTime") String dayTime);

    /**
     * 保存数据
     */
    @Insert("INSERT INTO app_data.tbl_agent_player_all (`agent_id`,`agent_name`,`loginNum`,`goldAll`, `bossAll`, `critAll`, "
        + "`lockAll`, `magicAll`, `diamondAll`, `frozenAll`, `goldChange`, `create_time`,"
        + " `yuGuAll`,`haiShouShiAll`,`haiHunShiAll`,`haiMoShiAll`,`haiYaoShiAll`,`dianCiShiAll`,`heiDongShiAll`,`lingZhuShiAll`,`longGuAll`"
        + " ,`longJiAll`,`longYuanAll`,`longZhuAll`,`wangHunShiAll`,`zhaoHuanShiAll`,`zhenZhuShiAll`,`skillBitAll`,`skillBlackHoleAll`,`skillTorpedoAll`) "
        + "VALUES (#{entity.agentId}, #{entity.agentName}, #{entity.loginNum}, #{entity.goldAll}, #{entity.bossAll}, #{entity.critAll}, "
        + "#{entity.lockAll}, #{entity.magicAll}, #{entity.diamondAll}, #{entity.frozenAll}, #{entity.goldChange}, #{entity.createTime}"
        + ", #{entity.yuGuAll}, #{entity.haiShouShiAll}, #{entity.haiHunShiAll}, #{entity.haiMoShiAll}, #{entity.haiYaoShiAll}, #{entity.dianCiShiAll}, #{entity.heiDongShiAll}, #{entity.lingZhuShiAll}"
        + ", #{entity.longGuAll}, #{entity.longJiAll}, #{entity.longYuanAll}, #{entity.longZhuAll}, #{entity.wangHunShiAll}, #{entity.zhaoHuanShiAll}, #{entity.zhenZhuShiAll}"
        + ", #{entity.skillBitAll}" + ", #{entity.skillBlackHoleAll}, #{entity.skillTorpedoAll})")
    void saveAgentPlayerAll(@Param("entity") AgentPlayerAll entity);

}
