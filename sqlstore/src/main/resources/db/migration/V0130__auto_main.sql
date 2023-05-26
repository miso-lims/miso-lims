-- libraryDesignCode_changes

CREATE TABLE `LibraryDesignCode` (
  `libraryDesignCodeId` bigint NOT NULL AUTO_INCREMENT,
  `code` VARCHAR(2) NOT NULL,
  `description` VARCHAR(255) NOT NULL,
  PRIMARY KEY (`libraryDesignCodeId`),
  UNIQUE KEY `libraryDesignCode_unique` (`code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- add current LibraryDesignCode values into the table
INSERT INTO LibraryDesignCode (code, description) SELECT DISTINCT SUBSTRING(name, 1,2), SUBSTRING(name, 1,2) FROM LibraryDesign;

ALTER TABLE LibraryDesign DROP FOREIGN KEY `FK_ld_lt`;
ALTER TABLE LibraryDesign DROP COLUMN libraryType;
ALTER TABLE LibraryAdditionalInfo ADD COLUMN `libraryDesignCodeId` bigint;
ALTER TABLE LibraryAdditionalInfo ADD CONSTRAINT `FK_lai_libraryDesignCode_libraryDesignCodeId` FOREIGN KEY (`libraryDesignCodeId`) REFERENCES `LibraryDesignCode` (`libraryDesignCodeId`);
ALTER TABLE LibraryDesign ADD COLUMN `libraryDesignCodeId` bigint;
ALTER TABLE LibraryDesign ADD CONSTRAINT `FK_ld_libraryDesignCode_libraryDesignCodeId` FOREIGN KEY (`libraryDesignCodeId`) REFERENCES `LibraryDesignCode` (`libraryDesignCodeId`);

ALTER TABLE LibraryDesign DROP INDEX `uk_libraryDesign_name`;
ALTER TABLE LibraryDesign ADD CONSTRAINT `uk_libraryDesign_name_sampleClass` UNIQUE (`name`, `sampleClassId`); 

-- fill in the libraryDesignCode values before making the column non-null
DELIMITER //
CREATE PROCEDURE update_libraryDesignCode ()
BEGIN
  DECLARE v_finished INT DEFAULT 0;
  DECLARE v_ldcId bigint;

  DECLARE updateLibraryDesignCodeCursor CURSOR FOR 
    SELECT libraryDesignCodeId FROM LibraryDesignCode;

  DECLARE CONTINUE HANDLER FOR NOT FOUND SET v_finished = 1;

  OPEN updateLibraryDesignCodeCursor;
    update_LDC: LOOP
      FETCH updateLibraryDesignCodeCursor INTO v_ldcId;
      IF v_finished = 1 THEN LEAVE update_LDC; END IF;

  	    SELECT code INTO @code FROM LibraryDesignCode WHERE libraryDesignCodeId = v_ldcId;
	    UPDATE LibraryDesign SET libraryDesignCodeId = v_ldcId WHERE SUBSTRING(name, 1, 2) = @code;
  	    -- infer libraryDesignCodeId from alias
	    UPDATE LibraryAdditionalInfo SET libraryDesignCodeId = v_ldcId WHERE libraryId IN (SELECT libraryId FROM Library WHERE RIGHT(alias, 2) = @code);
	    -- overwrite this value when libraryDesign is actually set. Note: There may be more than one library design with the same code.
	    UPDATE LibraryAdditionalInfo SET libraryDesignCodeId = v_ldcId WHERE libraryDesign IN (SELECT libraryDesignId FROM LibraryDesign WHERE SUBSTRING(name, 1, 2) = @code);
    END LOOP update_LDC;
  CLOSE updateLibraryDesignCodeCursor;

END//
DELIMITER ;
CALL update_libraryDesignCode();
DROP PROCEDURE IF EXISTS update_libraryDesignCode;

ALTER TABLE LibraryAdditionalInfo MODIFY COLUMN `libraryDesignCodeId` bigint NOT NULL;
ALTER TABLE LibraryDesign MODIFY COLUMN `libraryDesignCodeId` bigint NOT NULL;
ALTER TABLE LibraryDesign DROP COLUMN `suffix`;


