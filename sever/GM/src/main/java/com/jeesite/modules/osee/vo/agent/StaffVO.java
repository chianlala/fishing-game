package com.jeesite.modules.osee.vo.agent;

import com.jeesite.modules.osee.vo.BaseVO;

import java.io.Serializable;
import java.util.Date;

/**
 * 渠道推广员
 */
public class StaffVO extends BaseVO implements Serializable {

    private static final long serialVersionUID = 1200791388413942730L;

    private Long channelId; // 该推广员直属的代理渠道玩家ID

    private Long promoterId;
    private String promoterName;
    private Date month;

    public Long getChannelId() {
        return channelId;
    }

    public void setChannelId(Long channelId) {
        this.channelId = channelId;
    }

    public Long getPromoterId() {
        return promoterId;
    }

    public void setPromoterId(Long promoterId) {
        this.promoterId = promoterId;
    }

    public String getPromoterName() {
        return promoterName;
    }

    public void setPromoterName(String promoterName) {
        this.promoterName = promoterName;
    }

    public Date getMonth() {
        return month;
    }

    public void setMonth(Date month) {
        this.month = month;
    }
}
