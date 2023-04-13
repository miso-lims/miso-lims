-- identity_changes

ALTER TABLE `Identity` DROP COLUMN internalName;


-- unique_kitDescriptors

/*
* This migration cleans up some data that was added in a previous migration (V0002), merging/removing KitDescriptors with
* duplicate names. If any other duplicates have been added, they must be resolved manually before this migration will succeed.
* If there are multiple versions of the same kit, 'V1,' 'V2,' etc. can be added to the end of the name for uniqueness.
*/

-- Remove DetailedSample.kitDescriptorId field (currently unused; also only applicable to Stocks)
-- StartNoTest
ALTER TABLE DetailedSample DROP FOREIGN KEY DetailedSample_ibfk_1;
-- EndNoTest
ALTER TABLE DetailedSample DROP COLUMN kitDescriptorId;

-- Add missing FK
ALTER TABLE Kit ADD CONSTRAINT kit_kitDescriptor_fkey FOREIGN KEY (kitDescriptorId) REFERENCES KitDescriptor (kitDescriptorId);


-- StartNoTest

-- Remove 'Test' kit added in V0002 migration
DELETE FROM KitDescriptor WHERE kitDescriptorId=121 AND name='Test' AND partNumber='123123123';

-- Remove duplicates added in V0002 migration
DELIMITER //
DROP FUNCTION IF EXISTS kitExists//
CREATE FUNCTION kitExists (id bigint(20), kitName varchar(255), kitVersion int(3), kitManufacturer varchar(100),
    kitPartNumber varchar(50), kitKitType varchar(30), kitPlatformType varchar(20))
    RETURNS boolean NOT DETERMINISTIC READS SQL DATA
BEGIN
	IF EXISTS (SELECT 1 FROM KitDescriptor WHERE kitDescriptorId = id AND name = kitName AND manufacturer = kitManufacturer
	AND partNumber LIKE kitPartNumber AND kitType = kitKitType AND platformType = kitPlatformType)
    THEN
        IF ISNULL(kitVersion)
        THEN
            IF EXISTS (SELECT 1 FROM KitDescriptor WHERE kitDescriptorId = id AND version IS NULL)
            THEN
                RETURN true;
            ELSE
                RETURN false;
            END IF;
        ELSE
            IF EXISTS (SELECT 1 FROM KitDescriptor WHERE kitDescriptorId = id AND version = kitVersion)
            THEN
                RETURN true;
            ELSE
                RETURN false;
            END IF;
        END IF;
    END IF;
    RETURN false;
END//


DROP PROCEDURE IF EXISTS removeDupes//
CREATE PROCEDURE removeDupes (id1 bigint(20), id2 bigint(20), kitName varchar(255), kitVersion int(3), kitManufacturer varchar(100),
    kitPartNumber varchar(50), kitKitType varchar(30), kitPlatformType varchar(20))
BEGIN
	IF kitExists(id1, kitName, kitVersion, kitManufacturer, kitPartNumber, kitKitType, kitPlatformType)
	AND kitExists(id2, kitName, kitVersion, kitManufacturer, kitPartNumber, kitKitType, kitPlatformType)
	THEN
	   -- add id1 stockLevel to id2
        SELECT stockLevel INTO @dupeStock FROM KitDescriptor WHERE kitDescriptorId = id2;
        UPDATE KitDescriptor SET stockLevel = (stockLevel + @dupeStock) WHERE kitDescriptorId = id1;
        -- update foreign keys to reference id1
        UPDATE Kit SET kitDescriptorId = id1 WHERE kitDescriptorId = id2;
        UPDATE LibraryAdditionalInfo SET kitDescriptorId = id1 WHERE kitDescriptorId = id2;
        UPDATE TargetedResequencing SET kitDescriptorId = id1 WHERE kitDescriptorId = id2;
        -- delete id2
        DELETE FROM KitDescriptor WHERE kitDescriptorId = id2;
	END IF;
END//
DELIMITER ;

call removeDupes(10, 14, 'GS Titanium emPCR Bead Recovery Rgt', 1, 'Roche', '05233569001', 'EmPCR', 'LS454');

call removeDupes(18, 22, 'GS Titanium emPCR Bead Recovery Rgt', 2, 'Roche', '05233569001', 'EmPCR', 'LS454');
call removeDupes(18, 26, 'GS Titanium emPCR Bead Recovery Rgt', 2, 'Roche', '05233569001', 'EmPCR', 'LS454');
call removeDupes(22, 26, 'GS Titanium emPCR Bead Recovery Rgt', 2, 'Roche', '05233569001', 'EmPCR', 'LS454');

