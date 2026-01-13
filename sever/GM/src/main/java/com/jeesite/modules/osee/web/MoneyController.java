package com.jeesite.modules.osee.web;

import cn.hutool.core.map.MapUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.jeesite.common.config.Global;
import com.jeesite.common.entity.Page;
import com.jeesite.common.web.BaseController;
import com.jeesite.modules.model.bo.GiveHandleMailBO;
import com.jeesite.modules.model.dto.*;
import com.jeesite.modules.model.enums.AccountDetailTypeEnum;
import com.jeesite.modules.osee.service.MoneyService;
import com.jeesite.modules.osee.vo.CommonResponse;
import com.jeesite.modules.osee.vo.money.*;
import com.jeesite.modules.util.DictUtil;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 财务管理模块
 *
 * @author zjl
 */
@Controller
@RequestMapping("${adminPath}/osee/money")
public class MoneyController extends BaseController {

    @Autowired
    private MoneyService moneyService;

    // ========================= 邮件 ==========================
    @RequiresPermissions("money:mail:view")
    @RequestMapping("mail")
    public String mail(MailDTO mail, Model model) {
        model.addAttribute("mail", mail);
        return "modules/osee/money/mail";
    }

    @RequiresPermissions("money:mail:view")
    @RequestMapping("mail/list")
    @ResponseBody
    public Page<Map> mailList(MailDTO mail, HttpServletRequest request,
                              HttpServletResponse response) {
        Page<Map> page = new Page<>(request, response);
        CommonResponse commonResponse = moneyService.getMailList(mail, page);
        if (commonResponse.getSuccess()) {
            Map data = (Map) commonResponse.getData();
            page.setCount((Integer) data.get("totalNum"));
            page.setList((List<Map>) data.get("list"));
        }
        return page;
    }

    @RequiresPermissions("money:mail:view")
    @RequestMapping("mail/revoke")
    @ResponseBody
    public String mailRevoke(MailDTO mail, HttpServletRequest request,
                             HttpServletResponse response) {
        CommonResponse commonResponse = moneyService.mailRevoke(mail);
        if (commonResponse.getSuccess()) {
            return renderResult(Global.TRUE, "", commonResponse.getData());
        }
        return renderResult(Global.FALSE, "邮件撤销失败：" + commonResponse.getErrMsg());
    }

    // ========================= 充值明细 ==========================

    @RequiresPermissions("money:recharge:view")
    @RequestMapping("recharge")
    public String recharge(RechargeVO recharge, Model model) {
        model.addAttribute("recharge", recharge);
        return "modules/osee/money/recharge";
    }

    @RequiresPermissions("money:recharge:view")
    @RequestMapping("recharge/list")
    @ResponseBody
    public Page<Map> rechargeList(RechargeVO recharge, HttpServletRequest request,
                                  HttpServletResponse response) {
        Page<Map> page = new Page<>(request, response);
        CommonResponse commonResponse = moneyService.getRechargeList(recharge, page);
        if (commonResponse.getSuccess()) {
            Map data = (Map) commonResponse.getData();
            page.setCount((Integer) data.get("totalNum"));
            page.setList((List<Map>) data.get("list"));
            // 充值总金额数据
            Map<String, Object> otherData = new HashMap<>();
            otherData.put("totalMoney", data.get("totalMoney")); // 总金额
            otherData.put("rechargeTotalDragonCrystal",
                    MapUtil.getLong(data, "rechargeTotalDragonCrystal", 0L) / 10); // 充值龙晶总数
            page.setOtherData(otherData);
        }
        return page;
    }

    // ========================= 强化明细 ==========================

    @RequiresPermissions("money:forging:view")
    @RequestMapping("forging")
    public String forging(ForgingVO forgingVO, Model model) {
        model.addAttribute("forging", forgingVO);
        return "modules/osee/money/forging";
    }

    @RequiresPermissions("money:forging:view")
    @RequestMapping("forging/list")
    @ResponseBody
    public Page<Map> forgingList(ForgingVO forgingVO, HttpServletRequest request,
                                 HttpServletResponse response) {
        Page<Map> page = new Page<>(request, response);
        CommonResponse commonResponse = moneyService.getForgingList(forgingVO, page);
        if (commonResponse.getSuccess()) {
            Map data = (Map) commonResponse.getData();
            page.setCount((Integer) data.get("totalNum"));
            page.setList((List<Map>) data.get("list"));
        }
        return page;
    }

