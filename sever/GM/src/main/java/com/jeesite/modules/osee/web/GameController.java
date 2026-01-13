package com.jeesite.modules.osee.web;

import com.alibaba.fastjson.JSON;
import com.jeesite.common.config.Global;
import com.jeesite.common.entity.Page;
import com.jeesite.common.lang.DateUtils;
import com.jeesite.common.lang.StringUtils;
import com.jeesite.common.utils.excel.ExcelExport;
import com.jeesite.common.web.BaseController;
import com.jeesite.modules.model.dto.GameKillFishPageDTO;
import com.jeesite.modules.model.dto.TblAgentMailPageDTO;
import com.jeesite.modules.osee.service.GameService;
import com.jeesite.modules.osee.vo.CommonResponse;
import com.jeesite.modules.osee.vo.game.*;
import com.jeesite.modules.util.CommonUtils;
import com.jeesite.modules.util.DictUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 游戏基础信息控制类
 *
 * @author zjl
 */
@Controller
@RequestMapping("${adminPath}/osee/game")
@SuppressWarnings("unchecked")
@Slf4j
public class GameController extends BaseController {

    @Autowired
    private GameService gameService;

    @RequestMapping("/osee/getPumpingRatio")
    @ResponseBody
    public String getPumpingRatio(@RequestBody Map<String, Object> paramMap) {

        CommonResponse commonResponse = gameService.getPumpingRatio(paramMap);
        return renderResult(Global.TRUE, "", commonResponse);

    }


    @RequestMapping("/osee/setPumpingRatio")
    @ResponseBody
    public String setPumpingRatio(@RequestBody Map<String, Object> paramMap) {

        CommonResponse commonResponse = gameService.setPumpingRatio(paramMap);

        if (commonResponse.getSuccess()) {
            return renderResult(Global.TRUE, "操作成功");
        }

        return renderResult(Global.FALSE, commonResponse.getErrMsg());

    }

    /**
     * 击杀boss列表
     */
    @RequestMapping("killBossList")
    public String killBoss(KillBossVO killBossVO, Model model) {

        model.addAttribute("killBoss", killBossVO);

        model.addAttribute("gameStateDictList", DictUtil.getGameStateDictList(false));

        return "modules/osee/game/killBoss";

    }

    /**
     * 击杀boss信息列表(json)
     */
    @RequestMapping("killBossList/data")
    @ResponseBody
    public Page<Map> turntableList(KillBossVO killBossVO, HttpServletRequest request,
                                   HttpServletResponse response) {
        Page<Map> page = new Page<>(request, response);
        CommonResponse commonResponse = gameService.getKillBossList(killBossVO, page);
        if (commonResponse.getSuccess()) {
            Map data = (Map) commonResponse.getData();
            page.setCount((Integer) data.get("totalNum"));
            page.setList((List<Map>) data.get("list"));
        }
        return page;
    }

    // ========================= 轮盘概率 ==========================

    /**
     * 轮盘概率
     */
    @RequiresPermissions({"game:lottery:view"})
    @GetMapping("lottery")
    public String lottery(Model model) {
        CommonResponse commonResponse = gameService.getLotteryProbability();
        if (commonResponse.getSuccess()) {
            model.addAttribute("lottery",
                    JSON.parseObject(JSON.toJSONString(commonResponse.getData()), LotteryVO.class));
        }
        return "modules/osee/game/lotteryNew";
    }

    /**
     * 获取：轮盘概率数据
     */
    @RequiresPermissions({"game:lottery:view"})
    @GetMapping("lotteryData")
    @ResponseBody
    public String lotteryData() {
        CommonResponse commonResponse = gameService.getLotteryProbability();
        return renderResult(Global.TRUE, "", commonResponse.getData());
    }

