package com.jeesite.modules.osee.web;

import com.jeesite.common.config.Global;
import com.jeesite.common.entity.Page;
import com.jeesite.common.web.BaseController;
import com.jeesite.modules.osee.service.AgentService;
import com.jeesite.modules.osee.vo.CommonResponse;
import com.jeesite.modules.osee.vo.agent.*;
import com.jeesite.modules.osee.vo.money.AgentPlayerAllVO;
import com.jeesite.modules.sys.utils.UserUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

/**
 * 代理模块控制类
 *
 * @author Junlong
 */
@Controller
@RequestMapping("${adminPath}/ttmy/agent")
public class AgentController extends BaseController {

    @Autowired
    private AgentService agentService;

    // ========================= 代理列表 ==========================

    /**
     * 代理列表
     */
    @RequiresPermissions("agent:view")
    @RequestMapping("list")
    public String agent(AgentVO agent, Model model) {
        model.addAttribute("agent", agent);
        return "modules/ttmy/agent/agentList";
    }

    /**
     * 代理信息列表(json)
     */
    @RequiresPermissions("agent:view")
    @RequestMapping("list/data")
    @ResponseBody
    public Page<Map> agentList(AgentVO agent, HttpServletRequest request,
        HttpServletResponse response) {
        Page<Map> page = new Page<>(request, response);
        CommonResponse commonResponse = agentService.getAgentList(agent, page);
        if (commonResponse.getSuccess()) {
            Map data = (Map) commonResponse.getData();
            page.setCount((Integer) data.get("totalNum"));
            page.setList((List<Map>) data.get("list"));

            page.addOtherData("rate", data.get("rate")); // 渠道分成比例
        }
        return page;
    }

    /**
     * 代理操作：冻结，解冻
     *
     * @param playerIds 操作的代理玩家ID列表
     * @param option    1-冻结 0-解冻
     */
    @RequiresPermissions("agent:edit")
    @RequestMapping("operate")
    @ResponseBody
    public String operate(@RequestParam(value = "playerIds[]") Long[] playerIds,
        @RequestParam(value = "option") Integer option) {
        CommonResponse commonResponse = agentService.agentOperate(playerIds, option);
        String optionStr = option == 1 ? "冻结" : "解冻";
        if (commonResponse.getSuccess()) {
            return renderResult(Global.TRUE, optionStr + "成功！");
        }
        return renderResult(Global.FALSE, optionStr + "失败：" + commonResponse.getErrMsg());
    }

    /**
     * 修改渠道商总分成比例
     */
    @RequiresPermissions("agent:edit")
    @RequestMapping("change_rate")
    @ResponseBody
    public String changeRate(Double rate, Integer update) {
        CommonResponse commonResponse = agentService.updateChannelRate(rate, update);
        if (commonResponse.getSuccess()) {
            return renderResult(Global.TRUE, "修改成功");
        }
        return renderResult(Global.FALSE, "修改失败：" + commonResponse.getErrMsg());
    }

    /**
     * 修改指定渠道商分成比例
     */
    @RequiresPermissions("agent:edit")
    @RequestMapping("change_user_rate")
    @ResponseBody
    public String changeUserRate(Long playerId, String rate) {
        try {
            CommonResponse commonResponse = agentService.updateUserRate(playerId,
                Double.parseDouble(rate));
            if (commonResponse.getSuccess()) {
                return renderResult(Global.TRUE, "修改成功");
            }
            return renderResult(Global.FALSE, "修改失败：" + commonResponse.getErrMsg());
        } catch (NumberFormatException e) {
            return renderResult(Global.FALSE, "请输入数字");
        }
    }

    /**
     * 删除代理权限
     */
    @RequiresPermissions("agent:edit")
    @RequestMapping("delete")
    @ResponseBody
    public String delete(Long agentId) {
        CommonResponse commonResponse = agentService.deleteAgent(agentId);
        if (commonResponse.getSuccess()) {
            return renderResult(Global.TRUE, "删除成功");
        }
        return renderResult(Global.FALSE, "删除失败：" + commonResponse.getErrMsg());
    }

    /**
     * 进入员工管理列表
     */
    @RequiresPermissions("agent:view")
    @RequestMapping("staff/list")
    public String staff(StaffVO staffVO, Model model) {
        model.addAttribute("staff", staffVO);
        return "modules/ttmy/agent/staffList";
    }

