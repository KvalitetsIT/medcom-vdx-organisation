-- groups
INSERT INTO groups(group_id, group_name, group_type, parent_id) VALUES (1, 'company 1', 2, null);
INSERT INTO groups(group_id, group_name, group_type, parent_id) VALUES (2, 'company 2', 2, null);
INSERT INTO groups(group_id, group_name, group_type, parent_id) VALUES (3, 'company 3', 2, null);
INSERT INTO groups(group_id, group_name, group_type, parent_id) VALUES (4, 'kvak', 2, null);
INSERT INTO groups(group_id, group_name, group_type, parent_id) VALUES (5, 'test-org', 2, null);
INSERT INTO groups(group_id, group_name, group_type, parent_id) VALUES (6, 'another-test-org', 2, null);
INSERT INTO groups(group_id, group_name, group_type, parent_id) VALUES (7, 'pool-test-org', 2, null);
INSERT INTO groups(group_id, group_name, group_type, parent_id) VALUES (9, 'overflow', 2, null);
insert into groups(group_id, group_name, group_type, parent_id) values(10, 'super_parent', 2, null);
insert into groups(group_id, group_name, group_type, parent_id) values(11, 'parent', 2, 10);
insert into groups(group_id, group_name, group_type, parent_id) values(12, 'child_one', 2, 11);
insert into groups(group_id, group_name, group_type, parent_id) values(13, 'child', 2, 12);
insert into groups(group_id, group_name, group_type, parent_id) values(15, 'medcom', 1, null);
insert into groups(group_id, group_name, group_type, parent_id) values(16, 'æ/åø', 2, null);
insert into groups(group_id, group_name, group_type, parent_id) values(17, 'another-child', 2, 11);


-- * organisation *
INSERT INTO organisation (id, organisation_id, name, group_id, sms_sender_name) VALUES (1, 'company 1', 'company name 1', 1, 'SomeSender');
INSERT INTO organisation (id, organisation_id, name, group_id) VALUES (2, 'company 2', 'company name 2', 2);
INSERT INTO organisation (id, organisation_id, name, group_id) VALUES (3, 'company 3', 'company name 3', 3);
INSERT INTO organisation (id, organisation_id, name, group_id) VALUES (4, 'kvak', 'company name kvak', 4);
INSERT INTO organisation (id, organisation_id, name, group_id, sms_sender_name, sms_callback_url) VALUES (5, 'test-org', 'company name test-org', 5, 'MinAfsender', 'some_url');
INSERT INTO organisation (id, organisation_id, name, group_id) VALUES (6, 'another-test-org', 'company name another-test-org', 6);
INSERT INTO organisation (id, organisation_id, name, pool_size, group_id, allow_custom_uri_without_domain) VALUES (7, 'pool-test-org', 'company name another-test-org', 10, 7, 1);

insert into organisation(id, organisation_id, name, pool_size, group_id, sms_sender_name, sms_callback_url) values(10, 'parent', 'parent org', 20, 11, 'sms-sender', 'callback');
insert into organisation(id, organisation_id, name, group_id, history_api_key) values(11, 'child', 'child org', 13, '8adeac18-f061-4992-818b-8d4461ccfaa7');
insert into organisation(id, organisation_id, name, group_id, history_api_key) values(12, 'medcom', 'This Is Medcom', 15, '23646a74-7737-4bab-a8a9-07f21471a14a');
insert into organisation(id, organisation_id, name, group_id) values(13, 'æ/åø', 'This is with a slash', 16);
insert into organisation(id, organisation_id, name, group_id, history_api_key) values(14, 'another-child', 'This is a another child', 17, 'kuk');

INSERT INTO scheduling_info (id, uri_with_domain, provision_status, organisation_id)
VALUES (1, '1238@test.dk', 'PROVISIONED_OK', 5);

INSERT INTO scheduling_info (id, uri_with_domain, provision_status, organisation_id)
VALUES (2, '1239@test.dk', 'PROVISIONED_OK', 7);

-- test data for views

insert into entities_meetingroom(uuid, group_id, name) values ('80b58b9f-36ec-4e04-8ae5-092918542114', 1, 'multiple_one');
insert into entities_meetingroom(uuid, group_id, name) values ('de99982f-12c8-4dae-903e-3df3085a93b8', 2, 'multiple_two');

insert into entities_meetingroom_alias(uuid, relation_uuid, alias) values('8eda3a3d-70bd-474d-a727-91bd46ded2ec', '80b58b9f-36ec-4e04-8ae5-092918542114', 'alias_one');
insert into entities_meetingroom_alias(uuid, relation_uuid, alias) values('045003a1-9b31-4197-94ed-56690cd7878b', '80b58b9f-36ec-4e04-8ae5-092918542114', 'alias_two');

