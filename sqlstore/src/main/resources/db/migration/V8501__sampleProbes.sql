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
