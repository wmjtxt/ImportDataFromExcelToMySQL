use test1;
select * from testt;
select name from test1
union all
select name from test
where name = "Tom";

select test.name from test;

create table test1(id int, name varchar(10), vacation varchar(10));
insert into testt values(1,"Tom","teacher"),(2,"Bill","student");
insert into testt values(3,"Tom","teacher"),(4,"Bill","student");
insert into testt values(5,"Mary","actress"),(6,"Jack","Professor");