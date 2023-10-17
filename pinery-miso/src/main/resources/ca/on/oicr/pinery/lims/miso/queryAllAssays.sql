SELECT assayId
  ,alias AS name
  ,description
  ,version
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
