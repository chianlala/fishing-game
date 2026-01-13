package com.maple.game.osee.entity.fishing.game;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.maple.engine.container.DataContainer;
import com.maple.game.osee.entity.fishing.csv.file.FishRefreshRule;
import lombok.Data;

/**
 * 鱼类数据
 */
@Data
public class FishStruct {

    /**
     * 是否是 boss
     */
    private boolean bossFlag;

    /**
     * 实体id
     */
    private long id;

    /**
     * 刷新规则id
     */
    private long ruleId;

    /**
     * 配置id
     */
    private long configId;

    /**
     * 路线id
     */
    private long routeId;

    /**
     * 存活时间，单位：毫秒
     */
    private float lifeTime;

    /**
     * 存活时间：原始值，因为冰冻之后 lifeTime会增加，单位：秒
     */
    private float lifeOrigTime;

    /**
     * 当前存活时间，单位：毫秒
     */
    private float nowLifeTime;

    /**
     * 安全次数
     */
    private int safeTimes;

    /**
     * 命中次数
     */
    private int fireTimes;

    /**
     * 创建时间
     */
    private long createTime;

    /**
     * 血池类型
     */
    private int fishType2;

    /**
     * 鱼类型
     */
    private int fishType;

    /**
     * 是否号角召唤
     */
    private boolean isBossBulge;

    /**
     * 是否第一次刷
     */
    private boolean isFirst;

    /**
     * 冰冻时长，单位：毫秒
     */
    private long fTime;

    /**
     * 新刷新规则：是否动态刷新
     */
    private boolean dynamicRefresh;

    /**
     * 新刷新规则：是否是持续刷鱼时间刷出
     */
    private boolean durationRefreshFlag;

    /**
     * 新冰冻：鱼最后被冰冻的时间戳
     */
    private long lastFishFrozenTime;

    /**
     * 持续时间内刷新的间隔时间，单位：秒
     */
    private int delayTime;

    /**
     * 使用boss号角的间隔时间，单位：秒
     */
    private int bossBugleDelayTime;

    /**
     * 持续时间范围内死亡以后，是否继续刷鱼：0 否 1 是
     */
    private int durationDeathRefreshFlag;

    /**
     * 打中过该鱼的 userIdSet
     */
    private Set<Long> hitUserIdSet = new CopyOnWriteArraySet<>();

    /**
     * 是否是：出奇制胜
     */
    private boolean chuQiZhiShengFlag;

    /**
     * 模型 id
     */
    private int modelId;

    /**
     * 奖券数
     */
    private long lottery;

    /**
     * 鱼群形状 id
     */
    private int shapeId;

    /**
     * 跟着一起刷的鱼，备注，包含自己，并且集合的顺序是按照配置表里的顺序来的
     */
    private List<FishStruct> fishStructList;

    /**
     * 怪物当前血量
     */
    private int monsterHp;

    /**
     * 怪物原始血量
     */
    private int monsterMaxHp;

    /**
     * 怪物血量的管数
     */
    private int monsterHpCount;

    /**
     * 玩家攻击力范围
     */
    private List<Integer> playerAttackScopeList;

    /**
     * 怪物攻击力范围
     */
    private List<Integer> monsterAttackScopeList;

    /**
     * 怪物本次攻击需要的时间，单位：毫秒
     */
    private int monsterAttackTime;

    /**
     * 怪物攻击一次需要的时间集合，单位：毫秒
     */
    private List<Integer> monsterAttackTimeList;

    /**
     * 怪物正在攻击的玩家 id
     */
    private Long monsterAttackPlayerId;

    /**
     * 怪物可以攻击的玩家 idSet，备注：包含正在攻击的玩家 id
     */
    private LinkedHashSet<Long> tempMonsterAttackPlayerIdSet = new LinkedHashSet<>();

    /**
     * 怪物上一次攻击的时间戳
     */
    private long monsterLastAttackTs;

    /**
     * 怪物已经攻击了多久，用于：冰冻时计算
     */
    private long attackFinishMs;

    /**
     * 本次攻击玩家的血量
     */
    private int hitPlayerValue;

    /**
     * 本次攻击的玩家 id
     */
    private long hitPlayerId;

    /**
     * 是否是：世界 boss
     */
    private boolean worldBossFlag;

    /**
     * 怪物血量扣除方式：1 直接扣除（默认） 2 打死一起刷的鱼，才扣除第一条鱼的血量
     */
    private int monsterHpDeductType;

    /**
     * 如果是一起刷的鱼，则该字段用于：表示其在配置表中配置的位置
     */
    private int groupSeat;

    /**
     * 是否可以延长存活时间：1 可以（默认） 0 不可以
     */
    private boolean addSurvivalTimeFlag = true;

    private Long bfJdcz; // 爆发阶段差值

    /**
     * 刷新类型：1 普通 2 神灯 3号角 4普通一号 5普通二号 6鱼潮
     */
    private int fishRefreshType;

    /**
     * 客户端生存时间，单位：毫秒
     * <p>
     * // getfTime():15 累计冻结时间 // NowLifeTime :3 冻结鱼 // LifeTime 累计存活时间:20+15 // 鱼 20 // 2:00:00
     * <p>
     * // 15
     * <p>
     * //r时间 1 2 3冻 4冻 5... 16 18 19解 20 21 22冻 23 //fTime 0 0 0 15 16 16 16 16 16 16 16 31 //LifeTime 20 20 35 36 36 36
     * 36 36 36 36 51 51 //NowLifeTime 0 0 3 3 3 3 3 3 3 3 6 6 //fTime 0 0 15 16 16 16 16 16 16 16 31 31
     * <p>
     * // NowLifeTime => Math.max(r-f,NowLifeTime)
     * <p>
     * //目标 1 2 3 3 3 3 3 3 4 5 6 6
     * 机械迷城 获取 鱼潮 生存时间 的取负数
     */
    public float getClientLifeTime() {
        float realClientLifeTime = System.currentTimeMillis() - getCreateTime();
        return Math.max(realClientLifeTime - getFTime(), getNowLifeTime());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FishStruct struct = (FishStruct) o;
        return id == struct.id && ruleId == struct.ruleId && configId == struct.configId && routeId == struct.routeId;

    }

    @Override
    public int hashCode() {
        return Objects.hash(id, ruleId, configId, routeId);
    }
}
