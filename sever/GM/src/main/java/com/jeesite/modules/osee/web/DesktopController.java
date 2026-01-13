package com.jeesite.modules.osee.web;

import com.jeesite.common.config.Global;
import com.jeesite.common.web.BaseController;
import com.jeesite.modules.interceptor.GoogleAuthenticatorInterceptor;
import com.jeesite.modules.sys.dao.UserDao;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;

/**
 * 系统主界面控制类
 *
 * @author zjl
 */
@Controller
@RequestMapping("${adminPath}/osee/desktop")
@Slf4j
public class DesktopController extends BaseController {

    @Resource
    UserDao userDao;

    /**
     * 管理系统主界面的默认显示
     */
    @RequestMapping("")
    public String desktop(Model model) {

        String productName = Global.getProperty("productName");

        model.addAttribute("productName", productName);

        model.addAttribute("googleAuthenticatorFlag",
            GoogleAuthenticatorInterceptor.getGoogleAuthenticatorFlag());

        return "modules/osee/desktop";

    }

}
