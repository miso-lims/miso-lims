CREATE TABLE StorageLocation (
  locationId bigint(20) NOT NULL AUTO_INCREMENT,
  parentLocationId bigint(20),
  locationUnit varchar(50) NOT NULL,
  alias varchar(255) NOT NULL,
  identificationBarcode varchar(255),
  PRIMARY KEY (locationId),
  CONSTRAINT fk_location_parent FOREIGN KEY (parentLocationId) REFERENCES StorageLocation (locationId),
  CONSTRAINT uk_storagelocation_barcode UNIQUE (identificationBarcode),
  CONSTRAINT uk_storagelocation_child UNIQUE (parentLocationId, alias)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

ALTER TABLE Box ADD COLUMN locationId bigint(20);
ALTER TABLE Box ADD CONSTRAINT fk_box_location FOREIGN KEY (locationId) REFERENCES StorageLocation (locationId);
