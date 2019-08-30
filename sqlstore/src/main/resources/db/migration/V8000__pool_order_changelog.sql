CREATE TABLE PoolOrderChangeLog (
  poolOrderChangeLogId bigint(20) PRIMARY KEY AUTO_INCREMENT,
  poolOrderId bigint(20) NOT NULL,
  columnsChanged varchar(500) NOT NULL,
  userId bigint(20) NOT NULL,
  message longtext NOT NULL,
  changeTime timestamp DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_poolOrderChangeLog_pool FOREIGN KEY (poolOrderId) REFERENCES PoolOrder(poolOrderId),
  CONSTRAINT fk_poolOrderChangeLog_user FOREIGN KEY (userId) REFERENCES User(userId)
) Engine=InnoDB DEFAULT CHARSET=utf8;
