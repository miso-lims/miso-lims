ALTER TABLE Project ADD COLUMN shortName varchar(5) DEFAULT NULL;
UPDATE Project SET shortName = UPPER(SUBSTRING(alias, 1, 5));
