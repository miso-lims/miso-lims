-- unique_run_alias
ALTER TABLE Run ADD CONSTRAINT uk_run_alias UNIQUE (alias);

-- sampleclass
ALTER TABLE SampleClass MODIFY COLUMN dnaseTreatable BOOLEAN NOT NULL DEFAULT FALSE;

-- project_status
ALTER TABLE Project CHANGE COLUMN progress status varchar(20) NOT NULL;

-- sequencing_kit
ALTER TABLE Run ADD COLUMN sequencingKitId bigint;
ALTER TABLE Run ADD CONSTRAINT fk_run_sequencingKit FOREIGN KEY (sequencingKitId) REFERENCES KitDescriptor (kitDescriptorId);

