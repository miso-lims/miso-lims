DELIMITER //

DROP TRIGGER IF EXISTS AssayChange//
CREATE TRIGGER AssayChange BEFORE UPDATE ON Assay
FOR EACH ROW
  BEGIN
    DECLARE log_message longtext;
    SET log_message = CONCAT_WS(', ',
      makeChangeMessage('alias', OLD.alias, NEW.alias),
      makeChangeMessage('version', OLD.version, NEW.version),
      makeChangeMessage('description', OLD.description, NEW.description),
      makeChangeMessage('archived', booleanToString(OLD.archived), booleanToString(NEW.archived)),
      makeChangeMessage('case target days', OLD.caseTargetDays, NEW.caseTargetDays),
      makeChangeMessage('receipt target days', OLD.receiptTargetDays, NEW.receiptTargetDays),
      makeChangeMessage('extraction target days', OLD.extractionTargetDays, NEW.extractionTargetDays),
      makeChangeMessage('library preparation target days', OLD.libraryPreparationTargetDays, NEW.libraryPreparationTargetDays),
      makeChangeMessage('library qualification target days', OLD.libraryQualificationTargetDays, NEW.libraryQualificationTargetDays),
      makeChangeMessage('full depth sequencing target days', OLD.fullDepthSequencingTargetDays, NEW.fullDepthSequencingTargetDays),
      makeChangeMessage('analysis review target days', OLD.analysisReviewTargetDays, NEW.analysisReviewTargetDays),
      makeChangeMessage('release approval target days', OLD.releaseApprovalTargetDays, NEW.releaseApprovalTargetDays),
      makeChangeMessage('release target days', OLD.releaseTargetDays, NEW.releaseTargetDays),
      makeChangeMessage('draft', booleanToString(OLD.draft), booleanToString(NEW.draft))
    );
    IF log_message IS NOT NULL AND log_message <> '' THEN
      INSERT INTO AssayChangeLog(assayId, columnsChanged, userId, message, changeTime) VALUES (
      NEW.assayId,
        COALESCE(CONCAT_WS(',',
          makeChangeColumn('alias', OLD.alias, NEW.alias),
          makeChangeColumn('version', OLD.version, NEW.version),
          makeChangeColumn('description', OLD.description, NEW.description),
          makeChangeColumn('archived', OLD.archived, NEW.archived),
          makeChangeColumn('caseTargetDays', OLD.caseTargetDays, NEW.caseTargetDays),
          makeChangeColumn('receiptTargetDays', OLD.receiptTargetDays, NEW.receiptTargetDays),
          makeChangeColumn('extractionTargetDays', OLD.extractionTargetDays, NEW.extractionTargetDays),
          makeChangeColumn('libraryPreparationTargetDays', OLD.libraryPreparationTargetDays, NEW.libraryPreparationTargetDays),
          makeChangeColumn('libraryQualificationTargetDays', OLD.libraryQualificationTargetDays, NEW.libraryQualificationTargetDays),
          makeChangeColumn('fullDepthSequencingTargetDays', OLD.fullDepthSequencingTargetDays, NEW.fullDepthSequencingTargetDays),
          makeChangeColumn('analysisReviewTargetDays', OLD.analysisReviewTargetDays, NEW.analysisReviewTargetDays),
          makeChangeColumn('releaseApprovalTargetDays', OLD.releaseApprovalTargetDays, NEW.releaseApprovalTargetDays),
          makeChangeColumn('releaseTargetDays', OLD.releaseTargetDays, NEW.releaseTargetDays),
          makeChangeColumn('draft', OLD.draft, NEW.draft)
        ), ''),
        NEW.lastModifier,
        log_message,
        NEW.lastModified
      );
    END IF;
  END//

DROP TRIGGER IF EXISTS AssayInsert//
CREATE TRIGGER AssayInsert AFTER INSERT ON Assay
FOR EACH ROW
  INSERT INTO AssayChangeLog(assayId, columnsChanged, userId, message, changeTime) VALUES (
    NEW.assayId,
    '',
    NEW.lastModifier,
    'Assay created.',
    NEW.lastModified)//

DROP TRIGGER IF EXISTS AssayTestInsert//
CREATE TRIGGER AssayTestInsert AFTER INSERT ON Assay_AssayTest
FOR EACH ROW
  BEGIN
    IF TIMESTAMPDIFF(HOUR, (SELECT created FROM Assay WHERE assayId = NEW.assayId), NOW()) > 0 THEN
      INSERT INTO AssayChangeLog(assayId, columnsChanged, userId, message, changeTime)
        SELECT
          NEW.assayId,
          'tests',
          lastModifier,
          CONCAT('Added test ', (SELECT alias FROM AssayTest WHERE testId = NEW.testId)),
          lastModified
        FROM Assay
        WHERE assayId = NEW.assayId;
    END IF;
  END//

DROP TRIGGER IF EXISTS AssayTestDelete//
CREATE TRIGGER AssayTestDelete AFTER DELETE ON Assay_AssayTest
FOR EACH ROW
  INSERT INTO AssayChangeLog(assayId, columnsChanged, userId, message, changeTime)
    SELECT
      OLD.assayId,
      'tests',
      lastModifier,
      CONCAT('Removed test ', (SELECT alias FROM AssayTest WHERE testId = OLD.testId)),
      lastModified
    FROM Assay
    WHERE assayId = OLD.assayId//

