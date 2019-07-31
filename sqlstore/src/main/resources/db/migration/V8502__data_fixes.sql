-- StartNoTest
SELECT userId INTO @user FROM User WHERE loginName = 'admin';

UPDATE Box SET description = NULL, lastModifier = @user WHERE description = '';
UPDATE Box SET locationBarcode = NULL, lastModifier = @user WHERE locationBarcode = '';

UPDATE Run SET health = 'Unknown' WHERE health IS NULL;
-- EndNoTest

ALTER TABLE Run MODIFY COLUMN health varchar(50) NOT NULL;
