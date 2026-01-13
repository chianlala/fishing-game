package com.maple.game.osee.dao.data.mapper;

import java.util.List;

import org.apache.ibatis.annotations.*;

import com.maple.game.osee.dao.data.entity.UserProps;

/**
 * 代理数据接口  用户物品数据库
 *
 * @author Junlong
 */
@Mapper
public interface GUserPropsMapper {

    String TABLE_NAME = "g_user_props";

    /**
     * 查询用户的所有物品
     */
    @Select("select * from " + TABLE_NAME + " where `user_id` = #{id} ")
    List<UserProps> getUserPropsByUserId(Long id);

    /**
     * 查询用户的某个物品
     */
    @Select("select * from " + TABLE_NAME + " where `user_id` = #{userId} and `props_id` = #{propsId} ")
    UserProps getUserProps(Long userId, Integer propsId);

    /**
     * 数据写入
     */
    @Insert("<script> replace into " + TABLE_NAME + " (user_id,props_id,quantity) values  "
        + "  <foreach collection='result' item='item' separator=',' > "
        + "  (#{item.userId},#{item.propsId},#{item.quantity}) " + "  </foreach> </script>")
    Boolean saveUserPropsList(@Param("result") List<UserProps> result);

    /**
     * 插入一条
     * @param userProps
     * @return
     */
    @Insert("insert into g_user_props(user_id, props_id, quantity, expiration_time)\n" +
            "values (#{userId}, #{propsId}, #{quantity}, #{expirationTime})")
    Integer insertUserProps(UserProps userProps);

    /**
     * 更新
     * @param userProps
     * @return
     */
    @Update("update g_user_props " +
            "set quantity = #{quantity}," +
            "expiration_time = #{expirationTime} " +
            "where user_id = #{userId} and props_id = #{propsId}")
    Integer updateUserProps(UserProps userProps);
}
