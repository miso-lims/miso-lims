-- If you change these method names or signatures, update the docs in `docs/_posts/2017-12-07-admin-guide.md`
-- StartNoTest
DELIMITER //

DROP PROCEDURE IF EXISTS addContainerModel//
CREATE PROCEDURE addContainerModel(
  iAlias varchar(255),
  iBarcode varchar(255),
  iPartitionCount int(11),
  iPlatform varchar(255),
  iInstrumentModel varchar(50)
) BEGIN
  DECLARE errorMessage varchar(300);
  IF NOT EXISTS (SELECT 1 FROM InstrumentModel WHERE platform = iPlatform AND alias = iInstrumentModel) THEN
    SET errorMessage = CONCAT('Instrument model ''', iInstrumentModel, ''' not found.');
    SIGNAL SQLSTATE '45000' SET message_text = errorMessage;
  END IF;
  IF NOT EXISTS (SELECT 1 FROM SequencingContainerModel WHERE alias = iAlias AND partitionCount = iPartitionCount 
      AND platformType = iPlatform) THEN
    INSERT INTO SequencingContainerModel (alias, identificationBarcode, partitionCount, platformType)
    VALUES (iAlias, iBarcode, iPartitionCount, iPlatform);
  END IF;
  IF NOT EXISTS (SELECT 1 FROM SequencingContainerModel_InstrumentModel scmim
    JOIN SequencingContainerModel scm ON scm.sequencingContainerModelId = scmim.sequencingContainerModelId
    JOIN InstrumentModel im ON im.instrumentModelId = scmim.instrumentModelId
    WHERE scm.alias = iAlias AND im.alias = iInstrumentModel
  ) THEN
    INSERT INTO SequencingContainerModel_InstrumentModel (sequencingContainerModelId, instrumentModelId)
    VALUES (
      (SELECT sequencingContainerModelId FROM SequencingContainerModel WHERE alias = iAlias),
      (SELECT instrumentModelId FROM InstrumentModel WHERE alias = iInstrumentModel)
    );
  END IF;
END//

DELIMITER ;
-- EndNoTest