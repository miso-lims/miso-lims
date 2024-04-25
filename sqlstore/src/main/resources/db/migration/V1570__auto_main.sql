-- fix_sample_hierarchy
DELIMITER //

CREATE FUNCTION getParentIdByDiscriminator(pSampleId bigint, pDiscriminator varchar(50))
  RETURNS bigint NOT DETERMINISTIC READS SQL DATA
BEGIN
  DECLARE vSampleId bigint;
  DECLARE vDiscriminator varchar(50);
  SET vSampleId = pSampleId;
  SELECT discriminator INTO vDiscriminator FROM Sample WHERE sampleId = pSampleId;
  WHILE vSampleId IS NOT NULL AND vDiscriminator <> pDiscriminator DO
    SELECT p.sampleId, p.discriminator INTO vSampleId, vDiscriminator
    FROM Sample c
    LEFT JOIN Sample p ON p.sampleId = c.parentId
    WHERE c.sampleId = vSampleId;
  END WHILE;
  RETURN vSampleId;
END//

CREATE PROCEDURE updateSampleHierarchy(pSampleId bigint)
BEGIN
  DECLARE vTissueId bigint;
  DECLARE vIdentityId bigint;
  DECLARE vSampleDiscriminator varchar(50);
  
  DECLARE vDone BOOLEAN DEFAULT FALSE;
  DECLARE vChildId bigint;
  DECLARE vCursor CURSOR FOR SELECT sampleId FROM Sample WHERE parentId = pSampleId;
  DECLARE CONTINUE HANDLER FOR NOT FOUND SET vDone = TRUE;
  
  SELECT tissueId, identityId INTO vTissueId, vIdentityId FROM SampleHierarchy
  WHERE sampleId = (SELECT parentId FROM Sample WHERE sampleId = pSampleId);
  
  SELECT discriminator INTO vSampleDiscriminator FROM Sample WHERE sampleId = pSampleId;

  IF vSampleDiscriminator = 'Tissue' THEN
    SET vTissueId = pSampleId;
  ELSEIF vTissueId IS NULL THEN
    SET vTissueId = getParentIdByDiscriminator(pSampleId, 'Tissue');
  END IF;
  
  IF vSampleDiscriminator = 'Identity' THEN
    SET vIdentityId = pSampleId;
  ELSEIF vIdentityId IS NULL THEN
    IF vTissueId IS NOT NULL THEN
      SET vIdentityId = getParentIdByDiscriminator(vTissueId, 'Identity');
    ELSE
      SET vIdentityId = getParentIdByDiscriminator(pSampleId, 'Identity');
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

CREATE PROCEDURE rebuildSampleHierarchy()
BEGIN
  DECLARE vDone BOOLEAN DEFAULT FALSE;
  DECLARE vSampleId bigint;
  DECLARE vCursor CURSOR FOR
    SELECT s.sampleId FROM Sample s
    JOIN Sample p ON p.sampleId = s.parentId
    WHERE s.discriminator = 'Tissue'
    AND p.discriminator = 'Tissue';
  DECLARE CONTINUE HANDLER FOR NOT FOUND SET vDone = TRUE;

  OPEN vCursor;
  read_loop: LOOP
    FETCH vCursor INTO vSampleId;
    IF vDone THEN
      LEAVE read_loop;
    END IF;
    CALL updateSampleHierarchy(vSampleId);
  END LOOP;
  CLOSE vCursor;
END//

DELIMITER ;

CALL rebuildSampleHierarchy();

DROP PROCEDURE rebuildSampleHierarchy;
DROP PROCEDURE updateSampleHierarchy;
DROP FUNCTION getParentIdByDiscriminator;
