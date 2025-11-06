SELECT DISTINCT r.alias
  ,r.instrumentId AS instrumentId
  ,r.runId
  ,r.filePath
  ,r.health
  ,r.startDate
  ,r.completionDate
  ,r.qcPassed
  ,r.qcDate
  ,r.qcUser qcUserId
  ,r.dataReview
  ,r.dataReviewDate
  ,r.dataReviewerId
  ,r.creator
  ,r.created
  ,r.lastModifier
  ,r.lastModified
  ,sp.readLength = sp.readLength2 AS paired
  ,sp.readLength AS read_length
  ,sp.name AS sequencingParameters
  ,sp.chemistry
  ,ri.runBasesMask AS runBasesMask
  ,ri.workflowType AS workflowType
  ,sk.partNumber AS sequencingKit
FROM Run AS r
LEFT JOIN SequencingParameters AS sp ON sp.parametersId = r.sequencingParameters_parametersId
LEFT JOIN KitDescriptor sk ON sk.kitDescriptorId = r.sequencingKitId
LEFT JOIN RunIllumina AS ri ON ri.runId = r.runId
