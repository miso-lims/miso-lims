ALTER TABLE SequencingParameters ADD COLUMN runType varchar(255);

UPDATE SequencingParameters SET runType = 'ctc' WHERE parametersId = 101;
UPDATE SequencingParameters SET runType = 'platform_qc' WHERE parametersId = 102;
UPDATE SequencingParameters SET runType = 'sequencing_run' WHERE parametersId = 103;
UPDATE SequencingParameters SET runType = 'mux_scan' WHERE parametersId = 104;
