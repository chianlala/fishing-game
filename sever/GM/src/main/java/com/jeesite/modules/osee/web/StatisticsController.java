package com.jeesite.modules.osee.web;

import com.jeesite.common.config.Global;
import com.jeesite.common.web.BaseController;
import com.jeesite.modules.osee.service.StatisticsService;
import com.jeesite.modules.osee.vo.CommonResponse;
import com.jeesite.modules.osee.vo.shop.GoldenPigFormVO;
import com.jeesite.modules.osee.vo.shop.OpenRewordFormVO;
import com.jeesite.modules.osee.vo.shop.RateFormVO;
import com.jeesite.modules.osee.vo.shop.ReportFormVO;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 统计管理模块
 *
 * @author zjl
 */
@Controller
@RequestMapping("${adminPath}/osee/statistics")
public class StatisticsController extends BaseController {

    @Autowired
    private StatisticsService statisticsService;

    // ========================= 统计数据 ==========================

    @RequiresPermissions("statistics:data:view")
    @RequestMapping("data")
    public String data(Model model) {

        CommonResponse commonResponse = statisticsService.getServerData();

        if (commonResponse.getSuccess()) {

            Map data = (Map) commonResponse.getData();

            List showSessionNameList = (List) data.getOrDefault("showSessionNameList",
                new ArrayList<>());
            List openList = (List) data.getOrDefault("openList", new ArrayList<>());
            List sessionIdList = (List) data.getOrDefault("sessionIdList", new ArrayList<>());

            model.addAttribute("showSessionNameList", showSessionNameList);
            model.addAttribute("openList", openList);
            model.addAttribute("sessionIdList", sessionIdList);

        } else {

            model.addAttribute("showSessionNameList", new ArrayList<>());
            model.addAttribute("openList", new ArrayList<>());
            model.addAttribute("sessionIdList", new ArrayList<>());

        }

        return "modules/osee/statistics/statisticsData";

    }

    @RequiresPermissions("statistics:data:view")
    @RequestMapping("statistic/data")
    @ResponseBody
    public String statisticData() {
        CommonResponse commonResponse = statisticsService.getServerData();
        return renderResult(Global.TRUE, "", commonResponse);
    }

    // ========================= 统计监控 ==========================

    @RequiresPermissions("statistics:monitor:view")
    @RequestMapping("monitor")
    public String monitor() {
        return "modules/osee/statistics/statisticsMonitor";
    }

    @RequiresPermissions("statistics:monitor:view")
    @RequestMapping("monitor/data")
    @ResponseBody
    public String monitorData(@RequestParam("beginTime") Date beginTime,
        @RequestParam("endTime") Date endTime) {
        CommonResponse commonResponse = statisticsService.getServerMonitorData(beginTime, endTime);
        return renderResult(Global.TRUE, "", commonResponse);
    }

    @RequiresPermissions("player:view")
    @RequestMapping("monitor/playerData")
    @ResponseBody
    public String playerData(@RequestParam("userId") String userId) {
        CommonResponse commonResponse = statisticsService.playerData(userId);
        return renderResult(Global.TRUE, "", commonResponse);
    }

    @RequiresPermissions("player:view")
    @RequestMapping("monitor/playerGameData")
    @ResponseBody
    public String playerGameData(@RequestParam("userId") String userId) {
        CommonResponse commonResponse = statisticsService.playerGameData(userId);
        return renderResult(Global.TRUE, "", commonResponse);
    }

    @RequiresPermissions("player:view")
    @RequestMapping("monitor/playerHistoryData")
    @ResponseBody
    public String playerHistoryData(@RequestBody Map<String, Object> paramMap) {
        CommonResponse commonResponse = statisticsService.playerHistoryData(paramMap);
        return renderResult(Global.TRUE, "", commonResponse);
    }

    @RequiresPermissions("player:view")
    @RequestMapping("/player/jczd0List/update")
    @ResponseBody
    public String playerJczd0ListUpdate(@RequestBody Map<String, Object> paramMap) {

        CommonResponse commonResponse = statisticsService.playerJczd0ListUpdate(paramMap);

        if (commonResponse.getSuccess()) {
            return renderResult(Global.TRUE, "操作成功");
        }

        return renderResult(Global.FALSE, commonResponse.getErrMsg());

    }

    @RequiresPermissions("player:view")
    @RequestMapping("/player/jczd0List/closePersonal")
    @ResponseBody
    public String playerClosePersonal(@RequestBody Map<String, Object> paramMap) {

        CommonResponse commonResponse = statisticsService.playerClosePersonal(paramMap);

        if (commonResponse.getSuccess()) {
            return renderResult(Global.TRUE, "操作成功");
        }

        return renderResult(Global.FALSE, commonResponse.getErrMsg());

    }

