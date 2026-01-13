package com.jeesite.modules.osee.service;

import cn.hutool.core.convert.Convert;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jeesite.common.config.Global;
import com.jeesite.common.entity.Page;
import com.jeesite.modules.model.bo.BdzConfigBO;
import com.jeesite.modules.model.dto.GameKillFishPageDTO;
import com.jeesite.modules.model.dto.ProfitRatioDTO;
import com.jeesite.modules.model.dto.TblAgentMailPageDTO;
import com.jeesite.modules.model.entity.TblAgentMailDO;
import com.jeesite.modules.osee.dao.GameNoticeDao;
import com.jeesite.modules.osee.domain.GameNoticeEntity;
import com.jeesite.modules.osee.vo.CommonResponse;
import com.jeesite.modules.osee.vo.game.*;
import com.jeesite.modules.sys.utils.UserUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * 游戏模块service
 *
 * @author zjl
 */
@Service
public class GameService extends BaseService {

    /**
     * 获取游戏版本
     */
    public CommonResponse getGameVersion() {
        return restTemplate.getForObject(apiConfig.buildApiUrl("/osee/game_version"),
                CommonResponse.class);
    }

    /**
     * 设置游戏版本
     */
    public CommonResponse setGameVersion(String version) {
        JSONObject object = new JSONObject();
        object.put("version", version);
        return restTemplate.postForObject(apiConfig.buildApiUrl("/osee/game_version/update"),
                object.toJSONString(),
                CommonResponse.class);
    }

    /**
     * 获取游戏轮盘的概率
     */
    public CommonResponse getLotteryProbability() {
        return restTemplate.getForObject(apiConfig.buildApiUrl("/osee/lottery_probability"),
                CommonResponse.class);
    }

    /**
     * 设置轮盘概率
     */
    public CommonResponse setLotteryProbability(LotteryVO lottery) {
        return restTemplate
                .postForObject(apiConfig.buildApiUrl("/osee/lottery_probability/update"),
                        JSON.toJSONString(lottery),
                        CommonResponse.class);
    }

    /**
     * 获取游走字幕列表
     */
    public CommonResponse getSubtitleList(SubtitleVO subtitle, Page page) {
        subtitle.setPageInfo(page);
        return restTemplate
                .postForObject(apiConfig.buildApiUrl("/osee/wander_subtitle/list"),
                        JSON.toJSONString(subtitle),
                        CommonResponse.class);
    }

    /**
     * 根据ID查找游走字幕
     */
    public CommonResponse getSubtitleById(Long id) {
        JSONObject object = new JSONObject();
        object.put("subtitleId", id);
        return restTemplate.postForObject(apiConfig.buildApiUrl("/osee/wander_subtitle/query"),
                object.toJSONString(),
                CommonResponse.class);
    }

    /**
     * 更新游走字幕
     */
    public CommonResponse updateSubtitle(SubtitleVO subtitle) {
        return restTemplate
                .postForObject(apiConfig.buildApiUrl("/osee/wander_subtitle/update"),
                        JSON.toJSONString(subtitle),
                        CommonResponse.class);
    }

    /**
     * 保存游走字幕
     */
    public CommonResponse saveSubtitle(SubtitleVO subtitle) {
        return restTemplate
                .postForObject(apiConfig.buildApiUrl("/osee/wander_subtitle/add"),
                        JSON.toJSONString(subtitle),
                        CommonResponse.class);
    }

    /**
     * 删除游走字幕
     */
    public CommonResponse deleteSubtitle(Long id) {
        JSONObject object = new JSONObject();
        object.put("subtitleId", id);
        return restTemplate.postForObject(apiConfig.buildApiUrl("/osee/wander_subtitle/delete"),
                object.toJSONString(),
                CommonResponse.class);
    }

    /**
     * 获取反馈列表
     */
    public CommonResponse getFeedBack(FeedBackVO subtitle, Page page) {
        subtitle.setPageInfo(page);
        return restTemplate.postForObject(apiConfig.buildApiUrl("/osee/feedBack/list"),
                JSON.toJSONString(subtitle),
                CommonResponse.class);
    }

    /**
     * 删除反馈列表
     */
    public CommonResponse deleteFeedBack(Long id) {

        JSONObject object = new JSONObject();
        object.put("feedBackId", id);

        return restTemplate
                .postForObject(apiConfig.buildApiUrl("/osee/feedBack/delete"), object.toJSONString(),
                        CommonResponse.class);

    }

