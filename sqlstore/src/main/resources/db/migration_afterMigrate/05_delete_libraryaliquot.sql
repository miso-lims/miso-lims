-- StartNoTest
DELIMITER //

DROP PROCEDURE IF EXISTS deleteLibraryAliquot//
CREATE PROCEDURE deleteLibraryAliquot(
  iAliquotId BIGINT(20),
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

  -- check that the library aliquot exists and is derived from the given library
  IF NOT EXISTS (SELECT 1 FROM LibraryAliquot WHERE aliquotId = iAliquotId AND libraryId = iLibraryId)
  THEN
    SET errorMessage = CONCAT('Library aliquot with ID ', iAliquotId, ' derived from library LIB', iLibraryId, ' not found.');
    SIGNAL SQLSTATE '45000' SET message_text = errorMessage;
  END IF;

  -- confirm that the library aliquot is not present in any pools
  IF EXISTS (SELECT * FROM Pool_LibraryAliquot WHERE aliquotId = iAliquotId)
  THEN
    SET errorMessage = CONCAT('Cannot delete library aliquot with ID ', iAliquotId, ' since it is present in one or more pools.');
    SIGNAL SQLSTATE '45000' SET message_text = errorMessage;
  END IF;
  
  -- delete from LibraryAliquot table
  DELETE FROM LibraryAliquot WHERE aliquotId = iAliquotId;
  SELECT ROW_COUNT() AS number_deleted;

  COMMIT;
END//

DELIMITER ;
-- EndNoTest
