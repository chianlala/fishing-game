package com.jeesite.modules.configuration;

import com.jeesite.modules.interceptor.GoogleAuthenticatorInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 谷歌验证器的拦截器配置类
 */
@Configuration
public class GoogleAuthenticatorInterceptorConfiguration implements WebMvcConfigurer {

    @Value(value = "${adminPath}")
    private String adminPath;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {

        registry.addInterceptor(new GoogleAuthenticatorInterceptor())
            .addPathPatterns("/" + adminPath + "/osee/**") // 添加拦截路径
            .addPathPatterns("/" + adminPath + "/ttmy/agent/**") // 添加拦截路径
            .addPathPatterns("/" + adminPath + "/sys/**") // 添加拦截路径
        ;

    }

}
