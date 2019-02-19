-- novaseq_workflow_type

ALTER TABLE RunIllumina ADD COLUMN workflowType varchar(50);


-- change_user_column_nulls

ALTER TABLE User CHANGE COLUMN fullName fullName varchar(255) NOT NULL;
ALTER TABLE User CHANGE COLUMN loginName loginName varchar(255) NOT NULL;


