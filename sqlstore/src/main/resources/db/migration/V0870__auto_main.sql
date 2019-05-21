-- add_run_type
ALTER TABLE SequencingParameters ADD COLUMN runType varchar(255);

UPDATE SequencingParameters SET runType = 'ctc' WHERE name = 'Configuration Test Cell';
UPDATE SequencingParameters SET runType = 'platform_qc' WHERE name = 'Platform Quality Control';
UPDATE SequencingParameters SET runType = 'sequencing_run' WHERE name = 'Sequencing Run';
UPDATE SequencingParameters SET runType = 'mux_scan' WHERE name = 'Control Experiment';

