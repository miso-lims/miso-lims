-- StartNoTest
DELIMITER //


DROP PROCEDURE IF EXISTS addTissueMaterial//
CREATE PROCEDURE addTissueMaterial(iAlias varchar(255))
BEGIN
  DECLARE createUser bigint(20);
  DECLARE createTime datetime DEFAULT CURRENT_TIMESTAMP;
  
  IF NOT EXISTS (SELECT 1 FROM TissueMaterial WHERE alias = iAlias) THEN
    SET createUser = getAdminUserId();
    INSERT INTO TissueMaterial(alias, createdBy, creationDate, updatedBy, lastUpdated)
    VALUES (iAlias, createUser, createTime, createUser, createTime);
  END IF;
END//

DROP PROCEDURE IF EXISTS addTissueOrigin//
CREATE PROCEDURE addTissueOrigin(
  iAlias varchar(255),
  iDescription varchar(255)
) BEGIN
  DECLARE createUser bigint(20);
  DECLARE createTime datetime DEFAULT CURRENT_TIMESTAMP;
  
  IF NOT EXISTS (SELECT 1 FROM TissueOrigin WHERE alias = iAlias) THEN
    SET createUser = getAdminUserId();
    INSERT INTO TissueOrigin(alias, description, createdBy, creationDate, updatedBy, lastUpdated)
    VALUES (iAlias, iDescription, createUser, createTime, createUser, createTime);
  END IF;
END//

DROP PROCEDURE IF EXISTS addTissueType//
CREATE PROCEDURE addTissueType(
  iAlias varchar(255),
  iDescription varchar(255)
) BEGIN
  DECLARE createUser bigint(20);
  DECLARE createTime datetime DEFAULT CURRENT_TIMESTAMP;
  
  IF NOT EXISTS (SELECT 1 FROM TissueType WHERE alias = iAlias) THEN
    SET createUser = getAdminUserId();
    INSERT INTO TissueType(alias, description, createdBy, creationDate, updatedBy, lastUpdated)
    VALUES (iAlias, iDescription, createUser, createTime, createUser, createTime);
  END IF;
END//

DELIMITER ;
-- EndNoTest
