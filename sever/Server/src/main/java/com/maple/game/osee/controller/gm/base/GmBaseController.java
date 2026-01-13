package com.maple.game.osee.controller.gm.base;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.maple.game.osee.entity.gm.CommonResponse;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 后台基础控制器
 */
public abstract class GmBaseController {

    /**
     * 查询日期格式化
     */
    public static SimpleDateFormat DATE_FORMATER;

    /**
     * 起始时间
     */
    public static Date START_TIME;

    public GmBaseController() {
        try {
            DATE_FORMATER = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            START_TIME = DATE_FORMATER.parse("2018-01-01 00:00:00");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 后台检查方法
     */
    public String checker(Method taskMethod, Object params) {

        try {

            CommonResponse response = new CommonResponse(true);
            taskMethod.invoke(this, params, response);
            response.setSuccess(StrUtil.isBlank(response.getErrMsg()));

            return JSONUtil.toJsonStr(response);

        } catch (InvocationTargetException e) {

            Throwable targetException = e.getTargetException();

            targetException.printStackTrace();
            return JSONUtil.toJsonStr(new CommonResponse("ERROR_UNKNOWN", targetException.getMessage()));

        } catch (Exception e) {

            e.printStackTrace();
            return JSONUtil.toJsonStr(new CommonResponse("ERROR_UNKNOWN", "操作异常！"));

        }

    }
}