    /**
     * 渠道推广员列表(json)
     */
    @RequiresPermissions("agent:view")
    @RequestMapping("staff/list/data")
    @ResponseBody
    public Page<Map> staffList(StaffVO staff, HttpServletRequest request,
        HttpServletResponse response) {
        Page<Map> page = new Page<>(request, response);
        CommonResponse commonResponse = agentService.getAgentStaffList(staff, page);
        if (commonResponse.getSuccess()) {
            Map data = (Map) commonResponse.getData();
            page.setCount((Integer) data.get("totalNum"));
            page.setList((List<Map>) data.get("list"));

            page.addOtherData("rate", data.get("rate"));
            page.addOtherData("channelRate", data.get("channelRate"));
        }
        return page;
    }

    @RequiresPermissions("agent:edit")
    @RequestMapping("staff/delete")
    @ResponseBody
    public String deleteStaff(Long playerId) {
        CommonResponse commonResponse = agentService.deleteAgentStaff(playerId);
        if (commonResponse.getSuccess()) {
            return renderResult(Global.TRUE, "删除成功");
        }
        return renderResult(Global.FALSE, "删除失败：" + commonResponse.getErrMsg());
    }

    @RequiresPermissions("agent:edit")
    @RequestMapping("staff/rate/update")
    @ResponseBody
    public String changeRate(Long channelId, Long playerId, String nickname, Double rate,
        Integer update) {
        CommonResponse commonResponse;
        if (playerId == null) { // 修改总比例
            commonResponse = agentService.changeRate(channelId, rate, update);
        } else { // 单个玩家比例修改
            commonResponse = agentService.changeUserRate(channelId, playerId, nickname, rate,
                update);
        }
        if (commonResponse.getSuccess()) {
            return renderResult(Global.TRUE, "修改成功");
        }
        return renderResult(Global.FALSE, "修改失败：" + commonResponse.getErrMsg());
    }

    // ========================= 提现申请 ==========================

    /**
     * 进入提现申请列表
     */
    @RequiresPermissions("agent:withdraw:view")
    @RequestMapping("withdraw/list")
    public String withdraw(WithdrawVO withdrawVO, Model model) {
        model.addAttribute("withdraw", withdrawVO);
        return "modules/ttmy/agent/withdrawList";
    }

    /**
     * 提现申请列表数据(json)
     */
    @RequiresPermissions("agent:withdraw:view")
    @RequestMapping("withdraw/list/data")
    @ResponseBody
    public Page<Map> withdrawList(WithdrawVO withdraw, HttpServletRequest request,
        HttpServletResponse response) {
        Page<Map> page = new Page<>(request, response);
        CommonResponse commonResponse = agentService.getWithdrawList(withdraw,
            page);//agentService.getAgentList(agent, page);
        if (commonResponse.getSuccess()) {
            Map data = (Map) commonResponse.getData();
            page.setCount((Integer) data.get("totalNum"));
            page.setList((List<Map>) data.get("list"));

            page.addOtherData("time", data.get("time")); // 结算周期
        }
        return page;
    }

    /**
     * 更新提现申请的结算周期
     */
    @RequiresPermissions("agent:withdraw:edit")
    @RequestMapping("withdraw/time/update")
    @ResponseBody
    public String withdrawTimeUpdate(@RequestParam("time") String timeStr) {
        try {
            int time = Integer.parseInt(timeStr);
            CommonResponse commonResponse = agentService.updateWithdrawTime(time);
            if (commonResponse.getSuccess()) {
                return renderResult(Global.TRUE, "修改成功");
            }
            return renderResult(Global.FALSE, "修改失败：" + commonResponse.getErrMsg());
        } catch (NumberFormatException e) {
            return renderResult(Global.FALSE, "请输入数字");
        }
    }

    /**
     * 更新提现申请的提现状态
     */
    @RequiresPermissions("agent:withdraw:edit")
    @RequestMapping("withdraw/state/update")
    @ResponseBody
    public String withdrawStateUpdate(Long id, Integer state) {
        String creator = UserUtils.getUser().getUserName();
        CommonResponse commonResponse = agentService.updateWithdrawState(id, state, creator);
        if (commonResponse.getSuccess()) {
            return renderResult(Global.TRUE, "状态修改成功");
        }
        return renderResult(Global.FALSE, "状态修改失败：" + commonResponse.getErrMsg());
    }

