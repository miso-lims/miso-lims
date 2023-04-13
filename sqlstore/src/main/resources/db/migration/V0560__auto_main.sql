-- Default_Targeted_Sequencing

ALTER TABLE Project ADD COLUMN `targetedSequencingId` BIGINT DEFAULT NULL AFTER referenceGenomeId;

ALTER TABLE Project ADD CONSTRAINT fk_Project_TargetedSequencing FOREIGN KEY (targetedSequencingId) REFERENCES TargetedSequencing (targetedSequencingId);


