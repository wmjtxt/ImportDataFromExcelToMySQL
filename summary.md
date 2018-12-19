数据预处理
====

* 0 预备工作
    * 0.0 安装数据库，配置环境，创建用户密码；
    * 0.1 执行sql脚本，创建数据库、数据表、存储过程；(datapro.sql)
* 1 导入数据
    * 1.1 使用poi读取指定文件夹的所有Excel文件中的表格数据到Map；(FileUtil.java，DataImport.readExcelContent())
    * 1.2 JDBC连接数据库（测试了MySQL,Oracle）；(ConnectSQL.java)
    * 1.3 从Map取数据，拼接insert语句;
    * 1.4 采用事务批量提交insert，将数据导入数据库。(DataImport.java)
* 2 数据处理
    * 2.0 执行存储过程（以下几项为存储过程具体工作）；(datapro.sql，proc_datapro)
    * 2.1 将原始数据提取到用户表、联系表；
    * 2.2 删除无效数据；
    * 2.3 对数据去重；
    * 2.4 按相关属性对数据进行标注分类（主要是对对方联系号码进行处理）。
* 3 提取数据
    * 3.0 根据提取条件，拼接select语句；(DataFetch.java)
    * 3.1 连接数据库，执行查询语句；
    * 3.2 返回查询结果（返回类型为二维string数组）。
