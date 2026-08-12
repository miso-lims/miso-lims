CREATE TABLE RunElement (
  runId bigint NOT NULL,
  PRIMARY KEY (runId),
  CONSTRAINT fk_runelement_run FOREIGN KEY (runId) REFERENCES Run (runId)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
