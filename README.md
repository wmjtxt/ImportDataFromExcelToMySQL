

## 1 用Java从Excel导数据到Mysql

* 步骤
    * 在Mysql数据库中先建好table
    * 从Excel表格读数据
    * 用JDBC连接Mysql数据库
    * 把读出的数据导入到Mysql数据库的相应表中

## 2 数据预处理
* ***previous***<br>
	DataImport,Procedures,InitSQL,ConnectMySQL,Main  
* ***2018.10.29***<br>
	添加DataExport.java，查询数据结果返回一个String[]，这样就可以输出到图结构中了。
* ***2018.11.01***<br>
	今天对数据认真研究了一下，各种各样的无效数据啊，我先分类汇总一下，最后再决定怎么筛选数据。
	`dataclean.md`,	`all.sql`,linux下的mysql workbench真的很不好用,很多语句在win10运行没问题的，
	在Linux就是不行，看来是mysql的问题。后面还是尽量用win10来做吧。
