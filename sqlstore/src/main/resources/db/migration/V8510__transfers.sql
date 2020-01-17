CREATE TABLE Transfer (
  transferId bigint(20) PRIMARY KEY AUTO_INCREMENT,
  transferDate DATE NOT NULL,
  senderLabId bigint(20),
  senderGroupId bigint(20),
  recipient varchar(255),
  recipientGroupId bigint(20),
  creator bigint(20) NOT NULL,
  created TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  lastModifier bigint(20) NOT NULL,
  lastModified TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_transfer_senderLab FOREIGN KEY (senderLabId) REFERENCES Lab (labId),
  CONSTRAINT fk_transfer_senderGroup FOREIGN KEY (senderGroupId) REFERENCES _Group (groupId),
  CONSTRAINT fk_transfer_recipientGroup FOREIGN KEY (recipientGroupId) REFERENCES _Group (groupId),
  CONSTRAINT fk_transfer_creator FOREIGN KEY (creator) REFERENCES User (userId),
  CONSTRAINT fk_transfer_modifier FOREIGN KEY (lastModifier) REFERENCES User (userId)
) Engine=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE Transfer_Sample (
  transferId bigint(20),
  sampleId bigint(20),
  received BOOLEAN,
  qcPassed BOOLEAN,
  qcNote varchar(255),
  PRIMARY KEY (transferId, sampleId),
  CONSTRAINT fk_sample_transfer FOREIGN KEY (transferId) REFERENCES Transfer (transferId),
  CONSTRAINT fk_transfer_sample FOREIGN KEY (sampleId) REFERENCES Sample (sampleId)
) Engine=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE Transfer_Library (
  transferId bigint(20),
  libraryId bigint(20),
  received BOOLEAN,
  qcPassed BOOLEAN,
  qcNote varchar(255),
  PRIMARY KEY (transferId, libraryId),
  CONSTRAINT fk_library_transfer FOREIGN KEY (transferId) REFERENCES Transfer (transferId),
  CONSTRAINT fk_transfer_library FOREIGN KEY (libraryId) REFERENCES Library (libraryId)
) Engine=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE Transfer_LibraryAliquot (
  transferId bigint(20),
  aliquotId bigint(20),
  received BOOLEAN,
  qcPassed BOOLEAN,
  qcNote varchar(255),
  PRIMARY KEY (transferId, aliquotId),
  CONSTRAINT fk_libraryAliquot_transfer FOREIGN KEY (transferId) REFERENCES Transfer (transferId),
  CONSTRAINT fk_transfer_libraryAliquot FOREIGN KEY (aliquotId) REFERENCES LibraryAliquot (aliquotId)
) Engine=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE Transfer_Pool (
  transferId bigint(20),
  poolId bigint(20),
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
  transferId bigint(20) PRIMARY KEY AUTO_INCREMENT,
  projectId bigint(20),
  creator bigint(20),
  receivedDate DATE,
  labId bigint(20)
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

SET sql_notes = 0;
DELIMITER //

DROP FUNCTION IF EXISTS findParentWithLab//
CREATE FUNCTION findParentWithLab(pSampleId bigint(20)) RETURNS bigint(20)
BEGIN
  DECLARE vTissueId bigint(20);
  SET vTissueId = pSampleId;
  WHILE vTissueId IS NOT NULL AND NOT EXISTS (SELECT sampleId FROM SampleTissue WHERE sampleId = vTissueId AND labId IS NOT NULL) DO
    SELECT parentId INTO vTissueId FROM DetailedSample WHERE sampleId = vTissueId;
  END WHILE;
  RETURN vTissueId;
END//

DELIMITER ;
SET sql_notes = 1;

CREATE TABLE TissueParentView (
  sampleId bigint(20),
  tissueId bigint(20)
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
  transferId bigint(20) PRIMARY KEY AUTO_INCREMENT,
  projectId bigint(20),
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
  transferId bigint(20) PRIMARY KEY AUTO_INCREMENT,
  poolId bigint(20)
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
