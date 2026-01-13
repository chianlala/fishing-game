package com.jeesite.modules.osee.web;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.DesensitizedUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.jeesite.common.config.Global;
import com.jeesite.common.entity.Page;
import com.jeesite.common.web.BaseController;
import com.jeesite.modules.model.bo.ActiveConfigBO;
import com.jeesite.modules.model.csv.FishCcxxConfig;
import com.jeesite.modules.model.csv.FishConfig;
import com.jeesite.modules.model.dto.SetSpecifyFishKillDTO;
import com.jeesite.modules.osee.service.PlayerService;
import com.jeesite.modules.osee.vo.CommonResponse;
import com.jeesite.modules.osee.vo.player.AuthenticationVO;
import com.jeesite.modules.osee.vo.player.PlayerVO;
import com.jeesite.modules.sys.entity.DictData;
import com.jeesite.modules.sys.utils.DictUtils;
import com.jeesite.modules.sys.utils.UserUtils;
import com.jeesite.modules.util.DictUtil;
import com.jeesite.modules.util.MyCsvUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 游戏用户管理Controller
 *
 * @author zjl
 */
@Controller
@RequestMapping("${adminPath}/osee/player")
@Slf4j
public class PlayerController extends BaseController {

    @Autowired
    private PlayerService playerService;

    /**
     * 同步 apk
     */
    @RequiresPermissions("player:view")
    @RequestMapping("syncApk")
    public String syncApk() {
        CommonResponse commonResponse = playerService.syncApk();
        if (commonResponse.getSuccess()) {
            return renderResult(Global.TRUE, "同步apk成功！");
        }
        return renderResult(Global.FALSE, "同步apk失败：" + commonResponse.getErrMsg());
    }

    // ========================= 玩家用户 ==========================

    /**
     * 玩家列表
     */
    @RequiresPermissions("player:view")
    @RequestMapping("list")
    public String player(PlayerVO player, Model model) {

        model.addAttribute("player", player);

        boolean actionFlag = false;
        boolean editFlag = false;

        if (UserUtils.getAuthInfo().getStringPermissions() != null) {

            actionFlag = UserUtils.getAuthInfo().getStringPermissions().contains("player:action");
            editFlag = UserUtils.getAuthInfo().getStringPermissions().contains("player:edit");

        }

        model.addAttribute("actionFlag", actionFlag);
        model.addAttribute("editFlag", editFlag);

        model.addAttribute("gameStateDictList", DictUtil.getGameStateDictList(true));

        model.addAttribute("gameStateReadonly", false);

        return "modules/osee/player/playerList";

    }

    /**
     * 在线玩家列表
     */
    @RequiresPermissions("player:view")
    @RequestMapping("list/online")
    public String playerOnlineList(PlayerVO player, Model model) {

        model.addAttribute("player", player);

        boolean actionFlag = false;
        boolean editFlag = false;

        if (UserUtils.getAuthInfo().getStringPermissions() != null) {

            actionFlag = UserUtils.getAuthInfo().getStringPermissions().contains("player:action");
            editFlag = UserUtils.getAuthInfo().getStringPermissions().contains("player:edit");

        }

        model.addAttribute("actionFlag", actionFlag);
        model.addAttribute("editFlag", editFlag);

        model.addAttribute("gameStateDictList", DictUtil.getGameStateDictList(true));

        player.setGameState(-1);
        model.addAttribute("gameStateReadonly", true);

        return "modules/osee/player/playerList";

    }

    /**
     * 玩家信息列表(json)
     */
    @RequiresPermissions("player:view")
    @RequestMapping("list/data")
    @ResponseBody
    public Page<Map> playerList(PlayerVO player, HttpServletRequest request,
                                HttpServletResponse response) {

        Page<Map> page = new Page<>(request, response);
        CommonResponse commonResponse = playerService.getPlayerList(player, page);
        if (commonResponse.getSuccess()) {

            Map data = (Map) commonResponse.getData();
            page.setCount((Integer) data.get("totalNum"));
            page.setList((List<Map>) data.get("list"));

        }

        return page;
    }

    /**
     * 根据玩家id获取玩家信息(实物兑换需要)
     */
    @RequiresPermissions("player:view")
    @RequestMapping("info")
    @ResponseBody
    public String playerInfo(PlayerVO player) {
        CommonResponse commonResponse = playerService.getPlayerInfoById(player.getPlayerId());
        if (commonResponse.getSuccess()) {
            Map data = (Map) commonResponse.getData();
            JSONObject object = new JSONObject();
            object.put("nickname", data.get("nickname"));
            object.put("lottery", data.get("lottery"));
            return renderResult(Global.TRUE, "", object.toJSONString());
        }
        return renderResult(Global.FALSE, "玩家信息获取失败：" + commonResponse.getErrMsg());
    }

