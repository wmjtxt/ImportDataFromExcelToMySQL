package ImportData;

import com.mysql.jdbc.Connection;

import java.sql.DriverManager;

import java.sql.Statement;

public class CreateTable {
	public static void main(String[] args) {
		//声明connection对象
	    Connection conn;
	    //驱动程序名
		String driver = "com.mysql.jdbc.Driver";
		//url指向要访问的数据库
		//String url = "jdbc:mysql://localhost:3306/wmj?&useSSL=false";  //设置url，wmj是database
		String url = "jdbc:mysql://localhost:3306/?&useSSL=false";
		//用户名和密码
		String user = "root";
		String passwd = "root123";
		//数据库名
		String db = "case2";
		//表名
		String tbl_config = "tbl_config";
		String tbl_data = "tbl_data";
		try {			
			//加载驱动程序
		    Class.forName(driver);
		    //连接数据库
		    conn = (Connection)DriverManager.getConnection(url, user, passwd);
		    Statement stmt = conn.createStatement();
			String sql = "";
			
			/*//查询所有数据,测试用
			sql = "select count(*) from sys.sys_config";
			ResultSet ret = stmt.executeQuery(sql);
			if(ret.next()) {
				System.out.print("count="+ret.getInt(1));
			}*/
			
			//创建数据库，一个数据库对应一个案件，包含表：tbl_config,tbl_data等
			sql = "CREATE DATABASE if not exists " + db;
			stmt.executeUpdate(sql);
			
			//创建信息表
			sql = "CREATE TABLE if not exists " + db + "." + tbl_config +
					"(variable varchar(20), value varchar(20), set_time date)";
			stmt.executeUpdate(sql);
			
			//创建数据表
			sql = "CREATE TABLE if not exists " + db + "." + tbl_data + " (\r\n" + 
					"  `CallID` varchar(5) NOT NULL,\r\n" + 
					"  `CallRecordType` varchar(20) DEFAULT NULL,\r\n" + 
					"  `CallSource` varchar(10) DEFAULT NULL,\r\n" + 
					"  `CallListType` varchar(10) DEFAULT NULL,\r\n" + 
					"  `CallIdentification` varchar(10) DEFAULT NULL,\r\n" + 
					"  `RoamingIdentifier` varchar(10) DEFAULT NULL,\r\n" + 
					"  `CallDate` date DEFAULT NULL,\r\n" + 
					"  `CallTime` time DEFAULT NULL,\r\n" + 
					"  `TalkTime` int(5) DEFAULT NULL,\r\n" + 
					"  `PhoneAreaCode` varchar(4) DEFAULT NULL,\r\n" + 
					"  `PhoneArea` varchar(20) DEFAULT NULL,\r\n" + 
					"  `PhoneNumber` varchar(20) NOT NULL,\r\n" + 
					"  `OppositePhoneAreacode` varchar(10) DEFAULT NULL,\r\n" + 
					"  `OppositePhoneArea` varchar(20) DEFAULT NULL,\r\n" + 
					"  `OppositePhoneNumber` varchar(20) DEFAULT NULL,\r\n" + 
					"  `CallType` varchar(10) DEFAULT NULL,\r\n" + 
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
					"  `OppositeCID` varchar(10) DEFAULT NULL\r\n" + 
					") ENGINE=InnoDB DEFAULT CHARSET=utf8";
			stmt.executeUpdate(sql);
		
			stmt.close();
		    conn.close();
		    }
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
}
