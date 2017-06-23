CREATE TABLE RunIonTorrent(
  runId bigint(20) NOT NULL,
  PRIMARY KEY (`runId`),
  CONSTRAINT runiontorrent_run_runid FOREIGN KEY (runId) REFERENCES Run(runId)
) ENGINE=InnoDB CHARSET=utf8;

INSERT INTO RunIonTorrent (runId) SELECT runId FROM Run WHERE 
  sequencerReference_sequencerReferenceId IN (SELECT sr.referenceId FROM SequencerReference sr 
  JOIN Platform p ON sr.platformId = p.platformId WHERE p.name = 'IONTORRENT');
