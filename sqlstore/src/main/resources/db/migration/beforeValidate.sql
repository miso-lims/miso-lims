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
    -- V1210 was altered for compatibility with mariadb 10.2.41
    UPDATE flyway_schema_history SET checksum = 677713310 WHERE version = '0320';
    -- These were altered for the upgrade from MySQL 5.7 to 8.0
    UPDATE flyway_schema_history SET checksum = -1280024109 WHERE version = '0004';
    UPDATE flyway_schema_history SET checksum = -1106008266 WHERE version = '0110';
    UPDATE flyway_schema_history SET checksum = 702230053 WHERE version = '0170';
    UPDATE flyway_schema_history SET checksum = -1982688821 WHERE version = '0490';
    UPDATE flyway_schema_history SET checksum = 2057297132 WHERE version = '1000';
  END IF;
END//

DELIMITER ;

CALL updateMigrationChecksums();
DROP PROCEDURE updateMigrationChecksums;
