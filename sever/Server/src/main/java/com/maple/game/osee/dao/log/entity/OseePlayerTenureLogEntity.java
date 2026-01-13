package com.maple.game.osee.dao.log.entity;

import com.maple.database.data.DbEntity;
import lombok.Getter;
import lombok.Setter;

/**
 * 用户账户变动日志
 */
@Getter
@Setter
public class OseePlayerTenureLogEntity extends DbEntity {

    private static final long serialVersionUID = 3928922029028562872L;

    /**
     * 用户id
     */
    private long userId;

    /**
     * 用户昵称
     */
    private String nickname;

    /**
     * 变动来源
     */
    private int reason;

    /**
     * 变动前钻石
     */
    private long preDiamond;

    /**
     * 变动钻石
     */
    private long changeDiamond;

    /**
     * 变动前金币
     */
    private long preMoney;

    /**
     * 变动金币
     */
    private long changeMoney;

    /**
     * 变动前奖券
     */
    private long preLottery;

    /**
     * 变动奖券
     */
    private long changeLottery;

    /**
     * 变动前保险箱金币
     */
    private long preBankMoney;

    /**
     * 变动保险箱金币
     */
    private long changeBankMoney;

    /**
     * 变动前青铜鱼雷数量
     */
    private long preBronzeTorpedo;

    /**
     * 变动的青铜鱼雷数量
     */
    private long changeBronzeTorpedo;

    /**
     * 变动前白银鱼雷数量
     */
    private long preSilverTorpedo;

    /**
     * 变动的白银鱼雷数量
     */
    private long changeSilverTorpedo;

    /**
     * 变动前黄金鱼雷数量
     */
    private long preGoldTorpedo;

    /**
     * 变动的黄金鱼雷数量
     */
    private long changeGoldTorpedo;

    /**
     * 变动前黄金鱼雷数量
     */
    private long preRareTorpedo;

    /**
     * 变动的黄金鱼雷数量
     */
    private long changeRareTorpedo;

    /**
     * 变动前稀有鱼雷数量
     */
    private long preRareTorpedoBang;

    /**
     * 变动的稀有鱼雷数量
     */
    private long changeRareTorpedoBang;

    /**
     * 锁定技能变动
     */
    private long preSkillLock;
    private long changeSkillLock;

    /**
     * 冰冻技能变动
     */
    private long preSkillFrozen;
    private long changeSkillFrozen;

    /**
     * 冰冻技能变动
     */
    private long preSkillEleTic;
    private long changeSkillEleTic;

    /**
     * 急速技能变动
     */
    private long preSkillFast;
    private long changeSkillFast;

    /**
     * 翻倍技能变动
     */
    private long preSkillDouble;
    private long changeSkillDouble;

    /**
     * 暴击技能变动
     */
    private long preSkillCrit;
    private long changeSkillCrit;

    /**
     * boss号角变动
     */
    private long preBossBugle;
    private long changeBossBugle;

    /**
     * 龙晶变动
     */
    private long preDragonCrystal;
    private long changeDragonCrystal;

    /**
     * 附加值
     */
    private String extraData;

}
