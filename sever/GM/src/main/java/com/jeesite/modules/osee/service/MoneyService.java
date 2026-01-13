package com.jeesite.modules.osee.service;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import com.jeesite.common.entity.Page;
import com.jeesite.modules.model.bo.GiveHandleMailBO;
import com.jeesite.modules.model.dto.*;
import com.jeesite.modules.osee.vo.CommonResponse;
import com.jeesite.modules.osee.vo.money.*;
import com.jeesite.modules.sys.utils.UserUtils;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * 财务管理模块的服务类
 *
 * @author zjl
 */
@Service
public class MoneyService extends BaseService {

    /**
     * 撤销邮件
     */
    public CommonResponse mailRevoke(MailDTO mail) {
        return restTemplate
                .postForObject(apiConfig.buildApiUrl("/usdt/mail/revoke"), JSON.toJSONString(mail),
                        CommonResponse.class);
    }

    /**
     * 获取邮件记录明细
     */
    public CommonResponse getMailList(MailDTO mail, Page<Map> page) {
        mail.setPageInfo(page);
        return restTemplate
                .postForObject(apiConfig.buildApiUrl("/usdt/mail/list"), JSON.toJSONString(mail),
                        CommonResponse.class);
    }

    /**
     * 获取充值记录明细
     */
    public CommonResponse getRechargeList(RechargeVO recharge, Page<Map> page) {
        recharge.setPageInfo(page);
        return restTemplate.postForObject(apiConfig.buildApiUrl("/osee/recharge/list"),
                JSON.toJSONString(recharge),
                CommonResponse.class);
    }

    /**
     * 获取强化记录明细
     */
    public CommonResponse getForgingList(ForgingVO forgingVO, Page<Map> page) {
        forgingVO.setPageInfo(page);
        return restTemplate.postForObject(apiConfig.buildApiUrl("/osee/forging/list"),
                JSON.toJSONString(forgingVO),
                CommonResponse.class);
    }

    /**
     * 获取扣除记录明细
     */
    public CommonResponse getDeductList(DeductVO deduct, Page<Map> page) {
        deduct.setPageInfo(page);
        return restTemplate
                .postForObject(apiConfig.buildApiUrl("/osee/deduct/list"), JSON.toJSONString(deduct),
                        CommonResponse.class);
    }

    /**
     * 获取玩家账户变动明细数据
     */
    public CommonResponse getAccountList(AccountVO account, Page<Map> page) {
        account.setPageInfo(page);
        return restTemplate.postForObject(apiConfig.buildApiUrl("/osee/player/tenure/log"),
                JSON.toJSONString(account),
                CommonResponse.class);
    }

    /**
     * 获取账户变动原因列表
     */
    public CommonResponse getChangeReasonList() {
        return restTemplate
                .getForObject(apiConfig.buildApiUrl("/osee/player/tenure/change_reason"),
                        CommonResponse.class);
    }

    /**
     * 获取游戏抽水明细列表
     */
    public CommonResponse getEarningList(EarningVO earning, Page<Map> page) {
        earning.setPageInfo(page);
        return restTemplate.postForObject(apiConfig.buildApiUrl("/osee/cut_money/list"),
                JSON.toJSONString(earning),
                CommonResponse.class);
    }

    /**
     * 获取支出明细列表
     */
    public CommonResponse getExpendList(ExpendVO expend, Page<Map> page) {
        expend.setPageInfo(page);
        return restTemplate.postForObject(apiConfig.buildApiUrl("/osee/pay_money/list"),
                JSON.toJSONString(expend),
                CommonResponse.class);
    }

    /**
     * 获取五子棋记录列表
     */
    public CommonResponse getGobangList(GobangVO gobang, Page<Map> page) {
        gobang.setPageInfo(page);
        return restTemplate.postForObject(apiConfig.buildApiUrl("/osee/game/gobang/record"),
                JSON.toJSONString(gobang),
                CommonResponse.class);
    }

    /**
     * 获取水果拉霸记录列表
     */
    public CommonResponse getFruitList(FruitVO fruit, Page<Map> page) {
        fruit.setPageInfo(page);
        return restTemplate.postForObject(apiConfig.buildApiUrl("/osee/game/fruit/record"),
                JSON.toJSONString(fruit),
                CommonResponse.class);
    }

    /**
     * 获取拼十记录列表
     */
    public CommonResponse getFighttenList(FighttenVO fightten, Page<Map> page) {
        fightten.setPageInfo(page);
        return restTemplate
                .postForObject(apiConfig.buildApiUrl("/osee/game/fightten/record"),
                        JSON.toJSONString(fightten),
                        CommonResponse.class);
    }

    /**
     * 获取捕鱼记录列表
     */
    public CommonResponse getFishingList(FishingVO fishing, Page<Map> page) {

        fishing.setPageInfo(page);

        return restTemplate
                .postForObject(apiConfig.buildApiUrl("/osee/game/fishing/record"),
                        JSON.toJSONString(fishing),
                        CommonResponse.class);

    }

