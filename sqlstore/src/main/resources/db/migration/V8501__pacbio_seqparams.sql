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
