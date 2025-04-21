ALTER TABLE SequencingParameters
  ADD COLUMN flows SMALLINT UNSIGNED;

UPDATE SequencingParameters sp
JOIN InstrumentModel im ON im.instrumentModelId = sp.instrumentModelId
SET sp.flows = 0
WHERE im.platform = 'ULTIMA';
