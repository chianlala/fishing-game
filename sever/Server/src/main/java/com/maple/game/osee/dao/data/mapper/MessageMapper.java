package com.maple.game.osee.dao.data.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.*;

import com.maple.game.osee.dao.data.entity.MessageEntity;

/**
 * 消息/邮件数据库操作接口
 *
 * @author Junlong
 */
@Mapper
public interface MessageMapper {

    String TABLE_NAME = "tbl_ttmy_message";

    /**
     * 查询代理玩家列表
     */
    @Select("select * from " + TABLE_NAME + " msg ${condition} ORDER BY create_time desc ${page} ")
    List<MessageEntity> getList(@Param("condition") String condition, @Param("page") String page);

    @Select("select count(*) count from " + TABLE_NAME + " msg ${condition} ")
    Map<String, Object> getListCount(@Param("condition") String condition);

    /**
     * 创建表
     */
    @Update("create table if not exists " + TABLE_NAME + " (" + " `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',"
        + " `title` varchar(100) NOT NULL COMMENT '标题'," + " `content` varchar(500) NOT NULL COMMENT '内容',"
        + " `read` bool NULL COMMENT '是否已读'," + " `receive` bool NULL COMMENT '是否已接收附件',"
        + " `from_id` bigint(20) NULL COMMENT '发件人'," + " `from_game_id` bigint NULL COMMENT '发件人游戏 id',"
        + " `to_id` bigint(20) NULL COMMENT '收件人'," + " `to_game_id` bigint NULL COMMENT '收件人游戏 id',"
        + " `items_json` varchar(512) NULL COMMENT '附件信息'," + " `state` int(11) NOT NULL COMMENT '数据状态 0-正常 1-删除',"
        + " `create_time` timestamp(0) NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '数据创建时间'," + " PRIMARY KEY (`id`)"
        + ") ENGINE = InnoDB AUTO_INCREMENT = 100001;")
    void createTable();

    /**
     * 保存数据
     */
    @Insert("insert into " + TABLE_NAME + " ("
        + "`title`, `content`, `read`, `receive`, `from_id`, `to_id`, `items_json`, `state`, `type`, `agent_mail_id`, `from_game_id`, `to_game_id`, `ref_id`"
        + ") values (" + "#{entity.title}, #{entity.content}, #{entity.read}, #{entity.receive}, "
        + "#{entity.fromId}, #{entity.toId}, " + "#{entity.itemsJson}," + "#{entity.state}," + "#{entity.type},"
        + "#{entity.agentMailId}," + "#{entity.fromGameId}," + "#{entity.toGameId}," + "#{entity.refId}" + ")")
    @Options(useGeneratedKeys = true, keyProperty = "entity.id")
    void save(@Param("entity") MessageEntity messageEntity);

    /**
     * 更新消息数据
     */
    @Update("update " + TABLE_NAME + " set " + "`title` = #{entity.title}, `content` = #{entity.content}, "
        + "`read` = #{entity.read}, `receive` = #{entity.receive}, "
        + "`from_id` = #{entity.fromId}, `to_id` = #{entity.toId}, " + "`receiveTime` = #{entity.receiveTime}, "
        + "`items_json` = #{entity.itemsJson}, `state` = #{entity.state}" + " where `id` = #{entity.id}")
    void update(@Param("entity") MessageEntity messageEntity);

    /**
     * 删除指定id的消息
     */
    @Delete("delete from " + TABLE_NAME + " where `id` = #{entity.id}")
    void delete(@Param("entity") MessageEntity messageEntity);

    /**
     * 逻辑删除数据
     */
    @Update("update " + TABLE_NAME + " set `state` = 1 where `id` = #{entity.id}")
    void deleteLogical(@Param("entity") MessageEntity messageEntity);

    /**
     * 删除超过7天的所有消息
     */
    @Delete("delete from " + TABLE_NAME + " where to_days(now()) - to_days(create_time) > 7")
    int deleteOut7Day();

    /**
     * 逻辑删除超过7天的所有消息
     */
    @Update("update " + TABLE_NAME + " set `state` = 1 where to_days(now()) - to_days(create_time) > 7")
    int deleteOut7DayLogical();

    /**
     * 根据id查找数据
     */
    @Select("select * from " + TABLE_NAME + " where `id` = #{id} and `state` = 0")
    MessageEntity getById(@Param("id") Long id);

    /**
     * 根据id查找数据
     */
    @Select("select * from " + TABLE_NAME + " where `id` = #{id} and `state` = #{state}")
    MessageEntity getByIdWithState(@Param("id") Long id, @Param("state") Integer state);

    /**
     * 根据id查找数据
     */
    @Select("select * from " + TABLE_NAME + " where `id` = #{id} ${condition}")
    MessageEntity getByIdWithCondition(@Param("id") Long id, @Param("condition") String condition);

    /**
     * 查找玩家接收到的所有消息
     */
    @Select("select * from " + TABLE_NAME + " where `to_id` = #{toId} and `state` = 0")
    List<MessageEntity> getByToId(@Param("toId") Long toId);

    /**
     * 查找玩家发送的所有消息
     */
    @Select("select * from " + TABLE_NAME + " where `from_id` = #{fromId} and `state` = 0")
    List<MessageEntity> getByFromId(@Param("fromId") Long fromId);

    /**
     * 获取指定玩家总共收到的消息条数
     */
    @Select("select count(*) from " + TABLE_NAME + " where `to_id` = #{toId} and `state` = 0")
    int getCountByToId(@Param("toId") Long toId);

    /**
     * 获取玩家未读消息数量
     */
    @Select("select count(*) from " + TABLE_NAME + " where `to_id` = #{id} and `read` = false and `state` = 0")
    int getUnreadCount(@Param("id") Long playerId);

    /**
     * 获取玩家未接收附件的消息数量
     */
    @Select("select count(*) from " + TABLE_NAME + " where `to_id` = #{id} and `receive` = false and `state` = 0")
    int getUnreceivedCount(@Param("id") Long playerId);

    /**
     * 获取系统全服邮件 tips:系统邮件发送ID为-1，发送对象为默认的0就是全服系统邮件
     */
    @Select("select * from " + TABLE_NAME + " where `from_id` = -1 and `to_id` = 0 and `state` = 0")
    List<MessageEntity> getServerMessageList();

    /**
     * 获取指定玩家收到指定的发件人消息数量
     */
    @Select("select count(*) from " + TABLE_NAME + " where `from_id` = #{fromId} and `to_id` = #{toId}")
    long getFromCountByToId(@Param("fromId") Long fromId, @Param("toId") Long toId);

    /**
     * 查找 refId关联的 反馈回复内容数据
     */
    @Select("select content, ref_id from " + TABLE_NAME + " where `type` = 14 and `ref_id` in (${refIdSetStr})")
    List<MessageEntity> getListByRefFeedbackIdSetStr(@Param("refIdSetStr") String refIdSetStr);

}
