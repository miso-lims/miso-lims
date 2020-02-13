-- fix_deleting_sample_hierarchy
ALTER TABLE SampleHierarchy DROP FOREIGN KEY fk_sampleHierarchy_tissue;
ALTER TABLE SampleHierarchy ADD CONSTRAINT fk_sampleHierarchy_tissue FOREIGN KEY (tissueId) REFERENCES SampleTissue (sampleId) ON DELETE CASCADE;

ALTER TABLE SampleHierarchy DROP FOREIGN KEY fk_sampleHierarchy_identity;
ALTER TABLE SampleHierarchy ADD CONSTRAINT fk_sampleHierarchy_identity FOREIGN KEY (identityId) REFERENCES Identity (sampleId) ON DELETE CASCADE;

-- transferTime_timestamp
ALTER TABLE Transfer MODIFY COLUMN transferTime timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP;

-- v2_naming_scheme
ALTER TABLE SampleClass ADD COLUMN v2NamingCode VARCHAR(2);
ALTER TABLE TissuePieceType ADD COLUMN v2NamingCode VARCHAR(2) NOT NULL DEFAULT 'TP';

-- multiple_naming_schemes
ALTER TABLE Project ADD COLUMN secondaryNaming BOOLEAN NOT NULL DEFAULT FALSE;

