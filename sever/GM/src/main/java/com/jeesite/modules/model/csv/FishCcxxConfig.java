package com.jeesite.modules.model.csv;

import cn.hutool.json.JSONUtil;
import com.jeesite.modules.util.MyCsvUtil;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 场次相关配置表
 */
@EqualsAndHashCode(callSuper = true)
@Data
@MyCsvUtil.CsvAnnotation(fileUrl = "data/fishing/cfg_fish_ccxx.csv")
public class FishCcxxConfig extends MyCsvUtil.BaseCsv {

    /**
     * 场次 id：备注：1到100：大奖赛，101到199：体验场，201到299：积分场，1001到9999：龙晶场
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
        return JSONUtil.toList(getSkillIdListStr(), Integer.class);
    }

    /**
     * 冰冻使用类型：0全屏，1随机
     */
    private int frozenType;

    /**
     * 可以使用的技能，付费类型集合字符串，付费类型：0免费，1付费
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
        return JSONUtil.toList(getBatteryIdListStr(), Integer.class);
    }

    /**
     * 可以使用的翅膀集合字符串
     */
    private String wingIdListStr;

    public List<Integer> getWingIdList() {
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

}
