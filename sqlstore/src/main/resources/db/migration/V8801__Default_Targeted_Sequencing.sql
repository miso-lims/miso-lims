ALTER TABLE Project ADD COLUMN `targetedSequencingId` BIGINT (20) DEFAULT NULL AFTER referenceGenomeId;

ALTER TABLE Project ADD CONSTRAINT fk_Project_TargetedSequencing FOREIGN KEY (targetedSequencingId) REFERENCES TargetedSequencing (targetedSequencingId);
