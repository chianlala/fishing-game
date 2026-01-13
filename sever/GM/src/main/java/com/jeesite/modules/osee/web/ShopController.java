package com.jeesite.modules.osee.web;

import com.alibaba.fastjson.JSON;
import com.jeesite.common.config.Global;
import com.jeesite.common.entity.Page;
import com.jeesite.common.web.BaseController;
import com.jeesite.modules.osee.service.ShopService;
import com.jeesite.modules.osee.vo.CommonResponse;
import com.jeesite.modules.osee.vo.shop.*;
import com.jeesite.modules.sys.utils.UserUtils;
import com.jeesite.modules.util.CommonUtils;
import org.apache.ibatis.annotations.Param;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 商城管理模块
 *
 * @author zjl
 */
@Controller
@RequestMapping("${adminPath}/osee/shop")
public class ShopController extends BaseController {

    @Autowired
    private ShopService shopService;

    // ========================= 商城奖品 ==========================

    @RequiresPermissions("shop:reward:view")
    @RequestMapping("reward")
    public String shopReward(ShopVO shop, Model model) {
        model.addAttribute("shop", shop);
        return "modules/osee/shop/reward";
    }

    @RequiresPermissions("shop:reward:view")
    @PostMapping("reward/list")
    @ResponseBody
    public Page<Map> shopRewardList(ShopVO shop, HttpServletRequest request,
        HttpServletResponse response) {
        Page<Map> page = new Page<>(request, response);
        CommonResponse commonResponse = shopService.getShopRewardList(shop, page);
        if (commonResponse.getSuccess()) {
            Map data = (Map) commonResponse.getData();
            page.setCount((Integer) data.get("totalNum"));
            page.setList((List<Map>) data.get("list"));
        }
        return page;
    }

    @RequiresPermissions("shop:reward:edit")
    @RequestMapping("reward/form")
    public String shopRewardForm(ShopVO shop, Model model) {
        if (shop.getId() != null) {
            CommonResponse commonResponse = shopService.getShopRewardById(shop.getId());
            if (commonResponse.getSuccess()) {
                shop = JSON.parseObject(JSON.toJSONString(commonResponse.getData()), ShopVO.class);
            }
        }
        model.addAttribute("shop", shop);
        return "modules/osee/shop/rewardForm";
    }

    @RequiresPermissions("shop:reward:edit")
    @PostMapping("reward/save")
    @ResponseBody
    public String shopRewardSave(ShopVO shop, HttpServletRequest request) {
        if (shop.getSize() == null) {
            shop.setSize(0); // 无限次兑换
        }
        if (shop.getRefreshType() == null) {
            shop.setRefreshType(0); // 限购无限制
        }
        if (!shop.getImg().startsWith("http")) {
            // 为图片加上完整的访问链接，游戏前端需要访问
            shop.setImg(CommonUtils.getServerURIFromRequest(request) + shop.getImg());
        }
        CommonResponse commonResponse;
        if (shop.getId() != null) { // ID不为空就是更新数据
            commonResponse = shopService.updateShopReward(shop);
            if (commonResponse.getSuccess()) {
                return renderResult(Global.TRUE, "修改成功！");
            }
        } else {
            commonResponse = shopService.saveShopReward(shop);
            if (commonResponse.getSuccess()) {
                return renderResult(Global.TRUE, "保存成功！");
            }
        }
        return renderResult(Global.FALSE, "保存失败：" + commonResponse.getErrMsg());
    }

    @RequiresPermissions("shop:reward:edit")
    @PostMapping("reward/delete")
    @ResponseBody
    public String shopRewardDelete(ShopVO shop) {
        CommonResponse commonResponse = shopService.deleteShopReward(shop);
        if (commonResponse.getSuccess()) {
            return renderResult(Global.TRUE, "删除成功！");
        }
        return renderResult(Global.FALSE, "删除失败：" + commonResponse.getErrMsg());
    }

