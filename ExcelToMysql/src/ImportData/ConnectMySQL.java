package ImportData;

import java.sql.DriverManager;


import com.mysql.jdbc.Connection;

public class ConnectMySQL {
	//声明connection对象
	Connection conn = null;
	public Connection connectMySQL(String db) {
	    //驱动程序名
		String driver = "com.mysql.jdbc.Driver";
		//url指向要访问的数据库
		//String url = "jdbc:mysql://localhost:3306/wmj?&useSSL=false";  //设置url，wmj是database
		String url = "jdbc:mysql://localhost:3306/"+db+"?&useSSL=false";
		//用户名和密码
		String user = "root";
		String passwd = "root123";
		//long startTime, endTime;
		try {
			//startTime = System.currentTimeMillis();
			//加载驱动程序
		    Class.forName(driver);
		    //连接数据库
		    conn = (Connection)DriverManager.getConnection(url, user, passwd);
		    
			//endTime = System.currentTimeMillis();
			//System.out.println("连接数据库成功!\n连接数据库运行时间：" + (endTime - startTime) + "ms");
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return conn;
	}
}
