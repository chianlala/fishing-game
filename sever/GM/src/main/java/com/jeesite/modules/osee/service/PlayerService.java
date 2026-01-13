package com.jeesite.modules.osee.service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jeesite.common.codec.Md5Utils;
import com.jeesite.common.entity.Page;
import com.jeesite.modules.model.bo.ActiveConfigBO;
import com.jeesite.modules.model.dto.SetSpecifyFishKillDTO;
import com.jeesite.modules.osee.vo.CommonResponse;
import com.jeesite.modules.osee.vo.player.AuthenticationVO;
import com.jeesite.modules.osee.vo.player.PlayerVO;
import com.jeesite.modules.sys.utils.UserUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 游戏玩家模块服务类
 *
 * @author zjl
 */
@Service
public class PlayerService extends BaseService {

    /**
     * 获取用户列表
     */
    public CommonResponse getPlayerList(PlayerVO player, Page page) {
        player.setPageInfo(page);
        return restTemplate
            .postForObject(apiConfig.buildApiUrl("/osee/player/list"), JSON.toJSONString(player),
                CommonResponse.class);
    }

    /**
     * 通过id获取玩家相信信息
     */
    public CommonResponse getPlayerInfoById(Long playerId) {
        JSONObject object = new JSONObject();
        object.put("playerId", playerId);
        return restTemplate
            .postForObject(apiConfig.buildApiUrl("/osee/player/info"), object.toJSONString(),
                CommonResponse.class);
    }

    /**
     * 获取实名认证列表
     */
    public CommonResponse getAuthenticationList(AuthenticationVO authentication, Page page) {
        authentication.setPageInfo(page);
        return restTemplate
            .postForObject(apiConfig.buildApiUrl("/osee/authentication/list"),
                JSON.toJSONString(authentication),
                CommonResponse.class);
    }

    /**
     * 删除认证信息
     */
    public CommonResponse deleteAuthentication(Long id) {
        JSONObject object = new JSONObject();
        object.put("id", id);
        return restTemplate.postForObject(apiConfig.buildApiUrl("/osee/authentication/delete"),
            object.toJSONString(),
            CommonResponse.class);
    }

    /**
     * 初始化全服用户AP值
     */
    public CommonResponse apInit() {
        return restTemplate.getForObject(apiConfig.buildApiUrl("/osee/fishing_prob/init"),
            CommonResponse.class);
    }

    /**
     * 玩家下线
     */
    public CommonResponse offline(Long[] playerIds) {
        JSONObject object = new JSONObject();
        object.put("list", playerIds);
        return restTemplate
            .postForObject(apiConfig.buildApiUrl("/osee/player/offline"), object.toJSONString(),
                CommonResponse.class);
    }

    /**
     * 冻结/解冻
     */
    public CommonResponse frozen(Long[] playerIds, Integer option) {
        JSONObject object = new JSONObject();
        object.put("list", playerIds);
        object.put("type", option);
        return restTemplate
            .postForObject(apiConfig.buildApiUrl("/osee/player/frozen"), object.toJSONString(),
                CommonResponse.class);
    }

    /**
     * 更新玩家信息
     */
    public CommonResponse updatePlayer(PlayerVO player) {

        if (StrUtil.isNotBlank(player.getPassword())) {
            player.setPassword(player.getPassword());
        }
        if (StrUtil.isNotBlank(player.getBankpassword())) {
            player.setBankpassword(Md5Utils.md5(player.getBankpassword()));
        }

        player.setOperatorName(UserUtils.getUser().getUserName());

        return restTemplate.postForObject(apiConfig.buildApiUrl("/osee/player/update"),
            JSON.toJSONString(player),
            CommonResponse.class);
    }

    /**
     * 玩家注册
     */
    public CommonResponse RegisterPlayer(PlayerVO player) {
        if (!StringUtils.isEmpty(player.getPassword())) {
            player.setPassword(Md5Utils.md5(player.getPassword()));
        }
        return restTemplate.postForObject(apiConfig.buildApiUrl("/osee/player/register"),
            JSON.toJSONString(player),
            CommonResponse.class);
    }

    /**
     * 更改用户账户数据
     */

    public CommonResponse updateTenure(String creator, Long playerId, Integer tenureType,
        Long number) {
        JSONObject object = new JSONObject();
        object.put("playerId", playerId);
        object.put("tenureType", tenureType);
        object.put("number", number);
        object.put("creator", creator);
        return restTemplate.postForObject(apiConfig.buildApiUrl("/osee/player/tenure/update"),
            object.toJSONString(),
            CommonResponse.class);
    }

