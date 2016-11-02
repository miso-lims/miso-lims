--StartNoTest
SET @time = NOW();
SELECT userId INTO @user FROM User WHERE loginName = 'admin';

INSERT INTO SamplePurpose (alias, createdBy, creationDate, updatedBy, lastUpdated)
VALUES ('Ion Torrent', @user, @time, @user, @time);

UPDATE Lab SET instituteId = (SELECT instituteId FROM Institute WHERE alias = 'Ottawa Hospital Research Institute')
WHERE alias = 'John Bell';

INSERT INTO Institute (alias, createdBy, creationDate, updatedBy, lastUpdated)
VALUES ('Sick Kids', @user, @time, @user, @time);
SET @institute = LAST_INSERT_ID();

INSERT INTO Lab (instituteId, alias, createdBy, creationDate, updatedBy, lastUpdated)
VALUES (@institute, 'Not Specified', @user, @time, @user, @time);

INSERT INTO Indices (name, sequence, position, indexFamilyId)
VALUES ('S517', 'GCGTAAGA', 2, (SELECT indexFamilyId FROM IndexFamily WHERE name = 'Nextera XT Dual Index'));
--EndNoTest
