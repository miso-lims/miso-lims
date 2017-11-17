SELECT qcTypeId INTO @dnaseQc FROM QCType WHERE name = 'DNAse Treated';

UPDATE SampleStock ss
JOIN DetailedSample ds ON ds.sampleId = ss.sampleId
JOIN SampleClass sc ON sc.sampleClassId = ds.sampleClassId
JOIN SampleQC sqc ON sqc.sample_sampleId = ss.sampleId
SET dnaseTreated = 1
WHERE sqc.type = @dnaseQc
AND sqc.results > 0
AND sc.dnaseTreatable = 1;

DELETE FROM SampleQC
WHERE `type` = @dnaseQc
AND sample_sampleId IN (
  SELECT ss.sampleId FROM SampleStock ss
  JOIN DetailedSample ds ON ds.sampleId = ss.sampleId
  JOIN SampleClass sc ON sc.sampleClassId = ds.sampleClassId
  WHERE sc.dnaseTreatable = 1
);

-- DNAse treated should only be set on dnaseTreatable Stocks. These cases have been fixed automatically.
-- Next line will fail and require manual fixing for other (unexpected) cases.
DELETE FROM QCType WHERE qcTypeId = @dnaseQc;
