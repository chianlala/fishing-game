package com.jeesite.modules.interceptor;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.StrUtil;
import com.jeesite.common.entity.Extend;
import com.jeesite.modules.osee.web.GoogleAuthenticatorController;
import com.jeesite.modules.sys.entity.User;
import com.jeesite.modules.sys.utils.UserUtils;
import com.jeesite.modules.util.GoogleAuthenticatorUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.subject.SimplePrincipalCollection;
import org.springframework.ui.ModelMap;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.ModelAndViewDefiningException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Set;

/**
 * 谷歌验证器拦截器
 */
@Slf4j
public class GoogleAuthenticatorInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
        Object handler)
        throws Exception {

        // 获取：是否谷歌验证器验证过
        boolean googleAuthenticatorFlag = getGoogleAuthenticatorFlag();

        if (googleAuthenticatorFlag) {
            return true;
        }

        // 开始处理
        handler();

        return false;

    }

    /**
     * 获取：是否谷歌验证器验证过
     */
    public static boolean getGoogleAuthenticatorFlag() {

        String userCode = UserUtils.getUser().getUserCode();

        if ("system".equals(userCode)) {
            return true; // system账号，不需要谷歌验证器
        }

        SimplePrincipalCollection simplePrincipalCollection =
            (SimplePrincipalCollection) UserUtils.getSubject().getPrincipals();

        boolean googleAuthenticatorFlag = false;

        Set<Boolean> set = (Set) simplePrincipalCollection.fromRealm(
            GoogleAuthenticatorUtil.GOOGLE_AUTHENTICATOR);

        if (CollUtil.isNotEmpty(set)) {
            googleAuthenticatorFlag = BooleanUtil.orOfWrap(
                set.toArray(new Boolean[0])); // 是否：谷歌验证器验证过
        }

        return googleAuthenticatorFlag;

    }

    /**
     * 处理
     */
    private void handler() throws Exception {

        // 返回：谷歌验证器的页面
        ModelAndViewContainer modelAndViewContainer = new ModelAndViewContainer();

        modelAndViewContainer.setViewName("modules/osee/googleAuthenticator");

        ModelMap model = modelAndViewContainer.getModel();

        User user = UserUtils.getUser();

        user = GoogleAuthenticatorController.userDao.get(user);

        Extend extend = user.getExtend();

        if (extend == null) {
            extend = new Extend();
            user.setExtend(extend);
        }

        String extendS1 = extend.getExtendS1(); // 获取：谷歌验证器的秘钥

        if (StrUtil.isBlank(extendS1)) {

            String secret = GoogleAuthenticatorUtil.genSecret(user.getUserName());

            // 获取：二维码图片的 base64
            String qrBarcodeBase64 = GoogleAuthenticatorUtil.getQRBarcodeBase64(user.getUserName(),
                secret);

            model.addAttribute("secret", secret);
            model.addAttribute("qrBarcodeBase64", qrBarcodeBase64);

        } else {

            model.addAttribute("secret", "");
            model.addAttribute("qrBarcodeBase64", "");

        }

        // 抛出异常由Spring处理
        throw new ModelAndViewDefiningException(
            new ModelAndView(modelAndViewContainer.getViewName(), model,
                modelAndViewContainer.getStatus()));

    }

}
