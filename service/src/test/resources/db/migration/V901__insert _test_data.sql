-- groups
INSERT INTO groups(group_id, group_name, group_type, parent_id) VALUES (1, 'company 1', 2, null);
INSERT INTO groups(group_id, group_name, group_type, parent_id) VALUES (2, 'company 2', 2, null);
INSERT INTO groups(group_id, group_name, group_type, parent_id) VALUES (3, 'company 3', 2, null);
INSERT INTO groups(group_id, group_name, group_type, parent_id) VALUES (4, 'kvak', 2, null);
INSERT INTO groups(group_id, group_name, group_type, parent_id) VALUES (5, 'test-org', 2, null);
INSERT INTO groups(group_id, group_name, group_type, parent_id) VALUES (6, 'another-test-org', 2, null);
INSERT INTO groups(group_id, group_name, group_type, parent_id) VALUES (7, 'pool-test-org', 2, null);
INSERT INTO groups(group_id, group_name, group_type, parent_id) VALUES (8, 'pool-test-org2', 2, null);
INSERT INTO groups(group_id, group_name, group_type, parent_id) VALUES (9, 'overflow', 2, null);
insert into groups(group_id, group_name, group_type, parent_id) values(10, 'super_parent', 2, null);
insert into groups(group_id, group_name, group_type, parent_id) values(11, 'parent', 2, 10);
insert into groups(group_id, group_name, group_type, parent_id) values(12, 'child_one', 2, 11);
insert into groups(group_id, group_name, group_type, parent_id, deleted_time, deleted_by) values(14, 'child_one-deleted', 2, 11, now(), 'some-user');
insert into groups(group_id, group_name, group_type, parent_id) values(13, 'child', 2, 12);

-- * organisation *
INSERT INTO organisation (id, organisation_id, name, group_id, sms_sender_name) VALUES (1, 'company 1', 'company name 1', 1, 'SomeSender');
INSERT INTO organisation (id, organisation_id, name, group_id) VALUES (2, 'company 2', 'company name 2', 2);
INSERT INTO organisation (id, organisation_id, name, group_id) VALUES (3, 'company 3', 'company name 3', 3);
INSERT INTO organisation (id, organisation_id, name, group_id) VALUES (4, 'kvak', 'company name kvak', 4);
INSERT INTO organisation (id, organisation_id, name, group_id) VALUES (5, 'test-org', 'company name test-org', 5);
INSERT INTO organisation (id, organisation_id, name, group_id) VALUES (6, 'another-test-org', 'company name another-test-org', 6);
INSERT INTO organisation (id, organisation_id, name, pool_size, group_id) VALUES (7, 'pool-test-org', 'company name another-test-org', 10, 7);
INSERT INTO organisation (id, organisation_id, name, pool_size, group_id) VALUES (8, 'pool-test-org2', 'company name another-test-org2', 30, 8);
INSERT INTO organisation (id, organisation_id, name, pool_size, group_id) VALUES (9, 'overflow', 'overflow pool org', 10, 9);
insert into organisation(id, organisation_id, name, pool_size, group_id, sms_sender_name, sms_callback_url) values(10, 'parent', 'parent org', 20, 11, 'sms-sender', 'callback');
insert into organisation(id, organisation_id, name, group_id) values(11, 'child', 'child org', 13);

INSERT INTO scheduling_info (id, uri_with_domain, provision_status, organisation_id)
VALUES (1, '1238@test.dk', 'PROVISIONED_OK', 5);

