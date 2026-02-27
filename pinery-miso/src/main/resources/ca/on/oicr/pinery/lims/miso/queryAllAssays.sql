SELECT assayId
  ,alias AS name
  ,description
  ,version
  ,archived
  ,caseTargetDays
  ,receiptTargetDays
  ,extractionTargetDays
  ,libraryPreparationTargetDays
  ,libraryQualificationTargetDays
  ,fullDepthSequencingTargetDays
  ,analysisReviewTargetDays
  ,releaseApprovalTargetDays
  ,releaseTargetDays
FROM Assay
