-- concentration_to_detailedSample

ALTER TABLE DetailedSample ADD COLUMN concentration double DEFAULT NULL;
UPDATE DetailedSample ds SET concentration = (SELECT concentration FROM SampleStock ss WHERE ds.sampleId = ss.sampleId);
ALTER TABLE SampleStock DROP COLUMN concentration;


-- drop_partition_securityProfile

ALTER TABLE _Partition DROP FOREIGN KEY fk_partition_securityProfile;
ALTER TABLE _Partition DROP COLUMN securityProfile_profileId;


