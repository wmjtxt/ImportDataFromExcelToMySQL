package ImportData;

import java.sql.Statement;
import java.sql.ResultSet;


//import java.io.*;
public class DataOutput {
	public void outputFile() {
		//���ݿ���
		String db = "case3";
		ConnectMySQL connSql = new ConnectMySQL();
		CreateFile cf = new CreateFile();
		long startTime,endTime;
		try {
			//�������ݿ�
			startTime = System.currentTimeMillis();
			Statement stmt = connSql.connectMySQL(db).createStatement();
			endTime = System.currentTimeMillis();
			System.out.println("�������ݿ�ɹ���\n����ʱ�䣺" + (endTime - startTime) + "ms");
		    
			String sql = "";
			
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
			startTime = System.currentTimeMillis();
			ResultSet ret = stmt.executeQuery(sql);
			endTime = System.currentTimeMillis();
			System.out.println("select��ѯ����ʱ�䣺" + (endTime - startTime) + "ms");
			
			//�����ļ�   
			startTime = System.currentTimeMillis();
			String path = "E:\\dataProcess\\result";
			String filename = "graph";
			
			startTime = System.currentTimeMillis();
			cf.createFile(path,filename);
		    endTime = System.currentTimeMillis();
			System.out.println("�½��ļ�����ʱ�䣺" + (endTime - startTime) + "ms");
			
			//���������ļ�
		    int rowCount = 0;
		    startTime = System.currentTimeMillis();
			while(ret.next()){
				int UserID = ret.getInt(1);// ��ȡ��һ�е�ֵUserID
				int OppositeID = ret.getInt(2);// ��ȡ�ڶ��е�ֵOppositeID
				cf.myFile.println(UserID + "," + OppositeID);
				rowCount++;
			}
			endTime = System.currentTimeMillis();
			System.out.println("��������" + rowCount);
			System.out.println("д���ļ�����ʱ�䣺" + (endTime - startTime) + "ms");
			cf.resultFile.close();
			//#########################################################
			
			
			stmt.close();
			connSql.conn.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
}