-- StartNoTest
DELIMITER //

DROP PROCEDURE IF EXISTS deleteDilution//
CREATE PROCEDURE deleteDilution(
  iDilutionId BIGINT(20),
  iLibraryId BIGINT(20),
  iLoginName VARCHAR(255)
) BEGIN
  DECLARE errorMessage varchar(300);
  -- rollback if any errors are thrown
  DECLARE EXIT HANDLER FOR SQLEXCEPTION
  BEGIN
    ROLLBACK;
    RESIGNAL;
  END;

  START TRANSACTION;
  
  -- check that the user exists
  IF NOT EXISTS (SELECT 1 FROM User WHERE loginName = iLoginName)
  THEN
    SET errorMessage = CONCAT('Cannot find user with login name ', iLoginName);
    SIGNAL SQLSTATE '45000' SET message_text = errorMessage;
  END IF;

  -- check that the library dilution exists and is derived from the given library
  IF NOT EXISTS (SELECT 1 FROM LibraryDilution WHERE dilutionId = iDilutionId AND library_libraryId = iLibraryId)
  THEN
    SET errorMessage = CONCAT('Dilution with ID ', iDilutionId, ' derived from library LIB', iLibraryId, ' not found.');
    SIGNAL SQLSTATE '45000' SET message_text = errorMessage;
  END IF;

  -- confirm that the dilution is not present in any pools
  IF EXISTS (SELECT * FROM Pool_Dilution WHERE dilution_dilutionId = iDilutionId)
  THEN
    SET errorMessage = CONCAT('Cannot delete dilution with ID ', iDilutionId, ' since it is present in one or more pools.');
    SIGNAL SQLSTATE '45000' SET message_text = errorMessage;
  END IF;
  
  -- delete from LibraryDilution table
  DELETE FROM LibraryDilution WHERE dilutionId = iDilutionId;
  SELECT ROW_COUNT() AS number_deleted;
  
  -- add changelog to library
  INSERT INTO LibraryChangeLog (libraryId, columnsChanged, userId, message) VALUES
    (iLibraryId,
    CONCAT('LDI', iDilutionId),
    (SELECT userId FROM `User` WHERE loginName = iLoginName),
    CONCAT('Deleted dilution LDI', iDilutionId, '.'));

  COMMIT;
END//

DELIMITER ;
-- EndNoTest
