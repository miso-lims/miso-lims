-- workflow_progress_steps

CREATE TABLE StepSequencerPartitionContainer (
  workflowProgressId bigint NOT NULL,
  stepNumber         bigint NOT NULL,
  containerId        bigint NOT NULL,
  PRIMARY KEY (workflowProgressId, stepNumber),
  CONSTRAINT fk_StepSequencerPartitionContainer_step FOREIGN KEY (workflowProgressId, stepNumber) REFERENCES WorkflowProgressStep (workflowProgressId, stepNumber),
  CONSTRAINT fk_StepSequencerPartitionContainer_container FOREIGN KEY (containerId) REFERENCES SequencerPartitionContainer (containerId)
)
  ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

CREATE TABLE StepSequencingContainerModel (
  workflowProgressId         bigint NOT NULL,
  stepNumber                 bigint NOT NULL,
  sequencingContainerModelId bigint NOT NULL,
  PRIMARY KEY (workflowProgressId, stepNumber),
  CONSTRAINT fk_StepSequencingContainerModel_step FOREIGN KEY (workflowProgressId, stepNumber) REFERENCES WorkflowProgressStep (workflowProgressId, stepNumber),
  CONSTRAINT fk_StepSequencingContainerModel_model FOREIGN KEY (sequencingContainerModelId) REFERENCES SequencingContainerModel (sequencingContainerModelId)
)
  ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

CREATE TABLE StepString (
  workflowProgressId bigint  NOT NULL,
  stepNumber         bigint  NOT NULL,
  input              varchar(20) NOT NULL,
  PRIMARY KEY (workflowProgressId, stepNumber),
  CONSTRAINT fk_StepString_step FOREIGN KEY (workflowProgressId, stepNumber) REFERENCES WorkflowProgressStep (workflowProgressId, stepNumber)
)
  ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

CREATE TABLE StepSkip (
  workflowProgressId bigint  NOT NULL,
  stepNumber         bigint  NOT NULL,
  PRIMARY KEY (workflowProgressId, stepNumber),
  CONSTRAINT fk_StepSkip_step FOREIGN KEY (workflowProgressId, stepNumber) REFERENCES WorkflowProgressStep (workflowProgressId, stepNumber)
)
  ENGINE = InnoDB
  DEFAULT CHARSET = utf8;


-- add_index_family_fake

ALTER TABLE IndexFamily ADD COLUMN fakeSequence BOOLEAN NOT NULL DEFAULT FALSE;


