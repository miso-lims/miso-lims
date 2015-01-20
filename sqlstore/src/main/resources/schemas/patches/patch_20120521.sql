USE lims;

ALTER TABLE `lims`.`Sequencer_Partition` ADD COLUMN `dilutions_dilutionId` BIGINT(20) NOT NULL;
ALTER TABLE `lims`.`Sequencer_Partition` ADD PRIMARY KEY (dilutions_dilutionId)
RENAME TABLE `lims`.`Sequencer_Partition` TO `lims`.`Sequencer_Partition_Dilution`;