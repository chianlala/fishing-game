package com.jeesite.modules.model.csv;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.json.JSONUtil;
import com.jeesite.modules.util.MyCsvUtil;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 鱼配置
 */
@EqualsAndHashCode(callSuper = true)
@Data
@MyCsvUtil.CsvAnnotation(fileUrl = "data/fishing/cfg_fish.csv")
public class FishConfig extends MyCsvUtil.BaseCsv {

    /**
     * 名称
     */
    private String name;

    /**
     * 模型id
     */
    private int modelId;

    /**
     * 鱼类型 100-boss鱼 其他-暂时无关
     */
    private int fishType;

    /**
     * 血池类型
     */
    private int fishType2;

    /**
     * 基础金币
     */
    private int money;

    /**
     * 最大金币
     */
    private int maxMoney;

    /**
     * 最小安全值
     */
    private int minSafe;

    /**
     * 最大安全值
     */
    private int maxSafe;

    /**
     * 攻击值
     */
    private int attack;

    /**
     * 生命值
     */
    private int health;
    private int health1;
    private int health2;

    /**
     * 全服游走字幕延迟播报时间：-2 客户端请求发 -1 失效 0 立即发 大于0部分根据时间发，单位为 秒
     */
    private int wanderSubtitleS;

    /**
     * 最大生命值
     */
    private int maxhealth;

    /**
     * 号角召唤鱼：0 则不能召唤，否则则表示：要消耗的号角数量
     */
    private int HornCallsFish;

    /**
     * 经验值
     */
    private int exp;

    /**
     * 附带技能
     */
    private int skill;

    /**
     * 掉落材料
     */
    private String fallingMaterials;

    /**
     * 最少掉落技能数量
     */
    private String minSkillDropNum;

    /**
     * 最大掉落技能数量
     */
    private String maxSkillDropNum;

    /**
     * 技能掉落概率
     */
    private int skillDropProbability;

    /**
     * 技能掉落概率2000-500000
     */
    private int skillDropProbabilityOne;

    /**
     * 技能掉落概率500000-800000
     */
    private int skillDropProbabilityTwo;

    /**
     * 技能掉落概率最大
     */
    private int skillDropProbabilityMax;

    /**
     * 技能掉落概率最大
     */
    private int skillDropProbabilityJc;

    /**
     * 赢取的钱
     */
    private long winMoney;

    /**
     * 持续时间内刷新的间隔时间，单位：秒
     */
    private int delayTime;

    /**
     * 持续时间范围内死亡以后，是否继续刷鱼：0 否 1 是
     */
    private int durationDeathRefreshFlag;

    /**
     * 爆发时基础基础金币
     */
    private long bfMoney;

    public long getBfMoney() {
        return bfMoney == 0 ? getMoney() : bfMoney;
    }

    /**
     * 爆发时最大基础金币
     */
    private long bfMaxMoney;

    public long getBfMaxMoney() {
        return bfMaxMoney == 0 ? getMaxMoney() : bfMaxMoney;
    }

    /**
     * 回收时基础基础金币
     */
    private long hsMoney;

    public long getHsMoney() {
        return hsMoney == 0 ? getMoney() : hsMoney;
    }

    /**
     * 回收时最大基础金币
     */
    private long hsMaxMoney;

    public long getHsMaxMoney() {
        return hsMaxMoney == 0 ? getMaxMoney() : hsMaxMoney;
    }

    /**
     * 怪物血量：第一个参数：血量，第二个参数：血量管数，例如：[1000,10]，备注：如果不配置该字段，则表示不用扣除血量
     */
    private String monsterHpStr;

    public int getMonsterMaxHp() {
        return JSONUtil.toList(monsterHpStr, Integer.class).get(0);
    }

    public int getMonsterHpCount() {
        return JSONUtil.toList(monsterHpStr, Integer.class).get(1);
    }

    /**
     * 玩家攻击力范围，例如：[10,20]
     */
    private String playerAttackScopeStr;

    public List<Integer> getPlayerAttackScopeList() {
        List<Integer> resList = JSONUtil.toList(playerAttackScopeStr, Integer.class);
        return CollUtil.isEmpty(resList) ? CollUtil.newArrayList(10, 20) : resList;
    }

    /**
     * 怪物攻击力范围，例如：[10,20]，备注：玩家只有1000血
     */
    private String monsterAttackScopeStr;

    public List<Integer> getMonsterAttackScopeList() {
        List<Integer> resList = JSONUtil.toList(monsterAttackScopeStr, Integer.class);
        return CollUtil.isEmpty(resList) ? CollUtil.newArrayList(10, 20) : resList;
    }

    /**
     * 怪物攻击一次需要的时间，例如：2000，单位：毫秒
     */
    private int monsterAttackTime = 2000;

    /**
     * 定时刷鱼时间集合，即：会在那个时间点刷出该鱼，例如：[07:00,15:00]，备注：24小时值，该字段有值的鱼，血量是全服共用的
     */
    private String refreshTimeListStr;

    public List<String> getRefreshTimeList() {
        return JSONUtil.toList(refreshTimeListStr, String.class);
    }

    /**
     * 怪物血量扣除方式：1 直接扣除（默认） 2 打死一起刷的鱼，才扣除第一条鱼的血量
     */
    private int monsterHpDeductType;

    /**
     * 场景
     */
    private int scene;

}
