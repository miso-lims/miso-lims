-- archive_labs
ALTER TABLE Lab ADD COLUMN archived BOOLEAN NOT NULL DEFAULT FALSE;
ALTER TABLE Institute ADD COLUMN archived BOOLEAN NOT NULL DEFAULT FALSE;

-- kit_lots
ALTER TABLE Library ADD COLUMN kitLot varchar(100);
ALTER TABLE Run ADD COLUMN sequencingKitLot varchar(100);
ALTER TABLE SequencerPartitionContainer ADD COLUMN clusteringKitLot varchar(100);
ALTER TABLE SequencerPartitionContainer ADD COLUMN multiplexingKitLot varchar(100);

-- transfer_changelog
CREATE TABLE TransferChangeLog (
  transferChangeLogId bigint NOT NULL AUTO_INCREMENT,
  transferId bigint NOT NULL,
  columnsChanged varchar(500) NOT NULL,
  userId bigint NOT NULL,
  message longtext NOT NULL,
  changeTime TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP(),
  PRIMARY KEY (transferChangeLogId),
  CONSTRAINT fk_transferChangeLog_transfer FOREIGN KEY (transferId) REFERENCES Transfer(transferId),
  CONSTRAINT fk_transferChangeLog_user FOREIGN KEY (userId) REFERENCES User(userId)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

INSERT INTO TransferChangeLog (transferId, columnsChanged, userId, message, changeTime)
SELECT transferId, '', creator, 'Transfer created', created FROM Transfer;

-- sequencing_controls
CREATE TABLE SequencingControlType (
  sequencingControlTypeId bigint NOT NULL AUTO_INCREMENT,
  alias varchar(50) NOT NULL,
  PRIMARY KEY (sequencingControlTypeId)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

ALTER TABLE Sample ADD COLUMN sequencingControlTypeId bigint;
ALTER TABLE Sample ADD CONSTRAINT fk_sample_sequencingControlType FOREIGN KEY (sequencingControlTypeId) REFERENCES SequencingControlType (sequencingControlTypeId);

