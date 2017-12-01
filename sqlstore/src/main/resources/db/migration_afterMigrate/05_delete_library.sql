-- StartNoTest
DELIMITER //

DROP PROCEDURE IF EXISTS deleteLibrary//
CREATE PROCEDURE deleteLibrary(
  iLibraryId BIGINT(20),
  iLibraryAlias VARCHAR(255)
) BEGIN
  DECLARE errorMessage varchar(300);
  -- rollback if any errors are thrown
  DECLARE EXIT HANDLER FOR SQLEXCEPTION
  BEGIN
    ROLLBACK;
    RESIGNAL;
  END;

  START TRANSACTION;

  -- check that the library exists
  IF NOT EXISTS (SELECT 1 FROM Library WHERE libraryId = iLibraryId AND alias = iLibraryAlias)
  THEN
    SET errorMessage = CONCAT('Library with ID ', iLibraryId, ' and alias ', iLibraryAlias, ' not found.');
    SIGNAL SQLSTATE '45000' SET message_text = errorMessage;
  END IF;

  -- confirm that the library has no dilution children
  IF EXISTS (SELECT * FROM LibraryDilution WHERE library_libraryId = iLibraryId)
  THEN
    SET errorMessage = CONCAT('Cannot delete library with ID ', iLibraryId, ' due to child dilutions.');
    SIGNAL SQLSTATE '45000' SET message_text = errorMessage;
  END IF;

  -- delete related LibraryQCs, notes, and indices
  DELETE FROM LibraryQC WHERE library_libraryId = iLibraryId;
  DELETE FROM Note WHERE noteId IN (SELECT notes_noteId FROM Library_Note WHERE library_libraryId = iLibraryId);
  DELETE FROM Library_Note WHERE library_libraryId = iLibraryId;
  DELETE FROM Library_Index WHERE library_libraryId = iLibraryId;

  -- delete from DetailedLibrary
  DELETE FROM DetailedLibrary WHERE libraryId = iLibraryId;
  DELETE FROM LibraryChangeLog WHERE libraryId = iLibraryId;

  -- delete from Library table
  DELETE FROM Library WHERE libraryId = iLibraryId;
  SELECT ROW_COUNT() AS number_deleted;

  COMMIT;
END//
DELIMITER ;
-- EndNoTest
