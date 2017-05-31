UPDATE Sample
  SET qcPassed = (SELECT status FROM DetailedQcStatus JOIN DetailedSample ON DetailedQcStatus.detailedQcStatusId = DetailedSample.detailedQcStatusId WHERE DetailedSample.sampleId = Sample.sampleId)
  WHERE sampleId IN (SELECT sampleId FROM DetailedSample WHERE detailedQcStatusId IS NOT NULL);
