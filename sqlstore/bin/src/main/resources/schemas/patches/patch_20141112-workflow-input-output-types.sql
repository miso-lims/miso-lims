USE lims;

ALTER TABLE lims.WorkflowProcessDefinition ADD COLUMN inputType TEXT NULL;
ALTER TABLE lims.WorkflowProcessDefinition ADD COLUMN outputType TEXT NULL;