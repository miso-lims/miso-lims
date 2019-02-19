-- StartNoTest
DELIMITER //

DROP PROCEDURE IF EXISTS addReferenceGenome//
CREATE PROCEDURE addReferenceGenome(
  iAlias varchar(255),
  iDefaultSciName varchar(255)
) BEGIN
  IF NOT EXISTS (SELECT 1 FROM ReferenceGenome WHERE alias = iAlias) THEN
    INSERT INTO ReferenceGenome(alias, defaultSciName)
    VALUES (iAlias, iDefaultSciName);
  END IF;
END//

DELIMITER ;
-- EndNoTest
