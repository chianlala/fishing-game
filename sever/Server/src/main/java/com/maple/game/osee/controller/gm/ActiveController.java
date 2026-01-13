package com.maple.game.osee.controller.gm;

import com.google.protobuf.Message;
import com.maple.engine.anotation.AppController;
import com.maple.engine.anotation.AppHandler;
import com.maple.engine.anotation.GmController;
import com.maple.engine.anotation.GmHandler;
import com.maple.engine.data.ServerUser;
import com.maple.game.osee.controller.gm.base.GmBaseController;
import com.maple.game.osee.entity.gm.CommonResponse;
import com.maple.game.osee.proto.ActiveList;
import com.maple.game.osee.proto.OseeMessage;
import com.maple.game.osee.service.ActiveService;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Resource;
import java.lang.reflect.Method;
import java.util.Map;

/**
 * 活跃榜，控制器
 */
@GmController
@AppController(checkMethod = "appChecker")
@Slf4j
public class ActiveController extends GmBaseController {

    @Resource
    ActiveService baseService;

    /**
     * 默认检查器
     */
    public void appChecker(Method taskMethod, Message req, ServerUser user, Long exp) {
        try {
            taskMethod.invoke(this, req, user);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取：当前活跃榜的情况
     */
    @AppHandler(msgCode = OseeMessage.OseeMsgCode.C_S_GET_ACTIVE_LIST_REQUEST_VALUE)
    public void getActiveList(ActiveList.GetActiveListRequest request, ServerUser user) {
        baseService.getActiveList(request, user);
    }

    /**
     * 设置：活跃榜配置
     */
    @GmHandler(key = "/osee/active/config/put")
    public void activeConfigPut(Map<String, Object> paramMap, CommonResponse response) {
        baseService.activeConfigPut(paramMap, response);
    }

    /**
     * 分页排序查询：活跃榜配置
     */
    @GmHandler(key = "/osee/active/config/page")
    public void activeConfigPage(Map<String, Object> paramMap, CommonResponse response) {
        baseService.activeConfigPage(paramMap, response);
    }

    /**
     * 获取：头像图片集合
     */
    @AppHandler(msgCode = OseeMessage.OseeMsgCode.C_S_GET_HEAD_IMAGE_LIST_REQUEST_VALUE)
    public void getHeadImageList(ActiveList.GetHeadImageListRequest request, ServerUser user) {
        baseService.getHeadImageList(request, user);
    }

}
