-- add_QcColumArrayRun
ALTER TABLE ArrayRun ADD COLUMN qcPassed BOOLEAN;
ALTER TABLE ArrayRun ADD COLUMN qcUser bigint;
ALTER TABLE ArrayRun ADD COLUMN qcDate DATE;
ALTER TABLE ArrayRun ADD CONSTRAINT fk_arrayRun_qcUser FOREIGN KEY (qcUser) REFERENCES User (userId);

-- sampleProbes
CREATE TABLE SampleProbe (
  probeId bigint NOT NULL AUTO_INCREMENT,
  sampleId bigint NOT NULL,
  identifier varchar(255) NOT NULL,
  name varchar(255) NOT NULL,
  readNumber varchar(5) NOT NULL,
  pattern varchar(50) NOT NULL,
  sequence varchar(255) NOT NULL,
  featureType varchar(50) NOT NULL,
  targetGeneId varchar(50),
  targetGeneName varchar(50),
  PRIMARY KEY (probeId),
  CONSTRAINT fk_sampleProbe_sample FOREIGN KEY (sampleId) REFERENCES Sample (sampleId)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE ProbeSet (
  probeSetId bigint NOT NULL AUTO_INCREMENT,
  name varchar(255) NOT NULL,
  PRIMARY KEY (probeSetId)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE ProbeSetProbe (
  probeId bigint NOT NULL AUTO_INCREMENT,
  probeSetId bigint NOT NULL,
  identifier varchar(255) NOT NULL,
  name varchar(255) NOT NULL,
  readNumber varchar(5) NOT NULL,
  pattern varchar(50) NOT NULL,
  sequence varchar(255) NOT NULL,
  featureType varchar(50) NOT NULL,
  targetGeneId varchar(50),
  targetGeneName varchar(50),
  PRIMARY KEY (probeId),
  CONSTRAINT fk_probeSetProbe_set FOREIGN KEY (probeSetId) REFERENCES ProbeSet (probeSetId)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

