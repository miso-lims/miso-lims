-- StartNoTest
DELIMITER //

DROP PROCEDURE IF EXISTS moveBoxItem//
CREATE PROCEDURE moveBoxItem(
  iUserId bigint(20),
  iBoxId bigint(20),
  iTargetId bigint(20),
  iTargetType varchar(50),
  iPosition varchar(3)
) BEGIN
DECLARE oldBoxId bigint(20);
DECLARE oldPosition varchar(3);

-- If there is something else there, remove it.
INSERT INTO BoxChangeLog(boxId, columnsChanged, userId, message)
  SELECT iBoxId, 'contents', iUserId, CONCAT('Removed ', Sample.alias, ' (', Sample.name, ') from ', iPosition, '.')
  FROM BoxPosition JOIN Sample ON sampleId = targetId
  WHERE boxId = iBoxId AND position = iPosition AND targetType = 'SAMPLE';
INSERT INTO BoxChangeLog(boxId, columnsChanged, userId, message)
  SELECT iBoxId, 'contents', iUserId, CONCAT('Removed ', Library.alias, ' (', Library.name, ') from ', iPosition, '.')
  FROM BoxPosition JOIN Library ON libraryId = targetId
  WHERE boxId = iBoxId AND position = iPosition AND targetType = 'LIBRARY';
INSERT INTO BoxChangeLog(boxId, columnsChanged, userId, message)
  SELECT iBoxId, 'contents', iUserId, CONCAT('Removed ', LibraryDilution.name, ' (', Library.alias, ') from ', iPosition, '.')
  FROM BoxPosition JOIN LibraryDilution ON dilutionId = targetId JOIN Library ON library_libraryId = libraryId
  WHERE boxId = iBoxId AND position = iPosition AND targetType = 'DILUTION';
INSERT INTO BoxChangeLog(boxId, columnsChanged, userId, message)
  SELECT iBoxId, 'contents', iUserId, CONCAT('Removed ', Pool.alias, ' (', Pool.name, ') from ', iPosition, '.')
  FROM BoxPosition JOIN Pool ON poolId = targetId
  WHERE boxId = iBoxId AND position = iPosition AND targetType = 'POOL';

DELETE FROM BoxPosition WHERE boxId = iBoxId AND position = iPosition;

-- Preserver where this item was stored before.
SELECT boxId, position INTO oldBoxId, oldPosition FROM BoxPosition WHERE targetId = iTargetId AND targetType = iTargetType;
DELETE FROM BoxPosition WHERE targetId = iTargetId AND targetType = iTargetType;

-- Add the new item
INSERT INTO BoxPosition(boxId, targetId, targetType, position) VALUES (iBoxId, iTargetId, iTargetType, iPosition);

-- Figure out how to log this
IF oldBoxId IS NOT NULL AND oldBoxId = iBoxId THEN
-- Move within a box
INSERT INTO BoxChangeLog(boxId, columnsChanged, userId, message)
  SELECT iBoxId, 'contents', iUserId, CONCAT('Moved ', Sample.alias, ' (', Sample.name, ') from ', oldPosition, ' to ', iPosition, '.')
  FROM Sample
  WHERE sampleId = iTargetId AND iTargetType = 'SAMPLE';
INSERT INTO BoxChangeLog(boxId, columnsChanged, userId, message)
  SELECT iBoxId, 'contents', iUserId, CONCAT('Moved ', Library.alias, ' (', Library.name, ') from ', oldPosition, ' to ', iPosition, '.')
  FROM Library
  WHERE libraryId = iTargetId AND iTargetType = 'LIBRARY';
INSERT INTO BoxChangeLog(boxId, columnsChanged, userId, message)
  SELECT iBoxId, 'contents', iUserId, CONCAT('Moved ', LibraryDilution.name, ' (', Library.alias, ') from ', oldPosition, ' to ', iPosition, '.')
  FROM LibraryDilution JOIN Library ON library_libraryId = libraryId
  WHERE dilutionId = iTargetId AND iTargetType = 'DILUTION';
INSERT INTO BoxChangeLog(boxId, columnsChanged, userId, message)
  SELECT iBoxId, 'contents', iUserId, CONCAT('Moved ', Pool.alias, ' (', Pool.name, ') from ', oldPosition, ' to ', iPosition, '.')
  FROM Pool
  WHERE poolId = iTargetId AND iTargetType = 'POOL';

ELSEIF oldBoxId IS NOT NULL THEN
-- Move to a new box
INSERT INTO BoxChangeLog(boxId, columnsChanged, userId, message)
  SELECT oldBoxId, 'contents', iUserId, CONCAT('Moved ', Sample.alias, ' (', Sample.name, ') from ', oldPosition, ' to ', Box.alias, ' (', Box.name, ') ', iPosition, '.')
  FROM Sample, Box
  WHERE boxId = iBoxId AND iTargetType = 'SAMPLE' AND sampleId = iTargetId;
