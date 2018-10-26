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
		//���еĴ洢���̶��ڴ˺����ڴ����������������ֻ��Ҫִ��һ�Σ�
		//��ʵ�洢�����������ݿ��ϵģ�����û��Ҫÿ�ε��ô洢���̶���������ֱ࣬�ӵ��þ��С�
		//���������ֻ��Ҫ����һ�Σ������ݿ�����ݱ����õ�ʱ����þͿ��ԡ�
		//ԭ��һ���洢���̿��Դ�źܶ�SQL��䡭��
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
		//����
		String db = "test1";
		ConnectMySQL con = new ConnectMySQL();
		con.connectMySQL(db);
		System.out.println("�������ݿ�ɹ���");
		//Procedures proc = new Procedures(con,db);
		//���ô洢���̡���ÿ�ε������ݺ󣬵��ô洢���̽����ݴ�ԭʼ���ݱ���ȡ���û����ͨ�����С�
		CallableStatement callst = con.conn.prepareCall("call proc_dataprocess;");
		callst.executeUpdate();
	}
}
