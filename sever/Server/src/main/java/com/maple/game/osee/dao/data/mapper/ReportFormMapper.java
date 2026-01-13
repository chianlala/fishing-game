package com.maple.game.osee.dao.data.mapper;

import org.apache.ibatis.annotations.*;

import com.maple.game.osee.dao.data.entity.ReporFormEntity;

/**
 * 捕鱼大奖赛 游戏记录
 */
@Mapper
public interface ReportFormMapper {

    String TABLE_NAME = "tbl_app_report_form";

    @Update("CREATE TABLE IF NOT EXISTS " + TABLE_NAME + "(" + " `id` bigint(10) NOT NULL,"
        + " `day_time` varchar(50) DEFAULT NULL," + " `day1` varchar(255) NOT NULL DEFAULT '0',"
        + " `day2` varchar(255) NOT NULL DEFAULT '0'," + " `day3` varchar(255) NOT NULL DEFAULT '0',"
        + " `day4` varchar(255) NOT NULL DEFAULT '0'," + " `day5` varchar(255) NOT NULL DEFAULT '0',"
        + " `day6` varchar(255) NOT NULL DEFAULT '0'," + " `day7` varchar(255) NOT NULL DEFAULT '0',"
        + " `day8` varchar(255) NOT NULL DEFAULT '0'," + " `day9` varchar(255) NOT NULL DEFAULT '0',"
        + " `day10` varchar(255) NOT NULL DEFAULT '0'," + " `day11` varchar(255) NOT NULL DEFAULT '0',"
        + " `day12` varchar(255) NOT NULL DEFAULT '0'," + " `day13` varchar(255) NOT NULL DEFAULT '0',"
        + " `day14` varchar(255) NOT NULL DEFAULT '0'," + " `day15` varchar(255) NOT NULL DEFAULT '0',"
        + " `day16` varchar(255) NOT NULL DEFAULT '0'," + " `day17` varchar(255) NOT NULL DEFAULT '0',"
        + " `day18` varchar(255) NOT NULL DEFAULT '0'," + " `day19` varchar(255) NOT NULL DEFAULT '0',"
        + " `day20` varchar(255) NOT NULL DEFAULT '0'," + " `day21` varchar(255) NOT NULL DEFAULT '0',"
        + " `day22` varchar(255) NOT NULL DEFAULT '0'," + " `day23` varchar(255) NOT NULL DEFAULT '0',"
        + " `day24` varchar(255) NOT NULL DEFAULT '0'," + " `day25` varchar(255) NOT NULL DEFAULT '0',"
        + " `day26` varchar(255) NOT NULL DEFAULT '0'," + " `day27` varchar(255) NOT NULL DEFAULT '0',"
        + " `day28` varchar(255) NOT NULL DEFAULT '0'," + " `day29` varchar(255) NOT NULL DEFAULT '0',"
        + " `day30` varchar(255) NOT NULL DEFAULT '0',"
        + " `type` varchar(255) NOT NULL COMMENT '类型：register（注册）  pay(付费）'," + " `agent_id` bigint(10) NOT NULL ,"
        + "  PRIMARY KEY (`id`) USING BTREE) ENGINE = InnoDB AUTO_INCREMENT = 100001")
    void createTable();

    @Select("SELECT * from " + TABLE_NAME
        + " where day_time = #{initDay1} and agent_id = #{agentId} and type = #{type}")
    ReporFormEntity getByDayTime(@Param("initDay1") String initDay1, @Param("agentId") Long agentId,
        @Param("type") String type);

    @Insert("INSERT INTO " + TABLE_NAME + "(" + "`day_time`,`type`,`agent_id`" + ") VALUES ("
        + "#{entity.dayTime},#{entity.type},#{entity.agentId})")
    @Options(useGeneratedKeys = true, keyProperty = "entity.id")
    void save(@Param("entity") ReporFormEntity entity);

    /**
     * 更新某代理玩家下面所有玩家的代理等级
     */
    @Update("update " + TABLE_NAME + " set " + "day#{dayGap}=#{retention} " + "where day_time=#{initDay1} "
        + "and agent_id = #{agentId} " + "and type = #{type}")
    void updateDayByDayTime(@Param("dayGap") Integer dayGap, @Param("initDay1") String initDay1,
        @Param("retention") String retention, @Param("agentId") Long agentId, @Param("type") String type);
}
