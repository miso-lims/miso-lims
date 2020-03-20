ALTER TABLE Library ADD COLUMN kitLot varchar(100);
ALTER TABLE Run ADD COLUMN sequencingKitLot varchar(100);
ALTER TABLE SequencerPartitionContainer ADD COLUMN clusteringKitLot varchar(100);
ALTER TABLE SequencerPartitionContainer ADD COLUMN multiplexingKitLot varchar(100);
