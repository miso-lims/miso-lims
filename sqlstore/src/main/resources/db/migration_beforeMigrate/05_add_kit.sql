-- StartNoTest
DELIMITER //

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
DELIMITER ;
-- EndNoTest
