ALTER TABLE Run_Partition_LibraryAliquot
  ADD COLUMN dataReview BOOLEAN,
  ADD COLUMN dataReviewerId bigint(20),
  ADD COLUMN dataReviewDate DATE,
  ADD CONSTRAINT fk_run_aliquot_dataReviewer FOREIGN KEY (dataReviewerId) REFERENCES User (userId);

UPDATE Run_Partition_LibraryAliquot lib
JOIN Run r ON r.runId = lib.runId
SET
  lib.dataReview = r.dataReview,
  lib.dataReviewerId = r.dataReviewerId,
  lib.dataReviewDate = r.dataReviewDate
WHERE lib.statusId IS NOT NULL;
