SELECT tbl_call.CallRecordType,CallDate,CallTime,TalkTime,tbl_call.PhoneAreaCode,tbl_call.PhoneNumber,tbl_call.OppositePhoneAreaCode,tbl_call.OppositePhoneNumber,tbl_call.CallSign,tbl_call.CGI,tbl_call.CGIChinese 
FROM tbl_call WHERE tbl_call.GroupInfo = 's001' 
AND tbl_call.CallDate >= '2016-09-09' and tbl_call.CallDate <= '2016-09-09'
ORDER BY tbl_call.PhoneNumber,tbl_call.CallDate,tbl_call.CallTime;


SELECT tbl_call.CallRecordType,CallDate,CallTime,TalkTime,tbl_call.PhoneAreaCode,tbl_call.PhoneNumber,tbl_call.OppositePhoneAreaCode,tbl_call.OppositePhoneNumber,tbl_call.CallSign,tbl_call.CGI,tbl_call.CGIChinese 
FROM tbl_call join tbl_user on tbl_call.PhoneNumber = tbl_user.PhoneNumber WHERE tbl_call.GroupInfo = 's001' 
AND tbl_call.CallDate >= '2016-09-09' and tbl_call.CallDate <= '2016-09-09'
ORDER BY tbl_call.PhoneNumber,tbl_call.CallDate,tbl_call.CallTime;

SELECT tbl_call.CallRecordType,CallDate,CallTime,TalkTime,tbl_call.PhoneAreaCode,tbl_call.PhoneNumber,OppositePhoneAreaCode,OppositePhoneNumber,CallSign,CGI,CGIChinese
FROM tbl_call WHERE tbl_call.GroupInfo = 's001' 
AND tbl_call.CallDate >= '2016-09-01' and tbl_call.CallDate <= '2016-09-09'
and CallTime >= "00:00:00" and CallTime <= "05:00:00" 
and tbl_call.PhoneArea like '%兰州%' 
ORDER BY tbl_call.PhoneNumber,tbl_call.CallDate,tbl_call.CallTime;

SELECT tbl_call.CallRecordType,CallDate,CallTime,TalkTime,tbl_call.PhoneAreaCode,tbl_call.PhoneNumber,OppositePhoneAreaCode,OppositePhoneNumber,CallSign,CGI,CGIChinese,tbl_user.KeySigns
FROM tbl_call join tbl_user on tbl_call.OppositePhoneNumber = tbl_user.PhoneNumber WHERE tbl_call.GroupInfo = 's001' 
AND tbl_call.CallDate >= '2016-09-01' and tbl_call.CallDate <= '2016-09-09'
and CallTime >= "00:00:00" and CallTime <= "05:00:00" 
and tbl_call.PhoneArea like '%兰州%' 
and tbl_user.KeySigns = '0x300' 
ORDER BY tbl_call.PhoneNumber,tbl_call.CallDate,tbl_call.CallTime;

SELECT tbl_call.CallRecordType,CallDate,CallTime,TalkTime,tbl_call.PhoneAreaCode,tbl_call.PhoneNumber,OppositePhoneAreaCode,OppositePhoneNumber,CallSign,CGI,CGIChinese,tbl_user.KeySigns
FROM tbl_call join tbl_user WHERE tbl_call.GroupInfo = 's001' 
AND tbl_call.CallDate >= '2016-09-01' and tbl_call.CallDate <= '2016-09-09'
and CallTime >= "00:00:00" and CallTime <= "05:00:00" 
and tbl_call.PhoneArea like '%兰州%' 
and tbl_user.KeySigns = '0x300' 
ORDER BY tbl_call.PhoneNumber,tbl_call.CallDate,tbl_call.CallTime;