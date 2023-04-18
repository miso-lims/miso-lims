-- Switch tables to InnoDB to permit foreign key constraints.
ALTER TABLE TagBarcodes ENGINE = InnoDB ROW_FORMAT = DEFAULT;

CREATE TABLE `TagBarcodeFamily` (
  `tagFamilyId` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  `platformType` varchar(20) NOT NULL,
  `archived` BOOLEAN NOT NULL DEFAULT FALSE,
  PRIMARY KEY (`tagFamilyId`),
  UNIQUE KEY `UK_tbs_name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

INSERT INTO TagBarcodeFamily(platformType, name) VALUES
  ('ILLUMINA', 'TruSeq Single Index'),
  ('LS454', '454 Rapid Library'),
  ('ILLUMINA', 'Nextera Dual Index'),
  ('ILLUMINA', 'NEXTflex 8bp'),
  ('ILLUMINA', 'Nextera'),
  ('ILLUMINA', 'RBC1'),
  ('ILLUMINA', 'Illumina 6bp'),
  ('ILLUMINA', 'Agilent'),
  ('ILLUMINA', 'TruSeq smRNA'),
  ('ILLUMINA', 'TCRindex2'),
  ('ILLUMINA', 'SureSelect XT2'),
  ('ILLUMINA', 'NEXTflex 6bp');

ALTER TABLE TagBarcodes ADD COLUMN position int DEFAULT 1;
ALTER TABLE TagBarcodes ADD COLUMN tagFamilyId bigint;
INSERT INTO TagBarcodeFamily(name, platformType) SELECT DISTINCT strategyName, UPPER(platformName) FROM TagBarcodes WHERE 0 = (SELECT COUNT(*) FROM TagBarcodeFamily WHERE TagBarcodeFamily.name = TagBarcodes.strategyName AND TagBarcodeFamily.platformType = UPPER(TagBarcodes.platformName));
UPDATE TagBarcodes SET tagFamilyId = (SELECT tagFamilyId FROM TagBarcodeFamily WHERE TagBarcodeFamily.platformType = UPPER(TagBarcodes.platformName) AND TagBarcodeFamily.name = TagBarcodes.strategyName);
ALTER TABLE TagBarcodes CHANGE COLUMN tagFamilyId tagFamilyId bigint NOT NULL;
ALTER TABLE TagBarcodes ADD FOREIGN KEY (tagFamilyId) REFERENCES TagBarcodeFamily (tagFamilyId);
ALTER TABLE TagBarcodes DROP COLUMN platformName;
ALTER TABLE TagBarcodes DROP COLUMN strategyName;

-- Set positions in dual index barcodes
UPDATE TagBarcodes SET position = 2 WHERE name LIKE '_5%' AND tagFamilyId = (SELECT tagFamilyId FROM TagBarcodeFamily WHERE name = 'Nextera Dual Index');
UPDATE TagBarcodes SET position = 2 WHERE name LIKE 'S%7%' AND tagFamilyId = (SELECT tagFamilyId FROM TagBarcodeFamily WHERE name = '16S V4 Dual Index');

-- Pad single digit barcodes to sort nicely
UPDATE TagBarcodes SET name = CONCAT('Index 0', SUBSTRING(name, 7, 1)) WHERE name LIKE 'Index _';

-- Drop all barcode families with no barcodes present
DELETE FROM TagBarcodeFamily WHERE (SELECT COUNT(*) FROM TagBarcodes WHERE TagBarcodes.tagFamilyId = TagBarcodeFamily.tagFamilyId) = 0;
