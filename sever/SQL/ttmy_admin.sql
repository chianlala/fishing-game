/*
 Navicat Premium Dump SQL

 Source Server         : localhost
 Source Server Type    : MySQL
 Source Server Version : 80406 (8.4.6)
 Source Host           : localhost:3306
 Source Schema         : ttmy_admin

 Target Server Type    : MySQL
 Target Server Version : 80406 (8.4.6)
 File Encoding         : 65001

 Date: 13/01/2026 15:47:14
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for js_gen_table
-- ----------------------------
DROP TABLE IF EXISTS `js_gen_table`;
CREATE TABLE `js_gen_table`  (
  `table_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '表名',
  `class_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '实体类名称',
  `comments` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '表说明',
  `parent_table_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '关联父表的表名',
  `parent_table_fk_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '本表关联父表的外键名',
  `data_source_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '数据源名称',
  `tpl_category` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '使用的模板',
  `package_name` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '生成包路径',
  `module_name` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '生成模块名',
  `sub_module_name` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '生成子模块名',
  `function_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '生成功能名',
  `function_name_simple` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '生成功能名（简写）',
  `function_author` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '生成功能作者',
  `gen_base_dir` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '生成基础路径',
  `options` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '其它生成选项',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '创建者',
  `create_date` datetime NOT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '更新者',
  `update_date` datetime NOT NULL COMMENT '更新时间',
  `remarks` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '备注信息',
  PRIMARY KEY (`table_name`) USING BTREE,
  INDEX `idx_gen_table_ptn`(`parent_table_name` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '代码生成表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of js_gen_table
-- ----------------------------
INSERT INTO `js_gen_table` VALUES ('test_data', 'TestData', '测试数据', NULL, NULL, NULL, 'crud', 'com.jeesite.modules', 'test', '', '测试数据', '数据', 'ThinkGem', NULL, '{\"isHaveDelete\":\"1\",\"isFileUpload\":\"1\",\"isHaveDisableEnable\":\"1\",\"isImageUpload\":\"1\"}', 'system', '2019-01-15 10:57:50', 'system', '2019-01-15 10:57:50', NULL);
INSERT INTO `js_gen_table` VALUES ('test_data_child', 'TestDataChild', '测试数据子表', 'test_data', 'test_data_id', NULL, 'crud', 'com.jeesite.modules', 'test', '', '测试子表', '数据', 'ThinkGem', NULL, NULL, 'system', '2019-01-15 10:58:01', 'system', '2019-01-15 10:58:01', NULL);
INSERT INTO `js_gen_table` VALUES ('test_tree', 'TestTree', '测试树表', NULL, NULL, NULL, 'treeGrid', 'com.jeesite.modules', 'test', '', '测试树表', '数据', 'ThinkGem', NULL, '{\"treeViewName\":\"tree_name\",\"isHaveDelete\":\"1\",\"treeViewCode\":\"tree_code\",\"isFileUpload\":\"1\",\"isHaveDisableEnable\":\"1\",\"isImageUpload\":\"1\"}', 'system', '2019-01-15 10:58:08', 'system', '2019-01-15 10:58:08', NULL);

-- ----------------------------
-- Table structure for js_gen_table_column
-- ----------------------------
DROP TABLE IF EXISTS `js_gen_table_column`;
CREATE TABLE `js_gen_table_column`  (
  `id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '编号',
  `table_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '表名',
  `column_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '列名',
  `column_sort` decimal(10, 0) NULL DEFAULT NULL COMMENT '列排序（升序）',
  `column_type` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '类型',
  `column_label` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '列标签名',
  `comments` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '列备注说明',
  `attr_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '类的属性名',
  `attr_type` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '类的属性类型',
  `is_pk` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '是否主键',
  `is_null` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '是否可为空',
  `is_insert` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '是否插入字段',
  `is_update` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '是否更新字段',
  `is_list` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '是否列表字段',
  `is_query` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '是否查询字段',
  `query_type` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '查询方式',
  `is_edit` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '是否编辑字段',
  `show_type` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '表单类型',
  `options` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '其它生成选项',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_gen_table_column_tn`(`table_name` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '代码生成表列' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of js_gen_table_column
-- ----------------------------
INSERT INTO `js_gen_table_column` VALUES ('1085008126148808704', 'test_data', 'id', 10, 'varchar(64)', '编号', '编号', 'id', 'String', '1', '0', '1', NULL, NULL, NULL, NULL, '1', 'hidden', '{\"fieldValid\":\"abc\"}');
INSERT INTO `js_gen_table_column` VALUES ('1085008126165585920', 'test_data', 'test_input', 20, 'varchar(200)', '单行文本', '单行文本', 'testInput', 'String', NULL, '1', '1', '1', '1', '1', 'LIKE', '1', 'input', NULL);
INSERT INTO `js_gen_table_column` VALUES ('1085008132511567872', 'test_data', 'test_textarea', 30, 'varchar(200)', '多行文本', '多行文本', 'testTextarea', 'String', NULL, '1', '1', '1', '1', '1', 'LIKE', '1', 'textarea', '{\"isNewLine\":\"1\",\"gridRowCol\":\"12/2/10\"}');
INSERT INTO `js_gen_table_column` VALUES ('1085008132519956480', 'test_data', 'test_select', 40, 'varchar(10)', '下拉框', '下拉框', 'testSelect', 'String', NULL, '1', '1', '1', '1', '1', NULL, '1', 'select', '{\"dictName\":\"sys_menu_type\",\"dictType\":\"sys_menu_type\"}');
INSERT INTO `js_gen_table_column` VALUES ('1085008132532539392', 'test_data', 'test_select_multiple', 50, 'varchar(200)', '下拉多选', '下拉多选', 'testSelectMultiple', 'String', NULL, '1', '1', '1', '1', '1', NULL, '1', 'select_multiple', '{\"dictName\":\"sys_menu_type\",\"dictType\":\"sys_menu_type\"}');
INSERT INTO `js_gen_table_column` VALUES ('1085008138136129536', 'test_data', 'test_radio', 60, 'varchar(10)', '单选框', '单选框', 'testRadio', 'String', NULL, '1', '1', '1', '1', '1', NULL, '1', 'radio', '{\"dictName\":\"sys_menu_type\",\"dictType\":\"sys_menu_type\"}');
INSERT INTO `js_gen_table_column` VALUES ('1085008138144518144', 'test_data', 'test_checkbox', 70, 'varchar(200)', '复选框', '复选框', 'testCheckbox', 'String', NULL, '1', '1', '1', '1', '1', NULL, '1', 'checkbox', '{\"dictName\":\"sys_menu_type\",\"dictType\":\"sys_menu_type\"}');
INSERT INTO `js_gen_table_column` VALUES ('1085008144784101376', 'test_data', 'test_date', 80, 'datetime', '日期选择', '日期选择', 'testDate', 'java.util.Date', NULL, '1', '1', '1', '1', '1', 'BETWEEN', '1', 'date', NULL);
INSERT INTO `js_gen_table_column` VALUES ('1085008144796684288', 'test_data', 'test_datetime', 90, 'datetime', '日期时间', '日期时间', 'testDatetime', 'java.util.Date', NULL, '1', '1', '1', '1', '1', 'BETWEEN', '1', 'datetime', NULL);
INSERT INTO `js_gen_table_column` VALUES ('1085008144809267200', 'test_data', 'test_user_code', 100, 'varchar(64)', '用户选择', '用户选择', 'testUser', 'com.jeesite.modules.sys.entity.User', NULL, '1', '1', '1', '1', '1', NULL, '1', 'userselect', NULL);
INSERT INTO `js_gen_table_column` VALUES ('1085008150282833920', 'test_data', 'test_office_code', 110, 'varchar(64)', '机构选择', '机构选择', 'testOffice', 'com.jeesite.modules.sys.entity.Office', NULL, '1', '1', '1', '1', '1', NULL, '1', 'officeselect', NULL);
INSERT INTO `js_gen_table_column` VALUES ('1085008150291222528', 'test_data', 'test_area_code', 120, 'varchar(64)', '区域选择', '区域选择', 'testAreaCode|testAreaName', 'String', NULL, '1', '1', '1', '1', '1', NULL, '1', 'areaselect', NULL);
INSERT INTO `js_gen_table_column` VALUES ('1085008155706068992', 'test_data', 'test_area_name', 130, 'varchar(100)', '区域名称', '区域名称', 'testAreaName', 'String', NULL, '1', '1', '1', '1', '0', 'LIKE', '0', 'input', NULL);
INSERT INTO `js_gen_table_column` VALUES ('1085008155714457600', 'test_data', 'status', 140, 'char(1)', '状态', '状态（0正常 1删除 2停用）', 'status', 'String', NULL, '0', '1', NULL, '1', '1', NULL, NULL, 'select', '{\"dictName\":\"sys_search_status\",\"dictType\":\"sys_search_status\"}');
INSERT INTO `js_gen_table_column` VALUES ('1085008155722846208', 'test_data', 'create_by', 150, 'varchar(64)', '创建者', '创建者', 'createBy', 'String', NULL, '0', '1', NULL, NULL, NULL, NULL, NULL, 'input', NULL);
INSERT INTO `js_gen_table_column` VALUES ('1085008158314926080', 'test_data', 'create_date', 160, 'datetime', '创建时间', '创建时间', 'createDate', 'java.util.Date', NULL, '0', '1', NULL, '1', NULL, NULL, NULL, 'dateselect', NULL);
INSERT INTO `js_gen_table_column` VALUES ('1085008158327508992', 'test_data', 'update_by', 170, 'varchar(64)', '更新者', '更新者', 'updateBy', 'String', NULL, '0', '1', '1', NULL, NULL, NULL, NULL, 'input', NULL);
INSERT INTO `js_gen_table_column` VALUES ('1085008158335897600', 'test_data', 'update_date', 180, 'datetime', '更新时间', '更新时间', 'updateDate', 'java.util.Date', NULL, '0', '1', '1', NULL, NULL, NULL, NULL, 'dateselect', NULL);
INSERT INTO `js_gen_table_column` VALUES ('1085008161359990784', 'test_data', 'remarks', 190, 'varchar(500)', '备注信息', '备注信息', 'remarks', 'String', NULL, '1', '1', '1', '1', '1', NULL, '1', 'textarea', '{\"isNewLine\":\"1\",\"gridRowCol\":\"12/2/10\"}');
INSERT INTO `js_gen_table_column` VALUES ('1085008169022984192', 'test_data_child', 'id', 10, 'varchar(64)', '编号', '编号', 'id', 'String', '1', '0', '1', NULL, NULL, NULL, NULL, '1', 'hidden', '{\"fieldValid\":\"abc\"}');
INSERT INTO `js_gen_table_column` VALUES ('1085008169035567104', 'test_data_child', 'test_sort', 20, 'int(11)', '排序号', '排序号', 'testSort', 'Long', NULL, '1', '1', '1', '1', '1', NULL, '1', 'input', '{\"fieldValid\":\"digits\"}');
INSERT INTO `js_gen_table_column` VALUES ('1085008176040054784', 'test_data_child', 'test_data_id', 30, 'varchar(64)', '父表主键', '父表主键', 'testData', 'String', NULL, '1', '1', '1', '1', '1', NULL, '1', 'input', NULL);
INSERT INTO `js_gen_table_column` VALUES ('1085008176044249088', 'test_data_child', 'test_input', 40, 'varchar(200)', '单行文本', '单行文本', 'testInput', 'String', NULL, '1', '1', '1', '1', '1', 'LIKE', '1', 'input', NULL);
INSERT INTO `js_gen_table_column` VALUES ('1085008176052637696', 'test_data_child', 'test_textarea', 50, 'varchar(200)', '多行文本', '多行文本', 'testTextarea', 'String', NULL, '1', '1', '1', '1', '1', 'LIKE', '1', 'textarea', '{\"isNewLine\":\"1\",\"gridRowCol\":\"12/2/10\"}');
INSERT INTO `js_gen_table_column` VALUES ('1085008182088241152', 'test_data_child', 'test_select', 60, 'varchar(10)', '下拉框', '下拉框', 'testSelect', 'String', NULL, '1', '1', '1', '1', '1', NULL, '1', 'select', '{\"dictName\":\"sys_menu_type\",\"dictType\":\"sys_menu_type\"}');
INSERT INTO `js_gen_table_column` VALUES ('1085008182100824064', 'test_data_child', 'test_select_multiple', 70, 'varchar(200)', '下拉多选', '下拉多选', 'testSelectMultiple', 'String', NULL, '1', '1', '1', '1', '1', NULL, '1', 'select_multiple', '{\"dictName\":\"sys_menu_type\",\"dictType\":\"sys_menu_type\"}');
INSERT INTO `js_gen_table_column` VALUES ('1085008184583852032', 'test_data_child', 'test_radio', 80, 'varchar(10)', '单选框', '单选框', 'testRadio', 'String', NULL, '1', '1', '1', '1', '1', NULL, '1', 'radio', '{\"dictName\":\"sys_menu_type\",\"dictType\":\"sys_menu_type\"}');
INSERT INTO `js_gen_table_column` VALUES ('1085008184592240640', 'test_data_child', 'test_checkbox', 90, 'varchar(200)', '复选框', '复选框', 'testCheckbox', 'String', NULL, '1', '1', '1', '1', '1', NULL, '1', 'checkbox', '{\"dictName\":\"sys_menu_type\",\"dictType\":\"sys_menu_type\"}');
INSERT INTO `js_gen_table_column` VALUES ('1085008184596434944', 'test_data_child', 'test_date', 100, 'datetime', '日期选择', '日期选择', 'testDate', 'java.util.Date', NULL, '1', '1', '1', '1', '1', 'BETWEEN', '1', 'date', NULL);
INSERT INTO `js_gen_table_column` VALUES ('1085008188941733888', 'test_data_child', 'test_datetime', 110, 'datetime', '日期时间', '日期时间', 'testDatetime', 'java.util.Date', NULL, '1', '1', '1', '1', '1', 'BETWEEN', '1', 'datetime', NULL);
INSERT INTO `js_gen_table_column` VALUES ('1085008188950122496', 'test_data_child', 'test_user_code', 120, 'varchar(64)', '用户选择', '用户选择', 'testUser', 'com.jeesite.modules.sys.entity.User', NULL, '1', '1', '1', '1', '1', NULL, '1', 'userselect', NULL);
INSERT INTO `js_gen_table_column` VALUES ('1085008194176225280', 'test_data_child', 'test_office_code', 130, 'varchar(64)', '机构选择', '机构选择', 'testOffice', 'com.jeesite.modules.sys.entity.Office', NULL, '1', '1', '1', '1', '1', NULL, '1', 'officeselect', NULL);
INSERT INTO `js_gen_table_column` VALUES ('1085008194184613888', 'test_data_child', 'test_area_code', 140, 'varchar(64)', '区域选择', '区域选择', 'testAreaCode|testAreaName', 'String', NULL, '1', '1', '1', '1', '1', NULL, '1', 'areaselect', NULL);
INSERT INTO `js_gen_table_column` VALUES ('1085008194193002496', 'test_data_child', 'test_area_name', 150, 'varchar(100)', '区域名称', '区域名称', 'testAreaName', 'String', NULL, '1', '1', '1', '1', '0', 'LIKE', '0', 'input', NULL);
INSERT INTO `js_gen_table_column` VALUES ('1085008206939492352', 'test_tree', 'tree_code', 10, 'varchar(64)', '节点编码', '节点编码', 'treeCode', 'String', '1', '0', '1', NULL, NULL, NULL, NULL, '1', 'input', '{\"fieldValid\":\"abc\"}');
INSERT INTO `js_gen_table_column` VALUES ('1085008206947880960', 'test_tree', 'parent_code', 20, 'varchar(64)', '父级编号', '父级编号', 'parentCode|parentName', 'This', NULL, '0', '1', '1', '1', '1', NULL, '1', 'treeselect', NULL);
INSERT INTO `js_gen_table_column` VALUES ('1085008206956269568', 'test_tree', 'parent_codes', 30, 'varchar(1000)', '所有父级编号', '所有父级编号', 'parentCodes', 'String', NULL, '0', '1', '1', '1', '1', 'LIKE', '0', 'input', NULL);
INSERT INTO `js_gen_table_column` VALUES ('1085008209934225408', 'test_tree', 'tree_sort', 40, 'decimal(10,0)', '本级排序号', '本级排序号（升序）', 'treeSort', 'Integer', NULL, '0', '1', '1', '1', '1', NULL, '1', 'input', '{\"fieldValid\":\"digits\"}');
INSERT INTO `js_gen_table_column` VALUES ('1085008209942614016', 'test_tree', 'tree_sorts', 50, 'varchar(1000)', '所有级别排序号', '所有级别排序号', 'treeSorts', 'String', NULL, '0', '1', '1', '0', '1', NULL, '0', 'input', NULL);
INSERT INTO `js_gen_table_column` VALUES ('1085008209951002624', 'test_tree', 'tree_leaf', 60, 'char(1)', '是否最末级', '是否最末级', 'treeLeaf', 'String', NULL, '0', '1', '1', NULL, NULL, NULL, NULL, 'input', NULL);
INSERT INTO `js_gen_table_column` VALUES ('1085008216548642816', 'test_tree', 'tree_level', 70, 'decimal(4,0)', '层次级别', '层次级别', 'treeLevel', 'Integer', NULL, '0', '1', '1', NULL, NULL, NULL, NULL, 'input', '{\"fieldValid\":\"digits\"}');
INSERT INTO `js_gen_table_column` VALUES ('1085008216557031424', 'test_tree', 'tree_names', 80, 'varchar(1000)', '全节点名', '全节点名', 'treeNames', 'String', NULL, '0', '1', '1', '1', '1', 'LIKE', '1', 'input', NULL);
INSERT INTO `js_gen_table_column` VALUES ('1085008223184031744', 'test_tree', 'tree_name', 90, 'varchar(200)', '节点名称', '节点名称', 'treeName', 'String', NULL, '0', '1', '1', '1', '1', 'LIKE', '1', 'input', NULL);
INSERT INTO `js_gen_table_column` VALUES ('1085008223196614656', 'test_tree', 'status', 100, 'char(1)', '状态', '状态（0正常 1删除 2停用）', 'status', 'String', NULL, '0', '1', NULL, '1', '1', NULL, NULL, 'select', '{\"dictName\":\"sys_search_status\",\"dictType\":\"sys_search_status\"}');
INSERT INTO `js_gen_table_column` VALUES ('1085008223209197568', 'test_tree', 'create_by', 110, 'varchar(64)', '创建者', '创建者', 'createBy', 'String', NULL, '0', '1', NULL, NULL, NULL, NULL, NULL, 'input', NULL);
INSERT INTO `js_gen_table_column` VALUES ('1085008228905062400', 'test_tree', 'create_date', 120, 'datetime', '创建时间', '创建时间', 'createDate', 'java.util.Date', NULL, '0', '1', NULL, '1', NULL, NULL, NULL, 'dateselect', NULL);
INSERT INTO `js_gen_table_column` VALUES ('1085008228917645312', 'test_tree', 'update_by', 130, 'varchar(64)', '更新者', '更新者', 'updateBy', 'String', NULL, '0', '1', '1', NULL, NULL, NULL, NULL, 'input', NULL);
INSERT INTO `js_gen_table_column` VALUES ('1085008228930228224', 'test_tree', 'update_date', 140, 'datetime', '更新时间', '更新时间', 'updateDate', 'java.util.Date', NULL, '0', '1', '1', NULL, NULL, NULL, NULL, 'dateselect', NULL);
INSERT INTO `js_gen_table_column` VALUES ('1085008233371996160', 'test_tree', 'remarks', 150, 'varchar(500)', '备注信息', '备注信息', 'remarks', 'String', NULL, '1', '1', '1', '1', '1', NULL, '1', 'textarea', '{\"isNewLine\":\"1\",\"gridRowCol\":\"12/2/10\"}');

-- ----------------------------
-- Table structure for js_job_blob_triggers
-- ----------------------------
DROP TABLE IF EXISTS `js_job_blob_triggers`;
CREATE TABLE `js_job_blob_triggers`  (
  `SCHED_NAME` varchar(120) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `TRIGGER_NAME` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `TRIGGER_GROUP` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `BLOB_DATA` blob NULL,
  PRIMARY KEY (`SCHED_NAME`, `TRIGGER_NAME`, `TRIGGER_GROUP`) USING BTREE,
  INDEX `SCHED_NAME`(`SCHED_NAME` ASC, `TRIGGER_NAME` ASC, `TRIGGER_GROUP` ASC) USING BTREE,
  CONSTRAINT `js_job_blob_triggers_ibfk_1` FOREIGN KEY (`SCHED_NAME`, `TRIGGER_NAME`, `TRIGGER_GROUP`) REFERENCES `js_job_triggers` (`SCHED_NAME`, `TRIGGER_NAME`, `TRIGGER_GROUP`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of js_job_blob_triggers
-- ----------------------------

-- ----------------------------
-- Table structure for js_job_calendars
-- ----------------------------
DROP TABLE IF EXISTS `js_job_calendars`;
CREATE TABLE `js_job_calendars`  (
  `SCHED_NAME` varchar(120) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `CALENDAR_NAME` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `CALENDAR` blob NOT NULL,
  PRIMARY KEY (`SCHED_NAME`, `CALENDAR_NAME`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of js_job_calendars
-- ----------------------------

-- ----------------------------
-- Table structure for js_job_cron_triggers
-- ----------------------------
DROP TABLE IF EXISTS `js_job_cron_triggers`;
CREATE TABLE `js_job_cron_triggers`  (
  `SCHED_NAME` varchar(120) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `TRIGGER_NAME` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `TRIGGER_GROUP` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `CRON_EXPRESSION` varchar(120) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `TIME_ZONE_ID` varchar(80) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  PRIMARY KEY (`SCHED_NAME`, `TRIGGER_NAME`, `TRIGGER_GROUP`) USING BTREE,
  CONSTRAINT `js_job_cron_triggers_ibfk_1` FOREIGN KEY (`SCHED_NAME`, `TRIGGER_NAME`, `TRIGGER_GROUP`) REFERENCES `js_job_triggers` (`SCHED_NAME`, `TRIGGER_NAME`, `TRIGGER_GROUP`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of js_job_cron_triggers
-- ----------------------------

-- ----------------------------
-- Table structure for js_job_fired_triggers
-- ----------------------------
DROP TABLE IF EXISTS `js_job_fired_triggers`;
CREATE TABLE `js_job_fired_triggers`  (
  `SCHED_NAME` varchar(120) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `ENTRY_ID` varchar(95) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `TRIGGER_NAME` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `TRIGGER_GROUP` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `INSTANCE_NAME` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `FIRED_TIME` bigint NOT NULL,
  `SCHED_TIME` bigint NOT NULL,
  `PRIORITY` int NOT NULL,
  `STATE` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `JOB_NAME` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `JOB_GROUP` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `IS_NONCONCURRENT` varchar(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `REQUESTS_RECOVERY` varchar(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  PRIMARY KEY (`SCHED_NAME`, `ENTRY_ID`) USING BTREE,
  INDEX `IDX_QRTZ_FT_TRIG_INST_NAME`(`SCHED_NAME` ASC, `INSTANCE_NAME` ASC) USING BTREE,
  INDEX `IDX_QRTZ_FT_INST_JOB_REQ_RCVRY`(`SCHED_NAME` ASC, `INSTANCE_NAME` ASC, `REQUESTS_RECOVERY` ASC) USING BTREE,
  INDEX `IDX_QRTZ_FT_J_G`(`SCHED_NAME` ASC, `JOB_NAME` ASC, `JOB_GROUP` ASC) USING BTREE,
  INDEX `IDX_QRTZ_FT_JG`(`SCHED_NAME` ASC, `JOB_GROUP` ASC) USING BTREE,
  INDEX `IDX_QRTZ_FT_T_G`(`SCHED_NAME` ASC, `TRIGGER_NAME` ASC, `TRIGGER_GROUP` ASC) USING BTREE,
  INDEX `IDX_QRTZ_FT_TG`(`SCHED_NAME` ASC, `TRIGGER_GROUP` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of js_job_fired_triggers
-- ----------------------------

-- ----------------------------
-- Table structure for js_job_job_details
-- ----------------------------
DROP TABLE IF EXISTS `js_job_job_details`;
CREATE TABLE `js_job_job_details`  (
  `SCHED_NAME` varchar(120) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `JOB_NAME` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `JOB_GROUP` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `DESCRIPTION` varchar(250) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `JOB_CLASS_NAME` varchar(250) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `IS_DURABLE` varchar(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `IS_NONCONCURRENT` varchar(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `IS_UPDATE_DATA` varchar(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `REQUESTS_RECOVERY` varchar(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `JOB_DATA` blob NULL,
  PRIMARY KEY (`SCHED_NAME`, `JOB_NAME`, `JOB_GROUP`) USING BTREE,
  INDEX `IDX_QRTZ_J_REQ_RECOVERY`(`SCHED_NAME` ASC, `REQUESTS_RECOVERY` ASC) USING BTREE,
  INDEX `IDX_QRTZ_J_GRP`(`SCHED_NAME` ASC, `JOB_GROUP` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of js_job_job_details
-- ----------------------------

-- ----------------------------
-- Table structure for js_job_locks
-- ----------------------------
DROP TABLE IF EXISTS `js_job_locks`;
CREATE TABLE `js_job_locks`  (
  `SCHED_NAME` varchar(120) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `LOCK_NAME` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  PRIMARY KEY (`SCHED_NAME`, `LOCK_NAME`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of js_job_locks
-- ----------------------------

-- ----------------------------
-- Table structure for js_job_paused_trigger_grps
-- ----------------------------
DROP TABLE IF EXISTS `js_job_paused_trigger_grps`;
CREATE TABLE `js_job_paused_trigger_grps`  (
  `SCHED_NAME` varchar(120) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `TRIGGER_GROUP` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  PRIMARY KEY (`SCHED_NAME`, `TRIGGER_GROUP`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of js_job_paused_trigger_grps
-- ----------------------------

-- ----------------------------
-- Table structure for js_job_scheduler_state
-- ----------------------------
DROP TABLE IF EXISTS `js_job_scheduler_state`;
CREATE TABLE `js_job_scheduler_state`  (
  `SCHED_NAME` varchar(120) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `INSTANCE_NAME` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `LAST_CHECKIN_TIME` bigint NOT NULL,
  `CHECKIN_INTERVAL` bigint NOT NULL,
  PRIMARY KEY (`SCHED_NAME`, `INSTANCE_NAME`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of js_job_scheduler_state
-- ----------------------------

-- ----------------------------
-- Table structure for js_job_simple_triggers
-- ----------------------------
DROP TABLE IF EXISTS `js_job_simple_triggers`;
CREATE TABLE `js_job_simple_triggers`  (
  `SCHED_NAME` varchar(120) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `TRIGGER_NAME` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `TRIGGER_GROUP` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `REPEAT_COUNT` bigint NOT NULL,
  `REPEAT_INTERVAL` bigint NOT NULL,
  `TIMES_TRIGGERED` bigint NOT NULL,
  PRIMARY KEY (`SCHED_NAME`, `TRIGGER_NAME`, `TRIGGER_GROUP`) USING BTREE,
  CONSTRAINT `js_job_simple_triggers_ibfk_1` FOREIGN KEY (`SCHED_NAME`, `TRIGGER_NAME`, `TRIGGER_GROUP`) REFERENCES `js_job_triggers` (`SCHED_NAME`, `TRIGGER_NAME`, `TRIGGER_GROUP`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of js_job_simple_triggers
-- ----------------------------

-- ----------------------------
-- Table structure for js_job_simprop_triggers
-- ----------------------------
DROP TABLE IF EXISTS `js_job_simprop_triggers`;
CREATE TABLE `js_job_simprop_triggers`  (
  `SCHED_NAME` varchar(120) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `TRIGGER_NAME` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `TRIGGER_GROUP` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `STR_PROP_1` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `STR_PROP_2` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `STR_PROP_3` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `INT_PROP_1` int NULL DEFAULT NULL,
  `INT_PROP_2` int NULL DEFAULT NULL,
  `LONG_PROP_1` bigint NULL DEFAULT NULL,
  `LONG_PROP_2` bigint NULL DEFAULT NULL,
  `DEC_PROP_1` decimal(13, 4) NULL DEFAULT NULL,
  `DEC_PROP_2` decimal(13, 4) NULL DEFAULT NULL,
  `BOOL_PROP_1` varchar(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `BOOL_PROP_2` varchar(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  PRIMARY KEY (`SCHED_NAME`, `TRIGGER_NAME`, `TRIGGER_GROUP`) USING BTREE,
  CONSTRAINT `js_job_simprop_triggers_ibfk_1` FOREIGN KEY (`SCHED_NAME`, `TRIGGER_NAME`, `TRIGGER_GROUP`) REFERENCES `js_job_triggers` (`SCHED_NAME`, `TRIGGER_NAME`, `TRIGGER_GROUP`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of js_job_simprop_triggers
-- ----------------------------

-- ----------------------------
-- Table structure for js_job_triggers
-- ----------------------------
DROP TABLE IF EXISTS `js_job_triggers`;
CREATE TABLE `js_job_triggers`  (
  `SCHED_NAME` varchar(120) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `TRIGGER_NAME` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `TRIGGER_GROUP` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `JOB_NAME` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `JOB_GROUP` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `DESCRIPTION` varchar(250) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `NEXT_FIRE_TIME` bigint NULL DEFAULT NULL,
  `PREV_FIRE_TIME` bigint NULL DEFAULT NULL,
  `PRIORITY` int NULL DEFAULT NULL,
  `TRIGGER_STATE` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `TRIGGER_TYPE` varchar(8) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `START_TIME` bigint NOT NULL,
  `END_TIME` bigint NULL DEFAULT NULL,
  `CALENDAR_NAME` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `MISFIRE_INSTR` smallint NULL DEFAULT NULL,
  `JOB_DATA` blob NULL,
  PRIMARY KEY (`SCHED_NAME`, `TRIGGER_NAME`, `TRIGGER_GROUP`) USING BTREE,
  INDEX `IDX_QRTZ_T_J`(`SCHED_NAME` ASC, `JOB_NAME` ASC, `JOB_GROUP` ASC) USING BTREE,
  INDEX `IDX_QRTZ_T_JG`(`SCHED_NAME` ASC, `JOB_GROUP` ASC) USING BTREE,
  INDEX `IDX_QRTZ_T_C`(`SCHED_NAME` ASC, `CALENDAR_NAME` ASC) USING BTREE,
  INDEX `IDX_QRTZ_T_G`(`SCHED_NAME` ASC, `TRIGGER_GROUP` ASC) USING BTREE,
  INDEX `IDX_QRTZ_T_STATE`(`SCHED_NAME` ASC, `TRIGGER_STATE` ASC) USING BTREE,
  INDEX `IDX_QRTZ_T_N_STATE`(`SCHED_NAME` ASC, `TRIGGER_NAME` ASC, `TRIGGER_GROUP` ASC, `TRIGGER_STATE` ASC) USING BTREE,
  INDEX `IDX_QRTZ_T_N_G_STATE`(`SCHED_NAME` ASC, `TRIGGER_GROUP` ASC, `TRIGGER_STATE` ASC) USING BTREE,
  INDEX `IDX_QRTZ_T_NEXT_FIRE_TIME`(`SCHED_NAME` ASC, `NEXT_FIRE_TIME` ASC) USING BTREE,
  INDEX `IDX_QRTZ_T_NFT_ST`(`SCHED_NAME` ASC, `TRIGGER_STATE` ASC, `NEXT_FIRE_TIME` ASC) USING BTREE,
  INDEX `IDX_QRTZ_T_NFT_MISFIRE`(`SCHED_NAME` ASC, `MISFIRE_INSTR` ASC, `NEXT_FIRE_TIME` ASC) USING BTREE,
  INDEX `IDX_QRTZ_T_NFT_ST_MISFIRE`(`SCHED_NAME` ASC, `MISFIRE_INSTR` ASC, `NEXT_FIRE_TIME` ASC, `TRIGGER_STATE` ASC) USING BTREE,
  INDEX `IDX_QRTZ_T_NFT_ST_MISFIRE_GRP`(`SCHED_NAME` ASC, `MISFIRE_INSTR` ASC, `NEXT_FIRE_TIME` ASC, `TRIGGER_GROUP` ASC, `TRIGGER_STATE` ASC) USING BTREE,
  CONSTRAINT `js_job_triggers_ibfk_1` FOREIGN KEY (`SCHED_NAME`, `JOB_NAME`, `JOB_GROUP`) REFERENCES `js_job_job_details` (`SCHED_NAME`, `JOB_NAME`, `JOB_GROUP`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of js_job_triggers
-- ----------------------------

-- ----------------------------
-- Table structure for js_sys_area
-- ----------------------------
DROP TABLE IF EXISTS `js_sys_area`;
CREATE TABLE `js_sys_area`  (
  `area_code` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '区域编码',
  `parent_code` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '父级编号',
  `parent_codes` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '所有父级编号',
  `tree_sort` decimal(10, 0) NOT NULL COMMENT '本级排序号（升序）',
  `tree_sorts` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '所有级别排序号',
  `tree_leaf` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '是否最末级',
  `tree_level` decimal(4, 0) NOT NULL COMMENT '层次级别',
  `tree_names` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '全节点名',
  `area_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '区域名称',
  `area_type` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '区域类型',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '0' COMMENT '状态（0正常 1删除 2停用）',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '创建者',
  `create_date` datetime NOT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '更新者',
  `update_date` datetime NOT NULL COMMENT '更新时间',
  `remarks` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '备注信息',
  PRIMARY KEY (`area_code`) USING BTREE,
  INDEX `idx_sys_area_pc`(`parent_code` ASC) USING BTREE,
  INDEX `idx_sys_area_ts`(`tree_sort` ASC) USING BTREE,
  INDEX `idx_sys_area_status`(`status` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '行政区划' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of js_sys_area
-- ----------------------------
INSERT INTO `js_sys_area` VALUES ('370000', '0', '0,', 370000, '0000370000,', '0', 0, '山东省', '山东省', '1', '0', 'system', '2019-01-15 10:51:55', 'system', '2019-01-15 10:51:55', NULL);
INSERT INTO `js_sys_area` VALUES ('370100', '370000', '0,370000,', 370100, '0000370000,0000370100,', '0', 1, '山东省/济南市', '济南市', '2', '0', 'system', '2019-01-15 10:51:55', 'system', '2019-01-15 10:51:55', NULL);
INSERT INTO `js_sys_area` VALUES ('370102', '370100', '0,370000,370100,', 370102, '0000370000,0000370100,0000370102,', '1', 2, '山东省/济南市/历下区', '历下区', '3', '0', 'system', '2019-01-15 10:51:55', 'system', '2019-01-15 10:51:55', NULL);
INSERT INTO `js_sys_area` VALUES ('370103', '370100', '0,370000,370100,', 370103, '0000370000,0000370100,0000370103,', '1', 2, '山东省/济南市/市中区', '市中区', '3', '0', 'system', '2019-01-15 10:51:55', 'system', '2019-01-15 10:51:55', NULL);
INSERT INTO `js_sys_area` VALUES ('370104', '370100', '0,370000,370100,', 370104, '0000370000,0000370100,0000370104,', '1', 2, '山东省/济南市/槐荫区', '槐荫区', '3', '0', 'system', '2019-01-15 10:51:55', 'system', '2019-01-15 10:51:55', NULL);
INSERT INTO `js_sys_area` VALUES ('370105', '370100', '0,370000,370100,', 370105, '0000370000,0000370100,0000370105,', '1', 2, '山东省/济南市/天桥区', '天桥区', '3', '0', 'system', '2019-01-15 10:51:55', 'system', '2019-01-15 10:51:55', NULL);
INSERT INTO `js_sys_area` VALUES ('370112', '370100', '0,370000,370100,', 370112, '0000370000,0000370100,0000370112,', '1', 2, '山东省/济南市/历城区', '历城区', '3', '0', 'system', '2019-01-15 10:51:55', 'system', '2019-01-15 10:51:56', NULL);
INSERT INTO `js_sys_area` VALUES ('370113', '370100', '0,370000,370100,', 370113, '0000370000,0000370100,0000370113,', '1', 2, '山东省/济南市/长清区', '长清区', '3', '0', 'system', '2019-01-15 10:51:56', 'system', '2019-01-15 10:51:56', NULL);
INSERT INTO `js_sys_area` VALUES ('370114', '370100', '0,370000,370100,', 370114, '0000370000,0000370100,0000370114,', '1', 2, '山东省/济南市/章丘区', '章丘区', '3', '0', 'system', '2019-01-15 10:51:56', 'system', '2019-01-15 10:51:56', NULL);
INSERT INTO `js_sys_area` VALUES ('370124', '370100', '0,370000,370100,', 370124, '0000370000,0000370100,0000370124,', '1', 2, '山东省/济南市/平阴县', '平阴县', '3', '0', 'system', '2019-01-15 10:51:56', 'system', '2019-01-15 10:51:56', NULL);
INSERT INTO `js_sys_area` VALUES ('370125', '370100', '0,370000,370100,', 370125, '0000370000,0000370100,0000370125,', '1', 2, '山东省/济南市/济阳县', '济阳县', '3', '0', 'system', '2019-01-15 10:51:56', 'system', '2019-01-15 10:51:56', NULL);
INSERT INTO `js_sys_area` VALUES ('370126', '370100', '0,370000,370100,', 370126, '0000370000,0000370100,0000370126,', '1', 2, '山东省/济南市/商河县', '商河县', '3', '0', 'system', '2019-01-15 10:51:56', 'system', '2019-01-15 10:51:56', NULL);
INSERT INTO `js_sys_area` VALUES ('370200', '370000', '0,370000,', 370200, '0000370000,0000370200,', '0', 1, '山东省/青岛市', '青岛市', '2', '0', 'system', '2019-01-15 10:51:55', 'system', '2019-01-15 10:51:55', NULL);
INSERT INTO `js_sys_area` VALUES ('370202', '370200', '0,370000,370200,', 370202, '0000370000,0000370200,0000370202,', '1', 2, '山东省/青岛市/市南区', '市南区', '3', '0', 'system', '2019-01-15 10:51:56', 'system', '2019-01-15 10:51:56', NULL);
INSERT INTO `js_sys_area` VALUES ('370203', '370200', '0,370000,370200,', 370203, '0000370000,0000370200,0000370203,', '1', 2, '山东省/青岛市/市北区', '市北区', '3', '0', 'system', '2019-01-15 10:51:56', 'system', '2019-01-15 10:51:56', NULL);
INSERT INTO `js_sys_area` VALUES ('370211', '370200', '0,370000,370200,', 370211, '0000370000,0000370200,0000370211,', '1', 2, '山东省/青岛市/黄岛区', '黄岛区', '3', '0', 'system', '2019-01-15 10:51:56', 'system', '2019-01-15 10:51:56', NULL);
INSERT INTO `js_sys_area` VALUES ('370212', '370200', '0,370000,370200,', 370212, '0000370000,0000370200,0000370212,', '1', 2, '山东省/青岛市/崂山区', '崂山区', '3', '0', 'system', '2019-01-15 10:51:56', 'system', '2019-01-15 10:51:56', NULL);
INSERT INTO `js_sys_area` VALUES ('370213', '370200', '0,370000,370200,', 370213, '0000370000,0000370200,0000370213,', '1', 2, '山东省/青岛市/李沧区', '李沧区', '3', '0', 'system', '2019-01-15 10:51:56', 'system', '2019-01-15 10:51:56', NULL);
INSERT INTO `js_sys_area` VALUES ('370214', '370200', '0,370000,370200,', 370214, '0000370000,0000370200,0000370214,', '1', 2, '山东省/青岛市/城阳区', '城阳区', '3', '0', 'system', '2019-01-15 10:51:56', 'system', '2019-01-15 10:51:56', NULL);
INSERT INTO `js_sys_area` VALUES ('370281', '370200', '0,370000,370200,', 370281, '0000370000,0000370200,0000370281,', '1', 2, '山东省/青岛市/胶州市', '胶州市', '3', '0', 'system', '2019-01-15 10:51:56', 'system', '2019-01-15 10:51:56', NULL);
INSERT INTO `js_sys_area` VALUES ('370282', '370200', '0,370000,370200,', 370282, '0000370000,0000370200,0000370282,', '1', 2, '山东省/青岛市/即墨区', '即墨区', '3', '0', 'system', '2019-01-15 10:51:56', 'system', '2019-01-15 10:51:56', NULL);
INSERT INTO `js_sys_area` VALUES ('370283', '370200', '0,370000,370200,', 370283, '0000370000,0000370200,0000370283,', '1', 2, '山东省/青岛市/平度市', '平度市', '3', '0', 'system', '2019-01-15 10:51:56', 'system', '2019-01-15 10:51:56', NULL);
INSERT INTO `js_sys_area` VALUES ('370285', '370200', '0,370000,370200,', 370285, '0000370000,0000370200,0000370285,', '1', 2, '山东省/青岛市/莱西市', '莱西市', '3', '0', 'system', '2019-01-15 10:51:56', 'system', '2019-01-15 10:51:56', NULL);

-- ----------------------------
-- Table structure for js_sys_company
-- ----------------------------
DROP TABLE IF EXISTS `js_sys_company`;
CREATE TABLE `js_sys_company`  (
  `company_code` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '公司编码',
  `parent_code` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '父级编号',
  `parent_codes` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '所有父级编号',
  `tree_sort` decimal(10, 0) NOT NULL COMMENT '本级排序号（升序）',
  `tree_sorts` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '所有级别排序号',
  `tree_leaf` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '是否最末级',
  `tree_level` decimal(4, 0) NOT NULL COMMENT '层次级别',
  `tree_names` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '全节点名',
  `view_code` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '公司代码',
  `company_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '公司名称',
  `full_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '公司全称',
  `area_code` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '区域编码',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '0' COMMENT '状态（0正常 1删除 2停用）',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '创建者',
  `create_date` datetime NOT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '更新者',
  `update_date` datetime NOT NULL COMMENT '更新时间',
  `remarks` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '备注信息',
  `corp_code` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '0' COMMENT '租户代码',
  `corp_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'JeeSite' COMMENT '租户名称',
  `extend_s1` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '扩展 String 1',
  `extend_s2` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '扩展 String 2',
  `extend_s3` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '扩展 String 3',
  `extend_s4` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '扩展 String 4',
  `extend_s5` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '扩展 String 5',
  `extend_s6` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '扩展 String 6',
  `extend_s7` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '扩展 String 7',
  `extend_s8` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '扩展 String 8',
  `extend_i1` decimal(19, 0) NULL DEFAULT NULL COMMENT '扩展 Integer 1',
  `extend_i2` decimal(19, 0) NULL DEFAULT NULL COMMENT '扩展 Integer 2',
  `extend_i3` decimal(19, 0) NULL DEFAULT NULL COMMENT '扩展 Integer 3',
  `extend_i4` decimal(19, 0) NULL DEFAULT NULL COMMENT '扩展 Integer 4',
  `extend_f1` decimal(19, 4) NULL DEFAULT NULL COMMENT '扩展 Float 1',
  `extend_f2` decimal(19, 4) NULL DEFAULT NULL COMMENT '扩展 Float 2',
  `extend_f3` decimal(19, 4) NULL DEFAULT NULL COMMENT '扩展 Float 3',
  `extend_f4` decimal(19, 4) NULL DEFAULT NULL COMMENT '扩展 Float 4',
  `extend_d1` datetime NULL DEFAULT NULL COMMENT '扩展 Date 1',
  `extend_d2` datetime NULL DEFAULT NULL COMMENT '扩展 Date 2',
  `extend_d3` datetime NULL DEFAULT NULL COMMENT '扩展 Date 3',
  `extend_d4` datetime NULL DEFAULT NULL COMMENT '扩展 Date 4',
  PRIMARY KEY (`company_code`) USING BTREE,
  INDEX `idx_sys_company_cc`(`corp_code` ASC) USING BTREE,
  INDEX `idx_sys_company_pc`(`parent_code` ASC) USING BTREE,
  INDEX `idx_sys_company_ts`(`tree_sort` ASC) USING BTREE,
  INDEX `idx_sys_company_status`(`status` ASC) USING BTREE,
  INDEX `idx_sys_company_vc`(`view_code` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '公司表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of js_sys_company
-- ----------------------------

-- ----------------------------
-- Table structure for js_sys_company_office
-- ----------------------------
DROP TABLE IF EXISTS `js_sys_company_office`;
CREATE TABLE `js_sys_company_office`  (
  `company_code` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '公司编码',
  `office_code` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '机构编码',
  PRIMARY KEY (`company_code`, `office_code`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '公司部门关联表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of js_sys_company_office
-- ----------------------------

-- ----------------------------
-- Table structure for js_sys_config
-- ----------------------------
DROP TABLE IF EXISTS `js_sys_config`;
CREATE TABLE `js_sys_config`  (
  `id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '编号',
  `config_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '名称',
  `config_key` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '参数键',
  `config_value` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '参数值',
  `is_sys` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '系统内置（1是 0否）',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '创建者',
  `create_date` datetime NOT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '更新者',
  `update_date` datetime NOT NULL COMMENT '更新时间',
  `remarks` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '备注信息',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `idx_sys_config_key`(`config_key` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '参数配置表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of js_sys_config
-- ----------------------------
INSERT INTO `js_sys_config` VALUES ('1085006641583296512', '研发工具-代码生成默认包名', 'gen.defaultPackageName', 'com.jeesite.modules', '0', 'system', '2019-01-15 10:51:56', 'system', '2019-01-15 10:51:56', '新建项目后，修改该键值，在生成代码的时候就不要再修改了');
INSERT INTO `js_sys_config` VALUES ('1085006641797206016', '主框架页-桌面仪表盘首页地址', 'sys.index.desktopUrl', '/osee/desktop', '0', 'system', '2019-01-15 10:51:57', 'system', '2019-01-18 09:52:59', '主页面的第一个页签首页桌面地址');
INSERT INTO `js_sys_config` VALUES ('1085006641923035136', '主框架页-侧边栏的默认显示样式', 'sys.index.sidebarStyle', '1', '0', 'system', '2019-01-15 10:51:57', 'system', '2019-01-15 10:51:57', '1：默认显示侧边栏；2：默认折叠侧边栏');
INSERT INTO `js_sys_config` VALUES ('1085006642048864256', '主框架页-默认皮肤样式名称', 'sys.index.skinName', 'skin-blue-light', '0', 'system', '2019-01-15 10:51:57', 'system', '2019-01-15 10:51:57', 'skin-black-light、skin-black、skin-blue-light、skin-blue、skin-green-light、skin-green、skin-purple-light、skin-purple、skin-red-light、skin-red、skin-yellow-light、skin-yellow');
INSERT INTO `js_sys_config` VALUES ('1085006642149527552', '用户登录-登录失败多少次数后显示验证码', 'sys.login.failedNumAfterValidCode', '100', '0', 'system', '2019-01-15 10:51:57', 'system', '2019-01-15 10:51:57', '设置为0强制使用验证码登录');
INSERT INTO `js_sys_config` VALUES ('1085006642266968064', '用户登录-登录失败多少次数后锁定账号', 'sys.login.failedNumAfterLockAccount', '200', '0', 'system', '2019-01-15 10:51:57', 'system', '2019-01-15 10:51:57', '登录失败多少次数后锁定账号');
INSERT INTO `js_sys_config` VALUES ('1085006642367631360', '用户登录-登录失败多少次数后锁定账号的时间', 'sys.login.failedNumAfterLockMinute', '20', '0', 'system', '2019-01-15 10:51:57', 'system', '2019-01-15 10:51:57', '锁定账号的时间（单位：分钟）');
INSERT INTO `js_sys_config` VALUES ('1085006642459906048', '账号自助-是否开启用户注册功能', 'sys.account.registerUser', 'true', '0', 'system', '2019-01-15 10:51:57', 'system', '2019-01-15 10:51:57', '是否开启注册用户功能');
INSERT INTO `js_sys_config` VALUES ('1085006642577346560', '账号自助-允许自助注册的用户类型', 'sys.account.registerUser.userTypes', '-1', '0', 'system', '2019-01-15 10:51:57', 'system', '2019-01-15 10:51:57', '允许注册的用户类型（多个用逗号隔开，如果注册时不选择用户类型，则第一个为默认类型）');
INSERT INTO `js_sys_config` VALUES ('1085006642678009856', '账号自助-验证码有效时间（分钟）', 'sys.account.validCodeTimeout', '10', '0', 'system', '2019-01-15 10:51:57', 'system', '2019-01-15 10:51:57', '找回密码时，短信/邮件验证码有效时间（单位：分钟，0表示不限制）');
INSERT INTO `js_sys_config` VALUES ('1085006642787061760', '用户管理-账号默认角色-员工类型', 'sys.user.defaultRoleCodes.employee', 'default', '0', 'system', '2019-01-15 10:51:57', 'system', '2019-01-15 10:51:57', '所有员工用户都拥有的角色权限（适用于菜单授权查询）');
INSERT INTO `js_sys_config` VALUES ('1085006642896113664', '用户管理-账号初始密码', 'sys.user.initPassword', '123456', '0', 'system', '2019-01-15 10:51:57', 'system', '2019-01-15 10:51:57', '创建用户和重置密码的时候用户的密码');
INSERT INTO `js_sys_config` VALUES ('1085006642988388352', '用户管理-初始密码修改策略', 'sys.user.initPasswordModify', '1', '0', 'system', '2019-01-15 10:51:57', 'system', '2019-01-15 10:51:57', '0：初始密码修改策略关闭，没有任何提示；1：提醒用户，如果未修改初始密码，则在登录时和点击菜单就会提醒修改密码对话框；2：强制实行初始密码修改，登录后若不修改密码则不能进行系统操作');
INSERT INTO `js_sys_config` VALUES ('1085006643093245952', '用户管理-账号密码修改策略', 'sys.user.passwordModify', '0', '0', 'system', '2019-01-15 10:51:57', 'system', '2019-04-22 09:21:44', '0：密码修改策略关闭，没有任何提示；1：提醒用户，如果未修改初始密码，则在登录时和点击菜单就会提醒修改密码对话框；2：强制实行初始密码修改，登录后若不修改密码则不能进行系统操作。');
INSERT INTO `js_sys_config` VALUES ('1085006643198103552', '用户管理-账号密码修改策略验证周期', 'sys.user.passwordModifyCycle', '30', '0', 'system', '2019-01-15 10:51:57', 'system', '2019-01-15 10:51:57', '密码安全修改周期是指定时间内提醒或必须修改密码（例如设置30天）的验证时间（天），超过这个时间登录系统时需，提醒用户修改密码或强制修改密码才能继续使用系统。单位：天，如果设置30天，则第31天开始强制修改密码');
INSERT INTO `js_sys_config` VALUES ('1085006643315544064', '用户管理-密码修改多少次内不允许重复', 'sys.user.passwordModifyNotRepeatNum', '1', '0', 'system', '2019-01-15 10:51:57', 'system', '2019-01-15 10:51:57', '默认1次，表示不能与上次密码重复；若设置为3，表示并与前3次密码重复');
INSERT INTO `js_sys_config` VALUES ('1085006643449761792', '用户管理-账号密码修改最低安全等级', 'sys.user.passwordModifySecurityLevel', '0', '0', 'system', '2019-01-15 10:51:57', 'system', '2019-01-15 10:51:57', '0：不限制等级（用户在修改密码的时候不进行等级验证）\r；1：限制最低等级为很弱\r；2：限制最低等级为弱\r；3：限制最低等级为安全\r；4：限制最低等级为很安全');
INSERT INTO `js_sys_config` VALUES ('1092344803460943872', '主框架页-主导航菜单显示风格', 'sys.index.menuStyle', '1', '0', 'system', '2023-05-23 11:33:49', 'system', '2023-05-23 11:33:49', '1：菜单全部在左侧；2：根菜单显示在顶部');

-- ----------------------------
-- Table structure for js_sys_dict_data
-- ----------------------------
DROP TABLE IF EXISTS `js_sys_dict_data`;
CREATE TABLE `js_sys_dict_data`  (
  `dict_code` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '字典编码',
  `parent_code` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '父级编号',
  `parent_codes` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '所有父级编号',
  `tree_sort` decimal(10, 0) NOT NULL COMMENT '本级排序号（升序）',
  `tree_sorts` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '所有级别排序号',
  `tree_leaf` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '是否最末级',
  `tree_level` decimal(4, 0) NOT NULL COMMENT '层次级别',
  `tree_names` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '全节点名',
  `dict_label` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '字典标签',
  `dict_value` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '字典键值',
  `dict_type` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '字典类型',
  `is_sys` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '系统内置（1是 0否）',
  `description` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '字典描述',
  `css_style` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT 'css样式（如：color:red)',
  `css_class` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT 'css类名（如：red）',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '0' COMMENT '状态（0正常 1删除 2停用）',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '创建者',
  `create_date` datetime NOT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '更新者',
  `update_date` datetime NOT NULL COMMENT '更新时间',
  `remarks` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '备注信息',
  `corp_code` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '0' COMMENT '租户代码',
  `corp_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'JeeSite' COMMENT '租户名称',
  `extend_s1` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '扩展 String 1',
  `extend_s2` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '扩展 String 2',
  `extend_s3` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '扩展 String 3',
  `extend_s4` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '扩展 String 4',
  `extend_s5` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '扩展 String 5',
  `extend_s6` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '扩展 String 6',
  `extend_s7` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '扩展 String 7',
  `extend_s8` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '扩展 String 8',
  `extend_i1` decimal(19, 0) NULL DEFAULT NULL COMMENT '扩展 Integer 1',
  `extend_i2` decimal(19, 0) NULL DEFAULT NULL COMMENT '扩展 Integer 2',
  `extend_i3` decimal(19, 0) NULL DEFAULT NULL COMMENT '扩展 Integer 3',
  `extend_i4` decimal(19, 0) NULL DEFAULT NULL COMMENT '扩展 Integer 4',
  `extend_f1` decimal(19, 4) NULL DEFAULT NULL COMMENT '扩展 Float 1',
  `extend_f2` decimal(19, 4) NULL DEFAULT NULL COMMENT '扩展 Float 2',
  `extend_f3` decimal(19, 4) NULL DEFAULT NULL COMMENT '扩展 Float 3',
  `extend_f4` decimal(19, 4) NULL DEFAULT NULL COMMENT '扩展 Float 4',
  `extend_d1` datetime NULL DEFAULT NULL COMMENT '扩展 Date 1',
  `extend_d2` datetime NULL DEFAULT NULL COMMENT '扩展 Date 2',
  `extend_d3` datetime NULL DEFAULT NULL COMMENT '扩展 Date 3',
  `extend_d4` datetime NULL DEFAULT NULL COMMENT '扩展 Date 4',
  PRIMARY KEY (`dict_code`) USING BTREE,
  INDEX `idx_sys_dict_data_cc`(`corp_code` ASC) USING BTREE,
  INDEX `idx_sys_dict_data_dt`(`dict_type` ASC) USING BTREE,
  INDEX `idx_sys_dict_data_pc`(`parent_code` ASC) USING BTREE,
  INDEX `idx_sys_dict_data_status`(`status` ASC) USING BTREE,
  INDEX `idx_sys_dict_data_ts`(`tree_sort` ASC) USING BTREE,
  INDEX `idx_sys_dict_data_dv`(`dict_value` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '字典数据表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of js_sys_dict_data
-- ----------------------------
INSERT INTO `js_sys_dict_data` VALUES ('1085006649225318400', '0', '0,', 30, '0000000030,', '1', 0, '是', '是', '1', 'sys_yes_no', '1', '', '', '', '0', 'system', '2019-01-15 10:51:58', 'system', '2019-01-15 10:51:58', NULL, '0', 'JeeSite', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `js_sys_dict_data` VALUES ('1085006649485365248', '0', '0,', 40, '0000000040,', '1', 0, '否', '否', '0', 'sys_yes_no', '1', '', 'color:#aaa;', '', '0', 'system', '2019-01-15 10:51:58', 'system', '2019-01-15 10:51:58', NULL, '0', 'JeeSite', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `js_sys_dict_data` VALUES ('1085006649619582976', '0', '0,', 20, '0000000020,', '1', 0, '正常', '正常', '0', 'sys_status', '1', '', '', '', '0', 'system', '2019-01-15 10:51:58', 'system', '2019-01-15 10:51:58', NULL, '0', 'JeeSite', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `js_sys_dict_data` VALUES ('1085006649749606400', '0', '0,', 30, '0000000030,', '1', 0, '删除', '删除', '1', 'sys_status', '1', '', 'color:#f00;', '', '0', 'system', '2019-01-15 10:51:58', 'system', '2019-01-15 10:51:58', NULL, '0', 'JeeSite', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `js_sys_dict_data` VALUES ('1085006649900601344', '0', '0,', 40, '0000000040,', '1', 0, '停用', '停用', '2', 'sys_status', '1', '', 'color:#f00;', '', '0', 'system', '2019-01-15 10:51:58', 'system', '2019-01-15 10:51:58', NULL, '0', 'JeeSite', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `js_sys_dict_data` VALUES ('1085006650051596288', '0', '0,', 50, '0000000050,', '1', 0, '冻结', '冻结', '3', 'sys_status', '1', '', 'color:#fa0;', '', '0', 'system', '2019-01-15 10:51:58', 'system', '2019-01-15 10:51:59', NULL, '0', 'JeeSite', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `js_sys_dict_data` VALUES ('1085006650190008320', '0', '0,', 60, '0000000060,', '1', 0, '待审', '待审', '4', 'sys_status', '1', '', '', '', '0', 'system', '2019-01-15 10:51:59', 'system', '2019-01-15 10:51:59', NULL, '0', 'JeeSite', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `js_sys_dict_data` VALUES ('1085006650374557696', '0', '0,', 70, '0000000070,', '1', 0, '驳回', '驳回', '5', 'sys_status', '1', '', '', '', '0', 'system', '2019-01-15 10:51:59', 'system', '2019-01-15 10:51:59', NULL, '0', 'JeeSite', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `js_sys_dict_data` VALUES ('1085006650575884288', '0', '0,', 80, '0000000080,', '1', 0, '草稿', '草稿', '9', 'sys_status', '1', '', 'color:#aaa;', '', '0', 'system', '2019-01-15 10:51:59', 'system', '2019-01-15 10:51:59', NULL, '0', 'JeeSite', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `js_sys_dict_data` VALUES ('1085006650739462144', '0', '0,', 30, '0000000030,', '1', 0, '显示', '显示', '1', 'sys_show_hide', '1', '', '', '', '0', 'system', '2019-01-15 10:51:59', 'system', '2019-01-15 10:51:59', NULL, '0', 'JeeSite', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `js_sys_dict_data` VALUES ('1085006650932400128', '0', '0,', 40, '0000000040,', '1', 0, '隐藏', '隐藏', '0', 'sys_show_hide', '1', '', 'color:#aaa;', '', '0', 'system', '2019-01-15 10:51:59', 'system', '2019-01-15 10:51:59', NULL, '0', 'JeeSite', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `js_sys_dict_data` VALUES ('1085006651146309632', '0', '0,', 30, '0000000030,', '1', 0, '简体中文', '简体中文', 'zh_CN', 'sys_lang_type', '1', '', '', '', '0', 'system', '2019-01-15 10:51:59', 'system', '2019-01-15 10:51:59', NULL, '0', 'JeeSite', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `js_sys_dict_data` VALUES ('1085006651385384960', '0', '0,', 40, '0000000040,', '1', 0, '英语', '英语', 'en', 'sys_lang_type', '1', '', '', '', '0', 'system', '2019-01-15 10:51:59', 'system', '2019-01-15 10:51:59', NULL, '0', 'JeeSite', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `js_sys_dict_data` VALUES ('1085006651670597632', '0', '0,', 30, '0000000030,', '1', 0, 'PC电脑', 'PC电脑', 'pc', 'sys_device_type', '1', '', '', '', '0', 'system', '2019-01-15 10:51:59', 'system', '2019-01-15 10:51:59', NULL, '0', 'JeeSite', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `js_sys_dict_data` VALUES ('1085006651947421696', '0', '0,', 40, '0000000040,', '1', 0, '手机APP', '手机APP', 'mobileApp', 'sys_device_type', '1', '', '', '', '0', 'system', '2019-01-15 10:51:59', 'system', '2019-01-15 10:51:59', NULL, '0', 'JeeSite', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `js_sys_dict_data` VALUES ('1085006652396212224', '0', '0,', 50, '0000000050,', '1', 0, '手机Web', '手机Web', 'mobileWeb', 'sys_device_type', '1', '', '', '', '0', 'system', '2019-01-15 10:51:59', 'system', '2019-01-15 10:51:59', NULL, '0', 'JeeSite', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `js_sys_dict_data` VALUES ('1085006652710785024', '0', '0,', 60, '0000000060,', '1', 0, '微信设备', '微信设备', 'weixin', 'sys_device_type', '1', '', '', '', '0', 'system', '2019-01-15 10:51:59', 'system', '2019-01-15 10:51:59', NULL, '0', 'JeeSite', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `js_sys_dict_data` VALUES ('1085006652899528704', '0', '0,', 30, '0000000030,', '1', 0, '主导航菜单', '主导航菜单', 'default', 'sys_menu_sys_code', '1', '', '', '', '0', 'system', '2019-01-15 10:51:59', 'system', '2019-01-15 10:51:59', NULL, '0', 'JeeSite', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `js_sys_dict_data` VALUES ('1085006653063106560', '0', '0,', 30, '0000000030,', '1', 0, '菜单', '菜单', '1', 'sys_menu_type', '1', '', '', '', '0', 'system', '2019-01-15 10:51:59', 'system', '2019-01-15 10:51:59', NULL, '0', 'JeeSite', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `js_sys_dict_data` VALUES ('1085006653205712896', '0', '0,', 40, '0000000040,', '1', 0, '权限', '权限', '2', 'sys_menu_type', '1', '', 'color:#c243d6;', '', '0', 'system', '2019-01-15 10:51:59', 'system', '2019-01-15 10:51:59', NULL, '0', 'JeeSite', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `js_sys_dict_data` VALUES ('1085006653386067968', '0', '0,', 30, '0000000030,', '1', 0, '默认权重', '默认权重', '20', 'sys_menu_weight', '1', '', '', '', '0', 'system', '2019-01-15 10:51:59', 'system', '2019-01-15 10:51:59', NULL, '0', 'JeeSite', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `js_sys_dict_data` VALUES ('1085006653537062912', '0', '0,', 40, '0000000040,', '1', 0, '二级管理员', '二级管理员', '40', 'sys_menu_weight', '1', '', '', '', '0', 'system', '2019-01-15 10:51:59', 'system', '2019-01-15 10:51:59', NULL, '0', 'JeeSite', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `js_sys_dict_data` VALUES ('1085006653700640768', '0', '0,', 50, '0000000050,', '1', 0, '系统管理员', '系统管理员', '60', 'sys_menu_weight', '1', '', '', '', '0', 'system', '2019-01-15 10:51:59', 'system', '2019-01-15 10:51:59', NULL, '0', 'JeeSite', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `js_sys_dict_data` VALUES ('1085006653918744576', '0', '0,', 60, '0000000060,', '1', 0, '超级管理员', '超级管理员', '80', 'sys_menu_weight', '1', '', 'color:#c243d6;', '', '0', 'system', '2019-01-15 10:51:59', 'system', '2019-01-15 10:51:59', NULL, '0', 'JeeSite', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `js_sys_dict_data` VALUES ('1085006654086516736', '0', '0,', 30, '0000000030,', '1', 0, '国家', '国家', '0', 'sys_area_type', '1', '', '', '', '0', 'system', '2019-01-15 10:51:59', 'system', '2019-01-15 10:51:59', NULL, '0', 'JeeSite', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `js_sys_dict_data` VALUES ('1085006654258483200', '0', '0,', 40, '0000000040,', '1', 0, '省份直辖市', '省份直辖市', '1', 'sys_area_type', '1', '', '', '', '0', 'system', '2019-01-15 10:51:59', 'system', '2019-01-15 10:52:00', NULL, '0', 'JeeSite', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `js_sys_dict_data` VALUES ('1085006654505947136', '0', '0,', 50, '0000000050,', '1', 0, '地市', '地市', '2', 'sys_area_type', '1', '', '', '', '0', 'system', '2019-01-15 10:52:00', 'system', '2019-01-15 10:52:00', NULL, '0', 'JeeSite', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `js_sys_dict_data` VALUES ('1085006654749216768', '0', '0,', 60, '0000000060,', '1', 0, '区县', '区县', '3', 'sys_area_type', '1', '', '', '', '0', 'system', '2019-01-15 10:52:00', 'system', '2019-01-15 10:52:00', NULL, '0', 'JeeSite', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `js_sys_dict_data` VALUES ('1085006654900211712', '0', '0,', 30, '0000000030,', '1', 0, '省级公司', '省级公司', '1', 'sys_office_type', '1', '', '', '', '0', 'system', '2019-01-15 10:52:00', 'system', '2019-01-15 10:52:00', NULL, '0', 'JeeSite', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `js_sys_dict_data` VALUES ('1085006655097344000', '0', '0,', 40, '0000000040,', '1', 0, '市级公司', '市级公司', '2', 'sys_office_type', '1', '', '', '', '0', 'system', '2019-01-15 10:52:00', 'system', '2019-01-15 10:52:00', NULL, '0', 'JeeSite', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `js_sys_dict_data` VALUES ('1085006655260921856', '0', '0,', 50, '0000000050,', '1', 0, '部门', '部门', '3', 'sys_office_type', '1', '', '', '', '0', 'system', '2019-01-15 10:52:00', 'system', '2019-01-15 10:52:00', NULL, '0', 'JeeSite', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `js_sys_dict_data` VALUES ('1085006655437082624', '0', '0,', 30, '0000000030,', '1', 0, '正常', '正常', '0', 'sys_search_status', '1', '', '', '', '0', 'system', '2019-01-15 10:52:00', 'system', '2019-01-15 10:52:00', NULL, '0', 'JeeSite', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `js_sys_dict_data` VALUES ('1085006655588077568', '0', '0,', 40, '0000000040,', '1', 0, '停用', '停用', '2', 'sys_search_status', '1', '', 'color:#f00;', '', '0', 'system', '2019-01-15 10:52:00', 'system', '2019-01-15 10:52:00', NULL, '0', 'JeeSite', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `js_sys_dict_data` VALUES ('1085006655718100992', '0', '0,', 30, '0000000030,', '1', 0, '男', '男', '1', 'sys_user_sex', '1', '', '', '', '0', 'system', '2019-01-15 10:52:00', 'system', '2019-01-15 10:52:00', NULL, '0', 'JeeSite', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `js_sys_dict_data` VALUES ('1085006655881678848', '0', '0,', 40, '0000000040,', '1', 0, '女', '女', '2', 'sys_user_sex', '1', '', '', '', '0', 'system', '2019-01-15 10:52:00', 'system', '2019-01-15 10:52:00', NULL, '0', 'JeeSite', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `js_sys_dict_data` VALUES ('1085006656032673792', '0', '0,', 30, '0000000030,', '1', 0, '正常', '正常', '0', 'sys_user_status', '1', '', '', '', '0', 'system', '2019-01-15 10:52:00', 'system', '2019-01-15 10:52:00', NULL, '0', 'JeeSite', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `js_sys_dict_data` VALUES ('1085006656175280128', '0', '0,', 40, '0000000040,', '1', 0, '停用', '停用', '2', 'sys_user_status', '1', '', 'color:#f00;', '', '0', 'system', '2019-01-15 10:52:00', 'system', '2019-01-15 10:52:00', NULL, '0', 'JeeSite', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `js_sys_dict_data` VALUES ('1085006656317886464', '0', '0,', 50, '0000000050,', '1', 0, '冻结', '冻结', '3', 'sys_user_status', '1', '', 'color:#fa0;', '', '0', 'system', '2019-01-15 10:52:00', 'system', '2019-01-15 10:52:00', NULL, '0', 'JeeSite', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `js_sys_dict_data` VALUES ('1085006656460492800', '0', '0,', 30, '0000000030,', '1', 0, '员工', '员工', 'employee', 'sys_user_type', '1', '', '', '', '0', 'system', '2019-01-15 10:52:00', 'system', '2019-01-15 10:52:00', NULL, '0', 'JeeSite', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `js_sys_dict_data` VALUES ('1085006656624070656', '0', '0,', 40, '0000000040,', '1', 0, '会员', '会员', 'member', 'sys_user_type', '1', '', '', '', '0', 'system', '2019-01-15 10:52:00', 'system', '2019-01-15 10:52:00', NULL, '0', 'JeeSite', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `js_sys_dict_data` VALUES ('1085006656791842816', '0', '0,', 50, '0000000050,', '1', 0, '单位', '单位', 'btype', 'sys_user_type', '1', '', '', '', '0', 'system', '2019-01-15 10:52:00', 'system', '2019-01-15 10:52:00', NULL, '0', 'JeeSite', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `js_sys_dict_data` VALUES ('1085006656938643456', '0', '0,', 60, '0000000060,', '1', 0, '个人', '个人', 'persion', 'sys_user_type', '1', '', '', '', '0', 'system', '2019-01-15 10:52:00', 'system', '2019-01-15 10:52:00', NULL, '0', 'JeeSite', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `js_sys_dict_data` VALUES ('1085006657215467520', '0', '0,', 70, '0000000070,', '1', 0, '专家', '专家', 'expert', 'sys_user_type', '1', '', '', '', '0', 'system', '2019-01-15 10:52:00', 'system', '2019-01-15 10:52:00', NULL, '0', 'JeeSite', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `js_sys_dict_data` VALUES ('1085006657504874496', '0', '0,', 30, '0000000030,', '1', 0, '高管', '高管', '1', 'sys_role_type', '1', '', '', '', '0', 'system', '2019-01-15 10:52:00', 'system', '2019-01-15 10:52:00', NULL, '0', 'JeeSite', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `js_sys_dict_data` VALUES ('1085006657785892864', '0', '0,', 40, '0000000040,', '1', 0, '中层', '中层', '2', 'sys_role_type', '1', '', '', '', '0', 'system', '2019-01-15 10:52:00', 'system', '2019-01-15 10:52:00', NULL, '0', 'JeeSite', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `js_sys_dict_data` VALUES ('1085006657953665024', '0', '0,', 50, '0000000050,', '1', 0, '基层', '基层', '3', 'sys_role_type', '1', '', '', '', '0', 'system', '2019-01-15 10:52:00', 'system', '2019-01-15 10:52:00', NULL, '0', 'JeeSite', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `js_sys_dict_data` VALUES ('1085006658117242880', '0', '0,', 60, '0000000060,', '1', 0, '其它', '其它', '4', 'sys_role_type', '1', '', '', '', '0', 'system', '2019-01-15 10:52:00', 'system', '2019-01-15 10:52:00', NULL, '0', 'JeeSite', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `js_sys_dict_data` VALUES ('1085006658285015040', '0', '0,', 30, '0000000030,', '1', 0, '未设置', '未设置', '0', 'sys_role_data_scope', '1', '', '', '', '0', 'system', '2019-01-15 10:52:00', 'system', '2019-01-15 10:52:00', NULL, '0', 'JeeSite', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `js_sys_dict_data` VALUES ('1085006658452787200', '0', '0,', 40, '0000000040,', '1', 0, '全部数据', '全部数据', '1', 'sys_role_data_scope', '1', '', '', '', '0', 'system', '2019-01-15 10:52:00', 'system', '2019-01-15 10:52:01', NULL, '0', 'JeeSite', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `js_sys_dict_data` VALUES ('1085006658591199232', '0', '0,', 50, '0000000050,', '1', 0, '自定义数据', '自定义数据', '2', 'sys_role_data_scope', '1', '', '', '', '0', 'system', '2019-01-15 10:52:01', 'system', '2019-01-15 10:52:01', NULL, '0', 'JeeSite', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `js_sys_dict_data` VALUES ('1085006658733805568', '0', '0,', 60, '0000000060,', '1', 0, '本部门数据', '本部门数据', '3', 'sys_role_data_scope', '1', '', '', '', '0', 'system', '2019-01-15 10:52:01', 'system', '2019-01-15 10:52:01', NULL, '0', 'JeeSite', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `js_sys_dict_data` VALUES ('1085006658872217600', '0', '0,', 70, '0000000070,', '1', 0, '本公司数据', '本公司数据', '4', 'sys_role_data_scope', '1', '', '', '', '0', 'system', '2019-01-15 10:52:01', 'system', '2019-01-15 10:52:01', NULL, '0', 'JeeSite', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `js_sys_dict_data` VALUES ('1085006659094515712', '0', '0,', 80, '0000000080,', '1', 0, '本部门和本公司数据', '本部门和本公司数据', '5', 'sys_role_data_scope', '1', '', '', '', '0', 'system', '2019-01-15 10:52:01', 'system', '2019-01-15 10:52:01', NULL, '0', 'JeeSite', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `js_sys_dict_data` VALUES ('1085006659417477120', '0', '0,', 30, '0000000030,', '1', 0, '高管', '高管', '1', 'sys_post_type', '1', '', '', '', '0', 'system', '2019-01-15 10:52:01', 'system', '2019-01-15 10:52:01', NULL, '0', 'JeeSite', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `js_sys_dict_data` VALUES ('1085006659698495488', '0', '0,', 40, '0000000040,', '1', 0, '中层', '中层', '2', 'sys_post_type', '1', '', '', '', '0', 'system', '2019-01-15 10:52:01', 'system', '2019-01-15 10:52:01', NULL, '0', 'JeeSite', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `js_sys_dict_data` VALUES ('1085006659870461952', '0', '0,', 50, '0000000050,', '1', 0, '基层', '基层', '3', 'sys_post_type', '1', '', '', '', '0', 'system', '2019-01-15 10:52:01', 'system', '2019-01-15 10:52:01', NULL, '0', 'JeeSite', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `js_sys_dict_data` VALUES ('1085006660025651200', '0', '0,', 60, '0000000060,', '1', 0, '其它', '其它', '4', 'sys_post_type', '1', '', '', '', '0', 'system', '2019-01-15 10:52:01', 'system', '2019-01-15 10:52:01', NULL, '0', 'JeeSite', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `js_sys_dict_data` VALUES ('1085006660193423360', '0', '0,', 30, '0000000030,', '1', 0, '接入日志', '接入日志', 'access', 'sys_log_type', '1', '', '', '', '0', 'system', '2019-01-15 10:52:01', 'system', '2019-01-15 10:52:01', NULL, '0', 'JeeSite', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `js_sys_dict_data` VALUES ('1085006660344418304', '0', '0,', 40, '0000000040,', '1', 0, '修改日志', '修改日志', 'update', 'sys_log_type', '1', '', '', '', '0', 'system', '2019-01-15 10:52:01', 'system', '2019-01-15 10:52:01', NULL, '0', 'JeeSite', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `js_sys_dict_data` VALUES ('1085006660478636032', '0', '0,', 50, '0000000050,', '1', 0, '查询日志', '查询日志', 'select', 'sys_log_type', '1', '', '', '', '0', 'system', '2019-01-15 10:52:01', 'system', '2019-01-15 10:52:01', NULL, '0', 'JeeSite', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `js_sys_dict_data` VALUES ('1085006660612853760', '0', '0,', 60, '0000000060,', '1', 0, '登录登出', '登录登出', 'loginLogout', 'sys_log_type', '1', '', '', '', '0', 'system', '2019-01-15 10:52:01', 'system', '2019-01-15 10:52:01', NULL, '0', 'JeeSite', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `js_sys_dict_data` VALUES ('1085006660776431616', '0', '0,', 30, '0000000030,', '1', 0, '默认', '默认', 'DEFAULT', 'sys_job_group', '1', '', '', '', '0', 'system', '2019-01-15 10:52:01', 'system', '2019-01-15 10:52:01', NULL, '0', 'JeeSite', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `js_sys_dict_data` VALUES ('1085006660885483520', '0', '0,', 40, '0000000040,', '1', 0, '系统', '系统', 'SYSTEM', 'sys_job_group', '1', '', '', '', '0', 'system', '2019-01-15 10:52:01', 'system', '2019-01-15 10:52:01', NULL, '0', 'JeeSite', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `js_sys_dict_data` VALUES ('1085006661019701248', '0', '0,', 30, '0000000030,', '1', 0, '错过计划等待本次计划完成后立即执行一次', '错过计划等待本次计划完成后立即执行一次', '1', 'sys_job_misfire_instruction', '1', '', '', '', '0', 'system', '2019-01-15 10:52:01', 'system', '2019-01-15 10:52:01', NULL, '0', 'JeeSite', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `js_sys_dict_data` VALUES ('1085006661128753152', '0', '0,', 40, '0000000040,', '1', 0, '本次执行时间根据上次结束时间重新计算（时间间隔方式）', '本次执行时间根据上次结束时间重新计算（时间间隔方式）', '2', 'sys_job_misfire_instruction', '1', '', '', '', '0', 'system', '2019-01-15 10:52:01', 'system', '2019-01-15 10:52:01', NULL, '0', 'JeeSite', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `js_sys_dict_data` VALUES ('1085006661267165184', '0', '0,', 30, '0000000030,', '1', 0, '正常', '正常', '0', 'sys_job_status', '1', '', '', '', '0', 'system', '2019-01-15 10:52:01', 'system', '2019-01-15 10:52:01', NULL, '0', 'JeeSite', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `js_sys_dict_data` VALUES ('1085006661384605696', '0', '0,', 40, '0000000040,', '1', 0, '删除', '删除', '1', 'sys_job_status', '1', '', '', '', '0', 'system', '2019-01-15 10:52:01', 'system', '2019-01-15 10:52:01', NULL, '0', 'JeeSite', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `js_sys_dict_data` VALUES ('1085006661510434816', '0', '0,', 50, '0000000050,', '1', 0, '暂停', '暂停', '2', 'sys_job_status', '1', '', 'color:#f00;', '', '0', 'system', '2019-01-15 10:52:01', 'system', '2019-01-15 10:52:01', NULL, '0', 'JeeSite', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `js_sys_dict_data` VALUES ('1085006661665624064', '0', '0,', 30, '0000000030,', '1', 0, '完成', '完成', '3', 'sys_job_status', '1', '', '', '', '0', 'system', '2019-01-15 10:52:01', 'system', '2019-01-15 10:52:01', NULL, '0', 'JeeSite', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `js_sys_dict_data` VALUES ('1085006661904699392', '0', '0,', 40, '0000000040,', '1', 0, '错误', '错误', '4', 'sys_job_status', '1', '', 'color:#f00;', '', '0', 'system', '2019-01-15 10:52:01', 'system', '2019-01-15 10:52:01', NULL, '0', 'JeeSite', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `js_sys_dict_data` VALUES ('1085006662085054464', '0', '0,', 50, '0000000050,', '1', 0, '运行', '运行', '5', 'sys_job_status', '1', '', '', '', '0', 'system', '2019-01-15 10:52:01', 'system', '2019-01-15 10:52:01', NULL, '0', 'JeeSite', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `js_sys_dict_data` VALUES ('1085006662261215232', '0', '0,', 30, '0000000030,', '1', 0, '计划日志', '计划日志', 'scheduler', 'sys_job_type', '1', '', '', '', '0', 'system', '2019-01-15 10:52:01', 'system', '2019-01-15 10:52:01', NULL, '0', 'JeeSite', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `js_sys_dict_data` VALUES ('1085006662449958912', '0', '0,', 40, '0000000040,', '1', 0, '任务日志', '任务日志', 'job', 'sys_job_type', '1', '', '', '', '0', 'system', '2019-01-15 10:52:01', 'system', '2019-01-15 10:52:01', NULL, '0', 'JeeSite', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `js_sys_dict_data` VALUES ('1085006662634508288', '0', '0,', 50, '0000000050,', '1', 0, '触发日志', '触发日志', 'trigger', 'sys_job_type', '1', '', '', '', '0', 'system', '2019-01-15 10:52:01', 'system', '2019-01-15 10:52:02', NULL, '0', 'JeeSite', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `js_sys_dict_data` VALUES ('1085006662777114624', '0', '0,', 30, '0000000030,', '1', 0, '计划创建', '计划创建', 'jobScheduled', 'sys_job_event', '1', '', '', '', '0', 'system', '2019-01-15 10:52:02', 'system', '2019-01-15 10:52:02', NULL, '0', 'JeeSite', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `js_sys_dict_data` VALUES ('1085006662902943744', '0', '0,', 40, '0000000040,', '1', 0, '计划移除', '计划移除', 'jobUnscheduled', 'sys_job_event', '1', '', '', '', '0', 'system', '2019-01-15 10:52:02', 'system', '2019-01-15 10:52:02', NULL, '0', 'JeeSite', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `js_sys_dict_data` VALUES ('1085006663020384256', '0', '0,', 50, '0000000050,', '1', 0, '计划暂停', '计划暂停', 'triggerPaused', 'sys_job_event', '1', '', '', '', '0', 'system', '2019-01-15 10:52:02', 'system', '2019-01-15 10:52:02', NULL, '0', 'JeeSite', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `js_sys_dict_data` VALUES ('1085006663150407680', '0', '0,', 60, '0000000060,', '1', 0, '计划恢复', '计划恢复', 'triggerResumed', 'sys_job_event', '1', '', '', '', '0', 'system', '2019-01-15 10:52:02', 'system', '2019-01-15 10:52:02', NULL, '0', 'JeeSite', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `js_sys_dict_data` VALUES ('1085006663288819712', '0', '0,', 70, '0000000070,', '1', 0, '调度错误', '调度错误', 'schedulerError', 'sys_job_event', '1', '', '', '', '0', 'system', '2019-01-15 10:52:02', 'system', '2019-01-15 10:52:02', NULL, '0', 'JeeSite', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `js_sys_dict_data` VALUES ('1085006663456591872', '0', '0,', 80, '0000000080,', '1', 0, '任务执行', '任务执行', 'jobToBeExecuted', 'sys_job_event', '1', '', '', '', '0', 'system', '2019-01-15 10:52:02', 'system', '2019-01-15 10:52:02', NULL, '0', 'JeeSite', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `js_sys_dict_data` VALUES ('1085006663582420992', '0', '0,', 90, '0000000090,', '1', 0, '任务结束', '任务结束', 'jobWasExecuted', 'sys_job_event', '1', '', '', '', '0', 'system', '2019-01-15 10:52:02', 'system', '2019-01-15 10:52:02', NULL, '0', 'JeeSite', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `js_sys_dict_data` VALUES ('1085006663716638720', '0', '0,', 100, '0000000100,', '1', 0, '任务停止', '任务停止', 'jobExecutionVetoed', 'sys_job_event', '1', '', '', '', '0', 'system', '2019-01-15 10:52:02', 'system', '2019-01-15 10:52:02', NULL, '0', 'JeeSite', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `js_sys_dict_data` VALUES ('1085006663959908352', '0', '0,', 110, '0000000110,', '1', 0, '触发计划', '触发计划', 'triggerFired', 'sys_job_event', '1', '', '', '', '0', 'system', '2019-01-15 10:52:02', 'system', '2019-01-15 10:52:02', NULL, '0', 'JeeSite', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `js_sys_dict_data` VALUES ('1085006664127680512', '0', '0,', 120, '0000000120,', '1', 0, '触发验证', '触发验证', 'vetoJobExecution', 'sys_job_event', '1', '', '', '', '0', 'system', '2019-01-15 10:52:02', 'system', '2019-01-15 10:52:02', NULL, '0', 'JeeSite', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `js_sys_dict_data` VALUES ('1085006664295452672', '0', '0,', 130, '0000000130,', '1', 0, '触发完成', '触发完成', 'triggerComplete', 'sys_job_event', '1', '', '', '', '0', 'system', '2019-01-15 10:52:02', 'system', '2019-01-15 10:52:02', NULL, '0', 'JeeSite', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `js_sys_dict_data` VALUES ('1085006664438059008', '0', '0,', 140, '0000000140,', '1', 0, '触发错过', '触发错过', 'triggerMisfired', 'sys_job_event', '1', '', '', '', '0', 'system', '2019-01-15 10:52:02', 'system', '2019-01-15 10:52:02', NULL, '0', 'JeeSite', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `js_sys_dict_data` VALUES ('1085006664610025472', '0', '0,', 30, '0000000030,', '1', 0, 'PC', 'PC', 'pc', 'sys_msg_type', '1', '消息类型', '', '', '0', 'system', '2019-01-15 10:52:02', 'system', '2019-01-15 10:52:02', NULL, '0', 'JeeSite', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `js_sys_dict_data` VALUES ('1085006664744243200', '0', '0,', 40, '0000000040,', '1', 0, 'APP', 'APP', 'app', 'sys_msg_type', '1', '', '', '', '0', 'system', '2019-01-15 10:52:02', 'system', '2019-01-15 10:52:02', NULL, '0', 'JeeSite', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `js_sys_dict_data` VALUES ('1085006664907821056', '0', '0,', 50, '0000000050,', '1', 0, '短信', '短信', 'sms', 'sys_msg_type', '1', '', '', '', '0', 'system', '2019-01-15 10:52:02', 'system', '2019-01-15 10:52:02', NULL, '0', 'JeeSite', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `js_sys_dict_data` VALUES ('1085006665125924864', '0', '0,', 60, '0000000060,', '1', 0, '邮件', '邮件', 'email', 'sys_msg_type', '1', '', '', '', '0', 'system', '2019-01-15 10:52:02', 'system', '2019-01-15 10:52:02', NULL, '0', 'JeeSite', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `js_sys_dict_data` VALUES ('1085006665276919808', '0', '0,', 70, '0000000070,', '1', 0, '微信', '微信', 'weixin', 'sys_msg_type', '1', '', '', '', '0', 'system', '2019-01-15 10:52:02', 'system', '2019-01-15 10:52:02', NULL, '0', 'JeeSite', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `js_sys_dict_data` VALUES ('1085006665411137536', '0', '0,', 30, '0000000030,', '1', 0, '待推送', '待推送', '0', 'sys_msg_push_status', '1', '推送状态', '', '', '0', 'system', '2019-01-15 10:52:02', 'system', '2019-01-15 10:52:02', NULL, '0', 'JeeSite', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `js_sys_dict_data` VALUES ('1085006665545355264', '0', '0,', 30, '0000000030,', '1', 0, '成功', '成功', '1', 'sys_msg_push_status', '1', '', '', '', '0', 'system', '2019-01-15 10:52:02', 'system', '2019-01-15 10:52:02', NULL, '0', 'JeeSite', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `js_sys_dict_data` VALUES ('1085006665679572992', '0', '0,', 40, '0000000040,', '1', 0, '失败', '失败', '2', 'sys_msg_push_status', '1', '', 'color:#f00;', '', '0', 'system', '2019-01-15 10:52:02', 'system', '2019-01-15 10:52:02', NULL, '0', 'JeeSite', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `js_sys_dict_data` VALUES ('1085006665838956544', '0', '0,', 30, '0000000030,', '1', 0, '未送达', '未送达', '0', 'sys_msg_read_status', '1', '读取状态', '', '', '0', 'system', '2019-01-15 10:52:02', 'system', '2019-01-15 10:52:02', NULL, '0', 'JeeSite', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `js_sys_dict_data` VALUES ('1085006665956397056', '0', '0,', 40, '0000000040,', '1', 0, '已读', '已读', '1', 'sys_msg_read_status', '1', '', '', '', '0', 'system', '2019-01-15 10:52:02', 'system', '2019-01-15 10:52:02', NULL, '0', 'JeeSite', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `js_sys_dict_data` VALUES ('1085006666073837568', '0', '0,', 50, '0000000050,', '1', 0, '未读', '未读', '2', 'sys_msg_read_status', '1', '', '', '', '0', 'system', '2019-01-15 10:52:02', 'system', '2019-01-15 10:52:02', NULL, '0', 'JeeSite', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `js_sys_dict_data` VALUES ('1085006666195472384', '0', '0,', 30, '0000000030,', '1', 0, '普通', '普通', '1', 'msg_inner_content_level', '1', '内容级别', '', '', '0', 'system', '2019-01-15 10:52:02', 'system', '2019-01-15 10:52:02', NULL, '0', 'JeeSite', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `js_sys_dict_data` VALUES ('1085006666317107200', '0', '0,', 40, '0000000040,', '1', 0, '一般', '一般', '2', 'msg_inner_content_level', '1', '', '', '', '0', 'system', '2019-01-15 10:52:02', 'system', '2019-01-15 10:52:02', NULL, '0', 'JeeSite', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `js_sys_dict_data` VALUES ('1085006666442936320', '0', '0,', 50, '0000000050,', '1', 0, '紧急', '紧急', '3', 'msg_inner_content_level', '1', '', 'color:#f00;', '', '0', 'system', '2019-01-15 10:52:02', 'system', '2019-01-15 10:52:02', NULL, '0', 'JeeSite', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `js_sys_dict_data` VALUES ('1085006666572959744', '0', '0,', 30, '0000000030,', '1', 0, '公告', '公告', '1', 'msg_inner_content_type', '1', '内容类型', '', '', '0', 'system', '2019-01-15 10:52:02', 'system', '2019-01-15 10:52:02', NULL, '0', 'JeeSite', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `js_sys_dict_data` VALUES ('1085006666702983168', '0', '0,', 40, '0000000040,', '1', 0, '新闻', '新闻', '2', 'msg_inner_content_type', '1', '', '', '', '0', 'system', '2019-01-15 10:52:02', 'system', '2019-01-15 10:52:02', NULL, '0', 'JeeSite', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `js_sys_dict_data` VALUES ('1085006666824617984', '0', '0,', 50, '0000000050,', '1', 0, '会议', '会议', '3', 'msg_inner_content_type', '1', '', '', '', '0', 'system', '2019-01-15 10:52:02', 'system', '2019-01-15 10:52:03', NULL, '0', 'JeeSite', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `js_sys_dict_data` VALUES ('1085006666954641408', '0', '0,', 60, '0000000060,', '1', 0, '其它', '其它', '4', 'msg_inner_content_type', '1', '', '', '', '0', 'system', '2019-01-15 10:52:03', 'system', '2019-01-15 10:52:03', NULL, '0', 'JeeSite', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `js_sys_dict_data` VALUES ('1085006667088859136', '0', '0,', 30, '0000000030,', '1', 0, '用户', '用户', '1', 'msg_inner_receiver_type', '1', '接受类型', '', '', '0', 'system', '2019-01-15 10:52:03', 'system', '2019-01-15 10:52:03', NULL, '0', 'JeeSite', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `js_sys_dict_data` VALUES ('1085006667214688256', '0', '0,', 40, '0000000040,', '1', 0, '部门', '部门', '2', 'msg_inner_receiver_type', '1', '', '', '', '0', 'system', '2019-01-15 10:52:03', 'system', '2019-01-15 10:52:03', NULL, '0', 'JeeSite', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `js_sys_dict_data` VALUES ('1085006667386654720', '0', '0,', 50, '0000000050,', '1', 0, '角色', '角色', '3', 'msg_inner_receiver_type', '1', '', '', '', '0', 'system', '2019-01-15 10:52:03', 'system', '2019-01-15 10:52:03', NULL, '0', 'JeeSite', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `js_sys_dict_data` VALUES ('1085006667508289536', '0', '0,', 60, '0000000060,', '1', 0, '岗位', '岗位', '4', 'msg_inner_receiver_type', '1', '', '', '', '0', 'system', '2019-01-15 10:52:03', 'system', '2019-01-15 10:52:03', NULL, '0', 'JeeSite', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `js_sys_dict_data` VALUES ('1085006667625730048', '0', '0,', 30, '0000000030,', '1', 0, '正常', '正常', '0', 'msg_inner_msg_status', '1', '消息状态', '', '', '0', 'system', '2019-01-15 10:52:03', 'system', '2019-01-15 10:52:03', NULL, '0', 'JeeSite', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `js_sys_dict_data` VALUES ('1085006667759947776', '0', '0,', 40, '0000000040,', '1', 0, '删除', '删除', '1', 'msg_inner_msg_status', '1', '', '', '', '0', 'system', '2019-01-15 10:52:03', 'system', '2019-01-15 10:52:03', NULL, '0', 'JeeSite', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `js_sys_dict_data` VALUES ('1085006667885776896', '0', '0,', 50, '0000000050,', '1', 0, '审核', '审核', '4', 'msg_inner_msg_status', '1', '', '', '', '0', 'system', '2019-01-15 10:52:03', 'system', '2019-01-15 10:52:03', NULL, '0', 'JeeSite', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `js_sys_dict_data` VALUES ('1085006668007411712', '0', '0,', 60, '0000000060,', '1', 0, '驳回', '驳回', '5', 'msg_inner_msg_status', '1', '', 'color:#f00;', '', '0', 'system', '2019-01-15 10:52:03', 'system', '2019-01-15 10:52:03', NULL, '0', 'JeeSite', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `js_sys_dict_data` VALUES ('1085006668129046528', '0', '0,', 70, '0000000070,', '1', 0, '草稿', '草稿', '9', 'msg_inner_msg_status', '1', '', '', '', '0', 'system', '2019-01-15 10:52:03', 'system', '2019-01-15 10:52:03', NULL, '0', 'JeeSite', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `js_sys_dict_data` VALUES ('1086071167117340672', '0', '0,', 30, '0000000030,', '1', 0, '无限制', '无限制', '0', 'osee_shop_reward_refresh_type', '0', '', '', '', '0', 'system', '2019-01-18 09:21:59', 'system', '2019-01-18 09:21:59', '', '0', 'JeeSite', '', '', '', '', '', '', '', '', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `js_sys_dict_data` VALUES ('1086071229914460160', '0', '0,', 60, '0000000060,', '1', 0, '每日一次', '每日一次', '1', 'osee_shop_reward_refresh_type', '0', '', '', '', '0', 'system', '2019-01-18 09:22:14', 'system', '2019-01-18 09:22:14', '', '0', 'JeeSite', '', '', '', '', '', '', '', '', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `js_sys_dict_data` VALUES ('1086071284138422272', '0', '0,', 90, '0000000090,', '1', 0, '每周一次', '每周一次', '2', 'osee_shop_reward_refresh_type', '0', '', '', '', '0', 'system', '2019-01-18 09:22:27', 'system', '2019-01-18 09:22:27', '', '0', 'JeeSite', '', '', '', '', '', '', '', '', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `js_sys_dict_data` VALUES ('1086071323896229888', '0', '0,', 120, '0000000120,', '1', 0, '每月一次', '每月一次', '3', 'osee_shop_reward_refresh_type', '0', '', '', '', '0', 'system', '2019-01-18 09:22:37', 'system', '2019-01-18 09:22:37', '', '0', 'JeeSite', '', '', '', '', '', '', '', '', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `js_sys_dict_data` VALUES ('1086074276786909184', '0', '0,', 30, '0000000030,', '1', 0, '已使用', '已使用', '1', 'osee_game_cdk_use_status', '0', '', '', '', '0', 'system', '2019-01-18 09:34:21', 'system', '2019-01-18 09:34:21', '', '0', 'JeeSite', '', '', '', '', '', '', '', '', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `js_sys_dict_data` VALUES ('1086074316498579456', '0', '0,', 60, '0000000060,', '1', 0, '未使用', '未使用', '2', 'osee_game_cdk_use_status', '0', '', '', '', '0', 'system', '2019-01-18 09:34:30', 'system', '2019-01-18 09:34:30', '', '0', 'JeeSite', '', '', '', '', '', '', '', '', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `js_sys_dict_data` VALUES ('1086152884588953600', '0', '0,', 30, '0000000030,', '1', 0, '实物', '实物', '1', 'osee_shop_reward_type', '0', '', '', '', '0', 'system', '2019-01-18 14:46:42', 'system', '2019-01-18 14:46:42', '', '0', 'JeeSite', '', '', '', '', '', '', '', '', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `js_sys_dict_data` VALUES ('1086152906734878720', '0', '0,', 60, '0000000060,', '1', 0, '钻石', '钻石', '2', 'osee_shop_reward_type', '0', '', '', '', '0', 'system', '2019-01-18 14:46:47', 'system', '2019-01-18 14:46:47', '', '0', 'JeeSite', '', '', '', '', '', '', '', '', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `js_sys_dict_data` VALUES ('1086152927911919616', '0', '0,', 90, '0000000090,', '1', 0, '金币', '金币', '3', 'osee_shop_reward_type', '0', '', '', '', '0', 'system', '2019-01-18 14:46:52', 'system', '2019-01-18 14:46:52', '', '0', 'JeeSite', '', '', '', '', '', '', '', '', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `js_sys_dict_data` VALUES ('1086194759327154176', '0', '0,', 30, '0000000030,', '1', 0, '待发货', '待发货', '1', 'osee_shop_exchange_order_status', '0', '', '', '', '0', 'system', '2019-01-18 17:33:06', 'system', '2019-01-18 17:53:48', '', '0', 'JeeSite', '', '', '', '', '', '', '', '', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `js_sys_dict_data` VALUES ('1086194782999805952', '0', '0,', 60, '0000000060,', '1', 0, '已发货', '已发货', '2', 'osee_shop_exchange_order_status', '0', '', '', '', '0', 'system', '2019-01-18 17:33:11', 'system', '2019-01-18 17:33:11', '', '0', 'JeeSite', '', '', '', '', '', '', '', '', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `js_sys_dict_data` VALUES ('1086194822904414208', '0', '0,', 90, '0000000090,', '1', 0, '已拒绝', '已拒绝', '3', 'osee_shop_exchange_order_status', '0', '', '', '', '0', 'system', '2019-01-18 17:33:21', 'system', '2019-01-18 17:33:21', '', '0', 'JeeSite', '', '', '', '', '', '', '', '', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `js_sys_dict_data` VALUES ('1087260639874887680', '0', '0,', 30, '0000000030,', '1', 0, '正常', '正常', '1', 'osee_player_account_status', '0', '', '', '', '0', 'system', '2019-01-21 16:08:32', 'system', '2019-01-21 16:08:32', '', '0', 'JeeSite', '', '', '', '', '', '', '', '', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `js_sys_dict_data` VALUES ('1087260679695609856', '0', '0,', 60, '0000000060,', '1', 0, '冻结', '冻结', '2', 'osee_player_account_status', '0', '', '', '', '0', 'system', '2019-01-21 16:08:41', 'system', '2019-01-21 16:08:41', '', '0', 'JeeSite', '', '', '', '', '', '', '', '', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `js_sys_dict_data` VALUES ('1087260994750754816', '0', '0,', 30, '0000000030,', '1', 0, '离线', '离线', '0', 'osee_player_game_status', '0', '', '', '', '0', 'system', '2019-01-21 16:09:56', 'system', '2022-09-27 10:31:10', '', '0', 'JeeSite', '', '', '', '', '', '', '', '', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `js_sys_dict_data` VALUES ('1087261082185216000', '0', '0,', 60, '0000000060,', '1', 0, '在线', '在线', '100', 'osee_player_game_status', '0', '', '', '', '0', 'system', '2019-01-21 16:10:17', 'system', '2019-01-25 13:24:26', '', '0', 'JeeSite', '', '', '', '', '', '', '', '', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `js_sys_dict_data` VALUES ('1087261149348605952', '0', '0,', 90, '0000000090,', '1', 0, '游戏大厅', '游戏大厅', '1', 'osee_player_game_status', '0', '', '', '', '0', 'system', '2019-01-21 16:10:33', 'system', '2022-09-28 17:40:02', '', '0', 'JeeSite', '', '', '', '', '', '', '', '', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `js_sys_dict_data` VALUES ('1087261195104268288', '0', '0,', 120, '0000000120,', '1', 0, '捕鱼', '捕鱼', '3', 'osee_player_game_status', '0', '', '', '', '2', 'system', '2019-01-21 16:10:44', 'system', '2019-12-31 16:43:21', '', '0', 'JeeSite', '', '', '', '', '', '', '', '', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `js_sys_dict_data` VALUES ('1087261239446450176', '0', '0,', 150, '0000000150,', '1', 0, '拼十', '拼十', '4', 'osee_player_game_status', '0', '', '', '', '2', 'system', '2019-01-21 16:10:54', 'system', '2022-09-27 09:48:09', '', '0', 'JeeSite', '', '', '', '', '', '', '', '', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `js_sys_dict_data` VALUES ('1087261297466257408', '0', '0,', 180, '0000000180,', '1', 0, '二八杠', '二八杠', '5', 'osee_player_game_status', '0', '', '', '', '2', 'system', '2019-01-21 16:11:08', 'system', '2019-12-31 16:43:12', '', '0', 'JeeSite', '', '', '', '', '', '', '', '', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `js_sys_dict_data` VALUES ('1087261327472308224', '0', '0,', 210, '0000000210,', '1', 0, '水果拉霸', '水果拉霸', '6', 'osee_player_game_status', '0', '', '', '', '0', 'system', '2019-01-21 16:11:15', 'system', '2019-01-25 13:24:53', '', '0', 'JeeSite', '', '', '', '', '', '', '', '', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `js_sys_dict_data` VALUES ('1087261363962753024', '0', '0,', 240, '0000000240,', '1', 0, '五子棋', '五子棋', '7', 'osee_player_game_status', '0', '', '', '', '2', 'system', '2019-01-21 16:11:24', 'system', '2019-12-31 16:43:16', '', '0', 'JeeSite', '', '', '', '', '', '', '', '', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `js_sys_dict_data` VALUES ('1087261660839784448', '0', '0,', 30, '0000000030,', '1', 0, '微信', '微信', '1', 'osee_player_login_type', '0', '', '', '', '0', 'system', '2019-01-21 16:12:35', 'system', '2019-01-21 16:12:35', '', '0', 'JeeSite', '', '', '', '', '', '', '', '', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `js_sys_dict_data` VALUES ('1087261682612416512', '0', '0,', 60, '0000000060,', '1', 0, '账号', '账号', '2', 'osee_player_login_type', '0', '', '', '', '0', 'system', '2019-01-21 16:12:40', 'system', '2019-01-21 16:12:40', '', '0', 'JeeSite', '', '', '', '', '', '', '', '', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `js_sys_dict_data` VALUES ('1087262026771836928', '0', '0,', 30, '0000000030,', '1', 0, '正常', '正常', '1', 'osee_player_lose_ctrl', '0', '', '', '', '0', 'system', '2019-01-21 16:14:02', 'system', '2019-01-21 16:14:02', '', '0', 'JeeSite', '', '', '', '', '', '', '', '', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `js_sys_dict_data` VALUES ('1087262055284715520', '0', '0,', 60, '0000000060,', '1', 0, '必输', '必输', '2', 'osee_player_lose_ctrl', '0', '', '', '', '0', 'system', '2019-01-21 16:14:09', 'system', '2019-01-21 16:14:09', '', '0', 'JeeSite', '', '', '', '', '', '', '', '', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `js_sys_dict_data` VALUES ('1087262242472308736', '0', '0,', 30, '0000000030,', '1', 0, '玩家', '玩家', '1', 'osee_player_type', '0', '', '', '', '0', 'system', '2019-01-21 16:14:54', 'system', '2019-01-21 16:14:54', '', '0', 'JeeSite', '', '', '', '', '', '', '', '', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `js_sys_dict_data` VALUES ('1087262265536786432', '0', '0,', 60, '0000000060,', '1', 0, '线下代理', '线下代理', '2', 'osee_player_type', '0', '', '', '', '0', 'system', '2019-01-21 16:14:59', 'system', '2020-01-06 15:23:34', '', '0', 'JeeSite', '', '', '', '', '', '', '', '', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `js_sys_dict_data` VALUES ('1087983327111766016', '0', '0,', 30, '0000000030,', '1', 0, '金币', '金币', '1', 'osee_item_type', '0', '', '', '', '2', 'system', '2019-01-23 16:00:14', 'system', '2023-06-12 11:20:12', '', '0', 'JeeSite', '', '', '', '', '', '', '', '', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `js_sys_dict_data` VALUES ('1087983415674494976', '0', '0,', 60, '0000000060,', '1', 0, '奖券', '奖券', '3', 'osee_item_type', '0', '', '', '', '0', 'system', '2019-01-23 16:00:35', 'system', '2019-01-23 16:00:35', '', '0', 'JeeSite', '', '', '', '', '', '', '', '', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `js_sys_dict_data` VALUES ('1087983451288330240', '0', '0,', 90, '0000000090,', '1', 0, '钻石', '钻石', '4', 'osee_item_type', '0', '', '', '', '0', 'system', '2019-01-23 16:00:43', 'system', '2019-01-23 16:00:43', '', '0', 'JeeSite', '', '', '', '', '', '', '', '', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `js_sys_dict_data` VALUES ('1087984631083130880', '0', '0,', 30, '0000000030,', '1', 0, '成功', '成功', '1', 'osee_success_fail', '0', '', '', '', '0', 'system', '2019-01-23 16:05:24', 'system', '2019-01-23 16:05:24', '', '0', 'JeeSite', '', '', '', '', '', '', '', '', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `js_sys_dict_data` VALUES ('1087984651257733120', '0', '0,', 60, '0000000060,', '1', 0, '失败', '失败', '2', 'osee_success_fail', '0', '', '', '', '0', 'system', '2019-01-23 16:05:29', 'system', '2019-01-23 16:05:29', '', '0', 'JeeSite', '', '', '', '', '', '', '', '', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `js_sys_dict_data` VALUES ('1087985509814984704', '0', '0,', 30, '0000000030,', '1', 0, '微信', '微信', '1', 'osee_recharge_type', '0', '', '', '', '0', 'system', '2019-01-23 16:08:54', 'system', '2019-01-23 16:08:54', '', '0', 'JeeSite', '', '', '', '', '', '', '', '', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `js_sys_dict_data` VALUES ('1087985530182524928', '0', '0,', 60, '0000000060,', '1', 0, '支付宝', '支付宝', '2', 'osee_recharge_type', '0', '', '', '', '0', 'system', '2019-01-23 16:08:59', 'system', '2019-01-23 16:08:59', '', '0', 'JeeSite', '', '', '', '', '', '', '', '', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `js_sys_dict_data` VALUES ('1087985556749246464', '0', '0,', 90, '0000000090,', '1', 0, '后台充值', '后台充值', '3', 'osee_recharge_type', '0', '', '', '', '0', 'system', '2019-01-23 16:09:05', 'system', '2019-01-23 16:09:12', '', '0', 'JeeSite', '', '', '', '', '', '', '', '', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `js_sys_dict_data` VALUES ('1087989516000256000', '0', '0,', 30, '0000000030,', '1', 0, '充值', '充值', '1', 'osee_player_tenure_operation_type', '0', '', '', '', '0', 'system', '2019-01-23 16:24:49', 'system', '2019-01-23 16:24:49', '', '0', 'JeeSite', '', '', '', '', '', '', '', '', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `js_sys_dict_data` VALUES ('1087989539630964736', '0', '0,', 60, '0000000060,', '1', 0, '扣除', '扣除', '2', 'osee_player_tenure_operation_type', '0', '', '', '', '0', 'system', '2019-01-23 16:24:55', 'system', '2019-01-23 16:24:55', '', '0', 'JeeSite', '', '', '', '', '', '', '', '', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `js_sys_dict_data` VALUES ('1088309012838096896', '0', '0,', 50, '0000000050,', '1', 0, '五子棋', '五子棋', '6', 'osee_draw_game', '0', '', '', '', '0', 'system', '2019-01-24 13:34:23', 'system', '2019-05-26 14:13:29', '', '0', 'JeeSite', '', '', '', '', '', '', '', '', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `js_sys_dict_data` VALUES ('1088309045603999744', '0', '0,', 40, '0000000040,', '1', 0, '水果拉霸', '水果拉霸', '5', 'osee_draw_game', '0', '', '', '', '0', 'system', '2019-01-24 13:34:31', 'system', '2019-05-26 14:13:13', '', '0', 'JeeSite', '', '', '', '', '', '', '', '', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `js_sys_dict_data` VALUES ('1088309077531041792', '0', '0,', 30, '0000000030,', '1', 0, '二八杠', '二八杠', '4', 'osee_draw_game', '0', '', '', '', '0', 'system', '2019-01-24 13:34:39', 'system', '2019-05-26 14:12:57', '', '0', 'JeeSite', '', '', '', '', '', '', '', '', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `js_sys_dict_data` VALUES ('1088710150186512384', '0', '0,', 30, '0000000030,', '1', 0, '实名认证', '实名认证', '1', 'osee_pay_type', '0', '', '', '', '0', 'system', '2019-01-25 16:08:22', 'system', '2019-01-25 16:08:22', '', '0', 'JeeSite', '', '', '', '', '', '', '', '', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `js_sys_dict_data` VALUES ('1088710184147791872', '0', '0,', 60, '0000000060,', '1', 0, '任务奖励', '任务奖励', '2', 'osee_pay_type', '0', '', '', '', '0', 'system', '2019-01-25 16:08:30', 'system', '2019-01-25 16:08:30', '', '0', 'JeeSite', '', '', '', '', '', '', '', '', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `js_sys_dict_data` VALUES ('1088710221598732288', '0', '0,', 90, '0000000090,', '1', 0, '每日签到', '每日签到', '3', 'osee_pay_type', '0', '', '', '', '0', 'system', '2019-01-25 16:08:39', 'system', '2019-01-25 16:08:39', '', '0', 'JeeSite', '', '', '', '', '', '', '', '', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `js_sys_dict_data` VALUES ('1088710258642825216', '0', '0,', 120, '0000000120,', '1', 0, 'CDK兑换', 'CDK兑换', '4', 'osee_pay_type', '0', '', '', '', '0', 'system', '2019-01-25 16:08:48', 'system', '2019-01-25 16:08:48', '', '0', 'JeeSite', '', '', '', '', '', '', '', '', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `js_sys_dict_data` VALUES ('1089702541536100352', '0', '0,', 30, '0000000030,', '1', 0, '未支付', '未支付', '0', 'osee_pay_order_status', '0', '', '', '', '0', 'system', '2019-01-28 09:51:46', 'system', '2019-01-28 09:51:46', '', '0', 'JeeSite', '', '', '', '', '', '', '', '', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `js_sys_dict_data` VALUES ('1089702563682025472', '0', '0,', 60, '0000000060,', '1', 0, '成功', '成功', '1', 'osee_pay_order_status', '0', '', '', '', '0', 'system', '2019-01-28 09:51:52', 'system', '2019-01-28 09:51:52', '', '0', 'JeeSite', '', '', '', '', '', '', '', '', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `js_sys_dict_data` VALUES ('1089702593797128192', '0', '0,', 90, '0000000090,', '1', 0, '失败', '失败', '2', 'osee_pay_order_status', '0', '', '', '', '0', 'system', '2019-01-28 09:51:59', 'system', '2019-01-28 09:51:59', '', '0', 'JeeSite', '', '', '', '', '', '', '', '', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `js_sys_dict_data` VALUES ('1090118324383264768', '0', '0,', 20, '0000000020,', '1', 0, '捕鱼', '捕鱼', '2', 'osee_draw_game', '0', '', '', '', '0', 'system', '2019-01-29 13:23:57', 'system', '2019-05-26 14:12:03', '', '0', 'JeeSite', '', '', '', '', '', '', '', '', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `js_sys_dict_data` VALUES ('1106135527342673920', '0', '0,', 20, '0000000020,', '1', 0, '全部', '全部', '0', 'msg_inner_receiver_type', '1', '', '', '', '0', 'system', '2023-05-23 11:33:49', 'system', '2023-05-23 11:33:49', '', '0', 'JeeSite', '', '', '', '', '', '', '', '', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `js_sys_dict_data` VALUES ('1110820546292703232', '0', '0,', 30, '0000000030,', '1', 0, '一级代理', '一级代理', '1', 'ttmy_agent_level', '0', '一级代理', '', '', '1', 'system', '2019-03-27 16:27:11', 'system', '2026-01-12 18:28:02', '一级代理', '0', 'JeeSite', '', '', '', '', '', '', '', '', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `js_sys_dict_data` VALUES ('1110820590278369280', '0', '0,', 60, '0000000060,', '1', 0, '二级代理', '二级代理', '2', 'ttmy_agent_level', '0', '二级代理', '', '', '1', 'system', '2019-03-27 16:27:21', 'system', '2026-01-12 18:28:02', '二级代理', '0', 'JeeSite', '', '', '', '', '', '', '', '', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `js_sys_dict_data` VALUES ('1116585168380350464', '0', '0,', 120, '0000000120,', '1', 0, '青铜鱼雷', '青铜鱼雷', '5', 'osee_item_type', '0', '', '', '', '2', 'system', '2019-04-12 14:13:44', 'system', '2023-06-12 11:20:17', '', '0', 'JeeSite', '', '', '', '', '', '', '', '', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `js_sys_dict_data` VALUES ('1116585197841141760', '0', '0,', 150, '0000000150,', '1', 0, '白银鱼雷', '白银鱼雷', '6', 'osee_item_type', '0', '', '', '', '2', 'system', '2019-04-12 14:13:51', 'system', '2023-06-12 11:20:19', '', '0', 'JeeSite', '', '', '', '', '', '', '', '', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `js_sys_dict_data` VALUES ('1116585222663032832', '0', '0,', 180, '0000000180,', '1', 0, '黄金鱼雷', '黄金鱼雷', '7', 'osee_item_type', '0', '', '', '', '0', 'system', '2019-04-12 14:13:57', 'system', '2019-04-12 14:13:57', '', '0', 'JeeSite', '', '', '', '', '', '', '', '', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `js_sys_dict_data` VALUES ('1116585267047157760', '0', '0,', 210, '0000000210,', '1', 0, '锁定技能', '锁定技能', '8', 'osee_item_type', '0', '', '', '', '0', 'system', '2019-04-12 14:14:07', 'system', '2019-04-12 14:14:07', '', '0', 'JeeSite', '', '', '', '', '', '', '', '', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `js_sys_dict_data` VALUES ('1116585303587934208', '0', '0,', 240, '0000000240,', '1', 0, '冰冻技能', '冰冻技能', '9', 'osee_item_type', '0', '', '', '', '0', 'system', '2019-04-12 14:14:16', 'system', '2023-06-12 11:20:39', '', '0', 'JeeSite', '', '', '', '', '', '', '', '', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `js_sys_dict_data` VALUES ('1116585340627832832', '0', '0,', 270, '0000000270,', '1', 0, '急速技能', '急速技能', '10', 'osee_item_type', '0', '', '', '', '2', 'system', '2019-04-12 14:14:25', 'system', '2023-06-12 11:20:41', '', '0', 'JeeSite', '', '', '', '', '', '', '', '', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `js_sys_dict_data` VALUES ('1116585374329065472', '0', '0,', 300, '0000000300,', '1', 0, '暴击技能', '暴击技能', '11', 'osee_item_type', '0', '', '', '', '2', 'system', '2019-04-12 14:14:33', 'system', '2023-06-12 11:20:44', '', '0', 'JeeSite', '', '', '', '', '', '', '', '', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `js_sys_dict_data` VALUES ('1125274869668491264', '0', '0,', 330, '0000000330,', '1', 0, '月卡', '月卡', '12', 'osee_item_type', '0', '', '', '', '2', 'system', '2019-05-06 13:43:30', 'system', '2023-06-12 11:20:52', '', '0', 'JeeSite', '', '', '', '', '', '', '', '', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `js_sys_dict_data` VALUES ('1126375062364020736', '0', '0,', 60, '0000000060,', '1', 0, '日本語', '日本語', 'ja_JP', 'sys_lang_type', '1', '', '', '', '0', 'system', '2023-05-23 11:33:49', 'system', '2023-05-23 11:33:49', '', '0', 'JeeSite', '', '', '', '', '', '', '', '', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `js_sys_dict_data` VALUES ('1131856027641913344', '0', '0,', 90, '0000000090,', '1', 0, '必赢', '必赢', '3', 'osee_player_lose_ctrl', '0', '', '', '', '0', 'system', '2019-05-24 17:34:41', 'system', '2019-05-24 17:34:41', '', '0', 'JeeSite', '', '', '', '', '', '', '', '', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `js_sys_dict_data` VALUES ('1134345552028966912', '0', '0,', 360, '0000000360,', '1', 0, 'BOSS号角', 'BOSS号角', '13', 'osee_item_type', '0', '', '', '', '2', 'system', '2019-05-31 14:27:09', 'system', '2023-06-12 11:20:57', '', '0', 'JeeSite', '', '', '', '', '', '', '', '', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `js_sys_dict_data` VALUES ('1144555102101721088', '0', '0,', 80, '0000000080,', '1', 0, '龙晶战场', '龙晶战场', '7', 'osee_draw_game', '0', '', '', '', '0', 'system', '2019-06-28 18:36:16', 'system', '2019-06-28 18:36:16', '', '0', 'JeeSite', '', '', '', '', '', '', '', '', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `js_sys_dict_data` VALUES ('1144572988773306368', '0', '0,', 30, '0000000030,', '1', 0, '兑换龙晶', '兑换龙晶', '0', 'ttmy_dragon_crystal_exchange_type', '0', '', '', '', '0', 'system', '2019-06-28 19:47:20', 'system', '2019-06-28 19:47:20', '', '0', 'JeeSite', '', '', '', '', '', '', '', '', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `js_sys_dict_data` VALUES ('1144573012462735360', '0', '0,', 60, '0000000060,', '1', 0, '兑换鱼雷', '兑换鱼雷', '1', 'ttmy_dragon_crystal_exchange_type', '0', '', '', '', '0', 'system', '2019-06-28 19:47:26', 'system', '2019-06-28 19:47:26', '', '0', 'JeeSite', '', '', '', '', '', '', '', '', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `js_sys_dict_data` VALUES ('1144573231275380736', '0', '0,', 30, '0000000030,', '1', 0, '体验场', '体验场', '51', 'ttmy_fishing_room_index', '0', '', '', '', '0', 'system', '2019-06-28 19:48:18', 'system', '2022-09-20 19:08:06', '', '0', 'JeeSite', '', '', '', '', '', '', '', '', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `js_sys_dict_data` VALUES ('1144573254704762880', '0', '0,', 60, '0000000060,', '1', 0, '荧光女皇', '荧光女皇', '11', 'ttmy_fishing_room_index', '0', '', '', '', '0', 'system', '2019-06-28 19:48:24', 'system', '2022-09-20 19:08:17', '', '0', 'JeeSite', '', '', '', '', '', '', '', '', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `js_sys_dict_data` VALUES ('1144573277559525376', '0', '0,', 90, '0000000090,', '1', 0, '圣龙降世', '圣龙降世', '12', 'ttmy_fishing_room_index', '0', '', '', '', '0', 'system', '2019-06-28 19:48:29', 'system', '2022-09-20 19:08:32', '', '0', 'JeeSite', '', '', '', '', '', '', '', '', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `js_sys_dict_data` VALUES ('1144573297587326976', '0', '0,', 120, '0000000120,', '1', 0, '幽灵宝船', '幽灵宝船', '13', 'ttmy_fishing_room_index', '0', '', '', '', '0', 'system', '2019-06-28 19:48:34', 'system', '2022-09-20 19:08:49', '', '0', 'JeeSite', '', '', '', '', '', '', '', '', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `js_sys_dict_data` VALUES ('1144573317229252608', '0', '0,', 150, '0000000150,', '1', 0, '赤焰凤凰', '赤焰凤凰', '14', 'ttmy_fishing_room_index', '0', '', '', '', '0', 'system', '2019-06-28 19:48:39', 'system', '2022-09-20 19:09:07', '', '0', 'JeeSite', '', '', '', '', '', '', '', '', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `js_sys_dict_data` VALUES ('1149344606834356224', '0', '0,', 30, '0000000030,', '1', 0, '组织管理', '组织管理', 'office_user', 'sys_role_biz_scope', '1', '', '', '', '0', 'system', '2023-05-23 11:33:50', 'system', '2023-05-23 11:33:50', '', '0', 'JeeSite', '', '', '', '', '', '', '', '', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `js_sys_dict_data` VALUES ('1159846326527619072', '0', '0,', 30, '0000000030,', '1', 0, '实时兑换', '实时兑换', '1', 'osee_shop_reward_send_type', '0', '', '', '', '0', 'system', '2019-08-09 23:18:08', 'system', '2019-08-09 23:18:08', '', '0', 'JeeSite', '', '', '', '', '', '', '', '', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `js_sys_dict_data` VALUES ('1159846372060983296', '0', '0,', 60, '0000000060,', '1', 0, '手动发货', '手动发货', '2', 'osee_shop_reward_send_type', '0', '', '', '', '0', 'system', '2019-08-09 23:18:19', 'system', '2019-08-09 23:18:19', '', '0', 'JeeSite', '', '', '', '', '', '', '', '', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `js_sys_dict_data` VALUES ('1159846404021579776', '0', '0,', 90, '0000000090,', '1', 0, '自动发卡', '自动发卡', '3', 'osee_shop_reward_send_type', '0', '', '', '', '0', 'system', '2019-08-09 23:18:26', 'system', '2019-08-09 23:18:26', '', '0', 'JeeSite', '', '', '', '', '', '', '', '', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `js_sys_dict_data` VALUES ('1159847153921191936', '0', '0,', 390, '0000000390,', '1', 0, '分身炮', '分身炮', '19', 'osee_item_type', '0', '', '', '', '2', 'system', '2019-08-09 23:21:25', 'system', '2023-06-12 11:21:02', '', '0', 'JeeSite', '', '', '', '', '', '', '', '', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `js_sys_dict_data` VALUES ('1159863131178561536', '0', '0,', 30, '0000000030,', '1', 0, '未兑换', '未兑换', '1', 'ttmy_shop_stock_use_state', '0', '', '', '', '0', 'system', '2019-08-10 00:24:54', 'system', '2019-08-10 00:24:54', '', '0', 'JeeSite', '', '', '', '', '', '', '', '', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `js_sys_dict_data` VALUES ('1159863172412764160', '0', '0,', 60, '0000000060,', '1', 0, '已兑换', '已兑换', '2', 'ttmy_shop_stock_use_state', '0', '', '', '', '0', 'system', '2019-08-10 00:25:04', 'system', '2019-08-10 00:25:04', '', '0', 'JeeSite', '', '', '', '', '', '', '', '', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `js_sys_dict_data` VALUES ('1211925268011364352', '0', '0,', 270, '0000000270,', '1', 0, '经典渔场', '经典渔场', '8', 'osee_player_game_status', '0', '', '', '', '1', 'system', '2019-12-31 16:21:15', 'system', '2019-12-31 16:30:07', '', '0', 'JeeSite', '', '', '', '', '', '', '', '', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `js_sys_dict_data` VALUES ('1211925360483184640', '0', '0,', 300, '0000000300,', '1', 0, '龙晶战场', '龙晶战场', '9', 'osee_player_game_status', '0', '', '', '', '1', 'system', '2019-12-31 16:21:37', 'system', '2019-12-31 16:30:09', '', '0', 'JeeSite', '', '', '', '', '', '', '', '', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `js_sys_dict_data` VALUES ('1211925397091069952', '0', '0,', 330, '0000000330,', '1', 0, '大奖赛', '大奖赛', '10', 'osee_player_game_status', '0', '', '', '', '1', 'system', '2019-12-31 16:21:46', 'system', '2019-12-31 16:30:12', '', '0', 'JeeSite', '', '', '', '', '', '', '', '', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `js_sys_dict_data` VALUES ('1211928050831405056', '0', '0,', 270, '0000000270,', '1', 0, '经典渔场', '经典渔场', '7', 'osee_player_game_status', '0', '', '', '', '2', 'system', '2019-12-31 16:32:19', 'system', '2022-09-27 10:18:16', '', '0', 'JeeSite', '', '', '', '', '', '', '', '', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `js_sys_dict_data` VALUES ('1211928077242937344', '0', '0,', 300, '0000000300,', '1', 0, '龙晶战场', '龙晶战场', '9', 'osee_player_game_status', '0', '', '', '', '2', 'system', '2019-12-31 16:32:25', 'system', '2022-09-27 09:55:06', '', '0', 'JeeSite', '', '', '', '', '', '', '', '', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `js_sys_dict_data` VALUES ('1211928101871890432', '0', '0,', 330, '0000000330,', '1', 0, '大奖赛', '大奖赛', '9', 'osee_player_game_status', '0', '', '', '', '0', 'system', '2019-12-31 16:32:31', 'system', '2022-09-27 10:17:37', '', '0', 'JeeSite', '', '', '', '', '', '', '', '', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `js_sys_dict_data` VALUES ('1214085046242484224', '0', '0,', 90, '0000000090,', '1', 0, '线上代理', '线上代理', '3', 'osee_player_type', '0', '', '', '', '0', 'system', '2020-01-06 15:23:26', 'system', '2020-01-06 15:23:26', '', '0', 'JeeSite', '', '', '', '', '', '', '', '', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `js_sys_dict_data` VALUES ('1214121671922016256', '0', '0,', 30, '0000000030,', '1', 0, '华为支付', '华为支付', '1', 'osee_pay_way', '0', '', '', '', '0', 'system', '2020-01-06 17:48:59', 'system', '2020-01-06 17:49:25', '', '0', 'JeeSite', '', '', '', '', '', '', '', '', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `js_sys_dict_data` VALUES ('1214121847663353856', '0', '0,', 60, '0000000060,', '1', 0, '第三方支付', '第三方支付', '0', 'osee_pay_way', '0', '', '', '', '0', 'system', '2020-01-06 17:49:40', 'system', '2020-01-06 17:49:40', '', '0', 'JeeSite', '', '', '', '', '', '', '', '', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `js_sys_dict_data` VALUES ('1217025301970993152', '0', '0,', 30, '0000000030,', '1', 0, '玩家', '玩家', '1', 'osee_play_type', '0', '', '', '', '0', 'system', '2020-01-14 18:06:58', 'system', '2020-01-14 18:06:58', '', '0', 'JeeSite', '', '', '', '', '', '', '', '', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `js_sys_dict_data` VALUES ('1217025335617699840', '0', '0,', 60, '0000000060,', '1', 0, '代理', '代理', '2', 'osee_play_type', '0', '', '', '', '0', 'system', '2020-01-14 18:07:06', 'system', '2020-01-14 18:07:06', '', '0', 'JeeSite', '', '', '', '', '', '', '', '', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `js_sys_dict_data` VALUES ('1253193061556813824', '0', '0,', 1, '0000000001,', '1', 0, '龙晶', '龙晶', '18', 'osee_item_type', '0', '', '', '', '0', 'system', '2020-04-23 13:24:44', 'system', '2025-09-19 18:17:48', '', '0', 'JeeSite', '', '', '', '', '', '', '', '', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `js_sys_dict_data` VALUES ('1253196266902650880', '0', '0,', 30, '0000000030,', '1', 0, '正常', '正常', '0', 'osee_player_sendGift', '0', '', '', '', '1', 'system', '2020-04-23 13:37:28', 'system', '2026-01-12 18:27:49', '', '0', 'JeeSite', '', '', '', '', '', '', '', '', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `js_sys_dict_data` VALUES ('1253196305565745152', '0', '0,', 60, '0000000060,', '1', 0, '限制', '限制', '1', 'osee_player_sendGift', '0', '', '', '', '1', 'system', '2020-04-23 13:37:37', 'system', '2026-01-12 18:27:49', '', '0', 'JeeSite', '', '', '', '', '', '', '', '', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `js_sys_dict_data` VALUES ('1380418150506872832', '0', '0,', 30, '0000000030,', '1', 0, '关闭', '关闭', '0', 'osee_bang_way', '0', '', '', '', '0', 'system', '2021-04-09 15:11:49', 'system', '2021-04-09 15:11:49', '', '0', 'JeeSite', '', '', '', '', '', '', '', '', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `js_sys_dict_data` VALUES ('1380418179091054592', '0', '0,', 60, '0000000060,', '1', 0, '开启', '开启', '1', 'osee_bang_way', '0', '', '', '', '0', 'system', '2021-04-09 15:11:55', 'system', '2021-04-09 15:11:55', '', '0', 'JeeSite', '', '', '', '', '', '', '', '', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `js_sys_dict_data` VALUES ('1422465623892996096', '0', '0,', 450, '0000000450,', '1', 0, '京东卡', '京东卡', '1000', 'osee_item_type', '0', '', '', '', '2', 'system', '2021-08-03 15:53:28', 'system', '2023-06-12 11:21:06', '', '0', 'JeeSite', '', '', '', '', '', '', '', '', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `js_sys_dict_data` VALUES ('1426429295930740736', '0', '0,', 30, '0000000030,', '1', 0, '是', '是', '1', 'osee_yes_no', '0', '', '', '', '0', 'system', '2021-08-14 14:23:41', 'system', '2021-08-14 14:23:41', '', '0', 'JeeSite', '', '', '', '', '', '', '', '', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `js_sys_dict_data` VALUES ('1426429326897287168', '0', '0,', 60, '0000000060,', '1', 0, '否', '否', '0', 'osee_yes_no', '0', '', '', '', '0', 'system', '2021-08-14 14:23:48', 'system', '2021-08-14 14:23:48', '', '0', 'JeeSite', '', '', '', '', '', '', '', '', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `js_sys_dict_data` VALUES ('1435182170193944576', '0', '0,', 110, '0000000110,', '1', 0, '飞禽走兽', '飞禽走兽', '11', 'osee_draw_game', '0', '', '', '', '0', 'system', '2021-09-07 18:04:28', 'system', '2021-09-07 18:04:28', '', '0', 'JeeSite', '', '', '', '', '', '', '', '', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `js_sys_dict_data` VALUES ('1443506500253413376', '0', '0,', 30, '0000000030,', '1', 0, '体验场', '体验场', '51', 'game_room_index', '0', '捕鱼经典场', '', '', '0', 'system', '2021-09-30 17:22:23', 'system', '2022-09-27 09:21:05', '', '0', 'JeeSite', '', '', '', '', '', '', '', '', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `js_sys_dict_data` VALUES ('1443506590057656320', '0', '0,', 60, '0000000060,', '1', 0, '荧光女皇', '荧光女皇', '11', 'game_room_index', '0', '捕鱼能量场', '', '', '0', 'system', '2021-09-30 17:22:45', 'system', '2022-09-27 09:21:17', '', '0', 'JeeSite', '', '', '', '', '', '', '', '', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `js_sys_dict_data` VALUES ('1536625580212379648', '0', '0,', 30, '0000000030,', '1', 0, '库存阀值控制', '库存阀值控制', '0', 'osee_fish_kzlx', '0', '', '', '', '0', 'system', '2022-06-14 16:24:22', 'system', '2022-06-14 16:24:22', '', '0', 'JeeSite', '', '', '', '', '', '', '', '', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `js_sys_dict_data` VALUES ('1536625609585090560', '0', '0,', 60, '0000000060,', '1', 0, '库存阀值控制浮动', '库存阀值控制浮动', '1', 'osee_fish_kzlx', '0', '', '', '', '0', 'system', '2022-06-14 16:24:29', 'system', '2022-06-14 16:24:29', '', '0', 'JeeSite', '', '', '', '', '', '', '', '', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `js_sys_dict_data` VALUES ('1572181115147034624', '0', '0,', 180, '0000000180,', '1', 0, '觉醒阴阳龙', '觉醒阴阳龙', '15', 'ttmy_fishing_room_index', '0', '', '', '', '0', 'system', '2022-09-20 19:09:22', 'system', '2022-09-20 19:09:22', '', '0', 'JeeSite', '', '', '', '', '', '', '', '', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `js_sys_dict_data` VALUES ('1572181720548679680', '0', '0,', 210, '0000000210,', '1', 0, '大奖赛', '大奖赛', '31', 'ttmy_fishing_room_index', '0', '', '', '', '0', 'system', '2022-09-20 19:11:47', 'system', '2022-09-20 19:11:47', '', '0', 'JeeSite', '', '', '', '', '', '', '', '', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `js_sys_dict_data` VALUES ('1573152141544693760', '0', '0,', 90, '0000000090,', '1', 0, '概率控制', '概率控制', '2', 'osee_fish_kzlx', '0', '', '', '', '0', 'system', '2022-09-23 11:27:53', 'system', '2022-09-23 11:27:53', '', '0', 'JeeSite', '', '', '', '', '', '', '', '', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `js_sys_dict_data` VALUES ('1574569862710411264', '0', '0,', 90, '0000000090,', '1', 0, '圣龙降世', '圣龙降世', '12', 'game_room_index', '0', '', '', '', '0', 'system', '2022-09-27 09:21:24', 'system', '2022-09-27 09:21:24', '', '0', 'JeeSite', '', '', '', '', '', '', '', '', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `js_sys_dict_data` VALUES ('1574569901054738432', '0', '0,', 120, '0000000120,', '1', 0, '幽灵宝船', '幽灵宝船', '13', 'game_room_index', '0', '', '', '', '0', 'system', '2022-09-27 09:21:33', 'system', '2022-09-27 09:21:33', '', '0', 'JeeSite', '', '', '', '', '', '', '', '', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `js_sys_dict_data` VALUES ('1574569948278407168', '0', '0,', 150, '0000000150,', '1', 0, '赤焰凤凰', '赤焰凤凰', '14', 'game_room_index', '0', '', '', '', '0', 'system', '2022-09-27 09:21:44', 'system', '2022-09-27 09:21:44', '', '0', 'JeeSite', '', '', '', '', '', '', '', '', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `js_sys_dict_data` VALUES ('1574569983514755072', '0', '0,', 180, '0000000180,', '1', 0, '觉醒阴阳龙', '觉醒阴阳龙', '15', 'game_room_index', '0', '', '', '', '0', 'system', '2022-09-27 09:21:53', 'system', '2022-09-27 09:21:53', '', '0', 'JeeSite', '', '', '', '', '', '', '', '', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `js_sys_dict_data` VALUES ('1574570025633955840', '0', '0,', 210, '0000000210,', '1', 0, '大奖赛', '大奖赛', '31', 'game_room_index', '0', '', '', '', '0', 'system', '2022-09-27 09:22:03', 'system', '2022-09-27 09:22:03', '', '0', 'JeeSite', '', '', '', '', '', '', '', '', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `js_sys_dict_data` VALUES ('1574576819538214912', '0', '0,', 360, '0000000360,', '1', 0, '体验场', '体验场', '51', 'osee_player_game_status', '0', '', '', '', '0', 'system', '2022-09-27 09:49:03', 'system', '2022-09-27 10:18:24', '', '0', 'JeeSite', '', '', '', '', '', '', '', '', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `js_sys_dict_data` VALUES ('1574576901968871424', '0', '0,', 390, '0000000390,', '1', 0, '荧光女皇', '荧光女皇', '11', 'osee_player_game_status', '0', '', '', '', '0', 'system', '2022-09-27 09:49:22', 'system', '2022-09-27 10:18:30', '', '0', 'JeeSite', '', '', '', '', '', '', '', '', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `js_sys_dict_data` VALUES ('1574576928980189184', '0', '0,', 420, '0000000420,', '1', 0, '圣龙降世', '圣龙降世', '12', 'osee_player_game_status', '0', '', '', '', '0', 'system', '2022-09-27 09:49:29', 'system', '2022-09-27 10:18:36', '', '0', 'JeeSite', '', '', '', '', '', '', '', '', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `js_sys_dict_data` VALUES ('1574576954817101824', '0', '0,', 450, '0000000450,', '1', 0, '幽灵宝船', '幽灵宝船', '13', 'osee_player_game_status', '0', '', '', '', '0', 'system', '2022-09-27 09:49:35', 'system', '2022-09-27 10:18:44', '', '0', 'JeeSite', '', '', '', '', '', '', '', '', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `js_sys_dict_data` VALUES ('1574576979760627712', '0', '0,', 480, '0000000480,', '1', 0, '赤焰凤凰', '赤焰凤凰', '14', 'osee_player_game_status', '0', '', '', '', '0', 'system', '2022-09-27 09:49:41', 'system', '2022-09-27 10:18:51', '', '0', 'JeeSite', '', '', '', '', '', '', '', '', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `js_sys_dict_data` VALUES ('1574577004108562432', '0', '0,', 510, '0000000510,', '1', 0, '觉醒阴阳龙', '觉醒阴阳龙', '15', 'osee_player_game_status', '0', '', '', '', '0', 'system', '2022-09-27 09:49:47', 'system', '2022-09-27 10:18:56', '', '0', 'JeeSite', '', '', '', '', '', '', '', '', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `js_sys_dict_data` VALUES ('1578683202585051136', '0', '0,', 120, '0000000120,', '1', 0, '血池控制', '血池控制', '3', 'osee_fish_kzlx', '0', '', '', '', '0', 'system', '2022-10-08 17:46:21', 'system', '2022-10-08 17:46:21', '', '0', 'JeeSite', '', '', '', '', '', '', '', '', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `js_sys_dict_data` VALUES ('1844718791973449728', '0', '0,', 120, '0000000120,', '1', 0, '支付宝H5', '支付宝H5', '23', 'osee_recharge_type', '0', '', '', '', '0', 'system', '2024-10-11 20:36:48', 'system', '2024-10-11 20:37:45', '', '0', 'JeeSite', '', '', '', '', '', '', '', '', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `js_sys_dict_data` VALUES ('1891310419022020608', '0', '0,', 150, '0000000150,', '1', 0, '钻石支付', '钻石支付', '4', 'osee_recharge_type', '0', '', '', '', '0', 'system', '2025-02-17 10:15:17', 'system', '2025-02-17 10:15:17', '', '0', 'JeeSite', '', '', '', '', '', '', '', '', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);

-- ----------------------------
-- Table structure for js_sys_dict_type
-- ----------------------------
DROP TABLE IF EXISTS `js_sys_dict_type`;
CREATE TABLE `js_sys_dict_type`  (
  `id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '编号',
  `dict_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '字典名称',
  `dict_type` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '字典类型',
  `is_sys` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '是否系统字典',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '0' COMMENT '状态（0正常 1删除 2停用）',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '创建者',
  `create_date` datetime NOT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '更新者',
  `update_date` datetime NOT NULL COMMENT '更新时间',
  `remarks` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '备注信息',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_sys_dict_type_is`(`is_sys` ASC) USING BTREE,
  INDEX `idx_sys_dict_type_status`(`status` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '字典类型表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of js_sys_dict_type
-- ----------------------------
INSERT INTO `js_sys_dict_type` VALUES ('1085006647002337280', '是否', 'sys_yes_no', '1', '0', 'system', '2019-01-15 10:51:58', 'system', '2019-01-15 10:51:58', NULL);
INSERT INTO `js_sys_dict_type` VALUES ('1085006647144943616', '状态', 'sys_status', '1', '0', 'system', '2019-01-15 10:51:58', 'system', '2019-01-15 10:51:58', NULL);
INSERT INTO `js_sys_dict_type` VALUES ('1085006647203663872', '显示隐藏', 'sys_show_hide', '1', '0', 'system', '2019-01-15 10:51:58', 'system', '2019-01-15 10:51:58', NULL);
INSERT INTO `js_sys_dict_type` VALUES ('1085006647258189824', '国际化语言类型', 'sys_lang_type', '1', '0', 'system', '2019-01-15 10:51:58', 'system', '2019-01-15 10:51:58', NULL);
INSERT INTO `js_sys_dict_type` VALUES ('1085006647308521472', '客户端设备类型', 'sys_device_type', '1', '0', 'system', '2019-01-15 10:51:58', 'system', '2019-01-15 10:51:58', NULL);
INSERT INTO `js_sys_dict_type` VALUES ('1085006647354658816', '菜单归属系统', 'sys_menu_sys_code', '1', '0', 'system', '2019-01-15 10:51:58', 'system', '2019-01-15 10:51:58', NULL);
INSERT INTO `js_sys_dict_type` VALUES ('1085006647430156288', '菜单类型', 'sys_menu_type', '1', '0', 'system', '2019-01-15 10:51:58', 'system', '2019-01-15 10:51:58', NULL);
INSERT INTO `js_sys_dict_type` VALUES ('1085006647480487936', '菜单权重', 'sys_menu_weight', '1', '0', 'system', '2019-01-15 10:51:58', 'system', '2019-01-15 10:51:58', NULL);
INSERT INTO `js_sys_dict_type` VALUES ('1085006647535013888', '区域类型', 'sys_area_type', '1', '0', 'system', '2019-01-15 10:51:58', 'system', '2019-01-15 10:51:58', NULL);
INSERT INTO `js_sys_dict_type` VALUES ('1085006647581151232', '机构类型', 'sys_office_type', '1', '0', 'system', '2019-01-15 10:51:58', 'system', '2019-01-15 10:51:58', NULL);
INSERT INTO `js_sys_dict_type` VALUES ('1085006647631482880', '查询状态', 'sys_search_status', '1', '0', 'system', '2019-01-15 10:51:58', 'system', '2019-01-15 10:51:58', NULL);
INSERT INTO `js_sys_dict_type` VALUES ('1085006647681814528', '用户性别', 'sys_user_sex', '1', '0', 'system', '2019-01-15 10:51:58', 'system', '2019-01-15 10:51:58', NULL);
INSERT INTO `js_sys_dict_type` VALUES ('1085006647736340480', '用户状态', 'sys_user_status', '1', '0', 'system', '2019-01-15 10:51:58', 'system', '2019-01-15 10:51:58', NULL);
INSERT INTO `js_sys_dict_type` VALUES ('1085006647782477824', '用户类型', 'sys_user_type', '1', '0', 'system', '2019-01-15 10:51:58', 'system', '2019-01-15 10:51:58', NULL);
INSERT INTO `js_sys_dict_type` VALUES ('1085006647832809472', '角色分类', 'sys_role_type', '1', '0', 'system', '2019-01-15 10:51:58', 'system', '2019-01-15 10:51:58', NULL);
INSERT INTO `js_sys_dict_type` VALUES ('1085006647887335424', '角色数据范围', 'sys_role_data_scope', '1', '0', 'system', '2019-01-15 10:51:58', 'system', '2019-01-15 10:51:58', NULL);
INSERT INTO `js_sys_dict_type` VALUES ('1085006647937667072', '岗位分类', 'sys_post_type', '1', '0', 'system', '2019-01-15 10:51:58', 'system', '2019-01-15 10:51:58', NULL);
INSERT INTO `js_sys_dict_type` VALUES ('1085006648013164544', '日志类型', 'sys_log_type', '1', '0', 'system', '2019-01-15 10:51:58', 'system', '2019-01-15 10:51:58', NULL);
INSERT INTO `js_sys_dict_type` VALUES ('1085006648067690496', '作业分组', 'sys_job_group', '1', '0', 'system', '2019-01-15 10:51:58', 'system', '2019-01-15 10:51:58', NULL);
INSERT INTO `js_sys_dict_type` VALUES ('1085006648113827840', '作业错过策略', 'sys_job_misfire_instruction', '1', '0', 'system', '2019-01-15 10:51:58', 'system', '2019-01-15 10:51:58', NULL);
INSERT INTO `js_sys_dict_type` VALUES ('1085006648159965184', '作业状态', 'sys_job_status', '1', '0', 'system', '2019-01-15 10:51:58', 'system', '2019-01-15 10:51:58', NULL);
INSERT INTO `js_sys_dict_type` VALUES ('1085006648210296832', '作业任务类型', 'sys_job_type', '1', '0', 'system', '2019-01-15 10:51:58', 'system', '2019-01-15 10:51:58', NULL);
INSERT INTO `js_sys_dict_type` VALUES ('1085006648260628480', '作业任务事件', 'sys_job_event', '1', '0', 'system', '2019-01-15 10:51:58', 'system', '2019-01-15 10:51:58', NULL);
INSERT INTO `js_sys_dict_type` VALUES ('1085006648310960128', '消息类型', 'sys_msg_type', '1', '0', 'system', '2019-01-15 10:51:58', 'system', '2019-01-15 10:51:58', NULL);
INSERT INTO `js_sys_dict_type` VALUES ('1085006648357097472', '推送状态', 'sys_msg_push_status', '1', '0', 'system', '2019-01-15 10:51:58', 'system', '2019-01-15 10:51:58', NULL);
INSERT INTO `js_sys_dict_type` VALUES ('1085006648407429120', '读取状态', 'sys_msg_read_status', '1', '0', 'system', '2019-01-15 10:51:58', 'system', '2019-01-15 10:51:58', NULL);
INSERT INTO `js_sys_dict_type` VALUES ('1086070961353175040', '商城奖品限购更新频率', 'osee_shop_reward_refresh_type', '0', '0', 'system', '2019-01-18 09:21:10', 'system', '2019-01-18 09:21:10', '1688捕鱼商城奖品限购更新的频率数据');
INSERT INTO `js_sys_dict_type` VALUES ('1086074041016692736', '游戏cdk的使用状态', 'osee_game_cdk_use_status', '0', '0', 'system', '2019-01-18 09:33:24', 'system', '2019-01-24 10:56:52', '1688捕鱼的cdk使用状态：使用、未使用');
INSERT INTO `js_sys_dict_type` VALUES ('1086152761498714112', '游戏商城奖品类型', 'osee_shop_reward_type', '0', '0', 'system', '2019-01-18 14:46:13', 'system', '2019-01-18 14:46:21', '1688捕鱼商城奖品的类型');
INSERT INTO `js_sys_dict_type` VALUES ('1086194455051370496', '商城道具兑换订单状态', 'osee_shop_exchange_order_status', '0', '0', 'system', '2019-01-18 17:31:53', 'system', '2019-01-18 17:31:53', '1688捕鱼道具兑换订单的状态');
INSERT INTO `js_sys_dict_type` VALUES ('1087260546190913536', '玩家账号状态', 'osee_player_account_status', '0', '0', 'system', '2019-01-21 16:08:09', 'system', '2019-01-21 16:08:09', '1688捕鱼玩家的账号状态');
INSERT INTO `js_sys_dict_type` VALUES ('1087260878354624512', '玩家游戏状态', 'osee_player_game_status', '0', '0', 'system', '2019-01-21 16:09:28', 'system', '2019-01-21 16:09:28', '1688捕鱼玩家的游戏状态');
INSERT INTO `js_sys_dict_type` VALUES ('1087261552966479872', '玩家登录类型', 'osee_player_login_type', '0', '0', 'system', '2019-01-21 16:12:09', 'system', '2019-01-21 16:12:09', '1688捕鱼玩家登录游戏的类型');
INSERT INTO `js_sys_dict_type` VALUES ('1087261937164726272', '玩家输赢控制', 'osee_player_lose_ctrl', '0', '0', 'system', '2019-01-21 16:13:41', 'system', '2019-01-21 16:13:41', '1688捕鱼玩家的输赢控制选项');
INSERT INTO `js_sys_dict_type` VALUES ('1087262183148072960', '玩家类型', 'osee_player_type', '0', '0', 'system', '2019-01-21 16:14:39', 'system', '2019-01-21 16:14:39', '1688玩家身份类型');
INSERT INTO `js_sys_dict_type` VALUES ('1087982855365812224', '游戏物品类型列表', 'osee_item_type', '0', '0', 'system', '2019-01-23 15:58:21', 'system', '2019-01-23 15:58:21', '1688捕鱼游戏的物品类型列表');
INSERT INTO `js_sys_dict_type` VALUES ('1087984522647789568', '成功失败', 'osee_success_fail', '0', '0', 'system', '2019-01-23 16:04:59', 'system', '2019-01-23 16:04:59', '1688捕鱼定义的成功、失败');
INSERT INTO `js_sys_dict_type` VALUES ('1087985434695000064', '充值类型', 'osee_recharge_type', '0', '0', 'system', '2019-01-23 16:08:36', 'system', '2019-01-23 16:08:36', '1688捕鱼物品金币的充值类型');
INSERT INTO `js_sys_dict_type` VALUES ('1087989200777338880', '玩家账变操作类型', 'osee_player_tenure_operation_type', '0', '0', 'system', '2019-01-23 16:23:34', 'system', '2019-01-23 16:23:34', '1688捕鱼操作玩家账户的类型：充值or扣除');
INSERT INTO `js_sys_dict_type` VALUES ('1088308208928432128', '游戏内有抽水的游戏', 'osee_draw_game', '0', '0', 'system', '2019-01-24 13:31:11', 'system', '2019-01-24 13:31:11', '1688捕鱼内有抽水的游戏列表：二八杠、拉霸、五子棋');
INSERT INTO `js_sys_dict_type` VALUES ('1088710084017172480', '游戏支出类型', 'osee_pay_type', '0', '0', 'system', '2019-01-25 16:08:06', 'system', '2019-01-25 16:08:06', '1688捕鱼游戏内支出类型：1:实名认证 2:任务奖励 3:每日签到 4:CDK兑换');
INSERT INTO `js_sys_dict_type` VALUES ('1089702486028681216', '充值订单支付状态', 'osee_pay_order_status', '0', '0', 'system', '2019-01-28 09:51:33', 'system', '2019-01-28 09:51:33', '1688捕鱼充值订单充值状态：未支付、成功、失败');
INSERT INTO `js_sys_dict_type` VALUES ('1105440848414543872', '消息状态', 'msg_inner_msg_status', '0', '0', 'system', '2023-05-23 11:33:49', 'system', '2023-05-23 11:33:49', '');
INSERT INTO `js_sys_dict_type` VALUES ('1110820460724707328', '代理等级', 'ttmy_agent_level', '0', '1', 'system', '2019-03-27 16:26:51', 'system', '2026-01-12 18:28:02', '天天摸鱼游戏内代理的等级');
INSERT INTO `js_sys_dict_type` VALUES ('1144572930694778880', '龙晶兑换类型', 'ttmy_dragon_crystal_exchange_type', '0', '0', 'system', '2019-06-28 19:47:06', 'system', '2019-06-28 19:47:06', '兑换龙晶、兑换鱼雷');
INSERT INTO `js_sys_dict_type` VALUES ('1144573115936215040', '捕鱼游戏场次', 'ttmy_fishing_room_index', '0', '0', 'system', '2019-06-28 19:47:51', 'system', '2019-06-28 19:47:51', '初级场、中级场、高级场、超高级场、龙晶战场');
INSERT INTO `js_sys_dict_type` VALUES ('1149344200121085952', '角色业务范围', 'sys_role_biz_scope', '1', '0', 'system', '2023-05-23 11:33:50', 'system', '2023-05-23 11:33:50', '');
INSERT INTO `js_sys_dict_type` VALUES ('1159846173452300288', '游戏兑换商城奖品发货类型', 'osee_shop_reward_send_type', '0', '0', 'system', '2019-08-09 23:17:31', 'system', '2019-08-09 23:17:31', '1-实时兑换 2-手动发货 3-自动发卡');
INSERT INTO `js_sys_dict_type` VALUES ('1159862961648988160', '奖券商城库存物品兑换状态', 'ttmy_shop_stock_use_state', '0', '0', 'system', '2019-08-10 00:24:14', 'system', '2019-08-10 00:24:14', '1-未兑换 2-已兑换');
INSERT INTO `js_sys_dict_type` VALUES ('1214121420691595264', '支付通道', 'osee_pay_way', '0', '0', 'system', '2020-01-06 17:47:59', 'system', '2020-01-06 17:48:06', '');
INSERT INTO `js_sys_dict_type` VALUES ('1217025134278524928', '代理类型', 'osee_play_type', '0', '0', 'system', '2020-01-14 18:06:18', 'system', '2020-01-14 18:06:18', '');
INSERT INTO `js_sys_dict_type` VALUES ('1253196183863820288', '赠送类型', 'osee_player_sendGift', '0', '1', 'system', '2020-04-23 13:37:08', 'system', '2026-01-12 18:27:49', '');
INSERT INTO `js_sys_dict_type` VALUES ('1380418058202824704', '绑定权限', 'osee_bang_way', '0', '0', 'system', '2021-04-09 15:11:27', 'system', '2021-04-09 15:11:27', '');
INSERT INTO `js_sys_dict_type` VALUES ('1426429223100846080', '是否', 'osee_yes_no', '0', '0', 'system', '2021-08-14 14:23:23', 'system', '2021-08-14 14:23:23', '');
INSERT INTO `js_sys_dict_type` VALUES ('1443506138603745280', '捕鱼房间类型', 'game_room_index', '0', '0', 'system', '2021-09-30 17:20:57', 'system', '2021-09-30 17:21:03', '捕鱼房间对应的类型');
INSERT INTO `js_sys_dict_type` VALUES ('1536625218252333056', '龙晶场控制类型', 'osee_fish_kzlx', '0', '0', 'system', '2022-06-14 16:22:56', 'system', '2022-06-14 16:22:56', '');

-- ----------------------------
-- Table structure for js_sys_employee
-- ----------------------------
DROP TABLE IF EXISTS `js_sys_employee`;
CREATE TABLE `js_sys_employee`  (
  `emp_code` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '员工编码',
  `emp_no` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '员工工号',
  `emp_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '员工姓名',
  `emp_name_en` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '英文名',
  `office_code` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '机构编码',
  `office_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '机构名称',
  `company_code` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '公司编码',
  `company_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '公司名称',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '状态（0在职 1删除 2离职）',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '创建者',
  `create_date` datetime NOT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '更新者',
  `update_date` datetime NOT NULL COMMENT '更新时间',
  `remarks` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '备注信息',
  `corp_code` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '0' COMMENT '租户代码',
  `corp_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'JeeSite' COMMENT '租户名称',
  PRIMARY KEY (`emp_code`) USING BTREE,
  INDEX `idx_sys_employee_cco`(`company_code` ASC) USING BTREE,
  INDEX `idx_sys_employee_cc`(`corp_code` ASC) USING BTREE,
  INDEX `idx_sys_employee_ud`(`update_date` ASC) USING BTREE,
  INDEX `idx_sys_employee_oc`(`office_code` ASC) USING BTREE,
  INDEX `idx_sys_employee_status`(`status` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '员工表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of js_sys_employee
-- ----------------------------

-- ----------------------------
-- Table structure for js_sys_employee_office
-- ----------------------------
DROP TABLE IF EXISTS `js_sys_employee_office`;
CREATE TABLE `js_sys_employee_office`  (
  `id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '编号',
  `emp_code` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '员工编码',
  `office_code` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '机构编码',
  `post_code` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '岗位编码',
  PRIMARY KEY (`emp_code`, `office_code`) USING BTREE,
  UNIQUE INDEX `id`(`id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '员工附属机构关系表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of js_sys_employee_office
-- ----------------------------

-- ----------------------------
-- Table structure for js_sys_employee_post
-- ----------------------------
DROP TABLE IF EXISTS `js_sys_employee_post`;
CREATE TABLE `js_sys_employee_post`  (
  `emp_code` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '员工编码',
  `post_code` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '岗位编码',
  PRIMARY KEY (`emp_code`, `post_code`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '员工与岗位关联表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of js_sys_employee_post
-- ----------------------------

-- ----------------------------
-- Table structure for js_sys_file_entity
-- ----------------------------
DROP TABLE IF EXISTS `js_sys_file_entity`;
CREATE TABLE `js_sys_file_entity`  (
  `file_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '文件编号',
  `file_md5` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '文件MD5',
  `file_path` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '文件相对路径',
  `file_content_type` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '文件内容类型',
  `file_extension` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '文件后缀扩展名',
  `file_size` decimal(31, 0) NOT NULL COMMENT '文件大小(单位B)',
  `file_meta` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '文件信息(JSON格式)',
  PRIMARY KEY (`file_id`) USING BTREE,
  INDEX `idx_sys_file_entity_md5`(`file_md5` ASC) USING BTREE,
  INDEX `idx_sys_file_entity_size`(`file_size` ASC) USING BTREE,
  INDEX `file_md5`(`file_md5` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '文件实体表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of js_sys_file_entity
-- ----------------------------
INSERT INTO `js_sys_file_entity` VALUES ('1090440689115258880', '2250f49f3594daaff68a6ebbb62dd9d2', '201901/', 'image/gif', 'gif', 966634, NULL);
INSERT INTO `js_sys_file_entity` VALUES ('1090441747373793280', 'b32b7f486e75bfe586ae1e9c8f24b3cc', '201901/', 'image/jpeg', 'jpg', 346570, NULL);
INSERT INTO `js_sys_file_entity` VALUES ('1090526317766332416', 'bf010ab4165f23458732259f8d71429e', '201901/', 'image/jpeg', 'jpg', 108332, NULL);
INSERT INTO `js_sys_file_entity` VALUES ('1090526507093020672', 'aabd87943a47c4dfb0060f805922eb13', '201901/', 'image/x-icon', 'ico', 355574, NULL);
INSERT INTO `js_sys_file_entity` VALUES ('1098524226419974144', 'c04975e171170ae1f2d9c001c1794b3d', '201902/', 'image/png', 'png', 9445, NULL);
INSERT INTO `js_sys_file_entity` VALUES ('1110892453675864064', 'eb61717d9ec129483e921c66d0cd6563', '201903/', 'image/jpeg', 'jpg', 42268, NULL);
INSERT INTO `js_sys_file_entity` VALUES ('1110893742434488320', '8392722604d029e529ff3d2f73fb318c', '201903/', 'image/png', 'png', 350689, NULL);
INSERT INTO `js_sys_file_entity` VALUES ('1110893803495165952', '0786b3cf3fb7a484e403055b4cc8823b', '201903/', 'image/jpeg', 'jpg', 51662, NULL);
INSERT INTO `js_sys_file_entity` VALUES ('1110894241846071296', '2d318a32c6d802b932ca1a2f5d3c6354', '201903/', 'image/png', 'png', 1155310, NULL);
INSERT INTO `js_sys_file_entity` VALUES ('1110897970569744384', '8581a9e892353d60117dcd228d297271', '201903/', 'image/png', 'png', 35624, NULL);
INSERT INTO `js_sys_file_entity` VALUES ('1110898436921823232', 'be55f8273cb5ddb45bdce72db91b61e6', '201903/', 'image/png', 'png', 33037, NULL);
INSERT INTO `js_sys_file_entity` VALUES ('1110899415654273024', '6894b3b7d24fc4186caa827f231e086b', '201903/', 'image/png', 'png', 31025, NULL);
INSERT INTO `js_sys_file_entity` VALUES ('1115181067949461504', '97c5c47099114d47284ccb5f134e09a7', '201904/', 'image/jpeg', 'jpg', 28664, NULL);
INSERT INTO `js_sys_file_entity` VALUES ('1132839003179655168', 'cabf8c46fa58915d4e968820554af805', '201905/', 'image/png', 'png', 14899, NULL);
INSERT INTO `js_sys_file_entity` VALUES ('1138016226429739008', '426eb13451db60d640b144d3a57127f4', '201906/', 'image/png', 'png', 10377, NULL);
INSERT INTO `js_sys_file_entity` VALUES ('1138016395137228800', 'cd98d6b095850355a66cb50a27ba421e', '201906/', 'image/png', 'png', 11097, NULL);
INSERT INTO `js_sys_file_entity` VALUES ('1138018654503936000', 'cb1af1d5191f441da48ff44194161fb3', '201906/', 'image/png', 'png', 10645, NULL);
INSERT INTO `js_sys_file_entity` VALUES ('1138018753632116736', '472f95bfa1cd006efc3dc355eb07d6d8', '201906/', 'image/png', 'png', 10613, NULL);
INSERT INTO `js_sys_file_entity` VALUES ('1159359989447577600', '2f97d7895f1e0c9514c7e10037e4cd89', '201908/', 'image/png', 'png', 200331, NULL);
INSERT INTO `js_sys_file_entity` VALUES ('1159858778992570368', '044bc35e64837e34ff8722ebe2dbc504', '201908/', 'image/png', 'png', 25266, NULL);
INSERT INTO `js_sys_file_entity` VALUES ('1159859051840434176', 'a2d5e2b55136ed099a610727aba92cff', '201908/', 'image/png', 'png', 25174, NULL);
INSERT INTO `js_sys_file_entity` VALUES ('1159859215426678784', '28a0bae2c17e268de7829911d82062de', '201908/', 'image/png', 'png', 25320, NULL);
INSERT INTO `js_sys_file_entity` VALUES ('1159860036394577920', '11e527b965454d8844a40d47faf83a66', '201908/', 'image/png', 'png', 158777, NULL);
INSERT INTO `js_sys_file_entity` VALUES ('1168021726061793280', '8a3a77bb4c01ef81d1d9b85b8b7eafb3', '201909/', 'image/png', 'png', 7860, NULL);
INSERT INTO `js_sys_file_entity` VALUES ('1168021963698475008', '2c30d007edb13dd1c95fee253764ce00', '201909/', 'image/png', 'png', 7951, NULL);
INSERT INTO `js_sys_file_entity` VALUES ('1168022084737699840', 'e103a037c2794fe291ea944c70fa84a8', '201909/', 'image/png', 'png', 7994, NULL);
INSERT INTO `js_sys_file_entity` VALUES ('1168022568181567488', 'a7da19c35facfc166fae26138631269e', '201909/', 'image/png', 'png', 19173, NULL);
INSERT INTO `js_sys_file_entity` VALUES ('1169531047514112000', 'a2af86ce52cda1345c6ceb5299fa85cb', '201909/', 'image/jpeg', 'jpg', 92274, NULL);
INSERT INTO `js_sys_file_entity` VALUES ('1169531200304218112', '5ad80fcc6237df5bcba840666e928ca4', '201909/', 'image/jpeg', 'jpg', 115705, NULL);
INSERT INTO `js_sys_file_entity` VALUES ('1169531351408214016', '93a38afa1c0458294dd4a25ec139d7e6', '201909/', 'image/jpeg', 'jpg', 98761, NULL);
INSERT INTO `js_sys_file_entity` VALUES ('1169531470249623552', '6bcde5a3e8ab0c59d5132030390bccd2', '201909/', 'image/jpeg', 'jpg', 87603, NULL);
INSERT INTO `js_sys_file_entity` VALUES ('1169543832092442624', 'f17dc06833fd525b82d33662794369b9', '201909/', 'image/png', 'png', 27926, NULL);
INSERT INTO `js_sys_file_entity` VALUES ('1169547733051015168', '5514caf92004675333cb267293c90fe2', '201909/', 'image/png', 'png', 28211, NULL);
INSERT INTO `js_sys_file_entity` VALUES ('1169552105889759232', '3270a28dce4685b2a53f6461839626eb', '201909/', 'image/png', 'png', 26267, NULL);
INSERT INTO `js_sys_file_entity` VALUES ('1169552183975116800', '9b426d7810cd134dc1b8a1ce889325ef', '201909/', 'image/png', 'png', 26210, NULL);
INSERT INTO `js_sys_file_entity` VALUES ('1581831243228028928', '17fc2cdf443bbe43beae3875064786f7', '202210/', 'image/jpeg', 'jpg', 143432, NULL);
INSERT INTO `js_sys_file_entity` VALUES ('1892111776792543234', '958de3a5a0c530fecc369f9daf451fd4', '202502/', 'image/png', 'png', 213766, '{\"width\":500,\"height\":500}');

-- ----------------------------
-- Table structure for js_sys_file_upload
-- ----------------------------
DROP TABLE IF EXISTS `js_sys_file_upload`;
CREATE TABLE `js_sys_file_upload`  (
  `id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '编号',
  `file_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '文件编号',
  `file_name` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '文件名称',
  `file_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '文件分类（image、media、file）',
  `file_sort` decimal(10, 0) NULL DEFAULT NULL COMMENT '文件排序',
  `biz_key` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '业务主键',
  `biz_type` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '业务类型',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '0' COMMENT '状态（0正常 1删除 2停用）',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '创建者',
  `create_date` datetime NOT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '更新者',
  `update_date` datetime NOT NULL COMMENT '更新时间',
  `remarks` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '备注信息',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_sys_file_biz_ft`(`file_type` ASC) USING BTREE,
  INDEX `idx_sys_file_biz_fi`(`file_id` ASC) USING BTREE,
  INDEX `idx_sys_file_biz_status`(`status` ASC) USING BTREE,
  INDEX `idx_sys_file_biz_cb`(`create_by` ASC) USING BTREE,
  INDEX `idx_sys_file_biz_ud`(`update_date` ASC) USING BTREE,
  INDEX `idx_sys_file_biz_bt`(`biz_type` ASC) USING BTREE,
  INDEX `idx_sys_file_biz_bk`(`biz_key` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '文件上传表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of js_sys_file_upload
-- ----------------------------
INSERT INTO `js_sys_file_upload` VALUES ('1090440689178173440', '1090440689115258880', 'b3fb43166d224f4acb9108a409f790529822d179.gif', 'image', NULL, NULL, NULL, '0', 'system', '2019-01-30 10:44:54', 'system', '2019-01-30 10:44:54', NULL);
INSERT INTO `js_sys_file_upload` VALUES ('1090441105278296064', '1090440689115258880', 'b3fb43166d224f4acb9108a409f790529822d179.gif', 'file', NULL, NULL, NULL, '0', 'system', '2019-01-30 10:46:34', 'system', '2019-01-30 10:46:34', NULL);
INSERT INTO `js_sys_file_upload` VALUES ('1090441747776446464', '1090441747373793280', '581cd554564e9258e1c7bca49182d158cdbf4e43.jpg', 'image', NULL, NULL, NULL, '0', 'system', '2019-01-30 10:49:07', 'system', '2019-01-30 10:49:07', NULL);
INSERT INTO `js_sys_file_upload` VALUES ('1090442059326644224', '1090441747373793280', '581cd554564e9258e1c7bca49182d158cdbf4e43.jpg', 'file', NULL, NULL, NULL, '0', 'system', '2019-01-30 10:50:21', 'system', '2019-01-30 10:50:21', NULL);
INSERT INTO `js_sys_file_upload` VALUES ('1090442256580567040', '1090440689115258880', 'b3fb43166d224f4acb9108a409f790529822d179.gif', 'file', NULL, NULL, NULL, '0', 'system', '2019-01-30 10:51:08', 'system', '2019-01-30 10:51:08', NULL);
INSERT INTO `js_sys_file_upload` VALUES ('1090442749408354304', '1090440689115258880', 'b3fb43166d224f4acb9108a409f790529822d179.gif', 'file', NULL, NULL, NULL, '0', 'system', '2019-01-30 10:53:06', 'system', '2019-01-30 10:53:06', NULL);
INSERT INTO `js_sys_file_upload` VALUES ('1090442984562008064', '1090440689115258880', 'b3fb43166d224f4acb9108a409f790529822d179.gif', 'file', NULL, NULL, NULL, '0', 'system', '2019-01-30 10:54:02', 'system', '2019-01-30 10:54:02', NULL);
INSERT INTO `js_sys_file_upload` VALUES ('1090445441622380544', '1090440689115258880', 'b3fb43166d224f4acb9108a409f790529822d179.gif', 'file', NULL, NULL, NULL, '0', 'system', '2019-01-30 11:03:47', 'system', '2019-01-30 11:03:47', NULL);
INSERT INTO `js_sys_file_upload` VALUES ('1090446814865272832', '1090440689115258880', 'b3fb43166d224f4acb9108a409f790529822d179.gif', 'file', NULL, NULL, NULL, '0', 'system', '2019-01-30 11:09:15', 'system', '2019-01-30 11:09:15', NULL);
INSERT INTO `js_sys_file_upload` VALUES ('1090447606861238272', '1090441747373793280', '581cd554564e9258e1c7bca49182d158cdbf4e43.jpg', 'file', NULL, NULL, NULL, '0', 'system', '2019-01-30 11:12:24', 'system', '2019-01-30 11:12:24', NULL);
INSERT INTO `js_sys_file_upload` VALUES ('1090451405480878080', '1090440689115258880', 'b3fb43166d224f4acb9108a409f790529822d179.gif', 'file', NULL, NULL, NULL, '0', 'system', '2019-01-30 11:27:29', 'system', '2019-01-30 11:27:29', NULL);
INSERT INTO `js_sys_file_upload` VALUES ('1090451920575909888', '1090440689115258880', 'b3fb43166d224f4acb9108a409f790529822d179.gif', 'file', NULL, NULL, NULL, '0', 'system', '2019-01-30 11:29:32', 'system', '2019-01-30 11:29:32', NULL);
INSERT INTO `js_sys_file_upload` VALUES ('1090453224380747776', '1090440689115258880', 'b3fb43166d224f4acb9108a409f790529822d179.gif', 'file', NULL, NULL, NULL, '0', 'system', '2019-01-30 11:34:43', 'system', '2019-01-30 11:34:43', NULL);
INSERT INTO `js_sys_file_upload` VALUES ('1090454095659184128', '1090440689115258880', 'b3fb43166d224f4acb9108a409f790529822d179.gif', 'file', NULL, NULL, NULL, '0', 'system', '2019-01-30 11:38:11', 'system', '2019-01-30 11:38:11', NULL);
INSERT INTO `js_sys_file_upload` VALUES ('1090455234545348608', '1090440689115258880', 'b3fb43166d224f4acb9108a409f790529822d179.gif', 'file', NULL, NULL, NULL, '0', 'system', '2019-01-30 11:42:42', 'system', '2019-01-30 11:42:42', NULL);
INSERT INTO `js_sys_file_upload` VALUES ('1090455599183351808', '1090440689115258880', 'b3fb43166d224f4acb9108a409f790529822d179.gif', 'file', NULL, NULL, NULL, '0', 'system', '2019-01-30 11:44:09', 'system', '2019-01-30 11:44:09', NULL);
INSERT INTO `js_sys_file_upload` VALUES ('1090526318122848256', '1090526317766332416', 'Navicat_Keygen_Patch_v3.7_By_DFoX_URET.jpg', 'image', NULL, NULL, NULL, '0', 'admin', '2019-01-30 16:25:10', 'admin', '2019-01-30 16:25:10', NULL);
INSERT INTO `js_sys_file_upload` VALUES ('1090526507126575104', '1090526507093020672', 'idea.ico', 'image', NULL, NULL, NULL, '0', 'admin', '2019-01-30 16:25:55', 'admin', '2019-01-30 16:25:55', NULL);
INSERT INTO `js_sys_file_upload` VALUES ('1098524227040731136', '1098524226419974144', 'TIM图片20190221175154.png', 'image', NULL, NULL, NULL, '0', 'system', '2019-02-21 18:06:00', 'system', '2019-02-21 18:06:00', NULL);
INSERT INTO `js_sys_file_upload` VALUES ('1098524538107092992', '1098524226419974144', 'TIM图片20190221175154.png', 'file', NULL, NULL, NULL, '0', 'system', '2019-02-21 18:07:14', 'system', '2019-02-21 18:07:14', NULL);
INSERT INTO `js_sys_file_upload` VALUES ('1110892455760433152', '1110892453675864064', '3d0a05835d07f561e4cd0a6a6f8f587.jpg', 'image', NULL, NULL, NULL, '0', 'admin', '2019-03-27 21:12:55', 'admin', '2019-03-27 21:12:55', NULL);
INSERT INTO `js_sys_file_upload` VALUES ('1110893743030079488', '1110893742434488320', '1106846133641838592.png', 'image', NULL, NULL, NULL, '0', 'admin', '2019-03-27 21:18:02', 'admin', '2019-03-27 21:18:02', NULL);
INSERT INTO `js_sys_file_upload` VALUES ('1110893803704881152', '1110893803495165952', '1106567174723239936.jpg', 'image', NULL, NULL, NULL, '0', 'admin', '2019-03-27 21:18:17', 'admin', '2019-03-27 21:18:17', NULL);
INSERT INTO `js_sys_file_upload` VALUES ('1110894242974339072', '1110894241846071296', '1101076746869653504.png', 'image', NULL, NULL, NULL, '0', 'admin', '2019-03-27 21:20:02', 'admin', '2019-03-27 21:20:02', NULL);
INSERT INTO `js_sys_file_upload` VALUES ('1110897970678796288', '1110897970569744384', 'prop127.png', 'image', NULL, NULL, NULL, '0', 'admin', '2019-03-27 21:34:50', 'admin', '2019-03-27 21:34:50', NULL);
INSERT INTO `js_sys_file_upload` VALUES ('1110898436997320704', '1110898436921823232', 'prop76.png', 'image', NULL, NULL, NULL, '0', 'admin', '2019-03-27 21:36:42', 'admin', '2019-03-27 21:36:42', NULL);
INSERT INTO `js_sys_file_upload` VALUES ('1110899415750742016', '1110899415654273024', 'prop75.png', 'image', NULL, NULL, NULL, '0', 'admin', '2019-03-27 21:40:35', 'admin', '2019-03-27 21:40:35', NULL);
INSERT INTO `js_sys_file_upload` VALUES ('1111288103808892928', '1110892453675864064', '3d0a05835d07f561e4cd0a6a6f8f587.jpg', 'file', NULL, NULL, NULL, '0', 'admin', '2019-03-28 23:25:05', 'admin', '2019-03-28 23:25:05', NULL);
INSERT INTO `js_sys_file_upload` VALUES ('1111519293857955840', '1110898436921823232', 'prop76.png', 'file', NULL, NULL, NULL, '0', 'admin', '2019-03-29 14:43:45', 'admin', '2019-03-29 14:43:45', NULL);
INSERT INTO `js_sys_file_upload` VALUES ('1111519565774684160', '1110897970569744384', 'prop127.png', 'file', NULL, NULL, NULL, '0', 'admin', '2019-03-29 14:44:50', 'admin', '2019-03-29 14:44:50', NULL);
INSERT INTO `js_sys_file_upload` VALUES ('1111519646540201984', '1110899415654273024', 'prop75.png', 'file', NULL, NULL, NULL, '0', 'admin', '2019-03-29 14:45:09', 'admin', '2019-03-29 14:45:09', NULL);
INSERT INTO `js_sys_file_upload` VALUES ('1111519805290414080', '1110894241846071296', '1101076746869653504.png', 'file', NULL, NULL, NULL, '0', 'admin', '2019-03-29 14:45:47', 'admin', '2019-03-29 14:45:47', NULL);
INSERT INTO `js_sys_file_upload` VALUES ('1111520319512084480', '1110893803495165952', '1106567174723239936.jpg', 'file', NULL, NULL, NULL, '0', 'admin', '2019-03-29 14:47:50', 'admin', '2019-03-29 14:47:50', NULL);
INSERT INTO `js_sys_file_upload` VALUES ('1111520893410312192', '1110893803495165952', '1106567174723239936.jpg', 'file', NULL, NULL, NULL, '0', 'admin', '2019-03-29 14:50:07', 'admin', '2019-03-29 14:50:07', NULL);
INSERT INTO `js_sys_file_upload` VALUES ('1115181068402446336', '1115181067949461504', 'qrcode_for_gh_19ffc1307254_258.jpg', 'image', NULL, NULL, NULL, '0', 'admin', '2019-04-08 17:14:20', 'admin', '2019-04-08 17:14:20', NULL);
INSERT INTO `js_sys_file_upload` VALUES ('1132839003498422272', '1132839003179655168', 'zuanshi.png', 'image', NULL, NULL, NULL, '0', 'admin', '2019-05-27 10:40:40', 'admin', '2019-05-27 10:40:40', NULL);
INSERT INTO `js_sys_file_upload` VALUES ('1138016226819809280', '1138016226429739008', 'yulei (3).png', 'image', NULL, NULL, NULL, '0', 'admin', '2019-06-10 17:33:06', 'admin', '2019-06-10 17:33:06', NULL);
INSERT INTO `js_sys_file_upload` VALUES ('1138016395258863616', '1138016395137228800', 'yulei (4).png', 'image', NULL, NULL, NULL, '0', 'admin', '2019-06-10 17:33:47', 'admin', '2019-06-10 17:33:47', NULL);
INSERT INTO `js_sys_file_upload` VALUES ('1138018654571044864', '1138018654503936000', '未标题-1_0001_10yuan.png_0002_yulei-(4).png', 'image', NULL, NULL, NULL, '0', 'admin', '2019-06-10 17:42:45', 'admin', '2019-06-10 17:42:45', NULL);
INSERT INTO `js_sys_file_upload` VALUES ('1138018753695031296', '1138018753632116736', '未标题-1_0001_10yuan.png_0003_yulei-(3).png', 'image', NULL, NULL, NULL, '0', 'admin', '2019-06-10 17:43:09', 'admin', '2019-06-10 17:43:09', NULL);
INSERT INTO `js_sys_file_upload` VALUES ('1159359990399684608', '1159359989447577600', 'QQ浏览器截图20190225183515.png', 'image', NULL, NULL, NULL, '0', 'admin', '2019-08-08 15:05:36', 'admin', '2019-08-08 15:05:36', NULL);
INSERT INTO `js_sys_file_upload` VALUES ('1159858779273588736', '1159858778992570368', '100yuan.png', 'image', NULL, NULL, NULL, '0', 'admin', '2019-08-10 00:07:37', 'admin', '2019-08-10 00:07:37', NULL);
INSERT INTO `js_sys_file_upload` VALUES ('1159859051949486080', '1159859051840434176', '10yuan.png', 'image', NULL, NULL, NULL, '0', 'admin', '2019-08-10 00:08:42', 'admin', '2019-08-10 00:08:42', NULL);
INSERT INTO `js_sys_file_upload` VALUES ('1159859215510564864', '1159859215426678784', '50yuan.png', 'image', NULL, NULL, NULL, '0', 'admin', '2019-08-10 00:09:21', 'admin', '2019-08-10 00:09:21', NULL);
INSERT INTO `js_sys_file_upload` VALUES ('1159860036780453888', '1159860036394577920', 'longzhu_01.png', 'image', NULL, NULL, NULL, '0', 'admin', '2019-08-10 00:12:37', 'admin', '2019-08-10 00:12:37', NULL);
INSERT INTO `js_sys_file_upload` VALUES ('1168016424608337920', '1159859051840434176', '10yuan.png', 'file', NULL, NULL, NULL, '0', 'admin', '2019-09-01 12:23:11', 'admin', '2019-09-01 12:23:11', NULL);
INSERT INTO `js_sys_file_upload` VALUES ('1168017011374690304', '1159859215426678784', '50yuan.png', 'file', NULL, NULL, NULL, '0', 'admin', '2019-09-01 12:25:31', 'admin', '2019-09-01 12:25:31', NULL);
INSERT INTO `js_sys_file_upload` VALUES ('1168017217046581248', '1159858778992570368', '100yuan.png', 'file', NULL, NULL, NULL, '0', 'admin', '2019-09-01 12:26:20', 'admin', '2019-09-01 12:26:20', NULL);
INSERT INTO `js_sys_file_upload` VALUES ('1168021726271508480', '1168021726061793280', 'longzhu3.png', 'image', NULL, NULL, NULL, '0', 'admin', '2019-09-01 12:44:15', 'admin', '2019-09-01 12:44:15', NULL);
INSERT INTO `js_sys_file_upload` VALUES ('1168021963757195264', '1168021963698475008', 'longzhu1.png', 'image', NULL, NULL, NULL, '0', 'admin', '2019-09-01 12:45:12', 'admin', '2019-09-01 12:45:12', NULL);
INSERT INTO `js_sys_file_upload` VALUES ('1168022084788031488', '1168022084737699840', 'longzhu2.png', 'image', NULL, NULL, NULL, '0', 'admin', '2019-09-01 12:45:40', 'admin', '2019-09-01 12:45:40', NULL);
INSERT INTO `js_sys_file_upload` VALUES ('1168022568231899136', '1168022568181567488', 'btn_haojiao.png', 'image', NULL, NULL, NULL, '0', 'admin', '2019-09-01 12:47:36', 'admin', '2019-09-01 12:47:36', NULL);
INSERT INTO `js_sys_file_upload` VALUES ('1168368432926298112', '1159859051840434176', '10yuan.png', 'file', NULL, NULL, NULL, '0', 'admin', '2019-09-02 11:41:56', 'admin', '2019-09-02 11:41:56', NULL);
INSERT INTO `js_sys_file_upload` VALUES ('1168368519941328896', '1159859215426678784', '50yuan.png', 'file', NULL, NULL, NULL, '0', 'admin', '2019-09-02 11:42:17', 'admin', '2019-09-02 11:42:17', NULL);
INSERT INTO `js_sys_file_upload` VALUES ('1168368616611647488', '1159858778992570368', '100yuan.png', 'file', NULL, NULL, NULL, '0', 'admin', '2019-09-02 11:42:40', 'admin', '2019-09-02 11:42:40', NULL);
INSERT INTO `js_sys_file_upload` VALUES ('1168774820002996224', '1159859215426678784', '50yuan.png', 'file', NULL, NULL, NULL, '0', 'admin', '2019-09-03 14:36:47', 'admin', '2019-09-03 14:36:47', NULL);
INSERT INTO `js_sys_file_upload` VALUES ('1168774876026314752', '1159858778992570368', '100yuan.png', 'file', NULL, NULL, NULL, '0', 'admin', '2019-09-03 14:37:00', 'admin', '2019-09-03 14:37:00', NULL);
INSERT INTO `js_sys_file_upload` VALUES ('1169531048378138624', '1169531047514112000', '5b6ba251N1bd864cb.jpg', 'image', NULL, NULL, NULL, '0', 'admin', '2019-09-05 16:41:45', 'admin', '2019-09-05 16:41:45', NULL);
INSERT INTO `js_sys_file_upload` VALUES ('1169531200576847872', '1169531200304218112', '5b6ba252Nb73d645b.jpg', 'image', NULL, NULL, NULL, '0', 'admin', '2019-09-05 16:42:22', 'admin', '2019-09-05 16:42:22', NULL);
INSERT INTO `js_sys_file_upload` VALUES ('1169531351659872256', '1169531351408214016', '5b6ba1d5N81095128.jpg', 'image', NULL, NULL, NULL, '0', 'admin', '2019-09-05 16:42:58', 'admin', '2019-09-05 16:42:58', NULL);
INSERT INTO `js_sys_file_upload` VALUES ('1169531470505476096', '1169531470249623552', '5b6ba1d6N602c76d4.jpg', 'image', NULL, NULL, NULL, '0', 'admin', '2019-09-05 16:43:26', 'admin', '2019-09-05 16:43:26', NULL);
INSERT INTO `js_sys_file_upload` VALUES ('1169532879544156160', '1159859051840434176', '10yuan.png', 'file', NULL, NULL, NULL, '0', 'admin', '2019-09-05 16:49:02', 'admin', '2019-09-05 16:49:02', NULL);
INSERT INTO `js_sys_file_upload` VALUES ('1169543832289574912', '1169543832092442624', '100yuan.png', 'image', NULL, NULL, NULL, '0', 'admin', '2019-09-05 17:32:33', 'admin', '2019-09-05 17:32:33', NULL);
INSERT INTO `js_sys_file_upload` VALUES ('1169547733168455680', '1169547733051015168', '5000yuan.png', 'image', NULL, NULL, NULL, '0', 'admin', '2019-09-05 17:48:03', 'admin', '2019-09-05 17:48:03', NULL);
INSERT INTO `js_sys_file_upload` VALUES ('1169552106011394048', '1169552105889759232', '500yuan.png', 'image', NULL, NULL, NULL, '0', 'admin', '2019-09-05 18:05:26', 'admin', '2019-09-05 18:05:26', NULL);
INSERT INTO `js_sys_file_upload` VALUES ('1169552184063197184', '1169552183975116800', '3000yuan.png', 'image', NULL, NULL, NULL, '0', 'admin', '2019-09-05 18:05:45', 'admin', '2019-09-05 18:05:45', NULL);
INSERT INTO `js_sys_file_upload` VALUES ('1581831244792504320', '1581831243228028928', '173fc4ab10ddd9cdfd057e09d14ac71.jpg', 'image', NULL, NULL, NULL, '0', 'system', '2022-10-17 10:15:32', 'system', '2022-10-17 10:15:32', NULL);
INSERT INTO `js_sys_file_upload` VALUES ('1581831569087700992', '1581831243228028928', '173fc4ab10ddd9cdfd057e09d14ac71.jpg', 'file', NULL, NULL, NULL, '0', 'system', '2022-10-17 10:16:50', 'system', '2022-10-17 10:16:50', NULL);
INSERT INTO `js_sys_file_upload` VALUES ('1892111776792543233', '1892111776792543234', '微信截图_20250219115537.png', 'image', NULL, NULL, NULL, '0', 'system', '2025-02-19 15:19:37', 'system', '2025-02-19 15:19:37', NULL);
INSERT INTO `js_sys_file_upload` VALUES ('1892111977741647872', '1892111776792543234', '微信截图_20250219115537.png', 'image', NULL, NULL, NULL, '0', 'system', '2025-02-19 15:20:24', 'system', '2025-02-19 15:20:24', NULL);
INSERT INTO `js_sys_file_upload` VALUES ('1892112206339604480', '1892111776792543234', '微信截图_20250219115537.png', 'image', NULL, NULL, NULL, '0', 'system', '2025-02-19 15:21:18', 'system', '2025-02-19 15:21:18', NULL);

-- ----------------------------
-- Table structure for js_sys_job
-- ----------------------------
DROP TABLE IF EXISTS `js_sys_job`;
CREATE TABLE `js_sys_job`  (
  `job_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '任务名称',
  `job_group` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '任务组名',
  `description` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '任务描述',
  `invoke_target` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '调用目标字符串',
  `cron_expression` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'Cron执行表达式',
  `misfire_instruction` decimal(1, 0) NOT NULL COMMENT '计划执行错误策略',
  `concurrent` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '是否并发执行',
  `instance_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'JeeSiteScheduler' COMMENT '集群的实例名字',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '状态（0正常 1删除 2暂停）',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '创建者',
  `create_date` datetime NOT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '更新者',
  `update_date` datetime NOT NULL COMMENT '更新时间',
  `remarks` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '备注信息',
  PRIMARY KEY (`job_name`, `job_group`) USING BTREE,
  INDEX `idx_sys_job_status`(`status` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '作业调度表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of js_sys_job
-- ----------------------------
INSERT INTO `js_sys_job` VALUES ('MsgLocalMergePushTask', 'SYSTEM', '消息推送服务 (延迟推送)', 'msgLocalMergePushTask.execute()', '0 0/30 * * * ?', 2, '0', 'JeeSiteScheduler', '2', 'system', '2019-01-15 10:57:47', 'system', '2019-01-15 10:57:47', NULL);
INSERT INTO `js_sys_job` VALUES ('MsgLocalPushTask', 'SYSTEM', '消息推送服务 (实时推送)', 'msgLocalPushTask.execute()', '0/3 * * * * ?', 2, '0', 'JeeSiteScheduler', '2', 'system', '2019-01-15 10:57:47', 'system', '2019-01-15 10:57:47', NULL);

-- ----------------------------
-- Table structure for js_sys_job_log
-- ----------------------------
DROP TABLE IF EXISTS `js_sys_job_log`;
CREATE TABLE `js_sys_job_log`  (
  `id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '编号',
  `job_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '任务名称',
  `job_group` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '任务组名',
  `job_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '日志类型',
  `job_event` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '日志事件',
  `job_message` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '日志信息',
  `is_exception` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '是否异常',
  `exception_info` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '异常信息',
  `create_date` datetime NULL DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_sys_job_log_jn`(`job_name` ASC) USING BTREE,
  INDEX `idx_sys_job_log_jg`(`job_group` ASC) USING BTREE,
  INDEX `idx_sys_job_log_t`(`job_type` ASC) USING BTREE,
  INDEX `idx_sys_job_log_e`(`job_event` ASC) USING BTREE,
  INDEX `idx_sys_job_log_ie`(`is_exception` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '作业调度日志表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of js_sys_job_log
-- ----------------------------

-- ----------------------------
-- Table structure for js_sys_lang
-- ----------------------------
DROP TABLE IF EXISTS `js_sys_lang`;
CREATE TABLE `js_sys_lang`  (
  `id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '编号',
  `module_code` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '归属模块',
  `lang_code` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '语言编码',
  `lang_text` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '语言译文',
  `lang_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '语言类型',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '创建者',
  `create_date` datetime NOT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '更新者',
  `update_date` datetime NOT NULL COMMENT '更新时间',
  `remarks` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '备注信息',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_sys_lang_code`(`lang_code` ASC) USING BTREE,
  INDEX `idx_sys_lang_type`(`lang_type` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '国际化语言' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of js_sys_lang
-- ----------------------------

-- ----------------------------
-- Table structure for js_sys_log
-- ----------------------------
DROP TABLE IF EXISTS `js_sys_log`;
CREATE TABLE `js_sys_log`  (
  `id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '编号',
  `log_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '日志类型',
  `log_title` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '日志标题',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '创建者',
  `create_by_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '用户名称',
  `create_date` datetime NOT NULL COMMENT '创建时间',
  `request_uri` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '请求URI',
  `request_method` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '操作方式',
  `request_params` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '操作提交的数据',
  `diff_modify_data` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '新旧数据比较结果',
  `biz_key` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '业务主键',
  `biz_type` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '业务类型',
  `remote_addr` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '操作IP地址',
  `server_addr` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '请求服务器地址',
  `is_exception` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '是否异常',
  `exception_info` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '异常信息',
  `user_agent` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '用户代理',
  `device_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '设备名称/操作系统',
  `browser_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '浏览器名称',
  `execute_time` decimal(19, 0) NULL DEFAULT NULL COMMENT '执行时间',
  `corp_code` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '0' COMMENT '租户代码',
  `corp_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'JeeSite' COMMENT '租户名称',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_sys_log_cb`(`create_by` ASC) USING BTREE,
  INDEX `idx_sys_log_cc`(`corp_code` ASC) USING BTREE,
  INDEX `idx_sys_log_lt`(`log_type` ASC) USING BTREE,
  INDEX `idx_sys_log_bk`(`biz_key` ASC) USING BTREE,
  INDEX `idx_sys_log_bt`(`biz_type` ASC) USING BTREE,
  INDEX `idx_sys_log_ie`(`is_exception` ASC) USING BTREE,
  INDEX `idx_sys_log_cd`(`create_date` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '操作日志表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of js_sys_log
-- ----------------------------

-- ----------------------------
-- Table structure for js_sys_menu
-- ----------------------------
DROP TABLE IF EXISTS `js_sys_menu`;
CREATE TABLE `js_sys_menu`  (
  `menu_code` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '菜单编码',
  `parent_code` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '父级编号',
  `parent_codes` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '所有父级编号',
  `tree_sort` decimal(10, 0) NOT NULL COMMENT '本级排序号（升序）',
  `tree_sorts` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '所有级别排序号',
  `tree_leaf` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '是否最末级',
  `tree_level` decimal(4, 0) NOT NULL COMMENT '层次级别',
  `tree_names` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '全节点名',
  `menu_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '菜单名称',
  `menu_type` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '菜单类型（1菜单 2权限 3开发）',
  `menu_href` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '链接',
  `menu_target` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '目标',
  `menu_icon` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '图标',
  `menu_color` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '颜色',
  `menu_title` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '菜单标题',
  `permission` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '权限标识',
  `weight` decimal(4, 0) NULL DEFAULT NULL COMMENT '菜单权重',
  `is_show` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '是否显示（1显示 0隐藏）',
  `sys_code` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '归属系统（default:主导航菜单、mobileApp:APP菜单）',
  `module_codes` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '归属模块（多个用逗号隔开）',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '0' COMMENT '状态（0正常 1删除 2停用）',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '创建者',
  `create_date` datetime NOT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '更新者',
  `update_date` datetime NOT NULL COMMENT '更新时间',
  `remarks` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '备注信息',
  `extend_s1` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '扩展 String 1',
  `extend_s2` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '扩展 String 2',
  `extend_s3` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '扩展 String 3',
  `extend_s4` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '扩展 String 4',
  `extend_s5` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '扩展 String 5',
  `extend_s6` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '扩展 String 6',
  `extend_s7` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '扩展 String 7',
  `extend_s8` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '扩展 String 8',
  `extend_i1` decimal(19, 0) NULL DEFAULT NULL COMMENT '扩展 Integer 1',
  `extend_i2` decimal(19, 0) NULL DEFAULT NULL COMMENT '扩展 Integer 2',
  `extend_i3` decimal(19, 0) NULL DEFAULT NULL COMMENT '扩展 Integer 3',
  `extend_i4` decimal(19, 0) NULL DEFAULT NULL COMMENT '扩展 Integer 4',
  `extend_f1` decimal(19, 4) NULL DEFAULT NULL COMMENT '扩展 Float 1',
  `extend_f2` decimal(19, 4) NULL DEFAULT NULL COMMENT '扩展 Float 2',
  `extend_f3` decimal(19, 4) NULL DEFAULT NULL COMMENT '扩展 Float 3',
  `extend_f4` decimal(19, 4) NULL DEFAULT NULL COMMENT '扩展 Float 4',
  `extend_d1` datetime NULL DEFAULT NULL COMMENT '扩展 Date 1',
  `extend_d2` datetime NULL DEFAULT NULL COMMENT '扩展 Date 2',
  `extend_d3` datetime NULL DEFAULT NULL COMMENT '扩展 Date 3',
  `extend_d4` datetime NULL DEFAULT NULL COMMENT '扩展 Date 4',
  PRIMARY KEY (`menu_code`) USING BTREE,
  INDEX `idx_sys_menu_pc`(`parent_code` ASC) USING BTREE,
  INDEX `idx_sys_menu_ts`(`tree_sort` ASC) USING BTREE,
  INDEX `idx_sys_menu_status`(`status` ASC) USING BTREE,
  INDEX `idx_sys_menu_mt`(`menu_type` ASC) USING BTREE,
  INDEX `idx_sys_menu_sc`(`sys_code` ASC) USING BTREE,
  INDEX `idx_sys_menu_is`(`is_show` ASC) USING BTREE,
  INDEX `idx_sys_menu_mcs`(`module_codes` ASC) USING BTREE,
  INDEX `idx_sys_menu_wt`(`weight` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '菜单表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of js_sys_menu
-- ----------------------------
INSERT INTO `js_sys_menu` VALUES ('1085006669836128256', '0', '0,', 9900, '0000009900,', '0', 0, '系统管理', '系统管理', '1', '', '', 'icon-settings', '', NULL, '', 40, '1', 'default', 'core', '0', 'system', '2019-01-15 10:52:03', 'system', '2023-03-15 14:58:30', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `js_sys_menu` VALUES ('1085006670133923840', '1085006669836128256', '0,1085006669836128256,', 300, '0000009900,0000000300,', '0', 1, '系统管理/组织管理', '组织管理', '1', '', '', 'icon-grid', '', NULL, '', 40, '1', 'default', 'core', '0', 'system', '2019-01-15 10:52:03', 'system', '2023-03-15 14:58:30', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `js_sys_menu` VALUES ('1085006670385582080', '1085006670133923840', '0,1085006669836128256,1085006670133923840,', 40, '0000009900,0000000300,0000000040,', '0', 2, '系统管理/组织管理/用户管理', '用户管理', '1', '/sys/empUser/index', '', 'icon-user', '', NULL, '', 40, '1', 'default', 'core', '0', 'system', '2019-01-15 10:52:03', 'system', '2023-03-15 14:58:30', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `js_sys_menu` VALUES ('1085006670624657408', '1085006670385582080', '0,1085006669836128256,1085006670133923840,1085006670385582080,', 30, '0000009900,0000000300,0000000040,0000000030,', '1', 3, '系统管理/组织管理/用户管理/查看', '查看', '2', '', '', '', '', NULL, 'sys:empUser:view', 40, '1', 'default', 'core', '0', 'system', '2019-01-15 10:52:03', 'system', '2023-03-15 14:58:30', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `js_sys_menu` VALUES ('1085006670884704256', '1085006670385582080', '0,1085006669836128256,1085006670133923840,1085006670385582080,', 40, '0000009900,0000000300,0000000040,0000000040,', '1', 3, '系统管理/组织管理/用户管理/编辑', '编辑', '2', '', '', '', '', NULL, 'sys:empUser:edit', 40, '1', 'default', 'core', '0', 'system', '2019-01-15 10:52:03', 'system', '2023-03-15 14:58:30', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `js_sys_menu` VALUES ('1085006671182499840', '1085006670385582080', '0,1085006669836128256,1085006670133923840,1085006670385582080,', 60, '0000009900,0000000300,0000000040,0000000060,', '1', 3, '系统管理/组织管理/用户管理/分配角色', '分配角色', '2', '', '', '', '', NULL, 'sys:empUser:authRole', 40, '1', 'default', 'core', '0', 'system', '2019-01-15 10:52:04', 'system', '2023-03-15 14:58:30', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `js_sys_menu` VALUES ('1085006671392215040', '1085006670385582080', '0,1085006669836128256,1085006670133923840,1085006670385582080,', 50, '0000009900,0000000300,0000000040,0000000050,', '1', 3, '系统管理/组织管理/用户管理/分配数据', '分配数据', '2', '', '', '', '', NULL, 'sys:empUser:authDataScope', 40, '1', 'default', 'core', '0', 'system', '2019-01-15 10:52:04', 'system', '2023-03-15 14:58:30', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `js_sys_menu` VALUES ('1085006671614513152', '1085006670385582080', '0,1085006669836128256,1085006670133923840,1085006670385582080,', 60, '0000009900,0000000300,0000000040,0000000060,', '1', 3, '系统管理/组织管理/用户管理/停用启用', '停用启用', '2', '', '', '', '', NULL, 'sys:empUser:updateStatus', 40, '1', 'default', 'core', '0', 'system', '2019-01-15 10:52:04', 'system', '2023-03-15 14:58:30', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `js_sys_menu` VALUES ('1085006671807451136', '1085006670385582080', '0,1085006669836128256,1085006670133923840,1085006670385582080,', 70, '0000009900,0000000300,0000000040,0000000070,', '1', 3, '系统管理/组织管理/用户管理/重置密码', '重置密码', '2', '', '', '', '', NULL, 'sys:empUser:resetpwd', 40, '1', 'default', 'core', '0', 'system', '2019-01-15 10:52:04', 'system', '2023-03-15 14:58:30', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `js_sys_menu` VALUES ('1085006671996194816', '1085006670133923840', '0,1085006669836128256,1085006670133923840,', 50, '0000009900,0000000300,0000000050,', '0', 2, '系统管理/组织管理/机构管理', '机构管理', '1', '/sys/office/list', '', 'icon-grid', '', NULL, '', 60, '1', 'default', 'core', '0', 'system', '2019-01-15 10:52:04', 'system', '2023-03-15 14:58:30', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `js_sys_menu` VALUES ('1085006672700837888', '1085006671996194816', '0,1085006669836128256,1085006670133923840,1085006671996194816,', 30, '0000009900,0000000300,0000000050,0000000030,', '1', 3, '系统管理/组织管理/机构管理/查看', '查看', '2', '', '', '', '', NULL, 'sys:office:view', 60, '1', 'default', 'core', '0', 'system', '2019-01-15 10:52:04', 'system', '2023-03-15 14:58:30', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `js_sys_menu` VALUES ('1085006672902164480', '1085006671996194816', '0,1085006669836128256,1085006670133923840,1085006671996194816,', 40, '0000009900,0000000300,0000000050,0000000040,', '1', 3, '系统管理/组织管理/机构管理/编辑', '编辑', '2', '', '', '', '', NULL, 'sys:office:edit', 60, '1', 'default', 'core', '0', 'system', '2019-01-15 10:52:04', 'system', '2023-03-15 14:58:30', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `js_sys_menu` VALUES ('1085006673103491072', '1085006670133923840', '0,1085006669836128256,1085006670133923840,', 70, '0000009900,0000000300,0000000070,', '0', 2, '系统管理/组织管理/公司管理', '公司管理', '1', '/sys/company/list', '', 'icon-fire', '', NULL, '', 60, '1', 'default', 'core', '0', 'system', '2019-01-15 10:52:04', 'system', '2023-03-15 14:58:30', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `js_sys_menu` VALUES ('1085006673292234752', '1085006673103491072', '0,1085006669836128256,1085006670133923840,1085006673103491072,', 30, '0000009900,0000000300,0000000070,0000000030,', '1', 3, '系统管理/组织管理/公司管理/查看', '查看', '2', '', '', '', '', NULL, 'sys:company:view', 60, '1', 'default', 'core', '0', 'system', '2019-01-15 10:52:04', 'system', '2023-03-15 14:58:30', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `js_sys_menu` VALUES ('1085006673493561344', '1085006673103491072', '0,1085006669836128256,1085006670133923840,1085006673103491072,', 40, '0000009900,0000000300,0000000070,0000000040,', '1', 3, '系统管理/组织管理/公司管理/编辑', '编辑', '2', '', '', '', '', NULL, 'sys:company:edit', 60, '1', 'default', 'core', '0', 'system', '2019-01-15 10:52:04', 'system', '2023-03-15 14:58:30', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `js_sys_menu` VALUES ('1085006673707470848', '1085006670133923840', '0,1085006669836128256,1085006670133923840,', 70, '0000009900,0000000300,0000000070,', '0', 2, '系统管理/组织管理/岗位管理', '岗位管理', '1', '/sys/post/list', '', 'icon-trophy', '', NULL, '', 60, '1', 'default', 'core', '0', 'system', '2019-01-15 10:52:04', 'system', '2023-03-15 14:58:30', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `js_sys_menu` VALUES ('1085006673892020224', '1085006673707470848', '0,1085006669836128256,1085006670133923840,1085006673707470848,', 30, '0000009900,0000000300,0000000070,0000000030,', '1', 3, '系统管理/组织管理/岗位管理/查看', '查看', '2', '', '', '', '', NULL, 'sys:post:view', 60, '1', 'default', 'core', '0', 'system', '2019-01-15 10:52:04', 'system', '2023-03-15 14:58:30', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `js_sys_menu` VALUES ('1085006674399531008', '1085006673707470848', '0,1085006669836128256,1085006670133923840,1085006673707470848,', 40, '0000009900,0000000300,0000000070,0000000040,', '1', 3, '系统管理/组织管理/岗位管理/编辑', '编辑', '2', '', '', '', '', NULL, 'sys:post:edit', 60, '1', 'default', 'core', '0', 'system', '2019-01-15 10:52:04', 'system', '2023-03-15 14:58:30', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `js_sys_menu` VALUES ('1085006674600857600', '1085006669836128256', '0,1085006669836128256,', 400, '0000009900,0000000400,', '0', 1, '系统管理/权限管理', '权限管理', '1', '', '', 'icon-social-dropbox', '', NULL, '', 60, '1', 'default', 'core', '0', 'system', '2019-01-15 10:52:04', 'system', '2023-03-15 14:58:30', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `js_sys_menu` VALUES ('1085006674789601280', '1085006674600857600', '0,1085006669836128256,1085006674600857600,', 30, '0000009900,0000000400,0000000030,', '1', 2, '系统管理/权限管理/角色管理', '角色管理', '1', '/sys/role/list', '', 'icon-people', '', NULL, 'sys:role', 60, '1', 'default', 'core', '0', 'system', '2019-01-15 10:52:04', 'system', '2023-03-15 14:58:30', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `js_sys_menu` VALUES ('1085006675007705088', '1085006674600857600', '0,1085006669836128256,1085006674600857600,', 40, '0000009900,0000000400,0000000040,', '1', 2, '系统管理/权限管理/二级管理员', '二级管理员', '1', '/sys/secAdmin/list', '', 'icon-user-female', '', NULL, 'sys:secAdmin', 60, '1', 'default', 'core', '0', 'system', '2019-01-15 10:52:04', 'system', '2023-03-15 14:58:30', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `js_sys_menu` VALUES ('1085006675200643072', '1085006674600857600', '0,1085006669836128256,1085006674600857600,', 50, '0000009900,0000000400,0000000050,', '1', 2, '系统管理/权限管理/系统管理员', '系统管理员', '1', '/sys/corpAdmin/list', '', 'icon-badge', '', NULL, 'sys:corpAdmin', 80, '1', 'default', 'core', '0', 'system', '2019-01-15 10:52:04', 'system', '2023-03-15 14:58:30', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `js_sys_menu` VALUES ('1085006675531993088', '1085006669836128256', '0,1085006669836128256,', 500, '0000009900,0000000500,', '0', 1, '系统管理/系统设置', '系统设置', '1', '', '', 'icon-settings', '', NULL, '', 60, '1', 'default', 'core', '0', 'system', '2019-01-15 10:52:05', 'system', '2023-03-15 14:58:30', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `js_sys_menu` VALUES ('1085006675980783616', '1085006675531993088', '0,1085006669836128256,1085006675531993088,', 30, '0000009900,0000000500,0000000030,', '1', 2, '系统管理/系统设置/菜单管理', '菜单管理', '1', '/sys/menu/list', '', 'icon-book-open', '', NULL, 'sys:menu', 80, '1', 'default', 'core', '0', 'system', '2019-01-15 10:52:05', 'system', '2023-03-15 14:58:30', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `js_sys_menu` VALUES ('1085006676261801984', '1085006675531993088', '0,1085006669836128256,1085006675531993088,', 40, '0000009900,0000000500,0000000040,', '1', 2, '系统管理/系统设置/模块管理', '模块管理', '1', '/sys/module/list', '', 'icon-grid', '', NULL, 'sys:module', 80, '1', 'default', 'core', '0', 'system', '2019-01-15 10:52:05', 'system', '2023-03-15 14:58:30', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `js_sys_menu` VALUES ('1085006676458934272', '1085006675531993088', '0,1085006669836128256,1085006675531993088,', 50, '0000009900,0000000500,0000000050,', '1', 2, '系统管理/系统设置/参数设置', '参数设置', '1', '/sys/config/list', '', 'icon-wrench', '', NULL, 'sys:config', 60, '1', 'default', 'core', '0', 'system', '2019-01-15 10:52:05', 'system', '2023-03-15 14:58:30', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `js_sys_menu` VALUES ('1085006676647677952', '1085006675531993088', '0,1085006669836128256,1085006675531993088,', 60, '0000009900,0000000500,0000000060,', '0', 2, '系统管理/系统设置/字典管理', '字典管理', '1', '/sys/dictType/list', '', 'icon-social-dropbox', '', NULL, '', 60, '1', 'default', 'core', '0', 'system', '2019-01-15 10:52:05', 'system', '2023-03-15 14:58:30', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `js_sys_menu` VALUES ('1085006676849004544', '1085006676647677952', '0,1085006669836128256,1085006675531993088,1085006676647677952,', 30, '0000009900,0000000500,0000000060,0000000030,', '1', 3, '系统管理/系统设置/字典管理/类型查看', '类型查看', '2', '', '', '', '', NULL, 'sys:dictType:view', 60, '1', 'default', 'core', '0', 'system', '2019-01-15 10:52:05', 'system', '2023-03-15 14:58:30', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `js_sys_menu` VALUES ('1085006677033553920', '1085006676647677952', '0,1085006669836128256,1085006675531993088,1085006676647677952,', 40, '0000009900,0000000500,0000000060,0000000040,', '1', 3, '系统管理/系统设置/字典管理/类型编辑', '类型编辑', '2', '', '', '', '', NULL, 'sys:dictType:edit', 80, '1', 'default', 'core', '0', 'system', '2019-01-15 10:52:05', 'system', '2023-03-15 14:58:30', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `js_sys_menu` VALUES ('1085006677754974208', '1085006676647677952', '0,1085006669836128256,1085006675531993088,1085006676647677952,', 50, '0000009900,0000000500,0000000060,0000000050,', '1', 3, '系统管理/系统设置/字典管理/数据查看', '数据查看', '2', '', '', '', '', NULL, 'sys:dictData:view', 60, '1', 'default', 'core', '0', 'system', '2019-01-15 10:52:05', 'system', '2023-03-15 14:58:30', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `js_sys_menu` VALUES ('1085006678052769792', '1085006676647677952', '0,1085006669836128256,1085006675531993088,1085006676647677952,', 60, '0000009900,0000000500,0000000060,0000000060,', '1', 3, '系统管理/系统设置/字典管理/数据编辑', '数据编辑', '2', '', '', '', '', NULL, 'sys:dictData:edit', 60, '1', 'default', 'core', '0', 'system', '2019-01-15 10:52:05', 'system', '2023-03-15 14:58:30', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `js_sys_menu` VALUES ('1085006678245707776', '1085006675531993088', '0,1085006669836128256,1085006675531993088,', 70, '0000009900,0000000500,0000000070,', '1', 2, '系统管理/系统设置/行政区划', '行政区划', '1', '/sys/area/list', '', 'icon-map', '', NULL, 'sys:area', 60, '1', 'default', 'core', '0', 'system', '2019-01-15 10:52:05', 'system', '2023-03-15 14:58:30', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `js_sys_menu` VALUES ('1085006678455422976', '1085006675531993088', '0,1085006669836128256,1085006675531993088,', 80, '0000009900,0000000500,0000000080,', '1', 2, '系统管理/系统设置/国际化管理', '国际化管理', '1', '/sys/lang/list', '', 'icon-globe', '', NULL, 'sys:lang', 80, '1', 'default', 'core', '0', 'system', '2019-01-15 10:52:05', 'system', '2023-03-15 14:58:30', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `js_sys_menu` VALUES ('1085006697925382144', '1085006675531993088', '0,1085006669836128256,1085006675531993088,', 90, '0000009900,0000000500,0000000090,', '1', 2, '系统管理/系统设置/产品许可信息', '产品许可信息', '1', '//licence', '', 'icon-paper-plane', '', NULL, '', 80, '1', 'default', 'core', '0', 'system', '2019-01-15 10:52:11', 'system', '2023-03-15 14:58:30', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `js_sys_menu` VALUES ('1085006720784338944', '1085006669836128256', '0,1085006669836128256,', 600, '0000009900,0000000600,', '0', 1, '系统管理/系统监控', '系统监控', '1', '', '', 'icon-ghost', '', NULL, '', 60, '1', 'default', 'core', '0', 'system', '2019-01-15 10:52:18', 'system', '2023-03-15 14:58:30', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `js_sys_menu` VALUES ('1085006746340233216', '1085006720784338944', '0,1085006669836128256,1085006720784338944,', 40, '0000009900,0000000600,0000000040,', '1', 2, '系统管理/系统监控/访问日志', '访问日志', '1', '/sys/log/list', '', 'fa fa-bug', '', NULL, 'sys:log', 60, '1', 'default', 'core', '0', 'system', '2019-01-15 10:52:23', 'system', '2023-03-15 14:58:30', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `js_sys_menu` VALUES ('1085006767844429824', '1085006720784338944', '0,1085006669836128256,1085006720784338944,', 50, '0000009900,0000000600,0000000050,', '1', 2, '系统管理/系统监控/数据监控', '数据监控', '1', '//druid', '', 'icon-disc', '', NULL, 'sys:state:druid', 80, '1', 'default', 'core', '0', 'system', '2019-01-15 10:52:29', 'system', '2023-03-15 14:58:30', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `js_sys_menu` VALUES ('1085006795128377344', '1085006720784338944', '0,1085006669836128256,1085006720784338944,', 60, '0000009900,0000000600,0000000060,', '1', 2, '系统管理/系统监控/缓存监控', '缓存监控', '1', '/state/cache/index', '', 'icon-social-dribbble', '', NULL, 'sys:stste:cache', 80, '1', 'default', 'core', '0', 'system', '2019-01-15 10:52:34', 'system', '2023-03-15 14:58:30', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `js_sys_menu` VALUES ('1085006819660861440', '1085006720784338944', '0,1085006669836128256,1085006720784338944,', 70, '0000009900,0000000600,0000000070,', '1', 2, '系统管理/系统监控/服务器监控', '服务器监控', '1', '/state/server/index', '', 'icon-speedometer', '', NULL, 'sys:state:server', 80, '1', 'default', 'core', '0', 'system', '2019-01-15 10:52:40', 'system', '2023-03-15 14:58:30', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `js_sys_menu` VALUES ('1085006843853606912', '1085006720784338944', '0,1085006669836128256,1085006720784338944,', 80, '0000009900,0000000600,0000000080,', '1', 2, '系统管理/系统监控/作业监控', '作业监控', '1', '/job/list', '', 'icon-notebook', '', NULL, 'sys:job', 80, '1', 'default', 'core', '0', 'system', '2019-01-15 10:52:45', 'system', '2023-03-15 14:58:30', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `js_sys_menu` VALUES ('1085006867215880192', '1085006720784338944', '0,1085006669836128256,1085006720784338944,', 90, '0000009900,0000000600,0000000090,', '1', 2, '系统管理/系统监控/在线用户', '在线用户', '1', '/sys/online/list', '', 'icon-social-twitter', '', NULL, 'sys:online', 60, '1', 'default', 'core', '0', 'system', '2019-01-15 10:52:51', 'system', '2023-03-15 14:58:30', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `js_sys_menu` VALUES ('1085006889932230656', '1085006720784338944', '0,1085006669836128256,1085006720784338944,', 100, '0000009900,0000000600,0000000100,', '1', 2, '系统管理/系统监控/在线文档', '在线文档', '1', '//swagger-ui.html', '', 'icon-book-open', '', NULL, 'sys:swagger', 80, '1', 'default', 'core', '0', 'system', '2019-01-15 10:52:56', 'system', '2023-03-15 14:58:30', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `js_sys_menu` VALUES ('1085006912174624768', '1085006669836128256', '0,1085006669836128256,', 700, '0000009900,0000000700,', '0', 1, '系统管理/消息推送', '消息推送', '1', '', '', 'icon-envelope-letter', '', NULL, '', 60, '1', 'default', 'core', '0', 'system', '2019-01-15 10:53:02', 'system', '2023-03-15 14:58:30', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `js_sys_menu` VALUES ('1085006934953889792', '1085006912174624768', '0,1085006669836128256,1085006912174624768,', 30, '0000009900,0000000700,0000000030,', '1', 2, '系统管理/消息推送/未完成消息', '未完成消息', '1', '/msg/msgPush/list', '', '', '', NULL, 'msg:msgPush', 60, '1', 'default', 'core', '0', 'system', '2019-01-15 10:53:07', 'system', '2023-03-15 14:58:30', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `js_sys_menu` VALUES ('1085006959515734016', '1085006912174624768', '0,1085006669836128256,1085006912174624768,', 40, '0000009900,0000000700,0000000040,', '1', 2, '系统管理/消息推送/已完成消息', '已完成消息', '1', '/msg/msgPush/list?pushed=true', '', '', '', NULL, 'msg:msgPush', 60, '1', 'default', 'core', '0', 'system', '2019-01-15 10:53:14', 'system', '2023-03-15 14:58:30', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `js_sys_menu` VALUES ('1085006987131031552', '1085006912174624768', '0,1085006669836128256,1085006912174624768,', 50, '0000009900,0000000700,0000000050,', '1', 2, '系统管理/消息推送/消息模板管理', '消息模板管理', '1', '/msg/msgTemplate/list', '', '', '', NULL, 'msg:msgTemplate', 60, '1', 'default', 'core', '0', 'system', '2019-01-15 10:53:20', 'system', '2023-03-15 14:58:30', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `js_sys_menu` VALUES ('1085007011915173888', '1085006669836128256', '0,1085006669836128256,', 900, '0000009900,0000000900,', '0', 1, '系统管理/研发工具', '研发工具', '1', '', '', 'fa fa-code', '', NULL, '', 80, '1', 'default', 'core', '0', 'system', '2019-01-15 10:53:26', 'system', '2023-03-15 14:58:30', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `js_sys_menu` VALUES ('1085007042764279808', '1085007011915173888', '0,1085006669836128256,1085007011915173888,', 30, '0000009900,0000000900,0000000030,', '1', 2, '系统管理/研发工具/代码生成工具', '代码生成工具', '1', '/gen/genTable/list', '', 'fa fa-code', '', NULL, 'gen:genTable', 80, '1', 'default', 'core', '0', 'system', '2019-01-15 10:53:33', 'system', '2023-03-15 14:58:30', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `js_sys_menu` VALUES ('1085007068001406976', '1085007011915173888', '0,1085006669836128256,1085007011915173888,', 100, '0000009900,0000000900,0000000100,', '0', 2, '系统管理/研发工具/代码生成实例', '代码生成实例', '1', '', '', 'icon-social-dropbox', '', NULL, '', 80, '1', 'default', 'core', '0', 'system', '2019-01-15 10:53:40', 'system', '2023-03-15 14:58:30', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `js_sys_menu` VALUES ('1085007096610754560', '1085007068001406976', '0,1085006669836128256,1085007011915173888,1085007068001406976,', 30, '0000009900,0000000900,0000000100,0000000030,', '1', 3, '系统管理/研发工具/代码生成实例/单表_主子表', '单表/主子表', '1', '/test/testData/list', '', '', '', NULL, 'test:testData', 80, '1', 'default', 'core', '0', 'system', '2019-01-15 10:53:46', 'system', '2023-03-15 14:58:30', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `js_sys_menu` VALUES ('1085007121784967168', '1085007068001406976', '0,1085006669836128256,1085007011915173888,1085007068001406976,', 40, '0000009900,0000000900,0000000100,0000000040,', '1', 3, '系统管理/研发工具/代码生成实例/树表_树结构表', '树表/树结构表', '1', '/test/testTree/list', '', '', '', NULL, 'test:testTree', 80, '1', 'default', 'core', '0', 'system', '2019-01-15 10:53:53', 'system', '2023-03-15 14:58:30', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `js_sys_menu` VALUES ('1085007151791017984', '1085007011915173888', '0,1085006669836128256,1085007011915173888,', 200, '0000009900,0000000900,0000000200,', '0', 2, '系统管理/研发工具/数据表格实例', '数据表格实例', '1', '', '', '', '', NULL, '', 80, '1', 'default', 'core', '0', 'system', '2019-01-15 10:54:00', 'system', '2023-03-15 14:58:30', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `js_sys_menu` VALUES ('1085007179246931968', '1085007151791017984', '0,1085006669836128256,1085007011915173888,1085007151791017984,', 30, '0000009900,0000000900,0000000200,0000000030,', '1', 3, '系统管理/研发工具/数据表格实例/多表头分组小计合计', '多表头分组小计合计', '1', '/demo/dataGrid/groupGrid', '', '', '', NULL, '', 80, '1', 'default', 'core', '0', 'system', '2019-01-15 10:54:06', 'system', '2023-03-15 14:58:30', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `js_sys_menu` VALUES ('1085007204857352192', '1085007151791017984', '0,1085006669836128256,1085007011915173888,1085007151791017984,', 40, '0000009900,0000000900,0000000200,0000000040,', '1', 3, '系统管理/研发工具/数据表格实例/编辑表格多行编辑', '编辑表格多行编辑', '1', '/demo/dataGrid/editGrid', '', '', '', NULL, '', 80, '1', 'default', 'core', '0', 'system', '2019-01-15 10:54:13', 'system', '2023-03-15 14:58:30', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `js_sys_menu` VALUES ('1085007233798049792', '1085007011915173888', '0,1085006669836128256,1085007011915173888,', 300, '0000009900,0000000900,0000000300,', '0', 2, '系统管理/研发工具/表单组件实例', '表单组件实例', '1', '', '', '', '', NULL, '', 80, '1', 'default', 'core', '0', 'system', '2019-01-15 10:54:19', 'system', '2023-03-15 14:58:30', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `js_sys_menu` VALUES ('1085007265490210816', '1085007233798049792', '0,1085006669836128256,1085007011915173888,1085007233798049792,', 30, '0000009900,0000000900,0000000300,0000000030,', '1', 3, '系统管理/研发工具/表单组件实例/组件应用实例', '组件应用实例', '1', '/demo/form/editForm', '', '', '', NULL, '', 80, '1', 'default', 'core', '0', 'system', '2019-01-15 10:54:28', 'system', '2023-03-15 14:58:30', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `js_sys_menu` VALUES ('1085007301200515072', '1085007233798049792', '0,1085006669836128256,1085007011915173888,1085007233798049792,', 40, '0000009900,0000000900,0000000300,0000000040,', '1', 3, '系统管理/研发工具/表单组件实例/栅格布局实例', '栅格布局实例', '1', '/demo/form/layoutForm', '', '', '', NULL, '', 80, '1', 'default', 'core', '0', 'system', '2019-01-15 10:54:34', 'system', '2023-03-15 14:58:30', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `js_sys_menu` VALUES ('1085007331072348160', '1085007233798049792', '0,1085006669836128256,1085007011915173888,1085007233798049792,', 50, '0000009900,0000000900,0000000300,0000000050,', '1', 3, '系统管理/研发工具/表单组件实例/表格表单实例', '表格表单实例', '1', '/demo/form/tableForm', '', '', '', NULL, '', 80, '1', 'default', 'core', '0', 'system', '2019-01-15 10:54:43', 'system', '2023-03-15 14:58:30', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `js_sys_menu` VALUES ('1085007369139851264', '1085007011915173888', '0,1085006669836128256,1085007011915173888,', 400, '0000009900,0000000900,0000000400,', '0', 2, '系统管理/研发工具/前端界面实例', '前端界面实例', '1', '', '', '', '', NULL, '', 80, '1', 'default', 'core', '0', 'system', '2019-01-15 10:54:51', 'system', '2023-03-15 14:58:30', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `js_sys_menu` VALUES ('1085007404120346624', '1085007369139851264', '0,1085006669836128256,1085007011915173888,1085007369139851264,', 30, '0000009900,0000000900,0000000400,0000000030,', '1', 3, '系统管理/研发工具/前端界面实例/图标样式查找', '图标样式查找', '1', '//tags/iconselect', '', '', '', NULL, '', 80, '1', 'default', 'core', '0', 'system', '2019-01-15 10:55:00', 'system', '2023-03-15 14:58:30', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `js_sys_menu` VALUES ('1085007438425559040', '1085006669836128256', '0,1085006669836128256,', 999, '0000009900,0000000999,', '0', 1, '系统管理/JeeSite社区', 'JeeSite社区', '1', '', '', 'fa fa-code', '', NULL, '', 80, '1', 'default', 'core', '0', 'system', '2019-01-15 10:55:09', 'system', '2023-03-15 14:58:30', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `js_sys_menu` VALUES ('1085007482444779520', '1085007438425559040', '0,1085006669836128256,1085007438425559040,', 30, '0000009900,0000000999,0000000030,', '1', 2, '系统管理/JeeSite社区/官方网站', '官方网站', '1', 'http://jeesite.com', '_blank', '', '', NULL, '', 80, '1', 'default', 'core', '0', 'system', '2019-01-15 10:55:18', 'system', '2023-03-15 14:58:30', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `js_sys_menu` VALUES ('1085007518306078720', '1085007438425559040', '0,1085006669836128256,1085007438425559040,', 50, '0000009900,0000000999,0000000050,', '1', 2, '系统管理/JeeSite社区/作者博客', '作者博客', '1', 'https://my.oschina.net/thinkgem', '_blank', '', '', NULL, '', 80, '1', 'default', 'core', '0', 'system', '2019-01-15 10:55:27', 'system', '2023-03-15 14:58:30', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `js_sys_menu` VALUES ('1085007558101635072', '1085007438425559040', '0,1085006669836128256,1085007438425559040,', 40, '0000009900,0000000999,0000000040,', '1', 2, '系统管理/JeeSite社区/问题反馈', '问题反馈', '1', 'https://gitee.com/thinkgem/jeesite4/issues', '_blank', '', '', NULL, '', 80, '1', 'default', 'core', '0', 'system', '2019-01-15 10:55:37', 'system', '2023-03-15 14:58:30', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `js_sys_menu` VALUES ('1085007591425380352', '1085007438425559040', '0,1085006669836128256,1085007438425559040,', 60, '0000009900,0000000999,0000000060,', '1', 2, '系统管理/JeeSite社区/开源社区', '开源社区', '1', 'http://jeesite.net', '_blank', '', '', NULL, '', 80, '1', 'default', 'core', '0', 'system', '2019-01-15 10:55:44', 'system', '2023-03-15 14:58:30', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `js_sys_menu` VALUES ('1085011080834879488', '0', '0,', 9050, '0000009050,', '0', 0, '游戏管理', '游戏管理', '1', '', '', 'fa fa-gears', '', NULL, '', 40, '1', 'default', 'game', '0', 'system', '2019-01-15 11:09:35', 'system', '2023-03-15 14:58:29', '', '', '', '', '', '', '', '', '', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `js_sys_menu` VALUES ('1085011519332585472', '1085011080834879488', '0,1085011080834879488,', 80, '0000009050,0000000080,', '0', 1, '游戏管理/版本信息', '版本信息', '1', '/osee/game/version', '', 'fa fa-info-circle', '', NULL, '', 40, '1', 'default', 'game', '0', 'system', '2019-01-15 11:11:19', 'system', '2023-03-15 14:58:29', '', '', '', '', '', '', '', '', '', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `js_sys_menu` VALUES ('1085011892306874368', '1085011519332585472', '0,1085011080834879488,1085011519332585472,', 30, '0000009050,0000000080,0000000030,', '1', 2, '游戏管理/版本信息/查看', '查看', '2', '', '', '', '', NULL, 'game:version:view', 40, '0', 'default', 'game', '0', 'system', '2019-01-15 11:12:48', 'system', '2023-03-15 14:58:29', '', '', '', '', '', '', '', '', '', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `js_sys_menu` VALUES ('1085012076529094656', '1085011519332585472', '0,1085011080834879488,1085011519332585472,', 60, '0000009050,0000000080,0000000060,', '1', 2, '游戏管理/版本信息/编辑', '编辑', '2', '', '', '', '', NULL, 'game:version:edit', 40, '0', 'default', 'game', '0', 'system', '2019-01-15 11:13:32', 'system', '2023-03-15 14:58:29', '', '', '', '', '', '', '', '', '', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `js_sys_menu` VALUES ('1085019431499788288', '1085011080834879488', '0,1085011080834879488,', 60, '0000009050,0000000060,', '0', 1, '游戏管理/游走字幕', '游走字幕', '1', '/osee/game/subtitle', '', 'fa fa-font', '', NULL, '', 40, '1', 'default', 'game', '0', 'system', '2019-01-15 11:42:46', 'system', '2023-03-15 14:58:29', '', '', '', '', '', '', '', '', '', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `js_sys_menu` VALUES ('1085020021323788288', '1085011080834879488', '0,1085011080834879488,', 70, '0000009050,0000000070,', '0', 1, '游戏管理/游戏公告', '游戏公告', '1', '/osee/game/notice', '', 'fa fa-bullhorn', '', NULL, '', 40, '1', 'default', 'game', '0', 'system', '2019-01-15 11:45:06', 'system', '2023-03-15 14:58:29', '', '', '', '', '', '', '', '', '', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `js_sys_menu` VALUES ('1085021295159078912', '0', '0,', 9010, '0000009010,', '0', 0, '用户管理', '用户管理', '1', '', '', 'fa fa-user', '', NULL, '', 40, '1', 'default', 'player', '0', 'system', '2019-01-15 11:50:10', 'system', '2023-03-15 14:58:30', '', '', '', '', '', '', '', '', '', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `js_sys_menu` VALUES ('1085021470879444992', '1085021295159078912', '0,1085021295159078912,', 30, '0000009010,0000000030,', '0', 1, '用户管理/用户列表', '用户列表', '1', '/osee/player/list', '', 'fa fa-users', '', NULL, '', 40, '1', 'default', 'player', '0', 'system', '2019-01-15 11:50:52', 'system', '2023-03-15 14:58:30', '', '', '', '', '', '', '', '', '', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `js_sys_menu` VALUES ('1085357426347704320', '1085019431499788288', '0,1085011080834879488,1085019431499788288,', 30, '0000009050,0000000060,0000000030,', '1', 2, '游戏管理/游走字幕/查看', '查看', '2', '', '', '', '', NULL, 'game:subtitle:view', 40, '0', 'default', 'game', '0', 'system', '2019-01-16 10:05:50', 'system', '2023-03-15 14:58:29', '', '', '', '', '', '', '', '', '', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `js_sys_menu` VALUES ('1085357470249484288', '1085019431499788288', '0,1085011080834879488,1085019431499788288,', 60, '0000009050,0000000060,0000000060,', '1', 2, '游戏管理/游走字幕/编辑', '编辑', '2', '', '', '', '', NULL, 'game:subtitle:edit', 40, '0', 'default', 'game', '0', 'system', '2019-01-16 10:06:01', 'system', '2023-03-15 14:58:29', '', '', '', '', '', '', '', '', '', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `js_sys_menu` VALUES ('1085402262577029120', '1085020021323788288', '0,1085011080834879488,1085020021323788288,', 30, '0000009050,0000000070,0000000030,', '1', 2, '游戏管理/游戏公告/查看', '查看', '2', '', '', '', '', NULL, 'game:notice:view', 40, '0', 'default', 'game', '0', 'system', '2019-01-16 13:04:00', 'system', '2023-03-15 14:58:29', '', '', '', '', '', '', '', '', '', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `js_sys_menu` VALUES ('1085402363340988416', '1085020021323788288', '0,1085011080834879488,1085020021323788288,', 60, '0000009050,0000000070,0000000060,', '1', 2, '游戏管理/游戏公告/编辑', '编辑', '2', '', '', '', '', NULL, 'game:notice:edit', 40, '0', 'default', 'game', '0', 'system', '2019-01-16 13:04:24', 'system', '2023-03-15 14:58:29', '', '', '', '', '', '', '', '', '', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `js_sys_menu` VALUES ('1086156125624123392', '1085021470879444992', '0,1085021295159078912,1085021470879444992,', 30, '0000009010,0000000030,0000000030,', '1', 2, '用户管理/用户列表/查看', '查看', '2', '', '', '', '', NULL, 'player:view', 40, '0', 'default', 'player', '0', 'system', '2019-01-18 14:59:35', 'system', '2023-03-15 14:58:30', '', '', '', '', '', '', '', '', '', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `js_sys_menu` VALUES ('1086156201570385920', '1085021470879444992', '0,1085021295159078912,1085021470879444992,', 60, '0000009010,0000000030,0000000060,', '1', 2, '用户管理/用户列表/编辑', '编辑', '2', '', '', '', '', NULL, 'player:edit', 40, '0', 'default', 'player', '0', 'system', '2019-01-18 14:59:53', 'system', '2023-03-15 14:58:30', '', '', '', '', '', '', '', '', '', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `js_sys_menu` VALUES ('1105443204287991808', '0', '0,', 9030, '0000009030,', '1', 0, '站内消息', '站内消息', '1', '/msg/msgInner/list', '', 'icon-speech', '', NULL, 'msg:msgInner', 40, '1', 'default', 'core', '0', 'system', '2023-05-23 11:33:49', 'system', '2023-05-23 11:33:49', '', '', '', '', '', '', '', '', '', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `js_sys_menu` VALUES ('1448124255815577600', '1085011080834879488', '0,1085011080834879488,', 180, '0000009050,0000000180,', '0', 1, '游戏管理/反馈列表', '反馈列表', '1', '/osee/game/feedBack', '', 'fa fa-reorder', '', NULL, '', 40, '1', 'default', 'game', '0', 'system', '2021-10-13 11:11:42', 'system', '2023-03-15 14:58:29', '', '', '', '', '', '', '', '', '', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `js_sys_menu` VALUES ('1448127547568271360', '1448124255815577600', '0,1085011080834879488,1448124255815577600,', 30, '0000009050,0000000180,0000000030,', '1', 2, '游戏管理/反馈列表/查看', '查看', '2', '', '', '', '', NULL, 'game:feedBack:view', 40, '1', 'default', 'game', '0', 'system', '2021-10-13 11:24:47', 'system', '2023-03-15 14:58:29', '', '', '', '', '', '', '', '', '', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `js_sys_menu` VALUES ('1645760901891694592', '1085021470879444992', '0,1085021295159078912,1085021470879444992,', 90, '0000009010,0000000030,0000000090,', '1', 2, '用户管理/用户列表/操作', '操作', '2', '', '', '', '', '', 'player:action', 40, '1', 'default', 'player', '0', 'system', '2023-04-11 20:09:10', 'system', '2023-04-11 20:09:10', '', '', '', '', '', '', '', '', '', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `js_sys_menu` VALUES ('1734106107667324928', '1085021470879444992', '0,1085021295159078912,1085021470879444992,', 120, '0000009010,0000000030,0000000120,', '1', 2, '用户管理/用户列表/权限一', '权限一', '2', '', '', '', '', '', 'player:permission1', 40, '1', 'default', 'player', '0', 'system', '2023-12-11 15:01:29', 'system', '2023-12-11 15:01:29', '', '', '', '', '', '', '', '', '', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `js_sys_menu` VALUES ('1734106156509995008', '1085021470879444992', '0,1085021295159078912,1085021470879444992,', 150, '0000009010,0000000030,0000000150,', '1', 2, '用户管理/用户列表/权限二', '权限二', '2', '', '', '', '', '', 'player:permission2', 40, '1', 'default', 'player', '0', 'system', '2023-12-11 15:01:41', 'system', '2023-12-11 15:01:41', '', '', '', '', '', '', '', '', '', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `js_sys_menu` VALUES ('1734106197068914688', '1085021470879444992', '0,1085021295159078912,1085021470879444992,', 180, '0000009010,0000000030,0000000180,', '1', 2, '用户管理/用户列表/权限三', '权限三', '2', '', '', '', '', '', 'player:permission3', 40, '1', 'default', 'player', '0', 'system', '2023-12-11 15:01:50', 'system', '2023-12-11 15:01:50', '', '', '', '', '', '', '', '', '', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `js_sys_menu` VALUES ('1763097984844505088', '1085021295159078912', '0,1085021295159078912,', 31, '0000009010,0000000031,', '1', 1, '用户管理/在线用户', '在线用户', '1', '/osee/player/list/online', '', 'fa fa-user', '', '', '', 40, '1', 'default', 'player', '0', 'system', '2024-02-29 15:04:51', 'system', '2024-02-29 15:04:51', '', '', '', '', '', '', '', '', '', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);

-- ----------------------------
-- Table structure for js_sys_module
-- ----------------------------
DROP TABLE IF EXISTS `js_sys_module`;
CREATE TABLE `js_sys_module`  (
  `module_code` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '模块编码',
  `module_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '模块名称',
  `description` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '模块描述',
  `main_class_name` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '主类全名',
  `current_version` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '当前版本',
  `upgrade_info` varchar(300) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '升级信息',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '0' COMMENT '状态（0正常 1删除 2停用）',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '创建者',
  `create_date` datetime NOT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '更新者',
  `update_date` datetime NOT NULL COMMENT '更新时间',
  `remarks` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '备注信息',
  PRIMARY KEY (`module_code`) USING BTREE,
  INDEX `idx_sys_module_status`(`status` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '模块表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of js_sys_module
-- ----------------------------
INSERT INTO `js_sys_module` VALUES ('bpm', '业务流程', '流程设计器、流程监管控制、流程办理、流程追踪', 'com.jeesite.modules.bpm.entity.BpmEntity', '4.1.x', NULL, '0', 'system', '2023-05-23 11:33:50', 'system', '2023-05-23 11:33:50', NULL);
INSERT INTO `js_sys_module` VALUES ('cms', '内容管理', '网站、站点、栏目、文章、链接、评论、留言板', 'com.jeesite.modules.cms.web.CmsController', '4.0.0', NULL, '0', 'system', '2019-01-15 10:51:57', 'system', '2019-01-15 10:51:57', NULL);
INSERT INTO `js_sys_module` VALUES ('core', '核心模块', '用户、角色、组织、模块、菜单、字典、参数', 'com.jeesite.modules.sys.web.LoginController', '4.1.9', 'upgrade 2023-05-23 11:33:50 (4.1.2 -> 4.1.9)', '0', 'system', '2019-01-15 10:51:57', 'system', '2023-05-23 11:33:50', NULL);
INSERT INTO `js_sys_module` VALUES ('filemanager', '文件管理', '公共文件柜、个人文件柜、文件分享', 'com.jeesite.modules.filemanager.web.FilemanagerController', '4.1.4', NULL, '0', 'system', '2023-05-23 11:33:49', 'system', '2023-05-23 11:33:49', NULL);
INSERT INTO `js_sys_module` VALUES ('game', '游戏管理模块', '游戏相关参数和信息管理模块', 'com.jeesite.modules.osee.web.GameController', '1.0.0', NULL, '0', 'system', '2019-01-15 11:06:41', 'system', '2019-01-24 17:40:18', NULL);
INSERT INTO `js_sys_module` VALUES ('money', '财务管理模块', '游戏内金币、金钱相关的管理', 'com.jeesite.modules.osee.web.MoneyController', '1.0.0', NULL, '0', 'system', '2019-01-15 13:49:00', 'system', '2019-01-24 17:40:03', NULL);
INSERT INTO `js_sys_module` VALUES ('player', '玩家管理模块', '玩家管理相关的模块', 'com.jeesite.modules.osee.web.PlayerController', '1.0.0', NULL, '0', 'system', '2019-01-15 11:48:45', 'system', '2019-01-24 17:39:59', NULL);
INSERT INTO `js_sys_module` VALUES ('shop', '商城管理模块', '游戏内道具、奖励、商品兑换的管理模块', 'com.jeesite.modules.osee.web.ShopController', '1.0.0', NULL, '0', 'system', '2019-01-15 13:48:03', 'system', '2019-01-24 17:40:06', NULL);
INSERT INTO `js_sys_module` VALUES ('statistics', '统计管理模块', '游戏数据的统计、监控管理模块', 'com.jeesite.modules.osee.web.StatisticsController', '1.0.0', NULL, '0', 'system', '2019-01-15 12:01:31', 'system', '2019-01-24 17:40:13', NULL);

-- ----------------------------
-- Table structure for js_sys_msg_inner
-- ----------------------------
DROP TABLE IF EXISTS `js_sys_msg_inner`;
CREATE TABLE `js_sys_msg_inner`  (
  `id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '编号',
  `msg_title` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '消息标题',
  `content_level` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '内容级别（1普通 2一般 3紧急）',
  `content_type` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '内容类型（1公告 2新闻 3会议 4其它）',
  `msg_content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '消息内容',
  `receive_type` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '接受者类型（1用户 2部门 3角色 4岗位）',
  `receive_codes` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '接受者字符串',
  `receive_names` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '接受者名称字符串',
  `send_user_code` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '发送者用户编码',
  `send_user_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '发送者用户姓名',
  `send_date` datetime NULL DEFAULT NULL COMMENT '发送时间',
  `is_attac` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '是否有附件',
  `notify_types` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '通知类型（PC APP 短信 邮件 微信）多选',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '状态（0正常 1删除 4审核 5驳回 9草稿）',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '创建者',
  `create_date` datetime NOT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '更新者',
  `update_date` datetime NOT NULL COMMENT '更新时间',
  `remarks` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '备注信息',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_sys_msg_inner_cb`(`create_by` ASC) USING BTREE,
  INDEX `idx_sys_msg_inner_status`(`status` ASC) USING BTREE,
  INDEX `idx_sys_msg_inner_cl`(`content_level` ASC) USING BTREE,
  INDEX `idx_sys_msg_inner_sc`(`send_user_code` ASC) USING BTREE,
  INDEX `idx_sys_msg_inner_sd`(`send_date` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '内部消息' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of js_sys_msg_inner
-- ----------------------------

-- ----------------------------
-- Table structure for js_sys_msg_inner_record
-- ----------------------------
DROP TABLE IF EXISTS `js_sys_msg_inner_record`;
CREATE TABLE `js_sys_msg_inner_record`  (
  `id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '编号',
  `msg_inner_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '所属消息',
  `receive_user_code` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '接受者用户编码',
  `receive_user_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '接受者用户姓名',
  `read_status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '读取状态（0未送达 1已读 2未读）',
  `read_date` datetime NULL DEFAULT NULL COMMENT '阅读时间',
  `is_star` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '是否标星',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_sys_msg_inner_r_mi`(`msg_inner_id` ASC) USING BTREE,
  INDEX `idx_sys_msg_inner_r_rc`(`receive_user_code` ASC) USING BTREE,
  INDEX `idx_sys_msg_inner_r_ruc`(`receive_user_code` ASC) USING BTREE,
  INDEX `idx_sys_msg_inner_r_status`(`read_status` ASC) USING BTREE,
  INDEX `idx_sys_msg_inner_r_star`(`is_star` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '内部消息发送记录表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of js_sys_msg_inner_record
-- ----------------------------

-- ----------------------------
-- Table structure for js_sys_msg_push
-- ----------------------------
DROP TABLE IF EXISTS `js_sys_msg_push`;
CREATE TABLE `js_sys_msg_push`  (
  `id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '编号',
  `msg_type` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '消息类型（PC APP 短信 邮件 微信）',
  `msg_title` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '消息标题',
  `msg_content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '消息内容',
  `biz_key` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '业务主键',
  `biz_type` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '业务类型',
  `receive_code` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '接受者账号',
  `receive_user_code` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '接受者用户编码',
  `receive_user_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '接受者用户姓名',
  `send_user_code` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '发送者用户编码',
  `send_user_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '发送者用户姓名',
  `send_date` datetime NOT NULL COMMENT '发送时间',
  `is_merge_push` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '是否合并推送',
  `plan_push_date` datetime NULL DEFAULT NULL COMMENT '计划推送时间',
  `push_number` int NULL DEFAULT NULL COMMENT '推送尝试次数',
  `push_return_code` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '推送返回结果码',
  `push_return_msg_id` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '推送返回消息编号',
  `push_return_content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '推送返回的内容信息',
  `push_status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '推送状态（0未推送 1成功  2失败）',
  `push_date` datetime NULL DEFAULT NULL COMMENT '推送时间',
  `read_status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '读取状态（0未送达 1已读 2未读）',
  `read_date` datetime NULL DEFAULT NULL COMMENT '读取时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_sys_msg_push_type`(`msg_type` ASC) USING BTREE,
  INDEX `idx_sys_msg_push_rc`(`receive_code` ASC) USING BTREE,
  INDEX `idx_sys_msg_push_uc`(`receive_user_code` ASC) USING BTREE,
  INDEX `idx_sys_msg_push_suc`(`send_user_code` ASC) USING BTREE,
  INDEX `idx_sys_msg_push_pd`(`plan_push_date` ASC) USING BTREE,
  INDEX `idx_sys_msg_push_ps`(`push_status` ASC) USING BTREE,
  INDEX `idx_sys_msg_push_rs`(`read_status` ASC) USING BTREE,
  INDEX `idx_sys_msg_push_bk`(`biz_key` ASC) USING BTREE,
  INDEX `idx_sys_msg_push_bt`(`biz_type` ASC) USING BTREE,
  INDEX `idx_sys_msg_push_imp`(`is_merge_push` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '消息推送表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of js_sys_msg_push
-- ----------------------------

-- ----------------------------
-- Table structure for js_sys_msg_pushed
-- ----------------------------
DROP TABLE IF EXISTS `js_sys_msg_pushed`;
CREATE TABLE `js_sys_msg_pushed`  (
  `id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '编号',
  `msg_type` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '消息类型（PC APP 短信 邮件 微信）',
  `msg_title` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '消息标题',
  `msg_content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '消息内容',
  `biz_key` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '业务主键',
  `biz_type` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '业务类型',
  `receive_code` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '接受者账号',
  `receive_user_code` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '接受者用户编码',
  `receive_user_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '接受者用户姓名',
  `send_user_code` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '发送者用户编码',
  `send_user_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '发送者用户姓名',
  `send_date` datetime NOT NULL COMMENT '发送时间',
  `is_merge_push` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '是否合并推送',
  `plan_push_date` datetime NULL DEFAULT NULL COMMENT '计划推送时间',
  `push_number` int NULL DEFAULT NULL COMMENT '推送尝试次数',
  `push_return_content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '推送返回的内容信息',
  `push_return_code` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '推送返回结果码',
  `push_return_msg_id` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '推送返回消息编号',
  `push_status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '推送状态（0未推送 1成功  2失败）',
  `push_date` datetime NULL DEFAULT NULL COMMENT '推送时间',
  `read_status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '读取状态（0未送达 1已读 2未读）',
  `read_date` datetime NULL DEFAULT NULL COMMENT '读取时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_sys_msg_pushed_type`(`msg_type` ASC) USING BTREE,
  INDEX `idx_sys_msg_pushed_rc`(`receive_code` ASC) USING BTREE,
  INDEX `idx_sys_msg_pushed_uc`(`receive_user_code` ASC) USING BTREE,
  INDEX `idx_sys_msg_pushed_suc`(`send_user_code` ASC) USING BTREE,
  INDEX `idx_sys_msg_pushed_pd`(`plan_push_date` ASC) USING BTREE,
  INDEX `idx_sys_msg_pushed_ps`(`push_status` ASC) USING BTREE,
  INDEX `idx_sys_msg_pushed_rs`(`read_status` ASC) USING BTREE,
  INDEX `idx_sys_msg_pushed_bk`(`biz_key` ASC) USING BTREE,
  INDEX `idx_sys_msg_pushed_bt`(`biz_type` ASC) USING BTREE,
  INDEX `idx_sys_msg_pushed_imp`(`is_merge_push` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '消息已推送表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of js_sys_msg_pushed
-- ----------------------------

-- ----------------------------
-- Table structure for js_sys_msg_template
-- ----------------------------
DROP TABLE IF EXISTS `js_sys_msg_template`;
CREATE TABLE `js_sys_msg_template`  (
  `id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '编号',
  `module_code` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '归属模块',
  `tpl_key` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '模板键值',
  `tpl_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '模板名称',
  `tpl_type` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '模板类型',
  `tpl_content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '模板内容',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '0' COMMENT '状态（0正常 1删除 2停用）',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '创建者',
  `create_date` datetime NOT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '更新者',
  `update_date` datetime NOT NULL COMMENT '更新时间',
  `remarks` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '备注信息',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_sys_msg_tpl_key`(`tpl_key` ASC) USING BTREE,
  INDEX `idx_sys_msg_tpl_type`(`tpl_type` ASC) USING BTREE,
  INDEX `idx_sys_msg_tpl_status`(`status` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '消息模板' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of js_sys_msg_template
-- ----------------------------

-- ----------------------------
-- Table structure for js_sys_office
-- ----------------------------
DROP TABLE IF EXISTS `js_sys_office`;
CREATE TABLE `js_sys_office`  (
  `office_code` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '机构编码',
  `parent_code` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '父级编号',
  `parent_codes` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '所有父级编号',
  `tree_sort` decimal(10, 0) NOT NULL COMMENT '本级排序号（升序）',
  `tree_sorts` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '所有级别排序号',
  `tree_leaf` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '是否最末级',
  `tree_level` decimal(4, 0) NOT NULL COMMENT '层次级别',
  `tree_names` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '全节点名',
  `view_code` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '机构代码',
  `office_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '机构名称',
  `full_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '机构全称',
  `office_type` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '机构类型',
  `leader` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '负责人',
  `phone` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '办公电话',
  `address` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '联系地址',
  `zip_code` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '邮政编码',
  `email` varchar(300) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '电子邮箱',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '0' COMMENT '状态（0正常 1删除 2停用）',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '创建者',
  `create_date` datetime NOT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '更新者',
  `update_date` datetime NOT NULL COMMENT '更新时间',
  `remarks` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '备注信息',
  `corp_code` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '0' COMMENT '租户代码',
  `corp_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'JeeSite' COMMENT '租户名称',
  `extend_s1` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '扩展 String 1',
  `extend_s2` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '扩展 String 2',
  `extend_s3` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '扩展 String 3',
  `extend_s4` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '扩展 String 4',
  `extend_s5` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '扩展 String 5',
  `extend_s6` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '扩展 String 6',
  `extend_s7` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '扩展 String 7',
  `extend_s8` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '扩展 String 8',
  `extend_i1` decimal(19, 0) NULL DEFAULT NULL COMMENT '扩展 Integer 1',
  `extend_i2` decimal(19, 0) NULL DEFAULT NULL COMMENT '扩展 Integer 2',
  `extend_i3` decimal(19, 0) NULL DEFAULT NULL COMMENT '扩展 Integer 3',
  `extend_i4` decimal(19, 0) NULL DEFAULT NULL COMMENT '扩展 Integer 4',
  `extend_f1` decimal(19, 4) NULL DEFAULT NULL COMMENT '扩展 Float 1',
  `extend_f2` decimal(19, 4) NULL DEFAULT NULL COMMENT '扩展 Float 2',
  `extend_f3` decimal(19, 4) NULL DEFAULT NULL COMMENT '扩展 Float 3',
  `extend_f4` decimal(19, 4) NULL DEFAULT NULL COMMENT '扩展 Float 4',
  `extend_d1` datetime NULL DEFAULT NULL COMMENT '扩展 Date 1',
  `extend_d2` datetime NULL DEFAULT NULL COMMENT '扩展 Date 2',
  `extend_d3` datetime NULL DEFAULT NULL COMMENT '扩展 Date 3',
  `extend_d4` datetime NULL DEFAULT NULL COMMENT '扩展 Date 4',
  PRIMARY KEY (`office_code`) USING BTREE,
  INDEX `idx_sys_office_cc`(`corp_code` ASC) USING BTREE,
  INDEX `idx_sys_office_pc`(`parent_code` ASC) USING BTREE,
  INDEX `idx_sys_office_status`(`status` ASC) USING BTREE,
  INDEX `idx_sys_office_ot`(`office_type` ASC) USING BTREE,
  INDEX `idx_sys_office_vc`(`view_code` ASC) USING BTREE,
  INDEX `idx_sys_office_ts`(`tree_sort` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '组织机构表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of js_sys_office
-- ----------------------------
INSERT INTO `js_sys_office` VALUES ('1688_game_manager', '0', '0,', 30, '0000000030,', '1', 0, '1688后台管理', '1688_game_manager', '1688后台管理', '1688后台管理', '3', '', '', '', '', '', '0', 'system', '2019-01-30 10:20:25', 'system', '2019-01-30 10:20:25', '', '0', 'JeeSite', '', '', '', '', '', '', '', '', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);

-- ----------------------------
-- Table structure for js_sys_post
-- ----------------------------
DROP TABLE IF EXISTS `js_sys_post`;
CREATE TABLE `js_sys_post`  (
  `post_code` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '岗位编码',
  `post_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '岗位名称',
  `post_type` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '岗位分类（高管、中层、基层）',
  `post_sort` decimal(10, 0) NULL DEFAULT NULL COMMENT '岗位排序（升序）',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '0' COMMENT '状态（0正常 1删除 2停用）',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '创建者',
  `create_date` datetime NOT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '更新者',
  `update_date` datetime NOT NULL COMMENT '更新时间',
  `remarks` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '备注信息',
  `corp_code` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '0' COMMENT '租户代码',
  `corp_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'JeeSite' COMMENT '租户名称',
  PRIMARY KEY (`post_code`) USING BTREE,
  INDEX `idx_sys_post_cc`(`corp_code` ASC) USING BTREE,
  INDEX `idx_sys_post_status`(`status` ASC) USING BTREE,
  INDEX `idx_sys_post_ps`(`post_sort` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '员工岗位表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of js_sys_post
-- ----------------------------

-- ----------------------------
-- Table structure for js_sys_role
-- ----------------------------
DROP TABLE IF EXISTS `js_sys_role`;
CREATE TABLE `js_sys_role`  (
  `role_code` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '角色编码',
  `role_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '角色名称',
  `role_type` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '角色分类（高管、中层、基层、其它）',
  `role_sort` decimal(10, 0) NULL DEFAULT NULL COMMENT '角色排序（升序）',
  `is_sys` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '系统内置（1是 0否）',
  `user_type` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '用户类型（employee员工 member会员）',
  `data_scope` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '数据范围设置（0未设置  1全部数据 2自定义数据）',
  `biz_scope` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '适应业务范围（不同的功能，不同的数据权限支持）',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '0' COMMENT '状态（0正常 1删除 2停用）',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '创建者',
  `create_date` datetime NOT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '更新者',
  `update_date` datetime NOT NULL COMMENT '更新时间',
  `remarks` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '备注信息',
  `corp_code` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '0' COMMENT '租户代码',
  `corp_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'JeeSite' COMMENT '租户名称',
  `extend_s1` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '扩展 String 1',
  `extend_s2` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '扩展 String 2',
  `extend_s3` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '扩展 String 3',
  `extend_s4` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '扩展 String 4',
  `extend_s5` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '扩展 String 5',
  `extend_s6` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '扩展 String 6',
  `extend_s7` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '扩展 String 7',
  `extend_s8` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '扩展 String 8',
  `extend_i1` decimal(19, 0) NULL DEFAULT NULL COMMENT '扩展 Integer 1',
  `extend_i2` decimal(19, 0) NULL DEFAULT NULL COMMENT '扩展 Integer 2',
  `extend_i3` decimal(19, 0) NULL DEFAULT NULL COMMENT '扩展 Integer 3',
  `extend_i4` decimal(19, 0) NULL DEFAULT NULL COMMENT '扩展 Integer 4',
  `extend_f1` decimal(19, 4) NULL DEFAULT NULL COMMENT '扩展 Float 1',
  `extend_f2` decimal(19, 4) NULL DEFAULT NULL COMMENT '扩展 Float 2',
  `extend_f3` decimal(19, 4) NULL DEFAULT NULL COMMENT '扩展 Float 3',
  `extend_f4` decimal(19, 4) NULL DEFAULT NULL COMMENT '扩展 Float 4',
  `extend_d1` datetime NULL DEFAULT NULL COMMENT '扩展 Date 1',
  `extend_d2` datetime NULL DEFAULT NULL COMMENT '扩展 Date 2',
  `extend_d3` datetime NULL DEFAULT NULL COMMENT '扩展 Date 3',
  `extend_d4` datetime NULL DEFAULT NULL COMMENT '扩展 Date 4',
  PRIMARY KEY (`role_code`) USING BTREE,
  INDEX `idx_sys_role_cc`(`corp_code` ASC) USING BTREE,
  INDEX `idx_sys_role_is`(`is_sys` ASC) USING BTREE,
  INDEX `idx_sys_role_status`(`status` ASC) USING BTREE,
  INDEX `idx_sys_role_rs`(`role_sort` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '角色表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of js_sys_role
-- ----------------------------
INSERT INTO `js_sys_role` VALUES ('034621', '测试管理员', '', 10, '0', 'employee', NULL, NULL, '0', 'system', '2023-06-12 11:19:34', 'system', '2023-06-12 11:19:34', '', '0', 'JeeSite', '', '', '', '', '', '', '', '', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `js_sys_role` VALUES ('0999', 'bigfish999', '', 20, '0', 'employee', NULL, NULL, '0', 'system', '2023-10-19 09:12:05', 'system', '2023-10-19 09:12:05', '', '0', 'JeeSite', '', '', '', '', '', '', '', '', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `js_sys_role` VALUES ('corpAdmin', '系统管理员', '', 200, '0', 'none', '5', NULL, '0', 'system', '2019-01-15 10:52:03', 'system', '2022-09-28 18:28:03', '客户方使用的管理员角色，客户方管理员，集团管理员', '0', 'JeeSite', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `js_sys_role` VALUES ('default', '默认角色', '', 100, '0', 'none', '0', NULL, '1', 'system', '2019-01-15 10:52:03', 'system', '2023-06-03 21:50:08', '非管理员用户，共有的默认角色，在参数配置里指定', '0', 'JeeSite', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);

-- ----------------------------
-- Table structure for js_sys_role_data_scope
-- ----------------------------
DROP TABLE IF EXISTS `js_sys_role_data_scope`;
CREATE TABLE `js_sys_role_data_scope`  (
  `role_code` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '控制角色编码',
  `ctrl_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '控制类型',
  `ctrl_data` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '控制数据',
  `ctrl_permi` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '控制权限',
  PRIMARY KEY (`role_code`, `ctrl_type`, `ctrl_data`, `ctrl_permi`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '角色数据权限表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of js_sys_role_data_scope
-- ----------------------------

-- ----------------------------
-- Table structure for js_sys_role_menu
-- ----------------------------
DROP TABLE IF EXISTS `js_sys_role_menu`;
CREATE TABLE `js_sys_role_menu`  (
  `role_code` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '角色编码',
  `menu_code` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '菜单编码',
  PRIMARY KEY (`role_code`, `menu_code`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '角色与菜单关联表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of js_sys_role_menu
-- ----------------------------
INSERT INTO `js_sys_role_menu` VALUES ('022352', '1085021295159078912');
INSERT INTO `js_sys_role_menu` VALUES ('022352', '1085021470879444992');
INSERT INTO `js_sys_role_menu` VALUES ('022352', '1085030507834568704');
INSERT INTO `js_sys_role_menu` VALUES ('022352', '1085030702441885696');
INSERT INTO `js_sys_role_menu` VALUES ('022352', '1086156125624123392');
INSERT INTO `js_sys_role_menu` VALUES ('022352', '1087523144733528064');
INSERT INTO `js_sys_role_menu` VALUES ('022352', '1645760901891694592');
INSERT INTO `js_sys_role_menu` VALUES ('034621', '1085011080834879488');
INSERT INTO `js_sys_role_menu` VALUES ('034621', '1085016814308618240');
INSERT INTO `js_sys_role_menu` VALUES ('034621', '1085019431499788288');
INSERT INTO `js_sys_role_menu` VALUES ('034621', '1085020021323788288');
INSERT INTO `js_sys_role_menu` VALUES ('034621', '1085020305693405184');
INSERT INTO `js_sys_role_menu` VALUES ('034621', '1085021295159078912');
INSERT INTO `js_sys_role_menu` VALUES ('034621', '1085021470879444992');
INSERT INTO `js_sys_role_menu` VALUES ('034621', '1085030507834568704');
INSERT INTO `js_sys_role_menu` VALUES ('034621', '1085030702441885696');
INSERT INTO `js_sys_role_menu` VALUES ('034621', '1085051673381060608');
INSERT INTO `js_sys_role_menu` VALUES ('034621', '1085054627580362752');
INSERT INTO `js_sys_role_menu` VALUES ('034621', '1085055184055451648');
INSERT INTO `js_sys_role_menu` VALUES ('034621', '1085055959016030208');
INSERT INTO `js_sys_role_menu` VALUES ('034621', '1085056215950704640');
INSERT INTO `js_sys_role_menu` VALUES ('034621', '1085056316324593664');
INSERT INTO `js_sys_role_menu` VALUES ('034621', '1085056446289297408');
INSERT INTO `js_sys_role_menu` VALUES ('034621', '1085357426347704320');
INSERT INTO `js_sys_role_menu` VALUES ('034621', '1085357470249484288');
INSERT INTO `js_sys_role_menu` VALUES ('034621', '1085402262577029120');
INSERT INTO `js_sys_role_menu` VALUES ('034621', '1085402363340988416');
INSERT INTO `js_sys_role_menu` VALUES ('034621', '1085428737919315968');
INSERT INTO `js_sys_role_menu` VALUES ('034621', '1085428799089045504');
INSERT INTO `js_sys_role_menu` VALUES ('034621', '1086156125624123392');
INSERT INTO `js_sys_role_menu` VALUES ('034621', '1086156201570385920');
INSERT INTO `js_sys_role_menu` VALUES ('034621', '1087165801197867008');
INSERT INTO `js_sys_role_menu` VALUES ('034621', '1087165842717282304');
INSERT INTO `js_sys_role_menu` VALUES ('034621', '1087523144733528064');
INSERT INTO `js_sys_role_menu` VALUES ('034621', '1087970121651101696');
INSERT INTO `js_sys_role_menu` VALUES ('034621', '1087970211702808576');
INSERT INTO `js_sys_role_menu` VALUES ('034621', '1087970433279500288');
INSERT INTO `js_sys_role_menu` VALUES ('034621', '1088282368895913984');
INSERT INTO `js_sys_role_menu` VALUES ('034621', '1088282424336224256');
INSERT INTO `js_sys_role_menu` VALUES ('034621', '1110819951473287168');
INSERT INTO `js_sys_role_menu` VALUES ('034621', '1110820102547922944');
INSERT INTO `js_sys_role_menu` VALUES ('034621', '1110820171951071232');
INSERT INTO `js_sys_role_menu` VALUES ('034621', '1110820214670057472');
INSERT INTO `js_sys_role_menu` VALUES ('034621', '1113694937802493952');
INSERT INTO `js_sys_role_menu` VALUES ('034621', '1113695012591128576');
INSERT INTO `js_sys_role_menu` VALUES ('034621', '1144572229704945664');
INSERT INTO `js_sys_role_menu` VALUES ('034621', '1144572304237727744');
INSERT INTO `js_sys_role_menu` VALUES ('034621', '1159845016243826688');
INSERT INTO `js_sys_role_menu` VALUES ('034621', '1159845091711938560');
INSERT INTO `js_sys_role_menu` VALUES ('034621', '1159845146221113344');
INSERT INTO `js_sys_role_menu` VALUES ('034621', '1167015031736459264');
INSERT INTO `js_sys_role_menu` VALUES ('034621', '1167015116029386752');
INSERT INTO `js_sys_role_menu` VALUES ('034621', '1167015160979742720');
INSERT INTO `js_sys_role_menu` VALUES ('034621', '1167015281284964352');
INSERT INTO `js_sys_role_menu` VALUES ('034621', '1167015334955278336');
INSERT INTO `js_sys_role_menu` VALUES ('034621', '1196637686008713216');
INSERT INTO `js_sys_role_menu` VALUES ('034621', '1197419228210843648');
INSERT INTO `js_sys_role_menu` VALUES ('034621', '1309381903976685568');
INSERT INTO `js_sys_role_menu` VALUES ('034621', '1309382225218428928');
INSERT INTO `js_sys_role_menu` VALUES ('034621', '1310525070854549504');
INSERT INTO `js_sys_role_menu` VALUES ('034621', '1310525183597441024');
INSERT INTO `js_sys_role_menu` VALUES ('034621', '1359547184861908992');
INSERT INTO `js_sys_role_menu` VALUES ('034621', '1359547314893721600');
INSERT INTO `js_sys_role_menu` VALUES ('034621', '1448124255815577600');
INSERT INTO `js_sys_role_menu` VALUES ('034621', '1448127547568271360');
INSERT INTO `js_sys_role_menu` VALUES ('034621', '1574301837102968832');
INSERT INTO `js_sys_role_menu` VALUES ('034621', '1634078882152751104');
INSERT INTO `js_sys_role_menu` VALUES ('034621', '1634099652020277248');
INSERT INTO `js_sys_role_menu` VALUES ('034621', '1645760901891694592');
INSERT INTO `js_sys_role_menu` VALUES ('0999', '1085006669836128256');
INSERT INTO `js_sys_role_menu` VALUES ('0999', '1085006670133923840');
INSERT INTO `js_sys_role_menu` VALUES ('0999', '1085006670385582080');
INSERT INTO `js_sys_role_menu` VALUES ('0999', '1085006670624657408');
INSERT INTO `js_sys_role_menu` VALUES ('0999', '1085006670884704256');
INSERT INTO `js_sys_role_menu` VALUES ('0999', '1085006671182499840');
INSERT INTO `js_sys_role_menu` VALUES ('0999', '1085006671392215040');
INSERT INTO `js_sys_role_menu` VALUES ('0999', '1085006671614513152');
INSERT INTO `js_sys_role_menu` VALUES ('0999', '1085006671807451136');
INSERT INTO `js_sys_role_menu` VALUES ('0999', '1085006674600857600');
INSERT INTO `js_sys_role_menu` VALUES ('0999', '1085006674789601280');
INSERT INTO `js_sys_role_menu` VALUES ('0999', '1085006675007705088');
INSERT INTO `js_sys_role_menu` VALUES ('0999', '1085011080834879488');
INSERT INTO `js_sys_role_menu` VALUES ('0999', '1085016814308618240');
INSERT INTO `js_sys_role_menu` VALUES ('0999', '1085019431499788288');
INSERT INTO `js_sys_role_menu` VALUES ('0999', '1085020021323788288');
INSERT INTO `js_sys_role_menu` VALUES ('0999', '1085021295159078912');
INSERT INTO `js_sys_role_menu` VALUES ('0999', '1085021470879444992');
INSERT INTO `js_sys_role_menu` VALUES ('0999', '1085030507834568704');
INSERT INTO `js_sys_role_menu` VALUES ('0999', '1085030702441885696');
INSERT INTO `js_sys_role_menu` VALUES ('0999', '1085051673381060608');
INSERT INTO `js_sys_role_menu` VALUES ('0999', '1085054627580362752');
INSERT INTO `js_sys_role_menu` VALUES ('0999', '1085055184055451648');
INSERT INTO `js_sys_role_menu` VALUES ('0999', '1085055959016030208');
INSERT INTO `js_sys_role_menu` VALUES ('0999', '1085056215950704640');
INSERT INTO `js_sys_role_menu` VALUES ('0999', '1085357426347704320');
INSERT INTO `js_sys_role_menu` VALUES ('0999', '1085357470249484288');
INSERT INTO `js_sys_role_menu` VALUES ('0999', '1085402262577029120');
INSERT INTO `js_sys_role_menu` VALUES ('0999', '1085402363340988416');
INSERT INTO `js_sys_role_menu` VALUES ('0999', '1086156125624123392');
INSERT INTO `js_sys_role_menu` VALUES ('0999', '1086156201570385920');
INSERT INTO `js_sys_role_menu` VALUES ('0999', '1087165801197867008');
INSERT INTO `js_sys_role_menu` VALUES ('0999', '1087165842717282304');
INSERT INTO `js_sys_role_menu` VALUES ('0999', '1087523144733528064');
INSERT INTO `js_sys_role_menu` VALUES ('0999', '1087970121651101696');
INSERT INTO `js_sys_role_menu` VALUES ('0999', '1087970211702808576');
INSERT INTO `js_sys_role_menu` VALUES ('0999', '1087970433279500288');
INSERT INTO `js_sys_role_menu` VALUES ('0999', '1144572229704945664');
INSERT INTO `js_sys_role_menu` VALUES ('0999', '1144572304237727744');
INSERT INTO `js_sys_role_menu` VALUES ('0999', '1159845016243826688');
INSERT INTO `js_sys_role_menu` VALUES ('0999', '1159845091711938560');
INSERT INTO `js_sys_role_menu` VALUES ('0999', '1159845146221113344');
INSERT INTO `js_sys_role_menu` VALUES ('0999', '1196637686008713216');
INSERT INTO `js_sys_role_menu` VALUES ('0999', '1197419228210843648');
INSERT INTO `js_sys_role_menu` VALUES ('0999', '1309381903976685568');
INSERT INTO `js_sys_role_menu` VALUES ('0999', '1309382225218428928');
INSERT INTO `js_sys_role_menu` VALUES ('0999', '1448124255815577600');
INSERT INTO `js_sys_role_menu` VALUES ('0999', '1448127547568271360');
INSERT INTO `js_sys_role_menu` VALUES ('0999', '1574301837102968832');
INSERT INTO `js_sys_role_menu` VALUES ('0999', '1634078882152751104');
INSERT INTO `js_sys_role_menu` VALUES ('0999', '1634099652020277248');
INSERT INTO `js_sys_role_menu` VALUES ('0999', '1645760901891694592');
INSERT INTO `js_sys_role_menu` VALUES ('0999', '1714469279822942208');
INSERT INTO `js_sys_role_menu` VALUES ('corpAdmin', '1085011080834879488');
INSERT INTO `js_sys_role_menu` VALUES ('corpAdmin', '1085016814308618240');
INSERT INTO `js_sys_role_menu` VALUES ('corpAdmin', '1085019431499788288');
INSERT INTO `js_sys_role_menu` VALUES ('corpAdmin', '1085020021323788288');
INSERT INTO `js_sys_role_menu` VALUES ('corpAdmin', '1085020305693405184');
INSERT INTO `js_sys_role_menu` VALUES ('corpAdmin', '1085021295159078912');
INSERT INTO `js_sys_role_menu` VALUES ('corpAdmin', '1085021470879444992');
INSERT INTO `js_sys_role_menu` VALUES ('corpAdmin', '1085030507834568704');
INSERT INTO `js_sys_role_menu` VALUES ('corpAdmin', '1085030702441885696');
INSERT INTO `js_sys_role_menu` VALUES ('corpAdmin', '1085051673381060608');
INSERT INTO `js_sys_role_menu` VALUES ('corpAdmin', '1085054627580362752');
INSERT INTO `js_sys_role_menu` VALUES ('corpAdmin', '1085055184055451648');
INSERT INTO `js_sys_role_menu` VALUES ('corpAdmin', '1085055959016030208');
INSERT INTO `js_sys_role_menu` VALUES ('corpAdmin', '1085056215950704640');
INSERT INTO `js_sys_role_menu` VALUES ('corpAdmin', '1085056316324593664');
INSERT INTO `js_sys_role_menu` VALUES ('corpAdmin', '1085056446289297408');
INSERT INTO `js_sys_role_menu` VALUES ('corpAdmin', '1085357426347704320');
INSERT INTO `js_sys_role_menu` VALUES ('corpAdmin', '1085357470249484288');
INSERT INTO `js_sys_role_menu` VALUES ('corpAdmin', '1085402262577029120');
INSERT INTO `js_sys_role_menu` VALUES ('corpAdmin', '1085402363340988416');
INSERT INTO `js_sys_role_menu` VALUES ('corpAdmin', '1085428737919315968');
INSERT INTO `js_sys_role_menu` VALUES ('corpAdmin', '1085428799089045504');
INSERT INTO `js_sys_role_menu` VALUES ('corpAdmin', '1086156125624123392');
INSERT INTO `js_sys_role_menu` VALUES ('corpAdmin', '1086156201570385920');
INSERT INTO `js_sys_role_menu` VALUES ('corpAdmin', '1087165801197867008');
INSERT INTO `js_sys_role_menu` VALUES ('corpAdmin', '1087165842717282304');
INSERT INTO `js_sys_role_menu` VALUES ('corpAdmin', '1087523144733528064');
INSERT INTO `js_sys_role_menu` VALUES ('corpAdmin', '1087970121651101696');
INSERT INTO `js_sys_role_menu` VALUES ('corpAdmin', '1087970211702808576');
INSERT INTO `js_sys_role_menu` VALUES ('corpAdmin', '1087970433279500288');
INSERT INTO `js_sys_role_menu` VALUES ('corpAdmin', '1088282368895913984');
INSERT INTO `js_sys_role_menu` VALUES ('corpAdmin', '1088282424336224256');
INSERT INTO `js_sys_role_menu` VALUES ('corpAdmin', '1088724404717400064');
INSERT INTO `js_sys_role_menu` VALUES ('corpAdmin', '1088724531641233408');
INSERT INTO `js_sys_role_menu` VALUES ('corpAdmin', '1088724597860904960');
INSERT INTO `js_sys_role_menu` VALUES ('corpAdmin', '1110819951473287168');
INSERT INTO `js_sys_role_menu` VALUES ('corpAdmin', '1110820102547922944');
INSERT INTO `js_sys_role_menu` VALUES ('corpAdmin', '1110820171951071232');
INSERT INTO `js_sys_role_menu` VALUES ('corpAdmin', '1110820214670057472');
INSERT INTO `js_sys_role_menu` VALUES ('corpAdmin', '1113694937802493952');
INSERT INTO `js_sys_role_menu` VALUES ('corpAdmin', '1113695012591128576');
INSERT INTO `js_sys_role_menu` VALUES ('corpAdmin', '1144572229704945664');
INSERT INTO `js_sys_role_menu` VALUES ('corpAdmin', '1144572304237727744');
INSERT INTO `js_sys_role_menu` VALUES ('corpAdmin', '1159845016243826688');
INSERT INTO `js_sys_role_menu` VALUES ('corpAdmin', '1159845091711938560');
INSERT INTO `js_sys_role_menu` VALUES ('corpAdmin', '1159845146221113344');
INSERT INTO `js_sys_role_menu` VALUES ('corpAdmin', '1196637686008713216');
INSERT INTO `js_sys_role_menu` VALUES ('corpAdmin', '1197419228210843648');
INSERT INTO `js_sys_role_menu` VALUES ('corpAdmin', '1309381903976685568');
INSERT INTO `js_sys_role_menu` VALUES ('corpAdmin', '1309382225218428928');
INSERT INTO `js_sys_role_menu` VALUES ('corpAdmin', '1310525070854549504');
INSERT INTO `js_sys_role_menu` VALUES ('corpAdmin', '1310525183597441024');
INSERT INTO `js_sys_role_menu` VALUES ('corpAdmin', '1426423854595887104');
INSERT INTO `js_sys_role_menu` VALUES ('corpAdmin', '1435133319315898368');
INSERT INTO `js_sys_role_menu` VALUES ('corpAdmin', '1438796440854102016');
INSERT INTO `js_sys_role_menu` VALUES ('corpAdmin', '1448124255815577600');
INSERT INTO `js_sys_role_menu` VALUES ('corpAdmin', '1448127547568271360');
INSERT INTO `js_sys_role_menu` VALUES ('corpAdmin', '1574301837102968832');
INSERT INTO `js_sys_role_menu` VALUES ('corpAdmin', '1634078882152751104');
INSERT INTO `js_sys_role_menu` VALUES ('corpAdmin', '1634099652020277248');
INSERT INTO `js_sys_role_menu` VALUES ('corpAdmin', '1645760901891694592');
INSERT INTO `js_sys_role_menu` VALUES ('corpAdmin', '1734106107667324928');
INSERT INTO `js_sys_role_menu` VALUES ('corpAdmin', '1734106156509995008');
INSERT INTO `js_sys_role_menu` VALUES ('corpAdmin', '1734106197068914688');
INSERT INTO `js_sys_role_menu` VALUES ('corpAdmin', '1763097984844505088');
INSERT INTO `js_sys_role_menu` VALUES ('corpAdmin', '1780494619999895552');
INSERT INTO `js_sys_role_menu` VALUES ('corpAdmin', '1792724250848555008');
INSERT INTO `js_sys_role_menu` VALUES ('kf', '1085021295159078912');
INSERT INTO `js_sys_role_menu` VALUES ('kf', '1085021470879444992');
INSERT INTO `js_sys_role_menu` VALUES ('kf', '1085022370368909312');
INSERT INTO `js_sys_role_menu` VALUES ('kf', '1085076919655780352');
INSERT INTO `js_sys_role_menu` VALUES ('kf', '1085076993051906048');
INSERT INTO `js_sys_role_menu` VALUES ('kf', '1086156125624123392');
INSERT INTO `js_sys_role_menu` VALUES ('kf', '1086156201570385920');
INSERT INTO `js_sys_role_menu` VALUES ('kf', '1422465801924423680');
INSERT INTO `js_sys_role_menu` VALUES ('OKbuyu001', '1085011080834879488');
INSERT INTO `js_sys_role_menu` VALUES ('OKbuyu001', '1085016814308618240');
INSERT INTO `js_sys_role_menu` VALUES ('OKbuyu001', '1085021295159078912');
INSERT INTO `js_sys_role_menu` VALUES ('OKbuyu001', '1085021470879444992');
INSERT INTO `js_sys_role_menu` VALUES ('OKbuyu001', '1085030507834568704');
INSERT INTO `js_sys_role_menu` VALUES ('OKbuyu001', '1085030702441885696');
INSERT INTO `js_sys_role_menu` VALUES ('OKbuyu001', '1085054627580362752');
INSERT INTO `js_sys_role_menu` VALUES ('OKbuyu001', '1085055184055451648');
INSERT INTO `js_sys_role_menu` VALUES ('OKbuyu001', '1085056215950704640');
INSERT INTO `js_sys_role_menu` VALUES ('OKbuyu001', '1086156125624123392');
INSERT INTO `js_sys_role_menu` VALUES ('OKbuyu001', '1086156201570385920');
INSERT INTO `js_sys_role_menu` VALUES ('OKbuyu001', '1087165801197867008');
INSERT INTO `js_sys_role_menu` VALUES ('OKbuyu001', '1087165842717282304');
INSERT INTO `js_sys_role_menu` VALUES ('OKbuyu001', '1087523144733528064');
INSERT INTO `js_sys_role_menu` VALUES ('OKbuyu001', '1087970121651101696');
INSERT INTO `js_sys_role_menu` VALUES ('OKbuyu001', '1087970433279500288');
INSERT INTO `js_sys_role_menu` VALUES ('OKbuyu001', '1426423854595887104');
INSERT INTO `js_sys_role_menu` VALUES ('OKbuyu001', '1574301837102968832');
INSERT INTO `js_sys_role_menu` VALUES ('OKbuyu001', '1645760901891694592');
INSERT INTO `js_sys_role_menu` VALUES ('OKbuyu001', '1734106107667324928');
INSERT INTO `js_sys_role_menu` VALUES ('OKbuyu001', '1734106156509995008');
INSERT INTO `js_sys_role_menu` VALUES ('OKbuyu001', '1734106197068914688');
INSERT INTO `js_sys_role_menu` VALUES ('pxq123456', '1085011080834879488');
INSERT INTO `js_sys_role_menu` VALUES ('pxq123456', '1085019431499788288');
INSERT INTO `js_sys_role_menu` VALUES ('pxq123456', '1085020021323788288');
INSERT INTO `js_sys_role_menu` VALUES ('pxq123456', '1085020305693405184');
INSERT INTO `js_sys_role_menu` VALUES ('pxq123456', '1085021295159078912');
INSERT INTO `js_sys_role_menu` VALUES ('pxq123456', '1085021470879444992');
INSERT INTO `js_sys_role_menu` VALUES ('pxq123456', '1085030507834568704');
INSERT INTO `js_sys_role_menu` VALUES ('pxq123456', '1085030702441885696');
INSERT INTO `js_sys_role_menu` VALUES ('pxq123456', '1085051673381060608');
INSERT INTO `js_sys_role_menu` VALUES ('pxq123456', '1085054627580362752');
INSERT INTO `js_sys_role_menu` VALUES ('pxq123456', '1085055184055451648');
INSERT INTO `js_sys_role_menu` VALUES ('pxq123456', '1085056215950704640');
INSERT INTO `js_sys_role_menu` VALUES ('pxq123456', '1085357426347704320');
INSERT INTO `js_sys_role_menu` VALUES ('pxq123456', '1085357470249484288');
INSERT INTO `js_sys_role_menu` VALUES ('pxq123456', '1085402262577029120');
INSERT INTO `js_sys_role_menu` VALUES ('pxq123456', '1085402363340988416');
INSERT INTO `js_sys_role_menu` VALUES ('pxq123456', '1085428737919315968');
INSERT INTO `js_sys_role_menu` VALUES ('pxq123456', '1085428799089045504');
INSERT INTO `js_sys_role_menu` VALUES ('pxq123456', '1086156125624123392');
INSERT INTO `js_sys_role_menu` VALUES ('pxq123456', '1086156201570385920');
INSERT INTO `js_sys_role_menu` VALUES ('pxq123456', '1087523144733528064');
INSERT INTO `js_sys_role_menu` VALUES ('pxq123456', '1087970121651101696');
INSERT INTO `js_sys_role_menu` VALUES ('pxq123456', '1087970433279500288');
INSERT INTO `js_sys_role_menu` VALUES ('pxq123456', '1110819951473287168');
INSERT INTO `js_sys_role_menu` VALUES ('pxq123456', '1110820102547922944');
INSERT INTO `js_sys_role_menu` VALUES ('pxq123456', '1110820171951071232');
INSERT INTO `js_sys_role_menu` VALUES ('pxq123456', '1110820214670057472');
INSERT INTO `js_sys_role_menu` VALUES ('pxq123456', '1113694937802493952');
INSERT INTO `js_sys_role_menu` VALUES ('pxq123456', '1113695012591128576');
INSERT INTO `js_sys_role_menu` VALUES ('pxq123456', '1144572229704945664');
INSERT INTO `js_sys_role_menu` VALUES ('pxq123456', '1144572304237727744');
INSERT INTO `js_sys_role_menu` VALUES ('pxq123456', '1159845016243826688');
INSERT INTO `js_sys_role_menu` VALUES ('pxq123456', '1159845091711938560');
INSERT INTO `js_sys_role_menu` VALUES ('pxq123456', '1159845146221113344');
INSERT INTO `js_sys_role_menu` VALUES ('pxq123456', '1196637686008713216');
INSERT INTO `js_sys_role_menu` VALUES ('pxq123456', '1197419228210843648');
INSERT INTO `js_sys_role_menu` VALUES ('pxq123456', '1309381903976685568');
INSERT INTO `js_sys_role_menu` VALUES ('pxq123456', '1309382225218428928');
INSERT INTO `js_sys_role_menu` VALUES ('pxq123456', '1310525070854549504');
INSERT INTO `js_sys_role_menu` VALUES ('pxq123456', '1310525183597441024');
INSERT INTO `js_sys_role_menu` VALUES ('pxq123456', '1426423854595887104');
INSERT INTO `js_sys_role_menu` VALUES ('pxq123456', '1448124255815577600');
INSERT INTO `js_sys_role_menu` VALUES ('pxq123456', '1448127547568271360');
INSERT INTO `js_sys_role_menu` VALUES ('pxq123456', '1574301837102968832');
INSERT INTO `js_sys_role_menu` VALUES ('pxq123456', '1634099652020277248');
INSERT INTO `js_sys_role_menu` VALUES ('pxq123456', '1645760901891694592');
INSERT INTO `js_sys_role_menu` VALUES ('pxq123456', '1734106107667324928');
INSERT INTO `js_sys_role_menu` VALUES ('pxq123456', '1734106156509995008');
INSERT INTO `js_sys_role_menu` VALUES ('pxq123456', '1734106197068914688');
INSERT INTO `js_sys_role_menu` VALUES ('pxq123456', '1763097984844505088');
INSERT INTO `js_sys_role_menu` VALUES ('test_role', '1085021295159078912');
INSERT INTO `js_sys_role_menu` VALUES ('test_role', '1085021470879444992');
INSERT INTO `js_sys_role_menu` VALUES ('test_role', '1086156125624123392');
INSERT INTO `js_sys_role_menu` VALUES ('test_role', '1086156201570385920');
INSERT INTO `js_sys_role_menu` VALUES ('test_role_not_system', '1085030507834568704');
INSERT INTO `js_sys_role_menu` VALUES ('test_role_not_system', '1085030702441885696');
INSERT INTO `js_sys_role_menu` VALUES ('test_role_not_system', '1087523144733528064');
INSERT INTO `js_sys_role_menu` VALUES ('test_role_not_system_2', '1085054627580362752');
INSERT INTO `js_sys_role_menu` VALUES ('test_role_not_system_2', '1144572229704945664');
INSERT INTO `js_sys_role_menu` VALUES ('test_role_not_system_2', '1144572304237727744');

-- ----------------------------
-- Table structure for js_sys_user
-- ----------------------------
DROP TABLE IF EXISTS `js_sys_user`;
CREATE TABLE `js_sys_user`  (
  `user_code` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '用户编码',
  `login_code` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '登录账号',
  `user_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '用户昵称',
  `password` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '登录密码',
  `email` varchar(300) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '电子邮箱',
  `mobile` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '手机号码',
  `phone` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '办公电话',
  `sex` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '用户性别',
  `avatar` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '头像路径',
  `sign` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '个性签名',
  `wx_openid` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '绑定的微信号',
  `mobile_imei` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '绑定的手机串号',
  `user_type` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '用户类型',
  `ref_code` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '用户类型引用编号',
  `ref_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '用户类型引用姓名',
  `mgr_type` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '管理员类型（0非管理员 1系统管理员  2二级管理员）',
  `pwd_security_level` decimal(1, 0) NULL DEFAULT NULL COMMENT '密码安全级别（0初始 1很弱 2弱 3安全 4很安全）',
  `pwd_update_date` datetime NULL DEFAULT NULL COMMENT '密码最后更新时间',
  `pwd_update_record` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '密码修改记录',
  `pwd_question` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '密保问题',
  `pwd_question_answer` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '密保问题答案',
  `pwd_question_2` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '密保问题2',
  `pwd_question_answer_2` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '密保问题答案2',
  `pwd_question_3` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '密保问题3',
  `pwd_question_answer_3` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '密保问题答案3',
  `pwd_quest_update_date` datetime NULL DEFAULT NULL COMMENT '密码问题修改时间',
  `last_login_ip` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '最后登陆IP',
  `last_login_date` datetime NULL DEFAULT NULL COMMENT '最后登陆时间',
  `freeze_date` datetime NULL DEFAULT NULL COMMENT '冻结时间',
  `freeze_cause` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '冻结原因',
  `user_weight` decimal(8, 0) NULL DEFAULT 0 COMMENT '用户权重（降序）',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '状态（0正常 1删除 2停用 3冻结）',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '创建者',
  `create_date` datetime NOT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '更新者',
  `update_date` datetime NOT NULL COMMENT '更新时间',
  `remarks` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '备注信息',
  `corp_code` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '0' COMMENT '租户代码',
  `corp_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'JeeSite' COMMENT '租户名称',
  `extend_s1` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '扩展 String 1',
  `extend_s2` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '扩展 String 2',
  `extend_s3` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '扩展 String 3',
  `extend_s4` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '扩展 String 4',
  `extend_s5` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '扩展 String 5',
  `extend_s6` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '扩展 String 6',
  `extend_s7` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '扩展 String 7',
  `extend_s8` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '扩展 String 8',
  `extend_i1` decimal(19, 0) NULL DEFAULT NULL COMMENT '扩展 Integer 1',
  `extend_i2` decimal(19, 0) NULL DEFAULT NULL COMMENT '扩展 Integer 2',
  `extend_i3` decimal(19, 0) NULL DEFAULT NULL COMMENT '扩展 Integer 3',
  `extend_i4` decimal(19, 0) NULL DEFAULT NULL COMMENT '扩展 Integer 4',
  `extend_f1` decimal(19, 4) NULL DEFAULT NULL COMMENT '扩展 Float 1',
  `extend_f2` decimal(19, 4) NULL DEFAULT NULL COMMENT '扩展 Float 2',
  `extend_f3` decimal(19, 4) NULL DEFAULT NULL COMMENT '扩展 Float 3',
  `extend_f4` decimal(19, 4) NULL DEFAULT NULL COMMENT '扩展 Float 4',
  `extend_d1` datetime NULL DEFAULT NULL COMMENT '扩展 Date 1',
  `extend_d2` datetime NULL DEFAULT NULL COMMENT '扩展 Date 2',
  `extend_d3` datetime NULL DEFAULT NULL COMMENT '扩展 Date 3',
  `extend_d4` datetime NULL DEFAULT NULL COMMENT '扩展 Date 4',
  PRIMARY KEY (`user_code`) USING BTREE,
  INDEX `idx_sys_user_lc`(`login_code` ASC) USING BTREE,
  INDEX `idx_sys_user_email`(`email` ASC) USING BTREE,
  INDEX `idx_sys_user_mobile`(`mobile` ASC) USING BTREE,
  INDEX `idx_sys_user_wo`(`wx_openid` ASC) USING BTREE,
  INDEX `idx_sys_user_imei`(`mobile_imei` ASC) USING BTREE,
  INDEX `idx_sys_user_rt`(`user_type` ASC) USING BTREE,
  INDEX `idx_sys_user_rc`(`ref_code` ASC) USING BTREE,
  INDEX `idx_sys_user_mt`(`mgr_type` ASC) USING BTREE,
  INDEX `idx_sys_user_us`(`user_weight` ASC) USING BTREE,
  INDEX `idx_sys_user_ud`(`update_date` ASC) USING BTREE,
  INDEX `idx_sys_user_status`(`status` ASC) USING BTREE,
  INDEX `idx_sys_user_cc`(`corp_code` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '用户表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of js_sys_user
-- ----------------------------
INSERT INTO `js_sys_user` VALUES ('admin', 'admin', '系统管理员', '3167464996d5f67aadc0f1870b3684ff86a503e4b23dda2917b619ef', '', '', '', '1', '/userfiles/avatar/0/none/admin.jpg', '', NULL, NULL, 'none', NULL, NULL, '1', 0, '2023-10-11 15:12:08', '[[\"bb4713b366594385c8f172d04fc661c8348667336cadfaea85ef6450\",\"2022-07-14 07:12:04\"]]', NULL, NULL, NULL, NULL, NULL, NULL, NULL, '171.92.155.26', '2024-01-13 16:06:29', NULL, NULL, 0, '1', 'system', '2019-01-15 10:56:01', 'system', '2024-05-23 17:59:55', '客户方使用的系统管理员，用于一些常用的基础数据配置。', '0', 'JeeSite', 'MMYWIZJQMI4WMLLCGUZTELJUGFSTELLCMM4DSLJUMU4WCZJXGMYTCY3GGPT3HO7HXOP6PLVB46IINZMRTA======', '', '', '', '', '', '', '', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `js_sys_user` VALUES ('system', 'system', '超级管理员', '905ad26ea707be5c2fb1947f5f814dd9dff8e3b12b07f724098d4d22', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'none', NULL, NULL, '0', 3, '2019-03-20 16:55:02', '[[\"905ad26ea707be5c2fb1947f5f814dd9dff8e3b12b07f724098d4d22\",\"2019-03-20 16:55:01\"]]', NULL, NULL, NULL, NULL, NULL, NULL, NULL, '0:0:0:0:0:0:0:1', '2026-01-13 15:37:19', NULL, NULL, 0, '0', 'system', '2019-01-15 10:55:54', 'system', '2019-01-15 10:56:00', '开发者使用的最高级别管理员，主要用于开发和调试。', '0', 'JeeSite', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);

-- ----------------------------
-- Table structure for js_sys_user_data_scope
-- ----------------------------
DROP TABLE IF EXISTS `js_sys_user_data_scope`;
CREATE TABLE `js_sys_user_data_scope`  (
  `user_code` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '控制用户编码',
  `ctrl_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '控制类型',
  `ctrl_data` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '控制数据',
  `ctrl_permi` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '控制权限',
  PRIMARY KEY (`user_code`, `ctrl_type`, `ctrl_data`, `ctrl_permi`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '用户数据权限表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of js_sys_user_data_scope
-- ----------------------------
INSERT INTO `js_sys_user_data_scope` VALUES ('system', 'Office', '1688_game_manager', '1');
INSERT INTO `js_sys_user_data_scope` VALUES ('system', 'Office', '1688_game_manager', '2');

-- ----------------------------
-- Table structure for js_sys_user_role
-- ----------------------------
DROP TABLE IF EXISTS `js_sys_user_role`;
CREATE TABLE `js_sys_user_role`  (
  `user_code` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '用户编码',
  `role_code` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '角色编码',
  PRIMARY KEY (`user_code`, `role_code`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '用户与角色关联表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of js_sys_user_role
-- ----------------------------

-- ----------------------------
-- Table structure for tbl_game_notice
-- ----------------------------
DROP TABLE IF EXISTS `tbl_game_notice`;
CREATE TABLE `tbl_game_notice`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '类型id',
  `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `index` int NULL DEFAULT NULL COMMENT '序号',
  `title` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '标题',
  `content` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '内容',
  `start_time` timestamp NULL DEFAULT NULL COMMENT '生效时间',
  `end_time` timestamp NULL DEFAULT NULL COMMENT '失效时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of tbl_game_notice
-- ----------------------------

-- ----------------------------
-- Table structure for test_data
-- ----------------------------
DROP TABLE IF EXISTS `test_data`;
CREATE TABLE `test_data`  (
  `id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '编号',
  `test_input` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '单行文本',
  `test_textarea` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '多行文本',
  `test_select` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '下拉框',
  `test_select_multiple` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '下拉多选',
  `test_radio` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '单选框',
  `test_checkbox` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '复选框',
  `test_date` datetime NULL DEFAULT NULL COMMENT '日期选择',
  `test_datetime` datetime NULL DEFAULT NULL COMMENT '日期时间',
  `test_user_code` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '用户选择',
  `test_office_code` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '机构选择',
  `test_area_code` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '区域选择',
  `test_area_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '区域名称',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '0' COMMENT '状态（0正常 1删除 2停用）',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '创建者',
  `create_date` datetime NOT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '更新者',
  `update_date` datetime NOT NULL COMMENT '更新时间',
  `remarks` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '备注信息',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '测试数据' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of test_data
-- ----------------------------

-- ----------------------------
-- Table structure for test_data_child
-- ----------------------------
DROP TABLE IF EXISTS `test_data_child`;
CREATE TABLE `test_data_child`  (
  `id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '编号',
  `test_sort` int NULL DEFAULT NULL COMMENT '排序号',
  `test_data_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '父表主键',
  `test_input` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '单行文本',
  `test_textarea` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '多行文本',
  `test_select` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '下拉框',
  `test_select_multiple` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '下拉多选',
  `test_radio` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '单选框',
  `test_checkbox` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '复选框',
  `test_date` datetime NULL DEFAULT NULL COMMENT '日期选择',
  `test_datetime` datetime NULL DEFAULT NULL COMMENT '日期时间',
  `test_user_code` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '用户选择',
  `test_office_code` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '机构选择',
  `test_area_code` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '区域选择',
  `test_area_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '区域名称',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '测试数据子表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of test_data_child
-- ----------------------------

-- ----------------------------
-- Table structure for test_tree
-- ----------------------------
DROP TABLE IF EXISTS `test_tree`;
CREATE TABLE `test_tree`  (
  `tree_code` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '节点编码',
  `parent_code` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '父级编号',
  `parent_codes` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '所有父级编号',
  `tree_sort` decimal(10, 0) NOT NULL COMMENT '本级排序号（升序）',
  `tree_sorts` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '所有级别排序号',
  `tree_leaf` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '是否最末级',
  `tree_level` decimal(4, 0) NOT NULL COMMENT '层次级别',
  `tree_names` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '全节点名',
  `tree_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '节点名称',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '0' COMMENT '状态（0正常 1删除 2停用）',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '创建者',
  `create_date` datetime NOT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '更新者',
  `update_date` datetime NOT NULL COMMENT '更新时间',
  `remarks` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '备注信息',
  PRIMARY KEY (`tree_code`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '测试树表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of test_tree
-- ----------------------------

SET FOREIGN_KEY_CHECKS = 1;
