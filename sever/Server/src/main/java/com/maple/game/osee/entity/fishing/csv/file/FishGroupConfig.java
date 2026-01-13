package com.maple.game.osee.entity.fishing.csv.file;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import cn.hutool.core.util.ObjectUtil;
import com.maple.engine.anotation.AppData;
import com.maple.engine.data.BaseCsvData;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.util.ObjectUtils;

/**
 * 捕鱼鱼组表
 */
@EqualsAndHashCode(callSuper = true)
@AppData(fileUrl = "data/fishing/cfg_fish_group.csv")
@Data
@Slf4j
public class FishGroupConfig extends BaseCsvData {

    private String fishIdListStr;

    /**
     * 鱼 idList，代码里面用
     */
    private List<Long> fishIdList;

    /**
     * 鱼群 idList,代码里面用
     */
    private List<List<Long>> fishSchoolIdList;
    /**
     * 鱼群 idList,代码里面用 下标
     */
    private Integer index = 0;

    private String delayListStr;

    /**
     * 延迟刷新时间 idList，代码里面用
     */
    private List<Double> delayList;

    /**
     * 延迟刷新时间 idList,代码里面用
     */
    private List<List<Double>> fishSchoolDelayList;

    private String routeListStr;

    /**
     * 路径 list，代码里面用
     */
    private List<RouteConfig> routeList;

    /**
     * 鱼群类型：0为单个鱼，非鱼群；1为鱼群每条有独立的轨迹和生存时间；2为鱼群共用一条轨迹但生存时间独立，按照先后顺序刷出且可配置先生刷出的间隔；3为鱼群共用一条轨迹但独立生存时间，一起刷出不分先后顺序
     * 5 为 鱼群
     */
    private int groupType;

    /**
     * 鱼群组合形状ID：只有鱼群类型为3时生效。按照1、2、3、4来区分
     */
    private int shapeType;

    public List<Long> getFishIdList() {
        if (groupType==5){
            if (ObjectUtils.isEmpty(fishSchoolIdList)) {
               fishSchoolIdList = getFishSchoolIdList();
            }
            return fishSchoolIdList.get(this.index);
        }
        return JSONUtil.toList(getFishIdListStr(), Long.class);
    }

    public List<Double> getDelayList() {
        if (groupType==5){
            if (ObjectUtils.isEmpty(fishSchoolDelayList)) {
                fishSchoolDelayList = getFishSchoolDelayList();
            }
            return fishSchoolDelayList.get(this.index);
        }
        if (StrUtil.isBlank(getDelayListStr())) {
            return null;
        }
        return JSONUtil.toList(getDelayListStr(), Double.class);
    }

    public List<List<Long>> getFishSchoolIdList() {
        if (groupType==5){
            if (ObjectUtil.isNotEmpty(fishSchoolIdList)&& fishSchoolIdList.size()!=0){
                return fishSchoolIdList;
            }
            String str = StrUtil.removeAll(getFishIdListStr(), '[', ']', '{');
            List<String> splitTrimList = StrUtil.splitTrim(str, "},");
            ArrayList<List<Long>> lists = new ArrayList<List<Long>>();
            for (String splitTrim : splitTrimList) {
                List<String> list = StrUtil.splitTrim(StrUtil.removeAll(splitTrim, '}'), ",");
                List<Long> longList = list.stream()
                        .map(Long::parseLong)
                        .collect(Collectors.toList());
                lists.add(longList);
            }
            return lists;
        }
        return new ArrayList<>();
    }

    public List<List<Double>> getFishSchoolDelayList() {
        if (groupType==5){
            if (ObjectUtil.isNotEmpty(fishSchoolDelayList)&& fishSchoolDelayList.size()!=0){
                return fishSchoolDelayList;
            }
            String str = StrUtil.removeAll(getDelayListStr(), '[', ']', '{');
            List<String> splitTrimList = StrUtil.splitTrim(str, "},");
            ArrayList<List<Double>> lists = new ArrayList<List<Double>>();
            for (String splitTrim : splitTrimList) {
                List<String> list = StrUtil.splitTrim(StrUtil.removeAll(splitTrim, '}'), ",");
                List<Double> longList = list.stream()
                        .map(Double::parseDouble)
                        .collect(Collectors.toList());
                lists.add(longList);
            }
            return lists;
        }
        return new ArrayList<>();
    }

    public List<RouteConfig> getRouteList() {

        String str = StrUtil.removeAll(getRouteListStr(), '[', ']', '{');

        List<String> splitTrimList = StrUtil.splitTrim(str, "},");

        return splitTrimList.stream().map(it -> {

            List<String> trimList = StrUtil.splitTrim(it, ",");
            if (trimList.size() == 1) {
                log.error("trimList内容:{},trimList错误长度：{}",trimList,trimList.size());
            }

            return new RouteConfig(Convert.toLong(trimList.get(0)), Convert.toFloat(trimList.get(1)));

        }).collect(Collectors.toList());

    }

    public RouteConfig getRandomRoute() {

        return RandomUtil.randomEle(getRouteList());

    }

}
