--StartNoTest
ALTER TABLE TagBarcodes DROP FOREIGN KEY `TagBarcodes_ibfk_1`;
--EndNoTest

ALTER TABLE TagBarcodes CHANGE COLUMN tagFamilyId indexFamilyId bigint(20) NOT NULL;
ALTER TABLE TagBarcodeFamily CHANGE COLUMN tagFamilyId indexFamilyId bigint(20) NOT NULL AUTO_INCREMENT;
ALTER TABLE TagBarcodes CHANGE COLUMN tagId indexId bigint(20) NOT NULL AUTO_INCREMENT;
ALTER TABLE Library_TagBarcode CHANGE COLUMN barcode_barcodeId index_indexId bigint(20) NOT NULL;
ALTER TABLE Plate CHANGE COLUMN tagBarcodeId indexId bigint(20) DEFAULT NULL;

ALTER TABLE TagBarcodes RENAME TO Indices;
ALTER TABLE TagBarcodeFamily RENAME TO IndexFamily;
ALTER TABLE Library_TagBarcode RENAME TO Library_Index;

ALTER TABLE Indices ADD FOREIGN KEY (indexFamilyId) REFERENCES IndexFamily (indexFamilyId);