    // ========================= 扣除明细 ==========================

    @RequiresPermissions("money:deduct:view")
    @RequestMapping("deduct")
    public String deduct(DeductVO deduct, Model model) {
        model.addAttribute("deduct", deduct);
        return "modules/osee/money/deduct";
    }

    @RequiresPermissions("money:deduct:view")
    @RequestMapping("deduct/list")
    @ResponseBody
    public Page<Map> deductList(DeductVO deduct, HttpServletRequest request,
                                HttpServletResponse response) {
        Page<Map> page = new Page<>(request, response);
        CommonResponse commonResponse = moneyService.getDeductList(deduct, page);
        if (commonResponse.getSuccess()) {
            Map data = (Map) commonResponse.getData();
            // 扣除总数量数据
            Map<String, Object> otherData = new HashMap<>();
            otherData.put("totalMoney", data.get("totalMoney")); // 总金额
            page.setOtherData(otherData);

            page.setCount((Integer) data.get("totalNum"));
            page.setList((List<Map>) data.get("list"));
        }
        return page;
    }

    // ========================= 账户变动明细 ==========================

    @RequiresPermissions("money:account:view")
    @RequestMapping("account")
    public String account(GameKillFishPageDTO dto, Model model) {

        List<JSONObject> typeMapList = new ArrayList<>();

        for (AccountDetailTypeEnum item : AccountDetailTypeEnum.values()) {

            typeMapList.add(JSONUtil.createObj().set("dictLabel", item.getName())
                    .set("dictValue", item.getValue()));

        }

        dto.setTypeMapList(typeMapList);
        dto.setGameStateMapList(DictUtil.getGameStateDictList(true));

        model.addAttribute("dto", dto);

        return "modules/osee/money/account";

    }

    @RequiresPermissions("money:account:view")
    @RequestMapping("account/list")
    @ResponseBody
    public Page<Map> accountList(AccountVO account, HttpServletRequest request,
                                 HttpServletResponse response) {
        Page<Map> page = new Page<>(request, response);
        CommonResponse commonResponse = moneyService.getAccountList(account, page);
        if (commonResponse.getSuccess()) {
            Map data = (Map) commonResponse.getData();
            page.setCount((Integer) data.get("totalNum"));
            page.setList((List<Map>) data.get("list"));
        }
        return page;
    }

    // ========================= 抽水明细 ==========================

    @RequiresPermissions("money:earning:view")
    @RequestMapping("earning")
    public String earning(EarningVO earning, Model model) {
        model.addAttribute("earning", earning);
        return "modules/osee/money/earning";
    }

    @RequiresPermissions("money:earning:view")
    @RequestMapping("earning/list")
    @ResponseBody
    public Page<Map> earningList(EarningVO earning, HttpServletRequest request,
                                 HttpServletResponse response) {
        Page<Map> page = new Page<>(request, response);
        CommonResponse commonResponse = moneyService.getEarningList(earning, page);
        if (commonResponse.getSuccess()) {
            Map data = (Map) commonResponse.getData();
            page.setCount((Integer) data.get("totalNum"));
            page.setList((List<Map>) data.get("list"));

            Map<String, Object> otherData = new HashMap<>();
            // 总抽水数量
            Object totalCut = data.get("totalCut");
            otherData.put("totalCut", totalCut);
            page.setOtherData(otherData);
        }
        return page;
    }

    // ========================= 支出明细 ==========================

    @RequiresPermissions("money:expend:view")
    @RequestMapping("expend")
    public String expend(ExpendVO expend, Model model) {
        model.addAttribute("expend", expend);
        return "modules/osee/money/expend";
    }

    @RequiresPermissions("money:expend:view")
    @RequestMapping("expend/list")
    @ResponseBody
    public Page<Map> expendList(ExpendVO expend, HttpServletRequest request,
                                HttpServletResponse response) {
        Page<Map> page = new Page<>(request, response);
        CommonResponse commonResponse = moneyService.getExpendList(expend, page);
        if (commonResponse.getSuccess()) {
            Map data = (Map) commonResponse.getData();
            page.setCount((Integer) data.get("totalNum"));
            page.setList((List<Map>) data.get("list"));

            Map<String, Object> otherData = new HashMap<>();
            otherData.put("totalDiamond", data.get("totalDiamond")); // 钻石支出总数
            otherData.put("totalMoney", data.get("totalMoney")); // 金币支出总数
            otherData.put("totalLottery", data.get("totalLottery")); // 奖券支出总数
            page.setOtherData(otherData);
        }
        return page;
    }

