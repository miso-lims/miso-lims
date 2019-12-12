-- Update migration checksums. This should only be done when altering a previous migration cannot be avoided

-- V0800 was altered to remove use of a stored procedure that no longer exists
UPDATE flyway_schema_history SET checksum = 1064873732 WHERE version = '0800' AND checksum = -1786810864;
