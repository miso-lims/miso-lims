-- freezer_maps
CREATE TABLE StorageLocationMap (
  mapId bigint NOT NULL AUTO_INCREMENT,
  filename varchar(255) NOT NULL,
  description varchar(255),
  PRIMARY KEY (mapId)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

ALTER TABLE StorageLocation ADD COLUMN mapId bigint;
ALTER TABLE StorageLocation ADD CONSTRAINT fk_storageLocation_map FOREIGN KEY (mapId) REFERENCES StorageLocationMap (mapId);
ALTER TABLE StorageLocation ADD COLUMN mapAnchor varchar(100);

INSERT INTO StorageLocationMap (filename)
SELECT DISTINCT
  CASE
    WHEN mapUrl REGEXP '.*/freezermaps/.*#.*' THEN SUBSTRING(LEFT(mapUrl, LOCATE('#', mapUrl)-1), LOCATE('/freezermaps/', mapUrl)+13)
    ELSE SUBSTRING(mapUrl, LOCATE('/freezermaps/', mapUrl)+13)
  END
FROM StorageLocation
WHERE mapUrl REGEXP '.*/freezermaps/.*';

UPDATE StorageLocation SET
  mapId = CASE
    WHEN mapUrl REGEXP '.*/freezermaps/.*#.*' THEN (SELECT mapId FROM StorageLocationMap WHERE filename = SUBSTRING(LEFT(mapUrl, LOCATE('#', mapUrl)-1), LOCATE('/freezermaps/', mapUrl)+13))
    ELSE (SELECT mapId FROM StorageLocationMap WHERE filename = SUBSTRING(mapUrl, LOCATE('/freezermaps/', mapUrl)+13))
  END,
  mapAnchor = CASE
    WHEN mapUrl REGEXP '.*/freezermaps/.*#.*' THEN SUBSTRING(mapUrl, LOCATE('#', mapUrl)+1)
    ELSE NULL
  END
WHERE mapUrl REGEXP '.*/freezermaps/.*';

ALTER TABLE StorageLocation DROP COLUMN mapUrl;

-- orders
CREATE TABLE SequencingOrder (
  sequencingOrderId bigint NOT NULL AUTO_INCREMENT,
  poolId bigint NOT NULL,
  partitions int NOT NULL,
  parametersId bigint DEFAULT NULL,
  createdBy bigint NOT NULL,
  creationDate timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updatedBy bigint NOT NULL,
  lastUpdated timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  description varchar(255) DEFAULT NULL,
  PRIMARY KEY (sequencingOrderId),
  CONSTRAINT fk_sequencingOrder_creator FOREIGN KEY (createdBy) REFERENCES User (userId),
  CONSTRAINT fk_sequencingOrder_parameters FOREIGN KEY (parametersId) REFERENCES SequencingParameters (parametersId),
  CONSTRAINT fk_sequencingOrder_pool FOREIGN KEY (poolId) REFERENCES Pool (poolId),
  CONSTRAINT fk_sequencingOrder_updater FOREIGN KEY (updatedBy) REFERENCES User (userId)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

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

