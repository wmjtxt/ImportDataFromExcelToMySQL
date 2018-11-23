#创建数据库
CREATE DATABASE if not exists datapro;
USE datapro;
CREATE TABLE if not exists manage_users(
	UserID   varchar(10),
	UserName varchar(20),
	Role     varchar(10),
	PassWord varchar(32),
	PRIMARY KEY(UserID)
	);
CREATE TABLE if not exists manage_groups(
	GroupID   varchar(10),
	GroupName varchar(20),
	Remark    varchar(32),
	PRIMARY KEY(GroupID)
	);

#创建原始数据表data_all
CREATE TABLE if not exists data_all(
	CallID varchar(5) NOT NULL,
	CallRecordType varchar(20) DEFAULT NULL,
	CallSource varchar(10) DEFAULT NULL,
	CallListType varchar(10) DEFAULT NULL,
	CallIdentification varchar(10) DEFAULT NULL,
	RoamingIdentifier varchar(10) DEFAULT NULL,
	CallDate date NOT NULL,
	CallTime time NOT NULL,
	TalkTime int(5) NOT NULL,
	PhoneAreaCode varchar(10) DEFAULT NULL, 
	PhoneArea varchar(20) DEFAULT NULL,
	PhoneNumber varchar(20) NOT NULL,
	OppositePhoneAreaCode varchar(10) DEFAULT NULL,
	OppositePhoneArea varchar(20) DEFAULT NULL,
	OppositePhoneNumber varchar(20) NOT NULL, 
	CallType varchar(10) NOT NULL,
	CallTypeChinese varchar(30) DEFAULT NULL, 
	CallSign varchar(10) DEFAULT NULL,
	PhoneCallAreaCode varchar(4) DEFAULT NULL, 
	PhoneCallArea varchar(20) DEFAULT NULL,
	CallDescription varchar(10) DEFAULT NULL, 
	CardNumber varchar(10) DEFAULT NULL,
	InternetAccount varchar(20) DEFAULT NULL, 
	IMSI varchar(50) DEFAULT NULL,
	DeviceNumber varchar(50) DEFAULT NULL, 
	SwitchBoardID varchar(20) DEFAULT NULL,
	CGI varchar(50) DEFAULT NULL, 
	CGIChinese varchar(200) DEFAULT NULL,
	LAC varchar(10) DEFAULT NULL, 
	CID varchar(10) DEFAULT NULL,
	OppositeLAC varchar(10) DEFAULT NULL,
	OppositeCallAreacode varchar(4) DEFAULT NULL,
	OppositeCallArea varchar(20) DEFAULT NULL,
	OutRoute varchar(20) DEFAULT NULL, 
	InRoute varchar(20) DEFAULT NULL,
	DynamicRoamingNumber varchar(10) DEFAULT NULL, 
	CTX varchar(10) DEFAULT NULL,
	OppositeCTX varchar(10) DEFAULT NULL,
	FarwardingNumber varchar(10) DEFAULT NULL,
	CallCost varchar(10) DEFAULT NULL, 
	IP varchar(20) DEFAULT NULL,
	UserName varchar(20) DEFAULT NULL, 
	HAIP varchar(20) DEFAULT NULL,
	PdsnfaIP varchar(20) DEFAULT NULL, 
	PcfIP varchar(20) DEFAULT NULL,
	BSID varchar(20) DEFAULT NULL, 
	RoamingIdentifier2 varchar(10) DEFAULT NULL,
	CardType varchar(20) DEFAULT NULL, 
	UserIP varchar(20) DEFAULT NULL,
	OppositeIP varchar(20) DEFAULT NULL, 
	ConferenceID varchar(10) DEFAULT NULL,
	NumberOfParticipants varchar(10) DEFAULT NULL,
	UserAttachedNumber varchar(20) DEFAULT NULL,
	OppositePhoneNumberArea varchar(20) DEFAULT NULL,
	OppositeCID varchar(10) DEFAULT NULL,
	LeftNumSigns varchar(20),
	RightNumSigns varchar(20),
	GroupInfo varchar(20) NOT NULL
	);

#创建data_tel
CREATE TABLE if not exists data_tel(
	UserID INT NOT NULL AUTO_INCREMENT,
	PhoneNumber VARCHAR(20) NOT NULL,
	PhoneAreaCode VARCHAR(10) DEFAULT NULL,
	PhoneArea VARCHAR(20) DEFAULT NULL,
	NumberSource VARCHAR(20),
	KeySigns VARCHAR(20),
	IMSI VARCHAR(20),
	Name VARCHAR(20),
	IDCardNum VARCHAR(20),
	GroupInfo varchar(20) NOT NULL,
	PRIMARY KEY (UserID,PhoneNumber)
	);