    /**
     * 初始化全服用户捕鱼AP值
     */
    @RequiresPermissions("player:edit")
    @RequestMapping("apInit")
    @ResponseBody
    public String apInit() {
        CommonResponse commonResponse = playerService.apInit();
        if (commonResponse.getSuccess()) {
            return renderResult(Global.TRUE, "初始全服用户AP成功！");
        }
        return renderResult(Global.FALSE, "初始AP失败：" + commonResponse.getErrMsg());
    }

    /**
     * 玩家操作：冻结，解冻，下线
     *
     * @param playerIds 操作的玩家ID列表
     * @param option    1-冻结 2-解冻 3-下线
     */
    @RequiresPermissions("player:edit")
    @RequestMapping("operate")
    @ResponseBody
    public String operate(@RequestParam(value = "playerIds[]") Long[] playerIds,
                          @RequestParam(value = "option") Integer option) {
        CommonResponse commonResponse;
        if (option == 3) {
            commonResponse = playerService.offline(playerIds);
        } else {
            commonResponse = playerService.frozen(playerIds, option);
        }
        String optionStr = playerIds.length == 0 ? "全员下线"
                : (option == 1 ? "冻结" : (option == 2 ? "解冻" : "下线"));
        if (commonResponse.getSuccess()) {
            return renderResult(Global.TRUE, optionStr + "成功！");
        }
        return renderResult(Global.FALSE, optionStr + "失败：" + commonResponse.getErrMsg());
    }

    @RequiresPermissions("player:edit")
    @RequestMapping("form")
    public String playerInfo(PlayerVO player, Model model) {

        if (player.getPlayerId() != null) {

            CommonResponse commonResponse = playerService.getPlayerInfoById(player.getPlayerId());

            if (commonResponse.getSuccess()) {

                Object data = commonResponse.getData();
                PlayerVO playerVO = BeanUtil.copyProperties(data, PlayerVO.class);

                //                PlayerVO playerVO = JSON.parseObject(JSON.toJSON(data).toString(), PlayerVO.class);
                playerVO.setPlayerId(player.getPlayerId());

                boolean actionFlag = false;

                if (UserUtils.getAuthInfo().getStringPermissions() != null) {

                    actionFlag = UserUtils.getAuthInfo().getStringPermissions()
                            .contains("player:action");

                }

                if (BooleanUtil.isFalse(actionFlag)) {

                    playerVO.setPhone(DesensitizedUtil.mobilePhone(playerVO.getPhone()));

                }

                model.addAttribute("player", playerVO);
                model.addAttribute("playerId", playerVO.getPlayerId());
                model.addAttribute("playerName", playerVO.getNickname());
                model.addAttribute("ag", playerVO.getAg());
                model.addAttribute("one", playerVO.getOne());

                List<Map> openChessCardsList = new ArrayList<>();
                Map<String, Object> map0 = new HashMap<>();
                map0.put("name", "开启");
                map0.put("value", "1");

                Map<String, Object> map1 = new HashMap<>();
                map1.put("name", "不开启");
                map1.put("value", "0");

                openChessCardsList.add(map0);
                openChessCardsList.add(map1);

                model.addAttribute("openChessCardsList", openChessCardsList);

                List<Map> openChessCardsList1 = new ArrayList<>();

                Map<String, Object> map2 = new HashMap<>();
                map2.put("name", "开启");
                map2.put("value", "1");

                Map<String, Object> map3 = new HashMap<>();
                map3.put("name", "不开启");
                map3.put("value", "0");

                openChessCardsList1.add(map2);
                openChessCardsList1.add(map3);

                model.addAttribute("openChessCardsList1", openChessCardsList1);

            }

        } else {

            model.addAttribute("player", player);

        }

        addAttributeForI18nDictList(model, "osee_player_type", "oseePlayerTypeList",
                "用户信息修改.");

        addAttributeForI18nDictList(model, "osee_player_sendGift", "oseePlayerSendGiftList",
                "用户信息修改.");

        addAttributeForI18nDictList(model, "osee_pay_way", "oseePayWayList", "用户信息修改.");

        Map<Long, FishConfig> fishConfigMap = MyCsvUtil.getCsvMap(FishConfig.class);

        Set<cn.hutool.json.JSONObject> fishSet = new HashSet<>();

        Set<Integer> gameIdSet = new HashSet<>();

        Map<Long, FishCcxxConfig> fishCcxxConfigMap = MyCsvUtil.getCsvMap(FishCcxxConfig.class);

        for (Map.Entry<Long, FishCcxxConfig> item : fishCcxxConfigMap.entrySet()) {

            FishCcxxConfig fishCcxxConfig = item.getValue();

            if (fishCcxxConfig.getOpen() == 0) {
                continue;
            }

            gameIdSet.add(fishCcxxConfig.getGameId());

        }

        for (Map.Entry<Long, FishConfig> item : fishConfigMap.entrySet()) {

            if (item.getValue().getFishType2() != 4) {
                continue;
            }

            int scene = item.getValue().getScene();

            if (!gameIdSet.contains(scene)) {
                continue;
            }

            cn.hutool.json.JSONObject jsonObject = new cn.hutool.json.JSONObject();

            jsonObject.set("dictLabel", item.getValue().getName());

            jsonObject.set("dictValue", item.getKey());

            fishSet.add(jsonObject);

        }

        model.addAttribute("fishList", fishSet);

        return "modules/osee/player/playerForm";

    }

