package ImportData;

import java.sql.DriverManager;
import java.sql.Statement;
import java.sql.ResultSet;

import com.mysql.jdbc.Connection;

public class DataProcess {
	public static void main(String[] args) {
		//����connection����
	    Connection conn;
	    //����������
		String driver = "com.mysql.jdbc.Driver";
		//���ݿ���
		String db = "case3";
		//urlָ��Ҫ���ʵ����ݿ�
		//String url = "jdbc:mysql://localhost:3306/wmj?&useSSL=false";  //����url��wmj��database
		String url = "jdbc:mysql://localhost:3306/"+db+"?&useSSL=false";
		//�û���������
		String user = "root";
		String passwd = "root123";
		try {			
			//������������
		    Class.forName(driver);
		    //�������ݿ�
		    conn = (Connection)DriverManager.getConnection(url, user, passwd);
		    Statement stmt = conn.createStatement();
		    ResultSet ret;
			String sql = "";
			
			//#############���ݴ���#############################
			
			//#1.����tbl_user��, ���nodes
			sql = "DROP TABLE IF EXISTS tbl_user;";
			stmt.executeUpdate(sql);
			
			sql = "CREATE TABLE tbl_user (\r\n" + 
					"    UserID INT NOT NULL AUTO_INCREMENT,\r\n" + 
					"    PhoneNum VARCHAR(20),\r\n" + 
					"    PhoneAreaCode VARCHAR(10),\r\n" + 
					"    PhoneArea VARCHAR(20),\r\n" + 
					"    NumberSource VARCHAR(20),\r\n" + 
					"    Sign VARCHAR(1),\r\n" + 
					"    IMSI VARCHAR(20),\r\n" + 
					"    Name VARCHAR(20),\r\n" + 
					"    IDCardNum VARCHAR(20),\r\n" + 
					"    PRIMARY KEY (UserID)\r\n" + 
					");";
			stmt.executeUpdate(sql);

			//#2.��tbl_user��������
			//���к���
			sql = "INSERT INTO tbl_user(PhoneNum,PhoneAreaCode,PhoneArea,NumberSource,Sign,IMSI)\r\n" + 
					"SELECT DISTINCT PhoneNumber,PhoneAreaCode,PhoneArea,CallSource,1,IMSI\r\n" + 
					"FROM tbl_data;";
			stmt.executeUpdate(sql);
			
			//���к���
			sql = "INSERT INTO tbl_user(PhoneNum,PhoneAreaCode,PhoneArea,Sign)\r\n" + 
					"SELECT DISTINCT OppositePhoneNumber,OppositePhoneAreaCode,OppositePhoneArea,0\r\n" + 
					"FROM tbl_data;";
			stmt.executeUpdate(sql);
			
			
			//#3.tbl_user��ȥ��
			/* #as tmpresult��ʾ������ʱ��
			 * #
			 * #SQL_SAFE_UPDATESֵΪ1ʱ��������������޷����������������ERROR 1175 (HY000): You are using safe update mode and you tried to update a table without a WHERE that uses a KEY column����Ϊ0�����ִ��
			 * #1:û�м�where������ȫ����²��� ;
			 * #2:����where�����ֶΣ�����where �ֶ� û���������ı���� ;
			 * #3:ȫ��delete û�м�where��������where ����û����������
			 */
			sql = "SET SQL_SAFE_UPDATES = 0;";
			stmt.executeUpdate(sql);
			
			sql = "DELETE FROM tbl_user \r\n" + 
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
			
			sql = "SET SQL_SAFE_UPDATES = 1;";
			stmt.executeUpdate(sql);

			//#4.���²�ѯ�����Ϊͼ��edges
			sql = "SELECT DISTINCT\r\n" + 
					"    (SELECT \r\n" + 
					"            UserID\r\n" + 
					"        FROM\r\n" + 
					"            tbl_user\r\n" + 
					"        WHERE\r\n" + 
					"            PhoneNumber = tbl_user.PhoneNum) AS UserID,\r\n" + 
					"    (SELECT \r\n" + 
					"            UserID\r\n" + 
					"        FROM\r\n" + 
					"            tbl_user\r\n" + 
					"        WHERE\r\n" + 
					"            OppositePhoneNumber = tbl_user.PhoneNum) AS OppositeID\r\n" + 
					"FROM\r\n" + 
					"    tbl_data;";
			ret = stmt.executeQuery(sql);
			//���
			while(ret.next()){
				int UserID = ret.getInt(1);// ��ȡ��һ�е�ֵUserID
				int OppositeID = ret.getInt(2);// ��ȡ�ڶ��е�ֵOppositeID
				System.out.println(UserID + "," + OppositeID);
			}
			//#########################################################
			
			
			stmt.close();
		    conn.close();
		    }
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
}
