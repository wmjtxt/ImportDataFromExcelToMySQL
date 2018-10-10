SHOW DATABASES;
USE case1;
CREATE DATABASE case1;
USE test;
SELECT 
    *
FROM
    sys.sys_config;
#Error: Unable to load authentication plugin 'caching_sha2_password'.
#Solution:
ALTER USER 'root'@'localhost' IDENTIFIED WITH mysql_native_password BY 'root123';
drop database case1;
SELECT 
    COUNT(*)
FROM
    case1.tbl_data;
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
DROP TABLE IF EXISTS tbl_user;
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

#2.tbl_user插入数据
INSERT INTO tbl_user(PhoneNum,PhoneAreaCode,PhoneArea,NumberSource,Sign,IMSI)
SELECT DISTINCT PhoneNumber,PhoneAreaCode,PhoneArea,CallSource,1,IMSI
FROM tbl_data;

#3.tbl_user插入数据
INSERT INTO tbl_user(PhoneNum,PhoneAreaCode,PhoneArea,Sign)
SELECT DISTINCT OppositePhoneNumber,OppositePhoneAreaCode,OppositePhoneArea,0
FROM tbl_data;


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
#
#SQL_SAFE_UPDATES值为1时，以下三种情况无法正常操作，会出现ERROR 1175 (HY000): You are using safe update mode and you tried to update a table without a WHERE that uses a KEY column，设为0后可以执行
#1:没有加where条件的全表更新操作 ;
#2:加了where条件字段，但是where 字段 没有走索引的表更新 ;
#3:全表delete 没有加where条件或者where 条件没有走索引。


#5.以下查询结果可以作为图的edges
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
DROP TABLE IF EXISTS tbl_relation;
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

INSERT INTO tbl_relation(UserID,OppositeID,TalkTime,CallType,CallSign)
SELECT DISTINCT UserID,PhoneNum FROM tbl_user INNER JOIN tbl_data ON tbl_user.PhoneNum = tbl_data.PhoneNumber;
SELECT 
    PhoneNumber,
    OppositePhoneNumber,
    TalkTime,
    CallType,
    CallSign
FROM
    tbl_data;



#tbl_relation插入数据！！！！
INSERT INTO tbl_relation(UserID,OppositeID,TalkTime,CallType,CallSign)
SELECT (SELECT UserID FROM tbl_user WHERE PhoneNumber = tbl_user.PhoneNum) AS UserID,(SELECT UserID FROM tbl_user WHERE OppositePhoneNumber = tbl_user.PhoneNum) AS OppositeID,TalkTime,CallType,CallSign FROM tbl_data;





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