    @RequiresPermissions("shop:reward:edit")
    @RequestMapping("reward/changeIndex")
    @ResponseBody
    public String shopRewardChangeIndex(Long id, Integer type) {
        CommonResponse commonResponse = shopService.rewardChangeIndex(id, type);
        if (commonResponse.getSuccess()) {
            return renderResult(Global.TRUE, type == -1 ? "上移成功！" : "下移成功！");
        }
        return renderResult(Global.FALSE,
            (type == -1 ? "上移失败：" : "下移失败：") + commonResponse.getErrMsg());
    }

    /**
     * 进入商品库存页面
     */
    @RequiresPermissions("shop:reward:view")
    @RequestMapping("reward/stock")
    public String shopRewardStock(StockVO stock, Model model) {
        model.addAttribute("stock", stock);
        return "modules/osee/shop/rewardStock";
    }

    /**
     * 商品库存列表数据
     */
    @RequiresPermissions("shop:reward:view")
    @PostMapping("reward/stock/list")
    @ResponseBody
    public Page<Map> shopRewardStockList(StockVO stock, HttpServletRequest request,
        HttpServletResponse response) {
        Page<Map> page = new Page<>(request, response);
        CommonResponse commonResponse = shopService.getShopRewardStockList(stock, page);
        if (commonResponse.getSuccess()) {
            Map data = (Map) commonResponse.getData();
            page.setCount((Integer) data.get("totalNum"));
            page.setList((List<Map>) data.get("list"));

            page.addOtherData("shopName", data.get("shopName"));
            page.addOtherData("stock", data.get("stock"));
            page.addOtherData("usedNum", data.get("usedNum"));
        }
        return page;
    }

    /**
     * 进入商品库存添加页面
     */
    @RequiresPermissions("shop:reward:edit")
    @GetMapping("reward/stock/add")
    public String shopRewardStockAdd(StockVO stock, Model model) {
        model.addAttribute("stock", stock);
        return "modules/osee/shop/rewardStockAdd";
    }

    /**
     * 添加库存
     */
    @RequiresPermissions("shop:reward:edit")
    @PostMapping("reward/stock/add")
    @ResponseBody
    public String shopRewardStockAdd(StockVO stock) {
        CommonResponse commonResponse = shopService.addShopRewardStock(stock);
        if (commonResponse.getSuccess()) {
            return renderResult(Global.TRUE, "添加成功");
        }
        return renderResult(Global.FALSE, "添加失败：" + commonResponse.getErrMsg());
    }

    // ========================= 商城实物兑换 ==========================

    @RequiresPermissions("shop:exchange:edit")
    @GetMapping("exchange")
    public String exchange(ExchangeVO exchange, Model model) {
        CommonResponse commonResponse = shopService.getShopEntityList();
        if (commonResponse.getSuccess()) {
            Map data = (Map) commonResponse.getData();
            List<Map> entityList = (List<Map>) data.get("list");
            model.addAttribute("shopEntityList", entityList);
        }

        // 当前登录用户信息
        exchange.setCreator(UserUtils.getUser().getUserName());
        model.addAttribute("exchange", exchange);
        return "modules/osee/shop/exchange";
    }

    @RequiresPermissions("shop:exchange:edit")
    @PostMapping("exchange/reward/info")
    @ResponseBody
    public String rewardInfo(Long id) {
        CommonResponse commonResponse = shopService.getShopRewardById(id);
        if (commonResponse.getSuccess()) {
            return renderResult(Global.TRUE, "", JSON.toJSONString(commonResponse.getData()));
        }
        return renderResult(Global.FALSE, "获取奖励物品信息失败：" + commonResponse.getErrMsg());
    }

    @RequiresPermissions("shop:exchange:edit")
    @PostMapping("exchange/save")
    @ResponseBody
    public String exchangeSave(ExchangeVO exchange) {
        CommonResponse commonResponse = shopService.saveShopExchange(exchange);
        if (commonResponse.getSuccess()) {
            return renderResult(Global.TRUE, "提交订单成功！");
        }
        return renderResult(Global.FALSE, "提交订单失败：" + commonResponse.getErrMsg());
    }