    /**
     * 获取玩家绑定的代理信息
     */
    public CommonResponse getPlayerAgentInfo(Long playerId) {
        JSONObject object = new JSONObject();
        object.put("playerId", playerId);
        return restTemplate.postForObject(apiConfig.buildApiUrl("/ttmy/player/agent/info"),
            object.toJSONString(),
            CommonResponse.class);
    }

    /**
     * 获取玩家绑定的代理信息
     */
    public CommonResponse getPlayerUserInfo(Long playerId) {
        JSONObject object = new JSONObject();
        object.put("playerId", playerId);
        return restTemplate.postForObject(apiConfig.buildApiUrl("/ttmy/player/user/info"),
            object.toJSONString(),
            CommonResponse.class);
    }

    /**
     * 获取玩家的背包物品信息
     */
    public CommonResponse getPlayerPackageInfo(Long playerId) {
        JSONObject object = new JSONObject();
        object.put("playerId", playerId);
        return restTemplate.postForObject(apiConfig.buildApiUrl("/ttmy/player/package/info"),
            object.toJSONString(),
            CommonResponse.class);
    }

    /**
     * 获取玩家的背包物品信息
     */
    public CommonResponse getPlayerCxInfo(Long playerId) {
        JSONObject object = new JSONObject();
        object.put("playerId", playerId);
        return restTemplate
            .postForObject(apiConfig.buildApiUrl("/ttmy/player/cx/info"), object.toJSONString(),
                CommonResponse.class);
    }

    /**
     * 更改玩家VIP等级
     */
    public CommonResponse changeVipLevel(Long playerId, int vip) {
        JSONObject object = new JSONObject();
        object.put("playerId", playerId);
        object.put("vipLevel", vip);
        return restTemplate.postForObject(apiConfig.buildApiUrl("/ttmy/player/vip/change"),
            object.toJSONString(),
            CommonResponse.class);
    }

    /**
     * 同步 apk
     */
    public CommonResponse syncApk() {

        return restTemplate.postForObject(apiConfig.buildApiUrl("/ttmy/player/syncApk"), null,
            CommonResponse.class);

    }

    /**
     * 活跃榜列表
     */
    public CommonResponse activeConfigPage(ActiveConfigBO bo, Page<Map> page) {

        return restTemplate
            .postForObject(apiConfig.buildApiUrl("/osee/active/config/page"), "{}",
                CommonResponse.class);

    }

    /**
     * 活跃榜配置
     */
    public CommonResponse activeConfigPut(ActiveConfigBO bo) {

        return restTemplate.postForObject(apiConfig.buildApiUrl("/osee/active/config/put"),
            JSONUtil.toJsonStr(bo),
            CommonResponse.class);

    }

    /**
     * 获取：指定鱼种配置
     */
    public CommonResponse getSpecifyFishKill(Long userId) {

        return restTemplate
            .postForObject(apiConfig.buildApiUrl("/osee/player/getSpecifyFishKill"),
                "{\"userId\"=" + userId + "}",
                CommonResponse.class);

    }

    /**
     * 设置：指定鱼种配置
     */
    public CommonResponse setSpecifyFishKill(SetSpecifyFishKillDTO dto) {

        List<cn.hutool.json.JSONObject> specifyFishMapList = new ArrayList<>();

        cn.hutool.json.JSONObject jsonObject = new cn.hutool.json.JSONObject();

        if (CollUtil.isNotEmpty(dto.getFishIdArr())) {

            for (int i = 0; i < dto.getFishIdArr().size(); i++) {

                Long fishId = dto.getFishIdArr().get(i);

                if (fishId == null) {
                    continue;
                }

                Integer fishCount = dto.getFishCountArr().get(i);

                if (fishCount == null) {
                    continue;
                }

                cn.hutool.json.JSONObject specifyFishMap = new cn.hutool.json.JSONObject();

                specifyFishMap.set("fishConfigId", fishId);

                specifyFishMap.set("killCount", fishCount);

                specifyFishMapList.add(specifyFishMap);

            }

        }

        jsonObject.set("specifyFishMapList", specifyFishMapList);

        jsonObject.set("userId", dto.getUserId());

        jsonObject.set("operatorName", UserUtils.getUser().getUserName());

        return restTemplate
            .postForObject(apiConfig.buildApiUrl("/osee/player/setSpecifyFishKill"),
                JSONUtil.toJsonStr(jsonObject),
                CommonResponse.class);

    }

}
