# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table member (
  email                     varchar(255) not null,
  name                      varchar(255),
  password                  varchar(255),
  constraint pk_member primary key (email))
;

create sequence member_seq;




# --- !Downs

drop table if exists member cascade;

drop sequence if exists member_seq;