-- Message example: Added metric '<alias>' with min=<min>, max=<max> (<category> - <subcategory>)
DROP TRIGGER IF EXISTS AssayMetricInsert//
CREATE TRIGGER AssayMetricInsert AFTER INSERT ON Assay_Metric
FOR EACH ROW
  BEGIN
    IF TIMESTAMPDIFF(HOUR, (SELECT created FROM Assay WHERE assayId = NEW.assayId), NOW()) > 0 THEN
      INSERT INTO AssayChangeLog(assayId, columnsChanged, userId, message, changeTime)
        SELECT
          NEW.assayId,
          'metrics',
          lastModifier,
          (
            SELECT CONCAT(
              'Added metric ''',
              m.alias,
              '''',
              IF(NEW.minimumThreshold IS NOT NULL OR NEW.maximumThreshold IS NOT NULL, ' with ', ''),
              IF(NEW.minimumThreshold IS NOT NULL, CONCAT('min=', NEW.minimumThreshold), ''),
              IF(NEW.minimumThreshold IS NOT NULL AND NEW.maximumThreshold IS NOT NULL, ', ', ''),
              IF(NEW.maximumThreshold IS NOT NULL, CONCAT('max=', NEW.maximumThreshold), ''),
              ' (',
              LOWER(REPLACE(m.category, '_', ' ')),
              IF(m.subcategoryId IS NOT NULL, CONCAT(' - ', ms.alias), ''),
              ')'
            )
            FROM Metric m
            LEFT JOIN MetricSubcategory ms ON ms.subcategoryId = m.subcategoryId
            WHERE m.metricId = NEW.metricId
          ),
          lastModified
        FROM Assay
        WHERE assayId = NEW.assayId;
    END IF;
  END//

DROP TRIGGER IF EXISTS AssayMetricDelete//
CREATE TRIGGER AssayMetricDelete AFTER DELETE ON Assay_Metric
FOR EACH ROW
  INSERT INTO AssayChangeLog(assayId, columnsChanged, userId, message, changeTime)
    SELECT
      OLD.assayId,
      'metrics',
      lastModifier,
      (
        SELECT CONCAT(
          'Removed metric ''',
          m.alias,
          '''',
          IF(OLD.minimumThreshold IS NOT NULL OR OLD.maximumThreshold IS NOT NULL, ' with ', ''),
          IF(OLD.minimumThreshold IS NOT NULL, CONCAT('min=', OLD.minimumThreshold), ''),
          IF(OLD.minimumThreshold IS NOT NULL AND OLD.maximumThreshold IS NOT NULL, ', ', ''),
          IF(OLD.maximumThreshold IS NOT NULL, CONCAT('max=', OLD.maximumThreshold), ''),
          ' (',
          LOWER(REPLACE(m.category, '_', ' ')),
          IF(m.subcategoryId IS NOT NULL, CONCAT(' - ', ms.alias), ''),
          ')'
        )
        FROM Metric m
        LEFT JOIN MetricSubcategory ms ON ms.subcategoryId = m.subcategoryId
        WHERE m.metricId = OLD.metricId
      ),
      lastModified
    FROM Assay
    WHERE assayId = OLD.assayId//

-- Message example: Metric '<alias>' min: 1 → 2, max: 3 → 4 (<category> - <subcategory>)
DROP TRIGGER IF EXISTS AssayMetricChange//
CREATE TRIGGER AssayMetricChange BEFORE UPDATE ON Assay_Metric
FOR EACH ROW
  BEGIN
    DECLARE log_message longtext;
    SET log_message = CONCAT_WS(', ',
      makeChangeMessage('min', OLD.minimumThreshold, NEW.minimumThreshold),
      makeChangeMessage('max', OLD.maximumThreshold, NEW.maximumThreshold)
    );
    IF log_message IS NOT NULL AND log_message <> '' THEN
      SET log_message = (
        SELECT CONCAT(
          'Metric ''',
          m.alias,
          ''' ',
          log_message,
          ' (',
          LOWER(REPLACE(m.category, '_', ' ')),
          IF(m.subcategoryId IS NOT NULL, CONCAT(' - ', ms.alias), ''),
          ')'
        )
        FROM Metric m
        LEFT JOIN MetricSubcategory ms ON ms.subcategoryId = m.subcategoryId
        WHERE m.metricId = NEW.metricId
      );

      INSERT INTO AssayChangeLog(assayId, columnsChanged, userId, message, changeTime)
      SELECT
        NEW.assayId,
        COALESCE(CONCAT_WS(',',
          makeChangeColumn('metric minimumThreshold', OLD.minimumThreshold, NEW.minimumThreshold),
          makeChangeColumn('metric maximumThreshold', OLD.maximumThreshold, NEW.maximumThreshold)
        ), ''),
        lastModifier,
        log_message,
        lastModified
      FROM Assay WHERE assayId = NEW.assayId;
    END IF;
  END//

DELIMITER ;
