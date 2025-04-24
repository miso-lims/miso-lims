-- SequencerPartitionContainer
ALTER TABLE Run_SequencerPartitionContainer ADD COLUMN sequencingParametersId bigint,
ADD CONSTRAINT `fk_Run_SequencerPartitionContainer_sequencingParameters` FOREIGN KEY
(sequencingParametersId) REFERENCES SequencingParameters
(parametersId);
ALTER TABLE SequencingParameters ADD COLUMN movieTime int UNSIGNED;
UPDATE SequencingParameters SET movieTime = 0 WHERE instrumentModelId IN (SELECT instrumentModelId
FROM InstrumentModel WHERE platform = 'PACBIO');

-- sample_indices
RENAME TABLE IndexFamily TO LibraryIndexFamily;
RENAME TABLE Indices TO LibraryIndex;

CREATE TABLE SampleIndexFamily (
  indexFamilyId bigint NOT NULL AUTO_INCREMENT,
  name varchar(255) NOT NULL,
  PRIMARY KEY (indexFamilyId),
  CONSTRAINT uk_sampleIndexFamily_name UNIQUE (name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE SampleIndex (
  indexId bigint NOT NULL AUTO_INCREMENT,
  name varchar(24) NOT NULL,
  indexFamilyId bigint NOT NULL,
  PRIMARY KEY (indexId),
  CONSTRAINT fk_sampleIndex_sampleIndexFamily FOREIGN KEY (indexFamilyId)
    REFERENCES SampleIndexFamily(indexFamilyId),
  CONSTRAINT uk_sampleIndex_family_name UNIQUE (indexFamilyId, name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

ALTER TABLE Sample
  ADD COLUMN indexId bigint,
  ADD CONSTRAINT fk_sample_sampleIndex FOREIGN KEY (indexId) REFERENCES SampleIndex (indexId);

-- ultima_seqparams
ALTER TABLE SequencingParameters
  ADD COLUMN flows SMALLINT UNSIGNED;

UPDATE SequencingParameters sp
JOIN InstrumentModel im ON im.instrumentModelId = sp.instrumentModelId
SET sp.flows = 0
WHERE im.platform = 'ULTIMA';

