package com.maple.game.osee.common;

import java.math.BigDecimal;
import java.util.function.Function;

import org.redisson.api.RBucket;
import org.springframework.stereotype.Component;

import com.maple.database.config.redis.RedisHelper;

/**
 * 缓存查找助手<br/>
 */
@Component
public class CacheFindHelper {

    /**
     * 先缓存查询,没有的话执行function 并将结果缓存, 缓存是空串或者null字符 取默认值
     *
     * @param key 键值
     * @param defaultValue 默认值
     * @param function 如果缓存等于默认值的额外操作
     * @param <T> 缓存值的类型
     * @return 缓存的值或额外操作返回的值
     */
    public static <T> T find(String key, T defaultValue, Function<String, T> function) {
        final RBucket<String> value = RedisHelper.redissonClient.getBucket(key);

        String str = value.get();

        final T apply;
        if (!value.isExists() || str == null || "".equals(str) || "null".equals(str) || "0".equals(str)) {// 没有缓存的数据
            apply = function.apply(key);
            value.set(apply.toString());
            return apply;
        }

        if (defaultValue instanceof String) {
            return (T)str;
        }
        if (defaultValue instanceof Integer) {
            return (T)Integer.valueOf(new BigDecimal(str).intValue());
        }
        if (defaultValue instanceof Long) {
            return (T)Long.valueOf(new BigDecimal(str).longValue());
        }
        if (defaultValue instanceof Double) {
            return (T)Double.valueOf(new BigDecimal(str).doubleValue());
        }
        if (defaultValue instanceof Float) {
            return (T)Float.valueOf(new BigDecimal(str).floatValue());
        }

        return defaultValue;
    }
}
