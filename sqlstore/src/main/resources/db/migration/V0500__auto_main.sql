-- stepInteger

CREATE TABLE StepInteger (
  workflowProgressId bigint NOT NULL,
  stepNumber         bigint NOT NULL,
  `input`             INT NOT NULL,
  PRIMARY KEY (workflowProgressId, stepNumber),
  FOREIGN KEY (workflowProgressId, stepNumber) REFERENCES WorkflowProgressStep (workflowProgressId, stepNumber)
)
  ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;


