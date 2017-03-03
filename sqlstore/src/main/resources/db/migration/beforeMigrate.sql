-- 
-- Add value procedures
-- 

-- StartNoTest
DELIMITER //

DROP PROCEDURE IF EXISTS addBoxSize//
CREATE PROCEDURE addBoxSize(
  iRows int,
  iColumns int,
  iScannable tinyint(1)
) BEGIN
  IF NOT EXISTS (SELECT 1 FROM BoxSize WHERE rows = iRows AND columns = iColumns AND scannable = iScannable)
  THEN
    INSERT INTO BoxSize(rows, columns, scannable)
    VALUES (iRows, iColumns, iScannable);
  END IF;
END//

DROP PROCEDURE IF EXISTS addBoxUse//
CREATE PROCEDURE addBoxUse(
  iAlias varchar(255)
) BEGIN
  IF NOT EXISTS (SELECT 1 FROM BoxUse WHERE alias = iAlias)
  THEN
    INSERT INTO BoxUse(alias) VALUES (iAlias);
  END IF;
END//

DROP PROCEDURE IF EXISTS addIndexFamily//
CREATE PROCEDURE addIndexFamily(
  iName varchar(255),
  iPlatformType varchar(20),
  iArchived tinyint(1)
) BEGIN
  IF NOT EXISTS (SELECT 1 FROM IndexFamily WHERE name = iName)
  THEN
    INSERT INTO IndexFamily(name, platformType, archived)
    VALUES (iName, UPPER(iPlatformType), iArchived);
  END IF;
END//

