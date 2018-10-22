package ImportData;

import java.sql.PreparedStatement;

public class InitSQL {
	public void initSQL(ConnectMySQL connSql,String db) {
		//初始化数据库，建立信息表、原始数据表
		StringBuffer sql = new StringBuffer();
		try {
		    PreparedStatement pst = connSql.conn.prepareStatement("");
			
			//若数据库不存在，则创建数据库
			sql.append("CREATE DATABASE if not exists " + db);
			pst.addBatch(sql.toString());
			sql.delete(0, sql.length());
			
			//创建信息表
			/*sql.append("CREATE TABLE if not exists " + db + ".tbl_config(variable varchar(20), value varchar(20), set_time date)");
			pst.addBatch(sql.toString());
			sql.delete(0, sql.length());*/
			
			//创建tbl_user;
			sql.append("CREATE TABLE if not exists "+db+".tbl_user (" + 
					"    UserID INT NOT NULL AUTO_INCREMENT," + 
					"    PhoneNum VARCHAR(20)," + 
					"    PhoneAreaCode VARCHAR(10)," + 
					"    PhoneArea VARCHAR(20)," + 
					"    NumberSource VARCHAR(20)," + 
					"    Sign VARCHAR(1)," + 
					"    IMSI VARCHAR(20)," + 
					"    Name VARCHAR(20)," + 
					"    IDCardNum VARCHAR(20)," + 
					"	 GroupInfo varchar(20) NOT NULL,"+
					"    PRIMARY KEY (UserID)" + 
					");");
			pst.addBatch(sql.toString());
			sql.delete(0, sql.length());
			
			//创建tbl_call;
			sql.append("CREATE TABLE if not exists " + db + ".tbl_call("+
					"CallID INT NOT NULL AUTO_INCREMENT,"+
					"PhoneNum VARCHAR(20),"+
					"OppositePhoneNum varchar(20),"+
					"CallDate date,"+
					"CallTime time,"+
					"TalkTime int(5),"+
					"CallType varchar(10),"+
					"CGI varchar(50),"+
					"CGIChinese varchar(200),"+
					"GroupInfo varchar(20) NOT NULL,"+
					"PRIMARY KEY(CallID)"+
					");");
			pst.addBatch(sql.toString());
			sql.delete(0, sql.length());
			
			//若不存在则创建
			sql.append("CREATE TABLE if not exists " + db + ".tbl_data(\r\n" + 
					"  `CallID` varchar(5) NOT NULL,\r\n" + 
					"  `CallRecordType` varchar(20) DEFAULT NULL,\r\n" + 
					"  `CallSource` varchar(10) DEFAULT NULL,\r\n" + 
					"  `CallListType` varchar(10) DEFAULT NULL,\r\n" + 
					"  `CallIdentification` varchar(10) DEFAULT NULL,\r\n" + 
					"  `RoamingIdentifier` varchar(10) DEFAULT NULL,\r\n" + 
					"  `CallDate` date NOT NULL,\r\n" + 
					"  `CallTime` time NOT NULL,\r\n" + 
					"  `TalkTime` int(5) NOT NULL,\r\n" + 
					"  `PhoneAreaCode` varchar(4) DEFAULT NULL,\r\n" + 
					"  `PhoneArea` varchar(20) DEFAULT NULL,\r\n" + 
					"  `PhoneNumber` varchar(20) NOT NULL,\r\n" + 
					"  `OppositePhoneAreacode` varchar(10) DEFAULT NULL,\r\n" + 
					"  `OppositePhoneArea` varchar(20) DEFAULT NULL,\r\n" + 
					"  `OppositePhoneNumber` varchar(20) NOT NULL,\r\n" + 
					"  `CallType` varchar(10) NOT NULL,\r\n" + 
					"  `CallTypeChinese` varchar(30) DEFAULT NULL,\r\n" + 
					"  `CallSign` varchar(10) DEFAULT NULL,\r\n" + 
					"  `PhoneCallAreacode` varchar(4) DEFAULT NULL,\r\n" + 
					"  `PhoneCallArea` varchar(20) DEFAULT NULL,\r\n" + 
					"  `CallDescription` varchar(10) DEFAULT NULL,\r\n" + 
					"  `CardNumber` varchar(10) DEFAULT NULL,\r\n" + 
					"  `InternetAccount` varchar(20) DEFAULT NULL,\r\n" + 
					"  `IMSI` varchar(50) DEFAULT NULL,\r\n" + 
					"  `DeviceNumber` varchar(50) DEFAULT NULL,\r\n" + 
					"  `SwitchBoardID` varchar(20) DEFAULT NULL,\r\n" + 
					"  `CGI` varchar(50) DEFAULT NULL,\r\n" + 
					"  `CGIChinese` varchar(200) DEFAULT NULL,\r\n" + 
					"  `LAC` varchar(10) DEFAULT NULL,\r\n" + 
					"  `CID` varchar(10) DEFAULT NULL,\r\n" + 
					"  `OppositeLAC` varchar(10) DEFAULT NULL,\r\n" + 
					"  `OppositeCallAreacode` varchar(4) DEFAULT NULL,\r\n" + 
					"  `OppositeCallArea` varchar(20) DEFAULT NULL,\r\n" + 
					"  `OutRoute` varchar(20) DEFAULT NULL,\r\n" + 
					"  `InRoute` varchar(20) DEFAULT NULL,\r\n" + 
					"  `DynamicRoamingNumber` varchar(10) DEFAULT NULL,\r\n" + 
					"  `CTX` varchar(10) DEFAULT NULL,\r\n" + 
					"  `OppositeCTX` varchar(10) DEFAULT NULL,\r\n" + 
					"  `FarwardingNumber` varchar(10) DEFAULT NULL,\r\n" + 
					"  `CallCost` varchar(10) DEFAULT NULL,\r\n" + 
					"  `IP` varchar(20) DEFAULT NULL,\r\n" + 
					"  `UserName` varchar(20) DEFAULT NULL,\r\n" + 
					"  `HAIP` varchar(20) DEFAULT NULL,\r\n" + 
					"  `PdsnfaIP` varchar(20) DEFAULT NULL,\r\n" + 
					"  `PcfIP` varchar(20) DEFAULT NULL,\r\n" + 
					"  `BSID` varchar(20) DEFAULT NULL,\r\n" + 
					"  `RoamingIdentifier2` varchar(10) DEFAULT NULL,\r\n" + 
					"  `CardType` varchar(20) DEFAULT NULL,\r\n" + 
					"  `UserIP` varchar(20) DEFAULT NULL,\r\n" + 
					"  `OppositeIP` varchar(20) DEFAULT NULL,\r\n" + 
					"  `ConferenceID` varchar(10) DEFAULT NULL,\r\n" + 
					"  `NumberOfParticipants` varchar(10) DEFAULT NULL,\r\n" + 
					"  `UserAttachedNumber` varchar(20) DEFAULT NULL,\r\n" + 
					"  `OppositePhoneNumberArea` varchar(20) DEFAULT NULL,\r\n" + 
					"  `OppositeCID` varchar(10) DEFAULT NULL,\r\n" + 
					//"  PRIMARY KEY(`PhoneNumber`,`OppositePhoneNumber`,`CallDate`,`CallTime`,`TalkTime`,`CallType`)\r\n" +
					"  `GroupInfo` varchar(20) NOT NULL\n" +
					") ENGINE=MyISAM DEFAULT CHARSET=utf8");
			
			pst.addBatch(sql.toString());
			sql.delete(0, sql.length());
			
			//若原始数据表存在则清空
			sql.append("truncate table "+db+".tbl_data;");
			pst.addBatch(sql.toString());
			
			
			pst.executeBatch();
		
			pst.close();
		    connSql.conn.close();
		    }
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
}