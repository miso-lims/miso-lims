-- archived_SVRs

ALTER TABLE SampleValidRelationship ADD COLUMN archived BOOLEAN NOT NULL DEFAULT FALSE;

-- tagbarcode_to_index

-- StartNoTest
ALTER TABLE TagBarcodes DROP FOREIGN KEY `TagBarcodes_ibfk_1`;
-- EndNoTest

ALTER TABLE TagBarcodes CHANGE COLUMN tagFamilyId indexFamilyId bigint NOT NULL;
ALTER TABLE TagBarcodeFamily CHANGE COLUMN tagFamilyId indexFamilyId bigint NOT NULL AUTO_INCREMENT;
ALTER TABLE TagBarcodes CHANGE COLUMN tagId indexId bigint NOT NULL AUTO_INCREMENT;
ALTER TABLE Library_TagBarcode CHANGE COLUMN barcode_barcodeId index_indexId bigint NOT NULL;
ALTER TABLE Plate CHANGE COLUMN tagBarcodeId indexId bigint DEFAULT NULL;

ALTER TABLE TagBarcodes RENAME TO Indices;
ALTER TABLE TagBarcodeFamily RENAME TO IndexFamily;
ALTER TABLE Library_TagBarcode RENAME TO Library_Index;

ALTER TABLE Indices ADD FOREIGN KEY (indexFamilyId) REFERENCES IndexFamily (indexFamilyId);

