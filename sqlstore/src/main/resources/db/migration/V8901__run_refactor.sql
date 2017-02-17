CREATE TABLE RunPacBio(
  runId bigint(20) NOT NULL,
  movieDuration bigint(20),
  wellName varchar(50),
  creationDate date,
  CONSTRAINT runpacbio_run_runid FOREIGN KEY (runId) REFERENCES Run(runId)
) ENGINE=InnoDB CHARSET=utf8;

INSERT INTO RunPacBio(runId) SELECT runId FROM Run WHERE platformType = 'PACBIO';

CREATE TABLE RunLS454(
  runId bigint(20) NOT NULL,
  cycles int,
  CONSTRAINT runls454_run_runid FOREIGN KEY (runId) REFERENCES Run(runId)
) ENGINE=InnoDB CHARSET=utf8;

INSERT INTO RunLS454(runId, cycles) SELECT runId, cycles FROM Run WHERE platformType = 'LS454';

CREATE TABLE RunIllumina(
  runId bigint(20) NOT NULL,
  callCycle int,
  imgCycle int,
  numCycles int,
  scoreCycle int,
  CONSTRAINT runillumina_run_runid FOREIGN KEY (runId) REFERENCES Run(runId)
) ENGINE=InnoDB CHARSET=utf8;

INSERT INTO RunIllumina(runId, callCycle, imgCycle, numCycles, scoreCycle)
 SELECT runId, callCycle, imgCycle, numCycles, scoreCycle FROM Run WHERE platformType = 'ILLUMINA';

ALTER TABLE Run ADD COLUMN startDate date;
ALTER TABLE Run ADD COLUMN completionDate date;
ALTER TABLE Run ADD COLUMN health varchar(50);
ALTER TABLE Run ADD COLUMN metrics longtext;

UPDATE Run SET
  startDate = (SELECT startDate FROM Status WHERE Run.status_statusId = Status.statusId),
  completionDate = (SELECT completionDate FROM Status WHERE Run.status_statusId = Status.statusId),
  health = (SELECT Status.health FROM Status WHERE Run.status_statusId = Status.statusId);

ALTER TABLE Run DROP COLUMN platformRunId;
ALTER TABLE Run DROP COLUMN cycles;
ALTER TABLE Run DROP COLUMN platformType;
ALTER TABLE Run DROP FOREIGN KEY fk_run_status;
ALTER TABLE Run DROP COLUMN status_statusId;
ALTER TABLE Run DROP COLUMN callCycle;
ALTER TABLE Run DROP COLUMN imgCycle;
ALTER TABLE Run DROP COLUMN numCycles;
ALTER TABLE Run DROP COLUMN scoreCycle;
DROP TABLE Status;
