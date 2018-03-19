package ImportData;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import FileUtil.Util;

import com.mysql.jdbc.Connection;
import java.sql.*;

public class ImportDataFromExcelToMysql {
	private Logger logger = LoggerFactory.getLogger(ImportDataFromExcelToMysql.class);
	private Workbook wb;
	private Sheet sheet;
	private Row row;
	private static String[] filepathlist;    //�ļ����б�
	private static boolean titleflag = false;//�жϱ����Ƿ�Ϊ����
	
	public ImportDataFromExcelToMysql(String filepath) {
		if (filepath == null) {
			return;
		}
		String ext = filepath.substring(filepath.lastIndexOf("."));//��ȡ�ļ���ʽ
		try {
			InputStream is = new FileInputStream(filepath);
			if (".xls".equals(ext)) {
				wb = new HSSFWorkbook(is);
			} else if (".xlsx".equals(ext)) {
				wb = new XSSFWorkbook(is);
			} else {
				wb = null;
			}
		} catch (FileNotFoundException e) {
			logger.error("FileNotFoundException", e);
		} catch (IOException e) {
			logger.error("IOException", e);
		}
	}

	/**
	 * ��ȡExcel����ͷ������
	 */
	public String[] readExcelTitle() throws Exception {
		titleflag = false;
		if (wb == null) {
			throw new Exception("Workbook����Ϊ�գ�");
		}
		
		sheet = wb.getSheetAt(0);
		row = sheet.getRow(0);
		
		// ����������
		int colNum = row.getPhysicalNumberOfCells();
		System.out.println(colNum);
		String[] title = new String[colNum];
		for (int i = 0; i < colNum; i++) {
			//title[i] = getStringCellValue(row.getCell((short) i));
			title[i] = getCellFormatValue(row.getCell(i)).toString();
		}
		
		//�ж�title��һ���ֶ��Ƿ�Ϊ���֣�����֪��title�Ƿ�Ϊ����
		Cell cell = row.getCell(0);
		switch (cell.getCellTypeEnum()){//.getCellType()) {
		case NUMERIC:
		case FORMULA: {
			// �жϵ�ǰ��cell�Ƿ�ΪDate
			if (DateUtil.isCellDateFormatted(cell)) {
				titleflag = false;
			} else {// ����Ǵ�����
				titleflag = true;
			}
			break;
		}
		case STRING:// �����ǰCell��TypeΪSTRING
			titleflag = false;
			break;
		default:// Ĭ�ϵ�Cellֵ
			titleflag = false;
		}
		
		return title;
	}

	/**
	 * ��ȡExcel��������
	 */
	public Map<Integer, Map<Integer, Object>> readExcelContent() throws Exception {
		if (wb == null) {
			throw new Exception("Workbook����Ϊ�գ�");
		}
		Map<Integer, Map<Integer, Object>> content = new HashMap<Integer, Map<Integer, Object>>();

		sheet = wb.getSheetAt(0);
		// �õ�������
		int rowNum = sheet.getLastRowNum();
		row = sheet.getRow(0);
		int colNum = row.getPhysicalNumberOfCells();
		// ��������Ӧ�ôӵڶ��п�ʼ,��һ��Ϊ��ͷ�ı���
		for (int i = 1; i <= rowNum; i++) {
			row = sheet.getRow(i);
			int j = 0;
			Map<Integer, Object> cellValue = new HashMap<Integer, Object>();
			while (j < colNum) {
				Object obj = getCellFormatValue(row.getCell(j));
				cellValue.put(j, obj);
				j++;
			}
			content.put(i, cellValue);
		}
		return content;
	}

	/**
	 * ����Cell������������
	 */
	private Object getCellFormatValue(Cell cell) {
		Object cellvalue = "";
		if (cell != null) {
			// �жϵ�ǰCell��Type
			switch (cell.getCellTypeEnum()){//.getCellType()) {
			case NUMERIC:
			case FORMULA: {
				// �жϵ�ǰ��cell�Ƿ�ΪDate
				if (DateUtil.isCellDateFormatted(cell)) {
					// �����Date������ת��ΪData��ʽ
					// data��ʽ�Ǵ�ʱ����ģ�2013-7-10 0:00:00
					// cellvalue = cell.getDateCellValue().toLocaleString();
					// data��ʽ�ǲ�����ʱ����ģ�2013-7-10
					Date date = cell.getDateCellValue();
					cellvalue = date;
				} else {// ����Ǵ�����

					// ȡ�õ�ǰCell����ֵ
					cellvalue = String.valueOf((int)cell.getNumericCellValue());
				}
				break;
			}
			case STRING:// �����ǰCell��TypeΪSTRING
				// ȡ�õ�ǰ��Cell�ַ���
				cellvalue = "\""+cell.getRichStringCellValue().getString()+"\"";
				//�ַ�������˫����""������������ʱ�����
				break;
			default:// Ĭ�ϵ�Cellֵ
				cellvalue = "";
			}
		} else {
			cellvalue = "";
		}
		return cellvalue;
	}

	public static void main(String[] args) {
		try {			
			//connection
		    Class.forName("com.mysql.jdbc.Driver");
		    String url = "jdbc:mysql://localhost:3306/wmj?&useSSL=false";  //����url��wmj��database
		    Connection conn;//��������
		    conn = (Connection)DriverManager.getConnection(url, "root", "root123");//username="root",password = "root123"
		    Statement stmt = conn.createStatement();
			
			filepathlist = Util.fileList("D:\\lzu\\����Ԥ����\\Data", ".xls,.xlsx");//���������ļ��к������ļ�����
			
			
			
			for(String filepath : filepathlist) {
				//��������ǰ
				String t = "";
				String sql = "select count(*) from tbl_data_bak";
				ResultSet ret = stmt.executeQuery(sql);
				if(ret.next()) {
					System.out.print("count="+ret.getInt(1));
				}
				
				ImportDataFromExcelToMysql excelReader = new ImportDataFromExcelToMysql(filepath);
				// �Զ�ȡExcel���������
				String[] title = excelReader.readExcelTitle();
				//System.out.println("���Excel���ı���:");
				for (String s : title) {
					//System.out.print(s + "| ");
					t += s + ",";
				}
				t = t.substring(0,t.length()-1);
				//System.out.println("title = " + t);
				
				// ������������ݣ������
				if(titleflag) {
					sql = "insert into tbl_data_bak values(" + t + ");";
					stmt.executeUpdate(sql);
				}
				
				// �������ݺ�
				sql = "select count(*) from tbl_data_bak";
				ret = stmt.executeQuery(sql);
				if(ret.next()) {
					System.out.print("count="+ret.getInt(1));
				}
				
				// �Զ�ȡExcel������ݲ���
				Map<Integer, Map<Integer, Object>> map = excelReader.readExcelContent();
				//System.out.println("���Excel��������:");
				for (int i = 1; i <= map.size(); i++) {
					//System.out.println(map.get(i));
					sql = map.get(i).values().toString().substring(1,map.get(i).values().toString().length()-1);
					//System.out.println("sql=" + sql);
					stmt.executeUpdate("insert into tbl_data_bak values("+sql+");");
				}
				
				// �������ݺ�
				sql = "select count(*) from tbl_data_bak";
				ret = stmt.executeQuery(sql);
				if(ret.next()) {
					System.out.print("count="+ret.getInt(1));
				}
			}
			stmt.close();
		    conn.close();
		    }
		/*catch(SQLException e) {
			e.printStackTrace();
		}*/
		catch (FileNotFoundException e) {
			System.out.println("δ�ҵ�ָ��·�����ļ�!");
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