    // ========================= 五子棋记录 ==========================

    @RequiresPermissions("money:gobang:view")
    @RequestMapping("gobang")
    public String gobang(GobangVO gobang, Model model) {
        model.addAttribute("gobang", gobang);
        return "modules/osee/money/gobang";
    }

    @RequiresPermissions("money:gobang:view")
    @RequestMapping("gobang/list")
    @ResponseBody
    public Page<Map> gobangList(GobangVO gobang, HttpServletRequest request,
                                HttpServletResponse response) {
        Page<Map> page = new Page<>(request, response);
        CommonResponse commonResponse = moneyService.getGobangList(gobang, page);
        if (commonResponse.getSuccess()) {
            Map data = (Map) commonResponse.getData();
            page.setCount((Integer) data.get("totalNum"));
            page.setList((List<Map>) data.get("list"));

            Map<String, Object> otherData = new HashMap<>();
            otherData.put("totalWin", data.get("totalWin")); // 金币变动总额
            otherData.put("totalLose", data.get("totalLose")); // 下注总额总数
            otherData.put("totalMoney", data.get("totalMoney")); // 金币赢取总额
            page.setOtherData(otherData);
        }
        return page;
    }

    // ========================= 水果拉霸记录 ==========================

    @RequiresPermissions("money:fruit:view")
    @RequestMapping("fruit")
    public String fruit(FruitVO fruit, Model model) {
        model.addAttribute("fruit", fruit);
        return "modules/osee/money/fruit";
    }

    @RequiresPermissions("money:fruit:view")
    @RequestMapping("fruit/list")
    @ResponseBody
    public Page<Map> fruitList(FruitVO fruit, HttpServletRequest request,
                               HttpServletResponse response) {
        Page<Map> page = new Page<>(request, response);
        CommonResponse commonResponse = moneyService.getFruitList(fruit, page);
        if (commonResponse.getSuccess()) {
            Map data = (Map) commonResponse.getData();
            page.setCount((Integer) data.get("totalNum"));
            page.setList((List<Map>) data.get("list"));

            Map<String, Object> otherData = new HashMap<>();
            otherData.put("totalMoney", data.get("totalMoney")); // 金币变动总额
            otherData.put("totalCost", data.get("totalCost")); // 下注总额总数
            otherData.put("AllTotalWin", data.get("AllTotalWin")); // 金币赢取总额
            page.setOtherData(otherData);
        }
        return page;
    }

    // ========================= 拼十记录 ==========================

    @RequiresPermissions("money:fightten:view")
    @RequestMapping("fightten")
    public String fightten(FighttenVO fightten, Model model) {
        model.addAttribute("fightten", fightten);
        return "modules/osee/money/fightten";
    }

    @RequiresPermissions("money:fightten:view")
    @RequestMapping("fightten/list")
    @ResponseBody
    public Page<Map> fighttenList(FighttenVO fightten, HttpServletRequest request,
                                  HttpServletResponse response) {
        Page<Map> page = new Page<>(request, response);
        CommonResponse commonResponse = moneyService.getFighttenList(fightten, page);
        if (commonResponse.getSuccess()) {
            Map data = (Map) commonResponse.getData();
            page.setCount((Integer) data.get("totalNum"));
            page.setList((List<Map>) data.get("list"));

            Map<String, Object> otherData = new HashMap<>();
            otherData.put("totalMoney", data.get("totalMoney")); // 金币变动总额
            page.setOtherData(otherData);
        }
        return page;
    }

    // ========================= 捕鱼记录 ==========================

    @RequiresPermissions("money:fishing:view")
    @RequestMapping("fishing")
    public String fishing(FishingVO fishing, Model model) {

        model.addAttribute("fishing", fishing);

        model.addAttribute("gameStateDictList", DictUtil.getGameStateDictList(true));

        return "modules/osee/money/fishing";

    }

