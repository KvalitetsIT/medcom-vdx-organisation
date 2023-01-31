CREATE TABLE if not exists scheduling_info (
  id bigint(20) NOT NULL AUTO_INCREMENT,
  uri_with_domain varchar(100) NOT NULL,
  provision_status varchar(30) NOT NULL,
  organisation_id bigint(20) not null,
  PRIMARY KEY (id)
--  FOREIGN KEY (meetings_id) REFERENCES meetings(id),
--  FOREIGN KEY (scheduling_template_id) REFERENCES scheduling_template(id)
);