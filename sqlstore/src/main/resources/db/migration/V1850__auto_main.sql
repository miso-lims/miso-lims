-- sample_sopField
CREATE TABLE SampleSopFieldValue (
  sampleId BIGINT NOT NULL,
  sopFieldId BIGINT NOT NULL,
  value VARCHAR(255),
  PRIMARY KEY (sampleId, sopFieldId),
  CONSTRAINT fk_sampleSopFieldValue_sample FOREIGN KEY (sampleId) REFERENCES Sample(sampleId),
  CONSTRAINT fk_sampleSopFieldValue_sopField FOREIGN KEY (sopFieldId) REFERENCES SopField(sopFieldId)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- element
CREATE TABLE RunElement (
  runId bigint NOT NULL,
  PRIMARY KEY (runId),
  CONSTRAINT fk_runelement_run FOREIGN KEY (runId) REFERENCES Run (runId)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

