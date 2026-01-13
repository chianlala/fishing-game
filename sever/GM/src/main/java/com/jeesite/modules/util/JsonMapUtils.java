package com.jeesite.modules.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.*;

/**
 * jsonMap转换工具类
 */
public class JsonMapUtils {

    private static Logger logger = LoggerFactory.getLogger(JsonMapUtils.class);

    /**
     * 将对象转换为Map
     */
    public static Map<String, Object> objectToMap(Object obj) throws Exception {
        Map<String, Object> objMap = new HashMap<>();

        Class<?> clazz = obj.getClass();
        while (clazz != Object.class) {
            Field[] fields = clazz.getDeclaredFields();
            for (Field field : fields) {
                field.setAccessible(true);
                try {
                    switch (field.getType().getName()) {
                        case "java.util.Date":
                            objMap.put(field.getName(), ((Date) field.get(obj)).getTime());
                            break;
                        default:
                            objMap.put(field.getName(), field.get(obj));
                            break;
                    }
                } catch (Exception e) {
                    logger.error("转换对象时出现异常:[{}][{}]", e.getMessage(), e);
                }
            }
            clazz = clazz.getSuperclass();
        }

        return objMap;
    }

    /**
     * 对象数组转Map数组
     */
    public static List<Map<String, Object>> objectsToMaps(List<?> objs) throws Exception {
        List<Map<String, Object>> objMaps = new LinkedList<>();

        for (int i = 0; i < objs.size(); i++) {
            objMaps.add(objectToMap(objs.get(i)));
        }

        return objMaps;
    }

    /**
     * 获取对应类型的值
     */
    @SuppressWarnings("unchecked")
    public static <T> T parseObject(Map<String, Object> param, String key, JsonInnerType type)
        throws Exception {
        Object obj = param.get(key);

        try {
            if (obj instanceof Double) {
                Double dObj = (Double) obj;

                switch (type) {
                    case TYPE_DATE:
                        return (T) new Date(Math.round(dObj));
                    case TYPE_LONG:
                        return (T) Long.valueOf(Math.round(dObj));
                    case TYPE_INT:
                        return (T) Integer.valueOf((int) Math.round(dObj));
                    case TYPE_STRING:
                        return (T) Double.toString(dObj);
                    case TYPE_DOUBLE:
                    case TYPE_FLOAT:
                    case TYPE_STRING_ARRAY:
                    default:
                        return (T) obj;
                }
            } else if (obj instanceof String) {
                String sObj = obj.toString();
                switch (type) {
                    case TYPE_DATE:
                        return (T) new Date(Math.round(Long.parseLong(sObj)));
                    case TYPE_DOUBLE:
                        return (T) Double.valueOf(Double.parseDouble(sObj));
                    case TYPE_FLOAT:
                        return (T) Float.valueOf(Float.parseFloat(sObj));
                    case TYPE_INT:
                        return (T) Integer.valueOf(Integer.valueOf(sObj));
                    case TYPE_LONG:
                        return (T) Long.valueOf(Long.valueOf(sObj));
                    case TYPE_STRING_ARRAY:
                    case TYPE_STRING:
                    default:
                        return (T) obj;
                }
            }
            return (T) obj;
        } catch (Exception e) {
            logger.info("转换类型出错对象为:[{}][{}]", obj.getClass().getSimpleName(), obj);
        }
        return null;
    }

    /**
     * 内部类型枚举
     */
    public static enum JsonInnerType {

        /**
         * 整型
         */
        TYPE_INT,

        /**
         * 长整型
         */
        TYPE_LONG,

        /**
         * 字符串
         */
        TYPE_STRING,

        /**
         * 字符串数组
         */
        TYPE_STRING_ARRAY,

        /**
         * 单精度浮点型
         */
        TYPE_FLOAT,

        /**
         * 双精度浮点型
         */
        TYPE_DOUBLE,

        /**
         * 日期型
         */
        TYPE_DATE,

    }

}
