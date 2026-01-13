package com.jeesite.modules.osee.service;

import cn.hutool.core.util.URLUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import com.jeesite.common.entity.Page;
import com.jeesite.modules.osee.vo.CommonResponse;
import com.jeesite.modules.osee.vo.shop.GoldenPigFormVO;
import com.jeesite.modules.osee.vo.shop.OpenRewordFormVO;
import com.jeesite.modules.osee.vo.shop.RateFormVO;
import com.jeesite.modules.osee.vo.shop.ReportFormVO;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 游戏数据统计模块服务类
 *
 * @author zjl
 */
@Service
public class StatisticsService extends BaseService {

    /**
     * 获取服务器统计数据
     */
    public CommonResponse getServerData() {
        return restTemplate.getForObject(apiConfig.buildApiUrl("/osee/server/statistics"),
            CommonResponse.class);
    }

    /**
     * 获取服务器的监控数据
     */
    public CommonResponse getServerMonitorData(Date beginTime, Date endTime) {

        JSONObject jsonObject = JSONUtil.createObj();
        jsonObject.set("beginTime", beginTime);
        jsonObject.set("endTime", endTime);

        String buildQuery = URLUtil.buildQuery(jsonObject, null);

        return restTemplate
            .getForObject(apiConfig.buildApiUrl("/osee/server/monitor?" + buildQuery),
                CommonResponse.class);

    }

    /**
     * 获取捕鱼竞技模式游戏记录
     *
     * @return
     */
    public CommonResponse getServerGameData(int page, int limit, Date startDate, Date endDate,
        Integer mode,
        Integer type) {
        Map<String, Object> map = new HashMap<>();
        map.put("page", page);
        map.put("limit", limit);
        if (startDate != null && endDate != null) {
            map.put("startDate", startDate);
            map.put("endDate", endDate);
        }
        if (mode != null) {
            map.put("mode", mode);
        }
        if (type != null) {
            map.put("type", type);
        }
        return restTemplate
            .postForObject(apiConfig.buildApiUrl("/osee/server/games"), JSON.toJSONString(map),
                CommonResponse.class);
    }

    public CommonResponse getServerRankData(int page, int limit, Date date, Integer type,
        Integer mode) {

        Map<String, Object> map = new HashMap<>();
        map.put("page", page);
        map.put("limit", limit);
        map.put("date", date);
        map.put("type", type);
        map.put("mode", mode);

        return restTemplate
            .postForObject(apiConfig.buildApiUrl("/osee/server/ranks"), JSON.toJSONString(map),
                CommonResponse.class);

    }

    public CommonResponse getReportForm(ReportFormVO reportFormVO, Page page) {
        reportFormVO.setPageInfo(page);
        return restTemplate
            .postForObject(apiConfig.buildApiUrl("/osee/reportFormAll/list"),
                JSON.toJSONString(reportFormVO),
                CommonResponse.class);
    }

    public CommonResponse getOpenRewordForm(OpenRewordFormVO reportFormVO, Page page) {
        reportFormVO.setPageInfo(page);
        return restTemplate
            .postForObject(apiConfig.buildApiUrl("/osee/openRewordFormAll/list"),
                JSON.toJSONString(reportFormVO),
                CommonResponse.class);
    }

    public CommonResponse getGoldenPigForm(GoldenPigFormVO reportFormVO, Page page) {
        reportFormVO.setPageInfo(page);
        return restTemplate
            .postForObject(apiConfig.buildApiUrl("/osee/goldenPigFormAll/list"),
                JSON.toJSONString(reportFormVO),
                CommonResponse.class);
    }

    public CommonResponse getRateForm(RateFormVO rateFormVO, Page page) {
        rateFormVO.setPageInfo(page);
        return restTemplate
            .postForObject(apiConfig.buildApiUrl("/osee/rateFormAll/list"),
                JSON.toJSONString(rateFormVO),
                CommonResponse.class);
    }

    /**
     * 获取玩家预测节点
     */
    public CommonResponse playerData(String userId) {

        JSONObject jsonObject = JSONUtil.createObj();
        jsonObject.set("userId", userId);

        String buildQuery = URLUtil.buildQuery(jsonObject, null);

        return restTemplate
            .getForObject(apiConfig.buildApiUrl("/osee/server/monitor/playerData?" + buildQuery),
                CommonResponse.class);

    }

    /**
     * 获取：玩家游戏相关数据
     */
    public CommonResponse playerGameData(String userId) {

        JSONObject jsonObject = JSONUtil.createObj();
        jsonObject.set("userId", userId);

        String buildQuery = URLUtil.buildQuery(jsonObject, null);

        return restTemplate
            .getForObject(
                apiConfig.buildApiUrl("/osee/server/monitor/playerGameData?" + buildQuery),
                CommonResponse.class);

    }

    /**
     * 修改用户预测节点
     */
    public CommonResponse playerJczd0ListUpdate(Map<String, Object> paramMap) {

        return restTemplate
            .postForObject(apiConfig.buildApiUrl("/osee/player/jczd0List/update"),
                JSONUtil.toJsonStr(paramMap),
                CommonResponse.class);

    }

    /**
     * 获取玩家预测节点历史记录
     */
    public CommonResponse playerHistoryData(Map<String, Object> paramMap) {

        return restTemplate
            .postForObject(apiConfig.buildApiUrl("/osee/server/monitor/playerHistoryData"),
                JSONUtil.toJsonStr(paramMap),
                CommonResponse.class);

    }

    /**
     * 关闭个控
     */
    public CommonResponse playerClosePersonal(Map<String, Object> paramMap) {

        return restTemplate
            .postForObject(apiConfig.buildApiUrl("/osee/player/jczd0List/closePersonal"),
                JSONUtil.toJsonStr(paramMap),
                CommonResponse.class);

    }

}
