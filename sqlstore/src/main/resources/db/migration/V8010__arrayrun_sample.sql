ALTER TABLE ArrayRun
  ADD CONSTRAINT uk_arrayRun_id_array UNIQUE (arrayRunId, arrayId);

ALTER TABLE ArrayPosition
  ADD CONSTRAINT uk_arrayPosition_array_position_sample UNIQUE (arrayId, position, sampleId);

CREATE TABLE ArrayRun_Sample (
  arrayRunId bigint NOT NULL,
  arrayId bigint NOT NULL,
  position varchar(6) NOT NULL,
  sampleId bigint NOT NULL,
  statusId bigint,
  qcNote varchar(255),
  qcUser bigint,
  qcDate date,
  lastModifier bigint NOT NULL,
  PRIMARY KEY (arrayRunId, arrayId, position, sampleId),
  CONSTRAINT fk_arrayRunSample_arrayRun FOREIGN KEY (arrayRunId, arrayId) REFERENCES ArrayRun (arrayRunId, arrayId),
  CONSTRAINT fk_arrayRunSample_arrayPosition FOREIGN KEY (arrayId, position, sampleId)
    REFERENCES ArrayPosition (arrayId, position, sampleId),
  CONSTRAINT fk_arrayRunSample_status FOREIGN KEY (statusId) REFERENCES RunItemQcStatus (statusId),
  CONSTRAINT fk_arrayRunSample_qcUser FOREIGN KEY (qcUser) REFERENCES User (userId),
  CONSTRAINT fk_arrayRunSample_lastModifier FOREIGN KEY (lastModifier) REFERENCES User (userId)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