    /**
     * 更改轮盘概率
     */
    @RequiresPermissions({"game:lottery:edit"})
    @PostMapping("lottery/update")
    @ResponseBody
    public String lotteryUpdate(@RequestBody LotteryVO lottery) {
        if (!lottery.checkSum100()) {
            return renderResult(Global.FALSE, "总概率不为100，请重新输入！");
        }
        CommonResponse commonResponse = gameService.setLotteryProbability(lottery);
        if (commonResponse.getSuccess()) {
            return renderResult(Global.TRUE, "保存成功！");
        }
        return renderResult(Global.FALSE, "保存失败：" + commonResponse.getErrMsg());
    }

    // ========================= 游走字幕 ==========================

    /**
     * 游走字幕
     */
    @RequiresPermissions("game:subtitle:view")
    @GetMapping("subtitle")
    public String subtitle(SubtitleVO subtitle, Model model) {
        model.addAttribute("subtitle", subtitle);
        return "modules/osee/game/subtitle";
    }

    /**
     * 游走字幕列表
     */
    @RequiresPermissions("game:subtitle:view")
    @PostMapping("subtitle/list")
    @ResponseBody
    public Page<Map> subtitleList(SubtitleVO subtitle, HttpServletRequest request,
                                  HttpServletResponse response) {
        Page<Map> page = new Page<>(request, response);
        CommonResponse commonResponse = gameService.getSubtitleList(subtitle, page);
        if (commonResponse.getSuccess()) {
            Map data = (Map) commonResponse.getData();
            page.setCount((Integer) data.get("totalNum"));
            page.setList((List<Map>) data.get("list"));
        }
        return page;
    }

    /**
     * 游走字幕表单
     */
    @RequiresPermissions("game:subtitle:edit")
    @RequestMapping("subtitle/form")
    public String subtitleForm(SubtitleVO subtitle, Model model) {
        if (subtitle.getId() != null) {
            CommonResponse commonResponse = gameService.getSubtitleById(subtitle.getId());
            if (commonResponse.getSuccess()) {
                subtitle = JSON.parseObject(JSON.toJSONString(commonResponse.getData()),
                        SubtitleVO.class);
            }
        }
        model.addAttribute("subtitle", subtitle);
        return "modules/osee/game/subtitleForm";
    }

    /**
     * 保存游走字幕
     */
    @RequiresPermissions("game:subtitle:edit")
    @PostMapping("subtitle/save")
    @ResponseBody
    public String subtitleSave(SubtitleVO subtitle) {
        int i = subtitle.getEffectiveTime().compareTo(subtitle.getFailureTime());
        if (i >= 0) {
            return renderResult(Global.FALSE, "生效时间应该小于失效时间！");
        }
        CommonResponse commonResponse;
        if (subtitle.getId() == null) {
            commonResponse = gameService.saveSubtitle(subtitle);
            if (commonResponse.getSuccess()) {
                return renderResult(Global.TRUE, "保存成功！");
            }
        } else { // ID不为空就是更新数据
            commonResponse = gameService.updateSubtitle(subtitle);
            if (commonResponse.getSuccess()) {
                return renderResult(Global.TRUE, "修改成功！");
            }
        }
        return renderResult(Global.FALSE, "保存失败：" + commonResponse.getErrMsg());
    }

    /**
     * 删除游走字幕
     */
    @RequiresPermissions("game:subtitle:edit")
    @RequestMapping("subtitle/delete")
    @ResponseBody
    public String subtitleDelete(Long id) {
        CommonResponse commonResponse = gameService.deleteSubtitle(id);
        if (commonResponse.getSuccess()) {
            return renderResult(Global.TRUE, "删除成功！");
        }
        return renderResult(Global.FALSE, "删除失败：" + commonResponse.getErrMsg());
    }

    // ========================= 游戏公告 ==========================

    /**
     * 游戏公告
     */
    @RequiresPermissions("game:notice:view")
    @RequestMapping("notice")
    public String notice(NoticeVO notice, Model model) {
        model.addAttribute("notice", notice);
        return "modules/osee/game/notice";
    }

