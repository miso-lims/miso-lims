ALTER TABLE Sample ADD COLUMN creator bigint(20);
ALTER TABLE Sample ADD COLUMN created timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP();
ALTER TABLE Sample ADD COLUMN lastModified timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP();

-- StartNoTest
UPDATE Sample SET
  created = (SELECT MIN(changeTime) FROM SampleChangeLog WHERE sampleId = Sample.sampleId),
  lastModified = (SELECT MAX(changeTime) FROM SampleChangeLog WHERE sampleId = Sample.sampleId),
  creator = (SELECT userId FROM SampleChangeLog WHERE sampleId = Sample.sampleId ORDER BY changeTime ASC LIMIT 1);
-- EndNoTest

ALTER TABLE Sample CHANGE COLUMN creator creator bigint(20) NOT NULL;
ALTER TABLE Sample ADD CONSTRAINT fk_sample_creator FOREIGN KEY (creator) REFERENCES User (userId);

DROP VIEW IF EXISTS SampleDerivedInfo;


ALTER TABLE Library ADD COLUMN creator bigint(20);
ALTER TABLE Library ADD COLUMN created timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP();
ALTER TABLE Library ADD COLUMN lastModified timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP();

-- StartNoTest
UPDATE Library SET
  created = IF(ABS(DATEDIFF(TIMESTAMP(creationDate), (SELECT MIN(changeTime) FROM LibraryChangeLog WHERE libraryId = Library.libraryId))) > 1,
        LEAST(TIMESTAMP(creationDate), (SELECT MIN(changeTime) FROM LibraryChangeLog WHERE libraryId = Library.libraryId)),
        (SELECT MIN(changeTime) FROM LibraryChangeLog WHERE libraryId = Library.libraryId)),
  lastModified = (SELECT MAX(changeTime) FROM LibraryChangeLog WHERE libraryId = Library.libraryId),
  creator = (SELECT userId FROM LibraryChangeLog WHERE libraryId = Library.libraryId ORDER BY changeTime ASC LIMIT 1);
-- EndNoTest

ALTER TABLE Library DROP COLUMN creationDate;
ALTER TABLE Library CHANGE COLUMN creator creator bigint(20) NOT NULL;
ALTER TABLE Library ADD CONSTRAINT fk_library_creator FOREIGN KEY (creator) REFERENCES User (userId);

DROP VIEW IF EXISTS LibraryDerivedInfo;
