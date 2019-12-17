CREATE TABLE SampleHierarchy (
  sampleId bigint(20) PRIMARY KEY,
  identityId bigint(20),
  tissueId bigint(20),
  CONSTRAINT fk_sampleHierarchy_sample FOREIGN KEY (sampleId) REFERENCES DetailedSample (sampleId),
  CONSTRAINT fk_sampleHierarchy_identity FOREIGN KEY (identityId) REFERENCES Identity (sampleId),
  CONSTRAINT fk_sampleHierarchy_tissue FOREIGN KEY (tissueId) REFERENCES SampleTissue (sampleId)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DELIMITER //

DROP FUNCTION IF EXISTS getParentTissueId//
CREATE FUNCTION getParentTissueId(pSampleId bigint(20)) RETURNS bigint(20)
BEGIN
  DECLARE vTissueId bigint(20);
  SET vTissueId = pSampleId;
  WHILE vTissueId IS NOT NULL AND NOT EXISTS (SELECT sampleId FROM SampleTissue WHERE sampleId = vTissueId) DO
    SELECT parentId INTO vTissueId FROM DetailedSample WHERE sampleId = vTissueId;
  END WHILE;
  RETURN vTissueId;
END//

DROP FUNCTION IF EXISTS getParentIdentityId//
CREATE FUNCTION getParentIdentityId(pSampleId bigint(20)) RETURNS bigint(20)
BEGIN
  DECLARE vIdentityId bigint(20);
  SET vIdentityId = pSampleId;
  WHILE vIdentityId IS NOT NULL AND NOT EXISTS (SELECT sampleId FROM Identity WHERE sampleId = vIdentityId) DO
    SELECT parentId INTO vIdentityId FROM DetailedSample WHERE sampleId = vIdentityId;
  END WHILE;
  RETURN vIdentityId;
END//

DELIMITER ;

INSERT INTO SampleHierarchy(sampleId, identityId, tissueId)
SELECT sampleId, getParentIdentityId(sampleId), getParentTissueId(sampleId)
FROM DetailedSample;

DROP FUNCTION getParentTissueId;
DROP FUNCTION getParentIdentityId;
