-- unique_lab
ALTER TABLE Lab ADD CONSTRAINT uk_lab_alias UNIQUE (alias);

-- alter_subproject
DROP INDEX UK_n88wwxd7kv4q0m2xhfek9xl70 ON Subproject;
ALTER TABLE Subproject ADD UNIQUE uk_subproject_project_alias (projectId, alias);

-- timepoint
ALTER TABLE Sample ADD COLUMN timepoint varchar(50);

