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


#5.以下查询结果作为图的edges
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