    // ========================= 商城虚拟物品兑换记录 ==========================

    /**
     * 实物兑换记录
     */
    @RequiresPermissions("shop:exchange:log:real:view")
    @RequestMapping("exchange/log/real")
    public String realExchange(ExchangeLogVO exchangeLog, Model model) {
        model.addAttribute("exchangeLog", exchangeLog);
        return "modules/osee/shop/exchangeReal";
    }

    /**
     * 实物兑换记录列表(json)
     */
    @RequiresPermissions("shop:exchange:log:real:view")
    @RequestMapping("exchange/log/real/list")
    @ResponseBody
    public Page<Map> realExchangeList(ExchangeLogVO exchangeLog, HttpServletRequest request,
        HttpServletResponse response) {
        Page<Map> page = new Page<>(request, response);
        CommonResponse commonResponse = shopService.getRealExchangeList(exchangeLog, page);
        if (commonResponse.getSuccess()) {
            Map data = (Map) commonResponse.getData();
            page.setCount((Integer) data.get("totalNum"));
            page.setList((List<Map>) data.get("list"));
            // 兑换记录的统计信息数据
            Map<String, Object> otherData = new HashMap<>();
            otherData.put("state_0", data.get("state_0")); // 待发货条数
            otherData.put("state_1", data.get("state_1")); // 已发货条数
            otherData.put("state_2", data.get("state_2")); // 已拒绝条数
            page.setOtherData(otherData);
        }
        return page;
    }

    /**
     * 更新实物兑换订单的发货状态
     */
    @RequiresPermissions("shop:exchange:log:real:edit")
    @RequestMapping("exchange/log/real/state/update")
    @ResponseBody
    public String realExchangeStateUpdate(Long id, Integer state) {
        CommonResponse commonResponse = shopService.updateRealExchangeState(id, state);
        if (commonResponse.getSuccess()) {
            return renderResult(Global.TRUE, "订单状态修改成功！");
        }
        return renderResult(Global.FALSE, "订单状态修改失败：" + commonResponse.getErrMsg());
    }

    /**
     * 虚拟道具兑换记录
     */
    @RequiresPermissions("shop:exchange:log:unreal:view")
    @RequestMapping("exchange/log/unreal")
    public String unrealExchange(ExchangeLogVO exchangeLog, Model model) {
        model.addAttribute("exchangeLog", exchangeLog);
        return "modules/osee/shop/exchangeUnreal";
    }

    /**
     * 虚拟道具兑换记录列表(json)
     */
    @RequiresPermissions("shop:exchange:log:unreal:view")
    @RequestMapping("exchange/log/unreal/list")
    @ResponseBody
    public Page<Map> unrealExchangeList(ExchangeLogVO exchangeLog, HttpServletRequest request,
        HttpServletResponse response) {
        Page<Map> page = new Page<>(request, response);
        CommonResponse commonResponse = shopService.getUnrealExchangeList(exchangeLog, page);
        if (commonResponse.getSuccess()) {
            Map data = (Map) commonResponse.getData();
            page.setCount((Integer) data.get("totalNum"));
            page.setList((List<Map>) data.get("list"));
        }
        return page;
    }

    @RequestMapping("rewardRank")
    public String rewardRank() {
        System.out.println("hello");
        return "modules/osee/shop/rewardRank";
    }

    @RequestMapping("rewardSetting")
    public String rewardSetting() {
        return "modules/osee/shop/rewardSetting";
    }

    @RequestMapping("rewardSetting1")
    public String rewardSetting1() {
        return "modules/osee/shop/rewardSetting1";
    }

    @RequestMapping("rewardSetting2")
    public String rewardSetting2() {
        return "modules/osee/shop/rewardSetting2";
    }

