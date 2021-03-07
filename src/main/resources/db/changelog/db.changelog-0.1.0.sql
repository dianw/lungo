--liquibase formatted sql

--changeset dianw:0001
--comment: create app table
create table mainapi_app (
  id bigint(20),
  created_by varchar(256) not null,
  created_date datetime not null,
  last_modified_by varchar(256),
  last_modified_date datetime,
  app_name varchar(128) not null,
  business_address varchar(2048) not null,
  company_name varchar(512) not null,
  country varchar(128) not null,
  cs_email varchar(1024),
  cs_number varchar(128),
  representative_name varchar(256),
  website varchar(1024),
  primary key (id),
  key idx_app_name_created_by (app_name, created_by)
)
--rollback drop table mainapi_app
