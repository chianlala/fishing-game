package com.maple.game.osee.dao.data.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.maple.game.osee.dao.data.entity.ShopProps;

/**
 *
 * @author Junlong
 */
@Mapper
public interface GShopPropsMapper {

    String TABLE_NAME = "g_shop_props";

    /**
     * 查询所有在售商品
     */
    @Select("select * from " + TABLE_NAME + " where `id` = #{id} and `status` = 1 ")
    ShopProps getForSaleProp(@Param("id") Long id);
}
