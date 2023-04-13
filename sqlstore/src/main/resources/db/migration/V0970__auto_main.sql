-- initial_volumes
ALTER TABLE Sample MODIFY COLUMN volume DECIMAL(14,10);
ALTER TABLE Sample MODIFY COLUMN concentration DECIMAL(14,10);
ALTER TABLE SampleQC MODIFY COLUMN results DECIMAL(16,10);
ALTER TABLE Library MODIFY COLUMN volume DECIMAL(14,10);
ALTER TABLE Library MODIFY COLUMN concentration DECIMAL(14,10);
ALTER TABLE LibraryQC MODIFY COLUMN results DECIMAL(16,10);
ALTER TABLE LibraryAliquot MODIFY COLUMN volume DECIMAL(14,10);
ALTER TABLE LibraryAliquot MODIFY COLUMN volumeUsed DECIMAL(14,10);
ALTER TABLE LibraryAliquot MODIFY COLUMN ngUsed DECIMAL(14,10);
ALTER TABLE LibraryAliquot MODIFY COLUMN concentration DECIMAL(14,10);
ALTER TABLE Pool MODIFY COLUMN volume DECIMAL(14,10);
ALTER TABLE Pool MODIFY COLUMN concentration DECIMAL(14,10);
ALTER TABLE PoolQC MODIFY COLUMN results DECIMAL(16,10);
ALTER TABLE ContainerQC MODIFY COLUMN results DECIMAL(16,10);
ALTER TABLE LibraryTemplate MODIFY COLUMN defaultVolume DECIMAL(14,10);

ALTER TABLE Sample ADD COLUMN initialVolume DECIMAL(14,10);
ALTER TABLE DetailedSample ADD COLUMN volumeUsed DECIMAL(14,10);
ALTER TABLE DetailedSample ADD COLUMN ngUsed DECIMAL(14,10);
ALTER TABLE Library ADD COLUMN initialVolume DECIMAL(14,10);
ALTER TABLE Library ADD COLUMN volumeUsed DECIMAL(14,10);
ALTER TABLE Library ADD COLUMN ngUsed DECIMAL(14,10);

ALTER TABLE SampleSlide CHANGE COLUMN slides initialSlides int NOT NULL DEFAULT 0;
ALTER TABLE SampleSlide ADD COLUMN slides int NOT NULL DEFAULT 0;
UPDATE SampleSlide SET slides = initialSlides - discards;
UPDATE SampleSlide SET slides = slides - COALESCE((
  SELECT SUM(stp.slidesConsumed)
  FROM SampleTissuePiece stp
  JOIN DetailedSample ds ON ds.sampleId = stp.sampleId
  WHERE ds.parentId = SampleSlide.sampleId
), 0);

-- libraryTemplate_volumeUnits
ALTER TABLE LibraryTemplate ADD COLUMN volumeUnits varchar(30);
UPDATE LibraryTemplate SET volumeUnits = 'MICROLITRES' WHERE defaultVolume IS NOT NULL;

-- umis
ALTER TABLE Library ADD COLUMN umis BOOLEAN NOT NULL DEFAULT FALSE;

