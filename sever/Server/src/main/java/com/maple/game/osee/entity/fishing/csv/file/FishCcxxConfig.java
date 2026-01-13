package com.maple.game.osee.entity.fishing.csv.file;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.maple.engine.anotation.AppData;
import com.maple.engine.data.BaseCsvData;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;

/**
 * 场次相关配置表
 */
@EqualsAndHashCode(callSuper = true)
@Data
@AppData(fileUrl = "data/fishing/cfg_fish_ccxx.csv")
public class FishCcxxConfig extends BaseCsvData {

    /**
     * 场次 id：备注：1到100：大奖赛，101到199：体验场，201到299：积分场，1001到9999：龙晶场，100000 slot，100101 丛林野兽，100201 丛林野兽
     */
    private Integer sessionId;

    /**
     * 展示的场次名称
     */
    private String showSessionName;

    /**
     * 场次开关：0关，1开
     */
    private int open;

    /**
     * 可以使用的技能集合字符串
     */
    private String skillIdListStr;

    public List<Integer> getSkillIdList() {

        if (StrUtil.isBlank(getSkillIdListStr())) {
            return new ArrayList<>();
        }

        return JSONUtil.toList(getSkillIdListStr(), Integer.class);

    }

    /**
     * 冰冻使用类型：0 全屏（默认） 1 随机
     */
    private int frozenType;

    /**
     * 可以使用的技能，付费类型集合字符串，付费类型：0免费，1付费（默认）
     */
    private String skillPayTypeListStr;

    public List<Integer> getSkillPayTypeList() {
        return JSONUtil.toList(getSkillPayTypeListStr(), Integer.class);
    }

    /**
     * 可以使用的炮台集合字符串
     */
    private String batteryIdListStr;

    public List<Integer> getBatteryIdList() {

        if (StrUtil.isBlank(getBatteryIdListStr())) {
            return new ArrayList<>();
        }

        return JSONUtil.toList(getBatteryIdListStr(), Integer.class);

    }

    /**
     * 可以使用的翅膀集合字符串
     */
    private String wingIdListStr;

    public List<Integer> getWingIdList() {

        if (StrUtil.isBlank(getWingIdListStr())) {
            return new ArrayList<>();
        }

        return JSONUtil.toList(getWingIdListStr(), Integer.class);

    }

    /**
     * 每个房间的最大人数
     */
    private int playerNumber;

    /**
     * 每个场次的房间个数
     */
    private int roomNumber;

    /**
     * 游走字幕，最低中奖倍数
     */
    private int wanderSubtitleMinWinMultiple = 400;

    /**
     * 游走字幕，最低炮倍，备注：需要同时满足，游走字幕最低中奖倍数和游走字幕最低炮倍，才会播游走字幕
     */
    private int wanderSubtitleMinBatteryLevel = 10000;

    /**
     * 播报，最低中奖倍数
     */
    private int notificationMinWinMultiple = 400;

    /**
     * 播报，最低炮倍，备注：需要同时满足，播报最低中奖倍数和播报最低炮倍，才会播播报
     */
    private int notificationMinBatteryLevel = 10000;

}
