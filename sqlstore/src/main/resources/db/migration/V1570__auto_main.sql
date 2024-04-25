-- fix_sample_hierarchy
DELIMITER //

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