    @RequestMapping("rewardSetting3")
    public String rewardSetting3() {
        return "modules/osee/shop/rewardSetting3";
    }

    @RequestMapping("rewardSetting4")
    public String rewardSetting4() {
        return "modules/osee/shop/rewardSetting4";
    }

    @RequestMapping(value = "/updateRewardRankData", method = RequestMethod.POST)
    @ResponseBody
    public String rewardRankData(int id, int rank) {
        CommonResponse commonResponse = shopService.updateRewardRankData(id, rank);
        return renderResult(Global.TRUE, "", commonResponse.getData());
    }

    @RequestMapping("/rewardSettingData")
    @ResponseBody
    public String rewardSettingData(@Param("type") int type) {
        CommonResponse commonResponse = shopService.getRewardSettingData(type);
        return renderResult(Global.TRUE, "", commonResponse.getData());
    }

    @RequestMapping(value = "/updateRewardSettingData", method = RequestMethod.POST)
    @ResponseBody
    public void updateRewardSettingData(RewardVO rewardVO) {
        shopService.updateRewardSettingData(rewardVO);
    }

    @RequestMapping(value = "/saveRewardSettingData", method = RequestMethod.POST)
    @ResponseBody
    public String saveRewardSettingData(int rank, int type, int status) {
        CommonResponse commonResponse = shopService.saveRewardSettingData(rank, type, status);
        return renderResult(Global.TRUE, "", commonResponse.getData());
    }

    @RequestMapping(value = "/deleteRewardSettingData", method = RequestMethod.POST)
    @ResponseBody
    public String deleteRewardSettingData(int id) {
        CommonResponse commonResponse = shopService.deleteRewardSettingData(id);
        return renderResult(Global.TRUE, "", commonResponse.getData());
    }

    @RequestMapping(value = "/updateRewardRankData1", method = RequestMethod.POST)
    @ResponseBody
    public String rewardRankData1(int id, int rank) {
        CommonResponse commonResponse = shopService.updateRewardRankData1(id, rank);
        return renderResult(Global.TRUE, "", commonResponse.getData());
    }

    @RequestMapping("/rewardSettingData1")
    @ResponseBody
    public String rewardSettingData1(@Param("type") int type) {
        CommonResponse commonResponse = shopService.getRewardSettingData1(type);
        return renderResult(Global.TRUE, "", commonResponse.getData());
    }

    @RequestMapping(value = "/updateRewardSettingData1", method = RequestMethod.POST)
    @ResponseBody
    public void updateRewardSettingData1(RewardVO rewardVO) {
        shopService.updateRewardSettingData1(rewardVO);
    }

    @RequestMapping(value = "/saveRewardSettingData1", method = RequestMethod.POST)
    @ResponseBody
    public String saveRewardSettingData1(int rank, int type, int status) {
        CommonResponse commonResponse = shopService.saveRewardSettingData1(rank, type, status);
        return renderResult(Global.TRUE, "", commonResponse.getData());
    }

    @RequestMapping(value = "/deleteRewardSettingData1", method = RequestMethod.POST)
    @ResponseBody
    public String deleteRewardSettingData1(int id) {
        CommonResponse commonResponse = shopService.deleteRewardSettingData1(id);
        return renderResult(Global.TRUE, "", commonResponse.getData());
    }

    @RequestMapping(value = "/updateRewardRankData2", method = RequestMethod.POST)
    @ResponseBody
    public String rewardRankData2(int id, int rank) {
        CommonResponse commonResponse = shopService.updateRewardRankData2(id, rank);
        return renderResult(Global.TRUE, "", commonResponse.getData());
    }

    @RequestMapping("/rewardSettingData2")
    @ResponseBody
    public String rewardSettingData2(@Param("type") int type) {
        CommonResponse commonResponse = shopService.getRewardSettingData2(type);
        return renderResult(Global.TRUE, "", commonResponse.getData());
    }

