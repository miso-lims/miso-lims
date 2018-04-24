CREATE TABLE StepSequencerPartitionContainer (
  workflowProgressId BIGINT(20) NOT NULL,
  stepNumber         BIGINT(20) NOT NULL,
  containerId        BIGINT(20) NOT NULL,
  PRIMARY KEY (workflowProgressId, stepNumber),
  FOREIGN KEY (workflowProgressId, stepNumber) REFERENCES WorkflowProgressStep (workflowProgressId, stepNumber)
)
  ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

CREATE TABLE StepSequencingContainerModel (
  workflowProgressId         BIGINT(20) NOT NULL,
  stepNumber                 BIGINT(20) NOT NULL,
  sequencingContainerModelId BIGINT(20) NOT NULL,
  PRIMARY KEY (workflowProgressId, stepNumber),
  FOREIGN KEY (workflowProgressId, stepNumber) REFERENCES WorkflowProgressStep (workflowProgressId, stepNumber)
)
  ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

CREATE TABLE StepString (
  workflowProgressId BIGINT(20)  NOT NULL,
  stepNumber         BIGINT(20)  NOT NULL,
  input              varchar(20) NOT NULL,
  PRIMARY KEY (workflowProgressId, stepNumber),
  FOREIGN KEY (workflowProgressId, stepNumber) REFERENCES WorkflowProgressStep (workflowProgressId, stepNumber)
)
  ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

CREATE TABLE StepSkip (
  workflowProgressId BIGINT(20)  NOT NULL,
  stepNumber         BIGINT(20)  NOT NULL,
  PRIMARY KEY (workflowProgressId, stepNumber),
  FOREIGN KEY (workflowProgressId, stepNumber) REFERENCES WorkflowProgressStep (workflowProgressId, stepNumber)
)
  ENGINE = InnoDB
  DEFAULT CHARSET = utf8;
