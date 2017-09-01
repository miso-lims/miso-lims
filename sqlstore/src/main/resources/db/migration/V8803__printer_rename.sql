-- StartNoTest
UPDATE Printer SET backend = 'BRADY_FTP', configuration = JSON_OBJECT('host', JSON_EXTRACT(CAST(configuration AS JSON), '$.host'), 'pin', JSON_EXTRACT(CAST(configuration AS JSON), '$.password'))  WHERE backend = 'FTP';
-- EndNoTest
UPDATE Printer SET driver = 'BRADY_BPT_635_488' WHERE backend = 'BRADY_M80';
UPDATE Printer SET driver = 'BRADY_THT_181_492_3' WHERE backend = 'BRADY_STANDARD';
