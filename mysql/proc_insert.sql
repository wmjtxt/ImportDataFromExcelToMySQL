truncate table test1.tbl_user_1;
delimiter //
drop procedure if exists proc_insert;

create procedure proc_insert()
begin

 -- 此处可以添加任意个SQL语句

end
//

call proc_insert();

show create procedure proc_insert;
show procedure status;-- 显示所有存储过程
