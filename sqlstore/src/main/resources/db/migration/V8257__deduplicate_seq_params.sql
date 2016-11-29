--StartNoTest
UPDATE Run SET sequencingParameters_parametersId = (
  SELECT MIN(parametersId) FROM SequencingParameters sp 
  WHERE sp.platformId = (
    SELECT platformId FROM SequencingParameters
    WHERE parametersId = Run.sequencingParameters_parametersId) 
  AND sp.name = (SELECT name FROM SequencingParameters WHERE parametersId = Run.sequencingParameters_parametersId) 
  GROUP BY sp.platformId, sp.name
);

DELETE FROM SequencingParameters
WHERE parametersId NOT IN (
  SELECT MIN(sp.parametersId) FROM (SELECT * FROM SequencingParameters) sp
  GROUP BY sp.platformId, sp.name
);
--EndNoTest