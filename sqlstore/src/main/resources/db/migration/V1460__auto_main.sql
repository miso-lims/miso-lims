-- rename_project_fields
ALTER TABLE Project RENAME COLUMN alias TO title;
ALTER TABLE Project RENAME COLUMN shortName TO code;

