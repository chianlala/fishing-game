package com.jeesite.modules.util;

import org.apache.commons.lang3.ArrayUtils;

import java.lang.reflect.Field;
import java.util.*;

/**
 * 工具类
 *
 * @author zy
 */
public class SignBeanUtil {


    /**
     * 传JavaBean 和签名用的key ,有值参数进行字典排序 只过滤null （仅限于外部使用。支付接口） 比FilterNullSign(Object object, String
     * key, String Filter) 兼容性更广
     *
     * @param object
     * @param keyname
     * @param keyvalue
     * @param Filter
     * @param charset
     * @return
     * @throws IllegalAccessException
     * @throws IllegalArgumentException
     */
    public static String FilterNullSign(Object object, String keyname, String keyvalue,
        String Filter, String charset) throws IllegalArgumentException, IllegalAccessException {
        Sign signContent = SignBeanUtil.SignTool();
        String sign = null;
        Field[] fields = null;

        fields = getBeanFields(object.getClass(), fields);

        for (int i = 0; i < fields.length; i++) {
            Field f = fields[i];
            String name = f.getName();
            // 私有变量必须先设置Accessible为true
            f.setAccessible(true);
            Object valueObject = f.get(object);
            // System.out.println(valueObject);
            if (valueObject == null) {
                continue;
            }

            if (Filter != null && !Filter.equals("") && Filter.equals(name)) {
                continue;
            }
            String value = valueObject.toString();

            if (value != null) {
                signContent.putParam(name, value);
            }

        }
        signContent.putLastParam(keyname, keyvalue);
        String parm = signContent.getSignStr();
        System.out.println("待签名字符串只过滤null：" + parm);
        sign = MD5.sign(parm, charset);
        System.out.println("待签名字符串过滤null，签名结果：" + sign.toUpperCase());

        return sign;
    }

    /**
     * 获取类对象，包含父类
     *
     * @param cls
     * @param fs
     * @return
     */
    public static Field[] getBeanFields(Class cls, Field[] fs) {
        fs = (Field[]) ArrayUtils.addAll(fs, cls.getDeclaredFields());
        if (cls.getSuperclass() != null) {
            Class clsSup = cls.getSuperclass();
            fs = getBeanFields(clsSup, fs);
        }
        return fs;
    }

    public static Sign SignTool() {
        return new SignBeanUtil.Sign();
    }


    public static class Sign {

        private Map<String, String> map;
        private String lastStr = "";

        private Sign() {
            map = new HashMap<>();
        }

        public Sign putParam(String key, String value) {
            map.put(key, value);
            return this;
        }

        public Sign putParamFilter(String key, String value) {
            if (value != null && value != "") {
                map.put(key, value);
            }
            return this;
        }

        //只过滤null
        public Sign putParamFilterNull(String key, String value) {
            if (value != null) {
                map.put(key, value);
            }
            return this;
        }

        public Sign putLastParam(String key, String value) {
            lastStr = "&" + key + "=" + value;
            return this;
        }

        public String getParam(String key) {
            return map.get(key);
        }

        /**
         * 获取签名字符串
         *
         * @return
         */
        public String getSignStr() {
            List<String> keys = new ArrayList<String>(map.keySet());
            Collections.sort(keys);
            String prestr = "";
            for (int i = 0; i < keys.size(); i++) {
                String key = keys.get(i);
                String value = map.get(key);
                if (i == keys.size() - 1) {
                    prestr = prestr + key + "=" + value;
                } else {
                    prestr = prestr + key + "=" + value + "&";
                }
            }
            return prestr + lastStr;
        }

        /**
         * 获取签名
         *
         * @return
         */
        public String getSign() {
            return MD5.sign(getSignStr(), "utf-8");
        }
    }
}