insert into entities_meetingroom_alias(uuid, relation_uuid, alias) values('53c785c6-aaf7-4263-acd0-0f7ba371337f', 'de99982f-12c8-4dae-903e-3df3085a93b8', 'alias_one');
insert into entities_meetingroom_alias(uuid, relation_uuid, alias) values('ad4171ba-d901-44fa-8301-2546aeef24b3', 'de99982f-12c8-4dae-903e-3df3085a93b8', 'alias_two');

-- used for dk.medcom.vdx.organisation.integrationtest.OrganisationIT.testReadOrganisationTree_with_group_only
INSERT INTO `groups` (`group_id`, `group_name`, `group_type`, `parent_id`, `created_time`, `created_by`, `updated_time`, `updated_by`, `deleted_time`, `deleted_by`) VALUES (22, 'VDX', '2', NULL, '2020-09-03 09:17:11', 'system', '0001-01-01 00:00:00', NULL, '0001-01-01 00:00:00', NULL);
INSERT INTO `organisation` (`id`, `group_id`, `organisation_id`, `name`, `deleted_time`, `pool_size`, `sms_sender_name`, `allow_custom_uri_without_domain`, `sms_callback_url`) VALUES (NULL, '22', 'vdx', 'VDX', '0001-01-01 00:00:00', '4', 'VDX Video', '0', NULL);

INSERT INTO `groups` (`group_id`, `group_name`, `group_type`, `parent_id`, `created_time`, `created_by`, `updated_time`, `updated_by`, `deleted_time`, `deleted_by`) VALUES (21, 'Offentlige organisationer', '1', '22', '2020-10-01 09:41:12', 'jsk@medcom.dk', '0001-01-01 00:00:00', NULL, '0001-01-01 00:00:00', NULL);

INSERT INTO `groups` (`group_id`, `group_name`, `group_type`, `parent_id`, `created_time`, `created_by`, `updated_time`, `updated_by`, `deleted_time`, `deleted_by`) VALUES (20, 'Medcom', '2', '21', '2020-09-03 09:18:10', 'system', '2020-10-01 11:43:46', 'jsk@medcom.dk', '0001-01-01 00:00:00', NULL);
INSERT INTO `organisation` (`id`, `group_id`, `organisation_id`, `name`, `deleted_time`, `pool_size`, `sms_sender_name`, `allow_custom_uri_without_domain`, `sms_callback_url`) VALUES (NULL, '20', 'medcom1', 'Medcom', '0001-01-01 00:00:00', '4', NULL, '1', NULL);

INSERT INTO `groups` (`group_id`, `group_name`, `group_type`, `parent_id`, `created_time`, `created_by`, `updated_time`, `updated_by`, `deleted_time`, `deleted_by`) VALUES (42, 'Test Gruppe 1', '1', '20', '2023-02-03 21:27:40', 'jsk@medcom.dk', '2023-02-04 00:49:59', 'jsk@medcom.dk', '0001-01-01 00:00:00', NULL);
INSERT INTO `organisation` (`id`, `group_id`, `organisation_id`, `name`, `deleted_time`, `pool_size`, `sms_sender_name`, `allow_custom_uri_without_domain`, `sms_callback_url`) VALUES (NULL, '42', 'c53f8165-35e8-46db-b052-d93493c139d5/TestGruppe123', 'Test Gruppe 123', '2023-02-03 23:39:48', NULL, NULL, '0', NULL), (NULL, '42', 'a5fe2e71-6c60-47af-a496-d039f463b399/TestGruppe123', 'Test Gruppe 1234', '2023-02-04 00:49:43', NULL, NULL, '0', NULL);

INSERT INTO `groups` (`group_id`, `group_name`, `group_type`, `parent_id`, `created_time`, `created_by`, `updated_time`, `updated_by`, `deleted_time`, `deleted_by`) VALUES (27, 'MedCom Test 2', '2', '42', '2022-02-07 07:34:09', 'jsk@medcom.dk', '2023-02-03 23:22:00', 'jsk@medcom.dk', '0001-01-01 00:00:00', NULL);
INSERT INTO `organisation` (`id`, `group_id`, `organisation_id`, `name`, `deleted_time`, `pool_size`, `sms_sender_name`, `allow_custom_uri_without_domain`, `sms_callback_url`) VALUES (NULL, '27', 'medcom_test_2', 'MedCom Test 2', '0001-01-01 00:00:00', NULL, NULL, '0', NULL);