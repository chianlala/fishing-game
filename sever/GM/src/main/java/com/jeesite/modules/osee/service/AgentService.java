package com.jeesite.modules.osee.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jeesite.common.entity.Page;
import com.jeesite.modules.osee.vo.CommonResponse;
import com.jeesite.modules.osee.vo.agent.*;
import com.jeesite.modules.osee.vo.money.AgentPlayerAllVO;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * 代理模块服务类
 *
 * @author Junlong
 */
@Service
public class AgentService extends BaseService {

    /**
     * 获取代理列表
     */
    public CommonResponse getAgentList(AgentVO agent, Page<Map> page) {
        agent.setPageInfo(page);
        return restTemplate.postForObject(apiConfig.buildApiUrl("/ttmy/channel/list"),
            JSON.toJSONString(agent), CommonResponse.class);
    }

    /**
     * 代理玩家冻结/解冻
     *
     * @param option 1-冻结 0-解冻
     */
    public CommonResponse agentOperate(Long[] playerIds, Integer option) {
        JSONObject object = new JSONObject();
        object.put("idList", playerIds);
        object.put("option", option);
        return restTemplate.postForObject(apiConfig.buildApiUrl("/ttmy/agent/frozen"),
            object.toJSONString(), CommonResponse.class);
    }

    /**
     * 获取贡献明细
     */
    public CommonResponse getCommissionList(CommissionVO commission, Page<Map> page) {
        commission.setPageInfo(page);
        return restTemplate.postForObject(apiConfig.buildApiUrl("/ttmy/agent/commission"),
            JSON.toJSONString(commission), CommonResponse.class);
    }

    /**
     * 获取代理的兑换明细
     */
    public CommonResponse getCommissionExchangeList(CommissionExchangeVO commissionExchange,
        Page<Map> page) {
        commissionExchange.setPageInfo(page);
        return restTemplate.postForObject(apiConfig.buildApiUrl("/ttmy/agent/commission/exchange"),
            JSON.toJSONString(commissionExchange), CommonResponse.class);
    }

    /**
     * 删除指定代理的代理身份
     */
    public CommonResponse deleteAgent(Long agentId) {
        JSONObject object = new JSONObject();
        object.put("playerId", agentId);
        return restTemplate.postForObject(apiConfig.buildApiUrl("/ttmy/channel/delete"),
            object.toJSONString(), CommonResponse.class);
    }

    public CommonResponse updateChannelRate(Double rate, Integer update) {
        JSONObject object = new JSONObject();
        object.put("rate", rate);
        object.put("update", update);
        return restTemplate.postForObject(apiConfig.buildApiUrl("/ttmy/channel/rate/update"),
            object.toJSONString(), CommonResponse.class);
    }

    public CommonResponse updateUserRate(Long playerId, double rate) {
        JSONObject object = new JSONObject();
        object.put("playerId", playerId);
        object.put("rate", rate);
        return restTemplate.postForObject(apiConfig.buildApiUrl("/ttmy/channel/update"),
            object.toJSONString(), CommonResponse.class);
    }

    public CommonResponse getAgentStaffList(StaffVO staff, Page<Map> page) {
        staff.setPageInfo(page);
        return restTemplate.postForObject(apiConfig.buildApiUrl("/ttmy/channel/promoter/list"),
            JSON.toJSONString(staff), CommonResponse.class);
    }

    public CommonResponse getWithdrawList(WithdrawVO withdraw, Page<Map> page) {
        withdraw.setPageInfo(page);
        return restTemplate.postForObject(apiConfig.buildApiUrl("/ttmy/channel/withdraw/list"),
            JSON.toJSONString(withdraw), CommonResponse.class);
    }

    public CommonResponse updateWithdrawTime(int time) {
        JSONObject object = new JSONObject();
        object.put("time", time);
        return restTemplate.postForObject(
            apiConfig.buildApiUrl("/ttmy/channel/withdraw/time/update"), object.toJSONString(),
            CommonResponse.class);
    }

    public CommonResponse updateWithdrawState(Long id, Integer state, String creator) {
        JSONObject object = new JSONObject();
        object.put("id", id);
        object.put("state", state);
        object.put("creator", creator);
        return restTemplate.postForObject(
            apiConfig.buildApiUrl("/ttmy/channel/withdraw/state/update"), object.toJSONString(),
            CommonResponse.class);
    }

    public CommonResponse getMoneyList(MoneyVO money, Page<Map> page) {
        money.setPageInfo(page);
        return restTemplate.postForObject(apiConfig.buildApiUrl("/ttmy/channel/money/list"),
            JSON.toJSONString(money), CommonResponse.class);
    }

    public CommonResponse deleteAgentStaff(Long playerId) {
        JSONObject object = new JSONObject();
        object.put("playerId", playerId);
        return restTemplate.postForObject(apiConfig.buildApiUrl("/ttmy/channel/staff/delete"),
            object.toJSONString(), CommonResponse.class);
    }

    public CommonResponse changeRate(Long channelId, Double rate, Integer update) {
        JSONObject object = new JSONObject();
        object.put("channelId", channelId);
        object.put("rate", rate);
        object.put("update", update);
        return restTemplate.postForObject(apiConfig.buildApiUrl("/ttmy/channel/staff/rate/update"),
            object.toJSONString(), CommonResponse.class);
    }

    public CommonResponse changeUserRate(Long channelId, Long playerId, String nickname,
        Double rate, Integer update) {
        JSONObject object = new JSONObject();
        object.put("channelId", channelId);
        object.put("playerId", playerId);
        object.put("nickname", nickname);
        object.put("rate", rate);
        object.put("update", update);
        return restTemplate.postForObject(
            apiConfig.buildApiUrl("/ttmy/channel/staff/user_rate/update"), object.toJSONString(),
            CommonResponse.class);
    }

    /**
     * 获取变化明细
     */
    public CommonResponse getAgentPlayerAllList(AgentPlayerAllVO agentPlayerAllVO, Page<Map> page) {
        agentPlayerAllVO.setPageInfo(page);
        return restTemplate.postForObject(apiConfig.buildApiUrl("/osee/agentPlayerAll/list"),
            JSON.toJSONString(agentPlayerAllVO), CommonResponse.class);
    }
}