    @RequiresPermissions("money:fishing:view")
    @RequestMapping("fishing/list")
    @ResponseBody
    public Page<Map> fishingList(FishingVO fishing, HttpServletRequest request,
                                 HttpServletResponse response) {

        Page<Map> page = new Page<>(request, response);

        CommonResponse commonResponse = moneyService.getFishingList(fishing, page);

        if (commonResponse.getSuccess()) {

            Map data = (Map) commonResponse.getData();

            page.setCount((Integer) data.get("totalNum"));

            page.setList((List<Map>) data.get("list"));

            Map<String, Object> otherData = new HashMap<>();

            // 赢取金币总数
            otherData.put("totalWin", data.get("totalWin"));

            page.setOtherData(otherData);

        }

        return page;

    }

    // ========================= 二八杠记录 ==========================

    @RequiresPermissions("money:twoEight:view")
    @RequestMapping("twoEight")
    public String twoEight(TwoEightVO twoEight, Model model) {
        model.addAttribute("twoEight", twoEight);
        return "modules/osee/money/twoEight";
    }

    @RequiresPermissions("money:twoEight:view")
    @RequestMapping("twoEight/list")
    @ResponseBody
    public Page<Map> twoEightList(TwoEightVO twoEight, HttpServletRequest request,
                                  HttpServletResponse response) {
        Page<Map> page = new Page<>(request, response);
        CommonResponse commonResponse = moneyService.getTwoEightList(twoEight, page);
        if (commonResponse.getSuccess()) {
            Map data = (Map) commonResponse.getData();
            page.setCount((Integer) data.get("totalNum"));
            page.setList((List<Map>) data.get("list"));

            Map<String, Object> otherData = new HashMap<>();
            otherData.put("totalMoney", data.get("totalMoney")); // 金币变动总额
            page.setOtherData(otherData);
        }
        return page;
    }

    // ========================= 赠送记录 ==========================

    /**
     * 赠送记录
     */
    @RequiresPermissions("money:give:view")
    @RequestMapping("give")
    public String give(GiveVO give, Model model) {
        model.addAttribute("give", give);
        return "modules/osee/money/give";
    }

    /**
     * 赠送记录列表数据
     */
    @RequiresPermissions("money:give:view")
    @RequestMapping("give/list")
    @ResponseBody
    public Page<Map> giveList(GiveVO give, HttpServletRequest request,
                              HttpServletResponse response) {
        Page<Map> page = new Page<>(request, response);
        CommonResponse commonResponse = moneyService.getGiveList(give, page);
        if (commonResponse.getSuccess()) {
            Map data = (Map) commonResponse.getData();
            page.setCount((Integer) data.get("totalNum"));
            page.setList((List<Map>) data.get("list"));

            Map<String, Object> otherData = new HashMap<>();
            // 礼物总数
            otherData.put("giftTotalNum", data.get("giftTotalNum"));
            otherData.put("giftFromTotalNum", data.get("giftFromTotalNum"));
            otherData.put("giftToTotalNum", data.get("giftToTotalNum"));
            page.setOtherData(otherData);
        }
        return page;
    }

    /**
     * 赠送记录列表，处理邮件
     */
    @RequiresPermissions("money:give:view")
    @RequestMapping("give/handleMail")
    @ResponseBody
    public String giveHandleMail(GiveHandleMailBO bo) {

        CommonResponse commonResponse = moneyService.giveHandleMail(bo);

        if (commonResponse.getSuccess()) {
            return renderResult(Global.TRUE, text("操作成功！"));
        }
        return renderResult(Global.FALSE, text("操作失败：" + commonResponse.getErrMsg()));

    }

    /**
     * 赠送记录
     */
    @RequiresPermissions("money:tribe:view")
    @RequestMapping("tribe")
    public String tribe(TribeVO tribe, Model model) {
        model.addAttribute("tribe", tribe);
        return "modules/osee/money/tribe";
    }

    /**
     * 赠送记录列表数据
     */
    @RequiresPermissions("money:tribe:view")
    @RequestMapping("tribe/list")
    @ResponseBody
    public Page<Map> tribeList(TribeVO tribeVO, HttpServletRequest request,
                               HttpServletResponse response) {
        Page<Map> page = new Page<>(request, response);
        CommonResponse commonResponse = moneyService.getTribeList(tribeVO, page);
        if (commonResponse.getSuccess()) {
            Map data = (Map) commonResponse.getData();
            page.setCount((Integer) data.get("totalNum"));
            page.setList((List<Map>) data.get("list"));
            //            Map<String, Object> otherData = new HashMap<>();
            // 礼物总数
            //            otherData.put("giftTotalNum", data.get("giftTotalNum"));
            //            page.setOtherData(otherData);
        }
        return page;
    }

