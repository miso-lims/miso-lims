DROP TABLE Project_Study;
UPDATE Project SET progress = UPPER(progress);
ALTER TABLE Project ADD CONSTRAINT project_alias_UK UNIQUE (alias);
UPDATE KitDescriptor SET kitType = UPPER(kitType), platformType = UPPER(platformType);

UPDATE Platform SET name = UPPER(name);

ALTER TABLE Study ADD COLUMN studyTypeId bigint(20);
UPDATE Study SET studyTypeId = (SELECT typeId FROM StudyType WHERE name = studyType);
ALTER TABLE Study ADD CONSTRAINT study_studyTypeId FOREIGN KEY (studyTypeId) REFERENCES StudyType(typeId);
ALTER TABLE Study DROP COLUMN studyType;
ALTER TABLE Study CHANGE COLUMN studyTypeId studyTypeId bigint(20) NOT NULL;
ALTER TABLE Study ADD CONSTRAINT fk_study_securityProfile FOREIGN KEY (securityProfile_profileId) REFERENCES SecurityProfile (profileId);
ALTER TABLE Study ADD CONSTRAINT fk_study_project FOREIGN KEY (project_projectId) REFERENCES Project (projectId);
ALTER TABLE Study ADD CONSTRAINT fk_study_lastModifier_user FOREIGN KEY (lastModifier) REFERENCES User (userId);

ALTER TABLE ProjectOverview ADD COLUMN project_projectId bigint(20);
UPDATE ProjectOverview SET project_projectId = (SELECT project_projectId FROM Project_ProjectOverview WHERE overviews_overviewId = overviewId);
ALTER TABLE ProjectOverview ADD CONSTRAINT projectOverview_project_project_projectId FOREIGN KEY (project_projectId) REFERENCES Project(projectId);
DROP TABLE Project_ProjectOverview;

CREATE TABLE ProjectOverview_Sample (
  projectOverview_overviewId bigint(20) NOT NULL,
  sample_sampleId bigint(20) NOT NULL,
  CONSTRAINT projectOverview_sample_projectOverview_overviewId FOREIGN KEY (projectOverview_overviewId) REFERENCES ProjectOverview(overviewId),
  CONSTRAINT projectOverview_sample_sample_sampleId FOREIGN KEY (sample_sampleId) REFERENCES Sample(sampleId)
) ENGINE=InnoDB;

INSERT INTO ProjectOverview_Sample(projectOverview_overviewId, sample_sampleId)
  SELECT parentId, entityId FROM EntityGroup JOIN EntityGroup_Elements ON EntityGroup.entityGroupId = EntityGroup_Elements.entityGroup_entityGroupId
   WHERE entityType = 'uk.ac.bbsrc.tgac.miso.core.data.Sample' AND parentType = 'uk.ac.bbsrc.tgac.miso.core.data.impl.ProjectOverview';

DROP TABLE EntityGroup_Elements;
DROP TABLE EntityGroup;

UPDATE Pool SET platformType = UPPER(platformType);
ALTER TABLE Pool ADD CONSTRAINT fk_pool_securityProfile FOREIGN KEY (securityProfile_profileId) REFERENCES SecurityProfile (profileId);
ALTER TABLE Pool ADD CONSTRAINT fk_pool_lastModifier_user FOREIGN KEY (lastModifier) REFERENCES User (userId);

