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
			//�����ļ����ļ���   
			//startTime = System.currentTimeMillis();
			
			//1.�����ļ���
			File myFolderPath = new File(path);   
			try {   
			    if (!myFolderPath.exists()) {
			       myFolderPath.mkdir();   
			    }   
			}   
			catch (Exception e) {   
			    System.out.println("�½�Ŀ¼��������");   
			    e.printStackTrace();   
			}
			
			//2.�����ļ�   
			File myFilePath = new File(path+"/"+filename+".csv");   
			try {
				int i = 1;
				while(myFilePath.exists()) {
					myFilePath = new File(path + "/" + filename + i + ".csv");
					i++;
				}
				
			    myFilePath.createNewFile();   
			    System.out.println("�½��ļ���"+myFilePath);
			}
			catch (Exception e) {   
			    System.out.println("�½��ļ���������");   
			    e.printStackTrace();
			}
			resultFile = new FileWriter(myFilePath);   
		    myFile = new PrintWriter(resultFile);   
		    //myFile.println("hello,world");
		    //endTime = System.currentTimeMillis();
			//System.out.println("�½��ļ�����ʱ�䣺" + (endTime - startTime) + "ms");
			//resultFile.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
}