call removeDupes(19, 23, 'GS Titanium emPCR Reagents (Lib-L)', 2, 'Roche', '05233585001', 'EmPCR', 'LS454');
call removeDupes(19, 27, 'GS Titanium emPCR Reagents (Lib-L)', 2, 'Roche', '05233585001', 'EmPCR', 'LS454');
call removeDupes(23, 27, 'GS Titanium emPCR Reagents (Lib-L)', 2, 'Roche', '05233585001', 'EmPCR', 'LS454');

call removeDupes(65, 102, 'SOLiD ePCR Tubes and Caps', NULL, 'ABI', '4400401', 'EmPCR', 'Solid');

-- Add 'v1' or 'v2' to name when there are multiple versions
UPDATE KitDescriptor SET name = 'GS Titanium emPCR Bead Recovery Rgt V1' WHERE name = 'GS Titanium emPCR Bead Recovery Rgt' AND version = 1;
UPDATE KitDescriptor SET name = 'GS Titanium emPCR Bead Recovery Rgt V2' WHERE name = 'GS Titanium emPCR Bead Recovery Rgt' AND version = 2;
UPDATE KitDescriptor SET name = 'GS Titanium emPCR Emulsion Oil LV V1' WHERE name = 'GS Titanium emPCR Emulsion Oil LV' AND version = 1;
UPDATE KitDescriptor SET name = 'GS Titanium emPCR Emulsion Oil LV V2' WHERE name = 'GS Titanium emPCR Emulsion Oil LV' AND version = 2;
UPDATE KitDescriptor SET name = 'GS Titanium emPCR Emulsion Oil SV V1' WHERE name = 'GS Titanium emPCR Emulsion Oil SV' AND version = 1;
UPDATE KitDescriptor SET name = 'GS Titanium emPCR Emulsion Oil SV V2' WHERE name = 'GS Titanium emPCR Emulsion Oil SV' AND version = 2;
UPDATE KitDescriptor SET name = 'GS Titanium LV emPCR Kit (Lib-L) V1' WHERE name = 'GS Titanium LV emPCR Kit (Lib-L)' AND version = 1;
UPDATE KitDescriptor SET name = 'GS Titanium LV emPCR Kit (Lib-L) V2' WHERE name = 'GS Titanium LV emPCR Kit (Lib-L)' AND version = 2;
UPDATE KitDescriptor SET name = 'GS Titanium SV emPCR Kit (Lib-L) V1' WHERE name = 'GS Titanium SV emPCR Kit (Lib-L)' AND version = 1;
UPDATE KitDescriptor SET name = 'GS Titanium SV emPCR Kit (Lib-L) V2' WHERE name = 'GS Titanium SV emPCR Kit (Lib-L)' AND version = 2;

-- Other near-dupes in V0002 with different partNumbers
-- Ones ending in '02' (id2, kitPartNumber2) are discontinued - delete if unused
DELIMITER //
DROP PROCEDURE IF EXISTS removeNearDupes//
CREATE PROCEDURE removeNearDupes(id1 bigint(20), id2 bigint(20), kitName varchar(255), kitVersion int(3), kitManufacturer varchar(100),
    kitPartNumber1 varchar(50), kitPartNumber2 varchar(50), kitKitType varchar(30), kitPlatformType varchar(20))
BEGIN
	IF kitExists(id1, kitName, kitVersion, kitManufacturer, kitPartNumber1, kitKitType, kitPlatformType)
	AND kitExists(id2, kitName, kitVersion, kitManufacturer, kitPartNumber2, kitKitType, kitPlatformType)
	AND NOT EXISTS (SELECT 1 FROM Kit WHERE kitDescriptorId = id2)
	AND NOT EXISTS (SELECT 1 FROM LibraryAdditionalInfo WHERE kitDescriptorId = id2)
	AND NOT EXISTS (SELECT 1 FROM TargetedResequencing WHERE kitDescriptorId = id2)
	THEN
	    call removeDupes(id1, id2, kitName, kitVersion, kitManufacturer, '%', kitKitType, kitPlatformType);
	END IF;
END//
DELIMITER ;

call removeNearDupes(55, 56,'RiboMinus Eukaryotic Kit for RNA-Seq',NULL,'INVITROGEN','A1083708','A1083702','Library','Solid');
call removeNearDupes(57, 58,'RiboMinus Plant Kit for RNA-Seq',NULL,'INVITROGEN','A1083808','A1083802','Library','Solid');

DROP PROCEDURE removeNearDupes;
DROP PROCEDURE removeDupes;
DROP FUNCTION kitExists;

-- add constraint
ALTER TABLE KitDescriptor ADD CONSTRAINT uk_kitDescriptor_name UNIQUE (name);

-- EndNoTest