    /**
     * 游戏公告列表(list)
     */
    @RequiresPermissions("game:notice:view")
    @RequestMapping("notice/list")
    @ResponseBody
    public Page<Map> noticeList(NoticeVO notice, HttpServletRequest request,
                                HttpServletResponse response) {
        Page<Map> page = new Page<>(request, response);
        // 本页面不分页
        CommonResponse commonResponse = gameService.getNoticeList(notice, page);
        if (commonResponse.getSuccess()) {
            Map data = (Map) commonResponse.getData();
            page.setList((List<Map>) data.get("list"));
            // 本页面不分页
            page.setCount(page.getList().size());
            page.setPageSize(page.getList().size());
        }
        return page;
    }

    /**
     * 反馈列表
     */
    @RequiresPermissions("game:feedBack:view")
    @RequestMapping("feedBack")
    public String feedBack(FeedBackVO feedBackVO, Model model) {
        model.addAttribute("feedBack", feedBackVO);
        return "modules/osee/game/feedBack";
    }

    /**
     * 反馈列表(list)
     */
    @RequiresPermissions("game:feedBack:view")
    @RequestMapping("feedBack/list")
    @ResponseBody
    public Page<Map> feedBackList(FeedBackVO feedBackVO, HttpServletRequest request,
                                  HttpServletResponse response) {
        Page<Map> page = new Page<>(request, response);
        CommonResponse commonResponse = gameService.getFeedBack(feedBackVO, page);
        if (commonResponse.getSuccess()) {
            Map data = (Map) commonResponse.getData();
            page.setCount((Integer) data.get("totalNum"));
            page.setList((List<Map>) data.get("list"));
        }
        return page;
    }

    /**
     * 删除反馈列表
     */
    @RequiresPermissions("game:feedBack:view")
    @RequestMapping("feedBack/delete")
    @ResponseBody
    public String deleteFeedBack(Long id) {

        CommonResponse commonResponse = gameService.deleteFeedBack(id);
        if (commonResponse.getSuccess()) {
            return renderResult(Global.TRUE, "删除成功！");
        }
        return renderResult(Global.FALSE, "删除失败：" + commonResponse.getErrMsg());

    }

    /**
     * 游戏公告表单
     */
    @RequiresPermissions("game:notice:edit")
    @RequestMapping("notice/form")
    public String noticeForm(NoticeVO notice, Model model) {
        if (notice.getId() != null) {
            CommonResponse commonResponse = gameService.getNoticeById(notice.getId());
            if (commonResponse.getSuccess()) {
                notice = JSON.parseObject(JSON.toJSONString(commonResponse.getData()),
                        NoticeVO.class);
            }
        }
        model.addAttribute("notice", notice);
        return "modules/osee/game/noticeForm";
    }

    /**
     * 保存游戏公告
     */
    @RequiresPermissions("game:notice:edit")
    @PostMapping("notice/save")
    @ResponseBody
    public String noticeSave(NoticeVO notice) {
        int i = notice.getStartTime().compareTo(notice.getEndTime());
        if (i >= 0) {
            return renderResult(Global.FALSE, "生效时间应该小于失效时间！");
        }
        CommonResponse commonResponse;
        if (notice.getId() == null) {
            commonResponse = gameService.saveNotice(notice);
            if (commonResponse.getSuccess()) {
                return renderResult(Global.TRUE, "保存成功！");
            }
        } else { // ID不为空就是更新公告数据
            commonResponse = gameService.updateNotice(notice);
            if (commonResponse.getSuccess()) {
                return renderResult(Global.TRUE, "修改成功！");
            }
        }
        return renderResult(Global.FALSE, "保存失败：" + commonResponse.getErrMsg());
    }

    /**
     * 删除游戏公告
     */
    @RequiresPermissions("game:notice:edit")
    @RequestMapping("notice/delete")
    @ResponseBody
    public String noticeDelete(Long id) {
        CommonResponse commonResponse = gameService.deleteNotice(id);
        if (commonResponse.getSuccess()) {
            return renderResult(Global.TRUE, "删除成功！");
        }
        return renderResult(Global.FALSE, "删除失败：" + commonResponse.getErrMsg());
    }

