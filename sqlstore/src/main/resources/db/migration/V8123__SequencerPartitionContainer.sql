ALTER TABLE SequencerPartitionContainer ADD COLUMN sequencingParametersId bigint DEFAULT 0;
ALTER TABLE SequencingParameters ADD COLUMN movieTime bigint DEFAULT 0;