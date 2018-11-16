#0x200,右侧号码里在左侧也出现过的
UPDATE tbl_data SET OppositePhoneAreaCode = '0x200' WHERE OppositePhoneNumber IN (
SELECT OppositePhoneNumber FROM (
SELECT distinct OppositePhoneNumber FROM tbl_data WHERE OppositePhoneNumber IN (
SELECT distinct PhoneNumber FROM tbl_data 
)) AS t);

SELECT OppositePhoneNumber FROM (
SELECT distinct OppositePhoneNumber FROM tbl_data WHERE OppositePhoneNumber IN (
SELECT distinct PhoneNumber FROM tbl_data ) )as t;

SELECT count(*) FROM (
SELECT distinct OppositePhoneNumber FROM tbl_data WHERE OppositePhoneNumber IN (
SELECT distinct PhoneNumber FROM tbl_data ) )as t;

use datapro1;
select count(*) from tbl_data where RightNumSigns = '0x300';

#0x300,对方号码里与至少两个跟踪对象联系的
UPDATE tbl_data SET RightNumSigns = '0x300' WHERE OppositePhoneNumber IN (
select OppositePhoneNumber from(
select OppositePhoneNumber from (
select OppositePhoneNumber from tbl_data where length(OppositePhoneNumber)>1
group by OppositePhoneNumber,PhoneNumber
) as t group by OppositePhoneNumber having count(*) > 1
)as t1);

select count(*) from(
select OppositePhoneNumber from (
select OppositePhoneNumber from tbl_data where length(OppositePhoneNumber)>1
group by OppositePhoneNumber,PhoneNumber
) as t group by OppositePhoneNumber having count(*) > 1
)as t1;

select OppositePhoneNumber,count(*) from (
select OppositePhoneNumber from tbl_data where length(OppositePhoneNumber)>1
group by OppositePhoneNumber,PhoneNumber
) as t group by OppositePhoneNumber having count(*) > 1;
