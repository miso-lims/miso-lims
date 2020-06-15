CREATE TABLE Sop (
  sopId bigint(20) NOT NULL AUTO_INCREMENT,
  alias varchar(100) NOT NULL,
  version varchar(50) NOT NULL,
  category varchar(20) NOT NULL,
  url varchar(255) NOT NULL,
  archived BOOLEAN NOT NULL DEFAULT FALSE,
  PRIMARY KEY (sopId),
  CONSTRAINT uk_sop_version UNIQUE (category, alias, version)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

ALTER TABLE Sample ADD COLUMN sopId bigint(20);
ALTER TABLE Sample ADD CONSTRAINT fk_sample_sop FOREIGN KEY (sopId) REFERENCES Sop (sopId);
ALTER TABLE Library ADD COLUMN sopId bigint(20);
ALTER TABLE Library ADD CONSTRAINT fk_library_sop FOREIGN KEY (sopId) REFERENCES Sop (sopId);
ALTER TABLE Run ADD COLUMN sopId bigint(20);
ALTER TABLE Run ADD CONSTRAINT fk_run_sop FOREIGN KEY (sopId) REFERENCES Sop (sopId);
