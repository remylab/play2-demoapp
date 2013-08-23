# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table invitation (
  id                        bigint not null,
  group_id                  bigint,
  email                     varchar(255),
  confirmation_token        varchar(255),
  sender_id                 bigint,
  constraint pk_invitation primary key (id))
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
  constraint uq_member_email unique (email),
  constraint pk_member primary key (id))
;


create table mgroup_member (
  mgroup_id                      bigint not null,
  member_id                      bigint not null,
  constraint pk_mgroup_member primary key (mgroup_id, member_id))
;
create sequence invitation_seq;

create sequence mgroup_seq;

create sequence member_seq;

alter table invitation add constraint fk_invitation_group_1 foreign key (group_id) references mgroup (id);
create index ix_invitation_group_1 on invitation (group_id);
alter table invitation add constraint fk_invitation_sender_2 foreign key (sender_id) references member (id);
create index ix_invitation_sender_2 on invitation (sender_id);



alter table mgroup_member add constraint fk_mgroup_member_mgroup_01 foreign key (mgroup_id) references mgroup (id);

alter table mgroup_member add constraint fk_mgroup_member_member_02 foreign key (member_id) references member (id);

# --- !Downs

drop table if exists invitation cascade;

drop table if exists mgroup cascade;

drop table if exists mgroup_member cascade;

drop table if exists member cascade;

drop sequence if exists invitation_seq;

drop sequence if exists mgroup_seq;

drop sequence if exists member_seq;

