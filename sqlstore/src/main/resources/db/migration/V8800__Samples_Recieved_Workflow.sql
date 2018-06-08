CREATE TABLE StepPositiveDouble (
  workflowProgressId BIGINT(20) NOT NULL,
  stepNumber         BIGINT(20) NOT NULL,
  `input`             FLOAT unsigned NOT NULL,
  PRIMARY KEY (workflowProgressId, stepNumber),
  FOREIGN KEY (workflowProgressId, stepNumber) REFERENCES WorkflowProgressStep (workflowProgressId, stepNumber)
)
  ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

CREATE TABLE StepPositiveInteger (
  workflowProgressId BIGINT(20) NOT NULL,
  stepNumber         BIGINT(20) NOT NULL,
  `input`             INT unsigned NOT NULL,
  PRIMARY KEY (workflowProgressId, stepNumber),
  FOREIGN KEY (workflowProgressId, stepNumber) REFERENCES WorkflowProgressStep (workflowProgressId, stepNumber)
)
  ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

CREATE TABLE StepSampleStock (
  workflowProgressId BIGINT(20) NOT NULL,
  stepNumber         BIGINT(20) NOT NULL,
  sampleId           BIGINT(20) NOT NULL,
  PRIMARY KEY (workflowProgressId, stepNumber),
  FOREIGN KEY (workflowProgressId, stepNumber) REFERENCES WorkflowProgressStep (workflowProgressId, stepNumber),
  FOREIGN KEY (sampleId) REFERENCES Sample (sampleId)
)
  ENGINE = InnoDB
  DEFAULT CHARSET = utf8;
