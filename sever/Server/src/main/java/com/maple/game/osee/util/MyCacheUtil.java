package com.maple.game.osee.util;

import cn.hutool.cache.Cache;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.map.MapUtil;
import org.jetbrains.annotations.NotNull;
import org.redisson.api.RedissonClient;
import org.redisson.codec.JsonJacksonCodec;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 缓存工具类
 */
@Component
public class MyCacheUtil {

    public static final String DEFAULT_STR_KEY = "";

    private static RedissonClient redissonClient;
    private static Cache<String, Object> cache; // 本地缓存

    public MyCacheUtil(RedissonClient redissonClient, Cache<String, Object> cache) {
        MyCacheUtil.redissonClient = redissonClient;
        MyCacheUtil.cache = cache;
    }

    @NotNull
    public static Map<String, Set<Long>> getDefaultStringSetLongResultMap() {
        Map<String, Set<Long>> defaultResultMap = MapUtil.newHashMap();
        defaultResultMap.put(MyCacheUtil.DEFAULT_STR_KEY, new HashSet<>());
        return defaultResultMap;
    }

    @NotNull
    public static Map<String, String> getDefaultStringStringResultMap() {
        Map<String, String> defaultResultMap = MapUtil.newHashMap();
        defaultResultMap.put(MyCacheUtil.DEFAULT_STR_KEY, null);
        return defaultResultMap;
    }

    @NotNull
    public static <T> List<T> getDefaultResultList() {
        List<T> defaultResultList = CollUtil.newArrayList();
        defaultResultList.add(null);
        return defaultResultList;
    }

    /**
     * 防止缓存里面设置 空值，而导致没有设置进去
     */
    @NotNull
    private static <T> T checkAndSetResultToDefault(T result, T defaultResult) {
        if (result == null) {
            result = defaultResult;
        } else if (result instanceof Map && CollUtil.isEmpty((Map)result)) {
            result = defaultResult;
        }
        if (result == null) {
            throw new RuntimeException("操作失败：defaultResult == null"); // 不能为 null，目的：防止缓存不写入数据
        }
        return result;
    }

    /**
     * 获取：一般类型的缓存，并且会设置缓存
     */
    @NotNull
    public static <T> T getObjectCacheAndSet(String redisKey, T defaultResult) {

        // 获取缓存
        T result = (T)cache.get(redisKey, () -> redissonClient.<T>getBucket(redisKey, new JsonJacksonCodec()).get());

        if (result != null) {
            return result;
        }

        // 设置：一般类型的缓存，并返回
        return setObjectCache(redisKey, result, defaultResult);
    }

    /**
     * 设置：一般类型的缓存 defaultResult：当缓存里面没有值时，如果 defaultResult为 null，则会报错 注意：使用的时候，一定要指定具体的 class类型，不然获取的时候会报错
     */
    public static <T> T setObjectCache(String redisKey, T value, T defaultResult) {

        // 检查并设置值
        value = checkAndSetResultToDefault(value, defaultResult);

        // 先设置：redis缓存
        redissonClient.<T>getBucket(redisKey, new JsonJacksonCodec()).set(value);

        // 再设置：本地缓存
        cache.put(redisKey, value);

        return value;
    }

    /**
     * 清除：一般缓存
     */
    public static void clearObjectCache(String redisKey) {
        redissonClient.getBucket(redisKey).delete();
        cache.remove(redisKey);
    }

}
