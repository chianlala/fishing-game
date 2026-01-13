package com.jeesite.modules.api.agent.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jeesite.common.codec.Md5Utils;
import com.jeesite.modules.api.agent.domain.Token;
import com.jeesite.modules.api.agent.vo.*;
import com.jeesite.modules.osee.service.BaseService;
import com.jeesite.modules.osee.vo.CommonResponse;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * 代理绑定API服务层
 *
 * @author Junlong
 */
@Service
public class AgentApiService extends BaseService {

    /**
     * 绑定代理微信回调处理
     */
    public CommonResponse bind(String playerId) {
        JSONObject object = new JSONObject();
        object.put("playerId", playerId);
        return restTemplate.postForObject(apiConfig.buildApiUrl("/ttmy/agent/bind"),
            object.toJSONString(), CommonResponse.class);
    }

    /**
     * 绑定代理微信回调处理
     */
    public CommonResponse bindCallback(String code, String state) {
        JSONObject object = new JSONObject();
        object.put("code", code);
        object.put("state", state);
        return restTemplate.postForObject(apiConfig.buildApiUrl("/ttmy/agent/bind/callback"),
            object.toJSONString(), CommonResponse.class);
    }

    // ====================== 渠道商后台 ===========================

    /**
     * 从session中取出玩家登录令牌信息
     */
    public Token getTokenFromRequest(HttpServletRequest request) {
        HttpSession session = request.getSession();
        return (Token) session.getAttribute("_token");
    }

    /**
     * 往请求参数内添加token信息
     */
    public void setRequestToken(BaseVO baseVO, HttpServletRequest request) {
        Token token = getTokenFromRequest(request);
        if (token != null) {
            baseVO.setId(token.getId());
            baseVO.setToken(token.getToken());
        }
    }

    public void setRequestToken(JSONObject jsonObject, HttpServletRequest request) {
        Token token = getTokenFromRequest(request);
        if (token != null) {
            jsonObject.put("id", token.getId());
            jsonObject.put("token", token.getToken());
        }
    }

    // ================ 接口部分 =================

    /**
     * 登录
     */
    public CommonResponse login(String username, String password) {
        JSONObject object = new JSONObject();
        object.put("username", username);
        object.put("password", Md5Utils.md5(password)); // 密码散列了再传递过去
        return restTemplate.postForObject(apiConfig.buildApiUrl("/ttmy/channel/user/login"),
            object.toJSONString(), CommonResponse.class);
    }

    public CommonResponse getSpreadList(SpreadVO spreadVO, HttpServletRequest request) {
        setRequestToken(spreadVO, request);
        return restTemplate.postForObject(apiConfig.buildApiUrl("/ttmy/channel/user/list"),
            JSON.toJSONString(spreadVO), CommonResponse.class);
    }

    public CommonResponse getReportForm(ReportFormVO reportFormVO, HttpServletRequest request) {
        setRequestToken(reportFormVO, request);
        return restTemplate.postForObject(apiConfig.buildApiUrl("/osee/reportForm/list"),
            JSON.toJSONString(reportFormVO), CommonResponse.class);
    }

    public CommonResponse getGiveList(GiveVO giveVO, HttpServletRequest request) {
        setRequestToken(giveVO, request);
        return restTemplate.postForObject(
            apiConfig.buildApiUrl("/ttmy/channel/give/recordByAgentId"), JSON.toJSONString(giveVO),
            CommonResponse.class);
    }

    public CommonResponse getRateForm(RateFormVO rateFormVO, HttpServletRequest request) {
        setRequestToken(rateFormVO, request);
        return restTemplate.postForObject(apiConfig.buildApiUrl("/osee/rateForm/list"),
            JSON.toJSONString(rateFormVO), CommonResponse.class);
    }

    public CommonResponse getUserList(UserVO userVO, HttpServletRequest request) {
        setRequestToken(userVO, request);
        return restTemplate.postForObject(apiConfig.buildApiUrl("/ttmy/channel/staff/list"),
            JSON.toJSONString(userVO), CommonResponse.class);
    }

    public CommonResponse changePassword(String oldPassword, String newPassword,
        HttpServletRequest request) {
        JSONObject object = new JSONObject();
        setRequestToken(object, request);
        object.put("oldPassword", Md5Utils.md5(oldPassword));
        object.put("newPassword", Md5Utils.md5(newPassword));
        return restTemplate.postForObject(apiConfig.buildApiUrl("/ttmy/channel/password/update"),
            object.toJSONString(), CommonResponse.class);
    }

    public CommonResponse changeRate(Double rate, Integer update, HttpServletRequest request) {
        JSONObject object = new JSONObject();
        setRequestToken(object, request);
        object.put("rate", rate);
        object.put("update", update);
        return restTemplate.postForObject(apiConfig.buildApiUrl("/ttmy/channel/total_rate/update"),
            object.toJSONString(), CommonResponse.class);
    }

    public CommonResponse changeUserRate(Long playerId, String nickname, Double rate,
        Integer update, HttpServletRequest request) {
        JSONObject object = new JSONObject();
        setRequestToken(object, request);
        object.put("playerId", playerId);
        object.put("nickname", nickname);
        object.put("rate", rate);
        object.put("update", update);
        return restTemplate.postForObject(apiConfig.buildApiUrl("/ttmy/channel/user_rate/update"),
            object.toJSONString(), CommonResponse.class);
    }

    public CommonResponse deleteUser(Long playerId, HttpServletRequest request) {
        JSONObject object = new JSONObject();
        setRequestToken(object, request);
        object.put("playerId", playerId);
        return restTemplate.postForObject(apiConfig.buildApiUrl("/ttmy/channel/user/delete"),
            object.toJSONString(), CommonResponse.class);
    }

    public CommonResponse getMoneyList(MoneyVO moneyVO, HttpServletRequest request) {
        setRequestToken(moneyVO, request);
        return restTemplate.postForObject(apiConfig.buildApiUrl("/ttmy/channel/my_money/list"),
            JSON.toJSONString(moneyVO), CommonResponse.class);
    }

    public CommonResponse getWithdrawList(WithdrawListVO withdrawListVO,
        HttpServletRequest request) {
        setRequestToken(withdrawListVO, request);
        return restTemplate.postForObject(apiConfig.buildApiUrl("/ttmy/channel/my_withdraw/list"),
            JSON.toJSONString(withdrawListVO), CommonResponse.class);
    }

    public CommonResponse withdraw(WithdrawVO withdrawVO, HttpServletRequest request) {
        setRequestToken(withdrawVO, request);
        return restTemplate.postForObject(apiConfig.buildApiUrl("/ttmy/channel/withdraw"),
            JSON.toJSONString(withdrawVO), CommonResponse.class);
    }

    public CommonResponse changeAddress(AddressVO addressVO, HttpServletRequest request) {
        setRequestToken(addressVO, request);
        return restTemplate.postForObject(
            apiConfig.buildApiUrl("/ttmy/channel/withdraw/address/update"),
            JSON.toJSONString(addressVO), CommonResponse.class);
    }
}
