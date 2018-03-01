CREATE TABLE WorkflowProgress (
  workflowProgressId BIGINT(20)   NOT NULL AUTO_INCREMENT,
  workflowName       VARCHAR(255) NOT NULL,
  userId             BIGINT(20)   NOT NULL,
  created            TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
  lastModified       TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (workflowProgressId),
  FOREIGN KEY (userId) REFERENCES User (userId)
)
  ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

CREATE TABLE WorkflowProgressStep (
  workflowProgressId BIGINT(20) NOT NULL,
  stepNumber         BIGINT(20) NOT NULL,
  PRIMARY KEY (workflowProgressId, stepNumber),
  FOREIGN KEY (workflowProgressId) REFERENCES WorkflowProgress (workflowProgressId)
)
  ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

CREATE TABLE StepSample (
  workflowProgressId BIGINT(20) NOT NULL,
  stepNumber         BIGINT(20) NOT NULL,
  sampleId           BIGINT(20) NOT NULL,
  PRIMARY KEY (workflowProgressId, stepNumber),
  FOREIGN KEY (workflowProgressId, stepNumber) REFERENCES WorkflowProgressStep (workflowProgressId, stepNumber),
  FOREIGN KEY (sampleId) REFERENCES Sample (sampleId)
)
  ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

CREATE TABLE StepPool (
  workflowProgressId BIGINT(20) NOT NULL,
  stepNumber         BIGINT(20) NOT NULL,
  poolId             BIGINT(20) NOT NULL,
  PRIMARY KEY (workflowProgressId, stepNumber),
  FOREIGN KEY (workflowProgressId, stepNumber) REFERENCES WorkflowProgressStep (workflowProgressId, stepNumber),
  FOREIGN KEY (poolId) REFERENCES Pool (poolId)
)
  ENGINE = InnoDB
  DEFAULT CHARSET = utf8;
