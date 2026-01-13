package com.maple.game.osee.dao.log.mapper;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.*;

import com.maple.game.osee.dao.log.entity.*;

/**
 * 代理活跃抽水数据接口
 */
@Mapper
public interface AgentCutLogMapper {

    String TABLE_NAME = "tbl_agent_cut_log";
    String TABLE_NAME1 = "tbl_ocean_get_reword_log";
    String TABLE_NAME2 = "tbl_ocean_promotion_log";
    String TABLE_NAME3 = "tbl_ocean_promotion_reword_log";
    String TABLE_NAME4 = "tbl_ocean_reword_log";

    /**
     * 建表
     */
    @Update("CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" + "  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'ID',"
        + "  `player_id` bigint(20) NOT NULL COMMENT '玩家ID'," + "  `agent_id` bigint(20) NOT NULL COMMENT '代理ID',"
        + "  `game` int(8) NOT NULL COMMENT '游戏'," + "  `cut_money` bigint(20) NULL COMMENT '金币',"
        + "  `cut_dragon_crystal` bigint(20) NULL COMMENT '龙晶',"
        + "  `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '数据创建时间',"
        + "  PRIMARY KEY (`id`) USING BTREE" + ") ENGINE = InnoDB AUTO_INCREMENT = 100001;")
    void createTable();

    /**
     * 插入数据
     */
    @Insert("insert into " + TABLE_NAME1 + " (userId, num) " + "values (#{log.userId}, #{log.num})")
    int saveGetReword(@Param("log") OceanGetRewordEntity log);

    /**
     * 插入数据
     */
    @Insert("insert into " + TABLE_NAME2 + " (nickName, userId,oceanUserId) "
        + "values (#{log.nickName}, #{log.userId}, #{log.oceanUserId})")
    int savePromotion(@Param("log") OceanPromotionEntity log);

    /**
     * 查询
     */
    @Select("select * from " + TABLE_NAME2 + " "
        + "where oceanUserId = #{oceanUserId} order by id desc limit #{start},#{end}")
    List<OceanPromotionEntity> getPromotion(@Param("oceanUserId") Long oceanUserId, @Param("start") int start,
        @Param("end") int end);

    /**
     * 查询
     */
    @Select("select * from " + TABLE_NAME1 + " " + "where userId = #{userId} order by id desc limit #{start},#{end} ")
    List<OceanGetRewordEntity> oceanGetReword(@Param("userId") Long userId, @Param("start") int start,
        @Param("end") int end);

    /**
     * 查询
     */
    @Select("select * from " + TABLE_NAME4 + " "
        + "where oceanUserId = #{oceanUserId} order by id desc limit #{start},#{end}")
    List<OceanRewordEntity> oceanReword(@Param("oceanUserId") Long oceanUserId, @Param("start") int start,
        @Param("end") int end);

    /**
     * 插入数据
     */
    @Insert("insert into " + TABLE_NAME3 + " (nickName, userId,diamond,reword,oceanUserId) "
        + "values (#{log.nickName}, #{log.userId}, #{log.diamond}, #{log.reword}, #{log.oceanUserId})")
    int savePromotionReword(@Param("log") OceanPromotionRewordEntity log);

    /**
     * 查询
     */
    @Select("select * from " + TABLE_NAME3 + " "
        + "where oceanUserId = #{oceanUserId} order by id desc limit #{start},#{end} ")
    List<OceanPromotionRewordEntity> getPromotionReword(@Param("oceanUserId") Long oceanUserId,
        @Param("start") int start, @Param("end") int end);

    /**
     * 插入数据
     */
    @Insert("insert into " + TABLE_NAME4 + " (nickName, userId,diamond,shopName,reword,oceanUserId) "
        + "values (#{log.nickName}, #{log.userId}, #{log.diamond}, #{log.shopName}, #{log.reword}, #{log.oceanUserId})")
    int saveReword(@Param("log") OceanRewordEntity log);


    /**
     * 获取指定代理玩家当天所收获的活跃金
     */
    @Select("select COALESCE(sum(cut_money), 0) money, COALESCE(sum(cut_dragon_crystal), 0) dragon from " + TABLE_NAME
        + " " + "where date(create_time) = curdate() and agent_id = #{agentId}")
    Map<String, BigDecimal> getDailyActiveByAgentId(@Param("agentId") Long agentId);

    /**
     * 获取指定代理玩家所收获的活跃金
     */
    @Select("select COALESCE(sum(cut_money), 0) money, COALESCE(sum(cut_dragon_crystal), 0) dragon from " + TABLE_NAME
        + " " + "where agent_id = #{agentId}")
    Map<String, BigDecimal> getTotalActiveByAgentId(@Param("agentId") Long agentId);

    /**
     * 删除指定代理玩家所有的佣金信息
     */
    @Delete("delete from " + TABLE_NAME + " where agent_id = #{agentId}")
    void deleteByAgentId(@Param("agentId") Long agentId);
}
