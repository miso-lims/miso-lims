--StartNoTest
DELIMITER //

DROP PROCEDURE IF EXISTS queryAllModels//
CREATE PROCEDURE queryAllModels() BEGIN
  -- expansions: queryModelById
  PREPARE stmt FROM 'SELECT p.platformId
    , p.instrumentModel
    FROM Platform as p';
  EXECUTE stmt;
  DEALLOCATE PREPARE stmt;
END//

DROP PROCEDURE IF EXISTS queryModelById//
CREATE PROCEDURE queryModelById(
  iModelId BIGINT(20)
) BEGIN
  -- base: queryAllModels
  PREPARE stmt FROM 'SELECT p.platformId
    , p.instrumentModel
    FROM Platform as p
    WHERE p.platformId = ?';
  SET @platformId = iModelId;
  EXECUTE stmt USING @platformId;
  DEALLOCATE PREPARE stmt;
END//

DROP PROCEDURE IF EXISTS queryAllInstruments//
CREATE PROCEDURE queryAllInstruments() BEGIN
  -- expansions: queryInstrumentById, queryInstrumentsByModelId
  PREPARE stmt FROM 'SELECT sr.referenceId
    , sr.name
    , sr.platformId
    FROM SequencerReference AS sr';
  EXECUTE stmt;
  DEALLOCATE PREPARE stmt;
END//

DROP PROCEDURE IF EXISTS queryInstrumentById//
CREATE PROCEDURE queryInstrumentById(
  iInstrumentId BIGINT(20)
) BEGIN
  -- base: queryAllInstruments
  PREPARE stmt FROM 'SELECT sr.referenceId
    , sr.name
    , sr.platformId
    FROM SequencerReference AS sr
    WHERE sr.referenceId = ?';
  SET @referenceId = iInstrumentId;
  EXECUTE stmt USING @referenceId;
  DEALLOCATE PREPARE stmt;
END//

DROP PROCEDURE IF EXISTS queryInstrumentsByModelId//
CREATE PROCEDURE queryInstrumentsByModelId(
  iPlatformId BIGINT(20)
) BEGIN
  -- base: queryAllInstruments
  PREPARE stmt FROM 'SELECT sr.referenceId
    , sr.name
    , sr.platformId
    FROM SequencerReference AS sr
    WHERE sr.platformId = ?';
  SET @platformId = iPlatformId;
  EXECUTE stmt USING @platformId;
  DEALLOCATE PREPARE stmt;
END//

DROP PROCEDURE IF EXISTS queryAllOrders//
CREATE PROCEDURE queryAllOrders() BEGIN
  -- expansions: queryOrderById
  PREPARE stmt FROM 'SELECT o.poolOrderId orderId
  , o.creationDate createdDate
  , o.createdBy createdById
  , o.lastUpdated modifiedDate
  , o.updatedBy modifiedById
  , pool.platformType platform
  FROM PoolOrder o
  JOIN Pool pool ON pool.poolId = o.poolId';
  EXECUTE stmt;
  DEALLOCATE PREPARE stmt;
END//

DROP PROCEDURE IF EXISTS queryOrderById//
CREATE PROCEDURE queryOrderById(
  iOrderId BIGINT(20)
) BEGIN
  -- base: queryAllOrders
  PREPARE stmt FROM 'SELECT o.poolOrderId orderId
    , o.creationDate createdDate
    , o.createdBy createdById
    , o.lastUpdated modifiedDate
    , o.updatedBy modifiedById
    , pool.platformType platform
    FROM PoolOrder o
    JOIN Pool pool ON pool.poolId = o.poolId
    WHERE poolOrderId = ?';
  SET @poolOrderId = iOrderId;
  EXECUTE stmt USING @poolOrderId;
  DEALLOCATE PREPARE stmt;
END//

DROP PROCEDURE IF EXISTS queryAllOrderSamples//
CREATE PROCEDURE queryAllOrderSamples() BEGIN
  -- expansions: queryOrderSamplesByOrderId
  PREPARE stmt FROM 'SELECT o.poolOrderId orderId
    ,lib.NAME libraryId
    ,bc1.sequence barcode
    ,bc2.sequence barcode_two
    ,sp.paired paired
    ,sp.readLength read_length
    ,tr.alias targeted_sequencing
    FROM PoolOrder o
    LEFT JOIN SequencingParameters sp ON sp.parametersId = o.parametersId
    LEFT JOIN Pool p ON p.poolId = o.poolId
    LEFT JOIN Pool_Dilution pe ON pe.pool_poolId = p.poolId
    LEFT JOIN LibraryDilution ld ON ld.dilutionId = pe.dilution_dilutionId
    LEFT JOIN TargetedSequencing tr ON tr.targetedSequencingId = ld.targetedSequencingId
    LEFT JOIN Library lib ON lib.libraryId = ld.library_libraryId
    LEFT JOIN (
      SELECT library_libraryId
      ,sequence
      FROM Library_Index ltb
      INNER JOIN Indices AS tb ON tb.indexId = ltb.index_indexId AND tb.position = 1
    ) bc1 ON bc1.library_libraryId = lib.libraryId
    LEFT JOIN (
      SELECT library_libraryId
      ,sequence
      FROM Library_Index ltb
      INNER JOIN Indices AS tb ON tb.indexId = ltb.index_indexId AND tb.position = 2
    ) bc2 ON bc2.library_libraryId = lib.libraryId';
  EXECUTE stmt;
  DEALLOCATE PREPARE stmt;
