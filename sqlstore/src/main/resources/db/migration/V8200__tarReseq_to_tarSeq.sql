--StartNoTest because FK was never named in the original migration
ALTER TABLE LibraryDilution DROP FOREIGN KEY `LibraryDilution_ibfk_1`;
--EndNoTest
ALTER TABLE TargetedResequencing CHANGE `targetedResequencingId` `targetedSequencingId` BIGINT(20) NOT NULL AUTO_INCREMENT;
ALTER TABLE TargetedResequencing RENAME TO TargetedSequencing;
ALTER TABLE LibraryDilution CHANGE `targetedResequencingId` `targetedSequencingId` BIGINT(20) DEFAULT NULL;
ALTER TABLE LibraryDilution ADD CONSTRAINT `FK_ld_targetedSequencing_targetedSequencingId` FOREIGN KEY (`targetedSequencingId`) REFERENCES TargetedSequencing (`targetedSequencingId`);