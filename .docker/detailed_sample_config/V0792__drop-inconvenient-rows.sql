/*These tables came pre-loaded with a few values. We want to drop the single 
values without cascading into foreign key checks.*/
SET FOREIGN_KEY_CHECKS=0;
DELETE FROM BoxUse WHERE alias='Libraries';
DELETE FROM SampleClass WHERE alias='Slide';
DELETE FROM ReferenceGenome WHERE alias='Unknown';
SET FOREIGN_KEY_CHECKS=1;

