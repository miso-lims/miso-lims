-- transfers
CREATE TABLE Transfer (
  transferId bigint PRIMARY KEY AUTO_INCREMENT,
  transferDate DATE NOT NULL,
  senderLabId bigint,
  senderGroupId bigint,
  recipient varchar(255),
  recipientGroupId bigint,
  creator bigint NOT NULL,
  created TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  lastModifier bigint NOT NULL,
  lastModified TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_transfer_senderLab FOREIGN KEY (senderLabId) REFERENCES Lab (labId),
  CONSTRAINT fk_transfer_senderGroup FOREIGN KEY (senderGroupId) REFERENCES _Group (groupId),
  CONSTRAINT fk_transfer_recipientGroup FOREIGN KEY (recipientGroupId) REFERENCES _Group (groupId),
  CONSTRAINT fk_transfer_creator FOREIGN KEY (creator) REFERENCES User (userId),
  CONSTRAINT fk_transfer_modifier FOREIGN KEY (lastModifier) REFERENCES User (userId)
) Engine=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE Transfer_Sample (
  transferId bigint,
  sampleId bigint,
  received BOOLEAN,
  qcPassed BOOLEAN,
  qcNote varchar(255),
  PRIMARY KEY (transferId, sampleId),
  CONSTRAINT fk_sample_transfer FOREIGN KEY (transferId) REFERENCES Transfer (transferId),
  CONSTRAINT fk_transfer_sample FOREIGN KEY (sampleId) REFERENCES Sample (sampleId)
) Engine=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE Transfer_Library (
  transferId bigint,
  libraryId bigint,
  received BOOLEAN,
  qcPassed BOOLEAN,
  qcNote varchar(255),
  PRIMARY KEY (transferId, libraryId),
  CONSTRAINT fk_library_transfer FOREIGN KEY (transferId) REFERENCES Transfer (transferId),
  CONSTRAINT fk_transfer_library FOREIGN KEY (libraryId) REFERENCES Library (libraryId)
) Engine=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE Transfer_LibraryAliquot (
  transferId bigint,
  aliquotId bigint,
  received BOOLEAN,
  qcPassed BOOLEAN,
  qcNote varchar(255),
  PRIMARY KEY (transferId, aliquotId),
  CONSTRAINT fk_libraryAliquot_transfer FOREIGN KEY (transferId) REFERENCES Transfer (transferId),
  CONSTRAINT fk_transfer_libraryAliquot FOREIGN KEY (aliquotId) REFERENCES LibraryAliquot (aliquotId)
) Engine=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE Transfer_Pool (
  transferId bigint,
  poolId bigint,
  received BOOLEAN,
  qcPassed BOOLEAN,
  qcNote varchar(255),
  PRIMARY KEY (transferId, poolId),
  CONSTRAINT fk_pool_transfer FOREIGN KEY (transferId) REFERENCES Transfer (transferId),
  CONSTRAINT fk_transfer_pool FOREIGN KEY (poolId) REFERENCES Pool (poolId)
) Engine=InnoDB DEFAULT CHARSET=utf8;

ALTER TABLE Lab ADD COLUMN excludeFromPinery BOOLEAN NOT NULL DEFAULT FALSE;

-- StartNoTest
CREATE TABLE TemporaryTransfer (
  transferId bigint PRIMARY KEY AUTO_INCREMENT,
  projectId bigint,
  creator bigint,
  receivedDate DATE,
  labId bigint
) Engine=InnoDB DEFAULT CHARSET=utf8;

SELECT userId INTO @admin FROM User WHERE loginName = 'admin';
SET @now = NOW();

INSERT INTO Institute(alias, createdBy, updatedBy) VALUES
('External', @admin, @admin);

INSERT INTO Lab(instituteId, alias, createdBy, updatedBy, excludeFromPinery) VALUES
((SELECT instituteId FROM Institute WHERE alias = 'External'), 'Not Specified', @admin, @admin, TRUE);

SET @unknownLab = LAST_INSERT_ID();

INSERT INTO _Group (name, description)
VALUES ('Unspecified (Internal)', 'Default group created for sample transfer receipt');

SET @internalGroup = LAST_INSERT_ID();

