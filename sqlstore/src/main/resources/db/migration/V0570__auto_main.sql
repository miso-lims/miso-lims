-- Samples_Recieved_Workflow

CREATE TABLE StepPositiveDouble (
  workflowProgressId bigint NOT NULL,
  stepNumber         bigint NOT NULL,
  `input`             FLOAT NOT NULL,
  PRIMARY KEY (workflowProgressId, stepNumber),
  CONSTRAINT `fk_StepPositiveDouble_step` FOREIGN KEY (workflowProgressId, stepNumber) REFERENCES WorkflowProgressStep (workflowProgressId, stepNumber)
)
  ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;

CREATE TABLE StepPositiveInteger (
  workflowProgressId bigint NOT NULL,
  stepNumber         bigint NOT NULL,
  `input`             INT unsigned NOT NULL,
  PRIMARY KEY (workflowProgressId, stepNumber),
  CONSTRAINT `fk_StepPositiveInteger_step` FOREIGN KEY (workflowProgressId, stepNumber) REFERENCES WorkflowProgressStep (workflowProgressId, stepNumber)
)
  ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;

CREATE TABLE StepBox (
  workflowProgressId bigint NOT NULL,
  stepNumber         bigint NOT NULL,
  `boxId`           bigint NOT NULL,
  PRIMARY KEY (workflowProgressId, stepNumber),
  CONSTRAINT `fk_StepBox_Step` FOREIGN KEY (workflowProgressId, stepNumber) REFERENCES WorkflowProgressStep (workflowProgressId, stepNumber),
  CONSTRAINT `fk_StepBox_Box` FOREIGN KEY (boxId) REFERENCES Box (boxId)
)
  ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;

CREATE TABLE StepBoxPosition (
  workflowProgressId bigint  NOT NULL,
  stepNumber         bigint  NOT NULL,
  `input`             varchar(20) NOT NULL,
  PRIMARY KEY (workflowProgressId, stepNumber),
  CONSTRAINT `fk_StepBoxPosition_step` FOREIGN KEY (workflowProgressId, stepNumber) REFERENCES WorkflowProgressStep (workflowProgressId, stepNumber)
)
  ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;


-- Template_Default_Volume

ALTER TABLE LibraryTemplate ADD COLUMN `defaultVolume` double DEFAULT NULL AFTER projectId;


-- Optional_Dilution_Concentration

ALTER TABLE LibraryDilution MODIFY COLUMN concentration double;


-- Optional_Pool_Concentration

ALTER TABLE `Pool` MODIFY COLUMN `concentration` double DEFAULT NULL;


