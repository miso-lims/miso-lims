-- unique_shortname
ALTER TABLE Project ADD CONSTRAINT uk_project_shortname UNIQUE (shortName);

-- unique_constraints
ALTER TABLE Run_SequencerPartitionContainer ADD CONSTRAINT uk_run_position UNIQUE (Run_runId, positionId);
ALTER TABLE User ADD CONSTRAINT uk_user_loginname UNIQUE (loginName);
ALTER TABLE _Group ADD CONSTRAINT uk_group_name UNIQUE (name);

