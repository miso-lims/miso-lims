-- StartNoTest
DELIMITER //

DROP PROCEDURE IF EXISTS addTargetedSequencing//
CREATE PROCEDURE addTargetedSequencing(
  iAlias varchar(255),
  iDescription varchar(255),
  iKitName varchar(255),
  iArchived tinyint(1)
) BEGIN
  DECLARE errorMessage varchar(300);
  DECLARE kitId, createUser, exitingTargetedSequencingId, newTargetedSequencingId bigint(20);
  DECLARE createTime datetime DEFAULT CURRENT_TIMESTAMP;
  
  SELECT kitDescriptorId INTO kitId FROM KitDescriptor WHERE name = iKitName;
  IF kitId IS NULL THEN
    SET errorMessage = CONCAT('KitDescriptor ''', iKitName, ''' not found.');
    SIGNAL SQLSTATE '45000' SET message_text = errorMessage;
  END IF;
  
  IF NOT EXISTS (
    SELECT 1 FROM TargetedSequencing AS t
    JOIN TargetedSequencing_KitDescriptor AS tk ON t.targetedSequencingId = tk.targetedSequencingId
    WHERE t.alias = iAlias 
    AND tk.kitDescriptorId = kitId
    ) THEN
    SELECT targetedSequencingId INTO exitingTargetedSequencingId FROM TargetedSequencing WHERE alias = iAlias;
    IF exitingTargetedSequencingId IS NULL THEN
       SET createUser = getAdminUserId();
       INSERT INTO TargetedSequencing(alias, description, archived, createdBy, creationDate, updatedBy, lastUpdated)
       VALUES (iAlias, iDescription, iArchived, createUser, createTime, createUser, createTime);
       SET newTargetedSequencingId = LAST_INSERT_ID();
       INSERT INTO TargetedSequencing_KitDescriptor(targetedSequencingId, kitDescriptorId)
       VALUES(newTargetedSequencingId, kitId);
    ELSE
       INSERT INTO TargetedSequencing_KitDescriptor(targetedSequencingId, kitDescriptorId)
       VALUES(exitingTargetedSequencingId, kitId);
    END IF;
  END IF;
END//

DELIMITER ;
-- EndNoTest
