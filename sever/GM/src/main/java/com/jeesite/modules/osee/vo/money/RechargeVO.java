package com.jeesite.modules.osee.vo.money;

import com.jeesite.modules.osee.vo.BaseVO;

import java.io.Serializable;
import java.util.Date;

/**
 * 充值明细数据传输实体类
 *
 * @author zjl
 */
public class RechargeVO extends BaseVO implements Serializable {

    private static final long serialVersionUID = -4490180356182504690L;

    private String orderNum;         // 订单号
    private Integer rechargeType;   // 充值方式 1:微信 2:支付宝 3:后台
    private Integer orderState;     // 订单状态 1:成功 2:失败

    private Long playerId;          // 玩家id
    private String nickname;        // 玩家昵称
    private String creator;         // 创建人
    private Integer shopType;       // 商品类型 4:钻石 3:奖券 1:金币

    private Date startTime;         // 起始时间 (时间戳)
    private Date endTime;           // 结束时间 (时间戳)

    public String getOrderNum() {
        return orderNum;
    }

    public void setOrderNum(String orderNum) {
        this.orderNum = orderNum;
    }

    public Integer getRechargeType() {
        return rechargeType;
    }

    public void setRechargeType(Integer rechargeType) {
        this.rechargeType = rechargeType;
    }

    public Integer getOrderState() {
        return orderState;
    }

    public void setOrderState(Integer orderState) {
        this.orderState = orderState;
    }

    public Long getPlayerId() {
        return playerId;
    }

    public void setPlayerId(Long playerId) {
        this.playerId = playerId;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public Integer getShopType() {
        return shopType;
    }

    public void setShopType(Integer shopType) {
        this.shopType = shopType;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }
}
