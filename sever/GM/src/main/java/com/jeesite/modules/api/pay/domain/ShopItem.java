package com.jeesite.modules.api.pay.domain;

import java.io.Serializable;

/**
 * 商城商品信息
 *
 * @author Junlong
 */
public class ShopItem implements Serializable {

    private static final long serialVersionUID = 63880363576973716L;

    private Integer id;         // 商品ID
    private Double payMoney;    // 需要支付的金额：元
    private String shopName;    // 商品名：如 金币*1000
    private Integer shopType;   // 商品类型 1:金币 3:奖券 4:钻石 跟游戏内的物品id完全对应
    private Integer shopCount;  // 商品内物品的数量：如 金币*1000 就是1000

    /**
     * 生成一个金币商品信息
     */
    public static ShopItem createGoldItem(Integer id, Double payMoney, Integer shopCount) {
        return new ShopItem(id, payMoney, "金币*" + shopCount, 1, shopCount);
    }

    /**
     * 生成一个钻石商品信息
     */
    public static ShopItem createDiamondItem(Integer id, Double payMoney, Integer shopCount) {
        return new ShopItem(id, payMoney, "钻石*" + shopCount, 4, shopCount);
    }

    /**
     * 生成一个华为商品信息
     */
    public static ShopItem createHuaweiItem(Integer id, Double payMoney, String shopName,
        Integer shopType, Integer shopCount) {
        return new ShopItem(id, payMoney, shopName, shopType, shopCount);
    }

    public ShopItem(Integer id, Double payMoney, String shopName, Integer shopType,
        Integer shopCount) {
        this.id = id;
        this.payMoney = payMoney;
        this.shopName = shopName;
        this.shopType = shopType;
        this.shopCount = shopCount;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Double getPayMoney() {
        return payMoney;
    }

    public void setPayMoney(Double payMoney) {
        this.payMoney = payMoney;
    }

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public Integer getShopType() {
        return shopType;
    }

    public void setShopType(Integer shopType) {
        this.shopType = shopType;
    }

    public Integer getShopCount() {
        return shopCount;
    }

    public void setShopCount(Integer shopCount) {
        this.shopCount = shopCount;
    }

    @Override
    public String toString() {
        return "ShopItem{" +
            "id=" + id +
            ", payMoney=" + payMoney +
            ", shopName='" + shopName + '\'' +
            ", shopType=" + shopType +
            ", shopCount=" + shopCount +
            '}';
    }
}
