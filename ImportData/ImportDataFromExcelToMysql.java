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
	private static String[] filepathlist;    //文件名列表
	private static boolean titleflag = false;//判断标题是否为数据
	
	public ImportDataFromExcelToMysql(String filepath) {
		if (filepath == null) {
			return;
		}
		String ext = filepath.substring(filepath.lastIndexOf("."));//获取文件格式
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
	 * 读取Excel表格表头的内容
	 */
	public String[] readExcelTitle() throws Exception {
		titleflag = false;
		if (wb == null) {
			throw new Exception("Workbook对象为空！");
		}
		
		sheet = wb.getSheetAt(0);
		row = sheet.getRow(0);
		
		// 标题总列数
		int colNum = row.getPhysicalNumberOfCells();
		System.out.println(colNum);
		String[] title = new String[colNum];
		for (int i = 0; i < colNum; i++) {
			//title[i] = getStringCellValue(row.getCell((short) i));
			title[i] = getCellFormatValue(row.getCell(i)).toString();
		}
		
		//判断title第一个字段是否为数字，即可知道title是否为数据
		Cell cell = row.getCell(0);
		switch (cell.getCellTypeEnum()){//.getCellType()) {
		case NUMERIC:
		case FORMULA: {
			// 判断当前的cell是否为Date
			if (DateUtil.isCellDateFormatted(cell)) {
				titleflag = false;
			} else {// 如果是纯数字
				titleflag = true;
			}
			break;
		}
		case STRING:// 如果当前Cell的Type为STRING
			titleflag = false;
			break;
		default:// 默认的Cell值
			titleflag = false;
		}
		
		return title;
	}

	/**
	 * 读取Excel数据内容
	 */
	public Map<Integer, Map<Integer, Object>> readExcelContent() throws Exception {
		if (wb == null) {
			throw new Exception("Workbook对象为空！");
		}
		Map<Integer, Map<Integer, Object>> content = new HashMap<Integer, Map<Integer, Object>>();

		sheet = wb.getSheetAt(0);
		// 得到总行数
		int rowNum = sheet.getLastRowNum();
		row = sheet.getRow(0);
		int colNum = row.getPhysicalNumberOfCells();
		// 正文内容应该从第二行开始,第一行为表头的标题
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
	 * 根据Cell类型设置数据
	 */
	private Object getCellFormatValue(Cell cell) {
		Object cellvalue = "";
		if (cell != null) {
			// 判断当前Cell的Type
			switch (cell.getCellTypeEnum()){//.getCellType()) {
			case NUMERIC:
			case FORMULA: {
				// 判断当前的cell是否为Date
				if (DateUtil.isCellDateFormatted(cell)) {
					// 如果是Date类型则，转化为Data格式
					// data格式是带时分秒的：2013-7-10 0:00:00
					// cellvalue = cell.getDateCellValue().toLocaleString();
					// data格式是不带带时分秒的：2013-7-10
					Date date = cell.getDateCellValue();
					cellvalue = date;
				} else {// 如果是纯数字

					// 取得当前Cell的数值
					cellvalue = String.valueOf((int)cell.getNumericCellValue());
				}
				break;
			}
			case STRING:// 如果当前Cell的Type为STRING
				// 取得当前的Cell字符串
				cellvalue = "\""+cell.getRichStringCellValue().getString()+"\"";
				//字符串加上双引号""，否则导入数据时会出错
				break;
			default:// 默认的Cell值
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
		    String url = "jdbc:mysql://localhost:3306/wmj?&useSSL=false";  //设置url，wmj是database
		    Connection conn;//创建连接
		    conn = (Connection)DriverManager.getConnection(url, "root", "root123");//username="root",password = "root123"
		    Statement stmt = conn.createStatement();
			
			filepathlist = Util.fileList("D:\\lzu\\数据预处理\\Data", ".xls,.xlsx");//导入数据文件夹和数据文件类型
			
			
			
			for(String filepath : filepathlist) {
				//插入数据前
				String t = "";
				String sql = "select count(*) from tbl_data_bak";
				ResultSet ret = stmt.executeQuery(sql);
				if(ret.next()) {
					System.out.print("count="+ret.getInt(1));
				}
				
				ImportDataFromExcelToMysql excelReader = new ImportDataFromExcelToMysql(filepath);
				// 对读取Excel表格标题测试
				String[] title = excelReader.readExcelTitle();
				//System.out.println("获得Excel表格的标题:");
				for (String s : title) {
					//System.out.print(s + "| ");
					t += s + ",";
				}
				t = t.substring(0,t.length()-1);
				//System.out.println("title = " + t);
				
				// 如果标题是数据，则插入
				if(titleflag) {
					sql = "insert into tbl_data_bak values(" + t + ");";
					stmt.executeUpdate(sql);
				}
				
				// 插入数据后
				sql = "select count(*) from tbl_data_bak";
				ret = stmt.executeQuery(sql);
				if(ret.next()) {
					System.out.print("count="+ret.getInt(1));
				}
				
				// 对读取Excel表格内容测试
				Map<Integer, Map<Integer, Object>> map = excelReader.readExcelContent();
				//System.out.println("获得Excel表格的内容:");
				for (int i = 1; i <= map.size(); i++) {
					//System.out.println(map.get(i));
					sql = map.get(i).values().toString().substring(1,map.get(i).values().toString().length()-1);
					//System.out.println("sql=" + sql);
					stmt.executeUpdate("insert into tbl_data_bak values("+sql+");");
				}
				
				// 插入数据后
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
			System.out.println("未找到指定路径的文件!");
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
