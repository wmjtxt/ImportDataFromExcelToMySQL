package ImportData;

//import java.sql.SQLException;

//import com.mysql.jdbc.Connection;

public class Main {
	public static void main(String[] args) {
		//long startTime, endTime;
		//createFile test
		/*String path = "E:\\dataProcess\\test";
		String filename = "graph";
		CreateFile cf = new CreateFile();
		cf.createFile(path,filename);*/
		
		//test InitSQL
		/*ConnectMySQL connSql = new ConnectMySQL();
		//Connection conn;
		try {
			startTime = System.currentTimeMillis();
			connSql.connectMySQL("");
			endTime = System.currentTimeMillis();
			System.out.println("连接数据库成功!\n连接数据库运行时间：" + (endTime - startTime) + "ms");
			InitSQL isql = new InitSQL();
			startTime = System.currentTimeMillis();
			isql.initSQL(connSql);
			endTime = System.currentTimeMillis();
			System.out.println("初始化数据库成功!\n运行时间：" + (endTime - startTime) + "ms");
			connSql.conn.close();
		}
		catch(Exception e){
			e.printStackTrace();
		}*/
		
		//outputFile test
		//DataOutput dataOp = new DataOutput();
		//dataOp.outputFile();
		System.out.println("helloworld");
	}
}
