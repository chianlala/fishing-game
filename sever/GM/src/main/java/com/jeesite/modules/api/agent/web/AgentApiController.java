package com.jeesite.modules.api.agent.web;

import com.alibaba.fastjson.JSON;
import com.jeesite.modules.api.agent.domain.Page;
import com.jeesite.modules.api.agent.domain.Token;
import com.jeesite.modules.api.agent.service.AgentApiService;
import com.jeesite.modules.api.agent.vo.*;
import com.jeesite.modules.osee.config.ProjectConfig;
import com.jeesite.modules.osee.vo.CommonResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 后台代理控制类
 *
 * @author Junlong
 */
@Controller
@RequestMapping("${project.apiPath}/agent")
public class AgentApiController {

    @Autowired
    private AgentApiService agentApiService;

    @Autowired
    private ProjectConfig projectConfig;

    /**
     * 进入绑定授权界面
     */
    @RequestMapping("bind/{playerId}")
    public String bind(@PathVariable String playerId, HttpServletRequest request) {
        if (StringUtils.isEmpty(playerId)) {
            request.setAttribute("errorMessage", "未知代理玩家");
            return "modules/api/agent/bindError";
        }
        CommonResponse commonResponse = agentApiService.bind(playerId);
        if (commonResponse.getSuccess()) {
            // 重定向到微信授权界面
            return "redirect:" + commonResponse.getData();
        }
        request.setAttribute("errorMessage", commonResponse.getErrMsg());
        return "modules/api/agent/bindError";
    }

    /**
     * 微信授权用户信息之后回调
     *
     * @param code  获取微信用户信息的code
     * @param state 请求链接的传入的值直接返回回来(此处用它来传递代理玩家的ID)
     */
    @RequestMapping("bind/callback")
    public String bindCallback(String code, String state, HttpServletRequest request) {
        if (StringUtils.isEmpty(code) || StringUtils.isEmpty(state)) {
            request.setAttribute("errorMessage", "回调参数错误");
            return "modules/api/agent/bindError";
        }
        CommonResponse commonResponse = agentApiService.bindCallback(code, state);
        if (commonResponse.getSuccess()) {
            // 绑定成功，跳转下载页面
            String downloadUrl = (String) commonResponse.getData();
            return "redirect:" + downloadUrl;
        }
        request.setAttribute("errorMessage", commonResponse.getErrMsg());
        return "modules/api/agent/bindError";
    }

    // ======================================= 渠道商后台 ========================================

    /**
     * 首页
     */
    @RequestMapping("index")
    public String index(Model model, HttpServletRequest request) {
        model.addAttribute("serverName", "渠道商管理后台");
        model.addAttribute("projectName", "捕鱼大富翁");

        Token token = agentApiService.getTokenFromRequest(request);
        if (token == null) { // 当前用户未登录，跳转到登录页面
            model.addAttribute("loginName", "渠道商登录");
            return "modules/api/agent/login";
        }
        model.addAttribute("nickname", token.getNickname());
        return "modules/api/agent/index";
    }

    /**
     * 登录
     */
    @RequestMapping("login")
    @ResponseBody
    public CommonResponse login(String username, String password, HttpServletRequest request) {
        CommonResponse response = agentApiService.login(username, password);
        if (!response.getSuccess()) {
            return response;
        }
        Token loginToken = JSON.parseObject(JSON.toJSONString(response.getData()), Token.class);
        // 登录信息存入session
        request.getSession().setAttribute("_token", loginToken);
        return new CommonResponse("index");
    }

    /**
     * 注销登录
     */
    @RequestMapping("logout")
    @ResponseBody
    public CommonResponse logout(HttpServletRequest request) {
        request.getSession().removeAttribute("_token"); // 清空session
        return new CommonResponse("index");
    }

    /**
     * 跳转到指定的页面
     */
    @RequestMapping("go/{page}")
    public String dispatch(@PathVariable String page) {
        return "modules/api/agent/" + page;
    }

    // =============================== 页面对接接口 =================================

    @RequestMapping("spreadList")
    @ResponseBody
    public Page<Map> spreadList(SpreadVO spreadVO, HttpServletRequest request) {
        CommonResponse commonResponse = agentApiService.getSpreadList(spreadVO, request);
        Page<Map> page = new Page<>();
        if (commonResponse != null && commonResponse.getSuccess()) {
            Map data = (Map) commonResponse.getData();
            page.setTotal((Integer) data.get("totalNum"));
            page.setRows((List<Map>) data.get("list"));

            page.addOtherData("dailyPersonNum", data.get("dailyPersonNum"));
            page.addOtherData("dailyRecharge", data.get("dailyRecharge"));
            return page;
        } else {
            page.setSuccess(false);
            page.setErrMsg(commonResponse == null ? "返回数据为空" : commonResponse.getErrMsg());
        }
        return page;
    }

    @RequestMapping("reportForm")
    @ResponseBody
    public Page<Map> reportForm(ReportFormVO reportFormVO, HttpServletRequest request) {
        CommonResponse commonResponse = agentApiService.getReportForm(reportFormVO, request);
        Page<Map> page = new Page<>();
        if (commonResponse != null && commonResponse.getSuccess()) {
            Map data = (Map) commonResponse.getData();
            page.setTotal((Integer) data.get("totalNum"));
            page.setRows((List<Map>) data.get("list"));
            return page;
        } else {
            page.setSuccess(false);
            page.setErrMsg(commonResponse == null ? "返回数据为空" : commonResponse.getErrMsg());
        }
        return page;
    }

