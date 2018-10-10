package ImportData;

import java.sql.DriverManager;
import java.sql.Statement;
import java.sql.ResultSet;

import com.mysql.jdbc.Connection;

public class DataProcess {
	public static void main(String[] args) {
		//声明connection对象
	    Connection conn;
	    //驱动程序名
		String driver = "com.mysql.jdbc.Driver";
		//数据库名
		String db = "case2";
		//url指向要访问的数据库
		//String url = "jdbc:mysql://localhost:3306/wmj?&useSSL=false";  //设置url，wmj是database
		String url = "jdbc:mysql://localhost:3306/"+db+"?&useSSL=false";
		//用户名和密码
		String user = "root";
		String passwd = "root123";
		try {			
			//加载驱动程序
		    Class.forName(driver);
		    //连接数据库
		    conn = (Connection)DriverManager.getConnection(url, user, passwd);
		    Statement stmt = conn.createStatement();
		    ResultSet ret;
			String sql = "";
			
			/*//查询数据,测试用
			sql = "select count(*) from sys.sys_config";
			ResultSet ret = stmt.executeQuery(sql);
			if(ret.next()) {
				System.out.print("count="+ret.getInt(1));
			}*/
			//#############数据处理#############################
			
			//#1.建立tbl_user表, nodes
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

			//#2.tbl_user插入数据
			sql = "INSERT INTO tbl_user(PhoneNum,PhoneAreaCode,PhoneArea,NumberSource,Sign,IMSI)\r\n" + 
					"SELECT DISTINCT PhoneNumber,PhoneAreaCode,PhoneArea,CallSource,1,IMSI\r\n" + 
					"FROM tbl_data;";
			stmt.executeUpdate(sql);

			//#3.tbl_user插入数据
			sql = "INSERT INTO tbl_user(PhoneNum,PhoneAreaCode,PhoneArea,Sign)\r\n" + 
					"SELECT DISTINCT OppositePhoneNumber,OppositePhoneAreaCode,OppositePhoneArea,0\r\n" + 
					"FROM tbl_data;";
			stmt.executeUpdate(sql);


			//#4.tbl_user去重
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
			
			/* #as tmpresult表示建立临时表
			 * #
			 * #SQL_SAFE_UPDATES值为1时，以下三种情况无法正常操作，会出现ERROR 1175 (HY000): You are using safe update mode and you tried to update a table without a WHERE that uses a KEY column，设为0后可以执行
			 * #1:没有加where条件的全表更新操作 ;
			 * #2:加了where条件字段，但是where 字段 没有走索引的表更新 ;
			 * #3:全表delete 没有加where条件或者where 条件没有走索引。
			 */


			//#5.以下查询结果作为图的edges
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
			while(ret.next()){
				int UserID = ret.getInt(1);// 获取第一列的值UserID
				int OppositeID = ret.getInt(2);// 获取第二列的值OppositeID
				System.out.println(UserID + ", " + OppositeID);
			}
			/*---------------------
			作者：Above_my_point 
			来源：CSDN 
			原文：https://blog.csdn.net/above_my_point/article/details/78934163?utm_source=copy 
			版权声明：本文为博主原创文章，转载请附上博文链接！*/
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