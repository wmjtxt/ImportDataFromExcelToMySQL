# 需要删除的无效数据
# 1. 对方号码为空的 --> OppositePhoneNumber like " %"                       273114
delete from tbl_call where OppositePhoneNumber like " %"; -- 剩余84768
# 2. 重复数据（号码、对方号码、通话日期、通话时间、通话时长）相同的数据       9361


SET SQL_SAFE_UPDATES = 0;
delete from tbl_call_1 where CallID in (SELECT CallID FROM (SELECT CallID FROM tbl_call_1 WHERE (PhoneNum,OppositePhoneNum,CallDate,CallTime,TalkTime) IN (SELECT PhoneNum,OppositePhoneNum,CallDate,CallTime,TalkTime FROM tbl_call_1 GROUP BY PhoneNum,OppositePhoneNum,CallDate,CallTime,TalkTime HAVING COUNT(*) > 1) AND CallID NOT IN (SELECT MIN(CallID) FROM tbl_call_1 GROUP BY PhoneNum,OppositePhoneNum,CallDate,CallTime,TalkTime HAVING COUNT(*) > 1)) AS t);

select count(*) from tbl_call where CallID in (SELECT CallID FROM (SELECT CallID FROM tbl_call WHERE (PhoneNum,OppositePhoneNum,CallDate,CallTime,TalkTime) IN (SELECT PhoneNum,OppositePhoneNum,CallDate,CallTime,TalkTime FROM tbl_call GROUP BY PhoneNum,OppositePhoneNum,CallDate,CallTime,TalkTime HAVING COUNT(*) > 1) AND CallID NOT IN (SELECT MIN(CallID) FROM tbl_call GROUP BY PhoneNum,OppositePhoneNum,CallDate,CallTime,TalkTime HAVING COUNT(*) > 1)) AS t);


select count(*) from tbl_data;
select count(*) from tbl_call_1;
#75407
# 需要单独存储的数据
# 1. 10开头，如10086,10010,10000,1065/1069       8165/9389 /前为上步骤去重后个数
# 2. 11开头，如110,119,118,114                    205/216
# 3. 12开头，如120,12306                          113/118
# 4. 400开头                                       60/73
# total : 8543/9796
select count(*) from tbl_call where OppositePhoneNum like '10%' or OppositePhoneNum like '11%' or OppositePhoneNum like '12%' or OppositePhoneNum like '400%';

# 建立数据表tbl_serv_no
create table tbl_serv_no; -- 与tbl_call结构相同
# select data from tbl_call
# insert into tbl_serv_no
# delete data from tbl_call
insert into tbl_serv_no  
select count(*) from tbl_call where OppositePhoneNum like '10%' or OppositePhoneNum like '11%' or OppositePhoneNum like '12%' or OppositePhoneNum like '400%';

delete from tbl_call where OppositePhoneNum like '10%' or OppositePhoneNum like '11%' or OppositePhoneNum like '12%' or OppositePhoneNum like '400%';

#至此，有效数据共66864条
