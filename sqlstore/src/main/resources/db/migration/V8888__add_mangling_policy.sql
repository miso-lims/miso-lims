ALTER TABLE InstrumentModel ADD COLUMN dataManglingPolicy VARCHAR(50) DEFAULT 'NONE';
UPDATE InstrumentModel SET dataManglingPolicy = 'I5_RC' WHERE platform = 'ILLUMINA' AND alias LIKE '%NextSeq%';
