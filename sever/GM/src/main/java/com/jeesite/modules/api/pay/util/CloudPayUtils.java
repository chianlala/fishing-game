package com.jeesite.modules.api.pay.util;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CloudPayUtils {

    //MD5
    public static String getMD5(String s) {
        String vals = null;
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            byte[] byteArray = s.getBytes("UTF-8");
            byte[] md5Bytes = md5.digest(byteArray);
            StringBuffer hexValue = new StringBuffer();
            for (int i = 0; i < md5Bytes.length; i++) {
                int val = ((int) md5Bytes[i]) & 0xff;
                if (val < 16) {
                    hexValue.append("0");
                }
                hexValue.append(Integer.toHexString(val));
            }
            vals = hexValue.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return vals;
    }

    public static String filterNullParams(Object obj) {
        Class<? extends Object> cls = obj.getClass();
        Field[] fd = cls.getDeclaredFields();
        List<String> list = new ArrayList<String>();
        String result = "";
        for (Field f : fd) {
            boolean isStatic = Modifier.isStatic(f.getModifiers());
            if (!isStatic) {
                f.setAccessible(true);
                try {
                    if (f.get(obj) != null && !f.get(obj).equals("")) {
                        list.add(f.getName() + "	" + f.get(obj));
                    }
                } catch (IllegalArgumentException | IllegalAccessException e) {
                    e.printStackTrace();
                }
                f.setAccessible(false);
            }
        }
        Collections.sort(list);
        for (int i = 0; i < list.size(); i++) {
            result += i > 0 ? list.get(i).replace("ly", "&ly").replace("	", "=")
                : list.get(i).replace("	", "=");
        }
        return result;
    }
}
