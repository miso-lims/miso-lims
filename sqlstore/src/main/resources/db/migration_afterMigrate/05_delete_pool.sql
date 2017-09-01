-- StartNoTest
DELIMITER //

DROP PROCEDURE IF EXISTS deletePool//
CREATE PROCEDURE deletePool(
  iPoolId BIGINT(20),
  iPoolAlias VARCHAR(255)
) BEGIN
  DECLARE errorMessage VARCHAR(300);
  -- rollback if any errors are thrown
  DECLARE EXIT HANDLER FOR SQLEXCEPTION
  BEGIN
    ROLLBACK;
    RESIGNAL;
  END;
  
  START TRANSACTION;
  
  -- check that the pool exists
  IF NOT EXISTS (SELECT 1 FROM Pool WHERE poolId = iPoolId AND alias = iPoolAlias)
  THEN
    SET errorMessage = CONCAT('Cannot find pool with ID ', iPoolId, ' and alias "', iPoolAlias, '"');
    SIGNAL SQLSTATE '45000' SET message_text = errorMessage;
  END IF;
  
  DELETE Note, Pool_Note
    FROM Pool_Note
    JOIN Note
    WHERE Note.noteId = Pool_Note.notes_noteId
    AND Pool_Note.pool_poolId = iPoolId;

  DELETE FROM PoolOrder WHERE poolId = iPoolId;
  DELETE FROM PoolQC WHERE pool_poolId = iPoolId;
  DELETE FROM Pool_Watcher WHERE poolId = iPoolId;
  DELETE FROM PoolChangeLog WHERE poolId = iPoolId;
  DELETE FROM Pool_Dilution WHERE pool_poolId = iPoolId;
  DELETE FROM Pool WHERE poolId = iPoolId;
  
  SELECT ROW_COUNT() AS number_deleted;
  
  COMMIT;
END//

DELIMITER ;
-- EndNoTest
