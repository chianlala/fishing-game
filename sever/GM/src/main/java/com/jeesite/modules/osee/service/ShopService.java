package com.jeesite.modules.osee.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jeesite.common.entity.Page;
import com.jeesite.modules.osee.vo.CommonResponse;
import com.jeesite.modules.osee.vo.shop.*;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * 商城奖励模块服务类
 *
 * @author zjl
 */
@Service
public class ShopService extends BaseService {

    /**
     * 获取商城奖品列表
     */
    public CommonResponse getShopRewardList(ShopVO shop, Page page) {
        shop.setPageInfo(page);
        return restTemplate.postForObject(apiConfig.buildApiUrl("/osee/shop/list"),
            JSON.toJSONString(shop), CommonResponse.class);
    }

    /**
     * 获取商品对应库存列表
     */
    public CommonResponse getShopRewardStockList(StockVO stock, Page<Map> page) {
        stock.setPageInfo(page);
        return restTemplate.postForObject(apiConfig.buildApiUrl("/osee/shop/stock/list"),
            JSON.toJSONString(stock), CommonResponse.class);
    }

    /**
     * 交换奖品列表项顺序
     */
    public CommonResponse rewardChangeIndex(Long id, Integer type) {
        JSONObject object = new JSONObject();
        object.put("id", id);
        object.put("type", type);
        return restTemplate.postForObject(apiConfig.buildApiUrl("/osee/shop/change"),
            object.toJSONString(), CommonResponse.class);
    }

    /**
     * 获取指定id的奖励物品数据
     */
    public CommonResponse getShopRewardById(Long id) {
        JSONObject object = new JSONObject();
        object.put("id", id);
        return restTemplate.postForObject(apiConfig.buildApiUrl("/osee/shop/search"),
            object.toJSONString(), CommonResponse.class);
    }

    /**
     * 保存奖励数据
     */
    public CommonResponse saveShopReward(ShopVO shop) {
        return restTemplate.postForObject(apiConfig.buildApiUrl("/osee/shop/add"),
            JSON.toJSONString(shop), CommonResponse.class);
    }

    /**
     * 删除奖励数据
     */
    public CommonResponse deleteShopReward(ShopVO shop) {
        return restTemplate.postForObject(apiConfig.buildApiUrl("/osee/shop/delete"),
            JSON.toJSONString(shop), CommonResponse.class);
    }

    /**
     * 更新奖励数据
     */
    public CommonResponse updateShopReward(ShopVO shop) {
        return restTemplate.postForObject(apiConfig.buildApiUrl("/osee/shop/update"),
            JSON.toJSONString(shop), CommonResponse.class);
    }

    /**
     * 保存实物交换订单
     */
    public CommonResponse saveShopExchange(ExchangeVO exchange) {
        return restTemplate.postForObject(apiConfig.buildApiUrl("/osee/shop/submit"),
            JSON.toJSONString(exchange), CommonResponse.class);
    }

    /**
     * 获取商城实物奖品列表
     */
    public CommonResponse getShopEntityList() {
        return restTemplate.getForObject(apiConfig.buildApiUrl("/osee/shop/entity/list"),
            CommonResponse.class);
    }

    /**
     * 获取实物兑换列表
     */
    public CommonResponse getRealExchangeList(ExchangeLogVO exchangeLog, Page<Map> page) {
        exchangeLog.setPageInfo(page);
        return restTemplate.postForObject(apiConfig.buildApiUrl("/osee/shop/real_log/list"),
            JSON.toJSONString(exchangeLog), CommonResponse.class);
    }

    /**
     * 获取虚拟道具兑换列表
     */
    public CommonResponse getUnrealExchangeList(ExchangeLogVO exchangeLog, Page<Map> page) {
        exchangeLog.setPageInfo(page);
        return restTemplate.postForObject(apiConfig.buildApiUrl("/osee/shop/unreal_log/list"),
            JSON.toJSONString(exchangeLog), CommonResponse.class);
    }

    /**
     * 更新指定id的实物兑换订单的发货状态
     */
    public CommonResponse updateRealExchangeState(Long id, Integer state) {
        JSONObject object = new JSONObject();
        object.put("id", id);
        object.put("state", state);
        return restTemplate.postForObject(apiConfig.buildApiUrl("/osee/shop/real_log/state/update"),
            object.toJSONString(), CommonResponse.class);
    }

