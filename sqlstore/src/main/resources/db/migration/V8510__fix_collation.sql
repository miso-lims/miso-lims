-- StartNoTest
ALTER DATABASE CHARACTER SET utf8 COLLATE 'utf8_general_ci';

SET foreign_key_checks = 0;
ALTER TABLE BoxPosition CONVERT TO CHARACTER SET utf8 COLLATE utf8_general_ci;
ALTER TABLE ProjectOverview_Sample CONVERT TO CHARACTER SET utf8 COLLATE utf8_general_ci;
SET foreign_key_checks = 1;
-- EndNoTest

DROP TABLE IF EXISTS Workflow;
DROP TABLE IF EXISTS WorkflowDefinition;
DROP TABLE IF EXISTS WorkflowDefinition_State;
DROP TABLE IF EXISTS WorkflowDefinition_WorkflowProcessDefinition;
DROP TABLE IF EXISTS WorkflowProcess;
DROP TABLE IF EXISTS WorkflowProcessDefinition;
DROP TABLE IF EXISTS WorkflowProcessDefinition_State;
DROP TABLE IF EXISTS WorkflowProcess_State;
DROP TABLE IF EXISTS Workflow_WorkflowProcess;
