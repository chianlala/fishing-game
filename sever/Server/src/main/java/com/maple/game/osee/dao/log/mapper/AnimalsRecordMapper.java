package com.maple.game.osee.dao.log.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.*;

import com.maple.game.osee.dao.log.entity.AnimalsRecordLogEntity;

/**
 * 飞禽走兽游戏记录表
 */
@Mapper
public interface AnimalsRecordMapper {
    String TABLE_NAME = "tbl_osee_animals_record_log";

    /**
     * 保存数据
     */
    @Insert("INSERT INTO " + TABLE_NAME + " (`money`, `playerId`, `nickname`, `playBeforeMoney`, `playAfterMoney`, `input`,  `cardType`) "
            + "VALUES (#{entity.money}, #{entity.playerId}, #{entity.nickname}, #{entity.playBeforeMoney}, "
            + "#{entity.playAfterMoney}, #{entity.input},  #{entity.cardType})")
    void save(@Param("entity") AnimalsRecordLogEntity entity);

    @Insert({
            "<script>",
            "INSERT INTO " + TABLE_NAME + "(`money`, `playerId`, `nickname`, `playBeforeMoney`, `playAfterMoney`, `input`,  `cardType`) values ",
            "<foreach collection='testLists' item='entity' index='index' separator=','>",
            "(#{entity.money}, #{entity.playerId}, #{entity.nickname}, #{entity.playBeforeMoney},#{entity.playAfterMoney},#{entity.input},#{entity.cardType})",
            "</foreach>",
            "</script>"
    })
    int saveList(@Param(value = "testLists") List<AnimalsRecordLogEntity> testLists);

    /**
     * 根据条件查询记录
     */
    @Select("SELECT * FROM " + TABLE_NAME + " log ${where} ORDER BY log.id DESC ${page}")
    List<AnimalsRecordLogEntity> getLogList(@Param("where") String where, @Param("page") String page);


    /**
     * 从log表中 累加计算条件范围内 记录的 输赢金币总额 totalMoney 下注金币总额 totalCost 的赢取金币总额 AllTotalWin
     */
    @Select("SELECT COALESCE(SUM(money),0) totalMoney FROM " + TABLE_NAME + " log ${where}")
    Map<String, Object> getStatstic(@Param("where") String where);

    /**
     * 根据条件查询记录数量
     */
    @Select("SELECT COUNT(*) totalNum FROM " + TABLE_NAME + " log ${where}")
    int getLogCount(@Param("where") String where);

    /**
     * 创建表
     */
    @Update("CREATE TABLE IF NOT EXISTS " + TABLE_NAME + "(" +
            "  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '记录id'," +
            "  `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'," +
            "  `money` bigint(20) NOT NULL COMMENT '账户金币变动数额'," +
            "  `playerId` bigint(20) NOT NULL COMMENT '玩家id'," +
            "  `nickname` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '玩家昵称'," +
            "  `playBeforeMoney` bigint(20) NOT NULL COMMENT '游戏前剩余金币'," +
            "  `playAfterMoney` bigint(20) NOT NULL COMMENT '游戏后剩余金币'," +
            "  `input` varchar(255) COLLATE utf8mb4_general_ci NOT NULL COMMENT '下注情况'," +
            "  `cardType` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '中奖'," +
            "  PRIMARY KEY (`id`) USING BTREE," +
            "  KEY `INDEX_PLAYERID_TIME` (`playerId`,`create_time`)" +
            ") ENGINE=InnoDB AUTO_INCREMENT=260 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC")
    void createTable();
}
