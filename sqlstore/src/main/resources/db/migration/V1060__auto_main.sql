-- flatten_samples
DROP TRIGGER IF EXISTS DetailedSampleChange;
DROP TRIGGER IF EXISTS SampleAliquotChange;
DROP TRIGGER IF EXISTS SampleSlideChange;
DROP TRIGGER IF EXISTS SampleTissuePieceChange;
DROP TRIGGER IF EXISTS SampleStockChange;
DROP TRIGGER IF EXISTS SampleTissueChange;
DROP TRIGGER IF EXISTS IdentityChange;
DROP TRIGGER IF EXISTS SampleSingleCellChange;
DROP TRIGGER IF EXISTS SampleStockSingleCellChange;
DROP TRIGGER IF EXISTS SampleAliquotSingleCellChange;
DROP TRIGGER IF EXISTS DetailedSampleInsert;

ALTER TABLE Sample
  ADD COLUMN discriminator varchar(50) NOT NULL DEFAULT 'Sample',
  -- DetailedSample
  ADD COLUMN `sampleClassId` bigint,
  ADD COLUMN `detailedQcStatusId` bigint,
  ADD COLUMN `subprojectId` bigint,
  ADD COLUMN `archived` bit,
  ADD COLUMN `parentId` bigint,
  ADD COLUMN `siblingNumber` int,
  ADD COLUMN `groupId` varchar(100),
  ADD COLUMN `groupDescription` varchar(255),
  ADD COLUMN `isSynthetic` tinyint,
  ADD COLUMN `nonStandardAlias` tinyint,
  ADD COLUMN `preMigrationId` bigint,
  ADD COLUMN `detailedQcStatusNote` varchar(500),
  ADD COLUMN `creationDate` date,
  ADD COLUMN `volumeUsed` decimal(14,10),
  ADD COLUMN `ngUsed` decimal(14,10),
  ADD CONSTRAINT `uk_sample_preMigrationId` UNIQUE (`preMigrationId`),
  ADD CONSTRAINT `fk_sample_detailedQcStatus` FOREIGN KEY (`detailedQcStatusId`) REFERENCES `DetailedQcStatus` (`detailedQcStatusId`),
  ADD CONSTRAINT `fk_sample_sampleClass` FOREIGN KEY (`sampleClassId`) REFERENCES `SampleClass` (`sampleClassId`),
  ADD CONSTRAINT `fk_sample_subproject` FOREIGN KEY (`subprojectId`) REFERENCES `Subproject` (`subprojectId`),
  ADD CONSTRAINT `fk_sample_parent` FOREIGN KEY (`parentId`) REFERENCES `Sample` (`sampleId`),
  -- Identity
  ADD COLUMN `externalName` varchar(255),
  ADD COLUMN `donorSex` varchar(50),
  ADD COLUMN `consentLevel` varchar(50),
  -- SampleTissue
  ADD COLUMN `tissueOriginId` bigint,
  ADD COLUMN `tissueTypeId` bigint,
  ADD COLUMN `secondaryIdentifier` varchar(255),
  ADD COLUMN `labId` bigint,
  ADD COLUMN `region` varchar(255),
  ADD COLUMN `passageNumber` int,
  ADD COLUMN `tubeNumber` int,
  ADD COLUMN `timesReceived` int,
  ADD COLUMN `tissueMaterialId` bigint,
  ADD CONSTRAINT `fk_sample_lab` FOREIGN KEY (`labId`) REFERENCES `Lab` (`labId`),
  ADD CONSTRAINT `fk_sample_tissueOrigin` FOREIGN KEY (`tissueOriginId`) REFERENCES `TissueOrigin` (`tissueOriginId`),
  ADD CONSTRAINT `fk_sample_tissueType` FOREIGN KEY (`tissueTypeId`) REFERENCES `TissueType` (`tissueTypeId`),
  ADD CONSTRAINT `fk_sample_tissueMaterial` FOREIGN KEY (`tissueMaterialId`) REFERENCES `TissueMaterial` (`tissueMaterialId`),
  -- SampleTissueProcessing (no columns)
  -- SampleSlide
  ADD COLUMN `initialSlides` int,
  ADD COLUMN `discards` int,
  ADD COLUMN `thickness` int,
  ADD COLUMN `stain` bigint,
  ADD COLUMN `slides` int,
  ADD COLUMN `percentTumour` decimal(11,8),
  ADD COLUMN `percentNecrosis` decimal(11,8),
  ADD COLUMN `markedAreaSize` decimal(11,8),
  ADD COLUMN `markedAreaPercentTumour` decimal(11,8),
  ADD CONSTRAINT `fk_sample_stain` FOREIGN KEY (`stain`) REFERENCES `Stain` (`stainId`),
  -- SampleTissuePiece
  ADD COLUMN `slidesConsumed` int,
  ADD COLUMN `tissuePieceType` bigint,
  ADD COLUMN `referenceSlideId` bigint, -- (shared with SampleStock)
  ADD CONSTRAINT `fk_sample_referenceSlide` FOREIGN KEY (`referenceSlideId`) REFERENCES `Sample` (`sampleId`),
  ADD CONSTRAINT `fk_sample_tissuePieceType` FOREIGN KEY (`tissuePieceType`) REFERENCES `TissuePieceType` (`tissuePieceTypeId`),
  -- SampleSingleCell
  ADD COLUMN `initialCellConcentration` decimal(14,10),
  ADD COLUMN `digestion` varchar(255),
  -- SampleStock
  ADD COLUMN `strStatus` varchar(50),
  ADD COLUMN `dnaseTreated` tinyint,
  -- SampleStockSingleCell
  ADD COLUMN `targetCellRecovery` decimal(14,10),
  ADD COLUMN `cellViability` decimal(14,10),
  ADD COLUMN `loadingCellConcentration` decimal(14,10),
  -- SampleAliquot
  ADD COLUMN `samplePurposeId` bigint,
  ADD CONSTRAINT `fk_sample_samplePurpose` FOREIGN KEY (`samplePurposeId`) REFERENCES `SamplePurpose` (`samplePurposeId`),
  -- SampleAliquotSingleCell
  ADD COLUMN `inputIntoLibrary` decimal(14,10);

