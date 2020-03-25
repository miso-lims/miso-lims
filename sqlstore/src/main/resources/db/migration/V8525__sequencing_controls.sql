CREATE TABLE SequencingControlType (
  sequencingControlTypeId bigint(20) NOT NULL AUTO_INCREMENT,
  alias varchar(50) NOT NULL,
  PRIMARY KEY (sequencingControlTypeId)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

ALTER TABLE Sample ADD COLUMN sequencingControlTypeId bigint(20);
ALTER TABLE Sample ADD CONSTRAINT fk_sample_sequencingControlType FOREIGN KEY (sequencingControlTypeId) REFERENCES SequencingControlType (sequencingControlTypeId);
