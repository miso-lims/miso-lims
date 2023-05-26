-- novaseq

INSERT INTO InstrumentModel(platform, alias, description, numContainers, instrumentType)
SELECT 'ILLUMINA', 'Illumina NovaSeq 6000', NULL, 1, 'SEQUENCER' FROM DUAL
WHERE NOT EXISTS(SELECT * FROM InstrumentModel WHERE platform = 'ILLUMINA' AND alias = 'Illumina NovaSeq 6000');
