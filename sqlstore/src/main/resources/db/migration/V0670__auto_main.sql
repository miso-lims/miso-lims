-- 10X

DROP TABLE IF EXISTS SampleSingleCell;
DROP TABLE IF EXISTS SampleStockSingleCell;
DROP TABLE IF EXISTS SampleAliquotSingleCell;

CREATE TABLE SampleSingleCell (
  sampleId bigint NOT NULL,
  initialCellConcentration DECIMAL(14,10),
  digestion varchar(255) NOT NULL,
  PRIMARY KEY (sampleId),
  CONSTRAINT fk_sampleSingleCell_sample FOREIGN KEY (sampleId) REFERENCES Sample (sampleId)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE SampleStockSingleCell (
  sampleId bigint NOT NULL,
  targetCellRecovery DECIMAL(14,10),
  cellViability DECIMAL(14,10),
  loadingCellConcentration DECIMAL(14,10),
  PRIMARY KEY (sampleId),
  CONSTRAINT fk_sampleStockSingleCell_sample FOREIGN KEY (sampleId) REFERENCES Sample (sampleId)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE SampleAliquotSingleCell (
  sampleId bigint NOT NULL,
  inputIntoLibrary DECIMAL(14,10),
  PRIMARY KEY (sampleId),
  CONSTRAINT fk_sampleAliquotSingleCell_sample FOREIGN KEY (sampleId) REFERENCES Sample (sampleId)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


-- remove_on_update

ALTER TABLE LibraryDilution MODIFY COLUMN lastUpdated timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP;
ALTER TABLE DetailedQcStatus MODIFY COLUMN lastUpdated timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP;
ALTER TABLE Institute MODIFY COLUMN lastUpdated timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP;
ALTER TABLE Lab MODIFY COLUMN lastUpdated timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP;
ALTER TABLE PoolOrder MODIFY COLUMN lastUpdated timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP;
ALTER TABLE Project MODIFY COLUMN lastUpdated timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP;
ALTER TABLE ProjectOverview MODIFY COLUMN lastUpdated timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP;
ALTER TABLE SampleClass MODIFY COLUMN lastUpdated timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP;
ALTER TABLE SampleGroup MODIFY COLUMN lastUpdated timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP;
ALTER TABLE SampleNumberPerProject MODIFY COLUMN lastUpdated timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP;
ALTER TABLE SamplePurpose MODIFY COLUMN lastUpdated timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP;
ALTER TABLE SampleValidRelationship MODIFY COLUMN lastUpdated timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP;
ALTER TABLE SequencingParameters MODIFY COLUMN lastUpdated timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP;
ALTER TABLE Subproject MODIFY COLUMN lastUpdated timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP;
ALTER TABLE TargetedSequencing MODIFY COLUMN lastUpdated timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP;
ALTER TABLE TissueMaterial MODIFY COLUMN lastUpdated timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP;
ALTER TABLE TissueOrigin MODIFY COLUMN lastUpdated timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP;
ALTER TABLE TissueType MODIFY COLUMN lastUpdated timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP;
ALTER TABLE WorkflowProgress MODIFY COLUMN lastModified timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP;

DROP TABLE IF EXISTS Status;


-- library_spike_ins

DROP TABLE IF EXISTS LibrarySpikeIn;
CREATE TABLE LibrarySpikeIn (
  spikeInId bigint NOT NULL AUTO_INCREMENT,
  alias varchar(255) NOT NULL,
  PRIMARY KEY (spikeInId)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

ALTER TABLE Library ADD COLUMN spikeInId bigint;
ALTER TABLE Library ADD CONSTRAINT fk_library_spikeIn FOREIGN KEY (spikeInId) REFERENCES LibrarySpikeIn(spikeInId);
ALTER TABLE Library ADD COLUMN spikeInDilutionFactor varchar(50);
ALTER TABLE Library ADD COLUMN spikeInVolume DECIMAL(14,10);


-- ont_container_models

INSERT INTO SequencingContainerModel(alias, partitionCount, platformType)
SELECT alias, 1, 'OXFORDNANOPORE' FROM FlowCellVersion;

INSERT INTO SequencingContainerModel_Platform(sequencingContainerModelId, platformId)
SELECT sequencingContainerModelId, (SELECT platformId FROM Platform WHERE instrumentModel = 'MinION')
FROM SequencingContainerModel
WHERE alias LIKE 'FLO-MIN%' AND platformType = 'OXFORDNANOPORE';

INSERT INTO SequencingContainerModel_Platform(sequencingContainerModelId, platformId)
SELECT sequencingContainerModelId, (SELECT platformId FROM Platform WHERE instrumentModel = 'PromethION')
FROM SequencingContainerModel
WHERE alias LIKE 'PRO-%' AND platformType = 'OXFORDNANOPORE';

UPDATE SequencerPartitionContainer spc
JOIN OxfordNanoporeContainer ont ON ont.containerId = spc.containerId
JOIN FlowCellVersion fcv ON fcv.flowCellVersionId = ont.flowCellVersionId
SET spc.sequencingContainerModelId = COALESCE(
  (SELECT sequencingContainerModelId FROM SequencingContainerModel WHERE alias = fcv.alias AND platformType = 'OXFORDNANOPORE'),
  spc.sequencingContainerModelId
);

ALTER TABLE OxfordNanoporeContainer DROP FOREIGN KEY FK_OxfordNanoporeContainer_FlowCellVersion;
ALTER TABLE OxfordNanoporeContainer DROP COLUMN flowCellVersionId;
DROP TABLE FlowCellVersion;


-- add_order_description

ALTER TABLE `PoolOrder` ADD COLUMN description varchar(255); 


-- storage_url

ALTER TABLE StorageLocation ADD COLUMN mapUrl varchar(1024);


