UPDATE KitDescriptor SET kitType = UPPER(kitType), platformType = UPPER(platformType);

UPDATE Platform SET name = UPPER(name);

ALTER TABLE Study ADD COLUMN studyTypeId bigint(20);
UPDATE Study SET studyTypeId = (SELECT typeId FROM StudyType WHERE name = studyType);
ALTER TABLE Study ADD CONSTRAINT study_studyTypeId FOREIGN KEY (studyTypeId) REFERENCES StudyType(typeId);
ALTER TABLE Study DROP COLUMN studyType;
ALTER TABLE Study ALTER COLUMN studyTypeId bigint(20) NOT NULL;
