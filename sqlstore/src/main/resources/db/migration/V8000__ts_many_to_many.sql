CREATE TABLE `TargetedSequencingTemp` (
  `targetedSequencingId` bigint(20) NOT NULL AUTO_INCREMENT,
  `alias` varchar(255) NOT NULL,
  `description` varchar(255) NOT NULL,
  `archived` bit(1) NOT NULL DEFAULT b'0',
  `createdBy` bigint(20) NOT NULL,
  `creationDate` datetime NOT NULL,
  `updatedBy` bigint(20) NOT NULL,
  `lastUpdated` datetime NOT NULL,
  PRIMARY KEY (`targetedSequencingId`),
  UNIQUE KEY `UK_TargetedResequencing_a_kdi2` (`alias`),
  KEY `FK_TargetedResequencing_cb2` (`createdBy`),
  KEY `FK_TargetedResequencing_ub2` (`updatedBy`),
  CONSTRAINT `FK_TargetedResequencing_cb2` FOREIGN KEY (`createdBy`) REFERENCES `User` (`userId`),
  CONSTRAINT `FK_TargetedResequencing_ub2` FOREIGN KEY (`updatedBy`) REFERENCES `User` (`userId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE TargetedSequencing_KitDescriptor (
  targetedSequencingId bigint(20) NOT NULL,
  kitDescriptorId bigint(20) NOT NULL,
  PRIMARY KEY (targetedSequencingId,kitDescriptorId),
  CONSTRAINT TK_KitDescriptor_FK FOREIGN KEY (kitDescriptorId) REFERENCES KitDescriptor (kitDescriptorId)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

INSERT INTO TargetedSequencingTemp(targetedSequencingId, alias, description, archived, createdBy, creationDate, updatedBy, lastUpdated)
SELECT targetedSequencingId, alias, description, archived, createdBy, creationDate, updatedBy, lastUpdated FROM TargetedSequencing;

INSERT INTO TargetedSequencing_KitDescriptor(targetedSequencingId, kitDescriptorId)
SELECT targetedSequencingId, kitDescriptorId FROM TargetedSequencing;

-- StartNoTest

-- Delete foreign keys if they exist.

set @var=if((SELECT true FROM information_schema.TABLE_CONSTRAINTS WHERE
            CONSTRAINT_SCHEMA = DATABASE() AND
            TABLE_NAME        = 'LibraryDilution' AND
            CONSTRAINT_NAME   = 'FK_ld_targetedSequencing_targetedSequencingId' AND
            CONSTRAINT_TYPE   = 'FOREIGN KEY') = true,'ALTER TABLE LibraryDilution
            DROP FOREIGN KEY FK_ld_targetedSequencing_targetedSequencingId','select 1');

prepare stmt from @var;
execute stmt;
deallocate prepare stmt;

set @var=if((SELECT true FROM information_schema.TABLE_CONSTRAINTS WHERE
            CONSTRAINT_SCHEMA = DATABASE() AND
            TABLE_NAME        = 'LibraryDilution' AND
            CONSTRAINT_NAME   = 'fk_libraryDilution_targetedSequencing' AND
            CONSTRAINT_TYPE   = 'FOREIGN KEY') = true,'ALTER TABLE LibraryDilution
            DROP FOREIGN KEY fk_libraryDilution_targetedSequencing','select 1');

prepare stmt from @var;
execute stmt;
deallocate prepare stmt;
-- EndNoTest

DROP TABLE TargetedSequencing;
ALTER TABLE TargetedSequencingTemp RENAME TO TargetedSequencing;

ALTER TABLE LibraryDilution ADD CONSTRAINT FK_ld_targetedSequencing_targetedSequencingId FOREIGN KEY (targetedSequencingId) REFERENCES TargetedSequencing (targetedSequencingId);
ALTER TABLE TargetedSequencing_KitDescriptor ADD CONSTRAINT TK_TargetedSequencing_FK FOREIGN KEY (targetedSequencingId) REFERENCES TargetedSequencing (targetedSequencingId);
