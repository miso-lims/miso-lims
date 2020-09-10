-- Any triggers, procedures, and functions that are created in after migrate scripts
-- and depend on the schema should be dropped here

-- Disable "Trigger does not exist" warnings
SET sql_notes = 0;

DROP TRIGGER IF EXISTS ArrayInsert;
DROP TRIGGER IF EXISTS ArrayChange;
DROP TRIGGER IF EXISTS ArrayPositionInsert;
DROP TRIGGER IF EXISTS ArrayRunInsert;
DROP TRIGGER IF EXISTS ArrayRunChange;
DROP TRIGGER IF EXISTS BoxChange;
DROP TRIGGER IF EXISTS BoxInsert;
DROP TRIGGER IF EXISTS ContainerQCInsert;
DROP TRIGGER IF EXISTS ContainerQcUpdate;
DROP TRIGGER IF EXISTS ExperimentChange;
DROP TRIGGER IF EXISTS ExperimentInsert;
DROP TRIGGER IF EXISTS KitDescriptorChange;
DROP TRIGGER IF EXISTS KitDescriptorInsert;
DROP TRIGGER IF EXISTS LibraryChange;
DROP TRIGGER IF EXISTS LibraryAliquotChange;
DROP TRIGGER IF EXISTS LibraryAliquotInsert;
DROP TRIGGER IF EXISTS LibraryInsert;
DROP TRIGGER IF EXISTS LibraryQCInsert;
DROP TRIGGER IF EXISTS LibraryQcUpdate;
DROP TRIGGER IF EXISTS OxfordNanoporeContainerChange;
DROP TRIGGER IF EXISTS PartitionChange;
DROP TRIGGER IF EXISTS PartitionQCInsert;
DROP TRIGGER IF EXISTS PartitionQCUpdate;
DROP TRIGGER IF EXISTS PoolChange;
DROP TRIGGER IF EXISTS PoolInsert;
DROP TRIGGER IF EXISTS PoolOrderChange;
DROP TRIGGER IF EXISTS PoolOrderInsert;
DROP TRIGGER IF EXISTS PoolQCInsert;
DROP TRIGGER IF EXISTS PoolQcUpdate;
DROP TRIGGER IF EXISTS ProjectInsert;
DROP TRIGGER IF EXISTS ProjectChange;
DROP TRIGGER IF EXISTS RunChange;
DROP TRIGGER IF EXISTS RunChangeIllumina;
DROP TRIGGER IF EXISTS RunChangeLS454;
DROP TRIGGER IF EXISTS RunChangeOxfordNanopore;
DROP TRIGGER IF EXISTS RunChangeSolid;
DROP TRIGGER IF EXISTS RunInsert;
DROP TRIGGER IF EXISTS RunPartitionUpdate;
DROP TRIGGER IF EXISTS RunPartitionLibraryAliquotInsert;
DROP TRIGGER IF EXISTS RunPartitionLibraryAliquotUpdate;
DROP TRIGGER IF EXISTS SampleChange;
DROP TRIGGER IF EXISTS SampleInsert;
DROP TRIGGER IF EXISTS SampleQCInsert;
DROP TRIGGER IF EXISTS SampleQcUpdate;
DROP TRIGGER IF EXISTS SequencerPartitionContainerChange;
DROP TRIGGER IF EXISTS SequencerPartitionContainerInsert;
DROP TRIGGER IF EXISTS SequencingOrderChange;
DROP TRIGGER IF EXISTS SequencingOrderDelete;
DROP TRIGGER IF EXISTS SequencingOrderInsert;
DROP TRIGGER IF EXISTS StorageLocationInsert;
DROP TRIGGER IF EXISTS StorageLocationChange;
DROP TRIGGER IF EXISTS StudyChange;
DROP TRIGGER IF EXISTS StudyInsert;
DROP TRIGGER IF EXISTS TransferChange;
DROP TRIGGER IF EXISTS TransferInsert;
DROP TRIGGER IF EXISTS WorksetChange;
DROP TRIGGER IF EXISTS WorksetInsert;

DROP FUNCTION IF EXISTS getParentTissueId;
DROP FUNCTION IF EXISTS getParentIdentityId;

DROP PROCEDURE IF EXISTS updateSampleHierarchy;

SET sql_notes = 1;

