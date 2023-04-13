-- tissue_nullability

UPDATE SampleTissue SET tissueOriginId = (SELECT tissueOriginId FROM TissueOrigin WHERE description = 'Unknown') WHERE tissueOriginId IS NULL;

ALTER TABLE SampleTissue CHANGE tissueTypeId tissueTypeId bigint NOT NULL;
ALTER TABLE SampleTissue CHANGE tissueOriginId tissueOriginId bigint NOT NULL;

-- PacBio_library_options

INSERT INTO LibraryType (description, platformType, archived) VALUES ('Whole Genome', 'PACBIO', 0);

INSERT INTO LibrarySelectionType (name, description) VALUES ('SageHLS', 'Sage High Molecular Weight Library System'), ('BluePippin', 'Sage BluePippin size selection');