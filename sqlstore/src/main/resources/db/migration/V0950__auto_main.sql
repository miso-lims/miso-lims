-- tarseq_description_optional
ALTER TABLE TargetedSequencing MODIFY COLUMN description varchar(255);

-- seqparam_unique
ALTER TABLE SequencingParameters MODIFY COLUMN name varchar(255) NOT NULL;
ALTER TABLE SequencingParameters ADD CONSTRAINT uk_sequencingParameters_name_model UNIQUE (name, instrumentModelId);

