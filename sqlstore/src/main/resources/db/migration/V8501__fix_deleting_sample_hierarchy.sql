ALTER TABLE SampleHierarchy DROP FOREIGN KEY fk_sampleHierarchy_tissue;
ALTER TABLE SampleHierarchy ADD CONSTRAINT fk_sampleHierarchy_tissue FOREIGN KEY (tissueId) REFERENCES SampleTissue (sampleId) ON DELETE CASCADE;

ALTER TABLE SampleHierarchy DROP FOREIGN KEY fk_sampleHierarchy_identity;
ALTER TABLE SampleHierarchy ADD CONSTRAINT fk_sampleHierarchy_identity FOREIGN KEY (identityId) REFERENCES Identity (sampleId) ON DELETE CASCADE;
