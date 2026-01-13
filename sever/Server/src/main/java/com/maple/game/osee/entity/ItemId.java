package com.maple.game.osee.entity;

import java.util.HashMap;
import java.util.Map;

import com.maple.game.osee.util.MyRefreshFishingUtil;

/**
 * 游戏物品枚举类
 */
public enum ItemId {

    /**
     * 金币
     */
    MONEY(1, "金币"),

    /**
     * 银行金币
     */
    BANK_MONEY(2, "银行金币"),

    /**
     * 奖券
     */
    LOTTERY(3, "奖券"),

    /**
     * 钻石
     */
    DIAMOND(4, "钻石"),

    BRONZE_TORPEDO(5, "核弹"), // "青铜鱼雷"),

    SILVER_TORPEDO(6, "核弹"), // "白银鱼雷"),

    GOLD_TORPEDO(7, "弹头"), // "黄金鱼雷"),

    SKILL_AUTO_FIRE(9001, "自动开炮技能"),

    SKILL_DOUBLE(9002, "翻倍"),

    SKILL_LEI_MING_PO(9003, "雷鸣破"),

    SKILL_LEI_SHEN_BIAN(9004, "雷神变"),

    SKILL_TIAN_SHEN_GUAN_YU(9005, "天神关羽"),

    SKILL_LOCK(8, "锁定技能"),

    SKILL_FROZEN(9, "冰冻技能"),

    SKILL_FAST(10, "急速技能"),

    SKILL_CRIT(11, "暴击技能"),

    MONTH_CARD(12, "月卡"), // 单位为天

    BOSS_BUGLE(13, "BOSS号角"),

    QSZS_BATTERY_VIEW(14, "骑士之誓炮台外观"),

    BLNH_BATTERY_VIEW(15, "冰龙怒吼炮台外观"),

    LHTZ_BATTERY_VIEW(16, "莲花童子炮台外观"),

    SWHP_BATTERY_VIEW(17, "死亡火炮炮台外观"),

     DRAGON_CRYSTAL(18, "龙晶"),
//    DRAGON_CRYSTAL(18, "金币"),

    FEN_SHEN(19, "分身炮"),

    GOLD_TORPEDO_BANG(20, "绑定黄金核弹"), // "黄金鱼雷"),

    RARE_TORPEDO(21, "稀有核弹"), // "稀有鱼雷"),

    RARE_TORPEDO_BANG(22, "绑定稀有核弹"), // "稀有鱼雷"),

    YU_GU(23, "鱼骨"), // 鱼骨

    HAI_YAO_SHI(24, "海妖石"),

    WANG_HUN_SHI(25, "王魂石"),

    HAI_HUN_SHI(26, "海魂石"),

    ZHEN_ZHU_SHI(27, "珍珠石"),

    HAI_SHOU_SHI(28, "海兽石"),

    HAI_MO_SHI(29, "海魔石"),

    ZHAO_HUAN_SHI(30, "召唤石"),

    DIAN_CI_SHI(31, "电磁石"),

    HEI_DONG_SHI(32, "黑洞石"),

    LING_ZHU_SHI(33, "领主石"),

    LONG_GU(34, "龙骨"),

    LONG_ZHU(35, "龙珠"),

    LONG_YUAN(36, "龙元"),

    LONG_JI(37, "龙脊"),

    MAGIC_LAMP(38, "神灯"),

    VIP_CARD(39, "vip卡(v4)"), VIP_CARD8(40, "vip卡(v8)"),

    SKILL_ELETIC(50, "电磁炮技能"),

    SKILL_BLACK_HOLE(51, "黑洞炮技能"),

    SKILL_TORPEDO(52, "鱼雷炮技能"),

    BLACK_BULLET(54, "黑铁弹"),

    BRONZE_BULLET(55, "青铜弹"),

    SILVER_BULLET(56, "白银弹"),

    GOLD_BULLET(57, "黄金弹"),

    LBS_BATTERY_VIEW(63, "蓝宝石炮台外观"),

    TJZX_BATTERY_VIEW(62, "钛晶之息炮台外观"),

    HJHP_BATTERY_VIEW(61, "黄金火炮炮台外观"),

    GJZY_BATTERY_VIEW(60, "冠军之眼炮台外观"),

    HJZP_BATTERY_VIEW(59, "合金重炮炮台外观"),

    ZLHP_BATTERY_VIEW(58, "臻蓝火炮炮台外观"),

    SKILL_BIT(64, "钻头"),

    JC_OPEN_1(65, "经典渔场1海神令"),

    JC_OPEN_2(66, "经典渔场2海神令"),

    JC_OPEN_3(67, "龙晶场1海神令"),

    JC_OPEN_4(68, "龙晶场2海神令"),

    JC_OPEN_5(69, "龙晶场3海神令"), JC_OPEN_6(170, "龙晶场4海神令"), JC_OPEN_7(171, "龙晶场5海神令"),

    BATTERY_VIEW_0(70, "炮台外观默认"), BATTERY_VIEW_1(71, "电离子炮"), BATTERY_VIEW_2(72, "暗黑炮台"), BATTERY_VIEW_3(73, "武器大师"),
    BATTERY_VIEW_4(74, "青龙炮台"), BATTERY_VIEW_5(75, "金牛座炮"), BATTERY_VIEW_6(76, "红桃骑士"), BATTERY_VIEW_7(77, "财神炮台"),
    BATTERY_VIEW_8(78, "海滩派对"),

