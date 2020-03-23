CREATE TABLE TransferChangeLog (
  transferChangeLogId bigint(20) NOT NULL AUTO_INCREMENT,
  transferId bigint(20) NOT NULL,
  columnsChanged varchar(500) NOT NULL,
  userId bigint(20) NOT NULL,
  message longtext NOT NULL,
  changeTime TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP(),
  PRIMARY KEY (transferChangeLogId),
  CONSTRAINT fk_transferChangeLog_transfer FOREIGN KEY (transferId) REFERENCES Transfer(transferId),
  CONSTRAINT fk_transferChangeLog_user FOREIGN KEY (userId) REFERENCES User(userId)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

INSERT INTO TransferChangeLog (transferId, columnsChanged, userId, message, changeTime)
SELECT transferId, '', creator, 'Transfer created', created FROM Transfer;