    /**
     * 获取礼物赠送记录列表
     */
    public CommonResponse getGiveList(GiveVO give, Page<Map> page) {

        give.setPageInfo(page);

        //        if (give.getStartTime() != null) {
        //            give.setStartTime(DateUtil.offsetHour(give.getStartTime(), -8));
        //        }
        //        if (give.getEndTime() != null) {
        //            give.setEndTime(DateUtil.offsetHour(give.getEndTime(), -8));
        //        }

        return restTemplate.postForObject(apiConfig.buildApiUrl("/ttmy/game/give/record"),
                JSON.toJSONString(give),
                CommonResponse.class);

    }

    /**
     * 获取部落存取记录列表
     */
    public CommonResponse getTribeWareHouseList(TribeWareHouseVO give, Page<Map> page) {
        give.setPageInfo(page);
        return restTemplate
                .postForObject(apiConfig.buildApiUrl("/ttmy/game/tribleWareHouse/record"),
                        JSON.toJSONString(give),
                        CommonResponse.class);
    }

    /**
     * 获取部落列表
     */
    public CommonResponse getTribeList(TribeVO give, Page<Map> page) {
        give.setPageInfo(page);
        return restTemplate.postForObject(apiConfig.buildApiUrl("/ttmy/game/tribe/record"),
                JSON.toJSONString(give),
                CommonResponse.class);
    }

    /**
     * 获取二八杠记录列表
     */
    public CommonResponse getTwoEightList(TwoEightVO twoEight, Page<Map> page) {
        twoEight.setPageInfo(page);
        return restTemplate
                .postForObject(apiConfig.buildApiUrl("/osee/game/twoeight/record"),
                        JSON.toJSONString(twoEight),
                        CommonResponse.class);
    }

    /**
     * 获取龙晶兑换记录列表
     */
    public CommonResponse getCrystalExchangeList(CrystalExchangeVO crystalExchange,
                                                 Page<Map> page) {
        crystalExchange.setPageInfo(page);
        return restTemplate.postForObject(apiConfig.buildApiUrl("/ttmy/money/log/crystal/exchange"),
                JSON.toJSONString(crystalExchange), CommonResponse.class);
    }

    /**
     * 获取变化明细
     */
    public CommonResponse getChangeAllList(ChangeAllVO changeAllVO, Page<Map> page) {
        changeAllVO.setPageInfo(page);
        return restTemplate.postForObject(apiConfig.buildApiUrl("/osee/changeAll/list"),
                JSON.toJSONString(changeAllVO),
                CommonResponse.class);
    }

    /**
     * 获取飞禽走兽游戏记录
     */
    public CommonResponse getAnimalsList(TwoEightVO twoEight, Page<Map> page) {
        twoEight.setPageInfo(page);
        return restTemplate.postForObject(apiConfig.buildApiUrl("/osee/game/animals/record"), JSON.toJSONString(twoEight), CommonResponse.class);
    }


    /**
     * 获取百人拼十游戏记录
     */
    public CommonResponse getBaiRenList(TwoEightVO twoEight, Page<Map> page) {
        twoEight.setPageInfo(page);
        return restTemplate.postForObject(apiConfig.buildApiUrl("/osee/game/bairen/record"), JSON.toJSONString(twoEight), CommonResponse.class);
    }

    /**
     * 获取个控记录
     */
    public CommonResponse userControllerLogPage(UserControllerLogPageDTO dto, Page<Map> page) {

        dto.setByPage(page);

        return restTemplate
                .postForObject(apiConfig.buildApiUrl("/osee/userControllerLog/page"),
                        JSONUtil.toJsonStr(dto),
                        CommonResponse.class);
    }

    /**
     * 赠送记录列表，处理邮件
     */
    public CommonResponse giveHandleMail(GiveHandleMailBO bo) {

        return restTemplate.postForObject(apiConfig.buildApiUrl("/ttmy/game/give/handleMail"),
                JSONUtil.toJsonStr(bo),
                CommonResponse.class);

    }

    /**
     * 获取：手动提现记录
     */
    public CommonResponse manualWithdrawPage(TblManualWithdrawPageDTO dto, Page<Map> page) {

        dto.setByPage(page);

        return restTemplate.postForObject(apiConfig.buildApiUrl("/osee/manualWithdraw/page"),
                JSONUtil.toJsonStr(dto),
                CommonResponse.class);

    }

    /**
     * 处理：手动提现记录，状态
     */
    public CommonResponse handleManualWithdrawStatus(NotNullLong notNullLong, int newStatus) {

        JSONObject jsonObject = JSONUtil.createObj();

        String userName = UserUtils.getUser().getUserName();

        String userCode = UserUtils.getUser().getUserCode();

        jsonObject.set("id", notNullLong.getValue());
        jsonObject.set("updateName", userName);
        jsonObject.set("updateCode", userCode);

        String uri;

        if (newStatus == 2) {

            uri = "/osee/manualWithdraw/success";

        } else {

            uri = "/osee/manualWithdraw/reject";

        }

        return restTemplate.postForObject(apiConfig.buildApiUrl(uri), jsonObject.toString(),
                CommonResponse.class);

    }

    /**
     * 大逃杀历史记录
     */
    public CommonResponse brHistoryPage(BrHistoryPageDTO dto, Page<Map> page) {

        dto.setByPage(page);

        return restTemplate.postForObject(apiConfig.buildApiUrl("/osee/money/brHistory/page"), JSONUtil.toJsonStr(dto),
                CommonResponse.class);

    }

}