    @Autowired
    private GameNoticeDao gameNoticeDao;

    /**
     * 获取游戏公告列表
     */
    public CommonResponse getNoticeList(NoticeVO noticeVO, Page page) {

        noticeVO.setPageInfo(page);

        //        List<GameNoticeEntity> notices = gameNoticeDao.getAll();
        //        Map<String, Object> resultMap = new HashMap<>();
        //
        //        List<Map<String, Object>> noticeItemMap;
        //        try {
        //            noticeItemMap = JsonMapUtils.objectsToMaps(notices);
        //        } catch (Exception e) {
        //            e.printStackTrace();
        //            return new CommonResponse("ERROR", "操作异常");
        //        }
        //
        //        long nowTime = System.currentTimeMillis();
        //        for (int i = 0; i < notices.size(); i++) {
        //            int state = 0;
        //            GameNoticeEntity noticeEntity = notices.get(i);
        //            if (noticeEntity.getStartTime().getTime() > nowTime || noticeEntity.getEndTime().getTime() < nowTime) {
        //                state = 1;
        //            }
        //            noticeItemMap.get(i).put("state", state);
        //        }
        //
        //        resultMap.put("list", noticeItemMap);
        //
        //        return new CommonResponse(resultMap);

        return restTemplate.postForObject(apiConfig.buildApiUrl("/osee/notice/list"),
                JSON.toJSONString(noticeVO),
                CommonResponse.class);

    }

    /**
     * 根据ID查找公告
     */
    public CommonResponse getNoticeById(Long id) {

        JSONObject object = new JSONObject();
        object.put("noticeId", id);

        return restTemplate
                .postForObject(apiConfig.buildApiUrl("/osee/notice/query"), object.toJSONString(),
                        CommonResponse.class);

        //        CommonResponse commonResponse = new CommonResponse(true);
        //
        //        GameNoticeEntity notice = gameNoticeDao.getById(id);
        //        if (notice == null) {
        //            commonResponse.setSuccess(false);
        //            commonResponse.setErrMsg("公告不存在");
        //            return commonResponse;
        //        }
        //        Map<String, Object> resultMap = new HashMap<>();
        //        resultMap.put("id", notice.getId());
        //        resultMap.put("title", notice.getTitle());
        //        resultMap.put("content", notice.getContent());
        //        //        resultMap.put("startTime", notice.getStartTime().getTime());
        //        //        resultMap.put("endTime", notice.getEndTime().getTime());
        //        commonResponse.setData(resultMap);
        //        return commonResponse;

    }

    /**
     * 更新游戏公告数据
     */
    public CommonResponse updateNotice(NoticeVO noticeVO) {

        return restTemplate.postForObject(apiConfig.buildApiUrl("/osee/notice/update"),
                JSON.toJSONString(noticeVO),
                CommonResponse.class);

        //        GameNoticeEntity notice = gameNoticeDao.getById(noticeVO.getId());
        //        CommonResponse commonResponse = new CommonResponse(true);
        //        if (notice == null) {
        //            commonResponse.setSuccess(false);
        //            commonResponse.setErrMsg("公告不存在");
        //            return commonResponse;
        //        }
        //        notice.setTitle(noticeVO.getTitle());
        //        notice.setContent(noticeVO.getContent());
        //        notice.setStartTime(noticeVO.getStartTime());
        //        notice.setEndTime(noticeVO.getEndTime());
        //        gameNoticeDao.update(notice);
        //        refreshNotice();
        //        return commonResponse;

    }

    /**
     * 保存游戏公告
     */
    public CommonResponse saveNotice(NoticeVO noticeVO) {

        return restTemplate.postForObject(apiConfig.buildApiUrl("/osee/notice/add"),
                JSON.toJSONString(noticeVO),
                CommonResponse.class);

        //        GameNoticeEntity notice = new GameNoticeEntity();
        //        notice.setIndex(Integer.MAX_VALUE);
        //        notice.setTitle(noticeVO.getTitle());
        //        notice.setContent(noticeVO.getContent());
        //        notice.setStartTime(noticeVO.getStartTime());
        //        notice.setEndTime(noticeVO.getEndTime());
        //
        //        gameNoticeDao.save(notice);
        //
        //        if (notice.getId() <= 0) {
        //            return new CommonResponse(false);
        //        }
        //        refreshNotice();
        //        return new CommonResponse(true);

    }

