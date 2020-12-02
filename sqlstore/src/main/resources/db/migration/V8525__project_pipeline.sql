CREATE TABLE Pipeline (
  pipelineId bigint(20) NOT NULL AUTO_INCREMENT,
  alias varchar(50) NOT NULL,
  PRIMARY KEY (pipelineId),
  CONSTRAINT uk_pipeline_alias UNIQUE (alias)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

INSERT INTO Pipeline (alias) VALUES ('Default');
INSERT INTO Pipeline (alias)
SELECT 'Clinical' FROM DUAL
WHERE (SELECT COUNT(*) FROM (SELECT DISTINCT clinical FROM Project) sub) > 1;

ALTER TABLE Project ADD COLUMN pipelineId bigint(20);

UPDATE Project SET pipelineId = (SELECT pipelineId FROM Pipeline WHERE alias = 'Default');
UPDATE Project SET pipelineId = (SELECT pipelineId FROM Pipeline WHERE alias = 'Clinical')
WHERE (SELECT COUNT(*) FROM (SELECT DISTINCT clinical FROM Project) sub) > 1
AND clinical = TRUE;

ALTER TABLE Project MODIFY COLUMN pipelineId bigint(20) NOT NULL;
ALTER TABLE Project ADD CONSTRAINT fk_project_pipeline FOREIGN KEY (pipelineId) REFERENCES Pipeline (pipelineId);
