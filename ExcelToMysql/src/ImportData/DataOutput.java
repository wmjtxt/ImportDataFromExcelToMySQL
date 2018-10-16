package ImportData;

import java.sql.Statement;
import java.sql.ResultSet;


//import java.io.*;
public class DataOutput {
	public void outputFile() {
		//数据库名
		String db = "case3";
		ConnectMySQL connSql = new ConnectMySQL();
		CreateFile cf = new CreateFile();
		long startTime,endTime;
		try {
			//连接数据库
			startTime = System.currentTimeMillis();
			Statement stmt = connSql.connectMySQL(db).createStatement();
			endTime = System.currentTimeMillis();
			System.out.println("连接数据库成功！\n运行时间：" + (endTime - startTime) + "ms");
		    
			String sql = "";
			
			//#4.以下查询结果作为图的edges
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
			System.out.println("select查询运行时间：" + (endTime - startTime) + "ms");
			
			//创建文件   
			startTime = System.currentTimeMillis();
			String path = "E:\\dataProcess\\result";
			String filename = "graph";
			
			startTime = System.currentTimeMillis();
			cf.createFile(path,filename);
		    endTime = System.currentTimeMillis();
			System.out.println("新建文件运行时间：" + (endTime - startTime) + "ms");
			
			//保存结果到文件
		    int rowCount = 0;
		    startTime = System.currentTimeMillis();
			while(ret.next()){
				int UserID = ret.getInt(1);// 获取第一列的值UserID
				int OppositeID = ret.getInt(2);// 获取第二列的值OppositeID
				cf.myFile.println(UserID + "," + OppositeID);
				rowCount++;
			}
			endTime = System.currentTimeMillis();
			System.out.println("总行数：" + rowCount);
			System.out.println("写入文件运行时间：" + (endTime - startTime) + "ms");
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