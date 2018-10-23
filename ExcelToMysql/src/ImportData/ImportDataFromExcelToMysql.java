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

//import com.mysql.jdbc.Connection;
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
		//System.out.println("colNum="+colNum);
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
		ConnectMySQL connSql = new ConnectMySQL();
		InitSQL isql = new InitSQL();
		//数据存放路径
		String path = "/home/wmj/dataProcess/Data/";
		String db = "groups";//数据库名
		String groupInfo = "1";
		long startTime, endTime;
		long startTime1, endTime1;
		try {
		    //Statement stmt = conn.createStatement();
			connSql.connectMySQL("");//开始连接时不指定数据库
		    System.out.println("连接数据库成功!");
		    
			//文件路径
			filepathlist = Util.fileList(path, ".xls,.xlsx");
			int cnt = 0;//统计文件个数
			//初始化数据库
			isql.initSQL(connSql,db,groupInfo);
			connSql.connectMySQL(db);//重新指定数据库连接
			
			PreparedStatement pst = connSql.conn.prepareStatement("");
			//设置事务不自动提交
			connSql.conn.setAutoCommit(false);
			startTime = System.currentTimeMillis();
			for(String filepath : filepathlist) {
				startTime1 = System.currentTimeMillis();
				System.out.println("filepath="+filepath);
				if(filepath == null)
					break;
				cnt++;
				//title
				//需要作为左值的，设为StringBuffer，仅作为右值的，设为String即可
				StringBuffer tbuf = new StringBuffer();
				//sql语句
				String prefix = "insert into tbl_data values(";//sql前缀
				StringBuffer suffix = new StringBuffer();//sql后缀
				
				//读取Excel数据
				ImportDataFromExcelToMysql excelReader = new ImportDataFromExcelToMysql(filepath);
				// 对读取Excel表格标题测试
				String[] titles = excelReader.readExcelTitle();
				//System.out.println("获得Excel表格的标题:");
				for (String s : titles) {
					//System.out.print(s + "| ");
					tbuf.append(s + ",");
				}
				String title = tbuf.substring(0,tbuf.length()-1);
				//System.out.println("title = " + t);
				
				// 如果标题是数据，则插入
				if(titleflag) {
					//stmt.executeUpdate(prefix + suffix.append(title + ");"));
					pst.addBatch(prefix+suffix.append(title + ","+groupInfo+");"));
				}
				
				// 对读取Excel表格内容测试
				Map<Integer, Map<Integer, Object>> map = excelReader.readExcelContent();
				//System.out.println("获得Excel表格的内容:");
				for (int i = 1; i <= map.size(); i++) {
					suffix.delete(0, suffix.length());//清空sql后缀
					//System.out.println(map.get(i));
					suffix.append(map.get(i).values().toString().substring(1,map.get(i).values().toString().length()-1)+","+groupInfo+");");
					//System.out.println("sql=" + sql);
					//stmt.executeUpdate(prefix+suffix);
					pst.addBatch(prefix+suffix);
					
				}
				pst.executeBatch();
				connSql.conn.commit();
				endTime1 = System.currentTimeMillis();
				System.out.println("插入文件成功！文件名：" + filepath);
				System.out.println("\n运行时间：" + (endTime1 - startTime1) + "ms");
			}
			endTime = System.currentTimeMillis();
			System.out.println("导入数据成功！\n共导入" + cnt + "个文件");
			System.out.println("总运行时间：" + (endTime - startTime) + "ms");
			
			DataProcess dp = new DataProcess();
			dp.dataProcess(connSql,groupInfo);

			pst.close();
		    connSql.conn.close();
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