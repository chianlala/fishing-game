package com.maple.game.osee.dao.data.entity;

import java.text.SimpleDateFormat;
import java.util.*;

import org.springframework.util.StringUtils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.maple.database.data.DbEntity;
import com.maple.engine.utils.DateUtils;
import com.maple.game.osee.entity.ItemId;

/**
 * 购物数据
 */
public class ShoppingEntity extends DbEntity {

    /**
     * 玩家id
     */
    private long playerId;

    /**
     * 每日礼包
     */
    private String dailyBagInfo;

    /**
     * 特惠礼包
     */
    private String onceBagInfo;

    /**
     * 金币卡
     */
    private boolean moneyCard;

    /**
     * 最后领取时间
     */
    private Date lastReceive;

    public long getPlayerId() {
        return playerId;
    }

    public void setPlayerId(long playerId) {
        this.playerId = playerId;
    }

    public String getDailyBagInfo() {
        return dailyBagInfo;
    }

    public void setDailyBagInfo(String dailyBagInfo) {
        this.dailyBagInfo = dailyBagInfo;
    }

    public String getOnceBagInfo() {
        return onceBagInfo;
    }

    public void setOnceBagInfo(String onceBagInfo) {
        this.onceBagInfo = onceBagInfo;
    }

    public boolean isMoneyCard() {
        return moneyCard;
    }

    public void setMoneyCard(boolean moneyCard) {
        this.moneyCard = moneyCard;
    }

    public Date getLastReceive() {
        if (lastReceive != null) {
            return lastReceive;
        }
        try {
            return new SimpleDateFormat("yyyy-MM-dd").parse("2000-01-01");
        } catch (Exception e) {
            return null;
        }
    }

    public void setLastReceive(Date lastReceive) {
        this.lastReceive = lastReceive;
    }

    /**
     * 获取购买信息
     */
    public BuyInfo getDaily(ItemId itemId) {
        return getDailyBuyInfoMap().getOrDefault(itemId, new BuyInfo());
    }

    /**
     * 现在购买
     */
    public boolean dailyBuyNow(ItemId itemId) {
        BuyInfo buyInfo = getDailyBuyInfoMap().getOrDefault(itemId, new BuyInfo());
        if (DateUtils.isSameDay(buyInfo.lastTime, new Date())) {
            return false;
        }

        if (buyInfo.canContinue()) {
            buyInfo.setDays(buyInfo.getDays() + 1);
        } else {
            buyInfo.setDays(1);
        }
        buyInfo.setLastTime(new Date());
        setDailyBuyInfoMap(itemId, buyInfo);
        return true;
    }

    /**
     * 获取每日礼包信息
     */
    public Map<ItemId, BuyInfo> getDailyBuyInfoMap() {
        if (StringUtils.isEmpty(dailyBagInfo)) {
            return new HashMap<>();
        }
        return new Gson().fromJson(dailyBagInfo, new TypeToken<Map<ItemId, BuyInfo>>() {}.getType());
    }

    /**
     * 更新每日礼包信息
     */
    private void setDailyBuyInfoMap(ItemId itemId, BuyInfo buyInfo) {
        Map<ItemId, BuyInfo> buyInfoMap = getDailyBuyInfoMap();
        buyInfoMap.put(itemId, buyInfo);
        dailyBagInfo = new Gson().toJson(buyInfoMap);
    }

    /**
     * 获取特惠礼包信息
     */
    public Set<ItemId> getOnceBuyInfos() {
        if (StringUtils.isEmpty(onceBagInfo)) {
            return new HashSet<>();
        }
        return new Gson().fromJson(onceBagInfo, new TypeToken<Set<ItemId>>() {}.getType());
    }

    /**
     * 添加特惠礼包信息
     */
    public void addOnceBuyInfoMap(ItemId item) {
        Set<ItemId> buyInfos = getOnceBuyInfos();
        buyInfos.add(item);
        onceBagInfo = new Gson().toJson(buyInfos);
    }

    /**
     * 购买信息
     */
    public static class BuyInfo {

        /**
         * 天数
         */
        private int days;

        /**
         * 最后购买时间
         */
        private Date lastTime;

        public BuyInfo() {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            try {
                lastTime = format.parse("2000-01-01");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        /**
         * 是否能够算连续购买
         */
        public boolean canContinue() {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(getLastTime());
            calendar.add(Calendar.DAY_OF_YEAR, 1);

            return DateUtils.isSameDay(calendar.getTime(), new Date());
        }

        /**
         * 获取购买天数
         */
        public int getDays() {
            if (canContinue() || DateUtils.isSameDay(lastTime, new Date())) {
                return days;
            }
            return 0;
        }

        public void setDays(int days) {
            this.days = days;
        }

        public Date getLastTime() {
            return lastTime;
        }

        public void setLastTime(Date lastTime) {
            this.lastTime = lastTime;
        }
    }
}
