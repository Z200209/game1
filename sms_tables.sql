-- 创建短信记录表
DROP TABLE IF EXISTS `sms`;
CREATE TABLE `sms` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `phone` varchar(20) NOT NULL COMMENT '手机号',
  `content` text NOT NULL COMMENT '短信内容',
  `template_code` varchar(50) DEFAULT NULL COMMENT '模板编号',
  `template_param` text DEFAULT NULL COMMENT '模板参数',
  `status` tinyint unsigned NOT NULL DEFAULT '0' COMMENT '发送状态：0-发送中 1-成功 2-失败',
  `biz_id` varchar(100) DEFAULT NULL COMMENT '阿里云返回的业务ID',
  `create_time` int unsigned NOT NULL COMMENT '创建时间戳',
  `update_time` int unsigned NOT NULL COMMENT '更新时间戳',
  `send_time` int unsigned DEFAULT NULL COMMENT '发送时间戳',
  `is_deleted` tinyint unsigned NOT NULL DEFAULT '0' COMMENT '逻辑删除：0-未删除 1-已删除',
  PRIMARY KEY (`id`),
  KEY `idx_phone` (`phone`),
  KEY `idx_status` (`status`),
  KEY `idx_create_time` (`create_time`),
  KEY `idx_is_deleted` (`is_deleted`),
  KEY `idx_biz_id` (`biz_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='短信记录表';

DROP TABLE IF EXISTS `sms_task_crond`;
CREATE TABLE `sms_task_crond` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `phone` varchar(20) NOT NULL COMMENT '手机号',
  `template_param` text DEFAULT NULL COMMENT '模板参数',
  `status` tinyint unsigned NOT NULL DEFAULT '0' COMMENT '任务状态：0-初始 1-完成 2-失败',
  `execute_time` int unsigned DEFAULT NULL COMMENT '实际执行时间戳',
  `create_time` int unsigned NOT NULL COMMENT '创建时间戳',
  `update_time` int unsigned NOT NULL COMMENT '更新时间戳',
  `is_deleted` tinyint unsigned NOT NULL DEFAULT '0' COMMENT '逻辑删除标记：0-未删除 1-已删除',
  PRIMARY KEY (`id`),
  KEY `idx_phone` (`phone`),
  KEY `idx_status` (`status`),
  KEY `idx_create_time` (`create_time`),
  KEY `idx_is_deleted` (`is_deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='短信任务表';
