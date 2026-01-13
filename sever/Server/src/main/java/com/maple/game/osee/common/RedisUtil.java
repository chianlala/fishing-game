package com.maple.game.osee.common;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import com.maple.database.config.redis.RedisHelper;

@Component
public class RedisUtil {

    public static String get(String key) {
        return val(key, "");
    }

    private static RedissonClient redissonClient;

    public RedisUtil(RedissonClient redissonClient) {

        RedisUtil.redissonClient = redissonClient;

    }

    /**
     * 从Redis中获取指定键对应的值，并将其转换为指定类型, 如果值为空，则设置为默认值
     *
     * @param key 键
     * @param defaultValue 默认值
     * @return 指定键对应的值 或 默认值
     */
    @SuppressWarnings("unchecked")
    public static <T> T val(String key, T defaultValue) {

        if (redissonClient == null) {
            return defaultValue;
        }

        final String str = "" + redissonClient.getBucket(key).get();

        // String str = RedisHelper.get(key);
        if ("".equals(str) || "null".equals(str)) {
            return defaultValue;
        }
        if (defaultValue instanceof String) {
            return (T)str;
        }
        if (defaultValue instanceof Integer) {
            return (T)Integer.valueOf(str);
        }
        if (defaultValue instanceof Long) {
            return (T)Long.valueOf(str);
        }
        if (defaultValue instanceof Double) {
            return (T)Double.valueOf(str);
        }
        if (defaultValue instanceof Float) {
            return (T)Float.valueOf(str);
        }

        return defaultValue;
    }

    public static Set<String> values(String key, int start, int end) {
        return RedisHelper.redissonClient.getScoredSortedSet(key).valueRangeReversed(start, end).stream()
            .map(Object::toString).collect(Collectors.toCollection(LinkedHashSet::new));
    }

    // List 操作
    public static void rightPush(String key, String... val) {
        RedisHelper.redissonClient.getList(key).addAll(Arrays.asList(val));
    }

    public static Long size(String key) {
        int size = RedisHelper.redissonClient.getList(key).size();
        return Long.parseLong("" + size);
    }

    public static void set(String key, String val, int index) {

        if (index < 0) {
            return;
        }

        int size = RedisHelper.redissonClient.getList(key).size();

        if (index >= size) {
            return;
        }

        RedisHelper.redissonClient.getList(key).set(index, val);

    }

    public static int get(String key, int index) {

        if (index < 0) {

            return 0;

        }

        final Object s = RedisHelper.redissonClient.getList(key).get(index);

        return s == null ? 0 : new BigDecimal(s.toString()).intValue();

    }

    public static List<String> getList(String key) {

        try {

            return RedisHelper.redissonClient.getList(key).range(0, -1).stream().map(o -> (String)o)
                .collect(Collectors.toList());

        } catch (Exception e) {

            return new ArrayList<>();

        }

    }

    // ZSet 操作
    public static void zAdd(String key, String value, double score) {
        RedisHelper.redissonClient.getScoredSortedSet(key).add(score, value);
    }

    public static void zRemove(String key, String value) {
        RedisHelper.redissonClient.getScoredSortedSet(key).remove(value);
    }

    public static Double zScore(String key, String member) {
        return RedisHelper.redissonClient.getScoredSortedSet(key).getScore(member);
    }

    public static Integer zRank(String key, String member) {
        return RedisHelper.redissonClient.getScoredSortedSet(key).rank(member);
    }

    /**
     * 备注：为什么返回 List<String>：因为 {@link #zAdd} 这个方法里面，放入的是 String类型的 value
     */
    public static List<String> zValueRangeByScore(String key, double startScore, double endScore) {
        return (List)RedisHelper.redissonClient.getScoredSortedSet(key).valueRange(startScore, true, endScore, true);
    }

}
