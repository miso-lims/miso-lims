-- add_run_alias_index

DELIMITER //

DROP PROCEDURE IF EXISTS createRunAliasIndex //
CREATE PROCEDURE createRunAliasIndex () BEGIN
  DECLARE indexIsThere INTEGER;
  DECLARE runAliasIndex VARCHAR(100);
  DECLARE databaseName VARCHAR(100);
  SET @runAliasIndex = 'run_alias_index';
  SELECT DATABASE() INTO @databaseName;

  SELECT COUNT(1) INTO indexIsThere
  FROM information_schema.statistics
  WHERE table_schema = @databaseName
  AND table_name = 'Run'
  AND index_name = @runAliasIndex;

  IF indexIsThere = 0 THEN
    SET @query = CONCAT('CREATE INDEX ', @runAliasIndex, ' ON ',
      @databaseName,'.Run (alias)');
    PREPARE stmt FROM @query;
    EXECUTE stmt;
    DEALLOCATE PREPARE stmt;
  ELSE
    SELECT CONCAT('Index ', @runAliasIndex, ' already exists on Run');
  END IF;

END //
CALL createRunAliasIndex()//
DROP PROCEDURE createRunAliasIndex//

DELIMITER ;
