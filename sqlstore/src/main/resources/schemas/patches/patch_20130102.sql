USE lims;

CREATE TABLE `Plate_Elements` (
  `plate_plateId` bigint(20) NOT NULL,
  `elementType` VARCHAR(255) NOT NULL,
  `elementPosition` INT NOT NULL,
  `elementId` bigint(20) NOT NULL,
  PRIMARY KEY  (`plate_plateId`,`elementId`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;