    /**
     * 更改游戏公告顺序
     */
    @RequiresPermissions("game:notice:edit")
    @RequestMapping("notice/changeIndex")
    @ResponseBody
    public String noticeChangeIndex(Long id, Integer type) {

        CommonResponse commonResponse = gameService.changeNoticeIndex(id, type);

        if (commonResponse.getSuccess()) {
            return renderResult(Global.TRUE, type == -1 ? "上移成功！" : "下移成功！");
        }

        return renderResult(Global.FALSE,
                (type == -1 ? "上移失败：" : "下移失败：") + commonResponse.getErrMsg());

    }

    // ========================= 游戏版本信息 ==========================

    /**
     * 游戏版本号
     */
    @RequiresPermissions({"game:version:view"})
    @GetMapping("version")
    public String version(Model model) {
        CommonResponse commonResponse = gameService.getGameVersion();
        if (commonResponse.getSuccess()) {
            Map data = (Map) commonResponse.getData();
            model.addAttribute("version", data.get("version"));
        }
        return "modules/osee/game/version";
    }

    /**
     * 更改游戏版本
     */
    @RequiresPermissions({"game:version:edit"})
    @PostMapping("version/update")
    @ResponseBody
    public String versionUpdate(String version) {
        CommonResponse commonResponse = gameService.setGameVersion(version);
        if (commonResponse.getSuccess()) {
            return renderResult(Global.TRUE, "版本号修改成功！");
        }
        return renderResult(Global.FALSE, "版本号修改失败：" + commonResponse.getErrMsg());
    }

    // ========================= CDK ==========================

    /**
     * cdk
     */
    @RequiresPermissions({"game:cdk:view"})
    @RequestMapping("cdk")
    public String cdk(CdkVO cdk, Model model) {
        CommonResponse commonResponse = gameService.getCdkTypeList();
        if (commonResponse.getSuccess()) {
            Map data = (Map) commonResponse.getData();
            List<Map> cdkTypeList = (List<Map>) data.get("list");
            model.addAttribute("cdkTypeList", cdkTypeList);
        } else {
            model.addAttribute("cdkTypeList", new ArrayList<>());
        }
        model.addAttribute("cdk", cdk);
        return "modules/osee/game/cdk";
    }

    /**
     * cdk列表
     */
    @RequiresPermissions("game:cdk:view")
    @PostMapping("cdk/list")
    @ResponseBody
    public Page<Map> cdkList(CdkVO cdk, HttpServletRequest request, HttpServletResponse response) {
        Page<Map> page = new Page<>(request, response);
        CommonResponse commonResponse = gameService.getCdkList(cdk, page);
        if (commonResponse.getSuccess()) {
            Map data = (Map) commonResponse.getData();
            page.setCount((Integer) data.get("totalNum"));
            page.setList((List<Map>) data.get("list"));
        }
        return page;
    }

    /**
     * cdk添加表单
     */
    @RequiresPermissions("game:cdk:edit")
    @RequestMapping("cdk/form")
    public String cdkForm(CdkVO cdk, Model model) {
        CommonResponse commonResponse = gameService.getCdkTypeList();
        if (commonResponse.getSuccess()) {
            Map data = (Map) commonResponse.getData();
            List<Map> cdkTypeList = (List<Map>) data.get("list");
            model.addAttribute("cdkTypeList", cdkTypeList);
        }
        model.addAttribute("cdk", cdk);
        return "modules/osee/game/cdkForm";
    }