UPDATE Sample s
JOIN DetailedSample ds ON ds.sampleId = s.sampleId
LEFT JOIN Identity ident ON ident.sampleId = s.sampleId
LEFT JOIN SampleTissue st ON st.sampleId = s.sampleId
LEFT JOIN SampleSlide ss ON ss.sampleId = s.sampleId
LEFT JOIN SampleTissuePiece stp ON stp.sampleId = s.sampleId
LEFT JOIN SampleSingleCell ssc ON ssc.sampleId = s.sampleId
LEFT JOIN SampleStock sst ON sst.sampleId = s.sampleId
LEFT JOIN SampleStockSingleCell sssc ON sssc.sampleId = s.sampleId
LEFT JOIN SampleAliquot sa ON sa.sampleId = s.sampleId
LEFT JOIN SampleAliquotSingleCell sasc ON sasc.sampleId = s.sampleId
SET
  -- DetailedSample
  s.sampleClassId = ds.sampleClassId,
  s.detailedQcStatusId = ds.detailedQcStatusId,
  s.subprojectId = ds.subprojectId,
  s.archived = ds.archived,
  s.parentId = ds.parentId,
  s.siblingNumber = ds.siblingNumber,
  s.groupId = ds.groupId,
  s.groupDescription = ds.groupDescription,
  s.isSynthetic = ds.isSynthetic,
  s.nonStandardAlias = ds.nonStandardAlias,
  s.preMigrationId = ds.preMigrationId,
  s.detailedQcStatusNote = ds.detailedQcStatusNote,
  s.creationDate = ds.creationDate,
  s.volumeUsed = ds.volumeUsed,
  s.ngUsed = ds.ngUsed,
  -- Identity
  s.externalName = ident.externalName,
  s.donorSex = ident.donorSex,
  s.consentLevel = ident.consentLevel,
  -- SampleTissue
  s.tissueOriginId = st.tissueOriginId,
  s.tissueTypeId = st.tissueTypeId,
  s.secondaryIdentifier = st.secondaryIdentifier,
  s.labId = st.labId,
  s.region = st.region,
  s.passageNumber = st.passageNumber,
  s.tubeNumber = st.tubeNumber,
  s.timesReceived = st.timesReceived,
  s.tissueMaterialId = st.tissueMaterialId,
  -- SampleSlide
  s.initialSlides = ss.initialSlides,
  s.discards = ss.discards,
  s.thickness = ss.thickness,
  s.stain = ss.stain,
  s.slides = ss.slides,
  s.percentTumour = ss.percentTumour,
  s.percentNecrosis = ss.percentNecrosis,
  s.markedAreaSize = ss.markedAreaSize,
  s.markedAreaPercentTumour = ss.markedAreaPercentTumour,
  -- SampleTissuePiece
  s.slidesConsumed = stp.slidesConsumed,
  s.tissuePieceType = stp.tissuePieceType,
  s.referenceSlideId = COALESCE(stp.referenceSlideId, sst.referenceSlideId),
  -- SampleSingleCell
  s.initialCellConcentration = ssc.initialCellConcentration,
  s.digestion = ssc.digestion,
  -- SampleStock
  s.strStatus = sst.strStatus,
  s.dnaseTreated = sst.dnaseTreated,
  -- SampleStockSingleCell
  s.targetCellRecovery = sssc.targetCellRecovery,
  s.cellViability = sssc.cellViability,
  s.loadingCellConcentration = sssc.loadingCellConcentration,
  -- SampleAliquot
  s.samplePurposeId = sa.samplePurposeId,
  -- SampleAliquotSingleCell
  s.inputIntoLibrary = sasc.inputIntoLibrary;

