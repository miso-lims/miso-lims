ALTER TABLE Run ADD COLUMN sequencingKitId bigint(20);
ALTER TABLE Run ADD CONSTRAINT fk_run_sequencingKit FOREIGN KEY (sequencingKitId) REFERENCES KitDescriptor (kitDescriptorId);