    @RequiresPermissions("player:edit")
    @RequestMapping("registerForm")
    public String playerInfo(Model model) {
        return "modules/osee/player/playerRegisterForm";
    }

    @RequiresPermissions("player:edit")
    @RequestMapping("register")
    @ResponseBody
    public String playerRegister(PlayerVO player) {
        final CommonResponse commonResponse = playerService.RegisterPlayer(player);
        if (commonResponse.getSuccess()) {
            return renderResult(Global.TRUE, "保存成功！");
        }
        return renderResult(Global.FALSE, "保存失败：" + commonResponse.getErrMsg());
    }

    @RequiresPermissions("player:edit")
    @RequestMapping("save")
    @ResponseBody
    public String playerSave(PlayerVO player) {

        Integer[] ags = player.getAgs();
        Integer[] ones = player.getOnes();
        Integer sum = 0;

        if (ags != null) {
            for (Integer ag : ags) {
                sum += ag;
            }
        }

        player.setAg(sum);
        sum = 0;
        if (ones != null) {
            for (Integer one : ones) {
                sum += one;
            }
        }

        player.setOne(sum);
        CommonResponse commonResponse;

        if (player.getPlayerId() != null) {
            boolean actionFlag = false;

            if (UserUtils.getAuthInfo().getStringPermissions() != null) {

                actionFlag = UserUtils.getAuthInfo().getStringPermissions()
                        .contains("player:action");

            }

            if (BooleanUtil.isFalse(actionFlag)) {

                player.setPhone(null);

            }

            List<cn.hutool.json.JSONObject> specifyFishMapList = new ArrayList<>();

            if (CollUtil.isNotEmpty(player.getFishIdArr())) {

                for (int i = 0; i < player.getFishIdArr().size(); i++) {

                    Long fishId = player.getFishIdArr().get(i);

                    if (fishId == null) {
                        continue;
                    }

                    Integer fishCount = player.getFishCountArr().get(i);

                    if (fishCount == null) {
                        continue;
                    }

                    cn.hutool.json.JSONObject specifyFishMap = new cn.hutool.json.JSONObject();

                    specifyFishMap.set("fishConfigId", fishId);

                    specifyFishMap.set("killCount", fishCount);

                    specifyFishMapList.add(specifyFishMap);

                }

                player.setSpecifyFishMapList(specifyFishMapList);

            }

            commonResponse = playerService.updatePlayer(player);
            if (commonResponse.getSuccess()) {
                return renderResult(Global.TRUE, "保存成功！");
            }

        } else {

            commonResponse = new CommonResponse("", "后台不能新建玩家！");

        }

        return renderResult(Global.FALSE, "保存失败：" + commonResponse.getErrMsg());

    }

    @RequiresPermissions("player:edit")
    @RequestMapping("tenure/form")
    public String tenureForm(Long playerId, String nickname, Model model) {

        model.addAttribute("playerId", playerId);
        model.addAttribute("nickname", nickname);
        model.addAttribute("creator", UserUtils.getUser().getUserName());

        addAttributeForI18nDictList(model, "osee_player_tenure_operation_type",
                "oseePlayerTenureOperationTypeList",
                "玩家账变.");

        return "modules/osee/player/playerTenureUpdate";

    }

