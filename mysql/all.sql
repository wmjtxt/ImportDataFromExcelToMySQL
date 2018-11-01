show databases;
use case1;
create database case1;
use test;
SELECT 
    *
FROM
    sys.sys_config;
#Error: Unable to load authentication plugin 'caching_sha2_password'.
#Solution:
ALTER USER 'root'@'localhost' IDENTIFIED WITH mysql_native_password BY 'root123';
drop database case3;
SELECT 
    COUNT(*)
FROM
    case2.tbl_data;
SELECT 
    *
FROM
    case1.tbl_data;
SELECT 
    *
FROM
    sys.sys_config;


#############数据处理#############################

#1.建立tbl_user表, nodes
drop table if exists tbl_user;
CREATE TABLE tbl_user (
    UserID INT NOT NULL AUTO_INCREMENT,
    PhoneNum VARCHAR(20),
    PhoneAreaCode VARCHAR(10),
    PhoneArea VARCHAR(20),
    NumberSource VARCHAR(20),
    Sign VARCHAR(1),
    IMSI VARCHAR(20),
    Name VARCHAR(20),
    IDCardNum VARCHAR(20),
    PRIMARY KEY (UserID)
);
select count(*) from tbl_user_1;
#2.tbl_user插入数据
insert into groups.tbl_user_1(PhoneNum,PhoneAreaCode,PhoneArea,NumberSource,Sign,IMSI,GroupInfo)
select distinct PhoneNumber,PhoneAreaCode,PhoneArea,CallSource,1,IMSI,GroupInfo
from tbl_data;

#3.tbl_user插入数据
insert into tbl_user(PhoneNum,PhoneAreaCode,PhoneArea,Sign)
select distinct OppositePhoneNumber,OppositePhoneAreaCode,OppositePhoneArea,0
from tbl_data;


#4.tbl_user去重
SET SQL_SAFE_UPDATES = 0;
DELETE FROM tbl_user 
WHERE
    UserID IN (SELECT 
        UserID
    FROM
        (SELECT 
            UserID
        FROM
            tbl_user
        
        WHERE
            PhoneNum IN (SELECT 
                PhoneNum
            FROM
                tbl_user
            GROUP BY PhoneNum
            HAVING COUNT(PhoneNum) > 1)
            AND UserID NOT IN (SELECT 
                MIN(UserID)
            FROM
                tbl_user
            GROUP BY PhoneNum
            HAVING COUNT(PhoneNum) > 1)) AS tmpresult);
#as tmpresult表示建立临时表
SET SQL_SAFE_UPDATES = 1;
SELECT DISTINCT
    (SELECT 
            UserID
        FROM
            tbl_user
        WHERE
            PhoneNumber = tbl_user.PhoneNum) AS UserID,
    (SELECT 
            UserID
        FROM
            tbl_user
        WHERE
            OppositePhoneNumber = tbl_user.PhoneNum) AS OppositeID
FROM
    tbl_data;

#########################################################


#tbl_relation, edges
drop table if exists tbl_relation;
CREATE TABLE tbl_relation (
    RelationID INT NOT NULL AUTO_INCREMENT,
    TimeID INT,
    UserID INT,
    OppositeID INT,
    UserLocID INT,
    OppositeLocID INT,
    TalkTime INT,
    CallType VARCHAR(10),
    CallSign VARCHAR(10),
    Column_17 VARCHAR(20),
    Column_18 VARCHAR(20),
    Column_19 VARCHAR(20),
    Column_20 VARCHAR(20),
    PRIMARY KEY (RelationID)
);

SELECT 
    *
FROM
    tbl_relation;
SELECT DISTINCT
    UserID, OppositeID
FROM
    tbl_relation;

#insert into tbl_relation(UserID,OppositeID,TalkTime,CallType,CallSign)
#select PhoneNumber,OppositePhoneNumber,TalkTime,CallType,CallSign from tbl_data;

insert into tbl_relation(UserID,OppositeID,TalkTime,CallType,CallSign)
select distinct UserID,PhoneNum from tbl_user inner join tbl_data on tbl_user.PhoneNum = tbl_data.PhoneNumber;
SELECT 
    PhoneNumber,
    OppositePhoneNumber,
    TalkTime,
    CallType,
    CallSign
FROM
    tbl_data;



#tbl_relation插入数据！！！！
insert into tbl_relation(UserID,OppositeID,TalkTime,CallType,CallSign)
select (select UserID from tbl_user where PhoneNumber = tbl_user.PhoneNum) as UserID,(select UserID from tbl_user where OppositePhoneNumber = tbl_user.PhoneNum) as OppositeID,TalkTime,CallType,CallSign from tbl_data;





SELECT 
    UserID, OppositeID, COUNT(OppositeID) AS wight
FROM
    tbl_relation
GROUP BY UserID , OppositeID;



SELECT DISTINCT
    UserID
FROM
    tbl_user
        INNER JOIN
    tbl_data ON tbl_user.PhoneNum = tbl_data.OppositePhoneNumber;

SELECT 
    UserID
FROM
    tbl_user
WHERE
    tbl_user.PhoneNum IN (SELECT 
            PhoneNumber
        FROM
            (SELECT 
                PhoneNumber,
                    OppositePhoneNumber,
                    TalkTime,
                    CallType,
                    CallSign
            FROM
                tbl_data));

SELECT 
    PhoneNumber, OppositePhoneNumber
FROM
    tbl_data;

SELECT DISTINCT
    PhoneNumber, OppositePhoneNumber
