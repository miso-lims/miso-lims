CREATE TABLE Index_RealSequences (
  indexId bigint(20) NOT NULL,
  sequence varchar(100),
  PRIMARY KEY (indexId, sequence),
  CONSTRAINT fk_index_indexId FOREIGN KEY (indexId) REFERENCES Indices (indexId)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
