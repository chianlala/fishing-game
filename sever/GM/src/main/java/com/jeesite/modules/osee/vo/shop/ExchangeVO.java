package com.jeesite.modules.osee.vo.shop;

import java.io.Serializable;

/**
 * 实物交换数据传输实体
 *
 * @author zjl
 */
public class ExchangeVO implements Serializable {

    private static final long serialVersionUID = -5805008935310971852L;

    private String creator;     // 创建人姓名(订单提交者)
    private Long shopId;
    private Integer count;
    private Long playerId;
    private String phoneNum;
    private String consignee;   // 收货人
    private String address;     // 收货地址

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public Long getShopId() {
        return shopId;
    }

    public void setShopId(Long shopId) {
        this.shopId = shopId;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public Long getPlayerId() {
        return playerId;
    }

    public void setPlayerId(Long playerId) {
        this.playerId = playerId;
    }

    public String getPhoneNum() {
        return phoneNum;
    }

    public void setPhoneNum(String phoneNum) {
        this.phoneNum = phoneNum;
    }

    public String getConsignee() {
        return consignee;
    }

    public void setConsignee(String consignee) {
        this.consignee = consignee;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