    /**
     * 添加 i18n的字典集合属性
     */
    public static void addAttributeForI18nDictList(Model model, String dictType,
                                                   String attributeName, String preStr) {

        List<DictData> oseePlayerTenureOperationTypeListOrigin = DictUtils.getDictList(dictType);

        List<Object> oseePlayerTenureOperationTypeList =
                new ArrayList<>(oseePlayerTenureOperationTypeListOrigin.size());

        for (DictData item : oseePlayerTenureOperationTypeListOrigin) {

            DictData dictData = BeanUtil.copyProperties(item, DictData.class);

            dictData.setDictLabelOrig(
                    "<span data-i18n='" + preStr + item.getDictLabel() + "'>" + item.getDictLabel()
                            + "</span>");

            oseePlayerTenureOperationTypeList.add(dictData);

        }

        model.addAttribute(attributeName, oseePlayerTenureOperationTypeList);

    }

    @RequiresPermissions("player:edit")
    @RequestMapping("tenure/update")
    @ResponseBody
    public String tenureUpdate(String creator, Long playerId, Integer tenureType, Long number,
                               Integer opType) {
        if (opType == 2) { // 扣除
            number = -number;
        }
        CommonResponse commonResponse = playerService.updateTenure(creator, playerId, tenureType,
                number);
        String opStr = opType == 1 ? "充值" : "扣除";
        if (commonResponse.getSuccess()) {
            return renderResult(Global.TRUE, "账户" + opStr + "成功！");
        }
        return renderResult(Global.FALSE, "账户" + opStr + "失败：" + commonResponse.getErrMsg());
    }

    /**
     * 玩家代理信息查看
     */
    @RequiresPermissions("player:view")
    @RequestMapping("agentInfo")
    @ResponseBody
    public String agentInfo(Long playerId) {
        CommonResponse commonResponse = playerService.getPlayerAgentInfo(playerId);
        if (commonResponse.getSuccess()) {
            return renderResult(Global.TRUE, "", commonResponse.getData());
        }
        return renderResult(Global.FALSE, "代理信息获取失败：" + commonResponse.getErrMsg());
    }

    /**
     * 玩家代理信息查看
     */
    @RequiresPermissions("player:view")
    @RequestMapping("userInfo")
    @ResponseBody
    public String userInfo(Long playerId) {

        CommonResponse commonResponse = playerService.getPlayerUserInfo(playerId);

        if (commonResponse.getSuccess()) {

            boolean actionFlag = false;

            if (UserUtils.getAuthInfo().getStringPermissions() != null) {

                actionFlag = UserUtils.getAuthInfo().getStringPermissions()
                        .contains("player:action");

            }

            if (BooleanUtil.isFalse(actionFlag)) {

                if (commonResponse.getData() instanceof Map) {

                    Map data = (Map) commonResponse.getData();

                    String phone = MapUtil.getStr(data, "third");

                    if (StrUtil.isNotBlank(phone)) {

                        data.put("third", DesensitizedUtil.mobilePhone(phone));

                    }

                }

            }

            return renderResult(Global.TRUE, "", commonResponse.getData());

        }

        return renderResult(Global.FALSE, "绑定身份信息获取失败：" + commonResponse.getErrMsg());

    }

    /**
     * 玩家背包信息查看
     */
    @RequiresPermissions("player:view")
    @RequestMapping("packageInfo")
    @ResponseBody
    public String packageInfo(Long playerId) {
        CommonResponse commonResponse = playerService.getPlayerPackageInfo(playerId);
        if (commonResponse.getSuccess()) {
            return renderResult(Global.TRUE, "", commonResponse.getData());
        }
        return renderResult(Global.FALSE, "背包信息获取失败：" + commonResponse.getErrMsg());
    }

    /**
     * 玩家背包信息查看
     */
    @RequiresPermissions("player:view")
    @RequestMapping("cxInfo")
    @ResponseBody
    public String cxInfo(Long playerId) {
        CommonResponse commonResponse = playerService.getPlayerCxInfo(playerId);
        if (commonResponse.getSuccess()) {
            return renderResult(Global.TRUE, "", commonResponse.getData());
        }
        return renderResult(Global.FALSE, "捕鱼控制信息获取失败：" + commonResponse.getErrMsg());
    }

    /**
     * 更改玩家的vip等级
     */
    @RequiresPermissions("player:edit")
    @RequestMapping("changeVipLevel")
    @ResponseBody
    public String changeVipLevel(Long playerId, String vipLevel) {
        try {
            int vip = Integer.parseInt(vipLevel);
            if (vip < 0 || vip > 9) {
                return renderResult(Global.FALSE, "VIP等级请输入为0-9");
            }
            CommonResponse commonResponse = playerService.changeVipLevel(playerId, vip);
            if (commonResponse.getSuccess()) {
                return renderResult(Global.TRUE, "更改玩家VIP等级成功");
            }
            return renderResult(Global.FALSE, "更改玩家VIP等级失败：" + commonResponse.getErrMsg());
        } catch (NumberFormatException e) {
            return renderResult(Global.FALSE, "请输入数字");
        }
    }