    /**
     * 存取记录
     */
    @RequiresPermissions("money:tribe:view")
    @RequestMapping("tribeWareHouse")
    public String tribeWareHouse(TribeWareHouseVO tribe, Model model) {
        model.addAttribute("tribeWareHouse", tribe);
        return "modules/osee/money/tribeWareHouse";
    }

    /**
     * 存取记录列表数据
     */
    @RequiresPermissions("money:tribe:view")
    @RequestMapping("tribeWareHouse/list")
    @ResponseBody
    public Page<Map> tribeWareHouseList(TribeWareHouseVO tribeVO, HttpServletRequest request,
                                        HttpServletResponse response) {
        Page<Map> page = new Page<>(request, response);
        CommonResponse commonResponse = moneyService.getTribeWareHouseList(tribeVO, page);
        if (commonResponse.getSuccess()) {
            Map data = (Map) commonResponse.getData();
            page.setCount((Integer) data.get("totalNum"));
            page.setList((List<Map>) data.get("list"));
            Map<String, Object> otherData = new HashMap<>();
            // 礼物总数
            otherData.put("inCount", data.get("inCount"));
            otherData.put("outCount", data.get("outCount"));
            page.setOtherData(otherData);
        }
        return page;
    }

    // ========================= 龙晶兑换记录 ==========================

    /**
     * 龙晶兑换记录
     */
    @RequiresPermissions("money:crystal:view")
    @RequestMapping("crystalExchange")
    public String crystalExchange(CrystalExchangeVO crystalExchange, Model model) {
        model.addAttribute("crystalExchange", crystalExchange);
        return "modules/osee/money/crystalExchange";
    }

    /**
     * 龙晶兑换记录列表数据
     */
    @RequiresPermissions("money:crystal:view")
    @RequestMapping("crystalExchange/list")
    @ResponseBody
    public Page<Map> crystalExchangeList(CrystalExchangeVO crystalExchange,
                                         HttpServletRequest request,
                                         HttpServletResponse response) {
        Page<Map> page = new Page<>(request, response);
        CommonResponse commonResponse = moneyService.getCrystalExchangeList(crystalExchange, page);
        if (commonResponse.getSuccess()) {
            Map data = (Map) commonResponse.getData();
            page.setCount((Integer) data.get("totalNum"));
            page.setList((List<Map>) data.get("list"));
        }
        return page;
    }

    @RequiresPermissions("money:changeAll:view")
    @RequestMapping("changeAll")
    public String changeAll(ChangeAllVO changeAllVO, Model model) {
        model.addAttribute("changeAllVO", changeAllVO);
        return "modules/osee/money/changeAll";
    }

    @RequiresPermissions("money:changeAll:view")
    @RequestMapping("changeAll/list")
    @ResponseBody
    public Page<Map> changeAllList(ChangeAllVO changeAllVO, HttpServletRequest request,
                                   HttpServletResponse response) {
        Page<Map> page = new Page<>(request, response);
        CommonResponse commonResponse = moneyService.getChangeAllList(changeAllVO, page);
        if (commonResponse.getSuccess()) {
            Map data = (Map) commonResponse.getData();
            page.setCount((Integer) data.get("totalNum"));
            page.setList((List<Map>) data.get("list"));
        }
        return page;
    }

    // ========================= 飞禽走兽记录 ==========================

    @RequiresPermissions("money:animals:view")
    @RequestMapping("animals")
    public String animals(TwoEightVO twoEight, Model model) {
        model.addAttribute("animals", twoEight);
        return "modules/osee/money/animals";
    }

    @RequiresPermissions("money:animals:view")
    @RequestMapping("animals/list")
    @ResponseBody
    public Page<Map> animalsList(TwoEightVO twoEight, HttpServletRequest request, HttpServletResponse response) {
        Page<Map> page = new Page<>(request, response);
        CommonResponse commonResponse = moneyService.getAnimalsList(twoEight, page);
        if (commonResponse.getSuccess()) {
            Map<String, Object> data = (Map<String, Object>) commonResponse.getData();
            page.setCount(Integer.valueOf("" + data.get("totalNum")));
            page.setList((List<Map>) data.get("list"));

            Map<String, Object> otherData = new HashMap<>();
            otherData.put("totalMoney", data.get("totalMoney")); // 金币变动总额
            page.setOtherData(otherData);
        }
        return page;
    }

    // ========================= 百人拼十记录 ==========================

