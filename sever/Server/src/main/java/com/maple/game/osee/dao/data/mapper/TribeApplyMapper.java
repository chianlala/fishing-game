package com.maple.game.osee.dao.data.mapper;

import java.util.List;

import org.apache.ibatis.annotations.*;

import com.maple.game.osee.entity.tribe.TribeApply;

/**
 * 代理数据接口
 *
 * @author Junlong
 */
@Mapper
public interface TribeApplyMapper {

    String TABLE_NAME = "t_osee_tribe_apply";

    /**
     * 新增数据
     */
    @Insert("INSERT INTO " + TABLE_NAME + " (`userId`, `tribeId`, `Isadopt`, `create_time` " + ") VALUES ("
        + "#{entity.userId}, #{entity.tribeId}, #{entity.Isadopt}, #{entity.createTime} " + ")")
    @Options(useGeneratedKeys = true, keyProperty = "entity.id")
    void save(@Param("entity") TribeApply entity);

    /**
     * 获取申请
     */
    @Select("select * from " + TABLE_NAME + " WHERE `Isadopt` = #{Isadopt}")
    List<TribeApply> getTribeApplyByIsadopt(@Param("Isadopt") long Isadopt);

    /**
     * 获取申请
     */
    @Select("select * from " + TABLE_NAME + " WHERE `Isadopt` = #{Isadopt} and `userId` = #{userId}")
    List<TribeApply> getTribeApplyByIsadoptAndUserId(@Param("Isadopt") long Isadopt, @Param("userId") long userId);

    /**
     * 获取申请数据
     */
    @Select("select * from " + TABLE_NAME + " WHERE `id` = #{id}")
    TribeApply getTribeApplyById(@Param("id") long id);

    /**
     * 获取申请数据
     */
    @Select("select * from " + TABLE_NAME + " WHERE `tribeId` = #{tribeId} and `Isadopt` = #{Isadopt}")
    List<TribeApply> getTribeApplyByTribeId(@Param("tribeId") long tribeId, @Param("Isadopt") long Isadopt);

    /**
     * 获取申请数据
     */
    // @Select("select count(*) from " + TABLE_NAME + " WHERE `userId` = #{userId} and `tribeId` = #{tribeId}" )
    // int getCountByUserId(@Param("userId") long userId,@Param("tribeId") long tribeId);

    /**
     * 获取申请数据
     */
    @Select("select count(*) from " + TABLE_NAME + " WHERE `userId` = #{userId}")
    int getCountByUserId(@Param("userId") long userId);

    /**
     * 获取申请数据
     */
    @Select("select count(*) from " + TABLE_NAME
        + " WHERE `userId` = #{userId} and `tribeId` = #{tribeId} and `Isadopt` = '4'")
    int getCountByIsdopt(@Param("userId") long userId, @Param("tribeId") long tribeId);

    /**
     * 修改
     */
    @Update("UPDATE " + TABLE_NAME + " SET `userId` = #{entity.userId}, `tribeId` = #{entity.tribeId}, "
        + " `Isadopt` = #{entity.Isadopt} " + " WHERE `id` = #{entity.id}")
    void update(@Param("entity") TribeApply entity);

    /**
     * 删除指定数据
     */
    @Delete("delete from " + TABLE_NAME + " where `tribeId` = #{tribeId} and `userId` = #{userId}")
    void delete(@Param("tribeId") long tribeId, @Param("userId") long userId);

    /**
     * 删除指定数据
     */
    @Delete("delete from " + TABLE_NAME + " where `userId` = #{userId}")
    void deleteByUserId(@Param("userId") long userId);

}
