CREATE TABLE StorageLocationMap (
  mapId bigint(20) NOT NULL AUTO_INCREMENT,
  filename varchar(255) NOT NULL,
  description varchar(255),
  PRIMARY KEY (mapId)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

ALTER TABLE StorageLocation ADD COLUMN mapId bigint(20);
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