    @RequiresPermissions("money:bairen:view")
    @RequestMapping("bairen")
    public String bairen(TwoEightVO twoEight, Model model) {
        model.addAttribute("bairen", twoEight);
        return "modules/osee/money/bairen";
    }

    @RequiresPermissions("money:bairen:view")
    @RequestMapping("bairen/list")
    @ResponseBody
    public Page<Map> bairenList(TwoEightVO twoEight, HttpServletRequest request, HttpServletResponse response) {
        Page<Map> page = new Page<>(request, response);
        CommonResponse commonResponse = moneyService.getBaiRenList(twoEight, page);
        if (commonResponse.getSuccess()) {
            Map<String, Object> data = (Map<String, Object>) commonResponse.getData();
            page.setCount(Integer.valueOf("" + data.get("totalNum")));
            page.setList((List<Map>) data.get("list"));

            Map<String, Object> otherData = new HashMap<>();
            otherData.put("totalMoney", data.get("totalMoney")); // 金币变动总额
            page.setOtherData(otherData);
        }
        return page;
    }

    // ========================= 个控记录 ==========================

    @RequiresPermissions("money:userControllerLog:view")
    @RequestMapping("userControllerLog")
    public String prop(UserControllerLogPageDTO dto, Model model) {
        model.addAttribute("dto", dto);
        return "modules/osee/money/userControllerLog";
    }


    @RequestMapping("brHistory")
    public String brHistory(BrHistoryPageDTO dto, Model model) {

        model.addAttribute("dto", dto);

        return "modules/osee/money/brHistory";

    }

    @RequestMapping("brHistory/page")
    @ResponseBody
    public Page<Map> brHistoryPage(BrHistoryPageDTO dto, HttpServletRequest request,
                                   HttpServletResponse response) {

        Page<Map> page = new Page<>(request, response);

        CommonResponse commonResponse = moneyService.brHistoryPage(dto, page);

        if (commonResponse.getSuccess()) {

            Map data = (Map) commonResponse.getData();

            page.setCount((Integer) data.get("total"));

            List<Map> records = (List<Map>) data.get("records");

            page.setList(records);

        }

        return page;

    }

    // ========================= 手动提现记录 ==========================

    @RequiresPermissions("money:manualWithdraw:view")
    @RequestMapping("manualWithdraw")
    public String manualWithdrawHtml(TblManualWithdrawPageDTO dto, Model model) {
        model.addAttribute("dto", dto);
        return "modules/osee/money/manualWithdraw";
    }

    @RequiresPermissions("money:manualWithdraw:view")
    @RequestMapping("manualWithdraw/page")
    @ResponseBody
    public Page<Map> manualWithdrawPage(TblManualWithdrawPageDTO dto, HttpServletRequest request,
                                        HttpServletResponse response) {
        Page<Map> page = new Page<>(request, response);
        CommonResponse commonResponse = moneyService.manualWithdrawPage(dto, page);
        if (commonResponse.getSuccess()) {
            Map data = (Map) commonResponse.getData();
            page.setCount((Integer) data.get("total"));
            List<Map> records = (List<Map>) data.get("records");
            page.setList(records);
        }
        return page;
    }

    /**
     * 通过：手动提现记录
     */
    @RequiresPermissions("money:manualWithdraw:view")
    @RequestMapping("manualWithdraw/success")
    @ResponseBody
    public String manualWithdrawSuccess(@RequestBody @Valid NotNullLong notNullLong) {

        CommonResponse commonResponse = moneyService.handleManualWithdrawStatus(notNullLong, 2);

        if (commonResponse.getSuccess()) {
            return renderResult(Global.TRUE, text("操作成功！"));
        }
        return renderResult(Global.FALSE, text("操作失败：" + commonResponse.getErrMsg()));

    }

    /**
     * 拒绝：手动提现记录
     */
    @RequiresPermissions("money:manualWithdraw:view")
    @RequestMapping("manualWithdraw/reject")
    @ResponseBody
    public String manualWithdrawReject(@RequestBody @Valid NotNullLong notNullLong) {

        CommonResponse commonResponse = moneyService.handleManualWithdrawStatus(notNullLong, 3);

        if (commonResponse.getSuccess()) {
            return renderResult(Global.TRUE, text("操作成功！"));
        }
        return renderResult(Global.FALSE, text("操作失败：" + commonResponse.getErrMsg()));

    }

}
