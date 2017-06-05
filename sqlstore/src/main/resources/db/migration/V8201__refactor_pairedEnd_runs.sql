ALTER TABLE RunIllumina ADD COLUMN pairedEnd tinyint(1) NOT NULL DEFAULT '1';
ALTER TABLE RunLS454 ADD COLUMN pairedEnd tinyint(1) NOT NULL DEFAULT '1';

CREATE TABLE RunSolid(
  runId bigint(20) NOT NULL,
  pairedEnd tinyint(1) NOT NULL DEFAULT '1',
  PRIMARY KEY (`runId`),
  CONSTRAINT runsolid_run_runid FOREIGN KEY (runId) REFERENCES Run (runId)
) ENGINE=InnoDB CHARSET=utf8;

INSERT INTO RunSolid (runId, pairedEnd) SELECT runId, pairedEnd FROM Run WHERE 
  sequencerReference_sequencerReferenceId IN (SELECT sr.referenceId FROM SequencerReference sr 
  JOIN Platform p ON sr.platformId = p.platformId WHERE p.name = 'SOLID');
UPDATE RunIllumina SET pairedEnd = (SELECT pairedEnd FROM Run WHERE RunIllumina.runId = Run.runId);
UPDATE RunLS454 SET pairedEnd = (SELECT pairedEnd FROM Run WHERE RunLS454.runId = Run.runId);

ALTER TABLE Run DROP COLUMN pairedEnd;
ALTER TABLE RunPacBio DROP COLUMN creationDate;