UPDATE Sample s
JOIN Identity ident ON ident.sampleId = s.sampleId
SET s.discriminator = 'Identity';

UPDATE Sample s
JOIN SampleTissue st ON st.sampleId = s.sampleId
SET s.discriminator = 'Tissue';

UPDATE Sample s
JOIN SampleTissueProcessing stp ON stp.sampleId = s.sampleId
SET s.discriminator = 'TissueProcessing';

UPDATE Sample s
JOIN SampleSlide ss ON ss.sampleId = s.sampleId
SET s.discriminator = 'Slide';

UPDATE Sample s
JOIN SampleTissuePiece stp ON stp.sampleId = s.sampleId
SET s.discriminator = 'TissuePiece';

UPDATE Sample s
JOIN SampleSingleCell ssc ON ssc.sampleId = s.sampleId
SET s.discriminator = 'SingleCell';

UPDATE Sample s
JOIN SampleStock ss ON ss.sampleId = s.sampleId
SET s.discriminator = 'Stock';

UPDATE Sample s
JOIN SampleStockSingleCell sssc ON sssc.sampleId = s.sampleId
SET s.discriminator = 'StockSingleCell';

UPDATE Sample s
JOIN SampleAliquot sa ON sa.sampleId = s.sampleId
SET s.discriminator = 'Aliquot';

UPDATE Sample s
JOIN SampleAliquotSingleCell sasc ON sasc.sampleId = s.sampleId
SET s.discriminator = 'AliquotSingleCell';

ALTER TABLE SampleHierarchy
  DROP FOREIGN KEY fk_sampleHierarchy_identity,
  DROP FOREIGN KEY fk_sampleHierarchy_sample,
  DROP FOREIGN KEY fk_sampleHierarchy_tissue;

ALTER TABLE SampleHierarchy
  ADD CONSTRAINT `fk_sampleHierarchy_identity` FOREIGN KEY (`identityId`) REFERENCES `Sample` (`sampleId`) ON DELETE CASCADE,
  ADD CONSTRAINT `fk_sampleHierarchy_sample` FOREIGN KEY (`sampleId`) REFERENCES `Sample` (`sampleId`) ON DELETE CASCADE,
  ADD CONSTRAINT `fk_sampleHierarchy_tissue` FOREIGN KEY (`tissueId`) REFERENCES `Sample` (`sampleId`) ON DELETE CASCADE;

DROP TABLE SampleTissueProcessing;
DROP TABLE SampleTissuePiece;
DROP TABLE SampleSingleCell;
DROP TABLE SampleStock;
DROP TABLE SampleStockSingleCell;
DROP TABLE SampleAliquot;
DROP TABLE SampleAliquotSingleCell;
DROP TABLE SampleSlide;
DROP TABLE SampleTissue;
DROP TABLE Identity;
DROP TABLE DetailedSample;