    /**
     * 删除游戏公告
     */
    public CommonResponse deleteNotice(Long id) {

        JSONObject object = new JSONObject();
        object.put("id", id);
        return restTemplate
                .postForObject(apiConfig.buildApiUrl("/osee/notice/delete"), object.toJSONString(),
                        CommonResponse.class);

        //        gameNoticeDao.deleteById(id);
        //        refreshNotice();
        //        return new CommonResponse(true);
    }

    /**
     * 更改游戏公告顺序
     */
    public CommonResponse changeNoticeIndex(Long id, Integer type) {

        JSONObject object = new JSONObject();
        object.put("id", id);
        object.put("type", type);

        return restTemplate
                .postForObject(apiConfig.buildApiUrl("/osee/notice/change"), object.toJSONString(),
                        CommonResponse.class);

        //        CommonResponse commonResponse = new CommonResponse(true);
        //        if (!changeNotice(id, type)) {
        //            commonResponse.setSuccess(false);
        //            commonResponse.setErrMsg("该项无法继续移动");
        //        }
        //        return commonResponse;

    }

    /**
     * 获取cdk列表
     */
    public CommonResponse getCdkList(CdkVO cdk, Page page) {
        cdk.setPageInfo(page);
        return restTemplate
                .postForObject(apiConfig.buildApiUrl("/osee/cdk/list"), JSON.toJSONString(cdk),
                        CommonResponse.class);
    }

    /**
     * 获取cdk类型列表
     */
    public CommonResponse getCdkTypeList() {
        return restTemplate.getForObject(apiConfig.buildApiUrl("/osee/cdk_type/list"),
                CommonResponse.class);
    }

    /**
     * 添加cdk类型
     */
    public CommonResponse addCdkType(CdkTypeVO cdkType) {
        return restTemplate.postForObject(apiConfig.buildApiUrl("/osee/cdk_type/add"),
                JSON.toJSONString(cdkType),
                CommonResponse.class);
    }

    /**
     * 保存cdk
     */
    public CommonResponse saveCdk(CdkVO cdk) {
        return restTemplate
                .postForObject(apiConfig.buildApiUrl("/osee/cdk/add"), JSON.toJSONString(cdk),
                        CommonResponse.class);
    }

    /**
     * 删除cdk
     */
    public CommonResponse deleteCdk(Long typeId) {
        JSONObject object = new JSONObject();
        object.put("typeId", typeId);
        return restTemplate
                .postForObject(apiConfig.buildApiUrl("/osee/cdk/delete"), object.toJSONString(),
                        CommonResponse.class);
    }

    /**
     * 获取游戏设置信息
     */
    public CommonResponse getGameConfig() {
        return restTemplate.getForObject(apiConfig.buildApiUrl("/osee/game/config/info"),
                CommonResponse.class);
    }

    /**
     * 获取击杀 boss列表
     */
    public CommonResponse getKillBossList(KillBossVO killBossVO, Page page) {
        killBossVO.setPageInfo(page);
        return restTemplate.postForObject(apiConfig.buildApiUrl("/osee/killBoss/list"),
                JSON.toJSONString(killBossVO),
                CommonResponse.class);
    }


    /**
     * 获取机器人配置信息
     */
    public CommonResponse getRobotConfig() {
        return restTemplate.getForObject(apiConfig.buildApiUrl("/osee/game/ai/info"),
                CommonResponse.class);
    }

    /**
     * test
     */
    public CommonResponse test() {
        return restTemplate.getForObject(apiConfig.buildApiUrl("/osee/doDayTask"),
                CommonResponse.class);
    }


    /**
     * 获取游戏客服信息
     */
    public CommonResponse getSupportInfo() {
        return restTemplate.getForObject(apiConfig.buildApiUrl("/osee/support"),
                CommonResponse.class);
    }

    /**
     * 设置游戏客服信息
     */
    public CommonResponse setSupportInfo(SupportVO support) {
        return restTemplate.postForObject(apiConfig.buildApiUrl("/osee/support/update"),
                JSON.toJSONString(support),
                CommonResponse.class);
    }

    /**
     * 发送系统邮件
     */
    public CommonResponse sendMail(MailVO mail) {
        return restTemplate
                .postForObject(apiConfig.buildApiUrl("/ttmy/send_mail"), JSON.toJSONString(mail),
                        CommonResponse.class);
    }

