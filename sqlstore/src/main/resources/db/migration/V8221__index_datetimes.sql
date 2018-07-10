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