SELECT rqc.requisitionId
  ,qct.name
  ,rqc.results
  ,rqc.date
  ,rqc.creator
FROM RequisitionQc rqc
JOIN QCType qct ON qct.qcTypeId = rqc.type
