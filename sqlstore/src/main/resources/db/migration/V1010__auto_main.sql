-- run_approvers
ALTER TABLE _Group ADD COLUMN builtIn BOOLEAN NOT NULL DEFAULT FALSE;

INSERT INTO _Group (name, description, builtIn)
VALUES ('Run Approvers', 'Users allowed to approve run data', TRUE);

-- transfer_time
ALTER TABLE Transfer CHANGE COLUMN transferDate transferTime DATETIME NOT NULL;

-- drop_delete_procedures
DROP PROCEDURE IF EXISTS deleteContainer;
DROP PROCEDURE IF EXISTS deleteLibrary;
DROP PROCEDURE IF EXISTS deleteLibraryAliquot;
DROP PROCEDURE IF EXISTS deletePool;
DROP PROCEDURE IF EXISTS deleteRun;
DROP PROCEDURE IF EXISTS deleteSample;

-- qc_units
UPDATE QCType SET units = REPLACE(units, '&#181;', 'µ');
UPDATE QCType SET units = REPLACE(units, '&#37;', '%');
UPDATE QCType SET units = REPLACE(units, '&#178;', '²');
UPDATE QCType SET units = 'µL' WHERE units = 'uL';

-- project_clinical
ALTER TABLE Project ADD COLUMN clinical BOOLEAN NOT NULL DEFAULT FALSE;

