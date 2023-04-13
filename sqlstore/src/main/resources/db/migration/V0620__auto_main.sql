-- nonUniqueIndices

--StartNoTest

ALTER TABLE Indices DROP FOREIGN KEY Indices_ibfk_1;

set @var=if((SELECT true FROM information_schema.TABLE_CONSTRAINTS WHERE
            CONSTRAINT_SCHEMA = DATABASE() AND
            TABLE_NAME        = 'Indices' AND
            CONSTRAINT_NAME   = 'uk_index' AND
            CONSTRAINT_TYPE   = 'UNIQUE') = true,'ALTER TABLE Indices
            DROP INDEX uk_index','select 1');

prepare stmt from @var;
execute stmt;
deallocate prepare stmt;

ALTER TABLE Indices ADD CONSTRAINT `Indices_ibfk_1` FOREIGN KEY (`indexFamilyId`) REFERENCES `IndexFamily` (`indexFamilyId`);

-- EndNoTest


-- Edit_Library_creationDate

ALTER TABLE `Library` MODIFY `creationDate` date DEFAULT NULL;


-- Dilution_Creator_Id

ALTER TABLE LibraryDilution ADD creator bigint NOT NULL AFTER dilutionUserName;
UPDATE LibraryDilution SET creator = (SELECT userId FROM User WHERE fullName = dilutionUserName);
ALTER TABLE LibraryDilution ADD CONSTRAINT fk_libraryDilution_creator FOREIGN KEY (creator) REFERENCES User (userId);
ALTER TABLE LibraryDilution DROP COLUMN dilutionUserName;


-- Archive_Sample_Purposes

ALTER TABLE `SamplePurpose` ADD COLUMN `archived` tinyint NOT NULL DEFAULT 0;


