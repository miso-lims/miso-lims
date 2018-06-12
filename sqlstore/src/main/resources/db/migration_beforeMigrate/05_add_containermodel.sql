-- If you change these method names or signatures, update the docs in `docs/_posts/2017-12-07-admin-guide.md`
-- StartNoTest
DELIMITER //

DROP PROCEDURE IF EXISTS addContainerModel//
CREATE PROCEDURE addContainerModel(
  iName varchar(255),
  iBarcode varchar(255),
  iPartitionCount int(11),
  iPlatformType varchar(255),
  iPlatformModel varchar(50)
) BEGIN
  DECLARE errorMessage varchar(300);
  IF NOT EXISTS (SELECT 1 FROM Platform WHERE instrumentModel = iPlatformModel) THEN
    SET errorMessage = CONCAT('Platform ''', iPlatformModel, ''' not found.');
    SIGNAL SQLSTATE '45000' SET message_text = errorMessage;
  END IF;
  IF EXISTS (SELECT 1 FROM SequencingContainerModel WHERE alias = iName AND partitionCount = iPartitionCount 
  AND platformType = iPlatformType) THEN
    SET errorMessage = CONCAT('Container model ''', iName, ''' with ', iPartitionCount, ' partitions for platform ',
    iPlatformType, ' already exists.');
    SIGNAL SQLSTATE '45000' SET message_text = errorMessage;
  END IF;
  
  INSERT INTO SequencingContainerModel (alias, identificationBarcode, partitionCount, platformType)
  VALUES (iName, iBarcode, iPartitionCount, iPlatformType);
  INSERT INTO SequencingContainerModel_Platform (sequencingContainerModelId, platformId)
  VALUES (
    (SELECT sequencingContainerModelId FROM SequencingContainerModel WHERE alias = iName),
    (SELECT platformId FROM Platform WHERE instrumentModel = iPlatformModel));
END//

DELIMITER ;
-- EndNoTest