DROP FUNCTION IF EXISTS getParentTissueId;
DROP FUNCTION IF EXISTS getParentIdentityId;

-- Add missing default
ALTER TABLE TissuePieceType MODIFY COLUMN archived BOOLEAN NOT NULL DEFAULT FALSE;

DROP TRIGGER IF EXISTS DetailedLibraryChange;

ALTER TABLE Library
  ADD COLUMN discriminator varchar(50) NOT NULL DEFAULT 'Library',
  ADD COLUMN `archived` bit,
  ADD COLUMN `libraryDesign` bigint,
  ADD COLUMN `nonStandardAlias` tinyint,
  ADD COLUMN `preMigrationId` bigint,
  ADD COLUMN `libraryDesignCodeId` bigint,
  ADD COLUMN `groupId` varchar(100),
  ADD COLUMN `groupDescription` varchar(255),
  ADD CONSTRAINT `uk_library_preMigrationId` UNIQUE (`preMigrationId`),
  ADD CONSTRAINT `fk_library_libraryDesignCode` FOREIGN KEY (`libraryDesignCodeId`) REFERENCES `LibraryDesignCode` (`libraryDesignCodeId`),
  ADD CONSTRAINT `fk_library_libraryDesign` FOREIGN KEY (`libraryDesign`) REFERENCES `LibraryDesign` (`libraryDesignId`);

UPDATE Library l
JOIN DetailedLibrary dl ON dl.libraryId = l.libraryId
SET
  l.discriminator = 'DetailedLibrary',
  l.archived = dl.archived,
  l.libraryDesign = dl.libraryDesign,
  l.nonStandardAlias = dl.nonStandardAlias,
  l.preMigrationId = dl.preMigrationId,
  l.libraryDesignCodeId = dl.libraryDesignCodeId,
  l.groupId = dl.groupId,
  l.groupDescription = dl.groupDescription;

DROP TABLE DetailedLibrary;

ALTER TABLE LibraryAliquot
  ADD COLUMN discriminator varchar(50) NOT NULL DEFAULT 'LibraryAliquot',
  ADD COLUMN `nonStandardAlias` tinyint,
  ADD COLUMN `libraryDesignCodeId` bigint,
  ADD COLUMN `groupId` varchar(100),
  ADD COLUMN `groupDescription` varchar(255),
  ADD CONSTRAINT `fk_libraryAliquot_libraryDesignCode` FOREIGN KEY (`libraryDesignCodeId`) REFERENCES `LibraryDesignCode` (`libraryDesignCodeId`);

UPDATE LibraryAliquot la
JOIN DetailedLibraryAliquot dla ON dla.aliquotId = la.aliquotId
SET
  la.discriminator = 'DetailedLibraryAliquot',
  la.nonStandardAlias = dla.nonStandardAlias,
  la.libraryDesignCodeId = dla.libraryDesignCodeId,
  la.groupId = dla.groupId,
  la.groupDescription = dla.groupDescription;

DROP TABLE DetailedLibraryAliquot;

-- fail migration if there is a mix of plain and detailed items
DELIMITER //
CREATE PROCEDURE verifyDiscriminators()
BEGIN
  IF (
    EXISTS (SELECT 1 FROM Sample WHERE discriminator = 'Sample')
    OR EXISTS (SELECT 1 FROM Library WHERE discriminator = 'Library')
    OR EXISTS (SELECT 1 FROM LibraryAliquot WHERE discriminator = 'LibraryAliquot')
  )
  AND (
    EXISTS (SELECT 1 FROM Sample WHERE discriminator <> 'Sample')
    OR (SELECT 1 FROM Library WHERE discriminator <> 'Library')
    OR (SELECT 1 FROM LibraryAliquot WHERE discriminator <> 'LibraryAliquot')
  ) THEN
    SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Failed to determine all discriminators';
  END IF;
END//
DELIMITER ;
CALL verifyDiscriminators();
DROP PROCEDURE verifyDiscriminators;

-- performance_improvements

DROP VIEW IF EXISTS SampleHierarchyView;
