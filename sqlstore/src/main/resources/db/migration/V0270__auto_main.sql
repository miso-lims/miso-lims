-- tissue_nullability

UPDATE SampleTissue SET tissueOriginId = (SELECT tissueOriginId FROM TissueOrigin WHERE description = 'Unknown') WHERE tissueOriginId IS NULL;

ALTER TABLE SampleTissue CHANGE tissueTypeId tissueTypeId bigint(20) NOT NULL;
ALTER TABLE SampleTissue CHANGE tissueOriginId tissueOriginId bigint(20) NOT NULL;


