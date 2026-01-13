package com.maple.game.osee.configuration;

import cn.hutool.cache.Cache;
import cn.hutool.cache.CacheUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CacheConfiguration {

    /**
     * 本地缓存
     */
    @Bean
    public Cache<String, Object> cache() {
        return CacheUtil.newLRUCache(5120);
    }

}
