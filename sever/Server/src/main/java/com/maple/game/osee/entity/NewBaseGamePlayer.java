package com.maple.game.osee.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;

import com.maple.gamebase.data.BaseGamePlayer;

import cn.hutool.core.lang.func.Func1;
import cn.hutool.core.util.StrUtil;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.SneakyThrows;

@EqualsAndHashCode(callSuper = true)
@Data
public abstract class NewBaseGamePlayer extends BaseGamePlayer {

    private Func1<Long, TreeMap<Long, Set<byte[]>>> baseTreeMapFunc1;

    @SneakyThrows
    public TreeMap<Long, Set<byte[]>> getBaseTreeMap(long multipleTemp) {

        if (baseTreeMapFunc1 == null) {
            return null;
        }

        return baseTreeMapFunc1.call(multipleTemp);

    }

    /**
     * 额外节点的 index集合：额外初始节点
     */
    private List<Integer> extraChuShiJczd0IndexList = new ArrayList<>();

    /**
     * 额外节点的 index集合：收尾节点
     */
    private List<Integer> extraShouWeiJczd0IndexList = new ArrayList<>();

    /**
     * 当前命中鱼的名称
     */
    private String currentHitFishName;

    /**
     * 客户端显示的房间号
     */
    private Integer clientRoomNumber;

    /**
     * 后台显示用：mz
     */
    private double mz;

    /**
     * 后台显示用：命中次数取值方式
     */
    private String needHitNumberMode;

    /**
     * 后台显示用：当前命中次数
     */
    private long currentHitNumber;

    /**
     * 后台显示用：需要命中次数
     */
    private double needHitNumber;

    /**
     * 后台显示用：鱼的奖励倍数
     */
    private double fishRewardMult;

    /**
     * 炮台等级
     */
    private long batteryLevel = 50;

    /**
     * 临时炮倍，翻倍，默认为：1，目的：避免除以 0报错
     */
    private long batteryLevelTempMult = 1;

    public long getBatteryLevel() {
        return batteryLevel;
    }

    public abstract long getMoney();

    /**
     * 后台显示用：jcbfsxjcz：进场爆发上限
     */
    private long jcbfsxjcz;

    /**
     * 上一次的炮台等级，用于：重新生成当前未完成的节点和后续节点
     */
    private long lastBatteryLevel;

    /**
     * CCLSYKTF * BFXY 的值
     */
    private String bfxyValue;

    public String getBfxyValue() {

        return StrUtil.isBlank(bfxyValue) ? "0" : bfxyValue;

    }

    /**
     * 充值龙晶，购买弹头等操作，消耗的龙晶，用于：重新生成当前未完成的节点和后续节点
     */
    private long changeDragonCrystal;

    /**
     * aq信息
     */
    private String aqInfoStr;

    /**
     * 指定鱼种击杀信息
     */
    private String specifyFishInfoStr;

    /**
     * 是否是：机器人，默认：false
     */
    private boolean robotFlag;

}
