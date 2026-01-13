package com.jeesite.modules.osee.vo.shop;

import com.jeesite.modules.osee.vo.BaseVO;

/**
 * 奖券商城商品库存数据传输实体
 *
 * @author Junlong
 */
public class StockVO extends BaseVO {

    private static final long serialVersionUID = 8454406497457544892L;

    private Long shopId;    // 库存对应的商品ID
    private Integer state;  // 兑换情况
    private Long userId;    // 兑换人ID
    private String number;  // 卡号

    private String password; // 卡密

    public Long getShopId() {
        return shopId;
    }

    public void setShopId(Long shopId) {
        this.shopId = shopId;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
