package com.jeesite.modules.util;

import cn.hutool.core.convert.Convert;
import org.beetl.core.Context;
import org.beetl.core.Function;

/**
 * 调用方法：`${jsValue(a.name)}` 返回值：${a.name} 目的：跳过 beetl的 ${}，语法
 */
public class JsValue implements Function {

    @Override
    public Object call(Object[] objects, Context context) {

        Object object = objects[0];

        if (object == null) {
            return "";
        }

        String str = Convert.toStr(object);

        if (objects.length == 2) {

            return "${" + str;

        } else {

            return "${" + str + "}";

        }

    }

}
