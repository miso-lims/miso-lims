ALTER TABLE Platform ADD COLUMN dataManglingPolicy VARCHAR(50) DEFAULT 'NONE';
UPDATE Platform SET dataManglingPolicy = 'I5_RC' WHERE name = 'ILLUMINA' and instrumentModel LIKE '%NextSeq%';
