-- * groups *
INSERT INTO groups(group_id, group_name, group_type, parent_id) VALUES (1, 'company 1', 2, null);

-- * organisation *
INSERT INTO organisation (id, group_id, organisation_id, name, pool_size, sms_sender_name, allow_custom_uri_without_domain, sms_callback_url, history_api_key, device_webhook_endpoint, device_webhook_endpoint_key)
    VALUES (1, 1, 'company 1', 'company name 1', 2, 'sms-sender', 0, null, 'api-key', 'webhook-endpoint', 'webhook-endpoint-key');
