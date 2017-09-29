-- archive_library_type

ALTER TABLE LibraryType ADD COLUMN archived boolean NOT NULL DEFAULT FALSE;


-- sampleAdditionalInfo_to_detailedSample

ALTER TABLE SampleAdditionalInfo RENAME TO DetailedSample;


