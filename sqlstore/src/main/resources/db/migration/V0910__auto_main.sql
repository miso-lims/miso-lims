-- fix_box_size
-- Rename old table
ALTER TABLE `BoxSize`
    RENAME TO `BoxSize_old`;

-- Recreate table
CREATE TABLE `BoxSize` (
  `boxSizeId` bigint NOT NULL AUTO_INCREMENT,
  `boxSizeRows` bigint NOT NULL,
  `boxSizeColumns` bigint NOT NULL,
  `scannable` boolean DEFAULT 0 NOT NULL,
  PRIMARY KEY (`boxSizeId`),
  UNIQUE (`boxSizeRows`, `boxSizeColumns`, `scannable`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Copy table
INSERT INTO `BoxSize` (`boxSizeId`, `boxSizeRows`, `boxSizeColumns`, `scannable`)
    SELECT `boxSizeId`, `rows`, `columns`, `scannable`
    FROM `BoxSize_old`;

-- Update Box table
ALTER TABLE `Box`
    DROP FOREIGN KEY `fk_box_boxSize`;

ALTER TABLE `Box`
    ADD CONSTRAINT `fk_box_boxSize` FOREIGN KEY (`boxSizeId`) REFERENCES `BoxSize`(`boxSizeId`);

-- Drop old table
DROP TABLE `BoxSize_old`;

-- library_aliquots
ALTER TABLE LibraryAliquot ADD COLUMN alias varchar(100);
ALTER TABLE LibraryAliquot ADD COLUMN dnaSize bigint;

-- StartNoTest
UPDATE LibraryAliquot ali
JOIN Library lib ON lib.libraryId = ali.libraryId
SET ali.alias = lib.alias, ali.dnaSize = lib.dnaSize;
-- EndNoTest

ALTER TABLE LibraryAliquot MODIFY COLUMN alias varchar(100) NOT NULL;

CREATE TABLE DetailedLibraryAliquot (
  aliquotId bigint NOT NULL,
  nonStandardAlias BOOLEAN NOT NULL DEFAULT FALSE,
  libraryDesignCodeId bigint NOT NULL,
  groupId varchar(100),
  groupDescription varchar(255),
  PRIMARY KEY (aliquotId),
  CONSTRAINT fk_detailedLibraryAliquot_libraryAliquot FOREIGN KEY (aliquotId) REFERENCES LibraryAliquot (aliquotId),
  CONSTRAINT fk_detailedLibraryAliquot_libraryDesignCode FOREIGN KEY (libraryDesignCodeId) REFERENCES LibraryDesignCode (libraryDesignCodeId)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

INSERT INTO DetailedLibraryAliquot (aliquotId, nonStandardAlias, libraryDesignCodeId)
SELECT ali.aliquotId, dl.nonStandardAlias, dl.libraryDesignCodeId
FROM LibraryAliquot ali
JOIN Library lib ON lib.libraryId = ali.libraryId
JOIN DetailedLibrary dl ON dl.libraryId = lib.libraryId;

-- propagate_ali_to_ali
ALTER TABLE LibraryAliquot ADD COLUMN parentAliquotId bigint;
ALTER TABLE LibraryAliquot ADD CONSTRAINT fk_libraryAliquot_parentAliquot FOREIGN KEY (parentAliquotId) REFERENCES LibraryAliquot (aliquotId);

-- rm_external_role
ALTER TABLE User DROP COLUMN external;

-- drop_poolableelementview_index
DROP VIEW IF EXISTS `DuplicateBarcodes`;
DROP VIEW IF EXISTS `DuplicateBarcodes_Items`;
DROP VIEW IF EXISTS `PoolableElementView_Index`;

