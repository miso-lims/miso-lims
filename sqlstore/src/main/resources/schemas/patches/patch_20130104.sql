
DROP TABLE IF EXISTS `Pool_Elements`;

CREATE TABLE `Pool_Elements` (
  `pool_poolId` bigint(20) NOT NULL,
  `elementType` VARCHAR(255) NOT NULL,
  `elementId` bigint(20) NOT NULL,
  PRIMARY KEY  (`pool_poolId`,`elementId`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

INSERT INTO Pool_Elements(pool_poolId, elementType, elementId)
SELECT pool_poolId, CONCAT("uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryDilution"), dilutions_dilutionId FROM Pool_LibraryDilution;

INSERT INTO Pool_Elements(pool_poolId, elementType, elementId)
SELECT pool_poolId, CONCAT("uk.ac.bbsrc.tgac.miso.core.data.impl.emPCRDilution"), dilutions_dilutionId FROM Pool_emPCRDilution;
