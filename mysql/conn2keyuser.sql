select OppositePhoneNumber,PhoneNumber,count(*) from tbl_data  where length(OppositePhoneNumber) > 1 
group by OppositePhoneNumber,PhoneNumber;


select OppositePhoneNumber from tbl_data where OppositePhoneNumber in (select distinct OppositePhoneNumber from tbl_data where length(OppositePhoneNumber) > 1 group by OppositePhoneNumber,PhoneNumber having count(*) > 1) group by OppositePhoneNumber having count(*) > 1;

select OppositePhoneNumber,PhoneNumber from tbl_data where OppositePhoneNumber like '0014101%' group by OppositePhoneNumber,PhoneNumber having count(*) > 1;
select OppositePhoneNumber,PhoneNumber from tbl_data where OppositePhoneNumber like '0014101%';
select OppositePhoneNumber from tbl_data where OppositePhoneNumber in (select OppositePhoneNumber 
from (select distinct OppositePhoneNumber from tbl_data where OppositePhoneNumber like '0014101%' 
group by OppositePhoneNumber,PhoneNumber having count(*) > 1) as t);

select distinct OppositePhoneNumber from tbl_data where OppositePhoneNumber like '0014101%' 
group by OppositePhoneNumber,PhoneNumber having count(*) > 1;


#对方号码里至少和两个跟踪号码联系过的
UPDATE tbl_user SET KeySigns = '0x300' WHERE PhoneNumber IN (select OppositePhoneNumber from (select OppositePhoneNumber from tbl_data where length(OppositePhoneNumber)>1 not like ' %' 
group by OppositePhoneNumber,PhoneNumber having count(*) > 1) as t group by OppositePhoneNumber having count(*) > 1);

select distinct OppositePhoneNumber,PhoneNumber from tbl_data where OppositePhoneNumber = '95580';