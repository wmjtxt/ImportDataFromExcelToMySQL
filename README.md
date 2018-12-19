

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
* ***2018.11.04***<br>
	继续分析数据，对`tbl_call`去重，主要内容在`20181104.sql`，新建数据表`tbl_serv_no`存放服务号码的通话数据。
	有效数据量66864.
* ***2018.11.05***<br>
	之前的分析还是有遗漏，一是95开头的，二是931开头的。看样子95开头的都是服务号码，而931开头的号码却很奇怪。
	931是兰州的区号。931开头的号码有5337个，其中5335个是短消息，8位，比如93100120,93100038，并且都是93100开头。
	剩下两个是CDMA，应该是电话，需要保留在tbl_call。或者，搞不清楚931开头的号码是什么，就先全部保留着。
	另外，在win10下，存储过程的名字存放的表名不同，那判断存储过程存不存在的SQL语句就没办法统一了。
	实在不行的话，存储过程存在就先删除再创建，这样每次导数据就得创建存储过程。不过应该也耗费不了多少时间。
	* 今天继续做了根据筛选条件提取数据，这部分也不难。在DataFetch.java里。跟DataExport一样，返回String[]就行了。
	至于细节方面，比如要查询哪些数据，根据哪些筛选条件，可以再做修改。框架有了就好做了。
	那么，好像工作完成的差不多了，明天正好汇报，看接下来还需要做什么。

* ***2018.11.13***<br>
	**前几天做的忘了总结，今天补上**
	* 1.标志KeyUsers。在tbl_user表中设置人员标志属性Signs，重点用户为0x100，其他用户为0x200，插入数据时即赋值。
	低八位给用户加序号难以实现，因为向tbl_user插入数据时是通过select语句批量插入。autoincrement也许可以实现，
	但初始插入后还要去重，去重后，原序号就乱了。这个只能先放一放了。
	* 2.统计KeyUsers。之前的实现是把插入tbl_user表从存储过程里拿出来，新建一个类，添加一个查询KeyUsers方法来实现。
	~~现在看，其实插入数据到tbl_user可以仍旧放在存储过程里的。~~ 还是要放在存储过程之外。
	* 3.查询数据条件判别。这部分又加了几个条件，包括重点对象、通话类型、服务号码、国际号码等。目前还在做。

	**today**
	
	* tbl_call添加ServNoSigns,IntlNoSigns属性，标志对方号码是否为服务号码和国际号码，tbl_ser_no表暂时保留，
	后面不需要再删掉。
	* 在存储过程中修改相关语句。添加`Update tbl_call SET ServNoSigns = '1' where ...`删除原来的`Delete...`语句。
	至于国际用户，目前看，以00开头的可能是，其他还不确定。
	添加`Update tbl_call SET IntlNoSigns = '1' where OppositePhoneNumber like '00%';`
	* 发现有几条数据，对方号码是手机号前面加上00，00180××××0000这样的，然后搜索180××××0000这个手机号，
	有几百条数据的对方号码是这个号码，不知道这代表什么意思，是这号码偶尔在国外通话？还好这部分数据量不算大。
	* DataFetch条件，添加connTypes,persTypes,isServNo,isIntlNo。只有persTypes需要联表查询。

* ***2018.11.14***<br>
	* 整理了下之前的代码。现在有用的类有ConnectMySQL.java, InitSQL.java, Procedures.java, DataImport.java.
	* tbl_user，按KeySigns分类，0x000,0x100,0x200,0x300,分别表示
		* 0x000, 普通用户，仅在对方号码里出现过
		* 0x100, 重点用户
		* 0x200, 在对方号码里出现过的重点用户
		* 0x300, 对方号码里与至少两个重点用户联系过的（这个怎么查询？）
	* 0x300,搞定了，写在conn2keyuser.sql里，也加到procedure里了。真的麻烦啊这个，需要查询整个tbl_data表，挺耗时间的，
	* 0x100,0x200和0x300有重复,怎么处理？
	
* ***2018.11.15***
	* 昨天晚上发现，我对一些含义理解有误。再整理一下。
