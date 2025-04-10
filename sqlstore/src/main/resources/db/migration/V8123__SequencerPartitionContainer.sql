ALTER TABLE SequencerPartitionContainer ADD COLUMN sequencingParametersId bigint,
ADD CONSTRAINT `fk_sequencerPartitionContainer_sequencingParameters` FOREIGN KEY
(sequencingParametersId) REFERENCES SequencingParameters
(parametersId);
ALTER TABLE SequencingParameters ADD COLUMN movieTime int UNSIGNED;
UPDATE SequencingParameters SET movieTime = 0 WHERE instrumentModelId IN (SELECT instrumentModelId
FROM InstrumentModel WHERE platform = 'PACBIO');