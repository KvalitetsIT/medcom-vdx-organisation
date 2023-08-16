alter table organisation add history_api_key varchar(36) null;

create unique index organisation_history_api_key on organisation(history_api_key);
