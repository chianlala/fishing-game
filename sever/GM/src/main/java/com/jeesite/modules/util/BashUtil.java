package com.jeesite.modules.util;

import cn.hutool.core.util.CharsetUtil;
import cn.hutool.extra.ssh.JschUtil;
import com.jcraft.jsch.Session;
import org.springframework.stereotype.Component;

@Component
public class BashUtil {

    /**
     * 后台
     */
    public static void exec(String execStr) {

        Session session = JschUtil
            .getSession(MySettingUtil.SETTING.getStr("server.gm.host"), 22,
                MySettingUtil.SETTING.getStr("server.gm.user"),
                MySettingUtil.SETTING.getStr("server.gm.password"));

        JschUtil.exec(session, execStr, CharsetUtil.CHARSET_UTF_8);

        JschUtil.close(session);

    }

    /**
     * 代理后台
     */
    public static void execAgent(String execStr) {

        Session session = JschUtil.getSession(MySettingUtil.SETTING.getStr("server.agent.gm.host"),
            22,
            MySettingUtil.SETTING.getStr("server.agent.gm.user"),
            MySettingUtil.SETTING.getStr("server.agent.gm.password"));

        JschUtil.exec(session, execStr, CharsetUtil.CHARSET_UTF_8);

        JschUtil.close(session);

    }

}