DROP PROCEDURE IF EXISTS addIndex//
CREATE PROCEDURE addIndex(
  iFamilyName varchar(255),
  iName varchar(10),
  iSequence varchar(20),
  iPosition int(11)
) BEGIN
  DECLARE famId bigint(20);
  DECLARE errorMessage varchar(300);
  SELECT indexFamilyId INTO famId FROM IndexFamily WHERE name = iFamilyName;
  IF famId IS NULL
  THEN
    SET errorMessage = CONCAT('IndexFamily ''', iFamilyName, ''' not found.');
    SIGNAL SQLSTATE '45000' SET message_text = errorMessage;
  ELSE
    IF NOT EXISTS (SELECT 1 FROM Indices WHERE indexFamilyId = famId AND sequence = iSequence AND position = iPosition)
    THEN
      INSERT INTO Indices(name, sequence, position, indexFamilyId)
      VALUES (iName, iSequence, iPosition, famId);
    END IF;
  END IF;
  
END//

DROP FUNCTION IF EXISTS getAdminUserId//
CREATE FUNCTION getAdminUserId() RETURNS bigint(20)
BEGIN
  DECLARE adminId bigint(20);
  SELECT userId INTO adminId FROM User WHERE loginName = 'admin';
    IF adminId IS NULL
    THEN
      SIGNAL SQLSTATE '45000' SET message_text = '''admin'' user not found.';
    ELSE
      RETURN adminId;
    END IF;
END//

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

DROP PROCEDURE IF EXISTS addKitDescriptor//
CREATE PROCEDURE addKitDescriptor(
  iName varchar(255),
  iVersion int(3),
  iManufacturer varchar(100),
  iPartNumber varchar(50),
  iKitType varchar(30),
  iPlatformType varchar(20),
  iDescription varchar(255)
) BEGIN
  DECLARE createUser bigint(20);
  
  IF NOT EXISTS (SELECT 1 FROM KitDescriptor WHERE name = iName)
  THEN
    SET createUser = getAdminUserId();
    INSERT INTO KitDescriptor(name, version, manufacturer, partNumber, kitType, platformType, description, lastModifier)
    VALUES (iName, iVersion, iManufacturer, iPartNumber, iKitType, iPlatformType, iDescription, createUser);
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

DROP PROCEDURE IF EXISTS addPlatform//
CREATE PROCEDURE addPlatform(
  iName varchar(50),
  iInstrumentModel varchar(100),
  iDescription varchar(255),
  iNumContainers tinyint(4)
) BEGIN
  IF NOT EXISTS (SELECT 1 FROM Platform WHERE name = iName and instrumentModel = iInstrumentModel) THEN
    INSERT INTO Platform(name, instrumentModel, description, numContainers)
    VALUES (iName, iInstrumentModel, iDescription, iNumContainers);
  END IF;
END//

DROP PROCEDURE IF EXISTS addQcType//
CREATE PROCEDURE addQcType(
  iName varchar(255),
  iDescription varchar(255),
  iQcTarget varchar(50),
  iUnits varchar(20)
) BEGIN
  IF NOT EXISTS (SELECT 1 FROM QCType WHERE name = iName AND qcTarget = iQcTarget) THEN
    INSERT INTO QCType(name, description, qcTarget, units)
    VALUES (iName, iDescription, iQcTarget, iUnits);
  END IF;
END//

DROP PROCEDURE IF EXISTS addReferenceGenome//
CREATE PROCEDURE addReferenceGenome(
  iAlias varchar(255)
) BEGIN
  IF NOT EXISTS (SELECT 1 FROM ReferenceGenome WHERE alias = iAlias) THEN
    INSERT INTO ReferenceGenome(alias)
    VALUES (iAlias);
  END IF;
END//

DROP PROCEDURE IF EXISTS addSamplePurpose//
CREATE PROCEDURE addSamplePurpose(
  iAlias varchar(255)
) BEGIN
  DECLARE createTime datetime DEFAULT CURRENT_TIMESTAMP;
  DECLARE createUser bigint(20);
  
  IF NOT EXISTS (SELECT 1 FROM SamplePurpose WHERE alias = iAlias) THEN
    SET createUser = getAdminUserId();
    INSERT INTO SamplePurpose(alias, createdBy, creationDate, updatedBy, lastUpdated)
    VALUES (iAlias, createUser, createTime, createUser, createTime);
  END IF;
END//

DROP PROCEDURE IF EXISTS addSequencerReference//
CREATE PROCEDURE addSequencerReference(
  iName varchar(30),
  iPlatformName varchar(50),
  iPlatformModel varchar(100),
  iSerialNumber varchar(30),
  iIpAddress varchar(50),
  iAvailable tinyint(1),
  iDateCommissioned date,
  iDateDecommissioned date,
  iUpgradedSequencerReferenceName varchar(30)
) BEGIN
  DECLARE platId, upgradedId bigint(20);
  DECLARE errorMessage varchar(300);
  IF NOT EXISTS (SELECT 1 FROM SequencerReference WHERE name = iName) THEN
    SELECT platformId INTO platId FROM Platform WHERE name = iPlatformName AND instrumentModel = iPlatformModel;
    IF platId IS NULL THEN
      SET errorMessage = CONCAT('Platform ''', iPlatformModel, ''' not found.');
      SIGNAL SQLSTATE '45000' SET message_text = errorMessage;
    END IF;
    IF iUpgradedSequencerReferenceId IS NOT NULL THEN
      SELECT referenceId INTO upgradedId FROM SequencerReference WHERE name = iUpgradedSequencerReferenceName;
      IF upgradedId IS NULL THEN
        SET errorMessage = CONCAT('Upgraded sequencer ''', iUpgradedSequencerReferenceName, ''' not found.');
        SIGNAL SQLSTATE '45000' SET message_text = errorMessage;
      END IF;
    ELSE
      SET upgradedId = NULL;
    END IF;
    
    INSERT INTO SequencerReference(name, ipAddress, platformId, available, serialNumber, dateCommissioned, dateDecommissioned,
    upgradedSequencerReferenceId)
    VALUES (iName, toIpAddressBlob(iIpAddress), platId, iAvailable, iSerialNumber, iDateCommissioned, iDateDecommissioned, upgradedId);
  END IF;
END//

DROP FUNCTION IF EXISTS toHexString//
CREATE FUNCTION toHexString(iNumber varchar(3)) RETURNS varchar(2)
BEGIN
  RETURN LPAD(HEX(CAST(iNumber AS DECIMAL)), 2, '0');
END// 

DROP FUNCTION IF EXISTS toIpAddressBlob//
CREATE FUNCTION toIpAddressBlob(iAddress varchar(30)) RETURNS blob
BEGIN
  DECLARE errorMessage varchar(300);
  DECLARE pos, len int;
  DECLARE hexString varchar(8);
  IF NOT (iAddress REGEXP '^[[:digit:]]{1,3}\\.[[:digit:]]{1,3}\\.[[:digit:]]{1,3}\\.[[:digit:]]{1,3}$') THEN
    SET errorMessage = CONCAT('Invalid IP address: ''', iAddress, '''');
    SIGNAL SQLSTATE '45000' SET message_text = errorMessage;
  END IF;
  SET pos = 1;
  SET len = LOCATE('.', iAddress, pos) - 1;
  SET hexString = toHexString(SUBSTRING(iAddress, pos, len));
  SET pos = pos + len + 1;
  SET len = LOCATE('.', iAddress, pos) - pos;
  SET hexString = CONCAT(hexString, toHexString(SUBSTRING(iAddress, pos, len)));
  SET pos = pos + len + 1;
  SET len = LOCATE('.', iAddress, pos) - pos;
  SET hexString = CONCAT(hexString, toHexString(SUBSTRING(iAddress, pos, len)));
  SET pos = pos + len + 1;
  SET hexString = CONCAT(hexString, toHexString(SUBSTRING(iAddress, pos)));
  RETURN UNHEX(hexString);
END//

DROP PROCEDURE IF EXISTS addSequencingParameters//
CREATE PROCEDURE addSequencingParameters(
  iName text,
  iPlatformName varchar(50),
  iPlatformModel varchar(100),
  iXpath varchar(1024),
  iReadLength int(11),
  iPaired tinyint(1)
) BEGIN
  DECLARE errorMessage varchar(300);
  DECLARE platId, createUser bigint(20);
  DECLARE createTime datetime DEFAULT CURRENT_TIMESTAMP;
  
  SELECT platformId INTO platId FROM Platform WHERE name = iPlatformName AND instrumentModel = iPlatformModel;
  IF platId IS NULL THEN
    SET errorMessage = CONCAT('Platform ''', iPlatformModel, ''' not found.');
    SIGNAL SQLSTATE '45000' SET message_text = errorMessage;
  END IF;
  
  IF NOT EXISTS (SELECT 1 FROM SequencingParameters WHERE name = iName AND platformId = platId) THEN
    SET createUser = getAdminUserId();
    INSERT INTO SequencingParameters(name, platformId, xpath, readLength, paired, createdBy, creationDate, updatedBy, lastUpdated)
    VALUES (iName, platId, iXpath, iReadLength, iPaired, createUser, createTime, createUser, createTime);
  END IF;
END//

DROP PROCEDURE IF EXISTS addTargetedSequencing//
CREATE PROCEDURE addTargetedSequencing(
  iAlias varchar(255),
  iDescription varchar(255),
  iKitName varchar(255),
  iArchived tinyint(1)
) BEGIN
  DECLARE errorMessage varchar(300);
  DECLARE kitId, createUser, exitingTargetedSequencingId, newTargetedSequencingId bigint(20);
  DECLARE createTime datetime DEFAULT CURRENT_TIMESTAMP;
  
  SELECT kitDescriptorId INTO kitId FROM KitDescriptor WHERE name = iKitName;
  IF kitId IS NULL THEN
    SET errorMessage = CONCAT('KitDescriptor ''', iKitName, ''' not found.');
    SIGNAL SQLSTATE '45000' SET message_text = errorMessage;
  END IF;
  
  IF NOT EXISTS (
    SELECT 1 FROM TargetedSequencing AS t
    JOIN TargetedSequencing_KitDescriptor AS tk ON t.targetedSequencingId = tk.targetedSequencingId
    WHERE t.alias = iAlias 
    AND tk.kitDescriptorId = kitId
    ) THEN
    SELECT targetedSequencingId INTO exitingTargetedSequencingId FROM TargetedSequencing WHERE alias = iAlias;
    IF exitingTargetedSequencingId IS NULL THEN
       SET createUser = getAdminUserId();
       INSERT INTO TargetedSequencing(alias, description, archived, createdBy, creationDate, updatedBy, lastUpdated)
       VALUES (iAlias, iDescription, iArchived, createUser, createTime, createUser, createTime);
       SET newTargetedSequencingId = LAST_INSERT_ID();
       INSERT INTO TargetedSequencing_KitDescriptor(targetedSequencingId, kitDescriptorId)
       VALUES(newTargetedSequencingId, kitId);
    ELSE
       INSERT INTO TargetedSequencing_KitDescriptor(targetedSequencingId, kitDescriptorId)
       VALUES(exitingTargetedSequencingId, kitId);
    END IF;
  END IF;
END//

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