USE `lims` ;

CREATE TABLE IF NOT EXISTS `lims`.`Attached_Elements` (
  `attachableId` BIGINT(20) NOT NULL,
  `attachableEntityType` VARCHAR(255) NOT NULL,
  `attachedId` BIGINT(20) NOT NULL,
  `attachedEntityType` VARCHAR(255) NOT NULL,
  PRIMARY KEY (`attachableId`, `attachableEntityType`, `attachedId`, `attachedEntityType`))
ENGINE = MyISAM;