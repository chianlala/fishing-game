package com.maple.game.osee.dao.log.mapper;

import java.util.List;

import org.apache.ibatis.annotations.*;

import com.maple.game.osee.dao.log.entity.OseeFishingRecordLogEntity;

@Mapper
public interface OseeFishingRecordLogMapper {

    String TABLE_NAME = "tbl_osee_fishing_record_log";

    /**
     * 保存数据
     */
    @Insert("INSERT INTO " + TABLE_NAME + " (`player_id`,`room_index`, `spend_money`, `win_money`, "
        + "`drop_bronze_torpedo_num`, `drop_silver_torpedo_num`, `drop_gold_torpedo_num`, `drop_gold_torpedo_bang_num`, `drop_rare_torpedo_bang_num`, `drop_rare_torpedo_num`, "
        + "join_time, exit_time, time_spending, use_props, game_type) "
        + "VALUES (#{entity.playerId}, #{entity.roomIndex}, #{entity.spendMoney}, #{entity.winMoney}, "
        + "#{entity.dropBronzeTorpedoNum}, #{entity.dropSilverTorpedoNum}, #{entity.dropGoldTorpedoNum}, #{entity.dropGoldTorpedoBangNum}, #{entity.dropRareTorpedoBangNum}, #{entity.dropRareTorpedoNum}, #{entity.joinTime}, #{entity.exitTime}, #{entity.timeSpending}, #{entity.useProps}, #{entity.gameType})")
    void save(@Param("entity") OseeFishingRecordLogEntity entity);

    /**
     * 根据条件查询记录
     */
    @Select("SELECT * FROM " + TABLE_NAME + " log ${where} ORDER BY log.id DESC ${page}")
    List<OseeFishingRecordLogEntity> getLogList(@Param("where") String where, @Param("page") String page);

    /**
     * 根据条件查询记录数量
     */
    @Select("SELECT COUNT(*) totalNum FROM " + TABLE_NAME + " log ${where}")
    int getLogCount(@Param("where") String where);

    /**
     * 获取总数
     */
    @Select("select COALESCE(sum(${sum}), 0) from " + TABLE_NAME + " log ${where}")
    long getSum(@Param("sum") String sum, @Param("where") String where);

}
