/*
 Navicat Premium Dump SQL

 Source Server         : localhost
 Source Server Type    : MySQL
 Source Server Version : 80039 (8.0.39)
 Source Host           : localhost:3306
 Source Schema         : game

 Target Server Type    : MySQL
 Target Server Version : 80039 (8.0.39)
 File Encoding         : 65001

 Date: 08/07/2025 18:08:57
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for article
-- ----------------------------
DROP TABLE IF EXISTS `article`;
CREATE TABLE `article`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT,
  `relate_user_id` bigint UNSIGNED NOT NULL DEFAULT 0 COMMENT '关联用户id',
  `title` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '标题',
  `cover_image` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '封面',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '内容',
  `tags` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '标签',
  `weight` int UNSIGNED NOT NULL DEFAULT 1 COMMENT '权重',
  `create_time` int UNSIGNED NOT NULL,
  `update_time` int UNSIGNED NOT NULL,
  `is_deleted` tinyint UNSIGNED NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_id`(`relate_user_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '文章表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of article
-- ----------------------------

-- ----------------------------
-- Table structure for game
-- ----------------------------
DROP TABLE IF EXISTS `game`;
CREATE TABLE `game`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT,
  `game_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '游戏名字',
  `price` float UNSIGNED NOT NULL COMMENT '价格',
  `game_introduction` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '游戏介绍',
  `game_date` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '发行日期',
  `game_publisher` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '发行商',
  `images` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '轮播图',
  `create_time` int UNSIGNED NOT NULL,
  `update_time` int UNSIGNED NOT NULL,
  `is_deleted` tinyint UNSIGNED NOT NULL DEFAULT 0,
  `type_id` bigint UNSIGNED NULL DEFAULT NULL COMMENT '关联类型表ID',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_type_id`(`type_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 62 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of game
-- ----------------------------
INSERT INTO `game` VALUES (1, 'Racing Champions V2', 69.99, 'Updated racing game description', '2025-03-15', 'SpeedSoft Studios', 'https://zzt3.oss-cn-beijing.aliyuncs.com/image/2025/03/08/96a4f6b764aa49a1be3ff28be83bca68_347x206.png$https://zzt3.oss-cn-beijing.aliyuncs.com/image/2025/03/08/96a4f6b764aa49a1be3ff28be83bca68_347x206.png', 1736219395, 1736304112, 0, 1);
INSERT INTO `game` VALUES (2, 'New Adventure Game', 39.99, 'An exciting new adventure game.', '2025-01-20', 'Adventure Games Inc.', 'http://www.adventure.com/1.jpg$http://www.adventure.com/2.jpg$http://www.adventure.com/3.jpg', 1736219481, 1736219481, 0, 1);
INSERT INTO `game` VALUES (3, 'Space Explorer', 69.99, 'Explore the universe and discover new worlds.', '2025-02-15', 'Galaxy Games Inc.', 'http://www.space.com/planet1.jpg$http://www.space.com/ship1.jpg$http://www.space.com/star1.jpg', 1736219507, 1736219507, 0, 1);
INSERT INTO `game` VALUES (4, 'Racing Champions', 59.99, 'Experience the thrill of competitive racing.', '2025-03-01', 'SpeedSoft Studios', 'http://www.racing.com/car1.jpg$http://www.racing.com/track1.jpg$http://www.racing.com/podium1.jpg', 1736219513, 1739113537, 0, 1);
INSERT INTO `game` VALUES (5, '新游戏称', 9.99, '这是一个新的游戏介绍', '2023-10-01', '游戏发行商', 'image_url', 1736501237, 1738384121, 0, 1);
INSERT INTO `game` VALUES (6, '   ', 69.99, 'Updated racing game description', '2025-03-15', 'SpeedSoft Studios', 'http://www.racing.com/car2.jpg,http://www.racing.com/track2.jpg', 1736501257, 1736501257, 0, 1);
INSERT INTO `game` VALUES (7, '  1111    ', 69.99, 'Updated racing game description', '2025-03-15', 'SpeedSoft Studios', 'http://www.racing.com/car2.jpg,http://www.racing.com/track2.jpg', 1736501303, 1736501303, 0, 1);
INSERT INTO `game` VALUES (8, '  1111    ', 69.99, 'Updated racing game description', '2025-03-15', 'SpeedSoft Studios', 'http://www.racing.com/car2.jpg$http://www.racing.com/track2.jpg', 1736501374, 1736501374, 0, 1);
INSERT INTO `game` VALUES (9, '1111    ', 69.99, 'Updated racing game description', '2025-03-15', 'SpeedSoft Studios', 'http://www.racing.com/car2.jpg,http://www.racing.com/track2.jpg', 1736501389, 1736501389, 0, 2);
INSERT INTO `game` VALUES (10, '新游戏称', 9.99, '这是一个新的游戏介绍', '2023-10-01', '游戏发行商', 'image_url', 1736501723, 1738383966, 0, 2);
INSERT INTO `game` VALUES (11, 'Super Mario', 59.99, '经典的动作冒陪游戏', '2023-12-15', '任天堂', 'http://example.com/images/supermario.jpg', 1736557760, 1736557760, 0, 2);
INSERT INTO `game` VALUES (12, '11', 59.99, '经典的动作冒陪游戏', '2023-12-15', '任天堂', 'http://example.com/images/supermario.jpg', 1736558067, 1736558067, 0, 2);
INSERT INTO `game` VALUES (13, '测试游戏', 59.99, '这是一款测试游戏', '2025-01-13', '测试发行商', '测试图片路径', 1736760191, 1736760191, 0, 2);
INSERT INTO `game` VALUES (14, '测试游戏', 59.99, '这是一款测试游戏', '2025-01-13', '测试发行商', '测试图片路径', 1736760222, 1736760222, 0, 2);
INSERT INTO `game` VALUES (15, '1111', 9.99, '这是一个新的游戏介绍', '2023-10-01', '游戏发行商', 'image_url', 1736773555, 1738463251, 0, 2);
INSERT INTO `game` VALUES (16, '\" \"', 59.99, '测试游戏介绍', '2025-01-13', '测试发行商', '图片路径', 1736773572, 1737731352, 1, 2);
INSERT INTO `game` VALUES (18, '新游戏称', 9.99, '这是一个新的游戏介绍', '2023-10-01', '游戏发行商', 'image_url', 1737731354, 1737731354, 0, 2);
INSERT INTO `game` VALUES (19, '新游戏名称', 9.99, '这是一个新的游戏介绍', '2023-10-01', '游戏发行商', 'image_url', 1737984601, 1737984601, 0, 2);
INSERT INTO `game` VALUES (20, '59599', 0, '[   {     \"type\": \"text\",     \"content\": \"这是游戏的文字介绍\"   },   {     \"type\": \"image\",     \"content\": \"https://example.com/game-image.jpg\"   },   {     \"type\": \"video\",     \"content\": \"https://example.com/gameplay-video.mp4\"   } ]', '000', '000000', '000000', 1737984601, 1745727712, 0, 1);
INSERT INTO `game` VALUES (21, '新游戏名称', 9.99, '这是一个新的游戏介绍', '2023-10-01', '游戏发行商', 'image_url', 1737984602, 1737984602, 0, 2);
INSERT INTO `game` VALUES (22, '111111', 11111, '[   {     \"type\": \"text\",     \"content\": \"这是游戏的文字介绍\"   },   {     \"type\": \"image\",     \"content\": \"https://example.com/game-image.jpg\"   },   {     \"type\": \"video\",     \"content\": \"https://example.com/gameplay-video.mp4\"   } ]', '00000', '00000', '0000', 1737984602, 1745727929, 0, 1);
INSERT INTO `game` VALUES (23, '新游戏名称', 9.99, '这是一个新的游戏介绍', '2023-10-01', '游戏发行商', 'image_url', 1737984729, 1737984729, 0, 1);
INSERT INTO `game` VALUES (24, '新游戏名称', 9.99, '这是一个新的游戏介绍', '2023-10-01', '游戏发行商', 'image_url', 1737984729, 1737984729, 0, 2);
INSERT INTO `game` VALUES (25, '新游戏称', 9.99, '这是一个新的游戏介绍', '2023-10-01', '游戏发行商', 'image_url', 1737984729, 1738382818, 0, 2);
INSERT INTO `game` VALUES (26, '新游戏名称', 9.99, '这是一个新的游戏介绍', '2023-10-01', '游戏发行商', 'image_url', 1737984729, 1737984729, 0, 2);
INSERT INTO `game` VALUES (27, '新游戏名称', 9.99, '这是一个新的游戏介绍', '2023-10-01', '游戏发行商', 'image_url', 1737984731, 1737984731, 0, 1);
INSERT INTO `game` VALUES (28, '新游戏名称', 9.99, '这是一个新的游戏介绍', '2023-10-01', '游戏发行商', 'image_url', 1737984731, 1737984731, 0, 2);
INSERT INTO `game` VALUES (29, '新游戏名称', 9.99, '这是一个新的游戏介绍', '2023-10-01', '游戏发行商', 'image_url', 1737984731, 1737984731, 0, 1);
INSERT INTO `game` VALUES (30, '新游戏称', 9.99, '这是一个新的游戏介绍', '2023-10-01', '游戏发行商', 'image_url', 1737984731, 1738382811, 0, 2);
INSERT INTO `game` VALUES (31, '新游戏名称', 9.99, '这是一个新的游戏介绍', '2023-10-01', '游戏发行商', 'image_url', 1737985343, 1737985343, 0, 2);
INSERT INTO `game` VALUES (32, '新游戏名称', 9.99, '这是一个新的游戏介绍', '2023-10-01', '游戏发行商', 'image_url', 1737985343, 1737985343, 0, 2);
INSERT INTO `game` VALUES (33, '新游戏名称', 9.99, '这是一个新的游戏介绍', '2023-10-01', '游戏发行商', 'image_url', 1737985344, 1737985344, 0, 2);
INSERT INTO `game` VALUES (34, '新游戏名称', 9.99, '这是一个新的游戏介绍', '2023-10-01', '游戏发行商', 'image_url', 1737985344, 1737985344, 0, 1);
INSERT INTO `game` VALUES (35, '新游戏名称', 9.99, '这是一个新的游戏介绍', '2023-10-01', '游戏发行商', 'image_url', 1737985591, 1737985591, 0, 1);
INSERT INTO `game` VALUES (36, '新游戏名称', 9.99, '这是一个新的游戏介绍', '2023-10-01', '游戏发行商', 'image_url', 1737985592, 1737985592, 0, 2);
INSERT INTO `game` VALUES (37, '新游戏名称', 9.99, '这是一个新的游戏介绍', '2023-10-01', '游戏发行商', 'image_url', 1737985594, 1737985594, 0, 1);
INSERT INTO `game` VALUES (38, '新游戏称', 9.99, '这是一个新的游戏介绍', '2023-10-01', '游戏发行商', 'image_url', 1738384110, 1738384110, 0, 2);
INSERT INTO `game` VALUES (39, '新游戏称', 9.99, '这是一个新的游戏介绍', '2023-10-01', '游戏发行商', 'image_url', 1738461849, 1738461849, 0, 2);
INSERT INTO `game` VALUES (40, '111', 9.99, '这是一个新的游戏介绍', '2023-10-01', '游戏发行商', 'image_url', 1738462301, 1738462301, 0, 1);
INSERT INTO `game` VALUES (41, '111', 9.99, '这是一个新的游戏介绍', '2023-10-01', '游戏发行商', 'image_url', 1738463277, 1738463277, 0, 2);
INSERT INTO `game` VALUES (42, '111', 9.99, '这是一个新的游戏介绍', '2023-10-01', '游戏发行商', 'image_url', 1738463278, 1738463278, 0, 1);
INSERT INTO `game` VALUES (43, '111', 9.99, '这是一个新的游戏介绍', '2023-10-01', '游戏发行商', 'image_url', 1738548450, 1738548450, 0, 2);
INSERT INTO `game` VALUES (44, '111', 9.99, '这是一个新的游戏介绍', '2023-10-01', '游戏发行商', 'image_url', 1738665346, 1738665346, 0, 1);
INSERT INTO `game` VALUES (45, '111', 9.99, '这是一个新的游戏介绍', '2023-10-01', '游戏发行商', 'image_url', 1738665659, 1738665659, 0, 2);
INSERT INTO `game` VALUES (46, '11', 9.99, '这是一个新的游戏介绍', '2023-10-01', '游戏发行商', 'image_url', 1739113487, 1739114140, 0, NULL);
INSERT INTO `game` VALUES (47, '11', 9.99, '这是一个新的游戏介绍', '2023-10-01', '游戏发行商', 'image_url', 1739113490, 1739114140, 0, NULL);
INSERT INTO `game` VALUES (48, '11', 9.99, '这是一个新的游戏介绍', '2023-10-01', '游戏发行商', 'image_url', 1739113490, 1739114140, 0, NULL);
INSERT INTO `game` VALUES (49, '11', 9.99, '这是一个新的游戏介绍', '2023-10-01', '游戏发行商', 'image_url', 1739113490, 1739114140, 0, NULL);
INSERT INTO `game` VALUES (50, '11', 9.99, '这是一个新的游戏介绍', '2023-10-01', '游戏发行商', 'image_url', 1739113490, 1739114140, 0, NULL);
INSERT INTO `game` VALUES (51, '11', 9.99, '这是一个新的游戏介绍', '2023-10-01', '游戏发行商', 'image_url', 1739116329, 1739116329, 0, 3);
INSERT INTO `game` VALUES (52, '11', 9.99, '这是一个新的游戏介绍', '2023-10-01', '游戏发行商', 'image_url', 1739116353, 1739116353, 0, 1);
INSERT INTO `game` VALUES (53, '3', 3, '{\n    \"Blocks\": [\n        {\n            \"order\": 1,\n            \"type\": \"TEXT\",\n            \"content\": \"这是第一个区块的内容\"\n        },\n        {\n            \"order\": 2,\n            \"type\": \"IMAGE\",\n            \"content\": \"这是第二个区块的内容\"\n        }\n    ]\n}', '2', '3', '3', 1742740116, 1742740116, 0, 1);
INSERT INTO `game` VALUES (54, '1', 1, '{\n    \"blocks\": [\n        {\n            \"order\": 1,\n            \"type\": \"text\",\n            \"content\": \"这是一段文本介绍\"\n        },\n        {\n            \"order\": 2,\n            \"type\": \"image\",\n            \"content\": \"https://example.com/image1.jpg\"\n        },\n        {\n            \"order\": 3,\n            \"type\": \"video\",\n            \"content\": \"https://example.com/video1.mp4\"\n        }\n    ]\n}\n', '2', '3', '4', 1742743309, 1742743309, 0, 1);
INSERT INTO `game` VALUES (55, 'name', 1, '[   {     \"type\": \"text\",     \"content\": \"这是一个精彩刺激的动作冒险游戏\"   },   {     \"type\": \"image\",     \"content\": \"https://example.com/images/game_screenshot1.jpg\"   },   {     \"type\": \"text\",     \"content\": \"游戏特色包括丰富的角色系统和开放世界探索\"   },   {     \"type\": \"video\",     \"content\": \"https://example.com/videos/gameplay_demo.mp4\"   } ]', '2', '2', '2', 1745407234, 1745407234, 0, 1);
INSERT INTO `game` VALUES (56, 'name', 1, '[   {     \"type\": \"text\",     \"content\": \"这是游戏的文字介绍\"   },   {     \"type\": \"image\",     \"content\": \"https://example.com/game-image.jpg\"   },   {     \"type\": \"video\",     \"content\": \"https://example.com/gameplay-video.mp4\"   } ]', '2', '2', '2', 1745407376, 1745407376, 0, 1);
INSERT INTO `game` VALUES (57, 'name', 1, '[   {     \"type\": \"text\",     \"content\": \"这是游戏的文字介绍\"   },   {     \"type\": \"image\",     \"content\": \"https://example.com/game-image.jpg\"   },   {     \"type\": \"video\",     \"content\": \"https://example.com/gameplay-video.mp4\"   } ]', '2', '2', '2', 1745407381, 1745407381, 0, 1);
INSERT INTO `game` VALUES (58, 'name', 1, '[   {     \"type\": \"text\",     \"content\": \"这是游戏的文字介绍\"   },   {     \"type\": \"image\",     \"content\": \"https://example.com/game-image.jpg\"   },   {     \"type\": \"video\",     \"content\": \"https://example.com/gameplay-video.mp4\"   } ]', '2', '2', '2', 1745407382, 1745408017, 0, 1);
INSERT INTO `game` VALUES (59, '00', 0, '[   {     \"type\": \"text\",     \"content\": \"这是游戏的文字介绍\"   },   {     \"type\": \"image\",     \"content\": \"https://example.com/game-image.jpg\"   },   {     \"type\": \"video\",     \"content\": \"https://example.com/gameplay-video.mp4\"   } ]', '000', '00', '0000', 1745716886, 1745716886, 0, NULL);
INSERT INTO `game` VALUES (60, '0000000', 0, '[   {     \"type\": \"text\",     \"content\": \"这是游戏的文字介绍\"   },   {     \"type\": \"image\",     \"content\": \"https://example.com/game-image.jpg\"   },   {     \"type\": \"video\",     \"content\": \"https://example.com/gameplay-video.mp4\"   } ]', '0000', '00000', '000', 1745726979, 1745726979, 0, NULL);
INSERT INTO `game` VALUES (61, '0000000', 0, '[   {     \"type\": \"text\",     \"content\": \"这是游戏的文字介绍\"   },   {     \"type\": \"image\",     \"content\": \"https://example.com/game-image.jpg\"   },   {     \"type\": \"video\",     \"content\": \"https://example.com/gameplay-video.mp4\"   } ]', '0000', '00000', '000', 1745727036, 1745727036, 0, NULL);

-- ----------------------------
-- Table structure for game_tag_relation
-- ----------------------------
DROP TABLE IF EXISTS `game_tag_relation`;
CREATE TABLE `game_tag_relation`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT,
  `game_id` bigint UNSIGNED NOT NULL COMMENT '游戏ID',
  `tag_id` bigint UNSIGNED NOT NULL COMMENT '标签ID',
  `create_time` int UNSIGNED NOT NULL COMMENT '创建时间',
  `update_time` int UNSIGNED NOT NULL COMMENT '更新时间',
  `is_deleted` tinyint UNSIGNED NOT NULL DEFAULT 0 COMMENT '是否删除 1-是 0-否',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_game_tag`(`game_id` ASC, `tag_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 18 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '游戏标签关联表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of game_tag_relation
-- ----------------------------
INSERT INTO `game_tag_relation` VALUES (1, 55, 1, 1745407234, 1745407234, 0);
INSERT INTO `game_tag_relation` VALUES (2, 56, 1, 1745407376, 1745407376, 0);
INSERT INTO `game_tag_relation` VALUES (3, 57, 1, 1745407381, 1745407381, 0);
INSERT INTO `game_tag_relation` VALUES (4, 58, 1, 1745407382, 1745408017, 1);
INSERT INTO `game_tag_relation` VALUES (5, 58, 2, 1745408017, 1745408017, 0);
INSERT INTO `game_tag_relation` VALUES (6, 59, 3, 1745716887, 1745716887, 0);
INSERT INTO `game_tag_relation` VALUES (7, 59, 4, 1745716887, 1745716887, 0);
INSERT INTO `game_tag_relation` VALUES (8, 59, 5, 1745716887, 1745716887, 0);
INSERT INTO `game_tag_relation` VALUES (9, 59, 6, 1745716887, 1745716887, 0);
INSERT INTO `game_tag_relation` VALUES (10, 59, 7, 1745716887, 1745716887, 0);
INSERT INTO `game_tag_relation` VALUES (11, 60, 8, 1745727004, 1745727004, 0);
INSERT INTO `game_tag_relation` VALUES (12, 61, 8, 1745727082, 1745727082, 0);
INSERT INTO `game_tag_relation` VALUES (13, 20, 9, 1745727770, 1745727770, 0);
INSERT INTO `game_tag_relation` VALUES (14, 22, 3, 1745727929, 1745727929, 0);
INSERT INTO `game_tag_relation` VALUES (15, 22, 5, 1745727929, 1745727929, 0);
INSERT INTO `game_tag_relation` VALUES (16, 22, 6, 1745727929, 1745727929, 0);
INSERT INTO `game_tag_relation` VALUES (17, 22, 4, 1745727929, 1745727929, 0);

-- ----------------------------
-- Table structure for sms
-- ----------------------------
DROP TABLE IF EXISTS `sms`;
CREATE TABLE `sms`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '手机号',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '短信内容',
  `template_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '模板编号',
  `template_param` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '模板参数',
  `status` tinyint UNSIGNED NOT NULL DEFAULT 0 COMMENT '发送状态：0-发送中 1-成功 2-失败',
  `biz_id` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '阿里云返回的业务ID',
  `create_time` int UNSIGNED NOT NULL COMMENT '创建时间戳',
  `update_time` int UNSIGNED NOT NULL COMMENT '更新时间戳',
  `send_time` int UNSIGNED NULL DEFAULT NULL COMMENT '发送时间戳',
  `is_deleted` tinyint UNSIGNED NOT NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除 1-已删除',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_phone`(`phone` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE,
  INDEX `idx_create_time`(`create_time` ASC) USING BTREE,
  INDEX `idx_is_deleted`(`is_deleted` ASC) USING BTREE,
  INDEX `idx_biz_id`(`biz_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 28 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '短信记录表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sms
-- ----------------------------
INSERT INTO `sms` VALUES (1, '18735706997', 'SMS_154950909', 'SMS_154950909', 'SMS_154950909', 2, NULL, 1751112047, 1751112047, 1751112047, 0);
INSERT INTO `sms` VALUES (2, '18735706997', 'SMS_154950909', 'SMS_154950909', 'SMS_154950909', 2, NULL, 1751112210, 1751112210, 1751112210, 0);
INSERT INTO `sms` VALUES (3, '18735706997', 'SMS_321825144', 'SMS_321825144', 'SMS_321825144', 2, NULL, 1751113061, 1751113061, 1751113061, 0);
INSERT INTO `sms` VALUES (4, '18735706997', 'SMS_321825144', 'SMS_321825144', 'SMS_321825144', 2, NULL, 1751113102, 1751113102, 1751113102, 0);
INSERT INTO `sms` VALUES (5, '18735706997', '123456', 'SMS_321825144', '123456', 2, NULL, 1751113311, 1751113311, 1751113311, 0);
INSERT INTO `sms` VALUES (6, '18735706997', '{\"code\":\"123456\"}', 'SMS_321825144', '{\"code\":\"123456\"}', 2, NULL, 1751113360, 1751113360, 1751113360, 0);
INSERT INTO `sms` VALUES (7, '18735706997', '{\"code\":\"123456\"}', 'SMS_154950909', '{\"code\":\"123456\"}', 2, NULL, 1751113615, 1751113615, 1751113615, 0);
INSERT INTO `sms` VALUES (8, '18735706997', '{\"code\":\"123456\"}', 'SMS_154950909', '{\"code\":\"123456\"}', 2, NULL, 1751114445, 1751114445, 1751114445, 0);
INSERT INTO `sms` VALUES (9, '18735706997', '{\"code\":\"123456\"}', 'SMS_154950909', '{\"code\":\"123456\"}', 2, NULL, 1751116269, 1751116269, 1751116269, 0);
INSERT INTO `sms` VALUES (10, '18735706997', '{\"code\":\"123456\"}', 'SMS_154950909', '{\"code\":\"123456\"}', 2, NULL, 1751116326, 1751116326, 1751116326, 0);
INSERT INTO `sms` VALUES (11, '18735706997', '{\"code\":\"123456\"}', 'SMS_154950909', '{\"code\":\"123456\"}', 2, NULL, 1751116375, 1751116375, 1751116375, 0);
INSERT INTO `sms` VALUES (12, '18735706997', '{\"code\":\"123456\"}', 'SMS_154950909', '{\"code\":\"123456\"}', 2, NULL, 1751116657, 1751116657, 1751116657, 0);
INSERT INTO `sms` VALUES (13, '18735706997', '{\"code\":\"123456\"}', 'SMS_154950909', '{\"code\":\"123456\"}', 2, NULL, 1751118482, 1751118482, 1751118482, 0);
INSERT INTO `sms` VALUES (14, '18735706997', '{\"code\":\"1234\"}', 'SMS_154950909', '{\"code\":\"1234\"}', 2, NULL, 1751118487, 1751118487, 1751118487, 0);
INSERT INTO `sms` VALUES (15, '18735706997', '{\"code\":\"1234\"}', 'SMS_154950909', '{\"code\":\"1234\"}', 2, NULL, 1751118815, 1751118815, 1751118815, 0);
INSERT INTO `sms` VALUES (16, '18735706997', '{\"code\":\"1234\"}', 'SMS_154950909', '{\"code\":\"1234\"}', 2, NULL, 1751118873, 1751118873, 1751118873, 0);
INSERT INTO `sms` VALUES (17, '18735706997', '{\"code\":\"1234\"}', 'SMS_154950909', '{\"code\":\"1234\"}', 2, NULL, 1751118926, 1751118926, 1751118926, 0);
INSERT INTO `sms` VALUES (18, '18735706997', '{\"code\":\"1234\"}', 'SMS_154950909', '{\"code\":\"1234\"}', 2, NULL, 1751119167, 1751119167, 1751119167, 0);
INSERT INTO `sms` VALUES (19, '18735706997', '{\"code\":\"1234\"}', 'SMS_154950909', '{\"code\":\"1234\"}', 1, '829305651121407199^0', 1751121406, 1751121406, 1751121406, 0);
INSERT INTO `sms` VALUES (20, '18735706997', '{\"code\":\"1234\"}', 'SMS_154950909', '{\"code\":\"1234\"}', 1, '617117351126257680^0', 1751126257, 1751126257, 1751126257, 0);
INSERT INTO `sms` VALUES (21, '18735706997', '{\"code\":\"123456\"}', 'SMS_154950909', '{\"code\":\"123456\"}', 1, '659124051126580759^0', 1751126580, 1751126580, 1751126580, 0);
INSERT INTO `sms` VALUES (22, '18735706997', '{\"code\":\"123456\"}', 'SMS_154950909', '{\"code\":\"123456\"}', 1, '647605651126761684^0', 1751126761, 1751126761, 1751126761, 0);
INSERT INTO `sms` VALUES (23, '18735706997', '{\"code\":\"123456\"}', 'SMS_154950909', '{\"code\":\"123456\"}', 2, NULL, 1751126761, 1751126761, 1751126761, 0);
INSERT INTO `sms` VALUES (24, '18735706997', 'SMS_154950909', 'SMS_154950909', 'SMS_154950909', 2, NULL, 1751271480, 1751271480, 1751271480, 0);
INSERT INTO `sms` VALUES (25, '18735706997', '{\"code\":\"1234\"}', 'SMS_154950909', '{\"code\":\"1234\"}', 1, '175025251272443344^0', 1751272440, 1751272440, 1751272440, 0);
INSERT INTO `sms` VALUES (26, '18735706997', '{\"code\":\"1234\"}', 'SMS_154950909', '{\"code\":\"1234\"}', 1, '504417751273248015^0', 1751273245, 1751273245, 1751273245, 0);
INSERT INTO `sms` VALUES (27, '18735706997', '{\"code\":\"1234\"}', 'SMS_154950909', '{\"code\":\"1234\"}', 2, NULL, 1751273245, 1751273245, 1751273245, 0);

-- ----------------------------
-- Table structure for sms_task_crond
-- ----------------------------
DROP TABLE IF EXISTS `sms_task_crond`;
CREATE TABLE `sms_task_crond`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '手机号',
  `template_param` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '模板参数',
  `status` tinyint UNSIGNED NOT NULL DEFAULT 0 COMMENT '任务状态：0-初始 1-完成 2-失败',
  `execute_time` int UNSIGNED NULL DEFAULT NULL COMMENT '实际执行时间戳',
  `create_time` int UNSIGNED NOT NULL COMMENT '创建时间戳',
  `update_time` int UNSIGNED NOT NULL COMMENT '更新时间戳',
  `is_deleted` tinyint UNSIGNED NOT NULL DEFAULT 0 COMMENT '逻辑删除标记：0-未删除 1-已删除',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_phone`(`phone` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE,
  INDEX `idx_create_time`(`create_time` ASC) USING BTREE,
  INDEX `idx_is_deleted`(`is_deleted` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '短信任务表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sms_task_crond
-- ----------------------------
INSERT INTO `sms_task_crond` VALUES (1, '18735706997', 'SMS_154950909', 2, 1751270880, 1751270845, 1751270880, 0);
INSERT INTO `sms_task_crond` VALUES (2, '18735706997', 'SMS_154950909', 2, 1751271180, 1751271132, 1751271180, 0);
INSERT INTO `sms_task_crond` VALUES (3, '18735706997', 'SMS_154950909', 2, 1751271480, 1751271437, 1751271480, 0);
INSERT INTO `sms_task_crond` VALUES (4, '18735706997', '{\"code\":\"1234\"}', 1, 1751272440, 1751272409, 1751272440, 0);
INSERT INTO `sms_task_crond` VALUES (5, '18735706997', '{\"code\":\"1234\"}', 1, 1751273245, 1751273193, 1751273245, 0);
INSERT INTO `sms_task_crond` VALUES (6, '18735706997', '{\"code\":\"1234\"}', 2, 1751273245, 1751273195, 1751273245, 0);

-- ----------------------------
-- Table structure for tag
-- ----------------------------
DROP TABLE IF EXISTS `tag`;
CREATE TABLE `tag`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT,
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '标签名称',
  `create_time` int UNSIGNED NOT NULL COMMENT '创建时间',
  `update_time` int UNSIGNED NOT NULL COMMENT '更新时间',
  `is_deleted` tinyint UNSIGNED NOT NULL DEFAULT 0 COMMENT '是否删除 1-是 0-否',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_name`(`name` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 10 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '标签表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of tag
-- ----------------------------
INSERT INTO `tag` VALUES (1, '冒险，经营，111，222', 1745407234, 1745407234, 0);
INSERT INTO `tag` VALUES (2, '冒险，经营，111', 1745408017, 1745408017, 0);
INSERT INTO `tag` VALUES (3, '0', 1745716887, 1745716887, 0);
INSERT INTO `tag` VALUES (4, '3', 1745716887, 1745716887, 0);
INSERT INTO `tag` VALUES (5, '1', 1745716887, 1745716887, 0);
INSERT INTO `tag` VALUES (6, '2', 1745716887, 1745716887, 0);
INSERT INTO `tag` VALUES (7, '4', 1745716887, 1745716887, 0);
INSERT INTO `tag` VALUES (8, '00，001，002', 1745727004, 1745727004, 0);
INSERT INTO `tag` VALUES (9, '001，002', 1745727769, 1745727769, 0);

-- ----------------------------
-- Table structure for type
-- ----------------------------
DROP TABLE IF EXISTS `type`;
CREATE TABLE `type`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT,
  `type_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '类型名称',
  `image` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '类型图片',
  `create_time` int UNSIGNED NOT NULL COMMENT '创建时间',
  `update_time` int UNSIGNED NOT NULL COMMENT '更新时间',
  `is_deleted` tinyint UNSIGNED NOT NULL DEFAULT 0 COMMENT '软删除标记',
  `parent_id` bigint UNSIGNED NULL DEFAULT NULL COMMENT 'Parent Type ID',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_type_name`(`type_name` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 20 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of type
-- ----------------------------
INSERT INTO `type` VALUES (1, '冒险', '1', 1739107205, 1739107205, 0, NULL);
INSERT INTO `type` VALUES (2, '卡牌', '1', 1739107392, 1739107392, 0, 1);
INSERT INTO `type` VALUES (3, '1739334256', '22', 1739113238, 1739191517, 0, 2);
INSERT INTO `type` VALUES (4, '卡', '1', 1739113239, 1739191251, 0, 3);
INSERT INTO `type` VALUES (5, '动作', '1', 1739189388, 1739189388, 0, 4);
INSERT INTO `type` VALUES (6, '动', '1', 1739190022, 1739190022, 0, 5);
INSERT INTO `type` VALUES (7, '经营', '1', 1739191514, 1739191514, 0, 6);
INSERT INTO `type` VALUES (8, '模拟', '56', 1739333010, 1739334259, 0, NULL);
INSERT INTO `type` VALUES (12, 'jy', '1', 1739343018, 1739343018, 0, 8);
INSERT INTO `type` VALUES (14, '111', '1', 1739357819, 1739357819, 0, NULL);
INSERT INTO `type` VALUES (18, 'sss11', '1', 1739700607, 1739700607, 0, NULL);
INSERT INTO `type` VALUES (19, 'ssss11', '1', 1739700627, 1739700627, 0, 1);

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '用户id',
  `country_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '86',
  `phone` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '用户手机号码',
  `email` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '注册邮箱号',
  `username` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '用户昵称',
  `password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '密码',
  `avatar` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '用户头像url',
  `personal_profile` varchar(2000) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '个人简介',
  `cover_image` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '用户背景封面',
  `gender` tinyint UNSIGNED NOT NULL COMMENT '性别，1-男，2-女',
  `birthday` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '生日 1999-01-01',
  `wechat_open_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '微信',
  `wechat_union_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `wechat_no` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '微信号',
  `country` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `province` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `city` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `register_time` int UNSIGNED NOT NULL COMMENT '记录用户加入时间',
  `register_ip` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '注册ip',
  `last_login_time` int UNSIGNED NOT NULL COMMENT '最后登录时间',
  `last_login_ip` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '最后登录ip',
  `is_ban` tinyint UNSIGNED NOT NULL DEFAULT 0 COMMENT '是否禁用1-是0-否',
  `extra` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT 'json',
  `create_time` int UNSIGNED NOT NULL COMMENT '创建时间',
  `update_time` int UNSIGNED NOT NULL COMMENT '修改时间',
  `is_deleted` tinyint UNSIGNED NOT NULL DEFAULT 0 COMMENT '是否删除 1-是 0-否',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_phone_country_code`(`phone` ASC, `country_code` ASC) USING BTREE,
  INDEX `idx_username`(`username` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '用户表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user
-- ----------------------------
INSERT INTO `user` VALUES (1, '86', '18966156613', NULL, 'mlzadmin', 'f94db5c2cb86b216fe75faa920697efd', 'https://nftcn.oss-cn-shanghai.aliyuncs.com/photo/avatar.png', NULL, NULL, 1, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1671035411, '0:0:0:0:0:0:0:1', 1671035411, '0:0:0:0:0:0:0:1', 0, NULL, 1671035411, 1671035411, 0);
INSERT INTO `user` VALUES (2, '86', '18966156612', NULL, 'mlzadmin2', 'f94db5c2cb86b216fe75faa920697efd', 'https://nftcn.oss-cn-shanghai.aliyuncs.com/photo/avatar.png', NULL, NULL, 1, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1671096992, '0:0:0:0:0:0:0:1', 1671110744, '192.168.0.21', 0, NULL, 1671096992, 1671110744, 0);
INSERT INTO `user` VALUES (3, '86', '13800138000', NULL, '测试用户', '03f890e3cd1fecbe222f21344775b5ea', '/photo/avatar.png', NULL, NULL, 1, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1742700709, '0:0:0:0:0:0:0:1', 1742700953, '0:0:0:0:0:0:0:1', 0, NULL, 1742700709, 1742700953, 0);
INSERT INTO `user` VALUES (4, '86', '13800138001', NULL, '测试用户2', '03f890e3cd1fecbe222f21344775b5ea', '/photo/avatar.png', NULL, NULL, 1, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1742700775, '0:0:0:0:0:0:0:1', 1742700775, '0:0:0:0:0:0:0:1', 0, NULL, 1742700775, 1742700775, 0);
INSERT INTO `user` VALUES (5, '86', '19581546997', NULL, '1', 'f94db5c2cb86b216fe75faa920697efd', 'https://example.com/avatar.png', NULL, NULL, 1, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1742707424, '0:0:0:0:0:0:0:1', 1742733364, '0:0:0:0:0:0:0:1', 0, NULL, 1742707424, 1742733364, 0);
INSERT INTO `user` VALUES (6, '86', '18735706997', NULL, '091335', 'cf4a1fa3696e8f5bdbbdd35fa228cc2d', '/photo/avatar.png', NULL, NULL, 1, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1742717213, '0:0:0:0:0:0:0:1', 1742723012, '192.168.10.59', 0, NULL, 1742717213, 1742723012, 0);

-- ----------------------------
-- Table structure for user1
-- ----------------------------
DROP TABLE IF EXISTS `user1`;
CREATE TABLE `user1`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '手机号',
  `password` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '密码',
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '用户名称',
  `avatar` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '头像URL',
  `create_time` int NULL DEFAULT NULL COMMENT '创建时间',
  `update_time` int NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_phone`(`phone` ASC) USING BTREE COMMENT '手机号唯一索引'
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户信息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user1
-- ----------------------------
INSERT INTO `user1` VALUES (1, '19581546998', '$2a$10$tr406072P66vieZcPGwSGexQE6fAvgu0EoSbcACNn5qkgYUK/rBZK', '22', '1', 1742749111, 1742749111);
INSERT INTO `user1` VALUES (2, '19581546997', '$2a$10$8Jbayj8oX98MzKV73h1IWutopm7zd9cHh/Qa09lz9MtQjJu9bip6e', '22', '1', 1742749981, 1742749981);
INSERT INTO `user1` VALUES (3, '18735706987', '$2a$10$BxmloP9F6n9Zzf7pO4ldmOTxxkfybQ8.rLe5ryibVD0Q9Q5.5o1O2', '000', '111', 1745404023, 1745404023);
INSERT INTO `user1` VALUES (4, '18735706991', '$2a$10$UtgTvm5pChlvIPncb5w0UuS6Th0vnAc.VzcRQEkh3wLN17ctc0wDy', '1000', '1111', 1750843928, 1750843928);

SET FOREIGN_KEY_CHECKS = 1;
