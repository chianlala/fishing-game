package com.maple.game.osee.dao.log.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.maple.game.osee.dao.log.entity.OseeForgingLogEntity;
import com.maple.game.osee.dao.log.entity.OseeTurnTableEntity;

/**
 * 充值记录接口
 */
@Mapper
public interface OseeForgingLogMapper {

    String TABLE_NAME = "tbl_osee_forging_log";
    String TABLE_NAME1 = "tbl_osee_turntable";

    /**
     * 保存数据
     */
    @Insert("INSERT INTO " + TABLE_NAME + " (`isSuccess`, `user_id`, `nickname`, `payForging`, `reward`, "
        + "`type`, `target`) VALUES (#{entity.isSuccess}, "
        + "#{entity.userId}, #{entity.nickname}, #{entity.payForging}, #{entity.reward}, #{entity.type}, "
        + "#{entity.target})")
    void save(@Param("entity") OseeForgingLogEntity entity);

    /**
     * 保存数据
     */
    @Insert("INSERT INTO " + TABLE_NAME1 + " (`userId`, `userName`, `itemId`, `itemNum` "
        + ") VALUES (#{entity.userId}, " + "#{entity.userName}, #{entity.itemId}, #{entity.itemNum})")
    void saveTurnTable(@Param("entity") OseeTurnTableEntity entity);

    /**
     * 查询所有中奖记录
     */
    @Select("SELECT * FROM " + TABLE_NAME1 + " ORDER BY `id` DESC limit 0,50")
    List<OseeTurnTableEntity> getAll();

    /**
     * 查询所有中奖记录
     */
    @Select("SELECT * FROM " + TABLE_NAME1 + " where userId =${userId} ORDER BY `id` DESC limit 0,50")
    List<OseeTurnTableEntity> getTurnByUserId(@Param("userId") long userId);

    /**
     * 根据条件查询充值记录
     */
    @Select("SELECT * FROM " + TABLE_NAME + " log ${where} ORDER BY `id` DESC ${page}")
    List<OseeForgingLogEntity> getLogList(@Param("where") String where, @Param("page") String page);

    /**
     * 根据条件查询充值统计值
     */
    @Select("SELECT COUNT(*) totalNum FROM " + TABLE_NAME + " log ${where}")
    long getLogCount(@Param("where") String where);

}
