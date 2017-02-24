ALTER TABLE DetailedSample ADD COLUMN concentration double DEFAULT NULL;
UPDATE DetailedSample ds SET concentration = (SELECT concentration FROM SampleStock ss WHERE ds.sampleId = ss.sampleId);
ALTER TABLE SampleStock DROP COLUMN concentration;