package ImportData;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;

public class CreateFile {
	//long startTime, endTime;
	FileWriter resultFile;
	PrintWriter myFile;
	public void createFile(String path, String filename) {
		try {
			//创建文件和文件夹   
			//startTime = System.currentTimeMillis();
			
			//1.创建文件夹
			File myFolderPath = new File(path);   
			try {   
			    if (!myFolderPath.exists()) {
			       myFolderPath.mkdir();   
			    }   
			}   
			catch (Exception e) {   
			    System.out.println("新建目录操作出错");   
			    e.printStackTrace();   
			}
			
			//2.创建文件   
			File myFilePath = new File(path+"/"+filename+".csv");   
			try {
				int i = 1;
				while(myFilePath.exists()) {
					myFilePath = new File(path + "/" + filename + i + ".csv");
					i++;
				}
				
			    myFilePath.createNewFile();   
			    System.out.println("新建文件："+myFilePath);
			}
			catch (Exception e) {   
			    System.out.println("新建文件操作出错");   
			    e.printStackTrace();
			}
			resultFile = new FileWriter(myFilePath);   
		    myFile = new PrintWriter(resultFile);   
		    //myFile.println("hello,world");
		    //endTime = System.currentTimeMillis();
			//System.out.println("新建文件运行时间：" + (endTime - startTime) + "ms");
			//resultFile.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
}
