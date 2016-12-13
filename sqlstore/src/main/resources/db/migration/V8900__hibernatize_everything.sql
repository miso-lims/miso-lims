UPDATE KitDescriptor SET kitType = UPPER(kitType), platformType = UPPER(platformType);

UPDATE Platform SET name = UPPER(name);

ALTER TABLE Study ADD COLUMN studyTypeId bigint(20);
UPDATE Study SET studyTypeId = (SELECT typeId FROM StudyType WHERE name = studyType);
ALTER TABLE Study ADD CONSTRAINT study_studyTypeId FOREIGN KEY (studyTypeId) REFERENCES StudyType(typeId);
ALTER TABLE Study DROP COLUMN studyType;
ALTER TABLE Study ALTER COLUMN studyTypeId bigint(20) NOT NULL;

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

ALTER TABLE Status ADD FOREIGN KEY (instrumentName) REFERENCES SequencerReference (name);
ALTER TABLE Status ADD FOREIGN KEY (runName) REFERENCES Run (alias);
ALTER TABLE Run_Note ADD FOREIGN KEY (run_runId) REFERENCES Run (runId);
ALTER TABLE Run_Note ADD FOREIGN KEY (notes_noteId) REFERENCES Note (noteId);

UPDATE Run SET platformType = UPPER(platformType);

ALTER TABLE RunQC_Partition ADD COLUMN partition_partitionId BIGINT(20);
-- changes here
UPDATE RunQC_Partition rqp SET partition_partitionId = (
  SELECT p.partitionId FROM `_Partition` p 
  JOIN SequencerPartitionContainer_Partition spcp ON spcp.partitions_partitionId = p.partitionId
  WHERE spcp.container_containerId = rqp.containers_containerId
  AND p.partitionNumber = rqp.partitionNumber
);
ALTER TABLE RunQC_Partition CHANGE COLUMN partition_partitionId partition_partitionId BIGINT(20) NOT NULL;
ALTER TABLE RunQC_Partition ADD FOREIGN KEY (partition_partitionId) REFERENCES `_Partition` (partitionId);
ALTER TABLE RunQC_Partition ADD FOREIGN KEY (runQc_runQcId) REFERENCES `RunQC` (qcId);
ALTER TABLE RunQC_Partition DROP PRIMARY KEY;
ALTER TABLE RunQC_Partition ADD PRIMARY KEY(`runQc_runQcId`, `partitionId`);
ALTER TABLE RunQC_Partition DROP COLUMN partitionNumber;
ALTER TABLE RunQC_Partition DROP COLUMN containers_containerId;
