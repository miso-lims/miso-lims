-- new_project_fields
ALTER TABLE Project
  ADD COLUMN samplesExpected int,
  ADD COLUMN contactId bigint,
  ADD CONSTRAINT fk_project_contact FOREIGN KEY (contactId) REFERENCES Contact (contactId);

-- library_aliquot_description
ALTER TABLE LibraryAliquot ADD COLUMN description varchar(255);

-- combine_lab_and_institute
UPDATE Lab
JOIN Institute inst ON inst.instituteId = Lab.instituteId
SET Lab.alias = CONCAT(
  inst.alias,
  IF(Lab.alias = 'Not Specified' OR Lab.alias = inst.alias, '', CONCAT(' - ', Lab.alias))
);

ALTER TABLE Lab DROP FOREIGN KEY lab_institute_fkey;
ALTER TABLE Lab DROP INDEX `lab_institute-alias_uk`;
ALTER TABLE Lab DROP COLUMN instituteId;
DROP TABLE Institute;

