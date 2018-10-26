package ImportData;

import java.sql.Statement;
import java.sql.CallableStatement;
import java.sql.SQLException;

public class Procedures {
	private ConnectMySQL connsql;
	private String db;
	//private String groupinfo;
	public Procedures(ConnectMySQL connsql, String db) throws SQLException {
		this.connsql = connsql;
		this.db = db;
		this.defineProc();
	}
	private void defineProc() throws SQLException {
		//所有的存储过程都在此函数内创建，所以这个函数只需要执行一次？
		//其实存储过程是在数据库上的，根本没必要每次调用存储过程都调用这个类，直接调用就行。
		//所以这个类只需要调用一次，在数据库和数据表都建好的时候调用就可以。
		//原来一个存储过程可以存放很多SQL语句……
		Statement stmt = connsql.conn.createStatement();
		String sql = "DROP PROCEDURE IF EXISTS proc_dataprocess;";
		stmt.executeUpdate(sql);
		sql = "CREATE PROCEDURE proc_dataprocess()\r\n" + 
			  "BEGIN\r\n" + 
			  "INSERT into "+db+".tbl_user_1\r\n" + 
			  " 	select distinct null,PhoneNumber,PhoneAreaCode,PhoneArea,CallSource,1,IMSI,null,null,1\r\n" +
			  " 	from "+db+".tbl_data;\r\n" + 
			  "INSERT into "+db+".tbl_user_1\r\n" + 
			  "	select distinct null,OppositePhoneNumber,OppositePhoneAreaCode,OppositePhoneArea,null,0,IMSI,null,null,1\r\n" +
			  "	from "+db+".tbl_data;\r\n" +
			  "SET SQL_SAFE_UPDATES = 0;"+
			  "DELETE FROM tbl_user_1\r\n" + 
			  "WHERE\r\n" + 
				"    UserID IN (SELECT \r\n" + 
				"        UserID\r\n" + 
				"    FROM\r\n" + 
				"        (SELECT \r\n" + 
				"            UserID\r\n" + 
				"        FROM\r\n" + 
				"            tbl_user_1\r\n" + 
				"        \r\n" + 
				"        WHERE\r\n" + 
				"            PhoneNum IN (SELECT \r\n" + 
				"                PhoneNum\r\n" + 
				"            FROM\r\n" + 
				"                tbl_user_1\r\n" + 
				"            GROUP BY PhoneNum\r\n" + 
				"            HAVING COUNT(PhoneNum) > 1)\r\n" + 
				"            AND UserID NOT IN (SELECT \r\n" + 
				"                MIN(UserID)\r\n" + 
				"            FROM\r\n" + 
				"                tbl_user_1\r\n" + 
				"            GROUP BY PhoneNum\r\n" + 
				"            HAVING COUNT(PhoneNum) > 1)) AS tmpresult);"+
				"SET SQL_SAFE_UPDATES = 1;"+
				"INSERT INTO tbl_call_1(PhoneNum,OppositePhoneNum,CallDate,CallTime,TalkTime,CallType,CGI,CGIChinese,GroupInfo)\r\n"+
				"	SELECT PhoneNumber,OppositePhoneNumber,CallDate,CallTime,TalkTime,CallType,CGI,CGIChinese,GroupInfo\r\n"+
				"	FROM tbl_data;\r\n"+
			  "END;";
		stmt.executeUpdate(sql);
	}

	public static void main(String[] args) throws SQLException {
		//测试
		String db = "test1";
		ConnectMySQL con = new ConnectMySQL();
		con.connectMySQL(db);
		System.out.println("连接数据库成功！");
		//Procedures proc = new Procedures(con,db);
		//调用存储过程。在每次导完数据后，调用存储过程将数据从原始数据表提取到用户表和通话表中。
		CallableStatement callst = con.conn.prepareCall("call proc_dataprocess;");
		callst.executeUpdate();
	}
}
