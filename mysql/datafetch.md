根据筛选条件提取数据
====

* 筛选条件
	* 日期(CallDate)
	* 时间(CallTime)
	* 地区(PhoneArea)
	* 号码(PhoneNumber)
* 查询条件(sql where语句)
	* String dates: 开始日期：(startdate) 结束日期：(enddate)
	* String times: 开始时间：(starttime) 结束时间：(endtime)
	* String areas: 地区：(area)
	* String nums : 号码：(num)
	* 日期格式为yy-mm-dd,时间格式为hh:mm:ss,需要格式输入，地区和号码可以模糊查询
	* 初始值均设为"";
* 细节
	* 日期
		* 若startdate,enddate均不为空，则dates = "CallDate >= startdate and CallDate <= enddate";
		* 若startdate不为空,enddate为空，则dates = "CallDate >= startdate";
		* 若startdate为空,enddate不为空，则dates = "CallDate <= enddate";
	* 时间同上
	* areas和nums不为空，则areas = "PhoneArea = ares",nums = "PhoneNumber = num";
