CREATE OR REPLACE VIEW InstrumentStats AS
  SELECT
    Instrument.instrumentId AS id,
    Instrument.instrumentId AS instrumentId,
    (SELECT runId
      FROM Run r
      WHERE
        r.instrumentId = Instrument.instrumentId
        AND NOT EXISTS (SELECT *
         FROM Run i
         WHERE i.instrumentId = Instrument.instrumentId
           AND i.startDate > r.startDate)
      ORDER BY startDate DESC LIMIT 1) AS runId
  FROM Instrument
   JOIN Platform ON Platform.platformId = Instrument.platformId
  WHERE
    instrumentType = 'SEQUENCER'
    AND dateDecommissioned IS NULL;
