package com.jeesite.modules.osee.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;

/**
 * Spring自带的REST风格请求工具配置
 *
 * @author zjl
 */
@Configuration
public class RestTemplateConfig {

    @Bean
    public RestTemplate restTemplate() {
        final HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON_UTF8));
        // 一共有三个构造方法：空、默认请求工厂(apache,okhttp etc.)、消息转换器
        RestTemplate restTemplate = new RestTemplate();
        List<HttpMessageConverter<?>> messageConverters = restTemplate.getMessageConverters();
        for (HttpMessageConverter<?> messageConverter : messageConverters) {
            if (messageConverter instanceof StringHttpMessageConverter) {
                // 设置字符转换编码为utf-8，它默认的编码是ISO_8859_1
                ((StringHttpMessageConverter) messageConverter).setDefaultCharset(
                    Charset.forName("utf-8"));
                break;
            }
        }

        return restTemplate;
    }
}
