-- StartNoTest
DELIMITER //

DROP PROCEDURE IF EXISTS addLibraryDesign//
CREATE PROCEDURE addLibraryDesign(
  iName varchar(255),
  iSampleClassAlias varchar(255),
  iLibrarySelectionTypeName varchar(50),
  iLibraryStrategyTypeName varchar(50),
  iLibraryDesignCode varchar(2)
) BEGIN
  DECLARE scId, lSelId, lStratId, ldcId bigint(20);
  DECLARE errorMessage varchar(300);
  
  SELECT sampleClassId INTO scId FROM SampleClass WHERE alias = iSampleClassAlias;
  SELECT librarySelectionTypeId INTO lSelId FROM LibrarySelectionType WHERE name = iLibrarySelectionTypeName;
  SELECT libraryStrategyTypeId INTO lStratId FROM LibraryStrategyType WHERE name = iLibraryStrategyTypeName;
  SELECT libraryDesignCodeId INTO ldcId FROM LibraryDesignCode WHERE code = iLibraryDesignCode;
  
  IF scId IS NULL THEN
    SET errorMessage = CONCAT('SampleClass ''', iSampleClassAlias, ''' not found.');
    SIGNAL SQLSTATE '45000' SET message_text = errorMessage;
  ELSEIF lSelId IS NULL THEN
    SET errorMessage = CONCAT('Library Selection Type ''', iLibrarySelectionTypeName, ''' not found.');
    SIGNAL SQLSTATE '45000' SET message_text = errorMessage;
  ELSEIF lStratId IS NULL THEN
    SET errorMessage = CONCAT('Library Strategy Type ''', iLibraryStrategyTypeName, ''' not found.');
    SIGNAL SQLSTATE '45000' SET message_text = errorMessage;
  ELSEIF ldcId IS NULL THEN
    SET errorMessage = CONCAT('Library Design Code ''', iLibraryDesignCode, ''' not found.');
    SIGNAL SQLSTATE '45000' SET message_text = errorMessage;
  ELSE
    IF NOT EXISTS (SELECT 1 FROM LibraryDesign WHERE name = iName AND sampleClassId = scId
        AND librarySelectionType = lSelId AND libraryStrategyType = lStratId AND libraryDesignCodeId = ldcId) THEN
      INSERT INTO LibraryDesign(name, sampleClassId, librarySelectionType, libraryStrategyType, libraryDesignCodeId)
      VALUES (iName, scId, lSelId, lStratId, ldcId);
    END IF;
  END IF;
END//

DROP PROCEDURE IF EXISTS addLibraryDesignCode//
CREATE PROCEDURE addLibraryDesignCode(
  iCode varchar(2),
  iDescription varchar(255)
) BEGIN
  IF NOT EXISTS (SELECT 1 FROM LibraryDesignCode WHERE code = iCode) THEN
    INSERT INTO LibraryDesignCode(code, description)
    VALUES (iCode, iDescription);
  END IF;
END//

DROP PROCEDURE IF EXISTS addLibraryType//
CREATE PROCEDURE addLibraryType(
  iDescription varchar(255),
  iPlatformType varchar(50),
  archived tinyint(1)
) BEGIN
  IF NOT EXISTS (SELECT 1 FROM LibraryType WHERE description = iDescription AND platformType = iPlatformType) THEN
    INSERT INTO LibraryType(description, platformType, archived)
    VALUES (iDescription, iPlatformType, iArchived);
  END IF;
END//
DELIMITER ;
-- EndNoTest
