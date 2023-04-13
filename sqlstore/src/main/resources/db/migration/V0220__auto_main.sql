-- dilution_barcodes

ALTER TABLE LibraryDilution ADD COLUMN `discarded` tinyint NOT NULL DEFAULT '0';
CREATE INDEX LibraryDilution_identificationBarcode ON LibraryDilution(identificationBarcode);
UPDATE LibraryDilution SET identificationBarcode = NULL WHERE identificationBarcode = '';
ALTER TABLE LibraryDilution ADD CONSTRAINT uk_librarydilution_identificationbarcode UNIQUE (identificationBarcode);


