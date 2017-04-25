--StartNoTest
UPDATE IndexFamily SET platformType = 'ILLUMINA' WHERE name = '454' AND platformType = 'LS454';

CALL addIndexFamily('10X Family', 'ILLUMINA', FALSE);
CALL addIndex('10X Family', 'SI-GA-H8', 'TTGTTGAT', 1);
CALL addIndex('10X Family', 'SI-GA-H9', 'ACACTGTT', 1);
--EndNoTest
