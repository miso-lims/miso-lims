--StartNoTest
INSERT INTO Indices(name, sequence, position, indexFamilyId)
VALUES ('N517', 'GCGTAAGA', 2, (SELECT indexFamilyId FROM IndexFamily WHERE name = 'Nextera DNA Dual Index'));

SELECT userId INTO @user FROM User WHERE loginName = 'admin';
SET @time = NOW();

INSERT INTO TissueOrigin (alias, description, createdBy, creationDate, updatedBy, lastUpdated)
VALUES ('Pn', 'Peripheral Nerve', @user, @time, @user, @time),
('Um', 'Umbilical Cord', @user, @time, @user, @time);
--EndNoTest
