package ImportData;

//import java.sql.DriverManager;
import java.sql.Statement;
//import java.sql.PreparedStatement;
//import java.sql.ResultSet;

public class DataProcess {
	public void dataProcess(ConnectMySQL connSql,String groupinfo) {
		//将数据从原始数据表进行筛选并提取到人员信息表和通话信息表中。此步骤在导入数据完成后即自动调用。
		
		//数据库名
		//String db = "groups";
		long startTime, endTime;
		startTime = System.currentTimeMillis();
		try {
			Statement stmt = connSql.conn.createStatement();
		    //PreparedStatement pst = connSql.conn.prepareStatement("");
			String sql = "";
			
			//#############数据处理#############################
			
			//#1.向tbl_user插入数据
			//主叫号码
			sql = "INSERT INTO tbl_user_"+groupinfo+"(PhoneNum,PhoneAreaCode,PhoneArea,NumberSource,Sign,IMSI,GroupInfo)" + 
					"SELECT DISTINCT 'PhoneNumber','PhoneAreaCode','PhoneArea','CallSource',1,'IMSI','GroupInfo'" + 
					"FROM tbl_data;";
			stmt.executeUpdate(sql);
			//pst.addBatch(sql);
			
			//被叫号码
			sql = "INSERT INTO tbl_user_"+groupinfo+"(PhoneNum,PhoneAreaCode,PhoneArea,Sign,GroupInfo)" + 
					"SELECT DISTINCT OppositePhoneNumber,OppositePhoneAreaCode,OppositePhoneArea,0,GroupInfo" + 
					"FROM tbl_data;";
			stmt.executeUpdate(sql);
			//pst.addBatch(sql);
			
			//#2.tbl_user表去重
			/* #as tmpresult表示建立临时表
			 * #
			 * #SQL_SAFE_UPDATES值为1时，以下三种情况无法正常操作，会出现ERROR 1175 (HY000): You are using safe update mode and you tried to update a table without a WHERE that uses a KEY column，设为0后可以执行
			 * #1:没有加where条件的全表更新操作 ;
			 * #2:加了where条件字段，但是where 字段 没有走索引的表更新 ;
			 * #3:全表delete 没有加where条件或者where 条件没有走索引。
			 */
			sql = "SET SQL_SAFE_UPDATES = 0;";
			stmt.executeUpdate(sql);
			//pst.addBatch(sql);
			
			sql = "DELETE FROM tbl_user_"+groupinfo+" \r\n" + 
					"WHERE\r\n" + 
					"    UserID IN (SELECT \r\n" + 
					"        UserID\r\n" + 
					"    FROM\r\n" + 
					"        (SELECT \r\n" + 
					"            UserID\r\n" + 
					"        FROM\r\n" + 
					"            tbl_user\r\n" + 
					"        \r\n" + 
					"        WHERE\r\n" + 
					"            PhoneNum IN (SELECT \r\n" + 
					"                PhoneNum\r\n" + 
					"            FROM\r\n" + 
					"                tbl_user\r\n" + 
					"            GROUP BY PhoneNum\r\n" + 
					"            HAVING COUNT(PhoneNum) > 1)\r\n" + 
					"            AND UserID NOT IN (SELECT \r\n" + 
					"                MIN(UserID)\r\n" + 
					"            FROM\r\n" + 
					"                tbl_user\r\n" + 
					"            GROUP BY PhoneNum\r\n" + 
					"            HAVING COUNT(PhoneNum) > 1)) AS tmpresult);";
			stmt.executeUpdate(sql);
			//pst.addBatch(sql);
			
			sql = "SET SQL_SAFE_UPDATES = 1;";
			stmt.executeUpdate(sql);
			//pst.addBatch(sql);

			//#3.向tbl_call插入数据
			sql = "INSERT INTO tbl_call_"+groupinfo+"(PhoneNum,OppositePhoneNum,CallDate,CallTime,TalkTime,CallType,CGI,CGIChinese,GroupInfo)"+
				  "SELECT DISTINCT PhoneNumber,OppositePhoneNumber,CallDate,CallTime,TalkTime,CallType,CGI,CGIChinese,GroupInfo"+
				  "FROM tbl_data;";
			stmt.executeUpdate(sql);
			//pst.addBatch(sql);
			//#4.tbl_call去重
			
			//#########################################################
			
			//pst.executeBatch();
			//connSql.conn.commit();
			stmt.close();
		    }
		catch(Exception e)
		{
			e.printStackTrace();
		}
		endTime = System.currentTimeMillis();
		System.out.println("创建tbl_user成功!\n运行时间：" + (endTime - startTime) + "ms");
	}
}
