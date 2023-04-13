-- index_datetimes

-- add indexes on all default sort columns for faster querying
CREATE INDEX lastModified_Sample ON Sample (lastModified);
CREATE INDEX lastModified_Library ON Library (lastModified);
CREATE INDEX name_LibraryDilution ON LibraryDilution (name);
CREATE INDEX lastModified_Pool ON Pool (lastModified);
CREATE INDEX lastUpdated_PoolOrder ON PoolOrder (lastUpdated);
CREATE INDEX lastModified_Container ON SequencerPartitionContainer (lastModified);
CREATE INDEX startDate_Run ON Run (startDate);
CREATE INDEX startDate_ArrayRun ON ArrayRun (startDate);
CREATE INDEX name_Instrument ON Instrument (name);
CREATE INDEX name_Box ON Box (name);
CREATE INDEX name_StorageLocation ON StorageLocation (alias);
CREATE INDEX name_KitDescriptor ON KitDescriptor (name);


-- Library_Resources_Used

ALTER TABLE `LibraryDilution` ADD COLUMN `ngUsed` double DEFAULT NULL AFTER `concentration`;
ALTER TABLE `LibraryDilution` ADD COLUMN `volumeUsed` double DEFAULT NULL AFTER `volume`;


-- QCType_Corresponding_Field

ALTER TABLE QCType ADD COLUMN `correspondingField` varchar(50) NOT NULL DEFAULT 'NONE';
ALTER TABLE QCType ADD COLUMN `autoUpdateField` tinyint NOT NULL DEFAULT 0;


-- Dilution_Concentration_Units

 ALTER TABLE `LibraryDilution` ADD COLUMN `concentrationUnits` varchar(20) NOT NULL DEFAULT 'nM' AFTER `concentration`; 


