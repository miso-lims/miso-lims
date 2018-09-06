DROP TABLE IF EXISTS LibrarySpikeIn;
CREATE TABLE LibrarySpikeIn (
  spikeInId bigint(20) NOT NULL AUTO_INCREMENT,
  alias varchar(255) NOT NULL,
  PRIMARY KEY (spikeInId)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

ALTER TABLE Library ADD COLUMN spikeInId bigint(20);
ALTER TABLE Library ADD CONSTRAINT fk_library_spikeIn FOREIGN KEY (spikeInId) REFERENCES LibrarySpikeIn(spikeInId);
ALTER TABLE Library ADD COLUMN spikeInDilutionFactor varchar(50);
ALTER TABLE Library ADD COLUMN spikeInVolume DECIMAL(14,10);
