DROP INDEX UK_n88wwxd7kv4q0m2xhfek9xl70 ON Subproject;
ALTER TABLE Subproject ADD UNIQUE alias_unique_for_project (projectId, alias);