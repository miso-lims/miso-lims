ALTER TABLE SequencingParameters CHANGE COLUMN paired paired TINYINT(1) NOT NULL DEFAULT 0;
ALTER TABLE SequencingParameters CHANGE COLUMN readLength readLength INT(11) NOT NULL DEFAULT 0;