    /**
     * 添加奖券商城商品库存
     */
    public CommonResponse addShopRewardStock(StockVO stock) {
        return restTemplate.postForObject(apiConfig.buildApiUrl("/osee/shop/stock/add"),
            JSON.toJSONString(stock), CommonResponse.class);
    }

    public CommonResponse getRewardRankData() {
        return restTemplate.getForObject(apiConfig.buildApiUrl("/osee/shop/rewardRank"),
            CommonResponse.class);
    }

    public CommonResponse getRewardSettingData(int type) {
        JSONObject object = new JSONObject();
        object.put("type", type);
        return restTemplate.postForObject(apiConfig.buildApiUrl("/osee/shop/rewardSetting"),
            JSON.toJSONString(object), CommonResponse.class);
    }

    public void updateRewardSettingData(RewardVO rewardVO) {
        restTemplate.postForObject(apiConfig.buildApiUrl("/osee/shop/updateRewardSetting"),
            JSON.toJSONString(rewardVO), CommonResponse.class);
    }

    public CommonResponse saveRewardSettingData(int rank, int type, int status) {
        JSONObject object = new JSONObject();
        object.put("rank", rank);
        object.put("type", type);
        object.put("status", status);
        return restTemplate.postForObject(apiConfig.buildApiUrl("/osee/shop/saveRewardSetting"),
            JSON.toJSONString(object), CommonResponse.class);
    }

    public CommonResponse deleteRewardSettingData(int id) {
        JSONObject object = new JSONObject();
        object.put("id", id);
        return restTemplate.postForObject(apiConfig.buildApiUrl("/osee/shop/deleteRewardSetting"),
            JSON.toJSONString(object), CommonResponse.class);
    }

    public CommonResponse updateRewardRankData(int id, int rank) {
        JSONObject object = new JSONObject();
        object.put("id", id);
        object.put("rank", rank);
        return restTemplate.postForObject(apiConfig.buildApiUrl("/osee/shop/updateRewardRank"),
            JSON.toJSONString(object), CommonResponse.class);
    }


    public CommonResponse getRewardSettingData1(int type) {
        JSONObject object = new JSONObject();
        object.put("type", type);
        return restTemplate.postForObject(apiConfig.buildApiUrl("/osee/shop/rewardSetting1"),
            JSON.toJSONString(object), CommonResponse.class);
    }

    public void updateRewardSettingData1(RewardVO rewardVO) {
        restTemplate.postForObject(apiConfig.buildApiUrl("/osee/shop/updateRewardSetting1"),
            JSON.toJSONString(rewardVO), CommonResponse.class);
    }

    public CommonResponse saveRewardSettingData1(int rank, int type, int status) {
        JSONObject object = new JSONObject();
        object.put("rank", rank);
        object.put("type", type);
        object.put("status", status);
        return restTemplate.postForObject(apiConfig.buildApiUrl("/osee/shop/saveRewardSetting1"),
            JSON.toJSONString(object), CommonResponse.class);
    }

    public CommonResponse deleteRewardSettingData1(int id) {
        JSONObject object = new JSONObject();
        object.put("id", id);
        return restTemplate.postForObject(apiConfig.buildApiUrl("/osee/shop/deleteRewardSetting1"),
            JSON.toJSONString(object), CommonResponse.class);
    }

    public CommonResponse updateRewardRankData1(int id, int rank) {
        JSONObject object = new JSONObject();
        object.put("id", id);
        object.put("rank", rank);
        return restTemplate.postForObject(apiConfig.buildApiUrl("/osee/shop/updateRewardRank1"),
            JSON.toJSONString(object), CommonResponse.class);
    }

    public CommonResponse getRewardSettingData2(int type) {
        JSONObject object = new JSONObject();
        object.put("type", type);
        return restTemplate.postForObject(apiConfig.buildApiUrl("/osee/shop/rewardSetting2"),
            JSON.toJSONString(object), CommonResponse.class);
    }

    public void updateRewardSettingData2(RewardVO rewardVO) {
        restTemplate.postForObject(apiConfig.buildApiUrl("/osee/shop/updateRewardSetting2"),
            JSON.toJSONString(rewardVO), CommonResponse.class);
    }

