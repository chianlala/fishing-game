package com.jeesite.modules.osee.vo.agent;

import com.jeesite.modules.osee.vo.BaseVO;

import java.io.Serializable;

/**
 * 代理财务明细
 */
public class MoneyVO extends BaseVO implements Serializable {

    private static final long serialVersionUID = 1709631727817870302L;

    private Long channelId; // 渠道商ID

    public Long getChannelId() {
        return channelId;
    }

    public void setChannelId(Long channelId) {
        this.channelId = channelId;
    }
}
