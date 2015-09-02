
ALTER TABLE `Sequencer_Partition` ADD COLUMN `dilutions_dilutionId` BIGINT(20) NOT NULL;
ALTER TABLE `Sequencer_Partition` ADD PRIMARY KEY (dilutions_dilutionId)
RENAME TABLE `Sequencer_Partition` TO `Sequencer_Partition_Dilution`;
