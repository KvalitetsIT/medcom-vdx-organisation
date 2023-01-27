CREATE TABLE if not exists `organisation` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `group_id` bigint(20) NOT NULL,
  `organisation_id` varchar(100) NOT NULL,
  `name` varchar(100) DEFAULT NULL,
  `deleted_time` datetime NOT NULL DEFAULT '0001-01-01 00:00:00',
  `pool_size` int(4) DEFAULT NULL,
  `sms_sender_name` varchar(11) DEFAULT NULL,
  `allow_custom_uri_without_domain` tinyint(1) NOT NULL DEFAULT '0',
  `sms_callback_url` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `organisation_id` (`organisation_id`),
  KEY `idx_group_id` (`id`,`group_id`),
  KEY `group_id` (`group_id`),
  CONSTRAINT `organisation_ibfk_1` FOREIGN KEY (`group_id`) REFERENCES `groups` (`group_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1583 DEFAULT CHARSET=latin1