    // ========================= 捕鱼竞技模式数据统计 ====================
    //@RequiresPermissions("statistics:data:view")
    @RequestMapping("statistic/rank")
    @ResponseBody
    public String rankData(int page, int limit, @RequestParam("date") Date date,
        @RequestParam("type") Integer type,
        @RequestParam("mode") Integer mode) {

        CommonResponse commonResponse = statisticsService.getServerRankData(page, limit, date, type,
            mode);
        return renderResult(Global.TRUE, "", commonResponse.getData());

    }

    @RequestMapping("game")
    public String game() {
        return "modules/osee/statistics/statisticsGame";
    }

    @RequestMapping("rank")
    public String rank() {
        return "modules/osee/statistics/statisticsRank";
    }

    @RequestMapping("statistic/games")
    @ResponseBody
    public String gameData(int page, int limit,
        @RequestParam(value = "startDate", required = false) Date startDate,
        @RequestParam(value = "endDate", required = false) Date endDate,
        @RequestParam(value = "mode", required = false) Integer mode,
        @RequestParam(value = "type", required = false) Integer type) {
        CommonResponse commonResponse =
            statisticsService.getServerGameData(page, limit, startDate, endDate, mode, type);
        return renderResult(Global.TRUE, "", commonResponse.getData());
    }

    @RequestMapping("osee/rateForm")
    public String rateForm(RateFormVO rateFormVO, Model model) {
        model.addAttribute("rateForm", rateFormVO);
        return "modules/osee/statistics/rateForm";
    }

    @RequestMapping("osee/reportForm")
    public String reportForm(ReportFormVO reportFormVO, Model model) {
        model.addAttribute("reportForm", reportFormVO);
        return "modules/osee/statistics/reportForm";
    }

    @RequestMapping("/reportForm")
    @ResponseBody
    public com.jeesite.common.entity.Page<Map> reportForm(ReportFormVO reportFormVO,
        HttpServletRequest request,
        HttpServletResponse response) {
        com.jeesite.common.entity.Page<Map> page = new com.jeesite.common.entity.Page<>(request,
            response);
        reportFormVO.setAgentId(0L);
        CommonResponse commonResponse = statisticsService.getReportForm(reportFormVO, page);
        if (commonResponse != null && commonResponse.getSuccess()) {
            Map data = (Map) commonResponse.getData();
            page.setCount((Integer) data.get("totalNum"));
            page.setList((List<Map>) data.get("list"));
            return page;
        }
        return page;
    }

    @RequestMapping("/rateForm")
    @ResponseBody
    public com.jeesite.common.entity.Page<Map> rateForm(RateFormVO rateFormVO,
        HttpServletRequest request,
        HttpServletResponse response) {
        com.jeesite.common.entity.Page<Map> page = new com.jeesite.common.entity.Page<>(request,
            response);
        rateFormVO.setAgentId(0L);
        CommonResponse commonResponse = statisticsService.getRateForm(rateFormVO, page);
        if (commonResponse != null && commonResponse.getSuccess()) {
            Map data = (Map) commonResponse.getData();
            page.setCount((Integer) data.get("totalNum"));
            page.setList((List<Map>) data.get("list"));
        }
        return page;
    }

    @RequestMapping("osee/openRewordForm")
    public String openRewordForm(OpenRewordFormVO reportFormVO, Model model) {
        model.addAttribute("openRewordForm", reportFormVO);
        return "modules/osee/statistics/openRewordForm";
    }

    @RequestMapping("/openRewordForm")
    @ResponseBody
    public com.jeesite.common.entity.Page<Map> openRewordForm(OpenRewordFormVO reportFormVO,
        HttpServletRequest request,
        HttpServletResponse response) {
        com.jeesite.common.entity.Page<Map> page = new com.jeesite.common.entity.Page<>(request,
            response);
        CommonResponse commonResponse = statisticsService.getOpenRewordForm(reportFormVO, page);
        if (commonResponse != null && commonResponse.getSuccess()) {
            Map data = (Map) commonResponse.getData();
            page.setCount((Integer) data.get("totalNum"));
            page.setList((List<Map>) data.get("list"));
            return page;
        }
        return page;
    }

    @RequestMapping("osee/goldenPigForm")
    public String goldenPigForm(GoldenPigFormVO reportFormVO, Model model) {
        model.addAttribute("goldenPigForm", reportFormVO);
        return "modules/osee/statistics/goldenPigForm";
    }

    @RequestMapping("/goldenPigForm")
    @ResponseBody
    public com.jeesite.common.entity.Page<Map> goldenPigForm(GoldenPigFormVO reportFormVO,
        HttpServletRequest request,
        HttpServletResponse response) {
        com.jeesite.common.entity.Page<Map> page = new com.jeesite.common.entity.Page<>(request,
            response);
        CommonResponse commonResponse = statisticsService.getGoldenPigForm(reportFormVO, page);
        if (commonResponse != null && commonResponse.getSuccess()) {
            Map data = (Map) commonResponse.getData();
            page.setCount((Integer) data.get("totalNum"));
            page.setList((List<Map>) data.get("list"));
            return page;
        }
        return page;
    }

}
