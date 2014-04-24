# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table invitation (
  id                        bigint not null,
  group_id                  bigint,
  email                     varchar(255),
  confirmation_token        varchar(255),
  sender_id                 bigint,
  creation_time             bigint,
  constraint pk_invitation primary key (id))
;

create table item (
  id                        bigint not null,
  member_id                 bigint not null,
  title                     varchar(255),
  description               varchar(255),
  creation_time             bigint,
  constraint pk_item primary key (id))
;

create table mgroup (
  id                        bigint not null,
  name                      varchar(255),
  constraint pk_mgroup primary key (id))
;

create table member (
  id                        bigint not null,
  email                     varchar(255),
  first_name                varchar(255),
  last_name                 varchar(255),
  password                  varchar(255),
  active                    boolean,
  confirmation_token        varchar(255),
  creation_time             bigint,
  constraint uq_member_email unique (email),
  constraint pk_member primary key (id))
;

create table request (
  id                        bigint not null,
  requester_id              bigint,
  item_wanted_id            bigint,
  is_active                 boolean,
  is_replied                boolean,
  request_time              bigint,
  given_time                bigint,
  return_time               bigint,
  reply_time                bigint,
  constraint pk_request primary key (id))
;

create table tag (
  id                        bigint not null,
  type_type                 varchar(255),
  text                      varchar(255),
  constraint pk_tag primary key (id))
;

create table tag_type (
  type                      varchar(255) not null,
  constraint pk_tag_type primary key (type))
;


create table item_tag (
  item_id                        bigint not null,
  tag_id                         bigint not null,
  constraint pk_item_tag primary key (item_id, tag_id))
;

create table mgroup_member (
  mgroup_id                      bigint not null,
  member_id                      bigint not null,
  constraint pk_mgroup_member primary key (mgroup_id, member_id))
;
create sequence invitation_seq;

create sequence item_seq;

create sequence mgroup_seq;

create sequence member_seq;

create sequence request_seq;

create sequence tag_seq;

create sequence tag_type_seq;

alter table invitation add constraint fk_invitation_group_1 foreign key (group_id) references mgroup (id);
create index ix_invitation_group_1 on invitation (group_id);
alter table invitation add constraint fk_invitation_sender_2 foreign key (sender_id) references member (id);
create index ix_invitation_sender_2 on invitation (sender_id);
alter table item add constraint fk_item_member_3 foreign key (member_id) references member (id);
create index ix_item_member_3 on item (member_id);
alter table request add constraint fk_request_requester_4 foreign key (requester_id) references member (id);
create index ix_request_requester_4 on request (requester_id);
alter table request add constraint fk_request_itemWanted_5 foreign key (item_wanted_id) references item (id);
create index ix_request_itemWanted_5 on request (item_wanted_id);
alter table tag add constraint fk_tag_type_6 foreign key (type_type) references tag_type (type);
create index ix_tag_type_6 on tag (type_type);



alter table item_tag add constraint fk_item_tag_item_01 foreign key (item_id) references item (id);

alter table item_tag add constraint fk_item_tag_tag_02 foreign key (tag_id) references tag (id);

alter table mgroup_member add constraint fk_mgroup_member_mgroup_01 foreign key (mgroup_id) references mgroup (id);

alter table mgroup_member add constraint fk_mgroup_member_member_02 foreign key (member_id) references member (id);

# --- !Downs

drop table if exists invitation cascade;

drop table if exists item cascade;

drop table if exists item_tag cascade;

drop table if exists mgroup cascade;

drop table if exists mgroup_member cascade;

drop table if exists member cascade;

drop table if exists request cascade;

drop table if exists tag cascade;

drop table if exists tag_type cascade;

drop sequence if exists invitation_seq;

drop sequence if exists item_seq;

drop sequence if exists mgroup_seq;

drop sequence if exists member_seq;

drop sequence if exists request_seq;

drop sequence if exists tag_seq;

drop sequence if exists tag_type_seq;

