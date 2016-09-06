SET @time = NOW();
SELECT userId INTO @user FROM User WHERE loginName = 'admin';

UPDATE QcPassedDetail SET description = "Ready" WHERE status=1 AND description = "";

UPDATE QcPassedDetail SET description = "Failed: STR" WHERE description = "Failed STR";
UPDATE QcPassedDetail SET description = "Failed: Diagnosis" WHERE description = "Failed Diagnosis";
UPDATE QcPassedDetail SET description = "Failed: QC" WHERE description = "Failed QC";

UPDATE QcPassedDetail SET description = "Not Ready" WHERE status IS NULL AND description = "";
UPDATE QcPassedDetail SET status = NULL WHERE description = "Reference Required";

INSERT INTO QcPassedDetail (status, description, noteRequired, createdBy, creationDate, updatedBy, lastUpdated)
VALUES (NULL, "Waiting: Receive Tissue", false, @user, @time, @user, @time);