    public CommonResponse saveRewardSettingData2(int rank, int type, int status) {
        JSONObject object = new JSONObject();
        object.put("rank", rank);
        object.put("type", type);
        object.put("status", status);
        return restTemplate.postForObject(apiConfig.buildApiUrl("/osee/shop/saveRewardSetting2"),
            JSON.toJSONString(object), CommonResponse.class);
    }

    public CommonResponse deleteRewardSettingData2(int id) {
        JSONObject object = new JSONObject();
        object.put("id", id);
        return restTemplate.postForObject(apiConfig.buildApiUrl("/osee/shop/deleteRewardSetting2"),
            JSON.toJSONString(object), CommonResponse.class);
    }

    public CommonResponse updateRewardRankData2(int id, int rank) {
        JSONObject object = new JSONObject();
        object.put("id", id);
        object.put("rank", rank);
        return restTemplate.postForObject(apiConfig.buildApiUrl("/osee/shop/updateRewardRank2"),
            JSON.toJSONString(object), CommonResponse.class);
    }

    public CommonResponse getRewardSettingData3(int type) {
        JSONObject object = new JSONObject();
        object.put("type", type);
        return restTemplate.postForObject(apiConfig.buildApiUrl("/osee/shop/rewardSetting3"),
            JSON.toJSONString(object), CommonResponse.class);
    }

    public void updateRewardSettingData3(RewardVO rewardVO) {
        restTemplate.postForObject(apiConfig.buildApiUrl("/osee/shop/updateRewardSetting3"),
            JSON.toJSONString(rewardVO), CommonResponse.class);
    }

    public CommonResponse saveRewardSettingData3(int rank, int type, int status) {
        JSONObject object = new JSONObject();
        object.put("rank", rank);
        object.put("type", type);
        object.put("status", status);
        return restTemplate.postForObject(apiConfig.buildApiUrl("/osee/shop/saveRewardSetting3"),
            JSON.toJSONString(object), CommonResponse.class);
    }

    public CommonResponse deleteRewardSettingData3(int id) {
        JSONObject object = new JSONObject();
        object.put("id", id);
        return restTemplate.postForObject(apiConfig.buildApiUrl("/osee/shop/deleteRewardSetting3"),
            JSON.toJSONString(object), CommonResponse.class);
    }

    public CommonResponse updateRewardRankData3(int id, int rank) {
        JSONObject object = new JSONObject();
        object.put("id", id);
        object.put("rank", rank);
        return restTemplate.postForObject(apiConfig.buildApiUrl("/osee/shop/updateRewardRank3"),
            JSON.toJSONString(object), CommonResponse.class);
    }

    public CommonResponse getRewardSettingData4(int type) {
        JSONObject object = new JSONObject();
        object.put("type", type);
        return restTemplate.postForObject(apiConfig.buildApiUrl("/osee/shop/rewardSetting4"),
            JSON.toJSONString(object), CommonResponse.class);
    }

    public void updateRewardSettingData4(RewardVO rewardVO) {
        restTemplate.postForObject(apiConfig.buildApiUrl("/osee/shop/updateRewardSetting4"),
            JSON.toJSONString(rewardVO), CommonResponse.class);
    }

    public CommonResponse saveRewardSettingData4(int rank, int type, int status) {
        JSONObject object = new JSONObject();
        object.put("rank", rank);
        object.put("type", type);
        object.put("status", status);
        return restTemplate.postForObject(apiConfig.buildApiUrl("/osee/shop/saveRewardSetting4"),
            JSON.toJSONString(object), CommonResponse.class);
    }

    public CommonResponse deleteRewardSettingData4(int id) {
        JSONObject object = new JSONObject();
        object.put("id", id);
        return restTemplate.postForObject(apiConfig.buildApiUrl("/osee/shop/deleteRewardSetting4"),
            JSON.toJSONString(object), CommonResponse.class);
    }

    public CommonResponse updateRewardRankData4(int id, int rank) {
        JSONObject object = new JSONObject();
        object.put("id", id);
        object.put("rank", rank);
        return restTemplate.postForObject(apiConfig.buildApiUrl("/osee/shop/updateRewardRank4"),
            JSON.toJSONString(object), CommonResponse.class);
    }
}