INSERT INTO User_Group (users_userId, groups_groupId)
SELECT userId, @internalGroup
FROM User
WHERE INTERNAL = TRUE;

DELIMITER //

DROP FUNCTION IF EXISTS findParentWithLab//
CREATE FUNCTION findParentWithLab(pSampleId bigint) RETURNS bigint
  NOT DETERMINISTIC READS SQL DATA
BEGIN
  DECLARE vTissueId bigint;
  SET vTissueId = pSampleId;
  WHILE vTissueId IS NOT NULL AND NOT EXISTS (SELECT sampleId FROM SampleTissue WHERE sampleId = vTissueId AND labId IS NOT NULL) DO
    IF EXISTS (SELECT sampleId FROM DetailedSample WHERE sampleId = vTissueId) THEN
      SELECT parentId INTO vTissueId FROM DetailedSample WHERE sampleId = vTissueId;
    ELSE
      SET vTissueId = NULL;
    END IF;
  END WHILE;
  RETURN vTissueId;
END//

DELIMITER ;

CREATE TABLE TissueParentView (
  sampleId bigint,
  tissueId bigint
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

INSERT INTO TissueParentView (sampleId, tissueId)
SELECT sampleId, findParentWithLab(sampleId)
FROM Sample;

DROP FUNCTION findParentWithLab;

-- Construct inbound sample transfers
INSERT INTO TemporaryTransfer(projectId, receivedDate, creator, labId)
SELECT DISTINCT s.project_projectId, s.receivedDate, s.creator, COALESCE(st.labId, @unknownLab)
FROM Sample s
JOIN TissueParentView v ON v.sampleId = s.sampleId
LEFT JOIN SampleTissue st ON st.sampleId = v.tissueId
WHERE s.receivedDate IS NOT NULL;

INSERT INTO User_Group (users_userId, groups_groupId)
SELECT DISTINCT creator, @internalGroup FROM TemporaryTransfer
WHERE NOT EXISTS (SELECT 1 FROM User_Group WHERE users_userId = creator AND groups_groupId = @internalGroup);

INSERT INTO Transfer(transferId, transferDate, senderLabId, recipientGroupId, creator, created, lastModifier, lastModified)
SELECT transferId, receivedDate, labId, @internalGroup, creator, @now, creator, @now
FROM TemporaryTransfer;

INSERT INTO Transfer_Sample(transferId, sampleId, received, qcPassed, qcNote)
SELECT (
  SELECT transferId
  FROM TemporaryTransfer
  WHERE projectId = s.project_projectId
  AND receivedDate = s.receivedDate
  AND creator = s.creator
  AND labId = COALESCE(st.labId, @unknownLab)
), s.sampleId, TRUE, COALESCE(s.qcPassed, TRUE), IF(s.qcPassed = FALSE, 'Unspecified failure', NULL)
FROM Sample s
JOIN TissueParentView v ON v.sampleId = s.sampleId
LEFT JOIN SampleTissue st ON st.sampleId = v.tissueId
WHERE s.receivedDate IS NOT NULL;

DELETE FROM TemporaryTransfer;

-- Construct inbound library transfers
INSERT INTO TemporaryTransfer(projectId, receivedDate, creator, labId)
SELECT DISTINCT s.project_projectId, l.receivedDate, l.creator, COALESCE(st.labId, @unknownLab)
FROM Library l
JOIN Sample s ON s.sampleId = l.sample_sampleId
JOIN TissueParentView v ON v.sampleId = s.sampleId
LEFT JOIN SampleTissue st ON st.sampleId = v.tissueId
WHERE l.receivedDate IS NOT NULL;

INSERT INTO User_Group (users_userId, groups_groupId)
SELECT DISTINCT creator, @internalGroup FROM TemporaryTransfer
WHERE NOT EXISTS (SELECT 1 FROM User_Group WHERE users_userId = creator AND groups_groupId = @internalGroup);

INSERT INTO Transfer(transferId, transferDate, senderLabId, recipientGroupId, creator, created, lastModifier, lastModified)
SELECT transferId, receivedDate, labId, @internalGroup, @admin, @now, @admin, @now
FROM TemporaryTransfer;

INSERT INTO Transfer_Library(transferId, libraryId, received, qcPassed, qcNote)
SELECT (
  SELECT transferId
  FROM TemporaryTransfer
  WHERE projectId = s.project_projectId
  AND receivedDate = l.receivedDate
  AND creator = l.creator
  AND labId = COALESCE(st.labId, @unknownLab)
), l.libraryId, TRUE, COALESCE(l.qcPassed, TRUE), IF(l.qcPassed = FALSE, 'Unspecified failure', NULL)
FROM Library l
JOIN Sample s ON s.sampleId = l.sample_sampleId
JOIN TissueParentView v ON v.sampleId = s.sampleId
LEFT JOIN SampleTissue st ON st.sampleId = v.tissueId
WHERE l.receivedDate IS NOT NULL;

DROP TABLE TemporaryTransfer;
DROP TABLE TissueParentView;

-- Construct outbound sample transfers
CREATE TABLE TemporaryTransfer (
  transferId bigint PRIMARY KEY AUTO_INCREMENT,
  projectId bigint,
  distributionDate DATE,
  distributionRecipient varchar(250)
) Engine=InnoDB DEFAULT CHARSET=utf8;

-- Update auto_increment sequence
INSERT INTO TemporaryTransfer (transferId) VALUES ((SELECT MAX(transferId) FROM Transfer));
DELETE FROM TemporaryTransfer;

INSERT INTO TemporaryTransfer (projectId, distributionDate, distributionRecipient)
SELECT DISTINCT project_projectId, distributionDate, distributionRecipient
FROM Sample
WHERE distributed = TRUE;

INSERT INTO Transfer (transferId, transferDate, senderGroupId, recipient, creator, created, lastModifier, lastModified)
SELECT transferId, distributionDate, @internalGroup, distributionRecipient, @admin, @now, @admin, @now
FROM TemporaryTransfer;

INSERT INTO Transfer_Sample (transferId, sampleId, received, qcPassed)
SELECT (
  SELECT transferId
  FROM TemporaryTransfer
  WHERE projectId = s.project_projectId
  AND distributionDate = s.distributionDate
  AND distributionRecipient = s.distributionRecipient
), s.sampleId, TRUE, NULL
FROM Sample s
WHERE distributed = TRUE;

DELETE FROM TemporaryTransfer;

-- Construct outbound library transfers
INSERT INTO TemporaryTransfer (projectId, distributionDate, distributionRecipient)
SELECT DISTINCT s.project_projectId, l.distributionDate, l.distributionRecipient
FROM Library l
JOIN Sample s ON s.sampleId = l.sample_sampleId
WHERE l.distributed = TRUE;

INSERT INTO Transfer (transferId, transferDate, senderGroupId, recipient, creator, created, lastModifier, lastModified)
SELECT transferId, distributionDate, @internalGroup, distributionRecipient, @admin, @now, @admin, @now
FROM TemporaryTransfer;

INSERT INTO Transfer_Library (transferId, libraryId, received, qcPassed)
SELECT (
  SELECT transferId
  FROM TemporaryTransfer
  WHERE projectId = s.project_projectId
  AND distributionDate = l.distributionDate
  AND distributionRecipient = l.distributionRecipient
), l.libraryId, TRUE, NULL
FROM Library l
JOIN Sample s ON s.sampleId = l.sample_sampleId
WHERE l.distributed = TRUE;

DELETE FROM TemporaryTransfer;

-- Construct outbound library aliquot transfers
INSERT INTO TemporaryTransfer (projectId, distributionDate, distributionRecipient)
SELECT DISTINCT s.project_projectId, la.distributionDate, la.distributionRecipient
FROM LibraryAliquot la
JOIN Library l ON l.libraryId = la.libraryId
JOIN Sample s ON s.sampleId = l.sample_sampleId
WHERE la.distributed = TRUE;

INSERT INTO Transfer (transferId, transferDate, senderGroupId, recipient, creator, created, lastModifier, lastModified)
SELECT transferId, distributionDate, @internalGroup, distributionRecipient, @admin, @now, @admin, @now
FROM TemporaryTransfer;

INSERT INTO Transfer_LibraryAliquot (transferId, aliquotId, received, qcPassed)
SELECT (
  SELECT transferId
  FROM TemporaryTransfer
  WHERE projectId = s.project_projectId
  AND distributionDate = la.distributionDate
  AND distributionRecipient = la.distributionRecipient
), la.aliquotId, TRUE, NULL
FROM LibraryAliquot la
JOIN Library l ON l.libraryId = la.libraryId
JOIN Sample s ON s.sampleId = l.sample_sampleId
WHERE la.distributed = TRUE;

DROP TABLE TemporaryTransfer;

-- Construct outbound pool transfers
CREATE TABLE TemporaryTransfer (
  transferId bigint PRIMARY KEY AUTO_INCREMENT,
  poolId bigint
) Engine=InnoDB DEFAULT CHARSET=utf8;

-- Update auto_increment sequence
INSERT INTO TemporaryTransfer (transferId) VALUES ((SELECT MAX(transferId) FROM Transfer));
DELETE FROM TemporaryTransfer;

INSERT INTO TemporaryTransfer (poolId)
SELECT poolId
FROM Pool
WHERE distributed = TRUE;

INSERT INTO Transfer (transferId, transferDate, senderGroupId, recipient, creator, created, lastModifier, lastModified)
SELECT tt.transferId, p.distributionDate, @internalGroup, p.distributionRecipient, @admin, @now, @admin, @now
FROM TemporaryTransfer tt
JOIN Pool p ON p.poolId = tt.poolId
WHERE p.distributed = TRUE;

INSERT INTO Transfer_Pool (transferId, poolId, received, qcPassed)
SELECT tt.transferId, p.poolId, TRUE, NULL
FROM TemporaryTransfer tt
JOIN Pool p ON p.poolId = tt.poolId
WHERE p.distributed = TRUE;

DROP TABLE TemporaryTransfer;
-- EndNoTest

ALTER TABLE Sample DROP COLUMN receivedDate;
ALTER TABLE Sample DROP COLUMN distributed;
ALTER TABLE Sample DROP COLUMN distributionDate;
ALTER TABLE Sample DROP COLUMN distributionRecipient;

ALTER TABLE Library DROP COLUMN receivedDate;
ALTER TABLE Library DROP COLUMN distributed;
ALTER TABLE Library DROP COLUMN distributionDate;
ALTER TABLE Library DROP COLUMN distributionRecipient;

ALTER TABLE LibraryAliquot DROP COLUMN distributed;
ALTER TABLE LibraryAliquot DROP COLUMN distributionDate;
ALTER TABLE LibraryAliquot DROP COLUMN distributionRecipient;

ALTER TABLE Pool DROP COLUMN distributed;
ALTER TABLE Pool DROP COLUMN distributionDate;
ALTER TABLE Pool DROP COLUMN distributionRecipient;

-- run_approval
ALTER TABLE Run ADD COLUMN dataApproved BOOLEAN;
ALTER TABLE Run ADD COLUMN dataApproverId bigint;
ALTER TABLE Run ADD CONSTRAINT fk_run_approver FOREIGN KEY (dataApproverId) REFERENCES User (userId);

-- remove_old_triggers

DROP TRIGGER IF EXISTS LibraryAdditionalInfoChange;
DROP TRIGGER IF EXISTS BeforeInsertLibrary;
DROP TRIGGER IF EXISTS BeforeInsertPool;
DROP TRIGGER IF EXISTS RunChangePacBio;
DROP TRIGGER IF EXISTS SampleAdditionalInfoChange;
DROP TRIGGER IF EXISTS SampleCVSlideChange;
DROP TRIGGER IF EXISTS BeforeInsertSample;
DROP TRIGGER IF EXISTS LibraryAdditionalInfoChange;
DROP TRIGGER IF EXISTS PlateChange;
DROP TRIGGER IF EXISTS PlateInsert;
DROP TRIGGER IF EXISTS SampleLCMTubeChange;
DROP TRIGGER IF EXISTS StatusChange;

DROP FUNCTION IF EXISTS `nextval`;
DROP PROCEDURE IF EXISTS moveBoxItem;
DROP PROCEDURE IF EXISTS removeBoxItem;
DROP VIEW IF EXISTS BoxableView;

DROP PROCEDURE IF EXISTS addBoxSize;
DROP PROCEDURE IF EXISTS addBoxUse;
DROP PROCEDURE IF EXISTS addContainerModel;
DROP PROCEDURE IF EXISTS addIndex;
DROP PROCEDURE IF EXISTS addIndexFamily;
DROP PROCEDURE IF EXISTS addInstitute;
DROP PROCEDURE IF EXISTS addInstrument;
DROP PROCEDURE IF EXISTS addInstrumentModel;
DROP PROCEDURE IF EXISTS addKitDescriptor;
DROP PROCEDURE IF EXISTS addLab;
DROP PROCEDURE IF EXISTS addLibraryDesign;
DROP PROCEDURE IF EXISTS addLibraryDesignCode;
DROP PROCEDURE IF EXISTS addLibraryType;
DROP PROCEDURE IF EXISTS addQcType;
DROP PROCEDURE IF EXISTS addReferenceGenome;
DROP PROCEDURE IF EXISTS addSamplePurpose;
DROP PROCEDURE IF EXISTS addSequencingParameters;
DROP PROCEDURE IF EXISTS addTargetedSequencing;
DROP PROCEDURE IF EXISTS addTissueMaterial;
DROP PROCEDURE IF EXISTS addTissueOrigin;
DROP PROCEDURE IF EXISTS addTissueType;
DROP PROCEDURE IF EXISTS deleteContainer;
DROP PROCEDURE IF EXISTS deleteLibrary;
DROP PROCEDURE IF EXISTS deleteLibraryAliquot;
DROP PROCEDURE IF EXISTS deletePool;
DROP PROCEDURE IF EXISTS deleteRun;
DROP PROCEDURE IF EXISTS deleteSample;

-- sample_hierarchy
CREATE TABLE SampleHierarchy (
  sampleId bigint PRIMARY KEY,
  identityId bigint,
  tissueId bigint,
  CONSTRAINT fk_sampleHierarchy_sample FOREIGN KEY (sampleId) REFERENCES DetailedSample (sampleId) ON DELETE CASCADE,
  CONSTRAINT fk_sampleHierarchy_identity FOREIGN KEY (identityId) REFERENCES Identity (sampleId),
  CONSTRAINT fk_sampleHierarchy_tissue FOREIGN KEY (tissueId) REFERENCES SampleTissue (sampleId)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DELIMITER //

DROP FUNCTION IF EXISTS getParentTissueId//
CREATE FUNCTION getParentTissueId(pSampleId bigint) RETURNS bigint
  NOT DETERMINISTIC READS SQL DATA
BEGIN
  DECLARE vTissueId bigint;
  SET vTissueId = pSampleId;
  WHILE vTissueId IS NOT NULL AND NOT EXISTS (SELECT sampleId FROM SampleTissue WHERE sampleId = vTissueId) DO
    SELECT parentId INTO vTissueId FROM DetailedSample WHERE sampleId = vTissueId;
  END WHILE;
  RETURN vTissueId;
END//

DROP FUNCTION IF EXISTS getParentIdentityId//
CREATE FUNCTION getParentIdentityId(pSampleId bigint) RETURNS bigint
  NOT DETERMINISTIC READS SQL DATA
BEGIN
  DECLARE vIdentityId bigint;
  SET vIdentityId = pSampleId;
  WHILE vIdentityId IS NOT NULL AND NOT EXISTS (SELECT sampleId FROM Identity WHERE sampleId = vIdentityId) DO
    SELECT parentId INTO vIdentityId FROM DetailedSample WHERE sampleId = vIdentityId;
  END WHILE;
  RETURN vIdentityId;
END//

DELIMITER ;

INSERT INTO SampleHierarchy(sampleId, identityId, tissueId)
SELECT sampleId, getParentIdentityId(sampleId), getParentTissueId(sampleId)
FROM DetailedSample;

DROP FUNCTION getParentTissueId;
DROP FUNCTION getParentIdentityId;

-- slide_fields
ALTER TABLE SampleSlide ADD COLUMN percentTumour DECIMAL(11,8);
ALTER TABLE SampleSlide ADD COLUMN percentNecrosis DECIMAL(11,8);
ALTER TABLE SampleSlide ADD COLUMN markedAreaSize DECIMAL(11,8);
ALTER TABLE SampleSlide ADD COLUMN markedAreaPercentTumour DECIMAL(11,8);

ALTER TABLE SampleTissuePiece ADD COLUMN referenceSlideId bigint;
ALTER TABLE SampleTissuePiece ADD CONSTRAINT fk_sampleTissuePiece_referenceSlide FOREIGN KEY (referenceSlideId) REFERENCES SampleSlide(sampleId);
ALTER TABLE SampleStock ADD COLUMN referenceSlideId bigint;
ALTER TABLE SampleStock ADD CONSTRAINT fk_sampleStock_referenceSlide FOREIGN KEY (referenceSlideId) REFERENCES SampleSlide(sampleId);

-- run_purposes
DROP TRIGGER IF EXISTS PartitionQCInsert;
DROP TRIGGER IF EXISTS PartitionQCUpdate;

RENAME TABLE OrderPurpose TO RunPurpose;

ALTER TABLE Instrument ADD COLUMN defaultPurposeId bigint;
ALTER TABLE Instrument ADD CONSTRAINT instrument_defaultPurpose FOREIGN KEY (defaultPurposeId) REFERENCES RunPurpose (purposeId);
UPDATE Instrument inst
JOIN InstrumentModel im ON im.instrumentModelId = inst.instrumentModelId
SET defaultPurposeId = (SELECT purposeId FROM RunPurpose WHERE alias = 'Production')
WHERE im.instrumentType = 'SEQUENCER';

RENAME TABLE Run_Partition_QC TO Run_Partition;

ALTER TABLE Run_Partition MODIFY COLUMN partitionQcTypeId bigint;
ALTER TABLE Run_Partition ADD COLUMN purposeId bigint;
ALTER TABLE Run_Partition ADD CONSTRAINT runPartition_purpose FOREIGN KEY (purposeId) REFERENCES RunPurpose (purposeId);
ALTER TABLE Run_Partition ADD COLUMN lastModifier bigint;
ALTER TABLE Run_Partition ADD CONSTRAINT runPartition_lastModifier FOREIGN KEY (lastModifier) REFERENCES User (userId);

INSERT INTO Run_Partition (runId, partitionId)
SELECT rspc.Run_runId, spcp.partitions_partitionId
FROM Run_SequencerPartitionContainer rspc
JOIN SequencerPartitionContainer_Partition spcp ON spcp.container_containerId = rspc.containers_containerId
WHERE NOT EXISTS (
  SELECT 1 FROM Run_Partition
  WHERE runId = rspc.Run_runId AND partitionId = spcp.partitions_partitionId
);

UPDATE Run_Partition SET lastModifier = (SELECT userId FROM User WHERE loginName = 'admin');
ALTER TABLE Run_Partition MODIFY COLUMN lastModifier bigint NOT NULL;

UPDATE Run_Partition
SET purposeId = (SELECT purposeId FROM RunPurpose WHERE alias = 'Production');
ALTER TABLE Run_Partition MODIFY COLUMN purposeId bigint NOT NULL;

CREATE TABLE Run_Partition_LibraryAliquot (
  runId bigint NOT NULL,
  partitionId bigint NOT NULL,
  aliquotId bigint NOT NULL,
  purposeId bigint,
  lastModifier bigint NOT NULL,
  PRIMARY KEY (runId, partitionId, aliquotId),
  CONSTRAINT runAliquot_run FOREIGN KEY (runId) REFERENCES Run (runId),
  CONSTRAINT runAliquot_partition FOREIGN KEY (partitionId) REFERENCES _Partition (partitionId),
  CONSTRAINT runAliquot_aliquot FOREIGN KEY (aliquotId) REFERENCES LibraryAliquot (aliquotId),
  CONSTRAINT runAliquot_purpose FOREIGN KEY (purposeId) REFERENCES RunPurpose (purposeId),
  CONSTRAINT runAliquot_lastModifier FOREIGN KEY (lastModifier) REFERENCES User (userId)
) Engine=InnoDB DEFAULT CHARSET=utf8;

