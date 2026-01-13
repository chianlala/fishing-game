package com.jeesite.modules.util;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.jeesite.modules.model.csv.FishCcxxConfig;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 * 字典工具类
 */
public class DictUtil {

    /**
     * 获取：游戏状态
     */
    public static List<JSONObject> getGameStateDictList(boolean addFlag) {

        Map<Long, FishCcxxConfig> csvMap = MyCsvUtil.getCsvMap(FishCcxxConfig.class);

        List<JSONObject> gameStateDictList = new ArrayList<>();

        if (addFlag) {

            gameStateDictList.add(
                JSONUtil.createObj().set("dictLabel", "离线").set("dictValue", 0));

            gameStateDictList.add(
                JSONUtil.createObj().set("dictLabel", "在线").set("dictValue", -1));

            gameStateDictList.add(
                JSONUtil.createObj().set("dictLabel", "游戏大厅").set("dictValue", 1));

        }

        csvMap.values().stream().sorted(Comparator.comparingInt(FishCcxxConfig::getSessionId))
            .forEach(it -> {

                if (it.getOpen() == 1) {

                    gameStateDictList.add(
                        JSONUtil.createObj().set("dictLabel", it.getShowSessionName())
                            .set("dictValue", it.getSessionId()));

                }

            });

        return gameStateDictList;

    }

}
