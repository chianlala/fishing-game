package com.maple.game.osee.dao.data.mapper;

import java.util.List;

import org.apache.ibatis.annotations.*;

import com.maple.game.osee.dao.data.entity.ShoppingEntity;

/**
 * 购物数据接口
 */
@Mapper
public interface ShoppingMapper {

    String TABLE_NAME = "tbl_shopping";
    String TABLE_NAME1 = "tbl_shopping1";

    /**
     * 保存数据
     */
    @Insert("INSERT INTO " + TABLE_NAME
        + " (`player_id`, `daily_bag_info`, `once_bag_info`, `money_card`, `last_receive`) VALUES "
        + "(#{entity.playerId}, #{entity.dailyBagInfo}, #{entity.onceBagInfo}, #{entity.moneyCard}, #{entity.lastReceive})")
    @Options(useGeneratedKeys = true, keyProperty = "entity.id")
    void save(@Param("entity") ShoppingEntity entity);

    /**
     * 保存数据
     */
    @Insert("INSERT INTO " + TABLE_NAME1
        + " (`player_id`, `daily_bag_info`, `once_bag_info`, `money_card`, `last_receive`) VALUES "
        + "(#{entity.playerId}, #{entity.dailyBagInfo}, #{entity.onceBagInfo}, #{entity.moneyCard}, #{entity.lastReceive})")
    @Options(useGeneratedKeys = true, keyProperty = "entity.id")
    void save1(@Param("entity") ShoppingEntity entity);

    /**
     * 更新数据
     */
    @Update("UPDATE " + TABLE_NAME
        + " SET `player_id` = #{entity.playerId}, `daily_bag_info` = #{entity.dailyBagInfo}, "
        + "`once_bag_info` = #{entity.onceBagInfo}, `money_card` = #{entity.moneyCard}, `last_receive` = #{entity.lastReceive} "
        + "WHERE `id` = #{entity.id}")
    void update(@Param("entity") ShoppingEntity entity);

    /**
     * 更新数据
     */
    @Update("UPDATE " + TABLE_NAME1
        + " SET `player_id` = #{entity.playerId}, `daily_bag_info` = #{entity.dailyBagInfo}, "
        + "`once_bag_info` = #{entity.onceBagInfo}, `money_card` = #{entity.moneyCard}, `last_receive` = #{entity.lastReceive} "
        + "WHERE `id` = #{entity.id}")
    void update1(@Param("entity") ShoppingEntity entity);

    /**
     * 根据id查找数据
     */
    @Select("SELECT * FROM " + TABLE_NAME + " WHERE `id` = #{id}")
    ShoppingEntity getById(@Param("id") long id);

    /**
     * 根据玩家id查找数据
     */
    @Select("SELECT * FROM " + TABLE_NAME + " WHERE `player_id` = #{id}")
    ShoppingEntity getByPlayerId(@Param("id") long id);

    /**
     * 根据玩家id查找数据
     */
    @Select("SELECT count(*) FROM " + TABLE_NAME1
        + " WHERE `player_id` = #{id} and once_bag_info = #{bagInfo} and create_time  LIKE \"%\" #{time}\"%\"")
    int getCountByContion(@Param("id") long id, @Param("time") String time, @Param("bagInfo") String bagInfo);

    /**
     * 根据玩家id查找数据
     */
    @Select("SELECT * FROM " + TABLE_NAME1 + " WHERE `player_id` = #{id} and create_time LIKE \"%\" #{time}\"%\"")
    List<ShoppingEntity> getByContion(@Param("id") long id, @Param("time") String time);

    /**
     * 创建表
     */
    @Update("CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" + "id bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id', "
        + "`create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间', "
        + "`player_id` bigint(20) NULL DEFAULT NULL COMMENT '用户id', "
        + "`daily_bag_info` varchar(1024) NULL DEFAULT NULL COMMENT '每日礼包', "
        + "`once_bag_info` varchar(1024) NULL DEFAULT NULL COMMENT '特惠礼包', "
        + "`money_card` tinyint(1) NULL DEFAULT NULL COMMENT '金币卡', "
        + "`last_receive` timestamp NULL DEFAULT '2010-1-1' COMMENT '最后领取时间', "
        + "PRIMARY KEY (`id`) USING BTREE) ENGINE = InnoDB AUTO_INCREMENT = 100001")
    void createTable();
}
