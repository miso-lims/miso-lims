ALTER TABLE QCType ADD COLUMN archived BOOLEAN NOT NULL DEFAULT FALSE;
ALTER TABLE QCType ADD COLUMN precisionAfterDecimal INT NOT NULL DEFAULT 0;

-- StartNoTest
DELETE FROM QCType WHERE name = 'STR';
UPDATE QCType SET precisionAfterDecimal = 0 WHERE name = 'Tape Station';
UPDATE QCType SET precisionAfterDecimal = -1 WHERE name = 'DNAse Treated';
UPDATE QCType SET name = 'Qubit' WHERE name = 'QuBit';
-- EndNoTest

ALTER TABLE LibraryQC ADD CONSTRAINT fk_libraryQc_library FOREIGN KEY (library_libraryId) REFERENCES Library (libraryId);
ALTER TABLE LibraryQC ADD CONSTRAINT fk_libraryQc_qcType FOREIGN KEY (qcMethod) REFERENCES QCType (qcTypeId);

-- StartNoTest
INSERT INTO QCType (name, description, qcTarget, units) 
SELECT 'Insert Size', 'Insert Size', 'Library', 'bp' FROM DUAL
WHERE NOT EXISTS (SELECT * FROM QCType WHERE qcTarget = 'Library' AND units = 'bp') LIMIT 1;

SELECT qcTypeId INTO @tapeStationId FROM QCType WHERE qcTarget = 'Library' AND units = 'bp'; 
INSERT INTO LibraryQC (library_libraryId, qcUserName, qcDate, results, qcMethod, insertSize) 
  SELECT library_libraryId, qcUserName, qcDate, insertSize, @tapeStationId, 1 FROM LibraryQC WHERE insertSize <> 0;
-- EndNoTest

ALTER TABLE SampleQC CHANGE COLUMN qcUserName qcCreator varchar(255) NOT NULL;
ALTER TABLE LibraryQC CHANGE COLUMN qcUserName qcCreator varchar(255) NOT NULL;
ALTER TABLE PoolQC CHANGE COLUMN qcUserName qcCreator varchar(255) NOT NULL;
ALTER TABLE RunQC CHANGE COLUMN qcUserName qcCreator varchar(255) NOT NULL;
