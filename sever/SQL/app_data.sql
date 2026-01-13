/*
 Navicat Premium Dump SQL

 Source Server         : localhost
 Source Server Type    : MySQL
 Source Server Version : 80406 (8.4.6)
 Source Host           : localhost:3306
 Source Schema         : app_data

 Target Server Type    : MySQL
 Target Server Version : 80406 (8.4.6)
 File Encoding         : 65001

 Date: 13/01/2026 15:46:31
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for fruitlabarewardinfo
-- ----------------------------
DROP TABLE IF EXISTS `fruitlabarewardinfo`;
CREATE TABLE `fruitlabarewardinfo`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '奖励id',
  `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `user_id` bigint NULL DEFAULT NULL COMMENT '用户id',
  `achieve_num` int NULL DEFAULT NULL COMMENT '旋转次数',
  `reward_gold` int NULL DEFAULT NULL COMMENT '奖励金币',
  `reward_lottery` int NULL DEFAULT NULL COMMENT '奖励点券',
  `weather_receive` tinyint(1) NULL DEFAULT NULL COMMENT '是否领取',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of fruitlabarewardinfo
-- ----------------------------

-- ----------------------------
-- Table structure for g_log_room
-- ----------------------------
DROP TABLE IF EXISTS `g_log_room`;
CREATE TABLE `g_log_room`  (
  `id` int UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '序号',
  `u_id` int NULL DEFAULT NULL COMMENT '用户id',
  `r_type` int NULL DEFAULT NULL COMMENT '房间类型',
  `join_time` datetime NULL DEFAULT NULL COMMENT '加入时间',
  `exit_time` datetime NULL DEFAULT NULL COMMENT '退出时间',
  `time_spending` bigint NULL DEFAULT NULL COMMENT '停留时间',
  `use_battery` bigint NULL DEFAULT NULL COMMENT '开炮使用的子弹',
  `total_dragon_crystal` bigint NULL DEFAULT NULL COMMENT '总的击杀掉落的龙晶',
  `use_props` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '使用的道具',
  `enter_dragon_crystal` bigint NULL DEFAULT NULL COMMENT '进入房间时，携带的龙晶',
  `out_dragon_crystal` bigint NULL DEFAULT NULL COMMENT '退出房间时，携带的龙晶',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of g_log_room
-- ----------------------------

-- ----------------------------
-- Table structure for g_shop_props
-- ----------------------------
DROP TABLE IF EXISTS `g_shop_props`;
CREATE TABLE `g_shop_props`  (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '序号',
  `name` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '商品名称',
  `props_id` int NULL DEFAULT NULL COMMENT '商品id',
  `quantity` int NULL DEFAULT NULL COMMENT '商品数量',
  `currency` int NULL DEFAULT NULL COMMENT '币种类型',
  `price` int NULL DEFAULT NULL COMMENT '商品价格',
  `status` int NULL DEFAULT NULL COMMENT '商品状态 0-下架 1-在售',
  `first_give` float(3, 2) NULL DEFAULT NULL COMMENT '首次购买额外赠送比例',
  `follow_up_give` float(3, 2) NULL DEFAULT NULL COMMENT '后续购买额外赠送比例',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 45006 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of g_shop_props
-- ----------------------------
INSERT INTO `g_shop_props` VALUES (10002, '钻石', 4, 100, 7, 1, 1, 0.00, 0.00);
INSERT INTO `g_shop_props` VALUES (10003, '钻石', 4, 300, 7, 30, 0, 0.00, 0.00);
INSERT INTO `g_shop_props` VALUES (10004, '钻石', 4, 600, 7, 60, 0, 0.00, 0.00);
INSERT INTO `g_shop_props` VALUES (10005, '钻石', 4, 1000, 7, 10, 1, 0.00, 0.00);
INSERT INTO `g_shop_props` VALUES (10006, '钻石', 4, 5000, 7, 50, 1, 0.00, 0.00);
INSERT INTO `g_shop_props` VALUES (10007, '钻石', 4, 10000, 7, 100, 1, 0.00, 0.00);
INSERT INTO `g_shop_props` VALUES (10008, '钻石', 4, 50000, 7, 5000, 1, 0.00, 0.00);
INSERT INTO `g_shop_props` VALUES (11009, '金币', 1, 560000, 4, 80, 0, 1.00, 0.00);
INSERT INTO `g_shop_props` VALUES (11010, '金币', 1, 1260000, 4, 180, 0, 1.00, 0.00);
INSERT INTO `g_shop_props` VALUES (11011, '金币', 1, 4760000, 4, 680, 0, 1.00, 0.00);
INSERT INTO `g_shop_props` VALUES (11012, '金币', 1, 8960000, 4, 1280, 0, 1.00, 0.00);
INSERT INTO `g_shop_props` VALUES (11013, '金币', 1, 18760000, 4, 2680, 0, 1.00, 0.10);
INSERT INTO `g_shop_props` VALUES (11014, '金币', 1, 34650000, 4, 4950, 0, 1.00, 0.20);
INSERT INTO `g_shop_props` VALUES (11015, '金币', 1, 39760000, 4, 5680, 0, 1.00, 0.30);
INSERT INTO `g_shop_props` VALUES (11016, '金币', 1, 48860000, 4, 6980, 0, 1.00, 0.30);
INSERT INTO `g_shop_props` VALUES (12017, '锁定', 8, 50, 4, 50, 1, 0.00, 0.00);
INSERT INTO `g_shop_props` VALUES (12018, '冰冻', 9, 50, 4, 50, 1, 0.00, 0.00);
INSERT INTO `g_shop_props` VALUES (12019, '神灯', 38, 50, 4, 50, 1, 0.00, 0.00);
INSERT INTO `g_shop_props` VALUES (12020, '狂暴', 11, 50, 4, 50, 1, 0.00, 0.00);
INSERT INTO `g_shop_props` VALUES (12021, '号角', 13, 50, 4, 50, 1, 0.00, 0.00);
INSERT INTO `g_shop_props` VALUES (12022, 'VIP4', 39, 1, 7, 10, 0, 0.00, 0.00);
INSERT INTO `g_shop_props` VALUES (12023, 'VIP8', 40, 1, 7, 20, 0, 0.00, 0.00);
INSERT INTO `g_shop_props` VALUES (12024, '急速', 10, 50, 4, 50, 1, 0.00, 0.00);
INSERT INTO `g_shop_props` VALUES (12025, '分身', 19, 50, 4, 50, 1, 0.00, 0.00);
INSERT INTO `g_shop_props` VALUES (12026, '电磁炮', 50, 50, 4, 50, 1, 0.00, 0.00);
INSERT INTO `g_shop_props` VALUES (12027, '翻倍', 9002, 50, 4, 50, 1, 0.00, 0.00);
INSERT INTO `g_shop_props` VALUES (12028, '雷鸣破', 9003, 259200, 4, 100, 1, 0.00, 0.00);
INSERT INTO `g_shop_props` VALUES (12029, '雷神变', 9004, 259200, 4, 100, 1, 0.00, 0.00);
INSERT INTO `g_shop_props` VALUES (12030, '天神关羽', 9005, 259200, 4, 100, 1, 0.00, 0.00);
INSERT INTO `g_shop_props` VALUES (13021, '炮台1', 71, 259200, 4, 50, 0, 0.00, 0.00);
INSERT INTO `g_shop_props` VALUES (13022, '炮台2', 72, 259200, 4, 80, 0, 0.00, 0.00);
INSERT INTO `g_shop_props` VALUES (13023, '炮台3', 73, 259200, 4, 10000, 0, 0.00, 0.00);
INSERT INTO `g_shop_props` VALUES (13024, '炮台4', 74, 259200, 4, 10000, 0, 0.00, 0.00);
INSERT INTO `g_shop_props` VALUES (13025, '炮台5', 75, 259200, 4, 50, 1, 0.00, 0.00);
INSERT INTO `g_shop_props` VALUES (13026, '翅膀2', 82, 259200, 4, 50, 1, 0.00, 0.00);
INSERT INTO `g_shop_props` VALUES (13027, '翅膀3', 83, 259200, 4, 50, 1, 0.00, 0.00);
INSERT INTO `g_shop_props` VALUES (13028, '翅膀4', 84, 259200, 4, 50, 1, 0.00, 0.00);
INSERT INTO `g_shop_props` VALUES (13029, '翅膀5', 85, 259200, 4, 50, 1, 0.00, 0.00);
INSERT INTO `g_shop_props` VALUES (13030, '炮台6', 76, 259200, 4, 50, 1, 0.00, 0.00);
INSERT INTO `g_shop_props` VALUES (13031, '炮台7', 77, 259200, 4, 50, 1, 0.00, 0.00);
INSERT INTO `g_shop_props` VALUES (13032, '炮台8', 78, 259200, 4, 50, 1, 0.00, 0.00);
INSERT INTO `g_shop_props` VALUES (13033, '翅膀1', 81, 259200, 4, 50, 1, 0.00, 0.00);
INSERT INTO `g_shop_props` VALUES (13034, '海船巨炮', 134, 259200, 4, 50, 1, 0.00, 0.00);
INSERT INTO `g_shop_props` VALUES (13035, '貔貅炮台', 135, 259200, 4, 50, 1, 0.00, 0.00);
INSERT INTO `g_shop_props` VALUES (14001, '弹头', 7, 1, 18, 200000, 1, 0.00, 0.00);
INSERT INTO `g_shop_props` VALUES (14002, '弹头', 7, 10, 18, 2000000, 1, 0.00, 0.00);
INSERT INTO `g_shop_props` VALUES (14003, '弹头', 7, 50, 18, 10000000, 1, 0.00, 0.00);
INSERT INTO `g_shop_props` VALUES (14004, '弹头', 7, 100, 18, 20000000, 1, 0.00, 0.00);
INSERT INTO `g_shop_props` VALUES (14005, '弹头', 7, 500, 18, 100000000, 1, 0.00, 0.00);
INSERT INTO `g_shop_props` VALUES (14006, '弹头', 7, 1000, 18, 200000000, 1, 0.00, 0.00);
INSERT INTO `g_shop_props` VALUES (14999, '弹头', 7, -1, 18, -1, 1, 0.00, 0.00);
INSERT INTO `g_shop_props` VALUES (15001, '龙晶', 18, 200000, 7, 1, 1, 0.00, 0.00);
INSERT INTO `g_shop_props` VALUES (15002, '龙晶', 18, 2000000, 7, 10, 1, 0.00, 0.00);
INSERT INTO `g_shop_props` VALUES (15003, '龙晶', 18, 10000000, 7, 50, 1, 0.00, 0.00);
INSERT INTO `g_shop_props` VALUES (15004, '龙晶', 18, 20000000, 7, 100, 1, 0.00, 0.00);
INSERT INTO `g_shop_props` VALUES (15005, '龙晶', 18, 100000000, 7, 500, 1, 0.00, 0.00);
INSERT INTO `g_shop_props` VALUES (15006, '龙晶', 18, 200000000, 7, 1000, 1, 0.00, 0.00);
INSERT INTO `g_shop_props` VALUES (15999, '龙晶', 18, -1, 7, -1, 1, 0.00, 0.00);
INSERT INTO `g_shop_props` VALUES (20001, '钻石', 4, 100, 7, 1, 1, 0.00, 0.00);
INSERT INTO `g_shop_props` VALUES (20002, '钻石', 4, 100, 7, 1, 1, 0.00, 0.00);
INSERT INTO `g_shop_props` VALUES (20003, '钻石', 4, 300, 7, 30, 0, 0.00, 0.00);
INSERT INTO `g_shop_props` VALUES (20004, '钻石', 4, 600, 7, 60, 0, 0.00, 0.00);
INSERT INTO `g_shop_props` VALUES (20005, '钻石', 4, 1000, 7, 10, 1, 0.00, 0.00);
INSERT INTO `g_shop_props` VALUES (20006, '钻石', 4, 5000, 7, 50, 1, 0.00, 0.00);
INSERT INTO `g_shop_props` VALUES (20007, '钻石', 4, 10000, 7, 100, 1, 0.00, 0.00);
INSERT INTO `g_shop_props` VALUES (25001, '龙晶', 18, 12000, 4, 20, 1, 0.00, 0.00);
INSERT INTO `g_shop_props` VALUES (25002, '龙晶', 18, 66000, 4, 60, 1, 0.00, 0.00);
INSERT INTO `g_shop_props` VALUES (25003, '龙晶', 18, 354000, 4, 300, 1, 0.00, 0.00);
INSERT INTO `g_shop_props` VALUES (25004, '龙晶', 18, 1170000, 4, 980, 1, 0.00, 0.00);
INSERT INTO `g_shop_props` VALUES (25005, '龙晶', 18, 2370000, 4, 1980, 1, 0.00, 0.00);
INSERT INTO `g_shop_props` VALUES (25006, '龙晶', 18, 3940000, 4, 3280, 1, 0.00, 0.00);
INSERT INTO `g_shop_props` VALUES (25007, '龙晶', 18, 8800000, 4, 6480, 1, 0.00, 0.00);
INSERT INTO `g_shop_props` VALUES (25008, '龙晶', 18, 14000000, 4, 10000, 1, 0.00, 0.00);
INSERT INTO `g_shop_props` VALUES (35001, '龙晶', 18, 88000, 4, 80, 1, 0.00, 0.00);
INSERT INTO `g_shop_props` VALUES (35002, '龙晶', 18, 360000, 4, 300, 1, 0.00, 0.00);
INSERT INTO `g_shop_props` VALUES (35003, '龙晶', 18, 1380000, 4, 980, 1, 0.00, 0.00);
INSERT INTO `g_shop_props` VALUES (35004, '龙晶', 18, 2800000, 4, 1980, 1, 0.00, 0.00);
INSERT INTO `g_shop_props` VALUES (35005, '龙晶', 18, 4660000, 4, 3280, 1, 0.00, 0.00);
INSERT INTO `g_shop_props` VALUES (35006, '龙晶', 18, 9400000, 4, 6480, 1, 0.00, 0.00);
INSERT INTO `g_shop_props` VALUES (45001, '龙晶', 18, 200000, 7, 1, 1, 0.00, 0.00);
INSERT INTO `g_shop_props` VALUES (45002, '龙晶', 18, 2000000, 7, 10, 1, 0.00, 0.00);
INSERT INTO `g_shop_props` VALUES (45003, '龙晶', 18, 20000000, 7, 100, 1, 0.00, 0.00);
INSERT INTO `g_shop_props` VALUES (45004, '龙晶', 18, 100000000, 7, 500, 1, 0.00, 0.00);
INSERT INTO `g_shop_props` VALUES (45005, '龙晶', 18, 400000000, 7, 2000, 1, 0.00, 0.00);

-- ----------------------------
-- Table structure for g_user_props
-- ----------------------------
DROP TABLE IF EXISTS `g_user_props`;
CREATE TABLE `g_user_props`  (
  `user_id` int NOT NULL COMMENT '用户id',
  `props_id` int NOT NULL COMMENT '物品id',
  `quantity` bigint NULL DEFAULT NULL COMMENT '物品数量/时间戳，备注：时间戳为 -100时，表示是永久',
  PRIMARY KEY (`user_id`, `props_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of g_user_props
-- ----------------------------

-- ----------------------------
-- Table structure for t_osee_golden_pig_record
-- ----------------------------
DROP TABLE IF EXISTS `t_osee_golden_pig_record`;
CREATE TABLE `t_osee_golden_pig_record`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `userId` bigint NULL DEFAULT NULL,
  `nickName` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '用户名',
  `preLottery` bigint NULL DEFAULT NULL COMMENT '使用前奖券',
  `num` bigint NULL DEFAULT NULL COMMENT '消耗核弹数量',
  `changeLottery` bigint NULL DEFAULT NULL COMMENT '变化奖券',
  `afterLottery` bigint NULL DEFAULT NULL COMMENT '使用后奖券',
  `createTime` timestamp NULL DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of t_osee_golden_pig_record
-- ----------------------------

-- ----------------------------
-- Table structure for tbl_app_rate_form
-- ----------------------------
DROP TABLE IF EXISTS `tbl_app_rate_form`;
CREATE TABLE `tbl_app_rate_form`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `day_time` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `rate` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '0.00' COMMENT '付费率',
  `arppu` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '0.00' COMMENT 'arppu',
  `arpu` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '0.00' COMMENT 'arpu',
  `new_rate` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '0.00' COMMENT '新增付费率',
  `new_arppu` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '0.00' COMMENT '新增arppu',
  `new_arpu` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '0.00' COMMENT '新增arpu',
  `new_pay_num` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '0' COMMENT '新增付费人数',
  `login_num` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '0' COMMENT '登陆人数',
  `pay_num` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '0' COMMENT '付费人数',
  `all_money` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '0' COMMENT '总金额',
  `agent_id` bigint NOT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of tbl_app_rate_form
-- ----------------------------

-- ----------------------------
-- Table structure for tbl_app_report_form
-- ----------------------------
DROP TABLE IF EXISTS `tbl_app_report_form`;
CREATE TABLE `tbl_app_report_form`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `day_time` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `day1` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '0.00',
  `day2` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '0.00',
  `day3` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '0.00',
  `day4` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '0.00',
  `day5` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '0.00',
  `day6` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '0.00',
  `day7` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '0.00',
  `day8` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '0.00',
  `day9` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '0.00',
  `day10` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '0.00',
  `day11` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '0.00',
  `day12` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '0.00',
  `day13` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '0.00',
  `day14` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '0.00',
  `day15` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '0.00',
  `day16` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '0.00',
  `day17` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '0.00',
  `day18` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '0.00',
  `day19` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '0.00',
  `day20` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '0.00',
  `day21` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '0.00',
  `day22` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '0.00',
  `day23` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '0.00',
  `day24` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '0.00',
  `day25` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '0.00',
  `day26` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '0.00',
  `day27` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '0.00',
  `day28` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '0.00',
  `day29` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '0.00',
  `day30` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '0.00',
  `type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '类型：register（注册）  pay(付费）',
  `agent_id` bigint NOT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of tbl_app_report_form
-- ----------------------------

-- ----------------------------
-- Table structure for tbl_app_user
-- ----------------------------
DROP TABLE IF EXISTS `tbl_app_user`;
CREATE TABLE `tbl_app_user`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '用户id',
  `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '注册时间',
  `username` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '用户名',
  `phonenum` varchar(11) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '手机号',
  `openid` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '第三方平台id',
  `unionid` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '第三方平台唯一id',
  `password` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '密码(32位md5)',
  `nickname` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '昵称',
  `head_index` int NULL DEFAULT NULL COMMENT '头像序号',
  `head_url` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '头像地址',
  `sex` int NULL DEFAULT NULL COMMENT '性别',
  `user_state` int NOT NULL DEFAULT 0 COMMENT '用户状态',
  `online_state` int NULL DEFAULT 0 COMMENT '在线状态',
  `name` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '真实姓名',
  `idcard_no` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '身份证号码',
  `send_password` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '密码(32位md5)',
  `game_id` bigint NULL DEFAULT NULL COMMENT '游戏 id',
  `last_sign_in_time` datetime NULL DEFAULT NULL COMMENT '最后一次登录游戏的时间',
  `bio` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '个性签名',
  `my_invite_code` bigint NULL DEFAULT NULL,
  `invite_code` bigint NULL DEFAULT NULL,
  PRIMARY KEY (`id`, `user_state`) USING BTREE,
  UNIQUE INDEX `username`(`username` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 20024 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of tbl_app_user
-- ----------------------------
INSERT INTO `tbl_app_user` VALUES (20000, '2024-12-04 11:05:41', 'AA', '', '', '', 'e10adc3949ba59abbe56e057f20f883e', 'AA', 1, '', 0, 0, 1102, NULL, NULL, NULL, 961670, NULL, NULL, 0, 0);

-- ----------------------------
-- Table structure for tbl_app_user_authentication
-- ----------------------------
DROP TABLE IF EXISTS `tbl_app_user_authentication`;
CREATE TABLE `tbl_app_user_authentication`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '认证id',
  `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '认证时间',
  `user_id` bigint NOT NULL COMMENT '用户id',
  `name` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '真实姓名',
  `idcard_no` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '身份证号码',
  `phone_no` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '手机号码',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of tbl_app_user_authentication
-- ----------------------------

-- ----------------------------
-- Table structure for tbl_app_wander_subtitle
-- ----------------------------
DROP TABLE IF EXISTS `tbl_app_wander_subtitle`;
CREATE TABLE `tbl_app_wander_subtitle`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '游走字幕id',
  `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `content` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '字幕内容',
  `interval_time` int NULL DEFAULT NULL COMMENT '间隔时间',
  `start_time` timestamp NULL DEFAULT NULL COMMENT '生效时间',
  `end_time` timestamp NULL DEFAULT NULL COMMENT '失效时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of tbl_app_wander_subtitle
-- ----------------------------

-- ----------------------------
-- Table structure for tbl_change_all_log
-- ----------------------------
DROP TABLE IF EXISTS `tbl_change_all_log`;
CREATE TABLE `tbl_change_all_log`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `loginNum` bigint NULL DEFAULT NULL COMMENT '活跃人数',
  `goldAll` bigint NULL DEFAULT NULL COMMENT '龙珠数量',
  `bossAll` bigint NULL DEFAULT NULL COMMENT '号角数量',
  `critAll` bigint NULL DEFAULT NULL COMMENT '暴击数量',
  `lockAll` bigint NULL DEFAULT NULL COMMENT '锁定数量',
  `magicAll` bigint NULL DEFAULT NULL COMMENT '神灯数量',
  `diamondAll` bigint NULL DEFAULT NULL COMMENT '钻石数量',
  `frozenAll` bigint NULL DEFAULT NULL COMMENT '冰冻数量',
  `moneyChange` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '金币变化',
  `dragonChange` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '龙晶变化',
  `create_time` timestamp NULL DEFAULT NULL COMMENT '创建时间',
  `yuGuAll` bigint NULL DEFAULT NULL COMMENT '鱼骨数量',
  `haiShouShiAll` bigint NULL DEFAULT NULL COMMENT '海兽石数量',
  `haiHunShiAll` bigint NULL DEFAULT NULL COMMENT '海魂石数量',
  `haiMoShiAll` bigint NULL DEFAULT NULL COMMENT '海魔石数量',
  `haiYaoShiAll` bigint NULL DEFAULT NULL COMMENT '海妖石数量',
  `dianCiShiAll` bigint NULL DEFAULT NULL COMMENT '电磁石数量',
  `heiDongShiAll` bigint NULL DEFAULT NULL COMMENT '黑洞石数量',
  `lingZhuShiAll` bigint NULL DEFAULT NULL COMMENT '领主石数量',
  `longGuAll` bigint NULL DEFAULT NULL COMMENT '龙骨数量',
  `longJiAll` bigint NULL DEFAULT NULL COMMENT '龙脊数量',
  `longYuanAll` bigint NULL DEFAULT NULL COMMENT '龙元数量',
  `longZhuAll` bigint NULL DEFAULT NULL COMMENT '龙珠数量',
  `wangHunShiAll` bigint NULL DEFAULT NULL COMMENT '王魂石数量',
  `zhaoHuanShiAll` bigint NULL DEFAULT NULL COMMENT '召唤石数量',
  `zhenZhuShiAll` bigint NULL DEFAULT NULL COMMENT '珍珠石数量',
  `skillBitAll` bigint NULL DEFAULT NULL COMMENT '钻头数量',
  `skillBlackHoleAll` bigint NULL DEFAULT NULL COMMENT '黑洞炮数量',
  `skillTorpedoAll` bigint NULL DEFAULT NULL COMMENT '鱼雷炮数量',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 90 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of tbl_change_all_log
-- ----------------------------
INSERT INTO `tbl_change_all_log` VALUES (1, 0, 89668, 273, 211, 453, 135, 27950, 164, '0', '9920280000', '2024-12-05 03:00:07', 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
INSERT INTO `tbl_change_all_log` VALUES (2, 0, 1029999000, 0, 0, 0, 0, 0, 0, '0', '1029999437108000', '2024-12-06 03:00:01', 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
INSERT INTO `tbl_change_all_log` VALUES (3, 0, 1029999000, 0, 0, 0, 0, 0, 0, '0', '1029999437108000', '2024-12-06 03:02:24', 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
INSERT INTO `tbl_change_all_log` VALUES (4, 0, 1029999000, 0, 0, 0, 0, 0, 0, '0', '1029999753727800', '2024-12-07 03:00:01', 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
INSERT INTO `tbl_change_all_log` VALUES (5, 0, 1029999000, 0, 0, 0, 0, 0, 0, '0', '1029999753727800', '2024-12-08 03:00:01', 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
INSERT INTO `tbl_change_all_log` VALUES (6, 0, 1029999000, 0, 0, 0, 0, 0, 0, '0', '1029999753727800', '2024-12-09 03:00:01', 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
INSERT INTO `tbl_change_all_log` VALUES (7, 0, 1029999000, 0, 0, 0, 0, 0, 0, '0', '1029999781687800', '2024-12-10 03:00:01', 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
INSERT INTO `tbl_change_all_log` VALUES (8, 0, 1030000439, 0, 0, 0, 0, 850, 0, '0', '1030003775353800', '2024-12-11 03:00:01', 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
INSERT INTO `tbl_change_all_log` VALUES (9, 0, 1030000439, 0, 0, 0, 0, 850, 0, '0', '1030003690684800', '2024-12-12 03:00:01', 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
INSERT INTO `tbl_change_all_log` VALUES (10, 0, 1030000439, 0, 0, 0, 0, 850, 0, '0', '1030005387173400', '2024-12-13 03:00:01', 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
INSERT INTO `tbl_change_all_log` VALUES (11, 1, 1030000439, 0, 0, 0, 0, 850, 0, '0', '1030005333759400', '2024-12-14 03:00:01', 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
INSERT INTO `tbl_change_all_log` VALUES (12, 1, 1030000439, 0, 0, 0, 0, 850, 0, '0', '1030005043269400', '2024-12-15 03:00:01', 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
INSERT INTO `tbl_change_all_log` VALUES (13, 0, 1030000439, 0, 0, 0, 0, 850, 0, '0', '1030004769069400', '2024-12-16 03:00:01', 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
INSERT INTO `tbl_change_all_log` VALUES (14, 0, 1031004289, 280, 100, 0, 0, 15050, 0, '0', '1031026689360654', '2024-12-17 03:00:04', 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
INSERT INTO `tbl_change_all_log` VALUES (15, 0, 1031003289, 200, 100, 0, 0, 14900, 0, '0', '1031027556243400', '2024-12-18 03:00:04', 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
INSERT INTO `tbl_change_all_log` VALUES (16, 0, 1031003289, 200, 100, 0, 0, 14900, 0, '0', '1031027556243400', '2024-12-18 03:02:25', 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
INSERT INTO `tbl_change_all_log` VALUES (17, 0, 1031003289, 200, 100, 0, 0, 14650, 0, '0', '1031027611018610', '2024-12-19 03:00:05', 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
INSERT INTO `tbl_change_all_log` VALUES (18, 0, 1031003289, 200, 100, 0, 0, 14650, 0, '0', '1031027611018610', '2024-12-19 03:02:27', 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
INSERT INTO `tbl_change_all_log` VALUES (19, 1, 1031003318, 200, 100, 0, 0, 14400, 0, '0', '1031035903023010', '2024-12-20 03:00:05', 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
INSERT INTO `tbl_change_all_log` VALUES (20, 1, 1031003318, 200, 100, 0, 0, 14400, 0, '0', '1031035903023010', '2024-12-20 03:02:30', 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
INSERT INTO `tbl_change_all_log` VALUES (21, 0, 1031003318, 200, 100, 0, 0, 14400, 0, '0', '1031035903023010', '2024-12-21 03:00:02', 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
INSERT INTO `tbl_change_all_log` VALUES (22, 0, 1031003318, 200, 100, 0, 0, 14400, 0, '0', '1031035903023010', '2024-12-21 03:02:20', 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
INSERT INTO `tbl_change_all_log` VALUES (23, 0, 1031003318, 200, 100, 0, 0, 14400, 0, '0', '1031035903023010', '2024-12-22 03:00:01', 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
INSERT INTO `tbl_change_all_log` VALUES (24, 0, 1031003318, 200, 100, 0, 0, 14400, 0, '0', '1031035903023010', '2024-12-22 03:02:18', 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
INSERT INTO `tbl_change_all_log` VALUES (25, 0, 1031003318, 200, 100, 0, 0, 14400, 0, '0', '1031035903023010', '2024-12-23 03:00:01', 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
INSERT INTO `tbl_change_all_log` VALUES (26, 0, 1031003318, 200, 100, 0, 0, 14400, 0, '0', '1031035903023010', '2024-12-23 03:02:33', 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
INSERT INTO `tbl_change_all_log` VALUES (27, 0, 1031008318, 200, 100, 0, 0, 114400, 0, '0', '1031043559961610', '2024-12-24 03:00:04', 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
INSERT INTO `tbl_change_all_log` VALUES (28, 0, 1031008318, 200, 100, 0, 0, 114400, 0, '0', '1031043559961610', '2024-12-24 03:02:28', 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
INSERT INTO `tbl_change_all_log` VALUES (29, 0, 1031008475, 315, 1022, 0, 0, 143300, 0, '0', '1031047973761610', '2024-12-25 03:00:05', 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
INSERT INTO `tbl_change_all_log` VALUES (30, 0, 1031008475, 315, 1022, 0, 0, 143300, 0, '0', '1031047973761610', '2024-12-26 03:00:04', 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
INSERT INTO `tbl_change_all_log` VALUES (31, 0, 1031008475, 340, 1022, 0, 0, 143200, 0, '0', '1031057314369210', '2024-12-27 03:00:05', 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
INSERT INTO `tbl_change_all_log` VALUES (32, 0, 1031008475, 340, 1022, 0, 0, 143200, 0, '0', '1031057314369210', '2024-12-27 03:02:36', 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
INSERT INTO `tbl_change_all_log` VALUES (33, 0, 1031017824, 405, 1072, 50, 0, 157800, 50, '0', '1031070420039010', '2024-12-28 03:00:04', 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
INSERT INTO `tbl_change_all_log` VALUES (34, 0, 1031017824, 405, 1072, 50, 0, 157800, 50, '0', '1031070420039010', '2024-12-28 03:02:38', 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
INSERT INTO `tbl_change_all_log` VALUES (35, 0, 1031017824, 405, 1072, 50, 0, 157800, 50, '0', '1031070420039010', '2024-12-29 03:00:05', 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
INSERT INTO `tbl_change_all_log` VALUES (36, 0, 1031017824, 405, 1072, 50, 0, 157800, 50, '0', '1031070420039010', '2024-12-29 03:02:30', 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
INSERT INTO `tbl_change_all_log` VALUES (37, 0, 1031017824, 405, 1072, 50, 0, 157800, 50, '0', '1031070420039010', '2024-12-30 03:00:05', 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
INSERT INTO `tbl_change_all_log` VALUES (38, 0, 1031017824, 405, 1072, 50, 0, 157800, 50, '0', '1031070420039010', '2024-12-30 03:02:32', 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
INSERT INTO `tbl_change_all_log` VALUES (39, 0, 1031017824, 369, 1071, 50, 48, 157700, 50, '0', '1031075204538810', '2024-12-31 03:00:05', 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
INSERT INTO `tbl_change_all_log` VALUES (40, 0, 1031017824, 369, 1071, 50, 48, 157700, 50, '0', '1031075204538810', '2024-12-31 03:02:30', 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
INSERT INTO `tbl_change_all_log` VALUES (41, 0, 1031017803, 407, 1071, 50, 48, 159750, 50, '0', '1031085863718810', '2025-01-01 03:00:02', 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
INSERT INTO `tbl_change_all_log` VALUES (42, 0, 1031017803, 407, 1071, 50, 48, 159750, 50, '0', '1031085863718810', '2025-01-01 03:02:26', 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
INSERT INTO `tbl_change_all_log` VALUES (43, 0, 1031017803, 407, 1071, 50, 48, 159750, 50, '0', '1031085863718810', '2025-01-02 03:00:02', 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
INSERT INTO `tbl_change_all_log` VALUES (44, 0, 1031017803, 407, 1071, 50, 48, 159750, 50, '0', '1031085863718810', '2025-01-02 03:02:30', 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
INSERT INTO `tbl_change_all_log` VALUES (45, 0, 1031017303, 372, 1071, 50, 48, 159700, 50, '0', '1031400085944010', '2025-01-03 03:00:03', 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
INSERT INTO `tbl_change_all_log` VALUES (46, 0, 1031017303, 372, 1071, 50, 48, 159700, 50, '0', '1031400085944010', '2025-01-03 03:02:28', 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
INSERT INTO `tbl_change_all_log` VALUES (47, 0, 1031017303, 372, 1071, 50, 48, 159700, 50, '0', '1031400075676010', '2025-01-04 03:00:03', 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
INSERT INTO `tbl_change_all_log` VALUES (48, 0, 1031017303, 372, 1071, 50, 48, 159700, 50, '0', '1031400075676010', '2025-01-04 03:02:34', 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
INSERT INTO `tbl_change_all_log` VALUES (49, 0, 1031017303, 372, 1071, 50, 48, 159700, 50, '0', '1031400075676010', '2025-01-05 03:00:03', 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
INSERT INTO `tbl_change_all_log` VALUES (50, 0, 1031017303, 372, 1071, 50, 48, 159700, 50, '0', '1031400075676010', '2025-01-05 03:02:19', 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
INSERT INTO `tbl_change_all_log` VALUES (51, 0, 1031017303, 372, 1071, 50, 48, 159700, 50, '0', '1031400075676010', '2025-01-06 03:00:03', 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
INSERT INTO `tbl_change_all_log` VALUES (52, 0, 1031017303, 372, 1071, 50, 48, 159700, 50, '0', '1031400075676010', '2025-01-06 03:02:35', 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
INSERT INTO `tbl_change_all_log` VALUES (53, 0, 1031017477, 372, 1070, 50, 48, 159700, 50, '0', '1031399615576010', '2025-01-07 03:00:03', 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
INSERT INTO `tbl_change_all_log` VALUES (54, 0, 1031017477, 372, 1070, 50, 48, 159700, 50, '0', '1031399615576010', '2025-01-07 03:02:26', 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
INSERT INTO `tbl_change_all_log` VALUES (55, 0, 1031017457, 372, 1070, 50, 48, 160700, 50, '0', '1031399853328010', '2025-01-08 03:00:04', 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
INSERT INTO `tbl_change_all_log` VALUES (56, 0, 1031017457, 372, 1070, 50, 48, 160700, 50, '0', '1031399853328010', '2025-01-08 03:02:38', 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
INSERT INTO `tbl_change_all_log` VALUES (57, 0, 1031017457, 372, 1069, 50, 48, 160700, 50, '0', '1031399836108010', '2025-01-09 03:00:05', 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
INSERT INTO `tbl_change_all_log` VALUES (58, 0, 1031017457, 372, 1069, 50, 48, 160700, 50, '0', '1031399830938010', '2025-01-10 03:00:04', 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
INSERT INTO `tbl_change_all_log` VALUES (59, 0, 1031017457, 367, 1069, 50, 48, 160600, 50, '0', '1031400393967010', '2025-01-11 03:00:02', 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
INSERT INTO `tbl_change_all_log` VALUES (60, 0, 1031017457, 367, 1069, 50, 48, 160600, 50, '0', '1031400393967010', '2025-01-12 03:00:03', 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
INSERT INTO `tbl_change_all_log` VALUES (61, 0, 1031017457, 367, 1069, 50, 48, 160600, 50, '0', '1031400393967010', '2025-01-13 03:00:03', 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
INSERT INTO `tbl_change_all_log` VALUES (62, 0, 1031015356, 367, 1069, 50, 48, 160600, 50, '0', '1031398903737010', '2025-01-14 03:00:03', 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
INSERT INTO `tbl_change_all_log` VALUES (63, 0, 1031015346, 367, 1069, 50, 48, 160600, 50, '0', '1031398895447010', '2025-01-15 03:00:03', 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
INSERT INTO `tbl_change_all_log` VALUES (64, 0, 1031015336, 357, 1069, 50, 48, 160600, 50, '0', '1031398886633000', '2025-01-16 03:00:03', 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
INSERT INTO `tbl_change_all_log` VALUES (65, 0, 1031015336, 452, 1218, 199, 148, 160100, 50, '0', '1031398858833000', '2025-01-17 03:00:03', 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
INSERT INTO `tbl_change_all_log` VALUES (66, 0, 1031015336, 452, 1218, 199, 148, 160100, 50, '0', '1031398858833000', '2025-01-18 03:00:03', 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
INSERT INTO `tbl_change_all_log` VALUES (67, 0, 1031015336, 452, 1218, 199, 148, 160100, 50, '0', '1031398426403000', '2025-01-19 03:00:03', 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
INSERT INTO `tbl_change_all_log` VALUES (68, 0, 1031015336, 452, 1218, 199, 148, 160100, 50, '0', '1031398426403000', '2025-01-20 03:00:03', 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
INSERT INTO `tbl_change_all_log` VALUES (69, 0, 1031015297, 452, 1218, 199, 148, 160050, 100, '0', '1031397350023000', '2025-01-21 03:00:03', 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
INSERT INTO `tbl_change_all_log` VALUES (70, 1, 1031015297, 452, 1218, 199, 148, 160050, 100, '0', '1031396158623000', '2025-01-22 03:00:03', 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
INSERT INTO `tbl_change_all_log` VALUES (71, 0, 1031015297, 452, 1218, 199, 148, 160050, 100, '0', '1031390231663000', '2025-01-23 03:00:03', 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
INSERT INTO `tbl_change_all_log` VALUES (72, 0, 1031016397, 452, 1218, 199, 148, 160050, 100, '0', '1031390134612998', '2025-01-24 03:00:03', 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
INSERT INTO `tbl_change_all_log` VALUES (73, 0, 1031016397, 452, 1218, 199, 148, 160050, 100, '0', '1031390066822998', '2025-01-25 03:00:02', 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
INSERT INTO `tbl_change_all_log` VALUES (74, 0, 1031016397, 452, 1218, 199, 148, 160050, 100, '0', '1031390031022998', '2025-01-26 03:00:03', 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
INSERT INTO `tbl_change_all_log` VALUES (75, 0, 1031016397, 452, 1218, 199, 148, 160050, 100, '0', '1031390031022998', '2025-01-27 03:00:02', 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
INSERT INTO `tbl_change_all_log` VALUES (76, 0, 1031016397, 452, 1218, 199, 148, 160050, 100, '0', '1031390031022998', '2025-01-28 03:00:02', 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
INSERT INTO `tbl_change_all_log` VALUES (77, 0, 1031016397, 452, 1218, 199, 148, 160050, 100, '0', '1031390031022998', '2025-01-29 03:00:02', 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
INSERT INTO `tbl_change_all_log` VALUES (78, 0, 1031016397, 452, 1218, 199, 148, 160050, 100, '0', '1031390031022998', '2025-01-30 03:00:03', 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
INSERT INTO `tbl_change_all_log` VALUES (79, 0, 1031016397, 452, 1218, 199, 148, 160050, 100, '0', '1031390030870998', '2025-01-31 03:00:02', 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
INSERT INTO `tbl_change_all_log` VALUES (80, 0, 1031016397, 452, 1218, 199, 148, 160050, 100, '0', '1031390030870998', '2025-02-01 03:00:03', 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
INSERT INTO `tbl_change_all_log` VALUES (81, 0, 1031016397, 452, 1218, 199, 148, 160050, 100, '0', '1031390030870998', '2025-02-02 03:00:03', 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
INSERT INTO `tbl_change_all_log` VALUES (82, 0, 1031016397, 442, 1218, 199, 148, 160050, 100, '0', '1031390018046998', '2025-02-03 03:00:02', 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
INSERT INTO `tbl_change_all_log` VALUES (83, 0, 1031016397, 442, 1218, 199, 148, 160050, 100, '0', '1031390018046998', '2025-02-04 03:00:03', 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
INSERT INTO `tbl_change_all_log` VALUES (84, 0, 1031016397, 442, 1218, 199, 148, 160050, 100, '0', '1031390018046998', '2025-02-05 03:00:02', 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
INSERT INTO `tbl_change_all_log` VALUES (85, 0, 1031016397, 442, 1218, 199, 148, 160050, 100, '0', '1031391384902998', '2025-02-06 03:00:03', 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
INSERT INTO `tbl_change_all_log` VALUES (86, 0, 1031016396, 442, 1218, 199, 148, 160150, 100, '0', '1031391341002998', '2025-02-07 03:00:03', 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
INSERT INTO `tbl_change_all_log` VALUES (87, 0, 1031016396, 442, 1218, 199, 148, 160150, 100, '0', '1031391342802998', '2025-02-08 03:00:03', 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
INSERT INTO `tbl_change_all_log` VALUES (88, 3, 1031020146, 442, 1218, 199, 193, 579900, 136, '0', '1031397947136998', '2025-02-16 03:00:03', 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
INSERT INTO `tbl_change_all_log` VALUES (89, 0, 1031020146, 442, 1218, 199, 192, 579900, 136, '0', '1031397947136998', '2025-02-17 03:00:02', 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);

-- ----------------------------
-- Table structure for tbl_kill_boss
-- ----------------------------
DROP TABLE IF EXISTS `tbl_kill_boss`;
CREATE TABLE `tbl_kill_boss`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `userId` bigint NULL DEFAULT NULL COMMENT '用户id',
  `nickName` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '昵称',
  `batterLevel` bigint NULL DEFAULT NULL COMMENT '炮倍',
  `mult` bigint NULL DEFAULT NULL COMMENT '倍数',
  `bossName` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'boss名称',
  `createTime` timestamp NULL DEFAULT NULL COMMENT '创建时间',
  `room_index` int NULL DEFAULT NULL COMMENT '房间类型',
  `award` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '奖励',
  `blood_pool_float_kill_value` int NULL DEFAULT NULL COMMENT '血池概率击杀时，取的鱼的倍数',
  `blood_pool_float_kill_double_str` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '血池概率击杀时的倍数',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of tbl_kill_boss
-- ----------------------------

-- ----------------------------
-- Table structure for tbl_osee_cdk_type
-- ----------------------------
DROP TABLE IF EXISTS `tbl_osee_cdk_type`;
CREATE TABLE `tbl_osee_cdk_type`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '类型id',
  `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `name` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '类型名',
  `start_with` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '开头字符',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of tbl_osee_cdk_type
-- ----------------------------

-- ----------------------------
-- Table structure for tbl_osee_feed_back
-- ----------------------------
DROP TABLE IF EXISTS `tbl_osee_feed_back`;
CREATE TABLE `tbl_osee_feed_back`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `userId` bigint NULL DEFAULT NULL COMMENT '用户id',
  `context` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '反馈内容',
  `userName` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '用户名',
  `createTime` timestamp NULL DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of tbl_osee_feed_back
-- ----------------------------

-- ----------------------------
-- Table structure for tbl_osee_fish_all
-- ----------------------------
DROP TABLE IF EXISTS `tbl_osee_fish_all`;
CREATE TABLE `tbl_osee_fish_all`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `xlAll` bigint NULL DEFAULT NULL,
  `cxAll` bigint NULL DEFAULT NULL COMMENT '所有cx',
  `cxChallengeAll` bigint NULL DEFAULT NULL COMMENT '所有xh',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of tbl_osee_fish_all
-- ----------------------------

-- ----------------------------
-- Table structure for tbl_osee_notice
-- ----------------------------
DROP TABLE IF EXISTS `tbl_osee_notice`;
CREATE TABLE `tbl_osee_notice`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '类型id',
  `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `index` int NULL DEFAULT NULL COMMENT '序号',
  `title` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '标题',
  `content` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '内容',
  `start_time` timestamp NULL DEFAULT NULL COMMENT '生效时间',
  `end_time` timestamp NULL DEFAULT NULL COMMENT '失效时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of tbl_osee_notice
-- ----------------------------

-- ----------------------------
-- Table structure for tbl_osee_player
-- ----------------------------
DROP TABLE IF EXISTS `tbl_osee_player`;
CREATE TABLE `tbl_osee_player`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '玩家id',
  `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `user_id` bigint NULL DEFAULT NULL COMMENT '用户id',
  `money` bigint NULL DEFAULT NULL COMMENT '玩家金币',
  `lottery` bigint NULL DEFAULT NULL COMMENT '奖券',
  `diamond` bigint NULL DEFAULT NULL COMMENT '钻石',
  `vip_level` int NULL DEFAULT NULL COMMENT 'vip等级',
  `vip_points` bigint NULL DEFAULT NULL COMMENT 'vip积分',
  `level` int NULL DEFAULT NULL COMMENT '玩家等级',
  `experience` bigint NULL DEFAULT NULL COMMENT '玩家经验',
  `recharge_money` bigint NULL DEFAULT NULL COMMENT '充值金额',
  `player_type` int NULL DEFAULT NULL COMMENT '玩家类型',
  `gold_torpedo` bigint NULL DEFAULT NULL COMMENT '玩家黄金鱼雷数量',
  `skill_lock` bigint NULL DEFAULT NULL COMMENT '玩家锁定技能数量',
  `skill_frozen` bigint NULL DEFAULT NULL COMMENT '玩家冰冻技能数量',
  `skill_fast` bigint NULL DEFAULT NULL COMMENT '玩家急速技能数量',
  `skill_crit` bigint NULL DEFAULT NULL COMMENT '玩家暴击技能数量',
  `boss_bugle` bigint NULL DEFAULT NULL COMMENT '玩家BOSS号角数量',
  `battery_level` int NULL DEFAULT NULL COMMENT '玩家目前拥有的最高炮台等级',
  `monthcard_expire_date` date NULL DEFAULT NULL COMMENT '玩家月卡到期时间',
  `qszs_battery_expire_date` date NULL DEFAULT NULL COMMENT '骑士之誓炮台外观到期时间',
  `blnh_battery_expire_date` date NULL DEFAULT NULL COMMENT '冰龙怒吼炮台外观到期时间',
  `lhtz_battery_expire_date` date NULL DEFAULT NULL COMMENT '莲花童子炮台外观到期时间',
  `swhp_battery_expire_date` date NULL DEFAULT NULL COMMENT '死亡火炮炮台外观到期时间',
  `dragon_crystal` bigint NULL DEFAULT NULL COMMENT '玩家拥有的龙晶数量',
  `fen_shen` bigint NULL DEFAULT NULL COMMENT '玩家拥有的分身炮道具数量',
  `gold_torpedo_bang` bigint NULL DEFAULT NULL COMMENT '玩家非绑定黄金鱼雷数量',
  `rare_torpedo` bigint NULL DEFAULT NULL COMMENT '玩家稀有鱼雷数量',
  `rare_torpedo_bang` bigint NULL DEFAULT NULL COMMENT '玩家稀有鱼雷绑定数量',
  `skillEle` bigint NULL DEFAULT NULL COMMENT '玩家电磁炮技能数量',
  `yuGu` bigint NULL DEFAULT NULL COMMENT '玩家鱼骨数量',
  `haiYaoShi` bigint NULL DEFAULT NULL COMMENT '玩家海妖石数量',
  `wangHunShi` bigint NULL DEFAULT NULL COMMENT '玩家王魂石数量',
  `haiHunShi` bigint NULL DEFAULT NULL COMMENT '玩家海魂石数量',
  `zhenZhuShi` bigint NULL DEFAULT NULL COMMENT '玩家珍珠石数量',
  `haiShouShi` bigint NULL DEFAULT NULL COMMENT '玩家海兽石数量',
  `haiMoShi` bigint NULL DEFAULT NULL COMMENT '玩家海魔石数量',
  `zhaoHuanShi` bigint NULL DEFAULT NULL COMMENT '玩家召唤石数量',
  `dianChiShi` bigint NULL DEFAULT NULL COMMENT '玩家电磁石数量',
  `heiDongShi` bigint NULL DEFAULT NULL COMMENT '玩家黑洞石数量',
  `lingZhuShi` bigint NULL DEFAULT NULL COMMENT '玩家领主石数量',
  `longGu` bigint NULL DEFAULT NULL COMMENT '玩家龙骨数量',
  `longZhu` bigint NULL DEFAULT NULL COMMENT '玩家龙珠数量',
  `longYuan` bigint NULL DEFAULT NULL COMMENT '玩家龙元数量',
  `longJi` bigint NULL DEFAULT NULL COMMENT '玩家龙脊数量',
  `skillBlackHole` bigint NULL DEFAULT NULL COMMENT '玩家黑洞炮技能数量',
  `skillTorpedo` bigint NULL DEFAULT NULL COMMENT '玩家鱼雷炮技能数量',
  `sendCard` bigint NULL DEFAULT NULL COMMENT '玩家赠送卡数量',
  `blackBullet` bigint NULL DEFAULT NULL COMMENT '玩家黑铁弹数量',
  `bronzeBullet` bigint NULL DEFAULT NULL COMMENT '玩家青铜弹数量',
  `silverBullet` bigint NULL DEFAULT NULL COMMENT '玩家白银弹数量',
  `goldBullet` bigint NULL DEFAULT NULL COMMENT '玩家黄金弹数量',
  `lbs_battery_expire_date` date NULL DEFAULT NULL COMMENT '蓝宝石炮台外观',
  `tjzx_battery_expire_date` date NULL DEFAULT NULL COMMENT '钛晶之息炮台外观',
  `hjhp_battery_expire_date` date NULL DEFAULT NULL COMMENT '黄金火炮炮台外观',
  `gjzy_battery_expire_date` date NULL DEFAULT NULL COMMENT '冠军之眼炮台外观',
  `hjzp_battery_expire_date` date NULL DEFAULT NULL COMMENT '合金重炮炮台外观',
  `zlhp_battery_expire_date` date NULL DEFAULT NULL COMMENT '臻蓝火炮炮台外观',
  `tian_shen_guan_yu_battery_expire_date` date NULL DEFAULT NULL COMMENT '天神关羽过期时间',
  `skillBit` bigint NULL DEFAULT NULL COMMENT '玩家钻头数量',
  `magicLamp` bigint NULL DEFAULT NULL COMMENT '玩家神灯数量',
  `vipCard` bigint NULL DEFAULT NULL COMMENT '玩家vip卡数量',
  `isLive` bigint NULL DEFAULT NULL COMMENT '是否直播号',
  `userIp` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '用户注册ip',
  `thousandSword` bigint NULL DEFAULT NULL COMMENT '万剑阵',
  `time_spending` bigint UNSIGNED NULL DEFAULT NULL COMMENT '游戏时间',
  `use_battery` bigint NULL DEFAULT NULL COMMENT '使用的子弹(发射消耗)',
  `total_dragon_crystal` bigint NULL DEFAULT NULL COMMENT '总的击杀掉落的龙晶',
  `flag` int NULL DEFAULT NULL COMMENT '标记次数',
  `total_diamonds` bigint NULL DEFAULT NULL COMMENT '累计充值子弹数量',
  `total_pay` bigint NULL DEFAULT NULL COMMENT '累计充值',
  `shopping_pay` bigint NULL DEFAULT NULL COMMENT '商城消耗子弹总量',
  `last_sign_in_time` datetime NULL DEFAULT NULL COMMENT '最后一次登录游戏的时间',
  `last_join_room_time` datetime NULL DEFAULT NULL COMMENT '最后一次加入房间的时间',
  `skill_double` bigint NULL DEFAULT NULL COMMENT '玩家翻倍技能数量',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `user_id`(`user_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1191 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of tbl_osee_player
-- ----------------------------
INSERT INTO `tbl_osee_player` VALUES (664, '2024-12-06 05:00:39', 20000, 0, 40900, 60, 3, 0, 1, 0, 9, 0, 0, 0, 0, 0, 0, 0, 1000000, '2024-12-06', '2024-12-06', '2024-12-06', '2024-12-06', '2024-12-06', 99999989297100, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, '2024-12-06', '2024-12-06', '2024-12-06', '2024-12-06', '2024-12-06', '2024-12-06', '2024-12-06', 0, 0, 0, NULL, NULL, 0, NULL, 4051340, 1689440, NULL, NULL, NULL, NULL, '2026-01-13 15:03:21', '2026-01-13 15:41:27', 0);

-- ----------------------------
-- Table structure for tbl_shopping
-- ----------------------------
DROP TABLE IF EXISTS `tbl_shopping`;
CREATE TABLE `tbl_shopping`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'id',
  `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `player_id` bigint NULL DEFAULT NULL COMMENT '用户id',
  `daily_bag_info` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '每日礼包',
  `once_bag_info` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '特惠礼包',
  `money_card` tinyint(1) NULL DEFAULT NULL COMMENT '金币卡',
  `last_receive` timestamp NULL DEFAULT '2009-12-31 16:00:00' COMMENT '最后领取时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of tbl_shopping
-- ----------------------------

-- ----------------------------
-- Table structure for tbl_shopping1
-- ----------------------------
DROP TABLE IF EXISTS `tbl_shopping1`;
CREATE TABLE `tbl_shopping1`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'id',
  `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `player_id` bigint NULL DEFAULT NULL COMMENT '用户id',
  `daily_bag_info` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '每日礼包',
  `once_bag_info` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '特惠礼包',
  `money_card` tinyint(1) NULL DEFAULT NULL COMMENT '金币卡',
  `last_receive` timestamp NULL DEFAULT '2009-12-31 16:00:00' COMMENT '最后领取时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 22 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of tbl_shopping1
-- ----------------------------

-- ----------------------------
-- Table structure for tbl_ttmy_address
-- ----------------------------
DROP TABLE IF EXISTS `tbl_ttmy_address`;
CREATE TABLE `tbl_ttmy_address`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `player_id` bigint NOT NULL COMMENT '玩家ID',
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '玩家昵称',
  `phone` varchar(11) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '玩家手机号码',
  `address` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '玩家收货地址',
  `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '数据创建时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of tbl_ttmy_address
-- ----------------------------

SET FOREIGN_KEY_CHECKS = 1;
