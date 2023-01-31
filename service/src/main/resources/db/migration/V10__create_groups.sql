CREATE TABLE if not exists `groups` (
  `group_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `group_name` varchar(250) CHARACTER SET utf8mb4 DEFAULT NULL,
  `group_type` int(1) NOT NULL DEFAULT '1',
  `parent_id` bigint(20) DEFAULT NULL,
  `created_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `created_by` varchar(250) CHARACTER SET utf8mb4 NOT NULL DEFAULT 'system',
  `updated_time` datetime NOT NULL DEFAULT '0001-01-01 00:00:00',
  `updated_by` varchar(250) CHARACTER SET utf8mb4 DEFAULT NULL,
  `deleted_time` datetime NOT NULL DEFAULT '0001-01-01 00:00:00',
  `deleted_by` varchar(250) CHARACTER SET utf8mb4 DEFAULT NULL,
  PRIMARY KEY (`group_id`),
  KEY `idx_groupid_deleted` (`group_id`,`deleted_time`),
  KEY `idx_deleted_time` (`deleted_time`)
);