    // ========================= 实名认证 ==========================

    /**
     * 实名认证
     */
    @RequiresPermissions("player:authentication:view")
    @GetMapping("authentication")
    public String authentication(AuthenticationVO authentication, Model model) {
        model.addAttribute("authentication", authentication);
        return "modules/osee/player/authentication";
    }

    /**
     * 实名认证列表(json)
     */
    @RequiresPermissions("player:authentication:view")
    @PostMapping("authentication/list")
    @ResponseBody
    public Page<Map> authenticationList(AuthenticationVO authentication, HttpServletRequest request,
                                        HttpServletResponse response) {
        Page<Map> page = new Page<>(request, response);
        CommonResponse commonResponse = playerService.getAuthenticationList(authentication, page);
        if (commonResponse.getSuccess()) {
            Map data = (Map) commonResponse.getData();
            page.setCount((Integer) data.get("totalNum"));
            page.setList((List<Map>) data.get("list"));
        }
        return page;
    }

    /**
     * 删除实名认证
     */
    @RequiresPermissions("player:authentication:edit")
    @RequestMapping("authentication/delete")
    @ResponseBody
    public String authenticationDelete(Long id) {
        CommonResponse commonResponse = playerService.deleteAuthentication(id);
        if (commonResponse.getSuccess()) {
            return renderResult(Global.TRUE, text("删除成功！"));
        }
        return renderResult(Global.FALSE, text("删除失败：" + commonResponse.getErrMsg()));
    }

    @ResponseBody
    @RequestMapping("grandprix/rank")
    public String playerRank() {
        return "OK";
    }

    /**
     * 活跃榜列表
     */
    @RequiresPermissions("active:view")
    @RequestMapping("/active/config/list")
    public String activeConfigList(ActiveConfigBO bo, Model model) {

        model.addAttribute("bo", bo);
        return "modules/osee/player/activeConfigList";

    }

    /**
     * 活跃榜列表
     */
    @RequiresPermissions("active:view")
    @RequestMapping("/active/config/page")
    @ResponseBody
    public Page<Map> activeConfigPage(ActiveConfigBO bo, HttpServletRequest request,
                                      HttpServletResponse response) {

        Page<Map> page = new Page<>(request, response);

        CommonResponse commonResponse = playerService.activeConfigPage(bo, page);

        if (commonResponse.getSuccess()) {

            Map data = (Map) commonResponse.getData();

            page.setCount((Integer) data.get("total"));

            List<Map> dataList = (List<Map>) data.get("data");

            // 前端分页
            dataList = dataList.stream().skip((page.getPageNo() - 1) * page.getPageSize())
                    .limit(page.getPageSize())
                    .collect(Collectors.toList());

            page.setList(dataList);

        }

        return page;

    }

    /**
     * 活跃榜配置
     */
    @RequiresPermissions("active:edit")
    @RequestMapping("/active/config/edit")
    @ResponseBody
    public String activeConfigPut(ActiveConfigBO bo) {

        CommonResponse commonResponse = playerService.activeConfigPut(bo);

        if (commonResponse.getSuccess()) {
            return renderResult(Global.TRUE, text("操作成功！"));
        }
        return renderResult(Global.FALSE, text("操作失败：" + commonResponse.getErrMsg()));

    }

    /**
     * 获取：指定鱼种配置
     */
    @RequestMapping("/getSpecifyFishKill")
    @ResponseBody
    public String getSpecifyFishKill(SetSpecifyFishKillDTO dto) {

        CommonResponse commonResponse = playerService.getSpecifyFishKill(dto.getUserId());

        return renderResult(Global.TRUE, "", commonResponse);

    }

    /**
     * 设置：指定鱼种配置
     */
    //    @RequestMapping("/setSpecifyFishKill")
    @ResponseBody
    public String setSpecifyFishKill(SetSpecifyFishKillDTO dto) {

        CommonResponse commonResponse = playerService.setSpecifyFishKill(dto);

        if (commonResponse.getSuccess()) {
            return renderResult(Global.TRUE, text("操作成功！"));
        }
        return renderResult(Global.FALSE, text("操作失败：" + commonResponse.getErrMsg()));

    }

}
