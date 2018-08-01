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
