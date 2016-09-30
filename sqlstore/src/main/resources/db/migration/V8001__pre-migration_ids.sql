ALTER TABLE DetailedSample ADD COLUMN preMigrationId bigint(20);
ALTER TABLE DetailedSample ADD CONSTRAINT sample_preMigrationId UNIQUE (premigrationId);
ALTER TABLE LibraryAdditionalInfo ADD COLUMN preMigrationId bigint(20);
ALTER TABLE LibraryAdditionalInfo ADD CONSTRAINT library_preMigrationId UNIQUE (premigrationId);
ALTER TABLE LibraryDilution ADD COLUMN preMigrationId bigint(20);
ALTER TABLE LibraryDilution ADD CONSTRAINT dilution_preMigrationId UNIQUE (premigrationId);
