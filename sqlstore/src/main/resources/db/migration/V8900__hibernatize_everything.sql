UPDATE KitDescriptor SET kitType = UPPER(kitType), platformType = UPPER(platformType);

UPDATE Platform SET name = UPPER(name);

ALTER TABLE Study ADD COLUMN studyTypeId bigint(20);
UPDATE Study SET studyTypeId = (SELECT typeId FROM StudyType WHERE name = studyType);
ALTER TABLE Study ADD CONSTRAINT study_studyTypeId FOREIGN KEY (studyTypeId) REFERENCES StudyType(typeId);
ALTER TABLE Study DROP COLUMN studyType;
ALTER TABLE Study CHANGE COLUMN studyTypeId studyTypeId bigint(20) NOT NULL;

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

UPDATE Pool_Elements SET elementType = CASE elementType WHEN 'uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryDilution' THEN 'LDI' WHEN 'uk.ac.bbsrc.tgac.miso.core.data.impl.emPCRDilution' THEN 'EDI' END;

ALTER TABLE SequencerReference ADD COLUMN ip VARCHAR(50) NOT NULL DEFAULT 'localhost';
-- H2 doesn't have INET_NTOA function
-- StartNoTest
UPDATE SequencerReference SET ip = INET_NTOA(ipAddress);
--EndNoTest
ALTER TABLE SequencerReference DROP COLUMN available;
ALTER TABLE SequencerReference DROP COLUMN ipAddress;
ALTER TABLE SequencerReference ADD CONSTRAINT upgraded_SR_UK UNIQUE (upgradedSequencerReferenceId);

DROP TABLE Request;
DROP TABLE Request_Note;

ALTER TABLE Status ADD CONSTRAINT Status_SequencerReference_FK FOREIGN KEY (instrumentName) REFERENCES SequencerReference (name);

ALTER TABLE Run_Note ADD CONSTRAINT RunNote_Run_FK FOREIGN KEY (run_runId) REFERENCES Run (runId);
ALTER TABLE Run_Note ADD CONSTRAINT RunNote_Note_FK FOREIGN KEY (notes_noteId) REFERENCES Note (noteId);

UPDATE Run SET platformType = UPPER(platformType);

ALTER TABLE RunQC_Partition ADD COLUMN partition_partitionId BIGINT(20);

UPDATE RunQC_Partition rqp SET partition_partitionId = (
  SELECT p.partitionId FROM `_Partition` p 
  JOIN SequencerPartitionContainer_Partition spcp ON spcp.partitions_partitionId = p.partitionId
  WHERE spcp.container_containerId = rqp.containers_containerId
  AND p.partitionNumber = rqp.partitionNumber
);
ALTER TABLE RunQC_Partition CHANGE COLUMN partition_partitionId partition_partitionId BIGINT(20) NOT NULL;
ALTER TABLE RunQC_Partition DROP PRIMARY KEY;
ALTER TABLE RunQC_Partition ADD PRIMARY KEY(`runQc_runQcId`, `partition_partitionId`);
ALTER TABLE RunQC_Partition ADD CONSTRAINT RunQCPartition_Partition_FK FOREIGN KEY (partition_partitionId) REFERENCES `_Partition` (partitionId);
ALTER TABLE RunQC_Partition ADD CONSTRAINT RunQCPartition_RunQC_FK FOREIGN KEY (runQc_runQcId) REFERENCES `RunQC` (qcId);
ALTER TABLE RunQC_Partition DROP COLUMN partitionNumber;
ALTER TABLE RunQC_Partition DROP COLUMN containers_containerId;

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

ALTER TABLE BoxChangeLog ADD boxChangeLogId bigint(20) PRIMARY KEY AUTO_INCREMENT FIRST;
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

ALTER TABLE ExperimentChangeLog ADD experimentChangeLogId bigint(20) PRIMARY KEY AUTO_INCREMENT FIRST;
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

ALTER TABLE KitDescriptorChangeLog ADD kitDescriptorChangeLogId bigint(20) PRIMARY KEY AUTO_INCREMENT FIRST;
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

ALTER TABLE LibraryChangeLog ADD libraryChangeLogId bigint(20) PRIMARY KEY AUTO_INCREMENT FIRST;
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

ALTER TABLE PoolChangeLog ADD poolChangeLogId bigint(20) PRIMARY KEY AUTO_INCREMENT FIRST;
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

ALTER TABLE RunChangeLog ADD runChangeLogId bigint(20) PRIMARY KEY AUTO_INCREMENT FIRST;
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

ALTER TABLE SampleChangeLog ADD sampleChangeLogId bigint(20) PRIMARY KEY AUTO_INCREMENT FIRST;
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

ALTER TABLE SequencerPartitionContainerChangeLog ADD sequencerPartitionContainerChangeLogId bigint(20) PRIMARY KEY AUTO_INCREMENT FIRST;
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

ALTER TABLE StudyChangeLog ADD studyChangeLogId bigint(20) PRIMARY KEY AUTO_INCREMENT FIRST;
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
