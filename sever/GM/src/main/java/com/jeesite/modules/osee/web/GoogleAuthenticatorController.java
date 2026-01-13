package com.jeesite.modules.osee.web;

import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.StrUtil;
import com.jeesite.common.config.Global;
import com.jeesite.common.entity.Extend;
import com.jeesite.common.web.BaseController;
import com.jeesite.modules.sys.dao.UserDao;
import com.jeesite.modules.sys.entity.User;
import com.jeesite.modules.sys.utils.UserUtils;
import com.jeesite.modules.util.GoogleAuthenticatorUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.subject.SimplePrincipalCollection;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;

/**
 * 谷歌验证器控制器
 */
@Controller
@RequestMapping("${adminPath}/google/authenticator")
@Slf4j
public class GoogleAuthenticatorController extends BaseController {

    public static UserDao userDao;

    @Resource
    public void setUserDao(UserDao userDao) {
        GoogleAuthenticatorController.userDao = userDao;
    }

    /**
     * 绑定谷歌验证器
     */
    @ResponseBody
    @PostMapping(value = "/bind")
    public String bind(@RequestParam(value = "code") String code,
        @RequestParam(value = "secret") String secret) {

        User user = UserUtils.getUser();

        user = userDao.get(user);

        Extend extend = user.getExtend();

        if (extend == null) {
            extend = new Extend();
            user.setExtend(extend);
        }

        String extendS1 = extend.getExtendS1(); // 获取：谷歌验证器的秘钥

        if (StrUtil.isNotBlank(extendS1)) {
            return renderResult(Global.FALSE, "绑定失败：已经绑定了谷歌验证器");
        }

        // 验证 code
        boolean b = GoogleAuthenticatorUtil.authCode(code, secret);
        if (BooleanUtil.isFalse(b)) {
            return renderResult(Global.FALSE, "绑定失败：验证码错误");
        }

        extend.setExtendS1(secret); // 设置：谷歌验证器的秘钥
        userDao.update(user); // 保存到：数据库里

        // 保存到：shiro里
        SimplePrincipalCollection simplePrincipalCollection =
            (SimplePrincipalCollection) UserUtils.getSubject().getPrincipals();

        simplePrincipalCollection.add(true, GoogleAuthenticatorUtil.GOOGLE_AUTHENTICATOR);

        return renderResult(Global.TRUE, "绑定成功");

    }

    /**
     * 校验谷歌验证器
     */
    @ResponseBody
    @PostMapping(value = "/checkCode")
    public String checkCode(@RequestParam(value = "code") String code) {

        User user = UserUtils.getUser();

        user = userDao.get(user);

        Extend extend = user.getExtend();

        if (extend == null) {
            extend = new Extend();
            user.setExtend(extend);
        }

        String extendS1 = extend.getExtendS1(); // 获取：谷歌验证器的秘钥

        if (StrUtil.isBlank(extendS1)) {
            return renderResult(Global.FALSE, "验证失败：还未生成绑定秘钥");
        }

        // 验证 code
        boolean b = GoogleAuthenticatorUtil.authCode(code, extendS1);
        if (BooleanUtil.isFalse(b)) {
            return renderResult(Global.FALSE, "验证失败：验证码错误");
        }

        // 保存到：shiro里
        SimplePrincipalCollection simplePrincipalCollection =
            (SimplePrincipalCollection) UserUtils.getSubject().getPrincipals();

        simplePrincipalCollection.add(true, GoogleAuthenticatorUtil.GOOGLE_AUTHENTICATOR);

        return renderResult(Global.TRUE, "验证成功");

    }

}
