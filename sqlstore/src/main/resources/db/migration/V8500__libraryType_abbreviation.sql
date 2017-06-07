ALTER TABLE LibraryType ADD COLUMN abbreviation varchar(5) DEFAULT NULL;
UPDATE LibraryType SET abbreviation = 'PE' WHERE platformType = 'ILLUMINA' AND description = 'Paired End';
UPDATE LibraryType SET abbreviation = 'SE' WHERE platformType = 'ILLUMINA' AND description = 'Single End';
UPDATE LibraryType SET abbreviation = 'MP' WHERE platformType = 'ILLUMINA' AND description = 'Mate Pair';
UPDATE LibraryType SET abbreviation = 'TR' WHERE platformType = 'ILLUMINA' AND description = 'Total RNA';
