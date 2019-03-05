-- drop_security_profiles

ALTER TABLE `Deletion` DROP FOREIGN KEY fk_deletion_securityProfile;
ALTER TABLE `Library` DROP FOREIGN KEY fk_library_securityProfile;
ALTER TABLE `Pool` DROP FOREIGN KEY fk_pool_securityProfile;
ALTER TABLE `Project` DROP FOREIGN KEY fk_project_securityProfile;
ALTER TABLE `Run` DROP FOREIGN KEY fk_run_securityProfile;
ALTER TABLE `Sample` DROP FOREIGN KEY fk_sample_securityProfile;
ALTER TABLE `Study` DROP FOREIGN KEY fk_study_securityProfile;

ALTER TABLE `Box` DROP COLUMN `securityProfile_profileId`;
ALTER TABLE `Deletion` DROP COLUMN `securityProfileId`;
ALTER TABLE `Experiment` DROP COLUMN `securityProfile_profileId`;
ALTER TABLE `Library` DROP COLUMN `securityProfile_profileId`;
ALTER TABLE `LibraryDilution` DROP COLUMN `securityProfile_profileId`;
ALTER TABLE `Pool` DROP COLUMN `securityProfile_profileId`;
ALTER TABLE `Project` DROP COLUMN `securityProfile_profileId`;
ALTER TABLE `Run` DROP COLUMN `securityProfile_profileId`;
ALTER TABLE `Sample` DROP COLUMN `securityProfile_profileId`;
ALTER TABLE `SequencerPartitionContainer` DROP COLUMN `securityProfile_profileId`;
ALTER TABLE `Study` DROP COLUMN `securityProfile_profileId`;
DROP TABLE `SecurityProfile`;

DROP TABLE `SecurityProfile_ReadGroup`;
DROP TABLE `SecurityProfile_WriteGroup`;
DROP TABLE `SecurityProfile_ReadUser`;
DROP TABLE `SecurityProfile_WriteUser`;



