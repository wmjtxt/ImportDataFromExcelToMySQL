#############数据处理#############################

#1.建立tbl_user表, nodes
drop table if exists tbl_user;
create table tbl_user (
    UserID int not null auto_increment,
    PhoneNum varchar(20),
    PhoneAreaCode varchar(10),
    PhoneArea varchar(20),
    NumberSource varchar(20),
    Sign varchar(1),
    IMSI varchar(20),
    Name varchar(20),
    IDCardNum varchar(20),
    primary key (UserID)
);

#2.tbl_user插入数据
insert into tbl_user(PhoneNum,PhoneAreaCode,PhoneArea,NumberSource,Sign,IMSI)
select distinct PhoneNumber,PhoneAreaCode,PhoneArea,CallSource,1,IMSI
from tbl_data;

#3.tbl_user插入数据
insert into tbl_user(PhoneNum,PhoneAreaCode,PhoneArea,Sign)
select distinct OppositePhoneNumber,OppositePhoneAreaCode,OppositePhoneArea,0
from tbl_data;


#4.tbl_user去重
set SQL_SAFE_UPDATES = 0;
delete from tbl_user 
where
    UserID in (select 
        UserID
    from
        (select 
            UserID
        from
            tbl_user
        
        where
            PhoneNum in (select 
                PhoneNum
            from
                tbl_user
            group by PhoneNum
            having count(PhoneNum) > 1)
            and UserID not in (select 
                min(UserID)
            from
                tbl_user
            group by PhoneNum
            having count(PhoneNum) > 1)) as tmpresult);
#as tmpresult表示建立临时表
set SQL_SAFE_UPDATES = 1;
#
#SQL_SAFE_UPDATES值为1时，以下三种情况无法正常操作，会出现ERROR 1175 (HY000): You are using safe update mode and you tried to update a table without a WHERE that uses a KEY column，设为0后可以执行
#1:没有加where条件的全表更新操作 ;
#2:加了where条件字段，但是where 字段 没有走索引的表更新 ;
#3:全表delete 没有加where条件或者where 条件没有走索引。


#5.以下查询结果可以作为图的edges
select distinct
    (select 
            UserID
        from
            tbl_user
        where
            PhoneNumber = tbl_user.PhoneNum) as UserID,
    (select 
            UserID
        from
            tbl_user
        where
            OppositePhoneNumber = tbl_user.PhoneNum) as OppositeID
from
    tbl_data;

#########################################################