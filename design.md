Classes
====

|类名|功能|备注|
|----|----|----|
|InitSQL    |初始化数据库,包括创建数据库、数据表|首先，判断数据库或表是否存在，不存在则创建|
|DataImport |导入数据|将所有数据导入到原始数据表|
|DataProcess|保存有效数据|导入数据完成后进行,将有效数据从原始数据表提取到用户表和通话表中|
|Util       |获取文件路径名|在另一个package里,FileUtil|
|CreateFile |创建文件|这个类可以跟Util放在一个package里|
|ConnMySQL  |连接数据库||
|DataFetch  |提取数据|依筛选条件查询数据|

# Functions
## 目录

- [InitSQL](#initSQL)
- [DataImport](#dataimport)
- [DataProcess](#dataprocss)
- [Util](#util)
- [CreateFile](#createfile)
- [ConnMySQL](#connmysql)
- [DataFetch](#datafetch)

# InitSQL
	public void initSQL(ConnMySQL connSql, String db, String groupinfo);
# DataImport
	public DataImport(String filepath);
	public String[] readExcelTitle();
	public Map<Integer, Map<Integer, Object>> readExcelContend();
	private Object getCellFormatValue(Cell cell);
	public static void main(String[] args);
# DataProcess
	public void dataProcess(connMySQL connSql, String groupinfo);
# Util
	private static boolean matchType(File file, String fileTypes);
	public static String[] fileList(String filepath,String fileTypes);
# CreateFile
	public void createFile(String path, String filename);
# ConnMySQL
	public Connection connMySQL(String db);
# DataFetch
	public void dataFetch();
