--StartNoTest
DELIMITER //

DROP PROCEDURE IF EXISTS querySampleById//
CREATE PROCEDURE querySampleById(
  iSampleId BIGINT(20)
) BEGIN
  PREPARE stmt FROM 'SELECT * FROM (
    SELECT s.alias NAME
      , s.description description
      , s.NAME id
      , parent.NAME parentId
      , COALESCE(tt.alias, sc.alias) sampleType
      , NULL sampleType_platform
      , NULL sampleType_description
      , tt.alias tissueType
      , p.shortName project
      , sai.archived archived
      , scl.creationDate created
      , sclcu.userId createdById
      , scl.lastUpdated modified
      , scluu.userId modifiedById
      , s.identificationBarcode tubeBarcode
      , s.volume volume
      , sai.concentration concentration
      , s.locationBarcode storageLocation
      , NULL kitName
      , NULL kitDescription
      , NULL library_design_code
      , s.receivedDate receive_date
      , i.externalName external_name
      , tor.alias tissue_origin
      , tm.alias tissue_preparation
      , st.region tissue_region
      , st.secondaryIdentifier tube_id
      , ss.strStatus str_result
      , sai.groupId group_id
      , sai.groupDescription group_id_description
      , sp.alias purpose
      , qubit.results qubit_concentration
      , nanodrop.results nanodrop_concentration
      , NULL barcode
      , NULL barcode_two
      , qpcr.results qpcr_percentage_human
      , s.qcPassed qcPassed
      , qpd.description detailedQcStatus
      , box.locationBarcode boxLocation
      , box.alias boxAlias
      , pos.position boxPosition
      , NULL paired
      , NULL read_length
      , NULL targeted_sequencing
      , \'Sample\' miso_type
      , sai.preMigrationId premigration_id
      , s.scientificName organism
    FROM Sample s
    LEFT JOIN DetailedSample sai ON sai.sampleId = s.sampleId
    LEFT JOIN DetailedQcStatus qpd ON qpd.detailedQcStatusId = sai.detailedQcStatusId
    LEFT JOIN Sample parent ON parent.sampleId = sai.parentId
    LEFT JOIN SampleClass sc ON sc.sampleClassId = sai.sampleClassId
    LEFT JOIN Project p ON p.projectId = s.project_projectId
    LEFT JOIN Identity i ON i.sampleId = s.sampleId
    
    LEFT JOIN SampleAliquot sa ON sa.sampleId = sai.sampleId
    LEFT JOIN SamplePurpose sp ON sp.samplePurposeId = sa.samplePurposeId
    LEFT JOIN SampleTissue st ON st.sampleId = s.sampleId
    LEFT JOIN TissueType tt ON tt.tissueTypeId = st.tissueTypeId
    LEFT JOIN TissueOrigin tor ON tor.tissueOriginId = st.tissueOriginId
    LEFT JOIN TissueMaterial tm ON tm.tissueMaterialId = st.tissueMaterialId

    LEFT JOIN (SELECT sampleId, MAX(changeTime) as lastUpdated, MIN(changeTime) as creationDate from SampleChangeLog GROUP BY sampleId) scl ON sai.sampleId = scl.sampleId
    LEFT JOIN (SELECT userId, sampleId FROM SampleChangeLog scl1 WHERE changeTime = (SELECT MIN(scl2.changeTime) FROM SampleChangeLog scl2 where scl1.sampleId = scl2.sampleId)) sclcu ON sai.sampleId = sclcu.sampleId
    LEFT JOIN (SELECT userId, sampleId  FROM SampleChangeLog scl1 WHERE changeTime = (SELECT MAX(scl2.changeTime) FROM SampleChangeLog scl2 where scl1.sampleId = scl2.sampleId)) scluu ON sai.sampleId = scluu.sampleId
    LEFT JOIN SampleStock ss ON sai.sampleId = ss.sampleId

    LEFT JOIN (
      SELECT sample_sampleId
        , results
      FROM SampleQC
      INNER JOIN QCType ON QCType.qcTypeId = SampleQC.type
      WHERE QCType.NAME = \'QuBit\'
    ) qubit ON qubit.sample_sampleId = s.sampleId
    LEFT JOIN (
      SELECT sample_sampleId
        , results
      FROM SampleQC
      INNER JOIN QCType ON QCType.qcTypeId = SampleQC.type
      WHERE QCType.NAME = \'Nanodrop\'
    ) nanodrop ON nanodrop.sample_sampleId = s.sampleId
    LEFT JOIN (
      SELECT sample_sampleId
        , results
      FROM SampleQC
      INNER JOIN QCType ON QCType.qcTypeId = SampleQC.type
      WHERE QCType.NAME = \'Human qPCR\'
    ) qpcr ON qpcr.sample_sampleId = s.sampleId
    LEFT JOIN BoxPosition pos ON pos.targetId = s.sampleId
      AND pos.targetType LIKE \'Sample%\'
    LEFT JOIN Box box ON box.boxId = pos.boxId

    UNION

    SELECT l.alias NAME
      , l.description description
      , l.NAME id
      , parent.NAME parentId
      , NULL sampleType
      , lt.platformType sampleType_platform
      , lt.description sampleType_description
      , NULL tissueType
      , p.shortName project
      , lai.archived archived
      , l.creationDate created
      , lclcu.userId createdById
      , lcl.lastUpdated modified
      , lcluu.userId modifiedById
      , l.identificationBarcode tubeBarcode
      , l.volume volume
      , l.concentration concentration
      , l.locationBarcode storageLocation
      , kd.NAME kitName
      , kd.description kitDescription
      , ldc.code library_design_code
      , NULL receive_date
      , NULL external_name
      , NULL tissue_origin
      , NULL tissue_preparation
      , NULL tissue_region
      , NULL tube_id
      , NULL str_result
      , NULL group_id
      , NULL group_id_description
      , NULL purpose
      , qubit.results qubit_concentration
      , NULL nanodrop_concentration
      , bc1.sequence barcode
      , bc2.sequence barcode_two
      , NULL qpcr_percentage_human
      , l.qcPassed qcPassed
      , NULL detailedQcStatus
      , box.locationBarcode boxLocation
      , box.alias boxAlias
      , pos.position boxPosition
      , NULL paired
      , NULL readLength
      , NULL targeted_sequencing
      , \'Library\' miso_type
      , lai.preMigrationId premigration_id
      , NULL organism
    FROM Library l
    LEFT JOIN Sample parent ON parent.sampleId = l.sample_sampleId
    LEFT JOIN Project p ON p.projectId = parent.project_projectId
    LEFT JOIN DetailedLibrary lai ON lai.libraryId = l.libraryId
    LEFT JOIN LibraryDesignCode ldc ON ldc.libraryDesignCodeId = lai.libraryDesignCodeId
    LEFT JOIN KitDescriptor kd ON kd.kitDescriptorId = l.kitDescriptorId
    
      LEFT JOIN LibraryType lt ON lt.libraryTypeId = l.libraryType
    LEFT JOIN (
      SELECT library_libraryId
        , results
      FROM LibraryQC
      INNER JOIN QCType ON QCType.qcTypeId = LibraryQC.type
      WHERE QCType.NAME = \'QuBit\'
    ) qubit ON qubit.library_libraryId = l.libraryId
    LEFT JOIN (
      SELECT library_libraryId
        , sequence
      FROM Library_Index
      INNER JOIN Indices ON Indices.indexId = Library_Index.index_indexId
      WHERE position = 1
    ) bc1 ON bc1.library_libraryId = l.libraryId
    LEFT JOIN (
      SELECT library_libraryId
        , sequence
      FROM Library_Index
      INNER JOIN Indices ON Indices.indexId = Library_Index.index_indexId
      WHERE position = 2
    ) bc2 ON bc2.library_libraryId = l.libraryId
    LEFT JOIN BoxPosition pos ON pos.targetId = l.libraryId
      AND pos.targetType LIKE \'Library%\'
    LEFT JOIN Box box ON box.boxId = pos.boxId
    LEFT JOIN (SELECT libraryId, MAX(changeTime) as lastUpdated from LibraryChangeLog GROUP BY libraryId) lcl
      ON lai.libraryId = lcl.libraryId
    LEFT JOIN (SELECT userId, libraryId FROM LibraryChangeLog lcl1 WHERE changeTime = (
      SELECT MIN(lcl2.changeTime) FROM LibraryChangeLog lcl2 where lcl1.libraryId = lcl2.libraryId)
    ) lclcu ON lai.libraryId = lclcu.libraryId
    LEFT JOIN (SELECT userId, libraryId  FROM LibraryChangeLog lcl1 WHERE changeTime = (
      SELECT MAX(lcl2.changeTime) FROM LibraryChangeLog lcl2 where lcl1.libraryId = lcl2.libraryId)
    ) lcluu ON lai.libraryId = lcluu.libraryId

    UNION

    SELECT parent.alias name
      , NULL description
      , d.NAME id
      , parent.name parentId
      , NULL sampleType
      , lt.platformType sampleType_platform
      , lt.description sampleType_description
      , NULL tissueType
      , p.shortName project
      , 0 archived
      , CONVERT(d.creationDate, DATETIME) created
      , NULL createdById
      , d.lastUpdated modified
      , NULL modifiedById
      , d.identificationBarcode tubeBarcode
      , NULL volume
      , d.concentration concentration
      , NULL storageLocation
      , NULL kitName
      , NULL kitDescription
      , ldc.code library_design_code
      , NULL receive_date
      , NULL external_name
      , NULL tissue_origin
      , NULL tissue_preparation
      , NULL tissue_region
      , NULL tube_id
      , NULL str_result
      , NULL group_id
      , NULL group_id_description
      , NULL purpose
      , NULL qubit_concentration
      , NULL nanodrop_concentration
      , NULL barcode
      , NULL barcode_two
      , NULL qpcr_percentage_human
      , 1 qcPassed
      , NULL detailedQcStatus
      , NULL boxLocation
      , NULL boxAlias
      , NULL boxPosition
      , NULL paired
      , NULL readLength
      , NULL targeted_sequencing
      , \'Dilution\' miso_type
      , d.preMigrationId premigration_id
      , NULL organism
    FROM LibraryDilution d
    JOIN Library parent ON parent.libraryId = d.library_libraryId
    JOIN LibraryType lt ON lt.libraryTypeId = parent.libraryType
    LEFT JOIN DetailedLibrary lai ON lai.libraryId = parent.libraryId
    LEFT JOIN LibraryDesignCode ldc ON lai.libraryDesignCodeId = ldc.libraryDesignCodeId
    JOIN Sample s ON s.sampleId = parent.sample_sampleId
    JOIN Project p ON p.projectId = s.project_projectId
  ) COMBINED WHERE id = ?';
  SET @id = iSampleId;
  EXECUTE stmt USING @id;
  DEALLOCATE PREPARE stmt;
END//

DELIMITER ;
--EndNoTest