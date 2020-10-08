-- run_library_qc
ALTER TABLE Run_Partition_LibraryAliquot ADD COLUMN qcPassed BOOLEAN;
ALTER TABLE Run_Partition_LibraryAliquot ADD COLUMN qcNote varchar(255);

-- run_index_strategy
ALTER TABLE Run ADD COLUMN dataManglingPolicy varchar(50);

