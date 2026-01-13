package com.jeesite.modules.osee.vo.game;

import java.io.Serializable;

/**
 * 客服信息数据传输实体类
 *
 * @author zjl
 */
public class SupportVO implements Serializable {

    private static final long serialVersionUID = -6164641516050203922L;

    private String wechat;      // 客服微信
    private String qrcode;      // 客服二维码

    public String getWechat() {
        return wechat;
    }

    public void setWechat(String wechat) {
        this.wechat = wechat;
    }

    public String getQrcode() {
        return qrcode;
    }

    public void setQrcode(String qrcode) {
        this.qrcode = qrcode;
    }
}