CREATE TABLE `Pool_Dilution` (
  `pool_poolId` bigint(20) NOT NULL,
  `dilution_dilutionId` bigint(20) NOT NULL,
  PRIMARY KEY (`pool_poolId`,`dilution_dilutionId`),
  CONSTRAINT `Pool_Dilution_pool_poolId` FOREIGN KEY (`pool_poolId`) REFERENCES `Pool` (`poolId`),
  CONSTRAINT `Pool_Dilution_dilution_dilutionId` FOREIGN KEY (`dilution_dilutionId`) REFERENCES `LibraryDilution` (`dilutionId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

INSERT INTO Pool_Dilution(pool_poolId, dilution_dilutionId) SELECT
  pool_poolId, elementId FROM Pool_Elements
  WHERE elementType = 'uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryDilution';
INSERT INTO Pool_Dilution(pool_poolId, dilution_dilutionId) SELECT DISTINCT
  pool_poolId, emPCR.dilution_dilutionId
  FROM Pool_Elements JOIN emPCRDilution ON Pool_Elements.elementId = emPCRDilution.dilutionId
  JOIN emPCR ON emPCR.pcrId = emPCRDilution.emPCR_pcrId
  WHERE elementType = 'uk.ac.bbsrc.tgac.miso.core.data.impl.emPCRDilution'
  AND NOT EXISTS (SELECT * FROM Pool_Dilution WHERE Pool_Dilution.pool_poolId = Pool_Elements.pool_poolId AND Pool_Dilution.dilution_dilutionId = emPCR.dilution_dilutionId);

INSERT INTO Pool_Experiment(pool_poolId, experiments_experimentId) SELECT poolId, experiment_experimentId FROM Pool WHERE NOT EXISTS(SELECT * FROM Pool_Experiment WHERE poolId = pool_poolId AND experiment_experimentId = experiments_experimentId) AND experiment_experimentId IS NOT NULL;
ALTER TABLE Pool DROP COLUMN experiment_experimentId;
ALTER TABLE Experiment ADD COLUMN pool_poolId bigint(20);
UPDATE Experiment SET pool_poolId = (SELECT pool_poolId FROM Pool_Experiment WHERE experiments_experimentId = experimentId);
ALTER TABLE Experiment ADD CONSTRAINT fk_experiment_pool_poolId FOREIGN KEY (pool_poolId) REFERENCES Pool (poolId);
DROP TABLE Pool_Experiment;

INSERT INTO PoolChangeLog(poolId, userId, message) SELECT
  pool_poolId, (SELECT userId FROM `User` WHERE loginName = 'admin'),
  CONCAT(
    'Replaced emPCR dilution ', emPCRDilution.name, ' (concentration = ', emPCRDilution.concentration, ') created on ', emPCRDilution.creationDate, ' by ', emPCRDilution.dilutionUserName,
    'created from emPCR ', emPCR.name, ' (concentration = ', emPCR.concentration, ') created on ', emPCR.creationDate, ' by ', emPCR.pcrUserName,
    ' with library dilution ',  (SELECT name FROM LibraryDilution WHERE LibraryDilution.dilutionId = emPCR.dilution_dilutionId))
  FROM Pool_Elements JOIN emPCRDilution ON Pool_Elements.elementId = emPCRDilution.dilutionId
  JOIN emPCR ON emPCR.pcrId = emPCRDilution.emPCR_pcrId
  WHERE elementType = 'uk.ac.bbsrc.tgac.miso.core.data.impl.emPCRDilution'
  AND NOT EXISTS (SELECT * FROM Pool_Dilution WHERE Pool_Dilution.pool_poolId = Pool_Elements.pool_poolId AND Pool_Dilution.dilution_dilutionId = emPCR.dilution_dilutionId);

DROP TABLE Pool_Elements;

ALTER TABLE SequencerReference ADD COLUMN ip VARCHAR(50) NOT NULL DEFAULT 'localhost';
-- H2 doesn't have INET_NTOA function
-- StartNoTest
UPDATE SequencerReference SET ip = INET_NTOA(ipAddress);
--EndNoTest
ALTER TABLE SequencerReference DROP COLUMN available;
ALTER TABLE SequencerReference DROP COLUMN ipAddress;
ALTER TABLE SequencerReference ADD CONSTRAINT upgraded_SR_UK UNIQUE (upgradedSequencerReferenceId);
ALTER TABLE SequencerReference ADD CONSTRAINT fk_sequencerReference_platform FOREIGN KEY (platformId) REFERENCES Platform (platformId);

DROP TABLE Request;
DROP TABLE Request_Note;

ALTER TABLE ProjectOverview_Note ADD CONSTRAINT ProjectOverviewNote_ProjectOverview_FK FOREIGN KEY (overview_overviewId) REFERENCES ProjectOverview (overviewId);
ALTER TABLE ProjectOverview_Note ADD CONSTRAINT ProjectOverviewNote_Note_FK FOREIGN KEY (notes_noteId) REFERENCES Note (noteId);

ALTER TABLE Run_Note ADD CONSTRAINT RunNote_Run_FK FOREIGN KEY (run_runId) REFERENCES Run (runId);
ALTER TABLE Run_Note ADD CONSTRAINT RunNote_Note_FK FOREIGN KEY (notes_noteId) REFERENCES Note (noteId);

ALTER TABLE Pool_Note ADD CONSTRAINT PoolNote_Pool_FK FOREIGN KEY (pool_poolId) REFERENCES Pool (poolId);
ALTER TABLE Pool_Note ADD CONSTRAINT PoolNote_Note_FK FOREIGN KEY (notes_noteId) REFERENCES Note (noteId);

ALTER TABLE Sample_Note ADD CONSTRAINT SampleNote_Sample_FK FOREIGN KEY (sample_sampleId) REFERENCES Sample (sampleId);
ALTER TABLE Sample_Note ADD CONSTRAINT SampleNote_Note_FK FOREIGN KEY (notes_noteId) REFERENCES Note (noteId);

ALTER TABLE Library_Note ADD CONSTRAINT LibraryNote_Library_FK FOREIGN KEY (library_libraryId) REFERENCES Library (libraryId);
ALTER TABLE Library_Note ADD CONSTRAINT LibraryNote_Note_FK FOREIGN KEY (notes_noteId) REFERENCES Note (noteId);

ALTER TABLE Kit_Note ADD CONSTRAINT KitNote_Kit_FK FOREIGN KEY (kit_kitId) REFERENCES Kit (kitId);
ALTER TABLE Kit_Note ADD CONSTRAINT KitNote_Note_FK FOREIGN KEY (notes_noteId) REFERENCES Note (noteId);

UPDATE Run SET platformType = UPPER(platformType);
ALTER TABLE Run ADD CONSTRAINT fk_run_securityProfile FOREIGN KEY (securityProfile_profileId) REFERENCES SecurityProfile (profileId);
ALTER TABLE Run ADD CONSTRAINT fk_run_status FOREIGN KEY (status_statusId) REFERENCES Status (statusId);
ALTER TABLE Run ADD CONSTRAINT fk_run_sequencerReference FOREIGN KEY (sequencerReference_sequencerReferenceId) REFERENCES SequencerReference (referenceId);
ALTER TABLE Run ADD CONSTRAINT fk_run_lastModifier_user FOREIGN KEY (lastModifier) REFERENCES User (userId);
ALTER TABLE Run ADD CONSTRAINT fk_run_sequencingParameters FOREIGN KEY (sequencingParameters_parametersId) REFERENCES SequencingParameters (parametersId);

ALTER TABLE RunQC_Partition ADD COLUMN partition_partitionId BIGINT(20) NOT NULL;
-- StartNoTest
ALTER TABLE RunQC_Partition CHANGE COLUMN partition_partitionId partition_partitionId BIGINT(20);

UPDATE RunQC_Partition rqp SET partition_partitionId = (
  SELECT p.partitionId FROM `_Partition` p 
  JOIN SequencerPartitionContainer_Partition spcp ON spcp.partitions_partitionId = p.partitionId
  WHERE spcp.container_containerId = rqp.containers_containerId
  AND p.partitionNumber = rqp.partitionNumber
);
ALTER TABLE RunQC_Partition CHANGE COLUMN partition_partitionId partition_partitionId BIGINT(20) NOT NULL;
--EndNoTest
ALTER TABLE RunQC_Partition DROP PRIMARY KEY;
ALTER TABLE RunQC_Partition ADD PRIMARY KEY(`runQc_runQcId`, `partition_partitionId`);
ALTER TABLE RunQC_Partition ADD CONSTRAINT RunQCPartition_Partition_FK FOREIGN KEY (partition_partitionId) REFERENCES `_Partition` (partitionId);
ALTER TABLE RunQC_Partition ADD CONSTRAINT RunQCPartition_RunQC_FK FOREIGN KEY (runQc_runQcId) REFERENCES `RunQC` (qcId);
ALTER TABLE RunQC_Partition DROP COLUMN partitionNumber;
ALTER TABLE RunQC_Partition DROP COLUMN containers_containerId;

ALTER TABLE _Partition ADD CONSTRAINT fk_partition_securityProfile FOREIGN KEY (securityProfile_profileId) REFERENCES SecurityProfile (profileId);

CREATE TABLE Project_Watcher (
  projectId bigint(20) NOT NULL,
  userId bigint(20) NOT NULL,
  PRIMARY KEY (projectId, userId),
  CONSTRAINT fk_projectWatcher_project FOREIGN KEY (projectId) REFERENCES Project (projectId) ON DELETE CASCADE,
  CONSTRAINT fk_projectWatcher_user FOREIGN KEY (userId) REFERENCES User (userId) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE ProjectOverview_Watcher (
  overviewId bigint(20) NOT NULL,
  userId bigint(20) NOT NULL,
  PRIMARY KEY (overviewId, userId),
  CONSTRAINT fk_projectOverviewWatcher_project FOREIGN KEY (overviewId) REFERENCES ProjectOverview (overviewId) ON DELETE CASCADE,
  CONSTRAINT fk_projectOverviewWatcher_user FOREIGN KEY (userId) REFERENCES User (userId) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE Pool_Watcher (
  poolId bigint(20) NOT NULL,
  userId bigint(20) NOT NULL,
  PRIMARY KEY (poolId, userId),
  CONSTRAINT fk_poolWatcher_pool FOREIGN KEY (poolId) REFERENCES Pool (poolId) ON DELETE CASCADE,
  CONSTRAINT fk_poolWatcher_user FOREIGN KEY (userId) REFERENCES User (userId) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE Run_Watcher (
  runId bigint(20) NOT NULL,
  userId bigint(20) NOT NULL,
  PRIMARY KEY (runId, userId),
  CONSTRAINT fk_runWatcher_run FOREIGN KEY (runId) REFERENCES Run (runId) ON DELETE CASCADE,
  CONSTRAINT fk_runWatcher_user FOREIGN KEY (userId) REFERENCES User (userId) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- StartNoTest
INSERT INTO Project_Watcher(projectId, userId)
SELECT p.projectId, u.userId
FROM Watcher w
JOIN Project p ON p.name = w.entityName
JOIN User u ON u.userId = w.userId;

INSERT INTO ProjectOverview_Watcher(overviewId, userId)
SELECT o.overviewId, u.userId
FROM Watcher w
JOIN ProjectOverview o ON CONCAT('POV', o.overviewId) = w.entityName
JOIN User u ON u.userId = w.userId;

INSERT INTO Pool_Watcher(poolId, userId)
SELECT p.poolId, u.userId
FROM Watcher w
JOIN Pool p ON p.name = w.entityName
JOIN User u ON u.userId = w.userId;

INSERT INTO Run_Watcher(runId, userId)
SELECT r.runId, u.userId
FROM Watcher w
JOIN Run r ON r.name = w.entityName
JOIN User u ON u.userId = w.userId;
-- EndNoTest

DROP TABLE Watcher;

CREATE TABLE `Printer` (
  `printerId` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(100) NOT NULL,
  `driver` varchar(20) NOT NULL,
  `backend` varchar(20) NOT NULL,
  `configuration` varchar(1024),
  `enabled` boolean NOT NULL DEFAULT '1',
  PRIMARY KEY (`printerId`),
  CONSTRAINT printer_name UNIQUE(name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

INSERT INTO Printer (name, driver, backend, configuration, enabled)
  SELECT DISTINCT
    MIN(serviceName),
    CASE contextName
      WHEN 'brady1DBarcodeLabelSchema' THEN 'BRADY_1D'
      WHEN 'bradyStandardTubeBarcodeLabelSchema' THEN 'BRADY_STANDARD'
      WHEN 'bradyCustomStandardTubeBarcodeLabelSchema' THEN 'BRADY_STANDARD'
      WHEN 'bradyMinus80TubeBarcodeLabelSchema' THEN 'BRADY_M80'
      WHEN 'bradyCustomMinus80TubeBarcodeLabelSchema' THEN 'BRADY_M80'
    END,
    CASE printSchema
      WHEN 'mach4-type-spool-printer' THEN 'CUPS'
      WHEN 'mach4-type-ftp-printer' THEN 'FTP'
    END,
    contextFields,
    MAX(enabled)
  FROM PrintService
  WHERE
    contextName IN ('brady1DBarcodeLabelSchema', 'bradyStandardTubeBarcodeLabelSchema', 'bradyCustomStandardTubeBarcodeLabelSchema', 'bradyMinus80TubeBarcodeLabelSchema', 'bradyCustomMinus80TubeBarcodeLabelSchema')
    AND
    printSchema IN ('mach4-type-spool-printer', 'mach4-type-ftp-printer')
  GROUP BY printSchema, contextName, contextFields;

DROP TABLE PrintService;
DROP TABLE PrintJob;


ALTER TABLE BoxChangeLog ADD boxChangeLogId bigint(20) AUTO_INCREMENT
-- StartNoTest
 PRIMARY KEY FIRST
-- EndNoTest
;
ALTER TABLE BoxChangeLog MODIFY columnsChanged VARCHAR(500);
ALTER TABLE BoxChangeLog MODIFY message LONGTEXT;
ALTER TABLE BoxChangeLog ADD FOREIGN KEY (userId) REFERENCES User(userId);
-- Remove orphaned log entries.
DELETE
FROM BoxChangeLog
WHERE boxChangeLogId IN (
        SELECT *
        FROM (
            SELECT BoxChangeLog.boxChangeLogId
            FROM BoxChangeLog
            WHERE NOT EXISTS (
                    SELECT *
                    FROM Box
                    WHERE Box.boxId = BoxChangeLog.boxId
                    )
            ) AS t
        );
ALTER TABLE BoxChangeLog ADD FOREIGN KEY (boxId) REFERENCES Box(boxId);

ALTER TABLE ExperimentChangeLog ADD experimentChangeLogId bigint(20) AUTO_INCREMENT
-- StartNoTest
 PRIMARY KEY FIRST
-- EndNoTest
;
ALTER TABLE ExperimentChangeLog MODIFY columnsChanged VARCHAR(500);
ALTER TABLE ExperimentChangeLog MODIFY message LONGTEXT;
ALTER TABLE ExperimentChangeLog ADD FOREIGN KEY (userId) REFERENCES User(userId);
-- Remove orphaned log entries.
DELETE
FROM ExperimentChangeLog
WHERE experimentChangeLogId IN (
        SELECT *
        FROM (
            SELECT ExperimentChangeLog.experimentChangeLogId
            FROM ExperimentChangeLog
            WHERE NOT EXISTS (
                    SELECT *
                    FROM Experiment
                    WHERE Experiment.experimentId = ExperimentChangeLog.experimentId
                    )
            ) AS t
        );
ALTER TABLE ExperimentChangeLog ADD FOREIGN KEY (experimentId) REFERENCES Experiment(experimentId);

ALTER TABLE KitDescriptorChangeLog ADD kitDescriptorChangeLogId bigint(20) AUTO_INCREMENT
-- StartNoTest
 PRIMARY KEY FIRST
-- EndNoTest
;
ALTER TABLE KitDescriptorChangeLog MODIFY columnsChanged VARCHAR(500);
ALTER TABLE KitDescriptorChangeLog MODIFY message LONGTEXT;
ALTER TABLE KitDescriptorChangeLog ADD FOREIGN KEY (userId) REFERENCES User(userId);
-- Remove orphaned log entries.
DELETE
FROM KitDescriptorChangeLog
WHERE kitDescriptorChangeLogId IN (
        SELECT *
        FROM (
            SELECT KitDescriptorChangeLog.kitDescriptorChangeLogId
            FROM KitDescriptorChangeLog
            WHERE NOT EXISTS (
                    SELECT *
                    FROM KitDescriptor
                    WHERE KitDescriptor.kitDescriptorId = KitDescriptorChangeLog.kitDescriptorId
                    )
            ) AS t
        );
ALTER TABLE KitDescriptorChangeLog ADD FOREIGN KEY (kitDescriptorId) REFERENCES KitDescriptor(kitDescriptorId);

ALTER TABLE LibraryChangeLog ADD libraryChangeLogId bigint(20) AUTO_INCREMENT
-- StartNoTest
 PRIMARY KEY FIRST
-- EndNoTest
;
ALTER TABLE LibraryChangeLog MODIFY columnsChanged VARCHAR(500);
ALTER TABLE LibraryChangeLog MODIFY message LONGTEXT;
ALTER TABLE LibraryChangeLog ADD FOREIGN KEY (userId) REFERENCES User(userId);
-- Remove orphaned log entries.
DELETE
FROM LibraryChangeLog
WHERE libraryChangeLogId IN (
        SELECT *
        FROM (
            SELECT LibraryChangeLog.libraryChangeLogId
            FROM LibraryChangeLog
            WHERE NOT EXISTS (
                    SELECT *
                    FROM Library
                    WHERE Library.libraryId = LibraryChangeLog.libraryId
                    )
            ) AS t
        );
ALTER TABLE LibraryChangeLog ADD FOREIGN KEY (libraryId) REFERENCES Library(libraryId);

ALTER TABLE PoolChangeLog ADD poolChangeLogId bigint(20) AUTO_INCREMENT
-- StartNoTest
 PRIMARY KEY FIRST
-- EndNoTest
;
ALTER TABLE PoolChangeLog MODIFY columnsChanged VARCHAR(500);
ALTER TABLE PoolChangeLog MODIFY message LONGTEXT;
ALTER TABLE PoolChangeLog ADD FOREIGN KEY (userId) REFERENCES User(userId);
-- Remove orphaned log entries.
DELETE
FROM PoolChangeLog
WHERE poolChangeLogId IN (
        SELECT *
        FROM (
            SELECT PoolChangeLog.poolChangeLogId
            FROM PoolChangeLog
            WHERE NOT EXISTS (
                    SELECT *
                    FROM Pool
                    WHERE Pool.poolId = PoolChangeLog.poolId
                    )
            ) AS t
        );
ALTER TABLE PoolChangeLog ADD FOREIGN KEY (poolId) REFERENCES Pool(poolId);

ALTER TABLE RunChangeLog ADD runChangeLogId bigint(20) AUTO_INCREMENT
-- StartNoTest
 PRIMARY KEY FIRST
-- EndNoTest
;
ALTER TABLE RunChangeLog MODIFY columnsChanged VARCHAR(500);
ALTER TABLE RunChangeLog MODIFY message LONGTEXT;
ALTER TABLE RunChangeLog ADD FOREIGN KEY (userId) REFERENCES User(userId);
-- Remove orphaned log entries.
DELETE
FROM RunChangeLog
WHERE runChangeLogId IN (
        SELECT *
        FROM (
            SELECT RunChangeLog.runChangeLogId
            FROM RunChangeLog
            WHERE NOT EXISTS (
                    SELECT *
                    FROM Run
                    WHERE Run.runId = RunChangeLog.runId
                    )
            ) AS t
        );
ALTER TABLE RunChangeLog ADD FOREIGN KEY (runId) REFERENCES Run(runId);

ALTER TABLE SampleChangeLog ADD sampleChangeLogId bigint(20) AUTO_INCREMENT
-- StartNoTest
 PRIMARY KEY FIRST
-- EndNoTest
;
ALTER TABLE SampleChangeLog MODIFY columnsChanged VARCHAR(500);
ALTER TABLE SampleChangeLog MODIFY message LONGTEXT;
ALTER TABLE SampleChangeLog ADD FOREIGN KEY (userId) REFERENCES User(userId);
-- Remove orphaned log entries.
DELETE
FROM SampleChangeLog
WHERE sampleChangeLogId IN (
        SELECT *
        FROM (
            SELECT SampleChangeLog.sampleChangeLogId
            FROM SampleChangeLog
            WHERE NOT EXISTS (
                    SELECT *
                    FROM Sample
                    WHERE Sample.sampleId = SampleChangeLog.sampleId
                    )
            ) AS t
        );
ALTER TABLE SampleChangeLog ADD FOREIGN KEY (sampleId) REFERENCES Sample(sampleId);

ALTER TABLE SequencerPartitionContainerChangeLog ADD sequencerPartitionContainerChangeLogId bigint(20) AUTO_INCREMENT
-- StartNoTest
 PRIMARY KEY FIRST
-- EndNoTest
;
ALTER TABLE SequencerPartitionContainerChangeLog MODIFY columnsChanged VARCHAR(500);
ALTER TABLE SequencerPartitionContainerChangeLog MODIFY message LONGTEXT;
ALTER TABLE SequencerPartitionContainerChangeLog ADD FOREIGN KEY (userId) REFERENCES User(userId);
-- Remove orphaned log entries.
DELETE
FROM SequencerPartitionContainerChangeLog
WHERE sequencerPartitionContainerChangeLogId IN (
        SELECT *
        FROM (
            SELECT SequencerPartitionContainerChangeLog.sequencerPartitionContainerChangeLogId
            FROM SequencerPartitionContainerChangeLog
            WHERE NOT EXISTS (
                    SELECT *
                    FROM SequencerPartitionContainer
                    WHERE SequencerPartitionContainer.containerId = SequencerPartitionContainerChangeLog.containerId
                    )
            ) AS t
        );
ALTER TABLE SequencerPartitionContainerChangeLog ADD FOREIGN KEY (containerId) REFERENCES SequencerPartitionContainer(containerId);

ALTER TABLE StudyChangeLog ADD studyChangeLogId bigint(20) AUTO_INCREMENT
-- StartNoTest
 PRIMARY KEY FIRST
-- EndNoTest
;
ALTER TABLE StudyChangeLog MODIFY columnsChanged VARCHAR(500);
ALTER TABLE StudyChangeLog MODIFY message LONGTEXT;
ALTER TABLE StudyChangeLog ADD FOREIGN KEY (userId) REFERENCES User(userId);
-- Remove orphaned log entries.
DELETE
FROM StudyChangeLog
WHERE studyChangeLogId IN (
        SELECT *
        FROM (
            SELECT StudyChangeLog.studyChangeLogId
            FROM StudyChangeLog
            WHERE NOT EXISTS (
                    SELECT *
                    FROM Study
                    WHERE Study.studyId = StudyChangeLog.studyId
                    )
            ) AS t
        );
ALTER TABLE StudyChangeLog ADD FOREIGN KEY (studyId) REFERENCES Study(studyId);

ALTER TABLE BoxPosition ADD COLUMN targetId bigint(20) NOT NULL;
ALTER TABLE BoxPosition ADD COLUMN targetType varchar(1) NOT NULL;
ALTER TABLE BoxPosition ADD COLUMN position varchar(3) NOT NULL;
-- StartNoTest
ALTER TABLE BoxPosition CHANGE COLUMN targetId targetId bigint(20);
ALTER TABLE BoxPosition CHANGE COLUMN targetType targetType varchar(1);
ALTER TABLE BoxPosition CHANGE COLUMN position position varchar(3);
UPDATE BoxPosition SET
  position = CONCAT(CHAR(65 + `column`), LPAD(row, 2, '0')),
  targetType = (
    SELECT 'S' FROM Sample WHERE Sample.boxPositionId = BoxPosition.boxPositionId UNION
    SELECT 'L' FROM Library WHERE Library.boxPositionId = BoxPosition.boxPositionId UNION
    SELECT 'P' FROM Pool WHERE Pool.boxPositionId = BoxPosition.boxPositionId),
  targetId = (
    SELECT sampleId FROM Sample WHERE Sample.boxPositionId = BoxPosition.boxPositionId UNION
    SELECT libraryId FROM Library WHERE Library.boxPositionId = BoxPosition.boxPositionId UNION
    SELECT poolId FROM Pool WHERE Pool.boxPositionId = BoxPosition.boxPositionId);
-- EndNoTest

ALTER TABLE BoxPosition CHANGE COLUMN targetId targetId bigint(20) NOT NULL;
ALTER TABLE BoxPosition CHANGE COLUMN targetType targetType varchar(1) NOT NULL;
ALTER TABLE BoxPosition CHANGE COLUMN position position varchar(3) NOT NULL;
ALTER TABLE BoxPosition DROP PRIMARY KEY;
ALTER TABLE BoxPosition ADD CONSTRAINT box_postion_pk PRIMARY KEY(boxId, targetId, targetType);
ALTER TABLE BoxPosition ADD CONSTRAINT box_unique_item UNIQUE (targetId, targetType);
ALTER TABLE BoxPosition ADD CONSTRAINT box_single_occupancy UNIQUE (boxId, position);
-- StartNoTest
ALTER TABLE BoxPosition DROP INDEX boxId;
ALTER TABLE BoxPosition DROP COLUMN `row`;
ALTER TABLE BoxPosition DROP COLUMN `column`;
ALTER TABLE BoxPosition DROP COLUMN `boxPositionId`;
-- EndNoTest
ALTER TABLE Sample DROP COLUMN `boxPositionId`;
ALTER TABLE Library DROP COLUMN `boxPositionId`;
ALTER TABLE Pool DROP COLUMN `boxPositionId`;
DROP TABLE sequence_data;

DROP TABLE Submission_Partition;
DROP TABLE Submission_Chamber;
DROP TABLE Submission_Lane;
DROP TABLE Flowcell_Lane;
DROP TABLE Flowcell_Chamber;
DROP TABLE Chamber;
DROP TABLE Lane;
DROP TABLE Flowcell;

UPDATE LibraryType SET platformType = UPPER(platformType);

UPDATE Library SET platformName = UPPER(platformName);
ALTER TABLE Library CHANGE COLUMN platformName platformType varchar(255) DEFAULT NULL;
ALTER TABLE Library ADD CONSTRAINT fk_library_libraryType FOREIGN KEY (libraryType) REFERENCES LibraryType (libraryTypeId);
ALTER TABLE Library ADD CONSTRAINT fk_library_librarySelectionType FOREIGN KEY (librarySelectionType) REFERENCES LibrarySelectionType (librarySelectionTypeId);
ALTER TABLE Library ADD CONSTRAINT fk_library_libraryStrategyType FOREIGN KEY (libraryStrategyType) REFERENCES LibraryStrategyType (libraryStrategyTypeId);
ALTER TABLE Library ADD CONSTRAINT fk_library_sample FOREIGN KEY (sample_sampleId) REFERENCES Sample (sampleId);
ALTER TABLE Library DROP CONSTRAINT `library_user_userid_fkey`;
ALTER TABLE Library ADD CONSTRAINT fk_library_lastModifier_user FOREIGN KEY (lastModifier) REFERENCES User (userId);
ALTER TABLE Library ADD CONSTRAINT fk_library_securityProfile FOREIGN KEY (securityProfile_profileId) REFERENCES SecurityProfile (profileId);

ALTER TABLE LibraryDilution ADD COLUMN lastModifier bigint(20);
UPDATE LibraryDilution SET lastModifier = (SELECT lastModifier FROM Library l WHERE l.libraryId = library_libraryId);
ALTER TABLE LibraryDilution CHANGE COLUMN lastModifier lastModifier bigint(20) NOT NULL;
ALTER TABLE LibraryDilution ADD CONSTRAINT fk_libraryDilution_lastModifier_user FOREIGN KEY (lastModifier) REFERENCES User (userId);
ALTER TABLE LibraryDilution ADD CONSTRAINT fk_libraryDilution_library FOREIGN KEY (library_libraryId) REFERENCES Library (libraryId);
ALTER TABLE LibraryDilution ADD CONSTRAINT fk_libraryDilution_targetedSequencing FOREIGN KEY (targetedSequencingId) REFERENCES TargetedSequencing (targetedSequencingId);

ALTER TABLE LibraryAdditionalInfo RENAME TO DetailedLibrary;
ALTER TABLE DetailedLibrary DROP FOREIGN KEY `libraryAdditionalInfo_createUser_fkey`;
ALTER TABLE DetailedLibrary DROP COLUMN createdBy;
ALTER TABLE DetailedLibrary DROP COLUMN creationDate;
ALTER TABLE DetailedLibrary DROP FOREIGN KEY `libraryAdditionalInfo_updateUser_fkey`;
ALTER TABLE DetailedLibrary DROP COLUMN updatedBy;
ALTER TABLE DetailedLibrary DROP COLUMN lastUpdated;

UPDATE Project SET progress = UPPER(progress);
ALTER TABLE Project ADD CONSTRAINT fk_project_securityProfile FOREIGN KEY (securityProfile_profileId) REFERENCES SecurityProfile (profileId);

ALTER TABLE Sample ADD CONSTRAINT fk_sample_securityProfile FOREIGN KEY (securityProfile_profileId) REFERENCES SecurityProfile (profileId);
ALTER TABLE Sample ADD CONSTRAINT fk_sample_project FOREIGN KEY (project_projectId) REFERENCES Project (projectId);
ALTER TABLE Sample ADD CONSTRAINT fk_sample_lastModifier_user FOREIGN KEY (lastModifier) REFERENCES User (userId);

ALTER TABLE Box ADD CONSTRAINT fk_box_boxSize FOREIGN KEY (boxSizeId) REFERENCES BoxSize (boxSizeId);
ALTER TABLE Box ADD CONSTRAINT fk_box_boxUse FOREIGN KEY (boxUseId) REFERENCES BoxUse (boxUseId);
ALTER TABLE Box ADD CONSTRAINT fk_box_lastModifier_user FOREIGN KEY (lastModifier) REFERENCES User (userId);

DROP TABLE Study_Experiment;
