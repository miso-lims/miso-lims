DROP TABLE IF EXISTS `Kit`;
DROP TABLE IF EXISTS `KitComponent`;
CREATE TABLE `KitComponent` (
  `kitComponentId` bigint(20) NOT NULL AUTO_INCREMENT,
  `identificationBarcode` varchar(255) DEFAULT NULL,
  `locationBarcode` varchar(255) DEFAULT NULL,
  `lotNumber` varchar(30) NOT NULL,
  `kitReceivedDate` date NOT NULL,
  `kitExpiryDate` date NOT NULL,
  `exhausted` tinyint(1),
  `kitComponentDescriptorId` bigint(20) NOT NULL,
  PRIMARY KEY (`kitComponentId`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `KitComponentDescriptor`;
CREATE TABLE `KitComponentDescriptor` (
  `kitComponentDescriptorId` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  `referenceNumber` varchar(50) NOT NULL,
  `kitDescriptorId` bigint(20) NOT NULL,
  PRIMARY KEY (`kitComponentDescriptorId`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

ALTER TABLE `KitDescriptor` ADD `units` varchar(20) NOT NULL;
ALTER TABLE `KitDescriptor` ADD `kitValue` DECIMAL NOT NULL;

ALTER TABLE `Experiment_Kit` CHANGE `kits_kitId` `kitComponents_kitComponentId` bigint(20);

