-- pool_orders
CREATE TABLE OrderPurpose(
  purposeId bigint(20) NOT NULL AUTO_INCREMENT,
  alias varchar(50) NOT NULL,
  PRIMARY KEY (purposeId)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

INSERT INTO OrderPurpose(purposeId, alias) VALUES (1, 'Production');

ALTER TABLE SequencingOrder ADD COLUMN purposeId bigint(20) NOT NULL DEFAULT 1;
ALTER TABLE SequencingOrder ADD CONSTRAINT fk_sequencingOrder_purpose FOREIGN KEY (purposeId) REFERENCES OrderPurpose (purposeId);
ALTER TABLE SequencingOrder MODIFY COLUMN purposeId bigint(20) NOT NULL;

CREATE TABLE PoolOrder(
  poolOrderId bigint(20) NOT NULL AUTO_INCREMENT,
  alias varchar(100) NOT NULL,
  description varchar(255),
  purposeId bigint(20) NOT NULL,
  parametersId bigint(20),
  partitions int(11),
  draft BOOLEAN NOT NULL DEFAULT FALSE,
  poolId bigint(20),
  sequencingOrderId bigint(20),
  createdBy bigint(20) NOT NULL,
  creationDate TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updatedBy bigint(20) NOT NULL,
  lastUpdated TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_poolOrder_sequencingParameters FOREIGN KEY (parametersId) REFERENCES SequencingParameters (parametersId),
  CONSTRAINT fk_poolOrder_creator FOREIGN KEY (createdBy) REFERENCES User (userId),
  CONSTRAINT fk_poolOrder_updater FOREIGN KEY (updatedBy) REFERENCES User (userId),
  CONSTRAINT fk_poolOrder_pool FOREIGN KEY (poolId) REFERENCES Pool (poolId),
  CONSTRAINT fk_poolOrder_sequencingOrder FOREIGN KEY (sequencingOrderId) REFERENCES SequencingOrder (sequencingOrderId),
  CONSTRAINT fk_poolOrder_purpose FOREIGN KEY (purposeId) REFERENCES OrderPurpose (purposeId),
  PRIMARY KEY (poolOrderId)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE PoolOrder_LibraryAliquot(
  poolOrderId bigint(20) NOT NULL,
  aliquotId bigint(20) NOT NULL,
  proportion smallint(5) UNSIGNED NOT NULL DEFAULT 1,
  PRIMARY KEY (poolOrderId, aliquotId),
  CONSTRAINT fk_libraryAliquot_poolOrder FOREIGN KEY (poolOrderId) REFERENCES PoolOrder (poolOrderId),
  CONSTRAINT fk_poolOrder_libraryAliquot FOREIGN KEY (aliquotId) REFERENCES LibraryAliquot (aliquotId)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- data_fixes
-- StartNoTest
SELECT userId INTO @user FROM User WHERE loginName = 'admin';

UPDATE Box SET description = NULL, lastModifier = @user WHERE description = '';
UPDATE Box SET locationBarcode = NULL, lastModifier = @user WHERE locationBarcode = '';

UPDATE Run SET health = 'Unknown' WHERE health IS NULL;
-- EndNoTest

ALTER TABLE Run MODIFY COLUMN health varchar(50) NOT NULL;

