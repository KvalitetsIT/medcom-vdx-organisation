create table organisation_template(
    id bigint(20) not null auto_increment,
    organisation_code varchar(100) not null,
    name varchar(100) not null,
    parent_organisation_code varchar(100) not null,
    created_time datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by varchar(250) NOT NULL DEFAULT 'system',
    updated_time datetime NOT NULL DEFAULT '0001-01-01 00:00:00',
    updated_by varchar(250) DEFAULT NULL,
    primary key(id),
    unique key organisation_code (organisation_code),
    FOREIGN KEY (parent_organisation_code) references organisation (organisation_id)
);
