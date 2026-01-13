package com.jeesite.modules.osee.vo.shop;

import com.jeesite.modules.osee.vo.BaseVO;

import java.io.Serializable;

/**
 * 商城奖励传输实体类
 *
 * @author zjl
 */
public class ShopVO extends BaseVO implements Serializable {

    private static final long serialVersionUID = -3236925015560698862L;

    private Long id;            // 奖品id
    private Integer type;       // 奖品类型
    private String name;        // 奖品名称
    private String img;         // 奖励图片链接
    private Integer count;      // 奖品数量
    private Integer cost;       // 消耗奖券数量
    private Integer size;       // 限购次数 0:无限
    private Integer refreshType;// 更新频率 0:无限制 1:每日一次 2:每周一次 3:每月一次
    private Integer sendType;   // 发货类型 1-实时兑换 2-手动发货 3-自动发卡
    private Integer stock;      // 库存

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public Integer getCost() {
        return cost;
    }

    public void setCost(Integer cost) {
        this.cost = cost;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    public Integer getRefreshType() {
        return refreshType;
    }

    public void setRefreshType(Integer refreshType) {
        this.refreshType = refreshType;
    }

    public Integer getSendType() {
        return sendType;
    }

    public void setSendType(Integer sendType) {
        this.sendType = sendType;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }
}
