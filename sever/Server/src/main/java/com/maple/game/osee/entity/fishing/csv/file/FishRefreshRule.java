package com.maple.game.osee.entity.fishing.csv.file;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.util.StringUtils;

import com.maple.engine.anotation.AppData;
import com.maple.engine.container.DataContainer;
import com.maple.engine.data.BaseCsvData;
import com.maple.engine.manager.GsonManager;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 捕鱼刷新规则
 */
@EqualsAndHashCode(callSuper = true)
@AppData(fileUrl = "data/fishing/cfg_fish_refresh_rule.csv")
@Data
public class FishRefreshRule extends BaseCsvData {

    /**
     * 针对：动态刷新，并且配置了 delayTime的鱼，表示：该鱼不是马上刷出，而是等待 delayTime后再刷新，该值就是用于判断是否刷新的：下次刷新时间戳
     */
    private long nextDynamicRefreshDelayTs;

    /**
     * 最小间隔
     */
    private int minDelay;

    /**
     * 最大间隔
     */
    private int maxDelay;

    /**
     * 是否为鱼潮
     */
    private boolean fishTide;

    /**
     * 是否动态刷新
     */
    private boolean dynamicRefresh;

    /**
     * 是否为世界boss
     */
    private boolean worldBossFlag;

    /**
     * 是否是boss刷新规则
     */
    private boolean boss;

    /**
     * 出现场次
     */
    private String scene;

    /**
     * 备注
     */
    private String bz = "";

    /**
     * 真实出现场次
     */
    private Integer[] realScene;

    /**
     * 持续时间
     */
    private int duration;

    /**
     * 持续时间，递减的值，会随着时间递减，代码里面使用
     */
    private int durationTemp;

    /**
     * 下次刷新时间，代码里面使用，目前只有 boss鱼，有该值
     */
    private long nextRefreshTime;

    /**
     * 刷新类型：1 普通 2 神灯 3号角 4普通一号 5普通二号 6鱼潮
     */
    private int type;

    /**
     * 禁用刷鱼类型规则的区分：根据数组中配置的刷鱼类型进行判断，是否禁用对应刷鱼类型的刷鱼规则。
     * 例如：[0]表示没有任何限制，[1]表示限制刷新类型为1的刷鱼规则，[1,2]表示限制刷新类型为1和2的刷鱼规则，[1,5]表示限制刷新类型为1和5的刷鱼规则
     */
    public String limitRefreshRules;
    public List<Integer> getLimitRefreshRules(){
        return JSONUtil.toList(limitRefreshRules, Integer.class);
    }

    /**
     * 鱼数量，备注：目前只有普通鱼有
     */
    private int fishCount;

    /**
     * 鱼群 idList
     */
    private String collectIdListStr;

    public List<Long> getCollectIdList() {
        return JSONUtil.toList(getCollectIdListStr(), Long.class).stream()
            .map(it -> DataContainer.handleId(getGameId(), it)).collect(Collectors.toList());
    }

    /**
     * 每次随机少一个：下一次会刷新的鱼 collectIdList，备注：刷一次会从集合里面移除一个，如果全部移出，则会重置
     */
    private List<Long> nextRefreshCollectIdList;

    /**
     * 获取 collectIdList的方式：1 随机获取（默认） 2 每次随机少一个
     */
    private int refreshCollectType;

    /**
     * 定时刷鱼时间集合，即：会在那个时间点刷出该鱼，例如：[07:00,15:00]，备注：24小时值
     */
    private String refreshTimeListStr;

    public List<String> getRefreshTimeList() {

        if (StrUtil.isBlank(refreshTimeListStr)) {
            return null;
        }

        return JSONUtil.toList(refreshTimeListStr, String.class);

    }

    /**
     * 上一次的刷新时间（时间戳），备注：用于：定时刷鱼时间集合
     */
    private long fixedLastRefreshTime;

    public Integer[] getRealScene() {
        if (realScene == null) {
            realScene = StringUtils.isEmpty(scene) ? new Integer[0] : GsonManager.gson.fromJson(scene, Integer[].class);
        }
        return realScene;
    }

    /**
     * boss号角出场后，是否失效：0是，1否（默认）
     */
    private int useBossBugleInvalid = 1;


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FishRefreshRule that = (FishRefreshRule) o;
        return getId() == that.getId() && type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(),type);
    }
}