END//

DROP PROCEDURE IF EXISTS queryOrderSamplesByOrderId//
CREATE PROCEDURE queryOrderSamplesByOrderId(
  iOrderId BIGINT(20)
) BEGIN
  -- base: queryAllOrderSamples
  PREPARE stmt FROM 'SELECT o.poolOrderId orderId
    ,lib.NAME libraryId
    ,bc1.sequence barcode
    ,bc2.sequence barcode_two
    ,sp.paired paired
    ,sp.readLength read_length
    ,tr.alias targeted_sequencing
    FROM PoolOrder o
    LEFT JOIN SequencingParameters sp ON sp.parametersId = o.parametersId
    LEFT JOIN Pool p ON p.poolId = o.poolId
    LEFT JOIN Pool_Dilution pe ON pe.pool_poolId = p.poolId
    LEFT JOIN LibraryDilution ld ON ld.dilutionId = pe.dilution_dilutionId
    LEFT JOIN TargetedSequencing tr ON tr.targetedSequencingId = ld.targetedSequencingId
    LEFT JOIN Library lib ON lib.libraryId = ld.library_libraryId
    LEFT JOIN (
      SELECT library_libraryId
      ,sequence
      FROM Library_Index ltb
      INNER JOIN Indices AS tb ON tb.indexId = ltb.index_indexId AND tb.position = 1
    ) bc1 ON bc1.library_libraryId = lib.libraryId
    LEFT JOIN (
      SELECT library_libraryId
      ,sequence
      FROM Library_Index ltb
      INNER JOIN Indices AS tb ON tb.indexId = ltb.index_indexId AND tb.position = 2
    ) bc2 ON bc2.library_libraryId = lib.libraryId
    WHERE poolOrderId = ?';
  SET @poolOrderId = iOrderId;
  EXECUTE stmt USING @poolOrderId;
  DEALLOCATE PREPARE stmt;
END//

DROP PROCEDURE IF EXISTS queryAllUsers//
CREATE PROCEDURE queryAllUsers() BEGIN
  -- expansions: queryUserById
  PREPARE stmt FROM 'SELECT u.userId, u.fullname, u.email, u.active FROM User AS u';
  EXECUTE stmt;
  DEALLOCATE PREPARE stmt;
END//

DROP PROCEDURE IF EXISTS queryUserById//
CREATE PROCEDURE queryUserById(
  iUserId BIGINT(20)
) BEGIN
  -- base: queryAllUsers
  PREPARE stmt FROM 'SELECT u.userId, u.fullname, u.email, u.active FROM User AS u WHERE u.userId = ?';
  SET @userId = iUserId;
  EXECUTE stmt USING @userId;
  DEALLOCATE PREPARE stmt;
END//

DROP PROCEDURE IF EXISTS queryAllRuns//
CREATE PROCEDURE queryAllRuns() BEGIN
  -- expansions: queryRunById, queryRunByName
  PREPARE stmt FROM 'SELECT DISTINCT r.alias
    , r.sequencerReference_sequencerReferenceId AS instrumentId
    , r.runId
    , r.filePath
    , r.health
    , r.startDate
    , r.completionDate
    , spc.identificationBarcode
    , createLog.userId
    , createLog.changeTime
    , updateLog.userId
    , updateLog.changeTime
    , sp.paired paired
    , sp.readLength read_length
    FROM Run AS r
    LEFT JOIN SequencingParameters AS sp ON sp.parametersId = r.sequencingParameters_parametersId
    LEFT JOIN Run_SequencerPartitionContainer AS rscp ON rscp.Run_runId = r.runId
    LEFT JOIN SequencerPartitionContainer AS spc ON spc.containerId = rscp.containers_containerId
    LEFT JOIN RunChangeLog AS createLog ON createLog.runId = r.runId
    LEFT JOIN RunChangeLog AS rcl1 ON rcl1.runId = createLog.runId AND createLog.changeTime > rcl1.changeTime
    LEFT JOIN RunChangeLog AS updateLog ON updateLog.runId = r.runId
    LEFT JOIN RunChangeLog AS rcl2 ON rcl2.runId = updateLog.runId AND updateLog.changeTime < rcl2.changeTime
    WHERE rcl1.runId IS NULL
      AND rcl2.runId IS NULL';
  EXECUTE stmt;
  DEALLOCATE PREPARE stmt;
END//

