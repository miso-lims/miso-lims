-- unique_names

ALTER TABLE Sample ADD UNIQUE (name);
ALTER TABLE Library ADD UNIQUE (name);
ALTER TABLE Experiment ADD UNIQUE (name);
ALTER TABLE LibraryDilution ADD UNIQUE (name);
ALTER TABLE Run ADD UNIQUE (name);
ALTER TABLE Pool ADD UNIQUE (name);
ALTER TABLE Project ADD UNIQUE (name);
ALTER TABLE Study ADD UNIQUE (name);
ALTER TABLE Submission DROP COLUMN name;


-- tarseq_required

ALTER TABLE LibraryDesignCode ADD COLUMN targetedSequencingRequired tinyint NOT NULL DEFAULT 0;


