package com.maple.game.osee.model.entity;

import lombok.Data;

/**
 * 2023-06-21：更新 ALTER TABLE business_log.account_detail ADD COLUMN suf_lottery BIGINT NOT NULL DEFAULT "0" COMMENT
 * "奖券（后）" AFTER game_state_name; ALTER TABLE business_log.account_detail ADD COLUMN change_lottery BIGINT NOT NULL
 * DEFAULT "0" COMMENT "奖券（变）" AFTER suf_equipment; ALTER TABLE business_log.account_detail ADD COLUMN pre_lottery
 * BIGINT NOT NULL DEFAULT "0" COMMENT "奖券（前）" AFTER change_equipment;
 *
 * <p>
 * 2023-07-01：更新 ALTER TABLE business_log.account_detail MODIFY COLUMN `changing_reason_str` STRING NOT NULL COMMENT
 * "变化原因";
 *
 * <p>
 * 2023-07-19：更新 ALTER TABLE business_log.account_detail MODIFY COLUMN `kill_fish_name` STRING NOT NULL COMMENT "击杀鱼种";
 *
 * <p>
 * 2023-08-03：更新 ALTER TABLE business_log.account_detail ADD COLUMN ccjryk VARCHAR ( 50 ) NOT NULL DEFAULT "" COMMENT
 * "场次今日盈亏" AFTER session_current_profit_and_loss;
 *
 * <p>
 * 2023-08-21：更新 ALTER TABLE business_log.account_detail ADD COLUMN slots_not_get_win_money_str VARCHAR ( 100 ) NOT NULL
 * DEFAULT "" COMMENT "slots未发放字符串，格式：所有slots场次未发放 | 当前slots场次未发放";
 *
 * <p>
 * 2023-12-07：更新 ALTER TABLE business_log.account_detail MODIFY COLUMN `individual_control_value` STRING NOT NULL
 * COMMENT "个控值";
 */

/**
 * CREATE DATABASE IF NOT EXISTS `business_log`; CREATE TABLE IF NOT EXISTS business_log.account_detail ( `id` BIGINT
 * NOT NULL COMMENT "主键 id", `user_id` BIGINT NOT NULL COMMENT "用户 id", `create_ms` BIGINT NOT NULL COMMENT "创建时间",
 * `user_name` VARCHAR ( 100 ) NOT NULL COMMENT "用户登录名", `game_code` VARCHAR ( 50 ) NOT NULL COMMENT "游戏代号：LX_001 区块链",
 * `type` INT NOT NULL COMMENT "记录类型", `type_name` VARCHAR ( 20 ) NOT NULL COMMENT "记录类型，名称", `game_state` INT NOT NULL
 * COMMENT "游戏状态", `game_state_name` VARCHAR ( 20 ) NOT NULL COMMENT "游戏状态，名称", `suf_lottery` BIGINT NOT NULL DEFAULT
 * "0" COMMENT "奖券（后）", `suf_money` BIGINT NOT NULL COMMENT "金币（后）", `suf_diamond` BIGINT NOT NULL DEFAULT "0" COMMENT
 * "钻石（后）", `suf_gold_torpedo` BIGINT NOT NULL DEFAULT "0" COMMENT "弹头（后）", `suf_skill` VARCHAR ( 500 ) NOT NULL COMMENT
 * "技能（后）", `suf_equipment` VARCHAR ( 500 ) NOT NULL COMMENT "装备（后）", `change_lottery` BIGINT NOT NULL DEFAULT "0"
 * COMMENT "奖券（变）", `change_money` BIGINT NOT NULL COMMENT "金币（变）", `change_diamond` BIGINT NOT NULL DEFAULT "0" COMMENT
 * "钻石（变）", `change_gold_torpedo` BIGINT NOT NULL DEFAULT "0" COMMENT "弹头（变）", `change_skill` VARCHAR ( 500 ) NOT NULL
 * COMMENT "技能（变）", `change_equipment` VARCHAR ( 500 ) NOT NULL COMMENT "装备（变）", `pre_lottery` BIGINT NOT NULL DEFAULT
 * "0" COMMENT "奖券（前）", `pre_money` BIGINT NOT NULL COMMENT "金币（前）", `pre_diamond` BIGINT NOT NULL DEFAULT "0" COMMENT
 * "钻石（前）", `pre_gold_torpedo` BIGINT NOT NULL DEFAULT "0" COMMENT "弹头（前）", `pre_skill` VARCHAR ( 500 ) NOT NULL COMMENT
 * "技能（前）", `pre_equipment` VARCHAR ( 500 ) NOT NULL COMMENT "装备（前）", `changing_reason_str` STRING NOT NULL COMMENT
 * "变化原因", `individual_control_value` STRING NOT NULL COMMENT "个控值", `kill_fish_name` STRING NOT NULL COMMENT "击杀鱼种",
 * `node_info_str` VARCHAR ( 50 ) NOT NULL COMMENT "节点", `session_profit_and_loss` VARCHAR ( 50 ) NOT NULL COMMENT
 * "场次盈亏：记录此刻玩家在该场次的累计盈亏值", `ccjryk` VARCHAR ( 50 ) NOT NULL DEFAULT "" COMMENT "场次今日盈亏",
 * `session_current_profit_and_loss` VARCHAR ( 50 ) NOT NULL COMMENT "进场盈亏：记录该玩家从进入该场次到此刻的累计盈亏值", `all_profit_and_loss`
 * VARCHAR ( 50 ) NOT NULL COMMENT "总盈亏：记录该玩家在此刻在所有房间内的总盈亏值", `session_current_consume` VARCHAR ( 50 ) NOT NULL COMMENT
 * "场次：当前消耗：击杀上一条鱼后，开始统计，击杀鱼后清零。（包含期间攻击所有目标的消耗金币，以场次为单位）", `slots_not_get_win_money_str` VARCHAR ( 100 ) NOT NULL
 * DEFAULT "" COMMENT "slots未发放字符串，格式：所有slots场次未发放 | 当前slots场次未发放" ) DISTRIBUTED BY HASH ( `id` ) PROPERTIES (
 * "replication_allocation" = "tag.location.default: 1" );
 * <p>
 * <p>
 * SET PASSWORD = PASSWORD('Lxjj@2022'); DROP USER 'admin'@'%'; SET GLOBAL max_allowed_packet = 999999999; SET GLOBAL
 * exec_mem_limit = 137438953472;
 */
