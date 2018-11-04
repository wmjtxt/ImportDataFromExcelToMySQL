CREATE TABLE if not exists tbl_serv_no(
	CallID INT NOT NULL AUTO_INCREMENT,
	PhoneNum VARCHAR(20) NOT NULL,
	PhoneAreaCode varchar(10),
	OppositePhoneNum varchar(20),
	OppositePhoneAreaCode varchar(10),
	CallRecordType varchar(20),
	CallDate date NOT NULL,
	CallTime time NOT NULL,
	TalkTime int(5) NOT NULL,
	CallType varchar(10),
	CallSign varchar(10),
	CGI varchar(50),
	CGIChinese varchar(200),
	GroupInfo varchar(20) NOT NULL,
	PRIMARY KEY(CallID,PhoneNum,OppositePhoneNum,CallDate,CallTime,TalkTime,CallSign)
	);
