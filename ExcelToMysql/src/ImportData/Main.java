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
			System.out.println("�������ݿ�ɹ�!\n�������ݿ�����ʱ�䣺" + (endTime - startTime) + "ms");
			InitSQL isql = new InitSQL();
			startTime = System.currentTimeMillis();
			isql.initSQL(connSql);
			endTime = System.currentTimeMillis();
			System.out.println("��ʼ�����ݿ�ɹ�!\n����ʱ�䣺" + (endTime - startTime) + "ms");
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
