-- ts_many_to_many

CREATE TABLE `TargetedSequencingTemp` (
  `targetedSequencingId` bigint NOT NULL AUTO_INCREMENT,
  `alias` varchar(255) NOT NULL,
  `description` varchar(255) NOT NULL,
  `archived` bit NOT NULL DEFAULT b'0',
  `createdBy` bigint NOT NULL,
  `creationDate` datetime NOT NULL,
  `updatedBy` bigint NOT NULL,
  `lastUpdated` datetime NOT NULL,
  PRIMARY KEY (`targetedSequencingId`),
  UNIQUE KEY `UK_TargetedResequencing_a_kdi2` (`alias`),
  KEY `FK_TargetedResequencing_cb2` (`createdBy`),
  KEY `FK_TargetedResequencing_ub2` (`updatedBy`),
  CONSTRAINT `FK_TargetedResequencing_cb2` FOREIGN KEY (`createdBy`) REFERENCES `User` (`userId`),
  CONSTRAINT `FK_TargetedResequencing_ub2` FOREIGN KEY (`updatedBy`) REFERENCES `User` (`userId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE TargetedSequencing_KitDescriptor (
  targetedSequencingId bigint NOT NULL,
  kitDescriptorId bigint NOT NULL,
  PRIMARY KEY (targetedSequencingId,kitDescriptorId),
  CONSTRAINT TK_KitDescriptor_FK FOREIGN KEY (kitDescriptorId) REFERENCES KitDescriptor (kitDescriptorId)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

INSERT INTO TargetedSequencingTemp(targetedSequencingId, alias, description, archived, createdBy, creationDate, updatedBy, lastUpdated)
SELECT targetedSequencingId, alias, description, archived, createdBy, creationDate, updatedBy, lastUpdated FROM TargetedSequencing;

INSERT INTO TargetedSequencing_KitDescriptor(targetedSequencingId, kitDescriptorId)
SELECT targetedSequencingId, kitDescriptorId FROM TargetedSequencing;

-- StartNoTest

-- Delete foreign keys if they exist.

set @var=if((SELECT true FROM information_schema.TABLE_CONSTRAINTS WHERE
            CONSTRAINT_SCHEMA = DATABASE() AND
            TABLE_NAME        = 'LibraryDilution' AND
            CONSTRAINT_NAME   = 'FK_ld_targetedSequencing_targetedSequencingId' AND
            CONSTRAINT_TYPE   = 'FOREIGN KEY') = true,'ALTER TABLE LibraryDilution
            DROP FOREIGN KEY FK_ld_targetedSequencing_targetedSequencingId','select 1');

prepare stmt from @var;
execute stmt;
deallocate prepare stmt;

set @var=if((SELECT true FROM information_schema.TABLE_CONSTRAINTS WHERE
            CONSTRAINT_SCHEMA = DATABASE() AND
            TABLE_NAME        = 'LibraryDilution' AND
            CONSTRAINT_NAME   = 'fk_libraryDilution_targetedSequencing' AND
            CONSTRAINT_TYPE   = 'FOREIGN KEY') = true,'ALTER TABLE LibraryDilution
            DROP FOREIGN KEY fk_libraryDilution_targetedSequencing','select 1');

prepare stmt from @var;
execute stmt;
deallocate prepare stmt;
-- EndNoTest

DROP TABLE TargetedSequencing;
ALTER TABLE TargetedSequencingTemp RENAME TO TargetedSequencing;

ALTER TABLE LibraryDilution ADD CONSTRAINT FK_ld_targetedSequencing_targetedSequencingId FOREIGN KEY (targetedSequencingId) REFERENCES TargetedSequencing (targetedSequencingId);
ALTER TABLE TargetedSequencing_KitDescriptor ADD CONSTRAINT TK_TargetedSequencing_FK FOREIGN KEY (targetedSequencingId) REFERENCES TargetedSequencing (targetedSequencingId);


-- archived_qcTypes

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


-- library_size

ALTER TABLE Library ADD COLUMN dnaSize bigint;

UPDATE Library SET dnaSize = (SELECT insertSize FROM LibraryQC WHERE LibraryQC.library_libraryId = Library.libraryId AND insertSize IS NOT NULL AND insertSize != 0 ORDER BY qcDate LIMIT 1);


