USE `lims` ;

CREATE TABLE IF NOT EXISTS `lims`.`Assignee` (
  `entityName` VARCHAR(45) NOT NULL,
  `userId` BIGINT NOT NULL,
  PRIMARY KEY (`entityName`))
ENGINE = MyISAM;