@Data
public class AccountDetailDO {

    /**
     * 主键 id
     */
    private Long id;

    /**
     * 用户 id
     */
    private Long userId;

    /**
     * 用户登录名
     */
    private String userName;

    /**
     * 创建时间的时间戳
     */
    private Long createMs;

    /**
     * 游戏代号
     */
    private String gameCode = "";

    /**
     * 记录类型
     */
    private Integer type;

    /**
     * 记录类型，名称
     */
    private String typeName;

    /**
     * 游戏状态
     */
    private Integer gameState;

    /**
     * 游戏状态，名称
     */
    private String gameStateName;

    /**
     * 奖券（后）
     */
    private Long sufLottery;

    /**
     * 金币（后）
     */
    private Long sufMoney;

    /**
     * 钻石（后）
     */
    private Long sufDiamond;

    /**
     * 弹头（后）
     */
    private Long sufGoldTorpedo;

    /**
     * 技能（后）
     */
    private String sufSkill;

    /**
     * 装备（后）
     */
    private String sufEquipment;

    /**
     * 奖券（变）
     */
    private Long changeLottery;

    /**
     * 金币（变）
     */
    private Long changeMoney;

    /**
     * 钻石（变）
     */
    private Long changeDiamond;

    /**
     * 弹头（变）
     */
    private Long changeGoldTorpedo;

    /**
     * 技能（变）
     */
    private String changeSkill;

    /**
     * 装备（变）
     */
    private String changeEquipment;

    /**
     * 奖券（前）
     */
    private Long preLottery;

    /**
     * 金币（前）
     */
    private Long preMoney;

    /**
     * 钻石（前）
     */
    private Long preDiamond;

    /**
     * 弹头（前）
     */
    private Long preGoldTorpedo;

    /**
     * 技能（前）
     */
    private String preSkill;

    /**
     * 装备（前）
     */
    private String preEquipment;

    /**
     * 变化原因
     */
    private String changingReasonStr;

    /**
     * 个控值
     */
    private String individualControlValue;

    /**
     * 击杀鱼种 slots组合
     */
    private String killFishName;

    /**
     * 节点
     */
    private String nodeInfoStr;

    /**
     * 场次盈亏：记录此刻玩家在该场次的累计盈亏值
     */
    private String sessionProfitAndLoss;

    /**
     * 场次今日盈亏
     */
    private String ccjryk;

    /**
     * 进场盈亏：记录该玩家从进入该场次到此刻的累计盈亏值，退房间时清零，进房间开始计算
     */
    private String sessionCurrentProfitAndLoss;

    /**
     * 总盈亏：记录该玩家在此刻在所有房间内的总盈亏值
     */
    private String allProfitAndLoss;

    /**
     * 场次：当前消耗：击杀上一条鱼后，开始统计，击杀鱼后清零。（包含期间攻击所有目标的消耗金币，以场次为单位） 消耗：中奖后，开始统计，再次中奖后清零。（包含期间旋转消耗金币，以场次为单位）
     */
    private String sessionCurrentConsume;

    /**
     * slots未发放字符串，格式：所有slots场次未发放 | 当前slots场次未发放
     */
    private String slotsNotGetWinMoneyStr;

}
