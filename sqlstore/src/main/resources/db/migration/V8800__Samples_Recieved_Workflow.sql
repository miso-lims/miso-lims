CREATE TABLE StepPositiveDouble (
  workflowProgressId BIGINT(20) NOT NULL,
  stepNumber         BIGINT(20) NOT NULL,
  `input`             FLOAT unsigned NOT NULL,
  PRIMARY KEY (workflowProgressId, stepNumber),
  CONSTRAINT `WorkflowProgressStep_fk1` FOREIGN KEY (workflowProgressId, stepNumber) REFERENCES WorkflowProgressStep (workflowProgressId, stepNumber)
)
  ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

CREATE TABLE StepPositiveInteger (
  workflowProgressId BIGINT(20) NOT NULL,
  stepNumber         BIGINT(20) NOT NULL,
  `input`             INT unsigned NOT NULL,
  PRIMARY KEY (workflowProgressId, stepNumber),
  CONSTRAINT `WorkflowProgressStep_fk2` FOREIGN KEY (workflowProgressId, stepNumber) REFERENCES WorkflowProgressStep (workflowProgressId, stepNumber)
)
  ENGINE = InnoDB
  DEFAULT CHARSET = utf8;
