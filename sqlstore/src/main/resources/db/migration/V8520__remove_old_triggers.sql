-- StartNoTest
-- Disable "x does not exist" warnings
SET sql_notes = 0;

DROP TRIGGER IF EXISTS LibraryAdditionalInfoChange;
DROP TRIGGER IF EXISTS BeforeInsertLibrary;
DROP TRIGGER IF EXISTS BeforeInsertPool;
DROP TRIGGER IF EXISTS RunChangePacBio;
DROP TRIGGER IF EXISTS SampleAdditionalInfoChange;
DROP TRIGGER IF EXISTS SampleCVSlideChange;
DROP TRIGGER IF EXISTS BeforeInsertSample;
DROP TRIGGER IF EXISTS LibraryAdditionalInfoChange;
DROP TRIGGER IF EXISTS PlateChange;
DROP TRIGGER IF EXISTS PlateInsert;
DROP TRIGGER IF EXISTS SampleLCMTubeChange;
DROP TRIGGER IF EXISTS StatusChange;

DROP FUNCTION IF EXISTS `nextval`;
DROP PROCEDURE IF EXISTS moveBoxItem;
DROP PROCEDURE IF EXISTS removeBoxItem;
DROP VIEW IF EXISTS BoxableView;

SET sql_notes = 1;
-- EndNoTest
