package com.maple.game.osee.timer.fishing;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import com.maple.game.osee.entity.fishing.csv.file.FishCcxxConfig;
import com.maple.game.osee.model.entity.TblProfitRatioLogDO;
import com.maple.game.osee.service.TblProfitRatioLogService;
import com.maple.game.osee.util.FishingHelper;
import com.maple.game.osee.util.MyRefreshFishingUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Component
public class ProfitRatioLogTask {

    private static TblProfitRatioLogService tblProfitRatioLogService;

    public ProfitRatioLogTask(TblProfitRatioLogService tblProfitRatioLogService) {
        ProfitRatioLogTask.tblProfitRatioLogService = tblProfitRatioLogService;
    }

    /**
     * 添加数据
     */
    @Scheduled(fixedDelay = 5000L)
    public void insertData() {

        List<Long> produceGoldTotalList = new ArrayList<>(); // 每个房间 产出的 金币L
        List<Long> usedXhTotalList = new ArrayList<>(); // 每个房间的 子弹消耗
        List<BigDecimal> profitRatioHopeList = new ArrayList<>(); // 每个房间的 期望收益比
        List<BigDecimal> profitRatioRealList = new ArrayList<>(); // 每个房间的 实际收益比

        List<TblProfitRatioLogDO> insertList = new ArrayList<>();

        Date date = new Date();

        for (int i = 0; i < MyRefreshFishingUtil.CHALLENGE_AND_INTEGRAL_FISHING_CCXX_CONFIG_LIST.size(); i++) {

            FishCcxxConfig fishCcxxConfig = MyRefreshFishingUtil.CHALLENGE_AND_INTEGRAL_FISHING_CCXX_CONFIG_LIST.get(i);

            // 处理：收益比，基础的集合
            FishingHelper.handlerProfitRatioBaseList(produceGoldTotalList, usedXhTotalList, profitRatioHopeList,
                profitRatioRealList, fishCcxxConfig.getSessionId());

            // 积分场的不记录
            if (fishCcxxConfig.getSessionId() > 200 && fishCcxxConfig.getSessionId() < 300) {
                continue;
            }

            TblProfitRatioLogDO tblProfitRatioLogDO = new TblProfitRatioLogDO();
            tblProfitRatioLogDO.setCreateTime(date);
            tblProfitRatioLogDO.setHopeRatio(profitRatioHopeList.get(i));
            tblProfitRatioLogDO.setRealRatio(profitRatioRealList.get(i));
            tblProfitRatioLogDO.setRoomIndex(i);

            insertList.add(tblProfitRatioLogDO);

        }

        tblProfitRatioLogService.saveBatch(insertList); // 批量保存到数据库

    }

    /**
     * 删除数据：7天之前的数据 0 10 0 * * ? 每天的：0点10分
     */
    @Scheduled(cron = "0 10 0 * * ?")
    public void deleteData() {

        DateTime offsetDay = DateUtil.offsetDay(new DateTime(), -7);

        tblProfitRatioLogService.lambdaUpdate().lt(TblProfitRatioLogDO::getCreateTime, offsetDay).remove();

    }

    /**
     * 获取：最近 7天的数据
     */
    @NotNull
    public static List<TblProfitRatioLogDO> getSevenDayData(@Nullable Date beginTime, @Nullable Date endTime) {

        if (beginTime == null) {
            beginTime = DateUtil.offsetHour(new DateTime(), -1);
        }

        List<TblProfitRatioLogDO> tblProfitRatioLogDOList =
            tblProfitRatioLogService.lambdaQuery().ge(TblProfitRatioLogDO::getCreateTime, beginTime)
                .le(endTime != null, TblProfitRatioLogDO::getCreateTime, endTime).list();

        BigDecimal bigDecimal = new BigDecimal(100);

        tblProfitRatioLogDOList.forEach(item -> {
            item.setCreateTimeStr(DateTime.of(item.getCreateTime()).toString());
            item.setHopeRatio(item.getHopeRatio().multiply(bigDecimal));
            item.setRealRatio(item.getRealRatio().multiply(bigDecimal));
        });

        return tblProfitRatioLogDOList;

    }

}