    /**
     * 保存cdk
     */
    @RequiresPermissions("game:cdk:edit")
    @PostMapping("cdk/save")
    @ResponseBody
    public String cdkSave(CdkVO cdk) {

        if (cdk.getCount() > 1000) {

            return renderResult(Global.FALSE, "保存失败：cdk数量不能超过1000");

        }

        Integer[] itemId = cdk.getItemId();
        Integer[] itemCount = cdk.getItemCount();

        if (itemId == null || itemCount == null) {

            return renderResult(Global.FALSE, "保存失败：至少添加一份奖励！");

        }

        List<CdkVO.Reward> rewards = new ArrayList<>();

        for (int i = 0; i < itemId.length; i++) {

            if (itemId[i] == null || itemCount[i] == null) {

                return renderResult(Global.FALSE, "保存失败：奖励数据请不要为空！");

            }

            CdkVO.Reward reward = new CdkVO.Reward();
            reward.setItemId(itemId[i]);
            reward.setCount(itemCount[i]);
            rewards.add(reward);

        }

        cdk.setItemId(null);
        cdk.setItemCount(null);
        cdk.setRewards(JSON.toJSONString(rewards));
        CommonResponse commonResponse = gameService.saveCdk(cdk);

        if (commonResponse.getSuccess()) {
            return renderResult(Global.TRUE, "保存成功！");
        }

        return renderResult(Global.FALSE, "保存失败：" + commonResponse.getErrMsg());

    }

    /**
     * 删除cdk
     */
    @RequiresPermissions("game:cdk:edit")
    @PostMapping("cdk/delete/{typeId}")
    @ResponseBody
    public String cdkDelete(@PathVariable("typeId") Long typeId) {
        CommonResponse commonResponse = gameService.deleteCdk(typeId);
        if (commonResponse.getSuccess()) {
            return renderResult(Global.TRUE, "删除成功！");
        }
        return renderResult(Global.FALSE, "删除失败：" + commonResponse.getErrMsg());
    }

    /**
     * 添加cdk类型
     */
    @RequiresPermissions("game:cdk:edit")
    @PostMapping("cdk/type/add")
    @ResponseBody
    public String cdkTypeAdd(CdkTypeVO cdkType) {
        if (StringUtils.isEmpty(cdkType.getName())) {
            return renderResult(Global.FALSE, "添加的CDK类型名为空！");
        }
        CommonResponse commonResponse = gameService.addCdkType(cdkType);
        if (commonResponse.getSuccess()) {
            return renderResult(Global.TRUE, "添加CDK类型 " + cdkType.getName() + " 成功！");
        }
        return renderResult(Global.FALSE, "添加CDK类型失败：" + commonResponse.getErrMsg());
    }

    /**
     * 导出cdk到excel文件
     */
    @RequiresPermissions("game:cdk:view")
    @RequestMapping(value = "cdk/export")
    public void cdkExport(CdkVO cdk, HttpServletResponse response) {
        // page传空即不分页
        CommonResponse commonResponse = gameService.getCdkList(cdk, null);
        if (commonResponse.getSuccess()) {
            Map data = (Map) commonResponse.getData();
            List<Map> list = (List<Map>) data.get("list");
            if (list.size() <= 0) {
                return;
            }
            // 将列表中的map类型转换为想要的类
            List<CdkVO> collect = list.stream()
                    .map(obj -> JSON.parseObject(JSON.toJSONString(obj), CdkVO.class))
                    .collect(Collectors.toList());
            String fileName = "CDKEY数据" + DateUtils.getDate("yyyyMMddHHmmss") + ".xlsx";
            try (ExcelExport ee = new ExcelExport("CDKEY数据", CdkVO.class)) {
                ee.setDataList(collect).write(response, fileName);
            }
        }
    }

    // ========================= 客服信息 ==========================

    @RequiresPermissions("game:support:view")
    @RequestMapping("support")
    public String support(Model model) {
        CommonResponse commonResponse = gameService.getSupportInfo();
        if (commonResponse.getSuccess()) {
            Map data = (Map) commonResponse.getData();
            model.addAttribute("support",
                    JSON.parseObject(JSON.toJSONString(data), SupportVO.class));
        }
        return "modules/osee/game/support";
    }

