ALTER TABLE SequencerPartitionContainer ADD COLUMN sequencingParametersId bigint,
ADD CONSTRAINT `fk_sequencerPartitionContainer_sequencingParameters` FOREIGN KEY
(sequencingParametersId) REFERENCES SequencingParameters
(parametersId);
ALTER TABLE SequencingParameters ADD COLUMN movieTime int UNSIGNED;