DROP PROCEDURE IF EXISTS queryRunById//
CREATE PROCEDURE queryRunById(
  iRunId BIGINT(20)
) BEGIN
  -- base: queryAllRuns
  PREPARE stmt FROM 'SELECT DISTINCT r.alias, r.sequencerReference_sequencerReferenceId AS instrumentId
    , r.runId
    , r.filePath
    , r.health
    , r.startDate
    , r.completionDate
    , spc.identificationBarcode
    , createLog.userId
    , createLog.changeTime
    , updateLog.userId
    , updateLog.changeTime
    , sp.paired paired
    , sp.readLength read_length
    FROM Run AS r
    LEFT JOIN SequencingParameters AS sp ON sp.parametersId = r.sequencingParameters_parametersId
    LEFT JOIN Run_SequencerPartitionContainer AS rscp ON rscp.Run_runId = r.runId
    LEFT JOIN SequencerPartitionContainer AS spc ON spc.containerId = rscp.containers_containerId
    LEFT JOIN RunChangeLog AS createLog ON createLog.runId = r.runId
    LEFT JOIN RunChangeLog AS rcl1 ON rcl1.runId = createLog.runId AND createLog.changeTime > rcl1.changeTime
    LEFT JOIN RunChangeLog AS updateLog ON updateLog.runId = r.runId
    LEFT JOIN RunChangeLog AS rcl2 ON rcl2.runId = updateLog.runId AND updateLog.changeTime < rcl2.changeTime
    WHERE rcl1.runId IS NULL
      AND rcl2.runId IS NULL
      AND r.runId = ?';
  SET @runId = iRunId;
  EXECUTE stmt USING @runId;
  DEALLOCATE PREPARE stmt;
END//

-- TODO: SANITIZE!!!!!!!!!!!!!!!!!!!!!!!!!!!
DROP PROCEDURE IF EXISTS queryRunByName//
CREATE PROCEDURE queryRunByName(
  iRunAlias VARCHAR(100)
) BEGIN
  -- base: queryAllRuns
  PREPARE stmt FROM 'SELECT DISTINCT r.alias, r.sequencerReference_sequencerReferenceId AS instrumentId
    , r.runId
    , r.filePath
    , r.health
    , r.startDate
    , r.completionDate
    , spc.identificationBarcode
    , createLog.userId
    , createLog.changeTime
    , updateLog.userId
    , updateLog.changeTime
    , sp.paired paired
    , sp.readLength read_length
    FROM Run AS r
    LEFT JOIN SequencingParameters AS sp ON sp.parametersId = r.sequencingParameters_parametersId
    LEFT JOIN Run_SequencerPartitionContainer AS rscp ON rscp.Run_runId = r.runId
    LEFT JOIN SequencerPartitionContainer AS spc ON spc.containerId = rscp.containers_containerId
    LEFT JOIN RunChangeLog AS createLog ON createLog.runId = r.runId
    LEFT JOIN RunChangeLog AS rcl1 ON rcl1.runId = createLog.runId AND createLog.changeTime > rcl1.changeTime
    LEFT JOIN RunChangeLog AS updateLog ON updateLog.runId = r.runId
    LEFT JOIN RunChangeLog AS rcl2 ON rcl2.runId = updateLog.runId AND updateLog.changeTime < rcl2.changeTime
    WHERE rcl1.runId IS NULL 
      AND rcl2.runId IS NULL
      AND r.alias = ?';
  SET @alias = iRunAlias;
  EXECUTE stmt USING @alias;
  DEALLOCATE PREPARE stmt;
END//

DROP PROCEDURE IF EXISTS queryAllRunPositions//
CREATE PROCEDURE queryAllRunPositions() BEGIN
  -- expansions: queryRunPositionsByRunId
  PREPARE stmt FROM 'SELECT p.partitionId
    , p.partitionNumber
    , r_spc.Run_runId
    FROM _Partition AS p
    JOIN SequencerPartitionContainer_Partition AS spc_p ON spc_p.partitions_partitionId = p.partitionId
    JOIN Run_SequencerPartitionContainer AS r_spc ON r_spc.containers_containerId = spc_p.container_containerId';
  EXECUTE stmt;
  DEALLOCATE PREPARE stmt;
END//

DROP PROCEDURE IF EXISTS queryRunPositionsByRunId//
CREATE PROCEDURE queryRunPositionsByRunId(
  iRunId BIGINT(20)
) BEGIN
  -- base: queryAllRunPositions
  PREPARE stmt FROM 'SELECT p.partitionId
    , p.partitionNumber
    , r_spc.Run_runId
    FROM _Partition AS p
    JOIN SequencerPartitionContainer_Partition AS spc_p ON spc_p.partitions_partitionId = p.partitionId
    JOIN Run_SequencerPartitionContainer AS r_spc ON r_spc.containers_containerId = spc_p.container_containerId
    WHERE r_spc.Run_runId = ?';
  SET @runId = iRunId;
  EXECUTE stmt USING @runId;
  DEALLOCATE PREPARE stmt;
END//

