ALTER TABLE SequencingParameters ADD COLUMN chemistry varchar(255) DEFAULT 'UNKNOWN';

UPDATE SequencingParameters SET chemistry = 'V2' WHERE platformId IN (SELECT platformId FROM Platform WHERE name = 'ILLUMINA' AND instrumentModel = 'Illumina MiSeq');
UPDATE SequencingParameters SET chemistry = 'V3' WHERE xpath LIKE '%v3%';
UPDATE SequencingParameters SET chemistry = 'V4' WHERE xpath LIKE '%v4%';
UPDATE SequencingParameters SET chemistry = 'RAPID_RUN' WHERE xpath LIKE '%Rapid%';
UPDATE SequencingParameters SET chemistry = 'V4' WHERE platformId IN (SELECT platformId FROM Platform WHERE name = 'ILLUMINA' AND instrumentModel LIKE '%NextSeq%');

ALTER TABLE SequencingParameters DROP COLUMN xpath;