    /**
     * 赠送记录列表数据
     */
    @RequestMapping("giveSend")
    @ResponseBody
    public Page<Map> giveList(GiveVO give, HttpServletRequest request) {
        give.setAgentId(give.getId());
        CommonResponse commonResponse = agentApiService.getGiveList(give, request);
        Page<Map> page = new Page<>();
        if (commonResponse.getSuccess()) {
            Map data = (Map) commonResponse.getData();
            page.setTotal((Integer) data.get("totalNum"));
            page.setRows((List<Map>) data.get("list"));

            Map<String, Object> otherData = new HashMap<>();
            // 礼物总数
            otherData.put("giftTotalNum", data.get("giftTotalNum"));
            page.setOtherData(otherData);
        }
        return page;
    }

    @RequestMapping("rateForm")
    @ResponseBody
    public Page<Map> rateForm(RateFormVO rateFormVO, HttpServletRequest request) {
        CommonResponse commonResponse = agentApiService.getRateForm(rateFormVO, request);
        Page<Map> page = new Page<>();
        if (commonResponse != null && commonResponse.getSuccess()) {
            Map data = (Map) commonResponse.getData();
            page.setTotal((Integer) data.get("totalNum"));
            page.setRows((List<Map>) data.get("list"));
            return page;
        } else {
            page.setSuccess(false);
            page.setErrMsg(commonResponse == null ? "返回数据为空" : commonResponse.getErrMsg());
        }
        return page;
    }

    @RequestMapping("userList")
    @ResponseBody
    public Page<Map> userList(UserVO userVO, HttpServletRequest request) {
        CommonResponse commonResponse = agentApiService.getUserList(userVO, request);
        Page<Map> page = new Page<>();
        if (commonResponse != null && commonResponse.getSuccess()) {
            Map data = (Map) commonResponse.getData();
            page.setTotal((Integer) data.get("totalNum"));
            page.setRows((List<Map>) data.get("list"));

            page.addOtherData("rate", data.get("rate"));
            page.addOtherData("channelRate", data.get("channelRate"));
            return page;
        } else {
            page.setSuccess(false);
            page.setErrMsg(commonResponse == null ? "返回数据为空" : commonResponse.getErrMsg());
        }
        return page;
    }

    @RequestMapping("changeRate")
    @ResponseBody
    public CommonResponse changeRate(Long playerId, String nickname, Double rate, Integer update,
        HttpServletRequest request) {
        CommonResponse commonResponse;
        if (playerId == null) { // 修改总比例
            commonResponse = agentApiService.changeRate(rate, update, request);
        } else { // 单个玩家比例修改
            commonResponse = agentApiService.changeUserRate(playerId, nickname, rate, update,
                request);
        }
        return commonResponse;
    }

    @RequestMapping("deleteUser")
    @ResponseBody
    public CommonResponse deleteUser(Long playerId, HttpServletRequest request) {
        return agentApiService.deleteUser(playerId, request);
    }

    @RequestMapping("moneyList")
    @ResponseBody
    public Page<Map> moneyList(MoneyVO moneyVO, HttpServletRequest request) {
        CommonResponse commonResponse = agentApiService.getMoneyList(moneyVO, request);
        Page<Map> page = new Page<>();
        if (commonResponse != null && commonResponse.getSuccess()) {
            Map data = (Map) commonResponse.getData();
            page.setTotal((Integer) data.get("totalNum"));
            page.setRows((List<Map>) data.get("list"));

            page.addOtherData("totalRecharge", data.get("totalRecharge"));
            page.addOtherData("channelTotalMoney", data.get("channelTotalMoney"));
            page.addOtherData("spreadTotalMoney", data.get("spreadTotalMoney"));
            return page;
        } else {
            page.setSuccess(false);
            page.setErrMsg(commonResponse == null ? "返回数据为空" : commonResponse.getErrMsg());
        }
        return page;
    }

    @RequestMapping("withdrawList")
    @ResponseBody
    public Page<Map> withdrawList(WithdrawListVO withdrawListVO, HttpServletRequest request) {
        CommonResponse commonResponse = agentApiService.getWithdrawList(withdrawListVO, request);
        Page<Map> page = new Page<>();
        if (commonResponse != null && commonResponse.getSuccess()) {
            Map data = (Map) commonResponse.getData();
            page.setTotal((Integer) data.get("totalNum"));
            page.setRows((List<Map>) data.get("list"));

            page.addOtherData("cardNo", data.get("cardNo"));
            page.addOtherData("accountName", data.get("accountName"));
            page.addOtherData("bank", data.get("bank"));
            page.addOtherData("bankName", data.get("bankName"));
            page.addOtherData("remindMoney", data.get("remindMoney"));
            page.addOtherData("drawMoney", data.get("drawMoney"));
            page.addOtherData("notDrawMoney", data.get("notDrawMoney"));
            return page;
        } else {
            page.setSuccess(false);
            page.setErrMsg(commonResponse == null ? "返回数据为空" : commonResponse.getErrMsg());
        }
        return page;
    }

    @RequestMapping("withdraw")
    @ResponseBody
    public CommonResponse withdraw(WithdrawVO withdrawVO, HttpServletRequest request) {
        return agentApiService.withdraw(withdrawVO, request);
    }

    @RequestMapping("changeAddress")
    @ResponseBody
    public CommonResponse changeAddress(AddressVO addressVO, HttpServletRequest request) {
        return agentApiService.changeAddress(addressVO, request);
    }

    @RequestMapping("changePassword")
    @ResponseBody
    public CommonResponse changePassword(String oldPassword, String newPassword, String rePassword,
        HttpServletRequest request) {
        if (!rePassword.equals(newPassword)) {
            return new CommonResponse("", "确认密码不一致");
        }
        return agentApiService.changePassword(oldPassword, newPassword, request);
    }

}
