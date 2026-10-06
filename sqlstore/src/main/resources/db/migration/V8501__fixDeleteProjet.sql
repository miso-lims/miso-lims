DELIMITER //

CREATE PROCEDURE FixSubprojectProjectKey()
BEGIN
  DECLARE fk_name VARCHAR(64);

  SELECT CONSTRAINT_NAME INTO fk_name
  FROM INFORMATION_SCHEMA.KEY_COLUMN_USAGE
  WHERE TABLE_SCHEMA = SCHEMA()
    AND TABLE_NAME = 'Subproject'
    AND COLUMN_NAME = 'projectId'
    AND REFERENCED_TABLE_NAME = 'Project';
  
  SET @sql = CONCAT('ALTER TABLE Subproject DROP FOREIGN KEY ', fk_name);
  PREPARE stmt FROM @sql;
  EXECUTE stmt;
  DEALLOCATE PREPARE stmt;
END//

DELIMITER ;

CALL FixSubprojectProjectKey();
DROP PROCEDURE FixSubprojectProjectKey;

ALTER TABLE Subproject
  ADD CONSTRAINT fk_subproject_project FOREIGN KEY (projectId)
  REFERENCES Project (projectId) ON DELETE CASCADE;
