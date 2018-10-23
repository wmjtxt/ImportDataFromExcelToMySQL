package ImportData;

//import java.sql.DriverManager;
import java.sql.Statement;
//import java.sql.PreparedStatement;
//import java.sql.ResultSet;

public class DataProcess {
	public void dataProcess(ConnectMySQL connSql,String groupinfo) {
		//�����ݴ�ԭʼ���ݱ����ɸѡ����ȡ����Ա��Ϣ���ͨ����Ϣ���С��˲����ڵ���������ɺ��Զ����á�
		
		//���ݿ���
		//String db = "groups";
		long startTime, endTime;
		startTime = System.currentTimeMillis();
		try {
			Statement stmt = connSql.conn.createStatement();
		    //PreparedStatement pst = connSql.conn.prepareStatement("");
			String sql = "";
			
			//#############���ݴ���#############################
			
			//#1.��tbl_user��������
			//���к���
			sql = "INSERT INTO tbl_user_"+groupinfo+"(PhoneNum,PhoneAreaCode,PhoneArea,NumberSource,Sign,IMSI,GroupInfo)" + 
					"SELECT DISTINCT 'PhoneNumber','PhoneAreaCode','PhoneArea','CallSource',1,'IMSI','GroupInfo'" + 
					"FROM tbl_data;";
			stmt.executeUpdate(sql);
			//pst.addBatch(sql);
			
			//���к���
			sql = "INSERT INTO tbl_user_"+groupinfo+"(PhoneNum,PhoneAreaCode,PhoneArea,Sign,GroupInfo)" + 
					"SELECT DISTINCT OppositePhoneNumber,OppositePhoneAreaCode,OppositePhoneArea,0,GroupInfo" + 
					"FROM tbl_data;";
			stmt.executeUpdate(sql);
			//pst.addBatch(sql);
			
			//#2.tbl_user��ȥ��
			/* #as tmpresult��ʾ������ʱ��
			 * #
			 * #SQL_SAFE_UPDATESֵΪ1ʱ��������������޷����������������ERROR 1175 (HY000): You are using safe update mode and you tried to update a table without a WHERE that uses a KEY column����Ϊ0�����ִ��
			 * #1:û�м�where������ȫ����²��� ;
			 * #2:����where�����ֶΣ�����where �ֶ� û���������ı���� ;
			 * #3:ȫ��delete û�м�where��������where ����û����������
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

			//#3.��tbl_call��������
			sql = "INSERT INTO tbl_call_"+groupinfo+"(PhoneNum,OppositePhoneNum,CallDate,CallTime,TalkTime,CallType,CGI,CGIChinese,GroupInfo)"+
				  "SELECT DISTINCT PhoneNumber,OppositePhoneNumber,CallDate,CallTime,TalkTime,CallType,CGI,CGIChinese,GroupInfo"+
				  "FROM tbl_data;";
			stmt.executeUpdate(sql);
			//pst.addBatch(sql);
			//#4.tbl_callȥ��
			
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
		System.out.println("����tbl_user�ɹ�!\n����ʱ�䣺" + (endTime - startTime) + "ms");
	}
}
