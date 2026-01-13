package com.jeesite.modules.osee.config;

import com.jeesite.modules.util.MySettingUtil;
import org.springframework.context.annotation.Configuration;

/**
 * 游戏服务器通信接口信息配置
 */
@Configuration
public class GameApiConfig {

    /**
     * @param url 具体的通信链接地址
     * @return 游戏http通信链接
     */
    public String buildApiUrl(String url) {

        if (url == null) {
            url = "";
        }

        if (!url.startsWith("/")) {
            url = "/" + url;
        }

        return MySettingUtil.SETTING.getStr("api.server.host") + url;

    }

}