    BATTERY_VIEW_9(131, "西瓜太郎"), BATTERY_VIEW_10(132, "圣诞老人"), BATTERY_VIEW_11(133, "高达炮台"),
    BATTERY_VIEW_12(134, "海船巨炮"), BATTERY_VIEW_13(135, "貔貅炮台"),
    BATTERY_VIEW_14(151,"机甲铁手"),BATTERY_VIEW_15(152,"太空之力"), BATTERY_VIEW_16(153,"黑耀战神"),

    WING_VIEW_0(80, "翅膀外观默认"), WING_VIEW_1(81, "翅膀外观1"), WING_VIEW_2(82, "翅膀外观2"), WING_VIEW_3(83, "翅膀外观3"),
    WING_VIEW_4(84, "翅膀外观4"), WING_VIEW_5(85, "翅膀外观5"),



    DRAGON_BLESS(1000, "龙珠祝福"),

    FU_DAI_6(1001, "绿色福袋"),

    FU_DAI_30(1002, "蓝色福袋"),

    FU_DAI_128(1003, "紫色福袋"),

    FU_DAI_328(1004, "橙色福袋"),

    FU_DAI_648(1005, "红色福袋"),

    TE_HUI_6(1101, "6元特惠"),

    TE_HUI_30(1102, "30元特惠"),

    TE_HUI_98(1103, "98元特惠"),

    TE_HUI_198(1104, "198元特惠"),

    TE_HUI_328(1105, "328元特惠"),

    TE_HUI_648(1106, "648元特惠"),

    MONEY_CARD(1107, "金币卡"),

    CHANGE_FORGING_GROUP(1108, "切换材料"),

    BUY_BATTERY_LEVEL(1109, "购买炮台等级"),

    TE_HUI_680(1110, "金币特惠1"),

    TE_HUI_1980(1111, "金币特惠2"),

    TE_HUI_3180(1112, "金币特惠3"),

    TE_HUI_4680(1113, "龙晶特惠1"),

    TE_HUI_5980(1114, "龙晶特惠2"),

    TE_HUI_10000(1115, "龙晶特惠3"),

    FU_DAI_80(1130, "80钻每日礼包"),

    FU_DAI_280(1131, "280钻每日礼包"),

    FU_DAI_880(1132, "880钻每日礼包"),

    FU_DAI_1980(1133, "1980钻每日礼包"),

    FU_DAI_4680(1134, "4680钻每日礼包"),

    FU_DAI_80_1(1115, "80钻每日礼包首次购买"),

    FU_DAI_80_2(1116, "80钻每日礼包二日购买"),

    FU_DAI_80_3(1117, "80钻每日礼包三日购买"),

    FU_DAI_280_1(1118, "280钻每日礼包首日购买"),

    FU_DAI_280_2(1119, "280钻每日礼包二日购买"),

    FU_DAI_280_3(1120, "280钻每日礼包三日购买"),

    FU_DAI_880_1(1121, "880钻每日礼包首日购买"),

    FU_DAI_880_2(1122, "880钻每日礼包二日购买"),

    FU_DAI_880_3(1123, "880钻每日礼包三日购买"),

    FU_DAI_1980_1(1124, "1980钻每日礼包首日购买"),

    FU_DAI_1980_2(1125, "1980钻每日礼包二日购买"),

    FU_DAI_1980_3(1126, "1980钻每日礼包三日购买"),

    FU_DAI_4680_1(1127, "4680钻每日礼包首日购买"),

    FU_DAI_4680_2(1128, "4680钻每日礼包二日购买"),

    FU_DAI_4680_3(1129, "4680钻每日礼包三日购买"),

    ZHI_SHENG_500000(1130, "直升500000倍炮"),

    VIP_LEVEL(1131, "vip等级"),

    WAN_JIAN_JUE(1132, "万剑诀"),

    YAN_BAO_FU(1133, "炎爆符"),

    // 机械迷城 1801 开始
    LIANG_ZI_HUO_PAO(1801, "量子火炮"),  // 右边 是量子火炮

    WEI_XING_DAO_DAN(1802, "微型导弹"),  // 左边 是微型导弹

    ;

    /**
     * 物品id
     */
    private final int id;

    /**
     * 信息
     */
    private final String info;

    ItemId(int id, String info) {
        this.id = id;
        this.info = info;
    }

    public int getId() {
        return id;
    }

    public String getInfo() {
        return info;
    }

    private static final Map<Integer, ItemId> ITEM_ID_MAP = new HashMap<>();

    static {

        for (ItemId item : values()) {

            ITEM_ID_MAP.put(item.getId(), item);

        }

    }

    /**
     * 根据id获取物品枚举
     */
    public static ItemId getItemIdById(int id) {

        return ITEM_ID_MAP.get(id);

    }

    /**
     * 是否是该游戏有效道具
     */
    public static boolean isProp(int id) {

        return MyRefreshFishingUtil.FISHING_CCXX_CONFIG_MAP.values().stream()
            .allMatch(it -> it.getSkillIdList().contains(id));

    }

    /**
     * 是否是该游戏有效炮台
     */
    public static boolean isBattery(int id) {

        return MyRefreshFishingUtil.FISHING_CCXX_CONFIG_MAP.values().stream()
            .allMatch(it -> it.getBatteryIdList().contains(id));

    }

    /**
     * 是否是该游戏有效翅膀
     */
    public static boolean isWing(int id) {

        return MyRefreshFishingUtil.FISHING_CCXX_CONFIG_MAP.values().stream()
            .allMatch(it -> it.getWingIdList().contains(id));

    }

}
