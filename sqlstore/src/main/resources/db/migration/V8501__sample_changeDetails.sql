ALTER TABLE Sample ADD COLUMN creator bigint(20);
ALTER TABLE Sample ADD COLUMN created timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP();
ALTER TABLE Sample ADD COLUMN lastModified timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP();

UPDATE Sample SET
  created = (SELECT MIN(changeTime) FROM SampleChangeLog WHERE sampleId = Sample.sampleId),
  lastModified = (SELECT MAX(changeTime) FROM SampleChangeLog WHERE sampleId = Sample.sampleId),
  creator = (SELECT userId FROM SampleChangeLog WHERE sampleId = Sample.sampleId ORDER BY changeTime ASC LIMIT 1);

ALTER TABLE Sample CHANGE COLUMN creator creator bigint(20) NOT NULL;
ALTER TABLE Sample ADD CONSTRAINT fk_sample_creator FOREIGN KEY (creator) REFERENCES User (userId);

DROP VIEW IF EXISTS SampleDerivedInfo;
