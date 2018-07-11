-- StartNoTest
DELIMITER //

DROP PROCEDURE IF EXISTS removeBoxItem//
CREATE PROCEDURE removeBoxItem(
  iUserId bigint(20),
  iTargetId bigint(20),
  iTargetType varchar(50)
) BEGIN
DECLARE oldBoxId bigint(20);
DECLARE oldPosition varchar(3);

INSERT INTO BoxChangeLog(boxId, columnsChanged, userId, message)
  SELECT boxId, 'contents', iUserId, CONCAT('Removed ', Sample.alias, ' (', Sample.name, ') from ', position, '.')
  FROM BoxPosition JOIN Sample ON sampleId = targetId
  WHERE sampleId = iTargetId AND targetType = iTargetType AND iTargetType = 'SAMPLE';
INSERT INTO BoxChangeLog(boxId, columnsChanged, userId, message)
  SELECT boxId, 'contents', iUserId, CONCAT('Removed ', Library.alias, ' (', Library.name, ') from ', position, '.')
  FROM BoxPosition JOIN Library ON libraryId = targetId
  WHERE libraryId = iTargetId AND targetType = iTargetType AND iTargetType = 'LIBRARY';
INSERT INTO BoxChangeLog(boxId, columnsChanged, userId, message)
  SELECT boxId, 'contents', iUserId, CONCAT('Removed ', LibraryDilution.name, ' (', Library.alias, ') from ', position, '.')
  FROM BoxPosition JOIN LibraryDilution ON dilutionId = targetId JOIN Library ON library_libraryId = libraryId
  WHERE dilutionId = iTargetId AND targetType = iTargetType AND iTargetType = 'DILUTION';
INSERT INTO BoxChangeLog(boxId, columnsChanged, userId, message)
  SELECT boxId, 'contents', iUserId, CONCAT('Removed ', Pool.alias, ' (', Pool.name, ') from ', position, '.')
  FROM BoxPosition JOIN Pool ON poolId = targetId
  WHERE poolId = iTargetId AND targetType = iTargetType AND iTargetType = 'POOL';

DELETE FROM BoxPosition
  WHERE targetId = iTargetId AND targetType = iTargetType;

SELECT 1;
END//
DELIMITER ;
-- EndNoTest
