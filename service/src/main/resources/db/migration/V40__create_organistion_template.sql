create table organisation_template(
    id bigint(20) not null auto_increment,
    organisation_code varchar(100) not null,
    name varchar(100) not null,
    parent_organisation_code varchar(100) not null,
    primary key(id),
    unique key organisation_code (organisation_code),
    FOREIGN KEY (parent_organisation_code) references organisation (organisation_id)
);
