ALTER TABLE SequencerPartitionContainer ADD COLUMN sequencingParametersId bigint,
ADD CONSTRAINT `FK_Spc_Sp` FOREIGN KEY
(sequencingParametersId) REFERENCES SequencingParameters
(parametersId);
ALTER TABLE SequencingParameters ADD COLUMN movieTime bigint;