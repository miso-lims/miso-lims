-- tat_targets
ALTER TABLE Assay
  ADD COLUMN caseTargetDays SMALLINT,
  ADD COLUMN receiptTargetDays SMALLINT,
  ADD COLUMN extractionTargetDays SMALLINT,
  ADD COLUMN libraryPreparationTargetDays SMALLINT,
  ADD COLUMN libraryQualificationTargetDays SMALLINT,
  ADD COLUMN fullDepthSequencingTargetDays SMALLINT,
  ADD COLUMN analysisReviewTargetDays SMALLINT,
  ADD COLUMN releaseApprovalTargetDays SMALLINT,
  ADD COLUMN releaseTargetDays SMALLINT;