DROP PROCEDURE IF EXISTS queryAllRunSamples//
CREATE PROCEDURE queryAllRunSamples() BEGIN
  -- expansions: queryRunSamplesByRunId
  PREPARE stmt FROM 'SELECT part.partitionId
    , l.name libraryId
    , bc1.sequence barcode
    , bc2.sequence barcode_two, tr.alias targeted_sequencing
    FROM _Partition part
    JOIN Pool pool ON pool.poolId = part.pool_poolId
    JOIN Pool_Dilution ele ON ele.pool_poolId = pool.poolId
    JOIN LibraryDilution ld ON ld.dilutionId = ele.dilution_dilutionId
    JOIN Library l ON l.libraryId = ld.library_libraryId
    LEFT JOIN TargetedSequencing tr ON tr.targetedSequencingId = ld.targetedSequencingId
    LEFT JOIN (
      SELECT library_libraryId, sequence FROM Library_Index
      JOIN Indices ON Indices.indexId = Library_Index.index_indexId
      WHERE position = 1
    ) bc1 ON bc1.library_libraryId = l.libraryId
    LEFT JOIN (
      SELECT library_libraryId, sequence FROM Library_Index
      JOIN Indices ON Indices.indexId = Library_Index.index_indexId
      WHERE position = 2
    ) bc2 ON bc2.library_libraryId = l.libraryId';
  EXECUTE stmt;
  DEALLOCATE PREPARE stmt;
END//

DROP PROCEDURE IF EXISTS queryRunSamplesByRunId//
CREATE PROCEDURE queryRunSamplesByRunId(
  iRunId BIGINT(20)
) BEGIN
  -- base: queryAllRunSamples
  PREPARE stmt FROM 'SELECT part.partitionId
    , l.name libraryId
    , bc1.sequence barcode
    , bc2.sequence barcode_two, tr.alias targeted_sequencing
    FROM _Partition part
    JOIN Pool pool ON pool.poolId = part.pool_poolId
    JOIN Pool_Dilution ele ON ele.pool_poolId = pool.poolId
    JOIN LibraryDilution ld ON ld.dilutionId = ele.dilution_dilutionId
    JOIN Library l ON l.libraryId = ld.library_libraryId
    LEFT JOIN TargetedSequencing tr ON tr.targetedSequencingId = ld.targetedSequencingId
    LEFT JOIN (
      SELECT library_libraryId, sequence FROM Library_Index
      JOIN Indices ON Indices.indexId = Library_Index.index_indexId
      WHERE position = 1
    ) bc1 ON bc1.library_libraryId = l.libraryId
    LEFT JOIN (
      SELECT library_libraryId, sequence FROM Library_Index
      JOIN Indices ON Indices.indexId = Library_Index.index_indexId
      WHERE position = 2
    ) bc2 ON bc2.library_libraryId = l.libraryId
    JOIN SequencerPartitionContainer_Partition spcp ON spcp.partitions_partitionId = part.partitionId
    JOIN SequencerPartitionContainer spc ON spc.containerId = spcp.container_containerId
    JOIN Run_SequencerPartitionContainer rcpc ON rcpc.containers_containerId = spc.containerId WHERE rcpc.Run_runId = ?';
  SET @runId = iRunId;
  EXECUTE stmt USING @runId;
  DEALLOCATE PREPARE stmt;
END//