    @RequestMapping(value = "/updateRewardSettingData2", method = RequestMethod.POST)
    @ResponseBody
    public void updateRewardSettingData2(RewardVO rewardVO) {
        shopService.updateRewardSettingData2(rewardVO);
    }

    @RequestMapping(value = "/saveRewardSettingData2", method = RequestMethod.POST)
    @ResponseBody
    public String saveRewardSettingData2(int rank, int type, int status) {
        CommonResponse commonResponse = shopService.saveRewardSettingData2(rank, type, status);
        return renderResult(Global.TRUE, "", commonResponse.getData());
    }

    @RequestMapping(value = "/deleteRewardSettingData2", method = RequestMethod.POST)
    @ResponseBody
    public String deleteRewardSettingData2(int id) {
        CommonResponse commonResponse = shopService.deleteRewardSettingData2(id);
        return renderResult(Global.TRUE, "", commonResponse.getData());
    }

    @RequestMapping(value = "/updateRewardRankData3", method = RequestMethod.POST)
    @ResponseBody
    public String rewardRankData3(int id, int rank) {
        CommonResponse commonResponse = shopService.updateRewardRankData3(id, rank);
        return renderResult(Global.TRUE, "", commonResponse.getData());
    }

    @RequestMapping("/rewardSettingData3")
    @ResponseBody
    public String rewardSettingData3(@Param("type") int type) {
        CommonResponse commonResponse = shopService.getRewardSettingData3(type);
        return renderResult(Global.TRUE, "", commonResponse.getData());
    }

    @RequestMapping(value = "/updateRewardSettingData3", method = RequestMethod.POST)
    @ResponseBody
    public void updateRewardSettingData3(RewardVO rewardVO) {
        shopService.updateRewardSettingData3(rewardVO);
    }

    @RequestMapping(value = "/saveRewardSettingData3", method = RequestMethod.POST)
    @ResponseBody
    public String saveRewardSettingData3(int rank, int type, int status) {
        CommonResponse commonResponse = shopService.saveRewardSettingData3(rank, type, status);
        return renderResult(Global.TRUE, "", commonResponse.getData());
    }

    @RequestMapping(value = "/deleteRewardSettingData3", method = RequestMethod.POST)
    @ResponseBody
    public String deleteRewardSettingData3(int id) {
        CommonResponse commonResponse = shopService.deleteRewardSettingData3(id);
        return renderResult(Global.TRUE, "", commonResponse.getData());
    }


    @RequestMapping(value = "/updateRewardRankData4", method = RequestMethod.POST)
    @ResponseBody
    public String rewardRankData4(int id, int rank) {
        CommonResponse commonResponse = shopService.updateRewardRankData4(id, rank);
        return renderResult(Global.TRUE, "", commonResponse.getData());
    }

    @RequestMapping("/rewardSettingData4")
    @ResponseBody
    public String rewardSettingData4(@Param("type") int type) {
        CommonResponse commonResponse = shopService.getRewardSettingData4(type);
        return renderResult(Global.TRUE, "", commonResponse.getData());
    }

    @RequestMapping(value = "/updateRewardSettingData4", method = RequestMethod.POST)
    @ResponseBody
    public void updateRewardSettingData4(RewardVO rewardVO) {
        shopService.updateRewardSettingData4(rewardVO);
    }

    @RequestMapping(value = "/saveRewardSettingData4", method = RequestMethod.POST)
    @ResponseBody
    public String saveRewardSettingData4(int rank, int type, int status) {
        CommonResponse commonResponse = shopService.saveRewardSettingData4(rank, type, status);
        return renderResult(Global.TRUE, "", commonResponse.getData());
    }

    @RequestMapping(value = "/deleteRewardSettingData4", method = RequestMethod.POST)
    @ResponseBody
    public String deleteRewardSettingData4(int id) {
        CommonResponse commonResponse = shopService.deleteRewardSettingData4(id);
        return renderResult(Global.TRUE, "", commonResponse.getData());
    }
}