    // ========================= 财务明细 ==========================

    /**
     * 财务明细界面
     */
    @RequiresPermissions("agent:money:view")
    @RequestMapping("money/list")
    public String money(MoneyVO moneyVO, Model model) {
        model.addAttribute("money", moneyVO);
        return "modules/ttmy/agent/moneyList";
    }

    /**
     * 财务明细列表数据(json)
     */
    @RequiresPermissions("agent:money:view")
    @RequestMapping("money/list/data")
    @ResponseBody
    public Page<Map> moneyList(MoneyVO money, HttpServletRequest request,
        HttpServletResponse response) {
        Page<Map> page = new Page<>(request, response);
        CommonResponse commonResponse = agentService.getMoneyList(money, page);
        if (commonResponse.getSuccess()) {
            Map data = (Map) commonResponse.getData();
            page.setCount((Integer) data.get("totalNum"));
            page.setList((List<Map>) data.get("list"));

            page.addOtherData("dailyNewCount", data.get("dailyNewCount"));
            page.addOtherData("dailyTotalMoney", data.get("dailyTotalMoney"));
        }
        return page;
    }

    // ========================= 代理贡献明细 ==========================

    /**
     * 贡献明细列表
     */
    @RequiresPermissions("agent:view")
    @RequestMapping("commission")
    public String commission(CommissionVO commission, Model model) {
        model.addAttribute("commission", commission);
        return "modules/ttmy/agent/commissionList";
    }

    /**
     * 获取贡献明细列表数据
     */
    @RequiresPermissions("agent:view")
    @RequestMapping("commission/list")
    @ResponseBody
    public Page<Map> commissionList(CommissionVO commission, HttpServletRequest request,
        HttpServletResponse response) {
        Page<Map> page = new Page<>(request, response);
        CommonResponse commonResponse = agentService.getCommissionList(commission, page);
        if (commonResponse.getSuccess()) {
            Map data = (Map) commonResponse.getData();
            page.setCount((Integer) data.get("totalNum"));
            page.setList((List<Map>) data.get("list"));
        }
        return page;
    }

    // ========================= 代理兑换明细 ==========================

    /**
     * 兑换明细列表
     */
    @RequiresPermissions("agent:view")
    @RequestMapping("commissionExchange")
    public String commissionExchange(CommissionExchangeVO commissionExchange, Model model) {
        model.addAttribute("commissionExchange", commissionExchange);
        return "modules/ttmy/agent/commissionExchangeList";
    }

    /**
     * 获取兑换明细列表数据
     */
    @RequiresPermissions("agent:view")
    @RequestMapping("commissionExchange/list")
    @ResponseBody
    public Page<Map> commissionExchangeList(CommissionExchangeVO commissionExchange,
        HttpServletRequest request, HttpServletResponse response) {
        Page<Map> page = new Page<>(request, response);
        CommonResponse commonResponse = agentService.getCommissionExchangeList(commissionExchange,
            page);
        if (commonResponse.getSuccess()) {
            Map data = (Map) commonResponse.getData();
            page.setCount((Integer) data.get("totalNum"));
            page.setList((List<Map>) data.get("list"));
        }
        return page;
    }

    @RequiresPermissions("agent:agentPlayerAll:view")
    @RequestMapping("agentPlayerAll/list")
    public String agentPlayerAll(AgentPlayerAllVO agentPlayerAllVO, Model model) {
        model.addAttribute("agentPlayerAllVO", agentPlayerAllVO);
        return "modules/ttmy/agent/agentPlayerAll";
    }

    @RequiresPermissions("agent:agentPlayerAll:view")
    @RequestMapping("agentPlayerAll/list/data")
    @ResponseBody
    public Page<Map> agentPlayerAllList(AgentPlayerAllVO agentPlayerAllVO,
        HttpServletRequest request, HttpServletResponse response) {
        Page<Map> page = new Page<>(request, response);
        CommonResponse commonResponse = agentService.getAgentPlayerAllList(agentPlayerAllVO, page);
        if (commonResponse.getSuccess()) {
            Map data = (Map) commonResponse.getData();
            page.setCount((Integer) data.get("totalNum"));
            page.setList((List<Map>) data.get("list"));
        }
        return page;
    }
}
