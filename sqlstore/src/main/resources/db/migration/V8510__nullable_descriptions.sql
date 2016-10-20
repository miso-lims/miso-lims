ALTER TABLE Sample MODIFY description varchar(255) NULL;
ALTER TABLE Library MODIFY description varchar(255) NULL;
ALTER TABLE Pool MODIFY description varchar(255) NULL;
ALTER TABLE Run MODIFY description varchar(255) NULL;
ALTER TABLE Experiment MODIFY description varchar(255) NULL;
ALTER TABLE Platform MODIFY description varchar(255) NULL;
ALTER TABLE Subproject MODIFY description varchar(255) NULL;
ALTER TABLE Study MODIFY description varchar(255) NULL;

UPDATE Sample SET description = NULL WHERE description = '';
UPDATE Library SET description = NULL WHERE description = '';
UPDATE Pool SET description = NULL WHERE description = '';
UPDATE Run SET description = NULL WHERE description = '';
UPDATE Experiment SET description = NULL WHERE description = '';
UPDATE Platform SET description = NULL WHERE description = '';
UPDATE Subproject SET description = NULL WHERE description = '';
UPDATE Study SET description = NULL WHERE description = '';

ALTER TABLE Run MODIFY alias varchar(255) NOT NULL;

ALTER TABLE SamplePurpose DROP COLUMN description;
ALTER TABLE TissueMaterial DROP COLUMN description;
