package com.maple.game.osee.dao.log.mapper;

import org.apache.ibatis.annotations.*;

import com.maple.game.osee.entity.gm.ChangeAll;

/**
 * 捕鱼大奖赛 游戏记录
 */
@Mapper
public interface AppLoginLogMapper {

    String TABLE_NAME = "tbl_app_login_log";

    @Update("CREATE TABLE IF NOT EXISTS " + TABLE_NAME + "(\n"
        + "  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '登录id',\n"
        + "  `user_id` bigint(20) DEFAULT NULL COMMENT '用户id',\n"
        + "  `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '登录时间',\n"
        + "  `exit_time` timestamp NULL DEFAULT NULL COMMENT '退出时间',\n" + "  PRIMARY KEY (`id`) USING BTREE\n" + ") ;")
    void createTable();

    @Select("select COUNT(DISTINCT a.id) from app_data.tbl_app_user a LEFT JOIN app_data.tbl_ttmy_agent b on a.id = b.player_id "
        + "LEFT JOIN app_log.tbl_app_login_log c on a.id = c.user_id "
        + "where b.agent_player_id = #{agentId} and c.create_time like \"%\" #{date}\"%\" and a.create_time like \"%\" #{date1}\"%\"")
    int getByDayTime(@Param("date") String date, @Param("date1") String date1, @Param("agentId") Long agentId);

    @Select("select COUNT(DISTINCT a.id) from app_data.tbl_app_user a "
        + "LEFT JOIN app_log.tbl_app_login_log c on a.id = c.user_id "
        + "where c.create_time like \"%\" #{date}\"%\" and a.create_time like \"%\" #{date1}\"%\"")
    int getByDayTimeAll(@Param("date") String date, @Param("date1") String date1);

    @Select("select COUNT(DISTINCT a.id) from app_data.tbl_app_user a LEFT JOIN app_data.tbl_ttmy_agent b on a.id = b.player_id "
        + "LEFT JOIN app_log.tbl_app_login_log c on a.id = c.user_id "
        + "where b.agent_player_id = #{agentId} and c.create_time like \"%\" #{date}\"%\"")
    int getTwoByDayTime(@Param("date") String date, @Param("agentId") Long agentId);

    @Select("select COUNT(DISTINCT a.id) from app_data.tbl_app_user a "
        + "LEFT JOIN app_log.tbl_app_login_log c on a.id = c.user_id " + "where c.create_time like \"%\" #{date}\"%\"")
    int getTwoByDayTimeAll(@Param("date") String date);

    @Select("select COUNT(DISTINCT a.id) from app_data.tbl_app_user a LEFT JOIN app_data.tbl_ttmy_agent b on a.id = b.player_id "
        + "where b.agent_player_id = #{agentId} and a.create_time like \"%\" #{date}\"%\"")
    int getNewByDayTime(@Param("date") String date, @Param("agentId") Long agentId);

    @Select("select COUNT(DISTINCT a.id) from app_data.tbl_app_user a " + "where a.create_time like \"%\" #{date}\"%\"")
    int getNewByDayTimeAll(@Param("date") String date);

    @Select("select count(user_id) from (select user_id from app_log.tbl_app_login_log"
        + " where create_time like \"%\" #{date}\"%\"" + "group by user_id) as a")
    int getByTime(@Param("date") String date);

    /**
     * 保存数据
     */
    @Insert("INSERT INTO app_data.tbl_change_all_log (`loginNum`,`goldAll`, `bossAll`, `critAll`, "
        + "`lockAll`, `magicAll`, `diamondAll`, `frozenAll`, `moneyChange`, `dragonChange`, `create_time`,"
        + "`yuGuAll`,`haiShouShiAll`,`haiHunShiAll`,`haiMoShiAll`,`haiYaoShiAll`,`dianCiShiAll`,`heiDongShiAll`,`lingZhuShiAll`,`longGuAll`"
        + ",`longJiAll`,`longYuanAll`,`longZhuAll`,`wangHunShiAll`,`zhaoHuanShiAll`,`zhenZhuShiAll`,`skillBitAll`,`skillBlackHoleAll`,`skillTorpedoAll`) "
        + "VALUES (#{entity.loginNum}, #{entity.goldAll}, #{entity.bossAll}, #{entity.critAll}, "
        + "#{entity.lockAll}, #{entity.magicAll}, #{entity.diamondAll}, #{entity.frozenAll}, #{entity.moneyChange}, #{entity.dragonChange}, #{entity.createTime}"
        + ", #{entity.yuGuAll}, #{entity.haiShouShiAll}, #{entity.haiHunShiAll}, #{entity.haiMoShiAll}, #{entity.haiYaoShiAll}, #{entity.dianCiShiAll}, #{entity.heiDongShiAll}, #{entity.lingZhuShiAll}"
        + ", #{entity.longGuAll}, #{entity.longJiAll}, #{entity.longYuanAll}, #{entity.longZhuAll}, #{entity.wangHunShiAll}, #{entity.zhaoHuanShiAll}, #{entity.zhenZhuShiAll}"
        + ", #{entity.skillBitAll}" + ", #{entity.skillBlackHoleAll}, #{entity.skillTorpedoAll})")
    void saveChangeAll(@Param("entity") ChangeAll entity);

    @Select("select count(user_id) from (select user_id from app_log.tbl_app_login_log t1 left join app_data.tbl_ttmy_agent t2 on t1.user_id = t2.player_id"
        + " where t1.create_time like \"%\" #{date}\"%\"" + "and t2.agent_player_id = #{agentId} "
        + "group by user_id) as a")
    int getByTimeAndAgentId(@Param("date") String date, @Param("agentId") long agentId);

}
