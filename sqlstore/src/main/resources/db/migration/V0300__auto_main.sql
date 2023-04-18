-- derivedInfo_removal

ALTER TABLE Sample ADD COLUMN creator bigint;
ALTER TABLE Sample ADD COLUMN created timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP();
ALTER TABLE Sample ADD COLUMN lastModified timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP();

UPDATE Sample SET
  created = (SELECT MIN(changeTime) FROM SampleChangeLog WHERE sampleId = Sample.sampleId),
  lastModified = (SELECT MAX(changeTime) FROM SampleChangeLog WHERE sampleId = Sample.sampleId),
  creator = (SELECT userId FROM SampleChangeLog WHERE sampleId = Sample.sampleId ORDER BY changeTime ASC LIMIT 1);

ALTER TABLE Sample CHANGE COLUMN creator creator bigint NOT NULL;
ALTER TABLE Sample ADD CONSTRAINT fk_sample_creator FOREIGN KEY (creator) REFERENCES User (userId);

DROP VIEW IF EXISTS SampleDerivedInfo;


ALTER TABLE Library ADD COLUMN creator bigint;
ALTER TABLE Library ADD COLUMN created timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP();
ALTER TABLE Library ADD COLUMN lastModified timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP();

UPDATE Library SET
  created = (SELECT MIN(changeTime) FROM LibraryChangeLog WHERE libraryId = Library.libraryId),
  lastModified = (SELECT MAX(changeTime) FROM LibraryChangeLog WHERE libraryId = Library.libraryId),
  creator = (SELECT userId FROM LibraryChangeLog WHERE libraryId = Library.libraryId ORDER BY changeTime ASC LIMIT 1);

ALTER TABLE Library CHANGE COLUMN creator creator bigint NOT NULL;
ALTER TABLE Library ADD CONSTRAINT fk_library_creator FOREIGN KEY (creator) REFERENCES User (userId);

DROP VIEW IF EXISTS LibraryDerivedInfo;

ALTER TABLE Pool ADD COLUMN creator bigint;
ALTER TABLE Pool ADD COLUMN created timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP();
ALTER TABLE Pool ADD COLUMN lastModified timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP();

UPDATE Pool SET
  created = (SELECT MIN(changeTime) FROM PoolChangeLog WHERE poolId = Pool.poolId),
  lastModified = (SELECT MAX(changeTime) FROM PoolChangeLog WHERE poolId = Pool.poolId),
  creator = (SELECT userId FROM PoolChangeLog WHERE poolId = Pool.poolId ORDER BY changeTime ASC LIMIT 1);

ALTER TABLE Pool CHANGE COLUMN creator creator bigint NOT NULL;
ALTER TABLE Pool ADD CONSTRAINT fk_pool_creator FOREIGN KEY (creator) REFERENCES User (userId);

DROP VIEW IF EXISTS PoolDerivedInfo;

ALTER TABLE Run ADD COLUMN creator bigint;
ALTER TABLE Run ADD COLUMN created timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP();
ALTER TABLE Run ADD COLUMN lastModified timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP();

UPDATE Run SET
  created = (SELECT MIN(changeTime) FROM RunChangeLog WHERE runId = Run.runId),
  lastModified = (SELECT MAX(changeTime) FROM RunChangeLog WHERE runId = Run.runId),
  creator = (SELECT userId FROM RunChangeLog WHERE runId = Run.runId ORDER BY changeTime ASC LIMIT 1);

ALTER TABLE Run CHANGE COLUMN creator creator bigint NOT NULL;
ALTER TABLE Run ADD CONSTRAINT fk_run_creator FOREIGN KEY (creator) REFERENCES User (userId);

DROP VIEW IF EXISTS RunDerivedInfo;

ALTER TABLE SequencerPartitionContainer ADD COLUMN creator bigint;
ALTER TABLE SequencerPartitionContainer ADD COLUMN created timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP();
ALTER TABLE SequencerPartitionContainer ADD COLUMN lastModified timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP();

UPDATE SequencerPartitionContainer SET
  created = (SELECT MIN(changeTime) FROM SequencerPartitionContainerChangeLog WHERE containerId = SequencerPartitionContainer.containerId),
  lastModified = (SELECT MAX(changeTime) FROM SequencerPartitionContainerChangeLog WHERE containerId = SequencerPartitionContainer.containerId),
  creator = (SELECT userId FROM SequencerPartitionContainerChangeLog WHERE containerId = SequencerPartitionContainer.containerId ORDER BY changeTime ASC LIMIT 1);

ALTER TABLE SequencerPartitionContainer CHANGE COLUMN creator creator bigint NOT NULL;
ALTER TABLE SequencerPartitionContainer ADD CONSTRAINT fk_container_creator FOREIGN KEY (creator) REFERENCES User (userId);

DROP VIEW IF EXISTS ContainerDerivedInfo;

ALTER TABLE Box ADD COLUMN creator bigint;
ALTER TABLE Box ADD COLUMN created timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP();
ALTER TABLE Box ADD COLUMN lastModified timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP();

UPDATE Box SET
  created = (SELECT MIN(changeTime) FROM BoxChangeLog WHERE boxId = Box.boxId),
  lastModified = (SELECT MAX(changeTime) FROM BoxChangeLog WHERE boxId = Box.boxId),
  creator = (SELECT userId FROM BoxChangeLog WHERE boxId = Box.boxId ORDER BY changeTime ASC LIMIT 1);

ALTER TABLE Box CHANGE COLUMN creator creator bigint NOT NULL;
ALTER TABLE Box ADD CONSTRAINT fk_box_creator FOREIGN KEY (creator) REFERENCES User (userId);

DROP VIEW IF EXISTS BoxDerivedInfo;


-- delete_empcr_kits

DELETE FROM KitDescriptor WHERE kitType = 'EMPCR';


-- remove_alerts

DROP TABLE Alert;


