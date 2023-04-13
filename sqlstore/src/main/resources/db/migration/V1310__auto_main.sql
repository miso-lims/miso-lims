-- archive_kits
ALTER TABLE KitDescriptor ADD COLUMN archived BOOLEAN NOT NULL DEFAULT FALSE;

-- run_data_review
ALTER TABLE Run_Partition_LibraryAliquot
  ADD COLUMN dataReview BOOLEAN,
  ADD COLUMN dataReviewerId bigint,
  ADD COLUMN dataReviewDate DATE,
  ADD CONSTRAINT fk_run_aliquot_dataReviewer FOREIGN KEY (dataReviewerId) REFERENCES User (userId);

UPDATE Run_Partition_LibraryAliquot lib
JOIN Run r ON r.runId = lib.runId
SET
  lib.dataReview = r.dataReview,
  lib.dataReviewerId = r.dataReviewerId,
  lib.dataReviewDate = r.dataReviewDate
WHERE lib.statusId IS NOT NULL;

-- storage_labels
CREATE TABLE StorageLabel (
  labelId bigint NOT NULL AUTO_INCREMENT,
  label varchar(100) NOT NULL,
  PRIMARY KEY (labelId)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

ALTER TABLE StorageLocation
  ADD COLUMN labelId bigint,
  ADD CONSTRAINT fk_storageLocation_label FOREIGN KEY (labelId) REFERENCES StorageLabel (labelId);

