SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='TRADITIONAL,ALLOW_INVALID_DATES';

USE `lims` ;

-- -----------------------------------------------------
-- Table `lims`.`Workflow`
-- -----------------------------------------------------
ALTER TABLE `lims`.`Workflow`
  ADD COLUMN `alias` VARCHAR(100) NULL,
  ADD COLUMN `status` VARCHAR(50) NOT NULL;

-- -----------------------------------------------------
-- Table `lims`.`WorkflowDefinition_WorkflowProcessDefinition`
-- -----------------------------------------------------
ALTER TABLE `lims`.`WorkflowDefinition_WorkflowProcessDefinition` 
  ADD COLUMN `order` TINYINT NOT NULL;


SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
