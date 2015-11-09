SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='TRADITIONAL,ALLOW_INVALID_DATES';

USE `lims` ;

-- -----------------------------------------------------
-- Table `lims`.`Workflow`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `lims`.`Workflow` (
  `workflowId` BIGINT NOT NULL auto_increment,
  `alias` VARCHAR(100) NULL,
  `userId` BIGINT NULL,
  `start_date` DATE NULL,
  `completion_date` DATE NULL,
  `status` VARCHAR(50) NOT NULL,
  `workflowDefinition_definitionId` BIGINT NOT NULL,
  PRIMARY KEY (`workflowId`))
ENGINE = MyISAM AUTO_INCREMENT=1;


-- -----------------------------------------------------
-- Table `lims`.`WorkflowProcess`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `lims`.`WorkflowProcess` (
  `processId` BIGINT NOT NULL auto_increment,
  `userId` BIGINT NULL,
  `start_date` DATE NULL,
  `completion_date` DATE NULL,
  `workflowProcessDefinition_definitionId` BIGINT NOT NULL,
  PRIMARY KEY (`processId`))
ENGINE = MyISAM AUTO_INCREMENT=1;


-- -----------------------------------------------------
-- Table `lims`.`Workflow_WorkflowProcess`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `lims`.`Workflow_WorkflowProcess` (
  `workflowId` BIGINT NOT NULL auto_increment,
  `processId` BIGINT NOT NULL,
  PRIMARY KEY (`workflowId`, `processId`))
ENGINE = MyISAM AUTO_INCREMENT=1;


-- -----------------------------------------------------
-- Table `lims`.`Workflow_State`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `lims`.`Workflow_State` (
  `workflowId` BIGINT NOT NULL auto_increment,
  `state_key_id` BIGINT NOT NULL,
  `state_value_id` BIGINT NOT NULL,
  PRIMARY KEY (`workflowId`, `state_key_id`, `state_value_id`))
ENGINE = MyISAM AUTO_INCREMENT=1;


-- -----------------------------------------------------
-- Table `lims`.`WorkflowProcess_State`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `lims`.`WorkflowProcess_State` (
  `processId` BIGINT NOT NULL auto_increment,
  `state_key_id` BIGINT NOT NULL,
  `state_value_id` BIGINT NOT NULL,
  PRIMARY KEY (`processId`, `state_value_id`, `state_key_id`))
ENGINE = MyISAM AUTO_INCREMENT=1;


-- -----------------------------------------------------
-- Table `lims`.`State_Key`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `lims`.`State_Key` (
  `id` BIGINT NOT NULL auto_increment,
  `value` VARCHAR(50) NULL,
  PRIMARY KEY (`id`, `value`))
ENGINE = MyISAM AUTO_INCREMENT=1;


-- -----------------------------------------------------
-- Table `lims`.`State_Value`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `lims`.`State_Value` (
  `id` BIGINT NOT NULL auto_increment,
  `value` TEXT NULL,
  PRIMARY KEY (`id`))
ENGINE = MyISAM AUTO_INCREMENT=1;


-- -----------------------------------------------------
-- Table `lims`.`WorkflowDefinition`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `lims`.`WorkflowDefinition` (
  `workflowDefinitionId` BIGINT NOT NULL auto_increment,
  `userId` BIGINT NULL,
  `creation_date` DATE NULL,
  `name` VARCHAR(255) NULL,
  `description` TEXT NULL,
  PRIMARY KEY (`workflowDefinitionId`))
ENGINE = MyISAM AUTO_INCREMENT=1;


-- -----------------------------------------------------
-- Table `lims`.`WorkflowProcessDefinition`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `lims`.`WorkflowProcessDefinition` (
  `workflowProcessDefinitionId` BIGINT NOT NULL auto_increment,
  `userId` BIGINT NULL,
  `creation_date` DATE NULL,
  `name` VARCHAR(255) NULL,
  `description` TEXT NULL,
  `inputType` TEXT NULL,
  `outputType` TEXT NULL,
  PRIMARY KEY (`workflowProcessDefinitionId`))
ENGINE = MyISAM AUTO_INCREMENT=1;


-- -----------------------------------------------------
-- Table `lims`.`WorkflowProcessDefinition_State`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `lims`.`WorkflowProcessDefinition_State` (
  `workflowProcessDefinitionId` BIGINT NOT NULL auto_increment,
  `state_key_id` BIGINT NULL,
  `required` TINYINT(1) NULL,
  PRIMARY KEY (`workflowProcessDefinitionId`, `state_key_id`))
ENGINE = MyISAM AUTO_INCREMENT=1;


-- -----------------------------------------------------
-- Table `lims`.`WorkflowDefinition_State`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `lims`.`WorkflowDefinition_State` (
  `workflowDefinitionId` BIGINT NOT NULL auto_increment,
  `state_key_id` BIGINT NULL,
  `required` TINYINT(1) NULL,
  PRIMARY KEY (`workflowDefinitionId`, `state_key_id`))
ENGINE = MyISAM AUTO_INCREMENT=1;


-- -----------------------------------------------------
-- Table `lims`.`WorkflowDefinition_WorkflowProcessDefinition`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `lims`.`WorkflowDefinition_WorkflowProcessDefinition` (
  `workflowDefinitionId` BIGINT NOT NULL auto_increment,
  `workflowProcessDefinitionId` BIGINT NOT NULL,
  `order` TINYINT NOT NULL,
  PRIMARY KEY (`workflowDefinitionId`, `workflowProcessDefinitionId`))
ENGINE = MyISAM AUTO_INCREMENT=1;


SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
