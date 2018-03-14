-- Add column that already existed in OICR branch

-- StartNoTest
DELIMITER //

CREATE PROCEDURE tempAddColumn() BEGIN
  -- ignore failure due to column already existing
  DECLARE CONTINUE HANDLER FOR SQLSTATE '42S21' BEGIN END;
-- EndNoTest
  ALTER TABLE TissueType ADD COLUMN sampleTypeName varchar(255);
-- StartNoTest
END//

DELIMITER ;

CALL tempAddColumn;
DROP PROCEDURE tempAddColumn;
-- EndNoTest

-- Add missing contraints
ALTER TABLE Library_Index ADD CONSTRAINT fk_libraryIndex_library FOREIGN KEY (library_libraryId) REFERENCES Library (libraryId);
ALTER TABLE Library_Index ADD CONSTRAINT fk_libraryIndex_index FOREIGN KEY (index_indexId) REFERENCES Indices (indexId);
