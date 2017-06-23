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

ALTER TABLE Pool ADD COLUMN creator bigint(20);
ALTER TABLE Pool ADD COLUMN created timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP();
ALTER TABLE Pool ADD COLUMN lastModified timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP();

-- StartNoTest
UPDATE Pool SET
  created = IF(ABS(DATEDIFF(TIMESTAMP(creationDate), (SELECT MIN(changeTime) FROM PoolChangeLog WHERE poolId = Pool.poolId))) > 1,
        LEAST(TIMESTAMP(creationDate), (SELECT MIN(changeTime) FROM PoolChangeLog WHERE poolId = Pool.poolId)),
        (SELECT MIN(changeTime) FROM PoolChangeLog WHERE poolId = Pool.poolId)),
  lastModified = (SELECT MAX(changeTime) FROM PoolChangeLog WHERE poolId = Pool.poolId),
  creator = (SELECT userId FROM PoolChangeLog WHERE poolId = Pool.poolId ORDER BY changeTime ASC LIMIT 1);
-- EndNoTest

ALTER TABLE Pool DROP COLUMN creationDate;
ALTER TABLE Pool CHANGE COLUMN creator creator bigint(20) NOT NULL;
ALTER TABLE Pool ADD CONSTRAINT fk_pool_creator FOREIGN KEY (creator) REFERENCES User (userId);

DROP VIEW IF EXISTS PoolDerivedInfo;

ALTER TABLE Run ADD COLUMN creator bigint(20);
ALTER TABLE Run ADD COLUMN created timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP();
ALTER TABLE Run ADD COLUMN lastModified timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP();

-- StartNoTest
UPDATE Run SET
  created = (SELECT MIN(changeTime) FROM RunChangeLog WHERE runId = Run.runId),
  lastModified = (SELECT MAX(changeTime) FROM RunChangeLog WHERE runId = Run.runId),
  creator = (SELECT userId FROM RunChangeLog WHERE runId = Run.runId ORDER BY changeTime ASC LIMIT 1);
-- EndNoTest

ALTER TABLE Run CHANGE COLUMN creator creator bigint(20) NOT NULL;
ALTER TABLE Run ADD CONSTRAINT fk_run_creator FOREIGN KEY (creator) REFERENCES User (userId);

DROP VIEW IF EXISTS RunDerivedInfo;
