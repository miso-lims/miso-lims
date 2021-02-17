-- Update migration checksums. This should only be done when altering a previous migration cannot be avoided

SET sql_notes = 0;
DROP PROCEDURE IF EXISTS updateMigrationChecksums;
SET sql_notes = 1;

DELIMITER //

CREATE PROCEDURE updateMigrationChecksums()
BEGIN
  IF EXISTS (SELECT 1 FROM information_schema.tables WHERE table_schema = (SELECT DATABASE()) AND table_name = 'flyway_schema_history') THEN
    -- V0800 was altered to remove use of a stored procedure that no longer exists
    UPDATE flyway_schema_history SET checksum = 1064873732 WHERE version = '0800';
    -- V1000 was altered to fix an infinite loop when running on plain sample databases
    UPDATE flyway_schema_history SET checksum = 330422399 WHERE version = '1000';
    -- V1060 was altered to fix an error migrating tissue processing classes with no subcategory
    UPDATE flyway_schema_history SET checksum = -1464362715 WHERE version = '1060';
    -- V1210 was altered for compatibility between mysql and mariadb
    UPDATE flyway_schema_history SET checksum = 1607781079 WHERE version = '1210';
    /* V1000, 1060, 1100, and 1200 were updated to remove sql_note toggling
       as it is now disabled for the entire migration */
    UPDATE flyway_schema_history SET checksum = -159086106 WHERE version = '1000';
    UPDATE flyway_schema_history SET checksum = 1248674807 WHERE version = '1060';
    UPDATE flyway_schema_history SET checksum = 1770487871 WHERE version = '1100';
    UPDATE flyway_schema_history SET checksum = 876069334 WHERE version = '1200';
  END IF;
END//

DELIMITER ;

CALL updateMigrationChecksums();
DROP PROCEDURE updateMigrationChecksums;
