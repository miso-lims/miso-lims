UPDATE Lab
JOIN Institute inst ON inst.instituteId = Lab.instituteId
SET Lab.alias = CONCAT(
  inst.alias,
  IF(Lab.alias = 'Not Specified' OR Lab.alias = inst.alias, '', CONCAT(' - ', Lab.alias))
);

ALTER TABLE Lab DROP FOREIGN KEY lab_institute_fkey;
ALTER TABLE Lab DROP COLUMN instituteId;
DROP TABLE Institute;
