-- 
-- Add value procedures
-- 

-- StartNoTest
DELIMITER //

DROP PROCEDURE IF EXISTS addInstitute//
CREATE PROCEDURE addInstitute(
  iAlias varchar(255)
) BEGIN
  DECLARE createTime datetime DEFAULT CURRENT_TIMESTAMP;
  DECLARE createUser bigint(20);
  
  IF NOT EXISTS (SELECT 1 FROM Institute WHERE alias = iAlias)
  THEN
    SET createUser = getAdminUserId();
    INSERT INTO Institute(alias, createdBy, creationDate, updatedBy, lastUpdated)
    VALUES (iAlias, createUser, createTime, createUser, createTime);
  END IF;
END//

DROP PROCEDURE IF EXISTS addLab//
CREATE PROCEDURE addLab(
  iLabAlias varchar(255),
  iInstituteAlias varchar(255)
) BEGIN
  DECLARE createTime datetime DEFAULT CURRENT_TIMESTAMP;
  DECLARE createUser, instId bigint(20);
  DECLARE errorMessage varchar(300);
  
  SELECT instituteId INTO instId FROM Institute WHERE alias = iInstituteAlias;
  IF instId IS NULL
  THEN
    SET errorMessage = CONCAT('Institute ''', iInstituteAlias, ''' not found.');
    SIGNAL SQLSTATE '45000' SET message_text = errorMessage;
  ELSE
    IF NOT EXISTS (SELECT 1 FROM Lab WHERE instituteId = instId AND alias = iLabAlias) THEN
      SET createUser = getAdminUserId();
      INSERT INTO Lab(instituteId, alias, createdBy, creationDate, updatedBy, lastUpdated)
      VALUES (instId, iLabAlias, createUser, createTime, createUser, createTime);
    END IF;
  END IF;
END//
DELIMITER ;
-- EndNoTest
