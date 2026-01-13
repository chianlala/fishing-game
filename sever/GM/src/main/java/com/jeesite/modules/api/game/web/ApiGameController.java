package com.jeesite.modules.api.game.web;

import com.alibaba.fastjson.JSONObject;
import com.jeesite.modules.osee.domain.GameNoticeEntity;
import com.jeesite.modules.osee.service.GameService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * 游戏内与后台交互的API接口
 *
 * @author Junlong
 */
@Controller
@RequestMapping("${project.apiPath}/game")
public class ApiGameController {

    /**
     * 获取游戏公告列表
     */
    @GetMapping("notice/list")
    @ResponseBody
    public String noticeList() {
        JSONObject object = new JSONObject();
        List<Map<String, Object>> noticeList = new LinkedList<>();
        for (GameNoticeEntity notice : GameService.notices) {
            long nowTime = System.currentTimeMillis();
            if (notice.getStartTime().getTime() < nowTime
                && notice.getEndTime().getTime() > nowTime) {
                Map<String, Object> data = new HashMap<>();
                data.put("title", notice.getTitle());
                data.put("content", notice.getContent());
                noticeList.add(data);
            }
        }
        object.put("notice", noticeList);
        return object.toJSONString();
    }
}