FROM
    tbl_data;

SELECT 
    PhoneNumber,
    OppositePhoneNumber,
    COUNT(OppositePhoneNumber) AS number_of_call
FROM
    tbl_data;

drop table if exists tbl_relation2;
CREATE TABLE tbl_relation2 AS SELECT PhoneNumber,
    OppositePhoneNumber,
    COUNT(OppositePhoneNumber) AS number_of_call FROM
    tbl_data
GROUP BY PhoneNumber , OppositePhoneNumber;

SELECT 
    *
FROM
    tbl_relation2;

SELECT 
    COUNT(*)
FROM
    case2.tbl_user;
SELECT 
    *
FROM
    case2.tbl_data;
SELECT 
    COUNT(*)
FROM
    case3.tbl_data;




-- 鼠标点击某个节点时显示详细信息
SELECT 
    *
FROM
    case2.tbl_user
WHERE
    UserID = 3;

use case2;
show tables;
show engines;
show variables like 'have_%';


-- 根据用户、日期、时间、地域等查询数据

SELECT *
FROM
    tbl_data
WHERE
    CallDate = '2016-09-09';

-- 查询时间00:00到05:00之间所有通话记录
SELECT DISTINCT
    (SELECT 
            UserID
        FROM
            tbl_user
        WHERE
            PhoneNumber = tbl_user.PhoneNum) AS UserID,
    (SELECT 
            UserID
        FROM
            tbl_user
        WHERE
            OppositePhoneNumber = tbl_user.PhoneNum) AS OppositeID
FROM
    tbl_data
WHERE
    CallTime > '00:00:00'
        AND CallTime < '05:00:00';

-- 查询日期为2016-09-09，通话时间在00:00-05:00之间，地域为兰州的所有通话记录
SELECT 
    tbl_call_1.PhoneNum,tbl_call_1.OppositePhoneNum,tbl_call_1.CallDate,tbl_call_1.CallTime,tbl_user_1.PhoneArea
FROM
    tbl_call_1 inner join tbl_user_1
WHERE
    CallDate = '2016-09-09'
        AND CallTime > '00:00:00'
        AND CallTime < '05:00:00'
        AND PhoneArea LIKE '兰州%'
        AND tbl_call_1.GroupInfo = '1';


delete
from tbl_call_1
where OppositePhoneNum = " ";

-- 
select PhoneNum, OppositePhoneNum
from tbl_call_1;

-- 20181022
SELECT 
    *
FROM
    tbl_data;
SELECT 
    *
FROM
    tbl_call;
show databases;
use groups;

-- 向tbl_call添加数据
INSERT INTO tbl_call(PhoneNum,OppositePhoneNum,CallDate,CallTime,TalkTime,CallType,CGI,CGIChinese,GroupInfo)
SELECT DISTINCT PhoneNumber,OppositePhoneNumber,CallDate,CallTime,TalkTime,CallType,CGI,CGIChinese,GroupInfo
FROM tbl_data;



#20181101
use datapro;

select count(*) from tbl_data where CallRecordType  = "GSM";
select count(*) from tbl_data where CallRecordType  not like "GSM";
select count(*) from tbl_data where CallRecordType  not like "GSM" and CallRecordType  not like "GPRS" and CallRecordType  not like "短消息";
select CallRecordType from tbl_data where CallRecordType  not like "GSM" and CallRecordType  not like "GPRS" and CallRecordType  not like "短消息" and CallRecordType  not like "CDMA1X";
select count(*) from tbl_data where CallRecordType  like "CDMA%";


select count(*) from tbl_data where CallRecordType  = "GSM";   --  33597
select count(*) from tbl_data where CallRecordType  = "GPRS";  -- 271683
select count(*) from tbl_data where CallRecordType  = "短消息"; --  40211
select count(*) from tbl_data where CallRecordType  = "CDMA";  --  10955
select count(*) from tbl_data where CallRecordType  = "CDMA1X";--   1431
select count(*) from tbl_data where CallRecordType  = "长途通信";--     5

select count(*) from tbl_data where CallRecordType = "GPRS" and OppositePhoneNumber = " ";
select count(*) from tbl_data where CallRecordType = "CDMA1X" and OppositePhoneNumber = " ";
select CallRecordType,PhoneNumber,OppositePhoneNumber,CallSign,CallType from tbl_data where OppositePhoneNumber = "0";

select CallRecordType,OppositePhoneNumber,CallSign,CallType from tbl_data
where CallRecordType = "GPRS";

-- 3.根据通话时间通话时长筛选数据
select PhoneNumber,OppositePhoneNumber from tbl_data
union all
select PhoneNumber,OppositePhoneNumber from tbl_data
where CallDate = CallDate and CallTime = CallTime and OppositePhoneNumber not like " ";

select * from tbl_data a, tbl_data b where a.CallDate = b.CallDate and a.CallTime = b.CallTime and a.TalkTime = b.TalkTime and length(a.OppositePhoneNumber) > 1 and a.CallID <> b.CallID;

use datapro;
select * from tbl_data
here length(OppositePhoneNumber) > 1
group by PhoneNumber,OppositePhoneNumber,CallDate,CallTime,TalkTime having count(*) > 1;

select PhoneNumber,OppositePhoneNumber,CallDate,CallTime,TalkTime from tbl_data
where length(OppositePhoneNumber) > 1
group by PhoneNumber,OppositePhoneNumber,CallDate,CallTime,TalkTime having count(*) > 1;

