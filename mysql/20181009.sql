show databases;
use case1;
create database case1;
use test;
select * from sys.sys_config;
#Error: Unable to load authentication plugin 'caching_sha2_password'.
#Solution:
ALTER USER 'root'@'localhost' IDENTIFIED WITH mysql_native_password BY 'root123';
drop database case1;
select count(*) from case2.tbl_data;
select * from case1.tbl_data;
select * from sys.sys_config;


#############数据处理#############################

#1.建立tbl_user表, nodes
drop table if exists tbl_user;
create table tbl_user
(
   UserID               int not null auto_increment,
   PhoneNum             varchar(20),
   PhoneAreaCode        varchar(10),
   PhoneArea            varchar(20),
   NumberSource         varchar(20),
   Sign                 varchar(1),
   IMSI                 varchar(20),
   Name                 varchar(20),
   IDCardNum            varchar(20),
   primary key (UserID)
);

#2.tbl_user插入数据
insert into tbl_user(PhoneNum,PhoneAreaCode,PhoneArea,NumberSource,Sign,IMSI)
select distinct PhoneNumber,PhoneAreaCode,PhoneArea,CallSource,1,IMSI
from tbl_data;

#3.tbl_user插入数据
insert into tbl_user(PhoneNum,PhoneAreaCode,PhoneArea,Sign)
select distinct OppositePhoneNumber,OppositePhoneAreaCode,OppositePhoneArea,0
from tbl_data;


#4.tbl_user去重
SET SQL_SAFE_UPDATES = 0;
delete from tbl_user where UserID in (select UserID from (select UserID from tbl_user where PhoneNum in (SELECT PhoneNum FROM tbl_user group by PhoneNum having count(PhoneNum)>1) and UserID not in(select min(UserID) from tbl_user group by PhoneNum having count(PhoneNum)>1)) as tmpresult);
#as tmpresult表示建立临时表
SET SQL_SAFE_UPDATES = 1;
#
#SQL_SAFE_UPDATES值为1时，以下三种情况无法正常操作，会出现ERROR 1175 (HY000): You are using safe update mode and you tried to update a table without a WHERE that uses a KEY column，设为0后可以执行
#1:没有加where条件的全表更新操作 ;
#2:加了where条件字段，但是where 字段 没有走索引的表更新 ;
#3:全表delete 没有加where条件或者where 条件没有走索引。


#5.以下查询结果可以作为图的edges
select distinct (select UserID from tbl_user where PhoneNumber = tbl_user.PhoneNum) as UserID,(select UserID from tbl_user where OppositePhoneNumber = tbl_user.PhoneNum) as OppositeID from tbl_data;

#########################################################


#tbl_relation, edges
drop table if exists tbl_relation;
create table tbl_relation
(
   RelationID           int not null auto_increment,
   TimeID               int,
   UserID               int,
   OppositeID           int,
   UserLocID            int,
   OppositeLocID        int,
   TalkTime             int,
   CallType             varchar(10),
   CallSign             varchar(10),
   Column_17            varchar(20),
   Column_18            varchar(20),
   Column_19            varchar(20),
   Column_20            varchar(20),
   primary key (RelationID)
);

select * from tbl_relation;
select distinct UserID,OppositeID from tbl_relation;

#insert into tbl_relation(UserID,OppositeID,TalkTime,CallType,CallSign)
#select PhoneNumber,OppositePhoneNumber,TalkTime,CallType,CallSign from tbl_data;

insert into tbl_relation(UserID,OppositeID,TalkTime,CallType,CallSign)
select distinct UserID,PhoneNum from tbl_user inner join tbl_data on tbl_user.PhoneNum = tbl_data.PhoneNumber;
select PhoneNumber,OppositePhoneNumber,TalkTime,CallType,CallSign from tbl_data;



#tbl_relation插入数据！！！！
insert into tbl_relation(UserID,OppositeID,TalkTime,CallType,CallSign)
select (select UserID from tbl_user where PhoneNumber = tbl_user.PhoneNum) as UserID,(select UserID from tbl_user where OppositePhoneNumber = tbl_user.PhoneNum) as OppositeID,TalkTime,CallType,CallSign from tbl_data;





#利用tbl_relation可以加上权重
select UserID,OppositeID,count(OppositeID) as wight from tbl_relation group by UserID,OppositeID;



select distinct UserID from tbl_user inner join tbl_data on tbl_user.PhoneNum = tbl_data.OppositePhoneNumber;

select UserID from tbl_user where tbl_user.PhoneNum in (select PhoneNumber from (select PhoneNumber,OppositePhoneNumber,TalkTime,CallType,CallSign from tbl_data));

####

select PhoneNumber,OppositePhoneNumber from tbl_data;

select distinct PhoneNumber,OppositePhoneNumber from tbl_data;

select PhoneNumber,OppositePhoneNumber,count(OppositePhoneNumber) as number_of_call 
from tbl_data;

drop table if exists tbl_relation2;
create table tbl_relation2 as 
select PhoneNumber,OppositePhoneNumber,count(OppositePhoneNumber) as number_of_call 
from tbl_data 
group by PhoneNumber,OppositePhoneNumber;

select * from tbl_relation2;

