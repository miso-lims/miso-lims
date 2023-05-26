-- freezers

CREATE TABLE StorageLocation (
  locationId bigint NOT NULL AUTO_INCREMENT,
  parentLocationId bigint,
  locationUnit varchar(50) NOT NULL,
  alias varchar(255) NOT NULL,
  identificationBarcode varchar(255),
  PRIMARY KEY (locationId),
  CONSTRAINT fk_location_parent FOREIGN KEY (parentLocationId) REFERENCES StorageLocation (locationId),
  CONSTRAINT uk_storagelocation_barcode UNIQUE (identificationBarcode),
  CONSTRAINT uk_storagelocation_child UNIQUE (parentLocationId, locationUnit, alias)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

ALTER TABLE Box ADD COLUMN locationId bigint;
ALTER TABLE Box ADD CONSTRAINT fk_box_location FOREIGN KEY (locationId) REFERENCES StorageLocation (locationId);


-- qc

ALTER TABLE ContainerQC ADD created timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP;
ALTER TABLE ContainerQC ADD lastModified timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP;
ALTER TABLE LibraryQC ADD created timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP;
ALTER TABLE LibraryQC ADD lastModified timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP;
ALTER TABLE PoolQC ADD created timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP;
ALTER TABLE PoolQC ADD lastModified timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP;
ALTER TABLE SampleQC ADD created timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP;
ALTER TABLE SampleQC ADD lastModified timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP;


