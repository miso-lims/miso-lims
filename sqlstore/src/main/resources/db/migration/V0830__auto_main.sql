-- unique_container_serialnum
ALTER TABLE SequencerPartitionContainer ADD CONSTRAINT uk_container_identificationBarcode UNIQUE (identificationBarcode);

-- plate_change_log
DROP TABLE PlateChangeLog;

-- fix_login
DROP TABLE IF EXISTS `persistent_logins`;

CREATE TABLE `persistent_logins` (
  `username` varchar(64) NOT NULL,
  `series` varchar(64) NOT NULL,
  `token` varchar(64) NOT NULL,
  `last_used` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`series`)
) ENGINE = InnoDB ROW_FORMAT = DEFAULT;

