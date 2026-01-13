package com.maple.game.osee.dao.data.mapper.gm;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.maple.database.data.mapper.UserAuthenticationMapper;
import com.maple.database.data.mapper.UserMapper;
import com.maple.game.osee.dao.data.entity.RateFormEntity;
import com.maple.game.osee.dao.data.entity.ReporFormEntity;
import com.maple.game.osee.dao.data.entity.gm.GmAuthenticationInfo;
import com.maple.game.osee.dao.data.entity.gm.GmCdkInfo;
import com.maple.game.osee.dao.data.mapper.*;
import com.maple.game.osee.entity.fishing.GoldenPig;
import com.maple.game.osee.entity.fishing.challenge.FishJc;

/**
 * 后台数据查询接口
 */
@Mapper
public interface GmCommonMapper {

    /**
     * 获取用户实名认证记录
     */
    @Select("SELECT record.id, record.create_time, record.user_id playerId, user.nickname nickName, record.name realName,"
        + " record.idcard_no idCardNum, record.phone_no phoneNum FROM " + UserAuthenticationMapper.TABLE_NAME
        + " record LEFT JOIN " + UserMapper.TABLE_NAME + " user ON record.user_id = user.id"
        + " ${condition} ORDER BY record.id DESC ${page}")
    List<GmAuthenticationInfo> getAuthenticationList(@Param("condition") String condition, @Param("page") String page);

    /**
     * 获取用户实名认证数量
     */
    @Select("SELECT COUNT(*) FROM " + UserAuthenticationMapper.TABLE_NAME + " record LEFT JOIN " + UserMapper.TABLE_NAME
        + " user ON record.user_id = user.id ${condition}")
    int getAuthenticationCount(@Param("condition") String condition);

    /**
     * 获取cdk列表
     */
    @Select("SELECT cdk.cdkey, type.name typeName, cdk.rewards, cdk.user_id, cdk.user_game_id, cdk.nickname nickname, cdk.agent_id AS agentId, cdk.agent_game_id AS agentGameId FROM "
        + OseeCdkMapper.TABLE_NAME + " cdk LEFT JOIN " + OseeCdkTypeMapper.TABLE_NAME
        + " type ON cdk.type_id = type.id ${condition} ORDER BY cdk.`id` DESC ${page}")
    List<GmCdkInfo> getCdkInfoList(@Param("condition") String condition, @Param("page") String page);

    /**
     * 获取cdk数量
     */
    @Select("SELECT COUNT(*) FROM " + OseeCdkMapper.TABLE_NAME + " ${condition}")
    int getCdkInfoCount(@Param("condition") String condition);

    /**
     * 获取cdk列表
     */
    @Select("SELECT * FROM " + ReportFormMapper.TABLE_NAME + " " + " ${condition}  ${page}")
    List<ReporFormEntity> getReportFormList(@Param("condition") String condition, @Param("page") String page);

    /**
     * 获取cdk列表
     */
    @Select("SELECT * FROM " + OseePlayerMapper.TABLE_NAME2 + " " + " ${condition}  ${page}")
    List<FishJc> getOpenRewordList(@Param("condition") String condition, @Param("page") String page);

    /**
     * 获取cdk数量
     */
    @Select("SELECT COUNT(*) FROM " + OseePlayerMapper.TABLE_NAME2 + " ${condition}")
    int getOpenRewordFormCount(@Param("condition") String condition);

    /**
     * 获取cdk列表
     */
    @Select("SELECT * FROM " + OseePlayerMapper.TABLE_NAME3 + " " + " ${condition}  ${page}")
    List<GoldenPig> getGoldenPigList(@Param("condition") String condition, @Param("page") String page);

    /**
     * 获取cdk数量
     */
    @Select("SELECT COUNT(*) FROM " + OseePlayerMapper.TABLE_NAME3 + " ${condition}")
    int getGoldenPigFormCount(@Param("condition") String condition);

    /**
     * 获取cdk数量
     */
    @Select("SELECT COUNT(*) FROM " + ReportFormMapper.TABLE_NAME + " ${condition}")
    int getReportFormCount(@Param("condition") String condition);

    /**
     * 获取cdk列表
     */
    @Select("SELECT * FROM " + RateFormMapper.TABLE_NAME + " " + " ${condition}  ${page}")
    List<RateFormEntity> getRatetFormList(@Param("condition") String condition, @Param("page") String page);

    /**
     * 获取cdk数量
     */
    @Select("SELECT COUNT(*) FROM " + RateFormMapper.TABLE_NAME + " ${condition}")
    int getRateFormCount(@Param("condition") String condition);

}
