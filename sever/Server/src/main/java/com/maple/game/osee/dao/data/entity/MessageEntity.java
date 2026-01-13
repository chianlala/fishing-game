package com.maple.game.osee.dao.data.entity;

import java.util.Date;

import com.alibaba.fastjson.JSON;
import com.maple.database.data.DbEntity;
import com.maple.game.osee.entity.ItemData;

import lombok.Getter;
import lombok.Setter;

/**
 * 游戏内消息/邮件数据实体类
 *
 * @author Junlong
 */
@Getter
@Setter
public class MessageEntity extends DbEntity {

    private static final long serialVersionUID = 4104432655046923294L;

    /**
     * 标题
     */
    private String title;

    /**
     * 内容
     */
    private String content;

    /**
     * 发消息玩家ID
     */
    private Long fromId = 0L;

    /**
     * 发消息玩家游戏ID
     */
    private Long fromGameId = 0L;

    /**
     * 接收消息玩家ID
     */
    private Long toId = 0L;

    /**
     * 接收消息玩家游戏ID
     */
    private Long toGameId = 0L;

    /**
     * 附件信息(json格式,实际存入数据库内的数据)
     */
    private String itemsJson;

    /**
     * 消息附件物品
     */
    private ItemData[] items;

    /**
     * 是否已读
     */
    private Boolean read = false;

    /**
     * 是否领取附件
     */
    private Boolean receive = false;

    /**
     * 数据状态 0-正常（已同意） 1-已删除 2-撤销 3-待审核 4-已拒绝
     */
    private Integer state = 0;

    /**
     * 14 反馈回复
     */
    private Integer type;

    /**
     * 代理邮件主键 id
     */
    private Long agentMailId;

    /**
     * 过期时间
     */
    private Date expirationTime;

    /**
     * 领取时间
     */
    private Date receiveTime;

    /**
     * 关联的主键 id
     */
    private Long refId;

    public Long getToId() {
        return toId;
    }

    public void setToId(Long toId) {
        this.toId = toId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Boolean getRead() {
        return read;
    }

    public void setRead(Boolean read) {
        this.read = read;
    }

    public Boolean getReceive() {
        return receive;
    }

    public void setReceive(Boolean receive) {
        this.receive = receive;
    }

    public Long getFromId() {
        return fromId;
    }

    public void setFromId(Long fromId) {
        this.fromId = fromId;
    }

    public String getItemsJson() {
        if (this.items != null) {
            this.itemsJson = JSON.toJSONString(this.items);
        }
        return itemsJson;
    }

    public void setItemsJson(String itemsJson) {
        if (itemsJson != null && !itemsJson.equals("")) {
            // 数据库给该属性赋值之后就直接转换为数组格式数据
            ItemData[] itemData = JSON.parseObject(itemsJson, ItemData[].class);
            this.setItems(itemData);
        }
        this.itemsJson = itemsJson;
    }

    public ItemData[] getItems() {
        return items;
    }

    public void setItems(ItemData[] items) {
        this.items = items;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Long getAgentMailId() {
        return agentMailId;
    }

    public void setAgentMailId(Long agentMailId) {
        this.agentMailId = agentMailId;
    }

}
