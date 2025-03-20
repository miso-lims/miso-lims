CREATE TABLE RunUltima (
  runId bigint NOT NULL,
  PRIMARY KEY (runId),
  CONSTRAINT fk_runultima_run FOREIGN KEY (runId) REFERENCES Run (runId)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
