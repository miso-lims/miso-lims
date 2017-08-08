ALTER TABLE SequencingParameters ADD COLUMN chemistry varchar(255) DEFAULT 'UNKNOWN';

UPDATE SequencingParameters SET chemistry = 'V2' WHERE platformId IN (SELECT platformId FROM Platform WHERE name = 'ILLUMINA' AND instrumentModel = 'Illumina MiSeq');
UPDATE SequencingParameters SET chemistry = 'V3' WHERE xpath LIKE '%v3%';
UPDATE SequencingParameters SET chemistry = 'V4' WHERE xpath LIKE '%v4%';
UPDATE SequencingParameters SET chemistry = 'RAPID_RUN' WHERE xpath LIKE '%Rapid%';
INSERT INTO SequencingParameters (name, platformId, readLength, paired, createdBy, creationDate, updatedBy, lastUpdated, chemistry)
  SELECT CONCAT('High ', name), platformId, readLength, paired, createdBy, CURRENT_TIMESTAMP, updatedBy, CURRENT_TIMESTAMP, 'NS_HIGH'
  FROM SequencingParameters WHERE platformId IN (SELECT platformId FROM Platform WHERE name = 'ILLUMINA' AND instrumentModel LIKE '%NextSeq%') AND chemistry = 'UNKNOWN' AND name NOT LIKE 'Custom%';

UPDATE SequencingParameters SET name = CONCAT('Mid ', name), chemistry = 'NS_MID' WHERE platformId IN (SELECT platformId FROM Platform WHERE name = 'ILLUMINA' AND instrumentModel LIKE '%NextSeq%') AND chemistry = 'UNKNOWN' AND name NOT LIKE 'Custom%';

ALTER TABLE SequencingParameters DROP COLUMN xpath;