INSERT INTO BoxChangeLog(boxId, columnsChanged, userId, message)
SELECT iBoxId, 'contents', iUserId, CONCAT('Moved ', Sample.alias, ' (', Sample.name, ') from ', Box.alias, ' (', Box.name, ') ', oldPosition, ' to ', iPosition, '.')
  FROM Sample, Box
  WHERE boxId = oldBoxId AND iTargetType = 'SAMPLE' AND sampleId = iTargetId;

INSERT INTO BoxChangeLog(boxId, columnsChanged, userId, message)
  SELECT oldBoxId, 'contents', iUserId, CONCAT('Moved ', Library.alias, ' (', Library.name, ') from ', oldPosition, ' to ', Box.alias, ' (', Box.name, ') ', iPosition, '.')
  FROM Library, Box
  WHERE boxId = iBoxId AND iTargetType = 'LIBRARY' AND libraryId = iTargetId;
INSERT INTO BoxChangeLog(boxId, columnsChanged, userId, message)
SELECT iBoxId, 'contents', iUserId, CONCAT('Moved ', Library.alias, ' (', Library.name, ') from ', Box.alias, ' (', Box.name, ') ', oldPosition, ' to ', iPosition, '.')
  FROM Library, Box
  WHERE boxId = oldBoxId AND iTargetType = 'LIBRARY' AND libraryId = iTargetId;

INSERT INTO BoxChangeLog(boxId, columnsChanged, userId, message)
  SELECT oldBoxId, 'contents', iUserId, CONCAT('Moved ', LibraryDilution.name, ' (', Library.alias, ') from ', oldPosition, ' to ', Box.alias, ' (', Box.name, ') ', iPosition, '.')
  FROM LibraryDilution JOIN Library ON library_libraryId = libraryId, Box
  WHERE boxId = iBoxId AND iTargetType = 'DILUTION' AND dilutionId = iTargetId;
INSERT INTO BoxChangeLog(boxId, columnsChanged, userId, message)
SELECT iBoxId, 'contents', iUserId, CONCAT('Moved ', LibraryDilution.name, ' (', Library.alias, ') from ', Box.alias, ' (', Box.name, ') ', oldPosition, ' to ', iPosition, '.')
  FROM LibraryDilution JOIN Library ON library_libraryId = libraryId, Box
  WHERE boxId = oldBoxId AND iTargetType = 'DILUTION' AND dilutionId = iTargetId;

INSERT INTO BoxChangeLog(boxId, columnsChanged, userId, message)
  SELECT oldBoxId, 'contents', iUserId, CONCAT('Moved ', Pool.alias, ' (', Pool.name, ') from ', oldPosition, ' to ', Box.alias, ' (', Box.name, ') ', iPosition, '.')
  FROM Pool, Box
  WHERE boxId = iBoxId AND iTargetType = 'POOL' AND poolId = iTargetId;
INSERT INTO BoxChangeLog(boxId, columnsChanged, userId, message)
SELECT iBoxId, 'contents', iUserId, CONCAT('Moved ', Pool.alias, ' (', Pool.name, ') from ', Box.alias, ' (', Box.name, ') ', oldPosition, ' to ', iPosition, '.')
  FROM Pool, Box
  WHERE boxId = oldBoxId AND iTargetType = 'POOL' AND poolId = iTargetId;

ELSE
-- Put loose item into a box
INSERT INTO BoxChangeLog(boxId, columnsChanged, userId, message)
  SELECT iBoxId, 'contents', iUserId, CONCAT('Added ', Sample.alias, ' (', Sample.name, ') to ', iPosition, '.')
  FROM Sample
  WHERE sampleId = iTargetId AND iTargetType = 'SAMPLE';
INSERT INTO BoxChangeLog(boxId, columnsChanged, userId, message)
  SELECT iBoxId, 'contents', iUserId, CONCAT('Added ', Library.alias, ' (', Library.name, ') to ', iPosition, '.')
  FROM Library
  WHERE libraryId = iTargetId AND iTargetType = 'LIBRARY';
INSERT INTO BoxChangeLog(boxId, columnsChanged, userId, message)
  SELECT iBoxId, 'contents', iUserId, CONCAT('Added ', LibraryDilution.name, ' (', Library.alias, ') to ', iPosition, '.')
  FROM LibraryDilution JOIN Library ON library_libraryId = libraryId
  WHERE dilutionId = iTargetId AND iTargetType = 'DILUTION';
INSERT INTO BoxChangeLog(boxId, columnsChanged, userId, message)
  SELECT iBoxId, 'contents', iUserId, CONCAT('Added ', Pool.alias, ' (', Pool.name, ') to ', iPosition, '.')
  FROM Pool
  WHERE poolId = iTargetId AND iTargetType = 'POOL';

END IF;

SELECT 1;
END//
DELIMITER ;
-- EndNoTest
