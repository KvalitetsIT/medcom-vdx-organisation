CREATE OR REPLACE VIEW view_groups AS
select
    groups.group_id AS group_id,
    groups.parent_id AS parent_id,
    if((not(exists(
    select
        organisation.id
    from
        organisation
    where
        (groups.group_id = organisation.group_id)))),
    groups.group_name,
    convert(organisation.name
        using utf8mb4)) AS group_name,
    groups.group_type AS group_type,
    if((groups.group_type = 1),
    'group',
    if((groups.group_type = 2),
    'organisation',
    if((groups.group_type = 3),
    'praksis',
    ''))) AS group_type_name,
    if((groups.deleted_time > '0001-01-01'),
    1,
    0) AS Deleted,
    organisation.id AS organisation_id,
    organisation.organisation_id AS organisation_id_name,
    groups.created_time AS created_time,
    groups.created_by AS created_by,
    groups.updated_time AS updated_time,
    groups.updated_by AS updated_by,
    groups.deleted_time AS deleted_time,
    groups.deleted_by AS deleted_by
from
    (groups
left join organisation on
    ((groups.group_id = organisation.group_id)))
order by
    groups.group_id;

-- view_entities_meetingroom - tables and view.

CREATE TABLE `entities_meetingroom_alias` (
  `uuid` varchar(36) COLLATE utf8mb4_unicode_ci NOT NULL,
  `relation_uuid` varchar(36) COLLATE utf8mb4_unicode_ci NOT NULL,
  `alias` varchar(250) COLLATE utf8mb4_unicode_ci NOT NULL,
  `description` varchar(250) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `last_use` datetime NOT NULL DEFAULT '0001-01-01 00:00:00',
  `provision_status` varchar(30) COLLATE utf8mb4_unicode_ci DEFAULT 'AWAITS_PROVISION',
  `provision_status_description` varchar(250) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `provision_id` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `provision_timestamp` datetime DEFAULT '0001-01-01 00:00:00',
  `created_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `created_by` varchar(250) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'system',
  `updated_time` datetime NOT NULL DEFAULT '0001-01-01 00:00:00',
  `updated_by` varchar(250) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `deleted_time` datetime NOT NULL DEFAULT '0001-01-01 00:00:00',
  `deleted_by` varchar(250) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`uuid`),
  KEY `idx_emra_relation_uuid` (`relation_uuid`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
;

CREATE TABLE `entities_meetingroom` (
  `uuid` varchar(36) COLLATE utf8mb4_unicode_ci NOT NULL,
  `legacy_id` bigint(20) DEFAULT NULL,
  `group_id` bigint(20) NOT NULL,
  `name` varchar(250) COLLATE utf8mb4_unicode_ci NOT NULL,
  `description` varchar(250) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `pin` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `guest_pin` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `allow_guests` bit(1) NOT NULL DEFAULT b'1',
  `participant_limit` int(4) NOT NULL DEFAULT '0',
  `enable_overlay_text` bit(1) NOT NULL DEFAULT b'1',
  `guests_can_present` bit(1) NOT NULL DEFAULT b'1',
  `enable_chat` bit(1) NOT NULL DEFAULT b'1',
  `force_presenter_into_main` bit(1) NOT NULL DEFAULT b'1',
  `mute_all_guests` bit(1) NOT NULL DEFAULT b'0',
  `force_encryption` bit(1) NOT NULL DEFAULT b'0',
  `type` varchar(30) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'conference',
  `host_layout` varchar(30) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'one_main_seven_pips',
  `guest_layout` varchar(30) COLLATE utf8mb4_unicode_ci DEFAULT 'one_main_seven_pips',
  `quality` varchar(10) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'hd',
  `theme_id` varchar(36) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `legacy_theme_id` bigint(20) DEFAULT '0',
  `last_use` datetime NOT NULL DEFAULT '0001-01-01 00:00:00',
  `provision_status` varchar(30) COLLATE utf8mb4_unicode_ci DEFAULT 'AWAITS_PROVISION',
  `provision_status_description` varchar(250) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `provision_id` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `provision_timestamp` datetime DEFAULT '0001-01-01 00:00:00',
  `created_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `created_by` varchar(250) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'system',
  `updated_time` datetime NOT NULL DEFAULT '0001-01-01 00:00:00',
  `updated_by` varchar(250) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `deleted_time` datetime NOT NULL DEFAULT '0001-01-01 00:00:00',
  `deleted_by` varchar(250) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`uuid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
;

create view view_entities_meetingroom as
select
    `t1`.`uuid` AS `uuid`,
    `t1`.`group_id` AS `group_id`,
    `t1`.`name` AS `name`,
    `t1`.`description` AS `description`,
    `t1`.`pin` AS `pin`,
    `t1`.`guest_pin` AS `guest_pin`,
    `t1`.`allow_guests` AS `allow_guests`,
    `t1`.`participant_limit` AS `participant_limit`,
    `t1`.`enable_overlay_text` AS `enable_overlay_text`,
    `t1`.`guests_can_present` AS `guests_can_present`,
    `t1`.`enable_chat` AS `enable_chat`,
    `t1`.`force_presenter_into_main` AS `force_presenter_into_main`,
    `t1`.`mute_all_guests` AS `mute_all_guests`,
    `t1`.`force_encryption` AS `force_encryption`,
    `t1`.`type` AS `type`,
    `t1`.`host_layout` AS `host_layout`,
    `t1`.`guest_layout` AS `guest_layout`,
    `t1`.`quality` AS `quality`,
    `t1`.`theme_id` AS `theme_id`,
    `t1`.`last_use` AS `last_use`,
(
        select
            group_concat(
                `entities_meetingroom_alias`.`alias` separator ','
            )
        from
            `entities_meetingroom_alias`
        where
            (
                `entities_meetingroom_alias`.`relation_uuid` = `t1`.`uuid`
            )
    ) AS `aliases`,
(
        select
            group_concat(
                `entities_meetingroom_alias`.`last_use` separator ','
            )
        from
            `entities_meetingroom_alias`
        where
            (
                `entities_meetingroom_alias`.`relation_uuid` = `t1`.`uuid`
            )
    ) AS `aliases_last_use`,
    `t1`.`provision_status` AS `provision_status`,
    `t1`.`provision_status_description` AS `provision_status_description`,
    `t1`.`provision_id` AS `provision_id`,
    `t1`.`provision_timestamp` AS `provision_timestamp`,
    `t1`.`created_time` AS `created_time`,
    `t1`.`created_by` AS `created_by`,
    `t1`.`updated_time` AS `updated_time`,
    `t1`.`updated_by` AS `updated_by`,
    `t1`.`deleted_time` AS `deleted_time`,
    `t1`.`deleted_by` AS `deleted_by`
from
    `entities_meetingroom` `t1`
where
    (`t1`.`deleted_time` = '0001-01-01')
;

--
-- Table structure for table `entities_registeredclient`
--

CREATE TABLE IF NOT EXISTS `entities_registeredclient` (
  `uuid` varchar(36) NOT NULL,
  `group_id` bigint(20) NOT NULL,
  `alias` varchar(250) NOT NULL,
  `description` varchar(250) DEFAULT NULL,
  `enable_sso` bit(1) DEFAULT b'0',
  `owner_email_address` varchar(200) DEFAULT NULL,
  `username` varchar(200) DEFAULT NULL,
  `password` varchar(1000) DEFAULT NULL,
  `last_use` datetime NOT NULL DEFAULT '0001-01-01 00:00:00',
  `provision_status` varchar(30) DEFAULT 'AWAITS_PROVISION',
  `provision_status_description` varchar(250) DEFAULT NULL,
  `provision_id` varchar(50) DEFAULT NULL,
  `provision_timestamp` datetime DEFAULT '0001-01-01 00:00:00',
  `created_time` datetime NOT NULL DEFAULT current_timestamp(),
  `created_by` varchar(250) NOT NULL DEFAULT 'system',
  `updated_time` datetime NOT NULL DEFAULT '0001-01-01 00:00:00',
  `updated_by` varchar(250) DEFAULT NULL,
  `deleted_time` datetime NOT NULL DEFAULT '0001-01-01 00:00:00',
  `deleted_by` varchar(250) DEFAULT NULL,
  PRIMARY KEY (`uuid`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_swedish_ci;
COMMIT;

--

 CREATE TABLE `groups_domains` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `group_id` int(11) DEFAULT NULL,
  `domain` varchar(250) COLLATE utf8mb4_unicode_ci NOT NULL,
  `is_history_domain` bit(1) NOT NULL DEFAULT b'0',
  `created_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `created_by` varchar(250) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'system',
  `updated_time` datetime DEFAULT NULL,
  `updated_by` varchar(250) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `deleted_time` datetime DEFAULT NULL,
  `deleted_by` varchar(250) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `domain` (`domain`)
) ENGINE=InnoDB AUTO_INCREMENT=25 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci