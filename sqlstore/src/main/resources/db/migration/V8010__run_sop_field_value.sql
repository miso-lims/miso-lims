CREATE TABLE RunSopFieldValue (
  runId BIGINT NOT NULL,
  sopFieldId BIGINT NOT NULL,
  value VARCHAR(255),
  PRIMARY KEY (runId, sopFieldId),
  CONSTRAINT fk_runSopFieldValue_run FOREIGN KEY (runId) REFERENCES Run(runId),
  CONSTRAINT fk_runSopFieldValue_sopField FOREIGN KEY (sopFieldId) REFERENCES SopField(sopFieldId)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;