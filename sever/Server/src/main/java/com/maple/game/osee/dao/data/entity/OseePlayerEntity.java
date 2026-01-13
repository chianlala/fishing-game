package com.maple.game.osee.dao.data.entity;

import java.time.LocalDate;
import java.util.Date;

import com.maple.database.data.DbEntity;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * 玩家实体类
 */
@Getter
@Setter
@Slf4j
public class OseePlayerEntity extends DbEntity {

    private static final long serialVersionUID = 6524085625364080223L;

    /**
     * 累计获得的龙晶
     */
    private long totalDragonCrystal = 0L;

    /**
     * 发射使用的子弹
     */
    private long useBattery = 0L;

    /**
     * 玩家神灯数量
     */
    private long magicLamp;
    private long thousandSword;
    /**
     * 玩家vip卡数量
     */
    private long vipCard;
    /**
     * 实体id
     */
    public static final String EntityId = "ttmy";

    /**
     * 玩家id
     */
    private long userId;

    /**
     * 金币
     */
    private long money;

    /**
     * 保险箱金币
     */
    private long bankMoney;

    /**
     * 保险箱密码
     */
    private String bankPassword = "e10adc3949ba59abbe56e057f20f883e";

    /**
     * 奖券
     */
    private long lottery;

    /**
     * 钻石
     */
    private long diamond;

    /**
     * vip等级
     */
    private int vipLevel = 0;

    /**
     * 玩家等级
     */
    private int level = 1;

    /**
     * 玩家经验
     */
    private long experience;

    /**
     * 充值金额
     */
    private long rechargeMoney;

    /**
     * 必输控制标识
     */
    private int loseControl;

    /**
     *
     */
    private int playerType;

    /**
     * 青铜鱼雷
     */
    private long bronzeTorpedo;

    /**
     * 白银鱼雷
     */
    private long silverTorpedo;

    /**
     * 黄金鱼雷
     */
    private long goldTorpedo;

    /**
     * 锁定技能
     */
    private long skillLock;

    /**
     * 冰冻技能
     */
    private long skillFrozen;

    /**
     * 急速技能
     */
    private long skillFast;

    /**
     * 翻倍技能
     */
    private long skillDouble;

    /**
     * 暴击技能
     */
    private long skillCrit;

    /**
     * 电磁炮技能
     */
    private long skillEle;

    /**
     * boss号角
     */
    private long bossBugle;

    /**
     * 玩家目前所拥有的最高炮台等级
     */
    private long batteryLevel;

    /**
     * 月卡到期时间
     */
    private LocalDate monthCardExpireDate = LocalDate.now();

    /**
     * 玩家剩余拼十挑战次数
     */
    private long tenChallengeTimes;

    /**
     * 骑士之誓炮台外观
     */
    private LocalDate qszsBatteryExpireDate = LocalDate.now();

    /**
     * 冰龙怒吼炮台外观
     */
    private LocalDate blnhBatteryExpireDate = LocalDate.now();

    /**
     * 莲花童子炮台外观
     */
    private LocalDate lhtzBatteryExpireDate = LocalDate.now();

    /**
     * 死亡火炮炮台外观
     */
    private LocalDate swhpBatteryExpireDate = LocalDate.now();

    /**
     * 蓝宝石炮台外观
     */
    private LocalDate lbsBatteryExpireDate = LocalDate.now();

    /**
     * 钛晶之息炮台外观
     */
    private LocalDate tjzxBatteryExpireDate = LocalDate.now();

    /**
     * 黄金火炮炮台外观
     */
    private LocalDate hjhpBatteryExpireDate = LocalDate.now();

    /**
     * 冠军之眼炮台外观
     */
    private LocalDate gjzyBatteryExpireDate = LocalDate.now();

    /**
     * 合金重炮炮台外观
     */
    private LocalDate hjzpBatteryExpireDate = LocalDate.now();

    /**
     * 臻蓝火炮炮台外观
     */
    private LocalDate zlhpBatteryExpireDate = LocalDate.now();

    /**
     * 天神关羽过期时间  改这个和数据库g_shop_props 表中 商品时间挫 就好了
     */
    private LocalDate tianShenGuanYuBatteryExpireDate = LocalDate.now().plusDays(1);

    /**
     * 龙晶数量
     */
    private long dragonCrystal;

    public long getDragonCrystal() {

        // if (dragonCrystal == 0 && !FishingRobotUtil.robotFlag(getId()) && getId() != 100001) {
        //
        // StackTraceElement stackArr[] = Thread.currentThread().getStackTrace();
        //
        // log.info("龙晶变化-get，userId：{}，堆栈：{}", getId(), JSONUtil.toJsonStr(stackArr));
        //
        // }

        return dragonCrystal;

    }

    public void setDragonCrystal(long dragonCrystal) {

        // if (dragonCrystal == 0 && !FishingRobotUtil.robotFlag(getId()) && getId() != 100001) {
        //
        // StackTraceElement stackArr[] = Thread.currentThread().getStackTrace();
        //
        // log.info("龙晶变化-set，userId：{}，堆栈：{}", getId(), JSONUtil.toJsonStr(stackArr));
        //
        // }

        this.dragonCrystal = dragonCrystal;

    }

    /**
     * 分身炮道具
     */
    private long fenShen;

    /**
     */
    private long sendGift;

    /**
     * 玩家绑定黄金鱼雷数量
     */
    private long goldTorpedoBang;

    /**
     * 玩家绑定稀有鱼雷数量
     */
    private long rareTorpedoBang;

    /**
     * 玩家稀有鱼雷数量
     */
    private long rareTorpedo;

    /**
     * 玩家鱼骨数量
     */
    private long yuGu;

    /**
     * 玩家海妖石数量
     */
    private long haiYaoShi;

    /**
     * 玩家王魂石数量
     */
    private long wangHunShi;

    /**
     * 玩家海魂石数量
     */
    private long haiHunShi;

    /**
     * 玩家珍珠石数量
     */
    private long zhenZhuShi;

    /**
     * 玩家海兽石数量
     */
    private long haiShouShi;

    /**
     * 玩家海魔石数量
     */
    private long haiMoShi;

    /**
     * 玩家召唤石数量
     */
    private long zhaoHuanShi;

    /**
     * 玩家电磁石数量
     */
    private long dianChiShi;

    /**
     * 玩家黑洞石数量
     */
    private long heiDongShi;

    /**
     * 玩家领主石数量
     */
    private long lingZhuShi;

    /**
     * 玩家龙骨数量
     */
    private long longGu;

    /**
     * 玩家龙珠数量
     */
    private long longZhu;

    /**
     * 玩家龙元数量
     */
    private long longYuan;

    /**
     * 玩家龙脊数量
     */
    private long longJi;

    /**
     * 玩家黑洞炮数量
     */
    private long skillBlackHole;

    /**
     * 玩家鱼雷炮数量
     */
    private long skillTorpedo;

    /**
     */
    private long sendCard;

    /**
     * 玩家黑铁弹数量
     */
    private long blackBullet;

    /**
     * 玩家青铜弹数量
     */
    private long bronzeBullet;

    /**
     * 玩家白银弹数量
     */
    private long silverBullet;

    /**
     * 玩家黄金弹数量
     */
    private long goldBullet;

    /**
     * 玩家钻头数量
     */
    private long skillBit;

    /**
     * 玩家 ip
     */
    private String userIp;

    /**
     * 最后一次登录游戏的时间
     */
    private Date lastSignInTime;

    /**
     * 最后一次加入房间的时间
     */
    private Date lastJoinRoomTime;

}