#创建联系表data_call
CREATE TABLE if not exists data_call(
	CallID INT NOT NULL AUTO_INCREMENT,
	PhoneNumber VARCHAR(20) NOT NULL,
	PhoneAreaCode varchar(10),
	PhoneArea varchar(20) DEFAULT NULL,
	OppositePhoneNumber varchar(20),
	OppositePhoneAreaCode varchar(10),
	OppositePhoneArea varchar(20) DEFAULT NULL,
	CallRecordType varchar(20),
	CallDate date,
	CallTime time,
	TalkTime int(5),
	CallType varchar(10),
	CallSign varchar(10),
	CGI varchar(50),
	CGIChinese varchar(200),
	LeftNumSigns varchar(20),
	RightNumSigns varchar(20),
	GroupInfo varchar(20) NOT NULL,
	PRIMARY KEY(CallID,PhoneNumber,OppositePhoneNumber,CallDate,CallTime,TalkTime,CallSign)
	);

#创建存储过程
DELIMITER // 
CREATE PROCEDURE proc_datapro()
BEGIN 
	SET SQL_SAFE_UPDATES = 0;
	#提取数据到data_tel
	INSERT INTO data_tel 
		SELECT DISTINCT null,PhoneNumber,PhoneAreaCode,PhoneArea,CallSource,'0x100',IMSI,null,null,GroupInfo 
		FROM data_all;
	INSERT INTO data_tel
		SELECT DISTINCT null,OppositePhoneNumber,OppositePhoneAreaCode,OppositePhoneArea,null,'0x000',IMSI,null,null,GroupInfo
		FROM data_all;
	#0x200,右侧号码为跟踪对象的
	UPDATE data_all SET RightNumSigns = '0x200' WHERE OppositePhoneNumber IN (
		SELECT OppositePhoneNumber FROM (
			SELECT distinct OppositePhoneNumber FROM data_all WHERE OppositePhoneNumber IN (
				SELECT distinct PhoneNumber FROM data_all 
			)
		) AS t
	);
	#0x300,右侧号码与至少两个跟踪对象联系者
	UPDATE data_all SET RightNumSigns = '0x300' WHERE OppositePhoneNumber IN (
		SELECT OppositePhoneNumber FROM(
			SELECT OppositePhoneNumber FROM(
				SELECT OppositePhoneNumber FROM data_all WHERE LENGTH(OppositePhoneNumber)>1 AND RightNumSigns <> '0x200'
				GROUP BY OppositePhoneNumber,PhoneNumber
			) AS t1
			GROUP BY OppositePhoneNumber HAVING COUNT(*) > 1
		)as t2
	);
	#data_tel去重
	DELETE FROM data_tel WHERE UserID IN (
		SELECT UserID FROM(
			SELECT  UserID FROM data_tel WHERE PhoneNumber IN (
				SELECT PhoneNumber FROM data_tel 
				GROUP BY PhoneNumber HAVING COUNT(PhoneNumber) > 1
			) AND UserID NOT IN (
				SELECT MIN(UserID) FROM data_tel 
				GROUP BY PhoneNumber HAVING COUNT(PhoneNumber) > 1
			)
		) AS t
	);
	#提取数据到data_tel
	INSERT INTO data_call 
		SELECT null,PhoneNumber,PhoneAreaCode,PhoneArea,OppositePhoneNumber,OppositePhoneAreaCode,OppositePhoneArea,
			CallRecordType,CallDate,CallTime,TalkTime,CallType,CallSign,CGI,CGIChinese,LeftNumSigns,RightNumSigns,GroupInfo 
			FROM data_all;
	#data_call去重
	DELETE FROM data_call WHERE OppositePhoneNumber LIKE ' %';
	DELETE FROM data_call WHERE CallID IN (
		SELECT CallID FROM (
			SELECT CallID FROM data_call 
			WHERE (PhoneNumber,OppositePhoneNumber,CallDate,CallTime,TalkTime) IN (
				SELECT PhoneNumber,OppositePhoneNumber,CallDate,CallTime,TalkTime FROM data_call 
				GROUP BY PhoneNumber,OppositePhoneNumber,CallDate,CallTime,TalkTime 
				HAVING COUNT(*) > 1
			)
			AND CallID NOT IN (
				SELECT MIN(CallID) FROM data_call 
				GROUP BY PhoneNumber,OppositePhoneNumber,CallDate,CallTime,TalkTime 
				HAVING COUNT(*) > 1
			)
		) AS t
	);
	#ServNo
	UPDATE data_call SET RightNumSigns = '0x400'
		WHERE OppositePhoneNumber LIKE '10%' OR OppositePhoneNumber LIKE '11%' OR OppositePhoneNumber LIKE '12%'
			OR OppositePhoneNumber LIKE '400%' OR OppositePhoneNumber LIKE '95%';\n
	#IntlNo
	UPDATE data_call SET RightNumSigns = '0x500'
	 	WHERE OppositePhoneNumber LIKE '00%';
END 
//
DELIMITER ;
