-- tarReseq_to_tarSeq
SELECT CONSTRAINT_NAME INTO @constraint
    FROM INFORMATION_SCHEMA.KEY_COLUMN_USAGE
    WHERE TABLE_NAME = 'LibraryDilution' AND COLUMN_NAME = 'targetedResequencingId'
    AND REFERENCED_TABLE_NAME = 'TargetedResequencing' AND REFERENCED_COLUMN_NAME = 'targetedResequencingId';
SET @q = CONCAT('ALTER TABLE LibraryDilution DROP FOREIGN KEY ',@constraint);
PREPARE stmt FROM @q;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

ALTER TABLE TargetedResequencing CHANGE `targetedResequencingId` `targetedSequencingId` bigint NOT NULL AUTO_INCREMENT;
ALTER TABLE TargetedResequencing RENAME TO TargetedSequencing;
ALTER TABLE LibraryDilution CHANGE `targetedResequencingId` `targetedSequencingId` bigint DEFAULT NULL;
ALTER TABLE LibraryDilution ADD CONSTRAINT `FK_ld_targetedSequencing_targetedSequencingId` FOREIGN KEY (`targetedSequencingId`) REFERENCES TargetedSequencing (`targetedSequencingId`);


