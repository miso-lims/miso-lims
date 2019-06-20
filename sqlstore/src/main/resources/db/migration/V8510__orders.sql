CREATE TABLE SequencingOrder (
  sequencingOrderId bigint(20) NOT NULL AUTO_INCREMENT,
  poolId bigint(20) NOT NULL,
  partitions int(11) NOT NULL,
  parametersId bigint(20) DEFAULT NULL,
  createdBy bigint(20) NOT NULL,
  creationDate timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updatedBy bigint(20) NOT NULL,
  lastUpdated timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  description varchar(255) DEFAULT NULL,
  PRIMARY KEY (sequencingOrderId),
  CONSTRAINT fk_sequencingOrder_creator FOREIGN KEY (createdBy) REFERENCES User (userId),
  CONSTRAINT fk_sequencingOrder_parameters FOREIGN KEY (parametersId) REFERENCES SequencingParameters (parametersId),
  CONSTRAINT fk_sequencingOrder_pool FOREIGN KEY (poolId) REFERENCES Pool (poolId),
  CONSTRAINT fk_sequencingOrder_updater FOREIGN KEY (updatedBy) REFERENCES User (userId)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

INSERT INTO SequencingOrder (sequencingOrderId, poolId, partitions, parametersId, createdBy, creationDate, updatedBy, lastUpdated, description)
SELECT poolOrderId, poolId, partitions, parametersId, createdBy, creationDate, updatedBy, lastUpdated, description
FROM PoolOrder;

DROP TABLE PoolOrder;

DROP VIEW IF EXISTS OrderCompletion_Items;
DROP VIEW IF EXISTS OrderCompletion;
DROP VIEW IF EXISTS OrderCompletion_Backing;

DROP TRIGGER IF EXISTS PoolOrderInsert;
DROP TRIGGER IF EXISTS PoolOrderDelete;
DROP TRIGGER IF EXISTS PoolOrderChange;