DROP PROCEDURE IF EXISTS queryAllSamples//
CREATE PROCEDURE queryAllSamples() BEGIN
  -- expansions: querySampleById
  PREPARE stmt FROM 'SELECT s.alias NAME
      ,s.description description
      ,s.NAME id
      ,parent.NAME parentId
      ,COALESCE(tt.alias, sc.alias) sampleType
      ,NULL sampleType_platform
      ,NULL sampleType_description
      ,tt.alias tissueType
      ,p.shortName project
      ,sai.archived archived
      ,scl.creationDate created
      ,sclcu.userId createdById
      ,scl.lastUpdated modified
      ,scluu.userId modifiedById
      ,s.identificationBarcode tubeBarcode
      ,s.volume volume
      ,sai.concentration concentration
      ,s.locationBarcode storageLocation
      ,NULL kitName
      ,NULL kitDescription
      ,NULL library_design_code
      ,s.receivedDate receive_date
      ,i.externalName external_name
      ,tor.alias tissue_origin
      ,tm.alias tissue_preparation
      ,st.region tissue_region
      ,st.secondaryIdentifier tube_id
      ,ss.strStatus str_result
      ,sai.groupId group_id
      ,sai.groupDescription group_id_description
      ,sp.alias purpose
      ,qubit.results qubit_concentration
      ,nanodrop.results nanodrop_concentration
      ,NULL barcode
      ,NULL barcode_two
      ,qpcr.results qpcr_percentage_human
      ,s.qcPassed qcPassed
      ,qpd.description detailedQcStatus
      ,box.locationBarcode boxLocation
      ,box.alias boxAlias
      ,pos.position boxPosition
      ,NULL paired
      ,NULL read_length
      ,NULL targeted_sequencing
      ,\'Sample\' miso_type
      ,sai.preMigrationId premigration_id
      ,s.scientificName organism
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
        ,results
      FROM SampleQC
      INNER JOIN QCType ON QCType.qcTypeId = SampleQC.type
      WHERE QCType.NAME = \'QuBit\'
    ) qubit ON qubit.sample_sampleId = s.sampleId
    LEFT JOIN (
      SELECT sample_sampleId
        ,results
      FROM SampleQC
      INNER JOIN QCType ON QCType.qcTypeId = SampleQC.type
      WHERE QCType.NAME = \'Nanodrop\'
    ) nanodrop ON nanodrop.sample_sampleId = s.sampleId
    LEFT JOIN (
      SELECT sample_sampleId
        ,results
      FROM SampleQC
      INNER JOIN QCType ON QCType.qcTypeId = SampleQC.type
      WHERE QCType.NAME = \'Human qPCR\'
    ) qpcr ON qpcr.sample_sampleId = s.sampleId
    LEFT JOIN BoxPosition pos ON pos.targetId = s.sampleId
      AND pos.targetType LIKE \'Sample%\'
    LEFT JOIN Box box ON box.boxId = pos.boxId

    UNION

    SELECT l.alias NAME
      ,l.description description
      ,l.NAME id
      ,parent.NAME parentId
      ,NULL sampleType
      ,lt.platformType sampleType_platform
      ,lt.description sampleType_description
      ,NULL tissueType
      ,p.shortName project
      ,lai.archived archived
      ,l.creationDate created
      ,lclcu.userId createdById
      ,lcl.lastUpdated modified
      ,lcluu.userId modifiedById
      ,l.identificationBarcode tubeBarcode
      ,l.volume volume
      ,l.concentration concentration
      ,l.locationBarcode storageLocation
      ,kd.NAME kitName
      ,kd.description kitDescription
      ,ldc.code library_design_code
      ,NULL receive_date
      ,NULL external_name
      ,NULL tissue_origin
      ,NULL tissue_preparation
      ,NULL tissue_region
      ,NULL tube_id
      ,NULL str_result
      ,NULL group_id
      ,NULL group_id_description
      ,NULL purpose
      ,qubit.results qubit_concentration
      ,NULL nanodrop_concentration
      ,bc1.sequence barcode
      ,bc2.sequence barcode_two
      ,NULL qpcr_percentage_human
      ,l.qcPassed qcPassed
      ,NULL detailedQcStatus
      ,box.locationBarcode boxLocation
      ,box.alias boxAlias
      ,pos.position boxPosition
      ,NULL paired
      ,NULL readLength
      ,NULL targeted_sequencing
      ,\'Library\' miso_type
      ,lai.preMigrationId premigration_id
      ,NULL organism
    FROM Library l

    LEFT JOIN Sample parent ON parent.sampleId = l.sample_sampleId
    LEFT JOIN Project p ON p.projectId = parent.project_projectId
    LEFT JOIN DetailedLibrary lai ON lai.libraryId = l.libraryId
    LEFT JOIN LibraryDesignCode ldc ON ldc.libraryDesignCodeId = lai.libraryDesignCodeId
    LEFT JOIN KitDescriptor kd ON kd.kitDescriptorId = l.kitDescriptorId
    LEFT JOIN LibraryType lt ON lt.libraryTypeId = l.libraryType
    LEFT JOIN (
      SELECT library_libraryId
        ,results
      FROM LibraryQC
      INNER JOIN QCType ON QCType.qcTypeId = LibraryQC.type
      WHERE QCType.NAME = \'QuBit\'
    ) qubit ON qubit.library_libraryId = l.libraryId
    LEFT JOIN (
      SELECT library_libraryId
        ,sequence
      FROM Library_Index
      INNER JOIN Indices ON Indices.indexId = Library_Index.index_indexId
      WHERE position = 1
    ) bc1 ON bc1.library_libraryId = l.libraryId
    LEFT JOIN (
      SELECT library_libraryId
        ,sequence
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
      ,NULL description
      ,d.NAME id
      ,parent.name parentId
      ,NULL sampleType
      ,lt.platformType sampleType_platform
      ,lt.description sampleType_description
      ,NULL tissueType
      ,p.shortName project
      ,0 archived
      ,CONVERT(d.creationDate, DATETIME) created
      ,NULL createdById
      ,d.lastUpdated modified
      ,NULL modifiedById
      ,d.identificationBarcode tubeBarcode
      ,NULL volume
      ,d.concentration concentration
      ,NULL storageLocation
        ,NULL kitName
      ,NULL kitDescription
      ,ldc.code library_design_code
      ,NULL receive_date
      ,NULL external_name
      ,NULL tissue_origin
      ,NULL tissue_preparation
      ,NULL tissue_region
      ,NULL tube_id
      ,NULL str_result
      ,NULL group_id
      ,NULL group_id_description
      ,NULL purpose
      ,NULL qubit_concentration
      ,NULL nanodrop_concentration
      ,NULL barcode
      ,NULL barcode_two
      ,NULL qpcr_percentage_human
      ,1 qcPassed
      ,NULL detailedQcStatus
      ,NULL boxLocation
      ,NULL boxAlias
      ,NULL boxPosition
      ,NULL paired
      ,NULL readLength
      ,NULL targeted_sequencing
      ,\'Dilution\' miso_type
      ,d.preMigrationId premigration_id
      ,NULL organism
    FROM LibraryDilution d
    JOIN Library parent ON parent.libraryId = d.library_libraryId
    JOIN LibraryType lt ON lt.libraryTypeId = parent.libraryType
    LEFT JOIN DetailedLibrary lai ON lai.libraryId = parent.libraryId
    LEFT JOIN LibraryDesignCode ldc ON lai.libraryDesignCodeId = ldc.libraryDesignCodeId
    JOIN Sample s ON s.sampleId = parent.sample_sampleId
    JOIN Project p ON p.projectId = s.project_projectId';
  EXECUTE stmt;
  DEALLOCATE PREPARE stmt;
END//

DROP PROCEDURE IF EXISTS querySampleById//
CREATE PROCEDURE querySampleById(
  iSampleId BIGINT(20)
) BEGIN
  -- base: queryAllSamples
  PREPARE stmt FROM 'SELECT * FROM (
    SELECT s.alias NAME
      ,s.description description
      ,s.NAME id
      ,parent.NAME parentId
      ,COALESCE(tt.alias, sc.alias) sampleType
      ,NULL sampleType_platform
      ,NULL sampleType_description
      ,tt.alias tissueType
      ,p.shortName project
      ,sai.archived archived
      ,scl.creationDate created
      ,sclcu.userId createdById
      ,scl.lastUpdated modified
      ,scluu.userId modifiedById
      ,s.identificationBarcode tubeBarcode
      ,s.volume volume
      ,sai.concentration concentration
      ,s.locationBarcode storageLocation
      ,NULL kitName
      ,NULL kitDescription
      ,NULL library_design_code
      ,s.receivedDate receive_date
      ,i.externalName external_name
      ,tor.alias tissue_origin
      ,tm.alias tissue_preparation
      ,st.region tissue_region
      ,st.secondaryIdentifier tube_id
      ,ss.strStatus str_result
      ,sai.groupId group_id
      ,sai.groupDescription group_id_description
      ,sp.alias purpose
      ,qubit.results qubit_concentration
      ,nanodrop.results nanodrop_concentration
      ,NULL barcode
      ,NULL barcode_two
      ,qpcr.results qpcr_percentage_human
      ,s.qcPassed qcPassed
      ,qpd.description detailedQcStatus
      ,box.locationBarcode boxLocation
      ,box.alias boxAlias
      ,pos.position boxPosition
      ,NULL paired
      ,NULL read_length
      ,NULL targeted_sequencing
      ,\'Sample\' miso_type
      ,sai.preMigrationId premigration_id
      ,s.scientificName organism
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
        ,results
      FROM SampleQC
      INNER JOIN QCType ON QCType.qcTypeId = SampleQC.type
      WHERE QCType.NAME = \'QuBit\'
    ) qubit ON qubit.sample_sampleId = s.sampleId
    LEFT JOIN (
      SELECT sample_sampleId
        ,results
      FROM SampleQC
      INNER JOIN QCType ON QCType.qcTypeId = SampleQC.type
      WHERE QCType.NAME = \'Nanodrop\'
    ) nanodrop ON nanodrop.sample_sampleId = s.sampleId
    LEFT JOIN (
      SELECT sample_sampleId
        ,results
      FROM SampleQC
      INNER JOIN QCType ON QCType.qcTypeId = SampleQC.type
      WHERE QCType.NAME = \'Human qPCR\'
    ) qpcr ON qpcr.sample_sampleId = s.sampleId
    LEFT JOIN BoxPosition pos ON pos.targetId = s.sampleId
      AND pos.targetType LIKE \'Sample%\'
    LEFT JOIN Box box ON box.boxId = pos.boxId

    UNION

    SELECT l.alias NAME
      ,l.description description
      ,l.NAME id
      ,parent.NAME parentId
      ,NULL sampleType
      ,lt.platformType sampleType_platform
      ,lt.description sampleType_description
      ,NULL tissueType
      ,p.shortName project
      ,lai.archived archived
      ,l.creationDate created
      ,lclcu.userId createdById
      ,lcl.lastUpdated modified
      ,lcluu.userId modifiedById
      ,l.identificationBarcode tubeBarcode
      ,l.volume volume
      ,l.concentration concentration
      ,l.locationBarcode storageLocation
      ,kd.NAME kitName
      ,kd.description kitDescription
      ,ldc.code library_design_code
      ,NULL receive_date
      ,NULL external_name
      ,NULL tissue_origin
      ,NULL tissue_preparation
      ,NULL tissue_region
      ,NULL tube_id
      ,NULL str_result
      ,NULL group_id
      ,NULL group_id_description
      ,NULL purpose
      ,qubit.results qubit_concentration
      ,NULL nanodrop_concentration
      ,bc1.sequence barcode
      ,bc2.sequence barcode_two
      ,NULL qpcr_percentage_human
      ,l.qcPassed qcPassed
      ,NULL detailedQcStatus
      ,box.locationBarcode boxLocation
      ,box.alias boxAlias
      ,pos.position boxPosition
      ,NULL paired
      ,NULL readLength
      ,NULL targeted_sequencing
      ,\'Library\' miso_type
      ,lai.preMigrationId premigration_id
      ,NULL organism
    FROM Library l
    LEFT JOIN Sample parent ON parent.sampleId = l.sample_sampleId
    LEFT JOIN Project p ON p.projectId = parent.project_projectId
    LEFT JOIN DetailedLibrary lai ON lai.libraryId = l.libraryId
    LEFT JOIN LibraryDesignCode ldc ON ldc.libraryDesignCodeId = lai.libraryDesignCodeId
    LEFT JOIN KitDescriptor kd ON kd.kitDescriptorId = l.kitDescriptorId
    
      LEFT JOIN LibraryType lt ON lt.libraryTypeId = l.libraryType
    LEFT JOIN (
      SELECT library_libraryId
        ,results
      FROM LibraryQC
      INNER JOIN QCType ON QCType.qcTypeId = LibraryQC.type
      WHERE QCType.NAME = \'QuBit\'
    ) qubit ON qubit.library_libraryId = l.libraryId
    LEFT JOIN (
      SELECT library_libraryId
        ,sequence
      FROM Library_Index
      INNER JOIN Indices ON Indices.indexId = Library_Index.index_indexId
      WHERE position = 1
    ) bc1 ON bc1.library_libraryId = l.libraryId
    LEFT JOIN (
      SELECT library_libraryId
        ,sequence
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
    ,NULL description
      ,d.NAME id
      ,parent.name parentId
      ,NULL sampleType
      ,lt.platformType sampleType_platform
      ,lt.description sampleType_description
      ,NULL tissueType
      ,p.shortName project
      ,0 archived
      ,CONVERT(d.creationDate, DATETIME) created
      ,NULL createdById
      ,d.lastUpdated modified
      ,NULL modifiedById
      ,d.identificationBarcode tubeBarcode
      ,NULL volume
      ,d.concentration concentration
      ,NULL storageLocation
        ,NULL kitName
      ,NULL kitDescription
      ,ldc.code library_design_code
      ,NULL receive_date
      ,NULL external_name
      ,NULL tissue_origin
      ,NULL tissue_preparation
      ,NULL tissue_region
      ,NULL tube_id
      ,NULL str_result
      ,NULL group_id
      ,NULL group_id_description
      ,NULL purpose
      ,NULL qubit_concentration
      ,NULL nanodrop_concentration
      ,NULL barcode
      ,NULL barcode_two
      ,NULL qpcr_percentage_human
      ,1 qcPassed
      ,NULL detailedQcStatus
      ,NULL boxLocation
      ,NULL boxAlias
      ,NULL boxPosition
      ,NULL paired
      ,NULL readLength
      ,NULL targeted_sequencing
      ,\'Dilution\' miso_type
      ,d.preMigrationId premigration_id
      ,NULL organism
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

-- TODO: Does This Even Work??????
DROP PROCEDURE IF EXISTS querySampleChildIdsBySampleId//
CREATE PROCEDURE querySampleChildIdsBySampleId(
  iSampleId VARCHAR(50)
) BEGIN
  PREPARE stmt FROM 'SELECT child.name id
    FROM Sample child
    JOIN DetailedSample csai ON csai.sampleId = child.sampleId
    JOIN Sample parent ON parent.sampleId = csai.parentId
    WHERE parent.name = ?

    UNION ALL

    SELECT child.name id
    FROM Library child
    JOIN Sample parent ON parent.sampleId = child.sample_sampleId
    WHERE parent.name = ?';
  SET @name = iSampleId;
  EXECUTE stmt USING @name, @name;
  DEALLOCATE PREPARE stmt;
END//

DROP PROCEDURE IF EXISTS queryAllSampleTypes//
CREATE PROCEDURE queryAllSampleTypes() BEGIN
  PREPARE stmt FROM 'SELECT sc.alias NAME
      ,\'Sample\' miso_type
      ,NULL sampleType_platform
      ,NULL sampleType_description
      ,COUNT(*) count
      ,COUNT(CASE WHEN sai.archived = true THEN sai.archived END) archivedCount
      ,MIN(scl.creationDate) earliest
      ,MAX(scl.lastUpdated) latest
    FROM DetailedSample sai
    JOIN SampleClass sc ON sc.sampleClassId = sai.sampleClassId
    JOIN (
      SELECT sampleId, MAX(changeTime) lastUpdated, MIN(changeTime) creationDate 
      FROM SampleChangeLog GROUP BY sampleId
    ) scl ON sai.sampleId = scl.sampleId
    GROUP BY sai.sampleClassId
    
    UNION
    
    SELECT NULL NAME
      ,\'Library\' miso_type
      ,lt.platformType sampleType_platform
      ,lt.description sampleType_description
      ,COUNT(*) count
      ,COUNT(CASE WHEN lai.archived = true THEN lai.archived END) archivedCount
      ,MIN(lcl.creationDate) earliest
      ,MAX(lcl.lastUpdated) latest
    FROM Library l
    JOIN DetailedLibrary lai ON lai.libraryId = l.libraryId
    JOIN LibraryType lt ON lt.libraryTypeId = l.libraryType
    JOIN (
      SELECT libraryId, MAX(changeTime) lastUpdated, MIN(changeTime) creationDate
      FROM LibraryChangeLog GROUP BY libraryId
    ) lcl ON l.libraryId = lcl.libraryId
    GROUP BY l.libraryType
    
    UNION
    
    SELECT NULL NAME
      ,\'Dilution\' miso_type
      ,lt.platformType sampleType_platform
      ,lt.description sampleType_description
      ,COUNT(*) count
      ,0 archivedCount
      ,MIN(d.creationDate) earliest
      ,MAX(d.lastUpdated) latest
    FROM LibraryDilution d
    JOIN Library l ON l.libraryId = d.library_libraryId
    JOIN LibraryType lt ON lt.libraryTypeId = l.libraryType
    GROUP BY l.libraryType';
  EXECUTE stmt;
  DEALLOCATE PREPARE stmt;
END//

DROP PROCEDURE IF EXISTS queryAllSampleProjects//
CREATE PROCEDURE queryAllSampleProjects() BEGIN
  PREPARE stmt FROM 'SELECT NAME
      ,COUNT(*) count
      ,COUNT(
        CASE WHEN archived = true THEN archived END
      ) archivedCount
      ,MIN(created) earliest
      ,MAX(updated) latest
    FROM (
      SELECT sp.shortName NAME
        ,sai.archived archived
        ,scl.creationDate created
        ,scl.lastUpdated updated
      FROM DetailedSample sai
      INNER JOIN Sample s ON s.sampleId = sai.sampleId
      INNER JOIN Project sp ON sp.projectId = s.project_projectId
      INNER JOIN (
        SELECT sampleId
          ,MAX(changeTime) as lastUpdated
          ,MIN(changeTime) as creationDate
        FROM SampleChangeLog
        GROUP BY sampleId
      ) scl ON s.sampleId = scl.sampleId

      UNION ALL

      SELECT lp.shortName NAME
        ,lai.archived archived
        ,lcl.creationDate created
        ,lcl.lastUpdated updated
      FROM DetailedLibrary lai
      INNER JOIN Library l ON l.libraryId = lai.libraryId
      INNER JOIN Sample ls ON l.sample_sampleId = ls.sampleId
      INNER JOIN Project lp ON lp.projectId = ls.project_projectId
      INNER JOIN (
        SELECT libraryId
          ,MAX(changeTime) as lastUpdated
          ,MIN(changeTime) as creationDate
        FROM LibraryChangeLog
        GROUP BY libraryId
      ) lcl ON l.libraryId = lcl.libraryId
    ) COMBINED
    GROUP BY NAME';
  EXECUTE stmt;
  DEALLOCATE PREPARE stmt;
END//

DROP PROCEDURE IF EXISTS queryAllSampleChangeLogs//
CREATE PROCEDURE queryAllSampleChangeLogs() BEGIN
  -- expansions: querySampleChangeLogById
  PREPARE stmt FROM 'SELECT s.name sampleId
      ,scl.message action
      ,scl.userId
      ,scl.changeTime
    FROM SampleChangeLog scl
    JOIN Sample s ON s.sampleId = scl.sampleId

    UNION ALL

    SELECT l.name sampleId
      ,lcl.message action
      ,lcl.userId
      ,lcl.changeTime
    FROM LibraryChangeLog lcl
    JOIN Library l ON l.libraryId = lcl.libraryId';
  EXECUTE stmt;
  DEALLOCATE PREPARE stmt;
END//

DROP PROCEDURE IF EXISTS querySampleChangeLogById//
CREATE PROCEDURE querySampleChangeLogById(
  iSampleId BIGINT(20)
) BEGIN
  -- base: queryAllSampleChangeLogs
  PREPARE stmt FROM 'SELECT * FROM (
      SELECT s.name sampleId
        ,scl.message action
        ,scl.userId
        ,scl.changeTime
      FROM SampleChangeLog scl
      JOIN Sample s ON s.sampleId = scl.sampleId

      UNION ALL

      SELECT l.name sampleId
        ,lcl.message action
        ,lcl.userId
        ,lcl.changeTime
      FROM LibraryChangeLog lcl
      JOIN Library l ON l.libraryId = lcl.libraryId
    ) COMBINED
    WHERE sampleId = ?'; 
  SET @sampleId = iSampleId;
  EXECUTE stmt USING @sampleId;
  DEALLOCATE PREPARE stmt;
END//


DELIMITER ;
--EndNoTest