    @RequiresPermissions("game:support:edit")
    @PostMapping("support/update")
    @ResponseBody
    public String supportUpdate(SupportVO support, HttpServletRequest request) {
        if (!support.getQrcode().startsWith("http")) {
            // 为图片加上完整的访问链接，游戏前端需要访问
            support.setQrcode(CommonUtils.getServerURIFromRequest(request) + support.getQrcode());
        }
        CommonResponse commonResponse = gameService.setSupportInfo(support);
        if (commonResponse.getSuccess()) {
            return renderResult(Global.TRUE, "客服信息更新成功！");
        }
        return renderResult(Global.FALSE, "更新客服信息失败：" + commonResponse.getErrMsg());
    }

    // ========================= 发送邮件 ==========================

    /**
     * 进入邮件页面
     */
    @RequiresPermissions("game:mail:view")
    @RequestMapping("mail")
    public String mail(MailVO mailVO, Model model) {

        mailVO.setMailType("1");

        model.addAttribute("mail", mailVO);

        return "modules/osee/game/mailForm";

    }

    /**
     * 代理邮件记录
     */
    @RequiresPermissions("game:mail:view")
    @RequestMapping("/agent/mail/view")
    public String agentMailView(TblAgentMailPageDTO dto, Model model) {

        if (dto == null) {
            dto = new TblAgentMailPageDTO();
        }

        model.addAttribute("dto", dto);

        return "modules/osee/game/agentMailView";

    }

    /**
     * 获取：代理邮件记录数据
     */
    @RequiresPermissions("money:account:view")
    @RequestMapping("/agent/mail/page")
    @ResponseBody
    public Page<Map> agentMailPage(TblAgentMailPageDTO dto, HttpServletRequest request,
                                   HttpServletResponse response) {

        Page<Map> page = new Page<>(request, response);

        CommonResponse commonResponse = gameService.agentMailPage(dto, page);

        if (commonResponse.getSuccess()) {

            Map data = (Map) commonResponse.getData();
            page.setCount((Integer) data.get("total"));

            List<Map> records = (List<Map>) data.get("records");

            page.setList(records);

        }

        return page;

    }

    /**
     * 发送邮件
     */
    @RequiresPermissions("game:mail:edit")
    @PostMapping("mail/send")
    @ResponseBody
    public String sendMail(MailVO mail) {

        CommonResponse commonResponse;

        if ("1".equals(mail.getMailType())) { // 如果是：游戏邮件

            Integer[] itemIds = mail.getItemId();
            Integer[] itemCounts = mail.getItemCount();
            if (itemIds != null && itemCounts != null) {
                for (int i = 0; i < itemIds.length; i++) {
                    if (itemIds[i] == null || itemCounts[i] == null) {
                        return renderResult(Global.FALSE, "附件输入框请不要留空");
                    }
                }
            }

            commonResponse = gameService.sendMail(mail);

        } else {

            commonResponse = gameService.sendMailToAgent(mail);

        }

        if (commonResponse.getSuccess()) {
            return renderResult(Global.TRUE, "邮件发送成功");
        }

        return renderResult(Global.FALSE, "邮件发送失败：" + commonResponse.getErrMsg());

    }

    @RequiresPermissions("game:killFish:view")
    @RequestMapping("killFishList")
    public String killFishList(GameKillFishPageDTO dto, Model model) {

        if (dto == null) {
            dto = new GameKillFishPageDTO();
        }

        model.addAttribute("dto", dto);

        return "modules/osee/game/killFishList";
    }

    @RequiresPermissions("money:account:view")
    @RequestMapping("killFishList/page")
    @ResponseBody
    public Page<Map> killFishListPage(GameKillFishPageDTO dto, HttpServletRequest request,
                                      HttpServletResponse response) {
        Page<Map> page = new Page<>(request, response);
        CommonResponse commonResponse = gameService.killFishListPage(dto, page);
        if (commonResponse.getSuccess()) {
            Map data = (Map) commonResponse.getData();
            page.setCount((Integer) data.get("total"));

            List<Map> records = (List<Map>) data.get("records");

            page.setList(records);
        }
        return page;
    }

}
