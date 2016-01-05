--StartNoTest
ALTER TABLE SequencerReference ENGINE=InnoDB;
--EndNoTest
ALTER TABLE SequencerReference ADD COLUMN serialNumber varchar(30);
ALTER TABLE SequencerReference ADD COLUMN dateCommissioned date NOT NULL DEFAULT CURRENT_TIMESTAMP;
ALTER TABLE SequencerReference ADD COLUMN dateDecommissioned date DEFAULT NULL;
ALTER TABLE SequencerReference ADD COLUMN upgradedSequencerReferenceId bigint(20);
ALTER TABLE SequencerReference ADD CONSTRAINT sequencerReference_upgradedReference_fkey FOREIGN KEY (upgradedSequencerReferenceId) REFERENCES SequencerReference(referenceId);

CREATE TABLE SequencerServiceRecord (
  recordId bigint(20) PRIMARY KEY,
  sequencerReferenceId bigint(20) NOT NULL,
  title varchar(50) NOT NULL,
  details varchar(2000),
  servicedBy varchar(30) NOT NULL,
  phone varchar(12),
  serviceDate date NOT NULL,
  shutdownTime datetime DEFAULT NULL,
  restoredTime datetime DEFAULT NULL,
  CONSTRAINT sequencerServiceRecord_sequencer_fkey FOREIGN KEY (sequencerReferenceId) REFERENCES SequencerReference(referenceId)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE SequencerServiceAttachment (
  attachmentId bigint(20) PRIMARY KEY,
  serviceRecordId bigint(20) NOT NULL,
  attachment MEDIUMBLOB NOT NULL,
  comment varchar(255),
  CONSTRAINT sequencerServiceAttachment_serviceRecord_fkey FOREIGN KEY (serviceRecordId) REFERENCES SequencerServiceRecord(recordId)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
