--StartNoTest
DELIMITER //
DROP PROCEDURE IF EXISTS insert_svr_if_not_exists//
CREATE PROCEDURE insert_svr_if_not_exists(svrParentId BIGINT(20), svrChildId BIGINT(20))
BEGIN
  SET @time = NOW();
  SELECT userId INTO @user FROM User WHERE loginName = 'admin';

  IF NOT EXISTS (SELECT 1 FROM SampleValidRelationship WHERE parentId = svrParentId AND childId = svrChildId) THEN
    INSERT INTO SampleValidRelationship (parentId, childId, createdBy, creationDate, updatedBy, lastUpdated, archived)
      VALUES (svrParentId, svrChildId, @user, @time, @user, @time, 0);
  END IF;
END //
DELIMITER ;

SELECT sampleClassId INTO @curls FROM SampleClass WHERE alias = 'Curls';
SELECT sampleClassId INTO @cvSlide FROM SampleClass WHERE alias = 'CV Slide';
SELECT sampleClassId INTO @dnaStock FROM SampleClass WHERE alias = 'gDNA (stock)';
SELECT sampleClassId INTO @rnaStock FROM SampleClass WHERE alias = 'whole RNA (stock)';

CALL insert_svr_if_not_exists(@curls, @dnaStock);
CALL insert_svr_if_not_exists(@curls, @rnaStock);
CALL insert_svr_if_not_exists(@cvSlide, @dnaStock);
CALL insert_svr_if_not_exists(@cvSlide, @rnaStock);

DROP PROCEDURE IF EXISTS insert_svr_if_not_exists;
--EndNoTest