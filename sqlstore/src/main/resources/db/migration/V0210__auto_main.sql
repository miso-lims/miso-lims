-- delete_library_qc_insert_size

ALTER TABLE LibraryQC DROP COLUMN insertSize;


-- spc_platform_not_null

ALTER TABLE SequencerPartitionContainer CHANGE platform platform bigint(20) NOT NULL;