|词语|含义|备注|
|:--:|----|----|
|跟踪对象|指原始数据中左侧号码|0x100|
|至少联系两名跟踪对象者|右侧号码中与至少两名左侧号码联系过|0x300|
|0x000|右侧用户初始值|KeySigns|
|0x100|即跟踪对象，左侧用户初始值|KeySigns|
|0x200|右侧号码中在左侧也出现过的，即左侧号码集和右侧号码集的交集(应该没理解错吧)|KeySigns|
|0x300|至少联系两名跟踪对象者|KeySigns|
不过,0x200,0x300有重叠
	* 考虑在tbl_call表中添加属性PhoneNumberSigns,OppositePhoneNumberSigns,
  可以简化写成LeftNumSigns,RightNumSigns,即左侧号码标志，右侧号码标志
	* 初始时，左侧0x100,右侧0x000
	* 导完数据后,即提取users到tbl_user表中，在此过程中，可以查找右侧号码在左侧
    出现过的，RightNumSigns改为0x200;也可以查找右侧号码中至少联系两名左侧号码的，
    RightNumSigns = 0x300，注意，查找时要排除RightNumSigns = 0x200的号码。
	* ***不得已，又要改动表结构啦***，一改动表结构，就要改InitSQL.java(create table语句),Procedures.java(insert和update语句),DataImport.java(insert),DataFetch.java(select)等等
	* 又发现，好像LeftNumSigns没有用，保持对称吧，说不定以后有用
	* 改好在win10上运行，执行存储过程时间太久了，Ctrl+c了，不知道哪出问题了，明天再说。

* ***2018.11.16***
	* 执行0x300的update语句居然要一个多小时,不知道问题在哪。改了下，应该好了。
	* 关于存储过程，还是不太会用。还有触发器，sql其实没那么简单，觉得简单只是因为你知道的少。
* ***2018.11.17***
	* 添加类Configs.java,统计用户数，重要用户数等信息。
* ***2018.11.20***
	* 添加servnocnt,intlnocnt
* ***2018.11.22***
	* 去掉ServNoSigns,IntlNoSigns, RightNumSigns = 0x400代表服务号码,RightNumSigns = 0x500代表国际号码。
	* users,用户表的添加、删除、选择全部、修改密码
	* 导入数据时，选择文件路径，JFileChooser
	* config,getConfig
	* dataFetch:input,String[];output,String[]
	* todo: 
		* groupinfo,人群信息表的添加、删除、选择全部。
		* 单例模式,数据库连接

* ***2018.11.23***
	* 基本都做完了，剩下的就是修修补补。
	* 该写年会论文了。
* ***2018.12.02***
	* 年会论文已经水完。
	* 项目还需要再改：
		* 1.用户表、人群表
		* 2.导入数据DataImport.java
		* 3.能否适应多种数据库（尝试）
	* 添加win批处理文件datapro.bat

* ***2018.12.04***
	* 今天在Oracle上测试一下，发现Oracle跟MySQL差别好大啊。坑。different.md
	* Oracle的存储过程不会用啊！！！
	* ***Oracle也可以了***，数据库之间真是区别太大了。也不能随便就说哪个好。兴许是各有优劣吧，毕竟我知道的太少，不敢妄言。
	* 原来mysql也可以不加分号，只是加分号不会出错罢了。
* ***2018.12.05***
	* 完成了Oracle下的日期和时间整合，真是超麻烦。
	* 完成DataFetchFromOracle，其实还是处理日期，查询的时候也得加to_date和格式'yyyy-mm-dd hh24:mi:ss'。真的是...
	* 项目到此为止。
	* ***Oracle导入数据时，用PreparedStatement，事务，出现内存溢出？Exception in thread "main" java.lang.OutOfMemoryError: Java heap space,不知道是怎么回事*** Oracle你咋回事！？
	* 好像是导入到20万条数据的时候溢出的。tomcat不知道有用没，待会装了试一下。
	* 原来是addBatch，executeBatch的问题。addBatch很快，executeBatch超级慢，然后最后都卡在内存了？？？这是什么机制，反正MySQL没这问题。

* ***2018.12.12***
	* 项目还要写文档。报酬有的。哈哈哈。
	* 新系统已安装好,固态真快。
* ***2018.12.19***
    * 总结，见summary.md。
