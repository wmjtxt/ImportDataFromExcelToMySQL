package ImportData;

import java.sql.DriverManager;


import com.mysql.jdbc.Connection;

public class ConnectMySQL {
	//����connection����
	Connection conn = null;
	public Connection connectMySQL(String db) {
	    //����������
		String driver = "com.mysql.jdbc.Driver";
		//urlָ��Ҫ���ʵ����ݿ�
		//String url = "jdbc:mysql://localhost:3306/wmj?&useSSL=false";  //����url��wmj��database
		String url = "jdbc:mysql://localhost:3306/"+db+"?&useSSL=false";
		//�û���������
		String user = "root";
		String passwd = "root123";
		//long startTime, endTime;
		try {
			//startTime = System.currentTimeMillis();
			//������������
		    Class.forName(driver);
		    //�������ݿ�
		    conn = (Connection)DriverManager.getConnection(url, user, passwd);
		    
			//endTime = System.currentTimeMillis();
			//System.out.println("�������ݿ�ɹ�!\n�������ݿ�����ʱ�䣺" + (endTime - startTime) + "ms");
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return conn;
	}
}
