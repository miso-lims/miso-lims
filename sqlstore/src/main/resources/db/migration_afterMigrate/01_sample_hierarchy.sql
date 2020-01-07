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

DROP PROCEDURE IF EXISTS updateSampleHierarchy//
CREATE PROCEDURE updateSampleHierarchy(pSampleId bigint(20))
BEGIN
  DECLARE vTissueId bigint(20);
  DECLARE vIdentityId bigint(20);
  
  DECLARE vDone BOOLEAN DEFAULT FALSE;
  DECLARE vChildId bigint(20);
  DECLARE vCursor CURSOR FOR SELECT sampleId FROM DetailedSample WHERE parentId = pSampleId;
  DECLARE CONTINUE HANDLER FOR NOT FOUND SET vDone = TRUE;
  
  SELECT tissueId, identityId INTO vTissueId, vIdentityId FROM SampleHierarchy
  WHERE sampleId = (SELECT parentId FROM DetailedSample WHERE sampleId = pSampleId);
  
  IF vTissueId IS NULL THEN
    SET vTissueId = getParentTissueId(pSampleId);
  END IF;
  
  IF vIdentityId IS NULL THEN
    IF vTissueId IS NOT NULL THEN
      SET vIdentityId = getParentIdentityId(vTissueId);
    ELSE
      SET vIdentityId = getParentIdentityId(pSampleId);
    END IF;
  END IF;
  
  INSERT INTO SampleHierarchy(sampleId, identityId, tissueId)
  VALUES (pSampleId, vIdentityId, vTissueId)
  ON DUPLICATE KEY UPDATE identityId = VALUES(identityId), tissueId = VALUES(tissueId);
  
  OPEN vCursor;
  read_loop: LOOP
    FETCH vCursor INTO vChildId;
    IF vDone THEN
      LEAVE read_loop;
    END IF;
    CALL updateSampleHierarchy(vChildId);
  END LOOP;
  CLOSE vCursor;
END//

DELIMITER ;
