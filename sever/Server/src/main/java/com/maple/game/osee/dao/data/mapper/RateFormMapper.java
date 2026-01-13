package com.maple.game.osee.dao.data.mapper;

import org.apache.ibatis.annotations.*;

import com.maple.game.osee.dao.data.entity.RateFormEntity;

/**
 * 捕鱼大奖赛 游戏记录
 */
@Mapper
public interface RateFormMapper {

    String TABLE_NAME = "tbl_app_rate_form";

    @Update("CREATE TABLE IF NOT EXISTS " + TABLE_NAME + "(" + " `id` bigint(10) NOT NULL,"
        + " `day_time` varchar(50) DEFAULT NULL," + " `rate` varchar(255) NOT NULL DEFAULT '0.00',"
        + " `arppu` varchar(255) NOT NULL DEFAULT '0.00'," + " `arpu` varchar(255) NOT NULL DEFAULT '0.00',"
        + " `new_rate` varchar(255) NOT NULL DEFAULT '0.00'," + " `new_arppu` varchar(255) NOT NULL DEFAULT '0.00',"
        + " `new_arpu` varchar(255) NOT NULL DEFAULT '0.00'," + " `new_pay_num` varchar(10) NOT NULL DEFAULT '0',"
        + " `login_num` varchar(10) NOT NULL DEFAULT '0'," + " `pay_num` varchar(10) NOT NULL DEFAULT '0',"
        + " `all_money` varchar(255) NOT NULL DEFAULT '0'," + " `agent_id` bigint(10) NOT NULL ,"
        + "  PRIMARY KEY (`id`) USING BTREE) ENGINE = InnoDB AUTO_INCREMENT = 100001")
    void createTable();

    // @Select("SELECT * from " + TABLE_NAME + " where day_time = #{initDay1} and agent_id = #{agentId} and type =
    // #{type}")
    // ReporFormEntity getByDayTime(@Param("initDay1") String initDay1, @Param("agentId") Long agentId, @Param("type")
    // String type);

    @Insert("INSERT INTO " + TABLE_NAME + "("
        + "`day_time`,`rate`,`arppu`,`arpu`,`new_rate`,`new_arppu`,`new_arpu`,`new_pay_num`,`login_num`,`pay_num`,`all_money`,`agent_id`"
        + ") VALUES ("
        + "#{entity.dayTime},#{entity.rate},#{entity.arppu},#{entity.arpu},#{entity.newRate},#{entity.newArppu},#{entity.newArpu},#{entity.newPayNum},#{entity.loginNum},#{entity.payNum},#{entity.allMoney},#{entity.agentId})")
    @Options(useGeneratedKeys = true, keyProperty = "entity.id")
    void save(@Param("entity") RateFormEntity entity);

}
