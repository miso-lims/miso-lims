-- pacbio_seqparams
UPDATE Run_SequencerPartitionContainer rspc
JOIN Run ON Run.runId = rspc.Run_runId
JOIN Instrument inst ON inst.instrumentId = Run.instrumentId
JOIN InstrumentModel im ON im.instrumentModelId = inst.instrumentModelId
SET rspc.sequencingParametersId = Run.sequencingParameters_parametersId
WHERE im.platform = 'PACBIO';

UPDATE Run
JOIN Instrument inst ON inst.instrumentId = Run.instrumentId
JOIN InstrumentModel im ON im.instrumentModelId = inst.instrumentModelId
SET Run.sequencingParameters_parametersId = NULL
WHERE im.platform = 'PACBIO';

-- array_run_attachment
DROP TABLE IF EXISTS ArrayRun_Attachment;

CREATE TABLE ArrayRun_Attachment (
  arrayRunId bigint NOT NULL,
  attachmentId bigint NOT NULL,
  PRIMARY KEY (arrayRunId, attachmentId),
  CONSTRAINT fk_attachment_arrayrun FOREIGN KEY (arrayRunId) REFERENCES ArrayRun (arrayRunId),
  CONSTRAINT fk_arrayrun_attachment FOREIGN KEY (attachmentId) REFERENCES Attachment (attachmentId)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