    // ===================== 公告相关处理 =========================

    public GameService() {
        Executors.newSingleThreadScheduledExecutor()
                .schedule(this::refreshNotice, 5, TimeUnit.SECONDS);
    }

    /**
     * 公告列表
     */
    public static List<GameNoticeEntity> notices = new LinkedList<>();

    /**
     * 刷新系统公告列表
     */
    private void refreshNotice() {
        notices = gameNoticeDao.getAll();
        for (int i = 1; i <= notices.size(); i++) {
            if (notices.get(i - 1).getIndex() != i) {
                notices.get(i - 1).setIndex(i);
                gameNoticeDao.update(notices.get(i - 1));
            }
        }
    }


    /**
     * 获取：击杀鱼记录
     */
    public CommonResponse killFishListPage(GameKillFishPageDTO dto, Page<Map> page) {

        dto.setByPage(page);

        return restTemplate.postForObject(apiConfig.buildApiUrl("/osee/killFishList/page"),
                JSONUtil.toJsonStr(dto),
                CommonResponse.class);

    }

    public CommonResponse sendMailToAgent(MailVO mail) {

        String userName = UserUtils.getUser().getUserName();

        TblAgentMailDO tblAgentMailDO = new TblAgentMailDO();
        tblAgentMailDO.setCreateId(-1 * Convert.toLong(UserUtils.getUser().getId(), 1L));
        tblAgentMailDO.setCreateName(userName);
        tblAgentMailDO.setCreateUserType(1);
        tblAgentMailDO.setToUserIds(mail.getToUserIds());
        tblAgentMailDO.setToUserType(1);
        tblAgentMailDO.setPropsId(18);
        tblAgentMailDO.setPropsCount(mail.getPropsCount());

        return restTemplate
                .postForObject(apiConfig.buildApiUrl("/osee/agent/mail/insert"),
                        JSONUtil.toJsonStr(tblAgentMailDO),
                        CommonResponse.class);

    }

    /**
     * 获取：代理邮件记录数据
     */
    public CommonResponse agentMailPage(TblAgentMailPageDTO dto, Page<Map> page) {

        dto.setByPage(page);

        return restTemplate.postForObject(apiConfig.buildApiUrl("/osee/agent/mail/page"),
                JSONUtil.toJsonStr(dto),
                CommonResponse.class);

    }

    /**
     * 大逃杀：当前游戏数据
     */
    public CommonResponse battleRoyaleData() {

        return restTemplate.postForObject(apiConfig.buildApiUrl("/osee/battleRoyale/data"),
                null, CommonResponse.class);

    }

    /**
     * 大逃杀：设置：本次杀手要去的房间
     */
    public CommonResponse battleRoyaleSetKillRoom(Map<String, Object> paramMap) {

        return restTemplate.postForObject(apiConfig.buildApiUrl("/osee/battleRoyale/setKillRoom"),
                JSONUtil.toJsonStr(paramMap), CommonResponse.class);

    }

    /**
     * 飞禽走兽：当前游戏数据
     */
    public CommonResponse birdsAndAnimalsData() {

        return restTemplate.postForObject(apiConfig.buildApiUrl("/osee/birdsAndAnimals/data"),
                null, CommonResponse.class);

    }

    /**
     * 飞禽走兽：设置：本次要命中的元素
     */
    public CommonResponse birdsAndAnimalsSetKillRoom(Map<String, Object> paramMap) {

        return restTemplate.postForObject(apiConfig.buildApiUrl("/osee/birdsAndAnimals/setKillRoom"),
                JSONUtil.toJsonStr(paramMap), CommonResponse.class);

    }

    /**
     * 获取抽水比例
     */
    public CommonResponse getPumpingRatio(Map<String, Object> paramMap) {

        return restTemplate.postForObject(apiConfig.buildApiUrl("/osee/getPumpingRatio"),
                JSONUtil.toJsonStr(paramMap), CommonResponse.class);

    }

    /**
     * 设置抽水比例
     */
    public CommonResponse setPumpingRatio(Map<String, Object> paramMap) {

        return restTemplate.postForObject(apiConfig.buildApiUrl("/osee/setPumpingRatio"),
                JSONUtil.toJsonStr(paramMap), CommonResponse.class);

    }

}
