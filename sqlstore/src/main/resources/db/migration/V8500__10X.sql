CREATE TABLE SampleSingleCell (
  sampleId bigint(20) NOT NULL,
  initialCellConcentration DECIMAL(14,10),
  digestion varchar(255) NOT NULL,
  PRIMARY KEY (sampleId),
  CONSTRAINT fk_sampleSingleCell_sample FOREIGN KEY (sampleId) REFERENCES Sample (sampleId)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE SampleStockSingleCell (
  sampleId bigint(20) NOT NULL,
  targetCellRecovery DECIMAL(14,10),
  cellViability DECIMAL(14,10),
  loadingCellConcentration DECIMAL(14,10),
  PRIMARY KEY (sampleId),
  CONSTRAINT fk_sampleStockSingleCell_sample FOREIGN KEY (sampleId) REFERENCES Sample (sampleId)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE SampleAliquotSingleCell (
  sampleId bigint(20) NOT NULL,
  inputIntoLibrary DECIMAL(14,10),
  PRIMARY KEY (sampleId),
  CONSTRAINT fk_sampleAliquotSingleCell_sample FOREIGN KEY (sampleId) REFERENCES Sample (sampleId)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
