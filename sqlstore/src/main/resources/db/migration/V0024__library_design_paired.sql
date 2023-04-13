-- StartNoTest
ALTER TABLE LibraryType ENGINE = InnoDB;
-- EndNoTest
ALTER TABLE LibraryDesign ADD COLUMN libraryType bigint NOT NULL;
ALTER TABLE LibraryDesign DROP COLUMN paired;
ALTER TABLE LibraryDesign DROP COLUMN platformName;
ALTER TABLE LibraryDesign ADD CONSTRAINT `FK_ld_lt` FOREIGN KEY (libraryType) REFERENCES LibraryType (libraryTypeId);
